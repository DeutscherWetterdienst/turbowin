package turbowin;

import static turbowin.main_RS232_RS422.*;

import javax.swing.SwingWorker;

final class Rs232DashboardValuesComputationWorkflow {

  private Rs232DashboardValuesComputationWorkflow() {}

  static void start(final String date_time_last_update, final String last_update_record) {
    // called from: RS232_read_dasboard_values() [main_RS232_RS422.java]
    //
    // NB only for single barometer on Dashboard
    //
    //
    // NB update the global dasboard var's if ok, otherwise do not reset them but leave the old
    // values unchanged!
    // NB dashboard_double_last_update_record_pressure_ic -> glabal var, link between this function
    // and dashboard dial
    // NB dashboard_string_last_update_record_date_time   -> glabal var, link between this function
    // and dashboard dial
    // NB dashboard_double_last_update_record_ppp         -> glabal var, link between this function
    // and dashboard dial

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        boolean dashboard_double_last_update_record_pressure_ic_ok = false;
        boolean checksum_ok = false;
        String dashboard_string_last_update_record_pressure = "";
        String dashboard_string_last_update_record_ppp = "";
        int pos1 = 0; // position of the first "," in the last record
        int pos2 = 0; // position of the second "," in the last record
        int pos3 = 0; // position of the third "," in the last record

        // System.out.println("--- last_update_record = " + last_update_record);

        if (main.RS232_connection_mode == 1
            || main.RS232_connection_mode == 2
            || main.RS232_connection_mode == 4
            || main.RS232_connection_mode == 5
            || main.RS232_connection_mode == 6
            || main.RS232_connection_mode == 7
            || main.RS232_connection_mode
                == 8) // PTB220, PTB330, Mintaka Duo or Mintaka Star USB or WiFi or Mintaka StarX
        // USB or WiFi
        {
          if (last_update_record.length() > 10) // NB > 10 is a little bit arbitrary number
          {
            // System.out.println("--- record_checksum = " + record_checksum);
            // System.out.println("--- computed_checksum = " + computed_checksum);

            if (main.RS232_connection_mode == 5
                || main.RS232_connection_mode == 6
                || main.RS232_connection_mode == 7
                || main.RS232_connection_mode
                    == 8) // Mintaka Star USB or WiFi 0or Mintaka StarX USB or WiFi
            {
              // Mintaka Star or StarX perform a cheksum check
              String record_checksum =
                  last_update_record.substring(
                      last_update_record.length() - 14,
                      last_update_record.length()
                          - 12); // eg "24" from last_update_record: "1022.20,1022.20,0.80,2, 52
              // 41.9497N,  6 14.1848E,0,0,-1*24"
              String computed_checksum = Mintaka_Star_Checksum(last_update_record);

              checksum_ok = computed_checksum.equals(record_checksum);
            } else if (main.RS232_connection_mode == 1
                || main.RS232_connection_mode == 2
                || main.RS232_connection_mode == 4) // PTB220, PTB330 or Mintaka Duo
            {
              // no cheksum check (Sep 2017) for PTB220, PTB330 and Mintaka Duo. But Mintaka Duo
              // checksum value is available so could be used in the future
              checksum_ok = true;
            }

            if (checksum_ok) {
              if (main.RS232_connection_mode == 4
                  || main.RS232_connection_mode == 5
                  || main.RS232_connection_mode
                      == 6) // Mintaka Duo, Mintaka Star USB, Mintaka Star WiFi
              {
                // NB pressure and ppp pos in the saved records is the same for Duo, Star and StarX
                pos1 =
                    last_update_record.indexOf(",", 0); // position of the first "," in the record
                pos2 =
                    last_update_record.indexOf(
                        ",", pos1 + 1); // position of the second "," in the last record
                pos3 =
                    last_update_record.indexOf(
                        ",", pos2 + 1); // position of the third "," in the last record

                dashboard_string_last_update_record_pressure =
                    last_update_record.substring(0, pos1);
                dashboard_string_last_update_record_ppp =
                    last_update_record.substring(pos2 + 1, pos3);
              } else if (main.RS232_connection_mode == 7
                  || main.RS232_connection_mode == 8) // Mintaka StarX USB or Mintaka StarX WiFi
              {
                // NB pressure and ppp pos in the saved records is the same for Duo, Star and StarX
                // StarX example: // 1009.73,1007.73,0.00,0, 52 41.9491N,  6
                // 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
                Rs232DashboardRecordParser.Result parsedRecord =
                    Rs232DashboardRecordParser.parseStarX(last_update_record);
                if (parsedRecord != null) {
                  dashboard_string_last_update_record_pressure = parsedRecord.pressure;
                  dashboard_string_last_update_record_ppp = parsedRecord.pressureChange;
                  main.sensor_data_record_obs_pressure = parsedRecord.observationPressure;
                } else {
                  System.out.println(
                      "--- RS232_compute_dasboard_values() malformed StarX record");
                }
                /*
                pos1 =
                    last_update_record.indexOf(
                        ",", 0); // ML hereafter; position of the first "," in the last record
                pos2 =
                    last_update_record.indexOf(
                        ",",
                        pos1 + 1); // ppp hereafter; position of the second "," in the last record
                pos3 =
                    last_update_record.indexOf(
                        ",", pos2 + 1); // a hereafter; position of the third "," in the last record
                pos4 =
                    last_update_record.indexOf(",", pos3 + 1); // lat hereafter; position of the 4th "," in the last record
                */
              } else if (main.RS232_connection_mode == 1
                  || main.RS232_connection_mode == 2) // PTB220 or PTB330
              {
                int type_record_pressure_begin_pos = 0;
                int type_record_ppp_begin_pos = 0;
                // int type_record_a_begin_pos            = 0;

                if (main.RS232_connection_mode == 1) // PTB220
                {
                  type_record_pressure_begin_pos = main.RECORD_P_BEGIN_POS_PTB220;
                  type_record_ppp_begin_pos = main.RECORD_ppp_BEGIN_POS_PTB220;
                  // type_record_a_begin_pos          = main.RECORD_a_BEGIN_POS_PTB220;
                } else if (main.RS232_connection_mode == 2) // PTB330
                {
                  type_record_pressure_begin_pos = main.RECORD_P_BEGIN_POS_PTB330;
                  type_record_ppp_begin_pos = main.RECORD_ppp_BEGIN_POS_PTB330;
                  // type_record_a_begin_pos            = main.RECORD_a_BEGIN_POS_PTB330;
                }

                dashboard_string_last_update_record_pressure =
                    last_update_record.substring(
                        type_record_pressure_begin_pos, type_record_pressure_begin_pos + 7);
                dashboard_string_last_update_record_ppp =
                    last_update_record.substring(
                        type_record_ppp_begin_pos, type_record_ppp_begin_pos + 5);

                // System.out.println("+++ dashboard_string_last_update_record_pressure = " +
                // dashboard_string_last_update_record_pressure);

                // String sensor_data_record_obs_a              =
                // last_update_record.substring(type_record_a_begin_pos, type_record_a_begin_pos +
                // 1);
              } // else if (main.RS232_connection_mode == 1 || main.RS232_connection_mode == 2)

              if ((dashboard_string_last_update_record_pressure.equals("") == false)
                  && (dashboard_string_last_update_record_pressure != null)) {
                // pressure at sensor height + apply ic
                try {
                  Double.parseDouble(dashboard_string_last_update_record_pressure);
                } catch (NumberFormatException exception) {
                  System.out.println(
                      "--- " + "Function RS232_compute_dasboard_values() " + exception);
                }
                Double dashboardPressure =
                    DashboardPressureCalculator.calculate(
                        dashboard_string_last_update_record_pressure,
                        main.barometer_instrument_correction);
                if (dashboardPressure != null) {
                  dashboard_double_last_update_record_pressure_ic = dashboardPressure;
                  dashboard_double_last_update_record_pressure_ic_ok = true;
                }

                // only continue if the retrieved pressure was ok
                //
                if (dashboard_double_last_update_record_pressure_ic_ok == true) {
                  // date time last update (retrieved pressure was ok, now we can update the
                  // date-time of the last update var)
                  dashboard_string_last_update_record_date_time = date_time_last_update;
                }

                // ppp: only continue if the retrieved pressure was ok + there was at least 1 char
                // for the ppp
                //
                if (dashboard_double_last_update_record_pressure_ic_ok == true
                    && dashboard_string_last_update_record_ppp.length() >= 1
                    && dashboard_string_last_update_record_ppp.indexOf("*") == -1) {
                  // pressure change (ppp; pressure change during last 3 hours)
                  // so pressure was ok, if the ppp is not ok make it invalid, pressure is leading!
                  try {
                    Double.parseDouble(dashboard_string_last_update_record_ppp);
                  } catch (NumberFormatException exception) {
                    System.out.println(
                        "--- " + "Function RS232_compute_dasboard_values() " + exception);
                  }
                  dashboard_double_last_update_record_ppp =
                      DashboardPressureChangeCalculator.calculate(
                          dashboard_string_last_update_record_ppp);
                } // if (dashboard_double_last_update_record_pressure_ic_ok == true)
              } // if ((dashboard_string_last_update_record_pressure.equals("") == false) &&
              // (dashboard_string_last_update_record_pressure != null))
              else {
                // do nothing!!
                // the previuous "dashboard" values are still valid! (but will not updated by this
                // "last_update_record")
                System.out.println(
                    "+++ Function RS232_compute_dasboard_values(): dashboard_string_last_update_record_pressure = null/empty");
              }
            } // if (computed_checksum.equals(record_checksum))
            else {
              // do nothing!!
              // the previuous "dashboard" values are still valid! (but will not updated by this
              // "last_update_record")
              System.out.println("+++ Function RS232_compute_dasboard_values(): cheksum not ok");
            }
          } // if (last_update_record.length() > 10)
          else {
            // do nothing!!
            // the previuous "dashboard" values are still valid! (but will not be updated by this
            // "last_update_record")
            System.out.println(
                "+++ Function RS232_compute_dasboard_values(): last_update_record.length() <= 10 or not present");
          }
        } // if (main.RS232_connection_mode == 1 || etc

        return null;
      } // protected Void doInBackground() throws Exception
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
