package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObservationComposerTest {

  private String originalStationId;
  private String originalYearDayCode;
  private String originalHourCode;
  private String originalQcCode;
  private String originalLatitudeCode;
  private String originalLongitudeCode;
  private String originalWindSpeedCode;
  private String originalExtendedWindSpeedCode;
  private String originalMiddleCloudCode;
  private String originalSwellDirectionCode;
  private String originalSwellPeriodCode;
  private String originalSwellHeightCode;
  private String originalIcingCauseCode;
  private String originalIcingThicknessCode;
  private String originalIcingRateCode;
  private String originalIceConcentrationCode;
  private String originalIceDevelopmentCode;

  @Before
  public void saveObservationState() {
    originalStationId = main.station_ID;
    originalYearDayCode = mydatetime.YY_code;
    originalHourCode = mydatetime.GG_code;
    originalQcCode = myposition.Qc_code;
    originalLatitudeCode = myposition.lalala_code;
    originalLongitudeCode = myposition.lolololo_code;
    originalWindSpeedCode = mywind.ff_code;
    originalExtendedWindSpeedCode = mywind.fff00_code;
    originalMiddleCloudCode = mycm.cm_code;
    originalSwellDirectionCode = mywaves.Dw1_code;
    originalSwellPeriodCode = mywaves.Pw1_code;
    originalSwellHeightCode = mywaves.Hw1_code;
    originalIcingCauseCode = myicing.Is_code;
    originalIcingThicknessCode = myicing.EsEs_code;
    originalIcingRateCode = myicing.Rs_code;
    originalIceConcentrationCode = myice1.ci_code;
    originalIceDevelopmentCode = myice1.Si_code;
  }

  @After
  public void restoreObservationState() {
    main.station_ID = originalStationId;
    mydatetime.YY_code = originalYearDayCode;
    mydatetime.GG_code = originalHourCode;
    myposition.Qc_code = originalQcCode;
    myposition.lalala_code = originalLatitudeCode;
    myposition.lolololo_code = originalLongitudeCode;
    mywind.ff_code = originalWindSpeedCode;
    mywind.fff00_code = originalExtendedWindSpeedCode;
    mycm.cm_code = originalMiddleCloudCode;
    mywaves.Dw1_code = originalSwellDirectionCode;
    mywaves.Pw1_code = originalSwellPeriodCode;
    mywaves.Hw1_code = originalSwellHeightCode;
    myicing.Is_code = originalIcingCauseCode;
    myicing.EsEs_code = originalIcingThicknessCode;
    myicing.Rs_code = originalIcingRateCode;
    myice1.ci_code = originalIceConcentrationCode;
    myice1.Si_code = originalIceDevelopmentCode;
  }

  @Test
  public void returnsUndefinedWhenRequiredIdentityDataIsMissing() {
    main.station_ID = "";

    assertEquals(main.UNDEFINED, main.compose_coded_obs(" "));
  }

  @Test
  public void composesAnObservationWhenRequiredIdentityDataIsPresent() {
    main.station_ID = "TEST";
    mydatetime.YY_code = "01";
    mydatetime.GG_code = "12";
    myposition.Qc_code = "1";
    myposition.lalala_code = "234";
    myposition.lolololo_code = "5678";

    String observation = main.compose_coded_obs(" ");

    assertTrue(observation.startsWith("BBXX TEST 0112"));
    assertTrue(observation.endsWith("="));
  }

  @Test
  public void usesFixedWidthMissingValueSentinels() {
    setRequiredObservationData();

    String observation = main.compose_coded_obs(" ");

    assertTrue(observation.contains(" 1//// 2//// 4////"));
    assertTrue(observation.contains(" 222// 0////"));
  }

  @Test
  public void preservesOptionalGroupsAndSpecialFormattingBranches() {
    setRequiredObservationData();
    mywind.ff_code = "12";
    mywind.fff00_code = "345";
    mycm.cm_code = "7b";
    mywaves.Dw1_code = "99";
    myicing.Is_code = "1";
    myicing.EsEs_code = "02";
    myicing.Rs_code = "3";
    myice1.ci_code = "u";
    myice1.Si_code = "1";

    String observation = main.compose_coded_obs(" ");

    assertTrue(observation.contains(" 00345"));
    assertTrue(observation.contains(" 8//7/"));
    assertTrue(observation.contains(" 4////"));
    assertTrue(observation.contains(" 61023"));
    assertTrue(observation.contains(" ICE /1///"));
  }

  private void setRequiredObservationData() {
    main.station_ID = "TEST";
    mydatetime.YY_code = "01";
    mydatetime.GG_code = "12";
    myposition.Qc_code = "1";
    myposition.lalala_code = "234";
    myposition.lolololo_code = "5678";
  }

  @Test
  public void includesRepresentativeWeatherGroups() {
    String originalWindSource = mywind.iw_code;
    String originalWindDirection = mywind.dd_code;
    String originalWindSpeed = mywind.ff_code;
    String originalCloudCover = mycloudcover.N_code;
    String originalTemperatureSign = mytemp.sn_TTT_code;
    String originalTemperature = mytemp.TTT_code;
    try {
      main.station_ID = "TEST";
      mydatetime.YY_code = "01";
      mydatetime.GG_code = "12";
      myposition.Qc_code = "1";
      myposition.lalala_code = "234";
      myposition.lolololo_code = "5678";
      mywind.iw_code = "1";
      mywind.dd_code = "18";
      mywind.ff_code = "05";
      mycloudcover.N_code = "5";
      mytemp.sn_TTT_code = "0";
      mytemp.TTT_code = "123";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.startsWith("BBXX TEST 01121 99234 15678"));
      assertTrue(observation.contains(" 51805 "));
      assertTrue(observation.contains(" 10123 "));
      assertTrue(observation.endsWith("="));
    } finally {
      mywind.iw_code = originalWindSource;
      mywind.dd_code = originalWindDirection;
      mywind.ff_code = originalWindSpeed;
      mycloudcover.N_code = originalCloudCover;
      mytemp.sn_TTT_code = originalTemperatureSign;
      mytemp.TTT_code = originalTemperature;
    }
  }
}
