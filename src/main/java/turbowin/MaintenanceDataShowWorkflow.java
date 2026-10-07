package turbowin;

import java.util.List;
import javax.swing.SwingWorker;

/** Owns asynchronous loading of the maintenance data shown by the maintenance window. */
final class MaintenanceDataShowWorkflow {

  private MaintenanceDataShowWorkflow() {}

  static void start(mymaintenancedata owner) {
    final String newline = System.getProperty("line.separator");
    owner.prepareShowData();

    new SwingWorker<String, String>() {
      @Override
      protected String doInBackground() throws Exception {
        String file_line = null;

        // fill array
        main.fill_configuratie_array();

        for (int i = 0; i < main.MAX_AANTAL_CONFIGURATIEREGELS; i++) {
          if ((main.configuratie_regels[i] != null)
              && (main.configuratie_regels[i].compareTo("") != 0)) {
            // System.out.println("+++ configuratie_regels[" + i + "] = " + configuratie_regels[i]);
            file_line = main.configuratie_regels[i];
            publish(new String[] {file_line});
          }
        } // for (int i = 0; i < MAX_AANTAL_CONFIGURATIEREGELS; i++)

        // extend title with full path of the system log txt file (can be used to point the observer
        // to the file for eg forwarding to a Met Centre in case of problems)
        // setTitle("TurboWin+ system log [" + volledig_path_turbowin_system_logs + "]");

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void process(List<String> data) {
        // process: Receives data chunks from the publish method asynchronously on the Event
        // Dispatch Thread.
        for (String received_line : data) {
          // NB eg http://www.javacreed.com/swing-worker-example/
          //    The swing component should only accessed from the process() method and never used
          // from within the doInBackbround() method
          //    or other methods directly (by directly we mean from the same thread) invoked from
          // it.

          // NB altijd in for loop omdat meerdere ontbvangen string's verzameld kunnen zijn voordat
          // het hier procesed wordt(inherent aan SwingWorker)
          owner.appendShowDataLine(received_line, newline);
        }
      } // protected void process(List<String> data)

      @Override
      protected void done() {
        owner.finishShowData();
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
