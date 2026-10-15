package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MintakaStarXAirTemperatureRecordParserTest {

  private static final String HOUR = "2017031520";

  @Test
  public void parsesValidTemperatureAndTimestamp() {
    String record = validRecord("29.4", "60");

    MintakaStarXAirTemperatureRecord parsed =
        MintakaStarXAirTemperatureRecordParser.parse(record, HOUR);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals(5, parsed.minute);
    assertEquals("29.4", parsed.temperature);
  }

  @Test
  public void extractsTemperatureFieldAndAcceptsObservationAgeInSeconds() {
    MintakaStarXAirTemperatureRecord parsed =
        MintakaStarXAirTemperatureRecordParser.parse(validRecord("19.5", "600"), HOUR);

    assertEquals("19.5", parsed.temperature);
  }

  @Test
  public void preservesEmptyValueForTemperatureOutsideTheRange() {
    MintakaStarXAirTemperatureRecord parsed =
        MintakaStarXAirTemperatureRecordParser.parse(validRecord("61.0", "60"), HOUR);

    assertEquals("", parsed.temperature);
  }

  @Test
  public void rejectsStaleAndInvalidRecords() {
    assertNull(MintakaStarXAirTemperatureRecordParser.parse(validRecord("29.4", "601"), HOUR));
    assertNull(
        MintakaStarXAirTemperatureRecordParser.parse(
            replaceChecksum(validRecord("29.4", "60"), "00"), HOUR));
    assertNull(
        MintakaStarXAirTemperatureRecordParser.parse(validRecord("29.4", "60"), "2017031521"));
  }

  private static String validRecord(String temperature, String observationAge) {
    String withoutChecksum =
        "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,"
            + temperature
            + ",85,27.4,26.7,"
            + observationAge
            + "*00"
            + HOUR
            + "05";
    return withoutChecksum.replace(
        "*00", "*" + main_RS232_RS422.Mintaka_Star_Checksum(withoutChecksum));
  }

  private static String replaceChecksum(String record, String checksum) {
    return record.substring(0, record.length() - 14) + "*" + checksum + HOUR + "05";
  }
}
