package turbowin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/** JavaFX form for calculating and exporting a barometer comparison. */
final class BarometerComparisonView {
  private final TextField[] inputs = new TextField[9];
  private final TextField[] outputs = new TextField[4];
  private final Label status = new Label();
  private final Button export = new Button("Export");
  private final Function<Window, File> chooseFile;
  private BarometerComparisonModel.Result result;

  private BarometerComparisonView(Function<Window, File> chooseFile) {
    this.chooseFile = chooseFile;
  }

  static Scene create(
      String ship, String height, String keel, BooleanSupplier seaLevelReading, Runnable close) {
    return create(
        ship,
        height,
        keel,
        seaLevelReading,
        close,
        owner -> {
          FileChooser chooser = new FileChooser();
          chooser.setTitle("Export barometer comparison data");
          return chooser.showSaveDialog(owner);
        });
  }

  static Scene create(
      String ship,
      String height,
      String keel,
      BooleanSupplier seaLevelReading,
      Runnable close,
      Function<Window, File> chooseFile) {
    return new BarometerComparisonView(chooseFile)
        .build(ship, height, keel, seaLevelReading, close);
  }

  private Scene build(
      String ship, String height, String keel, BooleanSupplier seaLevelReading, Runnable close) {
    String[] inputLabels = {
      "Ship name (optional)",
      "Barometer height above Summer Load Line (m)",
      "Keel to Summer Load Line (m)",
      "Date and time UTC (optional)",
      "Position or port (optional)",
      "Ship barometer reading at bridge height (hPa)",
      "Reference barometer reading at sea level (hPa)",
      "Actual ship draft (m)",
      "Outdoor air temperature (°C)"
    };
    String[] inputIds = {
      "ship",
      "height",
      "keel",
      "date",
      "position",
      "ship_pressure",
      "reference_pressure",
      "draft",
      "temperature"
    };
    for (int i = 0; i < inputs.length; i++) {
      TextField field = new TextField();
      field.setId(inputIds[i]);
      field.setPrefColumnCount(12);
      if (i == 1 || i == 2 || i >= 5) field.setTextFormatter(decimalFormatter());
      inputs[i] = field;
    }
    inputs[0].setText(ship == null ? "" : ship.trim());
    inputs[1].setText(height == null ? "" : height.trim());
    inputs[2].setText(keel == null ? "" : keel.trim());
    inputs[1].setDisable(true);
    inputs[2].setDisable(true);
    String[] outputLabels = {
      "Barometer height above waterline (m)",
      "Ship pressure converted to sea level (hPa)",
      "Instrument error (hPa)",
      "Instrument correction (hPa)"
    };
    String[] outputIds = {
      "waterline_height", "sea_level_pressure", "instrument_error", "instrument_correction"
    };
    for (int i = 0; i < outputs.length; i++) {
      outputs[i] = new TextField();
      outputs[i].setId(outputIds[i]);
      outputs[i].setEditable(false);
      outputs[i].setFocusTraversable(false);
      outputs[i].setPrefColumnCount(12);
    }
    export.setDisable(true);
    status.setId("comparison_status");
    status.setWrapText(true);
    status.getStyleClass().add("hint");
    for (TextField field : inputs)
      field
          .textProperty()
          .addListener((observable, old, value) -> {
            result = null;
            export.setDisable(true);
            for (TextField output : outputs) output.clear();
            setStatus("", false);
          });
    VBox content =
        new VBox(
            10,
            card("Ship data", inputLabels, inputs, 0, 3),
            card("Actual situation at time of readings", inputLabels, inputs, 3, 9),
            card("Calculated results", outputLabels, outputs, 0, 4));
    Label hint =
        new Label(
            "Ship dimensions are configured under Maintenance → Station data. This form is not suitable for the Great Lakes.");
    hint.setWrapText(true);
    hint.getStyleClass().add("hint");
    content.getChildren().addAll(hint, status);
    content.setPadding(new Insets(14, 20, 14, 20));
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.getStyleClass().add("cloud-cover-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);
    Button calculate = new Button("Calc");
    calculate.setDefaultButton(true);
    calculate.setOnAction(event -> calculate(seaLevelReading.getAsBoolean()));
    Button cancel = new Button("Cancel");
    cancel.setCancelButton(true);
    cancel.setOnAction(event -> close.run());
    export.setOnAction(event -> export());
    HBox actions = new HBox(10, calculate, cancel, export);
    actions.setAlignment(Pos.CENTER);
    actions.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, actions);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(BarometerComparisonView.class.getResource("cloud-cover.css"))
                .toExternalForm());
    return scene;
  }

  private static VBox card(String title, String[] labels, TextField[] fields, int start, int end) {
    Label heading = new Label(title);
    heading.getStyleClass().add("group-title");
    GridPane grid = new GridPane();
    grid.setHgap(12);
    grid.setVgap(5);
    for (int i = start; i < end; i++) {
      Label label = new Label(labels[i]);
      label.setLabelFor(fields[i]);
      grid.addRow(i - start, label, fields[i]);
      GridPane.setHgrow(fields[i], Priority.ALWAYS);
      fields[i].setMaxWidth(220);
    }
    VBox card = new VBox(2, heading, grid);
    card.getStyleClass().add("choice-group");
    card.setPadding(new Insets(8, 12, 8, 12));
    return card;
  }

  private void calculate(boolean seaLevelReading) {
    try {
      result =
          BarometerComparisonModel.calculate(
              new BarometerComparisonModel.Input(
                  text(0), text(1), text(2), text(3), text(4), text(5), text(6), text(7), text(8)),
              seaLevelReading);
      outputs[0].setText(result.height());
      outputs[1].setText(result.seaLevel());
      outputs[2].setText(result.error());
      outputs[3].setText(result.correction());
      setStatus("", false);
      export.setDisable(false);
    } catch (IllegalArgumentException ex) {
      result = null;
      export.setDisable(true);
      for (TextField output : outputs) output.clear();
      setStatus(ex.getMessage(), true);
    }
  }

  private String text(int index) {
    return inputs[index].getText().trim();
  }

  private void export() {
    if (result == null) return;
    File file = chooseFile.apply(export.getScene().getWindow());
    if (file == null) return;
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
    BarometerComparisonExportWorkflow.ExportData data = result.exportData();
    export.setDisable(true);
    setStatus("Writing comparison…", false);
    CompletableFuture.runAsync(() -> {
      try {
        BarometerComparisonExportWorkflow.write(file.toPath(), data);
        Platform.runLater(() -> {
          setStatus("Comparison written to: " + file, false);
          export.setDisable(result == null);
        });
      } catch (IOException | SecurityException ex) {
        Platform.runLater(() -> {
          setStatus("Unable to write to: " + file + " (" + ex.getMessage() + ")", true);
          export.setDisable(result == null);
        });
      }
    });
  }

  private void setStatus(String message, boolean error) {
    status.setText(message);
    status.getStyleClass().removeAll("hint", "calculator-error");
    status.getStyleClass().add(error ? "calculator-error" : "hint");
  }

  private static TextFormatter<String> decimalFormatter() {
    return new TextFormatter<>(change -> {
      if (change.isContentChange()) {
        change.setText(change.getText().replace(',', '.'));
        if (!change.getControlNewText().matches("-?\\d*(?:\\.\\d*)?")) return null;
      }
      return change;
    });
  }
}
