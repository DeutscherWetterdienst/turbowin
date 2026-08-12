package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ApplicationShutdownWorkflowTest {

  @Test
  public void detectsActiveConnections() {
    assertTrue(ApplicationShutdownWorkflow.hasActiveConnections(1, "COM1", 0, null));
    assertTrue(ApplicationShutdownWorkflow.hasActiveConnections(6, null, 0, null));
    assertTrue(ApplicationShutdownWorkflow.hasActiveConnections(0, null, 1, "COM2"));
    assertFalse(ApplicationShutdownWorkflow.hasActiveConnections(0, null, 0, null));
  }

  @Test
  public void buildsBasicExitMessageWithoutActiveConnections() {
    assertEquals(
        "Are you sure you want to exit this application?",
        ApplicationShutdownWorkflow.buildExitMessage("TurboWin+", false, false, false));
  }

  @Test
  public void includesMonitoringAndUploadWarningsWhenNeeded() {
    String message = ApplicationShutdownWorkflow.buildExitMessage("TurboWin+", true, true, false);

    assertTrue(message.contains("will stop with monitoring and collecting"));
    assertTrue(message.contains("will stop with automated reports upload"));
    assertTrue(message.contains("minimise TurboWin+ instead of closing"));
    assertTrue(message.contains("\n\n ("));
    assertFalse(message.contains("\\n"));
  }
}
