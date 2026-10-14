package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
}
