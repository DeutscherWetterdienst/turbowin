package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigurationWriteWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void writesConfigurationFileToRequestedDirectory() throws Exception {
    File dataDirectory = Files.createTempDirectory("turbowin-config-data").toFile();
    File logsDirectory = Files.createTempDirectory("turbowin-config-logs").toFile();
    String[] lines = {"first", "", "last"};
    try {
      ConfigurationWriteWorkflow.writeConfigurationFile(
          dataDirectory.getPath(), lines, lines.length);
      ConfigurationWriteWorkflow.writeConfigurationFile(
          logsDirectory.getPath(), lines, lines.length);

      String expected = "first" + System.lineSeparator() + "last" + System.lineSeparator();
      assertEquals(
          expected, Files.readString(new File(dataDirectory, main.CONFIGURATION_FILE).toPath()));
      assertEquals(
          expected, Files.readString(new File(logsDirectory, main.CONFIGURATION_FILE).toPath()));
    } finally {
      new File(dataDirectory, main.CONFIGURATION_FILE).delete();
      new File(logsDirectory, main.CONFIGURATION_FILE).delete();
      dataDirectory.delete();
      logsDirectory.delete();
    }
  }

  @Test
  public void pendingSavesKeepTheirSettingsAndDestinationsWhenGlobalsChange() throws Exception {
    String originalDataDirectory = main.data_dir;
    String originalLogsDirectory = main.logs_dir;
    String originalShipName = main.ship_name;
    String[] originalLines = main.configuratie_regels.clone();
    Path data = temporaryFolder.newFolder("data").toPath();
    Path logs = temporaryFolder.newFolder("logs").toPath();
    Path laterData = temporaryFolder.newFolder("later-data").toPath();
    Path laterLogs = temporaryFolder.newFolder("later-logs").toPath();
    List<SwingWorker<Void, Void>> pendingWrites = new ArrayList<>();
    try {
      main.data_dir = data.toString();
      main.logs_dir = logs.toString();
      main.ship_name = "Ship at save time";
      Arrays.fill(main.configuratie_regels, "");

      ConfigurationWriteWorkflow.write(
          worker -> {
            pendingWrites.add(worker);
            if (pendingWrites.size() == 1) {
              // The first worker may start before the second is scheduled. Both must use the
              // same original snapshot, even if another settings change rebuilds the array.
              main.data_dir = laterData.toString();
              main.logs_dir = laterLogs.toString();
              main.ship_name = "Later ship";
              main.fill_configuratie_array();
            }
          });

      assertEquals(2, pendingWrites.size());
      for (SwingWorker<Void, Void> worker : pendingWrites) {
        worker.execute();
        worker.get(5, TimeUnit.SECONDS);
      }

      assertTrue(
          "The original data destination was lost",
          Files.isRegularFile(data.resolve(main.CONFIGURATION_FILE)));
      assertTrue(
          "The original logs destination was lost",
          Files.isRegularFile(logs.resolve(main.CONFIGURATION_FILE)));
      String savedData = Files.readString(data.resolve(main.CONFIGURATION_FILE));
      assertTrue(savedData.contains(main.SHIP_NAME_TXT + "Ship at save time"));
      assertTrue(savedData.contains(main.LOGS_DIR_TXT + logs));
      assertFalse(savedData.contains("Later ship"));
      assertEquals(savedData, Files.readString(logs.resolve(main.CONFIGURATION_FILE)));
      assertFalse(Files.exists(laterData.resolve(main.CONFIGURATION_FILE)));
      assertFalse(Files.exists(laterLogs.resolve(main.CONFIGURATION_FILE)));
    } finally {
      try {
        for (SwingWorker<Void, Void> worker : pendingWrites) {
          // Drain even unstarted workers before restoring globals if an assertion fails.
          worker.run();
          worker.get(5, TimeUnit.SECONDS);
        }
      } finally {
        main.data_dir = originalDataDirectory;
        main.logs_dir = originalLogsDirectory;
        main.ship_name = originalShipName;
        System.arraycopy(originalLines, 0, main.configuratie_regels, 0, originalLines.length);
      }
    }
  }
}
