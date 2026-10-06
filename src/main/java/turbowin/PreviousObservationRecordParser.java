package turbowin;

/** Parses the date/time and position fields needed from the previous observation record. */
final class PreviousObservationRecordParser {

  private PreviousObservationRecordParser() {}

  static PreviousObservationRecord parse(String record) {
    if (record.length() < 19) {
      return PreviousObservationRecord.invalid();
    }

    // IMMT fields: year [1..4], month [5..6], day [7..8], hour [9..10], quadrant [11],
    // latitude [12..14], and longitude [15..18]; coordinates are stored in tenths of degrees.
    int year = 0;
    int month = 0;
    int day = 0;
    int hour = 0;
    int quadrant = 0;
    float latitude = 0;
    float longitude = 0;
    boolean valid = true;

    try {
      year = parseInt(record.substring(1, 5));
    } catch (NumberFormatException ex) {
      System.out.println("+++ Error function: position_sequence_check(). " + ex);
      valid = false;
    }
    try {
      month = parseInt(record.substring(5, 7));
    } catch (NumberFormatException ex) {
      System.out.println("+++ Error function: position_sequence_check(). " + ex);
      valid = false;
    }
    try {
      day = parseInt(record.substring(7, 9));
    } catch (NumberFormatException ex) {
      System.out.println("+++ Error function: position_sequence_check(). " + ex);
      valid = false;
    }
    try {
      hour = parseInt(record.substring(9, 11));
    } catch (NumberFormatException ex) {
      System.out.println("+++ Error function: position_sequence_check(). " + ex);
      valid = false;
    }
    try {
      quadrant = parseInt(record.substring(11, 12));
    } catch (NumberFormatException ex) {
      System.out.println("+++ Error function: position_sequence_check(). " + ex);
      valid = false;
    }
    try {
      latitude = Float.parseFloat(record.substring(12, 15).trim()) / 10;
    } catch (NumberFormatException ex) {
      System.out.println("+++ Error function: position_sequence_check(). " + ex);
      valid = false;
    }
    try {
      longitude = Float.parseFloat(record.substring(15, 19).trim()) / 10;
    } catch (NumberFormatException ex) {
      System.out.println("+++ Error function: position_sequence_check(). " + ex);
      valid = false;
    }

    if (!valid) {
      return PreviousObservationRecord.invalid();
    }

    // WMO quadrants are 1=NE, 3=SE, 5=SW, and 7=NW.
    if (quadrant == 3 || quadrant == 5) {
      // Southern quadrants use a negative latitude.
      latitude *= -1;
    }
    if (quadrant == 5 || quadrant == 7) {
      // Western quadrants use a negative longitude.
      longitude *= -1;
    }

    return new PreviousObservationRecord(true, year, month, day, hour, latitude, longitude);
  }

  private static int parseInt(String value) {
    return Integer.valueOf(value.trim());
  }

  record PreviousObservationRecord(
      boolean valid, int year, int month, int day, int hour, float latitude, float longitude) {
    static PreviousObservationRecord invalid() {
      return new PreviousObservationRecord(false, 0, 0, 0, 0, 0, 0);
    }
  }
}
