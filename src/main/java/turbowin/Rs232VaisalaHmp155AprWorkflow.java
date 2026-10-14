package turbowin;

import static turbowin.main_RS232_RS422.*;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

final class Rs232VaisalaHmp155AprWorkflow {

  private Rs232VaisalaHmp155AprWorkflow() {}

  static void start(String destination, boolean retry) {
    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        Boolean laatste_record_ok = false;
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String record = null;
        String laatste_record = null;
        String local_dew_point = "";

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
          laatste_record =
              SensorDataRecordReader.readLatestRecord(sensor_data_file);

          if (laatste_record != null) {
            if (laatste_record.length() != main.RECORD_LENGTE_HMP155) // fixed record length
            {
              System.out.println(
                  "--- Vaisala HMP155 format (last record length = "
                      + laatste_record.length()
                      + "; should be "
                      + main.RECORD_LENGTE_HMP155
                      + ") last retrieved record NOT ok (file: "
                      + volledig_path_sensor_data
                      + ")");
              laatste_record = null;
            }
          }

          // last retrieved record ok
          if (laatste_record != null) {
            int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
            String record_datum_tijd_minuten =
                laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

            Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
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
            } // else
          } // if (laatste_record != null)
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        if (laatste_record_ok == true) {
          // dewpoint (APR)
          //
          mytemp.double_dew_point = main.INVALID;
          if ((local_dew_point.compareTo("") != 0)
              && (local_dew_point != null)
              && (local_dew_point.indexOf("*") == -1)) {
            try {
              mytemp.double_dew_point = Double.parseDouble(local_dew_point.trim());

              if (!(mytemp.double_dew_point > -70.0) && (mytemp.double_dew_point < 70.0)) {
                mytemp.double_dew_point = main.INVALID;
              } else {
                // make IMMT (and FM13) ready
                RS232_make_dew_point_APR_FM13_IMMT_ready();
              }
            } catch (NumberFormatException e) {
              System.out.println(
                  "--- "
                      + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs_APR() "
                      + e);
              mytemp.double_dew_point = main.INVALID;

              // laatste_record_ok = false;
            } // catch
          } // if
        } // if (laatste_record_ok == true)

        return laatste_record_ok;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        String error_info = "";
        double hulp_air_temp = 999999; // 999999 = random number but > 99.9
        double hulp_wet_bulb_temp = 999999; // 999999 = random number but > 99.9
        double hulp_RH = 999999; // 999999 = random number but > 99.9

        try {
          Boolean return_laatste_record_ok = get();

          if (return_laatste_record_ok) {
            // air temp check (APR)
            //
            if ((mytemp.air_temp.compareTo("") != 0)
                && (mytemp.air_temp != null)
                && (mytemp.air_temp.indexOf("*") == -1)) {
              try {
                hulp_air_temp = Double.parseDouble(mytemp.air_temp.trim());
                if (!(hulp_air_temp > -99.9) && (hulp_air_temp < 99.9)) {
                  mytemp.air_temp = "";
                } else {
                  // make IMMT (and FM13) ready
                  RS232_make_air_temp_APR_FM13_IMMT_ready();
                }
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs_APR() "
                        + e);
                mytemp.air_temp = "";
              }
            }

            // wet bulb temp check (APR)
            //
            if ((mytemp.wet_bulb_temp.compareTo("") != 0)
                && (mytemp.wet_bulb_temp != null)
                && (mytemp.wet_bulb_temp.indexOf("*") == -1)) {
              try {
                hulp_wet_bulb_temp = Double.parseDouble(mytemp.wet_bulb_temp.trim());
                if (!(hulp_wet_bulb_temp > -99.9) && (hulp_wet_bulb_temp < 99.9)) {
                  mytemp.wet_bulb_temp = "";
                } else {
                  // make IMMT (and FM13) ready
                  RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready();
                }
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs+APR() "
                        + e);
                mytemp.wet_bulb_temp = "";
              }
            }

            // RH check (APR) [NB used in IMMT but not used in FM13)
            //
            if ((mytemp.RH.compareTo("") != 0)
                && (mytemp.RH != null)
                && (mytemp.RH.indexOf("*") == -1)) {
              try {
                hulp_RH = Double.parseDouble(mytemp.RH.trim());
                mytemp.double_rv =
                    hulp_RH
                        / 100; // NB mytemp.double_rv in range 0.0 - 1.0 [see also mytemp.java] !!!
                if (!(hulp_RH > 0.0) && (hulp_air_temp <= 100.0)) {
                  mytemp.RH = "";
                  mytemp.double_rv = main.INVALID;
                } else {
                  // make IMMT ready (NB RH is not used in FM13)
                  RS232_make_RH_APR_FM13_IMMT_ready();
                }
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs_APR() "
                        + e);
                mytemp.RH = "";
                mytemp.double_rv = main.INVALID;
              }
            }

          } // if (return_laatste_record_ok)
          else {
            error_info =
                "automatically retrieved temperatures sensor data reading obsolete or format error";
            mytemp.air_temp = "";
            mytemp.RH = "";
            mytemp.wet_bulb_temp = "";
            mytemp.double_dew_point = main.INVALID;
            mytemp.double_rv = main.INVALID;
          }
        } // try
        catch (InterruptedException | ExecutionException ex) {
          error_info = "error retrieving air temp and RH data (" + ex + ")";
          mytemp.air_temp = "";
          mytemp.RH = "";
          mytemp.wet_bulb_temp = "";
          mytemp.double_dew_point = main.INVALID;
          mytemp.double_rv = main.INVALID;
        }

        // general error (air temp and RH no value but error_info not yet set)
        if (mytemp.air_temp.equals("") && mytemp.RH.equals("") && error_info.equals("")) {
          error_info = "automatically retrieved air temp and RH data not available";
          mytemp.air_temp = "";
          mytemp.RH = "";
          mytemp.wet_bulb_temp = "";
          mytemp.double_dew_point = main.INVALID;
          mytemp.double_rv = main.INVALID;
        }

        if (destination.equals("APR")) // APR = Automated Pressure Reporting
        {
          if (mytemp.air_temp.equals("") == true) {
            // if air temp is not available do not send the data
            main.log_turbowin_system_message("[APR] " + error_info);
          } else {
            // send the data
            //
            main_RS232_RS422.RS232_APR_AWSR_send(retry);
          } // else
        }

        if (destination.equals("MAIN_SCREEN") == true) {
          main.temperatures_fields_update();
        }
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
