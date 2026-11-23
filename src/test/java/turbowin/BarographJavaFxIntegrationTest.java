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

public class BarographJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void savesAndRestoresDistinctSamePressureGraph() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      mybarograph.a_code = "";
      mybarograph.pressure_amount_tendency = "";
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
      assertEquals("0.0", mybarograph.pressure_amount_tendency);
      assertEquals("0", mybarograph.a_code);
      assertEquals("000", mybarograph.ppp_code);
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
      mybarograph.a_code = "";
      mybarograph.pressure_amount_tendency = "";
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
      assertEquals("4", mybarograph.a_code);
      assertEquals("000", mybarograph.ppp_code);
    });
  }

  @Test
  public void sensorValuesAreReadOnlyAndAreNotReplacedOnAcceptance() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.RS232_connection_mode = 3;
      mybarograph.pressure_amount_tendency = "1.2";
      mybarograph.a_code = "2";
      mybarograph.ppp_code = "012";
    });
    JDialog dialog = GuiActionRunner.execute(BarographDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      assertFalse(field(scene).isEditable());
      assertTrue(option(scene, 2).isDisabled());
      assertTrue(option(scene, 2).isSelected());
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      mybarograph.jTextField1.setText("2.3");
      mybarograph.jRadioButton6.setSelected(true);
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
      assertEquals("1.2", mybarograph.pressure_amount_tendency);
      assertEquals("2", mybarograph.a_code);
      assertEquals("012", mybarograph.ppp_code);
    });
  }

  @Test
  public void sequenceNavigatesBackForwardAndStops() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = true;
      mybarograph.a_code = "";
      mybarograph.pressure_amount_tendency = "";
    });
    try {
      JDialog back = GuiActionRunner.execute(BarographDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(scene, "Back");
        return null;
      });
      FrameFixture barometer =
          WindowFinder.findFrame(new FrameTitleMatcher("barometer reading")).using(robot());
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
