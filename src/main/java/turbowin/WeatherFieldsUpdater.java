package turbowin;

import static turbowin.main.*;

/** Updates present and past weather fields on the main screen. */
final class WeatherFieldsUpdater {

  private WeatherFieldsUpdater() {}

  static void updatePresent() {
    if ((mypresentweather.present_weather.compareTo("") != 0)
        && (mypresentweather.present_weather != null))
      jTextField13.setText(mypresentweather.present_weather);
    else jTextField13.setText("");

    coded_obs_update();
  }

  static void updatePast() {
    // Field 1/W1 is the primary past-weather phenomenon.
    if ((mypastweather.past_weather_1.compareTo("") != 0) && (mypastweather.past_weather_1 != null))
      jTextField14.setText(mypastweather.past_weather_1);
    else jTextField14.setText("");

    // Field 2/W2 is the secondary past-weather phenomenon.
    if ((mypastweather.past_weather_2.compareTo("") != 0) && (mypastweather.past_weather_2 != null))
      jTextField15.setText(mypastweather.past_weather_2);
    else jTextField15.setText("");

    coded_obs_update();
  }
}
