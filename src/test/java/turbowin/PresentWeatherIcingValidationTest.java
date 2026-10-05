package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PresentWeatherIcingValidationTest {

  @Test
  public void acceptsInvalidWeatherConversionWithoutShowingAConfirmation() {
    assertTrue(PresentWeatherIcingValidation.validate(false, 48, true));
  }

  @Test
  public void acceptsPresentWeatherWithoutMissingIcingObservation() {
    assertTrue(PresentWeatherIcingValidation.validate(true, 48, false));
  }

  @Test
  public void acceptsUnrelatedPresentWeatherWithMissingIcingObservation() {
    assertTrue(PresentWeatherIcingValidation.validate(true, 1, true));
  }
}
