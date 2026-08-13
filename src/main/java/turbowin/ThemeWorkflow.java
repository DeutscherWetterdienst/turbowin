package turbowin;

import static turbowin.main.*;

import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/** Applies Nimbus themes and handles recreation after leaving the transparent theme. */
final class ThemeWorkflow {

  private ThemeWorkflow() {}

  static boolean requiresMainReset(String currentTheme) {
    return THEME_TRANSPARENT.equals(currentTheme);
  }

  static void applyTransparent() {
    if (!theme_mode.equals(THEME_TRANSPARENT)) {
      mainClass.dispose();
      theme_changed = true;

      try {
        theme_mode = THEME_TRANSPARENT;
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (ClassNotFoundException
          | InstantiationException
          | IllegalAccessException
          | UnsupportedLookAndFeelException ex) {
        String info = "Error invoking Transparent Theme";
        javax.swing.JOptionPane.showMessageDialog(
            null,
            info,
            main.APPLICATION_NAME + " message",
            javax.swing.JOptionPane.WARNING_MESSAGE);
      }

      // Essential for transparency: Metal is the only Java look-and-feel suitable for this theme.
      javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
      mainClass = new main();
      mainClass.setVisible(true);
    }
  }

  static void apply(
      main owner,
      String theme,
      Color control,
      Color nimbusBase,
      Color nimbusFocus,
      Color lightBackground,
      Color text,
      Color blueGrey,
      Color statusBar) {
    boolean resetMainClass = requiresMainReset(theme_mode);
    if (resetMainClass) {
      theme_changed = true;
      mainClass.dispose();
    }

    try {
      // Java 6 update 10 and JDK 7 use different Nimbus class packages; searching installed
      // look-and-feels keeps this compatible across those versions.
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          UIManager.put("control", control);
          UIManager.put("nimbusBase", nimbusBase);
          UIManager.put("nimbusFocus", nimbusFocus);
          UIManager.put("nimbusLightBackground", lightBackground);
          UIManager.put("text", text);
          UIManager.put("nimbusBlueGrey", blueGrey);
          SwingUtilities.updateComponentTreeUI(owner);
          jTextField4.setBackground(statusBar);
          break;
        }
      }
      theme_mode = theme;
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      String info = "Nimbus related Themes not supported on this computer";
      javax.swing.JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " message", javax.swing.JOptionPane.WARNING_MESSAGE);
      main.log_turbowin_system_message("[GENERAL] " + info);
    }

    if (resetMainClass) {
      mainClass = new main();
      mainClass.setVisible(true);
    }
  }
}
