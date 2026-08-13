package turbowin;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class OsmMapHtmlCharacterizationTest {

  @Test
  public void writesOnlineObservationMapShell() throws Exception {
    String html = writeObservationMap(main.OSM_ONLINE_MANUAL);

    assertTrue(html.contains("<title>TurboWin+ Obs's Map (internet)</title>"));
    assertTrue(html.contains(main.LEAFLET_CSS_URL));
    assertTrue(html.contains("L.esri.basemapLayer(\"Topographic\").addTo(map);"));
    assertTrue(html.endsWith("</html>\n"));
  }

  @Test
  public void writesOfflineObservationMapShell() throws Exception {
    String html = writeObservationMap(main.OSM_OFFLINE_MANUAL);

    assertTrue(html.contains("<title>TurboWin+ Obs's Map (offline)</title>"));
    assertTrue(html.contains("href=\"leaflet.css\""));
    assertTrue(html.contains("L.tileLayer('OSMPublicTransport/{z}/{x}/{y}.png'"));
    assertTrue(html.endsWith("</html>\n"));
  }

  @Test
  public void writesOnlineAwsSensorMapShell() throws Exception {
    String html = writeAwsSensorMap(main.OSM_ONLINE_AWS_SENSOR);

    assertTrue(html.contains("<title>TurboWin+ AWS sensor Map (internet)</title>"));
    assertTrue(html.contains(main.LEAFLET_ESRI_URL));
    assertTrue(html.contains("L.esri.basemapLayer(\"Topographic\").addTo(map);"));
    assertTrue(html.endsWith("</html>\n"));
  }

  @Test
  public void writesOfflineAwsSensorMapShell() throws Exception {
    String html = writeAwsSensorMap(main.OSM_OFFLINE_AWS_SENSOR);

    assertTrue(html.contains("<title>TurboWin+ AWS sensor Map (offline)</title>"));
    assertTrue(html.contains("src=\"leaflet.js\""));
    assertTrue(html.contains("L.tileLayer('OSMPublicTransport/{z}/{x}/{y}.png'"));
    assertTrue(html.endsWith("</html>\n"));
  }

  private String writeObservationMap(String mode) throws Exception {
    File output = Files.createTempFile("turbowin-observation-map", ".html").toFile();
    String previousMode = main.OSM_mode;
    try {
      main.OSM_mode = mode;
      invokePrivateWriter(
          "OSM_IMMT_Obsen_on_Map",
          new Class<?>[] {java.util.List.class, String.class},
          Collections.emptyList(),
          output.getAbsolutePath());
      return Files.readString(output.toPath());
    } finally {
      main.OSM_mode = previousMode;
      Files.deleteIfExists(output.toPath());
    }
  }

  private String writeAwsSensorMap(String mode) throws Exception {
    File output = Files.createTempFile("turbowin-aws-map", ".html").toFile();
    String previousMode = main.OSM_mode;
    try {
      main.OSM_mode = mode;
      for (String[] measurement : mylatestmeasurements.AWS_array) {
        Arrays.fill(measurement, "");
      }
      invokePrivateWriter(
          "OSM_AWS_Sensor_Obsen_on_Map", new Class<?>[] {String.class}, output.getAbsolutePath());
      return Files.readString(output.toPath());
    } finally {
      main.OSM_mode = previousMode;
      Files.deleteIfExists(output.toPath());
    }
  }

  private void invokePrivateWriter(String name, Class<?>[] parameterTypes, Object... arguments)
      throws Exception {
    Method writer = OSM.class.getDeclaredMethod(name, parameterTypes);
    writer.setAccessible(true);
    writer.invoke(new OSM(), arguments);
  }
}
