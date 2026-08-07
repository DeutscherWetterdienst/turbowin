package turbowin;

/** Formats the common diagnostic details written before sending an observation email. */
final class ObservationEmailLogDetails {

  private ObservationEmailLogDetails() {}

  static Details from(String cc, String port, String attachment) {
    return new Details(
        "null".equals(cc) ? "none" : cc,
        "null".equals(port) ? "system defined" : port,
        "null".equals(attachment) ? "none" : "yes");
  }

  static String message(
      String body, String recipient, Details details, String sender, String mode, String module) {
    return "[EMAIL] trying to send obs (body= \""
        + body
        + "\""
        + ") to "
        + recipient
        + " cc "
        + details.cc()
        + " from "
        + sender
        + " via "
        + mode
        + " port "
        + details.port()
        + " attachment "
        + details.attachment()
        + " ["
        + module
        + ", virtual thread]";
  }

  static final class Details {
    private final String cc;
    private final String port;
    private final String attachment;

    private Details(String cc, String port, String attachment) {
      this.cc = cc;
      this.port = port;
      this.attachment = attachment;
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
}
