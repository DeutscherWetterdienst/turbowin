package turbowin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/** Displays and exports the most recent hourly AWS measurements. */
final class LatestMeasurementsView {
  private static final DateTimeFormatter TIME =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH).withZone(ZoneOffset.UTC);
  private final TableView<List<String>> table = new TableView<>();
  private final Label status = new Label("Loading measurements…");
  private final Button refresh = new Button("Refresh");
  private final Button export = new Button("Export");
  private final AtomicBoolean closed;
  private final boolean knots;
  private final Function<Window, File> chooseFile;
  private final List<String> headers;
  private List<List<String>> rows = List.of();
  private boolean loading;
  private boolean writing;
  private boolean hasLoaded;

  private LatestMeasurementsView(
      AtomicBoolean closed, boolean knots, Function<Window, File> chooseFile) {
    this.closed = closed;
    this.knots = knots;
    this.chooseFile = chooseFile;
    headers = LatestMeasurementsTable.headers(knots);
  }

  static Scene create(AtomicBoolean closed, boolean knots, Runnable close) {
    return create(
        closed,
        knots,
        close,
        owner -> {
          FileChooser chooser = new FileChooser();
          chooser.setTitle("Export latest AWS measurements");
          chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file", "*.json"));
          return chooser.showSaveDialog(owner);
        });
  }

  static Scene create(
      AtomicBoolean closed, boolean knots, Runnable close, Function<Window, File> chooseFile) {
    return new LatestMeasurementsView(closed, knots, chooseFile).build(close);
  }

  private Scene build(Runnable close) {
    table.setId("measurements_table");
    table.setEditable(false);
    table.setPlaceholder(new Label("No measurements available"));
    for (int i = 0; i < headers.size(); i++) {
      int columnIndex = i;
      TableColumn<List<String>, String> column = new TableColumn<>(headers.get(i));
      column.setCellValueFactory(
          cell -> new ReadOnlyStringWrapper(cell.getValue().get(columnIndex)));
      column.setSortable(false);
      column.setReorderable(false);
      column.setPrefWidth(i >= 8 ? 155 : 100);
      table.getColumns().add(column);
    }
    VBox.setVgrow(table, Priority.ALWAYS);
    Label note = new Label("All measurements are at sensor height, except sea-level pressure.");
    note.getStyleClass().add("hint");
    status.setId("measurements_status");
    status.setWrapText(true);
    status.getStyleClass().add("hint");
    Button cancel = new Button("Cancel");
    cancel.setCancelButton(true);
    cancel.setOnAction(event -> close.run());
    refresh.setOnAction(event -> refresh());
    export.setOnAction(event -> export());
    HBox actions = new HBox(10, cancel, refresh, export);
    actions.setAlignment(Pos.CENTER);
    actions.getStyleClass().add("cloud-cover-actions");
    VBox body = new VBox(8, note, status, table);
    body.setPadding(new Insets(14, 20, 12, 20));
    VBox.setVgrow(body, Priority.ALWAYS);
    VBox root = new VBox(body, actions);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(LatestMeasurementsView.class.getResource("cloud-cover.css"))
                .toExternalForm());
    refresh();
    return scene;
  }

  private void refresh() {
    loading = true;
    refresh.setDisable(true);
    export.setDisable(true);
    setStatus("Loading measurements…", false);
    CompletableFuture.supplyAsync(
            () -> LatestMeasurementsTable.rows(LatestMeasurementsData.loadSnapshot(), knots))
        .whenComplete((loaded, failure) ->
            Platform.runLater(() -> {
              if (closed.get()) return;
              loading = false;
              refresh.setDisable(false);
              if (failure != null) {
                hasLoaded = false;
                setStatus("Unable to load measurements: " + failure.getCause(), true);
                export.setDisable(true);
                return;
              }
              rows = loaded;
              hasLoaded = true;
              table.setItems(FXCollections.observableArrayList(rows));
              setStatus("Date/Time select: " + TIME.format(Instant.now()) + " UTC", false);
              export.setDisable(writing);
            }));
  }

  private void export() {
    File file = chooseFile.apply(export.getScene().getWindow());
    if (file == null) return;
    if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
      setStatus("File name must end with .json", true);
      return;
    }
    if (file.exists()) {
      Alert confirmation =
          new Alert(
              Alert.AlertType.CONFIRMATION,
              "Overwrite existing file?",
              ButtonType.OK,
              ButtonType.CANCEL);
      confirmation.initOwner(export.getScene().getWindow());
      if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
    }
    List<List<String>> exportRows = rows;
    String ship = main.ship_name;
    writing = true;
    export.setDisable(true);
    setStatus("Writing measurements…", false);
    CompletableFuture.runAsync(() -> {
      try {
        LatestMeasurementsExportWorkflow.write(
            file.toPath(), ship, headers, exportRows, Instant.now());
        Platform.runLater(() -> {
          if (!closed.get()) {
            writing = false;
            setStatus("Measurements written to: " + file, false);
            export.setDisable(loading || !hasLoaded);
          }
        });
      } catch (IOException | SecurityException ex) {
        Platform.runLater(() -> {
          if (!closed.get()) {
            writing = false;
            setStatus("Unable to write to: " + file + " (" + ex.getMessage() + ")", true);
            export.setDisable(loading || !hasLoaded);
          }
        });
      }
    });
  }

  private void setStatus(String message, boolean error) {
    status.setText(message);
    status.getStyleClass().removeAll("hint", "calculator-error");
    status.getStyleClass().add(error ? "calculator-error" : "hint");
  }
}
