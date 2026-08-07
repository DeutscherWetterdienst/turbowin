package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationCharacterizationTest {

  private String originalShipName;
  private String originalImoNumber;
  private String originalWindSource;
  private String originalStationId;

  @Before
  public void resetConfigurationLines() {
    originalShipName = main.ship_name;
    originalImoNumber = main.imo_number;
    originalWindSource = main.wind_source;
    originalStationId = main.station_ID;
    Arrays.fill(main.configuratie_regels, "");
  }

  @After
  public void restoreGlobalConfigurationState() {
    main.ship_name = originalShipName;
    main.imo_number = originalImoNumber;
    main.wind_source = originalWindSource;
    main.station_ID = originalStationId;
    Arrays.fill(main.configuratie_regels, "");
  }

  @Test
  public void fillConfigurationArrayUsesTheExistingPrefixedLineFormat() {
    main.ship_name = "Test Ship";
    main.imo_number = "1234567";
    main.wind_source = "estimated";
    main.station_ID = "STATION-1";

    main.fill_configuratie_array();

    assertEquals(main.SHIP_NAME_TXT + "Test Ship", main.configuratie_regels[0]);
    assertEquals(main.IMO_NUMBER_TXT + "1234567", main.configuratie_regels[1]);
    assertEquals(main.WIND_SOURCE_TXT + "estimated", main.configuratie_regels[7]);
    assertEquals(main.STATION_ID_TXT + "STATION-1", main.configuratie_regels[70]);
  }

  @Test
  public void metadataParserReadsValuesAfterTheConfigurationPrefix() {
    main.configuratie_regels[0] = main.SHIP_NAME_TXT + "Parsed Ship";
    main.configuratie_regels[1] = main.IMO_NUMBER_TXT + "7654321";
    main.configuratie_regels[70] = main.STATION_ID_TXT + "PARSED-1";

    main.meta_data_from_configuration_regels_into_global_vars();

    assertEquals("Parsed Ship", main.ship_name);
    assertEquals("7654321", main.imo_number);
    assertEquals("PARSED-1", main.station_ID);
  }
}
