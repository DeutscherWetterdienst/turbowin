package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.Test;

public class MintakaDuoSensorDataFileLocatorTest {

  @Test
  public void usesTwoMinutesEarlierHourlySensorDataFile() {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    Calendar observationTime = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    observationTime.set(2015, Calendar.FEBRUARY, 4, 0, 1, 0);
    observationTime.set(Calendar.MILLISECOND, 0);

    File result = MintakaDuoSensorDataFileLocator.locate(observationTime, "/logs", format);

    assertEquals(new File("/logs", "sensor_data_2015020323.txt"), result);
  }
}
