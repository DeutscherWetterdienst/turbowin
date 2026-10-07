package turbowin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.SwingWorker;

/** Owns asynchronous reading of the observer log. */
final class ObserverLogReadWorkflow {

  private ObserverLogReadWorkflow() {}

  static void start(myobserver owner) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        /* initialisation */
        owner.clear_observer_data_array();

        String volledig_path = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;

        /* read all lines from observer log */
        try (BufferedReader in = new BufferedReader(new FileReader(volledig_path))) {
          SemicolonLogFileReader.read(
              in, myobserver.observer_data, myobserver.OBSERVER_ROWS, myobserver.OBSERVER_COLUMNS);
        } // try
        catch (IOException ex) {
          // do nothing, possible file was never created
        } // catch

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        owner.updateObserverTable();
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
