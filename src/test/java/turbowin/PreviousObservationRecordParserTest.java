package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Test;

public class PreviousObservationRecordParserTest {

  @Test
  public void parsesDateTimeAndPositionFields() {
    PreviousObservationRecordParser.PreviousObservationRecord result =
        PreviousObservationRecordParser.parse("A202601011211230450");

    assertTrue(result.valid());
    assertEquals(2026, result.year());
    assertEquals(1, result.month());
    assertEquals(1, result.day());
    assertEquals(12, result.hour());
    assertEquals(12.3f, result.latitude(), 0.001f);
    assertEquals(45.0f, result.longitude(), 0.001f);
  }

  @Test
  public void appliesQuadrantSigns() {
    PreviousObservationRecordParser.PreviousObservationRecord result =
        PreviousObservationRecordParser.parse("A202601011251230450");

    assertTrue(result.valid());
    assertEquals(-12.3f, result.latitude(), 0.001f);
    assertEquals(-45.0f, result.longitude(), 0.001f);
  }

  @Test
  public void rejectsShortAndMalformedRecords() {
    assertFalse(PreviousObservationRecordParser.parse("short").valid());
    assertFalse(PreviousObservationRecordParser.parse("A2026x011211230450").valid());
  }

  @Test
  public void reportsEachMalformedFieldBeforeRejectingRecord() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(output));

      assertFalse(PreviousObservationRecordParser.parse("A20x6x0111212304500").valid());
    } finally {
      System.setOut(originalOut);
    }

    assertEquals(2, output.toString().split("\\R").length);
  }
}
