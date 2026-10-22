package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DashboardPressureCalculatorTest {

  @Test
  public void roundsPressureAndAppliesCorrection() {
    assertEquals(1001.2, DashboardPressureCalculator.calculate("1000.04", "1.2"), 0.0);
  }

  @Test
  public void acceptsCorrectionAtTheLegacyBoundsByIgnoringIt() {
    assertEquals(1000.0, DashboardPressureCalculator.calculate("1000.04", "-4"), 0.0);
    assertEquals(1000.0, DashboardPressureCalculator.calculate("1000.04", "4"), 0.0);
  }

  @Test
  public void ignoresOutOfRangeCorrection() {
    assertEquals(1000.0, DashboardPressureCalculator.calculate("1000.04", "4.1"), 0.0);
  }

  @Test
  public void ignoresNaNCorrection() {
    assertEquals(1000.0, DashboardPressureCalculator.calculate("1000.04", "NaN"), 0.0);
  }

  @Test
  public void returnsNullForPressureOutsideExpectedRange() {
    assertNull(DashboardPressureCalculator.calculate("900.0", ""));
    assertNull(DashboardPressureCalculator.calculate("1100.0", ""));
  }

  @Test
  public void returnsNullForInvalidPressure() {
    assertNull(DashboardPressureCalculator.calculate("bad!!", ""));
  }
}
