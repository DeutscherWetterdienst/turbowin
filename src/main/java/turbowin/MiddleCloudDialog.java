package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX middle-cloud view to the Swing observation workflow. */
final class MiddleCloudDialog {
  private MiddleCloudDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new IllegalStateException("Clouds middle must be opened on the Swing event thread");
    }
    MiddleCloudView.State state =
        new MiddleCloudView.State(
            MiddleCloudState.cm_code, main.in_next_sequence, main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Clouds middle",
        new Dimension(800, 650),
        close ->
            MiddleCloudView.create(
                state,
                code -> SwingUtilities.invokeLater(() -> accept(code, close)),
                () -> SwingUtilities.invokeLater(() -> cancel(close)),
                () -> SwingUtilities.invokeLater(() -> back(close)),
                () -> SwingUtilities.invokeLater(() -> stop(close)),
                file -> SwingUtilities.invokeLater(() -> main.help_mouseClicked(file))));
  }

  private static void accept(String code, Runnable close) {
    MiddleCloudState.cm_code = code;
    main.clouds_middle_fields_update();
    close.run();
    if (main.in_next_sequence) {
      HighCloudDialog.open();
    }
  }

  private static void cancel(Runnable close) {
    close.run();
    if (main.in_next_sequence) {
      HighCloudDialog.open();
    }
  }

  private static void back(Runnable close) {
    close.run();
    mycl form = new mycl();
    form.setSize(800, 600);
    form.setVisible(true);
  }

  private static void stop(Runnable close) {
    close.run();
    main.in_next_sequence = false;
  }
}
