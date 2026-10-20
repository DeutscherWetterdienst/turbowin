package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AptrAwsrSendMethodNormalizerTest {

  @Test
  public void clearsLegacySendMethods() {
    assertEquals("", AptrAwsrSendMethodNormalizer.normalize(main.APTR_AWSR_SMTP_HOST));
    assertEquals("", AptrAwsrSendMethodNormalizer.normalize(main.APTR_AWSR_GMAIL));
    assertEquals("", AptrAwsrSendMethodNormalizer.normalize(main.APTR_AWSR_YAHOO_MAIL));
  }

  @Test
  public void preservesCurrentAndUnknownSendMethods() {
    assertEquals(
        main.APTR_AWSR_SERVER, AptrAwsrSendMethodNormalizer.normalize(main.APTR_AWSR_SERVER));
    assertEquals("unknown", AptrAwsrSendMethodNormalizer.normalize("unknown"));
  }

  @Test(expected = NullPointerException.class)
  public void preservesNullFailureOfLegacyNormalization() {
    AptrAwsrSendMethodNormalizer.normalize(null);
  }
}
