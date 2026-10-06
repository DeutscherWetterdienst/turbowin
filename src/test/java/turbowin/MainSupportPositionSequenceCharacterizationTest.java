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
    String originalYear = mydatetime.year;
    String originalMonth = mydatetime.MM_code;
    String originalDay = mydatetime.day;
    String originalHour = mydatetime.hour;
    int originalLatitudeDegrees = myposition.int_latitude_degrees;
    int originalLatitudeMinutes = myposition.int_latitude_minutes;
    String originalLatitudeHemisphere = myposition.latitude_hemisphere;
    int originalLongitudeDegrees = myposition.int_longitude_degrees;
    int originalLongitudeMinutes = myposition.int_longitude_minutes;
    String originalLongitudeHemisphere = myposition.longitude_hemisphere;

    try {
      main.last_record = "A202601011211230450";
      mydatetime.year = "2026";
      mydatetime.MM_code = "01";
      mydatetime.day = "01";
      mydatetime.hour = "13";
      myposition.int_latitude_degrees = 12;
      myposition.int_latitude_minutes = 18;
      myposition.latitude_hemisphere = myposition.HEMISPHERE_NORTH;
      myposition.int_longitude_degrees = 45;
      myposition.int_longitude_minutes = 0;
      myposition.longitude_hemisphere = myposition.HEMISPHERE_EAST;

      assertTrue(new main_support().position_sequence_check());
    } finally {
      main.last_record = originalLastRecord;
      mydatetime.year = originalYear;
      mydatetime.MM_code = originalMonth;
      mydatetime.day = originalDay;
      mydatetime.hour = originalHour;
      myposition.int_latitude_degrees = originalLatitudeDegrees;
      myposition.int_latitude_minutes = originalLatitudeMinutes;
      myposition.latitude_hemisphere = originalLatitudeHemisphere;
      myposition.int_longitude_degrees = originalLongitudeDegrees;
      myposition.int_longitude_minutes = originalLongitudeMinutes;
      myposition.longitude_hemisphere = originalLongitudeHemisphere;
    }
  }
}
