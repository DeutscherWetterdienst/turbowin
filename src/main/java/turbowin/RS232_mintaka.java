package turbowin;

import static turbowin.main_RS232_RS422.GPS_latitude;
import static turbowin.main_RS232_RS422.GPS_latitude_earlier;
import static turbowin.main_RS232_RS422.GPS_latitude_earlier_wind;
import static turbowin.main_RS232_RS422.GPS_latitude_hemisphere;
import static turbowin.main_RS232_RS422.GPS_longitude;
import static turbowin.main_RS232_RS422.GPS_longitude_earlier;
import static turbowin.main_RS232_RS422.GPS_longitude_earlier_wind;
import static turbowin.main_RS232_RS422.GPS_longitude_hemisphere;
import static turbowin.main_RS232_RS422.MAX_AGE_STARX_OBS_DATA;
import static turbowin.main_RS232_RS422.Mintaka_Star_Checksum;
import static turbowin.main_RS232_RS422.RS232_APR_AWSR_send;
import static turbowin.main_RS232_RS422.RS232_WOW_APR_compute_air_pressure_height_correction;
import static turbowin.main_RS232_RS422.RS232_compute_APR_COG_SOG_wind;
import static turbowin.main_RS232_RS422.RS232_make_RH_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_air_temp_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_dew_point_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_pressure_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_pressure_a_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_pressure_ppp_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class RS232_mintaka {

  public static void RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_GPS_For_Obs(
      final String mode, final boolean StarX) {
    // called from: - initSynopparameters() [myposition.java]

    //
    // MINTAKA STAR has an integrated GPS
    // GPS data is part of th saved pressure string eg:
    // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
    // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
    // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
    // <course> is True, <speed> in knots, <elevation> in meters
    //
    // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
    // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
    //
    //
    // STARX (NB first part of STARX is the same as the STAR)
    // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
    // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>,
    // <temperature>,<relativeHumidity>,<wetBulbTemperature>,<dewPoint>,
    // <humiditySensor pressure in mb>,<observationAge>*<checksum>
    //
    // 1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,1017.89,60*18

    // initialisation
    GPS_latitude = "";
    GPS_longitude = "";
    GPS_latitude_hemisphere = "";
    GPS_longitude_hemisphere = "";
    GPS_latitude_earlier = "";
    GPS_longitude_earlier = "";

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        // retrieve (almost) most recent recorded position
        //
        //
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;
        String sensor_data_file_naam_datum_tijd_deel_start =
            null; // for retrieving GPS position earlier (10 min or 3 hours) eg 2017092504 (file
        // name eg sensor_data_2017092505.txt)
        String date_time_SOG_COG_start =
            null; // for retrieving GPS position earlier (10 min or 3 hours) eg 201709250406 (last
        // 12 char of record)

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
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null;

            // retrieve the last record in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              laatste_record = record;
            } // while ((record = in.readLine()) != null)

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Star or StarX format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = -1;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos != -1) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos != -1);

              if (StarX == false) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
                  laatste_record = null;
                  System.out.println(
                      "--- Mintaka Star format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                }
              } else if (StarX == true) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                  laatste_record = null;
                  System.out.println(
                      "--- Mintaka StarX format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                }
              }
            } // if (laatste_record != null)

            // last retrieved record ok
            if (laatste_record != null) {
              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();

              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              /// System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

              // NB COG and SOG must be computed over last 10 minutes (format 101) or last 3 hours
              // (FM13)
              int COG_SOG_diff_min = 0;
              if (main.obs_format.equals(main.FORMAT_101)) {
                COG_SOG_diff_min = 10; // 10 minutes (between positions to compute SOG and COG)
              } else if (main.obs_format.equals(main.FORMAT_FM13)) {
                COG_SOG_diff_min =
                    180; // 180 minutes (3 hours, between positions to compute SOG and COG)
              }
              // NB GregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int
              // minute)
              // NB Constructs a GregorianCalendar with the given date and time set for the default
              // time zone with the default locale.
              // NB month 0..11            // The first month of the year in the Gregorian and
              // Julian calendars is JANUARY which is 0
              // NB dayOfMonth 1..31       // The first day of the month has value 1
              // NB hourOfDay: 0.. 23      // Field number for get and set indicating the hour of
              // the day. HOUR_OF_DAY is used for the 24-hour clock. E.g., at 10:04:15.250 PM the
              // HOUR_OF_DAY is 22.

              String date_time_SOG_COG_end =
                  record_datum_tijd_minuten; // date time of last retrieved record eg 201709250400
              // (YYYYMMDDHHmm has length 12)

              // record_datum_tijd_minuten format: YYYYMMDDHHmm
              String year_end = date_time_SOG_COG_end.substring(0, 4);
              String month_end = date_time_SOG_COG_end.substring(4, 6); // January = 01
              String day_end = date_time_SOG_COG_end.substring(6, 8);
              String hour_end = date_time_SOG_COG_end.substring(8, 10);
              String minute_end = date_time_SOG_COG_end.substring(10, 12);
              GregorianCalendar cal_SOG_COG_date =
                  new GregorianCalendar(
                      Integer.parseInt(year_end),
                      Integer.parseInt(month_end) - 1, // convert to internal month representation
                      Integer.parseInt(day_end),
                      Integer.parseInt(hour_end),
                      Integer.parseInt(minute_end));
              cal_SOG_COG_date.setTimeZone(
                  TimeZone.getTimeZone("UTC")); // !!!! (otherwise in local pc time zone)

              cal_SOG_COG_date.add(
                  Calendar.MINUTE,
                  -COG_SOG_diff_min); // substract 10 minutes (format 101) or 3 hours (FM13)
              date_time_SOG_COG_start =
                  main.sdf4.format(
                      cal_SOG_COG_date
                          .getTime()); // sdf4 : yyyyMMddHHmm eg 201701030812 // NB MM = 01 =
              // January!! so SimpleDateformat automatically converts the
              // internal  month representation [January = 0] to the human
              // representation [January = 1]

              String year_start = date_time_SOG_COG_start.substring(0, 4);
              String month_start = date_time_SOG_COG_start.substring(4, 6); // January = 01
              String day_start = date_time_SOG_COG_start.substring(6, 8);
              String hour_start = date_time_SOG_COG_start.substring(8, 10);

              sensor_data_file_naam_datum_tijd_deel_start =
                  year_start + month_start + day_start + hour_start; // eg 2017092505

              // below for testing
              // System.out.println("+++ sensor_data_file_naam_datum_tijd_deel_start: " +
              // sensor_data_file_naam_datum_tijd_deel_start);

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // eg max 5 minutes old
              {

                // cheksum check
                //
                // example Mintaka Star last_record : 1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211
                // example Mintaka StarX last record: 1018.12,1018.13,0.00,0, 16 30.4429N, 88
                // 21.9040W,0,2,7,29.4,85,27.4,26.7,1017.89,60*18
                //
                String record_checksum =
                    laatste_record.substring(
                        laatste_record.length() - 14,
                        laatste_record.length()
                            - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211"
                String computed_checksum = Mintaka_Star_Checksum(laatste_record);

                // below only for testing (uncomment when testing)
                // computed_checksum = record_checksum;

                if (computed_checksum.equals(record_checksum)) {
                  // System.out.println("checksum ok");

                  // retrieved (from file) record example Mintaka Star: 1029.97,1029.97,-0.90,7, 52
                  // 41.9535N,  6 14.1943E,0,0,19*1A201703152006
                  int pos1 =
                      laatste_record.indexOf(
                          ",", 0); // position of the first "," in the last record
                  int pos2 =
                      laatste_record.indexOf(
                          ",", pos1 + 1); // position of the second "," in the last record
                  int pos3 =
                      laatste_record.indexOf(
                          ",", pos2 + 1); // position of the third "," in the last record
                  int pos4 =
                      laatste_record.indexOf(
                          ",", pos3 + 1); // position of the 4th "," in the last record
                  int pos5 =
                      laatste_record.indexOf(
                          ",", pos4 + 1); // position of the 5th "," in the last record
                  int pos6 =
                      laatste_record.indexOf(
                          ",", pos5 + 1); // position of the 6th "," in the last record
                  // int pos7 = laatste_record.indexOf(",", pos6 +1);
                  //         // position of the 7th "," in the last record

                  GPS_latitude = laatste_record.substring(pos4 + 1, pos5);
                  GPS_longitude = laatste_record.substring(pos5 + 1, pos6);

                  System.out.println(
                      "--- sensor data record, GPS latitude: "
                          + GPS_latitude
                          + "("
                          + record_datum_tijd_minuten
                          + ")");
                  System.out.println(
                      "--- sensor data record, GPS longitude: "
                          + GPS_longitude
                          + "("
                          + record_datum_tijd_minuten
                          + ")");
                } else {
                  // System.out.println("record checksum = " + record_checksum);
                  // System.out.println("computed checksum = " + computed_checksum);
                  System.out.println("--- checksum NOT ok " + "(" + laatste_record + ")");

                  GPS_latitude = "";
                  GPS_longitude = "";
                } // else

              } // if (timeDiff <= 5L)
              else {
                GPS_latitude = "";
                GPS_longitude = "";
              } // else
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_GPS_For_Obs(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        // retrieve GPS position 10 minutes (format 101) or 3 hours (FM13) earlier
        //
        // NB there is no use to continue if a recent position was not found
        if ((GPS_latitude.compareTo("") != 0)
            && (GPS_latitude != null)
            && (GPS_latitude.indexOf("*") == -1)) {
          record = null;

          // determine sensor data file name of the file with the GPS position of 10 minutes or 3
          // hours ago
          sensor_data_file_name =
              "sensor_data_" + sensor_data_file_naam_datum_tijd_deel_start + ".txt";

          // first check if there is a sensor data file present (and not empty)
          volledig_path_sensor_data =
              main.logs_dir + java.io.File.separator + sensor_data_file_name;
          // System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

          sensor_data_file = new File(volledig_path_sensor_data);
          if (sensor_data_file.exists() && sensor_data_file.length() > 0) // length() in bytes
          {
            try (BufferedReader in =
                new BufferedReader(new FileReader(volledig_path_sensor_data))) {
              record = null;
              while ((record = in.readLine()) != null) {
                if (record.length()
                    > 15) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
                // leat 2 char pressure value= 15 chars)
                {
                  int pos = record.length() - 12; // pos is now start position of YYYYMMDDHHmm
                  String record_datum_tijd_minuten =
                      record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

                  if (record_datum_tijd_minuten.equals(
                      date_time_SOG_COG_start)) // found the corresponding record of 10 min or 3 hrs
                  // earlier
                  {
                    String record_checksum =
                        record.substring(
                            record.length() - 14,
                            record.length()
                                - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,
                    // 6 14.1848E,0,0,-1*24201703291211"
                    String computed_checksum = Mintaka_Star_Checksum(record);

                    // below only for testing (uncomment when testing)
                    // computed_checksum = record_checksum;

                    if (computed_checksum.equals(record_checksum)) {
                      // System.out.println("checksum ok");

                      // extra check on correct number of commas in the earlier (10 min or 3 hrs)
                      // record
                      //
                      int number_read_commas = 0;
                      int pos_comma = -1;

                      do {
                        pos_comma = record.indexOf(",", pos_comma + 1);
                        if (pos_comma != -1) // "," found
                        {
                          number_read_commas++;
                          // System.out.println("+++ number_read_commas = " + number_read_commas);
                        }
                      } while (pos_comma != -1);

                      boolean format_ok = true;
                      if (StarX == false) {
                        if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
                          format_ok = false;
                          System.out.println(
                              "--- Mintaka Star format (number commas) rertieved record earlier (for SOG and COG) NOT ok (file: "
                                  + volledig_path_sensor_data
                                  + "; record: "
                                  + record
                                  + ")");
                        }
                      } else if (StarX == true) {
                        if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                          format_ok = false;
                          System.out.println(
                              "--- Mintaka StarX format (number commas) rertieved record earlier (for SOG and COG) NOT ok (file: "
                                  + volledig_path_sensor_data
                                  + "; record: "
                                  + record
                                  + ")");
                        }
                      }

                      if (format_ok) {
                        // retrieved (from file) record example Mintaka Star:
                        // 1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006
                        int pos1 =
                            record.indexOf(",", 0); // position of the first "," in the last record
                        int pos2 =
                            record.indexOf(
                                ",", pos1 + 1); // position of the second "," in the last record
                        int pos3 =
                            record.indexOf(
                                ",", pos2 + 1); // position of the third "," in the last record
                        int pos4 =
                            record.indexOf(
                                ",", pos3 + 1); // position of the 4th "," in the last record
                        int pos5 =
                            record.indexOf(
                                ",", pos4 + 1); // position of the 5th "," in the last record
                        int pos6 =
                            record.indexOf(
                                ",", pos5 + 1); // position of the 6th "," in the last record
                        // int pos7 = record.indexOf(",", pos6 +1);
                        //       // position of the 7th "," in the last record

                        GPS_latitude_earlier = record.substring(pos4 + 1, pos5);
                        GPS_longitude_earlier = record.substring(pos5 + 1, pos6);

                        System.out.println(
                            "--- sensor data record, GPS latitude earlier: "
                                + GPS_latitude_earlier
                                + "("
                                + record_datum_tijd_minuten
                                + ")");
                        System.out.println(
                            "--- sensor data record, GPS longitude earlier: "
                                + GPS_longitude_earlier
                                + "("
                                + record_datum_tijd_minuten
                                + ")");
                      } // if (format_ok)
                      else {
                        GPS_latitude_earlier = "";
                        GPS_longitude_earlier = "";
                      }
                    } // if (computed_checksum.equals(record_checksum))
                    else {
                      // System.out.println("record checksum = " + record_checksum);
                      // System.out.println("computed checksum = " + computed_checksum);
                      System.out.println("--- checksum NOT ok " + "(" + record + ")");

                      GPS_latitude_earlier = "";
                      GPS_longitude_earlier = "";
                    } // else
                  } // if (record_datum_tijd_minuten.equals(date_time_SOG_COG_start))
                } // if (record.length() > 15)
                else {
                  GPS_latitude_earlier = "";
                  GPS_longitude_earlier = "";
                }
              } // while ((record = in.readLine()) != null)

            } // try
            catch (IOException ex) {
              System.out.println(
                  "--- Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_GPS_For_Obs(): "
                      + ex);
            }
          } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
        } // if ( (GPS_latitude.compareTo("") != 0) etc.

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        // intialisation (in this inner class)
        boolean fix_ok = true;
        String message = "";
        String message_fix_position = "";

        //
        //////// latitude (eg " 52 04.7041N") /////////
        //
        if ((GPS_latitude.compareTo("") != 0)
            && (GPS_latitude != null)
            && (GPS_latitude.indexOf("*") == -1)) {
          // fill the public string latitude values
          //
          // NB String hulp_GPS_latitude = GPS_latitude.replaceAll("\\s","");        // remove all
          // the whitespaces in the string

          String fix_latitude =
              GPS_latitude
                  .trim(); // to remove leading and trailing whitespace in the string eg " 52
          // 04.7041N" -> "52 04.7041N"
          int pos_space = fix_latitude.indexOf(" ", 0); // find first space (" ") in the string
          myposition.latitude_degrees =
              fix_latitude.substring(
                  0, pos_space); // eg "52 04.7041N" -> "52" and "2 04.7041N" -> "2"

          String fix_latitude_minutes =
              fix_latitude.substring(
                  pos_space + 1,
                  fix_latitude.length()
                      - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"
          BigDecimal bd_lat_min =
              new BigDecimal(fix_latitude_minutes)
                  .setScale(0, RoundingMode.HALF_UP); // 0 decimals rounding

          ///////////////////
          int hulp_bd_lat_min = bd_lat_min.intValue(); // BigDecimal to int
          if (hulp_bd_lat_min == 60) {
            // set latitude minutes to 0
            hulp_bd_lat_min = 0;
            bd_lat_min = BigDecimal.valueOf(hulp_bd_lat_min); // int to BigDecimal

            // increment latitude degrees
            int hulp_latitude_degrees =
                Integer.parseInt(myposition.latitude_degrees); // String to int
            hulp_latitude_degrees++;
            myposition.latitude_degrees =
                Integer.toString(hulp_latitude_degrees); // int to String (via Integer)
          }
          ///////////////////

          myposition.latitude_minutes =
              bd_lat_min.toString(); // rounded minutes value  e.g. "04.7041N" -> "4" minutes
          if (myposition.latitude_minutes.length() == 1) {
            myposition.latitude_minutes = "0" + myposition.latitude_minutes; // "4" -> "04"
          }

          GPS_latitude_hemisphere =
              fix_latitude.substring(fix_latitude.length() - 1); // eg "52 04.7041N" -> "N"
          if (GPS_latitude_hemisphere.toUpperCase().equals("N")) {
            myposition.latitude_hemisphere = myposition.HEMISPHERE_NORTH;
          } else if (GPS_latitude_hemisphere.toUpperCase().equals("S")) {
            myposition.latitude_hemisphere = myposition.HEMISPHERE_SOUTH;
          } else {
            myposition.latitude_hemisphere = "";
            fix_ok = false;
          }

          // lat int values
          //
          if (fix_ok == true) {
            try {
              myposition.int_latitude_degrees = Integer.parseInt(myposition.latitude_degrees);
            } catch (NumberFormatException e) {
              fix_ok = false;
            }

            try {
              myposition.int_latitude_minutes = Integer.parseInt(myposition.latitude_minutes);
            } catch (NumberFormatException e) {
              fix_ok = false;
            }
          } // if (fix_ok == true)

          // for latitude (LaLaLa) for IMMT log
          //
          if (fix_ok == true) {
            int int_latitude_minutes_6 =
                bd_lat_min.intValue()
                    / 6; // devide the minutes by six and disregard the remainder, now tenths of
            // degrees [IMMT rounding!!!}
            String latitude_minutes_6 =
                Integer.toString(int_latitude_minutes_6); // convert to String
            // myposition.lalala_code = myposition.latitude_degrees.trim().replaceFirst("^0+(?!$)",
            // "") + latitude_minutes_6;
            myposition.lalala_code = myposition.latitude_degrees + latitude_minutes_6;

            int len = 3; // always 3 chars
            if (myposition.lalala_code.length() < len) {
              // pad on left with zeros
              myposition.lalala_code =
                  "0000000000".substring(0, len - myposition.lalala_code.length())
                      + myposition.lalala_code;
            } else if (myposition.lalala_code.length() > len) {
              fix_ok = false;
              // System.out.println("+++++++++++ lalala_code error: " + myposition.lalala_code);
            }
          } // if (fix_ok == true)
        } // if ( (GPS_latitude.compareTo("") != 0) && (GPS_latitude != null) &&
        // (GPS_latitude.indexOf("*") == -1) )
        else {
          fix_ok = false;
        }

        //
        //////// longitude (eg "  4 14.7041W") /////////
        //
        if ((fix_ok == true)
            && (GPS_longitude.compareTo("") != 0)
            && (GPS_longitude != null)
            && (GPS_longitude.indexOf("*") == -1)) {
          // fill the public string longitude values
          //
          String fix_longitude =
              GPS_longitude
                  .trim(); // to remove leading and trailing whitespace in the string eg "  6
          // 14.7041N" -> "6 14.7041N"
          int pos_space = fix_longitude.indexOf(" ", 0); // find first space (" ") in the string
          myposition.longitude_degrees =
              fix_longitude.substring(
                  0, pos_space); // eg "6 14.7041W" -> "6" and "116 14.7041W" -> "116"

          String fix_longitude_minutes =
              fix_longitude.substring(
                  pos_space + 1,
                  fix_longitude.length()
                      - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"
          BigDecimal bd_lon_min =
              new BigDecimal(fix_longitude_minutes)
                  .setScale(0, RoundingMode.HALF_UP); // 0 decimals rounding

          ///////////////////
          int hulp_bd_lon_min = bd_lon_min.intValue(); // BigDecimal to int
          if (hulp_bd_lon_min == 60) {
            // set longitude minutes to 0
            hulp_bd_lon_min = 0;
            bd_lon_min = BigDecimal.valueOf(hulp_bd_lon_min); // int to BigDecimal

            // increment longitude degrees
            int hulp_longitude_degrees =
                Integer.parseInt(myposition.longitude_degrees); // String to int
            hulp_longitude_degrees++;
            myposition.longitude_degrees =
                Integer.toString(hulp_longitude_degrees); // int to String (via Integer)
          }
          ///////////////////

          myposition.longitude_minutes =
              bd_lon_min
                  .toString(); // rounded minutes value  e.g. "04.7041N" -> "5" minutes; "23.7041N"
          // -> "24" minutes
          if (myposition.longitude_minutes.length() == 1) {
            myposition.longitude_minutes = "0" + myposition.longitude_minutes; // "5" -> "05"
          }

          GPS_longitude_hemisphere =
              fix_longitude.substring(fix_longitude.length() - 1); // eg "4 14.7041W" -> "W"
          if (GPS_longitude_hemisphere.toUpperCase().equals("E")) {
            myposition.longitude_hemisphere = myposition.HEMISPHERE_EAST;
          } else if (GPS_longitude_hemisphere.toUpperCase().equals("W")) {
            myposition.longitude_hemisphere = myposition.HEMISPHERE_WEST;
          } else {
            myposition.longitude_hemisphere = "";
            fix_ok = false;
          }

          // fill the public int longitude values
          //
          if (fix_ok == true) {
            try {
              myposition.int_longitude_degrees = Integer.parseInt(myposition.longitude_degrees);
            } catch (NumberFormatException e) {
              fix_ok = false;
            }

            try {
              myposition.int_longitude_minutes = Integer.parseInt(myposition.longitude_minutes);
            } catch (NumberFormatException e) {
              fix_ok = false;
            }
          } // if (fix_ok == true)

          // for longitude (LoLoLoLo) for IMMT log (NB see also myposition.java) In fact this IMMT
          // preparation only necessary in APR mode
          //
          if (fix_ok == true) {
            int int_longitude_minutes_6 =
                bd_lon_min.intValue()
                    / 6; // devide the minutes by six and disregard the remainder, now tenths of
            // degrees [IMMT rounding!!!}
            String longitude_minutes_6 =
                Integer.toString(int_longitude_minutes_6); // convert to String
            myposition.lolololo_code = myposition.longitude_degrees + longitude_minutes_6;

            int len2 = 4; // always 4 chars
            if (myposition.lolololo_code.length() < len2) {
              // pad on left with zeros
              myposition.lolololo_code =
                  "0000000000".substring(0, len2 - myposition.lolololo_code.length())
                      + myposition.lolololo_code;
            } else if (myposition.lolololo_code.length() > len2) {
              fix_ok = false;
              // System.out.println("+++++++++++ lolololo_code error: " + myposition.lolololo_code);
            }
          } // if (fix_ok == true)

        } //  if ( (GPS_longitude.compareTo("") != 0) && (GPS_longitude != null) &&
        // (GPS_longitude.indexOf("*") == -1) )
        else {
          fix_ok = false;
        }

        // quadrant of the globe
        //
        if (fix_ok == true) {
          // quadrant of the globe for IMMT
          //
          if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true)
              && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true)) {
            myposition.Qc_code = "1";
          } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true)
              && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true)) {
            myposition.Qc_code = "3";
          } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true)
              && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true)) {
            myposition.Qc_code = "5";
          } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true)
              && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true)) {
            myposition.Qc_code = "7";
          } else {
            fix_ok = false;
            // System.out.println("+++++++++++ Qc_code error");
          }
        } // if (fix_ok == true)

        if ((fix_ok == true) && (mode.equals("MANUAL"))) {
          // latitude
          if (myposition.latitude_degrees.compareTo("") != 0) {
            myposition.jTextField1.setText(myposition.latitude_degrees);
          }

          if (myposition.latitude_minutes.compareTo("") != 0) {
            myposition.jTextField2.setText(myposition.latitude_minutes);
          }

          if (myposition.latitude_hemisphere.compareTo("") != 0) {
            if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH)) {
              myposition.jRadioButton1.setSelected(true);
            } else if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH)) {
              myposition.jRadioButton2.setSelected(true);
            }
          } // if (latitude_hemisphere != "")

          // longitude
          if (myposition.longitude_degrees.compareTo("") != 0) {
            myposition.jTextField3.setText(myposition.longitude_degrees);
          }

          if (myposition.longitude_minutes.compareTo("") != 0) {
            myposition.jTextField4.setText(myposition.longitude_minutes);
          }

          if (myposition.longitude_hemisphere.compareTo("") != 0) {
            if (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST)) {
              myposition.jRadioButton3.setSelected(true);
            } else if (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST)) {
              myposition.jRadioButton4.setSelected(true);
            }
          } // if (latitude_hemisphere != "")
        } // if ((fix_ok == true) && (mode.equals("MANUAL")))

        // computing COG and SOG (position interval 10 minutes or 3 hours)
        //
        //
        boolean GPS_earlier_ok = true;

        if ((GPS_latitude_earlier.compareTo("") == 0)
            || (GPS_longitude_earlier.compareTo("") == 0)
            || (GPS_latitude_earlier == null)
            || (GPS_longitude_earlier == null)
            || (GPS_latitude_earlier.indexOf("*") != -1)
            || (GPS_longitude_earlier.indexOf("*") != -1)) {
          GPS_earlier_ok = false;

          if (main.obs_format.equals(main.FORMAT_101)) {
            // 10 minutes based
            message =
                "[GPS] error; no 10 minutes earlier position data available for computing COG and SOG";
          } else if (main.obs_format.equals(main.FORMAT_FM13)) {
            // 180 minutes (3 hours, between positions to compute SOG and COG) based
            message =
                "[GPS] error; no 3 hours earlier position data available for computing COG and SOG";
          }

          main.log_turbowin_system_message(message);
        }

        if (fix_ok && GPS_earlier_ok) {
          // http://www.movable-type.co.uk/scripts/latlong.html

          // MINTAKA STAR has an integrated GPS
          // GPS data is part of the saved pressure string eg:
          // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
          // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
          // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
          // <course> is True, <speed> in knots, <elevation> in meters
          //
          // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
          // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
          //
          // STARX (NB first part of STARX is the same as the STAR)
          // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
          // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>,
          // <temperature>,<relativeHumidity>,<wetBulbTemperature>,<dewPoint>,
          // <humiditySensor pressure in mb>*<checksum>
          //
          // 1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,1017.89*18

          double double_lat_start;
          double double_lon_start;
          double double_lat_end;
          double double_lon_end;

          // latitude START position (latitude 10 min or 3 hrs earlier)
          //
          String fix_latitude_start =
              GPS_latitude_earlier
                  .trim(); // to remove leading and trailing whitespace in the string eg " 52
          // 04.7041N" -> "52 04.7041N"
          int pos_space =
              fix_latitude_start.indexOf(" ", 0); // find first space (" ") in the string
          String fix_latitude_degrees_start =
              fix_latitude_start.substring(
                  0, pos_space); // eg "52 04.7041N" -> "52" and "2 04.7041N" -> "2"
          String fix_latitude_minutes_start =
              fix_latitude_start.substring(
                  pos_space + 1,
                  fix_latitude_start.length()
                      - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"

          double double_latitude_degrees_start = Double.parseDouble(fix_latitude_degrees_start);
          double double_latitude_minutes_start = Double.parseDouble(fix_latitude_minutes_start);

          double_lat_start = double_latitude_degrees_start + double_latitude_minutes_start / 60.0;

          String fix_latitude_hemisphere_start =
              fix_latitude_start.substring(
                  fix_latitude_start.length() - 1); // eg "52 04.7041N" -> "N"
          if (fix_latitude_hemisphere_start.toUpperCase().equals("N")) {
            // North is positive value
            double_lat_start *= 1;
          } else if (fix_latitude_hemisphere_start.toUpperCase().equals("S")) {
            // south = negative value
            double_lat_start *= -1;
          } else {
            double_lat_start = Double.MAX_VALUE;
          }

          // longitude START position (latitude 10 min or 3 hrs earlier)
          //
          String fix_longitude_start =
              GPS_longitude_earlier
                  .trim(); // to remove leading and trailing whitespace in the string eg "152
          // 04.7041W" -> "152 04.7041W"
          pos_space = fix_longitude_start.indexOf(" ", 0); // find first space (" ") in the string
          String fix_longitude_degrees_start =
              fix_longitude_start.substring(
                  0, pos_space); // eg "152 04.7041W" -> "152" and "2 04.7041W" -> "2"
          String fix_longitude_minutes_start =
              fix_longitude_start.substring(
                  pos_space + 1,
                  fix_longitude_start.length()
                      - 1); // only the minutes of the latitude eg "152 04.7041W" -> "04.7041"

          double double_longitude_degrees_start = Double.parseDouble(fix_longitude_degrees_start);
          double double_longitude_minutes_start = Double.parseDouble(fix_longitude_minutes_start);

          double_lon_start = double_longitude_degrees_start + double_longitude_minutes_start / 60.0;

          String fix_longitude_hemisphere_start =
              fix_longitude_start.substring(
                  fix_longitude_start.length() - 1); // eg "152 04.7041W" -> "W"
          if (fix_longitude_hemisphere_start.toUpperCase().equals("E")) {
            // East is positive value
            double_lon_start *= 1;
          } else if (fix_longitude_hemisphere_start.toUpperCase().equals("W")) {
            // West = negative value
            double_lon_start *= -1;
          } else {
            double_lon_start = Double.MAX_VALUE;
          }

          // latitude END position (present pos)
          //
          String fix_latitude_end =
              GPS_latitude
                  .trim(); // to remove leading and trailing whitespace in the string eg " 52
          // 04.7041N" -> "52 04.7041N"
          pos_space = fix_latitude_end.indexOf(" ", 0); // find first space (" ") in the string
          String fix_latitude_degrees_end =
              fix_latitude_end.substring(
                  0, pos_space); // eg "52 04.7041N" -> "52" and "2 04.7041N" -> "2"
          String fix_latitude_minutes_end =
              fix_latitude_end.substring(
                  pos_space + 1,
                  fix_latitude_end.length()
                      - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"

          double double_latitude_degrees_end = Double.parseDouble(fix_latitude_degrees_end);
          double double_latitude_minutes_end = Double.parseDouble(fix_latitude_minutes_end);

          double_lat_end = double_latitude_degrees_end + double_latitude_minutes_end / 60.0;

          String fix_latitude_hemisphere_end =
              fix_latitude_end.substring(fix_latitude_end.length() - 1); // eg "52 04.7041N" -> "N"
          if (fix_latitude_hemisphere_end.toUpperCase().equals("N")) {
            // North is positive value
            double_lat_end *= 1;
          } else if (fix_latitude_hemisphere_end.toUpperCase().equals("S")) {
            // south = negative value
            double_lat_end *= -1;
          } else {
            // System.out.println("+++ double_lat_end = " + double_lat_end);
            double_lat_end = Double.MAX_VALUE;
          }

          // longitude END position (present pos)
          //
          String fix_longitude_end =
              GPS_longitude
                  .trim(); // to remove leading and trailing whitespace in the string eg "152
          // 04.7041W" -> "152 04.7041W"
          pos_space = fix_longitude_end.indexOf(" ", 0); // find first space (" ") in the string
          String fix_longitude_degrees_end =
              fix_longitude_end.substring(
                  0, pos_space); // eg "152 04.7041W" -> "152" and "2 04.7041W" -> "2"
          String fix_longitude_minutes_end =
              fix_longitude_end.substring(
                  pos_space + 1,
                  fix_longitude_end.length()
                      - 1); // only the minutes of the latitude eg "152 04.7041W" -> "04.7041"

          double double_longitude_degrees_end = Double.parseDouble(fix_longitude_degrees_end);
          double double_longitude_minutes_end = Double.parseDouble(fix_longitude_minutes_end);

          double_lon_end = double_longitude_degrees_end + double_longitude_minutes_end / 60.0;

          String fix_longitude_hemisphere_end =
              fix_longitude_end.substring(
                  fix_longitude_end.length() - 1); // eg "152 04.7041W" -> "W"
          if (fix_longitude_hemisphere_end.toUpperCase().equals("E")) {
            // East is positive value
            double_lon_end *= 1;
          } else if (fix_longitude_hemisphere_end.toUpperCase().equals("W")) {
            // West = negative value
            double_lon_end *= -1;
          } else {
            // System.out.println("+++ double_lon_end = " + double_lon_end);
            double_lon_end = Double.MAX_VALUE;
          }

          //
          // Compute distance and bearing between the two positions (present and 10 min/ 3 hours
          // earlier)
          //
          if (double_lat_start != Double.MAX_VALUE
              && double_lon_start != Double.MAX_VALUE
              && double_lat_end != Double.MAX_VALUE
              && double_lon_end != Double.MAX_VALUE) {
            // NB see: http://www.movable-type.co.uk/scripts/latlong.html
            //
            // var R = 6371e3; // metres
            // var ?1 = lat1.toRadians();
            // var ?2 = lat2.toRadians();
            // var ?? = (lat2-lat1).toRadians();
            // var ?? = (lon2-lon1).toRadians();

            // var ?? = Math.log(Math.tan(Math.PI/4+?2/2)/Math.tan(Math.PI/4+?1/2));    // the
            // projected latitude difference
            // var q = Math.abs(??) > 10e-12 ? ??/?? : Math.cos(?1); // E-W course becomes
            // ill-conditioned with 0/0
            //
            //// if dLon over 180 take shorter rhumb line across the anti-meridian:
            // if (Math.abs(??) > Math.PI) ?? = ??>0 ? -(2*Math.PI-??) : (2*Math.PI+??);
            //
            // var dist = Math.sqrt(??*?? + q*q*??*??) * R;
            // var brng = Math.atan2(??, ??).toDegrees();

            double R = 6371e3; // metres
            double lat1_rad =
                Math.toRadians(double_lat_start); // start latitude (latitude 10 min or 3 hours ago)
            double lat2_rad =
                Math.toRadians(double_lat_end); // end latitude (most recent stored latitude)
            double delta_lat_rad = Math.toRadians(double_lat_end - double_lat_start);
            double delta_lon_rad = Math.toRadians(double_lon_end - double_lon_start);
            double SOG = Double.MAX_VALUE;
            double COG = Double.MAX_VALUE;

            double delta_projected_lat =
                Math.log(
                    Math.tan(Math.PI / 4 + lat2_rad / 2) / Math.tan(Math.PI / 4 + lat1_rad / 2));

            double q =
                Math.abs(delta_projected_lat) > 10e-12
                    ? delta_lat_rad / delta_projected_lat
                    : Math.cos(lat1_rad);

            // if (Math.abs(delta_lon_rad) > Math.PI) delta_lon_rad = delta_lon_rad > 0 ?
            // -(2*Math.PI - delta_lon_rad) : (2*Math.PI + delta_lon_rad);
            // if dLon over 180 take shorter rhumb line across the anti-meridian:
            if (delta_lon_rad > Math.PI) delta_lon_rad -= 2 * Math.PI;
            if (delta_lon_rad < -Math.PI) delta_lon_rad += 2 * Math.PI;

            double dist =
                Math.sqrt(delta_lat_rad * delta_lat_rad + q * q * delta_lon_rad * delta_lon_rad)
                    * R;

            if (main.obs_format.equals(main.FORMAT_101)) {
              // 10 minutes based
              SOG = (dist / 1852) * 6; // now SOG in knots
            } else if (main.obs_format.equals(main.FORMAT_FM13)) {
              // 180 minutes (3 hours, between positions to compute SOG and COG) based
              SOG = (dist / 1852) / 3; // now SOG in knots
            }

            COG = Math.toDegrees(Math.atan2(delta_lon_rad, delta_projected_lat));
            if (COG < 0) COG += 360.0;

            // SOG
            //

            // NB WMO_NO_306.pdf
            //
            // WMO table  4451
            // vs Ship s average speed made good during the three hours preceding the time of
            // observation
            // Code
            // figure
            // 0 0 knot 0 km h 1
            // 1 1 5 knots 1 10 km h 1
            // 2 6 10 knots 11 19 km h 1
            // 3 11 15 knots 20 28 km h 1
            // 4 16 20 knots 29 37 km h 1
            // 5 21 25 knots 38 47 km h 1
            // 6 26 30 knots 48 56 km h 1
            // 7 31 35 knots 57 65 km h 1
            // 8 36 40 knots 66 75 km h 1
            // 9 Over 40 knots Over 75 km h 1
            /// Not applicable (report from a coastal land station) or not reported (see Regulation
            // 12.3.1.2 (b))
            if (SOG < 1.0) {
              myposition.jRadioButton14.setSelected(true);
            } else if (SOG >= 1.0 && SOG <= 5.0) {
              myposition.jRadioButton15.setSelected(true);
            } else if (SOG > 5.0 && SOG <= 10.0) {
              myposition.jRadioButton16.setSelected(true);
            } else if (SOG > 10.0 && SOG <= 15.0) {
              myposition.jRadioButton17.setSelected(true);
            } else if (SOG > 15.0 && SOG <= 20.0) {
              myposition.jRadioButton18.setSelected(true);
            } else if (SOG > 20.0 && SOG <= 25.0) {
              myposition.jRadioButton19.setSelected(true);
            } else if (SOG > 25.0 && SOG <= 30.0) {
              myposition.jRadioButton20.setSelected(true);
            } else if (SOG > 30.0 && SOG <= 35.0) {
              myposition.jRadioButton21.setSelected(true);
            } else if (SOG > 35.0 && SOG <= 40.0) {
              myposition.jRadioButton22.setSelected(true);
            } else if (SOG > 40) {
              myposition.jRadioButton23.setSelected(true);
            }

            // COG
            //

            // NB WMO_NO_306.pdf
            //
            // Ds True direction of resultant displacement of the ship during the three hours
            // preceding
            // the time of observation
            // D1 True direction of the point position from the station
            // Code
            // figure
            // 0 Calm (in D, DK), or stationary (in Ds), or at the station (in Da, D1), or
            // stationary or no clouds (in DH,
            // DL, DM)
            // 1 NE
            // 2 E
            // 3 SE
            // 4 S
            // 5 SW
            // 6 W
            // 7 NW
            // 8 N
            // 9 All directions (in Da, D1), or confused (in DK), or variable (in D(wind)), or
            // unknown (in Ds), or unknown
            // or clouds invisible (in DH, DL, DM)
            // / Report from a coastal land station or displacement of ship not reported (in Ds only
            // see
            // Regulation 12.3.1.2 (b))

            // NB see myposition.java
            //
            // public static final String COURSE_STATIONARY = "stationary";
            // public static final String COURSE_023_067    = "023 - 067";
            // public static final String COURSE_068_112    = "068 - 112";
            // public static final String COURSE_113_157    = "113 - 157";
            // public static final String COURSE_158_202    = "158 - 202";
            // public static final String COURSE_203_247    = "203 - 247";
            // public static final String COURSE_248_292    = "248 - 292";
            // public static final String COURSE_293_337    = "293 - 337";
            // public static final String COURSE_338_022    = "338 - 022";

            if (SOG < 1.0) // stopped
            {
              // by default: in case SOG is indicating stopped then SOG always stopped also!
              myposition.jRadioButton5.setSelected(true); // COG = stopped
            }
            // else if (SOG >= 1.0 && SOG <= 999.9) // not stopped (halverwege de test maar
            // veranderd om toch een richting tijdens laatste testjes te krijgen)
            else if (SOG >= 1.0 && SOG <= 999999.9) // not stopped
            {
              if (COG > 23.0 && COG <= 67.0) {
                myposition.jRadioButton6.setSelected(true);
              } else if (COG > 67.0 && COG <= 112.0) {
                myposition.jRadioButton7.setSelected(true);
              } else if (COG > 112.0 && COG <= 157.0) {
                myposition.jRadioButton8.setSelected(true);
              } else if (COG > 157.0 && COG <= 202.0) {
                myposition.jRadioButton9.setSelected(true);
              } else if (COG > 202.0 && COG <= 247.0) {
                myposition.jRadioButton10.setSelected(true);
              } else if (COG > 247.0 && COG <= 292.0) {
                myposition.jRadioButton11.setSelected(true);
              } else if (COG > 292.0 && COG <= 337.0) {
                myposition.jRadioButton12.setSelected(true);
              } else if (COG > 337.0 && COG <= 360.0 || COG > 0.0 && COG <= 23.0) {
                myposition.jRadioButton13.setSelected(true);
              }
            } // else if (SOG >= 1.0 && SOG <= 99.9)

            // below for testing
            // System.out.println("+++ double_lat_start = " + double_lat_start);
            // System.out.println("+++ double_lon_start = " + double_lon_start);
            // System.out.println("+++ double_lat_end = " + double_lat_end);
            // System.out.println("+++ double_lon_end = " + double_lon_end);
            // System.out.println("+++ dist [metres] = " + dist);
            System.out.println("--- SOG [knots] = " + SOG);
            System.out.println("--- COG [deg] = " + COG);
          } // if (double_lat_start != Double.MAX_VALUE etc.
        } // if ((fix_ok) etc.

        if (fix_ok == false) {
          message =
              "[GPS] error; no sensor data file available / formatting error last saved record / last saved record > 5 minutes old / checksum not ok";

          // reset all the previous inserted values in the position inut screen
          myposition.jTextField1.setText(""); // lat deg
          myposition.jTextField2.setText(""); // lat min
          myposition.jTextField3.setText(""); // lon deg
          myposition.jTextField4.setText(""); // lon min
          myposition.buttonGroup1.clearSelection(); // N/S
          myposition.buttonGroup2.clearSelection(); // E/W
          myposition.buttonGroup3.clearSelection(); // course
          myposition.buttonGroup4.clearSelection(); // speed
        } // if (fix_ok == false)

        if (fix_ok == false) {
          if (mode.equals("APR")) // e.g. every 1, 3 or 6 hours
          {
            // message (was set before)
            main.log_turbowin_system_message(message);
          } else if (mode.equals("MANUAL")) {
            // message (was set before)
            main.log_turbowin_system_message(message);

            // temporary message box (eg mismatch GPS date-time and computer date-time)
            String info = "no reliable GPS position available";

            final JOptionPane pane_end =
                new JOptionPane(
                    info,
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null,
                    new Object[] {},
                    null);
            final JDialog temp_dialog = pane_end.createDialog(main.APPLICATION_NAME);

            Timer timer_end =
                new Timer(
                    2500,
                    new ActionListener() {
                      @Override
                      public void actionPerformed(ActionEvent e) {
                        temp_dialog.dispose();
                      }
                    });
            timer_end.setRepeats(false);
            timer_end.start();
            temp_dialog.setVisible(true);
          } // else if (mode.equals("MANUAL"))
        } // if (fix_ok == false)
        else // fix_ok == true
        {
          if (mode.equals("APR")
              || mode.equals(
                  "MANUAL")) // only in APR mode (1, 3, 6 hours) or manual mode, otherwise every
          // minute message lines in the log
          {
            // logging
            //
            message_fix_position =
                "GPS position (dd-mm [N/S] ddd-mm [E/W]): "
                    + myposition.latitude_degrees
                    + "-"
                    + myposition.latitude_minutes
                    + " "
                    + myposition.latitude_hemisphere.substring(0, 1)
                    + " "
                    + myposition.longitude_degrees
                    + "-"
                    + myposition.longitude_minutes
                    + " "
                    + myposition.longitude_hemisphere.substring(0, 1);

            // main.log_turbowin_system_message("[GPS] date-time parsing ok; " +
            // message_fix_date_time);
            main.log_turbowin_system_message(
                "[GPS] last position parsing ok; " + message_fix_position);
          }
        } // else (fix_ok == true)
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }

  public static void RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(
      boolean local_tray_icon_clicked, final boolean StarX) {
    // called from: - initSynopparameters() [mybarometer.java]
    //              - main_windowIconfied() [main.java]
    //
    //
    // MINTAKA STAR has an integrated GPS
    // GPS data is part of th saved pressure string eg:
    // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
    // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
    // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
    // <course> is True, <speed> in knots, <elevation> in meters
    //
    // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
    // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
    //
    //
    // STARX (NB first part of STARX is the same as the STAR)
    //       TurboWinH
    //             <station pressure in mb>,
    //             <sea level pressure in mb>,
    //             <3 hour pressure tendency>,
    //             <WMO tendency characteristic code>,
    //             <lat>,
    //             <long>,
    //             <course>,
    //             <speed>,
    //             <elevation>,
    //             <temperature>,
    //             <relativeHumidity>,
    //             <wetBulbTemperature>,
    //             <dewPoint>,
    //             <observationAge>
    //             *<checksum>
    //
    //             <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W], <course> is True,
    //             <speed> in knots, <elevation> in meters, <relativeHumidity> is 0-100,
    //             temperatures are in degrees celsius, <observatoinAge> is in seconds.
    //
    // 1009.73,1007.73,0.00,0, 52 41.9491N,  6 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16

    // initialisation
    mybarometer.pressure_reading = "";

    // initialisation
    main.tray_icon_clicked = local_tray_icon_clicked;

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;
        String local_obs_age = "";
        int pos1 = 0; // position of the first "," in the last record
        int pos2 = 0; // position of the second "," in the last record
        int pos3 = 0; // position of the third "," in the last record
        int pos4 = 0;
        int pos5 = 0;
        int pos6 = 0;
        int pos7 = 0;
        int pos8 = 0;
        int pos9 = 0;
        int pos10 = 0;
        int pos11 = 0;
        int pos12 = 0;
        int pos13 = 0;
        int pos14 = 0; // pos of the "*"

        // initialisation
        main.sensor_data_record_obs_pressure = "";

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
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null;

            // retrieve the last record in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              laatste_record = record;
            } // while ((record = in.readLine()) != null)

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Star or StarX format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record (Star and StarX)
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = -1;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos != -1) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos != -1);

              if (StarX == false) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
                  laatste_record = null;
                  System.out.println(
                      "--- Mintaka Star format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                }
              } // if (StarX == false)
              else if (StarX == true) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                  laatste_record = null;
                  System.out.println(
                      "--- Mintaka StarX format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                  // System.out.println("--- Mintaka StarX format read commas = " +
                  // number_read_commas);
                  // System.out.println("--- Mintaka StarX format format commas = " +
                  // main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX);
                }
              } // else if (StarX == true)
            } // if (laatste_record != null)

            // last retrieved record ok
            //
            if (laatste_record != null) {
              // System.out.println("+++ Mintaka Duo, last retrieved record = " + laatste_record);

              // String record_datum_tijd_minuten =
              // laatste_record.substring(main.type_record_datum_tijd_begin_pos,
              // main.type_record_datum_tijd_begin_pos + 12);  // bv 201302201345

              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();

              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              /// System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max 5 minutes old
              {
                // cheksum check
                //
                // example Mintaka Star last_record : 1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211
                // example Mintaka StarX last record: 1018.12,1018.13,0.00,0, 16 30.4429N, 88
                // 21.9040W,0,2,7,29.4,85,27.4,26.7,1017.89,60*18
                //
                String record_checksum =
                    laatste_record.substring(
                        laatste_record.length() - 14,
                        laatste_record.length()
                            - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211"
                String computed_checksum = Mintaka_Star_Checksum(laatste_record);

                if (computed_checksum.equals(record_checksum)) {
                  if (StarX == false) {
                    pos1 =
                        laatste_record.indexOf(
                            ",", 0); // position of the first "," in the last record

                    // not StarX then there is no obs age to be checked
                    local_obs_age = "0";
                    main.sensor_data_record_obs_pressure = laatste_record.substring(0, pos1);
                  } else if (StarX == true) {
                    // StarX example: // 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                    // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                    pos1 =
                        laatste_record.indexOf(
                            ",", 0); // ML hereafter; position of the first "," in the last record
                    pos2 =
                        laatste_record.indexOf(
                            ",", pos1 + 1); // ppp hereafter; position of the second "," in the last
                    // record
                    pos3 =
                        laatste_record.indexOf(
                            ",",
                            pos2 + 1); // a hereafter; position of the third "," in the last record
                    pos4 =
                        laatste_record.indexOf(
                            ",",
                            pos3 + 1); // lat hereafter; position of the 4th "," in the last record
                    pos5 =
                        laatste_record.indexOf(
                            ",",
                            pos4
                                + 1); // lon hereafter; position n of the 5th "," in the last record
                    pos6 =
                        laatste_record.indexOf(
                            ",", pos5 + 1); // course hereafter; position of the 6th "," in the last
                    // record
                    pos7 = laatste_record.indexOf(",", pos6 + 1); // speed hereafter
                    pos8 = laatste_record.indexOf(",", pos7 + 1); // elevation hereafter
                    pos9 = laatste_record.indexOf(",", pos8 + 1); // air temp
                    pos10 = laatste_record.indexOf(",", pos9 + 1); // RH
                    pos11 = laatste_record.indexOf(",", pos10 + 1); // wet bulb
                    pos12 = laatste_record.indexOf(",", pos11 + 1); // dew point
                    pos13 = laatste_record.indexOf(",", pos12 + 1); // observation age
                    pos14 = laatste_record.indexOf("*", pos13 + 1); // pos of the "*"

                    local_obs_age = laatste_record.substring(pos13 + 1, pos14);
                    main.sensor_data_record_obs_pressure = laatste_record.substring(0, pos1);
                  }

                  if (main.tray_icon_clicked == true) {
                    System.out.println(
                        "--- sensor data record, pressure for tray icon: "
                            + main.sensor_data_record_obs_pressure);
                  } else {
                    System.out.println(
                        "--- sensor data record, pressure for barometer form: "
                            + main.sensor_data_record_obs_pressure);
                  }
                } // if (computed_checksum.equals(record_checksum))
                else // checksum not ok
                {
                  local_obs_age = "";
                  main.sensor_data_record_obs_pressure = "";
                  System.out.println(
                      "--- automatically retrieved barometer reading checksum not ok");
                } // else
              } // if (timeDiff <= 5L)
              else // timediff exceed limit
              {
                local_obs_age = "";
                main.sensor_data_record_obs_pressure = "";
                System.out.println("--- automatically retrieved barometer reading obsolete");
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return local_obs_age;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        final int AGE_NOT_OK = 999999;
        String error_info = "";
        double hulp_double_pressure_reading = Double.MAX_VALUE;

        try {
          // obs age (in sec) of the last record
          //
          String return_obs_age = get();

          int int_return_obs_age = AGE_NOT_OK; // 999999 = random number but > 99.9
          if ((return_obs_age.compareTo("") != 0)
              && (return_obs_age != null)
              && (return_obs_age.indexOf("*") == -1)) {
            try {
              int_return_obs_age = Integer.parseInt(return_obs_age.trim());
            } catch (NumberFormatException e) {
              int_return_obs_age = AGE_NOT_OK;
              System.out.println(
                  "--- "
                      + "RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs() "
                      + e);
              // return_obs_age = "";
            }
          }

          if ((int_return_obs_age >= 0) && (int_return_obs_age <= MAX_AGE_STARX_OBS_DATA)) {
            if ((main.sensor_data_record_obs_pressure.compareTo("") != 0)
                && (main.sensor_data_record_obs_pressure != null)) {
              // double hulp_double_pressure_reading;
              try {
                hulp_double_pressure_reading =
                    Double.parseDouble(main.sensor_data_record_obs_pressure.trim());
                hulp_double_pressure_reading =
                    Math.round(hulp_double_pressure_reading * 10) / 10.0d; // bv 998.19 -> 998.2
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs() "
                        + e);
                error_info = "air pressure data parsing error";
                hulp_double_pressure_reading = Double.MAX_VALUE;
              }

              if (!(hulp_double_pressure_reading > 900.0)
                  && (hulp_double_pressure_reading < 1100.0)) {
                error_info = "air pressure data outside boundary limits";
                hulp_double_pressure_reading = Double.MAX_VALUE;
              }

            } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
            else {
              error_info = "air pressure data not available";
              hulp_double_pressure_reading = Double.MAX_VALUE;
            }

          } // if ((hulp_return_obs_age >= 0) && (hulp_return_obs_age <= MAX_AGE_STARX_OBS_DATA))
          else {
            error_info = "air pressure data obsolete";
            hulp_double_pressure_reading = Double.MAX_VALUE;
          }

        } // try
        catch (InterruptedException | ExecutionException ex) {
          error_info = "error retrieving air pressure data (" + ex + ")";
          hulp_double_pressure_reading = Double.MAX_VALUE;
        } // catch

        // general error eg NumberFormatException when parsing
        if (error_info.equals("") && (hulp_double_pressure_reading > 999999.9)) {
          error_info = "automatically retrieved air pressure general error or checksum not ok";
        }

        if (!error_info.equals("")) {
          System.out.println("--- " + error_info);

          final JOptionPane pane_end =
              new JOptionPane(
                  error_info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog dialog = pane_end.createDialog(main.APPLICATION_NAME);

          Timer timer_end =
              new Timer(
                  2500,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      dialog.dispose();
                    }
                  });
          timer_end.setRepeats(false);
          timer_end.start();
          dialog.setVisible(true);
        } // if (!error_info.equals(""))
        else // no error encountered
        {
          if (main.tray_icon_clicked == true) {
            String pressure_sensor_height = "";

            // NB pressure at sensor height = pressure reading + ic
            // double double_barometer_instrument_correction =
            // Double.parseDouble(main.barometer_instrument_correction.trim());
            double double_barometer_instrument_correction = 0.0;
            try {
              double_barometer_instrument_correction =
                  Double.parseDouble(main.barometer_instrument_correction.trim());
            } catch (NumberFormatException ex) {
              // eg if ic was never inserted ("barometer_instrument_correction" then empty string)
              double_barometer_instrument_correction = 0.0;
            }

            if ((double_barometer_instrument_correction > -4.0)
                && (double_barometer_instrument_correction < 4.0)) {
              // 1 digit precision
              // NB example (double)Math.round(value * 100000d) / 100000d -> rounding for 5 digits
              // precision
              pressure_sensor_height =
                  Double.toString(
                      Math.round(
                              (hulp_double_pressure_reading
                                      + double_barometer_instrument_correction)
                                  * 10d)
                          / 10d);
            } else {
              // 1 digit precision
              pressure_sensor_height =
                  Double.toString(Math.round(hulp_double_pressure_reading * 10d) / 10d);
            }

            main_RS232_RS422.cal_system_date_time =
                new GregorianCalendar(
                    new SimpleTimeZone(0, "UTC")); // geeft systeem datum tijd in UTC van dit moment
            main_RS232_RS422.cal_system_date_time.add(
                Calendar.MINUTE, -1); // averaged the data is 1 minute old
            String date_time =
                main_RS232_RS422.sdf8.format(main_RS232_RS422.cal_system_date_time.getTime());

            String info = "";
            info =
                date_time
                    + " UTC "
                    + "\n"
                    + "\n"
                    + "pressure at sensor height: "
                    + pressure_sensor_height
                    + " hPa"
                    + "\n";
            // info = date_time + " UTC " + main.newline +
            //       main.newline +
            //       "pressure at sensor height: " + mybarometer.pressure_reading + " hPa" +
            // main.newline;

            // main.trayIcon.displayMessage(main.APPLICATION_NAME, info, TrayIcon.MessageType.INFO);
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
          } // if (main.tray_icon_clicked == true)
          else // barometer input screen opened
          {
            mybarometer.pressure_reading = Double.toString(hulp_double_pressure_reading);

            // check if barometer input page is opened
            if (mybarometer.jTextField1 != null) {
              // http://stackoverflow.com/questions/17397442/how-to-check-if-a-jframe-is-opened
            }

            mybarometer.jTextField1.setText(mybarometer.pressure_reading);
            mybarometer.jTextField2.requestFocus(); // focus on "insert draft" textfield
          } // else
        } // else (no error encountered)
      } // protected void done()
    }.execute(); // new SwingWorker<String, Void>()
  }

  public static void RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs(
      final boolean StarX) {
    // called from: - initSynopparameters() [mybarograph.java]

    // MINTAKA STAR has an integrated GPS
    // GPS data is part of th saved pressure string eg:
    // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
    // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
    // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
    // <course> is True, <speed> in knots, <elevation> in meters
    //
    // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
    // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
    //
    //
    // STARX (NB first part of STARX is the same as the STAR)
    //       TurboWinH
    //             <station pressure in mb>,
    //             <sea level pressure in mb>,
    //             <3 hour pressure tendency>,
    //             <WMO tendency characteristic code>,
    //             <lat>,
    //             <long>,
    //             <course>,
    //             <speed>,
    //             <elevation>,
    //             <temperature>,
    //             <relativeHumidity>,
    //             <wetBulbTemperature>,
    //             <dewPoint>,
    //             <observationAge>
    //             *<checksum>
    //
    //             <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W], <course> is True,
    //             <speed> in knots, <elevation> in meters, <relativeHumidity> is 0-100,
    //             temperatures are in degrees celsius, <observatoinAge> is in seconds.
    //
    // 1009.73,1007.73,0.00,0, 52 41.9491N,  6 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16

    // initialisation
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;
        String local_obs_age = "";
        int pos1 = 0; // position of the first "," in the last record
        int pos2 = 0; // position of the second "," in the last record
        int pos3 = 0; // position of the third "," in the last record
        int pos4 = 0;
        int pos5 = 0;
        int pos6 = 0;
        int pos7 = 0;
        int pos8 = 0;
        int pos9 = 0;
        int pos10 = 0;
        int pos11 = 0;
        int pos12 = 0;
        int pos13 = 0;
        int pos14 = 0; // pos of the "*"

        // initialisation
        // main.sensor_data_record_obs_pressure = "";

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
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null;

            // retrieve the last record in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              laatste_record = record;
            } // while ((record = in.readLine()) != null)

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Star or StarX format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = -1;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos != -1) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos != -1);

              if (StarX == false) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
                  laatste_record = null;
                  System.out.println(
                      "--- Mintaka Star format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                }
              } // if (StarX == false)
              else if (StarX == true) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                  laatste_record = null;
                  System.out.println(
                      "--- Mintaka StarX format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                }
              } // else if (StarX == true)
            } // if (laatste_record != null)

            // last retrieved record ok
            if (laatste_record != null) {
              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();

              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              /// System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max ? minutes old
              {
                // cheksum check
                //
                // example Mintaka Star last_record:  1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211
                // example Mintaka StarX last record: 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                //
                String record_checksum =
                    laatste_record.substring(
                        laatste_record.length() - 14,
                        laatste_record.length()
                            - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211"
                String computed_checksum = Mintaka_Star_Checksum(laatste_record);

                /**** TEST BEGIN *****/
                // computed_checksum = record_checksum;

                if (computed_checksum.equals(record_checksum)) {
                  if (StarX == false) {
                    pos1 =
                        laatste_record.indexOf(
                            ",", 0); // position of the first "," in the last record
                    pos2 =
                        laatste_record.indexOf(
                            ",", pos1 + 1); // position of the second "," in the last record
                    pos3 =
                        laatste_record.indexOf(
                            ",", pos2 + 1); // position of the third "," in the last record
                    pos4 = laatste_record.indexOf(",", pos3 + 1);

                    main.sensor_data_record_obs_ppp = laatste_record.substring(pos2 + 1, pos3);
                    main.sensor_data_record_obs_a = laatste_record.substring(pos3 + 1, pos4);

                    // if not StarX then there is no obs age to be checked
                    local_obs_age = "0";

                    System.out.println(
                        "--- sensor data record, tendency for obs: "
                            + main.sensor_data_record_obs_ppp);
                    System.out.println(
                        "--- sensor data record, characteristic for obs: "
                            + main.sensor_data_record_obs_a);

                  } // if (StarX == false)
                  else if (StarX == true) {
                    // StarX example: // 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                    // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                    // sensor height pressure
                    pos1 =
                        laatste_record.indexOf(
                            ",", 0); // MSLP hereafter; position of the first "," in the last record
                    pos2 =
                        laatste_record.indexOf(
                            ",", pos1 + 1); // ppp hereafter; position of the second "," in the last
                    // record
                    pos3 =
                        laatste_record.indexOf(
                            ",",
                            pos2 + 1); // a hereafter; position of the third "," in the last record
                    pos4 =
                        laatste_record.indexOf(
                            ",",
                            pos3 + 1); // lat hereafter; position of the 4th "," in the last record
                    pos5 =
                        laatste_record.indexOf(
                            ",",
                            pos4
                                + 1); // lon hereafter; position n of the 5th "," in the last record
                    pos6 =
                        laatste_record.indexOf(
                            ",", pos5 + 1); // course hereafter; position of the 6th "," in the last
                    // record
                    pos7 = laatste_record.indexOf(",", pos6 + 1); // speed hereafter
                    pos8 = laatste_record.indexOf(",", pos7 + 1); // elevation hereafter
                    pos9 = laatste_record.indexOf(",", pos8 + 1); // air temp
                    pos10 = laatste_record.indexOf(",", pos9 + 1); // RH
                    pos11 = laatste_record.indexOf(",", pos10 + 1); // wet bulb
                    pos12 = laatste_record.indexOf(",", pos11 + 1); // dew point
                    pos13 = laatste_record.indexOf(",", pos12 + 1); // observation age
                    pos14 = laatste_record.indexOf("*", pos13 + 1); // pos of the "*"

                    main.sensor_data_record_obs_ppp = laatste_record.substring(pos2 + 1, pos3);
                    main.sensor_data_record_obs_a = laatste_record.substring(pos3 + 1, pos4);

                    local_obs_age = laatste_record.substring(pos13 + 1, pos14);

                    System.out.println(
                        "--- sensor data record, tendency for obs: "
                            + main.sensor_data_record_obs_ppp);
                    System.out.println(
                        "--- sensor data record, characteristic for obs: "
                            + main.sensor_data_record_obs_a);
                    System.out.println("--- sensor data record, obs age (StarX): " + local_obs_age);
                  } // else if (StarX == true)

                } // if (computed_checksum.equals(record_checksum))
                else // checksum not ok
                {
                  main.sensor_data_record_obs_ppp = "";
                  main.sensor_data_record_obs_a = "";
                  local_obs_age = "";
                } // else (checksum not ok)
              } // if (timeDiff <= 5L)
              else {
                main.sensor_data_record_obs_ppp = "";
                main.sensor_data_record_obs_a = "";
                local_obs_age = "";
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs(): "
                    + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return local_obs_age;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        int hulp_pressure_change =
            3; // -1 negative; 1 positive; 0 same pressure; 3 is only start value
        final int AGE_NOT_OK = 999999;
        String error_info = "";
        double hulp_double_ppp_reading = Double.MAX_VALUE;
        int hulp_int_a_reading = Integer.MAX_VALUE;

        try {
          // obs age (in sec) of the last record
          //
          String return_obs_age = get();

          int int_return_obs_age = AGE_NOT_OK; // 999999 = random number but > 99.9
          if ((return_obs_age.compareTo("") != 0)
              && (return_obs_age != null)
              && (return_obs_age.indexOf("*") == -1)) {
            try {
              int_return_obs_age = Integer.parseInt(return_obs_age.trim());
            } catch (NumberFormatException e) {
              int_return_obs_age = AGE_NOT_OK;
              System.out.println(
                  "--- "
                      + "RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs() "
                      + e);
              // return_obs_age = "";
            }
          }

          if ((int_return_obs_age >= 0)
              && (int_return_obs_age <= main_RS232_RS422.MAX_AGE_STARX_OBS_DATA)) {
            // ppp (tendency)
            //
            if ((main.sensor_data_record_obs_ppp.compareTo("") != 0)
                && (main.sensor_data_record_obs_ppp != null)
                && (main.sensor_data_record_obs_ppp.indexOf("*") == -1)) {
              // double hulp_double_ppp_reading;
              try {
                hulp_double_ppp_reading =
                    Double.parseDouble(main.sensor_data_record_obs_ppp.trim());
                hulp_double_ppp_reading =
                    Math.round(hulp_double_ppp_reading * 10) / 10.0d; // e.g. 13.19 -> 13.2

                // System.out.println("+++ hulp_double_ppp_reading = " + hulp_double_ppp_reading);
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_For_Obs() "
                        + e);
                hulp_double_ppp_reading = Double.MAX_VALUE;
              }

              if ((hulp_double_ppp_reading > -99.9) && (hulp_double_ppp_reading < 99.9)) {
                // NB hulp_pressure_change: a help to determine a (pressure characteristic)
                if (hulp_double_ppp_reading > 0.0) {
                  hulp_pressure_change = 1;
                } else if (hulp_double_ppp_reading < 0.0) {
                  hulp_pressure_change = -1;
                } else if (hulp_double_ppp_reading == 0.0) {
                  hulp_pressure_change = 0;
                }

                // System.out.println("+++ hulp_pressure_change = " + hulp_pressure_change);

                // tendency
                // mybarograph.pressure_amount_tendency =
                // Double.toString(Math.abs(hulp_double_ppp_reading));  // only positive value in
                // this field
                // mybarograph.jTextField1.setText(mybarograph.pressure_amount_tendency);
              }
            } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
            else {
              error_info = "pressure tendency and/or characteristic not available";
              hulp_double_ppp_reading = Double.MAX_VALUE;
            }

            // a (characteristic)
            //
            if ((main.sensor_data_record_obs_a.compareTo("") != 0)
                && (main.sensor_data_record_obs_a != null)
                && (main.sensor_data_record_obs_a.indexOf("*") == -1)) {
              // int hulp_int_a_reading;
              try {
                hulp_int_a_reading = Integer.parseInt(main.sensor_data_record_obs_a.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_For_Obs() "
                        + e);
                hulp_int_a_reading = Integer.MAX_VALUE;
              }

            } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
            else {
              error_info = "pressure tendency and/or characteristic not available";
              hulp_int_a_reading = Integer.MAX_VALUE;
            }

          } // if ((hulp_return_obs_age >= 0) && (hulp_return_obs_age <= MAX_AGE_STARX_OBS_DATA))
          else {
            error_info = "pressure tendency and characteristic data obsolete or checksum not ok";
            hulp_double_ppp_reading = Double.MAX_VALUE;
            hulp_int_a_reading = Integer.MAX_VALUE;
          }

        } // try
        catch (InterruptedException | ExecutionException ex) {
          error_info = "error retrieving pressure tendency and characteristic data (" + ex + ")";
          hulp_double_ppp_reading = Double.MAX_VALUE;
          hulp_int_a_reading = Integer.MAX_VALUE;
        } // catch

        // general error eg NumberFormatException when parsing hulp_int_a_reading =
        // Integer.parseInt(main.sensor_data_record_obs_a.trim());
        if (error_info.equals("")
            && (hulp_double_ppp_reading > 999999.9 || hulp_int_a_reading > 999999)) {
          error_info =
              "automatically retrieved pressure tendency and characteristic general error or checksum not ok";
        }

        if (!error_info.equals("")) {
          System.out.println("--- " + error_info);

          final JOptionPane pane_end =
              new JOptionPane(
                  error_info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog dialog = pane_end.createDialog(main.APPLICATION_NAME);

          Timer timer_end =
              new Timer(
                  2500,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      dialog.dispose();
                    }
                  });
          timer_end.setRepeats(false);
          timer_end.start();
          dialog.setVisible(true);
        } // if (!error_info.equals(""))
        else // no error encountered
        {
          // set tendency textfield on a-ppp input page
          //
          mybarograph.pressure_amount_tendency =
              Double.toString(
                  Math.abs(hulp_double_ppp_reading)); // only positive value in this field
          mybarograph.jTextField1.setText(mybarograph.pressure_amount_tendency);

          // set characteristic on a-ppp input page
          //
          if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8)) {
            // initialisation
            mybarograph.jRadioButton1.setSelected(false);
            mybarograph.jRadioButton2.setSelected(false);
            mybarograph.jRadioButton3.setSelected(false);
            mybarograph.jRadioButton4.setSelected(false);
            mybarograph.jRadioButton5.setSelected(false);
            mybarograph.jRadioButton6.setSelected(false);
            mybarograph.jRadioButton7.setSelected(false);
            mybarograph.jRadioButton8.setSelected(false);
            mybarograph.jRadioButton9.setSelected(false);
            mybarograph.jRadioButton10.setSelected(false);
            mybarograph.jRadioButton11.setSelected(false);
            mybarograph.jRadioButton12.setSelected(false);

            // pressure higher than 3hrs ago
            if (hulp_pressure_change == 1) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_0))
                mybarograph.jRadioButton1.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_1))
                mybarograph.jRadioButton2.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_2))
                mybarograph.jRadioButton3.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_3))
                mybarograph.jRadioButton4.setSelected(true);
            }

            // pressure lower than 3hrs ago
            else if (hulp_pressure_change == -1) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_5))
                mybarograph.jRadioButton5.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_6))
                mybarograph.jRadioButton6.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_7))
                mybarograph.jRadioButton7.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_8))
                mybarograph.jRadioButton8.setSelected(true);
            }

            // pressure the same as 3hrs ago
            else if (hulp_pressure_change == 0) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_0_SAME))
                mybarograph.jRadioButton9.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_4))
                mybarograph.jRadioButton10.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_5_SAME))
                mybarograph.jRadioButton11.setSelected(true);
            } else {
              mybarograph.jRadioButton12.setSelected(true); // not determined
            }
          } // if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8))
        } // else ( no error encountered)
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }

  private static void RS232_Mintaka_Star_And_StarX_Determine_Position_Earlier_For_Wind(
      String date_time_SOG_COG_end, final boolean StarX) {

    // called by: RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()
    // [RS232_mintaka.java]
    //
    // NB called from within a SwingWorker
    //
    // NB only in case of FM13 (for wind calculations we need 10 min SOG and COG, format101 is
    // already 10 min SOG COG based)

    String date_time_SOG_COG_start_wind =
        null; // for retrieving GPS position earlier (10 min or 3 hours) eg 201709250406 (last 12
    // char of record)
    String record = null;
    final int COG_SOG_diff_min_wind = 10; // for wind calculations 10 min SOG and COG required

    // String date_time_SOG_COG_end = record_datum_tijd_minuten;  // date time of last retrieved
    // record eg 201709250400 (YYYYMMDDHHmm has length 12)

    if (date_time_SOG_COG_end != null) {
      // record_datum_tijd_minuten format: YYYYMMDDHHmm
      String year_end = date_time_SOG_COG_end.substring(0, 4);
      String month_end = date_time_SOG_COG_end.substring(4, 6); // January = 01
      String day_end = date_time_SOG_COG_end.substring(6, 8);
      String hour_end = date_time_SOG_COG_end.substring(8, 10);
      String minute_end = date_time_SOG_COG_end.substring(10, 12);
      GregorianCalendar cal_SOG_COG_date_wind =
          new GregorianCalendar(
              Integer.parseInt(year_end),
              Integer.parseInt(month_end) - 1, // convert to internal month representation
              Integer.parseInt(day_end),
              Integer.parseInt(hour_end),
              Integer.parseInt(minute_end));
      cal_SOG_COG_date_wind.setTimeZone(
          TimeZone.getTimeZone("UTC")); // !!!! (otherwise in local pc time zone)

      cal_SOG_COG_date_wind.add(Calendar.MINUTE, -COG_SOG_diff_min_wind); // substract 10 minutes
      date_time_SOG_COG_start_wind =
          main.sdf4.format(
              cal_SOG_COG_date_wind
                  .getTime()); // sdf4 : yyyyMMddHHmm eg 201701030812 // NB MM = 01 = January!! so
      // SimpleDateformat automatically converts the internal  month
      // representation [January = 0] to the human representation [January
      // = 1]

      String year_start = date_time_SOG_COG_start_wind.substring(0, 4);
      String month_start = date_time_SOG_COG_start_wind.substring(4, 6); // January = 01
      String day_start = date_time_SOG_COG_start_wind.substring(6, 8);
      String hour_start = date_time_SOG_COG_start_wind.substring(8, 10);

      String sensor_data_file_naam_datum_tijd_deel_start_wind =
          year_start + month_start + day_start + hour_start; // eg 2017092505

      // below for testing
      // System.out.println("+++ sensor_data_file_naam_datum_tijd_deel_start: " +
      // sensor_data_file_naam_datum_tijd_deel_start);

      // determine sensor data file name of the file with the GPS position of 10 minutes or 3 hours
      // ago
      String sensor_data_file_name_wind =
          "sensor_data_" + sensor_data_file_naam_datum_tijd_deel_start_wind + ".txt";

      // first check if there is a sensor data file present (and not empty)
      String volledig_path_sensor_data_wind =
          main.logs_dir + java.io.File.separator + sensor_data_file_name_wind;

      // below for testing
      // System.out.println("+++ te openen file earlier voor obs SOG and COG: "+
      // volledig_path_sensor_data);

      File sensor_data_file_wind = new File(volledig_path_sensor_data_wind);
      if (sensor_data_file_wind.exists() && sensor_data_file_wind.length() > 0) // length() in bytes
      {
        try (BufferedReader in =
            new BufferedReader(new FileReader(volledig_path_sensor_data_wind))) {
          record = null;
          while ((record = in.readLine()) != null) {
            if (record.length()
                > 15) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at leat
            // 2 char pressure value= 15 chars)
            {
              int pos = record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              if (record_datum_tijd_minuten.equals(
                  date_time_SOG_COG_start_wind)) // found the corresponding record of 10 min earlier
              {
                String record_checksum =
                    record.substring(
                        record.length() - 14,
                        record.length()
                            - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211"
                String computed_checksum = Mintaka_Star_Checksum(record);

                // below only for testing (uncomment when testing)
                // computed_checksum = record_checksum;

                if (computed_checksum.equals(record_checksum)) {
                  // System.out.println("checksum ok");

                  // extra check on correct number of commas in the earlier (10 min or 3 hrs) record
                  //
                  int number_read_commas = 0;
                  int pos_comma = -1;

                  do {
                    pos_comma = record.indexOf(",", pos_comma + 1);
                    if (pos_comma != -1) // "," found
                    {
                      number_read_commas++;
                      // System.out.println("+++ number_read_commas = " + number_read_commas);
                    }
                  } while (pos_comma != -1);

                  boolean format_ok = true;
                  if (StarX == false) {
                    if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
                      format_ok = false;
                      System.out.println(
                          "--- Mintaka Star format (number commas) rertieved record earlier (for SOG and COG wind) NOT ok (file: "
                              + volledig_path_sensor_data_wind
                              + "; record: "
                              + record
                              + ")");
                    }
                  } else if (StarX == true) {
                    if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                      format_ok = false;
                      System.out.println(
                          "--- Mintaka StarX format (number commas) rertieved record earlier (for SOG and COG wind) NOT ok (file: "
                              + volledig_path_sensor_data_wind
                              + "; record: "
                              + record
                              + ")");
                    }
                  }

                  if (format_ok) {
                    // retrieved (from file) record example Mintaka Star: 1029.97,1029.97,-0.90,7,
                    // 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006
                    int pos1 =
                        record.indexOf(",", 0); // position of the first "," in the last record
                    int pos2 =
                        record.indexOf(
                            ",", pos1 + 1); // position of the second "," in the last record
                    int pos3 =
                        record.indexOf(
                            ",", pos2 + 1); // position of the third "," in the last record
                    int pos4 =
                        record.indexOf(",", pos3 + 1); // position of the 4th "," in the last record
                    int pos5 =
                        record.indexOf(",", pos4 + 1); // position of the 5th "," in the last record
                    int pos6 =
                        record.indexOf(",", pos5 + 1); // position of the 6th "," in the last record
                    // int pos7 = record.indexOf(",", pos6 +1);
                    //   // position of the 7th "," in the last record

                    GPS_latitude_earlier_wind = record.substring(pos4 + 1, pos5);
                    GPS_longitude_earlier_wind = record.substring(pos5 + 1, pos6);

                    // below for testing
                    // System.out.println("+++ sensor data record, GPS latitude earlier (10 min): "
                    // + GPS_latitude_earlier_wind + "(" + record_datum_tijd_minuten + ")");
                    // System.out.println("+++ sensor data record, GPS longitude earlier (10 min): "
                    // + GPS_longitude_earlier_wind + "(" + record_datum_tijd_minuten + ")");

                    // if (destination.equals("MAIN_SCREEN") == false)
                    // {
                    //   System.out.println("--- sensor data record, GPS latitude earlier (10 min):
                    // " + GPS_latitude_earlier_wind + "(" + record_datum_tijd_minuten + ")");
                    //   System.out.println("--- sensor data record, GPS longitude earlier (10 min):
                    // " + GPS_longitude_earlier_wind + "(" + record_datum_tijd_minuten + ")");
                    // }
                  } // if (format_ok)
                  else {
                    GPS_latitude_earlier_wind = "";
                    GPS_longitude_earlier_wind = "";
                  }
                } // if (computed_checksum.equals(record_checksum))
                else {
                  // System.out.println("record checksum = " + record_checksum);
                  // System.out.println("computed checksum = " + computed_checksum);
                  System.out.println(
                      "--- checksum NOT ok, SOG and COG for wind in FM13 " + "(" + record + ")");

                  GPS_latitude_earlier_wind = "";
                  GPS_longitude_earlier_wind = "";
                } // else
              } // if (record_datum_tijd_minuten.equals(date_time_SOG_COG_start))
            } // if (record.length() > 15)
            else {
              GPS_latitude_earlier_wind = "";
              GPS_longitude_earlier_wind = "";
            }
          } // while ((record = in.readLine()) != null)

        } // try
        catch (FileNotFoundException ex) {
          System.out.println(
              "--- Function RS232_Mintaka_Star_And_StarX_Determine_Position_Earlier_For_Wind(): FileNotFoundException ("
                  + ex
                  + "");
        } catch (IOException ex) {
          System.out.println(
              "--- Function RS232_Mintaka_Star_And_StarX_Determine_Position_Earlier_For_Wind(): IOException ("
                  + ex
                  + "");
        }
      } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

    } // if (date_time_SOG_COG_end != null)
    else {
      GPS_latitude_earlier_wind = "";
      GPS_longitude_earlier_wind = "";
    } // else

    // below for testing
    // System.out.println("+++ sensor data record, GPS latitude earlier wind 10 min: " +
    // GPS_latitude_earlier_wind);
    // System.out.println("+++ sensor data record, GPS longitude earlier wind 10 min: " +
    // GPS_longitude_earlier_wind );

  }

  private static void RS232_Mintaka_Star_And_StarX_Write_Error_Info(
      final String destination, final String error_info) {
    // called from: RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()

    // NB 'destination' possible strings: - "WOW"
    //                                    - "APR"

    String tag = "";

    if (destination.equals("APR")) {
      tag = "[APR]";
    } else if (destination.equals("WOW")) {
      tag = "[WOW]";
    }

    if (error_info.equals(STRING_PRESSURE_RECORD_GPS_ERROR)) {
      main.log_turbowin_system_message(tag + " no GPS info in last 5 saved records");
    } else if (error_info.equals(STRING_PRESSURE_CHECKSUM_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved barometer reading: cheksum error ");
    } else if (error_info.equals(STRING_PRESSURE_TIMEDIFF_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved barometer reading: complete reading obsolete");
    } else if (error_info.equals(STRING_PRESSURE_OBS_AGE_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved barometer reading: StarX part obsolete");
    } else if (error_info.equals(STRING_PRESSURE_RECORD_FORMAT_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved barometer reading: error record format");
    } else if (error_info.equals(STRING_PRESSURE_IO_FILE_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved barometer reading: IO error opening sensor data file");
    } else if (error_info.equals(STRING_PRESSURE_NO_FILE_ERROR)) {
      main.log_turbowin_system_message(
          tag
              + " automatically retrieved barometer reading: error, no sensor data file or file empty");
    } else if (error_info.equals(STRING_PRESSURE_NUMBERFORMATECEPTION_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved barometer reading: error NumberFormatException");
    } else if (error_info.equals(STRING_PRESSURE_OUTSIDE_RANGE_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved barometer reading: outside range");
    } else if (error_info.equals(STRING_PRESSURE_RETRIEVING_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved barometer reading: error during retrieving data");
    } else if (error_info.equals(STRING_PRESSURE_HEIGHT_CORRECTION_ERROR)) {
      main.log_turbowin_system_message(tag + " computed height correction pressure not ok");
    } else if (error_info.equals(STRING_PRESSURE_INTERRUPTION_EXECUTIONEXEPTION_ERROR)) {
      main.log_turbowin_system_message(
          tag + " interruption or execution exception when reftrieving data from thread");
    } else if (error_info.equals(STRING_STARX_PART_OBSOLETE_ERROR)) {
      main.log_turbowin_system_message(
          tag + " StarX data obsolete (>" + main_RS232_RS422.MAX_AGE_STARX_OBS_DATA + " sec)");
    } else if (error_info.equals(STRING_PRESSURE_AMOUNT_TENDENCY_OUTSIDE_RANGE_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved pressure tendency: outside range");
    } else if (error_info.equals(STRING_PRESSURE_CHARACTERISTIC_OUTSIDE_RANGE_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved pressure characteristic (a): outside range");
    } else if (error_info.equals(STRING_AIR_TEMP_OUTSIDE_RANGE_ERROR)) {
      main.log_turbowin_system_message(tag + " automatically retrieved air temp: outside range");
    } else if (error_info.equals(STRING_WET_BULB_OUTSIDE_RANGE_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved wet-bulb temp: outside range");
    } else if (error_info.equals(STRING_DEWPOINT_OUTSIDE_RANGE_ERROR)) {
      main.log_turbowin_system_message(tag + " automatically retrieved dewpoint: outside range");
    } else if (error_info.equals(STRING_RH_OUTSIDE_RANGE_ERROR)) {
      main.log_turbowin_system_message(
          tag + " automatically retrieved RH (Relative Humidity): outside range");
    }
  }

  public static void RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
      final String destination, final boolean retry, final boolean StarX) {
    // called from: - class RS232_Class_Receive_Sensor_Data() [main_RS232_RS422.java]
    //              - class WiFi_Class_Receive_UDP() [main_RS232_RS422.java]
    //
    //
    // NB 'destination' possible strings: - "WOW"          // deprecated
    //                                    - "APR"          // means: only on APR scheduled hours
    //                                    - "MAIN_SCREEN"

    //
    // NB a STARX is always physically (WiFi) connected to a STAR, for simplicity I use the word
    // STARX as it would be a total new stand-alone device
    //
    // Mintaka Star has an integrated GPS
    // GPS data is part of the saved pressure string eg:
    //
    //
    // STAR
    // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
    // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
    // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
    // <course> is True, <speed> in knots, <elevation> in meters
    //
    // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
    // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
    // 1010.47,1010.47,-2.80,6,,,,,,20.8,63,16.3,13.5,246*01        // NB watch the - at ppp
    //
    //
    // 'STAR + STARX' (NB first part of 'STAR + STARX' is the same as the STAR) (NB STARX must
    // always be linked to a STAR)
    //       TurboWinH
    //             <station pressure in mb>,
    //             <sea level pressure in mb>,
    //             <3 hour pressure tendency>,
    //             <WMO tendency characteristic code>,
    //             <lat>,
    //             <long>,
    //             <course>,
    //             <speed>,
    //             <elevation>,
    //             <temperature>,
    //             <relativeHumidity>,
    //             <wetBulbTemperature>,
    //             <dewPoint>,
    //             <observationAge>
    //             *<checksum>
    //
    //             <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W], <course> is True,
    //             <speed> in knots, <elevation> in meters, <relativeHumidity> is 0-100,
    //             temperatures are in degrees celsius, <observatoinAge> is in seconds.
    //
    // 1009.73,1007.73,0.00,0, 52 41.9491N,  6 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16

    // initialisation
    GPS_latitude = "";
    GPS_longitude = "";
    GPS_latitude_hemisphere = "";
    GPS_longitude_hemisphere = "";
    main.sensor_data_record_obs_pressure = ""; // nodig?
    main.sensor_data_record_obs_ppp = ""; // nodig?
    main.sensor_data_record_obs_a = ""; // nodig?
    mybarometer.pressure_reading_corrected = "";
    mybarometer.pressure_msl_corrected = "";
    mytemp.air_temp = "";
    mytemp.wet_bulb_temp = "";
    mytemp.RH = "";
    mytemp.double_rv = main.INVALID;
    mytemp.double_dew_point = main.INVALID;
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;
        String error_info = "";
        String local_sensor_data_record_obs_pressure = "";
        String local_sensor_data_record_obs_ppp = "";
        String local_sensor_data_record_obs_a = "";
        String local_sensor_data_record_obs_temp = "";
        String local_sensor_data_record_obs_RH = "";
        String local_sensor_data_record_obs_wet_bulb = "";
        String local_sensor_data_record_obs_dew_point = "";
        String local_obs_age = "";
        final int AGE_NOT_OK = 99999;
        String sensor_data_file_naam_datum_tijd_deel_start =
            null; // for retrieving GPS position earlier (10 min or 3 hours) eg 2017092504 (file
        // name eg sensor_data_2017092505.txt)
        String date_time_SOG_COG_start =
            null; // for retrieving GPS position earlier (10 min or 3 hours) eg 201709250406 (last
        // 12 char of record)
        String date_time_SOG_COG_end = null;

        // determine sensor data file name
        String sensor_data_file_naam_datum_tijd_deel =
            main.sdf3.format(main.obs_file_datum_tijd.getTime()); // e.g. 2013020308
        String sensor_data_file_name =
            "sensor_data_" + sensor_data_file_naam_datum_tijd_deel + ".txt";

        // first check if there is a sensor data file present (and not empty)
        String volledig_path_sensor_data =
            main.logs_dir + java.io.File.separator + sensor_data_file_name;
        // System.out.println("+++ te openen file voor APR obs: "+ volledig_path_sensor_data);

        sensor_data_file = new File(volledig_path_sensor_data);
        if (sensor_data_file.exists() && sensor_data_file.length() > 0) // length() in bytes
        {
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null; // last record for all further manipulations
            String laatste_record_0 = null; // last record temperarily
            String laatste_record_1 = null; // 1 after last temperarily
            String laatste_record_2 = null; // 2 after last temperarily
            String laatste_record_3 = null; // 2 after last temperarily
            String laatste_record_4 = null; // 2 after last temperarily
            int num_read_records = 0;

            // retrieve the last 3 records in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              num_read_records++;

              if (num_read_records == 1) {
                laatste_record_0 = record;
              }

              if (num_read_records == 2) {
                laatste_record_1 = laatste_record_0;
                laatste_record_0 = record;
              }

              if (num_read_records == 3) {
                laatste_record_2 = laatste_record_1;
                laatste_record_1 = laatste_record_0;
                laatste_record_0 = record;
              }

              if (num_read_records == 4) {
                laatste_record_3 = laatste_record_2;
                laatste_record_2 = laatste_record_1;
                laatste_record_1 = laatste_record_0;
                laatste_record_0 = record;
              }

              if (num_read_records > 4) {
                laatste_record_4 = laatste_record_3;
                laatste_record_3 = laatste_record_2;
                laatste_record_2 = laatste_record_1;
                laatste_record_1 = laatste_record_0;
                laatste_record_0 = record;
              }
            } // while ((record = in.readLine()) != null)

            // check records on a GPS position APR destination
            //
            if (destination.equals("APR")) {
              // in APR mode the "laatste_record" is the last retrieved record with a least a few
              // char on the GPS position fields
              // this can be really the last retrieved record but also the 1 or 2,3,4 after the last
              // one
              // because for APR a GPS position must be present in the record
              boolean record_ok = false;

              // check last avaialble record
              if (laatste_record_0 != null) {
                record_ok = RS232_Mintaka_Star_And_StarX_Check_Record_GPS(laatste_record_0, StarX);
                if (record_ok) {
                  laatste_record = laatste_record_0;
                }
              } // if (laatste_record_0 != null)

              if (record_ok == false) {
                // check 1 after last record
                if (laatste_record_1 != null) {
                  record_ok =
                      RS232_Mintaka_Star_And_StarX_Check_Record_GPS(laatste_record_1, StarX);
                  if (record_ok) {
                    laatste_record = laatste_record_1;
                  }
                } // if (laatste_record_1 != null)
              } // if (record_ok == false)

              if (record_ok == false) {
                // check 2 after last record
                if (laatste_record_2 != null) {
                  record_ok =
                      RS232_Mintaka_Star_And_StarX_Check_Record_GPS(laatste_record_2, StarX);
                  if (record_ok) {
                    laatste_record = laatste_record_2;
                  }
                } // if (laatste_record_2 != null)
              } // if (record_ok == false)

              if (record_ok == false) {
                // check 3 after last record
                if (laatste_record_3 != null) {
                  record_ok =
                      RS232_Mintaka_Star_And_StarX_Check_Record_GPS(laatste_record_3, StarX);
                  if (record_ok) {
                    laatste_record = laatste_record_3;
                  }
                } // if (laatste_record_3 != null)
              } // if (record_ok == false)

              if (record_ok == false) {
                // check 4 after last record
                if (laatste_record_4 != null) {
                  record_ok =
                      RS232_Mintaka_Star_And_StarX_Check_Record_GPS(laatste_record_4, StarX);
                  if (record_ok) {
                    laatste_record = laatste_record_4;
                  } else {
                    error_info = STRING_PRESSURE_RECORD_GPS_ERROR;
                  }
                } // if (laatste_record_4 != null)
              } // if (record_ok == false)

              System.out.println("--- last retrieved record = " + laatste_record_0);
              System.out.println("--- 1 after last retrieved record = " + laatste_record_1);
              System.out.println("--- 2 after last retrieved record = " + laatste_record_2);
              System.out.println("--- 3 after last retrieved record = " + laatste_record_3);
              System.out.println("--- 4 after last retrieved record = " + laatste_record_4);
              System.out.println("--- last retrieved record with GPS info = " + laatste_record);
            } // if (destination.equals("APR"))

            // always continue with the last record (never mind the GPS position is present) if
            // MAIN_SCREEN destination
            //
            if (destination.equals("MAIN_SCREEN")) {
              // by default for the 1 minute update of the main screen
              // so for MAIN_SCREEN "laatste_record"ia always the last retrieved record nver ming
              // the GPS info is available in this record
              laatste_record = laatste_record_0; // NB could be null
            }

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;

                error_info = STRING_PRESSURE_RECORD_FORMAT_ERROR;
                if (destination.equals("MAIN_SCREEN") == false) {
                  System.out.println(
                      "--- Mintaka Star or StarX format (min. record length) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                }
              }
            } // if (laatste_record != null)

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = -1;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos != -1) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos != -1);

              if (StarX == true) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                  laatste_record = null;
                  error_info = STRING_PRESSURE_RECORD_FORMAT_ERROR;

                  if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println(
                        "--- Mintaka StarX format (number commas) last retrieved record NOT ok (file: "
                            + volledig_path_sensor_data
                            + ")");
                  }
                }
              } //  if (StarX == true)
              else {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
                  laatste_record = null;
                  error_info = STRING_PRESSURE_RECORD_FORMAT_ERROR;

                  if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println(
                        "--- Mintaka Star format (number commas) last retrieved record NOT ok (file: "
                            + volledig_path_sensor_data
                            + ")");
                  }
                  // local_sensor_data_record_obs_pressure = STRING_PRESSURE_RECORD_FORMAT_ERROR;
                }
              } // else
            } // if (laatste_record != null)

            // last retrieved record ok
            //
            if (laatste_record != null) {
              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();

              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              // System.out.println("+++ difference [min]: " + timeDiff); //differencs in min
              // System.out.println("+++ file last record date time [min, since January 1, 1970,
              // 00:00:00 GMT]: " + file_date.getTime() / (60 * 1000)); // in min
              // System.out.println("+++ system date time [min, since January 1, 1970, 00:00:00
              // GMT]: " + system_sec / (60 * 1000)); // in min
              // System.out.println("+++ file last record date time [YYYYMMDDUUMM]: " +
              // record_datum_tijd_minuten);
              // System.out.println("+++ file: " + volledig_path_sensor_data);

              // NB COG and SOG must be computed over last 10 minutes (format 101) or last 3 hours
              // (FM13)
              int COG_SOG_diff_min = 0;
              if (main.obs_format.equals(main.FORMAT_101)) {
                COG_SOG_diff_min = 10; // 10 minutes (between positions to compute SOG and COG)
              } else if (main.obs_format.equals(main.FORMAT_FM13)) {
                COG_SOG_diff_min =
                    180; // 180 minutes (3 hours, between positions to compute SOG and COG)
              }
              // NB GregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int
              // minute)
              // NB Constructs a GregorianCalendar with the given date and time set for the default
              // time zone with the default locale.
              // NB month 0..11            // The first month of the year in the Gregorian and
              // Julian calendars is JANUARY which is 0
              // NB dayOfMonth 1..31       // The first day of the month has value 1
              // NB hourOfDay: 0.. 23      // Field number for get and set indicating the hour of
              // the day. HOUR_OF_DAY is used for the 24-hour clock. E.g., at 10:04:15.250 PM the
              // HOUR_OF_DAY is 22.

              // String date_time_SOG_COG_end = record_datum_tijd_minuten;  // date time of last
              // retrieved record eg 201709250400 (YYYYMMDDHHmm has length 12)
              date_time_SOG_COG_end =
                  record_datum_tijd_minuten; // date time of last retrieved record eg 201709250400
              // (YYYYMMDDHHmm has length 12)

              // record_datum_tijd_minuten format: YYYYMMDDHHmm
              String year_end = date_time_SOG_COG_end.substring(0, 4);
              String month_end = date_time_SOG_COG_end.substring(4, 6); // January = 01
              String day_end = date_time_SOG_COG_end.substring(6, 8);
              String hour_end = date_time_SOG_COG_end.substring(8, 10);
              String minute_end = date_time_SOG_COG_end.substring(10, 12);
              GregorianCalendar cal_SOG_COG_date =
                  new GregorianCalendar(
                      Integer.parseInt(year_end),
                      Integer.parseInt(month_end) - 1, // convert to internal month representation
                      Integer.parseInt(day_end),
                      Integer.parseInt(hour_end),
                      Integer.parseInt(minute_end));
              cal_SOG_COG_date.setTimeZone(
                  TimeZone.getTimeZone("UTC")); // !!!! (otherwise in local pc time zone)

              cal_SOG_COG_date.add(
                  Calendar.MINUTE,
                  -COG_SOG_diff_min); // substract 10 minutes (format 101) or 3 hours (FM13)
              date_time_SOG_COG_start =
                  main.sdf4.format(
                      cal_SOG_COG_date
                          .getTime()); // sdf4 : yyyyMMddHHmm eg 201701030812 // NB MM = 01 =
              // January!! so SimpleDateformat automatically converts the
              // internal  month representation [January = 0] to the human
              // representation [January = 1]

              String year_start = date_time_SOG_COG_start.substring(0, 4);
              String month_start = date_time_SOG_COG_start.substring(4, 6); // January = 01
              String day_start = date_time_SOG_COG_start.substring(6, 8);
              String hour_start = date_time_SOG_COG_start.substring(8, 10);

              sensor_data_file_naam_datum_tijd_deel_start =
                  year_start + month_start + day_start + hour_start; // eg 2017092505

              // below for testing
              // System.out.println("+++ sensor_data_file_naam_datum_tijd_deel_start: " +
              // sensor_data_file_naam_datum_tijd_deel_start);

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max ? minutes old
              {
                // cheksum check
                //
                // example Mintaka Star last_record: 1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211
                //
                String record_checksum =
                    laatste_record.substring(
                        laatste_record.length() - 14,
                        laatste_record.length()
                            - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211"
                String computed_checksum =
                    Mintaka_Star_Checksum(laatste_record); // used by the Star and StarX !

                ///// TEST ////
                // computed_checksum = record_checksum;
                ///// TEST ///

                if (computed_checksum.equals(record_checksum)) {
                  // System.out.println("checksum ok");

                  // retrieved (from file) record example Mintaka Star: 1029.97,1029.97,-0.90,7, 52
                  // 41.9535N,  6 14.1943E,0,0,19*1A201703152006
                  // pressure at sensor height
                  int pos1 =
                      laatste_record.indexOf(
                          ",", 0); // ML hereafter; position of the first "," in the last record
                  int pos2 =
                      laatste_record.indexOf(
                          ",",
                          pos1 + 1); // ppp hereafter; position of the second "," in the last record
                  int pos3 =
                      laatste_record.indexOf(
                          ",",
                          pos2 + 1); // a hereafter; position of the third "," in the last record
                  int pos4 =
                      laatste_record.indexOf(
                          ",",
                          pos3 + 1); // lat hereafter; position of the 4th "," in the last record
                  int pos5 =
                      laatste_record.indexOf(
                          ",",
                          pos4 + 1); // lon hereafter; position n of the 5th "," in the last record
                  int pos6 =
                      laatste_record.indexOf(
                          ",",
                          pos5 + 1); // course hereafter; position of the 6th "," in the last record
                  // NB before already checked number commas in record

                  // pressure (uncorrected at sensor height)
                  //
                  local_sensor_data_record_obs_pressure = laatste_record.substring(0, pos1);

                  if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println(
                        "--- sensor data record, raw uncorrected pressure: "
                            + local_sensor_data_record_obs_pressure);
                  }

                  // ppp (3hr pressure tendency)
                  //
                  local_sensor_data_record_obs_ppp = laatste_record.substring(pos2 + 1, pos3);
                  if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println(
                        "--- sensor data record, 3hr pressure tendency (ppp): "
                            + local_sensor_data_record_obs_ppp);
                  }

                  // a (3hr pressure tendency characteristic; WMO code)
                  //
                  local_sensor_data_record_obs_a = laatste_record.substring(pos3 + 1, pos4);
                  if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println(
                        "--- sensor data record, 3hr pressure characteristic (a): "
                            + local_sensor_data_record_obs_a);
                  }

                  // rounding eg: 998.19 -> 998.2
                  //        double digitale_sensor_waarde =
                  // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) +
                  // HOOGTE_CORRECTIE;
                  //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) /
                  // 10.0d;  // bv 998.19 -> 998.2

                  // GPS
                  //
                  GPS_latitude = laatste_record.substring(pos4 + 1, pos5);
                  GPS_longitude = laatste_record.substring(pos5 + 1, pos6);

                  if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println("--- sensor data record, GPS latitude: " + GPS_latitude);
                    System.out.println("--- sensor data record, GPS longitude: " + GPS_longitude);
                  }

                  if (StarX == true) {
                    // StarX example: // 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                    // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                    int pos7 = laatste_record.indexOf(",", pos6 + 1); // speed hereafter
                    int pos8 = laatste_record.indexOf(",", pos7 + 1); // elevation hereafter
                    int pos9 = laatste_record.indexOf(",", pos8 + 1); // air temp
                    int pos10 = laatste_record.indexOf(",", pos9 + 1); // RH
                    int pos11 = laatste_record.indexOf(",", pos10 + 1); // wet bulb
                    int pos12 = laatste_record.indexOf(",", pos11 + 1); // dew point
                    int pos13 = laatste_record.indexOf(",", pos12 + 1); // observation age
                    int pos14 = laatste_record.indexOf("*", pos13 + 1); // pos of the "*"
                    // NB before already checked number commas in record

                    // air temperature (only available in case of StarX)
                    //
                    local_sensor_data_record_obs_temp = laatste_record.substring(pos9 + 1, pos10);

                    if (destination.equals("MAIN_SCREEN") == false) {
                      System.out.println(
                          "--- sensor data record, air temperature: "
                              + local_sensor_data_record_obs_temp);
                    }

                    // RH (only available in case of StarX)
                    //
                    local_sensor_data_record_obs_RH = laatste_record.substring(pos10 + 1, pos11);

                    if (destination.equals("MAIN_SCREEN") == false) {
                      System.out.println(
                          "--- sensor data record, RH: " + local_sensor_data_record_obs_RH);
                    }

                    // wet bulb (only available in case of StarX)
                    //
                    local_sensor_data_record_obs_wet_bulb =
                        laatste_record.substring(pos11 + 1, pos12);

                    if (destination.equals("MAIN_SCREEN") == false) {
                      System.out.println(
                          "--- sensor data record, wet bulb: "
                              + local_sensor_data_record_obs_wet_bulb);
                    }

                    // dew point (only available in case of StarX)
                    //
                    local_sensor_data_record_obs_dew_point =
                        laatste_record.substring(pos12 + 1, pos13);

                    if (destination.equals("MAIN_SCREEN") == false) {
                      System.out.println(
                          "--- sensor data record, dew point: "
                              + local_sensor_data_record_obs_dew_point);
                    }

                    // observation age (only available in case of StarX)
                    //
                    local_obs_age = laatste_record.substring(pos13 + 1, pos14);

                    if (destination.equals("MAIN_SCREEN") == false) {
                      System.out.println("--- sensor data record, obs age: " + local_obs_age);
                    }

                    int int_local_obs_age = AGE_NOT_OK; // 999999 = random number but > 99.9
                    if ((local_obs_age.compareTo("") != 0)
                        && (local_obs_age != null)
                        && (local_obs_age.indexOf("*") == -1)) {
                      try {
                        int_local_obs_age = Integer.parseInt(local_obs_age.trim());
                      } catch (NumberFormatException e) {
                        int_local_obs_age = AGE_NOT_OK;
                        System.out.println(
                            "--- "
                                + "RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR() "
                                + e);
                      }
                    }

                    if ((int_local_obs_age < 0)
                        || (int_local_obs_age
                            > main_RS232_RS422
                                .MAX_AGE_STARX_OBS_DATA)) // NB 60 * 10 = 600 sec = 10 minutes
                    {
                      error_info = STRING_STARX_PART_OBSOLETE_ERROR;
                      laatste_record = null;
                    } // if ((hulp_return_obs_age >= 0) && (hulp_return_obs_age <=
                    // MAX_AGE_STARX_OBS_DATA))
                  } // if (StarX == true)
                } // if (computed_checksum.equals(record_checksum))
                else // checksum not ok
                {
                  System.out.println("--- checksum NOT ok " + "(" + laatste_record + ")");

                  error_info = STRING_PRESSURE_CHECKSUM_ERROR;
                  laatste_record = null;
                } // else
              } // if (timeDiff <= 10L)
              else // timeDiff not ok
              {
                error_info = STRING_PRESSURE_TIMEDIFF_ERROR;
                laatste_record = null;
              } // else
            } // if (laatste_record != null)
          } // try
          catch (IOException ex) {
            error_info = STRING_PRESSURE_IO_FILE_ERROR;
            laatste_record = null;
            System.out.println(
                "--- Function RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(): "
                    + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
        else // no file
        {
          error_info = STRING_PRESSURE_NO_FILE_ERROR;
          laatste_record = null;
        }

        // clear memory
        main.obs_file_datum_tijd = null;

        // initialisation
        GPS_latitude_earlier = "";
        GPS_longitude_earlier = "";
        GPS_latitude_earlier_wind = "";
        GPS_longitude_earlier_wind = "";

        if (laatste_record == null) {
          GPS_latitude = "";
          GPS_longitude = "";
          local_sensor_data_record_obs_pressure = "";
          local_sensor_data_record_obs_ppp = "";
          local_sensor_data_record_obs_a = "";
          local_sensor_data_record_obs_temp = "";
          local_sensor_data_record_obs_RH = "";
          local_sensor_data_record_obs_wet_bulb = "";
          local_sensor_data_record_obs_dew_point = "";
          // GPS_latitude_earlier  = "";
          // GPS_longitude_earlier = "";

          // write error info to log
          if (destination.equals("MAIN_SCREEN") == false) {
            RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
          }
        } // if (laatste_record == null)

        // retrieve GPS position 10 minutes (format 101) or 3 hours (FM13) earlier
        //
        // NB there is no use to continue if a recent position was not found
        if ((GPS_latitude.compareTo("") != 0)
            && (GPS_latitude != null)
            && (GPS_latitude.indexOf("*") == -1)) {
          record = null;

          // determine sensor data file name of the file with the GPS position of 10 minutes or 3
          // hours ago
          sensor_data_file_name =
              "sensor_data_" + sensor_data_file_naam_datum_tijd_deel_start + ".txt";

          // first check if there is a sensor data file present (and not empty)
          volledig_path_sensor_data =
              main.logs_dir + java.io.File.separator + sensor_data_file_name;

          // below for testing
          // System.out.println("+++ te openen file earlier voor obs SOG and COG: "+
          // volledig_path_sensor_data);

          sensor_data_file = new File(volledig_path_sensor_data);
          if (sensor_data_file.exists() && sensor_data_file.length() > 0) // length() in bytes
          {
            try (BufferedReader in =
                new BufferedReader(new FileReader(volledig_path_sensor_data))) {
              record = null;
              while ((record = in.readLine()) != null) {
                if (record.length()
                    > 15) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
                // leat 2 char pressure value= 15 chars)
                {
                  int pos = record.length() - 12; // pos is now start position of YYYYMMDDHHmm
                  String record_datum_tijd_minuten =
                      record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

                  if (record_datum_tijd_minuten.equals(
                      date_time_SOG_COG_start)) // found the corresponding record of 10 min or 3 hrs
                  // earlier
                  {
                    String record_checksum =
                        record.substring(
                            record.length() - 14,
                            record.length()
                                - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,
                    // 6 14.1848E,0,0,-1*24201703291211"
                    String computed_checksum = Mintaka_Star_Checksum(record);

                    // below only for testing (uncomment when testing)
                    // computed_checksum = record_checksum;

                    if (computed_checksum.equals(record_checksum)) {
                      // System.out.println("checksum ok");

                      // extra check on correct number of commas in the earlier (10 min or 3 hrs)
                      // record
                      //
                      int number_read_commas = 0;
                      int pos_comma = -1;

                      do {
                        pos_comma = record.indexOf(",", pos_comma + 1);
                        if (pos_comma != -1) // "," found
                        {
                          number_read_commas++;
                          // System.out.println("+++ number_read_commas = " + number_read_commas);
                        }
                      } while (pos_comma != -1);

                      boolean format_ok = true;
                      if (StarX == false) {
                        if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
                          format_ok = false;
                          System.out.println(
                              "--- Mintaka Star format (number commas) rertieved record earlier (for SOG and COG) NOT ok (file: "
                                  + volledig_path_sensor_data
                                  + "; record: "
                                  + record
                                  + ")");
                        }
                      } else if (StarX == true) {
                        if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                          format_ok = false;
                          System.out.println(
                              "--- Mintaka StarX format (number commas) rertieved record earlier (for SOG and COG) NOT ok (file: "
                                  + volledig_path_sensor_data
                                  + "; record: "
                                  + record
                                  + ")");
                        }
                      }

                      if (format_ok) {
                        // retrieved (from file) record example Mintaka Star:
                        // 1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006
                        int pos1 =
                            record.indexOf(",", 0); // position of the first "," in the last record
                        int pos2 =
                            record.indexOf(
                                ",", pos1 + 1); // position of the second "," in the last record
                        int pos3 =
                            record.indexOf(
                                ",", pos2 + 1); // position of the third "," in the last record
                        int pos4 =
                            record.indexOf(
                                ",", pos3 + 1); // position of the 4th "," in the last record
                        int pos5 =
                            record.indexOf(
                                ",", pos4 + 1); // position of the 5th "," in the last record
                        int pos6 =
                            record.indexOf(
                                ",", pos5 + 1); // position of the 6th "," in the last record
                        // int pos7 = record.indexOf(",", pos6 +1);
                        //       // position of the 7th "," in the last record

                        GPS_latitude_earlier = record.substring(pos4 + 1, pos5);
                        GPS_longitude_earlier = record.substring(pos5 + 1, pos6);

                        // below for testing
                        // System.out.println("+++ sensor data record, GPS latitude earlier: " +
                        // GPS_latitude_earlier + "(" + record_datum_tijd_minuten + ")");
                        // System.out.println("+++ sensor data record, GPS longitude earlier: " +
                        // GPS_longitude_earlier + "(" + record_datum_tijd_minuten + ")");

                        if (destination.equals("MAIN_SCREEN") == false) {
                          System.out.println(
                              "--- sensor data record, GPS latitude earlier: "
                                  + GPS_latitude_earlier
                                  + "("
                                  + record_datum_tijd_minuten
                                  + ")");
                          System.out.println(
                              "--- sensor data record, GPS longitude earlier: "
                                  + GPS_longitude_earlier
                                  + "("
                                  + record_datum_tijd_minuten
                                  + ")");
                        }
                      } // if (format_ok)
                      else {
                        GPS_latitude_earlier = "";
                        GPS_longitude_earlier = "";
                      }
                    } // if (computed_checksum.equals(record_checksum))
                    else {
                      // System.out.println("record checksum = " + record_checksum);
                      // System.out.println("computed checksum = " + computed_checksum);
                      System.out.println("--- checksum NOT ok " + "(" + record + ")");

                      GPS_latitude_earlier = "";
                      GPS_longitude_earlier = "";
                    } // else
                  } // if (record_datum_tijd_minuten.equals(date_time_SOG_COG_start))
                } // if (record.length() > 15)
                else {
                  GPS_latitude_earlier = "";
                  GPS_longitude_earlier = "";
                }
              } // while ((record = in.readLine()) != null)

            } // try
            catch (IOException ex) {
              System.out.println(
                  "--- Function RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(): "
                      + ex);
            }
          } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

          // below for testing
          // System.out.println("+++ sensor data record, GPS latitude earlier: " +
          // GPS_latitude_earlier);
          // System.out.println("+++ sensor data record, GPS longitude earlier: " +
          // GPS_longitude_earlier);

          if (main.obs_format.equals(main.FORMAT_FM13)) {
            // not necessary for format#101 because SOG and COG for #101 meteo report already over
            // 10 minutes period
            RS232_Mintaka_Star_And_StarX_Determine_Position_Earlier_For_Wind(
                date_time_SOG_COG_end, StarX);
          }
        } // if ( (GPS_latitude.compareTo("") != 0) etc.

        // e.g. 1018.12,-2.30,8,23.4,65,15.4,12.8   [StarX]
        //      1018.12,-2.30,8,,,,                 [Star]
        //

        return local_sensor_data_record_obs_pressure
            + ","
            + local_sensor_data_record_obs_ppp
            + ","
            + local_sensor_data_record_obs_a
            + ","
            + local_sensor_data_record_obs_temp
            + ","
            + local_sensor_data_record_obs_RH
            + ","
            + local_sensor_data_record_obs_wet_bulb
            + ","
            + local_sensor_data_record_obs_dew_point;
        // NB in case not StarX: local_sensor_data_record_obs_temp      = ""
        //                       local_sensor_data_record_obs_RH        = ""
        //                       local_sensor_data_record_obs_wet_bulb  = ""
        //                       local_sensor_data_record_obs_dew_point = ""

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        // intialisation (in this inner class)
        boolean fix_ok = true;
        boolean pressure_ok = false;
        boolean ppp_ok = false;
        boolean a_ok = false;
        boolean air_temp_ok = false;
        boolean wet_bulb_ok = false;
        boolean dew_point_ok = false;
        boolean RH_ok = false;
        boolean GPS_earlier_ok = false;
        boolean GPS_earlier_wind_ok = false;
        String message = "";
        String sensor_data_record_WOW_pressure_MSL_inhg = "";
        String sensor_data_record_WOW_pressure_hpa = "";
        String sensor_data_record_APR_pressure_MSL_hpa = "";
        String sensor_data_record_APR_pressure_hpa = "";
        String sensor_data_record_APR_pressure_ppp = "";
        String sensor_data_record_APR_pressure_a = "";

        String sensor_data_record_APR_air_temp = "";
        String sensor_data_record_APR_RH = "";
        String sensor_data_record_APR_wet_bulb = "";
        String sensor_data_record_APR_dew_point = "";

        String sensor_data_record_WOW_air_temp = "";
        double hulp_double_WOW_pressure_reading = 0.0;
        double hulp_double_WOW_air_temp = 999.9;
        double hulp_double_APR_pressure_reading = 0.0;
        double WOW_height_correction_pressure = Double.MAX_VALUE;
        double APR_height_correction_pressure = Double.MAX_VALUE;

        double hulp_double_APR_air_temp = 999.9;
        double hulp_double_APR_wet_bulb = 999.9;
        double hulp_double_APR_dew_point = 999.9;
        double hulp_double_APR_RH = 999.9;

        double hulp_double_APR_pressure_ppp = 999.9;
        int hulp_int_APR_pressure_a = 999;

        String error_info = "";

        //
        // pressure [Star] and air temperature [StarX] (with logging)
        //
        if (destination.equals("WOW")) {
          pressure_ok = false;

          try {
            String return_string =
                get(); // e.g. 1018.12,-2.30,8,23.4,65,15.4,12.8  [StarX]  or 1018.12,-2.30,8,,,,
            // [Star]
            int pos_comma = return_string.indexOf(",");
            if (pos_comma != -1) {
              sensor_data_record_WOW_pressure_hpa = return_string.substring(0, pos_comma);
            } else {
              sensor_data_record_WOW_pressure_hpa = "";
            }
            main.sensor_data_record_obs_pressure = sensor_data_record_WOW_pressure_hpa; // nodig?

            if ((return_string.length() > pos_comma + 1) && (pos_comma != -1)) {
              sensor_data_record_WOW_air_temp = return_string.substring(pos_comma + 1);
            }
          } catch (InterruptedException | ExecutionException ex) {
            sensor_data_record_WOW_pressure_hpa = "";
            sensor_data_record_WOW_air_temp = "";
          }

          // pressure [WOW]
          //
          if ((sensor_data_record_WOW_pressure_hpa.compareTo("") != 0)) {
            try {
              hulp_double_WOW_pressure_reading =
                  Double.parseDouble(sensor_data_record_WOW_pressure_hpa.trim());
            } catch (NumberFormatException e) {
              System.out.println(
                  "--- "
                      + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                      + e);
              hulp_double_WOW_pressure_reading = Double.MAX_VALUE;
            }

            if ((hulp_double_WOW_pressure_reading > 900.0)
                && (hulp_double_WOW_pressure_reading < 1100.0)) {
              // DecimalFormat df = new DecimalFormat("0.000");           // rounding only 3
              // decimals

              // COMPUTE HEIGHT CORRECTION AND CONVERT TO MSL (further below in this function)
              // BECAUSE PRESSURE AT STATION HEIGHT NOT VISIBLE IN WOW!!! (+ apply barometer
              // instrument correction)
              WOW_height_correction_pressure =
                  RS232_WOW_APR_compute_air_pressure_height_correction(
                      hulp_double_WOW_pressure_reading);

              String message_b =
                  "[WOW] pressure at sensor height = "
                      + sensor_data_record_WOW_pressure_hpa
                      + " hPa";
              main.log_turbowin_system_message(message_b);
              String message_hc =
                  "[WOW] air pressure height corection = "
                      + Double.toString(WOW_height_correction_pressure)
                      + " hPa";
              main.log_turbowin_system_message(message_hc);
              String message_ic =
                  "[WOW] air pressure instrument corection = "
                      + main.barometer_instrument_correction
                      + " hPa";
              main.log_turbowin_system_message(message_ic);

              if (WOW_height_correction_pressure > -50.0 && WOW_height_correction_pressure < 50.0) {
                pressure_ok = true;
              } else {
                String message_hce =
                    "[WOW] computed height correction pressure not ok ("
                        + WOW_height_correction_pressure
                        + ")";
                main.log_turbowin_system_message(message_hce);
                pressure_ok = false;
              }
            } // if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading <
            // 1100.0))
            // else if (hulp_double_WOW_pressure_reading == INT_PRESSURE_CHECKSUM_ERROR)
            else if (hulp_double_WOW_pressure_reading >= INT_PRESSURE_CHECKSUM_ERROR - 1
                && hulp_double_WOW_pressure_reading <= INT_PRESSURE_CHECKSUM_ERROR + 1) {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved barometer reading: cheksum error ");
              // sensor_data_record_WOW_pressure_MSL_inhg = "";
              pressure_ok = false;
            }
            // else if (hulp_double_WOW_pressure_reading == INT_PRESSURE_TIMEDIFF_ERROR)
            else if (hulp_double_WOW_pressure_reading >= INT_PRESSURE_TIMEDIFF_ERROR - 1
                && hulp_double_WOW_pressure_reading <= INT_PRESSURE_TIMEDIFF_ERROR + 1) {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved barometer reading: complete reading obsolete");
              pressure_ok = false;
            }
            // else if (hulp_double_WOW_pressure_reading == INT_PRESSURE_OBS_AGE_ERROR)
            else if (hulp_double_WOW_pressure_reading >= INT_PRESSURE_OBS_AGE_ERROR - 1
                && hulp_double_WOW_pressure_reading <= INT_PRESSURE_OBS_AGE_ERROR + 1) {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved barometer reading: StarX part obsolete");
              pressure_ok = false;
            }
            // else if (hulp_double_WOW_pressure_reading == INT_PRESSURE_RECORD_FORMAT_ERROR)
            else if (hulp_double_WOW_pressure_reading >= INT_PRESSURE_RECORD_FORMAT_ERROR - 1
                && hulp_double_WOW_pressure_reading <= INT_PRESSURE_RECORD_FORMAT_ERROR + 1) {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved barometer reading: error record format");
              pressure_ok = false;
            }
            // else if (hulp_double_WOW_pressure_reading == INT_PRESSURE_IO_FILE_ERROR)
            else if (hulp_double_WOW_pressure_reading >= INT_PRESSURE_IO_FILE_ERROR - 1
                && hulp_double_WOW_pressure_reading <= INT_PRESSURE_IO_FILE_ERROR + 1) {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved barometer reading: IO error opening sensor data file");
              pressure_ok = false;
            }
            // else if (hulp_double_WOW_pressure_reading == INT_PRESSURE_NO_FILE_ERROR)
            else if (hulp_double_WOW_pressure_reading >= INT_PRESSURE_NO_FILE_ERROR - 1
                && hulp_double_WOW_pressure_reading <= INT_PRESSURE_NO_FILE_ERROR + 1) {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved barometer reading: error, no sensor data file or file empty");
              pressure_ok = false;
            } else if (hulp_double_WOW_pressure_reading > Double.MAX_VALUE - 1) {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved barometer reading: error NumberFormatException");
              pressure_ok = false;
            } else {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved barometer reading: outside range");
              pressure_ok = false;
            }
          } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
          else // so (still) no sensor data available
          {
            main.log_turbowin_system_message(
                "[WOW] automatically retrieved barometer reading: error during retrieving data");
            // sensor_data_record_WOW_pressure_MSL_inhg = "";
            pressure_ok = false;
          }

          // air temp (only if StarX) [WOW]
          //
          if (StarX == true) {
            if ((sensor_data_record_WOW_air_temp.compareTo("") != 0)) {
              try {
                hulp_double_WOW_air_temp =
                    Double.parseDouble(sensor_data_record_WOW_air_temp.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                        + e);
                hulp_double_WOW_air_temp = Double.MAX_VALUE;
              }

              if ((hulp_double_WOW_air_temp > -70.0) && (hulp_double_WOW_air_temp < 70.0)) {
                // DecimalFormat df = new DecimalFormat("0.000");           // rounding only 3
                // decimals

                air_temp_ok = true;
              }
              // else if (hulp_double_WOW_air_temp == INT_AIR_TEMP_CHECKSUM_ERROR)
              else if (hulp_double_WOW_air_temp >= INT_AIR_TEMP_CHECKSUM_ERROR - 1
                  && hulp_double_WOW_air_temp <= INT_AIR_TEMP_CHECKSUM_ERROR + 1) {
                main.log_turbowin_system_message(
                    "[WOW] automatically retrieved air temp: cheksum error ");
                air_temp_ok = false;
              }
              // else if (hulp_double_WOW_air_temp == INT_AIR_TEMP_TIMEDIFF_ERROR)
              else if (hulp_double_WOW_air_temp >= INT_AIR_TEMP_TIMEDIFF_ERROR - 1
                  && hulp_double_WOW_air_temp <= INT_AIR_TEMP_TIMEDIFF_ERROR + 1) {
                main.log_turbowin_system_message(
                    "[WOW] automatically retrieved air temp: complete reading obsolete");
                air_temp_ok = false;
              }
              // else if (hulp_double_WOW_air_temp == INT_AIR_TEMP_OBS_AGE_ERROR)
              else if (hulp_double_WOW_air_temp >= INT_AIR_TEMP_OBS_AGE_ERROR - 1
                  && hulp_double_WOW_air_temp <= INT_AIR_TEMP_OBS_AGE_ERROR + 1) {
                main.log_turbowin_system_message(
                    "[WOW] automatically retrieved air temp: StarX part obsolete");
                air_temp_ok = false;
              }
              // else if (hulp_double_WOW_air_temp == INT_AIR_TEMP_RECORD_FORMAT_ERROR)
              else if (hulp_double_WOW_air_temp >= INT_AIR_TEMP_RECORD_FORMAT_ERROR - 1
                  && hulp_double_WOW_air_temp <= INT_AIR_TEMP_RECORD_FORMAT_ERROR + 1) {
                main.log_turbowin_system_message(
                    "[WOW] automatically retrieved air temp: error record format");
                air_temp_ok = false;
              }
              // else if (hulp_double_WOW_air_temp == INT_AIR_TEMP_IO_FILE_ERROR)
              else if (hulp_double_WOW_air_temp >= INT_AIR_TEMP_IO_FILE_ERROR - 1
                  && hulp_double_WOW_air_temp <= INT_AIR_TEMP_IO_FILE_ERROR + 1) {
                main.log_turbowin_system_message(
                    "[WOW] automatically retrieved air temp: IO error opening sensor data file");
                air_temp_ok = false;
              }
              // else if (hulp_double_WOW_air_temp == INT_AIR_TEMP_NO_FILE_ERROR)
              else if (hulp_double_WOW_air_temp >= INT_AIR_TEMP_NO_FILE_ERROR - 1
                  && hulp_double_WOW_air_temp <= INT_AIR_TEMP_NO_FILE_ERROR + 1) {
                main.log_turbowin_system_message(
                    "[WOW] automatically retrieved air temp: error, no sensor data file or file empty");
                air_temp_ok = false;
              } else if (hulp_double_WOW_air_temp > Double.MAX_VALUE - 1) {
                main.log_turbowin_system_message(
                    "[WOW] automatically retrieved air temp: error NumberFormatException");
                air_temp_ok = false;
              } else {
                main.log_turbowin_system_message(
                    "[WOW] automatically retrieved air temp: outside range");
                air_temp_ok = false;
              }
            } // if ((sensor_data_record_WOW_air_temp.compareTo("") != 0))
            else // so (still) no sensor data available
            {
              main.log_turbowin_system_message(
                  "[WOW] automatically retrieved air temp: error during retrieving data");
              air_temp_ok = false;
            }
          } // if (STarX == true)

        } // if (destination.equals("WOW"))
        else if ((destination.equals("APR"))
            || (destination.equals("MAIN_SCREEN"))) // APR = Automated Pressure Reporting
        {
          /////// air pressure / ppp / a [APR]
          //
          pressure_ok = false;

          try {
            String return_string =
                get(); // e.g. 1018.12,-2.30,8,23.4,65,15.4,12.8  [StarX]  or 1018.12,-2.30,8,,,,
            // [Star]

            // e.g. return_string: 1018.12,-2.30,8,23.4,65,15.4,12.8  [StarX]
            //      return_string: 1018.12,-2.30,8,,,,                [Star]
            int pos1 = return_string.indexOf(",", 0);
            int pos2 = return_string.indexOf(",", pos1 + 1);
            int pos3 = return_string.indexOf(",", pos2 + 1);
            int pos4 = return_string.indexOf(",", pos3 + 1);
            int pos5 = return_string.indexOf(",", pos4 + 1);
            int pos6 = return_string.indexOf(",", pos5 + 1);

            sensor_data_record_APR_pressure_hpa = return_string.substring(0, pos1);
            sensor_data_record_APR_pressure_ppp = return_string.substring(pos1 + 1, pos2);
            sensor_data_record_APR_pressure_a = return_string.substring(pos2 + 1, pos3);

            sensor_data_record_APR_air_temp = return_string.substring(pos3 + 1, pos4);
            sensor_data_record_APR_RH = return_string.substring(pos4 + 1, pos5);
            sensor_data_record_APR_wet_bulb = return_string.substring(pos5 + 1, pos6);
            sensor_data_record_APR_dew_point = return_string.substring(pos6 + 1);

            main.sensor_data_record_obs_pressure = sensor_data_record_APR_pressure_hpa; // nodig?
            main.sensor_data_record_obs_ppp = sensor_data_record_APR_pressure_ppp; // nodig?
            main.sensor_data_record_obs_a = sensor_data_record_APR_pressure_a; // nodig?

          } // try
          catch (InterruptedException | ExecutionException ex) {
            sensor_data_record_APR_pressure_hpa = "";
            sensor_data_record_APR_pressure_ppp = "";
            sensor_data_record_APR_pressure_a = "";
            sensor_data_record_APR_air_temp = "";
            sensor_data_record_APR_RH = "";
            sensor_data_record_APR_wet_bulb = "";
            sensor_data_record_APR_dew_point = "";

            if (destination.equals("MAIN_SCREEN") == false) {
              error_info = STRING_PRESSURE_INTERRUPTION_EXECUTIONEXEPTION_ERROR;
              RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
            }
          }

          ////// air pressure //////
          //
          if ((sensor_data_record_APR_pressure_hpa.compareTo("") != 0)) {
            try {
              hulp_double_APR_pressure_reading =
                  Double.parseDouble(sensor_data_record_APR_pressure_hpa.trim());
              // hulp_double_pressure_reading = Math.round(hulp_double_pressure_reading * 10) /
              // 10.0d;  // bv 998.19 -> 998.2
            } catch (NumberFormatException e) {
              System.out.println(
                  "--- "
                      + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                      + e);
              hulp_double_APR_pressure_reading = Double.MAX_VALUE;
            }

            if ((hulp_double_APR_pressure_reading > 900.0)
                && (hulp_double_APR_pressure_reading < 1100.0)) {
              // DecimalFormat df = new DecimalFormat("0.0");           // rounding only 1 decimal

              // CONVERT TO MSL (+ apply barometer instrument correction)
              APR_height_correction_pressure =
                  RS232_WOW_APR_compute_air_pressure_height_correction(
                      hulp_double_APR_pressure_reading);

              if (destination.equals("MAIN_SCREEN") == false) {
                String message_b =
                    "[APR] air pressure at sensor height = "
                        + sensor_data_record_APR_pressure_hpa
                        + " hPa";
                main.log_turbowin_system_message(message_b);
                String message_hc =
                    "[APR] air pressure height correction = "
                        + Double.toString(APR_height_correction_pressure)
                        + " hPa";
                main.log_turbowin_system_message(message_hc);
                String message_ic =
                    "[APR] air pressure instrument correction = "
                        + main.barometer_instrument_correction
                        + " hPa";
                main.log_turbowin_system_message(message_ic);
              }

              if (APR_height_correction_pressure > -50.0 && APR_height_correction_pressure < 50.0) {
                // ic correction (make it 0.0 if outside the range)
                double double_barometer_instrument_correction = 0.0;

                if (main.barometer_instrument_correction.equals("") == false) {
                  try {
                    double_barometer_instrument_correction =
                        Double.parseDouble(main.barometer_instrument_correction);

                    if (!(double_barometer_instrument_correction >= -4.0
                        && double_barometer_instrument_correction <= 4.0)) {
                      double_barometer_instrument_correction = 0.0;
                    }
                  } catch (NumberFormatException e) {
                    double_barometer_instrument_correction = 0.0;
                  }
                }

                DecimalFormat df = new DecimalFormat("0.0"); // rounding only 1 decimal

                ////////////////// barometer reading (pressure at sensor height + ic)
                // ////////////////
                //
                String sensor_data_record_APR_pressure_reading_hpa_corrected =
                    df.format(
                        hulp_double_APR_pressure_reading + double_barometer_instrument_correction);
                mybarometer.pressure_reading_corrected =
                    sensor_data_record_APR_pressure_reading_hpa_corrected; // now pressure at sensor
                // height corrected for
                // ic

                ///////////////// pressure at MSL (+ ic) ///////////
                //
                // sensor_data_record_APR_pressure_MSL_hpa =
                // df.format(hulp_double_APR_pressure_reading + APR_height_correction_pressure +
                // double_barometer_instrument_correction);
                double hulp_double_APR_pressure_MSL_not_rounded =
                    hulp_double_APR_pressure_reading
                        + APR_height_correction_pressure
                        + double_barometer_instrument_correction;
                BigDecimal bd =
                    new BigDecimal(hulp_double_APR_pressure_MSL_not_rounded)
                        .setScale(2, RoundingMode.HALF_UP);
                double hulp_double_APR_pressure_MSL_rounded = bd.doubleValue();
                sensor_data_record_APR_pressure_MSL_hpa =
                    Double.toString(hulp_double_APR_pressure_MSL_rounded);
                mybarometer.pressure_msl_corrected =
                    sensor_data_record_APR_pressure_MSL_hpa; // sensor_data_record_APR_pressure_MSL_hpa the baromter instrument correction is included

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_msl =
                      "[APR] air pressure MSL = " + mybarometer.pressure_msl_corrected + " hPa";
                  main.log_turbowin_system_message(message_msl);
                }

                // make IMMT ready
                if (destination.equals("MAIN_SCREEN") == false) {
                  RS232_make_pressure_APR_FM13_IMMT_ready();
                }

                pressure_ok = true;
              } // if (APR_height_correction_pressure > -50.0 && APR_height_correction_pressure <
              // 50.0)
              else {
                pressure_ok = false;

                if (destination.equals("MAIN_SCREEN") == false) {
                  error_info = STRING_PRESSURE_HEIGHT_CORRECTION_ERROR;
                  RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
                }
              } // else
            } // if ((hulp_double_APR_pressure_reading > 900.0) && (hulp_double_APR_pressure_reading
            // < 1100.0))
            else if (hulp_double_APR_pressure_reading > Double.MAX_VALUE - 1) {
              pressure_ok = false;

              if (destination.equals("MAIN_SCREEN") == false) {
                error_info = STRING_PRESSURE_NUMBERFORMATECEPTION_ERROR;
                RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
              }
            } else {
              pressure_ok = false;

              if (destination.equals("MAIN_SCREEN") == false) {
                error_info = STRING_PRESSURE_OUTSIDE_RANGE_ERROR;
                RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
              }
            } // else
          } //  if ((sensor_data_record_APR_pressure_hpa.compareTo("") != 0))

          /////// ppp ///////
          //
          ppp_ok = false;
          mybarograph.pressure_amount_tendency = "";

          if ((sensor_data_record_APR_pressure_ppp.compareTo("") != 0)) {
            try {
              hulp_double_APR_pressure_ppp =
                  Double.parseDouble(sensor_data_record_APR_pressure_ppp.trim());
            } catch (NumberFormatException e) {
              System.out.println(
                  "--- "
                      + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                      + e);
              hulp_double_APR_pressure_ppp = Double.MAX_VALUE;
            }

            if ((hulp_double_APR_pressure_ppp > -99.9) && (hulp_double_APR_pressure_ppp < 99.9)) {
              DecimalFormat df = new DecimalFormat("0.0"); // rounding 1 decimal
              mybarograph.pressure_amount_tendency =
                  df.format(Math.abs(hulp_double_APR_pressure_ppp)); // only positve values !!
              ppp_ok = true;

              if (destination.equals("MAIN_SCREEN") == false) {
                String message_pressure_ppp =
                    "[APR] pressure tendency at sensor height = "
                        + mybarograph.pressure_amount_tendency
                        + " hPa";
                main.log_turbowin_system_message(message_pressure_ppp);
              }

              // make IMMT and FM13 ready [APR]
              RS232_make_pressure_ppp_APR_FM13_IMMT_ready();
            } else {
              if (destination.equals("MAIN_SCREEN") == false) {
                error_info = STRING_PRESSURE_AMOUNT_TENDENCY_OUTSIDE_RANGE_ERROR;
                RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
              }
              ppp_ok = false;
            }
          } // if ((sensor_data_record_APR_wet_bulb.compareTo("") != 0))

          ////// a /////////
          //
          a_ok = false;
          mybarograph.a_code = "";

          if ((sensor_data_record_APR_pressure_a.compareTo("") != 0)) {
            try {
              hulp_int_APR_pressure_a = Integer.parseInt(sensor_data_record_APR_pressure_a.trim());
            } catch (NumberFormatException e) {
              System.out.println(
                  "--- "
                      + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                      + e);
              hulp_int_APR_pressure_a = Integer.MAX_VALUE;
            }

            if ((hulp_int_APR_pressure_a >= 0) && (hulp_int_APR_pressure_a <= 9)) {
              mybarograph.a_code = sensor_data_record_APR_pressure_a.trim();
              a_ok = true;

              if (destination.equals("MAIN_SCREEN") == false) {
                String message_pressure_a =
                    "[APR] pressure characteristic at sensor height = "
                        + mybarograph.a_code
                        + " (code)";
                main.log_turbowin_system_message(message_pressure_a);
              }

              // make IMMT and FM13 ready [APR]
              RS232_make_pressure_a_APR_FM13_IMMT_ready();
            } else {
              if (destination.equals("MAIN_SCREEN") == false) {
                error_info = STRING_PRESSURE_CHARACTERISTIC_OUTSIDE_RANGE_ERROR;
                RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
              }
              a_ok = false;
            }
          } // if ((sensor_data_record_APR_wet_bulb.compareTo("") != 0))

          if (StarX == true) // air temp / RH / wet bulb / dew point (only if StarX) [APR]
          {
            // air temp (only if StarX) [APR]
            //
            air_temp_ok = false;

            if ((sensor_data_record_APR_air_temp.compareTo("") != 0)) {
              try {
                hulp_double_APR_air_temp =
                    Double.parseDouble(sensor_data_record_APR_air_temp.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                        + e);
                hulp_double_APR_air_temp = Double.MAX_VALUE;
              }

              if ((hulp_double_APR_air_temp > -70.0) && (hulp_double_APR_air_temp < 70.0)) {
                DecimalFormat df = new DecimalFormat("0.0"); // rounding 1 decimal
                mytemp.air_temp = df.format(hulp_double_APR_air_temp);
                air_temp_ok = true;

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_air_temp =
                      "[APR] air temp at sensor height = " + mytemp.air_temp + " \u00B0C";
                  main.log_turbowin_system_message(message_air_temp);
                }

                // make IMMT and FM13 ready [APR]
                RS232_make_air_temp_APR_FM13_IMMT_ready();
              } else {
                if (destination.equals("MAIN_SCREEN") == false) {
                  error_info = STRING_AIR_TEMP_OUTSIDE_RANGE_ERROR;
                  RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
                }
                air_temp_ok = false;
              }
            } // if ((sensor_data_record_APR_air_temp.compareTo("") != 0))

            // wet bulb (only if StarX) [APR]
            //
            wet_bulb_ok = false;

            if ((sensor_data_record_APR_wet_bulb.compareTo("") != 0)) {
              try {
                hulp_double_APR_wet_bulb =
                    Double.parseDouble(sensor_data_record_APR_wet_bulb.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                        + e);
                hulp_double_APR_wet_bulb = Double.MAX_VALUE;
              }

              if ((hulp_double_APR_wet_bulb > -70.0) && (hulp_double_APR_wet_bulb < 70.0)) {
                DecimalFormat df = new DecimalFormat("0.0"); // rounding 1 decimal
                mytemp.wet_bulb_temp = df.format(hulp_double_APR_wet_bulb);
                wet_bulb_ok = true;

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_wet_bulb_temp =
                      "[APR] wet-bulb temp at sensor height = " + mytemp.wet_bulb_temp + " \u00B0C";
                  main.log_turbowin_system_message(message_wet_bulb_temp);
                }

                // make IMMT ready [APR]
                RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready();
              } else {
                if (destination.equals("MAIN_SCREEN") == false) {
                  error_info = STRING_WET_BULB_OUTSIDE_RANGE_ERROR;
                  RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
                }
                wet_bulb_ok = false;
              }
            } // if ((sensor_data_record_APR_wet_bulb.compareTo("") != 0))

            // dew point (only if StarX) [APR]
            //
            dew_point_ok = false;

            if ((sensor_data_record_APR_dew_point.compareTo("") != 0)) {
              try {
                hulp_double_APR_dew_point =
                    Double.parseDouble(sensor_data_record_APR_dew_point.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                        + e);
                hulp_double_APR_dew_point = Double.MAX_VALUE;
              }

              if ((hulp_double_APR_dew_point > -70.0) && (hulp_double_APR_dew_point < 70.0)) {
                mytemp.double_dew_point = hulp_double_APR_dew_point;
                dew_point_ok = true;

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_dew_point =
                      "[APR] dew-point at sensor height = "
                          + Double.toString(mytemp.double_dew_point)
                          + " \u00B0C";
                  main.log_turbowin_system_message(message_dew_point);
                }

                // make IMMT ready [APR]
                RS232_make_dew_point_APR_FM13_IMMT_ready();
              } else {
                if (destination.equals("MAIN_SCREEN") == false) {
                  error_info = STRING_DEWPOINT_OUTSIDE_RANGE_ERROR;
                  RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
                }
                dew_point_ok = false;
              }
            } // if ((sensor_data_record_APR_dew_point.compareTo("") != 0))

            // RH (only if StarX) [APR]
            //
            RH_ok = false;

            if ((sensor_data_record_APR_RH.compareTo("") != 0)) {
              try {
                hulp_double_APR_RH = Double.parseDouble(sensor_data_record_APR_RH.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_For_WOW_APR() "
                        + e);
                hulp_double_APR_RH = Double.MAX_VALUE;
              }

              if ((hulp_double_APR_RH >= 0.0) && (hulp_double_APR_RH <= 100.0)) {
                mytemp.RH = sensor_data_record_APR_RH.trim(); // e.g. 60
                mytemp.double_rv =
                    hulp_double_APR_RH
                        / 100; // NB mytemp.double_rv in range 0.0 - 1.0 (or main.INVALID) !! [see
                // also mytemp.java]
                RH_ok = true;

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_dew_point = "[APR] RH at sensor height = " + mytemp.RH + " %";
                  main.log_turbowin_system_message(message_dew_point);
                }

                // make IMMT ready
                RS232_make_RH_APR_FM13_IMMT_ready();
              } else {
                if (destination.equals("MAIN_SCREEN") == false) {
                  error_info = STRING_RH_OUTSIDE_RANGE_ERROR;
                  RS232_Mintaka_Star_And_StarX_Write_Error_Info(destination, error_info);
                }
                RH_ok = false;
              }
            } // if ((sensor_data_record_APR_RH.compareTo("") != 0))
          } // if (StarX == true)
        } // else if ((destination.equals("APR")) || (destination.equals("MAIN_SCREEN")))

        //
        // GPS
        //

        //////// latitude (eg " 52 04.7041N") /////////
        //
        if ((GPS_latitude.compareTo("") != 0)
            && (GPS_latitude != null)
            && (GPS_latitude.indexOf("*") == -1)) {
          // fill the public string latitude values
          //
          // NB String hulp_GPS_latitude = GPS_latitude.replaceAll("\\s","");        // remove all
          // the whitespaces in the string

          String fix_latitude =
              GPS_latitude
                  .trim(); // to remove leading and trailing whitespace in the string eg " 52
          // 04.7041N" -> "52 04.7041N"
          int pos_space = fix_latitude.indexOf(" ", 0); // find first space (" ") in the string
          myposition.latitude_degrees =
              fix_latitude.substring(
                  0, pos_space); // eg "52 04.7041N" -> "52" and "2 04.7041N" -> "2"

          String fix_latitude_minutes =
              fix_latitude.substring(
                  pos_space + 1,
                  fix_latitude.length()
                      - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"
          BigDecimal bd_lat_min =
              new BigDecimal(fix_latitude_minutes)
                  .setScale(0, RoundingMode.HALF_UP); // 0 decimals rounding

          ///////////////////
          int hulp_bd_lat_min = bd_lat_min.intValue(); // BigDecimal to int
          if (hulp_bd_lat_min == 60) {
            // set latitude minutes to 0
            hulp_bd_lat_min = 0;
            bd_lat_min = BigDecimal.valueOf(hulp_bd_lat_min); // int to BigDecimal

            // increment latitude degrees
            int hulp_latitude_degrees =
                Integer.parseInt(myposition.latitude_degrees); // String to int
            hulp_latitude_degrees++;
            myposition.latitude_degrees =
                Integer.toString(hulp_latitude_degrees); // int to String (via Integer)
          }
          ///////////////////

          myposition.latitude_minutes =
              bd_lat_min.toString(); // rounded minutes value  e.g. "04.7041N" -> "4" minutes
          if (myposition.latitude_minutes.length() == 1) {
            myposition.latitude_minutes = "0" + myposition.latitude_minutes; // "4" -> "04"
          }

          GPS_latitude_hemisphere =
              fix_latitude.substring(fix_latitude.length() - 1); // eg "52 04.7041N" -> "N"
          if (GPS_latitude_hemisphere.toUpperCase().equals("N")) {
            myposition.latitude_hemisphere = myposition.HEMISPHERE_NORTH;
          } else if (GPS_latitude_hemisphere.toUpperCase().equals("S")) {
            myposition.latitude_hemisphere = myposition.HEMISPHERE_SOUTH;
          } else {
            myposition.latitude_hemisphere = "";
            fix_ok = false;
          }

          // lat int values
          //
          if (fix_ok == true) {
            try {
              myposition.int_latitude_degrees = Integer.parseInt(myposition.latitude_degrees);
            } catch (NumberFormatException e) {
              fix_ok = false;
            }

            try {
              myposition.int_latitude_minutes = Integer.parseInt(myposition.latitude_minutes);
            } catch (NumberFormatException e) {
              fix_ok = false;
            }
          } // if (fix_ok == true)

          // for latitude (LaLaLa) for IMMT log
          //
          if (fix_ok == true) {
            int int_latitude_minutes_6 =
                bd_lat_min.intValue()
                    / 6; // devide the minutes by six and disregard the remainder, now tenths of
            // degrees [IMMT rounding!!!}
            String latitude_minutes_6 =
                Integer.toString(int_latitude_minutes_6); // convert to String
            // myposition.lalala_code = myposition.latitude_degrees.trim().replaceFirst("^0+(?!$)",
            // "") + latitude_minutes_6;
            myposition.lalala_code = myposition.latitude_degrees + latitude_minutes_6;

            int len = 3; // always 3 chars
            if (myposition.lalala_code.length() < len) {
              // pad on left with zeros
              myposition.lalala_code =
                  "0000000000".substring(0, len - myposition.lalala_code.length())
                      + myposition.lalala_code;
            } else if (myposition.lalala_code.length() > len) {
              fix_ok = false;
              // System.out.println("+++++++++++ lalala_code error: " + myposition.lalala_code);
            }
          } // if (fix_ok == true)

        } // if ( (GPS_latitude.compareTo("") != 0) && (GPS_latitude != null) &&
        // (GPS_latitude.indexOf("*") == -1) )
        else {
          fix_ok = false;
        }

        //////// longitude (eg "  4 14.7041W") /////////
        //
        if ((fix_ok == true)
            && (GPS_longitude.compareTo("") != 0)
            && (GPS_longitude != null)
            && (GPS_longitude.indexOf("*") == -1)) {
          // fill the public string longitude values
          //
          String fix_longitude =
              GPS_longitude
                  .trim(); // to remove leading and trailing whitespace in the string eg "  6
          // 14.7041N" -> "6 14.7041N"
          int pos_space = fix_longitude.indexOf(" ", 0); // find first space (" ") in the string
          myposition.longitude_degrees =
              fix_longitude.substring(
                  0, pos_space); // eg "6 14.7041W" -> "6" and "116 14.7041W" -> "116"

          String fix_longitude_minutes =
              fix_longitude.substring(
                  pos_space + 1,
                  fix_longitude.length()
                      - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"
          BigDecimal bd_lon_min =
              new BigDecimal(fix_longitude_minutes)
                  .setScale(0, RoundingMode.HALF_UP); // 0 decimals rounding

          ///////////////////
          int hulp_bd_lon_min = bd_lon_min.intValue(); // BigDecimal to int
          if (hulp_bd_lon_min == 60) {
            // set longitude minutes to 0
            hulp_bd_lon_min = 0;
            bd_lon_min = BigDecimal.valueOf(hulp_bd_lon_min); // int to BigDecimal

            // increment longitude degrees
            int hulp_longitude_degrees =
                Integer.parseInt(myposition.longitude_degrees); // String to int
            hulp_longitude_degrees++;
            myposition.longitude_degrees =
                Integer.toString(hulp_longitude_degrees); // int to String (via Integer)
          }
          ///////////////////

          myposition.longitude_minutes =
              bd_lon_min
                  .toString(); // rounded minutes value  e.g. "04.7041N" -> "5" minutes; "23.7041N"
          // -> "24" minutes
          if (myposition.longitude_minutes.length() == 1) {
            myposition.longitude_minutes = "0" + myposition.longitude_minutes; // "5" -> "05"
          }

          GPS_longitude_hemisphere =
              fix_longitude.substring(fix_longitude.length() - 1); // eg "4 14.7041W" -> "W"
          if (GPS_longitude_hemisphere.toUpperCase().equals("E")) {
            myposition.longitude_hemisphere = myposition.HEMISPHERE_EAST;
          } else if (GPS_longitude_hemisphere.toUpperCase().equals("W")) {
            myposition.longitude_hemisphere = myposition.HEMISPHERE_WEST;
          } else {
            myposition.longitude_hemisphere = "";
            fix_ok = false;
          }

          // fill the public int longitude values
          //
          if (fix_ok == true) {
            try {
              myposition.int_longitude_degrees = Integer.parseInt(myposition.longitude_degrees);
            } catch (NumberFormatException e) {
              fix_ok = false;
            }

            try {
              myposition.int_longitude_minutes = Integer.parseInt(myposition.longitude_minutes);
            } catch (NumberFormatException e) {
              fix_ok = false;
            }
          } // if (fix_ok == true)

          // for longitude (LoLoLoLo) for IMMT log (NB see also myposition.java) In fact this IMMT
          // preparation only necessary in APR mode
          //
          if (fix_ok == true) {
            int int_longitude_minutes_6 =
                bd_lon_min.intValue()
                    / 6; // devide the minutes by six and disregard the remainder, now tenths of
            // degrees [IMMT rounding!!!}
            String longitude_minutes_6 =
                Integer.toString(int_longitude_minutes_6); // convert to String
            myposition.lolololo_code = myposition.longitude_degrees + longitude_minutes_6;

            int len2 = 4; // always 4 chars
            if (myposition.lolololo_code.length() < len2) {
              // pad on left with zeros
              myposition.lolololo_code =
                  "0000000000".substring(0, len2 - myposition.lolololo_code.length())
                      + myposition.lolololo_code;
            } else if (myposition.lolololo_code.length() > len2) {
              fix_ok = false;
              // System.out.println("+++++++++++ lolololo_code error: " + myposition.lolololo_code);
            }
          } // if (fix_ok == true)

        } //  if ( (GPS_longitude.compareTo("") != 0) && (GPS_longitude != null) &&
        // (GPS_longitude.indexOf("*") == -1) )
        else {
          fix_ok = false;
        }

        // quadrant of the globe
        //
        if (fix_ok == true) {
          // quadrant of the globe for IMMT
          //
          if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true)
              && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true)) {
            myposition.Qc_code = "1";
          } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true)
              && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true)) {
            myposition.Qc_code = "3";
          } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true)
              && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true)) {
            myposition.Qc_code = "5";
          } else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true)
              && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true)) {
            myposition.Qc_code = "7";
          } else {
            fix_ok = false;
            // System.out.println("+++++++++++ Qc_code error");
          }
        } // if (fix_ok == true)

        // initialisation (from version 4.1)
        GPS_earlier_ok = false;
        myposition.SOG_APR = Double.MAX_VALUE;
        myposition.COG_APR = Double.MAX_VALUE;
        myposition.course = "";
        myposition.speed = "";
        myposition.Ds_code = "";
        myposition.vs_code = "";
        GPS_earlier_wind_ok = false;
        myposition.SOG_APR_wind = Double.MAX_VALUE; // SOG wind if FM13 format
        myposition.COG_APR_wind = Double.MAX_VALUE; // COG wind if FM13 format

        if (fix_ok == true) {
          // computing COG and SOG (position interval 10 minutes or 3 hours)
          //
          //
          if ((GPS_latitude_earlier.compareTo("") == 0)
              || (GPS_longitude_earlier.compareTo("") == 0)
              || (GPS_latitude_earlier == null)
              || (GPS_longitude_earlier == null)
              || (GPS_latitude_earlier.indexOf("*") != -1)
              || (GPS_longitude_earlier.indexOf("*") != -1)) {
            GPS_earlier_ok = false;

            if (destination.equals("MAIN_SCREEN") == false) {
              if (main.obs_format.equals(main.FORMAT_101)) {
                // 10 minutes based (format101)
                message =
                    "[GPS] error; no 10 minutes earlier position data available for computing COG and SOG";
              } else if (main.obs_format.equals(main.FORMAT_FM13)) {
                // 180 minutes (3 hours, between positions to compute SOG and COG) based  (FM13)
                message =
                    "[GPS] error; no 3 hours earlier position data available for computing COG and SOG";
              }

              main.log_turbowin_system_message(message);
            } //  if (destination.equals("MAIN_SCREEN") == false)
          } else {
            GPS_earlier_ok = true;
          }
        } // if (fix_ok == true)

        if (fix_ok && GPS_earlier_ok) {
          // http://www.movable-type.co.uk/scripts/latlong.html

          // MINTAKA STAR has an integrated GPS
          // GPS data is part of the saved pressure string eg:
          // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
          // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
          // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
          // <course> is True, <speed> in knots, <elevation> in meters
          //
          // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
          // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
          //
          // STARX (NB first part of STARX is the same as the STAR)
          // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
          // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>,
          // <temperature>,<relativeHumidity>,<wetBulbTemperature>,<dewPoint>,
          // <humiditySensor pressure in mb>*<checksum>
          //
          // 1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,1017.89*18

          double double_lat_start = Double.MAX_VALUE;
          double double_lon_start = Double.MAX_VALUE;
          double double_lat_end = Double.MAX_VALUE;
          double double_lon_end = Double.MAX_VALUE;

          // latitude START position (latitude 10 min or 3 hrs earlier)
          //
          String fix_latitude_start =
              GPS_latitude_earlier
                  .trim(); // to remove leading and trailing whitespace in the string eg " 52
          // 04.7041N" -> "52 04.7041N"
          int pos_space =
              fix_latitude_start.indexOf(" ", 0); // find first space (" ") in the string
          String fix_latitude_degrees_start =
              fix_latitude_start.substring(
                  0, pos_space); // eg "52 04.7041N" -> "52" and "2 04.7041N" -> "2"
          String fix_latitude_minutes_start =
              fix_latitude_start.substring(
                  pos_space + 1,
                  fix_latitude_start.length()
                      - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"

          double double_latitude_degrees_start = Double.parseDouble(fix_latitude_degrees_start);
          double double_latitude_minutes_start = Double.parseDouble(fix_latitude_minutes_start);

          double_lat_start = double_latitude_degrees_start + double_latitude_minutes_start / 60.0;

          String fix_latitude_hemisphere_start =
              fix_latitude_start.substring(
                  fix_latitude_start.length() - 1); // eg "52 04.7041N" -> "N"
          if (fix_latitude_hemisphere_start.toUpperCase().equals("N")) {
            // North is positive value
            double_lat_start *= 1;
          } else if (fix_latitude_hemisphere_start.toUpperCase().equals("S")) {
            // South = negative value
            double_lat_start *= -1;
          } else {
            double_lat_start = Double.MAX_VALUE;
          }

          // longitude START position (latitude 10 min or 3 hrs earlier)
          //
          String fix_longitude_start =
              GPS_longitude_earlier
                  .trim(); // to remove leading and trailing whitespace in the string eg "152
          // 04.7041W" -> "152 04.7041W"
          pos_space = fix_longitude_start.indexOf(" ", 0); // find first space (" ") in the string
          String fix_longitude_degrees_start =
              fix_longitude_start.substring(
                  0, pos_space); // eg "152 04.7041W" -> "152" and "2 04.7041W" -> "2"
          String fix_longitude_minutes_start =
              fix_longitude_start.substring(
                  pos_space + 1,
                  fix_longitude_start.length()
                      - 1); // only the minutes of the latitude eg "152 04.7041W" -> "04.7041"

          double double_longitude_degrees_start = Double.parseDouble(fix_longitude_degrees_start);
          double double_longitude_minutes_start = Double.parseDouble(fix_longitude_minutes_start);

          double_lon_start = double_longitude_degrees_start + double_longitude_minutes_start / 60.0;

          String fix_longitude_hemisphere_start =
              fix_longitude_start.substring(
                  fix_longitude_start.length() - 1); // eg "152 04.7041W" -> "W"
          if (fix_longitude_hemisphere_start.toUpperCase().equals("E")) {
            // East is positive value
            double_lon_start *= 1;
          } else if (fix_longitude_hemisphere_start.toUpperCase().equals("W")) {
            // West = negative value
            double_lon_start *= -1;
          } else {
            double_lon_start = Double.MAX_VALUE;
          }

          // latitude END position (present pos)
          //
          String fix_latitude_end =
              GPS_latitude
                  .trim(); // to remove leading and trailing whitespace in the string eg " 52
          // 04.7041N" -> "52 04.7041N"
          pos_space = fix_latitude_end.indexOf(" ", 0); // find first space (" ") in the string
          String fix_latitude_degrees_end =
              fix_latitude_end.substring(
                  0, pos_space); // eg "52 04.7041N" -> "52" and "2 04.7041N" -> "2"
          String fix_latitude_minutes_end =
              fix_latitude_end.substring(
                  pos_space + 1,
                  fix_latitude_end.length()
                      - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"

          double double_latitude_degrees_end = Double.parseDouble(fix_latitude_degrees_end);
          double double_latitude_minutes_end = Double.parseDouble(fix_latitude_minutes_end);

          double_lat_end = double_latitude_degrees_end + double_latitude_minutes_end / 60.0;

          String fix_latitude_hemisphere_end =
              fix_latitude_end.substring(fix_latitude_end.length() - 1); // eg "52 04.7041N" -> "N"
          if (fix_latitude_hemisphere_end.toUpperCase().equals("N")) {
            // North is positive value
            double_lat_end *= 1;
          } else if (fix_latitude_hemisphere_end.toUpperCase().equals("S")) {
            // South = negative value
            double_lat_end *= -1;
          } else {
            // System.out.println("+++ double_lat_end = " + double_lat_end);
            double_lat_end = Double.MAX_VALUE;
          }

          // longitude END position (present pos)
          //
          String fix_longitude_end =
              GPS_longitude
                  .trim(); // to remove leading and trailing whitespace in the string eg "152
          // 04.7041W" -> "152 04.7041W"
          pos_space = fix_longitude_end.indexOf(" ", 0); // find first space (" ") in the string
          String fix_longitude_degrees_end =
              fix_longitude_end.substring(
                  0, pos_space); // eg "152 04.7041W" -> "152" and "2 04.7041W" -> "2"
          String fix_longitude_minutes_end =
              fix_longitude_end.substring(
                  pos_space + 1,
                  fix_longitude_end.length()
                      - 1); // only the minutes of the latitude eg "152 04.7041W" -> "04.7041"

          double double_longitude_degrees_end = Double.parseDouble(fix_longitude_degrees_end);
          double double_longitude_minutes_end = Double.parseDouble(fix_longitude_minutes_end);

          double_lon_end = double_longitude_degrees_end + double_longitude_minutes_end / 60.0;

          String fix_longitude_hemisphere_end =
              fix_longitude_end.substring(
                  fix_longitude_end.length() - 1); // eg "152 04.7041W" -> "W"
          if (fix_longitude_hemisphere_end.toUpperCase().equals("E")) {
            // East is positive value
            double_lon_end *= 1;
          } else if (fix_longitude_hemisphere_end.toUpperCase().equals("W")) {
            // West = negative value
            double_lon_end *= -1;
          } else {
            // System.out.println("+++ double_lon_end = " + double_lon_end);
            double_lon_end = Double.MAX_VALUE;
          }

          //
          // Compute distance and bearing between the two positions (present and 10 min/ 3 hours
          // earlier)
          //
          if (double_lat_start < 10000
              && double_lon_start < 10000
              && double_lat_end < 10000
              && double_lon_end < 10000) {
            main_RS232_RS422.RS232_compute_APR_COG_SOG(
                double_lat_start, double_lon_start, double_lat_end, double_lon_end);
          } // if (double_lat_start < 10000 && double_lon_start < 10000 && double_lat_end < 10000 &&
          // double_lon_end < 10000)
        } // if ((fix_ok) etc.

        // SOG and COG for wind (not necessary for format101 because the #101 format is already SOG
        // and COG 10 min based)
        if (main.obs_format.equals(main.FORMAT_FM13)) {
          if (fix_ok == true) {
            // computing COG and SOG for wind (position interval 10 minutes)
            //
            //
            if ((GPS_latitude_earlier_wind.compareTo("") == 0)
                || (GPS_longitude_earlier_wind.compareTo("") == 0)
                || (GPS_latitude_earlier_wind == null)
                || (GPS_longitude_earlier_wind == null)
                || (GPS_latitude_earlier_wind.indexOf("*") != -1)
                || (GPS_longitude_earlier_wind.indexOf("*") != -1)) {
              GPS_earlier_wind_ok = false;

              if (destination.equals("MAIN_SCREEN") == false) {
                // 10 minutes based (wind)
                message =
                    "[GPS] error; no 10 minutes earlier position data available for computing COG and SOG (wind)";

                main.log_turbowin_system_message(message);
              } //  if (destination.equals("MAIN_SCREEN") == false)
            } else {
              GPS_earlier_wind_ok = true;
            }
          } // if (fix_ok == true)

          if (fix_ok && GPS_earlier_wind_ok) {
            // http://www.movable-type.co.uk/scripts/latlong.html

            // MINTAKA STAR has an integrated GPS
            // GPS data is part of the saved pressure string eg:
            // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
            // <WMO tendency characteristic
            // code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
            // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
            // <course> is True, <speed> in knots, <elevation> in meters
            //
            // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
            // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
            //
            // STARX (NB first part of STARX is the same as the STAR)
            // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
            // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>,
            // <temperature>,<relativeHumidity>,<wetBulbTemperature>,<dewPoint>,
            // <humiditySensor pressure in mb>*<checksum>
            //
            // 1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,1017.89*18

            double double_lat_start = Double.MAX_VALUE;
            double double_lon_start = Double.MAX_VALUE;
            double double_lat_end = Double.MAX_VALUE;
            double double_lon_end = Double.MAX_VALUE;

            // latitude START position (latitude 10 min earlier)
            //
            String fix_latitude_start =
                GPS_latitude_earlier_wind
                    .trim(); // to remove leading and trailing whitespace in the string eg " 52
            // 04.7041N" -> "52 04.7041N"
            int pos_space =
                fix_latitude_start.indexOf(" ", 0); // find first space (" ") in the string
            String fix_latitude_degrees_start =
                fix_latitude_start.substring(
                    0, pos_space); // eg "52 04.7041N" -> "52" and "2 04.7041N" -> "2"
            String fix_latitude_minutes_start =
                fix_latitude_start.substring(
                    pos_space + 1,
                    fix_latitude_start.length()
                        - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"

            double double_latitude_degrees_start = Double.parseDouble(fix_latitude_degrees_start);
            double double_latitude_minutes_start = Double.parseDouble(fix_latitude_minutes_start);

            double_lat_start = double_latitude_degrees_start + double_latitude_minutes_start / 60.0;

            String fix_latitude_hemisphere_start =
                fix_latitude_start.substring(
                    fix_latitude_start.length() - 1); // eg "52 04.7041N" -> "N"
            if (fix_latitude_hemisphere_start.toUpperCase().equals("N")) {
              // North is positive value
              double_lat_start *= 1;
            } else if (fix_latitude_hemisphere_start.toUpperCase().equals("S")) {
              // South = negative value
              double_lat_start *= -1;
            } else {
              double_lat_start = Double.MAX_VALUE;
            }

            // longitude START position (latitude 10 min earlier)
            //
            String fix_longitude_start =
                GPS_longitude_earlier_wind
                    .trim(); // to remove leading and trailing whitespace in the string eg "152
            // 04.7041W" -> "152 04.7041W"
            pos_space = fix_longitude_start.indexOf(" ", 0); // find first space (" ") in the string
            String fix_longitude_degrees_start =
                fix_longitude_start.substring(
                    0, pos_space); // eg "152 04.7041W" -> "152" and "2 04.7041W" -> "2"
            String fix_longitude_minutes_start =
                fix_longitude_start.substring(
                    pos_space + 1,
                    fix_longitude_start.length()
                        - 1); // only the minutes of the latitude eg "152 04.7041W" -> "04.7041"

            double double_longitude_degrees_start = Double.parseDouble(fix_longitude_degrees_start);
            double double_longitude_minutes_start = Double.parseDouble(fix_longitude_minutes_start);

            double_lon_start =
                double_longitude_degrees_start + double_longitude_minutes_start / 60.0;

            String fix_longitude_hemisphere_start =
                fix_longitude_start.substring(
                    fix_longitude_start.length() - 1); // eg "152 04.7041W" -> "W"
            if (fix_longitude_hemisphere_start.toUpperCase().equals("E")) {
              // East is positive value
              double_lon_start *= 1;
            } else if (fix_longitude_hemisphere_start.toUpperCase().equals("W")) {
              // West = negative value
              double_lon_start *= -1;
            } else {
              double_lon_start = Double.MAX_VALUE;
            }

            // latitude END position (present pos)
            //
            String fix_latitude_end =
                GPS_latitude
                    .trim(); // to remove leading and trailing whitespace in the string eg " 52
            // 04.7041N" -> "52 04.7041N"
            pos_space = fix_latitude_end.indexOf(" ", 0); // find first space (" ") in the string
            String fix_latitude_degrees_end =
                fix_latitude_end.substring(
                    0, pos_space); // eg "52 04.7041N" -> "52" and "2 04.7041N" -> "2"
            String fix_latitude_minutes_end =
                fix_latitude_end.substring(
                    pos_space + 1,
                    fix_latitude_end.length()
                        - 1); // only the minutes of the latitude eg "52 04.7041N" -> "04.7041"

            double double_latitude_degrees_end = Double.parseDouble(fix_latitude_degrees_end);
            double double_latitude_minutes_end = Double.parseDouble(fix_latitude_minutes_end);

            double_lat_end = double_latitude_degrees_end + double_latitude_minutes_end / 60.0;

            String fix_latitude_hemisphere_end =
                fix_latitude_end.substring(
                    fix_latitude_end.length() - 1); // eg "52 04.7041N" -> "N"
            if (fix_latitude_hemisphere_end.toUpperCase().equals("N")) {
              // North is positive value
              double_lat_end *= 1;
            } else if (fix_latitude_hemisphere_end.toUpperCase().equals("S")) {
              // South = negative value
              double_lat_end *= -1;
            } else {
              // System.out.println("+++ double_lat_end = " + double_lat_end);
              double_lat_end = Double.MAX_VALUE;
            }

            // longitude END position (present pos)
            //
            String fix_longitude_end =
                GPS_longitude
                    .trim(); // to remove leading and trailing whitespace in the string eg "152
            // 04.7041W" -> "152 04.7041W"
            pos_space = fix_longitude_end.indexOf(" ", 0); // find first space (" ") in the string
            String fix_longitude_degrees_end =
                fix_longitude_end.substring(
                    0, pos_space); // eg "152 04.7041W" -> "152" and "2 04.7041W" -> "2"
            String fix_longitude_minutes_end =
                fix_longitude_end.substring(
                    pos_space + 1,
                    fix_longitude_end.length()
                        - 1); // only the minutes of the latitude eg "152 04.7041W" -> "04.7041"

            double double_longitude_degrees_end = Double.parseDouble(fix_longitude_degrees_end);
            double double_longitude_minutes_end = Double.parseDouble(fix_longitude_minutes_end);

            double_lon_end = double_longitude_degrees_end + double_longitude_minutes_end / 60.0;

            String fix_longitude_hemisphere_end =
                fix_longitude_end.substring(
                    fix_longitude_end.length() - 1); // eg "152 04.7041W" -> "W"
            if (fix_longitude_hemisphere_end.toUpperCase().equals("E")) {
              // East is positive value
              double_lon_end *= 1;
            } else if (fix_longitude_hemisphere_end.toUpperCase().equals("W")) {
              // West = negative value
              double_lon_end *= -1;
            } else {
              // System.out.println("+++ double_lon_end = " + double_lon_end);
              double_lon_end = Double.MAX_VALUE;
            }

            //
            // Compute distance and bearing between the two positions (present and 10 min earlier)
            //
            if (double_lat_start < 10000
                && double_lon_start < 10000
                && double_lat_end < 10000
                && double_lon_end < 10000) {
              RS232_compute_APR_COG_SOG_wind(
                  double_lat_start, double_lon_start, double_lat_end, double_lon_end);
            } // if (double_lat_start < 10000 && double_lon_start < 10000 && double_lat_end < 10000
            // && double_lon_end < 10000)
          } // if (fix_ok && GPS_earlier_wind_ ok)

        } // if (main.obs_format.equals(main.FORMAT_FM13))
        else if (main.obs_format.equals(main.FORMAT_101)) {
          if (fix_ok == true) {
            myposition.COG_APR_wind = myposition.COG_APR; // by default
            myposition.SOG_APR_wind = myposition.SOG_APR; // by default
          }
        }

        // COG and SOG logging, but only on APR scheduled hours
        //
        if (destination.equals("APR")) {
          if (main.obs_format.equals(main.FORMAT_FM13)) {
            if ((myposition.COG_APR >= 0.0)
                && (myposition.COG_APR <= 360.0)
                && (myposition.SOG_APR >= 0.0)
                && (myposition.SOG_APR <= 100.0)) {
              message =
                  "[APR] COG and SOG [3 hrs - obs]; COG and SOG calculation ok; "
                      + myposition.COG_APR
                      + "\u00B0 "
                      + myposition.SOG_APR
                      + " kts";
            } else {
              message =
                  "[APR] COG and SOG [3 hrs - obs] calculation error or start/end position not (yet) available";
            }
            main.log_turbowin_system_message(message);
          }
          if (main.obs_format.equals(main.FORMAT_FM13) || main.obs_format.equals(main.FORMAT_101)) {
            if ((myposition.COG_APR_wind >= 0.0)
                && (myposition.COG_APR_wind <= 360.0)
                && (myposition.SOG_APR_wind >= 0.0)
                && (myposition.SOG_APR_wind <= 100.0)) {
              message =
                  "[APR] COG and SOG [10 min]; COG and SOG calculation ok; "
                      + myposition.COG_APR_wind
                      + "\u00B0 "
                      + myposition.SOG_APR_wind
                      + " kts";
            } else {
              message =
                  "[APR] COG and SOG [10 min] calculation error or start/end position not (yet) available";
            }
            main.log_turbowin_system_message(message);
          }
        } // if (destination.equals("APR"))

        // GPS logging
        //
        if (destination.equals("MAIN_SCREEN") == false) {

          if (fix_ok == false) {
            // NB resetting necessary now because the data will not be sent
            main.Reset_all_meteo_parameters(); // now also the manually inserted data will be
            // cleared on the main screen fields

            if (destination.equals("WOW")) {
              message =
                  "[WOW] GPS error (no GPS info in last records or no sensor data file available / record formatting error / last saved record obsolete / checksum not ok)";
            } else if (destination.equals("APR")) {
              message =
                  "[APR] GPS error (no GPS info in last records or no sensor data file available / record formatting error / last saved record obsolete / checksum not ok)";
            }
            main.log_turbowin_system_message(message);
          } // if (fix_ok == false)
          else // fix_ok == true
          {
            // NB resetting happens in a later stage when the data will be send

            message =
                "GPS position (dd-mm [N/S] ddd-mm [E/W]): "
                    + myposition.latitude_degrees
                    + "-"
                    + myposition.latitude_minutes
                    + " "
                    + myposition.latitude_hemisphere.substring(0, 1)
                    + " "
                    + myposition.longitude_degrees
                    + "-"
                    + myposition.longitude_minutes
                    + " "
                    + myposition.longitude_hemisphere.substring(0, 1);
            if (destination.equals("WOW")) {
              main.log_turbowin_system_message("[WOW] position parsing ok; " + message);
            } else if (destination.equals("APR")) {
              main.log_turbowin_system_message("[APR] position parsing ok; " + message);
            }
          } // else (fix_ok == true)
        } // if (destination.equals("MAIN_SCREEN") == false)

        //////////////  send the data
        //

        // (2nd) initialisation if necessary
        if (StarX == true) {
          if (air_temp_ok == false) {
            mytemp.air_temp = "";
          }
          if (wet_bulb_ok == false) {
            mytemp.wet_bulb_temp = "";
          }
          if (dew_point_ok == false) {
            mytemp.double_dew_point = main.INVALID;
          }
          if (RH_ok == false) {
            mytemp.double_rv = main.INVALID;
            mytemp.RH = "";
          }
        } //  if (StarX == true)

        if (pressure_ok && fix_ok) {
          if (destination.equals("WOW")) {
            JOptionPane.showMessageDialog(
                null,
                "WOW in TurboWin+ is disabled",
                main.APPLICATION_NAME,
                JOptionPane.WARNING_MESSAGE);
            /*
                           // NB 1
                           // VOOR WOW MAAKT HET OP DIT MOMENT (APRIL 2017) NOG NIET UIT OF DE GPS POSITIE OK IS OMDAT ER NOG NIET VOOR MOBIELE STATIONS INGEVOERD KAN WORDEN
                           // MAAR ALS MOBIELE STATIONS WEL KUNNEN DAN IS DEZE GPS CHECK WEL VAN TOEPASSING
                           // VOOR DE MINTAKA STAR HIER MAAR VAST EEN VOORSCHOT OP GENOMEN (GPS MOET OK ZIJN)
                           //

                           // NB 2
                           //
                           // Februari 2018: zodra een StarX echte data verstuurd onderstaande aanpassen voor de extra buiten temp. die dan beschikbaar is (var: sensor_data_record_WOW_air_temp)
                           //

                           DecimalFormat df = new DecimalFormat("0.000");           // rounding only 3 decimals
                           sensor_data_record_WOW_pressure_MSL_inhg = df.format((hulp_double_WOW_pressure_reading + WOW_height_correction_pressure + Double.parseDouble(main.barometer_instrument_correction)) * HPA_TO_INHG);
                           RS232_Send_Sensor_Data_to_WOW(sensor_data_record_WOW_pressure_MSL_inhg);
            */
          } else if (destination.equals("APR")) {
            // DecimalFormat df = new DecimalFormat("0.0");            // rounding only 1 decimal
            // sensor_data_record_APR_pressure_MSL_hpa = df.format(hulp_double_APR_pressure_reading
            // + APR_height_correction_pressure +
            // Double.parseDouble(main.barometer_instrument_correction));

            // send the data
            //
            RS232_APR_AWSR_send(retry);
          } // else if (destination.equals("APR"))
        } // if (pressure_ok && fix_ok)

        // update, every minute, the position/air pressure/temperatures fields on main screen
        //
        if (destination.equals("MAIN_SCREEN") == true) {
          if (fix_ok) {
            // NB Star with integrated GPS

            if ((myposition.COG_APR >= 0.0 && myposition.COG_APR <= 360.0)
                && (myposition.SOG_APR >= 0.0 && myposition.SOG_APR <= 100.0)) {
              // NB myposition.course and myposition.speed only for main screen
              myposition.course = Double.toString(myposition.COG_APR);
              myposition.speed = Double.toString(myposition.SOG_APR);
            } else {
              myposition.course = "";
              myposition.speed = "";
            }

            main.obsolate_GPS_data_flag = false;
            main.position_fields_update();
          } else // fix not ok
          {
            // NB Star with integrated GPS

            // myposition.course = "";
            // myposition.speed = "";

            main.obsolate_GPS_data_flag = true;
            main.position_fields_update();
          }

          if (pressure_ok) {
            main.barometer_fields_update();
          }

          if (air_temp_ok) {
            main.temperatures_fields_update();
          }

          if (ppp_ok || a_ok) {
            if (ppp_ok == false) {
              mybarograph.pressure_amount_tendency = "";
            }
            if (a_ok == false) {
              mybarograph.a_code = "";
            }

            main.barograph_fields_update();
          } //  if (ppp_ok || a_ok)
        } // if (destination.equals("MAIN_SCREEN") == true)
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }

  private static boolean RS232_Mintaka_Star_And_StarX_Check_Record_GPS(
      final String check_record, final boolean StarX) {
    // called from: RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()

    // NB Only for a rough check of a GPS position availble in the record

    boolean record_ok = true;
    String local_GPS_latitude = "";
    String local_GPS_longitude = "";

    // check on null
    //
    if (check_record == null) {
      record_ok = false;
    }

    // check on minimum record length
    //
    if (record_ok && (check_record != null)) {
      if (!(check_record.length()
          > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at leat 2
      // char pressure value= 15 chars)
      {
        record_ok = false;
      }
    } // if (record_ok)

    // check on correct number of commas in the check_record
    //
    if (record_ok && (check_record != null)) {
      int number_read_commas = 0;
      int pos = -1;

      do {
        pos = check_record.indexOf(",", pos + 1);
        if (pos != -1) // "," found
        {
          number_read_commas++;
        }
      } while (pos != -1);

      if (StarX == true) {
        if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
          record_ok = false;
        }
      } //  if (StarX == true)
      else // no StarX
      {
        if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
          record_ok = false;
        }
      } // else  (no starx)
    } // if (record_ok)

    if (record_ok && (check_record != null)) {
      // retrieved (from file) record example Mintaka Star: 1029.97,1029.97,-0.90,7, 52 41.9535N,  6
      // 14.1943E,0,0,19*1A201703152006
      // pressure at sensor height
      int pos1 =
          check_record.indexOf(
              ",", 0); // ML hereafter; position of the first "," in the last record
      int pos2 =
          check_record.indexOf(
              ",", pos1 + 1); // ppp hereafter; position of the second "," in the last record
      int pos3 =
          check_record.indexOf(
              ",", pos2 + 1); // a hereafter; position of the third "," in the last record
      int pos4 =
          check_record.indexOf(
              ",", pos3 + 1); // lat hereafter; position of the 4th "," in the last record
      int pos5 =
          check_record.indexOf(
              ",", pos4 + 1); // lon hereafter; position n of the 5th "," in the last record
      int pos6 =
          check_record.indexOf(
              ",", pos5 + 1); // course hereafter; position of the 6th "," in the last record
      // NB before already checked number commas in record

      // GPS
      //
      local_GPS_latitude = check_record.substring(pos4 + 1, pos5);
      local_GPS_longitude = check_record.substring(pos5 + 1, pos6);

      if ((local_GPS_latitude.length() > 3) && (local_GPS_longitude.length() > 3)) {
        // so there was some (at least 3 chars) GPS info found in the record
        // further checking in: RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()
        record_ok = true;
      } else {
        record_ok = false;
      }
    } // if (record_ok && (check_record != null))

    return record_ok;
  }

  public static void RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() {
    // called from: - initSynopparameters() [mytemp.java]

    // MINTAKA STAR has an integrated GPS
    // GPS data is part of th saved pressure string eg:
    // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
    // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
    // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
    // <course> is True, <speed> in knots, <elevation> in meters
    //
    // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
    // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
    //

    // STARX (NB first part of STARX is the same as the STAR)
    //       TurboWinH
    //             <station pressure in mb>,
    //             <sea level pressure in mb>,
    //             <3 hour pressure tendency>,
    //             <WMO tendency characteristic code>,
    //             <lat>,
    //             <long>,
    //             <course>,
    //             <speed>,
    //             <elevation>,
    //             <temperature>,
    //             <relativeHumidity>,
    //             <wetBulbTemperature>,
    //             <dewPoint>,
    //             <observationAge>
    //             *<checksum>
    //
    //             <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W], <course> is True,
    //             <speed> in knots, <elevation> in meters, <relativeHumidity> is 0-100,
    //             temperatures are in degrees celsius, <observatoinAge> is in seconds.
    //
    // 1009.73,1007.73,0.00,0, 52 41.9491N,  6 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
    //        ,       ,    , , 52 41.9490N,  6 14.1741E,0,0,2,    ,  ,    ,    ,*03201805230929

    // initialisation
    mytemp.air_temp =
        ""; // if not ="" -> nullpointerexception if update button clicked on main page
    mytemp.wet_bulb_temp = "";
    mytemp.RH = ""; // range 0.0 - 100.0 %

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;

        String local_dew_point = "";
        String local_obs_age = "";

        // initialisation
        // main.sensor_data_record_obs_pressure = "";

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
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null;

            // retrieve the last record in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              laatste_record = record;
            } // while ((record = in.readLine()) != null)

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka StarX format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = -1;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos != -1) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos != -1);

              if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                // System.out.println("number_read_commas = " + number_read_commas);
                // System.out.println("main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX = " +
                // main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX);
                laatste_record = null;
                System.out.println(
                    "--- Mintaka StarX format (number commas) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            } // if (laatste_record != null)

            // last retrieved record ok
            if (laatste_record != null) {
              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();

              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              /// System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max ? minutes old
              {
                // cheksum check
                //
                // example Mintaka StarX last record: 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                //
                String record_checksum =
                    laatste_record.substring(
                        laatste_record.length() - 14,
                        laatste_record.length()
                            - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211"
                String computed_checksum = Mintaka_Star_Checksum(laatste_record);

                if (computed_checksum.equals(record_checksum)) { // sensor height pressure
                  int pos1 =
                      laatste_record.indexOf(
                          ",", 0); // MSL hereafter; position of the first "," in the last record
                  int pos2 =
                      laatste_record.indexOf(
                          ",",
                          pos1 + 1); // ppp hereafter; position of the second "," in the last record
                  int pos3 =
                      laatste_record.indexOf(
                          ",",
                          pos2 + 1); // a hereafter; position of the third "," in the last record
                  int pos4 = laatste_record.indexOf(",", pos3 + 1); // lat hereafter
                  int pos5 = laatste_record.indexOf(",", pos4 + 1); // lon hereafter
                  int pos6 = laatste_record.indexOf(",", pos5 + 1); // course hereafter
                  int pos7 = laatste_record.indexOf(",", pos6 + 1); // speed hereafter
                  int pos8 = laatste_record.indexOf(",", pos7 + 1); // elevation hereafter
                  int pos9 = laatste_record.indexOf(",", pos8 + 1); // air temp
                  int pos10 = laatste_record.indexOf(",", pos9 + 1); // RH
                  int pos11 = laatste_record.indexOf(",", pos10 + 1); // wet bulb
                  int pos12 = laatste_record.indexOf(",", pos11 + 1); // dew point
                  int pos13 = laatste_record.indexOf(",", pos12 + 1); // observation age
                  int pos14 = laatste_record.indexOf("*", pos13 + 1); // pos of the "*"

                  // rounding eg: 998.19 -> 998.2
                  //        double digitale_sensor_waarde =
                  // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) +
                  // HOOGTE_CORRECTIE;
                  //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) /
                  // 10.0d;  // bv 998.19 -> 998.2

                  mytemp.air_temp = laatste_record.substring(pos9 + 1, pos10);
                  mytemp.RH = laatste_record.substring(pos10 + 1, pos11);
                  mytemp.wet_bulb_temp = laatste_record.substring(pos11 + 1, pos12);
                  local_dew_point = laatste_record.substring(pos12 + 1, pos13);
                  local_obs_age = laatste_record.substring(pos13 + 1, pos14);

                  System.out.println(
                      "--- sensor data record, air temp for obs: " + mytemp.air_temp);
                  System.out.println("--- sensor data record, RH for obs: " + mytemp.RH);
                  System.out.println(
                      "--- sensor data record, wet bulb temp for obs: " + mytemp.wet_bulb_temp);
                  System.out.println(
                      "--- sensor data record, dewpoint for obs: " + local_dew_point);
                  System.out.println("--- sensor data record, obs age for obs: " + local_obs_age);

                } // if (computed_checksum.equals(record_checksum))
                else // checksum not ok
                {
                  // System.out.println("cheksum not OK");
                  mytemp.air_temp = "";
                  mytemp.RH = "";
                  mytemp.wet_bulb_temp = "";
                  local_dew_point = "";
                  local_obs_age = "";
                } // else (checksum not ok)
              } // if (timeDiff <= 5L)
              else {
                mytemp.air_temp = "";
                mytemp.RH = "";
                mytemp.wet_bulb_temp = "";
                local_dew_point = "";
                local_obs_age = "";
              } // else
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs(): "
                    + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        // dewpoint
        //
        mytemp.double_dew_point = main.INVALID;
        if ((local_dew_point.compareTo("") != 0)
            && (local_dew_point != null)
            && (local_dew_point.indexOf("*") == -1)) {
          try {
            mytemp.double_dew_point = Double.parseDouble(local_dew_point.trim());
          } catch (NumberFormatException e) {
            System.out.println(
                "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
            // mytemp.air_temp = "";
            mytemp.double_dew_point = main.INVALID;
          }
        }

        return local_obs_age;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        String error_info = "";
        final int AGE_NOT_OK = 999999;
        double hulp_air_temp = 999999; // 999999 = random number but > 99.9
        double hulp_wet_bulb_temp = 999999; // 999999 = random number but > 99.9

        try {
          // obs age (in sec) of the temp et al part of the last record
          //
          String return_obs_age = get();

          int hulp_return_obs_age = AGE_NOT_OK; // 999999 = random number but > 99.9
          if ((return_obs_age.compareTo("") != 0)
              && (return_obs_age != null)
              && (return_obs_age.indexOf("*") == -1)) {
            try {
              hulp_return_obs_age = Integer.parseInt(return_obs_age.trim());
            } catch (NumberFormatException e) {
              hulp_return_obs_age = AGE_NOT_OK;
              System.out.println(
                  "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
              // return_obs_age = "";
            }
          }

          if ((hulp_return_obs_age >= 0)
              && (hulp_return_obs_age
                  <= main_RS232_RS422.MAX_AGE_STARX_OBS_DATA)) // NB 60 * 10 = 600 sec = 10 minutes
          {
            // air temp
            //
            // double hulp_air_temp = 999999;                 // 999999 = random number but > 99.9
            if ((mytemp.air_temp.compareTo("") != 0)
                && (mytemp.air_temp != null)
                && (mytemp.air_temp.indexOf("*") == -1)) {
              try {
                hulp_air_temp = Double.parseDouble(mytemp.air_temp.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                // hulp_air_temp = Double.MAX_VALUE;
                mytemp.air_temp = "";
              }
            }

            if (!(hulp_air_temp > -99.9) && (hulp_air_temp < 99.9)) {
              mytemp.air_temp = "";
            }

            // wet bulb temp
            //
            // double hulp_wet_bulb_temp = 999999;                 // 999999 = random number but >
            // 99.9
            if ((mytemp.wet_bulb_temp.compareTo("") != 0)
                && (mytemp.wet_bulb_temp != null)
                && (mytemp.wet_bulb_temp.indexOf("*") == -1)) {
              try {
                hulp_wet_bulb_temp = Double.parseDouble(mytemp.wet_bulb_temp.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                mytemp.wet_bulb_temp = "";
              }
            }

            if (!(hulp_wet_bulb_temp > -99.9) && (hulp_wet_bulb_temp < 99.9)) {
              mytemp.wet_bulb_temp = "";
            }

            // RH
            //
            double hulp_RH = 999999; // 999999 = random number but > 99.9
            if ((mytemp.RH.compareTo("") != 0)
                && (mytemp.RH != null)
                && (mytemp.RH.indexOf("*") == -1)) {
              try {
                hulp_RH = Double.parseDouble(mytemp.RH.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                mytemp.RH = "";
              }
            }

            if (!(hulp_RH > 0.0) && (hulp_air_temp <= 100.0)) {
              mytemp.RH = "";
            }

          } // if ((hulp_return_obs_age > 0) && (hulp_return_obs_age <= 600))
          // else if (hulp_return_obs_age != AGE_NOT_OK)
          // {
          //   error_info = "sensor data reading ok but air temp and RH part obsolete";
          //   mytemp.air_temp         = "";
          //   mytemp.RH               = "";
          //   mytemp.wet_bulb_temp    = "";
          //   mytemp.double_dew_point = main.INVALID;
          // } // else if (hulp_return_obs_age != AGE_NOT_OK)
          else {
            error_info = "automatically retrieved sensor data reading obsolete or checksum not ok";
            mytemp.air_temp = "";
            mytemp.RH = "";
            mytemp.wet_bulb_temp = "";
            mytemp.double_dew_point = main.INVALID;
          } // else
        } // try
        catch (InterruptedException | ExecutionException ex) {
          error_info = "error retrieving air temp and RH data (" + ex + ")";
          mytemp.air_temp = "";
          mytemp.RH = "";
          mytemp.wet_bulb_temp = "";
          mytemp.double_dew_point = main.INVALID;
        } // catch

        // general error (air temp and RH no value but error_info not yet set)
        if (mytemp.air_temp.equals("") && mytemp.RH.equals("") && error_info.equals("")) {
          error_info =
              "automatically retrieved air temp and RH data not available or checksum not ok";
          mytemp.double_dew_point = main.INVALID;
        }

        if (!error_info.equals("")) {
          System.out.println("--- " + error_info);

          final JOptionPane pane_end =
              new JOptionPane(
                  error_info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog checking_ports_end_dialog = pane_end.createDialog(main.APPLICATION_NAME);

          Timer timer_end =
              new Timer(
                  2500,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      checking_ports_end_dialog.dispose();
                    }
                  });
          timer_end.setRepeats(false);
          timer_end.start();
          checking_ports_end_dialog.setVisible(true);
        } // if (!error_info.equals(""))
        else // no error encountered
        {
          mytemp.jTextField1.setText(mytemp.air_temp); // air temp
          mytemp.jTextField2.setText(mytemp.wet_bulb_temp); // wet-bulb temp

          // NB default is wet-bulb not frozen (so if no info available then by default the
          // "wet-bulb not frozen" button will be checked)
          if (hulp_wet_bulb_temp < 0.0) {
            mytemp.jRadioButton4.setSelected(true); // wet-bulb frozen radio button
          } else {
            mytemp.jRadioButton3.setSelected(true); // wet-bulb not frozen radio button
          }

          mytemp.jTextField4.setText(mytemp.RH); // RH
        } // else
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }

  public static void RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_Data_Files_For_Obs() {
    // called from: - initSynopparameters() [mybarograph.java]

    // initialisation
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;

        // initialisation
        // main.sensor_data_record_obs_pressure = "";

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
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null;

            // retrieve the last record in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              laatste_record = record;
            } // while ((record = in.readLine()) != null)

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Duo, format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = 0;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos > 0) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos > 0);

              if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA) {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Duo, format (number commas) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            } // if (laatste_record != null)

            // last retrieved record ok
            if (laatste_record != null) {
              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();

              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              /// System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

              if (timeDiff <= 3L) // max 3 minutes old
              {
                // retrieved record example Mintaka Duo: 1021.89,1021.89,1.70,1*05 201502041505
                int pos1 =
                    laatste_record.indexOf(",", 0); // position of the first "," in the last record
                int pos2 =
                    laatste_record.indexOf(
                        ",", pos1 + 1); // position of the second "," in the last record
                int pos3 =
                    laatste_record.indexOf(
                        ",", pos2 + 1); // position of the third "," in the last record

                main.sensor_data_record_obs_ppp = laatste_record.substring(pos2 + 1, pos3);
                main.sensor_data_record_obs_a =
                    laatste_record.substring(
                        pos3 + 1,
                        pos3 + 2); // a code is always 1 char (could be *, but this will be
                // corrected, see next code lines)

                // rounding eg: 998.19 -> 998.2
                //        double digitale_sensor_waarde =
                // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;
                // // bv 998.19 -> 998.2

                System.out.println(
                    "--- sensor data record, tendency for obs: " + main.sensor_data_record_obs_ppp);

                // first char after third comma could be * (always 1 char after last comma will be
                // read) if no "a" present -> correct this
                if (main.sensor_data_record_obs_ppp.indexOf("*") == 1) {
                  main.sensor_data_record_obs_a = "";
                }

                System.out.println(
                    "--- sensor data record, characteristic for obs: "
                        + main.sensor_data_record_obs_a);

              } // if (timeDiff <= 3L)
              else {
                main.sensor_data_record_obs_ppp = "";
                main.sensor_data_record_obs_a = "";
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_Data_Files_For_Obs(): "
                    + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        int hulp_pressure_change =
            3; // -1 negative; 1 positive; 0 same pressure; 3 is only start value

        // ppp
        //
        if ((main.sensor_data_record_obs_ppp.compareTo("") != 0)
            && (main.sensor_data_record_obs_ppp != null)
            && (main.sensor_data_record_obs_ppp.indexOf("*") == -1)) {
          double hulp_double_ppp_reading;
          try {
            hulp_double_ppp_reading = Double.parseDouble(main.sensor_data_record_obs_ppp.trim());
            hulp_double_ppp_reading =
                Math.round(hulp_double_ppp_reading * 10) / 10.0d; // e.g. 13.19 -> 13.2

            // System.out.println("+++ hulp_double_ppp_reading = " + hulp_double_ppp_reading);

          } catch (NumberFormatException e) {
            System.out.println(
                "--- " + "Function RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_For_Obs() " + e);
            hulp_double_ppp_reading = Double.MAX_VALUE;
          }

          if ((hulp_double_ppp_reading > -99.9) && (hulp_double_ppp_reading < 99.9)) {
            // NB hulp_pressure_change: a help to determine a (pressure characteristic)
            if (hulp_double_ppp_reading > 0.0) {
              hulp_pressure_change = 1;
            } else if (hulp_double_ppp_reading < 0.0) {
              hulp_pressure_change = -1;
            } else if (hulp_double_ppp_reading == 0.0) {
              hulp_pressure_change = 0;
            }

            // System.out.println("+++ hulp_pressure_change = " + hulp_pressure_change);

            // tendency
            mybarograph.pressure_amount_tendency =
                Double.toString(
                    Math.abs(hulp_double_ppp_reading)); // only positive value in this field
            mybarograph.jTextField1.setText(mybarograph.pressure_amount_tendency);
          }
        } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.

        // a
        //
        if ((main.sensor_data_record_obs_a.compareTo("") != 0)
            && (main.sensor_data_record_obs_a != null)
            && (main.sensor_data_record_obs_a.indexOf("*") == -1)) {
          int hulp_int_a_reading;

          try {
            hulp_int_a_reading = Integer.parseInt(main.sensor_data_record_obs_a.trim());
          } catch (NumberFormatException e) {
            System.out.println(
                "--- " + "Function RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_For_Obs() " + e);
            hulp_int_a_reading = Integer.MAX_VALUE;
          }

          if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8)) {
            // initialisation
            mybarograph.jRadioButton1.setSelected(false);
            mybarograph.jRadioButton2.setSelected(false);
            mybarograph.jRadioButton3.setSelected(false);
            mybarograph.jRadioButton4.setSelected(false);
            mybarograph.jRadioButton5.setSelected(false);
            mybarograph.jRadioButton6.setSelected(false);
            mybarograph.jRadioButton7.setSelected(false);
            mybarograph.jRadioButton8.setSelected(false);
            mybarograph.jRadioButton9.setSelected(false);
            mybarograph.jRadioButton10.setSelected(false);
            mybarograph.jRadioButton11.setSelected(false);
            mybarograph.jRadioButton12.setSelected(false);

            // pressure higher than 3hrs ago
            if (hulp_pressure_change == 1) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_0))
                mybarograph.jRadioButton1.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_1))
                mybarograph.jRadioButton2.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_2))
                mybarograph.jRadioButton3.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_3))
                mybarograph.jRadioButton4.setSelected(true);
            }

            // pressure lower than 3hrs ago
            else if (hulp_pressure_change == -1) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_5))
                mybarograph.jRadioButton5.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_6))
                mybarograph.jRadioButton6.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_7))
                mybarograph.jRadioButton7.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_8))
                mybarograph.jRadioButton8.setSelected(true);
            }

            // pressure the same as 3hrs ago
            else if (hulp_pressure_change == 0) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_0_SAME))
                mybarograph.jRadioButton9.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_4))
                mybarograph.jRadioButton10.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_5_SAME))
                mybarograph.jRadioButton11.setSelected(true);
            } else {
              mybarograph.jRadioButton12.setSelected(true); // not determined
            }
          } // if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8))
        } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }

  public static void RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs(
      boolean local_tray_icon_clicked) {
    // called from: - initSynopparameters() [mybarometer.java]
    //              - main_windowIconfied() [main.java]
    //

    // System.out.println("+++ Mintaka Duo test");

    // initialisation
    mybarometer.pressure_reading = "";

    // initialisation
    main.tray_icon_clicked = local_tray_icon_clicked;

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;

        // initialisation
        main.sensor_data_record_obs_pressure = "";

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
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null;

            // retrieve the last record in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              laatste_record = record;
            } // while ((record = in.readLine()) != null)

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Duo, format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = 0;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos > 0) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos > 0);

              if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA) {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Duo, format (number commas) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            } // if (laatste_record != null)

            // last retrieved record ok
            //
            if (laatste_record != null) {
              // System.out.println("+++ Mintaka Duo, last retrieved record = " + laatste_record);

              // String record_datum_tijd_minuten =
              // laatste_record.substring(main.type_record_datum_tijd_begin_pos,
              // main.type_record_datum_tijd_begin_pos + 12);  // bv 201302201345

              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();

              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              /// System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

              if (timeDiff <= 3L) // max 3 minutes old
              {
                // retrieved record example Mintaka Duo: 1021.89,1021.89,1.70,1*05 201502111405
                int pos1 =
                    laatste_record.indexOf(",", 0); // position of the first "," in the record
                main.sensor_data_record_obs_pressure = laatste_record.substring(0, pos1);

                // rounding eg: 998.19 -> 998.2
                //        double digitale_sensor_waarde =
                // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;
                // // bv 998.19 -> 998.2

                if (main.tray_icon_clicked == true) {
                  System.out.println(
                      "--- sensor data record, pressure for tray icon: "
                          + main.sensor_data_record_obs_pressure);
                } else {
                  System.out.println(
                      "--- sensor data record, pressure for barometer form: "
                          + main.sensor_data_record_obs_pressure);
                }
              } // if (timeDiff <= 3L)
              else {
                main.sensor_data_record_obs_pressure = "";
                System.out.println("--- automatically retrieved barometer reading obsolete");
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        if ((main.sensor_data_record_obs_pressure.compareTo("") != 0)
            && (main.sensor_data_record_obs_pressure != null)) {
          double hulp_double_pressure_reading;
          try {
            hulp_double_pressure_reading =
                Double.parseDouble(main.sensor_data_record_obs_pressure.trim());
            hulp_double_pressure_reading =
                Math.round(hulp_double_pressure_reading * 10) / 10.0d; // bv 998.19 -> 998.2
          } catch (NumberFormatException e) {
            System.out.println(
                "--- " + "Function RS232_MintakaDuo_Read_Sensor_Data_PPPP_For_Obs() " + e);
            hulp_double_pressure_reading = Double.MAX_VALUE;
          }

          if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading < 1100.0)) {
            if (main.tray_icon_clicked == true) {
              String pressure_sensor_height = "";

              // NB pressure at sensor height = pressure reading + ic
              // double double_barometer_instrument_correction =
              // Double.parseDouble(main.barometer_instrument_correction.trim());
              double double_barometer_instrument_correction = 0.0;
              try {
                double_barometer_instrument_correction =
                    Double.parseDouble(main.barometer_instrument_correction.trim());
              } catch (NumberFormatException ex) {
                // eg if there is an empty "barometer_instrument_correction" string (never ic
                // inserted)
                double_barometer_instrument_correction = 0.0;
              }

              if ((double_barometer_instrument_correction > -4.0)
                  && (double_barometer_instrument_correction < 4.0)) {
                // pressure_sensor_height = Double.toString(hulp_double_pressure_reading +
                // double_barometer_instrument_correction);
                // 1 digit precision
                pressure_sensor_height =
                    Double.toString(
                        Math.round(
                                (hulp_double_pressure_reading
                                        + double_barometer_instrument_correction)
                                    * 10d)
                            / 10d);
              } else {
                // pressure_sensor_height = Double.toString(hulp_double_pressure_reading);
                // 1 digit preciion
                pressure_sensor_height =
                    Double.toString(Math.round(hulp_double_pressure_reading * 10d) / 10d);
              }

              main_RS232_RS422.cal_system_date_time =
                  new GregorianCalendar(
                      new SimpleTimeZone(
                          0, "UTC")); // geeft systeem datum tijd in UTC van dit moment
              main_RS232_RS422.cal_system_date_time.add(
                  Calendar.MINUTE, -1); // averaged the data is 1 minute old
              String date_time =
                  main_RS232_RS422.sdf8.format(main_RS232_RS422.cal_system_date_time.getTime());

              String info = "";
              info =
                  date_time
                      + " UTC "
                      + "\n"
                      + "\n"
                      + "pressure at sensor height: "
                      + pressure_sensor_height
                      + " hPa"
                      + "\n";
              // info = date_time + " UTC " + main.newline +
              //       main.newline +
              //       "pressure at sensor height: " + mybarometer.pressure_reading + " hPa" +
              // main.newline;

              // main.trayIcon.displayMessage(main.APPLICATION_NAME, info,
              // TrayIcon.MessageType.INFO);
              JOptionPane.showMessageDialog(
                  null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);

            } else // barometer input screen opened
            {
              mybarometer.pressure_reading = Double.toString(hulp_double_pressure_reading);

              // savety check barometer input screen is opened
              if (mybarometer.jTextField1 != null) {
                mybarometer.jTextField1.setText(mybarometer.pressure_reading);
                mybarometer.jTextField2.requestFocus(); // focus on "insert draft" textfield
              }
            }
          } // if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading <
          // 1100.0))
        } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
        else // so (still) no sensor data available
        {
          if (main.tray_icon_clicked == true) {
            String info = "no sensor data available";
            // main.trayIcon.displayMessage(main.APPLICATION_NAME, info, TrayIcon.MessageType.INFO);
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
          } // if (main.tray_icon_clicked == true)
          else {
            // temporary message box (barometer data obsolete)
            final JOptionPane pane_end =
                new JOptionPane(
                    "automatically retrieved barometer reading obsolete",
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null,
                    new Object[] {},
                    null);
            final JDialog checking_ports_end_dialog = pane_end.createDialog(main.APPLICATION_NAME);

            Timer timer_end =
                new Timer(
                    2500,
                    new ActionListener() {
                      @Override
                      public void actionPerformed(ActionEvent e) {
                        checking_ports_end_dialog.dispose();
                      }
                    });
            timer_end.setRepeats(false);
            timer_end.start();
            checking_ports_end_dialog.setVisible(true);
          }
        } // else so (still) no sensor data available
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }

  public static void RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR(
      final String destination, final boolean retry) {

    // NB 'destination' possible strings: - WOW
    //                                    - APR
    //                                    - MAIN_SCREEN

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;

        // initialisation
        String sensor_data_record_WOW_APR_pressure_hpa = "";

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
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null;

            // retrieve the last record in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              laatste_record = record;
            } // while ((record = in.readLine()) != null)

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Duo reading data for WOW/APR, format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = 0;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos > 0) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos > 0);

              if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA) {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Duo reading data for WOW/APR, format (number commas) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            } // if (laatste_record != null)

            // last retrieved record ok
            //
            if (laatste_record != null) {
              // System.out.println("+++ Mintaka Duo, last retrieved record = " + laatste_record);

              // String record_datum_tijd_minuten =
              // laatste_record.substring(main.type_record_datum_tijd_begin_pos,
              // main.type_record_datum_tijd_begin_pos + 12);  // bv 201302201345

              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();
              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max 5/10 minutes old
              {
                // retrieved record example Mintaka Duo: 1021.89,1021.89,1.70,1*05 201502111405
                int pos1 =
                    laatste_record.indexOf(",", 0); // position of the first "," in the record
                sensor_data_record_WOW_APR_pressure_hpa = laatste_record.substring(0, pos1);

                // rounding eg: 998.19 -> 998.2
                //        double digitale_sensor_waarde =
                // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;
                // // bv 998.19 -> 998.2

                if (destination.equals("MAIN_SCREEN") == false) {
                  System.out.println(
                      "--- sensor data record, raw uncorrected pressure: "
                          + sensor_data_record_WOW_APR_pressure_hpa);
                }
              } // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
              else {
                sensor_data_record_WOW_APR_pressure_hpa = "";
                System.out.println(
                    "--- automatically retrieved barometer reading for WOW/APR obsolete");

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message =
                      "[WOW/APR] automatically retrieved barometer reading for WOW/APR obsolete ("
                          + timeDiff
                          + " minutes old)";
                  main.log_turbowin_system_message(message);
                }
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        // HMP155 connected? (2nd meteo instrument)
        //
        if (main.RS232_connection_mode_II == 1) {
          RS232_vaisala
              .RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument(
                  destination);
        }

        return sensor_data_record_WOW_APR_pressure_hpa;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        if (destination.equals("WOW")) {
          JOptionPane.showMessageDialog(
              null,
              "WOW in TurboWin+ is disabled",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
        } // if (destination.equals("WOW"))
        else if (destination.equals("APR")
            || destination.equals("MAIN_SCREEN")) // APR = Automated Pressure Reporting
        {
          String sensor_data_record_APR_pressure_MSL_hpa = "";
          String sensor_data_record_APR_pressure_hpa = "";

          try {
            sensor_data_record_APR_pressure_hpa =
                get(); // get the return value of the doInBackground()
          } catch (InterruptedException | ExecutionException ex) {
            if (destination.equals("MAIN_SCREEN") == false) {
              String message = "[APR] " + ex.toString();
              main.log_turbowin_system_message(message);
            }
            sensor_data_record_APR_pressure_hpa = "";
          }

          if ((sensor_data_record_APR_pressure_hpa.compareTo("") != 0)) {
            double hulp_double_APR_pressure_reading;
            try {
              hulp_double_APR_pressure_reading =
                  Double.parseDouble(sensor_data_record_APR_pressure_hpa.trim());
              // hulp_double_pressure_reading = Math.round(hulp_double_pressure_reading * 10) /
              // 10.0d;  // bv 998.19 -> 998.2
            } catch (NumberFormatException e) {
              System.out.println(
                  "--- " + "Function RS232_MintakaDuo_Read_Sensor_Data_For_WOW_APR() " + e);
              hulp_double_APR_pressure_reading = Double.MAX_VALUE;
            }

            if ((hulp_double_APR_pressure_reading > 900.0)
                && (hulp_double_APR_pressure_reading < 1100.0)) {
              DecimalFormat df = new DecimalFormat("0.0"); // rounding only 1 decimal

              // CONVERT TO MSL (+ apply barometer instrument correction)
              double APR_height_correction_pressure =
                  RS232_WOW_APR_compute_air_pressure_height_correction(
                      hulp_double_APR_pressure_reading);

              if (destination.equals("MAIN_SCREEN") == false) {
                String message_b =
                    "[APR] air pressure at sensor height = "
                        + sensor_data_record_APR_pressure_hpa
                        + " hPa";
                main.log_turbowin_system_message(message_b);
                String message_hc =
                    "[APR] air pressure height correction = "
                        + Double.toString(APR_height_correction_pressure)
                        + " hPa";
                main.log_turbowin_system_message(message_hc);
                String message_ic =
                    "[APR] air pressure instrument correction = "
                        + main.barometer_instrument_correction
                        + " hPa";
                main.log_turbowin_system_message(message_ic);
              }

              if (APR_height_correction_pressure > -50.0 && APR_height_correction_pressure < 50.0) {

                // ic correction (make it 0.0 if outside the range)
                double double_barometer_instrument_correction = 0.0;

                if (main.barometer_instrument_correction.equals("") == false) {
                  try {
                    double_barometer_instrument_correction =
                        Double.parseDouble(main.barometer_instrument_correction);

                    if (!(double_barometer_instrument_correction >= -4.0
                        && double_barometer_instrument_correction <= 4.0)) {
                      double_barometer_instrument_correction = 0.0;
                    }
                  } catch (NumberFormatException e) {
                    double_barometer_instrument_correction = 0.0;
                  }
                }

                // DecimalFormat df = new DecimalFormat("0.0");           // rounding only 1 decimal

                ////////////////// barometer reading (pressure at sensor height + ic)
                // ////////////////
                //
                String sensor_data_record_APR_pressure_reading_hpa_corrected =
                    df.format(
                        hulp_double_APR_pressure_reading + double_barometer_instrument_correction);
                mybarometer.pressure_reading_corrected =
                    sensor_data_record_APR_pressure_reading_hpa_corrected; // now pressure at sensor
                // height corrected for
                // ic

                ///////////////// pressure at MSL (+ ic) ///////////
                //
                // sensor_data_record_APR_pressure_MSL_hpa =
                // df.format(hulp_double_APR_pressure_reading + APR_height_correction_pressure +
                // double_barometer_instrument_correction);
                double hulp_double_APR_pressure_MSL_not_rounded =
                    hulp_double_APR_pressure_reading
                        + APR_height_correction_pressure
                        + double_barometer_instrument_correction;
                BigDecimal bd =
                    new BigDecimal(hulp_double_APR_pressure_MSL_not_rounded)
                        .setScale(2, RoundingMode.HALF_UP);
                double hulp_double_APR_pressure_MSL_rounded = bd.doubleValue();
                sensor_data_record_APR_pressure_MSL_hpa =
                    Double.toString(hulp_double_APR_pressure_MSL_rounded);
                mybarometer.pressure_msl_corrected =
                    sensor_data_record_APR_pressure_MSL_hpa; // sensor_data_record_APR_pressure_MSL_hpa the baromter instrument correction is included

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_msl =
                      "[APR] air pressure MSL = " + mybarometer.pressure_msl_corrected + " hPa";
                  main.log_turbowin_system_message(message_msl);
                }

                // make IMMT (and FM13) ready
                if (destination.equals("MAIN_SCREEN") == false) {
                  RS232_make_pressure_APR_FM13_IMMT_ready();
                }

                // send the data
                //
                if (destination.equals("MAIN_SCREEN") == false) {
                  RS232_APR_AWSR_send(retry);
                }

                // update the barometer fields on main screen
                //
                if (destination.equals("MAIN_SCREEN") == true) {
                  main.barometer_fields_update();
                }
              } else {
                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_hce =
                      "[APR] computed height correction pressure not ok ("
                          + APR_height_correction_pressure
                          + ")";
                  main.log_turbowin_system_message(message_hce);
                }
                sensor_data_record_APR_pressure_MSL_hpa = "";
              }
            } // if ((hulp_double_APR_pressure_reading > 900.0) && (hulp_double_APR_pressure_reading
            // < 1100.0))
            else {
              if (destination.equals("MAIN_SCREEN") == false) {
                main.log_turbowin_system_message(
                    "[APR] automatically retrieved barometer reading outside range");
              }
              sensor_data_record_APR_pressure_MSL_hpa = "";
            }
          } //  if ((sensor_data_record_APR_pressure_hpa.compareTo("") != 0))
          else {
            if (destination.equals("MAIN_SCREEN") == false) {
              main.log_turbowin_system_message(
                  "[APR] automatically retrieved barometer reading obsolete or error during retrieving data");
            }
            sensor_data_record_APR_pressure_MSL_hpa = "";
          }
        } // else if (destination.equals("APR") || destination.equals("MAIN_SCREEN"))
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }

  private static final String STRING_PRESSURE_INTERRUPTION_EXECUTIONEXEPTION_ERROR = "11111111";
  private static final String STRING_PRESSURE_RETRIEVING_ERROR =
      "11111112"; // number without significance (random)
  private static final String STRING_PRESSURE_OUTSIDE_RANGE_ERROR =
      "11111113"; // number without significance (random)
  private static final String STRING_PRESSURE_NUMBERFORMATECEPTION_ERROR =
      "11111114"; // number without significance (random)
  private static final String STRING_PRESSURE_RECORD_GPS_ERROR =
      "11111115"; // number without significance (random)
  private static final String STRING_PRESSURE_HEIGHT_CORRECTION_ERROR =
      "11111116"; // number without significance (random)
  private static final String STRING_STARX_PART_OBSOLETE_ERROR =
      "11111117"; // number without significance (random)
  private static final String STRING_PRESSURE_AMOUNT_TENDENCY_OUTSIDE_RANGE_ERROR =
      "11111118"; // number without significance (random)
  private static final String STRING_PRESSURE_CHARACTERISTIC_OUTSIDE_RANGE_ERROR =
      "11111119"; // number without significance (random)
  private static final String STRING_AIR_TEMP_OUTSIDE_RANGE_ERROR =
      "11111120"; // number without significance (random)
  private static final String STRING_WET_BULB_OUTSIDE_RANGE_ERROR =
      "11111121"; // number without significance (random)
  private static final String STRING_DEWPOINT_OUTSIDE_RANGE_ERROR =
      "11111122"; // number without significance (random)
  private static final String STRING_RH_OUTSIDE_RANGE_ERROR =
      "11111123"; // number without significance (random)

  private static final String STRING_PRESSURE_IO_FILE_ERROR =
      "4444444"; // number without significance (random)
  private static final String STRING_PRESSURE_NO_FILE_ERROR =
      "5555555"; // number without significance (random)
  private static final String STRING_PRESSURE_RECORD_FORMAT_ERROR =
      "6666666"; // number without significance (random)
  private static final String STRING_PRESSURE_CHECKSUM_ERROR =
      "7777777"; // number without significance (random)
  private static final String STRING_PRESSURE_TIMEDIFF_ERROR =
      "8888888"; // number without significance (random)
  private static final String STRING_PRESSURE_OBS_AGE_ERROR =
      "3333333"; // number without significance (random)
  private static final int INT_PRESSURE_IO_FILE_ERROR =
      4444444; // number without significance (see STRING_PRESSURE_IO_FILE_ERROR)
  private static final int INT_AIR_TEMP_IO_FILE_ERROR =
      4444444; // number without significance (see STRING_AIR_TEMP_IO_FILE_ERROR)
  private static final int INT_PRESSURE_NO_FILE_ERROR =
      5555555; // number without significance (see STRING_PRESSURE_NO_DFILE_ERROR)
  private static final int INT_AIR_TEMP_NO_FILE_ERROR =
      5555555; // number without significance (see STRING_AIR_TEMP_NO_DFILE_ERROR)
  private static final int INT_PRESSURE_RECORD_FORMAT_ERROR =
      6666666; // number without significance (see STRING_PRESSURE_RECORD_FORMAT_ERROR)
  private static final int INT_AIR_TEMP_RECORD_FORMAT_ERROR =
      6666666; // number without significance (see STRING_AIR_TEMP_RECORD_FORMAT_ERROR)
  private static final int INT_PRESSURE_CHECKSUM_ERROR =
      7777777; // number without significance (see STRING_PRESSURE_CHECKSUM_ERROR)
  private static final int INT_AIR_TEMP_CHECKSUM_ERROR =
      7777777; // number without significance (see STRING_AIR_TEMP_CHECKSUM_ERROR)
  private static final int INT_PRESSURE_TIMEDIFF_ERROR =
      8888888; // number without significance (see STRING_PRESSURE_TIMEDIFF_ERROR)
  private static final int INT_AIR_TEMP_TIMEDIFF_ERROR =
      8888888; // number without significance (see STRING_AIR_TEMP_TIMEDIFF_ERROR)
  private static final int INT_PRESSURE_OBS_AGE_ERROR =
      3333333; // number without significance (see STRING_PRESSURE_OBS_AGE_ERROR)
  private static final int INT_AIR_TEMP_OBS_AGE_ERROR =
      3333333; // number without significance (see STRING_AIR_TEMP_OBS_AGE_ERROR)
}
