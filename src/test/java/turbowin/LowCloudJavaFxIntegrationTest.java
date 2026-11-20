package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

/** Verifies the embedded low-cloud view and its Swing observation workflow. */
public class LowCloudJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void thumbnailsOpenTheirHelpWithoutChangingTheSelection() throws Exception {
    AtomicReference<JDialog> window = new AtomicReference<>();
    List<String> opened = new ArrayList<>();
    SwingUtilities.invokeAndWait(() ->
        window.set(
            FxSwingDialog.show(
                frame.target(),
                "Low clouds test",
                new Dimension(800, 650),
                close ->
                    LowCloudView.create(
                        new LowCloudView.State("6", false, true),
                        code -> {},
                        close,
                        close,
                        close,
                        opened::add))));
    Scene scene = CloudCoverFxTestSupport.awaitScene(window.get());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      List<Hyperlink> photos =
          scene.getRoot().lookupAll(".hyperlink").stream().map(Hyperlink.class::cast).toList();
      assertEquals(9, photos.size());
      for (int number = 1; number <= 9; number++) {
        String label = "Cl" + number + " cloud photos";
        Hyperlink photo =
            photos.stream()
                .filter(item -> label.equals(item.getAccessibleText()))
                .findFirst()
                .orElseThrow();
        assertFalse(((ImageView) photo.getGraphic()).getImage().isError());
        photo.fire();
        assertEquals("cl_" + number + ".pdf", opened.get(number - 1));
      }
      RadioButton selected =
          scene.getRoot().lookupAll(".radio-button").stream()
              .map(RadioButton.class::cast)
              .filter(RadioButton::isSelected)
              .findFirst()
              .orElseThrow();
      assertEquals("6", selected.getUserData());
      CloudCoverFxTestSupport.click(scene, "Help");
      assertEquals("cl.pdf", opened.get(9));
      VBox root = (VBox) scene.getRoot();
      HBox footer = (HBox) root.getChildren().get(1);
      assertEquals(Pos.CENTER, footer.getAlignment());
      assertEquals(
          List.of("Back", "OK", "Cancel", "Help", "Stop"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      assertTrue(((Button) footer.getChildren().get(0)).isDisabled());
      assertTrue(((Button) footer.getChildren().get(4)).isDisabled());
      return null;
    });
    SwingUtilities.invokeAndWait(window.get()::dispose);
  }

  @Test
  public void savesSelectionsAndRestoresThemOnReopening() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      LowCloudState.cl_code = "";
    });
    for (String code : List.of("6", "0", "")) {
      JDialog dialog = GuiActionRunner.execute(() -> LowCloudDialog.open());
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        scene.getRoot().applyCss();
        CloudCoverFxTestSupport.selectRadio(scene, code);
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals(code, LowCloudState.cl_code);
        assertEquals(code.isEmpty() ? "" : code + " (code)", main.jTextField33.getText());
      });
      JDialog reopened = GuiActionRunner.execute(() -> LowCloudDialog.open());
      Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
      CloudCoverFxTestSupport.onFx(() -> {
        reopenedScene.getRoot().applyCss();
        RadioButton selected =
            reopenedScene.getRoot().lookupAll(".radio-button").stream()
                .map(RadioButton.class::cast)
                .filter(RadioButton::isSelected)
                .findFirst()
                .orElseThrow();
        assertEquals(code, selected.getUserData());
        CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
    }
  }

  @Test
  public void sequenceMovesForwardBackwardAndCanBeStopped() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = true;
      LowCloudState.cl_code = "1";
    });
    try {
      JDialog cancelDialog = GuiActionRunner.execute(() -> LowCloudDialog.open());
      Scene cancelScene = CloudCoverFxTestSupport.awaitScene(cancelDialog);
      CloudCoverFxTestSupport.onFx(() -> {
        cancelScene.getRoot().applyCss();
        CloudCoverFxTestSupport.selectRadio(cancelScene, "7");
        CloudCoverFxTestSupport.click(cancelScene, "Cancel");
        return null;
      });
      DialogFixture cancelCover =
          WindowFinder.findDialog(new DialogTitleMatcher("Clouds middle")).using(robot());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("1", LowCloudState.cl_code);
        cancelCover.target().dispose();
      });

      JDialog dialog = GuiActionRunner.execute(() -> LowCloudDialog.open());
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        scene.getRoot().applyCss();
        CloudCoverFxTestSupport.selectRadio(scene, "4");
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      DialogFixture cover =
          WindowFinder.findDialog(new DialogTitleMatcher("Clouds middle")).using(robot());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("4", LowCloudState.cl_code);
        cover.target().dispose();
      });

      JDialog backDialog = GuiActionRunner.execute(() -> LowCloudDialog.open());
      Scene backScene = CloudCoverFxTestSupport.awaitScene(backDialog);
      CloudCoverFxTestSupport.onFx(() -> {
        backScene.getRoot().applyCss();
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      DialogFixture pastWeather =
          WindowFinder.findDialog(new DialogTitleMatcher("Past weather")).using(robot());
      SwingUtilities.invokeAndWait(pastWeather.target()::dispose);

      JDialog stopDialog = GuiActionRunner.execute(() -> LowCloudDialog.open());
      Scene stopScene = CloudCoverFxTestSupport.awaitScene(stopDialog);
      CloudCoverFxTestSupport.onFx(() -> {
        stopScene.getRoot().applyCss();
        CloudCoverFxTestSupport.selectRadio(stopScene, "7");
        CloudCoverFxTestSupport.click(stopScene, "Stop");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(stopDialog.isDisplayable());
        assertFalse(main.in_next_sequence);
        assertEquals("4", LowCloudState.cl_code);
      });
    } finally {
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    }
  }
}
