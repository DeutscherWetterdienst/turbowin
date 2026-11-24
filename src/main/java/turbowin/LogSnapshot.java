package turbowin;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Loads the recent records shown by the system and device log windows. */
final class LogSnapshot {
  record Content(String description, String text) {}

  private static final DateTimeFormatter MONTH =
      DateTimeFormatter.ofPattern("MMM_yyyy", Locale.ENGLISH).withZone(ZoneOffset.UTC);
  private static final DateTimeFormatter HOUR =
      DateTimeFormatter.ofPattern("yyyyMMddHH").withZone(ZoneOffset.UTC);

  private record Cached(
      Object key, long size, java.nio.file.attribute.FileTime modified, String text) {}

  private final Map<Path, Cached> cache = new HashMap<>();

  Content system(Path logs, Instant now) {
    Path path =
        logs.resolve(main.TURBOWIN_SYSTEM_LOGS_DIR)
            .resolve("turbowin_system_" + MONTH.format(now) + ".txt");
    return new Content(path.toString(), read(path, 5000, "No system log found for this month"));
  }

  Content device(
      Path logs, Instant now, int primary, int secondary, int gps, String ship, String[] gpsLines) {
    StringBuilder text = new StringBuilder("--- GENERAL ---\n");
    text.append("ship name: ").append(ship).append('\n');
    text.append("connected device(s) according user settings: ")
        .append(devices(primary, secondary, gps))
        .append('\n');
    text.append("Raw received records include the UTC storage timestamp (YYYYMMDDHHMM).\n");
    if (primary != 0) appendSensor(text, logs, now, false);
    if (secondary != 0) appendSensor(text, logs, now, true);
    if (gps == 1) {
      text.append("\n--- RECEIVED GPS DATA ---\n");
      for (int i = gpsLines.length - 1; i >= 0; i--) {
        String line = gpsLines[i];
        if (line != null && !line.isBlank()) {
          text.append(line);
          if (!line.endsWith("\n")) text.append('\n');
        }
      }
    }
    return new Content(logs.toString(), text.toString());
  }

  private void appendSensor(StringBuilder text, Path logs, Instant now, boolean secondary) {
    String prefix = secondary ? "sensor_data_II_" : "sensor_data_";
    Path path = logs.resolve(prefix + HOUR.format(now) + ".txt");
    // At an hour boundary the previous file can still receive delayed sensor records.
    Path previous = logs.resolve(prefix + HOUR.format(now.minusSeconds(120)) + ".txt");
    if (!Files.exists(path) && Files.exists(previous)) path = previous;
    text.append(
        secondary ? "\n--- RECEIVED DATA 2ND METEO INSTRUMENT ---\n" : "\n--- RECEIVED DATA ---\n");
    text.append(read(path, 100, "No device data file found")).append('\n');
  }

  private String read(Path path, int limit, String missing) {
    Deque<String> lines = new ArrayDeque<>(limit);
    try {
      BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
      Cached previous = cache.get(path);
      if (previous != null
          && java.util.Objects.equals(previous.key(), attributes.fileKey())
          && previous.size() == attributes.size()
          && previous.modified().equals(attributes.lastModifiedTime())) return previous.text();
      try (BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset())) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (lines.size() == limit) lines.removeFirst();
          lines.addLast(line);
        }
      }
      String text = lines.isEmpty() ? "No data in " + path : String.join("\n", lines);
      if (cache.size() >= 8 && !cache.containsKey(path)) cache.clear();
      cache.put(
          path,
          new Cached(attributes.fileKey(), attributes.size(), attributes.lastModifiedTime(), text));
      return text;
    } catch (java.nio.file.NoSuchFileException ex) {
      cache.remove(path);
      return missing + " (" + path + ")";
    } catch (IOException | SecurityException ex) {
      cache.remove(path);
      return "Unable to read " + path + ": " + ex.getMessage();
    }
  }

  private static String devices(int primary, int secondary, int gps) {
    String name =
        switch (primary) {
          case 1 -> "Vaisala PTB220 serial";
          case 2 -> "Vaisala PTB330 serial";
          case 3 -> "EUCAWS AWS serial";
          case 4 -> "Mintaka DUO USB";
          case 5 -> "Mintaka Star USB";
          case 6 -> "Mintaka Star LAN";
          case 7 -> "Mintaka Star serial + Mintaka StarX WiFi";
          case 8 -> "Mintaka Star LAN + Mintaka StarX WiFi";
          case 9 -> "OMC-140 AWS serial";
          case 10 -> "OMC-140 AWS LAN";
          case 11 -> "AMOS2X AWS serial";
          default -> "";
        };
    if (secondary == 1) name += (name.isEmpty() ? "" : " + ") + "Vaisala HMP155 USB";
    if (gps == 1) name += (name.isEmpty() ? "" : " + ") + "GPS (NMEA 0183)";
    return name.isEmpty() ? "none" : name;
  }
}
