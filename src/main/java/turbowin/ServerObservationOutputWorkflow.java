package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns observation validation and dispatch for server output. */
final class ServerObservationOutputWorkflow {

  private ServerObservationOutputWorkflow() {}

  static void start(main owner) {
    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        boolean doorgaan = false;

        String SPATIE = SPATIE_OBS_SERVER; // use "_" as marker between obs groups
        obs_write = compose_coded_obs(SPATIE);

        if (obs_write.compareTo(UNDEFINED) != 0) {
          doorgaan = true;
        } else {
          String info = "station ID, date/time or position not inserted";
          JOptionPane.showMessageDialog(
              null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
        }

        if (doorgaan == true) {
          if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2) {
            doorgaan = false;
            String info =
                "logs folder unknown, select: Maintenance -> Log files settings and retry";
            JOptionPane.showMessageDialog(
                null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          }
        }

        if (doorgaan == true) {
          if ((obs_format.equals(FORMAT_101)) && (upload_URL.equals("") || upload_URL == null)) {
            doorgaan = false;
            String info = "upload URL unknown, select: Maintenance -> Server settings and retry";
            JOptionPane.showMessageDialog(
                null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          }
        }

        if (doorgaan == true) {
          owner.bepaal_last_record_uit_immt();
          doorgaan = support_class.position_sequence_check();

          if (doorgaan) {
            doorgaan = support_class.Check_Land_Sea_Mask();
          }
        }

        if (doorgaan == true && obs_format.equals(FORMAT_101)) {
          // NB although "obs_write = compose_coded_obs(SPATIE);" (makes FM13 record) see above
          // is not necessary for the compressed obs
          // it is useful because parts of it will be used for checking position etc.
          format_101_class = new FORMAT_101();
          format_101_class.compress_and_decompress_101_control_center();
        }

        return doorgaan;
      }

      @Override
      protected void done() {
        try {
          boolean doorgaan = get();
          if (doorgaan == true) {
            if (obs_format.equals(FORMAT_FM13)) {
              if (offline_mode == false) {
                String info = "TurboWeb is disabled";
                JOptionPane.showMessageDialog(
                    null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
              } else {
                owner.Output_obs_to_server_FM13_TurboWin_stand_alone();
              }
            } else if (obs_format.equals(FORMAT_101)) {
              owner.Output_obs_to_server_format_101_V2();
            } else {
              String info = "obs format unknown (select: Maintenance -> Obs format setting)";
              JOptionPane.showMessageDialog(
                  null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
              System.out.println(
                  "+++ Not supported obs format in Function: Output_Obs_to_server_menu_actionPerformed()");
            }
          }
        } catch (InterruptedException | ExecutionException ex) {
          System.out.println(
              "+++ Error in Function: Output_Obs_to_server_menu_actionPerformed(). " + ex);
        }
      }
    }.execute();
  }
}
