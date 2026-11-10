package turbowin;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigurationFileStoreTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void writesOnlyNonEmptyConfigurationLinesAndReadsThemBack() throws Exception {
    File file = temporaryFolder.newFile("configuration.txt");
    String[] lines = {"first", "", null, "last"};

    ConfigurationFileStore.write(file, lines, lines.length);

    assertEquals(
        "first" + System.lineSeparator() + "last" + System.lineSeparator(),
        Files.readString(file.toPath()));

    String[] loaded = {"stale", "stale", "stale", "stale"};
    ConfigurationFileStore.read(file, loaded, loaded.length);

    assertArrayEquals(new String[] {"first", "last", "", ""}, loaded);
  }

  @Test
  public void readingClearsUnusedEntriesAtTheProductionArraySize() throws Exception {
    Path file = temporaryFolder.newFile("short-configuration.txt").toPath();
    Files.writeString(file, "one\ntwo\n");
    String[] loaded = new String[main.MAX_AANTAL_CONFIGURATIEREGELS];
    Arrays.fill(loaded, "stale");

    ConfigurationFileStore.read(file.toFile(), loaded, main.MAX_AANTAL_CONFIGURATIEREGELS);

    assertEquals("one", loaded[0]);
    assertEquals("two", loaded[1]);
    assertEquals("", loaded[main.MAX_AANTAL_CONFIGURATIEREGELS - 1]);
  }

  @Test
  public void readingTruncatesAtTheProductionArraySize() throws Exception {
    Path file = temporaryFolder.newFile("long-configuration.txt").toPath();
    StringBuilder contents = new StringBuilder();
    for (int i = 0; i <= main.MAX_AANTAL_CONFIGURATIEREGELS; i++) {
      contents.append("line-").append(i).append('\n');
    }
    Files.writeString(file, contents.toString());
    String[] loaded = new String[main.MAX_AANTAL_CONFIGURATIEREGELS];

    ConfigurationFileStore.read(file.toFile(), loaded, main.MAX_AANTAL_CONFIGURATIEREGELS);

    assertEquals("line-0", loaded[0]);
    assertEquals("line-99", loaded[main.MAX_AANTAL_CONFIGURATIEREGELS - 1]);
  }

  @Test
  public void failedSerializationPreservesPreviousFileAndRemovesTemporaryFile() throws Exception {
    Path directory = temporaryFolder.newFolder("serialization-failure").toPath();
    Path file = directory.resolve("configuration.txt");
    Files.writeString(file, "previous settings\n");

    // Simulate a failure after the writer has already emitted some configuration lines.
    assertThrows(
        IndexOutOfBoundsException.class,
        () -> ConfigurationFileStore.write(file.toFile(), new String[] {"partial settings"}, 2));

    assertEquals("previous settings\n", Files.readString(file));
    try (var entries = Files.list(directory)) {
      assertEquals(Arrays.asList(file), entries.toList());
    }
  }

  @Test
  public void ioFailureAfterWritingPartialContentsPreservesPreviousFile() throws Exception {
    Path directory = temporaryFolder.newFolder("write-failure").toPath();
    Path file = directory.resolve("configuration.txt");
    Files.writeString(file, "previous settings\n");
    IOException failure = new IOException("simulated disk full");

    IOException thrown =
        assertThrows(
            IOException.class,
            () ->
                ConfigurationFileStore.writeAtomically(
                    file.toFile(),
                    out -> {
                      out.write("partial replacement settings\n");
                      out.flush();
                      assertEquals("previous settings\n", Files.readString(file));
                      throw failure;
                    }));

    assertSame(failure, thrown);
    assertEquals("previous settings\n", Files.readString(file));
    try (var entries = Files.list(directory)) {
      assertEquals(Arrays.asList(file), entries.toList());
    }
  }

  @Test
  public void failedReplacementPreservesDestinationAndRemovesTemporaryFile() throws Exception {
    Path directory = temporaryFolder.newFolder("replacement-failure").toPath();
    Path destination = Files.createDirectory(directory.resolve("configuration.txt"));
    Path existing = destination.resolve("existing.txt");
    Files.writeString(existing, "keep this content\n");

    assertThrows(
        IOException.class,
        () -> ConfigurationFileStore.write(destination.toFile(), new String[] {"new settings"}, 1));

    assertEquals("keep this content\n", Files.readString(existing));
    try (var entries = Files.list(directory)) {
      assertEquals(Arrays.asList(destination), entries.toList());
    }
  }

  @Test
  public void replacementPreservesExistingPosixAccessMode() throws Exception {
    Path file = temporaryFolder.newFile("permissions.txt").toPath();
    Assume.assumeTrue(Files.getFileStore(file).supportsFileAttributeView("posix"));
    var permissions = PosixFilePermissions.fromString("rw-rw----");
    Files.setPosixFilePermissions(file, permissions);

    ConfigurationFileStore.write(file.toFile(), new String[] {"new settings"}, 1);

    assertEquals(permissions, Files.getPosixFilePermissions(file));
    assertEquals("new settings" + System.lineSeparator(), Files.readString(file));
  }

  @Test
  public void newFileUsesRegularFilePermissionsInsteadOfTemporaryFilePermissions()
      throws Exception {
    Path directory = temporaryFolder.newFolder("new-file-permissions").toPath();
    Assume.assumeTrue(Files.getFileStore(directory).supportsFileAttributeView("posix"));
    Path reference = directory.resolve("regular.txt");
    Files.writeString(reference, "reference");
    Path file = directory.resolve("configuration.txt");

    ConfigurationFileStore.write(file.toFile(), new String[] {"new settings"}, 1);

    assertEquals(Files.getPosixFilePermissions(reference), Files.getPosixFilePermissions(file));
  }

  @Test
  public void replacementFollowsExistingConfigurationSymlink() throws Exception {
    Path directory = temporaryFolder.newFolder("symlink").toPath();
    Path target = directory.resolve("actual-settings.txt");
    Files.writeString(target, "previous settings\n");
    Path link = directory.resolve("configuration.txt");
    try {
      Files.createSymbolicLink(link, target.getFileName());
    } catch (UnsupportedOperationException | IOException ex) {
      Assume.assumeNoException("Symbolic links are unavailable", ex);
    }

    ConfigurationFileStore.write(link.toFile(), new String[] {"new settings"}, 1);

    assertTrue(Files.isSymbolicLink(link));
    assertEquals("new settings" + System.lineSeparator(), Files.readString(target));
    assertEquals(Files.readString(target), Files.readString(link));
  }
}
