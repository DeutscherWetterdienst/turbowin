package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import org.junit.Test;

public class ConfigurationWriteWorkflowTest {

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
}
