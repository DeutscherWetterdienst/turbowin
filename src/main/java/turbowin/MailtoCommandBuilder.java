package turbowin;

/** Builds the legacy platform-specific commands used to open a mailto link. */
final class MailtoCommandBuilder {

  private MailtoCommandBuilder() {}

  static String[] forWindows(String mailtoPayload) {
    return new String[] {"cmd", "/c", "start", "mailto:", mailtoPayload};
  }

  static String[] forMacOs(String mailtoPayload) {
    return new String[] {"open", "mailto:", mailtoPayload};
  }

  static String[] forLinux(String mailtoPayload) {
    return new String[] {"xdg-open", "mailto:", mailtoPayload};
  }

  static String[] forKde(String mailtoPayload) {
    return new String[] {"kde-open", "mailto:", mailtoPayload};
  }
}
