package turbowin;

import java.net.URL;
import javax.swing.ImageIcon;

/** Shared classpath image-resource loading for the Swing screens. */
final class ImageUtils {

  private ImageUtils() {}

  static ImageIcon createImageIcon(Class<?> resourceClass, String path_and_file) {
    URL url = null;

    try {
      url = resourceClass.getResource(path_and_file);
    } catch (Exception e) {
      /* ... */
    }

    return new ImageIcon(url);
  }
}
