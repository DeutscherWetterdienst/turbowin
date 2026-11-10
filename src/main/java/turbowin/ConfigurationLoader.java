package turbowin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Reads and validates startup candidates without applying any settings to global state. */
final class ConfigurationLoader {

  private ConfigurationLoader() {}

  static Result load(String dataDirectory) {
    Set<Path> candidates = new LinkedHashSet<>();
    addCandidate(candidates, dataDirectory);
    return loadCandidates(candidates, ConfigurationFileStore::read);
  }

  static Result loadLegacy(String logsDirectory, String dataDirectory) {
    return loadLegacy(logsDirectory, dataDirectory, ConfigurationFileStore::read);
  }

  static Result loadLegacy(String logsDirectory, String dataDirectory, Reader reader) {
    Set<Path> candidates = new LinkedHashSet<>();
    addCandidate(candidates, logsDirectory);
    addCandidate(candidates, dataDirectory);
    return loadCandidates(candidates, reader);
  }

  private static Result loadCandidates(Set<Path> candidates, Reader reader) {
    List<String> messages = new ArrayList<>();
    for (Path candidate : candidates) {
      if (Files.notExists(candidate)) {
        messages.add("[CONFIGURATION] missing '" + candidate + "'");
        continue;
      }
      // Discard the entire buffer on failure, including any lines read before an I/O error.
      String[] lines = new String[main.MAX_AANTAL_CONFIGURATIEREGELS];
      try {
        reader.read(candidate.toFile(), lines, lines.length);
        ConfigurationSettings settings = ConfigurationManager.parseConfigurationLines(lines);
        if (settings.isEmpty()) {
          throw new IllegalArgumentException("no recognized settings");
        }
        messages.add("[CONFIGURATION] loaded '" + candidate + "'");
        return new Result(candidate, lines, settings, List.copyOf(messages));
      } catch (IOException ex) {
        messages.add(
            "[CONFIGURATION] unable to read '" + candidate + "' (" + ex.getMessage() + ")");
      } catch (IllegalArgumentException ex) {
        // The parser's top-level message contains a line number, never the rejected value.
        messages.add("[CONFIGURATION] rejected '" + candidate + "': " + ex.getMessage());
      }
    }
    messages.add("[CONFIGURATION] no usable configuration; retaining existing settings");
    return new Result(null, null, null, List.copyOf(messages));
  }

  private static void addCandidate(Set<Path> candidates, String directory) {
    if (directory != null && !directory.isEmpty()) {
      candidates.add(Path.of(directory, main.CONFIGURATION_FILE).toAbsolutePath().normalize());
    }
  }

  @FunctionalInterface
  interface Reader {
    void read(File file, String[] lines, int maximumLines) throws IOException;
  }

  record Result(
      Path source, String[] lines, ConfigurationSettings settings, List<String> messages) {
    boolean loaded() {
      return source != null;
    }
  }
}
