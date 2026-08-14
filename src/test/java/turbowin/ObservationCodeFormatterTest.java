package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObservationCodeFormatterTest {

  @Test
  public void formatsDirectAndCombinedCodes() {
    assertEquals("12", ObservationCodeFormatter.direct("12", "/"));
    assertEquals("/", ObservationCodeFormatter.direct("", "/"));
    assertEquals("0123", ObservationCodeFormatter.combined("0", "123", "////"));
    assertEquals("////", ObservationCodeFormatter.combined("0", "", "////"));
  }

  @Test
  public void formatsIceAndWindCodes() {
    assertEquals("/", ObservationCodeFormatter.iceValue("u"));
    assertEquals("4", ObservationCodeFormatter.iceValue("4"));
    assertEquals("7", ObservationCodeFormatter.firstCharacter("7a", "/"));
    assertEquals("/", ObservationCodeFormatter.firstCharacter("", "/"));
    assertEquals("05 0012", ObservationCodeFormatter.windSpeed("05", "12", " "));
    assertEquals("//", ObservationCodeFormatter.windSpeed("", "12", " "));
  }
}
