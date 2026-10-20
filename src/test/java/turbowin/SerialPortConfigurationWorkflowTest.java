package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SerialPortConfigurationWorkflowTest {

  @Test
  public void mapsSupportedParityValues() {
    assertEquals("none", SerialPortConfigurationWorkflow.parityDescription(0));
    assertEquals("odd", SerialPortConfigurationWorkflow.parityDescription(1));
    assertEquals("even", SerialPortConfigurationWorkflow.parityDescription(2));
  }

  @Test
  public void leavesUnknownParityValuesWithoutADescription() {
    assertEquals("", SerialPortConfigurationWorkflow.parityDescription(99));
  }
}
