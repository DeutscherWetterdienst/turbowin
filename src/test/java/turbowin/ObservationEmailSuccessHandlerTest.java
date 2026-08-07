package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ObservationEmailSuccessHandlerTest {

  @Test
  public void finalizesAndNotifiesManualSendInOrder() {
    List<String> actions = new ArrayList<>();

    ObservationEmailSuccessHandler.complete(
        true,
        () -> actions.add("log"),
        () -> actions.add("reset"),
        message -> actions.add(message));

    assertEquals(List.of("log", "reset", "sent obs successfully"), actions);
  }

  @Test
  public void doesNotNotifyAutomaticSend() {
    List<String> actions = new ArrayList<>();

    ObservationEmailSuccessHandler.complete(
        false,
        () -> actions.add("log"),
        () -> actions.add("reset"),
        message -> actions.add(message));

    assertEquals(List.of("log", "reset"), actions);
  }
}
