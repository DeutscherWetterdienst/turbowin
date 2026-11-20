package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the visibility view with the Swing observation sequence. */
final class VisibilityDialog {
  private VisibilityDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    VisibilityView.State state =
        new VisibilityView.State(VisibilityState.VV, main.in_next_sequence, main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Visibility",
        new Dimension(800, 650),
        close ->
            VisibilityView.create(
                state,
                choice ->
                    SwingUtilities.invokeLater(() -> {
                      VisibilityState.VV = choice == null ? "" : choice.value();
                      VisibilityState.VV_code = choice == null ? "//" : choice.code();
                      main.visibility_fields_update();
                      close.run();
                      if (main.in_next_sequence) next();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      if (main.in_next_sequence) next();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      WavesDialog.open();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      main.in_next_sequence = false;
                    }),
                () -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("visibility.pdf"))));
  }

  private static void next() {
    mypresentweather form = new mypresentweather();
    form.setSize(800, 600);
    form.setVisible(true);
  }
}
