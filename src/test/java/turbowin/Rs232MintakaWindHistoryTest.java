package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232MintakaWindHistoryTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDirectory;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private String originalLatitude;
  private String originalLongitude;

  @Before
  public void setUp() {
    originalLogsDirectory = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalLatitude = main_RS232_RS422.GPS_latitude_earlier_wind;
    originalLongitude = main_RS232_RS422.GPS_longitude_earlier_wind;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main_RS232_RS422.GPS_latitude_earlier_wind = "";
    main_RS232_RS422.GPS_longitude_earlier_wind = "";
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDirectory;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main_RS232_RS422.GPS_latitude_earlier_wind = originalLatitude;
    main_RS232_RS422.GPS_longitude_earlier_wind = originalLongitude;
  }

  @Test
  public void selectsMatchingWindHistoryRecordWhenNewerRecordFollowsIt() throws Exception {
    GregorianCalendar end = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    end.set(Calendar.MINUTE, 30);
    end.set(Calendar.SECOND, 0);
    end.set(Calendar.MILLISECOND, 0);

    GregorianCalendar target = (GregorianCalendar) end.clone();
    target.add(Calendar.MINUTE, -10);
    GregorianCalendar newer = (GregorianCalendar) target.clone();
    newer.add(Calendar.MINUTE, 1);

    String targetRecord =
        starRecord(" 52 00.0000N", " 004 00.0000E", target.getTime());
    String newerRecord = starRecord(" 53 00.0000N", " 005 00.0000E", newer.getTime());
    File historyFile =
        new File(main.logs_dir, "sensor_data_" + main.sdf3.format(target.getTime()) + ".txt");
    Files.writeString(
        historyFile.toPath(), targetRecord + System.lineSeparator() + newerRecord);

    RS232_mintaka.RS232_Mintaka_Star_And_StarX_Determine_Position_Earlier_For_Wind(
        main.sdf4.format(end.getTime()), false);

    assertEquals(" 52 00.0000N", main_RS232_RS422.GPS_latitude_earlier_wind);
    assertEquals(" 004 00.0000E", main_RS232_RS422.GPS_longitude_earlier_wind);
  }

  @Test
  public void invalidChecksumDuplicateClearsTheEarlierWindPosition() throws Exception {
    String valid = starRecord(" 52 00.0000N", " 004 00.0000E", main.sdf4.parse("201703152020"));
    String invalid = valid.substring(0, valid.length() - 14) + "ZZ201703152020";
    assertInvalidDuplicateClearsPosition(valid, invalid);
  }

  @Test
  public void invalidCommaCountDuplicateClearsTheEarlierWindPosition() throws Exception {
    String valid = starRecord(" 52 00.0000N", " 004 00.0000E", main.sdf4.parse("201703152020"));
    String payload = valid.substring(0, valid.indexOf('*')) + ",extra";
    String invalid =
        payload + "*" + main_RS232_RS422.Mintaka_Star_Checksum(payload + "*") + "201703152020";
    assertInvalidDuplicateClearsPosition(valid, invalid);
  }

  private void assertInvalidDuplicateClearsPosition(String valid, String invalid) throws Exception {
    File historyFile = new File(main.logs_dir, "sensor_data_2017031520.txt");
    // A later record for a different minute must not undo the duplicate's invalidation.
    String newer = starRecord(" 53 00.0000N", " 005 00.0000E", main.sdf4.parse("201703152021"));
    Files.writeString(
        historyFile.toPath(),
        valid + System.lineSeparator() + invalid + System.lineSeparator() + newer);

    RS232_mintaka.RS232_Mintaka_Star_And_StarX_Determine_Position_Earlier_For_Wind(
        "201703152030", false);

    assertEquals("", main_RS232_RS422.GPS_latitude_earlier_wind);
    assertEquals("", main_RS232_RS422.GPS_longitude_earlier_wind);
  }

  private String starRecord(String latitude, String longitude, java.util.Date date) {
    String payload =
        "1000.00,1000.00,1.00,2," + latitude + "," + longitude + ",0,0,0";
    return payload
        + "*"
        + main_RS232_RS422.Mintaka_Star_Checksum(payload + "*")
        + main.sdf4.format(date);
  }

  private static SimpleDateFormat utcFormat(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format;
  }
}
