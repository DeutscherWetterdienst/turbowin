package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Test;

public class ImmtLogReaderWorkflowTest {

  @Test
  public void readsTheLastRecord() throws IOException {
    File file = Files.createTempFile("turbowin-immt", ".log").toFile();
    try {
      Files.write(file.toPath(), java.util.Arrays.asList("first", "last"));

      assertEquals("last", ImmtLogReaderWorkflow.readLastRecord(file.getPath()));
    } finally {
      file.delete();
    }
  }

  @Test
  public void returnsEmptyForMissingOrEmptyFiles() throws IOException {
    File emptyFile = Files.createTempFile("turbowin-immt-empty", ".log").toFile();
    try {
      assertEquals("", ImmtLogReaderWorkflow.readLastRecord(emptyFile.getPath()));
      assertEquals(
          "",
          ImmtLogReaderWorkflow.readLastRecord(
              emptyFile.getParent() + File.separator + "missing-immt.log"));
    } finally {
      emptyFile.delete();
    }
  }
}
