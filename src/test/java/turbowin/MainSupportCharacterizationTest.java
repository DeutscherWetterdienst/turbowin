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

  @Test
  public void preservesAirTemperatureConversionAtBothValidationLevels() {
    String originalAirTemperature = mytemp.air_temp;

    try {
      mytemp.air_temp = "20";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());

      mytemp.air_temp = "";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());

      mytemp.air_temp = "invalid";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());
    } finally {
      mytemp.air_temp = originalAirTemperature;
    }
  }

  @Test
  public void preservesPressureAmountTendencyConversionAtBothValidationLevels() {
    String originalPressureAmountTendency = mybarograph.pressure_amount_tendency;
    String originalPressureCharacteristic = mybarograph.a_code;

    try {
      mybarograph.pressure_amount_tendency = "1.5";
      mybarograph.a_code = "2";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());

      mybarograph.pressure_amount_tendency = "";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());

      mybarograph.pressure_amount_tendency = "invalid";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());
    } finally {
      mybarograph.pressure_amount_tendency = originalPressureAmountTendency;
      mybarograph.a_code = originalPressureCharacteristic;
    }
  }

  @Test
  public void preservesSwellConversionAtLevelThree() {
    String originalFirstPeriod = mywaves.swell_1_period;
    String originalFirstHeight = mywaves.swell_1_height;
    String originalSecondPeriod = mywaves.swell_2_period;
    String originalSecondHeight = mywaves.swell_2_height;

    try {
      mywaves.swell_1_period = "5";
      mywaves.swell_1_height = "2";
      mywaves.swell_2_period = "6";
      mywaves.swell_2_height = "3";
      assertTrue(new main_support().checking_level_3());

      mywaves.swell_1_period = "";
      mywaves.swell_1_height = "";
      mywaves.swell_2_period = "";
      mywaves.swell_2_height = "";
      assertTrue(new main_support().checking_level_3());

      mywaves.swell_1_period = "invalid";
      mywaves.swell_1_height = "invalid";
      mywaves.swell_2_period = "invalid";
      mywaves.swell_2_height = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      mywaves.swell_1_period = originalFirstPeriod;
      mywaves.swell_1_height = originalFirstHeight;
      mywaves.swell_2_period = originalSecondPeriod;
      mywaves.swell_2_height = originalSecondHeight;
    }
  }

  @Test
  public void preservesMslPressureConversionAtLevelThree() {
    String originalPressure = mybarometer.pressure_msl_corrected;

    try {
      mybarometer.pressure_msl_corrected = "1013.25";
      assertTrue(new main_support().checking_level_3());

      mybarometer.pressure_msl_corrected = "";
      assertTrue(new main_support().checking_level_3());

      mybarometer.pressure_msl_corrected = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      mybarometer.pressure_msl_corrected = originalPressure;
    }
  }

  @Test
  public void preservesSeaWaterTemperatureConversionAtLevelThree() {
    String originalSeaWaterTemperature = mytemp.sea_water_temp;

    try {
      mytemp.sea_water_temp = "18";
      assertTrue(new main_support().checking_level_3());

      mytemp.sea_water_temp = "";
      assertTrue(new main_support().checking_level_3());

      mytemp.sea_water_temp = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      mytemp.sea_water_temp = originalSeaWaterTemperature;
    }
  }

  @Test
  public void preservesIceThicknessConversionAtLevelThree() {
    String originalIceThickness = myicing.EsEs_code;

    try {
      myicing.EsEs_code = "10";
      assertTrue(new main_support().checking_level_3());

      myicing.EsEs_code = "";
      assertTrue(new main_support().checking_level_3());

      myicing.EsEs_code = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      myicing.EsEs_code = originalIceThickness;
    }
  }

  @Test
  public void preservesWeatherIntegerConversionAtLevelTwo() {
    String originalPresentWeather = mypresentweather.ww_code;
    String originalVisibility = myvisibility.VV_code;

    try {
      mypresentweather.ww_code = "20";
      myvisibility.VV_code = "5";
      assertTrue(new main_support().checking_level_2());

      mypresentweather.ww_code = "";
      myvisibility.VV_code = "";
      assertTrue(new main_support().checking_level_2());

      mypresentweather.ww_code = "invalid";
      myvisibility.VV_code = "invalid";
      assertTrue(new main_support().checking_level_2());
    } finally {
      mypresentweather.ww_code = originalPresentWeather;
      myvisibility.VV_code = originalVisibility;
    }
  }

  @Test
  public void preservesPlainCloudIntegerConversionAtLevelTwo() {
    String originalLowCloudType = mycl.cl_code;
    String originalHighCloudType = mych.ch_code;

    try {
      mycl.cl_code = "2";
      mych.ch_code = "4";
      assertTrue(new main_support().checking_level_2());

      mycl.cl_code = "";
      mych.ch_code = "";
      assertTrue(new main_support().checking_level_2());

      mycl.cl_code = "invalid";
      mych.ch_code = "invalid";
      assertTrue(new main_support().checking_level_2());
    } finally {
      mycl.cl_code = originalLowCloudType;
      mych.ch_code = originalHighCloudType;
    }
  }

  @Test
  public void preservesMiddleCloudSuffixConversionAtLevelTwo() {
    String originalMiddleCloudType = mycm.cm_code;

    try {
      mycm.cm_code = "7";
      assertTrue(new main_support().checking_level_2());

      mycm.cm_code = "7a";
      assertTrue(new main_support().checking_level_2());

      mycm.cm_code = "";
      assertTrue(new main_support().checking_level_2());

      mycm.cm_code = "invalid";
      assertTrue(new main_support().checking_level_2());
    } finally {
      mycm.cm_code = originalMiddleCloudType;
    }
  }

  @Test
  public void preservesWeatherIntegerConversionAtLevelThree() {
    String originalPresentWeather = mypresentweather.ww_code;

    try {
      mypresentweather.ww_code = "20";
      assertTrue(new main_support().checking_level_3());

      mypresentweather.ww_code = "";
      assertTrue(new main_support().checking_level_3());

      mypresentweather.ww_code = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      mypresentweather.ww_code = originalPresentWeather;
    }
  }
}
