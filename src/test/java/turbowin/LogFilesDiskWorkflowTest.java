package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LogFilesDiskWorkflowTest {

  @Test
  public void usesDiskMoveMode() {
    assertEquals(main.MOVE_TO_DISK, LogFilesDiskWorkflow.moveMode());
  }
}
