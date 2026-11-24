package turbowin;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LogSnapshotTest {
  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void systemLogTracksCreationReplacementAndMonthChange() throws Exception {
    Path root = folder.getRoot().toPath();
    Path directory = Files.createDirectory(root.resolve(main.TURBOWIN_SYSTEM_LOGS_DIR));
    Instant november = Instant.parse("2026-11-30T23:59:59Z");
    LogSnapshot reader = new LogSnapshot();
    assertTrue(reader.system(root, november).text().contains("No system log"));
    Path file = directory.resolve("turbowin_system_Nov_2026.txt");
    Files.writeString(file, "first\n");
    assertEquals("first", reader.system(root, november).text());
    Files.writeString(file, "replacement\n");
    assertEquals("replacement", reader.system(root, november).text());
    StringBuilder records = new StringBuilder();
    for (int i = 0; i < 5002; i++) records.append("line ").append(i).append('\n');
    Files.writeString(file, records);
    String recent = reader.system(root, november).text();
    assertTrue(recent.startsWith("line 2\n"));
    assertTrue(recent.endsWith("line 5001"));
    Files.writeString(directory.resolve("turbowin_system_Dec_2026.txt"), "new month\n");
    assertEquals("new month", reader.system(root, november.plusSeconds(1)).text());
  }

  @Test
  public void deviceLogIncludesBothSensorsAndLatestGpsAndSwitchesHour() throws Exception {
    Path root = folder.getRoot().toPath();
    Instant now = Instant.parse("2026-11-20T12:59:59Z");
    Path primary = root.resolve("sensor_data_2026112012.txt");
    StringBuilder lines = new StringBuilder();
    for (int i = 0; i < 105; i++) lines.append("record ").append(i).append('\n');
    Files.writeString(primary, lines);
    Files.writeString(root.resolve("sensor_data_II_2026112012.txt"), "secondary\n");
    LogSnapshot reader = new LogSnapshot();
    String text =
        reader.device(root, now, 3, 1, 1, "Ship", new String[] {"new GPS", "old GPS"}).text();
    assertFalse(text.contains("record 4\n"));
    assertTrue(text.contains("record 5\n"));
    assertTrue(text.contains("record 104"));
    assertTrue(text.contains("secondary"));
    assertTrue(text.indexOf("old GPS") < text.indexOf("new GPS"));
    Files.writeString(root.resolve("sensor_data_2026112013.txt"), "next hour\n");
    assertTrue(
        reader
            .device(root, now.plusSeconds(1), 3, 1, 0, "Ship", new String[0])
            .text()
            .contains("next hour"));
  }
}
