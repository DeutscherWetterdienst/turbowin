package format101;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Format101TxtParser {

    private Format101TxtParser() {}

    public record ParsedLine(boolean present, Double value) {}

    public static ParsedLine[] parse(Path path, int expectedCount) throws IOException {
        var lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Empty format_101.txt: " + path);
        }
        if (!lines.get(0).trim().equals("0")) {
            throw new IllegalArgumentException("Invalid first line in " + path + " (expected '0'): " + lines.get(0));
        }

        java.util.ArrayList<ParsedLine> parsed = new java.util.ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String raw = lines.get(i).trim();
            if (raw.isEmpty()) {
                continue;
            }
            String[] parts = raw.split("\\s+");
            if (parts[0].equals("0")) {
                parsed.add(new ParsedLine(false, null));
            } else if (parts[0].equals("1")) {
                if (parts.length < 2) {
                    throw new IllegalArgumentException("Invalid present line (missing value): " + lines.get(i) + " in " + path);
                }
                parsed.add(new ParsedLine(true, Double.parseDouble(parts[1])));
            } else {
                throw new IllegalArgumentException("Invalid line (expected 0/1): " + lines.get(i) + " in " + path);
            }
        }

        if (parsed.size() != expectedCount) {
            throw new IllegalArgumentException("Input value line count mismatch: expected " + expectedCount + " entries, got " + parsed.size());
        }

        return parsed.toArray(new ParsedLine[0]);
    }
}
