package turbowin;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Locates the bounded installation layouts used by released Windows, Debian, and RPM builds. */
final class LegacyInstallationLocator {
  private static final Pattern RPM_DIRECTORY =
      Pattern.compile("TurboWin\\+-([0-9]+(?:\\.[0-9]+)*)([-_].*)?");

  private LegacyInstallationLocator() {}

  static List<Path> find(String os) {
    return find(os, System.getenv(), Path.of("/usr/local/bin"));
  }

  static List<Path> find(String os, Map<String, String> environment, Path unixBin) {
    Set<Path> candidates = new LinkedHashSet<>();
    if ("WINDOWS".equals(os)) {
      for (String name : List.of("ProgramFiles(x86)", "ProgramW6432", "ProgramFiles")) {
        String directory = environment.get(name);
        if (directory != null && !directory.isEmpty()) {
          add(candidates, Path.of(directory, "TurboWin+", "bin"));
        }
      }
      add(candidates, Path.of("C:\\Program Files (x86)", "TurboWin+", "bin"));
      add(candidates, Path.of("C:\\Program Files", "TurboWin+", "bin"));
    } else {
      List<Path> versioned = new ArrayList<>();
      if (Files.isDirectory(unixBin)) {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(unixBin, "TurboWin+-*")) {
          for (Path entry : entries) {
            if (RPM_DIRECTORY.matcher(entry.getFileName().toString()).matches()
                && Files.isDirectory(entry)) {
              versioned.add(entry);
            }
          }
        } catch (IOException | DirectoryIteratorException | SecurityException ex) {
          System.out.println("[MIGRATION] unable to inspect '" + unixBin + "': " + ex);
        }
      }
      versioned.sort((left, right) -> compareVersions(right, left));
      for (Path installation : versioned) {
        add(candidates, installation.resolve("turbowin_jws/bin"));
      }
      add(candidates, unixBin.resolve("turbowin_jws/bin"));
    }
    return List.copyOf(candidates);
  }

  private static void add(Set<Path> candidates, Path directory) {
    candidates.add(directory.toAbsolutePath().normalize());
  }

  private static int compareVersions(Path left, Path right) {
    Matcher leftMatch = RPM_DIRECTORY.matcher(left.getFileName().toString());
    Matcher rightMatch = RPM_DIRECTORY.matcher(right.getFileName().toString());
    leftMatch.matches();
    rightMatch.matches();
    String[] leftParts = leftMatch.group(1).split("\\.");
    String[] rightParts = rightMatch.group(1).split("\\.");
    for (int index = 0; index < Math.max(leftParts.length, rightParts.length); index++) {
      BigInteger leftNumber =
          index < leftParts.length ? new BigInteger(leftParts[index]) : BigInteger.ZERO;
      BigInteger rightNumber =
          index < rightParts.length ? new BigInteger(rightParts[index]) : BigInteger.ZERO;
      int comparison = leftNumber.compareTo(rightNumber);
      if (comparison != 0) {
        return comparison;
      }
    }
    // A final release precedes a prerelease of the same numeric version in discovery order.
    String leftSuffix = leftMatch.group(2);
    String rightSuffix = rightMatch.group(2);
    if (leftSuffix == null && rightSuffix != null) return 1;
    if (leftSuffix != null && rightSuffix == null) return -1;
    return left.getFileName().toString().compareTo(right.getFileName().toString());
  }
}
