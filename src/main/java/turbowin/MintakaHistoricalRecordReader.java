package turbowin;

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

  static String readLatestPhysicalRecord(File sensorDataFile) throws IOException {
    if (!sensorDataFile.exists() || sensorDataFile.length() <= 0) {
      return null;
    }

    String latestRecord = null;
    try (BufferedReader in = new BufferedReader(new FileReader(sensorDataFile))) {
      String record;
      while ((record = in.readLine()) != null) {
        latestRecord = record;
      }
    }
    return latestRecord;
  }

  private static boolean isValidRecord(String record, String timestamp, boolean starX) {
    if (!MintakaRecordValidator.isValid(record, starX)
        || !record.substring(record.length() - 12).equals(timestamp)) {
      return false;
    }
    return true;
  }
}
