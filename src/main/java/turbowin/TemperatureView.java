package turbowin;

import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/** The temperature input form, hosted in the existing Swing workflow. */
final class TemperatureView {
  record Input(String air, String wet, String humidity, String sea, boolean frozen) {}

  record State(
      Input input,
      String airExposure,
      String seaExposure,
      boolean airReadOnly,
      boolean humidityReadOnly,
      boolean seaReadOnly,
      boolean sequence,
      boolean offline) {}

  private final TextField air, wet, humidity, sea;
  private final RadioButton frozen, unfrozen;
  private final Scene scene;

  TemperatureView(
      State state,
      Consumer<Input> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    air = field("air_temp", state.input().air(), state.airReadOnly());
    wet = field("wet_bulb_temp", state.input().wet(), state.humidityReadOnly());
    humidity = field("rel_humidity", state.input().humidity(), state.humidityReadOnly());
    sea = field("sea_water_temp", state.input().sea(), state.seaReadOnly());
    ToggleGroup frozenGroup = new ToggleGroup();
    unfrozen = new RadioButton("not frozen");
    frozen = new RadioButton("frozen");
    unfrozen.setId("wet_bulb_unfrozen");
    frozen.setId("wet_bulb_frozen");
    unfrozen.setToggleGroup(frozenGroup);
    frozen.setToggleGroup(frozenGroup);
    (state.input().frozen() ? frozen : unfrozen).setSelected(true);

    VBox airBox =
        card(
            "Air temperature",
            row("Air (°C)", air, "−50.0 to 60.0"),
            exposure("Exposure", state.airExposure()));
    VBox wetBox =
        card(
            "Wet-bulb temperature",
            row("Wet bulb (°C)", wet, "−50.0 to 50.0"),
            new HBox(16, new Label("Wet bulb:"), unfrozen, frozen));
    VBox humidityBox =
        card("Relative humidity", row("Relative humidity (%)", humidity, "0 to 100"));
    VBox seaBox =
        card(
            "Sea water temperature",
            row("Sea water (°C)", sea, "−2.0 to 50.0"),
            exposure("Exposure", state.seaExposure()));
    VBox left = new VBox(12, airBox, wetBox, humidityBox);
    VBox right = new VBox(seaBox);
    HBox columns = new HBox(16, left, right);
    HBox.setHgrow(left, Priority.ALWAYS);
    HBox.setHgrow(right, Priority.ALWAYS);
    left.setMaxWidth(Double.MAX_VALUE);
    right.setMaxWidth(Double.MAX_VALUE);
    Label heading = new Label("Temperatures");
    heading.getStyleClass().add("heading");
    VBox content = new VBox(18, heading, columns);
    content.setAlignment(Pos.TOP_CENTER);
    content.setPadding(new Insets(20, 24, 20, 24));
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
    backButton.setOnAction(e -> back.run());
    ok.setDefaultButton(true);
    ok.setOnAction(e ->
        accept.accept(
            new Input(
                air.getText(),
                wet.getText(),
                humidity.getText(),
                sea.getText(),
                frozen.isSelected())));
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    stopButton.setOnAction(e -> stop.run());
    HBox buttons = new HBox(10, backButton, ok, cancelButton, helpButton, stopButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    scene = new Scene(root, 800, 600);
    scene
        .getStylesheets()
        .add(TemperatureView.class.getResource("cloud-cover.css").toExternalForm());
  }

  Scene scene() {
    return scene;
  }

  void sensorValues(Input input) {
    setSensorText(air, input.air());
    setSensorText(wet, input.wet());
    setSensorText(humidity, input.humidity());
    (input.frozen() ? frozen : unfrozen).setSelected(true);
  }

  private static void setSensorText(TextField field, String value) {
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
    if (readOnly) field.setStyle("-fx-text-fill: #6c7280;");
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

  private static HBox row(String label, TextField field, String range) {
    Label name = new Label(label);
    name.setLabelFor(field);
    Label limits = new Label(range);
    limits.getStyleClass().add("hint");
    HBox row = new HBox(8, name, field, limits);
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

  private static VBox card(String title, javafx.scene.Node... content) {
    Label heading = new Label(title);
    heading.getStyleClass().add("group-title");
    VBox box = new VBox(10);
    box.getStyleClass().add("choice-group");
    box.setPadding(new Insets(14));
    box.setMaxWidth(Double.MAX_VALUE);
    box.getChildren().add(heading);
    box.getChildren().addAll(content);
    return box;
  }

  private static VBox exposure(String title, String value) {
    Label label = new Label(title + " (Maintenance → Station data)");
    label.getStyleClass().add("hint");
    return new VBox(4, label, new Label(value == null || value.isBlank() ? "Not set" : value));
  }
}
