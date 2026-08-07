package turbowin;

/** Adapter for the Python executable observation transport. */
final class PythonObservationEmailSender implements ObservationEmailSender {

  private final Python_Email email;

  PythonObservationEmailSender(Python_Email email) {
    this.email = email;
  }

  @Override
  public int send(ObservationEmailRequest request) {
    int status =
        email.send_python_email(
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
    main.log_turbowin_system_message(
        "[EMAIL] " + email.python_email_exe_return_status_to_text(status));
    return status;
  }
}
