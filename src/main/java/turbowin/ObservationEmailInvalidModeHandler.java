package turbowin;

import java.util.function.Consumer;

/** Handles an invalid manual or automatic observation email mode. */
final class ObservationEmailInvalidModeHandler {

  static final String MESSAGE = "send obs failed (invalid manual or AP[T]R/AWSR email send mode)";

  private ObservationEmailInvalidModeHandler() {}

  static void handle(
      boolean manualSend,
      Runnable logObservation,
      Runnable resetObservation,
      Consumer<String> logFailure,
      Consumer<String> notify) {
    logObservation.run();
    resetObservation.run();
    logFailure.accept(MESSAGE);
    if (manualSend) {
      notify.accept(MESSAGE);
    }
  }
}
