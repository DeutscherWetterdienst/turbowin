package turbowin;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

final class Rs422ReadAndSendSensorDataForAwsrWorkflow {

  private final main_RS232_RS422 owner;

  Rs422ReadAndSendSensorDataForAwsrWorkflow(main_RS232_RS422 owner) {
    this.owner = owner;
  }

  void start(final boolean retry) {
    // check the presence of the various meteo parameters in the data received from an AWS (data
    // stored by TurboWin+ in sensor data files)

    // called from: Function RS422_V3_write_sensor_data_to_file() [file main_RS232_RS422.java]
    //

    // 3201312130055180596059842742000955055999202112502009000              44PDZS   NL 314
    // 0075300722           A411111111119999111111 06907106000002080151111 11100764 8951932 BLOKZYL
    // RR
    // 32018061308745900210                                  0              44TESTNL UK 314        2
    //   35           A599999999999999191191                    9999 9990530001234567
    // 32018061312745900210                                  0              44TESTNL US 314        2
    //   35           A599999999999999191191              12839999 9110530001234567
    // 32018061313745900210                                  0              44TESTNL US 314        2
    //   35           A599999999999999191191              12839999 9110530001234567
    // 32018061314745900210                                  0              44TESTNL US 314        2
    //   35           A599999999999999191191              12839999 9110530001234567

    // initialisation
    main_RS232_RS422.RS422_initialise_AWS_Sensor_Data_For_AWSR();

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String last_record = null;

        // determine sensor data file name
        String sensor_data_file_naam_datum_tijd_deel =
            main.sdf3.format(main.obs_file_datum_tijd.getTime()); // e.g. 2013020308
        String sensor_data_file_name =
            "sensor_data_" + sensor_data_file_naam_datum_tijd_deel + ".txt";

        // first check if there is a sensor data file present (and not empty)
        String volledig_path_sensor_data =
            main.logs_dir + java.io.File.separator + sensor_data_file_name;
        // System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

        sensor_data_file = new File(volledig_path_sensor_data);
        if (sensor_data_file.exists() && sensor_data_file.length() > 0) // length() in bytes
        {
          try {
            last_record =
                SensorDataRecordReader.readLatestMatchingRecord(
                    sensor_data_file,
                    record ->
                        record.length() > 43
                            && record.substring(0, 6).equals("$PEUMB")
                            && record.indexOf(sensor_data_file_naam_datum_tijd_deel) != -1);
            if (last_record == null) {
              last_record = "";
            }
          } // try
          catch (IOException ex) {
            last_record = "";
            System.out.println("--- Function RS422_Read_And_Send_Sensor_Data_For_AWSR(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
        else // no sensor_data_file present
        {
          last_record = null;
        }

        return last_record;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        boolean doorgaan_in_record = true;
        int number_read_commas;
        int pos;
        String record_aws_date =
            ""; // will be passed to function: RS422_Check_Date_Time_Last_Saved_Record_For_AWSR()
        String record_aws_time =
            ""; // will be passed to function: RS422_Check_Date_Time_Last_Saved_Record_For_AWSR()
        boolean set_date_time_record_ok = false;
        boolean check_date_time_record_ok = false;

        String laatste_record = null;
        try {
          laatste_record = get();
        } catch (InterruptedException | ExecutionException ex) {
          String message =
              "[AWSR] error retrieving AWS data from sensor data file; " + ex.toString();
          main.log_turbowin_system_message(message);
        }

        if (laatste_record == null) {
          String message = "[AWSR] no sensor data file exists or file empty";
          main.log_turbowin_system_message(message);
          doorgaan_in_record = false;
        } else if (laatste_record.equals("")) {
          String message =
              "[AWSR] sensor data file found but no records found or misformed records";
          main.log_turbowin_system_message(message);
          doorgaan_in_record = false;
        }

        if (doorgaan_in_record == true && laatste_record != null) {
          // Check the number of comma's in the record that must exactely match the fixed expected
          // number (TOTAL_NUMBER_RECORD_COMMAS)
          number_read_commas = 0;
          pos = -1;

          do {
            pos = laatste_record.indexOf(",", pos + 1);
            if (pos > 0) // "," found
            {
              number_read_commas++;
            }
          } while (pos > 0);

          // System.out.println("+++ record = " + laatste_record);
          // System.out.println("+++ number read commas = " + number_read_commas);
          if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS) {
            doorgaan_in_record = false;
          }
        } // if (doorgaan_in_record == true)

        if (doorgaan_in_record == true && laatste_record != null) {
          number_read_commas = 0;
          pos = -1;

          do {
            // NB eg:
            // $PEUMB,20120715,090227,52.405, 5.338,192.0,5.2,184.0, 997.4,998.6,2.3,2,16.0,87.5,
            // ,9.4,98.5,12.4,55.2,15.2,44.0,24.2,18.8,-38,,,201207150902
            pos = laatste_record.indexOf(",", pos + 1); // searching "," from position "pos + 1"
            if (pos > 0) // "," found
            {
              number_read_commas++;

              //////////////////// Date /////////////////
              //
              // format 101:
              //   mydatetime.year
              //   mydatetime.MM_code
              //   mydatetime.day
              //
              // extra for IMMT:
              //   mydatetime.YY_code
              //
              if (number_read_commas == main.DATE_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char
                  {
                    record_aws_date = laatste_record.substring(pos + 1, pos2); // YYYYMMDD
                    // mydatetime.day = record_aws_date.substring(record_aws_date.length() - 2);
                    //                               // DD eg "12" or "02"
                    // mydatetime.MM_code = record_aws_date.substring(record_aws_date.length() - 4,
                    // record_aws_date.length() - 2); // MM
                    // mydatetime.year = record_aws_date.substring(0, record_aws_date.length() - 4);
                    //                               // YYYY
                    // mydatetime.YY_code = mydatetime.day; // in case this AWS record it is always
                    // the same! (not in case manual input)
                  } // if (pos2 - pos >= 2)
                }
              } // if (number_read_commas == etc.

              ////////////////// Time /////////////////
              //
              // see also:  Function RS422_Set_Date_Time_Obs_And_IMMT_For_AWSR()
              //            Function RS422_Check_Date_Time_Last_Saved_Record_For_AWSR()
              //            (called from within this function just for sending)
              //
              //
              // format 101: see: 'see also' above
              //
              // extra for IMMT: see:  'see also' above
              //
              if (number_read_commas == main.TIME_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char
                  {
                    record_aws_time = laatste_record.substring(pos + 1, pos2);
                    // if (record_aws_time.length() == 6) // HHMMSS
                    // {
                    //   mydatetime.hour = record_aws_time.substring(0, 2);      // HH from HHMMSS
                    //   mydatetime.GG_code = mydatetime.hour; // in case this AWS record it is
                    // always the same! (not in case manual input)
                    // }
                  } // if (pos2 - pos >= 2)
                }
              } // if (number_read_commas == etc.

              ////////////////// Latitude [SMD output format sDD.ddd; s = "-" if South; + always
              // omitted] //////////////////
              //
              // for format101:
              //   myposition.int_latitude_degrees
              //   myposition.int_latitude_minutes
              //   myposition.latitude_hemisphere
              //
              // extra for IMMT:
              //   (myposition.Qc_code)
              //   myposition.lalala_code
              //
              if (number_read_commas == main.LATITUDE_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between commas at least 1 char
                  {
                    String record_aws_latitude = laatste_record.substring(pos + 1, pos2);

                    if (record_aws_latitude.indexOf("-") != -1) // found "-" in string
                    {
                      myposition.latitude_hemisphere = myposition.HEMISPHERE_SOUTH; // "South";
                      myposition.latitude_degrees =
                          record_aws_latitude.substring(
                              1, record_aws_latitude.indexOf(".")); // -12.344 -> 12
                    } else {
                      myposition.latitude_hemisphere = myposition.HEMISPHERE_NORTH; // "North";
                      myposition.latitude_degrees =
                          record_aws_latitude.substring(
                              0, record_aws_latitude.indexOf(".")); // 12.344 -> 12
                    }

                    // String latitude convert to integer
                    try {
                      myposition.int_latitude_degrees =
                          Integer.parseInt(myposition.latitude_degrees.trim());
                    } catch (NumberFormatException e) {
                    }

                    double decimal_degrees_lat = Math.abs(Double.parseDouble(record_aws_latitude));
                    myposition.int_latitude_minutes =
                        (int)
                            Math.round(
                                (decimal_degrees_lat - Math.floor(decimal_degrees_lat)) * 60);

                    // for IMMT
                    //
                    //
                    // NB myposition.Qc_code will be determined in the Longitude section (see below)

                    int int_latitude_minutes_6 =
                        myposition.int_latitude_minutes
                            / 6; // devide the minutes by six and disregard the remainder
                    String latitude_minutes_6 =
                        Integer.toString(int_latitude_minutes_6); // convert to String

                    myposition.lalala_code =
                        myposition.latitude_degrees.trim().replaceFirst("^0+(?!$)", "")
                            + latitude_minutes_6;

                    int len = 3; // always 3 chars
                    if (myposition.lalala_code.length() < len) {
                      // pad on left with zeros
                      myposition.lalala_code =
                          "0000000000".substring(0, len - myposition.lalala_code.length())
                              + myposition.lalala_code;
                    }
                  } // if (pos2 - pos >= 2)
                } // if (laatste_record.length() etc.
              } // if (number_read_commas == etc.

              ////////////// Longitude [SMD output format sDDD.ddd; s = "-" if West; + always
              // omitted] ////////////////
              //
              // for format101:
              //   myposition.int_longitude_degrees
              //   myposition.int_longitude_minutes
              //   myposition.longitude_hemisphere
              //
              // extra for IMMT:
              //   myposition.Qc_code
              //   myposition.lolololo_code
              //
              if (number_read_commas == main.LONGITUDE_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between commas at least 1 char
                  {
                    String record_aws_longitude = laatste_record.substring(pos + 1, pos2);

                    if (record_aws_longitude.indexOf("-") != -1) // found "-" in string
                    {
                      myposition.longitude_hemisphere = myposition.HEMISPHERE_WEST; // "West";
                      myposition.longitude_degrees =
                          record_aws_longitude.substring(
                              1, record_aws_longitude.indexOf(".")); // -12.344 -> 12
                    } else {
                      myposition.longitude_hemisphere = myposition.HEMISPHERE_EAST; // "East";
                      myposition.longitude_degrees =
                          record_aws_longitude.substring(
                              0, record_aws_longitude.indexOf(".")); // 12.344 -> 12
                    }

                    // String longitude omzetten naar integer
                    try {
                      myposition.int_longitude_degrees =
                          Integer.parseInt(myposition.longitude_degrees.trim());
                    } catch (NumberFormatException e) {
                    }

                    double decimal_degrees_lon = Math.abs(Double.parseDouble(record_aws_longitude));
                    myposition.int_longitude_minutes =
                        (int)
                            Math.round(
                                (decimal_degrees_lon - Math.floor(decimal_degrees_lon)) * 60);

                    // for IMMT
                    //
                    int int_longitude_minutes_6 =
                        myposition.int_longitude_minutes
                            / 6; // devide the minutes by six and disregard the remainder
                    String longitude_minutes_6 =
                        Integer.toString(int_longitude_minutes_6); // convert to String

                    myposition.lolololo_code =
                        myposition.longitude_degrees.trim().replaceFirst("^0+(?!$)", "")
                            + longitude_minutes_6;

                    int len = 4; // always 4 chars
                    if (myposition.lolololo_code.length() < len) // pad on left with zeros
                    {
                      myposition.lolololo_code =
                          "0000000000".substring(0, len - myposition.lolololo_code.length())
                              + myposition.lolololo_code;
                    }

                    // myposition.Qc_code (must come after latitude_hemisphere and
                    // longitude_hemisphere is determined)
                    if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true)
                        && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST)
                            == true)) {
                      myposition.Qc_code = "1";
                    } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH)
                            == true)
                        && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST)
                            == true)) {
                      myposition.Qc_code = "3";
                    } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH)
                            == true)
                        && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST)
                            == true)) {
                      myposition.Qc_code = "5";
                    } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH)
                            == true)
                        && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST)
                            == true)) {
                      myposition.Qc_code = "7";
                    }
                  } // if (pos2 - pos >= 2)
                }
              } // if (number_read_commas == etc.

              //////////////// Course Over Ground (COG) [SMD output format CCC.c] ////////////////
              //
              // for format101:
              //   myposition.Ds_code
              //
              if (number_read_commas == main.COG_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between commas at least 1 char
                  {
                    String record_aws_COG = laatste_record.substring(pos + 1, pos2);

                    int int_COG = (int) Math.round(Double.parseDouble(record_aws_COG));

                    if (int_COG == 0) {
                      int_COG = 360;
                    }

                    // determine code figure course
                    //
                    // NB "stationary" not possible with AWS COG reporting (so no myposition.Ds_code
                    // = "0";)
                    if (int_COG >= 23 && int_COG <= 67) // NE
                    {
                      myposition.Ds_code = "1";
                    } else if (int_COG >= 68 && int_COG <= 112) // E
                    {
                      myposition.Ds_code = "2";
                    } else if (int_COG >= 113 && int_COG <= 157) // SE
                    {
                      myposition.Ds_code = "3";
                    } else if (int_COG >= 158 && int_COG <= 202) // S
                    {
                      myposition.Ds_code = "4";
                    } else if (int_COG >= 203 && int_COG <= 247) // SW
                    {
                      myposition.Ds_code = "5";
                    } else if (int_COG >= 248 && int_COG <= 292) // W
                    {
                      myposition.Ds_code = "6";
                    } else if (int_COG >= 293 && int_COG <= 337) // NW
                    {
                      myposition.Ds_code = "7";
                    } else if ((int_COG >= 338 && int_COG <= 360)
                        || (int_COG >= 0 && int_COG <= 022)) // N
                    {
                      myposition.Ds_code = "8";
                    } else {
                      myposition.Ds_code =
                          "9"; // no information (could be Ds_code = "/" also, because two options
                      // to code Ds undefined)
                    }

                    // IMMT
                    //
                    // NB there is no 'stationary' in case of COG reporting in SMD output format
                    if (int_COG <= 9) {
                      mywind.COG_code = "00" + Integer.toString(int_COG); // 3 char
                    } else if (int_COG >= 10 && int_COG <= 99) {
                      mywind.COG_code = "0" + Integer.toString(int_COG); // 3 char
                    } else if (int_COG >= 100 && int_COG <= 360) {
                      mywind.COG_code = Integer.toString(int_COG); // 3 char
                    } else {
                      mywind.COG_code = "///"; // 3 char
                    }
                  } // if (pos2 - pos >= 2)
                } // if (record.length() > number_read_commas + 12)
              } // if (number_read_commas == etc.

              ///////////////// Speed Over Ground (SOG) [SMD output format SS.s] ////////////////
              //
              // for format101:
              //   myposition.vs_code
              //
              if (number_read_commas == main.SOG_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char
                  {
                    String record_aws_SOG = laatste_record.substring(pos + 1, pos2); // in m/s
                    double double_SOG = Double.parseDouble(record_aws_SOG); // in m/s
                    int int_SOG =
                        (int) Math.round(double_SOG * owner.M_S_TO_KNOTS); // in knots rounded
                    myposition.SOG_APR_wind =
                        double_SOG
                            * owner
                                .M_S_TO_KNOTS; // in knots not rounded; NB SOG_APR_wind in AWSR will
                    // be
                    // used only if the port mode option is set (= cancel
                    // APR/AWSR uploads is ship speed < 0.5 knot)

                    // determine code figure speed
                    if (int_SOG < 1) {
                      myposition.vs_code = "0";
                    } else if (int_SOG >= 1 && int_SOG <= 5) {
                      myposition.vs_code = "1";
                    } else if (int_SOG >= 6 && int_SOG <= 10) {
                      myposition.vs_code = "2";
                    } else if (int_SOG >= 11 && int_SOG <= 15) {
                      myposition.vs_code = "3";
                    } else if (int_SOG >= 16 && int_SOG <= 20) {
                      myposition.vs_code = "4";
                    } else if (int_SOG >= 21 && int_SOG <= 25) {
                      myposition.vs_code = "5";
                    } else if (int_SOG >= 26 && int_SOG <= 30) {
                      myposition.vs_code = "6";
                    } else if (int_SOG >= 31 && int_SOG <= 35) {
                      myposition.vs_code = "7";
                    } else if (int_SOG >= 36 && int_SOG <= 40) {
                      myposition.vs_code = "8";
                    } else if (int_SOG > 40) {
                      myposition.vs_code = "9";
                    } else {
                      myposition.vs_code = "/";
                    }

                    // IMMT
                    //
                    if (int_SOG <= 9) {
                      mywind.SOG_code = "0" + Integer.toString(int_SOG); // 2 char
                    } else if (int_SOG >= 10 && int_SOG <= 99) {
                      mywind.SOG_code = Integer.toString(int_SOG); // 2 char
                    } else {
                      mywind.SOG_code = "//"; // 2 char
                    }
                  } // if (pos2 - pos >= 2)
                } // if (record.length() > number_read_commas + 12)
              } // if (number_read_commas == etc.

              ///////////////// max height deck cargo (SLL only for IMMT) ///////////////////
              //
              //
              //
              //
              int int_max_height_deck_cargo = main.INVALID;
              try {
                int_max_height_deck_cargo = Integer.parseInt(main.max_height_deck_cargo.trim());
              } catch (NumberFormatException e) {
                int_max_height_deck_cargo = main.INVALID;
              }

              if (int_max_height_deck_cargo <= 9) {
                mywind.SLL_code = "0" + Integer.toString(int_max_height_deck_cargo); // 2 char
              } else if (int_max_height_deck_cargo >= 10 && int_max_height_deck_cargo <= 99) {
                mywind.SLL_code = Integer.toString(int_max_height_deck_cargo); // 2 char
              } else {
                mywind.SLL_code = "//"; // 2 char
              }

              // NB
              // because in a automated situation (AWSR) most probable the diff. sll-wl will never
              // be verified/updated (on the wind input page)
              // the sl_code(IMMT) and hh_code (IMMT) and mywind.int_difference_sll_wl (format101)
              // will be set to 'not preset'

              // forma101:
              mywind.int_difference_sll_wl = main.INVALID;

              // IMMT
              //
              mywind.sl_code = "/"; // 1 char
              mywind.hh_code = "//"; // 2 char

              //////////////////// True heading [SMD output format HHH.h] ///////////////////
              //
              // for format101:
              //   mywind.ship_heading
              //
              if (number_read_commas == main.HEADING_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between commas at least 1 char
                  {
                    String record_aws_ship_heading = laatste_record.substring(pos + 1, pos2);
                    double double_ship_heading = Double.parseDouble(record_aws_ship_heading);

                    int int_ship_heading = (int) Math.round(double_ship_heading);

                    if (int_ship_heading == 0) {
                      int_ship_heading = 360;
                    }

                    mywind.ship_heading = Integer.toString(int_ship_heading);

                    // IMMT
                    //
                    if (int_ship_heading <= 9) {
                      mywind.HDG_code = "00" + Integer.toString(int_ship_heading); // 3 char
                    } else if (int_ship_heading >= 10 && int_ship_heading <= 99) {
                      mywind.HDG_code = "0" + Integer.toString(int_ship_heading); // 3 char
                    } else if (int_ship_heading >= 100 && int_ship_heading <= 360) {
                      mywind.HDG_code = Integer.toString(int_ship_heading); // 3 char
                    } else {
                      mywind.HDG_code = "///"; // 3 char
                    }
                  } // if (pos2 - pos >= 2)
                } // if (record.length() > number_read_commas + 12)
              } // if (number_read_commas == etc.

              /////////////////////// air pressure at sensor height (read) [SMD output format
              // PPPP.p] ////////////////////
              //
              // for format101:
              //   (main.pressure_reading_msl_yes_no)
              //   mybarometer.pressure_reading_corrected
              //
              //
              //
              if (number_read_commas == main.PRESSURE_SENSOR_HEIGHT_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char
                  {
                    mybarometer.pressure_reading_corrected =
                        laatste_record.substring(pos + 1, pos2);

                    // by default for pressure value obtained from AWS!
                    // mybarometer.pressure_reading = mybarometer.pressure_reading_corrected;

                    // NB ic = 0.0 by default for pressure value obtained from AWS!

                    // main.barometer_instrument_correction = "0.0";
                  } // if (pos2 - pos >= 2)
                } // if (record.length() > number_read_commas + 12)
              } // if (number_read_commas == etc.

              /////////////////// air pressure MSL [SMD output format PPPP.p] /////////////////////
              //
              // for format101:
              //   mybarometer.pressure_msl_corrected
              //
              // IMMT:
              //   mybarometer.PPPP_code
              //
              if (number_read_commas == main.PRESSURE_MSL_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char
                  {
                    double double_pressure_msl_corrected = main.INVALID;

                    mybarometer.pressure_msl_corrected = laatste_record.substring(pos + 1, pos2);

                    // NB ic = 0.0 by default for pressure value obtained from AWS!

                    // IMMT
                    try {
                      double_pressure_msl_corrected =
                          Double.parseDouble(mybarometer.pressure_msl_corrected.trim());
                    } catch (NumberFormatException e) {
                      double_pressure_msl_corrected = main.INVALID;
                    }

                    if (double_pressure_msl_corrected != main.INVALID) {
                      mybarometer.PPPP_code =
                          PressureCodeFormatter.format(double_pressure_msl_corrected);
                    } // if (double_pressure_msl != INVALID)
                  } // if (pos2 - pos >= 2)
                } // if (record.length() > number_read_commas + 12)
              } // if (number_read_commas == etc.

              ///////////////// air pressure tendency over three three hours [SMD output format
              // sPP.p] ///////////////////////
              //
              // for format101:
              //   mybarograph.pressure_amount_tendency
              //
              if (number_read_commas == main.PRESSURE_TENDENCY_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char
                  {
                    double double_pressure_amount_tendency = main.INVALID;

                    mybarograph.pressure_amount_tendency = laatste_record.substring(pos + 1, pos2);

                    // IMMT
                    //
                    try {
                      double_pressure_amount_tendency =
                          Double.parseDouble(mybarograph.pressure_amount_tendency.trim());
                    } catch (NumberFormatException e) {
                      double_pressure_amount_tendency = main.INVALID;
                    }

                    if (double_pressure_amount_tendency != main.INVALID) {
                      mybarograph.ppp_code =
                          PressureTendencyCodeFormatter.format(double_pressure_amount_tendency);
                    } // if (double_pressure_amount_tendency != INVALID)
                  } // if (pos2 - pos >= 2)
                } // if (record.length() > number_read_commas + 12)
              } // if (number_read_commas == etc.

              ////////////////// air pressure characteristic [SMD output format A] ///////////////
              //
              // for format101:
              //   mybarograph.a_code
              //
              //
              if (number_read_commas == main.PRESSURE_CHARACTERISTIC_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char
                  {
                    mybarograph.a_code = laatste_record.substring(pos + 1, pos2);
                  } // if (pos2 - pos >= 2)
                } // if (record.length() > number_read_commas + 12)
              } // if (number_read_commas == etc.

              //////////////////////// air temp [SMD output format sTA.a] /////////////
              //
              // for format101:
              //   mytemp.air_temp
              //
              // extra for IMMT:
              //   mytemp.sn_TTT_code (1 char)
              //   mytemp.TTT_code  (3 chr)
              //
              if (number_read_commas == main.AIR_TEMP_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char
                  {
                    mytemp.air_temp = laatste_record.substring(pos + 1, pos2);

                    // for IMMT
                    //
                    double double_air_temp = main.INVALID;
                    try {
                      double_air_temp = Double.parseDouble(mytemp.air_temp.trim());
                    } catch (NumberFormatException e) {
                      double_air_temp = main.INVALID;
                    }

                    if (double_air_temp != main.INVALID) {
                      ImmtAirTemperatureCodeFormatter.Result code =
                          ImmtAirTemperatureCodeFormatter.format(double_air_temp);
                      mytemp.sn_TTT_code = code.signCode;
                      mytemp.TTT_code = code.temperatureCode;

                      // JOptionPane.showMessageDialog(null, TTT_code, main.APPLICATION_NAME + " TTT
                      // code", JOptionPane.WARNING_MESSAGE);
                    } // if (float_air_temp != INVALID)
                  } // if (pos2 - pos >= 2)
                } // if (laatste_record.length() > pos + 1 + 12)
              } // if (number_read_commas == etc.

              //////////////////////// relative humidity [SMD output format UUU.u] ///////////////
              //
              // for format101:
              //   mytemp.double_rv
              //
              // fot IMMT:
              //   RH (4 char)
              //   RHi (1 char)
              //
              if (number_read_commas == main.HUMIDITY_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char (e.g. "87.5")
                  {
                    try {
                      mytemp.double_rv =
                          Double.parseDouble(laatste_record.substring(pos + 1, pos2)) / 100;
                    } catch (NumberFormatException exception) {
                      mytemp.double_rv = main.INVALID;
                    }
                    // NB "/100" for consistency with not AWS computed rh (see also
                    // main.temperatures_fields_update() and mytemp.OK_button_actioPerformed())

                    // IMMT
                    //
                    // NB computing RH and RHi will be done in IMMT_log() [main.java]

                  } // if (pos2 - pos >= 2)
                } // if (laatste_record.length() > pos + 1 + 12)
              } // if (number_read_commas == etc.

              /////////////////////////// SST [SMD output format sTW.w] ////////////////////
              //
              // for format101:
              //    mytemp.sea_water_temp
              //
              // for IMMT:
              //    ss_TsTsTs_code (1 char)
              //    TstsTs_code (3 char)
              //    immt_ss_indicator (1 char)
              //
              if (number_read_commas == main.SST_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between conmmas at least 1 char (e.g. 15.4)
                  {
                    mytemp.sea_water_temp = laatste_record.substring(pos + 1, pos2);

                    // IMMT
                    //
                    double double_sea_water_temp = main.INVALID;
                    try {
                      double_sea_water_temp = Double.parseDouble(mytemp.sea_water_temp.trim());
                    } catch (NumberFormatException e) {
                      double_sea_water_temp = main.INVALID;
                    }

                    if (double_sea_water_temp != main.INVALID) {
                      // ss of TsTsTs and sst indicator (IMMT only)
                      if (double_sea_water_temp < 0.0) {
                        if (main.sst_exposure.equals(main.INTAKE) == true) {
                          mytemp.ss_TsTsTs_code =
                              "1"; // for obs and indirect also for immt (Sn calculation)
                          mytemp.immt_sst_indicator =
                              "1"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.BUCKET) == true) {
                          mytemp.ss_TsTsTs_code = "3"; // for obs and indirect also for immt
                          mytemp.immt_sst_indicator =
                              "0"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.HULL_CONTACT_SENSOR) == true) {
                          mytemp.ss_TsTsTs_code = "5"; // for obs and indirect also for immt
                          mytemp.immt_sst_indicator =
                              "3"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.TRAILING_THERMISTOR) == true) {
                          mytemp.ss_TsTsTs_code =
                              "7"; // trailing thermistor not for obs but for IMMT
                          mytemp.immt_sst_indicator =
                              "2"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.THROUGH_HULL_SENSOR) == true) {
                          mytemp.ss_TsTsTs_code =
                              "7"; // through hull sensor not for obs but for IMMT
                          mytemp.immt_sst_indicator =
                              "4"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.RADIATION_THERMOMETER) == true) {
                          mytemp.ss_TsTsTs_code =
                              "7"; // radiation thermometer not for obs but for IMMT
                          mytemp.immt_sst_indicator =
                              "5"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.BAIT_TANKS_THERMOMETER) == true) {
                          mytemp.ss_TsTsTs_code =
                              "7"; // bait tanks thermometer not for obs but for IMMT
                          mytemp.immt_sst_indicator =
                              "6"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.OTHER) == true) {
                          mytemp.ss_TsTsTs_code = "7"; // for obs and indirect also for immt
                          mytemp.immt_sst_indicator =
                              "7"; // only for immt log not for operational obs
                        }
                      } // if (double_sea_water_temp < 0.0
                      else // sst >= 0
                      {
                        if (main.sst_exposure.equals(main.INTAKE) == true) {
                          mytemp.ss_TsTsTs_code = "0";
                          mytemp.immt_sst_indicator =
                              "1"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.BUCKET) == true) {
                          mytemp.ss_TsTsTs_code = "2";
                          mytemp.immt_sst_indicator =
                              "0"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.HULL_CONTACT_SENSOR) == true) {
                          mytemp.ss_TsTsTs_code = "4";
                          mytemp.immt_sst_indicator =
                              "3"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.TRAILING_THERMISTOR) == true) {
                          mytemp.ss_TsTsTs_code = "6";
                          mytemp.immt_sst_indicator =
                              "2"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.THROUGH_HULL_SENSOR) == true) {
                          mytemp.ss_TsTsTs_code = "6";
                          mytemp.immt_sst_indicator =
                              "4"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.RADIATION_THERMOMETER) == true) {
                          mytemp.ss_TsTsTs_code = "6";
                          mytemp.immt_sst_indicator =
                              "5"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.BAIT_TANKS_THERMOMETER) == true) {
                          mytemp.ss_TsTsTs_code = "6";
                          mytemp.immt_sst_indicator =
                              "6"; // only for immt log not for operational obs
                        } else if (main.sst_exposure.equals(main.OTHER) == true) {
                          mytemp.ss_TsTsTs_code = "6";
                          mytemp.immt_sst_indicator =
                              "7"; // only for immt log not for operational obs
                        }
                      } // else (sst >= 0)

                      // TsTsTs (IMMT only)
                      int num_TsTsTs_code =
                          (int) Math.floor(Math.abs(double_sea_water_temp) * 10 + 0.5);
                      mytemp.TsTsTs_code =
                          Integer.toString(
                              num_TsTsTs_code); // convert to string      // or (alternative):
                      // Hw_code = String.valueOf(num_Hw_code);

                      // NB TsTsTs_code always 3 characters width e.g. 127 (accomplish via
                      // construction below)
                      int len = 3;
                      if (mytemp.TsTsTs_code.length() < len) {
                        // pad on left with zeros
                        mytemp.TsTsTs_code =
                            "0000000000".substring(0, len - mytemp.TsTsTs_code.length())
                                + mytemp.TsTsTs_code;
                      }
                    } // if (float_sea_water != INVALID)
                  } // if (pos2 - pos >= 2)
                } // if (laatste_record.length() > pos + 1 + 12)
              } // if (number_read_commas == etc.

              /////////////////////// relative wind speed [SMD output format WS.r]
              // //////////////////
              //
              // for format101:
              //   mywind.RWS_code
              //   (main.wind_units)
              //
              if (number_read_commas == main.RELATIVE_WIND_SPEED_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between commas at least 1 char
                  {
                    String record_aws_relative_wind_speed = laatste_record.substring(pos + 1, pos2);
                    Double double_relative_wind_speed =
                        Double.parseDouble(record_aws_relative_wind_speed);

                    // round to integer (NB already in m/s)

                    // NB in this stage always m/s (by default in case of AWS SMD ouput data)
                    mywind.int_relative_wind_speed = (int) Math.round(double_relative_wind_speed);

                    if (mywind.int_relative_wind_speed <= 9) {
                      mywind.RWS_code =
                          "00" + Integer.toString(mywind.int_relative_wind_speed); // 3 char
                    } else if (mywind.int_relative_wind_speed >= 10
                        && mywind.int_relative_wind_speed <= 99) {
                      mywind.RWS_code =
                          "0" + Integer.toString(mywind.int_relative_wind_speed); // 3 char
                    } else if (mywind.int_relative_wind_speed >= 100
                        && mywind.int_relative_wind_speed <= 300) {
                      mywind.RWS_code =
                          Integer.toString(
                              mywind.int_relative_wind_speed); // 3 char // so now in m/s or knots
                      // depending var. "main.wind_units"
                    } else {
                      mywind.RWS_code = "///"; // 3 char
                    }
                  } // if (pos2 - pos >= 2)
                } //
              } // if (number_read_commas == etc.

              ///////////////////// relative wind direction [SMD output format WDR.r]
              // //////////////////
              //
              // for format101:
              //   mywind.RWD_code
              //
              if (number_read_commas == main.RELATIVE_WIND_DIR_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between commas at least 1 char
                  {
                    String record_aws_relative_wind_dir = laatste_record.substring(pos + 1, pos2);
                    double double_relative_wind_dir =
                        Double.parseDouble(record_aws_relative_wind_dir);
                    mywind.int_relative_wind_dir = (int) Math.round(double_relative_wind_dir);

                    if (mywind.int_relative_wind_dir
                        == mywind
                            .WIND_DIR_VARIABLE) // variable (I think this will never met, but it
                    // makes no difference)
                    {
                      mywind.RWD_code =
                          "999"; // not in IMMT description but possible in MQCS (in IMMT no entry
                      // for variable mmeasured wind)
                    } else if (mywind.int_relative_wind_dir <= 9) {
                      mywind.RWD_code =
                          "00" + Integer.toString(mywind.int_relative_wind_dir); // 3 char
                    } else if (mywind.int_relative_wind_dir >= 10
                        && mywind.int_relative_wind_dir <= 99) {
                      mywind.RWD_code =
                          "0" + Integer.toString(mywind.int_relative_wind_dir); // 3 char
                    } else if (mywind.int_relative_wind_dir >= 100
                        && mywind.int_relative_wind_dir <= 360) {
                      mywind.RWD_code = Integer.toString(mywind.int_relative_wind_dir); // 3 char
                    } else {
                      mywind.RWD_code = "///"; // 3 char
                    }
                  } // if (pos2 - pos >= 2)
                }
              } // if (number_read_commas == etc.

              ///////////////////// true wind speed [SMD output format WS.t in m/s]
              // /////////////////////
              //
              // for format101:
              //   mywind.int_true_wind_speed
              //   (main.wind_units)
              //
              // IMMT:
              //   mywind.ff_code
              //   mywind.iw_code
              //
              if (number_read_commas == main.TRUE_WIND_SPEED_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between commas at least 1 char
                  {
                    String record_aws_true_wind_speed = laatste_record.substring(pos + 1, pos2);
                    Double double_true_wind_speed = Double.parseDouble(record_aws_true_wind_speed);

                    // NB in this stage it is always m/s
                    mywind.int_true_wind_speed = (int) Math.round(double_true_wind_speed);

                    // IMMT
                    //
                    // NB in case of AWS: by default wind speed units indicator = m/s was set and
                    // checked [mystationdata.java] (iw = 1)
                    mywind.iw_code =
                        "1"; // by defaut for AWS saved sensor data (see SMD output format)

                    if (mywind.int_true_wind_speed <= 9) {
                      mywind.ff_code = "0" + Integer.toString(mywind.int_true_wind_speed);
                    } else if ((mywind.int_true_wind_speed >= 10)
                        && (mywind.int_true_wind_speed <= 99)) {
                      mywind.ff_code = Integer.toString(mywind.int_true_wind_speed);
                    }
                  } // if (pos2 - pos >= 2)
                } //
              } // if (number_read_commas == etc.

              /////////////////// true wind direction [SMD output format WDT.t] /////////////////
              //
              // for format101:
              //   mywind.int_true_wind_dir
              //
              // IMMT:
              //   mywind.dd_code
              //
              if (number_read_commas == main.TRUE_WIND_DIR_COMMA_NUMBER) {
                if (laatste_record.length()
                    > pos + 1
                        + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                {
                  int pos2 = laatste_record.indexOf(",", pos + 1);
                  if (pos2 - pos >= 2) // between commas at least 1 char
                  {
                    String record_aws_true_wind_dir = laatste_record.substring(pos + 1, pos2);
                    double double_true_wind_dir = Double.parseDouble(record_aws_true_wind_dir);

                    int int_true_wind_dir = (int) Math.round(double_true_wind_dir);

                    if (int_true_wind_dir == 0) {
                      int_true_wind_dir = 360;
                    }

                    mywind.int_true_wind_dir = int_true_wind_dir;

                    // IMMT
                    //
                    int int_hulp_true_wind_dir;
                    int int_hulp_dd_code;

                    // 1 - 4 degrees -> 360 deg
                    if (int_true_wind_dir >= 1 && int_true_wind_dir <= 4) {
                      int_hulp_true_wind_dir = 360;
                    } else {
                      int_hulp_true_wind_dir = int_true_wind_dir;
                    }

                    // rounding to tens
                    int_hulp_dd_code = (int) Math.round((float) int_hulp_true_wind_dir / 10);

                    // coding
                    if (int_hulp_dd_code <= 9) {
                      mywind.dd_code = "0" + Integer.toString(int_hulp_dd_code);
                    } else if ((int_hulp_dd_code >= 10) && (int_hulp_dd_code <= 36)) {
                      mywind.dd_code = Integer.toString(int_hulp_dd_code);
                    }
                  } // if (pos2 - pos >= 2)
                } // if (laatste_record.length() > pos + 1 + 12)
              } // if (number_read_commas == etc.
            } // if (pos > 0)
          } while (pos > 0);
        } //  if (doorgaan_in_record == true && laatste_record != null)

        if (doorgaan_in_record) {
          // AWS supplies air temperature and RH separately.  Unlike the manual temperature form,
          // this parser does not pass through mytemp.OK_button_actionperformed(), so calculate the
          // dew point explicitly before the FM13 composer reads the code fields.
          DewPointStateUpdater.updateFromAwsValues();
        }

        // clear memory
        // check_presence_parameter_system_datum_tijd    = null;

        // set and check the date time parameters for format101 obs and IMMT
        set_date_time_record_ok =
            owner.RS422_Set_Date_Time_Obs_And_IMMT_For_AWSR(record_aws_date, record_aws_time);
        check_date_time_record_ok =
            owner.RS422_Check_Date_Time_Last_Saved_Record_For_AWSR(
                record_aws_date, record_aws_time);

        // send the data
        if (check_date_time_record_ok && set_date_time_record_ok) {
          main_RS232_RS422.RS232_APR_AWSR_send(retry); // so also applicable for RS422 of OMC-140
        } else {
          main.Reset_all_meteo_parameters(); // to be sure all the (possible) manual inserted data
          // will be cleared
        }
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  } // private void RS422_Read_And_Send_Sensor_Data_For_AWSR(final boolean retry)
}
