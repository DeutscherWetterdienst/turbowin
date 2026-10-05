package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IcingAirTemperatureValidationTest {

  @Test
  public void acceptsIcingAtTheTwentyDegreeBoundary() {
    assertTrue(IcingAirTemperatureValidation.validate(true, 20.0f, true));
  }

  @Test
  public void acceptsIcingWhenTemperatureConversionFails() {
    assertTrue(IcingAirTemperatureValidation.validate(false, 20.1f, true));
  }

  @Test
  public void acceptsIcingAtTheUpperSentinelBoundary() {
    assertTrue(IcingAirTemperatureValidation.validate(true, 99.9f, true));
  }

  @Test
  public void acceptsAnEmptyIcingObservationAtHighTemperature() {
    assertTrue(IcingAirTemperatureValidation.validate(true, 20.1f, false));
  }
}
