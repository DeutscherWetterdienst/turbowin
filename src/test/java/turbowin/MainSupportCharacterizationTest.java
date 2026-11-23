package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class MainSupportCharacterizationTest {

  @Test
  public void acceptsInvalidWindWaveValuesAsConversionFailuresAtLevelTwo() {
    String originalPeriod = WavesModel.wind_waves_period;
    String originalHeight = WavesModel.wind_waves_height;
    int originalWindSpeed = WindModel.int_true_wind_speed;
    String originalWindUnits = main.wind_units;

    try {
      WavesModel.wind_waves_period = "invalid";
      WavesModel.wind_waves_height = "invalid";
      WindModel.int_true_wind_speed = 10;
      main.wind_units = main.M_S;

      assertTrue(new main_support().checking_level_2());
    } finally {
      WavesModel.wind_waves_period = originalPeriod;
      WavesModel.wind_waves_height = originalHeight;
      WindModel.int_true_wind_speed = originalWindSpeed;
      main.wind_units = originalWindUnits;
    }
  }

  @Test
  public void acceptsInvalidWindWaveValuesAsConversionFailuresAtLevelThree() {
    String originalPeriod = WavesModel.wind_waves_period;
    String originalHeight = WavesModel.wind_waves_height;

    try {
      WavesModel.wind_waves_period = "invalid";
      WavesModel.wind_waves_height = "invalid";

      assertTrue(new main_support().checking_level_3());
    } finally {
      WavesModel.wind_waves_period = originalPeriod;
      WavesModel.wind_waves_height = originalHeight;
    }
  }

  @Test
  public void acceptsValidWindWaveValuesAtLevelTwo() {
    String originalPeriod = WavesModel.wind_waves_period;
    String originalHeight = WavesModel.wind_waves_height;
    int originalWindSpeed = WindModel.int_true_wind_speed;
    String originalWindUnits = main.wind_units;

    try {
      WavesModel.wind_waves_period = "5";
      WavesModel.wind_waves_height = "2";
      WindModel.int_true_wind_speed = 10;
      main.wind_units = main.M_S;

      assertTrue(new main_support().checking_level_2());
    } finally {
      WavesModel.wind_waves_period = originalPeriod;
      WavesModel.wind_waves_height = originalHeight;
      WindModel.int_true_wind_speed = originalWindSpeed;
      main.wind_units = originalWindUnits;
    }
  }

  @Test
  public void acceptsEmptyWindWaveValuesAtLevelTwo() {
    String originalPeriod = WavesModel.wind_waves_period;
    String originalHeight = WavesModel.wind_waves_height;

    try {
      WavesModel.wind_waves_period = "";
      WavesModel.wind_waves_height = "";

      assertTrue(new main_support().checking_level_2());
    } finally {
      WavesModel.wind_waves_period = originalPeriod;
      WavesModel.wind_waves_height = originalHeight;
    }
  }

  @Test
  public void acceptsValidWindWaveValuesAtLevelThree() {
    String originalPeriod = WavesModel.wind_waves_period;
    String originalHeight = WavesModel.wind_waves_height;

    try {
      WavesModel.wind_waves_period = "5";
      WavesModel.wind_waves_height = "2";

      assertTrue(new main_support().checking_level_3());
    } finally {
      WavesModel.wind_waves_period = originalPeriod;
      WavesModel.wind_waves_height = originalHeight;
    }
  }

  @Test
  public void acceptsEmptyWindWaveValuesAtLevelThree() {
    String originalPeriod = WavesModel.wind_waves_period;
    String originalHeight = WavesModel.wind_waves_height;

    try {
      WavesModel.wind_waves_period = "";
      WavesModel.wind_waves_height = "";

      assertTrue(new main_support().checking_level_3());
    } finally {
      WavesModel.wind_waves_period = originalPeriod;
      WavesModel.wind_waves_height = originalHeight;
    }
  }

  @Test
  public void preservesAirTemperatureConversionAtBothValidationLevels() {
    String originalAirTemperature = TemperatureModel.air_temp;

    try {
      TemperatureModel.air_temp = "20";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());

      TemperatureModel.air_temp = "";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());

      TemperatureModel.air_temp = "invalid";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());
    } finally {
      TemperatureModel.air_temp = originalAirTemperature;
    }
  }

  @Test
  public void preservesPressureAmountTendencyConversionAtBothValidationLevels() {
    String originalPressureAmountTendency = BarographModel.pressure_amount_tendency;
    String originalPressureCharacteristic = BarographModel.a_code;

    try {
      BarographModel.pressure_amount_tendency = "1.5";
      BarographModel.a_code = "2";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());

      BarographModel.pressure_amount_tendency = "";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());

      BarographModel.pressure_amount_tendency = "invalid";
      assertTrue(new main_support().checking_level_2());
      assertTrue(new main_support().checking_level_3());
    } finally {
      BarographModel.pressure_amount_tendency = originalPressureAmountTendency;
      BarographModel.a_code = originalPressureCharacteristic;
    }
  }

  @Test
  public void preservesSwellConversionAtLevelThree() {
    String originalFirstPeriod = WavesModel.swell_1_period;
    String originalFirstHeight = WavesModel.swell_1_height;
    String originalSecondPeriod = WavesModel.swell_2_period;
    String originalSecondHeight = WavesModel.swell_2_height;

    try {
      WavesModel.swell_1_period = "5";
      WavesModel.swell_1_height = "2";
      WavesModel.swell_2_period = "6";
      WavesModel.swell_2_height = "3";
      assertTrue(new main_support().checking_level_3());

      WavesModel.swell_1_period = "";
      WavesModel.swell_1_height = "";
      WavesModel.swell_2_period = "";
      WavesModel.swell_2_height = "";
      assertTrue(new main_support().checking_level_3());

      WavesModel.swell_1_period = "invalid";
      WavesModel.swell_1_height = "invalid";
      WavesModel.swell_2_period = "invalid";
      WavesModel.swell_2_height = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      WavesModel.swell_1_period = originalFirstPeriod;
      WavesModel.swell_1_height = originalFirstHeight;
      WavesModel.swell_2_period = originalSecondPeriod;
      WavesModel.swell_2_height = originalSecondHeight;
    }
  }

  @Test
  public void preservesMslPressureConversionAtLevelThree() {
    String originalPressure = BarometerModel.pressure_msl_corrected;

    try {
      BarometerModel.pressure_msl_corrected = "1013.25";
      assertTrue(new main_support().checking_level_3());

      BarometerModel.pressure_msl_corrected = "";
      assertTrue(new main_support().checking_level_3());

      BarometerModel.pressure_msl_corrected = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      BarometerModel.pressure_msl_corrected = originalPressure;
    }
  }

  @Test
  public void preservesSeaWaterTemperatureConversionAtLevelThree() {
    String originalSeaWaterTemperature = TemperatureModel.sea_water_temp;

    try {
      TemperatureModel.sea_water_temp = "18";
      assertTrue(new main_support().checking_level_3());

      TemperatureModel.sea_water_temp = "";
      assertTrue(new main_support().checking_level_3());

      TemperatureModel.sea_water_temp = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      TemperatureModel.sea_water_temp = originalSeaWaterTemperature;
    }
  }

  @Test
  public void preservesIceThicknessConversionAtLevelThree() {
    String originalIceThickness = IcingState.EsEs_code;

    try {
      IcingState.EsEs_code = "10";
      assertTrue(new main_support().checking_level_3());

      IcingState.EsEs_code = "";
      assertTrue(new main_support().checking_level_3());

      IcingState.EsEs_code = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      IcingState.EsEs_code = originalIceThickness;
    }
  }

  @Test
  public void preservesWeatherIntegerConversionAtLevelTwo() {
    String originalPresentWeather = PresentWeatherState.ww_code;
    String originalVisibility = VisibilityState.VV_code;

    try {
      PresentWeatherState.ww_code = "20";
      VisibilityState.VV_code = "5";
      assertTrue(new main_support().checking_level_2());

      PresentWeatherState.ww_code = "";
      VisibilityState.VV_code = "";
      assertTrue(new main_support().checking_level_2());

      PresentWeatherState.ww_code = "invalid";
      VisibilityState.VV_code = "invalid";
      assertTrue(new main_support().checking_level_2());
    } finally {
      PresentWeatherState.ww_code = originalPresentWeather;
      VisibilityState.VV_code = originalVisibility;
    }
  }

  @Test
  public void preservesPlainCloudIntegerConversionAtLevelTwo() {
    String originalLowCloudType = LowCloudState.cl_code;
    String originalHighCloudType = HighCloudState.ch_code;

    try {
      LowCloudState.cl_code = "2";
      HighCloudState.ch_code = "4";
      assertTrue(new main_support().checking_level_2());

      LowCloudState.cl_code = "";
      HighCloudState.ch_code = "";
      assertTrue(new main_support().checking_level_2());

      LowCloudState.cl_code = "invalid";
      HighCloudState.ch_code = "invalid";
      assertTrue(new main_support().checking_level_2());
    } finally {
      LowCloudState.cl_code = originalLowCloudType;
      HighCloudState.ch_code = originalHighCloudType;
    }
  }

  @Test
  public void preservesMiddleCloudSuffixConversionAtLevelTwo() {
    String originalMiddleCloudType = MiddleCloudState.cm_code;

    try {
      MiddleCloudState.cm_code = "7";
      assertTrue(new main_support().checking_level_2());

      MiddleCloudState.cm_code = "7a";
      assertTrue(new main_support().checking_level_2());

      MiddleCloudState.cm_code = "";
      assertTrue(new main_support().checking_level_2());

      MiddleCloudState.cm_code = "invalid";
      assertTrue(new main_support().checking_level_2());
    } finally {
      MiddleCloudState.cm_code = originalMiddleCloudType;
    }
  }

  @Test
  public void preservesWeatherIntegerConversionAtLevelThree() {
    String originalPresentWeather = PresentWeatherState.ww_code;

    try {
      PresentWeatherState.ww_code = "20";
      assertTrue(new main_support().checking_level_3());

      PresentWeatherState.ww_code = "";
      assertTrue(new main_support().checking_level_3());

      PresentWeatherState.ww_code = "invalid";
      assertTrue(new main_support().checking_level_3());
    } finally {
      PresentWeatherState.ww_code = originalPresentWeather;
    }
  }

  @Test
  public void parsesSatelliteCoordinatesAndHemisphereSigns() {
    assertEquals(12, main_support.parseSatelliteCoordinate("12", "N", "S"));
    assertEquals(-12, main_support.parseSatelliteCoordinate("12", "S", "S"));
    assertEquals(-45, main_support.parseSatelliteCoordinate("45", "W", "W"));
    assertEquals(0, main_support.parseSatelliteCoordinate("invalid", "N", "S"));
    assertEquals(0, main_support.parseSatelliteCoordinate("", "N", "S"));
  }

  @Test
  public void preservesNoaaNullHemisphereFailure() {
    String originalLatitudeDegrees = PositionModel.latitude_degrees;
    String originalLatitudeHemisphere = PositionModel.latitude_hemisphere;
    String originalLongitudeDegrees = PositionModel.longitude_degrees;
    String originalLongitudeHemisphere = PositionModel.longitude_hemisphere;

    try {
      PositionModel.latitude_degrees = "12";
      PositionModel.latitude_hemisphere = null;
      PositionModel.longitude_degrees = "4";
      PositionModel.longitude_hemisphere = "E";

      try {
        new main_support().determine_satellite_image_url_NOAA(main.SATELLITE_IR_IMAGE);
        fail("Expected a null latitude hemisphere to fail in the NOAA path");
      } catch (NullPointerException ex) {
        assertEquals("latitude_hemisphere", ex.getMessage());
      }
    } finally {
      PositionModel.latitude_degrees = originalLatitudeDegrees;
      PositionModel.latitude_hemisphere = originalLatitudeHemisphere;
      PositionModel.longitude_degrees = originalLongitudeDegrees;
      PositionModel.longitude_hemisphere = originalLongitudeHemisphere;
    }
  }
}
