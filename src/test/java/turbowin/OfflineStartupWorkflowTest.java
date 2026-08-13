package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OfflineStartupWorkflowTest {

  @Test
  public void resolvesConfiguredAndDefaultPorts() {
    assertEquals(12345, OfflineStartupWorkflow.resolvePort("", 12345));
    assertEquals(23456, OfflineStartupWorkflow.resolvePort("23456", 12345));
    assertEquals(12345, OfflineStartupWorkflow.resolvePort("invalid", 12345));
  }
}
