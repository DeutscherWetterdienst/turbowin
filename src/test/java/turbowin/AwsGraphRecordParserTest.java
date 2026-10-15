package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AwsGraphRecordParserTest {

  private static final String HOUR = "2026010100";
  private static final String RECORD =
      "$PEUMB,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26," + HOUR + "05";

  @Test
  public void parsesTimestampMinuteAndPrimaryAndSecondaryValues() {
    AwsGraphRecord parsed = AwsGraphRecordParser.parse(RECORD, HOUR, 5, 7);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals(5, parsed.minute);
    assertEquals("5", parsed.primaryValue);
    assertEquals("7", parsed.secondaryValue);
  }

  @Test
  public void acceptsRecordWithOnlySecondaryGraphValue() {
    AwsGraphRecord parsed =
        AwsGraphRecordParser.parse(RECORD.replace(",5,", ",,"), HOUR, 5, 7);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals(5, parsed.minute);
    assertNull(parsed.primaryValue);
    assertEquals("7", parsed.secondaryValue);
  }

  @Test
  public void rejectsInvalidPrefixHourAndMinute() {
    assertNull(AwsGraphRecordParser.parse(RECORD.replace("$PEUMB", "$OTHER"), HOUR, 5, -1));
    assertNull(AwsGraphRecordParser.parse(RECORD, "2026010101", 5, -1));
    assertNull(AwsGraphRecordParser.parse(RECORD.replace("05", "04"), HOUR, 5, -1));
  }

  @Test
  public void rejectsWrongCommaCountAndMissingValues() {
    assertNull(AwsGraphRecordParser.parse(RECORD.replace(",26", ""), HOUR, 5, -1));
    assertNull(AwsGraphRecordParser.parse(RECORD.replace(",5,", ",,"), HOUR, 5, -1));
  }
}
