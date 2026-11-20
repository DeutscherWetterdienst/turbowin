package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class IceFieldsUpdaterTest {

  @Test
  public void showsPresentForMeaningfulIceCode() {
    prepareFields();
    IceState.ci_code = "1";
    IceState.Si_code = "";
    IceState.bi_code = "";
    IceState.Di_code = "";
    IceState.zi_code = "";

    IceFieldsUpdater.update();

    assertEquals("present", main.jTextField19.getText());
  }

  @Test
  public void suppressesPresentForAllUnableToReportCodes() {
    prepareFields();
    IceState.ci_code = "u";
    IceState.Si_code = "u";
    IceState.bi_code = "u";
    IceState.Di_code = "u";
    IceState.zi_code = "u";

    IceFieldsUpdater.update();

    assertEquals("", main.jTextField19.getText());
  }

  private static void prepareFields() {
    main.jTextField19 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
  }
}
