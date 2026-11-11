package turbowin;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;

/** JavaFX replacement for the legacy cloud-cover Swing frame. */
final class CloudCoverJavaFxWindow {

  private static final AtomicBoolean PLATFORM_STARTED = new AtomicBoolean();

  private static final List<String> TOTAL_COVER =
      List.of(
          mycloudcover.N_CLOUDLESS,
          mycloudcover.N_1_8,
          mycloudcover.N_2_8,
          mycloudcover.N_3_8,
          mycloudcover.N_4_8,
          mycloudcover.N_5_8,
          mycloudcover.N_6_8,
          mycloudcover.N_7_8,
          mycloudcover.N_COMPLETELY_OVERCAST,
          mycloudcover.N_OBSCURED,
          mycloudcover.N_NOT_DETERMINED);
  private static final List<String> CLOUD_AMOUNT =
      List.of(
          mycloudcover.NH_0_8,
          mycloudcover.NH_1_8,
          mycloudcover.NH_2_8,
          mycloudcover.NH_3_8,
          mycloudcover.NH_4_8,
          mycloudcover.NH_5_8,
          mycloudcover.NH_6_8,
          mycloudcover.NH_7_8,
          mycloudcover.NH_8_8,
          mycloudcover.NH_OBSCURED,
          mycloudcover.NH_NOT_DETERMINED);
  private static final List<String> CLOUD_HEIGHT =
      List.of(
          mycloudcover.H_CLOUDLESS,
          mycloudcover.H_0_50,
          mycloudcover.H_50_100,
          mycloudcover.H_100_200,
          mycloudcover.H_200_300,
          mycloudcover.H_300_600,
          mycloudcover.H_600_1000,
          mycloudcover.H_1000_1500,
          mycloudcover.H_1500_2000,
          mycloudcover.H_2000_2500,
          mycloudcover.H_GROTER_2500,
          mycloudcover.H_NOT_DETERMINED);

  private final Stage stage = new Stage();
  private final ToggleGroup totalCover = new ToggleGroup();
  private final ToggleGroup cloudAmount = new ToggleGroup();
  private final ToggleGroup cloudHeight = new ToggleGroup();

  private CloudCoverJavaFxWindow() {
    stage.setTitle("Total cloud cover, amount of Cl (Cm) and height of base of lowest cloud");
    stage.setResizable(false);
    Scene scene = new Scene(buildContent(), 800, 600);
    scene.getStylesheets().add(getClass().getResource("/turbowin/cloud-cover.css").toExternalForm());
    stage.setScene(scene);
  }

  static void open() {
    Runnable show = () -> new CloudCoverJavaFxWindow().stage.show();
    if (PLATFORM_STARTED.compareAndSet(false, true)) Platform.startup(show);
    else Platform.runLater(show);
  }

  private VBox buildContent() {
    VBox root = new VBox(18);
    root.setPadding(new Insets(24));
    root.getStyleClass().add("cloud-cover-root");

    Label heading = new Label("Cloud observation");
    heading.getStyleClass().add("heading");

    HBox groups = new HBox(16);
    groups.getChildren().addAll(
        group("Total cloud cover (N)", TOTAL_COVER, totalCover, mycloudcover.N, mycloudcover.N_NOT_DETERMINED),
        group("Amount of Cl / Cm (Nh)", CLOUD_AMOUNT, cloudAmount, mycloudcover.Nh, mycloudcover.NH_NOT_DETERMINED),
        group("Lowest cloud-base height (h)", CLOUD_HEIGHT, cloudHeight, mycloudcover.h, advisedHeight()));

    Label hint = new Label("Choose one value in each group. Existing observations are preselected.");
    hint.getStyleClass().add("hint");

    HBox buttons = new HBox(10);
    buttons.setAlignment(Pos.CENTER_RIGHT);
    Button help = new Button(main.offline_mode ? "Help" : "Internet");
    Button back = new Button("Back");
    Button stop = new Button("Stop");
    Button cancel = new Button("Cancel");
    Button ok = new Button("OK");
    help.setOnAction(
        event -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("cloud_cover_height.pdf")));
    back.setDisable(!main.in_next_sequence);
    stop.setDisable(!main.in_next_sequence);
    back.setOnAction(event -> navigateBack());
    stop.setOnAction(event -> {
      main.in_next_sequence = false;
      stage.close();
    });
    cancel.setOnAction(event -> closeAndContinueIfNeeded());
    ok.setDefaultButton(true);
    ok.setOnAction(event -> accept());
    buttons.getChildren().addAll(help, back, stop, cancel, ok);

    root.getChildren().addAll(heading, groups, hint, buttons);
    VBox.setMargin(buttons, new Insets(12, 0, 0, 0));
    return root;
  }

  private VBox group(
      String title, List<String> values, ToggleGroup toggleGroup, String current, String fallback) {
    VBox box = new VBox(4);
    box.setPadding(new Insets(12));
    box.getStyleClass().add("choice-group");
    Label label = new Label(title);
    label.getStyleClass().add("group-title");
    box.getChildren().add(label);
    String selected = values.contains(current) ? current : fallback;
    for (String value : values) {
      RadioButton button = new RadioButton(value);
      button.setUserData(value);
      button.setToggleGroup(toggleGroup);
      button.setSelected(value.equals(selected));
      box.getChildren().add(button);
    }
    return box;
  }

  private String advisedHeight() {
    int advice = mycloudcover.computeHeightAdvice();
    if (advice == -9) return mycloudcover.H_CLOUDLESS;
    if (advice == 0) return mycloudcover.H_0_50;
    if (advice == 1) return mycloudcover.H_50_100;
    if (advice == 2) return mycloudcover.H_100_200;
    if (advice == 3) return mycloudcover.H_200_300;
    if (advice == 4) return mycloudcover.H_300_600;
    if (advice == 5) return mycloudcover.H_600_1000;
    if (advice == 6) return mycloudcover.H_1000_1500;
    if (advice == 7) return mycloudcover.H_1500_2000;
    if (advice == 8) return mycloudcover.H_2000_2500;
    if (advice == 9) return mycloudcover.H_GROTER_2500;
    return mycloudcover.H_NOT_DETERMINED;
  }

  private String selected(ToggleGroup group) {
    Toggle selected = group.getSelectedToggle();
    return selected == null ? "" : (String) selected.getUserData();
  }

  private void accept() {
    CloudCoverSelection.Result result =
        CloudCoverSelection.validate(selected(totalCover), selected(cloudAmount), selected(cloudHeight));
    if (!result.valid()) {
      mycloudcover.N = "";
      mycloudcover.Nh = "";
      mycloudcover.h = "";
      mycloudcover.N_code = "";
      mycloudcover.Nh_code = "";
      mycloudcover.h_code = "";
      SwingUtilities.invokeLater(main::cloud_cover_fields_update);
      Alert alert = new Alert(Alert.AlertType.WARNING, result.error);
      alert.setHeaderText(main.APPLICATION_NAME + " error");
      alert.showAndWait();
      return;
    }
    mycloudcover.N = result.n;
    mycloudcover.Nh = result.nh;
    mycloudcover.h = result.h;
    mycloudcover.N_code = result.nCode;
    mycloudcover.Nh_code = result.nhCode;
    mycloudcover.h_code = result.hCode;
    SwingUtilities.invokeLater(main::cloud_cover_fields_update);
    stage.close();
    if (main.in_next_sequence) navigateNext();
  }

  private void closeAndContinueIfNeeded() {
    stage.close();
    if (main.in_next_sequence) navigateNext();
  }

  private void navigateBack() {
    stage.close();
    SwingUtilities.invokeLater(() -> {
      mych form = new mych();
      form.setSize(800, 600);
      form.setVisible(true);
    });
  }

  private void navigateNext() {
    SwingUtilities.invokeLater(() -> {
      myobserver form = new myobserver();
      form.setSize(800, 600);
      form.setVisible(true);
    });
  }
}
