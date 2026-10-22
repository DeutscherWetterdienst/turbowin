package turbowin;

/** Converts a 10-meter wind speed to the dashboard's selected units. */
final class AwsDashboardWindSpeedAt10mConverter {

  private AwsDashboardWindSpeedAt10mConverter() {}

  static Integer convert(double windSpeedAt10m, String measuredUnits, String dashboardUnits) {
    if (measuredUnits.indexOf(main.M_S) != -1) {
      if (dashboardUnits.indexOf(main.KNOTS) != -1) {
        return (int) Math.round(windSpeedAt10m * main.M_S_KNOT_CONVERSION);
      }
      if (dashboardUnits.indexOf(main.M_S) != -1) {
        return (int) Math.round(windSpeedAt10m);
      }
    } else if (measuredUnits.indexOf(main.KNOTS) != -1) {
      if (dashboardUnits.indexOf(main.KNOTS) != -1) {
        return (int) Math.round(windSpeedAt10m);
      }
      if (dashboardUnits.indexOf(main.M_S) != -1) {
        // Preserve the existing conversion used by the legacy dashboard workflow.
        return (int) Math.round(windSpeedAt10m * main.M_S_KNOT_CONVERSION);
      }
    }
    return null;
  }
}
