package turbowin;

/**
 * Calculates WMO IMMT movement codes from speed and course.
 *
 * <p>The speed code ({@code vs}) uses knots: 0 is below 1 knot, 1 through 8 cover successive
 * 5-knot bands from 1 through 40 knots, and 9 means over 40 knots (up to the available 99-knot
 * range). The direction code ({@code Ds}) uses the course boundaries (23, 67], (67, 112], (112,
 * 157], (157, 202], (202, 247], (247, 292], (292, 337], and (337, 360] or (0, 23] for codes 1
 * through 8. Code 0 means stopped (speed below 1 knot), while code 9 means that no direction
 * information is available.
 *
 * <p>These are the WMO IMMT movement values: the speed and resultant direction describe the
 * vessel's movement during the preceding observation interval. A speed above 999999.9 is treated
 * as unavailable for direction calculation.
 */
final class ImmtMovementCodeCalculator {

  private ImmtMovementCodeCalculator() {}

  static String speedCode(double speed) {
    if (speed < 1.0) return "0";
    if (speed <= 5.0) return "1";
    if (speed <= 10.0) return "2";
    if (speed <= 15.0) return "3";
    if (speed <= 20.0) return "4";
    if (speed <= 25.0) return "5";
    if (speed <= 30.0) return "6";
    if (speed <= 35.0) return "7";
    if (speed <= 40.0) return "8";
    if (speed <= 99.0) return "9";
    return null;
  }

  static String directionCode(double speed, double course) {
    if (Double.isNaN(speed)) return null;
    if (speed < 1.0) return "0";
    if (speed > 999999.9) return null;
    if (course > 23.0 && course <= 67.0) return "1";
    if (course > 67.0 && course <= 112.0) return "2";
    if (course > 112.0 && course <= 157.0) return "3";
    if (course > 157.0 && course <= 202.0) return "4";
    if (course > 202.0 && course <= 247.0) return "5";
    if (course > 247.0 && course <= 292.0) return "6";
    if (course > 292.0 && course <= 337.0) return "7";
    if ((course > 337.0 && course <= 360.0) || (course > 0.0 && course <= 23.0)) {
      return "8";
    }
    return "9";
  }
}
