package turbowin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Performs the non-UI processing currently used by the observation statistics worker. */
final class ObsStatsImmtLogProcessor {

  private ObsStatsImmtLogProcessor() {}

  static Result process(
      String path,
      String logPeriod,
      LocalDate startDate,
      LocalDate endDate,
      boolean observerStatsMode) {
    List<String> immtList = new ArrayList<>();
    String immtRecFirst = "";
    String immtRecLast = "";
    int returnImmt = 0;
    int recCounter = 0;

    try (BufferedReader in = new BufferedReader(new FileReader(path))) {
      String record = "";
      while ((record = in.readLine()) != null) {
        try {
          if (record.length() >= main.IMMT_5_LENGTH) {
            recCounter++;

            // Keep the first and last raw IMMT records for the graph's raw-log line.
            if (recCounter == 1) {
              immtRecFirst = record;
            }
            if (recCounter >= 1) {
              immtRecLast = record;
            }

            if (logPeriod.equals(myimmtlogperiod.ALL)) {
              immtList.add(record);
            } else {
              boolean dateLogPeriodOk = checkRecordLogPeriod(record, startDate, endDate);
              if (dateLogPeriodOk) {
                immtList.add(record);
              }
            }
          }
        } catch (UnsupportedOperationException e) {
          returnImmt = -1;
        }
      }
    } catch (IOException ex) {
      returnImmt = -2;
    }

    if (observerStatsMode) {
      int testDifferentObserverNames = 0;
      String[] testObserversNamesArray = new String[Obs_Stats_view.MAX_NUMBER_OBSERVERS_TEST];
      for (int i = 0; i < Obs_Stats_view.MAX_NUMBER_OBSERVERS_TEST; i++) {
        testObserversNamesArray[i] = "";
      }

      for (String obs : immtList) {
        String observerName = "";
        boolean observerNameFound = false;

        if (obs.length() > main.IMMT_5_POSITION_OBSERVER - 1) {
          // IMMT-5 observer data starts at fixed position 173: surname;initials;rank;discharge-book number.
          observerName = obs.substring(main.IMMT_5_POSITION_OBSERVER);

          for (int i = 0; i < Obs_Stats_view.MAX_NUMBER_OBSERVERS_TEST; i++) {
            if (testObserversNamesArray[i].equals(observerName) && (!observerName.equals(""))) {
              observerNameFound = true;
              break;
            }
          }

          if (observerNameFound == false) {
            for (int i = 0; i < Obs_Stats_view.MAX_NUMBER_OBSERVERS_TEST; i++) {
              if (testObserversNamesArray[i].equals("") && (!observerName.equals(""))) {
                testObserversNamesArray[i] = observerName;
                break;
              }
            }
          }
        }
      }

      for (int i = 0; i < Obs_Stats_view.MAX_NUMBER_OBSERVERS_TEST; i++) {
        if (testObserversNamesArray[i].equals("") == false) {
          testDifferentObserverNames++;
        }
      }

      // The graph can display only a limited number of observer bars;
      // -3 signals that the limit was exceeded.
      if (testDifferentObserverNames > Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES) {
        returnImmt = -3;
      }
    }

    return new Result(returnImmt, immtList, immtRecFirst, immtRecLast);
  }

  private static boolean checkRecordLogPeriod(
      String record, LocalDate viewLocalStartDate, LocalDate viewLocalEndDate) {
    boolean dateLogPeriodOk = true;
    boolean continueChecking = true;
    LocalDate localRecordDate = null;

    // Fixed-width IMMT date fields: year [1..4], month [5..6], and day [7..8].
    String yearRecord = record.substring(1, 5);
    String monthRecord = record.substring(5, 7);
    String dayRecord = record.substring(7, 9);

    try {
      localRecordDate = LocalDate.parse(yearRecord + "-" + monthRecord + "-" + dayRecord);
      continueChecking = true;
    } catch (DateTimeParseException e) {
      continueChecking = false;
    }

    if (localRecordDate == null || viewLocalStartDate == null || viewLocalEndDate == null) {
      continueChecking = false;
    }

    if (continueChecking) {
      boolean isAfterOk = localRecordDate.isAfter(viewLocalStartDate.minusDays(1));
      boolean isBeforeOk = localRecordDate.isBefore(viewLocalEndDate.plusDays(1));

      if (isAfterOk && isBeforeOk) {
        dateLogPeriodOk = true;
      } else {
        dateLogPeriodOk = false;
      }
    }

    if (!continueChecking) {
      dateLogPeriodOk = false;
    }

    return dateLogPeriodOk;
  }

  record Result(int status, List<String> records, String firstRecord, String lastRecord) {
    Result {
      records = Collections.unmodifiableList(new ArrayList<>(records));
    }
  }
}
