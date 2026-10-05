package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VisibilityPresentWeatherValidationTest {

  @Test
  public void acceptsFogAtTheHalfNauticalMileBoundary() {
    assertTrue(validate(true, 42, true, 94));
  }

  @Test
  public void acceptsFogWhenWeatherConversionFails() {
    assertTrue(validate(false, 42, true, 99));
  }

  @Test
  public void acceptsWeatherFortyAtTheHalfNauticalMileBoundary() {
    assertTrue(validate(true, 40, true, 94));
  }

  @Test
  public void acceptsUnrelatedWeatherAndVisibilityCodes() {
    assertTrue(validate(true, 1, true, 99));
  }

  private boolean validate(
      boolean weatherCodeValid, int weatherCode, boolean visibilityCodeValid, int visibilityCode) {
    return VisibilityPresentWeatherValidation.validate(
        weatherCodeValid, weatherCode, visibilityCodeValid, visibilityCode, "fog");
  }
}
