package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232GpsNmeaWindCogSogWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDirectory;
  private SimpleDateFormat originalSystemLogMonthFormat;
  private SimpleDateFormat originalSystemLogMessageFormat;
  private double originalCog;
  private double originalSog;

  @Before
  public void setUp() throws Exception {
    originalLogsDirectory = main.logs_dir;
    originalSystemLogMonthFormat = main.sdf_tsl_1;
    originalSystemLogMessageFormat = main.sdf_tsl_2;
    originalCog = myposition.COG_APR_wind;
    originalSog = myposition.SOG_APR_wind;

    File logsDirectory = temporaryFolder.newFolder("logs");
    new File(logsDirectory, main.TURBOWIN_SYSTEM_LOGS_DIR).mkdirs();
    main.logs_dir = logsDirectory.getPath();
    main.sdf_tsl_1 = utcFormat("MMM_yyyy");
    main.sdf_tsl_2 = utcFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
    resetWindState();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDirectory;
    main.sdf_tsl_1 = originalSystemLogMonthFormat;
    main.sdf_tsl_2 = originalSystemLogMessageFormat;
    myposition.COG_APR_wind = originalCog;
    myposition.SOG_APR_wind = originalSog;
  }

  @Test
  public void missingPositionResetsWindStateAndLogsAprError() throws Exception {
    setStaleWindState();

    Rs232GpsNmeaWindCogSogWorkflow.start(
        "APR", new double[] {Double.MAX_VALUE, 0.0, 0.0, 0.0});

    waitFor(this::windStateIsReset);
    assertEquals(Double.MAX_VALUE, myposition.COG_APR_wind, 0.0);
    assertEquals(Double.MAX_VALUE, myposition.SOG_APR_wind, 0.0);
    assertLogContains(
        "[GPS] For APTR dashboard wind [10 min]: COG and SOG calculation error or start/end position not (yet) available");
  }

  @Test
  public void validMovementPublishesRoundedWindCourseAndSpeed() throws Exception {
    Rs232GpsNmeaWindCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});

    waitFor(() -> myposition.COG_APR_wind != Double.MAX_VALUE);

    assertEquals(90.0, myposition.COG_APR_wind, 0.001);
    assertEquals(6.0, myposition.SOG_APR_wind, 0.001);
  }

  @Test
  public void lowerAndUpperValidBoundsAreAcceptedButValuesAboveMaximumResetState()
      throws Exception {
    Rs232GpsNmeaWindCogSogWorkflow.start("UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, 0.0});
    waitFor(() -> myposition.SOG_APR_wind == 0.0);
    assertEquals(0.0, myposition.COG_APR_wind, 0.0);

    resetWindState();
    Rs232GpsNmeaWindCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(100.0)});
    waitFor(() -> myposition.SOG_APR_wind == 100.0);
    assertEquals(90.0, myposition.COG_APR_wind, 0.001);

    setStaleWindState();
    Rs232GpsNmeaWindCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(101.0)});
    waitFor(this::windStateIsReset);
    assertEquals(Double.MAX_VALUE, myposition.COG_APR_wind, 0.0);
    assertEquals(Double.MAX_VALUE, myposition.SOG_APR_wind, 0.0);
  }

  @Test
  public void aprAndManualLogSuccessfulCalculationButMinuteUpdatesDoNotLog() throws Exception {
    Rs232GpsNmeaWindCogSogWorkflow.start(
        "APR", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});
    waitFor(() -> myposition.SOG_APR_wind == 6.0);
    String successfulMessage =
        "[GPS] For APTR dashboard wind [10 min].: COG and SOG calculation ok; 90.0° 6.0 kts";
    assertLogContains(successfulMessage);

    resetWindState();
    Rs232GpsNmeaWindCogSogWorkflow.start(
        "MANUAL", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});
    waitFor(() -> myposition.SOG_APR_wind == 6.0);
    waitFor(() -> logContainsOccurrences(successfulMessage, 2));
    String logBeforeMinuteUpdate = Files.readString(currentLogFile(), StandardCharsets.UTF_8);

    resetWindState();
    Rs232GpsNmeaWindCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});
    waitFor(() -> myposition.SOG_APR_wind == 6.0);
    Thread.sleep(100);
    assertEquals(
        logBeforeMinuteUpdate, Files.readString(currentLogFile(), StandardCharsets.UTF_8));
  }

  private void resetWindState() {
    myposition.COG_APR_wind = Double.MAX_VALUE;
    myposition.SOG_APR_wind = Double.MAX_VALUE;
  }

  private void setStaleWindState() {
    myposition.COG_APR_wind = 12.0;
    myposition.SOG_APR_wind = 3.0;
  }

  private boolean windStateIsReset() {
    return myposition.COG_APR_wind == Double.MAX_VALUE
        && myposition.SOG_APR_wind == Double.MAX_VALUE;
  }

  private void assertLogContains(String expected) throws Exception {
    waitFor(
        () -> {
          try {
            return Files.exists(currentLogFile())
                && Files.readString(currentLogFile(), StandardCharsets.UTF_8).contains(expected);
          } catch (Exception ex) {
            return false;
          }
        });
  }

  private boolean logContainsOccurrences(String expected, int minimumOccurrences) {
    try {
      String contents = Files.readString(currentLogFile(), StandardCharsets.UTF_8);
      int occurrences = 0;
      int start = 0;
      while ((start = contents.indexOf(expected, start)) >= 0) {
        occurrences++;
        start += expected.length();
      }
      return occurrences >= minimumOccurrences;
    } catch (Exception ex) {
      return false;
    }
  }

  private Path currentLogFile() {
    return new File(
            main.logs_dir,
            main.TURBOWIN_SYSTEM_LOGS_DIR
                + File.separator
                + SystemLogWriterWorkflow.logFileName(main.sdf_tsl_1.format(new Date())))
        .toPath();
  }

  private static double longitudeForTenMinuteKnots(double knots) {
    return Math.toDegrees(knots / 6.0 * 1852.0 / 6371e3);
  }

  private static SimpleDateFormat utcFormat(String pattern) {
    return utcFormat(pattern, Locale.ROOT);
  }

  private static SimpleDateFormat utcFormat(String pattern, Locale locale) {
    SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format;
  }

  private static void waitFor(BooleanSupplier condition) throws Exception {
    long deadline = System.nanoTime() + 5_000_000_000L;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
    assertTrue("Timed out waiting for GPS wind COG/SOG workflow", condition.getAsBoolean());
  }
}
