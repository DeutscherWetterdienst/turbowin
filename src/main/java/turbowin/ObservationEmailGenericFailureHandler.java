package turbowin;

import java.util.function.Consumer;

/**
 * Handles a generic observation email transport failure.
 *
 * <p>The detailed transport failure is already written to the system log in {@code doInBackground}
 * before this finalization runs.
 */
final class ObservationEmailGenericFailureHandler {

  static final String MESSAGE = "send obs failed (see Info -> System log)";

  private ObservationEmailGenericFailureHandler() {}

  static void handle(
      boolean manualSend,
      Runnable logObservation,
      Runnable resetObservation,
      Consumer<String> notify) {
    logObservation.run();
    resetObservation.run();
    if (manualSend) {
      notify.accept(MESSAGE);
    }
  }
}
