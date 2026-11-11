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
            mycloudcover.N_COMPLETELY_OVERCAST, mycloudcover.NH_5_8, mycloudcover.H_600_1000);

    assertTrue(result.valid());
    assertEquals("8", result.nCode);
    assertEquals("5", result.nhCode);
    assertEquals("5", result.hCode);
  }

  @Test
  public void rejectsCloudlessWithCloudsPresent() {
    CloudCoverSelection.Result result =
        CloudCoverSelection.validate(
            mycloudcover.N_CLOUDLESS, mycloudcover.NH_1_8, mycloudcover.H_CLOUDLESS);

    assertFalse(result.valid());
    assertTrue(result.error.contains("amount of Cl"));
  }

  @Test
  public void rejectsLowCloudAmountGreaterThanTotalCover() {
    CloudCoverSelection.Result result =
        CloudCoverSelection.validate(mycloudcover.N_2_8, mycloudcover.NH_3_8, mycloudcover.H_0_50);

    assertFalse(result.valid());
    assertEquals("'total cloud cover' must be >= 'amount of Cl (or Cm)'", result.error);
  }

  @Test
  public void validatesObscuredAndNotDeterminedObservations() {
    CloudCoverSelection.Result obscured =
        CloudCoverSelection.validate(
            mycloudcover.N_OBSCURED, mycloudcover.NH_OBSCURED, mycloudcover.H_NOT_DETERMINED);
    assertTrue(obscured.valid());
    assertEquals("9", obscured.nCode);
    assertEquals("9", obscured.nhCode);
    assertEquals("/", obscured.hCode);
    assertFalse(
        CloudCoverSelection.validate(
                mycloudcover.N_NOT_DETERMINED, mycloudcover.NH_1_8, mycloudcover.H_NOT_DETERMINED)
            .valid());
    assertFalse(
        CloudCoverSelection.validate(
                mycloudcover.N_OBSCURED, mycloudcover.NH_OBSCURED, mycloudcover.H_0_50)
            .valid());
  }

  @Test
  public void mapsHeightAdviceToExistingMeteorologicalRanges() {
    assertEquals(mycloudcover.H_CLOUDLESS, CloudCoverJavaFxWindow.advisedHeight(-9));
    assertEquals(mycloudcover.H_300_600, CloudCoverJavaFxWindow.advisedHeight(4));
    assertEquals(mycloudcover.H_GROTER_2500, CloudCoverJavaFxWindow.advisedHeight(9));
    assertEquals(mycloudcover.H_NOT_DETERMINED, CloudCoverJavaFxWindow.advisedHeight(main.INVALID));
  }
}
