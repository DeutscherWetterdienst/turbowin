package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Test;

public class StartupEnvironmentTest {

  @Test
  public void selectsPlatformDataDirectories() {
    assertEquals(
        "C:" + File.separator + "ProgramData" + File.separator + "TurboWinPlus",
        StartupEnvironment.dataDirectory("WINDOWS"));
    assertEquals(
        File.separator + "opt" + File.separator + "turbowinplus" + File.separator + "data",
        StartupEnvironment.dataDirectory("LINUX"));
  }

  @Test
  public void detectsOfflineMarkerFiles() throws IOException {
    File directory = Files.createTempDirectory("turbowin-startup").toFile();
    File jnlpFile = new File(directory, "offline.jnlp");
    File launcherFile = new File(directory, "launcher");
    try {
      jnlpFile.createNewFile();
      launcherFile.createNewFile();

      StartupEnvironment.OfflineMode mode =
          StartupEnvironment.detectOfflineMode(
              directory.getPath(), "offline.jnlp", "offline.cmd", "launcher", "launcher-linux");

      assertTrue(mode.offline);
      assertTrue(mode.viaJnlp);
      assertTrue(mode.viaCommandLine);
    } finally {
      jnlpFile.delete();
      launcherFile.delete();
      directory.delete();
    }
  }

  @Test
  public void defaultsToCommandLineOfflineModeWithoutMarkers() throws IOException {
    File directory = Files.createTempDirectory("turbowin-startup").toFile();
    try {
      StartupEnvironment.OfflineMode mode =
          StartupEnvironment.detectOfflineMode(
              directory.getPath(), "offline.jnlp", "offline.cmd", "launcher", "launcher-linux");

      assertTrue(mode.offline);
      assertEquals(false, mode.viaJnlp);
      assertTrue(mode.viaCommandLine);
    } finally {
      directory.delete();
    }
  }
}
