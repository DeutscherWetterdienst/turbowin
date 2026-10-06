package turbowin;

import java.io.BufferedReader;
import java.io.IOException;

/** Extracts distinct observation years from IMMT records in their original order. */
final class ObserverYearExtractor {

  private ObserverYearExtractor() {}

  static void extract(BufferedReader input, String[] years) throws IOException {
    String record;

    while ((record = input.readLine()) != null) {
      if (record.length() > main.IMMT_POSITION_IMMT_VERSION) {
        String year = record.substring(1, 5);
        boolean alreadyPresent = false;

        for (int index = 0; index < main.MAX_AANTAL_JAREN_IN_IMMT; index++) {
          if (years[index].equals(year)) {
            alreadyPresent = true;
            break;
          }
        }

        if (!alreadyPresent) {
          for (int index = 0; index < main.MAX_AANTAL_JAREN_IN_IMMT; index++) {
            if (years[index].equals("")) {
              years[index] = year;
              break;
            }
          }
        }
      }
    }
  }
}
