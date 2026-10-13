package turbowin;

import static turbowin.main_RS232_RS422.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

final class Rs232DashboardValuesWorkflow {

  private Rs232DashboardValuesWorkflow() {}

  static void start() {
    // called from: init_dasboard_timer() [DASHBOARD_view.java]

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
        String last_record = null;

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

            // last retrieved record ok
            //
            if (laatste_record != null) {
              // System.out.println("+++ last retrieved record = " + laatste_record);

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

              if (timeDiff <= TIMEDIFF_SENSOR_DATA) // max 5 minutes old
              {
                last_record = laatste_record;
              } //
              else // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
              {
                // main.sensor_data_record_obs_pressure = "";
                // System.out.println("--- automatically retrieved barometer reading obsolete");
                last_record = "";
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println("--- Function RS232_read_dasboard_values(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return last_record;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {

        String last_update_record = null;
        try {
          last_update_record = get();
        } catch (InterruptedException | ExecutionException ex) {
          // Logger.getLogger(main_RS232_RS422.class.getName()).log(Level.SEVERE, null, ex);
          System.out.println(
              "+++ Function RS232_read_dasboard_values(), when retrieving last sensor data record: "
                  + ex);
        }

        String date_time_last_update =
            main.sdf_tsl_2.format(new Date()) + " UTC"; // new Date() -> always in UTC
        RS232_compute_dasboard_values(date_time_last_update, last_update_record);
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
