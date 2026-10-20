package turbowin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * Appends a sensor record while preserving the application's record-boundary conventions.
 *
 * <p>A complete record invokes {@code onCompleteRecord} for the main-screen last-update display
 * and appends the supplied UTC minute timestamp. A serial fragment is written without a timestamp
 * until the complete record arrives.
 */
final class SensorDataFileWriter {

  private SensorDataFileWriter() {}

  static void append(
      String logsDirectory,
      String path,
      String record,
      String separator,
      Supplier<String> timestamp,
      Runnable onCompleteRecord,
      String errorPrefix) {
    if (logsDirectory == null || logsDirectory.isEmpty()) {
      return;
    }

    try (BufferedWriter out = new BufferedWriter(new FileWriter(path, true))) {
      if (record.indexOf("\n") != -1) {
        onCompleteRecord.run();
        out.write(record.replaceAll("[\r\n]", ""));
        out.write(separator);
        out.write(timestamp.get());
        out.newLine();
      } else {
        out.write(record.replace("\r", ""));
      }
    } catch (IOException ex) {
      main.log_turbowin_system_message(errorPrefix + ex + "; unable to write to: " + path);
    }
  }
}
