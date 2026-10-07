package turbowin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous writing of the captain log. */
final class CaptainLogWriteWorkflow {

  private CaptainLogWriteWorkflow() {}

  static void start() {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        String volledig_path = main.logs_dir + java.io.File.separator + main.CAPTAIN_LOG;

        try {
          BufferedWriter out = new BufferedWriter(new FileWriter(volledig_path));
          SemicolonLogFileWriter.write(
              out, mycaptain.captain_data, mycaptain.CAPTAIN_ROWS, mycaptain.CAPTAIN_COLUMNS);

          out.close();

        } // try
        catch (Exception e) {
          JOptionPane.showMessageDialog(
              null,
              "unable to write to: " + volledig_path,
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        } // catch

        return null;
      } // protected Void doInBackground() throws Exception
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
