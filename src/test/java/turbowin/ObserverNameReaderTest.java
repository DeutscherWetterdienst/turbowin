package turbowin;

import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.Test;

public class ObserverNameReaderTest {

  @Test
  public void preservesObserverOrder() throws Exception {
    String[] observerNames = emptyObserverNames();

    ObserverNameReader.read(new BufferedReader(new StringReader("first\nsecond")), observerNames);

    assertArrayEquals(
        new String[] {
          "first", "second", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
        },
        observerNames);
  }

  @Test
  public void ignoresRecordsAfterTheObserverLimit() throws Exception {
    String[] observerNames = emptyObserverNames();
    StringBuilder records = new StringBuilder();
    for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS + 1; i++) {
      if (i > 0) {
        records.append('\n');
      }
      records.append("observer-").append(i);
    }

    ObserverNameReader.read(
        new BufferedReader(new StringReader(records.toString())), observerNames);

    for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS; i++) {
      org.junit.Assert.assertEquals("observer-" + i, observerNames[i]);
    }
  }

  private static String[] emptyObserverNames() {
    String[] observerNames = new String[main.MAX_AANTAL_WAARNEMERS];
    for (int i = 0; i < observerNames.length; i++) {
      observerNames[i] = "";
    }
    return observerNames;
  }
}
