package turbowin;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.SwingWorker;

final class Rs232DeleteSensorDataFilesWorkflow {

  private GregorianCalendar cal_delete_datum_tijd;

  void start() {
    // NB file name e.g. sensor_data_2019080301.txt (note there is also
    // sensor_data_II_2019080301.txt)
    // NB delete files older thean 200 hours till 1 year back

    // message to console
    if (main.RS232_connection_mode == 3
        || main.RS232_connection_mode == 9
        || main.RS232_connection_mode == 11) // AWS connected
    {
      System.out.println("--- deleting old (> 1 year) sensor data files");
    } else {
      System.out.println("--- deleting old (> 200 hrs) sensor data files");
    }

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        String file_naam;
        String volledig_path_sensor_data;
        int i_start;
        int i_end;

        if (main.RS232_connection_mode == 3
            || main.RS232_connection_mode == 9
            || main.RS232_connection_mode == 11) // AWS connected
        {
          i_start = 8760; // 1 year (365 days * 24 hours)
          i_end = 17520; // 2 years
        } else {
          i_start = 200; // 200 hours
          i_end = 8760; // 1 year
        }

        // for (int i = 200; i < 8760 + 200; i++)   // 365 dagen van 24 uur = 8760 uur
        for (int i = i_start; i < i_end + i_start; i++) // 365 dagen van 24 uur = 8760 uur
        {
          cal_delete_datum_tijd = new GregorianCalendar();
          cal_delete_datum_tijd.add(Calendar.HOUR_OF_DAY, -i);

          file_naam =
              "sensor_data_"
                  + main.sdf3.format(cal_delete_datum_tijd.getTime())
                  + ".txt"; // is in UTC !!
          // volledig_path_sensor_data = main.logs_dir + java.io.File.separator +
          // java.io.File.separator + file_naam;
          volledig_path_sensor_data = main.logs_dir + java.io.File.separator + file_naam;

          // System.out.println(volledig_path_sensor_data);
          File file_sensor_data = new File(volledig_path_sensor_data);
          if (file_sensor_data.exists()) {
            file_sensor_data.delete();
          }
        } // for (int i = 200; i < 8760 + 200; i++)

        return null;
      } // protected Void doInBackground() throws Exception
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
