package turbowin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WindWaveConsistencyValidationTest {

  @Test
  public void acceptsZeroWaveHeightAtEachSpeedThreshold() {
    assertTrue(WindWaveConsistencyValidation.validate(main.M_S, 5, true, 2.31f));
    assertTrue(WindWaveConsistencyValidation.validate(main.KNOTS, 10, true, 2.31f));
    assertTrue(WindWaveConsistencyValidation.validate(main.M_S, 21, true, 2.31f));
    assertTrue(WindWaveConsistencyValidation.validate(main.KNOTS, 41, true, 2.31f));
  }

  @Test
  public void acceptsWaveHeightAtTheStrictUpperBoundary() {
    assertTrue(WindWaveConsistencyValidation.validate(main.M_S, 22, true, 2.31f));
  }

  @Test
  public void acceptsTheUpperSpeedSentinelBoundary() {
    assertTrue(WindWaveConsistencyValidation.validate(main.M_S, 500, true, 0.0f));
  }

  @Test
  public void acceptsWhenWaveHeightConversionFails() {
    assertTrue(WindWaveConsistencyValidation.validate(main.M_S, 30, false, 0.0f));
  }
}
