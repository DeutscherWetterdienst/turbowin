package turbowin;

import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Connects the JavaFX barometer form to the existing pressure and sensor workflows. */
final class BarometerDialog {
  private BarometerDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    mybarometer model = new mybarometer();
    JTextField pressure = mybarometer.jTextField1;
    JTextField draft = mybarometer.jTextField2;
    boolean sensorConnected = main.RS232_connection_mode != 0;
    String msl = main.pressure_reading_msl_yes_no;
    String mslLabel =
        main.PRESSURE_READING_MSL_YES.equals(msl)
            ? "Yes"
            : main.PRESSURE_READING_MSL_NO.equals(msl) ? "No" : "Not set";
    BarometerView.State state =
        new BarometerView.State(
            pressure.getText(),
            draft.getText(),
            main.barometer_instrument_correction,
            mslLabel,
            mybarometer.jLabel10.getText(),
            !pressure.isEditable(),
            !draft.isEditable(),
            !draft.isEnabled(),
            main.in_next_sequence,
            main.offline_mode);
    model.dispose();

    AtomicReference<Scene> scene = new AtomicReference<>();
    AtomicReference<JDialog> window = new AtomicReference<>();
    Runnable refresh =
        () ->
            SwingUtilities.invokeLater(() -> {
              Scene current = scene.get();
              JDialog dialog = window.get();
              if (current == null || dialog == null || !dialog.isDisplayable()) return;
              String value = pressure.getText();
              Platform.runLater(() -> BarometerView.updateSensorPressure(current, value));
            });
    if (sensorConnected) {
      pressure
          .getDocument()
          .addDocumentListener(
              new DocumentListener() {
                public void insertUpdate(DocumentEvent event) {
                  refresh.run();
                }

                public void removeUpdate(DocumentEvent event) {
                  refresh.run();
                }

                public void changedUpdate(DocumentEvent event) {
                  refresh.run();
                }
              });
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
                            pressure.setText(input.pressure());
                            draft.setText(input.draft());
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
                            myposition previous = new myposition();
                            previous.setSize(800, 600);
                            previous.setVisible(true);
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
