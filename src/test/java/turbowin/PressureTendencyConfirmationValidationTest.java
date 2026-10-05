package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PressureTendencyConfirmationValidationTest {

  @Test
  public void acceptsAmountAtTheStrictLowerBoundary() {
    assertTrue(PressureTendencyConfirmationValidation.validateAmount(true, 30.0f));
  }

  @Test
  public void acceptsAmountAtTheInclusiveUpperBoundary() {
    assertTrue(PressureTendencyConfirmationValidation.validateAmount(true, 99.9f));
  }

  @Test
  public void acceptsCharacteristicWhenAmountIsBelowTheUpperRange() {
    assertTrue(PressureTendencyConfirmationValidation.validateCharacteristic(true, 0.0f, false));
  }

  @Test
  public void acceptsWhenAmountConversionFails() {
    assertTrue(PressureTendencyConfirmationValidation.validateAmount(false, 50.0f));
    assertTrue(PressureTendencyConfirmationValidation.validateCharacteristic(false, 50.0f, true));
  }
}
