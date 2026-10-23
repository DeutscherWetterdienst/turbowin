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
      Files.write(
          file.toPath(), List.of("wrong", VALID_RECORD, VALID_RECORD.replace(TIMESTAMP, "201703152007")));

      assertEquals(
          VALID_RECORD,
          MintakaHistoricalRecordReader.readLatestValidRecord(file, TIMESTAMP, false));
    } finally {
      file.delete();
    }
  }

  @Test
  public void historicalWorkflowFieldsPreserveCoordinatesFromTheMatchedRecord() throws IOException {
    File file = Files.createTempFile("turbowin-mintaka-history-fields", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of(VALID_RECORD));

      String record = MintakaHistoricalRecordReader.readLatestValidRecord(file, TIMESTAMP, false);
      MintakaStarRecordFields fields = MintakaStarRecordFieldsParser.parse(record, false);

      assertEquals(" 52 41.9535N", fields.latitude);
      assertEquals("  6 14.1943E", fields.longitude);
    } finally {
      file.delete();
    }
  }

  @Test
  public void matchingRecordPolicyPreservesDuplicateOrderAndShortRecordInvalidation()
      throws IOException {
    File file = Files.createTempFile("turbowin-mintaka-history-duplicates", ".txt").toFile();
    try {
      String invalid = VALID_RECORD.replace("*1A", "*00");
      String unrelated = VALID_RECORD.replace(TIMESTAMP, "201703152007");
      Files.write(file.toPath(), List.of(VALID_RECORD, invalid, unrelated));
      assertNull(MintakaHistoricalRecordReader.readLatestMatchingRecord(file, TIMESTAMP, false));

      Files.write(file.toPath(), List.of(invalid, VALID_RECORD, unrelated));
      assertEquals(
          VALID_RECORD,
          MintakaHistoricalRecordReader.readLatestMatchingRecord(file, TIMESTAMP, false));

      Files.write(file.toPath(), List.of(VALID_RECORD, "short", unrelated));
      assertNull(MintakaHistoricalRecordReader.readLatestMatchingRecord(file, TIMESTAMP, false));

      Files.write(file.toPath(), List.of("short", VALID_RECORD));
      assertEquals(
          VALID_RECORD,
          MintakaHistoricalRecordReader.readLatestMatchingRecord(file, TIMESTAMP, false));
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
  public void matchingRecordPolicyAlsoInvalidatesStarXDuplicates() throws IOException {
    File file = Files.createTempFile("turbowin-mintaka-starx-duplicates", ".txt").toFile();
    try {
      String payload =
          "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60";
      String valid =
          payload + "*" + main_RS232_RS422.Mintaka_Star_Checksum(payload + "*") + TIMESTAMP;
      String wrongChecksum = payload + "*ZZ" + TIMESTAMP;
      String wrongCommaPayload = payload + ",extra";
      String wrongCommaCount =
          wrongCommaPayload
              + "*"
              + main_RS232_RS422.Mintaka_Star_Checksum(wrongCommaPayload + "*")
              + TIMESTAMP;
      for (String invalid : List.of(wrongChecksum, wrongCommaCount)) {
        Files.write(file.toPath(), List.of(valid, invalid));
        assertNull(MintakaHistoricalRecordReader.readLatestMatchingRecord(file, TIMESTAMP, true));

        Files.write(file.toPath(), List.of(invalid, valid));
        assertEquals(
            valid, MintakaHistoricalRecordReader.readLatestMatchingRecord(file, TIMESTAMP, true));
      }
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

  @Test
  public void rejectsARecordWithTheWrongTimestamp() throws IOException {
    File file = Files.createTempFile("turbowin-mintaka-history-timestamp", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of(VALID_RECORD));

      assertNull(MintakaHistoricalRecordReader.readLatestValidRecord(file, "201703152007", false));
    } finally {
      file.delete();
    }
  }

  @Test
  public void acceptsAValidStarXRecordWithTheStarXCommaCount() throws IOException {
    File file = Files.createTempFile("turbowin-mintaka-history-starx", ".txt").toFile();
    try {
      String recordWithoutChecksum =
          "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60*00"
              + TIMESTAMP;
      String validStarXRecord =
          recordWithoutChecksum.replace(
              "*00", "*" + main_RS232_RS422.Mintaka_Star_Checksum(recordWithoutChecksum));
      Files.write(file.toPath(), List.of(validStarXRecord));

      assertEquals(
          validStarXRecord,
          MintakaHistoricalRecordReader.readLatestValidRecord(file, TIMESTAMP, true));
    } finally {
      file.delete();
    }
  }
}
