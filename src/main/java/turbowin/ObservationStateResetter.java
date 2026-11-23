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
    PastWeatherState.W1_code = "";
    PastWeatherState.W2_code = "";
    PastWeatherState.past_weather_1 = "";
    PastWeatherState.past_weather_2 = "";

    // present weather
    PresentWeatherState.ww_code = "";
    PresentWeatherState.present_weather = "";

    // visibility
    VisibilityState.VV = "";
    VisibilityState.VV_code = "";

    // barograph
    BarographModel.a_code = "";
    BarographModel.ppp_code = "";
    BarographModel.pressure_amount_tendency = "";

    // barometer
    BarometerModel.clearManualReduction();
    BarometerModel.pressure_reading = "";
    BarometerModel.pressure_msl = "";
    BarometerModel.PPPP_code = "";
    BarometerModel.deepest_draft = "";
    // BarometerModel.barometer_instrument_correction_new = "";
    BarometerModel.pressure_reading_corrected = "";
    BarometerModel.pressure_msl_corrected = "";

    // Cl
    LowCloudState.cl_code = "";

    // Cm
    MiddleCloudState.cm_code = "";

    // Ch
    HighCloudState.ch_code = "";

    // cloud cover and height
    CloudCoverState.N = "";
    CloudCoverState.Nh = "";
    CloudCoverState.h = "";
    CloudCoverState.N_code = "";
    CloudCoverState.Nh_code = "";
    CloudCoverState.h_code = "";

    // Temperatures
    TemperatureModel.air_temp = "";
    TemperatureModel.wet_bulb_temp = "";
    TemperatureModel.RH = "";
    TemperatureModel.sea_water_temp = "";
    TemperatureModel.sn_TTT_code = "";
    TemperatureModel.TTT_code = "";
    TemperatureModel.sn_TbTbTb_code = ""; // Tb = Twet
    TemperatureModel.TbTbTb_code = "";
    TemperatureModel.ss_TsTsTs_code = ""; // Ts = Tsea
    TemperatureModel.TsTsTs_code = "";
    TemperatureModel.sn_TdTdTd_code = "";
    TemperatureModel.TdTdTd_code = "";
    TemperatureModel.immt_sst_indicator = "";
    TemperatureModel.double_dew_point = INVALID;
    TemperatureModel.wet_bulb_frozen = false;
    TemperatureModel.double_rv = INVALID;

    // Wind
    WindModel.wind_dir = "";
    WindModel.wind_speed = "";
    WindModel.ship_ground_course = "";
    WindModel.ship_ground_speed = "";
    WindModel.ship_heading = "";
    WindModel.dd_code = "";
    WindModel.ff_code = "";
    WindModel.fff00_code = "";
    WindModel.iw_code = "";
    WindModel.HDG_code = "";
    WindModel.COG_code = "";
    WindModel.SOG_code = "";
    WindModel.SLL_code = "";
    WindModel.sl_code = "";
    WindModel.hh_code = "";
    WindModel.RWD_code = "";
    WindModel.RWS_code = "";
    WindModel.int_true_wind_dir = INVALID;
    WindModel.int_true_wind_speed = INVALID;
    WindModel.int_relative_wind_speed = INVALID;
    WindModel.int_relative_wind_dir = INVALID;

    // Waves
    WavesModel.wind_waves_period = "";
    WavesModel.wind_waves_height = "";
    WavesModel.swell_1_dir = "";
    WavesModel.swell_1_period = "";
    WavesModel.swell_1_height = "";
    WavesModel.swell_2_dir = "";
    WavesModel.swell_2_period = "";
    WavesModel.swell_2_height = "";
    WavesModel.Hw_code = "";
    WavesModel.Pw_code = "";
    WavesModel.Dw1_code = "";
    WavesModel.Pw1_code = "";
    WavesModel.Hw1_code = "";
    WavesModel.Dw2_code = "";
    WavesModel.Pw2_code = "";
    WavesModel.Hw2_code = "";

    // Captain
    for (int r = 0; r < CaptainModel.CAPTAIN_ROWS; r++) {
      for (int c = 0; c < CaptainModel.CAPTAIN_COLUMNS; c++) {
        CaptainModel.captain_data[r][c] = "";
      }
    }

    // Observer
    for (int r = 0; r < ObserverModel.OBSERVER_ROWS; r++) {
      for (int c = 0; c < ObserverModel.OBSERVER_COLUMNS; c++) {
        ObserverModel.observer_data[r][c] = "";
      }
    }
    ObserverModel.selected_observer = "";

    // two swell systems
    // ---

    // one swell system
    // ---

    // confused swell
    // ---

    // Icing
    IcingState.Is_code = "";
    IcingState.EsEs_code = "";
    IcingState.Rs_code = "";

    // Ice
    IceState.ci_code = "";
    IceState.Si_code = "";
    IceState.bi_code = "";
    IceState.Di_code = "";
    IceState.zi_code = "";

    // position
    PositionModel.latitude_degrees = "";
    PositionModel.latitude_minutes = "";
    PositionModel.latitude_hemisphere = "";
    PositionModel.longitude_degrees = "";
    PositionModel.longitude_minutes = "";
    PositionModel.longitude_hemisphere = "";
    PositionModel.course = "";
    PositionModel.speed = "";
    PositionModel.lalala_code = "";
    PositionModel.lolololo_code = "";
    PositionModel.Qc_code = "";
    PositionModel.Ds_code = "";
    PositionModel.vs_code = "";
    PositionModel.int_latitude_degrees =
        INVALID; // also used for position sequence check and cloud height advice computation
    PositionModel.int_latitude_minutes = INVALID; // also used for position sequence check
    PositionModel.int_longitude_degrees =
        INVALID; // also used for position sequence check and cloud height advice computation
    PositionModel.int_longitude_minutes = INVALID; // also used for position sequence check
    PositionModel.SOG_APR = Double.MAX_VALUE; // exclusively used for APR
    PositionModel.COG_APR = Double.MAX_VALUE; // exclusively used for APR
    PositionModel.SOG_APR_wind = Double.MAX_VALUE; // exclusively used for APR
    PositionModel.COG_APR_wind = Double.MAX_VALUE; // exclusively used for APR

    // date time
    DateTimeModel.year = "";
    DateTimeModel.month = "";
    DateTimeModel.day = "";
    DateTimeModel.hour = "";
    DateTimeModel.MM_code = ""; // month of year
    DateTimeModel.YY_code = ""; // day of the month
    DateTimeModel.GG_code = ""; // hour of obs

    // call sign
    // --

  }
}
