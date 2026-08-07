package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UrlUtilsTest {

  @Test
  public void encodesPunctuationUsingLegacyCharacterCodes() {
    assertEquals("%21%3F%26%3D%25", UrlUtils.urlEncode("!?&=%"));
  }

  @Test
  public void encodesMailtoLineBreaksAndSpaces() {
    assertEquals(
        "Observation%3A%2012%B0C%20clear%0ANext", UrlUtils.urlEncode("Observation: 12°C clear\nNext"));
  }

  @Test
  public void preservesLettersFromNonAsciiInput() {
    assertEquals("café", UrlUtils.urlEncode("café"));
  }
}
