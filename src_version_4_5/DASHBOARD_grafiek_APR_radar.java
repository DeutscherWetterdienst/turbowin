package turbowin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.text.AttributedString;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
//import static turbowin.DASHBOARD_view_APR_radar.jLabel20;



/**
 *
 * @author marti
 */
public class DASHBOARD_grafiek_APR_radar extends JPanel 
{
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public DASHBOARD_grafiek_APR_radar()
   { 
      color_black                   = Color.BLACK;
      //color_last_update             = color_black; 
      //color_digits                  = color_black;
      color_dark_blue               = new Color(0, 0, 128);
      color_blue                    = Color.BLUE;
      color_gray                    = Color.GRAY;
      color_wind_rose_additional    = Color.YELLOW;  //Color.BLACK;             // BF circles + labels; NORTH,WEST,EAST,SOUTH chars
      color_wind_rose_background    = new Color(0, 191, 255, 255);              // light blue
      color_wind_rose_ring          = Color.DARK_GRAY;
      color_contour_block           = Color.BLACK;
      color_fill_block              = new Color(0, 191, 255);                   // default can be overridden
      block_contour_thickness       = 2.0f;
      color_true_wind_arrow_max     = new Color(224, 224, 224, 120); 
      color_rel_wind_arrow_max      = new Color(224, 224, 224, 120); 
      color_true_wind_arrow_actual  = Color.YELLOW;
      color_rel_wind_arrow_actual   = new Color(255, 110, 0);                   // slightly brighter than Color.ORANGE;
      
      // get the screen size 
      //
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      double width_screen = screenSize.getWidth();
      double height_screen = screenSize.getHeight();
      System.out.println("--- Screen resolution APR Dashboard wind radar: " + width_screen + " x " + height_screen);   
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void setAllRenderingHints(Graphics2D g2d) 
   {
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 100);
      g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
   }


   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   @Override
   public void paintComponent(Graphics g) 
   {
      // eg: https://www.google.nl/search?q=barometer&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjJ78PZg8_UAhWEJMAKHZA3AsAQ_AUICigB&biw=1920&bih=950#imgrc=hE2asDauQrhr6M:
   
      // NB calculating instrument_width can't be done in DASHBOARD_grafiek_APR_radar() because from here (paintComponents) it is only known
      //    see https://stackoverflow.com/questions/12010587/how-to-get-real-jpanel-size
  
      boolean draw_info_blocks = false;
      //double x1_block          = 0;
      //double y1_block          = 0;
      double x5_block          = 0;
      double y5_block          = 0;
      double block_width       = 0;
      double block_height      = 0;
      double wind_rose_radius  = 0;
      
      
      // always call the super's method to clean "dirty" pixels
      super.paintComponent(g);
   
      // general
      final Graphics2D g2d = (Graphics2D) g;
      setAllRenderingHints(g2d);
  
      // set new origin to middle screen (especially for backround image option, origin will be updated afterwards)
      g2d.translate(getWidth() / 2, getHeight() / 2);                                       // centre of center panel (jPanel 1) 
      
      int fontSize_a = 8;                                                                   // units (eg 1100) wind rose
      int fontSize_c = 12;                                                                  // Bf labels
      int fontSize_d = 18;
      //int fontSize_h = 14;                                                                  // Bf descriptions
      int fontSize_e = DASHBOARD_view_APR_radar.width_APR_radar_dashboard / 70; 
      int dist_radar_blocks = DASHBOARD_view_APR_radar.width_APR_radar_dashboard / 70;      // distance between the info blocks and the radar screen
   
   
      font_a = new Font("SansSerif", Font.PLAIN, fontSize_a);                              // units (eg 1100)
      font_b = new Font("Monospaced", Font.BOLD, fontSize_d);                              // N, E, S, W
      font_c = new Font("SansSerif", Font.PLAIN, fontSize_c);                              // units (eg 1100)
      font_g = new Font("Monospaced", Font.BOLD, (int)(fontSize_e / 1.2));                 // labels and inserted data of the measured parameters
      //font_h = new Font("Monospaced", Font.PLAIN, fontSize_c);                             // Bf description (right info block)
      //font_h = new Font("Monospaced", Font.BOLD, fontSize_h);                              // Bf description (right info block)
      font_k = new Font("Monospaced", Font.BOLD, (int)(fontSize_e / 1.5));                // label "satellite image (internet)"
   
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // background image(not in night vision mode)
      //
   
      if ( (DASHBOARD_view_APR_radar.night_vision == false) && (main.dashboard_background_image.compareTo("") != 0) )
      {
         try
         {
            //Image img1 = new ImageIcon("C:/Users/marti/OneDrive/Pictures/Saved Pictures/taagborg_1.jpg").getImage();
            Image img_background = new ImageIcon(main.dashboard_background_image).getImage();
            g2d.drawImage(img_background, -(getWidth() / 2), -(getWidth() / 2), (getWidth() / 2), (getHeight() / 2), 0, 0, img_background.getWidth(null), img_background.getHeight(null), null);
         
            // NB "main.dashboard_background_image": saved in e.g. Function choose_APR dashboard_image() [DASHBOARD_view_APR_radar.java]
         }
         catch (Exception e)
         {
            String info = "error when retrieving selected background image (" + e + ")" ;
            JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);        
            main.dashboard_background_image = "";  
            
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
      } // if (DASHBOARD_view_APR.night_vision == false) etc.
   
      
      // update graphics origin
      //
      g2d.translate(-getWidth() / 2, -getHeight() / 2);                                                                                // reset
      g2d.translate(DASHBOARD_view_APR_radar.width_APR_radar_dashboard / 2, DASHBOARD_view_APR_radar.height_APR_radar_dashboard / 2);  // new origin (NB see DASHBOARD_APR_view_radar_componentResizedHandler() [DASHBOARD_view_APR_radar.java])
      
      
      // colors depending night or day vision
      //
      if (DASHBOARD_view_APR_radar.night_vision == true)
      {
         color_fill_block                 = Color.RED;
         //color_digits                     = color_black;
         //color_last_update                = color_black;                    // " measured_at" in case hybrid / Meteo France dashboard
         color_update_message_south_panel = color_gray;
      }
      else // day vision
      {
         if (main.dashboard_background_image.compareTo("") != 0)            // background image present
         {
            color_fill_block              = new Color(0, 191, 255, 128);    // 50% opacity;
         }
         else                                                               // no background image present
         {
            color_fill_block              = new Color(0, 191, 255);         // 100% opacity
         }
         
         //color_digits                     = color_black;
         //color_last_update                = color_black; 
         color_update_message_south_panel = color_black;
      }
   
      // obsolete data (no cummunication for roughly > 2 minutes with the APR sensor(s))
   //   ,,,,,
   //   if (main.displayed_aws_data_obsolate == true)                        // valid for analog and digital dashboard
   //   {
   //      color_digits = main.obsolate_color_data_from_aws;                 // inserted data e.g. 12.3 C
   //      color_last_update = main.obsolate_color_data_from_aws;
   //      
   //      main_RS232_RS422.RS422_initialise_AWS_Sensor_Data_For_Display();  // NB see also: main_RS232_RS422.MAX_AGE_AWS_DATA
   //   }
   
/*      
      // GPS data obsolete
      if (main.obsolate_GPS_data_flag == true)
      {
         // e.g. see function position_fields_update() [main.java]
         color_digits_GPS = main.obsolete_input_color_from_apr;                 
      }
      else
      {
         color_digits_GPS = color_black;
      }
      
      // air pressure data obsolete
      if (main.obsolate_data_flag == true)
      {
         //color_digits_air_pressure = main.obsolete_input_color_from_apr;                 
         //color_last_update = main.obsolate_color_data_from_aws;
      }
      
      // temperature data obsolete
      if ( (main.obsolate_data_flag_II == true) || ((main.obsolate_data_flag == true) && (main.RS232_connection_mode == 7 || main.RS232_connection_mode == 8)) ) // NB 7/8 : Mintaka StarX (temp) linked
      {
      //   jTextField36.setForeground(main.obsolete_input_color_from_apr);   
      //   RS232_initialise_APR_Sensor_Data_For_Display();  // NB see also: main_RS232_RS422.MAX_AGE_AWS_DATA
         //color_digits_themperature = main.obsolete_input_color_from_apr;                 // inserted data e.g. 12.3 C
      }
*/
   
      //////// measured at: ///////
      //
      String measured_at_line = "";
      String MEASURED_AT = "Measured  :";

      measured_at_line = MEASURED_AT + " -";
      
      if (main_RS232_RS422.test_record.length() > 8 )
      {
         //measured_at_line = main_RS232_RS422.test_record;
         measured_at_line = main_RS232_RS422.test_record.substring(0, main_RS232_RS422.test_record.length() -7) + " UTC"; // remove the seconds (e.g. "10:12:30 UTC" -> "10:12 UTC")
      }
      
      
      
      //// COG //////
      //
      
      String COG_10_min_line = "COG-10_min: -";         // only in case of format #101
      String COG_3_hrs_line  = "COG-3_hrs : -";         // only in case of FM13
      String COG_wind_line   = "COG-10_min: -";         // only in case of FM13


      if (main.obsolate_GPS_data_flag == false)
      {
         if (main.obs_format.equals(main.FORMAT_101)) 
         {
            if (myposition.COG_APR >= 0.0 && myposition.COG_APR <= 360.0)
            {
               COG_10_min_line = "COG-10_min: " + myposition.COG_APR + "\u00B0";
            }
         }
         else if (main.obs_format.equals(main.FORMAT_FM13))
         {
            if (myposition.COG_APR >= 0.0 && myposition.COG_APR <= 360.0)
            {
               COG_3_hrs_line = "COG-3_hrs : " + myposition.COG_APR + "\u00B0";
            }
            
            if (myposition.COG_APR_wind >= 0.0 && myposition.COG_APR_wind <= 360.0)
            {
               COG_wind_line  = "COG-10_min: " + myposition.COG_APR_wind + "\u00B0";
            }
         }
      }  //  if (main.obsolate_GPS_data_flag == false) 


      String SOG_10_min_line = "SOG-10_min: -";         // only in case of format #101
      String SOG_3_hrs_line  = "SOG-3_hrs : -";         // only in case of FM13
      String SOG_wind_line   = "SOG-10_min: -";         // only in case of FM13


      if (main.obsolate_GPS_data_flag == false)
      {
         if (main.obs_format.equals(main.FORMAT_101)) 
         {
            if (myposition.SOG_APR >= 0.0 && myposition.SOG_APR <= 50.0)
            {
               SOG_10_min_line = "SOG-10_min: " + myposition.SOG_APR + " kts";
            }
         }
         else if (main.obs_format.equals(main.FORMAT_FM13))
         {
            if (myposition.SOG_APR >= 0.0 && myposition.SOG_APR <= 50.0)
            {
               SOG_3_hrs_line = "SOG-3_hrs : " + myposition.SOG_APR + " kts";
            }
            
            if (myposition.SOG_APR_wind >= 0.0 && myposition.SOG_APR_wind <= 50.0)
            {
               SOG_wind_line  = "SOG-10_min: " + myposition.SOG_APR_wind + " kts";
            }
         }
      } // if (main.obsolate_GPS_data_flag == false)

      
      
      //////// Lat /////
      //
      String lat_line = "Lat       : -";
      
      if (main.obsolate_GPS_data_flag == false)
      {
         if (myposition.latitude_degrees.compareTo("") != 0 && myposition.latitude_minutes.compareTo("") != 0 && myposition.latitude_hemisphere.compareTo("") != 0 &&
             myposition.latitude_degrees != null && myposition.latitude_minutes != null && myposition.latitude_hemisphere != null)
         
         {
            lat_line = "Lat       : " + myposition.latitude_degrees + "\u00B0" + myposition.latitude_minutes + "'" + myposition.latitude_hemisphere.substring(0, 0 + 1);
         }
      }
      
      
      
      //////// Lon /////
      //
      String lon_line = "Lon       : -";
      
      if (main.obsolate_GPS_data_flag == false)
      {
         if (myposition.longitude_degrees.compareTo("") != 0 && myposition.longitude_minutes.compareTo("") != 0 && myposition.longitude_hemisphere.compareTo("") != 0 &&
             myposition.longitude_degrees != null && myposition.longitude_minutes != null && myposition.longitude_hemisphere != null)
         {
            lon_line = "Lon       : " +  myposition.longitude_degrees + "\u00B0" + myposition.longitude_minutes + "'" + myposition.longitude_hemisphere.substring(0, 0 + 1);
         }
      }
      
      
      
      ////////// MSLP //////
      //
      // NB based on barometer_fields_update() [main.java]
      //
      String mslp_line = "MSLP      : -";
      
      if (main.obsolate_data_flag == false)         // air pressure data flag
      {
         if ( (mybarometer.pressure_msl_corrected.compareTo("") != 0) && (mybarometer.pressure_msl_corrected != null) )
         {
            mslp_line = "MSLP      : " + mybarometer.pressure_msl_corrected + " hPa";
         }
      } 
      //System.out.println("+++ mybarometer.pressure_msl_corrected = " + mybarometer.pressure_msl_corrected);

      
      //////// air pressure tendency ////////
      //
      // NB based on: barograph_fields_update() [main.java]
      //
      String tendency_line = "Tendency  : -";
      
      if (main.obsolate_data_flag == false)         // air pressure data flag
      {
         if ((mybarograph.pressure_amount_tendency.compareTo("") != 0) && (mybarograph.pressure_amount_tendency != null))
         {
            tendency_line =  "Tendency  : " + mybarograph.pressure_amount_tendency + " hPa / 3 hrs";  
         }   
      }
      
      
      //////// air pressure tendency charactristic ////////
      //
      // NB based on: barograph_fields_update() [main.java]
      //
      String tendency_char_line = "Tend. char: -";
      
      if (main.obsolate_data_flag == false)         // air pressure data flag
      {
         if ((mybarograph.a_code.compareTo("") != 0) && (mybarograph.a_code != null))
         {
            tendency_char_line =  "Tend. char: " + mybarograph.a_code + " (code)"; 
         } 
      }
      
      
      //////// air pressire at sensor height ('read') /////
      //
      // NB based on barometer_fields_update() [main.java] 
      //
      String pressure_height_line = "Press read: -";
      
      if (main.obsolate_data_flag == false)         // air pressure data flag
      {
         if ( (mybarometer.pressure_reading_corrected.compareTo("") != 0) && (mybarometer.pressure_reading_corrected != null) )
         {
            pressure_height_line =  "Press read: " + mybarometer.pressure_reading_corrected + " hPa"; 
         }
      }
      
      
      ///////// air temp ///////
      //
      // NB based on: temperatures_fields_update() [main.java]
      //
      String air_temp_line = "Air temp  : -";
      
      if ( (main.obsolate_data_flag_II == false) || ((main.obsolate_data_flag == false) && (main.RS232_connection_mode == 7 || main.RS232_connection_mode == 8)) ) // NB 7/8 : Mintaka StarX (temp) linked  // temp data flag
      {
         if ((mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null))    
         {
            // het kan zijn dat er bv alleen staat 25 dit moet dan worden 25.0 C 
            int pos = mytemp.air_temp.indexOf(".");
            if (pos == -1)                                                        // dus geen "." in de air temp string
            {
               air_temp_line = "Air temp  : " + mytemp.air_temp + ".0 \u00B0C";
            }    
            else
            { 
               air_temp_line = "Air temp  : " + mytemp.air_temp + " \u00B0C";
            }   
         }  
      }
      
      
      /////// relative humidity (RH) /////////
      //
      // NB based on: temperatures_fields_update() [main.java]
      //
      String rh_line = "RH        : -";
      
      if ( (main.obsolate_data_flag_II == false) || ((main.obsolate_data_flag == false) && (main.RS232_connection_mode == 7 || main.RS232_connection_mode == 8)) ) // NB 7/8 : Mintaka StarX (temp) linked
      {
         if ((mytemp.double_rv != main.INVALID))
         {
            // (first a trick for relative humidity rounding to 1 figure behind the decimal)
            double hulp_double_rv = Math.round(mytemp.double_rv * 10 * 100) / 10.0; // nb Math.round(xxx) - > geeft long terug  // NB * 100 for getting %
            rh_line = "RH        : " + Double.toString(hulp_double_rv) + " %";
         }
      }
      
      
      
      
      //////////  next APR upload ////////
      //
      String next_APR_line = "Next APR  : -";
                
      cal_APR_system_date_time_db = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));   // system date time in UTC of this moment
      cal_APR_system_date_time_db.getTime();                                               // now effective
      int APR_system_hour_local_db = cal_APR_system_date_time_db.get(Calendar.HOUR_OF_DAY);   // HOUR_OF_DAY is used for the 24-hour clock. E.g., at 10:04:15.250 PM the HOUR_OF_DAY is 22.
      int APR_system_minute_local_db = cal_APR_system_date_time_db.get(Calendar.MINUTE);
      
      // text with next APR update (updated every minute)
      for (int i = (APR_system_hour_local_db + 1); i < (APR_system_hour_local_db + 1 + Integer.valueOf(main.APR_reporting_interval)); i++)
      {
         // 24 hours or higher makes no difference for this computation
         if (i % Integer.valueOf(main.APR_reporting_interval) == 0)
         {
            cal_APR_system_date_time_db.add(Calendar.HOUR_OF_DAY, i - APR_system_hour_local_db);   // add 1 - 5 hours
            cal_APR_system_date_time_db.getTime();
                                 
            //String next_APR_date_time = cal_APR_system_date_time_db.get(Calendar.DAY_OF_MONTH) + " " + main.convert_month(cal_APR_system_date_time_db.get(Calendar.MONTH)) + " " + cal_APR_system_date_time_db.get(Calendar.YEAR) + " " + cal_APR_system_date_time_db.get(Calendar.HOUR_OF_DAY) + ".00 UTC"; 
            String next_APR_date_time = cal_APR_system_date_time_db.get(Calendar.HOUR_OF_DAY) + ":00 UTC"; 
            
            next_APR_line = "Next APR  : " + next_APR_date_time;
            break;
         }
      } // for (int i = (APR_system_hour_local_db + 1); etc.
      
      

      //////////  visual obs message based on VOT ////////
      //
      String visual_obs_line = "Visual obs: -";

      //cal_APR_system_dt = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));   // system date time in UTC of this moment
      //cal_APR_system_dt.getTime();                                               // now effective
      //int APR_system_hour = cal_APR_system_dt.get(Calendar.HOUR_OF_DAY);   // HOUR_OF_DAY is used for the 24-hour clock. E.g., at 10:04:15.250 PM the HOUR_OF_DAY is 22.
      //int APR_system_minutes = cal_APR_system_dt.get(Calendar.MINUTE);
            
      if ( ((APR_system_hour_local_db + 1) % Integer.valueOf(main.APR_reporting_interval)) == 0)    
      {
                  //System.out.println("+++ TEST GEPASSEERD 2"); 
                  // NB NB currentTimeMillis(): returns the difference,in milliseconds, between the current system time and midnight, January 1, 1970 UTC
                  // NB cal_APR_system_date_time.computeTime(): Converts calendar field values to the time value (millisecond offset from the Epoch).( Epoch, January 1, 1970 00:00:00.000 GMT (Gregorian))
           
                  //System.out.println("+++ System.currentTimeMillis() = " + System.currentTimeMillis());
                  //System.out.println("+++ cal_APR_system_dt.getTimeInMillis() = " + cal_APR_system_dt.getTimeInMillis());
                  //System.out.println("+++  APR_system_minutes = " +  APR_system_minutes);
               
                  //long APR_minutes_for_upload = (System.currentTimeMillis() - cal_APR_system_dt.getTimeInMillis()) / (1000 * 60);
         int APR_minutes_for_upload = 60 -  APR_system_minute_local_db;
         if (APR_minutes_for_upload <= 30)
         {
            // less the 30 minutes (= 1800000 msec) before next APR upload
            // NB "-1" too be sure, thread is always behind
                     //main.jLabel39.setForeground(new Color(0,153,0));          // dark green
                     //main.jLabel39.setText("--- " + (APR_minutes_for_upload -1) + " minutes to go before next automated upload, you can now add observation data ---");
            visual_obs_line = "Visual obs: " + "now possible";  
            //DASHBOARD_view_APR_radar.jButton1.setEnabled(true);
            DASHBOARD_view_APR_radar.enable_disable_wind_controls_APR_radar(true);
            DASHBOARD_view_APR_radar.enable_disable_manual_input_controls_APR_radar(true);  // waves and SST
         }
         else
         {
                     //main.jLabel39.setForeground(Color.RED);
                     //main.jLabel39.setText("--- more than 30 minutes to go before next automated upload, please do not add observation data --- ");
            visual_obs_line = "Visual obs: " + "not yet";  
            //DASHBOARD_view_APR_radar.jButton1.setEnabled(false);
            DASHBOARD_view_APR_radar.enable_disable_wind_controls_APR_radar(false);
            DASHBOARD_view_APR_radar.enable_disable_manual_input_controls_APR_radar(false);  // waves and SST
         }
      } // if ( ((APR_system_hour + 1) % Integer.valueOf(main.APR_reporting_interval)) == 0)
      else
      {
                  //main.jLabel39.setForeground(Color.RED);
                  //main.jLabel39.setText("--- more than 30 minutes to go before next automated upload, please do not add observation data --- ");
         visual_obs_line = "Visual obs: " + "not yet";
         //DASHBOARD_view_APR_radar.jButton1.setEnabled(false);
         DASHBOARD_view_APR_radar.enable_disable_wind_controls_APR_radar(false);
         DASHBOARD_view_APR_radar.enable_disable_manual_input_controls_APR_radar(false);  // waves and SST
      } // else     
      
      
      
      
      //////////// wind rose /////////
      //
      double wind_rose_diameter = 0.0;
      if (DASHBOARD_view_APR_radar.width_APR_radar_dashboard < DASHBOARD_view_APR_radar.height_APR_radar_dashboard)
      {
         wind_rose_diameter = DASHBOARD_view_APR_radar.width_APR_radar_dashboard / 1.1;   // nb width from JPanel1 (center panel)
      }
      else
      {
         wind_rose_diameter = DASHBOARD_view_APR_radar.height_APR_radar_dashboard / 1.1;  // nb height from JPanel1 (center panel)
      }
   
      // for wind rose and wind arrow
      double marker_circle_diameter_1   = wind_rose_diameter - 26;                  // eg outside marker circle: 220 - 26 = 194; [most 'outside' outside marker circle]
      double marker_circle_diameter_2   = marker_circle_diameter_1 - 10;            // eg inside marker circle: 194 - 10 = 184; [most 'inside' outside marker circle]
      double bf_per_class_radius = (marker_circle_diameter_2 / 2) / 12;
   
      // wind rose itself
      draw_wind_rose_radar(g2d, wind_rose_diameter, marker_circle_diameter_1, marker_circle_diameter_2, bf_per_class_radius, DASHBOARD_view_APR_radar.beaufort_class_APR);      
   
      
      /////// ship in wind rose //////
      //
      int reading_int_course = Integer.MAX_VALUE;
      boolean course_ok = false;
      // NB myposition.COG_APR was initiated as Double.MAX_VALUE
      //    so if an value between 0.0 and 360,0 there was a COG computed
   
      //if (myposition.COG_APR >= 0.0 && myposition.COG_APR <= 360.0)
      if (myposition.COG_APR_wind >= 0.0 && myposition.COG_APR_wind <= 360.0)  // alwayes based on 10 min. (FM13 en format #101) 
      {
         //reading_int_course = (int)Math.round(myposition.COG_APR);
         reading_int_course = (int)Math.round(myposition.COG_APR_wind);
         
         if (reading_int_course == 0)
         {
            reading_int_course = 360;
         }
         course_ok = true;
      }
      
      
////////////////////////////////////////////////// BEGIN TESTING //////////////////////////////
//        reading_int_course = 33;
//        course_ok = true;
//        main.ship_type_dashboard = main.TALL_SHIP;
//      //main.ship_type_dashboard = main.NEUTRAL_SHIP;
//      //main.ship_type_dashboard = main.PASSENGER_SHIP;
//      //main.ship_type_dashboard = main.OIL_TANKER;
//      //main.ship_type_dashboard = main.CONTAINER_SHIP;
//      //main.ship_type_dashboard = main.LNG_TANKER;
//      //main.ship_type_dashboard = main.RESEARCH_VESSEL;
//      //main.ship_type_dashboard = main.RO_RO_SHIP;
//      //main.ship_type_dashboard = main.FERRY;
//      //main.ship_type_dashboard = main.LNG_TANKER_II;
////////////////////////////////////////////////// END TESTING ////////////////////////////////////////
 
 
  
      // if not yet done create object ship
      if (main.myship == null)
      {                                
         main.myship = new ship();
      }
   
      // if no (or not yet) ship type defined start with a container ship
      if (main.ship_type_dashboard.equals(""))
      {
         main.ship_type_dashboard = main.CONTAINER_SHIP;
      }
      
      // ship deck color if defined and stored before
      if (main.ship_deck_color_String.equals("") == false)
      {
         try
         {
            DASHBOARD_view_APR_radar.deck_color = new Color(Integer.parseInt(main.ship_deck_color_String), true);   // String to Color
         }
         catch (NumberFormatException e)
         {
            DASHBOARD_view_APR_radar.deck_color = null;        
         }
      }
      
      // ship (LNG)tank color if defined and stored before
      if (main.ship_tank_color_String.equals("") == false)
      {
         try
         {
            DASHBOARD_view_APR_radar.tank_color = new Color(Integer.parseInt(main.ship_tank_color_String), true);   // String to Color
         }
         catch (NumberFormatException e)
         {
            DASHBOARD_view_APR_radar.tank_color = null;        
         }
      }
   
      if (course_ok)
      {
         // set new drawing orientation (rotated to heading or COG)
         if (reading_int_course >= 1 && reading_int_course <= 360) 
         {
            g2d.rotate(Math.toRadians(reading_int_course));  // what follows will be rotated xxx(reading_int_course) degrees
         }   
      
         switch (main.ship_type_dashboard)
         {
            case main.TALL_SHIP             : main.myship.draw_tall_ship(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            case main.GENERAL_CARGO_SHIP    : main.myship.draw_general_cargo_ship(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color); break;
            case main.CONTAINER_SHIP        : main.myship.draw_container_ship(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color); break;
            case main.BULK_CARRIER          : main.myship.draw_bulk_carrier(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color); break;
            case main.OIL_TANKER            : main.myship.draw_oil_tanker(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color); break;
            case main.LNG_TANKER            : main.myship.draw_lng_tanker(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color, DASHBOARD_view_APR_radar.tank_color); break;
            case main.PASSENGER_SHIP        : main.myship.draw_passenger_ship(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            case main.RESEARCH_VESSEL       : main.myship.draw_research_vessel(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            case main.NEUTRAL_SHIP          : main.myship.draw_neutral_ship(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            case main.RO_RO_SHIP            : main.myship.draw_ro_ro_ship(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            case main.FERRY                 : main.myship.draw_ferry(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            case main.CONTAINER_SHIP_2      : main.myship.draw_container_ship_II(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color); break;
            case main.GENERAL_CARGO_CLASSIC : main.myship.draw_general_cargo_classic(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            case main.REEFER_SHIP           : main.myship.draw_reefer_ship(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            case main.GENERAL_CARGO_SHIP_2  : main.myship.draw_general_cargo_ship_II(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color); break;
            case main.LNG_TANKER_II         : main.myship.draw_lng_tanker_II(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color); break;
            case main.FRUIT_JUICE_TANKER    : main.myship.draw_fruit_juice_tanker(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision); break;
            default                         : main.myship.draw_container_ship(g2d, wind_rose_diameter, DASHBOARD_view_APR_radar.night_vision, DASHBOARD_view_APR_radar.deck_color); break;
         } // switch (main.ship_type_dashboard)
      
         // reset drawing orientation
         if (reading_int_course >= 1 && reading_int_course <= 360)    
         {
            g2d.rotate(Math.toRadians(-reading_int_course));  // what follows will be rotated -xxx degrees
         }
      
      } // if (course_ok)
      
      
      
      // declaration + initialisation for wind arrow's
      String wind_type               = "";
      boolean wind_dir_present;
      boolean wind_force_present;
      int int_wind_dir;
      int int_wind_force; 
      
      
      // NB most of the time if relative and true direction are almost the same (on the bow) then relative speed will be higher then true speed
      //   therfore first drawing the relative arrow and then the true arrow. Furtheremore the relative wind arrow is a little bit widther than the true wind arrow
      //
      //
      
      // compute the relative wind
      compute_relative_wind_APR();
      
      ////// relative wind arrows (before true wind arrow!) /////
      //
      wind_type                  = RELATIVE_WIND;
      wind_dir_present           = false;
      wind_force_present         = false;
      int_wind_dir               = 0;
      int_wind_force             = 0; 
      
      
      // check relative wind dir
      //
      if (DASHBOARD_view_APR_radar.relative_wind_dir_APR.equals(""))         // "" is default start value
      {
         wind_dir_present = false;        
      }
      else if ( DASHBOARD_view_APR_radar.relative_wind_dir_APR.contains("calm") || DASHBOARD_view_APR_radar.relative_wind_dir_APR.contains("variable") )
      {
         wind_dir_present = false;            
      }    
      else 
      {
         try
         {
            int_wind_dir = Integer.parseInt(DASHBOARD_view_APR_radar.relative_wind_dir_APR.trim());
            wind_dir_present = true;
         }
         catch (NumberFormatException nfe)
         {
            wind_dir_present = false;    
         }
      }

      // check relative wind force
      //
      if (DASHBOARD_view_APR_radar.relative_beaufort_class_APR <= 12)
      {
         int_wind_force = DASHBOARD_view_APR_radar.relative_beaufort_class_APR;
         wind_force_present = true; 
      }
      else
      {
         int_wind_force = 0;
         wind_force_present = false; 
      }
      
      draw_wind_arrow_radar_APR(g2d, marker_circle_diameter_2, bf_per_class_radius, wind_type, wind_dir_present, wind_force_present, int_wind_dir, int_wind_force);
   
      
      ////// True wind arrows (after relative wind arrow!) /////
      //
      wind_type                 = TRUE_WIND;
      wind_dir_present           = false;
      wind_force_present         = false;
      int_wind_dir               = 0;
      int_wind_force             = 0; 

      
      // check true wind dir
      //
      if (DASHBOARD_view_APR_radar.wind_dir_APR.equals(""))         // "" is default start value
      {
         wind_dir_present = false;        
      }
      else if ( DASHBOARD_view_APR_radar.wind_dir_APR.contains("calm") || DASHBOARD_view_APR_radar.wind_dir_APR.contains("variable") )
      {
         wind_dir_present = false;            
      }    
      else 
      {
         try
         {
            int_wind_dir = Integer.parseInt(DASHBOARD_view_APR_radar.wind_dir_APR.trim());
            wind_dir_present = true;
         }
         catch (NumberFormatException nfe)
         {
            wind_dir_present = false;    
         }
      }
      
      
      // check true wind force
      //
      if (DASHBOARD_view_APR_radar.beaufort_class_APR <= 12)
      {
         int_wind_force = DASHBOARD_view_APR_radar.beaufort_class_APR;
         wind_force_present = true; 
      }
      else
      {
         int_wind_force = 0;
         wind_force_present = false; 
      }
      
      draw_wind_arrow_radar_APR(g2d, marker_circle_diameter_2, bf_per_class_radius, wind_type, wind_dir_present, wind_force_present, int_wind_dir, int_wind_force);
   
      
      
      
      
      
      
   
      //////////////// left block - radar circle - right block ///////////
      //
      //
      //
      //             (x1,y1)           (x2,y2                                   (x5,y5)           (x6,y6)
      //                    ------------                                               ------------
      //                   |           |                                               |          |
      //                   |           |                                               |          |
      //                   |           |                                               |          |
      //                   |           |                  radar circle                 |          |
      //                   |           |                                               |          |
      //                   |           |                                               |          |
      //                   |           |                                               |          |
      //                    -----------                                                -----------|
      //             (x3,y3)           (x4,y4)                                   (x7,y7)           (x8,y8)
      //
      //
      //System.out.println("+++ wind_rose_diameter * 2 = " + wind_rose_diameter * 2);
      //System.out.println("+++ DASHBOARD_view_AWS_radar.width_AWS_radar_dashboard = " + DASHBOARD_view_AWS_radar.width_AWS_radar_dashboard);
      draw_info_blocks = ((wind_rose_diameter * 2) + (dist_radar_blocks * 2)) < DASHBOARD_view_APR_radar.width_APR_radar_dashboard; // all of the center drawing panel (JPanel1)
      //draw_info_blocks = ((wind_rose_diameter * 2) + 0) < DASHBOARD_view_APR_radar.width_APR_radar_dashboard; // all of the center drawing panel (JPanel1)
        
      if (draw_info_blocks)   
      {
         wind_rose_radius = wind_rose_diameter / 2;
         DASHBOARD_view_APR_radar.x1_block = -wind_rose_radius - (wind_rose_radius) - dist_radar_blocks;
         DASHBOARD_view_APR_radar.y1_block = -wind_rose_radius;
         
         //x5_block = wind_rose_radius + dist_radar_blocks;
         //y5_block = -wind_rose_radius;
         
         block_width  = wind_rose_radius;        //Math.abs(x1_block) - Math.abs(x2_block);
         block_height = wind_rose_diameter;      // origin lies between y7 and y5
         
         // fill left and right block
         ////
         //Color test_color = new Color(0, 191, 255, 128);    // 50% opacity
         
         g2d.setPaint(color_fill_block);
         //g2d.setPaint(test_color);
         ////////////
         
         g2d.fill(new RoundRectangle2D.Double(DASHBOARD_view_APR_radar.x1_block, DASHBOARD_view_APR_radar.y1_block, block_width, block_height, 20, 20));
         //g2d.fill(new RoundRectangle2D.Double(x5_block, y5_block, block_width, block_height, 20, 20));
         
         // contour left and right block
         g2d.setPaint(color_contour_block);
         g2d.setStroke(new BasicStroke(block_contour_thickness));
         g2d.draw(new RoundRectangle2D.Double(DASHBOARD_view_APR_radar.x1_block, DASHBOARD_view_APR_radar.y1_block, block_width, block_height, 20, 20));
         //g2d.draw(new RoundRectangle2D.Double(x5_block, y5_block, block_width, block_height, 20, 20));
      } // if (draw_info_blocks) 
      
     
    
      if (draw_info_blocks)
      {
         //g2d.setFont(font_f);
         g2d.setFont(font_g);
         g2d.setPaint(color_black);
         
         int height_letter = g2d.getFontMetrics().stringWidth("M");
         int x_line = 0;
         int y_line = 0;
         
         
         ////// left: --- AWS DATA --- //////
         //
         g2d.setPaint(color_black);
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 2;
         g2d.drawString("--- SENSOR DATA ---", x_line, y_line); 
         
         ////// left: measured: //////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 8;
         g2d.drawString(measured_at_line, x_line, y_line); 
    
         
         //////// left: Heading /////
         //
         //x_line = (int)x1_block + height_letter;
         //y_line = (int)y1_block + height_letter * 14;
         //g2d.drawString(heading_line, x_line, y_line); 

         //////// left: COG wind (10 min) only if FM13 /////
         //
         if (main.obs_format.equals(main.FORMAT_FM13)) 
         {
            x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
            y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 12;
            g2d.drawString(COG_wind_line, x_line, y_line); 
         }
         
         //////// left: SOG wind (10 min) only if FM13 /////
         //
         if (main.obs_format.equals(main.FORMAT_FM13)) 
         {
            x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
            y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 14;
            g2d.drawString(SOG_wind_line, x_line, y_line); 
         }
         

         //////// left: COG /////
         //
         //g2d.setPaint(color_digits_GPS);
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 16;
         if (main.obs_format.equals(main.FORMAT_101)) 
         {
            g2d.drawString(COG_10_min_line, x_line, y_line); 
         }
         else if (main.obs_format.equals(main.FORMAT_FM13)) 
         {
            g2d.drawString(COG_3_hrs_line, x_line, y_line); 
         }
         
         ///////// left: SOG //////
         //
         //g2d.setPaint(color_digits_GPS);
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 18;
         
         if (main.obs_format.equals(main.FORMAT_101)) 
         {
            g2d.drawString(SOG_10_min_line, x_line, y_line); 
         }
         else if (main.obs_format.equals(main.FORMAT_FM13)) 
         {
            g2d.drawString(SOG_3_hrs_line, x_line, y_line); 
         }
         
         
         ////////// left: Lat ////////
         //
         //g2d.setPaint(color_digits_GPS);
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 20;
         g2d.drawString(lat_line, x_line, y_line); 
         //g2d.setPaint(color_black);
        
         ////////// left: Lon ////////
         //
         //g2d.setPaint(color_digits_GPS);
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 22;
         g2d.drawString(lon_line, x_line, y_line); 
         //g2d.setPaint(color_black);
         
         ////////// left: MSLP ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 26;
         g2d.drawString(mslp_line, x_line, y_line); 
         
         
         ////////// left: tendency ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 28;
         g2d.drawString(tendency_line, x_line, y_line); 
         
         
         ////////// left: tendency characteristic ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 30;
         g2d.drawString(tendency_char_line, x_line, y_line); 
         
         ////////// left: presure at sensor height ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 32;
         g2d.drawString(pressure_height_line, x_line, y_line); 
         
         
         ////////// left: Air temp ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 36;
         g2d.drawString(air_temp_line, x_line, y_line); 
         
         
         ////////// left: RH ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 38;
         g2d.drawString(rh_line, x_line, y_line); 
         
         
         ////////// left: SST ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 40;
         //g2d.drawString(sst_line, x_line, y_line); 
         
         
         ////////// left: next APR ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 46;
         g2d.drawString(next_APR_line, x_line, y_line);
         
         
         ////////// left: Visual obs ////////
         //
         x_line = (int)DASHBOARD_view_APR_radar.x1_block + height_letter;
         y_line = (int)DASHBOARD_view_APR_radar.y1_block + height_letter * 48;
         g2d.drawString(visual_obs_line, x_line, y_line);
         
         
         ////////// left: satellite image link //////
         //
         String satellite_link_txt = "satellite image (internet)";
         DASHBOARD_view_APR_radar.x_line_satellite_link = (int)(DASHBOARD_view_APR_radar.x1_block + height_letter);
         DASHBOARD_view_APR_radar.y_line_satellite_link = (int)(DASHBOARD_view_APR_radar.y1_block + height_letter * 54);
         FontRenderContext frc = g2d.getFontRenderContext();
         DASHBOARD_view_APR_radar.layout_satellite_link = new TextLayout(satellite_link_txt, font_k, frc);
         
         // for underlining the "satellite image (internet)" string
         AttributedString as = new AttributedString(satellite_link_txt);
         as.addAttribute(TextAttribute.FONT, font_k);
         as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, satellite_link_txt.length());
         
         //DASHBOARD_view_APR_radar.layout_satellite_link.draw(g2d, DASHBOARD_view_APR_radar.x_line_satellite_link, DASHBOARD_view_APR_radar.y_line_satellite_link);  // https://cr.openjdk.java.net/~iris/se/10/build/latest/api/java/awt/font/TextLayout.html
         g2d.setPaint(color_dark_blue);
         g2d.drawString(as.getIterator(), DASHBOARD_view_APR_radar.x_line_satellite_link, DASHBOARD_view_APR_radar.y_line_satellite_link);
         
         // define bounding box for mouse click (note coordinate system is here still with 0,0 as middle screen -after translate-)!
         DASHBOARD_view_APR_radar.bounds = DASHBOARD_view_APR_radar.layout_satellite_link.getBounds();
         DASHBOARD_view_APR_radar.bounds.setRect(DASHBOARD_view_APR_radar.bounds.getX()+ DASHBOARD_view_APR_radar.x_line_satellite_link, DASHBOARD_view_APR_radar.bounds.getY()+ DASHBOARD_view_APR_radar.y_line_satellite_link, DASHBOARD_view_APR_radar.bounds.getWidth(), DASHBOARD_view_APR_radar.bounds.getHeight());
         //NB for completely outlined: g2d.draw(DASHBOARD_view_APR_radar.bounds);
         
      } // if (draw_info_blocks)  
      
      
      ///////////////////// "last updated date and time" bottom left screen [South panel]
      //
      String updated_text_bottom_screen = "last updated: ";
      String update_message_bottom_screen = "";
      
      main_RS232_RS422.dashboard_string_last_update_record_date_time = main.sdf_tsl_2.format(new Date()) + " UTC";   // new Date() -> always in UTC 
   
      if (main_RS232_RS422.dashboard_string_last_update_record_date_time.equals(""))
      {
         update_message_bottom_screen =  updated_text_bottom_screen + " unknown";
      }
      else
      {
         //update_message_bottom_screen = updated_text_bottom_screen + " " + main_RS232_RS422.dashboard_string_last_update_record_date_time.replace("-", " ");
         update_message_bottom_screen = updated_text_bottom_screen + " " + main_RS232_RS422.dashboard_string_last_update_record_date_time;
      }

      DASHBOARD_view_APR_radar.jLabel19.setForeground(color_update_message_south_panel);
      DASHBOARD_view_APR_radar.jLabel19.setText(update_message_bottom_screen);

   
   } // public void paintComponent(Graphics g)   
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void compute_relative_wind_APR()
   //public static void compute_relative_wind_APR()
   {
   	/* num_ground_course_ship        : hele graden */
   	/* num_ground_speed_ship         : hele knopen */
   	/* num_ground_speed_tienden_ship : tienden knopen */
	   /* num_wind_direction            : ingevulde wind; hier: true wind richting (hele graden ook variable=9999 mogelijk) ALTIJD T.O.V. TRUE NORTH */
   	/* num_wind_speed                : ingevulde wind; hier: true wind snelheid (hele knopen of m/s) */
   	/* iw_units                      : unit indicator (1 = knopen en 2 = m/s) */
	   /* num_apparent_wind_direction   : apparent ind direction (hele graden) */
   	/* num_apparent_wind_speed       : apparent wind speed  (hele knopen of m/s) */

   	/* n.b. num_wind_direction en/of num_wind_speed == MAXINT (leeg veld) is hier niet meer mogelijk */
   	/* dan was al eerder een Error gegeven */

      // method WOCE
      //
      // To' = 270 - To
      //
      // To: tue wind direction
      // To': true wind direction [mathematical]
      //
      //
      // Co' = 90 - Co
      //
      // Co': COG (course over ground)[mathematical]
      // Co: COG (course over ground)
      //
      //
      // Au = Au' = |T|cos(To') - |C|cos(Co')
      // Av = Av' = |T|sin(To') - |C|sin(Co')
      //
      // T: true wind speed
      // C: SOG (speed over ground)
      // Au: eastward component apparent wind
      // Av: northward component apparrent wind
      //
      //
      // P = |A| = sqrt(Au** + Av**)
      // Po = 270 - atan(Av/Au) - ho
      //
      // ho: heading vessel
      // P: apparent wind speed
      // Po: platform-relative wind direction
      //
      //
   
      if ( (mywind.int_true_wind_speed >= 0.0 && mywind.int_true_wind_speed <= 400.0) && 
           (myposition.SOG_APR_wind >= 0.0 && myposition.SOG_APR_wind <= 50.0) && 
           (mywind.int_true_wind_dir >= 0.0 && mywind.int_true_wind_dir <= 360.0) &&
           (myposition.COG_APR_wind >= 0.0 && myposition.COG_APR_wind <= 360.0) )
      {
         try
         {
         double ho = myposition.COG_APR_wind;            // num_heading_ship;
         double Co = myposition.COG_APR_wind;            // num_ground_course_ship;
         double T  = mywind.int_true_wind_speed;         // knots or m/s (depending main.wind_units)
         double To = mywind.int_true_wind_dir;
         double Ro = 0;                                   // by definition (aanname bij VOS)
         double C = 0;
         
         //
	      // eventueel omzetten van scheepssnelheid in knopen naar m/s (afh. van iw_units)
	      // dit omdat dan ook de num_wind_speed in m/s is (gelijke eenheden)
	      //
  
         if (main.wind_units.trim().indexOf(main.M_S) != -1)
         {
            C = myposition.SOG_APR_wind * main.KNOT_M_S_CONVERSION;           // m/s
         }
         else
         {
            C = myposition.SOG_APR_wind;                                      // knots
         }


       	//
      	// geen variable wind /* en geen stilligend schip */
	      //
         double To_mat = 270 - To;

         double Co_mat = 90 - Co;
         //if (Co_mat < 0) Co_mat += 360;

         double Au = T * Math.cos(Math.toRadians(To_mat)) - C * Math.cos(Math.toRadians(Co_mat));
         double Av = T * Math.sin(Math.toRadians(To_mat)) - C * Math.sin(Math.toRadians(Co_mat));

         double A = Math.sqrt(Au * Au + Av * Av);
         double P = Math.abs(A);
         double Po;

         if (Au != 0.0)
         {
            Po = 270 - Math.toDegrees(Math.atan2(Av, Au)) /*- ho*/;
         }
         else
         {
            Po = 0;
         }

         if (Po > 360) Po -= 360;          // kan groter dan 360 graden zijn
         if (Po < 0) Po += 360;            // komt hij hier ooit?

            
         // relative wind force [Bf]
         if (main.wind_units.trim().indexOf(main.M_S) != -1)
         {
            DASHBOARD_view_APR_radar.relative_beaufort_class_APR = DASHBOARD_view_APR_radar.convert_ms_to_bf((int)Math.round(P));
         }
         else
         {
            DASHBOARD_view_APR_radar.relative_beaufort_class_APR = DASHBOARD_view_APR_radar.convert_knots_to_bf((int)Math.round(P));     
         }

            
         // relative wind dir [deg]
         DASHBOARD_view_APR_radar.relative_wind_dir_APR = Integer.toString((int)Math.round(Po));            // double rounding then -> int then -> String

/////////////////////
         // correction if 0 degrees (computed) -> 360 degrees
         if (DASHBOARD_view_APR_radar.relative_beaufort_class_APR > 0)
         {
            if (DASHBOARD_view_APR_radar.relative_wind_dir_APR.equals("0"))
            {
               DASHBOARD_view_APR_radar.relative_wind_dir_APR = "360";
            }
         }

/////////////////////
         
         
         // show on bottom of the right panel
         DASHBOARD_view_APR_radar.jLabel20.setForeground(color_rel_wind_arrow_actual);
         if (main.wind_units.trim().indexOf(main.M_S) != -1)
         {
            //DASHBOARD_view_APR_radar.jLabel20.setText("rel wind: " + (int)Math.round(Po) + "\u00B0 " + (int)Math.round(P) + " m/s");
            DASHBOARD_view_APR_radar.jLabel20.setText("rel wind: " + DASHBOARD_view_APR_radar.relative_wind_dir_APR + "\u00B0 " + (int)Math.round(P) + " m/s");
         }
         else
         {
            //DASHBOARD_view_APR_radar.jLabel20.setText("rel wind: " + (int)Math.round(Po) + "\u00B0 " + (int)Math.round(P) + " kts");
            DASHBOARD_view_APR_radar.jLabel20.setText("rel wind: " + DASHBOARD_view_APR_radar.relative_wind_dir_APR + "\u00B0 " + (int)Math.round(P) + " kts");
         }
         
         }
         catch (Exception ex)
         {
            System.out.println("Function compute_relative_wind_APR(): " + ex);
            DASHBOARD_view_APR_radar.relative_beaufort_class_APR    = main.INVALID;     
            DASHBOARD_view_APR_radar.relative_wind_dir_APR          = "";
         }
            // voor consistentie
            //if (num_apparent_wind_speed == 0)
            //   num_apparent_wind_direction = 0;
///
            //if ((num_apparent_wind_speed != 0) && (num_apparent_wind_direction == 0))
            //   num_apparent_wind_direction = 360;
      }
      else
      {
         DASHBOARD_view_APR_radar.relative_beaufort_class_APR    = main.INVALID;     
         DASHBOARD_view_APR_radar.relative_wind_dir_APR          = "";
         
         DASHBOARD_view_APR_radar.jLabel20.setForeground(color_rel_wind_arrow_actual);
         DASHBOARD_view_APR_radar.jLabel20.setText("rel wind: -");   
         
      }
   ///// ONDERSTAANDE NOG NAKIJKEN /////
	/* n.b. num_true_wind_direction = 0 - 4 (Noord) wordt in aanroepende programma op 360 gezet */
   
      //System.out.println("+++++++++++ relative_wind_dir_APR [deg] = " + DASHBOARD_view_APR_radar.relative_wind_dir_APR);
      //System.out.println("+++++++++++ relative_beaufort_class_APR [Bf] = " + DASHBOARD_view_APR_radar.relative_beaufort_class_APR);

   
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   /*
   private void enable_disable_controls_APR_radar(boolean mode)
   {
      // called from paintComponents() [DASBOARD_grafiek_APR_radar.java]
      
      // NB mode = false: no visual obs yet
      //    mode = true: visual obs possible
      
      //////////// TESTING ///////////
      //mode = true;
      ////////////////////////////// 
      
      DASHBOARD_view_APR_radar.reset_APR_variables();
      
      
      // NB if wind is not estimated or not measured TRUE then make no changes to the default (see initComponents1()[DASHBOARD_view_APR_radar.java]) disabled situation
      if ( (main.wind_source.equals(main.ESTIMATED_TRUE)) || (main.wind_source.equals(main.MEASURED_TRUE)) )
      {
         if (mode == false)   // no visual obs (xx.00 hr - xx.30 hr)
         {
            DASHBOARD_view_APR_radar.relative_beaufort_class_APR = Integer.MAX_VALUE;
            DASHBOARD_view_APR_radar.relative_wind_dir_APR = "";
            
            DASHBOARD_view_APR_radar.beaufort_class_APR = Integer.MAX_VALUE;
            DASHBOARD_view_APR_radar.wind_dir_APR = "";
         
            DASHBOARD_view_APR_radar.jLabel5.setText("");
            DASHBOARD_view_APR_radar.jLabel6.setText("");
            DASHBOARD_view_APR_radar.jLabel7.setText("");
            DASHBOARD_view_APR_radar.jLabel8.setText("");
            DASHBOARD_view_APR_radar.jLabel9.setText("");
            DASHBOARD_view_APR_radar.jLabel10.setText("");
            DASHBOARD_view_APR_radar.jLabel11.setText("");
            DASHBOARD_view_APR_radar.jLabel12.setText("");
            DASHBOARD_view_APR_radar.jLabel13.setText("");
            DASHBOARD_view_APR_radar.jLabel14.setText("");
            DASHBOARD_view_APR_radar.jLabel15.setText("");
            DASHBOARD_view_APR_radar.jLabel16.setText("");
            DASHBOARD_view_APR_radar.jLabel17.setText("");
            DASHBOARD_view_APR_radar.jLabel18.setText("");
         
            DASHBOARD_view_APR_radar.jComboBox1.setSelectedItem("");                    // NB -1 doesn't work
          
            DASHBOARD_view_APR_radar.buttonGroup1.clearSelection();
            
            DASHBOARD_view_APR_radar.jLabel20.setForeground(color_rel_wind_arrow_actual);
            DASHBOARD_view_APR_radar.jLabel20.setText("rel wind: -");                   // calculated relative wind

         } // if (mode == false) 
      
         DASHBOARD_view_APR_radar.jButton1.setEnabled(mode);
         DASHBOARD_view_APR_radar.jComboBox1.setEnabled(mode);
      
         DASHBOARD_view_APR_radar.jRadioButton1.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton2.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton3.setEnabled(mode); 
         DASHBOARD_view_APR_radar.jRadioButton4.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton5.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton6.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton7.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton8.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton9.setEnabled(mode); 
         DASHBOARD_view_APR_radar.jRadioButton10.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton11.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton12.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton13.setEnabled(mode);
         DASHBOARD_view_APR_radar.jRadioButton14.setEnabled(mode);
      } //  if ( (!main.wind_source.equals(main.ESTIMATED_TRUE)) && (!main.wind_source.equals(main.MEASURED_TRUE)) )
   }
*/   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void draw_wind_arrow_radar_APR(Graphics2D g2d, double marker_circle_diameter_2, double bf_per_class_radius, String wind_type, boolean wind_dir_present, boolean wind_force_present, int wind_dir, int wind_force)
{
   boolean wind_dir_ok             = false;
   boolean wind_speed_ok           = false;
   int Bf_class                    = Integer.MAX_VALUE;
   int wind_dir_reading            = Integer.MAX_VALUE;
   double max_arrow_width          = 0;
   float thickness_wind_rose_arrow = 2.0f;
   
   
   if (wind_type.equals(TRUE_WIND))
   {
      color_wind_arrow_max    = color_true_wind_arrow_max;
      color_wind_arrow_actual = color_true_wind_arrow_actual;     
      max_arrow_width = 8;
   }
   else if (wind_type.equals(RELATIVE_WIND))
   {
      color_wind_arrow_max    = color_rel_wind_arrow_max;
      color_wind_arrow_actual = color_rel_wind_arrow_actual;
      max_arrow_width = 10;
   }
   
   // wind dir
   if (wind_dir_present)
   {
      wind_dir_reading = wind_dir;
      if (wind_dir_reading >= 1 && wind_dir_reading <= 360)       // dir = 0 should be impossible 
      {
         wind_dir_ok = true;
      }
   } //if (main.true_wind_dir_from_AWS_present)
   
/*   
   // insert wind speed as Bf
   if (wind_speed_present)
   {
      int wind_speed_reading = wind_speed; // NB could by kts or m/s
      if (wind_speed_reading >= 0.0 && wind_speed_reading <= 400.0) 
      {
         // so the digit indication is up to 400 kts/m/s and the analogue up to 110 kts/m/s
         if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1)                        // units is knots
         {
            
            Bf_class = convert_knots_to_bf(wind_speed_reading);
         } //  if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) 
         
         else if (main.wind_units_dashboard.indexOf(main.M_S) != -1)                      // units is m/s
         {
            Bf_class = convert_ms_to_bf(wind_speed_reading);
         } // else if (main.wind_units_dashboard.indexOf(main.M_S) != -1)
         
         
        
         
         wind_speed_ok = Bf_class <= 12;
        
      } // if (wind_speed_reading >= 0.0 && wind_speed_reading <= 400.0)
   } //if (main.true_wind_speed_from_AWS_present)
*/   
   if (wind_force_present)
   {
      Bf_class = wind_force;  //DASHBOARD_view_APR_radar.beaufort_class_APR;
   }
   
   wind_speed_ok = Bf_class <= 12;
   
   
   if (wind_speed_ok && wind_dir_ok)
   {
      double radius_wind_arrow_max = marker_circle_diameter_2 / 2;
    
      // NB "wind_rose_diameter = block_wind_rose_width / 1.5"  (if block_wind_rose_width < block_wind_rose_heightz) else "wind_rose_diameter = block_wind_rose_height / 1.5" ;
      //
      // point of the arrow is origin (0, 0)
      //
      //       (x1,y1)____________ (x2,y2)
      //              \          /
      //               \        /
      //                \      /
      //                 \    /  
      //                  \  /
      //                    .   (0,0)
      //
      //
      //
   
   
      double radius_wind_arrow_actual = bf_per_class_radius * Bf_class;
   
   
      g2d.setStroke(new BasicStroke(thickness_wind_rose_arrow));
   
      
      //double max_arrow_width = 10;
   
      // polygon of the max theoretical wind arrow (12 Bf)
      //
      double x1_arrow = Math.cos(Math.toRadians(wind_dir_reading - (max_arrow_width / 2) - 90)) * (radius_wind_arrow_max);                   // -90 because the drawing starts at EAST (90 degr)
      double x2_arrow = Math.cos(Math.toRadians(wind_dir_reading + (max_arrow_width / 2) - 90)) * (radius_wind_arrow_max); 
      double x3_arrow = Math.cos(Math.toRadians(wind_dir_reading + 0 - 90)) * (radius_wind_arrow_max);
      
      double y1_arrow = Math.sin(Math.toRadians(wind_dir_reading - (max_arrow_width / 2) - 90)) * (radius_wind_arrow_max);
      double y2_arrow = Math.sin(Math.toRadians(wind_dir_reading + (max_arrow_width / 2) - 90)) * (radius_wind_arrow_max);        
      double y3_arrow = Math.sin(Math.toRadians(wind_dir_reading + 0 - 90)) * (radius_wind_arrow_max);        
            
      double xPoints[] = {0, x1_arrow, x3_arrow, x2_arrow};
      double yPoints[] = {0, y1_arrow, y3_arrow, y2_arrow};       
   
      GeneralPath polygon_max = new GeneralPath(GeneralPath.WIND_EVEN_ODD,  xPoints.length);
      polygon_max.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++ ) 
      {
         polygon_max.lineTo(xPoints[index], yPoints[index]);
      }
      polygon_max.closePath();
   
      // polygon of the actual wind arrow (e.g 4 Bf)
      //
      double x1_arrow_actual = Math.cos(Math.toRadians(wind_dir_reading - (max_arrow_width / 2) - 90)) * (radius_wind_arrow_actual);                   // -90 because the drawing starts at EAST (90 degr)
      double x2_arrow_actual = Math.cos(Math.toRadians(wind_dir_reading + (max_arrow_width / 2) - 90)) * (radius_wind_arrow_actual); 
      double x3_arrow_actual = Math.cos(Math.toRadians(wind_dir_reading + 0 - 90)) * (radius_wind_arrow_actual);
      
      double y1_arrow_actual = Math.sin(Math.toRadians(wind_dir_reading - (max_arrow_width / 2) - 90)) * (radius_wind_arrow_actual);
      double y2_arrow_actual = Math.sin(Math.toRadians(wind_dir_reading + (max_arrow_width / 2) - 90)) * (radius_wind_arrow_actual);        
      double y3_arrow_actual = Math.sin(Math.toRadians(wind_dir_reading + 0 - 90)) * (radius_wind_arrow_actual);        
      
      double xPoints_actual[] = {0, x1_arrow_actual, x3_arrow_actual, x2_arrow_actual};
      double yPoints_actual[] = {0, y1_arrow_actual, y3_arrow_actual, y2_arrow_actual};       
   
      GeneralPath polygon_actual = new GeneralPath(GeneralPath.WIND_EVEN_ODD,  xPoints.length);
      polygon_actual.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints_actual.length; index++ ) 
      {
         polygon_actual.lineTo(xPoints_actual[index], yPoints_actual[index]);
      }
      polygon_actual.closePath();
   
   
      // wind arrow fill (full theoretical possible max Bf12)
      g2d.setPaint(color_wind_arrow_max);   
      g2d.fill(polygon_max);
   
      // wind arrow fill (only the actual measured Bf part)
      g2d.setPaint(color_wind_arrow_actual);   
      g2d.fill(polygon_actual);
   
      // wind arrow contour (full theoretical possible max Bf12)
      //g2d.setColor(color_black);
      g2d.setColor(color_wind_arrow_actual);
      g2d.draw(polygon_max); 
   } // if (true_wind_speed_ok && true_wind_dir_ok)
   
}
   
   
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void draw_wind_rose_radar(Graphics2D g2d, double wind_rose_diameter, double marker_circle_diameter_1, double marker_circle_diameter_2, double bf_per_class_radius, int beaufort_class)
   {
      // eg: https://www.google.nl/search?q=barometer&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjJ78PZg8_UAhWEJMAKHZA3AsAQ_AUICigB&biw=1920&bih=950#imgrc=hE2asDauQrhr6M:
   
      String letter = null;
      String text;
      int angle;
      int start;
      int x;
      int y;
      int width_letter;
      float thickness_wind_rose_ring = 1.0f;
 

      //int horz_offset                = (int)(wind_rose_diameter - 0);            // -20 orig
      //int units_start                = (int)(wind_rose_diameter / 2 - 18);       // eg 220 / 2 - 18 = 92   // not the 5 and 10 units markers
      int units_end = (int) (wind_rose_diameter / 2 - 14);                         // eg 220 / 2 - 14 = 96   // all markers end (5, 10 and intermediate) 
      int units_5_start = (int) (wind_rose_diameter / 2 - 21);                     // eg 220 / 2 - 21 = 89   //the 5 units markers
      int units_10_start = (int) (wind_rose_diameter / 2 - 22);                    // eg 220 / 2 - 22 = 88   //the 10 units markers
      int text_base_units_values = (int) (wind_rose_diameter / 2 - 10);            // eg 220 / 2 - 10 = 100
      //int text_base_comments         = text_base_units_values / 2 + 5;           // eg 100 / 2 + 5 = 55
      int text_base_comments = (int) (text_base_units_values - (22 * 1.5));
      int width_a1 = 0;
      int width_a2 = 0;
   

      if (DASHBOARD_view_APR_radar.night_vision == false) 
      {
         // main wind/compass rose area   
         g2d.setPaint(color_wind_rose_background);
         g2d.fill(new Arc2D.Double(-wind_rose_diameter / 2, -wind_rose_diameter / 2, wind_rose_diameter, wind_rose_diameter, 0, 360, Arc2D.CHORD));
      }
      
      // NB see also: https://docstore.mik.ua/orelly/java-ent/jfc/ch04_04.htm for obtaining area difference (substracting)

      // save current clipping area
      Rectangle rec = g2d.getClipBounds();

      // inside the inside marker ring area
      Shape circle = new Ellipse2D.Float((float) -marker_circle_diameter_2 / 2, (float) -marker_circle_diameter_2 / 2, (float) marker_circle_diameter_2, (float) marker_circle_diameter_2);

      if (DASHBOARD_view_APR_radar.night_vision == true)
      {
         // main wind/compass rose area   
         g2d.setPaint(color_black);   
         g2d.fill(circle);
      }
   
      
      if (beaufort_class <= 12) 
      {
         // set clipping area to wind rose
         g2d.setClip(circle);

         String image = "bf" + Integer.toString(beaufort_class) + "_wind_radar.jpg";

         if (DASHBOARD_view_APR_radar.night_vision == false)
         {
            BufferedImage img1 = null;
            try 
            {
               img1 = ImageIO.read(this.getClass().getResource(main.ICONS_DIRECTORY + image));
               int wind_rose_radius = (int) wind_rose_diameter / 2;
               g2d.drawImage(img1, -wind_rose_radius, -wind_rose_radius, (int) wind_rose_diameter, (int) wind_rose_diameter, this);
            } 
            catch (IOException ex) 
            {
               System.out.println("+++ Dashboard meteo radar background image error (day mode)");
            }
         }
         else // night
         {
            //g2d.setPaint(color_black);
            //g2d.fill(circle);
            
            try
            {
               BufferedImage img = ImageIO.read(this.getClass().getResource(main.ICONS_DIRECTORY + image));
               //int w = img.getWidth();
               //int h = img.getHeight();
               int wind_rose_radius = (int)wind_rose_diameter / 2;

               // NB see: https://docs.oracle.com/javase/tutorial/2d/images/drawimage.html
               //BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);                       // needed for the opacity filter
               BufferedImage bi = new BufferedImage((int)wind_rose_diameter, (int)wind_rose_diameter, BufferedImage.TYPE_INT_ARGB);  // needed for the opacity filter + also scaling to radar circle size

               Graphics g = bi.getGraphics();
               //g.drawImage(img, 0, 0,  w, h, null);                                                           // draw image in memory
               g.drawImage(img, 0, 0, (int)wind_rose_diameter, (int)wind_rose_diameter, null);                // draw image in memory
            
               float[] scales = {1f, 1f, 1f, 0.2f};                                                           // 4th argument is opacity
               float[] offsets = new float[4];
               RescaleOp rop = new RescaleOp(scales, offsets, null);

               // Draw the image, applying the filter 
               g2d.drawImage(bi, rop,  -wind_rose_radius, -wind_rose_radius);                                 // draw image on screen
            }
            catch (IOException ex) 
            {
               System.out.println("+++ Dashboard meteo radar background image error (night mode)");
            }            
         } // else (night)  
            
         // reset clipping area
         g2d.setClip(rec);

      } // if (beaufort_class <= 12)


      // outside ring
      g2d.setColor(color_wind_rose_ring);
      g2d.setStroke(new BasicStroke(thickness_wind_rose_ring));
      g2d.draw(new Arc2D.Double(-wind_rose_diameter / 2, -wind_rose_diameter / 2, wind_rose_diameter, wind_rose_diameter, 0, 360, Arc2D.CHORD));

      for (int i = 0; i < 360; i++) 
      {
         // NB first i = <never mind> -> paint starting point = always East -> clockwise
         g2d.setColor(color_black);

         g2d.setFont(font_a);
         //g2d.setFont(font_b);

         if ((i % 5 == 0) && (i % 10 != 0)) 
         {
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawLine(units_5_start, 0, units_end, 0);
         } 
         else if (i % 10 == 0) // tens marks                            // 10 units marks   
         {
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawLine(units_10_start, 0, units_end, 0);

            // eg text = "90"
            if (i >= 0 && i <= 270) 
            {
               text = String.valueOf(i + 90);        // i = 90 = start of the scale (East dir)
            } 
            else 
            {
               text = String.valueOf(i - 270);        // i = 270 = North dir
            }

            // text rotated
            //
            // Font metrics
            width_a1 = g2d.getFontMetrics().stringWidth("90");
            width_a2 = g2d.getFontMetrics().stringWidth("100");

            x = text_base_units_values;
            if (i > 270 && i <= 360) // 0 - 90 deg
            {
               y = 0 - width_a1 / 2;
            } 
            else // 100 - 360 deg
            {
               y = 0 - width_a2 / 2;
            }

            // unit numbers e.g. 90
            angle = 90;
            g2d.translate((float) x, (float) y);
            g2d.rotate(Math.toRadians(angle));
            g2d.drawString(text, 0, 0);

            g2d.rotate(-Math.toRadians(angle));    // rotate back
            g2d.translate(-(float) x, -(float) y);    // translate back

         } // else (tens)

         g2d.setColor(color_wind_rose_additional);

         // text decoration 'EAST'
         start = 0;
         //if (i == 360 - 15 || i == 360 - 5 || i == start + 5 || i == start + 15)
         if (i == 360 - 6 || i == 360 - 2 || i == start + 2 || i == start + 6) //if (i == start)   
         {
            if (i == 360 - 6) 
            {
               letter = "E";
            } 
            else if (i == 360 - 2) 
            {
               letter = "A";
            } 
            else if (i == start + 2) 
            {
               letter = "S";
            } 
            else if (i == start + 6) 
            {
               letter = "T";
            }
            //letter = "E";

            g2d.setFont(font_b);
            width_letter = g2d.getFontMetrics().stringWidth("E");
            y = 0 - width_letter / 2;

            x = text_base_comments;
            angle = 90;
            g2d.translate((float) x, (float) y);
            g2d.rotate(Math.toRadians(angle));
            g2d.drawString(letter, 0, 0);

            g2d.rotate(-Math.toRadians(angle));    // rotate back
            g2d.translate(-(float) x, -(float) y);    // translate back
         }

         // text decoration 'SOUTH'
         start = 90;
         if (i == start - 8 || i == start - 4 || i == start || i == start + 4 || i == start + 8) //if (i == start)
         {
            if (i == start - 8) 
            {
               letter = "S";
            } 
            else if (i == start - 4) 
            {
               letter = "O";
            } 
            else if (i == start) 
            {
               letter = "U";
            } 
            else if (i == start + 4) 
            {
               letter = "T";
            } 
            else if (i == start + 8) 
            {
               letter = "H";
            }
            //letter = "S";

            g2d.setFont(font_b);
            width_letter = g2d.getFontMetrics().stringWidth("S");
            y = 0 - width_letter / 2;

            x = text_base_comments;
            angle = 90;
            g2d.translate((float) x, (float) y);
            g2d.rotate(Math.toRadians(angle));
            g2d.drawString(letter, 0, 0);

            g2d.rotate(-Math.toRadians(angle));    // rotate back
            g2d.translate(-(float) x, -(float) y);    // translate back
         }

         // text decoration 'WEST'
         start = 180;
         if (i == start - 6 || i == start - 2 || i == start + 2 || i == start + 6) //if (i == start)
         {
            if (i == start - 6) 
            {
               letter = "W";
            } 
            else if (i == start - 2) 
            {
               letter = "E";
            } 
            else if (i == start + 2) 
            {
               letter = "S";
            } 
            else if (i == start + 6) 
            {
               letter = "T";
            }
            //letter = "W";

            g2d.setFont(font_b);
            width_letter = g2d.getFontMetrics().stringWidth("W");
            y = 0 - width_letter / 2;

            x = text_base_comments;
            angle = 90;
            g2d.translate((float) x, (float) y);
            g2d.rotate(Math.toRadians(angle));
            g2d.drawString(letter, 0, 0);

            g2d.rotate(-Math.toRadians(angle));    // rotate back
            g2d.translate(-(float) x, -(float) y);    // translate back
         }

         // text decoration 'NORTH'
         start = 270;
         if (i == start - 8 || i == start - 4 || i == start || i == start + 4 || i == start + 8) //if (i == start)
         {
            if (i == start - 8) 
            {
               letter = "N";
            } 
            else if (i == start - 4) 
            {
               letter = "O";
            } 
            else if (i == start) 
            {
               letter = "R";
            } 
            else if (i == start + 4) 
            {
               letter = "T";
            } 
            else if (i == start + 8) 
            {
               letter = "H";
            }
            //letter = "N";   

            g2d.setFont(font_b);
            width_letter = g2d.getFontMetrics().stringWidth("N");
            y = 0 - width_letter / 2;

            x = text_base_comments;
            angle = 90;
            g2d.translate((float) x, (float) y);
            g2d.rotate(Math.toRadians(angle));
            g2d.drawString(letter, 0, 0);

            g2d.rotate(-Math.toRadians(angle));    // rotate back
            g2d.translate(-(float) x, -(float) y);    // translate back
         }

         // Because the rotate function of Java Graphics takes a radian value as a parameter, we convert 3 degree to radians by the formula (?/180 x 3.0).
         //g2d.rotate((Math.PI / 180.0) * 3.0);  // 360 / 120 = 3.0
         g2d.rotate((Math.PI / 180.0) * 1.0);  // 360 / 360 = 1.0

      } // for (int i = 0; i < 360; i++)

      // NB Arc2D.Double(double x, double y, double w, double h, double start, double extent, int type)
      //    Constructs a new arc, initialized to the specified location, size, angular extents, and closure type.
      //
      // marker (outer) circles (this are not the Bf circles)
      //
      g2d.setColor(color_black);
      g2d.setStroke(new BasicStroke(1.0f));
      g2d.draw(new Arc2D.Double(-marker_circle_diameter_1 / 2, -marker_circle_diameter_1 / 2, marker_circle_diameter_1, marker_circle_diameter_1, 360, 360, Arc2D.OPEN));   // point East = 0 degrees (normally you would call East 90 degrees...) -> to the left
      g2d.draw(new Arc2D.Double(-marker_circle_diameter_2 / 2, -marker_circle_diameter_2 / 2, marker_circle_diameter_2, marker_circle_diameter_2, 360, 360, Arc2D.OPEN));   // point East = 0 degrees -> to the left

      // Bf circles
      //
      g2d.setColor(color_wind_rose_additional);
      //float[] dash = {2f, 2f, 2f};   // length, space
      float[] dash = {2f, 4f};   // length, space
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));

      // every 3Bf a circle + label
      for (int i = 3; i <= 9; i += 3) 
      {
         // NB i = Bf class
         g2d.draw(new Arc2D.Double(-bf_per_class_radius * i, -bf_per_class_radius * i, 2 * bf_per_class_radius * i, 2 * bf_per_class_radius * i, 360, 360, Arc2D.OPEN));   // point East = 0 degrees (normally you would call East 90 degrees...) -> to the left

         // Bf circles labels
         //g2d.setColor(color_black);
         g2d.setFont(font_c);
         String bf_text = "Bf " + i;

         // label along 360 degrees direction
         int x_bf_label = 0;
         int y_bf_label = (int) (-bf_per_class_radius * i);
         g2d.drawString(bf_text, x_bf_label, y_bf_label);

         // labels along 90 degrees direction
         int x_bf_label2 = (int) (bf_per_class_radius * i);
         int y_bf_label2 = 0;
         g2d.translate((float) x_bf_label2, (float) y_bf_label2);
         g2d.rotate(Math.toRadians(90));
         g2d.drawString(bf_text, 0, 0);
         g2d.rotate(-Math.toRadians(90));
         g2d.translate(-(float) x_bf_label2, -(float) y_bf_label2);

      } // for (int i = 3; i <= 9; i+=3)   

      /*
   // Labels (left below wind rose)
   //
   g2d.setColor(color_black);
   //g2d.setColor(color_wind_rose_additional);
   g2d.setFont(font_f);
   //g2d.drawString("True wind", (int)(-wind_rose_diameter / 2), (int)(wind_rose_diameter / 2));
   
   
   
   //
   int height_letter = g2d.getFontMetrics().stringWidth("M");
   
   
   // insert MSLP
   //
   
   
   //y = 0 - width_letter / 2
   
   int x_mslp = (int)((-wind_rose_diameter / 2) - (DASHBOARD_view_AWS_radar.width_AWS_radar_dashboard * 0.1 / 2));
   int y_mslp = (int)(wind_rose_diameter / 2) - (height_letter * 2);
   
   g2d.drawString("", x_mslp, y_mslp);
   if (main.pressure_MSL_from_AWS_present)
   {
      double reading_mslp = main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic;         // see: RS422_update_AWS_dasboard_values()
      if (reading_mslp > 900.0 && reading_mslp < 1100.0)
      {
         String digits = Double.toString(reading_mslp) + " hPa";
         g2d.drawString("MSLP: " + digits, x_mslp, y_mslp);
      }
   }
   else
   {
      g2d.drawString("MSLP: -", x_mslp, y_mslp);
   }
   
   // insert barometric tendency
   //
   int x_tendency = (int)((-wind_rose_diameter / 2) - (DASHBOARD_view_AWS_radar.width_AWS_radar_dashboard * 0.1 / 2));
   int y_tendency = (int)(wind_rose_diameter / 2);
   //
   g2d.drawString("", x_tendency, y_tendency);
   if (main.pressure_tendency_from_AWS_present)
   {
   //     
      double tendency_reading = main_RS232_RS422.dashboard_double_last_update_record_pressure_tendency; // see: RS422_update_AWS_dasboard_values()
      if (tendency_reading >= -99.9 && tendency_reading <= 99.9)
      {
         String digits = Double.toString(tendency_reading) + " hPa / 3 hrs";
         g2d.drawString("tendency: " + digits, x_tendency, y_tendency);  
      }
   } 
   else
   {
      g2d.drawString("tendency: -", x_tendency, y_tendency);
   }
       */
   }
   
   
   private GregorianCalendar cal_APR_system_date_time_db;
   
   private final String TRUE_WIND       = "true_wind";
   private final String RELATIVE_WIND  = "relative_wind";
   
   private final Color color_wind_rose_additional;
   private final Color color_wind_rose_ring;
   private final Color color_wind_rose_background;
   private final Color color_contour_block;
   
   private final Color color_true_wind_arrow_max; 
   private final Color color_rel_wind_arrow_max; 
   public static Color color_true_wind_arrow_actual;
   public static Color color_rel_wind_arrow_actual;
   
   private final Color color_blue;
   private final Color color_dark_blue;
   private final Color color_black;
   private final Color color_gray;
   private final float block_contour_thickness;
  
   private Color color_fill_block;                     // not final  // parameter value text blocks left and right of the main wind radar circle
   //private Color color_last_update;                    // not final
   //private Color color_digits;                         // not final
   private Color color_update_message_south_panel;
   //private Color color_digits_GPS;                     // not final

   private Color color_wind_arrow_max;                 // not final
   private Color color_wind_arrow_actual;              // not final
   
   private Font font_a; 
   private Font font_b;
   private Font font_c;
   private Font font_g;
   //private Font font_h;
   private Font font_k;
   
}
