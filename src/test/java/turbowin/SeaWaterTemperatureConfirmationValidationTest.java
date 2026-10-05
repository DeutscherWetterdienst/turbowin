package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SeaWaterTemperatureConfirmationValidationTest {

  @Test
  public void acceptsTemperatureAtTheThreshold() {
    assertTrue(SeaWaterTemperatureConfirmationValidation.validate(true, 35.0f));
  }

  @Test
  public void acceptsWhenTemperatureConversionFails() {
    assertTrue(SeaWaterTemperatureConfirmationValidation.validate(false, 36.0f));
  }

  @Test
  public void acceptsTemperatureBelowTheThreshold() {
    assertTrue(SeaWaterTemperatureConfirmationValidation.validate(true, 20.0f));
  }
}
