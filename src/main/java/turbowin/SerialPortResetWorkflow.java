package turbowin;

import com.fazecast.jSerialComm.SerialPort;
import java.util.function.Consumer;

/**
 * Resets a serial port and clears the remembered default-port values.
 *
 * <p>The port is closed so its listener/workflow can reopen it after a power break or temporary
 * device failure; the stale startup/default-port selection is then discarded.
 */
final class SerialPortResetWorkflow {

  private SerialPortResetWorkflow() {}

  interface Port {
    boolean isOpen();

    void closePort();

    String getDescriptivePortName();
  }

  static void reset(SerialPort port, String instrument, Runnable clearDefaultPort) {
    reset(
        new Port() {
          @Override
          public boolean isOpen() {
            return port.isOpen();
          }

          @Override
          public void closePort() {
            port.closePort();
          }

          @Override
          public String getDescriptivePortName() {
            return port.getDescriptivePortName();
          }
        },
        instrument,
        clearDefaultPort);
  }

  static void reset(Port port, String instrument, Runnable clearDefaultPort) {
    reset(port, instrument, clearDefaultPort, main::log_turbowin_system_message);
  }

  static void reset(
      Port port, String instrument, Runnable clearDefaultPort, Consumer<String> logMessage) {
    if (port.isOpen()) {
      logMessage.accept(statusMessage(instrument, "still open", port.getDescriptivePortName()));
      port.closePort();
      logMessage.accept(statusMessage(instrument, "now closed", port.getDescriptivePortName()));
    } else {
      logMessage.accept(statusMessage(instrument, "closed", port.getDescriptivePortName()));
    }
    clearDefaultPort.run();
  }

  static String statusMessage(String instrument, String status, String descriptivePortName) {
    return "[" + instrument + "] starting-com-port " + status + " (" + descriptivePortName + ")";
  }
}
