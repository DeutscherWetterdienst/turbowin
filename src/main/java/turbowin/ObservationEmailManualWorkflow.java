package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns manual observation email preparation and dispatch orchestration. */
final class ObservationEmailManualWorkflow {

  private ObservationEmailManualWorkflow() {}

  static void start(main owner) {

    // called from:
    //    - Output_obs_by_email_default_actionPerformed()
    //    - Output_obs_by_email_local_host_actionPerformed()  <-- deprecated
    //    - Output_obs_by_email_Gmail_actionPerformed()       <-- deprecated
    //    - Output_obs_by_email_Yahoo_actionPerformed()       <-- deprecated
    //    - Output_obs_by_email_Custom_actionPerformed()
    //

    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        boolean doorgaan = false;

        // compose coded obs
        String SPATIE = SPATIE_OBS_VIEW; // use " " as marker between obs groeps
        obs_write =
            compose_coded_obs(
                SPATIE); // returns UNDEFINED if call sign, position or date/tome not inserted

        // check Obs E-mail recipient address was added
        if (obs_email_recipient.compareTo("") != 0) {
          doorgaan = true;
        } else {
          doorgaan = false;

          String info =
              "Email address recipient not inserted (Select: Maintenance -> Email settings)";
          JOptionPane.showMessageDialog(
              null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
        } // else

        // check obs must contain at least station ID, date/time and position
        if (doorgaan == true) {
          if (obs_write.compareTo(UNDEFINED) != 0) {
            doorgaan = true;
          } else {
            doorgaan = false;
            // String info = "Call sign, date/time or position not inserted";
            String info = "station ID, date/time or position not inserted";
            JOptionPane.showMessageDialog(
                null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          } // else
        } // if (doorgaan == true)

        // check log dir known
        if (doorgaan == true) {
          if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2) {
            doorgaan = false;
            String info =
                "logs folder unknown, select: Maintenance -> Log files settings and retry";
            JOptionPane.showMessageDialog(
                null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          }
        } // if (doorgaan == true)

        // position + time sequence checks
        if (doorgaan == true) {
          owner.bepaal_last_record_uit_immt();
          doorgaan = support_class.position_sequence_check();

          if (doorgaan) {
            doorgaan = support_class.Check_Land_Sea_Mask();
          }
        } // if (doorgaan == true)

        // if appropriate make a format 101 obs
        if (doorgaan == true) {
          if (obs_format.equals(FORMAT_101)) {
            // NB although "obs_write = compose_coded_obs(SPATIE);" (makes FM13 record)  see above
            // is not necessary for the compressed obs
            //    it is useful because parts of it will be used for checking position etc.

            format_101_class = new FORMAT_101();
            format_101_class.compress_and_decompress_101_control_center();
          } // if (obs_format.equals(FORMAT_101))
        } // if (doorgaan == true)

        return doorgaan;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          boolean doorgaan = get();
          if (doorgaan == true) {
            if (obs_format.equals(FORMAT_FM13)) {
              if (email_send_mode.equals(EMAIL_SEND_DEFAULT)) {
                owner.Output_obs_by_email_FM13(); // default email app via desktop
              } else {
                boolean manual_send = true;

                if (custom_email_module.equals(PYTHON_EMAIL)) {
                  Output_obs_by_email_Localhost_Gmail_Yahoo_FM13_format_101(
                      manual_send); // via python email app

                } else // primary email module
                {
                  Output_obs_by_email_jakarta_FM13_format_101(
                      manual_send); // via jakarta email module
                }
              }
            } else if (obs_format.equals(FORMAT_101)) {
              if (email_send_mode.equals(EMAIL_SEND_DEFAULT)) {
                owner.Output_obs_by_email_format_101(); // default email app via desktop
              } else {
                boolean manual_send = true;

                if (custom_email_module.equals(PYTHON_EMAIL)) {
                  Output_obs_by_email_Localhost_Gmail_Yahoo_FM13_format_101(
                      manual_send); // via python email app

                } else {
                  Output_obs_by_email_jakarta_FM13_format_101(
                      manual_send); // via jakarta email module
                }
              }
            } else {
              String info = "obs format unknown (select: Maintenance -> Obs format setting)";
              JOptionPane.showMessageDialog(
                  null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
              System.out.println(
                  "+++ Not supported obs format in Function: Output_obs_by_email_all_manual()");
            } // else
          } // if (doorgaan == true)
        } // try
        catch (InterruptedException | ExecutionException ex) {
          System.out.println("+++ Error in Function: Output_obs_by_email_all_manual() " + ex);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
