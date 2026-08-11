package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AwsManualInputPreparationWorkflowTest {

  private String originalYear;
  private String originalMonth;
  private String originalDay;
  private String originalHour;
  private String originalMonthCode;
  private String originalYearDayCode;
  private String originalHourCode;
  private String originalLatitudeDegrees;
  private String originalLatitudeMinutes;
  private String originalLongitudeDegrees;
  private String originalLongitudeMinutes;
  private String originalLatitudeHemisphere;
  private String originalLongitudeHemisphere;
  private String originalQcCode;
  private String originalLatitudeCode;
  private String originalLongitudeCode;

  @Before
  public void saveState() {
    originalYear = mydatetime.year;
    originalMonth = mydatetime.month;
    originalDay = mydatetime.day;
    originalHour = mydatetime.hour;
    originalMonthCode = mydatetime.MM_code;
    originalYearDayCode = mydatetime.YY_code;
    originalHourCode = mydatetime.GG_code;
    originalLatitudeDegrees = myposition.latitude_degrees;
    originalLatitudeMinutes = myposition.latitude_minutes;
    originalLongitudeDegrees = myposition.longitude_degrees;
    originalLongitudeMinutes = myposition.longitude_minutes;
    originalLatitudeHemisphere = myposition.latitude_hemisphere;
    originalLongitudeHemisphere = myposition.longitude_hemisphere;
    originalQcCode = myposition.Qc_code;
    originalLatitudeCode = myposition.lalala_code;
    originalLongitudeCode = myposition.lolololo_code;
  }

  @After
  public void restoreState() {
    mydatetime.year = originalYear;
    mydatetime.month = originalMonth;
    mydatetime.day = originalDay;
    mydatetime.hour = originalHour;
    mydatetime.MM_code = originalMonthCode;
    mydatetime.YY_code = originalYearDayCode;
    mydatetime.GG_code = originalHourCode;
    myposition.latitude_degrees = originalLatitudeDegrees;
    myposition.latitude_minutes = originalLatitudeMinutes;
    myposition.longitude_degrees = originalLongitudeDegrees;
    myposition.longitude_minutes = originalLongitudeMinutes;
    myposition.latitude_hemisphere = originalLatitudeHemisphere;
    myposition.longitude_hemisphere = originalLongitudeHemisphere;
    myposition.Qc_code = originalQcCode;
    myposition.lalala_code = originalLatitudeCode;
    myposition.lolololo_code = originalLongitudeCode;
  }

  @Test
  public void convertsAwsDateAndPositionToImmtCodes() {
    mydatetime.year = "2024";
    mydatetime.month = "December";
    mydatetime.day = "31";
    mydatetime.hour = "23";
    myposition.latitude_degrees = "52";
    myposition.latitude_minutes = "30";
    myposition.longitude_degrees = "004";
    myposition.longitude_minutes = "18";
    myposition.latitude_hemisphere = myposition.HEMISPHERE_NORTH;
    myposition.longitude_hemisphere = myposition.HEMISPHERE_EAST;

    AwsManualInputPreparationWorkflow.prepare();

    assertEquals("2025", mydatetime.year);
    assertEquals("01", mydatetime.MM_code);
    assertEquals("01", mydatetime.YY_code);
    assertEquals("00", mydatetime.GG_code);
    assertEquals("1", myposition.Qc_code);
    assertEquals("525", myposition.lalala_code);
    assertEquals("0043", myposition.lolololo_code);
  }

  @Test
  public void clearsCodesWhenPositionIsInvalid() {
    mydatetime.year = "2024";
    mydatetime.month = "January";
    mydatetime.day = "1";
    mydatetime.hour = "0";
    myposition.latitude_degrees = "not-a-number";
    myposition.latitude_minutes = "30";
    myposition.longitude_degrees = "004";
    myposition.longitude_minutes = "18";
    myposition.Qc_code = "1";
    myposition.lalala_code = "525";
    myposition.lolololo_code = "0043";

    AwsManualInputPreparationWorkflow.prepare();

    assertEquals(" ", myposition.Qc_code);
    assertEquals("   ", myposition.lalala_code);
    assertEquals("    ", myposition.lolololo_code);
  }

  @Test
  public void truncatesCoordinateMinutesAndMapsAllQuadrants() {
    assertPositionEncoding(myposition.HEMISPHERE_NORTH, myposition.HEMISPHERE_EAST, "1");
    assertPositionEncoding(myposition.HEMISPHERE_SOUTH, myposition.HEMISPHERE_EAST, "3");
    assertPositionEncoding(myposition.HEMISPHERE_SOUTH, myposition.HEMISPHERE_WEST, "5");
    assertPositionEncoding(myposition.HEMISPHERE_NORTH, myposition.HEMISPHERE_WEST, "7");
  }

  private void assertPositionEncoding(
      String latitudeHemisphere, String longitudeHemisphere, String expectedQcCode) {
    mydatetime.year = "2024";
    mydatetime.month = "January";
    mydatetime.day = "1";
    mydatetime.hour = "0";
    myposition.latitude_degrees = "52";
    myposition.latitude_minutes = "35";
    myposition.longitude_degrees = "004";
    myposition.longitude_minutes = "23";
    myposition.latitude_hemisphere = latitudeHemisphere;
    myposition.longitude_hemisphere = longitudeHemisphere;

    AwsManualInputPreparationWorkflow.prepare();

    assertEquals(expectedQcCode, myposition.Qc_code);
    assertEquals("525", myposition.lalala_code);
    assertEquals("0043", myposition.lolololo_code);
  }
}
