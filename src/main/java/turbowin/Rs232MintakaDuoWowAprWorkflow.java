package turbowin;

import static turbowin.RS232_mintaka.*;
import static turbowin.main_RS232_RS422.RS232_APR_AWSR_send;
import static turbowin.main_RS232_RS422.RS232_WOW_APR_compute_air_pressure_height_correction;
import static turbowin.main_RS232_RS422.RS232_make_pressure_APR_FM13_IMMT_ready;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

final class Rs232MintakaDuoWowAprWorkflow {

  private Rs232MintakaDuoWowAprWorkflow() {}

  static void start(final String destination, final boolean retry) {

    // NB 'destination' possible strings: - WOW
    //                                    - APR
    //                                    - MAIN_SCREEN

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

        // initialisation
        String sensor_data_record_WOW_APR_pressure_hpa = "";

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
            MintakaDuoRecord parsedRecord = null;
            if (laatste_record != null && laatste_record.length() <= 15) {
              laatste_record = null;
              System.out.println(
                  "--- Mintaka Duo reading data for WOW/APR, format (min. record length) last retrieved record NOT ok (file: "
                      + volledig_path_sensor_data
                      + ")");
            }
            if (laatste_record != null
                && !MintakaDuoRecordParser.hasExpectedCommaCount(laatste_record)) {
              laatste_record = null;
              System.out.println(
                  "--- Mintaka Duo reading data for WOW/APR, format (number commas) last retrieved record NOT ok (file: "
                      + volledig_path_sensor_data
                      + ")");
            }
            if (laatste_record != null) {
              parsedRecord = MintakaDuoRecordParser.parse(laatste_record);
            }

            // last retrieved record ok
            //
            if (laatste_record != null) {
              // System.out.println("+++ Mintaka Duo, last retrieved record = " + laatste_record);

              // String record_datum_tijd_minuten =
              // laatste_record.substring(main.type_record_datum_tijd_begin_pos,
              // main.type_record_datum_tijd_begin_pos + 12);  // bv 201302201345

              Date file_date = main.sdf4.parse(parsedRecord.timestamp);
              long system_sec = System.currentTimeMillis();
              long timeDiff =
                  Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

              if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA) // max 5/10 minutes old
              {
                // retrieved record example Mintaka Duo: 1021.89,1021.89,1.70,1*05 201502111405
                sensor_data_record_WOW_APR_pressure_hpa = parsedRecord.pressure;

                // rounding eg: 998.19 -> 998.2
                //        double digitale_sensor_waarde =
                // Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;
                // // bv 998.19 -> 998.2

                if (destination.equals("MAIN_SCREEN") == false) {
                  System.out.println(
                      "--- sensor data record, raw uncorrected pressure: "
                          + sensor_data_record_WOW_APR_pressure_hpa);
                }
              } // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
              else {
                sensor_data_record_WOW_APR_pressure_hpa = "";
                System.out.println(
                    "--- automatically retrieved barometer reading for WOW/APR obsolete");

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message =
                      "[WOW/APR] automatically retrieved barometer reading for WOW/APR obsolete ("
                          + timeDiff
                          + " minutes old)";
                  main.log_turbowin_system_message(message);
                }
              }
            } // if (laatste_record != null)

          } // try
          catch (IOException ex) {
            System.out.println(
                "--- Function RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR(): " + ex);
          }
        } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)

        // clear memory
        main.obs_file_datum_tijd = null;

        // HMP155 connected? (2nd meteo instrument)
        //
        if (main.RS232_connection_mode_II == 1) {
          RS232_vaisala
              .RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument(
                  destination);
        }

        return sensor_data_record_WOW_APR_pressure_hpa;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        if (destination.equals("WOW")) {
          JOptionPane.showMessageDialog(
              null,
              "WOW in TurboWin+ is disabled",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
        } // if (destination.equals("WOW"))
        else if (destination.equals("APR")
            || destination.equals("MAIN_SCREEN")) // APR = Automated Pressure Reporting
        {
          String sensor_data_record_APR_pressure_MSL_hpa = "";
          String sensor_data_record_APR_pressure_hpa = "";

          try {
            sensor_data_record_APR_pressure_hpa =
                get(); // get the return value of the doInBackground()
          } catch (InterruptedException | ExecutionException ex) {
            if (destination.equals("MAIN_SCREEN") == false) {
              String message = "[APR] " + ex.toString();
              main.log_turbowin_system_message(message);
            }
            sensor_data_record_APR_pressure_hpa = "";
          }

          if ((sensor_data_record_APR_pressure_hpa.compareTo("") != 0)) {
            double hulp_double_APR_pressure_reading;
            try {
              hulp_double_APR_pressure_reading =
                  Double.parseDouble(sensor_data_record_APR_pressure_hpa.trim());
              // hulp_double_pressure_reading = Math.round(hulp_double_pressure_reading * 10) /
              // 10.0d;  // bv 998.19 -> 998.2
            } catch (NumberFormatException e) {
              System.out.println(
                  "--- " + "Function RS232_MintakaDuo_Read_Sensor_Data_For_WOW_APR() " + e);
              hulp_double_APR_pressure_reading = Double.MAX_VALUE;
            }

            if ((hulp_double_APR_pressure_reading > 900.0)
                && (hulp_double_APR_pressure_reading < 1100.0)) {
              DecimalFormat df = new DecimalFormat("0.0"); // rounding only 1 decimal

              // CONVERT TO MSL (+ apply barometer instrument correction)
              double APR_height_correction_pressure =
                  RS232_WOW_APR_compute_air_pressure_height_correction(
                      hulp_double_APR_pressure_reading);

              if (destination.equals("MAIN_SCREEN") == false) {
                String message_b =
                    "[APR] air pressure at sensor height = "
                        + sensor_data_record_APR_pressure_hpa
                        + " hPa";
                main.log_turbowin_system_message(message_b);
                String message_hc =
                    "[APR] air pressure height correction = "
                        + Double.toString(APR_height_correction_pressure)
                        + " hPa";
                main.log_turbowin_system_message(message_hc);
                String message_ic =
                    "[APR] air pressure instrument correction = "
                        + main.barometer_instrument_correction
                        + " hPa";
                main.log_turbowin_system_message(message_ic);
              }

              if (APR_height_correction_pressure > -50.0 && APR_height_correction_pressure < 50.0) {

                // ic correction (make it 0.0 if outside the range)
                double double_barometer_instrument_correction = 0.0;

                if (main.barometer_instrument_correction.equals("") == false) {
                  try {
                    double_barometer_instrument_correction =
                        Double.parseDouble(main.barometer_instrument_correction);

                    if (!(double_barometer_instrument_correction >= -4.0
                        && double_barometer_instrument_correction <= 4.0)) {
                      double_barometer_instrument_correction = 0.0;
                    }
                  } catch (NumberFormatException e) {
                    double_barometer_instrument_correction = 0.0;
                  }
                }

                // DecimalFormat df = new DecimalFormat("0.0");           // rounding only 1 decimal

                ////////////////// barometer reading (pressure at sensor height + ic)
                // ////////////////
                //
                String sensor_data_record_APR_pressure_reading_hpa_corrected =
                    df.format(
                        hulp_double_APR_pressure_reading + double_barometer_instrument_correction);
                mybarometer.pressure_reading_corrected =
                    sensor_data_record_APR_pressure_reading_hpa_corrected; // now pressure at sensor
                // height corrected for
                // ic

                ///////////////// pressure at MSL (+ ic) ///////////
                //
                // sensor_data_record_APR_pressure_MSL_hpa =
                // df.format(hulp_double_APR_pressure_reading + APR_height_correction_pressure +
                // double_barometer_instrument_correction);
                double hulp_double_APR_pressure_MSL_not_rounded =
                    hulp_double_APR_pressure_reading
                        + APR_height_correction_pressure
                        + double_barometer_instrument_correction;
                BigDecimal bd =
                    new BigDecimal(hulp_double_APR_pressure_MSL_not_rounded)
                        .setScale(2, RoundingMode.HALF_UP);
                double hulp_double_APR_pressure_MSL_rounded = bd.doubleValue();
                sensor_data_record_APR_pressure_MSL_hpa =
                    Double.toString(hulp_double_APR_pressure_MSL_rounded);
                mybarometer.pressure_msl_corrected =
                    sensor_data_record_APR_pressure_MSL_hpa; // sensor_data_record_APR_pressure_MSL_hpa the baromter instrument correction is included

                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_msl =
                      "[APR] air pressure MSL = " + mybarometer.pressure_msl_corrected + " hPa";
                  main.log_turbowin_system_message(message_msl);
                }

                // make IMMT (and FM13) ready
                if (destination.equals("MAIN_SCREEN") == false) {
                  RS232_make_pressure_APR_FM13_IMMT_ready();
                }

                // send the data
                //
                if (destination.equals("MAIN_SCREEN") == false) {
                  RS232_APR_AWSR_send(retry);
                }

                // update the barometer fields on main screen
                //
                if (destination.equals("MAIN_SCREEN") == true) {
                  main.barometer_fields_update();
                }
              } else {
                if (destination.equals("MAIN_SCREEN") == false) {
                  String message_hce =
                      "[APR] computed height correction pressure not ok ("
                          + APR_height_correction_pressure
                          + ")";
                  main.log_turbowin_system_message(message_hce);
                }
                sensor_data_record_APR_pressure_MSL_hpa = "";
              }
            } // if ((hulp_double_APR_pressure_reading > 900.0) && (hulp_double_APR_pressure_reading
            // < 1100.0))
            else {
              if (destination.equals("MAIN_SCREEN") == false) {
                main.log_turbowin_system_message(
                    "[APR] automatically retrieved barometer reading outside range");
              }
              sensor_data_record_APR_pressure_MSL_hpa = "";
            }
          } //  if ((sensor_data_record_APR_pressure_hpa.compareTo("") != 0))
          else {
            if (destination.equals("MAIN_SCREEN") == false) {
              main.log_turbowin_system_message(
                  "[APR] automatically retrieved barometer reading obsolete or error during retrieving data");
            }
            sensor_data_record_APR_pressure_MSL_hpa = "";
          }
        } // else if (destination.equals("APR") || destination.equals("MAIN_SCREEN"))
      } // protected void done()
    }.execute(); // new SwingWorker <Void, Void>()
  }
}
