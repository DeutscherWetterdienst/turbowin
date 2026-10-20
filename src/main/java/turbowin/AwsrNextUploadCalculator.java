package turbowin;

/** Calculates the next hour at which an AWSR report is scheduled. */
final class AwsrNextUploadCalculator {

  private AwsrNextUploadCalculator() {}

  static Integer nextUploadHour(int currentHour, int interval) {
    if (interval <= 0) {
      return null;
    }

    // Search the next reporting interval for the next aligned upload hour.
    // 24 hours or higher makes no difference for this computation.
    for (int hour = currentHour + 1; hour < currentHour + 1 + interval; hour++) {
      if (hour % interval == 0) {
        return hour;
      }
    }
    return currentHour + interval;
  }
}
