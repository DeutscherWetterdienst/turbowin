package turbowin;

import static turbowin.main_RS232_RS422.Mintaka_Star_Checksum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Reads the latest valid historical Mintaka record for a requested timestamp. */
final class MintakaHistoricalRecordReader {

  private MintakaHistoricalRecordReader() {}

  static String readLatestValidRecord(File sensorDataFile, String timestamp, boolean starX)
      throws IOException {
    if (!sensorDataFile.exists() || sensorDataFile.length() <= 0) {
      return null;
    }

    String latestValidRecord = null;
    try (BufferedReader in = new BufferedReader(new FileReader(sensorDataFile))) {
      String record;
      while ((record = in.readLine()) != null) {
        if (isValidRecord(record, timestamp, starX)) {
          latestValidRecord = record;
        }
      }
    }
    return latestValidRecord;
  }

  private static boolean isValidRecord(String record, String timestamp, boolean starX) {
    if (record.length() <= 15
        || !record.substring(record.length() - 12).equals(timestamp)
        || !Mintaka_Star_Checksum(record)
            .equals(record.substring(record.length() - 14, record.length() - 12))) {
      return false;
    }

    int commaCount = 0;
    int position = -1;
    do {
      position = record.indexOf(",", position + 1);
      if (position != -1) {
        commaCount++;
      }
    } while (position != -1);

    int expectedCommaCount =
        starX
            ? main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX
            : main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR;
    return commaCount == expectedCommaCount;
  }
}
