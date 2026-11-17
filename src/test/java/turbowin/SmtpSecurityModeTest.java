package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SmtpSecurityModeTest {

  @Test
  public void migratesLegacyModes() {
    assertEquals(SmtpSecurityMode.PLAIN, SmtpSecurityMode.normalize(main.CUSTOM_TLS, "25"));
    assertEquals(
        SmtpSecurityMode.STARTTLS, SmtpSecurityMode.normalize(main.CUSTOM_TLS_STARTTLS, "587"));
    assertEquals(SmtpSecurityMode.IMPLICIT_TLS, SmtpSecurityMode.normalize(main.CUSTOM_SSL, "465"));
  }

  @Test
  public void migratesAmbiguousLegacyCombinationFromPort() {
    assertEquals(
        SmtpSecurityMode.STARTTLS, SmtpSecurityMode.normalize(main.CUSTOM_SSL_STARTTLS, "587"));
    assertEquals(
        SmtpSecurityMode.IMPLICIT_TLS, SmtpSecurityMode.normalize(main.CUSTOM_SSL_STARTTLS, "465"));
    assertEquals("", SmtpSecurityMode.normalize(main.CUSTOM_SSL_STARTTLS, "25"));
  }
}
