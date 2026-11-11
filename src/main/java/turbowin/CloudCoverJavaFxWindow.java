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
            mycloudcover.N,
            mycloudcover.N.isEmpty() ? "" : mycloudcover.Nh,
            advisedHeight(mycloudcover.computeHeightAdvice()),
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
      case -9 -> mycloudcover.H_CLOUDLESS;
      case 0 -> mycloudcover.H_0_50;
      case 1 -> mycloudcover.H_50_100;
      case 2 -> mycloudcover.H_100_200;
      case 3 -> mycloudcover.H_200_300;
      case 4 -> mycloudcover.H_300_600;
      case 5 -> mycloudcover.H_600_1000;
      case 6 -> mycloudcover.H_1000_1500;
      case 7 -> mycloudcover.H_1500_2000;
      case 8 -> mycloudcover.H_2000_2500;
      case 9 -> mycloudcover.H_GROTER_2500;
      default -> mycloudcover.H_NOT_DETERMINED;
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
    mycloudcover.N = result.n;
    mycloudcover.Nh = result.nh;
    mycloudcover.h = result.h;
    mycloudcover.N_code = result.nCode;
    mycloudcover.Nh_code = result.nhCode;
    mycloudcover.h_code = result.hCode;
    main.cloud_cover_fields_update();
    close.run();
    if (main.in_next_sequence) {
      next();
    }
  }

  private static void clearObservation() {
    mycloudcover.N = "";
    mycloudcover.Nh = "";
    mycloudcover.h = "";
    mycloudcover.N_code = "";
    mycloudcover.Nh_code = "";
    mycloudcover.h_code = "";
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
    mych form = new mych();
    form.setSize(800, 600);
    form.setVisible(true);
  }

  private static void stop(Runnable close) {
    close.run();
    main.in_next_sequence = false;
  }

  private static void next() {
    myobserver form = new myobserver();
    form.setSize(800, 600);
    form.setVisible(true);
  }
}
