package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ObservationEmailInvocationFailureHandlerTest {

  @Test
  public void logsPrintsAndNotifiesManualFailure() {
    List<String> actions = new ArrayList<>();
    Exception exception = new IllegalStateException("failed");

    ObservationEmailInvocationFailureHandler.handle(
        true,
        exception,
        message -> actions.add("log: " + message),
        message -> actions.add("print: " + message),
        message -> actions.add("notify: " + message));

    assertEquals(
        List.of(
            "log: [EMAIL] error invoking email module (java.lang.IllegalStateException: failed)",
            "print: " + ObservationEmailInvocationFailureHandler.USER_MESSAGE,
            "notify: " + ObservationEmailInvocationFailureHandler.USER_MESSAGE),
        actions);
  }

  @Test
  public void onlyLogsAutomaticFailure() {
    List<String> actions = new ArrayList<>();

    ObservationEmailInvocationFailureHandler.handle(
        false,
        new Exception("failed"),
        message -> actions.add("log"),
        message -> actions.add("print"),
        message -> actions.add("notify"));

    assertEquals(List.of("log"), actions);
  }
}
