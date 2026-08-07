package turbowin;

/** Classifies the legacy manual and automatic observation email modes. */
final class ObservationEmailModeResolver {

  enum Mode {
    CUSTOM,
    DISABLED,
    INVALID
  }

  private ObservationEmailModeResolver() {}

  static Mode resolve(
      boolean manual,
      String manualMode,
      String automaticMode,
      String localHostMode,
      String gmailMode,
      String yahooMode,
      String customMode,
      String automaticSmtpHostMode,
      String automaticGmailMode,
      String automaticYahooMode,
      String automaticCustomMode) {
    String selectedMode = manual ? manualMode : automaticMode;
    if (manual) {
      if (localHostMode.equals(selectedMode)
          || gmailMode.equals(selectedMode)
          || yahooMode.equals(selectedMode)) {
        return Mode.DISABLED;
      }
      if (customMode.equals(selectedMode)) {
        return Mode.CUSTOM;
      }
    } else {
      if (automaticSmtpHostMode.equals(selectedMode)
          || automaticGmailMode.equals(selectedMode)
          || automaticYahooMode.equals(selectedMode)) {
        return Mode.DISABLED;
      }
      if (automaticCustomMode.equals(selectedMode)) {
        return Mode.CUSTOM;
      }
    }
    return Mode.INVALID;
  }
}
