package turbowin;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/** Builds paths for generated OSM map documents. */
final class OsmMapPath {

  private OsmMapPath() {}

  static String file(String logsDirectory, String osmDirectory, String mapFileName) {
    return logsDirectory + File.separator + osmDirectory + File.separator + mapFileName;
  }

  static Path installedAssets(String javaHome) {
    Path directory = Path.of(javaHome, "OSM");
    return Files.isRegularFile(directory.resolve("leaflet.js"))
            && Files.isRegularFile(directory.resolve("leaflet.css"))
            && Files.isRegularFile(directory.resolve("marker_red_meet.png"))
            && Files.isDirectory(directory.resolve("OSMPublicTransport"))
        ? directory
        : null;
  }

  static Path assets() {
    Path installed = installedAssets(System.getProperty("java.home"));
    if (installed != null) {
      return installed;
    }
    // Gradle development and test runs use unpacked classpath resources.
    URL resource = OsmMapPath.class.getResource("OSM/leaflet.js");
    if (resource != null && "file".equals(resource.getProtocol())) {
      try {
        return Path.of(resource.toURI()).getParent();
      } catch (URISyntaxException | IllegalArgumentException ex) {
        return null;
      }
    }
    return null;
  }
}
