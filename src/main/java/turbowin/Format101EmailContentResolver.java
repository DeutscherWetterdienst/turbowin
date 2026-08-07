package turbowin;

import java.io.File;

/** Selects the body text used when emailing a Format 101 observation. */
final class Format101EmailContentResolver {

  private Format101EmailContentResolver() {}

  static String resolve(String mode, File compressedFile) {
    if (main.FORMAT_101_BODY.equals(mode)) {
      return Format101FileReader.readFirstLine(compressedFile);
    }

    if (main.FORMAT_101_ATTACHEMENT.equals(mode) && compressedFile.exists()) {
      return "Please attach manually the file: " + compressedFile.getPath();
    }

    return "";
  }
}
