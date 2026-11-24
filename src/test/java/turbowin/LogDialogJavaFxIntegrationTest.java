package turbowin;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;

public class LogDialogJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void systemAndDeviceWindowsRefreshWithoutReopening() throws Exception {
    String oldLogs = main.logs_dir;
    String oldGps = main_RS232_RS422.GPS_array_device_log[0];
    Path root = startupDirectory.newFolder("live-logs").toPath();
    Path systemDir = Files.createDirectory(root.resolve(main.TURBOWIN_SYSTEM_LOGS_DIR));
    Instant now = Instant.now();
    String month =
        DateTimeFormatter.ofPattern("MMM_yyyy", Locale.ENGLISH)
            .withZone(ZoneOffset.UTC)
            .format(now);
    String hour = DateTimeFormatter.ofPattern("yyyyMMddHH").withZone(ZoneOffset.UTC).format(now);
    Path systemFile = systemDir.resolve("turbowin_system_" + month + ".txt");
    Path deviceFile = root.resolve("sensor_data_" + hour + ".txt");
    Files.writeString(systemFile, "system first\n");
    Files.writeString(deviceFile, "device first\n");
    SwingUtilities.invokeAndWait(() -> {
      main.logs_dir = root.toString();
      main.RS232_connection_mode = 3;
      main.RS232_GPS_connection_mode = 1;
    });
    JDialog system = null;
    JDialog device = null;
    try {
      system = GuiActionRunner.execute(() -> LogDialog.open(LogDialog.Kind.SYSTEM));
      device = GuiActionRunner.execute(() -> LogDialog.open(LogDialog.Kind.DEVICE));
      Scene systemScene = CloudCoverFxTestSupport.awaitScene(system);
      Scene deviceScene = CloudCoverFxTestSupport.awaitScene(device);
      awaitText(systemScene, "system first");
      awaitText(deviceScene, "device first");
      CloudCoverFxTestSupport.onFx(() -> {
        ((TextArea) systemScene.lookup("#log_text")).selectRange(0, 6);
        return null;
      });
      Files.writeString(systemFile, "system second\n", StandardOpenOption.APPEND);
      Files.writeString(deviceFile, "device second\n", StandardOpenOption.APPEND);
      awaitText(systemScene, "system second");
      awaitText(deviceScene, "device second");
      main_RS232_RS422.GPS_array_device_log[0] = "latest gps record";
      awaitText(deviceScene, "latest gps record");
      CloudCoverFxTestSupport.onFx(() -> {
        assertFalse(((TextArea) systemScene.lookup("#log_text")).isEditable());
        assertEquals("system", ((TextArea) systemScene.lookup("#log_text")).getSelectedText());
        CloudCoverFxTestSupport.click(systemScene, "Close");
        CloudCoverFxTestSupport.click(deviceScene, "Close");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {});
      assertFalse(system.isDisplayable());
      assertFalse(device.isDisplayable());
    } finally {
      JDialog systemWindow = system;
      JDialog deviceWindow = device;
      SwingUtilities.invokeAndWait(() -> {
        if (systemWindow != null) systemWindow.dispose();
        if (deviceWindow != null) deviceWindow.dispose();
        main.logs_dir = oldLogs;
        main_RS232_RS422.GPS_array_device_log[0] = oldGps;
      });
    }
  }

  private static void awaitText(Scene scene, String expected) throws Exception {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
    while (System.nanoTime() < deadline) {
      if (CloudCoverFxTestSupport.onFx(
          () -> ((TextArea) scene.lookup("#log_text")).getText().contains(expected))) return;
      Thread.sleep(50);
    }
    fail("Log did not update with " + expected);
  }
}
