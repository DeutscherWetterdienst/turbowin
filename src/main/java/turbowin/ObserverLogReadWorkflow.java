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
        String record;
        int pos_begin;
        int pos_eind;
        int r;

        /* initialisation */
        owner.clear_observer_data_array();

        String volledig_path = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;

        /* read all lines from observer log */
        try (BufferedReader in = new BufferedReader(new FileReader(volledig_path))) {
          r = 0;
          while ((record = in.readLine()) != null) {
            pos_begin = 0;
            pos_eind = 0;
            for (int c = 0; c < myobserver.OBSERVER_COLUMNS; c++) {
              pos_eind =
                  record.indexOf(
                      ";",
                      pos_begin); // Returns the index within this string of the first occurrence of
              // the specified substring, starting at the specified index.

              if (pos_eind != -1) {
                myobserver.observer_data[r][c] = record.substring(pos_begin, pos_eind);
                pos_begin = pos_eind + 1;
              } else {
                break;
              }
            } // for (int c = 0; c < OBSERVER_COLUMNS; c++)

            r++;

            /* safety */
            if (r >= myobserver.OBSERVER_ROWS) {
              break;
            }
          } // while((file_line = in.readLine()) != null)
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
