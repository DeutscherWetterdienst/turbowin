package format101;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class SpecLoader {

    private SpecLoader() {}

    public static List<PilotEntry> loadPiloteCsv(Path path) throws IOException {
        String text;
        try {
            text = Files.readString(path, Charset.forName("UTF-8"));
        } catch (MalformedInputException e) {
            text = Files.readString(path, Charset.forName("ISO-8859-1"));
        }

        List<PilotEntry> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new java.io.StringReader(text))) {
            String raw;
            while ((raw = br.readLine()) != null) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] cols = line.split(";");
                if (cols.length < 7) {
                    throw new IOException("Invalid pilote csv line (expected >= 7 columns): " + raw);
                }
                String bufr = cols[0].trim();
                String ref = cols[1].trim();
                int nbits = Integer.parseInt(cols[2].trim());
                double factor = parseDouble(cols[4]);
                double offset = parseDouble(cols[5]);
                int codmax = (int) Double.parseDouble(cols[6].trim());

                entries.add(new PilotEntry(bufr, ref, nbits, factor, offset, codmax));
            }
        }
        return entries;
    }

    private static double parseDouble(String s) {
        String t = s == null ? "" : s.trim();
        if (t.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(t);
    }

    public static List<PilotEntry> skipOperatingMode000000(List<PilotEntry> entries) {
        if (!entries.isEmpty()) {
            PilotEntry first = entries.get(0);
            if ("000000".equals(first.bufr()) && (first.ref() == null || first.ref().isEmpty())) {
                return entries.subList(1, entries.size());
            }
        }
        return entries;
    }
}
