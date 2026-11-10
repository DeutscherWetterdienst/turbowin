package turbowin;

import static turbowin.main.*;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

/** Handles confirmation and resource cleanup when the application is closed. */
final class ApplicationShutdownWorkflow {

  private static boolean exitRequested;

  private ApplicationShutdownWorkflow() {}

  static boolean hasActiveConnections(
      int connectionMode, String port, int secondaryConnectionMode, String secondaryPort) {
    // PTB220, PTB330, EUCAWS, OMC-140, MintakaDuo, Mintaka Star USB, HMP155
    // (all via serial communication), or Mintaka Star WiFi connected.
    // Mode 6 is intentionally considered active even when no serial port is configured.
    return ((connectionMode != 0) && (port != null))
        || (connectionMode == 6)
        || ((secondaryConnectionMode != 0) && (secondaryPort != null));
  }

  static String buildExitMessage(
      String applicationName, boolean activeConnections, boolean aprEnabled, boolean wowEnabled) {
    String info = "Are you sure you want to exit this application?";
    if (activeConnections) {
      info +=
          "\n\n ("
              + applicationName
              + " will stop with monitoring and collecting of the sensor data)";
      if (aprEnabled || wowEnabled) {
        info += "\n (" + applicationName + " will stop with automated reports upload)";
        info += "\n (minimise " + applicationName + " instead of closing)";
      }
    }
    return info;
  }

  static void handle(main owner) {
    if (exitRequested) {
      return;
    }
    // Also guard the confirmation dialog against repeated window/menu/tray exit events.
    exitRequested = true;

    // log memory statistics
    support_class.log_memory_statistics();

    String info =
        buildExitMessage(
            APPLICATION_NAME,
            hasActiveConnections(
                RS232_connection_mode, defaultPort, RS232_connection_mode_II, defaultPort_II),
            APR,
            WOW);
    int result =
        JOptionPane.showConfirmDialog(
            owner, info, "Exit " + APPLICATION_NAME, JOptionPane.YES_NO_OPTION);

    if (result != JOptionPane.YES_OPTION) {
      exitRequested = false;
      return;
    }

    new ApplicationExitRequest(
            (completed, cancel) -> saveBeforeExit(owner, completed, cancel),
            saved -> decideAfterSaveFailure(owner, saved),
            () -> finish(owner),
            () -> {
              ConfigurationWriteWorkflow.cancelExit();
              exitRequested = false;
            })
        .start();
  }

  private static void saveBeforeExit(
      main owner, Consumer<ConfigurationWriteWorkflow.SaveResult> completed, Runnable cancel) {
    JDialog progress = new JDialog(owner, "Saving settings", Dialog.ModalityType.APPLICATION_MODAL);
    progress.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    JPanel content = new JPanel(new BorderLayout(0, 12));
    content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
    content.add(new JLabel("Saving settings before closing TurboWin…"), BorderLayout.NORTH);
    JProgressBar indicator = new JProgressBar();
    indicator.setIndeterminate(true);
    content.add(indicator, BorderLayout.CENTER);
    JButton cancelExit = new JButton("Cancel exit");
    cancelExit.addActionListener(
        event -> {
          progress.dispose();
          cancel.run();
        });
    content.add(cancelExit, BorderLayout.SOUTH);
    progress.setContentPane(content);
    progress.pack();
    progress.setLocationRelativeTo(owner);
    ConfigurationWriteWorkflow.saveBeforeExit(
        saved -> {
          progress.dispose();
          completed.accept(saved);
        });
    // A modal Swing dialog keeps dispatching EDT events while preventing further UI edits.
    progress.setVisible(true);
  }

  private static ApplicationExitRequest.Decision decideAfterSaveFailure(
      main owner, ConfigurationWriteWorkflow.SaveResult result) {
    Object[] options = {"Retry saving", "Cancel exit", "Exit without saving"};
    int decision =
        JOptionPane.showOptionDialog(
            owner,
            "TurboWin could not save settings:\n\n"
                + String.join("\n", result.failures())
                + "\n\nRetry saving, keep TurboWin open, or exit without saving?",
            "Unable to save settings",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0]);
    return switch (decision) {
      case 0 -> ApplicationExitRequest.Decision.RETRY;
      case 2 -> ApplicationExitRequest.Decision.EXIT_WITHOUT_SAVING;
      default -> ApplicationExitRequest.Decision.CANCEL;
    };
  }

  private static void finish(main owner) {
    // serial communication barometer (not neccessary for WiFi barometer)
    // if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) ||
    // (RS232_connection_mode == 3) || (RS232_connection_mode == 4)) && (defaultPort != null) )
    if ((RS232_connection_mode != 0) && (defaultPort != null)) {
      if (main.serialPort != null) {
        // try
        // {
        main.serialPort.removeDataListener();
        main.serialPort.closePort();
        main.serialPort = null;
        // }
        // catch (SerialPortException ex)
        // {
        //   System.out.println(ex);
        // }
      } // if (main.serialPort != null)
    } // if ((RS232_connection_mode != 0) && (defaultPort != null))

    // serial communication thermometer
    if ((RS232_connection_mode_II != 0) && (defaultPort_II != null)) {
      //
      // NB in case of WiFi: defaultport_II == null
      //
      if (main.serialPort_II != null) {
        main.serialPort_II.removeDataListener();
        main.serialPort_II.closePort();
        main.serialPort_II = null;
      } // if (main.serialPort_II != null)
    } // if ((RS232_connection_mode_II != 0) && (defaultPort_II != null))

    // serial communication GPS
    if ((RS232_GPS_connection_mode != 0) && (main_RS232_RS422.GPS_defaultPort != null)) {
      if (main.GPS_serialPort != null) {
        // try
        // {
        main.GPS_serialPort.removeDataListener();
        main.GPS_serialPort.closePort();
        main.GPS_serialPort = null;
        // }
        // catch (SerialPortException ex)
        // {
        //   System.out.println(ex);
        // }
      } // if (main_RS232_RS422.GPS_serialPort != null)
    } // if ((RS232_GPS_connection_mode != 0) && (main_RS232_RS422.GPS_defaultPort != null))

    // if the program was minimized remove the trayIcon
    if ((main.ICONIFIED & owner.getExtendedState()) == main.ICONIFIED) {
      tray.remove(trayIcon);
    }

    // TurboWin+ stopped message to log
    log_turbowin_system_message(
        "[GENERAL] stopped "
            + APPLICATION_NAME
            + " "
            + application_mode
            + " "
            + TurboWinAppInfo.APPLICATION_VERSION);

    // exit
    System.exit(0);
  }
}
