package turbowin;

/** Formats an air temperature as the IMMT sign and three-digit TTT code. */
final class ImmtAirTemperatureCodeFormatter {

  private ImmtAirTemperatureCodeFormatter() {}

  static Result format(double temperature) {
    int code = (int) Math.floor(Math.abs(temperature) * 10 + 0.5);
    String formatted = Integer.toString(code);
    if (formatted.length() < 3) {
      formatted = "0000000000".substring(0, 3 - formatted.length()) + formatted;
    }
    return new Result(temperature < 0 ? "1" : "0", formatted);
  }

  static final class Result {
    final String signCode;
    final String temperatureCode;

    private Result(String signCode, String temperatureCode) {
      this.signCode = signCode;
      this.temperatureCode = temperatureCode;
    }
  }
}
