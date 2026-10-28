package turbowin;

import static turbowin.main.CONFIGURATION_FILE;
import static turbowin.main.OFFLINE_AMVER_DIR;
import static turbowin.main.OFFLINE_LOGS_DIR;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

/** Copies user data from the storage locations used by released TurboWin+ versions. */
final class LegacyDataMigration {

  private static final List<String> USER_DATA_ENTRIES =
      List.of(CONFIGURATION_FILE, OFFLINE_LOGS_DIR, OFFLINE_AMVER_DIR);

  private LegacyDataMigration() {}

  static void migrate(Iterable<Path> legacyDirectories, Path targetDirectory) {
    if (targetDirectory == null) {
      return;
    }

    Path normalizedTarget = targetDirectory.toAbsolutePath().normalize();
    for (Path legacyDirectory : legacyDirectories) {
      if (legacyDirectory == null) {
        continue;
      }
      Path normalizedLegacy = legacyDirectory.toAbsolutePath().normalize();
      if (normalizedLegacy.equals(normalizedTarget)) {
        continue;
      }

      for (String entry : USER_DATA_ENTRIES) {
        try {
          mergeIfAbsent(normalizedLegacy.resolve(entry), normalizedTarget.resolve(entry));
        } catch (IOException | SecurityException ex) {
          // Migration must not prevent the application from starting. The normal startup path
          // will report a write-permission problem if the new data root is unusable.
          System.out.println(
              "[GENERAL] unable to migrate legacy data from "
                  + normalizedLegacy
                  + " to "
                  + normalizedTarget
                  + ": "
                  + ex);
        }
      }
    }
  }

  private static void mergeIfAbsent(Path source, Path target) throws IOException {
    if (!Files.exists(source, LinkOption.NOFOLLOW_LINKS)) {
      return;
    }

    if (Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
      if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)
          && !Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS)) {
        return;
      }
      Files.createDirectories(target);
      try (DirectoryStream<Path> children = Files.newDirectoryStream(source)) {
        for (Path child : children) {
          mergeIfAbsent(child, target.resolve(child.getFileName()));
        }
      }
      return;
    }

    if (Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)
        && Files.notExists(target, LinkOption.NOFOLLOW_LINKS)) {
      Files.createDirectories(target.getParent());
      Files.copy(source, target);
    }
  }
}
