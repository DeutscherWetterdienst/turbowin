package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FixedLengthBarographRecordParserTest {

  private static final String HOUR = "2026010100";

  @Test
  public void parsesPtb220PressureAndTimestamp() {
    String record = "1013.25abcdefghijklmnopq" + HOUR + "05";

    FixedLengthBarographRecord parsed =
        FixedLengthBarographRecordParser.parse(record, 36, 24, 34, 0, HOUR);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals(5, parsed.minute);
    assertEquals("1013.25", parsed.pressure);
    assertFalse(parsed.pressureOutsideExpectedRange);
  }

  @Test
  public void identifiesInvalidPressureButKeepsTheRecord() {
    String record = "899.990abcdefghijklmnopq" + HOUR + "05";

    FixedLengthBarographRecord parsed =
        FixedLengthBarographRecordParser.parse(record, 36, 24, 34, 0, HOUR);

    assertTrue(parsed.pressureOutsideExpectedRange);
    assertEquals("899.990", parsed.pressure);
  }

  @Test
  public void rejectsWrongLengthHourAndMinute() {
    String record = "1013.25abcdefghijklmnopq" + HOUR + "05";

    assertNull(FixedLengthBarographRecordParser.parse(record, 35, 24, 34, 0, HOUR));
    assertNull(FixedLengthBarographRecordParser.parse(record, 36, 24, 34, 0, "2026010101"));
    assertNull(
        FixedLengthBarographRecordParser.parse(
            record.replace(HOUR + "05", HOUR + "04"), 36, 24, 34, 0, HOUR));
  }
}
