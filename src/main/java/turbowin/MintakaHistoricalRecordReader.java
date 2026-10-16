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
    String latestInvalidMatchingRecord = null;
    try (BufferedReader in = new BufferedReader(new FileReader(sensorDataFile))) {
      String record;
      while ((record = in.readLine()) != null) {
        if (hasTimestamp(record, timestamp)) {
          if (isValidRecord(record, timestamp, starX)) {
            latestValidRecord = record;
          } else {
            latestInvalidMatchingRecord = record;
          }
        }
      }
    }
    if (latestValidRecord == null && latestInvalidMatchingRecord != null) {
      reportInvalidRecord(sensorDataFile, latestInvalidMatchingRecord, starX);
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

  private static boolean hasTimestamp(String record, String timestamp) {
    return record.length() >= 12 && record.substring(record.length() - 12).equals(timestamp);
  }

  private static void reportInvalidRecord(File sensorDataFile, String record, boolean starX) {
    if (record.length() <= 15) {
      System.out.println(
          "--- Mintaka "
              + (starX ? "StarX" : "Star")
              + " format (min. record length) last retrieved record NOT ok (file: "
              + sensorDataFile
              + ")");
    } else if (!MintakaRecordValidator.hasValidChecksum(record)) {
      System.out.println("--- checksum NOT ok (" + record + ")");
    } else if (!MintakaRecordValidator.hasExpectedCommaCount(record, starX)) {
      System.out.println(
          "--- Mintaka "
              + (starX ? "StarX" : "Star")
              + " format (number commas) last retrieved record NOT ok (file: "
              + sensorDataFile
              + "; record: "
              + record
              + ")");
    }
  }
}
