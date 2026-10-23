package turbowin;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.LongSupplier;

/** Reads and parses the latest Mintaka Duo sensor-data record. */
final class MintakaDuoRecordReader {

  private MintakaDuoRecordReader() {}

  static MintakaDuoRecordReading readLatest(File sensorDataFile, long nowMillis)
      throws IOException, ParseException {
    return readLatest(sensorDataFile, () -> nowMillis, "Mintaka Duo");
  }

  static MintakaDuoRecordReading readLatest(
      File sensorDataFile, long nowMillis, String diagnosticContext)
      throws IOException, ParseException {
    return readLatest(sensorDataFile, () -> nowMillis, diagnosticContext);
  }

  static MintakaDuoRecordReading readLatest(
      File sensorDataFile, LongSupplier nowMillis, String diagnosticContext)
      throws IOException, ParseException {
    String rawRecord = SensorDataRecordReader.readLatestRecord(sensorDataFile);
    MintakaDuoRecord record = MintakaDuoRecordParser.parse(rawRecord);
    if (record == null) {
      if (rawRecord != null) {
        String failureReason = MintakaDuoRecordParser.failureReason(rawRecord);
        // A missing characteristic after the third comma was rejected without a format message.
        if (failureReason != null) {
          System.out.println(
              "--- "
                  + diagnosticContext
                  + ", format ("
                  + failureReason
                  + ") last retrieved record NOT ok (file: "
                  + sensorDataFile
                  + ")");
        }
      }
      return null;
    }

    long ageMinutes = SensorDataRecordAgeCalculator.ageMinutes(record.timestamp, nowMillis);
    return new MintakaDuoRecordReading(record, ageMinutes);
  }
}
