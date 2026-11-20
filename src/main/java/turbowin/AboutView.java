package turbowin;

import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/** JavaFX presentation of an AboutInfo snapshot. Must be created on the JavaFX thread. */
final class AboutView {
  private AboutView() {}

  static Scene create(AboutInfo info, Runnable close, Consumer<String> openLink) {
    Label title = new Label(info.title().replaceFirst("(\\[\\d+-bit\\])\\s+", "$1\n"));
    title.getStyleClass().add("title");
    title.setWrapText(true);
    Label endorsed =
        new Label(
            "Endorsed by the WMO (World Meteorological Organization) for use onboard voluntary observing ships");
    endorsed.setWrapText(true);
    endorsed.getStyleClass().add("subtitle");

    VBox details = new VBox(5);
    details.getStyleClass().add("details");
    if (main.THEME_NIMBUS_NIGHT.equals(info.theme())) {
      details.getStyleClass().add("night");
    } else if (main.THEME_NIMBUS_SUNRISE.equals(info.theme())) {
      details.getStyleClass().add("sunrise");
    } else if (main.THEME_NIMBUS_SUNSET.equals(info.theme())) {
      details.getStyleClass().add("sunset");
    }
    for (String line : info.details()) {
      Label label = new Label(line);
      label.setWrapText(true);
      label.setMaxWidth(Double.MAX_VALUE);
      details.getChildren().add(label);
    }

    VBox content = new VBox(16, title, endorsed, details);
    content.getStyleClass().add("content");
    content
        .getChildren()
        .addAll(
            linkSection(
                "icons/logo-sot.png",
                260,
                105,
                "TurboWin supports the Voluntary Observing Ship Scheme",
                () -> openLink.accept(AboutDialog.sotLink())),
            linkSection(
                "icons/gplv3-with-text-136x68.png",
                136,
                68,
                "TurboWin is free software and open-source software (GPLv3)",
                () -> openLink.accept(AboutDialog.sourceLink())));
    Label history = new Label("Turbo/TurboWin/TurboWin+ since 1990");
    history.getStyleClass().add("history");
    history.setMaxWidth(Double.MAX_VALUE);
    history.setAlignment(Pos.CENTER);
    history.setTextAlignment(TextAlignment.CENTER);
    history.setWrapText(true);
    content.getChildren().add(history);

    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.getStyleClass().add("about-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);
    Button ok = new Button("OK");
    ok.setDefaultButton(true);
    ok.setCancelButton(true);
    ok.setOnAction(event -> close.run());
    HBox footer = new HBox(ok);
    footer.setAlignment(Pos.CENTER);
    footer.getStyleClass().add("footer");
    VBox root = new VBox(scroll, footer);
    Scene scene = new Scene(root);
    scene.getStylesheets().add(resource("about.css").toExternalForm());
    return scene;
  }

  private static VBox linkSection(
      String imagePath, double maxWidth, double maxHeight, String text, Runnable action) {
    ImageView image = new ImageView(new Image(resource(imagePath).toExternalForm()));
    image.setPreserveRatio(true);
    image.setFitWidth(maxWidth);
    image.setFitHeight(maxHeight);
    Hyperlink link = new Hyperlink(text);
    link.setWrapText(true);
    link.setTextAlignment(TextAlignment.CENTER);
    link.setOnAction(event -> action.run());
    VBox section = new VBox(4, image, link);
    section.setAlignment(Pos.CENTER);
    return section;
  }

  private static URL resource(String path) {
    return Objects.requireNonNull(AboutView.class.getResource(path), "Missing resource: " + path);
  }
}
