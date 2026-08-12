package turbowin;

import static turbowin.main.*;

/** Builds and writes IMMT observation records. */
final class ImmtLogWorkflow {

  private ImmtLogWorkflow() {}

  static void start() {
    ImmtLogWriterWorkflow.start(composeRecord());
  }

  static String composeRecord() {
    final String SPATIE_1 = " ";
    final String SPATIE_2 = "  ";
    final String SPATIE_3 = "   ";
    final String SPATIE_4 = "    ";
    final String SPATIE_5 = "     ";
    final String SPATIE_6 = "      ";
    final String SPATIE_7 = "       ";

    String immt_rec = "";
    String Qc_1 = "9"; // 9 = the value of the element is missing
    String Qc_2 = "9";
    String Qc_3 = "9";
    String Qc_4 = "9";
    String Qc_5 = "9";
    String Qc_6 = "9";
    String Qc_7 = "9";
    String Qc_8 = "9";
    String Qc_9 = "9";
    String Qc_10 = "9";
    String Qc_11 = "9";
    String Qc_12 = "9";
    String Qc_13 = "9";
    String Qc_14 = "9";
    String Qc_15 = "9";
    String Qc_16 = "9";
    String Qc_17 = "9";
    String Qc_18 = "9";
    String Qc_19 = "9";
    String Qc_20 = "9";
    String Qc_21 =
        SPATIE_1; // this one always blank (1 space) (MQCS version) MQCS version numbers not
    // suitable for checking done by this program
    String Qc_22 = "9"; // only for vosclim -> this program always vosclim
    String Qc_23 = "9"; // only for vosclim -> this program always vosclim
    String Qc_24 = "9"; // only for vosclim -> this program always vosclim
    String Qc_25 = "9"; // only for vosclim -> this program always vosclim
    // String Qc_26 = "9";                                      // only for vosclim -> this program
    // always vosclim
    String Qc_27 = "9"; // only for vosclim -> this program always vosclim
    String Qc_28 = "9"; // only for vosclim -> this program always vosclim
    String Qc_29 = "9"; // only for vosclim -> this program always vosclim

    boolean HDG_ok = false;
    boolean sl_code_ok = false;

    //
    // immt record velden vullen (volgens WMO version IMMT-5)
    //
    immt_rec = "3"; // char number 1 (3=temp. in tenths of degrees C)

    immt_rec += mydatetime.year; // char number 2-5
    immt_rec += mydatetime.MM_code; // char number 6-7
    immt_rec += mydatetime.YY_code; // char number 8-9
    immt_rec += mydatetime.GG_code; // char number 10-11

    immt_rec += myposition.Qc_code; // char number 12

    immt_rec += myposition.lalala_code; // char number 13-15
    immt_rec += myposition.lolololo_code; // char number 16-19
    Qc_20 = "1";

    immt_rec += "0"; // char number 20

    try {
      int num_h = Integer.valueOf(mycloudcover.h_code);
      if (num_h >= 0 && num_h <= 9) {
        immt_rec += mycloudcover.h_code; // char number 21
        Qc_1 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 21
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 21
    } // catch

    try {
      int num_VV = Integer.valueOf(myvisibility.VV_code);
      if (num_VV >= 90 && num_VV <= 99) {
        immt_rec += myvisibility.VV_code; // char number 22-23
        Qc_2 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 22-23
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 22-23
    } // catch

    try {
      int num_N = Integer.valueOf(mycloudcover.N_code);
      if (num_N >= 0 && num_N <= 9) {
        immt_rec += mycloudcover.N_code; // char number 24
        Qc_3 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 24
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 24
    } // catch

    try {
      int num_dd = Integer.valueOf(mywind.dd_code);
      if ((num_dd >= 0 && num_dd <= 36) || (num_dd == 99)) {
        immt_rec += mywind.dd_code; // char number 25-26
        Qc_4 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 25-26
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 25-26
    } // catch

    try {
      int num_iw = Integer.valueOf(mywind.iw_code);
      if (num_iw == 0 || num_iw == 1 || num_iw == 3 || num_iw == 4) {
        immt_rec += mywind.iw_code; // char number 27
      } else {
        immt_rec += SPATIE_1; // char number 27
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 27
    } // catch

    try {
      // max ff 99 units for IMMT
      int num_ff = Integer.valueOf(mywind.ff_code);
      if (num_ff >= 0 && num_ff <= 99) {
        immt_rec += mywind.ff_code; // char number 28-29
        Qc_5 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 28-29
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 28-29
    } // catch

    try {
      int num_sn_TTT = Integer.valueOf(mytemp.sn_TTT_code);
      if (num_sn_TTT >= 0 && num_sn_TTT <= 1) {
        immt_rec += mytemp.sn_TTT_code; // char number 30
      } else {
        immt_rec += SPATIE_1; // char number 30
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 30
    } // catch

    try {
      int num_TTT = Integer.valueOf(mytemp.TTT_code);
      if (num_TTT >= 0 && num_TTT <= 999) {
        immt_rec += mytemp.TTT_code; // char number 31-33
        Qc_6 = "1";
      } else {
        immt_rec += SPATIE_3; // char number 31-33
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_3; // char number 31-33
    } // catch

    try {
      // sign of dewpoint must be converted to 'computed immt code'
      // also Sn_TdTdTd = 7(ice) is possible contrary to the operational branch

      // System.out.println("num_sn_TdTdTd = " + num_sn_TdTdTd);
      // System.out.println("mytemp.sn_TdTdTd_code = " + mytemp.sn_TdTdTd_code);

      if ((mytemp.sn_TbTbTb_code.equals("")) && (mytemp.sn_TdTdTd_code.equals("") == false)) {
        if (mytemp.sn_TdTdTd_code.equals("0")) {
          immt_rec += "5"; // char number 34
        } else if (mytemp.sn_TdTdTd_code.equals("1")) {
          immt_rec += "6"; // char number 34
        } else {
          immt_rec += SPATIE_1; // char number 34
        }
      } // if ((mytemp.sn_TbTbTb_code.equals("")) && (mytemp.sn_TdTdTd_code.equals("") == false))
      else if ((mytemp.sn_TbTbTb_code.equals("") == false)
          && (mytemp.sn_TdTdTd_code.equals("") == false)) {
        int num_sn_TdTdTd = Integer.valueOf(mytemp.sn_TdTdTd_code);
        int num_sn_TbTbTb = Integer.valueOf(mytemp.sn_TbTbTb_code);

        if (num_sn_TdTdTd == 0) {
          immt_rec += "5"; // char number 34
        } else if (num_sn_TdTdTd == 1 && num_sn_TbTbTb != 2 && num_sn_TbTbTb != 7) {
          immt_rec += "6"; // char number 34
        } else if (num_sn_TdTdTd == 1 && (num_sn_TbTbTb == 2 || num_sn_TbTbTb == 7)) {
          immt_rec += "7"; // char number 34
        } else {
          immt_rec += SPATIE_1; // char number 34
        }
      } // else if ((mytemp.sn_TbTbTb_code.equals("") == false) && (mytemp.sn_TdTdTd_code.equals("")
      // == false))
      else {
        immt_rec += SPATIE_1; // char number 34
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 34
    } // catch

    try {
      int num_TdTdTd = Integer.valueOf(mytemp.TdTdTd_code);
      if (num_TdTdTd >= 0 && num_TdTdTd <= 999) {
        immt_rec += mytemp.TdTdTd_code; // char number 35-37
        Qc_7 = "1";
      } else {
        immt_rec += SPATIE_3; // char number 35-37
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_3; // char number 35-37
    } // catch

    try {
      int num_PPPP = Integer.valueOf(mybarometer.PPPP_code);
      if (num_PPPP >= 0 && num_PPPP <= 9999) {
        immt_rec += mybarometer.PPPP_code; // char number 38-41
        Qc_8 = "1";
      } else {
        immt_rec += SPATIE_4; // char number 38-41
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_4; // char number 38-41
    } // catch

    try {
      int num_ww = Integer.valueOf(mypresentweather.ww_code);
      if (num_ww >= 0 && num_ww <= 99) {
        immt_rec += mypresentweather.ww_code; // char number 42-43
        Qc_9 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 42-43
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 42-43
    } // catch

    try {
      int num_W1 = Integer.valueOf(mypastweather.W1_code);
      if (num_W1 >= 0 && num_W1 <= 9) {
        immt_rec += mypastweather.W1_code; // char number 44
        Qc_9 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 44
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 44
    } // catch

    try {
      int num_W2 = Integer.valueOf(mypastweather.W2_code);
      if (num_W2 >= 0 && num_W2 <= 9) {
        immt_rec += mypastweather.W2_code; // char number 45
        Qc_9 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 45
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 45
    } // catch

    try {
      int num_Nh = Integer.valueOf(mycloudcover.Nh_code);
      if (num_Nh >= 0 && num_Nh <= 9) {
        immt_rec += mycloudcover.Nh_code; // char number 46
        Qc_3 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 46
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 46
    } // catch

    try {
      int num_cl = Integer.valueOf(mycl.cl_code);
      if (num_cl >= 0 && num_cl <= 9) {
        immt_rec += mycl.cl_code; // char number 47
        Qc_3 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 47
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 47
    } // catch

    try {
      String hulp_cm_code = "";

      if (mycm.cm_code.length() > 1) {
        hulp_cm_code =
            mycm.cm_code.substring(
                0, 1); // NB .substring(0, 1) --> because Cm_code in case of Cm7 an a, b, c could be
        // sticked to the 7 (7a, 7b, 7c)
      } else {
        hulp_cm_code = mycm.cm_code;
      }

      int num_cm = Integer.valueOf(hulp_cm_code);
      if (num_cm >= 0 && num_cm <= 9) {
        immt_rec += hulp_cm_code; // char number 48
        Qc_3 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 48
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 48
    } // catch

    try {
      int num_ch = Integer.valueOf(mych.ch_code);
      if (num_ch >= 0 && num_ch <= 9) {
        immt_rec += mych.ch_code; // char number 49
        Qc_3 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 49
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 49
    } // catch

    try {
      int num_ss_TsTsTs = Integer.valueOf(mytemp.ss_TsTsTs_code);
      if (num_ss_TsTsTs >= 0 && num_ss_TsTsTs <= 7) {
        if (num_ss_TsTsTs == 0 || num_ss_TsTsTs == 2 || num_ss_TsTsTs == 4 || num_ss_TsTsTs == 6) {
          immt_rec += "0"; // char number 50
        } else if (num_ss_TsTsTs == 1
            || num_ss_TsTsTs == 3
            || num_ss_TsTsTs == 5
            || num_ss_TsTsTs == 7) {
          immt_rec += "1"; // char number 50
        } else {
          immt_rec += SPATIE_1; // char number 50
        }
      } else {
        immt_rec += SPATIE_1; // char number 50
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 50
    } // catch

    try {
      int num_TsTsTs = Integer.valueOf(mytemp.TsTsTs_code);
      if (num_TsTsTs >= 0 && num_TsTsTs <= 999) {
        immt_rec += mytemp.TsTsTs_code; // char number 51-53
        Qc_10 = "1";
      } else {
        immt_rec += SPATIE_3; // char number 51-53
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_3; // char number 51-53
    } // catch

    try {
      int num_immt_sst_indicator = Integer.valueOf(mytemp.immt_sst_indicator);
      if (num_immt_sst_indicator >= 0 && num_immt_sst_indicator <= 7) {
        immt_rec += mytemp.immt_sst_indicator; // char number 54
      } else {
        immt_rec += SPATIE_1; // char number 54
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 54
    } // catch

    if (method_waves.equals(SEA_AND_SWELL_ESTIMATED) == true) {
      immt_rec += "0"; // char number 55
    } else if (method_waves.equals(WAVES_MEASURED_SHIPBORNE) == true) {
      immt_rec += "1"; // char number 55
    } else if (method_waves.equals(WAVES_MEASURED_BUOY) == true) {
      immt_rec += "4"; // char number 55
    } else if (method_waves.equals(WAVES_MEASURED_OTHER) == true) {
      immt_rec += "7"; // char number 55
    } else {
      // arbitrary, it could be " " also, but I choose "0" (estimated)
      immt_rec += "0"; // char number 55
    }

    try {
      int num_Pw = Integer.valueOf(mywaves.Pw_code);
      if (num_Pw >= 0 && num_Pw <= 99) {
        immt_rec += mywaves.Pw_code; // char number 56-57
        Qc_11 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 56-57
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 56-57
    } // catch

    try {
      int num_Hw = Integer.valueOf(mywaves.Hw_code);
      if (num_Hw >= 0 && num_Hw <= 99) {
        immt_rec += mywaves.Hw_code; // char number 58-59
        Qc_12 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 58-59
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 58-59
    } // catch

    try {
      int num_Dw1 = Integer.valueOf(mywaves.Dw1_code);
      if (num_Dw1 >= 0 && num_Dw1 <= 99) {
        immt_rec += mywaves.Dw1_code; // char number 60-61
        Qc_13 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 60-61
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 60-61
    } // catch

    try {
      int num_Pw1 = Integer.valueOf(mywaves.Pw1_code);
      if (num_Pw1 >= 0 && num_Pw1 <= 99) {
        immt_rec += mywaves.Pw1_code; // char number 62-63
        Qc_13 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 62-63
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 62-63
    } // catch

    try {
      int num_Hw1 = Integer.valueOf(mywaves.Hw1_code);
      if (num_Hw1 >= 0 && num_Hw1 <= 99) {
        immt_rec += mywaves.Hw1_code; // char number 64-65
        Qc_13 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 64-65
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 64-65
    } // catch

    if (main.obs_format.equals(main.FORMAT_FM13)) {
      try {
        int num_Is = Integer.valueOf(myicing.Is_code);
        if (num_Is >= 1 && num_Is <= 5) {
          immt_rec += myicing.Is_code; // char number 66
        } else {
          immt_rec += SPATIE_1; // char number 66
        }
      } catch (NumberFormatException e) {
        immt_rec += SPATIE_1; // char number 66
      }
    } // if (main.obs_format.equals(main.FORMAT_FM13))
    else // (eg format 101)
    {
      // NB format 101 is not according WMO code table 1751 as required for Is
      immt_rec += SPATIE_1; // char number 66
    } // else

    try {
      int num_EsEs = Integer.valueOf(myicing.EsEs_code);
      if (num_EsEs >= 0 && num_EsEs <= 99) {
        immt_rec += myicing.EsEs_code; // char number 67-68
      } else {
        immt_rec += SPATIE_2; // char number 67-68
      }
    } catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 67-68
    }

    try {
      int num_Rs = Integer.valueOf(myicing.Rs_code);
      if (num_Rs >= 0 && num_Rs <= 4) {
        immt_rec += myicing.Rs_code; // char number 69
      } else {
        immt_rec += SPATIE_1; // char number 69
      }
    } catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 69
    }

    immt_rec += "4"; // char number 70      // source of obs [4=electronic logbook]

    immt_rec += "4"; // char number 71      // obs platform [4=VOSClim]

    if (station_ID.length() == 7) {
      immt_rec += station_ID; // char number 72-78
    } else // station ID less than 7 chars
    {
      // int lengte = call_sign.trim().toUpperCase().length();
      int lengte = station_ID.length();

      if (lengte == 7) {
        immt_rec += station_ID; // char number 72-78
      } else if (lengte == 6) {
        immt_rec += station_ID; // char number 72-77
        immt_rec += SPATIE_1; // char number 78
      } else if (lengte == 5) {
        immt_rec += station_ID; // char number 72-76
        immt_rec += SPATIE_2; // char number 77-78
      } else if (lengte == 4) {
        immt_rec += station_ID; // char number 72-75
        immt_rec += SPATIE_3; // char number 76-78
      } else if (lengte == 3) {
        immt_rec += station_ID; // char number 72-74
        immt_rec += SPATIE_4; // char number 75-78
      } else // NB only call sign (station_ID) length of 3,4,5,6,7 char allowed (see IMMT
      // description)
      {
        // note: in TurboWin "unknown"
        immt_rec += SPATIE_7; // char number 72-78
      }
    } // else (station ID less than 7 chars)

    if (recruiting_country.length() > 2) {
      immt_rec +=
          recruiting_country.substring(
              recruiting_country.length() - 2); // char number 79 - 80 (e.g. Netherlands NL)
    } else {
      immt_rec += SPATIE_2; // char number 79 - 80
    }

    immt_rec += SPATIE_1; // char number 81

    immt_rec += "3"; // char number 82     NB 3 = automated QC only(inc time-seq checks)

    immt_rec += "1"; // char number 83    // 1 = weather indicator: manual

    immt_rec += "4"; // char number 84    //ir

    immt_rec += SPATIE_3; // char number 85-87 // RRR

    immt_rec += SPATIE_1; // char number 88    // tr

    try {
      int num_sn_TbTbTb_code = Integer.valueOf(mytemp.sn_TbTbTb_code);
      if (num_sn_TbTbTb_code >= 0 && num_sn_TbTbTb_code <= 2) {
        immt_rec += mytemp.sn_TbTbTb_code; // char number 89
      } else {
        immt_rec += SPATIE_1; // char number 89
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 89
    } // catch

    try {
      int num_TbTbTb_code = Integer.valueOf(mytemp.TbTbTb_code);
      if (num_TbTbTb_code >= 0 && num_TbTbTb_code <= 999) {
        immt_rec += mytemp.TbTbTb_code; // char number 90-92
        Qc_19 = "1";
      } else {
        immt_rec += SPATIE_3; // char number 90-92
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_3; // char number 90-92
    } // catch

    try {
      int num_a_code = Integer.valueOf(mybarograph.a_code);
      if (num_a_code >= 0 && num_a_code <= 8) {
        immt_rec += mybarograph.a_code; // char number 93
        Qc_15 = "1";
      } else {
        immt_rec += SPATIE_1; // char number 93
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 93
    } // catch

    try {
      int num_ppp_code = Integer.valueOf(mybarograph.ppp_code);
      if (num_ppp_code >= 0 && num_ppp_code <= 999) {
        immt_rec += mybarograph.ppp_code; // char number 94-96
        Qc_16 = "1";
      } else {
        immt_rec += SPATIE_3; // char number 94-96
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_3; // char number 94-96
    } // catch

    if (main.obs_format.equals(main.FORMAT_101)) {
      // Ds in IMMT defined as 3 hours period; in format101 it is defined as 10 minutes period so Ds
      // cannot be put in IMMT
      immt_rec += SPATIE_1; // char number 97
    } else {
      try {
        int num_Ds_code = Integer.valueOf(myposition.Ds_code);
        if (num_Ds_code >= 0 && num_Ds_code <= 9) {
          immt_rec += myposition.Ds_code; // char number 97
          Qc_17 = "1";
        } else {
          immt_rec += SPATIE_1; // char number 97
        }
      } // try
      catch (NumberFormatException e) {
        immt_rec += SPATIE_1; // char number 97
      } // catch
    } // else

    if (main.obs_format.equals(main.FORMAT_101)) {
      // vs in IMMT defined as 3 hours period; in format101 it is defined as 10 minutes period so vs
      // cannot be put in IMMT
      immt_rec += SPATIE_1; // char number 97
    } else {
      try {
        int num_vs_code = Integer.valueOf(myposition.vs_code);
        if (num_vs_code >= 0 && num_vs_code <= 9) {
          immt_rec += myposition.vs_code; // char number 98
          Qc_18 = "1";
        } else {
          immt_rec += SPATIE_1; // char number 98
        }
      } // try
      catch (NumberFormatException e) {
        immt_rec += SPATIE_1; // char number 98
      } // catch
    } // else

    try {
      int num_Dw2 = Integer.valueOf(mywaves.Dw2_code);
      if (num_Dw2 >= 0 && num_Dw2 <= 99) {
        immt_rec += mywaves.Dw2_code; // char number 99-100
        Qc_13 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 99-100
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 99-100
    } // catch

    try {
      int num_Pw2 = Integer.valueOf(mywaves.Pw2_code);
      if (num_Pw2 >= 0 && num_Pw2 <= 99) {
        immt_rec += mywaves.Pw2_code; // char number 101-102
        Qc_13 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 101-102
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 101-102
    } // catch

    try {
      int num_Hw2 = Integer.valueOf(mywaves.Hw2_code);
      if (num_Hw2 >= 0 && num_Hw2 <= 99) {
        immt_rec += mywaves.Hw2_code; // char number 103-104
        Qc_13 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 103-104
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 103-104
    } // catch

    try {
      int num_ci = Integer.valueOf(myice1.ci_code);
      if (num_ci >= 0 && num_ci <= 9) {
        immt_rec += myice1.ci_code; // char number 105
      } else {
        immt_rec += SPATIE_1; // char number 105
      }
    } catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 105
    }

    try {
      int num_Si = Integer.valueOf(myice1.Si_code);
      if (num_Si >= 0 && num_Si <= 9) {
        immt_rec += myice1.Si_code; // char number 106
      } else {
        immt_rec += SPATIE_1; // char number 106
      }
    } catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 106
    }

    try {
      int num_bi = Integer.valueOf(myice1.bi_code);
      if (num_bi >= 0 && num_bi <= 9) {
        immt_rec += myice1.bi_code; // char number 107
      } else {
        immt_rec += SPATIE_1; // char number 107
      }
    } catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 107
    }

    try {
      int num_Di = Integer.valueOf(myice1.Di_code);
      if (num_Di >= 0 && num_Di <= 9) {
        immt_rec += myice1.Di_code; // char number 108
      } else {
        immt_rec += SPATIE_1; // char number 108
      }
    } catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 108
    }

    try {
      int num_zi = Integer.valueOf(myice1.zi_code);
      if (num_zi >= 0 && num_zi <= 9) {
        immt_rec += myice1.zi_code; // char number 109
      } else {
        immt_rec += SPATIE_1; // char number 109
      }
    } catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 109
    }

    immt_rec += "A"; // char number 110 (FM-13 code version)

    immt_rec += "5"; // char number 111 (IMMT version)

    immt_rec += Qc_1; // char number 112
    immt_rec += Qc_2; // char number 113
    immt_rec += Qc_3; // char number 114
    immt_rec += Qc_4; // char number 115
    immt_rec += Qc_5; // char number 116
    immt_rec += Qc_6; // char number 117
    immt_rec += Qc_7; // char number 118
    immt_rec += Qc_8; // char number 119
    immt_rec += Qc_9; // char number 120
    immt_rec += Qc_10; // char number 121
    immt_rec += Qc_11; // char number 122
    immt_rec += Qc_12; // char number 123
    immt_rec += Qc_13; // char number 124
    immt_rec += Qc_14; // char number 125
    immt_rec += Qc_15; // char number 126
    immt_rec += Qc_16; // char number 127
    immt_rec += Qc_17; // char number 128
    immt_rec += Qc_18; // char number 129
    immt_rec += Qc_19; // char number 130
    immt_rec += Qc_20; // char number 131
    immt_rec += Qc_21; // char number 132  (MQCS)

    try {
      int num_HDG = Integer.valueOf(mywind.HDG_code);
      if (num_HDG >= 1 && num_HDG <= 360) {
        immt_rec += mywind.HDG_code; // char number 133-135
        Qc_22 = "1";
        HDG_ok = true;
      } else {
        // heading (HDG) is blank if heading was the same as COG (see wind input screen)
        HDG_ok = false;
      }
    } // try
    catch (NumberFormatException e) {
      HDG_ok = false;
    } // catch

    // So HDG (heading) blank -> use COG
    if (HDG_ok == false) {
      try {
        int num_COG =
            Integer.valueOf(
                mywind.COG_code); // use COG for HDG (see wind input screen) will be inserted into
        // IMMT on the position of HDG
        if (num_COG >= 1 && num_COG <= 360) {
          immt_rec += mywind.COG_code; // char number 133-135
          Qc_22 = "1";
        } else {
          immt_rec += SPATIE_3; // char number 133-135
        }
      } // try
      catch (NumberFormatException e) {
        immt_rec += SPATIE_3; // char number 133-135
      } // catch
    } // if (HDG_ok == false)

    try {
      int num_COG = Integer.valueOf(mywind.COG_code);
      if (num_COG >= 0 && num_COG <= 360) {
        immt_rec += mywind.COG_code; // char number 136-138
        Qc_23 = "1";
      } else {
        immt_rec += SPATIE_3; // char number 136-138
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_3; // char number 136-138
    } // catch

    try {
      int num_SOG = Integer.valueOf(mywind.SOG_code);
      if (num_SOG >= 0 && num_SOG <= 99) {
        immt_rec += mywind.SOG_code; // char number 139-140
        Qc_24 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 139-140
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 139-140
    } // catch

    try {
      int num_SLL = Integer.valueOf(mywind.SLL_code);
      if (num_SLL >= 0 && num_SLL <= 99) {
        immt_rec += mywind.SLL_code; // char number 141-142
        Qc_25 = "1";
      } else {
        immt_rec += SPATIE_2; // char number 141-142
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 141-142
    } // catch

    try {
      int num_sl = Integer.valueOf(mywind.sl_code);
      if (num_sl >= 0 && num_sl <= 1) {
        immt_rec += mywind.sl_code; // char number 143
        sl_code_ok = true;
      } else {
        immt_rec += SPATIE_1; // char number 143
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_1; // char number 143
    } // catch

    try {
      int num_hh = Integer.valueOf(mywind.hh_code);
      if (num_hh >= 0 && num_hh <= 99) {
        immt_rec += mywind.hh_code; // char number 144-145

        // Qc27 serves as the indicator for both sl and hh (from IMMT-4)
        if (sl_code_ok == true) {
          // So both, sl and hh, are now ok
          Qc_27 = "1";
        }
      } else {
        immt_rec += SPATIE_2; // char number 144-145
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_2; // char number 144-145
    } // catch

    try {
      int num_RWD = Integer.valueOf(mywind.RWD_code);
      if (num_RWD >= 0 && num_RWD <= 360) {
        immt_rec += mywind.RWD_code; // char number 146-148
        Qc_28 = "1";
      } else {
        immt_rec += SPATIE_3; // char number 146-148
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_3; // char number 146-148
    } // catch

    try {
      int num_RWS = Integer.valueOf(mywind.RWS_code);
      if (num_RWS >= 0 && num_RWS <= 999) {
        immt_rec += mywind.RWS_code; // char number 149-151
        Qc_29 = "1";
      } else {
        immt_rec += SPATIE_3; // char number 149-151
      }
    } // try
    catch (NumberFormatException e) {
      immt_rec += SPATIE_3; // char number 149-151
    } // catch

    immt_rec += Qc_22; // char number 152
    immt_rec += Qc_23; // char number 153
    immt_rec += Qc_24; // char number 154
    immt_rec += Qc_25; // char number 155
    immt_rec += SPATIE_1; // char number 156      // NB former Qc_26;
    immt_rec += Qc_27; // char number 157
    immt_rec += Qc_28; // char number 158
    immt_rec += Qc_29; // char number 159

    if (mytemp.double_rv >= 0.0
        && mytemp.double_rv <= 1.0
        && (mytemp.RH.trim().length() > 0)) // NB double_rv range 0.0 - 1.0
    {
      // RH
      int num_RH_code =
          (int)
              Math.floor(
                  Math.abs(mytemp.double_rv * 100) * 10
                      + 0.5); // e.g 0.5 -> 50 % -> 500 tenths of percentage
      String RH_code = Integer.toString(num_RH_code); // convert to string

      // NB RH_code always 4 characters width e.g. 0885 (accomplish via construction below)
      int len = 4;
      if (RH_code.length() < len) // pad on left with zeros
      {
        RH_code = "0000000000".substring(0, len - RH_code.length()) + RH_code;
      }

      if (RH_code.length() == 4) {
        immt_rec += RH_code; // char number 160-163  // relative humidity
        immt_rec += "0"; // char number 164      // relative humidity indicator
      } else {
        immt_rec += SPATIE_4; // char number 160-163  // relative humidity
        immt_rec += SPATIE_1; // char number 164      // relative humidity indicator
      }
    } else {
      immt_rec += SPATIE_4; // char number 160-163  // relative humidity
      immt_rec += SPATIE_1; // char number 164      // relative humidity indicator
    }

    // immt_rec += SPATIE_1;                                // char number 165
    if (RS232_connection_mode == 3 || RS232_connection_mode == 11) //  EUCAWS or AMOS2X
    {
      immt_rec += "2"; // char number 165      // 2 = AWS + manual observation
    } else if (RS232_connection_mode == 9 || RS232_connection_mode == 10) // OMC-140 AWS connected
    {
      immt_rec += "1"; // char number 165      // 1 = AWS
    } else {
      immt_rec += "0"; // char number 165      // 0 = no AWS
    }

    int lengte_imo = imo_number.trim().length();

    if (lengte_imo == 7) {
      immt_rec += imo_number; // char number 166-172
    } else if (lengte_imo == 6) {
      immt_rec += imo_number; // char number 166-171
      immt_rec += SPATIE_1; // char number 172
    } else if (lengte_imo == 5) {
      immt_rec += imo_number; // char number 166-170
      immt_rec += SPATIE_2; // char number 171-172
    } else if (lengte_imo == 4) {
      immt_rec += imo_number; // char number 166-169
      immt_rec += SPATIE_3; // char number 170-172
    } else if (lengte_imo == 3) {
      immt_rec += imo_number; // char number 166-168
      immt_rec += SPATIE_4; // char number 169-172
    } else if (lengte_imo == 2) {
      immt_rec += imo_number; // char number 166-167
      immt_rec += SPATIE_5; // char number 168-172
    } else if (lengte_imo == 1) {
      immt_rec += imo_number; // char number 166
      immt_rec += SPATIE_6; // char number 167-172
    } else //
    {
      immt_rec += SPATIE_7; // char number 166-172
    }

    /* BEGIN: THE FOLLOWING SECTION IS NOT PART OF THE OFFICIAL IMMT FORMAT */
    immt_rec += SPATIE_1; // char number 173

    if (myobserver.selected_observer != null && myobserver.selected_observer.compareTo("") != 0) {
      /*
            if (recruiting_country.indexOf("GERMANY") != -1)
            {
               //System.out.println("+++ GERMANY ok");

               // NB ONLY for Germany:
               //    convert TurboWin+ format storage of observer to TurboWin storage (replace ; by space and only use sure name + (first) initial
               //    Stam;M;1st off;-;       ->      Stam M
               String selected_observer = "";

               if (myobserver.selected_observer.length() > 2)
               {
                  int pos = -1;
                  int number_read_commas = 0;

                  do
                  {
                     pos = myobserver.selected_observer.indexOf(";", pos + 1);
                     if (pos != -1)
                     {
                        number_read_commas++;
                        if (number_read_commas == 2)
                        {
                           selected_observer = myobserver.selected_observer.substring(0, pos);
                           selected_observer = selected_observer.replace(';', ' ');      //replace occurrence of ';' to ' '     (eg Stam;M  -> Stam M)
                           selected_observer = selected_observer.replaceAll("\\.", "");    //replaces all occurrences of '.' to '' (eg Brouwer M.F  -> Brouwer MF)
                           break;
                        }
                     } // if (pos != -1)
                  } while (pos != -1);
               } // if (myobserver.selected_observer.length() > 2)

               immt_rec += selected_observer;                    // char number 174 - ?

            } // if (recruiting_country.indexOf("GERMANY") != -1)
            else
            {
               immt_rec += myobserver.selected_observer;         // char number 174 - ?
            }
      */
      immt_rec += myobserver.selected_observer; // char number 174 - ?
    }

    /* END: THE FOLLOWING SECTION IS NOT PART OF THE OFFICIAL IMMT FORMAT */

    return immt_rec;
  }
}
