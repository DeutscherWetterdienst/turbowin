package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class LandSeaMaskWorkflowTest {

  private static final LandSeaMaskPositionCalculator.Position POSITION =
      new LandSeaMaskPositionCalculator.Position(0, 13, 31);

  @Test
  public void acceptsSeaCoastAndUsableSamValues() {
    assertTrue(checkWithSamValue('0'));
    assertTrue(checkWithSamValue('1'));
    assertTrue(checkWithSamValue('4'));
  }

  @Test
  public void rejectsLandAndInvalidSamValues() {
    assertFalse(checkWithSamValue('2'));
    assertFalse(checkWithSamValue('3'));
  }

  @Test
  public void keepsDefaultWhenThePositionIsNotFound() {
    assertTrue(checkWithRecord("999" + spaces(124)));
  }

  @Test
  public void keepsDefaultWhenTheMaskLineHasAnInvalidLength() {
    assertTrue(checkWithRecord("013" + spaces(20)));
  }

  @Test
  public void keepsDefaultWhenTheOctantIsInvalid() {
    assertTrue(
        LandSeaMaskWorkflow.check(
            input("013" + spaces(30) + "3" + spaces(96)),
            new LandSeaMaskPositionCalculator.Position(main.INVALID, 13, 31)));
  }

  private static boolean checkWithSamValue(char samValue) {
    return checkWithRecord("013" + spaces(28) + samValue + spaces(95));
  }

  private static boolean checkWithRecord(String record) {
    return LandSeaMaskWorkflow.check(input(record), POSITION);
  }

  private static ByteArrayInputStream input(String content) {
    return new ByteArrayInputStream((content + "\n").getBytes(StandardCharsets.UTF_8));
  }

  private static String spaces(int count) {
    return " ".repeat(count);
  }
}
