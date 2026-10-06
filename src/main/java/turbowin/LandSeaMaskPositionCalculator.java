package turbowin;

/** Calculates the grid coordinates used to look up a position in the SAM mask. */
final class LandSeaMaskPositionCalculator {

  private LandSeaMaskPositionCalculator() {}

  static Position calculate(
      String latitudeHemisphere,
      String longitudeHemisphere,
      int latitudeDegrees,
      int longitudeDegrees) {
    // Octants follow WMO code table 0371: hemisphere and 90-degree longitude bands select the
    // SAM mask quadrant.
    int octant = main.INVALID;

    if (latitudeHemisphere.equals(myposition.HEMISPHERE_NORTH)
        && longitudeHemisphere.equals(myposition.HEMISPHERE_WEST)) {
      if (longitudeDegrees >= 0 && longitudeDegrees < 90) {
        octant = 0;
      } else if (longitudeDegrees >= 90 && longitudeDegrees <= 180) {
        octant = 1;
      }
    } else if (latitudeHemisphere.equals(myposition.HEMISPHERE_NORTH)
        && longitudeHemisphere.equals(myposition.HEMISPHERE_EAST)) {
      if (longitudeDegrees >= 90 && longitudeDegrees <= 180) {
        octant = 2;
      } else if (longitudeDegrees >= 0 && longitudeDegrees < 90) {
        octant = 3;
      }
    } else if (latitudeHemisphere.equals(myposition.HEMISPHERE_SOUTH)
        && longitudeHemisphere.equals(myposition.HEMISPHERE_WEST)) {
      if (longitudeDegrees >= 0 && longitudeDegrees < 90) {
        octant = 5;
      } else if (longitudeDegrees >= 90 && longitudeDegrees <= 180) {
        octant = 6;
      }
    } else if (latitudeHemisphere.equals(myposition.HEMISPHERE_SOUTH)
        && longitudeHemisphere.equals(myposition.HEMISPHERE_EAST)) {
      if (longitudeDegrees >= 90 && longitudeDegrees <= 180) {
        octant = 7;
      } else if (longitudeDegrees >= 0 && longitudeDegrees < 90) {
        octant = 8;
      }
    }

    // For IMMT LaLaLa/LoLoLoLo, tens digits select the 10-degree cell and units digits select the
    // one-degree position within that cell.
    int latitudeTenDegrees = latitudeDegrees / 10;
    int latitudeOneDegree = latitudeDegrees % 10;
    int longitudeTenDegrees = (longitudeDegrees / 10) % 10;
    int longitudeOneDegree = longitudeDegrees % 10;
    int tenDegreeCell = (octant * 100) + (latitudeTenDegrees * 10) + longitudeTenDegrees;
    int samIndex = 5 + (11 * latitudeOneDegree) + longitudeOneDegree;

    return new Position(octant, tenDegreeCell, samIndex);
  }

  record Position(int octant, int tenDegreeCell, int samIndex) {}
}
