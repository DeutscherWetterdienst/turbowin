package turbowin;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/** Connects past-weather choices to observation state and sequence navigation. */
final class PastWeatherDialog {
  private PastWeatherDialog() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException("Swing event thread required");
    PastWeatherView.State state =
        new PastWeatherView.State(
            PastWeatherState.past_weather_1,
            PastWeatherState.past_weather_2,
            main.in_next_sequence,
            main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        "Past weather",
        new Dimension(800, 700),
        close ->
            PastWeatherView.create(
                state,
                choices ->
                    SwingUtilities.invokeLater(() -> {
                      save(choices);
                      close.run();
                      if (main.in_next_sequence) LowCloudDialog.open();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      if (main.in_next_sequence) LowCloudDialog.open();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      PresentWeatherDialog.open();
                    }),
                () ->
                    SwingUtilities.invokeLater(() -> {
                      close.run();
                      main.in_next_sequence = false;
                    }),
                () -> SwingUtilities.invokeLater(() -> main.help_mouseClicked("weather.pdf"))));
  }

  private static void save(List<PastWeatherView.Choice> choices) {
    PastWeatherState.W1_code = "";
    PastWeatherState.W2_code = "";
    PastWeatherState.past_weather_1 = "";
    PastWeatherState.past_weather_2 = "";
    if (!choices.isEmpty()) {
      PastWeatherView.Choice first = choices.get(0);
      PastWeatherView.Choice second = choices.size() > 1 ? choices.get(1) : first;
      PastWeatherState.W1_code = first.code();
      PastWeatherState.W2_code = second.code();
      PastWeatherState.past_weather_1 = first.value();
      PastWeatherState.past_weather_2 = second.value();
    }
    main.past_weather_fields_update();
  }
}
