package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AirTemperatureConfirmationValidationTest {

  @Test
  public void acceptsTheUpperTemperatureThreshold() {
    assertTrue(AirTemperatureConfirmationValidation.validate(true, 50.0f));
  }

  @Test
  public void acceptsTheLowerTemperatureThreshold() {
    assertTrue(AirTemperatureConfirmationValidation.validate(true, -20.0f));
  }

  @Test
  public void acceptsTheSentinelBoundaries() {
    assertTrue(AirTemperatureConfirmationValidation.validate(true, 99.9f));
    assertTrue(AirTemperatureConfirmationValidation.validate(true, -99.9f));
  }

  @Test
  public void acceptsWhenTemperatureConversionFails() {
    assertTrue(AirTemperatureConfirmationValidation.validate(false, 60.0f));
  }
}
