package turbowin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/** Reads and validates one import without changing live settings or the shared line array. */
final class ConfigurationImportReader {

  private ConfigurationImportReader() {}

  static Result read(
      String source, boolean requireIdentityLabels, boolean preserveLogsDirectory, Reader reader)
      throws IOException {
    String[] lines = new String[main.MAX_AANTAL_CONFIGURATIEREGELS];
    reader.read(new File(source), lines, lines.length);
    if (requireIdentityLabels
        && (!contains(lines, main.SHIP_NAME_TXT) || !contains(lines, main.CALL_SIGN_TXT))) {
      throw new IllegalArgumentException("missing ship-name or call-sign label");
    }
    if (preserveLogsDirectory) {
      for (int index = 0; index < lines.length; index++) {
        if (lines[index] != null
            && ConfigurationLineMatcher.contains(lines[index], main.LOGS_DIR_TXT)) {
          lines[index] = "";
        }
      }
    }
    ConfigurationSettings settings = ConfigurationManager.parseConfigurationLines(lines);
    if (settings.isEmpty()) {
      throw new IllegalArgumentException("no recognized settings");
    }
    return new Result(lines, settings);
  }

  private static boolean contains(String[] lines, String label) {
    return Arrays.stream(lines)
        .anyMatch(line -> line != null && ConfigurationLineMatcher.contains(line, label));
  }

  @FunctionalInterface
  interface Reader {
    void read(File source, String[] lines, int maximumLines) throws IOException;
  }

  record Result(String[] lines, ConfigurationSettings settings) {
    void apply() {
      ConfigurationManager.applyConfiguration(settings);
      System.arraycopy(lines, 0, main.configuratie_regels, 0, lines.length);
    }
  }
}
