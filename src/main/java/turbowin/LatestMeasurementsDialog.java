package turbowin;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JDialog;

final class LatestMeasurementsDialog {
  private LatestMeasurementsDialog() {}

  static JDialog open() {
    AtomicBoolean closed = new AtomicBoolean();
    boolean knots = main.wind_units_dashboard.contains(main.KNOTS);
    JDialog dialog =
        FxSwingDialog.show(
            main.mainClass,
            "Latest AWS measurements",
            new Dimension(1000, 700),
            close -> LatestMeasurementsView.create(closed, knots, close));
    dialog.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosed(WindowEvent event) {
            closed.set(true);
          }
        });
    return dialog;
  }
}
