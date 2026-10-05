package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SwellConfirmationValidationTest {

  @Test
  public void acceptsPeriodsAndHeightsAtTheirThresholds() {
    assertTrue(
        SwellConfirmationValidation.validate(true, 25.0f, true, 12.2f, true, 25.0f, true, 12.2f));
  }

  @Test
  public void acceptsMeasurementsAtTheUpperSentinelBoundary() {
    assertTrue(
        SwellConfirmationValidation.validate(true, 99.9f, true, 99.9f, true, 99.9f, true, 99.9f));
  }

  @Test
  public void acceptsMeasurementsWhenConversionFails() {
    assertTrue(
        SwellConfirmationValidation.validate(
            false, 30.0f, false, 13.0f, false, 30.0f, false, 13.0f));
  }
}
