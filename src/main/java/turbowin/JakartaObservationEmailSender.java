package turbowin;

/** Adapter for the Jakarta Mail observation transport. */
final class JakartaObservationEmailSender implements ObservationEmailSender {

  private final Jakarta_Email email;

  JakartaObservationEmailSender(Jakarta_Email email) {
    this.email = email;
  }

  @Override
  public int send(ObservationEmailRequest request) {
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
}
