package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IceThicknessConfirmationValidationTest {

  @Test
  public void acceptsThicknessAtTheThreshold() {
    assertTrue(IceThicknessConfirmationValidation.validate(true, 20.0f));
  }

  @Test
  public void acceptsWhenThicknessConversionFails() {
    assertTrue(IceThicknessConfirmationValidation.validate(false, 25.0f));
  }

  @Test
  public void acceptsNegativeThicknessWithoutConfirmation() {
    assertTrue(IceThicknessConfirmationValidation.validate(true, -1.0f));
  }
}
