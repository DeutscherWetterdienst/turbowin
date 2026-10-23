package turbowin;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

final class Rs422ReadAwsSensorDataForDisplayWorkflow {

  private final main_RS232_RS422 owner;
  private GregorianCalendar check_presence_parameter_system_datum_tijd;
  private int system_minute;

  Rs422ReadAwsSensorDataForDisplayWorkflow(main_RS232_RS422 owner) {
    this.owner = owner;
  }

  void start() {
    // check the presence of the various meteo parameters in the data received from AWS (data stored
    // by TurboWin+ in sensor data files)

    // called from: Function RS422_V3_write_sensor_data_to_file() [file main_RS232_RS422.java]
    //

    // check_presence_parameter_system_datum_tijd = new GregorianCalendar(new SimpleTimeZone(0,
    // "UTC")); // system date time in UTC of this moment
    check_presence_parameter_system_datum_tijd =
        new GregorianCalendar(); // UTC at this momnet not important (only retrieving minutes)
    check_presence_parameter_system_datum_tijd.getTime(); // now effective

    // int system_minute = check_presence_parameter_system_datum_tijd.get(Calendar.MINUTE);
    system_minute = check_presence_parameter_system_datum_tijd.get(Calendar.MINUTE);

    // initialisation
    main_RS232_RS422.RS422_initialise_AWS_Sensor_Data_For_Display();

    // only check every 1 or 5 minutes? [eg if (system_minute % 5 == 0)]
    if (system_minute % 1 == 0) {
      // System.out.println("+++ checking parameter presence in sensor data file");

      new SwingWorker<String, Void>() {
        @Override
        protected String doInBackground() throws Exception {
          File sensor_data_file;
          String last_record = null;

          if (system_minute != 0) {
            // otherwise at 00 minutes the system is looking in an 'old' file because of the -1
            // MINUTE (without the 00 minutes sensor obs)
            check_presence_parameter_system_datum_tijd.add(Calendar.MINUTE, -1);
          }

          String sensor_data_file_naam_datum_tijd_deel =
              main.sdf3.format(
                  check_presence_parameter_system_datum_tijd
                      .getTime()); // -> eg 2013020308 // sdf3 was set in RS422_initComponents() to
          // UTC
          String sensor_data_file_name =
              "sensor_data_" + sensor_data_file_naam_datum_tijd_deel + ".txt";

          // first check if there is a sensor data file present (and not empty)
          String volledig_path_sensor_data =
              main.logs_dir + java.io.File.separator + sensor_data_file_name;

          // System.out.println("+++ checking file: " + volledig_path_sensor_data);

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
              System.out.println("--- Function RS422_Read_AWS_Sensor_Data_For_Display(): " + ex);
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

          String laatste_record = null;
          try {
            laatste_record = get();
          } catch (InterruptedException | ExecutionException ex) {
            String message =
                "[AWS] error retrieving AWS data from sensor data file; " + ex.toString();
            main.log_turbowin_system_message(message);
          }

          if (laatste_record == null) {
            String message = "[AWS] no sensor data file exists or file empty";
            main.log_turbowin_system_message(message);
            doorgaan_in_record = false;
          } else if (laatste_record.equals("")) {
            String message =
                "[AWS] sensor data file found but no records found or misformed records";
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
              pos = laatste_record.indexOf(",", pos + 1); // searching "," from position "pos + 1"
              if (pos > 0) // "," found
              {
                number_read_commas++;

                // Date
                //
                if (number_read_commas == main.DATE_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between conmmas at least 1 char
                    {
                      String record_aws_date =
                          laatste_record.substring(pos + 1, pos2); // YYYYMMDD or YYMMDD

                      // mydatetime.day   = record_aws_date.substring(record_aws_date.length() -2) +
                      // " -";   // DD
                      // mydatetime.month = record_aws_date.substring(record_aws_date.length() -4,
                      // record_aws_date.length() -2) + " -"; // MM
                      mydatetime.day =
                          record_aws_date.substring(record_aws_date.length() - 2); // DD

                      if (mydatetime.day.substring(0, 1).equals("0")) {
                        // otherwise will not match in the mydatetime input form
                        mydatetime.day = mydatetime.day.substring(1, 2);
                      }

                      // System.out.println("+++ day = " + mydatetime.day);
                      String str_month =
                          record_aws_date.substring(
                              record_aws_date.length() - 4, record_aws_date.length() - 2); // MM

                      // System.out.println("+++ month = " + str_month);
                      switch (str_month) {
                        case "01":
                          mydatetime.month = "January";
                          break;
                        case "02":
                          mydatetime.month = "February";
                          break;
                        case "03":
                          mydatetime.month = "March";
                          break;
                        case "04":
                          mydatetime.month = "April";
                          break;
                        case "05":
                          mydatetime.month = "May";
                          break;
                        case "06":
                          mydatetime.month = "June";
                          break;
                        case "07":
                          mydatetime.month = "July";
                          break;
                        case "08":
                          mydatetime.month = "August";
                          break;
                        case "09":
                          mydatetime.month = "September";
                          break;
                        case "10":
                          mydatetime.month = "October";
                          break;
                        case "11":
                          mydatetime.month = "November";
                          break;
                        case "12":
                          mydatetime.month = "December";
                          break;
                        default:
                          mydatetime.month = "unknown";
                          break;
                      }

                      mydatetime.year =
                          record_aws_date.substring(
                              0, record_aws_date.length() - 4); // YYYY (or YY)

                      main.date_from_AWS_present = true;
                      // System.out.println("+++ parameter date is aanwezig");

                    } // if (pos2 - pos >= 2)
                  }

                } // if (number_read_commas == etc.
                // Time
                //
                else if (number_read_commas == main.TIME_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between conmmas at least 1 char
                    {
                      String record_aws_time = laatste_record.substring(pos + 1, pos2);
                      if (record_aws_time.length() == 6) // HHMMSS
                      {
                        mydatetime.hour = record_aws_time.substring(0, 2); // HH from HHMMSS
                        mydatetime.minute = record_aws_time.substring(2, 4); // MM from HHMMSS
                      }

                      if (mydatetime.hour.substring(0, 1).equals("0")) {
                        // otherwise will not match in the mydatetime input form
                        mydatetime.hour = mydatetime.hour.substring(1, 2);
                      }

                      main.time_from_AWS_present = true;
                      // System.out.println("+++ parameter time is aanwezig");

                    } // if (pos2 - pos >= 2)
                  }
                } // else if (number_read_commas == etc.
                // Latitude [format sDD.ddd; s = "-" if South; + always omitted]
                //
                else if (number_read_commas == main.LATITUDE_COMMA_NUMBER) {
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
                        myposition.latitude_hemisphere = "South";
                        myposition.latitude_degrees =
                            record_aws_latitude.substring(
                                1, record_aws_latitude.indexOf(".")); // -12.344 -> 12
                      } else {
                        myposition.latitude_hemisphere = "North";
                        myposition.latitude_degrees =
                            record_aws_latitude.substring(
                                0, record_aws_latitude.indexOf(".")); // 12.344 -> 12
                      }

                      // double decimal_degrees_lat = Double.parseDouble(record_aws_latitude);
                      double decimal_degrees_lat =
                          Math.abs(Double.parseDouble(record_aws_latitude));

                      // double double_minutes_lat = (decimal_degrees_lat -
                      // Math.floor(decimal_degrees_lat)) * 60;
                      //
                      // BigDecimal bd = new BigDecimal(double_minutes_lat).setScale(1,
                      // RoundingMode.HALF_UP);  // one decimal, rounded e.g. 2.12939 -> 2.1
                      // double_minutes_lat = bd.doubleValue();
                      //
                      // if (double_minutes_lat < 10)
                      // {
                      //   myposition.latitude_minutes = "0" + Double.toString(double_minutes_lat);
                      // }
                      // else
                      // {
                      //   myposition.latitude_minutes = Double.toString(double_minutes_lat);
                      // }
                      int int_minutes_lat =
                          (int)
                              Math.round(
                                  (decimal_degrees_lat - Math.floor(decimal_degrees_lat)) * 60);
                      if (int_minutes_lat < 10) {
                        myposition.latitude_minutes = "0" + Integer.toString(int_minutes_lat);
                      } else {
                        myposition.latitude_minutes = Integer.toString(int_minutes_lat);
                      }

                      main.latitude_from_AWS_present = true;
                      // System.out.println("+++ parameter latitude is aanwezig");

                    } // if (pos2 - pos >= 2)
                  } // if (laatste_record.length() etc.
                } // else if (number_read_commas == etc.
                // Longitude [format sDDD.ddd; s = "-" if West; + always omitted]
                //
                else if (number_read_commas == main.LONGITUDE_COMMA_NUMBER) {
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
                        myposition.longitude_hemisphere = "West";
                        myposition.longitude_degrees =
                            record_aws_longitude.substring(
                                1, record_aws_longitude.indexOf(".")); // -12.344 -> 12
                      } else {
                        myposition.longitude_hemisphere = "East";
                        myposition.longitude_degrees =
                            record_aws_longitude.substring(
                                0, record_aws_longitude.indexOf(".")); // 12.344 -> 12
                      }

                      double decimal_degrees_lon =
                          Math.abs(Double.parseDouble(record_aws_longitude));

                      // double double_minutes_lon = (decimal_degrees_lon -
                      // Math.floor(decimal_degrees_lon)) * 60;
                      //
                      // BigDecimal bd = new BigDecimal(double_minutes_lon).setScale(1,
                      // RoundingMode.HALF_UP);  // one decimal, rounded e.g. 2.12939 -> 2.1
                      // double_minutes_lon = bd.doubleValue();
                      //
                      // if (double_minutes_lon < 10)
                      // {
                      //   myposition.longitude_minutes = "0" + Double.toString(double_minutes_lon);
                      // }
                      // else
                      // {
                      //   myposition.longitude_minutes = Double.toString(double_minutes_lon);
                      // }
                      int int_minutes_lon =
                          (int)
                              Math.round(
                                  (decimal_degrees_lon - Math.floor(decimal_degrees_lon)) * 60);
                      if (int_minutes_lon < 10) {
                        myposition.longitude_minutes = "0" + Integer.toString(int_minutes_lon);
                      } else {
                        myposition.longitude_minutes = Integer.toString(int_minutes_lon);
                      }

                      main.longitude_from_AWS_present = true;
                      // System.out.println("+++ parameter longitude is aanwezig");

                    } // if (pos2 - pos >= 2)
                  } //
                } // else if (number_read_commas == etc.
                // Course Over Ground (COG) [format CCC.c]
                //
                else if (number_read_commas == main.COG_COMMA_NUMBER) {
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

                      // NB if the ship is stationary then COG retrieved from the the AWS will be
                      // blanco -> unfortunately no option to indicate stationary
                      // if (int_COG == 0)
                      // {
                      //   myposition.course = myposition.COURSE_STATIONARY;
                      // }
                      if (int_COG >= 23 && int_COG <= 67) {
                        myposition.course = myposition.COURSE_023_067;
                      } else if (int_COG >= 68 && int_COG <= 112) {
                        myposition.course = myposition.COURSE_068_112;
                      } else if (int_COG >= 113 && int_COG <= 157) {
                        myposition.course = myposition.COURSE_113_157;
                      } else if (int_COG >= 158 && int_COG <= 202) {
                        myposition.course = myposition.COURSE_158_202;
                      } else if (int_COG >= 203 && int_COG <= 247) {
                        myposition.course = myposition.COURSE_203_247;
                      } else if (int_COG >= 248 && int_COG <= 292) {
                        myposition.course = myposition.COURSE_248_292;
                      } else if (int_COG >= 293 && int_COG <= 337) {
                        myposition.course = myposition.COURSE_293_337;
                      } else if (int_COG >= 338 && int_COG <= 360) {
                        myposition.course = myposition.COURSE_338_022;
                      } else if (int_COG > 0 && int_COG <= 22) {
                        myposition.course = myposition.COURSE_338_022;
                      }

                      // COG = 10 min average so can also used for wind input page
                      // mywind.ship_ground_course = record_aws_COG;
                      //
                      // if (mywind.ship_ground_course.equals("0.0"))
                      // {
                      //   mywind.ship_ground_course = "360.0";
                      // }
                      mywind.ship_ground_course = Integer.toString(int_COG);

                      main.COG_from_AWS_present = true;
                      // System.out.println("+++ parameter COG is aanwezig");

                    } // if (pos2 - pos >= 2)
                  } // if (record.length() > number_read_commas + 12)
                } // else if (number_read_commas == etc.
                // Speed Over Ground (SOG) [format SS.s]
                //
                else if (number_read_commas == main.SOG_COMMA_NUMBER) {
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

                      if (int_SOG == 0) {
                        myposition.speed = myposition.SPEED_0;
                      } else if (int_SOG >= 1 && int_SOG <= 5) {
                        myposition.speed = myposition.SPEED_1_5;
                      } else if (int_SOG >= 6 && int_SOG <= 10) {
                        myposition.speed = myposition.SPEED_6_10;
                      } else if (int_SOG >= 11 && int_SOG <= 15) {
                        myposition.speed = myposition.SPEED_11_15;
                      } else if (int_SOG >= 16 && int_SOG <= 20) {
                        myposition.speed = myposition.SPEED_16_20;
                      } else if (int_SOG >= 21 && int_SOG <= 25) {
                        myposition.speed = myposition.SPEED_21_25;
                      } else if (int_SOG >= 26 && int_SOG <= 30) {
                        myposition.speed = myposition.SPEED_26_30;
                      } else if (int_SOG >= 31 && int_SOG <= 35) {
                        myposition.speed = myposition.SPEED_31_35;
                      } else if (int_SOG >= 36 && int_SOG <= 40) {
                        myposition.speed = myposition.SPEED_36_40;
                      } else if (int_SOG >= 41) {
                        myposition.speed = myposition.SPEED_MORE_40;
                      }

                      // SOG 10 min average so can also used for wind input page
                      double_SOG =
                          Math.round(double_SOG * main.M_S_KNOT_CONVERSION * 10)
                              / 10.0; // always round to one decimal
                      mywind.ship_ground_speed = Double.toString(double_SOG);

                      main.SOG_from_AWS_present = true;
                      // System.out.println("+++ parameter SOG is aanwezig");

                    } // if (pos2 - pos >= 2)
                  } // if (record.length() > number_read_commas + 12)
                } // else if (number_read_commas == etc.
                // True heading [format HHH.h]
                //
                else if (number_read_commas == main.HEADING_COMMA_NUMBER) {
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

                      main.true_heading_from_AWS_present = true;
                      // NB heading is not displayed on TurboWin+ main screen (only on wind input
                      // form)

                      // System.out.println("+++ parameter Heading is aanwezig");
                    } // if (pos2 - pos >= 2)
                  } // if (record.length() > number_read_commas + 12)
                } // else if (number_read_commas == etc.
                // air pressure at sensor height (read) [format PPPP.p]
                //
                else if (number_read_commas == main.PRESSURE_SENSOR_HEIGHT_COMMA_NUMBER) {
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
                      mybarometer.pressure_reading = mybarometer.pressure_reading_corrected;

                      // ic = 0.0 by default for pressure value obtained from AWS!
                      main.barometer_instrument_correction = "0.0";

                      // for barometer input form
                      // if (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_NO))
                      // {
                      //   mybarometer.pressure_reading = mybarometer.pressure_reading_corrected;
                      //
                      //   // ic = 0.0 by default for pressure value obtained from AWS!
                      //   main.barometer_instrument_correction = "0.0";
                      // }
                      main.pressure_sensor_level_from_AWS_present = true;
                      // System.out.println("+++ parameter air pressure read is aanwezig");

                    } // if (pos2 - pos >= 2)
                  } // if (record.length() > number_read_commas + 12)
                } // else if (number_read_commas == etc.
                // air pressure MSL [format PPPP.p]
                //
                else if (number_read_commas == main.PRESSURE_MSL_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between conmmas at least 1 char
                    {
                      mybarometer.pressure_msl_corrected = laatste_record.substring(pos + 1, pos2);

                      // for barometer input form
                      // NB if AWS connected, barometer input form will always indicate barometer
                      // reading at sensor level (never MSL)
                      main.pressure_MSL_from_AWS_present = true;
                      // System.out.println("+++ parameter air pressure MSL is aanwezig");

                    } // if (pos2 - pos >= 2)
                  } // if (record.length() > number_read_commas + 12)
                } // else if (number_read_commas == etc.
                // air pressure tendency over three three hours [format sPP.p]
                //
                else if (number_read_commas == main.PRESSURE_TENDENCY_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between conmmas at least 1 char
                    {
                      mybarograph.pressure_amount_tendency =
                          laatste_record.substring(pos + 1, pos2);

                      main.pressure_tendency_from_AWS_present = true;
                      // System.out.println("+++ parameter air pressure tendency is aanwezig");

                    } // if (pos2 - pos >= 2)
                  } // if (record.length() > number_read_commas + 12)
                } // else if (number_read_commas == etc.
                // air pressure characteristic [format A]
                //
                else if (number_read_commas == main.PRESSURE_CHARACTERISTIC_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between conmmas at least 1 char
                    {
                      mybarograph.a_code = laatste_record.substring(pos + 1, pos2);

                      main.pressure_characteristic_from_AWS_present = true;
                      // System.out.println("+++ parameter air pressure characteristic (a code) is
                      // aanwezig");

                    } // if (pos2 - pos >= 2)
                  } // if (record.length() > number_read_commas + 12)
                } // else if (number_read_commas == etc.
                // air temp [format sTA.a]
                //
                else if (number_read_commas == main.AIR_TEMP_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between conmmas at least 1 char
                    {
                      mytemp.air_temp = laatste_record.substring(pos + 1, pos2);

                      main.air_temp_from_AWS_present = true;

                      // System.out.println("+++ parameter air temp is aanwezig");
                    } // if (pos2 - pos >= 2)

                    // System.out.println("+++++++= pos = " + pos);
                    // System.out.println("+++++++= pos2 = " + pos2);
                    // System.out.println("+++++++=  main.air_temp_from_AWS_present = " +
                    // main.air_temp_from_AWS_present);
                  } //
                } // else if (number_read_commas == etc.
                // relative humidity [format UUU.u]
                //
                else if (number_read_commas == main.HUMIDITY_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between conmmas at least 1 char
                    {
                      try {
                        mytemp.double_rv =
                            Double.parseDouble(laatste_record.substring(pos + 1, pos2)) / 100;
                      } catch (NumberFormatException exception) {
                        mytemp.double_rv = main.INVALID;
                      }
                      // NB "/100" for consistency with not AWS computed rh (see also
                      // main.temperatures_fields_update() and mytemp.OK_button_actioPerformed())

                      mytemp.RH = laatste_record.substring(pos + 1, pos2);

                      main.rh_from_AWS_present = true;

                      // System.out.println("+++ parameter relative humidity is aanwezig");
                    } // if (pos2 - pos >= 2)
                  } //
                } // else if (number_read_commas == etc.
                // SST [format sTW.w]
                //
                else if (number_read_commas == main.SST_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between conmmas at least 1 char
                    {
                      mytemp.sea_water_temp = laatste_record.substring(pos + 1, pos2);

                      main.SST_from_AWS_present = true;

                      // System.out.println("+++ parameter sst is aanwezig");
                    } // if (pos2 - pos >= 2)
                  } //
                } // else if (number_read_commas == etc.
                // relative wind speed [format WS.r]
                //
                else if (number_read_commas == main.RELATIVE_WIND_SPEED_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between commas at least 1 char
                    {
                      int int_relative_wind_speed;
                      String record_aws_relative_wind_speed =
                          laatste_record.substring(pos + 1, pos2);
                      Double double_relative_wind_speed =
                          Double.parseDouble(record_aws_relative_wind_speed);

                      // convert if necessary and round to integer
                      if (main.wind_units.indexOf(main.M_S)
                          == -1) //  not m/s so convert to knots (see TurboWin+ menu:
                      {
                        int_relative_wind_speed =
                            (int)
                                Math.round(
                                    double_relative_wind_speed
                                        * main.M_S_KNOT_CONVERSION); // NB int cast necessary for
                        // conversion from long (result
                        // from Math.round) to int
                        double_relative_wind_speed =
                            double_relative_wind_speed * main.M_S_KNOT_CONVERSION;
                      } else {
                        int_relative_wind_speed = (int) Math.round(double_relative_wind_speed);
                        // NB double_relative_wind_speed in m/s
                      }

                      mywind.wind_speed =
                          Integer.toString(int_relative_wind_speed); // for wind input screen
                      mywind.int_relative_wind_speed = int_relative_wind_speed;
                      mywind.double_relative_wind_speed =
                          double_relative_wind_speed; // only for dashboard

                      main.relative_wind_speed_from_AWS_present = true;

                      // System.out.println("+++ parameter relative wind speed is aanwezig");
                    } // if (pos2 - pos >= 2)
                  } //
                } // else if (number_read_commas == etc.
                // relative wind direction [format WDR.r]
                //
                else if (number_read_commas == main.RELATIVE_WIND_DIR_COMMA_NUMBER) {
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

                      // int int_relative_wind_dir = (int) Math.round(double_relative_wind_dir);
                      // mywind.int_relative_wind_dir = (int) Math.round(double_relative_wind_dir);
                      int int_relative_wind_dir = (int) Math.round(double_relative_wind_dir);

                      // if (int_relative_wind_dir == 0)
                      // {
                      //   int_relative_wind_dir = 360;
                      // }
                      // mywind.wind_dir = Integer.toString(mywind.int_relative_wind_dir);
                      mywind.wind_dir =
                          Integer.toString(int_relative_wind_dir); // for wind input screen
                      mywind.int_relative_wind_dir = int_relative_wind_dir;
                      main.relative_wind_dir_from_AWS_present = true;

                      // System.out.println("+++ parameter relative wind dir is aanwezig");
                    } // if (pos2 - pos >= 2)
                  } //
                } // else if (number_read_commas == etc.
                // true wind speed [format WS.t]
                //
                else if (number_read_commas == main.TRUE_WIND_SPEED_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between commas at least 1 char
                    {
                      int int_true_wind_speed;
                      String record_aws_true_wind_speed = laatste_record.substring(pos + 1, pos2);
                      Double double_true_wind_speed =
                          Double.parseDouble(record_aws_true_wind_speed);

                      if (main.wind_units.indexOf(main.M_S)
                          == -1) //  not m/s so convert to knots (see TurboWin+ menu:
                      {
                        int_true_wind_speed =
                            (int)
                                Math.round(
                                    double_true_wind_speed
                                        * main.M_S_KNOT_CONVERSION); // NB int cast necessary for
                        // conversion from long (result
                        // from Math.round) to int
                        double_true_wind_speed =
                            double_true_wind_speed * main.M_S_KNOT_CONVERSION; // in knots
                      } else {
                        int_true_wind_speed = (int) Math.round(double_true_wind_speed);
                        // NB double_true_wind_speed; in m/s
                      }

                      mywind.int_true_wind_speed = int_true_wind_speed;
                      mywind.double_true_wind_speed = double_true_wind_speed; // only for dashboard

                      main.true_wind_speed_from_AWS_present = true;

                      // System.out.println("+++ parameter true wind speed is aanwezig");
                    } // if (pos2 - pos >= 2)
                  } //
                } // else if (number_read_commas == etc.
                // true wind direction [format WDT.t]
                //
                else if (number_read_commas == main.TRUE_WIND_DIR_COMMA_NUMBER) {
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

                      main.true_wind_dir_from_AWS_present = true;

                      // System.out.println("+++ parameter true wind dir is aanwezig");
                    } // if (pos2 - pos >= 2)
                  } // if (laatste_record.length() > pos + 1 + 12)
                } // else if (number_read_commas == etc.
                // max wind gust speed from 10min [format WSX.x]
                //
                else if (number_read_commas == main.TRUE_WIND_GUST_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between commas at least 1 char
                    {
                      int int_true_wind_gust_speed;
                      String record_aws_true_wind_gust_speed =
                          laatste_record.substring(pos + 1, pos2);
                      Double double_true_wind_gust_speed =
                          Double.parseDouble(record_aws_true_wind_gust_speed);

                      // convert if necessary and round to integer
                      if (main.wind_units.indexOf(main.M_S)
                          == -1) //  not originally measured/observed in m/s so convert to knots
                      // (see TurboWin+ menu: Maintenance -> Station data)
                      {
                        int_true_wind_gust_speed =
                            (int)
                                Math.round(
                                    double_true_wind_gust_speed
                                        * main.M_S_KNOT_CONVERSION); // NB int cast necessary for
                        // conversion from long (result
                        // from Math.round) to int
                        double_true_wind_gust_speed =
                            double_true_wind_gust_speed
                                * main.M_S_KNOT_CONVERSION; // knots   // only for dashboard
                      } else {
                        int_true_wind_gust_speed = (int) Math.round(double_true_wind_gust_speed);
                        // NB double_true_wind_gust_speed in knots
                      }

                      mywind.int_true_wind_gust_speed = int_true_wind_gust_speed;
                      mywind.double_true_wind_gust_speed =
                          double_true_wind_gust_speed; // only for dashboard

                      main.true_wind_gust_from_AWS_present = true;
                    } // if (pos2 - pos >= 2)
                  } // if (laatste_record.length() > pos + 1 + 12) // for safety; 12 = YYYYMMDDHHmm
                  // (always at the end of every record)
                } // else if (number_read_commas == main.TRUE_WIND_GUST_COMMA_NUMBER)
                // max wind gust direction from 10min [format WDX.x]
                //
                else if (number_read_commas == main.TRUE_WIND_GUST_DIR_COMMA_NUMBER) {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between commas at least 1 char
                    {
                      String record_aws_true_wind_gust_dir =
                          laatste_record.substring(pos + 1, pos2);
                      double double_true_wind_gust_dir =
                          Double.parseDouble(record_aws_true_wind_gust_dir);

                      int int_true_wind_gust_dir = (int) Math.round(double_true_wind_gust_dir);

                      if (int_true_wind_gust_dir == 0) {
                        int_true_wind_gust_dir = 360;
                      }

                      mywind.int_true_wind_gust_dir = int_true_wind_gust_dir;

                      main.true_wind_gust_dir_from_AWS_present = true;

                      // System.out.println("+++ parameter true wind gust dir is aanwezig");
                    } // if (pos2 - pos >= 2)
                  } // if (laatste_record.length() > pos + 1 + 12)
                } // else if (number_read_commas == etc.
                //
                // supply voltage [format VV.v]
                //
                // -- not for displaying in TurboWin+
                //
                // internal temperature [format sTT.t]
                //
                // -- not for displaying in TurboWin+
                //
                // VOT (visual observation acquisition trigger) [format sNNN]
                //
                // else if (number_read_commas == main.VOT_COMMA_NUMBER)
                else if ((number_read_commas == main.VOT_COMMA_NUMBER)
                    && (main.RS232_connection_mode == 3)
                    && (main.eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN)
                        == false)) // only in case of EUCAWS + 'eucaws uploads method' (not for OMC
                // !)
                {
                  if (laatste_record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    int pos2 = laatste_record.indexOf(",", pos + 1);
                    if (pos2 - pos >= 2) // between commas at least 1 char
                    {
                      int int_VOT = Integer.parseInt(laatste_record.substring(pos + 1, pos2));
                      main.VOT = int_VOT; // for dashboard Meteo France

                      if (int_VOT < 0) {
                        main.jTextField4.setForeground(Color.RED);
                        // main.jTextField4.setText(hulp_VOT + " minutes to go before you can insert
                        // obervation parameters");
                        int_VOT = Math.abs(int_VOT) + 20;
                        String str_VOT = Integer.toString(int_VOT);
                        main.jTextField4.setText(
                            str_VOT
                                + " minutes to go before the obs will be sent by the AWS, please do not insert observation parameters");

                        main.jMenuItem46.setEnabled(false); // Obs to AWS
                      } else if (int_VOT == 100) {
                        main.jTextField4.setForeground(Color.RED);
                        main.jTextField4.setText("ship is in port (no obs will be sent)");

                        main.jMenuItem46.setEnabled(false); // Obs to AWS
                      } else if (int_VOT == 101) {
                        main.jTextField4.setForeground(Color.RED);
                        main.jTextField4.setText(
                            "observations are not required in this area (no obs will be sent)");

                        main.jMenuItem46.setEnabled(false); // Obs to AWS
                      } else if (int_VOT == 102) {
                        main.jTextField4.setForeground(Color.RED);
                        main.jTextField4.setText(
                            "the AWS transmitter is out of order (no obs will be sent)");

                        main.jMenuItem46.setEnabled(false); // Obs to AWS
                      } else if (int_VOT == 103) {
                        main.jTextField4.setForeground(Color.RED);
                        main.jTextField4.setText(
                            "the AWS GPS is out of order (no obs will be sent)");

                        main.jMenuItem46.setEnabled(false); // Obs to AWS
                      } else if (int_VOT == 104) {
                        main.jTextField4.setForeground(Color.RED);
                        main.jTextField4.setText(
                            "AWS transmission is deactivated (no obs will be sent)");

                        main.jMenuItem46.setEnabled(false); // Obs to AWS
                      } else {
                        main.jTextField4.setForeground(owner.color_dark_green);
                        String str_VOT = Integer.toString(int_VOT);
                        main.jTextField4.setText(
                            str_VOT
                                + " minutes to go before the obs will be sent by the AWS, you can now insert observation parameters and select \"Output -> Obs to AWS\" ");

                        main.jMenuItem46.setEnabled(
                            true); // Obs to AWS (uploads via EUCAWS iridium modem)
                      }

                      main.VOT_from_AWS_present = true;

                      // System.out.println("+++ parameter VOT is present");
                    } // if (pos2 - pos >= 2)
                  } //
                } // else if (number_read_commas == etc.

                // SPARE
                //
                // -- not for displaying in TurboWin+
                //
                //
                // SPARE
                // -- not for displaying in TurboWin+
                //
                // SPARE
                //
                // -- not for displaying in TurboWin+
                //
              } // if (pos > 0)
            } while (pos > 0);

            // update data text fields on main screen
            //
            if (main.air_temp_from_AWS_present && main.rh_from_AWS_present) {
              DewPointStateUpdater.updateFromAwsValues();
            }
            main.date_time_fields_update();
            main.position_fields_update();
            main.barometer_fields_update();
            main.barograph_fields_update();
            main.temperatures_fields_update();
            main.wind_fields_update();

            // update AWS dasboard values
            owner.RS422_update_AWS_dasboard_values();
          } // if (doorgaan_in_record)

          // clear memory
          check_presence_parameter_system_datum_tijd = null;
        } // protected void done()
      }.execute(); // new SwingWorker <Void, Void>()
    } // if (system_minute % 1 == 0)
  } // private void RS422_Read_AWS_Sensor_Data_For_Display()
}
