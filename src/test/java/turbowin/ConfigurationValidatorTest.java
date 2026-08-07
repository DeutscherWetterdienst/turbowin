package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationValidatorTest {

  private String originalShipName;
  private String originalStationId;
  private String originalRecruitingCountry;
  private String originalWindSource;
  private String originalMaxHeightDeckCargo;
  private String originalDiffSllWl;
  private String originalPressureReadingMslYesNo;
  private String originalBarometerAboveSll;
  private String originalKeelSll;
  private String originalAirTempExposure;
  private String originalSstExposure;
  private String originalLogsDir;
  private String originalObsFormat;
  private int originalRs232ConnectionMode;

  @Before
  public void saveGlobalState() {
    originalShipName = main.ship_name;
    originalStationId = main.station_ID;
    originalRecruitingCountry = main.recruiting_country;
    originalWindSource = main.wind_source;
    originalMaxHeightDeckCargo = main.max_height_deck_cargo;
    originalDiffSllWl = main.diff_sll_wl;
    originalPressureReadingMslYesNo = main.pressure_reading_msl_yes_no;
    originalBarometerAboveSll = main.barometer_above_sll;
    originalKeelSll = main.keel_sll;
    originalAirTempExposure = main.air_temp_exposure;
    originalSstExposure = main.sst_exposure;
    originalLogsDir = main.logs_dir;
    originalObsFormat = main.obs_format;
    originalRs232ConnectionMode = main.RS232_connection_mode;

    main.ship_name = "Test Ship";
    main.station_ID = "STATION-1";
    main.recruiting_country = "NL";
    main.wind_source = "measured";
    main.max_height_deck_cargo = "1";
    main.diff_sll_wl = "1";
    main.pressure_reading_msl_yes_no = "no";
    main.barometer_above_sll = "1";
    main.keel_sll = "1";
    main.air_temp_exposure = "air";
    main.sst_exposure = "sea";
    main.logs_dir = "/tmp";
    main.obs_format = main.FORMAT_FM13;
    main.RS232_connection_mode = 0;
  }

  @After
  public void restoreGlobalState() {
    main.ship_name = originalShipName;
    main.station_ID = originalStationId;
    main.recruiting_country = originalRecruitingCountry;
    main.wind_source = originalWindSource;
    main.max_height_deck_cargo = originalMaxHeightDeckCargo;
    main.diff_sll_wl = originalDiffSllWl;
    main.pressure_reading_msl_yes_no = originalPressureReadingMslYesNo;
    main.barometer_above_sll = originalBarometerAboveSll;
    main.keel_sll = originalKeelSll;
    main.air_temp_exposure = originalAirTempExposure;
    main.sst_exposure = originalSstExposure;
    main.logs_dir = originalLogsDir;
    main.obs_format = originalObsFormat;
    main.RS232_connection_mode = originalRs232ConnectionMode;
  }

  @Test
  public void reportsMissingShipNameFirst() {
    main.ship_name = "";
    main.station_ID = "STATION-1";

    assertEquals(
        "Ship name: unknown (select: Maintenance -> Station data)",
        ConfigurationValidator.findWarning());
  }

  @Test
  public void reportsMissingStationIdAfterShipName() {
    main.ship_name = "Test Ship";
    main.station_ID = "";

    assertEquals(
        "station ID: unknown (select: Maintenance -> Station data)",
        ConfigurationValidator.findWarning());
  }

  @Test
  public void recognizesAllAwsConnectionModes() {
    main.obs_format = main.FORMAT_AWS;

    for (int mode : new int[] {3, 9, 10, 11}) {
      main.RS232_connection_mode = mode;

      assertEquals("mode " + mode, "", ConfigurationValidator.findWarning());
    }
  }

  @Test
  public void reportsMissingAwsConnectionForAwsFormat() {
    main.obs_format = main.FORMAT_AWS;
    main.RS232_connection_mode = 0;

    assertEquals(
        "if obs format = \"AWS connected\" (see Maintenance -> Obs format setting) then set also the AWS connection (select: Maintenance -> Serial/USB/LAN connection settings)",
        ConfigurationValidator.findWarning());
  }

  @Test
  public void pressureWarningTakesPrecedenceOverAwsWarning() {
    main.obs_format = main.FORMAT_AWS;
    main.RS232_connection_mode = 1;
    main.pressure_reading_msl_yes_no = "yes";

    assertEquals(
        "If AWS or barometer connected (Maintenance -> Serial/USB/LAN connection settings): \"the reading does not indicate the MSL pressure\"\nPlease correct this in Maintenance -> Station data",
        ConfigurationValidator.findWarning());
  }

  @Test
  public void laterWarningsTakePrecedenceOverMissingMetadata() {
    main.ship_name = "";
    main.RS232_connection_mode = 1;
    main.pressure_reading_msl_yes_no = "yes";

    assertEquals(
        "If AWS or barometer connected (Maintenance -> Serial/USB/LAN connection settings): \"the reading does not indicate the MSL pressure\"\nPlease correct this in Maintenance -> Station data",
        ConfigurationValidator.findWarning());
  }
}
