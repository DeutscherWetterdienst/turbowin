package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.Test;

public class SystemLogWriterWorkflowTest {

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
