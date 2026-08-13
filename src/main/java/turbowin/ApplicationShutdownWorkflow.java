package turbowin;

import static turbowin.main.*;

import javax.swing.JOptionPane;

/** Handles confirmation and resource cleanup when the application is closed. */
final class ApplicationShutdownWorkflow {

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
    // TODO add your handling code here:

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

    if (result == JOptionPane.YES_OPTION) {
      // serial communication barometer (not neccessary for WiFi barometer)
      // if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) ||
      // (RS232_connection_mode == 3) || (RS232_connection_mode == 4)) && (defaultPort != null) )
      if ((RS232_connection_mode != 0) && (defaultPort != null)) {
        //
        // NB RxTx Serial ?
        // close() for Linux for a proper clean up of a port stale lock file (e.g.
        // /var/lock/LCK...ttyUSB0), not appropiate to Windows
        // unfortunately it didn't help (see also:
        // http://www.raspberrypi.org/forums/viewtopic.php?f=81&t=32186)
        //
        // The problem with file lock is likely due to the lock-file being created with root
        // permissions (sudo),
        // however the IDE is being ran under user mode. Either you will have to make the lock file
        // create in user-mode,
        // change the ownership of the lock, or run the IDE in root. Running it in root shouldn't be
        // a big issue.
        //
        //
        // NB in case of WiFi: defaultport == null
        //
        //
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
        // only necessary in case of RxTx serial?
        // if (main_RS232_RS422.GPS_serialPort != null)
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
}
