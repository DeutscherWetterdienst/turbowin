package turbowin;

import java.util.function.Consumer;

/** Handles an exception raised while invoking an observation email transport. */
final class ObservationEmailInvocationFailureHandler {

  static final String USER_MESSAGE = "send obs failed (check Info -> System log)";

  private ObservationEmailInvocationFailureHandler() {}

  static void handle(
      boolean manualSend,
      Exception exception,
      Consumer<String> log,
      Consumer<String> print,
      Consumer<String> notify) {
    log.accept("[EMAIL] error invoking email module (" + exception + ")");
    if (manualSend) {
      print.accept(USER_MESSAGE);
      notify.accept(USER_MESSAGE);
    }
  }
}
