package turbowin;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.SwingUtilities;

/** Coordinates asynchronous saving and the user's decision before any shutdown cleanup. */
final class ApplicationExitRequest {
  enum Decision {
    RETRY,
    CANCEL,
    EXIT_WITHOUT_SAVING
  }

  private final BiConsumer<Consumer<ConfigurationWriteWorkflow.SaveResult>, Runnable> save;
  private final Function<ConfigurationWriteWorkflow.SaveResult, Decision> decide;
  private final Runnable exit;
  private final Runnable cancel;
  private boolean waiting;
  private boolean finished;

  ApplicationExitRequest(
      BiConsumer<Consumer<ConfigurationWriteWorkflow.SaveResult>, Runnable> save,
      Function<ConfigurationWriteWorkflow.SaveResult, Decision> decide,
      Runnable exit,
      Runnable cancel) {
    this.save = save;
    this.decide = decide;
    this.exit = exit;
    this.cancel = cancel;
  }

  void start() {
    if (waiting || finished) {
      return;
    }
    waiting = true;
    save.accept(this::saved, this::cancelExit);
  }

  void cancelExit() {
    if (!finished) {
      finished = true;
      cancel.run();
    }
  }

  private void saved(ConfigurationWriteWorkflow.SaveResult result) {
    if (!waiting || finished) {
      return;
    }
    if (result.successful()) {
      finished = true;
      exit.run();
      return;
    }
    // Keep the request active while a modal decision dialog processes Swing events.
    Decision decision = decide.apply(result);
    if (decision == Decision.RETRY) {
      waiting = false;
      // Unwind the previous modal progress/decision dialogs before opening another attempt.
      SwingUtilities.invokeLater(this::start);
    } else {
      finished = true;
      if (decision == Decision.EXIT_WITHOUT_SAVING) {
        exit.run();
      } else {
        cancel.run();
      }
    }
  }
}
