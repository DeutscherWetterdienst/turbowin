package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AprToolbarWorkflowTest {

  @Test
  public void recognizesMissingSettings() {
    assertTrue(AprToolbarWorkflow.isBlank(""));
    assertFalse(AprToolbarWorkflow.isBlank("hourly"));
  }

  @Test
  public void validatesDraughtRange() {
    assertTrue(AprToolbarWorkflow.isValidDraught("0"));
    assertTrue(AprToolbarWorkflow.isValidDraught("50"));
    assertFalse(AprToolbarWorkflow.isValidDraught("-0.1"));
    assertFalse(AprToolbarWorkflow.isValidDraught("50.1"));
    assertFalse(AprToolbarWorkflow.isValidDraught("invalid"));
  }

  @Test
  public void preservesBarometerCorrectionValidationSemantics() {
    assertTrue(AprToolbarWorkflow.isValidBarometerCorrection("-4"));
    assertTrue(AprToolbarWorkflow.isValidBarometerCorrection("4"));
    assertTrue(AprToolbarWorkflow.isValidBarometerCorrection("100"));
    assertFalse(AprToolbarWorkflow.isValidBarometerCorrection("invalid"));
  }
}
