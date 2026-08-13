package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApplicationStartupWorkflowTest {

  @Test
  public void selectsOfflineAndWebApplicationModes() {
    assertEquals("", ApplicationStartupWorkflow.applicationMode(true));
    assertEquals("web mode", ApplicationStartupWorkflow.applicationMode(false));
  }
}
