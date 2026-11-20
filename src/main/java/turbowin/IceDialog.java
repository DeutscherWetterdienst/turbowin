package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the sea-ice selector to shared observation state. */
final class IceDialog {
  private IceDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    IceView.State state =
        new IceView.State(
            new IceView.Input(
                IceState.ci_code,
                IceState.Si_code,
                IceState.bi_code,
                IceState.Di_code,
                IceState.zi_code),
            main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Ice",
        new Dimension(800, 650),
        close ->
            IceView.create(
                state,
                input ->
                    SwingUtilities.invokeLater(() -> {
                      IceState.ci_code = input.concentration();
                      IceState.Si_code = input.development();
                      IceState.bi_code = input.land();
                      IceState.Di_code = input.bearing();
                      IceState.zi_code = input.situation();
                      main.ice_fields_update();
                      close.run();
                    }),
                () -> SwingUtilities.invokeLater(close),
                () -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("ice.pdf"))));
  }
}
