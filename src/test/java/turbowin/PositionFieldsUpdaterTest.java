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
    myposition.latitude_degrees = "52";
    myposition.latitude_minutes = "22.5";
    myposition.latitude_hemisphere = "North";
    myposition.longitude_degrees = "4";
    myposition.longitude_minutes = "53.1";
    myposition.longitude_hemisphere = "East";
    myposition.course = "180";
    myposition.speed = "12";

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
    myposition.latitude_degrees = "52";
    myposition.latitude_minutes = "";
    myposition.latitude_hemisphere = "North";
    myposition.longitude_degrees = "4";
    myposition.longitude_minutes = "53.1";
    myposition.longitude_hemisphere = "East";
    myposition.course = "";
    myposition.speed = "12";

    PositionFieldsUpdater.update();

    assertEquals("", main.jTextField5.getText());
    assertEquals("", main.jTextField7.getText());
  }
}
