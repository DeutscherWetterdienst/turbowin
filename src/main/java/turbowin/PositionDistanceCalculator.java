package turbowin;

/** Calculates great-circle distances between the current and previous observation positions. */
final class PositionDistanceCalculator {

  private PositionDistanceCalculator() {}

  static int calculate(
      double previousLatitude,
      double previousLongitude,
      int currentLatitudeDegrees,
      int currentLatitudeMinutes,
      String currentLatitudeHemisphere,
      int currentLongitudeDegrees,
      int currentLongitudeMinutes,
      String currentLongitudeHemisphere) {
    final int westBoundary = 90;
    final int eastBoundary = -90;
    // One arc minute in radians; coordinates are converted from degrees via 60 arc minutes.
    final double arcMinute = 0.0002908882;
    // Radians to nautical miles: 60 nautical miles per degree.
    final double minutesPerPi = 3437.746771;

    double currentLatitude = currentLatitudeDegrees + ((double) currentLatitudeMinutes / 60);
    double currentLongitude = currentLongitudeDegrees + ((double) currentLongitudeMinutes / 60);

    if (currentLatitudeHemisphere.equals(myposition.HEMISPHERE_SOUTH)) {
      currentLatitude *= -1;
    }

    if (currentLongitudeHemisphere.equals(myposition.HEMISPHERE_WEST)) {
      currentLongitude *= -1;
    }

    boolean currentPositionInPor =
        (currentLongitude >= -180 && currentLongitude <= eastBoundary)
            || (currentLongitude >= westBoundary && currentLongitude <= 180);

    // Normalize longitudes across the Pacific Ocean Region (POR) dateline before comparing them.
    if (currentPositionInPor) {
      if (currentLongitude < 0) {
        currentLongitude += 360;
      }
      if (previousLongitude < 0) {
        previousLongitude += 360;
      }
    }

    double longitudeDifference = currentLongitude - previousLongitude;
    if (Math.abs(longitudeDifference) > 180) {
      // Preserve the legacy sentinel when the longitude separation is beyond the valid range.
      return Integer.MAX_VALUE;
    }

    double sinePreviousLatitude = Math.sin(previousLatitude * 60 * arcMinute);
    double sineCurrentLatitude = Math.sin(currentLatitude * 60 * arcMinute);
    double cosinePreviousLatitude = Math.cos(previousLatitude * 60 * arcMinute);
    double cosineCurrentLatitude = Math.cos(currentLatitude * 60 * arcMinute);
    double cosineLongitudeDifference = Math.cos(longitudeDifference * 60 * arcMinute);
    // Great-circle formula: cos(angle) = sin(latA)sin(latB) + cos(latA)cos(latB)cos(deltaLon).
    double acosArgument =
        sinePreviousLatitude * sineCurrentLatitude
            + cosinePreviousLatitude * cosineCurrentLatitude * cosineLongitudeDifference;

    if (acosArgument <= -1 || acosArgument >= 1) {
      return 0;
    }

    return (int) Math.round(minutesPerPi * Math.acos(acosArgument));
  }
}
