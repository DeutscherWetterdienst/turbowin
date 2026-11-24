package turbowin;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Bridges the JavaFX position form to the existing observation and GPS workflows. */
final class PositionDialog {
  private PositionDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    PositionModel model = new PositionModel();
    JTextField[] fields = {
      PositionModel.jTextField1,
      PositionModel.jTextField2,
      PositionModel.jTextField3,
      PositionModel.jTextField4
    };
    PositionSelections selections = PositionModel.selections;
    String[] values = new String[fields.length];
    int[] selected = new int[4];
    for (int i = 0; i < fields.length; i++) values[i] = fields[i].getText();
    for (int i = 0; i < selected.length; i++) selected[i] = selections.selectedIndex(i);
    boolean gps = main.RS232_GPS_connection_mode != 0;
    PositionView.State state =
        new PositionView.State(
            values,
            selected,
            !fields[0].isEditable(),
            model.plotPositionAvailable(),
            model.courseLabel(),
            model.speedLabel(),
            main.in_next_sequence,
            main.offline_mode);
    AtomicReference<Scene> scene = new AtomicReference<>();
    AtomicReference<JDialog> window = new AtomicReference<>();
    if (gps) {
      for (int i = 0; i < fields.length; i++) {
        int index = i;
        Runnable refresh =
            () ->
                SwingUtilities.invokeLater(() -> {
                  Scene current = scene.get();
                  JDialog dialog = window.get();
                  if (current == null || dialog == null || !dialog.isDisplayable()) return;
                  String value = fields[index].getText();
                  Platform.runLater(() -> PositionView.updateField(current, index, value));
                });
        fields[i]
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
      for (int i = 0; i < selected.length; i++) {
        int index = i;
        selections
            .group(i)
            .addListener(event ->
                SwingUtilities.invokeLater(() -> {
                  Scene current = scene.get();
                  JDialog dialog = window.get();
                  if (current == null || dialog == null || !dialog.isDisplayable()) return;
                  int value = selections.selectedIndex(index);
                  Platform.runLater(() -> PositionView.updateSelection(current, index, value));
                }));
      }
    }
    JDialog result =
        FxSwingDialog.show(
            main.mainClass,
            "Position, course and speed",
            new Dimension(800, 650),
            close -> {
              Scene created =
                  PositionView.create(
                      state,
                      input ->
                          SwingUtilities.invokeLater(() -> {
                            for (int i = 0; i < fields.length; i++)
                              fields[i].setText(input.values()[i]);
                            for (int i = 0; i < selected.length; i++)
                              selections.selectIndex(i, input.selected()[i]);
                            model.setPlotPosition(input.plot());
                            if (model.accept()) {
                              close.run();
                              if (main.in_next_sequence) BarometerDialog.open();
                            }
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            if (main.in_next_sequence) BarometerDialog.open();
                            model.cancelCleanup();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            ObserverDialog.open();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            main.in_next_sequence = false;
                          }),
                      () ->
                          SwingUtilities.invokeLater(
                              () -> main.help_mouseClicked("position_course_speed.pdf")));
              scene.set(created);
              if (gps)
                SwingUtilities.invokeLater(() -> {
                  JDialog dialog = window.get();
                  if (dialog == null || !dialog.isDisplayable()) return;
                  for (int i = 0; i < fields.length; i++) {
                    int index = i;
                    String value = fields[i].getText();
                    if (!value.equals(values[i]))
                      Platform.runLater(() -> PositionView.updateField(created, index, value));
                  }
                  for (int i = 0; i < selected.length; i++) {
                    int index = i;
                    int value = selections.selectedIndex(i);
                    if (value != selected[i])
                      Platform.runLater(() -> PositionView.updateSelection(created, index, value));
                  }
                });
              return created;
            });
    window.set(result);
    result.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent event) {
            model.windowClosingCleanup();
          }
        });
    return result;
  }
}
