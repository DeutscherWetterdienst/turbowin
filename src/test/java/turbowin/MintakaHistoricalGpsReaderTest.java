package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.junit.Test;

public class MintakaHistoricalGpsReaderTest {

  private static final String TIMESTAMP = "201703152006";
  private static final String RECORD =
      "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A" + TIMESTAMP;
  private static final String MALFORMED_STAR_X_RECORD =
      "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60*18";

  @Test
  public void returnsCoordinatesFromTheMatchingRecord() throws Exception {
    File file = Files.createTempFile("turbowin-historical-gps", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of(RECORD));

      MintakaHistoricalGpsReading reading = MintakaHistoricalGpsReader.read(file, TIMESTAMP, false);

      assertEquals(" 52 41.9535N", reading.latitude);
      assertEquals("  6 14.1943E", reading.longitude);
    } finally {
      file.delete();
    }
  }

  @Test
  public void returnsNullForInvalidOrNonMatchingRecords() throws Exception {
    File file = Files.createTempFile("turbowin-historical-gps-invalid", ".txt").toFile();
    try {
      Files.write(file.toPath(), List.of(RECORD.replace("*1A", "*00")));

      assertNull(MintakaHistoricalGpsReader.read(file, TIMESTAMP, false));
      assertNull(MintakaHistoricalGpsReader.read(file, "201703152007", false));
    } finally {
      file.delete();
    }
  }

  @Test
  public void returnsNullForMalformedStarXDelimiter() {
    assertNull(
        MintakaHistoricalGpsReader.parseRecord(
            MALFORMED_STAR_X_RECORD.replace(",60*18", "60*18"), true));
  }
}
