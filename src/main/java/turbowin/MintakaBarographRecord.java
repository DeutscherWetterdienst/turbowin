package turbowin;

/** A validated pressure value for a Mintaka barograph graph. */
final class MintakaBarographRecord {

  final String timestamp;
  final int minute;
  final String pressure;

  MintakaBarographRecord(String timestamp, int minute, String pressure) {
    this.timestamp = timestamp;
    this.minute = minute;
    this.pressure = pressure;
  }
}
