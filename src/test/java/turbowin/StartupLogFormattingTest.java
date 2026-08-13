package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.TimeZone;
import org.junit.Test;

public class StartupLogFormattingTest {

  @Test
  public void createsMonthlyLogFormatInUtc() {
    assertEquals("MMM_yyyy", StartupLogFormatting.monthlyLogFormat().toPattern());
    assertEquals(
        TimeZone.getTimeZone("UTC"), StartupLogFormatting.monthlyLogFormat().getTimeZone());
  }

  @Test
  public void createsMessageLogFormatInUtc() {
    assertEquals("dd-MMM-yyyy HH:mm:ss", StartupLogFormatting.messageLogFormat().toPattern());
    assertEquals(
        TimeZone.getTimeZone("UTC"), StartupLogFormatting.messageLogFormat().getTimeZone());
  }
}
