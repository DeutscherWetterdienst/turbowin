package turbowin;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous preparation of the observation statistics data. */
final class ObservationStatisticsWorkflow {

  private ObservationStatisticsWorkflow() {}

  static void start(Obs_Stats_view owner, processing processingDialog) {
    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        String volledig_path_immt = main.logs_dir + java.io.File.separator + main.IMMT_LOG;
        ObsStatsImmtLogProcessor.Result result =
            ObsStatsImmtLogProcessor.process(
                volledig_path_immt,
                Obs_Stats_view.view_immt_log_period,
                Obs_Stats_view.view_local_start_date,
                Obs_Stats_view.view_local_end_date,
                main.obs_stats_mode.equals(main.OBSERVERS_STATS));
        Obs_Stats_view.immt_list.addAll(result.records());
        Obs_Stats_view.immt_rec_first = result.firstRecord();
        Obs_Stats_view.immt_rec_last = result.lastRecord();
        return result.status();
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        processingDialog
            .dispose(); // NB actually it closes much too early, but also putting the closing
        // statement in another functions (eg in display_IMMT_on_leaflet_map()) ,
        // the result is the same (too early)
        try {
          int return_immt = get();
          if (return_immt == 0) {
            Obs_Stats_view.immt_log_ok = true;
            owner.repaint();
          } else if (return_immt == -1) {
            Obs_Stats_view.immt_log_ok = false;
            String info = "Error when creating immt.log list";
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          } else if (return_immt == -2) {
            Obs_Stats_view.immt_log_ok = false;
            String info = "Error when opening immt.log";
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          } else if (return_immt == -3) {
            Obs_Stats_view.immt_log_ok = false;
            String info =
                "> "
                    + Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES
                    + " different observer names recorded in immt.log. Please send logs to your Port Meteorological Officer";
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
          }

        } catch (InterruptedException | ExecutionException ex) {
          String info = "internal error :" + ex;
          JOptionPane.showMessageDialog(
              null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Integer, Void>()
  }
}
