package turbowin;

import java.io.BufferedReader;
import java.io.IOException;

/** Reads the legacy semicolon-separated captain and observer log format. */
final class SemicolonLogFileReader {

  private SemicolonLogFileReader() {}

  static void read(BufferedReader in, String[][] data, int rows, int columns) throws IOException {
    String record;
    int r = 0;

    while ((record = in.readLine()) != null) {
      int pos_begin = 0;
      int pos_eind = 0;
      for (int c = 0; c < columns; c++) {
        pos_eind =
            record.indexOf(
                ";", pos_begin); // Returns the index within this string of the first occurrence of
        // the specified substring, starting at the specified index.

        if (pos_eind != -1) {
          data[r][c] = record.substring(pos_begin, pos_eind);
          pos_begin = pos_eind + 1;
        } else {
          break;
        }
      }

      r++;

      /* safety */
      if (r >= rows) {
        break;
      }
    }
  }
}
