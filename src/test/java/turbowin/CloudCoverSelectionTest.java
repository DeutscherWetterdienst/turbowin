package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CloudCoverSelectionTest {

  @Test
  public void convertsOvercastAndValidHeightToCodes() {
    CloudCoverSelection.Result result =
        CloudCoverSelection.validate(
            CloudCoverState.N_COMPLETELY_OVERCAST,
            CloudCoverState.NH_5_8,
            CloudCoverState.H_600_1000);

    assertTrue(result.valid());
    assertEquals("8", result.nCode);
    assertEquals("5", result.nhCode);
    assertEquals("5", result.hCode);
  }

  @Test
  public void rejectsCloudlessWithCloudsPresent() {
    CloudCoverSelection.Result result =
        CloudCoverSelection.validate(
            CloudCoverState.N_CLOUDLESS, CloudCoverState.NH_1_8, CloudCoverState.H_CLOUDLESS);

    assertFalse(result.valid());
    assertTrue(result.error.contains("amount of Cl"));
  }

  @Test
  public void rejectsLowCloudAmountGreaterThanTotalCover() {
    CloudCoverSelection.Result result =
        CloudCoverSelection.validate(
            CloudCoverState.N_2_8, CloudCoverState.NH_3_8, CloudCoverState.H_0_50);

    assertFalse(result.valid());
    assertEquals("'total cloud cover' must be >= 'amount of Cl (or Cm)'", result.error);
  }

  @Test
  public void validatesObscuredAndNotDeterminedObservations() {
    CloudCoverSelection.Result obscured =
        CloudCoverSelection.validate(
            CloudCoverState.N_OBSCURED,
            CloudCoverState.NH_OBSCURED,
            CloudCoverState.H_NOT_DETERMINED);
    assertTrue(obscured.valid());
    assertEquals("9", obscured.nCode);
    assertEquals("9", obscured.nhCode);
    assertEquals("/", obscured.hCode);
    assertFalse(
        CloudCoverSelection.validate(
                CloudCoverState.N_NOT_DETERMINED,
                CloudCoverState.NH_1_8,
                CloudCoverState.H_NOT_DETERMINED)
            .valid());
    assertFalse(
        CloudCoverSelection.validate(
                CloudCoverState.N_OBSCURED, CloudCoverState.NH_OBSCURED, CloudCoverState.H_0_50)
            .valid());
  }

  @Test
  public void mapsHeightAdviceToExistingMeteorologicalRanges() {
    assertEquals(CloudCoverState.H_CLOUDLESS, CloudCoverDialog.advisedHeight(-9));
    assertEquals(CloudCoverState.H_300_600, CloudCoverDialog.advisedHeight(4));
    assertEquals(CloudCoverState.H_GROTER_2500, CloudCoverDialog.advisedHeight(9));
    assertEquals(CloudCoverState.H_NOT_DETERMINED, CloudCoverDialog.advisedHeight(main.INVALID));
  }
}
