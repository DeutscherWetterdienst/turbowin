package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PressureTendencyCodeFormatterTest {

  @Test
  public void roundsAndPadsPressureTendency() {
    assertEquals("017", PressureTendencyCodeFormatter.format(1.7));
    assertEquals("000", PressureTendencyCodeFormatter.format(0.0));
    assertEquals("999", PressureTendencyCodeFormatter.format(99.9));
  }
}
