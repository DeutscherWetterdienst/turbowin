package turbowin;

import static turbowin.main_RS232_RS422.MAX_AGE_STARX_OBS_DATA;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
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
        File sensor_data_file =
            SensorDataFileLocator.locate(main.obs_file_datum_tijd, main.logs_dir, main.sdf3);
        String laatste_record = null;
        String local_obs_age = "";

        // initialisation
        main.sensor_data_record_obs_pressure = "";

        // first check if there is a sensor data file present (and not empty)
        String volledig_path_sensor_data = sensor_data_file.getPath();
        // System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

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
                    "--- Mintaka Star or StarX format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record (Star and StarX)
            //
            if (laatste_record != null) {
              if (!MintakaRecordValidator.hasExpectedCommaCount(laatste_record, StarX)) {
                laatste_record = null;
                String format = StarX ? "StarX" : "Star";
                System.out.println(
                    "--- Mintaka "
                        + format
                        + " format (number commas) last retrieved record NOT ok (file: "
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

              long timeDiff =
                  SensorDataRecordAgeCalculator.ageMinutes(
                      laatste_record.substring(laatste_record.length() - 12),
                      System::currentTimeMillis);

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
                if (MintakaRecordValidator.hasValidChecksum(laatste_record)) {
                  MintakaStarRecordFields fields =
                      MintakaStarRecordFieldsParser.parse(laatste_record, StarX);
                  if (fields != null) {
                    local_obs_age = fields.observationAge;
                    main.sensor_data_record_obs_pressure = fields.pressure;

                    if (main.tray_icon_clicked == true) {
                      System.out.println(
                          "--- sensor data record, pressure for tray icon: "
                              + main.sensor_data_record_obs_pressure);
                    } else {
                      System.out.println(
                          "--- sensor data record, pressure for barometer form: "
                              + main.sensor_data_record_obs_pressure);
                    }
                  } else {
                    local_obs_age = "";
                    main.sensor_data_record_obs_pressure = "";
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
