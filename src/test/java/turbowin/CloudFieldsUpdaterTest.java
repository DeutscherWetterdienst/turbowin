package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CloudFieldsUpdaterTest {

  private final JTextField[] originalFields = new JTextField[7];
  private final String[] originalCloudValues = new String[6];
  private int originalConnectionMode;

  @Before
  public void saveState() {
    originalFields[0] = main.jTextField30;
    originalFields[1] = main.jTextField31;
    originalFields[2] = main.jTextField32;
    originalFields[3] = main.jTextField33;
    originalFields[4] = main.jTextField34;
    originalFields[5] = main.jTextField35;
    originalFields[6] = main.jTextField4;
    originalCloudValues[0] = LowCloudState.cl_code;
    originalCloudValues[1] = MiddleCloudState.cm_code;
    originalCloudValues[2] = HighCloudState.ch_code;
    originalCloudValues[3] = CloudCoverState.N;
    originalCloudValues[4] = CloudCoverState.Nh;
    originalCloudValues[5] = CloudCoverState.h;
    originalConnectionMode = main.RS232_connection_mode;
  }

  @After
  public void restoreState() {
    main.jTextField30 = originalFields[0];
    main.jTextField31 = originalFields[1];
    main.jTextField32 = originalFields[2];
    main.jTextField33 = originalFields[3];
    main.jTextField34 = originalFields[4];
    main.jTextField35 = originalFields[5];
    main.jTextField4 = originalFields[6];
    LowCloudState.cl_code = originalCloudValues[0];
    MiddleCloudState.cm_code = originalCloudValues[1];
    HighCloudState.ch_code = originalCloudValues[2];
    CloudCoverState.N = originalCloudValues[3];
    CloudCoverState.Nh = originalCloudValues[4];
    CloudCoverState.h = originalCloudValues[5];
    main.RS232_connection_mode = originalConnectionMode;
  }

  @Test
  public void formatsCloudCodesAndCoverValues() {
    prepareFields();
    LowCloudState.cl_code = "5";
    MiddleCloudState.cm_code = "7a";
    HighCloudState.ch_code = "3";
    CloudCoverState.N = "6";
    CloudCoverState.Nh = "2";
    CloudCoverState.h = "4";

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
    LowCloudState.cl_code = "";
    MiddleCloudState.cm_code = "";
    HighCloudState.ch_code = "";
    CloudCoverState.N = "";
    CloudCoverState.Nh = "";
    CloudCoverState.h = "";

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
