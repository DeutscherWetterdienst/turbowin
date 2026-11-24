package turbowin;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;

public class LatestMeasurementsJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void refreshesAndExportsTheDisplayedMeasurements() throws Exception {
    String oldLogs = main.logs_dir;
    Path logs = startupDirectory.newFolder("aws-measurements").toPath();
    Instant now = Instant.now();
    Path source = LatestMeasurementsDataTest.sensorFile(logs, now);
    Path destination = logs.resolve("export.json");
    Files.writeString(source, LatestMeasurementsDataTest.record(now, "00", "5.0"));
    SwingUtilities.invokeAndWait(() -> {
      main.logs_dir = logs.toString();
      main.ship_name = "Ship A";
    });
    AtomicBoolean closed = new AtomicBoolean();
    JDialog dialog = null;
    try {
      dialog =
          GuiActionRunner.execute(() ->
              FxSwingDialog.show(
                  main.mainClass,
                  "Latest AWS measurements",
                  new Dimension(1000, 700),
                  close ->
                      LatestMeasurementsView.create(
                          closed, false, close, owner -> destination.toFile())));
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      await(scene, () -> table(scene).getItems().size() == 1);
      assertEquals(
          "5.00", CloudCoverFxTestSupport.onFx(() -> table(scene).getItems().get(0).get(8)));
      Files.writeString(
          source, LatestMeasurementsDataTest.record(now, "00", "7.5"), StandardOpenOption.APPEND);
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(scene, "Refresh");
        return null;
      });
      await(
          scene,
          () ->
              !table(scene).getItems().isEmpty()
                  && table(scene).getItems().get(0).get(8).equals("7.50"));
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(scene, "Export");
        return null;
      });
      await(
          scene,
          () ->
              ((Label) scene.lookup("#measurements_status"))
                  .getText()
                  .startsWith("Measurements written"));
      String json = Files.readString(destination);
      assertTrue(json.contains("\"ship name\": \"Ship A\""));
      assertTrue(json.contains("\"wind speed true [m/s]\": \"7.50\""));
      CloudCoverFxTestSupport.onFx(() -> {
        CloudCoverFxTestSupport.click(scene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {});
      assertFalse(dialog.isDisplayable());
    } finally {
      closed.set(true);
      JDialog window = dialog;
      SwingUtilities.invokeAndWait(() -> {
        if (window != null) window.dispose();
        main.logs_dir = oldLogs;
      });
    }
  }

  @SuppressWarnings("unchecked")
  private static TableView<List<String>> table(Scene scene) {
    return (TableView<List<String>>) scene.lookup("#measurements_table");
  }

  private static void await(Scene scene, java.util.concurrent.Callable<Boolean> condition)
      throws Exception {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
    while (System.nanoTime() < deadline) {
      if (CloudCoverFxTestSupport.onFx(condition)) return;
      Thread.sleep(25);
    }
    String status =
        CloudCoverFxTestSupport.onFx(
            () -> ((Label) scene.lookup("#measurements_status")).getText());
    fail("Measurement view did not update: " + status);
  }
}
