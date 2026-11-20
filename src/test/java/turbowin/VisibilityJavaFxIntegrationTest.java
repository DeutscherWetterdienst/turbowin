package turbowin;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class VisibilityJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void allRangesSaveTheCorrectCodeAndRestoreOnReopening() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      VisibilityState.VV = "";
      VisibilityState.VV_code = "";
    });
    List<String> values =
        List.of(
            "< 0.03 nm",
            "0.03 - 0.1 nm",
            "0.1 - 0.3 nm",
            "0.3 - 0.5 nm",
            "0.5 - 1.1 nm",
            "1.1 - 2.2 nm",
            "2.2 - 5.5 nm",
            "5.5 - 11 nm",
            "11 - 27 nm",
            "> 27 nm",
            "not determined");
    List<String> codes = List.of("90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "//");
    for (int i = 0; i < values.size(); i++) {
      String value = values.get(i);
      String code = codes.get(i);
      JDialog dialog = GuiActionRunner.execute(VisibilityDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        scene.getRoot().applyCss();
        option(scene, value).fire();
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals(value, VisibilityState.VV);
        assertEquals(code, VisibilityState.VV_code);
        assertEquals(value, main.jTextField18.getText());
      });
      JDialog reopened = GuiActionRunner.execute(VisibilityDialog::open);
      Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
      CloudCoverFxTestSupport.onFx(() -> {
        assertTrue(option(reopenedScene, value).isSelected());
        CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
    }
  }

  @Test
  public void defaultsToUndeterminedAndUnknownValuesClearOnAcceptance() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      VisibilityState.VV = "";
    });
    JDialog dialog = GuiActionRunner.execute(VisibilityDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      assertTrue(option(scene, "not determined").isSelected());
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      VisibilityState.VV = "unexpected";
    });
    JDialog unknown = GuiActionRunner.execute(VisibilityDialog::open);
    Scene unknownScene = CloudCoverFxTestSupport.awaitScene(unknown);
    CloudCoverFxTestSupport.onFx(() -> {
      assertTrue(
          unknownScene.getRoot().lookupAll(".radio-button").stream()
              .map(RadioButton.class::cast)
              .noneMatch(RadioButton::isSelected));
      CloudCoverFxTestSupport.click(unknownScene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(unknown.isDisplayable());
      assertEquals("", VisibilityState.VV);
      assertEquals("//", VisibilityState.VV_code);
    });
  }

  @Test
  public void helpAndFooterRetainTheirBehavior() throws Exception {
    AtomicReference<JDialog> window = new AtomicReference<>();
    List<String> opened = new ArrayList<>();
    SwingUtilities.invokeAndWait(() ->
        window.set(
            FxSwingDialog.show(
                frame.target(),
                "Visibility test",
                new Dimension(800, 650),
                close ->
                    VisibilityView.create(
                        new VisibilityView.State("", false, true),
                        choice -> {},
                        close,
                        close,
                        close,
                        () -> opened.add("visibility.pdf")))));
    Scene scene = CloudCoverFxTestSupport.awaitScene(window.get());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      assertEquals(11, scene.getRoot().lookupAll(".radio-button").size());
      assertNotNull(
          scene.getRoot().lookupAll(".label").stream()
              .filter(
                  node -> ((javafx.scene.control.Label) node).getText().equals("50 - 200 metres"))
              .findFirst()
              .orElseThrow());
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("Back", "OK", "Cancel", "Help", "Stop"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      assertTrue(((Button) footer.getChildren().get(0)).isDisabled());
      assertTrue(((Button) footer.getChildren().get(4)).isDisabled());
      CloudCoverFxTestSupport.click(scene, "Help");
      assertEquals(List.of("visibility.pdf"), opened);
      return null;
    });
    SwingUtilities.invokeAndWait(window.get()::dispose);
  }

  @Test
  public void sequenceMovesForwardBackwardAndCanBeStopped() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = true;
      VisibilityState.VV = "0.5 - 1.1 nm";
      VisibilityState.VV_code = "94";
    });
    try {
      JDialog cancel = GuiActionRunner.execute(VisibilityDialog::open);
      Scene cancelScene = CloudCoverFxTestSupport.awaitScene(cancel);
      CloudCoverFxTestSupport.onFx(() -> {
        option(cancelScene, "11 - 27 nm").fire();
        CloudCoverFxTestSupport.click(cancelScene, "Cancel");
        return null;
      });
      DialogFixture present =
          WindowFinder.findDialog(new DialogTitleMatcher("Present weather")).using(robot());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("94", VisibilityState.VV_code);
        present.target().dispose();
      });
      JDialog accept = GuiActionRunner.execute(VisibilityDialog::open);
      Scene acceptScene = CloudCoverFxTestSupport.awaitScene(accept);
      CloudCoverFxTestSupport.onFx(() -> {
        option(acceptScene, "11 - 27 nm").fire();
        CloudCoverFxTestSupport.click(acceptScene, "OK");
        return null;
      });
      DialogFixture acceptedPresent =
          WindowFinder.findDialog(new DialogTitleMatcher("Present weather")).using(robot());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("98", VisibilityState.VV_code);
        acceptedPresent.target().dispose();
      });
      JDialog back = GuiActionRunner.execute(VisibilityDialog::open);
      Scene backScene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      DialogFixture waves = WindowFinder.findDialog(new DialogTitleMatcher("Waves")).using(robot());
      CloudCoverFxTestSupport.awaitScene((JDialog) waves.target());
      SwingUtilities.invokeAndWait(waves.target()::dispose);
      JDialog stop = GuiActionRunner.execute(VisibilityDialog::open);
      Scene stopScene = CloudCoverFxTestSupport.awaitScene(stop);
      CloudCoverFxTestSupport.onFx(() -> {
        option(stopScene, "< 0.03 nm").fire();
        CloudCoverFxTestSupport.click(stopScene, "Stop");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(stop.isDisplayable());
        assertFalse(main.in_next_sequence);
        assertEquals("98", VisibilityState.VV_code);
      });
    } finally {
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    }
  }

  private static RadioButton option(Scene scene, String text) {
    return scene.getRoot().lookupAll(".radio-button").stream()
        .map(RadioButton.class::cast)
        .filter(button -> button.getText().equals(text))
        .findFirst()
        .orElseThrow();
  }
}
