package turbowin;

import java.io.BufferedWriter;
import java.io.IOException;

/** Writes one observer statistic to its yearly output and backup files. */
final class ObserverStatisticsWriter {

  private ObserverStatisticsWriter() {}

  static void write(
      BufferedWriter[] outputFiles,
      BufferedWriter[] backupFiles,
      int yearIndex,
      String observerName,
      int observationCount) {
    if (observationCount == 0) {
      return;
    }

    String line = observerName + Integer.toString(observationCount);
    writeLine(outputFiles[yearIndex], line);
    writeLine(backupFiles[yearIndex], line);
  }

  private static void writeLine(BufferedWriter output, String line) {
    try {
      if (output != null) {
        output.write(line);
        output.newLine();
      }
    } catch (IOException ignored) {
    }
  }
}
