package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObservationEmailRequestTest {

  @Test
  public void factoryPreservesAllTransportArguments() {
    ObservationEmailRequest request =
        ObservationEmailRequest.from(
            "tls",
            "smtp.example.org",
            "password",
            "to",
            "from",
            "subject",
            "body",
            "cc",
            "587",
            "yes");

    assertEquals("tls", request.smtpMode());
    assertEquals("smtp.example.org", request.smtpHost());
    assertEquals("password", request.password());
    assertEquals("to", request.recipient());
    assertEquals("from", request.sender());
    assertEquals("subject", request.subject());
    assertEquals("body", request.body());
    assertEquals("cc", request.cc());
    assertEquals("587", request.port());
    assertEquals("yes", request.attachment());
  }
}
