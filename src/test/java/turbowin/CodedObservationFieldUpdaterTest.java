package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class CodedObservationFieldUpdaterTest {

  @Test
  public void updatesCodedObservationForNonAwsMode() {
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 0;
    main.station_ID = "";

    CodedObservationFieldUpdater.update();

    assertEquals(main.UNDEFINED, main.jTextField4.getText());
  }

  @Test
  public void leavesCodedObservationUntouchedForAwsMode() {
    main.jTextField4 = new JTextField("existing observation");
    main.RS232_connection_mode = 3;

    CodedObservationFieldUpdater.update();

    assertEquals("existing observation", main.jTextField4.getText());
  }
}
