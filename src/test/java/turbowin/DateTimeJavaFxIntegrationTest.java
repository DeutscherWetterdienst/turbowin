package turbowin;

import static org.junit.Assert.*;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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

public class DateTimeJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void savesLeapDayAndRestoresAllSelections() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      mydatetime.year = "";
      mydatetime.month = "";
      mydatetime.day = "";
      mydatetime.hour = "";
    });
    JDialog dialog = GuiActionRunner.execute(DateTimeDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      select(scene, "2024", "February", "29", "3");
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("OK", "Cancel", main.offline_mode ? "Help" : "Internet"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("2024", mydatetime.year);
      assertEquals("February", mydatetime.month);
      assertEquals("29", mydatetime.day);
      assertEquals("3", mydatetime.hour);
      assertEquals("02", mydatetime.MM_code);
      assertEquals("29", mydatetime.YY_code);
      assertEquals("03", mydatetime.GG_code);
    });
    JDialog reopened = GuiActionRunner.execute(DateTimeDialog::open);
    Scene restored = CloudCoverFxTestSupport.awaitScene(reopened);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("2024", list(restored, "year").getSelectionModel().getSelectedItem());
      assertEquals("February", list(restored, "month").getSelectionModel().getSelectedItem());
      assertEquals("29", list(restored, "day").getSelectionModel().getSelectedItem());
      assertEquals("3", list(restored, "hour").getSelectionModel().getSelectedItem());
      CloudCoverFxTestSupport.click(restored, "Cancel");
      return null;
    });
  }

  @Test
  public void invalidDateStaysOpenForCorrection() throws Exception {
    JDialog dialog = GuiActionRunner.execute(DateTimeDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      select(scene, "2025", "February", "29", "12");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      list(scene, "day").getSelectionModel().select("28");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("28", mydatetime.YY_code);
    });
  }

  @Test
  public void missingSelectionStaysOpenForCorrection() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      mydatetime.year = "";
      mydatetime.month = "";
      mydatetime.day = "";
      mydatetime.hour = "";
    });
    JDialog dialog = GuiActionRunner.execute(DateTimeDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    CloudCoverFxTestSupport.onFx(() -> {
      select(scene, "2024", "January", "1", "0");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("01", mydatetime.YY_code);
      assertEquals("00", mydatetime.GG_code);
    });
  }

  @Test
  public void awsModesAreReadOnlyAndPreserveSensorDate() throws Exception {
    for (int mode : new int[] {3, 9, 10, 11}) {
      SwingUtilities.invokeAndWait(() -> {
        main.RS232_connection_mode = mode;
        mydatetime.year = "2024";
        mydatetime.month = "February";
        mydatetime.day = "29";
        mydatetime.hour = "3";
        mydatetime.MM_code = "02";
        mydatetime.YY_code = "29";
        mydatetime.GG_code = "03";
      });
      JDialog dialog = GuiActionRunner.execute(DateTimeDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        for (String id : new String[] {"year", "month", "day", "hour"})
          assertTrue(list(scene, id).isDisabled());
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals("2024", mydatetime.year);
        assertEquals("02", mydatetime.MM_code);
        assertEquals("29", mydatetime.YY_code);
        assertEquals("03", mydatetime.GG_code);
      });
    }
  }

  private static void select(Scene scene, String year, String month, String day, String hour) {
    list(scene, "year").getSelectionModel().select(year);
    list(scene, "month").getSelectionModel().select(month);
    list(scene, "day").getSelectionModel().select(day);
    list(scene, "hour").getSelectionModel().select(hour);
  }

  @SuppressWarnings("unchecked")
  private static ListView<String> list(Scene scene, String id) {
    return (ListView<String>) scene.lookup("#" + id);
  }
}
