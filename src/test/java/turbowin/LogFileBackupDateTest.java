package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.junit.Test;

public class LogFileBackupDateTest {

  @Test
  public void formatsTheLegacyBackupDate() {
    GregorianCalendar calendar =
        new GregorianCalendar(new SimpleTimeZone(0, "UTC"), Locale.ENGLISH);
    calendar.clear();
    calendar.set(2024, GregorianCalendar.JANUARY, 2);

    assertEquals("January 02, 2024", LogFileBackupDate.format(calendar));
  }
}
