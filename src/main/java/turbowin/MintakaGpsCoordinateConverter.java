package turbowin;

/** Converts a Mintaka GPS coordinate such as {@code 52 04.7041N} to decimal degrees. */
final class MintakaGpsCoordinateConverter {

  static final double INVALID_COORDINATE = Double.MAX_VALUE;

  private MintakaGpsCoordinateConverter() {}

  static double toDecimal(String coordinate, boolean latitude) {
    String value = coordinate.trim();
    int spacePosition = value.indexOf(' ');

    double degrees = Double.parseDouble(value.substring(0, spacePosition));
    double minutes = Double.parseDouble(value.substring(spacePosition + 1, value.length() - 1));
    double decimal = degrees + minutes / 60.0;
    char hemisphere = Character.toUpperCase(value.charAt(value.length() - 1));
    if ((latitude && hemisphere == 'S') || (!latitude && hemisphere == 'W')) {
      return -decimal;
    }
    if ((latitude && hemisphere == 'N') || (!latitude && hemisphere == 'E')) {
      return decimal;
    }
    return INVALID_COORDINATE;
  }
}
