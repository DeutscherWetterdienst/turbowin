package turbowin;

import static turbowin.main.*;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns the Format 101 server-upload SwingWorker workflow. */
final class Format101ServerWorkflow {

  private Format101ServerWorkflow() {}

  static void start() {
    // http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
    //
    // NB see also: RS232_Send_Sensor_Data_to_APR_format101_Server() [main_RS232_RS422.java]
    //
    // called from: Output_Obs_to_server_menu_actionPerformed() [main.java]

    // Temporary message box, (pop-up for only a short time) automatically disappears
    //
    support_class.response_warning_pop_up();

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        String server_format_101_line = "";
        int responseCode = OK_RESPONSE_FORMAT_101; // OK
        boolean isHttps = true;
        if (server_com_protocol.equals(HTTP_PROTOCOL)) {
          isHttps = false;
        }

        // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
        server_format_101_line = get_format_101_obs_from_file();
        if (server_format_101_line.equals("") == true) {
          responseCode = INVALID_RESPONSE_FORMAT_101; // self defined
        }

        if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_101)) {
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
          // Encode all 'not allowed' ASCII chars if not java.net.URISyntaxException (with index
          // number in the URL string)
          // String url =
          // "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?obs=" +
          // encoded_server_format_101_obs;
          String url =
              ServerObservationRequestBuilder.format101Url(upload_URL, server_format_101_line);
          return Format101ServerClient.execute(url, isHttps);
        }
        return Integer.toString(responseCode);
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          String response = get();

          int int_response_code = INTERNAL_ERROR_RESPONSE_CODE; // default, initial value
          String response_error = "";

          try {
            String string_response_code = response.substring(0, 3); // e.g. "200"
            try {
              int_response_code = Integer.parseInt(string_response_code);
              if (response.length() > 3) {
                response_error = response.substring(3);
              }
            } catch (NumberFormatException e2) {
              int_response_code = INTERNAL_ERROR_RESPONSE_CODE;
            }
          } catch (IndexOutOfBoundsException e) {
            int_response_code = INTERNAL_ERROR_RESPONSE_CODE;
          }

          if (int_response_code == 200) // OK
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
                    + HttpStatusMapper.httpResponseCodeToText(int_response_code)
                        .replace("<br>", " ")
                    + response_error;

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
                    + HttpStatusMapper.httpResponseCodeToText(int_response_code)
                    + response_error
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
              "[MANUAL] error in Function: Output_obs_to_server_format_101_V2;" + ex.toString();

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

  ///////

}
