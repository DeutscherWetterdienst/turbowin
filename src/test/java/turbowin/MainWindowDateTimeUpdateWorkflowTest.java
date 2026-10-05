package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MainWindowDateTimeUpdateWorkflowTest {

  @Test
  public void showsNoticeForSupportedConnectionsOrApr() {
    assertTrue(MainWindowDateTimeUpdateWorkflow.shouldShowUpdateNotice(3, false));
    assertTrue(MainWindowDateTimeUpdateWorkflow.shouldShowUpdateNotice(9, false));
    assertTrue(MainWindowDateTimeUpdateWorkflow.shouldShowUpdateNotice(10, false));
    assertTrue(MainWindowDateTimeUpdateWorkflow.shouldShowUpdateNotice(11, false));
    assertTrue(MainWindowDateTimeUpdateWorkflow.shouldShowUpdateNotice(0, true));
  }

  @Test
  public void doesNotShowNoticeForOtherDisconnectedModes() {
    assertFalse(MainWindowDateTimeUpdateWorkflow.shouldShowUpdateNotice(0, false));
    assertFalse(MainWindowDateTimeUpdateWorkflow.shouldShowUpdateNotice(1, false));
    assertFalse(MainWindowDateTimeUpdateWorkflow.shouldShowUpdateNotice(2, false));
  }
}
