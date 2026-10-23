package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Rs232DashboardRecordParserTest {

  @Test
  public void parsesPtb220FixedPositions() {
    Rs232DashboardRecordParser.Result result =
        Rs232DashboardRecordParser.parseFixedPosition(ptb220Record("1000.00", "1.23"), 1);

    assertEquals("1000.00", result.pressure);
    assertEquals("1.23 ", result.pressureChange);
  }

  @Test
  public void parsesPtb330FixedPositions() {
    Rs232DashboardRecordParser.Result result =
        Rs232DashboardRecordParser.parseFixedPosition(ptb330Record("1001.00", "2.34"), 2);

    assertEquals("1001.00", result.pressure);
    assertEquals("2.34 ", result.pressureChange);
  }

  @Test
  public void parsesCommaSeparatedFields() {
    Rs232DashboardRecordParser.Result result =
        Rs232DashboardRecordParser.parseCommaSeparated("1002.00,ignored,3.45,unused");

    assertEquals("1002.00", result.pressure);
    assertEquals("3.45", result.pressureChange);
  }

  @Test
  public void returnsNullForUnsupportedFixedRecord() {
    assertNull(Rs232DashboardRecordParser.parseFixedPosition("1000", 9));
  }

  @Test
  public void preservesLegacyFieldPositionsWhenCommaSeparatedFieldsAreMissing() {
    Rs232DashboardRecordParser.Result result =
        Rs232DashboardRecordParser.parseCommaSeparated("1002.00,missing");

    assertEquals("1002.00", result.pressure);
    assertEquals("1002.00", result.pressureChange);
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void throwsForShortFixedRecord() {
    Rs232DashboardRecordParser.parseFixedPosition("1000", 1);
  }

  @Test
  public void parsesValidStarXRecord() {
    Rs232DashboardRecordParser.Result result =
        Rs232DashboardRecordParser.parseStarX(starXRecord("1003.00", "4.56", "10"));

    assertEquals("1003.00", result.pressure);
    assertEquals("4.56", result.pressureChange);
    assertEquals("1003.00", result.observationPressure);
  }

  @Test
  public void returnsNullForInvalidStarXChecksum() {
    assertNull(Rs232DashboardRecordParser.parseStarX("1003.00,4.56,0,0,0,0,0,0,0,0,0,0,0,10*00"));
  }

  @Test
  public void clearsFieldsButPreservesObservationPressureForInvalidStarXAge() {
    Rs232DashboardRecordParser.Result result =
        Rs232DashboardRecordParser.parseStarX(starXRecord("1003.00", "4.56", "601"));

    assertEquals("", result.pressure);
    assertEquals("", result.pressureChange);
    assertEquals("1003.00", result.observationPressure);
  }

  private String ptb220Record(String pressure, String pressureChange) {
    StringBuilder record = new StringBuilder("                                ");
    record.replace(main.RECORD_P_BEGIN_POS_PTB220, main.RECORD_P_BEGIN_POS_PTB220 + 7, pressure);
    record.replace(
        main.RECORD_ppp_BEGIN_POS_PTB220, main.RECORD_ppp_BEGIN_POS_PTB220 + 5, pressureChange);
    return record.toString();
  }

  private String ptb330Record(String pressure, String pressureChange) {
    StringBuilder record = new StringBuilder("                              ");
    record.replace(main.RECORD_P_BEGIN_POS_PTB330, main.RECORD_P_BEGIN_POS_PTB330 + 7, pressure);
    record.replace(
        main.RECORD_ppp_BEGIN_POS_PTB330, main.RECORD_ppp_BEGIN_POS_PTB330 + 5, pressureChange);
    return record.toString();
  }

  private String starXRecord(String pressure, String pressureChange, String observationAge) {
    String recordWithoutChecksum =
        String.join(
            ",",
            pressure,
            "1004.00",
            pressureChange,
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            observationAge);
    String checksum = main_RS232_RS422.Mintaka_Star_Checksum(recordWithoutChecksum + "*");
    return recordWithoutChecksum + "*" + checksum + "............";
  }
}
