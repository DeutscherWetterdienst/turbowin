package turbowin;

/** Parsed values needed to plot one AWS graph record. */
final class AwsGraphRecord {

  final String timestamp;
  final int minute;
  final String primaryValue;
  final String secondaryValue;

  AwsGraphRecord(String timestamp, int minute, String primaryValue, String secondaryValue) {
    this.timestamp = timestamp;
    this.minute = minute;
    this.primaryValue = primaryValue;
    this.secondaryValue = secondaryValue;
  }
}
