package turbowin;

import static turbowin.RS232_mintaka.*;
import static turbowin.main_RS232_RS422.GPS_latitude;
import static turbowin.main_RS232_RS422.GPS_latitude_earlier;
import static turbowin.main_RS232_RS422.GPS_latitude_earlier_wind;
import static turbowin.main_RS232_RS422.GPS_latitude_hemisphere;
import static turbowin.main_RS232_RS422.GPS_longitude;
import static turbowin.main_RS232_RS422.GPS_longitude_earlier;
import static turbowin.main_RS232_RS422.GPS_longitude_earlier_wind;
import static turbowin.main_RS232_RS422.GPS_longitude_hemisphere;
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

final class Rs232MintakaWowAprWorkflow {

  private Rs232MintakaWowAprWorkflow() {}

  static void start(final String destination, final boolean retry, final boolean StarX) {
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
        File sensor_data_file =
            SensorDataFileLocator.locate(main.obs_file_datum_tijd, main.logs_dir, main.sdf3);
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

        // first check if there is a sensor data file present (and not empty)
        String volledig_path_sensor_data = sensor_data_file.getPath();
        // System.out.println("+++ te openen file voor APR obs: "+ volledig_path_sensor_data);

        if (sensor_data_file.exists() && sensor_data_file.length() > 0) // length() in bytes
        {
          try {
            record = null;
            laatste_record = null; // last record for all further manipulations
            String laatste_record_0 = null; // last record temperarily
            String laatste_record_1 = null; // 1 after last temperarily
            String laatste_record_2 = null; // 2 after last temperarily
            String laatste_record_3 = null; // 2 after last temperarily
            String laatste_record_4 = null; // 2 after last temperarily

            // retrieve the last 5 records in the sensor data file
            //
            List<String> last_records = SensorDataRecordReader.readLastRecords(sensor_data_file, 5);
            if (!last_records.isEmpty()) {
              int last_record_index = last_records.size() - 1;
              laatste_record_0 = last_records.get(last_record_index);
              if (last_record_index > 0) {
                laatste_record_1 = last_records.get(last_record_index - 1);
              }
              if (last_record_index > 1) {
                laatste_record_2 = last_records.get(last_record_index - 2);
              }
              if (last_record_index > 2) {
                laatste_record_3 = last_records.get(last_record_index - 3);
              }
              if (last_record_index > 3) {
                laatste_record_4 = last_records.get(last_record_index - 4);
              }
            }

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
              if (!MintakaRecordValidator.hasExpectedCommaCount(laatste_record, StarX)) {
                laatste_record = null;
                error_info = STRING_PRESSURE_RECORD_FORMAT_ERROR;

                if (!destination.equals("MAIN_SCREEN")) {
                  String format = StarX ? "StarX" : "Star";
                  System.out.println(
                      "--- Mintaka "
                          + format
                          + " format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                }
              }
            } // if (laatste_record != null)

            // last retrieved record ok
            //
            if (laatste_record != null) {
              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              long timeDiff =
                  SensorDataRecordAgeCalculator.ageMinutes(
                      record_datum_tijd_minuten, System::currentTimeMillis);

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
                if (MintakaRecordValidator.hasValidChecksum(laatste_record)) {
                  // System.out.println("checksum ok");

                  // retrieved (from file) record example Mintaka Star: 1029.97,1029.97,-0.90,7, 52
                  // 41.9535N,  6 14.1943E,0,0,19*1A201703152006
                  // pressure at sensor height
                  MintakaStarRecordFields commonFields =
                      MintakaStarRecordFieldsParser.parse(laatste_record, StarX);
                  if (commonFields == null) {
                    error_info = STRING_PRESSURE_RECORD_FORMAT_ERROR;
                    laatste_record = null;
                  } else {
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
                    local_sensor_data_record_obs_pressure = commonFields.pressure;

                    if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println(
                        "--- sensor data record, raw uncorrected pressure: "
                            + local_sensor_data_record_obs_pressure);
                    }

                  // ppp (3hr pressure tendency)
                  //
                    local_sensor_data_record_obs_ppp = commonFields.pressureTendency;
                    if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println(
                        "--- sensor data record, 3hr pressure tendency (ppp): "
                            + local_sensor_data_record_obs_ppp);
                    }

                  // a (3hr pressure tendency characteristic; WMO code)
                  //
                    local_sensor_data_record_obs_a = commonFields.tendencyCharacteristic;
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
                    GPS_latitude = commonFields.latitude;
                    GPS_longitude = commonFields.longitude;
                    local_obs_age = commonFields.observationAge;

                    if (destination.equals("MAIN_SCREEN") == false) {
                    System.out.println("--- sensor data record, GPS latitude: " + GPS_latitude);
                    System.out.println("--- sensor data record, GPS longitude: " + GPS_longitude);
                    }

                    if (StarX == true) {
                    MintakaStarXObservationFields fields =
                        MintakaStarXObservationFieldsParser.parse(laatste_record);
                    if (fields != null) {
                      GPS_latitude = fields.latitude;
                      GPS_longitude = fields.longitude;
                      local_sensor_data_record_obs_temp = fields.airTemperature;
                      local_sensor_data_record_obs_RH = fields.relativeHumidity;
                      local_sensor_data_record_obs_wet_bulb = fields.wetBulbTemperature;
                      local_sensor_data_record_obs_dew_point = fields.dewPoint;
                      local_obs_age = fields.observationAge;

                      if (!destination.equals("MAIN_SCREEN")) {
                        System.out.println(
                            "--- sensor data record, air temperature: "
                                + local_sensor_data_record_obs_temp);
                        System.out.println(
                            "--- sensor data record, RH: " + local_sensor_data_record_obs_RH);
                        System.out.println(
                            "--- sensor data record, wet bulb: "
                                + local_sensor_data_record_obs_wet_bulb);
                        System.out.println(
                            "--- sensor data record, dew point: "
                                + local_sensor_data_record_obs_dew_point);
                        System.out.println("--- sensor data record, obs age: " + local_obs_age);
                      }

                      int int_local_obs_age = AGE_NOT_OK;
                      if (!local_obs_age.isEmpty() && local_obs_age.indexOf("*") == -1) {
                        try {
                          int_local_obs_age = Integer.parseInt(local_obs_age.trim());
                        } catch (NumberFormatException e) {
                          int_local_obs_age = AGE_NOT_OK;
                          System.out.println(
                              "--- RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR() "
                                  + e);
                        }
                      }

                      if (int_local_obs_age < 0
                          || int_local_obs_age > main_RS232_RS422.MAX_AGE_STARX_OBS_DATA) {
                        error_info = STRING_STARX_PART_OBSOLETE_ERROR;
                        laatste_record = null;
                      }
                    } else {
                      error_info = STRING_PRESSURE_RECORD_FORMAT_ERROR;
                      laatste_record = null;
                    }
                    }
                  }
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
          String sensor_data_file_name =
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
              } else {
                GPS_latitude_earlier = "";
                GPS_longitude_earlier = "";
              }

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
}
