package turbowin;

import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/** Applies the initial look and feel and Linux font setup before Swing components are generated. */
final class StartupThemeWorkflow {

  private StartupThemeWorkflow() {}

  static boolean usesTransparentTheme(String theme) {
    return main.THEME_TRANSPARENT.equals(theme);
  }

  static boolean usesLinuxFont(String theme, String os) {
    return usesTransparentTheme(theme) && "LINUX".equals(os);
  }

  static boolean requiresUbuntuFontWarning(String os, String fontName) {
    return "LINUX".equals(os) && !"Ubuntu".equals(fontName);
  }

  static void initialize(main owner) {
    if (!usesTransparentTheme(main.theme_mode)) {
      applyNimbusOrFallbackLookAndFeel();
      if ("LINUX".equals(OSDetector.getOSString())) {
        if (!main.theme_changed) {
          main.current_font = owner.getFont();
        } else {
          owner.setFont(main.current_font);
        }
      }
    } else if ("LINUX".equals(OSDetector.getOSString())) {
      // Without Ubuntu 12-point font, transparent-mode labels and text take too much space; this
      // workaround is for Linux only, not Windows, and must run before initComponents().
      main.setUIFont(new javax.swing.plaf.FontUIResource("Ubuntu", Font.PLAIN, 12));
    }
  }

  static void finishTransparentTheme(main owner, String os, Font labelFont) {
    if (!usesTransparentTheme(main.theme_mode)) {
      return;
    }

    owner.setOpacity(0.75f);
    if (requiresUbuntuFontWarning(os, labelFont.getFontName())) {
      JOptionPane.showMessageDialog(
          null,
          "Install Ubuntu fonts for a better GUI lay out in opacity Theme mode",
          main.APPLICATION_NAME,
          JOptionPane.WARNING_MESSAGE);
    }
  }

  static void applyNimbusOrFallbackLookAndFeel() {
    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (ClassNotFoundException
          | InstantiationException
          | IllegalAccessException
          | UnsupportedLookAndFeelException ignored) {
        // Keep the platform default look and feel.
      }
    }
  }
}
