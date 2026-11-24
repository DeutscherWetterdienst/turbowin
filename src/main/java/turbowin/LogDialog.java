package turbowin;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Live JavaFX views of the system and device logs. */
final class LogDialog {
  enum Kind {
    SYSTEM,
    DEVICE
  }

  private LogDialog() {}

  static JDialog open(Kind kind) {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    AtomicBoolean closed = new AtomicBoolean();
    AtomicReference<LogSnapshot.Content> last = new AtomicReference<>();
    AtomicReference<Scene> scene = new AtomicReference<>();
    LogSnapshot snapshots = new LogSnapshot();
    String title = kind == Kind.SYSTEM ? "TurboWin system log" : "TurboWin device log";
    ScheduledExecutorService refresh =
        Executors.newSingleThreadScheduledExecutor(r -> {
          Thread thread = new Thread(r, "turbowin-log-refresh");
          thread.setDaemon(true);
          return thread;
        });
    JDialog dialog =
        FxSwingDialog.show(
            main.mainClass,
            title,
            new Dimension(950, 650),
            close -> {
              Label path = new Label();
              path.setId("log_path");
              path.setWrapText(true);
              path.getStyleClass().add("hint");
              TextArea text = new TextArea();
              text.setId("log_text");
              text.setEditable(false);
              text.setWrapText(false);
              VBox.setVgrow(text, Priority.ALWAYS);
              Button closeButton = new Button("Close");
              closeButton.setOnAction(event -> close.run());
              HBox actions = new HBox(closeButton);
              actions.setAlignment(Pos.CENTER_RIGHT);
              VBox root = new VBox(10, path, text, actions);
              root.setPadding(new Insets(16));
              root.getStyleClass().add("cloud-cover-root");
              Scene created = new Scene(root);
              created
                  .getStylesheets()
                  .add(
                      Objects.requireNonNull(LogDialog.class.getResource("cloud-cover.css"))
                          .toExternalForm());
              scene.set(created);
              return created;
            });
    dialog.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosed(WindowEvent event) {
            closed.set(true);
            refresh.shutdownNow();
          }
        });
    refresh.scheduleWithFixedDelay(
        () -> {
          if (closed.get() || scene.get() == null) return;
          try {
            Path logs = Path.of(main.logs_dir == null ? "" : main.logs_dir);
            Instant now = Instant.now();
            LogSnapshot.Content content =
                kind == Kind.SYSTEM
                    ? snapshots.system(logs, now)
                    : snapshots.device(
                        logs,
                        now,
                        main.RS232_connection_mode,
                        main.RS232_connection_mode_II,
                        main.RS232_GPS_connection_mode,
                        main.ship_name,
                        main_RS232_RS422.GPS_array_device_log.clone());
            if (content.equals(last.getAndSet(content))) return;
            Platform.runLater(() -> {
              if (closed.get()) return;
              Scene current = scene.get();
              ((Label) current.lookup("#log_path")).setText(content.description());
              TextArea area = (TextArea) current.lookup("#log_text");
              double scroll = area.getScrollTop();
              int anchor = area.getAnchor();
              int caret = area.getCaretPosition();
              ScrollBar bar = (ScrollBar) area.lookup(".scroll-bar:vertical");
              boolean atBottom =
                  bar != null
                      && bar.getMax() > bar.getMin()
                      && bar.getValue() >= bar.getMax() - 0.01;
              area.setText(content.text());
              area.selectRange(
                  Math.min(anchor, area.getLength()), Math.min(caret, area.getLength()));
              Platform.runLater(() -> {
                if (!closed.get()) area.setScrollTop(atBottom ? Double.MAX_VALUE : scroll);
              });
            });
          } catch (RuntimeException ex) {
            // Keep polling after transient path or filesystem failures.
            System.err.println("Unable to refresh " + title + ": " + ex.getMessage());
          }
        },
        0,
        1,
        TimeUnit.SECONDS);
    return dialog;
  }
}
