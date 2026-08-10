package turbowin;

import static turbowin.main.*;

import javax.swing.JOptionPane;

/** Builds the common prepared request used by the observation email workflows. */
final class ObservationEmailWorkflowPreparation {

  private ObservationEmailWorkflowPreparation() {}

  static Result prepare(boolean manualSend) {
    ObservationEmailModeResolver.Mode emailMode =
        ObservationEmailModeResolver.resolve(
            manualSend,
            email_send_mode,
            APTR_AWSR_send_method,
            EMAIL_SEND_LOCAL_HOST,
            EMAIL_SEND_GMAIL,
            EMAIL_SEND_YAHOO,
            EMAIL_SEND_CUSTOM,
            APTR_AWSR_SMTP_HOST,
            APTR_AWSR_GMAIL,
            APTR_AWSR_YAHOO_MAIL,
            APTR_AWSR_CUSTOM_MAIL);

    if (emailMode == ObservationEmailModeResolver.Mode.DISABLED) {
      JOptionPane.showMessageDialog(
          null,
          "invalid email send method (Maintenance -> Email settings, insert the CUSTOM settings)",
          main.APPLICATION_NAME + " error",
          JOptionPane.WARNING_MESSAGE);
    }

    if (emailMode != ObservationEmailModeResolver.Mode.CUSTOM) {
      return new Result(ObservationEmailStatus.INVALID_MODE, null);
    }

    boolean includeAttachment =
        (obs_format.equals(FORMAT_101)
                || (obs_format.equals(FORMAT_AWS)
                    && eucaws_uploads_method.equals(UPLOADS_VIA_TURBOWIN)))
            && obs_101_email.equals(FORMAT_101_ATTACHEMENT);
    ObservationEmailParameters parameters =
        ObservationEmailParameters.forCustomSettings(
            custom_security,
            custom_email_server,
            custom_password,
            obs_email_recipient,
            your_custom_address,
            obs_email_subject,
            obs_email_cc,
            custom_port,
            includeAttachment);

    ObservationEmailContentPreparer.Result content =
        ObservationEmailContentPreparer.prepare(
            obs_format,
            eucaws_uploads_method.equals(UPLOADS_VIA_TURBOWIN),
            obs_101_email,
            (obs_format.equals(FORMAT_101)
                        || (obs_format.equals(FORMAT_AWS)
                            && eucaws_uploads_method.equals(UPLOADS_VIA_TURBOWIN)))
                    && !obs_101_email.equals(FORMAT_101_ATTACHEMENT)
                ? get_format_101_obs_from_file()
                : "",
            obs_write,
            parameters.subject(),
            mydatetime.YY_code,
            mydatetime.GG_code);

    if (!content.nonEmpty()) {
      return new Result(ObservationEmailStatus.EMPTY_OBSERVATION, null);
    }

    ObservationEmailRequest request =
        ObservationEmailRequest.from(
            parameters.smtpMode(),
            parameters.smtpHost(),
            parameters.smtpPassword(),
            parameters.recipient(),
            parameters.sender(),
            content.subject(),
            content.body(),
            parameters.cc(),
            parameters.port(),
            parameters.attachment());
    return new Result(0, request);
  }

  static ObservationEmailRequest resolvePassword(ObservationEmailRequest request) {
    return ObservationEmailRequest.from(
        request.smtpMode(),
        request.smtpHost(),
        ObservationEmailPasswordResolver.resolve(request.password(), myemailsettings::decrypt),
        request.recipient(),
        request.sender(),
        request.subject(),
        request.body(),
        request.cc(),
        request.port(),
        request.attachment());
  }

  static final class Result {
    private final int status;
    private final ObservationEmailRequest request;

    private Result(int status, ObservationEmailRequest request) {
      this.status = status;
      this.request = request;
    }

    int status() {
      return status;
    }

    ObservationEmailRequest request() {
      return request;
    }
  }
}
