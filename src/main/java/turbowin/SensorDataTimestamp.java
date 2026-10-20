package turbowin;

import java.util.Calendar;

/** Applies the sensor-record timestamp correction used near a minute boundary. */
final class SensorDataTimestamp {

  private SensorDataTimestamp() {}

  static void roundToNextMinuteWhenLate(Calendar timestamp) {
    switch (timestamp.get(Calendar.SECOND)) {
      case 59:
        timestamp.add(Calendar.SECOND, 1);
        break;
      case 58:
        timestamp.add(Calendar.SECOND, 2);
        break;
      case 57:
        timestamp.add(Calendar.SECOND, 3);
        break;
      default:
        break;
    }
  }
}
