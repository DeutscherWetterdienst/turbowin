package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class PositionFieldsUpdaterTest {

  @Test
  public void formatsCompletePositionAndCourse() {
    main.jTextField5 = new JTextField();
    main.jTextField7 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    PositionModel.latitude_degrees = "52";
    PositionModel.latitude_minutes = "22.5";
    PositionModel.latitude_hemisphere = "North";
    PositionModel.longitude_degrees = "4";
    PositionModel.longitude_minutes = "53.1";
    PositionModel.longitude_hemisphere = "East";
    PositionModel.course = "180";
    PositionModel.speed = "12";

    PositionFieldsUpdater.update();

    assertEquals("52° - 22.5' N  4° - 53.1' E", main.jTextField5.getText());
    assertEquals("180°  12 kts", main.jTextField7.getText());
  }

  @Test
  public void clearsIncompletePositionAndCourse() {
    main.jTextField5 = new JTextField();
    main.jTextField7 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    PositionModel.latitude_degrees = "52";
    PositionModel.latitude_minutes = "";
    PositionModel.latitude_hemisphere = "North";
    PositionModel.longitude_degrees = "4";
    PositionModel.longitude_minutes = "53.1";
    PositionModel.longitude_hemisphere = "East";
    PositionModel.course = "";
    PositionModel.speed = "12";

    PositionFieldsUpdater.update();

    assertEquals("", main.jTextField5.getText());
    assertEquals("", main.jTextField7.getText());
  }
}
