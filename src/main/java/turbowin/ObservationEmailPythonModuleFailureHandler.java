package turbowin;

import java.util.function.Consumer;

/**
 * Handles failure to locate or copy the Python email module.
 *
 * <p>The copy/discovery operation has already written the failure cause to the separate Python
 * diagnostic log at {@code ../logs/python/log_python_email.txt}; this handler only performs the
 * shared observation log, reset, and user-notification actions.
 */
final class ObservationEmailPythonModuleFailureHandler {

  static final String MESSAGE =
      "error invoking email module (copy-error from jar to destination) (check Info -> System log)";

  private ObservationEmailPythonModuleFailureHandler() {}

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
