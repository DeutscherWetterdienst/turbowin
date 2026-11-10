package turbowin;

import static turbowin.main.CONFIGURATION_FILE;
import static turbowin.main.LOGS_DIR_TXT;
import static turbowin.main.OFFLINE_AMVER_DIR;
import static turbowin.main.OFFLINE_LOGS_DIR;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Copies legacy user data without overwriting current files or modifying the source installation.
 */
final class LegacyDataMigration {

  private static final List<String> USER_DATA_DIRECTORIES =
      List.of(OFFLINE_LOGS_DIR, OFFLINE_AMVER_DIR);

  private LegacyDataMigration() {}

  static void migrate(Iterable<Path> legacyDirectories, Path targetDirectory) {
    if (targetDirectory == null) {
      return;
    }
    Path target = targetDirectory.toAbsolutePath().normalize();
    // Never introduce a preferred legacy logs copy beside an existing modern root copy.
    boolean configurationSelected = hasConfiguration(target);
    for (Path legacyDirectory : legacyDirectories) {
      if (legacyDirectory == null) {
        continue;
      }
      Path legacy = legacyDirectory.toAbsolutePath().normalize();
      if (legacy.startsWith(target)) {
        continue;
      }
      if (!configurationSelected && hasConfiguration(legacy)) {
        ConfigurationLoader.Result selected =
            ConfigurationLoader.load(
                legacy.resolve(OFFLINE_LOGS_DIR).toString(), legacy.toString());
        selected.messages().forEach(System.out::println);
        if (selected.loaded()) {
          configurationSelected = true;
          String[] lines = selected.lines().clone();
          boolean defaultLogs = rebaseInstallationLogs(lines, legacy, target);
          List<Path> destinations = new ArrayList<>();
          destinations.add(target.resolve(CONFIGURATION_FILE));
          // With a custom logs folder, this copy would never be updated by subsequent saves.
          if (defaultLogs) {
            destinations.add(target.resolve(OFFLINE_LOGS_DIR).resolve(CONFIGURATION_FILE));
          }
          for (Path destination : destinations) {
            try {
              copyConfigurationIfAbsent(lines, destination);
            } catch (IOException | SecurityException ex) {
              reportFailure(selected.source(), destination, ex);
            }
          }
        }
      }
      for (String entry : USER_DATA_DIRECTORIES) {
        try {
          // Configuration has its own validated selection above, rather than a blind file copy.
          mergeIfAbsent(
              legacy.resolve(entry),
              target.resolve(entry),
              legacy.resolve(OFFLINE_LOGS_DIR).resolve(CONFIGURATION_FILE));
        } catch (IOException | DirectoryIteratorException | SecurityException ex) {
          reportFailure(legacy.resolve(entry), target.resolve(entry), ex);
        }
      }
    }
  }

  private static boolean hasConfiguration(Path directory) {
    return Files.exists(directory.resolve(CONFIGURATION_FILE), LinkOption.NOFOLLOW_LINKS)
        || Files.exists(
            directory.resolve(OFFLINE_LOGS_DIR).resolve(CONFIGURATION_FILE),
            LinkOption.NOFOLLOW_LINKS);
  }

  private static boolean rebaseInstallationLogs(String[] lines, Path legacy, Path target) {
    boolean defaultLogs = true;
    for (int index = 0; index < lines.length; index++) {
      String line = lines[index];
      if (line != null && line.startsWith(LOGS_DIR_TXT)) {
        String value = ConfigurationLineParser.value(line);
        if (pointsToInstallationLogs(value, legacy)) {
          value = target.resolve(OFFLINE_LOGS_DIR).toString();
        } else if (!value.isEmpty()) {
          try {
            Path custom = Path.of(value);
            if (!custom.isAbsolute()) {
              // Keep a relative custom folder attached to its original installation.
              value = legacy.resolve(custom).normalize().toString();
            }
          } catch (InvalidPathException ex) {
            // Preserve unresolvable custom values, as ordinary configuration loading does.
          }
        }
        lines[index] = LOGS_DIR_TXT + value;
        try {
          defaultLogs = Path.of(value).normalize().equals(target.resolve(OFFLINE_LOGS_DIR));
        } catch (InvalidPathException ex) {
          defaultLogs = false;
        }
      }
    }
    return defaultLogs;
  }

  private static boolean pointsToInstallationLogs(String value, Path legacy) {
    try {
      Path saved = Path.of(value);
      if (!saved.isAbsolute()) {
        saved = legacy.resolve(saved);
      }
      Path legacyLogs = legacy.resolve(OFFLINE_LOGS_DIR);
      if (saved.normalize().equals(legacyLogs)) {
        return true;
      }
      return Files.exists(saved) && Files.exists(legacyLogs) && Files.isSameFile(saved, legacyLogs);
    } catch (IOException | InvalidPathException | SecurityException ex) {
      // An unresolvable custom path is not evidence that it was installation-local.
      return false;
    }
  }

  private static void copyConfigurationIfAbsent(String[] lines, Path target) throws IOException {
    if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
      return;
    }
    Files.createDirectories(target.getParent());
    // Match the regular-file permissions of ordinary saves rather than createTempFile's 0600.
    Path temporary = ConfigurationFileStore.createTemporaryFile(target);
    try {
      ConfigurationFileStore.write(temporary.toFile(), lines, lines.length);
      // No REPLACE_EXISTING: a file created since the initial check must be preserved.
      Files.move(temporary, target);
      System.out.println("[MIGRATION] copied configuration to '" + target + "'");
    } catch (FileAlreadyExistsException ex) {
      // Another save or migration populated this destination; keep it.
    } finally {
      Files.deleteIfExists(temporary);
    }
  }

  private static void mergeIfAbsent(Path source, Path target, Path excludedConfiguration)
      throws IOException {
    if (source.equals(excludedConfiguration)
        || target.startsWith(source)
        || !Files.exists(source, LinkOption.NOFOLLOW_LINKS)) {
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
          mergeIfAbsent(child, target.resolve(child.getFileName()), excludedConfiguration);
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

  private static void reportFailure(Path source, Path target, Exception error) {
    // Startup will report separately if the new data root cannot be written at all.
    System.out.println("[MIGRATION] unable to copy '" + source + "' to '" + target + "': " + error);
  }
}
