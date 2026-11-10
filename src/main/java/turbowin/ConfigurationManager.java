package turbowin;

import static turbowin.main.*;

/** Builds legacy configuration lines and stages parsed values before applying live settings. */
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
    configuratie_regels[86] = main.DASHBOARD_FONT_TXT + main.dashboard_font; // for hybrid dashboard
    configuratie_regels[87] = main.COM_PROTOCOL_TXT + main.server_com_protocol; // HTTPS / HTTP
    configuratie_regels[88] =
        main.EUCAWS_OBS_ID_TXT + String.valueOf(main.eucaws_obs_id); // boolean
  }

  static void applyConfigurationLines() {
    applyConfiguration(parseConfigurationLines(main.configuratie_regels));
  }

  static ConfigurationSettings parseConfigurationLines(String[] lines) {
    ConfigurationSettings settings = new ConfigurationSettings();
    for (int teller = 0; teller < Math.min(lines.length, MAX_AANTAL_CONFIGURATIEREGELS); teller++) {
      if ((lines[teller] != null) && (lines[teller].compareTo("") != 0)) {
        try {
          // ship name
          if (ConfigurationLineMatcher.contains(lines[teller], SHIP_NAME_TXT)) {
            // zo ja, dan staat op een bepaalde pos (achter de : ) de inhoud
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> ship_name = parsed);
          }

          // imo number
          if (ConfigurationLineMatcher.contains(lines[teller], IMO_NUMBER_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> imo_number = parsed);
          }

          // call sign
          // if (lines[teller].indexOf(CALL_SIGN_TXT) != -1)
          // {
          //   call_sign = lines[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
          // }

          // masked call sign
          // if (lines[teller].indexOf(MASKED_CALL_SIGN_TXT) != -1)
          // {
          //   masked_call_sign =
          // lines[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
          // }

          // time zone computer
          if (ConfigurationLineMatcher.contains(lines[teller], TIME_ZONE_COMPUTER_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> time_zone_computer = parsed);
          }

          // recruiting country
          if (ConfigurationLineMatcher.contains(lines[teller], RECRUITING_COUNTRY_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> recruiting_country = parsed);
          }

          // method determining waves
          if (ConfigurationLineMatcher.contains(lines[teller], METHOD_WAVES_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> method_waves = parsed);
          }

          // wind meta data
          if (ConfigurationLineMatcher.contains(lines[teller], WIND_SOURCE_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> wind_source = parsed);
          }

          if (ConfigurationLineMatcher.contains(lines[teller], MAX_HEIGHT_DECK_CARGO_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> max_height_deck_cargo = parsed);
          }

          if (ConfigurationLineMatcher.contains(lines[teller], DIFF_SLL_WL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> diff_sll_wl = parsed);
          }

          // air pressure meta data
          if (ConfigurationLineMatcher.contains(lines[teller], BAROMETER_ABOVE_SLL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> barometer_above_sll = parsed);
          }

          if (ConfigurationLineMatcher.contains(lines[teller], BAROMETER_KEEL_TO_SLL_TXT)) {
            settings.add(ConfigurationLineParser.value(lines[teller]), parsed -> keel_sll = parsed);
          }

          if (ConfigurationLineMatcher.contains(lines[teller], PRESSURE_READING_MSL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> pressure_reading_msl_yes_no = parsed);
          }

          // air temp exposure
          if (ConfigurationLineMatcher.contains(lines[teller], AIR_TEMP_EXPOSURE_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> air_temp_exposure = parsed);
          }

          // sst exposure
          if (ConfigurationLineMatcher.contains(lines[teller], SST_EXPOSURE_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> sst_exposure = parsed);
          }

          // Obs E-mail recipient
          if (ConfigurationLineMatcher.contains(lines[teller], OBS_EMAIL_RECIPIENT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> obs_email_recipient = parsed);
          }

          // Obs E-mail subject
          if (ConfigurationLineMatcher.contains(lines[teller], OBS_EMAIL_SUBJECT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> obs_email_subject = parsed);
          }

          // logs dir (for immt.log etc.)
          if (ConfigurationLineMatcher.contains(lines[teller], LOGS_DIR_TXT)) {
            settings.add(ConfigurationLineParser.value(lines[teller]), parsed -> logs_dir = parsed);
          }

          // logs E-mail recipient
          if (ConfigurationLineMatcher.contains(lines[teller], LOGS_EMAIL_RECIPIENT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> logs_email_recipient = parsed);
          }

          // wind units (knots or m/s)
          if (ConfigurationLineMatcher.contains(lines[teller], WIND_UNITS_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> wind_units = parsed);
          }

          // RS232 INSTRUMENT connection mode (= RS232 connected instrument type -none, PTB220,
          // PTB330, SAWS-)
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_INSTRUMENT_TYPE_TXT)) {
            settings.add(
                ConfigurationValueParser.integer(ConfigurationLineParser.value(lines[teller])),
                parsed -> RS232_connection_mode = parsed);
          }

          // RS232 INSTRUMENT bits per sec
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_BITS_PER_SEC_TXT)) {
            settings.add(
                ConfigurationValueParser.integer(ConfigurationLineParser.value(lines[teller])),
                parsed -> bits_per_second = parsed);
          }

          // RS232 INSTRUMENT data bits
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_DATA_BITS_TXT)) {
            settings.add(
                SerialConfigurationValueParser.dataBits(
                    ConfigurationLineParser.value(lines[teller])),
                parsed -> data_bits = parsed);
          }

          // RS232 INSTRUMENT parity
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_PARITY_TXT)) {
            settings.add(
                SerialConfigurationValueParser.parity(ConfigurationLineParser.value(lines[teller])),
                parsed -> parity = parsed);
          }

          // RS232 INSTRUMENT stop bits
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_STOP_BITS_TXT)) {
            settings.add(
                SerialConfigurationValueParser.stopBits(
                    ConfigurationLineParser.value(lines[teller])),
                parsed -> stop_bits = parsed);
          }

          // RS232 INSTRUMENT prefered COM port (Windows and Linux)
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_PREFERED_COM_PORT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> prefered_COM_port_number = parsed);
          }

          // ic (instrument correction) barometer
          if (ConfigurationLineMatcher.contains(lines[teller], IC_BAROMETER_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> barometer_instrument_correction = parsed);
          }

          // obs format (FM13 or format 101)
          if (ConfigurationLineMatcher.contains(lines[teller], OBS_FORMAT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> obs_format = parsed);
          }

          // format 101 call sign encryption (yes or no)
          if (ConfigurationLineMatcher.contains(lines[teller], FORMAT_101_ENCRYPTION_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> obs_101_encryption = parsed);
          }

          // format 101 email (body or attachement)
          if (ConfigurationLineMatcher.contains(lines[teller], FORMAT_101_EMAIL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> obs_101_email = parsed);
          }

          // RS232 INSTRUMENT prefered COM port name (OS X)
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_PREF_COM_PORT_NAME_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> prefered_COM_port_name = parsed);
          }

          // WOW publish?
          if (ConfigurationLineMatcher.contains(lines[teller], WOW_PUBLISH_TXT)) {
            settings.add(
                ConfigurationValueParser.bool(ConfigurationLineParser.value(lines[teller])),
                parsed -> WOW = parsed);
            // NB You have to be carefull when using Boolean.valueOf(string) or
            // Boolean.parseBoolean(string).
            //    The reason for this is that the methods will always return false if the String is
            // not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in WOW settings
          }

          // WOW site id
          if (ConfigurationLineMatcher.contains(lines[teller], WOW_SITE_ID_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> WOW_site_id = parsed);
          }

          // WOW pin
          if (ConfigurationLineMatcher.contains(lines[teller], WOW_PIN_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> WOW_site_pin = parsed);
          }

          // WOW reporting(upload) interval
          if (ConfigurationLineMatcher.contains(lines[teller], WOW_REPORTING_INTERVAL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> WOW_reporting_interval = parsed);
          }

          // WOW/APR average draught
          if (ConfigurationLineMatcher.contains(lines[teller], WOW_APR_AVERAGE_DRAUGHT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> WOW_APR_average_draught = parsed);
          }

          // default E-mail program on this computer is AMOS Mail?
          if (ConfigurationLineMatcher.contains(lines[teller], AMOS_MAIL_TXT)) {
            settings.add(
                ConfigurationValueParser.bool(ConfigurationLineParser.value(lines[teller])),
                parsed -> amos_mail = parsed);
            // NB You have to be carefull when using Boolean.valueOf(string) or
            // Boolean.parseBoolean(string).
            //    The reason for this is that the methods will always return false if the String is
            // not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in email settings
          }

          // RS232 GPS connection mode
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_GPS_TYPE_TXT)) {
            settings.add(
                ConfigurationValueParser.integer(ConfigurationLineParser.value(lines[teller])),
                parsed -> RS232_GPS_connection_mode = parsed);
          }

          // RS232 GPS bits per second
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_GPS_BITS_PER_SEC_TXT)) {
            settings.add(
                ConfigurationValueParser.integer(ConfigurationLineParser.value(lines[teller])),
                parsed -> GPS_bits_per_second = parsed);
          }

          // RS232 prefered GPS COM port (Windows and Linux)
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_GPS_COM_PORT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> prefered_GPS_COM_port_number = parsed);
          }

          // RS232 prefered GPS COM port (OS X)
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_GPS_COM_PORT_NAME_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> prefered_GPS_COM_port_name = parsed);
          }

          // RS232 GPS sentence (RMC or GGA)
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_GPS_SENTENCE_TXT)) {
            settings.add(
                ConfigurationValueParser.integer(ConfigurationLineParser.value(lines[teller])),
                parsed -> RS232_GPS_sentence = parsed);
          }

          // APR (Automated Pressure Reports)?
          if (ConfigurationLineMatcher.contains(lines[teller], APR_TXT)) {
            settings.add(
                ConfigurationValueParser.bool(ConfigurationLineParser.value(lines[teller])),
                parsed -> APR = parsed);
            // NB You have to be carefull when using Boolean.valueOf(string) or
            // Boolean.parseBoolean(string).
            //    The reason for this is that the methods will always return false if the String is
            // not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in APR settings
          }

          // APR reporting(upload) interval
          if (ConfigurationLineMatcher.contains(lines[teller], APR_REPORTING_INTERVAL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> APR_reporting_interval = parsed);
          }

          // upload URL
          if (ConfigurationLineMatcher.contains(lines[teller], UPLOAD_URL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> upload_URL = parsed);
          }

          // AWSR (Automatic Weather Station Reports)?
          if (ConfigurationLineMatcher.contains(lines[teller], AWSR_TXT)) {
            settings.add(
                ConfigurationValueParser.bool(ConfigurationLineParser.value(lines[teller])),
                parsed -> AWSR = parsed);
            // NB You have to be carefull when using Boolean.valueOf(string) or
            // Boolean.parseBoolean(string).
            //    The reason for this is that the methods will always return false if the String is
            // not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in AWSR settings
          }

          // AWSR reporting(upload) interval
          if (ConfigurationLineMatcher.contains(lines[teller], AWSR_REPORTING_INTERVAL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> AWSR_reporting_interval = parsed);
          }

          // wind units graps/dasboard
          if (ConfigurationLineMatcher.contains(lines[teller], WIND_UNITS_DASHBOARD_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> wind_units_dashboard = parsed);
          }

          // ship type for visual dashboard
          if (ConfigurationLineMatcher.contains(lines[teller], SHIP_TYPE_DASHBOARD_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> ship_type_dashboard = parsed);
          }

          // anemometer height above WL
          if (ConfigurationLineMatcher.contains(lines[teller], HEIGHT_ANEMOMETER_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> height_anemometer = parsed);
          }

          // GUI mode (light/full)
          if (ConfigurationLineMatcher.contains(lines[teller], GUI_MODE_TXT)) {
            settings.add(ConfigurationLineParser.value(lines[teller]), parsed -> GUI_mode = parsed);
          }

          // GUI logo (EUMETNET/NOAAA/SOT)
          if (ConfigurationLineMatcher.contains(lines[teller], GUI_LOGO_TXT)) {
            settings.add(ConfigurationLineParser.value(lines[teller]), parsed -> GUI_logo = parsed);
          }

          // obs email cc
          if (ConfigurationLineMatcher.contains(lines[teller], OBS_EMAIL_CC_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> obs_email_cc = parsed);
          }

          // RS232 2nd meteo instrument connection mode (= RS232 connected instrument II type: none,
          // StarX, HMP155)
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_INSTRUMENT_TYPE_TXT_II)) {
            settings.add(
                ConfigurationValueParser.integer(ConfigurationLineParser.value(lines[teller])),
                parsed -> RS232_connection_mode_II = parsed);
          }

          // RS232 2nd meteo instrument bits per sec
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_BITS_PER_SEC_TXT_II)) {
            settings.add(
                ConfigurationValueParser.integer(ConfigurationLineParser.value(lines[teller])),
                parsed -> bits_per_second_II = parsed);
          }

          // RS232 2nd meteo instrument data bits
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_DATA_BITS_TXT_II)) {
            settings.add(
                SerialConfigurationValueParser.dataBits(
                    ConfigurationLineParser.value(lines[teller])),
                parsed -> data_bits_II = parsed);
          }

          // RS232 2nd meteo instrument parity
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_PARITY_TXT_II)) {
            settings.add(
                SerialConfigurationValueParser.parity(ConfigurationLineParser.value(lines[teller])),
                parsed -> parity_II = parsed);
          }

          // RS232 2nd imeteo instrument stop bits
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_STOP_BITS_TXT_II)) {
            settings.add(
                SerialConfigurationValueParser.stopBits(
                    ConfigurationLineParser.value(lines[teller])),
                parsed -> stop_bits_II = parsed);
          }

          // RS232 2nd meteo instrument prefered COM port (Windows and Linux)
          if (ConfigurationLineMatcher.contains(lines[teller], RS232_PREFERED_COM_PORT_TXT_II)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> prefered_COM_port_number_II = parsed);
          }

          // AP[&T]R / AWSR send method
          if (ConfigurationLineMatcher.contains(lines[teller], APTR_AWSR_SEND_METHOD_TXT)) {
            settings.add(
                AptrAwsrSendMethodNormalizer.normalize(
                    ConfigurationLineParser.value(lines[teller])),
                parsed -> APTR_AWSR_send_method = parsed);
          }

          // station ID
          if (ConfigurationLineMatcher.contains(lines[teller], STATION_ID_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> station_ID = parsed);
          }

          // dashboard background image
          if (ConfigurationLineMatcher.contains(lines[teller], DASHBOARD_BACKGROUND_IMAGE_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> dashboard_background_image = parsed);
          }

          // your custom email address
          if (ConfigurationLineMatcher.contains(lines[teller], YOUR_CUSTOM_ADDRESS_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> your_custom_address = parsed);
          }

          // custom email server
          if (ConfigurationLineMatcher.contains(lines[teller], CUSTOM_EMAIL_SERVER_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> custom_email_server = parsed);
          }

          // custom email password
          if (ConfigurationLineMatcher.contains(lines[teller], CUSTOM_PASSWORD_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> custom_password = parsed);
          }

          // custom email security
          if (ConfigurationLineMatcher.contains(lines[teller], CUSTOM_SECURITY_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> custom_security = parsed);
          }

          // custom email port
          if (ConfigurationLineMatcher.contains(lines[teller], CUSTOM_PORT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> custom_port = parsed);
          }

          // pop-up dashboard? (true/false)
          if (ConfigurationLineMatcher.contains(lines[teller], POP_UP_DASHBOARD_TXT)) {
            settings.add(
                ConfigurationValueParser.bool(
                    lines[teller].substring(CONFIGURATION_FILE_POS_INHOUD)),
                parsed -> pop_up_screen = parsed); // pop_up_dashboard =
            // Boolean.valueOf(lines[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
            // NB You have to be carefull when using Boolean.valueOf(string) or
            // Boolean.parseBoolean(string).
            //    The reason for this is that the methods will always return false if the String is
            // not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in APR settings
          }

          // pop-up dashboard interval
          if (ConfigurationLineMatcher.contains(lines[teller], POP_UP_DASHBOARD_INTERVAL_TXT)) {
            settings.add(
                lines[teller].substring(CONFIGURATION_FILE_POS_INHOUD),
                parsed -> pop_up_screen_interval = parsed); // pop_up_dashboard_interval =
            // lines[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
          }

          // dashboard ship deck color
          if (ConfigurationLineMatcher.contains(lines[teller], DASHBOARD_SHIP_DECK_COLOR_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> ship_deck_color_String = parsed);
          }

          // Custom email module; normalize the retired Python value after applying all lines.
          if (ConfigurationLineMatcher.contains(lines[teller], CUSTOM_EMAIL_MODULE_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> custom_email_module = parsed);
          }

          // log files email send method (default or custom)
          if (ConfigurationLineMatcher.contains(lines[teller], LOGS_EMAIL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> log_files_email_send_method = parsed);
          }

          // eucaws upload/send method
          if (ConfigurationLineMatcher.contains(lines[teller], EUCAWS_UPLOADS_METHOD_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> eucaws_uploads_method = parsed);
          }

          // port mode option. deactivate APR/AWSR if ship speed is minimal; (< 1 knot / < 0.5 knot)
          // [true/false]
          if (ConfigurationLineMatcher.contains(lines[teller], PORT_MODE_OPTION_TXT)) {
            settings.add(
                ConfigurationValueParser.bool(ConfigurationLineParser.value(lines[teller])),
                parsed -> port_mode_option = parsed);
            // NB You have to be carefull when using Boolean.valueOf(string) or
            // Boolean.parseBoolean(string).
            //    The reason for this is that the methods will always return false if the String is
            // not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in APR/AWSR settings
          }

          // listening LAN IP address
          if (ConfigurationLineMatcher.contains(lines[teller], LAN_IP_ADDRESS_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> lan_ip_address = parsed);
          }

          // dashboard ship (LNG)tank color
          if (ConfigurationLineMatcher.contains(lines[teller], DASHBOARD_SHIP_TANK_COLOR_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> ship_tank_color_String = parsed);
          }

          // dashboard font (hybrid dashboard)
          if (ConfigurationLineMatcher.contains(lines[teller], DASHBOARD_FONT_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]), parsed -> dashboard_font = parsed);
          }

          // communication protocol client <-> server
          if (ConfigurationLineMatcher.contains(lines[teller], COM_PROTOCOL_TXT)) {
            settings.add(
                ConfigurationLineParser.value(lines[teller]),
                parsed -> server_com_protocol = parsed);
          }

          // EUCAWS OBS_ID (intials added to the SMD input)
          if (ConfigurationLineMatcher.contains(lines[teller], EUCAWS_OBS_ID_TXT)) {
            settings.add(
                ConfigurationValueParser.bool(ConfigurationLineParser.value(lines[teller])),
                parsed -> eucaws_obs_id = parsed);
          }
        } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
          throw new IllegalArgumentException(
              "invalid configuration value on line " + (teller + 1), ex);
        }
      } // if ((lines[teller] != null) etc.
    } // for (teller = 0; teller < MAX_AANTAL_CONFIGURATIEREGELS; teller++)

    return settings;
  }

  static void applyConfiguration(ConfigurationSettings settings) {
    settings.apply();

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

    normalizeEmailModule();
  }

  static void normalizeEmailModule() {
    if (PYTHON_EMAIL.equals(custom_email_module)) {
      custom_email_module = JAKARTA_EMAIL;
    }
  }
}
