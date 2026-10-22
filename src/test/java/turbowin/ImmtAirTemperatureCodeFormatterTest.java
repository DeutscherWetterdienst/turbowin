package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImmtAirTemperatureCodeFormatterTest {

  @Test
  public void formatsNegativeZeroAndPositiveTemperatures() {
    assertEquals("1", ImmtAirTemperatureCodeFormatter.format(-2.6).signCode);
    assertEquals("026", ImmtAirTemperatureCodeFormatter.format(-2.6).temperatureCode);
    assertEquals("0", ImmtAirTemperatureCodeFormatter.format(0.0).signCode);
    assertEquals("000", ImmtAirTemperatureCodeFormatter.format(0.0).temperatureCode);
    assertEquals("0", ImmtAirTemperatureCodeFormatter.format(18.34).signCode);
    assertEquals("183", ImmtAirTemperatureCodeFormatter.format(18.34).temperatureCode);
  }
}
