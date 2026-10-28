package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import org.junit.Test;

public class ConfigurationPersistenceWorkflowTest {

  @Test
  public void prefersLogsDirectoryForConfiguration() {
    assertEquals("logs", ConfigurationPersistenceWorkflow.configurationDirectory("logs", "data"));
  }

  @Test
  public void fallsBackToDataDirectoryWhenLogsDirectoryIsEmpty() {
    assertEquals("data", ConfigurationPersistenceWorkflow.configurationDirectory("", "data"));
    assertEquals("data", ConfigurationPersistenceWorkflow.configurationDirectory(null, "data"));
  }

  @Test
  public void returnsEmptyDirectoryWhenNoConfigurationDirectoryExists() {
    assertEquals("", ConfigurationPersistenceWorkflow.configurationDirectory(null, ""));
  }

  @Test
  public void readsDataConfigurationWhenLogsDirectoryHasNoConfiguration() throws Exception {
    File logs = Files.createTempDirectory("turbowin-logs").toFile();
    File data = Files.createTempDirectory("turbowin-data").toFile();
    try {
      Files.writeString(new File(data, main.CONFIGURATION_FILE).toPath(), "old settings\n");

      assertEquals(
          data.getPath(),
          ConfigurationReadWorkflow.configurationDirectoryForRead(logs.getPath(), data.getPath()));
    } finally {
      new File(data, main.CONFIGURATION_FILE).delete();
      logs.delete();
      data.delete();
    }
  }

  @Test
  public void readClearsStaleDirectoryWhenNoDirectoryIsConfigured() {
    String previousLogsDirectory = main.logs_dir;
    String previousDataDirectory = main.data_dir;
    String previousHelpDirectory = main.hulp_dir;
    try {
      main.logs_dir = null;
      main.data_dir = null;
      main.hulp_dir = "stale";

      ConfigurationReadWorkflow.read(null);

      assertEquals("", main.hulp_dir);
    } finally {
      main.logs_dir = previousLogsDirectory;
      main.data_dir = previousDataDirectory;
      main.hulp_dir = previousHelpDirectory;
    }
  }
}
