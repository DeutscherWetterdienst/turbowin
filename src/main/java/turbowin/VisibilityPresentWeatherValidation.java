package turbowin;

import java.util.Arrays;

/** Validates visibility against present-weather codes. */
final class VisibilityPresentWeatherValidation {

  private static final Integer[] FOG = {42, 43, 44, 45, 46, 47, 48, 49};
  private static final Integer[] VISIBILITY_95_TO_99 = {95, 96, 97, 98, 99};
  private static final Integer[] VISIBILITY_90_TO_93 = {90, 91, 92, 93};

  private VisibilityPresentWeatherValidation() {}

  static boolean validate(
      boolean weatherCodeValid,
      int weatherCode,
      boolean visibilityCodeValid,
      int visibilityCode,
      String weather40Description) {
    if (weatherCodeValid
        && Arrays.asList(FOG).contains(weatherCode)
        && visibilityCodeValid
        && Arrays.asList(VISIBILITY_95_TO_99).contains(visibilityCode)) {
      return warning(
          "if present weather = 'fog', visibility cannot be > 0.5 nm (an exception is made for 'fog banks', 'fog in patches' and 'shallow fog')");
    }

    if (weatherCodeValid
        && weatherCode == 40
        && visibilityCodeValid
        && Arrays.asList(VISIBILITY_90_TO_93).contains(visibilityCode)) {
      String info =
          "if present weather = "
              + "\""
              + weather40Description
              + "\""
              + ", reported visibility cannot be < 0.5 nm";
      return warning(info);
    }

    return true;
  }

  private static boolean warning(String message) {
    return ValidationDialog.warning(message);
  }
}
