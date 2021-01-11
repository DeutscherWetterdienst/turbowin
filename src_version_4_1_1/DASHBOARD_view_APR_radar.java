/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbowin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author marti
 */
public class DASHBOARD_view_APR_radar extends javax.swing.JFrame {

   /**
    * Creates new form DASHBOARD_view_APR_radar
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
   
   
   
   /* inner class HitLinkMouseListener */
   public class HitLinkMouseListener extends MouseAdapter // https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/2d/advanced/examples/HitTestSample.java
   {
      //
      // Perform action if mouse click in satellite image text link.
      //
      
      @Override
      //public void mouseClicked(MouseEvent e)                            // Invoked when the mouse button has been clicked (pressed and released) on a component.
      public void mousePressed(MouseEvent e)                              // Invoked when a mouse button has been pressed on a component.     
      {
         if (SwingUtilities.isLeftMouseButton(e))                         // only left mouse press
         {
            // position of the mouse press/click
            float mouse_x = e.getX();
            float mouse_y = e.getY();
 
            
            // BELOW FOR TESTING
            //// Get the character position of the mouse click.
            ////TextHitInfo currentHit = layout_satellite_link.hitTestChar(clickX, clickY);
            ////int insertionIndex = currentHit.getInsertionIndex();
            //System.out.println(" --------------------------------------------------- ");
            //System.out.println("+++++++++++ x1_block = " + x1_block);
            //System.out.println("+++++++++++ y1_block = " + y1_block);
            //System.out.println("+++++++++++ e.getX() = " + mouse_x);
            //System.out.println("+++++++++++ e.getY() = " + mouse_y);
            //System.out.println("+++++++++++ x_line_satellite_link = " + x_line_satellite_link);
            //System.out.println("+++++++++++ y_line_satellite_link = " + y_line_satellite_link);
            //System.out.println("+++++++++++ x_line_satellite_link_absolute = " + x_line_satellite_link_absolute);
            //System.out.println("+++++++++++ y_line_satellite_link_absolute = " + y_line_satellite_link_absolute);
            //System.out.println("+++++++++++ getWidth() / 2 = " + (getWidth() / 2));
            //System.out.println("+++++++++++ getHeith() / 2 = " + (getHeight() / 2));
            //System.out.println("+++++++++++ insertionIndex = " + insertionIndex);
            // END FOR TESTING
 
            // NB bounds2 is a virtual rectangle with Java x,y coordinate system for comparision with mouse clicks (NB bounds was defined according the translated coordinate system !)
            Rectangle2D bounds2 = bounds.getBounds();     // so bounds2 is a copy of bounds
            bounds2.setRect((getWidth() / 2) + bounds.getX(), getHeight() / 2 + bounds.getY(), bounds.getWidth(), (bounds.getHeight() * 1.5)); //NB  (bounds.getHeight() * 1.5) =  to increase the clickable rectangle height
            //System.out.println ("+++++++++++ bounds2 = " + bounds2.getBounds());
        
            if (bounds2.contains(mouse_x, mouse_y)) 
            {
               //System.out.println("+++++++++++ clicked in bounding box");
            
               // defaults
               String url_satellite_image = "";
               String url_lat_hemisphere_sign = "";
               String url_lon_hemisphere_sign = "";
               boolean url_lat_ok = false;
               boolean url_lon_ok = false;
               
               
               // Latitude
               //
               //
               if (myposition.latitude_degrees.compareTo("") != 0 && myposition.latitude_hemisphere.compareTo("") != 0 &&
                   myposition.latitude_degrees != null && myposition.latitude_hemisphere != null)
               {
                  if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true))
                  {
                     url_lat_hemisphere_sign = "-";
                  }
                  else
                  {
                     url_lat_hemisphere_sign = "";
                  }
                  
                  // String latitude convert to integer
                  try 
                  {
                     int int_lat_degrees = Integer.parseInt(myposition.latitude_degrees.trim());
                     
                     if ((int_lat_degrees >= 0) && (int_lat_degrees <= 90))
                     {
                        url_lat_ok = true;
                     }
                  }
                  catch (NumberFormatException ex){/* ... */}
                  
               } // if (myposition.latitude_degrees.compareTo("") != 0 etc.
            
               
               // Longitude
               //
               //
               if (myposition.longitude_degrees.compareTo("") != 0 && myposition.longitude_hemisphere.compareTo("") != 0 &&
                   myposition.longitude_degrees != null && myposition.longitude_hemisphere != null)
               {
                  if ((myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true))
                  {
                     url_lon_hemisphere_sign = "-";  // West negative (http://realearth.ssec.wisc.edu/doc/ : API usage)
                  }
                  else
                  {
                     url_lon_hemisphere_sign = "";
                  }
                  
                  // String longitude convert to integer
                  try 
                  {
                     int int_lon_degrees = Integer.parseInt(myposition.longitude_degrees.trim());
                     
                     if ((int_lon_degrees >= 0) && (int_lon_degrees <= 180))
                     {
                        url_lon_ok = true;
                     }
                  }
                  catch (NumberFormatException ex){/* ... */}
                  
               } // if (myposition.longitude_degrees.compareTo("") != 0 etc.
               
               
               if (url_lat_ok && url_lon_ok)
               {
                  url_satellite_image = "https://realearth.ssec.wisc.edu/?products=globalir&time=latest&center=" + 
                                        url_lat_hemisphere_sign +               // "-" or ""
                                        myposition.latitude_degrees +
                                        "," +
                                        url_lon_hemisphere_sign +               // "-" or ""
                                        myposition.longitude_degrees +
                                        "&zoom=4";
               }
               else
               {
                  // something went wrong so take the default (0,0 as center of map and zoom level 3)
                  url_satellite_image = "https://realearth.ssec.wisc.edu/?products=globalir&time=latest&center=0,0&zoom=3";
               }
               
               // invoke, in the default web browser, the url to the satellite image
               main.satellite_link_mouse_clicked(url_satellite_image);
               
            } // if (bounds2.contains(mouse_x, mouse_y)) 
         } // public void mouseClicked(MouseEvent e)
      } // if (SwingUtilities.isLeftMouseButton(e))
   } // public class HitTestMouseListener extends MouseAdapter 
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void initComponents1()
   //public static void initComponents1()        
   {
      // fill wind direction combobox
      jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "variable", "calm", 
                                                                                "10", "20", "30", "40", "50" , "60", "70", "80", "90", "100",
                                                                                "110", "120", "130", "140", "150", "160", "170" , "180", "190", "200",
                                                                                "210", "220", "230", "240", "250", "260", "270" , "280", "290", "300",
                                                                                "310", "320", "330", "340", "350", "360"
                                                                              }));
      
      // check wind source (must be TRUE wind (estimated or measured))
      if ( (!main.wind_source.equals(main.ESTIMATED_TRUE)) && (!main.wind_source.equals(main.MEASURED_TRUE)) )
      {
         // NB see also: reset_Bf_text_labels() [DASHBOARD_view_APR_radar.java]
         
         // disable wind dir combobox and wind force input radiobuttons
         jComboBox1.setEnabled(false);
         jRadioButton1.setEnabled(false);
         jRadioButton2.setEnabled(false);
         jRadioButton3.setEnabled(false);
         jRadioButton4.setEnabled(false);
         jRadioButton5.setEnabled(false);
         jRadioButton6.setEnabled(false);
         jRadioButton7.setEnabled(false);
         jRadioButton8.setEnabled(false);
         jRadioButton9.setEnabled(false);
         jRadioButton10.setEnabled(false);
         jRadioButton11.setEnabled(false);
         jRadioButton12.setEnabled(false);
         jRadioButton13.setEnabled(false);
         jRadioButton14.setEnabled(false);
      }
      
      //System.out.println("+++ 1) [initComponets1() mywind.int_true_wind_speed = "+ mywind.int_true_wind_speed);
      
      // set
      wind_input_APR_from_main_form_or_previous = true;    // then no consistency checks (via itemStateChanged() e.g. if a radiobutton is selected here below)
      
      
      // insert earlier inserted/selected values
      if ( (main.wind_source.equals(main.ESTIMATED_TRUE)) || (main.wind_source.equals(main.MEASURED_TRUE)) )
      {
         if (mywind.int_true_wind_dir == main.INVALID)
         {
           jComboBox1.setSelectedItem(""); 
         }
         else if (mywind.int_true_wind_dir == 0)
         {
            jComboBox1.setSelectedItem("calm");
         }
         else if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE)
         {
            jComboBox1.setSelectedItem("variable");
         }
         else
         {
            // NB see update_APR_dashboard() [mywind.java]
            
            
            //jComboBox1.setSelectedItem(Integer.toString(mywind.int_true_wind_dir)); 
            int tmp_wind_dir = (int)(Math.round(mywind.int_true_wind_dir / 10.0) * 10);   // round to tens!
            jComboBox1.setSelectedItem(Integer.toString(tmp_wind_dir));
         }
         
         int Bf_number = main.INVALID;;
         if (main.wind_units.trim().indexOf(main.M_S) != -1)     // m/s
         {
            Bf_number = convert_ms_to_bf(mywind.int_true_wind_speed);
         }
         else
         {
            Bf_number = convert_knots_to_bf(mywind.int_true_wind_speed);
         }   
         
         //System.out.println("+++ 1) [initComponets1() Bf number = "+ Bf_number);
            
         if (Bf_number == 0)
         {
            jRadioButton1.setSelected(true);
         }
         else if (Bf_number == 1)
         {
            jRadioButton2.setSelected(true);     
         }
         else if (Bf_number == 2)
         {
            jRadioButton3.setSelected(true);     
         }
         else if (Bf_number == 3)
         {
            jRadioButton4.setSelected(true);     
         }
         else if (Bf_number == 4)
         {
            jRadioButton5.setSelected(true);     
         }
         else if (Bf_number == 5)
         {
            jRadioButton6.setSelected(true);     
         }
         else if (Bf_number == 6)
         {
            jRadioButton7.setSelected(true);     
         }
         else if (Bf_number == 7)
         {
            jRadioButton8.setSelected(true);     
         }
         else if (Bf_number == 8)
         {
            jRadioButton9.setSelected(true);     
         }
         else if (Bf_number == 9)
         {
            jRadioButton10.setSelected(true);     
         }
         else if (Bf_number == 10)
         {
            jRadioButton11.setSelected(true);     
         }
         else if (Bf_number == 11)
         {
            jRadioButton12.setSelected(true);     
         }
         else if (Bf_number == 12)
         {
            jRadioButton13.setSelected(true);     
         }        
         else //if (Bf_number == main.INVALID)
         {
            jRadioButton14.setSelected(true);     
         }
         
      } // if ( (main.wind_source.equals(main.ESTIMATED_TRUE)) || (main.wind_source.equals(main.MEASURED_TRUE)) )
      
      // reset
      wind_input_APR_from_main_form_or_previous = false;  
      
      
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
         
         background_color_panel6 = (new java.awt.Color(0, 191, 255));
              
         jPanel1.setBackground(Color.DARK_GRAY);
         jPanel2.setBackground(Color.BLACK);
         jPanel3.setBackground(Color.BLACK);
         jPanel4.setBackground(Color.BLACK);
         jPanel5.setBackground(Color.BLACK); 
         
         jPanel6.setBackground(Color.RED);
      }
      else
      {
         night_vision = false;
         
         //background_color_panel1 = jPanel1.getBackground();
         background_color_panel1 = jPanel4.getBackground();    // jPanel4 = West panel (but could also be another (site)panel)
         jPanel1.setBackground(background_color_panel1); 
         
         background_color_panel2 = jPanel2.getBackground();
         background_color_panel3 = jPanel3.getBackground();
         background_color_panel4 = jPanel4.getBackground();
         background_color_panel5 = jPanel5.getBackground();
         
         background_color_panel6 = jPanel6.getBackground();
      } 
   
      
      /* background color main panel (set by popup menu option) */
      popup = new JPopupMenu();
      
      JMenuItem menuItem = new JMenuItem("night colours");
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
            jPanel6.setBackground(Color.RED);
            
            //jPanel5.repaint();
         }
      });
      popup.add(menuItem);
      
      JMenuItem menuItem2 = new JMenuItem("day colours");
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
            jPanel6.setBackground(new java.awt.Color(0, 191, 255));
            
            //jPanel5.repaint();
         }
      });
      popup.add(menuItem2);  

      
      popup.add(new JSeparator()); // SEPARATOR
      
 
      JMenuItem menuItem3 = new JMenuItem("container ship");
      menuItem3.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.CONTAINER_SHIP;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem3);  
      
      JMenuItem menuItem4 = new JMenuItem("bulk carrier");
      menuItem4.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.BULK_CARRIER;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem4);  
      
      
      JMenuItem menuItem5 = new JMenuItem("oil tanker");
      menuItem5.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.OIL_TANKER;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem5);  
 
      JMenuItem menuItem6 = new JMenuItem("LNG tanker");
      menuItem6.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.LNG_TANKER;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem6);  
      
      
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
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem7); 
      
      
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
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem8);  
      
      
      JMenuItem menuItem9 = new JMenuItem("general cargo ship");
      menuItem9.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem9);  
      
      
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
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem10);  
      

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
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem11);  
      
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
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem12);  
      
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
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem13);  

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
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem14);  
      
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
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem15);  
    
      JMenuItem menuItem16 = new JMenuItem("general cargo ship II");
      menuItem16.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_2;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem16); 
      
      JMenuItem menuItem17 = new JMenuItem("LNG tanker II");
      menuItem17.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.LNG_TANKER_II;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem17);  

      
      // background image pop-up
      popup.add(new JSeparator()); // SEPARATOR
      

      JMenuItem menuItem1100 = new JMenuItem("background image...");
      menuItem1100.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            //main.dashboard_background_image = choose_dashboard_image();
            choose_APR_dashboard_image();             // NB will also be saved in configuration.txt here
            
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
                  main.set_muffin();
                  main.schrijf_configuratie_regels();
               }   
            } // if (result == JOptionPane.YES_OPTION)
         }
      });
      popup.add(menuItem1101); 
     
     
      MouseListener popupListener = new DASHBOARD_view_APR_radar.PopupListener();
      addMouseListener(popupListener);
      
      MouseListener hittestMouseListener = new DASHBOARD_view_APR_radar.HitLinkMouseListener();
      addMouseListener(hittestMouseListener);

      
      // title
      setTitle(main.APPLICATION_NAME + " Automated Pressure Reports Dashboard [meteo radar]");
      
      // Bf text labels must be empty string at start-up
      reset_Bf_text_labels();
   
      // NB see below, otherwise if you select Dashboard -> APR for the second, third or xth time it wil first display the situation 
      //    of the moment that the dasboard was closed, and after approx 1 minute it will be updated. 
      //    NOW it will update the dashboard immediately
      jPanel1.repaint();     
      
   } // private void initComponents1()
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/    
   private void choose_APR_dashboard_image() 
   {           
      // called from: initComponents1() [DASHBOARD_VIEW_APR_radar.java]
      
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

         int result = chooser.showOpenDialog(DASHBOARD_view_APR_radar.this);
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
               main.set_muffin();
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
   private void init_dashboard_APR_radar_timer()
   {
      // updating/displaying received APR sensor data (not from file), timer scheduled
      //
      // called from: DASHBOARD_view_APR_radar() [DASHBOARD_view_APR_radar.java]
   
      ActionListener update_dashboard_APR_radar_action = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            jPanel1.repaint();                                                        // main panel
         } 
      };  

      // main loop for updating AWS hybrid dashboard
      dashboard_update_APR_timer = new Timer(DELAY_UPDATE_APR_RADAR_SENSOR_LOOP, update_dashboard_APR_radar_action);
      dashboard_update_APR_timer.setRepeats(true);                                               // false = only one action
      dashboard_update_APR_timer.setInitialDelay(INITIAL_DELAY_UPDATE_APR_RADAR_SENSOR_LOOP);    // time in millisec to wait after timer is started to fire first event
      dashboard_update_APR_timer.setCoalesce(true);                                              // by default true, but to be certain
      dashboard_update_APR_timer.restart();
      dashboard_update_APR_timer_is_gecreeerd = true;
   }   
 
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/    
   public DASHBOARD_view_APR_radar() 
   {
      initComponents();
      initComponents1();
      init_dashboard_APR_radar_timer();
      
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

      buttonGroup1 = new javax.swing.ButtonGroup();
      jPanel5 = new javax.swing.JPanel();
      jLabel3 = new javax.swing.JLabel();
      jPanel6 = new javax.swing.JPanel();
      jLabel2 = new javax.swing.JLabel();
      jComboBox1 = new javax.swing.JComboBox<>();
      jLabel4 = new javax.swing.JLabel();
      jRadioButton1 = new javax.swing.JRadioButton();
      jRadioButton2 = new javax.swing.JRadioButton();
      jRadioButton3 = new javax.swing.JRadioButton();
      jRadioButton4 = new javax.swing.JRadioButton();
      jRadioButton5 = new javax.swing.JRadioButton();
      jRadioButton6 = new javax.swing.JRadioButton();
      jRadioButton7 = new javax.swing.JRadioButton();
      jRadioButton8 = new javax.swing.JRadioButton();
      jRadioButton9 = new javax.swing.JRadioButton();
      jRadioButton10 = new javax.swing.JRadioButton();
      jRadioButton11 = new javax.swing.JRadioButton();
      jRadioButton12 = new javax.swing.JRadioButton();
      jRadioButton13 = new javax.swing.JRadioButton();
      jRadioButton14 = new javax.swing.JRadioButton();
      jLabel5 = new javax.swing.JLabel();
      jLabel6 = new javax.swing.JLabel();
      jLabel7 = new javax.swing.JLabel();
      jLabel8 = new javax.swing.JLabel();
      jLabel9 = new javax.swing.JLabel();
      jLabel10 = new javax.swing.JLabel();
      jLabel11 = new javax.swing.JLabel();
      jLabel12 = new javax.swing.JLabel();
      jLabel13 = new javax.swing.JLabel();
      jLabel14 = new javax.swing.JLabel();
      jLabel15 = new javax.swing.JLabel();
      jLabel16 = new javax.swing.JLabel();
      jLabel17 = new javax.swing.JLabel();
      jLabel18 = new javax.swing.JLabel();
      jLabel20 = new javax.swing.JLabel();
      jLabel21 = new javax.swing.JLabel();
      jPanel4 = new javax.swing.JPanel();
      jPanel3 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      jButton1 = new javax.swing.JButton();
      jLabel19 = new javax.swing.JLabel();
      jPanel2 = new javax.swing.JPanel();
      jPanel1 = new DASHBOARD_grafiek_APR_radar();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosed(java.awt.event.WindowEvent evt) {
            DASHBOARD_view_APR_windowClosed(evt);
         }
         public void windowDeiconified(java.awt.event.WindowEvent evt) {
            DASHBOARD_view_APR_Deiconified(evt);
         }
      });

      jPanel5.setPreferredSize(new java.awt.Dimension(250, 613));

      jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
      jLabel3.setText("--- INPUT TRUE WIND DATA ---");

      jPanel6.setBackground(new java.awt.Color(0, 191, 255));
      jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
      jPanel6.setName(""); // NOI18N
      jPanel6.setPreferredSize(new java.awt.Dimension(200, 661));

      jLabel2.setText("true wind direction");

      jComboBox1.setMaximumRowCount(40);
      jComboBox1.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            wind_dir_itemStateChanged(evt);
         }
      });

      jLabel4.setText("true wind force");

      jRadioButton1.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton1);
      jRadioButton1.setText("Bf 0");
      jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf0_itemStateChanged(evt);
         }
      });

      jRadioButton2.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton2);
      jRadioButton2.setText("Bf 1");
      jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf1_itemStateChanged(evt);
         }
      });

      jRadioButton3.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton3);
      jRadioButton3.setText("Bf 2");
      jRadioButton3.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf2_itemStateChanged(evt);
         }
      });

      jRadioButton4.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton4);
      jRadioButton4.setText("Bf 3");
      jRadioButton4.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf3_itemStateChanged(evt);
         }
      });

      jRadioButton5.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton5);
      jRadioButton5.setText("Bf 4");
      jRadioButton5.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf4_itemStateChanged(evt);
         }
      });

      jRadioButton6.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton6);
      jRadioButton6.setText("Bf 5");
      jRadioButton6.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf5_itemStateChanged(evt);
         }
      });

      jRadioButton7.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton7);
      jRadioButton7.setText("Bf 6");
      jRadioButton7.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf6_itemStateChanged(evt);
         }
      });

      jRadioButton8.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton8);
      jRadioButton8.setText("Bf 7");
      jRadioButton8.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf7_itemStateChanged(evt);
         }
      });

      jRadioButton9.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton9);
      jRadioButton9.setText("Bf 8");
      jRadioButton9.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf8_itemStateChanged(evt);
         }
      });

      jRadioButton10.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton10);
      jRadioButton10.setText("Bf 9");
      jRadioButton10.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf9_itemStateChanged(evt);
         }
      });

      jRadioButton11.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton11);
      jRadioButton11.setText("Bf 10");
      jRadioButton11.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf10_itemStateChanged(evt);
         }
      });

      jRadioButton12.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton12);
      jRadioButton12.setText("Bf 11");
      jRadioButton12.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf11_itemStateChanged(evt);
         }
      });

      jRadioButton13.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton13);
      jRadioButton13.setText("Bf 12");
      jRadioButton13.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf12_itemStateChanged(evt);
         }
      });

      jRadioButton14.setBackground(new java.awt.Color(0, 191, 255));
      buttonGroup1.add(jRadioButton14);
      jRadioButton14.setText("not determined");
      jRadioButton14.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            Bf_not_determined_itemStateChanged(evt);
         }
      });

      jLabel5.setText("jLabel5");

      jLabel6.setText("jLabel6");

      jLabel7.setText("jLabel7");

      jLabel8.setText("jLabel8");

      jLabel9.setText("jLabel9");

      jLabel10.setText("jLabel10");

      jLabel11.setText("jLabel11");

      jLabel12.setText("jLabel12");

      jLabel13.setText("jLabel13");

      jLabel14.setText("jLabel14");

      jLabel15.setText("jLabel15");

      jLabel16.setText("jLabel16");

      jLabel17.setText("jLabel17");

      jLabel18.setText("jLabel18");

      jLabel20.setText("jLabel20");

      jLabel21.setText("jLabel21");

      javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
      jPanel6.setLayout(jPanel6Layout);
      jPanel6Layout.setHorizontalGroup(
         jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(jPanel6Layout.createSequentialGroup()
                  .addGap(19, 19, 19)
                  .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                           .addComponent(jRadioButton4)
                           .addComponent(jRadioButton5)
                           .addComponent(jRadioButton6))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                           .addComponent(jRadioButton13)
                           .addComponent(jRadioButton12)
                           .addComponent(jRadioButton11)))
                     .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jRadioButton7)
                        .addGap(47, 47, 47)
                        .addComponent(jRadioButton14))
                     .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                           .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addComponent(jRadioButton1)
                              .addComponent(jRadioButton2)
                              .addComponent(jRadioButton3))
                           .addGap(47, 47, 47)
                           .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addComponent(jRadioButton10)
                              .addComponent(jRadioButton9)
                              .addComponent(jRadioButton8))))))
               .addGroup(jPanel6Layout.createSequentialGroup()
                  .addGap(20, 20, 20)
                  .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
               .addGroup(jPanel6Layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                     .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                     .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addContainerGap(20, Short.MAX_VALUE))
         .addGroup(jPanel6Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addGroup(jPanel6Layout.createSequentialGroup()
                  .addComponent(jLabel21)
                  .addGap(0, 0, Short.MAX_VALUE))))
      );

      jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel16, jLabel17});

      jPanel6Layout.setVerticalGroup(
         jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel6Layout.createSequentialGroup()
            .addGap(20, 20, 20)
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(35, 35, 35)
            .addComponent(jLabel4)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jRadioButton1)
               .addComponent(jRadioButton8))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jRadioButton2)
               .addComponent(jRadioButton9))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jRadioButton3)
               .addComponent(jRadioButton10))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jRadioButton4)
               .addComponent(jRadioButton11))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jRadioButton5)
               .addComponent(jRadioButton12))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jRadioButton6)
               .addComponent(jRadioButton13))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jRadioButton7)
               .addComponent(jRadioButton14))
            .addGap(31, 31, 31)
            .addComponent(jLabel5)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel6)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel7)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel8)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel9)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel10)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel11)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel12)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel13)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel14)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel15)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel16)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel17)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel18)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
            .addComponent(jLabel21)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel20)
            .addGap(24, 24, 24))
      );

      jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jRadioButton1, jRadioButton10, jRadioButton11, jRadioButton12, jRadioButton13, jRadioButton2, jRadioButton3, jRadioButton4, jRadioButton5, jRadioButton6, jRadioButton7, jRadioButton8, jRadioButton9});

      javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
      jPanel5.setLayout(jPanel5Layout);
      jPanel5Layout.setHorizontalGroup(
         jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );
      jPanel5Layout.setVerticalGroup(
         jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel5Layout.createSequentialGroup()
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
      );

      getContentPane().add(jPanel5, java.awt.BorderLayout.EAST);

      jPanel4.setPreferredSize(new java.awt.Dimension(10, 613));

      javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
      jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 10, Short.MAX_VALUE)
      );
      jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 695, Short.MAX_VALUE)
      );

      getContentPane().add(jPanel4, java.awt.BorderLayout.WEST);

      jPanel3.setPreferredSize(new java.awt.Dimension(1209, 40));

      jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
      jLabel1.setText("--- right click for night colors and ship type ---");

      jButton1.setText("more visual observation");
      jButton1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            make_obs_actionPerformed(evt);
         }
      });

      jLabel19.setText("jLabel19");

      javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(260, 260, 260)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
            .addGap(260, 260, 260)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
      );
      jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(jButton1)
               .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
      );

      getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

      jPanel2.setPreferredSize(new java.awt.Dimension(1209, 40));

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 1278, Short.MAX_VALUE)
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 40, Short.MAX_VALUE)
      );

      getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

      jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
         public void componentResized(java.awt.event.ComponentEvent evt) {
            DASHBOARD_APR_view_radar_componentResizedHandler(evt);
         }
      });

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 1018, Short.MAX_VALUE)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 695, Short.MAX_VALUE)
      );

      getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

      pack();
   }// </editor-fold>//GEN-END:initComponents

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void DASHBOARD_APR_view_radar_componentResizedHandler(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_DASHBOARD_APR_view_radar_componentResizedHandler
      // TODO add your handling code here:
      
      
      width_APR_radar_dashboard = jPanel1.getWidth() + jPanel5.getWidth() - jPanel4.getWidth();       // center panel + east panel (NB east panel is with the manual input data)
      height_APR_radar_dashboard = jPanel1.getHeight();
      
      System.out.println("--- APR Dashboard wind radar (jPanel1 + jPanel5 - jPanel4) size = " + width_APR_radar_dashboard);
      
      jPanel1.repaint();  
      
   }//GEN-LAST:event_DASHBOARD_APR_view_radar_componentResizedHandler

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf0_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf0_itemStateChanged
      // TODO add your handling code here:
      if (jRadioButton1.isSelected() == true)                  
      {
         beaufort_class_APR = 0;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf0_itemStateChanged
 
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf1_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf1_itemStateChanged
      // TODO add your handling code here:
      if (jRadioButton2.isSelected() == true)                  
      {
         beaufort_class_APR = 1;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
      
   }//GEN-LAST:event_Bf1_itemStateChanged
 
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf2_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf2_itemStateChanged
      // TODO add your handling code here:
      if (jRadioButton3.isSelected() == true)                  
      {
         beaufort_class_APR = 2;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
      
   }//GEN-LAST:event_Bf2_itemStateChanged


   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf3_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf3_itemStateChanged
      // TODO add your handling code here:

      if (jRadioButton4.isSelected() == true)                  
      {
         beaufort_class_APR = 3;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf3_itemStateChanged


   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf4_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf4_itemStateChanged
      // TODO add your handling code here:
      
      if (jRadioButton5.isSelected() == true)                  
      {
         beaufort_class_APR = 4;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf4_itemStateChanged
  
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf5_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf5_itemStateChanged
      // TODO add your handling code here:
     
      if (jRadioButton6.isSelected() == true)                  
      {
         beaufort_class_APR = 5;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf5_itemStateChanged
 
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf6_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf6_itemStateChanged
      // TODO add your handling code here:
     
      if (jRadioButton7.isSelected() == true)                  
      {
         beaufort_class_APR = 6;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf6_itemStateChanged
 
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf7_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf7_itemStateChanged
      // TODO add your handling code here:
      
      if (jRadioButton8.isSelected() == true)                  
      {
         beaufort_class_APR = 7;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf7_itemStateChanged

   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf8_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf8_itemStateChanged
      // TODO add your handling code here:
      
      if (jRadioButton9.isSelected() == true)                  
      {
         beaufort_class_APR = 8;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf8_itemStateChanged

   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf9_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf9_itemStateChanged
      // TODO add your handling code here:
      
      if (jRadioButton10.isSelected() == true)                  
      {
         beaufort_class_APR = 9;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf9_itemStateChanged
  
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf10_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf10_itemStateChanged
      // TODO add your handling code here:
      
      if (jRadioButton11.isSelected() == true)                  
      {
      
         beaufort_class_APR = 10;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
   }//GEN-LAST:event_Bf10_itemStateChanged
 
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf11_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf11_itemStateChanged
      // TODO add your handling code here:
      
      if (jRadioButton12.isSelected() == true)                  
      {
         beaufort_class_APR = 11;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf11_itemStateChanged
  
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void Bf12_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf12_itemStateChanged
      // TODO add your handling code here:
      
      if (jRadioButton13.isSelected() == true)                  
      {
         beaufort_class_APR = 12;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf12_itemStateChanged
 
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Bf_not_determined_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Bf_not_determined_itemStateChanged
      // TODO add your handling code here:
      
      if (jRadioButton14.isSelected() == true)                  
      {
         beaufort_class_APR = main.INVALID;
         write_Bf_description(beaufort_class_APR);
         check_wind_consistency_APR();
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
      
   }//GEN-LAST:event_Bf_not_determined_itemStateChanged

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void wind_dir_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_wind_dir_itemStateChanged
      // TODO add your handling code here:
      
      //wind_dir_APR = String.valueOf(jComboBox1.getSelectedItem());
      
      if (jComboBox1.getSelectedItem() != null)
      {
         wind_dir_APR = jComboBox1.getSelectedItem().toString();
         check_wind_consistency_APR(); 
         compute_wind_APR();
         //compute_relative_wind_APR();
         jPanel1.repaint();
      }
   }//GEN-LAST:event_wind_dir_itemStateChanged

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/  
   //public static boolean check_wind_consistency_APR() 
   private boolean check_wind_consistency_APR()        
   {
      // called from: Bf0_itemStateChanged()
      //              Bf1_itemStateChanged()
      //              Bf2_itemStateChanged()
      //              etc.
      
      boolean checks_ok = true;
      
      //System.out.println("+++++++++++ check_wind_consistency_APR()");
      
      
      if (!wind_input_APR_from_main_form_or_previous)   // NB in case wind input from main screen or a previous APR dashboard input the checks were all ready performed in mywind.java !
      {
         // general check
         //
         if (main.wind_source.equals("")) 
         {
            //JOptionPane.showMessageDialog(null, "'wind speed units source' not selected (select: Maintenance -> Station data)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         
            int error_no = 1;
            write_Bf_error_message(error_no);
         
            checks_ok = false;
            reset_APR_wind_variables();         // repaint here
         }
         
/*         
         //else if ( (wind_dir_APR.trim().equals("variable")) && ((beaufort_class_APR >= 3) && (beaufort_class_APR <= 12)) )   
         else if ( (wind_dir_APR.trim().equals("variable")) && ((beaufort_class_APR != 1) || (beaufort_class_APR != 2)) )    
         {
            //JOptionPane.showMessageDialog(null, "if wind direction is variable, wind force must be < 3 Bf", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            int error_no = 2;
            write_Bf_error_message(error_no);

            checks_ok = false;
            reset_APR_variables();
         }
         else if ( (wind_dir_APR.trim().equals("calm")) && ((beaufort_class_APR >= 1) && (beaufort_class_APR <= 12)) )  
         {
            //JOptionPane.showMessageDialog(null, "if wind direction is calm, wind force must be 0 Bf", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE); 

            int error_no = 3;
            write_Bf_error_message(error_no);

            checks_ok = false;
            reset_APR_variables();
         }
         else if ( (beaufort_class_APR == 0) && ((wind_dir_APR.trim().equals("variable") || mywind.int_true_wind_dir > 0 && mywind.int_true_wind_dir <= 360))  )
         {
            //JOptionPane.showMessageDialog(null, "if wind force is 0 Bf, wind direction must be calm", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
       
            int error_no = 4;
            write_Bf_error_message(error_no);
         
            checks_ok = false;
            reset_APR_variables();
         }
*/         
         
         // check wind dir against beaufort class 
         //
         if ((beaufort_class_APR >= 0) && (beaufort_class_APR <= 12))    // so there was a beaufort class selected
         {
            if ( (wind_dir_APR.trim().equals("variable")) && ((beaufort_class_APR != 1) && (beaufort_class_APR != 2)) )    
            {
               //JOptionPane.showMessageDialog(null, "if wind direction is variable, wind force must be < 3 Bf", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
               int error_no = 2;
               write_Bf_error_message(error_no);

               checks_ok = false;
               reset_APR_wind_variables();
            }
            
            if ( (wind_dir_APR.trim().equals("calm")) && ((beaufort_class_APR >= 1) && (beaufort_class_APR <= 12)) )  
            {
               //JOptionPane.showMessageDialog(null, "if wind direction is calm, wind force must be 0 Bf", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE); 

               int error_no = 3;
               write_Bf_error_message(error_no);

               checks_ok = false;
               reset_APR_wind_variables();
            }
         } // if ((beaufort_class_APR >= 0) && (beaufort_class_APR >= 0))
         
         // check beaufort class against wind dir
         //
         if (wind_dir_APR.trim().equals("") == false)                // so there was a wind direction selected
         {
            if ( (beaufort_class_APR == 0) && ((wind_dir_APR.trim().equals("variable") || (mywind.int_true_wind_dir > 0 && mywind.int_true_wind_dir <= 360)))  )
            {
               //JOptionPane.showMessageDialog(null, "if wind force is 0 Bf, wind direction must be calm", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
       
               int error_no = 4;
               write_Bf_error_message(error_no);
         
               checks_ok = false;
               reset_APR_wind_variables();
            }
         } // if (wind_dir_APR.trim().equals("") == false)
         
      } // if (!wind_input_APR_from_main_form)
      
      //System.out.println("+++++++++++ checks_ok = " + checks_ok);
      
      return checks_ok;
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/  
   private void compute_wind_APR()
   //public static void compute_wind_APR()        
   {
      // called from: Bf0_itemStateChanged()
      //              Bf1_itemStateChanged()
      //              etc.

      
      boolean checks_ok = true;
      
      
      if (!wind_input_APR_from_main_form_or_previous)
      {   
         // initialisation
         mywind.int_true_wind_dir = main.INVALID;
         mywind.int_true_wind_speed = main.INVALID;
   
      
         ////////////// wind dir
         //     
         if (wind_dir_APR.trim().equals("") == true)                                        // bijzonder geval
         {
            mywind.int_true_wind_dir = main.INVALID; 
         }
         else if (wind_dir_APR.trim().equals("calm") == true)                               // bijzonder geval
         {
            mywind.int_true_wind_dir = 0; 
         }
         else if (wind_dir_APR.trim().equals("variable") == true)                           // bijzonder geval
         {
            mywind.int_true_wind_dir = mywind.WIND_DIR_VARIABLE;
         }
         else
         {    
            try 
            {
               mywind.int_true_wind_dir = Integer.parseInt(wind_dir_APR.trim());
            }
            catch (NumberFormatException e)
            {
               mywind.int_true_wind_dir = main.INVALID; 
            }
         } // else  
      
         ////////////// wind speed
         //
         if (beaufort_class_APR >= 0 && beaufort_class_APR <= 12)
         {
            if (main.wind_units.trim().indexOf(main.M_S) != -1)
            {
               mywind.iw_code = "0";                                                 // estimated in m/s
               mywind.int_true_wind_speed = convert_bf_to_m_s(beaufort_class_APR);
            }
            else // so wind units is knots or unknown -> knots
            {
               mywind.iw_code = "3";                                                 // estimated in knots
               mywind.int_true_wind_speed = convert_bf_to_knots(beaufort_class_APR);
            }
         }
         else
         {
            mywind.int_true_wind_speed = main.INVALID; 
         }
      
         //System.out.println("+++++++++++ wind_dir_APR = " + wind_dir_APR);
         //System.out.println("+++++++++++ beaufort_class_APR = " + beaufort_class_APR);
     

         if ((mywind.int_true_wind_dir != main.INVALID) && (mywind.int_true_wind_speed != main.INVALID))
         {
            checks_ok = true;
         }
         else
         {
            checks_ok = false;
         }

      
         if (checks_ok)
         {
            // wind direction code (NB int_true_wind_dir in graden)
            //
            if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE)                          // variable
            {
               mywind.dd_code = "99";
            } // if (int_true_wind_dir == 9999)
            else // so no variable wind
            {
               int int_hulp_true_wind_dir;
               int int_hulp_dd_code;
                 
               // to prevent that 1 - 4 degrees become 00 in the obs
	            if (mywind.int_true_wind_dir >= 1 && mywind.int_true_wind_dir <= 4)
               {
	               int_hulp_true_wind_dir = 360;
               }
	            else
               {
	               int_hulp_true_wind_dir = mywind.int_true_wind_dir;
               }
            
               // rounding to tens of degrees
               int_hulp_dd_code = (int)Math.round((float)int_hulp_true_wind_dir / 10);
                 
               /* convert to code */
               if (int_hulp_dd_code <= 9)
               {
                  mywind.dd_code = "0" + Integer.toString(int_hulp_dd_code);
               }
               else if ((int_hulp_dd_code >= 10) && (int_hulp_dd_code <= 36))
               {
                  mywind.dd_code = Integer.toString(int_hulp_dd_code);
               }
               else
               {
                  mywind.dd_code = "//";
               }
            } // else (so no variable wind)


            // wind speed code (NB int_true_wind_speed in knots or m/s)
            //
            if (mywind.int_true_wind_speed <= 9)
            {
               mywind.ff_code = "0" + Integer.toString(mywind.int_true_wind_speed);
            }
            else if ((mywind.int_true_wind_speed >= 10) && (mywind.int_true_wind_speed <= 99))
            {
               mywind.ff_code = Integer.toString(mywind.int_true_wind_speed);
            }
            else if ((mywind.int_true_wind_speed >= 100) && (mywind.int_true_wind_speed <= 200)) // NB due to the conversion from BF12 -> m/s or knots he probably never gets here
            {
               mywind.ff_code = "99";
               mywind.fff00_code = Integer.toString(mywind.int_true_wind_speed);
            }
            else
            {
               mywind.ff_code = "//";
            }
          
         } // if (checks_ok = true)   
         else // check_ok == false
         {
            mywind.dd_code = "//";
            mywind.ff_code = "//";
         }

      
      
         //
         //
         //         IMMT 
         //
         //
      
      
         //if (checks_ok)
         //{
            // update wind fields on main screen 
            main.wind_fields_update();
         //}
      
      } // if (!wind_input_APR_from_main_form_or_previous)
      
      
      
      
      
      
      //if (checks_ok)
      //{
         // show true wind on bottom right panel
         //
         jLabel21.setForeground(DASHBOARD_grafiek_APR_radar.color_true_wind_arrow_actual);
         
         String show_units = "";
         if (main.wind_units.trim().indexOf(main.M_S) != -1)
         {
            show_units = "m/s";
         }   
         else
         {
            show_units = "kts";
         }   
            
         String show_wind_dir = "-";
         if (mywind.int_true_wind_dir == 0)
         {
            show_wind_dir = "calm"; 
         }   
         else if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE)
         {
            show_wind_dir = "var";
         }
         else if (mywind.int_true_wind_dir > 0 && mywind.int_true_wind_dir <= 360)
         {
            show_wind_dir = Integer.toString(mywind.int_true_wind_dir);
         }
         
         String show_wind_speed = "-";
         if (mywind.int_true_wind_speed >= 0 && mywind.int_true_wind_speed <= 400)
         {
            show_wind_speed = Integer.toString(mywind.int_true_wind_speed);
         }
         
         DASHBOARD_view_APR_radar.jLabel21.setText("true wind: " + show_wind_dir + "� " + show_wind_speed + " " + show_units);
        
     //}

   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   public static void reset_APR_wind_variables()
   //private void reset_APR_variables()
   {
      // called from: - check_wind_consistency_APR() [DASHBOARD_view_APR_radar.java]
      //             
      //
      
      //System.out.println("+++++++++++ reset_APR_variables()");
      //System.out.println("+++++++++++ wind_dir_APR = " + wind_dir_APR);
      //System.out.println("+++++++++++ beaufort_class_APR = " + beaufort_class_APR);
      
      
      mywind.int_true_wind_dir    = main.INVALID;
      mywind.int_true_wind_speed  = main.INVALID;
   
      wind_dir_APR                = "";
      beaufort_class_APR          = main.INVALID;
      //write_Bf_description(beaufort_class_APR);
      
      relative_wind_dir_APR       = "";
      relative_beaufort_class_APR = Integer.MAX_VALUE;
     
      jPanel1.repaint();
      
      //String test = jComboBox1.getSelectedItem().toString();
      //System.out.println("+++++++++++ 1) jComboBox1.getSelectedItem().toString() = " + test);
      
      buttonGroup1.clearSelection();
     
      jComboBox1.setSelectedItem(null);                     // NB 0 or -1 doesn't work! why not??
      
      jLabel21.setForeground(DASHBOARD_grafiek_APR_radar.color_true_wind_arrow_actual);
      jLabel21.setText("true wind: -");                             // calculated relative wind
      
      jLabel20.setForeground(DASHBOARD_grafiek_APR_radar.color_rel_wind_arrow_actual);
      jLabel20.setText("rel wind: -");                             // calculated relative wind
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   public static void reset_APR_wind_variables_2()
   //private void reset_APR_variables()
   {
      // called from:  - enable_disable_wind_controls_APR_radar()
      //
      
      //System.out.println("+++++++++++ reset_APR_variables_2()");
      //System.out.println("+++++++++++ wind_dir_APR = " + wind_dir_APR);
      //System.out.println("+++++++++++ beaufort_class_APR = " + beaufort_class_APR);
      
      
      mywind.int_true_wind_dir    = main.INVALID;
      mywind.int_true_wind_speed  = main.INVALID;
   
      wind_dir_APR                = "";
      beaufort_class_APR          = main.INVALID;
      //write_Bf_description(beaufort_class_APR);
      
      relative_wind_dir_APR       = "";
      relative_beaufort_class_APR = Integer.MAX_VALUE;
     
      //jPanel1.repaint();
      
      
      //buttonGroup1.clearSelection();
     
      //jComboBox1.setSelectedItem(null);                     // NB 0 or -1 doesn't work! why not??
      
      jLabel21.setForeground(DASHBOARD_grafiek_APR_radar.color_true_wind_arrow_actual);
      jLabel21.setText("true wind: -");                             // calculated relative wind
      
      jLabel20.setForeground(DASHBOARD_grafiek_APR_radar.color_rel_wind_arrow_actual);
      jLabel20.setText("rel wind: -");                             // calculated relative wind
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void enable_disable_wind_controls_APR_radar(boolean mode)
   {
      // called from paintComponents() [DASHBOARD_grafiek_APR_radar.java]
      
      // NB mode = false: no visual obs yet
      //    mode = true: visual obs possible
      
      //////////// TESTING ///////////
      //mode = true;
      ////////////////////////////// 
      
      //System.out.println("+++++++++++ enable_disable_wind_controls_APR_radar()");

      
      // NB if wind is not estimated or not measured TRUE then make no changes to the default (see initComponents1()[DASHBOARD_view_APR_radar.java]) disabled situation
      if ( (main.wind_source.equals(main.ESTIMATED_TRUE)) || (main.wind_source.equals(main.MEASURED_TRUE)) )
      {
         if (mode == false)   // no visual obs (xx.00 hr - xx.30 hr)
         {
            reset_APR_wind_variables_2();
            
            //relative_beaufort_class_APR = Integer.MAX_VALUE;
            //relative_wind_dir_APR = "";
            
            //beaufort_class_APR = Integer.MAX_VALUE;
            //wind_dir_APR = "";
         
            jLabel5.setText("");
            jLabel6.setText("");
            jLabel7.setText("");
            jLabel8.setText("");
            jLabel9.setText("");
            jLabel10.setText("");
            jLabel11.setText("");
            jLabel12.setText("");
            jLabel13.setText("");
            jLabel14.setText("");
            jLabel15.setText("");
            jLabel16.setText("");
            jLabel17.setText("");
            jLabel18.setText("");
         
            jComboBox1.setSelectedItem("");                    // NB -1 doesn't work
          
            buttonGroup1.clearSelection();
            
            //jLabel20.setForeground(DASHBOARD_grafiek_APR_radar.color_rel_wind_arrow_actual);
            //jLabel20.setText("rel wind: -");                   // calculated relative wind

         } // if (mode == false) 
      
         jButton1.setEnabled(mode);
         jComboBox1.setEnabled(mode);
      
         jRadioButton1.setEnabled(mode);
         jRadioButton2.setEnabled(mode);
         jRadioButton3.setEnabled(mode); 
         jRadioButton4.setEnabled(mode);
         jRadioButton5.setEnabled(mode);
         jRadioButton6.setEnabled(mode);
         jRadioButton7.setEnabled(mode);
         jRadioButton8.setEnabled(mode);
         jRadioButton9.setEnabled(mode); 
         jRadioButton10.setEnabled(mode);
         jRadioButton11.setEnabled(mode);
         jRadioButton12.setEnabled(mode);
         jRadioButton13.setEnabled(mode);
         jRadioButton14.setEnabled(mode);
      } //  if ( (!main.wind_source.equals(main.ESTIMATED_TRUE)) && (!main.wind_source.equals(main.MEASURED_TRUE)) )
   }

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void compute_relative_wind_APR_old()
   {
      // called from: Bf0_itemStateChanged()
      //              Bf1_itemStateChanged()
      //              etc.
      //
   
      // NB e.g. website: https://questeria.info/aw/
      //                  https://www.madinstro.net/sundry/navigation/windreal.html
      
      
      
   /*
      // source: https://stackoverflow.com/questions/47537292/i-cannot-calculate-apparent-wind-speed-have-no-idea-whats-wrong-formula-is-ea
      
      // W = true wind speed
      // V = boat speed
      // lambda = true pointing angle in degrees (0 = upwind, 180 = downwind)
      //SOURCE: https://en.wikipedia.org/wiki/Apparent_wind
      public void setApparentWindSpeed() {
        this.apparentWindSpeed = ApparentWindSpeed(getTrueWindSpeed(), getBoatSpeed(), Math.toRadians(trueWindAngle));
      }

      public double ApparentWindSpeed(double W, double V, double lambda){
        return  Math.sqrt(Math.pow(W,2) + Math.pow(V,2) + 2*W*V*Math.cos(lambda));
      }   
   */
      
      
      //SOURCE: https://en.wikipedia.org/wiki/Apparent_wind

      // V = velocity (boat speed over ground, always => 0)                   // SOG_APR_wind (FM13) / SOG_SPR (format #101)
      // W = true wind velocity (always => 0)                                 // mywind.int_true_wind_speed
      // alpha  = true pointing angle in degrees (0 = upwind, 180 = downwind) // RBH true wind
      // A = apparent wind velocity (always => 0)                             // 
      // beta = The angle of apparent wind
      
      double alpha = 0;
      double A = 0;
      double W = 0;             // mywind.int_true_wind_speed
      double V = 0;             // SOG_APR_wind (FM13) / SOG_SPR (format #101)
      double beta = 0;
      
      
      if ( (mywind.int_true_wind_speed >= 0.0 && mywind.int_true_wind_speed <= 400.0) && 
           (myposition.SOG_APR_wind >= 0.0 && myposition.SOG_APR_wind <= 50.0) && 
           (mywind.int_true_wind_dir >= 0.0 && mywind.int_true_wind_dir <= 360.0) &&
           (myposition.COG_APR_wind >= 0.0 && myposition.COG_APR_wind <= 360.0) )
      {
         W = mywind.int_true_wind_speed;                                      // knots or m/s
         
         if (main.wind_units.trim().indexOf(main.M_S) != -1)
         {
            V = myposition.SOG_APR_wind * main.KNOT_M_S_CONVERSION;           // m/s
         }
         else
         {
            V = myposition.SOG_APR_wind;                                      // knots
         }
         
         // RBH true wind [deg]
         alpha = mywind.int_true_wind_dir - myposition.COG_APR_wind;
         if (alpha < 0) alpha += 360;
      
         // relative wind speed [knots or m/s]
         A = Math.sqrt(Math.pow(W, 2) + Math.pow(V, 2) + 2 * W * V * Math.cos(Math.toRadians(alpha)));   
         
         // relative wind force [Bf]
         if (main.wind_units.trim().indexOf(main.M_S) != -1)
         {
            relative_beaufort_class_APR = convert_ms_to_bf((int)Math.round(A));
         }
         else
         {
            relative_beaufort_class_APR = convert_knots_to_bf((int)Math.round(A));     
         }
         
         // relative wind dir [rad]
         beta = Math.toDegrees(Math.acos((W * Math.cos(Math.toRadians(alpha)) + V ) / A));
         
         // relative wind dir [deg]
         relative_wind_dir_APR = Integer.toString((int)Math.round(beta));            // double rounding then -> int then -> String
         
      } // if ( (mywind.int_true_wind_speed >= 0.0 etc.
      else
      {
         relative_beaufort_class_APR    = main.INVALID;     
         relative_wind_dir_APR          = "";
      }
      
      
      //System.out.println("+++++++++++ W (true wind speed) = " + W);
      //System.out.println("+++++++++++ V (SOG_APR_wind) = " + V);
      //System.out.println("+++++++++++ alpha (RBH true wind dir [deg]) = " + alpha);
      //System.out.println("+++++++++++ A (relative wind speed) = " + A);
      //System.out.println("+++++++++++ beta (relative wind dir [rad]) = " + beta);
      //
      //System.out.println("+++++++++++ relative_wind_dir_APR [deg] = " + relative_wind_dir_APR);
      //System.out.println("+++++++++++ relative_beaufort_class_APR [Bf] = " + relative_beaufort_class_APR);
      
      
   } // private void compute_relative_wind()
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
public static int convert_knots_to_bf(int wind_speed)        
{
   // NB see also: convert_knots_to_bf() [DASHBOARD_grafiek_AWS_radar.java]
   
   int bf_class = Integer.MAX_VALUE;
   
   
   if (wind_speed >= 64 && wind_speed < 400)
   {
      bf_class = 12; 
   }
   else
   {   
      switch (wind_speed)
      {
         case 0:                                                                 bf_class = 0; break;
         case 1: case 2: case 3:                                                 bf_class = 1; break;                  
         case 4: case 5: case 6:                                                 bf_class = 2; break;    
         case 7: case 8: case 9: case 10:                                        bf_class = 3; break;  
         case 11: case 12: case 13: case 14: case 15: case 16:                   bf_class = 4; break;
         case 17: case 18: case 19: case 20: case 21:                            bf_class = 5; break;
         case 22: case 23: case 24: case 25: case 26: case 27:                   bf_class = 6; break;
         case 28: case 29: case 30: case 31: case 32: case 33:                   bf_class = 7; break;
         case 34: case 35: case 36: case 37: case 38: case 39: case 40:          bf_class = 8; break;
         case 41: case 42: case 43: case 44: case 45: case 46: case 47:          bf_class = 9; break;
         case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: bf_class = 10;break;
         case 56: case 57: case 58: case 59: case 60: case 61: case 62: case 63: bf_class = 11;break;
         default :                                                               bf_class = Integer.MAX_VALUE; break;
      } // switch
   } // else  
   
   
   return bf_class;
}   
   


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
public static int convert_ms_to_bf(int wind_speed)        
{
   // NB see also: convert_ms_to_bf() [DASHBOARD_grafiek_AWS_radar.java]
   
   int bf_class = Integer.MAX_VALUE;
   
   
   if (wind_speed >= 33 && wind_speed < 400)
   {
      bf_class = 12; 
   }
   else
   {   
      switch (wind_speed)
      {
         case 0:                                                                 bf_class = 0; break;
         case 1:                                                                 bf_class = 1; break;    
         case 2: case 3:                                                         bf_class = 2; break;    
         case 4: case 5:                                                         bf_class = 3; break;  
         case 6: case 7:                                                         bf_class = 4; break;
         case 8: case 9: case 10:                                                bf_class = 5; break;
         case 11: case 12: case 13:                                              bf_class = 6; break;
         case 14: case 15: case 16: case 17:                                     bf_class = 7; break;
         case 18: case 19: case 20:                                              bf_class = 8; break;
         case 21: case 22: case 23: case 24:                                     bf_class = 9; break;
         case 25: case 26: case 27: case 28:                                     bf_class = 10;break;
         case 29: case 30: case 31: case 32:                                     bf_class = 11;break;
         default :                                                               bf_class = Integer.MAX_VALUE; break;
      } // switch
   } // else   
   
   
   return bf_class;
}  
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void make_obs_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_make_obs_actionPerformed
      // TODO add your handling code here:
      
      if (main.mainClass != null)
      {   
         if ((turbowin.main.ICONIFIED & main.mainClass.getExtendedState()) == turbowin.main.ICONIFIED)
         {
            if (turbowin.main.trayIcon != null)
            {
               //main.mainClass.tray.remove(turbowin.main.trayIcon) ; 
               turbowin.main.tray.remove(turbowin.main.trayIcon) ;
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
   private void DASHBOARD_view_APR_Deiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_DASHBOARD_view_APR_Deiconified
      // TODO add your handling code here:
      
      
      System.out.println("--- APR Dashboard (jPanel1) deiconified");
      jPanel1.repaint();  
      
   }//GEN-LAST:event_DASHBOARD_view_APR_Deiconified

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void DASHBOARD_view_APR_windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_DASHBOARD_view_APR_windowClosed
      // TODO add your handling code here:
      if (dashboard_update_APR_timer_is_gecreeerd == true)  
      {
         if (dashboard_update_APR_timer.isRunning())
         {
            dashboard_update_APR_timer.stop();
         }
      }
      
      dashboard_update_APR_timer = null;
      dashboard_update_APR_timer_is_gecreeerd = false;      
      
      main.dashboard_form_APR_radar = null;
   }//GEN-LAST:event_DASHBOARD_view_APR_windowClosed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void write_Bf_description(int beaufort_class)
   //public static void write_Bf_description(int beaufort_class)        
   {
      /////// Bf description (covers a number of lines) ///////
            
         
      // reset Bf text lables to blanco and fonts to default
      //
      reset_Bf_text_labels();

      // for copyright lables (decreased font size)
      //
      currentFont = jLabel4.getFont();                            // jLabel4 will never be changed so can be taken as the default
      String fontName = currentFont.getFontName();
      int fontStyle = currentFont.getStyle();
      //int fontSize = currentFont.getSize();

      switch (beaufort_class)
      {
         case 0 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Sea like a mirror");
                  jLabel7.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel8.setFont(new Font(fontName, fontStyle, 10));
                     jLabel8.setText("image � N.C. Horner");
                  }
                  break;
                  
         case 1 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Ripples with the appearance of");                   
                  jLabel7.setText("scales are formed, but without");  
                  jLabel8.setText("foam crests");
                  jLabel9.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel10.setFont(new Font(fontName, fontStyle, 10));
                     jLabel10.setText("image � G.J. Simpson");
                  }
                  break; 
         case 2 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Small wavelets, still short");
                  jLabel7.setText("but more pronounced: crests"); 
                  jLabel8.setText("have a glassy appearance and");
                  jLabel9.setText("do not break");
                  jLabel10.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel11.setFont(new Font(fontName, fontStyle, 10));
                     jLabel11.setText("image � G.J. Simpson");
                  }
                  break;
         case 3:
                  jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Large wavelets; crests begin ");
                  jLabel7.setText("to break; foam of glassy");
                  jLabel8.setText("appearance; perhaps scattered");
                  jLabel9.setText("white horses");
                  jLabel10.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true) 
                  {
                     jLabel11.setFont(new Font(fontName, fontStyle, 10));
                     jLabel11.setText("image � I.G. McNeil");
                  }
                  break;
         case 4 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Small waves, becoming longer;");
                  jLabel7.setText("fairly frequent white horses"); 
                  jLabel8.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel9.setFont(new Font(fontName, fontStyle, 10));
                     jLabel9.setText("image � I.G. McNeil");
                  }
                  break;
         case 5 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Moderate waves, taking a more");
                  jLabel7.setText("pronounced long form; many");
                  jLabel8.setText("white horses are formed (chance");
                  jLabel9.setText("of somespray)");
                  jLabel10.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel11.setFont(new Font(fontName, fontStyle, 10));
                     jLabel11.setText("image � Crown");
                  }
                  break;
         case 6 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Large waves begin to form;");
                  jLabel7.setText("the white foam crests are");
                  jLabel8.setText("more extensive everywhere"); 
                  jLabel9.setText("(probably some spray)");
                  jLabel10.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel11.setFont(new Font(fontName, fontStyle, 10));
                     jLabel11.setText("image � I.G. McNeil");
                  }
                  break;
         case 7 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Sea heaps up and white foam");
                  jLabel7.setText("from breaking waves begins to");
                  jLabel8.setText("be blown in streaks along the");
                  jLabel9.setText("direction of the wind");  
                  jLabel10.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel11.setFont(new Font(fontName, fontStyle, 10));
                     jLabel11.setText("image � Crown");
                  }
                  break;
         case 8 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Moderately high waves of");
                  jLabel7.setText("greater length; edges of crests");
                  jLabel8.setText("begin to break into the");
                  jLabel9.setText("spindrift; the foam is blown in");
                  jLabel10.setText("well-marked streaks along the"); 
                  jLabel11.setText("direction of the wind");
                  jLabel12.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel13.setFont(new Font(fontName, fontStyle, 10));
                     jLabel13.setText("image � W.A.E. Smith");
                  }
                  break;
         case 9 : jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("High waves; dense streaks of");
                  jLabel7.setText("foam along the direction of");
                  jLabel8.setText("the wind; crests of waves begin");
                  jLabel9.setText("to topple, tumble and roll");
                  jLabel10.setText("over; spray mayaffect"); 
                  jLabel11.setText("visibility");
                  jLabel12.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel13.setFont(new Font(fontName, fontStyle, 10));
                     jLabel13.setText("image � J.P. Lacock");
                  }
                  break;
         case 10: jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Very high waves with long");
                  jLabel7.setText("overhanging crests; the");
                  jLabel8.setText("resulting foam, in greater");
                  jLabel9.setText("patches, is blown in dense");
                  jLabel10.setText("white streaks along the");
                  jLabel11.setText("direction of the wind; on the");
                  jLabel12.setText("whole, the surface of the sea");
                  jLabel13.setText("takes a white appearance; the");
                  jLabel14.setText("tumbling of the sea becomes");
                  jLabel15.setText("heavy and shock-like;");
                  jLabel16.setText("visibility affected");
                  jLabel17.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel18.setFont(new Font(fontName, fontStyle, 10));
                     jLabel18.setText("image � G. Allen");
                  }
                  break;
        case 11:  jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("Exceptionally high waves");
                  jLabel7.setText("(small and medium-sized ships");
                  jLabel8.setText("might be for a time lost to");
                  jLabel9.setText("view behind the waves); the");
                  jLabel10.setText("sea is completely covered with");
                  jLabel11.setText("long white patches of foam");
                  jLabel12.setText("lying along the direction of");
                  jLabel13.setText("the wind; everywhere the"); 
                  jLabel14.setText("edges of the wave crests are");
                  jLabel15.setText("blown into froth; visibility");
                  jLabel16.setText("affected");
                  jLabel17.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel18.setFont(new Font(fontName, fontStyle, 10));
                     jLabel18.setText("image � Crown");
                  }
                  break;
         case 12: jLabel5.setText("Bf " + Integer.toString(beaufort_class) + ":");
                  jLabel6.setText("The air is filled with foam");
                  jLabel7.setText("and spray; sea completely white");
                  jLabel8.setText("with driving spray; visibility"); 
                  jLabel9.setText("very seriously affected");
                  jLabel10.setText("");
                  if (!DASHBOARD_view_APR_radar.night_vision == true)
                  {
                     jLabel11.setFont(new Font(fontName, fontStyle, 10));
                     jLabel11.setText("image � J.F. Thompson");
                  }
                  break;
         default: break;
      } // switch (beaufort_class)
      
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   //public static void write_Bf_error_message(int error_no)
   private void write_Bf_error_message(int error_no)        
   {
      //System.out.println("+++++++++++ write_Bf_error_message() error_no = " + error_no);
      
      // initialisation
      reset_Bf_text_labels();
      jLabel5.setForeground(Color.RED);
      jLabel6.setForeground(Color.RED);
      
      if (error_no == 1)
      {
         //JOptionPane.showMessageDialog(null, "'wind speed units source' not selected (select: Maintenance -> Station data)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         jLabel5.setText("'wind speed units source' not selected");
         jLabel6.setText("(select: Maintenance -> Station data)"); 
      }  
      else if (error_no == 2)
      {
         //JOptionPane.showMessageDialog(null, "if wind direction is variable, wind force must be < 3 Bf", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
        jLabel5.setText("if wind direction is variable,");
        jLabel6.setText("wind force must be 1 or 2 Bf");
         
      }
      else if (error_no == 3)
      {
         //JOptionPane.showMessageDialog(null, "if wind direction is calm, wind force must be 0 Bf", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);  
         jLabel5.setText("if wind direction is calm,");
         jLabel6.setText("wind force must be 0 Bf");
         
         //System.out.println("+++++++++++ write_Bf_error_message() jLabel5.getText() = " + jLabel5.getText());
         //System.out.println("+++++++++++ write_Bf_error_message() jLabel6.getText() = " + jLabel6.getText());
      }
      else if (error_no == 4)
      {
         //JOptionPane.showMessageDialog(null, "if wind force is 0 Bf, wind direction must be calm", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         jLabel5.setText("if wind force is 0 Bf,");
         jLabel6.setText("wind direction must be calm");
      }
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/       
   private void reset_Bf_text_labels()        
   {
      // called from: - write_Bf_description()
      //              - write Bf_error_message()
      //              - initComponents1()
      //
      
      
      // reset all the labels to default fonts
      //
      currentFont = jLabel4.getFont();                            // jLabel4 will never be changed so can be taken as the default
      String fontName = currentFont.getFontName();
      int fontStyle = currentFont.getStyle();
      int fontSize = currentFont.getSize();
     
      jLabel5.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel6.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel7.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel8.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel9.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel10.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel11.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel12.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel13.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel14.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel15.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel16.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel17.setFont(new Font(fontName, fontStyle, fontSize));
      jLabel18.setFont(new Font(fontName, fontStyle, fontSize));
      
      jLabel5.setForeground(Color.BLACK);                             // see also write_Bf_error_message()
      jLabel6.setForeground(Color.BLACK);                             // see also write_Bf_error_message()
      
      jLabel5.setText("");
      jLabel6.setText("");
      jLabel7.setText("");
      jLabel8.setText("");
      jLabel9.setText("");
      jLabel10.setText("");
      jLabel11.setText("");
      jLabel12.setText("");
      jLabel13.setText("");
      jLabel14.setText("");
      jLabel15.setText("");
      jLabel16.setText("");
      jLabel17.setText("");
      jLabel18.setText("");
      
      
      if ( (!main.wind_source.equals(main.ESTIMATED_TRUE)) && (!main.wind_source.equals(main.MEASURED_TRUE)) )
      {
         jLabel5.setText("to insert TRUE wind data the");
         jLabel6.setText("wind meta data method must be"); 
         jLabel7.setText("set to estimated or measured");
         jLabel8.setText("'true speed and true direction'");
         jLabel9.setText("(Maintenance -> Station data)");
         
         jLabel20.setText("");    // rel wind; bottom wind input panel
         jLabel21.setText("");    // true wind; bottom wind input panel
      }
   }


   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private int convert_bf_to_knots(int Bf_class)
   //public static int convert_bf_to_knots(int Bf_class)
   {
      int true_wind_speed_knots = main.INVALID;
    
      switch (Bf_class)
      {
         case 0:  true_wind_speed_knots = Bf0_kts; break;
         case 1:  true_wind_speed_knots = Bf1_kts; break;                  
         case 2:  true_wind_speed_knots = Bf2_kts; break;                                              
         case 3:  true_wind_speed_knots = Bf3_kts; break;
         case 4:  true_wind_speed_knots = Bf4_kts; break;
         case 5:  true_wind_speed_knots = Bf5_kts; break;
         case 6:  true_wind_speed_knots = Bf6_kts; break;
         case 7:  true_wind_speed_knots = Bf7_kts; break;
         case 8:  true_wind_speed_knots = Bf8_kts; break;
         case 9:  true_wind_speed_knots = Bf9_kts; break;
         case 10: true_wind_speed_knots = Bf10_kts; break;
         case 11: true_wind_speed_knots = Bf11_kts; break;
         case 12: true_wind_speed_knots = Bf12_kts; break;
         default: true_wind_speed_knots = main.INVALID; break;  
      } // switch
  
      
      return true_wind_speed_knots;
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private int convert_bf_to_m_s(int Bf_class)
   {
      // source: https://www.metoffice.gov.uk/weather/guides/coast-and-sea/beaufort-scale
      
      int true_wind_speed_m_s = main.INVALID;
    
      switch (Bf_class)
      {
         case 0:  true_wind_speed_m_s = Bf0_ms; break;
         case 1:  true_wind_speed_m_s = Bf1_ms; break;                  
         case 2:  true_wind_speed_m_s = Bf2_ms; break;                                              
         case 3:  true_wind_speed_m_s = Bf3_ms; break;
         case 4:  true_wind_speed_m_s = Bf4_ms; break;
         case 5:  true_wind_speed_m_s = Bf5_ms; break;
         case 6:  true_wind_speed_m_s = Bf6_ms; break;
         case 7:  true_wind_speed_m_s = Bf7_ms; break;
         case 8:  true_wind_speed_m_s = Bf8_ms; break;
         case 9:  true_wind_speed_m_s = Bf9_ms; break;
         case 10: true_wind_speed_m_s = Bf10_ms; break;
         case 11: true_wind_speed_m_s = Bf11_ms; break;
         case 12: true_wind_speed_m_s = Bf12_ms; break;
         default: true_wind_speed_m_s = main.INVALID; break;  
      } // switch
  
      
      return true_wind_speed_m_s;
   }  
   
   
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
         java.util.logging.Logger.getLogger(DASHBOARD_view_APR_radar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(DASHBOARD_view_APR_radar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(DASHBOARD_view_APR_radar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(DASHBOARD_view_APR_radar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
      //</editor-fold>

      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable() {
         public void run() {
            new DASHBOARD_view_APR_radar().setVisible(true);
         }
      });
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   public static javax.swing.ButtonGroup buttonGroup1;
   public static javax.swing.JButton jButton1;
   public static javax.swing.JComboBox<String> jComboBox1;
   private javax.swing.JLabel jLabel1;
   public static javax.swing.JLabel jLabel10;
   public static javax.swing.JLabel jLabel11;
   public static javax.swing.JLabel jLabel12;
   public static javax.swing.JLabel jLabel13;
   public static javax.swing.JLabel jLabel14;
   public static javax.swing.JLabel jLabel15;
   public static javax.swing.JLabel jLabel16;
   public static javax.swing.JLabel jLabel17;
   public static javax.swing.JLabel jLabel18;
   public static javax.swing.JLabel jLabel19;
   private javax.swing.JLabel jLabel2;
   public static javax.swing.JLabel jLabel20;
   private static javax.swing.JLabel jLabel21;
   private javax.swing.JLabel jLabel3;
   private static javax.swing.JLabel jLabel4;
   public static javax.swing.JLabel jLabel5;
   public static javax.swing.JLabel jLabel6;
   public static javax.swing.JLabel jLabel7;
   public static javax.swing.JLabel jLabel8;
   public static javax.swing.JLabel jLabel9;
   /*
   private javax.swing.JPanel jPanel1;
   */ private static DASHBOARD_grafiek_APR_radar jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JPanel jPanel5;
   private static javax.swing.JPanel jPanel6;
   public static javax.swing.JRadioButton jRadioButton1;
   public static javax.swing.JRadioButton jRadioButton10;
   public static javax.swing.JRadioButton jRadioButton11;
   public static javax.swing.JRadioButton jRadioButton12;
   public static javax.swing.JRadioButton jRadioButton13;
   public static javax.swing.JRadioButton jRadioButton14;
   public static javax.swing.JRadioButton jRadioButton2;
   public static javax.swing.JRadioButton jRadioButton3;
   public static javax.swing.JRadioButton jRadioButton4;
   public static javax.swing.JRadioButton jRadioButton5;
   public static javax.swing.JRadioButton jRadioButton6;
   public static javax.swing.JRadioButton jRadioButton7;
   public static javax.swing.JRadioButton jRadioButton8;
   public static javax.swing.JRadioButton jRadioButton9;
   // End of variables declaration//GEN-END:variables

   
   private final int DELAY_UPDATE_APR_RADAR_SENSOR_LOOP                 = 60000; // 1 min                 // time in millisec to wait after timer is started to fire first event (10 min = 10 * 1000 * 60 * 10 = 600000)
   private final int INITIAL_DELAY_UPDATE_APR_RADAR_SENSOR_LOOP         = 0; // 1000 = 1 sec              // time in millisec to wait after timer is started to fire first event

   public static final int Bf0_ms  = 0;
   public static final int Bf1_ms  = 1;
   public static final int Bf2_ms  = 3;
   public static final int Bf3_ms  = 5;
   public static final int Bf4_ms  = 7;
   public static final int Bf5_ms  = 10;
   public static final int Bf6_ms  = 12;
   public static final int Bf7_ms  = 15;
   public static final int Bf8_ms  = 19;
   public static final int Bf9_ms  = 23;
   public static final int Bf10_ms = 27;
   public static final int Bf11_ms = 31;
   public static final int Bf12_ms = 35;
  
   public static final int Bf0_kts  = 0;
   public static final int Bf1_kts  = 2;
   public static final int Bf2_kts  = 5;
   public static final int Bf3_kts  = 8;
   public static final int Bf4_kts  = 13;
   public static final int Bf5_kts  = 19;
   public static final int Bf6_kts  = 24;
   public static final int Bf7_kts  = 30;
   public static final int Bf8_kts  = 37;
   public static final int Bf9_kts  = 44;
   public static final int Bf10_kts = 51;
   public static final int Bf11_kts = 59;
   public static final int Bf12_kts = 66;
   
   private JPopupMenu popup;
   private static Color background_color_panel1;
   private static Color background_color_panel2;
   private static Color background_color_panel3;
   private static Color background_color_panel4;
   private static Color background_color_panel5;
   private static Color background_color_panel6;
 
   public static Timer dashboard_update_APR_timer;
   public static boolean dashboard_update_APR_timer_is_gecreeerd;
   public static boolean night_vision;
   public static int width_APR_radar_dashboard;
   public static int height_APR_radar_dashboard;
   public static int beaufort_class_APR                                    = main.INVALID;             // for the correct background photo of the radar screen
   public static String wind_dir_APR                                       = "";
   
   public static int relative_beaufort_class_APR                           = main.INVALID;             // for the correct background photo of the radar screen
   public static String relative_wind_dir_APR                              = "";
   public static boolean wind_input_APR_from_main_form_or_previous         = false;                    // for yes/no perform the consistency_checks
   
   public static Font currentFont;
   
   public static int x_line_satellite_link = 0;               // clickable text satellite link
   public static int y_line_satellite_link = 0;               // clickable text satellite link
   public static TextLayout layout_satellite_link;                    // clickable text satellite link

   public static double x1_block          = 0;
   public static double y1_block          = 0;
   public static Rectangle2D bounds;
}
