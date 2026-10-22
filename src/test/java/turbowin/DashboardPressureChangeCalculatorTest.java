package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DashboardPressureChangeCalculatorTest {

  @Test
  public void roundsValidPressureChange() {
    assertEquals(2.2, DashboardPressureChangeCalculator.calculate("2.19"), 0.0);
  }

  @Test
  public void acceptsPressureChangeAtTheUpperLimit() {
    assertEquals(100.0, DashboardPressureChangeCalculator.calculate("100"), 0.0);
  }

  @Test
  public void invalidatesPressureChangeAboveTheUpperLimit() {
    assertEquals(Double.MAX_VALUE, DashboardPressureChangeCalculator.calculate("100.1"), 0.0);
  }

  @Test
  public void invalidatesMalformedPressureChange() {
    assertEquals(Double.MAX_VALUE, DashboardPressureChangeCalculator.calculate("bad!!"), 0.0);
  }

  @Test
  public void returnsNullWhenPressureChangeIsMissingOrMarkedInvalid() {
    assertNull(DashboardPressureChangeCalculator.calculate(""));
    assertNull(DashboardPressureChangeCalculator.calculate("2.19*"));
  }
}
