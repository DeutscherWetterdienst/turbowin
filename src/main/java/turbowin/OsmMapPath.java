package turbowin;

import java.io.File;

/** Builds paths for generated OSM map documents. */
final class OsmMapPath {

  private OsmMapPath() {}

  static String file(String logsDirectory, String osmDirectory, String mapFileName) {
    return logsDirectory + File.separator + osmDirectory + File.separator + mapFileName;
  }
}
