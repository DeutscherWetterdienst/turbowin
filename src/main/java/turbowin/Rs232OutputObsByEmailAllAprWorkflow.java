package turbowin;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

final class Rs232OutputObsByEmailAllAprWorkflow {

  private Rs232OutputObsByEmailAllAprWorkflow() {}

  static void start() {
    // called from: - RS232_APR_AWSR_send()

    String log_tag;
    if (main.AWSR == true) {
      log_tag = "[AWSR]";
    } else {
      log_tag = "[APR]";
    }

    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        boolean doorgaan = false;

        // compose coded obs [obs_write = FM13 obs !!!]
        String SPATIE = main.SPATIE_OBS_VIEW; // use " " as marker between obs groeps
        main.obs_write =
            main.compose_coded_obs(
                SPATIE); // returns UNDEFINED if call sign, position or date/time not inserted

        // check Obs E-mail recipient address was added
        if (main.obs_email_recipient.compareTo("") != 0) {
          doorgaan = true;
        } else {
          doorgaan = false;

          String info =
              "Email address recipient not inserted (Select: Maintenance -> Email settings)";
          main.log_turbowin_system_message(log_tag + " " + info);
        } // else

        // check obs must contain at least call sign, date/time and position
        if (doorgaan == true) {
          if (main.obs_write.compareTo(main.UNDEFINED) != 0) {
            doorgaan = true;
          } else {
            doorgaan = false;
            String info = "Call sign, date/time or position not present";
            main.log_turbowin_system_message(log_tag + " " + info);
          } // else
        } // if (doorgaan == true)

        // check log dir known
        if (doorgaan == true) {
          if (main.logs_dir.trim().equals("") == true || main.logs_dir.trim().length() < 2) {
            doorgaan = false;
            String info =
                "logs folder unknown, select: 'Maintenance -> Log files settings' and retry";
            main.log_turbowin_system_message(log_tag + " " + info);
          }
        } // if (doorgaan == true)

        // position + time sequence checks
        // if (doorgaan == true)
        // {
        //   bepaal_last_record_uit_immt();
        //   doorgaan = position_sequence_check();
        //
        //   if (doorgaan)
        //   {
        //      doorgaan = Check_Land_Sea_Mask();
        //   }
        // } // if (doorgaan == true)

        // if appropriate make a format 101 obs
        if (doorgaan == true) {
          if (main.obs_format.equals(main.FORMAT_101)
              || (main.obs_format.equals(main.FORMAT_AWS)
                  && main.eucaws_uploads_method.equals(
                      main.UPLOADS_VIA_TURBOWIN))) // in case AWS (EUCAWS) format: in combination
          // with uploads via turbowin (and not via eucaws
          // own iridium modem)
          {
            // NB although "obs_write = compose_coded_obs(SPATIE);" (makes FM13 record)  see above
            // is not necessary for the compressed obs
            //    it is useful because parts of it will be used for checking position etc.

            main.format_101_class = new FORMAT_101();
            main.format_101_class.compress_and_decompress_101_control_center();
          } // if (obs_format.equals(FORMAT_101) etc.)
        } // if (doorgaan == true)

        return doorgaan;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          boolean doorgaan = get();
          if (doorgaan == true) {
            if (main.obs_format.equals(main.FORMAT_FM13)) {
              boolean manual_send = false;
              main.Output_obs_by_email_jakarta_FM13_format_101(manual_send);
            } else if (main.obs_format.equals(main.FORMAT_101)
                || (main.obs_format.equals(main.FORMAT_AWS)
                    && main.eucaws_uploads_method.equals(
                        main.UPLOADS_VIA_TURBOWIN))) // in case AWS (EUCAWS) format: in combination
            // with uploads via turbowin (and not via
            // eucaws own iridium modem)
            {
              boolean manual_send = false;
              main.Output_obs_by_email_jakarta_FM13_format_101(manual_send);
            } else {
              String info = "obs format unknown (select: Maintenance -> Obs format setting)";
              main.log_turbowin_system_message(log_tag + " " + info);

              System.out.println(
                  "+++ Not supported obs format in Function: RS232_Output_obs_by_email_all_APR()");
            } // else
          } // if (doorgaan == true)
        } // try
        catch (InterruptedException | ExecutionException ex) {
          System.out.println("+++ Error in Function: Output_obs_by_email_all_APR() " + ex);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
