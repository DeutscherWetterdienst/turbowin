package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AwsrScheduleDecisionTest {

  private static final int INTERVAL = 3;
  private static final int NO_INTERNET = 700;
  private static final int RETRY_MINUTES = 3;

  @Test
  public void recognizesScheduledUpload() {
    assertTrue(AwsrScheduleDecision.isScheduledUpload(6, 0, INTERVAL));
    assertFalse(AwsrScheduleDecision.isScheduledUpload(7, 0, INTERVAL));
    assertFalse(AwsrScheduleDecision.isScheduledUpload(6, 1, INTERVAL));
  }

  @Test
  public void recognizesRetryUpload() {
    assertTrue(
        AwsrScheduleDecision.isRetryUpload(
            NO_INTERNET, NO_INTERNET, 6, RETRY_MINUTES, INTERVAL, RETRY_MINUTES));
    assertFalse(
        AwsrScheduleDecision.isRetryUpload(
            0, NO_INTERNET, 6, RETRY_MINUTES, INTERVAL, RETRY_MINUTES));
  }

  @Test
  public void recognizesPendingRetryWindow() {
    assertTrue(
        AwsrScheduleDecision.isRetryPending(
            NO_INTERNET, NO_INTERNET, 6, 2, INTERVAL, RETRY_MINUTES));
    assertFalse(
        AwsrScheduleDecision.isRetryPending(
            NO_INTERNET, NO_INTERNET, 6, RETRY_MINUTES, INTERVAL, RETRY_MINUTES));
  }
}
