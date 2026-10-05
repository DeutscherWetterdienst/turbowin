package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BrowserOpenWorkflowTest {

  @Test
  public void recognizesTheLegacyHttpAddressForms() {
    assertTrue(BrowserOpenWorkflow.isWebAddress("https://example.org/help.pdf"));
    assertTrue(BrowserOpenWorkflow.isWebAddress("HTTP://example.org/help.pdf"));
    assertFalse(BrowserOpenWorkflow.isWebAddress("/tmp/help.pdf"));
  }
}
