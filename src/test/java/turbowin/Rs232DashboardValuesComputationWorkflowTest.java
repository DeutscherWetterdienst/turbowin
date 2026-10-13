package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Rs232DashboardValuesComputationWorkflowTest {

  private int originalConnectionMode;
  private String originalPressureRecord;
  private String originalCorrection;
  private double originalDashboardPressure;
  private double originalDashboardPpp;
  private String originalDashboardDateTime;

  @Before
  public void setUp() {
    originalConnectionMode = main.RS232_connection_mode;
    originalPressureRecord = main.sensor_data_record_obs_pressure;
    originalCorrection = main.barometer_instrument_correction;
    originalDashboardPressure = main_RS232_RS422.dashboard_double_last_update_record_pressure_ic;
    originalDashboardPpp = main_RS232_RS422.dashboard_double_last_update_record_ppp;
    originalDashboardDateTime = main_RS232_RS422.dashboard_string_last_update_record_date_time;

    main.RS232_connection_mode = 4;
    main.sensor_data_record_obs_pressure = "";
    main.barometer_instrument_correction = "0.0";
    resetDashboardState();
  }

  @After
  public void tearDown() {
    main.RS232_connection_mode = originalConnectionMode;
    main.sensor_data_record_obs_pressure = originalPressureRecord;
    main.barometer_instrument_correction = originalCorrection;
    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic = originalDashboardPressure;
    main_RS232_RS422.dashboard_double_last_update_record_ppp = originalDashboardPpp;
    main_RS232_RS422.dashboard_string_last_update_record_date_time = originalDashboardDateTime;
  }

  @Test
  public void allSupportedConnectionModesUseTheirRecordFormat() throws Exception {
    assertValidRecord(1, fixedRecord(main.RECORD_LENGTE_PTB220, main.RECORD_ppp_BEGIN_POS_PTB220));
    assertValidRecord(2, fixedRecord(main.RECORD_LENGTE_PTB330, main.RECORD_ppp_BEGIN_POS_PTB330));
    assertValidRecord(4, duoRecord("1000.0", "1.20"));
    assertValidRecord(5, starRecord(starPayload("1000.0", "1.20")));
    assertValidRecord(6, starRecord(starPayload("1000.0", "1.20")));
    assertValidRecord(7, starXRecord("1000.0", "1.20", "600"));
    assertValidRecord(8, starXRecord("1000.0", "1.20", "600"));
  }

  @Test
  public void checksumIsRequiredForStarButNotForDuo() throws Exception {
    String payload = starPayload("1000.0", "1.20");
    String invalidChecksum = payload + "*00" + timestamp();
    if (main_RS232_RS422.Mintaka_Star_Checksum(payload + "*").equals("00")) {
      invalidChecksum = payload + "*01" + timestamp();
    }

    main.RS232_connection_mode = 4;
    startAndWaitForPressure(1000.0, invalidChecksum);
    assertEquals(1000.0, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.001);

    seedExistingValues();
    main.RS232_connection_mode = 5;
    startComputation(invalidChecksum);
    flushEdt();
    assertExistingValues();
  }

  @Test
  public void starXAgeLimitAndMalformedInputPreservePreviousValues() throws Exception {
    seedExistingValues();
    main.RS232_connection_mode = 7;
    startAndWaitForPressure(1000.0, starXRecord("1000.0", "1.20", "600"));

    seedExistingValues();
    startComputation(starXRecord("1000.0", "1.20", "601"));
    flushEdt();
    assertExistingValues();

    startComputation(starXRecord("1000.0", "1.20", "bad"));
    flushEdt();
    assertExistingValues();

    startComputation("short");
    flushEdt();
    assertExistingValues();
  }

  @Test
  public void pressureCorrectionAndPppBoundariesArePreserved() throws Exception {
    main.RS232_connection_mode = 4;
    main.barometer_instrument_correction = "1.5";
    startAndWaitForPressure(1001.5, duoRecord("1000.0", "2.34"));
    assertEquals(2.3, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.001);

    main.barometer_instrument_correction = "4.0";
    resetDashboardState();
    startAndWaitForPressure(1000.0, duoRecord("1000.0", "100.0"));
    assertEquals(100.0, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.001);

    main.barometer_instrument_correction = "-4.0";
    resetDashboardState();
    startAndWaitForPressure(1000.0, duoRecord("1000.0", "100.1"));
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);

    main.barometer_instrument_correction = "0.0";
    resetDashboardState();
    startComputation(duoRecord("900.0", "1.0"));
    flushEdt();
    assertExistingValuesAsEmpty();

    resetDashboardState();
    startAndWaitForPressure(900.1, duoRecord("900.1", "1.0"));
    resetDashboardState();
    startAndWaitForPressure(1099.9, duoRecord("1099.9", "1.0"));

    seedExistingValues();
    startComputation(duoRecord("1100.0", "1.0"));
    flushEdt();
    assertExistingValues();
  }

  @Test
  public void emptyAndMalformedRecordsLeavePreviousDashboardValuesUnchanged() throws Exception {
    seedExistingValues();
    startComputation("");
    flushEdt();
    assertExistingValues();

    startComputation("malformed-record");
    flushEdt();
    assertExistingValues();
  }

  private void assertValidRecord(int connectionMode, String record) throws Exception {
    resetDashboardState();
    main.RS232_connection_mode = connectionMode;
    startAndWaitForPressure(1000.0, record);
    assertEquals(1.2, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.001);
  }

  private void startAndWaitForPressure(double expectedPressure, String record) throws Exception {
    startComputation(record);
    waitFor(
        () ->
            Math.abs(
                    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic
                        - expectedPressure)
                < 0.001);
    flushEdt();
  }

  private void startComputation(String record) {
    Rs232DashboardValuesComputationWorkflow.start("now", record);
  }

  private String fixedRecord(int length, int pppPosition) {
    char[] record = new char[length];
    java.util.Arrays.fill(record, ' ');
    copy(record, 0, "1000.00");
    copy(record, pppPosition, "01.20");
    copy(record, length - 12, timestamp());
    return new String(record);
  }

  private String duoRecord(String pressure, String ppp) {
    return pressure + ",1000.0," + ppp + ",1*00" + timestamp();
  }

  private String starPayload(String pressure, String ppp) {
    return pressure + ",1000.0," + ppp + ",2, 52 00.0000N, 004 00.0000E,0,0,0";
  }

  private String starRecord(String payload) {
    return payload + "*" + main_RS232_RS422.Mintaka_Star_Checksum(payload + "*") + timestamp();
  }

  private String starXRecord(String pressure, String ppp, String age) {
    String payload = starPayload(pressure, ppp) + ",20.0,50,19.0,10.0," + age;
    return starRecord(payload);
  }

  private String timestamp() {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format.format(new Date());
  }

  private void seedExistingValues() {
    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic = 1012.3;
    main_RS232_RS422.dashboard_double_last_update_record_ppp = 2.4;
    main_RS232_RS422.dashboard_string_last_update_record_date_time = "previous";
  }

  private void assertExistingValues() {
    assertEquals(1012.3, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.001);
    assertEquals(2.4, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.001);
    assertEquals(
        "previous", main_RS232_RS422.dashboard_string_last_update_record_date_time);
  }

  private void assertExistingValuesAsEmpty() {
    assertEquals(
        Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
    assertEquals("", main_RS232_RS422.dashboard_string_last_update_record_date_time);
  }

  private void resetDashboardState() {
    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic = Double.MAX_VALUE;
    main_RS232_RS422.dashboard_double_last_update_record_ppp = Double.MAX_VALUE;
    main_RS232_RS422.dashboard_string_last_update_record_date_time = "";
  }

  private static void copy(char[] target, int start, String value) {
    for (int index = 0; index < value.length(); index++) {
      target[start + index] = value.charAt(index);
    }
  }

  private static void waitFor(BooleanSupplier condition) throws Exception {
    long deadline = System.nanoTime() + 5_000_000_000L;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
    assertTrue("Timed out waiting for dashboard computation", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      javax.swing.SwingUtilities.invokeAndWait(() -> {});
    }
  }
}
