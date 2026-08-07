package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ObservationEmailContentPreparerTest {

  @Test
  public void usesFormat101BodyWhenBodyModeIsSelected() {
    ObservationEmailContentPreparer.Result result =
        ObservationEmailContentPreparer.prepare(
            main.FORMAT_101,
            false,
            main.FORMAT_101_BODY,
            "format 101 observation",
            "ignored",
            "subject",
            "24",
            "12");

    assertEquals("subject", result.subject());
    assertEquals("format 101 observation", result.body());
    assertTrue(result.nonEmpty());
  }

  @Test
  public void usesAttachmentMarkerWhenAttachmentModeIsSelected() {
    ObservationEmailContentPreparer.Result result =
        ObservationEmailContentPreparer.prepare(
            main.FORMAT_AWS,
            true,
            main.FORMAT_101_ATTACHEMENT,
            "ignored",
            "ignored",
            "subject",
            "24",
            "12");

    assertEquals("see attachment", result.body());
    assertTrue(result.nonEmpty());
  }

  @Test
  public void replacesFm13SubjectDateAndUsesFm13Body() {
    ObservationEmailContentPreparer.Result result =
        ObservationEmailContentPreparer.prepare(
            main.FORMAT_FM13,
            false,
            main.FORMAT_101_BODY,
            "ignored",
            "FM13 observation",
            "Observation ddhhmm",
            "24",
            "12");

    assertEquals("Observation 241200", result.subject());
    assertEquals("FM13 observation", result.body());
    assertTrue(result.nonEmpty());
  }

  @Test
  public void marksEmptyObservationAsInvalid() {
    ObservationEmailContentPreparer.Result result =
        ObservationEmailContentPreparer.prepare(
            main.FORMAT_FM13, false, main.FORMAT_101_BODY, "ignored", "", "subject", "24", "12");

    assertFalse(result.nonEmpty());
  }

  @Test
  public void preservesNullBodyMarkerForAnUnknownFormat() {
    ObservationEmailContentPreparer.Result result =
        ObservationEmailContentPreparer.prepare(
            "unknown", false, main.FORMAT_101_BODY, "ignored", "ignored", "subject", "24", "12");

    assertEquals("null", result.body());
    assertTrue(result.nonEmpty());
  }
}
