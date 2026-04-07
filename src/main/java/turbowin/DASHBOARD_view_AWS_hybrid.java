/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package turbowin;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.Timer;

/**
 *
 * @author marti
 */
public class DASHBOARD_view_AWS_hybrid extends javax.swing.JFrame {

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
   private void initComponents1()
   {
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
         }
      });
      popup.add(menuItem2);  
      
      popup.add(new JSeparator()); // SEPARATOR
      
      //
      //////////////////////// container ship I  /////////////////
      //
      JMenuItem menuItem3 = new JMenuItem("container ship I");
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
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem3);  

      //
      //////////////////////// container ship II  /////////////////
      //
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
      
      
      //
      //////////////////////// bulk carrier I /////////////////
      //
      JMenuItem menuItem4 = new JMenuItem("bulk carrier I");
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
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem4); 
      
      
      //
      //////////////////////// bulk carrier II /////////////////
      //
      JMenuItem menuItem28 = new JMenuItem("bulk carrier II");
      menuItem28.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.BULK_CARRIER_2;
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
      popup.add(menuItem28);  

      
      //
      //////////////////////// oil tanker  /////////////////
      //
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
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem5);  
 
      //
      //////////////////////// LNG tanker I  /////////////////
      //
      JMenuItem menuItem6 = new JMenuItem("LNG tanker I");
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
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem6);  
      
      //
      //////////////////////// LNG tanker II  /////////////////
      //
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
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem17);        


      //
      //////////////////////// passenger ship  /////////////////
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
      //////////////////////// neutral ship  /////////////////
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
      //////////////////////// general cargo I  /////////////////
      //
      JMenuItem menuItem9 = new JMenuItem("general cargo I");
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
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem9);
      
      //
      //////////////////////// general cargo II /////////////////
      //
      JMenuItem menuItem16 = new JMenuItem("general cargo II");
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
               //main.set_muffin();
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem16);       
      
      //
      //////////////////////// general cargo III  /////////////////
      //
      JMenuItem menuItem124 = new JMenuItem("general cargo III");
      menuItem124.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.GENERAL_CARGO_SHIP_3;
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
      popup.add(menuItem124);    
      

      //
      //////////////////////// heavy-lift I  /////////////////
      //
      JMenuItem menuItem125 = new JMenuItem("heavy-lift I");
      menuItem125.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.HEAVY_LIFT_1;
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
      popup.add(menuItem125); 


      //
      //////////////////////// heavy-lift II  /////////////////
      //
      JMenuItem menuItem126 = new JMenuItem("heavy-lift II");
      menuItem126.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.HEAVY_LIFT_2;
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
      popup.add(menuItem126);       
      
      
      //
      //////////////////////// research vessel  /////////////////
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
      
      
      JMenuItem menuItem11 = new JMenuItem("Ro-Ro ship I");
      menuItem11.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.RO_RO_SHIP_1;
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
      
     
      JMenuItem menuItem27 = new JMenuItem("Ro-Ro ship II");
      menuItem27.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.RO_RO_SHIP_2;
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
      popup.add(menuItem27);  

      
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
      

      JMenu menuItem21 = new JMenu("tall ship");
      popup.add(menuItem21); 

      JMenuItem menuItem21_full_rigged_3 = new JMenuItem("full-rigged, 3 masts");
      menuItem21.add(menuItem21_full_rigged_3);

      menuItem21_full_rigged_3.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.FULL_RIGGED_3; 
            //save_ship_dashboard_selection();
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

      JMenuItem menuItem21_full_rigged_4 = new JMenuItem("full-rigged, 4 masts");
      menuItem21.add(menuItem21_full_rigged_4);

      menuItem21_full_rigged_4.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.FULL_RIGGED_4;
            //save_ship_dashboard_selection();
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

      JMenuItem menuItem21_full_rigged_5 = new JMenuItem("full-rigged, 5 masts");
      menuItem21.add(menuItem21_full_rigged_5);

      menuItem21_full_rigged_5.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.FULL_RIGGED_5;
            //save_ship_dashboard_selection();
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
      
      JMenuItem menuItem21_barque_3 = new JMenuItem("barque, 3 masts");
      menuItem21.add(menuItem21_barque_3);

      menuItem21_barque_3.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.BARQUE_3;
            //save_ship_dashboard_selection();
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
 
      JMenuItem menuItem21_barque_4 = new JMenuItem("barque, 4 masts");
      menuItem21.add(menuItem21_barque_4);

      menuItem21_barque_4.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.BARQUE_4;
            //save_ship_dashboard_selection();
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
      
      JMenuItem menuItem21_barque_5 = new JMenuItem("barque, 5 masts");
      menuItem21.add(menuItem21_barque_5);

      menuItem21_barque_5.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.BARQUE_5;
            //save_ship_dashboard_selection();
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
      
      
      //
      //////////////////////// yacht /////////////////
      //
      JMenuItem menuItem22 = new JMenuItem("superyacht");
      menuItem22.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.YACHT;
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
               main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem22);  
      
      
      //
      //////////////////////// chemical/product tanker /////////////////
      //
      JMenuItem menuItem123 = new JMenuItem("chemical / product tanker");
      menuItem123.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.CHEMICAL_TANKER;
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
      popup.add(menuItem123);      
      
      JMenuItem menuItem29 = new JMenuItem("sailing yacht");
      menuItem29.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.SAILING_YACHT;
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
      popup.add(menuItem29);  

      JMenuItem menuItem30 = new JMenuItem("catamaran");
      menuItem30.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.ship_type_dashboard = main.CATAMARAN;
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
      popup.add(menuItem30);
      
   

      popup.add(new JSeparator()); // SEPARATOR
      
      JMenuItem menuItem19 = new JMenuItem("default font");
      menuItem19.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.dashboard_font = "0";
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
               main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem19);  
      
      JMenuItem menuItem20 = new JMenuItem("bigger font");
      menuItem20.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            main.dashboard_font = "1";
            repaint();
            
            // write meta data to muffins or configuration files
            if (main.offline_mode_via_cmd == true)                          // also if the turbowin_launcher is present (JPMS)
            {
                main.schrijf_configuratie_regels();          
            }
            else // so offline_via_jnlp mode or online (webstart) mode
            {
               main.schrijf_configuratie_regels();
            }  
         }
      });
      popup.add(menuItem20);      

      MouseListener popupListener = new DASHBOARD_view_AWS_hybrid.PopupListener();
      addMouseListener(popupListener);
      
      
      // title
      setTitle(main.APPLICATION_NAME + " Automatic Weather Station Dashboard [hybrid]");
   
      // NB see below, otherwise if you select Dashboard -> AWS for the second, third or xth time it wil first display the situation 
      //    of the moment that the dasboard was closed, and after approx 1 minute it will be updated. 
      //    NOW it will update the dashboard immediately
      jPanel1.repaint();     
   }
   
   
   
   
   /** Creates new form DASHBOARD_view_AWS_hybrid */
   public DASHBOARD_view_AWS_hybrid() 
   {
      initComponents();
      initComponents1();
      init_dasboard_AWS_hybrid_timer();
        
      if (main.theme_mode.equals(main.THEME_TRANSPARENT))   
      {
         setOpacity(0.75f);
      } // else if (theme_mode.equals(THEME_TRANSPARENT))  
   }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {

      jPanel5 = new javax.swing.JPanel();
      jPanel2 = new javax.swing.JPanel();
      jPanel3 = new javax.swing.JPanel();
      jLabel2 = new javax.swing.JLabel();
      jPanel4 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      jLabel3 = new javax.swing.JLabel();
      jButton1 = new javax.swing.JButton();
      /* jPanel1 = ne javax.swing.jPanel();
      */
      jPanel1 = new DASHBOARD_grafiek_AWS_hybrid();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      addWindowListener(new java.awt.event.WindowAdapter()
      {
         public void windowClosed(java.awt.event.WindowEvent evt)
         {
            Dashboard_view_AWS_hybrid_windowClosed(evt);
         }
         public void windowDeactivated(java.awt.event.WindowEvent evt)
         {
            Dashboard_view_AWS_hybrid_windowDeiconified(evt);
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
         .addGap(0, 564, Short.MAX_VALUE)
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
         .addGap(0, 564, Short.MAX_VALUE)
      );

      getContentPane().add(jPanel2, java.awt.BorderLayout.EAST);

      jPanel3.setPreferredSize(new java.awt.Dimension(1209, 40));

      jLabel2.setText("--- Pressure MSL = 1 min median at MSL. Temp, RH, SST = 1 min median at sensor ht. Wind speed and dir = 10 min average at sensor ht. Wind gust = max wind in last 10 min at sensor ht. Speed, Course, Heading = 10 min average ---");

      javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1225, Short.MAX_VALUE)
            .addContainerGap())
      );
      jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
      );

      getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

      jPanel4.setPreferredSize(new java.awt.Dimension(1209, 40));

      jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
      jLabel1.setText("--- right click for night colors and ship type ---");

      jButton1.setText("make visual observation");
      jButton1.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            make_obs_actionPerformed(evt);
         }
      });

      javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
      jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(210, 210, 210)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
            .addGap(210, 210, 210)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
      );
      jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(jButton1))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );

      getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

      jPanel1.setBackground(java.awt.Color.lightGray);
      jPanel1.addComponentListener(new java.awt.event.ComponentAdapter()
      {
         public void componentResized(java.awt.event.ComponentEvent evt)
         {
            DASHBOARD_AWS_hybrid_componentResizedHandler(evt);
         }
      });

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 1225, Short.MAX_VALUE)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 564, Short.MAX_VALUE)
      );

      getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

      pack();
   }// </editor-fold>//GEN-END:initComponents

   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
   private void DASHBOARD_AWS_hybrid_componentResizedHandler(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_DASHBOARD_AWS_hybrid_componentResizedHandler
      // TODO add your handling code here:
      System.out.println("--- AWS Dashboard hybrid (jPanel1) size = " + DASHBOARD_view_AWS_hybrid.jPanel1.getSize());
      
      width_AWS_hybrid_dashboard = jPanel1.getWidth();
      height_AWS_hybrid_dashboard = jPanel1.getHeight();
      
      jPanel1.repaint();  
   }//GEN-LAST:event_DASHBOARD_AWS_hybrid_componentResizedHandler

   
   
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
               main.mainClass.tray.remove(turbowin.main.trayIcon) ; 
               //System.out.println("remove icon");
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
   private void Dashboard_view_AWS_hybrid_windowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_Dashboard_view_AWS_hybrid_windowDeiconified
      // TODO add your handling code here:
      
      System.out.println("--- AWS Dashboard hybrid (jPanel1) deiconified");
      jPanel1.repaint();  
   }//GEN-LAST:event_Dashboard_view_AWS_hybrid_windowDeiconified

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/     
   private void Dashboard_view_AWS_hybrid_windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_Dashboard_view_AWS_hybrid_windowClosed
      // TODO add your handling code here:
      
      if (dashboard_update_timer_AWS_hybrid_is_gecreeerd == true)  
      {
         if (dashboard_update_timer_AWS_hybrid.isRunning())
         {
            dashboard_update_timer_AWS_hybrid.stop();
         }
      }
      
      dashboard_update_timer_AWS_hybrid = null;
      
      dashboard_update_timer_AWS_hybrid_is_gecreeerd = false; 
   }//GEN-LAST:event_Dashboard_view_AWS_hybrid_windowClosed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void init_dasboard_AWS_hybrid_timer()
{
   // updating/displaying received AWS sensor data (not from file), timer scheduled
   //
   // called from: DASHBOARD_view_AWS_hybrid() [DASHBOARD_view_AWS_hybrid.java]
   
   ActionListener update_dashboard_AWS_hybrid_action = new ActionListener()
   {
      @Override
      public void actionPerformed(ActionEvent e)
      {
         jPanel1.repaint();                                                        // main panel
      } 
   };  


   // main loop for updating AWS hybrid dashboard
   dashboard_update_timer_AWS_hybrid = new Timer(DELAY_UPDATE_AWS_HYBRID_SENSOR_LOOP, update_dashboard_AWS_hybrid_action);
   dashboard_update_timer_AWS_hybrid.setRepeats(true);                                               // false = only one action
   dashboard_update_timer_AWS_hybrid.setInitialDelay(INITIAL_DELAY_UPDATE_AWS_HYBRID_SENSOR_LOOP);   // time in millisec to wait after timer is started to fire first event
   dashboard_update_timer_AWS_hybrid.setCoalesce(true);                                              // by default true, but to be certain
   dashboard_update_timer_AWS_hybrid.restart();
   dashboard_update_timer_AWS_hybrid_is_gecreeerd = true;
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
            java.util.logging.Logger.getLogger(DASHBOARD_view_AWS_hybrid.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DASHBOARD_view_AWS_hybrid.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DASHBOARD_view_AWS_hybrid.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DASHBOARD_view_AWS_hybrid.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DASHBOARD_view_AWS_hybrid().setVisible(true);
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
   */private static DASHBOARD_grafiek_AWS_hybrid jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JPanel jPanel5;
   // End of variables declaration//GEN-END:variables

   public static int width_AWS_hybrid_dashboard;
   public static int height_AWS_hybrid_dashboard;
   public static Timer dashboard_update_timer_AWS_hybrid;
   public static boolean dashboard_update_timer_AWS_hybrid_is_gecreeerd;
   public static boolean night_vision;
   private final int DELAY_UPDATE_AWS_HYBRID_SENSOR_LOOP                  = 60000; // 1 min                          // time in millisec to wait after timer is started to fire first event (10 min = 10 * 1000 * 60 * 10 = 600000)
   private final int INITIAL_DELAY_UPDATE_AWS_HYBRID_SENSOR_LOOP          = 0; // 1000 = 1 sec              // time in millisec to wait after timer is started to fire first event
   //public static Timer dashboard_update_AWS_hybrid_timer;
   //public static boolean dashboard_update_AWS_hybrid_timer_is_gecreeerd;
   private JPopupMenu popup;
   private static Color background_color_panel1;
   private static Color background_color_panel2;
   private static Color background_color_panel3;
   private static Color background_color_panel4;
   private static Color background_color_panel5;
   public static Color deck_color                                          = null;
   public static Color tank_color                                          = null;
}
