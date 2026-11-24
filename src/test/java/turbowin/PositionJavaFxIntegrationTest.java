package turbowin;

import static org.junit.Assert.*;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class PositionJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void modelPreservesObservationPeriodsAndAutomaticModeRestrictions() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.RS232_GPS_connection_mode = 0;
      main.RS232_connection_mode = 0;
      main.APR = false;
      main.obs_format = main.FORMAT_FM13;
      PositionModel manual = new PositionModel();
      assertTrue(PositionModel.jTextField1.isEditable());
      assertTrue(manual.plotPositionAvailable());
      assertTrue(manual.courseLabel().contains("3 hrs"));
      main.obs_format = main.FORMAT_101;
      assertTrue(new PositionModel().speedLabel().contains("10 min"));
      main.APR = true;
      PositionModel automatic = new PositionModel();
      assertFalse(PositionModel.jTextField1.isEditable());
      assertFalse(automatic.plotPositionAvailable());
      assertTrue(automatic.courseLabel().contains("10 minutes"));
      main.obs_format = main.FORMAT_FM13;
      assertTrue(new PositionModel().speedLabel().contains("3 hours"));
    });
  }

  @Test
  public void savesCodesAndRestoresHighSpeedSelection() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      PositionModel.latitude_degrees = "";
      PositionModel.longitude_degrees = "";
      PositionModel.course = "";
      PositionModel.speed = "";
    });
    JDialog dialog = GuiActionRunner.execute(PositionDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      enterPosition(scene);
      option(scene, 2, 1).fire();
      option(scene, 3, 7).fire();
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("Back", "OK", "Cancel", main.offline_mode ? "Help" : "Internet", "Stop"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("101", PositionModel.lalala_code);
      assertEquals("0122", PositionModel.lolololo_code);
      assertEquals("1", PositionModel.Qc_code);
      assertEquals("1", PositionModel.Ds_code);
      assertEquals("7", PositionModel.vs_code);
    });
    JDialog reopened = GuiActionRunner.execute(PositionDialog::open);
    Scene restored = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("10", field(restored, "lat_degree").getText());
      assertTrue(option(restored, 3, 7).isSelected());
      CloudCoverFxTestSupport.click(restored, "Cancel");
      return null;
    });
  }

  @Test
  public void inconsistentCourseAndSpeedCanBeCorrected() throws Exception {
    SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    JDialog dialog = GuiActionRunner.execute(PositionDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      enterPosition(scene);
      option(scene, 2, 0).fire();
      option(scene, 3, 1).fire();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      option(scene, 3, 0).fire();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("0", PositionModel.Ds_code);
      assertEquals("0", PositionModel.vs_code);
    });
  }

  @Test
  public void automatedModeIsReadOnlyAndUpdatesStayIndependent() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.APR = true;
      main.obs_format = main.FORMAT_101;
    });
    JDialog dialog = GuiActionRunner.execute(PositionDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      assertFalse(field(scene, "lat_degree").isEditable());
      assertTrue(option(scene, 2, 1).isDisabled());
      assertTrue(((CheckBox) scene.lookup("#plot_position")).isDisabled());
      PositionView.updateField(scene, 0, "42");
      PositionView.updateField(scene, 3, "15");
      PositionView.updateSelection(scene, 2, 4);
      assertEquals("42", field(scene, "lat_degree").getText());
      assertEquals("15", field(scene, "lon_minute").getText());
      assertTrue(option(scene, 2, 4).isSelected());
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
  }

  @Test
  public void lateGpsUpdatesDoNotOverwriteOtherEdits() throws Exception {
    String originalLogs = main.logs_dir;
    try {
      SwingUtilities.invokeAndWait(() -> {
        main.in_next_sequence = false;
        main.RS232_GPS_connection_mode = 3;
        main.obs_format = main.FORMAT_101;
        main.logs_dir = startupDirectory.getRoot().getAbsolutePath();
      });
      JDialog dialog = GuiActionRunner.execute(PositionDialog::open);
      DialogFixture missingFix =
          WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME)).using(robot());
      SwingUtilities.invokeAndWait(missingFix.target()::dispose);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        field(scene, "lon_degree").setText("99");
        option(scene, 3, 5).fire();
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        PositionModel.jTextField1.setText("52");
        PositionModel.selections.latitude.set(PositionSelections.Latitude.SOUTH);
        PositionModel.selections.course.set(PositionSelections.Course.E);
      });
      SwingUtilities.invokeAndWait(() -> {});
      CloudCoverFxTestSupport.onFx(() -> {
        assertEquals("52", field(scene, "lat_degree").getText());
        assertEquals("99", field(scene, "lon_degree").getText());
        assertTrue(option(scene, 0, 1).isSelected());
        assertTrue(option(scene, 2, 2).isSelected());
        assertTrue(option(scene, 3, 5).isSelected());
        CloudCoverFxTestSupport.click(scene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
    } finally {
      SwingUtilities.invokeAndWait(() -> main.logs_dir = originalLogs);
    }
  }

  @Test
  public void sequenceNavigatesBackForwardAndStops() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = true;
      main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_YES;
    });
    try {
      JDialog back = GuiActionRunner.execute(PositionDialog::open);
      Scene backScene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      DialogFixture observer =
          WindowFinder.findDialog(new DialogTitleMatcher("Observer")).using(robot());
      SwingUtilities.invokeAndWait(observer.target()::dispose);
      JDialog forward = GuiActionRunner.execute(PositionDialog::open);
      Scene forwardScene = CloudCoverFxTestSupport.awaitScene(forward);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(forwardScene, "Cancel");
        return null;
      });
      DialogFixture barometer =
          WindowFinder.findDialog(new DialogTitleMatcher("Barometer reading")).using(robot());
      SwingUtilities.invokeAndWait(barometer.target()::dispose);
      JDialog stop = GuiActionRunner.execute(PositionDialog::open);
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

  private static void enterPosition(Scene scene) {
    field(scene, "lat_degree").setText("10");
    field(scene, "lat_minute").setText("11");
    field(scene, "lon_degree").setText("12");
    field(scene, "lon_minute").setText("13");
    option(scene, 0, 0).fire();
    option(scene, 1, 0).fire();
  }

  private static TextField field(Scene scene, String id) {
    return (TextField) scene.lookup("#" + id);
  }

  private static RadioButton option(Scene scene, int group, int index) {
    return (RadioButton) scene.lookup("#position_option_" + group + "_" + index);
  }
}
