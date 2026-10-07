package turbowin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.SwingWorker;

/** Owns asynchronous reading of the captain log. */
final class CaptainLogReadWorkflow {

  private CaptainLogReadWorkflow() {}

  static void start(mycaptain owner) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        /* initialisation */
        owner.clear_captain_data_array();

        String volledig_path =
            main.logs_dir
                + java.io.File.separator
                + main.CAPTAIN_LOG; // "java.io.File.separator" os independent

        /* read all lines from captain log */
        try (BufferedReader in = new BufferedReader(new FileReader(volledig_path))) {
          SemicolonLogFileReader.read(
              in, mycaptain.captain_data, mycaptain.CAPTAIN_ROWS, mycaptain.CAPTAIN_COLUMNS);
        } // try
        catch (IOException ex) {
          // do nothing, possible file was never created
        } // catch

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        owner.updateCaptainTable();
      }
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
