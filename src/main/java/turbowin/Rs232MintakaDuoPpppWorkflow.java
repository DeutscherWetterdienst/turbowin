package turbowin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

final class Rs232MintakaDuoPpppWorkflow {

  private Rs232MintakaDuoPpppWorkflow() {}

  static void start(boolean local_tray_icon_clicked) {
    // called from: - initSynopparameters() [mybarometer.java]
    //              - main_windowIconfied() [main.java]
    //

    // System.out.println("+++ Mintaka Duo test");

    // initialisation
    mybarometer.pressure_reading = "";

    // initialisation
    main.tray_icon_clicked = local_tray_icon_clicked;

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
        main.sensor_data_record_obs_pressure = "";

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
            //
            if (laatste_record != null) {
              // System.out.println("+++ Mintaka Duo, last retrieved record = " + laatste_record);

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

              if (timeDiff <= 3L) // max 3 minutes old
              {
                // retrieved record example Mintaka Duo: 1021.89,1021.89,1.70,1*05 201502111405
                int pos1 =
                    laatste_record.indexOf(",", 0); // position of the first "," in the record
                main.sensor_data_record_obs_pressure = laatste_record.substring(0, pos1);

                // rounding eg: 998.19 -> 998.2
                //        double digitale_sensor_waarde =
                // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;
                // // bv 998.19 -> 998.2

                if (main.tray_icon_clicked == true) {
                  System.out.println(
                      "--- sensor data record, pressure for tray icon: "
                          + main.sensor_data_record_obs_pressure);
                } else {
                  System.out.println(
                      "--- sensor data record, pressure for barometer form: "
                          + main.sensor_data_record_obs_pressure);
                }
              } // if (timeDiff <= 3L)
              else {
                main.sensor_data_record_obs_pressure = "";
                System.out.println("--- automatically retrieved barometer reading obsolete");
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        if ((main.sensor_data_record_obs_pressure.compareTo("") != 0)
            && (main.sensor_data_record_obs_pressure != null)) {
          double hulp_double_pressure_reading;
          try {
            hulp_double_pressure_reading =
                Double.parseDouble(main.sensor_data_record_obs_pressure.trim());
            hulp_double_pressure_reading =
                Math.round(hulp_double_pressure_reading * 10) / 10.0d; // bv 998.19 -> 998.2
          } catch (NumberFormatException e) {
            System.out.println(
                "--- " + "Function RS232_MintakaDuo_Read_Sensor_Data_PPPP_For_Obs() " + e);
            hulp_double_pressure_reading = Double.MAX_VALUE;
          }

          if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading < 1100.0)) {
            if (main.tray_icon_clicked == true) {
              String pressure_sensor_height = "";

              // NB pressure at sensor height = pressure reading + ic
              // double double_barometer_instrument_correction =
              // Double.parseDouble(main.barometer_instrument_correction.trim());
              double double_barometer_instrument_correction = 0.0;
              try {
                double_barometer_instrument_correction =
                    Double.parseDouble(main.barometer_instrument_correction.trim());
              } catch (NumberFormatException ex) {
                // eg if there is an empty "barometer_instrument_correction" string (never ic
                // inserted)
                double_barometer_instrument_correction = 0.0;
              }

              if ((double_barometer_instrument_correction > -4.0)
                  && (double_barometer_instrument_correction < 4.0)) {
                // pressure_sensor_height = Double.toString(hulp_double_pressure_reading +
                // double_barometer_instrument_correction);
                // 1 digit precision
                pressure_sensor_height =
                    Double.toString(
                        Math.round(
                                (hulp_double_pressure_reading
                                        + double_barometer_instrument_correction)
                                    * 10d)
                            / 10d);
              } else {
                // pressure_sensor_height = Double.toString(hulp_double_pressure_reading);
                // 1 digit preciion
                pressure_sensor_height =
                    Double.toString(Math.round(hulp_double_pressure_reading * 10d) / 10d);
              }

              main_RS232_RS422.cal_system_date_time =
                  new GregorianCalendar(
                      new SimpleTimeZone(
                          0, "UTC")); // geeft systeem datum tijd in UTC van dit moment
              main_RS232_RS422.cal_system_date_time.add(
                  Calendar.MINUTE, -1); // averaged the data is 1 minute old
              String date_time =
                  main_RS232_RS422.sdf8.format(main_RS232_RS422.cal_system_date_time.getTime());

              String info = "";
              info =
                  date_time
                      + " UTC "
                      + "\n"
                      + "\n"
                      + "pressure at sensor height: "
                      + pressure_sensor_height
                      + " hPa"
                      + "\n";
              // info = date_time + " UTC " + main.newline +
              //       main.newline +
              //       "pressure at sensor height: " + mybarometer.pressure_reading + " hPa" +
              // main.newline;

              // main.trayIcon.displayMessage(main.APPLICATION_NAME, info,
              // TrayIcon.MessageType.INFO);
              JOptionPane.showMessageDialog(
                  null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);

            } else // barometer input screen opened
            {
              mybarometer.pressure_reading = Double.toString(hulp_double_pressure_reading);

              // savety check barometer input screen is opened
              if (mybarometer.jTextField1 != null) {
                mybarometer.jTextField1.setText(mybarometer.pressure_reading);
                mybarometer.jTextField2.requestFocus(); // focus on "insert draft" textfield
              }
            }
          } // if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading <
          // 1100.0))
        } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
        else // so (still) no sensor data available
        {
          if (main.tray_icon_clicked == true) {
            String info = "no sensor data available";
            // main.trayIcon.displayMessage(main.APPLICATION_NAME, info, TrayIcon.MessageType.INFO);
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
          } // if (main.tray_icon_clicked == true)
          else {
            // temporary message box (barometer data obsolete)
            final JOptionPane pane_end =
                new JOptionPane(
                    "automatically retrieved barometer reading obsolete",
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null,
                    new Object[] {},
                    null);
            final JDialog checking_ports_end_dialog = pane_end.createDialog(main.APPLICATION_NAME);

            Timer timer_end =
                new Timer(
                    2500,
                    new ActionListener() {
                      @Override
                      public void actionPerformed(ActionEvent e) {
                        checking_ports_end_dialog.dispose();
                      }
                    });
            timer_end.setRepeats(false);
            timer_end.start();
            checking_ports_end_dialog.setVisible(true);
          }
        } // else so (still) no sensor data available
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
