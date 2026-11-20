package turbowin;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

final class CloudCoverFxTestSupport {
  private CloudCoverFxTestSupport() {}

  static Scene awaitScene(JDialog dialog) throws Exception {
    JFXPanel[] panel = new JFXPanel[1];
    SwingUtilities.invokeAndWait(
        () -> panel[0] = (JFXPanel) dialog.getContentPane().getComponent(0));
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
    while (System.nanoTime() < deadline) {
      Scene scene = onFx(panel[0]::getScene);
      if (scene != null) return scene;
      Thread.sleep(25);
    }
    throw new AssertionError("Cloud-cover JavaFX scene was not created");
  }

  static <T> T onFx(Callable<T> action) throws Exception {
    FutureTask<T> task = new FutureTask<>(action);
    Platform.runLater(task);
    return task.get(15, TimeUnit.SECONDS);
  }

  static void choose(Scene scene, String group, String value) {
    VBox choices = (VBox) scene.getRoot().lookup("#" + group);
    choices.getChildren().stream()
        .filter(RadioButton.class::isInstance)
        .map(RadioButton.class::cast)
        .filter(button -> value.equals(button.getUserData()))
        .findFirst()
        .orElseThrow()
        .fire();
  }

  static void click(Scene scene, String text) {
    scene.getRoot().lookupAll(".button").stream()
        .filter(Button.class::isInstance)
        .map(Button.class::cast)
        .filter(button -> text.equals(button.getText()))
        .findFirst()
        .orElseThrow()
        .fire();
  }

  static void selectRadio(Scene scene, String code) {
    scene.getRoot().lookupAll(".radio-button").stream()
        .filter(RadioButton.class::isInstance)
        .map(RadioButton.class::cast)
        .filter(button -> code.equals(button.getUserData()))
        .findFirst()
        .orElseThrow()
        .fire();
  }
}
