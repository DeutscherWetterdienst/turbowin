package turbowin;

import static org.junit.Assert.assertEquals;

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
