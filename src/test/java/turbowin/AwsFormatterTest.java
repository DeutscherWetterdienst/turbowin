package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AwsFormatterTest {

  @Test
  public void formatsDirectAndCloudCodes() {
    assertEquals("12", AwsObservationCodeFormatter.direct("12", "/"));
    assertEquals("", AwsObservationCodeFormatter.direct("/", "/"));
    assertEquals("31", AwsCloudCodeFormatter.convert("1", 30, "low (Cl)"));
    assertEquals("27", AwsCloudCodeFormatter.convertFirstDigit("7a", 20, "middle (Cm)"));
    assertEquals("", AwsCloudCodeFormatter.convert("x", 10, "high (Ch)"));
  }

  @Test
  public void formatsIceAndNumericCodes() {
    assertEquals("0.04", AwsIceCodeFormatter.thickness("04"));
    assertEquals("2", AwsIceCodeFormatter.direct("2"));
    assertEquals("14", AwsIceCodeFormatter.unknownValue("u", "14"));
    assertEquals("12", AwsIceCodeFormatter.iceCause("3"));
    assertEquals("180", AwsIceCodeFormatter.iceBearing("4"));
    assertEquals("", AwsIceCodeFormatter.iceBearing("0"));
    assertEquals(2.3, AwsNumericFormatter.roundToOneDecimal(2.26), 0.00001);
  }
}
