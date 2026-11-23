package turbowin;

import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX observer table to the existing log and OBS_ID workflows. */
final class ObserverDialog {
  private ObserverDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    myobserver model = new myobserver();
    ObserverView.State state =
        new ObserverView.State(
            main.recruiting_country.contains("GERMANY"),
            main.eucaws_obs_id,
            main.in_next_sequence,
            main.offline_mode);
    AtomicReference<ObserverView> view = new AtomicReference<>();
    AtomicReference<ObserverView.Data> pending = new AtomicReference<>();
    JDialog window =
        FxSwingDialog.show(
            main.mainClass,
            "Observer",
            new Dimension(850, 650),
            close -> {
              ObserverView created =
                  new ObserverView(
                      state,
                      data ->
                          SwingUtilities.invokeLater(() -> {
                            model.loadValues(data.rows(), data.selectedRow());
                            if (model.accept()) close.run();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            if (main.in_next_sequence) main.in_next_sequence = false;
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            CloudCoverJavaFxWindow.open();
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> {
                            close.run();
                            main.in_next_sequence = false;
                          }),
                      () ->
                          SwingUtilities.invokeLater(() -> main.help_mouseClicked("observer.pdf")));
              view.set(created);
              ObserverView.Data loaded = pending.getAndSet(null);
              if (loaded != null) created.load(loaded);
              return created.scene();
            });
    model.whenLoaded(() -> {
      if (!window.isDisplayable()) return;
      ObserverView.Data data = new ObserverView.Data(model.tableValues(), model.selectedRow());
      Platform.runLater(() -> {
        ObserverView current = view.get();
        if (current == null) pending.set(data);
        else current.load(data);
      });
    });
    return window;
  }
}
