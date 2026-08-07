package turbowin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.swing.ImageIcon;
import org.junit.Test;

public class ImageUtilsTest {

  @Test
  public void loadsAnIconFromTheClasspath() {
    ImageIcon icon = ImageUtils.createImageIcon(getClass(), "/turbowin/icons/wind.png");

    assertNotNull(icon);
    assertTrue(icon.getIconWidth() > 0);
    assertTrue(icon.getIconHeight() > 0);
  }
}
