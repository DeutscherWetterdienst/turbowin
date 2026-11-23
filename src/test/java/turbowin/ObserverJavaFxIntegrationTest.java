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

public class ObserverJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void loadsEditsAndSavesActiveCell() throws Exception {
    Path directory = startupDirectory.newFolder("observer-log").toPath();
    Path log = directory.resolve(main.OBSERVER_LOG);
    Files.writeString(log, "Doe;J.D.;Captain;N1234;\n", StandardCharsets.UTF_8);
    SwingUtilities.invokeAndWait(() -> {
      main.logs_dir = directory.toString();
      main.eucaws_obs_id = false;
      main.in_next_sequence = false;
      myobserver.selected_observer = "Doe;J.D.;Captain;N1234;";
    });
    JDialog dialog = GuiActionRunner.execute(ObserverDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    awaitLoaded(scene);
    CloudCoverFxTestSupport.onFx(() -> {
      TableView<ObserverView.Row> table = table(scene);
      assertEquals("Doe", table.getItems().get(0).value(0).get());
      assertEquals(0, table.getSelectionModel().getSelectedIndex());
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("Back", "OK", "Cancel", main.offline_mode ? "Help" : "Internet", "Stop"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      table.edit(0, table.getColumns().get(2));
      scene.getRoot().applyCss();
      scene.getRoot().layout();
      TextField editor = (TextField) table.lookup(".text-field");
      assertNotNull(editor);
      editor.setText("Officer");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("Doe;J.D.;Officer;N1234;", myobserver.selected_observer);
    });
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
    while (System.nanoTime() < deadline
        && !Files.readString(log).contains("Doe;J.D.;Officer;N1234;")) Thread.sleep(25);
    assertTrue(Files.readString(log).contains("Doe;J.D.;Officer;N1234;"));
  }

  @Test
  public void computedObserverIdRequiresInitialsAndRemainsReadOnly() throws Exception {
    Path directory = startupDirectory.newFolder("observer-ids").toPath();
    SwingUtilities.invokeAndWait(() -> {
      main.logs_dir = directory.toString();
      main.eucaws_obs_id = true;
      main.in_next_sequence = true;
      myobserver.selected_observer = "";
    });
    try {
      JDialog dialog = GuiActionRunner.execute(ObserverDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      awaitLoaded(scene);
      CloudCoverFxTestSupport.onFx(() -> {
        TableView<ObserverView.Row> table = table(scene);
        assertFalse(table.getColumns().get(2).isEditable());
        table.getItems().get(0).value(0).set("Smith");
        table.getSelectionModel().select(0);
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      DialogFixture warning =
          WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
              .using(robot());
      warning.button(JButtonMatcher.withText("OK")).click();
      SwingUtilities.invokeAndWait(() -> {
        assertTrue(dialog.isDisplayable());
        assertTrue(main.in_next_sequence);
      });
      CloudCoverFxTestSupport.onFx(() -> {
        table(scene).getItems().get(0).value(1).set("J.B.");
        table(scene).getItems().get(1).value(0).set("Jones");
        table(scene).getItems().get(1).value(1).set("J.B.");
        table(scene).getSelectionModel().select(1);
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertFalse(main.in_next_sequence);
        assertEquals("JB1", myobserver.observer_data[0][2]);
        assertEquals("Jones;J.B.;JB2;-;", myobserver.selected_observer);
      });
    } finally {
      SwingUtilities.invokeAndWait(() -> {
        main.in_next_sequence = false;
        main.eucaws_obs_id = false;
      });
    }
  }

  @Test
  public void cancelPreservesLogAndBackNavigatesToCloudCover() throws Exception {
    Path directory = startupDirectory.newFolder("observer-cancel").toPath();
    Path log = directory.resolve(main.OBSERVER_LOG);
    Files.writeString(log, "Doe;J.D.;Captain;N1234;\n", StandardCharsets.UTF_8);
    SwingUtilities.invokeAndWait(() -> {
      main.logs_dir = directory.toString();
      main.eucaws_obs_id = false;
      main.in_next_sequence = true;
    });
    try {
      JDialog cancel = GuiActionRunner.execute(ObserverDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(cancel);
      awaitLoaded(scene);
      CloudCoverFxTestSupport.onFx(() -> {
        table(scene).getItems().get(0).value(0).set("Changed");
        CloudCoverFxTestSupport.click(scene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(main.in_next_sequence));
      assertEquals("Doe;J.D.;Captain;N1234;\n", Files.readString(log));
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = true);
      JDialog back = GuiActionRunner.execute(ObserverDialog::open);
      Scene backScene = CloudCoverFxTestSupport.awaitScene(back);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(backScene, "Back");
        return null;
      });
      DialogFixture clouds =
          WindowFinder.findDialog(new DialogTitleMatcher(CloudCoverJavaFxWindow.TITLE))
              .using(robot());
      SwingUtilities.invokeAndWait(clouds.target()::dispose);
    } finally {
      SwingUtilities.invokeAndWait(() -> main.in_next_sequence = false);
    }
  }

  static void awaitLoaded(Scene scene) throws Exception {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
    while (System.nanoTime() < deadline) {
      if (CloudCoverFxTestSupport.onFx(() -> !table(scene).isDisabled())) return;
      Thread.sleep(25);
    }
    throw new AssertionError("Observer data did not load");
  }

  @SuppressWarnings("unchecked")
  static TableView<ObserverView.Row> table(Scene scene) {
    return (TableView<ObserverView.Row>) scene.lookup("#observer_table");
  }
}
