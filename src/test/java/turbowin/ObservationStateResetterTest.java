package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ObservationStateResetterTest {

  @Test
  public void resetsObservationInputStateToItsInitialValues() {
    mypastweather.W1_code = "W1";
    mypastweather.past_weather_1 = "present";
    mytemp.air_temp = "12";
    mywind.wind_speed = "20";
    myposition.latitude_degrees = "52";
    myposition.int_latitude_degrees = 52;
    mydatetime.year = "2026";
    myobserver.selected_observer = "observer";
    mytemp.wet_bulb_frozen = true;

    ObservationStateResetter.resetValues();

    assertEquals("", mypastweather.W1_code);
    assertEquals("", mypastweather.past_weather_1);
    assertEquals("", mytemp.air_temp);
    assertEquals("", mywind.wind_speed);
    assertEquals("", myposition.latitude_degrees);
    assertEquals(main.INVALID, myposition.int_latitude_degrees, 0.0);
    assertEquals("", mydatetime.year);
    assertEquals("", myobserver.selected_observer);
    assertFalse(mytemp.wet_bulb_frozen);
  }

  @Test
  public void clearsCaptainAndObserverTables() {
    mycaptain.captain_data[0][0] = "captain";
    myobserver.observer_data[0][0] = "observer";

    ObservationStateResetter.resetValues();

    assertEquals("", mycaptain.captain_data[0][0]);
    assertEquals("", myobserver.observer_data[0][0]);
  }

  @Test
  public void resetsComputedAndAprSentinels() {
    mytemp.double_dew_point = 1.0;
    mytemp.double_rv = 1.0;
    mywind.int_true_wind_dir = 1;
    mywind.int_true_wind_speed = 1;
    mywind.int_relative_wind_dir = 1;
    mywind.int_relative_wind_speed = 1;
    myposition.int_latitude_degrees = 1;
    myposition.int_latitude_minutes = 1;
    myposition.int_longitude_degrees = 1;
    myposition.int_longitude_minutes = 1;
    myposition.SOG_APR = 1.0;
    myposition.COG_APR = 1.0;
    myposition.SOG_APR_wind = 1.0;
    myposition.COG_APR_wind = 1.0;

    ObservationStateResetter.resetValues();

    assertEquals(main.INVALID, mytemp.double_dew_point, 0.0);
    assertEquals(main.INVALID, mytemp.double_rv, 0.0);
    assertEquals(main.INVALID, mywind.int_true_wind_dir);
    assertEquals(main.INVALID, mywind.int_true_wind_speed);
    assertEquals(main.INVALID, mywind.int_relative_wind_dir);
    assertEquals(main.INVALID, mywind.int_relative_wind_speed);
    assertEquals(main.INVALID, myposition.int_latitude_degrees);
    assertEquals(main.INVALID, myposition.int_latitude_minutes);
    assertEquals(main.INVALID, myposition.int_longitude_degrees);
    assertEquals(main.INVALID, myposition.int_longitude_minutes);
    assertEquals(Double.MAX_VALUE, myposition.SOG_APR, 0.0);
    assertEquals(Double.MAX_VALUE, myposition.COG_APR, 0.0);
    assertEquals(Double.MAX_VALUE, myposition.SOG_APR_wind, 0.0);
    assertEquals(Double.MAX_VALUE, myposition.COG_APR_wind, 0.0);
  }

  @Test
  public void resetsWeatherPositionAndDateTimeState() {
    mycloudcover.N = "8";
    mywaves.swell_1_height = "3";
    myicing.Is_code = "I";
    myice1.ci_code = "C";
    myposition.longitude_degrees = "4";
    myposition.SOG_APR = 12;
    mydatetime.GG_code = "10";

    ObservationStateResetter.resetValues();

    assertEquals("", mycloudcover.N);
    assertEquals("", mywaves.swell_1_height);
    assertEquals("", myicing.Is_code);
    assertEquals("", myice1.ci_code);
    assertEquals("", myposition.longitude_degrees);
    assertEquals(Double.MAX_VALUE, myposition.SOG_APR, 0.0);
    assertEquals("", mydatetime.GG_code);
  }
}
