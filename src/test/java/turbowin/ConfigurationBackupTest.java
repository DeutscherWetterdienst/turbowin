package turbowin;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigurationBackupTest {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private Path active() {
    return temporaryFolder.getRoot().toPath().resolve(main.CONFIGURATION_FILE);
  }

  private Path backup() {
    return active().resolveSibling(ConfigurationFileStore.BACKUP_FILE);
  }

  private void save(String ship) throws IOException {
    ConfigurationFileStore.save(active().toFile(), new String[] {main.SHIP_NAME_TXT + ship}, 1);
  }

  @Test
  public void retainsPreviousDistinctVersionAcrossRepeatedSaves() throws Exception {
    save("First");
    assertFalse(Files.exists(backup()));
    String first = Files.readString(active());
    save("Second");
    assertEquals(first, Files.readString(backup()));
    save("Second");
    assertEquals(first, Files.readString(backup()));
    String second = Files.readString(active());
    save("Third");
    assertEquals(second, Files.readString(backup()));
    assertEquals(active(), ConfigurationLoader.load(active().getParent().toString()).source());
  }

  @Test
  public void backupFailureLeavesActiveUntouched() throws Exception {
    save("First");
    String first = Files.readString(active());
    Files.createDirectory(backup());
    Files.writeString(backup().resolve("block-replacement"), "keep");
    assertThrows(IOException.class, () -> save("Second"));
    assertEquals(first, Files.readString(active()));
  }

  @Test
  public void rejectsInvalidNewSettingsBeforeChangingEitherFile() throws Exception {
    save("First");
    save("Second");
    String current = Files.readString(active());
    String previous = Files.readString(backup());
    assertThrows(
        IllegalArgumentException.class,
        () ->
            ConfigurationFileStore.save(
                active().toFile(), new String[] {main.RS232_GPS_TYPE_TXT + "invalid"}, 1));
    assertEquals(current, Files.readString(active()));
    assertEquals(previous, Files.readString(backup()));
  }

  @Test
  public void recoversWithoutOverwritingDamagedContentsAndPreservesThemOnRepair() throws Exception {
    save("First");
    save("Second");
    String previous = Files.readString(backup());
    String damaged = main.SHIP_NAME_TXT + "Rejected\n" + main.RS232_GPS_TYPE_TXT + "invalid\n";
    Files.writeString(active(), damaged);
    ConfigurationLoader.Result recovered =
        ConfigurationLoader.load(active().getParent().toString());
    assertTrue(recovered.recovered());
    assertEquals(backup(), recovered.source());
    assertEquals(damaged, Files.readString(active()));
    save("Recovered and edited");
    assertEquals(previous, Files.readString(backup()));
    try (var files = Files.list(active().getParent())) {
      List<Path> preserved = files.filter(p -> p.toString().endsWith(".damaged")).toList();
      assertEquals(1, preserved.size());
      assertEquals(damaged, Files.readString(preserved.get(0)));
    }
    assertFalse(ConfigurationLoader.load(active().getParent().toString()).recovered());
  }

  @Test
  public void missingActiveRecoversBackupAndMigrationDoesNotReplaceIt() throws Exception {
    save("First");
    save("Second");
    String previous = Files.readString(backup());
    Files.delete(active());
    Path legacy = temporaryFolder.newFolder("legacy").toPath();
    Files.writeString(legacy.resolve(main.CONFIGURATION_FILE), main.SHIP_NAME_TXT + "Legacy\n");
    LegacyDataMigration.migrate(List.of(legacy), active().getParent());
    assertFalse(Files.exists(active()));
    assertTrue(ConfigurationLoader.load(active().getParent().toString()).recovered());
    save("Recovered");
    assertEquals(previous, Files.readString(backup()));
  }

  @Test
  public void backupKeepsRestrictedActivePermissions() throws Exception {
    org.junit.Assume.assumeTrue(
        Files.getFileStore(active().getParent()).supportsFileAttributeView("posix"));
    save("First");
    var permissions = java.nio.file.attribute.PosixFilePermissions.fromString("rw-------");
    Files.setPosixFilePermissions(active(), permissions);
    save("Second");
    assertEquals(permissions, Files.getPosixFilePermissions(backup()));
  }

  @Test
  public void invalidBackupIsRejectedWithoutApplyingSettings() throws Exception {
    Files.writeString(backup(), main.RS232_GPS_TYPE_TXT + "invalid\n");
    assertFalse(ConfigurationLoader.load(active().getParent().toString()).loaded());
  }
}
