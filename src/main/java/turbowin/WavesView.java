package turbowin;

import java.util.Locale;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Wind-wave entry and swell-system choice. */
final class WavesView {
  enum Swell {
    UNDETERMINED,
    NONE,
    CONFUSED,
    ONE,
    TWO
  }

  record Input(String period, String height, Swell swell) {}

  record State(Input input, boolean sequence, boolean offline) {}

  private WavesView() {}

  static Scene create(
      State state,
      Consumer<Input> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    TextField period = field("wave_1", state.input().period());
    TextField height = field("wave_2", state.input().height());
    GridPane entries = new GridPane();
    entries.setHgap(10);
    entries.setVgap(10);
    addRow(entries, 0, "Period (sec)", period, "0–50 or confused");
    addRow(entries, 1, "Height (metres)", height, "0.0–49.0 or confused");
    VBox wind = card("Wind waves (sea)", entries);

    ToggleGroup group = new ToggleGroup();
    VBox choices =
        new VBox(
            4,
            option("Swell not determined", Swell.UNDETERMINED, group, state.input().swell()),
            option("No swell", Swell.NONE, group, state.input().swell()),
            option(
                "Confused swell or indeterminable direction",
                Swell.CONFUSED,
                group,
                state.input().swell()),
            option("One swell discernable", Swell.ONE, group, state.input().swell()),
            option("Two swells discernable", Swell.TWO, group, state.input().swell()));
    VBox swell = card("Swell system(s)", choices);
    VBox left = new VBox(12, wind, swell);
    HBox.setHgrow(left, Priority.ALWAYS);
    left.setMaxWidth(Double.MAX_VALUE);

    GridPane references = new GridPane();
    references.setHgap(10);
    references.setVgap(2);
    references.addRow(0, new Label("Bf"), new Label("Sea (m)"), new Label("m"), new Label("ft"));
    String[] heights = {
      "0.0", "0.1", "0.2", "0.6", "1.0", "2.0", "3.0", "4.0", "5.5", "7.0", "9.0", "11.5", "14.0"
    };
    int[] feet = {3, 7, 10, 13, 16, 20, 23, 26, 30, 33, 36, 40, 43, 46, 50};
    for (int i = 0; i < feet.length; i++) {
      if (i < heights.length) {
        references.add(new Label(Integer.toString(i)), 0, i + 1);
        references.add(new Label(heights[i]), 1, i + 1);
      }
      references.add(new Label(Integer.toString(i + 1)), 2, i + 1);
      references.add(new Label(Integer.toString(feet[i])), 3, i + 1);
    }
    Label hint = new Label("Probable mean sea height in the open sea, remote from land");
    hint.setWrapText(true);
    hint.getStyleClass().add("hint");
    VBox referenceCard = card("Reference", references, hint);
    referenceCard.setPrefWidth(270);
    HBox columns = new HBox(16, left, referenceCard);
    Label heading = new Label("Waves");
    heading.getStyleClass().add("heading");
    VBox content = new VBox(18, heading, columns);
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
    backButton.setOnAction(e -> back.run());
    ok.setDefaultButton(true);
    ok.setOnAction(e ->
        accept.accept(
            new Input(
                period.getText(),
                height.getText(),
                (Swell) group.getSelectedToggle().getUserData())));
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    stopButton.setOnAction(e -> stop.run());
    HBox buttons = new HBox(10, backButton, ok, cancelButton, helpButton, stopButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene.getStylesheets().add(WavesView.class.getResource("cloud-cover.css").toExternalForm());
    return scene;
  }

  private static TextField field(String id, String value) {
    TextField field = new TextField(value);
    field.setId(id);
    field.setPrefColumnCount(8);
    field.setMaxWidth(110);
    field.setTextFormatter(
        new TextFormatter<String>(change -> {
          if (change.isContentChange()) {
            change.setText(change.getText().replace(',', '.').toLowerCase(Locale.ROOT));
            String valueAfter = change.getControlNewText().toLowerCase(Locale.ROOT);
            if (!valueAfter.matches("-?\\d*(?:\\.\\d*)?") && !"confused".startsWith(valueAfter))
              return null;
          }
          return change;
        }));
    return field;
  }

  private static void addRow(GridPane grid, int row, String text, TextField field, String range) {
    Label label = new Label(text);
    label.setLabelFor(field);
    Label limits = new Label(range);
    limits.getStyleClass().add("hint");
    grid.addRow(row, label, field, limits);
  }

  private static RadioButton option(String text, Swell value, ToggleGroup group, Swell selected) {
    RadioButton button = new RadioButton(text);
    button.setUserData(value);
    button.setToggleGroup(group);
    button.setSelected(value == selected);
    return button;
  }

  private static VBox card(String title, javafx.scene.Node... nodes) {
    Label label = new Label(title);
    label.getStyleClass().add("group-title");
    VBox box = new VBox(8);
    box.getStyleClass().add("choice-group");
    box.setPadding(new Insets(14));
    box.setMaxWidth(Double.MAX_VALUE);
    box.getChildren().add(label);
    box.getChildren().addAll(nodes);
    return box;
  }
}
