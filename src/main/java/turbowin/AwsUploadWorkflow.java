package turbowin;

import static turbowin.main.*;

import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;

/** Owns the AWS serial observation upload workflow. */
final class AwsUploadWorkflow {

  private AwsUploadWorkflow() {}

  static void start(main owner) {
    // TODO add your handling code here:
    String message_info = "";
    String log_info = "";
    boolean send_ok = true;

    if (main.defaultPort != null) {
      String obs_for_AWS_string = owner.compile_obs_for_AWS();
      String log_obs_for_AWS_string =
          obs_for_AWS_string; // for logging but still without the newline ("\r\n")
      System.out.println("Writing " + obs_for_AWS_string + " to " + main.defaultPort);
      obs_for_AWS_string += "\r\n"; // <CR><LF>  required for EUCAWS

      if (main.serialPort.isOpen()) {
        // serialPort.writeBytes(obs_for_AWS_string.getBytes());              // Write data to port
        byte[] bytes_message_obs =
            obs_for_AWS_string.getBytes(StandardCharsets.UTF_8); // Java 7+ only
        if (main.serialPort.writeBytes(bytes_message_obs, bytes_message_obs.length)
            != -1) // Write data to port
        {
          message_info = "success obs sent to AWS";
          log_info =
              "[AWS] obs sent ok ("
                  + log_obs_for_AWS_string
                  + ") to "
                  + main.defaultPort_descriptive;
          send_ok = true;
        } else {
          message_info = "error obs to AWS";
          log_info =
              "[AWS] Unable to write "
                  + log_obs_for_AWS_string
                  + " to "
                  + main.defaultPort_descriptive;
          send_ok = false;
        }
      } // if (main.serialPort.isOpen())
      else {
        // System.out.println("+++ " + "[AWS] Couldn't open " +
        // main.serialPort.getDescriptivePortName());
        log_info =
            "[AWS] Couldn't open "
                + main.serialPort.getDescriptivePortName()
                + " ("
                + main.defaultPort_descriptive
                + ")";
        message_info = "error obs to AWS";
        send_ok = false;
      } // else
    } // if (main.defaultPort != null)
    else {
      // JOptionPane.showMessageDialog(null, "Failed to send obs to AWS because no serial connection
      // available (defaultPort = null)"  , APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
      log_info =
          "[AWS] Failed to send obs to AWS because no serial connection available (defaultPort = null)";
      message_info = "error obs to AWS";
      send_ok = false;
    }

    // show message box "success obs sent to AWS" or "error obs to AWS"
    if (send_ok) {
      JOptionPane.showMessageDialog(
          null, message_info, APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(
          null, message_info, APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
    }
    // final JOptionPane pane_end = new JOptionPane(message_info, JOptionPane.INFORMATION_MESSAGE,
    // JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
    // final JDialog end_dialog = pane_end.createDialog(APPLICATION_NAME);
    //
    // Timer timer_end = new Timer(1500, new ActionListener()
    // {
    //   @Override
    //   public void actionPerformed(ActionEvent e)
    //   {
    //      end_dialog.dispose();
    //   }
    // });
    // timer_end.setRepeats(false);
    // timer_end.start();
    // end_dialog.setVisible(true);

    // logging
    main.log_turbowin_system_message(
        log_info); // NB writing also to screen console [System.out.println(log_info)] is part of
    // this function

    // on request of Meteo France write the extra MANUAL measured and observed data to IMMT log
    owner.IMMT_AWS_manual_input_preperations();
    IMMT_log();

    // reset alll meteo parameters
    main_RS232_RS422.RS422_initialise_AWS_Sensor_Data_For_Display(); // must be called before:
    // Reset_all_meteo_parameters();
    Reset_all_meteo_parameters();
  }
}
