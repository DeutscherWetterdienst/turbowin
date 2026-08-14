package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
  public void writesMarkerForValidObservation() throws Exception {
    String html =
        writeObservationMap(main.OSM_OFFLINE_MANUAL, Collections.singletonList(validObservation()));

    assertTrue(html.contains("L.marker([52.4,4.4], markerOptions).addTo(map).bindPopup(\"<h3>"));
    assertTrue(html.contains("TESTNL weather observation"));
  }

  @Test
  public void skipsObservationWithInvalidPosition() throws Exception {
    String observation = validObservation();
    observation = observation.substring(0, 12) + "52X" + observation.substring(15);

    String html =
        writeObservationMap(main.OSM_OFFLINE_MANUAL, Collections.singletonList(observation));

    assertFalse(html.contains("L.marker(["));
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
    assertFalse(html.contains("L.marker(["));
    assertTrue(html.endsWith("</html>\n"));
  }

  @Test
  public void writesMarkerForValidAwsMeasurement() throws Exception {
    String html = writeAwsSensorMap(main.OSM_OFFLINE_AWS_SENSOR, true);

    assertTrue(html.contains("L.marker([52.4,4.4], markerOptions).addTo(map).bindPopup(\"<h3>"));
    assertTrue(html.contains("TEST AWS sensor"));
  }

  private String writeObservationMap(String mode) throws Exception {
    return writeObservationMap(mode, Collections.emptyList());
  }

  private String writeObservationMap(String mode, List<String> observations) throws Exception {
    File output = Files.createTempFile("turbowin-observation-map", ".html").toFile();
    String previousMode = main.OSM_mode;
    try {
      main.OSM_mode = mode;
      invokePrivateWriter(
          "OSM_IMMT_Obsen_on_Map",
          new Class<?>[] {java.util.List.class, String.class},
          observations,
          output.getAbsolutePath());
      return Files.readString(output.toPath());
    } finally {
      main.OSM_mode = previousMode;
      Files.deleteIfExists(output.toPath());
    }
  }

  private String writeAwsSensorMap(String mode) throws Exception {
    return writeAwsSensorMap(mode, false);
  }

  private String writeAwsSensorMap(String mode, boolean withMeasurement) throws Exception {
    File output = Files.createTempFile("turbowin-aws-map", ".html").toFile();
    String previousMode = main.OSM_mode;
    String previousStationId = main.station_ID;
    String previousWindUnits = main.wind_units_dashboard;
    try {
      main.OSM_mode = mode;
      for (String[] measurement : mylatestmeasurements.AWS_array) {
        Arrays.fill(measurement, "");
      }
      if (withMeasurement) {
        main.station_ID = "TEST";
        main.wind_units_dashboard = "";
        String[] measurement = mylatestmeasurements.AWS_array[0];
        measurement[mylatestmeasurements.date_index] = "20260115";
        measurement[mylatestmeasurements.time_index] = "123000";
        measurement[mylatestmeasurements.lat_index] = "52.4";
        measurement[mylatestmeasurements.lon_index] = "4.4";
        measurement[mylatestmeasurements.slp_index] = "1013";
        measurement[mylatestmeasurements.air_temp_index] = "12";
        measurement[mylatestmeasurements.sst_index] = "10";
        measurement[mylatestmeasurements.true_wind_speed_index] = "5";
        measurement[mylatestmeasurements.true_wind_dir_index] = "180";
      }
      invokePrivateWriter(
          "OSM_AWS_Sensor_Obsen_on_Map", new Class<?>[] {String.class}, output.getAbsolutePath());
      return Files.readString(output.toPath());
    } finally {
      main.OSM_mode = previousMode;
      main.station_ID = previousStationId;
      main.wind_units_dashboard = previousWindUnits;
      Files.deleteIfExists(output.toPath());
    }
  }

  private void invokePrivateWriter(String name, Class<?>[] parameterTypes, Object... arguments)
      throws Exception {
    Method writer = OSM.class.getDeclaredMethod(name, parameterTypes);
    writer.setAccessible(true);
    writer.invoke(new OSM(), arguments);
  }

  private String validObservation() {
    char[] observation = new char[100];
    Arrays.fill(observation, '9');
    set(observation, 1, "2026");
    set(observation, 5, "01");
    set(observation, 7, "15");
    set(observation, 9, "12");
    set(observation, 11, "1");
    set(observation, 12, "524");
    set(observation, 15, "0044");
    set(observation, 24, "18");
    set(observation, 26, "3");
    set(observation, 27, "05");
    set(observation, 29, "0");
    set(observation, 30, "012");
    set(observation, 37, "1013");
    set(observation, 49, "0");
    set(observation, 50, "015");
    set(observation, 71, "TESTNL ");
    return new String(observation);
  }

  private void set(char[] value, int offset, String replacement) {
    replacement.getChars(0, replacement.length(), value, offset);
  }
}
