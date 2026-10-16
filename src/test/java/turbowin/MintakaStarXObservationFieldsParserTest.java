package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MintakaStarXObservationFieldsParserTest {

  private static final String RECORD =
      "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60*18";

  @Test
  public void extractsAllStarXObservationFields() {
    MintakaStarXObservationFields fields = MintakaStarXObservationFieldsParser.parse(RECORD);

    assertNotNull(fields);
    assertEquals(" 16 30.4429N", fields.latitude);
    assertEquals(" 88 21.9040W", fields.longitude);
    assertEquals("29.4", fields.airTemperature);
    assertEquals("85", fields.relativeHumidity);
    assertEquals("27.4", fields.wetBulbTemperature);
    assertEquals("26.7", fields.dewPoint);
    assertEquals("60", fields.observationAge);
  }

  @Test
  public void rejectsRecordsWithoutAllStarXFields() {
    assertNull(MintakaStarXObservationFieldsParser.parse("short"));
  }
}
