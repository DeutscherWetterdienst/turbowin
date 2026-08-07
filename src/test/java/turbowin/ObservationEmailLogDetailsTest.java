package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObservationEmailLogDetailsTest {

  @Test
  public void replacesNullMarkersWithReadableLogValues() {
    ObservationEmailLogDetails.Details details =
        ObservationEmailLogDetails.from("null", "null", "null");

    assertEquals("none", details.cc());
    assertEquals("system defined", details.port());
    assertEquals("none", details.attachment());
  }

  @Test
  public void buildsTheTransportSpecificLogMessage() {
    ObservationEmailLogDetails.Details details =
        ObservationEmailLogDetails.from("cc@example.org", "587", "yes");

    assertEquals(
        "[EMAIL] trying to send obs (body= \"observation\") to to@example.org cc "
            + "cc@example.org from from@example.org via tls port 587 attachment yes [primary email "
            + "module, virtual thread]",
        ObservationEmailLogDetails.message(
            "observation",
            "to@example.org",
            details,
            "from@example.org",
            "tls",
            "primary email module"));
  }
}
