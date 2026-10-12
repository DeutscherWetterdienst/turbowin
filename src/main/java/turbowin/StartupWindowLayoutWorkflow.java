package turbowin;

import java.awt.Dimension;
import java.awt.Toolkit;

/** Positions the main window using the shared startup layout calculation. */
final class StartupWindowLayoutWorkflow {

  private StartupWindowLayoutWorkflow() {}

  static void initialize(main owner) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    WindowLayoutCalculator.calculate(screenSize.width, screenSize.height);
    owner.setLocation(main.x_pos_start_frame, main.y_pos_start_frame);
  }
}
