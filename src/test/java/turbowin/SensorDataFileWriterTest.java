package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class SensorDataFileWriterTest {

  @Test
  public void appendsCompleteRecordWithTimestamp() throws Exception {
    Path directory = Files.createTempDirectory("sensor-data-writer");
    Path file = directory.resolve("sensor_data.txt");
    AtomicBoolean completeRecord = new AtomicBoolean();

    SensorDataFileWriter.append(
        directory.toString(),
        file.toString(),
        "record\r\n",
        ",",
        () -> "2026-01-01T00:00:00Z",
        () -> completeRecord.set(true),
        "[TEST] ");

    assertEquals("record,2026-01-01T00:00:00Z\n", Files.readString(file));
    assertTrue(completeRecord.get());
  }

  @Test
  public void appendsPartialRecordWithoutTimestamp() throws Exception {
    Path directory = Files.createTempDirectory("sensor-data-writer");
    Path file = directory.resolve("sensor_data.txt");
    AtomicBoolean completeRecord = new AtomicBoolean();

    SensorDataFileWriter.append(
        directory.toString(),
        file.toString(),
        "partial\r",
        ",",
        () -> "unused",
        () -> completeRecord.set(true),
        "[TEST] ");

    assertEquals("partial", Files.readString(file));
    assertFalse(completeRecord.get());
  }
}
