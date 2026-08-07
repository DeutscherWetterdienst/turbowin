package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObservationEmailModeResolverTest {

  private static final String LOCAL = "local";
  private static final String GMAIL = "gmail";
  private static final String YAHOO = "yahoo";
  private static final String CUSTOM = "custom";
  private static final String AUTOMATIC_SMTP_HOST = main.APTR_AWSR_SMTP_HOST;
  private static final String AUTOMATIC_GMAIL = main.APTR_AWSR_GMAIL;
  private static final String AUTOMATIC_YAHOO = main.APTR_AWSR_YAHOO_MAIL;
  private static final String AUTOMATIC_CUSTOM = main.APTR_AWSR_CUSTOM_MAIL;

  @Test
  public void classifiesManualCustomMode() {
    assertEquals(
        ObservationEmailModeResolver.Mode.CUSTOM,
        ObservationEmailModeResolver.resolve(
            true,
            CUSTOM,
            "automatic",
            LOCAL,
            GMAIL,
            YAHOO,
            CUSTOM,
            AUTOMATIC_SMTP_HOST,
            AUTOMATIC_GMAIL,
            AUTOMATIC_YAHOO,
            AUTOMATIC_CUSTOM));
  }

  @Test
  public void classifiesAutomaticDisabledMode() {
    assertEquals(
        ObservationEmailModeResolver.Mode.DISABLED,
        ObservationEmailModeResolver.resolve(
            false,
            CUSTOM,
            AUTOMATIC_GMAIL,
            LOCAL,
            GMAIL,
            YAHOO,
            CUSTOM,
            AUTOMATIC_SMTP_HOST,
            AUTOMATIC_GMAIL,
            AUTOMATIC_YAHOO,
            AUTOMATIC_CUSTOM));
  }

  @Test
  public void classifiesAutomaticCustomMode() {
    assertEquals(
        ObservationEmailModeResolver.Mode.CUSTOM,
        ObservationEmailModeResolver.resolve(
            false,
            "manual",
            AUTOMATIC_CUSTOM,
            LOCAL,
            GMAIL,
            YAHOO,
            CUSTOM,
            AUTOMATIC_SMTP_HOST,
            AUTOMATIC_GMAIL,
            AUTOMATIC_YAHOO,
            AUTOMATIC_CUSTOM));
  }

  @Test
  public void classifiesUnknownModeAsInvalid() {
    assertEquals(
        ObservationEmailModeResolver.Mode.INVALID,
        ObservationEmailModeResolver.resolve(
            true,
            "unknown",
            CUSTOM,
            LOCAL,
            GMAIL,
            YAHOO,
            CUSTOM,
            AUTOMATIC_SMTP_HOST,
            AUTOMATIC_GMAIL,
            AUTOMATIC_YAHOO,
            AUTOMATIC_CUSTOM));
  }
}
