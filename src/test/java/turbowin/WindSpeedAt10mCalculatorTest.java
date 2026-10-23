package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WindSpeedAt10mCalculatorTest {

  @Test
  public void convertsValidWindSpeedAtValidHeight() {
    double expected = 10.0 * Math.log(10 / 0.0002) / Math.log(20 / 0.0002);

    assertEquals(expected, WindSpeedAt10mCalculator.calculate("20", 10.0), 0.0);
  }

  @Test
  public void rejectsHeightAtBounds() {
    assertEquals(main.INVALID, WindSpeedAt10mCalculator.calculate("10", 10.0), 0.0);
    assertEquals(main.INVALID, WindSpeedAt10mCalculator.calculate("200", 10.0), 0.0);
  }

  @Test
  public void rejectsMissingOrMalformedHeight() {
    assertEquals(main.INVALID, WindSpeedAt10mCalculator.calculate("", 10.0), 0.0);
    assertEquals(main.INVALID, WindSpeedAt10mCalculator.calculate("not-a-number", 10.0), 0.0);
  }

  @Test
  public void rejectsWindSpeedAtOrOutsideBounds() {
    assertEquals(main.INVALID, WindSpeedAt10mCalculator.calculate("20", -0.1), 0.0);
    assertEquals(main.INVALID, WindSpeedAt10mCalculator.calculate("20", 500.0), 0.0);
  }

  @Test
  public void preservesLegacyWhitespaceParsingBehavior() {
    assertEquals(main.INVALID, WindSpeedAt10mCalculator.calculate(" 20 ", 10.0), 0.0);
  }
}
