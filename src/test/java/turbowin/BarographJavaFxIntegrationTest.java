package turbowin;

import static org.junit.Assert.*;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class BarographJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void graphChoicesFitInAn800By600Window() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.RS232_connection_mode = 0;
    });
    JDialog dialog = GuiActionRunner.execute(BarographDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    SwingUtilities.invokeAndWait(() -> dialog.setSize(800, 600));
    CloudCoverFxTestSupport.onFx(() -> {
      // Include room for the native window decorations in the 600-pixel window height.
      scene.getRoot().resize(800, 560);
      scene.getRoot().applyCss();
      scene.getRoot().layout();
      ScrollPane scroll = (ScrollPane) ((VBox) scene.getRoot()).getChildren().get(0);
      assertTrue(
          "Content height "
              + scroll.getContent().getBoundsInLocal().getHeight()
              + ", viewport height "
              + scroll.getViewportBounds().getHeight(),
          scroll.getContent().getBoundsInLocal().getHeight()
              <= scroll.getViewportBounds().getHeight());
      assertTrue(
          scroll.getContent().getBoundsInLocal().getWidth()
              <= scroll.getViewportBounds().getWidth());
      assertTrue(
          option(scene, 0).localToScene(option(scene, 0).getBoundsInLocal()).getMinX()
              < option(scene, 4).localToScene(option(scene, 4).getBoundsInLocal()).getMinX());
      assertTrue(
          option(scene, 4).localToScene(option(scene, 4).getBoundsInLocal()).getMinX()
              < option(scene, 8).localToScene(option(scene, 8).getBoundsInLocal()).getMinX());
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
  }

  @Test
  public void savesAndRestoresDistinctSamePressureGraph() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      BarographModel.a_code = "";
      BarographModel.pressure_amount_tendency = "";
    });
    JDialog dialog = GuiActionRunner.execute(BarographDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene).setText("0,0");
      option(scene, 8).fire();
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
      assertEquals("0.0", BarographModel.pressure_amount_tendency);
      assertEquals("0", BarographModel.a_code);
      assertEquals("000", BarographModel.ppp_code);
    });
    JDialog reopened = GuiActionRunner.execute(BarographDialog::open);
    Scene restored = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("0.0", field(restored).getText());
      assertTrue(option(restored, 8).isSelected());
      CloudCoverFxTestSupport.click(restored, "Cancel");
      return null;
    });
  }

  @Test
  public void invalidSteadyAmountKeepsFormOpenForCorrection() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      BarographModel.a_code = "";
      BarographModel.pressure_amount_tendency = "";
    });
    JDialog dialog = GuiActionRunner.execute(BarographDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene).setText("1.2");
      option(scene, 9).fire();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene).setText("0");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("4", BarographModel.a_code);
      assertEquals("000", BarographModel.ppp_code);
    });
  }

  @Test
  public void validatesRangeAndRoundsPressureCode() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      BarographModel.a_code = "";
      BarographModel.pressure_amount_tendency = "";
    });
    JDialog dialog = GuiActionRunner.execute(BarographDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene).setText("50.1");
      option(scene, 1).fire();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene).setText("1.25");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("1", BarographModel.a_code);
      assertEquals("013", BarographModel.ppp_code);
    });
  }

  @Test
  public void sensorValuesAreReadOnlyAndAreNotReplacedOnAcceptance() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.RS232_connection_mode = 3;
      BarographModel.pressure_amount_tendency = "1.2";
      BarographModel.a_code = "2";
      BarographModel.ppp_code = "012";
    });
    JDialog dialog = GuiActionRunner.execute(BarographDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      assertFalse(field(scene).isEditable());
      assertTrue(option(scene, 2).isDisabled());
      assertTrue(option(scene, 2).isSelected());
      field(scene).setText("8.8");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      BarographModel.tendency.set(BarographModel.Tendency.FALLING_SLOWER);
    });
    SwingUtilities.invokeAndWait(() -> {});
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("8.8", field(scene).getText());
      assertTrue(option(scene, 5).isSelected());
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      BarographModel.amountInput.set("2,3");
      BarographModel.amountInput.set("invalid");
    });
    SwingUtilities.invokeAndWait(() -> {});
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("2.3", field(scene).getText());
      assertTrue(option(scene, 5).isSelected());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("1.2", BarographModel.pressure_amount_tendency);
      assertEquals("2", BarographModel.a_code);
      assertEquals("012", BarographModel.ppp_code);
    });
  }

  @Test
  public void sequenceNavigatesBackForwardAndStops() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = true;
      BarographModel.a_code = "";
      BarographModel.pressure_amount_tendency = "";
    });
    try {
      JDialog back = GuiActionRunner.execute(BarographDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(scene, "Back");
        return null;
      });
      DialogFixture barometer =
          WindowFinder.findDialog(new DialogTitleMatcher("Barometer reading")).using(robot());
      SwingUtilities.invokeAndWait(barometer.target()::dispose);
      JDialog forward = GuiActionRunner.execute(BarographDialog::open);
      Scene forwardScene = CloudCoverFxTestSupport.awaitScene(forward);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(forwardScene, "Cancel");
        return null;
      });
      DialogFixture temperatures =
          WindowFinder.findDialog(new DialogTitleMatcher("Temperatures")).using(robot());
      SwingUtilities.invokeAndWait(temperatures.target()::dispose);
      JDialog stop = GuiActionRunner.execute(BarographDialog::open);
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

  private static TextField field(Scene scene) {
    return (TextField) scene.lookup("#pressure_tendency");
  }

  private static RadioButton option(Scene scene, int index) {
    return (RadioButton) scene.lookup("#barograph_option_" + index);
  }
}
