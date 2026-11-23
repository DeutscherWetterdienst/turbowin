package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232MintakaWowAprWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private String originalObsFormat;
  private String originalSendMethod;
  private String originalBarometerAboveSll;
  private String originalKeelSll;
  private String originalAverageDraught;
  private boolean originalApr;
  private int originalConnectionMode;
  private boolean originalObsoleteGps;
  private String originalSensorPressure;
  private String originalSensorPpp;
  private String originalSensorA;
  private GregorianCalendar originalObservationDate;
  private String originalCorrection;
  private String originalPressureCorrected;
  private String originalPressureMslCorrected;
  private String originalPressureAmount;
  private String originalACode;
  private String originalAirTemp;
  private String originalWetBulb;
  private String originalRh;
  private double originalRelativeHumidity;
  private double originalDewPoint;
  private final JTextField[] originalMainFields = new JTextField[8];

  private String originalGpsLatitude;
  private String originalGpsLongitude;
  private String originalGpsLatitudeHemisphere;
  private String originalGpsLongitudeHemisphere;
  private String originalGpsLatitudeEarlier;
  private String originalGpsLongitudeEarlier;
  private String originalGpsLatitudeEarlierWind;
  private String originalGpsLongitudeEarlierWind;
  private String originalLatitudeDegrees;
  private String originalLatitudeMinutes;
  private String originalLatitudeHemisphere;
  private String originalLongitudeDegrees;
  private String originalLongitudeMinutes;
  private String originalLongitudeHemisphere;
  private String originalQcCode;
  private String originalCourse;
  private String originalSpeed;
  private double originalSog;
  private double originalCog;
  private double originalSogWind;
  private double originalCogWind;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObsFormat = main.obs_format;
    originalSendMethod = main.APTR_AWSR_send_method;
    originalBarometerAboveSll = main.barometer_above_sll;
    originalKeelSll = main.keel_sll;
    originalAverageDraught = main.WOW_APR_average_draught;
    originalApr = main.APR;
    originalConnectionMode = main.RS232_connection_mode;
    originalObsoleteGps = main.obsolate_GPS_data_flag;
    originalSensorPressure = main.sensor_data_record_obs_pressure;
    originalSensorPpp = main.sensor_data_record_obs_ppp;
    originalSensorA = main.sensor_data_record_obs_a;
    originalObservationDate = main.obs_file_datum_tijd;
    originalCorrection = main.barometer_instrument_correction;
    originalPressureCorrected = BarometerModel.pressure_reading_corrected;
    originalPressureMslCorrected = BarometerModel.pressure_msl_corrected;
    originalPressureAmount = BarographModel.pressure_amount_tendency;
    originalACode = BarographModel.a_code;
    originalAirTemp = TemperatureModel.air_temp;
    originalWetBulb = TemperatureModel.wet_bulb_temp;
    originalRh = TemperatureModel.RH;
    originalRelativeHumidity = TemperatureModel.double_rv;
    originalDewPoint = TemperatureModel.double_dew_point;
    originalMainFields[0] = main.jTextField5;
    originalMainFields[1] = main.jTextField7;
    originalMainFields[2] = main.jTextField9;
    originalMainFields[3] = main.jTextField10;
    originalMainFields[4] = main.jTextField11;
    originalMainFields[5] = main.jTextField12;
    originalMainFields[6] = main.jTextField36;
    originalMainFields[7] = main.jTextField37;

    originalGpsLatitude = main_RS232_RS422.GPS_latitude;
    originalGpsLongitude = main_RS232_RS422.GPS_longitude;
    originalGpsLatitudeHemisphere = main_RS232_RS422.GPS_latitude_hemisphere;
    originalGpsLongitudeHemisphere = main_RS232_RS422.GPS_longitude_hemisphere;
    originalGpsLatitudeEarlier = main_RS232_RS422.GPS_latitude_earlier;
    originalGpsLongitudeEarlier = main_RS232_RS422.GPS_longitude_earlier;
    originalGpsLatitudeEarlierWind = main_RS232_RS422.GPS_latitude_earlier_wind;
    originalGpsLongitudeEarlierWind = main_RS232_RS422.GPS_longitude_earlier_wind;
    originalLatitudeDegrees = PositionModel.latitude_degrees;
    originalLatitudeMinutes = PositionModel.latitude_minutes;
    originalLatitudeHemisphere = PositionModel.latitude_hemisphere;
    originalLongitudeDegrees = PositionModel.longitude_degrees;
    originalLongitudeMinutes = PositionModel.longitude_minutes;
    originalLongitudeHemisphere = PositionModel.longitude_hemisphere;
    originalQcCode = PositionModel.Qc_code;
    originalCourse = PositionModel.course;
    originalSpeed = PositionModel.speed;
    originalSog = PositionModel.SOG_APR;
    originalCog = PositionModel.COG_APR;
    originalSogWind = PositionModel.SOG_APR_wind;
    originalCogWind = PositionModel.COG_APR_wind;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main.obs_format = main.FORMAT_101;
    main.APTR_AWSR_send_method = "unsupported-test-method";
    main.barometer_above_sll = "10";
    main.keel_sll = "0";
    main.WOW_APR_average_draught = "0";
    main.APR = true;
    main.RS232_connection_mode = 0;
    main.obsolate_GPS_data_flag = false;
    main.barometer_instrument_correction = "0";
    main.sensor_data_record_obs_pressure = "";
    main.sensor_data_record_obs_ppp = "";
    main.sensor_data_record_obs_a = "";
    BarometerModel.pressure_reading_corrected = "";
    BarometerModel.pressure_msl_corrected = "";
    BarographModel.pressure_amount_tendency = "";
    BarographModel.a_code = "";
    TemperatureModel.air_temp = "";
    TemperatureModel.wet_bulb_temp = "";
    TemperatureModel.RH = "";
    TemperatureModel.double_rv = main.INVALID;
    TemperatureModel.double_dew_point = main.INVALID;
    resetPositionState();
    setMainFieldReferences();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.obs_format = originalObsFormat;
    main.APTR_AWSR_send_method = originalSendMethod;
    main.barometer_above_sll = originalBarometerAboveSll;
    main.keel_sll = originalKeelSll;
    main.WOW_APR_average_draught = originalAverageDraught;
    main.APR = originalApr;
    main.RS232_connection_mode = originalConnectionMode;
    main.obsolate_GPS_data_flag = originalObsoleteGps;
    main.sensor_data_record_obs_pressure = originalSensorPressure;
    main.sensor_data_record_obs_ppp = originalSensorPpp;
    main.sensor_data_record_obs_a = originalSensorA;
    main.obs_file_datum_tijd = originalObservationDate;
    main.barometer_instrument_correction = originalCorrection;
    BarometerModel.pressure_reading_corrected = originalPressureCorrected;
    BarometerModel.pressure_msl_corrected = originalPressureMslCorrected;
    BarographModel.pressure_amount_tendency = originalPressureAmount;
    BarographModel.a_code = originalACode;
    TemperatureModel.air_temp = originalAirTemp;
    TemperatureModel.wet_bulb_temp = originalWetBulb;
    TemperatureModel.RH = originalRh;
    TemperatureModel.double_rv = originalRelativeHumidity;
    TemperatureModel.double_dew_point = originalDewPoint;
    main.jTextField5 = originalMainFields[0];
    main.jTextField7 = originalMainFields[1];
    main.jTextField9 = originalMainFields[2];
    main.jTextField10 = originalMainFields[3];
    main.jTextField11 = originalMainFields[4];
    main.jTextField12 = originalMainFields[5];
    main.jTextField36 = originalMainFields[6];
    main.jTextField37 = originalMainFields[7];

    main_RS232_RS422.GPS_latitude = originalGpsLatitude;
    main_RS232_RS422.GPS_longitude = originalGpsLongitude;
    main_RS232_RS422.GPS_latitude_hemisphere = originalGpsLatitudeHemisphere;
    main_RS232_RS422.GPS_longitude_hemisphere = originalGpsLongitudeHemisphere;
    main_RS232_RS422.GPS_latitude_earlier = originalGpsLatitudeEarlier;
    main_RS232_RS422.GPS_longitude_earlier = originalGpsLongitudeEarlier;
    main_RS232_RS422.GPS_latitude_earlier_wind = originalGpsLatitudeEarlierWind;
    main_RS232_RS422.GPS_longitude_earlier_wind = originalGpsLongitudeEarlierWind;
    PositionModel.latitude_degrees = originalLatitudeDegrees;
    PositionModel.latitude_minutes = originalLatitudeMinutes;
    PositionModel.latitude_hemisphere = originalLatitudeHemisphere;
    PositionModel.longitude_degrees = originalLongitudeDegrees;
    PositionModel.longitude_minutes = originalLongitudeMinutes;
    PositionModel.longitude_hemisphere = originalLongitudeHemisphere;
    PositionModel.Qc_code = originalQcCode;
    PositionModel.course = originalCourse;
    PositionModel.speed = originalSpeed;
    PositionModel.SOG_APR = originalSog;
    PositionModel.COG_APR = originalCog;
    PositionModel.SOG_APR_wind = originalSogWind;
    PositionModel.COG_APR_wind = originalCogWind;
  }

  @Test
  public void aprFallsBackToTheMostRecentRecordWithGps() throws Exception {
    Date current = new Date();
    appendCurrentRecord(starRecord(starPayload("1000.0", "1.0", "2", "", ""), current));
    appendCurrentRecord(
        starRecord(
            starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E"),
            new Date(current.getTime() - 60_000L)));

    runWorkflow("APR", false, false);

    assertEquals("52 00.0000N", main_RS232_RS422.GPS_latitude);
    assertEquals("004 00.0000E", main_RS232_RS422.GPS_longitude);
    assertEquals("1000.0", main.sensor_data_record_obs_pressure);
    assertTrue(BarometerModel.pressure_reading_corrected.length() > 0);
  }

  @Test
  public void checksumFormatAndStaleRecordsClearTheMainScreenState() throws Exception {
    Date current = new Date();
    runWorkflowWithRecord(
        starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E")
            + "*00"
            + main.sdf4.format(current),
        "MAIN_SCREEN",
        false);
    assertRejectedMainScreenState();

    runWorkflowWithRecord(
        starRecord(
            starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E") + ",extra", current),
        "MAIN_SCREEN",
        false);
    assertRejectedMainScreenState();

    Date stale = new Date(System.currentTimeMillis() - 20 * 60 * 1000L);
    runWorkflowWithRecord(
        starRecord(starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E"), stale),
        "MAIN_SCREEN",
        false);
    assertRejectedMainScreenState();
  }

  @Test
  public void coordinateConversionAndTenMinuteMovementAreApplied() throws Exception {
    main.obs_format = main.FORMAT_101;
    // Match the reader's two-minute file margin, including at the start of an hour.
    Date current = twoMinutesAgo();
    Date earlier = new Date(current.getTime() - 10 * 60 * 1000L);
    writeRecordInHour(
        starRecord(starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E"), earlier),
        earlier);
    writeRecordInHour(
        starRecord(starPayload("1000.0", "1.0", "2", "52 01.0000N", "004 00.0000E"), current),
        current);

    startWorkflow("MAIN_SCREEN", false, false);

    assertEquals("52", PositionModel.latitude_degrees);
    assertEquals("01", PositionModel.latitude_minutes);
    assertEquals(PositionModel.HEMISPHERE_NORTH, PositionModel.latitude_hemisphere);
    assertEquals("004", PositionModel.lolololo_code.substring(0, 3));
    assertEquals("1", PositionModel.Qc_code);
    assertEquals(0.0, PositionModel.COG_APR, 0.001);
    assertTrue(PositionModel.SOG_APR > 5.0);
  }

  @Test
  public void historicalTargetIsSelectedWhenNewerRecordFollowsIt() throws Exception {
    Date current = twoMinutesAgo();
    Date earlier = new Date(current.getTime() - 10 * 60 * 1000L);
    Date newer = new Date(earlier.getTime() + 60 * 1000L);

    writeRecordInHour(
        starRecord(starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E"), earlier),
        earlier);
    writeRecordInHour(
        starRecord(starPayload("1000.0", "1.0", "2", "53 00.0000N", "005 00.0000E"), newer),
        earlier);
    writeRecordInHour(
        starRecord(starPayload("1000.0", "1.0", "2", "52 01.0000N", "004 01.0000E"), current),
        current);

    startWorkflow("MAIN_SCREEN", false, false);

    assertEquals("52 00.0000N", main_RS232_RS422.GPS_latitude_earlier);
    assertEquals("004 00.0000E", main_RS232_RS422.GPS_longitude_earlier);
  }

  @Test
  public void invalidChecksumDuplicateClearsTheEarlierWowAprPosition() throws Exception {
    assertInvalidHistoricalDuplicateClearsPosition(false);
  }

  @Test
  public void invalidCommaCountDuplicateClearsTheEarlierWowAprPosition() throws Exception {
    assertInvalidHistoricalDuplicateClearsPosition(true);
  }

  private void assertInvalidHistoricalDuplicateClearsPosition(boolean wrongCommaCount)
      throws Exception {
    for (boolean starX : new boolean[] {false, true}) {
      Date current = twoMinutesAgo();
      Date earlier = new Date(current.getTime() - 10 * 60 * 1000L);
      String extension = starX ? ",20.0,50,19.0,10.0,0" : "";
      String payload = starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E") + extension;
      writeRecordInHour(starRecord(payload, earlier), earlier);
      String invalid =
          wrongCommaCount
              ? starRecord(payload + ",extra", earlier)
              : payload + "*ZZ" + main.sdf4.format(earlier);
      writeRecordInHour(invalid, earlier);
      writeRecordInHour(
          starRecord(
              starPayload("1000.0", "1.0", "2", "52 01.0000N", "004 01.0000E") + extension,
              current),
          current);

      startWorkflow("MAIN_SCREEN", false, starX);

      assertEquals("", main_RS232_RS422.GPS_latitude_earlier);
      assertEquals("", main_RS232_RS422.GPS_longitude_earlier);
      assertEquals("52 01.0000N", main_RS232_RS422.GPS_latitude);
      assertEquals("004 01.0000E", main_RS232_RS422.GPS_longitude);
      assertEquals(Double.MAX_VALUE, PositionModel.SOG_APR, 0.0);
      assertEquals(Double.MAX_VALUE, PositionModel.COG_APR, 0.0);
    }
  }

  @Test
  public void starXPressureReductionUsesCurrentTemperatureInsteadOfSharedState() throws Exception {
    main.barometer_above_sll = "20";
    TemperatureModel.air_temp = "-20.0";
    runWorkflowWithRecord(
        starRecord(
            starXPayload("1000.0", "1.0", "2", "30.0", "50", "20.0", "10.0", "0"), new Date()),
        "MAIN_SCREEN",
        true);
    assertEquals("1002.25", BarometerModel.pressure_msl_corrected);
  }

  @Test
  public void invalidStarXTemperatureFallsBackWithoutDroppingPressure() throws Exception {
    main.barometer_above_sll = "20";
    runWorkflowWithRecord(
        starRecord(
            starXPayload("1000.0", "1.0", "2", "999.9", "50", "20.0", "10.0", "0"), new Date()),
        "MAIN_SCREEN",
        true);
    assertEquals("1002.37", BarometerModel.pressure_msl_corrected);
  }

  @Test
  public void pressureOnlyInstrumentDoesNotUseSharedTemperature() throws Exception {
    main.barometer_above_sll = "20";
    TemperatureModel.air_temp = "30.0";
    runWorkflowWithRecord(
        starRecord(starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E"), new Date()),
        "MAIN_SCREEN",
        false);
    assertEquals("1002.37", BarometerModel.pressure_msl_corrected);
  }

  @Test
  public void starXSensorBoundariesAreApplied() throws Exception {
    runWorkflowWithRecord(
        starRecord(
            starXPayload("1000.0", "1.0", "2", "-69.9", "100", "-69.9", "12.3", "0"), new Date()),
        "MAIN_SCREEN",
        true);

    assertEquals("-69.9", TemperatureModel.air_temp);
    assertEquals("-69.9", TemperatureModel.wet_bulb_temp);
    assertEquals("100", TemperatureModel.RH);
    assertEquals(1.0, TemperatureModel.double_rv, 0.001);
    assertEquals(12.3, TemperatureModel.double_dew_point, 0.001);

    runWorkflowWithRecord(
        starRecord(
            starXPayload("900.0", "1.0", "2", "70.0", "101", "70.0", "70.0", "0"), new Date()),
        "MAIN_SCREEN",
        true);

    assertEquals("", BarometerModel.pressure_reading_corrected);
    assertEquals("", TemperatureModel.air_temp);
    assertEquals("", TemperatureModel.wet_bulb_temp);
    assertEquals("", TemperatureModel.RH);
    assertEquals(main.INVALID, TemperatureModel.double_dew_point, 0.001);
  }

  @Test
  public void wowAprAndMainScreenUseDifferentDestinationsForTheSameRecord() throws Exception {
    runWorkflowWithRecord(
        starRecord(starPayload("1000.0", "1.2", "2", "52 00.0000N", "004 00.0000E"), new Date()),
        "APR",
        false);
    assertTrue(BarometerModel.pressure_reading_corrected.length() > 0);
    assertEquals("1.2", BarographModel.pressure_amount_tendency);
    assertEquals("2", BarographModel.a_code);
    assertEquals("", main.jTextField9.getText());

    runWorkflowWithRecord(
        starRecord(starPayload("1000.0", "1.2", "2", "52 00.0000N", "004 00.0000E"), new Date()),
        "MAIN_SCREEN",
        false);
    assertTrue(main.jTextField9.getText().contains("hPa"));
    assertEquals("1.2 hPa", main.jTextField11.getText());
    assertEquals("2 (code)", main.jTextField12.getText());

    runWorkflowWithRecord(
        starRecord(starPayload("1000.0", "1.2", "2", "52 00.0000N", "004 00.0000E"), new Date()),
        "WOW",
        false);
    assertEquals("", BarometerModel.pressure_reading_corrected);
    assertEquals("", main.sensor_data_record_obs_pressure);
  }

  private void assertRejectedMainScreenState() {
    assertEquals("", main.sensor_data_record_obs_pressure);
    assertEquals("", main_RS232_RS422.GPS_latitude);
    assertEquals("", main_RS232_RS422.GPS_longitude);
    assertTrue(main.obsolate_GPS_data_flag);
  }

  private void runWorkflowWithRecord(String record, String destination, boolean starX)
      throws Exception {
    writeCurrentRecord(record);
    startWorkflow(destination, false, starX);
  }

  private void runWorkflow(String destination, boolean retry, boolean starX) throws Exception {
    startWorkflow(destination, retry, starX);
  }

  private void startWorkflow(String destination, boolean retry, boolean starX) throws Exception {
    DialogCloser dialogCloser = new DialogCloser();
    dialogCloser.start();
    Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
    if (GraphicsEnvironment.isHeadless()) {
      Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
        if (!(error instanceof HeadlessException) && originalHandler != null) {
          originalHandler.uncaughtException(thread, error);
        }
      });
    }
    try {
      main.obs_file_datum_tijd = new GregorianCalendar();
      Rs232MintakaWowAprWorkflow.start(destination, retry, starX);
      waitFor(() -> main.obs_file_datum_tijd == null);
      dialogCloser.await();
      flushEdt();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void appendCurrentRecord(String record) throws Exception {
    Calendar fileDate = new GregorianCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    writeRecordInHour(record, fileDate.getTime());
  }

  private void writeCurrentRecord(String record) throws Exception {
    appendCurrentRecord(record);
  }

  private Date twoMinutesAgo() {
    return new Date(System.currentTimeMillis() - 2 * 60 * 1000L);
  }

  private void writeRecordInHour(String record, Date fileDate) throws Exception {
    File file = new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate) + ".txt");
    Files.writeString(
        file.toPath(),
        record + System.lineSeparator(),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
  }

  private String starPayload(
      String pressure, String ppp, String a, String latitude, String longitude) {
    return pressure + ",1000.0," + ppp + "," + a + "," + latitude + "," + longitude + ",0,0,0";
  }

  private String starXPayload(
      String pressure,
      String ppp,
      String a,
      String airTemp,
      String rh,
      String wetBulb,
      String dewPoint,
      String age) {
    return starPayload(pressure, ppp, a, "52 00.0000N", "004 00.0000E")
        + ","
        + airTemp
        + ","
        + rh
        + ","
        + wetBulb
        + ","
        + dewPoint
        + ","
        + age;
  }

  private String starRecord(String payload, Date recordDate) {
    String checksum = main_RS232_RS422.Mintaka_Star_Checksum(payload + "*");
    return payload + "*" + checksum + main.sdf4.format(recordDate);
  }

  private static SimpleDateFormat utcFormat(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format;
  }

  private void setMainFieldReferences() {
    main.jTextField5 = new JTextField();
    main.jTextField7 = new JTextField();
    main.jTextField9 = new JTextField();
    main.jTextField10 = new JTextField();
    main.jTextField11 = new JTextField();
    main.jTextField12 = new JTextField();
    main.jTextField36 = new JTextField();
    main.jTextField37 = new JTextField();
  }

  private void resetPositionState() {
    main_RS232_RS422.GPS_latitude = "";
    main_RS232_RS422.GPS_longitude = "";
    main_RS232_RS422.GPS_latitude_hemisphere = "";
    main_RS232_RS422.GPS_longitude_hemisphere = "";
    main_RS232_RS422.GPS_latitude_earlier = "";
    main_RS232_RS422.GPS_longitude_earlier = "";
    main_RS232_RS422.GPS_latitude_earlier_wind = "";
    main_RS232_RS422.GPS_longitude_earlier_wind = "";
    PositionModel.latitude_degrees = "";
    PositionModel.latitude_minutes = "";
    PositionModel.latitude_hemisphere = "";
    PositionModel.longitude_degrees = "";
    PositionModel.longitude_minutes = "";
    PositionModel.longitude_hemisphere = "";
    PositionModel.Qc_code = "";
    PositionModel.course = "";
    PositionModel.speed = "";
    PositionModel.SOG_APR = Double.MAX_VALUE;
    PositionModel.COG_APR = Double.MAX_VALUE;
    PositionModel.SOG_APR_wind = Double.MAX_VALUE;
    PositionModel.COG_APR_wind = Double.MAX_VALUE;
  }

  private static void waitFor(BooleanSupplier condition) throws Exception {
    long deadline = System.nanoTime() + 5_000_000_000L;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
    assertTrue("Timed out waiting for Mintaka WOW/APR workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      SwingUtilities.invokeAndWait(() -> {});
    }
  }

  private static final class DialogCloser {
    private Thread thread;

    void start() {
      if (GraphicsEnvironment.isHeadless()) {
        return;
      }
      thread =
          new Thread(() -> {
            long deadline = System.nanoTime() + 5_000_000_000L;
            while (System.nanoTime() < deadline) {
              for (Window window : Window.getWindows()) {
                if (window instanceof JDialog && window.isVisible()) {
                  SwingUtilities.invokeLater(window::dispose);
                  return;
                }
              }
              try {
                Thread.sleep(10);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
              }
            }
          });
      thread.setDaemon(true);
      thread.start();
    }

    void await() throws InterruptedException {
      if (thread != null) {
        thread.join(5_000);
      }
    }
  }
}
