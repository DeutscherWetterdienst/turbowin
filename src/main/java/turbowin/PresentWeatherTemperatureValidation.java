package turbowin;

import java.util.Arrays;
import javax.swing.JOptionPane;

/** Validates present-weather codes against high air temperatures. */
final class PresentWeatherTemperatureValidation {

  private PresentWeatherTemperatureValidation() {}

  static boolean validate(
      boolean airTemperatureValid,
      float airTemperature,
      int weatherCode,
      Integer[] driftingOrBlowingSnow,
      Integer[] depositingRime,
      Integer[] freezingDrizzle,
      Integer[] freezingRain,
      Integer[] snow,
      Integer[] snowFlakes,
      Integer[] snowGrainsOrCrystals,
      Integer[] snowShowers) {
    if (airTemperatureValid
        && airTemperature > 20.0
        && airTemperature < 99.9
        && Arrays.asList(driftingOrBlowingSnow).contains(weatherCode)) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'drifting snow' or 'blowing snow'");
    }
    if (airTemperatureValid
        && airTemperature > 20.0
        && airTemperature < 99.9
        && Arrays.asList(depositingRime).contains(weatherCode)) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'depositing rime'");
    }
    if (airTemperatureValid
        && airTemperature > 20.0
        && airTemperature < 99.9
        && Arrays.asList(freezingDrizzle).contains(weatherCode)) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'freezing drizzle'");
    }
    if (airTemperatureValid
        && airTemperature > 20.0
        && airTemperature < 99.9
        && Arrays.asList(freezingRain).contains(weatherCode)) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'freezing rain'");
    }
    if (airTemperatureValid
        && airTemperature > 20.0
        && airTemperature < 99.9
        && Arrays.asList(snow).contains(weatherCode)) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'snow'");
    }
    if (airTemperatureValid
        && airTemperature > 20.0
        && airTemperature < 99.9
        && Arrays.asList(snowFlakes).contains(weatherCode)) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'snow flakes'");
    }
    if (airTemperatureValid
        && airTemperature > 20.0
        && airTemperature < 99.9
        && Arrays.asList(snowGrainsOrCrystals).contains(weatherCode)) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'snow grains/crystals' or 'ice prisms/pellets'");
    }
    if (airTemperatureValid
        && airTemperature > 20.0
        && airTemperature < 99.9
        && Arrays.asList(snowShowers).contains(weatherCode)) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'snow shower(s)'");
    }
    return true;
  }

  private static boolean warning(String message) {
    JOptionPane.showMessageDialog(
        null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
    return false;
  }
}
