package turbowin;

import java.awt.Dimension;
import java.util.function.Supplier;
import javax.swing.JFrame;

/** Opens a settings window directly or routes the user through the password window. */
final class ProtectedWindowWorkflow {

  private ProtectedWindowWorkflow() {}

  static void open(
      boolean passwordVerified,
      Supplier<? extends JFrame> protectedWindow,
      Dimension protectedSize,
      Supplier<? extends JFrame> passwordWindow,
      Dimension passwordSize) {
    JFrame window = select(passwordVerified, protectedWindow, passwordWindow);
    window.setSize(passwordVerified ? protectedSize : passwordSize);
    window.setVisible(true);
  }

  static void openMaximized(
      boolean passwordVerified,
      Supplier<? extends JFrame> protectedWindow,
      Supplier<? extends JFrame> passwordWindow,
      Dimension passwordSize) {
    JFrame window = select(passwordVerified, protectedWindow, passwordWindow);
    if (passwordVerified) {
      window.setExtendedState(JFrame.MAXIMIZED_BOTH);
    } else {
      window.setSize(passwordSize);
    }
    window.setVisible(true);
  }

  static <T> T select(
      boolean passwordVerified,
      Supplier<? extends T> protectedWindow,
      Supplier<? extends T> passwordWindow) {
    return (passwordVerified ? protectedWindow : passwordWindow).get();
  }
}
