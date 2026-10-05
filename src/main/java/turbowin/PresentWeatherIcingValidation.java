package turbowin;

import java.util.Arrays;
import javax.swing.JOptionPane;

/** Handles level-three confirmation checks between present weather and icing. */
final class PresentWeatherIcingValidation {

  private static final Integer[] DEPOSITING_RIME = {48, 49};
  private static final Integer[] FREEZING_DRIZZLE = {56, 57};
  private static final Integer[] FREEZING_RAIN = {66, 67};

  private PresentWeatherIcingValidation() {}

  static boolean validate(boolean weatherCodeValid, int weatherCode, boolean noIcing) {
    if (weatherCodeValid && Arrays.asList(DEPOSITING_RIME).contains(weatherCode) && noIcing) {
      if (!confirm(
          "Fog, depositing rime (present weather) and no ICING?\n Press the NO button if it was a typing error, press the YES button if this observation is ok")) {
        return false;
      }
    }

    if (weatherCodeValid && Arrays.asList(FREEZING_DRIZZLE).contains(weatherCode) && noIcing) {
      if (!confirm(
          "Freezing drizzle (present weather) and no ICING?\n Press the NO button if it was a typing error, press the YES button if this observation is ok")) {
        return false;
      }
    }

    if (weatherCodeValid && Arrays.asList(FREEZING_RAIN).contains(weatherCode) && noIcing) {
      if (!confirm(
          "Freezing rain (present weather) and no ICING?\n Press the NO button if it was a typing error, press the YES button if this observation is ok")) {
        return false;
      }
    }

    return true;
  }

  private static boolean confirm(String message) {
    if (JOptionPane.showConfirmDialog(
            null,
            message,
            main.APPLICATION_NAME + ", please confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.NO_OPTION) {
      JOptionPane.showMessageDialog(
          null,
          "Please correct the error (no final obs was coded)",
          main.APPLICATION_NAME,
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }
}
