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
}
