package turbowin;

import static turbowin.main_RS232_RS422.GPS_latitude_earlier_wind;
import static turbowin.main_RS232_RS422.GPS_longitude_earlier_wind;
import static turbowin.main_RS232_RS422.Mintaka_Star_Checksum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class RS232_mintaka {

  public static void RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_GPS_For_Obs(
      final String mode, final boolean StarX) {
    Rs232MintakaGpsWorkflow.start(mode, StarX);
  }

  public static void RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(
      boolean local_tray_icon_clicked, final boolean StarX) {
    Rs232MintakaPpppWorkflow.start(local_tray_icon_clicked, StarX);
  }

  public static void RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs(
      final boolean StarX) {
    Rs232MintakaApppDataFilesWorkflow.start(StarX);
  }

  static void RS232_Mintaka_Star_And_StarX_Determine_Position_Earlier_For_Wind(
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
        try {
          record =
              MintakaHistoricalRecordReader.readLatestValidRecord(
                  sensor_data_file_wind, date_time_SOG_COG_start_wind, StarX);
          if (record != null) {
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
          } else {
            GPS_latitude_earlier_wind = "";
            GPS_longitude_earlier_wind = "";
          }

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

  static void RS232_Mintaka_Star_And_StarX_Write_Error_Info(
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
    Rs232MintakaWowAprWorkflow.start(destination, retry, StarX);
  }

  static boolean RS232_Mintaka_Star_And_StarX_Check_Record_GPS(
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
    Rs232MintakaStarXObservationWorkflow.start();
  }

  public static void RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_Data_Files_For_Obs() {
    Rs232MintakaDuoApppDataFilesWorkflow.start();
  }

  public static void RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs(
      boolean local_tray_icon_clicked) {
    Rs232MintakaDuoPpppWorkflow.start(local_tray_icon_clicked);
  }

  public static void RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR(
      final String destination, final boolean retry) {
    Rs232MintakaDuoWowAprWorkflow.start(destination, retry);
  }

  static final String STRING_PRESSURE_INTERRUPTION_EXECUTIONEXEPTION_ERROR = "11111111";
  static final String STRING_PRESSURE_RETRIEVING_ERROR =
      "11111112"; // number without significance (random)
  static final String STRING_PRESSURE_OUTSIDE_RANGE_ERROR =
      "11111113"; // number without significance (random)
  static final String STRING_PRESSURE_NUMBERFORMATECEPTION_ERROR =
      "11111114"; // number without significance (random)
  static final String STRING_PRESSURE_RECORD_GPS_ERROR =
      "11111115"; // number without significance (random)
  static final String STRING_PRESSURE_HEIGHT_CORRECTION_ERROR =
      "11111116"; // number without significance (random)
  static final String STRING_STARX_PART_OBSOLETE_ERROR =
      "11111117"; // number without significance (random)
  static final String STRING_PRESSURE_AMOUNT_TENDENCY_OUTSIDE_RANGE_ERROR =
      "11111118"; // number without significance (random)
  static final String STRING_PRESSURE_CHARACTERISTIC_OUTSIDE_RANGE_ERROR =
      "11111119"; // number without significance (random)
  static final String STRING_AIR_TEMP_OUTSIDE_RANGE_ERROR =
      "11111120"; // number without significance (random)
  static final String STRING_WET_BULB_OUTSIDE_RANGE_ERROR =
      "11111121"; // number without significance (random)
  static final String STRING_DEWPOINT_OUTSIDE_RANGE_ERROR =
      "11111122"; // number without significance (random)
  static final String STRING_RH_OUTSIDE_RANGE_ERROR =
      "11111123"; // number without significance (random)

  static final String STRING_PRESSURE_IO_FILE_ERROR =
      "4444444"; // number without significance (random)
  static final String STRING_PRESSURE_NO_FILE_ERROR =
      "5555555"; // number without significance (random)
  static final String STRING_PRESSURE_RECORD_FORMAT_ERROR =
      "6666666"; // number without significance (random)
  static final String STRING_PRESSURE_CHECKSUM_ERROR =
      "7777777"; // number without significance (random)
  static final String STRING_PRESSURE_TIMEDIFF_ERROR =
      "8888888"; // number without significance (random)
  static final String STRING_PRESSURE_OBS_AGE_ERROR =
      "3333333"; // number without significance (random)
  static final int INT_PRESSURE_IO_FILE_ERROR =
      4444444; // number without significance (see STRING_PRESSURE_IO_FILE_ERROR)
  static final int INT_AIR_TEMP_IO_FILE_ERROR =
      4444444; // number without significance (see STRING_AIR_TEMP_IO_FILE_ERROR)
  static final int INT_PRESSURE_NO_FILE_ERROR =
      5555555; // number without significance (see STRING_PRESSURE_NO_DFILE_ERROR)
  static final int INT_AIR_TEMP_NO_FILE_ERROR =
      5555555; // number without significance (see STRING_AIR_TEMP_NO_DFILE_ERROR)
  static final int INT_PRESSURE_RECORD_FORMAT_ERROR =
      6666666; // number without significance (see STRING_PRESSURE_RECORD_FORMAT_ERROR)
  static final int INT_AIR_TEMP_RECORD_FORMAT_ERROR =
      6666666; // number without significance (see STRING_AIR_TEMP_RECORD_FORMAT_ERROR)
  static final int INT_PRESSURE_CHECKSUM_ERROR =
      7777777; // number without significance (see STRING_PRESSURE_CHECKSUM_ERROR)
  static final int INT_AIR_TEMP_CHECKSUM_ERROR =
      7777777; // number without significance (see STRING_AIR_TEMP_CHECKSUM_ERROR)
  static final int INT_PRESSURE_TIMEDIFF_ERROR =
      8888888; // number without significance (see STRING_PRESSURE_TIMEDIFF_ERROR)
  static final int INT_AIR_TEMP_TIMEDIFF_ERROR =
      8888888; // number without significance (see STRING_AIR_TEMP_TIMEDIFF_ERROR)
  static final int INT_PRESSURE_OBS_AGE_ERROR =
      3333333; // number without significance (see STRING_PRESSURE_OBS_AGE_ERROR)
  static final int INT_AIR_TEMP_OBS_AGE_ERROR =
      3333333; // number without significance (see STRING_AIR_TEMP_OBS_AGE_ERROR)
}
