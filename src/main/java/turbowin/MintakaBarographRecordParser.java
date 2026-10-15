package turbowin;

import static turbowin.main_RS232_RS422.Mintaka_Star_Checksum;

/**
 * Validates and extracts pressure values from historical Mintaka graph records.
 *
 * <p>The connection modes identify the record formats: 4 is Mintaka Duo, 5 is Mintaka Star over
 * USB/serial, 6 is Mintaka Star over WiFi/LAN, 7 is Mintaka StarX over USB/serial, and 8 is
 * Mintaka StarX over WiFi/LAN. The corresponding records contain 3, 8, and 13 commas.
 *
 * <p>StarX records are laid out as MSL pressure, local pressure, acceleration, latitude, longitude,
 * course, speed, elevation, air temperature, relative humidity, wet-bulb temperature, dew-point
 * temperature, and observation age, followed by {@code *checksumYYYYMMDDHHmm}. The graph pressure
 * is the local-pressure field (between comma positions 0 and 1). Observation age is in seconds and
 * must be between 0 and {@link main_RS232_RS422#MAX_AGE_STARX_OBS_DATA}; blank or invalid ages are
 * rejected. Pressure outside 900.0–1100.0 hPa is cleared to avoid spikes in the graph.
 */
final class MintakaBarographRecordParser {

  private static final int AGE_NOT_OK = 999999; // Sentinel for a blank or invalid age in seconds.

  private MintakaBarographRecordParser() {}

  static MintakaBarographRecord parse(String record, int connectionMode, String expectedHour) {
    int expectedCommas = expectedCommaCount(connectionMode);
    if (expectedCommas < 0 || countCommas(record) != expectedCommas) {
      return null;
    }

    boolean starX = connectionMode == 7 || connectionMode == 8;
    if (starX && !hasValidChecksum(record)) {
      return null;
    }
    if (record.length() <= 15) {
      return null;
    }

    int timestampPosition = record.length() - 12;
    if (timestampPosition <= 0) {
      return null;
    }
    String timestamp = record.substring(timestampPosition, record.length());
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

    String pressure = starX ? parseStarXPressure(record) : parsePressure(record);
    if (pressure == null) {
      return null;
    }
    return new MintakaBarographRecord(timestamp, minute, pressure);
  }

  private static int expectedCommaCount(int connectionMode) {
    // 4 = Mintaka Duo; 5 = Mintaka Star USB/serial; 6 = Mintaka Star WiFi/LAN.
    if (connectionMode == 4) {
      return main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA;
    }
    if (connectionMode == 5 || connectionMode == 6) {
      return main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR;
    }
    // 7 = Mintaka StarX USB/serial; 8 = Mintaka StarX WiFi/LAN.
    if (connectionMode == 7 || connectionMode == 8) {
      return main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX;
    }
    return -1;
  }

  private static int countCommas(String record) {
    return (int) record.chars().filter(character -> character == ',').count();
  }

  private static boolean hasValidChecksum(String record) {
    return Mintaka_Star_Checksum(record)
        .equals(record.substring(record.length() - 14, record.length() - 12));
  }

  private static String parsePressure(String record) {
    int commaPosition = record.indexOf(',');
    if (commaPosition < 2) {
      return null;
    }
    return validatedPressure(record.substring(0, commaPosition));
  }

  private static String parseStarXPressure(String record) {
    // StarX fields 0 and 1 are MSL and local pressure; field 12 is observation age in seconds.
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

    String localPressure = record.substring(commaPositions[0] + 1, commaPositions[1]);
    String localObservationAge = record.substring(commaPositions[12] + 1, starPosition);
    int observationAge = AGE_NOT_OK;
    if (!localObservationAge.isEmpty() && localObservationAge.indexOf('*') == -1) {
      try {
        observationAge = Integer.parseInt(localObservationAge.trim());
      } catch (NumberFormatException exception) {
        observationAge = AGE_NOT_OK;
      }
    }
    if (observationAge < 0 || observationAge > main_RS232_RS422.MAX_AGE_STARX_OBS_DATA) {
      return null;
    }
    if (localPressure.isEmpty()) {
      return null;
    }
    return validatedPressure(localPressure);
  }

  private static String validatedPressure(String pressure) {
    // Keep the historical range check because outliers create spikes in the graph.
    try {
      double value = Double.parseDouble(pressure.trim());
      return value >= 900.0 && value <= 1100.0 ? pressure : "";
    } catch (NumberFormatException exception) {
      return "";
    }
  }
}
