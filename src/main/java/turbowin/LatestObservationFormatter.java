package turbowin;

import java.text.DecimalFormat;

final class LatestObservationFormatter {

  private LatestObservationFormatter() {}

  static String pressure(String pressure) {
    // IMMT: 991.7 hPa is encoded as 9917; 1007.8 hPa is encoded as 0078.
    try {
      int value = Integer.parseInt(pressure);
      if (value > 8000) {
        return pressure.substring(0, 3) + "." + pressure.substring(3) + " hPa";
      }
      return "1" + pressure.substring(0, 3) + "." + pressure.substring(3) + " hPa";
    } catch (NumberFormatException ex) {
      return "-";
    }
  }

  static String temperature(String sign, String temperature, boolean fahrenheit) {
    String result = signedTemperature(sign, temperature);
    return fahrenheit ? withFahrenheit(result) : result + " °C";
  }

  static String seaSurfaceTemperature(String sign, String temperature, boolean fahrenheit) {
    String result = signedTemperature(sign, temperature);
    return fahrenheit ? withFahrenheit(result) : result + " °C";
  }

  static String waveHeight(String height) {
    // IMMT wave and swell heights are encoded in half-meter units.
    try {
      return String.format("%.1f", Double.parseDouble(height) / 2.0);
    } catch (NumberFormatException ex) {
      return "-";
    }
  }

  static String swellHeight(String height) {
    return waveHeight(height);
  }

  private static String signedTemperature(String sign, String temperature) {
    // IMMT Sn sign code: 0 is positive and 1 is negative.
    String result = sign.equals("1") ? "-" : "";
    if (temperature.substring(0, 1).equals("0")) {
      return result + temperature.substring(1, 2) + "." + temperature.substring(2);
    }
    return result + temperature.substring(0, 2) + "." + temperature.substring(2);
  }

  private static String withFahrenheit(String celsius) {
    DecimalFormat format = new DecimalFormat("0.0");
    String fahrenheit = format.format((Double.parseDouble(celsius) * 1.8) + 32.0);
    // Locale may produce a comma (e.g. Netherlands); use a dot for dashboard compatibility.
    fahrenheit = fahrenheit.replace(",", ".");
    return celsius + " °C / " + fahrenheit + " °F";
  }
}
