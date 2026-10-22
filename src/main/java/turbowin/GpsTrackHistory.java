package turbowin;

/**
 * Stores recent GPS positions and retrieves positions at a requested time interval.
 *
 * <p>Entries are newest first: position {@code [0][0]} is latitude, {@code [0][1]} is longitude,
 * and each timestamp is minutes since the Unix epoch. Format 101 requests a 10-minute interval;
 * FM13 requests 180 minutes. The history is intentionally larger than 180 entries because gaps
 * and duplicate readings mean that minutes and entries do not correspond one-to-one.
 *
 * <p>Invalid positions and timestamps use {@link Double#MAX_VALUE} and {@link Long#MAX_VALUE}.
 * Retrieval returns latitude and longitude for the older position followed by latitude and
 * longitude for the newest position. During startup, when the format is unknown, callers use
 * {@code Integer.MAX_VALUE} as the interval and receive the invalid sentinels.
 */
final class GpsTrackHistory {

  private static final double INVALID_POSITION = Double.MAX_VALUE;
  private static final long INVALID_TIME = Long.MAX_VALUE;

  private final double[][] positions;
  private final long[] times;

  GpsTrackHistory(int maximumEntries) {
    positions = new double[maximumEntries + 1][2];
    times = new long[maximumEntries + 1];
    clear();
  }

  void clear() {
    for (int index = 0; index < positions.length; index++) {
      positions[index][0] = INVALID_POSITION;
      positions[index][1] = INVALID_POSITION;
      times[index] = INVALID_TIME;
    }
  }

  void store(double latitude, double longitude, long timeMinutes) {
    if ((latitude < (INVALID_POSITION - 1))
        && (longitude < (INVALID_POSITION - 1))
        && (timeMinutes < INVALID_TIME)) {
      for (int index = positions.length - 1; index > 0; index--) {
        positions[index][0] = positions[index - 1][0];
        positions[index][1] = positions[index - 1][1];
        times[index] = times[index - 1];
      }
      positions[0][0] = latitude;
      positions[0][1] = longitude;
      times[0] = timeMinutes;
    }
  }

  double[] retrieve(long intervalMinutes) {
    double latitudeStart = INVALID_POSITION;
    double longitudeStart = INVALID_POSITION;
    double latitudeEnd = INVALID_POSITION;
    double longitudeEnd = INVALID_POSITION;

    for (int index = positions.length - 1; index > 0; index--) {
      if ((times[0] - times[index]) == intervalMinutes) {
        latitudeStart = positions[index][0];
        longitudeStart = positions[index][1];
        latitudeEnd = positions[0][0];
        longitudeEnd = positions[0][1];
        break;
      }
    }
    return new double[] {latitudeStart, longitudeStart, latitudeEnd, longitudeEnd};
  }
}
