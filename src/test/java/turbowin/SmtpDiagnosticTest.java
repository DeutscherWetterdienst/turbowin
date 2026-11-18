package turbowin;

import static org.junit.Assert.assertEquals;

import jakarta.mail.MessagingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import org.junit.Test;

public class SmtpDiagnosticTest {

  @Test
  public void classifiesDnsAndConnectionFailures() {
    assertEquals(
        SmtpDiagnostic.Category.DNS,
        SmtpDiagnostic.classify(new UnknownHostException("mail.example")));
    assertEquals(
        SmtpDiagnostic.Category.CONNECTION,
        SmtpDiagnostic.classify(new ConnectException("Connection refused")));
  }

  @Test
  public void classifiesTlsAuthenticationAndRecipientFailures() {
    assertEquals(
        SmtpDiagnostic.Category.TLS_CERTIFICATE,
        SmtpDiagnostic.classify(new SSLException("PKIX path building failed")));
    assertEquals(
        SmtpDiagnostic.Category.AUTHENTICATION,
        SmtpDiagnostic.classify(new MessagingException("535 Authentication failed")));
    assertEquals(
        SmtpDiagnostic.Category.RECIPIENT_REJECTED,
        SmtpDiagnostic.classify(new MessagingException("550 No such recipient")));
  }

  @Test
  public void doesNotTreatPortNumbersAsSmtpStatusCodes() {
    assertEquals(
        "unknown", SmtpDiagnostic.extractStatus("Could not connect to SMTP host, port: 33811"));
    assertEquals("550", SmtpDiagnostic.extractStatus("550 No such recipient"));
    assertEquals("535", SmtpDiagnostic.extractStatus("SMTP error (535 Authentication failed)"));
    assertEquals("250", SmtpDiagnostic.extractStatus("250-PIPELINING"));
  }
}
