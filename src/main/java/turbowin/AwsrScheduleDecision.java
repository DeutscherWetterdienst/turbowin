package turbowin;

/** Contains the pure timing decisions used by the AWSR upload scheduler. */
final class AwsrScheduleDecision {

  private AwsrScheduleDecision() {}

  static boolean isScheduledUpload(int hour, int minute, int interval) {
    return minute == 0 && hour % interval == 0;
  }

  static boolean isRetryUpload(
      int responseCode, int noInternetCode, int hour, int minute, int interval, int retryMinutes) {
    return responseCode == noInternetCode && minute == retryMinutes && hour % interval == 0;
  }

  static boolean isRetryPending(
      int responseCode, int noInternetCode, int hour, int minute, int interval, int retryMinutes) {
    return responseCode == noInternetCode && minute < retryMinutes && hour % interval == 0;
  }
}
