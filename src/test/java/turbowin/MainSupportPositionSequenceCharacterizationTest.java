package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MainSupportPositionSequenceCharacterizationTest {

  @Test
  public void acceptsARecordThatIsTooShortToContainSequenceFields() {
    String originalLastRecord = main.last_record;
    try {
      main.last_record = "short";
      assertTrue(new main_support().position_sequence_check());
    } finally {
      main.last_record = originalLastRecord;
    }
  }

  @Test
  public void acceptsARecordWithMalformedNumericFields() {
    String originalLastRecord = main.last_record;
    try {
      main.last_record = "A2026x011211230450";
      assertTrue(new main_support().position_sequence_check());
    } finally {
      main.last_record = originalLastRecord;
    }
  }

  @Test
  public void acceptsAChronologicalObservationAtTheSamePosition() {
    String originalLastRecord = main.last_record;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    int originalLatitudeDegrees = PositionModel.int_latitude_degrees;
    int originalLatitudeMinutes = PositionModel.int_latitude_minutes;
    String originalLatitudeHemisphere = PositionModel.latitude_hemisphere;
    int originalLongitudeDegrees = PositionModel.int_longitude_degrees;
    int originalLongitudeMinutes = PositionModel.int_longitude_minutes;
    String originalLongitudeHemisphere = PositionModel.longitude_hemisphere;

    try {
      main.last_record = "A202601011211230450";
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "01";
      DateTimeModel.day = "01";
      DateTimeModel.hour = "13";
      PositionModel.int_latitude_degrees = 12;
      PositionModel.int_latitude_minutes = 18;
      PositionModel.latitude_hemisphere = PositionModel.HEMISPHERE_NORTH;
      PositionModel.int_longitude_degrees = 45;
      PositionModel.int_longitude_minutes = 0;
      PositionModel.longitude_hemisphere = PositionModel.HEMISPHERE_EAST;

      assertTrue(new main_support().position_sequence_check());
    } finally {
      main.last_record = originalLastRecord;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      PositionModel.int_latitude_degrees = originalLatitudeDegrees;
      PositionModel.int_latitude_minutes = originalLatitudeMinutes;
      PositionModel.latitude_hemisphere = originalLatitudeHemisphere;
      PositionModel.int_longitude_degrees = originalLongitudeDegrees;
      PositionModel.int_longitude_minutes = originalLongitudeMinutes;
      PositionModel.longitude_hemisphere = originalLongitudeHemisphere;
    }
  }
}
