package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.junit.Test;

public class MintakaHistoricalRecordReaderTest {

  private static final String TIMESTAMP = "201703152006";
  private static final String VALID_RECORD =
      "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A" + TIMESTAMP;

  @Test
  public void returnsTheLatestValidRecordForTheRequestedTimestamp() throws IOException {
    File file = Files.createTempFile("turbowin-mintaka-history-reader", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of("wrong", VALID_RECORD, VALID_RECORD));

      assertEquals(
          VALID_RECORD,
          MintakaHistoricalRecordReader.readLatestValidRecord(file, TIMESTAMP, false));
    } finally {
      file.delete();
    }
  }

  @Test
  public void rejectsWrongChecksumAndCommaCount() throws IOException {
    File file = Files.createTempFile("turbowin-mintaka-history-invalid", ".txt").toFile();
    try {
      String wrongChecksum = VALID_RECORD.replace("*1A", "*00");
      String wrongCommaCount = VALID_RECORD.replace(",0,0,19*1A", ",0,19*1A");
      Files.write(file.toPath(), List.of(wrongChecksum, wrongCommaCount));

      assertNull(MintakaHistoricalRecordReader.readLatestValidRecord(file, TIMESTAMP, false));
    } finally {
      file.delete();
    }
  }

  @Test
  public void returnsNullForMissingOrEmptyFiles() throws IOException {
    File emptyFile = Files.createTempFile("turbowin-mintaka-history-empty", ".txt").toFile();
    try {
      assertNull(MintakaHistoricalRecordReader.readLatestValidRecord(emptyFile, TIMESTAMP, false));
      assertNull(
          MintakaHistoricalRecordReader.readLatestValidRecord(
              new File(emptyFile.getParent(), "missing-mintaka-history.txt"), TIMESTAMP, false));
    } finally {
      emptyFile.delete();
    }
  }
}
