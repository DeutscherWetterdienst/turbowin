package turbowin;

import static turbowin.main.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns the FM13 server-upload SwingWorker workflow. */
final class Fm13ServerWorkflow {

  private Fm13ServerWorkflow() {}

  static void start() {
    // http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
    //
    // NB see also: RS232_Send_Sensor_Data_to_APR_format101_Server() [main_RS232_RS422.java]
    //
    // called from: Output_Obs_to_server_menu_actionPerformed() [main.java]

    // Temporary message box, (pop-up for only a short time) automatically disappears
    //
    support_class.response_warning_pop_up();

    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        Integer responseCode = main.OK_RESPONSE_FORMAT_FM13; // OK

        // compose coded obs [obs_write = FM13 obs !!!]
        // NB already done in Output_Obs_to_server_menu_actionPerformed()
        // String SPATIE = main.SPATIE_OBS_SERVER;                                   // use "_" as
        // marker between obs groeps
        // main.obs_write = main.compose_coded_obs(SPATIE);                          // returns
        // UNDEFINED if call sign, position or date/time not inserted

        if ((main.obs_write.equals("") == true)
            || (main.obs_write.equals(main.UNDEFINED) == true)) {
          responseCode = main.INVALID_RESPONSE_FORMAT_FM13;
        }

        if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_FM13)) {
          // NB encoding not necessary for FM13, but if necessary in the future see the comments
          // below

          // NB encoding:
          // Translates a string into application/x-www-form-urlencoded format using a specific
          // encoding scheme. This method uses the supplied encoding scheme to obtain
          // the bytes for unsafe characters.
          // Note: The World Wide Web Consortium Recommendation states that UTF-8 should be used.
          // Not doing so may introduce incompatibilites.
          //
          // http://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters:
          // You only need to keep in mind to encode only the individual query string parameter name
          // and/or value, not the entire URL,
          // for sure not the query string parameter separator character & nor the parameter
          // name-value separator character =.
          // String q = "random word 500 bank $";
          // String url = "http://example.com/query?q=" + URLEncoder.encode(q, "UTF-8");
          //
          // Encode all 'not alloud' ASCII chars if not java.net.URISyntaxException (with index
          // number in the URL string)
          // String encoded_server_format_101_obs = URLEncoder.encode(server_format_101_line,
          // "UTF-8");

          //// String url =
          // "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?obs=" +
          // encoded_server_format_101_obs;
          // String url = upload_URL + "obs=" + encoded_server_format_101_obs;       // eg upload
          // U?rL = http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?
          String url = ServerObservationRequestBuilder.fm13Url(upload_URL, main.obs_write);

          try {
            String message = "[MANUAL] sending 'GET' request to URL: " + url;
            main.log_turbowin_system_message(message);

            responseCode = ObservationServerClient.getResponseCode(url);

            // NB besides the response code there is also a corresponding response text, but
            // unfortunately with html tags,
            //    and only with the standard response codes, self defined response codes are not
            // returned?. Not suitable for direct using it into a popup message box
            //    so only using the reponse code and locally (in this program) determined the
            // corresponding return http message text
            //

          } // try
          catch (MalformedURLException ex) {
            // String message = "[MANUAL] send obs failed; MalformedURLException (function:
            // Output_obs_to_server_format_101())";
            // main.log_turbowin_system_message(message);
            // main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);

            responseCode = RESPONSE_MALFORMED_URL;
          } catch (URISyntaxException ex) {
            responseCode = main.RESPONSE_MALFORMED_URL;
          } catch (IOException ex) {
            // String message = "[MANUAL] send obs failed; IOException; most probably no internet
            // connection available; (function: Output_obs_to_server_format_101())";
            // main.log_turbowin_system_message(message);
            // main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);

            responseCode = RESPONSE_NO_INTERNET;
          } // catch
        } // if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_101))

        return responseCode;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          Integer response_code = get();

          if (response_code == 200) // OK
          {
            String message_a = "[MANUAL] send obs success";

            // file logging
            main.log_turbowin_system_message(message_a);

            // bottom line main screen
            main.jTextField4.setText(
                main.sdf_tsl_2.format(new Date())
                    + " UTC "
                    + message_a); // update status field (bottom line main -progress- window)

            // pop-up message in manual mode
            String info =
                "<html>"
                    + main.sdf_tsl_2.format(new Date())
                    + " UTC "
                    + "send obs success"
                    + "<br>"
                    + "Many thanks for your cooperation"
                    + "</html>";
            JOptionPane.showMessageDialog(
                null, info, APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);

            IMMT_log();
            Reset_all_meteo_parameters();
          } else // send obs NOT ok
          {
            // NB besides the response code there is also a corresponding response text (from the
            // apache server, but unfortunately with html tags,
            //    and only with the standard response codes, self defined response codes are not
            // returned (from the apache server)?.
            //    Not suitable for direct using it into a popup message box
            //    so only using the reponse code and locally (in this program) determined the
            // corresponding return http message text
            //
            String message_b =
                "[MANUAL] send obs failed; "
                    + HttpStatusMapper.httpResponseCodeToText(response_code).replace("<br>", " ");

            // file logging
            main.log_turbowin_system_message(message_b);

            // bottom main screen
            main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message_b);

            // pop-up message in manual mode
            String info =
                "<html>"
                    + main.sdf_tsl_2.format(new Date())
                    + " UTC "
                    + "send obs failed; "
                    + "<br>"
                    + HttpStatusMapper.httpResponseCodeToText(response_code)
                    + "</html>";
            JOptionPane.showMessageDialog(
                null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);

            // "try again?" pop-up message
            if (JOptionPane.showConfirmDialog(
                    null,
                    "try again (Obs to server)",
                    APPLICATION_NAME + " ",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION) {
              // YES button pressed (= try again)
              start();
            } else // NO or CANCEL clicked by the user
            {
              IMMT_log();
              Reset_all_meteo_parameters();
            }
          } // else (send obs NOT ok)
        } // protected void done()
        catch (InterruptedException | ExecutionException ex) {
          String message =
              "[MANUAL] error in Function: Output_obs_to_server_FM13_TurboWin_stand_alone();"
                  + ex.toString();

          // fille logging
          main.log_turbowin_system_message(message);

          // bottom main screen
          main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);

          // pop-up message in manual mode
          String info =
              "<html>"
                  + main.sdf_tsl_2.format(new Date())
                  + " UTC "
                  + "send obs failed; "
                  + "<br>"
                  + HttpStatusMapper.httpResponseCodeToText(RESPONSE_INTERRUPTION)
                  + "</html>";
          JOptionPane.showMessageDialog(
              null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);

          // "try again?" pop-up message
          if (JOptionPane.showConfirmDialog(
                  null,
                  "try again (Obs to server)",
                  APPLICATION_NAME + " ",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE)
              == JOptionPane.YES_OPTION) {
            // YES button pressed (= try again)
            start();
          } else // NO or CANCEL clicked by the user
          {
            IMMT_log();
            Reset_all_meteo_parameters();
          }
        } // catch (InterruptedException | ExecutionException ex)
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
