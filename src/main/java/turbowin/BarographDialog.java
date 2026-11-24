package turbowin;

import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX barograph form to the existing observation and sensor workflows. */
final class BarographDialog {
  private BarographDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    BarographModel model = new BarographModel();
    ObservableSelection<BarographModel.Tendency> tendency = BarographModel.tendency;
    ObservableText amount = BarographModel.amountInput;
    boolean sensorConnected = main.RS232_connection_mode != 0;
    int selected = selected(tendency);
    BarographView.State state =
        new BarographView.State(
            amount.get(),
            selected,
            model.sensorReadOnly(),
            main.in_next_sequence,
            main.offline_mode);
    AtomicReference<Scene> scene = new AtomicReference<>();
    AtomicReference<JDialog> window = new AtomicReference<>();
    Runnable refreshAmount =
        () ->
            SwingUtilities.invokeLater(() -> {
              Scene current = scene.get();
              JDialog dialog = window.get();
              if (current == null || dialog == null || !dialog.isDisplayable()) return;
              String value = amount.get();
              Platform.runLater(() -> BarographView.updateSensorAmount(current, value));
            });
    Runnable refreshSelection =
        () ->
            SwingUtilities.invokeLater(() -> {
              Scene current = scene.get();
              JDialog dialog = window.get();
              if (current == null || dialog == null || !dialog.isDisplayable()) return;
              int choice = selected(tendency);
              Platform.runLater(() -> BarographView.updateSensorSelection(current, choice));
            });
    if (sensorConnected) {
      amount.addListener(event -> refreshAmount.run());
      tendency.addListener(event -> refreshSelection.run());
    }
    JDialog result =
        FxSwingDialog.show(
            main.mainClass,
            "Barograph reading",
            new Dimension(800, 650),
            close -> {
              Scene created =
                  BarographView.create(
                      state,
                      input ->
                          SwingUtilities.invokeLater(() -> {
                            amount.set(input.amount());
                            tendency.set(BarographModel.Tendency.values()[input.selected()]);
                            if (model.accept()) {
                              close.run();
                              if (main.in_next_sequence) TemperatureDialog.open();
                            }
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            if (main.in_next_sequence) TemperatureDialog.open();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            BarometerDialog.open();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            main.in_next_sequence = false;
                          }),
                      () ->
                          SwingUtilities.invokeLater(
                              () -> main.help_mouseClicked(BarographModel.BAROGRAAF_HELP_DIR)));
              scene.set(created);
              if (sensorConnected)
                SwingUtilities.invokeLater(() -> {
                  JDialog dialog = window.get();
                  if (dialog == null || !dialog.isDisplayable()) return;
                  String currentAmount = amount.get();
                  int currentSelection = selected(tendency);
                  if (!currentAmount.equals(state.amount()))
                    Platform.runLater(
                        () -> BarographView.updateSensorAmount(created, currentAmount));
                  if (currentSelection != state.selected())
                    Platform.runLater(
                        () -> BarographView.updateSensorSelection(created, currentSelection));
                });
              return created;
            });
    window.set(result);
    return result;
  }

  private static int selected(ObservableSelection<BarographModel.Tendency> tendency) {
    return tendency.get() == null ? 11 : tendency.get().ordinal();
  }
}
