package turbowin;

import java.util.Objects;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** The observation position, course and speed form. */
final class PositionView {
  record State(
      String[] values,
      int[] selected,
      boolean readOnly,
      boolean mapAvailable,
      String courseLabel,
      String speedLabel,
      boolean sequence,
      boolean offline) {}

  record Input(String[] values, int[] selected, boolean plot) {}

  private static final String[] FIELD_IDS = {
    "lat_degree", "lat_minute", "lon_degree", "lon_minute"
  };
  private static final String[][] OPTIONS = {
    {"North", "South"},
    {"East", "West"},
    {
      "stationary",
      "023 - 067",
      "068 - 112",
      "113 - 157",
      "158 - 202",
      "203 - 247",
      "248 - 292",
      "293 - 337",
      "338 - 022"
    },
    {
      "0", "1 - 5", "6 - 10", "11 - 15", "16 - 20", "21 - 25", "26 - 30", "31 - 35", "36 - 40",
      "> 40"
    }
  };

  private PositionView() {}

  static Scene create(
      State state,
      Consumer<Input> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    TextField[] fields = new TextField[4];
    for (int i = 0; i < fields.length; i++)
      fields[i] = field(FIELD_IDS[i], state.values()[i], state.readOnly());
    ToggleGroup[] groups = {
      new ToggleGroup(), new ToggleGroup(), new ToggleGroup(), new ToggleGroup()
    };
    HBox latHemisphere = hemisphere(0, groups[0], state);
    HBox lonHemisphere = hemisphere(1, groups[1], state);
    VBox latitude = card("Latitude", coordinate(fields[0], fields[1], latHemisphere));
    VBox longitude = card("Longitude", coordinate(fields[2], fields[3], lonHemisphere));
    HBox coords = new HBox(14, latitude, longitude);
    HBox.setHgrow(latitude, Priority.ALWAYS);
    HBox.setHgrow(longitude, Priority.ALWAYS);
    VBox course = card(state.courseLabel(), choices(2, groups[2], state));
    VBox speed = card(state.speedLabel(), choices(3, groups[3], state));
    HBox movement = new HBox(14, course, speed);
    HBox.setHgrow(course, Priority.ALWAYS);
    HBox.setHgrow(speed, Priority.ALWAYS);
    CheckBox plot = new CheckBox("Plot position on World Topographic Map (internet)");
    plot.setId("plot_position");
    plot.setDisable(!state.mapAvailable());
    Label title = new Label("Position, course and speed");
    title.getStyleClass().add("heading");
    VBox content = new VBox(16, title, coords, movement, plot);
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
    ok.setOnAction(e -> {
      String[] values = new String[fields.length];
      int[] selected = new int[groups.length];
      for (int i = 0; i < fields.length; i++) values[i] = fields[i].getText();
      for (int i = 0; i < groups.length; i++) {
        Toggle toggle = groups[i].getSelectedToggle();
        selected[i] = toggle == null ? -1 : (int) toggle.getUserData();
      }
      accept.accept(new Input(values, selected, plot.isSelected()));
    });
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    stopButton.setOnAction(e -> stop.run());
    HBox buttons = new HBox(10, backButton, ok, cancelButton, helpButton, stopButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(PositionView.class.getResource("cloud-cover.css"))
                .toExternalForm());
    return scene;
  }

  static void updateField(Scene scene, int index, String value) {
    TextField field = (TextField) scene.lookup("#" + FIELD_IDS[index]);
    TextFormatter<?> formatter = field.getTextFormatter();
    field.setTextFormatter(null);
    field.setText(value);
    field.setTextFormatter(formatter);
  }

  static void updateSelection(Scene scene, int group, int selected) {
    RadioButton first = (RadioButton) scene.lookup("#position_option_" + group + "_0");
    if (selected < 0) first.getToggleGroup().selectToggle(null);
    else
      ((RadioButton) scene.lookup("#position_option_" + group + "_" + selected)).setSelected(true);
  }

  private static HBox hemisphere(int index, ToggleGroup group, State state) {
    HBox row = new HBox(12);
    for (int i = 0; i < OPTIONS[index].length; i++)
      row.getChildren().add(option(index, i, group, state));
    return row;
  }

  private static GridPane choices(int index, ToggleGroup group, State state) {
    GridPane grid = new GridPane();
    grid.setHgap(16);
    grid.setVgap(6);
    for (int i = 0; i < OPTIONS[index].length; i++)
      grid.add(option(index, i, group, state), i % 2, i / 2);
    return grid;
  }

  private static RadioButton option(int groupIndex, int index, ToggleGroup group, State state) {
    RadioButton option = new RadioButton(OPTIONS[groupIndex][index]);
    option.setId("position_option_" + groupIndex + "_" + index);
    option.setUserData(index);
    option.setToggleGroup(group);
    option.setSelected(state.selected()[groupIndex] == index);
    option.setDisable(state.readOnly());
    return option;
  }

  private static TextField field(String id, String value, boolean readOnly) {
    TextField field = new TextField(value);
    field.setId(id);
    field.setPrefColumnCount(4);
    field.setMaxWidth(80);
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

  private static VBox coordinate(TextField degrees, TextField minutes, HBox hemisphere) {
    Label deg = new Label("Degrees");
    deg.setLabelFor(degrees);
    Label min = new Label("Minutes");
    min.setLabelFor(minutes);
    HBox row = new HBox(12, new VBox(4, deg, degrees), new VBox(4, min, minutes));
    return new VBox(10, row, hemisphere);
  }

  private static VBox card(String title, javafx.scene.Node content) {
    Label label = new Label(title);
    label.setWrapText(true);
    label.getStyleClass().add("group-title");
    VBox box = new VBox(10, label, content);
    box.getStyleClass().add("choice-group");
    box.setPadding(new Insets(14));
    box.setMaxWidth(Double.MAX_VALUE);
    return box;
  }
}
