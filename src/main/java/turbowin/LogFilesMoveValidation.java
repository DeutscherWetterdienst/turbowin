package turbowin;

import java.io.File;

/** Applies the legacy precondition for moving the IMMT log. */
final class LogFilesMoveValidation {

  private LogFilesMoveValidation() {}

  static boolean hasUsableImmtLog(String path) {
    File immtSourceFile = new File(path);
    return immtSourceFile.exists() && immtSourceFile.length() > 10;
  }
}
