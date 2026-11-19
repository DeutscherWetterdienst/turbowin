package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class AwsrUploadGateTest {
  @Test
  public void sendsOnceForSixtyRecordsAndAgainInNextScheduledHour() {
    AwsrUploadGate gate = new AwsrUploadGate();
    Instant hour = Instant.parse("2026-11-18T18:00:00Z");
    int sends = 0;
    for (int second = 0; second < 60; second++) {
      if (gate.claim(hour.plusSeconds(second), false)) {
        sends++;
      }
    }
    assertEquals(1, sends);
    assertTrue(gate.claim(hour.plusSeconds(3600), false));
    assertFalse(gate.claim(hour, false)); // A backwards clock adjustment must not resend.
  }

  @Test
  public void allowsOneSeparateRetryForTheScheduledHour() {
    AwsrUploadGate gate = new AwsrUploadGate();
    Instant hour = Instant.parse("2026-11-18T18:00:00Z");
    assertTrue(gate.claim(hour, false));
    assertTrue(gate.claim(hour.plusSeconds(300), true));
    assertFalse(gate.claim(hour.plusSeconds(301), true));
  }

  @Test
  public void invalidDataReleasesReservationWithoutReleasingAnOlderHour() {
    AwsrUploadGate gate = new AwsrUploadGate();
    Instant hour = Instant.parse("2026-11-18T18:00:00Z");
    assertTrue(gate.claim(hour, false));
    Instant next = hour.plusSeconds(3600);
    assertTrue(gate.claim(next, false));
    gate.release(next, false);
    assertFalse(gate.claim(hour, false));
    assertTrue(gate.claim(next, false));
  }

  @Test
  public void concurrentRecordsOnlyClaimOneDispatch() throws Exception {
    AwsrUploadGate gate = new AwsrUploadGate();
    Instant hour = Instant.parse("2026-11-18T18:00:00Z");
    AtomicInteger sends = new AtomicInteger();
    try (var workers = Executors.newFixedThreadPool(8)) {
      for (int i = 0; i < 60; i++) {
        workers.submit(() -> {
          if (gate.claim(hour, false)) sends.incrementAndGet();
        });
      }
      workers.shutdown();
      assertTrue(workers.awaitTermination(5, TimeUnit.SECONDS));
    }
    assertEquals(1, sends.get());
  }
}
