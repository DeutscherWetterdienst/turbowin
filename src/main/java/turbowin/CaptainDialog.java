package turbowin;

import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX captains table to the existing captain log workflow. */
final class CaptainDialog {
  private CaptainDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    mycaptain model = new mycaptain();
    CaptainView.State state =
        new CaptainView.State(main.recruiting_country.contains("GERMANY"), main.offline_mode);
    AtomicReference<CaptainView> view = new AtomicReference<>();
    AtomicReference<String[][]> pending = new AtomicReference<>();
    JDialog window =
        FxSwingDialog.show(
            main.mainClass,
            "Captains",
            new Dimension(850, 600),
            close -> {
              CaptainView created =
                  new CaptainView(
                      state,
                      rows ->
                          SwingUtilities.invokeLater(() -> {
                            model.loadValues(rows);
                            if (model.accept()) close.run();
                          }),
                      () -> SwingUtilities.invokeLater(close),
                      () ->
                          SwingUtilities.invokeLater(() -> main.help_mouseClicked("captain.pdf")));
              view.set(created);
              String[][] loaded = pending.getAndSet(null);
              if (loaded != null) created.load(loaded);
              return created.scene();
            });
    model.whenLoaded(() -> {
      if (!window.isDisplayable()) return;
      String[][] rows = model.tableValues();
      Platform.runLater(() -> {
        CaptainView current = view.get();
        if (current == null) pending.set(rows);
        else current.load(rows);
      });
    });
    return window;
  }
}
