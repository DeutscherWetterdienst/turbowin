/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package turbowin;

import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import static turbowin.main_RS232_RS422.RS232_APR_AWSR_send;
import static turbowin.main_RS232_RS422.RS232_make_RH_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_air_temp_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_dew_point_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_pressure_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_pressure_a_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_pressure_ppp_APR_FM13_IMMT_ready;
import static turbowin.main_RS232_RS422.RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready;
//import static turbowin.main_RS232_RS422.TIMEDIFF_SENSOR_DATA;




/**
 *
 * @author marti
 */
public class RS232_vaisala {
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                       RS232_Read_Sensor_Data_a_ppp_Data_Files_For_Obs                       */
/*                                                                                             */
/***********************************************************************************************/
public static void RS232_Read_Sensor_Data_a_ppp_For_Obs()
{
   // Vaisala PTB220 or PTB330 (NOT AWS and NOT Mintaka Duo/Star)
   
   
   if (main.RS232_connection_mode == 1)       // PTB220
   {
      main.type_record_lengte                 = main.RECORD_LENGTE_PTB220;
      main.type_record_datum_tijd_begin_pos   = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
      //type_record_minuten_begin_pos      = RECORD_MINUTEN_BEGIN_POS_PTB220;
      //type_record_pressure_begin_pos     = RECORD_P_BEGIN_POS_PTB220;
      main.type_record_a_begin_pos            = main.RECORD_a_BEGIN_POS_PTB220;
      main.type_record_ppp_begin_pos          = main.RECORD_ppp_BEGIN_POS_PTB220;
   }
   else if (main.RS232_connection_mode == 2)  // PTB330   
   {
      main.type_record_lengte                 = main.RECORD_LENGTE_PTB330;
      main.type_record_datum_tijd_begin_pos   = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB330;
      //type_record_minuten_begin_pos      = RECORD_MINUTEN_BEGIN_POS_PTB330;
      //type_record_pressure_begin_pos     = RECORD_P_BEGIN_POS_PTB330;
      main.type_record_a_begin_pos            = main.RECORD_a_BEGIN_POS_PTB330;
      main.type_record_ppp_begin_pos          = main.RECORD_ppp_BEGIN_POS_PTB330;
   }
   
   // initialisation
   mybarograph.pressure_amount_tendency = "";
   mybarograph.a_code                   = "";
   

   new SwingWorker<Void, Void>()
   {
      @Override
      protected Void doInBackground() throws Exception
      {
         main.obs_file_datum_tijd = new GregorianCalendar();
         main.obs_file_datum_tijd.add(Calendar.MINUTE, -2);     // of is -1 ook goed????? : to be sure there was all time that it was written to the file
         File sensor_data_file;
         String record         = null;
         String laatste_record = null;

         // initialisation
         main.sensor_data_record_obs_ppp = "";
         main.sensor_data_record_obs_a   = "";

         // determine file name
         String sensor_data_file_naam_datum_tijd_deel = main.sdf3.format(main.obs_file_datum_tijd.getTime()); // geeft bv 2013020308
         String sensor_data_file_name = "sensor_data_" + sensor_data_file_naam_datum_tijd_deel + ".txt";

         // first check if there is a sensor data file present (and not empty)
         String volledig_path_sensor_data = main.logs_dir + java.io.File.separator + sensor_data_file_name;
         //System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

         sensor_data_file = new File(volledig_path_sensor_data);
         if (sensor_data_file.exists() && sensor_data_file.length() > 0)     // length() in bytes
         {
            try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
            {
               record         = null;
               laatste_record = null;

               while ((record = in.readLine()) != null)
               {
                  if (record.length() == main.type_record_lengte)
                  {
                     laatste_record = record;
                  } 
               } // while ((record = in.readLine()) != null)

               if (laatste_record != null)
               {
                  String record_datum_tijd_minuten = laatste_record.substring(main.type_record_datum_tijd_begin_pos, main.type_record_datum_tijd_begin_pos + 12);  // bv 201302201345
                  Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
                  long system_sec = System.currentTimeMillis();

                  long timeDiff = Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

                  ///System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

                  //String record_pressure_present = null;
                  if (timeDiff <= 3L)      // max 3 minutes old
                  {
                     main.sensor_data_record_obs_ppp = laatste_record.substring(main.type_record_ppp_begin_pos, main.type_record_ppp_begin_pos + 5);
                     main.sensor_data_record_obs_a   = laatste_record.substring(main.type_record_a_begin_pos, main.type_record_a_begin_pos + 1);

                     // rounding eg: 998.19 -> 998.2
                     //        double digitale_sensor_waarde = Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                     //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;  // bv 998.19 -> 998.2

                     System.out.println("--- sensor data record, tendency for obs: " + main.sensor_data_record_obs_ppp);
                     System.out.println("--- sensor data record, characteristic for obs: " + main.sensor_data_record_obs_a);
                  }
                  else
                  {
                     //main.sensor_data_record_obs_pressure = "";       // t.m versie 2.3.3. dit ten onrechte
                     main.sensor_data_record_obs_ppp = "";
                     main.sensor_data_record_obs_a = "";
                  }

               } // if (laatste_record != null)

            } // try
            catch (IOException ex) 
            {  
               System.out.println("--- Function RS232_Read_Sensor_Data_a_ppp_For_Obs(): " + ex);
            }

         } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
 
         // clear memory
         main.obs_file_datum_tijd      = null;
         //hulp_obs_file_datum_tijd = null;

         return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         int hulp_pressure_change = 3;                // -1 negative; 1 positive; 0 same pressure; 3 is only start value
         //Determine_Pressure_Characteristic();
         
         // ppp
         //
         if ( (main.sensor_data_record_obs_ppp.compareTo("") != 0) && (main.sensor_data_record_obs_ppp != null) && (main.sensor_data_record_obs_ppp.indexOf("*") == -1) )
         {
            double hulp_double_ppp_reading;
            try
            {
               hulp_double_ppp_reading = Double.parseDouble(main.sensor_data_record_obs_ppp.trim());
               hulp_double_ppp_reading = Math.round(hulp_double_ppp_reading * 10) / 10.0d;  // bv 13.19 -> 13.2
            }
            catch (NumberFormatException e)
            {
               System.out.println("--- " + "Function RS232_Read_Sensor_Data_a_ppp_For_Obs() " + e);
               hulp_double_ppp_reading = Double.MAX_VALUE;
            }   
             
            if ((hulp_double_ppp_reading > -99.9) && (hulp_double_ppp_reading < 99.9))
            {
               // NB hulp_pressure_change: for 'future' use to determine a (pressure characteristic)
               if (hulp_double_ppp_reading > 0.0)
               {
                  hulp_pressure_change = 1; 
               }
               else if (hulp_double_ppp_reading < 0.0)
               {
                  hulp_pressure_change = -1;
               }
               else if (hulp_double_ppp_reading == 0.0)
               {
                  hulp_pressure_change = 0;
               }
        
               // tendency
               mybarograph.pressure_amount_tendency = Double.toString(Math.abs(hulp_double_ppp_reading));  // only pos value in this field
               mybarograph.jTextField1.setText(mybarograph.pressure_amount_tendency); 
            }
         } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
         
         
         // a
         //
         if ((main.sensor_data_record_obs_a.compareTo("") != 0) && (main.sensor_data_record_obs_a != null) && (main.sensor_data_record_obs_a.indexOf("*") == -1) )
         {
            int hulp_int_a_reading;
            
            try
            {
               hulp_int_a_reading = Integer.parseInt(main.sensor_data_record_obs_a.trim());
            }
            catch (NumberFormatException e)
            {
               System.out.println("--- " + "Function RS232_Read_Sensor_Data_a_ppp_For_Obs() " + e);
               hulp_int_a_reading = Integer.MAX_VALUE;
            }   
             
            if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8))
            {
               // initialisation
               mybarograph.jRadioButton1.setSelected(false);
               mybarograph.jRadioButton2.setSelected(false);
               mybarograph.jRadioButton3.setSelected(false);
               mybarograph.jRadioButton4.setSelected(false);
               mybarograph.jRadioButton5.setSelected(false);
               mybarograph.jRadioButton6.setSelected(false);
               mybarograph.jRadioButton7.setSelected(false);
               mybarograph.jRadioButton8.setSelected(false);
               mybarograph.jRadioButton9.setSelected(false);
               mybarograph.jRadioButton10.setSelected(false);
               mybarograph.jRadioButton11.setSelected(false);
               mybarograph.jRadioButton12.setSelected(false);
               
               // pressure higher than 3hrs ago
               if (hulp_pressure_change == 1)
               {
                  if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_0))
                     mybarograph.jRadioButton1.setSelected(true);
                  else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_1))
                     mybarograph.jRadioButton2.setSelected(true);
                  else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_2))
                     mybarograph.jRadioButton3.setSelected(true);
                  else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_3))
                     mybarograph.jRadioButton4.setSelected(true);
               }
               
               // pressure lower than 3hrs ago
               else if (hulp_pressure_change == -1)
               {
                  if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_5))
                     mybarograph.jRadioButton5.setSelected(true);
                  else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_6))
                     mybarograph.jRadioButton6.setSelected(true);
                  else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_7))
                     mybarograph.jRadioButton7.setSelected(true);
                  else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_8))
                     mybarograph.jRadioButton8.setSelected(true);
               }
            
               // pressure the same as 3hrs ago
               else if (hulp_pressure_change == 0)
               {
                  if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_0_SAME))
                     mybarograph.jRadioButton9.setSelected(true);
                  else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_4))                                
                     mybarograph.jRadioButton10.setSelected(true);
                  else if (main.sensor_data_record_obs_a.trim().equals(mybarograph.A_5_SAME))                                
                     mybarograph.jRadioButton11.setSelected(true);              
               }    
               else
               {
                  mybarograph.jRadioButton12.setSelected(true);   // not determined
               }
               
            } // if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8))
         } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
      } // protected void done()

   }.execute(); // new SwingWorker <Void, Void>()

}
   
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                            RS232_Read_Sensor_Data_PPPP_For_Obs                              */
/*                                                                                             */
/***********************************************************************************************/
public static void RS232_Read_Sensor_Data_PPPP_For_Obs(boolean local_tray_icon_clicked)
{
   // Vaisala PTB220 or PTB330 (NOT AWS and NOT Mintaka Duo and NOT Mintaka Star)
   
   
   // called from: - initSynopparameters() [mybarometer.java]
   //              - main_windowIconfied() [main.java]
   //
   
   
   //if (barometer_type.equals(PTB220))
   if (main.RS232_connection_mode == 1)       // PTB220
   {
      main.type_record_lengte                 = main.RECORD_LENGTE_PTB220;
      main.type_record_datum_tijd_begin_pos   = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
      //type_record_minuten_begin_pos      = RECORD_MINUTEN_BEGIN_POS_PTB220;
      main.type_record_pressure_begin_pos     = main.RECORD_P_BEGIN_POS_PTB220;
   }
   //else if (barometer_type.equals(PTB330_SERVICE_PORT))
   //else if (barometer_type.equals(PTB330))   
   else if (main.RS232_connection_mode == 2)  // PTB330   
   {
      main.type_record_lengte                 = main.RECORD_LENGTE_PTB330;
      main.type_record_datum_tijd_begin_pos   = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB330;
      //type_record_minuten_begin_pos      = RECORD_MINUTEN_BEGIN_POS_PTB330;
      main.type_record_pressure_begin_pos     = main.RECORD_P_BEGIN_POS_PTB330;
   }
   
   // initialisation
   mybarometer.pressure_reading = "";
   
   // initialisation
   main.tray_icon_clicked = local_tray_icon_clicked;
   

   new SwingWorker<Void, Void>()
   {
      @Override
      protected Void doInBackground() throws Exception
      {
         main.obs_file_datum_tijd = new GregorianCalendar();
         main.obs_file_datum_tijd.add(Calendar.MINUTE, -2);     // of is -1 ook goed????? : to be sure there was all time that it was written to the file
         File sensor_data_file;
         String record         = null;
         String laatste_record = null;

         // initialisation
         main.sensor_data_record_obs_pressure = "";

         // present obs value
         String sensor_data_file_naam_datum_tijd_deel = main.sdf3.format(main.obs_file_datum_tijd.getTime()); // geeft bv 2013020308
         String sensor_data_file_name = "sensor_data_" + sensor_data_file_naam_datum_tijd_deel + ".txt";

         // first check if there is a sensor data file present (and not empty)
         String volledig_path_sensor_data = main.logs_dir + java.io.File.separator + sensor_data_file_name;
         //System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

         sensor_data_file = new File(volledig_path_sensor_data);
         if (sensor_data_file.exists() && sensor_data_file.length() > 0)     // length() in bytes
         {
            try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
            {
               record         = null;
               laatste_record = null;

               while ((record = in.readLine()) != null)
               {
                  if (record.length() == main.type_record_lengte)
                  {
                     laatste_record = record;
                  } 
               } // while ((record = in.readLine()) != null)

               if (laatste_record != null)
               {
                  String record_datum_tijd_minuten = laatste_record.substring(main.type_record_datum_tijd_begin_pos, main.type_record_datum_tijd_begin_pos + 12);  // bv 201302201345
                  Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
                  long system_sec = System.currentTimeMillis();

                  long timeDiff = Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

                  ///System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

                  //String record_pressure_present = null;
                  if (timeDiff <= 3L)      // max 3 minutes old
                  {
                     main.sensor_data_record_obs_pressure = laatste_record.substring(main.type_record_pressure_begin_pos, main.type_record_pressure_begin_pos + 7);

                     // rounding eg: 998.19 -> 998.2
                     //        double digitale_sensor_waarde = Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                     //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;  // bv 998.19 -> 998.2

                     if (main.tray_icon_clicked == true)
                     {   
                        System.out.println("--- sensor data record, pressure for tray icon: " + main.sensor_data_record_obs_pressure);
                     }
                     else
                     {
                        System.out.println("--- sensor data record, pressure for barometer form: " + main.sensor_data_record_obs_pressure);
                     }   
                  } // if (timeDiff <= 3L)
                  else
                  {
                     main.sensor_data_record_obs_pressure = "";
                     System.out.println("--- automatically retrieved barometer reading obsolete");
                  }

               } // if (laatste_record != null)

            } // try
            catch (IOException ex) 
            {  
               System.out.println("--- Function RS232_Read_Sensor_Data_PPPP_For_Obs(): " + ex);
            }

         } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
 
         // clear memory
         main.obs_file_datum_tijd      = null;

         return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) && (main.sensor_data_record_obs_pressure != null))
         {
            double hulp_double_pressure_reading;
            try
            {
               hulp_double_pressure_reading = Double.parseDouble(main.sensor_data_record_obs_pressure.trim());
               hulp_double_pressure_reading = Math.round(hulp_double_pressure_reading * 10) / 10.0d;  // bv 998.19 -> 998.2
            }
            catch (NumberFormatException e)
            {
               System.out.println("--- " + "Function RS232_Read_Sensor_Data_PPPP_For_Obs() " + e);
               hulp_double_pressure_reading = Double.MAX_VALUE;
            }   
             
            if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading < 1100.0))
            {
               if (main.tray_icon_clicked == true)
               {   
                  String pressure_sensor_height = "";
                  
                  // NB pressure at sensor height = pressure reading + ic
                  //double double_barometer_instrument_correction = Double.parseDouble(main.barometer_instrument_correction.trim());
                  double double_barometer_instrument_correction = 0.0;
                  try
                  {
                     double_barometer_instrument_correction = Double.parseDouble(main.barometer_instrument_correction.trim()); 
                  }
                  catch (NumberFormatException ex)
                  {
                     double_barometer_instrument_correction = 0.0;
                  }
                  
                  if ((double_barometer_instrument_correction > -4.0) && (double_barometer_instrument_correction < 4.0))
                  {        
                     //pressure_sensor_height = Double.toString(hulp_double_pressure_reading + double_barometer_instrument_correction);
                     // 1 digit precision
                     pressure_sensor_height = Double.toString(Math.round((hulp_double_pressure_reading + double_barometer_instrument_correction) * 10d) / 10d);
                  }
                  else
                  {
                     //pressure_sensor_height = Double.toString(hulp_double_pressure_reading);
                     // 1 digit precision
                     pressure_sensor_height = Double.toString(Math.round(hulp_double_pressure_reading * 10d) / 10d);
                  }                     
                  
                  main_RS232_RS422.cal_system_date_time = new GregorianCalendar(new SimpleTimeZone(0, "UTC")); // geeft systeem datum tijd in UTC van dit moment
                  main_RS232_RS422.cal_system_date_time.add(Calendar.MINUTE, -1);                              // averaged the data is 1 minute old
                  String date_time = main_RS232_RS422.sdf8.format(main_RS232_RS422.cal_system_date_time.getTime());  
                  
                  String info = "";
                  info = date_time + " UTC " + "\n" +
                                    "\n" +
                                    "pressure at sensor height: " + pressure_sensor_height + " hPa" + "\n";
                  //info = date_time + " UTC " + main.newline +
                  //       main.newline +
                  //       "pressure at sensor height: " + mybarometer.pressure_reading + " hPa" + main.newline;

                  main.trayIcon.displayMessage(main.APPLICATION_NAME, info, TrayIcon.MessageType.INFO);
               }
               else // barometer input screen opened
               {      
                  mybarometer.pressure_reading = Double.toString(hulp_double_pressure_reading);
                  
                  if (mybarometer.jTextField1 != null)
                  {
                     mybarometer.jTextField1.setText(mybarometer.pressure_reading); 
                     mybarometer.jTextField2.requestFocus();      // focus on "insert draft" textfield
                  }
               }    
            } // if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading < 1100.0))
         } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
         else // so (still) no sensor data available
         {
            if (main.tray_icon_clicked == true)
            {
               String info = "no sensor data available";
               main.trayIcon.displayMessage(main.APPLICATION_NAME, info, TrayIcon.MessageType.INFO);
            } // if (main.tray_icon_clicked == true)
            else
            {
               // temporary message box (barometer data obsolete) 
               final JOptionPane pane_end = new JOptionPane("automatically retrieved barometer reading obsolete", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
               final JDialog checking_ports_end_dialog = pane_end.createDialog(main.APPLICATION_NAME);

               Timer timer_end = new Timer(2500, new ActionListener()
               {
                  @Override
                  public void actionPerformed(ActionEvent e)
                  {
                     checking_ports_end_dialog.dispose();
                  }
               });
               timer_end.setRepeats(false);
               timer_end.start();
               checking_ports_end_dialog.setVisible(true);               
            }            
         } // else so (still) no sensor data available
      } // protected void done()

   }.execute(); // new SwingWorker <Void, Void>()

}
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/  
public static void RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR(final String destination, final boolean retry)
{
   // PTB220 or PTB330
   
   // NB 'destination' possible strings: - WOW
   //                                    - APR
   //                                    - MAIN_SCREEN
   
   
   if (main.RS232_connection_mode == 1)       // PTB220
   {
      main.type_record_lengte                 = main.RECORD_LENGTE_PTB220;
      main.type_record_datum_tijd_begin_pos   = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
      main.type_record_pressure_begin_pos     = main.RECORD_P_BEGIN_POS_PTB220;
      main.type_record_a_begin_pos            = main.RECORD_a_BEGIN_POS_PTB220;
      main.type_record_ppp_begin_pos          = main.RECORD_ppp_BEGIN_POS_PTB220;
   }
   else if (main.RS232_connection_mode == 2)  // PTB330   
   {
      main.type_record_lengte                 = main.RECORD_LENGTE_PTB330;
      main.type_record_datum_tijd_begin_pos   = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB330;
      main.type_record_pressure_begin_pos     = main.RECORD_P_BEGIN_POS_PTB330;
      main.type_record_a_begin_pos            = main.RECORD_a_BEGIN_POS_PTB330;
      main.type_record_ppp_begin_pos          = main.RECORD_ppp_BEGIN_POS_PTB330;
   }
   
   // initialisation
   //mybarometer.pressure_reading = "";
   
 
   new SwingWorker<String, Void>()
   {
      @Override
      protected String doInBackground() throws Exception
      {
         main.obs_file_datum_tijd = new GregorianCalendar();
         main.obs_file_datum_tijd.add(Calendar.MINUTE, -2);     // of is -1 ook goed????? : to be sure there was all time that it was written to the file
         File sensor_data_file;
         String record         = null;
         String laatste_record = null;
          

         // initialisation
         //main.sensor_data_record_obs_pressure = "";
         
         // initialisation
         //mybarometer.pressure_reading_corrected = "";
         //mybarometer.pressure_msl_corrected     = "";
         //mybarograph.pressure_amount_tendency   = "";
         //mybarograph.a_code                     = "";
         

         // determine sensor data file name
         String sensor_data_file_naam_datum_tijd_deel = main.sdf3.format(main.obs_file_datum_tijd.getTime()); // geeft bv 2013020308
         String sensor_data_file_name = "sensor_data_" + sensor_data_file_naam_datum_tijd_deel + ".txt";

         // first check if there is a sensor data file present (and not empty)
         String volledig_path_sensor_data = main.logs_dir + java.io.File.separator + sensor_data_file_name;

         sensor_data_file = new File(volledig_path_sensor_data);
         if (sensor_data_file.exists() && sensor_data_file.length() > 0)     // length() in bytes
         {
            try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
            {
               record         = null;
               laatste_record = null;

               while ((record = in.readLine()) != null)
               {
                  if (record.length() == main.type_record_lengte)
                  {
                     laatste_record = record;
                  } 
               } // while ((record = in.readLine()) != null)

               if (laatste_record != null)
               {
                  String record_datum_tijd_minuten = laatste_record.substring(main.type_record_datum_tijd_begin_pos, main.type_record_datum_tijd_begin_pos + 12);  // bv 201302201345
                  Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
                  long system_sec = System.currentTimeMillis();

                  long timeDiff = Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

                  if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA)      // max x minutes old
                  {
                     //sensor_data_record_WOW_APR_pressure_hpa = laatste_record.substring(main.type_record_pressure_begin_pos, main.type_record_pressure_begin_pos + 7);


                     // rounding eg: 998.19 -> 998.2
                     //        double digitale_sensor_waarde = Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                     //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;  // bv 998.19 -> 998.2

                     if (destination.equals("MAIN_SCREEN") == false)
                     {
                        //System.out.println("--- sensor data record, raw uncorrected pressure: " + sensor_data_record_WOW_APR_pressure_hpa);
                        System.out.println("--- last sensor data record: " + laatste_record);
                     }

                  } // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
                  else
                  {
                     laatste_record = null;

                     //sensor_data_record_WOW_APR_pressure_hpa = "";
                     System.out.println("--- automatically retrieved barometer reading obsolete");

                     if (destination.equals("MAIN_SCREEN") == false)
                     {
                        String message = "[WOW/APR] automatically retrieved barometer reading for WOW/APR obsolete (" + timeDiff + " minutes old)";
                        main.log_turbowin_system_message(message);
                     }
                  }
               } // if (laatste_record != null)

            } // try
            catch (IOException ex) 
            {  
               System.out.println("--- Function RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR(): " + ex);
            }

         } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
 
         // clear memory
         main.obs_file_datum_tijd      = null;
         
         // HMP155 connected? (2nd meteo instrument)
         //
         if (main.RS232_connection_mode_II == 1)
         {
            RS232_vaisala.RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument(destination);
         }
         
         //return sensor_data_record_WOW_APR_pressure_hpa;
         return laatste_record;
         
      } // protected String doInBackground() throws Exception
   
      @Override
      protected void done()
      {
         // initialisation
         mybarometer.pressure_reading_corrected = "";
         mybarometer.pressure_msl_corrected     = "";
         mybarograph.pressure_amount_tendency   = "";
         mybarograph.a_code                     = "";
         
         boolean a_ok = false;
         boolean ppp_ok = false;
         
         
         if (destination.equals("WOW"))
         {   
            
            JOptionPane.showMessageDialog(null, "WOW in TurboWin+ is disabled", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            
            
/*            
            String last_record = "";
            String sensor_data_record_WOW_pressure_MSL_inhg = "";
            String sensor_data_record_WOW_pressure_hpa = "";
            try 
            {
               //sensor_data_record_WOW_pressure_hpa = get();    // get the return value of the doInBackground()
               
               last_record = get();
               if ( (last_record != null) && (last_record.equals("") == false) )
               {
                  sensor_data_record_WOW_pressure_hpa = last_record.substring(main.type_record_pressure_begin_pos, main.type_record_pressure_begin_pos + 7);
               }
               else
               {
                  sensor_data_record_WOW_pressure_hpa = "";
               }
            } 
            catch (NullPointerException | InterruptedException | ExecutionException ex) 
            {
               sensor_data_record_WOW_pressure_hpa = "";
               String message = "[WOW] " + ex.toString();
               main.log_turbowin_system_message(message);
            }
         
            if ((sensor_data_record_WOW_pressure_hpa.compareTo("") != 0))
            {
               double hulp_double_pressure_reading;
               try
               {
                  hulp_double_pressure_reading = Double.parseDouble(sensor_data_record_WOW_pressure_hpa.trim());
                  //hulp_double_pressure_reading = Math.round(hulp_double_pressure_reading * 10) / 10.0d;  // bv 998.19 -> 998.2
               }
               catch (NumberFormatException e)
               {
                  System.out.println("--- " + "Function RS232_Vaisala_Read_Sensor_Data_For_WOW_APR() " + e);
                  hulp_double_pressure_reading = Double.MAX_VALUE;
               }   
             
               if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading < 1100.0))
               {
                  DecimalFormat df = new DecimalFormat("0.000");           // rounding only 3 decimals
               
                  // CONVERT TO MSL BECAUSE PRESSURE AT STATION HEIGHT NOT VISIBLE IN WOW!!! (+ apply barometer instrument correction)
                  double WOW_height_correction_pressure = main_RS232_RS422.RS232_WOW_APR_compute_air_pressure_height_correction(hulp_double_pressure_reading);
                  
                  String message_b = "[WOW] air pressure at sensor height = " + sensor_data_record_WOW_pressure_hpa + " hPa";
                  main.log_turbowin_system_message(message_b);
                  String message_hc = "[WOW] air pressure height corection = " + Double.toString(WOW_height_correction_pressure) + " hPa";
                  main.log_turbowin_system_message(message_hc);
                  String message_ic = "[WOW] air pressure instrument corection = " + main.barometer_instrument_correction + " hPa";
                  main.log_turbowin_system_message(message_ic);
               
                  if (WOW_height_correction_pressure > -50.0 && WOW_height_correction_pressure < 50.0)
                  {   
                     sensor_data_record_WOW_pressure_MSL_inhg = df.format((hulp_double_pressure_reading + WOW_height_correction_pressure + Double.parseDouble(main.barometer_instrument_correction)) * main_RS232_RS422.HPA_TO_INHG); 
                     main_RS232_RS422.RS232_Send_Sensor_Data_to_WOW(sensor_data_record_WOW_pressure_MSL_inhg);
                  }
                  else
                  {
                     String message_hce = "[WOW] computed height correction pressure not ok (" + WOW_height_correction_pressure + ")";
                     main.log_turbowin_system_message(message_hce);
                     sensor_data_record_WOW_pressure_MSL_inhg = "";
                  }
               } // if ((hulp_double_pressure_reading > 900.0) && (hulp_double_pressure_reading < 1100.0))
               else
               {
                  main.log_turbowin_system_message("[WOW] automatically retrieved barometer reading outside range");
                  sensor_data_record_WOW_pressure_MSL_inhg = "";
               }
            } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
            else // so (still) no sensor data available
            {
               main.log_turbowin_system_message("[WOW] automatically retrieved barometer reading obsolete or error during retrieving data");
               sensor_data_record_WOW_pressure_MSL_inhg = "";
            }
*/            
         } // if (destination.equals("WOW"))
         //else if (destination.equals("APR"))      // APR = Automated Pressure Reporting
         else if (destination.equals("APR") || destination.equals("MAIN_SCREEN"))      // APR = Automated Pressure Reporting   
         {
            String last_record = "";
            String sensor_data_record_APR_pressure_MSL_hpa = "";
            String sensor_data_record_APR_pressure_hpa = "";
         
            try 
            {
               //sensor_data_record_APR_pressure_hpa = get();    // get the return value of the doInBackground()
               last_record = get();
               if ( (last_record != null) && (last_record.equals("") == false) )
               {
                  sensor_data_record_APR_pressure_hpa = last_record.substring(main.type_record_pressure_begin_pos, main.type_record_pressure_begin_pos + 7);
                  //sensor_data_record_APR_a =
                  //sensor_data_record_APR_ppp =
                          
                  main.sensor_data_record_obs_ppp = last_record.substring(main.type_record_ppp_begin_pos, main.type_record_ppp_begin_pos + 5);
                  main.sensor_data_record_obs_a   = last_record.substring(main.type_record_a_begin_pos, main.type_record_a_begin_pos + 1);
               }
               else
               {
                  sensor_data_record_APR_pressure_hpa = "";
                  main.sensor_data_record_obs_a   = "";
                  main.sensor_data_record_obs_ppp = ""; 
               }
               
            } // try
            catch (NullPointerException | InterruptedException | ExecutionException ex) 
            {
               sensor_data_record_APR_pressure_hpa = "";
               main.sensor_data_record_obs_a   = "";
               main.sensor_data_record_obs_ppp = "";
               
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  String message = "[APR] " + ex.toString();
                  main.log_turbowin_system_message(message);
               }
            }
            
            
            //
            ////// a (air pressure characteristic) [APR]
            //
            a_ok = false;
            if ((main.sensor_data_record_obs_a.compareTo("") != 0) && (main.sensor_data_record_obs_a != null) && (main.sensor_data_record_obs_a.indexOf("*") == -1) )
            {
               int hulp_int_a_reading;
            
               try
               {
                  hulp_int_a_reading = Integer.parseInt(main.sensor_data_record_obs_a.trim());
               }
               catch (NumberFormatException e)
               {
                  hulp_int_a_reading = Integer.MAX_VALUE;
               }   
             
               if ((hulp_int_a_reading >= 0) && (hulp_int_a_reading <= 8))
               {
                  mybarograph.a_code = main.sensor_data_record_obs_a;
                  
                  if (destination.equals("MAIN_SCREEN") == false)
                  {
                     String message_pressure_a = "[APR] pressure characteristic at sensor height = " + mybarograph.a_code + " (code)";
                     main.log_turbowin_system_message(message_pressure_a);
                  }
                     
                  // make IMMT and FM13 ready [APR]   
                  RS232_make_pressure_a_APR_FM13_IMMT_ready();
                  
                  // for every minute update main screen
                  a_ok = true;
               }
            } // if ((main.sensor_data_record_obs_a.compareTo("") != 0) etc. 
            
            
            //
            ////// ppp (air pressure tendency) [APR]
            //
            ppp_ok = false;
            if ( (main.sensor_data_record_obs_ppp.compareTo("") != 0) && (main.sensor_data_record_obs_ppp != null) && (main.sensor_data_record_obs_ppp.indexOf("*") == -1) )
            {
               double hulp_double_ppp_reading;
               try
               {
                  hulp_double_ppp_reading = Double.parseDouble(main.sensor_data_record_obs_ppp.trim());
                  hulp_double_ppp_reading = Math.round(hulp_double_ppp_reading * 10) / 10.0d;  // bv 13.19 -> 13.2
               }
               catch (NumberFormatException e)
               {
                  //System.out.println("--- " + "Function RS232_Read_Sensor_Data_a_ppp_For_Obs() " + e);
                  hulp_double_ppp_reading = Double.MAX_VALUE;
               }   
             
               if ((hulp_double_ppp_reading > -99.9) && (hulp_double_ppp_reading < 99.9))
               {
                  // tendency
                  mybarograph.pressure_amount_tendency = Double.toString(Math.abs(hulp_double_ppp_reading));  // only pos value in this field
    
                  if (destination.equals("MAIN_SCREEN") == false)
                  {
                     String message_pressure_ppp = "[APR] pressure tendency at sensor height = " + mybarograph.pressure_amount_tendency + " hPa";
                     main.log_turbowin_system_message(message_pressure_ppp);
                  }
                     
                  // make IMMT and FM13 ready [APR]   
                  RS232_make_pressure_ppp_APR_FM13_IMMT_ready();   
                  
                  // for every minute update main screen
                  ppp_ok = true;
               }
            } // if ((main.sensor_data_record_obs_pressure.compareTo("") != 0) etc.
            
            // update main screen barograph data (a, ppp) every minute
            if (destination.equals("MAIN_SCREEN") == true)
            {
               if (a_ok || ppp_ok)
               {
                  main.barograph_fields_update();
               }
            }

            
            //
            /////// air pressure [APR] ////
            //
            if ((sensor_data_record_APR_pressure_hpa.compareTo("") != 0))
            {
               double hulp_double_APR_pressure_reading;
               try
               {
                  hulp_double_APR_pressure_reading = Double.parseDouble(sensor_data_record_APR_pressure_hpa.trim());
                  //hulp_double_pressure_reading = Math.round(hulp_double_pressure_reading * 10) / 10.0d;  // bv 998.19 -> 998.2
               }
               catch (NumberFormatException e)
               {
                  System.out.println("--- " + "Function RS232_Vaisala_Read_Sensor_Data_For_WOW_APR() " + e);
                  hulp_double_APR_pressure_reading = Double.MAX_VALUE;
               }   
             
               if ((hulp_double_APR_pressure_reading > 900.0) && (hulp_double_APR_pressure_reading < 1100.0))
               {
                  DecimalFormat df = new DecimalFormat("0.0");           // rounding only 1 decimal
               
                  // CONVERT TO MSL (+ apply barometer instrument correction)
                  double APR_height_correction_pressure = main_RS232_RS422.RS232_WOW_APR_compute_air_pressure_height_correction(hulp_double_APR_pressure_reading);
                  
                  if (destination.equals("MAIN_SCREEN") == false)
                  {
                     String message_b = "[APR] air pressure at sensor height = " + sensor_data_record_APR_pressure_hpa + " hPa";
                     main.log_turbowin_system_message(message_b);
                     String message_hc = "[APR] air pressure height correction = " + Double.toString(APR_height_correction_pressure) + " hPa";
                     main.log_turbowin_system_message(message_hc);
                     String message_ic = "[APR] air pressure instrument correction = " + main.barometer_instrument_correction + " hPa";
                     main.log_turbowin_system_message(message_ic);
                  }
               
                  if (APR_height_correction_pressure > -50.0 && APR_height_correction_pressure < 50.0)
                  {   
                     // ic correction (make it 0.0 if outside the range)
                     double double_barometer_instrument_correction = 0.0;
                     
                     if (main.barometer_instrument_correction.equals("") == false)
                     {
                        try
                        {
                           double_barometer_instrument_correction = Double.parseDouble(main.barometer_instrument_correction);
               
                           if (!(double_barometer_instrument_correction >= -4.0 && double_barometer_instrument_correction  <= 4.0))
                           {
                              double_barometer_instrument_correction = 0.0;
                           }
                        }
                        catch (NumberFormatException e) 
                        {
                           double_barometer_instrument_correction = 0.0;
                        }               
                     }
   
                     //DecimalFormat df = new DecimalFormat("0.0");           // rounding only 1 decimal
                     
                     ////////////////// barometer reading (pressure at sensor height + ic) ////////////////
                     //
                     String sensor_data_record_APR_pressure_reading_hpa_corrected = df.format(hulp_double_APR_pressure_reading + double_barometer_instrument_correction); 
                     mybarometer.pressure_reading_corrected = sensor_data_record_APR_pressure_reading_hpa_corrected;   // now pressure at sensor height corrected for ic
   
   
                     ///////////////// pressure at MSL (+ ic) ///////////
                     //
                     //sensor_data_record_APR_pressure_MSL_hpa = df.format(hulp_double_APR_pressure_reading + APR_height_correction_pressure + double_barometer_instrument_correction); 
                     double hulp_double_APR_pressure_MSL_not_rounded = hulp_double_APR_pressure_reading + APR_height_correction_pressure + double_barometer_instrument_correction;
                     BigDecimal bd = new BigDecimal(hulp_double_APR_pressure_MSL_not_rounded).setScale(2, RoundingMode.HALF_UP);
                     double hulp_double_APR_pressure_MSL_rounded = bd.doubleValue();
                     sensor_data_record_APR_pressure_MSL_hpa = Double.toString(hulp_double_APR_pressure_MSL_rounded);
                     mybarometer.pressure_msl_corrected = sensor_data_record_APR_pressure_MSL_hpa;   // sensor_data_record_APR_pressure_MSL_hpa the baromter instrument correction is included
                     
                     if (destination.equals("MAIN_SCREEN") == false)
                     {
                        String message_msl = "[APR] air pressure MSL = " + mybarometer.pressure_msl_corrected + " hPa";
                        main.log_turbowin_system_message(message_msl);
                     }
                     
                     // make IMMT (and FM13) ready (but not if only data was retrieved for displaying on main screen)
                     //
                     if (destination.equals("MAIN_SCREEN") == false)
                     {
                        RS232_make_pressure_APR_FM13_IMMT_ready();
                     }
                     
                     
                     // send the data (but not if only data was retrieved for displaying on main screen)
                     //
                     if (destination.equals("MAIN_SCREEN") == false)
                     {
                        RS232_APR_AWSR_send(retry);
                     }                
                     
                     // update the barometer fields on main screen
                     //
                     if (destination.equals("MAIN_SCREEN") == true)
                     {
                        main.barometer_fields_update();
                     }
                  }
                  else
                  {
                     if (destination.equals("MAIN_SCREEN") == false)
                     {
                        String message_hce = "[APR] computed height correction pressure not ok (" + APR_height_correction_pressure + ")";
                        main.log_turbowin_system_message(message_hce);
                     }
                     sensor_data_record_APR_pressure_MSL_hpa = "";
                  }            
               } // if ((hulp_double_APR_pressure_reading > 900.0) && (hulp_double_APR_pressure_reading < 1100.0))
               else
               {
                  if (destination.equals("MAIN_SCREEN") == false)
                  {
                     main.log_turbowin_system_message("[APR] automatically retrieved barometer reading outside range");
                  }
                  sensor_data_record_APR_pressure_MSL_hpa = "";
               }
            } //  if ((sensor_data_record_APR_pressure_hpa.compareTo("") != 0))
            else
            {
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  main.log_turbowin_system_message("[APR] automatically retrieved barometer reading obsolete or error during retrieving data");
               }
               sensor_data_record_APR_pressure_MSL_hpa = "";
            }
         } // else if (destination.equals("APR"))
      } // protected void done()

   }.execute(); // new SwingWorker <String, Void>()

}
   
   
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs()
{
   // NB this function for manual obs (inserting data on the temperatures input form)
   // called from: initSynopparameters() [mytemp.java]
   
   // NB see also - Read_Sensor_Data_Files_For_Air_Temp_Graph_HMP155() [RS232_view.java]
   //             - RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR() [RS232_vaisala.java]
   
   // example saved record:
   //
   // ___22.98____17.77____14.77____60.09_201909041422 
   // 3x      4x       4x       4x       1x spaces
   //
   
   
   // initialisation
   mytemp.air_temp                     = "";           // if not ="" -> nullpointerexception if update button clicked on main page
   mytemp.wet_bulb_temp                = "";
   mytemp.RH                           = "";           // range 0 - 100
   mytemp.double_dew_point             = main.INVALID;
   mytemp.double_rv                    = main.INVALID; // range 0.0 - 1.0 (or invalid) !!!!
           
 
   new SwingWorker<Boolean, Void>()
   {
      @Override
      protected Boolean doInBackground() throws Exception
      {
         Boolean laatste_record_ok = false;
         main.obs_file_datum_tijd = new GregorianCalendar();
         main.obs_file_datum_tijd.add(Calendar.MINUTE, -2);     // of is -1 ook goed????? : to be sure there was all time that it was written to the file
         File sensor_data_file;
         String record = null;
         String laatste_record = null;
         String local_dew_point = "";


         // determine sensor data file name
         String sensor_data_file_naam_datum_tijd_deel = main.sdf3.format(main.obs_file_datum_tijd.getTime()); // e.g. 2013020308
         String sensor_data_file_name = "sensor_data_II_" + sensor_data_file_naam_datum_tijd_deel + ".txt";
         
         // first check if there is a sensor data file present (and not empty)
         String volledig_path_sensor_data = main.logs_dir + java.io.File.separator + sensor_data_file_name;
         //System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

         sensor_data_file = new File(volledig_path_sensor_data);
         if (sensor_data_file.exists() && sensor_data_file.length() > 0)     // length() in bytes
         {
            try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
            {
               record         = null;
               laatste_record = null;

               // retrieve the last record in the sensor data file
               //
               while ((record = in.readLine()) != null)
               {
                  laatste_record = record;
               } // while ((record = in.readLine()) != null)
                  
               // check on minimum record length
               //
               if (laatste_record != null)
               {
                  if (laatste_record.length() != main.RECORD_LENGTE_HMP155)       // fixed record length
                  {
                     System.out.println("--- Vaisala HMP155 format (last record length = " + laatste_record.length() + "; should be " + main.RECORD_LENGTE_HMP155 + ") last retrieved record NOT ok (file: " + volledig_path_sensor_data + ")"); 
                     laatste_record = null;
                  }
               }
            
               // last retrieved record ok
               if (laatste_record != null)
               {
                  int pos = laatste_record.length() -12;                                               // pos is now start position of YYYYMMDDHHmm
                  String record_datum_tijd_minuten = laatste_record.substring(pos, pos + 12);          // YYYYMMDDHHmm has length 12
                     
                  Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
                  long system_sec = System.currentTimeMillis();

                  long timeDiff = Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes
            
                  if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA)      // max ? minutes old
                  {
                     //   21.54    16.00    12.43    56.29 201909070722                           
                     int pos1 = 0;            // air temp, start pos
                     int pos2 = 9;            // Twet, start pos
                     int pos3 = 18;           // tdew, start pos
                     int pos4 = 27;           // RH, start pos
                           
                     // rounding eg: 998.19 -> 998.2
                     //        double digitale_sensor_waarde = Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                     //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;  // bv 998.19 -> 998.2
                        
                     mytemp.air_temp      = laatste_record.substring(pos1, pos1 + 8);
                     mytemp.wet_bulb_temp = laatste_record.substring(pos2, pos2 + 8);
                     local_dew_point      = laatste_record.substring(pos3, pos3 + 8);
                     mytemp.RH            = laatste_record.substring(pos4, pos4 + 8);
                           
                     System.out.println("--- sensor data record, air temp for obs: " + mytemp.air_temp);
                     System.out.println("--- sensor data record, wet bulb temp for obs: " + mytemp.wet_bulb_temp);
                     System.out.println("--- sensor data record, dewpoint for obs: " + local_dew_point);
                     System.out.println("--- sensor data record, RH for obs: " + mytemp.RH);
                           
                     laatste_record_ok = true;
                    
                  } // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
                  else
                  {
                     mytemp.air_temp         = "";
                     mytemp.wet_bulb_temp    = "";
                     local_dew_point         = ""; 
                     mytemp.RH               = "";
                     mytemp.double_rv        = main.INVALID;
                     mytemp.double_dew_point = main.INVALID;
                        
                     laatste_record_ok = false;
                  } // else
                     
               } // if (laatste_record != null)
            
            } // try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
         } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
         
         // clear memory
         main.obs_file_datum_tijd      = null;
         
         if (laatste_record_ok == true)
         {
            // dewpoint
            //
            mytemp.double_dew_point = main.INVALID;                 
            if ( (local_dew_point.compareTo("") != 0) && (local_dew_point != null) && (local_dew_point.indexOf("*") == -1) )
            {
               try
               {
                  mytemp.double_dew_point = Double.parseDouble(local_dew_point.trim());
                  
                  if (!(mytemp.double_dew_point > -70.0) && (mytemp.double_dew_point < 70.0))
                  {
                     mytemp.double_dew_point = main.INVALID;
                  }
               }
               catch (NumberFormatException e)
               {
                  System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                  mytemp.double_dew_point = main.INVALID;
               
                  //laatste_record_ok = false;
               }  // catch
            } // if
         } // if (laatste_record_ok == true)
         
         
         return laatste_record_ok;
         
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         String error_info = "";
         double hulp_air_temp = 999999;                 // 999999 = random number but > 99.9
         double hulp_wet_bulb_temp = 999999;            // 999999 = random number but > 99.9
         double hulp_RH = 999999;                       // 999999 = random number but > 99.9
         
         try 
         {
            Boolean return_laatste_record_ok = get();
               
            if (return_laatste_record_ok)
            {
               // air temp check
               //
               if ( (mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null) && (mytemp.air_temp.indexOf("*") == -1) )
               {
                  try
                  {
                     hulp_air_temp = Double.parseDouble(mytemp.air_temp.trim());
                     if (!(hulp_air_temp > -99.9) && (hulp_air_temp < 99.9))
                     {
                        mytemp.air_temp = "";
                     }
                  }
                  catch (NumberFormatException e)
                  {
                     System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                     mytemp.air_temp = "";
                  }
               }
               
               // wet bulb temp check
               //
               //double hulp_wet_bulb_temp = 999999;                 // 999999 = random number but > 99.9
               if ( (mytemp.wet_bulb_temp.compareTo("") != 0) && (mytemp.wet_bulb_temp != null) && (mytemp.wet_bulb_temp.indexOf("*") == -1) )
               {
                  try
                  {
                     hulp_wet_bulb_temp = Double.parseDouble(mytemp.wet_bulb_temp.trim());
                     if (!(hulp_wet_bulb_temp > -99.9) && (hulp_wet_bulb_temp < 99.9))
                     {
                        mytemp.wet_bulb_temp = "";
                     }
                  }
                  catch (NumberFormatException e)
                  {
                     System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                     mytemp.wet_bulb_temp = "";
                  }
               }
               
               // RH check
               //
               if ( (mytemp.RH.compareTo("") != 0) && (mytemp.RH != null) && (mytemp.RH.indexOf("*") == -1) )
               {
                  try
                  {
                     hulp_RH = Double.parseDouble(mytemp.RH.trim());
                     mytemp.double_rv = hulp_RH / 100;                    // NB mytemp.double_rv in range 0.0 - 1.0 [see also mytemp.java] !!!
                     if (!(hulp_RH > 0.0) && (hulp_air_temp <= 100.0))
                     {
                        mytemp.RH = "";
                        mytemp.double_rv = main.INVALID;
                     }
                  }
                  catch (NumberFormatException e)
                  {
                     System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() " + e);
                     mytemp.RH = "";
                     mytemp.double_rv = main.INVALID;
                  }
               }
               
            } // if (return_laatste_record_ok)
            else
            {
               error_info = "automatically retrieved temperatures sensor data reading obsolete or format error";
               mytemp.air_temp         = "";
               mytemp.RH               = "";
               mytemp.wet_bulb_temp    = "";
               mytemp.double_dew_point = main.INVALID;
               mytemp.double_rv        = main.INVALID;
            }
         } // try
         catch (InterruptedException | ExecutionException ex) 
         {
            error_info = "error retrieving air temp and RH data (" + ex + ")";
            mytemp.air_temp         = "";
            mytemp.RH               = "";
            mytemp.wet_bulb_temp    = "";
            mytemp.double_dew_point = main.INVALID;
            mytemp.double_rv        = main.INVALID;
         }
         
         // general error (air temp and RH no value but error_info not yet set)
         if (mytemp.air_temp.equals("") && mytemp.RH.equals("") && error_info.equals(""))
         {
            error_info = "automatically retrieved air temp and RH data not available";
            mytemp.air_temp         = "";
            mytemp.RH               = "";
            mytemp.wet_bulb_temp    = "";
            mytemp.double_dew_point = main.INVALID;
            mytemp.double_rv        = main.INVALID;
         }
         
         if (!error_info.equals(""))
         {
            System.out.println("--- " + error_info);
            
            final JOptionPane pane = new JOptionPane(error_info, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            final JDialog dialog = pane.createDialog(main.APPLICATION_NAME);

            Timer timer = new Timer(2500, new ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  dialog.dispose();
               }
            });
            timer.setRepeats(false);
            timer.start();
            dialog.setVisible(true);   
         } // if (!error_info.equals(""))
         else // no error encountered
         {
            mytemp.jTextField1.setText(mytemp.air_temp);      // air temp
            mytemp.jTextField2.setText(mytemp.wet_bulb_temp); // wet-bulb temp
            
            // NB default is wet-bulb not frozen (so if no info available then by default the "wet-bulb not frozen" button will be checked) 
            if (hulp_wet_bulb_temp < 0.0)
            {
                mytemp.jRadioButton4.setSelected(true);       // wet-bulb frozen radio button
            }
            else
            {
                mytemp.jRadioButton3.setSelected(true);       // wet-bulb not frozen radio button
            }
            
            mytemp.jTextField4.setText(mytemp.RH);            // RH
         } // else
      }
   }.execute(); // new SwingWorker <Void, Void>()   
}

   
   
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR(final String destination, final boolean retry)        
{
   // NB this function for temperatures via APR WITH NO 1ST INSTRUMENT (BAROMETER) CONNECTED!
   
   // NB 'destination' possible strings: - WOW
   //                                    - APR
   //                                    - MAIN_SCREEN
   
   // NB see also - Read_Sensor_Data_Files_For_Air_Temp_Graph_HMP155() [main_RS232_RS422.java]
   //             - RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() [RS232_vaisla.java]
   //             - RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() [RS232_vaisala.java]
   
   // example saved record:
   //
   // ___22.98____17.77____14.77____60.09_201909041422 
   // 3x      4x       4x       4x       1x spaces
   //
   
   
   // initialisation
   mytemp.air_temp                     = "";           // if not ="" -> nullpointerexception if update button clicked on main page
   mytemp.wet_bulb_temp                = "";
   mytemp.RH                           = "";           // range 0 - 100
   mytemp.double_dew_point             = main.INVALID;
   mytemp.double_rv                    = main.INVALID; // range 0.0 - 1.0 (or invalid) !!!!
           
 
   new SwingWorker<Boolean, Void>()
   {
      @Override
      protected Boolean doInBackground() throws Exception
      {
         Boolean laatste_record_ok = false;
         main.obs_file_datum_tijd = new GregorianCalendar();
         main.obs_file_datum_tijd.add(Calendar.MINUTE, -2);     // of is -1 ook goed????? : to be sure there was all time that it was written to the file
         File sensor_data_file;
         String record = null;
         String laatste_record = null;
         String local_dew_point = "";


         // determine sensor data file name
         String sensor_data_file_naam_datum_tijd_deel = main.sdf3.format(main.obs_file_datum_tijd.getTime()); // e.g. 2013020308
         String sensor_data_file_name = "sensor_data_II_" + sensor_data_file_naam_datum_tijd_deel + ".txt";
         
         // first check if there is a sensor data file present (and not empty)
         String volledig_path_sensor_data = main.logs_dir + java.io.File.separator + sensor_data_file_name;
         //System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

         sensor_data_file = new File(volledig_path_sensor_data);
         if (sensor_data_file.exists() && sensor_data_file.length() > 0)     // length() in bytes
         {
            try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
            {
               record         = null;
               laatste_record = null;

               // retrieve the last record in the sensor data file
               //
               while ((record = in.readLine()) != null)
               {
                  laatste_record = record;
               } // while ((record = in.readLine()) != null)
                  
               // check on minimum record length
               //
               if (laatste_record != null)
               {
                  if (laatste_record.length() != main.RECORD_LENGTE_HMP155)       // fixed record length
                  {
                     System.out.println("--- Vaisala HMP155 format (last record length = " + laatste_record.length() + "; should be " + main.RECORD_LENGTE_HMP155 + ") last retrieved record NOT ok (file: " + volledig_path_sensor_data + ")"); 
                     laatste_record = null;
                  }
               }
            
               // last retrieved record ok
               if (laatste_record != null)
               {
                  int pos = laatste_record.length() -12;                                               // pos is now start position of YYYYMMDDHHmm
                  String record_datum_tijd_minuten = laatste_record.substring(pos, pos + 12);          // YYYYMMDDHHmm has length 12
                     
                  Date file_date = main.sdf4.parse(record_datum_tijd_minuten);
                  long system_sec = System.currentTimeMillis();

                  long timeDiff = Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes
            
                  if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA)      // max ? minutes old
                  {
                     //   21.54    16.00    12.43    56.29 201909070722                           
                     int pos1 = 0;            // air temp, start pos
                     int pos2 = 9;            // Twet, start pos
                     int pos3 = 18;           // tdew, start pos
                     int pos4 = 27;           // RH, start pos
                           
                     // rounding eg: 998.19 -> 998.2
                     //        double digitale_sensor_waarde = Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                     //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;  // bv 998.19 -> 998.2
                        
                     mytemp.air_temp      = laatste_record.substring(pos1, pos1 + 8);
                     mytemp.wet_bulb_temp = laatste_record.substring(pos2, pos2 + 8);
                     local_dew_point      = laatste_record.substring(pos3, pos3 + 8);
                     mytemp.RH            = laatste_record.substring(pos4, pos4 + 8);
                     
                     // skip all spaves
                     mytemp.air_temp      = mytemp.air_temp.trim();
                     mytemp.wet_bulb_temp = mytemp.wet_bulb_temp.trim();
                     local_dew_point      = local_dew_point.trim();
                     mytemp.RH            = mytemp.RH.trim();      

                           
                     if (destination.equals("MAIN_SCREEN") == false)
                     {
                        System.out.println("--- sensor data record, air temp for obs: " + mytemp.air_temp);
                        System.out.println("--- sensor data record, wet bulb temp for obs: " + mytemp.wet_bulb_temp);
                        System.out.println("--- sensor data record, dewpoint for obs: " + local_dew_point);
                        System.out.println("--- sensor data record, RH for obs: " + mytemp.RH);
                     }
                     
                     laatste_record_ok = true;
                    
                  } // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
                  else
                  {
                     mytemp.air_temp         = "";
                     mytemp.wet_bulb_temp    = "";
                     local_dew_point         = ""; 
                     mytemp.RH               = "";
                     mytemp.double_rv        = main.INVALID;
                     mytemp.double_dew_point = main.INVALID;
                        
                     laatste_record_ok = false;
                  } // else
                     
               } // if (laatste_record != null)
            
            } // try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
         } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
         
         // clear memory
         main.obs_file_datum_tijd      = null;
         
         if (laatste_record_ok == true)
         {
            // dewpoint (APR)
            //
            mytemp.double_dew_point = main.INVALID;                 
            if ( (local_dew_point.compareTo("") != 0) && (local_dew_point != null) && (local_dew_point.indexOf("*") == -1) )
            {
               try
               {
                  mytemp.double_dew_point = Double.parseDouble(local_dew_point.trim());
                  
                  if (!(mytemp.double_dew_point > -70.0) && (mytemp.double_dew_point < 70.0))
                  {
                     mytemp.double_dew_point = main.INVALID;
                  }
                  else
                  {
                     // make IMMT (and FM13) ready   
                     RS232_make_dew_point_APR_FM13_IMMT_ready();
                  }   
               }
               catch (NumberFormatException e)
               {
                  System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs_APR() " + e);
                  mytemp.double_dew_point = main.INVALID;
               
                  //laatste_record_ok = false;
               }  // catch
            } // if
         } // if (laatste_record_ok == true)
         
         
         return laatste_record_ok;
         
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         String error_info = "";
         double hulp_air_temp = 999999;                 // 999999 = random number but > 99.9
         double hulp_wet_bulb_temp = 999999;            // 999999 = random number but > 99.9
         double hulp_RH = 999999;                       // 999999 = random number but > 99.9
         
         try 
         {
            Boolean return_laatste_record_ok = get();
               
            if (return_laatste_record_ok)
            {
               // air temp check (APR)
               //
               if ( (mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null) && (mytemp.air_temp.indexOf("*") == -1) )
               {
                  try
                  {
                     hulp_air_temp = Double.parseDouble(mytemp.air_temp.trim());
                     if (!(hulp_air_temp > -99.9) && (hulp_air_temp < 99.9))
                     {
                        mytemp.air_temp = "";
                     }
                     else
                     {
                        // make IMMT (and FM13) ready   
                        RS232_make_air_temp_APR_FM13_IMMT_ready();
                     }
                  }
                  catch (NumberFormatException e)
                  {
                     System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs_APR() " + e);
                     mytemp.air_temp = "";
                  }
               }
               
               // wet bulb temp check (APR)
               //
               if ( (mytemp.wet_bulb_temp.compareTo("") != 0) && (mytemp.wet_bulb_temp != null) && (mytemp.wet_bulb_temp.indexOf("*") == -1) )
               {
                  try
                  {
                     hulp_wet_bulb_temp = Double.parseDouble(mytemp.wet_bulb_temp.trim());
                     if (!(hulp_wet_bulb_temp > -99.9) && (hulp_wet_bulb_temp < 99.9))
                     {
                        mytemp.wet_bulb_temp = "";
                     }
                     else
                     {
                        // make IMMT (and FM13) ready   
                        RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready();
                     }
                  }
                  catch (NumberFormatException e)
                  {
                     System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs+APR() " + e);
                     mytemp.wet_bulb_temp = "";
                  }
               }
               
               // RH check (APR) [NB used in IMMT but not used in FM13)
               //
               if ( (mytemp.RH.compareTo("") != 0) && (mytemp.RH != null) && (mytemp.RH.indexOf("*") == -1) )
               {
                  try
                  {
                     hulp_RH = Double.parseDouble(mytemp.RH.trim());
                     mytemp.double_rv = hulp_RH / 100;                    // NB mytemp.double_rv in range 0.0 - 1.0 [see also mytemp.java] !!!
                     if (!(hulp_RH > 0.0) && (hulp_air_temp <= 100.0))
                     {
                        mytemp.RH = "";
                        mytemp.double_rv = main.INVALID;
                     }
                     else
                     {
                        // make IMMT ready (NB RH is not used in FM13)
                        RS232_make_RH_APR_FM13_IMMT_ready(); 
                     }   
                  }
                  catch (NumberFormatException e)
                  {
                     System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs_APR() " + e);
                     mytemp.RH = "";
                     mytemp.double_rv = main.INVALID;
                  }
               }
               
            } // if (return_laatste_record_ok)
            else
            {
               error_info = "automatically retrieved temperatures sensor data reading obsolete or format error";
               mytemp.air_temp         = "";
               mytemp.RH               = "";
               mytemp.wet_bulb_temp    = "";
               mytemp.double_dew_point = main.INVALID;
               mytemp.double_rv        = main.INVALID;
            }
         } // try
         catch (InterruptedException | ExecutionException ex) 
         {
            error_info = "error retrieving air temp and RH data (" + ex + ")";
            mytemp.air_temp         = "";
            mytemp.RH               = "";
            mytemp.wet_bulb_temp    = "";
            mytemp.double_dew_point = main.INVALID;
            mytemp.double_rv        = main.INVALID;
         }
         
         // general error (air temp and RH no value but error_info not yet set)
         if (mytemp.air_temp.equals("") && mytemp.RH.equals("") && error_info.equals(""))
         {
            error_info = "automatically retrieved air temp and RH data not available";
            mytemp.air_temp         = "";
            mytemp.RH               = "";
            mytemp.wet_bulb_temp    = "";
            mytemp.double_dew_point = main.INVALID;
            mytemp.double_rv        = main.INVALID;
         }
         
         
         if (destination.equals("APR"))      // APR = Automated Pressure Reporting
         {
            if (mytemp.air_temp.equals("") == true)
            {
               // if air temp is not available do not send the data
               main.log_turbowin_system_message("[APR] " + error_info);
            }
            else
            {
               // send the data
               //
               main_RS232_RS422.RS232_APR_AWSR_send(retry);
            } // else
         }
         
         if (destination.equals("MAIN_SCREEN") == true)
         {
            main.temperatures_fields_update();
         }

      } // protected void done()
   }.execute(); // new SwingWorker <Void, Void>()   
   
   
}
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument(final String destination)
{
   // called from: - RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR()
   //              - RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR()
   
   
   // NB this function for temperatures via APR WITH ALSO A 1ST INSTRUMENT (BAROMETER) CONNECTED!
   
   // NB see also: RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR [RS232_vaisala.java] {no 1st instrument connected)
   // NB the difference with: RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs() is the use of a SwingWorker
   //    in APR in 2nd instrument mode this function will be invoked from within a SwingWorker of a barometer data retrieving function
   //    + in this function the second last record is also taken into consideration
   
   // example saved record:
   //
   // ___22.98____17.77____14.77____60.09_201909041422 
   // 3x      4x       4x       4x       1x spaces
   //
   
   
   // initialisation
   mytemp.air_temp                     = "";           // if not ="" -> nullpointerexception if update button clicked on main page
   mytemp.wet_bulb_temp                = "";
   mytemp.RH                           = "";           // range 0 - 100
   mytemp.double_dew_point             = main.INVALID;
   mytemp.double_rv                    = main.INVALID; // range 0.0 - 1.0 (or invalid) !!!!


   Boolean laatste_record_ok = false;
   main.obs_file_datum_tijd = new GregorianCalendar();
   main.obs_file_datum_tijd.add(Calendar.MINUTE, -2);     // of is -1 ook goed????? : to be sure there was all time that it was written to the file
   File sensor_data_file;
   String record = null;
   String record_0 = null;                               // last retrieved record
   String record_1 = null;                               // second last retrieved record (Dutch: een-na-laatste)
   String laatste_record = null;
   String local_dew_point = "";
   
   int error_code = 0;                                    // 0: no error
                                                          // 1: sensor data file not present
                                                          // 2: last retrieved record; record length not ok
                                                          // 3: last retrieved record obsolete
                                                          // 4: error when determining file date of sensor data file
                                                          // 5: error when opening sensor data file


   // determine sensor data file name
   String sensor_data_file_naam_datum_tijd_deel = main.sdf3.format(main.obs_file_datum_tijd.getTime()); // e.g. 2013020308
   String sensor_data_file_name = "sensor_data_II_" + sensor_data_file_naam_datum_tijd_deel + ".txt";
         
   // first check if there is a sensor data file present (and not empty)
   String volledig_path_sensor_data = main.logs_dir + java.io.File.separator + sensor_data_file_name;
   //System.out.println("+++ te openen file voor obs: "+ volledig_path_sensor_data);

   sensor_data_file = new File(volledig_path_sensor_data);
   if (sensor_data_file.exists() && sensor_data_file.length() > 0)     // length() in bytes
   {
      try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
      {
         record         = null;
         record_0       = null;                     // last retrieved record
         record_1       = null;                     // second last retrieved record
         laatste_record = null;                     // last record which is checked with ok result (most of the times record_0 but sometimes record_1)

         
         // retrieve the last and second last records in the sensor data file
         //
         while ((record = in.readLine()) != null)
         {
            record_1 = record_0;
            record_0 = record;
         } // while ((record = in.readLine()) != null)
                  
         // check on minimum record length last retrieved record (record_0)
         //
         if (record_0 != null)
         {
            if (record_0.length() != main.RECORD_LENGTE_HMP155)       // fixed record length
            {
               System.out.println("--- Vaisala HMP155 format (last record length = " + record_0.length() + "; should be " + main.RECORD_LENGTE_HMP155 + ") last retrieved record NOT ok (file: " + volledig_path_sensor_data + ")"); 
               record_0 = null;
            }
         }
         
         // check on minimum record length second last retrieved record (record_1) if last record was NOT ok
         //
         if ((record_0 == null) && (record_1 != null))
         {
            if (record_1.length() != main.RECORD_LENGTE_HMP155)       // fixed record length
            {
               System.out.println("--- Vaisala HMP155 format (second last record length = " + record_1.length() + "; should be " + main.RECORD_LENGTE_HMP155 + ") last retrieved record NOT ok (file: " + volledig_path_sensor_data + ")"); 
               error_code = 2;
               record_1 = null;
            }
            else
            {
               System.out.println("--- Vaisala HMP155 format second last record length = ok (= alternative for last record)"); 

            }
         }         
         
         // determine 'laatste_record' (will be record_0 or record_1)
         //
         if (record_0 != null)
         {
            laatste_record = record_0;          // last retrieved record
         }
         else if (record_1 != null)
         {
            laatste_record = record_1;          // second last retrieved record
         }
         else                                   // last and second last record both NOT ok
         {
            laatste_record = null;
         }
         
            
         // last (or second last) retrieved record ok
         if (laatste_record != null)
         {
            int pos = laatste_record.length() -12;                                               // pos is now start position of YYYYMMDDHHmm
            String record_datum_tijd_minuten = laatste_record.substring(pos, pos + 12);          // YYYYMMDDHHmm has length 12
                     
            Date file_date;
            try 
            {
               file_date = main.sdf4.parse(record_datum_tijd_minuten);
            
               long system_sec = System.currentTimeMillis();

               long timeDiff = Math.abs(file_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes
            
               if (timeDiff <= main_RS232_RS422.TIMEDIFF_SENSOR_DATA)      // max ? minutes old
               {
                  //   21.54    16.00    12.43    56.29 201909070722                           
                  int pos1 = 0;            // air temp, start pos
                  int pos2 = 9;            // Twet, start pos
                  int pos3 = 18;           // tdew, start pos
                  int pos4 = 27;           // RH, start pos
                           
                  // rounding eg: 998.19 -> 998.2
                  //        double digitale_sensor_waarde = Double.parseDouble(RS232_view.sensor_waarde_array[i].trim()) + HOOGTE_CORRECTIE;
                  //        digitale_sensor_waarde = Math.round(digitale_sensor_waarde * 10) / 10.0d;  // bv 998.19 -> 998.2
                        
                  mytemp.air_temp      = laatste_record.substring(pos1, pos1 + 8);
                  mytemp.wet_bulb_temp = laatste_record.substring(pos2, pos2 + 8);
                  local_dew_point      = laatste_record.substring(pos3, pos3 + 8);
                  mytemp.RH            = laatste_record.substring(pos4, pos4 + 8);
                  
                  // skip all spaves
                  mytemp.air_temp      = mytemp.air_temp.trim();
                  mytemp.wet_bulb_temp = mytemp.wet_bulb_temp.trim();
                  local_dew_point      = local_dew_point.trim();
                  mytemp.RH            = mytemp.RH.trim();      
                           
                  if (destination.equals("MAIN_SCREEN") == false)
                  {
                     System.out.println("--- sensor data record, air temp for obs: " + mytemp.air_temp);
                     System.out.println("--- sensor data record, wet bulb temp for obs: " + mytemp.wet_bulb_temp);
                     System.out.println("--- sensor data record, dewpoint for obs: " + local_dew_point);
                     System.out.println("--- sensor data record, RH for obs: " + mytemp.RH);
                  }
                           
                  laatste_record_ok = true;
                  
               } // if (timeDiff <= TIMEDIFF_SENSOR_DATA)
               else
               {
                  mytemp.air_temp         = "";
                  mytemp.wet_bulb_temp    = "";
                  local_dew_point         = ""; 
                  mytemp.RH               = "";
                  mytemp.double_rv        = main.INVALID;
                  mytemp.double_dew_point = main.INVALID;
                        
                  laatste_record_ok = false;
                  error_code = 3;
               } // else
            
            } // try 
            catch (ParseException ex) 
            {
               //Logger.getLogger(main_RS232_RS422.class.getName()).log(Level.SEVERE, null, ex);
               
               System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() " + ex);
         
               mytemp.air_temp         = "";
               mytemp.wet_bulb_temp    = "";
               local_dew_point         = ""; 
               mytemp.RH               = "";
               mytemp.double_rv        = main.INVALID;
               mytemp.double_dew_point = main.INVALID;
                        
               laatste_record_ok = false;
               error_code = 4;
            } // catch
                     
         } // if (laatste_record != null)
            
      } // try
      catch (IOException ex) 
      {
         System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() " + ex);
         
         mytemp.air_temp         = "";
         mytemp.wet_bulb_temp    = "";
         local_dew_point         = ""; 
         mytemp.RH               = "";
         mytemp.double_rv        = main.INVALID;
         mytemp.double_dew_point = main.INVALID;
                        
         laatste_record_ok = false;
         error_code = 5;
      } // try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_sensor_data)))
   } // if (sensor_data_file.exists() && sensor_data_file.length() > 0)
   else
   {
      laatste_record_ok = false;
      error_code = 1;
   }
         
   // clear memory
   main.obs_file_datum_tijd      = null;
         
   
   
   if (laatste_record_ok)
   {
      double hulp_double_air_temp      = 999999;            // 999999 = random number but > 99.9
      double hulp_double_wet_bulb_temp = 999999;            // 999999 = random number but > 99.9
      double hulp_double_RH            = 999999;            // 999999 = random number but > 99.9
      
   
      // air temp check [APR]
      //
      if ( (mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null) && (mytemp.air_temp.indexOf("*") == -1) )
      {
         try
         {
            hulp_double_air_temp = Double.parseDouble(mytemp.air_temp.trim());
            
            if ((hulp_double_air_temp > -99.9) && (hulp_double_air_temp < 99.9))
            {
               DecimalFormat df = new DecimalFormat("0.0");               // rounding 1 decimal
               mytemp.air_temp = df.format(hulp_double_air_temp);
                      
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  String message_air_temp = "[APR] air temp at sensor height = " + mytemp.air_temp + " \u00B0C";
                  main.log_turbowin_system_message(message_air_temp);
               }
                     
               // make IMMT ready   
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  RS232_make_air_temp_APR_FM13_IMMT_ready();
               }
            }   
            else
            {
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  main.log_turbowin_system_message("[APR] automatically retrieved air temp: outside range");
               }
               mytemp.air_temp = "";
            }
         } // try
         catch (NumberFormatException e)
         {
            System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() " + e);
            mytemp.air_temp = "";
         }
      } // if ( (mytemp.air_temp.compareTo("") != 0) etc.
      
   
      // wet bulb temp check [APR]
      //
      if ( (mytemp.wet_bulb_temp.compareTo("") != 0) && (mytemp.wet_bulb_temp != null) && (mytemp.wet_bulb_temp.indexOf("*") == -1) )
      {
         try
         {
            hulp_double_wet_bulb_temp = Double.parseDouble(mytemp.wet_bulb_temp.trim());
            
            if ((hulp_double_wet_bulb_temp > -99.9) && (hulp_double_wet_bulb_temp < 99.9))
            {
               DecimalFormat df = new DecimalFormat("0.0");               // rounding 1 decimal
               mytemp.wet_bulb_temp = df.format(hulp_double_wet_bulb_temp);
                      
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  String message_wet_bulb_temp = "[APR] wet-bulb temp at sensor height = " + mytemp.wet_bulb_temp + " \u00B0C";
                  main.log_turbowin_system_message(message_wet_bulb_temp);
               }
                     
               // make IMMT ready [APR] 
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready();
               }
            }   
            else
            {
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  main.log_turbowin_system_message("[APR] automatically retrieved wet-bulb temp: outside range");
               }
               mytemp.wet_bulb_temp = "";
            }
         } // try
         catch (NumberFormatException e)
         {
            System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() " + e);
            mytemp.wet_bulb_temp = "";
         }
      } // if ( (mytemp.wet_bulb_temp.compareTo("") != 0) etc.
      
      
      // dewpoint check [APR]
      //
      mytemp.double_dew_point = main.INVALID;                 
      if ( (local_dew_point.compareTo("") != 0) && (local_dew_point != null) && (local_dew_point.indexOf("*") == -1) )
      {
         try
         {
            mytemp.double_dew_point = Double.parseDouble(local_dew_point.trim());
            
            if ((mytemp.double_dew_point > -70.0) && (mytemp.double_dew_point < 70.0))
            {
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  DecimalFormat df = new DecimalFormat("0.0");               // rounding 1 decimal
                  String message_dew_point = "[APR] dew-point at sensor height = " + df.format(mytemp.double_dew_point) + " \u00B0C";
                  main.log_turbowin_system_message(message_dew_point);
               }
               
               // make IMMT ready 
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  RS232_make_dew_point_APR_FM13_IMMT_ready();
               }
            } 
            else
            {
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  main.log_turbowin_system_message("[APR] automatically retrieved dew point: outside range");
               }
               mytemp.double_dew_point = main.INVALID;
            }            
         }
         catch (NumberFormatException e)
         {
            System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() " + e);
            mytemp.double_dew_point = main.INVALID;
         }  // catch
      } // if ( (local_dew_point.compareTo("") != 0) etc.
      
      
      // RH check [APR]
      //
      if ( (mytemp.RH.compareTo("") != 0) && (mytemp.RH != null) && (mytemp.RH.indexOf("*") == -1) )
      {
         try
         {
            hulp_double_RH = Double.parseDouble(mytemp.RH);               // range 0.0 - 100.0
            
            if ((hulp_double_RH >= 0.0) && (hulp_double_RH <= 100.0))
            {
               mytemp.double_rv = hulp_double_RH / 100;                    // NB mytemp.double_rv in range 0.0 - 1.0 [see also mytemp.java] !!!
               
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  String message_dew_point = "[APR] RH at sensor height = " + mytemp.RH + " %";
                  main.log_turbowin_system_message(message_dew_point);
               }
               
               // NB altough RH not used in FM13 
               // make IMMT ready   
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  RS232_make_RH_APR_FM13_IMMT_ready();
               }
            } 
            else
            {
               if (destination.equals("MAIN_SCREEN") == false)
               {
                  main.log_turbowin_system_message("[APR] automatically retrieved RH: outside range");
               }
               mytemp.RH = "";
               mytemp.double_rv = main.INVALID;
            }           
            
         } // try
         catch (NumberFormatException e)
         {
            System.out.println("--- " + "RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument() " + e);
            mytemp.RH = "";
            mytemp.double_rv = main.INVALID;
         }
      } // if ( (mytemp.RH.compareTo("") != 0) etc.
      
      
      // update the temperature fields on main screen
      //
      if (destination.equals("MAIN_SCREEN") == true)
      {
          main.temperatures_fields_update();
      }
      
   } // if (laatste_record_ok)   
   else
   {
      // error codes in this function:
      //    0: no error
      //    1: sensor data file not present
      //    2: last and second last retrieved records; record length not ok
      //    3: last retrieved record obsolete
      //    4: error when determining file date of sensor data file
      //    5: error when opening sensor data file
      
      String error_message = "";
      switch (error_code)
      {
         case 0: error_message = ""; 
                                 break;
         case 1: error_message = "[APR] HMP155; file sensor data not found";
                                 break;
         case 2: error_message = "[APR] HMP155; last and second last retrieved records; record length not ok"; 
                                 break;
         case 3: error_message = "[APR] HMP155; last retrieved record obsolete";
                                 break;
         case 4: error_message = "[APR] HMP155; error when determining file date of sensor data file";
                                 break;
         case 5: error_message = "[APR] HMP155; error when opening sensor data file"; 
                                 break;
         default: error_message = ""; 
                                 break;
      }                
                      
      if (error_message.equals("") == false)     
      {
         if (destination.equals("MAIN_SCREEN") == false)
         {
            main.log_turbowin_system_message(error_message);
         }
      }
   } // else
   
}
   
   
   
   
   
   
   
   
   
   
   
   
   
}
