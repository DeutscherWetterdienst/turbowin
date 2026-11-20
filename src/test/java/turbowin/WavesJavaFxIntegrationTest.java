package turbowin;

import static org.junit.Assert.*;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
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

public class WavesJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void restoresAllSwellChoicesFromObservationState() throws Exception {
    prepare();
    for (WavesView.Swell choice : WavesView.Swell.values()) {
      SwingUtilities.invokeAndWait(() -> {
        mywaves.swell_1_dir =
            switch (choice) {
              case NONE -> "no swell";
              case CONFUSED -> "confused";
              case ONE, TWO -> "180";
              case UNDETERMINED -> "";
            };
        mywaves.swell_2_dir = choice == WavesView.Swell.TWO ? "270" : "";
      });
      JDialog dialog = GuiActionRunner.execute(WavesDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        assertTrue(option(scene, choice).isSelected());
        CloudCoverFxTestSupport.click(scene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
    }
  }

  @Test
  public void savesWaveCodesAndRestoresNoSwell() throws Exception {
    prepare();
    JDialog dialog = GuiActionRunner.execute(WavesDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      field(scene, "wave_1").setText("8");
      field(scene, "wave_2").setText("2,3");
      select(scene, WavesView.Swell.NONE);
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("Back", "OK", "Cancel", main.offline_mode ? "Help" : "Internet", "Stop"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      assertTrue(((Button) footer.getChildren().get(0)).isDisabled());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("8", mywaves.wind_waves_period);
      assertEquals("2.3", mywaves.wind_waves_height);
      assertEquals("08", mywaves.Pw_code);
      assertEquals("05", mywaves.Hw_code);
      assertEquals("no swell", mywaves.swell_1_dir);
      assertEquals("00", mywaves.Dw1_code);
      assertEquals("00", mywaves.Dw2_code);
    });
    JDialog reopened = GuiActionRunner.execute(WavesDialog::open);
    Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("8", field(reopenedScene, "wave_1").getText());
      assertEquals("2.3", field(reopenedScene, "wave_2").getText());
      assertTrue(option(reopenedScene, WavesView.Swell.NONE).isSelected());
      CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
  }

  @Test
  public void confusedWaveValuesAreAcceptedAndEncoded() throws Exception {
    prepare();
    JDialog dialog = GuiActionRunner.execute(WavesDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "wave_1").setText("Confused");
      field(scene, "wave_2").setText("confused");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("confused", mywaves.wind_waves_period);
      assertEquals("99", mywaves.Pw_code);
      assertEquals("//", mywaves.Hw_code);
    });
  }

  @Test
  public void invalidWaveValuesKeepTheDialogOpenForCorrection() throws Exception {
    prepare();
    JDialog dialog = GuiActionRunner.execute(WavesDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "wave_1").setText("0");
      field(scene, "wave_2").setText("3");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "wave_2").setText("2");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("04", mywaves.Hw_code);
    });
  }

  @Test
  public void eachSwellChoiceOpensItsFormAndCanReturnToWaves() throws Exception {
    prepare();
    SwingUtilities.invokeAndWait(() -> main.in_next_sequence = true);
    try {
      for (var choice :
          List.of(WavesView.Swell.CONFUSED, WavesView.Swell.ONE, WavesView.Swell.TWO)) {
        JDialog dialog = GuiActionRunner.execute(WavesDialog::open);
        Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
        CloudCoverFxTestSupport.onFx(() -> {
          select(scene, choice);
          field(scene, "wave_1").setText("5");
          field(scene, "wave_2").setText("2");
          CloudCoverFxTestSupport.click(scene, "OK");
          return null;
        });
        String title =
            switch (choice) {
              case CONFUSED -> "Confused swell or indeterminable direction";
              case ONE -> "One swell discernable";
              case TWO -> "Two swells discernable";
              default -> throw new AssertionError();
            };
        FrameFixture swell = WindowFinder.findFrame(new FrameTitleMatcher(title)).using(robot());
        swell.button(JButtonMatcher.withText("Back")).click();
        DialogFixture waves =
            WindowFinder.findDialog(new DialogTitleMatcher("Waves")).using(robot());
        CloudCoverFxTestSupport.awaitScene((JDialog) waves.target());
        SwingUtilities.invokeAndWait(waves.target()::dispose);
      }
    } finally {
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    }
  }

  @Test
  public void sequenceCanGoBackCancelForwardAndStop() throws Exception {
    prepare();
    SwingUtilities.invokeAndWait(() -> main.in_next_sequence = true);
    try {
      JDialog back = GuiActionRunner.execute(WavesDialog::open);
      Scene backScene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      DialogFixture wind = WindowFinder.findDialog(new DialogTitleMatcher("Wind")).using(robot());
      CloudCoverFxTestSupport.awaitScene((JDialog) wind.target());
      SwingUtilities.invokeAndWait(wind.target()::dispose);
      JDialog cancel = GuiActionRunner.execute(WavesDialog::open);
      Scene cancelScene = CloudCoverFxTestSupport.awaitScene(cancel);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(cancelScene, "Cancel");
        return null;
      });
      DialogFixture visibility =
          WindowFinder.findDialog(new DialogTitleMatcher("Visibility")).using(robot());
      SwingUtilities.invokeAndWait(visibility.target()::dispose);
      JDialog accept = GuiActionRunner.execute(WavesDialog::open);
      Scene acceptScene = CloudCoverFxTestSupport.awaitScene(accept);
      CloudCoverFxTestSupport.onFx(() -> {
        select(acceptScene, WavesView.Swell.NONE);
        CloudCoverFxTestSupport.click(acceptScene, "OK");
        return null;
      });
      DialogFixture acceptedVisibility =
          WindowFinder.findDialog(new DialogTitleMatcher("Visibility")).using(robot());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("no swell", mywaves.swell_1_dir);
        acceptedVisibility.target().dispose();
      });
      JDialog stop = GuiActionRunner.execute(WavesDialog::open);
      Scene stopScene = CloudCoverFxTestSupport.awaitScene(stop);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(stopScene, "Stop");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(stop.isDisplayable());
        assertFalse(main.in_next_sequence);
      });
    } finally {
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    }
  }

  private static void prepare() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      mywaves.wind_waves_period = "";
      mywaves.wind_waves_height = "";
      mywaves.swell_1_dir = "";
      mywaves.swell_1_period = "";
      mywaves.swell_1_height = "";
      mywaves.swell_2_dir = "";
      mywaves.swell_2_period = "";
      mywaves.swell_2_height = "";
    });
  }

  private static TextField field(Scene scene, String id) {
    return (TextField) scene.lookup("#" + id);
  }

  private static RadioButton option(Scene scene, WavesView.Swell choice) {
    return scene.getRoot().lookupAll(".radio-button").stream()
        .map(RadioButton.class::cast)
        .filter(item -> item.getUserData() == choice)
        .findFirst()
        .orElseThrow();
  }

  private static void select(Scene scene, WavesView.Swell choice) {
    option(scene, choice).fire();
  }
}
