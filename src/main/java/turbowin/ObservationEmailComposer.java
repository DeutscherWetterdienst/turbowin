package turbowin;

/** Builds the legacy mailto payload used by the desktop email workflows. */
final class ObservationEmailComposer {

  private ObservationEmailComposer() {}

  /**
   * Builds a mailto query: {@code ?} prefixes the first variable after the address and {@code &}
   * separates subsequent variables.
   *
   * <p>The {@code cc.length() > 3} check is retained as a legacy workflow rule.
   */
  static String buildMailText(String recipient, String cc, String subject, String body) {
    if (cc.length() > 3) {
      return recipient + "?cc=" + cc + "&subject=" + subject + "&body=" + body;
    }

    return recipient + "?subject=" + subject + "&body=" + body;
  }
}
