package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Rs232GpsNmeaDatePositionParsingCharacterizationTest {

  private String originalDate;
  private String originalTime;
  private String originalLatitude;
  private String originalLatitudeHemisphere;
  private String originalLongitude;
  private String originalLongitudeHemisphere;
  private String originalObservationFormat;
  private double[][] originalGpsArrayPositions;
  private long[] originalGpsArrayTimes;

  @Before
  public void saveGpsInputState() throws Exception {
    originalDate = main_RS232_RS422.GPS_date;
    originalTime = main_RS232_RS422.GPS_time;
    originalLatitude = main_RS232_RS422.GPS_latitude;
    originalLatitudeHemisphere = main_RS232_RS422.GPS_latitude_hemisphere;
    originalLongitude = main_RS232_RS422.GPS_longitude;
    originalLongitudeHemisphere = main_RS232_RS422.GPS_longitude_hemisphere;
    originalObservationFormat = main.obs_format;
    originalGpsArrayPositions = copyGpsArrayPositions(gpsArrayPositions());
    originalGpsArrayTimes = Arrays.copyOf(gpsArrayTimes(), gpsArrayTimes().length);
  }

  @After
  public void restoreGpsInputState() throws Exception {
    main_RS232_RS422.GPS_date = originalDate;
    main_RS232_RS422.GPS_time = originalTime;
    main_RS232_RS422.GPS_latitude = originalLatitude;
    main_RS232_RS422.GPS_latitude_hemisphere = originalLatitudeHemisphere;
    main_RS232_RS422.GPS_longitude = originalLongitude;
    main_RS232_RS422.GPS_longitude_hemisphere = originalLongitudeHemisphere;
    main.obs_format = originalObservationFormat;
    double[][] gpsPositions = gpsArrayPositions();
    for (int i = 0; i < gpsPositions.length; i++) {
      gpsPositions[i] = Arrays.copyOf(originalGpsArrayPositions[i], originalGpsArrayPositions[i].length);
    }
    System.arraycopy(originalGpsArrayTimes, 0, gpsArrayTimes(), 0, originalGpsArrayTimes.length);
  }

  @Test
  public void rejectsMissingDateOrTimeWithoutShowingManualErrorUi() {
    main_RS232_RS422.GPS_date = "";
    main_RS232_RS422.GPS_time = "";

    assertFalse(main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("MINUTE_UPDATE"));
  }

  @Test
  public void rejectsAnObsoleteGpsDateWithoutShowingManualErrorUi() {
    main_RS232_RS422.GPS_date = "000000";
    main_RS232_RS422.GPS_time = "000000";

    assertFalse(main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("MINUTE_UPDATE"));
  }

  @Test
  public void parsesValidSouthwesternFixAndStoresSignedTrackPosition() throws Exception {
    setCurrentGpsDateTime();
    main_RS232_RS422.GPS_latitude = "1259.6";
    main_RS232_RS422.GPS_latitude_hemisphere = "S";
    main_RS232_RS422.GPS_longitude = "17959.6";
    main_RS232_RS422.GPS_longitude_hemisphere = "W";
    main.obs_format = main.FORMAT_101;

    assertTrue(main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("MINUTE_UPDATE"));

    assertEquals("13", myposition.latitude_degrees);
    assertEquals("0", myposition.latitude_minutes);
    assertEquals(myposition.HEMISPHERE_SOUTH, myposition.latitude_hemisphere);
    assertEquals("130", myposition.lalala_code);
    assertEquals("180", myposition.longitude_degrees);
    assertEquals("0", myposition.longitude_minutes);
    assertEquals(myposition.HEMISPHERE_WEST, myposition.longitude_hemisphere);
    assertEquals("1800", myposition.lolololo_code);
    assertEquals("5", myposition.Qc_code);
    assertEquals(-13.993333333333334, gpsArrayPositions()[0][0], 0.0000000001);
    assertEquals(-180.99333333333334, gpsArrayPositions()[0][1], 0.0000000001);
  }

  @Test
  public void rejectsUnknownCoordinateHemispheres() {
    setCurrentGpsDateTime();
    main_RS232_RS422.GPS_latitude = "4804.7";
    main_RS232_RS422.GPS_latitude_hemisphere = "X";
    main_RS232_RS422.GPS_longitude = "01144.3";
    main_RS232_RS422.GPS_longitude_hemisphere = "Y";

    assertFalse(main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("MINUTE_UPDATE"));
  }

  private static void setCurrentGpsDateTime() {
    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
    TimeZone utc = TimeZone.getTimeZone("UTC");
    dateFormat.setTimeZone(utc);
    timeFormat.setTimeZone(utc);
    main_RS232_RS422.GPS_date = dateFormat.format(now);
    main_RS232_RS422.GPS_time = timeFormat.format(now);
  }

  private static double[][] gpsArrayPositions() throws Exception {
    Field field = main_RS232_RS422.class.getDeclaredField("GPS_array_pos");
    field.setAccessible(true);
    return (double[][]) field.get(null);
  }

  private static long[] gpsArrayTimes() throws Exception {
    Field field = main_RS232_RS422.class.getDeclaredField("GPS_array_time");
    field.setAccessible(true);
    return (long[]) field.get(null);
  }

  private static double[][] copyGpsArrayPositions(double[][] positions) {
    double[][] copy = new double[positions.length][];
    for (int i = 0; i < positions.length; i++) {
      copy[i] = Arrays.copyOf(positions[i], positions[i].length);
    }
    return copy;
  }
}
