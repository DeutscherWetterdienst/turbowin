package turbowin;

import static turbowin.main.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous duplicate checking and writing of IMMT observation records. */
final class ImmtLogWriterWorkflow {

  private ImmtLogWriterWorkflow() {}

  static void start(String immt_rec) {
    boolean doorgaan = true;

    if (main.logs_dir.equals("") == true) {
      JOptionPane.showMessageDialog(
          null,
          "logs folder unknown, please select: Maintenance -> Log files settings",
          main.APPLICATION_NAME + " error",
          JOptionPane.WARNING_MESSAGE);
      doorgaan = false;
    }

    if (doorgaan == true) {
      new SwingWorker<Integer, Void>() {
        @Override
        protected Integer doInBackground() throws Exception {
          Integer return_code = -1;
          boolean huidige_rec_ok = true;
          String datum_tijd_positie_last_record = "";
          String datum_tijd_positie_huidige_record = "";

          // Prevent storing an observation twice: characters 1 through 18 contain the
          // date/time/position key used for comparison.
          if (last_record.length() >= 19) {
            datum_tijd_positie_last_record = last_record.substring(1, 19);
          }

          if (immt_rec.length() >= 19) {
            datum_tijd_positie_huidige_record = immt_rec.substring(1, 19);
            // Check the year from the current IMMT record's date/time/position key.
            String str_year = datum_tijd_positie_huidige_record.substring(1, 5);
            try {
              int int_year = Integer.parseInt(str_year);
              if (int_year >= 2000 && int_year <= 2099) {
                huidige_rec_ok = true;
              }
            } catch (NumberFormatException e) {
              huidige_rec_ok = false;
            }
          } else {
            // Do not store records without date/time and position.
            huidige_rec_ok = false;
          }

          if ((datum_tijd_positie_last_record.compareTo(datum_tijd_positie_huidige_record) != 0)
              && (huidige_rec_ok == true)) {
            String volledig_path = main.logs_dir + File.separator + IMMT_LOG;

            try (BufferedWriter out = new BufferedWriter(new FileWriter(volledig_path, true))) {
              out.write(immt_rec);
              out.newLine();
              return_code = 0;
            } catch (IOException e) {
              return_code = 1;
            }
          } else {
            return_code = 2;
          }

          return return_code;
        }

        @Override
        protected void done() {
          String message = "";
          try {
            Integer return_code = get();

            // Status -1 means that nothing was done, so it is intentionally silent.
            if (return_code == 0) {
              message = "appended immt.log successfully";
            } else if (return_code == 1) {
              String volledig_path_immt = main.logs_dir + File.separator + IMMT_LOG;
              message = "unable to write to: " + volledig_path_immt;
            }
            // Status 2 means a duplicate IMMT record and is intentionally silent.
          } catch (InterruptedException | ExecutionException ex) {
            message = "exception when writing immt.log (" + ex + ")";
          }

          if (message.equals("") == false) {
            if (APR == true || AWSR == true) {
              main.log_turbowin_system_message("[IMMT] " + message);
            } else if (message.contains("successfully")) {
              // Successful manual writes go only to the system log, not a popup.
              main.log_turbowin_system_message("[IMMT] " + message);
            } else {
              JOptionPane.showMessageDialog(
                  null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
          }
        }
      }.execute();
    }
  }
}
