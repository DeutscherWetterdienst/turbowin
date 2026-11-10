package turbowin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * Reads and writes the line-oriented TurboWin configuration file.
 *
 * <p>Production callers use a 100-entry line array. Writing skips null and empty entries; reading
 * clears the array and fills it sequentially with at most 100 physical lines. Array positions need
 * not survive a round trip: settings are identified by their labels when applied. The writer uses
 * {@link BufferedWriter#newLine()} for platform line separators, and both operations use the
 * default charset of {@link FileReader} and {@link FileWriter}.
 *
 * <p>Writes use a temporary sibling file and atomic replacement. An unsupported atomic move is a
 * save failure; there is no fallback that truncates the previous configuration file.
 */
final class ConfigurationFileStore {

  private ConfigurationFileStore() {}

  static void write(File file, String[] lines, int maximumLines) throws IOException {
    writeAtomically(
        file,
        out -> {
          for (int i = 0; i < maximumLines; i++) {
            if (lines[i] != null && !lines[i].isEmpty()) {
              out.write(lines[i]);
              out.newLine();
            }
          }
        });
  }

  static void writeAtomically(File file, ContentWriter writeContent) throws IOException {
    Path target = file.toPath().toAbsolutePath();
    // FileWriter followed configuration symlinks; replace their target rather than the link.
    if (Files.isSymbolicLink(target)) {
      target = target.toRealPath();
    }
    Path temporary = createTemporaryFile(target);
    try {
      PosixFileAttributeView attributes =
          Files.getFileAttributeView(target, PosixFileAttributeView.class);
      if (attributes != null && Files.exists(target)) {
        // Restore the exact mode after creation, since the process umask can remove access bits.
        Files.setPosixFilePermissions(temporary, attributes.readAttributes().permissions());
      }
      try (BufferedWriter out = new BufferedWriter(new FileWriter(temporary.toFile(), false))) {
        writeContent.write(out);
      }
      Files.move(
          temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } finally {
      Files.deleteIfExists(temporary);
    }
  }

  static Path createTemporaryFile(Path target) throws IOException {
    String prefix = "." + target.getFileName() + "-";
    if (Files.getFileStore(target.getParent())
        .supportsFileAttributeView(PosixFileAttributeView.class)) {
      // Use the regular-file mode subject to the process umask, rather than createTempFile's
      // more restrictive default mode. An existing target's exact mode is restored before writing.
      return Files.createTempFile(
          target.getParent(),
          prefix,
          ".tmp",
          PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-rw-rw-")));
    }
    return Files.createTempFile(target.getParent(), prefix, ".tmp");
  }

  @FunctionalInterface
  interface ContentWriter {
    void write(BufferedWriter out) throws IOException;
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
