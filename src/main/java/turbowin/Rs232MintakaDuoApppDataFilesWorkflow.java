package turbowin;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import javax.swing.SwingWorker;

final class Rs232MintakaDuoApppDataFilesWorkflow {

  private Rs232MintakaDuoApppDataFilesWorkflow() {}

  static void start() {
    // called from: - initSynopparameters() [BarographModel.java]

    // initialisation
    BarographModel.pressure_amount_tendency = "";
    BarographModel.a_code = "";

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
            BarographModel.pressure_amount_tendency =
                Double.toString(
                    Math.abs(hulp_double_ppp_reading)); // only positive value in this field
            BarographModel.amountInput.set(BarographModel.pressure_amount_tendency);
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

            // pressure higher than 3hrs ago
            if (hulp_pressure_change == 1) {
              if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_0))
                BarographModel.tendency.set(BarographModel.Tendency.RISING_THEN_FALLING);
              else if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_1))
                BarographModel.tendency.set(BarographModel.Tendency.RISING_SLOWER);
              else if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_2))
                BarographModel.tendency.set(BarographModel.Tendency.RISING);
              else if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_3))
                BarographModel.tendency.set(BarographModel.Tendency.RISING_FASTER);
            }

            // pressure lower than 3hrs ago
            else if (hulp_pressure_change == -1) {
              if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_5))
                BarographModel.tendency.set(BarographModel.Tendency.FALLING_THEN_RISING);
              else if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_6))
                BarographModel.tendency.set(BarographModel.Tendency.FALLING_SLOWER);
              else if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_7))
                BarographModel.tendency.set(BarographModel.Tendency.FALLING);
              else if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_8))
                BarographModel.tendency.set(BarographModel.Tendency.FALLING_FASTER);
            }

            // pressure the same as 3hrs ago
            else if (hulp_pressure_change == 0) {
              if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_0_SAME))
                BarographModel.tendency.set(BarographModel.Tendency.SAME_RISING_THEN_FALLING);
              else if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_4))
                BarographModel.tendency.set(BarographModel.Tendency.STEADY);
              else if (main.sensor_data_record_obs_a.trim().equals(BarographModel.A_5_SAME))
                BarographModel.tendency.set(BarographModel.Tendency.SAME_FALLING_THEN_RISING);
            } else {
              BarographModel.tendency.set(BarographModel.Tendency.UNDETERMINED); // not determined
            }
          } // if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8))
        } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
