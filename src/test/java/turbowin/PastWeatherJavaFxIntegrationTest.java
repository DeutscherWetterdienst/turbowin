package turbowin;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class PastWeatherJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void everySingleConditionIsRecordedTwiceAndRestored() throws Exception {
    reset();
    List<String> codes = List.of("9", "8", "7", "6", "5", "4", "3", "2", "1", "0");
    List<String> values =
        List.of(
            "thunderstorm(s), with or without precipitation",
            "shower(s)",
            "snow, or rain and snow mixed",
            "rain",
            "drizzle",
            "fog or ice fog of thick haze (visibility < 0.5 nm)",
            "sandstorm, duststorm or blowing snow",
            "sky covering > 0.5 throughout period",
            "sky covering > 0.5 and <= 0.5 during parts period",
            "sky covering <= 0.5 throughout period");
    for (int i = 0; i < codes.size(); i++) {
      String code = codes.get(i), value = values.get(i);
      SwingUtilities.invokeAndWait(() -> {
        PastWeatherState.past_weather_1 = "";
        PastWeatherState.past_weather_2 = "";
      });
      JDialog dialog = GuiActionRunner.execute(PastWeatherDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        option(scene, code).fire();
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals(code, PastWeatherState.W1_code);
        assertEquals(code, PastWeatherState.W2_code);
        assertEquals(value, PastWeatherState.past_weather_1);
        assertEquals(value, PastWeatherState.past_weather_2);
        assertEquals(value, main.jTextField14.getText());
        assertEquals(value, main.jTextField15.getText());
      });
      JDialog reopened = GuiActionRunner.execute(PastWeatherDialog::open);
      Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
      CloudCoverFxTestSupport.onFx(() -> {
        assertTrue(option(reopenedScene, code).isSelected());
        assertEquals(
            1,
            reopenedScene.getRoot().lookupAll(".check-box").stream()
                .map(CheckBox.class::cast)
                .filter(CheckBox::isSelected)
                .count());
        CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
    }
  }

  @Test
  public void multipleChoicesUseHighestPriorityAndEmptySelectionClearsValues() throws Exception {
    reset();
    JDialog dialog = GuiActionRunner.execute(PastWeatherDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      option(scene, "3").fire();
      option(scene, "6").fire();
      option(scene, "9").fire();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("9", PastWeatherState.W1_code);
      assertEquals("6", PastWeatherState.W2_code);
    });
    JDialog reopened = GuiActionRunner.execute(PastWeatherDialog::open);
    Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertTrue(option(reopenedScene, "9").isSelected());
      assertTrue(option(reopenedScene, "6").isSelected());
      assertFalse(option(reopenedScene, "3").isSelected());
      option(reopenedScene, "9").fire();
      option(reopenedScene, "6").fire();
      CloudCoverFxTestSupport.click(reopenedScene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(reopened.isDisplayable());
      assertEquals("", PastWeatherState.W1_code);
      assertEquals("", PastWeatherState.W2_code);
      assertEquals("", main.jTextField14.getText());
      assertEquals("", main.jTextField15.getText());
    });
  }

  @Test
  public void footerAndHelpPreserveExistingBehavior() throws Exception {
    AtomicReference<JDialog> window = new AtomicReference<>();
    List<String> opened = new ArrayList<>();
    SwingUtilities.invokeAndWait(() ->
        window.set(
            FxSwingDialog.show(
                frame.target(),
                "Past weather test",
                new Dimension(800, 700),
                close ->
                    PastWeatherView.create(
                        new PastWeatherView.State("", "", false, true),
                        choices -> {},
                        close,
                        close,
                        close,
                        () -> opened.add("weather.pdf")))));
    Scene scene = CloudCoverFxTestSupport.awaitScene(window.get());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      assertEquals(10, scene.getRoot().lookupAll(".check-box").size());
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("Back", "OK", "Cancel", "Help", "Stop"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      assertTrue(((Button) footer.getChildren().get(0)).isDisabled());
      assertTrue(((Button) footer.getChildren().get(4)).isDisabled());
      CloudCoverFxTestSupport.click(scene, "Help");
      assertEquals(List.of("weather.pdf"), opened);
      return null;
    });
    SwingUtilities.invokeAndWait(window.get()::dispose);
  }

  @Test
  public void sequenceMovesForwardBackwardAndCanBeStopped() throws Exception {
    reset();
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = true;
      PastWeatherState.W1_code = "5";
      PastWeatherState.W2_code = "5";
      PastWeatherState.past_weather_1 = "drizzle";
      PastWeatherState.past_weather_2 = "drizzle";
    });
    try {
      JDialog cancel = GuiActionRunner.execute(PastWeatherDialog::open);
      Scene cancelScene = CloudCoverFxTestSupport.awaitScene(cancel);
      CloudCoverFxTestSupport.onFx(() -> {
        option(cancelScene, "9").fire();
        CloudCoverFxTestSupport.click(cancelScene, "Cancel");
        return null;
      });
      DialogFixture low =
          WindowFinder.findDialog(new DialogTitleMatcher("Clouds low")).using(robot());
      CloudCoverFxTestSupport.awaitScene((JDialog) low.target());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("5", PastWeatherState.W1_code);
        low.target().dispose();
      });
      JDialog accept = GuiActionRunner.execute(PastWeatherDialog::open);
      Scene acceptScene = CloudCoverFxTestSupport.awaitScene(accept);
      CloudCoverFxTestSupport.onFx(() -> {
        option(acceptScene, "9").fire();
        CloudCoverFxTestSupport.click(acceptScene, "OK");
        return null;
      });
      DialogFixture acceptedLow =
          WindowFinder.findDialog(new DialogTitleMatcher("Clouds low")).using(robot());
      CloudCoverFxTestSupport.awaitScene((JDialog) acceptedLow.target());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("9", PastWeatherState.W1_code);
        assertEquals("5", PastWeatherState.W2_code);
        acceptedLow.target().dispose();
      });
      JDialog back = GuiActionRunner.execute(PastWeatherDialog::open);
      Scene backScene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      DialogFixture present =
          WindowFinder.findDialog(new DialogTitleMatcher("Present weather")).using(robot());
      CloudCoverFxTestSupport.awaitScene((JDialog) present.target());
      SwingUtilities.invokeAndWait(present.target()::dispose);
      JDialog stop = GuiActionRunner.execute(PastWeatherDialog::open);
      Scene stopScene = CloudCoverFxTestSupport.awaitScene(stop);
      CloudCoverFxTestSupport.onFx(() -> {
        option(stopScene, "0").fire();
        CloudCoverFxTestSupport.click(stopScene, "Stop");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(stop.isDisplayable());
        assertFalse(main.in_next_sequence);
        assertEquals("9", PastWeatherState.W1_code);
      });
    } finally {
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    }
  }

  private static void reset() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      PastWeatherState.W1_code = "";
      PastWeatherState.W2_code = "";
      PastWeatherState.past_weather_1 = "";
      PastWeatherState.past_weather_2 = "";
    });
  }

  private static CheckBox option(Scene scene, String code) {
    return scene.getRoot().lookupAll(".check-box").stream()
        .map(CheckBox.class::cast)
        .filter(box -> ((PastWeatherView.Choice) box.getUserData()).code().equals(code))
        .findFirst()
        .orElseThrow();
  }
}
