package turbowin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

final class Rs232MintakaStarXObservationWorkflow {

  private Rs232MintakaStarXObservationWorkflow() {}

  static void start() {
    // called from: - initSynopparameters() [TemperatureModel.java]

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
    //        ,       ,    , , 52 41.9490N,  6 14.1741E,0,0,2,    ,  ,    ,    ,*03201805230929

    // initialisation
    TemperatureModel.air_temp =
        ""; // if not ="" -> nullpointerexception if update button clicked on main page
    TemperatureModel.wet_bulb_temp = "";
    TemperatureModel.RH = ""; // range 0.0 - 100.0 %

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        main.obs_file_datum_tijd = new GregorianCalendar();
        main.obs_file_datum_tijd.add(
            Calendar.MINUTE,
            -2); // of is -1 ook goed????? : to be sure there was all time that it was written to
        // the file
        File sensor_data_file;
        String laatste_record = null;

        String local_dew_point = "";
        String local_obs_age = "";

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
                    "--- Mintaka StarX format (min. record length) last retrieved record NOT ok (file: "
                        + volledig_path_sensor_data
                        + ")");
              }
            }

            // check on correct number of commas in the laatste_record
            //
            if (laatste_record != null) {
              if (!MintakaRecordValidator.hasExpectedCommaCount(laatste_record, true)) {
                // System.out.println("number_read_commas = " + number_read_commas);
                // System.out.println("main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX = " +
                // main.TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX);
                laatste_record = null;
                System.out.println(
                    "--- Mintaka StarX format (number commas) last retrieved record NOT ok (file: "
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
                // example Mintaka StarX last record: 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                //
                if (MintakaRecordValidator.hasValidChecksum(laatste_record)) {
                  MintakaStarXObservationFields fields =
                      MintakaStarXObservationFieldsParser.parse(laatste_record);
                  if (fields != null) {
                    TemperatureModel.air_temp = fields.airTemperature;
                    TemperatureModel.RH = fields.relativeHumidity;
                    TemperatureModel.wet_bulb_temp = fields.wetBulbTemperature;
                    local_dew_point = fields.dewPoint;
                    local_obs_age = fields.observationAge;

                    // rounding eg: 998.19 -> 998.2
                    //        double digitale_sensor_waarde =
                    // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) +
                    // HOOGTE_CORRECTIE;
                    //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) /
                    // 10.0d;  // bv 998.19 -> 998.2

                    System.out.println(
                        "--- sensor data record, air temp for obs: " + TemperatureModel.air_temp);
                    System.out.println(
                        "--- sensor data record, RH for obs: " + TemperatureModel.RH);
                    System.out.println(
                        "--- sensor data record, wet bulb temp for obs: "
                            + TemperatureModel.wet_bulb_temp);
                    System.out.println(
                        "--- sensor data record, dewpoint for obs: " + local_dew_point);
                    System.out.println("--- sensor data record, obs age for obs: " + local_obs_age);
                  } else {
                    TemperatureModel.air_temp = "";
                    TemperatureModel.RH = "";
                    TemperatureModel.wet_bulb_temp = "";
                    local_dew_point = "";
                    local_obs_age = "";
                  }
                } // if (computed_checksum.equals(record_checksum))
                else // checksum not ok
                {
                  // System.out.println("cheksum not OK");
                  TemperatureModel.air_temp = "";
                  TemperatureModel.RH = "";
                  TemperatureModel.wet_bulb_temp = "";
                  local_dew_point = "";
                  local_obs_age = "";
                } // else (checksum not ok)
              } // if (timeDiff <= 5L)
              else {
                TemperatureModel.air_temp = "";
                TemperatureModel.RH = "";
                TemperatureModel.wet_bulb_temp = "";
                local_dew_point = "";
                local_obs_age = "";
              } // else
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs(): "
                    + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        // dewpoint
        //
        TemperatureModel.double_dew_point = main.INVALID;
        if ((local_dew_point.compareTo("") != 0)
            && (local_dew_point != null)
            && (local_dew_point.indexOf("*") == -1)) {
          try {
            TemperatureModel.double_dew_point = Double.parseDouble(local_dew_point.trim());
          } catch (NumberFormatException e) {
            System.out.println(
                "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
            // TemperatureModel.air_temp = "";
            TemperatureModel.double_dew_point = main.INVALID;
          }
        }

        return local_obs_age;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        String error_info = "";
        final int AGE_NOT_OK = 999999;
        double hulp_air_temp = 999999; // 999999 = random number but > 99.9
        double hulp_wet_bulb_temp = 999999; // 999999 = random number but > 99.9

        try {
          // obs age (in sec) of the temp et al part of the last record
          //
          String return_obs_age = get();

          int hulp_return_obs_age = AGE_NOT_OK; // 999999 = random number but > 99.9
          if ((return_obs_age.compareTo("") != 0)
              && (return_obs_age != null)
              && (return_obs_age.indexOf("*") == -1)) {
            try {
              hulp_return_obs_age = Integer.parseInt(return_obs_age.trim());
            } catch (NumberFormatException e) {
              hulp_return_obs_age = AGE_NOT_OK;
              System.out.println(
                  "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
              // return_obs_age = "";
            }
          }

          if ((hulp_return_obs_age >= 0)
              && (hulp_return_obs_age
                  <= main_RS232_RS422.MAX_AGE_STARX_OBS_DATA)) // NB 60 * 10 = 600 sec = 10 minutes
          {
            // air temp
            //
            // double hulp_air_temp = 999999;                 // 999999 = random number but > 99.9
            if ((TemperatureModel.air_temp.compareTo("") != 0)
                && (TemperatureModel.air_temp != null)
                && (TemperatureModel.air_temp.indexOf("*") == -1)) {
              try {
                hulp_air_temp = Double.parseDouble(TemperatureModel.air_temp.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                // hulp_air_temp = Double.MAX_VALUE;
                TemperatureModel.air_temp = "";
              }
            }

            if (!(hulp_air_temp > -99.9) && (hulp_air_temp < 99.9)) {
              TemperatureModel.air_temp = "";
            }

            // wet bulb temp
            //
            // double hulp_wet_bulb_temp = 999999;                 // 999999 = random number but >
            // 99.9
            if ((TemperatureModel.wet_bulb_temp.compareTo("") != 0)
                && (TemperatureModel.wet_bulb_temp != null)
                && (TemperatureModel.wet_bulb_temp.indexOf("*") == -1)) {
              try {
                hulp_wet_bulb_temp = Double.parseDouble(TemperatureModel.wet_bulb_temp.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                TemperatureModel.wet_bulb_temp = "";
              }
            }

            if (!(hulp_wet_bulb_temp > -99.9) && (hulp_wet_bulb_temp < 99.9)) {
              TemperatureModel.wet_bulb_temp = "";
            }

            // RH
            //
            double hulp_RH = 999999; // 999999 = random number but > 99.9
            if ((TemperatureModel.RH.compareTo("") != 0)
                && (TemperatureModel.RH != null)
                && (TemperatureModel.RH.indexOf("*") == -1)) {
              try {
                hulp_RH = Double.parseDouble(TemperatureModel.RH.trim());
              } catch (NumberFormatException e) {
                System.out.println(
                    "--- " + "RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                TemperatureModel.RH = "";
              }
            }

            if (!(hulp_RH > 0.0) && (hulp_air_temp <= 100.0)) {
              TemperatureModel.RH = "";
            }

          } // if ((hulp_return_obs_age > 0) && (hulp_return_obs_age <= 600))
          // else if (hulp_return_obs_age != AGE_NOT_OK)
          // {
          //   error_info = "sensor data reading ok but air temp and RH part obsolete";
          //   TemperatureModel.air_temp         = "";
          //   TemperatureModel.RH               = "";
          //   TemperatureModel.wet_bulb_temp    = "";
          //   TemperatureModel.double_dew_point = main.INVALID;
          // } // else if (hulp_return_obs_age != AGE_NOT_OK)
          else {
            error_info = "automatically retrieved sensor data reading obsolete or checksum not ok";
            TemperatureModel.air_temp = "";
            TemperatureModel.RH = "";
            TemperatureModel.wet_bulb_temp = "";
            TemperatureModel.double_dew_point = main.INVALID;
          } // else
        } // try
        catch (InterruptedException | ExecutionException ex) {
          error_info = "error retrieving air temp and RH data (" + ex + ")";
          TemperatureModel.air_temp = "";
          TemperatureModel.RH = "";
          TemperatureModel.wet_bulb_temp = "";
          TemperatureModel.double_dew_point = main.INVALID;
        } // catch

        // general error (air temp and RH no value but error_info not yet set)
        if (TemperatureModel.air_temp.equals("")
            && TemperatureModel.RH.equals("")
            && error_info.equals("")) {
          error_info =
              "automatically retrieved air temp and RH data not available or checksum not ok";
          TemperatureModel.double_dew_point = main.INVALID;
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
        } // if (!error_info.equals(""))
        else // no error encountered
        {
          TemperatureModel.airInput.set(TemperatureModel.air_temp); // air temp
          TemperatureModel.wetBulbInput.set(TemperatureModel.wet_bulb_temp); // wet-bulb temp

          // NB default is wet-bulb not frozen (so if no info available then by default the
          // "wet-bulb not frozen" button will be checked)
          if (hulp_wet_bulb_temp < 0.0) {
            TemperatureModel.selectedWetBulbFrozen = true;
          } else {
            TemperatureModel.selectedWetBulbFrozen = false;
          }

          TemperatureModel.humidityInput.set(TemperatureModel.RH); // RH
          TemperatureDialog.sensorUpdated();
        } // else
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
