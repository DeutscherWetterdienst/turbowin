package turbowin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Predicate;

/** Reads the latest fixed-length record from a sensor-data file. */
final class SensorDataRecordReader {

  private SensorDataRecordReader() {}

  static String readLastRecord(File sensorDataFile, int recordLength) throws IOException {
    if (!sensorDataFile.exists() || sensorDataFile.length() <= 0) {
      return null;
    }

    String lastRecord = null;
    try (BufferedReader in = new BufferedReader(new FileReader(sensorDataFile))) {
      String record;
      while ((record = in.readLine()) != null) {
        if (record.length() == recordLength) {
          lastRecord = record;
        }
      }
    }
    return lastRecord;
  }

  static String readLatestRecord(File sensorDataFile) throws IOException {
    if (!sensorDataFile.exists() || sensorDataFile.length() <= 0) {
      return null;
    }

    String lastRecord = null;
    try (BufferedReader in = new BufferedReader(new FileReader(sensorDataFile))) {
      String record;
      while ((record = in.readLine()) != null) {
        lastRecord = record;
      }
    }
    return lastRecord;
  }

  static String readLatestMatchingRecord(File sensorDataFile, Predicate<String> matches)
      throws IOException {
    if (!sensorDataFile.exists() || sensorDataFile.length() <= 0) {
      return null;
    }

    String lastMatchingRecord = null;
    try (BufferedReader in = new BufferedReader(new FileReader(sensorDataFile))) {
      String record;
      while ((record = in.readLine()) != null) {
        if (matches.test(record)) {
          lastMatchingRecord = record;
        }
      }
    }
    return lastMatchingRecord;
  }
}
