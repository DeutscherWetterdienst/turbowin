package turbowin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Reads historical Mintaka records using the requested record-selection policy. */
final class MintakaHistoricalRecordReader {

  private MintakaHistoricalRecordReader() {}

  /**
   * Preserves the legacy history loop's file-order policy: an invalid matching duplicate clears
   * an earlier valid position, and a later valid match can restore it. Unrelated timestamps do not
   * replace the requested position; a short physical record clears it regardless of timestamp.
   */
  static String readLatestMatchingRecord(File sensorDataFile, String timestamp, boolean starX)
      throws IOException {
    if (!sensorDataFile.exists() || sensorDataFile.length() <= 0) {
      return null;
    }

    String latestMatchingRecord = null;
    try (BufferedReader in = new BufferedReader(new FileReader(sensorDataFile))) {
      String record;
      while ((record = in.readLine()) != null) {
        if (record.length() <= 15) {
          latestMatchingRecord = null;
        } else if (hasTimestamp(record, timestamp)) {
          latestMatchingRecord = isValidRecord(record, timestamp, starX) ? record : null;
        }
      }
    }
    return latestMatchingRecord;
  }

  /** Skips malformed matches silently, retaining the latest valid record for this timestamp. */
  static String readLatestValidRecord(File sensorDataFile, String timestamp, boolean starX)
      throws IOException {
    if (!sensorDataFile.exists() || sensorDataFile.length() <= 0) {
      return null;
    }

    String latestValidRecord = null;
    try (BufferedReader in = new BufferedReader(new FileReader(sensorDataFile))) {
      String record;
      while ((record = in.readLine()) != null) {
        if (hasTimestamp(record, timestamp)) {
          if (isValidRecord(record, timestamp, starX)) {
            latestValidRecord = record;
          }
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

  private static boolean hasTimestamp(String record, String timestamp) {
    return record.length() >= 12 && record.substring(record.length() - 12).equals(timestamp);
  }
}
