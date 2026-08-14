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

  @Test
  public void includesAdditionalPassThroughWeatherGroups() {
    String originalPressure = mybarometer.PPPP_code;
    String originalPressureCharacteristic = mybarograph.a_code;
    String originalPressureAmount = mybarograph.ppp_code;
    String originalPresentWeather = mypresentweather.ww_code;
    String originalPastWeatherOne = mypastweather.W1_code;
    String originalPastWeatherTwo = mypastweather.W2_code;
    String originalLowCloud = mycl.cl_code;
    String originalHighCloud = mych.ch_code;
    String originalWavePeriod = mywaves.Pw_code;
    String originalWaveHeight = mywaves.Hw_code;
    String originalSwellDirectionOne = mywaves.Dw1_code;
    String originalSwellPeriodOne = mywaves.Pw1_code;
    String originalSwellHeightOne = mywaves.Hw1_code;
    String originalSwellDirectionTwo = mywaves.Dw2_code;
    String originalSwellPeriodTwo = mywaves.Pw2_code;
    String originalSwellHeightTwo = mywaves.Hw2_code;
    String originalIceCause = myicing.Is_code;
    String originalIceThickness = myicing.EsEs_code;
    String originalIceRate = myicing.Rs_code;
    try {
      main.station_ID = "TEST";
      mydatetime.YY_code = "01";
      mydatetime.GG_code = "12";
      myposition.Qc_code = "1";
      myposition.lalala_code = "234";
      myposition.lolololo_code = "5678";
      mybarometer.PPPP_code = "1234";
      mybarograph.a_code = "1";
      mybarograph.ppp_code = "234";
      mypresentweather.ww_code = "45";
      mypastweather.W1_code = "6";
      mypastweather.W2_code = "7";
      mycl.cl_code = "2";
      mych.ch_code = "3";
      mywaves.Pw_code = "04";
      mywaves.Hw_code = "05";
      mywaves.Dw1_code = "12";
      mywaves.Pw1_code = "03";
      mywaves.Hw1_code = "04";
      mywaves.Dw2_code = "24";
      mywaves.Pw2_code = "05";
      mywaves.Hw2_code = "06";
      myicing.Is_code = "1";
      myicing.EsEs_code = "04";
      myicing.Rs_code = "2";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 41234 "));
      assertTrue(observation.contains(" 51234 "));
      assertTrue(observation.contains(" 74567 "));
      assertTrue(observation.contains(" 20405 "));
      assertTrue(observation.contains(" 31224 "));
      assertTrue(observation.contains(" 40304 "));
      assertTrue(observation.contains(" 50506 "));
      assertTrue(observation.contains(" 61042"));
    } finally {
      mybarometer.PPPP_code = originalPressure;
      mybarograph.a_code = originalPressureCharacteristic;
      mybarograph.ppp_code = originalPressureAmount;
      mypresentweather.ww_code = originalPresentWeather;
      mypastweather.W1_code = originalPastWeatherOne;
      mypastweather.W2_code = originalPastWeatherTwo;
      mycl.cl_code = originalLowCloud;
      mych.ch_code = originalHighCloud;
      mywaves.Pw_code = originalWavePeriod;
      mywaves.Hw_code = originalWaveHeight;
      mywaves.Dw1_code = originalSwellDirectionOne;
      mywaves.Pw1_code = originalSwellPeriodOne;
      mywaves.Hw1_code = originalSwellHeightOne;
      mywaves.Dw2_code = originalSwellDirectionTwo;
      mywaves.Pw2_code = originalSwellPeriodTwo;
      mywaves.Hw2_code = originalSwellHeightTwo;
      myicing.Is_code = originalIceCause;
      myicing.EsEs_code = originalIceThickness;
      myicing.Rs_code = originalIceRate;
    }
  }

  @Test
  public void combinesTemperatureSignAndValueGroups() {
    String originalAirSign = mytemp.sn_TTT_code;
    String originalAirTemperature = mytemp.TTT_code;
    String originalDewSign = mytemp.sn_TdTdTd_code;
    String originalDewPoint = mytemp.TdTdTd_code;
    String originalSeaSign = mytemp.ss_TsTsTs_code;
    String originalSeaTemperature = mytemp.TsTsTs_code;
    String originalWetBulbSign = mytemp.sn_TbTbTb_code;
    String originalWetBulb = mytemp.TbTbTb_code;
    try {
      main.station_ID = "TEST";
      mydatetime.YY_code = "01";
      mydatetime.GG_code = "12";
      myposition.Qc_code = "1";
      myposition.lalala_code = "234";
      myposition.lolololo_code = "5678";
      mytemp.sn_TTT_code = "0";
      mytemp.TTT_code = "123";
      mytemp.sn_TdTdTd_code = "1";
      mytemp.TdTdTd_code = "234";
      mytemp.ss_TsTsTs_code = "0";
      mytemp.TsTsTs_code = "111";
      mytemp.sn_TbTbTb_code = "0";
      mytemp.TbTbTb_code = "222";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 10123 "));
      assertTrue(observation.contains(" 21234 "));
      assertTrue(observation.contains(" 00111 "));
      assertTrue(observation.contains(" 80222"));
    } finally {
      mytemp.sn_TTT_code = originalAirSign;
      mytemp.TTT_code = originalAirTemperature;
      mytemp.sn_TdTdTd_code = originalDewSign;
      mytemp.TdTdTd_code = originalDewPoint;
      mytemp.ss_TsTsTs_code = originalSeaSign;
      mytemp.TsTsTs_code = originalSeaTemperature;
      mytemp.sn_TbTbTb_code = originalWetBulbSign;
      mytemp.TbTbTb_code = originalWetBulb;
    }
  }

  @Test
  public void replacesIncompleteTemperatureGroupsWithUndefinedValues() {
    String originalAirSign = mytemp.sn_TTT_code;
    String originalAirTemperature = mytemp.TTT_code;
    try {
      main.station_ID = "TEST";
      mydatetime.YY_code = "01";
      mydatetime.GG_code = "12";
      myposition.Qc_code = "1";
      myposition.lalala_code = "234";
      myposition.lolololo_code = "5678";
      mytemp.sn_TTT_code = "0";
      mytemp.TTT_code = "";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 1//// "));
    } finally {
      mytemp.sn_TTT_code = originalAirSign;
      mytemp.TTT_code = originalAirTemperature;
    }
  }

  @Test
  public void includesLegacyIceValuesWhenReportable() {
    String originalConcentration = myice1.ci_code;
    String originalDevelopment = myice1.Si_code;
    String originalAmountAndType = myice1.bi_code;
    String originalBearing = myice1.Di_code;
    String originalSituation = myice1.zi_code;
    try {
      main.station_ID = "TEST";
      mydatetime.YY_code = "01";
      mydatetime.GG_code = "12";
      myposition.Qc_code = "1";
      myposition.lalala_code = "234";
      myposition.lolololo_code = "5678";
      myice1.ci_code = "1";
      myice1.Si_code = "2";
      myice1.bi_code = "3";
      myice1.Di_code = "4";
      myice1.zi_code = "5";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" ICE 12345"));
    } finally {
      myice1.ci_code = originalConcentration;
      myice1.Si_code = originalDevelopment;
      myice1.bi_code = originalAmountAndType;
      myice1.Di_code = originalBearing;
      myice1.zi_code = originalSituation;
    }
  }

  @Test
  public void omitsLegacyIceValuesThatAreUnavailable() {
    String originalConcentration = myice1.ci_code;
    String originalDevelopment = myice1.Si_code;
    String originalAmountAndType = myice1.bi_code;
    String originalBearing = myice1.Di_code;
    String originalSituation = myice1.zi_code;
    try {
      main.station_ID = "TEST";
      mydatetime.YY_code = "01";
      mydatetime.GG_code = "12";
      myposition.Qc_code = "1";
      myposition.lalala_code = "234";
      myposition.lolololo_code = "5678";
      myice1.ci_code = "u";
      myice1.Si_code = "u";
      myice1.bi_code = "u";
      myice1.Di_code = "u";
      myice1.zi_code = "u";

      String observation = ObservationComposer.compose(" ");

      assertTrue(!observation.contains(" ICE "));
    } finally {
      myice1.ci_code = originalConcentration;
      myice1.Si_code = originalDevelopment;
      myice1.bi_code = originalAmountAndType;
      myice1.Di_code = originalBearing;
      myice1.zi_code = originalSituation;
    }
  }

  @Test
  public void extractsTheFirstCharacterOfMiddleCloudCode() {
    String originalMiddleCloud = mycm.cm_code;
    try {
      main.station_ID = "TEST";
      mydatetime.YY_code = "01";
      mydatetime.GG_code = "12";
      myposition.Qc_code = "1";
      myposition.lalala_code = "234";
      myposition.lolololo_code = "5678";
      mycm.cm_code = "7a";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 8//7/ "));
    } finally {
      mycm.cm_code = originalMiddleCloud;
    }
  }

  @Test
  public void usesPlaceholderForMissingMiddleCloudCode() {
    String originalMiddleCloud = mycm.cm_code;
    try {
      main.station_ID = "TEST";
      mydatetime.YY_code = "01";
      mydatetime.GG_code = "12";
      myposition.Qc_code = "1";
      myposition.lalala_code = "234";
      myposition.lolololo_code = "5678";
      mycm.cm_code = "";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 8//// "));
    } finally {
      mycm.cm_code = originalMiddleCloud;
    }
  }
}
