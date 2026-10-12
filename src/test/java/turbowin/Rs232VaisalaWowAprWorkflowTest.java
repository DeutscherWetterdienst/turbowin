package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232VaisalaWowAprWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private int originalRecordLength;
  private int originalDatePosition;
  private int originalPressurePosition;
  private int originalAPosition;
  private int originalPppPosition;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private GregorianCalendar originalObservationDate;
  private int originalConnectionMode;
  private int originalSecondConnectionMode;
  private String originalPressureRecord;
  private String originalPppRecord;
  private String originalARecord;
  private String originalPressureCorrection;
  private String originalBarometerHeight;
  private String originalKeelHeight;
  private String originalDraught;
  private boolean originalApr;
  private String originalSendMethod;
  private String originalPressureCorrected;
  private String originalPressureMslCorrected;
  private String originalPppAmount;
  private String originalPppCode;
  private String originalACode;
  private String originalPpppCode;
  private int originalLatitudeDegrees;
  private int originalLatitudeMinutes;
  private int originalLongitudeDegrees;
  private int originalLongitudeMinutes;
  private String originalLatitudeHemisphere;
  private String originalLongitudeHemisphere;
  private JTextField originalPressureField;
  private JTextField originalMslField;
  private JTextField originalTendencyField;
  private JTextField originalCharacteristicField;
  private JTextField originalAirTempField;
  private JTextField originalWetBulbField;
  private JTextField originalDewPointField;
  private JTextField originalSeaWaterField;
  private String originalAirTemp;
  private String originalWetBulb;
  private String originalRh;
  private String originalSeaWater;
  private double originalDewPoint;
  private double originalRelativeHumidity;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalRecordLength = main.type_record_lengte;
    originalDatePosition = main.type_record_datum_tijd_begin_pos;
    originalPressurePosition = main.type_record_pressure_begin_pos;
    originalAPosition = main.type_record_a_begin_pos;
    originalPppPosition = main.type_record_ppp_begin_pos;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObservationDate = main.obs_file_datum_tijd;
    originalConnectionMode = main.RS232_connection_mode;
    originalSecondConnectionMode = main.RS232_connection_mode_II;
    originalPressureRecord = main.sensor_data_record_obs_pressure;
    originalPppRecord = main.sensor_data_record_obs_ppp;
    originalARecord = main.sensor_data_record_obs_a;
    originalPressureCorrection = main.barometer_instrument_correction;
    originalBarometerHeight = main.barometer_above_sll;
    originalKeelHeight = main.keel_sll;
    originalDraught = main.WOW_APR_average_draught;
    originalApr = main.APR;
    originalSendMethod = main.APTR_AWSR_send_method;
    originalPressureCorrected = mybarometer.pressure_reading_corrected;
    originalPressureMslCorrected = mybarometer.pressure_msl_corrected;
    originalPppAmount = mybarograph.pressure_amount_tendency;
    originalPppCode = mybarograph.ppp_code;
    originalACode = mybarograph.a_code;
    originalPpppCode = mybarometer.PPPP_code;
    originalLatitudeDegrees = myposition.int_latitude_degrees;
    originalLatitudeMinutes = myposition.int_latitude_minutes;
    originalLongitudeDegrees = myposition.int_longitude_degrees;
    originalLongitudeMinutes = myposition.int_longitude_minutes;
    originalLatitudeHemisphere = myposition.latitude_hemisphere;
    originalLongitudeHemisphere = myposition.longitude_hemisphere;
    originalPressureField = main.jTextField9;
    originalMslField = main.jTextField10;
    originalTendencyField = main.jTextField11;
    originalCharacteristicField = main.jTextField12;
    originalAirTempField = main.jTextField36;
    originalWetBulbField = main.jTextField37;
    originalDewPointField = main.jTextField38;
    originalSeaWaterField = main.jTextField40;
    originalAirTemp = mytemp.air_temp;
    originalWetBulb = mytemp.wet_bulb_temp;
    originalRh = mytemp.RH;
    originalSeaWater = mytemp.sea_water_temp;
    originalDewPoint = mytemp.double_dew_point;
    originalRelativeHumidity = mytemp.double_rv;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.type_record_lengte = main.RECORD_LENGTE_PTB220;
    main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
    main.type_record_pressure_begin_pos = main.RECORD_P_BEGIN_POS_PTB220;
    main.type_record_a_begin_pos = main.RECORD_a_BEGIN_POS_PTB220;
    main.type_record_ppp_begin_pos = main.RECORD_ppp_BEGIN_POS_PTB220;
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main.RS232_connection_mode = 1;
    main.RS232_connection_mode_II = 0;
    main.sensor_data_record_obs_pressure = "";
    main.sensor_data_record_obs_ppp = "";
    main.sensor_data_record_obs_a = "";
    main.barometer_instrument_correction = "1.5";
    main.barometer_above_sll = "0.0";
    main.keel_sll = "0.0";
    main.WOW_APR_average_draught = "0.0";
    main.APR = true;
    main.APTR_AWSR_send_method = "";
    myposition.int_latitude_degrees = 0;
    myposition.int_latitude_minutes = 0;
    myposition.int_longitude_degrees = 0;
    myposition.int_longitude_minutes = 0;
    myposition.latitude_hemisphere = "";
    myposition.longitude_hemisphere = "";
    resetModel();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.type_record_lengte = originalRecordLength;
    main.type_record_datum_tijd_begin_pos = originalDatePosition;
    main.type_record_pressure_begin_pos = originalPressurePosition;
    main.type_record_a_begin_pos = originalAPosition;
    main.type_record_ppp_begin_pos = originalPppPosition;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.obs_file_datum_tijd = originalObservationDate;
    main.RS232_connection_mode = originalConnectionMode;
    main.RS232_connection_mode_II = originalSecondConnectionMode;
    main.sensor_data_record_obs_pressure = originalPressureRecord;
    main.sensor_data_record_obs_ppp = originalPppRecord;
    main.sensor_data_record_obs_a = originalARecord;
    main.barometer_instrument_correction = originalPressureCorrection;
    main.barometer_above_sll = originalBarometerHeight;
    main.keel_sll = originalKeelHeight;
    main.WOW_APR_average_draught = originalDraught;
    main.APR = originalApr;
    main.APTR_AWSR_send_method = originalSendMethod;
    mybarometer.pressure_reading_corrected = originalPressureCorrected;
    mybarometer.pressure_msl_corrected = originalPressureMslCorrected;
    mybarograph.pressure_amount_tendency = originalPppAmount;
    mybarograph.ppp_code = originalPppCode;
    mybarograph.a_code = originalACode;
    mybarometer.PPPP_code = originalPpppCode;
    myposition.int_latitude_degrees = originalLatitudeDegrees;
    myposition.int_latitude_minutes = originalLatitudeMinutes;
    myposition.int_longitude_degrees = originalLongitudeDegrees;
    myposition.int_longitude_minutes = originalLongitudeMinutes;
    myposition.latitude_hemisphere = originalLatitudeHemisphere;
    myposition.longitude_hemisphere = originalLongitudeHemisphere;
    main.jTextField9 = originalPressureField;
    main.jTextField10 = originalMslField;
    main.jTextField11 = originalTendencyField;
    main.jTextField12 = originalCharacteristicField;
    main.jTextField36 = originalAirTempField;
    main.jTextField37 = originalWetBulbField;
    main.jTextField38 = originalDewPointField;
    main.jTextField40 = originalSeaWaterField;
    mytemp.air_temp = originalAirTemp;
    mytemp.wet_bulb_temp = originalWetBulb;
    mytemp.RH = originalRh;
    mytemp.sea_water_temp = originalSeaWater;
    mytemp.double_dew_point = originalDewPoint;
    mytemp.double_rv = originalRelativeHumidity;
  }

  @Test
  public void freshRecordRoundsPressureAndPreparesPressureCodes() throws Exception {
    runPressureRecord("1001.26", "13.19", "2", "APR", new Date());

    assertEquals("1002.8", mybarometer.pressure_reading_corrected);
    assertEquals("1002.76", mybarometer.pressure_msl_corrected);
    assertEquals("0028", mybarometer.PPPP_code);
    assertEquals("13.2", mybarograph.pressure_amount_tendency);
    assertEquals("132", mybarograph.ppp_code);
    assertEquals("2", mybarograph.a_code);
  }

  @Test
  public void pressureLimitsAndInstrumentCorrectionAreCharacterized() throws Exception {
    runPressureRecord("900.0", "1.0", "2", "APR", new Date());
    assertEquals("", mybarometer.pressure_reading_corrected);

    runPressureRecord("900.1", "1.0", "2", "APR", new Date());
    assertEquals("901.6", mybarometer.pressure_reading_corrected);

    runPressureRecord("1099.9", "1.0", "2", "APR", new Date());
    assertEquals("1101.4", mybarometer.pressure_reading_corrected);

    runPressureRecord("1100.0", "1.0", "2", "APR", new Date());
    assertEquals("", mybarometer.pressure_reading_corrected);

    main.barometer_instrument_correction = "4.1";
    runPressureRecord("1000.0", "1.0", "2", "APR", new Date());
    assertEquals("1000.0", mybarometer.pressure_reading_corrected);
  }

  @Test
  public void missingMalformedAndStaleRecordsClearAprResults() throws Exception {
    mybarometer.pressure_reading_corrected = "old";
    mybarometer.pressure_msl_corrected = "old";
    mybarograph.pressure_amount_tendency = "old";
    writeRawRecord("malformed");
    runWorkflow("APR");
    assertCleared();

    resetModel();
    writePressureRecord(
        "1000.0", "1.0", "2", new Date(System.currentTimeMillis() - 20 * 60 * 1000L));
    runWorkflow("APR");
    assertCleared();

    resetModel();
    runWorkflow("APR");
    assertCleared();
  }

  @Test
  public void mainScreenUpdatesPressureAndTendencyFieldsAndReadsSecondInstrument() throws Exception {
    main.RS232_connection_mode_II = 1;
    main.jTextField9 = new JTextField();
    main.jTextField10 = new JTextField();
    main.jTextField11 = new JTextField();
    main.jTextField12 = new JTextField();
    main.jTextField36 = new JTextField();
    main.jTextField37 = new JTextField();
    main.jTextField38 = new JTextField();
    main.jTextField40 = new JTextField();
    mytemp.sea_water_temp = "";
    writeHmpRecord("20.0", "-0.1", "10.0", "50.0", new Date());

    runPressureRecord("1000.0", "1.0", "3", "MAIN_SCREEN", new Date());

    assertEquals("1001.5 hPa", main.jTextField9.getText());
    assertEquals("1001.5 hPa", main.jTextField10.getText());
    assertEquals("1.0 hPa", main.jTextField11.getText());
    assertEquals("3 (code)", main.jTextField12.getText());
    assertEquals("20.0 \u00b0C", main.jTextField36.getText());
    assertEquals("-0.1 \u00b0C", main.jTextField37.getText());
    assertEquals("10.0 \u00b0C", main.jTextField38.getText());
  }

  private void runPressureRecord(
      String pressure, String ppp, String a, String destination, Date recordDate) throws Exception {
    writePressureRecord(pressure, ppp, a, recordDate);
    runWorkflow(destination);
  }

  private void runWorkflow(String destination) throws Exception {
    Rs232VaisalaWowAprWorkflow.start(destination, false);
    waitFor(() -> main.obs_file_datum_tijd == null);
    flushEdt();
  }

  private void writePressureRecord(String pressure, String ppp, String a, Date recordDate)
      throws Exception {
    writeRawRecord(fixedWidthPressureRecord(pressure, ppp, a, main.sdf4.format(recordDate)));
  }

  private String fixedWidthPressureRecord(String pressure, String ppp, String a, String timestamp) {
    char[] record = "                                    ".toCharArray();
    copy(record, main.RECORD_P_BEGIN_POS_PTB220, padded(pressure, 7));
    copy(record, main.RECORD_ppp_BEGIN_POS_PTB220, padded(ppp, 5));
    copy(record, main.RECORD_a_BEGIN_POS_PTB220, a);
    copy(record, main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220, timestamp);
    return new String(record);
  }

  private void writeHmpRecord(
      String airTemp, String wetBulb, String dewPoint, String rh, Date recordDate) throws Exception {
    String record =
        padded(airTemp, 8)
            + " "
            + padded(wetBulb, 8)
            + " "
            + padded(dewPoint, 8)
            + " "
            + padded(rh, 8)
            + " "
            + main.sdf4.format(recordDate);
    assertEquals(main.RECORD_LENGTE_HMP155, record.length());

    Calendar fileDate = utcCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    File file =
        new File(
            main.logs_dir,
            "sensor_data_II_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
  }

  private void writeRawRecord(String record) throws Exception {
    Calendar fileDate = utcCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    File file =
        new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
  }

  private void resetModel() {
    mybarometer.pressure_reading_corrected = "";
    mybarometer.pressure_msl_corrected = "";
    mybarometer.PPPP_code = "";
    mybarograph.pressure_amount_tendency = "";
    mybarograph.ppp_code = "";
    mybarograph.a_code = "";
    mytemp.air_temp = "";
    mytemp.wet_bulb_temp = "";
    mytemp.RH = "";
    mytemp.double_dew_point = main.INVALID;
    mytemp.double_rv = main.INVALID;
  }

  private void assertCleared() {
    assertEquals("", mybarometer.pressure_reading_corrected);
    assertEquals("", mybarometer.pressure_msl_corrected);
    assertEquals("", mybarograph.pressure_amount_tendency);
    assertEquals("", mybarograph.a_code);
  }

  private static void copy(char[] target, int start, String value) {
    for (int index = 0; index < value.length(); index++) {
      target[start + index] = value.charAt(index);
    }
  }

  private static String padded(String value, int width) {
    return String.format("%-" + width + "s", value);
  }

  private static Calendar utcCalendar() {
    return new GregorianCalendar(TimeZone.getTimeZone("UTC"));
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
    assertTrue("Timed out waiting for Vaisala WOW/APR workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      SwingUtilities.invokeAndWait(() -> {});
    }
  }
}
