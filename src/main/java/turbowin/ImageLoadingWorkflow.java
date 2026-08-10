package turbowin;

import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous loading of toolbar images. */
final class ImageLoadingWorkflow {

  private ImageLoadingWorkflow() {}

  static void start(main owner, String imagePath) {
    new SwingWorker<ImageIcon, Object>() {
      @Override
      public ImageIcon doInBackground() {
        return owner.createImageIcon(imagePath);
      }

      @Override
      public void done() {
        try {
          owner.setToolbarIcon(imagePath, get());
        } catch (InterruptedException ignore) {
        } catch (ExecutionException e) {
          Throwable cause = e.getCause();
          String why = cause != null ? cause.getMessage() : e.getMessage();
          JOptionPane.showMessageDialog(
              null,
              "Error retrieving toolbar icon file: " + why,
              main.APPLICATION_NAME,
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }.execute();
  }
}
