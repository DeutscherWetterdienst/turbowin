package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LandSeaMaskPositionCalculatorTest {

  @Test
  public void calculatesNorthernWesternGridCoordinates() {
    LandSeaMaskPositionCalculator.Position position =
        LandSeaMaskPositionCalculator.calculate(
            myposition.HEMISPHERE_NORTH, myposition.HEMISPHERE_WEST, 12, 34);

    assertEquals(0, position.octant());
    assertEquals(13, position.tenDegreeCell());
    assertEquals(31, position.samIndex());
  }

  @Test
  public void calculatesAllHemisphereOctants() {
    assertEquals(
        3,
        LandSeaMaskPositionCalculator.calculate(
                myposition.HEMISPHERE_NORTH, myposition.HEMISPHERE_EAST, 0, 89)
            .octant());
    assertEquals(
        6,
        LandSeaMaskPositionCalculator.calculate(
                myposition.HEMISPHERE_SOUTH, myposition.HEMISPHERE_WEST, 0, 90)
            .octant());
    assertEquals(
        7,
        LandSeaMaskPositionCalculator.calculate(
                myposition.HEMISPHERE_SOUTH, myposition.HEMISPHERE_EAST, 0, 90)
            .octant());
  }

  @Test
  public void preservesTheInvalidOctantForOutOfRangeLongitudes() {
    assertEquals(
        main.INVALID,
        LandSeaMaskPositionCalculator.calculate(
                myposition.HEMISPHERE_NORTH, myposition.HEMISPHERE_EAST, 12, 181)
            .octant());
  }
}
