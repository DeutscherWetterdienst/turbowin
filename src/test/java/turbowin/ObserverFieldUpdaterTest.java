package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class ObserverFieldUpdaterTest {

  @Test
  public void displaysSelectedObserver() {
    main.jTextField20 = new JTextField();
    myobserver.selected_observer = "Observer One";

    ObserverFieldUpdater.update();

    assertEquals("Observer One", main.jTextField20.getText());
  }

  @Test
  public void clearsEmptySelectedObserver() {
    main.jTextField20 = new JTextField("old observer");
    myobserver.selected_observer = "";

    ObserverFieldUpdater.update();

    assertEquals("", main.jTextField20.getText());
  }
}
