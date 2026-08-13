package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Test;

public class SystemLogEmailWorkflowTest {

  @Test
  public void selectsCurrentAndPreviousMonthlyLogs() {
    SimpleDateFormat monthlyFormat = new SimpleDateFormat("MMM_yyyy");
    monthlyFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.set(2026, Calendar.JANUARY, 15, 12, 0, 0);

    File[] files = SystemLogEmailWorkflow.recentLogFiles("logs", "system", monthlyFormat, calendar);

    assertEquals(
        "logs" + File.separator + "system" + File.separator + "turbowin_system_Jan_2026.txt",
        files[0].getPath());
    assertEquals(
        "logs" + File.separator + "system" + File.separator + "turbowin_system_Dec_2025.txt",
        files[1].getPath());
  }
}
