package turbowin;

import java.net.URL;
import java.util.List;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** JavaFX middle-cloud selector; callbacks return application work to the Swing workflow. */
final class MiddleCloudView {
  record State(String code, boolean sequence, boolean offline) {}

  private static final List<List<String>> ROWS =
      List.of(List.of("1", "2", "3"), List.of("4", "5", "6", "7a"), List.of("7b", "7c", "8", "9"));

  private MiddleCloudView() {}

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
    Label heading = new Label("Clouds middle");
    heading.getStyleClass().add("heading");
    Label hint =
        new Label(
            state.offline()
                ? "Click on the appropriate thumbnail for more cloud photos."
                : "Click on the appropriate thumbnail for more internet photos.");
    hint.setWrapText(true);
    hint.getStyleClass().add("hint");
    content.getChildren().addAll(heading, hint);
    for (List<String> row : ROWS) {
      HBox photos = new HBox(12);
      photos.setAlignment(Pos.CENTER);
      for (String code : row) {
        Image image = new Image(resource("icons/glyph_cm_" + code + ".jpg").toExternalForm());
        ImageView thumbnail = new ImageView(image);
        thumbnail.setFitWidth(150);
        thumbnail.setFitHeight(100);
        thumbnail.setPreserveRatio(true);
        Hyperlink photo = new Hyperlink();
        photo.setGraphic(thumbnail);
        photo.setAccessibleText("Cm" + code + " cloud photos");
        photo.setTooltip(new Tooltip("Cm" + code + " cloud photos"));
        photo.getStyleClass().add("cloud-photo");
        photo.setOnAction(event -> help.accept("cm_" + code.charAt(0) + ".pdf"));
        RadioButton option = option("Cm" + code.charAt(0), code, selection, state.code());
        option.setAccessibleText("Cm" + code);
        VBox item = new VBox(4, photo, option);
        item.setAlignment(Pos.CENTER_LEFT);
        photos.getChildren().add(item);
      }
      content.getChildren().add(photos);
    }
    HBox other =
        new HBox(
            40,
            option("no clouds Cm", "0", selection, state.code()),
            option("not determined", "", selection, state.code()));
    other.setAlignment(Pos.CENTER);
    content.getChildren().add(other);

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
    helpButton.setOnAction(event -> help.accept("cm.pdf"));
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
    scene.getStylesheets().add(resource("cloud-photos.css").toExternalForm());
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
        MiddleCloudView.class.getResource(path), "Missing resource: " + path);
  }
}
