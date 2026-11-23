package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class DateTimeFieldsUpdaterTest {

  @Test
  public void formatsCompleteUtcDateAndTime() {
    main.jTextField3 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    DateTimeModel.day = "04";
    DateTimeModel.month = "October";
    DateTimeModel.year = "2026";
    DateTimeModel.hour = "12";
    DateTimeModel.minute = "34";

    DateTimeFieldsUpdater.update();

    assertEquals("04 October 2026  12.34 UTC", main.jTextField3.getText());
  }

  @Test
  public void clearsIncompleteDateAndTime() {
    main.jTextField3 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    DateTimeModel.day = "04";
    DateTimeModel.month = "October";
    DateTimeModel.year = "2026";
    DateTimeModel.hour = "";
    DateTimeModel.minute = "34";

    DateTimeFieldsUpdater.update();

    assertEquals("", main.jTextField3.getText());
  }
}
