package turbowin;

import java.util.function.Consumer;

/**
 * Performs the shared finalization after an observation email succeeds.
 *
 * <p>The transport path has already recorded the success in the system log.
 */
final class ObservationEmailSuccessHandler {

  private ObservationEmailSuccessHandler() {}

  static void complete(
      boolean manualSend,
      Runnable logObservation,
      Runnable resetObservation,
      Consumer<String> notify) {
    logObservation.run();
    resetObservation.run();
    if (manualSend) {
      notify.accept("sent obs successfully");
    }
  }
}
