package turbowin;

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
}
