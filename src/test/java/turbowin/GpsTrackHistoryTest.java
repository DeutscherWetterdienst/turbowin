package turbowin;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class GpsTrackHistoryTest {

  @Test
  public void storesNewestPositionAndRetrievesMatchingInterval() {
    GpsTrackHistory history = new GpsTrackHistory(3);
    history.store(10.0, 20.0, 100);
    history.store(11.0, 21.0, 110);

    assertArrayEquals(new double[] {10.0, 20.0, 11.0, 21.0}, history.retrieve(10), 0.0);
  }

  @Test
  public void returnsSentinelsWhenNoIntervalMatches() {
    GpsTrackHistory history = new GpsTrackHistory(3);
    history.store(11.0, 21.0, 110);

    assertArrayEquals(
        new double[] {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE},
        history.retrieve(10),
        0.0);
  }

  @Test
  public void ignoresInvalidPositionsAndTimes() {
    GpsTrackHistory history = new GpsTrackHistory(3);
    history.store(Double.MAX_VALUE, 21.0, 110);
    history.store(11.0, 21.0, Long.MAX_VALUE);

    assertArrayEquals(
        new double[] {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE},
        history.retrieve(10),
        0.0);
  }
}
