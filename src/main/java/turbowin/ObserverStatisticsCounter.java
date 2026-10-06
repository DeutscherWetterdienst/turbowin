package turbowin;

import java.io.BufferedReader;
import java.io.IOException;

/** Counts IMMT observations for each known observer and year. */
final class ObserverStatisticsCounter {

  private ObserverStatisticsCounter() {}

  static int[][] count(BufferedReader input, String[] observerNames, String[] years)
      throws IOException {
    int[][] observationCounts = new int[main.MAX_AANTAL_JAREN_IN_IMMT][main.MAX_AANTAL_WAARNEMERS];
    String record;

    while ((record = input.readLine()) != null) {
      if (record.length() > main.IMMT_POSITION_IMMT_VERSION) {
        // IMMT versions 3, 4, and 5 store the observer field at different offsets.
        String immtVersion =
            record.substring(main.IMMT_POSITION_IMMT_VERSION, main.IMMT_POSITION_IMMT_VERSION + 1);
        int observerPosition;

        if (immtVersion.equals("3")) {
          observerPosition = main.IMMT_3_POSITION_OBSERVER;
        } else if (immtVersion.equals("4")) {
          observerPosition = main.IMMT_4_POSITION_OBSERVER;
        } else if (immtVersion.equals("5")) {
          observerPosition = main.IMMT_5_POSITION_OBSERVER;
        } else {
          observerPosition = main.INVALID;
        }

        if (record.length() > observerPosition - 1) {
          // Observer fields are surname;initials;rank;discharge-book number, matching observerNames.
          String observerName = record.substring(observerPosition);
          String year = record.substring(1, 5);

          for (int observerIndex = 0; observerIndex < main.MAX_AANTAL_WAARNEMERS; observerIndex++) {
            if (!observerNames[observerIndex].equals("")) {
              if (observerName.equals(observerNames[observerIndex])) {
                for (int yearIndex = 0; yearIndex < main.MAX_AANTAL_JAREN_IN_IMMT; yearIndex++) {
                  if (year.equals(years[yearIndex])) {
                    observationCounts[yearIndex][observerIndex]++;
                  }
                }
              }
            }
          }
        }
      }
    }

    return observationCounts;
  }
}
