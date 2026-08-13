package turbowin;

import static turbowin.main.*;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Handles the system-tray menu and tray-icon interactions after window minimization. */
final class SystemTrayWorkflow {

  private SystemTrayWorkflow() {}

  static boolean usesSystemTray(OSDetector.OSType osType) {
    return osType == OSDetector.OSType.WINDOWS;
  }

  static void initialize() {
    if (!SystemTray.isSupported()) {
      log_turbowin_system_message("[GENERAL] SystemTray is not supported");
    } else {
      tray = SystemTray.getSystemTray();
    }
  }

  static void handle(main owner) {
    // TODO add your handling code here:

    // NB on the latest Linux desktops (GNOME, Cinnamon, Xubuntu) issues with iconifying
    //    looks like the evt event WINDOW_ICONIFIED is send several times
    //
    boolean use_system_tray = usesSystemTray(OSDetector.detect_OS());

    if (use_system_tray) {
      // Check the SystemTray is supported
      if (!SystemTray.isSupported()) {
        System.out.println("+++ SystemTray is not supported");
      } else {
        owner.setVisible(false);

        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(owner.createImage(main.ICONS_DIRECTORY + "tray.png"));
        // final SystemTray tray = SystemTray.getSystemTray();
        tray = SystemTray.getSystemTray();

        // set tool tip text
        trayIcon.setToolTip("TurboWin+ weather observations");

        // message pop-up (left mouse click on TurboWin+ system tray icon)
        //     NB The only way to display a "dynamic" tooltip that pops up when the mouse cursor
        // hovers over the tray icon
        //
        MouseListener ml =
            new MouseListener() {
              @Override
              public void mouseClicked(MouseEvent e) {
                //// test begin ////
                // trayIcon.displayMessage("TurboWin+", "test", MessageType.INFO);
                /////// test end ////

                // if (e.getButton() == MouseEvent.BUTTON1)           // left mouse button
                if (SwingUtilities.isLeftMouseButton(
                    e)) // to be sure this is better than testing on BUTTON1
                {
                  /// test begin
                  // JOptionPane.showMessageDialog(null, "test", main.APPLICATION_NAME + " test left
                  // mouse button clicked", JOptionPane.INFORMATION_MESSAGE);
                  // trayIcon.displayMessage("TurboWin+", "test left mouse button clicked",
                  // MessageType.INFO);
                  /// test end

                  String info =
                      "no sensor data available"; // eg just after start-up (takes 1 minute to start
                  // collecting)

                  if (RS232_connection_mode == 4) // Mintaka Duo
                  {
                    // check if we receive valid data (parameter day is arbitrary, could eg also be
                    // month etc.)
                    try {
                      // NB tray icon message will be set in:
                      // main_RS232_RS422.RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs()
                      boolean local_tray_icon_clicked = true;
                      RS232_mintaka.RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs(
                          local_tray_icon_clicked); // retrieve mybarometer.pressure_reading
                    } // try
                    catch (NumberFormatException en) {
                      info =
                          "no sensor data available"; // eg just after start-up (takes 1 minute to
                      // start collecting)
                    } // catch
                  } // if (RS232_connection_mode == 4)
                  else if (RS232_connection_mode == 5
                      || RS232_connection_mode == 6) // Mintaka Star USB or Mintaka Star WiFi
                  {
                    // check if we receive valid data (parameter day is arbitrary, could eg also be
                    // month etc.)
                    try {
                      // NB tray icon message will be set in:
                      // RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs()
                      boolean local_tray_icon_clicked = true;
                      boolean StarX = false;
                      RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(
                          local_tray_icon_clicked, StarX); // retrieve mybarometer.pressure_reading
                    } // try
                    catch (NumberFormatException en) {
                      info =
                          "no sensor data available"; // eg just after start-up (takes 1 minute to
                      // start collecting)
                    } // catch
                  } // else if (RS232_connection_mode == 5 || RS232_connection_mode == 6)
                  else if (RS232_connection_mode == 7
                      || RS232_connection_mode == 8) // Mintaka StarX USB or Mintaka StarX WiFi
                  {
                    // check if we receive valid data (parameter day is arbitrary, could eg also be
                    // month etc.)
                    try {
                      // NB tray icon message will be set in:
                      // main_RS232_RS422.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs()
                      boolean local_tray_icon_clicked = true;
                      boolean StarX = true;
                      RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(
                          local_tray_icon_clicked, StarX); // retrieve mybarometer.pressure_reading
                    } // try
                    catch (NumberFormatException en) {
                      info =
                          "no sensor data available"; // eg just after start-up (takes 1 minute to
                      // start collecting)
                    } // catch
                  } // else if (RS232_connection_mode == 7 || RS232_connection_mode == 8)
                  else if ((RS232_connection_mode == 1)
                      || (RS232_connection_mode == 2)) // PTB220 or PTB330
                  {
                    // check if we receive valid data (parameter day is arbitrary, could eg also be
                    // month etc.)
                    try {
                      // NB tray icon message will be set in: RS232_Read_Sensor_Data_PPPP_For_Obs()
                      boolean local_tray_icon_clicked = true;
                      RS232_vaisala.RS232_Read_Sensor_Data_PPPP_For_Obs(
                          local_tray_icon_clicked); // retrieve mybarometer.pressure_reading
                    } // try
                    catch (NumberFormatException en) {
                      info =
                          "no sensor data available"; // eg just after start-up (takes 1 minute to
                      // start collecting)
                    } // catch
                  } // if ( (RS232_connection_mode == 1) || (RS232_connection_mode == 2) )
                  else if (((RS232_connection_mode == 3
                              || RS232_connection_mode == 9
                              || RS232_connection_mode == 11)
                          && (defaultPort != null))
                      || (RS232_connection_mode == 10)) // AWS (serial or LAB)
                  {
                    // check if we receive valid data (parameter day is arbitrary, could eg also be
                    // month etc.)
                    try {
                      int int_day = Integer.parseInt(mydatetime.day);

                      if ((int_day >= 1) && (int_day <= 31)) {

                        info = "";
                        info =
                            mydatetime.day
                                + " "
                                + mydatetime.month
                                + " "
                                + mydatetime.year
                                + "  "
                                + mydatetime.hour
                                + "."
                                + mydatetime.minute
                                + " UTC"
                                + "\n"
                                + "\n"
                                + "pressure at sensor height: "
                                + mybarometer.pressure_reading
                                + " hPa"
                                + "\n"
                                + "pressure at MSL: "
                                + mybarometer.pressure_msl_corrected
                                + " hPa"; // + "\n"; +
                        // commented out because will not be visible due to the available pop-up
                        // space
                        // "\n" +
                        // "air temp at sensor height: " + mytemp.air_temp + " C" + "\n" +
                        // "SST at sensor depth: " + mytemp.sea_water_temp + " C" + "\n" +
                        // "\n" +
                        // "true wind speed at sensor height: " + mywind.int_true_wind_speed + " " +
                        // wind_speed_units + "\n" +
                        // "true wind dir at sensor height: " + mywind.int_true_wind_dir + " degr";
                      } // if ( (int_day >= 1) && (int_day <= 31) )

                      // trayIcon.displayMessage("TurboWin+", info, MessageType.INFO);
                      JOptionPane.showMessageDialog(
                          null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);

                    } // try
                    catch (NumberFormatException en) {
                      info =
                          "no sensor data available"; // eg just after start-up (takes 1 minute to
                      // start collecting)
                      // trayIcon.displayMessage("TurboWin+", info, MessageType.INFO);
                      JOptionPane.showMessageDialog(
                          null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
                    } // catch

                  } // else if ( (RS232_connection_mode == 3 || RS232_connection_mode == 9 ||
                  // RS232_connection_mode == 11) && (defaultPort != null) )
                  else // TurboWin+ stand-alone mode (no serial connection to barometer or AWS and
                  // no WiFi)
                  {
                    info = "right click icon to maximize or exit TurboWin+";
                    // trayIcon.displayMessage("TurboWin+", info, MessageType.INFO);
                    JOptionPane.showMessageDialog(
                        null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
                  } // else
                } // if (e.getButton() == MouseEvent.BUTTON1)
              } // public void mouseClicked(MouseEvent e)

              @Override
              public void mousePressed(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body
                // of generated methods, choose Tools | Templates.
              }

              @Override
              public void mouseReleased(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body
                // of generated methods, choose Tools | Templates.
              }

              @Override
              public void mouseEntered(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body
                // of generated methods, choose Tools | Templates.
              }

              @Override
              public void mouseExited(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body
                // of generated methods, choose Tools | Templates.
              }
            }; // MouseListener ml = new MouseListener()
        trayIcon.addMouseListener(ml);

        // Create a pop-up menu components
        //

        if (((RS232_connection_mode == 3
                    || RS232_connection_mode == 9
                    || RS232_connection_mode == 11)
                && (defaultPort != null))
            || (RS232_connection_mode == 10)) // AWS connected
        {
          MenuItem dashboard_analog_Item = new MenuItem("dashboard analog");
          popup.add(dashboard_analog_Item);

          dashboard_analog_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Dashboard_AWS_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          MenuItem dashboard_digital_Item = new MenuItem("dashboard digital");
          popup.add(dashboard_digital_Item);

          dashboard_digital_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Dashboard_AWS_digital_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          MenuItem dashboard_hybrid_Item = new MenuItem("dashboard hybrid");
          popup.add(dashboard_hybrid_Item);

          dashboard_hybrid_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Dashboard_AWS_hybrid_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          MenuItem dashboard_radar_Item = new MenuItem("dashboard wind radar");
          popup.add(dashboard_radar_Item);

          dashboard_radar_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Dashboard_AWS_radar_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          popup.addSeparator();

          MenuItem pressure_graph_Item = new MenuItem("pressure graph");
          MenuItem wind_dir_graph_Item = new MenuItem("wind direction graph");
          MenuItem wind_speed_graph_Item = new MenuItem("wind speed graph");
          MenuItem sst_graph_Item = new MenuItem("SST graph");
          MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
          MenuItem total_graph_Item = new MenuItem("total graph");

          popup.add(pressure_graph_Item);
          popup.add(wind_dir_graph_Item);
          popup.add(wind_speed_graph_Item);
          popup.add(sst_graph_Item);
          popup.add(air_temp_graph_Item);
          popup.add(total_graph_Item);

          popup.addSeparator();

          // air pressure graph
          //
          pressure_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_Pressure_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          // wind dir graph
          //
          wind_dir_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graph_Wind_Dir_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          // wind speed graph
          //
          wind_speed_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_Wind_Speed_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          // SST graph
          //
          sst_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_SST_Sensor_data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          // air temp graph
          //
          air_temp_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_Airtemp_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          // total graph (pressure, air temp, wind dir and wind speed in one total graph)
          //
          total_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graph_All_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });
        } // if ( (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode
        // == 11) && (defaultPort != null) )

        if (((RS232_connection_mode == 1)
                || (RS232_connection_mode == 2)
                || (RS232_connection_mode == 4)
                || (RS232_connection_mode == 5))
            && (defaultPort != null)) // PTB220 or PTB330 or Mintaka Duo or Mintaka Star USB
        {
          MenuItem dashboard_Item = new MenuItem("dashboard barometer");
          popup.add(dashboard_Item);

          popup.addSeparator();

          dashboard_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Dashboard_Barometer_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          MenuItem pressure_graph_Item = new MenuItem("pressure graph");
          popup.add(pressure_graph_Item);

          // popup.addSeparator();

          pressure_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_Pressure_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          if ((RS232_connection_mode_II == 1)
              && (defaultPort_II != null)) // also a HMP155 connected
          {
            MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
            popup.add(air_temp_graph_Item);

            air_temp_graph_Item.addActionListener(
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    owner.Graphs_Airtemp_Sensor_Data_actionPerformed(null);
                  } // public void actionPerformed(ActionEvent e)
                });
          }

          popup.addSeparator();
        } // if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) ||
        // (RS232_connection_mode == 4) || (RS232_connection_mode == 5)) && (defaultPort != null)
        // ) // PTB220 or PTB330 or Mintaka Duo

        if (RS232_connection_mode == 6) // Mintaka Star LAN
        {
          MenuItem dashboard_Item = new MenuItem("dashboard barometer");
          popup.add(dashboard_Item);

          popup.addSeparator();

          dashboard_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Dashboard_Barometer_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          MenuItem pressure_graph_Item = new MenuItem("pressure graph");
          popup.add(pressure_graph_Item);

          // popup.addSeparator();

          pressure_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_Pressure_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          if ((RS232_connection_mode_II == 1)
              && (defaultPort_II != null)) // also a HMP155 connected
          {
            MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
            popup.add(air_temp_graph_Item);

            air_temp_graph_Item.addActionListener(
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    owner.Graphs_Airtemp_Sensor_Data_actionPerformed(null);
                  } // public void actionPerformed(ActionEvent e)
                });
          }

          popup.addSeparator();
        } // if (RS232_connection_mode == 6)

        if (((RS232_connection_mode == 7) && (defaultPort != null))
            || (RS232_connection_mode == 8)) // Mintaka StarX
        {
          MenuItem dashboard_Item = new MenuItem("dashboard barometer");
          popup.add(dashboard_Item);

          popup.addSeparator();

          dashboard_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Dashboard_Barometer_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          MenuItem pressure_graph_Item = new MenuItem("pressure graph");
          popup.add(pressure_graph_Item);

          MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
          popup.add(air_temp_graph_Item);

          popup.addSeparator();

          pressure_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_Pressure_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          air_temp_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_Airtemp_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });
        } // if ( ((RS232_connection_mode == 7) && (defaultPort != null)) || (RS232_connection_mode
        // == 8) )

        if ((RS232_connection_mode == 0)
            && (RS232_connection_mode_II == 1)
            && (defaultPort_II
                != null)) // no 1st instrument (barometer) but one 2nd instrument (temperature
        // device)
        {
          MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
          popup.add(air_temp_graph_Item);

          air_temp_graph_Item.addActionListener(
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  owner.Graphs_Airtemp_Sensor_Data_actionPerformed(null);
                } // public void actionPerformed(ActionEvent e)
              });

          popup.addSeparator();
        } // if ((RS232_connection_mode == 0) && (RS232_connection_mode_II == 1) && (defaultPort_II
        // != null))

        // menu items 'exit' and 'restore (maximize)' always present!
        //
        MenuItem restoreItem = new MenuItem("Maximize TurboWin+");
        popup.add(restoreItem);
        MenuItem exitItem = new MenuItem("Exit TurboWin+");
        popup.add(exitItem);

        // exit program
        //
        exitItem.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                owner.main_windowClosing(null);
              } // public void actionPerformed(ActionEvent e)
            });

        // restore (maximize main screen)
        //
        restoreItem.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                owner.setExtendedState(NORMAL);
                // restoring old windows state
                // int state = getExtendedState();
                // state = state & ~turbowin.main.ICONIFIED;
                // owner.setExtendedState(state);

                owner.setVisible(true);
                tray.remove(trayIcon);

                // NB there will be no deiconified windows system message! (in case of the system
                // tray)
                owner.main_window_updating_date_time();
              } // public void actionPerformed(ActionEvent e)
            });

        trayIcon.setPopupMenu(popup);

        try {
          // if (tray != null && trayIcon != null)
          // {
          tray.add(trayIcon);
          // }
          // trayIcon.displayMessage("TurboWin+", "test", MessageType.INFO);
        } catch (AWTException e) {
          System.out.println("+++ TrayIcon could not be added.");
        }
      } // else (so system tray available)
    } // if (use_system_tray)
  }
}
