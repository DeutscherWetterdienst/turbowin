package turbowin;

import java.util.Objects;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Pressure tendency selector, including the distinct graphs sharing codes 0 and 5. */
final class BarographView {
  record State(String amount, int selected, boolean sensor, boolean sequence, boolean offline) {}

  record Input(String amount, int selected) {}

  private static final String[] IMAGES = {
    "A_0", "A_1", "A_2", "A_3", "A_5", "A_6", "A_7", "A_8", "A_0_same", "A_4", "A_5_same"
  };
  private static final String[] CODES = {"0", "1", "2", "3", "5", "6", "7", "8", "0", "4", "5"};

  private BarographView() {}

  static void updateSensorAmount(Scene scene, String amount) {
    TextField field = (TextField) scene.lookup("#pressure_tendency");
    TextFormatter<?> formatter = field.getTextFormatter();
    field.setTextFormatter(null);
    field.setText(amount);
    field.setTextFormatter(formatter);
  }

  static void updateSensorSelection(Scene scene, int selected) {
    ((RadioButton) scene.lookup("#barograph_option_" + selected)).setSelected(true);
  }

  static Scene create(
      State state,
      Consumer<Input> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    TextField amount = new TextField(state.amount());
    amount.setId("pressure_tendency");
    amount.setPrefColumnCount(7);
    amount.setMaxWidth(110);
    amount.setEditable(!state.sensor());
    if (state.sensor()) amount.getStyleClass().add("sensor-value");
    amount.setTextFormatter(
        new TextFormatter<String>(change -> {
          if (change.isContentChange()) {
            change.setText(change.getText().replace(',', '.'));
            if (!change.getControlNewText().matches("-?\\d*(?:\\.\\d*)?")) return null;
          }
          return change;
        }));
    Label label = new Label("Pressure tendency during last 3 hours (hPa)");
    label.setLabelFor(amount);
    HBox amountRow = new HBox(10, label, amount, new Label("[0.0 - 50.0]"));
    amountRow.setAlignment(Pos.CENTER_LEFT);
    VBox amountCard = card("Amount of pressure tendency", amountRow);
    ToggleGroup selection = new ToggleGroup();
    VBox higher = group("Pressure higher than 3 hours ago", 0, 4, selection, state);
    VBox lower = group("Pressure lower than 3 hours ago", 4, 8, selection, state);
    VBox same = group("Pressure the same as 3 hours ago", 8, 11, selection, state);
    RadioButton undetermined = new RadioButton("Not determined");
    undetermined.setId("barograph_option_11");
    undetermined.setUserData(11);
    undetermined.setToggleGroup(selection);
    undetermined.setSelected(state.selected() == 11);
    undetermined.setDisable(state.sensor());
    Label heading = new Label("Barograph reading");
    heading.getStyleClass().add("heading");
    VBox content = new VBox(14, heading, amountCard, higher, lower, same, undetermined);
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
            new Input(amount.getText(), (int) selection.getSelectedToggle().getUserData())));
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
            Objects.requireNonNull(BarographView.class.getResource("cloud-cover.css"))
                .toExternalForm());
    return scene;
  }

  private static VBox group(String title, int first, int end, ToggleGroup selection, State state) {
    HBox choices = new HBox(18);
    choices.setAlignment(Pos.CENTER_LEFT);
    for (int i = first; i < end; i++) {
      ImageView image =
          new ImageView(
              new Image(
                  Objects.requireNonNull(
                          BarographView.class.getResource("icons/" + IMAGES[i] + ".jpg"))
                      .toExternalForm()));
      image.setFitWidth(100);
      image.setFitHeight(100);
      image.setPreserveRatio(true);
      RadioButton option = new RadioButton("(a = " + CODES[i] + ")");
      option.setId("barograph_option_" + i);
      option.setUserData(i);
      option.setToggleGroup(selection);
      option.setSelected(state.selected() == i);
      option.setDisable(state.sensor());
      VBox item = new VBox(4, image, option);
      item.setAlignment(Pos.CENTER);
      choices.getChildren().add(item);
    }
    return card(title, choices);
  }

  private static VBox card(String title, javafx.scene.Node content) {
    Label heading = new Label(title);
    heading.getStyleClass().add("group-title");
    VBox box = new VBox(8, heading, content);
    box.getStyleClass().add("choice-group");
    box.setPadding(new Insets(14));
    box.setMaxWidth(Double.MAX_VALUE);
    return box;
  }
}
