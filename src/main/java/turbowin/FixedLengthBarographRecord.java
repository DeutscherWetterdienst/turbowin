package turbowin;

/** A validated fixed-width barograph record. */
final class FixedLengthBarographRecord {

  final String timestamp;
  final int minute;
  final String pressure;
  final boolean pressureOutsideExpectedRange;

  FixedLengthBarographRecord(
      String timestamp, int minute, String pressure, boolean pressureOutsideExpectedRange) {
    this.timestamp = timestamp;
    this.minute = minute;
    this.pressure = pressure;
    this.pressureOutsideExpectedRange = pressureOutsideExpectedRange;
  }
}
