package turbowin;

/** Coordinates the level-three conversions and cross-field validations. */
final class LevelThreeValidationWorkflow {

  private LevelThreeValidationWorkflow() {}

  static boolean run(main_support support) {
    boolean doorgaan = true;
    boolean level_3_ok = true;
    boolean wind_waves_period_conversion_ok = true;
    boolean wind_waves_height_conversion_ok = true;
    boolean first_swell_period_conversion_ok = true;
    boolean first_swell_height_conversion_ok = true;
    boolean second_swell_period_conversion_ok = true;
    boolean second_swell_height_conversion_ok = true;
    boolean air_temp_conversion_ok = true;
    boolean air_pressure_conversion_ok = true;
    boolean amount_pressure_tendency_conversion_ok = true;
    boolean sea_water_temp_conversion_ok = true;
    boolean ice_thickness_conversion_ok = true;
    boolean ww_code_conversion_ok = true;
    float float_wind_waves_period = main.INVALID;
    float float_wind_waves_height = main.INVALID;
    float float_first_swell_period = main.INVALID;
    float float_first_swell_height = main.INVALID;
    float float_second_swell_period = main.INVALID;
    float float_second_swell_height = main.INVALID;
    float float_air_temp = main.INVALID;
    float float_air_pressure_msl_corrected = main.INVALID;
    float float_amount_pressure_tendency = main.INVALID;
    float float_sea_water_temp = main.INVALID;
    float float_ice_thickness = main.INVALID;
    int int_ww_code = main.INVALID;
    //
    ///////////////////////////// conversions //////////////////////////
    //

    main_support.WindWaveValidation windWaveValidation =
        support.validateWindWaves("checking_level_3", true);
    float_wind_waves_period = windWaveValidation.period();
    float_wind_waves_height = windWaveValidation.height();
    wind_waves_period_conversion_ok = windWaveValidation.periodValid();
    wind_waves_height_conversion_ok = windWaveValidation.heightValid();

    // first swell system period conversion
    Float parsedFirstSwellPeriod =
        support.parseValidationFloat(
            mywaves.swell_1_period,
            "[GENERAL] first swell period conversion error; Function: checking_level_3()");
    if (parsedFirstSwellPeriod != null) {
      float_first_swell_period = parsedFirstSwellPeriod;
      first_swell_period_conversion_ok = true;
    } else {
      first_swell_period_conversion_ok = false;
    }

    // first swell system height conversion
    Float parsedFirstSwellHeight =
        support.parseValidationFloat(
            mywaves.swell_1_height,
            "[GENERAL] first swell height conversion error; Function: checking_level_3()");
    if (parsedFirstSwellHeight != null) {
      float_first_swell_height = parsedFirstSwellHeight;
      first_swell_height_conversion_ok = true;
    } else {
      first_swell_height_conversion_ok = false;
    }

    // second swell system period conversion
    Float parsedSecondSwellPeriod =
        support.parseValidationFloat(
            mywaves.swell_2_period,
            "[GENERAL] second swell period conversion error; Function: checking_level_3()");
    if (parsedSecondSwellPeriod != null) {
      float_second_swell_period = parsedSecondSwellPeriod;
      second_swell_period_conversion_ok = true;
    } else {
      second_swell_period_conversion_ok = false;
    }

    // second swell system height conversion
    Float parsedSecondSwellHeight =
        support.parseValidationFloat(
            mywaves.swell_2_height,
            "[GENERAL] second swell height conversion error; Function: checking_level_3()");
    if (parsedSecondSwellHeight != null) {
      float_second_swell_height = parsedSecondSwellHeight;
      second_swell_height_conversion_ok = true;
    } else {
      second_swell_height_conversion_ok = false;
    }

    main_support.FloatValidation airTemperatureValidation =
        support.validateFloat(mytemp.air_temp, "air temp conversion error", "checking_level_3");
    float_air_temp = airTemperatureValidation.value();
    air_temp_conversion_ok = airTemperatureValidation.valid();

    // string pressure_msl_corrected to float
    Float parsedAirPressureMsl =
        support.parseValidationFloat(
            mybarometer.pressure_msl_corrected,
            "[GENERAL] air pressure conversion error; Function: checking_level_3()");
    if (parsedAirPressureMsl != null) {
      float_air_pressure_msl_corrected = parsedAirPressureMsl;
      air_pressure_conversion_ok = true;
    } else {
      air_pressure_conversion_ok = false;
    }

    main_support.FloatValidation pressureTendencyValidation =
        support.validateFloat(
            mybarograph.pressure_amount_tendency,
            "amount air pressure tendency conversion error",
            "checking_level_3");
    float_amount_pressure_tendency = pressureTendencyValidation.value();
    amount_pressure_tendency_conversion_ok = pressureTendencyValidation.valid();

    // string SST to float
    Float parsedSeaWaterTemperature =
        support.parseValidationFloat(
            mytemp.sea_water_temp, "[GENERAL] SST conversion error; Function: checking_level_3()");
    if (parsedSeaWaterTemperature != null) {
      float_sea_water_temp = parsedSeaWaterTemperature;
      sea_water_temp_conversion_ok = true;
    } else {
      sea_water_temp_conversion_ok = false;
    }

    // string thickness ice accretion (EsEs) to float
    Float parsedIceThickness =
        support.parseValidationFloat(
            myicing.EsEs_code,
            "[GENERAL] ice thickness (EsEs) conversion error; Function: checking_level_3()");
    if (parsedIceThickness != null) {
      float_ice_thickness = parsedIceThickness; // EsEs_code = ice thickness in centimetres
      ice_thickness_conversion_ok = true;
    } else {
      ice_thickness_conversion_ok = false;
    }

    main_support.IntegerValidation presentWeatherValidation =
        support.validateInteger(
            mypresentweather.ww_code, "ww conversion error", "checking_level_3");
    int_ww_code = presentWeatherValidation.value();
    ww_code_conversion_ok = presentWeatherValidation.valid();

    //
    ///////////////////////////// checks //////////////////////////
    //

    // speed ship
    //
    if (doorgaan && !ShipSpeedConfirmationValidation.validate(myposition.vs_code)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // wind speed
    //
    if (doorgaan
        && !WindSpeedConfirmationValidation.validate(main.wind_units, mywind.int_true_wind_speed)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // wind waves period
    //
    if (doorgaan
        && !WindWaveConfirmationValidation.validate(
            wind_waves_period_conversion_ok,
            float_wind_waves_period,
            wind_waves_height_conversion_ok,
            float_wind_waves_height)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // first swell period
    //
    if (doorgaan
        && !SwellConfirmationValidation.validate(
            first_swell_period_conversion_ok,
            float_first_swell_period,
            first_swell_height_conversion_ok,
            float_first_swell_height,
            second_swell_period_conversion_ok,
            float_second_swell_period,
            second_swell_height_conversion_ok,
            float_second_swell_height)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // wind speed <--> wand waves height
    //
    if (doorgaan
        && !WindWaveConsistencyValidation.validate(
            main.wind_units,
            mywind.int_true_wind_speed,
            wind_waves_height_conversion_ok,
            float_wind_waves_height)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // air temp
    //
    if (doorgaan
        && !AirTemperatureConfirmationValidation.validate(air_temp_conversion_ok, float_air_temp)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // icing <-> air temperature
    //
    if (doorgaan
        && !IcingAirTemperatureValidation.confirmLevelThree(
            air_temp_conversion_ok,
            float_air_temp,
            !myicing.Is_code.equals("")
                || !myicing.EsEs_code.equals("")
                || !myicing.Rs_code.equals(""))) {
      doorgaan = false;
      level_3_ok = false;
    }

    // air pressure (MSL)
    //
    if (doorgaan
        && !AirPressureConfirmationValidation.validate(
            air_pressure_conversion_ok, float_air_pressure_msl_corrected)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // amount pressure tendency
    //
    if (doorgaan
        && !PressureTendencyConfirmationValidation.validateAmount(
            amount_pressure_tendency_conversion_ok, float_amount_pressure_tendency)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // amount pressure tendency <-> characteristic pressure tendency (a)
    //
    if (doorgaan
        && !PressureTendencyConfirmationValidation.validateCharacteristic(
            amount_pressure_tendency_conversion_ok,
            float_amount_pressure_tendency,
            mybarograph.a_code.equals(""))) {
      doorgaan = false;
      level_3_ok = false;
    }

    // SST
    //
    if (doorgaan
        && !SeaWaterTemperatureConfirmationValidation.validate(
            sea_water_temp_conversion_ok, float_sea_water_temp)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // thickness ice accretion (EsEs)
    //
    if (doorgaan
        && !IceThicknessConfirmationValidation.validate(
            ice_thickness_conversion_ok, float_ice_thickness)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // present weather <-> icing
    //
    if (doorgaan
        && !PresentWeatherIcingValidation.validate(
            ww_code_conversion_ok,
            int_ww_code,
            myicing.Is_code.equals("")
                && myicing.EsEs_code.equals("")
                && myicing.Rs_code.equals(""))) {
      level_3_ok = false;
      doorgaan = false;
    }

    return level_3_ok;
  }
}
