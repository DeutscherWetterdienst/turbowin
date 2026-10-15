package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MintakaBarographRecordParserTest {

  private static final String HOUR = "2017031520";

  @Test
  public void parsesMintakaDuoMode() {
    String record = "1021.89,1021.89,1.70,1*00" + HOUR + "05";

    MintakaBarographRecord parsed = MintakaBarographRecordParser.parse(record, 4, HOUR);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals("1021.89", parsed.pressure);
  }

  @Test
  public void parsesAndValidatesMintakaStarPressure() {
    String record = "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19" + HOUR + "05";

    MintakaBarographRecord parsed = MintakaBarographRecordParser.parse(record, 5, HOUR);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals(5, parsed.minute);
    assertEquals("1029.97", parsed.pressure);
  }

  @Test
  public void parsesMintakaStarWifiMode() {
    String record = "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19" + HOUR + "05";

    MintakaBarographRecord parsed = MintakaBarographRecordParser.parse(record, 6, HOUR);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals("1029.97", parsed.pressure);
  }

  @Test
  public void rejectsPressureOutsideTheExpectedRange() {
    String record = "899.99,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19" + HOUR + "05";

    MintakaBarographRecord parsed = MintakaBarographRecordParser.parse(record, 5, HOUR);

    assertEquals("", parsed.pressure);
  }

  @Test
  public void parsesValidStarXPressureOnlyWhenObservationIsRecent() {
    String withoutChecksum =
        "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60*00"
            + HOUR
            + "05";
    String record =
        withoutChecksum.replace(
            "*00", "*" + main_RS232_RS422.Mintaka_Star_Checksum(withoutChecksum));

    MintakaBarographRecord parsed = MintakaBarographRecordParser.parse(record, 7, HOUR);

    assertEquals("1018.13", parsed.pressure);
  }

  @Test
  public void parsesMintakaStarXWifiMode() {
    String withoutChecksum =
        "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60*00"
            + HOUR
            + "05";
    String record =
        withoutChecksum.replace(
            "*00", "*" + main_RS232_RS422.Mintaka_Star_Checksum(withoutChecksum));

    MintakaBarographRecord parsed = MintakaBarographRecordParser.parse(record, 8, HOUR);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals("1018.13", parsed.pressure);
  }

  @Test
  public void rejectsWrongFormatHourMinuteAndChecksum() {
    String record = "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19" + HOUR + "05";

    assertNull(MintakaBarographRecordParser.parse(record, 4, HOUR));
    assertNull(MintakaBarographRecordParser.parse(record, 5, "2017031521"));
    assertNull(MintakaBarographRecordParser.parse(record, 5, HOUR.replace("20", "21")));

    String starXWithoutChecksum =
        "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60*00"
            + HOUR
            + "05";
    assertNull(MintakaBarographRecordParser.parse(starXWithoutChecksum, 7, HOUR));
  }
}
