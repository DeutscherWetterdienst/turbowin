package turbowin;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import org.junit.Test;

public class AprUploadSchedulingTest {
  @Test
  public void highFrequencyRecordsStartOnlyOneUploadAndOneRetry() {
    AwsrUploadGate gate = new AwsrUploadGate();
    Instant hour = Instant.parse("2026-11-18T18:00:00Z");
    int scheduled = 0;
    int retries = 0;
    for (int second = 0; second < 60; second++) {
      if (gate.claim(hour.plusSeconds(second), false)) scheduled++;
      if (gate.claim(hour.plusSeconds(180 + second), true)) retries++;
    }
    assertEquals(1, scheduled);
    assertEquals(1, retries);
    // A late fallback for the same hour must not start another ordinary upload.
    assertEquals(false, gate.claim(hour.plusSeconds(60), false));
    assertEquals(true, gate.claim(hour.plusSeconds(3600), false));
  }
}
