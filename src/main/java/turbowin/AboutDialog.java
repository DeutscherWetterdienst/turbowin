package turbowin;

import java.awt.Dimension;
import java.awt.Window;
import javax.swing.SwingUtilities;

/** Entry point for the embedded JavaFX About dialog. */
final class AboutDialog {
  private static final String SOT = "link_sot_vos";
  private static final String SOURCE = "link_gitlab";

  private AboutDialog() {}

  static void show(Window owner) {
    AboutInfo info = AboutInfo.capture();
    FxSwingDialog.show(
        owner,
        "about " + main.APPLICATION_NAME,
        new Dimension(700, 760),
        close ->
            AboutView.create(
                info,
                close,
                link ->
                    SwingUtilities.invokeLater(() -> AboutLinkWorkflow.start(link, SOT, SOURCE))));
  }

  static String sotLink() {
    return SOT;
  }

  static String sourceLink() {
    return SOURCE;
  }
}
