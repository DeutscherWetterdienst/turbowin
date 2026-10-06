package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PositionDistanceCalculatorTest {

  @Test
  public void returnsZeroForTheSamePosition() {
    assertEquals(
        0,
        PositionDistanceCalculator.calculate(
            52.5, 4.5, 52, 30, myposition.HEMISPHERE_NORTH, 4, 30, myposition.HEMISPHERE_EAST));
  }

  @Test
  public void calculatesApproximatelySixtyMilesPerDegreeOfLongitude() {
    int distance =
        PositionDistanceCalculator.calculate(
            0.0, 0.0, 0, 0, myposition.HEMISPHERE_NORTH, 1, 0, myposition.HEMISPHERE_EAST);

    assertTrue(distance >= 59 && distance <= 61);
  }

  @Test
  public void returnsMaximumDistanceWhenLongitudeDifferenceExceeds180Degrees() {
    assertEquals(
        Integer.MAX_VALUE,
        PositionDistanceCalculator.calculate(
            0.0, 0.0, 0, 0, myposition.HEMISPHERE_NORTH, 170, 0, myposition.HEMISPHERE_WEST));
  }

  @Test
  public void handlesThePorLongitudeWraparound() {
    int distance =
        PositionDistanceCalculator.calculate(
            0.0, -179.0, 0, 0, myposition.HEMISPHERE_NORTH, 179, 0, myposition.HEMISPHERE_EAST);

    assertTrue(distance >= 119 && distance <= 121);
  }
}
