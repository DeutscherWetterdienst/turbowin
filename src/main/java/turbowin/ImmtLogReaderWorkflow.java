package turbowin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads the last record from the IMMT log.
 *
 * <p>This read is synchronous; callers must invoke it from a SwingWorker rather than the UI
 * thread.
 */
final class ImmtLogReaderWorkflow {

  private ImmtLogReaderWorkflow() {}

  static String readLastRecord(String path) {
    String lastRecord = "";
    File immtFile = new File(path);
    if (immtFile.exists() && immtFile.length() > 0) {
      try (BufferedReader in = new BufferedReader(new FileReader(immtFile))) {
        String record;
        while ((record = in.readLine()) != null) {
          lastRecord = record;
        }
      } catch (IOException ex) {
        System.out.println("--- Function bepaal_last_record_uit_immt(): " + ex);
      }
    }
    return lastRecord;
  }
}
