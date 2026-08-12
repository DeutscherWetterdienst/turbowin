package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogFilesEmailWorkflowTest {

  private String originalRecipient;
  private String originalAddress;
  private String originalSecurity;
  private String originalServer;
  private String originalPort;

  @Before
  public void saveState() {
    originalRecipient = main.logs_email_recipient;
    originalAddress = main.your_custom_address;
    originalSecurity = main.custom_security;
    originalServer = main.custom_email_server;
    originalPort = main.custom_port;
  }

  @After
  public void restoreState() {
    main.logs_email_recipient = originalRecipient;
    main.your_custom_address = originalAddress;
    main.custom_security = originalSecurity;
    main.custom_email_server = originalServer;
    main.custom_port = originalPort;
  }

  @Test
  public void recognizesCompleteCustomEmailSettings() {
    main.logs_email_recipient = "logs@example.com";
    main.your_custom_address = "ship@example.com";
    main.custom_security = "TLS";
    main.custom_email_server = "smtp.example.com";
    main.custom_port = "587";

    assertTrue(LogFilesEmailWorkflow.customEmailSettingsComplete());
  }

  @Test
  public void rejectsIncompleteCustomEmailSettings() {
    main.logs_email_recipient = "logs@example.com";
    main.your_custom_address = "ship@example.com";
    main.custom_security = "TLS";
    main.custom_email_server = "";
    main.custom_port = "587";

    assertFalse(LogFilesEmailWorkflow.customEmailSettingsComplete());
  }
}
