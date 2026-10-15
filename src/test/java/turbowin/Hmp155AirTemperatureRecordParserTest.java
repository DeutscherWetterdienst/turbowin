package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Hmp155AirTemperatureRecordParserTest {

  private static final String HOUR = "2026010100";

  @Test
  public void parsesTemperatureAndTimestamp() {
    Hmp155AirTemperatureRecord parsed =
        Hmp155AirTemperatureRecordParser.parse(record("22.98   ", "05"), HOUR);

    assertEquals(HOUR + "05", parsed.timestamp);
    assertEquals(5, parsed.minute);
    assertEquals("22.98", parsed.temperature);
    assertFalse(parsed.temperatureOutsideExpectedRange);
  }

  @Test
  public void identifiesInvalidTemperatureButKeepsTheRecord() {
    Hmp155AirTemperatureRecord parsed =
        Hmp155AirTemperatureRecordParser.parse(record("100.0   ", "05"), HOUR);

    assertEquals("100.0", parsed.temperature);
    assertTrue(parsed.temperatureOutsideExpectedRange);
  }

  @Test
  public void rejectsWrongLengthHourAndMinute() {
    assertNull(Hmp155AirTemperatureRecordParser.parse("short", HOUR));
    assertNull(Hmp155AirTemperatureRecordParser.parse(record("22.98   ", "05"), "2026010101"));
    assertNull(Hmp155AirTemperatureRecordParser.parse(record("22.98   ", "04"), HOUR));
  }

  private static String record(String temperature, String minute) {
    return temperature + "abcdefghijklmnopqrstuvwxyzab" + HOUR + minute;
  }
}
