package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ObservationEmailGenericFailureHandlerTest {

  @Test
  public void logsResetsAndNotifiesManualFailure() {
    List<String> actions = new ArrayList<>();

    ObservationEmailGenericFailureHandler.handle(
        true,
        () -> actions.add("log"),
        () -> actions.add("reset"),
        message -> actions.add(message));

    assertEquals(List.of("log", "reset", ObservationEmailGenericFailureHandler.MESSAGE), actions);
  }

  @Test
  public void doesNotNotifyAutomaticFailure() {
    List<String> actions = new ArrayList<>();

    ObservationEmailGenericFailureHandler.handle(
        false,
        () -> actions.add("log"),
        () -> actions.add("reset"),
        message -> actions.add("notify"));

    assertEquals(List.of("log", "reset"), actions);
  }
}
