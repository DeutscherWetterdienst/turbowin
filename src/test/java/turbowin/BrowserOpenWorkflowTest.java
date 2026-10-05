package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BrowserOpenWorkflowTest {

  @Test
  public void recognizesTheLegacyHttpAddressForms() {
    assertTrue(DesktopUtils.isWebAddress("https://example.org/help.pdf"));
    assertTrue(DesktopUtils.isWebAddress("HTTP://example.org/help.pdf"));
    assertFalse(DesktopUtils.isWebAddress("/tmp/help.pdf"));
  }
}
