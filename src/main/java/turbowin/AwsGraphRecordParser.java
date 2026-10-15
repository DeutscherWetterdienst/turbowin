package turbowin;

/** Parses one validated AWS record for graph plotting. */
final class AwsGraphRecordParser {

  private AwsGraphRecordParser() {}

  static AwsGraphRecord parse(
      String record, String expectedHour, int primaryComma, int secondaryComma) {
    if (countCommas(record) != main.TOTAL_NUMBER_RECORD_COMMAS) {
      return null;
    }
    if (record.length() <= 18 || !record.startsWith("$PEUMB")) {
      return null;
    }

    int timestampPosition = record.lastIndexOf(",");
    if (timestampPosition <= 0 || timestampPosition + 13 != record.length()) {
      return null;
    }

    String timestamp = record.substring(timestampPosition + 1);
    if (!timestamp.substring(0, 10).equals(expectedHour)) {
      return null;
    }

    int minute;
    try {
      minute = Integer.parseInt(timestamp.substring(10).trim());
    } catch (NumberFormatException exception) {
      return null;
    }
    if (minute < 0 || minute > 59 || minute % 5 != 0) {
      return null;
    }

    String primaryValue = valueAfterComma(record, primaryComma, timestampPosition);
    String secondaryValue = valueAfterComma(record, secondaryComma, timestampPosition);
    if (primaryValue == null && secondaryValue == null) {
      return null;
    }
    return new AwsGraphRecord(timestamp, minute, primaryValue, secondaryValue);
  }

  private static int countCommas(String record) {
    return (int) record.chars().filter(character -> character == ',').count();
  }

  private static String valueAfterComma(String record, int commaNumber, int timestampPosition) {
    if (commaNumber < 0) {
      return null;
    }

    int position = -1;
    for (int currentComma = 1; currentComma <= commaNumber; currentComma++) {
      position = record.indexOf(',', position + 1);
      if (position < 0) {
        return null;
      }
    }

    int nextPosition = record.indexOf(',', position + 1);
    if (nextPosition < 0 || nextPosition > timestampPosition || nextPosition - position < 2) {
      return null;
    }
    return record.substring(position + 1, nextPosition);
  }
}
