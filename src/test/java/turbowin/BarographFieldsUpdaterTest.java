package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class BarographFieldsUpdaterTest {

  @Test
  public void formatsPressureTendencyAndCharacteristicCode() {
    main.jTextField11 = new JTextField();
    main.jTextField12 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    mybarograph.pressure_amount_tendency = "5";
    mybarograph.a_code = "2";

    BarographFieldsUpdater.update();

    assertEquals("5 hPa", main.jTextField11.getText());
    assertEquals("2 (code)", main.jTextField12.getText());
  }

  @Test
  public void clearsEmptyPressureTendencyValues() {
    main.jTextField11 = new JTextField();
    main.jTextField12 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";

    BarographFieldsUpdater.update();

    assertEquals("", main.jTextField11.getText());
    assertEquals("", main.jTextField12.getText());
  }
}
