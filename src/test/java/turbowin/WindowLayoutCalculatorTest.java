package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WindowLayoutCalculatorTest {

  @Test
  public void calculatesCenteredFramePositions() {
    WindowLayoutCalculator.calculate(1920, 1080);

    assertEquals(1920, main.screenWidth);
    assertEquals(1080, main.screenHeight);
    assertEquals(435, main.x_pos_start_frame);
    assertEquals(170, main.y_pos_start_frame);
    assertEquals(460, main.x_pos_main_frame);
    assertEquals(240, main.y_pos_frame);
    assertEquals(760, main.x_pos_pop_up_frame);
  }
}
