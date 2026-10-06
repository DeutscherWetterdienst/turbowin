package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LogFilesMoveValidationTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void rejectsMissingImmtLog() {
    assertFalse(
        LogFilesMoveValidation.hasUsableImmtLog(
            new File(temporaryFolder.getRoot(), "immt.log").getPath()));
  }

  @Test
  public void rejectsImmtLogOfTenBytesOrLess() throws IOException {
    File immtLog = temporaryFolder.newFile("immt.log");
    writeBytes(immtLog, 10);

    assertFalse(LogFilesMoveValidation.hasUsableImmtLog(immtLog.getPath()));
  }

  @Test
  public void acceptsImmtLogLargerThanTenBytes() throws IOException {
    File immtLog = temporaryFolder.newFile("immt.log");
    writeBytes(immtLog, 11);

    assertTrue(LogFilesMoveValidation.hasUsableImmtLog(immtLog.getPath()));
  }

  private static void writeBytes(File file, int count) throws IOException {
    try (FileOutputStream output = new FileOutputStream(file)) {
      output.write(new byte[count]);
    }
  }
}
