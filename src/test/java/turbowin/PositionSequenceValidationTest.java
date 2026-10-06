package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PositionSequenceValidationTest {

  @Test
  public void acceptsDistanceAtEachTimeWindowBoundary() {
    assertFalse(PositionSequenceValidation.exceedsAllowedDistance(0, 180));
    assertFalse(PositionSequenceValidation.exceedsAllowedDistance(6, 180));
    assertFalse(PositionSequenceValidation.exceedsAllowedDistance(12, 360));
    assertFalse(PositionSequenceValidation.exceedsAllowedDistance(18, 540));
    assertFalse(PositionSequenceValidation.exceedsAllowedDistance(24, 720));
  }

  @Test
  public void rejectsDistanceJustAboveEachTimeWindowBoundary() {
    assertTrue(PositionSequenceValidation.exceedsAllowedDistance(6, 181));
    assertTrue(PositionSequenceValidation.exceedsAllowedDistance(12, 361));
    assertTrue(PositionSequenceValidation.exceedsAllowedDistance(18, 541));
    assertTrue(PositionSequenceValidation.exceedsAllowedDistance(24, 721));
  }

  @Test
  public void acceptsElapsedTimesOutsideTheConfiguredWindows() {
    assertFalse(PositionSequenceValidation.exceedsAllowedDistance(-1, Integer.MAX_VALUE));
    assertFalse(PositionSequenceValidation.exceedsAllowedDistance(25, Integer.MAX_VALUE));
  }
}
