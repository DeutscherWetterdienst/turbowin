package turbowin;

import static turbowin.main.*;

/** Calculates centered positions for the main window and its auxiliary frames. */
final class WindowLayoutCalculator {

  private WindowLayoutCalculator() {}

  static void calculate(int width, int height) {
    screenWidth = width;
    screenHeight = height;

    x_pos_start_frame = width / 2 - (1050 / 2);
    y_pos_start_frame = height / 2 - (740 / 2);

    x_pos_main_frame = width / 2 - (1000 / 2);
    y_pos_main_frame = height / 2 - (700 / 2);

    x_pos_amver_frame = width / 2 - (1000 / 2);
    y_pos_amver_frame = height / 2 - (750 / 2);

    x_pos_frame = width / 2 - (800 / 2);
    y_pos_frame = height / 2 - (600 / 2);

    x_pos_small_frame = width / 2 - (400 / 2);
    y_pos_small_frame = height / 2 - (300 / 2);

    x_pos_about_frame = width / 2 - (600 / 2);
    y_pos_about_frame = height / 2 - (700 / 2);

    x_pos_calculator_frame = width / 2 - (350 / 2);
    y_pos_calculator_frame = height / 2 - (550 / 2);

    x_pos_pop_up_frame = width / 2 - (401 / 2);
    y_pos_pop_up_frame = height / 2 - (236 / 2);

    x_pos_immtlogperiod_frame = width / 2 - (600 / 2);
    y_pos_immtlogperiod_frame = height / 2 - (300 / 2);
  }
}
