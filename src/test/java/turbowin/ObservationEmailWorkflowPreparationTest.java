package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ObservationEmailWorkflowPreparationTest {

  @Test
  public void invalidModeProducesInvalidStatusWithoutARequest() {
    String originalMode = main.email_send_mode;
    String originalAutomaticMode = main.APTR_AWSR_send_method;
    try {
      main.email_send_mode = "unknown";
      main.APTR_AWSR_send_method = "unknown";

      ObservationEmailWorkflowPreparation.Result result =
          ObservationEmailWorkflowPreparation.prepare(true);

      assertEquals(1002, result.status());
      assertNull(result.request());
    } finally {
      main.email_send_mode = originalMode;
      main.APTR_AWSR_send_method = originalAutomaticMode;
    }
  }

  @Test
  public void preparesCustomFm13Request() {
    String originalMode = main.email_send_mode;
    String originalAutomaticMode = main.APTR_AWSR_send_method;
    String originalFormat = main.obs_format;
    String originalBody = main.obs_write;
    String originalSubject = main.obs_email_subject;
    String originalRecipient = main.obs_email_recipient;
    String originalSender = main.your_custom_address;
    String originalSecurity = main.custom_security;
    String originalHost = main.custom_email_server;
    String originalPassword = main.custom_password;
    String originalPort = main.custom_port;
    String originalCc = main.obs_email_cc;
    String originalUploads = main.eucaws_uploads_method;
    String originalAttachmentMode = main.obs_101_email;
    String originalYear = mydatetime.YY_code;
    String originalGroup = mydatetime.GG_code;
    try {
      main.email_send_mode = main.EMAIL_SEND_CUSTOM;
      main.APTR_AWSR_send_method = "unknown";
      main.obs_format = main.FORMAT_FM13;
      main.obs_write = "FM13 body";
      main.obs_email_subject = "Observation ddhhmm";
      main.obs_email_recipient = "to@example.org";
      main.your_custom_address = "from@example.org";
      main.custom_security = "tls";
      main.custom_email_server = "smtp.example.org";
      main.custom_password = "null";
      main.custom_port = "587";
      main.obs_email_cc = "";
      main.eucaws_uploads_method = "";
      main.obs_101_email = main.FORMAT_101_BODY;
      mydatetime.YY_code = "24";
      mydatetime.GG_code = "12";

      ObservationEmailWorkflowPreparation.Result result =
          ObservationEmailWorkflowPreparation.prepare(true);

      assertEquals(0, result.status());
      assertEquals("Observation 241200", result.request().subject());
      assertEquals("FM13 body", result.request().body());
      assertEquals("to@example.org", result.request().recipient());
      assertEquals("null", result.request().password());
    } finally {
      main.email_send_mode = originalMode;
      main.APTR_AWSR_send_method = originalAutomaticMode;
      main.obs_format = originalFormat;
      main.obs_write = originalBody;
      main.obs_email_subject = originalSubject;
      main.obs_email_recipient = originalRecipient;
      main.your_custom_address = originalSender;
      main.custom_security = originalSecurity;
      main.custom_email_server = originalHost;
      main.custom_password = originalPassword;
      main.custom_port = originalPort;
      main.obs_email_cc = originalCc;
      main.eucaws_uploads_method = originalUploads;
      main.obs_101_email = originalAttachmentMode;
      mydatetime.YY_code = originalYear;
      mydatetime.GG_code = originalGroup;
    }
  }

  @Test
  public void emptyFm13ObservationProducesEmptyStatus() {
    String originalMode = main.email_send_mode;
    String originalFormat = main.obs_format;
    String originalBody = main.obs_write;
    try {
      main.email_send_mode = main.EMAIL_SEND_CUSTOM;
      main.obs_format = main.FORMAT_FM13;
      main.obs_write = "";

      ObservationEmailWorkflowPreparation.Result result =
          ObservationEmailWorkflowPreparation.prepare(true);

      assertEquals(ObservationEmailStatus.EMPTY_OBSERVATION, result.status());
      assertNull(result.request());
    } finally {
      main.email_send_mode = originalMode;
      main.obs_format = originalFormat;
      main.obs_write = originalBody;
    }
  }
}
