package turbowin;

/** Extracts values from the legacy fixed-width configuration-line format. */
final class ConfigurationLineParser {

  private ConfigurationLineParser() {}

  static String value(String line) {
    return line.substring(main.CONFIGURATION_FILE_POS_INHOUD);
  }
}
