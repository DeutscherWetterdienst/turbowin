package turbowin;

import java.awt.Dimension;
import javafx.application.Platform;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Bridges the JavaFX form to the existing temperature calculations and Swing sequence. */
final class TemperatureDialog {
  private static volatile TemperatureView activeView;
  private static JDialog activeWindow;

  private TemperatureDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    activeView = null;
    TemperatureModel model = new TemperatureModel();
    boolean thermometer =
        main.RS232_connection_mode == 7
            || main.RS232_connection_mode == 8
            || main.RS232_connection_mode_II == 1;
    boolean sensor = main.APR && thermometer;
    TemperatureView.State state =
        new TemperatureView.State(
            input(model),
            main.air_temp_exposure,
            main.sst_exposure,
            main.air_temp_from_AWS_present || sensor,
            main.rh_from_AWS_present || sensor,
            main.SST_from_AWS_present,
            main.in_next_sequence,
            main.offline_mode);
    JDialog window =
        FxSwingDialog.show(
            main.mainClass,
            "Temperatures",
            new Dimension(800, 600),
            close -> {
              TemperatureView view =
                  new TemperatureView(
                      state,
                      value ->
                          SwingUtilities.invokeLater(() -> {
                            TemperatureModel.jTextField1.setText(value.air());
                            TemperatureModel.jTextField2.setText(value.wet());
                            TemperatureModel.jTextField4.setText(value.humidity());
                            model.jTextField3.setText(value.sea());
                            TemperatureModel.selectedWetBulbFrozen = value.frozen();
                            if (model.accept()) {
                              close.run();
                              if (main.in_next_sequence) next();
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
                            BarographDialog.open();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            main.in_next_sequence = false;
                          }),
                      () ->
                          SwingUtilities.invokeLater(
                              () -> main.help_mouseClicked("temperatures.pdf")));
              activeView = view;
              if (thermometer) {
                SwingUtilities.invokeLater(TemperatureDialog::sensorUpdated);
              }
              return view.scene();
            });
    activeWindow = window;
    return window;
  }

  /** Sensor callbacks run on the Swing thread; copy their latest values to the JavaFX form. */
  static void sensorUpdated() {
    TemperatureView view = activeView;
    JDialog window = activeWindow;
    if (view == null || window == null || !window.isDisplayable()) return;
    TemperatureView.Input values =
        new TemperatureView.Input(
            TemperatureModel.jTextField1.getText(),
            TemperatureModel.jTextField2.getText(),
            TemperatureModel.jTextField4.getText(),
            "",
            TemperatureModel.selectedWetBulbFrozen);
    Platform.runLater(() -> view.sensorValues(values));
  }

  private static TemperatureView.Input input(TemperatureModel model) {
    return new TemperatureView.Input(
        TemperatureModel.jTextField1.getText(),
        TemperatureModel.jTextField2.getText(),
        TemperatureModel.jTextField4.getText(),
        model.jTextField3.getText(),
        TemperatureModel.selectedWetBulbFrozen);
  }

  private static void next() {
    WindDialog.open();
  }
}
