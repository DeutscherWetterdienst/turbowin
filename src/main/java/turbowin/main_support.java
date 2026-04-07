/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package turbowin;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import static turbowin.main.APPLICATION_NAME;
import static turbowin.main.CAPTAIN_LOG;
import static turbowin.main.IMMT_LOG;
import static turbowin.main.INVALID;
import static turbowin.main.OBSERVER_LOG;
import static turbowin.main.cal_systeem_datum_tijd;
import static turbowin.main.log_turbowin_system_message;
import static turbowin.main.logs_dir;
import static turbowin.main.output_dir;
import static turbowin.main.ship_name;
import static turbowin.main.station_ID;
import static turbowin.main.support_class;
import static turbowin.main.temp_logs_dir;

/**
 *
 * @author marti
 */
public class main_support {
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   /*
   public static boolean determine_screen_size()
   {
      boolean display_resolution_greather_than_HD  = false;
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      double width_screen = screenSize.getWidth();
      double height_screen = screenSize.getHeight();      
         
      System.out.println("--- screen resolution: " + width_screen + "x" + height_screen);
      
      if ((width_screen > 1920) && (height_screen < 1080))   
      {
         display_resolution_greather_than_HD = true;
      }
      
      
      return display_resolution_greather_than_HD;
   }
   */
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static String getLinuxFlavor() 
   {
      String flavor = readOsRelease();
      if (flavor == null) 
      {
         flavor = getLegacyLinuxFlavor();
      }
      
      return flavor != null ? flavor : "Unknown Linux Flavor";
   }

   private static String readOsRelease() 
   {
      String osReleaseFile = "/etc/os-release";
      try (BufferedReader reader = new BufferedReader(new FileReader(osReleaseFile))) 
      {
         String line;
         while ((line = reader.readLine()) != null) 
         {
            if (line.startsWith("PRETTY_NAME")) 
            {
               // Common PRETTY_NAME Values by Distribution e.g.:
               //
               // Ubuntu 22.04 LTS
               // Debian GNU/Linux 12 (bookworm)
               // Fedora Linux 39
               return line.split("=")[1].replaceAll("\"", "");
            }
         }
      } 
      catch (IOException ignored) 
      {
      }
      
      return null;
   }

   private static String getLegacyLinuxFlavor() 
   {
      String[] files = {"/etc/debian_version", "/etc/redhat-release", "/etc/SuSE-release"};
      for (String filePath : files) 
      {
         try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) 
         {
            return reader.readLine();
         } 
         catch (IOException ignored) 
         {
         }
      }
      
      return null;
   }

   // public static void main(String[] args) {
   //     System.out.println("Linux Flavor: " + getLinuxFlavor());
   // }
   

   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static String print_libraries_name_and_version()
   {
      String library_log_string = "";
      
      
      try 
      {
         Enumeration resEnum;
         resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME); // Silently ignore wrong manifests on classpath?
         while (resEnum.hasMoreElements()) 
         {
            try 
            {
               URL url = (URL)resEnum.nextElement();
               InputStream is = url.openStream();
               if (is != null) 
               {
                  Manifest manifest = new Manifest(is);
                  Attributes mainAttribs = manifest.getMainAttributes();
                  String library_name = mainAttribs.getValue("Bundle-Name");
                  String library_version = mainAttribs.getValue("Bundle-Version");
                  
                  if (library_name != null)
                  {   
                     library_log_string += library_name + " " + library_version + "; "; 
                  }
                  
                  //System.out.println("--- " + library_log_string);
               }
            }
            catch (Exception e) 
            {
               // NB Silently ignore wrong manifests on classpath
            }
         } // while (resEnum.hasMoreElements())
      } // try
      catch (IOException ex) 
      {
         // NB Silently ignore wrong manifests on classpath
      }
      
      
      return library_log_string;
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void log_integrated_libraries()
   {
      log_turbowin_system_message("[GENERAL] libraries: " + main.APPLICATION_MET_MODULES + " " + print_libraries_name_and_version());
   }
      
      
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void password_timer_task() 
   {
   /*
      TimerTask task = new TimerTask() 
      {
         @Override
         public void run() 
         {
            System.out.println("Task performed on: " + new Date() + "n" + "Thread's name: " + Thread.currentThread().getName());
         }
      };
      
      Timer timer = new Timer();
    
      long delay = 1000L;
      timer.schedule(task, delay);
   */   
      /////////////
      
   /*   
      Timer timer = new Timer();
      timer.schedule(new TimerTask() 
      {
         @Override
         public void run() 
         {
       // Your database code here
         }
       }, 2*60*1000);
  */
      
      ActionListener update_password_action = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            password_ok = false;
         } 
      };  

      // main loop for updating password_ok
      password_timer = new Timer(DELAY_UPDATE_PASSWORD_LOOP, update_password_action);
      password_timer.setRepeats(false);                                  // false = only one action
      //password_timer.setInitialDelay(0);                                 // time in millisec to wait after timer is started to fire first event
      password_timer.setCoalesce(true);                                  // by default true, but to be certain
      password_timer.restart();
      //password_timer_is_gecreeerd = true;    
      
   }      
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void open_browser_on_not_linux(final String subject_address)
   {
      // none-LINUX: first try desktop procedure
      //             second try runtime procedure
      
      
      new SwingWorker<Integer, Void>()
      {
         @Override
         protected Integer doInBackground() throws Exception
         {
            Desktop desktop = null;
            int code = 0;

            // Before more Desktop API is used, first check
            // whether the API is supported by this particular
            // virtual machine (VM) on this particular host.
            if (Desktop.isDesktopSupported())
            {
               desktop = Desktop.getDesktop();
               URI uri = null;
               try
               {
                  if ( !(subject_address.contains("http") || subject_address.contains("HTTP")) )   
                  {
                     desktop.open(new File(subject_address));
                  }
                  else
                  {
                     // e.g. https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/help_files/barometer.pdf?inline=true 
                     //String http_adres = main.URL_INTERNET_HELP + help_page + "?inline=true"; // help_dir was set in java input page file e.g. mycm.java; ch1_image_mouseClicked()
                     
                     // so must be an internet address (http)
                     uri = new URI(subject_address);
                     desktop.browse(uri);
                  }
                  
               } // try
               catch (IOException | URISyntaxException ioe) 
               { 
                  code = -1;
               }
            } // if (Desktop.isDesktopSupported())
            else
            {
               code = -1;
            }
            
            
            if (code == -1)
            {
               // Desktop method failed
               // now trying to open with system-specific commands
               //
               // KDE:     kde-open
               // GNOME:   gnome-open
               // Any X-server system: xdg-open
               // MAC:     open
               // Windows: explorer               
               //
               // e.g. see: http://stackoverflow.com/questions/18004150/desktop-api-is-not-supported-on-the-current-platform
               
               //URI uri = null;
               //String te_open_help_file = null;
               //Runtime runtime;
                  
               //if (!local_help_file_exists)  
               //if (browser_address.contains("http"))  
               //{
               //   // e.g. https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/help_files/barometer.pdf?inline=true 
                //  //String http_adres = main.URL_INTERNET_HELP + help_page + "?inline=true"; 
               //   uri = new URI(browser_address);
               //   te_open_help_file = uri.toString();
               //   // NB Maybe uri.toString() is maybe(?)not necassary in case "xdg-open" this should be tested (see also manual on "xdg-open")
               //}
               //else if (offline_mode == true)
               //else
               //{
               //   //String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_dir; // nb help_dir is specific per parameter eg wind, waves etc.
               //   te_open_help_file = browser_address;
               //}
                  
               //runtime = Runtime.getRuntime();
                  
               // Microsoft Windows
               // --- NB Desktop method will always succeed
               
               // Linux (Gnome)
               // --- NB Desktop method will succeed (Desktop is based on Gnome) so not necessary to try a customised open command ---
                  
               // Linux (KDE)
               try 
               {
                  // create cmd array
                  String[] cmdArray = {"kde-open", subject_address};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               } 
               catch (IOException e) 
               {
                  // Linux (RaspBerry) [14-11-2014: tested on stand-alone RaspBerry succesfully]
                  try 
                  {
                     // create cmd array
                     String[] cmdArray = {"xdg-open", subject_address};

                     // create a process and execute cmdArray
                     Process process = Runtime.getRuntime().exec(cmdArray);
                  } 
                  catch (IOException e2) 
                  {
                     // Mac
                     try 
                     {
                        // create cmd array
                        String[] cmdArray = {"open", subject_address};

                        // create a process and execute cmdArray
                        Process process = Runtime.getRuntime().exec(cmdArray);
                     } 
                     catch (IOException e3) 
                     {
                        //JOptionPane.showMessageDialog(null, "Error invoking default web browser or pdf-reader (runtime-method): " + e3 , main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                        code = -2;
                     } // catch                 
                  } // catch                 
               } // catch                
            } // if (code == -1 //else (// Destop method not supported)

            return code;

         } // protected Void doInBackground() throws Exception
         
         @Override
         protected void done()
         {
            try
            {
               Integer response_code = get();

               if (response_code == -2)
               {
                  String message = "[GENERAL] Error invoking default web browser or pdf reader";
                  JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  main.log_turbowin_system_message(message);
               }   
               //else if (response_code == -3)
               //{
               //   String message = "[GENERAL] Error invoking default web browser";
               //   JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               //   main.log_turbowin_system_message(message);
               //}   
            } // try
            catch (InterruptedException | ExecutionException ex) 
            {   
               String message = "[GENERAL] Error invoking default web browser or pdf reader; " + ex.toString(); 
               main.log_turbowin_system_message(message);
               //main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
            } // catch
            
         } // protected void done()
         
      }.execute(); // new SwingWorker<Void, Void>()
      
   }
   
   
 
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void open_browser_on_linux(final String subject_address)
   {
      // LINUX: first try runtime procedure
      //        second try Desktop procedure
      //
      // why?: on Fedora strange results if first Desktop method was invoked, then the browser only opens as soon as TurboWin+ itself is closed....
      //       (so no error on Desktop procedure/method but strange behavior)
      //       In several correspondance items you can find that the Desktop method is not workig fine on Linux
      //
      // related to: hardware accelarator of the webbrowser? (https://stackoverflow.com/questions/69037458/selenium-chromedriver-gives-initializesandbox-called-with-multiple-threads-in)
      //         because the following errors in output window NetBeans IDE when trying to open URL
      //         libva error: vaGetDriverNameByIndex() failed with unknown libva error, driver_name = (null)
      //         [5757:5757:1122/165659.328325:ERROR:sandbox_linux.cc(376)] InitializeSandbox() called with multiple threads in process gpu-process.  
      //
      
         
      new SwingWorker<Integer, Void>()
      {
         @Override
         protected Integer doInBackground() throws Exception
         {
            int code = 0; 
            //boolean local_help_file_exists = false;
            
             
            // Are the help files stored locally? (installed as part of the complete TurboWin+ installation)
            //
            //String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_page; // nb help_page is parameter specific e.g. wind.pdf, waves.pdf etc.
            //File f = new File(help_file_path);
            //if (f.isFile())
            //{
            //   local_help_file_exists = true;
            //}

            
            // trying to open with system-specific commands
            //
            // KDE:     kde-open
            // GNOME:   gnome-open
            // Any X-server system: xdg-open
            // MAC:     open
            // Windows: explorer               
            //
            // e.g. see: http://stackoverflow.com/questions/18004150/desktop-api-is-not-supported-on-the-current-platform
               
            URI uri = null;
            //String te_open_help_file = null;
            //Runtime runtime;
                  
            //if (!local_help_file_exists)  
            //{
            //   // e.g. https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/help_files/barometer.pdf?inline=true 
            //   String http_adres = main.URL_INTERNET_HELP + help_page + "?inline=true"; 
            //   uri = new URI(http_adres);
            //   te_open_help_file = uri.toString();
            //   // NB Maybe uri.toString() is maybe(?)not necassary in case "xdg-open" this should be tested (see also manual on "xdg-open")
            //}
            //else if (offline_mode == true)
            //{
            //   //String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_dir; // nb help_dir is specific per parameter eg wind, waves etc.
            //   te_open_help_file = help_file_path;
            //}
                  
            //runtime = Runtime.getRuntime();
                 
            // Microsoft Windows
            // --- NB Desktop method will always succeed
               
            // Linux (Gnome)
            // --- NB Desktop method will succeed (Desktop is based on Gnome) so not necessary to try a customised open command ---
                  
            // Linux (KDE)
            try 
            {
               // create cmd array
               String[] cmdArray = {"kde-open", subject_address};

               // create a process and execute cmdArray
               Process process = Runtime.getRuntime().exec(cmdArray);
            } 
            catch (IOException e) 
            {
               // Linux (RaspBerry) [14-11-2014: tested on stand-alone RaspBerry succesfully]
               try 
               {
                  // create cmd array
                  String[] cmdArray = {"xdg-open", subject_address};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               } 
               catch (IOException e2) 
               {
                  // Mac
                  try 
                  {
                     // create cmd array
                     String[] cmdArray = {"open", subject_address};

                     // create a process and execute cmdArray
                     Process process = Runtime.getRuntime().exec(cmdArray);
                  } 
                  catch (IOException e3) 
                  {
                     //JOptionPane.showMessageDialog(null, "Error invoking default web browser (-Desktop-method not supported on this computer system and Runtime alternatives failed)" , main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                     code = -1;
                  } // catch                 
               } // catch                 
            } // catch    
               
            if (code == -1)
            {
               Desktop desktop = null;
               if (Desktop.isDesktopSupported())
               {
                  //JOptionPane.showMessageDialog(null, "Desktop.isDesktopSupported() = OK",  main.APPLICATION_NAME + " test", JOptionPane.ERROR_MESSAGE);

                  desktop = Desktop.getDesktop();
                  uri = null;
                  try
                  {
                     if ( !(subject_address.contains("http") || subject_address.contains("HTTP")) )
                     {
                        desktop.open(new File(subject_address));
                     }
                     else
                     {
                        // e.g. https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/help_files/barometer.pdf?inline=true 
                        //String http_adres = main.URL_INTERNET_HELP + help_page + "?inline=true"; // help_dir was set in java input page file e.g. mycm.java; ch1_image_mouseClicked()
                     
                        // so must be an internet address (http)
                        uri = new URI(subject_address);
                        desktop.browse(uri);
                     }
                  } // try
                  catch (IOException | URISyntaxException ioe) 
                  { 
                     code = -3;
                  }
               } // if (Desktop.isDesktopSupported())       
               else
               {
                  code = -2;
               }
                  
            } // if (code == -1)  
               

            return code;

         } // protected Void doInBackground() throws Exception
         
         @Override
         protected void done()
         {
            try
            {
               Integer response_code = get();

               if (response_code == -2)
               {
                  String message = "[GENERAL] Error invoking default web browser or pdf reader";
                  JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  main.log_turbowin_system_message(message);
               }   
               else if (response_code == -3)
               {
                  String message = "[GENERAL] Error invoking default web browser or pdf-reader (IOException or URISyntaxException)";
                  JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  main.log_turbowin_system_message(message);
               }   
            } // try
            catch (InterruptedException | ExecutionException ex) 
            {   
               String message = "[GENERAL] Error invoking default web browser or pdf reader; " + ex.toString(); 
               main.log_turbowin_system_message(message);
               //main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
            } // catch
         } // protected void done()      
         
      }.execute(); // new SwingWorker<Void, Void>()
      
   }
   
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/ 
   public void response_warning_pop_up()
   {
      // Temporary message box, (pop-up for only a short time) automatically disappears
      //
      
      // called from: Output_obs_to_server_format_101()
      //              Output_obs_to_server_FM13_TurboWin_stand_alone()
      
      
      final JOptionPane pane = new JOptionPane("server response may take a number of seconds", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
      final JDialog response_warning_dialog = pane.createDialog(main.APPLICATION_NAME);

      Timer timer_begin = new Timer(3000, new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            response_warning_dialog.dispose();
         }
      });
      timer_begin.setRepeats(false);
      timer_begin.start();
      response_warning_dialog.setVisible(true);
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   public void determine_satellite_image_url(String satellite_image_mode) 
   {  
      // defaults
      String products = "";
      String url_satellite_image = "";
      String url_lat_hemisphere_sign = "";
      String url_lon_hemisphere_sign = "";
      boolean url_lat_ok = false;
      boolean url_lon_ok = false;
      
      
      // determine type of satellite image
      //
      if (satellite_image_mode.equals(main.SATELLITE_IR_IMAGE))
      {
         products = "globalir";
      }
      else if (satellite_image_mode.equals(main.SATELLITE_VIS_IMAGE))
      {
         products = "global1kmvis";
      }
      else if (satellite_image_mode.equals(main.SATELLITE_SST_IMAGE))  
      {
         products = "NESDIS-SST";
      }
      else // default
      {
         products = "";
      }

      
      // Latitude
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
               
              
      /// Determine complete URL
      //
      if (url_lat_ok && url_lon_ok)
      {
         url_satellite_image = "https://realearth.ssec.wisc.edu/?products=" + products + "&time=latest&center=" + 
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
         url_satellite_image = "https://realearth.ssec.wisc.edu/?products=" + products + "&time=latest&center=0,0&zoom=3";
      }
               
      // invoke, in the default web browser, the url to the satellite image
      //
      main.satellite_link_mouse_clicked(url_satellite_image);
      
   }
   
   
 
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public void log_memory_statistics()
{
   // called from: - read_muffin() [main.java]
   //              - lees_configuratie_regels()[main.java]
   //              - main_windowClosing()[main.java]
   
   
   // print memory statistics (https://stackoverflow.com/questions/3571203/what-are-runtime-getruntime-totalmemory-and-freememory) 
   //                          https://stackoverflow.com/questions/4667483/how-is-the-default-java-heap-size-determined
   //
   // NB max memory   = used memory + free memory + unlocated memory
   //    total memory = used memory + free memory
   
   
   // Total designated memory, this will equal the configured -Xmx value:
   long total_designated_memory = Runtime.getRuntime().maxMemory() / 1024 / 1024; // MB
   
   // Current allocated free memory, is the current allocated space ready for new objects. Caution this is not the total free available memory:
   long current_allocated_free_memory = Runtime.getRuntime().freeMemory() /1024 /1024; // MB
   
   // Total allocated memory, is the total allocated space reserved for the java process:
   long total_allocated_memory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
   
   // Used memory, has to be calculated:
   long used_memory = total_allocated_memory - current_allocated_free_memory;
   
   // Total free designated memory, has to be calculated:
   long total_free_designated_memory = total_designated_memory - used_memory;
   
   String memory_summary = "JVM: total designated memory = " + total_designated_memory + " MB; " + 
                                 "current allocated free memory = " + current_allocated_free_memory + " MB; " + 
                                 "total allocated memory = " + total_allocated_memory + " MB; " +
                                 "used memory = " + used_memory + " MB; " +   
                                 "total free designated memory = " + total_free_designated_memory + " MB";
   log_turbowin_system_message("[GENERAL] " + memory_summary);
   //log_turbowin_system_message("[GENERAL] JVM total designated memory = " + total_designated_memory + " MB");
   //log_turbowin_system_message("[GENERAL] JVM current allocated free memory = " + current_allocated_free_memory + " MB");
   //log_turbowin_system_message("[GENERAL] JVM total allocated memory = " + total_allocated_memory + " MB");
   //log_turbowin_system_message("[GENERAL] JVM used memory = " + used_memory + " MB");  
   //log_turbowin_system_message("[GENERAL] JVM total free memory = " + total_free_memory + " MB");
}
   
   

/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public void log_java_version()
{
   // called from: - read_muffin()
   //              - lees_configuratie_regels()

   String java_version = System.getProperty("java.runtime.version");
   String java_name = System.getProperty("java.vm.name");
   
   log_turbowin_system_message("[GENERAL] Java: " + java_version + "; " + java_name);
}
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public boolean position_sequence_check()
{
   // initialisation
   boolean time_sequence_checks_ok  = true;
   boolean doorgaan                 = true;
   boolean string_num_converions_ok = true;
   int num_vorige_obs_jaar          = 0;
   int num_vorige_obs_maand         = 0;
   int num_vorige_obs_dag           = 0;
   int num_vorige_obs_uur           = 0;
   int num_quadrant                 = 0;
   float num_latitude               = 0;
   float num_longitude              = 0;
   
      
   //
   ////////////////// first determine the substrings and convert them to numerical values               
   //
   if (main.last_record.length() >= 19)
   {
      /* string date time from previous obs (last stored record) */
      String jaar      = main.last_record.substring(1, 5);              // cpp: jaar  = last_record.substr(1, 4);
      String maand     = main.last_record.substring(5, 7);              // cpp: maand = last_record.substr(5, 2);
      String dag       = main.last_record.substring(7, 9);              // cpp: dag   = last_record.substr(7, 2);
      String uur       = main.last_record.substring(9, 11);             // cpp: uur   = last_record.substr(9, 2);

      /* obs code position from previous obs (last stored record) */
      String quadrant  = main.last_record.substring(11, 12);            // cpp: quadrant  = last_record.substr(11, 1);                       // WMO code table 3333
      String latitude  = main.last_record.substring(12, 15);            // cpp: latitude  = last_record.substr(12, 3);                       // tenths of degrees
      String longitude = main.last_record.substring(15, 19);            // cpp: longitude = last_record.substr(15, 4);                       // tenths of degrees

      /* date/time of last stored record convertion to numerical values */
      try
      {
         num_vorige_obs_jaar  = Integer.valueOf(jaar.trim());  // cpp: num_vorige_obs_jaar = atoi(jaar.c_str());
      }
      catch (NumberFormatException ex)
      {
         System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
         string_num_converions_ok = false;
      }
      try
      {  
         num_vorige_obs_maand = Integer.valueOf(maand.trim()); // cpp: num_vorige_obs_maand = atoi(maand.c_str());
      }
      catch (NumberFormatException ex)
      {
         System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
         string_num_converions_ok = false;
      }      
      try
      {   
         num_vorige_obs_dag   = Integer.valueOf(dag.trim());   // cpp: num_vorige_obs_dag   = atoi(dag.c_str());
      }
      catch (NumberFormatException ex)
      {
         System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
         string_num_converions_ok = false;
      }  
      try
      {
         num_vorige_obs_uur   = Integer.valueOf(uur.trim());   // cpp: num_vorige_obs_uur   = atoi(uur.c_str());
      }
      catch (NumberFormatException ex)
      {
         System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
         string_num_converions_ok = false;
      }     
      
      /* positie uit laatste record omzetten naar num_waarden + afronden + "+/-" (afh. quadrant) maken */
      try
      {
         num_quadrant = Integer.valueOf(quadrant.trim());    // cpp: num_quadrant  = atoi(quadrant.c_str());
      }
      catch (NumberFormatException ex)
      {
         System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
         string_num_converions_ok = false;
      }        
      try
      {
         num_latitude = Float.valueOf(latitude.trim());      // cpp: num_latitude  = atoi(latitude.c_str());
      }
      catch (NumberFormatException ex)
      {
         System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
         string_num_converions_ok = false;
      }        
      try
      {
         num_longitude = Float.valueOf(longitude.trim());     // cpp: num_longitude = atoi(longitude.c_str());
      }
      catch (NumberFormatException ex)
      {
         System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
         string_num_converions_ok = false;
      }  
   } // if (last_record.length() >= 19) 
   else // record too short for obtaining substrings
   {
      string_num_converions_ok = false;
   }
      
   
   //
   //////////////// conversion from substring to ints or floats successful
   //
   if (string_num_converions_ok == true)   
   {   
      float num_vorige_obs_breedte = num_latitude / 10;           // nu in graden en tienden
      float num_vorige_obs_lengte  = num_longitude / 10;          // nu in graden en tienden

      if (num_quadrant == 3 || num_quadrant == 5)                 // Zuiderbreedte
      {
         num_vorige_obs_breedte *= -1;
      }
      if (num_quadrant == 5 || num_quadrant == 7)                 // Westerlengte
      {
         num_vorige_obs_lengte *= -1;
      }


      /* 
      // compare date/time with the date/time of the previous obs
      */
      
      System.out.println("--- comparing date/time of entered obs with date/time of last saved obs");

      /* date-time previous saved obs */
      Calendar calendar_vorige_obs = new GregorianCalendar(num_vorige_obs_jaar, num_vorige_obs_maand -1, num_vorige_obs_dag, num_vorige_obs_uur, 0);// Month value is 0-based. e.g., 0 for January.

      /* date-time present obs */
      int num_huidige_obs_jaar = Integer.valueOf(mydatetime.year.trim());
      int num_huidige_obs_maand = Integer.valueOf(mydatetime.MM_code.trim());// NB do not use mydatetime.month because = February etc.
      int num_huidige_obs_dag = Integer.valueOf(mydatetime.day.trim());
      int num_huidige_obs_uur = Integer.valueOf(mydatetime.hour.trim());

      Calendar calendar_huidige_obs = new GregorianCalendar(num_huidige_obs_jaar, num_huidige_obs_maand - 1, num_huidige_obs_dag, num_huidige_obs_uur, 0);// Month value is 0-based. e.g., 0 for January.


      if (calendar_huidige_obs.compareTo(calendar_vorige_obs) <= 0)
      {
         SimpleDateFormat sdf2;
         sdf2 = new SimpleDateFormat("MMMM dd, yyyy HH");                            // e.g "MMMM dd, yyyy" -> februari 27, 2010

         String string_calendar_huidige_obs = sdf2.format(calendar_huidige_obs.getTime());

         String info = "";
         info = "-time sequence check-\n";
         info += "obs date/time";
         info += " (";
         info += string_calendar_huidige_obs;
         info += ".00 UTC";
         info += ")";

         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + " please confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
         {
            //MessageBox("Please correct the error (no final obs was coded)", "TurboWin message", MB_OK);
            JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
            doorgaan = false;
            time_sequence_checks_ok = false;
         }
      } // if (calendar_huidige_obs.compareTo(calendar_vorige_obs) < 0)


      /* present obs position compared to the position of the previous obs */
      if (doorgaan)
      {
         System.out.println("--- comparing position of entered obs with position of last saved obs");
         
         /* first determine the time diff between present and previous obs */
         long obs_verschil_uur = (calendar_huidige_obs.getTimeInMillis() - calendar_vorige_obs.getTimeInMillis()) / 3600000;    // 3600000 = 1000 * 60 * 60 = 1 uur

         if (obs_verschil_uur >= 0)                   // alleen verdere checks als huidige obs datum/tijd is later dan vorige obs datum/tijd
         {
            /* NB er wordt als grens genomen dat er max 30 mijl per uur afgelegd kan zijn */
            /* NB for testing see e.g.: http://williams.best.vwh.net/gccalc.htm */
            int afstand_vorige_huidige_obs = bepaal_afstand_huidige_obs_pos_tot_vorige_obs_pos(num_vorige_obs_breedte, num_vorige_obs_lengte);

            // JOptionPane.showMessageDialog(null, afstand_vorige_huidige_obs,  main.APPLICATION_NAME + " afstand tot vorige obs", JOptionPane.WARNING_MESSAGE);

            if (((obs_verschil_uur >= 0 && obs_verschil_uur <= 6) && (afstand_vorige_huidige_obs > 180)) ||
                    ((obs_verschil_uur > 6 && obs_verschil_uur <= 12) && (afstand_vorige_huidige_obs > 360)) ||
                    ((obs_verschil_uur > 12 && obs_verschil_uur <= 18) && (afstand_vorige_huidige_obs > 540)) ||
                    ((obs_verschil_uur > 18 && obs_verschil_uur <= 24) && (afstand_vorige_huidige_obs > 720)))
            {
      		   String info = "";
               info = "-position sequence check-\n";
               info += "obs position:\n";

               info += myposition.latitude_degrees;
               info += " ";
               info += myposition.latitude_minutes;
               info += "' ";
               info += myposition.latitude_hemisphere;
               info += "  ";
               info += myposition.longitude_degrees;
               info += "\u00B0 ";
               info += myposition.longitude_minutes;
               info += "' ";
               info += myposition.longitude_hemisphere;
               info += "\n";

               if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + " please confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
               {
                  JOptionPane.showMessageDialog(null, "Please correct the obs position (no final obs was coded)", main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
                  doorgaan = false;
                  time_sequence_checks_ok = false;
		         }
            } // if ( ((obs_verschil_uur > 0 && obs_verschil <= 6) etc.
         } // if (obs_verschil_uur > 0)
      } // if (doorgaan)
   } // if (string_num_converions_ok == true)


   return time_sequence_checks_ok;
}
   


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private int bepaal_afstand_huidige_obs_pos_tot_vorige_obs_pos(double num_vorige_obs_breedte, double num_vorige_obs_lengte)
{
	/* used formula :                                                  */
	/* cos_AB = sin_bA * sin_bB + cos_bA * cos_bB * cos_delta_l_AB     */
	/*                                                                 */
	/* distance = 60 arccos(cos_AB)                                    */
	/*                                                                 */
	/* from degrees angle to radians angle : graden * 60 * boogminuut  */


   // constants
   final int westgrens_POR     = 90;
   final int oostgrens_POR     = -90;
   final double boogminuut     = 0.0002908882; 
   final double h180pi         = 3437.746771;  

   // var's
	int afstand = Integer.MAX_VALUE;
	double delta_l_ab;
	double cos_delta_l_ab;
	double sin_breedte_a;
	double sin_breedte_b;
	double cos_breedte_a;
	double cos_breedte_b;
	double num_huidige_obs_breedte;
	double num_huidige_obs_lengte;
   double acos_argument;
	boolean huidige_obs_in_por;



   num_huidige_obs_breedte = (double)myposition.int_latitude_degrees +  ((double)myposition.int_latitude_minutes / 60);
   num_huidige_obs_lengte =  (double)myposition.int_longitude_degrees + ((double)myposition.int_longitude_minutes / 60);

   
   if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true)
   {
      num_huidige_obs_breedte *= -1;
   }

   if (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true)
   {
      num_huidige_obs_lengte *= -1;
   }

   /* due to 180 degrees passage in the POR */
       //if ((num_huidige_obs_lengte >= westgrens_POR) && (num_huidige_obs_lengte <= oostgrens_POR)) // komt hier nooit in ????
   
       //if ( (num_huidige_obs_lengte >= westgrens_POR && num_huidige_obs_lengte <= 180) ||      // westgrens_por +90
       //     (num_huidige_obs_lengte >= -180 && num_huidige_obs_lengte <= oostgrens_POR) )      // oostgrens_por -90
   huidige_obs_in_por = (num_huidige_obs_lengte >= -180 && num_huidige_obs_lengte <= oostgrens_POR) || (num_huidige_obs_lengte >= westgrens_POR && num_huidige_obs_lengte <= 180); // westgrens_por +90 // oostgrens_por -90

	if (huidige_obs_in_por == true)
	{
		if (num_huidige_obs_lengte < 0) num_huidige_obs_lengte += 360;
		if (num_vorige_obs_lengte < 0) num_vorige_obs_lengte += 360;
	} // if (huidige_obs_in_por == true)


	/* determine longitude difference */
	delta_l_ab = num_huidige_obs_lengte - num_vorige_obs_lengte;


	/* longitude difference > 180: do not compute but give MAX_VALUE (> 180 gives issues in formula) */
	if (Math.abs(delta_l_ab) > 180)
   {
      afstand = Integer.MAX_VALUE;
   }
	else // longitude difference < 180
	{
		/* the (greatcircle) computation */
		sin_breedte_a  = Math.sin(num_vorige_obs_breedte  * 60 * boogminuut);
		sin_breedte_b  = Math.sin(num_huidige_obs_breedte * 60 * boogminuut);
		cos_breedte_a  = Math.cos(num_vorige_obs_breedte  * 60 * boogminuut);
		cos_breedte_b  = Math.cos(num_huidige_obs_breedte * 60 * boogminuut);
		cos_delta_l_ab = Math.cos(delta_l_ab * 60 * boogminuut);

      /* first test acos argument to prevent ACOS domain error (if 2x exact the same position + obs to screen)*/
      acos_argument = sin_breedte_a * sin_breedte_b + cos_breedte_a * cos_breedte_b * cos_delta_l_ab;
      if (acos_argument <= -1 || acos_argument >= 1)
      {
         afstand = 0;
      }
      else
      {
      	afstand = (int)Math.round(h180pi *  Math.acos(acos_argument)); //Returns the closest int to the argument (zelde als (int)Math.floor(a + 0.5f)
      }
	} // else (longitude difference < 180)


	return afstand;
}

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public boolean Move_log_files(final String move_mode_logs)
{
   // called from: - Maintenance_Move_log_files_to_disk_actionPerformed() [main.java]
   //              - Maintenance_Move_log_files_by_email_actionPerformed()[main.java]
   
   
   /*
   // Note: temp_dir only for move_mode_logs = MOVE_TO_EMAIL; in move_mode_logs = MOVE_TO_DISK temp_dir is a dummy
   */
   
   /*
   // Note:  If you intend to distribute your program as an unsigned Java Web Start application,
   //      then instead of using the JFileChooser API you should use the file services provided
   //      by the JNLP API. These services FileOpenService and FileSaveService not only
   //      provide support for choosing files in a restricted environment, but also take care of
   //      actually opening and saving them. An example of using these services is in JWSFileChooserDemo.
   //      Documentation for using the JNLP API can be found in the Java Web Start lesson.
   //     (http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html)
   */

   boolean doorgaan         = false;
   boolean doorgaan_captain = true;


   /* first check if there is an immt log source file present (and not empty) */
   main.volledig_path_srcFilename_immt = logs_dir + java.io.File.separator + IMMT_LOG;
   File immt_source_file = new File(main.volledig_path_srcFilename_immt);
   if (immt_source_file.exists() && immt_source_file.length() > 10)
   {
      doorgaan = true;
   }
   else
   {
      JOptionPane.showMessageDialog(null, "Move log files cancelled, reason: nothing to move; IMMT log (file with all stored observations for climatological use) empty ", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      doorgaan = false;
   }


   /* OK immt log present, so continue */

   if (doorgaan == true)
   {
      /* in MOVE_TO_DISK mode filechooser dialog popup */
      if (move_mode_logs.equals(main.MOVE_TO_DISK) == true)
      {
         // pop-up the file/directory chooser dialog box
         //JFileChooser chooser = new JFileChooser(".");                                                         // present dir
         //JFileChooser chooser = new JFileChooser(javax.swing.filechooser.FileSystemView.getFileSystemView() ); // Constructs a JFileChooser using the given FileSystemView
         JFileChooser chooser = new JFileChooser();                                                              // Constructs a JFileChooser pointing to the user's default directory. This default depends on the operating system. It is typically the "My Documents" folder on Windows, and the user's home directory on Unix.
         chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);                                // now the user can only select directories
         //int result = chooser.showSaveDialog(main.this);
         int result = chooser.showSaveDialog(null);

         if (result == JFileChooser.APPROVE_OPTION)
         {
            output_dir = chooser.getSelectedFile().getPath();                                        // getSelectedFile() -> in this case returns not a file but a directory !

            final File dirs = new File(output_dir);

            if (dirs.exists() == false)                   // output_dir not exists
            {
               final boolean success = dirs.mkdirs();
               if (success == false)
               {
                  JOptionPane.showMessageDialog(null, "Could not create " + output_dir, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  doorgaan = false;
               }
            } // else destination path do not exist


            // output_dir and log_dir must be different !
            if (doorgaan == true)
            {
               if (output_dir.equals(logs_dir) == true)
               {
                  JOptionPane.showMessageDialog(null, "Download folder the same as log files folder, LOG FILES NOT MOVED ", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);

                  output_dir = null;
                  doorgaan = false;
               } // if (output_dir.equals(logs_dir) == true)
            } // if (doorgaan == true)

         } // if (result == JFileChooser.APPROVE_OPTION
         else // Cancel buttom / cross
         {
            doorgaan = false;
         } // else
 
      } // if (move_mode_logs.equals(MOVE_TO_DISK) == true)
      else if (move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)
      {
         output_dir = main.temp_logs_dir;
      } // else if (move_mode_logs.equals(MOVE_TO_EMAIL) == true)

   } // if doorgaan == true




   /*
   // move captain source file to captain destination file (if captain source file is present)
   */
   if (doorgaan == true)
   {
      /* captain distination file (eg PGDE_captain.log) */
      //volledig_path_dstFilename_captain = output_dir + java.io.File.separator + CAPTAIN_LOG;
      //volledig_path_dstFilename_captain = output_dir + java.io.File.separator + call_sign + "_" + CAPTAIN_LOG;
      main.volledig_path_dstFilename_captain = output_dir + java.io.File.separator + station_ID + "_" + CAPTAIN_LOG;

      /* captain source file (captain.log) */
      main.volledig_path_srcFilename_captain = logs_dir + java.io.File.separator + CAPTAIN_LOG;

      /* captain backup file (eg PGDE_CAPTAIN_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));// gives system date and time (UTC) of this moment
      SimpleDateFormat sdf2;
      sdf2 = new SimpleDateFormat("MMMM dd, yyyy");                            // e.g "MMMM dd, yyyy" -> februari 27, 2010
      String systeem_date_time = sdf2.format(cal_systeem_datum_tijd.getTime());
      //volledig_path_backup_srcFilename_captain = logs_dir + java.io.File.separator + "CAPTAIN_BACKUP " + systeem_date_time + ".TXT";
      //volledig_path_backup_srcFilename_captain = logs_dir + java.io.File.separator + call_sign + "_" + "CAPTAIN_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_captain = logs_dir + java.io.File.separator + station_ID + "_" + "CAPTAIN_BACKUP " + systeem_date_time + ".TXT";

      File captain_source_file = new File(main.volledig_path_srcFilename_captain);
      if (captain_source_file.exists())
      {
         doorgaan_captain = captain_source_file.length() > 5;
      }
      else // no captain source file present
      {
         // NB no message necessary, because captain data (file) not mandatory (contrary to immt log)
         doorgaan_captain = false;
      }
   } // if (doorgaan == true)


   if (doorgaan == true && doorgaan_captain == true && move_mode_logs.equals(main.MOVE_TO_DISK) == true)
   {
      /* copy captain source file to destination captain file */
      new SwingWorker<String, Void>()
      {
         @Override
         protected String doInBackground() throws Exception
         {
            String result = null;

            try (FileChannel srcChannel = new FileInputStream(main.volledig_path_srcFilename_captain).getChannel(); 
                 FileChannel dstChannel = new FileOutputStream(main.volledig_path_dstFilename_captain).getChannel())
            {
               // Copy file contents from source to destination
               dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

               result = "OK";
            }
            catch (IOException e)
            {
               //JOptionPane.showMessageDialog(null, "Unable to move " + main.volledig_path_srcFilename_captain + " to " + main.volledig_path_dstFilename_captain, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               result = "NOT_OK";
            }

            return result;

         } // protected Void doInBackground() throws Exception

         @Override
         protected void done()
         {
            String result_opgehaald = null;

            try
            {
               result_opgehaald = get();
            }
            catch (InterruptedException | ExecutionException ex) {}

            if ((result_opgehaald != null) && result_opgehaald.equals("NOT_OK") == true)
            {
               JOptionPane.showMessageDialog(null, "Unable to move " + main.volledig_path_srcFilename_captain + " to " + main.volledig_path_dstFilename_captain, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE); 
            }
            if ( (result_opgehaald != null) && (result_opgehaald.equals("OK") == true) )
            {
               // rename sourcefile to backup file (after it was copied, see doInBackground())
               File source_file = new File(main.volledig_path_srcFilename_captain);
               File renamed_file = new File(main.volledig_path_backup_srcFilename_captain);

               if (source_file.renameTo(renamed_file) == false)
               {
                  // failed: most of the time because backup file of the same name already exist (2nd backup same day)

                  if (move_mode_logs.equals(main.MOVE_TO_DISK) == true)
                  {
                     // LET OP
                     // deze melding NIET geven als de files gezipped worden er daarna per email moeten worden verstuurd
                     // want dan wordt met deze melding de aanmaak van het zip bestand opgehouden, maar het email programma
                     // met een verwijzing naar dit zip bestand is echter al wel geopend !!
                     JOptionPane.showMessageDialog(null, "Backing up log file " + main.volledig_path_srcFilename_captain + " failed (2nd move/backup same day?)", main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
                  }
                  source_file.delete();        // because backup failed, immt.log still present to avoid confusing with log files, simply delete the immt.log
               }
            } // if (result_opgehaald.equals("OK") == true)
         } // protected void done()
      }.execute(); // new SwingWorker<Void, Void>()

   } // if (doorgaan == true && doorgaan_captain == true && move_mode_logs.equals(main.MOVE_TO_DISK) == true)


   if (doorgaan == true && doorgaan_captain == true && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)
   {
      // NB do not use swingworker in case of email send, due to possible synchronisation issue (zip attachement not created on time)
      
      /* copy captain source file to destination captain file */
      String result = null;

      try (FileChannel srcChannel = new FileInputStream(main.volledig_path_srcFilename_captain).getChannel(); 
           FileChannel dstChannel = new FileOutputStream(main.volledig_path_dstFilename_captain).getChannel())
      {
         // Copy file contents from source to destination
         dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

         result = "OK";
      }
      catch (IOException e)
      {
               //JOptionPane.showMessageDialog(null, "Unable to move " + main.volledig_path_srcFilename_captain + " to " + main.volledig_path_dstFilename_captain, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         result = "NOT_OK";
      }

      if (result.equals("NOT_OK") == true)
      {
         JOptionPane.showMessageDialog(null, "Unable to move " + main.volledig_path_srcFilename_captain + " to " + main.volledig_path_dstFilename_captain, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE); 
      }
      if (result.equals("OK") == true)
      {
         // rename sourcefile to backup file (after it was copied, see doInBackground())
         File source_file = new File(main.volledig_path_srcFilename_captain);
         File renamed_file = new File(main.volledig_path_backup_srcFilename_captain);

         if (source_file.renameTo(renamed_file) == false)
         {
            // failed: most of the time because backup file of the same name already exist (2nd backup same day)

            if (move_mode_logs.equals(main.MOVE_TO_DISK) == true)
            {
               // LET OP
               // deze melding NIET geven als de files gezipped worden er daarna per email moeten worden verstuurd
               // want dan wordt met deze melding de aanmaak van het zip bestand opgehouden, maar het email programma
               // met een verwijzing naar dit zip bestand is echter al wel geopend !!
               JOptionPane.showMessageDialog(null, "Backing up log file " + main.volledig_path_srcFilename_captain + " failed (2nd move/backup same day?)", main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
            }
            source_file.delete();        // because backup failed, immt.log still present to avoid confusing with log files, simply delete the immt.log
         }
      } // if (result_opgehaald.equals("OK") == true)
   } // if (doorgaan == true && doorgaan_captain == true && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)
   
   
   /*
   // copy immt source file to destination and make a backup of the immt file
   //
   // + copy observer data plus number of observations (via function Kopieeren_Waarnemers_En_Aantallen())
   */
   if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_DISK) == true)
   {
      /* immt distination file (eg PGDE_immt.log) */
      //volledig_path_dstFilename_immt = output_dir + java.io.File.separator + IMMT_LOG;
      //volledig_path_dstFilename_immt = output_dir + java.io.File.separator + call_sign + "_" + IMMT_LOG;
      main.volledig_path_dstFilename_immt = output_dir + java.io.File.separator + station_ID + "_" + IMMT_LOG;

      /* immt source file (immt.log) */
      main.volledig_path_srcFilename_immt = logs_dir + java.io.File.separator + IMMT_LOG;

      /* immt backup file (eg PGDE_IMMT_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));// gives system date and time (UTC) of this moment
      SimpleDateFormat sdf2;
      sdf2 = new SimpleDateFormat("MMMM dd, yyyy");                            // e.g "MMMM dd, yyyy" -> februari 27, 2010
      String systeem_date_time = sdf2.format(cal_systeem_datum_tijd.getTime());
      //volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP " + systeem_date_time + ".TXT";
      //volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + call_sign + "_" + "IMMT_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + station_ID + "_" + "IMMT_BACKUP " + systeem_date_time + ".TXT";

      new SwingWorker<String, Void>()
      {
         @Override
         protected String doInBackground() throws Exception
         {
            String result = null;

            /*
            // count number of obs per observer per year
            */
            support_class.Kopieeren_Waarnemers_En_Aantallen();


            /*
            // copy immt
            */
            try (FileChannel srcChannel = new FileInputStream(main.volledig_path_srcFilename_immt).getChannel();
                 FileChannel dstChannel = new FileOutputStream(main.volledig_path_dstFilename_immt).getChannel())
            {
               // Copy file contents from source to destination
               dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

               result = "OK";
            }
            catch (IOException e)
            {
               //JOptionPane.showMessageDialog(null, "Unable to move " + main.volledig_path_srcFilename_immt + " to " + main.volledig_path_dstFilename_immt, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               result = "NOT_OK";
            }

            return result;

         } // protected Void doInBackground() throws Exception

         @Override
         protected void done()
         {
            String result_opgehaald = null;

            try
            {
               result_opgehaald = get();
            }
            catch (InterruptedException | ExecutionException ex) {}

            if ( (result_opgehaald != null) && (result_opgehaald.equals("NOT_OK") == true) )
            {
               JOptionPane.showMessageDialog(null, "Unable to move " + main.volledig_path_srcFilename_immt + " to " + main.volledig_path_dstFilename_immt, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
            if ( (result_opgehaald != null) && (result_opgehaald.equals("OK") == true) )
            {
               if (move_mode_logs.equals(main.MOVE_TO_DISK) == true)
               {
                  // LET OP
                  // deze melding NIET geven als de files gezipped worden er daarna per email moeten worden verstuurd
                  // want dan wordt met deze melding de aanmaak van het zip bestand opgehouden, maar het email programma
                  // met een verwijzing naar dit zip bestand is echter al wel geopend !!

                  // Note: not possible to show this message at the end of this function (outsite the swingworker)
                  //       because Swingworker not finished
                  String info = "meteo log files moved to folder: " + output_dir;
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
                  main.log_turbowin_system_message("[GENERAL] "+ info);
               }

               // rename sourcefile to backup file (after it was copied, see doInBackground())
               File source_file = new File(main.volledig_path_srcFilename_immt);
               File renamed_file = new File(main.volledig_path_backup_srcFilename_immt);

               if (source_file.renameTo(renamed_file) == false)
               {
                  // rename failed: most of the time because a backup file of the same name already exist (2nd backup same day)

                  if (move_mode_logs.equals(main.MOVE_TO_DISK) == true)
                  {        
                     // LET OP
                     // deze melding NIET geven als de files gezipped worden er daarna per email moeten worden verstuurd
                     // want dan wordt met deze melding de aanmaak van het zip bestand opgehouden, maar het email programma
                     // met een verwijzing naar dit zip bestand is echter al wel geopend !!
                        
                     JOptionPane.showMessageDialog(null, "Backing up log file " + main.volledig_path_srcFilename_immt + " failed (2nd move/backup same day?)", main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
                  }
                  source_file.delete();        // because backup failed, immt.log still present to avoid confusing with log files, simply delete the immt.log
               }

               // NB
               // omdat zippen afhankelijk is van de verplaatste log files, moet dit hier in het done() deel gebeuren
               // want als je dat anders doet kan het zijn dat de swingworker die de log files veplaatst
               // nog bezig is zodat dan een i/0 zip error optreed
               //
               if (move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)
               {
                  zip_log_files();
               }
               
               // Clearing all the observer data ? (the captain data is always cleared automatically after an upload/sending of the logs)
               //
               String info = "Clearing all the data of the observers (surname, full initials/full christian name, rank, discharge book number)";
               if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
               {
                  String volledig_path_observer = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;
                  try 
                  {
                     FileChannel.open(Paths.get(volledig_path_observer), StandardOpenOption.WRITE).truncate(0).close();
                     JOptionPane.showMessageDialog(null, "Successfully cleared all the data of the observers", APPLICATION_NAME + " message", JOptionPane.INFORMATION_MESSAGE);
                  } 
                  catch (IOException ex) 
                  {
                     JOptionPane.showMessageDialog(null, "Clearing all the data of the observers failed", APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  }
               }
               
            } // if (result_opgehaald.equals("OK") == true)
         } // protected void done()
      }.execute(); // new SwingWorker<Void, Void>()

   } // if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_DISK) == true)

   
   if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)
   {
       // NB do not use swingworker in case of email send, due to possible synchronisation issue (zip attachement not created on time)
      
      /* immt distination file (eg PGDE_immt.log) */
      //volledig_path_dstFilename_immt = output_dir + java.io.File.separator + IMMT_LOG;
      //volledig_path_dstFilename_immt = output_dir + java.io.File.separator + call_sign + "_" + IMMT_LOG;
      main.volledig_path_dstFilename_immt = output_dir + java.io.File.separator + station_ID + "_" + IMMT_LOG;

      /* immt source file (immt.log) */
      main.volledig_path_srcFilename_immt = logs_dir + java.io.File.separator + IMMT_LOG;

      /* immt backup file (eg PGDE_IMMT_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));// gives system date and time (UTC) of this moment
      SimpleDateFormat sdf2;
      sdf2 = new SimpleDateFormat("MMMM dd, yyyy");                            // e.g "MMMM dd, yyyy" -> februari 27, 2010
      String systeem_date_time = sdf2.format(cal_systeem_datum_tijd.getTime());
      //volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP " + systeem_date_time + ".TXT";
      //volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + call_sign + "_" + "IMMT_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + station_ID + "_" + "IMMT_BACKUP " + systeem_date_time + ".TXT";

      String result = null;

      /*
      // count number of obs per observer per year
      */
      support_class.Kopieeren_Waarnemers_En_Aantallen();


      /*
      // copy immt
      */
      try (FileChannel srcChannel = new FileInputStream(main.volledig_path_srcFilename_immt).getChannel();
           FileChannel dstChannel = new FileOutputStream(main.volledig_path_dstFilename_immt).getChannel())
      {
         // Copy file contents from source to destination
         dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

         result = "OK";
      }
      catch (IOException e)
      {
         result = "NOT_OK";
      }


      if ( (result.equals("NOT_OK") == true) )
      {
         JOptionPane.showMessageDialog(null, "Unable to move " + main.volledig_path_srcFilename_immt + " to " + main.volledig_path_dstFilename_immt, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      }
      if ( (result.equals("OK") == true) )
      {
         // rename sourcefile to backup file (after it was copied)
         File source_file = new File(main.volledig_path_srcFilename_immt);
         File renamed_file = new File(main.volledig_path_backup_srcFilename_immt);

         if (source_file.renameTo(renamed_file) == false)
         {
            // rename failed: most of the time because a backup file of the same name already exist (2nd backup same day)
            JOptionPane.showMessageDialog(null, "Backing up log file " + main.volledig_path_srcFilename_immt + " failed (2nd move/backup same day?)", main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
            source_file.delete();        // because backup failed, immt.log still present to avoid confusing with log files, simply delete the immt.log
         }

         if (move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)
         {
            zip_log_files();
         }

         // Clearing all the observer data ? (the captain data is always cleared automatically after an upload/sending of the logs)
         //
         String info = "Clearing all the data of the observers (surname, full initials/full christian name, rank, discharge book number)";
         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
         {
            String volledig_path_observer = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;
            try 
            {
               FileChannel.open(Paths.get(volledig_path_observer), StandardOpenOption.WRITE).truncate(0).close();
               JOptionPane.showMessageDialog(null, "Successfully cleared all the data of the observers", APPLICATION_NAME + " message", JOptionPane.INFORMATION_MESSAGE);
            } 
            catch (IOException ex) 
            {
               JOptionPane.showMessageDialog(null, "Clearing all the data of the observers failed", APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         }
               
      } // if (result_opgehaald.equals("OK") == true)
   } // if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)
   

   return doorgaan;
}
   


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void zip_log_files()
{
   File file_logs_dir = new File(temp_logs_dir /*+ java.io.File.separator*/);
   String[] filenames = file_logs_dir.list(); // Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.

   for (int i = 0; i < filenames.length; i++)
   {
      filenames[i] = temp_logs_dir + java.io.File.separator + filenames[i];
   }

   // Create a buffer for reading the files
   byte[] buf = new byte[1024];

   try
   {
      // Create the ZIP file
      //String outFilename = "C:/Users/Martin/Downloads/logs/temp/logs.zip";
      //String outFilename = "C:/Users/Martin/Documents/logs.zip";
      String outFilename = temp_logs_dir + java.io.File.separator + ship_name + " " + main.LOGS_ZIP; // e.g. "C:/Users/Martin/Downloads/logs/temp/happy sailor logs.zip";
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

      // Compress the files
      for (int i = 0; i < filenames.length; i++)
      {
         //JOptionPane.showMessageDialog(null, filenames[i] , APPLICATION_NAME + " test", JOptionPane.WARNING_MESSAGE);
         File te_zippen_file = new File(filenames[i]);
         if (te_zippen_file.isDirectory() == false)     // ABSOLUUT GEEN DIRECTORIES, WANT DAN INVALID ARCHIEF
         {
            FileInputStream in = new FileInputStream(filenames[i]);

            // Add ZIP entry to output stream.
            try
            {
               //out.putNextEntry(new ZipEntry(filenames[i]));
               //out.putNextEntry(new ZipEntry(Path.GetFileName(filenames[i]))); // Path.GetFileName -> to prevent full paths are included
               out.putNextEntry(new ZipEntry(te_zippen_file.getName())); // getName -> to prevent full paths are included


               ////out.setLevel(Deflater.DEFAULT_COMPRESSION);
            }
            catch (ZipException ex)
            {
              JOptionPane.showMessageDialog(null, "zip error (Maintenance_Move_log_files_by_email_actionPerformed)"  , APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
            }


            // Transfer bytes from the input file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0)
            {
               out.write(buf, 0, len);
            }

            // Complete the entry
            out.closeEntry();
            in.close();

         } //if (test.isDirectory() == false)
      } // for (int i = 0; i < filenames.length; i++)

      // Complete the ZIP file
      out.close();

   } // try
   catch (IOException e)
   {
      JOptionPane.showMessageDialog(null, "i/o zip error (Maintenance_Move_log_files_by_email_actionPerformed)"  , APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
   }

}

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public void Kopieeren_Waarnemers_En_Aantallen()
{
   // called from: Move_log_files()-- doInbackground---  [main_support.java]

   /*
   // checking which years are present in immt.txt staan bv 2003, 2004 en 2005
   */
   
   int posi;
   int w;
   int immt_position_observer;
   int[][] aantal_waarnemer                      = new int [main.MAX_AANTAL_JAREN_IN_IMMT][main.MAX_AANTAL_WAARNEMERS];
   boolean jaar_substring_al_aanwezig;
   String record;
   String volledig_path_immt                     = logs_dir + java.io.File.separator + IMMT_LOG;
   //String moved_observername_file                = output_dir + java.io.File.separator + OBSERVER_LOG;
   //String moved_observername_file                = output_dir + java.io.File.separator + call_sign + "_" + OBSERVER_LOG;
   String moved_observername_file                = main.output_dir + java.io.File.separator + station_ID + "_" + OBSERVER_LOG;
   String[] backup_moved_observername_file_array = new String[main.MAX_AANTAL_JAREN_IN_IMMT];
   String[] moved_observername_file_array        = new String[main.MAX_AANTAL_JAREN_IN_IMMT];
   String jaar_substring;
   String waarnemer_substring                    = "";
   String observername_office;
   String immt_version;
   BufferedWriter out_0                          = null;
   BufferedWriter out_0_backup                   = null;
   BufferedWriter out_1                          = null;
   BufferedWriter out_1_backup                   = null;
   BufferedWriter out_2                          = null;
   BufferedWriter out_2_backup                   = null;
   BufferedWriter out_3                          = null;
   BufferedWriter out_3_backup                   = null;
   BufferedWriter out_4                          = null;
   BufferedWriter out_4_backup                   = null;



   /* initialisation */
   for (int p = 0; p < main.MAX_AANTAL_JAREN_IN_IMMT; p++)
   {
      main.jaar_substring_array[p] = "";
   }                                   // lege array plaats maken

   /* read all lines/records from immt log */
   try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt)))
   {
      while((record = in.readLine()) != null)
      {
         if (record.length() > main.IMMT_POSITION_IMMT_VERSION)                   // NB at least number greater than year in IMMT
         {
            jaar_substring = record.substring(1, 5);                              // eg 2006
            jaar_substring_al_aanwezig = false;

            //System.out.println("+++ jaar_substring = " + jaar_substring);

            for (int k = 0; k < main.MAX_AANTAL_JAREN_IN_IMMT; k++)
            {
               if (main.jaar_substring_array[k].equals(jaar_substring))           // this year was stored before
               {
                  jaar_substring_al_aanwezig = true;
                  break;
               }
            } // for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)


            if (jaar_substring_al_aanwezig == false)
            {
               for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++)
               {
                  if (main.jaar_substring_array[m].equals(""))                     // empty array place
                  {
                     main.jaar_substring_array[m] = jaar_substring;
                     break;
                  }
               } // for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)
            } // if (jaar_substring_al_aanwezig == false)

         } // if (obs_immt.length() > IMMT_POSITION_IMMT_VERSION)
      } // while((record = in.readLine()) != null)
   } // try
   catch (IOException ex)
   {
      // do nothing, possible file was never created
   } // catch



   /*
   // download (en backup) file namen bepalen m.b.v argument "moved_observername_file" deze is bv A:\PGDE_observer.log
   //
   // dit uitbreiden met jaartallen eerder gelezen uit immt log
   // bv A:\PGDE_observer.log -> A:\PGDE_observer_2004.log
   //                         -> A:\PGDE_observer_2005.log
   //                         -> A:\PGDE_observer_2006.log
   //
   // dus nu meerdere download file namen
   */


   /* initialisation */
   for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)
   {
      moved_observername_file_array[j] = "";
      backup_moved_observername_file_array[j] = "";
   }

   /* positie bepalen waar jaartal tussengevoegd moet worden (bv A:\observer.log -> A:\observer_2006.log) */
   posi = moved_observername_file.indexOf(".log");              // nb variable pos wordt ook gebruikt in TPoint

   /* (absolute) filenamen van de download (en download backup) observer files bepalen */

   cal_systeem_datum_tijd = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));// geeft systeem datum tijd in UTC van dit moment
   SimpleDateFormat sdf2;
   sdf2 = new SimpleDateFormat("MMMM dd, yyyy");                            // e.g "MMMM dd, yyyy" -> februari 27, 2010
   String systeem_date_time = sdf2.format(cal_systeem_datum_tijd.getTime());

      //volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP " + systeem_date_time + ".TXT";


   for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)
   {
      if (main.jaar_substring_array[j].compareTo("") != 0)
      {
         /* eg A:\PGDE_observer_2006.log + A:\PGDE_observer_2007.log) */
         moved_observername_file_array[j] = moved_observername_file.substring(0, posi) + "_" + main.jaar_substring_array[j] + ".log";
         /* bepalen naam van de observername backup file (altijd op een vaste plaats) */

         /* eg PGDE_OBSERVER_2006_BACKUP November 22, 2009.txt + PGDE_OBSERVER_2007_BACKUP November 22, 2009.txt */
         //backup_moved_observername_file_array[j] = logs_dir + java.io.File.separator + "OBSERVER_" + jaar_substring_array[j] + "_BACKUP " + systeem_date_time + ".TXT";
         //backup_moved_observername_file_array[j] = logs_dir + java.io.File.separator + call_sign + "_" + "OBSERVER_" + jaar_substring_array[j] + "_BACKUP " + systeem_date_time + ".TXT";
         backup_moved_observername_file_array[j] = logs_dir + java.io.File.separator + station_ID + "_" + "OBSERVER_" + main.jaar_substring_array[j] + "_BACKUP " + systeem_date_time + ".TXT";
      } // if (jaar_substring_array[k] != "")
   } // for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)




   /*
   // NB you know here that observer name file in this stage is present
   */

   /* initialisation */
   for (int b = 0; b < main.MAX_AANTAL_WAARNEMERS; b++)
   {
      main.observername_array[b] = "";
   }

   String volledig_path_observer = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;

   /* read all lines/records from observer log */
   try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_observer)))
   {
      w = 0;
      while((record = in.readLine()) != null)
      {
         if (w < main.MAX_AANTAL_WAARNEMERS)               // extra check
         {
            main.observername_array[w++] = record;
         }
      } // while((record = in.readLine()) != null)

   } // try
   catch (IOException ex)
   {
      // do nothing, possible file was never created
   } // catch



   /*
   // uitlezen van immt log file om het aantal waarnemingen per waarnemer te tellen
   // NB je weet zeker dat de immt.log in deze fase aanwezig is
   */

   for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++)
   {
      for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS; i++)
      {
         aantal_waarnemer[m][i] = 0;
      }
   }

   /* read all lines/records from immt log */
   try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt)))
   {
      while ((record = in.readLine()) != null)
      {
         /* eerst immt version bepalen want dan is pas bekend wat de positie van de waarnemer is */
         if (record.length() > main.IMMT_POSITION_IMMT_VERSION)
         {
            immt_version = record.substring(main.IMMT_POSITION_IMMT_VERSION, main.IMMT_POSITION_IMMT_VERSION + 1);

            if (immt_version.equals("3") == true)
            {
               immt_position_observer = main.IMMT_3_POSITION_OBSERVER;
            }
            else if (immt_version.equals("4") == true)
            {
               immt_position_observer = main.IMMT_4_POSITION_OBSERVER;
            }
            else if (immt_version.equals("5") == true)
            {
               immt_position_observer = main.IMMT_5_POSITION_OBSERVER;
            }
            else
            {
               immt_position_observer = INVALID;                                 //  dan zal verderop geen waarnemer uit de record worden gelezen
            }

            //if (record.length() > IMMT_POSITION_OBSERVER - 1)
            if (record.length() > immt_position_observer - 1)
            {
               waarnemer_substring = record.substring(immt_position_observer);    //record.substring(IMMT_POSITION_OBSERVER);
               jaar_substring      = record.substring(1, 5);                      // eg 2010

               for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS; i++)
               {
                  if (main.observername_array[i].compareTo("") != 0)
                  {
                     // NB waarnemer_substring  : surname - ; - initials (separated by .'s) - ; - rank - ; - discharge book number 
                     // NB observername_array[i]: surname - ; - initials (separated by .'s) - ; - rank - ; - discharge book number 

                     if (waarnemer_substring.equals(main.observername_array[i]) == true)
                     {
                        for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++)
                        {
                           if (jaar_substring.equals(main.jaar_substring_array[m]))
                           {
                              aantal_waarnemer[m][i]++;
                           }
                        } // for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++)
                     } // if (waarnemer_substring.equals(hulp_observername))
                  } // if (observername_array[i].compareTo("") != 0)
               } // for (i = 0; i < MAX_AANTAL_WAARNEMERS: i++)
            } // if (obs_immt.length() > IMMT_POSITION_OBSERVER -1)
         } // if (record.length() > IMMT_POSITION_IMMT_VERSION)
      } // while((record = in.readLine()) != null)
   } // try
   catch (IOException ex)
   {
     // do nothing, possible file was never created
   } // catch




   /*
   // observers and number of made observations to download and backup file(s)
   */

   /* open the moved(download) and backup observer files */
   if (moved_observername_file_array[0].compareTo("") != 0)
   {
      try
      {
         out_0        = new BufferedWriter(new FileWriter(moved_observername_file_array[0]));
         out_0_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[0]));
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[0].compareTo("") != 0)
   if (moved_observername_file_array[1].compareTo("") != 0)
   {
      try
      {
         out_1        = new BufferedWriter(new FileWriter(moved_observername_file_array[1]));
         out_1_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[1]));
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[2].compareTo("") != 0)
   if (moved_observername_file_array[2].compareTo("") != 0)
   {
      try
      {
         out_2        = new BufferedWriter(new FileWriter(moved_observername_file_array[2]));
         out_2_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[2]));
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[2].compareTo("") != 0)
   if (moved_observername_file_array[3].compareTo("") != 0)
   {
      try
      {
         out_3        = new BufferedWriter(new FileWriter(moved_observername_file_array[3]));
         out_3_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[3]));
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[3].compareTo("") != 0)
   if (moved_observername_file_array[4].compareTo("") != 0)
   {
      try
      {
         out_4        = new BufferedWriter(new FileWriter(moved_observername_file_array[4]));
         out_4_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[4]));
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[4].compareTo("") != 0)




   /* write to the moved and backup files */
   for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++)
   {
      for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS; i++)
      {
         observername_office = main.observername_array[i];

         if (observername_office.compareTo("") != 0)
         {
            //observername_office += "\t";                // tab
            observername_office += Integer.toString(aantal_waarnemer[m][i]);      // number of observations waarnemingen

            if (m == 0 && aantal_waarnemer[m][i] != 0)
            {
               try
               {
                  if (out_0 != null)
                  {   
                     out_0.write(observername_office);
                     out_0.newLine();
                  }
               }
               catch (IOException ex) { }
               try
               {
                  if (out_0_backup != null)
                  {   
                     out_0_backup.write(observername_office);
                     out_0_backup.newLine();
                  }
               }
               catch (IOException ex) { }
            } // if (m == 0 && aantal_waarnemer[m][i] != 0)
            else if (m == 1 && aantal_waarnemer[m][i] != 0)
            {
               try
               { 
                  if (out_1 != null)
                  {   
                     out_1.write(observername_office);
                     out_1.newLine();
                  }
               }
               catch (IOException ex) { }
               try
               {
                  if (out_1_backup != null)
                  {   
                     out_1_backup.write(observername_office);
                     out_1_backup.newLine();
                  }
               }
               catch (IOException ex) {  }
            } // else if (m == 2 && aantal_waarnemer[j][i] != 0)
            else if (m == 2 && aantal_waarnemer[m][i] != 0)
            {
               try
               {
                  if (out_2 != null)
                  {   
                     out_2.write(observername_office);
                     out_2.newLine();
                  }
               }
               catch (IOException ex) { }
               try
               {
                  if (out_2_backup != null)
                  {   
                     out_2_backup.write(observername_office);
                     out_2_backup.newLine();
                  }
               } catch (IOException ex) { }
            } // else if (m == 2 && aantal_waarnemer[m][i] != 0)
            else if (m == 3 && aantal_waarnemer[m][i] != 0)
            {
               try
               {
                  if (out_3 != null)
                  {   
                     out_3.write(observername_office);
                     out_3.newLine();
                  }
               }
               catch (IOException ex) {  }
               try
               {
                  if (out_3_backup != null)
                  {   
                     out_3_backup.write(observername_office);
                     out_3_backup.newLine();
                  }
               }
               catch (IOException ex) {  }
            } // else if (m == 3 && aantal_waarnemer[m][i] != 0)
            else if (m == 4 && aantal_waarnemer[m][i] != 0)
            {
               try
               {
                  if (out_4 != null)
                  {   
                     out_4.write(observername_office);
                     out_4.newLine();
                  }
               }
               catch (IOException ex) {  }
               try
               {
                  if (out_4_backup != null)
                  {   
                     out_4_backup.write(observername_office);
                     out_4_backup.newLine();
                  }
               }
               catch (IOException ex) {  }
            } // else if (m == 4 && aantal_waarnemer[m][i] != 0)
         } // if (observername_office != "")
      } // for (i = 0; i < MAX_AANTAL_WAARNEMERS; i++)
   } // for (int m = 0; m < MAX_AANTAL_JAREN_IN_IMMT; m++)




   /* close all the (moved and backup) observer files */
   if (moved_observername_file_array[0].compareTo("") != 0)
   {
      try
      {
         if (out_0 != null)
         {
            out_0.close();
         }   
         if (out_0_backup != null)
         {
            out_0_backup.close();
         }   
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[0].compareTo("") != 0)
   if (moved_observername_file_array[1].compareTo("") != 0)
   {
      try
      {
         if (out_1 != null)
         {   
            out_1.close();
         }
         if (out_1_backup != null)
         {   
            out_1_backup.close();
         }   
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[2].compareTo("") != 0)
   if (moved_observername_file_array[2].compareTo("") != 0)
   {
      try
      {
         if (out_2 != null)
         {   
            out_2.close();
         }
         if (out_2_backup != null)
         {   
            out_2_backup.close();
         }
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[2].compareTo("") != 0)
   if (moved_observername_file_array[3].compareTo("") != 0)
   {
      try
      {
         if (out_3 != null)
         {   
            out_3.close();
         }
         if (out_3_backup != null)
         {   
            out_3_backup.close();
         }   
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[3].compareTo("") != 0)
   if (moved_observername_file_array[4].compareTo("") != 0)
   {
      try
      {
         if (out_4 != null)
         {
            out_4.close();
         }
         if (out_4_backup != null)
         {   
            out_4_backup.close();
         }   
      } // try
      catch (Exception e) { /* ... */}
   } // if (moved_observername_file_array[4].compareTo("") != 0)


}
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
public boolean checking_level_2()
{
   boolean doorgaan                               = true;
   boolean level_2_ok                             = true;
   boolean wind_waves_period_conversion_ok        = true;
   boolean wind_waves_height_conversion_ok        = true;
   boolean pressure_amount_tendency_conversion_ok = true;
   boolean cl_code_conversion_ok                  = true;
   boolean cm_code_conversion_ok                  = true;
   boolean ch_code_conversion_ok                  = true;
   boolean ww_code_conversion_ok                  = true;
   boolean VV_code_conversion_ok                  = true;
   boolean air_temp_conversion_ok                 = true;
   float float_wind_waves_period                  = main.INVALID;
   float float_wind_waves_height                  = main.INVALID; 
   float float_pressure_amount_tendency           = main.INVALID;
   float float_air_temp                           = main.INVALID;
   int int_cl_code                                = main.INVALID;
   int int_cm_code                                = main.INVALID;
   int int_ch_code                                = main.INVALID;
   int int_ww_code                                = main.INVALID;
   int int_VV_code                                = main.INVALID;
   Integer sky_not_discernible_array[]            = {43, 45, 47, 49};
   Integer drizzle_rain_array[]                   = {50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69};
   Integer present_weather_36_39_array[]          = {36, 37, 38, 39};
   Integer present_weather_48_49_array[]          = {48, 49};
   Integer present_weather_56_57_array[]          = {56, 57};
   Integer present_weather_66_67_array[]          = {66, 67};
   Integer present_weather_68_69_array[]          = {68, 69};
   Integer present_weather_70_75_array[]          = {70, 71, 72, 73, 74, 75};
   Integer present_weather_76_79_array[]          = {76, 77, 78, 79};
   Integer present_weather_83_86_array[]          = {83, 84, 85, 86};
   Integer cl_1_9_array[]                         = {1, 2, 3, 4, 5, 6, 7, 8, 9};
   Integer ch_1_9_array[]                         = {1, 2, 3, 4, 5, 6, 7, 8, 9};
   Integer fog_array[]                            = {42, 43, 44, 45, 46, 47, 48, 49};
   Integer visibility_95_99_array[]               = {95, 96, 97, 98, 99};
   Integer visibility_90_93_array[]               = {90, 91, 92, 93};
   
   
   //
   ///////////////////////////// conversions //////////////////////////
   //
   
   // wind_waves_period conversion
   try
   {
      if (mywaves.wind_waves_period.equals("") == false && mywaves.wind_waves_period != null)
      {
         float_wind_waves_period = Float.parseFloat(mywaves.wind_waves_period);
         wind_waves_period_conversion_ok = true;
      }
      else
      {
         wind_waves_period_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] wind waves period conversion error; Function: checking_level_2()");
      wind_waves_period_conversion_ok = false;
   }   
   
   // wind_waves_height conversion
   try
   {
      if (mywaves.wind_waves_height.equals("") == false && mywaves.wind_waves_height != null)
      {
        float_wind_waves_height = Float.parseFloat(mywaves.wind_waves_height);
        wind_waves_height_conversion_ok = true;
      }
      else
      {
         wind_waves_height_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] wind waves height conversion error; Function: checking_level_2()");
      wind_waves_height_conversion_ok = false;
   }   
   
   // pressure amount tendency conversion
   try
   {
      if (mybarograph.pressure_amount_tendency.equals("") == false && mybarograph.pressure_amount_tendency != null)
      {
         float_pressure_amount_tendency = Float.parseFloat(mybarograph.pressure_amount_tendency);
         pressure_amount_tendency_conversion_ok = true;
      }
      else
      {
         pressure_amount_tendency_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] pressure amount tendency conversion error; Function: checking_level_2()");
      pressure_amount_tendency_conversion_ok = false;
   }
   
    // string Cl code conversion to int
   try
   {
      if (mycl.cl_code.equals("") == false && mycl.cl_code != null)
      {
         int_cl_code = Integer.parseInt(mycl.cl_code);
         cl_code_conversion_ok = true;
      }
      else
      {
         cl_code_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] Cl conversion error; Function: checking_level_2()");
      cl_code_conversion_ok = false;
   }   
  
   // string Cm code conversion to int
   try
   {
      if (mycm.cm_code.equals("") == false && mycm.cm_code != null)
      {
         int_cm_code = Integer.parseInt(mycm.cm_code.substring(0, 1));   // to eliminate the a,b or c in 7a, 7b, 7c Cm code (see mycm.java)
         cm_code_conversion_ok = true;
      }
      else
      {
         cm_code_conversion_ok = false; 
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] Cm conversion error; Function: checking_level_2()");
      cm_code_conversion_ok = false;
   }   
   
   // string Ch code conversion to int
   try
   {
      if (mych.ch_code.equals("") == false && mych.ch_code != null)
      {
         int_ch_code = Integer.parseInt(mych.ch_code);
         ch_code_conversion_ok = true;
      }
      else
      {
         ch_code_conversion_ok = false;
      }   
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] Ch conversion error; Function: checking_level_2()");
      ch_code_conversion_ok = false;
   }   
   
   // string ww code conversion to int
   try
   {
      if (mypresentweather.ww_code.equals("") == false && mypresentweather.ww_code != null)
      {
         int_ww_code = Integer.parseInt(mypresentweather.ww_code);
         ww_code_conversion_ok = true;
      }
      else
      {
         ww_code_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] ww conversion error; Function: checking_level_2()");
      ww_code_conversion_ok = false;
   }      
   
   // string VV code conversion to int
   try
   {
      if (myvisibility.VV_code.equals("") == false && myvisibility.VV_code != null)
      {
         int_VV_code = Integer.parseInt(myvisibility.VV_code);
         VV_code_conversion_ok = true;
      }
      else
      {
         VV_code_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] VV conversion error; Function: checking_level_2()");
      VV_code_conversion_ok = false;
   }         
   
   // string air temp conversion to float
   try
   {
      if (mytemp.air_temp.equals("") == false && mytemp.air_temp != null)
      {
         float_air_temp = Float.parseFloat(mytemp.air_temp);
         air_temp_conversion_ok = true;
      }
      else
      {
         air_temp_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] air temp conversion error; Function: checking_level_2()");
      air_temp_conversion_ok = false;
   }   
   
   
   
   //
   ///////////////////////////// checks //////////////////////////
   //
   
   //
   ////////// wind - waves checks /////
   //
   
   // wind speed <-> sea period
   if ((doorgaan == true) && wind_waves_period_conversion_ok == true)
   {
      if ( (mywind.int_true_wind_speed == 0) && (float_wind_waves_period > 0.01 && float_wind_waves_period < 99.9)            )
      {
         JOptionPane.showMessageDialog(null, "if (true) wind speed is 0, wind waves period must be 0 or blank", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         level_2_ok = false;
         doorgaan = false;
      }
   }
   
   // wind speed <-> sea height
   if ( (doorgaan == true) && (wind_waves_height_conversion_ok == true) ) 
   {
      if ( (mywind.int_true_wind_speed == 0) && (float_wind_waves_height > 0.01 && float_wind_waves_height < 99.9) )
      {
         JOptionPane.showMessageDialog(null, "if (true) wind speed is 0, wind waves height must be 0 or blank", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         level_2_ok = false;
         doorgaan = false;
      }
   }
   
   // wind speed <-> sea height
   if ( (doorgaan == true) && (wind_waves_height_conversion_ok == true) ) 
   {
      if ( (main.wind_units.trim().indexOf(main.M_S) != -1) && (mywind.int_true_wind_speed >= 0 && mywind.int_true_wind_speed <= 3) && (float_wind_waves_height > 9.7 && float_wind_waves_height < 99.9) )
      {
         // wind speed in m/s
         JOptionPane.showMessageDialog(null, "if (true) wind speed in range 0 - 3 m/s, wind waves height must be < 9.8 m or blank", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         level_2_ok = false;
         doorgaan = false;
      }
      else if ( (main.wind_units.trim().indexOf(main.KNOTS) != -1) && (mywind.int_true_wind_speed >= 0 && mywind.int_true_wind_speed <= 6) && (float_wind_waves_height > 9.7 && float_wind_waves_height < 99.9) )
      {
         // wind speed in knots
         JOptionPane.showMessageDialog(null, "if (true) wind speed in range 0 - 6 knots, wind waves height must be < 9.8 m or blank", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         level_2_ok = false;
         doorgaan = false;
      }
   }
   
   
   //
   ////////// air pressure /////
   //
   
   // pressure characteristic <-> pressure tendency
   if ( (doorgaan == true) && (pressure_amount_tendency_conversion_ok == true) )
   {      
      if (mybarograph.a_code.equals("4") && mybarograph.pressure_amount_tendency.equals(""))
      {
         JOptionPane.showMessageDialog(null, "if air pressure characteristic is steady (a = 0), amount of pressure tendency must be 0", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         level_2_ok = false;
         doorgaan = false;
      }
      else if ( (mybarograph.a_code.equals("4") && float_pressure_amount_tendency > 0.01 && float_pressure_amount_tendency < 50.0) )
      {
         JOptionPane.showMessageDialog(null, "if air pressure characteristic is steady (a = 0), amount of pressure tendency must be 0", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         level_2_ok = false;
         doorgaan = false;
      }
   }
   
   
   //
   ////////// cloud cover <-> cloud types /////
   //
   if ((doorgaan == true) && (mycloudcover.N.equals(mycloudcover.N_CLOUDLESS)) && mycl.cl_code.equals("0") == false)
   {
      JOptionPane.showMessageDialog(null, "if total cloud cover is 'cloudless', Cl must be 'no clouds Cl'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;
   }
   
   if ((doorgaan == true) && (mycloudcover.N.equals(mycloudcover.N_CLOUDLESS)) && mycm.cm_code.equals("0") == false)
   {
      JOptionPane.showMessageDialog(null, "if total cloud cover is 'cloudless', Cm must be 'no clouds Cm'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;
   }
   
   if ((doorgaan == true) && (mycloudcover.N.equals(mycloudcover.N_CLOUDLESS)) && mych.ch_code.equals("0") == false)
   {
      JOptionPane.showMessageDialog(null, "if total cloud cover is 'cloudless', Ch must be 'no clouds Ch'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;
   }
   
   if ((doorgaan == true) && (mycl.cl_code.equals("0") && mycm.cm_code.equals("0") && mych.ch_code.equals("0") && mycloudcover.N.equals(mycloudcover.N_CLOUDLESS) == false))
   {
      JOptionPane.showMessageDialog(null, "if Cl and Cm and Ch is 'no clouds', total cloud cover must be 'cloudless'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;
   }
   
   if ((doorgaan == true) && (mycloudcover.Nh.equals(mycloudcover.NH_0_8) && mycl.cl_code.equals("0") == false))
   {
      JOptionPane.showMessageDialog(null, "if 'amount of Cl (or Cm if Cl not present)' is '0/8', Cl must be 'no clouds Cl'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   if ((doorgaan == true) && (mycloudcover.Nh.equals(mycloudcover.NH_0_8) && mycl.cl_code.equals("0") && mycm.cm_code.equals("0") == false))
   {
      JOptionPane.showMessageDialog(null, "if 'amount of Cl (or Cm if Cl not present)' is '0/8' and Cl is 'no clouds Cl', Cm must be 'no clouds Cm'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }   
   
   if ((doorgaan == true) && mycl.cl_code.equals("0") && mycm.cm_code.equals("0") && mycloudcover.Nh.equals(mycloudcover.NH_0_8) == false)
   {
      JOptionPane.showMessageDialog(null, "if Cl and Cm is 'no clouds', amount of Cl (or Cm if Cl not present)' must be '0/8'  ", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   if ((doorgaan == true) && ch_code_conversion_ok && (mycloudcover.Nh.equals(mycloudcover.NH_8_8)) && (int_ch_code >= 0 && int_ch_code <= 9))
   {
      JOptionPane.showMessageDialog(null, "if 'amount of Cl (or Cm if Cl not present)' is '8/8', Ch must be 'not determined'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   if ((doorgaan == true) && cl_code_conversion_ok && cm_code_conversion_ok && (mycloudcover.Nh.equals(mycloudcover.NH_8_8)) && (int_cl_code >= 1 && int_cl_code <= 9) && (int_cm_code >= 0 && int_cm_code <= 9))
   {
      JOptionPane.showMessageDialog(null, "if 'amount of Cl (or Cm if Cl not present)' is '8/8' and Cl was determined (in range 1 - 9 or 'no clouds Cl'), Cm must be 'not determined'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   
   //
   ////////// cloud cover <-> present weather /////
   //
   if ((doorgaan == true) && ww_code_conversion_ok && (Arrays.asList(sky_not_discernible_array).indexOf(int_ww_code) != -1) && 
       (mycloudcover.N.equals(mycloudcover.N_NOT_DETERMINED) == false && mycloudcover.N.equals(mycloudcover.N_OBSCURED) == false))
   {
      JOptionPane.showMessageDialog(null, "if 'present weather' is 'fog with sky not discernable', 'total cloud cover' must be 'obscured' or 'not determined'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   if ((doorgaan == true) && ww_code_conversion_ok && (Arrays.asList(sky_not_discernible_array).indexOf(int_ww_code) != -1) && (mycl.cl_code.equals("") == false))
   {
      JOptionPane.showMessageDialog(null, "if 'present weather' is 'fog with sky not discernable', Cl must be 'not determined'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   if ((doorgaan == true) && ww_code_conversion_ok && (Arrays.asList(sky_not_discernible_array).indexOf(int_ww_code) != -1) && (mycm.cm_code.equals("") == false))
   {
      JOptionPane.showMessageDialog(null, "if 'present weather' is 'fog with sky not discernable', Cm must be 'not determined'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   if ((doorgaan == true) && ww_code_conversion_ok && (Arrays.asList(sky_not_discernible_array).indexOf(int_ww_code) != -1) && (mych.ch_code.equals("") == false))
   {
      JOptionPane.showMessageDialog(null, "if 'present weather' is 'fog with sky not discernable', Ch must be 'not determined'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   if ((doorgaan == true) && (mycloudcover.N.equals(mycloudcover.N_CLOUDLESS)) && ww_code_conversion_ok && (Arrays.asList(drizzle_rain_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "if 'present weather' is drizzle or rain, 'total cloud cover' cannot be 'cloudless'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   
   //
   ////////// cloud type <-> cloud height /////
   //
   if ((doorgaan == true) && cl_code_conversion_ok && (Arrays.asList(cl_1_9_array).indexOf(int_cl_code) != -1) && 
       (mycloudcover.h.equals(mycloudcover.H_GROTER_2500) || mycloudcover.h.equals(mycloudcover.H_CLOUDLESS)))
   {
      JOptionPane.showMessageDialog(null, "if type Cl cloud is observed, 'height of base of lowest cloud' cannot be '>=2500m (8000 ft)' and not 'cloudless'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   if ((doorgaan == true) && ch_code_conversion_ok && mycl.cl_code.equals("0") && mycm.cm_code.equals("0") && (Arrays.asList(ch_1_9_array).indexOf(int_ch_code) != -1) &&
       (mycloudcover.h.equals(mycloudcover.H_0_50) || mycloudcover.h.equals(mycloudcover.H_50_100) || mycloudcover.h.equals(mycloudcover.H_100_200)))
   {
      JOptionPane.showMessageDialog(null, "if Cl = 'no clouds Cl' and Cm = 'no clouds Cm' and Ch in range 1 - 9, 'height of base of lowest cloud in the sky' cannot be < 200 m (< 600 ft)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false; 
   }
   
   
   //
   ////////// visibilty <-> present weather /////
   //   
   if ((doorgaan == true) && ww_code_conversion_ok && (Arrays.asList(fog_array).indexOf(int_ww_code) != -1) &&
                             VV_code_conversion_ok && (Arrays.asList(visibility_95_99_array).indexOf(int_VV_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "if present weather = 'fog', visibility cannot be > 0.5 nm (an exception is made for 'fog banks', 'fog in patches' and 'shallow fog')", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }   
   
   if ((doorgaan == true) && ww_code_conversion_ok && (int_ww_code == 40) &&
                             VV_code_conversion_ok && (Arrays.asList(visibility_90_93_array).indexOf(int_VV_code) != -1))
   {
      String info = "if present weather = " + "\"" + mypresentweather.ww_40  +  "\"" + ", reported visibility cannot be < 0.5 nm";  // to put "\"" diect in JOptionPane.showMessageDialog not allowed
      JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
      
      
   //
   ////////// present weather <-> air temperature /////
   //    
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) &&
                            (Arrays.asList(present_weather_36_39_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'drifting snow' or 'blowing snow'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
   
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) &&
                            (Arrays.asList(present_weather_48_49_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'depositing rime'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
   
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) &&
                            (Arrays.asList(present_weather_56_57_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'freezing drizzle'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
   
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) &&
                            (Arrays.asList(present_weather_66_67_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'freezing rain'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
    
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) &&
                            (Arrays.asList(present_weather_68_69_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'snow'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
      
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) &&
                            (Arrays.asList(present_weather_70_75_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'snow flakes'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
   
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) &&
                            (Arrays.asList(present_weather_76_79_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'snow grains/crystals' or 'ice prisms/pellets'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
       
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) &&
                            (Arrays.asList(present_weather_83_86_array).indexOf(int_ww_code) != -1))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'present weather' can not indicate 'snow shower(s)'", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
       
   
   //
   ////////// icing <-> air temperature /////
   // 
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 20.0 && float_air_temp < 99.9) && 
                             (!myicing.Is_code.equals("") || !myicing.EsEs_code.equals("") || !myicing.Rs_code.equals("")))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 20.0 \u00B0C then 'Icing (Ice accretion)' is not possible", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
   

   //
   ////////// ice <-> air temperature /////
   // 
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 25.0 && float_air_temp < 99.9) &&
                             (!myice1.ci_code.equals("") || !myice1.Si_code.equals("") || !myice1.bi_code.equals("") || !myice1.Di_code.equals("") || !myice1.zi_code.equals("")))
   {
      JOptionPane.showMessageDialog(null, "If 'air temperature' > 25.0 \u00B0C then 'Ice' is not possible", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      level_2_ok = false;
      doorgaan = false;       
   }
   
   
   return level_2_ok;
}
   


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
public boolean checking_level_3()
{
   boolean doorgaan                               = true;
   boolean level_3_ok                             = true;   
   boolean wind_waves_period_conversion_ok        = true;
   boolean wind_waves_height_conversion_ok        = true;
   boolean first_swell_period_conversion_ok       = true;
   boolean first_swell_height_conversion_ok       = true;
   boolean second_swell_period_conversion_ok      = true;
   boolean second_swell_height_conversion_ok      = true;
   boolean air_temp_conversion_ok                 = true;
   boolean air_pressure_conversion_ok             = true;
   boolean amount_pressure_tendency_conversion_ok = true;
   boolean sea_water_temp_conversion_ok           = true;
   boolean ice_thickness_conversion_ok            = true;
   boolean ww_code_conversion_ok                  = true;
   float float_wind_waves_period                  = main.INVALID;
   float float_wind_waves_height                  = main.INVALID; 
   float float_first_swell_period                 = main.INVALID;
   float float_first_swell_height                 = main.INVALID;
   float float_second_swell_period                = main.INVALID;
   float float_second_swell_height                = main.INVALID;
   float float_air_temp                           = main.INVALID;
   float float_air_pressure_msl_corrected         = main.INVALID;
   float float_amount_pressure_tendency           = main.INVALID;
   float float_sea_water_temp                     = main.INVALID;
   float float_ice_thickness                      = main.INVALID;
   int int_ww_code                                = main.INVALID;
   Integer present_weather_48_49_array[]          = {48, 49};
   Integer present_weather_56_57_array[]          = {56, 57};
   Integer present_weather_66_67_array[]          = {66, 67};

   
   //
   ///////////////////////////// conversions //////////////////////////
   //
   
   // wind_waves_period conversion
   try
   {
      if (mywaves.wind_waves_period.equals("") == false && mywaves.wind_waves_period != null)
      {
         float_wind_waves_period = Float.parseFloat(mywaves.wind_waves_period);
         wind_waves_period_conversion_ok = true;
      }
      else
      {
         wind_waves_period_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] wind waves period conversion error; Function: checking_level_3()");
      wind_waves_period_conversion_ok = false;
   }   
   
   // wind_waves_height conversion
   try
   {
      if (mywaves.wind_waves_height.equals("") == false && mywaves.wind_waves_height != null)
      {
         float_wind_waves_height = Float.parseFloat(mywaves.wind_waves_height);
         wind_waves_height_conversion_ok = true;
      }
      else
      {
         wind_waves_period_conversion_ok = false;
      }         
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] wind waves height conversion error; Function: checking_level_3()");
      wind_waves_height_conversion_ok = false;
   }   
   
   // first swell system period conversion
   try
   {
      if (mywaves.swell_1_period.equals("") == false && mywaves.swell_1_period != null)
      {
         float_first_swell_period = Float.parseFloat(mywaves.swell_1_period);
         first_swell_period_conversion_ok = true;
      }
      else
      {
         first_swell_period_conversion_ok = false;
      }         
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] first swell period conversion error; Function: checking_level_3()");
      first_swell_period_conversion_ok = false;
   }   
   
   // first swell system height conversion
   try
   {
      if (mywaves.swell_1_height.equals("") == false && mywaves.swell_1_height != null)
      {
         float_first_swell_height = Float.parseFloat(mywaves.swell_1_height);
         first_swell_height_conversion_ok = true;
      }
      else
      {
         first_swell_height_conversion_ok = false;
      }         
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] first swell height conversion error; Function: checking_level_3()");
      first_swell_height_conversion_ok = false;
   }   
   
   // second swell system period conversion
   try
   {
      if (mywaves.swell_2_period.equals("") == false && mywaves.swell_2_period != null)
      {
         float_second_swell_period = Float.parseFloat(mywaves.swell_2_period);
         second_swell_period_conversion_ok = true;
      }
      else
      {
         second_swell_period_conversion_ok = false;
      }         
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] second swell period conversion error; Function: checking_level_3()");
      second_swell_period_conversion_ok = false;
   }   
   
   // second swell system height conversion
   try
   {
      if (mywaves.swell_2_height.equals("") == false && mywaves.swell_2_height != null)
      {
         float_second_swell_height = Float.parseFloat(mywaves.swell_2_height);
         second_swell_height_conversion_ok = true;
      }
      else
      {
         second_swell_height_conversion_ok = false;
      }         
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] second swell height conversion error; Function: checking_level_3()");
      second_swell_height_conversion_ok = false;
   }   
   
   // string air_temp conversion to float
   try
   {
      if (mytemp.air_temp.equals("") == false && mytemp.air_temp != null)
      {
         float_air_temp = Float.parseFloat(mytemp.air_temp);
         air_temp_conversion_ok = true;
      }
      else
      {
         air_temp_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] air temp conversion error; Function: checking_level_3()");
      air_temp_conversion_ok = false;
   }
   
   // string pressure_msl_corrected to float
   try
   {   
      if (mybarometer.pressure_msl_corrected.equals("") == false && mybarometer.pressure_msl_corrected != null)
      {
         float_air_pressure_msl_corrected = Float.parseFloat(mybarometer.pressure_msl_corrected);
         air_pressure_conversion_ok = true;
      }
      else
      {
         air_pressure_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] air pressure conversion error; Function: checking_level_3()");
      air_pressure_conversion_ok = false;
   }              
   
   // string amount pressure tendency to float
   try
   {   
      if (mybarograph.pressure_amount_tendency.equals("") == false && mybarograph.pressure_amount_tendency != null)
      {
         float_amount_pressure_tendency = Float.parseFloat(mybarograph.pressure_amount_tendency);
         amount_pressure_tendency_conversion_ok = true;
      }
      else
      {
         amount_pressure_tendency_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] amount air pressure tendency conversion error; Function: checking_level_3()");
      amount_pressure_tendency_conversion_ok = false;
   }              
   
   // string SST to float
   try
   {
      if (mytemp.sea_water_temp.equals("") == false && mytemp.sea_water_temp != null)
      {
         float_sea_water_temp = Float.parseFloat(mytemp.sea_water_temp);
         sea_water_temp_conversion_ok = true;
      }
      else
      {
         sea_water_temp_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] SST conversion error; Function: checking_level_3()");
      sea_water_temp_conversion_ok = false;
   }
   
   // string thickness ice accretion (EsEs) to float
   try
   {
      if (myicing.EsEs_code.equals("") == false && myicing.EsEs_code != null)
      {
         float_ice_thickness = Float.parseFloat(myicing.EsEs_code);                      // EsEs_code = ice thickness in centimetres
         ice_thickness_conversion_ok = true;
      }
      else
      {
         ice_thickness_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      main.log_turbowin_system_message("[GENERAL] ice thickness (EsEs) conversion error; Function: checking_level_3()");
      ice_thickness_conversion_ok = false;
   }
   
   // string ww code conversion to int
   try
   {
      if (mypresentweather.ww_code.equals("") == false && mypresentweather.ww_code != null)
      {
         int_ww_code = Integer.parseInt(mypresentweather.ww_code);
         ww_code_conversion_ok = true;
      }
      else
      {
         ww_code_conversion_ok = false;
      }
   }
   catch (NumberFormatException ex)
   {
      //doorgaan = true;
      main.log_turbowin_system_message("[GENERAL] ww conversion error; Function: checking_level_3()");
      ww_code_conversion_ok = false;
   }      
   
   
   
   //
   ///////////////////////////// checks //////////////////////////
   //
   
   
   // speed ship
   //
   if ((doorgaan == true) && (myposition.vs_code.equals("8") || myposition.vs_code.equals("9")))
   {
      String info = "Ship's speed > 35 knots \n Press the NO button if it was a typing error, press the YES button if this average speed is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
   
   // wind speed
   //
   if ( (doorgaan == true) && (main.wind_units.trim().indexOf(main.M_S) != -1) && (mywind.int_true_wind_speed > 28 && mywind.int_true_wind_speed < 500.0) )
   {
      // wind speed in m/s
      String info = "Wind speed > 28 m/s (> 55 knots) \n Press the NO button if it was a typing error, press the YES button if this wind speed is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }

   if ( (doorgaan == true) && (main.wind_units.trim().indexOf(main.KNOTS) != -1) && (mywind.int_true_wind_speed > 55 && mywind.int_true_wind_speed < 500.0) )
   {
      // wind speed in m/s
      String info = "Wind speed > 55 knots \n Press the NO button if it was a typing error, press the YES button if this wind speed is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }

   
   // wind waves period
   //
   if ((doorgaan == true) && wind_waves_period_conversion_ok == true)
   {
      if (float_wind_waves_period > 25.0 && float_wind_waves_period < 99.9)
      {
         String info = "Wind waves period > 25 seconds \n Press the NO button if it was a typing error, press the YES button if this wind waves period is ok";
         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
         {
            JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            doorgaan = false;
            level_3_ok = false;
         }
      }
   }
   
   
   // wind waves height
   //
   if ((doorgaan == true) && wind_waves_height_conversion_ok == true)
   {
      if (float_wind_waves_height > 12.2 && float_wind_waves_height < 99.9)
      {
         String info = "Wind waves height > 12.2 metres \n Press the NO button if it was a typing error, press the YES button if this wind waves height is ok";
         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
         {
            JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            doorgaan = false;
            level_3_ok = false;
         }
      }
   }   
   
   
   // first swell period
   //
   if ((doorgaan == true) && first_swell_period_conversion_ok == true)
   {
      if (float_first_swell_period > 25.0 && float_first_swell_period < 99.9)
      {
         String info = "First swell system waves period > 25 seconds \n Press the NO button if it was a typing error, press the YES button if this first swell system waves period is ok";
         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
         {
            JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            doorgaan = false;
            level_3_ok = false;
         }
      }
   }
   
   
   // first swell height
   //
   if ((doorgaan == true) && first_swell_height_conversion_ok == true)
   {
      if (float_first_swell_height > 12.2 && float_first_swell_height < 99.9)
      {
         String info = "First swell system waves height > 12.2 metres \n Press the NO button if it was a typing error, press the YES button if this first swell system height waves is ok";
         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
         {
            JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            doorgaan = false;
            level_3_ok = false;
         }
      }
   }
   
   
   // second swell period
   //
   if ((doorgaan == true) && second_swell_period_conversion_ok == true)
   {
      if (float_second_swell_period > 25.0 && float_second_swell_period < 99.9)
      {
         String info = "Second swell system waves period > 25 seconds \n Press the NO button if it was a typing error, press the YES button if this second swell system waves period is ok";
         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
         {
            JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            doorgaan = false;
            level_3_ok = false;
         }
      }
   }
   
   
   // second swell height
   //
   if ((doorgaan == true) && second_swell_height_conversion_ok == true)
   {
      if (float_second_swell_height > 12.2 && float_second_swell_height < 99.9)
      {
         String info = "Second swell system waves height > 12.2 metres \n Press the NO button if it was a typing error, press the YES button if this second swell system height waves is ok";
         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
         {
            JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            doorgaan = false;
            level_3_ok = false;
         }
      }
   }
   
   
   // wind speed <--> wand waves height
   //
   if ( (doorgaan == true) && (main.wind_units.trim().indexOf(main.M_S) != -1) && (mywind.int_true_wind_speed > 5 && mywind.int_true_wind_speed < 500.0) && 
                              (wind_waves_height_conversion_ok && float_wind_waves_height <= 0.01) )
   {
      // wind [m/s]
      String info = "Wind speed > 5 m/s (> 10 kts) and wind waves height 0 metres \n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
   if ( (doorgaan == true) && (main.wind_units.trim().indexOf(main.KNOTS) != -1) && (mywind.int_true_wind_speed > 10 && mywind.int_true_wind_speed < 500.0) && 
                              (wind_waves_height_conversion_ok && float_wind_waves_height <= 0.01) )
   {
      // wind [knots]
      String info = "Wind speed > 10 kts and wind waves height 0 metres \n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
   if ( (doorgaan == true) && (main.wind_units.trim().indexOf(main.M_S) != -1) && (mywind.int_true_wind_speed > 21 && mywind.int_true_wind_speed < 500.0) && 
                              (wind_waves_height_conversion_ok && float_wind_waves_height < 2.3) )
   {
      // wind [m/s]
      String info = "Wind speed > 21 m/s (> 41 kts) and wind waves height < 2.3 metres \n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
  if ( (doorgaan == true) && (main.wind_units.trim().indexOf(main.KNOTS) != -1) && (mywind.int_true_wind_speed > 41 && mywind.int_true_wind_speed < 500.0) && 
                             (wind_waves_height_conversion_ok && float_wind_waves_height < 2.3) )
   {
      // wind [knots]
      String info = "Wind speed > 41 kts and wind waves height < 2.3 metres \n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
  
   // air temp
   //
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 50.0 && float_air_temp < 99.9))
   {
      String info = "Air temperature > 50.0 \u00B0C \n Press the NO button if it was a typing error, press the YES button if this air temp is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
           
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp < -20.0 && float_air_temp > -99.9))
   {
      String info = "Air temperature < -20.0 \u00B0C \n Press the NO button if it was a typing error, press the YES button if this air temp is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }           
           
           
   // icing <-> air temperature 
   // 
   if ((doorgaan == true) && air_temp_conversion_ok && (float_air_temp > 4.0 && float_air_temp < 99.9) && 
                             (!myicing.Is_code.equals("") || !myicing.EsEs_code.equals("") || !myicing.Rs_code.equals("")))
   {
      String info = "Air temperature > 4.0 \u00B0C and Icing (Ice accretion)\n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }  
      
   
   // air pressure (MSL)
   //
   if ((doorgaan == true) && air_pressure_conversion_ok && !(float_air_pressure_msl_corrected >= 910.0 && float_air_pressure_msl_corrected <= 1050.0))
   {
      String info = "Air pressure (MSL) < 950.0 hPa or > 1050.0 hPa\n Press the NO button if it was a typing error, press the YES button if this air pressure (MSL) is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
     
   
   // amount pressure tendency
   //
   if ((doorgaan == true) && amount_pressure_tendency_conversion_ok && (float_amount_pressure_tendency > 30.0 && float_amount_pressure_tendency <= 99.9))
   {
      String info = "pressure tendency amount, last 3 hours > 30.0 hPa\n Press the NO button if it was a typing error, press the YES button if this amount of pressure tendency is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }

   
   // amount pressure tendency <-> characteristic pressure tendency (a)
   //
   if ((doorgaan == true) && amount_pressure_tendency_conversion_ok && (float_amount_pressure_tendency >= 0.0 && float_amount_pressure_tendency <= 99.9) && 
                                                                       (mybarograph.a_code.equals("") == true)  )
   {
      String info = "Amount of pressure tendency available and characteristic of tendency not available?\n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
    
   
   // SST
   //
   if ((doorgaan == true) && sea_water_temp_conversion_ok && (float_sea_water_temp > 35.0))
   {
      String info = "Seawater temperature > 35.0 \u00B0C?\n Press the NO button if it was a typing error, press the YES button if this sewater temp is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
   
   // thickness ice accretion (EsEs)
   //
   if ((doorgaan == true) && ice_thickness_conversion_ok && (float_ice_thickness > 20.0)) 
   {
      String info = "Ice thickness > 20.0 centimetres?\n Press the NO button if it was a typing error, press the YES button if this ice thickness is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }        
  
   
   // present weather <-> icing
   //  
   if ( (doorgaan == true) && ww_code_conversion_ok && (Arrays.asList(present_weather_48_49_array).indexOf(int_ww_code) != -1) && 
                                        (myicing.Is_code.equals("") && myicing.EsEs_code.equals("") && myicing.Rs_code.equals("")) )
   {
      String info = "Fog, depositing rime (present weather) and no ICING?\n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
   if ( (doorgaan == true) && ww_code_conversion_ok && (Arrays.asList(present_weather_56_57_array).indexOf(int_ww_code) != -1) && 
                                        (myicing.Is_code.equals("") && myicing.EsEs_code.equals("") && myicing.Rs_code.equals("")) )
   {
      String info = "Freezing drizzle (present weather) and no ICING?\n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
   if ( (doorgaan == true) && ww_code_conversion_ok && (Arrays.asList(present_weather_66_67_array).indexOf(int_ww_code) != -1) && 
                                        (myicing.Is_code.equals("") && myicing.EsEs_code.equals("") && myicing.Rs_code.equals("")) )
   {
      String info = "Freezing rain (present weather) and no ICING?\n Press the NO button if it was a typing error, press the YES button if this observation is ok";
      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + ", please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
         level_3_ok = false;
      }
   }
   
   
   
   
   return level_3_ok;
}
   
   
  
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public boolean Check_Land_Sea_Mask()
{
   // called from doInBackground() 4x 
   //    - Output_Obs_to_server_menu_actionPerformed() [main.java]
   //    - Output_obs_by_email_all_manual() [main.java]
   //    - Output_obs_to_file_actionPerformed() [main.java]
   //    - Output_obs_to_clipboard_actionPerformed() [main.java]
   
   // NB om de grootte van de jar te beperken wordt er alleen gecheckt op 1 graads vak niveau (zgf_sam) 
   //    en (i.t.t. TurboWin) niet op 1/10 graads vak niveau (zgf_lkw) 

   int Octant = INVALID;
   int la_vak_10;
   int la_vak_1;
   int lo_vak_10;
   int lo_vak_1;
   int vak_10;
   int sam_index;
   int land_zee_cijfer_sam;            // gevonden cijfer in ZGF_SAM (10 graads vakken file)
   int int_record_sam_vak_10;
   boolean zee_vak_ok = true;
   String record_sam;


   System.out.println("--- Checking entered position against a land-sea mask");
   
   try (InputStream is = getClass().getResourceAsStream(main.ICONS_DIRECTORY + "zgf_sam");
        BufferedReader in_1 = new BufferedReader(new InputStreamReader(is))                                                                                   )
   {
      // reading file 1 degree squares
      //InputStream is = getClass().getResourceAsStream(main.ICONS_DIRECTORY + "zgf_sam");
      //BufferedReader in_1 = new BufferedReader(new InputStreamReader(is));

      // deze aanzetten om de 1/10 graads niveau file mee te nemen                          
      // InputStream is = getClass().getResourceAsStream(main.ICONS_DIRECTORY + "zgf_lkw"); 
      // BufferedReader in_2 = new BufferedReader(new InputStreamReader(is));               



      //
      // Octant bepalen (WMO code table 0371)
      //
      //if ((obs_North_or_South == "N") && (obs_East_or_West == "W"))
      if ( (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true) && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true) )
      {
         //if (num_LoLoLoLo >= 0 && num_LoLoLoLo < 900)                      // n.b. 900 = 90.0 gr
         if (myposition.int_longitude_degrees >= 0 && myposition.int_longitude_degrees < 90)
            Octant = 0;
         else if (myposition.int_longitude_degrees >= 90 && myposition.int_longitude_degrees <= 180)             // n.b. 1800 = 180.0 gr
            Octant = 1;
      } 

      //else if ((obs_North_or_South == "N") && (obs_East_or_West == "E"))
      else if ( (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true) && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true) )
      {
         if (myposition.int_longitude_degrees >= 90 && myposition.int_longitude_degrees <= 180)
            Octant = 2;
         else if (myposition.int_longitude_degrees >= 0 && myposition.int_longitude_degrees < 90)
            Octant = 3;
      } 

      //else if ((obs_North_or_South == "S") && (obs_East_or_West == "W"))
      else if ( (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true) && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true) )
      {
         if (myposition.int_longitude_degrees >= 0 && myposition.int_longitude_degrees < 90)                      // n.b. 900 = 90.0 gr
            Octant = 5;
         else if (myposition.int_longitude_degrees >= 90 && myposition.int_longitude_degrees <= 180)             // n.b. 1800 = 180.0 gr
            Octant = 6;
      } 

      //else if ((obs_North_or_South == "S") && (obs_East_or_West == "E"))
      else if ( (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true) && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true) )
      {
         if (myposition.int_longitude_degrees >= 90 && myposition.int_longitude_degrees <= 180)
            Octant = 7;
         else if (myposition.int_longitude_degrees >= 0 && myposition.int_longitude_degrees < 90)
            Octant = 8;
      } 


      //
      // La-Vak bepalen
      //
      //la_vak_10    = num_LaLaLa / 100;                        // 123 -> 1
      //la_vak_1     = (num_LaLaLa / 10) % 10;                  // 123 -> 2
      la_vak_10 = myposition.int_latitude_degrees / 10;         // 12 -> 1
      la_vak_1  = myposition.int_latitude_degrees % 10;         // 12 -> 2
      //la_vak_1_10  = num_LaLaLa % 10;                         // 123 -> 3
      //la_voor_lkw  = num_LaLaLa / 10;                         // 123 -> 12

      //
      // Lo-Vak bepalen
      //
      //lo_vak_10    = (num_LoLoLoLo / 100) % 10;               // 1234 -> 2
      //lo_vak_1     = (num_LoLoLoLo / 10) % 10;                // 1234 -> 3
      lo_vak_10 = (myposition.int_longitude_degrees / 10) % 10; // 123 -> 2
      lo_vak_1  = (myposition.int_longitude_degrees) % 10;      // 123 -> 3
      //lo_vak_1_10  = num_LoLoLoLo % 10;                       // 1234 -> 4
      //lo_voor_lkw  = (num_LoLoLoLo / 10) % 100;               // 1234 -> 23

      //
      // 10 graads vak bepalen (WMO code tabel 0371)
      //
      vak_10 = (Octant * 100) + (la_vak_10 * 10) + lo_vak_10;

      while( ((record_sam = in_1.readLine()) != null) && (Octant != INVALID) )
      {
         //record_sam.read_line(in_1);
         if (record_sam.length() == 127)                   // zijn allemaal 127 char. lang
         {
            //if (atoi(record_sam.substring(0, 3)) == vak_10)
            try
            {
               int_record_sam_vak_10 = Integer.valueOf(record_sam.substring(0, 3));

               if (int_record_sam_vak_10 == vak_10)
               {
                  sam_index = 5 + (11 * la_vak_1) + lo_vak_1; // start op positie 0

                  //land_zee_cijfer_sam = atoi(record_sam.substring(sam_index, sam_index + 1));
                  land_zee_cijfer_sam = Integer.valueOf(record_sam.substring(sam_index, sam_index + 1));

                  if (land_zee_cijfer_sam == 0)               // sea position
                     zee_vak_ok = true;
                  else if (land_zee_cijfer_sam == 4)          // not yet but in mask/file
                     zee_vak_ok = true;
                  else if (land_zee_cijfer_sam == 2)          // wrong position
                     zee_vak_ok = false;
                  else if (land_zee_cijfer_sam == 3)          // land position
                     zee_vak_ok = false;
                  else if (land_zee_cijfer_sam == 1)          // coast position
                  {
                     // code if testing on 1/10 degree squares 
                     zee_vak_ok = true;
                  } // else if (land_zee_cijfer == 1)

                  break;                                           // ok, gevonden verlaten do-while sam

               } // if (atoi(record.SubString(0, 3)) == vak_10)
            } // try
            catch (NumberFormatException e) { }

         } // if (record.length() == 127)
         else // invalid line length
         {
            break;
         }
      } // while((record_sam = in_1.readLine()) != null)

   }
   catch (Exception ex)
   {
      //JOptionPane.showMessageDialog(null, "Reading error 'sea-land mask' file", APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      System.out.println("--- Function Check_Land_Sea_Mask(): " + ex);
   } // catch


   if (zee_vak_ok == false)
   {
      String info = "";
      info = "-land mask check-\n";
      info += "obs position:\n";
      info += myposition.latitude_degrees;
      info += "\u00B0 ";
      info += myposition.latitude_minutes;
      info += "' ";
      info += myposition.latitude_hemisphere;
      info += "  ";
      info += myposition.longitude_degrees;
      info += "\u00B0 ";
      info += myposition.longitude_minutes;
      info += "' ";
      info += myposition.longitude_hemisphere;
      info += "\n";

      if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + " please confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
      {
         JOptionPane.showMessageDialog(null, "Please correct the error (no final obs was coded)", main.APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
         zee_vak_ok = false;
      }
      else
      {
         zee_vak_ok = true;
      }
   } // if (zee_vak_ok == false)
   
   
   return zee_vak_ok;
}


   private static final int DELAY_UPDATE_PASSWORD_LOOP                 = 3600000; // 60 min                          // time in millisec to wait after timer is started to fire first event (10 min = 10 * 1000 * 60 * 10 = 600000)
   public static Timer password_timer;   
   public static boolean password_ok                                   = false;
   
}
