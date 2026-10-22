package turbowin;

/**
 * Calculates the corrected pressure value displayed on the serial dashboard.
 *
 * <p>Pressure values are in hPa and must be strictly between 900 and 1100 hPa. The instrument
 * correction is also in hPa and is accepted only strictly between -4 and 4 hPa; out-of-range
 * numeric values and {@code NaN} use the legacy zero-correction behavior. A non-numeric
 * non-empty correction propagates its parse failure. Pressure is rounded to one decimal place
 * before and after applying the correction.
 */
final class DashboardPressureCalculator {

  private static final double MIN_PRESSURE_HPA = 900.0;
  private static final double MAX_PRESSURE_HPA = 1100.0;
  private static final double MIN_INSTRUMENT_CORRECTION_HPA = -4.0;
  private static final double MAX_INSTRUMENT_CORRECTION_HPA = 4.0;

  private DashboardPressureCalculator() {}

  static Double calculate(String pressure, String instrumentCorrection) {
    final double roundedPressure;
    try {
      roundedPressure = Math.round(Double.parseDouble(pressure) * 10) / 10.0d;
    } catch (NumberFormatException exception) {
      return null;
    }

    if (!(roundedPressure > MIN_PRESSURE_HPA && roundedPressure < MAX_PRESSURE_HPA)) {
      return null;
    }

    double correction = 0.0;
    if (!instrumentCorrection.equals("")) {
      correction = Double.parseDouble(instrumentCorrection.trim());
    }

    if (Double.isNaN(correction)
        || correction <= MIN_INSTRUMENT_CORRECTION_HPA
        || correction >= MAX_INSTRUMENT_CORRECTION_HPA) {
      correction = 0.0;
    }

    return Math.round((roundedPressure + correction) * 10d) / 10d;
  }
}
