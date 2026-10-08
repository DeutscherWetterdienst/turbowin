package turbowin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Owns asynchronous import of an AMVER sailing plan. */
final class AmverSailingPlanImportWorkflow {

  private AmverSailingPlanImportWorkflow() {}

  static void start(String importFile, Consumer<String> readLine, Runnable onComplete) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        String file_line;

        // read all lines from imported sailing plan file
        try (BufferedReader in = new BufferedReader(new FileReader(importFile))) {
          while ((file_line = in.readLine()) != null) {
            readLine.accept(file_line);
          } // while((file_line = in.readLine()) != null)
        } // try
        catch (IOException ex) {
          // JOptionPane.showMessageDialog(null, "Error reading saling plan import file (" +
          // importFile + ")",  main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          System.out.println("--- Function import_button_actionPerformed(): " + ex);
        } // catch

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        onComplete.run();
      }
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
