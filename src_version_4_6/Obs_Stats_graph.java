/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package turbowin;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 *
 * @author marti
 */
public class Obs_Stats_graph extends JPanel {
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public Obs_Stats_graph()
   {
      
   }  
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   @Override
   public void paintComponent(Graphics g)
   {
      
      // NB var 'immt_log_ok' will be set in Obs_Stats_view.java in a swingworker thread so possible the boolean value is set later
      //    so it could be that the graph with the first 50 observers bars is diplayed with the warning pop-up message 
      //    (" > 50 different observer names recorded in immt.log. Please send logs to your Port Meteorological Officer")
      //    but it is also possible the pop-up message is displayed over an "empty" graph, all depends on when the swingworker thread is finished
      //    but anyhow, both scenario's are ok and do not cause an exception
      // 
      if (Obs_Stats_view.immt_log_ok)               // NB var immt_log_ok will be set in Obs_Stats_view.java in a swingworker thread so possible the boolean value is  
      {
         super.paintComponent(g);

         double lengte_y_as_mark               = 5;
         double afstand_aanduiding_naar_marker = 3;  // distance between end of indication and begin of marker
         double linker_kantlijn_margin         = 80; // x left top point
         double boven_kantlijn_margin          = 40; // y left top point
         double rechter_kantlijn_margin        = 80;
         double onder_kantlijn_margin          = 60;

         Point2D links_boven_grafiek = new Point2D.Double(linker_kantlijn_margin, boven_kantlijn_margin);
         Point2D links_onder_grafiek = new Point2D.Double(linker_kantlijn_margin, getHeight() - onder_kantlijn_margin);
         Point2D rechts_onder_grafiek = new Point2D.Double(getWidth() - rechter_kantlijn_margin, getHeight() - onder_kantlijn_margin);

         Graphics2D g2 = (Graphics2D)g;      // device


         Color color_raster          = null;
         Color color_raster_blue_air = new Color(114, 160, 193);          //  Air Superiority Blue
         Color color_raster_green    = new Color(0, 153, 0);              // dark green
         //Color color_pen_black     = Color.BLACK;

         if (main.obs_stats_mode.equals(main.OBSERVERS_STATS))
         {
            color_raster = color_raster_green;
         }
         else if (main.obs_stats_mode.equals(main.OBSERVATIONS_STATS))
         {
            color_raster = color_raster_blue_air;
         }


         //
         // safe method for constructing a square
         //
         g2.setPaint(color_raster);
         g2.setStroke(new BasicStroke(2f));

         Rectangle2D rect = new Rectangle2D.Double();
         rect.setFrameFromDiagonal(links_boven_grafiek, rechts_onder_grafiek);
         g2.draw(rect);     


         //
         // y-axis
         //
         Font font_y = new Font("SansSerif", Font.BOLD, 11);
         g2.setFont(font_y);
         
         String test_aanduiding_y = "";
         if (main.obs_stats_mode.equals(main.OBSERVERS_STATS))
         {   
            test_aanduiding_y = "> 100 obs";
         }
         else if (main.obs_stats_mode.equals(main.OBSERVATIONS_STATS))
         {
            test_aanduiding_y = "> 1000 obs";
         }

         int parameter_start_waarde                       = 0;   // lowest parameter value in graph
         int aantal_parameter_markers_y_as                = 10;  // actually 11 markers (if source included)
         int aantal_units_tussen_2_markers                = 1;   // initialisation
         
         if (main.obs_stats_mode.equals(main.OBSERVERS_STATS))
         {   
            aantal_units_tussen_2_markers                = 10;   // nb units = number obs or hPa or degrees or wind speed etc.
         }
         else if (main.obs_stats_mode.equals(main.OBSERVATIONS_STATS))
         {
            aantal_units_tussen_2_markers                = 100;  // nb units = number obs or hPa or degrees or wind speed etc.
         }         
         int max_obs = aantal_parameter_markers_y_as * aantal_units_tussen_2_markers;

         double y_as_lengte                              = links_onder_grafiek.getY() - links_boven_grafiek.getY();
         double schaling                                 = y_as_lengte / aantal_parameter_markers_y_as;

         FontRenderContext context_y = g2.getFontRenderContext();
         Rectangle2D bounds_y = font_y.getStringBounds(test_aanduiding_y, context_y);
         double stringWidth_y = bounds_y.getWidth();
         double ascent_y = -bounds_y.getY();

         for (int i = 0; i <= aantal_parameter_markers_y_as; i++)
         {
            double y_pos_marker = links_onder_grafiek.getY() - (i * (schaling));

            // markers at left x-axis
            //
            g2.setStroke(new BasicStroke(1f));

            Point2D marker_begin_linker_y_as  = new Point2D.Double(links_onder_grafiek.getX() - lengte_y_as_mark, y_pos_marker);
            Point2D marker_eind_linker_y_as   = new Point2D.Double(links_onder_grafiek.getX(), y_pos_marker);

            Line2D line_marker_linker_y_as = new Line2D.Double(marker_begin_linker_y_as, marker_eind_linker_y_as);
            g2.draw(line_marker_linker_y_as);


            // NB drawString doesn't work with a Point2D argument
            int int_aanduiding = parameter_start_waarde + i * aantal_units_tussen_2_markers;

            String aanduiding = "";
            if (i == aantal_parameter_markers_y_as)
            {
               // extra ">" indication for 'max or more'
               aanduiding = "> " + Integer.toString(int_aanduiding) + " obs";
            }
            else
            {
               aanduiding = Integer.toString(int_aanduiding) + " obs";
            }


            // indications at left y-axis
            //
            g2.drawString(aanduiding, (int)(marker_begin_linker_y_as.getX() - stringWidth_y - afstand_aanduiding_naar_marker), (int)(y_pos_marker + ascent_y / 2));


            // markers at right y-axis
            //
            g2.setStroke(new BasicStroke(1f));

            Point2D marker_begin_rechter_y_as  = new Point2D.Double(rechts_onder_grafiek.getX(), y_pos_marker);
            Point2D marker_eind_rechter_y_as   = new Point2D.Double(rechts_onder_grafiek.getX() + lengte_y_as_mark, y_pos_marker);

            Line2D line_marker_rechter_y_as = new Line2D.Double(marker_begin_rechter_y_as, marker_eind_rechter_y_as);
            g2.draw(line_marker_rechter_y_as);

            // indications at right y-as
            //
            g2.drawString(aanduiding, (int)(marker_eind_rechter_y_as.getX() + afstand_aanduiding_naar_marker), (int)(y_pos_marker + /*(stringHeight - leading) / 2*/ ascent_y / 2));


            // horizontal main lines
            //
            if (i != 0) // the x-axis (y = 0) itself do not over write
            {
               g2.setStroke(new BasicStroke(2f));

               Point2D horizontaal_hulp_lijn_begin  = new Point2D.Double(links_onder_grafiek.getX(), y_pos_marker);
               Point2D horizontaal_hulp_lijn_eind   = new Point2D.Double(rechts_onder_grafiek.getX(), y_pos_marker);

               Line2D hulp_lijn = new Line2D.Double(horizontaal_hulp_lijn_begin, horizontaal_hulp_lijn_eind);
               g2.draw(hulp_lijn);
            } // if (i != 0)

         } // for (int i = 0; i <= aantal_markers_y_as; i++)

         //
         // read IMMT log and draw the bars with observers count or with the different obs parameters count
         //
         if (Obs_Stats_view.immt_log_ok)
         {   
            if (main.obs_stats_mode.equals(main.OBSERVERS_STATS))
            {   
               draw_Observers_Bars(g2, links_onder_grafiek, rechts_onder_grafiek, schaling, aantal_units_tussen_2_markers, max_obs);
            }
            else if (main.obs_stats_mode.equals(main.OBSERVATIONS_STATS))
            {
               draw_Observations_Bars(g2, links_onder_grafiek, rechts_onder_grafiek, schaling, aantal_units_tussen_2_markers, max_obs);
            }               
         } // if (Obs_Stats_view.immt_log_ok)
         
      }  // if (Obs_Stats_view.immt_log_ok) 
   } // public void paintComponent(Graphics g) 
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void draw_Observations_Bars(final Graphics2D g2d, Point2D left_below_graph, Point2D right_below_graph, double scaling, int number_units_between_2_markers, int max_obs)  
   {
      // NB lay out scheme
      //    parameters_count_array[0] : air pressure
      //    parameters_count_array[1] : air temp
      //    parameters_count_array[2] : dewpoint
      //    parameters_count_array[3] : sst
      //    parameters_count_array[4] : wind  (speed and/or dir)
      //    parameters_count_array[5] : waves ()
      //    parameters_count_array[6] : weather
      //    parameters_count_array[7] : clouds
      //    parameters_count_array[8] : visibility
      //    parameters_count_array[9] : icing 
      //    parameters_count_array[10] : ice
      
      
      Font font_parameter_name = new Font("Monospaced", Font.PLAIN, 12);
      g2d.setFont(font_parameter_name);
      String test_aanduiding_y = "air pressure";
      FontRenderContext context_parameter_name = g2d.getFontRenderContext();
      Rectangle2D bounds_parameter_name = font_parameter_name.getStringBounds(test_aanduiding_y, context_parameter_name);
      double stringWidth_parameter_name = bounds_parameter_name.getWidth();
      double ascent_parameter_name = -bounds_parameter_name.getY();   
      
      int parameters_count_array[] = new int[Obs_Stats_view.NUMBER_PARAMETERS];
      String parameter_names_array[] = new String[Obs_Stats_view.NUMBER_PARAMETERS];
      
      // initialisation
      for (int i = 0; i < Obs_Stats_view.NUMBER_PARAMETERS; i++)
      {
         parameters_count_array[i] = 0;
      }   
      parameter_names_array[0]  = " air pressure";
      parameter_names_array[1]  = " air temp";
      parameter_names_array[2]  = " dew point";
      parameter_names_array[3]  = " sst";
      parameter_names_array[4]  = " wind";
      parameter_names_array[5]  = " waves";      
      parameter_names_array[6]  = " weather";
      parameter_names_array[7]  = " clouds";
      parameter_names_array[8]  = " visibility";
      parameter_names_array[9]  = " icing";
      parameter_names_array[10] = " ice";
      
      
      
      for (String obs : Obs_Stats_view.immt_list) 
      {
         if (obs.length() >= main.IMMT_5_LENGTH)                       // avoiding out of index-range exceptions        
         {
            String height_clouds             = "";
            String visibility                = "";
            String cloud_amount              = "";
            String true_wind_dir             = "";
            String true_wind_speed           = "";
            String air_temp                  = "";
            String dew_point                 = "";
            String pressure_MSL              = "";
            String present_weather           = "";
            String past_weather_1            = "";
            String past_weather_2            = "";
            String amount_lowest_clouds      = "";
            String Cl                        = "";
            String Cm                        = "";
            String Ch                        = "";
            String sst                       = "";
            String wind_waves_period         = "";
            String wind_waves_height         = "";
            String swell_1_dir               = "";
            String swell_1_period            = "";
            String swell_1_height            = "";      
            String ice_accretion             = "";
            String thickness_ice_accretion   = "";
            String rate_ice_accretion        = "";
            String swell_2_dir               = "";
            String swell_2_period            = "";
            String swell_2_height            = "";  
            String arrangement_sea_ice       = "";
            String stage_development         = "";
            String ice_land_origin           = "";
            String bearing_ice_edge          = "";
            String ice_situation             = "";
            
            
            // see IMMT description
            // eg IMMT record: 32018051112152700620         0129                     0              44TESTNL US 314            23           A599999199999999991191                    9999 999     01234567 
         
            height_clouds             = obs.substring(20, 21);
            visibility                = obs.substring(21, 23);
            cloud_amount              = obs.substring(23, 24);
            true_wind_dir             = obs.substring(24, 26);            // WMO code table 0877
            true_wind_speed           = obs.substring(27, 29);
            air_temp                  = obs.substring(30, 33);
            dew_point                 = obs.substring(34, 37);
            pressure_MSL              = obs.substring(37, 41);
            present_weather           = obs.substring(41, 43);
            past_weather_1            = obs.substring(43, 44);
            past_weather_2            = obs.substring(44, 45);
            amount_lowest_clouds      = obs.substring(45, 46);
            Cl                        = obs.substring(46, 47);
            Cm                        = obs.substring(47, 48);
            Ch                        = obs.substring(48, 49);
            sst                       = obs.substring(50, 53);
            wind_waves_period         = obs.substring(55, 57);
            wind_waves_height         = obs.substring(57, 59);
            swell_1_dir               = obs.substring(59, 61);
            swell_1_period            = obs.substring(61, 63);
            swell_1_height            = obs.substring(63, 65);         
            ice_accretion             = obs.substring(65, 66);
            thickness_ice_accretion   = obs.substring(66, 68);  
            rate_ice_accretion        = obs.substring(68, 69);
            swell_2_dir               = obs.substring(98, 100);
            swell_2_period            = obs.substring(100, 102);
            swell_2_height            = obs.substring(102, 104);  
            arrangement_sea_ice       = obs.substring(104, 105);
            stage_development         = obs.substring(105, 106);
            ice_land_origin           = obs.substring(106, 107);
            bearing_ice_edge          = obs.substring(107, 108);
            ice_situation             = obs.substring(108, 109);


            
            // air pressure
            if ((pressure_MSL.indexOf(' ') == -1) && (pressure_MSL.indexOf('/') == -1))
            {
               parameters_count_array[0]++;
            }   
            
            // air temp
            if ((air_temp.indexOf(' ') == -1) && (air_temp.indexOf('/') == -1))
            {
               parameters_count_array[1]++;
            }  
            
            // dew-point
            if ((dew_point.indexOf(' ') == -1) && (dew_point.indexOf('/') == -1))
            {
               parameters_count_array[2]++;
            }  
            
            // sst
            if ((sst.indexOf(' ') == -1) && (sst.indexOf('/') == -1))
            {
               parameters_count_array[3]++;
            }  
            
            // wind
            if ((true_wind_dir.indexOf(' ') == -1) && (true_wind_dir.indexOf('/') == -1) ||
                (true_wind_speed.indexOf(' ') == -1) && (true_wind_speed.indexOf('/') == -1))
            {
               parameters_count_array[4]++;
            } 
            
            // waves
            if ((wind_waves_period.indexOf(' ') == -1) && (wind_waves_period.indexOf('/') == -1) ||
               (wind_waves_height.indexOf(' ') == -1) && (wind_waves_height.indexOf('/') == -1) ||
               (swell_1_dir.indexOf(' ') == -1) && (swell_1_dir.indexOf('/') == -1) ||
               (swell_1_period.indexOf(' ') == -1) && (swell_1_period.indexOf('/') == -1) ||
               (swell_1_height.indexOf(' ') == -1) && (swell_1_height.indexOf('/') == -1) ||
               (swell_2_dir.indexOf(' ') == -1) && (swell_2_dir.indexOf('/') == -1) ||
               (swell_2_period.indexOf(' ') == -1) && (swell_2_period.indexOf('/') == -1) ||
               (swell_2_height.indexOf(' ') == -1) && (swell_2_height.indexOf('/') == -1))
            {
               parameters_count_array[5]++;
            }   
            
            // weather
            if ((present_weather.indexOf(' ') == -1) && (present_weather.indexOf('/') == -1) ||
                (past_weather_1.indexOf(' ') == -1) && (past_weather_1.indexOf('/') == -1) ||
                (past_weather_2.indexOf(' ') == -1) && (past_weather_2.indexOf('/') == -1) )
            {
               parameters_count_array[6]++; 
            }
            
            // clouds
            if ((height_clouds.indexOf(' ') == -1) && (height_clouds.indexOf('/') == -1) || 
                (cloud_amount.indexOf(' ') == -1) && (cloud_amount.indexOf('/') == -1) || 
                (amount_lowest_clouds.indexOf(' ') == -1) && (amount_lowest_clouds.indexOf('/') == -1) || 
                (Cl.indexOf(' ') == -1) && (Cl.indexOf('/') == -1) || 
                (Cm.indexOf(' ') == -1) && (Cm.indexOf('/') == -1) ||
                (Ch.indexOf(' ') == -1) && (Ch.indexOf('/') == -1) )         
            {
               parameters_count_array[7]++; 
            }  
            
            // visibility
            if ((visibility.indexOf(' ') == -1) && (visibility.indexOf('/') == -1))
            {
               parameters_count_array[8]++;
            }  
            
            // icing
            if ((ice_accretion.indexOf(' ') == -1) && (ice_accretion.indexOf('/') == -1) ||
                (thickness_ice_accretion.indexOf(' ') == -1) && (thickness_ice_accretion.indexOf('/') == -1) ||
                (rate_ice_accretion.indexOf(' ') == -1) && (rate_ice_accretion.indexOf('/') == -1) )
            {
               parameters_count_array[9]++;
            }   
               
            // ice
            if ((arrangement_sea_ice.indexOf(' ') == -1) && (arrangement_sea_ice.indexOf('/') == -1) ||  
                (stage_development.indexOf(' ') == -1) && (stage_development.indexOf('/') == -1) ||  
                (ice_land_origin.indexOf(' ') == -1) && (ice_land_origin.indexOf('/') == -1) ||  
                (bearing_ice_edge.indexOf(' ') == -1) && (bearing_ice_edge.indexOf('/') == -1) ||   
                (ice_situation.indexOf(' ') == -1) && (ice_situation.indexOf('/') == -1) )
            {
               parameters_count_array[10]++;
            }
            
         } // if (obs.length() >= main.IMMT_5_LENGTH) 
      } // for (String obs : Obs_Stats_view.immt_list) 
      
      
      
      // e.g. 6 different observer names -> 7 (6+1) intermediate spaces between the bars -> 13 'blocks'(6 bars and 7 spaces)
      //
      //
      //
      // | -----------------------------------------------------------------------------|   // x-as length
      // | <--->,<--->,<--->,<--->,<--->,<--->,<--->,<--->,<--->,<---><--->,<--->,<---> |   // 13 blocks
      // | space  bar  space  bar  space  bar  space  bar  space  bar space  bar  space |   // 6 bars + 7 paces = 13 blocks 
      //
      //
      //
      //

      int number_blocks = Obs_Stats_view.NUMBER_PARAMETERS + (Obs_Stats_view.NUMBER_PARAMETERS + 1);
      double x_as_length = right_below_graph.getX() - left_below_graph.getX();
      double bar_width = x_as_length / number_blocks;
      
      // NB
      // block (space/bar)    [i] (of parameter[i])     diff. (pass)
      // 1                    0                         1
      // 3                    1                         2
      // 5                    2                         3
      // 7                    3                         4
      // 9                    4                         5
      // etc.
      //
      
      int pass = 0;
      for (int i = 0; i < number_blocks; i++)
      {
         int x_bar = (int) (bar_width * i);
         int y_bar = (int) left_below_graph.getY();
         
         
         // NB even block: space
         //    odd block: bar
         //
         if ((i % 2) != 0) // odd (bar with number of observations)
         {
            pass++;
            
            int number_obs = parameters_count_array[i - pass];
            int number_obs_display = number_obs;       // to display the real number on top of the bar (and not the rounded to 100 number if real obs number > 100)
            
            // the bars indicating the number of observations is limited to max_obs (e.g. 1000)
            if (number_obs > max_obs)
            {
               number_obs = max_obs;
            }
            
            int bar_height = (int) (scaling / number_units_between_2_markers * number_obs);
            
         
            // set the color of the bars
            if (Obs_Stats_view.night_vision)
            { 
               g2d.setColor(Obs_Stats_view.BAR_OBSERVATIONS_NIGHT_COLOR);
            }
            else
            {
               g2d.setColor(Obs_Stats_view.BAR_OBSERVATIONS_DAY_COLOR);
            }
            g2d.fillRect((int)left_below_graph.getX() + x_bar, y_bar - bar_height, (int) bar_width, bar_height);
            
            // number of obs on top of the bar (on the left, to avoid text cluttering with the parameter name)
            if (number_obs_display > 0)
            {
               int x_string_number_obs = (int) (left_below_graph.getX() + x_bar);
               int y_string_number_obs = y_bar - bar_height; 
            
               g2d.setColor(java.awt.Color.BLACK);
               g2d.drawString(Integer.toString(number_obs_display), x_string_number_obs, y_string_number_obs);
            }
            
            // name of the parameter (e.g. air pressure)
            int x_string = (int) (left_below_graph.getX() + x_bar + (bar_width / 2) - stringWidth_parameter_name / 2);
            int y_string = (int) left_below_graph.getY();   
           
            g2d.setColor(java.awt.Color.BLACK);
            
            // set the parameter name vertical in the bar
            x_string = (int) (left_below_graph.getX() + x_bar + (bar_width / 2) + (ascent_parameter_name / 2));
               
            int angle = 270;
            g2d.translate(x_string, y_string);
            g2d.rotate(Math.toRadians(angle));
            g2d.drawString(parameter_names_array[i - pass], 0, 0);
            
            g2d.rotate(-Math.toRadians(angle));                   // rotate back
            g2d.translate(-x_string, -y_string);                  // translate back
         
         } // if ((i % 2) != 0)
      } // for (int i = 1; i < number_blocks; i+=2)  
            

      //
      /////////////// retrieve and display the log period of the IMMT log ////////////////
      //

      String requested_start_date = "";
      String requested_end_date = "";
      if (!Obs_Stats_view.immt_list.isEmpty())
      {   
         if (Obs_Stats_view.view_immt_log_period.equals(myimmtlogperiod.CUSTOM))
         {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");          // e.g. 21-Sep-2025
            requested_start_date = Obs_Stats_view.view_local_start_date.format(dateTimeFormatter);  
            requested_end_date   = Obs_Stats_view.view_local_end_date.format(dateTimeFormatter);  
         }
         else
         {
            requested_start_date = "";
            requested_end_date = "";
         }
         
         String log_line_1 = Obs_Stats_view.immt_rec_first;             // always the first line of the complete raw available immt log
         String log_line_last = Obs_Stats_view.immt_rec_last;           // always the last line of the complete raw available immt log 
         write_period_log_line(log_line_1, log_line_last, requested_start_date, requested_end_date);
      }
      else
      {
         Obs_Stats_view.jLabel1.setText("no records found matching the specified criteria"); 
      }
      
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void draw_Observers_Bars(final Graphics2D g2d, Point2D left_below_graph, Point2D right_below_graph, double scaling, int number_units_between_2_markers, int max_obs)  
   {
      // NB only the saved observations with a observer name at the end of the record string will be taken into account
      
      // font etc, observer name
      Font font_observer_name = new Font("Monospaced", Font.PLAIN, 12);
      g2d.setFont(font_observer_name);
      String test_aanduiding_y = "Brouwer;M.F.;2 off;-;";
      FontRenderContext context_observer_name = g2d.getFontRenderContext();
      Rectangle2D bounds_observer_name = font_observer_name.getStringBounds(test_aanduiding_y, context_observer_name);
      double stringWidth_observer_name = bounds_observer_name.getWidth();
      double ascent_observer_name = -bounds_observer_name.getY();                         // The ascent is the distance from the baseline to the top of the font
      
      
      int different_observer_names = 0;
      String observers_names_array[] = new String[Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES];
      int observers_count_array[] = new int[Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES];     
      
      // initialisation
      for (int i = 0; i < Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES; i++)
      {
         observers_names_array[i] = "";
         observers_count_array[i] = 0;
      }      
      
      
      for (String obs : Obs_Stats_view.immt_list) 
      {
         String observer_name        = "";
         boolean observer_name_found = false;

         if (obs.length() > main.IMMT_5_POSITION_OBSERVER - 1)                            // main.IMMT_5_POSITION_OBSERVER - 1 = 172
         {   
            observer_name              = obs.substring(main.IMMT_5_POSITION_OBSERVER);    // main.IMMT_5_POSITION_OBSERVER = 173  // e.g. Janssen;P;4 off;-;
            
            if (!observer_name.equals(""))
            {
               int pos_1  = observer_name.indexOf(';');
               int pos_2  = observer_name.indexOf(';', pos_1 +1);
               
               if (pos_1 != -1 && pos_2 != -1)
               {   
                  observer_name = observer_name.substring(0, pos_1) + " " + observer_name.substring(pos_1 +1, pos_2);                      // e.g. Janssen P
               }   
            } // if (!observer_name.equals(""))
            
            for (int i = 0; i < Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES; i++)
            {
               if (observers_names_array[i].equals(observer_name) && (!observer_name.equals("")) )
               {
                  observers_count_array[i]++;
                  observer_name_found = true;
                  break;
               }
            } // for (int i = 0; i < MAX_NUMBER_OBSERVERS_BARS; i++)  

            if (observer_name_found == false)
            {
               for (int i = 0; i < Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES; i++)
               {
                  if (observers_names_array[i].equals("") && (!observer_name.equals("")))
                  {
                     observers_names_array[i] = observer_name;
                     observers_count_array[i]++;
                     break;
                  }      
               } // for (int i = 0; i < MAX_NUMBER_OBSERVERS_BARS; i++)
            } // if (observer_name_found == false)

         } // if (obs.length() > main.IMMT_5_POSITION_OBSERVER - 1)
      } // for (String obs : immt_list)
      
      
      // count number of different observer names
      for (int i = 0; i < Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES; i++)
      {
         if (observers_names_array[i].equals("") == false)
         {
            different_observer_names++;
         }   
      } // for (int i = 0; i < MAX_NUMBER_OBSERVERS_BARS; i++)
      
      

      // e.g. 6 different observer names -> 7 (6+1) intermediate spaces between the bars -> 13 'blocks'(6 bars and 7 spaces)
      //
      //
      //
      // | ------------------------------------------------------------------------------|   // x-as length
      // | <--->,<--->,<--->,<--->,<--->,<--->,<--->,<--->,<--->,<--->,<--->,<--->,<---> |   // 13 blocks
      // | space  bar  space  bar  space  bar  space  bar  space  bar  space  bar  space |   // 6 bars + 7 spaces = 13 blocks 
      //
      //
      //

      int number_blocks = different_observer_names + (different_observer_names + 1);
      double x_as_length = right_below_graph.getX() - left_below_graph.getX();
      double bar_width = x_as_length / number_blocks;
      
      // NB
      // block (space/bar)    [i] (of observers[i])     diff. (pass)
      // 1                    0                         1
      // 3                    1                         2
      // 5                    2                         3
      // 7                    3                         4
      // 9                    4                         5
      // etc.
      //
      
      int pass = 0;
      for (int i = 0; i < number_blocks; i++)
      {
         int x_bar = (int) (bar_width * i);
         int y_bar = (int) left_below_graph.getY();
         
         
         // NB even block: space
         //    odd block: bar
         //
         if ((i % 2) != 0) // odd (bar with number of observations)
         {
            pass++;
            int number_obs = observers_count_array[i - pass];
            int number_obs_display = number_obs;       // to display the real number on top of the bar (and not the rounded to 100 number if real obs number > 100)
            
            // the bars indicating the number of observations is limited to max_obs (e.g. 100)
            if (number_obs > max_obs)
            {
               number_obs = max_obs;
            }
            
            int bar_height = (int) (scaling / number_units_between_2_markers * number_obs);
         
            // draw the bars
            if (Obs_Stats_view.night_vision)
            { 
               g2d.setColor(Obs_Stats_view.BAR_OBSERVERS_NIGHT_COLOR);
            }
            else
            {
               g2d.setColor(Obs_Stats_view.BAR_OBSERVERS_DAY_COLOR);
            }
            g2d.fillRect((int)left_below_graph.getX() + x_bar, y_bar - bar_height, (int) bar_width, bar_height);
            
            // number of obs on top of the bar
            int x_string_number_obs = (int) (left_below_graph.getX() + x_bar);
            int y_string_number_obs = y_bar - bar_height; 
            
            g2d.setColor(java.awt.Color.BLACK);
            g2d.drawString(Integer.toString(number_obs_display), x_string_number_obs, y_string_number_obs);
            
            
            // name of the observer (below the bars or in the bars)
            int x_string = (int) (left_below_graph.getX() + x_bar + (bar_width / 2) - stringWidth_observer_name / 2);
            int y_string = (int) left_below_graph.getY();   
           
            g2d.setColor(java.awt.Color.BLACK);
            
            if (different_observer_names <= 2)
            {
               // set the observer name horizontal below the bar
               g2d.drawString(observers_names_array[i - pass], x_string, y_string + (int)(ascent_observer_name));
            }
            else
            {
               // set the observer name vertical in the bar
               x_string = (int) (left_below_graph.getX() + x_bar + (bar_width / 2) + (ascent_observer_name / 2));
               
               int angle = 270;
               g2d.translate(x_string, y_string);
               g2d.rotate(Math.toRadians(angle));
               g2d.drawString(observers_names_array[i - pass], 0, 0);
            
               g2d.rotate(-Math.toRadians(angle));                   // rotate back
               g2d.translate(-x_string, -y_string);                  // translate back
            }
         
         } // if ((i % 2) != 0)
      } // for (int i = 1; i < number_blocks; i+=2)  
            

      //
      /////////////// retrieve and display the log period of the IMMT log ////////////////
      //
      String requested_start_date = "";
      String requested_end_date = "";
      if (!Obs_Stats_view.immt_list.isEmpty())
      {   
         if (Obs_Stats_view.view_immt_log_period.equals(myimmtlogperiod.CUSTOM))
         {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");          // e.g. 21-Sep-2025
            requested_start_date = Obs_Stats_view.view_local_start_date.format(dateTimeFormatter);  
            requested_end_date   = Obs_Stats_view.view_local_end_date.format(dateTimeFormatter);  
         }
         else
         {
            requested_start_date = "";
            requested_end_date = "";
         }
         
         String log_line_1 = Obs_Stats_view.immt_rec_first;             // always the first line of the complete raw available immt log
         String log_line_last = Obs_Stats_view.immt_rec_last;           // always the last line of the complete raw available immt log 
         write_period_log_line(log_line_1, log_line_last, requested_start_date, requested_end_date);
      }
      else
      {
         Obs_Stats_view.jLabel1.setText("no records found matching the specified criteria"); 
      }
      
   } // private void draw_Obs_Bars(final Graphics g)
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void write_period_log_line(final String log_line_1, final String log_line_last, final String requested_start_date, final String requested_end_date)
   {
      String text_1 = "";
      String text_2 = "";
      
      //log line e.g. "displayed log period: 01-Jan-2022 / 29-Nov-2023 (available log period: 12-Sep-2023 / 10-Dec-2023)"
      //          or  "log period: 12-Sep-2023 / 10-Dec-2023"
      
      
      // custom requested log period
      //
      if ( (Obs_Stats_view.view_immt_log_period.equals(myimmtlogperiod.CUSTOM)) && (!requested_start_date.equals("")) && (!requested_end_date.equals("")) )
      {
         text_1 = "displayed log period: " + requested_start_date + " / " + requested_end_date;
      }   
      
      // first line in IMMT log
      //
      // see IMMT description
      // e.g. IMMT record: 32018051112152700620         0129                     0              44TESTNL US 314            23           A599999199999999991191                    9999 999     01234567 
      String year_1              = log_line_1.substring(1, 5);              
      String month_1             = log_line_1.substring(5, 7);              
      String day_1               = log_line_1.substring(7, 9);              
      //String hour_1              = log_line_1.substring(9, 11);     
      
      // do not show the preceding '0' in the log period line to be displayed
      if (day_1.length() == 2 && day_1.substring(0,1).equals("0"))
      {
         day_1 = day_1.substring(1,2);
      }
      
      // convert month (in numbers e.g. "01") to month_let (month in 3 characters e.g. "Jan")      
      String month_1_let = convert_month(month_1);
      // NB String strDate_1 = day_1 + "-" + month_1_let + "-" + year_1 + " " + hour_1 + ":00 UTC";   // from verion 4.6: commentent out, with the time the log line could be very long
      String strDate_1 = day_1 + "-" + month_1_let + "-" + year_1;
      
      // last line in IMMT log
      //
      // see IMMT description
      // e.g. IMMT record: 32018051112152700620         0129                     0              44TESTNL US 314            23           A599999199999999991191                    9999 999     01234567 
      String year_last            = log_line_last.substring(1, 5);              
      String month_last           = log_line_last.substring(5, 7);              
      String day_last             = log_line_last.substring(7, 9);              
      //String hour_last            = log_line_last.substring(9, 11);  
      
      // do not show the preceding '0' in the log period line to be displayed
      if (day_last.length() == 2 && day_last.substring(0,1).equals("0"))
      {
         day_last = day_last.substring(1,2);
      }
      
      // convert month (in numbers e.g. "01") to month_let (month in 3 characters e.g. "Jan")      
      String month_last_let = convert_month(month_last);
      // NB String strDate_last = day_last + "-" + month_last_let + "-" + year_last + " " + hour_last + ":00 UTC"; // from verion 4.6: comment out,with the time the log line could be very long
      String strDate_last = day_last + "-" + month_last_let + "-" + year_last;
      
      // display IMMT log period (start date time / end date time)
      //
      if (Obs_Stats_view.view_immt_log_period.equals(myimmtlogperiod.CUSTOM))
      {   
         text_2 = "available log period: " + strDate_1 + " / " + strDate_last;
      }
      else
      {
         text_2 = "log period: " + strDate_1 + " / " + strDate_last;
      }   
      
      if (text_1.equals(""))                   // so no custom log period requested
      {
         Obs_Stats_view.jLabel1.setText(text_2); 
      }  
      else
      {   
         Obs_Stats_view.jLabel1.setText(text_1 + " (" + text_2 + ")"); 
      }   
   }

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private String convert_month(final String month)
   {
      // convert month (from -String-numbers e.g. "01") to month_let (month in 3 characters e.g. "Jan") 
      
      String month_let = null;
      switch (month)
      {
         case "01": month_let = "Jan";
                    break;
         case "02": month_let = "Feb";
                    break;         
         case "03": month_let = "Mar";
                    break;           
         case "04": month_let = "Apr";
                    break;           
         case "05": month_let = "May";
                    break;           
         case "06": month_let = "Jun";
                    break;           
         case "07": month_let = "Jul";
                    break;
         case "08": month_let = "Aug";
                    break;         
         case "09": month_let = "Sep";
                    break;           
         case "10": month_let = "Oct";
                    break;           
         case "11": month_let = "Nov";
                    break;           
         case "12": month_let = "Dec";
                    break;             
      }         
      
      
      return month_let;
   }
   
}
