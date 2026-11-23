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
    originalYear = DateTimeModel.year;
    originalMonth = DateTimeModel.month;
    originalDay = DateTimeModel.day;
    originalHour = DateTimeModel.hour;
    originalMonthCode = DateTimeModel.MM_code;
    originalYearDayCode = DateTimeModel.YY_code;
    originalHourCode = DateTimeModel.GG_code;
    originalLatitudeDegrees = PositionModel.latitude_degrees;
    originalLatitudeMinutes = PositionModel.latitude_minutes;
    originalLongitudeDegrees = PositionModel.longitude_degrees;
    originalLongitudeMinutes = PositionModel.longitude_minutes;
    originalLatitudeHemisphere = PositionModel.latitude_hemisphere;
    originalLongitudeHemisphere = PositionModel.longitude_hemisphere;
    originalQcCode = PositionModel.Qc_code;
    originalLatitudeCode = PositionModel.lalala_code;
    originalLongitudeCode = PositionModel.lolololo_code;
  }

  @After
  public void restoreState() {
    DateTimeModel.year = originalYear;
    DateTimeModel.month = originalMonth;
    DateTimeModel.day = originalDay;
    DateTimeModel.hour = originalHour;
    DateTimeModel.MM_code = originalMonthCode;
    DateTimeModel.YY_code = originalYearDayCode;
    DateTimeModel.GG_code = originalHourCode;
    PositionModel.latitude_degrees = originalLatitudeDegrees;
    PositionModel.latitude_minutes = originalLatitudeMinutes;
    PositionModel.longitude_degrees = originalLongitudeDegrees;
    PositionModel.longitude_minutes = originalLongitudeMinutes;
    PositionModel.latitude_hemisphere = originalLatitudeHemisphere;
    PositionModel.longitude_hemisphere = originalLongitudeHemisphere;
    PositionModel.Qc_code = originalQcCode;
    PositionModel.lalala_code = originalLatitudeCode;
    PositionModel.lolololo_code = originalLongitudeCode;
  }

  @Test
  public void convertsAwsDateAndPositionToImmtCodes() {
    DateTimeModel.year = "2024";
    DateTimeModel.month = "December";
    DateTimeModel.day = "31";
    DateTimeModel.hour = "23";
    PositionModel.latitude_degrees = "52";
    PositionModel.latitude_minutes = "30";
    PositionModel.longitude_degrees = "004";
    PositionModel.longitude_minutes = "18";
    PositionModel.latitude_hemisphere = PositionModel.HEMISPHERE_NORTH;
    PositionModel.longitude_hemisphere = PositionModel.HEMISPHERE_EAST;

    AwsManualInputPreparationWorkflow.prepare();

    assertEquals("2025", DateTimeModel.year);
    assertEquals("01", DateTimeModel.MM_code);
    assertEquals("01", DateTimeModel.YY_code);
    assertEquals("00", DateTimeModel.GG_code);
    assertEquals("1", PositionModel.Qc_code);
    assertEquals("525", PositionModel.lalala_code);
    assertEquals("0043", PositionModel.lolololo_code);
  }

  @Test
  public void clearsCodesWhenPositionIsInvalid() {
    DateTimeModel.year = "2024";
    DateTimeModel.month = "January";
    DateTimeModel.day = "1";
    DateTimeModel.hour = "0";
    PositionModel.latitude_degrees = "not-a-number";
    PositionModel.latitude_minutes = "30";
    PositionModel.longitude_degrees = "004";
    PositionModel.longitude_minutes = "18";
    PositionModel.Qc_code = "1";
    PositionModel.lalala_code = "525";
    PositionModel.lolololo_code = "0043";

    AwsManualInputPreparationWorkflow.prepare();

    assertEquals(" ", PositionModel.Qc_code);
    assertEquals("   ", PositionModel.lalala_code);
    assertEquals("    ", PositionModel.lolololo_code);
  }

  @Test
  public void truncatesCoordinateMinutesAndMapsAllQuadrants() {
    assertPositionEncoding(PositionModel.HEMISPHERE_NORTH, PositionModel.HEMISPHERE_EAST, "1");
    assertPositionEncoding(PositionModel.HEMISPHERE_SOUTH, PositionModel.HEMISPHERE_EAST, "3");
    assertPositionEncoding(PositionModel.HEMISPHERE_SOUTH, PositionModel.HEMISPHERE_WEST, "5");
    assertPositionEncoding(PositionModel.HEMISPHERE_NORTH, PositionModel.HEMISPHERE_WEST, "7");
  }

  private void assertPositionEncoding(
      String latitudeHemisphere, String longitudeHemisphere, String expectedQcCode) {
    DateTimeModel.year = "2024";
    DateTimeModel.month = "January";
    DateTimeModel.day = "1";
    DateTimeModel.hour = "0";
    PositionModel.latitude_degrees = "52";
    PositionModel.latitude_minutes = "35";
    PositionModel.longitude_degrees = "004";
    PositionModel.longitude_minutes = "23";
    PositionModel.latitude_hemisphere = latitudeHemisphere;
    PositionModel.longitude_hemisphere = longitudeHemisphere;

    AwsManualInputPreparationWorkflow.prepare();

    assertEquals(expectedQcCode, PositionModel.Qc_code);
    assertEquals("525", PositionModel.lalala_code);
    assertEquals("0043", PositionModel.lolololo_code);
  }
}
