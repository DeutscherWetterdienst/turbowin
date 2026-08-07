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
 * <p>The file is the legacy indexed representation of the configuration array: file line {@code
 * n} corresponds to configuration array entry {@code n}; production callers use 100 entries. The
 * writer deliberately uses {@link BufferedWriter#newLine()} so the file follows the platform line
 * separator used by the original implementation.
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
