package turbowin;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous loading of cloud-layer images. */
final class CloudLayerImageWorkflow {

  private CloudLayerImageWorkflow() {}

  static void start(mycl owner, String imagePath) {
    new SwingWorker<ImageIcon, Object>() {
      @Override
      public ImageIcon doInBackground() {
        return owner.createImageIcon(imagePath);
      }

      @Override
      public void done() {
        try {
          if (owner.isKnownCloudLayerImagePath(imagePath)) {
            owner.setCloudLayerImageIcon(imagePath, get());
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
        } // catcl
      } //  public void done()
    }.execute();
  }
}
