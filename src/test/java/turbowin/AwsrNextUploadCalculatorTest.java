package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AwsrNextUploadCalculatorTest {

  @Test
  public void calculatesNextAlignedHour() {
    assertEquals(Integer.valueOf(6), AwsrNextUploadCalculator.nextUploadHour(5, 3));
  }

  @Test
  public void skipsToTheFollowingIntervalWhenAlreadyAligned() {
    assertEquals(Integer.valueOf(9), AwsrNextUploadCalculator.nextUploadHour(6, 3));
  }

  @Test
  public void allowsTheHourToCrossMidnight() {
    assertEquals(Integer.valueOf(24), AwsrNextUploadCalculator.nextUploadHour(23, 3));
  }

  @Test
  public void leavesStatusUnchangedForZeroInterval() {
    assertNull(AwsrNextUploadCalculator.nextUploadHour(5, 0));
  }

  @Test
  public void leavesStatusUnchangedForNegativeInterval() {
    assertNull(AwsrNextUploadCalculator.nextUploadHour(5, -3));
  }
}
