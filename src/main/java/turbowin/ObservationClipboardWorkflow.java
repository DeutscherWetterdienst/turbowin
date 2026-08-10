package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns clipboard observation validation and dispatch orchestration. */
final class ObservationClipboardWorkflow {

  private ObservationClipboardWorkflow() {}

  static void start(main owner) {
    // TODO add your handling code here:

    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        boolean doorgaan = false;

        // compose coded obs
        String SPATIE = SPATIE_OBS_VIEW; // marker between obs groups
        obs_write = compose_coded_obs(SPATIE);

        // check call sign and date time entered (level 1b checks)
        if (obs_write.compareTo(UNDEFINED) != 0) {
          doorgaan = true;
        } else {
          doorgaan = false;
          // String info = "Call sign, date/time or position not inserted";
          String info = "station ID, date/time or position not inserted";
          JOptionPane.showMessageDialog(
              null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
        } // else

        // level-2 checks
        //
        //
        if (doorgaan == true) {
          doorgaan = support_class.checking_level_2() == true;
        }

        // level-3 checks
        //
        //
        if (doorgaan == true) {
          doorgaan = support_class.checking_level_3() == true;
        }

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
          // if (doorgaan == true)
          // {
          //   Output_obs_to_clipboard();
          // }
          if (doorgaan == true) {
            if (obs_format.equals(FORMAT_FM13)) {
              owner.Output_obs_to_clipboard_FM13();
            } else if (obs_format.equals(FORMAT_101)) {
              owner.Output_obs_to_clipboard_format_101();
            } else {
              String info = "obs format unknown (select: Maintenance -> Obs format setting)";
              JOptionPane.showMessageDialog(
                  null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
              System.out.println(
                  "+++ Not supported obs format in Function: Output_obs_to_clipboard_actionPerformed()");
            } // else
          } // if (doorgaan == true)
        } // try
        catch (InterruptedException | ExecutionException ex) {
          System.out.println(
              "+++ Error in Function: Output_obs_to_clipboard_actionPerformed(). " + ex);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
