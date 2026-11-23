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
    originalCog = PositionModel.COG_APR;
    originalSog = PositionModel.SOG_APR;
    originalCourse = PositionModel.course;
    originalSpeed = PositionModel.speed;
    originalDsCode = PositionModel.Ds_code;
    originalVsCode = PositionModel.vs_code;

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
    PositionModel.COG_APR = originalCog;
    PositionModel.SOG_APR = originalSog;
    PositionModel.course = originalCourse;
    PositionModel.speed = originalSpeed;
    PositionModel.Ds_code = originalDsCode;
    PositionModel.vs_code = originalVsCode;
  }

  @Test
  public void missingPositionResetsStateAndLogsAprError() throws Exception {
    setStalePositionState();

    Rs232GpsNmeaCogSogWorkflow.start("APR", new double[] {Double.MAX_VALUE, 0.0, 0.0, 0.0});

    waitFor(() -> positionStateIsReset());
    assertEquals(Double.MAX_VALUE, PositionModel.COG_APR, 0.0);
    assertEquals(Double.MAX_VALUE, PositionModel.SOG_APR, 0.0);
    assertEquals("", PositionModel.course);
    assertEquals("", PositionModel.speed);
    assertEquals("", PositionModel.Ds_code);
    assertEquals("", PositionModel.vs_code);
    assertLogContains(
        "[GPS] COG and SOG calculation [10 min] error or start/end position not (yet) available");
  }

  @Test
  public void validMovementPublishesRoundedCourseSpeedAndCodes() throws Exception {
    Rs232GpsNmeaCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});

    waitFor(() -> "90.0".equals(PositionModel.course) && "6.0".equals(PositionModel.speed));

    assertEquals(90.0, PositionModel.COG_APR, 0.001);
    assertEquals(6.0, PositionModel.SOG_APR, 0.001);
    assertEquals("2", PositionModel.Ds_code);
    assertEquals("2", PositionModel.vs_code);
  }

  @Test
  public void format101AndFm13UseTheirConfiguredSpeedIntervals() throws Exception {
    double[] oneNauticalMileEast = new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)};

    Rs232GpsNmeaCogSogWorkflow.start("UPDATE_MINUTE", oneNauticalMileEast);
    waitFor(() -> "6.0".equals(PositionModel.speed));
    assertEquals(main.FORMAT_101, main.obs_format);
    assertEquals("6.0", PositionModel.speed);

    main.obs_format = main.FORMAT_FM13;
    resetPositionState();
    Rs232GpsNmeaCogSogWorkflow.start("UPDATE_MINUTE", oneNauticalMileEast);
    waitFor(() -> "0.333".equals(PositionModel.speed));

    assertEquals(0.333, PositionModel.SOG_APR, 0.0005);
    assertEquals("90.0", PositionModel.course);
  }

  @Test
  public void zeroAndMaximumValidSpeedAreAcceptedButValuesAboveMaximumResetState()
      throws Exception {
    Rs232GpsNmeaCogSogWorkflow.start("UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, 0.0});
    waitFor(() -> "0.0".equals(PositionModel.speed));
    assertEquals("0.0", PositionModel.course);
    assertEquals("0", PositionModel.Ds_code);
    assertEquals("0", PositionModel.vs_code);

    resetPositionState();
    Rs232GpsNmeaCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(100.0)});
    waitFor(() -> "100.0".equals(PositionModel.speed));
    assertEquals(100.0, PositionModel.SOG_APR, 0.001);
    assertEquals("100.0", PositionModel.speed);

    setStalePositionState();
    Rs232GpsNmeaCogSogWorkflow.start(
        "UPDATE_MINUTE", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(101.0)});
    waitFor(() -> positionStateIsReset());
    assertEquals(Double.MAX_VALUE, PositionModel.COG_APR, 0.0);
    assertEquals(Double.MAX_VALUE, PositionModel.SOG_APR, 0.0);
    assertEquals("", PositionModel.course);
    assertEquals("", PositionModel.speed);
    assertEquals("", PositionModel.Ds_code);
    assertEquals("", PositionModel.vs_code);
  }

  @Test
  public void manualAndAprLoggingUseFormatSpecificMessages() throws Exception {
    Rs232GpsNmeaCogSogWorkflow.start(
        "APR", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});
    waitFor(() -> "6.0".equals(PositionModel.speed));
    assertLogContains("[GPS] COG and SOG calculation [10 min] ok; 90.0° 6.0 kts");

    main.obs_format = main.FORMAT_FM13;
    resetPositionState();
    Rs232GpsNmeaCogSogWorkflow.start(
        "MANUAL", new double[] {0.0, 0.0, 0.0, longitudeForTenMinuteKnots(6.0)});
    waitFor(() -> "0.333".equals(PositionModel.speed));
    assertLogContains("[GPS] COG and SOG calculation [3 hrs] ok; 90.0° 0.333 kts");
  }

  private void resetPositionState() {
    PositionModel.COG_APR = Double.MAX_VALUE;
    PositionModel.SOG_APR = Double.MAX_VALUE;
    PositionModel.course = "";
    PositionModel.speed = "";
    PositionModel.Ds_code = "";
    PositionModel.vs_code = "";
  }

  private void setStalePositionState() {
    PositionModel.COG_APR = 12.0;
    PositionModel.SOG_APR = 3.0;
    PositionModel.course = "stale course";
    PositionModel.speed = "stale speed";
    PositionModel.Ds_code = "stale direction";
    PositionModel.vs_code = "stale speed code";
  }

  private boolean positionStateIsReset() {
    return PositionModel.COG_APR == Double.MAX_VALUE
        && PositionModel.SOG_APR == Double.MAX_VALUE
        && "".equals(PositionModel.course)
        && "".equals(PositionModel.speed)
        && "".equals(PositionModel.Ds_code)
        && "".equals(PositionModel.vs_code);
  }

  private void assertLogContains(String expected) throws Exception {
    Path logFile =
        new File(
                main.logs_dir,
                main.TURBOWIN_SYSTEM_LOGS_DIR
                    + File.separator
                    + SystemLogWriterWorkflow.logFileName(main.sdf_tsl_1.format(new Date())))
            .toPath();
    waitFor(() -> {
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
