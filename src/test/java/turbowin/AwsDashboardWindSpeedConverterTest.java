package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AwsDashboardWindSpeedConverterTest {

  @Test
  public void convertsMetersPerSecondToKnots() {
    assertEquals(
        Integer.valueOf(19),
        AwsDashboardWindSpeedConverter.convert(10.0, 10, main.M_S, main.KNOTS));
  }

  @Test
  public void convertsKnotsToMetersPerSecond() {
    assertEquals(
        Integer.valueOf(5), AwsDashboardWindSpeedConverter.convert(10.0, 10, main.KNOTS, main.M_S));
  }

  @Test
  public void preservesMeasuredRoundedValueForMatchingUnits() {
    assertEquals(
        Integer.valueOf(11), AwsDashboardWindSpeedConverter.convert(10.6, 11, main.M_S, main.M_S));
    assertEquals(
        Integer.valueOf(11),
        AwsDashboardWindSpeedConverter.convert(10.6, 11, main.KNOTS, main.KNOTS));
  }

  @Test
  public void returnsNullForUnsupportedUnitCombination() {
    assertNull(AwsDashboardWindSpeedConverter.convert(10.0, 10, "", main.M_S));
  }
}
