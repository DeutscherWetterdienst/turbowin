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
    originalIntTrueWindDir = mywind.int_true_wind_dir;
    originalIntTrueWindSpeed = mywind.int_true_wind_speed;
    originalAirTemp = mytemp.air_temp;
    originalDoubleRv = mytemp.double_rv;
    originalSeaWaterTemp = mytemp.sea_water_temp;
    originalVvCode = myvisibility.VV_code;
    originalWwCode = mypresentweather.ww_code;
    originalW1Code = mypastweather.W1_code;
    originalW2Code = mypastweather.W2_code;
    originalNCode = mycloudcover.N_code;
    originalNhCode = mycloudcover.Nh_code;
    originalHCode = mycloudcover.h_code;
    originalClCode = mycl.cl_code;
    originalCmCode = mycm.cm_code;
    originalChCode = mych.ch_code;
    originalPwCode = mywaves.Pw_code;
    originalHwCode = mywaves.Hw_code;
    originalDw1Code = mywaves.Dw1_code;
    originalPw1Code = mywaves.Pw1_code;
    originalHw1Code = mywaves.Hw1_code;
    originalDw2Code = mywaves.Dw2_code;
    originalPw2Code = mywaves.Pw2_code;
    originalHw2Code = mywaves.Hw2_code;
    originalEsEsCode = myicing.EsEs_code;
    originalRsCode = myicing.Rs_code;
    originalIsCode = myicing.Is_code;
    originalCiCode = myice1.ci_code;
    originalBiCode = myice1.bi_code;
    originalZiCode = myice1.zi_code;
    originalSiCode = myice1.Si_code;
    originalDiCode = myice1.Di_code;
    originalSelectedObserver = myobserver.selected_observer;

    main.diff_sll_wl = "";
    main.wind_units = "knots";
    main.eucaws_obs_id = false;
    main.air_temp_from_AWS_present = false;
    main.rh_from_AWS_present = false;
    main.SST_from_AWS_present = false;
    main.true_wind_speed_from_AWS_present = false;
    main.true_wind_dir_from_AWS_present = false;

    mywind.int_true_wind_dir = main.INVALID;
    mywind.int_true_wind_speed = main.INVALID;
    mytemp.air_temp = "";
    mytemp.double_rv = main.INVALID;
    mytemp.sea_water_temp = "";
    myvisibility.VV_code = "//";
    mypresentweather.ww_code = "//";
    mypastweather.W1_code = "/";
    mypastweather.W2_code = "/";
    mycloudcover.N_code = "/";
    mycloudcover.Nh_code = "/";
    mycloudcover.h_code = "/";
    mycl.cl_code = "/";
    mycm.cm_code = "/";
    mych.ch_code = "/";
    mywaves.Pw_code = "//";
    mywaves.Hw_code = "//";
    mywaves.Dw1_code = "//";
    mywaves.Pw1_code = "//";
    mywaves.Hw1_code = "//";
    mywaves.Dw2_code = "//";
    mywaves.Pw2_code = "//";
    mywaves.Hw2_code = "//";
    myicing.EsEs_code = "//";
    myicing.Rs_code = "/";
    myicing.Is_code = "/";
    myice1.ci_code = "/";
    myice1.bi_code = "/";
    myice1.zi_code = "/";
    myice1.Si_code = "/";
    myice1.Di_code = "/";
    myobserver.selected_observer = "";
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
    mywind.int_true_wind_dir = originalIntTrueWindDir;
    mywind.int_true_wind_speed = originalIntTrueWindSpeed;
    mytemp.air_temp = originalAirTemp;
    mytemp.double_rv = originalDoubleRv;
    mytemp.sea_water_temp = originalSeaWaterTemp;
    myvisibility.VV_code = originalVvCode;
    mypresentweather.ww_code = originalWwCode;
    mypastweather.W1_code = originalW1Code;
    mypastweather.W2_code = originalW2Code;
    mycloudcover.N_code = originalNCode;
    mycloudcover.Nh_code = originalNhCode;
    mycloudcover.h_code = originalHCode;
    mycl.cl_code = originalClCode;
    mycm.cm_code = originalCmCode;
    mych.ch_code = originalChCode;
    mywaves.Pw_code = originalPwCode;
    mywaves.Hw_code = originalHwCode;
    mywaves.Dw1_code = originalDw1Code;
    mywaves.Pw1_code = originalPw1Code;
    mywaves.Hw1_code = originalHw1Code;
    mywaves.Dw2_code = originalDw2Code;
    mywaves.Pw2_code = originalPw2Code;
    mywaves.Hw2_code = originalHw2Code;
    myicing.EsEs_code = originalEsEsCode;
    myicing.Rs_code = originalRsCode;
    myicing.Is_code = originalIsCode;
    myice1.ci_code = originalCiCode;
    myice1.bi_code = originalBiCode;
    myice1.zi_code = originalZiCode;
    myice1.Si_code = originalSiCode;
    myice1.Di_code = originalDiCode;
    myobserver.selected_observer = originalSelectedObserver;
  }

  @Test
  public void compilesTheAwsMessageWithItsProtocolIdentifier() {
    String observation = AwsObservationComposer.compile();

    assertTrue(observation.startsWith("$PTBWP,"));
  }

  @Test
  public void convertsAndRoundsNumericInputs() {
    mywind.int_true_wind_speed = 10;
    mytemp.air_temp = "2.129";
    mytemp.double_rv = 0.567;
    mytemp.sea_water_temp = "2.129";

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
    mycm.cm_code = "7a";
    mych.ch_code = "3";
    myicing.Is_code = "1";
    myice1.ci_code = "u";
    myice1.Di_code = "2";
    main.eucaws_obs_id = true;
    myobserver.selected_observer = "Janssen;K;AB1;-;";

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
    int originalWindDirection = mywind.int_true_wind_dir;
    int originalWindSpeed = mywind.int_true_wind_speed;
    boolean originalDirectionFromAws = main.true_wind_dir_from_AWS_present;
    boolean originalSpeedFromAws = main.true_wind_speed_from_AWS_present;
    try {
      main.diff_sll_wl = "5";
      main.wind_units = main.M_S;
      mywind.int_true_wind_dir = 180;
      mywind.int_true_wind_speed = 10;
      main.true_wind_dir_from_AWS_present = false;
      main.true_wind_speed_from_AWS_present = false;

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("5", fields[1]);
      assertEquals("180", fields[2]);
      assertEquals("10.0", fields[3]);
    } finally {
      main.diff_sll_wl = originalDiffSllWl;
      main.wind_units = originalWindUnits;
      mywind.int_true_wind_dir = originalWindDirection;
      mywind.int_true_wind_speed = originalWindSpeed;
      main.true_wind_dir_from_AWS_present = originalDirectionFromAws;
      main.true_wind_speed_from_AWS_present = originalSpeedFromAws;
    }
  }

  @Test
  public void convertsWindWavePeriodsAndHeights() {
    String originalPw = mywaves.Pw_code;
    String originalHw = mywaves.Hw_code;
    String originalDw1 = mywaves.Dw1_code;
    String originalPw1 = mywaves.Pw1_code;
    String originalHw1 = mywaves.Hw1_code;
    String originalDw2 = mywaves.Dw2_code;
    String originalPw2 = mywaves.Pw2_code;
    String originalHw2 = mywaves.Hw2_code;
    try {
      mywaves.Pw_code = "03";
      mywaves.Hw_code = "03";
      mywaves.Dw1_code = "12";
      mywaves.Pw1_code = "04";
      mywaves.Hw1_code = "05";
      mywaves.Dw2_code = "24";
      mywaves.Pw2_code = "06";
      mywaves.Hw2_code = "07";

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
      mywaves.Pw_code = originalPw;
      mywaves.Hw_code = originalHw;
      mywaves.Dw1_code = originalDw1;
      mywaves.Pw1_code = originalPw1;
      mywaves.Hw1_code = originalHw1;
      mywaves.Dw2_code = originalDw2;
      mywaves.Pw2_code = originalPw2;
      mywaves.Hw2_code = originalHw2;
    }
  }

  @Test
  public void omitsUnavailableWindWaveValues() {
    String originalPw = mywaves.Pw_code;
    String originalHw = mywaves.Hw_code;
    String originalDw1 = mywaves.Dw1_code;
    String originalPw1 = mywaves.Pw1_code;
    String originalHw1 = mywaves.Hw1_code;
    String originalDw2 = mywaves.Dw2_code;
    String originalPw2 = mywaves.Pw2_code;
    String originalHw2 = mywaves.Hw2_code;
    try {
      mywaves.Pw_code = "99";
      mywaves.Hw_code = "99";
      mywaves.Dw1_code = "99";
      mywaves.Pw1_code = "99";
      mywaves.Hw1_code = "99";
      mywaves.Dw2_code = "99";
      mywaves.Pw2_code = "99";
      mywaves.Hw2_code = "99";

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
      mywaves.Pw_code = originalPw;
      mywaves.Hw_code = originalHw;
      mywaves.Dw1_code = originalDw1;
      mywaves.Pw1_code = originalPw1;
      mywaves.Hw1_code = originalHw1;
      mywaves.Dw2_code = originalDw2;
      mywaves.Pw2_code = originalPw2;
      mywaves.Hw2_code = originalHw2;
    }
  }

  @Test
  public void convertsCloudTypesToAwsCodes() {
    String originalLowCloud = mycl.cl_code;
    String originalMiddleCloud = mycm.cm_code;
    String originalHighCloud = mych.ch_code;
    try {
      mycl.cl_code = "1";
      mycm.cm_code = "7a";
      mych.ch_code = "3";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("31", fields[13]);
      assertEquals("27", fields[14]);
      assertEquals("13", fields[15]);
    } finally {
      mycl.cl_code = originalLowCloud;
      mycm.cm_code = originalMiddleCloud;
      mych.ch_code = originalHighCloud;
    }
  }

  @Test
  public void omitsUnavailableAndMalformedCloudTypes() {
    String originalLowCloud = mycl.cl_code;
    String originalMiddleCloud = mycm.cm_code;
    String originalHighCloud = mych.ch_code;
    try {
      mycl.cl_code = "/";
      mycm.cm_code = "x";
      mych.ch_code = "";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("", fields[13]);
      assertEquals("", fields[14]);
      assertEquals("", fields[15]);
    } finally {
      mycl.cl_code = originalLowCloud;
      mycm.cm_code = originalMiddleCloud;
      mych.ch_code = originalHighCloud;
    }
  }

  @Test
  public void convertsIceCodesToAwsValues() {
    String originalIceThickness = myicing.EsEs_code;
    String originalIceRate = myicing.Rs_code;
    String originalIceCause = myicing.Is_code;
    String originalConcentration = myice1.ci_code;
    String originalAmountAndType = myice1.bi_code;
    String originalSituation = myice1.zi_code;
    String originalDevelopment = myice1.Si_code;
    String originalBearing = myice1.Di_code;
    try {
      myicing.EsEs_code = "04";
      myicing.Rs_code = "2";
      myicing.Is_code = "3";
      myice1.ci_code = "u";
      myice1.bi_code = "u";
      myice1.zi_code = "u";
      myice1.Si_code = "u";
      myice1.Di_code = "8";

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
      myicing.EsEs_code = originalIceThickness;
      myicing.Rs_code = originalIceRate;
      myicing.Is_code = originalIceCause;
      myice1.ci_code = originalConcentration;
      myice1.bi_code = originalAmountAndType;
      myice1.zi_code = originalSituation;
      myice1.Si_code = originalDevelopment;
      myice1.Di_code = originalBearing;
    }
  }

  @Test
  public void convertsEverySupportedIceCauseCode() {
    String originalIceCause = myicing.Is_code;
    String[] sourceCodes = {"1", "2", "3", "4", "5", "6", "14"};
    String[] expectedCodes = {"8", "4", "12", "2", "10", "6", "14"};
    try {
      for (int i = 0; i < sourceCodes.length; i++) {
        myicing.Is_code = sourceCodes[i];
        String[] fields = AwsObservationComposer.compile().split(",", -1);
        assertEquals(expectedCodes[i], fields[27]);
      }
    } finally {
      myicing.Is_code = originalIceCause;
    }
  }

  @Test
  public void convertsEverySupportedIceBearingCode() {
    String originalBearing = myice1.Di_code;
    String[] sourceCodes = {"1", "2", "3", "4", "5", "6", "7", "8"};
    String[] expectedCodes = {"45", "90", "135", "180", "225", "270", "315", "360"};
    try {
      for (int i = 0; i < sourceCodes.length; i++) {
        myice1.Di_code = sourceCodes[i];
        String[] fields = AwsObservationComposer.compile().split(",", -1);
        assertEquals(expectedCodes[i], fields[32]);
      }
    } finally {
      myice1.Di_code = originalBearing;
    }
  }

  @Test
  public void omitsUnavailableIceCodes() {
    String originalIceThickness = myicing.EsEs_code;
    String originalIceRate = myicing.Rs_code;
    String originalIceCause = myicing.Is_code;
    String originalConcentration = myice1.ci_code;
    String originalAmountAndType = myice1.bi_code;
    String originalSituation = myice1.zi_code;
    String originalDevelopment = myice1.Si_code;
    String originalBearing = myice1.Di_code;
    try {
      myicing.EsEs_code = "//";
      myicing.Rs_code = "/";
      myicing.Is_code = "9";
      myice1.ci_code = "/";
      myice1.bi_code = "/";
      myice1.zi_code = "/";
      myice1.Si_code = "/";
      myice1.Di_code = "0";

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
      myicing.EsEs_code = originalIceThickness;
      myicing.Rs_code = originalIceRate;
      myicing.Is_code = originalIceCause;
      myice1.ci_code = originalConcentration;
      myice1.bi_code = originalAmountAndType;
      myice1.zi_code = originalSituation;
      myice1.Si_code = originalDevelopment;
      myice1.Di_code = originalBearing;
    }
  }

  @Test
  public void preservesDirectAwsObservationCodes() {
    String originalVisibility = myvisibility.VV_code;
    String originalPresentWeather = mypresentweather.ww_code;
    String originalPastWeatherOne = mypastweather.W1_code;
    String originalPastWeatherTwo = mypastweather.W2_code;
    String originalCloudCover = mycloudcover.N_code;
    String originalCloudAmount = mycloudcover.Nh_code;
    String originalCloudHeight = mycloudcover.h_code;
    try {
      myvisibility.VV_code = "10";
      mypresentweather.ww_code = "45";
      mypastweather.W1_code = "2";
      mypastweather.W2_code = "3";
      mycloudcover.N_code = "7";
      mycloudcover.Nh_code = "5";
      mycloudcover.h_code = "4";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("10", fields[7]);
      assertEquals("45", fields[8]);
      assertEquals("2", fields[9]);
      assertEquals("3", fields[10]);
      assertEquals("7", fields[11]);
      assertEquals("5", fields[12]);
      assertEquals("4", fields[16]);
    } finally {
      myvisibility.VV_code = originalVisibility;
      mypresentweather.ww_code = originalPresentWeather;
      mypastweather.W1_code = originalPastWeatherOne;
      mypastweather.W2_code = originalPastWeatherTwo;
      mycloudcover.N_code = originalCloudCover;
      mycloudcover.Nh_code = originalCloudAmount;
      mycloudcover.h_code = originalCloudHeight;
    }
  }

  @Test
  public void omitsDirectAwsObservationSentinelCodes() {
    String originalVisibility = myvisibility.VV_code;
    String originalPresentWeather = mypresentweather.ww_code;
    String originalPastWeatherOne = mypastweather.W1_code;
    String originalPastWeatherTwo = mypastweather.W2_code;
    String originalCloudCover = mycloudcover.N_code;
    String originalCloudAmount = mycloudcover.Nh_code;
    String originalCloudHeight = mycloudcover.h_code;
    try {
      myvisibility.VV_code = "//";
      mypresentweather.ww_code = "//";
      mypastweather.W1_code = "/";
      mypastweather.W2_code = "/";
      mycloudcover.N_code = "/";
      mycloudcover.Nh_code = "/";
      mycloudcover.h_code = "/";

      String[] fields = AwsObservationComposer.compile().split(",", -1);

      assertEquals("", fields[7]);
      assertEquals("", fields[8]);
      assertEquals("", fields[9]);
      assertEquals("", fields[10]);
      assertEquals("", fields[11]);
      assertEquals("", fields[12]);
      assertEquals("", fields[16]);
    } finally {
      myvisibility.VV_code = originalVisibility;
      mypresentweather.ww_code = originalPresentWeather;
      mypastweather.W1_code = originalPastWeatherOne;
      mypastweather.W2_code = originalPastWeatherTwo;
      mycloudcover.N_code = originalCloudCover;
      mycloudcover.Nh_code = originalCloudAmount;
      mycloudcover.h_code = originalCloudHeight;
    }
  }
}
