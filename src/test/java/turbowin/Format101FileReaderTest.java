package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Format101FileReaderTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private PrintStream originalStandardOut;

  @Before
  public void saveStandardOut() {
    originalStandardOut = System.out;
  }

  @After
  public void restoreStandardOut() {
    System.setOut(originalStandardOut);
  }

  @Test
  public void readsOnlyTheFirstLine() throws Exception {
    File file = temporaryFolder.newFile("format101.txt");
    Files.writeString(file.toPath(), "first-line\nsecond-line\n");

    assertEquals("first-line", Format101FileReader.readFirstLine(file));
  }

  @Test
  public void returnsEmptyStringForEmptyOrMissingFiles() throws Exception {
    File emptyFile = temporaryFolder.newFile("empty-format101.txt");
    File missingFile = new File(temporaryFolder.getRoot(), "missing-format101.txt");

    assertEquals("", Format101FileReader.readFirstLine(emptyFile));
    assertEquals("", Format101FileReader.readFirstLine(missingFile));
  }

  @Test
  public void logsDifferentDiagnosticsForEmptyAndUnreadableFiles() throws Exception {
    File emptyFile = temporaryFolder.newFile("empty-format101.txt");
    File missingFile = new File(temporaryFolder.getRoot(), "missing-format101.txt");
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Format101FileReader.readFirstLine(emptyFile);
    String emptyDiagnostic = output.toString();
    output.reset();
    Format101FileReader.readFirstLine(missingFile);
    String missingDiagnostic = output.toString();

    assertTrue(emptyDiagnostic.contains("empty file: " + emptyFile.getPath()));
    assertTrue(missingDiagnostic.contains("error opening file: " + missingFile.getPath()));
  }
}
