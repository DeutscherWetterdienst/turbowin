package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Format101EmailContentResolverTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void resolvesBodyModeFromTheCompressedFile() throws Exception {
    File file = temporaryFolder.newFile("format101.txt");
    Files.writeString(file.toPath(), "compressed-observation\n");

    assertEquals(
        "compressed-observation",
        Format101EmailContentResolver.resolve(main.FORMAT_101_BODY, file));
  }

  @Test
  public void resolvesAttachmentModeToTheManualAttachmentInstruction() throws Exception {
    File file = temporaryFolder.newFile("format101.txt");

    assertEquals(
        "Please attach manually the file: " + file.getPath(),
        Format101EmailContentResolver.resolve(main.FORMAT_101_ATTACHEMENT, file));
  }

  @Test
  public void preservesTheLegacyAttachmentInstructionForAnExistingDirectory() throws Exception {
    File directory = temporaryFolder.newFolder("format101-directory");

    assertEquals(
        "Please attach manually the file: " + directory.getPath(),
        Format101EmailContentResolver.resolve(main.FORMAT_101_ATTACHEMENT, directory));
  }

  @Test
  public void returnsEmptyWhenTheRequestedContentIsUnavailable() throws Exception {
    File missingFile = new File(temporaryFolder.getRoot(), "missing-format101.txt");

    assertEquals("", Format101EmailContentResolver.resolve(main.FORMAT_101_BODY, missingFile));
    assertEquals(
        "", Format101EmailContentResolver.resolve(main.FORMAT_101_ATTACHEMENT, missingFile));
  }
}
