package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
  public void overlappingSavesFinishWithNewestSettingsInSingleFile() throws Exception {
    verifyOrderedSaves(false);
  }

  @Test
  public void failedOlderWriteDoesNotBlockNewerSave() throws Exception {
    verifyOrderedSaves(true);
  }

  private void verifyOrderedSaves(boolean failFirstWrite) throws Exception {
    String originalDataDirectory = main.data_dir;
    String originalLogsDirectory = main.logs_dir;
    String originalShipName = main.ship_name;
    String[] originalLines = main.configuratie_regels.clone();
    Path data = temporaryFolder.newFolder("data").toPath();
    Path logs = temporaryFolder.newFolder("logs").toPath();
    CountDownLatch firstStarted = new CountDownLatch(1);
    CountDownLatch releaseFirst = new CountDownLatch(1);
    CountDownLatch newerStarted = new CountDownLatch(1);
    CountDownLatch completed = new CountDownLatch(2);
    List<String> attempts = Collections.synchronizedList(new ArrayList<>());
    ConfigurationWriteWorkflow.ConfigurationWriter writer =
        (directory, lines, maximum) -> {
          try {
            boolean older = lines[0].equals(main.SHIP_NAME_TXT + "Older ship");
            attempts.add((older ? "older " : "newer ") + Path.of(directory).getFileName());
            if (!older) {
              newerStarted.countDown();
            }
            if (older && directory.equals(data.toString())) {
              firstStarted.countDown();
              try {
                if (!releaseFirst.await(5, TimeUnit.SECONDS)) {
                  throw new IOException("Timed out waiting to release first save");
                }
              } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IOException(ex);
              }
              if (failFirstWrite) {
                throw new IOException("Simulated older data-copy failure");
              }
            }
            ConfigurationWriteWorkflow.writeConfigurationFile(directory, lines, maximum);
          } finally {
            completed.countDown();
          }
        };
    boolean firstScheduled = false;
    boolean bothScheduled = false;
    try {
      main.data_dir = data.toString();
      main.logs_dir = logs.toString();
      main.ship_name = "Older ship";
      if (failFirstWrite) {
        ConfigurationWriteWorkflow.saveBeforeExit(saved -> {}, writer);
      } else {
        ConfigurationWriteWorkflow.write(writer);
      }
      firstScheduled = true;
      assertTrue("First save did not start", firstStarted.await(5, TimeUnit.SECONDS));

      main.ship_name = "Newer ship";
      ConfigurationWriteWorkflow.write(writer);
      bothScheduled = true;
      assertFalse(
          "Newer save overtook the blocked older save",
          newerStarted.await(200, TimeUnit.MILLISECONDS));
      releaseFirst.countDown();
      assertTrue("Queued saves did not finish", completed.await(5, TimeUnit.SECONDS));

      assertEquals(List.of("older data", "newer data"), attempts);
      String saved = Files.readString(data.resolve(main.CONFIGURATION_FILE));
      assertTrue(saved.contains(main.SHIP_NAME_TXT + "Newer ship"));
      assertFalse(saved.contains("Older ship"));
      assertFalse(Files.exists(logs.resolve(main.CONFIGURATION_FILE)));
    } finally {
      releaseFirst.countDown();
      try {
        if (!bothScheduled) {
          completed.countDown();
        }
        if (firstScheduled) {
          assertTrue("Save queue did not drain", completed.await(5, TimeUnit.SECONDS));
        }
      } finally {
        ConfigurationWriteWorkflow.cancelExit();
        main.data_dir = originalDataDirectory;
        main.logs_dir = originalLogsDirectory;
        main.ship_name = originalShipName;
        System.arraycopy(originalLines, 0, main.configuratie_regels, 0, originalLines.length);
      }
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
              // A queued worker must retain its snapshot when globals change.
              main.data_dir = laterData.toString();
              main.logs_dir = laterLogs.toString();
              main.ship_name = "Later ship";
              main.fill_configuratie_array();
            }
          });

      assertEquals(1, pendingWrites.size());
      for (SwingWorker<Void, Void> worker : pendingWrites) {
        worker.execute();
        worker.get(5, TimeUnit.SECONDS);
      }

      assertTrue(
          "The original data destination was lost",
          Files.isRegularFile(data.resolve(main.CONFIGURATION_FILE)));
      assertFalse(Files.exists(logs.resolve(main.CONFIGURATION_FILE)));
      String savedData = Files.readString(data.resolve(main.CONFIGURATION_FILE));
      assertTrue(savedData.contains(main.SHIP_NAME_TXT + "Ship at save time"));
      assertTrue(savedData.contains(main.LOGS_DIR_TXT + logs));
      assertFalse(savedData.contains("Later ship"));
      assertFalse(Files.exists(logs.resolve(main.CONFIGURATION_FILE)));
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
        ConfigurationWriteWorkflow.cancelExit();
        main.data_dir = originalDataDirectory;
        main.logs_dir = originalLogsDirectory;
        main.ship_name = originalShipName;
        System.arraycopy(originalLines, 0, main.configuratie_regels, 0, originalLines.length);
      }
    }
  }
}
