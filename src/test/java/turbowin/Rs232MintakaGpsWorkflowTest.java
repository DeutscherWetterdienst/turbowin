package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232MintakaGpsWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private String originalObservationFormat;
  private GregorianCalendar originalObservationDate;
  private String originalGpsLatitude;
  private String originalGpsLongitude;
  private String originalGpsLatitudeHemisphere;
  private String originalGpsLongitudeHemisphere;
  private String originalGpsLatitudeEarlier;
  private String originalGpsLongitudeEarlier;
  private String originalLatitudeDegrees;
  private String originalLatitudeMinutes;
  private String originalLatitudeHemisphere;
  private String originalLongitudeDegrees;
  private String originalLongitudeMinutes;
  private String originalLongitudeHemisphere;
  private String originalLalalaCode;
  private String originalLolololoCode;
  private String originalQcCode;
  private String originalCourse;
  private String originalSpeed;
  private int originalLatitudeDegreesInt;
  private int originalLatitudeMinutesInt;
  private int originalLongitudeDegreesInt;
  private int originalLongitudeMinutesInt;
  private double originalSog;
  private double originalCog;
  private JTextField originalLatitudeField;
  private JTextField originalLatitudeMinutesField;
  private JTextField originalLongitudeField;
  private JTextField originalLongitudeMinutesField;
  private final ButtonGroup[] originalButtonGroups = new ButtonGroup[4];
  private final JRadioButton[] originalButtons = new JRadioButton[23];

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObservationFormat = main.obs_format;
    originalObservationDate = main.obs_file_datum_tijd;
    originalGpsLatitude = main_RS232_RS422.GPS_latitude;
    originalGpsLongitude = main_RS232_RS422.GPS_longitude;
    originalGpsLatitudeHemisphere = main_RS232_RS422.GPS_latitude_hemisphere;
    originalGpsLongitudeHemisphere = main_RS232_RS422.GPS_longitude_hemisphere;
    originalGpsLatitudeEarlier = main_RS232_RS422.GPS_latitude_earlier;
    originalGpsLongitudeEarlier = main_RS232_RS422.GPS_longitude_earlier;
    originalLatitudeDegrees = myposition.latitude_degrees;
    originalLatitudeMinutes = myposition.latitude_minutes;
    originalLatitudeHemisphere = myposition.latitude_hemisphere;
    originalLongitudeDegrees = myposition.longitude_degrees;
    originalLongitudeMinutes = myposition.longitude_minutes;
    originalLongitudeHemisphere = myposition.longitude_hemisphere;
    originalLalalaCode = myposition.lalala_code;
    originalLolololoCode = myposition.lolololo_code;
    originalQcCode = myposition.Qc_code;
    originalCourse = myposition.course;
    originalSpeed = myposition.speed;
    originalLatitudeDegreesInt = myposition.int_latitude_degrees;
    originalLatitudeMinutesInt = myposition.int_latitude_minutes;
    originalLongitudeDegreesInt = myposition.int_longitude_degrees;
    originalLongitudeMinutesInt = myposition.int_longitude_minutes;
    originalSog = myposition.SOG_APR;
    originalCog = myposition.COG_APR;
    originalLatitudeField = myposition.jTextField1;
    originalLatitudeMinutesField = myposition.jTextField2;
    originalLongitudeField = myposition.jTextField3;
    originalLongitudeMinutesField = myposition.jTextField4;
    originalButtonGroups[0] = myposition.buttonGroup1;
    originalButtonGroups[1] = myposition.buttonGroup2;
    originalButtonGroups[2] = myposition.buttonGroup3;
    originalButtonGroups[3] = myposition.buttonGroup4;
    originalButtons[0] = myposition.jRadioButton1;
    originalButtons[1] = myposition.jRadioButton2;
    originalButtons[2] = myposition.jRadioButton3;
    originalButtons[3] = myposition.jRadioButton4;
    originalButtons[4] = myposition.jRadioButton5;
    originalButtons[5] = myposition.jRadioButton6;
    originalButtons[6] = myposition.jRadioButton7;
    originalButtons[7] = myposition.jRadioButton8;
    originalButtons[8] = myposition.jRadioButton9;
    originalButtons[9] = myposition.jRadioButton10;
    originalButtons[10] = myposition.jRadioButton11;
    originalButtons[11] = myposition.jRadioButton12;
    originalButtons[12] = myposition.jRadioButton13;
    originalButtons[13] = myposition.jRadioButton14;
    originalButtons[14] = myposition.jRadioButton15;
    originalButtons[15] = myposition.jRadioButton16;
    originalButtons[16] = myposition.jRadioButton17;
    originalButtons[17] = myposition.jRadioButton18;
    originalButtons[18] = myposition.jRadioButton19;
    originalButtons[19] = myposition.jRadioButton20;
    originalButtons[20] = myposition.jRadioButton21;
    originalButtons[21] = myposition.jRadioButton22;
    originalButtons[22] = myposition.jRadioButton23;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main.obs_format = main.FORMAT_101;
    main_RS232_RS422.GPS_latitude = "";
    main_RS232_RS422.GPS_longitude = "";
    main_RS232_RS422.GPS_latitude_hemisphere = "";
    main_RS232_RS422.GPS_longitude_hemisphere = "";
    main_RS232_RS422.GPS_latitude_earlier = "";
    main_RS232_RS422.GPS_longitude_earlier = "";
    myposition.latitude_degrees = "";
    myposition.latitude_minutes = "";
    myposition.latitude_hemisphere = "";
    myposition.longitude_degrees = "";
    myposition.longitude_minutes = "";
    myposition.longitude_hemisphere = "";
    myposition.lalala_code = "";
    myposition.lolololo_code = "";
    myposition.Qc_code = "";
    myposition.course = "";
    myposition.speed = "";
    myposition.jTextField1 = new JTextField();
    myposition.jTextField2 = new JTextField();
    myposition.jTextField3 = new JTextField();
    myposition.jTextField4 = new JTextField();
    setPositionControls();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.obs_format = originalObservationFormat;
    main.obs_file_datum_tijd = originalObservationDate;
    main_RS232_RS422.GPS_latitude = originalGpsLatitude;
    main_RS232_RS422.GPS_longitude = originalGpsLongitude;
    main_RS232_RS422.GPS_latitude_hemisphere = originalGpsLatitudeHemisphere;
    main_RS232_RS422.GPS_longitude_hemisphere = originalGpsLongitudeHemisphere;
    main_RS232_RS422.GPS_latitude_earlier = originalGpsLatitudeEarlier;
    main_RS232_RS422.GPS_longitude_earlier = originalGpsLongitudeEarlier;
    myposition.latitude_degrees = originalLatitudeDegrees;
    myposition.latitude_minutes = originalLatitudeMinutes;
    myposition.latitude_hemisphere = originalLatitudeHemisphere;
    myposition.longitude_degrees = originalLongitudeDegrees;
    myposition.longitude_minutes = originalLongitudeMinutes;
    myposition.longitude_hemisphere = originalLongitudeHemisphere;
    myposition.lalala_code = originalLalalaCode;
    myposition.lolololo_code = originalLolololoCode;
    myposition.Qc_code = originalQcCode;
    myposition.course = originalCourse;
    myposition.speed = originalSpeed;
    myposition.int_latitude_degrees = originalLatitudeDegreesInt;
    myposition.int_latitude_minutes = originalLatitudeMinutesInt;
    myposition.int_longitude_degrees = originalLongitudeDegreesInt;
    myposition.int_longitude_minutes = originalLongitudeMinutesInt;
    myposition.SOG_APR = originalSog;
    myposition.COG_APR = originalCog;
    myposition.jTextField1 = originalLatitudeField;
    myposition.jTextField2 = originalLatitudeMinutesField;
    myposition.jTextField3 = originalLongitudeField;
    myposition.jTextField4 = originalLongitudeMinutesField;
    setButtonReferences(originalButtons);
    myposition.buttonGroup1 = originalButtonGroups[0];
    myposition.buttonGroup2 = originalButtonGroups[1];
    myposition.buttonGroup3 = originalButtonGroups[2];
    myposition.buttonGroup4 = originalButtonGroups[3];
  }

  @Test
  public void starRecordConvertsHemisphereCoordinatesAndSelectsCogAndSogCategories()
      throws Exception {
    Date end = new Date();
    Date start = shifted(end, -10);
    writePositionPair(
        false,
        end,
        start,
        " 52 00.0000N",
        " 004 01.6300E",
        " 52 00.0000N",
        " 004 00.0000E");

    runWorkflow(false);

    assertEquals("52", myposition.latitude_degrees);
    assertEquals("00", myposition.latitude_minutes);
    assertEquals(myposition.HEMISPHERE_NORTH, myposition.latitude_hemisphere);
    assertEquals("004", myposition.longitude_degrees);
    assertEquals("02", myposition.longitude_minutes);
    assertEquals(myposition.HEMISPHERE_EAST, myposition.longitude_hemisphere);
    assertEquals("1", myposition.Qc_code);
    assertTrue(myposition.jRadioButton7.isSelected());
    assertTrue(myposition.jRadioButton16.isSelected());
  }

  @Test
  public void starXRecordUsesItsCommaCountAndConvertsSouthWestCoordinates() throws Exception {
    Date end = new Date();
    Date start = shifted(end, -10);
    writePositionPair(
        true,
        end,
        start,
        " 12 00.0000S",
        " 034 00.0000W",
        " 12 00.0000S",
        " 034 00.0000W");

    runWorkflow(true);

    assertEquals("12", myposition.latitude_degrees);
    assertEquals(myposition.HEMISPHERE_SOUTH, myposition.latitude_hemisphere);
    assertEquals("034", myposition.longitude_degrees);
    assertEquals(myposition.HEMISPHERE_WEST, myposition.longitude_hemisphere);
    assertEquals("5", myposition.Qc_code);
  }

  @Test
  public void checksumAndCommaCountFailuresRejectTheCurrentPosition() throws Exception {
    Date end = new Date();
    Date start = shifted(end, -10);
    String payload = starPayload(" 52 00.0000N", " 004 00.0000E", 8);
    String wrongChecksum = payload + "*00" + main.sdf4.format(end);
    writeHistoryRecord(starRecord(starPayload(" 52 00.0000N", " 004 00.0000E", 8), start));
    writeCurrentRecord(wrongChecksum, true);
    runWorkflow(false);
    assertEquals("", main_RS232_RS422.GPS_latitude);

    resetPositionControls();
    String commaMismatch = starRecord(starPayload(" 52 00.0000N", " 004 00.0000E", 9), end);
    writeCurrentRecord(commaMismatch);
    runWorkflow(false);
    assertEquals("", main_RS232_RS422.GPS_latitude);
  }

  @Test
  public void staleOrMissingHistoryPositionClearsTheFix() throws Exception {
    Date stale = new Date(System.currentTimeMillis() - 20 * 60 * 1000L);
    writeCurrentRecord(starRecord(starPayload(" 52 00.0000N", " 004 00.0000E", 8), stale));
    runWorkflow(false);
    assertEquals("", main_RS232_RS422.GPS_latitude);

    resetPositionControls();
    Date end = new Date();
    writeCurrentRecord(starRecord(starPayload(" 52 00.0000N", " 004 00.0000E", 8), end));
    runWorkflow(false);
    assertEquals(" 52 00.0000N", main_RS232_RS422.GPS_latitude);
    assertEquals("52", myposition.latitude_degrees);
  }

  @Test
  public void historicalTargetIsSelectedWhenNewerRecordFollowsIt() throws Exception {
    Date end = new Date();
    Date start = shifted(end, -10);
    Date newer = shifted(start, 1);

    writeHistoryRecord(starRecord(starPayload(" 52 00.0000N", " 004 00.0000E", 8), start));
    writeHistoryRecord(starRecord(starPayload(" 53 00.0000N", " 005 00.0000E", 8), newer));
    writeCurrentRecord(
        starRecord(starPayload(" 52 02.0000N", " 004 02.0000E", 8), end), true);

    runWorkflow(false);

    assertEquals(" 52 00.0000N", main_RS232_RS422.GPS_latitude_earlier);
    assertEquals(" 004 00.0000E", main_RS232_RS422.GPS_longitude_earlier);
  }

  private void runWorkflow(boolean starX) throws Exception {
    Rs232MintakaGpsWorkflow.start("APR", starX);
    waitFor(() -> main.obs_file_datum_tijd == null);
    flushEdt();
  }

  private void writePositionPair(
      boolean starX,
      Date end,
      Date start,
      String currentLatitude,
      String currentLongitude,
      String earlierLatitude,
      String earlierLongitude)
      throws Exception {
    String currentPayload = starPayload(currentLatitude, currentLongitude, starX ? 13 : 8);
    String earlierPayload = starPayload(earlierLatitude, earlierLongitude, starX ? 13 : 8);
    writeHistoryRecord(starRecord(earlierPayload, start));
    writeCurrentRecord(starRecord(currentPayload, end), true);
  }

  private String starPayload(String latitude, String longitude, int commaCount) {
    String payload =
        "1000.00,1000.00,1.00,2,"
            + latitude
            + ","
            + longitude
            + ",0,0,0";
    if (commaCount == 13) {
      payload += ",20.0,50,19.0,10.0,0";
    } else if (commaCount == 9) {
      payload += ",extra";
    }
    return payload;
  }

  private String starRecord(String payload, Date recordDate) {
    String checksum = main_RS232_RS422.Mintaka_Star_Checksum(payload + "*");
    return payload + "*" + checksum + main.sdf4.format(recordDate);
  }

  private void writeCurrentRecord(String record) throws Exception {
    writeCurrentRecord(record, false);
  }

  private void writeCurrentRecord(String record, boolean append) throws Exception {
    Calendar fileDate = utcCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    writeFile(record, fileDate, append);
  }

  private void writeHistoryRecord(String record) throws Exception {
    String timestamp = record.substring(record.length() - 12);
    Date recordDate = main.sdf4.parse(timestamp);
    Calendar fileDate = utcCalendar(recordDate);
    writeFile(record, fileDate, true);
  }

  private void writeFile(String record, Calendar fileDate, boolean append) throws Exception {
    File file =
        new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    if (append) {
      Files.writeString(
          file.toPath(),
          record + System.lineSeparator(),
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } else {
      Files.writeString(file.toPath(), record + System.lineSeparator());
    }
  }

  private void setPositionControls() {
    myposition.buttonGroup1 = new ButtonGroup();
    myposition.buttonGroup2 = new ButtonGroup();
    myposition.buttonGroup3 = new ButtonGroup();
    myposition.buttonGroup4 = new ButtonGroup();
    JRadioButton[] buttons = new JRadioButton[23];
    for (int index = 0; index < buttons.length; index++) {
      buttons[index] = new JRadioButton();
    }
    setButtonReferences(buttons);
    myposition.buttonGroup1.add(myposition.jRadioButton1);
    myposition.buttonGroup1.add(myposition.jRadioButton2);
    myposition.buttonGroup2.add(myposition.jRadioButton3);
    myposition.buttonGroup2.add(myposition.jRadioButton4);
    for (int index = 4; index < 13; index++) {
      myposition.buttonGroup3.add(buttons[index]);
    }
    for (int index = 13; index < buttons.length; index++) {
      myposition.buttonGroup4.add(buttons[index]);
    }
  }

  private void resetPositionControls() {
    setPositionControls();
  }

  private static void setButtonReferences(JRadioButton[] buttons) {
    myposition.jRadioButton1 = buttons[0];
    myposition.jRadioButton2 = buttons[1];
    myposition.jRadioButton3 = buttons[2];
    myposition.jRadioButton4 = buttons[3];
    myposition.jRadioButton5 = buttons[4];
    myposition.jRadioButton6 = buttons[5];
    myposition.jRadioButton7 = buttons[6];
    myposition.jRadioButton8 = buttons[7];
    myposition.jRadioButton9 = buttons[8];
    myposition.jRadioButton10 = buttons[9];
    myposition.jRadioButton11 = buttons[10];
    myposition.jRadioButton12 = buttons[11];
    myposition.jRadioButton13 = buttons[12];
    myposition.jRadioButton14 = buttons[13];
    myposition.jRadioButton15 = buttons[14];
    myposition.jRadioButton16 = buttons[15];
    myposition.jRadioButton17 = buttons[16];
    myposition.jRadioButton18 = buttons[17];
    myposition.jRadioButton19 = buttons[18];
    myposition.jRadioButton20 = buttons[19];
    myposition.jRadioButton21 = buttons[20];
    myposition.jRadioButton22 = buttons[21];
    myposition.jRadioButton23 = buttons[22];
  }

  private static Calendar utcCalendar() {
    return new GregorianCalendar(TimeZone.getTimeZone("UTC"));
  }

  private static Calendar utcCalendar(Date date) {
    Calendar calendar = utcCalendar();
    calendar.setTime(date);
    return calendar;
  }

  private static Date shifted(Date date, int minutes) {
    Calendar calendar = utcCalendar(date);
    calendar.add(Calendar.MINUTE, minutes);
    return calendar.getTime();
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
    assertTrue("Timed out waiting for Mintaka GPS workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      SwingUtilities.invokeAndWait(() -> {});
    }
  }
}
