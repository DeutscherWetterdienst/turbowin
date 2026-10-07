package turbowin;

import java.io.BufferedWriter;
import java.io.IOException;

/** Writes the legacy semicolon-separated captain and observer log format. */
final class SemicolonLogFileWriter {

  private SemicolonLogFileWriter() {}

  static void write(BufferedWriter out, String[][] data, int rows, int columns) throws IOException {
    for (int r = 0; r < rows; r++) {
      // at least surname must be present (c = 0)
      if ((data[r][0] != null) && (data[r][0].compareTo("") != 0)) {
        for (int c = 0; c < columns; c++) {
          if ((data[r][c] != null) && (data[r][c].compareTo("") != 0)) {
            out.write(data[r][c]);
          } else // empty field/cell
          {
            out.write("-");
          }

          out.write(";"); // semi-column seperated
        } // for (int c = 0; c < COLUMNS; c++)

        out.newLine(); // newLine(): write a line separator. The line separator string is
        // defined by the system property line.separator, and is not
        // necessarily a single newline ('\n') character.
      } // if ((data[r][0] != null)
    } // for (int r = 0; r < rows; r++)
  }
}
