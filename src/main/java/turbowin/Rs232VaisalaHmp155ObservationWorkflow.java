package turbowin;

import static turbowin.main_RS232_RS422.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

final class Rs232VaisalaHmp155ObservationWorkflow {

  private Rs232VaisalaHmp155ObservationWorkflow() {}

  static void start() {
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
          laatste_record = SensorDataRecordReader.readLatestRecord(sensor_data_file);

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

              TemperatureModel.air_temp = laatste_record.substring(pos1, pos1 + 8);
              TemperatureModel.wet_bulb_temp = laatste_record.substring(pos2, pos2 + 8);
              local_dew_point = laatste_record.substring(pos3, pos3 + 8);
              TemperatureModel.RH = laatste_record.substring(pos4, pos4 + 8);

              System.out.println(
                  "--- sensor data record, air temp for obs: " + TemperatureModel.air_temp);
              System.out.println(
                  "--- sensor data record, wet bulb temp for obs: "
                      + TemperatureModel.wet_bulb_temp);
              System.out.println("--- sensor data record, dewpoint for obs: " + local_dew_point);
              System.out.println("--- sensor data record, RH for obs: " + TemperatureModel.RH);

              laatste_record_ok = true;

            } // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
            else {
              TemperatureModel.air_temp = "";
              TemperatureModel.wet_bulb_temp = "";
              local_dew_point = "";
              TemperatureModel.RH = "";
              TemperatureModel.double_rv = main.INVALID;
              TemperatureModel.double_dew_point = main.INVALID;

              laatste_record_ok = false;
            } // else
          } // if (laatste_record != null)
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        if (laatste_record_ok == true) {
          // dewpoint
          //
          TemperatureModel.double_dew_point = main.INVALID;
          if ((local_dew_point.compareTo("") != 0)
              && (local_dew_point != null)
              && (local_dew_point.indexOf("*") == -1)) {
            try {
              TemperatureModel.double_dew_point = Double.parseDouble(local_dew_point.trim());

              if (!(TemperatureModel.double_dew_point > -70.0)
                  && (TemperatureModel.double_dew_point < 70.0)) {
                TemperatureModel.double_dew_point = main.INVALID;
              }
            } catch (NumberFormatException e) {
              System.out.println(
                  "--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
              TemperatureModel.double_dew_point = main.INVALID;

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
            // air temp check
            //
            if ((TemperatureModel.air_temp.compareTo("") != 0)
                && (TemperatureModel.air_temp != null)
                && (TemperatureModel.air_temp.indexOf("*") == -1)) {
              try {
                hulp_air_temp = Double.parseDouble(TemperatureModel.air_temp.trim());
                if (!(hulp_air_temp > -99.9) && (hulp_air_temp < 99.9)) {
                  TemperatureModel.air_temp = "";
                }
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                TemperatureModel.air_temp = "";
              }
            }

            // wet bulb temp check
            //
            // double hulp_wet_bulb_temp = 999999;                 // 999999 = random number but >
            // 99.9
            if ((TemperatureModel.wet_bulb_temp.compareTo("") != 0)
                && (TemperatureModel.wet_bulb_temp != null)
                && (TemperatureModel.wet_bulb_temp.indexOf("*") == -1)) {
              try {
                hulp_wet_bulb_temp = Double.parseDouble(TemperatureModel.wet_bulb_temp.trim());
                if (!(hulp_wet_bulb_temp > -99.9) && (hulp_wet_bulb_temp < 99.9)) {
                  TemperatureModel.wet_bulb_temp = "";
                }
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                TemperatureModel.wet_bulb_temp = "";
              }
            }

            // RH check
            //
            if ((TemperatureModel.RH.compareTo("") != 0)
                && (TemperatureModel.RH != null)
                && (TemperatureModel.RH.indexOf("*") == -1)) {
              try {
                hulp_RH = Double.parseDouble(TemperatureModel.RH.trim());
                TemperatureModel.double_rv =
                    hulp_RH / 100; // NB TemperatureModel.double_rv in range 0.0 - 1.0 [see also
                // TemperatureModel.java] !!!
                if (!(hulp_RH > 0.0) && (hulp_air_temp <= 100.0)) {
                  TemperatureModel.RH = "";
                  TemperatureModel.double_rv = main.INVALID;
                }
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                TemperatureModel.RH = "";
                TemperatureModel.double_rv = main.INVALID;
              }
            }

          } // if (return_laatste_record_ok)
          else {
            error_info =
                "automatically retrieved temperatures sensor data reading obsolete or format error";
            TemperatureModel.air_temp = "";
            TemperatureModel.RH = "";
            TemperatureModel.wet_bulb_temp = "";
            TemperatureModel.double_dew_point = main.INVALID;
            TemperatureModel.double_rv = main.INVALID;
          }
        } // try
        catch (InterruptedException | ExecutionException ex) {
          error_info = "error retrieving air temp and RH data (" + ex + ")";
          TemperatureModel.air_temp = "";
          TemperatureModel.RH = "";
          TemperatureModel.wet_bulb_temp = "";
          TemperatureModel.double_dew_point = main.INVALID;
          TemperatureModel.double_rv = main.INVALID;
        }

        // general error (air temp and RH no value but error_info not yet set)
        if (TemperatureModel.air_temp.equals("")
            && TemperatureModel.RH.equals("")
            && error_info.equals("")) {
          error_info = "automatically retrieved air temp and RH data not available";
          TemperatureModel.air_temp = "";
          TemperatureModel.RH = "";
          TemperatureModel.wet_bulb_temp = "";
          TemperatureModel.double_dew_point = main.INVALID;
          TemperatureModel.double_rv = main.INVALID;
        }

        if (!error_info.equals("")) {
          System.out.println("--- " + error_info);

          final JOptionPane pane =
              new JOptionPane(
                  error_info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog dialog = pane.createDialog(main.APPLICATION_NAME);

          Timer timer =
              new Timer(
                  2500,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      dialog.dispose();
                    }
                  });
          timer.setRepeats(false);
          timer.start();
          dialog.setVisible(true);
        } // if (!error_info.equals(""))
        else // no error encountered
        {
          TemperatureModel.jTextField1.setText(TemperatureModel.air_temp); // air temp
          TemperatureModel.jTextField2.setText(TemperatureModel.wet_bulb_temp); // wet-bulb temp

          // NB default is wet-bulb not frozen (so if no info available then by default the
          // "wet-bulb not frozen" button will be checked)
          if (hulp_wet_bulb_temp < 0.0) {
            TemperatureModel.selectedWetBulbFrozen = true;
          } else {
            TemperatureModel.selectedWetBulbFrozen = false;
          }

          TemperatureModel.jTextField4.setText(TemperatureModel.RH); // RH
          TemperatureDialog.sensorUpdated();
        } // else
      }
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
