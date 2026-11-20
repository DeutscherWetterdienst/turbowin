package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Connects the JavaFX icing form to observation validation and state. */
final class IcingDialog {
  private IcingDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    IcingView.State state =
        new IcingView.State(
            IcingState.Is_code,
            IcingState.EsEs_code,
            IcingState.Rs_code,
            main.RS232_connection_mode == 3 || main.obs_format.equals(main.FORMAT_101),
            main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Ice accretion (icing)",
        new Dimension(800, 600),
        close ->
            IcingView.create(
                state,
                input ->
                    SwingUtilities.invokeLater(() -> {
                      if (save(input)) close.run();
                    }),
                () -> SwingUtilities.invokeLater(close),
                () -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("icing.pdf"))));
  }

  private static boolean save(IcingView.Input input) {
    String thickness = input.thickness().trim();
    IcingState.Is_code = input.cause();
    IcingState.Rs_code = input.rate();
    IcingState.EsEs_code = "";
    if (!thickness.isEmpty() || !input.cause().isEmpty() || !input.rate().isEmpty()) {
      int number;
      try {
        number = Integer.parseInt(thickness);
      } catch (NumberFormatException e) {
        number = main.INVALID;
      }
      if (number < 0 || number > 99 || thickness.length() > 2) {
        JOptionPane.showMessageDialog(
            null,
            "ice thickness must be in range 0 - 99 (or blank, but blank only if cause of ice accretion and rate of ice accreation are not determined)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        IcingState.Is_code = "";
        IcingState.Rs_code = "";
        return false;
      }
      IcingState.EsEs_code = thickness.length() < 2 ? "0" + thickness : thickness;
    }
    main.icing_fields_update();
    return true;
  }
}
