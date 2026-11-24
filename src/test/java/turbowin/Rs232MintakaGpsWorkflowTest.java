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
  private PositionSelections originalSelections;

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
    originalLatitudeDegrees = PositionModel.latitude_degrees;
    originalLatitudeMinutes = PositionModel.latitude_minutes;
    originalLatitudeHemisphere = PositionModel.latitude_hemisphere;
    originalLongitudeDegrees = PositionModel.longitude_degrees;
    originalLongitudeMinutes = PositionModel.longitude_minutes;
    originalLongitudeHemisphere = PositionModel.longitude_hemisphere;
    originalLalalaCode = PositionModel.lalala_code;
    originalLolololoCode = PositionModel.lolololo_code;
    originalQcCode = PositionModel.Qc_code;
    originalCourse = PositionModel.course;
    originalSpeed = PositionModel.speed;
    originalLatitudeDegreesInt = PositionModel.int_latitude_degrees;
    originalLatitudeMinutesInt = PositionModel.int_latitude_minutes;
    originalLongitudeDegreesInt = PositionModel.int_longitude_degrees;
    originalLongitudeMinutesInt = PositionModel.int_longitude_minutes;
    originalSog = PositionModel.SOG_APR;
    originalCog = PositionModel.COG_APR;
    originalLatitudeField = PositionModel.jTextField1;
    originalLatitudeMinutesField = PositionModel.jTextField2;
    originalLongitudeField = PositionModel.jTextField3;
    originalLongitudeMinutesField = PositionModel.jTextField4;
    originalSelections = PositionModel.selections;

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
    PositionModel.latitude_degrees = "";
    PositionModel.latitude_minutes = "";
    PositionModel.latitude_hemisphere = "";
    PositionModel.longitude_degrees = "";
    PositionModel.longitude_minutes = "";
    PositionModel.longitude_hemisphere = "";
    PositionModel.lalala_code = "";
    PositionModel.lolololo_code = "";
    PositionModel.Qc_code = "";
    PositionModel.course = "";
    PositionModel.speed = "";
    PositionModel.jTextField1 = new JTextField();
    PositionModel.jTextField2 = new JTextField();
    PositionModel.jTextField3 = new JTextField();
    PositionModel.jTextField4 = new JTextField();
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
    PositionModel.latitude_degrees = originalLatitudeDegrees;
    PositionModel.latitude_minutes = originalLatitudeMinutes;
    PositionModel.latitude_hemisphere = originalLatitudeHemisphere;
    PositionModel.longitude_degrees = originalLongitudeDegrees;
    PositionModel.longitude_minutes = originalLongitudeMinutes;
    PositionModel.longitude_hemisphere = originalLongitudeHemisphere;
    PositionModel.lalala_code = originalLalalaCode;
    PositionModel.lolololo_code = originalLolololoCode;
    PositionModel.Qc_code = originalQcCode;
    PositionModel.course = originalCourse;
    PositionModel.speed = originalSpeed;
    PositionModel.int_latitude_degrees = originalLatitudeDegreesInt;
    PositionModel.int_latitude_minutes = originalLatitudeMinutesInt;
    PositionModel.int_longitude_degrees = originalLongitudeDegreesInt;
    PositionModel.int_longitude_minutes = originalLongitudeMinutesInt;
    PositionModel.SOG_APR = originalSog;
    PositionModel.COG_APR = originalCog;
    PositionModel.jTextField1 = originalLatitudeField;
    PositionModel.jTextField2 = originalLatitudeMinutesField;
    PositionModel.jTextField3 = originalLongitudeField;
    PositionModel.jTextField4 = originalLongitudeMinutesField;
    PositionModel.selections = originalSelections;
  }

  @Test
  public void starRecordConvertsHemisphereCoordinatesAndSelectsCogAndSogCategories()
      throws Exception {
    Date end = new Date();
    Date start = shifted(end, -10);
    writePositionPair(
        false, end, start, " 52 00.0000N", " 004 01.6300E", " 52 00.0000N", " 004 00.0000E");

    runWorkflow(false);

    assertEquals("52", PositionModel.latitude_degrees);
    assertEquals("00", PositionModel.latitude_minutes);
    assertEquals(PositionModel.HEMISPHERE_NORTH, PositionModel.latitude_hemisphere);
    assertEquals("004", PositionModel.longitude_degrees);
    assertEquals("02", PositionModel.longitude_minutes);
    assertEquals(PositionModel.HEMISPHERE_EAST, PositionModel.longitude_hemisphere);
    assertEquals("1", PositionModel.Qc_code);
    assertEquals(PositionSelections.Course.E, PositionModel.selections.course.get());
    assertEquals(PositionSelections.Speed.SIX_TO_TEN, PositionModel.selections.speed.get());
  }

  @Test
  public void starXRecordUsesItsCommaCountAndConvertsSouthWestCoordinates() throws Exception {
    Date end = new Date();
    Date start = shifted(end, -10);
    writePositionPair(
        true, end, start, " 12 00.0000S", " 034 00.0000W", " 12 00.0000S", " 034 00.0000W");

    runWorkflow(true);

    assertEquals("12", PositionModel.latitude_degrees);
    assertEquals(PositionModel.HEMISPHERE_SOUTH, PositionModel.latitude_hemisphere);
    assertEquals("034", PositionModel.longitude_degrees);
    assertEquals(PositionModel.HEMISPHERE_WEST, PositionModel.longitude_hemisphere);
    assertEquals("5", PositionModel.Qc_code);
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
    assertEquals("52", PositionModel.latitude_degrees);
  }

  @Test
  public void historicalTargetIsSelectedWhenNewerRecordFollowsIt() throws Exception {
    Date end = new Date();
    Date start = shifted(end, -10);
    Date newer = shifted(start, 1);

    writeHistoryRecord(starRecord(starPayload(" 52 00.0000N", " 004 00.0000E", 8), start));
    writeHistoryRecord(starRecord(starPayload(" 53 00.0000N", " 005 00.0000E", 8), newer));
    writeCurrentRecord(starRecord(starPayload(" 52 02.0000N", " 004 02.0000E", 8), end), true);

    runWorkflow(false);

    assertEquals(" 52 00.0000N", main_RS232_RS422.GPS_latitude_earlier);
    assertEquals(" 004 00.0000E", main_RS232_RS422.GPS_longitude_earlier);
  }

  private void runWorkflow(boolean starX) throws Exception {
    Rs232MintakaGpsWorkflow.start("APR", starX);
    waitFor(() -> main.obs_file_datum_tijd == null);
    flushEdt();
  }

  @Test
  public void invalidChecksumDuplicateClearsTheEarlierGpsPosition() throws Exception {
    assertInvalidHistoricalDuplicateClearsPosition(false);
  }

  @Test
  public void invalidCommaCountDuplicateClearsTheEarlierGpsPosition() throws Exception {
    assertInvalidHistoricalDuplicateClearsPosition(true);
  }

  private void assertInvalidHistoricalDuplicateClearsPosition(boolean wrongCommaCount)
      throws Exception {
    for (boolean starX : new boolean[] {false, true}) {
      Date end = shifted(new Date(), -2);
      Date start = shifted(end, -10);
      String payload = starPayload(" 52 00.0000N", " 004 00.0000E", starX ? 13 : 8);
      writeHistoryRecord(starRecord(payload, start));
      String invalid =
          wrongCommaCount
              ? starRecord(payload + ",extra", start)
              : payload + "*ZZ" + main.sdf4.format(start);
      writeHistoryRecord(invalid);
      writeCurrentRecord(
          starRecord(starPayload(" 52 02.0000N", " 004 02.0000E", starX ? 13 : 8), end), true);

      runWorkflow(starX);

      assertEquals("", main_RS232_RS422.GPS_latitude_earlier);
      assertEquals("", main_RS232_RS422.GPS_longitude_earlier);
      // Invalid history must not discard the independent, valid current position.
      assertEquals(" 52 02.0000N", main_RS232_RS422.GPS_latitude);
      assertEquals(" 004 02.0000E", main_RS232_RS422.GPS_longitude);
    }
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
    String payload = "1000.00,1000.00,1.00,2," + latitude + "," + longitude + ",0,0,0";
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
    PositionModel.selections = new PositionSelections();
  }

  private void resetPositionControls() {
    setPositionControls();
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
