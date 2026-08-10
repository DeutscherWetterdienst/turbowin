package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ObservationEmailInvalidModeHandlerTest {

  @Test
  public void logsResetsAndNotifiesManualFailure() {
    List<String> actions = new ArrayList<>();

    ObservationEmailInvalidModeHandler.handle(
        true,
        () -> actions.add("log observation"),
        () -> actions.add("reset"),
        message -> actions.add("log: " + message),
        message -> actions.add("notify: " + message));

    assertEquals(
        List.of(
            "log observation",
            "reset",
            "log: " + ObservationEmailInvalidModeHandler.MESSAGE,
            "notify: " + ObservationEmailInvalidModeHandler.MESSAGE),
        actions);
  }

  @Test
  public void doesNotNotifyAutomaticFailure() {
    List<String> actions = new ArrayList<>();

    ObservationEmailInvalidModeHandler.handle(
        false,
        () -> actions.add("log observation"),
        () -> actions.add("reset"),
        message -> actions.add("log"),
        message -> actions.add("notify"));

    assertEquals(List.of("log observation", "reset", "log"), actions);
  }
}
