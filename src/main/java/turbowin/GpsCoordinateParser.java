package turbowin;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Converts NMEA degree-minute coordinates into TurboWin's legacy coordinate fields. */
final class GpsCoordinateParser {

  private GpsCoordinateParser() {}

  static Result parse(
      String latitude, String latitudeHemisphere, String longitude, String longitudeHemisphere) {
    // NMEA latitude is DDmm.mmmm and longitude is DDDmm.mmmm; rounded minutes retain the legacy
    // carry to the next degree when they reach 60.
    String latitudeDegrees = latitude.substring(0, 2);
    String latitudeMinutesValue = latitude.substring(2);
    BigDecimal latitudeMinutes =
        new BigDecimal(latitudeMinutesValue).setScale(0, RoundingMode.HALF_UP);
    if (latitudeMinutes.intValue() == 60) {
      latitudeMinutes = BigDecimal.ZERO;
      latitudeDegrees = Integer.toString(Integer.parseInt(latitudeDegrees) + 1);
    }
    String latitudeMinutesText = latitudeMinutes.toString();

    String convertedLatitudeHemisphere;
    if (latitudeHemisphere.toUpperCase().equals("N")) {
      convertedLatitudeHemisphere = myposition.HEMISPHERE_NORTH;
    } else if (latitudeHemisphere.toUpperCase().equals("S")) {
      convertedLatitudeHemisphere = myposition.HEMISPHERE_SOUTH;
    } else {
      convertedLatitudeHemisphere = "";
    }

    String longitudeDegrees = longitude.substring(0, 3);
    String longitudeMinutesValue = longitude.substring(3);
    BigDecimal longitudeMinutes =
        new BigDecimal(longitudeMinutesValue).setScale(0, RoundingMode.HALF_UP);
    if (longitudeMinutes.intValue() == 60) {
      longitudeMinutes = BigDecimal.ZERO;
      longitudeDegrees = Integer.toString(Integer.parseInt(longitudeDegrees) + 1);
    }
    String longitudeMinutesText = longitudeMinutes.toString();

    String convertedLongitudeHemisphere;
    if (longitudeHemisphere.toUpperCase().equals("E")) {
      convertedLongitudeHemisphere = myposition.HEMISPHERE_EAST;
    } else if (longitudeHemisphere.toUpperCase().equals("W")) {
      convertedLongitudeHemisphere = myposition.HEMISPHERE_WEST;
    } else {
      convertedLongitudeHemisphere = "";
    }

    boolean valid = true;
    int latitudeDegreesNumber = 0;
    int latitudeMinutesNumber = 0;
    int longitudeDegreesNumber = 0;
    int longitudeMinutesNumber = 0;
    double latitudeMinutesDecimal = 0;
    double longitudeMinutesDecimal = 0;
    boolean latitudeDegreesParsed = true;
    boolean latitudeMinutesParsed = true;
    boolean longitudeDegreesParsed = true;
    boolean longitudeMinutesParsed = true;
    try {
      latitudeDegreesNumber = Integer.parseInt(latitudeDegrees);
    } catch (NumberFormatException ex) {
      valid = false;
      latitudeDegreesParsed = false;
    }
    try {
      latitudeMinutesNumber = Integer.parseInt(latitudeMinutesText);
      latitudeMinutesDecimal = Double.parseDouble(latitudeMinutesValue);
    } catch (NumberFormatException ex) {
      valid = false;
      latitudeMinutesParsed = false;
    }
    try {
      longitudeDegreesNumber = Integer.parseInt(longitudeDegrees);
    } catch (NumberFormatException ex) {
      valid = false;
      longitudeDegreesParsed = false;
    }
    try {
      longitudeMinutesNumber = Integer.parseInt(longitudeMinutesText);
      longitudeMinutesDecimal = Double.parseDouble(longitudeMinutesValue);
    } catch (NumberFormatException ex) {
      valid = false;
      longitudeMinutesParsed = false;
    }
    if (convertedLatitudeHemisphere.isEmpty() || convertedLongitudeHemisphere.isEmpty()) {
      valid = false;
    }

    // IMMT LaLaLa/LoLoLoLo codes use six-minute units; minute remainders are deliberately dropped.
    String latitudeCode = latitudeDegrees + latitudeMinutes.intValue() / 6;
    if (latitudeCode.length() < 3) {
      latitudeCode = "0000000000".substring(0, 3 - latitudeCode.length()) + latitudeCode;
    } else if (latitudeCode.length() > 3) {
      valid = false;
    }

    String longitudeCode = longitudeDegrees + longitudeMinutes.intValue() / 6;
    if (longitudeCode.length() < 4) {
      longitudeCode = "0000000000".substring(0, 4 - longitudeCode.length()) + longitudeCode;
    } else if (longitudeCode.length() > 4) {
      valid = false;
    }

    String quadrant = null;
    if (convertedLatitudeHemisphere.equals(myposition.HEMISPHERE_NORTH)
        && convertedLongitudeHemisphere.equals(myposition.HEMISPHERE_EAST)) {
      quadrant = "1";
    } else if (convertedLatitudeHemisphere.equals(myposition.HEMISPHERE_SOUTH)
        && convertedLongitudeHemisphere.equals(myposition.HEMISPHERE_EAST)) {
      quadrant = "3";
    } else if (convertedLatitudeHemisphere.equals(myposition.HEMISPHERE_SOUTH)
        && convertedLongitudeHemisphere.equals(myposition.HEMISPHERE_WEST)) {
      quadrant = "5";
    } else if (convertedLatitudeHemisphere.equals(myposition.HEMISPHERE_NORTH)
        && convertedLongitudeHemisphere.equals(myposition.HEMISPHERE_WEST)) {
      quadrant = "7";
    } else {
      valid = false;
    }

    // Keep high-accuracy signed decimal degrees for course/speed calculations.
    double signedLatitude = latitudeDegreesNumber + latitudeMinutesDecimal / 60.0;
    if (convertedLatitudeHemisphere.equals(myposition.HEMISPHERE_SOUTH)) {
      signedLatitude *= -1;
    }
    double signedLongitude = longitudeDegreesNumber + longitudeMinutesDecimal / 60.0;
    if (convertedLongitudeHemisphere.equals(myposition.HEMISPHERE_WEST)) {
      signedLongitude *= -1;
    }

    return new Result(
        valid,
        latitudeDegrees,
        latitudeMinutesText,
        convertedLatitudeHemisphere,
        latitudeDegreesNumber,
        latitudeMinutesNumber,
        latitudeCode,
        latitudeDegreesParsed,
        latitudeMinutesParsed,
        longitudeDegrees,
        longitudeMinutesText,
        convertedLongitudeHemisphere,
        longitudeDegreesNumber,
        longitudeMinutesNumber,
        longitudeCode,
        longitudeDegreesParsed,
        longitudeMinutesParsed,
        quadrant,
        signedLatitude,
        signedLongitude);
  }

  static final class Result {
    final boolean valid;
    final String latitudeDegrees;
    final String latitudeMinutes;
    final String latitudeHemisphere;
    final int latitudeDegreesNumber;
    final int latitudeMinutesNumber;
    final String latitudeCode;
    final boolean latitudeDegreesParsed;
    final boolean latitudeMinutesParsed;
    final String longitudeDegrees;
    final String longitudeMinutes;
    final String longitudeHemisphere;
    final int longitudeDegreesNumber;
    final int longitudeMinutesNumber;
    final String longitudeCode;
    final boolean longitudeDegreesParsed;
    final boolean longitudeMinutesParsed;
    final String quadrant;
    final double signedLatitude;
    final double signedLongitude;

    private Result(
        boolean valid,
        String latitudeDegrees,
        String latitudeMinutes,
        String latitudeHemisphere,
        int latitudeDegreesNumber,
        int latitudeMinutesNumber,
        String latitudeCode,
        boolean latitudeDegreesParsed,
        boolean latitudeMinutesParsed,
        String longitudeDegrees,
        String longitudeMinutes,
        String longitudeHemisphere,
        int longitudeDegreesNumber,
        int longitudeMinutesNumber,
        String longitudeCode,
        boolean longitudeDegreesParsed,
        boolean longitudeMinutesParsed,
        String quadrant,
        double signedLatitude,
        double signedLongitude) {
      this.valid = valid;
      this.latitudeDegrees = latitudeDegrees;
      this.latitudeMinutes = latitudeMinutes;
      this.latitudeHemisphere = latitudeHemisphere;
      this.latitudeDegreesNumber = latitudeDegreesNumber;
      this.latitudeMinutesNumber = latitudeMinutesNumber;
      this.latitudeCode = latitudeCode;
      this.latitudeDegreesParsed = latitudeDegreesParsed;
      this.latitudeMinutesParsed = latitudeMinutesParsed;
      this.longitudeDegrees = longitudeDegrees;
      this.longitudeMinutes = longitudeMinutes;
      this.longitudeHemisphere = longitudeHemisphere;
      this.longitudeDegreesNumber = longitudeDegreesNumber;
      this.longitudeMinutesNumber = longitudeMinutesNumber;
      this.longitudeCode = longitudeCode;
      this.longitudeDegreesParsed = longitudeDegreesParsed;
      this.longitudeMinutesParsed = longitudeMinutesParsed;
      this.quadrant = quadrant;
      this.signedLatitude = signedLatitude;
      this.signedLongitude = signedLongitude;
    }
  }
}
