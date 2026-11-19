package turbowin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/** Hosts a JavaFX scene in a non-modal Swing dialog. Call from the Swing event thread. */
final class FxSwingDialog {
  private FxSwingDialog() {}

  static JDialog show(
      Window owner, String title, Dimension size, Function<Runnable, Scene> sceneFactory) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new IllegalStateException("Dialog must be created on the Swing event thread");
    }
    AtomicBoolean closed = new AtomicBoolean();
    JDialog dialog =
        new JDialog(owner, title) {
          @Override
          public void dispose() {
            closed.set(true);
            super.dispose();
          }
        };
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JFXPanel panel = new JFXPanel(); // Initializes JavaFX once, including on subsequent openings.
    panel.setPreferredSize(size);
    dialog.add(panel, BorderLayout.CENTER);
    dialog.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosed(WindowEvent event) {
            closed.set(true);
            Platform.runLater(() -> panel.setScene(null));
          }
        });
    dialog.pack();
    dialog.setLocationRelativeTo(owner);
    dialog.setVisible(true);
    Platform.runLater(() -> {
      // JavaFX must stay available after the last embedded window is closed.
      Platform.setImplicitExit(false);
      if (!closed.get()) {
        Scene scene = sceneFactory.apply(() -> SwingUtilities.invokeLater(dialog::dispose));
        if (!closed.get()) {
          panel.setScene(scene);
        }
      }
    });
    return dialog;
  }
}
