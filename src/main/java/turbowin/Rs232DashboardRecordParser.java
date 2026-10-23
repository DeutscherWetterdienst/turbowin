package turbowin;

/** Parses pressure fields from comma-separated, fixed-position, and StarX dashboard records. */
final class Rs232DashboardRecordParser {

  private Rs232DashboardRecordParser() {}

  /**
   * Parses modes 4–6, where the first comma-separated field is pressure and the third field is
   * pressure change.
   */
  static Result parseCommaSeparated(String record) {
    int firstComma = record.indexOf(',');
    int secondComma = record.indexOf(',', firstComma + 1);
    int thirdComma = record.indexOf(',', secondComma + 1);
    return new Result(
        record.substring(0, firstComma), record.substring(secondComma + 1, thirdComma), null);
  }

  /**
   * Parses mode 1 as PTB220 and mode 2 as PTB330, extracting 7 pressure characters and 5 pressure
   * change characters from their respective fixed offsets.
   */
  static Result parseFixedPosition(String record, int connectionMode) {
    int pressurePosition;
    int pressureChangePosition;
    if (connectionMode == 1) {
      pressurePosition = main.RECORD_P_BEGIN_POS_PTB220;
      pressureChangePosition = main.RECORD_ppp_BEGIN_POS_PTB220;
    } else if (connectionMode == 2) {
      pressurePosition = main.RECORD_P_BEGIN_POS_PTB330;
      pressureChangePosition = main.RECORD_ppp_BEGIN_POS_PTB330;
    } else {
      return null;
    }

    return new Result(
        record.substring(pressurePosition, pressurePosition + 7),
        record.substring(pressureChangePosition, pressureChangePosition + 5),
        null);
  }

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
    String observationAgeField = record.substring(commaPositions[12] + 1, starPosition);
    // An absent age was invalid but silent; non-empty malformed ages still produce a diagnostic.
    if (observationAgeField.isEmpty()) {
      return new Result("", "", record.substring(0, commaPositions[0]));
    }

    int observationAge;
    try {
      observationAge = Integer.parseInt(observationAgeField.trim());
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
