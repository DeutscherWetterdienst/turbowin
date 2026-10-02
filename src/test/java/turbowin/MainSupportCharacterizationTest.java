package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MainSupportCharacterizationTest {

  @Test
  public void acceptsInvalidWindWaveValuesAsConversionFailuresAtLevelTwo() {
    String originalPeriod = mywaves.wind_waves_period;
    String originalHeight = mywaves.wind_waves_height;
    int originalWindSpeed = mywind.int_true_wind_speed;
    String originalWindUnits = main.wind_units;

    try {
      mywaves.wind_waves_period = "invalid";
      mywaves.wind_waves_height = "invalid";
      mywind.int_true_wind_speed = 10;
      main.wind_units = main.M_S;

      assertTrue(new main_support().checking_level_2());
    } finally {
      mywaves.wind_waves_period = originalPeriod;
      mywaves.wind_waves_height = originalHeight;
      mywind.int_true_wind_speed = originalWindSpeed;
      main.wind_units = originalWindUnits;
    }
  }

  @Test
  public void acceptsInvalidWindWaveValuesAsConversionFailuresAtLevelThree() {
    String originalPeriod = mywaves.wind_waves_period;
    String originalHeight = mywaves.wind_waves_height;

    try {
      mywaves.wind_waves_period = "invalid";
      mywaves.wind_waves_height = "invalid";

      assertTrue(new main_support().checking_level_3());
    } finally {
      mywaves.wind_waves_period = originalPeriod;
      mywaves.wind_waves_height = originalHeight;
    }
  }

  @Test
  public void acceptsValidWindWaveValuesAtLevelTwo() {
    String originalPeriod = mywaves.wind_waves_period;
    String originalHeight = mywaves.wind_waves_height;
    int originalWindSpeed = mywind.int_true_wind_speed;
    String originalWindUnits = main.wind_units;

    try {
      mywaves.wind_waves_period = "5";
      mywaves.wind_waves_height = "2";
      mywind.int_true_wind_speed = 10;
      main.wind_units = main.M_S;

      assertTrue(new main_support().checking_level_2());
    } finally {
      mywaves.wind_waves_period = originalPeriod;
      mywaves.wind_waves_height = originalHeight;
      mywind.int_true_wind_speed = originalWindSpeed;
      main.wind_units = originalWindUnits;
    }
  }

  @Test
  public void acceptsEmptyWindWaveValuesAtLevelTwo() {
    String originalPeriod = mywaves.wind_waves_period;
    String originalHeight = mywaves.wind_waves_height;

    try {
      mywaves.wind_waves_period = "";
      mywaves.wind_waves_height = "";

      assertTrue(new main_support().checking_level_2());
    } finally {
      mywaves.wind_waves_period = originalPeriod;
      mywaves.wind_waves_height = originalHeight;
    }
  }

  @Test
  public void acceptsValidWindWaveValuesAtLevelThree() {
    String originalPeriod = mywaves.wind_waves_period;
    String originalHeight = mywaves.wind_waves_height;

    try {
      mywaves.wind_waves_period = "5";
      mywaves.wind_waves_height = "2";

      assertTrue(new main_support().checking_level_3());
    } finally {
      mywaves.wind_waves_period = originalPeriod;
      mywaves.wind_waves_height = originalHeight;
    }
  }

  @Test
  public void acceptsEmptyWindWaveValuesAtLevelThree() {
    String originalPeriod = mywaves.wind_waves_period;
    String originalHeight = mywaves.wind_waves_height;

    try {
      mywaves.wind_waves_period = "";
      mywaves.wind_waves_height = "";

      assertTrue(new main_support().checking_level_3());
    } finally {
      mywaves.wind_waves_period = originalPeriod;
      mywaves.wind_waves_height = originalHeight;
    }
  }
}
