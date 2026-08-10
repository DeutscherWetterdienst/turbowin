package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObservationEmailStatusTest {

  @Test
  public void preservesTheLegacyStatusCodes() {
    assertEquals(1000, ObservationEmailStatus.PYTHON_MODULE_UNAVAILABLE);
    assertEquals(1001, ObservationEmailStatus.EMPTY_OBSERVATION);
    assertEquals(1002, ObservationEmailStatus.INVALID_MODE);
  }
}
