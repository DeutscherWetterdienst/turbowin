package turbowin;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;

/** Locates the hourly sensor-data file used by sensor workflows. */
final class SensorDataFileLocator {

  private SensorDataFileLocator() {}

  static File locate(Calendar observationTime, String logsDirectory, DateFormat fileDateFormat) {
    Calendar fileTime = (Calendar) observationTime.clone();
    // Keep a two-minute margin so the hourly file has time to be written before it is read.
    fileTime.add(Calendar.MINUTE, -2);
    String datePart = fileDateFormat.format(fileTime.getTime());
    return new File(logsDirectory, "sensor_data_" + datePart + ".txt");
  }
}
