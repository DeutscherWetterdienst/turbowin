package turbowin;

import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Visibility ranges and their observation codes. */
final class VisibilityView {
  record Choice(String value, String code, String metres) {}

  record State(String value, boolean sequence, boolean offline) {}

  static final List<Choice> CHOICES =
      List.of(
          new Choice("< 0.03 nm", "90", "< 50 metres"),
          new Choice("0.03 - 0.1 nm", "91", "50 - 200 metres"),
          new Choice("0.1 - 0.3 nm", "92", "200 - 500 metres"),
          new Choice("0.3 - 0.5 nm", "93", "500 - 1000 metres"),
          new Choice("0.5 - 1.1 nm", "94", ""),
          new Choice("1.1 - 2.2 nm", "95", ""),
          new Choice("2.2 - 5.5 nm", "96", ""),
          new Choice("5.5 - 11 nm", "97", ""),
          new Choice("11 - 27 nm", "98", ""),
          new Choice("> 27 nm", "99", ""),
          new Choice("not determined", "//", ""));

  private VisibilityView() {}

  static Scene create(
      State state,
      Consumer<Choice> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    Label heading = new Label("Visibility");
    heading.getStyleClass().add("heading");
    Label hint =
        new Label(
            "When visibility is not uniform in all directions, estimate it in the direction of least visibility.");
    hint.setWrapText(true);
    hint.setMaxWidth(650);
    hint.setAlignment(Pos.CENTER);
    hint.getStyleClass().add("hint");
    ToggleGroup selection = new ToggleGroup();
    GridPane options = new GridPane();
    options.setHgap(18);
    options.setVgap(2);
    options.setAlignment(Pos.CENTER);
    String selectedValue = state.value().isEmpty() ? "not determined" : state.value();
    for (int i = 0; i < CHOICES.size(); i++) {
      Choice choice = CHOICES.get(i);
      Label metres = new Label(choice.metres());
      metres.getStyleClass().add("hint");
      RadioButton button = new RadioButton(choice.value());
      button.setUserData(choice);
      button.setToggleGroup(selection);
      button.setSelected(choice.value().equals(selectedValue));
      options.addRow(i, metres, button);
    }
    VBox card = new VBox(options);
    card.getStyleClass().add("choice-group");
    card.setPadding(new Insets(16, 24, 16, 24));
    card.setMaxWidth(380);
    VBox content = new VBox(18, heading, hint, card);
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
    ok.setOnAction(e -> {
      Toggle selected = selection.getSelectedToggle();
      accept.accept(selected == null ? null : (Choice) selected.getUserData());
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
        .add(VisibilityView.class.getResource("cloud-cover.css").toExternalForm());
    return scene;
  }
}
