package turbowin;

/** Coordinates the level-two conversions and cross-field validations. */
final class LevelTwoValidationWorkflow {

  private LevelTwoValidationWorkflow() {}

  static boolean run(main_support support) {
    boolean doorgaan = true;
    boolean level_2_ok = true;
    boolean wind_waves_period_conversion_ok = true;
    boolean wind_waves_height_conversion_ok = true;
    boolean pressure_amount_tendency_conversion_ok = true;
    boolean cl_code_conversion_ok = true;
    boolean cm_code_conversion_ok = true;
    boolean ch_code_conversion_ok = true;
    boolean ww_code_conversion_ok = true;
    boolean VV_code_conversion_ok = true;
    boolean air_temp_conversion_ok = true;
    float float_wind_waves_period = main.INVALID;
    float float_wind_waves_height = main.INVALID;
    float float_pressure_amount_tendency = main.INVALID;
    float float_air_temp = main.INVALID;
    int int_cl_code = main.INVALID;
    int int_cm_code = main.INVALID;
    int int_ch_code = main.INVALID;
    int int_ww_code = main.INVALID;
    int int_VV_code = main.INVALID;
    Integer sky_not_discernible_array[] = {43, 45, 47, 49};
    Integer drizzle_rain_array[] = {
      50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69
    };
    Integer present_weather_36_39_array[] = {36, 37, 38, 39};
    Integer present_weather_48_49_array[] = {48, 49};
    Integer present_weather_56_57_array[] = {56, 57};
    Integer present_weather_66_67_array[] = {66, 67};
    Integer present_weather_68_69_array[] = {68, 69};
    Integer present_weather_70_75_array[] = {70, 71, 72, 73, 74, 75};
    Integer present_weather_76_79_array[] = {76, 77, 78, 79};
    Integer present_weather_83_86_array[] = {83, 84, 85, 86};

    //
    ///////////////////////////// conversions //////////////////////////
    //

    main_support.WindWaveValidation windWaveValidation =
        support.validateWindWaves("checking_level_2", false);
    float_wind_waves_period = windWaveValidation.period();
    float_wind_waves_height = windWaveValidation.height();
    wind_waves_period_conversion_ok = windWaveValidation.periodValid();
    wind_waves_height_conversion_ok = windWaveValidation.heightValid();

    main_support.FloatValidation pressureTendencyValidation =
        support.validateFloat(
            mybarograph.pressure_amount_tendency,
            "pressure amount tendency conversion error",
            "checking_level_2");
    float_pressure_amount_tendency = pressureTendencyValidation.value();
    pressure_amount_tendency_conversion_ok = pressureTendencyValidation.valid();

    // string Cl code conversion to int
    Integer parsedLowCloudType =
        support.parseValidationInt(
            mycl.cl_code, "[GENERAL] Cl conversion error; Function: checking_level_2()");
    if (parsedLowCloudType != null) {
      int_cl_code = parsedLowCloudType;
      cl_code_conversion_ok = true;
    } else {
      cl_code_conversion_ok = false;
    }

    // string Cm code conversion to int
    Integer parsedMiddleCloudType =
        support.parseValidationFirstDigitInt(
            mycm.cm_code, "[GENERAL] Cm conversion error; Function: checking_level_2()");
    if (parsedMiddleCloudType != null) {
      int_cm_code = parsedMiddleCloudType;
      cm_code_conversion_ok = true;
    } else {
      cm_code_conversion_ok = false;
    }

    // string Ch code conversion to int
    Integer parsedHighCloudType =
        support.parseValidationInt(
            mych.ch_code, "[GENERAL] Ch conversion error; Function: checking_level_2()");
    if (parsedHighCloudType != null) {
      int_ch_code = parsedHighCloudType;
      ch_code_conversion_ok = true;
    } else {
      ch_code_conversion_ok = false;
    }

    main_support.IntegerValidation presentWeatherValidation =
        support.validateInteger(
            mypresentweather.ww_code, "ww conversion error", "checking_level_2");
    int_ww_code = presentWeatherValidation.value();
    ww_code_conversion_ok = presentWeatherValidation.valid();

    // string VV code conversion to int
    Integer parsedVisibility =
        support.parseValidationInt(
            myvisibility.VV_code, "[GENERAL] VV conversion error; Function: checking_level_2()");
    if (parsedVisibility != null) {
      int_VV_code = parsedVisibility;
      VV_code_conversion_ok = true;
    } else {
      VV_code_conversion_ok = false;
    }

    main_support.FloatValidation airTemperatureValidation =
        support.validateFloat(mytemp.air_temp, "air temp conversion error", "checking_level_2");
    float_air_temp = airTemperatureValidation.value();
    air_temp_conversion_ok = airTemperatureValidation.valid();

    //
    ///////////////////////////// checks //////////////////////////
    //

    //
    ////////// wind - waves checks /////
    //
    if (doorgaan
        && !LevelTwoWindWaveValidation.validate(
            main.wind_units,
            mywind.int_true_wind_speed,
            wind_waves_period_conversion_ok,
            float_wind_waves_period,
            wind_waves_height_conversion_ok,
            float_wind_waves_height)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// air pressure /////
    //
    if (doorgaan
        && !LevelTwoPressureValidation.validate(
            pressure_amount_tendency_conversion_ok,
            mybarograph.a_code,
            mybarograph.pressure_amount_tendency,
            float_pressure_amount_tendency)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// cloud cover <-> cloud types /////
    //
    if (doorgaan
        && !LevelTwoCloudValidation.validate(
            cl_code_conversion_ok,
            cm_code_conversion_ok,
            ch_code_conversion_ok,
            int_cl_code,
            int_cm_code,
            int_ch_code)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// cloud cover <-> present weather /////
    //
    if (doorgaan
        && !LevelTwoCloudValidation.validatePresentWeather(
            ww_code_conversion_ok, int_ww_code, sky_not_discernible_array, drizzle_rain_array)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// cloud type <-> cloud height /////
    //
    if (doorgaan
        && !LevelTwoCloudValidation.validateCloudHeights(
            cl_code_conversion_ok, ch_code_conversion_ok, int_cl_code, int_ch_code)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// visibilty <-> present weather /////
    //
    if (doorgaan
        && !VisibilityPresentWeatherValidation.validate(
            ww_code_conversion_ok,
            int_ww_code,
            VV_code_conversion_ok,
            int_VV_code,
            mypresentweather.ww_40)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// present weather <-> air temperature /////
    //
    if (doorgaan
        && !PresentWeatherTemperatureValidation.validate(
            air_temp_conversion_ok,
            float_air_temp,
            int_ww_code,
            present_weather_36_39_array,
            present_weather_48_49_array,
            present_weather_56_57_array,
            present_weather_66_67_array,
            present_weather_68_69_array,
            present_weather_70_75_array,
            present_weather_76_79_array,
            present_weather_83_86_array)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// icing <-> air temperature /////
    //
    if (doorgaan
        && !IcingAirTemperatureValidation.validate(
            air_temp_conversion_ok,
            float_air_temp,
            !myicing.Is_code.equals("")
                || !myicing.EsEs_code.equals("")
                || !myicing.Rs_code.equals(""))) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// ice <-> air temperature /////
    //
    if (doorgaan
        && !IceAirTemperatureValidation.validate(
            air_temp_conversion_ok,
            float_air_temp,
            !myice1.ci_code.equals("")
                || !myice1.Si_code.equals("")
                || !myice1.bi_code.equals("")
                || !myice1.Di_code.equals("")
                || !myice1.zi_code.equals(""))) {
      level_2_ok = false;
      doorgaan = false;
    }

    return level_2_ok;
  }
}
