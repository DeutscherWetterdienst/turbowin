package turbowin;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigurationLoaderTest {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void interruptedReadDiscardsPartialBufferBeforeTryingBackup() throws Exception {
    Path logs = temporaryFolder.newFolder("logs").toPath();
    Path data = temporaryFolder.newFolder("data").toPath();
    Files.createFile(logs.resolve(main.CONFIGURATION_FILE));
    Files.writeString(data.resolve(main.CONFIGURATION_FILE), main.WIND_SOURCE_TXT + "backup\n");
    ConfigurationLoader.Result result =
        ConfigurationLoader.load(
            logs.toString(),
            data.toString(),
            (file, lines, maximum) -> {
              if (file.toPath().getParent().equals(logs)) {
                lines[0] = main.SHIP_NAME_TXT + "partial";
                throw new IOException("interrupted read");
              }
              ConfigurationFileStore.read(file, lines, maximum);
            });
    assertEquals(data.resolve(main.CONFIGURATION_FILE), result.source());
    assertEquals(main.WIND_SOURCE_TXT + "backup", result.lines()[0]);
    assertEquals("", result.lines()[1]);
    assertTrue(result.messages().toString().contains("unable to read"));
  }

  @Test
  public void directoryInsteadOfFileFallsBackToBackup() throws Exception {
    Path logs = temporaryFolder.newFolder("logs").toPath();
    Path data = temporaryFolder.newFolder("data").toPath();
    Files.createDirectory(logs.resolve(main.CONFIGURATION_FILE));
    Files.writeString(data.resolve(main.CONFIGURATION_FILE), main.SHIP_NAME_TXT + "backup\n");
    ConfigurationLoader.Result result = ConfigurationLoader.load(logs.toString(), data.toString());
    assertEquals(data.resolve(main.CONFIGURATION_FILE), result.source());
    assertTrue(result.messages().toString().contains("unable to read"));
  }

  @Test
  public void missingEmptyAndUnknownOnlyPreferredCopiesFallBack() throws Exception {
    Path logs = temporaryFolder.newFolder("logs").toPath();
    Path data = temporaryFolder.newFolder("data").toPath();
    Files.writeString(data.resolve(main.CONFIGURATION_FILE), main.SHIP_NAME_TXT + "backup\n");
    assertEquals(
        data.resolve(main.CONFIGURATION_FILE),
        ConfigurationLoader.load(logs.toString(), data.toString()).source());
    for (String content : new String[] {"", "unknown setting : ignored\n"}) {
      Files.writeString(logs.resolve(main.CONFIGURATION_FILE), content);
      ConfigurationLoader.Result result =
          ConfigurationLoader.load(logs.toString(), data.toString());
      assertEquals(data.resolve(main.CONFIGURATION_FILE), result.source());
      assertTrue(result.messages().toString().contains("no recognized settings"));
    }
  }

  @Test
  public void validPreferredCopyDoesNotReadBackup() throws Exception {
    Path logs = temporaryFolder.newFolder("logs").toPath();
    Path data = temporaryFolder.newFolder("data").toPath();
    Files.writeString(logs.resolve(main.CONFIGURATION_FILE), main.SHIP_NAME_TXT + "preferred\n");
    Files.createFile(data.resolve(main.CONFIGURATION_FILE));
    AtomicInteger reads = new AtomicInteger();
    ConfigurationLoader.Result result =
        ConfigurationLoader.load(
            logs.toString(),
            data.toString(),
            (file, lines, maximum) -> {
              reads.incrementAndGet();
              assertEquals(logs, file.toPath().getParent());
              ConfigurationFileStore.read(file, lines, maximum);
            });
    assertTrue(result.loaded());
    assertEquals(1, reads.get());
  }

  @Test
  public void rejectionMessagesDoNotExposeInvalidValues() throws Exception {
    Path logs = temporaryFolder.newFolder("logs").toPath();
    Files.writeString(
        logs.resolve(main.CONFIGURATION_FILE),
        main.RS232_INSTRUMENT_TYPE_TXT + "secret-invalid-value\n");
    ConfigurationLoader.Result result = ConfigurationLoader.load(logs.toString(), logs.toString());
    assertFalse(result.loaded());
    assertEquals(2, result.messages().size());
    assertTrue(result.messages().toString().contains("line 1"));
    assertFalse(result.messages().toString().contains("secret-invalid-value"));
  }
}
