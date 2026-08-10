package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ObservationEmailEmptyObservationHandlerTest {

  @Test
  public void resetsLogsAndNotifiesManualFailure() {
    List<String> actions = new ArrayList<>();

    ObservationEmailEmptyObservationHandler.handle(
        true,
        () -> actions.add("reset"),
        message -> actions.add("log: " + message),
        message -> actions.add("notify: " + message));

    assertEquals(
        List.of(
            "reset",
            "log: " + ObservationEmailEmptyObservationHandler.MESSAGE,
            "notify: " + ObservationEmailEmptyObservationHandler.MESSAGE),
        actions);
  }

  @Test
  public void doesNotNotifyAutomaticFailure() {
    List<String> actions = new ArrayList<>();

    ObservationEmailEmptyObservationHandler.handle(
        false,
        () -> actions.add("reset"),
        message -> actions.add("log"),
        message -> actions.add("notify"));

    assertEquals(List.of("reset", "log"), actions);
  }
}
