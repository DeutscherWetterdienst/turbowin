package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SystemLogWriterWorkflowTest {
  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void createsSystemLogDirectoryForConfiguredFolderBeforeFirstWrite() throws Exception {
    Path logs = folder.newFolder("custom-logs").toPath();
    SystemLogWriterWorkflow.append(logs.toString(), "Nov_2026", "first entry");
    Path file = logs.resolve(main.TURBOWIN_SYSTEM_LOGS_DIR).resolve("turbowin_system_Nov_2026.txt");
    assertEquals("first entry" + System.lineSeparator(), Files.readString(file));
  }

  @Test
  public void doesNotCreateAMissingConfiguredLogsRoot() throws Exception {
    Path missing = folder.getRoot().toPath().resolve("missing");
    assertThrows(
        IOException.class,
        () -> SystemLogWriterWorkflow.append(missing.toString(), "Nov_2026", "entry"));
    assertFalse(Files.exists(missing));
  }

  @Test
  public void createsMonthlyLogFileName() {
    assertEquals("turbowin_system_JAN_2026.txt", SystemLogWriterWorkflow.logFileName("JAN_2026"));
  }

  @Test
  public void createsSystemLogPath() {
    assertEquals(
        "logs" + File.separator + "system" + File.separator + "turbowin_system_JAN_2026.txt",
        SystemLogWriterWorkflow.logFilePath("logs", "system", "JAN_2026"));
  }

  @Test
  public void createsUtcLogLine() {
    assertEquals(
        "09-Jan-2026 12:23:33 UTC startup complete",
        SystemLogWriterWorkflow.logLine("09-Jan-2026 12:23:33", "startup complete"));
  }
}
