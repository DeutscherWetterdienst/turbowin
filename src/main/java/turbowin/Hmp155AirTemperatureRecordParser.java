package turbowin;

/**
 * Parses fixed-width HMP155 air-temperature records.
 *
 * <p>Saved HMP155 records are 48 characters wide. In the example below, underscores represent
 * spaces:
 *
 * <pre>___22.98____17.77____14.77____60.09_201909041422</pre>
 *
 * <p>The first value is the air temperature and occupies 8 characters with 3 leading spaces. Each
 * following fixed-width value occupies 9 characters with 4 leading spaces. One space separates the
 * values from the trailing {@code YYYYMMDDHHmm} timestamp, whose final two characters are the
 * minute.
 */
final class Hmp155AirTemperatureRecordParser {

  private static final int RECORD_LENGTH = main.RECORD_LENGTE_HMP155;
  private static final int TIMESTAMP_POSITION = RECORD_LENGTH - 12;
  private static final int MINUTE_POSITION = RECORD_LENGTH - 2;

  private Hmp155AirTemperatureRecordParser() {}

  static Hmp155AirTemperatureRecord parse(String record, String expectedHour) {
    if (record.length() != RECORD_LENGTH) {
      return null;
    }

    String timestamp = record.substring(TIMESTAMP_POSITION, RECORD_LENGTH);
    if (!timestamp.substring(0, 10).equals(expectedHour)) {
      return null;
    }

    int minute;
    try {
      minute = Integer.parseInt(record.substring(MINUTE_POSITION).trim());
    } catch (NumberFormatException exception) {
      return null;
    }
    if (minute < 0 || minute > 59 || minute % 5 != 0) {
      return null;
    }

    String temperature = record.substring(0, 8).trim();
    return new Hmp155AirTemperatureRecord(
        timestamp, minute, temperature, isTemperatureOutsideExpectedRange(temperature));
  }

  private static boolean isTemperatureOutsideExpectedRange(String temperature) {
    try {
      double value = Double.parseDouble(temperature);
      return value < -99.9 || value > 99.9;
    } catch (NumberFormatException exception) {
      return true;
    }
  }
}
