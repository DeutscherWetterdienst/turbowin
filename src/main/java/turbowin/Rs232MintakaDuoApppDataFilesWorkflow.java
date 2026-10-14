package turbowin;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String laatste_record = null;

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
          try {
            laatste_record = SensorDataRecordReader.readLatestRecord(sensor_data_file);

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Duo, format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = 0;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos > 0) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos > 0);

              if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA) {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Duo, format (number commas) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            } // if (laatste_record != null)

            // last retrieved record ok
            if (laatste_record != null) {
              int pos = laatste_record.length() - 12; // pos is now start position of YYYYMMDDHHmm
              String record_datum_tijd_minuten =
                  laatste_record.substring(pos, pos + 12); // YYYYMMDDHHmm has length 12

              Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
              long system_sec = System.currentTimeMillis();

              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              /// System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

              if (timeDiff <= 3L) // max 3 minutes old
              {
                // retrieved record example Mintaka Duo: 1021.89,1021.89,1.70,1*05 201502041505
                int pos1 =
                    laatste_record.indexOf(",", 0); // position of the first "," in the last record
                int pos2 =
                    laatste_record.indexOf(
                        ",", pos1 + 1); // position of the second "," in the last record
                int pos3 =
                    laatste_record.indexOf(
                        ",", pos2 + 1); // position of the third "," in the last record

                main.sensor_data_record_obs_ppp = laatste_record.substring(pos2 + 1, pos3);
                main.sensor_data_record_obs_a =
                    laatste_record.substring(
                        pos3 + 1,
                        pos3 + 2); // a code is always 1 char (could be *, but this will be
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

              } // if (timeDiff <= 3L)
              else {
                main.sensor_data_record_obs_ppp = "";
                main.sensor_data_record_obs_a = "";
              }
            } // if (laatste_record != null)

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
