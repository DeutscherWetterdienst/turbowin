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
            52.5,
            4.5,
            52,
            30,
            PositionModel.HEMISPHERE_NORTH,
            4,
            30,
            PositionModel.HEMISPHERE_EAST));
  }

  @Test
  public void calculatesApproximatelySixtyMilesPerDegreeOfLongitude() {
    int distance =
        PositionDistanceCalculator.calculate(
            0.0, 0.0, 0, 0, PositionModel.HEMISPHERE_NORTH, 1, 0, PositionModel.HEMISPHERE_EAST);

    assertTrue(distance >= 59 && distance <= 61);
  }

  @Test
  public void returnsMaximumDistanceWhenLongitudeDifferenceExceeds180Degrees() {
    assertEquals(
        Integer.MAX_VALUE,
        PositionDistanceCalculator.calculate(
            0.0, 0.0, 0, 0, PositionModel.HEMISPHERE_NORTH, 170, 0, PositionModel.HEMISPHERE_WEST));
  }

  @Test
  public void handlesThePorLongitudeWraparound() {
    int distance =
        PositionDistanceCalculator.calculate(
            0.0,
            -179.0,
            0,
            0,
            PositionModel.HEMISPHERE_NORTH,
            179,
            0,
            PositionModel.HEMISPHERE_EAST);

    assertTrue(distance >= 119 && distance <= 121);
  }
}
