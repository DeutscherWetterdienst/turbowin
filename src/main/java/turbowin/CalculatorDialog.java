package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;

/** Opens the unit calculator in a JavaFX dialog. */
final class CalculatorDialog {
  private CalculatorDialog() {}

  static JDialog open() {
    return FxSwingDialog.show(
        main.mainClass,
        "Calculator " + main.APPLICATION_NAME,
        new Dimension(500, 600),
        CalculatorView::create);
  }
}
