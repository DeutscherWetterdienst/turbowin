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
    mydatetime.day = "04";
    mydatetime.month = "October";
    mydatetime.year = "2026";
    mydatetime.hour = "12";
    mydatetime.minute = "34";

    DateTimeFieldsUpdater.update();

    assertEquals("04 October 2026  12.34 UTC", main.jTextField3.getText());
  }

  @Test
  public void clearsIncompleteDateAndTime() {
    main.jTextField3 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
    mydatetime.day = "04";
    mydatetime.month = "October";
    mydatetime.year = "2026";
    mydatetime.hour = "";
    mydatetime.minute = "34";

    DateTimeFieldsUpdater.update();

    assertEquals("", main.jTextField3.getText());
  }
}
