package turbowin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.swing.SwingWorker;

/** Owns asynchronous reading and display of the current TurboWin system log. */
final class SystemLogDisplayWorkflow {

  private SystemLogDisplayWorkflow() {}

  static void start(mysystem_log owner) {
    final String newline = System.getProperty("line.separator");

    new SwingWorker<String, String>() {
      @Override
      protected String doInBackground() throws Exception {
        String file_line = null;
        int teller1 = 0;
        int teller2 = -1;
        boolean doorgaan = true;

        String file_naam = "turbowin_system_" + main.sdf_tsl_1.format(new Date()) + ".txt";
        String volledig_path_turbowin_system_logs =
            main.logs_dir
                + java.io.File.separator
                + main.TURBOWIN_SYSTEM_LOGS_DIR
                + java.io.File.separator
                + file_naam;

        // extend title with full path of the system log txt file (can be used to point the observer
        // to the file for eg forwarding to a Met Centre in case of a problems)
        owner.setTitle("TurboWin+ system log [" + volledig_path_turbowin_system_logs + "]");

        try (BufferedReader in =
            new BufferedReader(
                new FileReader(volledig_path_turbowin_system_logs))) // try with resources
        {
          while ((in.readLine()) != null) {
            teller1++;
          }
        } // try
        catch (FileNotFoundException ex) {
          String info =
              "[GENERAL] reading error 'TurboWin+ system log' file or no TurboWin+ events logged for the current month";
          main.log_turbowin_system_message(info);
          doorgaan = false;
        } catch (IOException ex) {
          String info = "[GENERAL] error 'TurboWin+ system log' file (" + ex + ")";
          main.log_turbowin_system_message(info);
          doorgaan = false;
        } // catch
        System.out.println("--- system log number of records: " + teller1);

        if (doorgaan) {
          // Display only the most recent 5000 records; the historical comment said "last 1000".
          if (teller1 > 5000) {
            teller2 = (teller1 - 5000) * -1;
          } else {
            teller2 = -1;
          }
          System.out.println(
              "--- system log displaying from record number: " + Math.abs(teller2 + 1));

          try (BufferedReader in2 =
              new BufferedReader(
                  new FileReader(volledig_path_turbowin_system_logs))) // try with resources
          {
            while ((file_line = in2.readLine()) != null) {
              teller2++;
              if (teller2 >= 0) {
                publish(new String[] {file_line});
              }
            }
          } catch (FileNotFoundException ex) {
            String info =
                "[GENERAL] reading error 'TurboWin+ system log' file or no TurboWin+ events logged for the current month";
            main.log_turbowin_system_message(info);
            doorgaan = false;
          } catch (IOException ex) {
            String info = "[GENERAL] error 'TurboWin+ system log' file (" + ex + ")";
            main.log_turbowin_system_message(info);
            doorgaan = false;
          } // catch
        } // if (doorgaan)

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void process(List<String> data) {
        // process() runs on the Event Dispatch Thread, so it is safe to update the Swing text area
        // through appendSystemLogLine(). SwingWorker may deliver multiple published lines as a
        // batch.
        for (String received_line : data) {
          owner.appendSystemLogLine(received_line, newline);
        }
      } // protected void process(List<String> data)

      @Override
      protected void done() {
        owner.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
