package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ShipSpeedConfirmationValidationTest {

  @Test
  public void acceptsNonExtremeShipSpeedCodes() {
    assertTrue(ShipSpeedConfirmationValidation.validate("0"));
    assertTrue(ShipSpeedConfirmationValidation.validate("7"));
  }

  @Test
  public void acceptsEmptyShipSpeedCode() {
    assertTrue(ShipSpeedConfirmationValidation.validate(""));
  }
}
