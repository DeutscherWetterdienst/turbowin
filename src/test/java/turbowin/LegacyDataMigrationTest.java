package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
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
        List.of("ship name          : Old ship"),
        Files.readAllLines(target.resolve(main.CONFIGURATION_FILE)));
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
    if (Files.getFileStore(target).supportsFileAttributeView("posix")) {
      Path reference = temporaryFolder.getRoot().toPath().resolve("ordinary-save.txt");
      ConfigurationFileStore.write(reference.toFile(), new String[] {"reference"}, 1);
      assertEquals(
          Files.getPosixFilePermissions(reference),
          Files.getPosixFilePermissions(target.resolve(main.CONFIGURATION_FILE)));
      assertEquals(
          Files.getPosixFilePermissions(reference),
          Files.getPosixFilePermissions(target.resolve("logs/configuration.txt")));
    }
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
  public void windowsLogsOnlyConfigurationIsDiscoveredOutsideWorkingDirectoryAndRebased()
      throws Exception {
    Path working = temporaryFolder.newFolder("unrelated").toPath();
    Path programFiles = temporaryFolder.newFolder("Program Files (x86)").toPath();
    Path legacy = programFiles.resolve("TurboWin+/bin");
    Path target = temporaryFolder.newFolder("ProgramData").toPath();
    Files.createDirectories(legacy.resolve("logs"));
    String original = configuration("Windows ship", legacy.resolve("logs").toString());
    Files.writeString(legacy.resolve("logs/configuration.txt"), original);

    LegacyDataMigration.migrate(
        LegacyInstallationLocator.find(
            working.toString(),
            "WINDOWS",
            Map.of("ProgramFiles(x86)", programFiles.toString()),
            working),
        target);

    assertMigratedCopies(target, "Windows ship", target.resolve("logs").toString());
    assertEquals(original, Files.readString(legacy.resolve("logs/configuration.txt")));
    assertFalse(Files.exists(legacy.resolve("configuration.txt")));
  }

  @Test
  public void prefersValidLogsCopyAndPublishesTheSameSettingsToBothDestinations() throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.createDirectory(legacy.resolve("logs"));
    String root = configuration("Root ship", legacy.resolve("logs").toString());
    String logs = configuration("Logs ship", legacy.resolve("logs").toString());
    Files.writeString(legacy.resolve("configuration.txt"), root);
    Files.writeString(legacy.resolve("logs/configuration.txt"), logs);

    LegacyDataMigration.migrate(List.of(legacy), target);

    assertMigratedCopies(target, "Logs ship", target.resolve("logs").toString());
    assertEquals(root, Files.readString(legacy.resolve("configuration.txt")));
    assertEquals(logs, Files.readString(legacy.resolve("logs/configuration.txt")));
  }

  @Test
  public void malformedPreferredCopyFallsBackToValidRootCopy() throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.createDirectory(legacy.resolve("logs"));
    Files.writeString(
        legacy.resolve("logs/configuration.txt"),
        main.SHIP_NAME_TXT + "Rejected ship\n" + main.RS232_INSTRUMENT_TYPE_TXT + "invalid\n");
    Files.writeString(legacy.resolve("configuration.txt"), configuration("Root ship", "logs"));

    LegacyDataMigration.migrate(List.of(legacy), target);

    assertMigratedCopies(target, "Root ship", target.resolve("logs").toString());
  }

  @Test
  public void preservesExternalAndDeliberatelyCustomLogsFolders() throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path external = temporaryFolder.newFolder("external-logs").toPath();
    for (Path custom :
        List.of(external, legacy.resolve("logs-custom"), legacy.resolve("logs/custom"))) {
      Path target = Files.createTempDirectory(temporaryFolder.getRoot().toPath(), "target-");
      Files.writeString(
          legacy.resolve("configuration.txt"), configuration("Custom ship", custom.toString()));

      LegacyDataMigration.migrate(List.of(legacy), target);

      String migrated = Files.readString(target.resolve("configuration.txt"));
      assertTrue(migrated.contains(main.LOGS_DIR_TXT + custom));
      assertFalse(Files.exists(target.resolve("logs/configuration.txt")));
      assertEquals(
          target.resolve("configuration.txt"),
          ConfigurationLoader.load(target.resolve("logs").toString(), target.toString()).source());
    }
  }

  @Test
  public void relativeCustomFolderKeepsItsOriginalLocationAfterMigration() throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.writeString(
        legacy.resolve("configuration.txt"), configuration("Custom ship", "../custom-logs"));

    LegacyDataMigration.migrate(List.of(legacy), target);

    assertTrue(
        Files.readString(target.resolve("configuration.txt"))
            .contains(main.LOGS_DIR_TXT + legacy.resolve("../custom-logs").normalize()));
    assertFalse(Files.exists(target.resolve("logs/configuration.txt")));
  }

  @Test
  public void existingModernRootConfigurationCannotBeSupersededByALegacyLogsCopy()
      throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.createDirectory(legacy.resolve("logs"));
    Files.writeString(
        legacy.resolve("logs/configuration.txt"), configuration("Legacy ship", "logs"));
    String current = configuration("Current ship", target.resolve("logs").toString());
    Files.writeString(target.resolve("configuration.txt"), current);

    LegacyDataMigration.migrate(List.of(legacy), target);

    assertEquals(current, Files.readString(target.resolve("configuration.txt")));
    assertFalse(Files.exists(target.resolve("logs/configuration.txt")));
    assertEquals(
        target.resolve("configuration.txt"),
        ConfigurationLoader.load(target.resolve("logs").toString(), target.toString()).source());
  }

  @Test
  public void existingModernLogsConfigurationAlsoPreventsImportingAnotherConfiguration()
      throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.createDirectory(target.resolve("logs"));
    Files.writeString(legacy.resolve("configuration.txt"), configuration("Legacy ship", "logs"));
    String current = configuration("Current ship", target.resolve("logs").toString());
    Files.writeString(target.resolve("logs/configuration.txt"), current);

    LegacyDataMigration.migrate(List.of(legacy), target);

    assertEquals(current, Files.readString(target.resolve("logs/configuration.txt")));
    assertFalse(Files.exists(target.resolve("configuration.txt")));
  }

  @Test
  public void invalidConfigurationsAreNotCopiedWithTheLogDirectory() throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.createDirectory(legacy.resolve("logs"));
    Files.writeString(legacy.resolve("configuration.txt"), "unknown label : ignored\n");
    Files.writeString(
        legacy.resolve("logs/configuration.txt"), main.RS232_INSTRUMENT_TYPE_TXT + "invalid\n");
    Files.writeString(legacy.resolve("logs/history.txt"), "history\n");

    LegacyDataMigration.migrate(List.of(legacy), target);

    assertFalse(Files.exists(target.resolve("configuration.txt")));
    assertFalse(Files.exists(target.resolve("logs/configuration.txt")));
    assertEquals("history\n", Files.readString(target.resolve("logs/history.txt")));
  }

  @Test
  public void debianLayoutWithoutSavedConfigurationDoesNotInventMigratedSettings()
      throws Exception {
    Path unixBin = temporaryFolder.newFolder("usr-local-bin").toPath();
    Path legacy = unixBin.resolve("turbowin_jws/bin");
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.createDirectories(legacy);

    LegacyDataMigration.migrate(
        LegacyInstallationLocator.find(null, "LINUX", Map.of(), unixBin), target);

    assertFalse(Files.exists(target.resolve("configuration.txt")));
    assertFalse(Files.exists(target.resolve("logs/configuration.txt")));
    assertFalse(Files.exists(legacy.resolve("logs")));
  }

  @Test
  public void discoveredRpmInstallationsFallBackFromAnInvalidNewerVersion() throws Exception {
    Path unixBin = temporaryFolder.newFolder("usr-local-bin").toPath();
    Path working = temporaryFolder.newFolder("elsewhere").toPath();
    Path newer = unixBin.resolve("TurboWin+-4.7.0/turbowin_jws/bin");
    Path older = unixBin.resolve("TurboWin+-4.6.0/turbowin_jws/bin");
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.createDirectories(newer.resolve("logs"));
    Files.createDirectories(older.resolve("logs"));
    Files.writeString(
        newer.resolve("logs/configuration.txt"), main.RS232_INSTRUMENT_TYPE_TXT + "invalid\n");
    Files.writeString(
        older.resolve("configuration.txt"),
        configuration("RPM ship", older.resolve("logs").toString()));
    Files.writeString(
        older.resolve("logs/configuration.txt"),
        configuration("RPM ship", older.resolve("logs").toString()));

    LegacyDataMigration.migrate(
        LegacyInstallationLocator.find(working.toString(), "LINUX", Map.of(), unixBin), target);

    assertMigratedCopies(target, "RPM ship", target.resolve("logs").toString());
  }

  @Test
  public void repeatedMigrationDoesNotReintroduceTheOldLogsFolder() throws Exception {
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Path target = temporaryFolder.newFolder("target").toPath();
    Files.writeString(
        legacy.resolve("configuration.txt"),
        configuration("First ship", legacy.resolve("logs").toString()));
    LegacyDataMigration.migrate(List.of(legacy), target);
    Files.writeString(
        legacy.resolve("configuration.txt"),
        configuration("Later legacy ship", legacy.resolve("logs").toString()));

    LegacyDataMigration.migrate(List.of(legacy), target);

    assertMigratedCopies(target, "First ship", target.resolve("logs").toString());
  }

  private static String configuration(String ship, String logsDirectory) {
    return main.SHIP_NAME_TXT + ship + "\n" + main.LOGS_DIR_TXT + logsDirectory + "\n";
  }

  private static void assertMigratedCopies(Path target, String ship, String logsDirectory)
      throws Exception {
    String data = Files.readString(target.resolve("configuration.txt"));
    assertTrue(data, data.contains(main.SHIP_NAME_TXT + ship));
    assertTrue(data, data.contains(main.LOGS_DIR_TXT + logsDirectory));
    assertEquals(data, Files.readString(target.resolve("logs/configuration.txt")));
    assertTrue(
        ConfigurationLoader.load(target.resolve("logs").toString(), target.toString()).loaded());
  }

  @Test
  public void skipsWhenLegacyAndTargetDirectoriesAreTheSame() throws Exception {
    Path directory = temporaryFolder.newFolder("same-directory").toPath();
    Files.writeString(directory.resolve(main.CONFIGURATION_FILE), "current\n");

    LegacyDataMigration.migrate(List.of(directory), directory);

    assertEquals("current\n", Files.readString(directory.resolve(main.CONFIGURATION_FILE)));
  }
}
