package turbowin;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Connects the JavaFX cloud-cover view to the existing Swing observation workflow. */
final class CloudCoverJavaFxWindow {
  static final String TITLE =
      "Total cloud cover, amount of Cl (Cm) and height of base of lowest cloud";

  private CloudCoverJavaFxWindow() {}

  static JDialog open() {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new IllegalStateException("Cloud cover must be opened on the Swing event thread");
    }
    CloudCoverView.State state =
        new CloudCoverView.State(
            CloudCoverState.N,
            CloudCoverState.N.isEmpty() ? "" : CloudCoverState.Nh,
            advisedHeight(CloudHeightAdvice.compute()),
            main.in_next_sequence,
            main.offline_mode);
    return FxSwingDialog.show(
        main.mainClass,
        TITLE,
        new Dimension(850, 680),
        close ->
            CloudCoverView.create(
                state,
                (n, nh, h) -> SwingUtilities.invokeLater(() -> accept(n, nh, h, close)),
                () -> SwingUtilities.invokeLater(() -> cancel(close)),
                () -> SwingUtilities.invokeLater(() -> back(close)),
                () -> SwingUtilities.invokeLater(() -> stop(close)),
                () ->
                    SwingUtilities.invokeLater(
                        () -> main.help_mouseClicked("cloud_cover_height.pdf"))));
  }

  static String advisedHeight(int advice) {
    return switch (advice) {
      case -9 -> CloudCoverState.H_CLOUDLESS;
      case 0 -> CloudCoverState.H_0_50;
      case 1 -> CloudCoverState.H_50_100;
      case 2 -> CloudCoverState.H_100_200;
      case 3 -> CloudCoverState.H_200_300;
      case 4 -> CloudCoverState.H_300_600;
      case 5 -> CloudCoverState.H_600_1000;
      case 6 -> CloudCoverState.H_1000_1500;
      case 7 -> CloudCoverState.H_1500_2000;
      case 8 -> CloudCoverState.H_2000_2500;
      case 9 -> CloudCoverState.H_GROTER_2500;
      default -> CloudCoverState.H_NOT_DETERMINED;
    };
  }

  private static void accept(String n, String nh, String h, Runnable close) {
    // The original dialog resets the observation before validating the new selection.
    clearObservation();
    CloudCoverSelection.Result result = CloudCoverSelection.validate(n, nh, h);
    if (!result.valid()) {
      JOptionPane.showMessageDialog(
          null, result.error, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      clearObservation();
      return;
    }
    CloudCoverState.N = result.n;
    CloudCoverState.Nh = result.nh;
    CloudCoverState.h = result.h;
    CloudCoverState.N_code = result.nCode;
    CloudCoverState.Nh_code = result.nhCode;
    CloudCoverState.h_code = result.hCode;
    main.cloud_cover_fields_update();
    close.run();
    if (main.in_next_sequence) {
      next();
    }
  }

  private static void clearObservation() {
    CloudCoverState.N = "";
    CloudCoverState.Nh = "";
    CloudCoverState.h = "";
    CloudCoverState.N_code = "";
    CloudCoverState.Nh_code = "";
    CloudCoverState.h_code = "";
    main.cloud_cover_fields_update();
  }

  private static void cancel(Runnable close) {
    close.run();
    if (main.in_next_sequence) {
      next();
    }
  }

  private static void back(Runnable close) {
    close.run();
    HighCloudDialog.open();
  }

  private static void stop(Runnable close) {
    close.run();
    main.in_next_sequence = false;
  }

  private static void next() {
    ObserverDialog.open();
  }
}
