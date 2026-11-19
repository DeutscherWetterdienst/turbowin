package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import org.junit.Test;

public class WowUploadGateTest {
  @Test
  public void allowsOneUploadPerScheduledMinuteAndAnotherAtTheNextInterval() {
    WowUploadGate gate = new WowUploadGate();
    Instant minute = Instant.parse("2026-11-18T18:01:00Z");
    int starts = 0;
    for (int second = 0; second < 60; second++) {
      if (gate.claim(minute.plusSeconds(second))) starts++;
    }
    assertEquals(1, starts);
    assertTrue(gate.claim(minute.plusSeconds(600)));
    assertFalse(gate.claim(minute));
  }
}
