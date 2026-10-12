package turbowin;

import static turbowin.main_RS232_RS422.MAX_AGE_STARX_OBS_DATA;
import static turbowin.main_RS232_RS422.Mintaka_Star_Checksum;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

final class Rs232MintakaPpppWorkflow {

  private Rs232MintakaPpppWorkflow() {}

  static void start(boolean local_tray_icon_clicked, final boolean StarX) {
    // called from: - initSynopparameters() [mybarometer.java]
    //              - main_windowIconfied() [main.java]
    //
    //
    // MINTAKA STAR has an integrated GPS
    // GPS data is part of th saved pressure string eg:
    // <station pressure in mb>,<sea level pressure in mb>,<3 hour pressure tendency>,
    // <WMO tendency characteristic code>,<lat>,<long>,<course>,<speed>,<elevation>*<checksum>
    // where <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W],
    // <course> is True, <speed> in knots, <elevation> in meters
    //
    // 1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03
    // 1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31
    //
    //
    // STARX (NB first part of STARX is the same as the STAR)
    //       TurboWinH
    //             <station pressure in mb>,
    //             <sea level pressure in mb>,
    //             <3 hour pressure tendency>,
    //             <WMO tendency characteristic code>,
    //             <lat>,
    //             <long>,
    //             <course>,
    //             <speed>,
    //             <elevation>,
    //             <temperature>,
    //             <relativeHumidity>,
    //             <wetBulbTemperature>,
    //             <dewPoint>,
    //             <observationAge>
    //             *<checksum>
    //
    //             <lat> = ddd mm.mmmm[N|S], <long> = ddd mm.mmmm[E|W], <course> is True,
    //             <speed> in knots, <elevation> in meters, <relativeHumidity> is 0-100,
    //             temperatures are in degrees celsius, <observatoinAge> is in seconds.
    //
    // 1009.73,1007.73,0.00,0, 52 41.9491N,  6 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16

    // initialisation
    mybarometer.pressure_reading = "";

    // initialisation
    main.tray_icon_clicked = local_tray_icon_clicked;

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
        String local_obs_age = "";
        int pos1 = 0; // position of the first "," in the last record
        int pos2 = 0; // position of the second "," in the last record
        int pos3 = 0; // position of the third "," in the last record
        int pos4 = 0;
        int pos5 = 0;
        int pos6 = 0;
        int pos7 = 0;
        int pos8 = 0;
        int pos9 = 0;
        int pos10 = 0;
        int pos11 = 0;
        int pos12 = 0;
        int pos13 = 0;
        int pos14 = 0; // pos of the "*"

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
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data))) {
            record = null;
            laatste_record = null;

            // retrieve the last record in the sensor data file
            //
            while ((record = in.readLine()) != null) {
              laatste_record = record;
            } // while ((record = in.readLine()) != null)

            // check on minimum record length
            //
            if (laatste_record != null) {
              if (!(laatste_record.length()
                  > 15)) // NB > 15 is a little bit arbitrary number (YYYYMMDDHHmm + 3 commas + at
              // leat 2 char pressure value= 15 chars)
              {
                laatste_record = null;
                System.out.println(
                    "--- Mintaka Star or StarX format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record (Star and StarX)
            //
            if (laatste_record != null) {
              int number_read_commas = 0;
              int pos = -1;

              do {
                pos = laatste_record.indexOf(",", pos + 1);
                if (pos != -1) // "," found
                {
                  number_read_commas++;
                  // System.out.println("+++ number_read_commas = " + number_read_commas);
                }
              } while (pos != -1);

              if (StarX == false) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR) {
                  laatste_record = null;
                  System.out.println(
                      "--- Mintaka Star format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                }
              } // if (StarX == false)
              else if (StarX == true) {
                if (number_read_commas != main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX) {
                  laatste_record = null;
                  System.out.println(
                      "--- Mintaka StarX format (number commas) last retrieved record NOT ok (file: "
                          + volledig_path_sensor_data
                          + ")");
                  // System.out.println("--- Mintaka StarX format read commas = " +
                  // number_read_commas);
                  // System.out.println("--- Mintaka StarX format format commas = " +
                  // main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX);
                }
              } // else if (StarX == true)
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

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max 5 minutes old
              {
                // cheksum check
                //
                // example Mintaka Star last_record : 1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211
                // example Mintaka StarX last record: 1018.12,1018.13,0.00,0, 16 30.4429N, 88
                // 21.9040W,0,2,7,29.4,85,27.4,26.7,1017.89,60*18
                //
                String record_checksum =
                    laatste_record.substring(
                        laatste_record.length() - 14,
                        laatste_record.length()
                            - 12); // eg "24" from record "1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211"
                String computed_checksum = Mintaka_Star_Checksum(laatste_record);

                if (computed_checksum.equals(record_checksum)) {
                  if (StarX == false) {
                    pos1 =
                        laatste_record.indexOf(
                            ",", 0); // position of the first "," in the last record

                    // not StarX then there is no obs age to be checked
                    local_obs_age = "0";
                    main.sensor_data_record_obs_pressure = laatste_record.substring(0, pos1);
                  } else if (StarX == true) {
                    // StarX example: // 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                    // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                    pos1 =
                        laatste_record.indexOf(
                            ",", 0); // ML hereafter; position of the first "," in the last record
                    pos2 =
                        laatste_record.indexOf(
                            ",", pos1 + 1); // ppp hereafter; position of the second "," in the last
                    // record
                    pos3 =
                        laatste_record.indexOf(
                            ",",
                            pos2 + 1); // a hereafter; position of the third "," in the last record
                    pos4 =
                        laatste_record.indexOf(
                            ",",
                            pos3 + 1); // lat hereafter; position of the 4th "," in the last record
                    pos5 =
                        laatste_record.indexOf(
                            ",",
                            pos4
                                + 1); // lon hereafter; position n of the 5th "," in the last record
                    pos6 =
                        laatste_record.indexOf(
                            ",", pos5 + 1); // course hereafter; position of the 6th "," in the last
                    // record
                    pos7 = laatste_record.indexOf(",", pos6 + 1); // speed hereafter
                    pos8 = laatste_record.indexOf(",", pos7 + 1); // elevation hereafter
                    pos9 = laatste_record.indexOf(",", pos8 + 1); // air temp
                    pos10 = laatste_record.indexOf(",", pos9 + 1); // RH
                    pos11 = laatste_record.indexOf(",", pos10 + 1); // wet bulb
                    pos12 = laatste_record.indexOf(",", pos11 + 1); // dew point
                    pos13 = laatste_record.indexOf(",", pos12 + 1); // observation age
                    pos14 = laatste_record.indexOf("*", pos13 + 1); // pos of the "*"

                    local_obs_age = laatste_record.substring(pos13 + 1, pos14);
                    main.sensor_data_record_obs_pressure = laatste_record.substring(0, pos1);
                  }

                  if (main.tray_icon_clicked == true) {
                    System.out.println(
                        "--- sensor data record, pressure for tray icon: "
                            + main.sensor_data_record_obs_pressure);
                  } else {
                    System.out.println(
                        "--- sensor data record, pressure for barometer form: "
                            + main.sensor_data_record_obs_pressure);
                  }
                } // if (computed_checksum.equals(record_checksum))
                else // checksum not ok
                {
                  local_obs_age = "";
                  main.sensor_data_record_obs_pressure = "";
                  System.out.println(
                      "--- automatically retrieved barometer reading checksum not ok");
                } // else
              } // if (timeDiff <= 5L)
              else // timediff exceed limit
              {
                local_obs_age = "";
                main.sensor_data_record_obs_pressure = "";
                System.out.println("--- automatically retrieved barometer reading obsolete");
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return local_obs_age;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        final int AGE_NOT_OK = 999999;
        String error_info = "";
        double hulp_double_pressure_reading = Double.MAX_VALUE;

        try {
          // obs age (in sec) of the last record
          //
          String return_obs_age = get();

          int int_return_obs_age = AGE_NOT_OK; // 999999 = random number but > 99.9
          if ((return_obs_age.compareTo("") != 0)
              && (return_obs_age != null)
              && (return_obs_age.indexOf("*") == -1)) {
            try {
              int_return_obs_age = Integer.parseInt(return_obs_age.trim());
            } catch (NumberFormatException e) {
              int_return_obs_age = AGE_NOT_OK;
              System.out.println(
                  "--- "
                      + "RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs() "
                      + e);
              // return_obs_age = "";
            }
          }

          if ((int_return_obs_age >= 0) && (int_return_obs_age <= MAX_AGE_STARX_OBS_DATA)) {
            if ((main.sensor_data_record_obs_pressure.compareTo("") != 0)
                && (main.sensor_data_record_obs_pressure != null)) {
              // double hulp_double_pressure_reading;
              try {
                hulp_double_pressure_reading =
                    Double.parseDouble(main.sensor_data_record_obs_pressure.trim());
                hulp_double_pressure_reading =
                    Math.round(hulp_double_pressure_reading * 10) / 10.0d; // bv 998.19 -> 998.2
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs() "
                        + e);
                error_info = "air pressure data parsing error";
                hulp_double_pressure_reading = Double.MAX_VALUE;
              }

              if (!(hulp_double_pressure_reading > 900.0)
                  && (hulp_double_pressure_reading < 1100.0)) {
                error_info = "air pressure data outside boundary limits";
                hulp_double_pressure_reading = Double.MAX_VALUE;
              }

            } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
            else {
              error_info = "air pressure data not available";
              hulp_double_pressure_reading = Double.MAX_VALUE;
            }

          } // if ((hulp_return_obs_age >= 0) && (hulp_return_obs_age <= MAX_AGE_STARX_OBS_DATA))
          else {
            error_info = "air pressure data obsolete";
            hulp_double_pressure_reading = Double.MAX_VALUE;
          }

        } // try
        catch (InterruptedException | ExecutionException ex) {
          error_info = "error retrieving air pressure data (" + ex + ")";
          hulp_double_pressure_reading = Double.MAX_VALUE;
        } // catch

        // general error eg NumberFormatException when parsing
        if (error_info.equals("") && (hulp_double_pressure_reading > 999999.9)) {
          error_info = "automatically retrieved air pressure general error or checksum not ok";
        }

        if (!error_info.equals("")) {
          System.out.println("--- " + error_info);

          final JOptionPane pane_end =
              new JOptionPane(
                  error_info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog dialog = pane_end.createDialog(main.APPLICATION_NAME);

          Timer timer_end =
              new Timer(
                  2500,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      dialog.dispose();
                    }
                  });
          timer_end.setRepeats(false);
          timer_end.start();
          dialog.setVisible(true);
        } // if (!error_info.equals(""))
        else // no error encountered
        {
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
              // eg if ic was never inserted ("barometer_instrument_correction" then empty string)
              double_barometer_instrument_correction = 0.0;
            }

            if ((double_barometer_instrument_correction > -4.0)
                && (double_barometer_instrument_correction < 4.0)) {
              // 1 digit precision
              // NB example (double)Math.round(value * 100000d) / 100000d -> rounding for 5 digits
              // precision
              pressure_sensor_height =
                  Double.toString(
                      Math.round(
                              (hulp_double_pressure_reading
                                      + double_barometer_instrument_correction)
                                  * 10d)
                          / 10d);
            } else {
              // 1 digit precision
              pressure_sensor_height =
                  Double.toString(Math.round(hulp_double_pressure_reading * 10d) / 10d);
            }

            main_RS232_RS422.cal_system_date_time =
                new GregorianCalendar(
                    new SimpleTimeZone(0, "UTC")); // geeft systeem datum tijd in UTC van dit moment
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

            // main.trayIcon.displayMessage(main.APPLICATION_NAME, info, TrayIcon.MessageType.INFO);
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
          } // if (main.tray_icon_clicked == true)
          else // barometer input screen opened
          {
            mybarometer.pressure_reading = Double.toString(hulp_double_pressure_reading);

            // check if barometer input page is opened
            if (mybarometer.jTextField1 != null) {
              // http://stackoverflow.com/questions/17397442/how-to-check-if-a-jframe-is-opened
            }

            mybarometer.jTextField1.setText(mybarometer.pressure_reading);
            mybarometer.jTextField2.requestFocus(); // focus on "insert draft" textfield
          } // else
        } // else (no error encountered)
      } // protected void done()
    }.execute(); // new SwingWorker<String, Void>()
  }
}
