package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StartupEnvironmentWorkflowTest {

  @Test
  public void createsOfflineAndOnlineApplicationLabels() {
    assertEquals("TurboWin+ ", StartupEnvironmentWorkflow.applicationLabel("TurboWin+", true));
    assertEquals(
        "TurboWin+ web mode", StartupEnvironmentWorkflow.applicationLabel("TurboWin+", false));
  }
}
