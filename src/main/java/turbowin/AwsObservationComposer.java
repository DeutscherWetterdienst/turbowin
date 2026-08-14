package turbowin;

import static turbowin.main.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Compiles the AWS observation string from the current application state. */
final class AwsObservationComposer {

  private AwsObservationComposer() {}

  static String compile() {
    String AWS_obs = "";
    String AWS_id = "";
    String AWS_diff_SLL_WL = "";
    String AWS_dd = "";
    String AWS_ff = "";
    String AWS_TTT = "";
    String AWS_rh = "";
    String AWS_sst = "";
    String AWS_VV = "";
    String AWS_ww = "";
    String AWS_W1 = "";
    String AWS_W2 = "";
    String AWS_N = "";
    String AWS_Nh = "";
    String AWS_Cl = "";
    String AWS_Cm = "";
    String AWS_Ch = "";
    String AWS_h = "";
    String AWS_Pw = "";
    String AWS_Hw = "";
    String AWS_Dw1 = "";
    String AWS_Pw1 = "";
    String AWS_Hw1 = "";
    String AWS_Dw2 = "";
    String AWS_Pw2 = "";
    String AWS_Hw2 = "";
    String AWS_EsEs = "";
    String AWS_Rs = "";
    String AWS_Is = "";
    String AWS_ci = "";
    String AWS_bi = "";
    String AWS_zi = "";
    String AWS_Si = "";
    String AWS_Di = "";
    String AWS_obs_id = "";

    double double_wind_speed;
    double double_sst;
    double double_air_temp;

    // See docs: - "EUCAWS inputs/outputs Complementary information about codes by Pierre Blouch"
    //           - "SMD & PSO formats by jean-Baptiste Cohuet"

    // AWS NMEA identifier
    //
    AWS_id = "$PTBWP";

    // departure of SLL from the actual sea level (diff SLL - WL) [format sHH; range -10..20;
    // resolution 1; units m]
    //
    AWS_diff_SLL_WL =
        diff_sll_wl; // global var (set in "Maintenance -> Station data" and "Input -> Wind")

    // wind direction [format WST; range 10..360; resolution 10; units deg]
    //
    if (true_wind_dir_from_AWS_present
        == false) // if parameter is measured by AWS than must be not a part of the string send to
    // the AWS
    {
      if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE) {
        AWS_dd = "0"; // NB wind dir = variable -> 0 : special for EUCAWS!!
      } else if (mywind.int_true_wind_dir != INVALID) {
        // NB So wind_dir = 0 (calm) also included here
        AWS_dd = Integer.toString(mywind.int_true_wind_dir);
      } else {
        AWS_dd = "";
      }
    } // if (true_wind_dir_from_AWS_present == false)

    // wind speed [format WS.s; range 0..75; resolution 0.1; units m/s]
    //
    if (true_wind_speed_from_AWS_present == false) {
      if (mywind.int_true_wind_speed != INVALID) {
        if (main.wind_units.trim().indexOf(main.M_S) != -1) // so wind speed in m/s
        {
          double_wind_speed = mywind.int_true_wind_speed * 1.0; // double_wind_speed: units m/s
        } else // so wind speed units knots or wind speed units unknown
        {
          double_wind_speed =
              mywind.int_true_wind_speed * KNOT_M_S_CONVERSION; // double_wind_speed: units m/s
        }

        // rounded one digit
        BigDecimal bd =
            new BigDecimal(double_wind_speed)
                .setScale(1, RoundingMode.HALF_UP); // one decimal, rounded e.g. 2.12939 -> 2.1
        double_wind_speed = bd.doubleValue();

        AWS_ff = Double.toString(double_wind_speed);
      } else {
        AWS_ff = "";
      }
    } // if (true_wind_speed_from_AWS_present == false)

    // air  temperature [format sTA.w; range -60..+60; resolution 0.1; unit C]
    //
    if (air_temp_from_AWS_present == false) {
      if ((mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null)) {
        double_air_temp = Double.parseDouble(mytemp.air_temp);

        BigDecimal bd =
            new BigDecimal(double_air_temp)
                .setScale(1, RoundingMode.HALF_UP); // one decimal, rounded e.g. 2.12939 -> 2.1
        double_air_temp = bd.doubleValue();

        AWS_TTT = Double.toString(double_air_temp);
      } else {
        AWS_TTT = "";
      }
    } // if (air_temp_from_AWS_present == false)

    // relative humidity [format UUU; range 0..100; resolution 1; unit %]
    //
    if (rh_from_AWS_present == false) {
      if ((mytemp.double_rv != main.INVALID)) {
        int int_rh =
            (int)
                Math.round(mytemp.double_rv * 100); // rounding and in % (and eg not 100.0 but 100)

        if ((int_rh >= 0) && (int_rh <= 100)) {
          AWS_rh = Integer.toString(int_rh);
        } else {
          AWS_rh = "";
        }
      } // if ((mytemp.double_rv != main.INVALID))
      else {
        AWS_rh = "";
      }
    } // if (rh_from_AWS_present == false)

    // sea water temperature [format sTW.w; range -5..45; resolution 0.1; unit C]
    //
    if (SST_from_AWS_present == false) {
      if ((mytemp.sea_water_temp.compareTo("") != 0) && (mytemp.sea_water_temp != null)) {
        double_sst = Double.parseDouble(mytemp.sea_water_temp);

        BigDecimal bd =
            new BigDecimal(double_sst)
                .setScale(1, RoundingMode.HALF_UP); // one decimal, rounded e.g. 2.12939 -> 2.1
        double_sst = bd.doubleValue();

        AWS_sst = Double.toString(double_sst);
      } else {
        AWS_sst = "";
      }
    } // if (SST_from_AWS_present == false)

    // visibility [format VV; range 0..99; resolution -; units: code]
    //
    if (myvisibility.VV_code.equals("//")) {
      AWS_VV = "";
    } else if ((myvisibility.VV_code != null) && (myvisibility.VV_code.compareTo("") != 0)) {
      AWS_VV = myvisibility.VV_code;
    } else {
      AWS_VV = "";
    }

    // Present Weather [format WW; range 0..99; resolution -; units: bufr code table 020003]
    //
    if (mypresentweather.ww_code.equals("//")) {
      AWS_ww = "";
    } else if ((mypresentweather.ww_code != null)
        && (mypresentweather.ww_code.compareTo("") != 0)) {
      AWS_ww = mypresentweather.ww_code;
    } else {
      AWS_ww = "";
    }

    // Past weather 1 (W1; bufr table 020004)
    //
    if (mypastweather.W1_code.equals("/")) {
      AWS_W1 = "";
    } else if ((mypastweather.W1_code != null) && (mypastweather.W1_code.compareTo("") != 0)) {
      AWS_W1 = mypastweather.W1_code;
    } else {
      AWS_W1 = "";
    }

    // past weather 2 (W2; bufr table 020004)
    //
    if (mypastweather.W2_code.equals("/")) {
      AWS_W2 = "";
    } else if ((mypastweather.W2_code != null) && (mypastweather.W2_code.compareTo("") != 0)) {
      AWS_W2 = mypastweather.W2_code;
    } else {
      AWS_W2 = "";
    }

    // total cloud cover (N)
    //
    if (mycloudcover.N_code.equals("/")) {
      AWS_N = "";
    } else if ((mycloudcover.N_code != null) && (mycloudcover.N_code.compareTo("") != 0)) {
      AWS_N = mycloudcover.N_code;
    } else {
      AWS_N = "";
    }

    // Cloud amount Cl/Cm (Nh) [bufr table 020011]
    //
    if (mycloudcover.Nh_code.equals("/")) {
      AWS_Nh = "";
    } else if ((mycloudcover.Nh_code != null) && (mycloudcover.Nh_code.compareTo("") != 0)) {
      AWS_Nh = mycloudcover.Nh_code;
    } else {
      AWS_Nh = "";
    }

    // clouds low (Cl) [bufr table 020012]
    //
    AWS_Cl = AwsCloudCodeFormatter.convert(mycl.cl_code, 30, "low (Cl)");

    // clouds middle (Cm) [bufr table 020012]
    //
    AWS_Cm = AwsCloudCodeFormatter.convertFirstDigit(mycm.cm_code, 20, "middle (Cm)");

    // clouds high (Ch) [bufr table 020012]
    //
    AWS_Ch = AwsCloudCodeFormatter.convert(mych.ch_code, 10, "high (Ch)");

    // height of base of lowest clouds (h)
    //
    if (mycloudcover.h_code.equals("/")) {
      AWS_h = "";
    } else if ((mycloudcover.h_code != null) && (mycloudcover.h_code.compareTo("") != 0)) {
      AWS_h = mycloudcover.h_code;
    } else {
      AWS_h = "";
    }

    // Pw (period wind waves)
    //
    AWS_Pw = AwsWaveCodeFormatter.period(mywaves.Pw_code);

    // Hw (height of wind waves)
    //
    AWS_Hw = AwsWaveCodeFormatter.height(mywaves.Hw_code);

    // dw1 (direction of first swell)
    //
    AWS_Dw1 = AwsWaveCodeFormatter.direction(mywaves.Dw1_code);

    // Pw1 (period of first swell)
    //
    AWS_Pw1 = AwsWaveCodeFormatter.period(mywaves.Pw1_code);

    // Hw1 (height of first swell)
    //
    AWS_Hw1 = AwsWaveCodeFormatter.height(mywaves.Hw1_code);

    // Dw2 (direction of second swell)
    //
    AWS_Dw2 = AwsWaveCodeFormatter.direction(mywaves.Dw2_code);

    // Pw2 (period of second swell)
    //
    AWS_Pw2 = AwsWaveCodeFormatter.period(mywaves.Pw2_code);

    // Hw2 (height of second swell)
    //
    AWS_Hw2 = AwsWaveCodeFormatter.height(mywaves.Hw2_code);

    // ice deposit (thickness)
    //
    AWS_EsEs = AwsIceCodeFormatter.thickness(myicing.EsEs_code);

    // rate of ice accretion (Rs) [bufr table 020032]
    //
    AWS_Rs = AwsIceCodeFormatter.direct(myicing.Rs_code);

    // cause of ice accretion (Is) [bufr table 020033]
    //
    AWS_Is = AwsIceCodeFormatter.iceCause(myicing.Is_code);

    // sea ice concentration (ci) [bufr table 020034]
    //
    AWS_ci = AwsIceCodeFormatter.unknownValue(myice1.ci_code, "14");

    // amount and type of ice (bi) [bufr table 020035]
    //
    AWS_bi = AwsIceCodeFormatter.unknownValue(myice1.bi_code, "14");

    // ice situation (zi) [bufr table 020036]
    //
    AWS_zi = AwsIceCodeFormatter.unknownValue(myice1.zi_code, "30");

    // ice development (Si) [bufr table 020037]
    //
    AWS_Si = AwsIceCodeFormatter.unknownValue(myice1.Si_code, "30");

    // bearing of ice edge (Di) [bufr id 020038 NO TABLE]
    //
    AWS_Di = AwsIceCodeFormatter.iceBearing(myice1.Di_code);

    // OBS_ID (but only if requested and set in the maintenance section
    //
    if (eucaws_obs_id == true) {
      if (!"".equals(myobserver.selected_observer)) // so there is an observer selected
      {
        // extract OBS_ID (column3) from selected_observer  e.g.: "Janssen;K;AB1;-;" -> OBS_ID = AB1
        int firstSemicolon = myobserver.selected_observer.indexOf(';');
        int secondSemicolon = myobserver.selected_observer.indexOf(';', firstSemicolon + 1);
        int thirdSemicolon = myobserver.selected_observer.indexOf(';', secondSemicolon + 1);

        if (firstSemicolon != -1 && secondSemicolon != -1 && thirdSemicolon != -1) {
          String selected_obs_id =
              myobserver.selected_observer.substring(secondSemicolon + 1, thirdSemicolon);

          if (selected_obs_id.length() == 3) // so a "-" is also not taken into account
          {
            AWS_obs_id = selected_obs_id;
          } else {
            AWS_obs_id = "";
          }
        } // if (firstSemicolon != -1 && secondSemicolon != -1 && thirdSemicolon != -1)
      } // if (!"".equals(myobserver.selected_observer))
    } // if (eucaws_obs_id == true)

    // compose AWS string
    //
    AWS_obs =
        AWS_id
            + ","
            + AWS_diff_SLL_WL
            + ","
            + AWS_dd
            + ","
            + AWS_ff
            + ","
            + AWS_TTT
            + ","
            + AWS_rh
            + ","
            + AWS_sst
            + ","
            + AWS_VV
            + ","
            + AWS_ww
            + ","
            + AWS_W1
            + ","
            + AWS_W2
            + ","
            + AWS_N
            + ","
            + AWS_Nh
            + ","
            + AWS_Cl
            + ","
            + AWS_Cm
            + ","
            + AWS_Ch
            + ","
            + AWS_h
            + ","
            + AWS_Pw
            + ","
            + AWS_Hw
            + ","
            + AWS_Dw1
            + ","
            + AWS_Pw1
            + ","
            + AWS_Hw1
            + ","
            + AWS_Dw2
            + ","
            + AWS_Pw2
            + ","
            + AWS_Hw2
            + ","
            + AWS_EsEs
            + ","
            + AWS_Rs
            + ","
            + AWS_Is
            + ","
            + AWS_ci
            + ","
            + AWS_bi
            + ","
            + AWS_zi
            + ","
            + AWS_Si
            + ","
            + AWS_Di;

    // add OBS_ID if requested and present
    //
    // if ((eucaws_obs_id == true) && (!"".equals(AWS_obs_id))) //
    if (eucaws_obs_id == true) {
      // see https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/issues/211
      // $PTBWP,sHH,WDT,WS.s,sTA.a,UUU,TW.w,VV,WW,W1W1,W2W2,N,NH,CL,CM,CH,H,PW,HW.w,DSW1,
      // PW1,HW1.w1,DSW2, PW2,HW2.w2,E.EE,R,I,CC,BB,ZZ,SS,DDD,OBS
      AWS_obs += "," + AWS_obs_id; // could be empty ("") !
    }

    // voor testen
    // JOptionPane.showMessageDialog(null, AWS_obs  , APPLICATION_NAME + " AWS_obs",
    // JOptionPane.INFORMATION_MESSAGE);

    return AWS_obs;
  }
}
