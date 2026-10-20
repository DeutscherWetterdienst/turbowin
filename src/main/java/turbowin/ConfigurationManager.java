package turbowin;

import static turbowin.main.*;

/** Builds the legacy configuration-line representation from the application's settings. */
final class ConfigurationManager {

  private ConfigurationManager() {}

  static void fillConfigurationLines() {
    String[] configuratie_regels = main.configuratie_regels;
    configuratie_regels[0] = main.SHIP_NAME_TXT + main.ship_name.trim();
    configuratie_regels[1] = main.IMO_NUMBER_TXT + main.imo_number.trim();
    // Legacy fields retained for configuration-file compatibility; their values are not written.
    configuratie_regels[2] = main.CALL_SIGN_TXT;
    configuratie_regels[3] = main.MASKED_CALL_SIGN_TXT;
    configuratie_regels[4] = main.TIME_ZONE_COMPUTER_TXT + main.time_zone_computer.trim();
    configuratie_regels[5] = main.RECRUITING_COUNTRY_TXT + main.recruiting_country.trim();
    configuratie_regels[6] = main.METHOD_WAVES_TXT + main.method_waves.trim();
    configuratie_regels[7] = main.WIND_SOURCE_TXT + main.wind_source.trim();
    configuratie_regels[8] = main.BAROMETER_ABOVE_SLL_TXT + main.barometer_above_sll.trim();
    configuratie_regels[9] = main.BAROMETER_KEEL_TO_SLL_TXT + main.keel_sll.trim();
    configuratie_regels[10] =
        main.PRESSURE_READING_MSL_TXT + main.pressure_reading_msl_yes_no.trim();
    configuratie_regels[11] = main.AIR_TEMP_EXPOSURE_TXT + main.air_temp_exposure.trim();
    configuratie_regels[12] = main.SST_EXPOSURE_TXT + main.sst_exposure.trim();
    configuratie_regels[13] = main.MAX_HEIGHT_DECK_CARGO_TXT + main.max_height_deck_cargo.trim();
    configuratie_regels[14] = main.DIFF_SLL_WL_TXT + main.diff_sll_wl.trim();
    configuratie_regels[15] = main.OBS_EMAIL_RECIPIENT_TXT + main.obs_email_recipient;
    configuratie_regels[16] = main.OBS_EMAIL_SUBJECT_TXT + main.obs_email_subject;
    configuratie_regels[17] = main.LOGS_DIR_TXT + main.logs_dir;
    configuratie_regels[18] = main.LOGS_EMAIL_RECIPIENT_TXT + main.logs_email_recipient;
    configuratie_regels[19] = main.WIND_UNITS_TXT + main.wind_units.trim();
    configuratie_regels[20] = main.RS232_INSTRUMENT_TYPE_TXT + main.RS232_connection_mode;
    configuratie_regels[21] = main.RS232_BITS_PER_SEC_TXT + main.bits_per_second;
    configuratie_regels[22] = main.RS232_DATA_BITS_TXT + main.data_bits;
    configuratie_regels[23] = main.RS232_PARITY_TXT + main.parity;
    configuratie_regels[24] = main.RS232_STOP_BITS_TXT + main.stop_bits;
    configuratie_regels[25] = main.RS232_PREFERED_COM_PORT_TXT + main.prefered_COM_port_number;
    configuratie_regels[26] = main.IC_BAROMETER_TXT + main.barometer_instrument_correction;
    configuratie_regels[27] = main.OBS_FORMAT_TXT + main.obs_format.trim();
    configuratie_regels[28] = main.FORMAT_101_ENCRYPTION_TXT + main.obs_101_encryption.trim();
    configuratie_regels[29] = main.FORMAT_101_EMAIL_TXT + main.obs_101_email.trim();
    configuratie_regels[30] =
        main.RS232_PREF_COM_PORT_NAME_TXT
            + main.prefered_COM_port_name; // not in use from version 3.4
    configuratie_regels[31] = main.WOW_PUBLISH_TXT + String.valueOf(main.WOW); // boolean
    configuratie_regels[32] = main.WOW_SITE_ID_TXT + main.WOW_site_id;
    configuratie_regels[33] = main.WOW_PIN_TXT + main.WOW_site_pin;
    configuratie_regels[34] = main.WOW_REPORTING_INTERVAL_TXT + main.WOW_reporting_interval;
    configuratie_regels[35] = main.WOW_APR_AVERAGE_DRAUGHT_TXT + main.WOW_APR_average_draught;
    configuratie_regels[36] = main.AMOS_MAIL_TXT + String.valueOf(main.amos_mail); // boolean
    configuratie_regels[37] = main.RS232_GPS_TYPE_TXT + main.RS232_GPS_connection_mode;
    configuratie_regels[38] = main.RS232_GPS_BITS_PER_SEC_TXT + main.GPS_bits_per_second;
    configuratie_regels[39] = main.RS232_GPS_COM_PORT_TXT + main.prefered_GPS_COM_port_number;
    configuratie_regels[40] = main.RS232_GPS_COM_PORT_NAME_TXT + main.prefered_GPS_COM_port_name;
    configuratie_regels[41] = main.RS232_GPS_SENTENCE_TXT + main.RS232_GPS_sentence; // integer
    configuratie_regels[42] = main.APR_TXT + String.valueOf(main.APR); // boolean
    configuratie_regels[43] = main.APR_REPORTING_INTERVAL_TXT + main.APR_reporting_interval;
    configuratie_regels[44] = main.UPLOAD_URL_TXT + main.upload_URL;
    configuratie_regels[45] = main.AWSR_TXT + String.valueOf(main.AWSR); // boolean
    configuratie_regels[46] = main.AWSR_REPORTING_INTERVAL_TXT + main.AWSR_reporting_interval;
    configuratie_regels[47] = main.WIND_UNITS_DASHBOARD_TXT + main.wind_units_dashboard.trim();
    configuratie_regels[48] = main.SHIP_TYPE_DASHBOARD_TXT + main.ship_type_dashboard;
    configuratie_regels[49] = main.HEIGHT_ANEMOMETER_TXT + main.height_anemometer;
    configuratie_regels[50] = main.GUI_MODE_TXT + main.GUI_mode;
    configuratie_regels[51] = main.GUI_LOGO_TXT + main.GUI_logo;
    configuratie_regels[52] = main.OBS_EMAIL_CC_TXT + main.obs_email_cc;
    configuratie_regels[53] = main.LOCAL_EMAIL_SERVER_TXT + main.local_email_server;
    configuratie_regels[54] = main.YOUR_GMAIL_ADDRESS_TXT + main.your_gmail_address;
    configuratie_regels[55] = main.GMAIL_APP_PASSWORD_TXT + main.gmail_app_password;
    configuratie_regels[56] = main.GMAIL_SECURITY_TXT + main.gmail_security;
    configuratie_regels[57] = main.YOUR_YAHOO_ADDRESS_TXT + main.your_yahoo_address;
    configuratie_regels[58] = main.YAHOO_APP_PASSWORD_TXT + main.yahoo_app_password;
    configuratie_regels[59] = main.YAHOO_SECURITY_TXT + main.yahoo_security;
    configuratie_regels[60] = main.YOUR_SHIP_ADDRESS_TXT + main.your_ship_address;
    configuratie_regels[61] = main.SMTP_HOST_PASSWORD_TXT + main.smtp_host_password;
    configuratie_regels[62] = main.SMTP_HOST_PORT_TXT + main.smtp_host_port;
    configuratie_regels[63] = main.RS232_INSTRUMENT_TYPE_TXT_II + main.RS232_connection_mode_II;
    configuratie_regels[64] = main.RS232_BITS_PER_SEC_TXT_II + main.bits_per_second_II;
    configuratie_regels[65] = main.RS232_DATA_BITS_TXT_II + main.data_bits_II;
    configuratie_regels[66] = main.RS232_PARITY_TXT_II + main.parity_II;
    configuratie_regels[67] = main.RS232_STOP_BITS_TXT_II + main.stop_bits_II;
    configuratie_regels[68] =
        main.RS232_PREFERED_COM_PORT_TXT_II + main.prefered_COM_port_number_II;
    configuratie_regels[69] = main.APTR_AWSR_SEND_METHOD_TXT + main.APTR_AWSR_send_method;
    configuratie_regels[70] = main.STATION_ID_TXT + main.station_ID;
    configuratie_regels[71] = main.DASHBOARD_BACKGROUND_IMAGE_TXT + main.dashboard_background_image;
    configuratie_regels[72] = main.YOUR_CUSTOM_ADDRESS_TXT + main.your_custom_address;
    configuratie_regels[73] = main.CUSTOM_EMAIL_SERVER_TXT + main.custom_email_server;
    configuratie_regels[74] = main.CUSTOM_PASSWORD_TXT + main.custom_password;
    configuratie_regels[75] = main.CUSTOM_SECURITY_TXT + main.custom_security;
    configuratie_regels[76] = main.CUSTOM_PORT_TXT + main.custom_port;
    configuratie_regels[77] = main.POP_UP_DASHBOARD_TXT + String.valueOf(main.pop_up_screen);
    configuratie_regels[78] = main.POP_UP_DASHBOARD_INTERVAL_TXT + main.pop_up_screen_interval;
    configuratie_regels[79] = main.DASHBOARD_SHIP_DECK_COLOR_TXT + main.ship_deck_color_String;
    configuratie_regels[80] = main.CUSTOM_EMAIL_MODULE_TXT + main.custom_email_module;
    configuratie_regels[81] = main.LOGS_EMAIL_TXT + main.log_files_email_send_method;
    configuratie_regels[82] = main.EUCAWS_UPLOADS_METHOD_TXT + main.eucaws_uploads_method;
    configuratie_regels[83] = main.PORT_MODE_OPTION_TXT + main.port_mode_option;
    configuratie_regels[84] = main.LAN_IP_ADDRESS_TXT + main.lan_ip_address;
    configuratie_regels[85] = main.DASHBOARD_SHIP_TANK_COLOR_TXT + main.ship_tank_color_String;
    configuratie_regels[86] =
        main.DASHBOARD_FONT_TXT + main.dashboard_font; // for hybrid dashboard
    configuratie_regels[87] =
        main.COM_PROTOCOL_TXT + main.server_com_protocol; // HTTPS / HTTP
    configuratie_regels[88] =
        main.EUCAWS_OBS_ID_TXT + String.valueOf(main.eucaws_obs_id); // boolean
  }

  static void applyConfigurationLines() {
    // called from:
    //   - import_button_actionPerformed() [tmystationdata.java]
    //   - lees_configuratie_regels() [main.java]
    //   - Maintenance_Import_maintenance_data_actionPerformed() [main.java]

    /* put collected meta data from muffin (or configuration file if read muffin failed) into appropriate global vars */
    for (int teller = 0; teller < MAX_AANTAL_CONFIGURATIEREGELS; teller++) {
      if ((configuratie_regels[teller] != null)
          && (configuratie_regels[teller].compareTo("") != 0)) {
        // ship name
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], SHIP_NAME_TXT)) {
          // zo ja, dan staat op een bepaalde pos (achter de : ) de inhoud
          ship_name = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // imo number
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], IMO_NUMBER_TXT)) {
          imo_number = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // call sign
        // if (configuratie_regels[teller].indexOf(CALL_SIGN_TXT) != -1)
        // {
        //   call_sign = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        // }

        // masked call sign
        // if (configuratie_regels[teller].indexOf(MASKED_CALL_SIGN_TXT) != -1)
        // {
        //   masked_call_sign =
        // configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        // }

        // time zone computer
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], TIME_ZONE_COMPUTER_TXT)) {
          time_zone_computer = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // recruiting country
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RECRUITING_COUNTRY_TXT)) {
          recruiting_country = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // method determining waves
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], METHOD_WAVES_TXT)) {
          method_waves = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // wind meta data
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], WIND_SOURCE_TXT)) {
          wind_source = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], MAX_HEIGHT_DECK_CARGO_TXT)) {
          max_height_deck_cargo = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], DIFF_SLL_WL_TXT)) {
          diff_sll_wl = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // air pressure meta data
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], BAROMETER_ABOVE_SLL_TXT)) {
          barometer_above_sll = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], BAROMETER_KEEL_TO_SLL_TXT)) {
          keel_sll = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], PRESSURE_READING_MSL_TXT)) {
          pressure_reading_msl_yes_no = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // air temp exposure
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], AIR_TEMP_EXPOSURE_TXT)) {
          air_temp_exposure = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // sst exposure
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], SST_EXPOSURE_TXT)) {
          sst_exposure = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // Obs E-mail recipient
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], OBS_EMAIL_RECIPIENT_TXT)) {
          obs_email_recipient = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // Obs E-mail subject
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], OBS_EMAIL_SUBJECT_TXT)) {
          obs_email_subject = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // logs dir (for immt.log etc.)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], LOGS_DIR_TXT)) {
          logs_dir = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // logs E-mail recipient
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], LOGS_EMAIL_RECIPIENT_TXT)) {
          logs_email_recipient = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // wind units (knots or m/s)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], WIND_UNITS_TXT)) {
          wind_units = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // RS232 INSTRUMENT connection mode (= RS232 connected instrument type -none, PTB220,
        // PTB330, SAWS-)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_INSTRUMENT_TYPE_TXT)) {
          RS232_connection_mode =
              ConfigurationValueParser.integer(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 INSTRUMENT bits per sec
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_BITS_PER_SEC_TXT)) {
          bits_per_second =
              ConfigurationValueParser.integer(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 INSTRUMENT data bits
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], RS232_DATA_BITS_TXT)) {
          data_bits =
              SerialConfigurationValueParser.dataBits(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 INSTRUMENT parity
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], RS232_PARITY_TXT)) {
          parity =
              SerialConfigurationValueParser.parity(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 INSTRUMENT stop bits
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], RS232_STOP_BITS_TXT)) {
          stop_bits =
              SerialConfigurationValueParser.stopBits(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 INSTRUMENT prefered COM port (Windows and Linux)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_PREFERED_COM_PORT_TXT)) {
          prefered_COM_port_number = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // ic (instrument correction) barometer
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], IC_BAROMETER_TXT)) {
          barometer_instrument_correction =
              ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // obs format (FM13 or format 101)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], OBS_FORMAT_TXT)) {
          obs_format = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // format 101 call sign encryption (yes or no)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], FORMAT_101_ENCRYPTION_TXT)) {
          obs_101_encryption = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // format 101 email (body or attachement)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], FORMAT_101_EMAIL_TXT)) {
          obs_101_email = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // RS232 INSTRUMENT prefered COM port name (OS X)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_PREF_COM_PORT_NAME_TXT)) {
          prefered_COM_port_name = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // WOW publish?
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], WOW_PUBLISH_TXT)) {
          WOW =
              ConfigurationValueParser.bool(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in WOW settings
        }

        // WOW site id
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], WOW_SITE_ID_TXT)) {
          WOW_site_id = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // WOW pin
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], WOW_PIN_TXT)) {
          WOW_site_pin = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // WOW reporting(upload) interval
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], WOW_REPORTING_INTERVAL_TXT)) {
          WOW_reporting_interval = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // WOW/APR average draught
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], WOW_APR_AVERAGE_DRAUGHT_TXT)) {
          WOW_APR_average_draught = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // default E-mail program on this computer is AMOS Mail?
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], AMOS_MAIL_TXT)) {
          amos_mail =
              ConfigurationValueParser.bool(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in email settings
        }

        // RS232 GPS connection mode
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], RS232_GPS_TYPE_TXT)) {
          RS232_GPS_connection_mode =
              ConfigurationValueParser.integer(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 GPS bits per second
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_GPS_BITS_PER_SEC_TXT)) {
          GPS_bits_per_second =
              ConfigurationValueParser.integer(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 prefered GPS COM port (Windows and Linux)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_GPS_COM_PORT_TXT)) {
          prefered_GPS_COM_port_number = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // RS232 prefered GPS COM port (OS X)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_GPS_COM_PORT_NAME_TXT)) {
          prefered_GPS_COM_port_name = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // RS232 GPS sentence (RMC or GGA)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_GPS_SENTENCE_TXT)) {
          RS232_GPS_sentence =
              ConfigurationValueParser.integer(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // APR (Automated Pressure Reports)?
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], APR_TXT)) {
          APR =
              ConfigurationValueParser.bool(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in APR settings
        }

        // APR reporting(upload) interval
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], APR_REPORTING_INTERVAL_TXT)) {
          APR_reporting_interval = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // upload URL
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], UPLOAD_URL_TXT)) {
          upload_URL = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // AWSR (Automatic Weather Station Reports)?
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], AWSR_TXT)) {
          AWSR =
              ConfigurationValueParser.bool(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in AWSR settings
        }

        // AWSR reporting(upload) interval
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], AWSR_REPORTING_INTERVAL_TXT)) {
          AWSR_reporting_interval = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // wind units graps/dasboard
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], WIND_UNITS_DASHBOARD_TXT)) {
          wind_units_dashboard = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // ship type for visual dashboard
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], SHIP_TYPE_DASHBOARD_TXT)) {
          ship_type_dashboard = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // anemometer height above WL
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], HEIGHT_ANEMOMETER_TXT)) {
          height_anemometer = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // GUI mode (light/full)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], GUI_MODE_TXT)) {
          GUI_mode = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // GUI logo (EUMETNET/NOAAA/SOT)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], GUI_LOGO_TXT)) {
          GUI_logo = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // obs email cc
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], OBS_EMAIL_CC_TXT)) {
          obs_email_cc = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // RS232 2nd meteo instrument connection mode (= RS232 connected instrument II type: none,
        // StarX, HMP155)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_INSTRUMENT_TYPE_TXT_II)) {
          RS232_connection_mode_II =
              ConfigurationValueParser.integer(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 2nd meteo instrument bits per sec
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_BITS_PER_SEC_TXT_II)) {
          bits_per_second_II =
              ConfigurationValueParser.integer(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 2nd meteo instrument data bits
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_DATA_BITS_TXT_II)) {
          data_bits_II =
              SerialConfigurationValueParser.dataBits(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 2nd meteo instrument parity
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], RS232_PARITY_TXT_II)) {
          parity_II =
              SerialConfigurationValueParser.parity(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 2nd imeteo instrument stop bits
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_STOP_BITS_TXT_II)) {
          stop_bits_II =
              SerialConfigurationValueParser.stopBits(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
        }

        // RS232 2nd meteo instrument prefered COM port (Windows and Linux)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], RS232_PREFERED_COM_PORT_TXT_II)) {
          prefered_COM_port_number_II = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // AP[&T]R / AWSR send method
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], APTR_AWSR_SEND_METHOD_TXT)) {
          APTR_AWSR_send_method = ConfigurationLineParser.value(configuratie_regels[teller]);

          APTR_AWSR_send_method = AptrAwsrSendMethodNormalizer.normalize(APTR_AWSR_send_method);
        }

        // station ID
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], STATION_ID_TXT)) {
          station_ID = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // dashboard background image
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], DASHBOARD_BACKGROUND_IMAGE_TXT)) {
          dashboard_background_image = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // your custom email address
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], YOUR_CUSTOM_ADDRESS_TXT)) {
          your_custom_address = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // custom email server
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], CUSTOM_EMAIL_SERVER_TXT)) {
          custom_email_server = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // custom email password
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], CUSTOM_PASSWORD_TXT)) {
          custom_password = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // custom email security
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], CUSTOM_SECURITY_TXT)) {
          custom_security = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // custom email port
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], CUSTOM_PORT_TXT)) {
          custom_port = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // pop-up dashboard? (true/false)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], POP_UP_DASHBOARD_TXT)) {
          pop_up_screen =
              ConfigurationValueParser.bool(
                  configuratie_regels[teller].substring(
                      CONFIGURATION_FILE_POS_INHOUD)); // pop_up_dashboard =
          // Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in APR settings
        }

        // pop-up dashboard interval
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], POP_UP_DASHBOARD_INTERVAL_TXT)) {
          pop_up_screen_interval =
              configuratie_regels[teller].substring(
                  CONFIGURATION_FILE_POS_INHOUD); // pop_up_dashboard_interval =
          // configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // dashboard ship deck color
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], DASHBOARD_SHIP_DECK_COLOR_TXT)) {
          ship_deck_color_String = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // custom email module (primary-jakarta, secondary-python)
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], CUSTOM_EMAIL_MODULE_TXT)) {
          custom_email_module = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // log files email send method (default or custom)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], LOGS_EMAIL_TXT)) {
          log_files_email_send_method = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // eucaws upload/send method
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], EUCAWS_UPLOADS_METHOD_TXT)) {
          eucaws_uploads_method = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // port mode option. deactivate APR/AWSR if ship speed is minimal; (< 1 knot / < 0.5 knot)
        // [true/false]
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], PORT_MODE_OPTION_TXT)) {
          port_mode_option =
              ConfigurationValueParser.bool(
                  ConfigurationLineParser.value(configuratie_regels[teller]));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in APR/AWSR settings
        }

        // listening LAN IP address
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], LAN_IP_ADDRESS_TXT)) {
          lan_ip_address = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // dashboard ship (LNG)tank color
        if (ConfigurationLineMatcher.contains(
            configuratie_regels[teller], DASHBOARD_SHIP_TANK_COLOR_TXT)) {
          ship_tank_color_String = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // dashboard font (hybrid dashboard)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], DASHBOARD_FONT_TXT)) {
          dashboard_font = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // communication protocol client <-> server
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], COM_PROTOCOL_TXT)) {
          server_com_protocol = ConfigurationLineParser.value(configuratie_regels[teller]);
        }

        // EUCAWS OBS_ID (intials added to the SMD input)
        if (ConfigurationLineMatcher.contains(configuratie_regels[teller], EUCAWS_OBS_ID_TXT)) {
          eucaws_obs_id =
              Boolean.parseBoolean(ConfigurationLineParser.value(configuratie_regels[teller]));
        }
      } // if ((configuratie_regels[teller] != null) etc.
    } // for (teller = 0; teller < MAX_AANTAL_CONFIGURATIEREGELS; teller++)

    // 1st meteo instrument: generic name "prefered_COM_port" will be used [main_RS232_RS422.java]
    //
    if (prefered_COM_port_number
        .trim()
        .equals("")) // So no Windows or Linux com port number selected
    {
      prefered_COM_port = prefered_COM_port_name; // OS X
    } else {
      prefered_COM_port = prefered_COM_port_number; // Windows and Linux
    }

    // GPS: generic name "prefered_GPS_COM_port" will be used [main_RS232_RS422.java]
    //
    if (prefered_GPS_COM_port_number
        .trim()
        .equals("")) // So no Windows or Linux GPS com port number selected
    {
      prefered_GPS_COM_port = prefered_GPS_COM_port_name; // OS X
    } else {
      prefered_GPS_COM_port = prefered_GPS_COM_port_number; // Windows and Linux
    }

    // 2nd meteo instrument: generic name "prefered_COM_port_II" will be used
    // [main_RS232_RS422.java]
    //
    if (prefered_COM_port_number_II
        .trim()
        .equals("")) // So no Windows or Linux com port number selected
    {
      // prefered_COM_port_II = prefered_COM_port_name_II;                     // OS X
    } else {
      prefered_COM_port_II = prefered_COM_port_number_II; // Windows and Linux
    }
  }
}
