package turbowin;

import java.util.Objects;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** JavaFX form for a barometer reading and station pressure metadata. */
final class BarometerView {
  record State(
      String pressure,
      String draft,
      String correction,
      String msl,
      String notice,
      boolean pressureReadOnly,
      boolean draftReadOnly,
      boolean draftDisabled,
      boolean sequence,
      boolean offline) {}

  record Input(String pressure, String draft) {}

  private BarometerView() {}

  static Scene create(
      State state,
      Consumer<Input> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    TextField pressure = field("barometer_reading", state.pressure(), state.pressureReadOnly());
    TextField draft = field("deepest_draft", state.draft(), state.draftReadOnly());
    draft.setDisable(state.draftDisabled());
    TextField correction = field("instrument_correction", state.correction(), true);
    correction.setDisable(true);
    GridPane grid = new GridPane();
    grid.setHgap(12);
    grid.setVgap(12);
    row(grid, 0, "Barometer reading (hPa)", pressure, "860.0 – 1070.0");
    row(grid, 1, "Current deepest draft (metres)", draft, "0.0 – 50.0");
    row(grid, 2, "Instrument correction (hPa)", correction, "Station data");
    VBox readings = card("Reading", grid);
    Label metadata =
        new Label("Does the reading indicate mean sea level pressure?  " + state.msl());
    metadata.setId("msl_metadata");
    metadata.setWrapText(true);
    Label stationHint = new Label("Metadata: Maintenance → Station data");
    stationHint.getStyleClass().add("hint");
    Label correctionHint = new Label("Instrument correction is not the height correction.");
    correctionHint.getStyleClass().add("hint");
    VBox station = card("Station metadata", new VBox(6, metadata, stationHint, correctionHint));
    Label title = new Label("Barometer reading");
    title.getStyleClass().add("heading");
    Label notice = new Label(state.notice());
    notice.setWrapText(true);
    notice.getStyleClass().add("hint");
    VBox content = new VBox(16, title, readings, station, notice);
    content.setPadding(new Insets(20, 24, 20, 24));
    content.setAlignment(Pos.TOP_CENTER);
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.getStyleClass().add("cloud-cover-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);
    Button backButton = new Button("Back");
    Button ok = new Button("OK");
    Button cancelButton = new Button("Cancel");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    Button stopButton = new Button("Stop");
    backButton.setDisable(!state.sequence());
    stopButton.setDisable(!state.sequence());
    backButton.setOnAction(event -> back.run());
    ok.setDefaultButton(true);
    ok.setOnAction(event -> accept.accept(new Input(pressure.getText(), draft.getText())));
    cancelButton.setOnAction(event -> cancel.run());
    helpButton.setOnAction(event -> help.run());
    stopButton.setOnAction(event -> stop.run());
    HBox buttons = new HBox(10, backButton, ok, cancelButton, helpButton, stopButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(BarometerView.class.getResource("cloud-cover.css"))
                .toExternalForm());
    return scene;
  }

  static void updateSensorPressure(Scene scene, String value) {
    TextField field = (TextField) scene.lookup("#barometer_reading");
    TextFormatter<?> formatter = field.getTextFormatter();
    field.setTextFormatter(null);
    field.setText(value);
    field.setTextFormatter(formatter);
  }

  private static TextField field(String id, String value, boolean readOnly) {
    TextField field = new TextField(value);
    field.setId(id);
    field.setPrefColumnCount(7);
    field.setMaxWidth(110);
    field.setEditable(!readOnly);
    if (readOnly) field.getStyleClass().add("sensor-value");
    field.setTextFormatter(
        new TextFormatter<String>(change -> {
          if (change.isContentChange()) {
            change.setText(change.getText().replace(',', '.'));
            if (!change.getControlNewText().matches("-?\\d*(?:\\.\\d*)?")) return null;
          }
          return change;
        }));
    return field;
  }

  private static void row(GridPane grid, int row, String name, TextField field, String hint) {
    Label label = new Label(name);
    label.setLabelFor(field);
    Label limits = new Label(hint);
    limits.getStyleClass().add("hint");
    grid.addRow(row, label, field, limits);
  }

  private static VBox card(String title, javafx.scene.Node content) {
    Label label = new Label(title);
    label.getStyleClass().add("group-title");
    VBox box = new VBox(10, label, content);
    box.getStyleClass().add("choice-group");
    box.setPadding(new Insets(14));
    box.setMaxWidth(Double.MAX_VALUE);
    return box;
  }
}
