package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LogFilesDestinationValidationTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void acceptsAnExistingDirectory() {
    assertTrue(LogFilesDestinationValidation.ensureDirectory(temporaryFolder.getRoot().getPath()));
  }

  @Test
  public void createsAMissingDirectory() {
    File destination = new File(temporaryFolder.getRoot(), "destination");

    assertTrue(LogFilesDestinationValidation.ensureDirectory(destination.getPath()));
    assertTrue(destination.isDirectory());
  }

  @Test
  public void rejectsAPathThatCannotBeCreated() throws IOException {
    File parentFile = temporaryFolder.newFile("parent");
    File destination = new File(parentFile, "destination");

    assertFalse(LogFilesDestinationValidation.ensureDirectory(destination.getPath()));
  }

  @Test
  public void identifiesTheSourceAndDestinationAsTheSame() {
    assertTrue(LogFilesDestinationValidation.isSameDirectory("logs", "logs"));
    assertFalse(LogFilesDestinationValidation.isSameDirectory("logs", "other"));
  }
}
