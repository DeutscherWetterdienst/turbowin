package turbowin;

import static org.junit.Assert.*;

import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;

public class CalculatorJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void convertsAllUnitsAndClearsOnlyChangedResult() throws Exception {
    JDialog dialog = GuiActionRunner.execute(CalculatorDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      scene.getRoot().layout();
      ScrollPane scroll = (ScrollPane) ((VBox) scene.getRoot()).getChildren().get(0);
      assertTrue(
          scroll.getContent().getBoundsInLocal().getHeight()
              <= scroll.getViewportBounds().getHeight());
      Map<String, String> inputs =
          Map.of(
              "feet_to_metres",
              "10",
              "metres_to_feet",
              "10",
              "fahrenheit_to_celsius",
              "32",
              "celsius_to_fahrenheit",
              "0",
              "mph_to_knots",
              "10",
              "knots_to_mph",
              "10",
              "inhg_to_hpa",
              "30",
              "hpa_to_inhg",
              "1000");
      inputs.forEach((id, value) -> field(scene, id, "input").setText(value));
      CloudCoverFxTestSupport.click(scene, "Calculate");
      Map<String, String> outputs =
          Map.of(
              "feet_to_metres",
              "3.0",
              "metres_to_feet",
              "32.8",
              "fahrenheit_to_celsius",
              "0.0",
              "celsius_to_fahrenheit",
              "32.0",
              "mph_to_knots",
              "8.7",
              "knots_to_mph",
              "11.5",
              "inhg_to_hpa",
              "1015.9",
              "hpa_to_inhg",
              "29.5");
      outputs.forEach((id, value) -> {
        assertEquals(value, field(scene, id, "output").getText());
        assertFalse(field(scene, id, "output").isEditable());
      });
      field(scene, "feet_to_metres", "input").setText("12,5");
      assertEquals("12.5", field(scene, "feet_to_metres", "input").getText());
      assertEquals("", field(scene, "feet_to_metres", "output").getText());
      assertEquals("32.8", field(scene, "metres_to_feet", "output").getText());
      CloudCoverFxTestSupport.click(scene, "Calculate");
      assertEquals("3.8", field(scene, "feet_to_metres", "output").getText());
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
  }

  @Test
  public void invalidInputIsReportedWhileOtherConversionsStillWork() throws Exception {
    JDialog dialog = GuiActionRunner.execute(CalculatorDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene, "feet_to_metres", "input").setText("-");
      field(scene, "celsius_to_fahrenheit", "input").setText("-40");
      CloudCoverFxTestSupport.click(scene, "Calculate");
      assertEquals("feet invalid input", ((Label) scene.lookup("#calculator_errors")).getText());
      assertEquals("-40.0", field(scene, "celsius_to_fahrenheit", "output").getText());
      field(scene, "feet_to_metres", "input").setText("2");
      assertEquals("", ((Label) scene.lookup("#calculator_errors")).getText());
      CloudCoverFxTestSupport.click(scene, "Calculate");
      assertEquals("0.6", field(scene, "feet_to_metres", "output").getText());
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
  }

  private static TextField field(Scene scene, String conversion, String type) {
    return (TextField) scene.lookup("#" + conversion + "_" + type);
  }
}
