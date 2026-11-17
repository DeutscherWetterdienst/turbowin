package turbowin;

/** Adapter for the Jakarta Mail observation transport. */
final class JakartaObservationEmailSender implements ObservationEmailSender {

  private final Jakarta_Email email;

  JakartaObservationEmailSender(Jakarta_Email email) {
    this.email = email;
  }

  @Override
  public int send(ObservationEmailRequest request) {
    // ObservationEmailParameters uses the sender as the effective username for legacy settings.
    // Keep that path on the original API so existing integrations and subclasses remain valid.
    if (request.smtpUsername().trim().isEmpty()
        || request.smtpUsername().trim().equals(request.sender().trim())) {
      return email.send_jakarta_email_obs(
          request.smtpMode(),
          request.smtpHost(),
          request.password(),
          request.recipient(),
          request.sender(),
          request.subject(),
          request.body(),
          request.cc(),
          request.port(),
          request.attachment());
    }
    return email.send_jakarta_email_obs_with_username(
        request.smtpMode(),
        request.smtpHost(),
        request.password(),
        request.recipient(),
        request.sender(),
        request.subject(),
        request.body(),
        request.cc(),
        request.port(),
        request.attachment(),
        request.smtpUsername());
  }
}
