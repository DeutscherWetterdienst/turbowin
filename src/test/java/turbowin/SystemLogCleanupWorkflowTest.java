package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Test;

public class SystemLogCleanupWorkflowTest {

  @Test
  public void deletesTheLogFileForTheRequestedMonth() throws Exception {
    File logsDirectory = Files.createTempDirectory("turbowin-system-logs").toFile();
    File systemLogsDirectory = new File(logsDirectory, "system");
    assertTrue(systemLogsDirectory.mkdir());
    SimpleDateFormat monthlyFormat = new SimpleDateFormat("MMM_yyyy");
    monthlyFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date date = monthlyFormat.parse("JAN_2026");
    File logFile =
        new File(
            SystemLogWriterWorkflow.logFilePath(
                logsDirectory.getPath(), "system", monthlyFormat.format(date)));
    assertTrue(logFile.createNewFile());

    try {
      SystemLogCleanupWorkflow.deleteLogFile(
          logsDirectory.getPath(), "system", monthlyFormat, date);

      assertFalse(logFile.exists());
    } finally {
      logFile.delete();
      systemLogsDirectory.delete();
      logsDirectory.delete();
    }
  }
}
