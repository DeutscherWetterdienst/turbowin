package turbowin;

import java.awt.AWTEvent;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/** Applies the TurboWin icon to every Swing frame, including generated windows. */
final class WindowIconWorkflow {

  private static boolean installed;

  private WindowIconWorkflow() {}

  static void install() {
    if (installed) {
      return;
    }
    installed = true;

    Image icon =
        Toolkit.getDefaultToolkit()
            .getImage(WindowIconWorkflow.class.getResource(main.ICONS_DIRECTORY + "tray.png"));
    AWTEventListener listener =
        event -> {
          if (event instanceof WindowEvent windowEvent
              && windowEvent.getID() == WindowEvent.WINDOW_OPENED
              && windowEvent.getWindow() instanceof JFrame frame) {
            frame.setIconImage(icon);
          }
        };
    Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);

    for (Window window : Window.getWindows()) {
      if (window instanceof JFrame frame) {
        frame.setIconImage(icon);
      }
    }
  }
}
