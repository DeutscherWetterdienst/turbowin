package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.junit.Test;

public class MintakaDuoRecordReaderTest {

  private static final String RECORD = "1021.89,1021.89,1.70,1*05201502041505";
  private static final long RECORD_TIME_MILLIS = 1423062300000L;

  @Test
  public void returnsLatestValidRecordWithinConfiguredAge() throws Exception {
    withUtcDateFormat(
        () -> {
          File file = Files.createTempFile("turbowin-mintaka-duo", ".txt").toFile();
          try {
            Files.writeString(file.toPath(), RECORD);

            MintakaDuoRecordReading result =
                MintakaDuoRecordReader.readLatest(file, RECORD_TIME_MILLIS + 3 * 60 * 1000);

            assertEquals("1.70", result.record.pressureTendency);
            assertEquals(3, result.ageMinutes);
          } finally {
            file.delete();
          }
        });
  }

  @Test
  public void rejectsStaleMalformedMissingAndEmptyRecords() throws Exception {
    withUtcDateFormat(
        () -> {
          File file = Files.createTempFile("turbowin-mintaka-duo", ".txt").toFile();
          File missing = new File(file.getParent(), "missing-mintaka-duo.txt");
          try {
            Files.writeString(file.toPath(), RECORD);
            MintakaDuoRecordReading stale =
                MintakaDuoRecordReader.readLatest(file, RECORD_TIME_MILLIS + 4 * 60 * 1000);
            assertEquals(false, stale.isWithin(3));

            Files.writeString(file.toPath(), "malformed");
            assertNull(MintakaDuoRecordReader.readLatest(file, RECORD_TIME_MILLIS));

            Files.writeString(file.toPath(), "");
            assertNull(MintakaDuoRecordReader.readLatest(file, RECORD_TIME_MILLIS));
            assertNull(MintakaDuoRecordReader.readLatest(missing, RECORD_TIME_MILLIS));
          } finally {
            file.delete();
          }
        });
  }

  @Test
  public void samplesClockAfterReadingThreeMinuteBoundaryRecord() throws Exception {
    withUtcDateFormat(
        () -> {
          File file = Files.createTempFile("turbowin-mintaka-duo", ".txt").toFile();
          try {
            Files.writeString(file.toPath(), RECORD);

            MintakaDuoRecordReading result =
                MintakaDuoRecordReader.readLatest(
                    file,
                    () -> {
                      try {
                        Files.writeString(file.toPath(), "malformed");
                      } catch (Exception exception) {
                        throw new AssertionError(exception);
                      }
                      return RECORD_TIME_MILLIS + 3 * 60 * 1000;
                    },
                    "Mintaka Duo");

            assertEquals(3, result.ageMinutes);
            assertTrue(result.isWithin(3));
          } finally {
            file.delete();
          }
        });
  }

  private static void withUtcDateFormat(ThrowingRunnable runnable) throws Exception {
    SimpleDateFormat original = main.sdf4;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    main.sdf4 = format;
    try {
      runnable.run();
    } finally {
      main.sdf4 = original;
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable {
    void run() throws Exception;
  }
}
