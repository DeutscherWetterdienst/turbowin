package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObservationEmailComposerTest {

  @Test
  public void includesCcWhenItIsLongerThanTheLegacyThreshold() {
    assertEquals(
        "recipient@example.com?cc=copy@example.com&subject=Subject&body=Observation",
        ObservationEmailComposer.buildMailText(
            "recipient@example.com", "copy@example.com", "Subject", "Observation"));
  }

  @Test
  public void omitsShortCcValues() {
    assertEquals(
        "recipient@example.com?subject=Subject&body=Observation",
        ObservationEmailComposer.buildMailText(
            "recipient@example.com", "", "Subject", "Observation"));
  }

  @Test
  public void omitsCcAtTheThreeCharacterThreshold() {
    assertEquals(
        "recipient@example.com?subject=Subject&body=Observation",
        ObservationEmailComposer.buildMailText(
            "recipient@example.com", "123", "Subject", "Observation"));
  }

  @Test
  public void includesCcAtFourCharacters() {
    assertEquals(
        "recipient@example.com?cc=1234&subject=Subject&body=Observation",
        ObservationEmailComposer.buildMailText(
            "recipient@example.com", "1234", "Subject", "Observation"));
  }
}
