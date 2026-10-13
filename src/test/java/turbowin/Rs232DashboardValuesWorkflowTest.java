package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232DashboardValuesWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private SimpleDateFormat originalSdfTsl2;
  private GregorianCalendar originalObservationDate;
  private int originalConnectionMode;
  private String originalCorrection;
  private double originalDashboardPressure;
  private double originalDashboardPpp;
  private String originalDashboardDateTime;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalSdfTsl2 = main.sdf_tsl_2;
    originalObservationDate = main.obs_file_datum_tijd;
    originalConnectionMode = main.RS232_connection_mode;
    originalCorrection = main.barometer_instrument_correction;
    originalDashboardPressure = main_RS232_RS422.dashboard_double_last_update_record_pressure_ic;
    originalDashboardPpp = main_RS232_RS422.dashboard_double_last_update_record_ppp;
    originalDashboardDateTime = main_RS232_RS422.dashboard_string_last_update_record_date_time;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main.sdf_tsl_2 = utcFormat("yyyy-MM-dd HH:mm");
    main.RS232_connection_mode = 4;
    main.barometer_instrument_correction = "0.0";
    resetDashboardState();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.sdf_tsl_2 = originalSdfTsl2;
    main.obs_file_datum_tijd = originalObservationDate;
    main.RS232_connection_mode = originalConnectionMode;
    main.barometer_instrument_correction = originalCorrection;
    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic = originalDashboardPressure;
    main_RS232_RS422.dashboard_double_last_update_record_ppp = originalDashboardPpp;
    main_RS232_RS422.dashboard_string_last_update_record_date_time = originalDashboardDateTime;
  }

  @Test
  public void missingAndEmptyFilesLeaveExistingDashboardValuesUnchanged() throws Exception {
    setDashboardState(1012.3, 2.4, "previous");
    runWorkflowWithoutFile();
    assertDashboardState(1012.3, 2.4, "previous");

    resetDashboardState();
    setDashboardState(1012.3, 2.4, "previous");
    writeRawRecord("");
    runWorkflow();
    assertDashboardState(1012.3, 2.4, "previous");
  }

  @Test
  public void lastLineIsSelectedAndFreshnessCutoffIsApplied() throws Exception {
    Date current = new Date();
    writeRawRecord(
        duoRecord("999.0", "1.0", new Date(current.getTime() - 60_000L))
            + System.lineSeparator()
            + duoRecord("1001.2", "2.34", current));
    runWorkflow();

    waitForDashboardPressure(1001.2);
    assertEquals(1001.2, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.001);
    assertEquals(2.3, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.001);

    resetDashboardState();
    writeRawRecord(
        duoRecord(
            "1001.2", "2.34", new Date(System.currentTimeMillis() - 10 * 60 * 1000L)));
    runWorkflow();
    waitForDashboardPressure(1001.2);

    resetDashboardState();
    writeRawRecord(
        duoRecord(
            "1001.2", "2.34", new Date(System.currentTimeMillis() - 11 * 60 * 1000L)));
    runWorkflow();
    assertDashboardState(Double.MAX_VALUE, Double.MAX_VALUE, "");
  }

  @Test
  public void malformedShortRecordDoesNotReplaceDashboardValues() throws Exception {
    setDashboardState(1012.3, 2.4, "previous");
    writeRawRecord("bad," + main.sdf4.format(new Date()));
    runWorkflow();

    assertDashboardState(1012.3, 2.4, "previous");
  }

  @Test
  public void fileReadIOExceptionLeavesDashboardValuesUnchanged() throws Exception {
    setDashboardState(1012.3, 2.4, "previous");
    File file = sensorDataFile();
    assertTrue(file.mkdir());
    assertTrue(new File(file, "not-a-record").createNewFile());

    runWorkflow();

    assertDashboardState(1012.3, 2.4, "previous");
  }

  private void runWorkflowWithoutFile() throws Exception {
    main.obs_file_datum_tijd = new GregorianCalendar();
    Rs232DashboardValuesWorkflow.start();
    waitFor(() -> main.obs_file_datum_tijd == null);
    flushEdt();
  }

  private void runWorkflow() throws Exception {
    runWorkflowWithoutFile();
  }

  private void writeRawRecord(String record) throws Exception {
    Files.writeString(sensorDataFile().toPath(), record);
  }

  private File sensorDataFile() {
    Date fileDate = new Date(System.currentTimeMillis() - 2 * 60 * 1000L);
    return new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate) + ".txt");
  }

  private String duoRecord(String pressure, String ppp, Date recordDate) {
    return pressure + ",1000.0," + ppp + ",1*" + main.sdf4.format(recordDate);
  }

  private void resetDashboardState() {
    setDashboardState(Double.MAX_VALUE, Double.MAX_VALUE, "");
  }

  private void setDashboardState(double pressure, double ppp, String dateTime) {
    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic = pressure;
    main_RS232_RS422.dashboard_double_last_update_record_ppp = ppp;
    main_RS232_RS422.dashboard_string_last_update_record_date_time = dateTime;
  }

  private void assertDashboardState(double pressure, double ppp, String dateTime) {
    assertEquals(pressure, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.001);
    assertEquals(ppp, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.001);
    assertEquals(dateTime, main_RS232_RS422.dashboard_string_last_update_record_date_time);
  }

  private static SimpleDateFormat utcFormat(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format;
  }

  private static void waitFor(BooleanSupplier condition) throws Exception {
    long deadline = System.nanoTime() + 5_000_000_000L;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
    assertTrue("Timed out waiting for dashboard values workflow", condition.getAsBoolean());
  }

  private static void waitForDashboardPressure(double expectedPressure) throws Exception {
    waitFor(
        () ->
            Math.abs(
                    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic
                        - expectedPressure)
                < 0.001);
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      SwingUtilities.invokeAndWait(() -> {});
    }
  }
}
