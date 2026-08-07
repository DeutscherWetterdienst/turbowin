package turbowin;

/**
 * Holds the SMTP parameters shared by the observation email transports.
 *
 * <p>The Python email module requires the literal {@code "null"} marker instead of an empty
 * string for omitted arguments. Custom security values are {@code CUSTOM_TLS_STARTTLS},
 * {@code CUSTOM_TLS}, {@code CUSTOM_SSL_STARTTLS}, or {@code CUSTOM_SSL}; hosts may be values such
 * as {@code smtp.mail.special.com} and ports such as {@code 587}. FM13 does not use an attachment,
 * while Format 101 may request one with the {@code "yes"} marker.
 */
final class ObservationEmailParameters {

  static final String NULL_VALUE = "null";

  private final String smtpMode;
  private final String smtpHost;
  private final String smtpPassword;
  private final String recipient;
  private final String sender;
  private final String subject;
  private final String cc;
  private final String port;
  private final String attachment;

  private ObservationEmailParameters(
      String smtpMode,
      String smtpHost,
      String smtpPassword,
      String recipient,
      String sender,
      String subject,
      String cc,
      String port,
      String attachment) {
    this.smtpMode = smtpMode;
    this.smtpHost = smtpHost;
    this.smtpPassword = smtpPassword;
    this.recipient = recipient;
    this.sender = sender;
    this.subject = subject;
    this.cc = cc;
    this.port = port;
    this.attachment = attachment;
  }

  static ObservationEmailParameters forCustomSettings(
      String security,
      String host,
      String password,
      String recipient,
      String sender,
      String subject,
      String cc,
      String port,
      boolean includeAttachment) {
    return new ObservationEmailParameters(
        security,
        host,
        password,
        recipient,
        sender,
        subject,
        cc.length() > 3 ? cc : NULL_VALUE,
        port,
        includeAttachment ? "yes" : NULL_VALUE);
  }

  String smtpMode() {
    return smtpMode;
  }

  String smtpHost() {
    return smtpHost;
  }

  String smtpPassword() {
    return smtpPassword;
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
