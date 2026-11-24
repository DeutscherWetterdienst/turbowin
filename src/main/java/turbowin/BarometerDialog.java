package turbowin;

import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX barometer form to the existing pressure and sensor workflows. */
final class BarometerDialog {
  private BarometerDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    BarometerModel model = new BarometerModel();
    ObservableText pressure = BarometerModel.pressureInput;
    ObservableText draft = BarometerModel.draftInput;
    boolean sensorConnected = main.RS232_connection_mode != 0;
    String msl = main.pressure_reading_msl_yes_no;
    String mslLabel =
        main.PRESSURE_READING_MSL_YES.equals(msl)
            ? "Yes"
            : main.PRESSURE_READING_MSL_NO.equals(msl) ? "No" : "Not set";
    BarometerView.State state =
        new BarometerView.State(
            pressure.get(),
            draft.get(),
            main.barometer_instrument_correction,
            mslLabel,
            model.notice(),
            model.sensorReadOnly(),
            model.sensorReadOnly(),
            main.PRESSURE_READING_MSL_YES.equals(msl),
            main.in_next_sequence,
            main.offline_mode);
    AtomicReference<Scene> scene = new AtomicReference<>();
    AtomicReference<JDialog> window = new AtomicReference<>();
    Runnable refresh =
        () ->
            SwingUtilities.invokeLater(() -> {
              Scene current = scene.get();
              JDialog dialog = window.get();
              if (current == null || dialog == null || !dialog.isDisplayable()) return;
              String value = pressure.get();
              Platform.runLater(() -> BarometerView.updateSensorPressure(current, value));
            });
    if (sensorConnected) {
      pressure.addListener(event -> refresh.run());
    }
    JDialog result =
        FxSwingDialog.show(
            main.mainClass,
            "Barometer reading",
            new Dimension(800, 600),
            close -> {
              Scene created =
                  BarometerView.create(
                      state,
                      input ->
                          SwingUtilities.invokeLater(() -> {
                            pressure.set(input.pressure());
                            draft.set(input.draft());
                            if (model.accept()) {
                              close.run();
                              if (main.in_next_sequence) BarographDialog.open();
                            }
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            if (main.in_next_sequence) BarographDialog.open();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            PositionDialog.open();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            main.in_next_sequence = false;
                          }),
                      () ->
                          SwingUtilities.invokeLater(
                              () -> main.help_mouseClicked("barometer.pdf")));
              scene.set(created);
              if (sensorConnected) refresh.run();
              return created;
            });
    window.set(result);
    return result;
  }
}
