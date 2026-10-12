package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Characterizes the observable state established by main startup. */
public class MainStartupCharacterizationTest extends AbstractUiTest {

  @Test
  public void startupInitializesCoreApplicationState() {
    assertEquals(main.MODE_PRESSURE, main.mode_grafiek);
  }
}
