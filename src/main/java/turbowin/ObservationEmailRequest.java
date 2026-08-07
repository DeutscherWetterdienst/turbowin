package turbowin;

/** Immutable arguments shared by the observation email transports. */
final class ObservationEmailRequest {

  private final String smtpMode;
  private final String smtpHost;
  private final String password;
  private final String recipient;
  private final String sender;
  private final String subject;
  private final String body;
  private final String cc;
  private final String port;
  private final String attachment;

  ObservationEmailRequest(
      String smtpMode,
      String smtpHost,
      String password,
      String recipient,
      String sender,
      String subject,
      String body,
      String cc,
      String port,
      String attachment) {
    this.smtpMode = smtpMode;
    this.smtpHost = smtpHost;
    this.password = password;
    this.recipient = recipient;
    this.sender = sender;
    this.subject = subject;
    this.body = body;
    this.cc = cc;
    this.port = port;
    this.attachment = attachment;
  }

  String smtpMode() {
    return smtpMode;
  }

  String smtpHost() {
    return smtpHost;
  }

  String password() {
    return password;
  }

  String recipient() {
    return recipient;
  }

  String sender() {
    return sender;
  }

  String subject() {
    return subject;
  }

  String body() {
    return body;
  }

  String cc() {
    return cc;
  }

  String port() {
    return port;
  }

  String attachment() {
    return attachment;
  }
}
