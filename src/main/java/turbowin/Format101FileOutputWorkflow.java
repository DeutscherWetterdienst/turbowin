package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

/** Owns the Format 101 file-output SwingWorker workflow. */
final class Format101FileOutputWorkflow {

  private Format101FileOutputWorkflow() {}

  static void start(main owner) {
    // pop-up the file chooser dialog box
    JFileChooser chooser = new JFileChooser();
    int result = chooser.showSaveDialog(owner);
    if (result == JFileChooser.APPROVE_OPTION) {
      owner.output_file = chooser.getSelectedFile().getPath();

      new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() throws Exception {
          boolean obs_written_ok = true;
          boolean doorgaan = true;
          String file_format_101_line = "";

          file_format_101_line = get_format_101_obs_from_file();
          if (file_format_101_line.equals("") == true) {
            doorgaan = false;
          }

          if (doorgaan == true) {
            obs_written_ok =
                ObservationFileWriter.write(new File(owner.output_file), file_format_101_line);
          } // if (doorgaan == true)

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
          } catch (InterruptedException | ExecutionException ex) {
            System.out.println("--- Function Output_obs_to_file_format_101(): " + ex);
          }
        } // protected void done()
      }.execute(); // new SwingWorker<Void, Void>()
    } // if (result == JFileChooser.APPROVE_OPTION
  }
}
