package turbowin;

import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.Before;
import org.junit.Test;

public class ObserverYearExtractorTest {

  private String[] years;

  @Before
  public void setUp() {
    years = new String[main.MAX_AANTAL_JAREN_IN_IMMT];
    for (int i = 0; i < years.length; i++) {
      years[i] = "";
    }
  }

  @Test
  public void preservesTheOrderOfDistinctYears() throws Exception {
    extract(record("2024") + "\n" + record("2022") + "\n" + record("2024"));

    assertArrayEquals(new String[] {"2024", "2022", "", "", ""}, years);
  }

  @Test
  public void ignoresRecordsShorterThanTheVersionPosition() throws Exception {
    extract("short record");

    assertArrayEquals(new String[] {"", "", "", "", ""}, years);
  }

  @Test
  public void keepsOnlyTheMaximumNumberOfYears() throws Exception {
    extract(
        record("2020")
            + "\n"
            + record("2021")
            + "\n"
            + record("2022")
            + "\n"
            + record("2023")
            + "\n"
            + record("2024")
            + "\n"
            + record("2025"));

    assertArrayEquals(new String[] {"2020", "2021", "2022", "2023", "2024"}, years);
  }

  private void extract(String records) throws Exception {
    ObserverYearExtractor.extract(new BufferedReader(new StringReader(records)), years);
  }

  private static String record(String year) {
    StringBuilder record = new StringBuilder(" ".repeat(main.IMMT_POSITION_IMMT_VERSION + 1));
    record.replace(1, 5, year);
    return record.toString();
  }
}
