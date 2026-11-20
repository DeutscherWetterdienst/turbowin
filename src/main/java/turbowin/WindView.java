package turbowin;

import java.util.Locale;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Wind observation form; it passes values to the Swing workflow for validation. */
final class WindView {
  record Input(
      String direction,
      String speed,
      String course,
      String groundSpeed,
      String heading,
      String cargo,
      String waterline) {}

  record State(
      Input input,
      String source,
      boolean metresPerSecond,
      boolean readOnly,
      boolean estimated,
      boolean sequence,
      boolean offline) {}

  private WindView() {}

  static Scene create(
      State state,
      Consumer<Input> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    Input values = state.input();
    TextField direction =
        field("wind_1", values.direction(), false, state.readOnly(), "variable", "calm");
    TextField speed = field("wind_2", values.speed(), true, state.readOnly());
    TextField course = field("wind_3", values.course(), false, state.readOnly(), "stationary");
    TextField groundSpeed = field("wind_4", values.groundSpeed(), true, state.readOnly());
    TextField heading = field("wind_5", values.heading(), false, state.readOnly());
    TextField cargo = field("wind_6", values.cargo(), true, false);
    TextField waterline = field("wind_7", values.waterline(), true, false);
    course.setDisable(state.estimated());
    groundSpeed.setDisable(state.estimated());
    heading.setDisable(state.estimated());

    Label title = new Label("Wind");
    title.getStyleClass().add("heading");
    Label sourceLabel = new Label("Source (Maintenance → Station data)");
    sourceLabel.getStyleClass().add("hint");
    Label source = new Label(state.source().isBlank() ? "Not set" : state.source());
    source.setWrapText(true);
    VBox sourceCard = card(sourceLabel, source);

    String kind =
        state.source().equals(main.MEASURED_OFF_BOW)
            ? "Apparent"
            : state.source().equals(main.ESTIMATED_TRUE)
                    || state.source().equals(main.MEASURED_TRUE)
                ? "True"
                : "Wind";
    String units = state.metresPerSecond() ? "m/s" : "knots";
    GridPane readings = new GridPane();
    readings.setHgap(10);
    readings.setVgap(8);
    addRow(readings, 0, kind + " direction (°)", direction, "variable, calm, 1–360");
    addRow(readings, 1, kind + " speed (" + units + ")", speed, "0–200");
    addRow(readings, 2, "Ship's ground course (°)", course, "stationary, 1–360");
    addRow(readings, 3, "Ship's ground speed (knots)", groundSpeed, "0.0–50.0");
    addRow(readings, 4, "Ship's heading (°)", heading, "1–360");
    Label shipHint = new Label("Ship's movement at the time of the wind observation");
    shipHint.getStyleClass().add("hint");
    ImageView beaufort = new ImageView();
    beaufort.setId("beaufort_image");
    beaufort.setFitWidth(150);
    beaufort.setFitHeight(100);
    beaufort.setPreserveRatio(true);
    VBox picture = new VBox(6, beaufort);
    picture.setAlignment(Pos.TOP_CENTER);
    boolean trueWind =
        state.source().equals(main.ESTIMATED_TRUE) || state.source().equals(main.MEASURED_TRUE);
    Runnable updatePicture =
        () -> {
          int force = trueWind ? beaufort(speed.getText(), state.metresPerSecond()) : -1;
          beaufort.setImage(
              force < 0
                  ? null
                  : new Image(
                      WindView.class
                          .getResource("icons/glyph_bf_" + force + ".jpg")
                          .toExternalForm()));
          beaufort.setAccessibleText(force < 0 ? "" : "Beaufort " + force);
        };
    speed.textProperty().addListener((observable, oldValue, newValue) -> updatePicture.run());
    updatePicture.run();
    HBox observation = new HBox(18, readings, picture);
    HBox.setHgrow(readings, Priority.ALWAYS);
    VBox observationCard = card(observation, shipHint);

    GridPane ship = new GridPane();
    ship.setHgap(10);
    ship.setVgap(8);
    addRow(ship, 0, "Max. deck cargo above summer load line (m)", cargo, "0–100");
    addRow(ship, 1, "Summer load line minus water line (m)", waterline, "−10–50");
    Label waterHint = new Label("Negative if the summer load line is below the water line");
    waterHint.getStyleClass().add("hint");
    VBox content = new VBox(12, title, sourceCard, observationCard, card(ship, waterHint));
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
                direction.getText(),
                speed.getText(),
                course.getText(),
                groundSpeed.getText(),
                heading.getText(),
                cargo.getText(),
                waterline.getText())));
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    stopButton.setOnAction(e -> stop.run());
    HBox buttons = new HBox(10, backButton, ok, cancelButton, helpButton, stopButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene.getStylesheets().add(WindView.class.getResource("cloud-cover.css").toExternalForm());
    return scene;
  }

  private static TextField field(
      String id, String value, boolean decimal, boolean readOnly, String... words) {
    TextField field = new TextField(value);
    field.setId(id);
    field.setPrefColumnCount(8);
    field.setMaxWidth(110);
    field.setEditable(!readOnly);
    if (readOnly) field.getStyleClass().add("sensor-value");
    field.setTextFormatter(
        new TextFormatter<String>(change -> {
          if (change.isContentChange()) {
            change.setText(change.getText().replace(',', '.'));
            String candidate = change.getControlNewText().toLowerCase(Locale.ROOT);
            boolean valid = candidate.matches(decimal ? "-?\\d*(?:\\.\\d*)?" : "-?\\d*");
            for (String word : words) valid |= word.startsWith(candidate);
            if (!valid) return null;
          }
          return change;
        }));
    return field;
  }

  private static void addRow(GridPane grid, int row, String name, TextField field, String range) {
    Label label = new Label(name);
    label.setLabelFor(field);
    Label limits = new Label(range);
    limits.getStyleClass().add("hint");
    grid.addRow(row, label, field, limits);
  }

  private static VBox card(javafx.scene.Node... nodes) {
    VBox box = new VBox(8, nodes);
    box.getStyleClass().add("choice-group");
    box.setPadding(new Insets(14));
    box.setMaxWidth(Double.MAX_VALUE);
    return box;
  }

  static int beaufort(String value, boolean metresPerSecond) {
    try {
      int speed = (int) Math.round(Double.parseDouble(value.trim()));
      if (speed < 0) return -1;
      int[] boundaries =
          metresPerSecond
              ? new int[] {0, 1, 3, 5, 7, 10, 13, 17, 20, 24, 28, 32}
              : new int[] {0, 3, 6, 10, 16, 21, 27, 33, 40, 47, 55, 63};
      for (int force = 0; force < boundaries.length; force++)
        if (speed <= boundaries[force]) return force;
      return 12;
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
