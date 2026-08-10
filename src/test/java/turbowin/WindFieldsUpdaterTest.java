package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class WindFieldsUpdaterTest {

  @Test
  public void formatsTrueAndRelativeWindInMetersPerSecond() {
    main.jTextField17 = new JTextField();
    main.jTextField16 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    main.true_wind_dir_from_AWS_present = true;
    main.true_wind_speed_from_AWS_present = true;
    main.relative_wind_dir_from_AWS_present = true;
    main.relative_wind_speed_from_AWS_present = true;
    main.wind_units = main.M_S;
    mywind.int_true_wind_dir = 90;
    mywind.int_true_wind_speed = 10;
    mywind.int_relative_wind_dir = 180;
    mywind.int_relative_wind_speed = 5;

    WindFieldsUpdater.update();

    assertEquals("90 ° / 10 m/s", main.jTextField17.getText());
    assertEquals("180 ° / 5 m/s", main.jTextField16.getText());
  }

  @Test
  public void representsVariableWindAndUsesKnotsWhenConfigured() {
    main.jTextField17 = new JTextField();
    main.jTextField16 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    main.true_wind_dir_from_AWS_present = true;
    main.true_wind_speed_from_AWS_present = true;
    main.relative_wind_dir_from_AWS_present = true;
    main.relative_wind_speed_from_AWS_present = true;
    main.wind_units = "knots";
    mywind.int_true_wind_dir = mywind.WIND_DIR_VARIABLE;
    mywind.int_true_wind_speed = main.INVALID;
    mywind.int_relative_wind_dir = main.INVALID;
    mywind.int_relative_wind_speed = 12;

    WindFieldsUpdater.update();

    assertEquals("var ° / - kts", main.jTextField17.getText());
    assertEquals("- ° / 12 kts", main.jTextField16.getText());
  }
}
