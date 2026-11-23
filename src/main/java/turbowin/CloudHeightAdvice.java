package turbowin;

/** Legacy advice for the height of the lowest cloud base. */
final class CloudHeightAdvice {
  private CloudHeightAdvice() {}

  static int compute() {
    int h_advies = main.INVALID;
    int local_int_Cl;
    int local_int_Cm;
    int local_int_Ch;
    int local_int_ww_code;
    int local_int_latitude_degrees;
    double local_double_air_temp;
    double local_double_dew_point;

    // Cl_code to int (code)
    //
    if (LowCloudState.cl_code.trim().length() == 0) {
      local_int_Cl = main.INVALID;
    } else {
      try {
        local_int_Cl = Integer.parseInt(LowCloudState.cl_code.trim());
      } catch (NumberFormatException e) {
        local_int_Cl = main.INVALID;
      }
    } // else

    // Cm_code to int (code)
    //
    if (MiddleCloudState.cm_code.trim().length() == 0) {
      local_int_Cm = main.INVALID;
    } else {
      try {
        local_int_Cm = Integer.parseInt(MiddleCloudState.cm_code.trim());
      } catch (NumberFormatException e) {
        local_int_Cm = main.INVALID;
      }
    } // else

    // Ch_code to int (code)
    //
    if (HighCloudState.ch_code.trim().length() == 0) {
      local_int_Ch = main.INVALID;
    } else {
      try {
        local_int_Ch = Integer.parseInt(HighCloudState.ch_code.trim());
      } catch (NumberFormatException e) {
        local_int_Ch = main.INVALID;
      }
    } // else

    // air temp to double (degrees en tenths)
    //
    if (TemperatureModel.air_temp.trim().length() == 0) {
      local_double_air_temp = main.INVALID;
    } else {
      try {
        local_double_air_temp = Double.parseDouble(TemperatureModel.air_temp.trim());
      } catch (NumberFormatException e) {
        local_double_air_temp = main.INVALID;
      }
    } // else

    // dew point (degrees en tenths)
    //
    local_double_dew_point = TemperatureModel.double_dew_point; // kan INVALID zijn

    // latitude (whole degrees)
    local_int_latitude_degrees = PositionModel.int_latitude_degrees;

    // ww
    try {
      local_int_ww_code = Integer.parseInt(PresentWeatherState.ww_code.trim());
    } catch (NumberFormatException e) {
      local_int_ww_code = main.INVALID;
    }

    switch (local_int_Cl) {
      case 1:
      case 2:
        if (local_double_air_temp != main.INVALID && local_double_dew_point != main.INVALID) {
          double hoogte = (local_double_air_temp - local_double_dew_point) * 125;

          if (hoogte < 50.0) h_advies = 0; // 0 - 50 meter
          else if (hoogte >= 50.0 && hoogte < 100.0) h_advies = 1;
          else if (hoogte >= 100.0 && hoogte < 200.0) h_advies = 2;
          else if (hoogte >= 200.0 && hoogte < 300.0) h_advies = 3;
          else if (hoogte >= 300.0 && hoogte < 600.0) h_advies = 4;
          else if (hoogte >= 600.0 && hoogte < 1000.0) h_advies = 5;
          else if (hoogte >= 1000.0 && hoogte < 1500.0) h_advies = 6;
          else if (hoogte >= 1500.0 && hoogte < 2000.0) h_advies = 7;
          else if (hoogte >= 2000.0 && hoogte < 2500.0) h_advies = 8;
          // NB ort wolken dus dan geen advies verder
        } // if (local_tdry != MAXINT && local_tdew != MAXINT)
        break;
      case 3:
      case 4:
      case 5:
        if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 4;
        else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70) h_advies = 5;
        else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0) h_advies = 6;
        break;
      case 6:
      case 7:
        if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 1;
        else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70) h_advies = 2;
        else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0) h_advies = 3;
        break;
      case 8:
        if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 4;
        else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70) h_advies = 5;
        else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0) h_advies = 5;
        break;
      case 9:
        if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 4;
        else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70) h_advies = 5;
        else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0) h_advies = 6;
        break;
      default:
        break;
    } // switch (local_Cl)

    // geen Cl aanwezig dan kijken naar Cm /
    if (local_int_Cl == 0) {
      switch (local_int_Cm) {
        case 1:
          if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 8;
          else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70) h_advies = 9;
          else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0)
            h_advies = 9;
          break;
        case 2: // (continious) neerslag (rain, snow , ice pellets (zie bij main differences
          // Nimbostratus and similar clouds of other genera) /
          if (local_int_ww_code == 61
              || local_int_ww_code == 63
              || local_int_ww_code == 65
              || local_int_ww_code == 66
              || local_int_ww_code == 67
              || local_int_ww_code == 68
              || local_int_ww_code == 69
              || local_int_ww_code == 71
              || local_int_ww_code == 73
              || local_int_ww_code == 75
              || local_int_ww_code == 79) {
            if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 2;
            else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70)
              h_advies = 3;
            else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0)
              h_advies = 4;
          } // if (local_ww == 61 || local_ww == 63 || etc.
          else // dus geen (continous neerslag etc.) /
          {
            if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 6;
            else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70)
              h_advies = 7;
            else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0)
              h_advies = 8;
          } // else (geen cont neerslag etc.)
          break;
        case 3:
        case 4:
        case 5:
        case 6:
          if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 8;
          else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70) h_advies = 9;
          else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0)
            h_advies = 9;
          break;
        case 7:
          if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 6;
          else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70) h_advies = 7;
          else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0)
            h_advies = 8;
          break;
        case 8:
        case 9:
          if (local_int_latitude_degrees >= 70 && local_int_latitude_degrees <= 90) h_advies = 8;
          else if (local_int_latitude_degrees > 20 && local_int_latitude_degrees < 70) h_advies = 9;
          else if (local_int_latitude_degrees <= 20 && local_int_latitude_degrees >= 0)
            h_advies = 9;
          break;
        default:
          break;
      } // switch (local_int_Cm)
    } // if (local_int_Cl == 0)

    // N.B. WMO code h=9 (h_advies) geld voor cloudless EN wolken basis >= 2500 meter /
    // even hier als trucje bij cloudless h_advies = -9                               /

    // geen Cl en geen Cm aanwezig
    if (local_int_Cl == 0 && local_int_Cm == 0) {
      if (local_int_Ch >= 1 && local_int_Ch <= 9) // geldige Ch type
      h_advies = 9; // hoogte >= 2500 meter
    } // if (local_Cl == MAXINT && local_Cm == MAXINT)

    // cloudless
    if (local_int_Cl == 0 && local_int_Cm == 0 && local_int_Ch == 0)
      h_advies = -9; // cloudless (fictief codecijfer om cloudless even aan te geven

    return h_advies;
  }
}
