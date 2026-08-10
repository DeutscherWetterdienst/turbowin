package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

/** Owns the FM13 file-output SwingWorker workflow. */
final class Fm13FileOutputWorkflow {

  private Fm13FileOutputWorkflow() {}

  static void start(main owner) {
    // JOptionPane.showMessageDialog(null, "test output obs to file", main.APPLICATION_NAME + "
    // info", JOptionPane.INFORMATION_MESSAGE);

    // pop-up the file chooser dialog box
    JFileChooser chooser = new JFileChooser();
    int result = chooser.showSaveDialog(owner);
    if (result == JFileChooser.APPROVE_OPTION) {
      owner.output_file = chooser.getSelectedFile().getPath();

      new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() throws Exception {
          boolean obs_written_ok = true;

          obs_written_ok = ObservationFileWriter.write(new File(owner.output_file), obs_write);

          return obs_written_ok;
        } // protected Void doInBackground() throws Exception

        @Override
        protected void done() {
          try {
            boolean result_obs_written_ok = get();

            if (result_obs_written_ok == true) {
              String info = "obs written to: " + owner.output_file;
              JOptionPane.showMessageDialog(
                  null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
            } else {
              JOptionPane.showMessageDialog(
                  null,
                  "unable to write to: " + owner.output_file,
                  APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
            }

            IMMT_log();

            Reset_all_meteo_parameters();
          } // protected void done()
          catch (InterruptedException | ExecutionException ex) {
            // Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("--- Function Output_obs_to_file_FM13(): " + ex);
          }
        }
      }.execute(); // new SwingWorker<Void, Void>()
    } // if (result == JFileChooser.APPROVE_OPTION
  }
}
