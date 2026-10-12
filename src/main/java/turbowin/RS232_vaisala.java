package turbowin;

import static turbowin.main_RS232_RS422.RS232_make_RH_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_air_temp_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_dew_point_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// import static turbowin.main_RS232_RS422.TIMEDIFF_SENSOR_DATA;

public class RS232_vaisala {

  public static void RS232_Read_Sensor_Data_a_ppp_For_Obs() {
    // Vaisala PTB220 or PTB330 (NOT AWS and NOT Mintaka Duo/Star)

    if (main.RS232_connection_mode == 1) // PTB220
    {
      main.type_record_lengte = main.RECORD_LENGTE_PTB220;
      main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
      // type_record_minuten_begin_pos      = RECORD_MINUTEN_BEGIN_POS_PTB220;
      // type_record_pressure_begin_pos     = RECORD_P_BEGIN_POS_PTB220;
      main.type_record_a_begin_pos = main.RECORD_a_BEGIN_POS_PTB220;
      main.type_record_ppp_begin_pos = main.RECORD_ppp_BEGIN_POS_PTB220;
    } else if (main.RS232_connection_mode == 2) // PTB330
    {
      main.type_record_lengte = main.RECORD_LENGTE_PTB330;
      main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB330;
      // type_record_minuten_begin_pos      = RECORD_MINUTEN_BEGIN_POS_PTB330;
      // type_record_pressure_begin_pos     = RECORD_P_BEGIN_POS_PTB330;
      main.type_record_a_begin_pos = main.RECORD_a_BEGIN_POS_PTB330;
      main.type_record_ppp_begin_pos = main.RECORD_ppp_BEGIN_POS_PTB330;
    }

    // initialisation
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";

    Rs232VaisalaPressureTrendWorkflow.start();
  }

  public static void RS232_Read_Sensor_Data_PPPP_For_Obs(boolean local_tray_icon_clicked) {
    // Vaisala PTB220 or PTB330 (NOT AWS and NOT Mintaka Duo and NOT Mintaka Star)

    // called from: - initSynopparameters() [mybarometer.java]
    //              - main_windowIconfied() [main.java]
    //

    // if (barometer_type.equals(PTB220))
    if (main.RS232_connection_mode == 1) // PTB220
    {
      main.type_record_lengte = main.RECORD_LENGTE_PTB220;
      main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
      // type_record_minuten_begin_pos      = RECORD_MINUTEN_BEGIN_POS_PTB220;
      main.type_record_pressure_begin_pos = main.RECORD_P_BEGIN_POS_PTB220;
    }
    // else if (barometer_type.equals(PTB330_SERVICE_PORT))
    // else if (barometer_type.equals(PTB330))
    else if (main.RS232_connection_mode == 2) // PTB330
    {
      main.type_record_lengte = main.RECORD_LENGTE_PTB330;
      main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB330;
      // type_record_minuten_begin_pos      = RECORD_MINUTEN_BEGIN_POS_PTB330;
      main.type_record_pressure_begin_pos = main.RECORD_P_BEGIN_POS_PTB330;
    }

    // initialisation
    mybarometer.pressure_reading = "";

    // initialisation
    main.tray_icon_clicked = local_tray_icon_clicked;

    Rs232VaisalaPressureReadingWorkflow.start(local_tray_icon_clicked);
  }

  public static void RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR(
      final String destination, final boolean retry) {
    // PTB220 or PTB330

    // NB 'destination' possible strings: - WOW
    //                                    - APR
    //                                    - MAIN_SCREEN

    if (main.RS232_connection_mode == 1) // PTB220
    {
      main.type_record_lengte = main.RECORD_LENGTE_PTB220;
      main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
      main.type_record_pressure_begin_pos = main.RECORD_P_BEGIN_POS_PTB220;
      main.type_record_a_begin_pos = main.RECORD_a_BEGIN_POS_PTB220;
      main.type_record_ppp_begin_pos = main.RECORD_ppp_BEGIN_POS_PTB220;
    } else if (main.RS232_connection_mode == 2) // PTB330
    {
      main.type_record_lengte = main.RECORD_LENGTE_PTB330;
      main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB330;
      main.type_record_pressure_begin_pos = main.RECORD_P_BEGIN_POS_PTB330;
      main.type_record_a_begin_pos = main.RECORD_a_BEGIN_POS_PTB330;
      main.type_record_ppp_begin_pos = main.RECORD_ppp_BEGIN_POS_PTB330;
    }

    // initialisation
    // mybarometer.pressure_reading = "";

    Rs232VaisalaWowAprWorkflow.start(destination, retry);
  }

  public static void RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() {
    // NB this function for manual obs (inserting data on the temperatures input form)
    // called from: initSynopparameters() [mytemp.java]

    // NB see also - Read_Sensor_Data_Files_For_Air_Temp_Graph_HMP155() [RS232_view.java]
    //             - RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR()
    // [RS232_vaisala.java]

    // example saved record:
    //
    // ___22.98____17.77____14.77____60.09_201909041422
    // 3x      4x       4x       4x       1x spaces
    //

    // initialisation
    mytemp.air_temp =
        ""; // if not ="" -> nullpointerexception if update button clicked on main page
    mytemp.wet_bulb_temp = "";
    mytemp.RH = ""; // range 0 - 100
    mytemp.double_dew_point = main.INVALID;
    mytemp.double_rv = main.INVALID; // range 0.0 - 1.0 (or invalid) !!!!

    Rs232VaisalaHmp155ObservationWorkflow.start();
  }

  public static void RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR(
      final String destination, final boolean retry) {
    // NB this function for temperatures via APR WITH NO 1ST INSTRUMENT (BAROMETER) CONNECTED!

    // NB 'destination' possible strings: - WOW
    //                                    - APR
    //                                    - MAIN_SCREEN

    // NB see also - Read_Sensor_Data_Files_For_Air_Temp_Graph_HMP155() [main_RS232_RS422.java]
    //             - RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs()
    // [RS232_vaisla.java]
    //             -
    // RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument()
    // [RS232_vaisala.java]

    // example saved record:
    //
    // ___22.98____17.77____14.77____60.09_201909041422
    // 3x      4x       4x       4x       1x spaces
    //

    // initialisation
    mytemp.air_temp =
        ""; // if not ="" -> nullpointerexception if update button clicked on main page
    mytemp.wet_bulb_temp = "";
    mytemp.RH = ""; // range 0 - 100
    mytemp.double_dew_point = main.INVALID;
    mytemp.double_rv = main.INVALID; // range 0.0 - 1.0 (or invalid) !!!!

    Rs232VaisalaHmp155AprWorkflow.start(destination, retry);
  }

  public static void RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument(
      final String destination) {
    // called from: - RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR()
    //              - RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR()

    // NB this function for temperatures via APR WITH ALSO A 1ST INSTRUMENT (BAROMETER) CONNECTED!

    // NB see also: RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR
    // [RS232_vaisala.java] {no 1st instrument connected)
    // NB the difference with: RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() is the
    // use of a SwingWorker
    //    in APR in 2nd instrument mode this function will be invoked from within a SwingWorker of a
    // barometer data retrieving function
    //    + in this function the second last record is also taken into consideration

    // example saved record:
    //
    // ___22.98____17.77____14.77____60.09_201909041422
    // 3x      4x       4x       4x       1x spaces
    //

    // initialisation
    mytemp.air_temp =
        ""; // if not ="" -> nullpointerexception if update button clicked on main page
    mytemp.wet_bulb_temp = "";
    mytemp.RH = ""; // range 0 - 100
    mytemp.double_dew_point = main.INVALID;
    mytemp.double_rv = main.INVALID; // range 0.0 - 1.0 (or invalid) !!!!

    Boolean laatste_record_ok = false;
    main.obs_file_datum_tijd = new GregorianCalendar();
    main.obs_file_datum_tijd.add(
        Calendar.MINUTE,
        -2); // of is -1 ook goed????? : to be sure there was all time that it was written to the
    // file
    File sensor_data_file;
    String record = null;
    String record_0 = null; // last retrieved record
    String record_1 = null; // second last retrieved record (Dutch: een-na-laatste)
    String laatste_record = null;
    String local_dew_point = "";

    int error_code = 0; // 0: no error
    // 1: sensor data file not present
    // 2: last retrieved record; record length not ok
    // 3: last retrieved record obsolete
    // 4: error when determining file date of sensor data file
    // 5: error when opening sensor data file

    // determine sensor data file name
    String sensor_data_file_naam_datum_tijd_deel =
        main.sdf3.format(main.obs_file_datum_tijd.getTime()); // e.g. 2013020308
    String sensor_data_file_name =
        "sensor_data_II_" + sensor_data_file_naam_datum_tijd_deel + ".txt";

    // first check if there is a sensor data file present (and not empty)
    String volledig_path_sensor_data =
        main.logs_dir + java.io.File.separator + sensor_data_file_name;
    // System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

    sensor_data_file = new File(volledig_path_sensor_data);
    if (sensor_data_file.exists() && sensor_data_file.length() > 0) // length() in bytes
    {
      try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
        record = null;
        record_0 = null; // last retrieved record
        record_1 = null; // second last retrieved record
        laatste_record =
            null; // last record which is checked with ok result (most of the times record_0 but
        // sometimes record_1)

        // retrieve the last and second last records in the sensor data file
        //
        while ((record = in.readLine()) != null) {
          record_1 = record_0;
          record_0 = record;
        } // while ((record = in.readLine()) != null)

        // check on minimum record length last retrieved record (record_0)
        //
        if (record_0 != null) {
          if (record_0.length() != main.RECORD_LENGTE_HMP155) // fixed record length
          {
            System.out.println(
                "--- Vaisala HMP155 format (last record length = "
                    + record_0.length()
                    + "; should be "
                    + main.RECORD_LENGTE_HMP155
                    + ") last retrieved record NOT ok (file: "
                    + volledig_path_sensor_data
                    + ")");
            record_0 = null;
          }
        }

        // check on minimum record length second last retrieved record (record_1) if last record was
        // NOT ok
        //
        if ((record_0 == null) && (record_1 != null)) {
          if (record_1.length() != main.RECORD_LENGTE_HMP155) // fixed record length
          {
            System.out.println(
                "--- Vaisala HMP155 format (second last record length = "
                    + record_1.length()
                    + "; should be "
                    + main.RECORD_LENGTE_HMP155
                    + ") last retrieved record NOT ok (file: "
                    + volledig_path_sensor_data
                    + ")");
            error_code = 2;
            record_1 = null;
          } else {
            System.out.println(
                "--- Vaisala HMP155 format second last record length = ok (= alternative for last record)");
          }
        }

        // determine 'laatste_record' (will be record_0 or record_1)
        //
        if (record_0 != null) {
          laatste_record = record_0; // last retrieved record
        } else if (record_1 != null) {
          laatste_record = record_1; // second last retrieved record
        } else // last and second last record both NOT ok
        {
          laatste_record = null;
        }

        // last (or second last) retrieved record ok
        if (laatste_record != null) {
          int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
          String record_datum_tijd_minuten =
              laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

          Date file_date;
          try {
            file_date = main.sdf4.parse(record_datum_tijd_minuten);

            long system_sec = System.currentTimeMillis();

            long timeDiff =
                Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

            if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max ? minutes old
            {
              //   21.54    16.00    12.43    56.29 201909070722
              int pos1 = 0; // air temp, start pos
              int pos2 = 9; // Twet, start pos
              int pos3 = 18; // tdew, start pos
              int pos4 = 27; // RH, start pos

              // rounding eg: 998.19 -> 998.2
              //        double digitale_sensor_waarde =
              // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
              //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;
              // // bv 998.19 -> 998.2

              mytemp.air_temp = laatste_record.substring(pos1, pos1 + 8);
              mytemp.wet_bulb_temp = laatste_record.substring(pos2, pos2 + 8);
              local_dew_point = laatste_record.substring(pos3, pos3 + 8);
              mytemp.RH = laatste_record.substring(pos4, pos4 + 8);

              // skip all spaves
              mytemp.air_temp = mytemp.air_temp.trim();
              mytemp.wet_bulb_temp = mytemp.wet_bulb_temp.trim();
              local_dew_point = local_dew_point.trim();
              mytemp.RH = mytemp.RH.trim();

              if (destination.equals("MAIN_SCREEN") == false) {
                System.out.println("--- sensor data record, air temp for obs: " + mytemp.air_temp);
                System.out.println(
                    "--- sensor data record, wet bulb temp for obs: " + mytemp.wet_bulb_temp);
                System.out.println("--- sensor data record, dewpoint for obs: " + local_dew_point);
                System.out.println("--- sensor data record, RH for obs: " + mytemp.RH);
              }

              laatste_record_ok = true;

            } // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
            else {
              mytemp.air_temp = "";
              mytemp.wet_bulb_temp = "";
              local_dew_point = "";
              mytemp.RH = "";
              mytemp.double_rv = main.INVALID;
              mytemp.double_dew_point = main.INVALID;

              laatste_record_ok = false;
              error_code = 3;
            } // else

          } // try
          catch (ParseException ex) {
            // Logger.getLogger(main_RS232_RS422.class.getName()).log(Level.SEVERE, null, ex);

            System.out.println(
                "--- "
                    + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() "
                    + ex);

            mytemp.air_temp = "";
            mytemp.wet_bulb_temp = "";
            local_dew_point = "";
            mytemp.RH = "";
            mytemp.double_rv = main.INVALID;
            mytemp.double_dew_point = main.INVALID;

            laatste_record_ok = false;
            error_code = 4;
          } // catch
        } // if (laatste_record != null)

      } // try
      catch (IOException ex) {
        System.out.println(
            "--- "
                + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() "
                + ex);

        mytemp.air_temp = "";
        mytemp.wet_bulb_temp = "";
        local_dew_point = "";
        mytemp.RH = "";
        mytemp.double_rv = main.INVALID;
        mytemp.double_dew_point = main.INVALID;

        laatste_record_ok = false;
        error_code = 5;
      } // try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
    } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
    else {
      laatste_record_ok = false;
      error_code = 1;
    }

    // clear memory
    main.obs_file_datum_tijd = null;

    if (laatste_record_ok) {
      double hulp_double_air_temp = 999999; // 999999 = random number but > 99.9
      double hulp_double_wet_bulb_temp = 999999; // 999999 = random number but > 99.9
      double hulp_double_RH = 999999; // 999999 = random number but > 99.9

      // air temp check [APR]
      //
      if ((mytemp.air_temp.compareTo("") != 0)
          && (mytemp.air_temp != null)
          && (mytemp.air_temp.indexOf("*") == -1)) {
        try {
          hulp_double_air_temp = Double.parseDouble(mytemp.air_temp.trim());

          if ((hulp_double_air_temp > -99.9) && (hulp_double_air_temp < 99.9)) {
            DecimalFormat df = new DecimalFormat("0.0"); // rounding 1 decimal
            mytemp.air_temp = df.format(hulp_double_air_temp);

            if (destination.equals("MAIN_SCREEN") == false) {
              String message_air_temp =
                  "[APR] air temp at sensor height = " + mytemp.air_temp + " \u00B0C";
              main.log_turbowin_system_message(message_air_temp);
            }

            // make IMMT ready
            if (destination.equals("MAIN_SCREEN") == false) {
              RS232_make_air_temp_APR_FM13_IMMT_ready();
            }
          } else {
            if (destination.equals("MAIN_SCREEN") == false) {
              main.log_turbowin_system_message(
                  "[APR] automatically retrieved air temp: outside range");
            }
            mytemp.air_temp = "";
          }
        } // try
        catch (NumberFormatException e) {
          System.out.println(
              "--- "
                  + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() "
                  + e);
          mytemp.air_temp = "";
        }
      } // if ( (mytemp.air_temp.compareTo("") != 0) etc.

      // wet bulb temp check [APR]
      //
      if ((mytemp.wet_bulb_temp.compareTo("") != 0)
          && (mytemp.wet_bulb_temp != null)
          && (mytemp.wet_bulb_temp.indexOf("*") == -1)) {
        try {
          hulp_double_wet_bulb_temp = Double.parseDouble(mytemp.wet_bulb_temp.trim());

          if ((hulp_double_wet_bulb_temp > -99.9) && (hulp_double_wet_bulb_temp < 99.9)) {
            DecimalFormat df = new DecimalFormat("0.0"); // rounding 1 decimal
            mytemp.wet_bulb_temp = df.format(hulp_double_wet_bulb_temp);

            if (destination.equals("MAIN_SCREEN") == false) {
              String message_wet_bulb_temp =
                  "[APR] wet-bulb temp at sensor height = " + mytemp.wet_bulb_temp + " \u00B0C";
              main.log_turbowin_system_message(message_wet_bulb_temp);
            }

            // make IMMT ready [APR]
            if (destination.equals("MAIN_SCREEN") == false) {
              RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready();
            }
          } else {
            if (destination.equals("MAIN_SCREEN") == false) {
              main.log_turbowin_system_message(
                  "[APR] automatically retrieved wet-bulb temp: outside range");
            }
            mytemp.wet_bulb_temp = "";
          }
        } // try
        catch (NumberFormatException e) {
          System.out.println(
              "--- "
                  + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() "
                  + e);
          mytemp.wet_bulb_temp = "";
        }
      } // if ( (mytemp.wet_bulb_temp.compareTo("") != 0) etc.

      // dewpoint check [APR]
      //
      mytemp.double_dew_point = main.INVALID;
      if ((local_dew_point.compareTo("") != 0)
          && (local_dew_point != null)
          && (local_dew_point.indexOf("*") == -1)) {
        try {
          mytemp.double_dew_point = Double.parseDouble(local_dew_point.trim());

          if ((mytemp.double_dew_point > -70.0) && (mytemp.double_dew_point < 70.0)) {
            if (destination.equals("MAIN_SCREEN") == false) {
              DecimalFormat df = new DecimalFormat("0.0"); // rounding 1 decimal
              String message_dew_point =
                  "[APR] dew-point at sensor height = "
                      + df.format(mytemp.double_dew_point)
                      + " \u00B0C";
              main.log_turbowin_system_message(message_dew_point);
            }

            // make IMMT ready
            if (destination.equals("MAIN_SCREEN") == false) {
              RS232_make_dew_point_APR_FM13_IMMT_ready();
            }
          } else {
            if (destination.equals("MAIN_SCREEN") == false) {
              main.log_turbowin_system_message(
                  "[APR] automatically retrieved dew point: outside range");
            }
            mytemp.double_dew_point = main.INVALID;
          }
        } catch (NumberFormatException e) {
          System.out.println(
              "--- "
                  + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() "
                  + e);
          mytemp.double_dew_point = main.INVALID;
        } // catch
      } // if ( (local_dew_point.compareTo("") != 0) etc.

      // RH check [APR]
      //
      if ((mytemp.RH.compareTo("") != 0) && (mytemp.RH != null) && (mytemp.RH.indexOf("*") == -1)) {
        try {
          hulp_double_RH = Double.parseDouble(mytemp.RH); // range 0.0 - 100.0

          if ((hulp_double_RH >= 0.0) && (hulp_double_RH <= 100.0)) {
            mytemp.double_rv =
                hulp_double_RH
                    / 100; // NB mytemp.double_rv in range 0.0 - 1.0 [see also mytemp.java] !!!

            if (destination.equals("MAIN_SCREEN") == false) {
              String message_dew_point = "[APR] RH at sensor height = " + mytemp.RH + " %";
              main.log_turbowin_system_message(message_dew_point);
            }

            // NB altough RH not used in FM13
            // make IMMT ready
            if (destination.equals("MAIN_SCREEN") == false) {
              RS232_make_RH_APR_FM13_IMMT_ready();
            }
          } else {
            if (destination.equals("MAIN_SCREEN") == false) {
              main.log_turbowin_system_message("[APR] automatically retrieved RH: outside range");
            }
            mytemp.RH = "";
            mytemp.double_rv = main.INVALID;
          }

        } // try
        catch (NumberFormatException e) {
          System.out.println(
              "--- "
                  + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() "
                  + e);
          mytemp.RH = "";
          mytemp.double_rv = main.INVALID;
        }
      } // if ( (mytemp.RH.compareTo("") != 0) etc.

      // update the temperature fields on main screen
      //
      if (destination.equals("MAIN_SCREEN") == true) {
        main.temperatures_fields_update();
      }

    } // if (laatste_record_ok)
    else {
      // error codes in this function:
      //    0: no error
      //    1: sensor data file not present
      //    2: last and second last retrieved records; record length not ok
      //    3: last retrieved record obsolete
      //    4: error when determining file date of sensor data file
      //    5: error when opening sensor data file

      String error_message = "";
      switch (error_code) {
        case 0:
          error_message = "";
          break;
        case 1:
          error_message = "[APR] HMP155; file sensor data not found";
          break;
        case 2:
          error_message =
              "[APR] HMP155; last and second last retrieved records; record length not ok";
          break;
        case 3:
          error_message = "[APR] HMP155; last retrieved record obsolete";
          break;
        case 4:
          error_message = "[APR] HMP155; error when determining file date of sensor data file";
          break;
        case 5:
          error_message = "[APR] HMP155; error when opening sensor data file";
          break;
        default:
          error_message = "";
          break;
      }

      if (error_message.equals("") == false) {
        if (destination.equals("MAIN_SCREEN") == false) {
          main.log_turbowin_system_message(error_message);
        }
      }
    } // else
  }
}
