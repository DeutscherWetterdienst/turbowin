package turbowin;

/**
 * URL formatting helpers used by the legacy output workflows.
 *
 * <p>This encoding is used when constructing mailto URLs for cross-platform fallback mail-program
 * launching. In particular, mailto bodies use {@code %0A} for line breaks and {@code %20} for
 * spaces. Keep the deliberately legacy encoding contract here rather than replacing it with
 * ordinary display formatting.
 */
final class UrlUtils {

  private UrlUtils() {}

  static String urlEncode(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (Character.isLetterOrDigit(ch)) {
        sb.append(ch);
      } else {
        sb.append(String.format("%%%02X", (int) ch));
      }
    }

    return sb.toString();
  }
}
