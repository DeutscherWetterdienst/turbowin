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

  static void processMultiple(
      File sensorDataFile,
      String expectedHour,
      int[] primaryCommas,
      AwsGraphCollector[] collectors,
      int graphIndex)
      throws IOException {
    if (primaryCommas.length != collectors.length) {
      throw new IllegalArgumentException("Each graph series needs one comma index and collector");
    }

    SensorDataRecordReader.forEachRecord(
        sensorDataFile,
        record -> {
          for (int series = 0; series < primaryCommas.length; series++) {
            AwsGraphRecord parsed =
                AwsGraphRecordParser.parse(record, expectedHour, primaryCommas[series], -1);
            if (parsed != null) {
              collectors[series].add(parsed, graphIndex * 12 + parsed.minute / 5);
            }
          }
        });
  }
}
