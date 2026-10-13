package turbowin;

import static turbowin.main_RS232_RS422.*;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

final class Rs232GpsNmeaCogSogWorkflow {

  private Rs232GpsNmeaCogSogWorkflow() {}

  static void start(String mode, double[] positions) {
    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        boolean COG_SOG_ok = true;

        // calculate COG and SOG (and course and speed for main screen)
        if ((positions[0] < Double.MAX_VALUE - 1)
            && (positions[1] < Double.MAX_VALUE - 1)
            && (positions[2] < Double.MAX_VALUE - 1)
            && (positions[3] < Double.MAX_VALUE - 1)) {
          // NB computing COG and SOG happens every minute in UPDATE_MINUTE mode
          RS232_compute_APR_COG_SOG(positions[0], positions[1], positions[2], positions[3]);

          // publish course and speed on main screen (indirect via myposition.course and
          // myposition.speed)
          if ((myposition.COG_APR >= 0.0 && myposition.COG_APR <= 360.0)
              && (myposition.SOG_APR >= 0.0 && myposition.SOG_APR <= 100.0)) {
            // NB myposition.course and myposition.speed only for main screen
            //    see position_fields_update()[main.java] called by
            // RS232_GPS_NMEA_0183_init_new_data_received_check_timer() [RS232_RS422.java]
            myposition.course = Double.toString(myposition.COG_APR);
            myposition.speed = Double.toString(myposition.SOG_APR);
          } else {
            // reset the course ansd speed vars
            myposition.COG_APR = Double.MAX_VALUE;
            myposition.SOG_APR = Double.MAX_VALUE;
            myposition.course = "";
            myposition.speed = "";
            myposition.Ds_code = ""; // from version 4.2
            myposition.vs_code = ""; // from version 4.2
            COG_SOG_ok = false;
          } // else
        } // if ( (positions[0] < Double.MAX_VALUE -1) etc.
        else {
          // reset the course ansd speed vars
          myposition.COG_APR = Double.MAX_VALUE;
          myposition.SOG_APR = Double.MAX_VALUE;
          myposition.course = "";
          myposition.speed = "";
          myposition.Ds_code = ""; // from version 4.2
          myposition.vs_code = ""; // from version 4.2
          COG_SOG_ok = false;
        } // else

        return COG_SOG_ok;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          boolean COG_SOG_result = get();

          // only in APR mode (1, 3, 6 hours) or manual mode, otherwise every minute message lines
          // in the log
          if (mode.equals("APR") || mode.equals("MANUAL")) {
            if (COG_SOG_result == true) {
              if (main.obs_format.equals(main.FORMAT_101)) {
                main.log_turbowin_system_message(
                    "[GPS] COG and SOG calculation [10 min] ok; "
                        + myposition.COG_APR
                        + "\u00B0 "
                        + myposition.SOG_APR
                        + " kts");
              } else if (main.obs_format.equals(main.FORMAT_FM13)) {
                main.log_turbowin_system_message(
                    "[GPS] COG and SOG calculation [3 hrs] ok; "
                        + myposition.COG_APR
                        + "\u00B0 "
                        + myposition.SOG_APR
                        + " kts");
              }

              // main.log_turbowin_system_message("[GPS] COG and SOG calculation ok; " +
              // myposition.COG_APR + "\u00B0 " + myposition.SOG_APR + " kts");
            } else {
              if (main.obs_format.equals(main.FORMAT_101)) {
                main.log_turbowin_system_message(
                    "[GPS] COG and SOG calculation [10 min] error or start/end position not (yet) available");
              } else if (main.obs_format.equals(main.FORMAT_FM13)) {
                main.log_turbowin_system_message(
                    "[GPS] COG and SOG calculation [3 hrs] error or start/end position not (yet) available");
              }
              // main.log_turbowin_system_message("[GPS] COG and SOG calculation error or
              // start/end position not (yet) available");
            }
          } // if (mode.equals("APR") || mode.equals("MANUAL"))
        } // try
        catch (InterruptedException | ExecutionException ex) {
          System.out.println("+++ Error in Function: RS232_GPS_NMEA_0183_Date_Position_Parsing()");
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
