package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MintakaRecordValidatorTest {

  private static final String STAR_RECORD =
      "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006";

  @Test
  public void validatesStarFormatAndChecksum() {
    assertTrue(MintakaRecordValidator.hasExpectedCommaCount(STAR_RECORD, false));
    assertTrue(MintakaRecordValidator.hasValidChecksum(STAR_RECORD));
    assertTrue(MintakaRecordValidator.isValid(STAR_RECORD, false));
  }

  @Test
  public void rejectsWrongFormatAndChecksum() {
    assertFalse(MintakaRecordValidator.hasExpectedCommaCount(STAR_RECORD + ",extra", false));
    assertFalse(MintakaRecordValidator.hasValidChecksum(STAR_RECORD.replace("*1A", "*00")));
    assertFalse(MintakaRecordValidator.isValid("short", false));
  }
}
