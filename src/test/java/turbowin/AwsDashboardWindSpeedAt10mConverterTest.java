package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AwsDashboardWindSpeedAt10mConverterTest {

  @Test
  public void convertsMetersPerSecondToKnots() {
    assertEquals(
        Integer.valueOf(19),
        AwsDashboardWindSpeedAt10mConverter.convert(10.0, main.M_S, main.KNOTS));
  }

  @Test
  public void preservesMetersPerSecondForMatchingUnits() {
    assertEquals(
        Integer.valueOf(11), AwsDashboardWindSpeedAt10mConverter.convert(10.6, main.M_S, main.M_S));
  }

  @Test
  public void preservesKnotsForMatchingUnits() {
    assertEquals(
        Integer.valueOf(11),
        AwsDashboardWindSpeedAt10mConverter.convert(10.6, main.KNOTS, main.KNOTS));
  }

  @Test
  public void preservesLegacyKnotsToMetersPerSecondConversion() {
    assertEquals(
        Integer.valueOf(19),
        AwsDashboardWindSpeedAt10mConverter.convert(10.0, main.KNOTS, main.M_S));
  }

  @Test
  public void returnsNullForUnsupportedUnitCombination() {
    assertNull(AwsDashboardWindSpeedAt10mConverter.convert(10.0, "", main.M_S));
  }
}
