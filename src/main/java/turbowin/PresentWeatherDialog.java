package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Connects the JavaFX present-weather selector to the Swing observation workflow. */
final class PresentWeatherDialog {
  private PresentWeatherDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    PresentWeatherView.State state =
        new PresentWeatherView.State(
            PresentWeatherState.ww_code,
            PresentWeatherState.present_weather,
            main.in_next_sequence,
            main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Present weather",
        new Dimension(800, 700),
        close ->
            PresentWeatherView.create(
                state,
                input ->
                    SwingUtilities.invokeLater(() -> {
                      PresentWeatherState.ww_code = "";
                      PresentWeatherState.present_weather = "";
                      main.present_weather_fields_update();
                      if (input.weather() == null && input.generalSelected()) {
                        JOptionPane.showMessageDialog(
                            null,
                            "specific weather condition not selected",
                            main.APPLICATION_NAME + " warning",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                      }
                      if (input.weather() != null) {
                        PresentWeatherState.ww_code = input.weather().code();
                        PresentWeatherState.present_weather = input.weather().description();
                        main.present_weather_fields_update();
                      }
                      close.run();
                      if (main.in_next_sequence) next();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      if (main.in_next_sequence) next();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      VisibilityDialog.open();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      main.in_next_sequence = false;
                    }),
                () -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("weather.pdf"))));
  }

  private static void next() {
    PastWeatherDialog.open();
  }
}
