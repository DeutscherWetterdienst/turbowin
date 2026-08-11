package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class VisibilityFieldsUpdaterTest {

  @Test
  public void displaysVisibilityValue() {
    prepareFields();
    myvisibility.VV = "9999";

    VisibilityFieldsUpdater.update();

    assertEquals("9999", main.jTextField18.getText());
  }

  @Test
  public void clearsEmptyVisibilityValue() {
    prepareFields();
    myvisibility.VV = "";

    VisibilityFieldsUpdater.update();

    assertEquals("", main.jTextField18.getText());
  }

  private static void prepareFields() {
    main.jTextField18 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
  }
}
