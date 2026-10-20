package turbowin;

/** Converts legacy configuration values into their typed representations. */
final class ConfigurationValueParser {

  private ConfigurationValueParser() {}

  static int integer(String value) {
    return Integer.parseInt(value);
  }

  static boolean bool(String value) {
    return Boolean.valueOf(value);
  }
}
