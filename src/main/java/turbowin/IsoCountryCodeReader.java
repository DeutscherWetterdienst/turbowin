package turbowin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** Reads the fixed-size ISO-country-code list used by the station-data form. */
final class IsoCountryCodeReader {

  private IsoCountryCodeReader() {}

  static String[] read(InputStream input, int maximumRecords) throws IOException {
    String[] records = new String[maximumRecords];
    for (int i = 0; i < maximumRecords; i++) {
      records[i] = "";
    }

    try (BufferedReader in = new BufferedReader(new InputStreamReader(input))) {
      int recordIndex = 0;
      String fileLine;
      while ((fileLine = in.readLine()) != null) {
        if (fileLine.length() > 3) {
          fileLine = fileLine.replaceAll("\t", "");
          records[recordIndex] = fileLine;
          recordIndex++;
        }

        if (recordIndex >= maximumRecords) {
          break;
        }
      }
    }

    return records;
  }
}
