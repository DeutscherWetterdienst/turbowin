package turbowin;

import static org.junit.Assert.*;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class PresentWeatherJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void specificConditionsFitInTheWindowAndRemainScrollable() throws Exception {
    reset();
    JDialog dialog = GuiActionRunner.execute(PresentWeatherDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      list(scene, 1).getSelectionModel().select(2);
      scene.getRoot().applyCss();
      scene.getRoot().layout();
      ScrollPane outer = (ScrollPane) scene.lookup(".cloud-cover-scroll");
      assertTrue(
          "Present weather content should fit in the window",
          outer.getContent().getLayoutBounds().getHeight()
              <= outer.getViewportBounds().getHeight() + 1);
      ListView<?> specific = list(scene, 3);
      assertEquals(10, specific.getItems().size());
      specific.scrollTo(9);
      specific.getSelectionModel().select(9);
      assertEquals(9, specific.getSelectionModel().getSelectedIndex());
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
  }

  @Test
  public void categoryChangesClearOtherSelectionAndRestoreSpecificWeather() throws Exception {
    reset();
    JDialog dialog = GuiActionRunner.execute(PresentWeatherDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      ListView<?> dry = list(scene, 1), wet = list(scene, 2), specific = list(scene, 3);
      dry.getSelectionModel().select(2);
      assertEquals(10, specific.getItems().size());
      specific.getSelectionModel().select(0);
      wet.getSelectionModel().select(5);
      assertEquals(-1, dry.getSelectionModel().getSelectedIndex());
      assertEquals(-1, specific.getSelectionModel().getSelectedIndex());
      specific.getSelectionModel().select(8);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("61", PresentWeatherState.ww_code);
      assertEquals(PresentWeatherCatalog.ww_61, PresentWeatherState.present_weather);
      assertEquals(PresentWeatherCatalog.ww_61, main.jTextField13.getText());
    });
    JDialog reopened = GuiActionRunner.execute(PresentWeatherDialog::open);
    Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals(5, list(reopenedScene, 2).getSelectionModel().getSelectedIndex());
      PresentWeatherCatalog.Weather selected =
          (PresentWeatherCatalog.Weather)
              list(reopenedScene, 3).getSelectionModel().getSelectedItem();
      assertEquals("61", selected.code());
      CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
  }

  @Test
  public void selectingGeneralWithoutSpecificShowsWarningAndKeepsDialogOpen() throws Exception {
    reset();
    SwingUtilities.invokeAndWait(() -> {
      PresentWeatherState.ww_code = "61";
      PresentWeatherState.present_weather = PresentWeatherCatalog.ww_61;
    });
    JDialog dialog = GuiActionRunner.execute(PresentWeatherDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      list(scene, 1).getSelectionModel().select(2);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " warning"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> {
      assertTrue(dialog.isDisplayable());
      assertEquals("", PresentWeatherState.ww_code);
    });
    CloudCoverFxTestSupport.onFx(() -> {
      list(scene, 3).getSelectionModel().select(9);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("40", PresentWeatherState.ww_code);
    });
  }

  @Test
  public void undeterminedAndLegacyUnpaddedCodesRestore() throws Exception {
    reset();
    SwingUtilities.invokeAndWait(() -> {
      PresentWeatherState.ww_code = "//";
      PresentWeatherState.present_weather = "not determined";
    });
    JDialog unknown = GuiActionRunner.execute(PresentWeatherDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(unknown);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals(-1, list(scene, 1).getSelectionModel().getSelectedIndex());
      assertEquals(
          "//",
          ((PresentWeatherCatalog.Weather) list(scene, 3).getSelectionModel().getSelectedItem())
              .code());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(unknown.isDisplayable());
      assertEquals("//", PresentWeatherState.ww_code);
      PresentWeatherState.ww_code = "5";
      PresentWeatherState.present_weather = PresentWeatherCatalog.ww_5;
    });
    JDialog legacy = GuiActionRunner.execute(PresentWeatherDialog::open);
    Scene legacyScene = CloudCoverFxTestSupport.awaitScene(legacy);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals(8, list(legacyScene, 1).getSelectionModel().getSelectedIndex());
      assertEquals(
          "05",
          ((PresentWeatherCatalog.Weather)
                  list(legacyScene, 3).getSelectionModel().getSelectedItem())
              .code());
      CloudCoverFxTestSupport.click(legacyScene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(legacy.isDisplayable()));
  }

  @Test
  public void sequenceNavigatesBackForwardAndStops() throws Exception {
    reset();
    SwingUtilities.invokeAndWait(() -> main.in_next_sequence = true);
    try {
      JDialog back = GuiActionRunner.execute(PresentWeatherDialog::open);
      Scene backScene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      DialogFixture visibility =
          WindowFinder.findDialog(new DialogTitleMatcher("Visibility")).using(robot());
      CloudCoverFxTestSupport.awaitScene((JDialog) visibility.target());
      SwingUtilities.invokeAndWait(visibility.target()::dispose);
      JDialog forward = GuiActionRunner.execute(PresentWeatherDialog::open);
      Scene forwardScene = CloudCoverFxTestSupport.awaitScene(forward);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(forwardScene, "Cancel");
        return null;
      });
      DialogFixture past =
          WindowFinder.findDialog(new DialogTitleMatcher("Past weather")).using(robot());
      SwingUtilities.invokeAndWait(past.target()::dispose);
      JDialog stop = GuiActionRunner.execute(PresentWeatherDialog::open);
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

  private static void reset() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      PresentWeatherState.ww_code = "";
      PresentWeatherState.present_weather = "";
    });
  }

  private static ListView<?> list(Scene scene, int number) {
    return (ListView<?>) scene.lookup("#present_weather_list_" + number);
  }
}
