package turbowin;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class StartupCommunicationWorkflowTest {

  @Test
  public void createsCommunicationComponents() {
    StartupCommunicationWorkflow.CommunicationComponents components =
        StartupCommunicationWorkflow.initialize();

    assertNotNull(components.serial);
    assertNotNull(components.mintaka);
    assertNotNull(components.vaisala);
  }
}
