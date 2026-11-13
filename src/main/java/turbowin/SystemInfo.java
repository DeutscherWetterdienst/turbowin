package turbowin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Provides concise, non-identifying runtime information for support diagnostics. */
final class SystemInfo {

  private SystemInfo() {}

  static String operatingSystem() {
    return property("os.name")
        + " version "
        + property("os.version")
        + " running on "
        + property("os.arch")
        + " (JVM "
        + property("sun.arch.data.model")
        + "-bit)";
  }

  static String javaRuntime() {
    return property("java.runtime.version")
        + "; "
        + property("java.vm.name")
        + "; vendor "
        + property("java.vendor");
  }

  static String linuxDistribution() {
    if (!property("os.name").toLowerCase().contains("linux")) {
      return "";
    }

    try {
      List<String> lines = Files.readAllLines(Path.of("/etc/os-release"));
      for (String line : lines) {
        if (line.startsWith("PRETTY_NAME=")) {
          return unquote(line.substring("PRETTY_NAME=".length()));
        }
      }
    } catch (IOException | SecurityException ignored) {
      // Distribution details are optional diagnostics.
    }
    return "";
  }

  static String desktopEnvironment() {
    String desktop = System.getenv("XDG_CURRENT_DESKTOP");
    if (desktop == null || desktop.isBlank()) {
      desktop = System.getenv("XDG_SESSION_DESKTOP");
    }
    return desktop == null ? "" : desktop;
  }

  private static String property(String name) {
    return System.getProperty(name, "unknown");
  }

  private static String unquote(String value) {
    if (value.length() >= 2
        && ((value.startsWith("\"") && value.endsWith("\""))
            || (value.startsWith("'") && value.endsWith("'")))) {
      return value.substring(1, value.length() - 1);
    }
    return value;
  }
}
