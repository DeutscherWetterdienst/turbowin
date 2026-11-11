package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;
import turbowin.matchers.FrameTitleMatcher;

/** Exercises the embedded JavaFX dialog against the real Swing observation state. */
public class CloudCoverJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void acceptsObservationAndReopensAfterAbout() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      AboutDialog.show(frame.target());
      mycloudcover.N = mycloudcover.N_4_8;
      mycloudcover.Nh = mycloudcover.NH_3_8;
      mycloudcover.h = mycloudcover.H_300_600;
      main.in_next_sequence = false;
    });
    JDialog dialog = GuiActionRunner.execute(() -> CloudCoverJavaFxWindow.open());
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      VBox total = (VBox) scene.getRoot().lookup("#total");
      assertTrue(
          total.getChildren().stream()
              .filter(RadioButton.class::isInstance)
              .map(RadioButton.class::cast)
              .anyMatch(button ->
                  mycloudcover.N_4_8.equals(button.getUserData()) && button.isSelected()));
      Button back =
          scene.getRoot().lookupAll(".button").stream()
              .filter(Button.class::isInstance)
              .map(Button.class::cast)
              .filter(button -> "Back".equals(button.getText()))
              .findFirst()
              .orElseThrow();
      assertTrue(back.isDisabled());
      CloudCoverFxTestSupport.choose(scene, "total", mycloudcover.N_4_8);
      CloudCoverFxTestSupport.choose(scene, "cl", mycloudcover.NH_3_8);
      CloudCoverFxTestSupport.choose(scene, "low", mycloudcover.H_300_600);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("4", mycloudcover.N_code);
      assertEquals("3", mycloudcover.Nh_code);
      assertEquals("4", mycloudcover.h_code);
      assertEquals(mycloudcover.H_300_600, main.jTextField32.getText());
    });
    JDialog reopened = GuiActionRunner.execute(() -> CloudCoverJavaFxWindow.open());
    Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
    String expectedAdvice =
        GuiActionRunner.execute(
            () -> CloudCoverJavaFxWindow.advisedHeight(mycloudcover.computeHeightAdvice()));
    CloudCoverFxTestSupport.onFx(() -> {
      reopenedScene.getRoot().applyCss();
      VBox heights = (VBox) reopenedScene.getRoot().lookup("#low");
      assertTrue(
          heights.getChildren().stream()
              .filter(RadioButton.class::isInstance)
              .map(RadioButton.class::cast)
              .anyMatch(
                  button -> expectedAdvice.equals(button.getUserData()) && button.isSelected()));
      CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
  }

  @Test
  public void stopLeavesSequenceModeAndDoesNotOverwriteObservation() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = true;
      mycloudcover.N = mycloudcover.N_2_8;
    });
    JDialog dialog = GuiActionRunner.execute(() -> CloudCoverJavaFxWindow.open());
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      CloudCoverFxTestSupport.click(scene, "Stop");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertFalse(main.in_next_sequence);
      assertEquals(mycloudcover.N_2_8, mycloudcover.N);
    });
  }

  @Test
  public void invalidSelectionClearsObservationAndKeepsDialogOpen() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      mycloudcover.N = mycloudcover.N_4_8;
      mycloudcover.Nh = mycloudcover.NH_3_8;
      mycloudcover.h = mycloudcover.H_300_600;
      mycloudcover.N_code = "4";
      mycloudcover.Nh_code = "3";
      mycloudcover.h_code = "4";
    });
    JDialog dialog = GuiActionRunner.execute(() -> CloudCoverJavaFxWindow.open());
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      CloudCoverFxTestSupport.choose(scene, "total", mycloudcover.N_CLOUDLESS);
      CloudCoverFxTestSupport.choose(scene, "cl", mycloudcover.NH_1_8);
      CloudCoverFxTestSupport.choose(scene, "low", mycloudcover.H_CLOUDLESS);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> {
      assertTrue(dialog.isDisplayable());
      assertEquals("", mycloudcover.N);
      assertEquals("", mycloudcover.Nh_code);
      assertEquals("", main.jTextField32.getText());
    });
    CloudCoverFxTestSupport.onFx(() -> {
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
  }

  @Test
  public void sequenceCancelMovesForwardAndBackReturnsToHighClouds() throws Exception {
    SwingUtilities.invokeAndWait(() -> main.in_next_sequence = true);
    try {
      JDialog dialog = GuiActionRunner.execute(() -> CloudCoverJavaFxWindow.open());
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        scene.getRoot().applyCss();
        CloudCoverFxTestSupport.click(scene, "Cancel");
        return null;
      });
      FrameFixture observer =
          WindowFinder.findFrame(new FrameTitleMatcher("Observer")).using(robot());
      SwingUtilities.invokeAndWait(observer.target()::dispose);

      JDialog backDialog = GuiActionRunner.execute(() -> CloudCoverJavaFxWindow.open());
      Scene backScene = CloudCoverFxTestSupport.awaitScene(backDialog);
      CloudCoverFxTestSupport.onFx(() -> {
        backScene.getRoot().applyCss();
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      FrameFixture highClouds =
          WindowFinder.findFrame(new FrameTitleMatcher("Clouds high")).using(robot());
      SwingUtilities.invokeAndWait(highClouds.target()::dispose);
    } finally {
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    }
  }
}
