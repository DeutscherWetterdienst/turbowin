package turbowin;

import java.util.function.Consumer;

/** Selects the synchronous or asynchronous strategy for loading toolbar images. */
final class ToolbarImageInitializationWorkflow {

  private static final String[] TOOLBAR_IMAGES = {
    "date_time.png",
    "position.png",
    "wind.png",
    "waves.png",
    "barometer.png",
    "barograph.png",
    "temperatures.png",
    "present_weather.png",
    "past_weather.png",
    "visibility.png",
    "cl.png",
    "cm.png",
    "ch.png",
    "height.png",
    "icing.png",
    "ice.png",
    "observers.png",
    "captains.png",
    "next_screen.png"
  };

  private ToolbarImageInitializationWorkflow() {}

  static void initialize(
      boolean useSynchronousLoading,
      Consumer<String> synchronousLoader,
      Consumer<String> asynchronousLoader) {
    // Fedora/Linux background image loading intermittently failed even when the resources existed,
    // so synchronous loading is deliberately used there.
    Consumer<String> loader = useSynchronousLoading ? synchronousLoader : asynchronousLoader;
    for (String image : TOOLBAR_IMAGES) {
      loader.accept(main.ICONS_DIRECTORY + image);
    }
  }
}
