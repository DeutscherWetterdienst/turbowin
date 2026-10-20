package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PeumbRecordExtractorTest {

  @Test
  public void extractsRecordAfterTransportPrefix() {
    assertEquals(
        "$PEUMB,20260411,060028*57",
        PeumbRecordExtractor.extract("prefix\\0\\s:device*31\\$PEUMB,20260411,060028*57"));
  }

  @Test
  public void returnsNullWhenLineContainsNoPeumbRecord() {
    assertNull(PeumbRecordExtractor.extract("other sensor data"));
  }

  @Test
  public void removesLineTerminatorsFromExtractedRecord() {
    assertEquals(
        "$PEUMB,20260411,060028*57", PeumbRecordExtractor.extract("$PEUMB,20260411,060028*57\r\n"));
  }
}
