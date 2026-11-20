package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX high-cloud view to the Swing observation workflow. */
final class HighCloudDialog {
  private HighCloudDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new IllegalStateException("Clouds high must be opened on the Swing event thread");
    }
    HighCloudView.State state =
        new HighCloudView.State(HighCloudState.ch_code, main.in_next_sequence, main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Clouds high",
        new Dimension(800, 650),
        close ->
            HighCloudView.create(
                state,
                code -> SwingUtilities.invokeLater(() -> accept(code, close)),
                () -> SwingUtilities.invokeLater(() -> cancel(close)),
                () -> SwingUtilities.invokeLater(() -> back(close)),
                () -> SwingUtilities.invokeLater(() -> stop(close)),
                file -> SwingUtilities.invokeLater(() -> main.help_mouseClicked(file))));
  }

  private static void accept(String code, Runnable close) {
    HighCloudState.ch_code = code;
    main.clouds_high_fields_update();
    close.run();
    if (main.in_next_sequence) {
      CloudCoverJavaFxWindow.open();
    }
  }

  private static void cancel(Runnable close) {
    close.run();
    if (main.in_next_sequence) {
      CloudCoverJavaFxWindow.open();
    }
  }

  private static void back(Runnable close) {
    close.run();
    mycm form = new mycm();
    form.setSize(800, 600);
    form.setVisible(true);
  }

  private static void stop(Runnable close) {
    close.run();
    main.in_next_sequence = false;
  }
}
