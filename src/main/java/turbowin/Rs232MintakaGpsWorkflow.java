package turbowin;

import static turbowin.main_RS232_RS422.GPS_latitude;
import static turbowin.main_RS232_RS422.GPS_latitude_earlier;
import static turbowin.main_RS232_RS422.GPS_latitude_hemisphere;
import static turbowin.main_RS232_RS422.GPS_longitude;
import static turbowin.main_RS232_RS422.GPS_longitude_earlier;
import static turbowin.main_RS232_RS422.GPS_longitude_hemisphere;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

final class Rs232MintakaGpsWorkflow {

  private Rs232MintakaGpsWorkflow() {}

  static void start(final String mode, final boolean StarX) {
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
          try {
            laatste_record = SensorDataRecordReader.readLatestRecord(sensor_data_file);

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
              if (!MintakaRecordValidator.hasExpectedCommaCount(laatste_record, StarX)) {
                laatste_record = null;
                String format = StarX ? "StarX" : "Star";
                System.out.println(
                    "--- Mintaka "
                        + format
                        + " format (number commas) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            } // if (laatste_record != null)

            // last retrieved record ok
            if (laatste_record != null) {
              String record_datum_tijd_minuten =
                  laatste_record.substring(laatste_record.length() - 12);
              long timeDiff =
                  SensorDataRecordAgeCalculator.ageMinutes(
                      laatste_record.substring(laatste_record.length() - 12),
                      System::currentTimeMillis);

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
                if (MintakaRecordValidator.hasValidChecksum(laatste_record)) {
                  // System.out.println("checksum ok");

                  // retrieved (from file) record example Mintaka Star: 1029.97,1029.97,-0.90,7, 52
                  // 41.9535N,  6 14.1943E,0,0,19*1A201703152006
                  MintakaGpsFields gpsFields = MintakaGpsFieldsParser.parse(laatste_record);
                  GPS_latitude = gpsFields.latitude;
                  GPS_longitude = gpsFields.longitude;

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
            try {
              record =
                  MintakaHistoricalRecordReader.readLatestValidRecord(
                      sensor_data_file, date_time_SOG_COG_start, StarX);
              if (record != null) {
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
                    if (MintakaRecordValidator.hasValidChecksum(record)) {
                      // System.out.println("checksum ok");

                      // extra check on correct number of commas in the earlier (10 min or 3 hrs)
                      // record
                      //
                      boolean format_ok =
                          MintakaRecordValidator.hasExpectedCommaCount(record, StarX);
                      if (!format_ok) {
                        String format = StarX ? "StarX" : "Star";
                        System.out.println(
                            "--- Mintaka "
                                + format
                                + " format (number commas) rertieved record earlier (for SOG and COG) NOT ok (file: "
                                + volledig_path_sensor_data
                                + "; record: "
                                + record
                                + ")");
                      }

                      if (format_ok) {
                        // retrieved (from file) record example Mintaka Star:
                        // 1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006
                        MintakaGpsFields gpsFields = MintakaGpsFieldsParser.parse(record);
                        GPS_latitude_earlier = gpsFields.latitude;
                        GPS_longitude_earlier = gpsFields.longitude;

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
              } else {
                GPS_latitude_earlier = "";
                GPS_longitude_earlier = "";
              }

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

          double double_lat_start =
              MintakaGpsCoordinateConverter.toDecimal(GPS_latitude_earlier, true);
          double double_lon_start =
              MintakaGpsCoordinateConverter.toDecimal(GPS_longitude_earlier, false);
          double double_lat_end = MintakaGpsCoordinateConverter.toDecimal(GPS_latitude, true);
          double double_lon_end = MintakaGpsCoordinateConverter.toDecimal(GPS_longitude, false);

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
}
