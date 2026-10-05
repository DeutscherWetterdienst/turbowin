package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WindWaveConfirmationValidationTest {

  @Test
  public void acceptsPeriodAtTheThreshold() {
    assertTrue(WindWaveConfirmationValidation.validate(true, 25.0f, false, 12.3f));
  }

  @Test
  public void acceptsHeightAtTheThreshold() {
    assertTrue(WindWaveConfirmationValidation.validate(false, 25.1f, true, 12.2f));
  }

  @Test
  public void acceptsMeasurementsAtTheUpperSentinelBoundary() {
    assertTrue(WindWaveConfirmationValidation.validate(true, 99.9f, true, 99.9f));
  }

  @Test
  public void acceptsMeasurementsWhenConversionFails() {
    assertTrue(WindWaveConfirmationValidation.validate(false, 30.0f, false, 13.0f));
  }
}
