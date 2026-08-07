package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObservationEmailTransportExecutorTest {

  @Test
  public void executesTheSelectedSenderAndReturnsItsStatus() throws Exception {
    ObservationEmailRequest request =
        new ObservationEmailRequest(
            "tls",
            "smtp.example.org",
            "password",
            "to",
            "from",
            "subject",
            "body",
            "null",
            "587",
            "null");

    int status = ObservationEmailTransportExecutor.execute(r -> r.body().length(), request);

    assertEquals(4, status);
  }
}
