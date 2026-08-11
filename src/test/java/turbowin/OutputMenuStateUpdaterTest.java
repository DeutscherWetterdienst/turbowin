package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JMenuItem;
import org.junit.Test;

public class OutputMenuStateUpdaterTest {

  @Test
  public void enablesManualOutputsForConfiguredNonAwsMode() {
    prepareMenuItems();
    main.RS232_connection_mode = 0;
    main.obs_format = main.FORMAT_FM13;
    main.offline_mode = false;
    main.upload_URL = "";
    main.obs_email_recipient = "recipient@example.com";
    main.obs_email_subject = "observation";
    main.obs_101_email = "body";
    main.your_custom_address = "custom@example.com";
    main.custom_security = "TLS";
    main.custom_email_server = "smtp.example.com";
    main.custom_port = "587";
    main.APR = false;
    main.AWSR = false;

    OutputMenuStateUpdater.update();

    assertTrue(main.jMenuItem20.isEnabled());
    assertTrue(main.jMenuItem23.isEnabled());
    assertTrue(main.jMenuItem24.isEnabled());
    assertFalse(main.jMenuItem46.isEnabled());
    assertTrue(main.jMenuItem48.isEnabled());
    assertTrue(main.jMenuItem80.isEnabled());
  }

  @Test
  public void disablesManualOutputsForAwsMode() {
    prepareMenuItems();
    main.RS232_connection_mode = 3;
    main.eucaws_uploads_method = "";
    main.APR = false;
    main.AWSR = false;

    OutputMenuStateUpdater.update();

    assertTrue(main.jMenuItem20.isEnabled());
    assertFalse(main.jMenuItem23.isEnabled());
    assertFalse(main.jMenuItem24.isEnabled());
    assertTrue(main.jMenuItem46.isEnabled());
    assertFalse(main.jMenuItem48.isEnabled());
    assertFalse(main.jMenuItem80.isEnabled());
  }

  @Test
  public void disablesAllManualOutputsForAprMode() {
    prepareMenuItems();
    main.RS232_connection_mode = 0;
    main.obs_format = main.FORMAT_FM13;
    main.offline_mode = false;
    main.upload_URL = "";
    main.obs_email_recipient = "";
    main.obs_email_subject = "";
    main.your_custom_address = "";
    main.custom_security = "";
    main.custom_email_server = "";
    main.custom_port = "";
    main.APR = true;
    main.AWSR = false;

    OutputMenuStateUpdater.update();

    assertFalse(main.jMenuItem20.isEnabled());
    assertFalse(main.jMenuItem23.isEnabled());
    assertFalse(main.jMenuItem24.isEnabled());
    assertFalse(main.jMenuItem46.isEnabled());
    assertFalse(main.jMenuItem48.isEnabled());
    assertFalse(main.jMenuItem80.isEnabled());
  }

  private static void prepareMenuItems() {
    main.jMenuItem20 = new JMenuItem();
    main.jMenuItem23 = new JMenuItem();
    main.jMenuItem24 = new JMenuItem();
    main.jMenuItem46 = new JMenuItem();
    main.jMenuItem48 = new JMenuItem();
    main.jMenuItem80 = new JMenuItem();
  }
}
