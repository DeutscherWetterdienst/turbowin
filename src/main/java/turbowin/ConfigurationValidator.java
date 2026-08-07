package turbowin;

import static turbowin.main.*;

/** Validates the application metadata and returns the first warning message. */
final class ConfigurationValidator {

  private ConfigurationValidator() {}

  static String findWarning() {
    // called from: - read_muffin() [main.java]
    //              - lees_configuratie_regels() [main.java]
    //

    String info = "";

    // on start up
    if (ship_name.trim().equals("") == true || ship_name.trim().length() < 2) {
      info = "Ship name: unknown (select: Maintenance -> Station data)";
    }
    // else if (imo_number.trim().equals("") == true || imo_number.trim().length() < 2)
    // {
    //   info = "IMO number: unknown (select: Maintenance -> Station data)";
    // }
    // else if (call_sign.trim().equals("") == true || call_sign.trim().length() < 2)
    // {
    //   info = "Call sign: unknown (select: Maintenance -> Station data)";
    // }

    else if (station_ID.trim().equals("") == true || station_ID.trim().length() < 2) {
      info = "station ID: unknown (select: Maintenance -> Station data)";
    }

    // else if (time_zone_computer.trim().equals("") == true || time_zone_computer.trim().length() <
    // 2)
    // {
    //   info = "Time zone computer: unknown (select: Maintenance -> Station data)";
    // }
    else if (recruiting_country.trim().equals("") == true
        || recruiting_country.trim().length() < 2) {
      info = "Recruiting country: unknown (select: Maintenance -> Station data)";
    } else if (wind_source.trim().equals("") == true || wind_source.trim().length() < 2) {
      info = "Wind source (measured/estimated): unknown (select: Maintenance -> Station data)";
    } else if (max_height_deck_cargo.trim().equals("") == true
        || max_height_deck_cargo.trim().length() < 1) {
      info = "Maximum height deck cargo: unknown (select: Maintenance -> Station data)";
    } else if (diff_sll_wl.trim().equals("") == true || diff_sll_wl.trim().length() < 1) {
      info = "Difference SLL and water line: unknown (select: Maintenance -> Station data)";
    } else if (pressure_reading_msl_yes_no.trim().equals("") == true
        || pressure_reading_msl_yes_no.trim().length() < 2) {
      info = "Air pressure reading indication: unknown (select: Maintenance -> Station data)";
    } else if (barometer_above_sll.trim().equals("") == true
        || barometer_above_sll.trim().length() < 1) {
      info = "Height of the barometer above SLL: unknown (select: Maintenance -> Station data)";
    } else if (keel_sll.trim().equals("") == true || keel_sll.trim().length() < 1) {
      info = "Distance of bottom of the keel to SLL: unknown (select: Maintenance -> Station data)";
    } else if (air_temp_exposure.trim().equals("") == true
        || air_temp_exposure.trim().length() < 2) {
      info = "Air temp exposure: unknown (select: Maintenance -> Station data)";
    } else if (sst_exposure.trim().equals("") == true || sst_exposure.trim().length() < 2) {
      info = "Sea water temp exposure: unknown (select: Maintenance -> Station data)";
    } else if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2) {
      info = "Logs folder unknown (select: Maintenance -> Log files settings)";
    } else if (obs_format.trim().equals("") == true || obs_format.trim().length() < 2) {
      info = "obs format unknown (select: Maintenance -> Obs format setting)";
    }
    //
    // NB no warning message if obs_email_recipient, obs_email_subject or logs_email_recipient is
    // unknown

    // extra check on combination obs format AWS and AWS connected
    if ((main.obs_format.equals(main.FORMAT_AWS) == true)
        && (main.RS232_connection_mode != 3
            && RS232_connection_mode != 9
            && RS232_connection_mode != 10
            && RS232_connection_mode
                != 11)) // RS232_connection_mode = 3 or 9 or 10 or 11 = AWS connected
    {
      info =
          "if obs format = \"AWS connected\" (see Maintenance -> Obs format setting) then set also the AWS connection (select: Maintenance -> Serial/USB/LAN connection settings)";
    }

    // extra check 'wind speed units estimated/measured' and AWS connected
    // if ((wind_units.indexOf(M_S) == -1) && ((RS232_connection_mode == 3) ||
    // (RS232_connection_mode == 9) || (RS232_connection_mode == 10) || RS232_connection_mode ==
    // 11))
    // {
    //   info = "If AWS connected the \"wind speed units estimated/measured\" will always be
    // \"m/s\"\n";
    //   info += "Please correct this in Maintenance -> Station data";
    // }

    // extra check 'wind source and AWS connected
    // if ((wind_source.equals(main.MEASURED_OFF_BOW) == false) && ((RS232_connection_mode == 3) ||
    // (RS232_connection_mode == 9) || (RS232_connection_mode == 10) || RS232_connection_mode ==
    // 11))
    // {
    //   info = "If AWS connected the \'wind source\" will always be \"measured speed + app. dir.
    // (OFF THE BOW, clockwise)\"\n";
    //   info += "Please correct this in Maintenance -> Station data";
    // }

    // extra check 'air pressure station level' and AWS or barometer connected
    if ((main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_YES) == true)
        && (main.RS232_connection_mode != 0)) // barometer or AWS connected
    {
      info =
          "If AWS or barometer connected (Maintenance -> Serial/USB/LAN connection settings): \"the reading does not indicate the MSL pressure\"\n";
      info += "Please correct this in Maintenance -> Station data";
    }

    return info;
  }
}
