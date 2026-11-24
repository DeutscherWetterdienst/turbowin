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
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class TemperatureJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void invalidTemperatureKeepsTheDialogOpenForCorrection() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.air_temp_from_AWS_present = false;
      main.rh_from_AWS_present = false;
      main.SST_from_AWS_present = false;
      main.air_temp_exposure = main.MARINE_SCREEN;
    });
    JDialog dialog = GuiActionRunner.execute(TemperatureDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "air_temp").setText("61");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "air_temp").setText("20");
      field(scene, "wet_bulb_temp").clear();
      field(scene, "sea_water_temp").clear();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("20", TemperatureModel.air_temp);
    });
  }

  @Test
  public void savesTemperaturesAndRestoresThemOnReopening() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.air_temp_from_AWS_present = false;
      main.rh_from_AWS_present = false;
      main.SST_from_AWS_present = false;
      main.air_temp_exposure = main.MARINE_SCREEN;
      main.sst_exposure = main.INTAKE;
      TemperatureModel.air_temp = "";
      TemperatureModel.wet_bulb_temp = "";
      TemperatureModel.RH = "";
      TemperatureModel.sea_water_temp = "";
    });
    JDialog dialog = GuiActionRunner.execute(TemperatureDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      field(scene, "air_temp").setText("20,0");
      field(scene, "wet_bulb_temp").setText("15");
      field(scene, "sea_water_temp").setText("18");
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
      assertEquals("20.0", TemperatureModel.air_temp);
      assertEquals("15", TemperatureModel.wet_bulb_temp);
      assertEquals("18", TemperatureModel.sea_water_temp);
      assertEquals("200", TemperatureModel.TTT_code);
      assertEquals("180", TemperatureModel.TsTsTs_code);
      assertNotEquals(main.INVALID, TemperatureModel.double_dew_point, 0.001);
    });
    JDialog reopened = GuiActionRunner.execute(TemperatureDialog::open);
    Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("20.0", field(reopenedScene, "air_temp").getText());
      assertEquals("15", field(reopenedScene, "wet_bulb_temp").getText());
      assertEquals("18", field(reopenedScene, "sea_water_temp").getText());
      CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
  }

  @Test
  public void sensorInputsRemainReadOnlyAndUpdateTheView() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.air_temp_from_AWS_present = true;
      main.rh_from_AWS_present = true;
      main.SST_from_AWS_present = true;
    });
    try {
      JDialog dialog = GuiActionRunner.execute(TemperatureDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        assertFalse(field(scene, "air_temp").isEditable());
        assertFalse(field(scene, "wet_bulb_temp").isEditable());
        assertFalse(field(scene, "rel_humidity").isEditable());
        assertFalse(field(scene, "sea_water_temp").isEditable());
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        TemperatureModel.jTextField1.setText("21.5");
        TemperatureModel.jTextField2.setText("-0.1");
        TemperatureModel.jTextField4.setText("72.0");
        TemperatureModel.selectedWetBulbFrozen = true;
        TemperatureDialog.sensorUpdated();
      });
      CloudCoverFxTestSupport.onFx(() -> {
        assertEquals("21.5", field(scene, "air_temp").getText());
        assertEquals("-0.1", field(scene, "wet_bulb_temp").getText());
        assertEquals("72.0", field(scene, "rel_humidity").getText());
        assertTrue(((RadioButton) scene.lookup("#wet_bulb_frozen")).isSelected());
        CloudCoverFxTestSupport.click(scene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
    } finally {
      SwingUtilities.invokeAndWait(() -> {
        main.air_temp_from_AWS_present = false;
        main.rh_from_AWS_present = false;
        main.SST_from_AWS_present = false;
      });
    }
  }

  @Test
  public void sequenceNavigatesBackForwardAndStops() throws Exception {
    SwingUtilities.invokeAndWait(() -> main.in_next_sequence = true);
    try {
      JDialog back = GuiActionRunner.execute(TemperatureDialog::open);
      Scene backScene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      DialogFixture barograph =
          WindowFinder.findDialog(new DialogTitleMatcher("Barograph reading")).using(robot());
      SwingUtilities.invokeAndWait(barograph.target()::dispose);
      JDialog forward = GuiActionRunner.execute(TemperatureDialog::open);
      Scene forwardScene = CloudCoverFxTestSupport.awaitScene(forward);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(forwardScene, "Cancel");
        return null;
      });
      DialogFixture wind = WindowFinder.findDialog(new DialogTitleMatcher("Wind")).using(robot());
      SwingUtilities.invokeAndWait(wind.target()::dispose);
      JDialog stop = GuiActionRunner.execute(TemperatureDialog::open);
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
