package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ThemeWorkflowTest {

  @Test
  public void resetsMainWindowWhenLeavingTransparentTheme() {
    assertTrue(ThemeWorkflow.requiresMainReset(main.THEME_TRANSPARENT));
  }

  @Test
  public void doesNotResetMainWindowForNimbusThemes() {
    assertFalse(ThemeWorkflow.requiresMainReset(main.THEME_NIMBUS_DAY));
  }
}
