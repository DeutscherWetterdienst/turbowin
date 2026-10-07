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

          for (int r = 0; r < mycaptain.CAPTAIN_ROWS; r++) {
            // at least surname must be present (c = 0)
            if ((mycaptain.captain_data[r][0] != null)
                && (mycaptain.captain_data[r][0].compareTo("") != 0)) {
              for (int c = 0; c < mycaptain.CAPTAIN_COLUMNS; c++) {
                if ((mycaptain.captain_data[r][c] != null)
                    && (mycaptain.captain_data[r][c].compareTo("") != 0)) {
                  out.write(mycaptain.captain_data[r][c]);
                } else // empty field/cell
                {
                  out.write("-");
                }

                out.write(";"); // semi-column seperated
              } // for (int c = 0; c < CAPTAIN_COLUMNS; c++)

              out.newLine(); // newLine(): write a line separator. The line separator string is
              // defined by the system property line.separator, and is not
              // necessarily a single newline ('\n') character.
            } // if ((captain_data[r][0] != null)
          } // for (int r = 0; r < CAPTAIN_ROWS; r++)

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
