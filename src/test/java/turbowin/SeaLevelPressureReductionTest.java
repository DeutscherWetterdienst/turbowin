package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SeaLevelPressureReductionTest {

  @Test
  public void usesWarmColdZeroAndStandardObservedTemperatures() {
    assertEquals(2.253978, SeaLevelPressureReduction.heightCorrection(1000, 20, "30"), 0.000001);
    assertEquals(2.699162, SeaLevelPressureReduction.heightCorrection(1000, 20, "-20"), 0.00001);
    assertEquals(2.501532, SeaLevelPressureReduction.heightCorrection(1000, 20, "0"), 0.000001);
    assertEquals(
        2.371312, SeaLevelPressureReduction.heightCorrection(1000, 20, " 15.0 "), 0.000001);
  }

  @Test
  public void fallsBackForMissingMalformedFlaggedAndInvalidTemperatures() {
    for (String value :
        new String[] {
          null,
          "",
          " ",
          "bad",
          "20.0*",
          "NaN",
          "Infinity",
          "-Infinity",
          "70",
          "-70",
          "999.9",
          Double.toString(Double.MAX_VALUE)
        }) {
      assertEquals(
          value, 2.371312, SeaLevelPressureReduction.heightCorrection(1000, 20, value), 0.000001);
    }
  }

  @Test
  public void handlesZeroAndNegativeHeights() {
    assertEquals(0, SeaLevelPressureReduction.heightCorrection(1000, 0, "30"), 0);
    assertEquals(-2.253978, SeaLevelPressureReduction.heightCorrection(1000, -20, "30"), 0.000001);
  }
}
