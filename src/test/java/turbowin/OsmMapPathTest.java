package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.Test;

public class OsmMapPathTest {

  @Test
  public void buildsMapFilePathInsideOsmDirectory() {
    assertEquals(
        "logs" + File.separator + "OSM" + File.separator + "position_leaflet_maps.html",
        OsmMapPath.file("logs", "OSM", "position_leaflet_maps.html"));
  }
}
