package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateTimeUtilsTest {

  @Test
  public void convertsAllCalendarMonthNumbers() {
    assertEquals("January", DateTimeUtils.convert_month(0));
    assertEquals("February", DateTimeUtils.convert_month(1));
    assertEquals("March", DateTimeUtils.convert_month(2));
    assertEquals("April", DateTimeUtils.convert_month(3));
    assertEquals("May", DateTimeUtils.convert_month(4));
    assertEquals("June", DateTimeUtils.convert_month(5));
    assertEquals("July", DateTimeUtils.convert_month(6));
    assertEquals("August", DateTimeUtils.convert_month(7));
    assertEquals("September", DateTimeUtils.convert_month(8));
    assertEquals("October", DateTimeUtils.convert_month(9));
    assertEquals("November", DateTimeUtils.convert_month(10));
    assertEquals("December", DateTimeUtils.convert_month(11));
  }

  @Test
  public void returnsEmptyStringForAnInvalidMonthNumber() {
    assertEquals("", DateTimeUtils.convert_month(-1));
    assertEquals("", DateTimeUtils.convert_month(12));
  }

  @Test
  public void convertsTwoDigitMonthsToTheirAbbreviations() {
    assertEquals("Jan", DateTimeUtils.shortMonth("01"));
    assertEquals("Nov", DateTimeUtils.shortMonth("11"));
    assertEquals("Dec", DateTimeUtils.shortMonth("12"));
    assertEquals(null, DateTimeUtils.shortMonth("00"));
  }

  @Test
  public void preservesLegacyMapMonthFormatting() {
    assertEquals("nov", DateTimeUtils.shortMonthOrOriginal("11"));
    assertEquals("unknown", DateTimeUtils.shortMonthOrOriginal("unknown"));
  }
}
