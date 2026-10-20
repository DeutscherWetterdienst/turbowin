package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class SerialPortResetWorkflowTest {

  @Test
  public void formatsStatusMessage() {
    assertEquals(
        "[GPS] starting-com-port closed (COM-test)",
        SerialPortResetWorkflow.statusMessage("GPS", "closed", "COM-test"));
  }

  @Test
  public void resetsOpenPortAndClearsDefaultPortAfterCloseAndLogs() {
    List<String> events = new ArrayList<>();
    FakePort port = new FakePort(true, events);

    SerialPortResetWorkflow.reset(
        port, "GPS", () -> events.add("clear default port"), message -> events.add("log: " + message));

    assertFalse(port.isOpen());
    assertEquals(
        Arrays.asList(
            "log: [GPS] starting-com-port still open (COM-test)",
            "close port",
            "log: [GPS] starting-com-port now closed (COM-test)",
            "clear default port"),
        events);
  }

  @Test
  public void logsClosedPortAndClearsDefaultPortWithoutClosing() {
    List<String> events = new ArrayList<>();
    FakePort port = new FakePort(false, events);

    SerialPortResetWorkflow.reset(
        port, "GPS", () -> events.add("clear default port"), message -> events.add("log: " + message));

    assertFalse(port.isOpen());
    assertEquals(
        Arrays.asList(
            "log: [GPS] starting-com-port closed (COM-test)", "clear default port"),
        events);
  }

  private static final class FakePort implements SerialPortResetWorkflow.Port {
    private boolean open;
    private final List<String> events;

    private FakePort(boolean open, List<String> events) {
      this.open = open;
      this.events = events;
    }

    @Override
    public boolean isOpen() {
      return open;
    }

    @Override
    public void closePort() {
      events.add("close port");
      open = false;
    }

    @Override
    public String getDescriptivePortName() {
      return "COM-test";
    }
  }
}
