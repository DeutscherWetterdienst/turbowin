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
}
