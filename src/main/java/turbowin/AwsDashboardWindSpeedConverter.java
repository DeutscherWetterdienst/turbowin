package turbowin;

/** Converts measured wind speed to the dashboard's selected units. */
final class AwsDashboardWindSpeedConverter {

  private AwsDashboardWindSpeedConverter() {}

  static Integer convert(
      double measuredSpeed, int measuredRoundedSpeed, String measuredUnits, String dashboardUnits) {
    if (measuredUnits.indexOf(main.M_S) != -1) {
      if (dashboardUnits.indexOf(main.KNOTS) != -1) {
        return (int) Math.round(measuredSpeed * main.M_S_KNOT_CONVERSION);
      }
      if (dashboardUnits.indexOf(main.M_S) != -1) {
        return measuredRoundedSpeed;
      }
    } else if (measuredUnits.indexOf(main.KNOTS) != -1) {
      if (dashboardUnits.indexOf(main.KNOTS) != -1) {
        return measuredRoundedSpeed;
      }
      if (dashboardUnits.indexOf(main.M_S) != -1) {
        return (int) Math.round(measuredSpeed * main.KNOT_M_S_CONVERSION);
      }
    }
    return null;
  }
}
