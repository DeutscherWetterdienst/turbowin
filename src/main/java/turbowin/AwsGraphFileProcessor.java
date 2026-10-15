package turbowin;

import java.io.File;
import java.io.IOException;

/** Processes all records in one AWS graph file. */
final class AwsGraphFileProcessor {

  private AwsGraphFileProcessor() {}

  static void process(
      File sensorDataFile,
      String expectedHour,
      int primaryComma,
      int secondaryComma,
      AwsGraphCollector collector,
      int graphIndex)
      throws IOException {
    SensorDataRecordReader.forEachRecord(
        sensorDataFile,
        record -> {
          AwsGraphRecord parsed =
              AwsGraphRecordParser.parse(record, expectedHour, primaryComma, secondaryComma);
          if (parsed != null) {
            collector.add(parsed, graphIndex * 12 + parsed.minute / 5);
          }
        });
  }
}
