package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class LogFileCopyWorkflowTest {

  @Test
  public void copiesExistingFile() throws Exception {
    Path directory = Files.createTempDirectory("turbowin-copy-test");
    Path source = directory.resolve("source.log");
    Path destination = directory.resolve("destination.log");
    Files.writeString(source, "observation data");

    assertEquals("OK", LogFileCopyWorkflow.copy(source.toString(), destination.toString()));
    assertTrue(Files.exists(destination));
    assertEquals("observation data", Files.readString(destination));
  }

  @Test
  public void reportsMissingSourceWithoutThrowing() throws Exception {
    Path directory = Files.createTempDirectory("turbowin-copy-test");
    Path destination = directory.resolve("destination.log");

    assertEquals(
        "NOT_OK",
        LogFileCopyWorkflow.copy(
            directory.resolve("missing.log").toString(), destination.toString()));
  }
}
