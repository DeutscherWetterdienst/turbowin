package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ImmtMovementCodeCalculatorTest {

  @Test
  public void mapsSpeedBoundaries() {
    assertEquals("0", ImmtMovementCodeCalculator.speedCode(0.9));
    assertEquals("1", ImmtMovementCodeCalculator.speedCode(1.0));
    assertEquals("2", ImmtMovementCodeCalculator.speedCode(5.1));
    assertEquals("8", ImmtMovementCodeCalculator.speedCode(40.0));
    assertEquals("9", ImmtMovementCodeCalculator.speedCode(40.1));
    assertNull(ImmtMovementCodeCalculator.speedCode(100.0));
  }

  @Test
  public void mapsDirectionBoundariesAndStationarySpeed() {
    assertEquals("0", ImmtMovementCodeCalculator.directionCode(0.5, 180.0));
    assertEquals("8", ImmtMovementCodeCalculator.directionCode(1.0, 23.0));
    assertEquals("1", ImmtMovementCodeCalculator.directionCode(1.0, 23.1));
    assertEquals("2", ImmtMovementCodeCalculator.directionCode(1.0, 67.1));
    assertEquals("8", ImmtMovementCodeCalculator.directionCode(1.0, 360.0));
    assertEquals("9", ImmtMovementCodeCalculator.directionCode(1.0, -1.0));
    assertNull(ImmtMovementCodeCalculator.directionCode(1_000_000.0, 90.0));
    assertNull(ImmtMovementCodeCalculator.directionCode(Double.NaN, 90.0));
  }
}
