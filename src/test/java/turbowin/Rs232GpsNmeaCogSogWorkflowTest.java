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

public class Rs232GpsNmeaCogSogWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDirectory;
  private String originalObservationFormat;
  private SimpleDateFormat originalSystemLogMonthFormat;
  private SimpleDateFormat originalSystemLogMessageFormat;
  private double originalCog;
  private double originalSog;
  private String originalCourse;
  private String originalSpeed;
  private String originalDsCode;
  private String originalVsCode;

  @Before
  public void setUp() throws Exception {
    originalLogsDirectory = main.logs_dir;
    originalObservationFormat = main.obs_format;
    originalSystemLogMonthFormat = main.sdf_tsl_1;
    originalSystemLogMessageFormat = main.sdf_tsl_2;
    originalCog = myposition.COG_APR;
    originalSog = myposition.SOG_APR;
    originalCourse = myposition.course;
    originalSpeed = myposition.speed;
    originalDsCode = myposition.Ds_code;
    originalVsCode = myposition.vs_code;

    File logsDirectory = temporaryFolder.newFolder("logs");
    new File(logsDirectory, main.TURBOWIN_SYSTEM_LOGS_DIR).mkdirs();
    main.logs_dir = logsDirectory.getPath();
    main.obs_format = main.FORMAT_101;
    main.sdf_tsl_1 = utcFormat("MMM_yyyy");
    main.sdf_tsl_2 = utcFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
    resetPositionState();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDirectory;
    main.obs_format = originalObservationFormat;
    main.sdf_tsl_1 = originalSystemLogMonthFormat;
    main.sdf_tsl_2 = originalSystemLogMessageFormat;
    myposition.COG_APR = originalCog;
    myposition.SOG_APR = originalSog;
    myposition.course = originalCourse;
    myposition.speed = originalSpeed;
    myposition.Ds_code = originalDsCode;
    myposition.vs_code = originalVsCode;
  }

  @Test
  public void missingPositionResetsStateAndLogsAprError() throws Exception {
    setStalePositionState();

    Rs232GpsNmeaCogSogWorkflow.start("APR", new double[] {Double.MAX_VALUE, 0.0, 0.0, 0.0});

    waitFor(() -> positionStateIsReset());
    assertEquals(Double.MAX_VALUE, myposition.COG_APR, 0.0);
    assertEquals(Double.MAX_VALUE, myposition.SOG_APR, 0.0);
    assertEquals("", myposition.course);
    assertEquals("", myposition.speed);
    assertEquals("", myposition.Ds_code);
    assertEquals("", myposition.vs_code);
    assertLogContains(
        "[GPS] COG and SOG calculation [10 min] error or start/end position not (yet) available");
  }

  @Test
  public void validMovementPublishesRoundedCourseSpeedAndCodes() throws Exception {
    Rs232GpsNmeaCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});

    waitFor(() -> "90.0".equals(myposition.course) && "6.0".equals(myposition.speed));

    assertEquals(90.0, myposition.COG_APR, 0.001);
    assertEquals(6.0, myposition.SOG_APR, 0.001);
    assertEquals("2", myposition.Ds_code);
    assertEquals("2", myposition.vs_code);
  }

  @Test
  public void format101AndFm13UseTheirConfiguredSpeedIntervals() throws Exception {
    double[] oneNauticalMileEast =
        new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)};

    Rs232GpsNmeaCogSogWorkflow.start("UPDATE_MINUTE", oneNauticalMileEast);
    waitFor(() -> "6.0".equals(myposition.speed));
    assertEquals(main.FORMAT_101, main.obs_format);
    assertEquals("6.0", myposition.speed);

    main.obs_format = main.FORMAT_FM13;
    resetPositionState();
    Rs232GpsNmeaCogSogWorkflow.start("UPDATE_MINUTE", oneNauticalMileEast);
    waitFor(() -> "0.333".equals(myposition.speed));

    assertEquals(0.333, myposition.SOG_APR, 0.0005);
    assertEquals("90.0", myposition.course);
  }

  @Test
  public void zeroAndMaximumValidSpeedAreAcceptedButValuesAboveMaximumResetState()
      throws Exception {
    Rs232GpsNmeaCogSogWorkflow.start("UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, 0.0});
    waitFor(() -> "0.0".equals(myposition.speed));
    assertEquals("0.0", myposition.course);
    assertEquals("0", myposition.Ds_code);
    assertEquals("0", myposition.vs_code);

    resetPositionState();
    Rs232GpsNmeaCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(100.0)});
    waitFor(() -> "100.0".equals(myposition.speed));
    assertEquals(100.0, myposition.SOG_APR, 0.001);
    assertEquals("100.0", myposition.speed);

    setStalePositionState();
    Rs232GpsNmeaCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(101.0)});
    waitFor(() -> positionStateIsReset());
    assertEquals(Double.MAX_VALUE, myposition.COG_APR, 0.0);
    assertEquals(Double.MAX_VALUE, myposition.SOG_APR, 0.0);
    assertEquals("", myposition.course);
    assertEquals("", myposition.speed);
    assertEquals("", myposition.Ds_code);
    assertEquals("", myposition.vs_code);
  }

  @Test
  public void manualAndAprLoggingUseFormatSpecificMessages() throws Exception {
    Rs232GpsNmeaCogSogWorkflow.start(
        "APR", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});
    waitFor(() -> "6.0".equals(myposition.speed));
    assertLogContains("[GPS] COG and SOG calculation [10 min] ok; 90.0° 6.0 kts");

    main.obs_format = main.FORMAT_FM13;
    resetPositionState();
    Rs232GpsNmeaCogSogWorkflow.start(
        "MANUAL", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});
    waitFor(() -> "0.333".equals(myposition.speed));
    assertLogContains("[GPS] COG and SOG calculation [3 hrs] ok; 90.0° 0.333 kts");
  }

  private void resetPositionState() {
    myposition.COG_APR = Double.MAX_VALUE;
    myposition.SOG_APR = Double.MAX_VALUE;
    myposition.course = "";
    myposition.speed = "";
    myposition.Ds_code = "";
    myposition.vs_code = "";
  }

  private void setStalePositionState() {
    myposition.COG_APR = 12.0;
    myposition.SOG_APR = 3.0;
    myposition.course = "stale course";
    myposition.speed = "stale speed";
    myposition.Ds_code = "stale direction";
    myposition.vs_code = "stale speed code";
  }

  private boolean positionStateIsReset() {
    return myposition.COG_APR == Double.MAX_VALUE
        && myposition.SOG_APR == Double.MAX_VALUE
        && "".equals(myposition.course)
        && "".equals(myposition.speed)
        && "".equals(myposition.Ds_code)
        && "".equals(myposition.vs_code);
  }

  private void assertLogContains(String expected) throws Exception {
    Path logFile =
        new File(
                main.logs_dir,
                main.TURBOWIN_SYSTEM_LOGS_DIR
                    + File.separator
                    + SystemLogWriterWorkflow.logFileName(main.sdf_tsl_1.format(new Date())))
            .toPath();
    waitFor(
        () -> {
          try {
            return Files.exists(logFile)
                && Files.readString(logFile, StandardCharsets.UTF_8).contains(expected);
          } catch (Exception ex) {
            return false;
          }
        });
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
    assertTrue("Timed out waiting for GPS COG/SOG workflow", condition.getAsBoolean());
  }
}
