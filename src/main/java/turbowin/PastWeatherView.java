package turbowin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Multiple past-weather conditions, in descending observation priority. */
final class PastWeatherView {
  record Choice(String code, String label, String value) {}

  record State(String first, String second, boolean sequence, boolean offline) {}

  static final List<Choice> CHOICES =
      List.of(
          new Choice(
              "9",
              "thunderstorm(s), with or without precipitation",
              "thunderstorm(s), with or without precipitation"),
          new Choice("8", "shower(s)", "shower(s)"),
          new Choice("7", "snow, or rain and snow mixed", "snow, or rain and snow mixed"),
          new Choice("6", "rain", "rain"),
          new Choice("5", "drizzle", "drizzle"),
          new Choice(
              "4",
              "fog or ice fog or thick haze (visibility < 0.5 nm)",
              "fog or ice fog of thick haze (visibility < 0.5 nm)"),
          new Choice(
              "3", "sandstorm, duststorm or blowing snow", "sandstorm, duststorm or blowing snow"),
          new Choice(
              "2", "sky covering > 0.5 throughout period", "sky covering > 0.5 throughout period"),
          new Choice(
              "1",
              "sky covering > 0.5 and <= 0.5 during parts of period",
              "sky covering > 0.5 and <= 0.5 during parts period"),
          new Choice(
              "0",
              "sky covering <= 0.5 throughout period",
              "sky covering <= 0.5 throughout period"));

  private PastWeatherView() {}

  static Scene create(
      State state,
      Consumer<List<Choice>> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    Label heading = new Label("Past weather");
    heading.getStyleClass().add("heading");
    Label description =
        hint(
            "Past weather and present weather should together describe the weather during the observation period. If the weather changes completely, past weather describes the conditions before the present weather.");
    Label period =
        hint(
            "Observation period: 6 hours at 0000, 0600, 1200 and 1800 UTC; 3 hours at 0300, 0900, 1500 and 2100 UTC; 1 hour at the other observation times.");
    Label instruction = new Label("Select all applicable weather conditions");
    instruction.getStyleClass().add("group-title");
    VBox choices = new VBox(2);
    List<CheckBox> checkboxes = new ArrayList<>();
    for (Choice choice : CHOICES) {
      CheckBox box = new CheckBox(choice.label());
      box.setUserData(choice);
      box.setSelected(
          choice.value().equals(state.first()) || choice.value().equals(state.second()));
      checkboxes.add(box);
      choices.getChildren().add(box);
    }
    VBox card = new VBox(6, instruction, choices);
    card.getStyleClass().add("choice-group");
    card.setPadding(new Insets(14, 20, 14, 20));
    card.setMaxWidth(560);
    VBox content = new VBox(14, heading, description, period, card);
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
            checkboxes.stream()
                .filter(CheckBox::isSelected)
                .map(box -> (Choice) box.getUserData())
                .toList()));
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
        .add(PastWeatherView.class.getResource("cloud-cover.css").toExternalForm());
    return scene;
  }

  private static Label hint(String text) {
    Label label = new Label(text);
    label.setWrapText(true);
    label.setMaxWidth(720);
    label.getStyleClass().add("hint");
    return label;
  }
}
