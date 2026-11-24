package turbowin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/** Historical hourly AWS measurements shared with the map. */
final class LatestMeasurementsData {
  private LatestMeasurementsData() {}

  static synchronized String[][] loadSnapshot() {
    read();
    return snapshot();
  }

  static synchronized String[][] snapshot() {
    String[][] copy = new String[AWS_array.length][];
    for (int i = 0; i < copy.length; i++) copy[i] = AWS_array[i].clone();
    return copy;
  }

  static synchronized void read() {
    // called from: - import_AWS_measurements() [LatestMeasurementsData.java]
    //              - OSM_AWS_Sensor_data_on_leaflet_map() [OSM.java]

    // NB this function reads sensor data from an AWS (not from an individual Vaisala or Mintaka
    // barometer) stored in files
    //
    // NB This function will be called from within a swingworker,
    //    so not necessary to use a swingworker here (it is adviced to use a swingworker when file
    // reading/writing)

    // NB Only the data at exactly the whole hour will be retrieved!!

    int number_read_commas = 0;
    int aantal_intelezen_files = 0;
    int int_record_minuten = 0;
    int pos = 0;
    String sensor_data_file_name = "";
    String volledig_path_sensor_data = "";
    String record_parameter = "";
    String record_datum_tijd = "";
    String record_minuten = "";
    boolean doorgaan_in_record = true;

    sdf3_local =
        new SimpleDateFormat(
            "yyyyMMddHH"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    sdf3_local.setTimeZone(TimeZone.getTimeZone("UTC"));

    // initialisation
    for (int i = 0; i < AANTAL_AWS_MEASUREMENTS; i++) // rows
    {
      for (int c = 0; c < AANTAL_AWS_PARAMETERS; c++) // columns (date, time, lat etc.)
      {
        AWS_array[i][c] = "";
      }
    }

    // initialisation
    // aantal_intelezen_files = 6;                            // max 6 hours back (= max duty watch
    // on bridge
    aantal_intelezen_files = AANTAL_AWS_MEASUREMENTS - 1; // -1 !! otherwise outside array boundary)

    // message to console
    // SimpleDateFormat sdf2_local;
    // sdf2_local = new SimpleDateFormat("MMMM dd, yyyy");                            // e.g "MMMM
    // dd, yyyy" -> februari 27, 2010
    // System.out.println("--- " + sdf2_local.format(new Date()) + " UTC " + "reading AWS sensor
    // data from file for latest AWS measuremets");
    System.out.println("--- " + "reading AWS sensor data from files for latest AWS measuremets");

    for (int i = aantal_intelezen_files; i >= 0; i--) // so oldest file read first
    {
      cal_file_datum_tijd = new GregorianCalendar();
      cal_file_datum_tijd.add(Calendar.HOUR_OF_DAY, -i);

      String sensor_data_file_naam_datum_tijd_deel =
          sdf3_local.format(cal_file_datum_tijd.getTime()); // -> eg 2013020308
      sensor_data_file_name = "sensor_data_" + sensor_data_file_naam_datum_tijd_deel + ".txt";

      //// TEST ////
      // System.out.println("+++ " + "sensor_data_file_name = " +  sensor_data_file_name);

      // first check if there is a sensor data file present (and not empty)
      volledig_path_sensor_data = main.logs_dir + java.io.File.separator + sensor_data_file_name;

      File sensor_data_file = new File(volledig_path_sensor_data);
      if (sensor_data_file.exists() && sensor_data_file.length() > 0) // length() in bytes
      {
        try (BufferedReader in =
            new BufferedReader(
                new FileReader(
                    volledig_path_sensor_data))) // try-with-resources: will always automatically
        // closed the open files
        {
          String record = null;

          while ((record = in.readLine())
              != null) // null means that the end of the stream has been reached
          {
            // System.out.println("+++ ingelezen record: " + record);

            // initialisation
            doorgaan_in_record = true;

            // Check the number of comma's in the record that must exactely match the fixed expected
            // number (TOTAL_NUMBER_RECORD_COMMAS)
            //
            number_read_commas = 0;
            pos = -1;

            do {
              pos = record.indexOf(",", pos + 1);
              if (pos > 0) // "," found
              {
                number_read_commas++;
              }
            } while (pos > 0);

            if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS) {
              doorgaan_in_record = false;
            }

            if ((doorgaan_in_record == true)
                && (record.length() > 18)
                && (record
                    .substring(0, 6)
                    .equals("$PEUMB"))) // NB > 18 is a little bit arbitrary number (YYYYMMDDHHmm +
            // $PEUMB = 18 chars)
            {
              pos = record.lastIndexOf(","); // searching for LAST appearance of ","
              if ((pos > 0)
                  && (pos + 1 + 12 == record.length())) // so "," found and YYYYMMDDHHmm follows
              {
                record_datum_tijd =
                    record.substring(pos + 1, pos + 1 + 10); // YYYYMMDDHH has length 10

                if (record_datum_tijd.equals(sensor_data_file_naam_datum_tijd_deel)) {
                  // System.out.println("+++
                  // record_datum_tijd.equals(sensor_data_file_naam_datum_tijd_deel");

                  // $PEUMB,20190201,150000,45.960,-2.124,128.3,10.8,131.6,997.4,1000.5,6.0,2,20.5,53.0,9.3,3.1,128.3,4.2,350.3,7.8,358.3,23.1,28.3,-10,,,,201902011500

                  // initialisation
                  doorgaan_in_record = false;
                  pos = -1;

                  if (record.length()
                      > pos + 1
                          + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every record)
                  {
                    pos =
                        record.indexOf(",", pos + 1); // searching first "," from position "pos + 1"
                    if (pos > 0) // first "," found
                    {
                      pos =
                          record.indexOf(
                              ",", pos + 1); // searching second "," from position "pos + 1"
                      if (pos > 0) // second "," found
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        String record_time = record.substring(pos + 1, pos2); // eg 150000 [HHMMSS]
                        String record_minutes = record_time.substring(2, 4); // eg 00 [MM]

                        if (record_minutes.equals("00")) {
                          // System.out.println("+++ geselecteerd 00 min record: " + record);
                          doorgaan_in_record = true;
                        }
                      }
                    }
                  }
                } else {
                  doorgaan_in_record = false;
                }
              } // if (pos > 0) -> so "," NOT found
              else {
                doorgaan_in_record = false;
              }

              // If the by TurboWin added date and time are OK and number of commas in the record
              // is OK then continue
              //
              if (doorgaan_in_record == true) {
                // System.out.println("+++ If the by TurboWin added date and time are OK and number
                // of commas in the record is OK then continue " + record);

                // initialisation
                number_read_commas = 0;
                pos = -1;

                do {
                  pos = record.indexOf(",", pos + 1); // searching "," from position "pos + 1"

                  if (pos > 0) // "," found
                  {
                    number_read_commas++;

                    // Date
                    //
                    if (number_read_commas == main.DATE_COMMA_NUMBER) {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // sensor_waarde_array[(aantal_intelezen_files - i) * 12 + int_minuten_5]
                          // = record_parameter;           // so every 5 minutes storage (=array
                          // position)
                          // datum_tijd_array[(aantal_intelezen_files - i) * 12 + int_minuten_5] =
                          // record_datum_tijd_met_minuten; // so every 5 minutes storage (=array
                          // position)

                          // AWS_array[(aantal_intelezen_files - i) * 12 +
                          // int_minuten_5][date_index] = record_parameter;          // so every 5
                          // minutes

                          // System.out.println("+++ [DATE]sensor_data_file_name = " +
                          // sensor_data_file_name);
                          // System.out.println("+++ [DATE]aantal_intelezen_files: " +
                          // aantal_intelezen_files);
                          // System.out.println("+++ [DATE]i: " + i);

                          AWS_array[(aantal_intelezen_files - i)][date_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.DATE_COMMA_NUMBER)

                    // Time
                    //
                    if (number_read_commas == main.TIME_COMMA_NUMBER) {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 +
                          // int_minuten_5][time_index] = record_parameter;          // so every 5
                          // minutes
                          AWS_array[(aantal_intelezen_files - i)][time_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.TIME_COMMA_NUMBER)

                    // Latitude
                    //
                    if (number_read_commas == main.LATITUDE_COMMA_NUMBER) {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 + int_minuten_5][lat_index]
                          // = record_parameter;          // so every 5 minutes
                          AWS_array[(aantal_intelezen_files - i)][lat_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.LATITUDE_COMMA_NUMBER)

                    // Longitude
                    //
                    if (number_read_commas == main.LONGITUDE_COMMA_NUMBER) {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 + int_minuten_5][lon_index]
                          // = record_parameter;          // so every 5 minutes
                          AWS_array[(aantal_intelezen_files - i)][lon_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.LONGITUDE_COMMA_NUMBER)

                    // SLP (Sea Level Pressure)
                    //
                    if (number_read_commas
                        == main.PRESSURE_MSL_COMMA_NUMBER) // eg air temp value always after the
                    // 12th comma
                    {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 + int_minuten_5][slp_index]
                          // = record_parameter;
                          AWS_array[(aantal_intelezen_files - i)][slp_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.PRESSURE_MSL_COMMA_NUMBER)

                    // Air temp
                    //
                    if (number_read_commas
                        == main.AIR_TEMP_COMMA_NUMBER) // eg air temp value always after the 12th
                    // comma
                    {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 +
                          // int_minuten_5][air_temp_index] = record_parameter;
                          AWS_array[(aantal_intelezen_files - i)][air_temp_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.AIR_TEMP_COMMA_NUMBER)

                    // RH
                    //
                    if (number_read_commas
                        == main.HUMIDITY_COMMA_NUMBER) // eg air temp value always after the 12th
                    // comma
                    {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 + int_minuten_5][rh_index]
                          // = record_parameter;
                          AWS_array[(aantal_intelezen_files - i)][rh_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.HUMIDITY_COMMA_NUMBER)

                    // SST
                    //
                    if (number_read_commas
                        == main.SST_COMMA_NUMBER) // eg air temp value always after the 12th comma
                    {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 + int_minuten_5][sst_index]
                          // = record_parameter;
                          AWS_array[(aantal_intelezen_files - i)][sst_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.SST_COMMA_NUMBER)

                    // True Wind Speed (at sensor height)
                    //
                    if (number_read_commas
                        == main.TRUE_WIND_SPEED_COMMA_NUMBER) // eg air temp value always after the
                    // 12th comma
                    {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        // System.out.println("+++ ingelezen record bij number_read_commas ==
                        // main.TRUE_WIND_SPEED_COMMA_NUMBER: " + record);

                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 +
                          // int_minuten_5][true_wind_speed_index] = record_parameter;
                          AWS_array[(aantal_intelezen_files - i)][true_wind_speed_index] =
                              record_parameter; // so every 1 hour

                          // System.out.println("+++ record_parameter (wind speed): " +
                          // record_parameter);

                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.TRUE_WIND_SPEED_COMMA_NUMBER)

                    // True Wind Dir (at sensor height)
                    //
                    if (number_read_commas
                        == main.TRUE_WIND_DIR_COMMA_NUMBER) // eg air temp value always after the
                    // 12th comma
                    {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 +
                          // int_minuten_5][true_wind_dir_index] = record_parameter;
                          AWS_array[(aantal_intelezen_files - i)][true_wind_dir_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.TRUE_WIND_DIR_COMMA_NUMBER)

                    // True Wind Gust (gust speed) (at sensor height)
                    //
                    if (number_read_commas
                        == main.TRUE_WIND_GUST_COMMA_NUMBER) // eg air temp value always after the
                    // 12th comma
                    {
                      if (record.length()
                          > pos + 1
                              + 12) // for safety; 12 = YYYYMMDDHHmm (always at the end of every
                      // record)
                      {
                        int pos2 = record.indexOf(",", pos + 1);
                        if (pos2 - pos >= 2) // between conmmas at least 1 char
                        {
                          record_parameter = record.substring(pos + 1, pos2);
                          // int int_minuten_5 = int_record_minuten / 5;

                          // AWS_array[(aantal_intelezen_files - i) * 12 +
                          // int_minuten_5][true_wind_gust_index] = record_parameter;
                          AWS_array[(aantal_intelezen_files - i)][true_wind_gust_index] =
                              record_parameter; // so every 1 hour
                        } // if (pos2 - pos >= 2)
                      } // if (record.length() > number_read_commas + 12)
                    } // if (number_read_commas == main.TRUE_WIND_GUST_COMMA_NUMBER)
                  } // if (pos > 0)
                } while (pos > 0);
              } // if (doorgaan_in_record)
            } // if ( (record.length() > 18) etc.
          } // while ((record = in.readLine()) != null)
        } // try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
        catch (FileNotFoundException ex) {
          System.out.println(
              "--- Function Read_Sensor_Data_Files_For_Latest_AWS_Measurements(): " + ex);
        } catch (IOException ex) {
          System.out.println(
              "--- Function Read_Sensor_Data_Files_For_Latest_AWS_Measurements(): " + ex);
        }
      } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

      // clear memory
      cal_file_datum_tijd = null;
    } // for (int i = aantal_intelezen_files; i >= 0; i--)
  }

  public static final int AANTAL_AWS_MEASUREMENTS =
      672; // 28 dagen * 24 hr = 672      192;   // 8 * 24 = 192      //84;   // 6 hours (every 5
  // minutes) e.g. from 9.00 UTC - 15.55 UTC = 6 * 12 + 12 = 84
  public static final int AANTAL_AWS_PARAMETERS = 11; // date, time, lat, lon, SLP, air temp etc.

  public static final String[][] AWS_array =
      new String[AANTAL_AWS_MEASUREMENTS]
          [AANTAL_AWS_PARAMETERS]; // NB default Sring Array values: null
  public static final int date_index = 0;
  public static final int time_index = 1;
  public static final int lat_index = 2;
  public static final int lon_index = 3;
  // private final int cog_index                                       = 4;
  // private final int sog_index                                       = 5;
  public static final int slp_index = 4;
  public static final int air_temp_index = 5;
  static final int rh_index = 6;
  public static final int sst_index = 7;
  public static final int true_wind_speed_index = 8;
  public static final int true_wind_dir_index = 9;
  static final int true_wind_gust_index = 10;

  private static GregorianCalendar cal_file_datum_tijd;
  private static SimpleDateFormat sdf3_local;
}
