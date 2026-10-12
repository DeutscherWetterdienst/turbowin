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
    originalPressureCorrected = mybarometer.pressure_reading_corrected;
    originalPressureMslCorrected = mybarometer.pressure_msl_corrected;
    originalPressureAmount = mybarograph.pressure_amount_tendency;
    originalACode = mybarograph.a_code;
    originalAirTemp = mytemp.air_temp;
    originalWetBulb = mytemp.wet_bulb_temp;
    originalRh = mytemp.RH;
    originalRelativeHumidity = mytemp.double_rv;
    originalDewPoint = mytemp.double_dew_point;
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
    originalLatitudeDegrees = myposition.latitude_degrees;
    originalLatitudeMinutes = myposition.latitude_minutes;
    originalLatitudeHemisphere = myposition.latitude_hemisphere;
    originalLongitudeDegrees = myposition.longitude_degrees;
    originalLongitudeMinutes = myposition.longitude_minutes;
    originalLongitudeHemisphere = myposition.longitude_hemisphere;
    originalQcCode = myposition.Qc_code;
    originalCourse = myposition.course;
    originalSpeed = myposition.speed;
    originalSog = myposition.SOG_APR;
    originalCog = myposition.COG_APR;
    originalSogWind = myposition.SOG_APR_wind;
    originalCogWind = myposition.COG_APR_wind;

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
    mybarometer.pressure_reading_corrected = "";
    mybarometer.pressure_msl_corrected = "";
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";
    mytemp.air_temp = "";
    mytemp.wet_bulb_temp = "";
    mytemp.RH = "";
    mytemp.double_rv = main.INVALID;
    mytemp.double_dew_point = main.INVALID;
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
    mybarometer.pressure_reading_corrected = originalPressureCorrected;
    mybarometer.pressure_msl_corrected = originalPressureMslCorrected;
    mybarograph.pressure_amount_tendency = originalPressureAmount;
    mybarograph.a_code = originalACode;
    mytemp.air_temp = originalAirTemp;
    mytemp.wet_bulb_temp = originalWetBulb;
    mytemp.RH = originalRh;
    mytemp.double_rv = originalRelativeHumidity;
    mytemp.double_dew_point = originalDewPoint;
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
    myposition.latitude_degrees = originalLatitudeDegrees;
    myposition.latitude_minutes = originalLatitudeMinutes;
    myposition.latitude_hemisphere = originalLatitudeHemisphere;
    myposition.longitude_degrees = originalLongitudeDegrees;
    myposition.longitude_minutes = originalLongitudeMinutes;
    myposition.longitude_hemisphere = originalLongitudeHemisphere;
    myposition.Qc_code = originalQcCode;
    myposition.course = originalCourse;
    myposition.speed = originalSpeed;
    myposition.SOG_APR = originalSog;
    myposition.COG_APR = originalCog;
    myposition.SOG_APR_wind = originalSogWind;
    myposition.COG_APR_wind = originalCogWind;
  }

  @Test
  public void aprFallsBackToTheMostRecentRecordWithGps() throws Exception {
    Date current = new Date();
    appendCurrentRecord(
        starRecord(
            starPayload("1000.0", "1.0", "2", "", ""),
            current));
    appendCurrentRecord(
        starRecord(
            starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E"),
            new Date(current.getTime() - 60_000L)));

    runWorkflow("APR", false, false);

    assertEquals("52 00.0000N", main_RS232_RS422.GPS_latitude);
    assertEquals("004 00.0000E", main_RS232_RS422.GPS_longitude);
    assertEquals("1000.0", main.sensor_data_record_obs_pressure);
    assertTrue(mybarometer.pressure_reading_corrected.length() > 0);
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
                starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E") + ",extra",
                current),
        "MAIN_SCREEN",
        false);
    assertRejectedMainScreenState();

    Date stale = new Date(System.currentTimeMillis() - 20 * 60 * 1000L);
    runWorkflowWithRecord(
        starRecord(
            starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E"), stale),
        "MAIN_SCREEN",
        false);
    assertRejectedMainScreenState();
  }

  @Test
  public void coordinateConversionAndTenMinuteMovementAreApplied() throws Exception {
    main.obs_format = main.FORMAT_101;
    Date current = new Date();
    Date earlier = new Date(current.getTime() - 10 * 60 * 1000L);
    writeRecordInHour(
        starRecord(
            starPayload("1000.0", "1.0", "2", "52 00.0000N", "004 00.0000E"), earlier),
        earlier);
    writeRecordInHour(
        starRecord(
            starPayload("1000.0", "1.0", "2", "52 01.0000N", "004 00.0000E"), current),
        twoMinutesAgo());

    startWorkflow("MAIN_SCREEN", false, false);

    assertEquals("52", myposition.latitude_degrees);
    assertEquals("01", myposition.latitude_minutes);
    assertEquals(myposition.HEMISPHERE_NORTH, myposition.latitude_hemisphere);
    assertEquals("004", myposition.lolololo_code.substring(0, 3));
    assertEquals("1", myposition.Qc_code);
    assertEquals(0.0, myposition.COG_APR, 0.001);
    assertTrue(myposition.SOG_APR > 5.0);
  }

  @Test
  public void starXSensorBoundariesAreApplied() throws Exception {
    runWorkflowWithRecord(
        starRecord(
            starXPayload("1000.0", "1.0", "2", "-69.9", "100", "-69.9", "12.3", "0"),
            new Date()),
        "MAIN_SCREEN",
        true);

    assertEquals("-69.9", mytemp.air_temp);
    assertEquals("-69.9", mytemp.wet_bulb_temp);
    assertEquals("100", mytemp.RH);
    assertEquals(1.0, mytemp.double_rv, 0.001);
    assertEquals(12.3, mytemp.double_dew_point, 0.001);

    runWorkflowWithRecord(
        starRecord(
            starXPayload("900.0", "1.0", "2", "70.0", "101", "70.0", "70.0", "0"),
            new Date()),
        "MAIN_SCREEN",
        true);

    assertEquals("", mybarometer.pressure_reading_corrected);
    assertEquals("", mytemp.air_temp);
    assertEquals("", mytemp.wet_bulb_temp);
    assertEquals("", mytemp.RH);
    assertEquals(main.INVALID, mytemp.double_dew_point, 0.001);
  }

  @Test
  public void wowAprAndMainScreenUseDifferentDestinationsForTheSameRecord() throws Exception {
    runWorkflowWithRecord(
        starRecord(
            starPayload("1000.0", "1.2", "2", "52 00.0000N", "004 00.0000E"),
            new Date()),
        "APR",
        false);
    assertTrue(mybarometer.pressure_reading_corrected.length() > 0);
    assertEquals("1.2", mybarograph.pressure_amount_tendency);
    assertEquals("2", mybarograph.a_code);
    assertEquals("", main.jTextField9.getText());

    runWorkflowWithRecord(
        starRecord(
            starPayload("1000.0", "1.2", "2", "52 00.0000N", "004 00.0000E"),
            new Date()),
        "MAIN_SCREEN",
        false);
    assertTrue(main.jTextField9.getText().contains("hPa"));
    assertEquals("1.2 hPa", main.jTextField11.getText());
    assertEquals("2 (code)", main.jTextField12.getText());

    runWorkflowWithRecord(
        starRecord(
            starPayload("1000.0", "1.2", "2", "52 00.0000N", "004 00.0000E"),
            new Date()),
        "WOW",
        false);
    assertEquals("", mybarometer.pressure_reading_corrected);
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
    Thread.UncaughtExceptionHandler originalHandler =
        Thread.getDefaultUncaughtExceptionHandler();
    if (GraphicsEnvironment.isHeadless()) {
      Thread.setDefaultUncaughtExceptionHandler(
          (thread, error) -> {
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
    File file =
        new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate) + ".txt");
    Files.writeString(
        file.toPath(),
        record + System.lineSeparator(),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
  }

  private String starPayload(String pressure, String ppp, String a, String latitude, String longitude) {
    return pressure
        + ",1000.0,"
        + ppp
        + ","
        + a
        + ","
        + latitude
        + ","
        + longitude
        + ",0,0,0";
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
    myposition.latitude_degrees = "";
    myposition.latitude_minutes = "";
    myposition.latitude_hemisphere = "";
    myposition.longitude_degrees = "";
    myposition.longitude_minutes = "";
    myposition.longitude_hemisphere = "";
    myposition.Qc_code = "";
    myposition.course = "";
    myposition.speed = "";
    myposition.SOG_APR = Double.MAX_VALUE;
    myposition.COG_APR = Double.MAX_VALUE;
    myposition.SOG_APR_wind = Double.MAX_VALUE;
    myposition.COG_APR_wind = Double.MAX_VALUE;
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
          new Thread(
              () -> {
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
