package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class IceFieldsUpdaterTest {

  @Test
  public void showsPresentForMeaningfulIceCode() {
    prepareFields();
    myice1.ci_code = "1";
    myice1.Si_code = "";
    myice1.bi_code = "";
    myice1.Di_code = "";
    myice1.zi_code = "";

    IceFieldsUpdater.update();

    assertEquals("present", main.jTextField19.getText());
  }

  @Test
  public void suppressesPresentForAllUnableToReportCodes() {
    prepareFields();
    myice1.ci_code = "u";
    myice1.Si_code = "u";
    myice1.bi_code = "u";
    myice1.Di_code = "u";
    myice1.zi_code = "u";

    IceFieldsUpdater.update();

    assertEquals("", main.jTextField19.getText());
  }

  private static void prepareFields() {
    main.jTextField19 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
  }
}
