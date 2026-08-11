package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class BarometerFieldsUpdaterTest {

  @Test
  public void formatsCorrectedPressureValues() {
    main.jTextField9 = new JTextField();
    main.jTextField10 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    mybarometer.pressure_reading_corrected = "1012.3";
    mybarometer.pressure_msl_corrected = "1015.8";

    BarometerFieldsUpdater.update();

    assertEquals("1012.3 hPa", main.jTextField9.getText());
    assertEquals("1015.8 hPa", main.jTextField10.getText());
  }

  @Test
  public void clearsEmptyPressureValues() {
    main.jTextField9 = new JTextField();
    main.jTextField10 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    mybarometer.pressure_reading_corrected = "";
    mybarometer.pressure_msl_corrected = "";

    BarometerFieldsUpdater.update();

    assertEquals("", main.jTextField9.getText());
    assertEquals("", main.jTextField10.getText());
  }
}
