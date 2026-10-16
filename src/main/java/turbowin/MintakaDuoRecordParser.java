package turbowin;

/**
 * Validates and extracts fields from Mintaka Duo records.
 *
 * <p>The saved layout is {@code pressure,pressure,pressureTendency,characteristic*checksum} followed
 * by a 12-character {@code YYYYMMDDHHmm} timestamp. The legacy minimum-length guard is a
 * conservative check for that timestamp, the three required commas, and a minimum pressure
 * fragment. The characteristic immediately after the third comma is one character; it can be
 * {@code *} when no characteristic is present and the caller handles that case.
 *
 * <p>This shared parser returns {@code null} for malformed records. {@link #failureReason(String)}
 * preserves the legacy minimum-length and comma-count distinctions for the shared reader's
 * diagnostics.
 */
final class MintakaDuoRecordParser {

  private MintakaDuoRecordParser() {}

  static MintakaDuoRecord parse(String record) {
    if (failureReason(record) != null) {
      return null;
    }

    int firstComma = record.indexOf(',');
    int secondComma = record.indexOf(',', firstComma + 1);
    int thirdComma = record.indexOf(',', secondComma + 1);
    if (firstComma < 0 || secondComma < 0 || thirdComma < 0 || thirdComma + 1 >= record.length()) {
      return null;
    }

    String timestamp = record.substring(record.length() - 12);
    return new MintakaDuoRecord(
        timestamp,
        record.substring(0, firstComma),
        record.substring(secondComma + 1, thirdComma),
        record.substring(thirdComma + 1, thirdComma + 2));
  }

  static String failureReason(String record) {
    if (record == null || record.length() <= 15) {
      return "min. record length";
    }
    if (!hasExpectedCommaCount(record)) {
      return "number commas";
    }
    return null;
  }

  private static boolean hasExpectedCommaCount(String record) {
    if (record == null) {
      return false;
    }

    int commaCount = 0;
    int position = 0;
    do {
      position = record.indexOf(',', position + 1);
      if (position > 0) {
        commaCount++;
      }
    } while (position > 0);
    return commaCount == main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA;
  }
}
