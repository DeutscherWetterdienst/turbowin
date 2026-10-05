package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WindSpeedConfirmationValidationTest {

  @Test
  public void acceptsMetersPerSecondAtTheThreshold() {
    assertTrue(WindSpeedConfirmationValidation.validate(main.M_S, 28));
  }

  @Test
  public void acceptsKnotsAtTheThreshold() {
    assertTrue(WindSpeedConfirmationValidation.validate(main.KNOTS, 55));
  }

  @Test
  public void acceptsTheUpperSentinelBoundary() {
    assertTrue(WindSpeedConfirmationValidation.validate(main.M_S, 500));
  }

  @Test
  public void acceptsAnUnrelatedUnit() {
    assertTrue(WindSpeedConfirmationValidation.validate("unknown", 100));
  }
}
