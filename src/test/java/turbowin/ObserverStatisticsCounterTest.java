package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.Before;
import org.junit.Test;

public class ObserverStatisticsCounterTest {

  private final String[] observerNames = new String[main.MAX_AANTAL_WAARNEMERS];
  private final String[] years = new String[main.MAX_AANTAL_JAREN_IN_IMMT];

  @Before
  public void setUp() {
    for (int i = 0; i < observerNames.length; i++) {
      observerNames[i] = "";
    }
    for (int i = 0; i < years.length; i++) {
      years[i] = "";
    }
    observerNames[0] = "Smith;J;Captain;123";
    years[0] = "2024";
  }

  @Test
  public void countsMatchingObserverRecordsForVersionsThreeFourAndFive() throws Exception {
    String records =
        record('3', main.IMMT_3_POSITION_OBSERVER, "2024")
            + "\n"
            + record('4', main.IMMT_4_POSITION_OBSERVER, "2024")
            + "\n"
            + record('5', main.IMMT_5_POSITION_OBSERVER, "2024");

    int[][] counts = count(records);

    assertEquals(3, counts[0][0]);
  }

  @Test
  public void ignoresUnmatchedObserverAndYearRecords() throws Exception {
    String unmatchedObserver =
        record('4', main.IMMT_4_POSITION_OBSERVER, "2024")
            .replace(observerNames[0], "Other;J;Captain;456");
    String unmatchedYear = record('4', main.IMMT_4_POSITION_OBSERVER, "2023");

    int[][] counts = count(unmatchedObserver + "\n" + unmatchedYear);

    assertEquals(0, counts[0][0]);
  }

  @Test
  public void ignoresRecordsShorterThanTheVersionPosition() throws Exception {
    assertEquals(0, count("short record")[0][0]);
  }

  @Test
  public void ignoresAnUnknownImmtVersion() throws Exception {
    assertEquals(0, count(record('9', main.IMMT_5_POSITION_OBSERVER, "2024"))[0][0]);
  }

  private int[][] count(String records) throws Exception {
    return ObserverStatisticsCounter.count(
        new BufferedReader(new StringReader(records)), observerNames, years);
  }

  private static String record(char version, int observerPosition, String year) {
    StringBuilder record = new StringBuilder(" ".repeat(observerPosition));
    record.replace(1, 5, year);
    record.setCharAt(main.IMMT_POSITION_IMMT_VERSION, version);
    record.append("Smith;J;Captain;123");
    return record.toString();
  }
}
