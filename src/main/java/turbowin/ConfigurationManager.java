package turbowin;

import static turbowin.main.*;

import com.fazecast.jSerialComm.SerialPort;

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
        if (configuratie_regels[teller].indexOf(SHIP_NAME_TXT) != -1) {
          // zo ja, dan staat op een bepaalde pos (achter de : ) de inhoud
          ship_name = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // imo number
        if (configuratie_regels[teller].indexOf(IMO_NUMBER_TXT) != -1) {
          imo_number = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
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
        if (configuratie_regels[teller].indexOf(TIME_ZONE_COMPUTER_TXT) != -1) {
          time_zone_computer = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // recruiting country
        if (configuratie_regels[teller].indexOf(RECRUITING_COUNTRY_TXT) != -1) {
          recruiting_country = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // method determining waves
        if (configuratie_regels[teller].indexOf(METHOD_WAVES_TXT) != -1) {
          method_waves = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // wind meta data
        if (configuratie_regels[teller].indexOf(WIND_SOURCE_TXT) != -1) {
          wind_source = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        if (configuratie_regels[teller].indexOf(MAX_HEIGHT_DECK_CARGO_TXT) != -1) {
          max_height_deck_cargo =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        if (configuratie_regels[teller].indexOf(DIFF_SLL_WL_TXT) != -1) {
          diff_sll_wl = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // air pressure meta data
        if (configuratie_regels[teller].indexOf(BAROMETER_ABOVE_SLL_TXT) != -1) {
          barometer_above_sll =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        if (configuratie_regels[teller].indexOf(BAROMETER_KEEL_TO_SLL_TXT) != -1) {
          keel_sll = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        if (configuratie_regels[teller].indexOf(PRESSURE_READING_MSL_TXT) != -1) {
          pressure_reading_msl_yes_no =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // air temp exposure
        if (configuratie_regels[teller].indexOf(AIR_TEMP_EXPOSURE_TXT) != -1) {
          air_temp_exposure = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // sst exposure
        if (configuratie_regels[teller].indexOf(SST_EXPOSURE_TXT) != -1) {
          sst_exposure = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // Obs E-mail recipient
        if (configuratie_regels[teller].indexOf(OBS_EMAIL_RECIPIENT_TXT) != -1) {
          obs_email_recipient =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // Obs E-mail subject
        if (configuratie_regels[teller].indexOf(OBS_EMAIL_SUBJECT_TXT) != -1) {
          obs_email_subject = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // logs dir (for immt.log etc.)
        if (configuratie_regels[teller].indexOf(LOGS_DIR_TXT) != -1) {
          logs_dir = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // logs E-mail recipient
        if (configuratie_regels[teller].indexOf(LOGS_EMAIL_RECIPIENT_TXT) != -1) {
          logs_email_recipient =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // wind units (knots or m/s)
        if (configuratie_regels[teller].indexOf(WIND_UNITS_TXT) != -1) {
          wind_units = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // RS232 INSTRUMENT connection mode (= RS232 connected instrument type -none, PTB220,
        // PTB330, SAWS-)
        if (configuratie_regels[teller].indexOf(RS232_INSTRUMENT_TYPE_TXT) != -1) {
          RS232_connection_mode =
              Integer.parseInt(
                  configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
        }

        // RS232 INSTRUMENT bits per sec
        if (configuratie_regels[teller].indexOf(RS232_BITS_PER_SEC_TXT) != -1) {
          bits_per_second =
              Integer.parseInt(
                  configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
        }

        // RS232 INSTRUMENT data bits
        if (configuratie_regels[teller].indexOf(RS232_DATA_BITS_TXT) != -1) {
          String hulp_data_bits =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);

          switch (hulp_data_bits) {
            case "7":
              data_bits = 7;
              break;
            case "8":
              data_bits = 8;
              break;
            default:
              data_bits = 0; // non existing (data bits) value
              break;
          } // switch (hulp_data_bits)
        }

        // RS232 INSTRUMENT parity
        if (configuratie_regels[teller].indexOf(RS232_PARITY_TXT) != -1) {
          String hulp_parity = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);

          switch (hulp_parity) {
            case "0":
              parity = SerialPort.NO_PARITY;
              break;
            case "1":
              parity = SerialPort.ODD_PARITY;
              break;
            case "2":
              parity = SerialPort.EVEN_PARITY;
              break;
            default:
              parity = 99; // non existing (parity) value
              break;
          } // switch (hulp_parity)
        }

        // RS232 INSTRUMENT stop bits
        if (configuratie_regels[teller].indexOf(RS232_STOP_BITS_TXT) != -1) {
          String hulp_stop_bits =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);

          switch (hulp_stop_bits) {
            case "1":
              stop_bits = SerialPort.ONE_STOP_BIT;
              break;
            case "2":
              stop_bits = SerialPort.TWO_STOP_BITS;
              break;
            default:
              stop_bits = 0; // non existing (stop bits) value
              break;
          } // switch (hulp_stop_bits)
        }

        // RS232 INSTRUMENT prefered COM port (Windows and Linux)
        if (configuratie_regels[teller].indexOf(RS232_PREFERED_COM_PORT_TXT) != -1) {
          prefered_COM_port_number =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // ic (instrument correction) barometer
        if (configuratie_regels[teller].indexOf(IC_BAROMETER_TXT) != -1) {
          barometer_instrument_correction =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // obs format (FM13 or format 101)
        if (configuratie_regels[teller].indexOf(OBS_FORMAT_TXT) != -1) {
          obs_format = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // format 101 call sign encryption (yes or no)
        if (configuratie_regels[teller].indexOf(FORMAT_101_ENCRYPTION_TXT) != -1) {
          obs_101_encryption = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // format 101 email (body or attachement)
        if (configuratie_regels[teller].indexOf(FORMAT_101_EMAIL_TXT) != -1) {
          obs_101_email = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // RS232 INSTRUMENT prefered COM port name (OS X)
        if (configuratie_regels[teller].indexOf(RS232_PREF_COM_PORT_NAME_TXT) != -1) {
          prefered_COM_port_name =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // WOW publish?
        if (configuratie_regels[teller].indexOf(WOW_PUBLISH_TXT) != -1) {
          WOW =
              Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in WOW settings
        }

        // WOW site id
        if (configuratie_regels[teller].indexOf(WOW_SITE_ID_TXT) != -1) {
          WOW_site_id = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // WOW pin
        if (configuratie_regels[teller].indexOf(WOW_PIN_TXT) != -1) {
          WOW_site_pin = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // WOW reporting(upload) interval
        if (configuratie_regels[teller].indexOf(WOW_REPORTING_INTERVAL_TXT) != -1) {
          WOW_reporting_interval =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // WOW/APR average draught
        if (configuratie_regels[teller].indexOf(WOW_APR_AVERAGE_DRAUGHT_TXT) != -1) {
          WOW_APR_average_draught =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // default E-mail program on this computer is AMOS Mail?
        if (configuratie_regels[teller].indexOf(AMOS_MAIL_TXT) != -1) {
          amos_mail =
              Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in email settings
        }

        // RS232 GPS connection mode
        if (configuratie_regels[teller].indexOf(RS232_GPS_TYPE_TXT) != -1) {
          RS232_GPS_connection_mode =
              Integer.parseInt(
                  configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
        }

        // RS232 GPS bits per second
        if (configuratie_regels[teller].indexOf(RS232_GPS_BITS_PER_SEC_TXT) != -1) {
          GPS_bits_per_second =
              Integer.parseInt(
                  configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
        }

        // RS232 prefered GPS COM port (Windows and Linux)
        if (configuratie_regels[teller].indexOf(RS232_GPS_COM_PORT_TXT) != -1) {
          prefered_GPS_COM_port_number =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // RS232 prefered GPS COM port (OS X)
        if (configuratie_regels[teller].indexOf(RS232_GPS_COM_PORT_NAME_TXT) != -1) {
          prefered_GPS_COM_port_name =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // RS232 GPS sentence (RMC or GGA)
        if (configuratie_regels[teller].indexOf(RS232_GPS_SENTENCE_TXT) != -1) {
          RS232_GPS_sentence =
              Integer.parseInt(
                  configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
        }

        // APR (Automated Pressure Reports)?
        if (configuratie_regels[teller].indexOf(APR_TXT) != -1) {
          APR =
              Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in APR settings
        }

        // APR reporting(upload) interval
        if (configuratie_regels[teller].indexOf(APR_REPORTING_INTERVAL_TXT) != -1) {
          APR_reporting_interval =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // upload URL
        if (configuratie_regels[teller].indexOf(UPLOAD_URL_TXT) != -1) {
          upload_URL = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // AWSR (Automatic Weather Station Reports)?
        if (configuratie_regels[teller].indexOf(AWSR_TXT) != -1) {
          AWSR =
              Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in AWSR settings
        }

        // AWSR reporting(upload) interval
        if (configuratie_regels[teller].indexOf(AWSR_REPORTING_INTERVAL_TXT) != -1) {
          AWSR_reporting_interval =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // wind units graps/dasboard
        if ((configuratie_regels[teller].indexOf(WIND_UNITS_DASHBOARD_TXT) != -1)) {
          wind_units_dashboard =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // ship type for visual dashboard
        if (configuratie_regels[teller].indexOf(SHIP_TYPE_DASHBOARD_TXT) != -1) {
          ship_type_dashboard =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // anemometer height above WL
        if (configuratie_regels[teller].indexOf(HEIGHT_ANEMOMETER_TXT) != -1) {
          height_anemometer = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // GUI mode (light/full)
        if (configuratie_regels[teller].indexOf(GUI_MODE_TXT) != -1) {
          GUI_mode = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // GUI logo (EUMETNET/NOAAA/SOT)
        if (configuratie_regels[teller].indexOf(GUI_LOGO_TXT) != -1) {
          GUI_logo = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // obs email cc
        if (configuratie_regels[teller].indexOf(OBS_EMAIL_CC_TXT) != -1) {
          obs_email_cc = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // RS232 2nd meteo instrument connection mode (= RS232 connected instrument II type: none,
        // StarX, HMP155)
        if (configuratie_regels[teller].indexOf(RS232_INSTRUMENT_TYPE_TXT_II) != -1) {
          RS232_connection_mode_II =
              Integer.parseInt(
                  configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
        }

        // RS232 2nd meteo instrument bits per sec
        if (configuratie_regels[teller].indexOf(RS232_BITS_PER_SEC_TXT_II) != -1) {
          bits_per_second_II =
              Integer.parseInt(
                  configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
        }

        // RS232 2nd meteo instrument data bits
        if (configuratie_regels[teller].indexOf(RS232_DATA_BITS_TXT_II) != -1) {
          String hulp_data_bits =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);

          switch (hulp_data_bits) {
            case "7":
              data_bits_II = 7;
              break;
            case "8":
              data_bits_II = 8;
              break;
            default:
              data_bits_II = 0; // non existing (data bits) value
              break;
          } // switch (hulp_data_bits)
        }

        // RS232 2nd meteo instrument parity
        if (configuratie_regels[teller].indexOf(RS232_PARITY_TXT_II) != -1) {
          String hulp_parity = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);

          switch (hulp_parity) {
            case "0":
              parity_II = SerialPort.NO_PARITY;
              break;
            case "1":
              parity_II = SerialPort.ODD_PARITY;
              break;
            case "2":
              parity_II = SerialPort.EVEN_PARITY;
              break;
            default:
              parity_II = 99; // non existing (parity) value
              break;
          } // switch (hulp_parity)
        }

        // RS232 2nd imeteo instrument stop bits
        if (configuratie_regels[teller].indexOf(RS232_STOP_BITS_TXT_II) != -1) {
          String hulp_stop_bits =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);

          switch (hulp_stop_bits) {
            case "1":
              stop_bits_II = SerialPort.ONE_STOP_BIT;
              break;
            case "2":
              stop_bits_II = SerialPort.TWO_STOP_BITS;
              break;
            default:
              stop_bits_II = 0; // non existing (stop bits) value
              break;
          } // switch (hulp_stop_bits)
        }

        // RS232 2nd meteo instrument prefered COM port (Windows and Linux)
        if (configuratie_regels[teller].indexOf(RS232_PREFERED_COM_PORT_TXT_II) != -1) {
          prefered_COM_port_number_II =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // AP[&T]R / AWSR send method
        if (configuratie_regels[teller].indexOf(APTR_AWSR_SEND_METHOD_TXT) != -1) {
          APTR_AWSR_send_method =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);

          // from version 4.2: SMTP host, Gmail and Yahoo are not possible anymore (only possible
          // 'Server Met. center' and Custom email) (when reading configaration settings from a
          // previous TurboWin+ version)
          if ((APTR_AWSR_send_method.equals(APTR_AWSR_SMTP_HOST))
              || (APTR_AWSR_send_method.equals(APTR_AWSR_GMAIL))
              || (APTR_AWSR_send_method.equals(APTR_AWSR_YAHOO_MAIL))) {
            APTR_AWSR_send_method = "";
          }
        }

        // station ID
        if (configuratie_regels[teller].indexOf(STATION_ID_TXT) != -1) {
          station_ID = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // dashboard background image
        if (configuratie_regels[teller].indexOf(DASHBOARD_BACKGROUND_IMAGE_TXT) != -1) {
          dashboard_background_image =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // your custom email address
        if (configuratie_regels[teller].indexOf(YOUR_CUSTOM_ADDRESS_TXT) != -1) {
          your_custom_address =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // custom email server
        if (configuratie_regels[teller].indexOf(CUSTOM_EMAIL_SERVER_TXT) != -1) {
          custom_email_server =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // custom email password
        if (configuratie_regels[teller].indexOf(CUSTOM_PASSWORD_TXT) != -1) {
          custom_password = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // custom email security
        if (configuratie_regels[teller].indexOf(CUSTOM_SECURITY_TXT) != -1) {
          custom_security = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // custom email port
        if (configuratie_regels[teller].indexOf(CUSTOM_PORT_TXT) != -1) {
          custom_port = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // pop-up dashboard? (true/false)
        if (configuratie_regels[teller].indexOf(POP_UP_DASHBOARD_TXT) != -1) {
          pop_up_screen =
              Boolean.valueOf(
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
        if (configuratie_regels[teller].indexOf(POP_UP_DASHBOARD_INTERVAL_TXT) != -1) {
          pop_up_screen_interval =
              configuratie_regels[teller].substring(
                  CONFIGURATION_FILE_POS_INHOUD); // pop_up_dashboard_interval =
          // configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // dashboard ship deck color
        if (configuratie_regels[teller].indexOf(DASHBOARD_SHIP_DECK_COLOR_TXT) != -1) {
          ship_deck_color_String =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // custom email module (primary-jakarta, secondary-python)
        if (configuratie_regels[teller].indexOf(CUSTOM_EMAIL_MODULE_TXT) != -1) {
          custom_email_module =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // log files email send method (default or custom)
        if (configuratie_regels[teller].indexOf(LOGS_EMAIL_TXT) != -1) {
          log_files_email_send_method =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // eucaws upload/send method
        if (configuratie_regels[teller].indexOf(EUCAWS_UPLOADS_METHOD_TXT) != -1) {
          eucaws_uploads_method =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // port mode option. deactivate APR/AWSR if ship speed is minimal; (< 1 knot / < 0.5 knot)
        // [true/false]
        if (configuratie_regels[teller].indexOf(PORT_MODE_OPTION_TXT) != -1) {
          port_mode_option =
              Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
          // NB You have to be carefull when using Boolean.valueOf(string) or
          // Boolean.parseBoolean(string).
          //    The reason for this is that the methods will always return false if the String is
          // not equal to "true" (the case is ignored).
          //    For example: Boolean.valueOf("YES") -> false
          //    BUT no problem here because automatically genenerated in APR/AWSR settings
        }

        // listening LAN IP address
        if (configuratie_regels[teller].indexOf(LAN_IP_ADDRESS_TXT) != -1) {
          lan_ip_address = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // dashboard ship (LNG)tank color
        if (configuratie_regels[teller].indexOf(DASHBOARD_SHIP_TANK_COLOR_TXT) != -1) {
          ship_tank_color_String =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // dashboard font (hybrid dashboard)
        if (configuratie_regels[teller].indexOf(DASHBOARD_FONT_TXT) != -1) {
          dashboard_font = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // communication protocol client <-> server
        if (configuratie_regels[teller].indexOf(COM_PROTOCOL_TXT) != -1) {
          server_com_protocol =
              configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
        }

        // EUCAWS OBS_ID (intials added to the SMD input)
        if (configuratie_regels[teller].indexOf(EUCAWS_OBS_ID_TXT) != -1) {
          eucaws_obs_id =
              Boolean.parseBoolean(
                  configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
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
