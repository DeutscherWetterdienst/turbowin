package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects the JavaFX wind form with observation calculations and sequence navigation. */
final class WindDialog {
  private WindDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    mywind model = new mywind();
    WindView.State state =
        new WindView.State(
            model.initialValues(),
            main.wind_source,
            main.wind_units.contains(main.M_S),
            main.true_wind_dir_from_AWS_present || main.true_wind_speed_from_AWS_present,
            main.wind_source.equals(main.ESTIMATED_TRUE),
            main.in_next_sequence,
            main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Wind",
        new Dimension(800, 650),
        close ->
            WindView.create(
                state,
                value ->
                    SwingUtilities.invokeLater(() -> {
                      if (model.accept(value)) {
                        close.run();
                        if (main.in_next_sequence) next();
                      }
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      if (main.in_next_sequence) next();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      TemperatureDialog.open();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      main.in_next_sequence = false;
                    }),
                () -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("wind.pdf"))));
  }

  private static void next() {
    WavesDialog.open();
  }
}
