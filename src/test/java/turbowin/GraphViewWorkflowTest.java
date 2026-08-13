package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GraphViewWorkflowTest {

  @Test
  public void selectsSecondaryAirTemperatureGraphWhenConfigured() {
    assertEquals(main.MODE_AIRTEMP_II, GraphViewWorkflow.airTemperatureMode(1));
    assertEquals(main.MODE_AIRTEMP, GraphViewWorkflow.airTemperatureMode(0));
  }
}
