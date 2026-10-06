package turbowin;

/** Handles level-three consistency checks between wind speed and wind-wave height. */
final class WindWaveConsistencyValidation {

  private WindWaveConsistencyValidation() {}

  static boolean validate(
      String windUnits, int windSpeed, boolean waveHeightValid, float waveHeight) {
    if (windUnits.trim().indexOf(main.M_S) != -1
        && windSpeed > 5
        && windSpeed < 500.0
        && waveHeightValid
        && waveHeight <= 0.01) {
      if (!confirm(
          "Wind speed > 5 m/s (> 10 kts) and wind waves height 0 metres \n Press the NO button if it was a typing error, press the YES button if this observation is ok")) {
        return false;
      }
    }

    if (windUnits.trim().indexOf(main.KNOTS) != -1
        && windSpeed > 10
        && windSpeed < 500.0
        && waveHeightValid
        && waveHeight <= 0.01) {
      if (!confirm(
          "Wind speed > 10 kts and wind waves height 0 metres \n Press the NO button if it was a typing error, press the YES button if this observation is ok")) {
        return false;
      }
    }

    if (windUnits.trim().indexOf(main.M_S) != -1
        && windSpeed > 21
        && windSpeed < 500.0
        && waveHeightValid
        && waveHeight < 2.3) {
      if (!confirm(
          "Wind speed > 21 m/s (> 41 kts) and wind waves height < 2.3 metres \n Press the NO button if it was a typing error, press the YES button if this observation is ok")) {
        return false;
      }
    }

    if (windUnits.trim().indexOf(main.KNOTS) != -1
        && windSpeed > 41
        && windSpeed < 500.0
        && waveHeightValid
        && waveHeight < 2.3) {
      if (!confirm(
          "Wind speed > 41 kts and wind waves height < 2.3 metres \n Press the NO button if it was a typing error, press the YES button if this observation is ok")) {
        return false;
      }
    }

    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
