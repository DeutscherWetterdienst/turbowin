package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class WeatherFieldsUpdaterTest {

  @Test
  public void updatesPresentAndPastWeatherValues() {
    prepareFields();
    mypresentweather.present_weather = "RA";
    mypastweather.past_weather_1 = "DZ";
    mypastweather.past_weather_2 = "FG";

    WeatherFieldsUpdater.updatePresent();
    WeatherFieldsUpdater.updatePast();

    assertEquals("RA", main.jTextField13.getText());
    assertEquals("DZ", main.jTextField14.getText());
    assertEquals("FG", main.jTextField15.getText());
  }

  @Test
  public void clearsEmptyPresentAndPastWeatherValues() {
    prepareFields();
    mypresentweather.present_weather = "";
    mypastweather.past_weather_1 = "";
    mypastweather.past_weather_2 = "";

    WeatherFieldsUpdater.updatePresent();
    WeatherFieldsUpdater.updatePast();

    assertEquals("", main.jTextField13.getText());
    assertEquals("", main.jTextField14.getText());
    assertEquals("", main.jTextField15.getText());
  }

  private static void prepareFields() {
    main.jTextField13 = new JTextField();
    main.jTextField14 = new JTextField();
    main.jTextField15 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
  }
}
