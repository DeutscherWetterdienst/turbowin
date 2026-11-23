package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ObservationStateResetterTest {

  @Test
  public void resetsObservationInputStateToItsInitialValues() {
    PastWeatherState.W1_code = "W1";
    PastWeatherState.past_weather_1 = "present";
    TemperatureModel.air_temp = "12";
    WindModel.wind_speed = "20";
    PositionModel.latitude_degrees = "52";
    PositionModel.int_latitude_degrees = 52;
    DateTimeModel.year = "2026";
    ObserverModel.selected_observer = "observer";
    TemperatureModel.wet_bulb_frozen = true;

    ObservationStateResetter.resetValues();

    assertEquals("", PastWeatherState.W1_code);
    assertEquals("", PastWeatherState.past_weather_1);
    assertEquals("", TemperatureModel.air_temp);
    assertEquals("", WindModel.wind_speed);
    assertEquals("", PositionModel.latitude_degrees);
    assertEquals(main.INVALID, PositionModel.int_latitude_degrees, 0.0);
    assertEquals("", DateTimeModel.year);
    assertEquals("", ObserverModel.selected_observer);
    assertFalse(TemperatureModel.wet_bulb_frozen);
  }

  @Test
  public void clearsCaptainAndObserverTables() {
    CaptainModel.captain_data[0][0] = "captain";
    ObserverModel.observer_data[0][0] = "observer";

    ObservationStateResetter.resetValues();

    assertEquals("", CaptainModel.captain_data[0][0]);
    assertEquals("", ObserverModel.observer_data[0][0]);
  }

  @Test
  public void resetsComputedAndAprSentinels() {
    TemperatureModel.double_dew_point = 1.0;
    TemperatureModel.double_rv = 1.0;
    WindModel.int_true_wind_dir = 1;
    WindModel.int_true_wind_speed = 1;
    WindModel.int_relative_wind_dir = 1;
    WindModel.int_relative_wind_speed = 1;
    PositionModel.int_latitude_degrees = 1;
    PositionModel.int_latitude_minutes = 1;
    PositionModel.int_longitude_degrees = 1;
    PositionModel.int_longitude_minutes = 1;
    PositionModel.SOG_APR = 1.0;
    PositionModel.COG_APR = 1.0;
    PositionModel.SOG_APR_wind = 1.0;
    PositionModel.COG_APR_wind = 1.0;

    ObservationStateResetter.resetValues();

    assertEquals(main.INVALID, TemperatureModel.double_dew_point, 0.0);
    assertEquals(main.INVALID, TemperatureModel.double_rv, 0.0);
    assertEquals(main.INVALID, WindModel.int_true_wind_dir);
    assertEquals(main.INVALID, WindModel.int_true_wind_speed);
    assertEquals(main.INVALID, WindModel.int_relative_wind_dir);
    assertEquals(main.INVALID, WindModel.int_relative_wind_speed);
    assertEquals(main.INVALID, PositionModel.int_latitude_degrees);
    assertEquals(main.INVALID, PositionModel.int_latitude_minutes);
    assertEquals(main.INVALID, PositionModel.int_longitude_degrees);
    assertEquals(main.INVALID, PositionModel.int_longitude_minutes);
    assertEquals(Double.MAX_VALUE, PositionModel.SOG_APR, 0.0);
    assertEquals(Double.MAX_VALUE, PositionModel.COG_APR, 0.0);
    assertEquals(Double.MAX_VALUE, PositionModel.SOG_APR_wind, 0.0);
    assertEquals(Double.MAX_VALUE, PositionModel.COG_APR_wind, 0.0);
  }

  @Test
  public void resetsWeatherPositionAndDateTimeState() {
    CloudCoverState.N = "8";
    WavesModel.swell_1_height = "3";
    IcingState.Is_code = "I";
    IceState.ci_code = "C";
    PositionModel.longitude_degrees = "4";
    PositionModel.SOG_APR = 12;
    DateTimeModel.GG_code = "10";

    ObservationStateResetter.resetValues();

    assertEquals("", CloudCoverState.N);
    assertEquals("", WavesModel.swell_1_height);
    assertEquals("", IcingState.Is_code);
    assertEquals("", IceState.ci_code);
    assertEquals("", PositionModel.longitude_degrees);
    assertEquals(Double.MAX_VALUE, PositionModel.SOG_APR, 0.0);
    assertEquals("", DateTimeModel.GG_code);
  }
}
