package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/** Characterizes the status and user-visible completion contract shared by both email workflows. */
public class ObservationEmailWorkflowCharacterizationTest {

  @Test
  public void successLogsAndResetsForBothManualAndAutomaticSends() {
    assertEquals(List.of("log", "reset", "sent obs successfully"), successActions(true));
    assertEquals(List.of("log", "reset"), successActions(false));
  }

  @Test
  public void emptyObservationDoesNotWriteAnImmtLog() {
    List<String> actions = new ArrayList<>();

    ObservationEmailEmptyObservationHandler.handle(
        true,
        () -> actions.add("reset"),
        message -> actions.add("system: " + message),
        message -> actions.add("warning: " + message));

    assertEquals(
        List.of(
            "reset",
            "system: " + ObservationEmailEmptyObservationHandler.MESSAGE,
            "warning: " + ObservationEmailEmptyObservationHandler.MESSAGE),
        actions);
  }

  @Test
  public void invalidAndTransportFailuresWriteImmtLog() {
    List<String> invalidActions = new ArrayList<>();
    ObservationEmailInvalidModeHandler.handle(
        false,
        () -> invalidActions.add("log"),
        () -> invalidActions.add("reset"),
        message -> invalidActions.add("system: " + message),
        message -> invalidActions.add("warning"));

    List<String> transportActions = new ArrayList<>();
    ObservationEmailGenericFailureHandler.handle(
        false,
        () -> transportActions.add("log"),
        () -> transportActions.add("reset"),
        message -> transportActions.add("warning"));

    assertEquals(
        List.of("log", "reset", "system: " + ObservationEmailInvalidModeHandler.MESSAGE),
        invalidActions);
    assertEquals(List.of("log", "reset"), transportActions);
  }

  @Test
  public void PythonModuleFailureResetsAndNotifiesOnlyManualSends() {
    List<String> actions = new ArrayList<>();

    ObservationEmailPythonModuleFailureHandler.handle(
        true,
        () -> actions.add("log"),
        () -> actions.add("reset"),
        message -> actions.add("warning: " + message));

    assertEquals(
        List.of("log", "reset", "warning: " + ObservationEmailPythonModuleFailureHandler.MESSAGE),
        actions);
  }

  @Test
  public void invocationFailureLogsAndShowsManualWarning() {
    List<String> actions = new ArrayList<>();

    ObservationEmailInvocationFailureHandler.handle(
        true,
        new IllegalStateException("failed"),
        message -> actions.add("log: " + message),
        message -> actions.add("print: " + message),
        message -> actions.add("warning: " + message));

    assertEquals(
        List.of(
            "log: [EMAIL] error invoking email module (java.lang.IllegalStateException: failed)",
            "print: " + ObservationEmailInvocationFailureHandler.USER_MESSAGE,
            "warning: " + ObservationEmailInvocationFailureHandler.USER_MESSAGE),
        actions);
  }

  private static List<String> successActions(boolean manualSend) {
    List<String> actions = new ArrayList<>();
    ObservationEmailSuccessHandler.complete(
        manualSend,
        () -> actions.add("log"),
        () -> actions.add("reset"),
        message -> actions.add(message));
    return actions;
  }
}
