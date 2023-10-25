/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbowin;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author marti
 */
public class DASHBOARD_view_AWS_radar extends javax.swing.JFrame 
{

   /**
    * Creates new form DASHBOARD_view_AWS_radar
    */
   
   /* inner class popupListener */
   class PopupListener extends MouseAdapter 
   {
      @Override
      public void mousePressed(MouseEvent e) 
      {
         maybeShowPopup(e);
         //System.out.println("Popup menu will be visible!");
      }
      @Override
      public void mouseReleased(MouseEvent e) 
      {
         maybeShowPopup(e);
      }

      private void maybeShowPopup(MouseEvent e) 
      {
         if (e.isPopupTrigger()) 
         {
            popup.show(e.getComponent(), e.getX(), e.getY());
         }
      }
   }      
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void save_ship_dashboard_selection()
   {
      // write meta data to configuration file
      //main.ship_deck_color_String = Integer.toString(deck_color.getRGB());   // save color as String
      if (DASHBOARD_view_AWS_radar.deck_color != null)
      {
         main.ship_deck_color_String = Integer.toString(DASHBOARD_view_AWS_radar.deck_color.getRGB());   // save color as String 
      }  
      
      if (DASHBOARD_view_AWS_radar.tank_color != null)   
      {
         main.ship_tank_color_String = Integer.toString(DASHBOARD_view_AWS_radar.tank_color.getRGB());   // save color as String 
      }  
      
      if (main.offline_mode_via_cmd == true)                                 // also if the turbowin_launcher is present (JPMS)
      {
         main.schrijf_configuratie_regels();          
      }
      else // so offline_via_jnlp mode or online (webstart) mode
      {
         main.schrijf_configuratie_regels();
      }  
   }

   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void initComponents1()
   {
      // this dashboard could be set to automatically (by the system) opening
      if (isAlwaysOnTopSupported() && (main.dashboard_was_automatically_opened == true))
      {
         try
         {
            setAlwaysOnTop(true);
            System.out.println("--- AWS Dashboard opened and set to 'Always On Top'");
            
            // move mouse pointer to centre screen to deactivate screensaver if screensaver is active
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int centerX = screenSize.width / 2;
            int centerY = screenSize.height / 2;
            Robot robot = null;
            try 
            {
               robot = new Robot();
               robot.mouseMove(centerX, centerY);
            } 
            catch (AWTException e) 
            {
               main.log_turbowin_system_message("[GENERAL] pop-up AWS dashboard: error mouse move for deactivating screensaver (" + e.getCause().toString() + ")");
            }
         }
         catch (SecurityException ex)
         {
            main.log_turbowin_system_message("[GENERAL] pop-up AWS dashboard: there is no permission to set the value of the always-on-top property (" + ex.getCause().toString() + ")");
         }
      }

      
      /* background color main panel (set by main menu theme option) */
      if (main.theme_mode.equals(main.THEME_NIMBUS_NIGHT))
      {
         night_vision = true;
         
         background_color_panel1 = jPanel1.getBackground();
         //background_color_panel1 = Color.LIGHT_GRAY;
         background_color_panel2 = Color.LIGHT_GRAY;
         background_color_panel3 = Color.LIGHT_GRAY;
         background_color_panel4 = Color.LIGHT_GRAY;
         background_color_panel5 = Color.LIGHT_GRAY;
              
         jPanel1.setBackground(Color.DARK_GRAY);
         jPanel2.setBackground(Color.BLACK);
         jPanel3.setBackground(Color.BLACK);
         jPanel4.setBackground(Color.BLACK);
         jPanel5.setBackground(Color.BLACK); 
      }
      else
      {
         night_vision = false;
         
         //background_color_panel1 = jPanel1.getBackground();
          background_color_panel1 = jPanel4.getBackground();    // jPanel4 = left panel (but could also be another (site)panel)
         jPanel1.setBackground(background_color_panel1); 
         
         background_color_panel2 = jPanel2.getBackground();
         background_color_panel3 = jPanel3.getBackground();
         background_color_panel4 = jPanel4.getBackground();
         background_color_panel5 = jPanel5.getBackground();
      } 
   
      
      /* background color main panel (set by popup menu option) */
      popup = new JPopupMenu();
      
      JMenuItem menuItem = new JMenuItem("night colors");
      menuItem.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            night_vision = true;
       
            jPanel1.setBackground(Color.DARK_GRAY); 
            jPanel2.setBackground(Color.BLACK);
            jPanel3.setBackground(Color.BLACK);
            jPanel4.setBackground(Color.BLACK);
            jPanel5.setBackground(Color.BLACK); 
         }
      });
      popup.add(menuItem);
      
      JMenuItem menuItem2 = new JMenuItem("day colors");
      menuItem2.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            night_vision = false;
            
            jPanel1.setBackground(background_color_panel1); 
            jPanel2.setBackground(background_color_panel2);
            jPanel3.setBackground(background_color_panel3);
            jPanel4.setBackground(background_color_panel4);
            jPanel5.setBackground(background_color_panel5); 
         }
      });
      popup.add(menuItem2);  
      
      popup.add(new JSeparator()); // SEPARATOR
     

      //
      //////////////////////// container ship /////////////////
      //      
      JMenu menuItem3 = new JMenu("container ship");
      popup.add(menuItem3); 

      JMenuItem menuItem3_container_color_various = new JMenuItem("container color various");
      menuItem3.add(menuItem3_container_color_various);

      menuItem3_container_color_various.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_VARIOUS;                                   
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });
 
      JMenuItem menuItem3_container_color_white = new JMenuItem("container color white");
      menuItem3.add(menuItem3_container_color_white);

      menuItem3_container_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_WHITE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });      
      
      JMenuItem menuItem3_container_color_light_blue = new JMenuItem("container color light blue");
      menuItem3.add(menuItem3_container_color_light_blue);

      menuItem3_container_color_light_blue.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_LIGHT_BLUE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });            
      
      JMenuItem menuItem3_container_color_blue = new JMenuItem("container color blue");
      menuItem3.add(menuItem3_container_color_blue);

      menuItem3_container_color_blue.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_BLUE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });         
      
      JMenuItem menuItem3_container_color_dark_blue = new JMenuItem("container color dark blue");
      menuItem3.add(menuItem3_container_color_dark_blue);

      menuItem3_container_color_dark_blue.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_DARK_BLUE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });         
      
      JMenuItem menuItem3_container_color_green = new JMenuItem("container color green");
      menuItem3.add(menuItem3_container_color_green);
      
      menuItem3_container_color_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_GREEN;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });         
      
      JMenuItem menuItem3_container_color_gray = new JMenuItem("container color gray");
      menuItem3.add(menuItem3_container_color_gray);
      
      menuItem3_container_color_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_GRAY;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      
      JMenuItem menuItem3_container_color_magenta = new JMenuItem("container color magenta");
      menuItem3.add(menuItem3_container_color_magenta);
      
      menuItem3_container_color_magenta.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_MAGENTA;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      
      JMenuItem menuItem3_container_color_yellow = new JMenuItem("container color yellow");
      menuItem3.add(menuItem3_container_color_yellow);
      
      menuItem3_container_color_yellow.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_YELLOW;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      
      JMenuItem menuItem3_container_color_orange = new JMenuItem("container color orange");
      menuItem3.add(menuItem3_container_color_orange);
      
      menuItem3_container_color_orange.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_ORANGE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      
      JMenuItem menuItem3_container_color_maroon = new JMenuItem("container color maroon");
      menuItem3.add(menuItem3_container_color_maroon);
      
      menuItem3_container_color_maroon.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_MAROON;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      

      
      //
      //////////////////////// container ship II /////////////////
      //
/*      
      JMenuItem menuItem13 = new JMenuItem("container ship II");
      menuItem13.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem13);  
*/      
      JMenu menuItem13 = new JMenu("container ship II");
      popup.add(menuItem13); 

      JMenuItem menuItem13_container_color_various = new JMenuItem("container color various");
      menuItem13.add(menuItem13_container_color_various);

      menuItem13_container_color_various.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_VARIOUS;                                   
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });
 
      JMenuItem menuItem13_container_color_white = new JMenuItem("container color white");
      menuItem13.add(menuItem13_container_color_white);

      menuItem13_container_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_WHITE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });      
      
      JMenuItem menuItem13_container_color_light_blue = new JMenuItem("container color light blue");
      menuItem13.add(menuItem13_container_color_light_blue);

      menuItem13_container_color_light_blue.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_LIGHT_BLUE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });            
      
      JMenuItem menuItem13_container_color_blue = new JMenuItem("container color blue");
      menuItem13.add(menuItem13_container_color_blue);

      menuItem13_container_color_blue.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_BLUE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });         
      
      JMenuItem menuItem13_container_color_dark_blue = new JMenuItem("container color dark blue");
      menuItem13.add(menuItem13_container_color_dark_blue);

      menuItem13_container_color_dark_blue.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_DARK_BLUE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });         
      
      JMenuItem menuItem13_container_color_green = new JMenuItem("container color green");
      menuItem13.add(menuItem13_container_color_green);
      
      menuItem13_container_color_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_GREEN;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });         
      
      JMenuItem menuItem13_container_color_gray = new JMenuItem("container color gray");
      menuItem13.add(menuItem13_container_color_gray);
      
      menuItem13_container_color_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_GRAY;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      
      JMenuItem menuItem13_container_color_magenta = new JMenuItem("container color magenta");
      menuItem13.add(menuItem13_container_color_magenta);
      
      menuItem13_container_color_magenta.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_MAGENTA;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      
      JMenuItem menuItem13_container_color_yellow = new JMenuItem("container color yellow");
      menuItem13.add(menuItem13_container_color_yellow);
      
      menuItem13_container_color_yellow.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_YELLOW;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      
      JMenuItem menuItem13_container_color_orange = new JMenuItem("container color orange");
      menuItem13.add(menuItem13_container_color_orange);
      
      menuItem13_container_color_orange.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_ORANGE;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });              
      
      JMenuItem menuItem13_container_color_maroon = new JMenuItem("container color maroon");
      menuItem13.add(menuItem13_container_color_maroon);
      
      menuItem13_container_color_maroon.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = ship.CONTAINER_COLOR_MAROON;                                    
            main.ship_type_dashboard = main.CONTAINER_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });              

      
      //
      //////////////////////// bulk carrier /////////////////
      //
      JMenu menuItem4 = new JMenu("bulk carrier");
      popup.add(menuItem4); 

      JMenuItem menuItem4_deck_color_brown = new JMenuItem("deck color brown");
      menuItem4.add(menuItem4_deck_color_brown);

      menuItem4_deck_color_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BROWN;                      
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
 
      JMenuItem menuItem4_deck_color_red_brown = new JMenuItem("deck color red-brown");
      menuItem4.add(menuItem4_deck_color_red_brown);

      menuItem4_deck_color_red_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED_BROWN;                          
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem4_deck_color_red = new JMenuItem("deck color red");
      menuItem4.add(menuItem4_deck_color_red);

      menuItem4_deck_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED; 
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem4_deck_color_green = new JMenuItem("deck color green");
      menuItem4.add(menuItem4_deck_color_green);

      menuItem4_deck_color_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GREEN;                                  // green-blue
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem4_deck_color_light_green = new JMenuItem("deck color light-green");
      menuItem4.add(menuItem4_deck_color_light_green);

      menuItem4_deck_color_light_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GREEN;                                                           
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem4_deck_color_orange = new JMenuItem("deck color orange");
      menuItem4.add(menuItem4_deck_color_orange);

      menuItem4_deck_color_orange.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_ORANGE; 
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem4_deck_color_yellow = new JMenuItem("deck color yellow");
      menuItem4.add(menuItem4_deck_color_yellow);

      menuItem4_deck_color_yellow.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_YELLOW;
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem4_deck_color_gray_brown = new JMenuItem("deck color gray-brown");
      menuItem4.add(menuItem4_deck_color_gray_brown);

      menuItem4_deck_color_gray_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY_BROWN;
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });


      JMenuItem menuItem4_deck_color_gray = new JMenuItem("deck color gray");
      menuItem4.add(menuItem4_deck_color_gray);

      menuItem4_deck_color_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY;
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem4_deck_color_light_gray = new JMenuItem("deck color light-gray");
      menuItem4.add(menuItem4_deck_color_light_gray);

      menuItem4_deck_color_light_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GRAY;
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem4_deck_color_dark_gray = new JMenuItem("deck color dark-gray");
      menuItem4.add(menuItem4_deck_color_dark_gray);

      menuItem4_deck_color_dark_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_DARK_GRAY;
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem4_deck_color_blue_gray = new JMenuItem("deck color blue-gray");
      menuItem4.add(menuItem4_deck_color_blue_gray);

      menuItem4_deck_color_blue_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BLUE_GRAY; 
            main.ship_type_dashboard = main.BULK_CARRIER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      
      //
      //////////////////////// oil tanker /////////////////
      //
      JMenu menuItem5 = new JMenu("oil tanker");
      popup.add(menuItem5); 

      JMenuItem menuItem5_deck_color_brown = new JMenuItem("deck color brown");
      menuItem5.add(menuItem5_deck_color_brown);

      menuItem5_deck_color_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BROWN;                      
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
 
      JMenuItem menuItem5_deck_color_red_brown = new JMenuItem("deck color red-brown");
      menuItem5.add(menuItem5_deck_color_red_brown);

      menuItem5_deck_color_red_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED_BROWN;                          
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem5_deck_color_red = new JMenuItem("deck color red");
      menuItem5.add(menuItem5_deck_color_red);

      menuItem5_deck_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED; 
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem5_deck_color_green = new JMenuItem("deck color green");
      menuItem5.add(menuItem5_deck_color_green);

      menuItem5_deck_color_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GREEN;                                  // green-blue
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem5_deck_color_light_green = new JMenuItem("deck color light-green");
      menuItem5.add(menuItem5_deck_color_light_green);

      menuItem5_deck_color_light_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GREEN;                                                           
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem5_deck_color_orange = new JMenuItem("deck color orange");
      menuItem5.add(menuItem5_deck_color_orange);

      menuItem5_deck_color_orange.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_ORANGE; 
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem5_deck_color_yellow = new JMenuItem("deck color yellow");
      menuItem5.add(menuItem5_deck_color_yellow);

      menuItem5_deck_color_yellow.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_YELLOW;
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem5_deck_color_gray_brown = new JMenuItem("deck color gray-brown");
      menuItem5.add(menuItem5_deck_color_gray_brown);

      menuItem5_deck_color_gray_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY_BROWN;
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });


      JMenuItem menuItem5_deck_color_gray = new JMenuItem("deck color gray");
      menuItem5.add(menuItem5_deck_color_gray);

      menuItem5_deck_color_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY;
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem5_deck_color_light_gray = new JMenuItem("deck color light-gray");
      menuItem5.add(menuItem5_deck_color_light_gray);

      menuItem5_deck_color_light_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GRAY;
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem5_deck_color_dark_gray = new JMenuItem("deck color dark-gray");
      menuItem5.add(menuItem5_deck_color_dark_gray);

      menuItem5_deck_color_dark_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_DARK_GRAY;
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem5_deck_color_blue_gray = new JMenuItem("deck color blue-gray");
      menuItem5.add(menuItem5_deck_color_blue_gray);

      menuItem5_deck_color_blue_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BLUE_GRAY; 
            main.ship_type_dashboard = main.OIL_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      
      //
      //////////////////////// LNG tanker /////////////////
      //
      JMenu menuItem6 = new JMenu("LNG tanker");
      popup.add(menuItem6); 

      JMenuItem menuItem6_deck_color_brown_tank_color_red = new JMenuItem("deck color brown; tanks red");
      menuItem6.add(menuItem6_deck_color_brown_tank_color_red);

      menuItem6_deck_color_brown_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BROWN; 
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
 
      JMenuItem menuItem6_deck_color_red_brown_tank_color_red = new JMenuItem("deck color red-brown; tanks red");
      menuItem6.add(menuItem6_deck_color_red_brown_tank_color_red);

      menuItem6_deck_color_red_brown_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED_BROWN;  
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem6_deck_color_red_tank_color_red = new JMenuItem("deck color red; tanks red");
      menuItem6.add(menuItem6_deck_color_red_tank_color_red);

      menuItem6_deck_color_red_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED; 
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem6_deck_color_green_tank_color_red = new JMenuItem("deck color green; tanks red");
      menuItem6.add(menuItem6_deck_color_green_tank_color_red);

      menuItem6_deck_color_green_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GREEN;                                  // green-blue
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem6_deck_color_light_green_tank_color_red = new JMenuItem("deck color light-green; tanks red");
      menuItem6.add(menuItem6_deck_color_light_green_tank_color_red);

      menuItem6_deck_color_light_green_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GREEN;  
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      //JMenuItem menuItem6_deck_color_orange_tank_color_red = new JMenuItem("deck color orange; tanks red");
      //menuItem6.add(menuItem6_deck_color_orange_tank_color_red);
      //
      //menuItem6_deck_color_orange_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      //{
      //   @Override
      //   public void actionPerformed(ActionEvent e) 
      //   {
      //      deck_color = DECK_COLOR_ORANGE; 
      //      tank_color = TANK_COLOR_RED;
      //      main.ship_type_dashboard = main.LNG_TANKER;
      //      save_ship_dashboard_selection();
      //      repaint();
      //   }
      //});
      
      //JMenuItem menuItem6_deck_color_yellow_tank_color_red = new JMenuItem("deck color yellow; tanks red");
      //menuItem6.add(menuItem6_deck_color_yellow_tank_color_red);
      //
      //menuItem6_deck_color_yellow_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      //{
      //   @Override
      //   public void actionPerformed(ActionEvent e) 
      //   {
      //      deck_color = DECK_COLOR_YELLOW;
      //      tank_color = TANK_COLOR_RED;
      //      main.ship_type_dashboard = main.LNG_TANKER;
      //      save_ship_dashboard_selection();
      //      repaint();
      //   }
      //});
      
      JMenuItem menuItem6_deck_color_gray_brown_tank_color_red = new JMenuItem("deck color gray-brown; tanks red");
      menuItem6.add(menuItem6_deck_color_gray_brown_tank_color_red);

      menuItem6_deck_color_gray_brown_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY_BROWN;
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });


      JMenuItem menuItem6_deck_color_gray_tank_color_red = new JMenuItem("deck color gray; tanks red");
      menuItem6.add(menuItem6_deck_color_gray_tank_color_red);

      menuItem6_deck_color_gray_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY;
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem6_deck_color_light_gray_tank_color_red = new JMenuItem("deck color light-gray; tanks red");
      menuItem6.add(menuItem6_deck_color_light_gray_tank_color_red);

      menuItem6_deck_color_light_gray_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GRAY;
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem6_deck_color_dark_gray_tank_color_red = new JMenuItem("deck color dark-gray; tanks red");
      menuItem6.add(menuItem6_deck_color_dark_gray_tank_color_red);

      menuItem6_deck_color_dark_gray_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_DARK_GRAY;
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem6_deck_color_blue_gray_tank_color_red = new JMenuItem("deck color blue-gray; tanks red");
      menuItem6.add(menuItem6_deck_color_blue_gray_tank_color_red);

      menuItem6_deck_color_blue_gray_tank_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BLUE_GRAY; 
            tank_color = TANK_COLOR_RED;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      
///////////////////////// ........ ///////////////////

      // menuitem seperator
      menuItem6.addSeparator();

      JMenuItem menuItem6_deck_color_brown_tank_color_white = new JMenuItem("deck color brown; tanks white");
      menuItem6.add(menuItem6_deck_color_brown_tank_color_white);

      menuItem6_deck_color_brown_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BROWN;  
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
 
      JMenuItem menuItem6_deck_color_red_brown_tank_color_white = new JMenuItem("deck color red-brown; tanks white");
      menuItem6.add(menuItem6_deck_color_red_brown_tank_color_white);

      menuItem6_deck_color_red_brown_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED_BROWN;  
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem6_deck_color_red_tank_color_white = new JMenuItem("deck color red; tanks white");
      menuItem6.add(menuItem6_deck_color_red_tank_color_white);

      menuItem6_deck_color_red_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED; 
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem6_deck_color_green_tank_color_white = new JMenuItem("deck color green; tanks white");
      menuItem6.add(menuItem6_deck_color_green_tank_color_white);

      menuItem6_deck_color_green_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GREEN;                                  // green-blue
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem6_deck_color_light_green_tank_color_white = new JMenuItem("deck color light-green; tanks white");
      menuItem6.add(menuItem6_deck_color_light_green_tank_color_white);

      menuItem6_deck_color_light_green_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GREEN;   
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      //JMenuItem menuItem6_deck_color_orange_tank_color_white = new JMenuItem("deck color orange; tanks white");
      //menuItem6.add(menuItem6_deck_color_orange_tank_color_white);
      //
      //menuItem6_deck_color_orange_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      //{
      //   @Override
      //   public void actionPerformed(ActionEvent e) 
      //   {
      //      deck_color = DECK_COLOR_ORANGE; 
      //      tank_color = TANK_COLOR_WHITE;
      //      main.ship_type_dashboard = main.LNG_TANKER;
      //      save_ship_dashboard_selection();
      //      repaint();
      //   }
      //});
      
      //JMenuItem menuItem6_deck_color_yellow_tank_color_white = new JMenuItem("deck color yellow; tanks white");
      //menuItem6.add(menuItem6_deck_color_yellow_tank_color_white);
      //
      //menuItem6_deck_color_yellow_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      //{
      //   @Override
      //   public void actionPerformed(ActionEvent e) 
      //   {
      //      deck_color = DECK_COLOR_YELLOW;
      //      tank_color = TANK_COLOR_WHITE;
      //      main.ship_type_dashboard = main.LNG_TANKER;
      //      save_ship_dashboard_selection();
      //      repaint();
      //   }
      //});
      
      JMenuItem menuItem6_deck_color_gray_brown_tank_color_white = new JMenuItem("deck color gray-brown; tanks white");
      menuItem6.add(menuItem6_deck_color_gray_brown_tank_color_white);

      menuItem6_deck_color_gray_brown_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY_BROWN;
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });


      JMenuItem menuItem6_deck_color_gray_tank_color_white = new JMenuItem("deck color gray; tanks white");
      menuItem6.add(menuItem6_deck_color_gray_tank_color_white);

      menuItem6_deck_color_gray_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY;
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem6_deck_color_light_gray_tank_color_white = new JMenuItem("deck color light-gray; tanks white");
      menuItem6.add(menuItem6_deck_color_light_gray_tank_color_white);

      menuItem6_deck_color_light_gray_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GRAY;
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem6_deck_color_dark_gray_tank_color_white = new JMenuItem("deck color dark-gray; tanks white");
      menuItem6.add(menuItem6_deck_color_dark_gray_tank_color_white);

      menuItem6_deck_color_dark_gray_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_DARK_GRAY;
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem6_deck_color_blue_gray_tank_color_white = new JMenuItem("deck color blue-gray; tanks white");
      menuItem6.add(menuItem6_deck_color_blue_gray_tank_color_white);

      menuItem6_deck_color_blue_gray_tank_color_white.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BLUE_GRAY; 
            tank_color = TANK_COLOR_WHITE;
            main.ship_type_dashboard = main.LNG_TANKER;
            save_ship_dashboard_selection();
            repaint();
         }
      });




////////////////////////// ......... ///////////////////////////





      
      //
      //////////////////////// LNG tanker II /////////////////
      //
      JMenu menuItem17 = new JMenu("LNG tanker II");
      popup.add(menuItem17); 

      JMenuItem menuItem17_deck_color_brown = new JMenuItem("deck color brown");
      menuItem17.add(menuItem17_deck_color_brown);

      menuItem17_deck_color_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BROWN;                      
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });
 
      JMenuItem menuItem17_deck_color_red_brown = new JMenuItem("deck color red-brown");
      menuItem17.add(menuItem17_deck_color_red_brown);

      menuItem17_deck_color_red_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED_BROWN;                          
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem17_deck_color_red = new JMenuItem("deck color red");
      menuItem17.add(menuItem17_deck_color_red);

      menuItem17_deck_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED; 
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem17_deck_color_green = new JMenuItem("deck color green");
      menuItem17.add(menuItem17_deck_color_green);

      menuItem17_deck_color_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GREEN;                                  // green-blue
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem17_deck_color_light_green = new JMenuItem("deck color light-green");
      menuItem17.add(menuItem17_deck_color_light_green);

      menuItem17_deck_color_light_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GREEN;                                                           
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem17_deck_color_orange = new JMenuItem("deck color orange");
      menuItem17.add(menuItem17_deck_color_orange);

      menuItem17_deck_color_orange.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_ORANGE; 
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem17_deck_color_yellow = new JMenuItem("deck color yellow");
      menuItem17.add(menuItem17_deck_color_yellow);

      menuItem17_deck_color_yellow.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_YELLOW;
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem17_deck_color_gray_brown = new JMenuItem("deck color gray-brown");
      menuItem17.add(menuItem17_deck_color_gray_brown);

      menuItem17_deck_color_gray_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY_BROWN;
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });


      JMenuItem menuItem17_deck_color_gray = new JMenuItem("deck color gray");
      menuItem17.add(menuItem17_deck_color_gray);

      menuItem17_deck_color_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY;
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem17_deck_color_light_gray = new JMenuItem("deck color light-gray");
      menuItem17.add(menuItem17_deck_color_light_gray);

      menuItem17_deck_color_light_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GRAY;
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem17_deck_color_dark_gray = new JMenuItem("deck color dark-gray");
      menuItem17.add(menuItem17_deck_color_dark_gray);

      menuItem17_deck_color_dark_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_DARK_GRAY;
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem17_deck_color_blue_gray = new JMenuItem("deck color blue-gray");
      menuItem17.add(menuItem17_deck_color_blue_gray);

      menuItem17_deck_color_blue_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BLUE_GRAY; 
            main.ship_type_dashboard = main.LNG_TANKER_II;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      
      //
      //////////////////////// general cargo ship /////////////////
      //
      JMenu menuItem9 = new JMenu("general cargo ship");
      popup.add(menuItem9); 

      JMenuItem menuItem9_deck_color_brown = new JMenuItem("deck color brown");
      menuItem9.add(menuItem9_deck_color_brown);

      menuItem9_deck_color_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BROWN;                      
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem9_deck_color_red_brown = new JMenuItem("deck color red-brown");
      menuItem9.add(menuItem9_deck_color_red_brown);

      menuItem9_deck_color_red_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED_BROWN;                          
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem9_deck_color_red = new JMenuItem("deck color red");
      menuItem9.add(menuItem9_deck_color_red);

      menuItem9_deck_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED; 
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem9_deck_color_green = new JMenuItem("deck color green");
      menuItem9.add(menuItem9_deck_color_green);

      menuItem9_deck_color_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GREEN;                                  // green-blue
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem9_deck_color_light_green = new JMenuItem("deck color light-green");
      menuItem9.add(menuItem9_deck_color_light_green);

      menuItem9_deck_color_light_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GREEN;                                                           
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem9_deck_color_orange = new JMenuItem("deck color orange");
      menuItem9.add(menuItem9_deck_color_orange);

      menuItem9_deck_color_orange.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_ORANGE; 
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem9_deck_color_yellow = new JMenuItem("deck color yellow");
      menuItem9.add(menuItem9_deck_color_yellow);

      menuItem9_deck_color_yellow.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_YELLOW;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem9_deck_color_gray_brown = new JMenuItem("deck color gray-brown");
      menuItem9.add(menuItem9_deck_color_gray_brown);

      menuItem9_deck_color_gray_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY_BROWN;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });


      JMenuItem menuItem9_deck_color_gray = new JMenuItem("deck color gray");
      menuItem9.add(menuItem9_deck_color_gray);

      menuItem9_deck_color_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem9_deck_color_light_gray = new JMenuItem("deck color light-gray");
      menuItem9.add(menuItem9_deck_color_light_gray);

      menuItem9_deck_color_light_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GRAY;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem9_deck_color_dark_gray = new JMenuItem("deck color dark-gray");
      menuItem9.add(menuItem9_deck_color_dark_gray);

      menuItem9_deck_color_dark_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_DARK_GRAY;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem9_deck_color_blue_gray = new JMenuItem("deck color blue-gray");
      menuItem9.add(menuItem9_deck_color_blue_gray);

      menuItem9_deck_color_blue_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BLUE_GRAY; 
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            save_ship_dashboard_selection();
            repaint();
         }
      });

    
      //
      //////////////////////// general cargo II /////////////////
      //
      JMenu menuItem16 = new JMenu("general cargo ship II");
      popup.add(menuItem16); 

      JMenuItem menuItem16_deck_color_brown = new JMenuItem("deck color brown");
      menuItem16.add(menuItem16_deck_color_brown);

      menuItem16_deck_color_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BROWN;                      
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem16_deck_color_red_brown = new JMenuItem("deck color red-brown");
      menuItem16.add(menuItem16_deck_color_red_brown);

      menuItem16_deck_color_red_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED_BROWN;                          
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem16_deck_color_red = new JMenuItem("deck color red");
      menuItem16.add(menuItem16_deck_color_red);

      menuItem16_deck_color_red.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_RED; 
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem16_deck_color_green = new JMenuItem("deck color green");
      menuItem16.add(menuItem16_deck_color_green);

      menuItem16_deck_color_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GREEN;                                  // green-blue
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem16_deck_color_light_green = new JMenuItem("deck color light-green");
      menuItem16.add(menuItem16_deck_color_light_green);

      menuItem16_deck_color_light_green.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GREEN;                                                           
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem16_deck_color_orange = new JMenuItem("deck color orange");
      menuItem16.add(menuItem16_deck_color_orange);

      menuItem16_deck_color_orange.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_ORANGE; 
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem16_deck_color_yellow = new JMenuItem("deck color yellow");
      menuItem16.add(menuItem16_deck_color_yellow);

      menuItem16_deck_color_yellow.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_YELLOW;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem16_deck_color_gray_brown = new JMenuItem("deck color gray-brown");
      menuItem16.add(menuItem16_deck_color_gray_brown);

      menuItem16_deck_color_gray_brown.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY_BROWN;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });


      JMenuItem menuItem16_deck_color_gray = new JMenuItem("deck color gray");
      menuItem16.add(menuItem16_deck_color_gray);

      menuItem16_deck_color_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_GRAY;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem16_deck_color_light_gray = new JMenuItem("deck color light-gray");
      menuItem16.add(menuItem16_deck_color_light_gray);

      menuItem16_deck_color_light_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_LIGHT_GRAY;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      JMenuItem menuItem16_deck_color_dark_gray = new JMenuItem("deck color dark-gray");
      menuItem16.add(menuItem16_deck_color_dark_gray);

      menuItem16_deck_color_dark_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_DARK_GRAY;
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });
      
      JMenuItem menuItem16_deck_color_blue_gray = new JMenuItem("deck color blue-gray");
      menuItem16.add(menuItem16_deck_color_blue_gray);

      menuItem16_deck_color_blue_gray.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            deck_color = DECK_COLOR_BLUE_GRAY; 
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            save_ship_dashboard_selection();
            repaint();
         }
      });

      
      //
      //////////////////////// passenger ship /////////////////
      //
      JMenuItem menuItem7 = new JMenuItem("passenger ship");
      menuItem7.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.PASSENGER_SHIP;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem7); 
      
      
      //
      //////////////////////// neutral ship /////////////////
      //
      JMenuItem menuItem8 = new JMenuItem("neutral ship");
      menuItem8.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.NEUTRAL_SHIP;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem8);  
      
      
      //
      //////////////////////// research vessel /////////////////
      //
      JMenuItem menuItem10 = new JMenuItem("research vessel");
      menuItem10.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.RESEARCH_VESSEL;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem10);  
      
      
      //
      //////////////////////// Ro-Ro ship /////////////////
      //
      JMenuItem menuItem11 = new JMenuItem("Ro-Ro ship");
      menuItem11.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.RO_RO_SHIP;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem11);  
      
      
      //
      //////////////////////// ferry /////////////////
      //
      JMenuItem menuItem12 = new JMenuItem("ferry");
      menuItem12.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.FERRY;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem12);  

      
      //
      //////////////////////// general cargo classic /////////////////
      //
      JMenuItem menuItem14 = new JMenuItem("general cargo classic");
      menuItem14.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.GENERAL_CARGO_CLASSIC;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem14);  
      
      
      //
      //////////////////////// reefer ship /////////////////
      //
      JMenuItem menuItem15 = new JMenuItem("reefer ship");
      menuItem15.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.REEFER_SHIP;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem15);  
       
      
      //
      //////////////////////// fruit juice tanker /////////////////
      //
      JMenuItem menuItem18 = new JMenuItem("fruit juice tanker");
      menuItem18.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.FRUIT_JUICE_TANKER;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem18);  
      
      
      // background image pop-up
      popup.add(new JSeparator()); // SEPARATOR
      

      JMenuItem menuItem1100 = new JMenuItem("background image...");
      menuItem1100.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            //main.dashboard_background_image = choose_dashboard_image();
            choose_dashboard_image();             // NB will also be saved in configuration.txt here
            
            if (main.dashboard_background_image.compareTo("") != 0)
            {
               repaint();
            }
         }
      });
      popup.add(menuItem1100);  
      
     
      JMenuItem menuItem1101 = new JMenuItem("clear background");
      menuItem1101.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            int result = JOptionPane.showConfirmDialog(null, "clear background", main.APPLICATION_NAME, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             
            if (result == JOptionPane.YES_OPTION)                             // NB // 0=yes, 1=no, 2=cancel 
            {
               main.dashboard_background_image = "";
               repaint();
               
               // write meta data to muffins or configuration files
               if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
               {
                  main.schrijf_configuratie_regels();          
               }
               else // so offline_via_jnlp mode or online (webstart) mode
               {
                  //main.set_muffin();
                  main.schrijf_configuratie_regels();
               }   
            } // if (result == JOptionPane.YES_OPTION)
         }
      });
      popup.add(menuItem1101); 
     
     
      MouseListener popupListener = new DASHBOARD_view_AWS_radar.PopupListener();
      addMouseListener(popupListener);
      
      
      // title
      setTitle(main.APPLICATION_NAME + " Automatic Weather Station Dashboard [wind radar]");
   
      // NB see below, otherwise if you select Dashboard -> AWS for the second, third or xth time it wil first display the situation 
      //    of the moment that the dasboard was closed, and after approx 1 minute it will be updated. 
      //    NOW it will update the dashboard immediately
      jPanel1.repaint();     
      
   } // private void initComponents1()
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/    
   private void choose_dashboard_image() 
   {                          
      boolean doorgaan = true;
      String info = "please select first day colours and then try again";
      
      
     if (night_vision == true)
      {
         JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
      }
      

      if (doorgaan == true)
      {
         // pop-up the file chooser dialog box
         JFileChooser chooser = new JFileChooser();
         FileNameExtensionFilter  filter = new FileNameExtensionFilter("JPG & PNG & GIF images", "jpg", "png", "gif");
         chooser.setFileFilter(filter);

         int result = chooser.showOpenDialog(DASHBOARD_view_AWS_radar.this);
         if (result == JFileChooser.APPROVE_OPTION)
         {
            main.dashboard_background_image = chooser.getSelectedFile().getPath();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
               main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }               

         } // if (result == JFileChooser.APPROVE_OPTION
      } // if (doorgaan == true)
      
   }                                              
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void init_dashboard_AWS_radar_timer()
   {
      // updating/displaying received AWS sensor data (not from file), timer scheduled
      //
      // called from: DASHBOARD_view_AWS_radar() [DASHBOARD_view_AWS_radar.java]
   
      ActionListener update_dashboard_AWS_radar_action = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            jPanel1.repaint();                                                        // main panel
         } 
      };  

      // main loop for updating AWS radar dashboard
      dashboard_update_timer_AWS_radar = new Timer(DELAY_UPDATE_AWS_RADAR_SENSOR_LOOP, update_dashboard_AWS_radar_action);
      dashboard_update_timer_AWS_radar.setRepeats(true);                                               // false = only one action
      dashboard_update_timer_AWS_radar.setInitialDelay(INITIAL_DELAY_UPDATE_AWS_RADAR_SENSOR_LOOP);    // time in millisec to wait after timer is started to fire first event
      dashboard_update_timer_AWS_radar.setCoalesce(true);                                              // by default true, but to be certain
      dashboard_update_timer_AWS_radar.restart();
      dashboard_update_timer_AWS_radar_is_gecreeerd = true;
   }   
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public DASHBOARD_view_AWS_radar() 
   {
      initComponents();
      initComponents1();
      init_dashboard_AWS_radar_timer();
      
      if (main.theme_mode.equals(main.THEME_TRANSPARENT))   
      {
         setOpacity(0.75f);
      } // else if (theme_mode.equals(THEME_TRANSPARENT))  
   }

   
   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
    * Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jPanel5 = new javax.swing.JPanel();
      jPanel2 = new javax.swing.JPanel();
      jPanel3 = new javax.swing.JPanel();
      jLabel2 = new javax.swing.JLabel();
      jPanel4 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      jLabel3 = new javax.swing.JLabel();
      jButton1 = new javax.swing.JButton();
      jPanel1 = new DASHBOARD_grafiek_AWS_radar();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosed(java.awt.event.WindowEvent evt) {
            Dashboard_view_AWS_radar_windowClosed(evt);
         }
         public void windowClosing(java.awt.event.WindowEvent evt) {
            Dashboard_view_AWS_radar_windowClosing(evt);
         }
         public void windowDeiconified(java.awt.event.WindowEvent evt) {
            Dashboard_AWS_radar_windowDeiconified(evt);
         }
      });

      jPanel5.setPreferredSize(new java.awt.Dimension(10, 551));

      javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
      jPanel5.setLayout(jPanel5Layout);
      jPanel5Layout.setHorizontalGroup(
         jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 10, Short.MAX_VALUE)
      );
      jPanel5Layout.setVerticalGroup(
         jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 600, Short.MAX_VALUE)
      );

      getContentPane().add(jPanel5, java.awt.BorderLayout.WEST);

      jPanel2.setPreferredSize(new java.awt.Dimension(10, 551));

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 10, Short.MAX_VALUE)
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 600, Short.MAX_VALUE)
      );

      getContentPane().add(jPanel2, java.awt.BorderLayout.EAST);

      jPanel3.setPreferredSize(new java.awt.Dimension(1209, 40));

      jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      jLabel2.setText("--- Wind speed and dir = 10 min average. Wind gust = max wind in last 10 min. MSLP = 1 min median. Press read, Air temp, RH, SST = 1 min median at sensor ht. SOG, COG, Heading = 10 min average ---");

      javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1189, Short.MAX_VALUE)
            .addContainerGap())
      );
      jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
      );

      getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

      jPanel4.setPreferredSize(new java.awt.Dimension(1209, 40));

      jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
      jLabel1.setText("--- right click for night colors and ship type ---");

      jButton1.setText("make visual observation");
      jButton1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            make_obs_actionPerformed(evt);
         }
      });

      javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
      jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(260, 260, 260)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
            .addGap(260, 260, 260)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
      );
      jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
               .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jButton1)))
            .addContainerGap())
      );

      getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

      jPanel1.setBackground(java.awt.Color.lightGray);
      jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));
      jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
         public void componentResized(java.awt.event.ComponentEvent evt) {
            DASHBOARD_view_radar_componentResizedHandler(evt);
         }
      });

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 1189, Short.MAX_VALUE)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 600, Short.MAX_VALUE)
      );

      getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

      pack();
   }// </editor-fold>//GEN-END:initComponents

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void DASHBOARD_view_radar_componentResizedHandler(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_DASHBOARD_view_radar_componentResizedHandler
      // TODO add your handling code here:
      System.out.println("--- AWS Dashboard wind radar (jPanel1) size = " + DASHBOARD_view_AWS_radar.jPanel1.getSize());
      
      width_AWS_radar_dashboard = jPanel1.getWidth();
      height_AWS_radar_dashboard = jPanel1.getHeight();
      
      jPanel1.repaint();  
   }//GEN-LAST:event_DASHBOARD_view_radar_componentResizedHandler

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void make_obs_actionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_make_obs_actionPerformed
   {//GEN-HEADEREND:event_make_obs_actionPerformed
      // TODO add your handling code here:
      
      
      this.setAlwaysOnTop(false);                                    // reset AWS pop-up dashboard option to 'NOT aways on top', otherwise main screen cannot be opened this way
      //System.out.println("--- APR Dashboard (re)set to 'NOT Always On Top'");
  
      
      if (main.mainClass != null)
      {   
         if ((turbowin.main.ICONIFIED & main.mainClass.getExtendedState()) == turbowin.main.ICONIFIED)
         {
            if (turbowin.main.trayIcon != null)
            {
               main.mainClass.tray.remove(turbowin.main.trayIcon) ; 
            }
         }
         
         main.mainClass.setExtendedState(NORMAL);
         main.mainClass.setVisible(true); 
         
      } // if (main.mainClass != null)
   }//GEN-LAST:event_make_obs_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/    
   private void Dashboard_AWS_radar_windowDeiconified(java.awt.event.WindowEvent evt)//GEN-FIRST:event_Dashboard_AWS_radar_windowDeiconified
   {//GEN-HEADEREND:event_Dashboard_AWS_radar_windowDeiconified
      // TODO add your handling code here:
      
      System.out.println("--- AWS Dashboard wind radar (jPanel1) deiconified");
      jPanel1.repaint();  
   }//GEN-LAST:event_Dashboard_AWS_radar_windowDeiconified

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/    
   private void Dashboard_view_AWS_radar_windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_Dashboard_view_AWS_radar_windowClosed
      // TODO add your handling code here:
      
      if (dashboard_update_timer_AWS_radar_is_gecreeerd == true)  
      {
         if (dashboard_update_timer_AWS_radar.isRunning())
         {
            dashboard_update_timer_AWS_radar.stop();
         }
      }
      
      dashboard_update_timer_AWS_radar = null;
      dashboard_update_timer_AWS_radar_is_gecreeerd = false;  
      
      main.dashboard_form_AWS_radar = null;
      main.dashboard_was_automatically_opened = false;                        // reset; related to AlwaysOnTop yes/no [DASHBOARD_view_AWS_radar.java]
   }//GEN-LAST:event_Dashboard_view_AWS_radar_windowClosed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/    
   private void Dashboard_view_AWS_radar_windowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_Dashboard_view_AWS_radar_windowClosing
   {//GEN-HEADEREND:event_Dashboard_view_AWS_radar_windowClosing
      // TODO add your handling code here:
      
      
      // NB windowClosing will be invoked if the user (manually) closes this window
      //    this function will not be called if this window is automatically closed by the system [via main.dashboard_form_AWS_radar.dispose()]
      
      // NB flag (boolean) "in_dashboard_pop_up_period" is set in Function: RS422_init_new_aws_data_received_check_timer() [RS232_RS422.java]
      //
      
      if (main.in_dashboard_pop_up_period == true)
      {
         main.dashboard_was_manually_closed_in_dashboard_pop_up_period = true;                       // for option pop-up window/dashboard
      }
      else
      {
         main.dashboard_was_manually_closed_in_dashboard_pop_up_period = false;                      // for option pop-up window/dashboard 
      }
   }//GEN-LAST:event_Dashboard_view_AWS_radar_windowClosing

   
   
   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
      /* Set the Nimbus look and feel */
      //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
      /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
       */
      try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(DASHBOARD_view_AWS_radar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(DASHBOARD_view_AWS_radar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(DASHBOARD_view_AWS_radar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(DASHBOARD_view_AWS_radar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
      //</editor-fold>

      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            new DASHBOARD_view_AWS_radar().setVisible(true);
         }
      });
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   public static javax.swing.JButton jButton1;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   public static javax.swing.JLabel jLabel3;
   /*
   private javax.swing.JPanel jPanel1;
   */private static DASHBOARD_grafiek_AWS_radar jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   public static javax.swing.JPanel jPanel4;
   private javax.swing.JPanel jPanel5;
   // End of variables declaration//GEN-END:variables

   public static final int AWS_DASHBOARD_POP_UP_MINUTES_TO_HOUR            = 10;
   public static int width_AWS_radar_dashboard;
   public static int height_AWS_radar_dashboard;
   public static Timer dashboard_update_timer_AWS_radar;
   public static boolean dashboard_update_timer_AWS_radar_is_gecreeerd;
   public static boolean night_vision;
   private final int DELAY_UPDATE_AWS_RADAR_SENSOR_LOOP                    = 60000; // 1 min                 // time in millisec to wait after timer is started to fire first event (10 min = 10 * 1000 * 60 * 10 = 600000)
   private final int INITIAL_DELAY_UPDATE_AWS_RADAR_SENSOR_LOOP            = 0; // 1000 = 1 sec              // time in millisec to wait after timer is started to fire first event
   //public static Timer dashboard_update_AWS_radar_timer;
   //public static boolean dashboard_update_AWS_radar_timer_is_gecreeerd;
   private JPopupMenu popup;
   private static Color background_color_panel1;
   private static Color background_color_panel2;
   private static Color background_color_panel3;
   private static Color background_color_panel4;
   private static Color background_color_panel5;
   public static Color deck_color                                          = null;
   public static Color tank_color                                          = null;
   private final Color DECK_COLOR_BROWN                                    = new Color(153, 67, 0); 
   private final Color DECK_COLOR_RED_BROWN                                = new Color(204, 69, 50, 255);
   private final Color DECK_COLOR_RED                                      = new Color(204, 0, 0); 
   private final Color DECK_COLOR_GREEN                                    = new Color(0, 100, 70);           // MUST BE THE SAME AS IN ship.java  
   private final Color DECK_COLOR_LIGHT_GREEN                              = new Color(0, 204, 102);          // MUST BE THE SAME AS IN ship.java 
   private final Color DECK_COLOR_ORANGE                                   = new Color(255, 128, 0); 
   private final Color DECK_COLOR_YELLOW                                   = new Color(255, 215, 0);          // gold
   private final Color DECK_COLOR_GRAY_BROWN                               = new Color(124, 112, 98);
   private final Color DECK_COLOR_GRAY                                     = Color.GRAY;
   private final Color DECK_COLOR_LIGHT_GRAY                               = Color.LIGHT_GRAY;
   private final Color DECK_COLOR_DARK_GRAY                                = Color.DARK_GRAY;
   private final Color DECK_COLOR_BLUE_GRAY                                = new Color(90, 141, 185); 
   private final Color TANK_COLOR_WHITE                                    = Color.WHITE;
   private final Color TANK_COLOR_RED                                      = Color.RED;

}
