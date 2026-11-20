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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Ice accretion input for the existing observation workflow. */
final class IcingView {
  record Choice(String code, String label, boolean extended) {}

  record State(String cause, String thickness, String rate, boolean extended, boolean offline) {}

  record Input(String cause, String thickness, String rate) {}

  static final List<Choice> CAUSES =
      List.of(
          new Choice("1", "icing from sea spray", false),
          new Choice("2", "icing from fog", false),
          new Choice("3", "icing from spray and fog", false),
          new Choice("4", "icing from rain", false),
          new Choice("5", "icing from spray and rain", false),
          new Choice("6", "icing from fog and rain", true),
          new Choice("14", "icing from spray and fog and rain", true),
          new Choice("", "not determined", false));
  static final List<Choice> RATES =
      List.of(
          new Choice("0", "ice not building up", false),
          new Choice("1", "ice building up slowly", false),
          new Choice("2", "ice building up rapidly", false),
          new Choice("3", "ice melting or breaking up slowly", false),
          new Choice("4", "ice melting or breaking up rapidly", false),
          new Choice("", "not determined", false));

  private IcingView() {}

  static Scene create(State state, Consumer<Input> accept, Runnable cancel, Runnable help) {
    String value = state.thickness().matches("-?\\d*(?:\\.\\d*)?") ? state.thickness() : "";
    TextField thickness = new TextField(value);
    thickness.setId("icing_thickness");
    thickness.setPrefColumnCount(6);
    thickness.setMaxWidth(110);
    thickness.setTextFormatter(
        new TextFormatter<String>(change -> {
          if (change.isContentChange()) {
            change.setText(change.getText().replace(',', '.'));
            if (!change.getControlNewText().matches("-?\\d*(?:\\.\\d*)?")) return null;
          }
          return change;
        }));
    Label fieldLabel = new Label("Thickness of ice accretion (cm)");
    fieldLabel.setLabelFor(thickness);
    Label range = new Label("0–99");
    range.getStyleClass().add("hint");
    HBox measurement = new HBox(10, fieldLabel, thickness, range);
    measurement.setAlignment(Pos.CENTER);
    ToggleGroup causes = new ToggleGroup();
    ToggleGroup rates = new ToggleGroup();
    VBox causesBox =
        options("Cause of ice accretion", CAUSES, causes, state.cause(), state.extended());
    VBox ratesBox = options("Rate of ice accretion", RATES, rates, state.rate(), true);
    causesBox.setId("icing_cause");
    ratesBox.setId("icing_rate");
    HBox.setHgrow(causesBox, Priority.ALWAYS);
    HBox.setHgrow(ratesBox, Priority.ALWAYS);
    causesBox.setMaxWidth(Double.MAX_VALUE);
    ratesBox.setMaxWidth(Double.MAX_VALUE);
    HBox columns = new HBox(16, causesBox, ratesBox);
    Label heading = new Label("Ice accretion (icing)");
    heading.getStyleClass().add("heading");
    VBox content = new VBox(20, heading, measurement, columns);
    content.setPadding(new Insets(20, 24, 20, 24));
    content.setAlignment(Pos.TOP_CENTER);
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.getStyleClass().add("cloud-cover-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);
    Button ok = new Button("OK");
    Button cancelButton = new Button("Cancel");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    ok.setDefaultButton(true);
    ok.setOnAction(
        e -> accept.accept(new Input(selected(causes), thickness.getText(), selected(rates))));
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    HBox buttons = new HBox(10, ok, cancelButton, helpButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene.getStylesheets().add(IcingView.class.getResource("cloud-cover.css").toExternalForm());
    return scene;
  }

  private static VBox options(
      String title, List<Choice> choices, ToggleGroup group, String saved, boolean extended) {
    Label heading = new Label(title);
    heading.getStyleClass().add("group-title");
    VBox box = new VBox(5);
    box.getStyleClass().add("choice-group");
    box.setPadding(new Insets(14));
    box.getChildren().add(heading);
    String selected = choices.stream().anyMatch(choice -> choice.code().equals(saved)) ? saved : "";
    for (Choice choice : choices) {
      RadioButton button = new RadioButton(choice.label());
      button.setUserData(choice.code());
      button.setToggleGroup(group);
      button.setVisible(!choice.extended() || extended);
      button.setManaged(button.isVisible());
      button.setSelected(choice.code().equals(selected));
      box.getChildren().add(button);
    }
    return box;
  }

  private static String selected(ToggleGroup group) {
    Toggle toggle = group.getSelectedToggle();
    return toggle == null ? "" : (String) toggle.getUserData();
  }
}
