package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ObservationFileWriterTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void writesTheObservationWithoutAddingANewline() throws Exception {
    File file = temporaryFolder.newFile("observation.txt");

    assertTrue(ObservationFileWriter.write(file, "observation-content"));
    assertEquals("observation-content", Files.readString(file.toPath()));
  }

  @Test
  public void reportsFailureWhenTheTargetCannotBeWritten() throws Exception {
    File directory = temporaryFolder.newFolder("target-directory");

    assertFalse(ObservationFileWriter.write(directory, "observation-content"));
  }
}
