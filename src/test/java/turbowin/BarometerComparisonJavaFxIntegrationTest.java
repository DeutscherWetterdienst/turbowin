package turbowin;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;

public class BarometerComparisonJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void computesFromFormAndInvalidatesResultsWhenInputsChange() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.ship_name = "Test ship";
      main.barometer_above_sll = "20";
      main.keel_sll = "15";
      main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_NO;
    });
    Path exportPath = startupDirectory.getRoot().toPath().resolve("comparison.txt");
    JDialog dialog =
        GuiActionRunner.execute(() ->
            FxSwingDialog.show(
                main.mainClass,
                "Barometer comparison",
                new Dimension(1000, 700),
                close ->
                    BarometerComparisonView.create(
                        main.ship_name,
                        main.barometer_above_sll,
                        main.keel_sll,
                        () -> false,
                        close,
                        owner -> exportPath.toFile())));
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      scene.getRoot().layout();
      ScrollPane scroll = (ScrollPane) ((VBox) scene.getRoot()).getChildren().get(0);
      assertTrue(
          scroll.getContent().getBoundsInLocal().getHeight()
              <= scroll.getViewportBounds().getHeight());
      assertEquals("Test ship", field(scene, "ship").getText());
      assertTrue(field(scene, "height").isDisabled());
      assertTrue(field(scene, "keel").isDisabled());
      assertTrue(button(scene, "Export").isDisabled());
      field(scene, "ship_pressure").setText("1000");
      field(scene, "reference_pressure").setText("1014");
      field(scene, "draft").setText("12,8");
      field(scene, "temperature").setText("20");
      CloudCoverFxTestSupport.click(scene, "Calc");
      assertEquals("22.2", field(scene, "waterline_height").getText());
      assertEquals("1002.6", field(scene, "sea_level_pressure").getText());
      assertEquals("11.4", field(scene, "instrument_correction").getText());
      assertFalse(button(scene, "Export").isDisabled());
      CloudCoverFxTestSupport.click(scene, "Export");
      return null;
    });
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
    while (System.nanoTime() < deadline
        && !CloudCoverFxTestSupport.onFx(() ->
            ((Label) scene.lookup("#comparison_status"))
                .getText()
                .startsWith("Comparison written"))) {
      Thread.sleep(25);
    }
    assertTrue(
        Files.readString(exportPath).contains("instrument correction ship barometer: 11.4 hPa"));
    CloudCoverFxTestSupport.onFx(() -> {
      assertTrue(
          ((Label) scene.lookup("#comparison_status")).getText().startsWith("Comparison written"));
      field(scene, "draft").setText("14");
      assertEquals("", field(scene, "sea_level_pressure").getText());
      assertTrue(button(scene, "Export").isDisabled());
      field(scene, "ship_pressure").setText("799");
      CloudCoverFxTestSupport.click(scene, "Calc");
      assertEquals(
          "ship barometer reading out of physical range",
          ((Label) scene.lookup("#comparison_status")).getText());
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
  }

  @Test
  public void seaLevelBarometerShowsWhyComparisonCannotBeCalculated() throws Exception {
    SwingUtilities.invokeAndWait(
        () -> main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_YES);
    JDialog dialog = GuiActionRunner.execute(BarometerComparisonDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      CloudCoverFxTestSupport.click(scene, "Calc");
      assertTrue(((Label) scene.lookup("#comparison_status")).getText().contains("bridge level"));
      assertTrue(button(scene, "Export").isDisabled());
      return null;
    });
    SwingUtilities.invokeAndWait(
        () -> main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_NO);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "ship_pressure").setText("799");
      CloudCoverFxTestSupport.click(scene, "Calc");
      assertFalse(((Label) scene.lookup("#comparison_status")).getText().contains("bridge level"));
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
  }

  private static TextField field(Scene scene, String id) {
    return (TextField) scene.lookup("#" + id);
  }

  private static Button button(Scene scene, String text) {
    return scene.getRoot().lookupAll(".button").stream()
        .filter(Button.class::isInstance)
        .map(Button.class::cast)
        .filter(button -> text.equals(button.getText()))
        .findFirst()
        .orElseThrow();
  }
}
