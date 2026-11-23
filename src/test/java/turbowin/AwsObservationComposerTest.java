package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AwsObservationComposerTest {

  private String originalDiffSllWl;
  private String originalWindUnits;
  private boolean originalEucawsObsId;
  private boolean originalAirTempFromAwsPresent;
  private boolean originalRhFromAwsPresent;
  private boolean originalSstFromAwsPresent;
  private boolean originalTrueWindSpeedFromAwsPresent;
  private boolean originalTrueWindDirFromAwsPresent;
  private int originalIntTrueWindDir;
  private int originalIntTrueWindSpeed;
  private String originalAirTemp;
  private double originalDoubleRv;
  private String originalSeaWaterTemp;
  private String originalVvCode;
  private String originalWwCode;
  private String originalW1Code;
  private String originalW2Code;
  private String originalNCode;
  private String originalNhCode;
  private String originalHCode;
  private String originalClCode;
  private String originalCmCode;
  private String originalChCode;
  private String originalPwCode;
  private String originalHwCode;
  private String originalDw1Code;
  private String originalPw1Code;
  private String originalHw1Code;
  private String originalDw2Code;
  private String originalPw2Code;
  private String originalHw2Code;
  private String originalEsEsCode;
  private String originalRsCode;
  private String originalIsCode;
  private String originalCiCode;
  private String originalBiCode;
  private String originalZiCode;
  private String originalSiCode;
  private String originalDiCode;
  private String originalSelectedObserver;

  @Before
  public void resetAwsInputs() {
    originalDiffSllWl = main.diff_sll_wl;
    originalWindUnits = main.wind_units;
    originalEucawsObsId = main.eucaws_obs_id;
    originalAirTempFromAwsPresent = main.air_temp_from_AWS_present;
    originalRhFromAwsPresent = main.rh_from_AWS_present;
    originalSstFromAwsPresent = main.SST_from_AWS_present;
    originalTrueWindSpeedFromAwsPresent = main.true_wind_speed_from_AWS_present;
    originalTrueWindDirFromAwsPresent = main.true_wind_dir_from_AWS_present;
    originalIntTrueWindDir = WindModel.int_true_wind_dir;
    originalIntTrueWindSpeed = WindModel.int_true_wind_speed;
    originalAirTemp = TemperatureModel.air_temp;
    originalDoubleRv = TemperatureModel.double_rv;
    originalSeaWaterTemp = TemperatureModel.sea_water_temp;
    originalVvCode = VisibilityState.VV_code;
    originalWwCode = PresentWeatherState.ww_code;
    originalW1Code = PastWeatherState.W1_code;
    originalW2Code = PastWeatherState.W2_code;
    originalNCode = CloudCoverState.N_code;
    originalNhCode = CloudCoverState.Nh_code;
    originalHCode = CloudCoverState.h_code;
    originalClCode = LowCloudState.cl_code;
    originalCmCode = MiddleCloudState.cm_code;
    originalChCode = HighCloudState.ch_code;
    originalPwCode = WavesModel.Pw_code;
    originalHwCode = WavesModel.Hw_code;
    originalDw1Code = WavesModel.Dw1_code;
    originalPw1Code = WavesModel.Pw1_code;
    originalHw1Code = WavesModel.Hw1_code;
    originalDw2Code = WavesModel.Dw2_code;
    originalPw2Code = WavesModel.Pw2_code;
    originalHw2Code = WavesModel.Hw2_code;
    originalEsEsCode = IcingState.EsEs_code;
    originalRsCode = IcingState.Rs_code;
    originalIsCode = IcingState.Is_code;
    originalCiCode = IceState.ci_code;
    originalBiCode = IceState.bi_code;
    originalZiCode = IceState.zi_code;
    originalSiCode = IceState.Si_code;
    originalDiCode = IceState.Di_code;
    originalSelectedObserver = ObserverModel.selected_observer;

    main.diff_sll_wl = "";
    main.wind_units = "knots";
    main.eucaws_obs_id = false;
    main.air_temp_from_AWS_present = false;
    main.rh_from_AWS_present = false;
    main.SST_from_AWS_present = false;
    main.true_wind_speed_from_AWS_present = false;
    main.true_wind_dir_from_AWS_present = false;

    WindModel.int_true_wind_dir = main.INVALID;
    WindModel.int_true_wind_speed = main.INVALID;
    TemperatureModel.air_temp = "";
    TemperatureModel.double_rv = main.INVALID;
    TemperatureModel.sea_water_temp = "";
    VisibilityState.VV_code = "//";
    PresentWeatherState.ww_code = "//";
    PastWeatherState.W1_code = "/";
    PastWeatherState.W2_code = "/";
    CloudCoverState.N_code = "/";
    CloudCoverState.Nh_code = "/";
    CloudCoverState.h_code = "/";
    LowCloudState.cl_code = "/";
    MiddleCloudState.cm_code = "/";
    HighCloudState.ch_code = "/";
    WavesModel.Pw_code = "//";
    WavesModel.Hw_code = "//";
    WavesModel.Dw1_code = "//";
    WavesModel.Pw1_code = "//";
    WavesModel.Hw1_code = "//";
    WavesModel.Dw2_code = "//";
    WavesModel.Pw2_code = "//";
    WavesModel.Hw2_code = "//";
    IcingState.EsEs_code = "//";
    IcingState.Rs_code = "/";
    IcingState.Is_code = "/";
    IceState.ci_code = "/";
    IceState.bi_code = "/";
    IceState.zi_code = "/";
    IceState.Si_code = "/";
    IceState.Di_code = "/";
    ObserverModel.selected_observer = "";
  }

  @After
  public void restoreAwsInputs() {
    main.diff_sll_wl = originalDiffSllWl;
    main.wind_units = originalWindUnits;
    main.eucaws_obs_id = originalEucawsObsId;
    main.air_temp_from_AWS_present = originalAirTempFromAwsPresent;
    main.rh_from_AWS_present = originalRhFromAwsPresent;
    main.SST_from_AWS_present = originalSstFromAwsPresent;
    main.true_wind_speed_from_AWS_present = originalTrueWindSpeedFromAwsPresent;
    main.true_wind_dir_from_AWS_present = originalTrueWindDirFromAwsPresent;
    WindModel.int_true_wind_dir = originalIntTrueWindDir;
    WindModel.int_true_wind_speed = originalIntTrueWindSpeed;
    TemperatureModel.air_temp = originalAirTemp;
    TemperatureModel.double_rv = originalDoubleRv;
    TemperatureModel.sea_water_temp = originalSeaWaterTemp;
    VisibilityState.VV_code = originalVvCode;
    PresentWeatherState.ww_code = originalWwCode;
    PastWeatherState.W1_code = originalW1Code;
    PastWeatherState.W2_code = originalW2Code;
    CloudCoverState.N_code = originalNCode;
    CloudCoverState.Nh_code = originalNhCode;
    CloudCoverState.h_code = originalHCode;
    LowCloudState.cl_code = originalClCode;
    MiddleCloudState.cm_code = originalCmCode;
    HighCloudState.ch_code = originalChCode;
    WavesModel.Pw_code = originalPwCode;
    WavesModel.Hw_code = originalHwCode;
    WavesModel.Dw1_code = originalDw1Code;
    WavesModel.Pw1_code = originalPw1Code;
    WavesModel.Hw1_code = originalHw1Code;
    WavesModel.Dw2_code = originalDw2Code;
    WavesModel.Pw2_code = originalPw2Code;
    WavesModel.Hw2_code = originalHw2Code;
    IcingState.EsEs_code = originalEsEsCode;
    IcingState.Rs_code = originalRsCode;
    IcingState.Is_code = originalIsCode;
    IceState.ci_code = originalCiCode;
    IceState.bi_code = originalBiCode;
    IceState.zi_code = originalZiCode;
    IceState.Si_code = originalSiCode;
    IceState.Di_code = originalDiCode;
    ObserverModel.selected_observer = originalSelectedObserver;
  }

  @Test
  public void compilesTheAwsMessageWithItsProtocolIdentifier() {
    String observation = AwsObservationComposer.compile();

    assertTrue(observation.startsWith("$PTBWP,"));
  }

  @Test
  public void convertsAndRoundsNumericInputs() {
    WindModel.int_true_wind_speed = 10;
    TemperatureModel.air_temp = "2.129";
    TemperatureModel.double_rv = 0.567;
    TemperatureModel.sea_water_temp = "2.129";

    String[] fields = AwsObservationComposer.compile().split(",", -1);

    assertEquals("5.1", fields[3]);
    assertEquals("2.1", fields[4]);
    assertEquals("57", fields[5]);
    assertEquals("2.1", fields[6]);
  }

  @Test
  public void emitsEmptyFieldsForMissingValues() {
    String[] fields = AwsObservationComposer.compile().split(",", -1);

    assertEquals("", fields[2]);
    assertEquals("", fields[3]);
    assertEquals("", fields[4]);
    assertEquals("", fields[5]);
    assertEquals("", fields[6]);
    assertEquals("", fields[7]);
  }

  @Test
  public void preservesBufrMappingsAndOptionalObserverId() {
    MiddleCloudState.cm_code = "7a";
    HighCloudState.ch_code = "3";
    IcingState.Is_code = "1";
    IceState.ci_code = "u";
    IceState.Di_code = "2";
    main.eucaws_obs_id = true;
    ObserverModel.selected_observer = "Janssen;K;AB1;-;";

    String[] fields = AwsObservationComposer.compile().split(",", -1);

    assertEquals("27", fields[14]);
    assertEquals("13", fields[15]);
    assertEquals("8", fields[27]);
    assertEquals("14", fields[28]);
    assertEquals("90", fields[32]);
    assertEquals("AB1", fields[33]);
  }

  @Test
  public void preservesTheCompleteAwsProtocolFieldLayout() {
    String observation = AwsObservationComposer.compile();
    String[] fields = observation.split(",", -1);

    assertEquals("$PTBWP", fields[0]);
    assertEquals(33, fields.length);
  }

  @Test
  public void convertsManualWindValuesIntoAwsFields() {
    String originalDiffSllWl = main.diff_sll_wl;
    String originalWindUnits = main.wind_units;
    int originalWindDirection = WindModel.int_true_wind_dir;
    int originalWindSpeed = WindModel.int_true_wind_speed;
    boolean originalDirectionFromAws = main.true_wind_dir_from_AWS_present;
    boolean originalSpeedFromAws = main.true_wind_speed_from_AWS_present;
    try {
      main.diff_sll_wl = "5";
      main.wind_units = main.M_S;
      WindModel.int_true_wind_dir = 180;
      WindModel.int_true_wind_speed = 10;
      main.true_wind_dir_from_AWS_present = false;
      main.true_wind_speed_from_AWS_present = false;

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("5", fields[1]);
      assertEquals("180", fields[2]);
      assertEquals("10.0", fields[3]);
    } finally {
      main.diff_sll_wl = originalDiffSllWl;
      main.wind_units = originalWindUnits;
      WindModel.int_true_wind_dir = originalWindDirection;
      WindModel.int_true_wind_speed = originalWindSpeed;
      main.true_wind_dir_from_AWS_present = originalDirectionFromAws;
      main.true_wind_speed_from_AWS_present = originalSpeedFromAws;
    }
  }

  @Test
  public void convertsWindWavePeriodsAndHeights() {
    String originalPw = WavesModel.Pw_code;
    String originalHw = WavesModel.Hw_code;
    String originalDw1 = WavesModel.Dw1_code;
    String originalPw1 = WavesModel.Pw1_code;
    String originalHw1 = WavesModel.Hw1_code;
    String originalDw2 = WavesModel.Dw2_code;
    String originalPw2 = WavesModel.Pw2_code;
    String originalHw2 = WavesModel.Hw2_code;
    try {
      WavesModel.Pw_code = "03";
      WavesModel.Hw_code = "03";
      WavesModel.Dw1_code = "12";
      WavesModel.Pw1_code = "04";
      WavesModel.Hw1_code = "05";
      WavesModel.Dw2_code = "24";
      WavesModel.Pw2_code = "06";
      WavesModel.Hw2_code = "07";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("3", fields[17]);
      assertEquals("1.5", fields[18]);
      assertEquals("120", fields[19]);
      assertEquals("4", fields[20]);
      assertEquals("2.5", fields[21]);
      assertEquals("240", fields[22]);
      assertEquals("6", fields[23]);
      assertEquals("3.5", fields[24]);
    } finally {
      WavesModel.Pw_code = originalPw;
      WavesModel.Hw_code = originalHw;
      WavesModel.Dw1_code = originalDw1;
      WavesModel.Pw1_code = originalPw1;
      WavesModel.Hw1_code = originalHw1;
      WavesModel.Dw2_code = originalDw2;
      WavesModel.Pw2_code = originalPw2;
      WavesModel.Hw2_code = originalHw2;
    }
  }

  @Test
  public void omitsUnavailableWindWaveValues() {
    String originalPw = WavesModel.Pw_code;
    String originalHw = WavesModel.Hw_code;
    String originalDw1 = WavesModel.Dw1_code;
    String originalPw1 = WavesModel.Pw1_code;
    String originalHw1 = WavesModel.Hw1_code;
    String originalDw2 = WavesModel.Dw2_code;
    String originalPw2 = WavesModel.Pw2_code;
    String originalHw2 = WavesModel.Hw2_code;
    try {
      WavesModel.Pw_code = "99";
      WavesModel.Hw_code = "99";
      WavesModel.Dw1_code = "99";
      WavesModel.Pw1_code = "99";
      WavesModel.Hw1_code = "99";
      WavesModel.Dw2_code = "99";
      WavesModel.Pw2_code = "99";
      WavesModel.Hw2_code = "99";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("", fields[17]);
      assertEquals("", fields[18]);
      assertEquals("", fields[19]);
      assertEquals("", fields[20]);
      assertEquals("", fields[21]);
      assertEquals("", fields[22]);
      assertEquals("", fields[23]);
      assertEquals("", fields[24]);
    } finally {
      WavesModel.Pw_code = originalPw;
      WavesModel.Hw_code = originalHw;
      WavesModel.Dw1_code = originalDw1;
      WavesModel.Pw1_code = originalPw1;
      WavesModel.Hw1_code = originalHw1;
      WavesModel.Dw2_code = originalDw2;
      WavesModel.Pw2_code = originalPw2;
      WavesModel.Hw2_code = originalHw2;
    }
  }

  @Test
  public void convertsCloudTypesToAwsCodes() {
    String originalLowCloud = LowCloudState.cl_code;
    String originalMiddleCloud = MiddleCloudState.cm_code;
    String originalHighCloud = HighCloudState.ch_code;
    try {
      LowCloudState.cl_code = "1";
      MiddleCloudState.cm_code = "7a";
      HighCloudState.ch_code = "3";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("31", fields[13]);
      assertEquals("27", fields[14]);
      assertEquals("13", fields[15]);
    } finally {
      LowCloudState.cl_code = originalLowCloud;
      MiddleCloudState.cm_code = originalMiddleCloud;
      HighCloudState.ch_code = originalHighCloud;
    }
  }

  @Test
  public void omitsUnavailableAndMalformedCloudTypes() {
    String originalLowCloud = LowCloudState.cl_code;
    String originalMiddleCloud = MiddleCloudState.cm_code;
    String originalHighCloud = HighCloudState.ch_code;
    try {
      LowCloudState.cl_code = "/";
      MiddleCloudState.cm_code = "x";
      HighCloudState.ch_code = "";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("", fields[13]);
      assertEquals("", fields[14]);
      assertEquals("", fields[15]);
    } finally {
      LowCloudState.cl_code = originalLowCloud;
      MiddleCloudState.cm_code = originalMiddleCloud;
      HighCloudState.ch_code = originalHighCloud;
    }
  }

  @Test
  public void convertsIceCodesToAwsValues() {
    String originalIceThickness = IcingState.EsEs_code;
    String originalIceRate = IcingState.Rs_code;
    String originalIceCause = IcingState.Is_code;
    String originalConcentration = IceState.ci_code;
    String originalAmountAndType = IceState.bi_code;
    String originalSituation = IceState.zi_code;
    String originalDevelopment = IceState.Si_code;
    String originalBearing = IceState.Di_code;
    try {
      IcingState.EsEs_code = "04";
      IcingState.Rs_code = "2";
      IcingState.Is_code = "3";
      IceState.ci_code = "u";
      IceState.bi_code = "u";
      IceState.zi_code = "u";
      IceState.Si_code = "u";
      IceState.Di_code = "8";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("0.04", fields[25]);
      assertEquals("2", fields[26]);
      assertEquals("12", fields[27]);
      assertEquals("14", fields[28]);
      assertEquals("14", fields[29]);
      assertEquals("30", fields[30]);
      assertEquals("30", fields[31]);
      assertEquals("360", fields[32]);
    } finally {
      IcingState.EsEs_code = originalIceThickness;
      IcingState.Rs_code = originalIceRate;
      IcingState.Is_code = originalIceCause;
      IceState.ci_code = originalConcentration;
      IceState.bi_code = originalAmountAndType;
      IceState.zi_code = originalSituation;
      IceState.Si_code = originalDevelopment;
      IceState.Di_code = originalBearing;
    }
  }

  @Test
  public void convertsEverySupportedIceCauseCode() {
    String originalIceCause = IcingState.Is_code;
    String[] sourceCodes = {"1", "2", "3", "4", "5", "6", "14"};
    String[] expectedCodes = {"8", "4", "12", "2", "10", "6", "14"};
    try {
      for (int i = 0; i < sourceCodes.length; i++) {
        IcingState.Is_code = sourceCodes[i];
        String[] fields = AwsObservationComposer.compile().split(",", -1);
        assertEquals(expectedCodes[i], fields[27]);
      }
    } finally {
      IcingState.Is_code = originalIceCause;
    }
  }

  @Test
  public void convertsEverySupportedIceBearingCode() {
    String originalBearing = IceState.Di_code;
    String[] sourceCodes = {"1", "2", "3", "4", "5", "6", "7", "8"};
    String[] expectedCodes = {"45", "90", "135", "180", "225", "270", "315", "360"};
    try {
      for (int i = 0; i < sourceCodes.length; i++) {
        IceState.Di_code = sourceCodes[i];
        String[] fields = AwsObservationComposer.compile().split(",", -1);
        assertEquals(expectedCodes[i], fields[32]);
      }
    } finally {
      IceState.Di_code = originalBearing;
    }
  }

  @Test
  public void omitsUnavailableIceCodes() {
    String originalIceThickness = IcingState.EsEs_code;
    String originalIceRate = IcingState.Rs_code;
    String originalIceCause = IcingState.Is_code;
    String originalConcentration = IceState.ci_code;
    String originalAmountAndType = IceState.bi_code;
    String originalSituation = IceState.zi_code;
    String originalDevelopment = IceState.Si_code;
    String originalBearing = IceState.Di_code;
    try {
      IcingState.EsEs_code = "//";
      IcingState.Rs_code = "/";
      IcingState.Is_code = "9";
      IceState.ci_code = "/";
      IceState.bi_code = "/";
      IceState.zi_code = "/";
      IceState.Si_code = "/";
      IceState.Di_code = "0";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("", fields[25]);
      assertEquals("", fields[26]);
      assertEquals("", fields[27]);
      assertEquals("", fields[28]);
      assertEquals("", fields[29]);
      assertEquals("", fields[30]);
      assertEquals("", fields[31]);
      assertEquals("", fields[32]);
    } finally {
      IcingState.EsEs_code = originalIceThickness;
      IcingState.Rs_code = originalIceRate;
      IcingState.Is_code = originalIceCause;
      IceState.ci_code = originalConcentration;
      IceState.bi_code = originalAmountAndType;
      IceState.zi_code = originalSituation;
      IceState.Si_code = originalDevelopment;
      IceState.Di_code = originalBearing;
    }
  }

  @Test
  public void preservesDirectAwsObservationCodes() {
    String originalVisibility = VisibilityState.VV_code;
    String originalPresentWeather = PresentWeatherState.ww_code;
    String originalPastWeatherOne = PastWeatherState.W1_code;
    String originalPastWeatherTwo = PastWeatherState.W2_code;
    String originalCloudCover = CloudCoverState.N_code;
    String originalCloudAmount = CloudCoverState.Nh_code;
    String originalCloudHeight = CloudCoverState.h_code;
    try {
      VisibilityState.VV_code = "10";
      PresentWeatherState.ww_code = "45";
      PastWeatherState.W1_code = "2";
      PastWeatherState.W2_code = "3";
      CloudCoverState.N_code = "7";
      CloudCoverState.Nh_code = "5";
      CloudCoverState.h_code = "4";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("10", fields[7]);
      assertEquals("45", fields[8]);
      assertEquals("2", fields[9]);
      assertEquals("3", fields[10]);
      assertEquals("7", fields[11]);
      assertEquals("5", fields[12]);
      assertEquals("4", fields[16]);
    } finally {
      VisibilityState.VV_code = originalVisibility;
      PresentWeatherState.ww_code = originalPresentWeather;
      PastWeatherState.W1_code = originalPastWeatherOne;
      PastWeatherState.W2_code = originalPastWeatherTwo;
      CloudCoverState.N_code = originalCloudCover;
      CloudCoverState.Nh_code = originalCloudAmount;
      CloudCoverState.h_code = originalCloudHeight;
    }
  }

  @Test
  public void omitsDirectAwsObservationSentinelCodes() {
    String originalVisibility = VisibilityState.VV_code;
    String originalPresentWeather = PresentWeatherState.ww_code;
    String originalPastWeatherOne = PastWeatherState.W1_code;
    String originalPastWeatherTwo = PastWeatherState.W2_code;
    String originalCloudCover = CloudCoverState.N_code;
    String originalCloudAmount = CloudCoverState.Nh_code;
    String originalCloudHeight = CloudCoverState.h_code;
    try {
      VisibilityState.VV_code = "//";
      PresentWeatherState.ww_code = "//";
      PastWeatherState.W1_code = "/";
      PastWeatherState.W2_code = "/";
      CloudCoverState.N_code = "/";
      CloudCoverState.Nh_code = "/";
      CloudCoverState.h_code = "/";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("", fields[7]);
      assertEquals("", fields[8]);
      assertEquals("", fields[9]);
      assertEquals("", fields[10]);
      assertEquals("", fields[11]);
      assertEquals("", fields[12]);
      assertEquals("", fields[16]);
    } finally {
      VisibilityState.VV_code = originalVisibility;
      PresentWeatherState.ww_code = originalPresentWeather;
      PastWeatherState.W1_code = originalPastWeatherOne;
      PastWeatherState.W2_code = originalPastWeatherTwo;
      CloudCoverState.N_code = originalCloudCover;
      CloudCoverState.Nh_code = originalCloudAmount;
      CloudCoverState.h_code = originalCloudHeight;
    }
  }

  @Test
  public void roundsManualNumericWeatherValues() {
    String originalWindUnits = main.wind_units;
    int originalWindSpeed = WindModel.int_true_wind_speed;
    String originalAirTemperature = TemperatureModel.air_temp;
    double originalRelativeHumidity = TemperatureModel.double_rv;
    String originalSeaWaterTemperature = TemperatureModel.sea_water_temp;
    boolean originalSpeedFromAws = main.true_wind_speed_from_AWS_present;
    boolean originalAirTemperatureFromAws = main.air_temp_from_AWS_present;
    boolean originalHumidityFromAws = main.rh_from_AWS_present;
    boolean originalSstFromAws = main.SST_from_AWS_present;
    try {
      main.wind_units = main.M_S;
      WindModel.int_true_wind_speed = 12;
      TemperatureModel.air_temp = "2.26";
      TemperatureModel.double_rv = 0.567;
      TemperatureModel.sea_water_temp = "4.26";
      main.true_wind_speed_from_AWS_present = false;
      main.air_temp_from_AWS_present = false;
      main.rh_from_AWS_present = false;
      main.SST_from_AWS_present = false;

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("12.0", fields[3]);
      assertEquals("2.3", fields[4]);
      assertEquals("57", fields[5]);
      assertEquals("4.3", fields[6]);
    } finally {
      main.wind_units = originalWindUnits;
      WindModel.int_true_wind_speed = originalWindSpeed;
      TemperatureModel.air_temp = originalAirTemperature;
      TemperatureModel.double_rv = originalRelativeHumidity;
      TemperatureModel.sea_water_temp = originalSeaWaterTemperature;
      main.true_wind_speed_from_AWS_present = originalSpeedFromAws;
      main.air_temp_from_AWS_present = originalAirTemperatureFromAws;
      main.rh_from_AWS_present = originalHumidityFromAws;
      main.SST_from_AWS_present = originalSstFromAws;
    }
  }

  @Test
  public void omitsWeatherValuesProvidedByAws() {
    String originalAirTemperature = TemperatureModel.air_temp;
    double originalRelativeHumidity = TemperatureModel.double_rv;
    String originalSeaWaterTemperature = TemperatureModel.sea_water_temp;
    boolean originalAirTemperatureFromAws = main.air_temp_from_AWS_present;
    boolean originalHumidityFromAws = main.rh_from_AWS_present;
    boolean originalSstFromAws = main.SST_from_AWS_present;
    try {
      TemperatureModel.air_temp = "2.26";
      TemperatureModel.double_rv = 0.567;
      TemperatureModel.sea_water_temp = "4.26";
      main.air_temp_from_AWS_present = true;
      main.rh_from_AWS_present = true;
      main.SST_from_AWS_present = true;

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("", fields[4]);
      assertEquals("", fields[5]);
      assertEquals("", fields[6]);
    } finally {
      TemperatureModel.air_temp = originalAirTemperature;
      TemperatureModel.double_rv = originalRelativeHumidity;
      TemperatureModel.sea_water_temp = originalSeaWaterTemperature;
      main.air_temp_from_AWS_present = originalAirTemperatureFromAws;
      main.rh_from_AWS_present = originalHumidityFromAws;
      main.SST_from_AWS_present = originalSstFromAws;
    }
  }
}
