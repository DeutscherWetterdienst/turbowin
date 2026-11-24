package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;

final class BarometerComparisonDialog {
  private BarometerComparisonDialog() {}

  static JDialog open() {
    String ship = main.ship_name;
    String height = main.barometer_above_sll;
    String keel = main.keel_sll;
    return FxSwingDialog.show(
        main.mainClass,
        "Barometer comparison",
        new Dimension(1000, 700),
        close ->
            BarometerComparisonView.create(
                ship,
                height,
                keel,
                () -> main.PRESSURE_READING_MSL_YES.equals(main.pressure_reading_msl_yes_no),
                close));
  }
}
