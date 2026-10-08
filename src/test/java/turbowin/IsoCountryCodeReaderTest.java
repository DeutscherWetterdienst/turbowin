package turbowin;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class IsoCountryCodeReaderTest {

  @Test
  public void skipsShortLinesAndRemovesTabs() throws Exception {
    String input = "\nABC\n\tNLD\t Netherlands\nDEU Germany\n";

    assertArrayEquals(
        new String[] {"NLD Netherlands", "DEU Germany", "", ""},
        IsoCountryCodeReader.read(
            new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), 4));
  }

  @Test
  public void stopsAfterMaximumNumberOfAcceptedRecords() throws Exception {
    String input = "AAAA\nBBBB\nCCCC\n";

    assertArrayEquals(
        new String[] {"AAAA", "BBBB"},
        IsoCountryCodeReader.read(
            new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), 2));
  }
}
