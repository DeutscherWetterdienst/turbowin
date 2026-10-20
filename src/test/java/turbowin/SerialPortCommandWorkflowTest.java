package turbowin;

import static org.junit.Assert.assertArrayEquals;

import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class SerialPortCommandWorkflowTest {

  @Test
  public void encodesCommandsAsUtf8() {
    assertArrayEquals(
        "測定\r".getBytes(StandardCharsets.UTF_8), SerialPortCommandWorkflow.encode("測定\r"));
  }
}
