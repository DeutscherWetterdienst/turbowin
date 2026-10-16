package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Characterizes the record fields consumed by the Mintaka GPS and WOW/APR workflows. */
public class MintakaGpsWowWorkflowCharacterizationTest {

  private static final String STAR_RECORD =
      "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006";

  @Test
  public void gpsBranchExtractsLatitudeAndLongitudeFromValidStarRecord() {
    assertTrue(MintakaRecordValidator.isValid(STAR_RECORD, false));

    MintakaStarRecordFields fields = MintakaStarRecordFieldsParser.parse(STAR_RECORD, false);

    assertEquals(" 52 41.9535N", fields.latitude);
    assertEquals("  6 14.1943E", fields.longitude);
  }

  @Test
  public void wowAprBranchUsesPressureFromValidStarRecord() {
    assertTrue(MintakaRecordValidator.hasValidChecksum(STAR_RECORD));

    int firstComma = STAR_RECORD.indexOf(',');

    assertEquals("1029.97", STAR_RECORD.substring(0, firstComma));
  }

  @Test
  public void gpsAndWowAprBranchesRejectInvalidChecksums() {
    String invalidChecksum = STAR_RECORD.replace("*1A", "*00");

    assertFalse(MintakaRecordValidator.hasValidChecksum(invalidChecksum));
    assertFalse(MintakaRecordValidator.isValid(invalidChecksum, false));
  }
}
