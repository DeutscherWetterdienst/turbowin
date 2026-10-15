package turbowin;

import static turbowin.main_RS232_RS422.Mintaka_Star_Checksum;

/**
 * Validates and extracts air temperature from historical Mintaka StarX records.
 *
 * <p>The StarX record fields are station pressure (mb), sea-level pressure (mb), three-hour
 * pressure tendency, WMO tendency characteristic code, latitude, longitude, true course, speed
 * (knots), elevation (meters), temperature (degrees Celsius), relative humidity (0–100), wet-bulb
 * temperature (degrees Celsius), dew point (degrees Celsius), and observation age (seconds),
 * followed by {@code *checksum}. Latitude and longitude use {@code ddd mm.mmmm[N|S]} and
 * {@code ddd mm.mmmm[E|W]} respectively. The parser extracts the temperature field and accepts
 * observation ages from 0 through {@link main_RS232_RS422#MAX_AGE_STARX_OBS_DATA} seconds.
 */
final class MintakaStarXAirTemperatureRecordParser {

  private static final int AGE_NOT_OK = 999999; // Sentinel for a blank or invalid age in seconds.

  private MintakaStarXAirTemperatureRecordParser() {}

  static MintakaStarXAirTemperatureRecord parse(String record, String expectedHour) {
    if (countCommas(record) != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX
        || !hasValidChecksum(record)
        || record.length() <= 15) {
      return null;
    }

    int timestampPosition = record.length() - 12;
    String timestamp = record.substring(timestampPosition);
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

    int[] commaPositions = new int[13];
    int previousPosition = -1;
    for (int i = 0; i < commaPositions.length; i++) {
      commaPositions[i] = record.indexOf(',', previousPosition + 1);
      if (commaPositions[i] < 0) {
        return null;
      }
      previousPosition = commaPositions[i];
    }
    int starPosition = record.indexOf('*', commaPositions[12] + 1);
    if (starPosition < 0) {
      return null;
    }

    int observationAge = parseObservationAge(record, commaPositions[12], starPosition);
    if (observationAge < 0 || observationAge > main_RS232_RS422.MAX_AGE_STARX_OBS_DATA) {
      return null;
    }

    // Comma 8 follows elevation; comma 9 follows air temperature (Celsius).
    String airTemperature = record.substring(commaPositions[8] + 1, commaPositions[9]);
    if (airTemperature.isEmpty()) {
      return null;
    }
    return new MintakaStarXAirTemperatureRecord(
        timestamp, minute, validatedTemperature(airTemperature));
  }

  private static int countCommas(String record) {
    return (int) record.chars().filter(character -> character == ',').count();
  }

  private static boolean hasValidChecksum(String record) {
    return Mintaka_Star_Checksum(record)
        .equals(record.substring(record.length() - 14, record.length() - 12));
  }

  private static int parseObservationAge(String record, int commaPosition, int starPosition) {
    // The field after comma 12 is observation age in seconds; '*' starts the checksum.
    String observationAge = record.substring(commaPosition + 1, starPosition);
    if (observationAge.isEmpty() || observationAge.indexOf('*') != -1) {
      return AGE_NOT_OK;
    }
    try {
      return Integer.parseInt(observationAge.trim());
    } catch (NumberFormatException exception) {
      // Preserve the diagnostic emitted by the original StarX workflow for malformed ages.
      System.out.println(
          "--- Read_Sensor_Data_Files_For_Air_Temp_Mintaka_StarX() " + exception);
      return AGE_NOT_OK;
    }
  }

  private static String validatedTemperature(String temperature) {
    try {
      double value = Double.parseDouble(temperature.trim());
      return value >= -50.0 && value <= 60.0 ? temperature : "";
    } catch (NumberFormatException exception) {
      return "";
    }
  }
}
