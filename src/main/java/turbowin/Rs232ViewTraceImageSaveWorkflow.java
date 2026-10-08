package turbowin;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous saving of an RS232 graph trace image. */
final class Rs232ViewTraceImageSaveWorkflow {

  private Rs232ViewTraceImageSaveWorkflow() {}

  static void start(BufferedImage image, String imagePath) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        try {
          ImageIO.write(image, "png", new File(imagePath));
        } // try
        catch (HeadlessException | IOException e) {
          JOptionPane.showMessageDialog(
              null,
              "unable to write to: " + imagePath,
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        } // catch

        return null;
      } // protected Void doInBackground() throws Exception
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
