package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class IcingFieldsUpdaterTest {

  @Test
  public void showsPresentWhenAnIcingCodeExists() {
    main.jTextField21 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    myicing.Is_code = "1";
    myicing.EsEs_code = "";
    myicing.Rs_code = "";

    IcingFieldsUpdater.update();

    assertEquals("present", main.jTextField21.getText());
  }

  @Test
  public void clearsTheIcingIndicatorWhenCodesAreEmpty() {
    main.jTextField21 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    myicing.Is_code = "";
    myicing.EsEs_code = "";
    myicing.Rs_code = "";

    IcingFieldsUpdater.update();

    assertEquals("", main.jTextField21.getText());
  }
}
