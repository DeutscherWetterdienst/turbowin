package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PositionMapPreparationWorkflowTest {

  @Test
  public void formatsWindValuesForMap() {
    PositionMapPreparationWorkflow.Observation observation =
        PositionMapPreparationWorkflow.prepare(
            "01", "JAN", "2026", "12", 5, 999, -1, 10, "m/s", "", "", "");

    assertEquals("5 degr", observation.windDirection);
    assertEquals("10 m/s", observation.windSpeed);
  }

  @Test
  public void formatsTemperaturesAndPressureForMap() {
    PositionMapPreparationWorkflow.Observation observation =
        PositionMapPreparationWorkflow.prepare(
            "01", "JAN", "2026", "12", 999, 999, -1, -1, "knots", "12", "4.5", "1012");

    assertEquals("12.0 &#176C", observation.airTemperature);
    assertEquals("4.5 &#176C", observation.seaWaterTemperature);
    assertEquals("1012 hPa", observation.mslPressure);
  }

  @Test
  public void clearsInvalidMapValues() {
    PositionMapPreparationWorkflow.Observation observation =
        PositionMapPreparationWorkflow.prepare(
            "01", "JAN", "2026", "12", -1, 999, -1, -1, "knots", "", "", "");

    assertEquals("", observation.windDirection);
    assertEquals("", observation.windSpeed);
    assertEquals("", observation.airTemperature);
    assertEquals("", observation.seaWaterTemperature);
    assertEquals("", observation.mslPressure);
  }

  @Test(expected = NullPointerException.class)
  public void preservesNullTemperatureFailureBehavior() {
    PositionMapPreparationWorkflow.prepare(
        "01", "JAN", "2026", "12", -1, 999, -1, -1, "knots", null, "", "");
  }
}
