package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Characterizes the fields consumed by the non-Duo Mintaka PPPP and APPP workflows. */
public class MintakaWorkflowRecordCharacterizationTest {

  private static final String STAR_RECORD =
      "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006";

  @Test
  public void ppppBranchUsesPressureAndNoObservationAgeForStarRecords() {
    assertTrue(MintakaRecordValidator.isValid(STAR_RECORD, false));

    int firstComma = STAR_RECORD.indexOf(',');
    assertEquals("1029.97", STAR_RECORD.substring(0, firstComma));
  }

  @Test
  public void apppBranchUsesPressureTendencyAndCharacteristicForStarRecords() {
    assertTrue(MintakaRecordValidator.isValid(STAR_RECORD, false));

    int firstComma = STAR_RECORD.indexOf(',');
    int secondComma = STAR_RECORD.indexOf(',', firstComma + 1);
    int thirdComma = STAR_RECORD.indexOf(',', secondComma + 1);
    int fourthComma = STAR_RECORD.indexOf(',', thirdComma + 1);

    assertEquals("-0.90", STAR_RECORD.substring(secondComma + 1, thirdComma));
    assertEquals("7", STAR_RECORD.substring(thirdComma + 1, fourthComma));
  }

  @Test
  public void invalidChecksumIsRejectedBeforeWorkflowFieldsAreConsumed() {
    String invalidChecksum = STAR_RECORD.replace("*1A", "*00");

    assertFalse(MintakaRecordValidator.hasValidChecksum(invalidChecksum));
  }
}
