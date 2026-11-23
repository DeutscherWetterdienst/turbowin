package turbowin;

import static org.junit.Assert.*;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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

public class WindJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void beaufortImageUsesTheCorrectSpeedRanges() {
    assertEquals(0, WindView.beaufort("0", false));
    assertEquals(4, WindView.beaufort("16", false));
    assertEquals(5, WindView.beaufort("17", false));
    assertEquals(12, WindView.beaufort("64", false));
    assertEquals(4, WindView.beaufort("7", true));
    assertEquals(5, WindView.beaufort("8", true));
    assertEquals(12, WindView.beaufort("33", true));
    assertEquals(-1, WindView.beaufort("", true));
  }

  @Test
  public void estimatedWindSavesAndRestoresValuesAndBeaufortImage() throws Exception {
    prepare(main.ESTIMATED_TRUE);
    JDialog dialog = GuiActionRunner.execute(WindDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      assertTrue(field(scene, "wind_3").isDisabled());
      field(scene, "wind_1").setText("210");
      field(scene, "wind_2").setText("15");
      assertEquals("Beaufort 4", scene.lookup("#beaufort_image").getAccessibleText());
      assertFalse(((ImageView) scene.lookup("#beaufort_image")).getImage().isError());
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
      assertEquals(210, WindModel.int_true_wind_dir);
      assertEquals(15, WindModel.int_true_wind_speed);
      assertEquals("21", WindModel.dd_code);
      assertEquals("15", WindModel.ff_code);
    });
    JDialog reopened = GuiActionRunner.execute(WindDialog::open);
    Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("210", field(reopenedScene, "wind_1").getText());
      assertEquals("15", field(reopenedScene, "wind_2").getText());
      CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
  }

  @Test
  public void apparentWindUsesShipMovementToCalculateTrueWind() throws Exception {
    prepare(main.MEASURED_OFF_BOW);
    JDialog dialog = GuiActionRunner.execute(WindDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "wind_1").setText("90");
      field(scene, "wind_2").setText("10");
      field(scene, "wind_3").setText("stationary");
      field(scene, "wind_4").setText("0");
      field(scene, "wind_5").setText("180");
      assertNull(((ImageView) scene.lookup("#beaufort_image")).getImage());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("090", WindModel.RWD_code);
      assertEquals("010", WindModel.RWS_code);
      assertEquals(10, WindModel.int_true_wind_speed);
      assertNotEquals(main.INVALID, WindModel.int_true_wind_dir);
    });
  }

  @Test
  public void awsWindIsReadOnlyButCargoValuesCanBeChanged() throws Exception {
    prepare(main.MEASURED_TRUE);
    SwingUtilities.invokeAndWait(() -> {
      main.true_wind_dir_from_AWS_present = true;
      main.true_wind_speed_from_AWS_present = true;
      WindModel.int_true_wind_dir = 270;
      WindModel.int_true_wind_speed = 20;
    });
    try {
      JDialog dialog = GuiActionRunner.execute(WindDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        for (int i = 1; i <= 5; i++) assertFalse(field(scene, "wind_" + i).isEditable());
        assertTrue(field(scene, "wind_6").isEditable());
        field(scene, "wind_6").setText("9");
        field(scene, "wind_7").setText("-2");
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals(270, WindModel.int_true_wind_dir);
        assertEquals(20, WindModel.int_true_wind_speed);
        assertEquals("9", main.max_height_deck_cargo);
        assertEquals("-2", main.diff_sll_wl);
      });
    } finally {
      SwingUtilities.invokeAndWait(() -> {
        main.true_wind_dir_from_AWS_present = false;
        main.true_wind_speed_from_AWS_present = false;
      });
    }
  }

  @Test
  public void invalidValuesKeepTheDialogOpenForCorrection() throws Exception {
    prepare(main.ESTIMATED_TRUE);
    JDialog dialog = GuiActionRunner.execute(WindDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "wind_1").setText("361");
      field(scene, "wind_2").setText("5");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME)).using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "wind_1").setText("180");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals(180, WindModel.int_true_wind_dir);
    });
  }

  @Test
  public void sequenceNavigatesBackForwardAndStops() throws Exception {
    prepare(main.ESTIMATED_TRUE);
    SwingUtilities.invokeAndWait(() -> main.in_next_sequence = true);
    try {
      JDialog back = GuiActionRunner.execute(WindDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(scene, "Back");
        return null;
      });
      DialogFixture temperature =
          WindowFinder.findDialog(new DialogTitleMatcher("Temperatures")).using(robot());
      SwingUtilities.invokeAndWait(temperature.target()::dispose);
      JDialog forward = GuiActionRunner.execute(WindDialog::open);
      Scene forwardScene = CloudCoverFxTestSupport.awaitScene(forward);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(forwardScene, "Cancel");
        return null;
      });
      DialogFixture waves = WindowFinder.findDialog(new DialogTitleMatcher("Waves")).using(robot());
      SwingUtilities.invokeAndWait(waves.target()::dispose);
      JDialog stop = GuiActionRunner.execute(WindDialog::open);
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

  private static void prepare(String source) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.in_next_sequence = false;
      main.wind_source = source;
      main.wind_units = main.KNOTS;
      main.true_wind_dir_from_AWS_present = false;
      main.true_wind_speed_from_AWS_present = false;
      main.max_height_deck_cargo = "5";
      main.diff_sll_wl = "2";
      WindModel.int_true_wind_dir = main.INVALID;
      WindModel.int_true_wind_speed = main.INVALID;
      WindModel.int_relative_wind_dir = main.INVALID;
      WindModel.int_relative_wind_speed = main.INVALID;
      WindModel.ship_ground_course = "";
      WindModel.ship_ground_speed = "";
      WindModel.ship_heading = "";
    });
  }

  private static TextField field(Scene scene, String id) {
    return (TextField) scene.lookup("#" + id);
  }
}
