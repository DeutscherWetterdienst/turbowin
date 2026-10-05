package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AirPressureConfirmationValidationTest {

  @Test
  public void acceptsTheLowerPressureBoundary() {
    assertTrue(AirPressureConfirmationValidation.validate(true, 910.0f));
  }

  @Test
  public void acceptsTheUpperPressureBoundary() {
    assertTrue(AirPressureConfirmationValidation.validate(true, 1050.0f));
  }

  @Test
  public void acceptsWhenPressureConversionFails() {
    assertTrue(AirPressureConfirmationValidation.validate(false, 800.0f));
  }

  @Test
  public void acceptsPressureInsideTheConfiguredRange() {
    assertTrue(AirPressureConfirmationValidation.validate(true, 950.0f));
  }
}
