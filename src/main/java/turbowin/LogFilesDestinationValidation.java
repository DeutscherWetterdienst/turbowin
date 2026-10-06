package turbowin;

import java.io.File;

/** Handles the filesystem checks for a log-file destination directory. */
final class LogFilesDestinationValidation {

  private LogFilesDestinationValidation() {}

  static boolean ensureDirectory(String path) {
    File directory = new File(path);
    return directory.exists() || directory.mkdirs();
  }

  static boolean isSameDirectory(String destination, String source) {
    return destination.equals(source);
  }
}
