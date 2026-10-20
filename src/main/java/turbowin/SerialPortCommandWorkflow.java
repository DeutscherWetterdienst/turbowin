package turbowin;

import com.fazecast.jSerialComm.SerialPort;
import java.nio.charset.StandardCharsets;

/** Writes UTF-8 commands to a configured serial port. */
final class SerialPortCommandWorkflow {

  private SerialPortCommandWorkflow() {}

  static int write(SerialPort port, String command) {
    byte[] bytes = encode(command);
    return port.writeBytes(bytes, bytes.length);
  }

  static byte[] encode(String command) {
    return command.getBytes(StandardCharsets.UTF_8);
  }
}
