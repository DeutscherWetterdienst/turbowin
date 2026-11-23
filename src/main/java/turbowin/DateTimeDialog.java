package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX date and time form to the existing observation workflow. */
final class DateTimeDialog {
  private DateTimeDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    DateTimeModel model = new DateTimeModel();
    DateTimeView.State state =
        new DateTimeView.State(model.selectedValues(), model.isReadOnly(), main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Date and Time",
        new Dimension(800, 600),
        close ->
            DateTimeView.create(
                state,
                selection ->
                    SwingUtilities.invokeLater(() -> {
                      model.selectValues(selection);
                      if (model.accept()) close.run();
                    }),
                () -> SwingUtilities.invokeLater(close),
                () -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("date_time.pdf"))));
  }
}
