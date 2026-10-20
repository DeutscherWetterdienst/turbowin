package turbowin;

/** Preserves the legacy substring-based matching of configuration lines. */
final class ConfigurationLineMatcher {

  private ConfigurationLineMatcher() {}

  static boolean contains(String line, String key) {
    return line.indexOf(key) != -1;
  }
}
