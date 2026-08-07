package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ServerObservationRequestBuilderTest {

  @Test
  public void keepsTheExistingFm13PayloadFormat() {
    assertEquals(
        "https://example.test/upload?obs=BBXX TEST 0112",
        ServerObservationRequestBuilder.fm13Url("https://example.test/upload?", "BBXX TEST 0112"));
  }

  @Test
  public void encodesTheFormat101PayloadAsUtf8FormData() {
    assertEquals(
        "https://example.test/upload?obs=BBXX+TEST%2B%C3%A9",
        ServerObservationRequestBuilder.format101Url(
            "https://example.test/upload?", "BBXX TEST+é"));
  }
}
