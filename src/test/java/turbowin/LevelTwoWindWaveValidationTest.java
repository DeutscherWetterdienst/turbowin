package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LevelTwoWindWaveValidationTest {

  @Test
  public void acceptsZeroWindAtTheWaveBoundaries() {
    assertTrue(LevelTwoWindWaveValidation.validate(main.M_S, 0, true, 0.01f, true, 0.01f));
  }

  @Test
  public void acceptsWaveHeightAtTheUpperBoundary() {
    assertTrue(LevelTwoWindWaveValidation.validate(main.M_S, 3, true, 0.0f, true, 9.7f));
    assertTrue(LevelTwoWindWaveValidation.validate(main.KNOTS, 6, true, 0.0f, true, 9.7f));
  }

  @Test
  public void acceptsWaveValuesAtTheUpperSentinelBoundary() {
    assertTrue(LevelTwoWindWaveValidation.validate(main.M_S, 10, true, 99.9f, true, 99.9f));
  }

  @Test
  public void acceptsWhenConversionsFail() {
    assertTrue(LevelTwoWindWaveValidation.validate(main.M_S, 0, false, 5.0f, false, 5.0f));
  }
}
