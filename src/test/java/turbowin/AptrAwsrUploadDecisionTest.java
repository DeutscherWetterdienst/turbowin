package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AptrAwsrUploadDecisionTest {

  @Test
  public void cancelsLowSpeedPortModeUpload() {
    assertEquals(
        AptrAwsrUploadDecision.Action.CANCEL_LOW_SPEED,
        decide(true, 0.4, main.APTR_AWSR_SERVER, main.FORMAT_101, ""));
  }

  @Test
  public void cancelsUnavailableSpeedPortModeUpload() {
    assertEquals(
        AptrAwsrUploadDecision.Action.CANCEL_UNAVAILABLE_SPEED,
        decide(true, 101.0, main.APTR_AWSR_SERVER, main.FORMAT_101, ""));
  }

  @Test
  public void selectsServerFormat() {
    assertEquals(
        AptrAwsrUploadDecision.Action.SEND_FORMAT101_SERVER,
        decide(false, 2.0, main.APTR_AWSR_SERVER, main.FORMAT_101, ""));
    assertEquals(
        AptrAwsrUploadDecision.Action.SEND_FORMAT101_SERVER,
        decide(false, 2.0, main.APTR_AWSR_SERVER, main.FORMAT_AWS, main.UPLOADS_VIA_TURBOWIN));
    assertEquals(
        AptrAwsrUploadDecision.Action.SEND_FM13_SERVER,
        decide(false, 2.0, main.APTR_AWSR_SERVER, main.FORMAT_FM13, ""));
  }

  @Test
  public void selectsEmailOrReportsUnknownRouting() {
    assertEquals(
        AptrAwsrUploadDecision.Action.SEND_EMAIL,
        decide(false, 2.0, main.APTR_AWSR_CUSTOM_MAIL, main.FORMAT_101, ""));
    assertEquals(
        AptrAwsrUploadDecision.Action.UNKNOWN_FORMAT,
        decide(false, 2.0, main.APTR_AWSR_SERVER, "unknown", ""));
    assertEquals(
        AptrAwsrUploadDecision.Action.UNKNOWN_SEND_METHOD,
        decide(false, 2.0, "unknown", main.FORMAT_101, ""));
  }

  private static AptrAwsrUploadDecision.Action decide(
      boolean portMode, double speed, String sendMethod, String format, String uploadsMethod) {
    return AptrAwsrUploadDecision.decide(portMode, speed, sendMethod, format, uploadsMethod);
  }
}
