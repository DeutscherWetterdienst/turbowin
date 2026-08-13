package turbowin;

/** Shared validation helpers for automated reporting settings. */
final class ReportingSettingsValidation {

  private ReportingSettingsValidation() {}

  static boolean isBlank(String value) {
    return value.equals("");
  }
}
