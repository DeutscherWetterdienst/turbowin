package turbowin;

import static turbowin.main.RESPONSE_MALFORMED_URL;
import static turbowin.main.RESPONSE_NO_INTERNET;
import static turbowin.main_RS232_RS422.APR_server_response_code;
import static turbowin.main_RS232_RS422.format_101_obs;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.SwingWorker;

final class Rs232SendSensorDataToAprFormat101Workflow {

  private Rs232SendSensorDataToAprFormat101Workflow() {}

  static void start(final boolean retry) {
    // NB see also: Output_obs_to_server_FM13() [main.java]

    // called from: - RS232_APR_AWSR_send()
    //      which itself is called from  -
    // RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()
    //                                   - RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR()
    //                                   - RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR()
    //

    String log_tag;
    if (main.AWSR == true) {
      log_tag = "[AWSR]";
    } else {
      log_tag = "[APR]";
    }

    ///////////////// compress the data end send it //////////////////////
    //
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        int responseCode = main.OK_RESPONSE_FORMAT_101; // OK
        String responseString = "";
        HttpURLConnection con_http = null;
        HttpsURLConnection con_https = null;

        boolean isHttps = true;
        if (main.server_com_protocol.equals(main.HTTP_PROTOCOL)) {
          isHttps = false;
        }

        // NB in this phase you kow: - date-time ok
        //                           - position ok
        //                           - call sign ok (RS232_check_APR_settings())
        //                           - logs ok (RS232_check_APR_settings())
        //

        // compress the data
        //
        main.format_101_class = new FORMAT_101();
        main.format_101_class.compress_and_decompress_101_control_center();

        // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
        //
        format_101_obs = "";

        final String volledig_path_format_101_compressed_file =
            main.logs_dir
                + java.io.File.separator
                + main.FORMAT_101_ROOT_DIR
                + java.io.File.separator
                + main.FORMAT_101_TEMP_DIR
                + java.io.File.separator
                + "HPK_"
                + main.FORMAT_101_INPUT_FILE; // NB adding "HPK_" to the input file name is
        // automatically done by the C-code compression
        // functions

        try (BufferedReader in =
            new BufferedReader(new FileReader(volledig_path_format_101_compressed_file))) {
          if ((format_101_obs = in.readLine()) == null) {
            String message =
                log_tag
                    + " when retrieveing format 101 data empty file: "
                    + volledig_path_format_101_compressed_file;
            main.log_turbowin_system_message(message);

            format_101_obs =
                ""; // the function which invoke get_format_101_obs_from_file() will check this
            // value
            responseCode = main.INVALID_RESPONSE_FORMAT_101;
          } // else

        } // try
        catch (IOException | HeadlessException e) {
          String message =
              log_tag
                  + " when retrieving format 101 data error opening file: "
                  + volledig_path_format_101_compressed_file;
          main.log_turbowin_system_message(message);

          format_101_obs =
              ""; // the function which invoke get_format_101_obs_from_file() will check this value
          responseCode = main.INVALID_RESPONSE_FORMAT_101;
        } // catch

        if (Objects.equals(responseCode, main.OK_RESPONSE_FORMAT_101)) // null save comparising
        {
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
          String encoded_format_101_obs = null;
          try {
            encoded_format_101_obs = URLEncoder.encode(format_101_obs, "UTF-8");
          } catch (UnsupportedEncodingException ex) {
            responseCode = main.RESPONSE_UNSUPPORTED_ENCODING;
          }

          // continue if the encoding was ok
          if (!Objects.equals(
              responseCode, main.RESPONSE_UNSUPPORTED_ENCODING)) // null save comparising
          {
            // String url =
            // "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?obs=" +
            // encoded_format_101_obs;
            String url = main.upload_URL + "obs=" + encoded_format_101_obs;

            int maxRetries =
                2; // Maximum retries per IP (ps keep in mind, in this automatic mode, after failing
            // there is always a retry after 3 minutes)
            boolean success = false;
            URL obj = null;
            try {
              // Resolve all IP addresses for the given domain
              InetAddress[] addresses = InetAddress.getAllByName(new URI(url).toURL().getHost());

              // Loop through all resolved IP addresses
              for (InetAddress address : addresses) // Loop through all resolved IP addresses
              {
                // Multiple Connection Attempts (Failover Mechanism).
                // If one IP is encountering issues, another IP may work fine. This is particularly
                // useful in cases where
                // certain routing or networking issues might cause a TCP reset.

                System.out.println("--- Trying IP: " + address.getHostAddress());
                int attempt = 0;
                int backoff = 1000; // Start with 1 second
                while (attempt < maxRetries && !success) // Automatic Retries within same IP
                {
                  // Automatic Retries with Exponential Backoff (Delaying Retries).
                  // TCP resets can be caused by transient network issues or server-side conditions
                  // that may be temporary.
                  // A retry helps in such cases by allowing the server to recover, or by avoiding
                  // network glitches that
                  // could have caused the reset. Exponential backoff prevents overwhelming the
                  // server with rapid retry attempts
                  // and gives time for transient errors to resolve. It also helps in reducing
                  // congestion on the network

                  attempt++;
                  try {
                    obj = new URI(url).toURL();

                    if (isHttps) {
                      // String message = "[MANUAL] sending 'GET' request (https) to URL: " + url;
                      String message = log_tag + " sending 'GET' request (https) to URL: " + url;
                      main.log_turbowin_system_message(message);

                      con_https = (HttpsURLConnection) obj.openConnection(); // For HTTPS
                      con_https.setRequestMethod("GET");
                      responseCode = con_https.getResponseCode();
                    } else {
                      // String message = "[MANUAL] sending 'GET' request (http) to URL: " + url;
                      String message = log_tag + " sending 'GET' request (http) to URL: " + url;
                      main.log_turbowin_system_message(message);

                      con_http = (HttpURLConnection) obj.openConnection(); // FOR HTTP
                      con_http.setRequestMethod("GET");
                      responseCode = con_http.getResponseCode();
                    } // else

                  } // try
                  catch (MalformedURLException | URISyntaxException ex) {
                    responseCode = RESPONSE_MALFORMED_URL;
                    responseString = ex.getMessage();
                  } catch (SocketException se) {
                    // (hopefully...) also catching TCP errors ('TCP reset from server' / 'TCP RST')
                    responseCode = RESPONSE_NO_INTERNET;
                    responseString = se.getMessage(); // e.g. ........

                    Thread.sleep(backoff); // Wait before retrying
                    backoff *= 2; // Exponential backoff
                  } catch (IOException ix) {
                    responseCode = RESPONSE_NO_INTERNET;
                    responseString =
                        ix.getMessage(); // e.g. "Permission denied: connect" (when firewall
                    // blocking)

                    Thread.sleep(backoff); // Wait before retrying
                    backoff *= 2; // Exponential backoff
                  } // catch (IOException ex)
                  finally {
                    // Always disconnect to free up resources
                    if (isHttps && (con_https != null)) {
                      con_https.disconnect();
                    }
                    if ((!isHttps) && (con_http != null)) {
                      con_http.disconnect();
                    }
                  } // finally

                  if (responseCode == 200) {
                    // Handle response
                    success = true;
                  }
                } // while (attempt < maxRetries && !success)

                if (success) {
                  // Exit the IP loop if one IP worked successfully
                  break;
                }
              } //  for (InetAddress address : addresses)

              if (!success) {
                System.out.println("--- All IP addresses failed after retries.");
              }

            } // try
            catch (UnknownHostException | MalformedURLException e) {
              responseCode = RESPONSE_NO_INTERNET;
              responseString = e.getMessage();
            }
          } // if (!Objects.equals(response_code, main.RESPONSE_UNSUPPORTED_ENCODING))
        } // if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_101))

        String response = Integer.toString(responseCode);

        if (!responseString.equals("")) {
          response += " (" + responseString + ")";
        }

        return response;
      } // protected Integer doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          String response = get();

          int int_response_code = main.INTERNAL_ERROR_RESPONSE_CODE; // default, initial value
          String response_error = "";

          try {
            String string_response_code = response.substring(0, 3); // e.g. "200"
            try {
              int_response_code = Integer.parseInt(string_response_code);
              if (response.length() > 3) {
                response_error = response.substring(3);
              }
            } catch (NumberFormatException e2) {
              int_response_code = main.INTERNAL_ERROR_RESPONSE_CODE;
            }
          } catch (IndexOutOfBoundsException e) {
            int_response_code = main.INTERNAL_ERROR_RESPONSE_CODE;
          }

          // how to continue depends on response_code
          //
          if (int_response_code == 200) // OK
          {
            String message_a = log_tag + " send obs success";
            main.log_turbowin_system_message(message_a);
            main.jTextField4.setText(
                main.sdf_tsl_2.format(new Date())
                    + " UTC "
                    + message_a); // update status field (bottom line main -progress- window)

            // save data to immt log and clear main screen
            //
            // NB in some rare occasions it is possible that within 1 second the obs will be send
            // twice
            //    (somethimes if the wifi/ethernet connection is bridged) and in that case a
            // misformed record will be writkten to the IMMT log
            //    to prevent this check the date/time and position elements (because after the first
            // upload they were reset)
            //
            if ((!mydatetime.year.equals(""))
                && (!mydatetime.MM_code.equals(""))
                && (!mydatetime.YY_code.equals(""))
                && (!mydatetime.GG_code.equals(""))
                && (!myposition.Qc_code.equals(""))
                && (!myposition.lalala_code.equals(""))
                && (!myposition.lolololo_code.equals(""))) {
              // RS232_make_APR_IMMT_ready();                        // pressure in IMMT will only
              // be recorded as "mybarometer.PPPP_code" (not mybarometer.pressure_msl_corrected)
              main.IMMT_log();
            }
            main.Reset_all_meteo_parameters();
          } else // send obs NOT ok or compresing NOT ok or encoding NOT ok
          {
            // NB besides the response code there is also a corresponding response text, but
            // unfortunately with html tags,
            //    and only with the standard response codes, self defined response codes are not
            // returned?. Not suitable for direct using it into a popup message box
            //    so only using the reponse code and locally (in this program) determined the
            // corresponding return http message text

            // NB If no internet connection available the responseCode will be RESPONSE_NO_INTERNET
            //    (if internet available the response code will be overwritten with eg 200)

            String message_b =
                log_tag
                    + " send obs failed; "
                    + HttpStatusMapper.httpResponseCodeToText(int_response_code)
                        .replace("<br>", " ")
                    + response_error;

            // file logging
            main.log_turbowin_system_message(message_b);

            // bottom main screen
            main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message_b);

            // so even no success after a retry, but now save the data to IMMT because no further
            // attempts will follow (and the data was not saved before)
            if (retry) {
              // save data to immt log and clear main screen
              //
              // NB in some rare occasions it is possible that within 1 second the obs will be send
              // twice
              //    (somethimes if the wifi/ethernet connection is bridged) and in that case a
              // misformed record will be writkten to the IMMT log
              //    to prevent this check the date/time and position elements (because after the
              // first upload they were reset)
              //
              if ((!mydatetime.year.equals(""))
                  && (!mydatetime.MM_code.equals(""))
                  && (!mydatetime.YY_code.equals(""))
                  && (!mydatetime.GG_code.equals(""))
                  && (!myposition.Qc_code.equals(""))
                  && (!myposition.lalala_code.equals(""))
                  && (!myposition.lolololo_code.equals(""))) {
                // RS232_make_APR_IMMT_ready();                     // pressure in IMMT will only be
                // recorded as "mybarometer.PPPP_code" (not mybarometer.pressure_msl_corrected)
                main.IMMT_log();
              }
              main.Reset_all_meteo_parameters();
            } // if retry
          } // else (send obs NOT ok

          // for checking if a upload retry will be necessary [ see unction ...
          APR_server_response_code = int_response_code;

        } // try
        catch (InterruptedException | ExecutionException ex) {
          String message =
              log_tag
                  + " error in Function: RS232_Send_Sensor_Data_to_APR_format101_Server_V2(); no internet connection available?; "
                  + ex.toString();
          main.log_turbowin_system_message(message);
          main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<String, Void>()
  }
}
