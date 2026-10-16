package turbowin;

import static turbowin.main_RS232_RS422.Mintaka_Star_Checksum;

/** Shared format and checksum validation for historical Mintaka records. */
final class MintakaRecordValidator {

  private MintakaRecordValidator() {}

  static boolean hasExpectedCommaCount(String record, boolean starX) {
    int commaCount = (int) record.chars().filter(character -> character == ',').count();
    int expectedCommaCount =
        starX
            ? main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX
            : main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR;
    return commaCount == expectedCommaCount;
  }

  static boolean hasValidChecksum(String record) {
    return Mintaka_Star_Checksum(record)
        .equals(record.substring(record.length() - 14, record.length() - 12));
  }

  static boolean isValid(String record, boolean starX) {
    return record.length() > 15 && hasExpectedCommaCount(record, starX) && hasValidChecksum(record);
  }
}
