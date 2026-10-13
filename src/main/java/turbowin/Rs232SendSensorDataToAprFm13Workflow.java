package turbowin;

import static turbowin.main_RS232_RS422.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

final class Rs232SendSensorDataToAprFm13Workflow {

  private Rs232SendSensorDataToAprFm13Workflow() {}

  static void start(final boolean retry) {
    // NB see also: Output_obs_to_server_FM13() [main.java] and

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

    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        Integer responseCode = main.OK_RESPONSE_FORMAT_FM13; // OK

        // NB in this phase you kow: - date-time ok
        //                           - position ok
        //                           - call sign ok (RS232_check_APR_settings())
        //                           - logs ok (RS232_check_APR_settings())
        //

        // compose coded obs [obs_write = FM13 obs !!!]
        String SPATIE = main.SPATIE_OBS_SERVER; // use "_" as marker between obs groups
        main.obs_write =
            main.compose_coded_obs(
                SPATIE); // returns UNDEFINED if call sign, position or date/time not inserted

        if ((main.obs_write.equals("") == true)
            || (main.obs_write.equals(main.UNDEFINED) == true)) {
          responseCode = main.INVALID_RESPONSE_FORMAT_FM13;
        }

        return responseCode;
      } // protected Integer doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          Integer response_code = get();

          // continue if FM13 obs ok
          //
          if (Objects.equals(response_code, main.OK_RESPONSE_FORMAT_FM13)) // null save comparising
          {
            // NB encoding for FM13 obs not necessary, but if necessary in the future see comments
            // below

            // NB encoding:
            // Translates a string into application/x-www-form-urlencoded format using a specific
            // encoding scheme. This method uses the supplied encoding scheme to obtain
            // the bytes for unsafe characters.
            // Note: The World Wide Web Consortium Recommendation states that UTF-8 should be used.
            // Not doing so may introduce incompatibilites.
            //
            // http://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters:
            // You only need to keep in mind to encode only the individual query string parameter
            // name and/or value, not the entire URL,
            // for sure not the query string parameter separator character & nor the parameter
            // name-value separator character =.
            // String q = "random word 500 bank $";
            // String url = "http://example.com/query?q=" + URLEncoder.encode(q, "UTF-8");
            //
            // Encode all 'not alloud' ASCII chars if not java.net.URISyntaxException (with index
            // number in the URL string)
            // String encoded_format_FM13_obs = null;
            // try
            // {
            //   encoded_format_FM13_obs = URLEncoder.encode(main.obs_write, "UTF-8");
            // }
            // catch (UnsupportedEncodingException ex)
            // {
            //   response_code = main.RESPONSE_UNSUPPORTED_ENCODING;
            // }

            // continue if the encoding was ok (= always in case of FM13 obs)
            if (!Objects.equals(
                response_code, main.RESPONSE_UNSUPPORTED_ENCODING)) // null save comparising
            {
              // eg String url =
              // "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?obs=" +
              // encoded_format_101_obs;
              // String url = main.upload_URL + "obs=" + encoded_format_FM13_obs;
              String url = main.upload_URL + "obs=" + main.obs_write;

              URL obj = null;
              try {
                obj = new URI(url).toURL();
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional (default is GET)
                con.setRequestMethod("GET");

                String message = log_tag + " sending 'GET' request to URL: " + url;
                main.log_turbowin_system_message(message);

                response_code = con.getResponseCode(); // if internet available the default
                // OK_RESPONSE_FORMAT_FM13 will be overwritten here

              } // try
              catch (MalformedURLException ex) {
                response_code = main.RESPONSE_MALFORMED_URL;
              } catch (URISyntaxException ex) {
                response_code = main.RESPONSE_MALFORMED_URL;
              } catch (IOException ex) {
                response_code = main.RESPONSE_NO_INTERNET;
              } // catch
            } // if (!Objects.equals(response_code, main.RESPONSE_UNSUPPORTED_ENCODING))
          } // if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_FM13))

          // how to continue depends on response_code
          //
          if (response_code == 200) // OK
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
            //    (if internet avaialble the response code will be overwritten with eg 200)

            String message_b =
                log_tag
                    + " send obs failed; "
                    + HttpStatusMapper.httpResponseCodeToText(response_code).replace("<br>", " ");

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

          // for checking if a upload retry will be necessary [ see function ...]
          APR_server_response_code = response_code;

        } // try
        catch (InterruptedException | ExecutionException ex) {
          String message =
              log_tag
                  + " error in Function: RS232_Send_Sensor_Data_to_APR_FM13_Server(); no internet connection available?; "
                  + ex.toString();
          main.log_turbowin_system_message(message);
          main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
