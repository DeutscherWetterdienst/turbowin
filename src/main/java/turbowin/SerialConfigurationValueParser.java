package turbowin;

import com.fazecast.jSerialComm.SerialPort;

/** Converts legacy configuration strings into serial-port parameter values. */
final class SerialConfigurationValueParser {

  private SerialConfigurationValueParser() {}

  static int dataBits(String value) {
    return switch (value) {
      case "7" -> 7;
      case "8" -> 8;
      default -> 0; // non-existing data-bit value
    };
  }

  static int parity(String value) {
    return switch (value) {
      case "0" -> SerialPort.NO_PARITY;
      case "1" -> SerialPort.ODD_PARITY;
      case "2" -> SerialPort.EVEN_PARITY;
      default -> 99; // non-existing parity value
    };
  }

  static int stopBits(String value) {
    return switch (value) {
      case "1" -> SerialPort.ONE_STOP_BIT;
      case "2" -> SerialPort.TWO_STOP_BITS;
      default -> 0; // non-existing stop-bit value
    };
  }
}
