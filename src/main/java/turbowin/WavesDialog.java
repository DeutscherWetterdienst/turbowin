package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Connects the JavaFX waves view to the existing observation and swell workflows. */
final class WavesDialog {
  private WavesDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    mywaves model = new mywaves();
    WavesView.State state =
        new WavesView.State(model.initialValues(), main.in_next_sequence, main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Waves",
        new Dimension(800, 650),
        close ->
            WavesView.create(
                state,
                input ->
                    SwingUtilities.invokeLater(() -> {
                      if (model.accept(input)) {
                        close.run();
                        switch (input.swell()) {
                          case CONFUSED -> show(new myconfusedswell());
                          case ONE -> show(new myoneswellsystem());
                          case TWO -> show(new mytwoswellsystems());
                          case NONE, UNDETERMINED -> {
                            if (main.in_next_sequence) next();
                          }
                        }
                      }
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      if (main.in_next_sequence) next();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      WindDialog.open();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      main.in_next_sequence = false;
                    }),
                () -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("waves.pdf"))));
  }

  private static void next() {
    VisibilityDialog.open();
  }

  private static void show(JFrame form) {
    form.setSize(800, 600);
    form.setVisible(true);
  }
}
