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
  private String originalLowCloudCode;
  private String originalHighCloudCode;
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
    originalYearDayCode = DateTimeModel.YY_code;
    originalHourCode = DateTimeModel.GG_code;
    originalQcCode = PositionModel.Qc_code;
    originalLatitudeCode = PositionModel.lalala_code;
    originalLongitudeCode = PositionModel.lolololo_code;
    originalWindSpeedCode = WindModel.ff_code;
    originalExtendedWindSpeedCode = WindModel.fff00_code;
    originalMiddleCloudCode = MiddleCloudState.cm_code;
    originalLowCloudCode = LowCloudState.cl_code;
    originalHighCloudCode = HighCloudState.ch_code;
    originalSwellDirectionCode = WavesModel.Dw1_code;
    originalSwellPeriodCode = WavesModel.Pw1_code;
    originalSwellHeightCode = WavesModel.Hw1_code;
    originalIcingCauseCode = IcingState.Is_code;
    originalIcingThicknessCode = IcingState.EsEs_code;
    originalIcingRateCode = IcingState.Rs_code;
    originalIceConcentrationCode = IceState.ci_code;
    originalIceDevelopmentCode = IceState.Si_code;
  }

  @After
  public void restoreObservationState() {
    main.station_ID = originalStationId;
    DateTimeModel.YY_code = originalYearDayCode;
    DateTimeModel.GG_code = originalHourCode;
    PositionModel.Qc_code = originalQcCode;
    PositionModel.lalala_code = originalLatitudeCode;
    PositionModel.lolololo_code = originalLongitudeCode;
    WindModel.ff_code = originalWindSpeedCode;
    WindModel.fff00_code = originalExtendedWindSpeedCode;
    MiddleCloudState.cm_code = originalMiddleCloudCode;
    LowCloudState.cl_code = originalLowCloudCode;
    HighCloudState.ch_code = originalHighCloudCode;
    WavesModel.Dw1_code = originalSwellDirectionCode;
    WavesModel.Pw1_code = originalSwellPeriodCode;
    WavesModel.Hw1_code = originalSwellHeightCode;
    IcingState.Is_code = originalIcingCauseCode;
    IcingState.EsEs_code = originalIcingThicknessCode;
    IcingState.Rs_code = originalIcingRateCode;
    IceState.ci_code = originalIceConcentrationCode;
    IceState.Si_code = originalIceDevelopmentCode;
  }

  @Test
  public void returnsUndefinedWhenRequiredIdentityDataIsMissing() {
    main.station_ID = "";

    assertEquals(main.UNDEFINED, main.compose_coded_obs(" "));
  }

  @Test
  public void composesAnObservationWhenRequiredIdentityDataIsPresent() {
    main.station_ID = "TEST";
    DateTimeModel.YY_code = "01";
    DateTimeModel.GG_code = "12";
    PositionModel.Qc_code = "1";
    PositionModel.lalala_code = "234";
    PositionModel.lolololo_code = "5678";

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
    WindModel.ff_code = "12";
    WindModel.fff00_code = "345";
    MiddleCloudState.cm_code = "7b";
    LowCloudState.cl_code = "";
    HighCloudState.ch_code = "";
    WavesModel.Dw1_code = "99";
    IcingState.Is_code = "1";
    IcingState.EsEs_code = "02";
    IcingState.Rs_code = "3";
    IceState.ci_code = "u";
    IceState.Si_code = "1";

    String observation = main.compose_coded_obs(" ");

    assertTrue(observation.contains(" 00345"));
    assertTrue(observation.contains(" 8//7/"));
    assertTrue(observation.contains(" 4////"));
    assertTrue(observation.contains(" 61023"));
    assertTrue(observation.contains(" ICE /1///"));
  }

  private void setRequiredObservationData() {
    main.station_ID = "TEST";
    DateTimeModel.YY_code = "01";
    DateTimeModel.GG_code = "12";
    PositionModel.Qc_code = "1";
    PositionModel.lalala_code = "234";
    PositionModel.lolololo_code = "5678";
  }

  @Test
  public void includesRepresentativeWeatherGroups() {
    String originalWindSource = WindModel.iw_code;
    String originalWindDirection = WindModel.dd_code;
    String originalWindSpeed = WindModel.ff_code;
    String originalCloudCover = CloudCoverState.N_code;
    String originalTemperatureSign = TemperatureModel.sn_TTT_code;
    String originalTemperature = TemperatureModel.TTT_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      WindModel.iw_code = "1";
      WindModel.dd_code = "18";
      WindModel.ff_code = "05";
      CloudCoverState.N_code = "5";
      TemperatureModel.sn_TTT_code = "0";
      TemperatureModel.TTT_code = "123";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.startsWith("BBXX TEST 01121 99234 15678"));
      assertTrue(observation.contains(" 51805 "));
      assertTrue(observation.contains(" 10123 "));
      assertTrue(observation.endsWith("="));
    } finally {
      WindModel.iw_code = originalWindSource;
      WindModel.dd_code = originalWindDirection;
      WindModel.ff_code = originalWindSpeed;
      CloudCoverState.N_code = originalCloudCover;
      TemperatureModel.sn_TTT_code = originalTemperatureSign;
      TemperatureModel.TTT_code = originalTemperature;
    }
  }

  @Test
  public void includesAdditionalPassThroughWeatherGroups() {
    String originalPressure = BarometerModel.PPPP_code;
    String originalPressureCharacteristic = BarographModel.a_code;
    String originalPressureAmount = BarographModel.ppp_code;
    String originalPresentWeather = PresentWeatherState.ww_code;
    String originalPastWeatherOne = PastWeatherState.W1_code;
    String originalPastWeatherTwo = PastWeatherState.W2_code;
    String originalLowCloud = LowCloudState.cl_code;
    String originalHighCloud = HighCloudState.ch_code;
    String originalWavePeriod = WavesModel.Pw_code;
    String originalWaveHeight = WavesModel.Hw_code;
    String originalSwellDirectionOne = WavesModel.Dw1_code;
    String originalSwellPeriodOne = WavesModel.Pw1_code;
    String originalSwellHeightOne = WavesModel.Hw1_code;
    String originalSwellDirectionTwo = WavesModel.Dw2_code;
    String originalSwellPeriodTwo = WavesModel.Pw2_code;
    String originalSwellHeightTwo = WavesModel.Hw2_code;
    String originalIceCause = IcingState.Is_code;
    String originalIceThickness = IcingState.EsEs_code;
    String originalIceRate = IcingState.Rs_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      BarometerModel.PPPP_code = "1234";
      BarographModel.a_code = "1";
      BarographModel.ppp_code = "234";
      PresentWeatherState.ww_code = "45";
      PastWeatherState.W1_code = "6";
      PastWeatherState.W2_code = "7";
      LowCloudState.cl_code = "2";
      HighCloudState.ch_code = "3";
      WavesModel.Pw_code = "04";
      WavesModel.Hw_code = "05";
      WavesModel.Dw1_code = "12";
      WavesModel.Pw1_code = "03";
      WavesModel.Hw1_code = "04";
      WavesModel.Dw2_code = "24";
      WavesModel.Pw2_code = "05";
      WavesModel.Hw2_code = "06";
      IcingState.Is_code = "1";
      IcingState.EsEs_code = "04";
      IcingState.Rs_code = "2";

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
      BarometerModel.PPPP_code = originalPressure;
      BarographModel.a_code = originalPressureCharacteristic;
      BarographModel.ppp_code = originalPressureAmount;
      PresentWeatherState.ww_code = originalPresentWeather;
      PastWeatherState.W1_code = originalPastWeatherOne;
      PastWeatherState.W2_code = originalPastWeatherTwo;
      LowCloudState.cl_code = originalLowCloud;
      HighCloudState.ch_code = originalHighCloud;
      WavesModel.Pw_code = originalWavePeriod;
      WavesModel.Hw_code = originalWaveHeight;
      WavesModel.Dw1_code = originalSwellDirectionOne;
      WavesModel.Pw1_code = originalSwellPeriodOne;
      WavesModel.Hw1_code = originalSwellHeightOne;
      WavesModel.Dw2_code = originalSwellDirectionTwo;
      WavesModel.Pw2_code = originalSwellPeriodTwo;
      WavesModel.Hw2_code = originalSwellHeightTwo;
      IcingState.Is_code = originalIceCause;
      IcingState.EsEs_code = originalIceThickness;
      IcingState.Rs_code = originalIceRate;
    }
  }

  @Test
  public void combinesTemperatureSignAndValueGroups() {
    String originalAirSign = TemperatureModel.sn_TTT_code;
    String originalAirTemperature = TemperatureModel.TTT_code;
    String originalDewSign = TemperatureModel.sn_TdTdTd_code;
    String originalDewPoint = TemperatureModel.TdTdTd_code;
    String originalSeaSign = TemperatureModel.ss_TsTsTs_code;
    String originalSeaTemperature = TemperatureModel.TsTsTs_code;
    String originalWetBulbSign = TemperatureModel.sn_TbTbTb_code;
    String originalWetBulb = TemperatureModel.TbTbTb_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      TemperatureModel.sn_TTT_code = "0";
      TemperatureModel.TTT_code = "123";
      TemperatureModel.sn_TdTdTd_code = "1";
      TemperatureModel.TdTdTd_code = "234";
      TemperatureModel.ss_TsTsTs_code = "0";
      TemperatureModel.TsTsTs_code = "111";
      TemperatureModel.sn_TbTbTb_code = "0";
      TemperatureModel.TbTbTb_code = "222";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 10123 "));
      assertTrue(observation.contains(" 21234 "));
      assertTrue(observation.contains(" 00111 "));
      assertTrue(observation.contains(" 80222"));
    } finally {
      TemperatureModel.sn_TTT_code = originalAirSign;
      TemperatureModel.TTT_code = originalAirTemperature;
      TemperatureModel.sn_TdTdTd_code = originalDewSign;
      TemperatureModel.TdTdTd_code = originalDewPoint;
      TemperatureModel.ss_TsTsTs_code = originalSeaSign;
      TemperatureModel.TsTsTs_code = originalSeaTemperature;
      TemperatureModel.sn_TbTbTb_code = originalWetBulbSign;
      TemperatureModel.TbTbTb_code = originalWetBulb;
    }
  }

  @Test
  public void replacesIncompleteTemperatureGroupsWithUndefinedValues() {
    String originalAirSign = TemperatureModel.sn_TTT_code;
    String originalAirTemperature = TemperatureModel.TTT_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      TemperatureModel.sn_TTT_code = "0";
      TemperatureModel.TTT_code = "";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 1//// "));
    } finally {
      TemperatureModel.sn_TTT_code = originalAirSign;
      TemperatureModel.TTT_code = originalAirTemperature;
    }
  }

  @Test
  public void includesLegacyIceValuesWhenReportable() {
    String originalConcentration = IceState.ci_code;
    String originalDevelopment = IceState.Si_code;
    String originalAmountAndType = IceState.bi_code;
    String originalBearing = IceState.Di_code;
    String originalSituation = IceState.zi_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      IceState.ci_code = "1";
      IceState.Si_code = "2";
      IceState.bi_code = "3";
      IceState.Di_code = "4";
      IceState.zi_code = "5";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" ICE 12345"));
    } finally {
      IceState.ci_code = originalConcentration;
      IceState.Si_code = originalDevelopment;
      IceState.bi_code = originalAmountAndType;
      IceState.Di_code = originalBearing;
      IceState.zi_code = originalSituation;
    }
  }

  @Test
  public void omitsLegacyIceValuesThatAreUnavailable() {
    String originalConcentration = IceState.ci_code;
    String originalDevelopment = IceState.Si_code;
    String originalAmountAndType = IceState.bi_code;
    String originalBearing = IceState.Di_code;
    String originalSituation = IceState.zi_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      IceState.ci_code = "u";
      IceState.Si_code = "u";
      IceState.bi_code = "u";
      IceState.Di_code = "u";
      IceState.zi_code = "u";

      String observation = ObservationComposer.compose(" ");

      assertTrue(!observation.contains(" ICE "));
    } finally {
      IceState.ci_code = originalConcentration;
      IceState.Si_code = originalDevelopment;
      IceState.bi_code = originalAmountAndType;
      IceState.Di_code = originalBearing;
      IceState.zi_code = originalSituation;
    }
  }

  @Test
  public void extractsTheFirstCharacterOfMiddleCloudCode() {
    String originalMiddleCloud = MiddleCloudState.cm_code;
    String originalCloudAmount = CloudCoverState.Nh_code;
    String originalLowCloud = LowCloudState.cl_code;
    String originalHighCloud = HighCloudState.ch_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      MiddleCloudState.cm_code = "7a";
      CloudCoverState.Nh_code = "";
      LowCloudState.cl_code = "";
      HighCloudState.ch_code = "";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 8//7/ "));
    } finally {
      MiddleCloudState.cm_code = originalMiddleCloud;
      CloudCoverState.Nh_code = originalCloudAmount;
      LowCloudState.cl_code = originalLowCloud;
      HighCloudState.ch_code = originalHighCloud;
    }
  }

  @Test
  public void usesPlaceholderForMissingMiddleCloudCode() {
    String originalMiddleCloud = MiddleCloudState.cm_code;
    String originalCloudAmount = CloudCoverState.Nh_code;
    String originalLowCloud = LowCloudState.cl_code;
    String originalHighCloud = HighCloudState.ch_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      MiddleCloudState.cm_code = "";
      CloudCoverState.Nh_code = "";
      LowCloudState.cl_code = "";
      HighCloudState.ch_code = "";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 8//// "));
    } finally {
      MiddleCloudState.cm_code = originalMiddleCloud;
      CloudCoverState.Nh_code = originalCloudAmount;
      LowCloudState.cl_code = originalLowCloud;
      HighCloudState.ch_code = originalHighCloud;
    }
  }

  @Test
  public void composesWindSpeedWithOptionalHighSpeedExtension() {
    String originalCloudCover = CloudCoverState.N_code;
    String originalWindDirection = WindModel.dd_code;
    String originalWindSpeed = WindModel.ff_code;
    String originalHighWindSpeed = WindModel.fff00_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      CloudCoverState.N_code = "5";
      WindModel.dd_code = "18";
      WindModel.ff_code = "05";
      WindModel.fff00_code = "12";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 51805 0012 "));
    } finally {
      CloudCoverState.N_code = originalCloudCover;
      WindModel.dd_code = originalWindDirection;
      WindModel.ff_code = originalWindSpeed;
      WindModel.fff00_code = originalHighWindSpeed;
    }
  }

  @Test
  public void usesWindSpeedPlaceholderWhenMissing() {
    String originalCloudCover = CloudCoverState.N_code;
    String originalWindDirection = WindModel.dd_code;
    String originalWindSpeed = WindModel.ff_code;
    String originalHighWindSpeed = WindModel.fff00_code;
    try {
      main.station_ID = "TEST";
      DateTimeModel.YY_code = "01";
      DateTimeModel.GG_code = "12";
      PositionModel.Qc_code = "1";
      PositionModel.lalala_code = "234";
      PositionModel.lolololo_code = "5678";
      CloudCoverState.N_code = "5";
      WindModel.dd_code = "18";
      WindModel.ff_code = "";
      WindModel.fff00_code = "12";

      String observation = ObservationComposer.compose(" ");

      assertTrue(observation.contains(" 518// "));
    } finally {
      CloudCoverState.N_code = originalCloudCover;
      WindModel.dd_code = originalWindDirection;
      WindModel.ff_code = originalWindSpeed;
      WindModel.fff00_code = originalHighWindSpeed;
    }
  }
}
