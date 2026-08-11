package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class IdentifierFieldsUpdaterTest {

  @Test
  public void updatesShipNameAndStationId() {
    prepareFields();
    main.ship_name = "Research Vessel";
    main.station_ID = "SHIP01";

    IdentifierFieldsUpdater.update();

    assertEquals("Research Vessel", main.jTextField1.getText());
    assertEquals("SHIP01", main.jTextField2.getText());
  }

  @Test
  public void clearsEmptyShipNameAndStationId() {
    prepareFields();
    main.ship_name = "";
    main.station_ID = "";
    main.jTextField1.setText("old ship");
    main.jTextField2.setText("old station");

    IdentifierFieldsUpdater.update();

    assertEquals("", main.jTextField1.getText());
    assertEquals("", main.jTextField2.getText());
  }

  private static void prepareFields() {
    main.jTextField1 = new JTextField();
    main.jTextField2 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
  }
}
