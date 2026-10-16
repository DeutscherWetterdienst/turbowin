package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MintakaStarRecordFieldsParserTest {

  private static final String STAR_RECORD =
      "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006";
  private static final String STAR_X_RECORD =
      "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60*18";

  @Test
  public void parsesStarFieldsAndUsesZeroAge() {
    MintakaStarRecordFields fields = MintakaStarRecordFieldsParser.parse(STAR_RECORD, false);

    assertNotNull(fields);
    assertEquals("1029.97", fields.pressure);
    assertEquals("-0.90", fields.pressureTendency);
    assertEquals("7", fields.tendencyCharacteristic);
    assertEquals(" 52 41.9535N", fields.latitude);
    assertEquals("  6 14.1943E", fields.longitude);
    assertEquals("0", fields.observationAge);
  }

  @Test
  public void parsesStarXFieldsAndObservationAge() {
    MintakaStarRecordFields fields = MintakaStarRecordFieldsParser.parse(STAR_X_RECORD, true);

    assertNotNull(fields);
    assertEquals("1018.12", fields.pressure);
    assertEquals("0.00", fields.pressureTendency);
    assertEquals("0", fields.tendencyCharacteristic);
    assertEquals(" 16 30.4429N", fields.latitude);
    assertEquals(" 88 21.9040W", fields.longitude);
    assertEquals("60", fields.observationAge);
  }

  @Test
  public void rejectsRecordsWithoutCommonFields() {
    assertNull(MintakaStarRecordFieldsParser.parse("short", false));
  }

  @Test
  public void toleratesEmptyPressureField() {
    String record = ",1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006";

    MintakaStarRecordFields fields = MintakaStarRecordFieldsParser.parse(record, false);

    assertNotNull(fields);
    assertEquals("", fields.pressure);
  }

  @Test
  public void rejectsMalformedStarXFields() {
    assertNull(MintakaStarRecordFieldsParser.parse(STAR_RECORD, true));
  }
}
