package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SystemTrayWorkflowTest {

  @Test
  public void usesSystemTrayOnWindows() {
    assertTrue(SystemTrayWorkflow.usesSystemTray(OSDetector.OSType.WINDOWS));
  }

  @Test
  public void doesNotUseSystemTrayOnNonWindowsSystems() {
    assertFalse(SystemTrayWorkflow.usesSystemTray(OSDetector.OSType.LINUX));
    assertFalse(SystemTrayWorkflow.usesSystemTray(OSDetector.OSType.MACOS));
    assertFalse(SystemTrayWorkflow.usesSystemTray(OSDetector.OSType.OTHER));
  }
}
