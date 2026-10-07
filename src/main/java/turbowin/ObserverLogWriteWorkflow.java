package turbowin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous writing of the observer log. */
final class ObserverLogWriteWorkflow {

  private ObserverLogWriteWorkflow() {}

  static void start() {
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        String observer_log_ok = "OK";
        String volledig_path = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;

        try (BufferedWriter out = new BufferedWriter(new FileWriter(volledig_path))) {
          for (int r = 0; r < myobserver.OBSERVER_ROWS; r++) {
            // at least surname must be present (c = 0)
            if ((myobserver.observer_data[r][0] != null)
                && (myobserver.observer_data[r][0].compareTo("") != 0)) {
              for (int c = 0; c < myobserver.OBSERVER_COLUMNS; c++) {
                if ((myobserver.observer_data[r][c] != null)
                    && (myobserver.observer_data[r][c].compareTo("") != 0)) {
                  out.write(myobserver.observer_data[r][c]);
                } else // empty field/cell
                {
                  out.write("-");
                }

                out.write(";"); // semi-column seperated
              } // for (int c = 0; c < OBSERVER_COLUMNS; c++)

              out.newLine(); // newLine(): write a line separator. The line separator string is
              // defined by the system property line.separator, and is not
              // necessarily a single newline ('\n') character.
            } // if ((observer_data[r][0] != null)
          } // for (int r = 0; r < OBSERVER_ROWS; r++)

        } // try
        catch (IOException ex) {
          // JOptionPane.showMessageDialog(null, "unable to write to: " + volledig_path,
          // main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          observer_log_ok = "unable to write to: " + volledig_path;
        } // catch

        return observer_log_ok;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          String result_observer_log_ok = get();
          if (result_observer_log_ok.equals("OK") == false) {
            JOptionPane.showMessageDialog(
                null,
                result_observer_log_ok,
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          }
        } // protected void done()
        catch (InterruptedException | ExecutionException ex) {
          System.out.println("--- Function schrijf_observer_log(): " + ex);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
