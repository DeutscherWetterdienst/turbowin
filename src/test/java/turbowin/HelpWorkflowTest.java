package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Test;

public class HelpWorkflowTest {

  @Test
  public void prefersExistingLocalHelpFile() throws IOException {
    File root = Files.createTempDirectory("turbowin-help").toFile();
    File applicationDirectory = new File(root, "app");
    File helpFile = new File(new File(root, "runtime"), "help/wind.pdf");
    try {
      applicationDirectory.mkdirs();
      helpFile.getParentFile().mkdirs();
      helpFile.createNewFile();

      assertEquals(
          new File(applicationDirectory, "../runtime/help/wind.pdf").getPath(),
          HelpWorkflow.resolveHelpLocation(
              applicationDirectory.getPath(), "help", "wind.pdf", "https://example.test/"));
    } finally {
      helpFile.delete();
      helpFile.getParentFile().delete();
      new File(root, "runtime").delete();
      applicationDirectory.delete();
      root.delete();
    }
  }

  @Test
  public void fallsBackToRemoteHelpUrl() {
    assertEquals(
        "https://example.test/wind.pdf",
        HelpWorkflow.resolveHelpLocation(
            "/missing/app", "help", "wind.pdf", "https://example.test/"));
  }

  @Test
  public void returnsNullForInvalidRemoteUrl() {
    assertNull(
        HelpWorkflow.resolveHelpLocation("/missing/app", "help", "wind.pdf", "http://[invalid/"));
  }
}
