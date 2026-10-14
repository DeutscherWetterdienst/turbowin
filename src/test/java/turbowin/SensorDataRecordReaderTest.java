package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class SensorDataRecordReaderTest {

  @Test
  public void returnsTheLastRecordWithTheExpectedLength() throws IOException {
    File file = Files.createTempFile("turbowin-sensor", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of("short", "1234", "wrong-length", "5678"));

      assertEquals("5678", SensorDataRecordReader.readLastRecord(file, 4));
    } finally {
      file.delete();
    }
  }

  @Test
  public void returnsNullForMissingOrEmptyFiles() throws IOException {
    File emptyFile = Files.createTempFile("turbowin-sensor-empty", ".txt").toFile();
    try {
      assertNull(SensorDataRecordReader.readLastRecord(emptyFile, 4));
      assertNull(
          SensorDataRecordReader.readLastRecord(
              new File(emptyFile.getParent(), "missing-sensor-data.txt"), 4));
    } finally {
      emptyFile.delete();
    }
  }

  @Test
  public void returnsTheLatestLineWithoutApplyingLengthFiltering() throws IOException {
    File file = Files.createTempFile("turbowin-sensor-latest", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of("short", "a longer final record"));

      assertEquals("a longer final record", SensorDataRecordReader.readLatestRecord(file));
    } finally {
      file.delete();
    }
  }

  @Test
  public void latestRecordReturnsNullForMissingOrEmptyFiles() throws IOException {
    File emptyFile = Files.createTempFile("turbowin-sensor-latest-empty", ".txt").toFile();
    try {
      assertNull(SensorDataRecordReader.readLatestRecord(emptyFile));
      assertNull(
          SensorDataRecordReader.readLatestRecord(
              new File(emptyFile.getParent(), "missing-latest-sensor-data.txt")));
    } finally {
      emptyFile.delete();
    }
  }

  @Test
  public void returnsTheLatestRecordMatchingThePredicate() throws IOException {
    File file = Files.createTempFile("turbowin-sensor-matching", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of("ignore", "match-1", "other", "match-2"));

      assertEquals(
          "match-2",
          SensorDataRecordReader.readLatestMatchingRecord(
              file, record -> record.startsWith("match")));
    } finally {
      file.delete();
    }
  }

  @Test
  public void latestMatchingRecordReturnsNullWhenNothingMatches() throws IOException {
    File file = Files.createTempFile("turbowin-sensor-no-match", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of("first", "second"));

      assertNull(SensorDataRecordReader.readLatestMatchingRecord(file, record -> false));
    } finally {
      file.delete();
    }
  }

  @Test
  public void returnsTheLastRequestedNumberOfRecordsInFileOrder() throws IOException {
    File file = Files.createTempFile("turbowin-sensor-last-records", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of("first", "second", "third"));

      assertEquals(
          Arrays.asList("second", "third"), SensorDataRecordReader.readLastRecords(file, 2));
    } finally {
      file.delete();
    }
  }

  @Test
  public void lastRecordsReturnsEmptyForMissingOrEmptyFiles() throws IOException {
    File emptyFile = Files.createTempFile("turbowin-sensor-last-records-empty", ".txt").toFile();
    try {
      assertEquals(List.of(), SensorDataRecordReader.readLastRecords(emptyFile, 2));
      assertEquals(
          List.of(),
          SensorDataRecordReader.readLastRecords(
              new File(emptyFile.getParent(), "missing-last-records.txt"), 2));
    } finally {
      emptyFile.delete();
    }
  }
}
