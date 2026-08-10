package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns the Python observation email SwingWorker workflow. */
final class PythonObservationEmailWorkflow {

  private PythonObservationEmailWorkflow() {}

  static void start(boolean manual_send) {
    // NB manual_send = true/false   (set in Output_obs_by_email_all_manual() [main.java] or part of
    // AP[&T]R / AWSR)
    //
    // NB This function called by: - Output_obs_by_email_all_manual() [main.java]                //
    // note: in this function format101 obs and FM13 obs were already made!
    //                             - RS232_Output_obs_by_email_all_APR() [main_RS232_RS422.java] //
    // note: in this function format101 obs and FM13 obs were already made!

    new SwingWorker<Integer, Void>() {

      @Override
      protected Integer doInBackground() throws Exception {
        //
        ///////////////////////// PREPARE EMAIL PARAMETERS //////////////////////
        //

        // initialisation
        String smtp_mode =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String smtp_host_local =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String smtp_password_local =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String send_to =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String send_from =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String email_subject =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String email_body =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String send_cc =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String smtp_port_local =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script
        String attachment =
            "null"; // NB do not insert "" here because this will be considerd as a 'none' argument
        // for the python script

        ObservationEmailModeResolver.Mode emailMode =
            ObservationEmailModeResolver.resolve(
                manual_send,
                email_send_mode,
                APTR_AWSR_send_method,
                EMAIL_SEND_LOCAL_HOST,
                EMAIL_SEND_GMAIL,
                EMAIL_SEND_YAHOO,
                EMAIL_SEND_CUSTOM,
                APTR_AWSR_SMTP_HOST,
                APTR_AWSR_GMAIL,
                APTR_AWSR_YAHOO_MAIL,
                APTR_AWSR_CUSTOM_MAIL);
        if (emailMode == ObservationEmailModeResolver.Mode.DISABLED) {
          JOptionPane.showMessageDialog(
              null,
              "invalid email send method (Maintenance -> Email settings, insert the CUSTOM settings)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        } else if (emailMode == ObservationEmailModeResolver.Mode.CUSTOM) {
          boolean includeAttachment =
              (obs_format.equals(FORMAT_101)
                      || (main.obs_format.equals(main.FORMAT_AWS)
                          && main.eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN)))
                  && main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT);
          ObservationEmailParameters parameters =
              ObservationEmailParameters.forCustomSettings(
                  custom_security,
                  custom_email_server,
                  custom_password,
                  obs_email_recipient,
                  your_custom_address,
                  obs_email_subject,
                  obs_email_cc,
                  custom_port,
                  includeAttachment);
          smtp_mode = parameters.smtpMode();
          smtp_host_local = parameters.smtpHost();
          smtp_password_local = parameters.smtpPassword();
          send_to = parameters.recipient();
          send_from = parameters.sender();
          email_subject = parameters.subject();
          send_cc = parameters.cc();
          smtp_port_local = parameters.port();
          attachment = parameters.attachment();
        } // else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_CUSTOM)) ||
        // (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL)) )

        //
        ///////////////////////// PREPARE OBS (format101 or FM13) //////////////////////
        //
        int python_email_status = 0;
        boolean doorgaan = true;
        /*
           // shortcuts to SMTP host, Gmail and Yhoo disabled from version 4.2
           if ( (manual_send == true && (email_send_mode.equals(EMAIL_SEND_LOCAL_HOST) ||
                                         email_send_mode.equals(EMAIL_SEND_GMAIL) ||
                                         email_send_mode.equals(EMAIL_SEND_YAHOO) ||
                                         email_send_mode.equals(EMAIL_SEND_CUSTOM))) ||
                (manual_send == false && (APTR_AWSR_send_method.equals(APTR_AWSR_SMTP_HOST) ||
                                          APTR_AWSR_send_method.equals(APTR_AWSR_GMAIL) ||
                                          APTR_AWSR_send_method.equals(APTR_AWSR_YAHOO_MAIL) ||
                                          APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL))) )
        */
        if (emailMode == ObservationEmailModeResolver.Mode.CUSTOM) {
          ObservationEmailContentPreparer.Result content =
              ObservationEmailContentPreparer.prepare(
                  obs_format,
                  eucaws_uploads_method.equals(UPLOADS_VIA_TURBOWIN),
                  obs_101_email,
                  (obs_format.equals(FORMAT_101)
                              || (obs_format.equals(FORMAT_AWS)
                                  && eucaws_uploads_method.equals(UPLOADS_VIA_TURBOWIN)))
                          && !obs_101_email.equals(FORMAT_101_ATTACHEMENT)
                      ? get_format_101_obs_from_file()
                      : "",
                  obs_write,
                  email_subject,
                  mydatetime.YY_code,
                  mydatetime.GG_code);
          email_subject = content.subject();
          email_body = content.body();
          doorgaan = content.nonEmpty();

          if (!doorgaan) {
            python_email_status = 1001; // empty obs
          }
        } // if (email_send_mode.equals(EMAIL_SEND_LOCAL_HOST) ||
        // email_send_mode.equals(EMAIL_SEND_GMAIL) || email_send_mode.equals(EMAIL_SEND_YAHOO)
        // etc.
        else {
          python_email_status = 1002; // invalid email_send_mode
          doorgaan = false;
        }

        //
        ///////////////////////// INVOKE PYTHON EMAIL MODULE //////////////////////
        //
        if (doorgaan) // so no empty obs and send mode = ok
        {
          ObservationEmailLogDetails.Details logDetails =
              ObservationEmailLogDetails.from(send_cc, smtp_port_local, attachment);
          main.log_turbowin_system_message(
              ObservationEmailLogDetails.message(
                  email_body, send_to, logDetails, send_from, smtp_mode, "secondary email module"));

          if (python_email_class == null) {
            python_email_class = new Python_Email();
          }

          boolean python_email_found_ok = false;
          python_email_found_ok = python_email_class.python_email_control_center();

          if (python_email_found_ok) // 'python email exe' copied sucessfully (this time or already
          // in the past) from jar to destination or was already present
          {
            String smtp_password_local_plain =
                ObservationEmailPasswordResolver.resolve(
                    smtp_password_local, myemailsettings::decrypt);

            ObservationEmailRequest request =
                ObservationEmailRequest.from(
                    smtp_mode,
                    smtp_host_local,
                    smtp_password_local_plain,
                    send_to,
                    send_from,
                    email_subject,
                    email_body,
                    send_cc,
                    smtp_port_local,
                    attachment);
            python_email_status =
                ObservationEmailTransportExecutor.execute(
                    new PythonObservationEmailSender(python_email_class), request);
          } else // python module not found / copy-error
          {
            python_email_status = 1000; // python module not found / copy-error
          }
        } //  if (doorgaan)

        return python_email_status;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          int python_email_status = get();

          if (python_email_status == 0) // OK
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
          } else if (python_email_status == 1000) // copy failure or python email exe not find
          {
            ObservationEmailPythonModuleFailureHandler.handle(
                manual_send,
                main::IMMT_log,
                main::Reset_all_meteo_parameters,
                info ->
                    JOptionPane.showMessageDialog(
                        null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE));
          } else if (python_email_status == 1001) // empty obs
          {
            ObservationEmailEmptyObservationHandler.handle(
                manual_send,
                main::Reset_all_meteo_parameters,
                info -> main.log_turbowin_system_message("[EMAIL] " + info),
                info ->
                    JOptionPane.showMessageDialog(
                        null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE));
          } else if (python_email_status == 1002) // invalid send mode
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
