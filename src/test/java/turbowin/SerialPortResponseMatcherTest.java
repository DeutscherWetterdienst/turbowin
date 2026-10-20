package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SerialPortResponseMatcherTest {

  @Test
  public void matchesMarkerInResponse() {
    assertTrue(SerialPortResponseMatcher.contains("HMP155 response", "HMP155"));
    assertFalse(SerialPortResponseMatcher.contains("PTB response", "HMP155"));
  }

  @Test
  public void handlesNullResponses() {
    assertFalse(SerialPortResponseMatcher.contains(null, "HMP155"));
    assertFalse(SerialPortResponseMatcher.containsAny(null, "HMP155", "PTB220"));
  }

  @Test
  public void matchesAnyMarker() {
    assertTrue(SerialPortResponseMatcher.containsAny("$GN", "$GP", "$GL", "$GN"));
  }
}
