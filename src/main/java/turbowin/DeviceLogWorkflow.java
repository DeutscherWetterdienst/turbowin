package turbowin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.SwingWorker;

/** Owns asynchronous loading of the current device data log. */
final class DeviceLogWorkflow {

  private DeviceLogWorkflow() {}

  static void start(mydevice_log owner, String newline) {
    new SwingWorker<String, String>() {
      @Override
      protected String doInBackground() throws Exception {
        String file_line = null;
        int teller1 = 0; // for counting total number of records in requested file (first loop)
        int teller2 = -1; // for counting numer of records in requested file (second loop)
        boolean doorgaan = true;

        String connected_devices = owner.connected_devices_in_plain_language();

        // write general comment in the device window log
        publish(new String[] {"--- GENERAL ---"});
        publish(new String[] {"ship name: " + main.ship_name});
        publish(new String[] {"connected device(s) according user settings: " + connected_devices});
        publish(
            new String[] {
              "showing: raw received data records from device + per record last 12 characters added by this program indicating UTC timestamp (YYYYMMDDHHMM) storage"
            });
        publish(
            new String[] {
              "date/time log invoke: " + main.sdf_tsl_2.format(new Date()) + " UTC"
            }); //  new Date() -> always in UTC

        // posibilities:
        //     sensor_data_file_type_1_present e.g. sensor_data_2024032607.txt
        //     sensor_data_file_type_2_present e.g. sensor_data_II_2024032607.txt
        //
        boolean sensor_data_file_type_1_present = false;
        boolean sensor_data_file_type_2_present = false;
        if (main.RS232_connection_mode != 0) {
          sensor_data_file_type_1_present = true;
        }
        if (main.RS232_connection_mode_II != 0) {
          sensor_data_file_type_2_present = true;
        }

        int start = 0;
        int end = 0;
        if (sensor_data_file_type_1_present && sensor_data_file_type_2_present) {
          start = 1;
          end = 2;
        }
        if (sensor_data_file_type_1_present && !sensor_data_file_type_2_present) {
          start = 1;
          end = 1;
        }
        if (!sensor_data_file_type_1_present && sensor_data_file_type_2_present) {
          start = 2;
          end = 2;
        }

        //
        // sensor data (GPS data yes or no included)
        //
        for (int i = start; i <= end; i++) {
          publish(new String[] {owner.log_separator()});

          // read (from the last sensor data file) and write the sensor data records on the window
          // log
          if (i == 1) {
            publish(new String[] {"--- RECEIVED DATA [CURRENT HOUR] ---"});
          } else if (i == 2) {
            publish(new String[] {"--- RECEIVED DATA 2ND METEO INSTRUMENT [CURRENT HOUR] ---"});
          }

          GregorianCalendar sensor_data_file_datum_tijd = new GregorianCalendar();
          sensor_data_file_datum_tijd.add(
              Calendar.MINUTE,
              -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
          // the file

          // determine sensor data file name
          String sensor_data_file_naam_datum_tijd_deel =
              main.sdf3.format(sensor_data_file_datum_tijd.getTime()); // e.g. 2013020308

          String sensor_data_file_name = "";
          if (i == 1) {
            sensor_data_file_name = "sensor_data_" + sensor_data_file_naam_datum_tijd_deel + ".txt";
          } else if (i == 2) {
            sensor_data_file_name =
                "sensor_data_II_" + sensor_data_file_naam_datum_tijd_deel + ".txt";
          }

          // first check if there is a sensor data file present (and not empty)
          String volledig_path_sensor_data =
              main.logs_dir + java.io.File.separator + sensor_data_file_name;

          File sensor_data_file = new File(volledig_path_sensor_data);
          if (sensor_data_file.exists()) {
            doorgaan = true;
          } else {
            publish(new String[] {"no device data file found (" + volledig_path_sensor_data + ")"});
            doorgaan = false;
          }

          if (doorgaan && sensor_data_file.length() == 0) // NB length() in bytes
          {
            publish(
                new String[] {
                  "no data found, empty device file (" + volledig_path_sensor_data + ")"
                });
          }

          if (doorgaan && sensor_data_file.length() > 0) {
            try (BufferedReader in =
                new BufferedReader(new FileReader(volledig_path_sensor_data))) {
              while ((in.readLine()) != null) {
                teller1++;
              }
            } // try
            catch (FileNotFoundException ex) {
              String info =
                  "[GENERAL] reading error "
                      + main.APPLICATION_NAME
                      + " device log (sensor data) file";
              main.log_turbowin_system_message(info);
              doorgaan = false;
            } catch (IOException ex) {
              String info =
                  "[GENERAL] error "
                      + main.APPLICATION_NAME
                      + " device log (sensor data) file ("
                      + ex
                      + ")";
              main.log_turbowin_system_message(info);
              doorgaan = false;
            } // catch
            System.out.println("--- device log number of records: " + teller1);

            if (doorgaan) {
              // trick to display always the last 100 records if > 100 records stored in requested
              // file
              if (teller1 > 100) {
                teller2 = (teller1 - 100) * -1; // e.g. teller1 = 1700 -> teller2 = -700;
              } else {
                teller2 = -1;
              }
              System.out.println(
                  "--- device log displaying from record number: " + Math.abs(teller2 + 1));

              try (BufferedReader in2 =
                  new BufferedReader(
                      new FileReader(volledig_path_sensor_data))) // try with resources
              {
                while ((file_line = in2.readLine()) != null) {
                  teller2++;
                  if (teller2 >= 0) {
                    publish(new String[] {file_line});
                  }
                } // while ((file_line = in2.readLine()) != null)
              } catch (FileNotFoundException ex) {
                String info =
                    "[GENERAL] reading error "
                        + main.APPLICATION_NAME
                        + " device log (sensor data) file";
                main.log_turbowin_system_message(info);
                doorgaan = false;
              } catch (IOException ex) {
                String info =
                    "[GENERAL] error "
                        + main.APPLICATION_NAME
                        + " device log (sensor data) file ("
                        + ex
                        + ")";
                main.log_turbowin_system_message(info);
                doorgaan = false;
              } // catch
            } // if (doorgaan)
          } // if (doorgaan && sensor_data_file.length() > 0)
        } // for (int i = start; i < end; i++)

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void process(List<String> data) {
        // process: Receives data chunks from the publish method asynchronously on the Event
        // Dispatch Thread.
        for (String received_line : data) {
          // NB eg http://www.javacreed.com/swing-worker-example/
          //    This swing component is only accessed from the process() method and never used from
          // within the doInBackbround() method
          //    or other methods directly (by directly we mean from the same thread) invoked from
          // it.

          // NB altijd in for loop omdat meerdere ontvangen string's verzameld kunnen zijn voordat
          // het hier geprocessed wordt(inherent aan SwingWorker)
          owner.appendDeviceLogLine(received_line, newline);
        }
      } // protected void process(List<String> data)

      @Override
      protected void done() {
        owner.resetDeviceLogCursor();
      } // protected void done()
    }.execute(); // new SwingWorker<String, String>()
  }
}
