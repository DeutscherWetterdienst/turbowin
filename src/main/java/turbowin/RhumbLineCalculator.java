package turbowin;

/** Calculates distance and course between two positions using a rhumb line. */
final class RhumbLineCalculator {

  private static final double EARTH_RADIUS_METERS = 6371e3;

  private RhumbLineCalculator() {}

  static Result calculate(
      double latitudeStart, double longitudeStart, double latitudeEnd, double longitudeEnd) {
    double latitudeStartRadians = Math.toRadians(latitudeStart);
    double latitudeEndRadians = Math.toRadians(latitudeEnd);
    double deltaLatitudeRadians = Math.toRadians(latitudeEnd - latitudeStart);
    double deltaLongitudeRadians = Math.toRadians(longitudeEnd - longitudeStart);

    double deltaProjectedLatitude =
        Math.log(
            Math.tan(Math.PI / 4 + latitudeEndRadians / 2)
                / Math.tan(Math.PI / 4 + latitudeStartRadians / 2));
    double q =
        Math.abs(deltaProjectedLatitude) > 10e-12
            ? deltaLatitudeRadians / deltaProjectedLatitude
            : Math.cos(latitudeStartRadians);

    if (deltaLongitudeRadians > Math.PI) deltaLongitudeRadians -= 2 * Math.PI;
    if (deltaLongitudeRadians < -Math.PI) deltaLongitudeRadians += 2 * Math.PI;

    double distanceMeters =
        Math.sqrt(
                deltaLatitudeRadians * deltaLatitudeRadians
                    + q * q * deltaLongitudeRadians * deltaLongitudeRadians)
            * EARTH_RADIUS_METERS;

    double courseDegrees =
        Math.toDegrees(Math.atan2(deltaLongitudeRadians, deltaProjectedLatitude));
    if (courseDegrees < 0) courseDegrees += 360.0;

    return new Result(distanceMeters, courseDegrees);
  }

  static final class Result {
    final double distanceMeters;
    final double courseDegrees;

    private Result(double distanceMeters, double courseDegrees) {
      this.distanceMeters = distanceMeters;
      this.courseDegrees = courseDegrees;
    }
  }
}
