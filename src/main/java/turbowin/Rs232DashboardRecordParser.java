package turbowin;

/** Parses the pressure fields from a StarX dashboard record. */
final class Rs232DashboardRecordParser {

  private Rs232DashboardRecordParser() {}

  /**
   * Parses a StarX record with 13 comma-separated fields before the observation age, which is in
   * seconds and must be between zero and {@link main_RS232_RS422#MAX_AGE_STARX_OBS_DATA}. The
   * two-character checksum follows {@code *} and precedes the trailing 12-character timestamp;
   * therefore its position is {@code length - 14} through {@code length - 12}. The checksum is
   * calculated from the record text before {@code *}.
   */
  static Result parseStarX(String record) {
    String expectedChecksum = record.substring(record.length() - 14, record.length() - 12);
    if (!main_RS232_RS422.Mintaka_Star_Checksum(record).equals(expectedChecksum)) {
      return null;
    }

    int[] commaPositions = new int[13];
    int previousPosition = -1;
    for (int index = 0; index < commaPositions.length; index++) {
      commaPositions[index] = record.indexOf(',', previousPosition + 1);
      previousPosition = commaPositions[index];
    }

    int starPosition = record.indexOf('*', commaPositions[12] + 1);

    int observationAge;
    try {
      observationAge =
          Integer.parseInt(record.substring(commaPositions[12] + 1, starPosition).trim());
    } catch (NumberFormatException exception) {
      System.out.println("--- RS232_compute_dasboard_values() " + exception);
      return new Result("", "", record.substring(0, commaPositions[0]));
    }

    if (observationAge < 0 || observationAge > main_RS232_RS422.MAX_AGE_STARX_OBS_DATA) {
      return new Result("", "", record.substring(0, commaPositions[0]));
    }

    return new Result(
        record.substring(0, commaPositions[0]),
        record.substring(commaPositions[1] + 1, commaPositions[2]),
        record.substring(0, commaPositions[0]));
  }

  static final class Result {
    final String pressure;
    final String pressureChange;
    final String observationPressure;

    Result(String pressure, String pressureChange, String observationPressure) {
      this.pressure = pressure;
      this.pressureChange = pressureChange;
      this.observationPressure = observationPressure;
    }
  }
}
