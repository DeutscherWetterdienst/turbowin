/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbowin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author marti
 */
public class ship 
{
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public ship()
   {
      color_white                        = Color.WHITE;
      color_black                        = Color.BLACK;
      color_life_boat                    = new Color(255, 69, 0);                                // orange-red
      color_deck_lng_tanker_II           = new Color(155, 11, 11);                               // red-brown
      color_deck_passenger_ship          = new Color(222,184, 135);   
      color_acc_passenger_ship           = new Color(255, 218, 185);
      color_deck_reefer_ship             = new Color(225, 225, 225);
      color_deck_general_cargo_classic   = new Color(124, 112, 98);                              // gray-brown
      color_deck_general_cargo_ship      = new Color(210, 105, 30);
      color_deck_general_cargo_ship_II   = new Color(60, 179, 113);                              // medium sea green
      color_deck_research_vessel         = new Color(36, 128, 55); 
      color_deck_container_ship_II       = new Color(181, 101, 29);                              // light-brown
      color_deck1_ferry                  = new Color(70, 130, 180);                              // main deck        // steel blue
      color_deck2_ferry                  = new Color(123, 163, 201);                             // bridge deck      // between steel blue and light steel blue
      color_deck3_ferry                  = new Color(176, 196, 222);                             // upper/top deck   // light steel blue
      color_funnel_ferry                 = new Color(173, 216, 220);//new Color(70, 130, 180);
      color_deck_ro_ro                   = Color.DARK_GRAY;
      color_lanes_ro_ro                  = new Color(255, 255, 0, 128);                          // yellow, semi transparent
      color_crane_research_vessel        = new Color(204, 69, 50, 255);                          // red-brown
      color_hoist_research_vessel        = new Color(0, 102, 204, 255);                          // blue
      color_cradle_research_vessel       = new Color(255, 165, 0, 255);                          // orange
      color_night_crane_research_vessel  = new Color(204, 69, 50, 64);                           // red-brown, semi transparent
      color_night_hoist_research_vessel  = new Color(0, 102, 204, 64);                           // blue, semi transparent
      color_night_cradle_research_vessel = new Color(255, 165, 0, 64);                           // orange, semi transparent
      color_hatches_general_cargo_ship   = new Color(0, 128, 0);                                 // green
      color_hatches_general_cargo_ship_II= new Color(0, 128, 0);                                 // green
      color_pool_1                       = new Color(100, 149, 237);                             // starboard and aft swimming pool edge; color between inner swimming pool and outer pool; for depth effect
      color_pool_2                       = new Color(0, 0, 205);                                 // inner swimming pool (pool bottom)
      color_pool_3                       = new Color(0, 255, 255, 100);                          // outer swimming pool (covering pool bottom and edges (semi transparent)
      color_pool_4                       = new Color(212, 241, 249);
      color_funnel_passenger_ship        = Color.LIGHT_GRAY;
      color_bridge_lng_tanker_II         = new Color(90, 141, 185);                              // grey-blue
      color_bridge_container_ship        = new Color(245,255,250); 
      color_bridge_oil_tanker            = Color.GRAY;  
      color_bridge_research_vessel       = new Color(60, 159, 113); 
      color_bridge_ro_ro_ship            = new Color(60, 159, 113);
      color_bulk_carrier                 = new Color(204, 69, 50, 255);                          // red-brown
      color_lng_tanks                    = Color.RED;
      color_deck_lng_tanker              = Color.LIGHT_GRAY; 
      color_deck_oil_tanker              = new Color(0, 100, 70);                                // green-blue
      color_pipes_oil_tanker             = Color.LIGHT_GRAY;  
      color_deck_steel_blue              = new Color(176,196,222, 255);                          // light steel blue (NB alpha 255 = 100% opaque)
      color_medium_gray                  = new Color(128,128,128);
      int alpha_container                = 128;                                                  // semi transparent
      color_container_1                  = new Color(255, 51, 51, alpha_container);              // red      
      color_container_2                  = new Color(178, 34, 34, alpha_container);              // firebrick red
      color_container_3                  = new Color(240,128,128, alpha_container);              // light coral red
      color_container_4                  = new Color(70,130,180, alpha_container);               // steel blue
      color_container_5                  = new Color(30,144,255, alpha_container);               // dodger blue
      color_container_6                  = new Color(65,105,225, alpha_container);               // royal blue
      color_container_7                  = new Color(255,245,238, alpha_container);              // sea shell white
      color_container_8                  = new Color(245,255,250, alpha_container);              // mint crean white
      color_container_9                  = new Color(244,164,96, alpha_container);               // sandy brown
      color_container_10                 = new Color(255,127,80, alpha_container);               // coral red
      color_derricks                     = new Color(255,191,0);                                 // yellow-orange
      color_tank_deck_fruit_juice_tanker = new Color(222,195,90);                                // oker // tank deck(s)
      color_bridge_fruit_juice           = new Color(22,180,210);                                // light blue/green
      color_aft_deck_fruit_juice_tanker  = new Color(22,173,200);
      color_tanks_fruit_juice            = new Color(240,240,240);                               // off white
      color_deck_tall_ship               = new Color(205, 133, 63);                              // brown (peru)
      color_masts_tall_ship              = new Color(160, 82, 45);                               // brown (sienna)
      color_deck_yacht                   = new Color(222, 184, 135);                             // brown (burlywood)
   } 
 
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_general_cargo_ship(Graphics2D g2d, double wind_rose_diameter, boolean night_mode, Color deck_color) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/
      //
      
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;
      
      
      // deck color (might be set by pop-up submenu)
      //
      if (deck_color != null)
      {
         color_deck_general_cargo_ship = deck_color;                         // NB if var deck_color not known -> default will be used 
      }

      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      //////////// bow ////////
      //
      x = 0;                                                                // control point quadTo; relative to origin (centre of wind rose)
      //y = -(wind_rose_diameter / 2.0);                                      // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 2;                         // NB factor 2.0 is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.1);         // NB factor 0.1 depends on factor used in bow_height_virtual
      
      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; //x;
      y1 = y + bow_height_virtual;
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length_main = Math.abs(y1) * 2.3;                       // so the length of the main mid section; NB factor 2.3 depends on the factor used at bow_height_virtual and y

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4 - (ship_breadth / 7);
      x6 = x3 + (ship_breadth / 7);
      y3 = y1 + ship_length_main;
      y4 = y3;
      y5 = y3 + (ship_breadth * 0.5);
      y6 = y5;

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_black);
      g2d.draw(ship);
      
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_general_cargo_ship);
      }
      g2d.fill(ship);

      
      /////////////// ship bridge /////////
      //
      double bridge_height = 7;                     
      //double acc_height = 20;                        
      double acc_height = (y6 - y3) / 2;                        
      double bridge_indention = ship_breadth / 5;
      double dist_origin_bridge = y3 - bridge_height;
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);

      
      ////////////// hatches ////////////
      //
      double hatch_breath = ship_breadth * 0.8;
      double hatch_length = hatch_breath / 2;
      double hatch_intermediate = 2;   // distance between 2 hatches
      double y_hatch = y1;
      double x_hatch = -hatch_breath / 2;

      for (int i = 0; i < 50; i++) // max 50 hatches
      {
         if (y_hatch + (hatch_length + hatch_intermediate / 2) < (ship_length_main / 2 - bridge_height)) 
         {
            if (night_mode)
            {
               g2d.setPaint(color_medium_gray);
            }
            else
            {
               //if ((color_deck_general_cargo_ship == DASHBOARD_view_APR_radar.DECK_COLOR_GREEN) || (color_deck_general_cargo_ship == DASHBOARD_view_APR_radar.DECK_COLOR_LIGHT_GREEN))
               //if ((color_deck_general_cargo_ship.getRGB() == DASHBOARD_view_APR_radar.DECK_COLOR_GREEN.getRGB()) || (color_deck_general_cargo_ship.getRGB() == DASHBOARD_view_APR_radar.DECK_COLOR_LIGHT_GREEN.getRGB()))
               if ((color_deck_general_cargo_ship.getRGB() == DECK_COLOR_GREEN.getRGB()) || (color_deck_general_cargo_ship.getRGB() == DECK_COLOR_LIGHT_GREEN.getRGB()))  
               {
                  g2d.setPaint(color_hatches_general_cargo_ship);                                 // greenish color
               }
               else
               {
                  g2d.setPaint(Color.LIGHT_GRAY); 
               }
            }
            
            g2d.fill(new Rectangle2D.Double(x_hatch, y_hatch, hatch_breath, hatch_length));
            
            // hatch sections
            g2d.setColor(Color.DARK_GRAY);
            double y_sub_hatch = y_hatch;
            for (int k = 0; k < 100; k++)
            {
               if (y_sub_hatch < (y_hatch + hatch_length))
               {
                  g2d.draw(new Line2D.Double(x_hatch, y_sub_hatch, (x_hatch + hatch_breath), y_sub_hatch));
                  
                  y_sub_hatch = y_sub_hatch + 10;
               }
               else
               {
                  break;
               }
            } //  for (int k = 0; k < 100; k++)  
            
            y_hatch += hatch_length + hatch_intermediate;
         } 
         else 
         {
            break;
         }

      } // for (int i = 0; i < 50; i++)      
      
      
      ////////////// crane pillars ////////////
      //
      //                          ___
      //                         /   \
      //       (x1_arm,y1_arm)  |     | (x2_arm,y2_arm)     <-- pillar crane
      //                        |\___/|
      //                        |     |
      //                        |     |
      //                        |     |
      //                        |     |                     <-- arm crane
      //                        |     |
      //                        |     |
      //                        |     |
      //                         -----
      //        (x3_arm,y3_arm)          (x4_arm,y4_arm)
      //
      //
      
      double dist_between_cranes = ship_length_main / 3;
      double width_crane_pillar = ship_breadth * 0.15; //0.2;
      double height_crane_pillar = width_crane_pillar;
      double x_pillar_crane = x2 - width_crane_pillar;//0;
      double y_pillar_crane = 0;
      
      double x1_arm = x_pillar_crane;
      double x2_arm = x_pillar_crane + width_crane_pillar;
      double arm_slope = 4;
      double x3_arm = x1_arm + arm_slope;
      double x4_arm = x2_arm - arm_slope;
      
      // if the (ship) figure is very small
      if (x4_arm < x3_arm)
      {
         x3_arm = x1_arm;
         x4_arm = x2_arm;
      }
      
      double y1_first_arm = 0;                 // counting from the bow
      double y1_second_arm = 0;                // counting from the bow
      double y1_third_arm = 0;                 // counting from the bow
      
      for (int p = 0; p < 3; p++)
      {
         y_pillar_crane = y2 + (p * dist_between_cranes);
         
         Shape shape_crane_pillar = new Ellipse2D.Double(x_pillar_crane, y_pillar_crane, width_crane_pillar, height_crane_pillar);

         if (night_mode)
         {
            g2d.setPaint(Color.LIGHT_GRAY);
         }
         else
         {
            g2d.setPaint(color_white);
         }
         g2d.fill(shape_crane_pillar);
      
         g2d.setColor(Color.LIGHT_GRAY);
         g2d.setStroke(new BasicStroke(1.0f));
         g2d.draw(shape_crane_pillar);
         
         // collect data for the arms of the cranes
         if (p == 0)
         {
            y1_first_arm = y_pillar_crane + (height_crane_pillar / 2);
         }
         else if (p == 1)
         {
            y1_second_arm = y_pillar_crane + (height_crane_pillar / 2);
         }
         else if (p == 2)
         {
            y1_third_arm = y_pillar_crane + (height_crane_pillar / 2);
         }
      } // for (int p = 0; p < 3; p++)
      
      
      ///////////////// crane arms /////////////
      //
      //g2d.setStroke(new BasicStroke(3.0f)); 
      // if the (ship) figure is very small (thickness crane arms)
      if (x4_arm < x3_arm)
      {
         g2d.setStroke(new BasicStroke(2.0f)); 
      }
      else // 'normal' figure
      {
         g2d.setStroke(new BasicStroke(3.0f));  
      }
      
      
      if (night_mode)
      {
         g2d.setColor(Color.LIGHT_GRAY);
      }
      else
      {
         g2d.setColor(color_white);
      }
      
      double y3_first_arm = y1_second_arm - width_crane_pillar;   // NB y1_first_arm to y3_first_arm - 4 = arm length
      double y3_second_arm = y1_third_arm - width_crane_pillar;   // NB y1_second_arm to y3_second_arm - 4 = arm length
      double y3_third_arm = y4 - width_crane_pillar;              // NB y1_third_arm to y4 (start bridge) - 4 = arm length            
      
      for (int p = 0; p < 3; p++)
      {   
         double y1_arm = 0;
         double y2_arm = 0;
         double y3_arm = 0;
         double y4_arm = 0;
         
         if (p == 0)
         {
            y1_arm = y1_first_arm;
            y2_arm = y1_arm;
            y3_arm = y3_first_arm;
            y4_arm = y3_arm;
         }
         else if (p == 1)
         {
            y1_arm = y1_second_arm;
            y2_arm = y1_arm;
            y3_arm = y3_second_arm;
            y4_arm = y3_arm;
         }
         else if (p == 2)
         {
            y1_arm = y1_third_arm;
            y2_arm = y1_arm;
            y3_arm = y3_third_arm;
            y4_arm = y3_arm;
         }
         
         double xPoints[] = {x1_arm, x3_arm, x4_arm, x2_arm};
         double yPoints[] = {y1_arm, y3_arm, y4_arm, y2_arm};         // -values for y goes/points to the bow

         GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
         polygon.moveTo(xPoints[0], yPoints[0]);
         for (int index = 1; index < xPoints.length; index++) 
         {
            polygon.lineTo(xPoints[index], yPoints[index]);
         }
         polygon.closePath();
        
         g2d.draw(polygon);  
         
         // crane arm reinforcement part
         double x1_sub = x1_arm + (arm_slope / 2);
         double x2_sub = x2_arm - (arm_slope / 2);
         double y1_sub = (y1_arm + y3_arm) / 2;
         double y2_sub = y1_sub;    
         
         g2d.draw(new Line2D.Double(x1_sub, y1_sub, x2_sub, y2_sub));
         
      } // for (int p = 0; p < 3; p++)        
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_tall_ship(Graphics2D g2d, double wind_rose_diameter, boolean night_mode)   
   {
      //
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/
      //
      //
      //
      
            
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double xs = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double ys = 0;
      
      String category = "";
      int num_masts = 0;
      int num_sails = 3;                                       // number of lowered sails visible on the yard
      double dist_between_masts = 0;
      int mast_thickness;
      
       ///////////////////////// START TEST /////////////////////   
      //category = FULL_RIGGED;        // FULL_RIGGED, BARQUE
      //num_masts = 3;                 // 3, 4, 5
      //////////////////////// END TEST /////////////
      
      
      // determine the ship catogory and the number of masts
      //
      // e.g. ship_sub_dashboard = "full_rigged_5", "barque_3"
      //
      if (main.ship_type_dashboard.contains(FULL_RIGGED))
      {
         category = FULL_RIGGED;
      }
      if (main.ship_type_dashboard.contains(BARQUE))
      {
         category = BARQUE;
      }   
      else
      {
         category = FULL_RIGGED;
      }
      //
      try
      {   
         num_masts = Integer.parseInt(main.ship_type_dashboard.substring(main.ship_type_dashboard.length() -1));
      }
      catch (NumberFormatException | IndexOutOfBoundsException e)
      {
         num_masts = 3;
         System.out.println("--- " + e.toString());
      }
      
      
      // ship_beam is leading for scaling
      //
      double ship_beam = wind_rose_diameter / 11.0;
      
      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_beam, 0, -wind_rose_diameter / 2 - ship_beam));
      
      //////////// bow ////////
      //
      x = 0;                                                                   // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_beam * 2.2;                             // NB factor 'x.x' (e.g. 2.2) is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.4);            // NB factor 0.4 depends on factor used in bow_height_virtual

      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_beam / 2.0; 
      y1 = y + bow_height_virtual;  
      x2 = ship_beam / 2.0;
      y2 = y1;
      double ship_length = Math.abs(y1) * 2.3;                                  // so the length of the main mid section; nb factor 'x.x' (e.g. 2.3) depends on the factor used at bow_height_virtual and y
      
      ///////////// aft ship (classic spherical shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_beam;
      y3 = y1 + ship_length;
      y4 = y3;
      xs = 0;                                                                   // control point quadTo; relative to origin (centre of wind rose)
      double stern_height_virtual = ship_beam * 1.6;                            // NB factor 'x.x' is indication of the stern curve; quadTo; quadTo; vertical distance from control point to start end end point       
      ys = (wind_rose_diameter / 2.0) - (stern_height_virtual * 1.0);           // NB factor 1.0 depends on factor used in bow_height_virtual

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.quadTo(xs, ys, x3, y3);
      ship.lineTo(x1, y1);
      ship.closePath();

      // bow sprit
      g2d.setColor(color_masts_tall_ship);
      g2d.setStroke(new BasicStroke(4));
      g2d.draw(new Line2D.Double(0, 0, x, y));

      // bow sprit lines
      g2d.setStroke(new BasicStroke(1));
      g2d.draw(new Line2D.Double(x1 + ship_beam / 4, y1, x, y));
      g2d.draw(new Line2D.Double(x2 - ship_beam / 4, y2, x, y));
      
      // fill total ship
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_tall_ship);
      }
      g2d.fill(ship);
      
      // contour total ship
      g2d.setStroke(new BasicStroke(1.0f));
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_masts_tall_ship);
      }
      g2d.draw(ship);
      
      
      ///////////// masts, yards, sails//////////
      //
      mast_thickness = 4;
      dist_between_masts = ship_length / num_masts + 2;                        // NB ship_length = the length of the main mid section
      g2d.setStroke(new BasicStroke(mast_thickness));
      double y_mast = 0;                                                       // this var also necessary when drawing accomodation
      double y_mast_first = 0;
      double y_mast_second = 0;
      double y_mast_second_last = 0;
      double y_mast_last = 0;
      
      if (category.equals(FULL_RIGGED) || category.equals(BARQUE))              // NB full-rigged = 3,4,5 masts
      {
         double masts_offset_1 = dist_between_masts * 0.3;                      // relative to y1 + next masts for ship with 3 masts
         double masts_offset_2 = dist_between_masts * 0.2;                      // relative to y1 + next masts for ship with 4 or 5 masts 
         for (int i = 0; i < num_masts; i++)
         {
            // determine mast dimensions (same for all categories tall ship)
            double diameter_mast = mast_thickness * 2;
            double x_mast = x1 + (ship_beam / 2) - (diameter_mast / 2);        // x_mast: The X coordinate of the upper-left corner of the framing rectangle of this Ellipse2D.
                                                                               // y_mast: The Y coordinate of the upper-left corner of the framing rectangle of this Ellipse2D.'
            if (category.equals(BARQUE) && (i == num_masts -1))                // aftmost mast -mizzen- fore-and-aft rigged   
            {
               if (num_masts == 3)
               {
                  y_mast = y1 + masts_offset_1 + (dist_between_masts * i);
               }  
               else
               {
                  y_mast = y1 + masts_offset_2 + (dist_between_masts * i);
               }
               
               double y1_mast = y_mast + (mast_thickness * 2);
               
               double y2_mast = 0;
               if (num_masts > 3)
               {
                  y2_mast = ys;
               }
               else
               {
                  y2_mast = y3;   
               }
               
               double x1_mast = x1 + (ship_beam / 2);
               
               /////// fore-and-aft sail lowered aftmost -mizzen- mast
               if (night_mode)
               {
                  g2d.setColor(Color.LIGHT_GRAY);
               }
               else
               {
                  g2d.setColor(Color.WHITE);
               }
               double sail_width = (y2_mast - y1_mast) / num_sails;

               double x_sail = x1_mast - (mast_thickness * 0.5);            // the x coordinate of the upper-left corner of the arc to be drawn.
               double y_sail = y1_mast + (0 * sail_width);                  // the y coordinate of the upper-left corner of the arc to be drawn.
                  
               double width = mast_thickness * 1.5;                         // the width of the arc to be drawn.
               double height = sail_width;                                  // the height of the arc to be drawn.
               double startAngle = -90;                                     // the beginning angle
               double arcAngle = 180;                                       // the angular extent of the arc, relative to the start angle.
               g2d.draw(new Arc2D.Double(x_sail, y_sail, width, height, startAngle, arcAngle, Arc2D.OPEN));
               
               /////// yard aftmost mast (mizzen) barque
               double yard_length = ((x2 + (ship_beam / 2)) - (x1 - (ship_beam / 2))) * 0.75;              // so length yard mizzen = 75% of the length of the main mast yard
               g2d.setColor(color_masts_tall_ship);
               g2d.draw(new Line2D.Double(x1_mast, y1_mast, x1_mast, y1_mast + yard_length)); 
               
               /////// mast aftmost mast (mizzen) barque
               Shape shape_mast = new Ellipse2D.Double(x_mast, y_mast, diameter_mast, diameter_mast);
               g2d.fill(shape_mast);
               
            } // if (category.equals(BARQUE) && (i == num_masts -1)) 
            else // not the afmost mast -or- [the aftmost mast but not fore-and-aft rigged]
            {
               if (num_masts == 3)
               {
                  y_mast = y1 + masts_offset_1 + (dist_between_masts * i);
               }  
               else
               {
                  y_mast = y1 + masts_offset_2 + (dist_between_masts * i);
               }
               
               double x1_mast = x1 - (ship_beam / 2);
               double x2_mast = x2 + (ship_beam / 2); 

               //////// sails (lowered)
               if (night_mode)
               {
                  g2d.setColor(Color.LIGHT_GRAY);
               }
               else
               {
                  g2d.setColor(Color.WHITE);
               }
               double sail_width = (x2_mast - x1_mast) / num_sails;
               for (int k = 0; k < num_sails; k++)
               {
                  double x_sail = x1_mast + (k * sail_width);                  // the x coordinate of the upper-left corner of the arc to be drawn.
                  double y_sail = y_mast - mast_thickness;                     // the y coordinate of the upper-left corner of the arc to be drawn.
                  double width = sail_width;                                   // the width of the arc to be drawn.
                  double height = mast_thickness * 1.5;                        // the height of the arc to be drawn.
                  double startAngle = 0;                                       // the beginning angle
                  double arcAngle = 180;                                       // the angular extent of the arc, relative to the start angle.
                  g2d.draw(new Arc2D.Double(x_sail, y_sail, width, height, startAngle, arcAngle, Arc2D.OPEN));

               } // for (int k = 0; k < num_sails; k++)

               /////// yards
               g2d.setColor(color_masts_tall_ship);
               g2d.draw(new Line2D.Double(x1_mast, y_mast, x2_mast, y_mast)); 

               Shape shape_mast = new Ellipse2D.Double(x_mast, y_mast, diameter_mast, diameter_mast);
               g2d.fill(shape_mast);
            } // else (not the afmost mast -or- [the aftmost mast but not fore-and-aft rigged])
            
            
            // determine position of first accomodation (between first and second mast)
            if (i == 0)
            {
               y_mast_first = y_mast;
            }
            if (i == 1)
            {
               y_mast_second = y_mast;
            }
            
            // determine position of second accomodation (between last mast and second last mast)
            if (i == (num_masts -2))
            {
               y_mast_second_last = y_mast;
            }
            if (i == (num_masts -1))
            {
               y_mast_last = y_mast;
            }
            
         } // for (int i = 0; i < num_masts; i++)
         
         
         /////// optional first accomodation (between first and second mast)
         if (num_masts > 3)
         {   
            g2d.setColor(color_acc_passenger_ship);
            double x_acc_1 = x1 + (ship_beam / 4);
            double y_acc_1 = y_mast_first + (mast_thickness * 2);
            double width_acc_1 = ship_beam / 2;
            double height_acc_1 = y_mast_second - y_acc_1 - (dist_between_masts / 2);
            boolean raised_acc_1 = true;
            g2d.fill3DRect((int)x_acc_1, (int)y_acc_1, (int)width_acc_1, (int)height_acc_1, raised_acc_1);
         }
         
         /////// always (second) accomodation (between second last en last mast)
         g2d.setColor(color_acc_passenger_ship);
         double x_acc_2 = x1 + (ship_beam / 4);
         double y_acc_2 = y_mast_second_last + (mast_thickness * 2);
         double width_acc_2 = ship_beam / 2;
         double height_acc_2 = y_mast_last - y_acc_2 - (dist_between_masts /2);
         boolean raised_acc_2 = true;
         g2d.fill3DRect((int)x_acc_2, (int)y_acc_2, (int)width_acc_2, (int)height_acc_2, raised_acc_2);

      } // if (category.equals(FULL_RIGGED) || category.equals(BARQUE))
      
   }   
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_yacht(Graphics2D g2d, double wind_rose_diameter, boolean night_mode)   
   {
      //
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/
      //
      //
      //
   
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double xs = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double ys = 0;            
      
      // ship_beam is leading for scaling
      //
      double ship_beam = wind_rose_diameter / 9.5;
      
      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_beam, 0, -wind_rose_diameter / 2 - ship_beam));
      
      //////////// bow ////////
      //
      x = 0;                                                                   // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_beam * 3.4;                             // NB factor 'x.x' (e.g. 2.2) is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.2) + (bow_height_virtual * 0.1);            // NB factor 0.4 depends on factor used in bow_height_virtual

      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_beam / 2.0; 
      y1 = y + bow_height_virtual;  
      x2 = ship_beam / 2.0;
      y2 = y1;
      double ship_length = Math.abs(y1) * 4.4;                                  // so the length of the main mid section; nb factor 'x.x' (e.g. 2.3) depends on the factor used at bow_height_virtual and y
      
      ///////////// aft ship (slight spherical shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_beam;
      y3 = y1 + ship_length;
      y4 = y3;
      xs = 0;                                                                   // control point quadTo; relative to origin (centre of wind rose)
      double stern_height_virtual = ship_beam * 0.1;                            // NB factor 'x.x' is indication of the stern curve; quadTo; quadTo; vertical distance from control point to start end end point       
      ys = (wind_rose_diameter / 4.0) - (stern_height_virtual * 1.0);           // NB factor 1.0 depends on factor used in bow_height_virtual
      
      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.quadTo(xs, ys, x3, y3);
      ship.lineTo(x1, y1);
      ship.closePath();
      
      // fill total ship (main deck)
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_yacht);
      }
      g2d.fill(ship);
      
      // contour total ship (main deck)
      g2d.setStroke(new BasicStroke(1.0f));
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(Color.BLACK);
      }
      g2d.draw(ship);
      
      int delta = 6;                                  // horizontal distance between main deck -> first deck -> second deck -> third deck -> fourth deck
      
      ////////////// first deck ///////////////
      //
      GeneralPath deck_1 = new GeneralPath();
      deck_1.moveTo(x1 + delta, y1);
      deck_1.quadTo(x, y + (delta * 4), x2 - delta, y2);
      deck_1.lineTo(x4 - delta, y4 - delta);
      deck_1.quadTo(xs, ys - delta, x3 + delta, y3 - delta);
      deck_1.lineTo(x1 + delta, y1);
      deck_1.closePath();
      
      // contour deck_1
      g2d.setStroke(new BasicStroke(1.0f));
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(Color.BLACK);
      }
      g2d.draw(deck_1);

      ////////////// second deck ///////////////
      //
      double deck_2_width = Math.abs((x1 + (delta * 2)) - (x2 - (delta * 2)));
      int deck_2_arcWidth = 30;
      double deck_2_height = Math.abs(y1 - (y3 - delta));
      if (deck_2_width > (delta * 2))
      {
         int deck_2_arcHeight = deck_2_arcWidth;   
      
         // contour deck_2
         g2d.setStroke(new BasicStroke(1.0f));
         if (night_mode)
         {
            g2d.setColor(color_medium_gray);
         }
         else
         {
            g2d.setColor(Color.BLACK);
         }
         g2d.drawRoundRect((int)x1 + (delta * 2), (int)y1 - (delta * 2), (int)deck_2_width, (int)deck_2_height, deck_2_arcWidth, deck_2_arcHeight);
      } //if (deck_2_width > (delta * 2))
      
      ////////////// third deck ///////////////
      //
      double deck_3_width = Math.abs((x1 + (delta * 3)) - (x2 - (delta * 3)));
      if (deck_3_width > (delta * 3))
      {
         double deck_3_height = Math.abs((y1 + (delta * 5)) - (y3 - (delta * 5)));
         int deck_3_arcWidth = 40;
         int deck_3_arcHeight = deck_2_arcWidth;  
         
         // contour deck_3
         g2d.setStroke(new BasicStroke(1.0f));
         if (night_mode)
         {
            g2d.setColor(color_medium_gray);
         }
         else
         {
            g2d.setColor(Color.BLACK);
         }
         g2d.drawRoundRect((int)x1 + (delta * 3), (int)y1 + (delta * 5) - (delta * 2), (int)deck_3_width, (int)deck_3_height, deck_3_arcWidth, deck_3_arcHeight);

         double y1_pool = y1 + (delta * 5) - (delta * 2) + deck_3_height + delta;   // aft line third deck + delta
         double y2_pool = y1 - (delta * 2) + deck_2_height - delta;                 // aft line second deck - delta
         double x1_pool = x1 + (delta * 3) + delta /2 ;                             // now lines with the third deck
         double x2_pool = x1_pool + deck_3_width - delta;                           // now lines with the third deck
         double pool_width = Math.abs(x1_pool - x2_pool);
         double pool_height = Math.abs (y1_pool - y2_pool);
      
         if (night_mode)
         {
            g2d.setColor(Color.BLACK);
         }
         else
         {
            g2d.setColor(color_pool_4);
         }
         g2d.fillOval((int)x1_pool, (int)y1_pool , (int)pool_width, (int)pool_height);   // This fills a circle or an oval that fits within the rectangle specified by the X, Y, width and height arguments. 
                                                                                         // The oval is drawn inside a rectangle whose upper left hand corner is at (x_heli, y_heli), and whose width and height are as specified (d).
         // swimming pool contour
         g2d.setColor(Color.BLACK);
         g2d.drawOval((int)x1_pool, (int)y1_pool , (int)pool_width, (int)pool_height);                                                    

      } //if (deck_3_width > (delta * 3))

      ////////////// fourth deck (upper deck) ///////////////
      //
      double deck_4_width = Math.abs((x1 + (delta * 4)) - (x2 - (delta * 4)));
      if (deck_4_width > (delta * 4))
      {   
         double deck_4_height = Math.abs((y1 + (delta * 6)) - (y3 - (delta * 6)));
         int deck_4_arcWidth = 45;
         int deck_4_arcHeight = deck_2_arcWidth;   
      
         // contour deck_4
         g2d.setStroke(new BasicStroke(1.0f));
         if (night_mode)
         {
            g2d.setColor(color_medium_gray);
         }
         else
         {
            g2d.setColor(Color.BLACK);
         }
         g2d.drawRoundRect((int)x1 + (delta * 4), (int)y1 + (delta * 6) - (delta * 2), (int)deck_4_width, (int)deck_4_height, deck_4_arcWidth, deck_4_arcHeight);
      
         //////// satellite dome's (on upper deck)  ///////
         //
         double dome_diameter = ship_beam / 9;
      
         if ((dome_diameter * 4) < deck_4_width)
         {   
            // 1st row big dome's 
            double x1_dome = 0 - (dome_diameter * 1.5);                                  
            double y1_dome = 0 - dome_diameter;          
            double x2_dome = 0 + (dome_diameter * 0.5);                                  
            double y2_dome = y1_dome;
      
            Shape shape_dome_1 = new Ellipse2D.Double(x1_dome, y1_dome, dome_diameter, dome_diameter);
            Shape shape_dome_2 = new Ellipse2D.Double(x2_dome, y2_dome, dome_diameter, dome_diameter);
      
            if (night_mode)
            {
               g2d.setPaint(color_medium_gray);
            }
            else
            {
               g2d.setPaint(color_white);
            }
            g2d.fill(shape_dome_1);
            g2d.fill(shape_dome_2);
      
            // contour big dome's 1st row
            g2d.setStroke(new BasicStroke(1.0f)); 
            g2d.setPaint(color_black);
            g2d.draw(shape_dome_1);
            g2d.draw(shape_dome_2);
         
            // 2nd row with smaller dome's
            double dome_small_diameter = dome_diameter * 2 / 3;
            double x3_dome = 0 - (dome_small_diameter * 1.5);                                  
            double y3_dome = y1_dome + dome_diameter + dome_small_diameter / 2;          
            double x4_dome = 0 + (dome_small_diameter * 0.5);                                  
            double y4_dome = y3_dome;
      
            Shape shape_dome_3 = new Ellipse2D.Double(x3_dome, y3_dome, dome_small_diameter, dome_small_diameter);
            Shape shape_dome_4 = new Ellipse2D.Double(x4_dome, y4_dome, dome_small_diameter, dome_small_diameter);
      
            if (night_mode)
            {
               g2d.setPaint(color_medium_gray);
            }
            else
            {
               g2d.setPaint(color_white);
            }
            g2d.fill(shape_dome_3);
            g2d.fill(shape_dome_4);
      
            // contour smaller dome's 2nd row
            g2d.setStroke(new BasicStroke(1.0f)); 
            g2d.setPaint(color_black);
            g2d.draw(shape_dome_3);
            g2d.draw(shape_dome_4);
         } // if ((dome_diameter * 4) < deck_4_width)
      } // if (deck_4_width > (delta * 4))

      ////////////// helicopter landing zone circle (on first deck) ///////////////
      //
      double heli_circle_offset = 2;                     // do not touch the railing
      double heli_circle_thickness = 1.0f;
      if (deck_3_width > (delta * 3))
      {
          heli_circle_thickness = 3.0f;
      }
      double width_heli = (Math.abs(x1 - x2) - (2 * heli_circle_thickness) - (2 * heli_circle_offset)) / 2;
      int d = (int)width_heli;                           // d = diameter circle        
      int r = d/2;                                       // r = radius circle
      int x_heli = (int)x - r;
      int y_heli = (int)y1 - (delta * 2) - d - r;
      
      g2d.setStroke(new BasicStroke((float)heli_circle_thickness));
      
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(Color.WHITE);
      }
      g2d.drawOval(x_heli, y_heli , d, d);                // This draws a circle or an oval that fits within the rectangle specified by the X, Y, width and height arguments. 
                                                          // The oval is drawn inside a rectangle whose upper left hand corner is at (x_heli, y_heli), and whose width and height are as specified (d).
      
      // 'H'-letter in the circle (perpendicular on fore-aft line)
      //
      //      (x_heli, y_heli)
      //      *------------------------------
      //      |                             |
      //      |    h1        h5        h2   |
      //      |     ------------------      |
      //      |              |              |
      //      |              * (hc_x, hc_y) |
      //      |              |              |
      //      |     ------------------      |
      //      |    h3        h6        h4   |
      //      |                             |
      //      -------------------------------
      //
      //
      int hc_x = x_heli + r;                             // NB 'hc' = helideck centre
      int hc_y = y_heli + r;                             // NB 'hc' = helideck centre
      
      // NB l = half length of the 'H' vertical side: h1-h5 / h5-h2 / h3-h6 / h6-h4
      // NB w = half width horizontal part of the 'H': h5-h6
      int l = r * 2 / 4;
      int w = l * 2 / 3;
      int h1_x = hc_x - l;
      int h1_y = hc_y - w;
      int h2_x = hc_x + l;
      int h2_y = h1_y;  
      int h3_x = h1_x;
      int h3_y = hc_y + w;  
      int h4_x = h2_x;
      int h4_y = h3_y;  
      int h5_x = (h1_x + h2_x) / 2;
      int h5_y = h1_y;
      int h6_x = h5_x;
      int h6_y = h3_y;
      
      g2d.drawLine(h1_x, h1_y, h2_x, h2_y);
      g2d.drawLine(h5_x, h5_y, h6_x, h6_y);
      g2d.drawLine(h3_x, h3_y, h4_x, h4_y);
      
      // reset stroke thickness
      g2d.setStroke(new BasicStroke(1.0f));
      
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_general_cargo_ship_II(Graphics2D g2d, double wind_rose_diameter, boolean night_mode, Color deck_color) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/
      //
      
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;
      
      
      // deck color (might be set by pop-up submenu)
      //
      if (deck_color != null)
      {
         color_deck_general_cargo_ship_II = deck_color;                         // NB if var deck_color not known -> default will be used 
      }

      // ship_breadth (=ship width) is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      //////////// bow ////////
      //
      x = 0;                                                                // control point quadTo; relative to origin (centre of wind rose)
      //y = -(wind_rose_diameter / 2.0);                                      // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 2;                         // NB factor 2.0 is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.1);         // NB factor 0.1 depends on factor used in bow_height_virtual
      
      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; //x;
      y1 = y + bow_height_virtual;
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length_main = Math.abs(y1) * 2.3;                       // so the length of the main mid section; NB factor 2.3 depends on the factor used at bow_height_virtual and y

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4 - (ship_breadth / 7);
      x6 = x3 + (ship_breadth / 7);
      y3 = y1 + ship_length_main;
      y4 = y3;
      y5 = y3 + (ship_breadth * 0.5);
      y6 = y5;

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      g2d.setStroke(new BasicStroke(1.0f));
      //g2d.setPaint(color_black);
      g2d.setPaint(Color.RED);
      g2d.draw(ship);
      
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_general_cargo_ship_II);
      }
      g2d.fill(ship);

      
      /////////////// ship bridge /////////
      //
      double bridge_height = 7;                     
      double acc_height = (y6 - y3) / 2;                        
      double bridge_indention = ship_breadth / 5;
      double dist_origin_bridge = y3 - bridge_height;
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);

      
      ////////////// hatches ////////////
      //
      double hatch_breath = ship_breadth * 0.8;
      double hatch_length = hatch_breath * 0.9;
      double hatch_intermediate = 2;   // distances between 2 hatches
      double y_hatch = y1;
      double x_hatch = -hatch_breath / 2;

      for (int i = 0; i < 50; i++) // max 50 hatches
      {
         if (y_hatch + (hatch_length + hatch_intermediate / 2) < dist_origin_bridge)    
         {
            if (night_mode)
            {
               g2d.setPaint(color_medium_gray);
            }
            else
            {
               //if ((color_deck_general_cargo_ship_II == DASHBOARD_view_APR_radar.DECK_COLOR_GREEN) || (color_deck_general_cargo_ship_II == DASHBOARD_view_APR_radar.DECK_COLOR_LIGHT_GREEN))
               //if ((color_deck_general_cargo_ship_II.getRGB() == DASHBOARD_view_APR_radar.DECK_COLOR_GREEN.getRGB()) || (color_deck_general_cargo_ship_II.getRGB() == DASHBOARD_view_APR_radar.DECK_COLOR_LIGHT_GREEN.getRGB()))
               if ((color_deck_general_cargo_ship_II.getRGB() == DECK_COLOR_GREEN.getRGB()) || (color_deck_general_cargo_ship_II.getRGB() == DECK_COLOR_LIGHT_GREEN.getRGB()))   
               {
                  g2d.setPaint(color_hatches_general_cargo_ship_II);                                 // greenish color
               }
               else
               {
                  g2d.setPaint(Color.LIGHT_GRAY); 
               }
            }
            
            g2d.fill(new Rectangle2D.Double(x_hatch, y_hatch, hatch_breath, hatch_length));
            
            // hatch sections
            g2d.setColor(color_medium_gray);
            double y_sub_hatch = y_hatch;
            for (int k = 0; k < 100; k++)
            {
               if (y_sub_hatch < (y_hatch + hatch_length))
               {
                  g2d.draw(new Line2D.Double(x_hatch, y_sub_hatch, (x_hatch + hatch_breath), y_sub_hatch));
                  
                  y_sub_hatch = y_sub_hatch + 10;
               }
               else
               {
                  break;
               }
            } //  for (int k = 0; k < 100; k++)  
            
            y_hatch += hatch_length + hatch_intermediate;
         } 
         else 
         {
            break;
         }

      } // for (int i = 0; i < 50; i++)      
      
      
      ////////////// crane pillars ////////////
      //
      //                          ___
      //                         /   \
      //       (x1_arm,y1_arm)  |     | (x2_arm,y2_arm)     <-- pillar crane
      //                        |\___/|
      //                        |     |
      //                        |     |
      //                        |     |
      //                        |     |                     <-- arm crane
      //                        |     |
      //                        |     |
      //                        |     |
      //                         -----
      //        (x3_arm,y3_arm)          (x4_arm,y4_arm)
      //
      //
      
      double dist_between_cranes = ship_length_main / 4;
      double width_crane_pillar = ship_breadth * 0.15; //0.2;
      double height_crane_pillar = width_crane_pillar;
      // NB double x_pillar_crane = x2 - width_crane_pillar;    // starboard side
      double x_pillar_crane = x1;                               // port side
      double y_pillar_crane = 0;
      
      double x1_arm = x_pillar_crane;
      double x2_arm = x_pillar_crane + width_crane_pillar;
      double arm_slope = 4;
      double x3_arm = x1_arm + arm_slope;
      double x4_arm = x2_arm - arm_slope;
      
      // if the (ship) figure is very small
      if (x4_arm < x3_arm)
      {
         x3_arm = x1_arm;
         x4_arm = x2_arm;
      }
      
      double y1_first_arm = 0;                 // counting from the bow
      double y1_second_arm = 0;                // counting from the bow
      double y1_third_arm = 0;                 // counting from the bow
      double y1_fourth_arm = 0;                // counting from the bow
      
      for (int p = 0; p < 4; p++)              // 4 cranes
      {
         y_pillar_crane = y2 + (p * dist_between_cranes);
         
         Shape shape_crane_pillar = new Ellipse2D.Double(x_pillar_crane, y_pillar_crane, width_crane_pillar, height_crane_pillar);

         if (night_mode)
         {
            g2d.setPaint(Color.LIGHT_GRAY);
         }
         else
         {
            g2d.setPaint(color_white);
         }
         g2d.fill(shape_crane_pillar);
      
         g2d.setColor(Color.LIGHT_GRAY);
         g2d.setStroke(new BasicStroke(1.0f));
         g2d.draw(shape_crane_pillar);
         
         // collect data for the arms of the cranes
         if (p == 0)
         {
            y1_first_arm = y_pillar_crane + (height_crane_pillar / 2);
         }
         else if (p == 1)
         {
            y1_second_arm = y_pillar_crane + (height_crane_pillar / 2);
         }
         else if (p == 2)
         {
            y1_third_arm = y_pillar_crane + (height_crane_pillar / 2);
         }
         else if (p == 3)
         {
            y1_fourth_arm = y_pillar_crane + (height_crane_pillar / 2);
         }
      } // for (int p = 0; p < 4; p++)
      
      
      ///////////////// crane arms /////////////
      //
      // if the (ship) figure is very small (thickness crane arms)
      if (x4_arm < x3_arm)
      {
         g2d.setStroke(new BasicStroke(2.0f)); 
      }
      else // 'normal' figure
      {
         g2d.setStroke(new BasicStroke(3.0f));  
      }
      
      if (night_mode)
      {
         g2d.setColor(Color.LIGHT_GRAY);
      }
      else
      {
         g2d.setColor(color_white);
      }
      
      double y3_first_arm = y1_second_arm - width_crane_pillar;   // NB y1_first_arm to y3_first_arm - 4 = arm length
      double y3_second_arm = y1_third_arm - width_crane_pillar;   // NB y1_second_arm to y3_second_arm - 4 = arm length
      double y3_third_arm = y1_fourth_arm - width_crane_pillar;
      double y3_fourth_arm = y4 - width_crane_pillar;              // NB y1_third_arm to y4 (start bridge) - 4 = arm length 
      
      for (int p = 0; p < 4; p++)
      {   
         double y1_arm = 0;
         double y2_arm = 0;
         double y3_arm = 0;
         double y4_arm = 0;
         
         if (p == 0)
         {
            y1_arm = y1_first_arm;
            y2_arm = y1_arm;
            y3_arm = y3_first_arm;
            y4_arm = y3_arm;
         }
         else if (p == 1)
         {
            y1_arm = y1_second_arm;
            y2_arm = y1_arm;
            y3_arm = y3_second_arm;
            y4_arm = y3_arm;
         }
         else if (p == 2)
         {
            y1_arm = y1_third_arm;
            y2_arm = y1_arm;
            y3_arm = y3_third_arm;
            y4_arm = y3_arm;
         }
         else if (p == 3)
         {
            y1_arm = y1_fourth_arm;
            y2_arm = y1_arm;
            y3_arm = y3_fourth_arm;
            y4_arm = y3_arm;
         }
         
         double xPoints[] = {x1_arm, x3_arm, x4_arm, x2_arm};
         double yPoints[] = {y1_arm, y3_arm, y4_arm, y2_arm};         // -values for y goes/points to the bow

         GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
         polygon.moveTo(xPoints[0], yPoints[0]);
         for (int index = 1; index < xPoints.length; index++) 
         {
            polygon.lineTo(xPoints[index], yPoints[index]);
         }
         polygon.closePath();
         g2d.draw(polygon);  
         
         // crane arm reinforcement part
         double x1_sub = x1_arm + (arm_slope / 2);
         double x2_sub = x2_arm - (arm_slope / 2);
         double y1_sub = (y1_arm + y3_arm) / 2;
         double y2_sub = y1_sub;    
         
         g2d.draw(new Line2D.Double(x1_sub, y1_sub, x2_sub, y2_sub));
         
      } // for (int p = 0; p < 4; p++)        
   }
 
   

   public ship(Color color_white, Color color_black, Color color_deck_passenger_ship, Color color_pool_1, Color color_pool_2, Color color_pool_3, Color color_pool_4, Color color_acc_passenger_ship, Color color_life_boat, Color color_funnel_passenger_ship, Color color_funnel_ferry, Color color_bridge_oil_tanker, Color color_bridge_container_ship, Color color_bridge_ro_ro_ship, Color color_bulk_carrier, Color color_lng_tanks, Color color_deck_lng_tanker, Color color_deck_oil_tanker, Color color_pipes_oil_tanker, Color color_deck_steel_blue, Color color_deck_reefer_ship, Color color_deck_general_cargo_classic, Color color_deck_research_vessel, Color color_crane_research_vessel, Color color_hoist_research_vessel, Color color_cradle_research_vessel, Color color_bridge_research_vessel, 
               Color color_night_crane_research_vessel, Color color_night_hoist_research_vessel, Color color_night_cradle_research_vessel, Color color_deck_general_cargo_ship, Color color_deck_container_ship_II, Color color_deck1_ferry, Color color_deck2_ferry, Color color_deck3_ferry, Color color_deck_ro_ro, Color color_lanes_ro_ro, Color color_hatches_general_cargo_ship, Color color_medium_gray, Color color_container_1, Color color_container_2, Color color_container_3, Color color_container_4, Color color_container_5, Color color_container_6, Color color_container_7, Color color_container_8, Color color_container_9, Color color_container_10, Color color_derricks, Color color_deck_general_cargo_ship_II, Color color_hatches_general_cargo_ship_II, Color color_deck_lng_tanker_II, 
               Color color_bridge_lng_tanker_II, Color color_tank_deck_fruit_juice_tanker, Color color_bridge_fruit_juice, Color color_tanks_fruit_juice, Color color_aft_deck_fruit_juice_tanker, java.awt.Color color_deck_tall_ship, java.awt.Color color_masts_tall_ship, java.awt.Color color_deck_yacht)
   {
      this.color_white = color_white;
      this.color_black = color_black;
      this.color_deck_passenger_ship = color_deck_passenger_ship;
      this.color_pool_1 = color_pool_1;
      this.color_pool_2 = color_pool_2;
      this.color_pool_3 = color_pool_3;
      this.color_pool_4 = color_pool_4;
      this.color_acc_passenger_ship = color_acc_passenger_ship;
      this.color_life_boat = color_life_boat;
      this.color_funnel_passenger_ship = color_funnel_passenger_ship;
      this.color_funnel_ferry = color_funnel_ferry;
      this.color_bridge_lng_tanker_II = color_bridge_lng_tanker_II;
      this.color_bridge_oil_tanker = color_bridge_oil_tanker;
      this.color_bridge_container_ship = color_bridge_container_ship;
      this.color_bridge_ro_ro_ship = color_bridge_ro_ro_ship;
      this.color_bulk_carrier = color_bulk_carrier;
      this.color_lng_tanks = color_lng_tanks;
      this.color_deck_lng_tanker = color_deck_lng_tanker;
      this.color_deck_lng_tanker_II = color_deck_lng_tanker_II;
      this.color_deck_oil_tanker = color_deck_oil_tanker;
      this.color_pipes_oil_tanker = color_pipes_oil_tanker;
      this.color_deck_steel_blue = color_deck_steel_blue;
      this.color_deck_reefer_ship = color_deck_reefer_ship;
      this.color_deck_general_cargo_ship_II = color_deck_general_cargo_ship_II;
      this.color_deck_general_cargo_classic = color_deck_general_cargo_classic;
      this.color_deck_research_vessel = color_deck_research_vessel;
      this.color_crane_research_vessel = color_crane_research_vessel;
      this.color_hoist_research_vessel = color_hoist_research_vessel;
      this.color_cradle_research_vessel = color_cradle_research_vessel;
      this.color_bridge_research_vessel = color_bridge_research_vessel;
      this.color_night_crane_research_vessel = color_night_crane_research_vessel;
      this.color_night_hoist_research_vessel = color_night_hoist_research_vessel;
      this.color_night_cradle_research_vessel = color_night_cradle_research_vessel;
      this.color_deck_general_cargo_ship = color_deck_general_cargo_ship;
      this.color_deck_container_ship_II = color_deck_container_ship_II;
      this.color_deck1_ferry = color_deck1_ferry;
      this.color_deck2_ferry = color_deck2_ferry;
      this.color_deck3_ferry = color_deck3_ferry;
      this.color_deck_ro_ro = color_deck_ro_ro;
      this.color_lanes_ro_ro = color_lanes_ro_ro;
      this.color_hatches_general_cargo_ship = color_hatches_general_cargo_ship;
      this.color_hatches_general_cargo_ship_II = color_hatches_general_cargo_ship_II;
      this.color_medium_gray = color_medium_gray;
      this.color_container_1 = color_container_1;
      this.color_container_2 = color_container_2;
      this.color_container_3 = color_container_3;
      this.color_container_4 = color_container_4;
      this.color_container_5 = color_container_5;
      this.color_container_6 = color_container_6;
      this.color_container_7 = color_container_7;
      this.color_container_8 = color_container_8;
      this.color_container_9 = color_container_9;
      this.color_container_10 = color_container_10;
      this.color_derricks = color_derricks;
      this.color_tank_deck_fruit_juice_tanker = color_tank_deck_fruit_juice_tanker;
      this.color_bridge_fruit_juice = color_bridge_fruit_juice;
      this.color_tanks_fruit_juice = color_tanks_fruit_juice;
      this.color_aft_deck_fruit_juice_tanker = color_aft_deck_fruit_juice_tanker;
      this.color_deck_tall_ship = color_deck_tall_ship;
      this.color_masts_tall_ship = color_masts_tall_ship;
      this.color_deck_yacht = color_deck_yacht;
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_neutral_ship(Graphics2D g2d, double wind_rose_diameter, boolean night_mode) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/

      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;

      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      //////////// bow ////////
      //
      x = 0;                                                             // control point quadTo; relative to origin (centre of wind rose)
      y = -(wind_rose_diameter / 2.0);                                   // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 2.5;                    // quadTo; vertical distance from control point to start end end point       

      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; //x;
      y1 = y + bow_height_virtual;
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length = Math.abs(y1) * 2;                              // so the length of the main mid section

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4 - (ship_breadth / 7);
      x6 = x3 + (ship_breadth / 7);
      y3 = y1 + ship_length;
      y4 = y3;
      y5 = y3 + (ship_breadth * 1.2);
      y6 = y5;

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_black);
      g2d.draw(ship);
      
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_steel_blue);
      }
      g2d.fill(ship);

      //System.out.println("x = "  + x);
      //System.out.println("x1 = "  + x1);
      //System.out.println("x2 = "  + x2);
      //System.out.println("y = "  + y);
      //System.out.println("y1 = "  + y1);
      //System.out.println("y2 = "  + y2);
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_research_vessel(Graphics2D g2d, double wind_rose_diameter, boolean night_mode) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/

      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;

      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      //////////// bow ////////
      //
      x = 0;                                                             // control point quadTo; relative to origin (centre of wind rose)
      y = -(wind_rose_diameter / 2.0);                                   // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 2.5;                    // quadTo; vertical distance from control point to start end end point       

      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; //x;
      y1 = y + bow_height_virtual;
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length = Math.abs(y1) * 2;                              // so the length of the main mid section

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4 - (ship_breadth / 7);
      x6 = x3 + (ship_breadth / 7);
      y3 = y1 + ship_length;
      y4 = y3;
      y5 = y3 + (ship_breadth * 1.2);
      y6 = y5;

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      // ship contour
      g2d.setStroke(new BasicStroke(2.0f));
      if (night_mode)
      {
         g2d.setColor(color_black);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(ship);
      
      // ship fill
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_research_vessel);
      }
      g2d.fill(ship);

      
      ///////// deck curve on fore ship /////////
      //
      double y_control = -(wind_rose_diameter / 2.0);                            // control y point quadTo; relative to origin (centre of wind rose)
      bow_height_virtual = ship_breadth * 2.5;                                   // for quadTo; vertical distance from control point to start end end point       
      
      double x1_curve = x1;
      double y1_curve = y1;
      double ctrx_curve = 0;
      double ctry_curve = y_control + bow_height_virtual / 1.1;
      double x2_curve = x1_curve + ship_breadth;
      double y2_curve = y1_curve;
 
      QuadCurve2D quad = new QuadCurve2D.Double(x1_curve, y1_curve, ctrx_curve, ctry_curve, x2_curve, y2_curve);
      g2d.setStroke(new BasicStroke(2.0f));
      
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(quad);

      
      ///////////////////// ship bridge ///////////////////
      //
      //
      //
      //                   ------------
      //                  /            \
      //                 /              \
      //                /(x5,y5)         \(x6,y6)
      //  (x1,y1)---------------------------------(x2,y2)
      //         |                               |
      //         |                               |
      //  (x4,y4)---------------------------------(x3,y3)
      //                
      //
      //
      // NB
      //    bridge_height = distance y2 -> y3
      //    bridge_indention = x1 -> x5
      //
      // NB
      //    all relative to origin (0, 0)
      //
      // NB read in this bridge section "x1_bridge" for "x1" etc
      //
   
      
      double bridge_height = ship_breadth / 5;                    
      double bridge_indention = ship_breadth / 5;
      double dist_origin_bridge = 0 - bridge_height;  
      
      double bridge_overhang = 5;
      double x1_bridge = -ship_breadth / 2 - bridge_overhang;
      double x2_bridge = ship_breadth / 2 + bridge_overhang;
      double x3_bridge = x2_bridge;
      double x4_bridge = x1_bridge;
      double x5_bridge = x1_bridge + bridge_indention;
      double x6_bridge = x2_bridge - bridge_indention;
      double y1_bridge = dist_origin_bridge; //0;
      double y2_bridge = y1_bridge;
      double y3_bridge = y1_bridge + bridge_height;
      double y4_bridge = y3_bridge;
      double y5_bridge = y1_bridge;
      double y6_bridge = y1_bridge;
       

      GeneralPath bridge = new GeneralPath();
      bridge.moveTo(x1_bridge, y1_bridge);
      bridge.lineTo(x5_bridge, y5_bridge);
      bridge.quadTo(x, y1_bridge - (ship_breadth / 4), x6_bridge, y6_bridge);
      bridge.lineTo(x2_bridge, y2_bridge);
      bridge.lineTo(x3_bridge, y3_bridge);
      bridge.lineTo(x4_bridge, y4_bridge);
      bridge.closePath();

      // bridge contour
      g2d.setStroke(new BasicStroke(5.0f));
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(bridge);
      
      // bridge fill
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_bridge_research_vessel);
      }
      g2d.fill(bridge);
      
      
      //////// satellite dome (middle bridge) ///////
      //
      double dome_diameter = ship_breadth / 4;
      double x1_dome = 0 - dome_diameter / 2;                           // middle of the bridge
      double y1_dome = y4_bridge - dome_diameter;                 // middle of the bridge
      Shape shape_dome = new Ellipse2D.Double(x1_dome, y1_dome, dome_diameter, dome_diameter);
      
      // fill dome
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {
         g2d.setPaint(color_white);
      }
      g2d.fill(shape_dome);
      
      // contour dome
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setColor(color_black);
      g2d.draw(shape_dome);

      
     
      /////////////////// crane pillar /////////////////
      //
      //                          ___
      //                         /   \
      //       (x1_arm,y1_arm)  |     | (x2_arm,y2_arm)     <-- pillar crane
      //                        |\___/|
      //                        |     |
      //                        |     |
      //                        |     |
      //                        |     |                     <-- arm crane
      //                        |     |
      //                        |     |
      //                        |     |
      //                         -----
      //        (x3_arm,y3_arm)          (x4_arm,y4_arm)
      //
      //
      
      double width_crane_pillar = ship_breadth * 0.15;
      double height_crane_pillar = width_crane_pillar;
      double x_pillar_crane = x2 - width_crane_pillar;//0;
      double y_pillar_crane = (y4 - y3_bridge) / 2;                // crane position in the middle of bridge and where the indention/curve of the aft ship starts
      
      double x1_arm = x_pillar_crane;
      double x2_arm = x_pillar_crane + width_crane_pillar;
      double arm_slope = 4;
      double x3_arm = x1_arm + arm_slope;
      double x4_arm = x2_arm - arm_slope;
      
      // if the (ship) figure is very small
      if (x4_arm < x3_arm)
      {
         x3_arm = x1_arm;
         x4_arm = x2_arm;
      }
      
      Shape shape_crane_pillar = new Ellipse2D.Double(x_pillar_crane, y_pillar_crane, width_crane_pillar, height_crane_pillar);
      if (night_mode)
      {
         g2d.setPaint(color_night_crane_research_vessel);
      }
      else
      {
         g2d.setPaint(color_crane_research_vessel);
      }
      g2d.fill(shape_crane_pillar);
      
      if (night_mode)
      {
         g2d.setColor(color_night_crane_research_vessel); 
      }
      else
      {
         g2d.setColor(color_crane_research_vessel);
      }
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_crane_pillar);
      
      
      ///////////////// crane arm /////////////
      //
      g2d.setStroke(new BasicStroke(3.0f)); 
      if (night_mode)
      {
         g2d.setColor(color_night_crane_research_vessel);
      }
      else
      {
         g2d.setColor(color_crane_research_vessel);
      }
      
      double y1_arm = y_pillar_crane + (height_crane_pillar / 2);
      double y2_arm = y1_arm;  
      double y3_arm = y1_arm - (y4 - y3_bridge) / 2;    // arm length = y4 - y3_bridge) / 2
      double y4_arm = y3_arm;
         
      double xPoints[] = {x1_arm, x3_arm, x4_arm, x2_arm};
      double yPoints[] = {y1_arm, y3_arm, y4_arm, y2_arm};         // -values for y goes/points to the bow

      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++) 
      {
         polygon.lineTo(xPoints[index], yPoints[index]);
      }
      polygon.closePath();
      g2d.draw(polygon);  
         
      // crane arm reinforcement part
      double x1_sub = x1_arm + (arm_slope / 2);
      double x2_sub = x2_arm - (arm_slope / 2);
      double y1_sub = (y1_arm + y3_arm) / 2;
      double y2_sub = y1_sub;    
      g2d.draw(new Line2D.Double(x1_sub, y1_sub, x2_sub, y2_sub));
      
      
      ////////////////////// equipment containers //////////////////
      //
      //               x1                           x2
      //                |                           |
      //                |                           |
      //  (x4_bridge,y4_bridge)                    (x3_bridge,y3_bridge)
      //               -------------------------------                          rear side bridge   
      //                |                           |
      //   (x1_c,y1_c)  |---| (x2_c,y2_c)           |
      //                |   |                       |
      //                |   |                       |
      //                |   |                       |                                
      //    (x5_c,y5_c) |-- | (x6_c,y6_c)           |
      //                |   |                       |                                
      //                |   |                       |
      //                |   |                       |
      //   (x4_c,y4_c)  |---| (x3_c,y3_c)           |
      //                |                           |
      //                |                           |
      //        (x3,y3) |                           | (x4,y4)
      //                 \                         /
      //                  \                       /
      //                port                  starboard
      //
      //
      //
      double x1_container = x1;
      double x2_container = x1_container + (ship_breadth * 0.2); 
      double x3_container = x2_container;
      double x4_container = x1_container;       
      double y1_container = y4_bridge + (ship_breadth * 0.3);                             
      double y2_container = y1_container;
      double y3_container = y3 - (ship_breadth * 0.3);
      double y4_container = y3_container;
              
      double xPoints_container[] = {x1_container, x2_container, x3_container, x4_container};
      double yPoints_container[] = {y1_container, y2_container, y3_container, y4_container};                   // -values for y goes/points to the bow

      GeneralPath polygon_container = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints_container.length);
      polygon_container.moveTo(xPoints_container[0], yPoints_container[0]);
      for (int index = 1; index < xPoints_container.length; index++)            
      {
         polygon_container.lineTo(xPoints_container[index], yPoints_container[index]);
      }
      polygon_container.closePath();   
      
      // container fill
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {
         // NB these (reearch/equipment containers are always white!
         g2d.setPaint(color_white);
      }
      g2d.fill(polygon_container);  
      
      // container contour
      g2d.setStroke(new BasicStroke(1.0f)); 
      g2d.setColor(color_black);
      g2d.draw(polygon_container);
      
      // makes 2 containers
      double x5_container = x1_container;
      double x6_container = x2_container;     
      double y5_container = (y1_container + y4_container) / 2;
      double y6_container = y5_container;
      
      GeneralPath container_mid = new GeneralPath();
      container_mid.moveTo(x5_container, y5_container);
      container_mid.lineTo(x6_container, y6_container);
      container_mid.closePath();
      g2d.draw(container_mid);
      
      //////////////// container cradle /////////////////
      //
      //
      //                  (x7_c,y7_c)    
      //    (x5_c,y5_c) |-------| (x6_c,y6_c)           
      //                |   |   |                                                    
      //                |   |   |    
      //                |   |   |   
      //                |   |   |   
      //                |   |   |   
      //                |   |   |
      //                |   |   |                       
      //   (x4_c,y4_c)  |-------|  (x3_c,y3_c)          
      //                  (x8_c,y8_c)
      //
      //
      
      double x7_container = (x5_container + x6_container) / 2;
      double x8_container = x7_container;     
      double y7_container = y5_container;
      double y8_container = y4_container;

      
      // NB drawing the cradle lines via GeneralPath is also ok
      // 
      // GeneralPath container_cradle = new GeneralPath();
      // container_cradle.moveTo(x5_container, y5_container);
      // container_cradle.lineTo(x6_container, y6_container);
      // 
      // container_cradle.moveTo(x7_container, y7_container);
      // container_cradle.lineTo(x8_container, y8_container);
      //
      // container_cradle.moveTo(x4_container, y4_container);
      // container_cradle.lineTo(x3_container, y3_container);
      //
      // container_cradle.closePath();
      //
      // g2d.setStroke(new BasicStroke(2.0f)); 
      // g2d.setColor(Color.ORANGE);
      // g2d.draw(container_cradle);
      
      g2d.setStroke(new BasicStroke(2.0f)); 
      if (night_mode)
      {
         g2d.setColor(color_night_cradle_research_vessel);
      }
      else
      {
         g2d.setColor(color_cradle_research_vessel);
      }
      g2d.draw(new Line2D.Double(x5_container, y5_container, x6_container, y6_container));
      g2d.draw(new Line2D.Double(x7_container, y7_container, x8_container, y8_container));
      g2d.draw(new Line2D.Double(x4_container, y4_container, x3_container, y3_container));
     
      
      ///////////////////// aft hoist constuction /////////////////
      //
      //
      //             port                        starboard
      //               |                             |
      //               |                             |
      //               |                             |
      //               |                             |
      //  (x1_hoist,y1_hoist)                    (x2_hoist,y2_hoist)  
      //               |    ---------------------    |                        
      //               |   /                      \  |
      //               | /                          \|                            
      //               -------------------------------                        <--- stern
      //        (x6,y6)                                (x5,y5)  
      //
      //
      
      double x1_hoist = x6 + (ship_breadth / 10);
      double x2_hoist = x5 - (ship_breadth / 10);  
      double y1_hoist = y6 - (ship_length / 17);                               // NB ship_length = the length of the main mid section
      double y2_hoist = y1_hoist;  
      
      double xPoints_hoist[] = {x6, x1_hoist, x2_hoist, x5};
      double yPoints_hoist[] = {y6, y1_hoist, y2_hoist, y5};                   // -values for y goes/points to the bow
   
      g2d.setStroke(new BasicStroke(3.0f)); 
      if (night_mode)
      {
         g2d.setColor(color_night_hoist_research_vessel);
      }
      else
      {
         g2d.setColor(color_hoist_research_vessel);
      }
      
      GeneralPath polygon_hoist = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints_hoist.length);
      polygon_hoist.moveTo(xPoints_hoist[0], yPoints_hoist[0]);
      for (int index = 1; index < xPoints_hoist.length; index++)              // NB do not close the polygon!
      {
         polygon_hoist.lineTo(xPoints_hoist[index], yPoints_hoist[index]);
      }
      //polygon_hoist.closePath();                                            // NB do not close the polygon!
      g2d.draw(polygon_hoist);  
      
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_container_ship_II(Graphics2D g2d, double wind_rose_diameter, boolean night_mode, Color deck_color) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y-  ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/

      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;
   
      
      // ship_breadth (= ship width) is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;
      
      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));
      
      //////////// bow ////////
      //
      x = 0;                                                             // control point quadTo; relative to origin (centre of wind rose)
      //double bow_height_virtual = ship_breadth * 1.1;                    // NB factor 1.1 is indication of the bow curve; quadTo; vertical distance from control point to start end end point       
      //y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.6);      // NB fator 0.6 depends on factor used in bow_height_virtual
      double bow_height_virtual = ship_breadth * 2.0;                    // NB factor 1.1 is indication of the bow curve; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.1);      // NB fator 0.6 depends on factor used in bow_height_virtual
      
      
      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; 
      y1 = y + bow_height_virtual;  
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length = Math.abs(y1) * 1.9;                           // so the length of the main mid section; nb factor 1.65 depends on the factor used at bow_height_virtual and y
      
      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4 - (ship_breadth / 10);  // very slight trapezium
      x6 = x3 + (ship_breadth / 10);  // very slight trapezium
      y3 = y1 + ship_length;
      y4 = y3;
      y5 = y3 + (ship_breadth * 1.2);
      y6 = y5;
      
      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();
      
      // fill total ship
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_container_ship_II);
      }
      g2d.fill(ship);
      
      // contour total ship
      g2d.setStroke(new BasicStroke(1.0f));
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_black);
      }
      g2d.draw(ship);
      
      /////////////// ship bridge /////////
      //
      double bridge_height = 7;                     
      if (wind_rose_diameter > BRIDGE_DRAW_LIMIT)
      {
         bridge_height = 10;
      }
      
      double acc_height = bridge_height * 1.5;
      double bridge_indention = ship_breadth / 5;
      double dist_origin_bridge = y6 - (bridge_height + acc_height + 5);
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);
      
            
      ////////////// containers ////////////
      //
      int max_number_containers_rows = 10;
      int number_containers_in_row = 8;
      double container_breadth = ship_breadth / number_containers_in_row;
      double container_length = 0;
      double container_length_40 = container_breadth * 5;                        // fixed relation (1:5) for 40ft container
      double container_length_20 = container_length_40 / 2;
      double x_container;
      double y_container;
      boolean doorgaan = true;
      double y_container_offset;                                                 // painting on aft ship start with x,y as top left and not bottom left
 
      y_container = y1;                                                          // now y_container is negative value
      
    
      //System.out.println("+++ m = " + m + " y_container= " + y_container); 
      for (int i = 0; i < max_number_containers_rows; i++) 
      {
         if (Math.abs(y_container - dist_origin_bridge) > container_length_40)   
         {
            container_length = container_length_40;
            doorgaan = true;
         } 
         else if (Math.abs(y_container - dist_origin_bridge) > container_length_20)   
         {
            container_length = container_length_20;                             // 20 ft container
            doorgaan = true;
         } 
         else 
         {
            doorgaan = false;
         }

         if (doorgaan) 
         {
            y_container_offset = 0;                                              // because of the starting point (x,y) of the drawing of a rectangle

            int start_in_row = 0;
            int end_in_row = 0;
            if (i == 0) // first row fore ship (20ft row on the bow)
            {
               container_length = container_length_20;
               x_container = x1 + container_breadth;
               y_container = y1 - container_length;
               start_in_row = 1;                                                  // because less container space available on the bow
               end_in_row = number_containers_in_row - 1;                         // because less container space available on the bow
            } 
            else 
            {
               start_in_row = 0;                                                  // full space for containers available 
               end_in_row = number_containers_in_row;                             // full space for containers available
               x_container = x1;
            }

            for (int k = start_in_row; k < end_in_row; k++) 
            {
               Shape shape_container = new Rectangle2D.Double(x_container, y_container - y_container_offset, container_breadth, container_length);
/*
               if (night_mode)
               {
                  g2d.setPaint(CONTAINER_COLOR_NIGHT);
               }               
               else // day mode
               {
                  int random = (int)(Math.random() * 10 + 1);    // 1 - 10 return value

                  switch (random) 
                  {
                     case 1:  g2d.setPaint(color_container_1); break;
                     case 2:  g2d.setPaint(color_container_2); break;
                     case 3:  g2d.setPaint(color_container_3); break;
                     case 4:  g2d.setPaint(color_container_4); break;
                     case 5:  g2d.setPaint(color_container_5); break;
                     case 6:  g2d.setPaint(color_container_6); break;
                     case 7:  g2d.setPaint(color_container_7); break;
                     case 8:  g2d.setPaint(color_container_8); break;
                     case 9:  g2d.setPaint(color_container_9); break;
                     case 10: g2d.setPaint(color_container_10); break;
                     default: g2d.setPaint(color_container_1); break;
                  }
               } // else (day mode)
*/
               if (night_mode)
               {
                  g2d.setPaint(CONTAINER_COLOR_NIGHT);
               }
               else // day mode 
               {   
                  if (deck_color != null)
                  {   
                     if (deck_color.equals(CONTAINER_COLOR_WHITE))
                     {
                        g2d.setPaint(CONTAINER_COLOR_WHITE);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_LIGHT_BLUE))
                     {
                        g2d.setPaint(CONTAINER_COLOR_LIGHT_BLUE);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_BLUE))
                     {
                        g2d.setPaint(CONTAINER_COLOR_BLUE);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_DARK_BLUE))
                     {
                        g2d.setPaint(CONTAINER_COLOR_DARK_BLUE);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_GREEN))
                     {
                        g2d.setPaint(CONTAINER_COLOR_GREEN);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_MAGENTA))
                     {
                        g2d.setPaint(CONTAINER_COLOR_MAGENTA);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_MAROON))
                     {
                        g2d.setPaint(CONTAINER_COLOR_MAROON);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_ORANGE))
                     {
                        g2d.setPaint(CONTAINER_COLOR_ORANGE);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_YELLOW))
                     {
                        g2d.setPaint(CONTAINER_COLOR_YELLOW);
                     }
                     else if (deck_color.equals(CONTAINER_COLOR_GRAY))
                     {
                        g2d.setPaint(CONTAINER_COLOR_GRAY);
                     }
                     else // no deck color defined or deckcolor = various
                     {
                        int random = (int)(Math.random() * 10 + 1);    // 1 - 10 return value

                        switch (random) 
                        {
                           case 1:  g2d.setPaint(color_container_1); break;
                           case 2:  g2d.setPaint(color_container_2); break;
                           case 3:  g2d.setPaint(color_container_3); break;
                           case 4:  g2d.setPaint(color_container_4); break;
                           case 5:  g2d.setPaint(color_container_5); break;
                           case 6:  g2d.setPaint(color_container_6); break;
                           case 7:  g2d.setPaint(color_container_7); break;
                           case 8:  g2d.setPaint(color_container_8); break;
                           case 9:  g2d.setPaint(color_container_9); break;
                           case 10: g2d.setPaint(color_container_10); break;
                           default: g2d.setPaint(color_container_1); break;
                        }
                     } // else (no deck color defined or deckcolor = various)
                  } // if (deck_color != null)
                  else // deck_color == null
                  {
                     int random = (int)(Math.random() * 10 + 1);    // 1 - 10 return value

                     switch (random) 
                     {
                        case 1:  g2d.setPaint(color_container_1); break;
                        case 2:  g2d.setPaint(color_container_2); break;
                        case 3:  g2d.setPaint(color_container_3); break;
                        case 4:  g2d.setPaint(color_container_4); break;
                        case 5:  g2d.setPaint(color_container_5); break;
                        case 6:  g2d.setPaint(color_container_6); break;
                        case 7:  g2d.setPaint(color_container_7); break;
                        case 8:  g2d.setPaint(color_container_8); break;
                        case 9:  g2d.setPaint(color_container_9); break;
                        case 10: g2d.setPaint(color_container_10); break;
                        default: g2d.setPaint(color_container_1); break;
                     }                     
                  } // else (deck-color == null)
               } // else (day mode)




               g2d.fill(shape_container);

               g2d.setColor(color_black);
               g2d.setStroke(new BasicStroke(1.0f));
               g2d.draw(shape_container);

               x_container = x_container + container_breadth;

               //System.out.println("+++ i = " + i + " ;x_container: = " + x_container); 
            } // for (int k = 0; k < number_containers_row; k++)

            y_container = y_container + container_length;
         } // if (doorgaan) 
         else 
         {
            break;
         }
      } // for (int i = 0; i < max_number_containers_rows; i++)   
      
   }

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_general_cargo_classic(Graphics2D g2d, double wind_rose_diameter, boolean night_mode)    
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/
      //
      //
      //
      
      int MAX_HATCHES = 20;
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double xs = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double ys = 0;
      
      
      // set the often used colors
      //
      Color color_gear;
      Color color_hatch;
      if (night_mode)
      {
         color_gear = Color.BLACK;
         color_hatch = color_medium_gray;
      }
      else
      {
         color_gear = color_derricks;
         color_hatch = Color.LIGHT_GRAY;
      }
      
      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;
      
      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));
      
      //////////// bow ////////
      //
      x = 0;                                                                   // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 2.1;                          // NB factor 2.0 is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.1);            // NB factor 0.1 depends on factor used in bow_height_virtual

      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; 
      y1 = y + bow_height_virtual;  
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length = Math.abs(y1) * 2.1;                                  // so the length of the main mid section; nb factor 1.65 depends on the factor used at bow_height_virtual and y

      ///////////// aft ship (classic spherical shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      y3 = y1 + ship_length;
      y4 = y3;
      xs = 0;                                                                   // control point quadTo; relative to origin (centre of wind rose)
      double stern_height_virtual = ship_breadth * 0.5;                         // NB factor 2.0 is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      ys = (wind_rose_diameter / 2.0) - (stern_height_virtual * 1.0);           // NB factor 0.1 depends on factor used in bow_height_virtual


      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.quadTo(xs, ys, x3, y3);
      ship.lineTo(x1, y1);
      
      ship.closePath();
      
      // fill total ship
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_general_cargo_classic);
      }
      g2d.fill(ship);
      
      // contour total ship
      g2d.setStroke(new BasicStroke(1.0f));
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_black);
      }
      g2d.draw(ship);
      

      /////////////// ship bridge /////////
      //
      //System.out.println("+++ wind_rose_diameter = " + wind_rose_diameter);
      
      double bridge_height = 10;
      if (wind_rose_diameter > BRIDGE_DRAW_LIMIT)                             // >= e.g. when 4K screen resolution + TurboWin+ full screen 
      {
         bridge_height = 14;                                           
      }   
      
      double acc_height = bridge_height * 7;
      if (wind_rose_diameter > BRIDGE_DRAW_LIMIT)                             // >= e.g. when 4K screen resolution + TurboWin+ full screen  
      {
         acc_height = bridge_height * 10;
      }
      
      if (acc_height > ship_breadth)
      {
         acc_height = ship_breadth;
      }
      
      double bridge_indention = 3;
      double dist_origin_bridge = 0 + (y3 / 3);                     // NB y3 always positve (because always on aft ship)
      double y_bridge_aft = draw_ship_bridge_classic(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);
 

      ////////////// loading gear (hatches, masts and dericks) ////////////
      //
      double hatch_breath = ship_breadth / 2;
      double hatch_length = hatch_breath;
      double hatch_intermediate = hatch_breath / 2;                // distances between 2 hatches
      double y_hatch = y1;
      double x_hatch = -hatch_breath / 2;
      double y1_derricks_hatch_1_mik = 0;
      
      final double mast_diameter_fore = hatch_breath / 4;          // hatch I
      double x1_mast_fore_port = 0;
      double y1_mast_fore_port = 0;
      double x1_mast_fore_sb = 0;
      double y1_mast_fore_sb = 0;      

      final double mast_diameter_heavy = hatch_breath / 3;         // between hatch II and hatch III
      double x1_mast_heavy = 0;
      double y1_mast_heavy = 0;

      
      // htaches and masts and derricks fore ship
      for (int i = 0; i < MAX_HATCHES; i++) // max 20 hatches
      {
         if (y_hatch + (hatch_length + hatch_intermediate / 2) < (dist_origin_bridge))    
         { 
            // hatches
            //
            //g2d.setPaint(Color.LIGHT_GRAY);
            g2d.setPaint(color_hatch);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.fill3DRect((int) x_hatch, (int) y_hatch, (int) hatch_breath, (int) hatch_length, true);   // raised hatches
            
            g2d.setPaint(color_gear);

            // masts for hatch I
            //
            if (i == 0)
            {
               // port fore mast (for hatch I)
               x1_mast_fore_port = x_hatch - (mast_diameter_fore / 2);            
               y1_mast_fore_port = y_hatch - (hatch_intermediate / 2) - (mast_diameter_fore / 2);
               
               Shape shape_mast_port = new Ellipse2D.Double(x1_mast_fore_port, y1_mast_fore_port, mast_diameter_fore, mast_diameter_fore);
               g2d.fill(shape_mast_port);

               // starboard fore mast (for hatch I)
               x1_mast_fore_sb = x_hatch + hatch_breath - mast_diameter_fore + (mast_diameter_fore / 2);            
               y1_mast_fore_sb = y_hatch - (hatch_intermediate / 2) - (mast_diameter_fore / 2);
               
               Shape shape_mast_sb = new Ellipse2D.Double(x1_mast_fore_sb, y1_mast_fore_sb, mast_diameter_fore, mast_diameter_fore);
               g2d.fill(shape_mast_sb);
               
               // derricks hatch I (defined from fore to aft ship = from hatch I masts to island between hatch I and hatch II)
               y1_derricks_hatch_1_mik = y_hatch + hatch_length + hatch_intermediate / 2;
               
               g2d.setStroke(new BasicStroke(3.0f));
               g2d.draw(new Line2D.Double((x1_mast_fore_port + (mast_diameter_heavy / 2) / 2), (y1_mast_fore_port + mast_diameter_heavy / 2), (x1_mast_fore_port + mast_diameter_heavy / 2), (y1_derricks_hatch_1_mik -1)));
               g2d.draw(new Line2D.Double((x1_mast_fore_sb + mast_diameter_heavy / 2), (y1_mast_fore_sb + mast_diameter_heavy / 2), (x1_mast_fore_sb + mast_diameter_heavy / 2), (y1_derricks_hatch_1_mik -1)));
            } //  if (i == 0)
            
            
            // mast between hatch II and hatch III (heavy duty mast/derrick) and derricks hatch II and hatch III
            //
            if (i == 2)
            {
               // heavy duty mast/derrick (between hatch II and hatch III)
               x1_mast_heavy = 0 - (mast_diameter_heavy / 2);
               y1_mast_heavy = y_hatch - (hatch_intermediate / 2) - (mast_diameter_heavy / 2);
               
               Shape shape_mast_sb = new Ellipse2D.Double(x1_mast_heavy, y1_mast_heavy, mast_diameter_heavy, mast_diameter_heavy);
               g2d.fill(shape_mast_sb);
               
               // derricks hatch II (defined from aft to fore ship = from heavy duty mast to island between hatch II and hatch III))
               g2d.setStroke(new BasicStroke(3.0f));
               g2d.draw(new Line2D.Double((x1_mast_heavy), (y1_mast_heavy + mast_diameter_heavy / 2), (x1_mast_fore_port + mast_diameter_heavy / 2), y1_derricks_hatch_1_mik +1));
               g2d.draw(new Line2D.Double((x1_mast_heavy + mast_diameter_heavy), (y1_mast_heavy + mast_diameter_heavy / 2), (x1_mast_fore_sb + mast_diameter_heavy / 2), y1_derricks_hatch_1_mik +1));
    
               // derricks hatch III (defined from aft to fore ship = from ship bridge to heavy duty mast))
               g2d.setStroke(new BasicStroke(3.0f));
               g2d.draw(new Line2D.Double((x1 + 2), (dist_origin_bridge -2), x1_mast_heavy, (y1_mast_heavy + mast_diameter_heavy / 2)));
               g2d.draw(new Line2D.Double((x2 - 2), (dist_origin_bridge -2), (x1_mast_heavy + mast_diameter_heavy), (y1_mast_heavy + mast_diameter_heavy / 2)));
            } // if (i = 2)
            
            y_hatch += hatch_length + hatch_intermediate;
         } 
         else 
         {
            break;
         }
      } // for (int i = 0; i < MAX_HATCHES; i++)
      
      // hatch aft ship
      if (y_bridge_aft < y3)
      {
         final double mast_diameter_aft = hatch_breath / 4;          // hatch IV
         
         y_hatch = y_bridge_aft + (hatch_intermediate / 2);
         g2d.setPaint(color_hatch);
         g2d.setStroke(new BasicStroke(1.0f));
         g2d.fill3DRect((int) x_hatch, (int) y_hatch, (int) hatch_breath, (int) hatch_length, true);   // raised hatches
         
         g2d.setPaint(color_gear);
         
         // port aft masts (for hatch IV)
         double x1_mast_aft_port = x1 + (mast_diameter_aft / 2);            
         double y1_mast_aft_port = y_bridge_aft;
               
         Shape shape_mast_port_aft = new Ellipse2D.Double(x1_mast_aft_port, y1_mast_aft_port, mast_diameter_aft, mast_diameter_aft);
         g2d.fill(shape_mast_port_aft);

         // starboard aft mast (for hatch IV)
         double x1_mast_aft_sb = x2 - mast_diameter_aft - (mast_diameter_aft / 2);            
         double y1_mast_aft_sb = y_bridge_aft;
               
         Shape shape_mast_sb_aft = new Ellipse2D.Double(x1_mast_aft_sb, y1_mast_aft_sb, mast_diameter_aft, mast_diameter_aft);
         g2d.fill(shape_mast_sb_aft);

         // derricks hatch IV (defined from fore to aft ship = from hatch IV masts to island after hatch IV)
         double y1_derricks_hatch_4_mik = y_hatch + hatch_length + hatch_intermediate / 2;
               
         g2d.setStroke(new BasicStroke(3.0f));
         // NB "x1_mast_heavy": trick to simulate the gear island after hatch IV
         g2d.draw(new Line2D.Double((x1_mast_aft_port + (mast_diameter_aft / 2) / 2), (y1_mast_aft_port + mast_diameter_aft / 2), (x1_mast_heavy), y1_derricks_hatch_4_mik));
         g2d.draw(new Line2D.Double((x1_mast_aft_sb + mast_diameter_aft / 2), (y1_mast_aft_sb + mast_diameter_aft / 2), (x1_mast_heavy + mast_diameter_heavy), y1_derricks_hatch_4_mik));
         
      } // if (y_bridge_aft < y3)        

   }
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_reefer_ship(Graphics2D g2d, double wind_rose_diameter, boolean night_mode) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/
   
      int MAX_HATCHES = 20;
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;
      
      Color color_hatch;
      if (night_mode)
      {
         color_hatch = color_medium_gray;
      }
      else
      {   
         color_hatch = Color.white;
      }
      

      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));
      
      //////////// bow ////////
      //
      x = 0;                                                                // control point quadTo; relative to origin (centre of wind rose)
      //y = -(wind_rose_diameter / 2.0);                                      // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 2;                         // NB factor 2.0 is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.1);         // NB factor 0.1 depends on factor used in bow_height_virtual
      
      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; //x;
      y1 = y + bow_height_virtual;
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length_main = Math.abs(y1) * 2.3;                       // so the length of the main mid section; NB factor 2.3 depends on the factor used at bow_height_virtual and y

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4 - (ship_breadth / 7);
      x6 = x3 + (ship_breadth / 7);
      y3 = y1 + ship_length_main;
      y4 = y3;
      y5 = y3 + (ship_breadth * 0.5);
      y6 = y5;

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      // contour ship
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(Color.DARK_GRAY);                 // g2d.setPaint(color_medium_gray);
      g2d.draw(ship);
      
      // fill ship
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_reefer_ship);
      }
      g2d.fill(ship);
      
      
      /////////////// ship bridge /////////
      //
      double bridge_height = 10;                     
      double acc_height = (y6 - y3);                        
      double bridge_indention = ship_breadth / 9;
      double dist_origin_bridge = y3 - (bridge_height * 2);
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);

      
      /////////////// hatches /////////////
      //
      double hatch_width = ship_breadth / 1.5;
      double hatch_length = hatch_width;
      double hatch_intermediate = hatch_width / 2;                // distances between 2 hatches
      double y_hatch = y1;
      double x_hatch = -hatch_width / 2;
      
      double width_crane_pillar = ship_breadth * 0.15; //0.2;
      
      // hatches and cranes
      for (int i = 0; i < MAX_HATCHES; i++) // max 20 hatches
      {
         if (y_hatch + (hatch_length + hatch_intermediate / 4) < (dist_origin_bridge))    
         { 
           
            // hatches
            g2d.setPaint(color_hatch);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.fill3DRect((int) x_hatch, (int) y_hatch, (int) hatch_width, (int) hatch_length, true);   // raised hatches
            
            // cranes
            double x_pillar_crane = 0 - (width_crane_pillar / 2);
            double y_pillar_crane = y_hatch - hatch_intermediate + (width_crane_pillar / 2);
            double crane_arm_length =  y_hatch + hatch_length + (hatch_intermediate / 2) - (width_crane_pillar * 0.75);       
            draw_crane(g2d, night_mode, ship_breadth, x_pillar_crane, y_pillar_crane, crane_arm_length, width_crane_pillar);
         }
         
         y_hatch += hatch_length + hatch_intermediate;
         
      } // for (int i = 0; i < MAX_HATCHES; i++)   
      
   }
   
   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_lng_tanker_II(Graphics2D g2d, double wind_rose_diameter, boolean night_mode, Color deck_color) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/
   
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;
      
      
      // deck color (might be set via pop-up sub menu)
      if (deck_color != null)
      {
         color_deck_lng_tanker_II = deck_color;                              // NB if var deck_color not known -> default will be used 
      }

      // ship_breadth (width) is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));
      
      //////////// bow ////////
      //
      x = 0;                                                                // control point quadTo; relative to origin (centre of wind rose)
      //y = -(wind_rose_diameter / 2.0);                                      // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 2;                         // NB factor 2.0 is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.1);         // NB factor 0.1 depends on factor used in bow_height_virtual
      
      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; //x;
      y1 = y + bow_height_virtual;
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length_main = Math.abs(y1) * 2.3;                       // so the length of the main mid section; NB factor 2.3 depends on the factor used at bow_height_virtual and y

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4 - (ship_breadth / 7);
      x6 = x3 + (ship_breadth / 7);
      y3 = y1 + ship_length_main;
      y4 = y3;
      y5 = y3 + (ship_breadth * 0.5);
      y6 = y5;

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      // contour ship
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_black);
      g2d.draw(ship);
      
      // fill ship
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_lng_tanker_II);
      }
      g2d.fill(ship);
      
      
      /////////////// ship bridge /////////
      //
      double bridge_height = 8;                     
      double acc_height = (y6 - y3) / 2;                        
      double bridge_indention = ship_breadth / 9;
      double dist_origin_bridge = y3 - (bridge_height * 2);
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);


      
      ////////////// tanks side  ////////////
      //
      double width_tank_side = (x2 - x1) / 8;
      double distance_railing_tank_side = width_tank_side / 2;
      
      
      ////////////// port tank-side ///////////////
      //
      //
      //
      //        3 ___ 4
      //         /  /
      //        /  /
      //      2 |  | 5
      //        |  |
      //        |  |
      //        |  |
      //        |  |
      //      1 ---- 6
      //       
      //
      double x1_tank_side = x3 + distance_railing_tank_side;
      double x2_tank_side = x1_tank_side;
      double x3_tank_side = x2_tank_side + (distance_railing_tank_side * 2);
      double x4_tank_side = x3_tank_side + width_tank_side;
      double x5_tank_side = x2_tank_side + width_tank_side;     
      double x6_tank_side = x1_tank_side + width_tank_side; 
      
      double y1_tank_side = dist_origin_bridge - bridge_height;
      double y2_tank_side = y1 + (ship_breadth / 1.5);
      double y3_tank_side = y1;
      double y4_tank_side = y3_tank_side;
      double y5_tank_side = y2_tank_side;
      double y6_tank_side = y1_tank_side;   
      
      double x3_front_tank_port = x3_tank_side;
      double y3_front_tank_port = y3_tank_side;
      
      
      GeneralPath tank_side = new GeneralPath();
      tank_side.moveTo(x1_tank_side, y1_tank_side);
      tank_side.lineTo(x2_tank_side, y2_tank_side);
      tank_side.lineTo(x3_tank_side, y3_tank_side);
      tank_side.lineTo(x4_tank_side, y4_tank_side);
      tank_side.lineTo(x5_tank_side, y5_tank_side);
      tank_side.lineTo(x6_tank_side, y6_tank_side);
      tank_side.closePath();

      // contour tank-side
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_white);
      g2d.draw(tank_side);
      
      // fill tank-side
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      g2d.fill(tank_side);
      
      
      ////////////// SB tank-side ///////////////
      //
      //
      //
      //    4 ___ 3
      //      \  \
      //       \  \
      //      5 |  | 2
      //        |  |
      //        |  |
      //        |  |
      //        |  |
      //      6 ---- 1
      //       
      //
      x1_tank_side = x4 - distance_railing_tank_side;
      x2_tank_side = x1_tank_side;
      x3_tank_side = x2_tank_side - (distance_railing_tank_side * 2);
      x4_tank_side = x3_tank_side - width_tank_side;
      x5_tank_side = x2_tank_side - width_tank_side;     
      x6_tank_side = x1_tank_side - width_tank_side; 
      
      double x3_front_tank_sb = x3_tank_side;
      double y3_front_tank_sb = y3_tank_side;

      
      GeneralPath tank_side_2 = new GeneralPath();
      tank_side_2.moveTo(x1_tank_side, y1_tank_side);
      tank_side_2.lineTo(x2_tank_side, y2_tank_side);
      tank_side_2.lineTo(x3_tank_side, y3_tank_side);
      tank_side_2.lineTo(x4_tank_side, y4_tank_side);
      tank_side_2.lineTo(x5_tank_side, y5_tank_side);
      tank_side_2.lineTo(x6_tank_side, y6_tank_side);
      tank_side_2.closePath();

      // contour tank-side
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_white);
      g2d.draw(tank_side_2);
      
      // fill tank-side
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      g2d.fill(tank_side_2);
      
      
      // front tank
      g2d.setPaint(color_white);
      g2d.draw(new Line2D.Double(x3_front_tank_port, y3_front_tank_port, x3_front_tank_sb, y3_front_tank_sb));
      
      
       ////////////// pipes  (bow to bridge) ////////////
      //
      double between_pipes = 3;
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(Color.LIGHT_GRAY);

      // from bow to bridge (port side from center line)
      double x_pipe = 0 - between_pipes;
      double y_pipe = y1 + width_tank_side;                         
      for (int i = 0; i < 3; i++) 
      {
         Shape shape_pipe = new Line2D.Double(x_pipe, y_pipe, x_pipe, y1_tank_side);
         g2d.draw(shape_pipe);
         x_pipe -= between_pipes;
      }

      
      ////////////////// manifold pipes ////////////////////
      //
      x_pipe = -(ship_breadth / 2) + between_pipes;
      y_pipe = 0;
      for (int i = 0; i < 6; i++) 
      {
         Shape shape_pipe = new Line2D.Double(x_pipe, y_pipe, x_pipe + ship_breadth - (2 * between_pipes), y_pipe);
         g2d.draw(shape_pipe);
         y_pipe += between_pipes;
      }
      
      
      /////////////////// platform ////////////////////
      //
      //
      //          
      //         port                 sb
      //          |                   |
      //          |                   |              
      //          |                   |      
      //          |                   |
      //          |           3|----- | 4             
      //          |            |      |     
      //          |            |      |       platform
      //          |           2|----- | 1             
      //          |                   |            
      //          |                   |           
      //             
      //          
      //                
      //
      double platform_length = ship_breadth / 2;
      double platform_width = ship_breadth / 3;
      
      
      if (((y4 / 2) + platform_length) < (dist_origin_bridge - bridge_height)) // only if there is enough room to draw 
      {
         double x1_platform = x4;
         double x2_platform = x1_platform - platform_width;      
         double x3_platform = x2_platform;       
         double x4_platform = x1_platform;
      
         double y1_platform = (y4 / 2) + platform_length;
         double y2_platform = y1_platform;   
         double y3_platform = (y4 / 2);       
         double y4_platform = y3_platform;
      
         GeneralPath platform = new GeneralPath();
         platform.moveTo(x1_platform, y1_platform);
         platform.lineTo(x2_platform, y2_platform);
         platform.lineTo(x3_platform, y3_platform);
         platform.lineTo(x4_platform, y4_platform);
         platform.closePath();

         // contour platform
         g2d.setStroke(new BasicStroke(1.0f));
         g2d.setPaint(color_white);
         g2d.draw(platform);
      
         // fill platform
         if (night_mode)
         {
            g2d.setPaint(Color.DARK_GRAY);
         }
         else
         {
            g2d.setPaint(color_bridge_lng_tanker_II);
         }
         g2d.fill(platform);
        
       
         // platform pipes 
         double y_center_platform = y4_platform + (platform_length / 2);
      
         x_pipe = 0 - (3 * between_pipes);                         // center line [bow - aft]
         y_pipe = y_center_platform;
         for (int i = 0; i < 6; i++) 
         {
            Shape shape_pipe = new Line2D.Double(x_pipe, y_pipe, x2_platform, y_pipe);
            g2d.setPaint(Color.LIGHT_GRAY);
            g2d.draw(shape_pipe);
            y_pipe += between_pipes;
            if (i == 1 || i == 3 || i == 5)
            {
               x_pipe += between_pipes;
            }
         } // for (int i = 0; i < 6; i++) 
      } // if (((y4 / 2) + platform_length) < (dist_origin_bridge - bridge_height))
   }
    
   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_fruit_juice_tanker(Graphics2D g2d, double wind_rose_diameter, boolean night_mode) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/
   
      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;
      

      // ship_breadth (ship width) is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));
      
      //////////// bow ////////
      //
      x = 0;                                                                // control point quadTo; relative to origin (centre of wind rose)
      //y = -(wind_rose_diameter / 2.0);                                      // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 2;                         // NB factor 2.0 is indication of the bow curve; quadTo; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.1);         // NB factor 0.1 depends on factor used in bow_height_virtual
      
      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; //x;
      y1 = y + bow_height_virtual;
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length_main = Math.abs(y1) * 2.3;                       // so the length of the main mid section; NB factor 2.3 depends on the factor used at bow_height_virtual and y

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4 - (ship_breadth / 7);
      x6 = x3 + (ship_breadth / 7);
      y3 = y1 + ship_length_main;
      y4 = y3;
      y5 = y3 + (ship_breadth * 0.5);
      y6 = y5;

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      // contour ship
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_black);
      g2d.draw(ship);
      
      // fill ship
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_tank_deck_fruit_juice_tanker);
      }
      g2d.fill(ship);
   
      
      ////////////////// fill aft ship (fruit juice tanker different color aft deck compared to main section and bow)
      //
      GeneralPath aft_ship = new GeneralPath();
      aft_ship.moveTo(x3, y3);
      aft_ship.lineTo(x4, y4);
      aft_ship.lineTo(x5, y5);
      aft_ship.lineTo(x6, y6);
      aft_ship.closePath();
      
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_aft_deck_fruit_juice_tanker);
      }
      g2d.fill(aft_ship);

      
      /////////////// ship bridge /////////
      //
      double bridge_height = 6;                     
      double acc_height = (y6 - y3) / 2;                        
      double bridge_indention = ship_breadth / 5;
      double dist_origin_bridge = y3;
      draw_ship_bridge_II(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);
      
      
      ////////////// tanks side  ////////////
      //
      double width_tank_side = (x2 - x1) / 6;
      double distance_railing_tank_side = width_tank_side / 2;
      
      
      ////////////// port tank-side ///////////////
      //
      //
      //
      //          3   4
      //          /  /
      //         /  /
      //      2 |  | 5
      //        |  |
      //        |  |
      //        |  | 6
      //        | /  
      //        1 
      //       
      //
      double x1_tank_side = x3 + distance_railing_tank_side;
      double x2_tank_side = x1_tank_side;
      double x3_tank_side = x2_tank_side + (distance_railing_tank_side * 2);
      double x4_tank_side = x3_tank_side + width_tank_side;
      double x5_tank_side = x2_tank_side + width_tank_side;     
      double x6_tank_side = x1_tank_side + width_tank_side; 
      
      double y1_tank_side = dist_origin_bridge;
      double y2_tank_side = y1 + (ship_breadth / 3);
      double y3_tank_side = y1 - (bow_height_virtual / 4);    //y1;
      double y4_tank_side = y3_tank_side;
      double y5_tank_side = y2_tank_side;
      double y6_tank_side = y1_tank_side - distance_railing_tank_side * 4;  
      
      double x4_front_tank_port = x4_tank_side;
      double y4_front_tank_port = y4_tank_side;
      
      
      GeneralPath tank_side = new GeneralPath();
      tank_side.moveTo(x1_tank_side, y1_tank_side);
      tank_side.lineTo(x2_tank_side, y2_tank_side);
      tank_side.lineTo(x4_tank_side, y4_tank_side);
      tank_side.lineTo(x5_tank_side, y5_tank_side);
      tank_side.lineTo(x6_tank_side, y6_tank_side);
      tank_side.closePath();

      // contour tank-side
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_tanks_fruit_juice);
      g2d.draw(tank_side);
      
      // fill tank-side
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_tanks_fruit_juice);
      }
      g2d.fill(tank_side);
      
      
      ////////////// SB tank-side ///////////////
      //
      //
      //
      //     4  3
      //      \  \
      //       \  \
      //      5 |  | 2
      //        |  |
      //        |  |
      //      6 |  |
      //         \ |
      //           1
      //       
      //
      x1_tank_side = x4 - distance_railing_tank_side;
      x2_tank_side = x1_tank_side;
      x3_tank_side = x2_tank_side - (distance_railing_tank_side * 2);
      x4_tank_side = x3_tank_side - width_tank_side;
      x5_tank_side = x2_tank_side - width_tank_side;     
      x6_tank_side = x1_tank_side - width_tank_side; 
      
      double x4_front_tank_sb = x4_tank_side;
      double y4_front_tank_sb = y4_tank_side;

      
      GeneralPath tank_side_2 = new GeneralPath();
      tank_side_2.moveTo(x1_tank_side, y1_tank_side);
      tank_side_2.lineTo(x2_tank_side, y2_tank_side);
      tank_side_2.lineTo(x4_tank_side, y4_tank_side);
      tank_side_2.lineTo(x5_tank_side, y5_tank_side);
      tank_side_2.lineTo(x6_tank_side, y6_tank_side);
      tank_side_2.closePath();

      // contour tank-side
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_tanks_fruit_juice);
      g2d.draw(tank_side_2);
      
      // fill tank-side
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_tanks_fruit_juice);
      }
      g2d.fill(tank_side_2);
      
      
      // front of the tank
      g2d.setPaint(color_tanks_fruit_juice);
      g2d.draw(new Line2D.Double(x4_front_tank_port, y4_front_tank_port, x4_front_tank_sb, y4_front_tank_sb));
      
      
      ////////////// pipes  (bow to bridge) ////////////
      //
      double between_pipes = 3;
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(Color.DARK_GRAY);
      
      //     ^ bow 
      //     |
      //
      //       
      //     | (x4, y4)        <----- at the height of the begin of the bow
      //     |
      //     |
      //     | }
      //     | }    platform
      //     | }
      //     |
      //     |
      //     | (x3, y3)
      //     \ 
      //       \
      //        |  (x2,y2)
      //        |
      //        |
      //        |  (x1, y1)     <----- at the height of the bridge
      //
      //      aft ship  
      //
      double y_pipe_front = y3_tank_side + 2;
      double y_pipe_aft = y3 - (ship_breadth / 10);                    // at the height of the bridge 
      
      double x1_pipe = 0 + between_pipes;
      double x2_pipe = 0;
      double x3_pipe = 0;
      double x4_pipe = 0;

      double y1_pipe = y_pipe_aft;                    // at the height of the bridge              // center ship
      double y2_pipe = 0 + (y_pipe_aft / 1.1);
      double y3_pipe = 0 + (y_pipe_aft / 1.3);
      double y4_pipe = y_pipe_front;

      for (int i = 0; i < 3; i++) 
      {
         if (i == 2)
         {
            y4_pipe = 0; 
         }
         x2_pipe = x1_pipe;
         x3_pipe = x1_pipe - (between_pipes * 2);
         x4_pipe = x3_pipe;

         double xPoints_deck_line[] = {x1_pipe, x2_pipe, x3_pipe, x4_pipe};         // -values for x goes/points to port (relative to origin)
         double yPoints_deck_line[] = {y1_pipe, y2_pipe, y3_pipe, y4_pipe};         // -values for y goes/points to the bow( relative to origin)

         GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints_deck_line.length);
         polyline.moveTo(xPoints_deck_line[0], yPoints_deck_line[0]);
         for (int index = 1; index < xPoints_deck_line.length; index++) 
         {
            polyline.lineTo(xPoints_deck_line[index], yPoints_deck_line[index]);
         }

         //g2d.setColor(Color.RED);
         g2d.draw(polyline);
         
         x1_pipe -= between_pipes;
      }

      
      /////////////////// platform ////////////////////
      //
      //
      //          
      //         port                 sb
      //          |                    |
      //          |                    |              
      //          |                    |      
      //          |  3               4 |
      //          | |--------------- | | <-- centre ships             
      //          | |                | |     }  
      //          | |                | |     }  platform
      //          | |                | |     }        
      //          | |----------------| |                            
      //          | 2                1 |           
      //          |                    |
      //          |                    |
      //          x3                   x4
      //                
      //
      double platform_width = ship_breadth - (2 * distance_railing_tank_side);
      double platform_length = platform_width;
      
      if ((0 + platform_length) < (dist_origin_bridge - bridge_height)) // only if there is enough room to draw 
      {
         double x1_platform = x4 - distance_railing_tank_side;
         double x2_platform = x1_platform - platform_width;      
         double x3_platform = x2_platform;       
         double x4_platform = x1_platform;
      
         double y1_platform = 0 + platform_length;
         double y2_platform = y1_platform;   
         double y3_platform = 0;       
         double y4_platform = y3_platform;
      
         GeneralPath platform = new GeneralPath();
         platform.moveTo(x1_platform, y1_platform);
         platform.lineTo(x2_platform, y2_platform);
         platform.lineTo(x3_platform, y3_platform);
         platform.lineTo(x4_platform, y4_platform);
         platform.closePath();

         // fill platform
         if (night_mode)
         {
            g2d.setPaint(Color.DARK_GRAY);
         }
         else
         {
            g2d.setPaint(color_tank_deck_fruit_juice_tanker);
         }
         g2d.fill(platform);
        
        
         // platform pipes 
         g2d.setStroke(new BasicStroke(1.0f));
         g2d.setPaint(Color.DARK_GRAY);
       
         // platform pipes, from aft platform to mid platform (port side from center line)
         double y_platform_middle = (y1_platform + y4_platform) / 2;
         double x_platform_pipe = 0 - between_pipes;
         double y_platform_pipe_aft = y1_platform;                         
         for (int i = 0; i < 3; i++) 
         {
            Shape shape_pipe = new Line2D.Double(x_platform_pipe, y_platform_pipe_aft, x_platform_pipe, y_platform_middle);
            g2d.draw(shape_pipe);
            x_platform_pipe -= between_pipes;
         }

         // platform pipes, from mid platform to front platform (port side from center line)
         x_platform_pipe = 0 - between_pipes;
         double y_platform_pipe_front = y4_platform;                         
         for (int i = 0; i < 2; i++) 
         {
            Shape shape_pipe = new Line2D.Double(x_platform_pipe, y_platform_middle, x_platform_pipe, y_platform_pipe_front);
            g2d.draw(shape_pipe);
            x_platform_pipe -= between_pipes;
         }

         // contour platform (after drawing platform pipes!)
         g2d.setStroke(new BasicStroke(2.0f));
         
         if (night_mode)
         {
            g2d.setPaint(color_medium_gray);
         }
         else
         {
            g2d.setPaint(color_white);
         }
         g2d.draw(platform);
         
      } // if ((0 + platform_length) < (dist_origin_bridge - bridge_height))
      
      
      /////// cylinder tank
      //
      //
      //
      //          
      //         port                 sb
      //          |                    |
      //          |                 a b|              
      //          |           3   4    |     
      //          |           -----    |      }
      //          |           |   |    |      }        
      //          |           |   |    |      } cylinder tank
      //          |           |---|    |      }
      //          |           2   1    |
      //          |                    |
      //          | |--------------- | | <-- midships             
      //          | |                | |     }
      //          | |                | |     }  platform
      //          | |                | |     }        
      //          | |----------------| |                            
      //          |                    |           
      //          |                    |
      //          |                    |
      //          x3                   x4
      //  
      //
      // NB 
      //    column a = width_tank_side
      //    column b = distance_railing_tank_side;
      //
      //
  
      double cylinder_width = platform_width / 5;
      double cylinder_length = 2 * cylinder_width;
                 
      double x1_cylinder = x4 - width_tank_side - (2 * distance_railing_tank_side);
      double x2_cylinder = x1_cylinder - cylinder_width;      
      double x3_cylinder = x2_cylinder;       
      double x4_cylinder = x1_cylinder;
      
      double y1_cylinder = 0 - distance_railing_tank_side - (distance_railing_tank_side / 2);
      double y2_cylinder = y1_cylinder;   
      double y3_cylinder = y2_cylinder - cylinder_length;       
      double y4_cylinder = y3_cylinder;
         
      double circle_diameter = x1_cylinder - x2_cylinder;
      double x_aft_circle = x2_cylinder;                           // origin from the boxing rectangle
      double y_aft_circle = y1_cylinder - (circle_diameter / 2) ;  // origin from the boxing rectangle
      double x_front_circle = x2_cylinder;                         // origin from the boxing rectangle
      double y_front_circle = y3_cylinder - (circle_diameter / 2) ;// origin from the boxing rectangle
         
      // front and aft sides cylinder
      Shape shape_front_side = new Ellipse2D.Double(x_front_circle, y_front_circle, circle_diameter, circle_diameter); 
      Shape shape_aft_side = new Ellipse2D.Double(x_aft_circle, y_aft_circle, circle_diameter, circle_diameter);
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_tanks_fruit_juice);
      }
      g2d.fill(shape_front_side);
      g2d.fill(shape_aft_side);
         
      // main part cylinder
      GeneralPath cylinder = new GeneralPath();
      cylinder.moveTo(x1_cylinder, y1_cylinder);
      cylinder.lineTo(x2_cylinder, y2_cylinder);
      cylinder.lineTo(x3_cylinder, y3_cylinder);
      cylinder.lineTo(x4_cylinder, y4_cylinder);
      cylinder.closePath();
      
      // fill cylinder
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_tanks_fruit_juice);
      }
      g2d.fill(cylinder);

   }
    
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_crane(Graphics2D g2d, boolean night_mode, double ship_breadth, double x_pillar_crane, double y_pillar_crane, double crane_arm_length, double width_crane_pillar) 
   {
      ///////////////////////// crane pillars ////////////////////////
      //
      //
      //                          ___
      //                         /   \
      //       (x1_arm,y1_arm)  |     | (x2_arm,y2_arm)     <-- pillar crane
      //                        |\___/|
      //                        |     |
      //                        |     |
      //                        |     |
      //                        |     |                     <-- arm crane
      //                        |     |
      //                        |     |
      //                        |     |
      //                         -----
      //        (x3_arm,y3_arm)          (x4_arm,y4_arm)
      //
      //
      //
      //
      
      if (night_mode)
      {
         g2d.setPaint(Color.BLACK);
      }
      else
      {
         g2d.setPaint(Color.LIGHT_GRAY);
      }
      
      
      double height_crane_pillar = width_crane_pillar;
      double x1_arm = x_pillar_crane;
      double x2_arm = x_pillar_crane + width_crane_pillar;
      double arm_slope = 4;
      double x3_arm = x1_arm + arm_slope;
      double x4_arm = x2_arm - arm_slope;
      
      // if the (ship) figure is very small
      if (x4_arm < x3_arm)
      {
         x3_arm = x1_arm;
         x4_arm = x2_arm;
      }
      
      Shape shape_crane_pillar = new Ellipse2D.Double(x_pillar_crane, y_pillar_crane, width_crane_pillar, height_crane_pillar);
      g2d.fill(shape_crane_pillar);
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_crane_pillar);
     
      
      ///////////////// crane arm /////////////
      //
      //g2d.setStroke(new BasicStroke(3.0f)); 
      // if the (ship) figure is very small (thickness crane arms)
      if (x4_arm < x3_arm)
      {
         g2d.setStroke(new BasicStroke(2.0f)); 
      }
      else // 'normal' figure
      {
         g2d.setStroke(new BasicStroke(3.0f));  
      }
      
     
      double y1_arm = y_pillar_crane + (height_crane_pillar / 2);    // counting from the bow
      double y3_arm = crane_arm_length;
      double y2_arm = y1_arm;
      double y4_arm = y3_arm;
         
      double xPoints[] = {x1_arm, x3_arm, x4_arm, x2_arm};
      double yPoints[] = {y1_arm, y3_arm, y4_arm, y2_arm};          // -values for y goes/points to the bow

      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++) 
      {
         polygon.lineTo(xPoints[index], yPoints[index]);
      }
      polygon.closePath();
      g2d.draw(polygon);  
         
      // crane arm reinforcement part
      double x1_sub = x1_arm + (arm_slope / 2);
      double x2_sub = x2_arm - (arm_slope / 2);
      double y1_sub = (y1_arm + y3_arm) / 2;
      double y2_sub = y1_sub;    
      g2d.draw(new Line2D.Double(x1_sub, y1_sub, x2_sub, y2_sub));
         
   }
           
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_ro_ro_ship(Graphics2D g2d, double wind_rose_diameter, boolean night_mode) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/

      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;

      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      //////////// bow ////////
      //
      x = 0;                                                             // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 1.1;                    // NB factor 1.1 is indication of the bow curve; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.6);      // NB fator 0.6 depends on factor used in bow_height_virtual
      
      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; 
      y1 = y + bow_height_virtual;  
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length = Math.abs(y1) * 1.65;                          // so the length of the main mid section; nb factor 1.65 depends on the factor used at bow_height_virtual and y

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4; // - (ship_breadth / 7);  // so in fact here not a trapezium but a simple square
      x6 = x3; // + (ship_breadth / 7);
      y3 = y1 + ship_length;
      y4 = y3;
      y5 = y3 + (ship_breadth * 1.2);
      y6 = y5;
      

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      // fill total ship
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_ro_ro);
      }
      g2d.fill(ship);
      
      // contour total ship
      g2d.setStroke(new BasicStroke(2.0f));
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(ship);
      
      
      //////////// fill the bow ////////
      GeneralPath bow = new GeneralPath();
      bow.moveTo(x1, y1);
      bow.quadTo(x, y, x2, y2);
      bow.lineTo(x1, y1);
      bow.closePath();
      
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {   
         g2d.setPaint(color_white);
      }
      g2d.fill(bow);
      
      
      ////////////////// deck lanes ////////////
      //
      // NB drawing the deck lanes before drawing the bridge
      //
      
      g2d.setStroke(new BasicStroke(1.0f)); 
      g2d.setColor(Color.BLACK);
      
      // draw horizontal line as begin point deck lanes (where the bow begins)
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(new Line2D.Double(x1, y1, x2, y2));
      
      int number_lanes = 8;
      double lane_breadth = ship_breadth / number_lanes;
      g2d.setColor(color_lanes_ro_ro);
      for (int i = 1; i < number_lanes; i++)
      {
         // draw 7 lines (do not over-write the port and starboard ship contour line)
         double x1_lane = x1 + (i * lane_breadth);
         double x2_lane = x1_lane;                    // always vertical line
         double y1_lane = y1;
         double y2_lane = y6;
         g2d.draw(new Line2D.Double(x1_lane, y1_lane, x2_lane, y2_lane));
      }
      
      
      ///////////////////// ship bridge ////////////////////
      //
      //
      //
      //            (x3,y3)           (x4,y4)
      //                   ------------
      //                  /            \
      //                 /              \
      //                /(x2,y2)         \(x5,y5)
      //  (x1,y1)---------------------------------(x6,y6)            <---- dist_origin_bridge
      //         |                               |
      //         |                               |
      // (x12,y12)---------------------------------(x7,y7)
      //            | (x11,y11)         (x8,y8)|
      //            |                          |
      //            |                          |
      //            |                          |
      //            |                          |
      //  (x10,y10) ---------------------------- (x9,y9)            <--- y3 (aft of ship mid section)
      //
      //
      //
      //
      // NB
      //    bridge_height = distance y1 -> y12
      //    bridge_indention = x1 -> x2
      //
      //    bridge_height2 = distance y2 -> y3         // bridge expansion
      //    bridge_indention2 = x2 -> x3               // bridge expansion
      //
      //    acc_indention = x12 -> x11
      //    acc_height = y10 -> y12
      //
      // NB
      //    all relative to origin (0, 0)
      //
      // NB read in this bridge section "x1_bridge" for "x1" etc
      //
      //
      // NB drawing bridge after drawing the deck lanes
      //
   
      
      double bridge_height = ship_breadth / 9;                    
      double bridge_indention = ship_breadth / 3;
      double bridge_height2 = ship_breadth / 10;        // bridge expansion                   
      double bridge_indention2 = ship_breadth / 10;     // bridge expansion
      double dist_origin_bridge = 0 + (y3 / 3);         // NB y3 always positve (because always on aft ship)
      
      double bridge_overhang = 3;
      double acc_indention = bridge_overhang;//ship_breadth / 12;
      
      double x1_bridge = -ship_breadth / 2 - bridge_overhang;
      double x2_bridge = x1_bridge + bridge_indention;
      double x3_bridge = x2_bridge + bridge_indention2;
      
      double x12_bridge = x1_bridge;
      double x11_bridge = x12_bridge + acc_indention;
      double x10_bridge = x11_bridge;
      
      double x6_bridge = ship_breadth / 2 + bridge_overhang;
      double x5_bridge = x6_bridge - bridge_indention;
      double x4_bridge = x5_bridge - bridge_indention2;
      
      double x7_bridge = x6_bridge;
      double x8_bridge = x7_bridge - acc_indention;
      double x9_bridge = x8_bridge;
      
      double y1_bridge = dist_origin_bridge;        
      double y2_bridge = y1_bridge;       
      double y3_bridge = y2_bridge - bridge_height2;        
      double y4_bridge = y3_bridge ;      
      double y5_bridge = y1_bridge;       
      double y6_bridge = y1_bridge;     
      double y7_bridge = y6_bridge + bridge_height;   
      
      double acc_height = y3 - y7_bridge;
      
      double y8_bridge = y7_bridge;    
      double y9_bridge = y8_bridge + acc_height;
      double y10_bridge = y9_bridge;
      double y11_bridge = y8_bridge;
      double y12_bridge = y8_bridge;
      
      double xPoints_bridge[] = {x1_bridge, x2_bridge, x3_bridge, x4_bridge, x5_bridge, x6_bridge, x7_bridge, x8_bridge, x9_bridge, x10_bridge, x11_bridge, x12_bridge};
      double yPoints_bridge[] = {y1_bridge, y2_bridge, y3_bridge, y4_bridge, y5_bridge, y6_bridge, y7_bridge, y8_bridge, y9_bridge, y10_bridge, y11_bridge, y12_bridge};                   // -values for y goes/points to the bow

      GeneralPath polygon_bridge = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints_bridge.length);
      polygon_bridge.moveTo(xPoints_bridge[0], yPoints_bridge[0]);
      for (int index = 1; index < xPoints_bridge.length; index++)            
      {
         polygon_bridge.lineTo(xPoints_bridge[index], yPoints_bridge[index]);
      }
      polygon_bridge.closePath();   
      
      // bridge fill
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {
         g2d.setPaint(color_bridge_ro_ro_ship);
      }
      g2d.fill(polygon_bridge);  
      
      // bridge contour
      g2d.setStroke(new BasicStroke(2.0f)); 
      
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(polygon_bridge);
      
      // draw horizontal lines as indication of a 2nd deck aft accomodation
      double between_acc = 5;
      g2d.draw(new Line2D.Double(x10_bridge, y10_bridge - between_acc, x9_bridge, y9_bridge - between_acc));
      
      
      //////// satellite dome's (on bridge)  ///////
      //
      double dome_diameter = ship_breadth / 10;
      double x1_dome = 0 - dome_diameter / 2;                      // just on the centre line                               
      double y1_dome = y12_bridge;                                         
      //double x2_dome = x1_dome;                                    // 2nd dome behind the 1st one                           
      //double y2_dome = y1_dome + (dome_diameter * 2);
      
      Shape shape_dome_1 = new Ellipse2D.Double(x1_dome, y1_dome, dome_diameter, dome_diameter);
      //Shape shape_dome_2 = new Ellipse2D.Double(x2_dome, y2_dome, dome_diameter, dome_diameter);
      
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_white);
      }
      g2d.fill(shape_dome_1);
      //g2d.fill(shape_dome_2);
      
      // contour domes
      g2d.setStroke(new BasicStroke(1.0f)); 
      g2d.setPaint(color_black);
      g2d.draw(shape_dome_1);
      //g2d.draw(shape_dome_2);
      
      
      ///////////// stern ramp   ////////////
      //
      g2d.setStroke(new BasicStroke(6.0f)); 
      if (night_mode)
      {
         g2d.setColor(Color.DARK_GRAY);
      }
      else
      {
         g2d.setColor(color_bridge_ro_ro_ship);
      }
      double x1_ramp = x1 + lane_breadth;
      double x2_ramp = x2 - lane_breadth;
      g2d.draw(new Line2D.Double(x1_ramp, y6, x2_ramp, y5));
   }      
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_ferry(Graphics2D g2d, double wind_rose_diameter, boolean night_mode) 
   {
      //
      //          (x,y).
      //               
      //
      //               ^
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      // eg see http://what-when-how.com/introduction-to-computer-graphics-using-java-2d-and-3d/basic-principles-of-two-dimensional-graphics-introduction-to-computer-graphics-using-java-2d-and-3d-part-2/

      double x = 0;
      double y = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double x3 = 0;
      double y3 = 0;
      double x4 = 0;
      double y4 = 0;
      double x5 = 0;
      double y5 = 0;
      double x6 = 0;
      double y6 = 0;

      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      //////////// bow ////////
      //
      x = 0;                                                             // control point quadTo; relative to origin (centre of wind rose)
      double bow_height_virtual = ship_breadth * 1.2;                    // NB factor 1.2 indicates the bow curve; quadTo; vertical distance from control point to start end end point       
      y = -(wind_rose_diameter / 2.0) + (bow_height_virtual * 0.5);      // NB factor 0.5 depends on factor used in bow_height_virtual
      
      ///////////// main ship section (rectangle shape) ///////////
      //
      x1 = -ship_breadth / 2.0; 
      y1 = y + bow_height_virtual;  
      x2 = ship_breadth / 2.0;
      y2 = y1;
      double ship_length = Math.abs(y1) * 1.7;                          // so the length of the main mid section; nb factor 1.7 depends on the factor used at bow_height_virtual and y

      ///////////// aft ship (trapezium shape) ///////////
      //
      x3 = x1;
      x4 = x3 + ship_breadth;
      x5 = x4; // - (ship_breadth / 7);  // so in fact here not a trapezium but a simple square
      x6 = x3; // + (ship_breadth / 7);
      y3 = y1 + ship_length;
      y4 = y3;
      y5 = y3 + (ship_breadth * 1.2);
      y6 = y5;

      ////////////// total ship ///////////////
      //
      GeneralPath ship = new GeneralPath();
      ship.moveTo(x1, y1);
      ship.quadTo(x, y, x2, y2);
      ship.lineTo(x4, y4);
      ship.lineTo(x5, y5);
      ship.lineTo(x6, y6);
      ship.lineTo(x3, y3);
      ship.closePath();

      g2d.setStroke(new BasicStroke(2.0f));
      if (night_mode)
      {
         g2d.setPaint(color_black);
      }
      else
      {
         g2d.setPaint(color_white);
      }
      g2d.draw(ship);
      
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck1_ferry);
      }
      g2d.fill(ship);

      
      
      ///////////////////// ship bridge ////////////////////
      //
      //
      //
      //            (x3,y3)           (x4,y4)
      //                   ------------
      //                  /            \
      //                 /              \
      //                /(x2,y2)         \(x5,y5)
      //  (x1,y1)---------------------------------(x6,y6)            <---- dist_origin_bridge
      //         |                               |
      //         |                               |
      // (x12,y12)---------------------------------(x7,y7)
      //            | (x11,y11)         (x8,y8)|
      //            |                          |
      //            |                          |
      //            |                          |
      //            |                          |
      //  (x10,y10) ---------------------------- (x9,y9)            <--- y3 (aft of ship mid section)
      //
      //
      //
      //
      // NB
      //    bridge_height = distance y1 -> y12
      //    bridge_indention = x1 -> x2
      //
      //    bridge_height2 = distance y2 -> y3         // bridge expansion
      //    bridge_indention2 = x2 -> x3               // bridge expansion
      //
      //    acc_indention = x12 -> x11
      //    acc_height = y10 -> y12
      //
      // NB
      //    all relative to origin (0, 0)
      //
      // NB read in this bridge section "x1_bridge" for "x1" etc
      //
      //
      // NB drawing bridge after drawing the deck lanes
      //
   
      
      double bridge_height = ship_breadth / 9;                    
      double bridge_indention = ship_breadth / 3;
      double bridge_height2 = ship_breadth / 10;        // bridge expansion                   
      double bridge_indention2 = ship_breadth / 10;     // bridge expansion
      double dist_origin_bridge = y1; 
      
      double bridge_overhang = 3;
      double acc_indention = ship_breadth / 8;
      
      double x1_bridge = -ship_breadth / 2 - bridge_overhang;
      double x2_bridge = x1_bridge + bridge_indention;
      double x3_bridge = x2_bridge + bridge_indention2;
      
      double x12_bridge = x1_bridge;
      double x11_bridge = x12_bridge + acc_indention;
      double x10_bridge = x11_bridge;
      
      double x6_bridge = ship_breadth / 2 + bridge_overhang;
      double x5_bridge = x6_bridge - bridge_indention;
      double x4_bridge = x5_bridge - bridge_indention2;
      
      double x7_bridge = x6_bridge;
      double x8_bridge = x7_bridge - acc_indention;
      double x9_bridge = x8_bridge;
      
      double y1_bridge = dist_origin_bridge;        
      double y2_bridge = y1_bridge;       
      double y3_bridge = y2_bridge - bridge_height2;        
      double y4_bridge = y3_bridge ;      
      double y5_bridge = y1_bridge;       
      double y6_bridge = y1_bridge;     
      double y7_bridge = y6_bridge + bridge_height;   
      
      double acc_height = (y6 - ship_breadth / 2) - y7_bridge;
      
      double y8_bridge = y7_bridge;    
      double y9_bridge = y8_bridge + acc_height;
      double y10_bridge = y9_bridge;
      double y11_bridge = y8_bridge;
      double y12_bridge = y8_bridge;
      
      double xPoints_bridge[] = {x1_bridge, x2_bridge, x3_bridge, x4_bridge, x5_bridge, x6_bridge, x7_bridge, x8_bridge, x9_bridge, x10_bridge, x11_bridge, x12_bridge};
      double yPoints_bridge[] = {y1_bridge, y2_bridge, y3_bridge, y4_bridge, y5_bridge, y6_bridge, y7_bridge, y8_bridge, y9_bridge, y10_bridge, y11_bridge, y12_bridge};                   // -values for y goes/points to the bow

      GeneralPath polygon_bridge = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints_bridge.length);
      polygon_bridge.moveTo(xPoints_bridge[0], yPoints_bridge[0]);
      for (int index = 1; index < xPoints_bridge.length; index++)            
      {
         polygon_bridge.lineTo(xPoints_bridge[index], yPoints_bridge[index]);
      }
      polygon_bridge.closePath();   
      
      // bridge fill
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_deck2_ferry);
      }
      g2d.fill(polygon_bridge);  
      
      // bridge contour
      g2d.setStroke(new BasicStroke(2.0f)); 
      if (night_mode)
      {
         g2d.setColor(Color.LIGHT_GRAY);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(polygon_bridge);
      
      
      /////// top acc deck (upper deck/top deck) //////
      //
      //
      //  (x13,y13)             (x14,y14)
      //           -------------
      //           |           |
      //           |           |
      //           |           |
      //           |           |
      //           |           |
      //           _____________
      //   (x16,y16)            (x15,y15)
      //
      //
      double between_acc = 5;
      double x13_bridge = x11_bridge + acc_indention;
      double x14_bridge = x8_bridge - acc_indention;
      double x15_bridge = x14_bridge;
      double x16_bridge = x13_bridge;
      double y13_bridge = y7_bridge + (between_acc * 4);
      double y14_bridge = y13_bridge;
      double y15_bridge = y9_bridge - (between_acc * 4);
      double y16_bridge = y15_bridge;      
       
      double xPoints_top_deck[] = {x13_bridge, x14_bridge, x15_bridge, x16_bridge};
      double yPoints_top_deck[] = {y13_bridge, y14_bridge, y15_bridge, y16_bridge};                   // -values for y goes/points to the bow
        
      GeneralPath polygon_top_deck = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints_top_deck.length);
      polygon_top_deck.moveTo(xPoints_top_deck[0], yPoints_top_deck[0]);
      for (int index = 1; index < xPoints_top_deck.length; index++)            
      {
         polygon_top_deck.lineTo(xPoints_top_deck[index], yPoints_top_deck[index]);
      }
      polygon_top_deck.closePath();        
      
      // top deck fill
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_deck3_ferry);
      }
      g2d.fill(polygon_top_deck);  
              
      // top deck contour
      g2d.setStroke(new BasicStroke(2.0f)); 
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(polygon_top_deck);        
      
       
      ////////////// funnel /////////////
      //
      double x1_funnel = x16_bridge / 1.8; 
      double width_funnel = Math.abs(x1_funnel) * 2;
      double length_funnel = width_funnel * 1.5;
      double y1_funnel = (y15_bridge / 2);

      // fill funnel
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {
         g2d.setPaint(color_funnel_ferry);
      }
      g2d.fill(new RoundRectangle2D.Double(x1_funnel, y1_funnel, width_funnel, length_funnel, 20, 20));
      
      // contour funnel
      if (night_mode)
      {
         g2d.setPaint(color_black);
      }
      else
      {
         g2d.setPaint(Color.BLUE);
      }
      g2d.draw(new RoundRectangle2D.Double(x1_funnel, y1_funnel, width_funnel, length_funnel, 20, 20));

      ///////////// pipes in funnel //////////////
      //
      g2d.setPaint(color_black);
      double diameter_pipe = width_funnel / 4;
      double x1_pipe;
      double y1_pipe = 0;

      for (int p = 0; p < 10; p++) 
      {
         x1_pipe = -(diameter_pipe / 2);
         y1_pipe = (y1_funnel + diameter_pipe) + (p * diameter_pipe);

         if ((diameter_pipe * (p + 1)) < (length_funnel - (diameter_pipe * 2))) 
         {
            Shape shape_pipe = new Ellipse2D.Double(x1_pipe, y1_pipe, diameter_pipe, diameter_pipe);
            g2d.fill(shape_pipe);
         } 
         else 
         {
            break;
         }
      } // for (int p = 0; p < 10; p++)
      
      
      //////// satellite dome's (on top deck)  ///////
      //
      double dome_diameter = ship_breadth / 8;
      
      double x1_dome = 0 - dome_diameter / 2;                                  // middle of the fore acc
      double y1_dome = (y1_funnel - length_funnel + y13_bridge) / 2;           // between bridge and funnel
      double x2_dome = 0 - dome_diameter / 2;                                  // middle of the fore acc
      double y2_dome = y1_dome - (dome_diameter * 2);
      
      Shape shape_dome_1 = new Ellipse2D.Double(x1_dome, y1_dome, dome_diameter, dome_diameter);
      Shape shape_dome_2 = new Ellipse2D.Double(x2_dome, y2_dome, dome_diameter, dome_diameter);
      
      if (night_mode)
      {
         g2d.setPaint(Color.LIGHT_GRAY);
      }
      else
      {
         g2d.setPaint(color_white);
      }
      g2d.fill(shape_dome_1);
      g2d.fill(shape_dome_2);
      
      // contour domes
      g2d.setStroke(new BasicStroke(1.0f)); 
      g2d.setPaint(color_black);
      g2d.draw(shape_dome_1);
      g2d.draw(shape_dome_2);
      
      
      ///////// stern ramp   ////////
      g2d.setStroke(new BasicStroke(6.0f)); 
      if (night_mode)
      {
         g2d.setColor(Color.DARK_GRAY);
      }
      else
      {
         g2d.setColor(Color.BLUE);
      }
      double x1_ramp = x1 + acc_indention;
      double x2_ramp = x2 - acc_indention;
      g2d.draw(new Line2D.Double(x1_ramp, y6, x2_ramp, y5));
      
   }      
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_container_ship(Graphics2D g2d, double wind_rose_diameter, boolean night_mode, Color deck_color)
   {
      // ship_breadth (= ship width) is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      double x = -ship_breadth / 2.0;                                       // relative to origin (centre of wind rose)
      double y = -(wind_rose_diameter / 2.0 - ship_breadth);                // relative to origin (centre of wwind rose)

      ////////////// Bow (ellipse shape) //////////////
      //
      double bow_width = ship_breadth;
      double bow_height = ship_breadth * 2;                                  // total height of the ellipse, bur only the upper half will be visible as the bow        
      Shape shape_bow = new Ellipse2D.Double(x, y, bow_width, bow_height);

      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_bow);   // point East = 0 degrees (normally you would call East 90 degrees...) -> to the left

      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_steel_blue);
      }
      g2d.fill(shape_bow);

      ///////////// main ship section (rectangle shape) ///////////
      //
      double x1 = x;
      double y1 = y + bow_height / 2.0;
      double stern = bow_height / 2;
      double ship_length = (Math.abs(y1) * 2) + stern;    // length without the bow!
      Shape shape_midships = new Rectangle2D.Double(x1, y1, ship_breadth, ship_length);

      g2d.setColor(color_black);
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_midships);

      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else
      {   
         g2d.setPaint(color_deck_steel_blue);
      }
      g2d.fill(shape_midships);

      double bridge_height = 5;                        // for bridge and containers
      if (wind_rose_diameter > BRIDGE_DRAW_LIMIT)      // e.g. if 4K screen resolution
      {
         bridge_height = 7;                            // for bridge and containers
      }
      
      //double acc_height = 10;                        // for bridge and containers
      double acc_height = bridge_height * 2;           // for bridge and containers
      double bridge_indention = ship_breadth / 5; //8;   

      ////////////// containers ////////////
      //
      int max_number_containers_rows = 10;
      int number_containers_in_row = 8;
      double container_breadth = ship_breadth / number_containers_in_row;
      double container_length = 0;
      double container_length_40 = container_breadth * 5;                           // fixed relation (1:5) for 40ft container
      double container_length_20 = container_length_40 / 2;
      double x_container;
      double y_container;
      boolean doorgaan = true;
      double offset_y_bridge = 0;                                               // for cheking space between bridge and first container row for and aft the bridge
      double y_container_offset;                                                // painting on aft ship start with x,y as top left and not bottom left

      for (int m = 0; m < 2; m++) // two loops (fore and aft ship)
      {
         // NB m = 0: fore ship
         // NB m =1: aft ship

         if (m == 0) // fore ship
         {
            y_container = y1;                                                   // now y_container is negative value
         } 
         else // aft ship
         {
            y_container = Math.abs(y1) + stern;//ship_length / 2;                              // now y_container is positive value (-values for y goes to the bow from the origin
         }

         //System.out.println("+++ m = " + m + " y_container= " + y_container); 
         for (int i = 0; i < max_number_containers_rows; i++) 
         {
            if (m == 0) // fore ship
            {
               offset_y_bridge = 0;
            } 
            else // aft ship
            {
               offset_y_bridge = bridge_height + acc_height;
            }

            if (Math.abs(y_container) - offset_y_bridge > container_length_40) 
            {
               container_length = container_length_40;
               doorgaan = true;
            } 
            else if (Math.abs(y_container - offset_y_bridge) > container_length_20) 
            {
               container_length = container_length_20;                             // 20 ft container
               doorgaan = true;
            } 
            else 
            {
               doorgaan = false;
            }

            if (doorgaan) 
            {
               if (m == 0) // fore ship 
               {
                  y_container_offset = 0;                                             // because of the starting point (x,y) of the drawing of a rectangle
               } 
               else // aft ship
               {
                  y_container_offset = container_length;                             // because of the starting point (x,y) of the drawing of a rectangle
               }

               int start_in_row = 0;
               int end_in_row = 0;
               if (i == 0 && m == 0) // first row fore ship (20ft row on the bow)
               {
                  container_length = container_length_20;
                  x_container = x1 + container_breadth;
                  y_container = y1 - container_length;
                  start_in_row = 1;                                                  // because less container space available on the bow
                  end_in_row = number_containers_in_row - 1;                          // because less container space available on the bow
               } 
               else 
               {
                  start_in_row = 0;                                                  // full space for containers available 
                  end_in_row = number_containers_in_row;                             // full space for containers available
                  x_container = x1;
               }

               for (int k = start_in_row; k < end_in_row; k++) 
               {
                  Shape shape_container = new Rectangle2D.Double(x_container, y_container - y_container_offset, container_breadth, container_length);

                  if (night_mode)
                  {
                     g2d.setPaint(CONTAINER_COLOR_NIGHT);
                  }
                  else // day mode 
                  {   
                     if (deck_color != null)
                     {   
                        if (deck_color.equals(CONTAINER_COLOR_WHITE))
                        {
                           g2d.setPaint(CONTAINER_COLOR_WHITE);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_LIGHT_BLUE))
                        {
                           g2d.setPaint(CONTAINER_COLOR_LIGHT_BLUE);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_BLUE))
                        {
                           g2d.setPaint(CONTAINER_COLOR_BLUE);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_DARK_BLUE))
                        {
                           g2d.setPaint(CONTAINER_COLOR_DARK_BLUE);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_GREEN))
                        {
                           g2d.setPaint(CONTAINER_COLOR_GREEN);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_MAGENTA))
                        {
                           g2d.setPaint(CONTAINER_COLOR_MAGENTA);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_MAROON))
                        {
                           g2d.setPaint(CONTAINER_COLOR_MAROON);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_ORANGE))
                        {
                           g2d.setPaint(CONTAINER_COLOR_ORANGE);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_YELLOW))
                        {
                           g2d.setPaint(CONTAINER_COLOR_YELLOW);
                        }
                        else if (deck_color.equals(CONTAINER_COLOR_GRAY))
                        {
                           g2d.setPaint(CONTAINER_COLOR_GRAY);
                        }
                        else // no deck color defined or deckcolor = various
                        {
                           int random = (int)(Math.random() * 10 + 1);    // 1 - 10 return value

                           switch (random) 
                           {
                              case 1:  g2d.setPaint(color_container_1); break;
                              case 2:  g2d.setPaint(color_container_2); break;
                              case 3:  g2d.setPaint(color_container_3); break;
                              case 4:  g2d.setPaint(color_container_4); break;
                              case 5:  g2d.setPaint(color_container_5); break;
                              case 6:  g2d.setPaint(color_container_6); break;
                              case 7:  g2d.setPaint(color_container_7); break;
                              case 8:  g2d.setPaint(color_container_8); break;
                              case 9:  g2d.setPaint(color_container_9); break;
                              case 10: g2d.setPaint(color_container_10); break;
                              default: g2d.setPaint(color_container_1); break;
                           }
                        } // else (no deck color defined or deckcolor = various)
                     } // if (deck_color != null)
                     else // deck_color == null
                     {
                        int random = (int)(Math.random() * 10 + 1);    // 1 - 10 return value

                        switch (random) 
                        {
                           case 1:  g2d.setPaint(color_container_1); break;
                           case 2:  g2d.setPaint(color_container_2); break;
                           case 3:  g2d.setPaint(color_container_3); break;
                           case 4:  g2d.setPaint(color_container_4); break;
                           case 5:  g2d.setPaint(color_container_5); break;
                           case 6:  g2d.setPaint(color_container_6); break;
                           case 7:  g2d.setPaint(color_container_7); break;
                           case 8:  g2d.setPaint(color_container_8); break;
                           case 9:  g2d.setPaint(color_container_9); break;
                           case 10: g2d.setPaint(color_container_10); break;
                           default: g2d.setPaint(color_container_1); break;
                        }                     
                     } // else (deck-color == null)
                  } // else (day mode)
                  
                  g2d.fill(shape_container);

                  g2d.setColor(color_black);
                  g2d.setStroke(new BasicStroke(1.0f));
                  g2d.draw(shape_container);

                  x_container = x_container + container_breadth;

                  //System.out.println("+++ i = " + i + " ;x_container: = " + x_container); 
               } // for (int k = 0; k < number_containers_row; k++)

               if (m == 0) // fore ship
               {
                  y_container = y_container + container_length;
               } 
               else // aft ship
               {
                  y_container = y_container - container_length;
               }
            } 
            else 
            {
               break;
            }
         } // for (int i = 0; i < max_number_containers_rows; i++)   
      } //for (int m = 0; m < 2; m++)   

      //////// ship bridge ////////
      double dist_origin_bridge = 0;
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);

   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_oil_tanker(Graphics2D g2d, double wind_rose_diameter, boolean night_mode, Color deck_color) 
   {
      //
      //  (x,y)  .
      //             /  \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //

      
      // deck color (might be set by pop-up submenu)
      //
      if (deck_color != null)
      {
         color_deck_oil_tanker = deck_color;                                 // NB if var deck_color not known -> default (= var color_deck_oil_tanker) will be used 
      }
      if (night_mode)                                                        // in night mode always dark-gray
      {
         color_deck_oil_tanker = Color.DARK_GRAY;
      }   
      
      
      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      double x = -ship_breadth / 2.0;                                       // relative to origin (centre of wind rose)
      double y = -(wind_rose_diameter / 2.0 - ship_breadth);                // relative to origin (centre of wind rose)

      ////////////// Bow (ellipse shape) //////////////
      //
      double bow_width = ship_breadth;
      double bow_height = ship_breadth * 1.7;                                  // total height of the ellipse, bur only the upper half will be visible as the bow        
      Shape shape_bow = new Ellipse2D.Double(x, y, bow_width, bow_height);

      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_bow);   // point East = 0 degrees (normally you would call East 90 degrees...) -> to the left

      g2d.setPaint(color_deck_oil_tanker);
      g2d.fill(shape_bow);

      ///////////// main ship section (rectangle shape) ///////////
      //
      double x1 = x;
      double y1 = y + bow_height / 2.0;
      double ship_length = Math.abs(y1) * 2;                  // so the length of the main mid section
      Shape shape_midships = new Rectangle2D.Double(x1, y1, ship_breadth, ship_length);

      g2d.setColor(color_black);
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_midships);

      //g2d.setPaint(color_deck);  
      g2d.setPaint(color_deck_oil_tanker);
      g2d.fill(shape_midships);

   
      ///////////// aft ship (trapezium shape) ///////////
      //
      double x3 = x1;
      double x4 = x3 + ship_breadth;
      double x5 = x4 - (ship_breadth / 5);
      double x6 = x3 + (ship_breadth / 5);
      double y3 = y1 + ship_length;
      double y4 = y3;
      double y5 = y3 + (ship_breadth / 1.5);
      double y6 = y5;

      double xPoints[] = {x3, x4, x5, x6};         // -values for x goes/points to port (relative to origin)
      double yPoints[] = {y3, y4, y5, y6};         // -values for y goes/points to the bow( relative to origin)

      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++) 
      {
         polygon.lineTo(xPoints[index], yPoints[index]);
      }
      polygon.closePath();

      // aft ship fill
      g2d.setPaint(color_deck_oil_tanker);
      g2d.fill(polygon);

      // aft ship contour
      g2d.setColor(color_black);
      g2d.draw(polygon);

      /////////////// ship bridge /////////
      //
      double bridge_height = 7;                      
      double acc_height = (y5 - y4) / 2; //17;                       
      double bridge_indention = ship_breadth / 4;
      double dist_origin_bridge = y3 - bridge_height;
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);

      ////////////// pipes  ////////////
      //
      double between_pipes = 3;
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_pipes_oil_tanker);

      // from bow to bridge (starboard side from center line)
      double x_pipe = 0;
      double y_pipe = y1;                          // at the bow
      for (int i = 0; i < 3; i++) 
      {
         Shape shape_pipe = new Line2D.Double(x_pipe, y_pipe, x_pipe, y_pipe + ship_length - bridge_height - between_pipes);
         g2d.draw(shape_pipe);
         x_pipe += between_pipes;
      }

      // from manifold to bridge (port side from center line)
      x_pipe = -between_pipes;
      y_pipe = 0;                                  // center ship (manifold)                    
      for (int i = 0; i < 3; i++) 
      {
         Shape shape_pipe = new Line2D.Double(x_pipe, y_pipe, x_pipe, y_pipe + (ship_length / 2) - bridge_height - between_pipes);
         g2d.draw(shape_pipe);
         x_pipe -= between_pipes;
      }

      // manifold pipes
      x_pipe = -(ship_breadth / 2) + between_pipes;
      y_pipe = 0;
      for (int i = 0; i < 6; i++) 
      {
         Shape shape_pipe = new Line2D.Double(x_pipe, y_pipe, x_pipe + ship_breadth - (2 * between_pipes), y_pipe);
         g2d.draw(shape_pipe);
         y_pipe += between_pipes;
      }

      ///////////////  deck lines ///////////////
      //
      //       
      //     | (x0, y0)
      //     |
      //     |
      //     | (x1, y1)
      //     \ 
      //       \
      //        |  (x2,y2)
      //        |
      //        |
      //        |  (x3, y3)  
      //
      //    
      //
      double x0_deck_line = 0;
      double x1_deck_line = 0;
      double x2_deck_line = 0;
      double x3_deck_line = 0;

      double y0_deck_line = 0;             // center ship
      double y1_deck_line = y_pipe;
      double y2_deck_line = y1_deck_line + between_pipes;
      double y3_deck_line = y1 + ship_length - bridge_height - between_pipes;

      for (int i = 0; i < 2; i++) 
      {
         if (i == 0) // port deck line
         {
            x0_deck_line = x1;
            x1_deck_line = x1;
            x2_deck_line = -(ship_breadth / 2) + (2 * between_pipes);
            x3_deck_line = x2_deck_line;
         } 
         else // starboard deck line
         {
            x0_deck_line = x1 + ship_breadth;
            x1_deck_line = x1 + ship_breadth;
            x2_deck_line = (ship_breadth / 2) - (2 * between_pipes);
            x3_deck_line = x2_deck_line;
         }

         double xPoints_deck_line[] = {x0_deck_line, x1_deck_line, x2_deck_line, x3_deck_line};         // -values for x goes/points to port (relative to origin)
         double yPoints_deck_line[] = {y0_deck_line, y1_deck_line, y2_deck_line, y3_deck_line};         // -values for y goes/points to the bow( relative to origin)

         GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints_deck_line.length);
         polyline.moveTo(xPoints_deck_line[0], yPoints_deck_line[0]);
         for (int index = 1; index < xPoints_deck_line.length; index++) 
         {
            polyline.lineTo(xPoints_deck_line[index], yPoints_deck_line[index]);
         }

         g2d.setColor(Color.RED);
         g2d.draw(polyline);

      } // for (int i = 0; i < 2; i++)

      ////////////// helicopter landing zone circle ///////////////
      //
      double heli_circle_offset = 4;                      // do not touch the railing or pipes
      double heli_circle_thickness = 1.0f;
      double width_heli = ((ship_breadth / 2) - (2 * heli_circle_thickness) ) / 2;
      int d = (int)width_heli;                            // d = diameter circle        
      int r = d/2;                                        // r = radius circle
      int x_heli = (int)x1 + (int)heli_circle_offset;     // do not touch the railing
      int y_heli = (int)y1 + (int)(ship_length / 6);

      g2d.setPaint(Color.LIGHT_GRAY);
      g2d.fillOval(x_heli, y_heli , d, d);
      
      
      g2d.setStroke(new BasicStroke((float)heli_circle_thickness));
      
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(Color.YELLOW);
      }
      
      
      g2d.drawOval(x_heli, y_heli , d, d);                // This draws a circle or an oval that fits within the rectangle specified by the X, Y, width and height arguments. 
                                                          // The oval is drawn inside a rectangle whose upper left hand corner is at (x_heli, y_heli), and whose width and height are as specified (d).
      
      // 'H'-letter in the circle (perpendicular on fore-aft line)
      //
      //      (x_heli, y_heli)
      //      *------------------------------
      //      |                             |
      //      |    h1        h5        h2   |
      //      |     ------------------      |
      //      |              |              |
      //      |              * (hc_x, hc_y) |
      //      |              |              |
      //      |     ------------------      |
      //      |    h3        h6        h4   |
      //      |                             |
      //      -------------------------------
      //
      //
      int hc_x = x_heli + r;                             // NB 'hc' = helideck centre
      int hc_y = y_heli + r;                             // NB 'hc' = helideck centre
      
      // NB l = half length of the 'H' vertical side: h1-h5 / h5-h2 / h3-h6 / h6-h4
      // NB w = half width horizontal part of the 'H': h5-h6
      int l = r * 2 / 4;
      int w = l * 2 / 3;
      int h1_x = hc_x - l;
      int h1_y = hc_y - w;
      int h2_x = hc_x + l;
      int h2_y = h1_y;  
      int h3_x = h1_x;
      int h3_y = hc_y + w;  
      int h4_x = h2_x;
      int h4_y = h3_y;  
      int h5_x = (h1_x + h2_x) / 2;
      int h5_y = h1_y;
      int h6_x = h5_x;
      int h6_y = h3_y;
      
      g2d.drawLine(h1_x, h1_y, h2_x, h2_y);
      g2d.drawLine(h5_x, h5_y, h6_x, h6_y);
      g2d.drawLine(h3_x, h3_y, h4_x, h4_y);
      
      // reset stroke thickness
      g2d.setStroke(new BasicStroke(1.0f));

   }

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_lng_tanker(Graphics2D g2d, double wind_rose_diameter, boolean night_mode, Color deck_color, Color tank_color) 
   {
      //
      //  (x,y)  .
      //             /  \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //
      
      
      // deck color (might be set via pop-up sub menu)
      if (deck_color != null)
      {
         color_deck_lng_tanker = deck_color;                              // NB if var deck_color not known -> default will be used 
      }

      // tank color (might be set via pop-up sub menu)
      if (tank_color != null)
      {
         // so the default red tank color will be overwritten
         color_lng_tanks = tank_color;                                   // NB if var tank_color not known -> default will be used 
      }
      
      
      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      double x = -ship_breadth / 2.0;                                       // relative to origin (centre of wind rose)
      double y = -(wind_rose_diameter / 2.0 - ship_breadth);                // relative to origin (centre of wind rose)

      ////////////// Bow (ellipse shape) //////////////
      //
      double bow_width = ship_breadth;
      double bow_height = ship_breadth * 2.0;                               // total height of the ellipse, bur only the upper half will be visible as the bow        
      Shape shape_bow = new Ellipse2D.Double(x, y, bow_width, bow_height);

      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_bow);                                                  // point East = 0 degrees (normally you would call East 90 degrees...) -> to the left

      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {   
         g2d.setPaint(color_deck_lng_tanker);
      }
      g2d.fill(shape_bow);

      ///////////// main ship section (rectangle shape) ///////////
      //
      double x1 = x;
      double y1 = y + bow_height / 2.0;
      double ship_length = Math.abs(y1) * 2;                  // so the length of the main mid section
      Shape shape_midships = new Rectangle2D.Double(x1, y1, ship_breadth, ship_length);

      g2d.setColor(color_black);
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_midships);

      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_deck_lng_tanker);
      }
      g2d.fill(shape_midships);


      ///////////// aft ship (trapezium shape) ///////////
      //
      double x3 = x1;
      double x4 = x3 + ship_breadth;
      double x5 = x4 - (ship_breadth / 5);
      double x6 = x3 + (ship_breadth / 5);
      double y3 = y1 + ship_length;
      double y4 = y3;
      double y5 = y3 + (ship_breadth / 1.5);
      double y6 = y5;

      double xPoints[] = {x3, x4, x5, x6};         // -values for x goes/points to port (relative to origin)
      double yPoints[] = {y3, y4, y5, y6};         // -values for y goes/points to the bow( relative to origin)

      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++) 
      {
         polygon.lineTo(xPoints[index], yPoints[index]);
      }
      polygon.closePath();

      // aft ship fill
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_deck_lng_tanker);
      }
      g2d.fill(polygon);

      // aft ship contour
      g2d.setColor(color_black);
      g2d.draw(polygon);

      /////////////// ship bridge /////////
      //
      double bridge_height = 7;                     
      double acc_height = (y5 - y4) / 2; //17;                        
      double bridge_indention = ship_breadth / 5;
      double dist_origin_bridge = y3 - bridge_height;
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);

      ///////////// LNG tanks //////////
      //
      double tank_intermediate = ship_breadth / 10;   // distances between 2 tanks and between tank and railing
      double tank_width = ship_breadth - (2 * tank_intermediate);
      double tank_height = tank_width;

      double y_tank = y1;
      double x_tank = -tank_width / 2;

      for (int i = 0; i < 20; i++) // max 20 hatches
      {
         if (y_tank + (tank_height + tank_intermediate / 2) < (ship_length / 2 - bridge_height)) 
         {
            Shape shape_tank = new Ellipse2D.Double(x_tank, y_tank, tank_width, tank_height);

            // fill tank
            g2d.setPaint(color_lng_tanks);
            g2d.fill(shape_tank);

            // contour tank
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.setPaint(color_black);
            g2d.draw(shape_tank);

            y_tank += tank_height + tank_intermediate;
         } 
         else 
         {
            break;
         }
      } // for (int i = 0; i < 20; i++)

      ////////////// pipes  ////////////
      //
      double between_pipes = 3;
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_lng_tanks);

      // from bow to bridge (starboard side from center line)
      double x_pipe = 0;
      double y_pipe = y1;                          // at the bow
      for (int i = 0; i < 3; i++) 
      {
         Shape shape_pipe = new Line2D.Double(x_pipe, y_pipe, x_pipe, y_pipe + ship_length - bridge_height - between_pipes);
         g2d.draw(shape_pipe);
         x_pipe += between_pipes;
      }

   }

   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_bulk_carrier(Graphics2D g2d, double wind_rose_diameter, boolean night_mode, Color deck_color) 
   {
      //
      //  (x,y)  .
      //              / \
      //             /   \
      //            /     \
      //           /       \
      //  (x1,y1) |         | (x2,y2)                                    y - ^
      //          |         |                                                |
      //          |         |                                                |
      //          |    *    |   * = origin (0,0)                             |
      //          |         |                                     x-  <--------------> x+
      //          |         |                                                |
      //          |         |                                                |
      //  (x3,y3) ----------- (x4,y4)                                        |
      //          \         /                                                v
      //   (x6,y6) --------- (x5,y5)                                      y+
      //
      //
      //

      
      // deck color (might be set by pop-up submenu)
      //
      if (deck_color != null)
      {
         color_bulk_carrier = deck_color;                                   // NB if var deck_color not known -> default (= var color_bulk_carrier) will be used 
      }
      if (night_mode)                                                        // in night mode always dark-gray
      {
         color_bulk_carrier = Color.DARK_GRAY;
      }   
      
      
      // ship_breadth (= ship width) is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 7.0;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      ///////////// general /////
      //
      double x = -ship_breadth / 2.0;                                       // relative to origin (centre of wind rose)
      double y = -(wind_rose_diameter / 2.0 - ship_breadth);                // relative to origin (centre of wind rose)

      ////////////// Bow (ellipse shape) //////////////
      //
      double bow_width = ship_breadth;
      double bow_height = ship_breadth * 1.2;                                  // total height of the ellipse, bur only the upper half will be visible as the bow        
      Shape shape_bow = new Ellipse2D.Double(x, y, bow_width, bow_height);

      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_bow);   // point East = 0 degrees (normally you would call East 90 degrees...) -> to the left

      g2d.setPaint(color_bulk_carrier);
      g2d.fill(shape_bow);

      ///////////// main ship section (rectangle shape) ///////////
      //
      double x1 = x;
      double y1 = y + bow_height / 2.0;
      double ship_length = Math.abs(y1) * 2;                  // so the length of the main mid section
      Shape shape_midships = new Rectangle2D.Double(x1, y1, ship_breadth, ship_length);

      g2d.setColor(color_black);
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_midships);

      //g2d.setPaint(color_deck);  
      g2d.setPaint(color_bulk_carrier);
      g2d.fill(shape_midships);

      

      ///////////// aft ship (trapezium shape) ///////////
      //
      double x3 = x1;
      double x4 = x3 + ship_breadth;
      double x5 = x4 - (ship_breadth / 5);
      double x6 = x3 + (ship_breadth / 5);
      double y3 = y1 + ship_length;
      double y4 = y3;
      double y5 = y3 + (ship_breadth / 1.5);
      double y6 = y5;

      double xPoints[] = {x3, x4, x5, x6};         // -values for x goes/points to port (relative to origin)
      double yPoints[] = {y3, y4, y5, y6};         // -values for y goes/points to the bow( relative to origin)

      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++) 
      {
         polygon.lineTo(xPoints[index], yPoints[index]);
      }
      polygon.closePath();

      // aft ship fill
      g2d.setPaint(color_bulk_carrier);
      g2d.fill(polygon);

      // aft ship contour
      g2d.setColor(color_black);
      g2d.draw(polygon);

      /////////////// ship bridge /////////
      //
      double bridge_height = 7;                      
      double acc_height = (y5 - y4) / 2;//17;                     
      double bridge_indention = ship_breadth / 5;
      double dist_origin_bridge = y3 - bridge_height;
      draw_ship_bridge(g2d, ship_breadth, bridge_height, acc_height, dist_origin_bridge, bridge_indention, night_mode);

      ////////////// hatches ////////////
      //
      double hatch_breath = ship_breadth / 2;
      double hatch_length = hatch_breath;
      double hatch_intermediate = hatch_breath / 2;   // distances between 2 hatches
      double y_hatch = y1;
      double x_hatch = -hatch_breath / 2;

      for (int i = 0; i < 20; i++) // max 20 hatches
      {
         if (y_hatch + (hatch_length + hatch_intermediate / 2) < (ship_length / 2 - bridge_height)) 
         {
            g2d.setPaint(color_bulk_carrier);
            g2d.fill3DRect((int) x_hatch, (int) y_hatch, (int) hatch_breath, (int) hatch_length, true);   // raised hatches
            y_hatch += hatch_length + hatch_intermediate;
         } 
         else 
         {
            break;
         }
      } // for (int i = 0; i < 20; i++)

   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void draw_ship_bridge(Graphics2D g2d, double ship_breadth, double bridge_height, double acc_height, double dist_origin_bridge, double bridge_indention, boolean night_mode) 
   {
      // NB ship bridge for a limited number of ships (not for all ship types)
      
      
      ///////////// ship bridge ////////////
      //
      //
      //
      //  (x1,y1)----------------------------- (x2,y2)          <---- dist_origin_bridge
      //         |                            |
      //         |   (x7,y7)        (x4,y4)   |
      //  (x8,y8)--------               -------(x3,y3)
      //                |               |
      //                |               |
      //          (x6,y6)----------------(x5,y5)
      //
      //
      // NB
      //    bridge_height = distance y2 -> y3
      //    acc_height = distance y3 -> y5
      //    bridge_indention = x3 -> x4
      //
      // NB
      //    all relative to origin (0, 0)
      //

      double bridge_overhang = 3;
      //double bridge_indention = 8;
      double x1_bridge = -ship_breadth / 2 - bridge_overhang;
      double x2_bridge = ship_breadth / 2 + bridge_overhang;
      double x3_bridge = x2_bridge;
      double x4_bridge = ship_breadth / 2 - bridge_indention;
      double x5_bridge = x4_bridge;
      double x6_bridge = -ship_breadth / 2 + bridge_indention;
      double x7_bridge = x6_bridge;
      double x8_bridge = x1_bridge;

      double y1_bridge = dist_origin_bridge; //0;
      double y2_bridge = y1_bridge;
      double y3_bridge = y1_bridge + bridge_height;
      double y4_bridge = y3_bridge + 2;
      double y5_bridge = y1_bridge + bridge_height + acc_height;
      double y6_bridge = y5_bridge;
      double y7_bridge = y4_bridge;
      double y8_bridge = y3_bridge;

      double xPoints[] = {x1_bridge, x2_bridge, x3_bridge, x4_bridge, x5_bridge, x6_bridge, x7_bridge, x8_bridge};
      double yPoints[] = {y1_bridge, y2_bridge, y3_bridge, y4_bridge, y5_bridge, y6_bridge, y7_bridge, y8_bridge};         // -values for y goes/points to the bow

      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++) 
      {
         polygon.lineTo(xPoints[index], yPoints[index]);
      }
      polygon.closePath();

      // bridge fill
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else if (main.ship_type_dashboard.equals(main.CONTAINER_SHIP)) 
      {
         g2d.setPaint(color_bridge_container_ship);
      } 
      else if (main.ship_type_dashboard.equals(main.OIL_TANKER)) 
      {
         g2d.setPaint(color_bridge_oil_tanker);
      } 
      else if (main.ship_type_dashboard.equals(main.LNG_TANKER_II)) 
      {
         g2d.setPaint(color_bridge_lng_tanker_II);
      } 
      else 
      {
         g2d.setPaint(Color.white);
      }
      g2d.fill(polygon);

      // bridge contour
      if (main.ship_type_dashboard.equals(main.OIL_TANKER) || main.ship_type_dashboard.equals(main.LNG_TANKER_II)) 
      {
         if (night_mode)
         {
            g2d.setColor(color_medium_gray);
         }
         else
         {    
            g2d.setColor(color_white);
         }
      } 
      else if (main.ship_type_dashboard.equals(main.REEFER_SHIP)) 
      {
         if (night_mode)
         {
            g2d.setColor(color_black);
         }
         else
         {    
            g2d.setColor(color_medium_gray);
         }
      }       
      else 
      {
         g2d.setColor(color_black);
      }
      g2d.draw(polygon);

   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void draw_ship_bridge_II(Graphics2D g2d, double ship_breadth, double bridge_height, double acc_height, double dist_origin_bridge, double bridge_indention, boolean night_mode) 
   {
      // NB ship bridge for a limited number of ships (not for all ship types)
      
      
      /////////////////// ship bridge //////////////////
      //
      //
      //         (x10,y10)             (x12,y12)     
      //                 ---------------                         <---- bridge expansion
      //                /               \
      //  (x1,y1)-------(x9,y9) (x11,y11) ----- (x2,y2)          <---- dist_origin_bridge
      //         |                            |
      //         |   (x7,y7)        (x4,y4)   |
      //  (x8,y8)--------               -------(x3,y3)
      //                |               |
      //                |               |
      //          (x6,y6)----------------(x5,y5)
      //
      //
      //
      // NB
      //    bridge_height = distance y2 -> y3
      //    acc_height = distance y3 -> y5
      //    bridge_indention = distance x3 -> x4
      //
      //    bridge_indention2 = distance x9 -> x10                  // bridge expansion
      //    bridge_height2 = distance y9 -> y10                     // bridge expansion
      //
      // NB
      //    all relative to origin (0, 0)
      //

      double bridge_overhang = 3;
      //double bridge_indention = 8;
      double bridge_indention2 = ship_breadth / 10;                 // bridge expansion
      double bridge_height2 = ship_breadth / 15;                    // bridge expansion

      double x1_bridge = -ship_breadth / 2 - bridge_overhang;
      double x2_bridge = ship_breadth / 2 + bridge_overhang;
      double x3_bridge = x2_bridge;
      double x4_bridge = ship_breadth / 2 - bridge_indention;
      double x5_bridge = x4_bridge;
      double x6_bridge = -ship_breadth / 2 + bridge_indention;
      double x7_bridge = x6_bridge;
      double x8_bridge = x1_bridge;
      double x9_bridge = x6_bridge;
      double x10_bridge = x9_bridge + bridge_indention2;   
      double x11_bridge = x4_bridge;       
      double x12_bridge = x11_bridge - bridge_indention2;       

      double y1_bridge = dist_origin_bridge; //0;
      double y2_bridge = y1_bridge;
      double y3_bridge = y1_bridge + bridge_height;
      double y4_bridge = y3_bridge;
      double y5_bridge = y1_bridge + bridge_height + acc_height;
      double y6_bridge = y5_bridge;
      double y7_bridge = y4_bridge;
      double y8_bridge = y3_bridge;
      double y9_bridge = y1_bridge;
      double y10_bridge = y9_bridge - bridge_height2;   
      double y11_bridge = y9_bridge;       
      double y12_bridge = y11_bridge - bridge_height2;       


      double xPoints[] = {x1_bridge, x9_bridge, x10_bridge, x12_bridge, x11_bridge, x2_bridge, x3_bridge, x4_bridge, x5_bridge, x6_bridge, x7_bridge, x8_bridge};
      double yPoints[] = {y1_bridge, y9_bridge, y10_bridge, y12_bridge, y11_bridge, y2_bridge, y3_bridge, y4_bridge, y5_bridge, y6_bridge, y7_bridge, y8_bridge};         // -values for y goes/points to the bow

      GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++) 
      {
         polygon.lineTo(xPoints[index], yPoints[index]);
      }
      polygon.closePath();

      // bridge fill
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else 
      {
         g2d.setPaint(color_bridge_fruit_juice);
      }
      g2d.fill(polygon);

      // bridge contour
      if (night_mode)
      {
         g2d.setColor(color_medium_gray);
      }
      else
      {
         g2d.setColor(color_white);
      }
      g2d.draw(polygon);

   }
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private double draw_ship_bridge_classic(Graphics2D g2d, double ship_breadth, double bridge_height, double acc_height, double dist_origin_bridge, double bridge_indention, boolean night_mode) 
   {
      // NB ship bridge for a limited number od ships (not for all ship types)
      
      
      ///////////// ship bridge [curved front !) ////////////
      //
      //
      //                 
      //  (x1,y1)----------------- curved---------------- (x2,y2)          <---- dist_origin_bridge
      //         |                                       |
      //         |   (x7,y7)                   (x4,y4)   |
      //  (x8,y8)--------                          -------(x3,y3)
      //                |                          |
      //                |                          |
      //                |                          |
      //                |                          |
      //                |                          |
      //                | x12,y12)       (x13,y13) |
      //                |        |-------|         |
      //                |        |       |         |
      //          (x6,y6)---------       ---------(x5,y5)
      //                  (x10,y10)     (x11,y11)
      //
      //
      //
      // NB
      //    bridge_height = distance y2 -> y3
      //    acc_height = distance y3 -> y5
      //    bridge_indention = x3 -> x4
      //
      // NB
      //    all relative to origin (0, 0)
      //
      

      double bridge_overhang = 3;
      double x1_bridge = -ship_breadth / 2 - bridge_overhang;
      double x2_bridge = ship_breadth / 2 + bridge_overhang;
      double x3_bridge = x2_bridge;
      double x4_bridge = ship_breadth / 2 - bridge_indention;
      double x5_bridge = x4_bridge;
      double x6_bridge = -ship_breadth / 2 + bridge_indention;
      double x7_bridge = x6_bridge;
      double x8_bridge = x1_bridge;
      
      double x10_bridge = x6_bridge + 10;
      double x11_bridge = x5_bridge - 10;      
      double x12_bridge = x10_bridge;
      double x13_bridge = x11_bridge;       

      double y1_bridge = dist_origin_bridge; 
      double y2_bridge = y1_bridge;
      double y3_bridge = y1_bridge + bridge_height;
      double y4_bridge = y3_bridge + 1;
      double y5_bridge = y1_bridge + bridge_height + acc_height;
      double y6_bridge = y5_bridge;
      double y7_bridge = y4_bridge;
      double y8_bridge = y3_bridge;
      
      double y10_bridge = y6_bridge;
      double y11_bridge = y5_bridge;      
      double y12_bridge = y10_bridge - 5;
      double y13_bridge = y11_bridge - 5;       
      
      double ctrx_curve = 0;
      double ctry_curve = dist_origin_bridge - 15;   // -15: curve of the bridge, greather value = more curve
      
      
      GeneralPath bridge = new GeneralPath();
      bridge.moveTo(x1_bridge, y1_bridge);
      bridge.quadTo(ctrx_curve, ctry_curve, x2_bridge, y2_bridge);
      bridge.lineTo(x3_bridge, y3_bridge);
      bridge.lineTo(x4_bridge, y4_bridge);
      bridge.lineTo(x5_bridge, y5_bridge);
      
      bridge.lineTo(x11_bridge, y11_bridge);
      bridge.lineTo(x13_bridge, y13_bridge);
      bridge.lineTo(x12_bridge, y12_bridge);
      bridge.lineTo(x10_bridge, y10_bridge);
      
      bridge.lineTo(x6_bridge, y6_bridge);
      bridge.lineTo(x7_bridge, y7_bridge);
      bridge.lineTo(x8_bridge, y8_bridge);
      bridge.closePath();
      

      // bridge fill
      if (night_mode)
      {
         g2d.setPaint(Color.DARK_GRAY);
      }
      else 
      {
         g2d.setPaint(Color.white);
      }
      g2d.fill(bridge);
      
      
      // bridge contour
      if (main.ship_type_dashboard.equals(main.OIL_TANKER)) 
      {
         if (night_mode)
         {
            g2d.setColor(color_medium_gray);
         }
         else
         {    
            g2d.setColor(color_white);
         }
      } 
      else 
      {
         g2d.setColor(color_black);
      }
      g2d.draw(bridge);
      
      
      ////////////// funnel /////////////
      //
      double funnel_width = ship_breadth / 5;
      double x1_funnel = 0 - funnel_width / 2;
      double funnel_length = funnel_width * 1.8;
      double y1_funnel = y3_bridge;

      // fill funnel
      g2d.setPaint(Color.BLACK);
      g2d.fill(new RoundRectangle2D.Double(x1_funnel, y1_funnel, funnel_width, funnel_length, 40, 40));   
      
      
      return y6_bridge;
   }
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void draw_passenger_ship(Graphics2D g2d, double wind_rose_diameter, boolean night_mode) 
   {
      double x_control = 0;
      double y_control = 0;
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      double bow_height_virtual = 0;

      // ship_breadth is leading for scaling
      //
      double ship_breadth = wind_rose_diameter / 8.5;

      ////////////// course line ///////////////
      //
      g2d.setColor(color_black);
      float[] dash = {2f, 0f, 2f};
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));
      g2d.draw(new Line2D.Double(0, wind_rose_diameter / 2 + ship_breadth, 0, -wind_rose_diameter / 2 - ship_breadth));

      ////////////// Bow //////////////
      //
      double bow_height = ship_breadth * 3.0;

      x_control = 0;                                                             // control point quadTo; relative to origin (centre of wind rose)
      y_control = -(wind_rose_diameter / 2.0);                                   // control point quadTo; relative to origin (centre of wind rose)
      bow_height_virtual = ship_breadth * 2.5;                                   // quadTo; vertical distance from control point to start end end point       
      x1 = -ship_breadth / 2.0;
      y1 = y_control + bow_height_virtual;
      x2 = ship_breadth / 2.0;
      y2 = y1;

      GeneralPath bow = new GeneralPath();

      bow.moveTo(x1, y1);
      bow.quadTo(x_control, y_control, x2, y2);
      bow.closePath();

      g2d.setStroke(new BasicStroke(1.0f));
      g2d.setPaint(color_black);
      g2d.draw(bow);
      
      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_deck_passenger_ship);
      }
      g2d.fill(bow);

      ///////////// main ship section (rectangle shape) ///////////
      //
      double stern = bow_height / 2;
      double ship_length = (Math.abs(y1) * 2) + stern;    // length without the bow!
      Shape shape_midships = new Rectangle2D.Double(x1, y1, ship_breadth, ship_length);

      g2d.setColor(color_black);
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(shape_midships);

      if (night_mode)
      {
         g2d.setPaint(color_medium_gray);
      }
      else
      {
         g2d.setPaint(color_deck_passenger_ship);
      }
      g2d.fill(shape_midships);

      ////////// ship bridge(s) ////////////
      //
      double x1_curve = x1;
      double y1_curve = y1;
      double ctrx_curve = 0;
      double ctry_curve = y_control + bow_height_virtual / 1.1;
      double x2_curve = x1_curve + ship_breadth;
      double y2_curve = y1_curve;
      double between_curves = ship_breadth / 8;

      for (int i = 0; i < 3; i++) 
      {
         QuadCurve2D quad = new QuadCurve2D.Double(x1_curve + i, y1_curve, ctrx_curve, ctry_curve, x2_curve - i, y2_curve);
         g2d.setColor(color_black);
         g2d.draw(quad);

         y1_curve -= between_curves;
         y2_curve = y1_curve;
         ctry_curve -= between_curves;
         x1_curve += 1.5;
         x2_curve -= 1.5;
      } // for (int i = 0; i < 3; i++)

      ////////// swimming pool /////////////
      //
      //
      //               
      //  y = - (to fore ship) < -------   Y    ------> y = + (to aft ship)
      //                                   |
      //                                   |
      //                                   |
      //                 (x2,y2)           |             (x3,y3)
      //                       ____________|____________                 ^ x = + (to starboard)
      //                      | \ ____________________/ |                |
      //                      |  |         |          | |                |
      //                      |  |         |          | |                |
      //  <--- fore ship      |  |         |          | |                |                             ----> aft ship    
      //                      |  | ________|__________| |                |
      //                      |_/_____________________\_|                |
      //                 (x1,y1)           |              (x4,y4)        v x = - (to port)
      //                                   |
      //                                   |                   
      //                                   |   
      //
      //
      //
      //
      double pool_width = ship_breadth * 0.6;
      double pool_length = pool_width * 1.2;
      double pool_edge = 4;
      double pool_width_inner = pool_width - (2 * pool_edge);
      double pool_length_inner = pool_length - (2 * pool_edge);

      // outer swimming pool coordinates
      double x1_pool = -pool_width / 2;                    //  relative to origin; middle of the pool
      double y1_pool = -pool_length / 2;                   //  relative to origin; middle of the pool
      double x2_pool = x1_pool + pool_width;
      double y2_pool = y1_pool;
      double x3_pool = x2_pool;
      double y3_pool = y2_pool + pool_length;
      double x4_pool = x1_pool;
      double y4_pool = y3_pool;

      // inner swimming pool coordinates
      double x1_sub_pool = x1_pool + pool_edge;
      double y1_sub_pool = y1_pool + pool_edge;
      double x2_sub_pool = x2_pool - pool_edge;
      double y2_sub_pool = y1_sub_pool;
      double x3_sub_pool = x2_sub_pool;
      double y3_sub_pool = y3_pool - pool_edge;
      double x4_sub_pool = x1_sub_pool;
      double y4_sub_pool = y3_sub_pool;

      // fill inner swimming pool (pool bottom)
      if (night_mode)
      {
         g2d.setPaint(Color.BLACK);
      }
      else
      {   
         g2d.setPaint(color_pool_2);
      }
      g2d.fill(new Rectangle2D.Double(x1_sub_pool, y1_sub_pool, pool_width_inner, pool_length_inner));

      // fore pool edge (trapezium shape)
      GeneralPath fpe = new GeneralPath();
      fpe.moveTo(x1_pool, y1_pool);
      fpe.lineTo(x2_pool, y2_pool);
      fpe.lineTo(x2_sub_pool, y2_sub_pool);
      fpe.lineTo(x1_sub_pool, y1_sub_pool);
      fpe.closePath();
      g2d.setPaint(color_black);
      g2d.draw(fpe);
      g2d.setPaint(color_white);
      g2d.fill(fpe);

      // starboard pool edge (trapezium shape)
      GeneralPath spe = new GeneralPath();
      spe.moveTo(x2_pool, y2_pool);
      spe.lineTo(x3_pool, y3_pool);
      spe.lineTo(x3_sub_pool, y3_sub_pool);
      spe.lineTo(x2_sub_pool, y2_sub_pool);
      spe.closePath();
      g2d.setPaint(color_black);
      g2d.draw(spe);
      if (night_mode)
      {
         g2d.setPaint(Color.BLACK);
      }
      else
      {   
         g2d.setPaint(color_pool_1);
      }   
      g2d.fill(spe);

      // aft pool edge (trapezium shape)
      GeneralPath ape = new GeneralPath();
      ape.moveTo(x3_pool, y3_pool);
      ape.lineTo(x4_pool, y4_pool);
      ape.lineTo(x4_sub_pool, y4_sub_pool);
      ape.lineTo(x3_sub_pool, y3_sub_pool);
      ape.closePath();
      g2d.setPaint(color_black);
      g2d.draw(ape);
      if (night_mode)
      {
         g2d.setPaint(Color.BLACK);
      }
      else
      {
         g2d.setPaint(color_pool_1);
      }   
      g2d.fill(ape);

      // port pool edge (trapezium shape)
      GeneralPath ppe = new GeneralPath();
      ppe.moveTo(x4_pool, y4_pool);
      ppe.lineTo(x1_pool, y1_pool);
      ppe.lineTo(x1_sub_pool, y1_sub_pool);
      ppe.lineTo(x4_sub_pool, y4_sub_pool);
      ppe.closePath();
      g2d.setPaint(color_black);
      g2d.draw(ppe);
      if (night_mode)
      {
         g2d.setPaint(Color.BLACK);
      }
      else
      {   
         g2d.setPaint(color_white);
      }   
      g2d.fill(ppe);

      // complete swimming pool (outer pool)
      if (night_mode)
      {
         g2d.setPaint(Color.BLACK);
      }
      else
      {   
         g2d.setPaint(color_pool_3);
      }
      g2d.fill(new Rectangle2D.Double(x1_pool, y1_pool, pool_width, pool_length));

      /*   
      // vortex pool slide (suisbuis)
      //
      // NB e.g. see: https://stackoverflow.com/questions/29638733/java-draw-a-circular-spiral-using-drawarc/29639168
      //
      g2d.setStroke(new BasicStroke(2.0f));
      g2d.setPaint(Color.ORANGE);
      int centerX = 0;
      int centerY = 0 - (int)((Math.abs(y1_pool) / 2)); //(int)(y1_pool + (2 * pool_edge));

      int numIterations = 2;
      int arcWidth = 2;      // start width circle (px)
      int arcGrowDelta = 4;  // growth every circle (px)

      for (int i = 0; i < numIterations; i++) 
      {
         if ((2 * arcWidth) < pool_width - (2 * pool_edge))
         {
            g2d.drawArc(centerX - arcWidth, centerY - arcWidth, 2 * arcWidth, 2 * arcWidth, 0, 180);
            arcWidth += arcGrowDelta;
            g2d.drawArc(centerX - arcWidth, centerY - arcWidth, 2 * arcWidth - arcGrowDelta, 2 * arcWidth, 180, 180);    
         
         } // if (2 * arcWidth < pool_width)
      } // for (int i = 0; i < numIterations; i++) 
      */
      
      /////////// diving board /////
      //
      double dive_platform_width = 10;
      double dive_platform_height = 4;
      double dive_board_width = 2;
      double dive_board_length = 12;
      double dive_platform_indention = (dive_platform_width - dive_board_width) / 2;

      if (dive_platform_width < pool_width - (2 * pool_edge)) 
      {
         double x1_dive = 0;
         double y1_dive = y1_pool;
         double x2_dive = x1_dive + dive_platform_width;
         double y2_dive = y1_dive;
         double x3_dive = x2_dive;
         double y3_dive = y2_dive + dive_platform_height;          // platform 'height' 
         double x4_dive = x3_dive - dive_platform_indention;       // platform indention
         double y4_dive = y3_dive;
         double x5_dive = x4_dive;
         double y5_dive = y4_dive + dive_board_length;             // dive board length;
         double x6_dive = x5_dive - dive_board_width;              // dive board width;
         double y6_dive = y5_dive;
         double x7_dive = x6_dive;
         double y7_dive = y3_dive;
         double x8_dive = x1_dive;
         double y8_dive = y7_dive;

         double xPoints[] = {x1_dive, x2_dive, x3_dive, x4_dive, x5_dive, x6_dive, x7_dive, x8_dive};
         double yPoints[] = {y1_dive, y2_dive, y3_dive, y4_dive, y5_dive, y6_dive, y7_dive, y8_dive};         // -values for y goes/points to the bow

         GeneralPath polygon_dive = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
         polygon_dive.moveTo(xPoints[0], yPoints[0]);
         for (int index = 1; index < xPoints.length; index++) 
         {
            polygon_dive.lineTo(xPoints[index], yPoints[index]);
         }
         polygon_dive.closePath();

         g2d.setPaint(Color.LIGHT_GRAY);
         g2d.fill(polygon_dive);
      } // if (dive_platform_width < pool_width - (2 * pool_edge))

      // accomodation fore and aft
      double between_acc = 4;

      /////////// fore accomodation /////////////
      //
      double length_fore_acc = Math.abs(y1) - (pool_length / 2) - between_curves;
      double x1_gp;
      double y1_gp = 0;
      double ctrx_gp;
      double ctry_gp;
      double x2_gp;
      double y2_gp;
      double x3_gp;
      double y3_gp;
      double x4_gp;
      double y4_gp = 0;

      for (int k = 0; k < 3; k++) 
      {
         GeneralPath gp = new GeneralPath();

         x1_gp = x1 + (k * between_acc);
         y1_gp = y1 + (k * between_acc);
         ctrx_gp = 0;
         ctry_gp = y_control + bow_height_virtual / 1.1 + (k * between_acc);
         x2_gp = x1_gp + ship_breadth - (2 * k * between_acc);
         y2_gp = y1_gp;
         x3_gp = x2_gp;
         y3_gp = y2_gp + length_fore_acc - (2 * k * between_acc);
         x4_gp = x1_gp;
         y4_gp = y3_gp;

         gp.moveTo(x1_gp, y1_gp);
         gp.quadTo(ctrx_gp, ctry_gp, x2_gp, y2_gp);
         gp.lineTo(x3_gp, y3_gp);
         gp.lineTo(x4_gp, y4_gp);
         gp.closePath();

         // contour acc
         g2d.setStroke(new BasicStroke(1.0f));
         g2d.setColor(color_black);
         g2d.draw(gp);

         // fill acc
         if (k == 0) 
         {
            g2d.setPaint(color_deck_passenger_ship);       // to cover the fore midship section line)
            g2d.fill(gp);
         }

         if (k == 2) 
         {
            if (night_mode)
            {
               g2d.setPaint(color_medium_gray); 
            }  
            else
            {
               g2d.setPaint(color_acc_passenger_ship);
            }
            g2d.fill(gp);
         }
      } // for (int k = 0; k < 3; k++)

      //////// satellite dome (after building "fore accomodation"!) ///////
      //
      double dome_diameter = ship_breadth / 4;
      double x1_dome = 0 - dome_diameter / 2;            // middle of the fore acc
      double y1_dome = (y1_gp + y4_gp) / 2 - (dome_diameter / 2);
      Shape shape_dome = new Ellipse2D.Double(x1_dome, y1_dome, dome_diameter, dome_diameter);
      
      if (night_mode)
      {
         g2d.setPaint(Color.LIGHT_GRAY);
      }
      else
      {
         g2d.setPaint(color_white);
      }
      g2d.fill(shape_dome);
      g2d.setPaint(color_black);
      g2d.draw(shape_dome);

      /////////// aft accomodation //////////////
      //
      double length_aft_acc = ship_length / 2 - (pool_length / 2) - between_curves;
      double x1_gp2 = 0;
      double y1_gp2;
      double ctrx_gp2;
      double ctry_gp2;
      double x2_gp2;
      double y2_gp2;
      double x3_gp2;
      double y3_gp2 = 0;
      double x4_gp2;
      double y4_gp2;

      for (int k = 0; k < 3; k++) 
      {
         GeneralPath gp2 = new GeneralPath();

         x1_gp2 = x1 + (k * between_acc);
         y1_gp2 = (pool_length / 2) + between_curves + (k * between_acc);
         ctrx_gp2 = 0;
         ctry_gp2 = y1_gp2 - between_curves - (k * between_acc);
         x2_gp2 = x1_gp2 + ship_breadth - (2 * k * between_acc);
         y2_gp2 = y1_gp2;
         x3_gp2 = x2_gp2;
         y3_gp2 = y2_gp2 + length_aft_acc - (2 * k * between_acc);
         x4_gp2 = x1_gp2;
         y4_gp2 = y3_gp2;

         gp2.moveTo(x1_gp2, y1_gp2);
         gp2.quadTo(ctrx_gp2, ctry_gp2, x2_gp2, y2_gp2);
         gp2.lineTo(x3_gp2, y3_gp2);
         gp2.lineTo(x4_gp2, y4_gp2);
         gp2.closePath();

         // contour acc
         g2d.setStroke(new BasicStroke(1.0f));
         g2d.setColor(color_black);
         g2d.draw(gp2);

         // fill acc
         if (k == 0) 
         {
            g2d.setPaint(color_deck_passenger_ship);       // to cover the fore midship section line)
            g2d.fill(gp2);
         }

         if (k == 2) 
         {
            if (night_mode)
            {
               g2d.setPaint(color_medium_gray); 
            } 
            else
            {   
               g2d.setPaint(color_acc_passenger_ship);
            }
            g2d.fill(gp2);
         }
      } // for (int k = 0; k < 3; k++)

      ////////// funnel /////////////
      //
      double x1_funnel = x1_gp2 / 1.8;
      double width_funnel = Math.abs(x1_funnel) * 2;
      double length_funnel = width_funnel * 1.5;
      double y1_funnel = y3_gp2 - length_funnel;

      g2d.setPaint(color_funnel_passenger_ship);
      g2d.fill(new RoundRectangle2D.Double(x1_funnel, y1_funnel, width_funnel, length_funnel, 20, 20));

      /////////// pipes in funnel //////////////
      //
      g2d.setPaint(color_black);
      double diameter_pipe = width_funnel / 4;
      double x1_pipe;
      double y1_pipe = 0;

      for (int p = 0; p < 10; p++) 
      {
         x1_pipe = -(diameter_pipe / 2);
         y1_pipe = (y1_funnel + diameter_pipe) + (p * diameter_pipe);

         if ((diameter_pipe * (p + 1)) < (length_funnel - diameter_pipe)) 
         {
            Shape shape_pipe = new Ellipse2D.Double(x1_pipe, y1_pipe, diameter_pipe, diameter_pipe);
            g2d.fill(shape_pipe);
         } 
         else 
         {
            break;
         }
      } // for (int p = 0; p < 10; p++)

      ////////// life boats (after building "pipes in funnel" !) ////////////
      //
      g2d.setPaint(color_life_boat);

      double life_boat_breadth = 3.0;
      double life_boat_length = ship_breadth / 6;    // 15

      g2d.setStroke(new BasicStroke((float) life_boat_breadth));
      double x1_life_boat = 0;
      double y1_life_boat = 0;
      double x2_life_boat = 0;
      double y2_life_boat;

      for (int h = 0; h < 2; h++) // h = 0: port side; h = 1 : starboard side
      {
         if (h == 0) // port side
         {
            x1_life_boat = x1;
            y1_life_boat = y1 + life_boat_length;
            x2_life_boat = x1_life_boat;
         } 
         else if (h == 1) // starboard side
         {
            x1_life_boat = x1 + ship_breadth;
            y1_life_boat = y1 + life_boat_length;
            x2_life_boat = x1_life_boat;
         }

         for (int b = 0; b < 20; b++) 
         {
            if (y1_life_boat + (2 * life_boat_length) < y1_pipe) // from "bow" to the "pipes" 
            {
               y2_life_boat = y1_life_boat + life_boat_length;
               g2d.draw(new Line2D.Double(x1_life_boat, y1_life_boat, x2_life_boat, y2_life_boat));

               y1_life_boat += (2 * life_boat_length);
            } 
            else 
            {
               break;
            }
         } //  for (int b = 0; b < 20; b++)
      } // for (int h = 0; h < 2; h++)

   }
   
   private final Color DECK_COLOR_GREEN                                    = new Color(0, 100, 70);           // MUST BE THE SAME AS IN DASBOARD_view_AWS_radar.java and DASHBOARD_VIEW_APR_radar.java  
   private final Color DECK_COLOR_LIGHT_GREEN                              = new Color(0, 204, 102);          // MUST BE THE SAME AS IN DASBOARD_view_AWS_radar.java and DASHBOARD_VIEW_APR_radar.java

   private final Color color_white;
   private final Color color_black;
   private final Color color_deck_passenger_ship;
   private final Color color_pool_1;
   private final Color color_pool_2;
   private final Color color_pool_3;
   private final Color color_pool_4;
   private final Color color_acc_passenger_ship;
   private final Color color_life_boat;
   private final Color color_funnel_passenger_ship;
   private final Color color_funnel_ferry;
   private final Color color_bridge_lng_tanker_II;
   private final Color color_bridge_oil_tanker;
   private final Color color_bridge_container_ship;
   private final Color color_bridge_ro_ro_ship;
   private final Color color_bridge_fruit_juice;
   private Color color_bulk_carrier;
   private Color color_lng_tanks;
   private Color color_deck_lng_tanker;
   private Color color_deck_oil_tanker;
   private final Color color_pipes_oil_tanker;
   private final Color color_deck_steel_blue;
   private Color color_deck_lng_tanker_II;
   private final Color color_deck_reefer_ship;
   private final Color color_deck_general_cargo_classic;
   private final Color color_deck_research_vessel;
   private final Color color_crane_research_vessel;
   private final Color color_hoist_research_vessel;
   private final Color color_cradle_research_vessel;
   private final Color color_bridge_research_vessel;
   private final Color color_night_crane_research_vessel;
   private final Color color_night_hoist_research_vessel;
   private final Color color_night_cradle_research_vessel;
   private Color color_deck_general_cargo_ship; 
   private Color color_deck_general_cargo_ship_II;        
   private final Color color_deck_container_ship_II;
   private final Color color_deck1_ferry;
   private final Color color_deck2_ferry;
   private final Color color_deck3_ferry;
   private final Color color_deck_ro_ro;
   private final Color color_tank_deck_fruit_juice_tanker;
   private final Color color_lanes_ro_ro;
   private final Color color_hatches_general_cargo_ship; 
   private final Color color_hatches_general_cargo_ship_II;
   private final Color color_medium_gray;
   private final Color color_container_1;
   private final Color color_container_2;
   private final Color color_container_3;
   private final Color color_container_4;
   private final Color color_container_5;
   private final Color color_container_6;
   private final Color color_container_7;
   private final Color color_container_8;
   private final Color color_container_9;
   private final Color color_container_10;
   private final Color color_derricks;
   private final Color color_tanks_fruit_juice;
   private final Color color_aft_deck_fruit_juice_tanker;
   private final Color color_deck_tall_ship;
   private final Color color_masts_tall_ship;
   private final Color color_deck_yacht;
   
   public static final Color CONTAINER_COLOR_VARIOUS                       = Color.BLACK;                     // stand-in for various random generated container colors!
   public static final Color CONTAINER_COLOR_GREEN                         = new Color(0, 128, 0);            // Evergreen (https://www.freightcourse.com/shipping-container-colors/)
   public static final Color CONTAINER_COLOR_GRAY                          = Color.LIGHT_GRAY;                // Maersk
   public static final Color CONTAINER_COLOR_BLUE                          = Color.BLUE;                      // COSCO / PIL
   public static final Color CONTAINER_COLOR_LIGHT_BLUE                    = new Color(173, 216, 230);        // Maersk
   public static final Color CONTAINER_COLOR_DARK_BLUE                     = new Color(0, 0, 204);            // CMA CGM / Wan Hai Lines
   public static final Color CONTAINER_COLOR_MAGENTA                       = Color.MAGENTA;                   // ONE
   public static final Color CONTAINER_COLOR_YELLOW                        = new Color(255, 215, 0);          // MSC
   public static final Color CONTAINER_COLOR_ORANGE                        = Color.ORANGE;                    // Hapag Lloyd
   public static final Color CONTAINER_COLOR_WHITE                         = Color.WHITE;                     // YML / CASCO
   public static final Color CONTAINER_COLOR_MAROON                        = new Color(128, 0, 0);            // HMM / Zim Integrated Shipping Services [Deep reddish brown]
   public static final Color CONTAINER_COLOR_NIGHT                         = new Color(90, 90, 90);           // dark gray
   
   private final String FULL_RIGGED                                        = "full_rigged";
   private final String BARQUE                                             = "barque";
   private final int BRIDGE_DRAW_LIMIT                                     = 1000;                            // if wind_rose_diameter > this value: draw bridge slightly different
   
}
