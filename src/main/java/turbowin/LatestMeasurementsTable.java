package turbowin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Formats the shared hourly AWS observations for display and export. */
final class LatestMeasurementsTable {
  private LatestMeasurementsTable() {}

  static List<String> headers(boolean knots) {
    String unit = knots ? "kts" : "m/s";
    return List.of(
        "Date [UTC]",
        "Time [UTC]",
        "Lat [°]",
        "Lon [°]",
        "SLP [hPa]",
        "Air temp [°C]",
        "RH [%]",
        "SST [°C]",
        "wind speed true [" + unit + "]",
        "wind dir true [°]",
        "wind gust true [" + unit + "]");
  }

  static List<List<String>> rows(String[][] data, boolean knots) {
    List<List<String>> rows = new ArrayList<>();
    for (int i = data.length - 1; i >= 0; i--) {
      String[] row = data[i];
      if (row[0] == null || row[1] == null || row[0].isEmpty() || row[1].isEmpty()) continue;
      List<String> values = new ArrayList<>(row.length);
      for (int c = 0; c < row.length; c++) {
        String value = row[c] == null ? "" : row[c];
        if (c == LatestMeasurementsData.date_index) {
          value =
              value.length() == 8
                  ? value.substring(0, 4)
                      + "-"
                      + value.substring(4, 6)
                      + "-"
                      + value.substring(6, 8)
                  : "";
        } else if (c == LatestMeasurementsData.time_index) {
          value = value.length() == 6 ? value.substring(0, 2) + ":" + value.substring(2, 4) : "";
        } else if ((c == LatestMeasurementsData.true_wind_speed_index
                || c == LatestMeasurementsData.true_wind_gust_index)
            && !value.isEmpty()) {
          try {
            value =
                String.format(
                    Locale.ROOT,
                    "%.2f",
                    Double.parseDouble(value) * (knots ? main.M_S_KNOT_CONVERSION : 1));
          } catch (NumberFormatException ex) {
            // Keep the original record visible if a device wrote an invalid value.
          }
        }
        values.add(value);
      }
      rows.add(List.copyOf(values));
    }
    return List.copyOf(rows);
  }
}
