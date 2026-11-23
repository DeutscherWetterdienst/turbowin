package turbowin;

import static org.junit.Assert.*;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class BarometerJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void savesCorrectedSeaLevelReadingAndRestoresFields() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_YES;
      main.barometer_instrument_correction = "0.5";
      mybarometer.pressure_reading = "";
      mybarometer.deepest_draft = "";
    });
    JDialog dialog = GuiActionRunner.execute(BarometerDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      assertTrue(field(scene, "deepest_draft").isDisabled());
      assertEquals("0.5", field(scene, "instrument_correction").getText());
      assertTrue(((Label) scene.lookup("#msl_metadata")).getText().endsWith("Yes"));
      field(scene, "barometer_reading").setText("1013,2");
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("Back", "OK", "Cancel", main.offline_mode ? "Help" : "Internet", "Stop"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("1013.2", mybarometer.pressure_reading);
      assertEquals("1013.7", mybarometer.pressure_msl_corrected);
      assertEquals("0137", mybarometer.PPPP_code);
    });
    JDialog reopened = GuiActionRunner.execute(BarometerDialog::open);
    Scene restored = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("1013.2", field(restored, "barometer_reading").getText());
      CloudCoverFxTestSupport.click(restored, "Cancel");
      return null;
    });
  }

  @Test
  public void invalidReadingCanBeCorrected() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_YES;
      main.barometer_instrument_correction = "0.0";
      mybarometer.pressure_reading = "";
    });
    JDialog dialog = GuiActionRunner.execute(BarometerDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "barometer_reading").setText("800");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "barometer_reading").setText("1000");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("1000.0", mybarometer.pressure_msl_corrected);
    });
  }

  @Test
  public void sensorPressureUpdatesWithoutOverwritingDraft() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.RS232_connection_mode = 3;
      main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_NO;
      main.barometer_instrument_correction = "0.0";
      mybarometer.pressure_reading = "1000.0";
      mybarometer.deepest_draft = "10";
      mybarometer.PPPP_code = "0010";
    });
    JDialog dialog = GuiActionRunner.execute(BarometerDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      assertFalse(field(scene, "barometer_reading").isEditable());
      assertFalse(field(scene, "deepest_draft").isEditable());
      assertFalse(field(scene, "deepest_draft").isDisabled());
      assertTrue(((Label) scene.lookup("#msl_metadata")).getText().endsWith("No"));
      return null;
    });
    SwingUtilities.invokeAndWait(() -> mybarometer.jTextField1.setText("1001.5"));
    SwingUtilities.invokeAndWait(() -> {});
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("1001.5", field(scene, "barometer_reading").getText());
      assertEquals("10", field(scene, "deepest_draft").getText());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("1000.0", mybarometer.pressure_reading);
      assertEquals("0010", mybarometer.PPPP_code);
    });
  }

  @Test
  public void sequenceNavigatesBackForwardAndStops() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = true;
      main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_YES;
    });
    try {
      JDialog back = GuiActionRunner.execute(BarometerDialog::open);
      Scene backScene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      FrameFixture position =
          WindowFinder.findFrame(new FrameTitleMatcher("Position, course and speed"))
              .using(robot());
      SwingUtilities.invokeAndWait(position.target()::dispose);
      JDialog forward = GuiActionRunner.execute(BarometerDialog::open);
      Scene forwardScene = CloudCoverFxTestSupport.awaitScene(forward);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(forwardScene, "Cancel");
        return null;
      });
      DialogFixture barograph =
          WindowFinder.findDialog(new DialogTitleMatcher("Barograph reading")).using(robot());
      SwingUtilities.invokeAndWait(barograph.target()::dispose);
      JDialog stop = GuiActionRunner.execute(BarometerDialog::open);
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

  private static TextField field(Scene scene, String id) {
    return (TextField) scene.lookup("#" + id);
  }
}
