package turbowin;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Validates and computes the comparison using bridge-level pressure readings. */
final class BarometerComparisonModel {
  record Input(
      String ship,
      String height,
      String keel,
      String date,
      String position,
      String shipPressure,
      String referencePressure,
      String draft,
      String temperature) {}

  record Result(Input input, String height, String seaLevel, String error, String correction) {
    BarometerComparisonExportWorkflow.ExportData exportData() {
      return new BarometerComparisonExportWorkflow.ExportData(
          input.ship(),
          input.height(),
          input.keel(),
          input.date(),
          input.position(),
          input.shipPressure(),
          input.referencePressure(),
          input.draft(),
          input.temperature(),
          height,
          seaLevel,
          error,
          correction);
    }
  }

  private BarometerComparisonModel() {}

  static Result calculate(Input input, boolean seaLevelReading) {
    if (seaLevelReading)
      throw new IllegalArgumentException(
          "This form is for ship barometers indicating air pressure at bridge level, not sea level.");
    double height =
        number(
            input.height(),
            "barometer height above SLL not available (Maintenance → Station data)",
            "barometer height above SLL incorrect value (Maintenance → Station data)");
    double keel =
        number(
            input.keel(),
            "distance keel - SLL not available (Maintenance → Station data)",
            "distance keel-SLL incorrect value (Maintenance → Station data)");
    double pressure =
        number(
            input.shipPressure(),
            "ship barometer reading not inserted",
            "ship barometer reading incorrect value");
    range(pressure, 800, 1200, "ship barometer reading out of physical range");
    double reference =
        number(
            input.referencePressure(),
            "reference barometer reading not inserted",
            "reference barometer reading incorrect value");
    range(reference, 800, 1200, "reference barometer reading out of physical range");
    double draft =
        number(
            input.draft(), "actual ship draft not inserted", "actual ship draft incorrect value");
    range(draft, 0, 50, "actual draft out of range");
    double temperature =
        number(
            input.temperature(),
            "outdoor air temperature not inserted",
            "outdoor air temperature incorrect value");
    range(temperature, -100, 100, "outdoor air temperature out of physical range");
    double aboveWater = keel + height - draft;
    double denominator = 29.27 * (temperature + 273.15);
    if (aboveWater < 0 || denominator == 0)
      throw new IllegalArgumentException(
          "computation error encountered or ship barometer height above the waterline negative");
    double seaLevel = pressure + pressure * aboveWater / denominator;
    double correction = reference - seaLevel;
    return new Result(
        input, round(aboveWater), round(seaLevel), round(-correction), round(correction));
  }

  private static double number(String value, String missing, String invalid) {
    if (value.isBlank()) throw new IllegalArgumentException(missing);
    try {
      double result = Double.parseDouble(value);
      if (!Double.isFinite(result)) throw new NumberFormatException();
      return result;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(invalid);
    }
  }

  private static void range(double value, double min, double max, String message) {
    if (value < min || value > max) throw new IllegalArgumentException(message);
  }

  private static String round(double value) {
    return Double.toString(
        BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue());
  }
}
