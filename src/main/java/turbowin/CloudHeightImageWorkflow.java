package turbowin;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous loading of cloud-height images. */
final class CloudHeightImageWorkflow {

  private CloudHeightImageWorkflow() {}

  static void start(mych owner, String imagePath) {
    new SwingWorker<ImageIcon, Object>() {
      @Override
      public ImageIcon doInBackground() {
        return owner.createImageIcon(imagePath);
      }

      @Override
      public void done() {
        try {
          if (owner.isKnownCloudHeightImagePath(imagePath)) {
            owner.setCloudHeightImageIcon(imagePath, get());
          }
        } // try
        catch (InterruptedException ignore) {
        } catch (java.util.concurrent.ExecutionException e) {
          String why = null;
          Throwable cause = e.getCause();
          if (cause != null) {
            why = cause.getMessage();
          } else {
            why = e.getMessage();
          }
          // System.err.println("Error retrieving file: " + why);
          JOptionPane.showMessageDialog(
              null,
              "Error retrieving file: " + why,
              main.APPLICATION_NAME + " error",
              JOptionPane.ERROR_MESSAGE);
        } // catch
      } //  public void done()
    }.execute();
  }
}
