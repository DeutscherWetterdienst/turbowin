package turbowin;

/**
 * Removes APTR/AWSR methods retired in version 4.2: SMTP host, Gmail, and Yahoo are no longer
 * supported; only the server meteorological center and custom email methods remain available.
 */
final class AptrAwsrSendMethodNormalizer {

  private AptrAwsrSendMethodNormalizer() {}

  static String normalize(String value) {
    if (value.equals(main.APTR_AWSR_SMTP_HOST)
        || value.equals(main.APTR_AWSR_GMAIL)
        || value.equals(main.APTR_AWSR_YAHOO_MAIL)) {
      return "";
    }
    return value;
  }
}
