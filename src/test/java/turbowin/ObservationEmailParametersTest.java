package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObservationEmailParametersTest {

  @Test
  public void usesNullMarkersRequiredByThePythonModuleForMissingOptionalValues() {
    ObservationEmailParameters parameters =
        ObservationEmailParameters.forCustomSettings(
            "tls",
            "smtp.example.org",
            "encrypted",
            "to@example.org",
            "from@example.org",
            "subject",
            "cc",
            "587",
            false);

    assertEquals("tls", parameters.smtpMode());
    assertEquals("smtp.example.org", parameters.smtpHost());
    assertEquals("encrypted", parameters.smtpPassword());
    assertEquals("to@example.org", parameters.recipient());
    assertEquals("from@example.org", parameters.sender());
    assertEquals("subject", parameters.subject());
    assertEquals("null", parameters.cc());
    assertEquals("587", parameters.port());
    assertEquals("null", parameters.attachment());
  }

  @Test
  public void preservesCcAndMarksRequestedAttachment() {
    ObservationEmailParameters parameters =
        ObservationEmailParameters.forCustomSettings(
            "ssl",
            "smtp.example.org",
            "encrypted",
            "to@example.org",
            "from@example.org",
            "subject",
            "cc@example.org",
            "465",
            true);

    assertEquals("cc@example.org", parameters.cc());
    assertEquals("yes", parameters.attachment());
  }

  @Test
  public void preservesTheLegacyCcLengthCutoff() {
    assertEquals(
        "null",
        ObservationEmailParameters.forCustomSettings(
                "", "", "", "", "", "", "123", "", false)
            .cc());
    assertEquals(
        "1234",
        ObservationEmailParameters.forCustomSettings(
                "", "", "", "", "", "", "1234", "", false)
            .cc());
  }
}
