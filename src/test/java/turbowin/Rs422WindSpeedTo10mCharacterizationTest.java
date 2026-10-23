package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Rs422WindSpeedTo10mCharacterizationTest {

  private String originalHeight;
  private final main_RS232_RS422 owner = new main_RS232_RS422();

  @Before
  public void saveState() {
    originalHeight = main.height_anemometer;
  }

  @After
  public void restoreState() {
    main.height_anemometer = originalHeight;
  }

  @Test
  public void convertsValidWindSpeedAtValidSensorHeight() {
    main.height_anemometer = "20";

    double expected =
        10.0 * Math.log(10 / 0.0002) / Math.log(Integer.parseInt(main.height_anemometer) / 0.0002);

    assertEquals(expected, owner.RS422_convert_wind_speed_to_10m(10.0), 0.0);
  }

  @Test
  public void returnsInvalidForHeightAtOrBelowTenMeters() {
    main.height_anemometer = "10";

    assertEquals(main.INVALID, owner.RS422_convert_wind_speed_to_10m(10.0), 0.0);
  }

  @Test
  public void returnsInvalidForHeightAtOrAboveTwoHundredMeters() {
    main.height_anemometer = "200";

    assertEquals(main.INVALID, owner.RS422_convert_wind_speed_to_10m(10.0), 0.0);
  }

  @Test
  public void returnsInvalidForMissingOrMalformedHeight() {
    main.height_anemometer = "";
    assertEquals(main.INVALID, owner.RS422_convert_wind_speed_to_10m(10.0), 0.0);

    main.height_anemometer = "not-a-number";
    assertEquals(main.INVALID, owner.RS422_convert_wind_speed_to_10m(10.0), 0.0);
  }

  @Test
  public void returnsInvalidForWindSpeedOutsideAllowedRange() {
    main.height_anemometer = "20";

    assertEquals(main.INVALID, owner.RS422_convert_wind_speed_to_10m(-0.1), 0.0);
    assertEquals(main.INVALID, owner.RS422_convert_wind_speed_to_10m(500.0), 0.0);
  }
}
