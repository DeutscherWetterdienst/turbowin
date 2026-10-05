package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LevelTwoPressureValidationTest {

  @Test
  public void acceptsNonSteadyCharacteristics() {
    assertTrue(LevelTwoPressureValidation.validate(true, "3", "", 25.0f));
  }

  @Test
  public void acceptsAmountAtTheStrictBoundaries() {
    assertTrue(LevelTwoPressureValidation.validate(true, "4", "0.01", 0.01f));
    assertTrue(LevelTwoPressureValidation.validate(true, "4", "50", 50.0f));
  }

  @Test
  public void acceptsWhenTendencyConversionFails() {
    assertTrue(LevelTwoPressureValidation.validate(false, "4", "", 25.0f));
  }
}
