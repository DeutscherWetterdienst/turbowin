package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class TemperatureFieldsUpdaterTest {

  @Test
  public void formatsWholeNumberTemperaturesWithUnits() {
    main.jTextField36 = new JTextField();
    main.jTextField37 = new JTextField();
    main.jTextField38 = new JTextField();
    main.jTextField40 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    main.air_temp_from_AWS_present = true;
    main.rh_from_AWS_present = false;
    main.SST_from_AWS_present = true;
    mytemp.air_temp = "5";
    mytemp.wet_bulb_temp = "6";
    mytemp.sea_water_temp = "7";
    mytemp.double_rv = main.INVALID;

    TemperatureFieldsUpdater.update();

    assertEquals("5.0 °C", main.jTextField36.getText());
    assertEquals("6.0 °C", main.jTextField37.getText());
    assertEquals("7.0 °C", main.jTextField40.getText());
  }

  @Test
  public void displaysAwsRelativeHumidityAndNotWetBulb() {
    main.jTextField36 = new JTextField();
    main.jTextField37 = new JTextField();
    main.jTextField38 = new JTextField();
    main.jTextField40 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    main.air_temp_from_AWS_present = false;
    main.rh_from_AWS_present = true;
    main.SST_from_AWS_present = false;
    mytemp.air_temp = "";
    mytemp.wet_bulb_temp = "12";
    mytemp.double_rv = 0.856;

    TemperatureFieldsUpdater.update();

    assertEquals("", main.jTextField36.getText());
    assertEquals("NA", main.jTextField37.getText());
    assertEquals("85.6 %", main.jTextField38.getText());
  }
}
