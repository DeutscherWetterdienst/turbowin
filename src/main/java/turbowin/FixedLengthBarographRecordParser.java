package turbowin;

/** Parses fixed-width PTB220 and PTB330 barograph records. */
final class FixedLengthBarographRecordParser {

  private FixedLengthBarographRecordParser() {}

  static FixedLengthBarographRecord parse(
      String record,
      int recordLength,
      int timestampPosition,
      int minutePosition,
      int pressurePosition,
      String expectedHour) {
    if (record.length() != recordLength) {
      return null;
    }

    String timestamp = record.substring(timestampPosition, timestampPosition + 12);
    if (!timestamp.substring(0, 10).equals(expectedHour)) {
      return null;
    }

    int minute;
    try {
      minute = Integer.parseInt(record.substring(minutePosition, minutePosition + 2).trim());
    } catch (NumberFormatException exception) {
      return null;
    }
    if (minute < 0 || minute > 59 || minute % 5 != 0) {
      return null;
    }

    String pressure = record.substring(pressurePosition, pressurePosition + 7);
    return new FixedLengthBarographRecord(
        timestamp, minute, pressure.trim(), isPressureOutsideExpectedRange(pressure));
  }

  private static boolean isPressureOutsideExpectedRange(String pressure) {
    try {
      double value = Double.parseDouble(pressure);
      return value < 900.0 || value > 1100.0;
    } catch (NumberFormatException exception) {
      return true;
    }
  }
}
