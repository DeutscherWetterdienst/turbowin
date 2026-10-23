package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Rs232DashboardValuesComputationWorkflowCharacterizationTest {

  private int originalConnectionMode;
  private String originalInstrumentCorrection;
  private double originalPressure;
  private double originalPpp;
  private String originalDateTime;
  private String originalObservationPressure;

  @Before
  public void saveState() {
    originalConnectionMode = main.RS232_connection_mode;
    originalInstrumentCorrection = main.barometer_instrument_correction;
    originalPressure = main_RS232_RS422.dashboard_double_last_update_record_pressure_ic;
    originalPpp = main_RS232_RS422.dashboard_double_last_update_record_ppp;
    originalDateTime = main_RS232_RS422.dashboard_string_last_update_record_date_time;
    originalObservationPressure = main.sensor_data_record_obs_pressure;
    main.RS232_connection_mode = 1;
  }

  @After
  public void restoreState() {
    main.RS232_connection_mode = originalConnectionMode;
    main.barometer_instrument_correction = originalInstrumentCorrection;
    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic = originalPressure;
    main_RS232_RS422.dashboard_double_last_update_record_ppp = originalPpp;
    main_RS232_RS422.dashboard_string_last_update_record_date_time = originalDateTime;
    main.sensor_data_record_obs_pressure = originalObservationPressure;
  }

  @Test
  public void roundsPressureAndAppliesInstrumentCorrection() throws Exception {
    main.barometer_instrument_correction = "1.2";

    startAndWaitFor("1000.04", "2.19", "pressure-correction");

    assertEquals(1001.2, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(2.2, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
  }

  @Test
  public void ignoresOutOfRangeInstrumentCorrection() throws Exception {
    main.barometer_instrument_correction = "4.1";

    startAndWaitFor("1000.04", "0.04", "pressure-correction-range");

    assertEquals(1000.0, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(0.0, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
  }

  @Test
  public void invalidPppResetsTheDashboardValue() throws Exception {
    main.barometer_instrument_correction = "";
    main_RS232_RS422.dashboard_double_last_update_record_ppp = 7.7;

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    try {
      System.setOut(new PrintStream(output, true));
      startAndWaitFor("1000.00", "bad!!", "invalid-ppp");
      waitForOutput(output, "Function RS232_compute_dasboard_values()");
    } finally {
      System.setOut(originalOut);
    }

    assertEquals(1000.0, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
    assertTrue(output.toString().contains("Function RS232_compute_dasboard_values()"));
  }

  @Test
  public void invalidPressureLeavesPreviousDashboardValuesUnchanged() throws Exception {
    main.barometer_instrument_correction = "";
    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic = 1005.5;
    main_RS232_RS422.dashboard_double_last_update_record_ppp = 7.7;
    main_RS232_RS422.dashboard_string_last_update_record_date_time = "before-invalid-pressure";

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    try {
      System.setOut(new PrintStream(output, true));
      Rs232DashboardValuesComputationWorkflow.start(
          "invalid-pressure", ptb220Record("bad!!!!", "2.19"));
      waitForOutput(output, "Function RS232_compute_dasboard_values()");
    } finally {
      System.setOut(originalOut);
    }

    assertEquals(1005.5, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(7.7, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
    assertEquals(
        "before-invalid-pressure", main_RS232_RS422.dashboard_string_last_update_record_date_time);
    assertTrue(output.toString().contains("Function RS232_compute_dasboard_values()"));
  }

  private void waitForOutput(ByteArrayOutputStream output, String expected) throws Exception {
    long deadline = System.currentTimeMillis() + 2000;
    while (!output.toString().contains(expected) && System.currentTimeMillis() < deadline) {
      Thread.sleep(10);
    }
    assertTrue(output.toString().contains(expected));
  }

  @Test
  public void parsesPtb330PressureAndPppAtTheirFixedPositions() throws Exception {
    main.RS232_connection_mode = 2;
    main.barometer_instrument_correction = "";

    startAndWaitForRecord(ptb330Record("1002.34", "1.27"), "ptb330");

    assertEquals(1002.3, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(1.3, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
  }

  @Test
  public void parsesMintakaDuoPressureAndPppFromCommaSeparatedFields() throws Exception {
    main.RS232_connection_mode = 4;
    main.barometer_instrument_correction = "";

    startAndWaitForRecord(mintakaDuoRecord("1003.45", "2.34"), "mintaka-duo");

    assertEquals(1003.5, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(2.3, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
  }

  @Test
  public void ignoresStarXRecordWithInvalidChecksum() throws Exception {
    main.RS232_connection_mode = 7;
    main_RS232_RS422.dashboard_double_last_update_record_pressure_ic = 1005.5;
    main_RS232_RS422.dashboard_double_last_update_record_ppp = 7.7;
    main_RS232_RS422.dashboard_string_last_update_record_date_time = "before-invalid-checksum";

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    try {
      System.setOut(new PrintStream(output, true));
      Rs232DashboardValuesComputationWorkflow.start(
          "invalid-checksum", starXRecord("1004.00", "1.23"));
      waitForOutput(output, "cheksum not ok");
    } finally {
      System.setOut(originalOut);
    }

    assertEquals(1005.5, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(7.7, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
    assertEquals(
        "before-invalid-checksum", main_RS232_RS422.dashboard_string_last_update_record_date_time);
  }

  @Test
  public void parsesStarXPressureAndPppWhenChecksumAndAgeAreValid() throws Exception {
    main.RS232_connection_mode = 7;
    main.barometer_instrument_correction = "";

    startAndWaitForRecord(validStarXRecord("1004.00", "1.23", "10"), "starx");

    assertEquals(1004.0, main_RS232_RS422.dashboard_double_last_update_record_pressure_ic, 0.0);
    assertEquals(1.2, main_RS232_RS422.dashboard_double_last_update_record_ppp, 0.0);
  }

  private void startAndWaitFor(String pressure, String ppp, String dateTime) throws Exception {
    startAndWaitForRecord(ptb220Record(pressure, ppp), dateTime);
  }

  private void startAndWaitForRecord(String record, String dateTime) throws Exception {
    Rs232DashboardValuesComputationWorkflow.start(dateTime, record);
    long deadline = System.currentTimeMillis() + 2000;
    while (!dateTime.equals(main_RS232_RS422.dashboard_string_last_update_record_date_time)
        && System.currentTimeMillis() < deadline) {
      Thread.sleep(10);
    }
    assertEquals(dateTime, main_RS232_RS422.dashboard_string_last_update_record_date_time);
  }

  private String ptb220Record(String pressure, String ppp) {
    StringBuilder record = new StringBuilder("                                ");
    record.replace(0, 7, pressure);
    record.replace(main.RECORD_ppp_BEGIN_POS_PTB220, main.RECORD_ppp_BEGIN_POS_PTB220 + 5, ppp);
    return record.toString();
  }

  private String ptb330Record(String pressure, String ppp) {
    StringBuilder record = new StringBuilder("                              ");
    record.replace(main.RECORD_P_BEGIN_POS_PTB330, main.RECORD_P_BEGIN_POS_PTB330 + 7, pressure);
    record.replace(main.RECORD_ppp_BEGIN_POS_PTB330, main.RECORD_ppp_BEGIN_POS_PTB330 + 5, ppp);
    return record.toString();
  }

  private String mintakaDuoRecord(String pressure, String ppp) {
    return pressure + ",ignored," + ppp + ",unused";
  }

  private String starXRecord(String pressure, String ppp) {
    return pressure + "," + ppp + ",0,0,0,0,0,0,0,0,0,0,0,10*00";
  }

  private String validStarXRecord(String pressure, String ppp, String observationAge) {
    String recordWithoutChecksum =
        String.join(
            ",",
            pressure,
            "1004.00",
            ppp,
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            observationAge);
    String checksum = main_RS232_RS422.Mintaka_Star_Checksum(recordWithoutChecksum + "*");
    return recordWithoutChecksum + "*" + checksum + "............";
  }
}
