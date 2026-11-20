package turbowin;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.skin.ComboBoxListViewSkin;

/** Applies the Swing theme to embedded JavaFX scenes. */
final class FxTheme {
  private static final Set<Scene> scenes = Collections.newSetFromMap(new WeakHashMap<>());
  private static volatile boolean night;
  private static volatile boolean toolkitReady;

  private FxTheme() {}

  /** Called on the Swing thread; JavaFX may not have been initialized yet. */
  static void setTheme(String theme) {
    night = main.THEME_NIMBUS_NIGHT.equals(theme);
    if (toolkitReady) Platform.runLater(FxTheme::refresh);
  }

  static void toolkitReady() {
    toolkitReady = true;
  }

  /** Called on the JavaFX thread when an embedded scene is created. */
  static void register(Scene scene) {
    scene.getStylesheets().add(FxTheme.class.getResource("night.css").toExternalForm());
    scenes.add(scene);
    apply(scene);
  }

  static void unregister(Scene scene) {
    if (scene != null) scenes.remove(scene);
  }

  private static void refresh() {
    for (Scene scene : scenes) apply(scene);
  }

  private static void apply(Scene scene) {
    applyClass(scene.getRoot());
    // Combo-box popups use a separate window and do not always restyle when their owner changes.
    scene.getRoot().applyCss();
    for (var node : scene.getRoot().lookupAll(".combo-box")) {
      if (node instanceof ComboBox<?> combo
          && combo.getSkin() instanceof ComboBoxListViewSkin<?> skin) {
        var popup = skin.getPopupContent();
        if (popup != null) {
          applyClass(popup);
          popup.applyCss();
        }
      }
    }
  }

  private static void applyClass(javafx.scene.Node node) {
    var classes = node.getStyleClass();
    if (night) {
      if (!classes.contains("night-theme")) classes.add("night-theme");
    } else classes.remove("night-theme");
  }
}
