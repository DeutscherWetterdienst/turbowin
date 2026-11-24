package turbowin;

import java.awt.Dimension;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;

/** Shows the active operational log folder without changing configuration. */
final class LogFolderDialog {
  private LogFolderDialog() {}

  static JDialog open() {
    String folder = main.logs_dir;
    return FxSwingDialog.show(
        main.mainClass,
        "Log folder",
        new Dimension(620, 220),
        close -> {
          Label title = new Label("Log folder");
          title.getStyleClass().add("group-title");
          TextField path =
              new TextField(folder == null || folder.isBlank() ? "Not configured" : folder);
          path.setId("log_folder_path");
          path.setEditable(false);
          title.setLabelFor(path);
          Label info =
              new Label(
                  "Operational logs, including observations and sensor data, are stored here.");
          info.setWrapText(true);
          info.getStyleClass().add("hint");
          Button closeButton = new Button("Close");
          closeButton.setCancelButton(true);
          closeButton.setOnAction(event -> close.run());
          HBox actions = new HBox(closeButton);
          actions.setAlignment(Pos.CENTER_RIGHT);
          VBox root = new VBox(12, title, path, info, actions);
          root.setPadding(new Insets(20));
          root.getStyleClass().add("cloud-cover-root");
          Scene scene = new Scene(root);
          scene
              .getStylesheets()
              .add(
                  Objects.requireNonNull(LogFolderDialog.class.getResource("cloud-cover.css"))
                      .toExternalForm());
          return scene;
        });
  }
}
