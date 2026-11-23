package turbowin;

import static turbowin.main.*;

/** Updates the selected observer field on the main screen. */
final class ObserverFieldUpdater {

  private ObserverFieldUpdater() {}

  static void update() {
    if (ObserverModel.selected_observer.compareTo("") != 0) {
      jTextField20.setText(ObserverModel.selected_observer);
    } else {
      jTextField20.setText("");
    }
  }
}
