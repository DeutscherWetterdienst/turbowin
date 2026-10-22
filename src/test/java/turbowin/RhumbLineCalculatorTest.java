package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RhumbLineCalculatorTest {

  @Test
  public void calculatesNorthwardCourse() {
    RhumbLineCalculator.Result result = RhumbLineCalculator.calculate(50, 10, 51, 10);

    assertEquals(0.0, result.courseDegrees, 0.000001);
    assertEquals(111194.926, result.distanceMeters, 0.1);
  }

  @Test
  public void calculatesStationaryPosition() {
    RhumbLineCalculator.Result result = RhumbLineCalculator.calculate(50, 10, 50, 10);

    assertEquals(0.0, result.distanceMeters, 0.000001);
    assertEquals(0.0, result.courseDegrees, 0.000001);
  }

  @Test
  public void usesShorterAntiMeridianCourse() {
    RhumbLineCalculator.Result result = RhumbLineCalculator.calculate(0, 179, 0, -179);

    assertEquals(90.0, result.courseDegrees, 0.000001);
    assertEquals(222389.853, result.distanceMeters, 0.1);
  }
}
