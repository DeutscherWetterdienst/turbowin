package turbowin;

import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** JavaFX high-cloud selector; callbacks return application work to the Swing workflow. */
final class HighCloudView {
  record State(String code, boolean sequence, boolean offline) {}

  private HighCloudView() {}

  static Scene create(
      State state,
      Consumer<String> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Consumer<String> help) {
    ToggleGroup selection = new ToggleGroup();
    VBox content = new VBox(14);
    content.setPadding(new Insets(20, 24, 20, 24));
    content.setAlignment(Pos.TOP_CENTER);
    Label heading = new Label("Clouds high");
    heading.getStyleClass().add("heading");
    Label hint =
        new Label(
            state.offline()
                ? "Click on the appropriate thumbnail for more cloud photos."
                : "Click on the appropriate thumbnail for more internet photos.");
    hint.setWrapText(true);
    hint.getStyleClass().add("hint");
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(40);
    grid.setVgap(12);
    for (int index = 1; index <= 9; index++) {
      int number = index;
      Image image = new Image(resource("icons/glyph_ch_" + number + ".jpg").toExternalForm());
      ImageView thumbnail = new ImageView(image);
      thumbnail.setFitWidth(150);
      thumbnail.setFitHeight(100);
      thumbnail.setPreserveRatio(true);
      Hyperlink photo = new Hyperlink();
      photo.setGraphic(thumbnail);
      photo.setAccessibleText("Ch" + number + " cloud photos");
      photo.setTooltip(new Tooltip("Ch" + number + " cloud photos"));
      photo.getStyleClass().add("cloud-photo");
      photo.setOnAction(event -> help.accept("ch_" + number + ".pdf"));
      RadioButton option = option("Ch" + number, Integer.toString(number), selection, state.code());
      VBox item = new VBox(4, photo, option);
      item.setAlignment(Pos.CENTER_LEFT);
      grid.add(item, (number - 1) % 3, (number - 1) / 3);
    }
    HBox other =
        new HBox(
            40,
            option("no clouds Ch", "0", selection, state.code()),
            option("not determined", "", selection, state.code()));
    other.setAlignment(Pos.CENTER);
    content.getChildren().addAll(heading, hint, grid, other);

    Button backButton = new Button("Back");
    Button ok = new Button("OK");
    Button cancelButton = new Button("Cancel");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    Button stopButton = new Button("Stop");
    backButton.setDisable(!state.sequence());
    stopButton.setDisable(!state.sequence());
    backButton.setOnAction(event -> back.run());
    ok.setDefaultButton(true);
    ok.setOnAction(event -> {
      Toggle selected = selection.getSelectedToggle();
      accept.accept(selected == null ? "" : (String) selected.getUserData());
    });
    cancelButton.setOnAction(event -> cancel.run());
    helpButton.setOnAction(event -> help.accept("ch.pdf"));
    stopButton.setOnAction(event -> stop.run());
    HBox buttons = new HBox(10, backButton, ok, cancelButton, helpButton, stopButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.setFitToHeight(true);
    scroll.getStyleClass().add("cloud-cover-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene.getStylesheets().add(resource("cloud-cover.css").toExternalForm());
    scene.getStylesheets().add(resource("high-cloud.css").toExternalForm());
    return scene;
  }

  private static RadioButton option(String text, String code, ToggleGroup group, String current) {
    RadioButton option = new RadioButton(text);
    option.setUserData(code);
    option.setToggleGroup(group);
    option.setSelected(code.equals(current));
    return option;
  }

  private static URL resource(String path) {
    return Objects.requireNonNull(
        HighCloudView.class.getResource(path), "Missing resource: " + path);
  }
}
