package turbowin;

import java.util.List;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** JavaFX presentation; all application actions are supplied by the Swing workflow. */
final class CloudCoverView {
  record State(String n, String nh, String heightAdvice, boolean sequence, boolean offline) {}

  @FunctionalInterface
  interface Accept {
    void accept(String n, String nh, String h);
  }

  private static final List<String> TOTAL_COVER =
      List.of(
          CloudCoverState.N_CLOUDLESS,
          CloudCoverState.N_1_8,
          CloudCoverState.N_2_8,
          CloudCoverState.N_3_8,
          CloudCoverState.N_4_8,
          CloudCoverState.N_5_8,
          CloudCoverState.N_6_8,
          CloudCoverState.N_7_8,
          CloudCoverState.N_COMPLETELY_OVERCAST,
          CloudCoverState.N_OBSCURED,
          CloudCoverState.N_NOT_DETERMINED);
  private static final List<String> CLOUD_AMOUNT =
      List.of(
          CloudCoverState.NH_0_8,
          CloudCoverState.NH_1_8,
          CloudCoverState.NH_2_8,
          CloudCoverState.NH_3_8,
          CloudCoverState.NH_4_8,
          CloudCoverState.NH_5_8,
          CloudCoverState.NH_6_8,
          CloudCoverState.NH_7_8,
          CloudCoverState.NH_8_8,
          CloudCoverState.NH_OBSCURED,
          CloudCoverState.NH_NOT_DETERMINED);
  private static final List<String> CLOUD_HEIGHT =
      List.of(
          CloudCoverState.H_CLOUDLESS,
          CloudCoverState.H_0_50,
          CloudCoverState.H_50_100,
          CloudCoverState.H_100_200,
          CloudCoverState.H_200_300,
          CloudCoverState.H_300_600,
          CloudCoverState.H_600_1000,
          CloudCoverState.H_1000_1500,
          CloudCoverState.H_1500_2000,
          CloudCoverState.H_2000_2500,
          CloudCoverState.H_GROTER_2500,
          CloudCoverState.H_NOT_DETERMINED);

  private CloudCoverView() {}

  static Scene create(
      State state, Accept accept, Runnable cancel, Runnable back, Runnable stop, Runnable help) {
    ToggleGroup totalCover = new ToggleGroup();
    ToggleGroup cloudAmount = new ToggleGroup();
    ToggleGroup cloudHeight = new ToggleGroup();
    VBox content = new VBox(18);
    content.setPadding(new Insets(24));
    content.getStyleClass().add("cloud-cover-content");
    Label heading = new Label("Cloud observation");
    heading.getStyleClass().add("heading");
    HBox groups = new HBox(16);
    groups
        .getChildren()
        .addAll(
            group(
                "total",
                "Total cloud cover (N)",
                TOTAL_COVER,
                totalCover,
                state.n(),
                CloudCoverState.N_NOT_DETERMINED),
            group(
                "cl",
                "Amount of Cl (or Cm if Cl not present)",
                CLOUD_AMOUNT,
                cloudAmount,
                state.nh(),
                CloudCoverState.NH_NOT_DETERMINED),
            group(
                "low",
                "Lowest cloud-base height (h)",
                CLOUD_HEIGHT,
                cloudHeight,
                state.heightAdvice(),
                CloudCoverState.H_NOT_DETERMINED));
    Label hint =
        new Label(
            "This program computes a 'height of base of lowest cloud' advice if cloud classification, latitude, air temp, wet bulb temp and present weather are available. This advice can always be overruled by checking another button.");
    hint.setWrapText(true);
    hint.getStyleClass().add("hint");
    HBox buttons = new HBox(10);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    Button backButton = new Button("Back");
    Button stopButton = new Button("Stop");
    Button cancelButton = new Button("Cancel");
    Button ok = new Button("OK");
    helpButton.setOnAction(event -> help.run());
    backButton.setDisable(!state.sequence());
    stopButton.setDisable(!state.sequence());
    backButton.setOnAction(event -> back.run());
    stopButton.setOnAction(event -> stop.run());
    cancelButton.setOnAction(event -> cancel.run());
    ok.setDefaultButton(true);
    ok.setOnAction(
        event -> accept.accept(selected(totalCover), selected(cloudAmount), selected(cloudHeight)));
    buttons.getChildren().addAll(backButton, ok, cancelButton, helpButton, stopButton);
    content.getChildren().addAll(heading, groups, hint);
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.setFitToHeight(true);
    scroll.getStyleClass().add("cloud-cover-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(CloudCoverView.class.getResource("cloud-cover.css"))
                .toExternalForm());
    return scene;
  }

  private static VBox group(
      String id,
      String title,
      List<String> values,
      ToggleGroup toggles,
      String current,
      String fallback) {
    VBox box = new VBox(4);
    box.setId(id);
    box.setPadding(new Insets(12));
    box.getStyleClass().add("choice-group");
    HBox.setHgrow(box, Priority.ALWAYS);
    Label label = new Label(title);
    label.setWrapText(true);
    label.getStyleClass().add("group-title");
    box.getChildren().add(label);
    String selected = values.contains(current) ? current : fallback;
    for (String value : values) {
      RadioButton button = new RadioButton(value);
      button.setUserData(value);
      button.setToggleGroup(toggles);
      button.setSelected(value.equals(selected));
      box.getChildren().add(button);
    }
    return box;
  }

  private static String selected(ToggleGroup group) {
    Toggle selected = group.getSelectedToggle();
    return selected == null ? "" : (String) selected.getUserData();
  }
}
