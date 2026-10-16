package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MintakaDuoRecordParserTest {

  private static final String RECORD = "1021.89,1021.89,1.70,1*05201502041505";

  @Test
  public void parsesDuoFieldsAndTimestamp() {
    MintakaDuoRecord parsed = MintakaDuoRecordParser.parse(RECORD);

    assertEquals("201502041505", parsed.timestamp);
    assertEquals("1021.89", parsed.pressure);
    assertEquals("1.70", parsed.pressureTendency);
    assertEquals("1", parsed.tendencyCharacteristic);
  }

  @Test
  public void rejectsShortAndWrongFormatRecords() {
    // The parser intentionally consolidates these two legacy format failures as null.
    assertNull(MintakaDuoRecordParser.parse("short"));
    assertNull(MintakaDuoRecordParser.parse(RECORD.replace(",1.70", "1.70")));
  }
}
