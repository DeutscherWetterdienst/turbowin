package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LegacyDataMigrationTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void copiesConfigurationLogsAndAmverDataAndKeepsLegacyFiles() throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.writeString(legacy.resolve(main.CONFIGURATION_FILE), "ship name          : Old ship\n");
    Files.createDirectories(legacy.resolve(main.OFFLINE_LOGS_DIR).resolve("history"));
    Files.writeString(
        legacy.resolve(main.OFFLINE_LOGS_DIR).resolve("history").resolve("immt.log"), "record\n");
    Files.createDirectories(legacy.resolve(main.OFFLINE_AMVER_DIR));
    Files.writeString(legacy.resolve(main.OFFLINE_AMVER_DIR).resolve("report.txt"), "report\n");
    Files.createDirectories(legacy.resolve(main.OFFLINE_HELP_DIR));
    Files.writeString(legacy.resolve("turbowin_launcher"), "launcher\n");

    LegacyDataMigration.migrate(List.of(legacy), target);

    assertEquals(
        "ship name          : Old ship\n",
        Files.readString(target.resolve(main.CONFIGURATION_FILE)));
    assertEquals(
        "record\n",
        Files.readString(
            target.resolve(main.OFFLINE_LOGS_DIR).resolve("history").resolve("immt.log")));
    assertEquals(
        "report\n", Files.readString(target.resolve(main.OFFLINE_AMVER_DIR).resolve("report.txt")));
    assertFalse(
        "application help is installed with the application", Files.exists(target.resolve("help")));
    assertFalse(
        "startup markers are not user settings", Files.exists(target.resolve("turbowin_launcher")));
    assertTrue(Files.exists(legacy.resolve(main.CONFIGURATION_FILE)));
    assertTrue(Files.exists(legacy.resolve(main.OFFLINE_LOGS_DIR)));
  }

  @Test
  public void doesNotOverwriteExistingTargetDataAndMergesLegacyDirectories() throws Exception {
    Path olderLegacy = temporaryFolder.newFolder("older-legacy").toPath();
    Path newerLegacy = temporaryFolder.newFolder("newer-legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.writeString(olderLegacy.resolve(main.CONFIGURATION_FILE), "older\n");
    Files.writeString(newerLegacy.resolve(main.CONFIGURATION_FILE), "newer\n");
    Files.createDirectories(newerLegacy.resolve(main.OFFLINE_LOGS_DIR));
    Files.writeString(newerLegacy.resolve(main.OFFLINE_LOGS_DIR).resolve("sensor.txt"), "sensor\n");
    Files.writeString(target.resolve(main.CONFIGURATION_FILE), "current\n");

    LegacyDataMigration.migrate(List.of(newerLegacy, olderLegacy), target);

    assertEquals("current\n", Files.readString(target.resolve(main.CONFIGURATION_FILE)));
    assertEquals(
        "sensor\n", Files.readString(target.resolve(main.OFFLINE_LOGS_DIR).resolve("sensor.txt")));
  }

  @Test
  public void skipsWhenLegacyAndTargetDirectoriesAreTheSame() throws Exception {
    Path directory = temporaryFolder.newFolder("same-directory").toPath();
    Files.writeString(directory.resolve(main.CONFIGURATION_FILE), "current\n");

    LegacyDataMigration.migrate(List.of(directory), directory);

    assertEquals("current\n", Files.readString(directory.resolve(main.CONFIGURATION_FILE)));
  }
}
