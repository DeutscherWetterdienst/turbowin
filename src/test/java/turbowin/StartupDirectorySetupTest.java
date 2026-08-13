package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import org.junit.Test;

public class StartupDirectorySetupTest {

  @Test
  public void derivesOfflineLogAndAmverDirectories() {
    StartupDirectorySetup.Directories directories =
        StartupDirectorySetup.directories("/tmp/turbowin-data");

    assertEquals("/tmp/turbowin-data" + File.separator + "logs", directories.logsDirectory);
    assertEquals("/tmp/turbowin-data" + File.separator + "amver", directories.amverDirectory);
  }

  @Test
  public void createsMissingLogsDirectoryDuringInitialization() throws Exception {
    File dataDirectory = Files.createTempDirectory("turbowin-startup").toFile();
    try {
      StartupDirectorySetup.initialize(StartupDirectorySetup.directories(dataDirectory.getPath()));

      assertTrue(new File(dataDirectory, "logs").isDirectory());
    } finally {
      new File(dataDirectory, "logs" + File.separator + "turbowin_system").delete();
      new File(dataDirectory, "logs").delete();
      new File(dataDirectory, "amver").delete();
      dataDirectory.delete();
    }
  }
}
