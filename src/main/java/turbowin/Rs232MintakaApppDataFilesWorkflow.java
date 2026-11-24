package turbowin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

final class Rs232MintakaApppDataFilesWorkflow {

  private Rs232MintakaApppDataFilesWorkflow() {}

  static void start(final boolean StarX) {
    // called from: - initSynopparameters() [BarographModel.java]

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
    BarographModel.pressure_amount_tendency = "";
    BarographModel.a_code = "";

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        File sensor_data_file =
            SensorDataFileLocator.locate(main.obs_file_datum_tijd, main.logs_dir, main.sdf3);
        String laatste_record = null;
        String local_obs_age = "";

        // initialisation
        // main.sensor_data_record_obs_pressure = "";

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

            // check on correct number of commas in the laatste_record
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
            if (laatste_record != null) {
              long timeDiff =
                  SensorDataRecordAgeCalculator.ageMinutes(
                      laatste_record.substring(laatste_record.length() - 12),
                      System::currentTimeMillis);

              /// System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max ? minutes old
              {
                // cheksum check
                //
                // example Mintaka Star last_record:  1022.20,1022.20,0.80,2, 52 41.9497N,  6
                // 14.1848E,0,0,-1*24201703291211
                // example Mintaka StarX last record: 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                //
                if (MintakaRecordValidator.hasValidChecksum(laatste_record)) {
                  MintakaStarRecordFields fields =
                      MintakaStarRecordFieldsParser.parse(laatste_record, StarX);
                  if (fields != null) {
                    main.sensor_data_record_obs_ppp = fields.pressureTendency;
                    main.sensor_data_record_obs_a = fields.tendencyCharacteristic;
                    local_obs_age = fields.observationAge;
                    if (StarX == false) {
                      System.out.println(
                          "--- sensor data record, tendency for obs: "
                              + main.sensor_data_record_obs_ppp);
                      System.out.println(
                          "--- sensor data record, characteristic for obs: "
                              + main.sensor_data_record_obs_a);

                    } // if (StarX == false)
                    else if (StarX == true) {
                      System.out.println(
                          "--- sensor data record, tendency for obs: "
                              + main.sensor_data_record_obs_ppp);
                      System.out.println(
                          "--- sensor data record, characteristic for obs: "
                              + main.sensor_data_record_obs_a);
                      System.out.println(
                          "--- sensor data record, obs age (StarX): " + local_obs_age);
                    } // else if (StarX == true)
                  } else {
                    main.sensor_data_record_obs_ppp = "";
                    main.sensor_data_record_obs_a = "";
                    local_obs_age = "";
                  }

                } // if (computed_checksum.equals(record_checksum))
                else // checksum not ok
                {
                  main.sensor_data_record_obs_ppp = "";
                  main.sensor_data_record_obs_a = "";
                  local_obs_age = "";
                } // else (checksum not ok)
              } // if (timeDiff <= 5L)
              else {
                main.sensor_data_record_obs_ppp = "";
                main.sensor_data_record_obs_a = "";
                local_obs_age = "";
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs(): "
                    + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        return local_obs_age;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        int hulp_pressure_change =
            3; // -1 negative; 1 positive; 0 same pressure; 3 is only start value
        final int AGE_NOT_OK = 999999;
        String error_info = "";
        double hulp_double_ppp_reading = Double.MAX_VALUE;
        int hulp_int_a_reading = Integer.MAX_VALUE;

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

          if ((int_return_obs_age >= 0)
              && (int_return_obs_age <= main_RS232_RS422.MAX_AGE_STARX_OBS_DATA)) {
            // ppp (tendency)
            //
            if ((main.sensor_data_record_obs_ppp.compareTo("") != 0)
                && (main.sensor_data_record_obs_ppp != null)
                && (main.sensor_data_record_obs_ppp.indexOf("*") == -1)) {
              // double hulp_double_ppp_reading;
              try {
                hulp_double_ppp_reading =
                    Double.parseDouble(main.sensor_data_record_obs_ppp.trim());
                hulp_double_ppp_reading =
                    Math.round(hulp_double_ppp_reading * 10) / 10.0d; // e.g. 13.19 -> 13.2

                // System.out.println("+++ hulp_double_ppp_reading = " + hulp_double_ppp_reading);
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_For_Obs() "
                        + e);
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
                // BarographModel.pressure_amount_tendency =
                // Double.toString(Math.abs(hulp_double_ppp_reading));  // only positive value in
                // this field
                // BarographModel.jTextField1.setText(BarographModel.pressure_amount_tendency);
              }
            } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
            else {
              error_info = "pressure tendency and/or characteristic not available";
              hulp_double_ppp_reading = Double.MAX_VALUE;
            }

            // a (characteristic)
            //
            if ((main.sensor_data_record_obs_a.compareTo("") != 0)
                && (main.sensor_data_record_obs_a != null)
                && (main.sensor_data_record_obs_a.indexOf("*") == -1)) {
              // int hulp_int_a_reading;
              try {
                hulp_int_a_reading = Integer.parseInt(main.sensor_data_record_obs_a.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- "
                        + "Function RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_For_Obs() "
                        + e);
                hulp_int_a_reading = Integer.MAX_VALUE;
              }

            } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
            else {
              error_info = "pressure tendency and/or characteristic not available";
              hulp_int_a_reading = Integer.MAX_VALUE;
            }

          } // if ((hulp_return_obs_age >= 0) && (hulp_return_obs_age <= MAX_AGE_STARX_OBS_DATA))
          else {
            error_info = "pressure tendency and characteristic data obsolete or checksum not ok";
            hulp_double_ppp_reading = Double.MAX_VALUE;
            hulp_int_a_reading = Integer.MAX_VALUE;
          }

        } // try
        catch (InterruptedException | ExecutionException ex) {
          error_info = "error retrieving pressure tendency and characteristic data (" + ex + ")";
          hulp_double_ppp_reading = Double.MAX_VALUE;
          hulp_int_a_reading = Integer.MAX_VALUE;
        } // catch

        // general error eg NumberFormatException when parsing hulp_int_a_reading =
        // Integer.parseInt(main.sensor_data_record_obs_a.trim());
        if (error_info.equals("")
            && (hulp_double_ppp_reading > 999999.9 || hulp_int_a_reading > 999999)) {
          error_info =
              "automatically retrieved pressure tendency and characteristic general error or checksum not ok";
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
          // set tendency textfield on a-ppp input page
          //
          BarographModel.pressure_amount_tendency =
              Double.toString(
                  Math.abs(hulp_double_ppp_reading)); // only positive value in this field
          BarographModel.jTextField1.setText(BarographModel.pressure_amount_tendency);

          // set characteristic on a-ppp input page
          //
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
        } // else ( no error encountered)
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
