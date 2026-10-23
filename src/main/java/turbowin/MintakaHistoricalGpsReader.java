package turbowin;

import java.io.File;
import java.io.IOException;

/** Reads and extracts coordinates from a historical Mintaka record. */
final class MintakaHistoricalGpsReader {

  private MintakaHistoricalGpsReader() {}

  static MintakaHistoricalGpsReading readLatestMatching(File file, String timestamp, boolean starX)
      throws IOException {
    String record = MintakaHistoricalRecordReader.readLatestMatchingRecord(file, timestamp, starX);
    return record == null ? null : parseRecord(record, starX);
  }

  static MintakaHistoricalGpsReading read(File file, String timestamp, boolean starX)
      throws IOException {
    return read(file, timestamp, starX, false);
  }

  static MintakaHistoricalGpsReading read(
      File file, String timestamp, boolean starX, boolean windDiagnostics) throws IOException {
    String record =
        MintakaHistoricalRecordReader.readLatestValidRecord(
            file, timestamp, starX, windDiagnostics);
    if (record == null) {
      return null;
    }

    return parseRecord(record, starX);
  }

  static MintakaHistoricalGpsReading parseRecord(String record, boolean starX) {
    MintakaStarRecordFields fields = MintakaStarRecordFieldsParser.parse(record, starX);
    return fields == null
        ? null
        : new MintakaHistoricalGpsReading(fields.latitude, fields.longitude);
  }
}
