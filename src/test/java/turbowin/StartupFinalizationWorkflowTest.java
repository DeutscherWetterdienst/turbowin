package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StartupFinalizationWorkflowTest {

  @Test
  public void createsInitialStartupMessage() {
    assertTrue(StartupFinalizationWorkflow.startupMessage(false).startsWith("[GENERAL] started "));
  }

  @Test
  public void createsThemeRestartMessage() {
    assertTrue(
        StartupFinalizationWorkflow.startupMessage(true)
            .startsWith("[GENERAL] restarted main module (Theme changed)"));
  }
}
