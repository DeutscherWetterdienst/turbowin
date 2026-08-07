package turbowin;

import javax.swing.UIManager;

/** Swing look-and-feel helpers that do not depend on a particular window. */
final class SwingUiUtils {

  private SwingUiUtils() {}

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
