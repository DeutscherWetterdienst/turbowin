package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IceAirTemperatureValidationTest {

  @Test
  public void acceptsIceAtTheTwentyFiveDegreeBoundary() {
    assertTrue(IceAirTemperatureValidation.validate(true, 25.0f, true));
  }

  @Test
  public void acceptsIceWhenTemperatureConversionFails() {
    assertTrue(IceAirTemperatureValidation.validate(false, 25.1f, true));
  }

  @Test
  public void acceptsIceAtTheUpperSentinelBoundary() {
    assertTrue(IceAirTemperatureValidation.validate(true, 99.9f, true));
  }

  @Test
  public void acceptsAnEmptyIceObservationAtHighTemperature() {
    assertTrue(IceAirTemperatureValidation.validate(true, 25.1f, false));
  }
}
