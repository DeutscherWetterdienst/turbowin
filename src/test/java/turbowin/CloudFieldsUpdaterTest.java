package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class CloudFieldsUpdaterTest {

  @Test
  public void formatsCloudCodesAndCoverValues() {
    prepareFields();
    mycl.cl_code = "5";
    mycm.cm_code = "7a";
    mych.ch_code = "3";
    mycloudcover.N = "6";
    mycloudcover.Nh = "2";
    mycloudcover.h = "4";

    CloudFieldsUpdater.updateLow();
    CloudFieldsUpdater.updateMiddle();
    CloudFieldsUpdater.updateHigh();
    CloudFieldsUpdater.updateCover();

    assertEquals("5 (code)", main.jTextField33.getText());
    assertEquals("7 (code)", main.jTextField34.getText());
    assertEquals("3 (code)", main.jTextField35.getText());
    assertEquals("6", main.jTextField30.getText());
    assertEquals("2", main.jTextField31.getText());
    assertEquals("4", main.jTextField32.getText());
  }

  @Test
  public void clearsEmptyCloudValues() {
    prepareFields();
    mycl.cl_code = "";
    mycm.cm_code = "";
    mych.ch_code = "";
    mycloudcover.N = "";
    mycloudcover.Nh = "";
    mycloudcover.h = "";

    CloudFieldsUpdater.updateLow();
    CloudFieldsUpdater.updateMiddle();
    CloudFieldsUpdater.updateHigh();
    CloudFieldsUpdater.updateCover();

    assertEquals("", main.jTextField33.getText());
    assertEquals("", main.jTextField34.getText());
    assertEquals("", main.jTextField35.getText());
    assertEquals("", main.jTextField30.getText());
    assertEquals("", main.jTextField31.getText());
    assertEquals("", main.jTextField32.getText());
  }

  private static void prepareFields() {
    main.jTextField30 = new JTextField();
    main.jTextField31 = new JTextField();
    main.jTextField32 = new JTextField();
    main.jTextField33 = new JTextField();
    main.jTextField34 = new JTextField();
    main.jTextField35 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
  }
}
