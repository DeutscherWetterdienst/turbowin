package turbowin;

import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Connects the JavaFX barograph form to the existing observation and sensor workflows. */
final class BarographDialog {
  private BarographDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    mybarograph model = new mybarograph(false);
    JRadioButton[] options = options();
    JTextField amount = mybarograph.jTextField1;
    boolean sensorConnected = main.RS232_connection_mode != 0;
    int selected = selected(options);
    BarographView.State state =
        new BarographView.State(
            amount.getText(),
            selected,
            !mybarograph.jTextField1.isEditable(),
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
              String value = amount.getText();
              int choice = selected(options);
              Platform.runLater(() -> BarographView.updateSensorValues(current, value, choice));
            });
    if (sensorConnected) {
      amount
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
      for (JRadioButton option : options) option.addItemListener(event -> refresh.run());
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
                            amount.setText(input.amount());
                            options[input.selected()].setSelected(true);
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
                              () -> main.help_mouseClicked(mybarograph.BAROGRAAF_HELP_DIR)));
              scene.set(created);
              if (sensorConnected) refresh.run();
              return created;
            });
    window.set(result);
    return result;
  }

  private static int selected(JRadioButton[] options) {
    for (int i = 0; i < options.length; i++) {
      if (options[i].isSelected()) return i;
    }
    return 11;
  }

  private static JRadioButton[] options() {
    return new JRadioButton[] {
      mybarograph.jRadioButton1, mybarograph.jRadioButton2, mybarograph.jRadioButton3,
      mybarograph.jRadioButton4, mybarograph.jRadioButton5, mybarograph.jRadioButton6,
      mybarograph.jRadioButton7, mybarograph.jRadioButton8, mybarograph.jRadioButton9,
      mybarograph.jRadioButton10, mybarograph.jRadioButton11, mybarograph.jRadioButton12
    };
  }
}
