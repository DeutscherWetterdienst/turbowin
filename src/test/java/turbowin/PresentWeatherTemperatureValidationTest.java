package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PresentWeatherTemperatureValidationTest {

  private static final Integer[] DRIFTING_OR_BLOWING_SNOW = {36, 37, 38, 39};
  private static final Integer[] DEPOSITING_RIME = {48, 49};
  private static final Integer[] FREEZING_DRIZZLE = {56, 57};
  private static final Integer[] FREEZING_RAIN = {66, 67};
  private static final Integer[] SNOW = {68, 69};
  private static final Integer[] SNOW_FLAKES = {70, 71, 72, 73, 74, 75};
  private static final Integer[] SNOW_GRAINS_OR_CRYSTALS = {76, 77, 78, 79};
  private static final Integer[] SNOW_SHOWERS = {83, 84, 85, 86};

  @Test
  public void acceptsRestrictedWeatherAtTheTwentyDegreeBoundary() {
    assertTrue(validate(true, 20.0f, 36));
  }

  @Test
  public void acceptsRestrictedWeatherWhenTemperatureConversionFails() {
    assertTrue(validate(false, 20.1f, 36));
  }

  @Test
  public void acceptsRestrictedWeatherAtTheUpperSentinelBoundary() {
    assertTrue(validate(true, 99.9f, 36));
  }

  @Test
  public void acceptsUnrelatedWeatherAtHighTemperature() {
    assertTrue(validate(true, 20.1f, 1));
  }

  private boolean validate(boolean temperatureValid, float temperature, int weatherCode) {
    return PresentWeatherTemperatureValidation.validate(
        temperatureValid,
        temperature,
        weatherCode,
        DRIFTING_OR_BLOWING_SNOW,
        DEPOSITING_RIME,
        FREEZING_DRIZZLE,
        FREEZING_RAIN,
        SNOW,
        SNOW_FLAKES,
        SNOW_GRAINS_OR_CRYSTALS,
        SNOW_SHOWERS);
  }
}
