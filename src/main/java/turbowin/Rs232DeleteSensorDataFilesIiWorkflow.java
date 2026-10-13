package turbowin;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.SwingWorker;

final class Rs232DeleteSensorDataFilesIiWorkflow {

  private GregorianCalendar cal_delete_datum_tijd;

  void start() {
    // NB file name e.g. sensor_data_II_2019080301.txt  (for the second meteo sensor eg thermometer,
    // never for an AWS)
    //    (note there is also sensor_data_2019080301.txt)

    // console
    System.out.println("--- deleting old (> 200 hrs) sensor data files II");

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        String file_naam;
        String volledig_path_sensor_data;
        int i_start;
        int i_end;

        i_start = 200; // 200 hours
        i_end = 8760; // 1 year

        for (int i = i_start; i < i_end + i_start; i++) // 365 days of 24 hours = 8760 hours
        {
          cal_delete_datum_tijd = new GregorianCalendar();
          cal_delete_datum_tijd.add(Calendar.HOUR_OF_DAY, -i);

          file_naam =
              "sensor_data_II_"
                  + main.sdf3.format(cal_delete_datum_tijd.getTime())
                  + ".txt"; // is in UTC !!
          volledig_path_sensor_data = main.logs_dir + java.io.File.separator + file_naam;

          File file_sensor_data = new File(volledig_path_sensor_data);
          if (file_sensor_data.exists()) {
            file_sensor_data.delete();
          }
        } // for (int i = i_start; i < i_end + i_start; i++)

        return null;
      } // protected Void doInBackground() throws Exception
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
