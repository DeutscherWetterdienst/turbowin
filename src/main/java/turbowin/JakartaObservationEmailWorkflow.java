package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns the Jakarta observation email SwingWorker workflow. */
final class JakartaObservationEmailWorkflow {

  private JakartaObservationEmailWorkflow() {}

  static void start(boolean manual_send) {
    // NB manual_send = true/false   (set in Output_obs_by_email_all_manual() [main.java] or
    // RS232_Output_obs_by_email_all_APR()[main_RS232_RS422.java])
    //
    // NB This function called by: - Output_obs_by_email_all_manual()    --- done --- [main.java]
    //          // note: in this function format101 obs and FM13 obs were already made!
    //                             - RS232_Output_obs_by_email_all_APR() --- done ---
    // [main_RS232_RS422.java] // note: in this function format101 obs and FM13 obs were already
    // made!

    new SwingWorker<Integer, Void>() {

      @Override
      protected Integer doInBackground() throws Exception {
        ObservationEmailWorkflowPreparation.Result preparation =
            ObservationEmailWorkflowPreparation.prepare(manual_send);
        int jakarta_email_status = preparation.status();
        ObservationEmailRequest request = preparation.request();

        if (request != null) {
          ObservationEmailLogDetails.Details logDetails =
              ObservationEmailLogDetails.from(request.cc(), request.port(), request.attachment());
          main.log_turbowin_system_message(
              ObservationEmailLogDetails.message(
                  request.body(),
                  request.recipient(),
                  logDetails,
                  request.sender(),
                  request.smtpMode(),
                  "primary email module"));

          if (jakarta_email_class == null) {
            jakarta_email_class = new Jakarta_Email();
          }

          request = ObservationEmailWorkflowPreparation.resolvePassword(request);
          jakarta_email_status =
              ObservationEmailTransportExecutor.execute(
                  new JakartaObservationEmailSender(jakarta_email_class), request);
        }

        return jakarta_email_status;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          int jakarta_email_status = get();

          if (jakarta_email_status == 0) // OK
          {
            ObservationEmailSuccessHandler.complete(
                manual_send,
                main::IMMT_log,
                main::Reset_all_meteo_parameters,
                info ->
                    JOptionPane.showMessageDialog(
                        null,
                        info,
                        main.APPLICATION_NAME + " info",
                        JOptionPane.INFORMATION_MESSAGE));
          } else if (jakarta_email_status == ObservationEmailStatus.EMPTY_OBSERVATION) // empty obs
          {
            ObservationEmailEmptyObservationHandler.handle(
                manual_send,
                main::Reset_all_meteo_parameters,
                info -> main.log_turbowin_system_message("[EMAIL] " + info),
                info ->
                    JOptionPane.showMessageDialog(
                        null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE));
          } else if (jakarta_email_status
              == ObservationEmailStatus.INVALID_MODE) // invalid send mode
          {
            ObservationEmailInvalidModeHandler.handle(
                manual_send,
                main::IMMT_log,
                main::Reset_all_meteo_parameters,
                info -> main.log_turbowin_system_message("[EMAIL] " + info),
                info ->
                    JOptionPane.showMessageDialog(
                        null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE));
          } else // failed to send
          {
            ObservationEmailGenericFailureHandler.handle(
                manual_send,
                main::IMMT_log,
                main::Reset_all_meteo_parameters,
                info ->
                    JOptionPane.showMessageDialog(
                        null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE));
          }
        } // try
        catch (InterruptedException | ExecutionException ex) {
          ObservationEmailInvocationFailureHandler.handle(
              manual_send,
              ex,
              main::log_turbowin_system_message,
              System.out::println,
              info ->
                  JOptionPane.showMessageDialog(
                      null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE));
        } // catch
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
