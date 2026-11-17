package turbowin;

/** Presets for common SMTP transports. Presets never contain credentials. */
enum SmtpProviderProfile {
  MANUAL("Manual", "", "", ""),
  LOCAL_RELAY("Local relay", "localhost", "25", SmtpSecurityMode.PLAIN),
  GENERIC_STARTTLS("Generic STARTTLS", "", "587", SmtpSecurityMode.STARTTLS),
  GENERIC_IMPLICIT_TLS("Generic implicit TLS", "", "465", SmtpSecurityMode.IMPLICIT_TLS),
  GMAIL("Gmail", "smtp.gmail.com", "587", SmtpSecurityMode.STARTTLS),
  OUTLOOK("Outlook.com", "smtp-mail.outlook.com", "587", SmtpSecurityMode.STARTTLS),
  YAHOO("Yahoo Mail", "smtp.mail.yahoo.com", "587", SmtpSecurityMode.STARTTLS);

  private final String label;
  private final String host;
  private final String port;
  private final String security;

  SmtpProviderProfile(String label, String host, String port, String security) {
    this.label = label;
    this.host = host;
    this.port = port;
    this.security = security;
  }

  String label() {
    return label;
  }

  void apply(
      javax.swing.JTextField hostField,
      javax.swing.JTextField portField,
      java.util.function.Consumer<String> securitySetter) {
    if (!host.isEmpty()) {
      hostField.setText(host);
    }
    if (!port.isEmpty()) {
      portField.setText(port);
    }
    if (!security.isEmpty()) {
      securitySetter.accept(security);
    }
  }

  @Override
  public String toString() {
    return label;
  }
}
