package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationApplyCharacterizationTest {

  private String[] originalConfigurationLines;
  private String originalShipName;
  private String originalWindSource;
  private String originalStationId;
  private int originalConnectionMode;
  private int originalDataBits;
  private int originalParity;
  private int originalStopBits;
  private boolean originalWow;
  private boolean originalEucawsObsId;
  private String originalSendMethod;
  private String originalComPortNumber;
  private String originalComPortName;
  private String originalComPort;
  private String originalGpsComPortNumber;
  private String originalGpsComPortName;
  private String originalGpsComPort;

  @Before
  public void saveGlobalState() {
    originalConfigurationLines =
        Arrays.copyOf(main.configuratie_regels, main.configuratie_regels.length);
    originalShipName = main.ship_name;
    originalWindSource = main.wind_source;
    originalStationId = main.station_ID;
    originalConnectionMode = main.RS232_connection_mode;
    originalDataBits = main.data_bits;
    originalParity = main.parity;
    originalStopBits = main.stop_bits;
    originalWow = main.WOW;
    originalEucawsObsId = main.eucaws_obs_id;
    originalSendMethod = main.APTR_AWSR_send_method;
    originalComPortNumber = main.prefered_COM_port_number;
    originalComPortName = main.prefered_COM_port_name;
    originalComPort = main.prefered_COM_port;
    originalGpsComPortNumber = main.prefered_GPS_COM_port_number;
    originalGpsComPortName = main.prefered_GPS_COM_port_name;
    originalGpsComPort = main.prefered_GPS_COM_port;
    Arrays.fill(main.configuratie_regels, "");
  }

  @After
  public void restoreGlobalState() {
    main.ship_name = originalShipName;
    main.wind_source = originalWindSource;
    main.station_ID = originalStationId;
    main.RS232_connection_mode = originalConnectionMode;
    main.data_bits = originalDataBits;
    main.parity = originalParity;
    main.stop_bits = originalStopBits;
    main.WOW = originalWow;
    main.eucaws_obs_id = originalEucawsObsId;
    main.APTR_AWSR_send_method = originalSendMethod;
    main.prefered_COM_port_number = originalComPortNumber;
    main.prefered_COM_port_name = originalComPortName;
    main.prefered_COM_port = originalComPort;
    main.prefered_GPS_COM_port_number = originalGpsComPortNumber;
    main.prefered_GPS_COM_port_name = originalGpsComPortName;
    main.prefered_GPS_COM_port = originalGpsComPort;
    System.arraycopy(
        originalConfigurationLines,
        0,
        main.configuratie_regels,
        0,
        originalConfigurationLines.length);
  }

  @Test
  public void appliesRepresentativeScalarAndTypedConfigurationValues() {
    main.configuratie_regels[0] = main.SHIP_NAME_TXT + "Configured Ship";
    main.configuratie_regels[7] = main.WIND_SOURCE_TXT + "estimated";
    main.configuratie_regels[20] = main.RS232_INSTRUMENT_TYPE_TXT + "2";
    main.configuratie_regels[22] = main.RS232_DATA_BITS_TXT + "7";
    main.configuratie_regels[23] = main.RS232_PARITY_TXT + "1";
    main.configuratie_regels[24] = main.RS232_STOP_BITS_TXT + "2";
    main.configuratie_regels[31] = main.WOW_PUBLISH_TXT + "true";
    main.configuratie_regels[70] = main.STATION_ID_TXT + "STATION-1";
    main.configuratie_regels[88] = main.EUCAWS_OBS_ID_TXT + "true";

    ConfigurationManager.applyConfigurationLines();

    assertEquals("Configured Ship", main.ship_name);
    assertEquals("estimated", main.wind_source);
    assertEquals(2, main.RS232_connection_mode);
    assertEquals(7, main.data_bits);
    assertEquals(1, main.parity);
    assertEquals(SerialPort.TWO_STOP_BITS, main.stop_bits);
    assertTrue(main.WOW);
    assertEquals("STATION-1", main.station_ID);
    assertTrue(main.eucaws_obs_id);
  }

  @Test
  public void clearsObsoleteSendMethodAndSelectsConfiguredPortNames() {
    main.eucaws_obs_id = false;
    main.configuratie_regels[69] = main.APTR_AWSR_SEND_METHOD_TXT + main.APTR_AWSR_GMAIL;
    main.configuratie_regels[25] = main.RS232_PREFERED_COM_PORT_TXT + "";
    main.configuratie_regels[30] = main.RS232_PREF_COM_PORT_NAME_TXT + "COM-A";
    main.configuratie_regels[39] = main.RS232_GPS_COM_PORT_TXT + "GPS-1";
    main.configuratie_regels[40] = main.RS232_GPS_COM_PORT_NAME_TXT + "GPS-A";

    ConfigurationManager.applyConfigurationLines();

    assertEquals("", main.APTR_AWSR_send_method);
    assertEquals("COM-A", main.prefered_COM_port);
    assertEquals("GPS-1", main.prefered_GPS_COM_port);
    assertFalse(main.eucaws_obs_id);
  }
}
