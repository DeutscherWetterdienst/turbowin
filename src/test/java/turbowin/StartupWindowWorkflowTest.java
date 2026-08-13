package turbowin;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import javax.swing.JTextField;
import org.junit.Test;

public class StartupWindowWorkflowTest {

  @Test
  public void configuresStatusField() {
    JTextField statusField = new JTextField();

    StartupWindowWorkflow.configureStatusField(statusField);

    assertEquals(new Color(204, 255, 255), statusField.getBackground());
    assertEquals("fm13_field", statusField.getName());
  }
}
