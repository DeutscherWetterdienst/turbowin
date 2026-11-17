package turbowin;

/** Canonical SMTP transport modes and migration of values written by older TurboWin versions. */
final class SmtpSecurityMode {

  static final String PLAIN = "CUSTOM_PLAIN";
  static final String STARTTLS = "CUSTOM_STARTTLS";
  static final String IMPLICIT_TLS = "CUSTOM_IMPLICIT_TLS";

  private SmtpSecurityMode() {}

  static String normalize(String value, String port) {
    if (PLAIN.equals(value) || STARTTLS.equals(value) || IMPLICIT_TLS.equals(value)) {
      return value;
    }
    if (main.CUSTOM_TLS.equals(value) || "TLS".equalsIgnoreCase(value)) {
      return PLAIN;
    }
    if (main.CUSTOM_TLS_STARTTLS.equals(value) || "STARTTLS".equalsIgnoreCase(value)) {
      return STARTTLS;
    }
    if (main.CUSTOM_SSL.equals(value) || "SSL".equalsIgnoreCase(value)) {
      return IMPLICIT_TLS;
    }
    if (main.CUSTOM_SSL_STARTTLS.equals(value)) {
      if ("465".equals(port)) {
        return IMPLICIT_TLS;
      }
      if ("587".equals(port)) {
        return STARTTLS;
      }
    }
    return "";
  }
}
