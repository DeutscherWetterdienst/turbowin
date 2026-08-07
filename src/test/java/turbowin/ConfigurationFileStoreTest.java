package turbowin;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
}
