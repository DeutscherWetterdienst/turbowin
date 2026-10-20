package turbowin;

import com.fazecast.jSerialComm.SerialPort;

/** Applies the shared serial-port configuration used before instrument commands are sent. */
final class SerialPortConfigurationWorkflow {

  private SerialPortConfigurationWorkflow() {}

  static void configure(
      SerialPort port, int bitsPerSecond, int dataBits, int stopBits, int parity) {
    port.setComPortParameters(bitsPerSecond, dataBits, stopBits, parity);
    port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
  }

  static String parityDescription(int parity) {
    switch (parity) {
      case 0:
        return "none";
      case 1:
        return "odd";
      case 2:
        return "even";
      default:
        return "";
    }
  }
}
