package turbowin;

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/** Builds the main input context menu. */
final class PopupMenuWorkflow {

  private static final String[] MENU_LABELS = {
    "Date & Time...",
    "Position, Course & Speed...",
    "Barometer reading...",
    "Barograph reading...",
    "Temperatures...",
    "Wind...",
    "Waves...",
    "Visibility...",
    "Present weather...",
    "Past weather...",
    "Clouds low...",
    "Clouds middle...",
    "Clouds high...",
    "Cloud cover & height...",
    "Icing...",
    "Ice...",
    "Observer...",
    "Day colours",
    "Night colours",
    "Sunrise colours",
    "Sunset colours",
    "Transparent"
  };

  private PopupMenuWorkflow() {}

  static String[] menuLabels() {
    return MENU_LABELS.clone();
  }

  static void populate(
      main owner, JPopupMenu popup_input, JToolBar toolbar, MouseListener listener) {
    /* create pop-up menu (right mouse button) */
    JMenuItem menuItem301 = new JMenuItem(MENU_LABELS[0]);
    menuItem301.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_DateTime_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem301);

    JMenuItem menuItem302 = new JMenuItem(MENU_LABELS[1]);
    menuItem302.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Position_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem302);

    JMenuItem menuItem303 = new JMenuItem(MENU_LABELS[2]);
    menuItem303.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Barometer_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem303);

    JMenuItem menuItem304 = new JMenuItem(MENU_LABELS[3]);
    menuItem304.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Barograph_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem304);

    JMenuItem menuItem305 = new JMenuItem(MENU_LABELS[4]);
    menuItem305.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Temperatures_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem305);

    JMenuItem menuItem306 = new JMenuItem(MENU_LABELS[5]);
    menuItem306.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Wind_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem306);

    // if (!main.GUI_mode.equals(main.GUI_LIGHT))
    // {
    JMenuItem menuItem307 = new JMenuItem(MENU_LABELS[6]);
    menuItem307.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_waves_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem307);

    JMenuItem menuItem308 = new JMenuItem(MENU_LABELS[7]);
    menuItem308.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Visibility_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem308);

    JMenuItem menuItem309 = new JMenuItem(MENU_LABELS[8]);
    menuItem309.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Presentweather_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem309);

    JMenuItem menuItem310 = new JMenuItem(MENU_LABELS[9]);
    menuItem310.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Pastweather_menu_actionperformed(null);
          }
        });
    popup_input.add(menuItem310);

    JMenuItem menuItem311 = new JMenuItem(MENU_LABELS[10]);
    menuItem311.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Cloudslow_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem311);

    JMenuItem menuItem312 = new JMenuItem(MENU_LABELS[11]);
    menuItem312.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Cloudsmiddle_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem312);

    JMenuItem menuItem313 = new JMenuItem(MENU_LABELS[12]);
    menuItem313.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Cloudshigh_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem313);

    JMenuItem menuItem314 = new JMenuItem(MENU_LABELS[13]);
    menuItem314.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Cloudcover_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem314);
    // } // if (!main.GUI_mode.equals(main.GUI_LIGHT))

    JMenuItem menuItem315 = new JMenuItem(MENU_LABELS[14]);
    menuItem315.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Icing_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem315);

    JMenuItem menuItem316 = new JMenuItem(MENU_LABELS[15]);
    menuItem316.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Ice_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem316);

    JMenuItem menuItem317 = new JMenuItem(MENU_LABELS[16]);
    menuItem317.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Input_Observer_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem317);

    popup_input.addSeparator();

    JMenuItem menuItem318 = new JMenuItem(MENU_LABELS[17]);
    menuItem318.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Themes_1_actionPerformed(null);
          }
        });
    popup_input.add(menuItem318);

    JMenuItem menuItem319 = new JMenuItem(MENU_LABELS[18]);
    menuItem319.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Themes_2_actionPerformed(null);
          }
        });
    popup_input.add(menuItem319);

    JMenuItem menuItem320 = new JMenuItem(MENU_LABELS[19]);
    menuItem320.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Themes_3_actionPerformed(null);
          }
        });
    popup_input.add(menuItem320);

    JMenuItem menuItem321 = new JMenuItem(MENU_LABELS[20]);
    menuItem321.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Themes_4_actionPerformed(null);
          }
        });
    popup_input.add(menuItem321);

    JMenuItem menuItem322 = new JMenuItem(MENU_LABELS[21]);
    menuItem322.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            owner.Themes_5_actionPerformed(null);
          }
        });
    popup_input.add(menuItem322);

    owner.addMouseListener(listener); // connect to jFrame otherwise eg:
    // jTextField1.addMouseListener(popupListener);
    toolbar.addMouseListener(listener); // also connected to Toolbar now

    // in 'gui light' mode, by default, the logo (Label13 reused) do not respond to right mouse
    // click
    // if (GUI_mode.equals(GUI_LIGHT))
    // {
    //   // in GUI LIGHT mode label13 (present weather in FULL mode) was altered to the chosen logo
    // (eumetnet, noaa, sot)
    //   jLabel13.addMouseListener(popupListener_input);
    // }
  }
}
