package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PressureCodeFormatterTest {

  @Test
  public void roundsAndPadsPressureCode() {
    assertEquals("0132", PressureCodeFormatter.format(1013.2));
    assertEquals("0007", PressureCodeFormatter.format(1000.7));
    assertEquals("9998", PressureCodeFormatter.format(999.8));
  }

  @Test
  public void removesOnlyTheLeadingThousandsDigit() {
    assertEquals("0000", PressureCodeFormatter.format(1000.0));
    assertEquals("9999", PressureCodeFormatter.format(1999.9));
  }
}
