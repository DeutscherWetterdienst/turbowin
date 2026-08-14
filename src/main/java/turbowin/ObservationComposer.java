package turbowin;

import static turbowin.main.*;

/** Builds legacy coded observations from the current observation state. */
final class ObservationComposer {

  private ObservationComposer() {}

  static String compose(String SPATIE) {
    String coded_obs_call_sign; // station ID from version 4.2
    String coded_obs_YY;
    String coded_obs_GG;
    String coded_obs_iw;
    String coded_obs_lalala;
    String coded_obs_lolololo;
    String coded_obs_Qc;
    String coded_obs_Ds;
    String coded_obs_vs;
    String coded_obs_N;
    String coded_obs_Nh;
    String coded_obs_h;
    String coded_obs_VV;
    String coded_obs_dd;
    String coded_obs_ff;
    String coded_obs_snTTT;
    String coded_obs_snTdTdTd;
    String coded_obs_PPPP;
    String coded_obs_a;
    String coded_obs_ppp;
    String coded_obs_ww;
    String coded_obs_W1;
    String coded_obs_W2;
    String coded_obs_Cl;
    String coded_obs_Cm;
    String coded_obs_Ch;
    String coded_obs_ssTsTsTs;
    String coded_obs_Pw;
    String coded_obs_Hw;
    String coded_obs_Dw1;
    String coded_obs_Hw1;
    String coded_obs_Pw1;
    String coded_obs_Dw2;
    String coded_obs_Hw2;
    String coded_obs_Pw2;
    String coded_obs_Is;
    String coded_obs_EsEs;
    String coded_obs_Rs;
    String coded_obs_snTbTbTb;
    String coded_obs_ci;
    String coded_obs_Si;
    String coded_obs_bi;
    String coded_obs_Di;
    String coded_obs_zi;

    // if station ID or masked call sign (= masked sation ID) inserted -> use this for obs else
    // 'normal' call sign
    //
    if ((station_ID != null) && (station_ID.trim().length() > 0)) {
      coded_obs_call_sign = station_ID;
    } else {
      coded_obs_call_sign = "unknown";
    }

    // JOptionPane.showMessageDialog(null, obs_call_sign, "mycallsign.call_sign",
    // JOptionPane.INFORMATION_MESSAGE);

    // date/time (null value if YY_code never activated /null pointer, "" if page visited but
    // nothing done)"
    //
    coded_obs_YY = ObservationCodeFormatter.direct(mydatetime.YY_code, "//");

    coded_obs_GG = ObservationCodeFormatter.direct(mydatetime.GG_code, "//");

    // position
    //
    coded_obs_Qc = ObservationCodeFormatter.direct(myposition.Qc_code, "/");

    coded_obs_lalala = ObservationCodeFormatter.direct(myposition.lalala_code, "///");

    coded_obs_lolololo = ObservationCodeFormatter.direct(myposition.lolololo_code, "////");

    // ship course
    //
    coded_obs_Ds = ObservationCodeFormatter.direct(myposition.Ds_code, "/");

    // ship speed
    //
    coded_obs_vs = ObservationCodeFormatter.direct(myposition.vs_code, "/");

    // total cloud cover (N)
    //
    coded_obs_N = ObservationCodeFormatter.direct(mycloudcover.N_code, "/");

    // cover Cl/Cm (Nh)
    //
    coded_obs_Nh = ObservationCodeFormatter.direct(mycloudcover.Nh_code, "/");

    // height lowest cloud in the sky (h)
    //
    coded_obs_h = ObservationCodeFormatter.direct(mycloudcover.h_code, "/");

    // visibility (VV)
    //
    coded_obs_VV = ObservationCodeFormatter.direct(myvisibility.VV_code, "//");

    // winds source
    coded_obs_iw = ObservationCodeFormatter.direct(mywind.iw_code, "/");

    // wind direction (dd)
    //
    coded_obs_dd = ObservationCodeFormatter.direct(mywind.dd_code, "//");

    // wind speed (ff)
    //
    if ((mywind.ff_code != null) && (mywind.ff_code.compareTo("") != 0)) {
      coded_obs_ff = mywind.ff_code;

      // extra 00fff group only added if wind speed >= 100 units (so only if fff00_code != "")
      if ((mywind.fff00_code != null) && (mywind.fff00_code.compareTo("") != 0)) {
        coded_obs_ff += SPATIE + "00" + mywind.fff00_code;
      }
    } else {
      coded_obs_ff = "//";
    }

    // air temperature
    //
    coded_obs_snTTT =
        ObservationCodeFormatter.combined(mytemp.sn_TTT_code, mytemp.TTT_code, "////");

    // dew point
    //
    coded_obs_snTdTdTd =
        ObservationCodeFormatter.combined(mytemp.sn_TdTdTd_code, mytemp.TdTdTd_code, "////");

    // air pressure (at MSL)
    //
    coded_obs_PPPP = ObservationCodeFormatter.direct(mybarometer.PPPP_code, "////");

    // air pressure tendency characteristic
    //
    coded_obs_a = ObservationCodeFormatter.direct(mybarograph.a_code, "/");

    // air pressure tendency amount
    //
    coded_obs_ppp = ObservationCodeFormatter.direct(mybarograph.ppp_code, "///");

    // present weather
    //
    coded_obs_ww = ObservationCodeFormatter.direct(mypresentweather.ww_code, "//");

    // past weather 1
    //
    coded_obs_W1 = ObservationCodeFormatter.direct(mypastweather.W1_code, "/");

    // past weather 2
    //
    coded_obs_W2 = ObservationCodeFormatter.direct(mypastweather.W2_code, "/");

    // clouds low (Cl)
    //
    coded_obs_Cl = ObservationCodeFormatter.direct(mycl.cl_code, "/");

    // clouds middle (Cm)
    //
    if ((mycm.cm_code != null) && (mycm.cm_code.compareTo("") != 0))
      coded_obs_Cm =
          mycm.cm_code.substring(
              0, 1); // omdat bij cm_code in geval Cm7 een a, b, c er achter staat (dus 7a, 7b, 7c)
    else coded_obs_Cm = "/";

    // clouds high (Ch)
    //
    coded_obs_Ch = ObservationCodeFormatter.direct(mych.ch_code, "/");

    // Tsea
    //
    coded_obs_ssTsTsTs =
        ObservationCodeFormatter.combined(mytemp.ss_TsTsTs_code, mytemp.TsTsTs_code, "////");

    // wind waves period
    //
    coded_obs_Pw = ObservationCodeFormatter.direct(mywaves.Pw_code, "//");

    // wind waves height
    //
    coded_obs_Hw = ObservationCodeFormatter.direct(mywaves.Hw_code, "//");

    // swell 1 direction
    //

    // JOptionPane.showMessageDialog(null, mywaves.Dw1_code, "mywaves.Dw1_code",
    // JOptionPane.WARNING_MESSAGE);
    coded_obs_Dw1 = ObservationCodeFormatter.direct(mywaves.Dw1_code, "//");

    // swell 1 period
    //
    coded_obs_Pw1 = ObservationCodeFormatter.direct(mywaves.Pw1_code, "//");

    // swell 1 height
    //
    coded_obs_Hw1 = ObservationCodeFormatter.direct(mywaves.Hw1_code, "//");

    // swell 2 direction
    //
    coded_obs_Dw2 = ObservationCodeFormatter.direct(mywaves.Dw2_code, "//");

    // swell 2 period
    //
    coded_obs_Pw2 = ObservationCodeFormatter.direct(mywaves.Pw2_code, "//");

    // swell 2 height
    //
    coded_obs_Hw2 = ObservationCodeFormatter.direct(mywaves.Hw2_code, "//");

    // icing cause (Is)
    //
    coded_obs_Is = ObservationCodeFormatter.direct(myicing.Is_code, "/");

    // icing thickness (EsEs)
    //
    coded_obs_EsEs = ObservationCodeFormatter.direct(myicing.EsEs_code, "//");

    // icing rate (Rs)
    //
    coded_obs_Rs = ObservationCodeFormatter.direct(myicing.Rs_code, "/");

    // wet bulb temperature (TbTbTb)
    //
    coded_obs_snTbTbTb =
        ObservationCodeFormatter.combined(mytemp.sn_TbTbTb_code, mytemp.TbTbTb_code, "////");

    // concentration or arrangement of sea ice (ci)
    //
    if ((myice1.ci_code != null) && (myice1.ci_code.compareTo("") != 0)) {
      if (myice1.ci_code.trim().equals("u")) // unable to report etc.
      {
        coded_obs_ci = "/";
      } else {
        coded_obs_ci = myice1.ci_code;
      }
    } else {
      coded_obs_ci = "/";
    }

    // stage of development Si)
    //
    if ((myice1.Si_code != null) && (myice1.Si_code.compareTo("") != 0)) {
      if (myice1.Si_code.trim().equals("u")) // unable to report etc.
      {
        coded_obs_Si = "/";
      } else {
        coded_obs_Si = myice1.Si_code;
      }
    } else {
      coded_obs_Si = "/";
    }

    // Ice of land origin (bi)
    //
    if ((myice1.bi_code != null) && (myice1.bi_code.compareTo("") != 0)) {
      if (myice1.bi_code.trim().equals("u")) // unable to report etc.
      {
        coded_obs_bi = "/";
      } else {
        coded_obs_bi = myice1.bi_code;
      }
    } else {
      coded_obs_bi = "/";
    }

    // Bearing of principal ice edge (Di)
    //
    if ((myice1.Di_code != null) && (myice1.Di_code.compareTo("") != 0)) {
      if (myice1.Di_code.trim().equals("u")) // unable to report etc.
      {
        coded_obs_Di = "/";
      } else {
        coded_obs_Di = myice1.Di_code;
      }
    } else {
      coded_obs_Di = "/";
    }

    // Ice situation and trend over preceding three hours (zi)
    //
    if ((myice1.zi_code != null) && (myice1.zi_code.compareTo("") != 0)) {
      if (myice1.zi_code.trim().equals("u")) // unable to report etc.
      {
        coded_obs_zi = "/";
      } else {
        coded_obs_zi = myice1.zi_code;
      }
    } else {
      coded_obs_zi = "/";
    }

    if ((coded_obs_call_sign.compareTo("unknown") != 0)
        && (coded_obs_YY.compareTo("//") != 0)
        && (coded_obs_GG.compareTo("//") != 0)
        && (coded_obs_Qc.compareTo("/") != 0)
        && (coded_obs_lalala.compareTo("///") != 0)
        && (coded_obs_lolololo.compareTo("////") != 0)) {
      // LET OP
      // NB met IE7 gaat als je voor spatie een " " neemt het wel goed
      //    met FireFox gaat als je voor spatie " " neemt het NIET goed

      coded_obs_total =
          "BBXX"
              + // BBXX
              SPATIE
              + coded_obs_call_sign
              + // D..D
              SPATIE
              + coded_obs_YY
              + coded_obs_GG
              + coded_obs_iw
              + // YYGGiw
              SPATIE
              + "99"
              + coded_obs_lalala
              + // 99LaLaLa
              SPATIE
              + coded_obs_Qc
              + coded_obs_lolololo
              + // QcLoLoLoLo
              SPATIE
              + "41"
              + coded_obs_h
              + coded_obs_VV
              + // irihhVV
              SPATIE
              + coded_obs_N
              + coded_obs_dd
              + coded_obs_ff
              + // Nddff
              SPATIE
              + "1"
              + coded_obs_snTTT
              + // 1snTTT
              SPATIE
              + "2"
              + coded_obs_snTdTdTd
              + // 2snTdTdTd
              SPATIE
              + "4"
              + coded_obs_PPPP
              + // 4PPPP
              SPATIE
              + "5"
              + coded_obs_a
              + coded_obs_ppp
              + // 5appp
              SPATIE
              + "7"
              + coded_obs_ww
              + coded_obs_W1
              + coded_obs_W2
              + // 7wwW1W2                                                         // 4PPPP
              SPATIE
              + "8"
              + coded_obs_Nh
              + coded_obs_Cl
              + coded_obs_Cm
              + coded_obs_Ch
              + // 8NhClCmCh                                                         // 4PPPP
              SPATIE
              + "222"
              + coded_obs_Ds
              + coded_obs_vs
              + // 222Dsvs                                                         // 4PPPP
              SPATIE
              + "0"
              + coded_obs_ssTsTsTs
              + // 0ssTwTwTw
              SPATIE
              + "2"
              + coded_obs_Pw
              + coded_obs_Hw; // 2PwPwHwHw
      // SPATIE +
      // "3" + coded_obs_Dw1 + coded_obs_Dw2 +                                            //
      // 3dw1dw1dw2dw2
      // SPATIE +
      // "4" + coded_obs_Pw1 + coded_obs_Hw1;                                             //
      // 4Pw1Pw1Hw1Hw1

      if (((coded_obs_Dw1 + coded_obs_Dw2).equals("////") == false)) {
        // swell dir group only if relevant data available (not ////)
        coded_obs_total += SPATIE + "3" + coded_obs_Dw1 + coded_obs_Dw2; // 5Pw2Pw2Hw2Hw2
      } // if (((coded_obs_Dw1 + coded_obs_Dw2).equals("////") == false))

      if (((coded_obs_Pw1 + coded_obs_Hw1).equals("////") == false)
          || (coded_obs_Dw1.equals("99") == true)) {
        // 1st swell group only if relevant data available (not ////) or if confused swell (Dw1 =
        // 99)
        coded_obs_total += SPATIE + "4" + coded_obs_Pw1 + coded_obs_Hw1; // 5Pw2Pw2Hw2Hw2
      } // if ( ((coded_obs_Pw1 + coded_obs_Hw1).equals("////") == false) )

      if (((coded_obs_Pw2 + coded_obs_Hw2).equals("////") == false)) {
        // 2nd swell group only if relevant data available (not ////)
        coded_obs_total += SPATIE + "5" + coded_obs_Pw2 + coded_obs_Hw2; // 5Pw2Pw2Hw2Hw2
      } // if ( ((coded_obs_Pw2 + coded_obs_Hw2).equals("////") == false) )

      if (((coded_obs_Is + coded_obs_EsEs + coded_obs_Rs).equals("////") == false)) {
        // icing group only if relevant data available (not ////)
        coded_obs_total += SPATIE + "6" + coded_obs_Is + coded_obs_EsEs + coded_obs_Rs; // 6IsEsEsRs
      } // if (((coded_obs_Is + coded_obs_EsEs + coded_obs_Rs).equals("////") == false))

      if ((coded_obs_snTbTbTb.equals("////") == false)) {
        // wet bulb group only if relevant data available (not ////)
        coded_obs_total += SPATIE + "8" + coded_obs_snTbTbTb; // 8swTbTbTb
      } // if ( (coded_obs_snTbTbTb.equals("////") == false) )

      if (((coded_obs_ci + coded_obs_Si + coded_obs_bi + coded_obs_Di + coded_obs_zi)
              .equals("/////")
          == false)) {
        // ice group only if relevant data available (not /////)
        coded_obs_total +=
            SPATIE
                + "ICE"
                + SPATIE
                + coded_obs_ci
                + coded_obs_Si
                + coded_obs_bi
                + coded_obs_Di
                + coded_obs_zi; // ICE ciSibiDizi
      } //

      coded_obs_total += "=";

    } // if ( (coded_obs_call_sign.compareTo("unknown") != 0) etc.
    else // call sign, dat/time or position not inserted
    {
      coded_obs_total = UNDEFINED;
    } // else

    return coded_obs_total;
  }
}
