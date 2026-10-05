package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LatestObservationFormatterTest {

  @Test
  public void formatsPressureUsingImmtLeadingOneRule() {
    assertEquals("991.7 hPa", LatestObservationFormatter.pressure("9917"));
    assertEquals("1007.8 hPa", LatestObservationFormatter.pressure("0078"));
    assertEquals("-", LatestObservationFormatter.pressure("invalid"));
  }

  @Test
  public void formatsCelsiusAndOptionalUsTemperature() {
    assertEquals("12.3 °C", LatestObservationFormatter.temperature("0", "123", false));
    assertEquals("-12.3 °C", LatestObservationFormatter.temperature("1", "123", false));
    assertEquals("12.3 °C / 54.1 °F", LatestObservationFormatter.temperature("0", "123", true));
  }

  @Test
  public void formatsSeaSurfaceTemperatureLikeAirTemperature() {
    assertEquals(
        "-4.5 °C / 23.9 °F", LatestObservationFormatter.seaSurfaceTemperature("1", "045", true));
  }

  @Test
  public void convertsWaveAndSwellHeightsFromHalfMeters() {
    assertEquals("1.5", LatestObservationFormatter.waveHeight("3"));
    assertEquals("-", LatestObservationFormatter.waveHeight("invalid"));
    assertEquals("2.0", LatestObservationFormatter.swellHeight("4"));
  }
}
