package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AwsWaveCodeFormatterTest {

  @Test
  public void formatsPeriodsHeightsAndDirections() {
    assertEquals("3", AwsWaveCodeFormatter.period("03"));
    assertEquals("", AwsWaveCodeFormatter.period("99"));
    assertEquals("1.5", AwsWaveCodeFormatter.height("03"));
    assertEquals("", AwsWaveCodeFormatter.height("//"));
    assertEquals("120", AwsWaveCodeFormatter.direction("12"));
    assertEquals("", AwsWaveCodeFormatter.direction("99"));
  }
}
