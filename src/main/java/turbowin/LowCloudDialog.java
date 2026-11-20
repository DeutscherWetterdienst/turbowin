package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX low-cloud view to the Swing observation workflow. */
final class LowCloudDialog {
  private LowCloudDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new IllegalStateException("Clouds low must be opened on the Swing event thread");
    }
    LowCloudView.State state =
        new LowCloudView.State(LowCloudState.cl_code, main.in_next_sequence, main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Clouds low",
        new Dimension(800, 650),
        close ->
            LowCloudView.create(
                state,
                code -> SwingUtilities.invokeLater(() -> accept(code, close)),
                () -> SwingUtilities.invokeLater(() -> cancel(close)),
                () -> SwingUtilities.invokeLater(() -> back(close)),
                () -> SwingUtilities.invokeLater(() -> stop(close)),
                file -> SwingUtilities.invokeLater(() -> main.help_mouseClicked(file))));
  }

  private static void accept(String code, Runnable close) {
    LowCloudState.cl_code = code;
    main.clouds_low_fields_update();
    close.run();
    if (main.in_next_sequence) {
      MiddleCloudDialog.open();
    }
  }

  private static void cancel(Runnable close) {
    close.run();
    if (main.in_next_sequence) {
      MiddleCloudDialog.open();
    }
  }

  private static void back(Runnable close) {
    close.run();
    mypastweather form = new mypastweather();
    form.setSize(800, 600);
    form.setVisible(true);
  }

  private static void stop(Runnable close) {
    close.run();
    main.in_next_sequence = false;
  }
}
