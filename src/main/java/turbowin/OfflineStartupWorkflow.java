package turbowin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.swing.JOptionPane;

/** Initializes the offline single-instance check. */
final class OfflineStartupWorkflow {

  private OfflineStartupWorkflow() {}

  static int resolvePort(String commandLinePort, int defaultPort) {
    if (commandLinePort.equals("")) {
      return defaultPort;
    }
    try {
      return Integer.parseInt(commandLinePort);
    } catch (NumberFormatException e) {
      return defaultPort;
    }
  }

  static ServerSocket openInstanceCheck(String commandLinePort, int defaultPort) {
    int port = resolvePort(commandLinePort, defaultPort);
    if (!commandLinePort.equals("") && port == defaultPort) {
      try {
        Integer.parseInt(commandLinePort);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            null,
            "command line argument PORT number not OK",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
    }

    System.out.println("--- server port for checking multiple instances running = " + port);
    try {
      return new ServerSocket(port, 10, InetAddress.getLocalHost());
    } catch (java.net.UnknownHostException e) {
      return null;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
          null, "TurboWin+ is already running", main.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
      System.exit(0);
      return null;
    }
  }
}
