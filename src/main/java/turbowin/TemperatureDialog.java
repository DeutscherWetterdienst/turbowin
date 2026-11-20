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
    mytemp model = new mytemp();
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
                            mytemp.jTextField1.setText(value.air());
                            mytemp.jTextField2.setText(value.wet());
                            mytemp.jTextField4.setText(value.humidity());
                            model.jTextField3.setText(value.sea());
                            (value.frozen() ? mytemp.jRadioButton4 : mytemp.jRadioButton3)
                                .setSelected(true);
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
                            new mybarograph().setVisible(true);
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
            mytemp.jTextField1.getText(),
            mytemp.jTextField2.getText(),
            mytemp.jTextField4.getText(),
            "",
            mytemp.jRadioButton4.isSelected());
    Platform.runLater(() -> view.sensorValues(values));
  }

  private static TemperatureView.Input input(mytemp model) {
    return new TemperatureView.Input(
        mytemp.jTextField1.getText(),
        mytemp.jTextField2.getText(),
        mytemp.jTextField4.getText(),
        model.jTextField3.getText(),
        mytemp.jRadioButton4.isSelected());
  }

  private static void next() {
    mywind form = new mywind();
    form.setSize(800, 600);
    form.setVisible(true);
  }
}
