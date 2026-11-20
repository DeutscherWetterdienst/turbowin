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
import org.assertj.swing.fixture.FrameFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;
import turbowin.matchers.FrameTitleMatcher;

/** Verifies the embedded middle-cloud view and its Swing observation workflow. */
public class MiddleCloudJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void thumbnailsOpenTheirHelpWithoutChangingTheSelection() throws Exception {
    AtomicReference<JDialog> window = new AtomicReference<>();
    List<String> opened = new ArrayList<>();
    SwingUtilities.invokeAndWait(() ->
        window.set(
            FxSwingDialog.show(
                frame.target(),
                "Middle clouds test",
                new Dimension(800, 650),
                close ->
                    MiddleCloudView.create(
                        new MiddleCloudView.State("7b", false, true),
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
      List<String> codes = List.of("1", "2", "3", "4", "5", "6", "7a", "7b", "7c", "8", "9");
      assertEquals(codes.size(), photos.size());
      for (String code : codes) {
        String label = "Cm" + code + " cloud photos";
        Hyperlink photo =
            photos.stream()
                .filter(item -> label.equals(item.getAccessibleText()))
                .findFirst()
                .orElseThrow();
        assertFalse(((ImageView) photo.getGraphic()).getImage().isError());
        photo.fire();
        assertEquals("cm_" + code.charAt(0) + ".pdf", opened.get(opened.size() - 1));
      }
      RadioButton selected =
          scene.getRoot().lookupAll(".radio-button").stream()
              .map(RadioButton.class::cast)
              .filter(RadioButton::isSelected)
              .findFirst()
              .orElseThrow();
      assertEquals("7b", selected.getUserData());
      CloudCoverFxTestSupport.click(scene, "Help");
      assertEquals("cm.pdf", opened.get(11));
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
  public void savesAndRestoresEachCm7VariantAndMissingValues() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      MiddleCloudState.cm_code = "";
    });
    for (String code : List.of("7a", "7b", "7c", "0", "")) {
      JDialog dialog = GuiActionRunner.execute(() -> MiddleCloudDialog.open());
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        scene.getRoot().applyCss();
        CloudCoverFxTestSupport.selectRadio(scene, code);
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals(code, MiddleCloudState.cm_code);
        assertEquals(
            code.isEmpty() ? "" : code.substring(0, 1) + " (code)", main.jTextField34.getText());
      });
      JDialog reopened = GuiActionRunner.execute(() -> MiddleCloudDialog.open());
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
      MiddleCloudState.cm_code = "1";
    });
    try {
      JDialog cancelDialog = GuiActionRunner.execute(() -> MiddleCloudDialog.open());
      Scene cancelScene = CloudCoverFxTestSupport.awaitScene(cancelDialog);
      CloudCoverFxTestSupport.onFx(() -> {
        cancelScene.getRoot().applyCss();
        CloudCoverFxTestSupport.selectRadio(cancelScene, "7c");
        CloudCoverFxTestSupport.click(cancelScene, "Cancel");
        return null;
      });
      DialogFixture cancelHigh =
          WindowFinder.findDialog(new DialogTitleMatcher("Clouds high")).using(robot());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("1", MiddleCloudState.cm_code);
        cancelHigh.target().dispose();
      });

      JDialog dialog = GuiActionRunner.execute(() -> MiddleCloudDialog.open());
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        scene.getRoot().applyCss();
        CloudCoverFxTestSupport.selectRadio(scene, "7b");
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      DialogFixture high =
          WindowFinder.findDialog(new DialogTitleMatcher("Clouds high")).using(robot());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("7b", MiddleCloudState.cm_code);
        high.target().dispose();
      });

      JDialog backDialog = GuiActionRunner.execute(() -> MiddleCloudDialog.open());
      Scene backScene = CloudCoverFxTestSupport.awaitScene(backDialog);
      CloudCoverFxTestSupport.onFx(() -> {
        backScene.getRoot().applyCss();
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      FrameFixture low = WindowFinder.findFrame(new FrameTitleMatcher("Clouds low")).using(robot());
      SwingUtilities.invokeAndWait(low.target()::dispose);

      JDialog stopDialog = GuiActionRunner.execute(() -> MiddleCloudDialog.open());
      Scene stopScene = CloudCoverFxTestSupport.awaitScene(stopDialog);
      CloudCoverFxTestSupport.onFx(() -> {
        stopScene.getRoot().applyCss();
        CloudCoverFxTestSupport.selectRadio(stopScene, "3");
        CloudCoverFxTestSupport.click(stopScene, "Stop");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(stopDialog.isDisplayable());
        assertFalse(main.in_next_sequence);
        assertEquals("7b", MiddleCloudState.cm_code);
      });
    } finally {
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    }
  }
}
