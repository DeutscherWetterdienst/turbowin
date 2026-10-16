package turbowin;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import javax.swing.SwingWorker;

final class Rs232MintakaDuoApppDataFilesWorkflow {

  private Rs232MintakaDuoApppDataFilesWorkflow() {}

  static void start() {
    // called from: - initSynopparameters() [mybarograph.java]

    // initialisation
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        File sensor_data_file =
            SensorDataFileLocator.locate(main.obs_file_datum_tijd, main.logs_dir, main.sdf3);

        // initialisation
        // main.sensor_data_record_obs_pressure = "";

        // first check if there is a sensor data file present (and not empty)
        String volledig_path_sensor_data = sensor_data_file.getPath();
        // System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

        if (sensor_data_file.exists() && sensor_data_file.length() > 0) // length() in bytes
        {
          try {
            MintakaDuoRecordReading reading =
                MintakaDuoRecordReader.readLatest(
                    sensor_data_file, System::currentTimeMillis, "Mintaka Duo");

            // last retrieved record ok
            if (reading != null && reading.isWithin(3L)) {
              // retrieved record example Mintaka Duo: 1021.89,1021.89,1.70,1*05 201502041505
              main.sensor_data_record_obs_ppp = reading.record.pressureTendency;
              main.sensor_data_record_obs_a =
                  reading
                      .record
                      .tendencyCharacteristic; // a code is always 1 char (could be *, but this
              // will be
              // corrected, see next code lines)

              // rounding eg: 998.19 -> 998.2
              //        double digitale_sensor_waarde =
              // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
              //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;
              // // bv 998.19 -> 998.2

              System.out.println(
                  "--- sensor data record, tendency for obs: " + main.sensor_data_record_obs_ppp);

              // first char after third comma could be * (always 1 char after last comma will be
              // read) if no "a" present -> correct this
              if (main.sensor_data_record_obs_ppp.indexOf("*") == 1) {
                main.sensor_data_record_obs_a = "";
              }

              System.out.println(
                  "--- sensor data record, characteristic for obs: "
                      + main.sensor_data_record_obs_a);
            } else if (reading != null) {
              main.sensor_data_record_obs_ppp = "";
              main.sensor_data_record_obs_a = "";
            }

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_Data_Files_For_Obs(): "
                    + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        int hulp_pressure_change =
            3; // -1 negative; 1 positive; 0 same pressure; 3 is only start value

        // ppp
        //
        if ((main.sensor_data_record_obs_ppp.compareTo("") != 0)
            && (main.sensor_data_record_obs_ppp != null)
            && (main.sensor_data_record_obs_ppp.indexOf("*") == -1)) {
          double hulp_double_ppp_reading;
          try {
            hulp_double_ppp_reading = Double.parseDouble(main.sensor_data_record_obs_ppp.trim());
            hulp_double_ppp_reading =
                Math.round(hulp_double_ppp_reading * 10) / 10.0d; // e.g. 13.19 -> 13.2

            // System.out.println("+++ hulp_double_ppp_reading = " + hulp_double_ppp_reading);

          } catch (NumberFormatException e) {
            System.out.println(
                "--- " + "Function RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_For_Obs() " + e);
            hulp_double_ppp_reading = Double.MAX_VALUE;
          }

          if ((hulp_double_ppp_reading > -99.9) && (hulp_double_ppp_reading < 99.9)) {
            // NB hulp_pressure_change: a help to determine a (pressure characteristic)
            if (hulp_double_ppp_reading > 0.0) {
              hulp_pressure_change = 1;
            } else if (hulp_double_ppp_reading < 0.0) {
              hulp_pressure_change = -1;
            } else if (hulp_double_ppp_reading == 0.0) {
              hulp_pressure_change = 0;
            }

            // System.out.println("+++ hulp_pressure_change = " + hulp_pressure_change);

            // tendency
            mybarograph.pressure_amount_tendency =
                Double.toString(
                    Math.abs(hulp_double_ppp_reading)); // only positive value in this field
            mybarograph.jTextField1.setText(mybarograph.pressure_amount_tendency);
          }
        } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.

        // a
        //
        if ((main.sensor_data_record_obs_a.compareTo("") != 0)
            && (main.sensor_data_record_obs_a != null)
            && (main.sensor_data_record_obs_a.indexOf("*") == -1)) {
          int hulp_int_a_reading;

          try {
            hulp_int_a_reading = Integer.parseInt(main.sensor_data_record_obs_a.trim());
          } catch (NumberFormatException e) {
            System.out.println(
                "--- " + "Function RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_For_Obs() " + e);
            hulp_int_a_reading = Integer.MAX_VALUE;
          }

          if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8)) {
            // initialisation
            mybarograph.jRadioButton1.setSelected(false);
            mybarograph.jRadioButton2.setSelected(false);
            mybarograph.jRadioButton3.setSelected(false);
            mybarograph.jRadioButton4.setSelected(false);
            mybarograph.jRadioButton5.setSelected(false);
            mybarograph.jRadioButton6.setSelected(false);
            mybarograph.jRadioButton7.setSelected(false);
            mybarograph.jRadioButton8.setSelected(false);
            mybarograph.jRadioButton9.setSelected(false);
            mybarograph.jRadioButton10.setSelected(false);
            mybarograph.jRadioButton11.setSelected(false);
            mybarograph.jRadioButton12.setSelected(false);

            // pressure higher than 3hrs ago
            if (hulp_pressure_change == 1) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_0))
                mybarograph.jRadioButton1.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_1))
                mybarograph.jRadioButton2.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_2))
                mybarograph.jRadioButton3.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_3))
                mybarograph.jRadioButton4.setSelected(true);
            }

            // pressure lower than 3hrs ago
            else if (hulp_pressure_change == -1) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_5))
                mybarograph.jRadioButton5.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_6))
                mybarograph.jRadioButton6.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_7))
                mybarograph.jRadioButton7.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_8))
                mybarograph.jRadioButton8.setSelected(true);
            }

            // pressure the same as 3hrs ago
            else if (hulp_pressure_change == 0) {
              if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_0_SAME))
                mybarograph.jRadioButton9.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_4))
                mybarograph.jRadioButton10.setSelected(true);
              else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_5_SAME))
                mybarograph.jRadioButton11.setSelected(true);
            } else {
              mybarograph.jRadioButton12.setSelected(true); // not determined
            }
          } // if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8))
        } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
