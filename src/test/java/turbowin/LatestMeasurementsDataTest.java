package turbowin;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LatestMeasurementsDataTest {
  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void readsHourlyRecordsNewestFirstAndFormatsWind() throws Exception {
    String oldLogs = main.logs_dir;
    try {
      main.logs_dir = folder.getRoot().getAbsolutePath();
      Instant now = Instant.now();
      Instant previous = now.minusSeconds(3600);
      Files.writeString(
          sensorFile(folder.getRoot().toPath(), previous), record(previous, "00", "4.2"));
      Files.writeString(
          sensorFile(folder.getRoot().toPath(), now),
          record(now, "00", "5.0") + record(now, "05", "99.0"));
      String[][] snapshot = LatestMeasurementsData.loadSnapshot();
      List<List<String>> rows = LatestMeasurementsTable.rows(snapshot, false);
      assertEquals(2, rows.size());
      assertEquals("5.00", rows.get(0).get(LatestMeasurementsData.true_wind_speed_index));
      assertEquals("4.20", rows.get(1).get(LatestMeasurementsData.true_wind_speed_index));
      assertEquals("00", rows.get(0).get(LatestMeasurementsData.time_index).substring(3));
      assertEquals(
          String.format(java.util.Locale.ROOT, "%.2f", 5 * main.M_S_KNOT_CONVERSION),
          LatestMeasurementsTable.rows(snapshot, true)
              .get(0)
              .get(LatestMeasurementsData.true_wind_speed_index));
      assertTrue(LatestMeasurementsTable.headers(true).get(8).contains("kts"));
    } finally {
      main.logs_dir = oldLogs;
    }
  }

  @Test
  public void exportsValidEscapedJsonForDisplayedValues() throws Exception {
    Path file = folder.getRoot().toPath().resolve("measurements.json");
    List<String> headers = LatestMeasurementsTable.headers(false);
    List<String> row =
        List.of("2026-11-20", "12:00", "52", "4", "1013", "12", "75", "10", "5.00", "180", "6.00");
    LatestMeasurementsExportWorkflow.write(
        file, "Ship \"A\"\\B", headers, List.of(row), Instant.parse("2026-11-20T12:30:00Z"));
    String json = Files.readString(file);
    assertTrue(json.contains("\"ship name\": \"Ship \\\"A\\\"\\\\B\""));
    assertTrue(json.contains("\"date/time export\": \"20-Nov-2026 12:30:00 UTC\""));
    assertTrue(json.contains("\"wind speed true [m/s]\": \"5.00\""));
    assertFalse(json.contains("},\n  ]"));
  }

  static Path sensorFile(Path root, Instant hour) {
    String stamp = DateTimeFormatter.ofPattern("yyyyMMddHH").withZone(ZoneOffset.UTC).format(hour);
    return root.resolve("sensor_data_" + stamp + ".txt");
  }

  static String record(Instant hour, String minute, String windSpeed) {
    String stamp = DateTimeFormatter.ofPattern("yyyyMMddHH").withZone(ZoneOffset.UTC).format(hour);
    return "$PEUMB,"
        + stamp.substring(0, 8)
        + ","
        + stamp.substring(8)
        + minute
        + "00,45.960,-2.124,128.3,10.8,131.6,997.4,1000.5,6.0,2,20.5,53.0,9.3,3.1,128.3,"
        + windSpeed
        + ",350.3,7.8,358.3,23.1,28.3,-10,,,,"
        + stamp
        + minute
        + "\n";
  }
}
