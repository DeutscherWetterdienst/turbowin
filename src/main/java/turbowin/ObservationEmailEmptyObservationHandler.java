package turbowin;

import java.util.function.Consumer;

/**
 * Handles the shared completion path for an observation with no data.
 * Empty observations are reset and reported to the system log, but intentionally not written to
 * the IMMT observation log.
 */
final class ObservationEmailEmptyObservationHandler {

  static final String MESSAGE =
      "send obs failed (observation contains no data due to an internal error)";

  private ObservationEmailEmptyObservationHandler() {}

  static void handle(
      boolean manualSend,
      Runnable resetObservation,
      Consumer<String> logFailure,
      Consumer<String> notify) {
    resetObservation.run();
    logFailure.accept(MESSAGE);
    if (manualSend) {
      notify.accept(MESSAGE);
    }
  }
}
