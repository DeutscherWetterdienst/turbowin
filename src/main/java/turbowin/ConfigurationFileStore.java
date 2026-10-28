package turbowin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Reads and writes the line-oriented TurboWin configuration file.
 *
 * <p>Production callers use a 100-entry line array. Writing skips null and empty entries; reading
 * clears the array and fills it sequentially with at most 100 physical lines. Array positions need
 * not survive a round trip: settings are identified by their labels when applied. The writer uses
 * {@link BufferedWriter#newLine()} for platform line separators, and both operations use the
 * default charset of {@link FileReader} and {@link FileWriter}.
 */
final class ConfigurationFileStore {

  private ConfigurationFileStore() {}

  static void write(File file, String[] lines, int maximumLines) throws IOException {
    try (BufferedWriter out = new BufferedWriter(new FileWriter(file, false))) {
      for (int i = 0; i < maximumLines; i++) {
        if (lines[i] != null && !lines[i].isEmpty()) {
          out.write(lines[i]);
          out.newLine();
        }
      }
    }
  }

  static void read(File file, String[] lines, int maximumLines) throws IOException {
    for (int i = 0; i < maximumLines; i++) {
      lines[i] = "";
    }

    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
      int lineNumber = 0;
      String line;
      while ((line = in.readLine()) != null && lineNumber < maximumLines) {
        lines[lineNumber] = line;
        lineNumber++;
      }
    }
  }
}
