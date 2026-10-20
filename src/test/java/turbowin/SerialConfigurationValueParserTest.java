package turbowin;

import static org.junit.Assert.assertEquals;

import com.fazecast.jSerialComm.SerialPort;
import org.junit.Test;

public class SerialConfigurationValueParserTest {

  @Test
  public void parsesDataBits() {
    assertEquals(7, SerialConfigurationValueParser.dataBits("7"));
    assertEquals(8, SerialConfigurationValueParser.dataBits("8"));
    assertEquals(0, SerialConfigurationValueParser.dataBits("9"));
  }

  @Test
  public void parsesParity() {
    assertEquals(SerialPort.NO_PARITY, SerialConfigurationValueParser.parity("0"));
    assertEquals(SerialPort.ODD_PARITY, SerialConfigurationValueParser.parity("1"));
    assertEquals(SerialPort.EVEN_PARITY, SerialConfigurationValueParser.parity("2"));
    assertEquals(99, SerialConfigurationValueParser.parity("9"));
  }

  @Test
  public void parsesStopBits() {
    assertEquals(SerialPort.ONE_STOP_BIT, SerialConfigurationValueParser.stopBits("1"));
    assertEquals(SerialPort.TWO_STOP_BITS, SerialConfigurationValueParser.stopBits("2"));
    assertEquals(0, SerialConfigurationValueParser.stopBits("9"));
  }
}
