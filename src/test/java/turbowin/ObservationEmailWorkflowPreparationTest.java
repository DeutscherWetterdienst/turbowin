package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ObservationEmailWorkflowPreparationTest {

  @Test
  public void invalidModeProducesInvalidStatusWithoutARequest() {
    String originalMode = main.email_send_mode;
    String originalAutomaticMode = main.APTR_AWSR_send_method;
    try {
      main.email_send_mode = "unknown";
      main.APTR_AWSR_send_method = "unknown";

      ObservationEmailWorkflowPreparation.Result result =
          ObservationEmailWorkflowPreparation.prepare(true);

      assertEquals(1002, result.status());
      assertNull(result.request());
    } finally {
      main.email_send_mode = originalMode;
      main.APTR_AWSR_send_method = originalAutomaticMode;
    }
  }
}
