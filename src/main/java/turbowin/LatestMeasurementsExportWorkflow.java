package turbowin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Exports a snapshot of the displayed measurements as JSON. */
final class LatestMeasurementsExportWorkflow {
  private static final DateTimeFormatter TIME =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH).withZone(ZoneOffset.UTC);

  private LatestMeasurementsExportWorkflow() {}

  static void write(
      Path path, String ship, List<String> headers, List<List<String>> rows, Instant now)
      throws IOException {
    try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      out.write(
          "{\n  \"ship name\": "
              + quote(ship)
              + ",\n  \"date/time export\": "
              + quote(TIME.format(now) + " UTC")
              + ",\n  \"measurements\": [\n");
      for (int row = 0; row < rows.size(); row++) {
        if (row > 0) out.write(",\n");
        out.write("    {");
        for (int column = 0; column < headers.size(); column++) {
          if (column > 0) out.write(", ");
          out.write(quote(headers.get(column)) + ": " + quote(rows.get(row).get(column)));
        }
        out.write("}");
      }
      out.write("\n  ]\n}\n");
    }
  }

  private static String quote(String text) {
    StringBuilder result = new StringBuilder("\"");
    for (char c : (text == null ? "" : text).toCharArray()) {
      switch (c) {
        case '"' -> result.append("\\\"");
        case '\\' -> result.append("\\\\");
        case '\n' -> result.append("\\n");
        case '\r' -> result.append("\\r");
        case '\t' -> result.append("\\t");
        default -> {
          if (c < 0x20) result.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
          else result.append(c);
        }
      }
    }
    return result.append('"').toString();
  }
}
