package turbowin;

import static turbowin.main.*;

/** Resets observation model state without touching Swing controls. */
final class ObservationStateResetter {

  private ObservationStateResetter() {}

  static void resetValues() {
    System.out.println(
        "--- " + "Resetting all meteo parameters and clearing all fields main screen");

    // global var's
    //

    // past weather
    mypastweather.W1_code = "";
    mypastweather.W2_code = "";
    mypastweather.past_weather_1 = "";
    mypastweather.past_weather_2 = "";

    // present weather
    mypresentweather.ww_code = "";
    mypresentweather.present_weather = "";

    // visibility
    myvisibility.VV = "";
    myvisibility.VV_code = "";

    // barograph
    mybarograph.a_code = "";
    mybarograph.ppp_code = "";
    mybarograph.pressure_amount_tendency = "";

    // barometer
    mybarometer.pressure_reading = "";
    mybarometer.pressure_msl = "";
    mybarometer.PPPP_code = "";
    mybarometer.deepest_draft = "";
    // mybarometer.barometer_instrument_correction_new = "";
    mybarometer.pressure_reading_corrected = "";
    mybarometer.pressure_msl_corrected = "";

    // Cl
    mycl.cl_code = "";

    // Cm
    mycm.cm_code = "";

    // Ch
    mych.ch_code = "";

    // cloud cover and height
    mycloudcover.N = "";
    mycloudcover.Nh = "";
    mycloudcover.h = "";
    mycloudcover.N_code = "";
    mycloudcover.Nh_code = "";
    mycloudcover.h_code = "";

    // Temperatures
    mytemp.air_temp = "";
    mytemp.wet_bulb_temp = "";
    mytemp.RH = "";
    mytemp.sea_water_temp = "";
    mytemp.sn_TTT_code = "";
    mytemp.TTT_code = "";
    mytemp.sn_TbTbTb_code = ""; // Tb = Twet
    mytemp.TbTbTb_code = "";
    mytemp.ss_TsTsTs_code = ""; // Ts = Tsea
    mytemp.TsTsTs_code = "";
    mytemp.sn_TdTdTd_code = "";
    mytemp.TdTdTd_code = "";
    mytemp.immt_sst_indicator = "";
    mytemp.double_dew_point = INVALID;
    mytemp.wet_bulb_frozen = false;
    mytemp.double_rv = INVALID;

    // Wind
    mywind.wind_dir = "";
    mywind.wind_speed = "";
    mywind.ship_ground_course = "";
    mywind.ship_ground_speed = "";
    mywind.ship_heading = "";
    mywind.dd_code = "";
    mywind.ff_code = "";
    mywind.fff00_code = "";
    mywind.iw_code = "";
    mywind.HDG_code = "";
    mywind.COG_code = "";
    mywind.SOG_code = "";
    mywind.SLL_code = "";
    mywind.sl_code = "";
    mywind.hh_code = "";
    mywind.RWD_code = "";
    mywind.RWS_code = "";
    mywind.int_true_wind_dir = INVALID;
    mywind.int_true_wind_speed = INVALID;
    mywind.int_relative_wind_speed = INVALID;
    mywind.int_relative_wind_dir = INVALID;

    // Waves
    mywaves.wind_waves_period = "";
    mywaves.wind_waves_height = "";
    mywaves.swell_1_dir = "";
    mywaves.swell_1_period = "";
    mywaves.swell_1_height = "";
    mywaves.swell_2_dir = "";
    mywaves.swell_2_period = "";
    mywaves.swell_2_height = "";
    mywaves.Hw_code = "";
    mywaves.Pw_code = "";
    mywaves.Dw1_code = "";
    mywaves.Pw1_code = "";
    mywaves.Hw1_code = "";
    mywaves.Dw2_code = "";
    mywaves.Pw2_code = "";
    mywaves.Hw2_code = "";

    // Captain
    for (int r = 0; r < mycaptain.CAPTAIN_ROWS; r++) {
      for (int c = 0; c < mycaptain.CAPTAIN_COLUMNS; c++) {
        mycaptain.captain_data[r][c] = "";
      }
    }

    // Observer
    for (int r = 0; r < myobserver.OBSERVER_ROWS; r++) {
      for (int c = 0; c < myobserver.OBSERVER_COLUMNS; c++) {
        myobserver.observer_data[r][c] = "";
      }
    }
    myobserver.selected_observer = "";

    // two swell systems
    // ---

    // one swell system
    // ---

    // confused swell
    // ---

    // Icing
    myicing.Is_code = "";
    myicing.EsEs_code = "";
    myicing.Rs_code = "";

    // Ice
    myice1.ci_code = "";
    myice1.Si_code = "";
    myice1.bi_code = "";
    myice1.Di_code = "";
    myice1.zi_code = "";

    // position
    myposition.latitude_degrees = "";
    myposition.latitude_minutes = "";
    myposition.latitude_hemisphere = "";
    myposition.longitude_degrees = "";
    myposition.longitude_minutes = "";
    myposition.longitude_hemisphere = "";
    myposition.course = "";
    myposition.speed = "";
    myposition.lalala_code = "";
    myposition.lolololo_code = "";
    myposition.Qc_code = "";
    myposition.Ds_code = "";
    myposition.vs_code = "";
    myposition.int_latitude_degrees =
        INVALID; // also used for position sequence check and cloud height advice computation
    myposition.int_latitude_minutes = INVALID; // also used for position sequence check
    myposition.int_longitude_degrees =
        INVALID; // also used for position sequence check and cloud height advice computation
    myposition.int_longitude_minutes = INVALID; // also used for position sequence check
    myposition.SOG_APR = Double.MAX_VALUE; // exclusively used for APR
    myposition.COG_APR = Double.MAX_VALUE; // exclusively used for APR
    myposition.SOG_APR_wind = Double.MAX_VALUE; // exclusively used for APR
    myposition.COG_APR_wind = Double.MAX_VALUE; // exclusively used for APR

    // date time
    mydatetime.year = "";
    mydatetime.month = "";
    mydatetime.day = "";
    mydatetime.hour = "";
    mydatetime.MM_code = ""; // month of year
    mydatetime.YY_code = ""; // day of the month
    mydatetime.GG_code = ""; // hour of obs

    // call sign
    // --

  }
}
