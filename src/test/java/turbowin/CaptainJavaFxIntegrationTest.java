package turbowin;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
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

public class CaptainJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void loadsAndSavesAnActivelyEditedCell() throws Exception {
    Path directory = startupDirectory.newFolder("captain-log").toPath();
    Path log = directory.resolve(main.CAPTAIN_LOG);
    Files.writeString(log, "Doe;J.D.;01/01/2024;-;N1234;\n", StandardCharsets.UTF_8);
    SwingUtilities.invokeAndWait(() -> {
      main.logs_dir = directory.toString();
      main.recruiting_country = "";
    });
    JDialog dialog = GuiActionRunner.execute(CaptainDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    awaitLoaded(scene);
    CloudCoverFxTestSupport.onFx(() -> {
      TableView<CaptainView.Row> table = table(scene);
      assertEquals("Doe", table.getItems().get(0).value(0).get());
      assertEquals("01/01/2024", table.getItems().get(0).value(2).get());
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("OK", "Cancel", main.offline_mode ? "Help" : "Internet"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      table.edit(0, table.getColumns().get(3));
      scene.getRoot().applyCss();
      scene.getRoot().layout();
      TextField editor = (TextField) table.lookup(".text-field");
      assertNotNull(editor);
      editor.setText("30/06/2024");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
    String expected = "Doe;J.D.;01/01/2024;30/06/2024;N1234;";
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
    while (System.nanoTime() < deadline && !Files.readString(log).contains(expected))
      Thread.sleep(25);
    assertTrue(Files.readString(log).contains(expected));
  }

  @Test
  public void invalidDatesKeepTheFormOpenAndDoNotSave() throws Exception {
    Path directory = startupDirectory.newFolder("captain-invalid").toPath();
    Path log = directory.resolve(main.CAPTAIN_LOG);
    String initial = "Doe;J.D.;01/01/2024;-;N1234;\n";
    Files.writeString(log, initial, StandardCharsets.UTF_8);
    SwingUtilities.invokeAndWait(() -> main.logs_dir = directory.toString());
    JDialog dialog = GuiActionRunner.execute(CaptainDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    awaitLoaded(scene);
    CloudCoverFxTestSupport.onFx(() -> {
      table(scene).getItems().get(0).value(2).set("2024-01-01");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    assertEquals(initial, Files.readString(log));
    CloudCoverFxTestSupport.onFx(() -> {
      table(scene).getItems().get(0).value(2).set("01/01/2024");
      table(scene).getItems().get(0).value(3).set("invalid");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    warning =
        WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
            .using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    SwingUtilities.invokeAndWait(() -> assertTrue(dialog.isDisplayable()));
    assertEquals(initial, Files.readString(log));
    CloudCoverFxTestSupport.onFx(() -> {
      table(scene).getItems().get(0).value(3).set("15/02/2024");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
    String corrected = "Doe;J.D.;01/01/2024;15/02/2024;N1234;";
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
    while (System.nanoTime() < deadline && !Files.readString(log).contains(corrected))
      Thread.sleep(25);
    assertTrue(Files.readString(log).contains(corrected));
  }

  @Test
  public void germanHeadingAndCancellationPreserveTheLog() throws Exception {
    Path directory = startupDirectory.newFolder("captain-cancel").toPath();
    Path log = directory.resolve(main.CAPTAIN_LOG);
    String initial = "Doe;Jane;01/01/2024;-;N1234;\n";
    Files.writeString(log, initial, StandardCharsets.UTF_8);
    SwingUtilities.invokeAndWait(() -> {
      main.logs_dir = directory.toString();
      main.recruiting_country = "GERMANY";
    });
    JDialog dialog = GuiActionRunner.execute(CaptainDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    awaitLoaded(scene);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("Full christian name", table(scene).getColumns().get(1).getText());
      table(scene).getItems().get(0).value(0).set("Changed");
      CloudCoverFxTestSupport.click(scene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> assertFalse(dialog.isDisplayable()));
    assertEquals(initial, Files.readString(log));
  }

  private static void awaitLoaded(Scene scene) throws Exception {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
    while (System.nanoTime() < deadline) {
      if (CloudCoverFxTestSupport.onFx(() -> !table(scene).isDisabled())) return;
      Thread.sleep(25);
    }
    throw new AssertionError("Captain data did not load");
  }

  @SuppressWarnings("unchecked")
  private static TableView<CaptainView.Row> table(Scene scene) {
    return (TableView<CaptainView.Row>) scene.lookup("#captain_table");
  }
}
