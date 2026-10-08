package turbowin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous saving of an AMVER sailing plan. */
final class AmverSailingPlanSaveWorkflow {

  private AmverSailingPlanSaveWorkflow() {}

  static void start(String savedFile) {
    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        boolean result_ok = false;

        try (BufferedWriter out = new BufferedWriter(new FileWriter(savedFile))) {
          AmverSailingPlanExportFormatter.write(out);
          result_ok = true;
        } catch (IOException ex) {
          // JOptionPane.showMessageDialog(null, "unable to write to: " + "sp_saved_file",
          // main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          System.out.println("--- Function save_amver_sp(): " + ex);
          result_ok = false;
        } // catch

        return result_ok;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          boolean result_ok = get();
          if (result_ok == true) {
            // user feedback
            String info = "Amver Sailing Plan written to: " + savedFile;
            info +=
                "\n NOTE Sailing Plan was not saved in AMVER format but in an internal format for future (import) use";
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
          } else {
            JOptionPane.showMessageDialog(
                null,
                "unable to write to: " + "sp_saved_file",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          }
        } // protected void done()
        catch (InterruptedException | ExecutionException ex) {
          System.out.println("--- Function save_amver_sp(): " + ex);
        }
      }
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
