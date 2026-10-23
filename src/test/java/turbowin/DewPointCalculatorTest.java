package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DewPointCalculatorTest {

  @Test
  public void calculatesDewPointForAwsExample() {
    double dewPoint = DewPointCalculator.calculate("16.7", 0.95, "1016.9");

    assertEquals(15.9681, dewPoint, 0.0001);
  }

  @Test
  public void formatsAwsExampleAsTheFm13DewPointGroup() {
    ImmtAirTemperatureCodeFormatter.Result code =
        ImmtAirTemperatureCodeFormatter.format(
            DewPointCalculator.calculate("16.7", 0.95, "1016.9"));

    assertEquals("0", code.signCode);
    assertEquals("160", code.temperatureCode);
  }

  @Test
  public void usesAirTemperatureAtSaturation() {
    assertEquals(0.0, DewPointCalculator.calculate("0.0", 1.0, "1013.25"), 0.0);
  }

  @Test
  public void returnsInvalidForMissingOrOutOfRangeValues() {
    assertEquals(main.INVALID, DewPointCalculator.calculate("", 0.95, "1013.25"), 0.0);
    assertEquals(main.INVALID, DewPointCalculator.calculate("16.7", 1.01, "1013.25"), 0.0);
    assertEquals(main.INVALID, DewPointCalculator.calculate("not-a-number", 0.95, "1013.25"), 0.0);
  }
}
