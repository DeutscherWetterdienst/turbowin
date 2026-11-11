package turbowin;

import java.awt.Font;
import javax.swing.UIManager;

/** Swing look-and-feel helpers that do not depend on a particular window. */
final class SwingUiUtils {

  static final int UI_FONT_SMALL = 10;
  static final int UI_FONT_MEDIUM = 11;
  static final int UI_FONT_STANDARD = 12;
  static final int UI_FONT_LARGE = 13;
  static final int UI_FONT_TITLE = 14;

  private SwingUiUtils() {}

  static Font uiFont(int style, int size) {
    return new Font(Font.SANS_SERIF, style, size);
  }

  static void setUIFont(javax.swing.plaf.FontUIResource f) {
    java.util.Enumeration<?> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof javax.swing.plaf.FontUIResource) {
        UIManager.put(key, f);
      }
    }
  }
}
