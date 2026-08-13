package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AwsrToolbarWorkflowTest {

  @Test
  public void recognizesMissingAwsrSettings() {
    assertTrue(AwsrToolbarWorkflow.isBlank(""));
    assertFalse(AwsrToolbarWorkflow.isBlank("hourly"));
  }
}
