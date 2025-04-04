package turbowin;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;


/*
 * main.java
 *
 * Created on 26 maart 2008, 8:31
 */




/*
*
* ---------------------------------------------------------------------------------------------------------------------
* Ive installed previous versions of jdk1.6.0_17, and then after installing netbeans 7, I upgrade jdk to version jdk1.6.0_25.
* 
* Now, everytime I start Netbeans, it always show me this message :
*
* Cannot locate java installation in specified jdkhome:
* C:\Program Files\Java\jdk1.6.0_17
* Do you want to try to use default version?
*
* If I click Yes, the next time Netbeans started, the same message appear.
*
* So to remove this, I change netbeans.conf in C:\Program Files\NetBeans 7.0\etc.
* 
* Change netbeans_jdkhome like this :
* netbeans_jdkhome=C:\Program Files\Java\jdk1.6.0_25?
* ---------------------------------------------------------------------------------------------------------------------
* Wanneer .jar file direct wordt opgestart heb je geen invloed op de java heap memory
* (wel via java web start in de jnlp file of via cmd batch file)
* 
* dus wanneer direct via jar file aanpassen in java control panel:
* http://www.wikihow.com/Increase-Java-Memory-in-Windows-7
* 
* ---------------------------------------------------------------------------------------------------------------------
*
* na een copy van de ene PC naar een andere:
*         - indien reference problems, resolve door te verwijzen naar:
*         - ../backup/jnlp_jar/jnlp.jar
*         - ../backup/jssc_jar/jssc.jar
*
* IDE: hierna after a clean build:
*         - copy icons dir from turbowin_jws\backup\build_classes\      -> turbowin_jws\build\classes\turbowin\
*         - copy format_101 dir from turbowin_jws\backup\build_classes\ -> turbowin_jws\build\classes\turbowin\
*         - copy python dir from turbowin_jws\backup\build_classes\     -> turbowin_jws\build\classes\turbowin\
*         - copy OSM dir from turbowin_jws\backup\build_classes\        -> turbowin_jws\build\classes\turbowin\
*         - [not JPMS version?] recreate dir dist\logs ????
*         - [not JPMS version?] copy the file  \backup\software\jnlp\turbowin_jws_offline.jnlp to turbowin_jws\dist\  ????? alleen voor turboweb nodig ???
*         - [not JPMS version?] copy the file  \backup\software\cmd\turbowin_plus_offline.cmd to turbowin_jws\dist\   ????? alleen voor 32 bit versie nodig ???
* 
* ---------------------------------------------------------------------------------------------------------------------
* IDE: Bij properties -> Build -> Compiling: Compile on Save uitzetten!!
* IDE: Bij Properties -> Build -> Compiling: -Xlint:unchecked
* 
* IDE: Tools -> Options -> Editor -> Formatting: number of spaces per indent: 3
*                                                                  Tab size : 3
* 
* IDE: Tools -> Options -> Editor -> Hints:  probable bugs     : unused assignment                     : uitgezet
*                                            suggestions       : split declaration                     : uitgezet
*                                            JDK 1.5 and later : use switch over strings where possible: uitgezet
*                                                              : convert to try-with-resources         : uitgezet
* ---------------------------------------------------------------------------------------------------------------------
* om JNLP API (b.v.voor BasicService) ter beschikking te hebben 
* met rechtermuisknop klikken op "turbowin_jws" -> properties -> libraries -> compile -> add jar/folder 
* C:\Program Files\Java\jdk1.6.0\sample\jnlp\servlet
* in dist komt dan (automatisch) een sub-dir lib  met daarin jnlp.jar
* 
* Als compiler alsnog jnlp.jar niet kan vinden dan verwijzen naar jnlp.jar in de dist/lib folder
* ( "turbowin_jws" -> properties -> libraries -> compile -> add jar/folder ->
* C:\Program Files\NetBeans 6.5.1\projects\turbowin_jws\dist\lib)
* 
* ook deze jnlp.jar moet gesigned worden + aangemeld in de turbowin_jws.jnlp file
* 
* vanaf JDK7: jnlp.jar via download:  "Java SE Development Kit 7u21 Demos and Samples Downloads" (ergens unzippen en dan verwijzen naar ...\samples\jnlp\servlet\jnlp.jar)
* voor het gemak ook onder \...\backup\jnlp_jar gezet
* 
* ---------------------------------------------------------------------------------------------------------------------
* 
* SELF SIGNED [niet meer geldig/geaccepteerd bij nieuwere JREs]
* java web start applicaties moeten "getekend" zijn
* 1. key genereren bv: "c:\program files\java\jdk1.6.0_10\bin\keytool" -genkey -keystore myKeys -alias keyalias -keypass Martin
* 2. signen bv       : "C:\program files\Java\jdk1.6.0_10\bin\jarsigner" -verbose -keystore myKeys "C:\program files\netbeans-6.5\projects\turbowin_jws\dist\turbowin_jws.jar" keyalias
* (zie de specifieke .cmd files in "C:\Program Files\NetBeans 6.5\projects\turbowin_jws\backup\java_web_start" met hierin deze commando's)
*
* 
* CERTIFICATE
* - jnlp.jar, jssc.jar/jSerialCoomm-1.3.11.jar en turbowin_jws.jar moeten gesigned worden
* - NB vanaf versie 1.7 update 25 moet de manifest in de jars gewijzigd worden voor permisions en codebase (anders warning in console) [gebeurt met manifest_input.cmd]
* - NB vanaf versie 1.7 update 45 moet de manifest in de jars gewijzigd worden voor application-name (anders warning in console) [gebeurt met manifest_input.cmd]
* - NB manifest_input.cmd is nodig om permissies, application name and codebase te zetten. Omdat dit de eerste keer gebeurt (bv na clean build) geeft een volgende 
*      keer runnen manifest_input.cmd in feite een update (wat manifest_input.cmd met "umf" doet) warnings for duplicate entry -> DIT GEEFT NIETS
*
*
* ---------------------------------------------------------------------------------------------------------------------
* - muffin: // Windows 7        bv : C:\Users\hometrainer\AppData\LocalLow\Sun\Java\Deployment\cache\6.0\muffin
*           // XP               bv : C:\Documents and Settings\stam\Local Settings\Application Data\Sun\Java\Deployment\cache\6.0\muffin
* ---------------------------------------------------------------------------------------------------------------------
* - Bij overgang JDK 6 naar JDK 7:  - bij project properties (rechtermuisklik turbowin_jws -> properties -> sources) source/binary format evetueel veranderen van JDK 6 naar JDK 7 (bij JDK 6 wordt er gecheckt tegen JDK6 eigenschappen; bij JDK7 wordt er -ook- gecheckt tegen java 1.7 -nieuwe- eigenschappen)                                  
*                                   - bij project properties (rechtermuisklik turbowin_jws -> properties -> libraries) java platform: JDK 1.7 
*                                   - bij project properties (rechtermuisklik turbowin_jws -> properties -> libraries) compile time libraries: ...\jdk1_7_0_17_samples\sample\jnlp\servlet\jnlp.jar
*                                   - ook jarsigner_jnlp.cmd, keytool_turbowin_jws.cmd en jarsigner_turbowin_jws.cmd aanpassen
* 
* ----------------------------------------------------------------------------------------------------------------------
*
* wellicht interessant : https://blogs.oracle.com/jtc/entry/serial_port_communication_for_java
*
* NB VANAF VERSIE 2.4.0 JSSC ALS SERIAL COMMUNICATIE LIBRARY (BEHOEFT GEEN APARTE OS AFHANKELIJKE DRIVER INSTALLATIE)
*
*-----------------------------------------------------------------------------------------------------------------------
* directory structuur in offline mode
*              - main dir - turbowin_jws.jar
*              - main dir - turbowin_plus_offline.cmd (indien deze file aanwezig dan wordt turbowin_jws_offline.jnlp genegeerd)
*              - main dir - turbowin_jws_offline.jnlp (wanneer turbowin_plus_offline.cmd afwezig dan via de jnlp methode)
*              - sub dir lib (met files jnlp.jar, jssc.jar en ??AbsoluteLayout.jar??)
*              - sub dir help (met zelfde files als zoals op knmi turbowin internet server)
*              - sub dir logs (deze is echter niet noodzakelijk, indien niet aanwezig automatisch aagemaakt door TurboWin+)
*              - sub dir docs met ........
*              - sub dir amver (deze is echter niet noodzakelijk, indien niet aanwezig automatisch aagemaakt door TurboWin+)
*              - zie FORMAT_101.java voor extra dirs en files voor semi compressed berichten versturen
*              - NB voor volledig overzicht zie doc: "turbowin+_folder_structuur.docx"
*
* NB WHEN STARTED WITH turbowin_jws_offline.jnlp AND turbowin_plus_offline.cmd IS IN THE SAME DIR
*    THEN THE META DATA WILL ONLY BE STORED AND RETRIEVED FROM CONFIGURATION FILES AND NOT FROM/TO MUFFINS!
*    DELETE/RENAME turbowin_plus_offline.cmd TO USE MUFFINS
* 
* ----------------------------------------------------------------------------------------------------------------------
*
*
* global var: RS232_connection_mode:    0 = no instrument; serial connection or WiFi (default) 
*             (instrument type)         1 = barometer Vaisala PTB220 serial
*                                       2 = barometer Vaisala PTB330 serial
*                                       3 = EUCOS AWS (EUCAWS) serial
*                                       4 = barometer Mintaka Duo USB
*                                       5 = barometer Mintaka Star (USB)
*                                       6 = barometer Mintaka Star [WiFi] LAN (access point mode or station mode)
*                                       7 = barometer Mintaka Star (USB) + Mintaka StarX (WiFi)
*                                       8 = barometer Mintaka Star (WiFi] LAN (access point mode or station mode) + Mintaka StarX (WiFi)
*                                       9 = OMC-140 AWS (Observator) serial
*                                       10= OMC-140 AWS (Observator) [ethernet] LAN
*                                       11= AMOS2X AWS serial
*
* global var: RS232_connection_mode_II: 0 = no 2nd meteo instrument; serial connection or WiFi (default) 
*             (instrument type)         1 = Vaisala HMP 155 (with additional T probe) serial
*
*
*
* global var: RS232_GPS_connection_mode: 0 = no GPS serial connection (default) 
*                                        1 = GPS NMEA 0183 
*                                        2 = GPS NMEA 2000  [for future use]
*                                        3 = GPS in Mintaka Star (USB, station mode, access point)
*                                        4 = GPS in Mintaka StarX (USB, station mode, access point)
*
* global var: RS232_GPS_sentence :       0 = no sentence
*                                        1 = RMC
*                                        2 = GGA
*
* global var obs_format:  - FORMAT_FM13
*                         - FORMAT_101
*                         - FORMAT_AWS
*
* global var OSM_mode:    - OSM_ONLINE_MANUAL         // visual VOS (+ APR VOS)
*                         - OSM_OFFLINE_MANUAL        // visual VOS (+ APR VOS)
*                         - OSM_ONLINE_AWS_SENSOR
*                         - OSM_OFFLINE_AWS_SENSOR
*                         - OSM_ONLINE_AWS_VISUAL
*                         - OSM_OFFLINE_AWS_VISUAL
*
* global var GUI_mode     - GUI_FULL
*                         - GUI_LIGHT
*
*
* global var GUI_logo     - LOGO_EUMETNET
*                         - LOGO_NOAA
*                         - LOGO_SOT
*
* global var email_module - PYTHON_EMAIL
*                         - JAKARTA_EMAIL
*
* global var log_files_email_send_method - LOGS_DEFAULT_EMAIL
*                                        - LOGS_CUSTOM_EMAIL
*
* global var eucaws_uploads_method - UPLOADS_VIA_EUCAWS
*                                  - UPLOADS_VIA_TURBOWIN
*
* global var deactivate_APR_AWSR_ship_speed_minimal - true
*                                                   - false
*
* global var communication protocol - HTTPS_PROTOCOL
*                                   - HTTP_PROTOCOL

*
* ----------------------------------------------------------------------------------------------------------------------
* configuration data (bv call sign, imo nummer, hoogte deklading etc) wordt op 3 plaatsen weggeschreven: !!!
*   - 1x in muffin (java cache)
*   - 2x in configuration.txt - logs_dir (user defined in online mode sub dir in offline mode -dir waar immt.log staat-)
*                             - data_dir (system defined -dir waar turbowin_jws.jar staat- NB kan zijn dat user-dir write protected is bv bij 
*                                         installatie in de Program Files !! -> komt dan verder geen melding, dit is bij installatie script
*                                         aan te passen zie [DIRS] section, permissions etc. bij inno setup)
*
* 
* main.configuratie_regels[0 - 14]                              -> station data 
* main.configuratie_regels[15]                                  -> email settings (obs email recipient)
* main.configuratie_regels[16]                                  -> email settings (obs email subject)
* main.configuratie_regels[17]                                  -> logs_dir
* main.configuratie_regels[18]                                  -> email settings (logs email recipient)
* main.configuratie_regels[19]                                  -> station data (wind units)
* main.configuratie_regels[20]                                  -> INSTRUMENT instrument connection mode; serial communication settings
* main.configuratie_regels[21]                                  -> INSTRUMENT bps; serial communication settings
* main.configuratie_regels[22]                                  -> INSTRUMENT data bits; serial communication settings
* main.configuratie_regels[23]                                  -> INSTRUMENT parity serial; communication settings
* main.configuratie_regels[24]                                  -> INSTRUMENT stop bits; serial communication settings
* main.configuratie_regels[25]                                  -> INSTRUMENT prefered COM port (Windows and Linux); serial communication settings
* main.configuratie_regels[26]                                  -> barometer instrument correction
* main.configuratie_regels[27]                                  -> obs format (101 or FM13)
* main.configuratie_regels[28]                                  -> obs format 101 call sign encryption (yes or no)
* main.configuratie_regels[29]                                  -> obs format 101 email (body or attachement)
* main.configuratie_regels[30]                                  -> INSTRUMENT prefered COM port name (OS X); serial communication settings
* main.configuratie_regels[31]                                  -> WOW (true/false publish on WOW -WeatherObservationsWebsite-)
* main.configuratie_regels[32]                                  -> WOW_site_id                       
* main.configuratie_regels[33]                                  -> WOW_site_pin      
* main.configuratie_regels[34]                                  -> WOW_reporting_interval 
* main.configuratie_regels[35]                                  -> WOW/APR average draught            
* main.configuratie_regels[36]                                  -> default E-mail program on this computer is AMOS Mail [true/false]
* main.configuratie_regels[37]                                  -> GPS (NMEA 0183) connection mode; serial communication settings
* main.configuratie_regels[38]                                  -> GPS (NMEA 0183) bits per second; serial communication settings
* main.configuratie_regels[39]                                  -> GPS (NMEA 0183) prefered COM port (Windows and Linux); serial communication settings
* main.configuratie_regels[40]                                  -> GPS (NMEA 0183) prefered COM port name (OS X); serial communication settings
* main.configuratie_regels[41]                                  -> GPS (NMEA 0183) RS232_GPS_sentence to use: serial communication settings
* main.configuratie_regels[42]                                  -> APR (Automated Pressure Reports) [true/false]
* main.configuratie_regels[43]                                  -> APR reporting interval
* main.configuratie_regels[44]                                  -> upload URL (Output -> Obs to server)
* main.configuratie_regels[45]                                  -> AWSR (Automatic Weather Station Reports) [true/false]
* main.configuratie_regels[46]                                  -> AWSR reporting interval
* main.configuratie_regels[47]                                  -> wind speed units graphs/dasboard
* main.configuratie_regels[48]                                  -> ship type
* main.configuratie_regels[49]                                  -> height anemometer above WL
* main.configuratie_regels[50]                                  -> GUI_mode (light, full)
* main.configuratie_regels[51]                                  -> GUI_logo (Eumetnet, NOAA, SOT)
* main.configuratie_regels[52]                                  -> obs email cc
* main.configuratie_regels[53]                                  -> obs email SMTP HOST server name (eg smtp.xy.com)
* main.configuratie_regels[54]                                  -> obs email your Gmail address
* main.configuratie_regels[55]                                  -> obs email Gmail app password
* main.configuratie_regels[56]                                  -> obs email Gmail security (TLS / SSL)
* main.configuratie_regels[57]                                  -> obs email your Yahoo address
* main.configuratie_regels[58]                                  -> obs email Yahoo app password
* main.configuratie_regels[59]                                  -> obs email Yahoo security (TLS / SSL)
* main.configuratie_regels[60]                                  -> obs email SMTP HOST ship email address
* main.configuratie_regels[61]                                  -> obs email SMTP HOST password
* main.configuratie_regels[62]                                  -> obs email SMTP HOST port
* main.configuratie_regels[63]                                  -> RS232_connection_mode_II (2nd meteo instrument)      
* main.configuratie_regels[64]                                  -> bits_per_second_II (2nd meteo instrument)                      
* main.configuratie_regels[65]                                  -> data_bits_II (2nd meteo instrument)                               
* main.configuratie_regels[66]                                  -> parity_II (2nd meteo instrument)                                    
* main.configuratie_regels[67]                                  -> stop_bits_II (2nd meteo instrument)                               
* main.configuratie_regels[68]                                  -> prefered_COM_port_number_II (2nd meteo instrument)   
* main.configuratie_regels[69]                                  -> APTR/AWSR send method (server/SMTP host/Gmail/Yahoo Mail)
* main.configuratie_regels[70]                                  => station ID (SOT ID)
* main.configuratie_regels[71]                                  => dashboard background image
* main.configuratie_regels[72]                                  => your custom email address
* main.configuratie_regels[73]                                  => custom email server
* main.configuratie_regels[74]                                  => custom email password
* main.configuratie_regels[75]                                  => custom email security
* main.configuratie_regels[76]                                  => custom email port
* main.configuratie_regels[77]                                  => pop-up dashboard [true/false]
* main.configuratie_regels[78]                                  => pop-up dashboard interval (1, 3, 6 hours)
* main.configuratie_regels[79]                                  => dashboard ship deck color
* main.configuratie_regels[80]                                  => custom email module (jakarta or python)
* main.configuratie_regels[81]                                  => logs email method (pc-default or custom)
* main.configuratie_regels[82]                                  => eucaws upload method (eucaws modem or turbowin)
* main.configuratie_regels[83]                                  => port mode option; deactivate_APR_AWSR_ship_speed_minimal [true/false]
* main.configuratie_regels[84]                                  => LAN IP addrees; (IP address TurboWin+ is listening to if Mintaka ENet box is connected)
* main.configuratie_regels[85]                                  => dashboard ship (LNG)tank color
* main.configuratie_regels[86]                                  => communication protocol client <-> server (HTTPS-HTTP]
*
*
*+++++++++++++++++++ NB IF NEW ENTRY, IT IS ONLY NECESSARY TO APPEND THESE TWO FUNCTIONS: ++++++++++++++++++++
*                              - fill_configuratie_array() [main.java]
*                              - meta_data_from_configuration_regels_into_global_vars()[main.java]
* 
*
* lezen via: - lees_configuratie_regels()
* 
* schrijven via: - schrijf_configuratie_regels()
* 
* maar zie ook: - meta_data_from_configuration_regels_into_global_vars() [main.java]
*          - OK_button_actionPerformed [mystationdata.java]
*          - OK_button_actionPerformed [mylogfiles.java]
*          - OK_button_actionPerformed [myemailsettings.java]
*          - OK_button_actionPerformed [RS232_settings.java]
*          - OK_button_actionPerformed [mywind.java] 
*          - OK_button_actionPerformed [mybarometer.java] 
*          - OK_button_actionPerformed [myobsformat.java]
*          - OK_button_actionPerformed [WOW_settings.java]
*          - OK_button_actionPerformed [myserversettings.java]
*
* ---------------------------------------------------------------------------------------------------------------------
* 
* NB input/output in een GUI altijd via een SwingWorker (Core Java Volume 1 bld 795 e.v.; Volume 2 bld 37, 215) 
* 
* NB java.io.File.separator gebruiken i.p.v. "/" of "\"
*
* 
* ---------------------------------------------------------------------------------------------------------------------
* CHECK ONLY SINGLE INSTANCE OF TURBOWIN+ IS RUNNING:
* 
* only jnlp mode can use the single instance running check 
* 
* in offline mode: So by removing the file turbowin_plus_offline.cmd and invoking turbowin+ via turbowin_jws_offline.jnlp there will be a single instance running check
* online mode: is always started via the jnlp file -> always single instance running check
*  
* ---------------------------------------------------------------------------------------------------------------------
* UPDATE IN/TERVAL PARAMETERS
* update date-time field main screen every minute if GPS is connected (both, APR and MANUAL mode)
*
* ---------------------------------------------------------------------------------------------------------------------
** The following functions are using the serial communication library
*      - main_windowClosing() [main.java]
*      - Output_obs_to_AWS_actionPerformed() [main.java]
*      - ......
*
* ---------------------------------------------------------------------------------------------------------------------
* logging/dispalying examples:
*               - main.log_turbowin_system_message(message);  // NB inclusive System.out.printLn
*               - JOptionPane.showMessageDialog(null, "TurboWin+ is already running", main.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);  
*               - System.out.println("test");
* 
**/


/**
 *
 * @author  Martin
 */
public class main extends javax.swing.JFrame {
   
   /* inner class popupListener */
   class PopupListener_input extends MouseAdapter 
   {
      @Override
      public void mousePressed(MouseEvent e) 
      {
         ShowPopup(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) 
      {
         ShowPopup(e);
      }

      private void ShowPopup(MouseEvent e) 
      {
         if (e.isPopupTrigger()) 
         {
            popup_input.show(e.getComponent(), e.getX(), e.getY());
         }
      }
   } // class PopupListener_input extends MouseAdapter
   
   
   /* Creates new form main */
   public main() 
   {

      if (!theme_mode.equals(THEME_TRANSPARENT))  
      {
         // always at first start-up of this application
         try
         {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
               if ("Nimbus".equals(info.getName()))
               //if ("Metal".equals(info.getName()))   
               {
                  UIManager.setLookAndFeel(info.getClassName());
               
                  //UIManager.setLookAndFeel(new MetalLookAndFeel());
                  //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

                  // NB onderstaande hier nog niet nodig omdat de componenten nog niet aangemaakt zijn (initComponents();)
                  //SwingUtilities.updateComponentTreeUI(main.this);
               
                  // NB onderstaande kan niet omdat jTextField4 nog niet gecreeerd
                  //jTextField4.setBackground(new java.awt.Color(204, 255, 255));
               
                  //theme_mode = THEME_NIMBUS_DAY;

                  break;
               }
            }
         }
         catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
         {
            // If Metal is not available, you can set the GUI to another look and feel.
            //JOptionPane.showMessageDialog(null, "Metal color scheme not supported on this computer", main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }  // java default look and feel (voor Java 1.6 het zelde als:  UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");)
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) { }
         }
         
         String os = OSDetector.getOSString();

         if (os.equals("LINUX"))
         {
            if (theme_changed == false)
            {
               current_font = super.getFont();
            }
            else // theme_changed
            {
               super.setFont(current_font);
            }
         }
      } //  if (!theme_mode.equals(THEME_TRANSPARENT))  
      else // THEME_TRANSPARENT
      {
         // NB https://stackoverflow.com/questions/7434845/setting-the-default-font-of-swing-program

         String os = OSDetector.getOSString();
            
         if (os.equals("LINUX"))
         {
            // NB necesarry because otherwise under LINUX OS the labels/text etc. takes too much space (but only in transparent mode!), not appropriate under Windos OS 
            //    related to: getCrossPlatformLookAndFeelClassName() and setDefaultLookAndFeelDecorated(true) (see: Themes_5_actionPerformed()[main.java])
            //
            // NB call setUIFont() before calling initComponents()!
            //
            setUIFont(new javax.swing.plaf.FontUIResource("Ubuntu", Font.PLAIN, 12));
         }
         
      } // else (THEME_TRANSPARENT)
         
      initComponents();
      bepaal_frame_location();
      initImages();
      initComponents2();
      
      if (theme_mode.equals(THEME_TRANSPARENT))   
      {
         // NB before, by invoking initComponents2(), most of the main start-up settings were already done
         //    but the specific main screen (menu) items settings must be done again
         
         setOpacity(0.75f);
         
         // font check
         String os = OSDetector.getOSString();
            
         if (os.equals("LINUX"))
         {
            Font f = jLabel1.getFont();
	         String fontName = f.getFontName();
            if (fontName.equals("Ubuntu") == false)
            {
               JOptionPane.showMessageDialog(null, "Install Ubuntu fonts for a better GUI lay out in opacity Theme mode", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE); 
            }
         }
       
      } // else if (theme_mode.equals(THEME_TRANSPARENT))
      
      // Font name to console (all operating systems)
      Font f = jLabel1.getFont();
	   String fontName = f.getFontName();
      System.out.println("--- current font = " + fontName);
      
   } //  public main() 
    
   
/*    
   // Implement the SingleInstanceListener for your application
   class SISListener implements SingleInstanceListener 
   {
      @Override
      public void newActivation(String[] params) 
      {
            
         // your code to handle the new arguments here
         JOptionPane.showMessageDialog(null, "TurboWin+ is already running", main.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);   
      }
   }  // class SISListener implements SingleInstanceListener 
*/   
   
  



   /** This method is called from within the init() method to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jSeparator10 = new javax.swing.JSeparator();
      jSeparator12 = new javax.swing.JSeparator();
      jPanel1 = new javax.swing.JPanel();
      jToolBar1 = new javax.swing.JToolBar();
      jButton2 = new javax.swing.JButton();
      jButton3 = new javax.swing.JButton();
      jButton6 = new javax.swing.JButton();
      jButton7 = new javax.swing.JButton();
      jButton8 = new javax.swing.JButton();
      jButton4 = new javax.swing.JButton();
      jButton5 = new javax.swing.JButton();
      jButton11 = new javax.swing.JButton();
      jButton9 = new javax.swing.JButton();
      jButton10 = new javax.swing.JButton();
      jButton12 = new javax.swing.JButton();
      jButton13 = new javax.swing.JButton();
      jButton14 = new javax.swing.JButton();
      jButton15 = new javax.swing.JButton();
      jButton16 = new javax.swing.JButton();
      jButton17 = new javax.swing.JButton();
      jButton18 = new javax.swing.JButton();
      jButton19 = new javax.swing.JButton();
      jButton20 = new javax.swing.JButton();
      jSeparator11 = new javax.swing.JToolBar.Separator();
      jCheckBox1 = new javax.swing.JCheckBox();
      jSeparator13 = new javax.swing.JToolBar.Separator();
      jCheckBox2 = new javax.swing.JCheckBox();
      jPanel2 = new javax.swing.JPanel();
      jLabel33 = new javax.swing.JLabel();
      jTextField33 = new javax.swing.JTextField();
      jLabel34 = new javax.swing.JLabel();
      jTextField34 = new javax.swing.JTextField();
      jLabel36 = new javax.swing.JLabel();
      jTextField35 = new javax.swing.JTextField();
      jLabel13 = new javax.swing.JLabel();
      jTextField13 = new javax.swing.JTextField();
      jLabel14 = new javax.swing.JLabel();
      jTextField14 = new javax.swing.JTextField();
      jLabel15 = new javax.swing.JLabel();
      jTextField15 = new javax.swing.JTextField();
      jLabel19 = new javax.swing.JLabel();
      jTextField21 = new javax.swing.JTextField();
      jLabel21 = new javax.swing.JLabel();
      jTextField19 = new javax.swing.JTextField();
      jLabel30 = new javax.swing.JLabel();
      jTextField30 = new javax.swing.JTextField();
      jLabel31 = new javax.swing.JLabel();
      jTextField31 = new javax.swing.JTextField();
      jLabel32 = new javax.swing.JLabel();
      jTextField32 = new javax.swing.JTextField();
      jLabel20 = new javax.swing.JLabel();
      jTextField20 = new javax.swing.JTextField();
      jPanel3 = new javax.swing.JPanel();
      jLabel38 = new javax.swing.JLabel();
      jTextField40 = new javax.swing.JTextField();
      jLabel16 = new javax.swing.JLabel();
      jTextField16 = new javax.swing.JTextField();
      jLabel22 = new javax.swing.JLabel();
      jTextField22 = new javax.swing.JTextField();
      jLabel24 = new javax.swing.JLabel();
      jTextField24 = new javax.swing.JTextField();
      jLabel25 = new javax.swing.JLabel();
      jTextField25 = new javax.swing.JTextField();
      jLabel27 = new javax.swing.JLabel();
      jTextField27 = new javax.swing.JTextField();
      jLabel29 = new javax.swing.JLabel();
      jTextField29 = new javax.swing.JTextField();
      jLabel17 = new javax.swing.JLabel();
      jTextField17 = new javax.swing.JTextField();
      jLabel23 = new javax.swing.JLabel();
      jTextField23 = new javax.swing.JTextField();
      jLabel26 = new javax.swing.JLabel();
      jTextField26 = new javax.swing.JTextField();
      jLabel28 = new javax.swing.JLabel();
      jTextField28 = new javax.swing.JTextField();
      jLabel18 = new javax.swing.JLabel();
      jTextField18 = new javax.swing.JTextField();
      jPanel4 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      jTextField1 = new javax.swing.JTextField();
      jLabel2 = new javax.swing.JLabel();
      jTextField2 = new javax.swing.JTextField();
      jLabel3 = new javax.swing.JLabel();
      jTextField3 = new javax.swing.JTextField();
      jLabel5 = new javax.swing.JLabel();
      jTextField5 = new javax.swing.JTextField();
      jLabel7 = new javax.swing.JLabel();
      jTextField7 = new javax.swing.JTextField();
      jLabel9 = new javax.swing.JLabel();
      jTextField9 = new javax.swing.JTextField();
      jLabel10 = new javax.swing.JLabel();
      jTextField10 = new javax.swing.JTextField();
      jLabel11 = new javax.swing.JLabel();
      jTextField11 = new javax.swing.JTextField();
      jLabel12 = new javax.swing.JLabel();
      jTextField12 = new javax.swing.JTextField();
      jLabel35 = new javax.swing.JLabel();
      jTextField36 = new javax.swing.JTextField();
      jLabel37 = new javax.swing.JLabel();
      jTextField37 = new javax.swing.JTextField();
      jLabel40 = new javax.swing.JLabel();
      jTextField38 = new javax.swing.JTextField();
      jPanel5 = new javax.swing.JPanel();
      jSeparator1 = new javax.swing.JSeparator();
      jTextField4 = new javax.swing.JTextField();
      jLabel4 = new javax.swing.JLabel();
      jLabel39 = new javax.swing.JLabel();
      jLabel6 = new javax.swing.JLabel();
      jLabel41 = new javax.swing.JLabel();
      jMenuBar1 = new javax.swing.JMenuBar();
      jMenu1 = new javax.swing.JMenu();
      jMenuItem1 = new javax.swing.JMenuItem();
      jMenu2 = new javax.swing.JMenu();
      jMenuItem30 = new javax.swing.JMenuItem();
      jSeparator4 = new javax.swing.JSeparator();
      jMenuItem3 = new javax.swing.JMenuItem();
      jMenuItem4 = new javax.swing.JMenuItem();
      jMenuItem7 = new javax.swing.JMenuItem();
      jMenuItem8 = new javax.swing.JMenuItem();
      jMenuItem9 = new javax.swing.JMenuItem();
      jMenuItem5 = new javax.swing.JMenuItem();
      jMenuItem6 = new javax.swing.JMenuItem();
      jMenuItem12 = new javax.swing.JMenuItem();
      jMenuItem10 = new javax.swing.JMenuItem();
      jMenuItem11 = new javax.swing.JMenuItem();
      jMenuItem13 = new javax.swing.JMenuItem();
      jMenuItem14 = new javax.swing.JMenuItem();
      jMenuItem15 = new javax.swing.JMenuItem();
      jMenuItem16 = new javax.swing.JMenuItem();
      jMenuItem17 = new javax.swing.JMenuItem();
      jMenuItem18 = new javax.swing.JMenuItem();
      jMenuItem19 = new javax.swing.JMenuItem();
      jMenu3 = new javax.swing.JMenu();
      jMenuItem20 = new javax.swing.JMenuItem();
      jMenuItem23 = new javax.swing.JMenuItem();
      jMenuItem80 = new javax.swing.JMenuItem();
      jMenuItem24 = new javax.swing.JMenuItem();
      jMenuItem46 = new javax.swing.JMenuItem();
      jMenuItem48 = new javax.swing.JMenuItem();
      jMenu4 = new javax.swing.JMenu();
      jMenuItem21 = new javax.swing.JMenuItem();
      jMenuItem25 = new javax.swing.JMenuItem();
      jMenuItem26 = new javax.swing.JMenuItem();
      jMenuItem50 = new javax.swing.JMenuItem();
      jMenuItem42 = new javax.swing.JMenuItem();
      jMenuItem52 = new javax.swing.JMenuItem();
      jMenuItem54 = new javax.swing.JMenuItem();
      jSeparator2 = new javax.swing.JSeparator();
      jMenuItem27 = new javax.swing.JMenuItem();
      jMenuItem28 = new javax.swing.JMenuItem();
      jSeparator3 = new javax.swing.JSeparator();
      jMenuItem2 = new javax.swing.JMenuItem();
      jMenuItem29 = new javax.swing.JMenuItem();
      jSeparator7 = new javax.swing.JPopupMenu.Separator();
      jMenuItem59 = new javax.swing.JMenuItem();
      jMenuItem60 = new javax.swing.JMenuItem();
      jMenuItem61 = new javax.swing.JMenuItem();
      jMenu5 = new javax.swing.JMenu();
      jMenuItem31 = new javax.swing.JMenuItem();
      jMenuItem32 = new javax.swing.JMenuItem();
      jMenuItem34 = new javax.swing.JMenuItem();
      jMenuItem22 = new javax.swing.JMenuItem();
      jMenuItem76 = new javax.swing.JMenuItem();
      jMenu6 = new javax.swing.JMenu();
      jMenuItem33 = new javax.swing.JMenuItem();
      jMenuItem38 = new javax.swing.JMenuItem();
      jMenuItem39 = new javax.swing.JMenuItem();
      jMenuItem40 = new javax.swing.JMenuItem();
      jMenu8 = new javax.swing.JMenu();
      jMenuItem41 = new javax.swing.JMenuItem();
      jMenuItem43 = new javax.swing.JMenuItem();
      jMenuItem44 = new javax.swing.JMenuItem();
      jMenuItem45 = new javax.swing.JMenuItem();
      jMenuItem47 = new javax.swing.JMenuItem();
      jMenuItem51 = new javax.swing.JMenuItem();
      jMenu9 = new javax.swing.JMenu();
      jMenuItem72 = new javax.swing.JMenuItem();
      jMenuItem73 = new javax.swing.JMenuItem();
      jMenuItem58 = new javax.swing.JMenuItem();
      jMenuItem65 = new javax.swing.JMenuItem();
      jSeparator8 = new javax.swing.JPopupMenu.Separator();
      jMenuItem55 = new javax.swing.JMenuItem();
      jMenuItem77 = new javax.swing.JMenuItem();
      jSeparator14 = new javax.swing.JPopupMenu.Separator();
      jMenuItem56 = new javax.swing.JMenuItem();
      jMenuItem57 = new javax.swing.JMenuItem();
      jMenuItem63 = new javax.swing.JMenuItem();
      jMenuItem64 = new javax.swing.JMenuItem();
      jMenu10 = new javax.swing.JMenu();
      jMenuItem66 = new javax.swing.JMenuItem();
      jMenuItem62 = new javax.swing.JMenuItem();
      jMenuItem68 = new javax.swing.JMenuItem();
      jSeparator9 = new javax.swing.JPopupMenu.Separator();
      jMenuItem67 = new javax.swing.JMenuItem();
      jMenuItem69 = new javax.swing.JMenuItem();
      jMenuItem70 = new javax.swing.JMenuItem();
      jSeparator15 = new javax.swing.JPopupMenu.Separator();
      jMenuItem78 = new javax.swing.JMenuItem();
      jMenuItem140 = new javax.swing.JMenuItem();
      jMenuItem79 = new javax.swing.JMenuItem();
      jSeparator16 = new javax.swing.JPopupMenu.Separator();
      jMenu11 = new javax.swing.JMenu();
      jMenuItem81 = new javax.swing.JMenuItem();
      jMenuItem71 = new javax.swing.JMenuItem();
      jMenuItem82 = new javax.swing.JMenuItem();
      jMenuItem83 = new javax.swing.JMenuItem();
      jMenuItem84 = new javax.swing.JMenuItem();
      jMenuItem85 = new javax.swing.JMenuItem();
      jMenuItem86 = new javax.swing.JMenuItem();
      jMenuItem87 = new javax.swing.JMenuItem();
      jMenuItem88 = new javax.swing.JMenuItem();
      jMenuItem89 = new javax.swing.JMenuItem();
      jMenuItem90 = new javax.swing.JMenuItem();
      jMenuItem91 = new javax.swing.JMenuItem();
      jMenu12 = new javax.swing.JMenu();
      jMenuItem92 = new javax.swing.JMenuItem();
      jMenuItem93 = new javax.swing.JMenuItem();
      jMenuItem94 = new javax.swing.JMenuItem();
      jMenuItem95 = new javax.swing.JMenuItem();
      jMenuItem96 = new javax.swing.JMenuItem();
      jMenuItem97 = new javax.swing.JMenuItem();
      jMenuItem98 = new javax.swing.JMenuItem();
      jMenuItem99 = new javax.swing.JMenuItem();
      jMenuItem100 = new javax.swing.JMenuItem();
      jMenuItem101 = new javax.swing.JMenuItem();
      jMenuItem102 = new javax.swing.JMenuItem();
      jMenuItem103 = new javax.swing.JMenuItem();
      jMenu13 = new javax.swing.JMenu();
      jMenuItem104 = new javax.swing.JMenuItem();
      jMenuItem105 = new javax.swing.JMenuItem();
      jMenuItem106 = new javax.swing.JMenuItem();
      jMenuItem107 = new javax.swing.JMenuItem();
      jMenuItem108 = new javax.swing.JMenuItem();
      jMenuItem109 = new javax.swing.JMenuItem();
      jMenuItem110 = new javax.swing.JMenuItem();
      jMenuItem111 = new javax.swing.JMenuItem();
      jMenuItem112 = new javax.swing.JMenuItem();
      jMenuItem113 = new javax.swing.JMenuItem();
      jMenuItem114 = new javax.swing.JMenuItem();
      jMenuItem115 = new javax.swing.JMenuItem();
      jMenu14 = new javax.swing.JMenu();
      jMenuItem116 = new javax.swing.JMenuItem();
      jMenuItem117 = new javax.swing.JMenuItem();
      jMenuItem118 = new javax.swing.JMenuItem();
      jMenuItem119 = new javax.swing.JMenuItem();
      jMenuItem120 = new javax.swing.JMenuItem();
      jMenuItem121 = new javax.swing.JMenuItem();
      jMenuItem122 = new javax.swing.JMenuItem();
      jMenuItem123 = new javax.swing.JMenuItem();
      jMenuItem124 = new javax.swing.JMenuItem();
      jMenuItem125 = new javax.swing.JMenuItem();
      jMenuItem126 = new javax.swing.JMenuItem();
      jMenuItem127 = new javax.swing.JMenuItem();
      jMenu15 = new javax.swing.JMenu();
      jMenuItem128 = new javax.swing.JMenuItem();
      jMenuItem129 = new javax.swing.JMenuItem();
      jMenuItem130 = new javax.swing.JMenuItem();
      jMenuItem131 = new javax.swing.JMenuItem();
      jMenuItem132 = new javax.swing.JMenuItem();
      jMenuItem133 = new javax.swing.JMenuItem();
      jMenuItem134 = new javax.swing.JMenuItem();
      jMenuItem135 = new javax.swing.JMenuItem();
      jMenuItem136 = new javax.swing.JMenuItem();
      jMenuItem137 = new javax.swing.JMenuItem();
      jMenuItem138 = new javax.swing.JMenuItem();
      jMenuItem139 = new javax.swing.JMenuItem();
      jMenu7 = new javax.swing.JMenu();
      jMenuItem36 = new javax.swing.JMenuItem();
      jMenuItem49 = new javax.swing.JMenuItem();
      jMenuItem75 = new javax.swing.JMenuItem();
      jSeparator5 = new javax.swing.JPopupMenu.Separator();
      jMenuItem53 = new javax.swing.JMenuItem();
      jMenuItem35 = new javax.swing.JMenuItem();
      jSeparator17 = new javax.swing.JPopupMenu.Separator();
      jMenuItem74 = new javax.swing.JMenuItem();
      jSeparator6 = new javax.swing.JPopupMenu.Separator();
      jMenuItem37 = new javax.swing.JMenuItem();

      setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
      setFocusable(false);
      setMinimumSize(new java.awt.Dimension(1050, 740));
      setResizable(false);
      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(java.awt.event.WindowEvent evt) {
            main_windowClosing(evt);
         }
         public void windowDeiconified(java.awt.event.WindowEvent evt) {
            main_windowDeiconified(evt);
         }
         public void windowIconified(java.awt.event.WindowEvent evt) {
            main_windowIconfied(evt);
         }
      });

      jToolBar1.setRollover(true);

      jButton2.setToolTipText("Date and Time");
      jButton2.setFocusable(false);
      jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton2.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            date_time_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton2);

      jButton3.setToolTipText("Position, course and speed");
      jButton3.setFocusable(false);
      jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton3.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            position_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton3);

      jButton6.setToolTipText("Barometer reading");
      jButton6.setFocusable(false);
      jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton6.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            barometer_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton6);

      jButton7.setToolTipText("Barograph reading");
      jButton7.setFocusable(false);
      jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton7.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            barograph_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton7);

      jButton8.setToolTipText("Temperatures");
      jButton8.setFocusable(false);
      jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton8.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            temperatures_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton8);

      jButton4.setToolTipText("Wind");
      jButton4.setFocusable(false);
      jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton4.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton4);

      jButton5.setToolTipText("Waves");
      jButton5.setFocusable(false);
      jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton5.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            waves_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton5);

      jButton11.setToolTipText("Visibility");
      jButton11.setFocusable(false);
      jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton11.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            visibility_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton11);

      jButton9.setToolTipText("Present weather");
      jButton9.setFocusable(false);
      jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton9.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            present_weather_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton9);

      jButton10.setToolTipText("Past weather");
      jButton10.setFocusable(false);
      jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton10.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton10);

      jButton12.setToolTipText("Clouds low");
      jButton12.setFocusable(false);
      jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton12.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            cl_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton12);

      jButton13.setToolTipText("Clouds middle");
      jButton13.setFocusable(false);
      jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton13.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton13.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            cm_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton13);

      jButton14.setToolTipText("Clouds high");
      jButton14.setFocusable(false);
      jButton14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton14.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton14.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton14.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            ch_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton14);

      jButton15.setToolTipText("Clouds cover and height");
      jButton15.setFocusable(false);
      jButton15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton15.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton15.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton15.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            height_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton15);

      jButton16.setToolTipText("Icing");
      jButton16.setFocusable(false);
      jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton16.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton16.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            icing_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton16);

      jButton17.setToolTipText("Ice");
      jButton17.setFocusable(false);
      jButton17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton17.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton17.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton17.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            ice_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton17);

      jButton18.setToolTipText("Observer");
      jButton18.setFocusable(false);
      jButton18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton18.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton18.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton18.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            observer_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton18);

      jButton19.setToolTipText("Captains");
      jButton19.setFocusable(false);
      jButton19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton19.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton19.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton19.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            captain_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton19);

      jButton20.setToolTipText("next form automation");
      jButton20.setFocusable(false);
      jButton20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton20.setPreferredSize(new java.awt.Dimension(28, 28));
      jButton20.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton20.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            next_screen_toolbar_mouseClicked(evt);
         }
      });
      jToolBar1.add(jButton20);
      jToolBar1.add(jSeparator11);

      jCheckBox1.setText("APR");
      jCheckBox1.setToolTipText("Automated Pressure (&Temperature)  Reports");
      jCheckBox1.setFocusable(false);
      jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jCheckBox1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            APR_toolbar_itemStateChanged(evt);
         }
      });
      jToolBar1.add(jCheckBox1);
      jToolBar1.add(jSeparator13);

      jCheckBox2.setText("AWSR");
      jCheckBox2.setToolTipText("Automatic Weather Station Reports");
      jCheckBox2.setFocusable(false);
      jCheckBox2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jCheckBox2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jCheckBox2.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            AWSR_toolbar_itemStateChanged(evt);
         }
      });
      jToolBar1.add(jCheckBox2);

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );

      jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

      jLabel33.setForeground(new java.awt.Color(0, 0, 255));
      jLabel33.setText("Cl");
      jLabel33.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            cl_mainscreen_mouseClicked(evt);
         }
      });

      jTextField33.setEditable(false);
      jTextField33.setFocusable(false);
      jTextField33.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            cl_mainscreen_mouseClicked(evt);
         }
      });

      jLabel34.setForeground(new java.awt.Color(0, 0, 255));
      jLabel34.setText("Cm");
      jLabel34.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            cm_mainscreen_mouseClicked(evt);
         }
      });

      jTextField34.setEditable(false);
      jTextField34.setFocusable(false);
      jTextField34.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            cm_mainscreen_mouseClicked(evt);
         }
      });

      jLabel36.setForeground(new java.awt.Color(0, 0, 255));
      jLabel36.setText("Ch");
      jLabel36.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            ch_mainscreen_mouseClicked(evt);
         }
      });

      jTextField35.setEditable(false);
      jTextField35.setFocusable(false);
      jTextField35.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            ch_mainscreen_mouseClicked(evt);
         }
      });

      jLabel13.setForeground(new java.awt.Color(0, 0, 255));
      jLabel13.setText("Present weath.");
      jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            present_weather_mainscreen_mouseClicked(evt);
         }
      });

      jTextField13.setEditable(false);
      jTextField13.setFocusable(false);
      jTextField13.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            present_weather_mainscreen_mouseClicked(evt);
         }
      });

      jLabel14.setForeground(new java.awt.Color(0, 0, 255));
      jLabel14.setText("Past weath. 1st");
      jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_1_mainscreen_mouseClicked(evt);
         }
      });

      jTextField14.setEditable(false);
      jTextField14.setFocusable(false);
      jTextField14.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_1_mainscreen_mouseClicked(evt);
         }
      });

      jLabel15.setForeground(new java.awt.Color(0, 0, 255));
      jLabel15.setText("Past weath. 2nd");
      jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_2_mainscreen_mouseClicked(evt);
         }
      });

      jTextField15.setEditable(false);
      jTextField15.setFocusable(false);
      jTextField15.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_2_mainscreen_mouseClicked(evt);
         }
      });

      jLabel19.setForeground(new java.awt.Color(0, 0, 255));
      jLabel19.setText("Icing");
      jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            icing_mainscreen_mouseClicked(evt);
         }
      });

      jTextField21.setEditable(false);
      jTextField21.setFocusable(false);
      jTextField21.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            icing_mainscreen_mouseClicked(evt);
         }
      });

      jLabel21.setText("Ice");
      jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            ice_mainscreen_mouseClicked(evt);
         }
      });

      jTextField19.setEditable(false);
      jTextField19.setFocusable(false);
      jTextField19.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            ice_mainscreen_mouseClicked(evt);
         }
      });

      jLabel30.setForeground(new java.awt.Color(0, 0, 255));
      jLabel30.setText("Total cloud cov");
      jLabel30.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            total_cloud_cover_mainscreen_mouseClicked(evt);
         }
      });

      jTextField30.setEditable(false);
      jTextField30.setFocusable(false);
      jTextField30.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            total_cloud_cover_mainscreen_mouseClicked(evt);
         }
      });

      jLabel31.setForeground(java.awt.Color.blue);
      jLabel31.setText("Amount Cl (Cm)");
      jLabel31.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            amount_cl_mainscreen_mouseClicked(evt);
         }
      });

      jTextField31.setEditable(false);
      jTextField31.setFocusable(false);
      jTextField31.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            amount_cl_mainscreen_mouseClicked(evt);
         }
      });

      jLabel32.setForeground(new java.awt.Color(0, 0, 255));
      jLabel32.setText("ht lowest cloud");
      jLabel32.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            height_lowest_cloud_mainscreen_mouseClicked(evt);
         }
      });

      jTextField32.setEditable(false);
      jTextField32.setFocusable(false);
      jTextField32.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            height_lowest_cloud_mainscreen_mouseClicked(evt);
         }
      });

      jLabel20.setForeground(new java.awt.Color(0, 0, 255));
      jLabel20.setText("Observer");
      jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            observer_mainscreen_mouseClicked(evt);
         }
      });

      jTextField20.setEditable(false);
      jTextField20.setFocusable(false);
      jTextField20.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            observer_mainscreen_mouseClicked(evt);
         }
      });

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(jLabel34)
                  .addComponent(jLabel19)
                  .addComponent(jLabel36)
                  .addComponent(jLabel33)
                  .addComponent(jLabel21)
                  .addComponent(jLabel14)
                  .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jLabel15)
                  .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
               .addComponent(jLabel20))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
               .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
      );

      jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField13, jTextField14, jTextField15, jTextField19, jTextField20, jTextField21, jTextField30, jTextField31, jTextField32, jTextField33, jTextField34, jTextField35});

      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel13)
               .addComponent(jTextField13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel14)
               .addComponent(jTextField14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel15)
               .addComponent(jTextField15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel33))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel34)
               .addComponent(jTextField34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel36))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel30)
               .addComponent(jTextField30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel31)
               .addComponent(jTextField31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel32))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel19)
               .addComponent(jTextField21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel21))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel20))
            .addContainerGap())
      );

      jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

      jLabel38.setText("Seawater temp");
      jLabel38.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            seawater_temp_mainscreen_mouseClicked(evt);
         }
      });

      jTextField40.setEditable(false);
      jTextField40.setFocusable(false);
      jTextField40.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            seawater_temp_mainscreen_mouseClicked(evt);
         }
      });

      jLabel16.setText("Apparent wind");
      jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            true_wind_speed_mainscreen_mouseClicked(evt);
         }
      });

      jTextField16.setEditable(false);
      jTextField16.setFocusable(false);
      jTextField16.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            true_wind_speed_mainscreen_mouseClicked(evt);
         }
      });

      jLabel22.setForeground(java.awt.Color.blue);
      jLabel22.setText("(Wind) wave ht");
      jLabel22.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_wave_height_mainscreen_mouseClicked(evt);
         }
      });

      jTextField22.setEditable(false);
      jTextField22.setFocusable(false);
      jTextField22.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_wave_height_mainscreen_mouseClicked(evt);
         }
      });

      jLabel24.setForeground(new java.awt.Color(0, 0, 255));
      jLabel24.setText("1st swell dir");
      jLabel24.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_dir_mainscreen_mouseClicked(evt);
         }
      });

      jTextField24.setEditable(false);
      jTextField24.setFocusable(false);
      jTextField24.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_dir_mainscreen_mouseClicked(evt);
         }
      });

      jLabel25.setForeground(new java.awt.Color(0, 0, 255));
      jLabel25.setText("1st swell height");
      jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_height_mainscreen_mouseClicked(evt);
         }
      });

      jTextField25.setEditable(false);
      jTextField25.setFocusable(false);
      jTextField25.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_height_mainscreen_mouseClicked(evt);
         }
      });

      jLabel27.setForeground(new java.awt.Color(0, 0, 255));
      jLabel27.setText("2nd swell dir");
      jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_dir_mainscreen_mouseClicked(evt);
         }
      });

      jTextField27.setEditable(false);
      jTextField27.setFocusable(false);
      jTextField27.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_dir_mainscreen_mouseClicked(evt);
         }
      });

      jLabel29.setForeground(new java.awt.Color(0, 0, 255));
      jLabel29.setText("2nd swell period");
      jLabel29.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_period_mainscreen_mouseClicked(evt);
         }
      });

      jTextField29.setEditable(false);
      jTextField29.setFocusable(false);
      jTextField29.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_period_mainscreen_mouseClicked(evt);
         }
      });

      jLabel17.setText("True wind");
      jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            true_wind_dir_mainscreen_mouseClicked(evt);
         }
      });

      jTextField17.setEditable(false);
      jTextField17.setFocusable(false);
      jTextField17.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            true_wind_dir_mainscreen_mouseClicked(evt);
         }
      });

      jLabel23.setForeground(new java.awt.Color(0, 0, 255));
      jLabel23.setText("(Wind) wave per");
      jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_wave_period_mainscreen_mouseClicked(evt);
         }
      });

      jTextField23.setEditable(false);
      jTextField23.setFocusable(false);
      jTextField23.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_wave_period_mainscreen_mouseClicked(evt);
         }
      });

      jLabel26.setForeground(new java.awt.Color(0, 0, 255));
      jLabel26.setText("1st swell period");
      jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_period_mainscreen_mouseClicked(evt);
         }
      });

      jTextField26.setEditable(false);
      jTextField26.setFocusable(false);
      jTextField26.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_period_mainscreen_mouseClicked(evt);
         }
      });

      jLabel28.setForeground(new java.awt.Color(0, 0, 255));
      jLabel28.setText("2nd swell height");
      jLabel28.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_height_mainscreen_mouseClicked(evt);
         }
      });

      jTextField28.setEditable(false);
      jTextField28.setFocusable(false);
      jTextField28.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_height_mainscreen_mouseClicked(evt);
         }
      });

      jLabel18.setForeground(java.awt.Color.blue);
      jLabel18.setText("Visibility");
      jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            visibility_mainscreen_mouseClicked(evt);
         }
      });

      jTextField18.setEditable(false);
      jTextField18.setFocusable(false);
      jTextField18.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            visibility_mainscreen_mouseClicked(evt);
         }
      });

      javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jLabel23)
               .addComponent(jLabel25)
               .addComponent(jLabel16)
               .addComponent(jLabel24)
               .addComponent(jLabel27)
               .addComponent(jLabel26)
               .addComponent(jLabel29)
               .addComponent(jLabel28)
               .addComponent(jLabel22)
               .addComponent(jLabel18)
               .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                  .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
               .addComponent(jTextField40, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );

      jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField16, jTextField17, jTextField18, jTextField22, jTextField23, jTextField24, jTextField25, jTextField26, jTextField27, jTextField28, jTextField29, jTextField40});

      jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel38))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel17))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel16))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel23)
               .addComponent(jTextField23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel22))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel24)
               .addComponent(jTextField24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel26)
               .addComponent(jTextField26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel25)
               .addComponent(jTextField25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel27)
               .addComponent(jTextField27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel29)
               .addComponent(jTextField29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel28)
               .addComponent(jTextField28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel18)
               .addComponent(jTextField18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );

      jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

      jLabel1.setText("Ship name");
      jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            ship_name_mainscreen_mouseClicked(evt);
         }
      });

      jTextField1.setEditable(false);
      jTextField1.setFocusable(false);
      jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            ship_name_mainscreen_mouseClicked(evt);
         }
      });

      jLabel2.setText("Station ID");
      jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            station_ID_mainscreen_mouseClicked(evt);
         }
      });

      jTextField2.setEditable(false);
      jTextField2.setFocusable(false);
      jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            station_ID_mainscreen_mouseClicked(evt);
         }
      });

      jLabel3.setText("Date & Time obs");
      jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            date_time_mainscreen_mouseClicked(evt);
         }
      });

      jTextField3.setEditable(false);
      jTextField3.setFocusable(false);
      jTextField3.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            date_time_mainscreen_mouseClicked(evt);
         }
      });

      jLabel5.setText("Position");
      jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            position_mainscreen_mouseClicked(evt);
         }
      });

      jTextField5.setEditable(false);
      jTextField5.setFocusable(false);
      jTextField5.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            position_mainscreen_mouseClicked(evt);
         }
      });

      jLabel7.setText("Course & Speed");
      jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            course_speed_mainscreen_mouseClicked(evt);
         }
      });

      jTextField7.setEditable(false);
      jTextField7.setFocusable(false);
      jTextField7.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            course_speed_mainscreen_mouseClicked(evt);
         }
      });

      jLabel9.setText("Pressure (read+ic)");
      jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            pressure_read_mainscreen_mouseClicked(evt);
         }
      });

      jTextField9.setEditable(false);
      jTextField9.setFocusable(false);
      jTextField9.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            pressure_read_mainscreen_mouseClicked(evt);
         }
      });

      jLabel10.setText("Pressure (MSL)");
      jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            pressure_msl_mainscreen_mouseClicked(evt);
         }
      });

      jTextField10.setEditable(false);
      jTextField10.setFocusable(false);
      jTextField10.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            pressure_msl_mainscreen_mouseClicked(evt);
         }
      });

      jLabel11.setText("Pressure tendency");
      jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            amount_pressure_tendency_mainscreen_mouseClicked(evt);
         }
      });

      jTextField11.setEditable(false);
      jTextField11.setFocusable(false);
      jTextField11.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            amount_pressure_tendency_mainscreen_mouseClicked(evt);
         }
      });

      jLabel12.setText("Char. press. tend.");
      jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            char_pressure_tendency_mainscreen_mouseClicked(evt);
         }
      });

      jTextField12.setEditable(false);
      jTextField12.setFocusable(false);
      jTextField12.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            char_pressure_tendency_mainscreen_mouseClicked(evt);
         }
      });

      jLabel35.setText("Air temp");
      jLabel35.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            air_temp_mainscreen_mouseClicked(evt);
         }
      });

      jTextField36.setEditable(false);
      jTextField36.setFocusable(false);
      jTextField36.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            air_temp_mainscreen_mouseClicked(evt);
         }
      });

      jLabel37.setText("Wet-bulb temp");
      jLabel37.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            wet_bulb_temp_mainscreen_mouseClicked(evt);
         }
      });

      jTextField37.setEditable(false);
      jTextField37.setHorizontalAlignment(javax.swing.JTextField.LEFT);
      jTextField37.setFocusable(false);
      jTextField37.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            wet_bulb_temp_mainscreen_mouseClicked(evt);
         }
      });

      jLabel40.setText("Dew point");
      jLabel40.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            dew_point_mainscreen_mouseClicked(evt);
         }
      });

      jTextField38.setEditable(false);
      jTextField38.setFocusable(false);
      jTextField38.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            dew_point_mainscreen_mouseClicked(evt);
         }
      });

      javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
      jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jLabel1)
               .addComponent(jLabel7)
               .addComponent(jLabel5)
               .addComponent(jLabel9)
               .addComponent(jLabel12)
               .addComponent(jLabel10)
               .addComponent(jLabel3)
               .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                  .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
               .addComponent(jLabel35)
               .addComponent(jLabel37)
               .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
      );

      jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField1, jTextField10, jTextField11, jTextField12, jTextField2, jTextField3, jTextField36, jTextField37, jTextField38, jTextField5, jTextField7, jTextField9});

      jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel1)
               .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel2))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel3))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel5))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel7))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel9))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel10))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel11))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel12))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel35)
               .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel37)
               .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel40))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );

      jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      jPanel5.setOpaque(false);

      jTextField4.setEditable(false);
      jTextField4.setFocusable(false);

      jLabel4.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
      jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      jLabel4.setText("Turbo+");

      jLabel39.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
      jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      jLabel39.setText("--- adding data: input menu, popup menu, toolbar icons or click on the text labels or fields ---");

      jLabel6.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
      jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      jLabel6.setText("-");

      jLabel41.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
      jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      jLabel41.setText("-");

      javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
      jPanel5.setLayout(jPanel5Layout);
      jPanel5Layout.setHorizontalGroup(
         jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel5Layout.createSequentialGroup()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
               .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE)
               .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE)
               .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE)
               .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap(20, Short.MAX_VALUE))
      );

      jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel4, jSeparator1, jTextField4});

      jPanel5Layout.setVerticalGroup(
         jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(7, 7, 7)
            .addComponent(jLabel39)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel4)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel6)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel41))
      );

      jMenuBar1.setMaximumSize(new java.awt.Dimension(200, 21));
      jMenuBar1.setPreferredSize(new java.awt.Dimension(200, 21));

      jMenu1.setText("File");

      jMenuItem1.setText("Exit");
      jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            File_Exit_menu_actionPerformd(evt);
         }
      });
      jMenu1.add(jMenuItem1);

      jMenuBar1.add(jMenu1);

      jMenu2.setText("Input");

      jMenuItem30.setText("Next form automation");
      jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Next_form_automation_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem30);
      jMenu2.add(jSeparator4);

      jMenuItem3.setText("Date & Time...");
      jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_DateTime_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem3);

      jMenuItem4.setText("Position, Course & Speed...");
      jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Position_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem4);

      jMenuItem7.setText("Barometer reading...");
      jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Barometer_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem7);

      jMenuItem8.setText("Barograph reading...");
      jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Barograph_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem8);

      jMenuItem9.setText("Temperatures...");
      jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Temperatures_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem9);

      jMenuItem5.setText("Wind...");
      jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Wind_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem5);

      jMenuItem6.setText("Waves...");
      jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_waves_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem6);

      jMenuItem12.setText("Visibility...");
      jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Visibility_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem12);

      jMenuItem10.setText("Present weather...");
      jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Presentweather_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem10);

      jMenuItem11.setText("Past weather...");
      jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Pastweather_menu_actionperformed(evt);
         }
      });
      jMenu2.add(jMenuItem11);

      jMenuItem13.setText("Clouds low...");
      jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Cloudslow_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem13);

      jMenuItem14.setText("Clouds middle...");
      jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Cloudsmiddle_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem14);

      jMenuItem15.setText("Clouds high...");
      jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Cloudshigh_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem15);

      jMenuItem16.setText("Cloud cover & height...");
      jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Cloudcover_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem16);

      jMenuItem17.setText("Icing...");
      jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Icing_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem17);

      jMenuItem18.setText("Ice...");
      jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Ice_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem18);

      jMenuItem19.setText("Observer...");
      jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Observer_menu_actionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem19);

      jMenuBar1.add(jMenu2);

      jMenu3.setText("Output");
      jMenu3.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_by_email_Custom_actionPerformed(evt);
         }
      });

      jMenuItem20.setText("Obs to server (internet)");
      jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_Obs_to_server_menu_actionPerformed(evt);
         }
      });
      jMenu3.add(jMenuItem20);

      jMenuItem23.setText("Obs by Email (default)...");
      jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_by_email_default_actionPerformed(evt);
         }
      });
      jMenu3.add(jMenuItem23);

      jMenuItem80.setText("Obs by Email (Custom)");
      jMenuItem80.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_by_email_Custom_actionPerformed(evt);
         }
      });
      jMenu3.add(jMenuItem80);

      jMenuItem24.setText("Obs to file...");
      jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_to_file_actionPerformed(evt);
         }
      });
      jMenu3.add(jMenuItem24);

      jMenuItem46.setText("Obs to AWS");
      jMenuItem46.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_to_AWS_actionPerformed(evt);
         }
      });
      jMenu3.add(jMenuItem46);

      jMenuItem48.setText("Obs to clipboard");
      jMenuItem48.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_to_clipboard_actionPerformed(evt);
         }
      });
      jMenu3.add(jMenuItem48);

      jMenuBar1.add(jMenu3);

      jMenu4.setText("Maintenance");
      jMenu4.setPreferredSize(new java.awt.Dimension(80, 19));

      jMenuItem21.setText("Station data...");
      jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Stationdata_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem21);

      jMenuItem25.setText("Email settings...");
      jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Email_settings_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem25);

      jMenuItem26.setText("Log files settings...");
      jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Log_files_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem26);

      jMenuItem50.setText("Obs format setting...");
      jMenuItem50.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_obs_format_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem50);

      jMenuItem42.setText("Serial / USB / LAN device settings...");
      jMenuItem42.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Serial_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem42);

      jMenuItem52.setText("APR / APTR / AWSR settings...");
      jMenuItem52.setActionCommand("WOW/AP[&T]R/AWSR settings...");
      jMenuItem52.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_WOW_settings_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem52);

      jMenuItem54.setText("Server settings...");
      jMenuItem54.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_server_settings_actionperformed(evt);
         }
      });
      jMenu4.add(jMenuItem54);
      jMenu4.add(jSeparator2);

      jMenuItem27.setText("Observers...");
      jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Observer_menu_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem27);

      jMenuItem28.setText("Captains...");
      jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Captains_Menu_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem28);
      jMenu4.add(jSeparator3);

      jMenuItem2.setText("Move log files to (USB) disk...");
      jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Move_log_files_to_disk_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem2);

      jMenuItem29.setText("Move log files by Email");
      jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Move_log_files_by_email_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem29);
      jMenu4.add(jSeparator7);

      jMenuItem59.setText("Show all maintenance data");
      jMenuItem59.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Show_maintenance_data_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem59);

      jMenuItem60.setText("Export all maintenance data");
      jMenuItem60.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Export_maintenance_data_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem60);

      jMenuItem61.setText("Import all maintenance data");
      jMenuItem61.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Import_maintenance_data_actionPerformed(evt);
         }
      });
      jMenu4.add(jMenuItem61);

      jMenuBar1.add(jMenu4);

      jMenu5.setText("Themes");
      jMenu5.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_5_actionPerformed(evt);
         }
      });

      jMenuItem31.setText("Day");
      jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_1_actionPerformed(evt);
         }
      });
      jMenu5.add(jMenuItem31);

      jMenuItem32.setText("Night");
      jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_2_actionPerformed(evt);
         }
      });
      jMenu5.add(jMenuItem32);

      jMenuItem34.setText("Sunrise");
      jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_3_actionPerformed(evt);
         }
      });
      jMenu5.add(jMenuItem34);

      jMenuItem22.setText("Sunset");
      jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_4_actionPerformed(evt);
         }
      });
      jMenu5.add(jMenuItem22);

      jMenuItem76.setText("Transparent");
      jMenuItem76.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_5_actionPerformed(evt);
         }
      });
      jMenu5.add(jMenuItem76);

      jMenuBar1.add(jMenu5);

      jMenu6.setText("Amver");

      jMenuItem33.setText("Sailing Plan...");
      jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Amver_SailingPlan_actionPerformed(evt);
         }
      });
      jMenu6.add(jMenuItem33);

      jMenuItem38.setText("Deviation Report...");
      jMenuItem38.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Amver_DeviationReport_actionPerformed(evt);
         }
      });
      jMenu6.add(jMenuItem38);

      jMenuItem39.setText("Arrival Report...");
      jMenuItem39.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Amver_ArrivalReport_actionPerformed(evt);
         }
      });
      jMenu6.add(jMenuItem39);

      jMenuItem40.setText("Position Report...");
      jMenuItem40.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Amver_PositionReport_actionPerformed(evt);
         }
      });
      jMenu6.add(jMenuItem40);

      jMenuBar1.add(jMenu6);

      jMenu8.setText("Graphs");

      jMenuItem41.setText("Sensor data pressure");
      jMenuItem41.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graphs_Pressure_Sensor_Data_actionPerformed(evt);
         }
      });
      jMenu8.add(jMenuItem41);

      jMenuItem43.setText("Sensor data air temp");
      jMenuItem43.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graphs_Airtemp_Sensor_Data_actionPerformed(evt);
         }
      });
      jMenu8.add(jMenuItem43);

      jMenuItem44.setText("Sensor data SST");
      jMenuItem44.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graphs_SST_Sensor_data_actionPerformed(evt);
         }
      });
      jMenu8.add(jMenuItem44);

      jMenuItem45.setText("Sensor data wind speed");
      jMenuItem45.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graphs_Wind_Speed_Sensor_Data_actionPerformed(evt);
         }
      });
      jMenu8.add(jMenuItem45);

      jMenuItem47.setText("Sensor data wind dir");
      jMenuItem47.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graph_Wind_Dir_Sensor_Data_actionPerformed(evt);
         }
      });
      jMenu8.add(jMenuItem47);

      jMenuItem51.setText("Sensor data total");
      jMenuItem51.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graph_All_Sensor_Data_actionPerformed(evt);
         }
      });
      jMenu8.add(jMenuItem51);

      jMenuBar1.add(jMenu8);

      jMenu9.setText("Dashboard");

      jMenuItem72.setText("Observers stats");
      jMenuItem72.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_Obs_Stats_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem72);

      jMenuItem73.setText("Observations stats");
      jMenuItem73.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_Observations_Stats_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem73);

      jMenuItem58.setText("Latest Obs");
      jMenuItem58.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_Latest_Obs_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem58);

      jMenuItem65.setText("Latest AWS measurements");
      jMenuItem65.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_latest_AWS_measurements_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem65);
      jMenu9.add(jSeparator8);

      jMenuItem55.setText("Barometer");
      jMenuItem55.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_Barometer_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem55);

      jMenuItem77.setText("APR meteo radar");
      jMenuItem77.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_APR_radar_actionperformed(evt);
         }
      });
      jMenu9.add(jMenuItem77);
      jMenu9.add(jSeparator14);

      jMenuItem56.setText("AWS analog");
      jMenuItem56.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_AWS_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem56);

      jMenuItem57.setText("AWS digital");
      jMenuItem57.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_AWS_digital_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem57);

      jMenuItem63.setText("AWS hybrid");
      jMenuItem63.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_AWS_hybrid_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem63);

      jMenuItem64.setText("AWS wind radar");
      jMenuItem64.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_AWS_radar_actionPerformed(evt);
         }
      });
      jMenu9.add(jMenuItem64);

      jMenuBar1.add(jMenu9);

      jMenu10.setText("Maps");

      jMenuItem66.setText("Obs's Map (offline)");
      jMenuItem66.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_Obs_Manual_Map_Offline_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem66);

      jMenuItem62.setText("AWS sensor Map (offline)");
      jMenuItem62.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_AWS_Sensor_Map_Offline_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem62);

      jMenuItem68.setText("AWS visual Map (offline)");
      jMenuItem68.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_AWS_Visual_Map_Offline_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem68);
      jMenu10.add(jSeparator9);

      jMenuItem67.setText("Obs's Map (internet)");
      jMenuItem67.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_Obs_Manual_Map_Online_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem67);

      jMenuItem69.setText("AWS sensor Map (internet)");
      jMenuItem69.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_AWS_Sensor_Map_Online_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem69);

      jMenuItem70.setText("AWS Obs's visual Map (internet)");
      jMenuItem70.setActionCommand("AWS visual Map (internet)");
      jMenuItem70.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_AWS_Visual_Map_Online_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem70);
      jMenu10.add(jSeparator15);

      jMenuItem78.setText("Satellite image IR  (internet)");
      jMenuItem78.setActionCommand("Satellite image IR (internet)");
      jMenuItem78.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_satellite_image_IR_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem78);

      jMenuItem140.setText("Satellite image vis (internet)");
      jMenuItem140.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_satellite_image_vis_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem140);

      jMenuItem79.setText("Satellite image SST (internet)");
      jMenuItem79.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_satellite_image_SST_actionPerformed(evt);
         }
      });
      jMenu10.add(jMenuItem79);
      jMenu10.add(jSeparator16);

      jMenu11.setText("Pilot charts SA (internet)");

      jMenuItem81.setText("January");
      jMenuItem81.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_january_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem81);

      jMenuItem71.setText("February");
      jMenuItem71.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_february_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem71);

      jMenuItem82.setText("March");
      jMenuItem82.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_march_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem82);

      jMenuItem83.setText("April");
      jMenuItem83.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_april_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem83);

      jMenuItem84.setText("May");
      jMenuItem84.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_may_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem84);

      jMenuItem85.setText("June");
      jMenuItem85.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_june_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem85);

      jMenuItem86.setText("July");
      jMenuItem86.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_july_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem86);

      jMenuItem87.setText("August");
      jMenuItem87.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_august_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem87);

      jMenuItem88.setText("September");
      jMenuItem88.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_september_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem88);

      jMenuItem89.setText("October");
      jMenuItem89.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_october_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem89);

      jMenuItem90.setText("November");
      jMenuItem90.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_november_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem90);

      jMenuItem91.setText("December");
      jMenuItem91.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_december_actionPerformed(evt);
         }
      });
      jMenu11.add(jMenuItem91);

      jMenu10.add(jMenu11);

      jMenu12.setText("Pilot charts NA (internet)");

      jMenuItem92.setText("January");
      jMenuItem92.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_january_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem92);

      jMenuItem93.setText("February");
      jMenuItem93.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_february_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem93);

      jMenuItem94.setText("March");
      jMenuItem94.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_march_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem94);

      jMenuItem95.setText("April");
      jMenuItem95.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_april_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem95);

      jMenuItem96.setText("May");
      jMenuItem96.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_may_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem96);

      jMenuItem97.setText("June");
      jMenuItem97.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_june_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem97);

      jMenuItem98.setText("July");
      jMenuItem98.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_july_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem98);

      jMenuItem99.setText("August");
      jMenuItem99.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_august_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem99);

      jMenuItem100.setText("September");
      jMenuItem100.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_september_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem100);

      jMenuItem101.setText("October");
      jMenuItem101.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_october_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem101);

      jMenuItem102.setText("November");
      jMenuItem102.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_november_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem102);

      jMenuItem103.setText("December");
      jMenuItem103.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_december_actionPerformed(evt);
         }
      });
      jMenu12.add(jMenuItem103);

      jMenu10.add(jMenu12);

      jMenu13.setText("Pilot charts SP (internet)");

      jMenuItem104.setText("January");
      jMenuItem104.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_january_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem104);

      jMenuItem105.setText("February");
      jMenuItem105.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_february_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem105);

      jMenuItem106.setText("March");
      jMenuItem106.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_march_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem106);

      jMenuItem107.setText("April");
      jMenuItem107.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_april_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem107);

      jMenuItem108.setText("May");
      jMenuItem108.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_may_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem108);

      jMenuItem109.setText("June");
      jMenuItem109.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_june_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem109);

      jMenuItem110.setText("July");
      jMenuItem110.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_july_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem110);

      jMenuItem111.setText("August");
      jMenuItem111.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_august_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem111);

      jMenuItem112.setText("September");
      jMenuItem112.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_september_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem112);

      jMenuItem113.setText("October");
      jMenuItem113.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_october_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem113);

      jMenuItem114.setText("November");
      jMenuItem114.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_november_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem114);

      jMenuItem115.setText("December");
      jMenuItem115.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_december_actionPerformed(evt);
         }
      });
      jMenu13.add(jMenuItem115);

      jMenu10.add(jMenu13);

      jMenu14.setText("Pilot charts NP (internet)");

      jMenuItem116.setText("January");
      jMenuItem116.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_january_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem116);

      jMenuItem117.setText("February");
      jMenuItem117.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilots_charts_NP_february_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem117);

      jMenuItem118.setText("March");
      jMenuItem118.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_march_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem118);

      jMenuItem119.setText("April");
      jMenuItem119.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_april_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem119);

      jMenuItem120.setText("May");
      jMenuItem120.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_may_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem120);

      jMenuItem121.setText("June");
      jMenuItem121.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_june_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem121);

      jMenuItem122.setText("July");
      jMenuItem122.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_july_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem122);

      jMenuItem123.setText("August");
      jMenuItem123.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_august_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem123);

      jMenuItem124.setText("September");
      jMenuItem124.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_september_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem124);

      jMenuItem125.setText("October");
      jMenuItem125.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_october_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem125);

      jMenuItem126.setText("November");
      jMenuItem126.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_november_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem126);

      jMenuItem127.setText("December");
      jMenuItem127.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_december_actionPerformed(evt);
         }
      });
      jMenu14.add(jMenuItem127);

      jMenu10.add(jMenu14);

      jMenu15.setText("Pilot charts Indian (internet)");

      jMenuItem128.setText("January");
      jMenuItem128.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_january_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem128);

      jMenuItem129.setText("February");
      jMenuItem129.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_february_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem129);

      jMenuItem130.setText("March");
      jMenuItem130.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_march_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem130);

      jMenuItem131.setText("April");
      jMenuItem131.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_april_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem131);

      jMenuItem132.setText("May");
      jMenuItem132.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_may_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem132);

      jMenuItem133.setText("June");
      jMenuItem133.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_june_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem133);

      jMenuItem134.setText("July");
      jMenuItem134.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_july_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem134);

      jMenuItem135.setText("August");
      jMenuItem135.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_august_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem135);

      jMenuItem136.setText("September");
      jMenuItem136.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_september_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem136);

      jMenuItem137.setText("October");
      jMenuItem137.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_october_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem137);

      jMenuItem138.setText("November");
      jMenuItem138.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_november_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem138);

      jMenuItem139.setText("December");
      jMenuItem139.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_december_actionPerformed(evt);
         }
      });
      jMenu15.add(jMenuItem139);

      jMenu10.add(jMenu15);

      jMenuBar1.add(jMenu10);

      jMenu7.setText("Info");

      jMenuItem36.setText("Statistics (internet)");
      jMenuItem36.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_Statistics_menu_actionPerformed(evt);
         }
      });
      jMenu7.add(jMenuItem36);

      jMenuItem49.setText("Calculator...");
      jMenuItem49.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_Calculator_menu_actionPerformed(evt);
         }
      });
      jMenu7.add(jMenuItem49);

      jMenuItem75.setText("barometer comparison...");
      jMenuItem75.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_barometer_comparison_menu_actionPerformed(evt);
         }
      });
      jMenu7.add(jMenuItem75);
      jMenu7.add(jSeparator5);

      jMenuItem53.setText("System log");
      jMenuItem53.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_System_Log_menu_actionPerformed(evt);
         }
      });
      jMenu7.add(jMenuItem53);

      jMenuItem35.setText("send System logs");
      jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_send_System_log_menu_actionperformed(evt);
         }
      });
      jMenu7.add(jMenuItem35);
      jMenu7.add(jSeparator17);

      jMenuItem74.setText("device log");
      jMenuItem74.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_device_log_menu_actionPerformed(evt);
         }
      });
      jMenu7.add(jMenuItem74);
      jMenu7.add(jSeparator6);

      jMenuItem37.setText("About...");
      jMenuItem37.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_About_menu_actionPerformed(evt);
         }
      });
      jMenu7.add(jMenuItem37);

      jMenuBar1.add(jMenu7);

      setJMenuBar(jMenuBar1);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
         .addGroup(layout.createSequentialGroup()
            .addGap(33, 33, 33)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
               .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addGroup(layout.createSequentialGroup()
                  .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(72, 72, 72))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
               .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
      );
   }// </editor-fold>//GEN-END:initComponents


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void setUIFont(javax.swing.plaf.FontUIResource f)
   {
      java.util.Enumeration keys = UIManager.getDefaults().keys();
      while (keys.hasMoreElements()) 
      {
         Object key = keys.nextElement();
         Object value = UIManager.get(key);
         if (value instanceof javax.swing.plaf.FontUIResource)
         {
            UIManager.put(key, f);
         }
      }
   }  
   
   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public ImageIcon createImageIcon(String path_and_file)
   {
      URL url = null;

      try
      {
         url = getClass().getResource(path_and_file);
      }
      catch (Exception e) { /* ... */}

      ImageIcon icon_glyph = new javax.swing.ImageIcon(url);

      return icon_glyph;
   }



   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   // The doInBackground method, which creates the image icon for the photograph, is invoked by the background thread.
   // After the image icon is fully loaded, the done method is invoked on the event-dispatching thread.
   // This updates the GUI to display the photograph

   // SwingWorker is only designed to be executed once. Executing a SwingWorker more than once will not result in invoking the doInBackground method twice.
   // see: http://java.sun.com/javase/6/docs/api/javax/swing/SwingWorker.html
   private void loadImage(final String imagePath)
   {
      new SwingWorker<ImageIcon, Object>()
      {
         @Override
         public ImageIcon doInBackground()
         {
             return createImageIcon(imagePath);
         }

         @Override
         public void done()
         {
            try
            {
               // toolbar icons
               //
               //if (imagePath.equals(main.ICONS_DIRECTORY + "call_sign.png"))
               //{
               //   ImageIcon toolbar_img_call_sign = get();
               //   jButton1.setIcon(toolbar_img_call_sign);
               //}
               if (imagePath.equals(main.ICONS_DIRECTORY + "date_time.png"))
               {
                  ImageIcon toolbar_img_date_time = get();
                  jButton2.setIcon(toolbar_img_date_time);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "position.png"))
               {
                  ImageIcon toolbar_img_position = get();
                  jButton3.setIcon(toolbar_img_position);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "wind.png"))
               {
                  ImageIcon toolbar_img_wind = get();
                  jButton4.setIcon(toolbar_img_wind);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "waves.png"))
               {
                  ImageIcon toolbar_img_waves = get();
                  jButton5.setIcon(toolbar_img_waves);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "barometer.png"))
               {
                  ImageIcon toolbar_img_barometer = get();
                  jButton6.setIcon(toolbar_img_barometer);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "barograph.png"))
               {
                  ImageIcon toolbar_img_barograph = get();
                  jButton7.setIcon(toolbar_img_barograph);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "temperatures.png"))
               {
                  ImageIcon toolbar_img_temperatures = get();
                  jButton8.setIcon(toolbar_img_temperatures);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "present_weather.png"))
               {
                  ImageIcon toolbar_img_present_weather = get();
                  jButton9.setIcon(toolbar_img_present_weather);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "past_weather.png"))
               {
                  ImageIcon toolbar_img_past_weather = get();
                  jButton10.setIcon(toolbar_img_past_weather);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "visibility.png"))
               {
                  ImageIcon toolbar_img_visibility = get();
                  jButton11.setIcon(toolbar_img_visibility);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "cl.png"))
               {
                  ImageIcon toolbar_img_cl = get();
                  jButton12.setIcon(toolbar_img_cl);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "cm.png"))
               {
                  ImageIcon toolbar_img_cm = get();
                  jButton13.setIcon(toolbar_img_cm);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "ch.png"))
               {
                  ImageIcon toolbar_img_ch = get();
                  jButton14.setIcon(toolbar_img_ch);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "height.png"))
               {
                  ImageIcon toolbar_img_clouds_height = get();
                  jButton15.setIcon(toolbar_img_clouds_height);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "icing.png"))
               {
                  ImageIcon toolbar_img_icing = get();
                  jButton16.setIcon(toolbar_img_icing);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "ice.png"))
               {
                  ImageIcon toolbar_img_ice = get();
                  jButton17.setIcon(toolbar_img_ice);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "observers.png"))
               {
                  ImageIcon toolbar_img_observers = get();
                  jButton18.setIcon(toolbar_img_observers);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "captains.png"))
               {
                  ImageIcon toolbar_img_captains = get();
                  jButton19.setIcon(toolbar_img_captains);
               }
               else if (imagePath.equals(main.ICONS_DIRECTORY + "next_screen.png"))
               {
                  ImageIcon toolbar_img_next_screen = get();
                  jButton20.setIcon(toolbar_img_next_screen);
               }

            } // try
            catch (InterruptedException ignore) { }
            catch (java.util.concurrent.ExecutionException e)
            {
                String why = null;
                Throwable cause = e.getCause();
                if (cause != null)
                {
                   why = cause.getMessage();
                }
                else
                {
                   why = e.getMessage();
                }
                //System.err.println("Error retrieving file: " + why);
                JOptionPane.showMessageDialog(null, "Error retrieving toolbar icon file: " + why, main.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
            } // catch
         } //  public void done()
      }.execute();
   } // private void loadImage(final String imagePath, final int index)



   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void loadImage_straight(final String imagePath)
   {
      if (imagePath.equals(main.ICONS_DIRECTORY + "date_time.png"))
      {
         ImageIcon toolbar_img_date_time = createImageIcon(imagePath);
         jButton2.setIcon(toolbar_img_date_time);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "position.png"))
      {
         ImageIcon toolbar_img_position = createImageIcon(imagePath);
         jButton3.setIcon(toolbar_img_position);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "wind.png"))
      {
         ImageIcon toolbar_img_wind = createImageIcon(imagePath);
         jButton4.setIcon(toolbar_img_wind);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "waves.png"))
      {
         ImageIcon toolbar_img_waves = createImageIcon(imagePath);
         jButton5.setIcon(toolbar_img_waves);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "barometer.png"))
      {
         ImageIcon toolbar_img_barometer = createImageIcon(imagePath);
         jButton6.setIcon(toolbar_img_barometer);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "barograph.png"))
      {
         ImageIcon toolbar_img_barograph = createImageIcon(imagePath);
         jButton7.setIcon(toolbar_img_barograph);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "temperatures.png"))
      {
         ImageIcon toolbar_img_temperatures = createImageIcon(imagePath);
         jButton8.setIcon(toolbar_img_temperatures);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "present_weather.png"))
      {
         ImageIcon toolbar_img_present_weather = createImageIcon(imagePath);
         jButton9.setIcon(toolbar_img_present_weather);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "past_weather.png"))
      {
         ImageIcon toolbar_img_past_weather = createImageIcon(imagePath);
         jButton10.setIcon(toolbar_img_past_weather);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "visibility.png"))
      {
         ImageIcon toolbar_img_visibility = createImageIcon(imagePath);
         jButton11.setIcon(toolbar_img_visibility);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "cl.png"))
      {
         ImageIcon toolbar_img_cl = createImageIcon(imagePath);
         jButton12.setIcon(toolbar_img_cl);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "cm.png"))
      {
         ImageIcon toolbar_img_cm = createImageIcon(imagePath);
         jButton13.setIcon(toolbar_img_cm);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "ch.png"))
      {
         ImageIcon toolbar_img_ch = createImageIcon(imagePath);
         jButton14.setIcon(toolbar_img_ch);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "height.png"))
      {
         ImageIcon toolbar_img_clouds_height = createImageIcon(imagePath);
         jButton15.setIcon(toolbar_img_clouds_height);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "icing.png"))
      {
         ImageIcon toolbar_img_icing = createImageIcon(imagePath);
         jButton16.setIcon(toolbar_img_icing);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "ice.png"))
      {
         ImageIcon toolbar_img_ice = createImageIcon(imagePath);
         jButton17.setIcon(toolbar_img_ice);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "observers.png"))
      {
         ImageIcon toolbar_img_observers = createImageIcon(imagePath);
         jButton18.setIcon(toolbar_img_observers);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "captains.png"))
      {
         ImageIcon toolbar_img_captains = createImageIcon(imagePath);
         jButton19.setIcon(toolbar_img_captains);
      }
      else if (imagePath.equals(main.ICONS_DIRECTORY + "next_screen.png"))
      {
         ImageIcon toolbar_img_next_screen = createImageIcon(imagePath);
         jButton20.setIcon(toolbar_img_next_screen);
      }
   } // private void loadImage(final String imagePath, final int index)
   
   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void initImages()
   {
      String os = OSDetector.getOSString();
      
      if (os.equals("LINUX"))
      {
         // NB
         //    on Fedora linux approx. every 5-10 times when activating the form the glyphs are not loaded (no errors or exceptions)
         //    even with using BufferdImages instead of the imageIcons, the same result. 
         //    After testing all the pictures were found everytime (so that was not the issue)
         //    It seems the background loading is not always ok on Fedora

         loadImage_straight(main.ICONS_DIRECTORY + "date_time.png");
         loadImage_straight(main.ICONS_DIRECTORY + "position.png");
         loadImage_straight(main.ICONS_DIRECTORY + "wind.png");
         loadImage_straight(main.ICONS_DIRECTORY + "waves.png");
         loadImage_straight(main.ICONS_DIRECTORY + "barometer.png");
         loadImage_straight(main.ICONS_DIRECTORY + "barograph.png");
         loadImage_straight(main.ICONS_DIRECTORY + "temperatures.png");
         loadImage_straight(main.ICONS_DIRECTORY + "present_weather.png");
         loadImage_straight(main.ICONS_DIRECTORY + "past_weather.png");
         loadImage_straight(main.ICONS_DIRECTORY + "visibility.png");
         loadImage_straight(main.ICONS_DIRECTORY + "cl.png");
         loadImage_straight(main.ICONS_DIRECTORY + "cm.png");
         loadImage_straight(main.ICONS_DIRECTORY + "ch.png");
         loadImage_straight(main.ICONS_DIRECTORY + "height.png");
         loadImage_straight(main.ICONS_DIRECTORY + "icing.png");
         loadImage_straight(main.ICONS_DIRECTORY + "ice.png");
         loadImage_straight(main.ICONS_DIRECTORY + "observers.png");
         loadImage_straight(main.ICONS_DIRECTORY + "captains.png");
         loadImage_straight(main.ICONS_DIRECTORY + "next_screen.png");
      }
      else
      {
         loadImage(main.ICONS_DIRECTORY + "date_time.png");
         loadImage(main.ICONS_DIRECTORY + "position.png");
         loadImage(main.ICONS_DIRECTORY + "wind.png");
         loadImage(main.ICONS_DIRECTORY + "waves.png");
         loadImage(main.ICONS_DIRECTORY + "barometer.png");
         loadImage(main.ICONS_DIRECTORY + "barograph.png");
         loadImage(main.ICONS_DIRECTORY + "temperatures.png");
         loadImage(main.ICONS_DIRECTORY + "present_weather.png");
         loadImage(main.ICONS_DIRECTORY + "past_weather.png");
         loadImage(main.ICONS_DIRECTORY + "visibility.png");
         loadImage(main.ICONS_DIRECTORY + "cl.png");
         loadImage(main.ICONS_DIRECTORY + "cm.png");
         loadImage(main.ICONS_DIRECTORY + "ch.png");
         loadImage(main.ICONS_DIRECTORY + "height.png");
         loadImage(main.ICONS_DIRECTORY + "icing.png");
         loadImage(main.ICONS_DIRECTORY + "ice.png");
         loadImage(main.ICONS_DIRECTORY + "observers.png");
         loadImage(main.ICONS_DIRECTORY + "captains.png");
         loadImage(main.ICONS_DIRECTORY + "next_screen.png");
      }
   }


   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static String convert_month(int month_number)   
{
   String month_name = "";
   
   
   if (month_number == 0)
   {
      month_name = "January";
   }
   else if (month_number == 1)
   {
      month_name = "February";
   }
   if (month_number == 2)
   {
      month_name = "March";
   }
   if (month_number == 3)
   {
      month_name = "April";
   }
   if (month_number == 4)
   {
      month_name = "May";
   }
   if (month_number == 5)
   {
      month_name = "June";
   }
   if (month_number == 6)
   {
      month_name = "July";
   }
   if (month_number == 7)
   {
      month_name = "August";
   }
   if (month_number == 8)
   {
      month_name = "September";
   }
   if (month_number == 9)
   {
      month_name = "October";
   }
   if (month_number == 10)
   {
      month_name = "November";
   }
   if (month_number == 11)
   {
      month_name = "December";
   }
   
   
   return month_name;
}
   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void check_and_set_datetime_v2()
{
   // NB Never mind the computer is set to UTC or not. SimpleTimeZone with argument UTC convert system date time
   //    always to UTC !!
   //
   String hulp_system_minute_LT = "";
   String hulp_system_minute_UTC = "";
   int len;

   
   ///////// local computer time (LT) (eg http://www.egmdss.com/gmdss-courses/mod/resource/view.php?id=2231)
   //
   cal_systeem_datum_tijd_LT = new GregorianCalendar();                                // Constructs a default GregorianCalendar using the current time in the default time zone with the default locale.
   cal_systeem_datum_tijd_LT.getTime();                                                // effectueren

   int system_year_LT          = cal_systeem_datum_tijd_LT.get(Calendar.YEAR);
   int system_month_LT         = cal_systeem_datum_tijd_LT.get(Calendar.MONTH);        // The first month of the year is JANUARY which is 0
   int system_day_of_month_LT  = cal_systeem_datum_tijd_LT.get(Calendar.DAY_OF_MONTH); // The first day of the month has value 1
   int system_hour_of_day_LT   = cal_systeem_datum_tijd_LT.get(Calendar.HOUR_OF_DAY);  // HOUR_OF_DAY: 24 hour clock; HOUR : 12 hour clock
   int system_minute_LT        = cal_systeem_datum_tijd_LT.get(Calendar.MINUTE);
   
   if (system_minute_LT <= 9)
   {
      hulp_system_minute_LT = "0" + Integer.toString(system_minute_LT);
   }
   else
   {
      hulp_system_minute_LT = Integer.toString(system_minute_LT);
   }

   String system_LT_string = "system: " + convert_month(system_month_LT) + " " + system_day_of_month_LT + ", " + system_year_LT + " " + system_hour_of_day_LT + "." + hulp_system_minute_LT + " LT";
      
   
   ///////// UTC computer time
   //
   cal_systeem_datum_tijd_UTC = new GregorianCalendar(new SimpleTimeZone(0, "UTC")); // gives system date and time in UTC of this moment
   cal_systeem_datum_tijd_UTC.getTime();                                 // effectueren

   int system_year_UTC          = cal_systeem_datum_tijd_UTC.get(Calendar.YEAR);
   int system_month_UTC         = cal_systeem_datum_tijd_UTC.get(Calendar.MONTH);        // The first month of the year is JANUARY which is 0
   int system_day_of_month_UTC  = cal_systeem_datum_tijd_UTC.get(Calendar.DAY_OF_MONTH); // The first day of the month has value 1
   int system_hour_of_day_UTC   = cal_systeem_datum_tijd_UTC.get(Calendar.HOUR_OF_DAY);  // HOUR_OF_DAY: 24 hour clock; HOUR : 12 hour clock
   int system_minute_UTC        = cal_systeem_datum_tijd_UTC.get(Calendar.MINUTE);

   if (system_minute_UTC <= 9)
   {
      hulp_system_minute_UTC = "0" + Integer.toString(system_minute_UTC);
   }
   else
   {
      hulp_system_minute_UTC = Integer.toString(system_minute_UTC);
   }   
   
   String system_UTC_string = "system: " + convert_month(system_month_UTC) + " " + system_day_of_month_UTC + ", " + system_year_UTC + " " + system_hour_of_day_UTC + "." + hulp_system_minute_UTC + " UTC";
      
   
   ///////// obs time
   //
   if (system_minute_UTC > 30)
   {
      cal_systeem_datum_tijd_UTC.add(Calendar.HOUR_OF_DAY, 1);   // add 1 hour
      cal_systeem_datum_tijd_UTC.getTime();
   }

   int obs_year          = cal_systeem_datum_tijd_UTC.get(Calendar.YEAR);
   int obs_month         = cal_systeem_datum_tijd_UTC.get(Calendar.MONTH);        // The first month of the year is JANUARY which is 0
   int obs_day_of_month  = cal_systeem_datum_tijd_UTC.get(Calendar.DAY_OF_MONTH); // The first day of the month has value 1
   int obs_hour_of_day   = cal_systeem_datum_tijd_UTC.get(Calendar.HOUR_OF_DAY);  // HOUR_OF_DAY: 24 hour clock; HOUR : 12 hour clock

   String obs_UTC_string = "obs: " + convert_month(obs_month) + " " + obs_day_of_month + ", " + obs_year + " " + obs_hour_of_day + ".00 UTC";   

   // set this date-time required confirmation pop-up JOptionPane always on top // from version 4.4
   JFrame jf = new JFrame();
   jf.setAlwaysOnTop(true);
   if (JOptionPane.showConfirmDialog(jf, obs_UTC_string + "\n\n" + "(" + system_UTC_string + ")" + "\n" + "(" + system_LT_string + ")", "Date and Time", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
   //if (JOptionPane.showConfirmDialog(null, obs_UTC_string + "\n\n" + "(" + system_UTC_string + ")" + "\n" + "(" + system_LT_string + ")", "Date and Time", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
   {
      // So date time of the confirmation message confirmed by the user

      mydatetime.year  = Integer.toString(obs_year);                     // for progress main screen and IMMT
      mydatetime.month = convert_month(obs_month);                       // for progress main screen
      mydatetime.day   = Integer.toString(obs_day_of_month);             // for progress main screen
      mydatetime.hour  = Integer.toString(obs_hour_of_day);              // for progress main screen

      /* update date and time on TurboWin+ main screen */
      date_time_fields_update();


      // determine code figures for month (not for operational obs only for IMMT storage)
      //
      mydatetime.MM_code = Integer.toString(obs_month +1);                   // +1 because obs_month started with 0 (January)

      len = 2;                                                               // MM_code always 2 characters width e.g. 03
      if (mydatetime.MM_code.length() < len)                                 // pad on left with zeros
      {
         mydatetime.MM_code = "0000000000".substring(0, len - mydatetime.MM_code.length()) + mydatetime.MM_code;
      }
         
      // determine code figures for day of the month (for obs and IMMT)
      //
      mydatetime.YY_code = Integer.toString(obs_day_of_month);               // obs_day_of_month e.g. 1,2,11,23,29

      len = 2;                                                               // YY_code always 2 characters width e.g. 03
      if (mydatetime.YY_code.length() < len)                                 // pad on left with zeros
      {
         mydatetime.YY_code = "0000000000".substring(0, len - mydatetime.YY_code.length()) + mydatetime.YY_code;
      }

      // determine code figures for hour of day (for obs and IMMT)
      //
      mydatetime.GG_code = Integer.toString(obs_hour_of_day);                // obs_hour_of_day e.g. 1,2,11,23

      len = 2;                                                               // GG_code always 2 characters width e.g. 03
      if (mydatetime.GG_code.length() < len)                                 // pad on left with zeros
      {
         mydatetime.GG_code = "0000000000".substring(0, len - mydatetime.GG_code.length()) + mydatetime.GG_code;
      }
         
      use_system_date_time_for_updating = true;
      
      // set start-up sequence finished flag (used by pop-up screen reminder visual obs)
      //turbowin_start_up_sequence_finished = true;
         
   } // if (JOptionPane.showConfirmDialog(null, datum_tijd_string, "Date and time of observation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
   else
   {
      use_system_date_time_for_updating = false;   // see Function: main_RS232_RS422.set_datetime_while_collecting_sensor_data()
      
      // warning that APR will not work!!
      if (APR == true)
      {
         String info = "If this computer is not running on the correct date/time, APR (Automated Pressure Reports) will stop working!";
         JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
      }
   } // else


   /* clear memory as soon as possible */
   //cal_systeem_datum_tijd         = null;
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void specific_connection_initComponents()
{
   
   // called from: - read_muffin() [main.java]
   //              - lees_configuratie_regels() [main.java]
   
   
   //
   //////// RS232/RS422/WiFi //////
   //
   if (theme_changed)   
   {
      // NB if transparent scheme all the necessary start-up items were all ready done
      //    so not necessary to invoke e.g. RS232_RS422.RS232_initComponents(); etc becuase they are still running
        
      // text field labels
      if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 11)     // EUCOS AWS serial or OMC AWS serial or AMOS2X serial
      {
         // in AWS mode no dew point but relative humidity 
         jLabel40.setText("relative humidity");
                  
         // in AWS mode update info 
         jLabel41.setText("--- sensor data updated every minute ---");
      }
      else if (RS232_connection_mode == 10)                                 // OMC-140 ethernet LAN
      {
         // in AWS mode no dew point but relative humidity 
         jLabel40.setText("relative humidity");
                  
         // in AWS mode update info 
         jLabel41.setText("--- sensor data updated every minute ---");
      }       
         
      // in transparant theme mode no popup menu avaialable so over write the appropriate text on the main menu 
      jLabel39.setText("--- adding data: input menu, toolbar icons or click on the text labels or fields ---");
      
      // Text on label6 was set only once at start up in function RS232_GPS_NMEA_0183_initComponents() [main_RS232_RS422]
      if (main_RS232_RS422.GPS_defaultPort != null)
      {   
         //System.out.println("+++ GPS_defaultPort = " + GPS_defaultPort);
         // info text on (bottom) main screen 
         main.jLabel6.setText(main.APPLICATION_NAME + " receiving GPS data via serial communication.....");
      }
         
   } // if (theme_changed)    
   else // no Theme change
   {
      // NB at first time start-up of this application it will never be the trnsparent scheme so all items below will be invoked/set the first time
      if (RS232_connection_mode == 1 || RS232_connection_mode == 2 || RS232_connection_mode == 4 || RS232_connection_mode == 5 || RS232_connection_mode == 7) // PTB220 or PTB330 or Mintaka Duo or Mintaka Star USB or Mintaka Star + StarX USB
      {   
         RS232_RS422.RS232_initComponents();                                 // for Vaisala/Mintaka barometers (not Mintaka Star Wifi)
      }
      else if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 11)     // EUCOS AWS serial or OMC AWS serial or AMOS2X serial
      {
         /* in AWS mode input items like call sign, position, date/time etc. are not asked */ 
         //disable_aws_input_menu_items();
                  
         /* in AWS mode Output -> obs to file etc. disable */
         //disable_aws_output_menu_items();
                  
         /* in AWS mode no dew point but relative humidity */
         jLabel40.setText("relative humidity");
                  
         /* in AWS mode update info */
         //jLabel8.setText("--- sensor data updated every minute ---");
         jLabel41.setText("--- sensor data updated every minute ---");
                  
         RS232_RS422.RS422_initComponents(); 
      }
      else if (RS232_connection_mode == 6 || RS232_connection_mode == 8)    // Mintaka Star WiFi or Mintaka Star + StarX WiFi
      {
         RS232_RS422.WiFi_initComponents();
      }
      else if (RS232_connection_mode == 10)                                 // OMC-140 ethernet LAN
      {
          /* in AWS mode no dew point but relative humidity */
         jLabel40.setText("relative humidity");
                  
         /* in AWS mode update info */
         //jLabel8.setText("--- sensor data updated every minute ---");
         jLabel41.setText("--- sensor data updated every minute ---");
      
         RS232_RS422.Ethernet_initComponents();
      }
        
   
      //
      //////// 2nd RS232 //////
      //
   
      /////////////////////// TEST BEGIN /////////////////
      //RS232_connection_mode_II = 1;
      ////////////////////// TEST END ////////////////
   
      if (RS232_connection_mode_II == 1)
      {
         RS232_RS422.RS232_initComponents_II();        
      }
   
   
      //
      ////////// GPS connected ? 
      //
      if (RS232_GPS_connection_mode == 1)                                     // 0 = no GPS; 1 = GPS (NMEA 1083)
      {
         RS232_RS422.RS232_GPS_NMEA_0183_initComponents();
      }
   
   
      //
      ////////// check date and time (and ask observer for confirmation) 
      //
      if (RS232_connection_mode != 3 && RS232_connection_mode != 9 && RS232_connection_mode != 10 && RS232_connection_mode != 11) // not AWS connected mode 
      {
         check_and_set_datetime_v2();    // NB var use_system_date_time_for_updating = true can be set here, used in case of an connected barometer [static]
      }
      else // AWS connected
      {
         // in AWS connected mode the date time is updated when reading the incoming AWS measured data
         use_system_date_time_for_updating = false;   // in AWS connected mode the date time is always! updated when reading the incoming AWS measured data [static]
      
         // but if an instrument is connected but this is not an  AWS, so a barometer is connected then the date time will be automatically inserted on main screen
      }
   } // else (no Theme change)
   
   
   // APR and AWSR checkboxes on the toolbar
   //
   
   // initialisation
   jCheckBox1.setEnabled(true);                                           // APR checkbox   
   jCheckBox2.setEnabled(true);                                           // AWSR checkbox
   
   if (RS232_connection_mode == 3)                                        // EUCAWS (has its own send method/device, so APR and AWSR not appropriate))
   {
      jCheckBox1.setEnabled(false);                                       // APR checkbox   
      jCheckBox2.setEnabled(false);                                       // AWSR checkbox
   }
   
   if ((RS232_connection_mode == 9) || (RS232_connection_mode == 10) || RS232_connection_mode == 11)     // OMC-140 serial and OMC-140 LAN or AMOS2X serial
   {
      jCheckBox1.setEnabled(false);                                       // APR checkbox
   }
   
   if ((RS232_connection_mode != 3) && (RS232_connection_mode != 9) && (RS232_connection_mode != 10) && (RS232_connection_mode != 11))     // EUCAWS and OMC-140 and AMOS2X
   {
      jCheckBox2.setEnabled(false);                                       // AWSR checkbox
   }
   
   if (RS232_connection_mode == 0)                                        // no devices connected
   {
      jCheckBox1.setEnabled(false);                                       // APR checkbox   
      jCheckBox2.setEnabled(false);                                       // AWSR checkbox
   
   }
   
   // set start-up sequence finished flag
   turbowin_start_up_sequence_finished = true;
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void disable_and_enable_output_menu_items()
{
   // initialisation (because some could be disabled before and must now enabled (see Function OK_button_actionPerformed() [main_RS232_RS422.java])
   jMenuItem20.setEnabled(true);    // Obs to server
   jMenuItem23.setEnabled(true);    // Email default
   //jMenuItem74.setEnabled(true);    // Email SMTP host
   //jMenuItem72.setEnabled(true);    // Email Gmail
   //jMenuItem73.setEnabled(true);    // Email Yahoo
   jMenuItem80.setEnabled(true);    // Email Custom
   jMenuItem24.setEnabled(true);    // Obs to file
   jMenuItem46.setEnabled(true);    // Obs to AWS
   jMenuItem48.setEnabled(true);    // Obs to clipbord
   
   
   // AWS (EUCAWS or OMC-140 etc.) connection 
   //       NB so OMC-140, AMOS2X can never send manually output observations! (only in AWSR mode)
   if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)  // AWS connected mode
   {
      jMenuItem20.setEnabled(false);                                 // Obs to server
      jMenuItem23.setEnabled(false);                                 // Obs by E-mail (default)
      jMenuItem24.setEnabled(false);                                 // Obs to file
      jMenuItem48.setEnabled(false);                                 // Obs to clipboard
      
      //jMenuItem72.setEnabled(false);                                 // Obs by E-mail (Gmail)
      //jMenuItem73.setEnabled(false);                                 // Obs by E-mail (Yahoo)
      //jMenuItem74.setEnabled(false);                                 // Obs by E-mail (local host)
      jMenuItem80.setEnabled(false);                                 // Obs by E-mail (Custom)
   }
   
   
   // if not EUCAWS connected 
   if (RS232_connection_mode != 3)                                   // not EUCAWS connected mode 
   {
      // disable the "Obs to AWS" menu item 
      jMenuItem46.setEnabled(false);                                 // Obs to AWS (so also disabled for OMC-140!!)
   } 
   
   // EUCAWS connected but uploads via TurboWin+
   if ( (RS232_connection_mode == 3) && (eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN)) )
   {
      // disable the "Obs to AWS" menu item 
      jMenuItem46.setEnabled(false);                                    
   }   
   
   // if not EUCAWS and not OMC-140 and not AMOS2X connected 
   // Email options
   if ((RS232_connection_mode != 3) && (RS232_connection_mode != 9) && (RS232_connection_mode != 10) && (RS232_connection_mode != 11)) // not EUCAWS and not OMC-140 and not AMOS2X connected mode 
   {
      // [ALL]           // NB the basics are these two items (recipient and subject) that must be present always 
      if ( (obs_email_recipient.equals("")) || (obs_email_subject.equals("")) )
      {
         jMenuItem23.setEnabled(false);                              // Obs by E-mail (default)
         //jMenuItem72.setEnabled(false);                              // Obs by E-mail (Gmail)
         //jMenuItem73.setEnabled(false);                              // Obs by E-mail (Yahoo)
         //jMenuItem74.setEnabled(false);                              // Obs by E-mail (local host)
         jMenuItem80.setEnabled(false);                              // Obs by E-mail (Custom)                            
      }
      else // recipient and subject ok
      {
         // [DEFAULT]
         if (obs_format.equals(main.FORMAT_101))
         {
            if (obs_101_email.equals(""))                               // obs_101_email: body or attachment
            {
               jMenuItem23.setEnabled(false);                           // Obs by E-mail (default)
            }
            else
            {
               jMenuItem23.setEnabled(true);                            // Obs by E-mail (default)
            } // else
         } // if (obs_format.equals(main.FORMAT_101))
         else // FM13
         {
            jMenuItem23.setEnabled(true);                               // Obs by E-mail (default)
         }
         
         // [SMTP HOST]
         //if ( (local_email_server.equals("")) || (your_ship_address.equals("")) )
         //{
         //   jMenuItem74.setEnabled(false);                           // Obs by E-mail (local host)        
         //}
         //else
         //{
         //   jMenuItem74.setEnabled(true);                            // Obs by E-mail (local host)
         //} // else
         
         // [GMAIl]
         //if ( (your_gmail_address.equals("")) || (gmail_app_password.equals("")) || (gmail_security.equals("")) )
         //{
         //   jMenuItem72.setEnabled(false);                           // Obs by E-mail (Gmail)
         //}
         //else
         //{
         //   jMenuItem72.setEnabled(true);                            // Obs by E-mail (Gmail)
         //} // else
         
         // [YAHOO]
         //if ( (your_yahoo_address.equals("")) || (yahoo_app_password.equals("")) || (yahoo_security.equals("")) )
         //{
         //   jMenuItem73.setEnabled(false);                           // Obs by E-mail (Yahoo)
         //}
         //else
         //{
         //   jMenuItem73.setEnabled(true);                            // Obs by E-mail (Yahoo)
         //} // else
         
         // [CUSTOM]
         //if ( (your_custom_address.equals("")) || (custom_password.equals("")) || (custom_security.equals("")) || (custom_email_server.equals("")) || (custom_port.equals("")))
         if ( (your_custom_address.equals("")) || (custom_security.equals("")) || (custom_email_server.equals("")) || (custom_port.equals("")))   
         {
            jMenuItem80.setEnabled(false);                           // Obs by E-mail (Custom)
         }
         else
         {
            jMenuItem80.setEnabled(true);                            // Obs by E-mail (Custom) 
         }
         
      } // else (recipient and subject ok)
      
   } // if (RS232_connection_mode != 3)
   
/*
   // Obs to server
   //
   // check if upload URL was entered 
   if ( (upload_URL.equals("") || upload_URL == null) )
   {
      // no upload URL entered
      jMenuItem20.setEnabled(false);             
   }
   else // upload URL ok
   {
      if ((obs_format.equals(FORMAT_FM13)) && (offline_mode == true))
      {
         // NB FM13 only "obs to server" in online mode, in case of format 101 "obs to server" is an output option in both modes (online/web and offline)
         // gray (disable) the "output -> obs to server (internet)" menu selection option
         jMenuItem20.setEnabled(false);
      }
   } // upload URL ok
*/   

   // Obs to server
   //
   if ((obs_format.equals(FORMAT_FM13)) && (offline_mode == false))
   {
      // FM13 + Java Web Start (TurboWeb): by default always a valid FM13 URL avaialble (the URL were TurboWeb it was downloaded from!!)
      jMenuItem20.setEnabled(true);
   }
   else if ( (upload_URL.equals("") || upload_URL == null) )
   {
      // if not FM13 + Java Web Start (TurboWeb) then upload URL must be available
      jMenuItem20.setEnabled(false);
   }
   
   
   // APR and AWSR 
   //
   if (APR || AWSR)                     // NB APR and APTR could never be checked together
   {
      // in APR and AWSR mode disable all output menu items (in APR and AWSR mode the output method is set in 'Maintenance -> APR/APTR/AWSR settings')
      //    NB at the beginning of this function all output menu options were enabled(true)
      //    NB in OMC-140 mode these menu items were already disabled (so is not strict necessary do do it here again)
      jMenuItem20.setEnabled(false);    // Obs to server
      jMenuItem23.setEnabled(false);    // Email default
      //jMenuItem74.setEnabled(false);    // Email SMTP host
      //jMenuItem72.setEnabled(false);    // Email Gmail
      //jMenuItem73.setEnabled(false);    // Email Yahoo
      jMenuItem24.setEnabled(false);    // Obs to file
      jMenuItem46.setEnabled(false);    // Obs to AWS
      jMenuItem48.setEnabled(false);    // Obs to clipboard
      jMenuItem80.setEnabled(false);    // Email Custom
   } // if (APR || APTR)
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private static void disable_dashboard_and_maps_menu_items()
{
   // initialisation (because some could be disabled before and must now enabled (see Function OK_button_actionPerformed() [main_RS232_RS422.java])
   
   
   jMenuItem55.setEnabled(true);
   jMenuItem56.setEnabled(true);
   jMenuItem57.setEnabled(true); 
   jMenuItem58.setEnabled(true);
   jMenuItem62.setEnabled(true);
   jMenuItem63.setEnabled(true);
   jMenuItem64.setEnabled(true);
   jMenuItem65.setEnabled(true);
   jMenuItem66.setEnabled(true);
   jMenuItem67.setEnabled(true);
   jMenuItem68.setEnabled(true);
   jMenuItem69.setEnabled(true);
   jMenuItem70.setEnabled(true);
   jMenuItem77.setEnabled(true);
   
   
   
   // if no AWS connected 
   if (RS232_connection_mode != 3 && RS232_connection_mode != 9 && RS232_connection_mode != 10 && RS232_connection_mode != 11)  // not AWS connected mode
   {
      // disable Dashboard AWS (analog)
      jMenuItem56.setEnabled(false);                           // Dashboard - AWS [analog)
                  
      // disable Dasboard AWS (digital)
      jMenuItem57.setEnabled(false);                           // Dashboard - AWS [digital)
      
      // disable Dashboard AWS (hybrid)
      jMenuItem63.setEnabled(false);                           // Dashboard - AWS [hybrid)
      
      // disable Dashboard AWS (wind radar)
      jMenuItem64.setEnabled(false);                           // Dashboard - AWS [wind radar)
      
      // disable Latest AWS measurements
      jMenuItem65.setEnabled(false);                           // Dashboard - Latest AWS measurements
      
      // disable Maps -> AWS sensor Map (offline)              // Maps -> AWS sensor Map (offline)  
      jMenuItem62.setEnabled(false);                           // NB sensor_data files based 
      
      //disable Maps -> AWS visual Map (offline)               // Maps -> AWS visual Map (offline)
      jMenuItem68.setEnabled(false);                           // NB IMMT based 
      
      // disable Maps -> AWS sensor Map (online)               // Maps -> AWS sensor Map (online)  
      jMenuItem69.setEnabled(false);                           // NB sensor_data files based 
      
      // disable Maps -> AWS visual Map (online)               // Maps -> AWS visual Map (online) 
      jMenuItem70.setEnabled(false);                           // NB IMMT based 
   }
             
   // if no barometer connected 
   if (RS232_connection_mode != 1 && RS232_connection_mode != 2 && RS232_connection_mode != 4 && RS232_connection_mode != 5 && RS232_connection_mode != 6 && RS232_connection_mode != 7 && RS232_connection_mode != 8)  
   {
      jMenuItem55.setEnabled(false);                           // Dashboard - barometer
   }   
          
   
   // if AWS connected 
   if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11) // AWS connected mode
   {
      // AWS connected BUT screen resolution < 1366 * 768: disable Dashboard AWS (analog) 
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      double width_screen = screenSize.getWidth();
      double height_screen = screenSize.getHeight();
      //System.out.println("--- Screen resolution AWS Dashboard wind radar: " + width_screen + " x " + height_screen);   
      if ((width_screen < 1366) || (height_screen < 768))
      {
         jMenuItem56.setEnabled(false);                         // Dashboard - AWS [analog)
      }
      
      // disable Dashboard - latest obs
      jMenuItem58.setEnabled(false);                            // Dashboard - latest obs
      
      // disable Maps -> Obs's Map (offline)                    // NB IMMT log based
      jMenuItem66.setEnabled(false);                            // Maps -> Obs's Map (offline)
      
      // disable Maps -> Obs's Map (online)                     // NB IMMT log based
      jMenuItem67.setEnabled(false);                            // Maps -> Obs's Map (online)
   }
   
   
   // not APR
   if (main.APR == false)
   {
      jMenuItem77.setEnabled(false);                            // Dashboard -> APR meteo radar
   }
   
   
   // online mode (TurboWeb)
   //     NB disable all offline Maps links because these offline maps will never be present in TurboWeb mode 
   //     (to keep TurboWeb as small as possible + they will always have an internet connection for the online Maps)
   if (offline_mode == false)
   {
       // disable Maps -> Obs's Map (offline)                   // NB IMMT log based
      jMenuItem66.setEnabled(false);                            // Maps -> Obs's Map (offline)
      
      // disable Maps -> AWS sensor Map (offline)               // Maps -> AWS sensor Map (offline)  
      jMenuItem62.setEnabled(false);                            // NB sensor_data files based 
      
      // disable Maps -> AWS visual Map (offline)               // Maps -> AWS visual Map (offline)
      jMenuItem68.setEnabled(false);                            // NB IMMT based 
   }
   
   
   // for DWD (Germany) even if an AWS is connected, wind radar always disabled! 
   //if (recruiting_country.indexOf("GERMANY") != -1)
   //{
   //   // disable Dashboard AWS (wind radar)
   //   jMenuItem64.setEnabled(false);                            // Dashboard - AWS [wind radar)        
   //}
}



   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void coded_obs_update()
   {
      //if (!main.obs_format.equals(main.FORMAT_AWS))            // not AWS connected mode
      if ((RS232_connection_mode != 3) && (RS232_connection_mode != 9) && (RS232_connection_mode != 10) && (RS232_connection_mode != 11)) // not EUCAWS and not OMC-140 and not AMOS2X connected mode    
      {
         //System.out.println("+++++++++++ coded_obs_update");
         
         // NB in case format 101 (compressed) this coded obs is only a kind of decompressed FM13 obs
         //    especially Is (icing) has a specially coding/meaning 
         //    (see also Function private void initSynopparameters() in icing.java)
         //     So it is possible that the icing group contains 6 characters!
         
         /* update coded obs status field (bottom line main -progress- window) */
         String SPATIE = SPATIE_OBS_VIEW;                                          // dan gewoon " " als spatie grbruiken 
         jTextField4.setText(compose_coded_obs(SPATIE)); 
      }
   }           

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void bepaal_frame_location()
   {
      Toolkit kit = Toolkit.getDefaultToolkit();
      Dimension screenSize = kit.getScreenSize();
      screenWidth = screenSize.width;
      screenHeight = screenSize.height;

      // compute x-y location start screen + set Location to this position
      x_pos_start_frame = screenWidth / 2 - (1050 / 2);
      y_pos_start_frame = screenHeight / 2 - (740 / 2);
      setLocation(x_pos_start_frame, y_pos_start_frame);

      // compute x-y location main screen (Maintenance etc)
      x_pos_main_frame = screenWidth / 2 - (1000 / 2);
      y_pos_main_frame = screenHeight / 2 - (700 / 2);
      
      // compute x-y location main screen (AMVER forms)
      x_pos_amver_frame = screenWidth / 2 - (1000 / 2);
      y_pos_amver_frame = screenHeight / 2 - (750 / 2);
      
      // compute position of other (parameter/element like waves) screens(frames)
      x_pos_frame = screenWidth / 2 - (800 / 2);
      y_pos_frame = screenHeight / 2 - (600 / 2);
    
      // compute position of latest obsabout screen
      x_pos_small_frame = screenWidth / 2 - (400 / 2);
      y_pos_small_frame = screenHeight / 2 - (300 / 2);
      
      // compute position of info-about screen
      x_pos_about_frame = screenWidth / 2 - (600 / 2);
      y_pos_about_frame = screenHeight / 2 - (700 / 2);
      
      // compute position of calculator screen
      x_pos_calculator_frame = screenWidth / 2 - (350 / 2);
      y_pos_calculator_frame = screenHeight / 2 - (550 / 2);
      
      // compute x-y location Dashboard latest measurements table screen
      //x_pos_latestmeasurements_frame = screenWidth / 2 - (1000 / 2);
      //y_pos_latestmeasurements_frame = screenHeight / 2 - (750 / 2);
       
      // compute position of calculator screen
      x_pos_pop_up_frame = screenWidth / 2 - (401 / 2);
      y_pos_pop_up_frame = screenHeight / 2 - (236 / 2);
      
      // compute position of immt log period screen
      x_pos_immtlogperiod_frame = screenWidth / 2 - (600 / 2);
      y_pos_immtlogperiod_frame = screenHeight / 2 - (300 / 2);
   }      

   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static String compose_coded_obs(String SPATIE)
   {
      String coded_obs_call_sign;                     // station ID from version 4.2
      String coded_obs_YY;
      String coded_obs_GG;
      String coded_obs_iw;
      String coded_obs_lalala;
      String coded_obs_lolololo;
      String coded_obs_Qc;
      String coded_obs_Ds;
      String coded_obs_vs;
      String coded_obs_N;
      String coded_obs_Nh;
      String coded_obs_h;
      String coded_obs_VV;
      String coded_obs_dd;
      String coded_obs_ff;
      String coded_obs_snTTT;
      String coded_obs_snTdTdTd;
      String coded_obs_PPPP;   
      String coded_obs_a;   
      String coded_obs_ppp;   
      String coded_obs_ww;
      String coded_obs_W1;
      String coded_obs_W2;
      String coded_obs_Cl;
      String coded_obs_Cm;
      String coded_obs_Ch;
      String coded_obs_ssTsTsTs;
      String coded_obs_Pw;
      String coded_obs_Hw;
      String coded_obs_Dw1;
      String coded_obs_Hw1;
      String coded_obs_Pw1;
      String coded_obs_Dw2;
      String coded_obs_Hw2;
      String coded_obs_Pw2;
      String coded_obs_Is;
      String coded_obs_EsEs;
      String coded_obs_Rs;
      String coded_obs_snTbTbTb;
      String coded_obs_ci;
      String coded_obs_Si;
      String coded_obs_bi;
      String coded_obs_Di;
      String coded_obs_zi;
              
      
      // if station ID or masked call sign (= masked sation ID) inserted -> use this for obs else 'normal' call sign
      //
      if ((station_ID != null) && (station_ID.trim().length() > 0))
      {
         coded_obs_call_sign = station_ID;       
      }
      //else if ((masked_call_sign != null) && (masked_call_sign.trim().length() > 0))
      //{
      //   coded_obs_call_sign = masked_call_sign;
      //}
      //else if ((call_sign != null) && (call_sign.trim().length() > 0))
      //{
      //   coded_obs_call_sign = call_sign;
      //}
      else
      {
         coded_obs_call_sign = "unknown";
      }
         
      //JOptionPane.showMessageDialog(null, obs_call_sign, "mycallsign.call_sign", JOptionPane.INFORMATION_MESSAGE);

      // date/time (null value if YY_code never activated /null pointer, "" if page visited but nothing done)"
      //
      if ((mydatetime.YY_code != null) && (mydatetime.YY_code.compareTo("") != 0))     // day
         coded_obs_YY = mydatetime.YY_code;
      else
         coded_obs_YY = "//";
         
      if ((mydatetime.GG_code != null) && (mydatetime.GG_code.compareTo("") != 0))     // hour
         coded_obs_GG = mydatetime.GG_code;
      else
         coded_obs_GG = "//";
         
      
      // position
      //
      if ((myposition.Qc_code != null) && (myposition.Qc_code.compareTo("") != 0))
         coded_obs_Qc = myposition.Qc_code;
      else
         coded_obs_Qc = "/";
         
      if ((myposition.lalala_code != null) && (myposition.lalala_code.compareTo("") != 0))
         coded_obs_lalala = myposition.lalala_code;
      else
         coded_obs_lalala = "///";
         
      if ((myposition.lolololo_code != null) && (myposition.lolololo_code.compareTo("") != 0))
         coded_obs_lolololo = myposition.lolololo_code;
      else
         coded_obs_lolololo = "////";
         
      
      // ship course
      //
      if ((myposition.Ds_code != null) && (myposition.Ds_code.compareTo("") != 0))
         coded_obs_Ds = myposition.Ds_code;
      else
         coded_obs_Ds = "/";
         
      
      // ship speed
      //
      if ((myposition.vs_code != null) && (myposition.vs_code.compareTo("") != 0))
         coded_obs_vs = myposition.vs_code;
      else
         coded_obs_vs = "/";
         
         
      // total cloud cover (N) 
      //
      if ((mycloudcover.N_code != null) && (mycloudcover.N_code.compareTo("") != 0))
         coded_obs_N = mycloudcover.N_code;
      else
         coded_obs_N = "/"; 
         
      
      // cover Cl/Cm (Nh)
      //
      if ((mycloudcover.Nh_code != null) && (mycloudcover.Nh_code.compareTo("") != 0))
         coded_obs_Nh = mycloudcover.Nh_code;
      else
         coded_obs_Nh = "/"; 
        
      
      // height lowest cloud in the sky (h)
      //
      if ((mycloudcover.h_code != null) && (mycloudcover.h_code.compareTo("") != 0))
         coded_obs_h = mycloudcover.h_code;
      else
         coded_obs_h = "/"; 
         
         
      // visibility (VV)
      //
      if ((myvisibility.VV_code != null) && (myvisibility.VV_code.compareTo("") != 0))
         coded_obs_VV = myvisibility.VV_code;
      else
         coded_obs_VV = "//"; 
         

      // winds source
      if ((mywind.iw_code != null) && (mywind.iw_code.compareTo("") != 0))
         coded_obs_iw = mywind.iw_code;
      else
         coded_obs_iw = "/";


      // wind direction (dd)
      //
      if ((mywind.dd_code != null) && (mywind.dd_code.compareTo("") != 0))
         coded_obs_dd = mywind.dd_code;
      else
         coded_obs_dd = "//"; 

      
      // wind speed (ff)
      //
      if ((mywind.ff_code != null) && (mywind.ff_code.compareTo("") != 0))
      {
         coded_obs_ff = mywind.ff_code;
         
         // extra 00fff group only added if wind speed >= 100 units (so only if fff00_code != "")
         if ((mywind.fff00_code != null) && (mywind.fff00_code.compareTo("") != 0))
         {
            coded_obs_ff += SPATIE + "00" + mywind.fff00_code;
         }
      }
      else
      {
         coded_obs_ff = "//";
      } 
         
         
      // air temperature 
      //
      if ((mytemp.sn_TTT_code != null) && (mytemp.sn_TTT_code.compareTo("") != 0) &&
          (mytemp.TTT_code != null)    && (mytemp.TTT_code.compareTo("") != 0) )
         coded_obs_snTTT = mytemp.sn_TTT_code + mytemp.TTT_code;
      else
         coded_obs_snTTT = "////"; 
         
      
      // dew point
      //
      if ((mytemp.sn_TdTdTd_code != null) && (mytemp.sn_TdTdTd_code.compareTo("") != 0) &&
          (mytemp.TdTdTd_code != null)    && (mytemp.TdTdTd_code.compareTo("") != 0) )
         coded_obs_snTdTdTd = mytemp.sn_TdTdTd_code + mytemp.TdTdTd_code;
      else
         coded_obs_snTdTdTd = "////"; 
         
         
      // air pressure (at MSL)
      //
      if ((mybarometer.PPPP_code != null) && (mybarometer.PPPP_code.compareTo("") != 0))
         coded_obs_PPPP = mybarometer.PPPP_code;
      else
         coded_obs_PPPP = "////"; 
         
      
      // air pressure tendency characteristic
      //
      if ((mybarograph.a_code != null) && (mybarograph.a_code.compareTo("") != 0))
         coded_obs_a = mybarograph.a_code;
      else
         coded_obs_a = "/"; 

      
      // air pressure tendency amount
      //
      if ((mybarograph.ppp_code != null) && (mybarograph.ppp_code.compareTo("") != 0))
         coded_obs_ppp = mybarograph.ppp_code;
      else
         coded_obs_ppp = "///"; 
      
      
      // present weather
      //
      if ((mypresentweather.ww_code != null) && (mypresentweather.ww_code.compareTo("") != 0))
         coded_obs_ww = mypresentweather.ww_code;
      else
         coded_obs_ww = "//"; 
      
      
      // past weather 1
      //
      if ((mypastweather.W1_code != null) && (mypastweather.W1_code.compareTo("") != 0))
         coded_obs_W1 = mypastweather.W1_code;
      else
         coded_obs_W1 = "/"; 
     
      
      // past weather 2
      //
      if ((mypastweather.W2_code != null) && (mypastweather.W2_code.compareTo("") != 0))
         coded_obs_W2 = mypastweather.W2_code;
      else
         coded_obs_W2 = "/"; 
     
         
      // clouds low (Cl)
      //
      if ((mycl.cl_code != null) && (mycl.cl_code.compareTo("") != 0))
         coded_obs_Cl = mycl.cl_code;
      else
         coded_obs_Cl = "/"; 
      
      
      // clouds middle (Cm)
      //
      if ((mycm.cm_code != null) && (mycm.cm_code.compareTo("") != 0))
         coded_obs_Cm = mycm.cm_code.substring(0, 1);    // omdat bij cm_code in geval Cm7 een a, b, c er achter staat (dus 7a, 7b, 7c)
      else
         coded_obs_Cm = "/"; 
      
      
      // clouds high (Ch)
      //
      if ((mych.ch_code != null) && (mych.ch_code.compareTo("") != 0))
         coded_obs_Ch = mych.ch_code;
      else
         coded_obs_Ch = "/"; 
      
      
      // Tsea
      //
      if ((mytemp.ss_TsTsTs_code != null) && (mytemp.ss_TsTsTs_code.compareTo("") != 0) &&
          (mytemp.TsTsTs_code != null)    && (mytemp.TsTsTs_code.compareTo("") != 0) )
         coded_obs_ssTsTsTs = mytemp.ss_TsTsTs_code + mytemp.TsTsTs_code;
      else
         coded_obs_ssTsTsTs = "////"; 
      
      
      // wind waves period
      //
      if ((mywaves.Pw_code != null) && (mywaves.Pw_code.compareTo("") != 0))
         coded_obs_Pw = mywaves.Pw_code;
      else
         coded_obs_Pw = "//"; 
      
      
      // wind waves height
      //
      if ((mywaves.Hw_code != null) && (mywaves.Hw_code.compareTo("") != 0))
         coded_obs_Hw = mywaves.Hw_code;
      else
         coded_obs_Hw = "//"; 
      
      
      // swell 1 direction
      //

      //JOptionPane.showMessageDialog(null, mywaves.Dw1_code, "mywaves.Dw1_code", JOptionPane.WARNING_MESSAGE);
      if ((mywaves.Dw1_code != null) && (mywaves.Dw1_code.compareTo("") != 0))
         coded_obs_Dw1 = mywaves.Dw1_code;
      else
         coded_obs_Dw1 = "//"; 
     
      
      // swell 1 period
      //
      if ((mywaves.Pw1_code != null) && (mywaves.Pw1_code.compareTo("") != 0))
         coded_obs_Pw1 = mywaves.Pw1_code;
      else
         coded_obs_Pw1 = "//"; 
     
      
      // swell 1 height
      //
      if ((mywaves.Hw1_code != null) && (mywaves.Hw1_code.compareTo("") != 0))
         coded_obs_Hw1 = mywaves.Hw1_code;
      else
         coded_obs_Hw1 = "//"; 
     
      
      // swell 2 direction
      //
      if ((mywaves.Dw2_code != null) && (mywaves.Dw2_code.compareTo("") != 0))
         coded_obs_Dw2 = mywaves.Dw2_code;
      else
         coded_obs_Dw2 = "//"; 
     
      
      // swell 2 period
      //
      if ((mywaves.Pw2_code != null) && (mywaves.Pw2_code.compareTo("") != 0))
         coded_obs_Pw2 = mywaves.Pw2_code;
      else
         coded_obs_Pw2 = "//"; 
     
      
      // swell 2 height
      //
      if ((mywaves.Hw2_code != null) && (mywaves.Hw2_code.compareTo("") != 0))
         coded_obs_Hw2 = mywaves.Hw2_code;
      else
         coded_obs_Hw2 = "//"; 


      // icing cause (Is)
      //
      if ((myicing.Is_code != null) && (myicing.Is_code.compareTo("") != 0))
      {
         coded_obs_Is = myicing.Is_code;
      }
      else
      {
         coded_obs_Is = "/";
      }


      // icing thickness (EsEs)
      //
      if ((myicing.EsEs_code != null) && (myicing.EsEs_code.compareTo("") != 0))
      {
         coded_obs_EsEs = myicing.EsEs_code;
      }
      else
      {
         coded_obs_EsEs = "//";
      }


      // icing rate (Rs)
      //
      if ((myicing.Rs_code != null) && (myicing.Rs_code.compareTo("") != 0))
      {
         coded_obs_Rs = myicing.Rs_code;
      }
      else
      {
         coded_obs_Rs = "/";
      }
      
      
      // wet bulb temperature (TbTbTb)
      //
      if ((mytemp.sn_TbTbTb_code != null) && (mytemp.sn_TbTbTb_code.compareTo("") != 0) &&
          (mytemp.TbTbTb_code != null)    && (mytemp.TbTbTb_code.compareTo("") != 0) )
         coded_obs_snTbTbTb = mytemp.sn_TbTbTb_code + mytemp.TbTbTb_code;
      else
         coded_obs_snTbTbTb = "////"; 


      // concentration or arrangement of sea ice (ci)
      //
      if ((myice1.ci_code != null) && (myice1.ci_code.compareTo("") != 0))
      {
         if (myice1.ci_code.trim().equals("u"))              // unable to report etc.
         {
            coded_obs_ci = "/";
         }
         else
         {
            coded_obs_ci = myice1.ci_code;
         }
      }
      else
      {
         coded_obs_ci = "/";
      }


      // stage of development Si)
      //
      if ((myice1.Si_code != null) && (myice1.Si_code.compareTo("") != 0))
      {
         if (myice1.Si_code.trim().equals("u"))              // unable to report etc.
         {
            coded_obs_Si = "/";
         }
         else
         {
            coded_obs_Si = myice1.Si_code;
         }
      }
      else
      {
         coded_obs_Si = "/";
      }


      // Ice of land origin (bi)
      //
      if ((myice1.bi_code != null) && (myice1.bi_code.compareTo("") != 0))
      {
         if (myice1.bi_code.trim().equals("u"))              // unable to report etc.
         {
            coded_obs_bi = "/";
         }
         else
         {
            coded_obs_bi = myice1.bi_code;
         }
      }
      else
      {
         coded_obs_bi = "/";
      }


      // Bearing of principal ice edge (Di)
      //
      if ((myice1.Di_code != null) && (myice1.Di_code.compareTo("") != 0))
      {
         if (myice1.Di_code.trim().equals("u"))              // unable to report etc.
         {
            coded_obs_Di = "/";
         }
         else
         {
            coded_obs_Di = myice1.Di_code;
         }
      }
      else
      {
         coded_obs_Di = "/";
      }

     
      // Ice situation and trend over preceding three hours (zi)
      //
      if ((myice1.zi_code != null) && (myice1.zi_code.compareTo("") != 0))
      {
         if (myice1.zi_code.trim().equals("u"))              // unable to report etc.
         {
            coded_obs_zi = "/";
         }
         else
         {
            coded_obs_zi = myice1.zi_code;
         }
      }
      else
      {
         coded_obs_zi = "/";
      }


      
      if ( (coded_obs_call_sign.compareTo("unknown") != 0) &&
           (coded_obs_YY.compareTo("//") != 0) && (coded_obs_GG.compareTo("//") != 0) &&
           (coded_obs_Qc.compareTo("/") != 0) && (coded_obs_lalala.compareTo("///") != 0) && (coded_obs_lolololo.compareTo("////") != 0) )
      {   
         // LET OP
         // NB met IE7 gaat als je voor spatie een " " neemt het wel goed
         //    met FireFox gaat als je voor spatie " " neemt het NIET goed

         coded_obs_total =
                 "BBXX" +                                                                         // BBXX
                 SPATIE +
                 coded_obs_call_sign +                                                            // D..D
                 SPATIE +
                 coded_obs_YY + coded_obs_GG + coded_obs_iw +                                     // YYGGiw
                 SPATIE +
                 "99" + coded_obs_lalala +                                                        // 99LaLaLa
                 SPATIE +
                 coded_obs_Qc + coded_obs_lolololo +                                              // QcLoLoLoLo
                 SPATIE +
                 "41" + coded_obs_h + coded_obs_VV +                                              // irihhVV
                 SPATIE +
                 coded_obs_N + coded_obs_dd + coded_obs_ff +                                      // Nddff
                 SPATIE +
                 "1" + coded_obs_snTTT +                                                          // 1snTTT
                 SPATIE +
                 "2" + coded_obs_snTdTdTd +                                                       // 2snTdTdTd
                 SPATIE +
                 "4" + coded_obs_PPPP +                                                           // 4PPPP
                 SPATIE +
                 "5" + coded_obs_a + coded_obs_ppp +                                              // 5appp
                 SPATIE +
                 "7" + coded_obs_ww + coded_obs_W1 + coded_obs_W2 +                               // 7wwW1W2                                                         // 4PPPP
                 SPATIE +
                 "8" + coded_obs_Nh + coded_obs_Cl + coded_obs_Cm + coded_obs_Ch +                // 8NhClCmCh                                                         // 4PPPP
                 SPATIE +
                 "222" + coded_obs_Ds + coded_obs_vs +                                            // 222Dsvs                                                         // 4PPPP
                 SPATIE +
                 "0" + coded_obs_ssTsTsTs +                                                       // 0ssTwTwTw
                 SPATIE +
                 "2" + coded_obs_Pw + coded_obs_Hw;                                              // 2PwPwHwHw
                 //SPATIE +
                 //"3" + coded_obs_Dw1 + coded_obs_Dw2 +                                            // 3dw1dw1dw2dw2
                 //SPATIE +
                 //"4" + coded_obs_Pw1 + coded_obs_Hw1;                                             // 4Pw1Pw1Hw1Hw1

         if (((coded_obs_Dw1 + coded_obs_Dw2).equals("////") == false))
         {
            // swell dir group only if relevant data available (not ////)
            coded_obs_total +=
                 SPATIE +
                 "3" + coded_obs_Dw1 + coded_obs_Dw2;                                             // 5Pw2Pw2Hw2Hw2
         } // if (((coded_obs_Dw1 + coded_obs_Dw2).equals("////") == false)) 

         if ( ((coded_obs_Pw1 + coded_obs_Hw1).equals("////") == false) || (coded_obs_Dw1.equals("99") == true) )
         {
            // 1st swell group only if relevant data available (not ////) or if confused swell (Dw1 = 99)
            coded_obs_total +=
                 SPATIE +
                 "4" + coded_obs_Pw1 + coded_obs_Hw1;                                             // 5Pw2Pw2Hw2Hw2
         } // if ( ((coded_obs_Pw1 + coded_obs_Hw1).equals("////") == false) )

         if (((coded_obs_Pw2 + coded_obs_Hw2).equals("////") == false)) 
         {
            // 2nd swell group only if relevant data available (not ////)
            coded_obs_total +=
                 SPATIE +
                 "5" + coded_obs_Pw2 + coded_obs_Hw2;                                             // 5Pw2Pw2Hw2Hw2
         } // if ( ((coded_obs_Pw2 + coded_obs_Hw2).equals("////") == false) )

         if (((coded_obs_Is + coded_obs_EsEs + coded_obs_Rs).equals("////") == false))
         {
            // icing group only if relevant data available (not ////)
            coded_obs_total +=
                 SPATIE +
                 "6" + coded_obs_Is + coded_obs_EsEs + coded_obs_Rs;                             // 6IsEsEsRs
         } // if (((coded_obs_Is + coded_obs_EsEs + coded_obs_Rs).equals("////") == false))

         if ((coded_obs_snTbTbTb.equals("////") == false)) 
         {
            // wet bulb group only if relevant data available (not ////)
            coded_obs_total +=
                 SPATIE +
                 "8" + coded_obs_snTbTbTb;                                                        // 8swTbTbTb
         } // if ( (coded_obs_snTbTbTb.equals("////") == false) )

         if (((coded_obs_ci + coded_obs_Si + coded_obs_bi + coded_obs_Di + coded_obs_zi).equals("/////") == false))
         {
            // ice group only if relevant data available (not /////)
            coded_obs_total +=
                 SPATIE +
                 "ICE" +
                 SPATIE + 
                 coded_obs_ci + coded_obs_Si + coded_obs_bi + coded_obs_Di + coded_obs_zi;                                     // ICE ciSibiDizi
         } // 


         coded_obs_total +=
                 "=";
         
      } // if ( (coded_obs_call_sign.compareTo("unknown") != 0) etc.
      else // call sign, dat/time or position not inserted
      {
         coded_obs_total = UNDEFINED;
      } // else
       
         
      return coded_obs_total;   
   }
  
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void fill_configuratie_array()
   { 
      configuratie_regels[0] =  main.SHIP_NAME_TXT + main.ship_name.trim();                              
      configuratie_regels[1] =  main.IMO_NUMBER_TXT + main.imo_number.trim();                            
      configuratie_regels[2] =  main.CALL_SIGN_TXT;                                   // main.CALL_SIGN_TXT + main.call_sign.trim();                              
      configuratie_regels[3] =  main.MASKED_CALL_SIGN_TXT;                            // main.MASKED_CALL_SIGN_TXT + main.masked_call_sign.trim();                
      configuratie_regels[4] =  main.TIME_ZONE_COMPUTER_TXT + main.time_zone_computer.trim();            
      configuratie_regels[5] =  main.RECRUITING_COUNTRY_TXT + main.recruiting_country.trim();            
      configuratie_regels[6] =  main.METHOD_WAVES_TXT + main.method_waves.trim();                        
      configuratie_regels[7] =  main.WIND_SOURCE_TXT + main.wind_source.trim();                          
      configuratie_regels[8] =  main.BAROMETER_ABOVE_SLL_TXT + main.barometer_above_sll.trim();          
      configuratie_regels[9] =  main.BAROMETER_KEEL_TO_SLL_TXT + main.keel_sll.trim();                   
      configuratie_regels[10] = main.PRESSURE_READING_MSL_TXT + main.pressure_reading_msl_yes_no.trim(); 
      configuratie_regels[11] = main.AIR_TEMP_EXPOSURE_TXT + main.air_temp_exposure.trim();              
      configuratie_regels[12] = main.SST_EXPOSURE_TXT + main.sst_exposure.trim();                        
      configuratie_regels[13] = main.MAX_HEIGHT_DECK_CARGO_TXT + main.max_height_deck_cargo.trim();       
      configuratie_regels[14] = main.DIFF_SLL_WL_TXT + main.diff_sll_wl.trim();                          
      configuratie_regels[15] = main.OBS_EMAIL_RECIPIENT_TXT + main.obs_email_recipient;                 
      configuratie_regels[16] = main.OBS_EMAIL_SUBJECT_TXT + main.obs_email_subject;                     
      configuratie_regels[17] = main.LOGS_DIR_TXT + main.logs_dir;                         
      configuratie_regels[18] = main.LOGS_EMAIL_RECIPIENT_TXT + main.logs_email_recipient;               
      configuratie_regels[19] = main.WIND_UNITS_TXT + main.wind_units.trim();                            
      configuratie_regels[20] = main.RS232_INSTRUMENT_TYPE_TXT + main.RS232_connection_mode;             
      configuratie_regels[21] = main.RS232_BITS_PER_SEC_TXT + main.bits_per_second;                      
      configuratie_regels[22] = main.RS232_DATA_BITS_TXT + main.data_bits;                               
      configuratie_regels[23] = main.RS232_PARITY_TXT + main.parity;                                     
      configuratie_regels[24] = main.RS232_STOP_BITS_TXT + main.stop_bits;                               
      configuratie_regels[25] = main.RS232_PREFERED_COM_PORT_TXT + main.prefered_COM_port_number;        
      configuratie_regels[26] = main.IC_BAROMETER_TXT + main.barometer_instrument_correction;           
      configuratie_regels[27] = main.OBS_FORMAT_TXT + main.obs_format.trim();                            
      configuratie_regels[28] = main.FORMAT_101_ENCRYPTION_TXT + main.obs_101_encryption.trim();        
      configuratie_regels[29] = main.FORMAT_101_EMAIL_TXT + main.obs_101_email.trim();                   
      configuratie_regels[30] = main.RS232_PREF_COM_PORT_NAME_TXT + main.prefered_COM_port_name;        // not in use from version 3.4 
      configuratie_regels[31] = main.WOW_PUBLISH_TXT + String.valueOf(main.WOW);                        // boolean
      configuratie_regels[32] = main.WOW_SITE_ID_TXT + main.WOW_site_id;
      configuratie_regels[33] = main.WOW_PIN_TXT + main.WOW_site_pin;
      configuratie_regels[34] = main.WOW_REPORTING_INTERVAL_TXT + main.WOW_reporting_interval;
      configuratie_regels[35] = main.WOW_APR_AVERAGE_DRAUGHT_TXT + main.WOW_APR_average_draught;
      configuratie_regels[36] = main.AMOS_MAIL_TXT + String.valueOf(main.amos_mail);                    // boolean
      configuratie_regels[37] = main.RS232_GPS_TYPE_TXT + main.RS232_GPS_connection_mode;
      configuratie_regels[38] = main.RS232_GPS_BITS_PER_SEC_TXT + main.GPS_bits_per_second;
      configuratie_regels[39] = main.RS232_GPS_COM_PORT_TXT + main.prefered_GPS_COM_port_number;
      configuratie_regels[40] = main.RS232_GPS_COM_PORT_NAME_TXT + main.prefered_GPS_COM_port_name;
      configuratie_regels[41] = main.RS232_GPS_SENTENCE_TXT + main.RS232_GPS_sentence;                  // integer
      configuratie_regels[42] = main.APR_TXT + String.valueOf(main.APR);                                // boolean
      configuratie_regels[43] = main.APR_REPORTING_INTERVAL_TXT + main.APR_reporting_interval;
      configuratie_regels[44] = main.UPLOAD_URL_TXT + main.upload_URL;   
      configuratie_regels[45] = main.AWSR_TXT + String.valueOf(main.AWSR);                              // boolean
      configuratie_regels[46] = main.AWSR_REPORTING_INTERVAL_TXT + main.AWSR_reporting_interval;
      configuratie_regels[47] = main.WIND_UNITS_DASHBOARD_TXT + main.wind_units_dashboard.trim();  
      configuratie_regels[48] = main.SHIP_TYPE_DASHBOARD_TXT + main.ship_type_dashboard;  
      configuratie_regels[49] = main.HEIGHT_ANEMOMETER_TXT + main.height_anemometer; 
      configuratie_regels[50] = main.GUI_MODE_TXT + main.GUI_mode;
      configuratie_regels[51] = main.GUI_LOGO_TXT + main.GUI_logo;     
      configuratie_regels[52] = main.OBS_EMAIL_CC_TXT + main.obs_email_cc; 
      configuratie_regels[53] = main.LOCAL_EMAIL_SERVER_TXT + main.local_email_server; 
      configuratie_regels[54] = main.YOUR_GMAIL_ADDRESS_TXT + main.your_gmail_address;
      configuratie_regels[55] = main.GMAIL_APP_PASSWORD_TXT + main.gmail_app_password;
      configuratie_regels[56] = main.GMAIL_SECURITY_TXT + main.gmail_security;
      configuratie_regels[57] = main.YOUR_YAHOO_ADDRESS_TXT + main.your_yahoo_address;
      configuratie_regels[58] = main.YAHOO_APP_PASSWORD_TXT + main.yahoo_app_password;
      configuratie_regels[59] = main.YAHOO_SECURITY_TXT + main.yahoo_security;
      configuratie_regels[60] = main.YOUR_SHIP_ADDRESS_TXT + main.your_ship_address;
      configuratie_regels[61] = main.SMTP_HOST_PASSWORD_TXT + main.smtp_host_password;
      configuratie_regels[62] = main.SMTP_HOST_PORT_TXT + main.smtp_host_port;
      configuratie_regels[63] = main.RS232_INSTRUMENT_TYPE_TXT_II + main.RS232_connection_mode_II;       
      configuratie_regels[64] = main.RS232_BITS_PER_SEC_TXT_II + main.bits_per_second_II;                      
      configuratie_regels[65] = main.RS232_DATA_BITS_TXT_II + main.data_bits_II;                               
      configuratie_regels[66] = main.RS232_PARITY_TXT_II + main.parity_II;                                     
      configuratie_regels[67] = main.RS232_STOP_BITS_TXT_II + main.stop_bits_II;                               
      configuratie_regels[68] = main.RS232_PREFERED_COM_PORT_TXT_II + main.prefered_COM_port_number_II;        
      configuratie_regels[69] = main.APTR_AWSR_SEND_METHOD_TXT + main.APTR_AWSR_send_method; 
      configuratie_regels[70] = main.STATION_ID_TXT + main.station_ID;
      configuratie_regels[71] = main.DASHBOARD_BACKGROUND_IMAGE_TXT + main.dashboard_background_image;
      configuratie_regels[72] = main.YOUR_CUSTOM_ADDRESS_TXT + main.your_custom_address;
      configuratie_regels[73] = main.CUSTOM_EMAIL_SERVER_TXT + main.custom_email_server; 
      configuratie_regels[74] = main.CUSTOM_PASSWORD_TXT + main.custom_password;
      configuratie_regels[75] = main.CUSTOM_SECURITY_TXT + main.custom_security;
      configuratie_regels[76] = main.CUSTOM_PORT_TXT + main.custom_port;
      configuratie_regels[77] = main.POP_UP_DASHBOARD_TXT + String.valueOf(main.pop_up_screen);         // configuratie_regels[77] = main.POP_UP_DASHBOARD_TXT + String.valueOf(main.pop_up_dashboard);
      configuratie_regels[78] = main.POP_UP_DASHBOARD_INTERVAL_TXT + main.pop_up_screen_interval;       // configuratie_regels[78] = main.POP_UP_DASHBOARD_INTERVAL_TXT + main.pop_up_dashboard_interval; 
      configuratie_regels[79] = main.DASHBOARD_SHIP_DECK_COLOR_TXT + main.ship_deck_color_String;
      configuratie_regels[80] = main.CUSTOM_EMAIL_MODULE_TXT + main.custom_email_module;
      configuratie_regels[81] = main.LOGS_EMAIL_TXT + main.log_files_email_send_method;
      configuratie_regels[82] = main.EUCAWS_UPLOADS_METHOD_TXT + main.eucaws_uploads_method;
      configuratie_regels[83] = main.PORT_MODE_OPTION_TXT + main.port_mode_option;
      configuratie_regels[84] = main.LAN_IP_ADDRESS_TXT + main.lan_ip_address;
      configuratie_regels[85] = main.DASHBOARD_SHIP_TANK_COLOR_TXT + main.ship_tank_color_String;
      configuratie_regels[86] = main.DASHBOARD_FONT_TXT + main.dashboard_font;                          // for hybrid dashboard
      configuratie_regels[87] = main.COM_PROTOCOL_TXT + main.server_com_protocol;
   }
   
    
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void schrijf_configuratie_regels()
   {       
      /* NB input/output in een GUI altijd via een SwingWorker (Core Java Volume 1 bld 795 e.v.; Volume 2 bld 37, 215) */

      /* This is also a backup for writting to muffin !!! */
      /* backup (file configuration.txt) in: data dir (system defined) AND logs dir (user defined) */

      /* NB i.v.m. Swingworker backgroud proces kan het niet in 1 lus gebeuren */


      // fill array
      fill_configuratie_array();
      
      
      /*
      // to data dir
      */
      if ((data_dir != null) && (data_dir.compareTo("") != 0))
      {
         new SwingWorker<Void, Void>()
         {
            @Override
            protected Void doInBackground() throws Exception
            {
               // NB eg configuratie_regels[2]  = "wind source        : estimated; true speed and true direction"
               String volledig_path = data_dir + java.io.File.separator + CONFIGURATION_FILE;
      
                //JOptionPane.showMessageDialog(null, hulp_dir, APPLICATION_NAME + " hulp_dir", JOptionPane.WARNING_MESSAGE);
               try (BufferedWriter out = new BufferedWriter(new FileWriter(volledig_path, false)))
               {
                  for (int i = 0; i < MAX_AANTAL_CONFIGURATIEREGELS; i++)
                  {
                     if ((configuratie_regels[i] != null) && (configuratie_regels[i].compareTo("") != 0))
                     {
                        //System.out.println("+++ configuratie_regels[" + i + "] = " + configuratie_regels[i]);
                        
                        out.write(configuratie_regels[i]);
                        out.newLine();   // newLine(): write a line separator. The line separator string is defined by the system property line.separator, and is not necessarily a single newline ('\n') character.
                     }
                  } // for (int i = 0; i < MAX_AANTAL_CONFIGURATIEREGELS; i++)
      
               } // try
               //catch (Exception e)
               //{
               //   // No message if not writable, in the case of internet invoking (webstart) the data dir will be
               //   // windows\ystem32 (or something like that) and under Vista or Windows7 write protected
               //   //JOptionPane.showMessageDialog(null, "unable to write to: " + volledig_path, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               //} // catch
               catch (IOException ex) 
               {  
                  System.out.println("--- Function schrijf_configuratie_regels(): " + ex);
               }

      
               return null;
      
            } // protected Void doInBackground() throws Exception
         }.execute(); // new SwingWorker<Void, Void>()
      } // if ((data_dir != null) && (data_dir.compareTo("") != 0))


      /*
      // to logs dir
      */
      if ((logs_dir != null) && (logs_dir.compareTo("") != 0))
      {
         new SwingWorker<Void, Void>()
         {
            @Override
            protected Void doInBackground() throws Exception
            {
               // NB bv configuratie_regels[2]  = "wind source        : estimated; true speed and true direction"
               String volledig_path = logs_dir + java.io.File.separator + CONFIGURATION_FILE;

               //JOptionPane.showMessageDialog(null, hulp_dir, APPLICATION_NAME + " hulp_dir", JOptionPane.WARNING_MESSAGE);
               try (BufferedWriter out = new BufferedWriter(new FileWriter(volledig_path, false)))
               {
                  for (int i = 0; i < MAX_AANTAL_CONFIGURATIEREGELS; i++)
                  {
                     if ((configuratie_regels[i] != null) && (configuratie_regels[i].compareTo("") != 0))
                     {
                        out.write(configuratie_regels[i]);
                        out.newLine();   // newLine(): write a line separator. The line separator string is defined by the system property line.separator, and is not necessarily a single newline ('\n') character.
                     }
                  } // for (int i = 0; i < MAX_AANTAL_CONFIGURATIEREGELS; i++)

               } // try
               catch (IOException e)
               {
                  JOptionPane.showMessageDialog(null, "unable to write to: " + volledig_path, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               } // catch

               return null;

            } // protected Void doInBackground() throws Exception
         }.execute(); // new SwingWorker<Void, Void>()
      } // if ((logs_dir != null) && (logs_dir.compareTo("") != 0))
   } 


   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public void lees_configuratie_regels()
   {
      // called from: initComponents2()
      // 
      // or
      //
      // could also be:
      // An alternative if function read_muffin() failed (geen persistentService of geen muffin aanwezig) !!! 
      // on 2 locations configuration.txt present: - logs_dir (user defined in online mode or fixed subdir in offline mode)
      //                                           - data_dir (system defined)
      //
      // NB input/output GUI always via a SwingWorker (Core Java Volume 1 bld 795 e.v.; Volume 2 bld 37, 215) 

       
      
      // initialisation
      hulp_dir = "";

      if ((logs_dir != null) && (logs_dir.compareTo("") != 0))
      {
         hulp_dir = logs_dir;
      }
      else if ((data_dir != null) && (data_dir.compareTo("") != 0))
      {
         hulp_dir = data_dir;
      }


      if ((hulp_dir != null) && (hulp_dir.compareTo("") != 0))
      {
         new SwingWorker<Void, Void>()
         {
            @Override
            protected Void doInBackground() throws Exception
            {
               // NB e.g. configuratie_regels[2]  = "wind source        : estimated; true speed and true direction"
               int teller;
               String file_line;
               String volledig_path = hulp_dir + java.io.File.separator + CONFIGURATION_FILE;
      
               for (teller = 0; teller < MAX_AANTAL_CONFIGURATIEREGELS; teller++)
               {
                  configuratie_regels[teller] = "";
               }
      
               /* read all lines from configuration file */
               try (BufferedReader in = new BufferedReader(new FileReader(volledig_path)))
               {
                  teller = 0;
                  while((file_line = in.readLine()) != null)
                  {
                     configuratie_regels[teller] = file_line;
                     teller++;
            
                     /* for safety */
                     if (teller >= MAX_AANTAL_CONFIGURATIEREGELS)
                     {
                        break;
                     }
            
                  } // while((file_line = in.readLine()) != null)
               } // try
               catch (IOException e)
               {
                  // do nothing, it is possible (at first use) that the file was never created
               } // catch

               /* put collected meta data from configuration file into appropriate global vars */
               meta_data_from_configuration_regels_into_global_vars();

               return null;

            } // protected Void doInBackground() throws Exception

            @Override
            protected void done()
            {
               // "TurboWin+ started" message to log (logs dir must be known!)
               if (!theme_changed)
               {
                  log_turbowin_system_message("[GENERAL] started " + APPLICATION_NAME + " " + application_mode + " " + APPLICATION_VERSION);
               }
               else
               {
                  log_turbowin_system_message("[GENERAL] restarted main module (Theme changed)" + APPLICATION_NAME + " " + application_mode + " " + APPLICATION_VERSION);
               }
               
               // log Java version
               support_class.log_java_version();
               
               // log integrated libraries (jars)
               support_class.log_integrated_libraries();
               
               // log memory staistics
               support_class.log_memory_statistics();
               
               // deleting old (> 3 months) turbowin system logs
               log_turbowin_system_message("[GENERAL] deleting old (> 3 months) " + APPLICATION_NAME + " system logs");
               delete_logs_turbowin_system();
               
               /* station ID and ship name update on main (progress) screen" */
               ID_fields_update();
 
               /* meta data (mystationdata.java), eg IMO number and call sign, must be present */
               check_meta_data();
               
               /* if indicated by the user (via Maintenance menu) set GUI light mode */
               //if (GUI_mode.equals(GUI_LIGHT))
               //{
               //   set_GUI_light_mode();
               //}
               
               /* pop-up menu */
               create_popup_menu();                       // NB with dependency to GUI_mode
               
               /* check immt size (main.java) */
               check_immt_size();
               
 ////////////////////////////////////////// TEST //////////////////////////////////////////
 //              RS232_connection_mode = 6;     // mintakaStar WiFi
 ////////////////////////////////////////// TEST //////////////////////////////////////////               
               
               
               //
               ///////////// RS232/RS422/Wifi ///////////////
               //
               specific_connection_initComponents();
              
               /* gray (disable) the not appropriate graph/dashboard/maps menu selection options */
               disable_graph_menu_items();
               disable_dashboard_and_maps_menu_items();
               disable_and_enable_output_menu_items();
               set_APR_toolbar();
               set_AWSR_toolbar();
               
               /* GPS connected ? */
               //if (RS232_GPS_connection_mode == 1)   // 0 = no GPS; 1 = GPS (NMEA 1083)
               //{
               //   RS232_RS422.RS232_GPS_NMEA_0183_initComponents();
               //}

            }
         }.execute(); // new SwingWorker<Void, Void>()

         /* Hij komt alleen in deze functie als het lezen van de muffin niet gelukt was */
         /* het kan zijn dat de muffin er niet was omdat de temporary internet files net verwijderd waren */
         /* (via java control panel) */
         /* Daarom dan nu proberen weer naar muffin te schrijven */
         //set_muffin();
         //if (offline_mode_via_cmd == false)   // so offline_via_jnlp mode or online (webstart) mode
         //{
         //   set_muffin();
         //}   

      } // if ((hulp_dir != null) && (hulp_dir.compareTo("") != 0))
      
    }

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void disable_graph_menu_items()
{
   // NOTE
   // Even in the case of the settings indicate a barometer or AWS connected but the program couldn't find one (defaultPort == null)
   // you can still open the graphs for checking 'old' values via the main menu bar
   //
   // NOTE 
   // in that case with the right mouse button clicking on the minimised icon (iconfied) than in case of defaultPort == null
   // the graph options are disabled
   
   
   /* gray (disable) graph menu selection options */
   if (RS232_connection_mode == 0)                                                                  // no instrument connected
   {
      jMenuItem41.setEnabled(false);               // menu item graph air pressure
      jMenuItem43.setEnabled(false);               // menu item graph air temp
      jMenuItem44.setEnabled(false);               // menu item graph SST
      jMenuItem45.setEnabled(false);               // menu item graph wind speed
      jMenuItem47.setEnabled(false);               // menu item graph wind dir
      jMenuItem51.setEnabled(false);               // menu item graph total (pressure, air temp, wind dir, wind speed)
   }
   else if (RS232_connection_mode == 1 || RS232_connection_mode == 2 || RS232_connection_mode == 4 || RS232_connection_mode == 5 || RS232_connection_mode == 6) // PTB220 or PTB330 or Mintaka Duo or Mintaka Star USB or Mintaka Star WiFi connected
   {
      jMenuItem41.setEnabled(true);                // menu item graph air pressure
      jMenuItem43.setEnabled(false);               // menu item graph air temp
      jMenuItem44.setEnabled(false);               // menu item SST
      jMenuItem45.setEnabled(false);               // menu item graph wind speed
      jMenuItem47.setEnabled(false);               // menu item graph wind dir
      jMenuItem51.setEnabled(false);               // menu item graph total (pressure, air temp, wind dir, wind speed)
   }
   else if (RS232_connection_mode == 7 || RS232_connection_mode == 8) // Mintaka StarX USB or Mintaka StarX LAN connected
   {
      jMenuItem41.setEnabled(true);                // menu item graph air pressure
      jMenuItem43.setEnabled(true);                // menu item graph air temp
      jMenuItem44.setEnabled(false);               // menu item SST
      jMenuItem45.setEnabled(false);               // menu item graph wind speed
      jMenuItem47.setEnabled(false);               // menu item graph wind dir
      jMenuItem51.setEnabled(false);               // menu item graph total (pressure, air temp, wind dir, wind speed)
   }
   else if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)     // AWS connected
   {
      jMenuItem41.setEnabled(true);                // menu item graph air pressure
      jMenuItem43.setEnabled(true);                // menu item graph air temp
      jMenuItem44.setEnabled(true);                // menu item graph SST
      jMenuItem45.setEnabled(true);                // menu item graph wind speed (wind speed gust included as a second line)
      jMenuItem47.setEnabled(true);                // menu item graph wind dir
      jMenuItem51.setEnabled(true);                // menu item graph total (pressure, air temp, wind dir, wind speed)
   }     
   
   
  /************* TEST BEGIN ******************/
  //RS232_connection_mode_II = 1;
  /************* TEST END *******************/
   
   if (RS232_connection_mode_II == 1) 
   {
      jMenuItem43.setEnabled(true);               // menu item graph air temp
   }
            
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void meta_data_from_configuration_regels_into_global_vars()
{
   // called from:
   //   - import_button_actionPerformed() [tmystationdata.java]
   //   - lees_configuratie_regels() [main.java]
   //   - Maintenance_Import_maintenance_data_actionPerformed() [main.java]
   
   
   /* put collected meta data from muffin (or configuration file if read muffin failed) into appropriate global vars */
   for (int teller = 0; teller < MAX_AANTAL_CONFIGURATIEREGELS; teller++)
   {
      if ((configuratie_regels[teller] != null) && (configuratie_regels[teller].compareTo("") != 0))
      {
         // ship name
         if (configuratie_regels[teller].indexOf(SHIP_NAME_TXT) != -1)
         {
            // zo ja, dan staat op een bepaalde pos (achter de : ) de inhoud
            ship_name = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // imo number
         if (configuratie_regels[teller].indexOf(IMO_NUMBER_TXT) != -1)
         {
            imo_number = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // call sign
         //if (configuratie_regels[teller].indexOf(CALL_SIGN_TXT) != -1)
         //{
         //   call_sign = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}

         // masked call sign
         //if (configuratie_regels[teller].indexOf(MASKED_CALL_SIGN_TXT) != -1)
         //{
         //   masked_call_sign = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}

         // time zone computer
         if (configuratie_regels[teller].indexOf(TIME_ZONE_COMPUTER_TXT) != -1)
         {
            time_zone_computer = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // recruiting country
         if (configuratie_regels[teller].indexOf(RECRUITING_COUNTRY_TXT) != -1)
         {
            recruiting_country = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // method determining waves
         if (configuratie_regels[teller].indexOf(METHOD_WAVES_TXT) != -1)
         {
            method_waves = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // wind meta data
         if (configuratie_regels[teller].indexOf(WIND_SOURCE_TXT) != -1)
         {
            wind_source = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         if (configuratie_regels[teller].indexOf(MAX_HEIGHT_DECK_CARGO_TXT) != -1)
         {
            max_height_deck_cargo = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         if (configuratie_regels[teller].indexOf(DIFF_SLL_WL_TXT) != -1)
         {
            diff_sll_wl = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // air pressure meta data
         if (configuratie_regels[teller].indexOf(BAROMETER_ABOVE_SLL_TXT) != -1)
         {
            barometer_above_sll = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         if (configuratie_regels[teller].indexOf(BAROMETER_KEEL_TO_SLL_TXT) != -1)
         {
            keel_sll = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         if (configuratie_regels[teller].indexOf(PRESSURE_READING_MSL_TXT) != -1)
         {
            pressure_reading_msl_yes_no = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // air temp exposure
         if (configuratie_regels[teller].indexOf(AIR_TEMP_EXPOSURE_TXT) != -1)
         {
            air_temp_exposure = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // sst exposure
         if (configuratie_regels[teller].indexOf(SST_EXPOSURE_TXT) != -1)
         {
            sst_exposure = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // Obs E-mail recipient
         if (configuratie_regels[teller].indexOf(OBS_EMAIL_RECIPIENT_TXT) != -1)
         {
            obs_email_recipient = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // Obs E-mail subject
         if (configuratie_regels[teller].indexOf(OBS_EMAIL_SUBJECT_TXT) != -1)
         {
            obs_email_subject = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // logs dir (for immt.log etc.)
         if (configuratie_regels[teller].indexOf(LOGS_DIR_TXT) != -1)
         {
            logs_dir = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // logs E-mail recipient
         if (configuratie_regels[teller].indexOf(LOGS_EMAIL_RECIPIENT_TXT) != -1)
         {
            logs_email_recipient = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // wind units (knots or m/s)
         if (configuratie_regels[teller].indexOf(WIND_UNITS_TXT) != -1)
         {
            wind_units = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
           

         // RS232 INSTRUMENT connection mode (= RS232 connected instrument type -none, PTB220, PTB330, SAWS-)
         if (configuratie_regels[teller].indexOf(RS232_INSTRUMENT_TYPE_TXT) != -1)
         {
            RS232_connection_mode = Integer.parseInt(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
         }
        
         // RS232 INSTRUMENT bits per sec
         if (configuratie_regels[teller].indexOf(RS232_BITS_PER_SEC_TXT) != -1)
         {
            bits_per_second = Integer.parseInt(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
         }
         
         // RS232 INSTRUMENT data bits
         if (configuratie_regels[teller].indexOf(RS232_DATA_BITS_TXT) != -1)
         {
            String hulp_data_bits = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
            
            switch (hulp_data_bits)  
            {
               case "7"  : data_bits = 7;
                                       break;
               case "8"  : data_bits = 8;
                                       break;
               default   : data_bits = 0;                                // non existing (data bits) value
                                       break;
            } // switch (hulp_data_bits)
         }

         // RS232 INSTRUMENT parity
         if (configuratie_regels[teller].indexOf(RS232_PARITY_TXT) != -1)
         {
            String hulp_parity = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
            
            switch (hulp_parity)
            {
               case "0" : parity = SerialPort.NO_PARITY;
                          break;
               case "1" : parity = SerialPort.ODD_PARITY;
                          break;
               case "2" : parity = SerialPort.EVEN_PARITY;
                          break;
               default  : parity = 99;                         // non existing (parity) value
                          break;
            } // switch (hulp_parity)            
         }
         
         // RS232 INSTRUMENT stop bits
         if (configuratie_regels[teller].indexOf(RS232_STOP_BITS_TXT) != -1)
         {
            String hulp_stop_bits = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
            
            switch (hulp_stop_bits)  
            {
               case "1"  : stop_bits = SerialPort.ONE_STOP_BIT;
                                       break;
               case "2"  : stop_bits = SerialPort.TWO_STOP_BITS;
                                       break;
               default   : stop_bits = 0;                       // non existing (stop bits) value
                                       break;
            } // switch (hulp_stop_bits)            
         }
         
         // RS232 INSTRUMENT prefered COM port (Windows and Linux)
         if (configuratie_regels[teller].indexOf(RS232_PREFERED_COM_PORT_TXT) != -1)
         {
            prefered_COM_port_number = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // ic (instrument correction) barometer
         if (configuratie_regels[teller].indexOf(IC_BAROMETER_TXT) != -1)
         {
            barometer_instrument_correction = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // obs format (FM13 or format 101)
         if (configuratie_regels[teller].indexOf(OBS_FORMAT_TXT) != -1)
         {
            obs_format = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // format 101 call sign encryption (yes or no)
         if (configuratie_regels[teller].indexOf(FORMAT_101_ENCRYPTION_TXT) != -1)
         {
            obs_101_encryption = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // format 101 email (body or attachement)
         if (configuratie_regels[teller].indexOf(FORMAT_101_EMAIL_TXT) != -1)
         {
            obs_101_email = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // RS232 INSTRUMENT prefered COM port name (OS X)
         if (configuratie_regels[teller].indexOf(RS232_PREF_COM_PORT_NAME_TXT) != -1)
         {
            prefered_COM_port_name = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }         
         
         // WOW publish?
         if (configuratie_regels[teller].indexOf(WOW_PUBLISH_TXT) != -1)
         {
            WOW = Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
            // NB You have to be carefull when using Boolean.valueOf(string) or Boolean.parseBoolean(string). 
            //    The reason for this is that the methods will always return false if the String is not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in WOW settings
         }  
         
         // WOW site id
         if (configuratie_regels[teller].indexOf(WOW_SITE_ID_TXT) != -1)
         {
            WOW_site_id = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }           
         
         // WOW pin
         if (configuratie_regels[teller].indexOf(WOW_PIN_TXT) != -1)
         {
            WOW_site_pin = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }           
         
         // WOW reporting(upload) interval
         if (configuratie_regels[teller].indexOf(WOW_REPORTING_INTERVAL_TXT) != -1)
         {
            WOW_reporting_interval = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }           
         
         // WOW/APR average draught
         if (configuratie_regels[teller].indexOf(WOW_APR_AVERAGE_DRAUGHT_TXT) != -1)
         {
            WOW_APR_average_draught = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }         
         
         // default E-mail program on this computer is AMOS Mail?
         if (configuratie_regels[teller].indexOf(AMOS_MAIL_TXT) != -1)
         {
            amos_mail = Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
            // NB You have to be carefull when using Boolean.valueOf(string) or Boolean.parseBoolean(string). 
            //    The reason for this is that the methods will always return false if the String is not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in email settings
         }   
         
         // RS232 GPS connection mode
         if (configuratie_regels[teller].indexOf(RS232_GPS_TYPE_TXT) != -1)
         {
            RS232_GPS_connection_mode = Integer.parseInt(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
         }
         
         // RS232 GPS bits per second
         if (configuratie_regels[teller].indexOf(RS232_GPS_BITS_PER_SEC_TXT) != -1)
         {
            GPS_bits_per_second = Integer.parseInt(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
         }         
        
         // RS232 prefered GPS COM port (Windows and Linux)
         if (configuratie_regels[teller].indexOf(RS232_GPS_COM_PORT_TXT) != -1)
         {
            prefered_GPS_COM_port_number = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }          
         
         // RS232 prefered GPS COM port (OS X)
         if (configuratie_regels[teller].indexOf(RS232_GPS_COM_PORT_NAME_TXT) != -1)
         {
            prefered_GPS_COM_port_name = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // RS232 GPS sentence (RMC or GGA)
         if (configuratie_regels[teller].indexOf(RS232_GPS_SENTENCE_TXT) != -1)
         {
            RS232_GPS_sentence = Integer.parseInt(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
         }
         
         // APR (Automated Pressure Reports)?
         if (configuratie_regels[teller].indexOf(APR_TXT) != -1)
         {
            APR = Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
            // NB You have to be carefull when using Boolean.valueOf(string) or Boolean.parseBoolean(string). 
            //    The reason for this is that the methods will always return false if the String is not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in APR settings
         }  
         
         // APR reporting(upload) interval
         if (configuratie_regels[teller].indexOf(APR_REPORTING_INTERVAL_TXT) != -1)
         {
            APR_reporting_interval = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // upload URL
         if (configuratie_regels[teller].indexOf(UPLOAD_URL_TXT) != -1)
         {
            upload_URL = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // AWSR (Automatic Weather Station Reports)?
         if (configuratie_regels[teller].indexOf(AWSR_TXT) != -1)
         {
            AWSR = Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
            // NB You have to be carefull when using Boolean.valueOf(string) or Boolean.parseBoolean(string). 
            //    The reason for this is that the methods will always return false if the String is not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in AWSR settings
         }  
         
         // AWSR reporting(upload) interval
         if (configuratie_regels[teller].indexOf(AWSR_REPORTING_INTERVAL_TXT) != -1)
         {
            AWSR_reporting_interval = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // wind units graps/dasboard
         if ((configuratie_regels[teller].indexOf(WIND_UNITS_DASHBOARD_TXT) != -1))
         {
            wind_units_dashboard = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // ship type for visual dashboard
         if (configuratie_regels[teller].indexOf(SHIP_TYPE_DASHBOARD_TXT) != -1)
         {
            ship_type_dashboard = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // anemometer height above WL
         if (configuratie_regels[teller].indexOf(HEIGHT_ANEMOMETER_TXT) != -1)
         {
            height_anemometer = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // GUI mode (light/full)
         if (configuratie_regels[teller].indexOf(GUI_MODE_TXT) != -1)
         {
            GUI_mode = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // GUI logo (EUMETNET/NOAAA/SOT)
         if (configuratie_regels[teller].indexOf(GUI_LOGO_TXT) != -1)
         {
            GUI_logo = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // obs email cc 
         if (configuratie_regels[teller].indexOf(OBS_EMAIL_CC_TXT) != -1)
         {
            obs_email_cc = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }

         // obs email local host 
         //if (configuratie_regels[teller].indexOf(LOCAL_EMAIL_SERVER_TXT) != -1)
         //{
         //   local_email_server = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);   // NB also called SMTP host
         //}

         // your Gmail address 
         //if (configuratie_regels[teller].indexOf(YOUR_GMAIL_ADDRESS_TXT) != -1)
         //{
         //   your_gmail_address = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}
         
         // Gmail app password 
         //if (configuratie_regels[teller].indexOf(GMAIL_APP_PASSWORD_TXT) != -1)
         //{
         //   gmail_app_password = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}
         
         // Gmail security
         //if (configuratie_regels[teller].indexOf(GMAIL_SECURITY_TXT) != -1)
         //{
         //   gmail_security = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}
         
         // your Yahoo address 
         //if (configuratie_regels[teller].indexOf(YOUR_YAHOO_ADDRESS_TXT) != -1)
         //{
         //   your_yahoo_address = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}
         
         // Yahoo app password 
         //if (configuratie_regels[teller].indexOf(YAHOO_APP_PASSWORD_TXT) != -1)
         //{
         //   yahoo_app_password = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}
         
         // Yahoo security
         //if (configuratie_regels[teller].indexOf(YAHOO_SECURITY_TXT) != -1)
         //{
         //   yahoo_security = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}
         
         // your ship address
         //if (configuratie_regels[teller].indexOf(YOUR_SHIP_ADDRESS_TXT) != -1)
         //{
         //   your_ship_address = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}
         
         // SMTP host password
         //if (configuratie_regels[teller].indexOf(SMTP_HOST_PASSWORD_TXT) != -1) 
         //{
         //   smtp_host_password = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}  
                 
         // SMTP host port
         //if (configuratie_regels[teller].indexOf(SMTP_HOST_PORT_TXT) != -1) 
         //{
         //   smtp_host_port = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         //}  
         
         
         // RS232 2nd meteo instrument connection mode (= RS232 connected instrument II type: none, StarX, HMP155)
         if (configuratie_regels[teller].indexOf(RS232_INSTRUMENT_TYPE_TXT_II) != -1)
         {
            RS232_connection_mode_II = Integer.parseInt(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
         }        
                 
         // RS232 2nd meteo instrument bits per sec
         if (configuratie_regels[teller].indexOf(RS232_BITS_PER_SEC_TXT_II) != -1)
         {
            bits_per_second_II = Integer.parseInt(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
         }        
                 
         // RS232 2nd meteo instrument data bits
         if (configuratie_regels[teller].indexOf(RS232_DATA_BITS_TXT_II) != -1)
         {
            String hulp_data_bits = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
            
            switch (hulp_data_bits)  
            {
               case "7"  : data_bits_II = 7;
                           break;
               case "8"  : data_bits_II = 8;
                           break;
               default   : data_bits_II = 0;                                // non existing (data bits) value
                           break;
            } // switch (hulp_data_bits)
         }

         // RS232 2nd meteo instrument parity
         if (configuratie_regels[teller].indexOf(RS232_PARITY_TXT_II) != -1)
         {
            String hulp_parity = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
            
            switch (hulp_parity)
            {
               case "0" : parity_II = SerialPort.NO_PARITY;
                          break;
               case "1" : parity_II = SerialPort.ODD_PARITY;
                          break;
               case "2" : parity_II = SerialPort.EVEN_PARITY;
                          break;
               default  : parity_II = 99;                                // non existing (parity) value
                          break;
            } // switch (hulp_parity)            
         }
         
         // RS232 2nd imeteo instrument stop bits
         if (configuratie_regels[teller].indexOf(RS232_STOP_BITS_TXT_II) != -1)
         {
            String hulp_stop_bits = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
            
            switch (hulp_stop_bits)  
            {
               case "1"  : stop_bits_II = SerialPort.ONE_STOP_BIT;
                           break;
               case "2"  : stop_bits_II = SerialPort.TWO_STOP_BITS;
                           break;
               default   : stop_bits_II = 0;                       // non existing (stop bits) value
                           break;
            } // switch (hulp_stop_bits)            
         }      
         
         // RS232 2nd meteo instrument prefered COM port (Windows and Linux)
         if (configuratie_regels[teller].indexOf(RS232_PREFERED_COM_PORT_TXT_II) != -1)
         {
            prefered_COM_port_number_II = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // AP[&T]R / AWSR send method
         if (configuratie_regels[teller].indexOf(APTR_AWSR_SEND_METHOD_TXT) != -1)
         {
            APTR_AWSR_send_method = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
            
            // from version 4.2: SMTP host, Gmail and Yahoo are not possible anymore (only possible 'Server Met. center' and Custom email) (when reading configaration settings from a previous TurboWin+ version)
            if ((APTR_AWSR_send_method.equals(APTR_AWSR_SMTP_HOST)) || (APTR_AWSR_send_method.equals(APTR_AWSR_GMAIL)) || (APTR_AWSR_send_method.equals(APTR_AWSR_YAHOO_MAIL)))
            {
               APTR_AWSR_send_method = "";        
            }
         }   
         
         // station ID
         if (configuratie_regels[teller].indexOf(STATION_ID_TXT) != -1)
         {
            station_ID = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // dashboard background image
         if (configuratie_regels[teller].indexOf(DASHBOARD_BACKGROUND_IMAGE_TXT) != -1)
         {
            dashboard_background_image = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // your custom email address
         if (configuratie_regels[teller].indexOf(YOUR_CUSTOM_ADDRESS_TXT) != -1)
         {
            your_custom_address = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // custom email server
         if (configuratie_regels[teller].indexOf(CUSTOM_EMAIL_SERVER_TXT) != -1)
         {
            custom_email_server = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // custom email password
         if (configuratie_regels[teller].indexOf(CUSTOM_PASSWORD_TXT) != -1)   
         {
            custom_password = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // custom email security
         if (configuratie_regels[teller].indexOf(CUSTOM_SECURITY_TXT) != -1)    
         {
            custom_security = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // custom email port
         if (configuratie_regels[teller].indexOf(CUSTOM_PORT_TXT) != -1)
         {
            custom_port = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // pop-up dashboard? (true/false)
         if (configuratie_regels[teller].indexOf(POP_UP_DASHBOARD_TXT) != -1)
         {
            pop_up_screen = Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));    // pop_up_dashboard = Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));
            // NB You have to be carefull when using Boolean.valueOf(string) or Boolean.parseBoolean(string). 
            //    The reason for this is that the methods will always return false if the String is not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in APR settings
         }           
                 
         // pop-up dashboard interval
         if (configuratie_regels[teller].indexOf(POP_UP_DASHBOARD_INTERVAL_TXT) != -1)
         {
            pop_up_screen_interval = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);          // pop_up_dashboard_interval = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }   
         
         // dashboard ship deck color
         if (configuratie_regels[teller].indexOf(DASHBOARD_SHIP_DECK_COLOR_TXT) != -1)
         {
            ship_deck_color_String = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }    
         
         // custom email module (primary-jakarta, secondary-python)
         if (configuratie_regels[teller].indexOf(CUSTOM_EMAIL_MODULE_TXT) != -1)
         {
            custom_email_module = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }  
         
         // log files email send method (default or custom)
         if (configuratie_regels[teller].indexOf(LOGS_EMAIL_TXT) != -1)
         {
            log_files_email_send_method = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // eucaws upload/send method
         if (configuratie_regels[teller].indexOf(EUCAWS_UPLOADS_METHOD_TXT) != -1)
         {
            eucaws_uploads_method = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // port mode option. deactivate APR/AWSR if ship speed is minimal; (< 1 knot / < 0.5 knot) [true/false]
         if (configuratie_regels[teller].indexOf(PORT_MODE_OPTION_TXT)  != -1)
         {
            port_mode_option = Boolean.valueOf(configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD));    
            // NB You have to be carefull when using Boolean.valueOf(string) or Boolean.parseBoolean(string). 
            //    The reason for this is that the methods will always return false if the String is not equal to "true" (the case is ignored).
            //    For example: Boolean.valueOf("YES") -> false
            //    BUT no problem here because automatically genenerated in APR/AWSR settings
         }
         
         // listening LAN IP address
         if (configuratie_regels[teller].indexOf(LAN_IP_ADDRESS_TXT)  != -1)
         {
            lan_ip_address = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // dashboard ship (LNG)tank color
         if (configuratie_regels[teller].indexOf(DASHBOARD_SHIP_TANK_COLOR_TXT) != -1)
         {
            ship_tank_color_String = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // dashboard font (hybrid dashboard)
         if (configuratie_regels[teller].indexOf(DASHBOARD_FONT_TXT) != -1)
         {
            dashboard_font = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }
         
         // communication protocol client <-> server
         if (configuratie_regels[teller].indexOf(COM_PROTOCOL_TXT) != -1)
         {
            server_com_protocol = configuratie_regels[teller].substring(CONFIGURATION_FILE_POS_INHOUD);
         }  
                 
      } // if ((configuratie_regels[teller] != null) etc.
   } // for (teller = 0; teller < MAX_AANTAL_CONFIGURATIEREGELS; teller++)

   
   // 1st meteo instrument: generic name "prefered_COM_port" will be used [main_RS232_RS422.java]
   //
   if (prefered_COM_port_number.trim().equals(""))                    // So no Windows or Linux com port number selected
   {
      prefered_COM_port = prefered_COM_port_name;                     // OS X
   }
   else
   {
      prefered_COM_port = prefered_COM_port_number;                   // Windows and Linux
   }   
   
   
   // GPS: generic name "prefered_GPS_COM_port" will be used [main_RS232_RS422.java]
   //
   if (prefered_GPS_COM_port_number.trim().equals(""))                // So no Windows or Linux GPS com port number selected
   {
      prefered_GPS_COM_port = prefered_GPS_COM_port_name;             // OS X
   }
   else
   {
      prefered_GPS_COM_port = prefered_GPS_COM_port_number;           // Windows and Linux
   }   
   

   // 2nd meteo instrument: generic name "prefered_COM_port_II" will be used [main_RS232_RS422.java]
   //
   if (prefered_COM_port_number_II.trim().equals(""))                      // So no Windows or Linux com port number selected
   {
      //prefered_COM_port_II = prefered_COM_port_name_II;                     // OS X
   }
   else
   {
      prefered_COM_port_II = prefered_COM_port_number_II;                   // Windows and Linux
   }   
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private static void check_meta_data()
{
   // called from: - read_muffin() [main.java]
   //              - lees_configuratie_regels() [main.java]
   //
   
   String info = "";

   
   // on start up
   if (ship_name.trim().equals("") == true || ship_name.trim().length() < 2)
   {
      info = "Ship name: unknown (select: Maintenance -> Station data)";
   }
   //else if (imo_number.trim().equals("") == true || imo_number.trim().length() < 2)
   //{
   //   info = "IMO number: unknown (select: Maintenance -> Station data)";
   //}
   //else if (call_sign.trim().equals("") == true || call_sign.trim().length() < 2)
   //{
   //   info = "Call sign: unknown (select: Maintenance -> Station data)";
   //}
   
   else if (station_ID.trim().equals("") == true || station_ID.trim().length() < 2)
   {
      info = "station ID: unknown (select: Maintenance -> Station data)";
   }
   
   //else if (time_zone_computer.trim().equals("") == true || time_zone_computer.trim().length() < 2)
   //{
   //   info = "Time zone computer: unknown (select: Maintenance -> Station data)";
   //}
   else if (recruiting_country.trim().equals("") == true || recruiting_country.trim().length() < 2)
   {
      info = "Recruiting country: unknown (select: Maintenance -> Station data)";
   }
   else if (wind_source.trim().equals("") == true || wind_source.trim().length() < 2)
   {
      info = "Wind source (measured/estimated): unknown (select: Maintenance -> Station data)";
   }
   else if (max_height_deck_cargo.trim().equals("") == true || max_height_deck_cargo.trim().length() < 1)
   {
      info = "Maximum height deck cargo: unknown (select: Maintenance -> Station data)";
   }
   else if (diff_sll_wl.trim().equals("") == true || diff_sll_wl.trim().length() < 1)
   {
      info = "Difference SLL and water line: unknown (select: Maintenance -> Station data)";
   }
   else if (pressure_reading_msl_yes_no.trim().equals("") == true || pressure_reading_msl_yes_no.trim().length() < 2)
   {
      info = "Air pressure reading indication: unknown (select: Maintenance -> Station data)";
   }
   else if (barometer_above_sll.trim().equals("") == true || barometer_above_sll.trim().length() < 1)
   {
      info = "Height of the barometer above SLL: unknown (select: Maintenance -> Station data)";
   }
   else if (keel_sll.trim().equals("") == true || keel_sll.trim().length() < 1)
   {
      info = "Distance of bottom of the keel to SLL: unknown (select: Maintenance -> Station data)";
   }
   else if (air_temp_exposure.trim().equals("") == true || air_temp_exposure.trim().length() < 2)
   {
      info = "Air temp exposure: unknown (select: Maintenance -> Station data)";
   }
   else if (sst_exposure.trim().equals("") == true || sst_exposure.trim().length() < 2)
   {
      info = "Sea water temp exposure: unknown (select: Maintenance -> Station data)";
   }
   else if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
   {
      info = "Logs folder unknown (select: Maintenance -> Log files settings)";
   }
   else if (obs_format.trim().equals("") == true || obs_format.trim().length() < 2)
   {
      info = "obs format unknown (select: Maintenance -> Obs format setting)";
   }
   //
   // NB no warning message if obs_email_recipient, obs_email_subject or logs_email_recipient is unknown

   
   // extra check on combination obs format AWS and AWS connected
   if ( (main.obs_format.equals(main.FORMAT_AWS) == true) && (main.RS232_connection_mode != 3 && RS232_connection_mode != 9 && RS232_connection_mode != 10 && RS232_connection_mode != 11))  // RS232_connection_mode = 3 or 9 or 10 or 11 = AWS connected
   {
       info = "if obs format = \"AWS connected\" (see Maintenance -> Obs format setting) then set also the AWS connection (select: Maintenance -> Serial/USB/LAN connection settings)";      
   }
   
   // extra check 'wind speed units estimated/measured' and AWS connected
   //if ((wind_units.indexOf(M_S) == -1) && ((RS232_connection_mode == 3) || (RS232_connection_mode == 9) || (RS232_connection_mode == 10) || RS232_connection_mode == 11)) 
   //{
   //   info = "If AWS connected the \"wind speed units estimated/measured\" will always be \"m/s\"\n";
   //   info += "Please correct this in Maintenance -> Station data";
   //}

   // extra check 'wind source and AWS connected
   //if ((wind_source.equals(main.MEASURED_OFF_BOW) == false) && ((RS232_connection_mode == 3) || (RS232_connection_mode == 9) || (RS232_connection_mode == 10) || RS232_connection_mode == 11)) 
   //{
   //   info = "If AWS connected the \'wind source\" will always be \"measured speed + app. dir. (OFF THE BOW, clockwise)\"\n";
   //   info += "Please correct this in Maintenance -> Station data";
   //}
   
   // extra check 'air pressure station level' and AWS or barometer connected
   if ( (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_YES) == true) &&
        (main.RS232_connection_mode != 0) )            // barometer or AWS connected
   {
      info = "If AWS or barometer connected (Maintenance -> Serial/USB/LAN connection settings): \"the reading does not indicate the MSL pressure\"\n";
      info += "Please correct this in Maintenance -> Station data";    
   }
    
       
   // if info/warning available -> pop-up message
   if (info.compareTo("") != 0)
   {
      JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
   }
}



   /***********************************************************************************************/
   /*                                                                                             */
   /*     new                                                                                        */
   /*                                                                                             */
   /***********************************************************************************************/
/*
   private void Output_Obs_to_server_menu_actionPerformed(java.awt.event.ActionEvent evt) {                                                           

      boolean doorgaan = false;

      // compose coded obs
      String SPATIE = SPATIE_OBS_SERVER;                                     // use "_" as marker between obs groups
      obs_write = compose_coded_obs(SPATIE);

      if (obs_write.compareTo(UNDEFINED) != 0)
      {
         doorgaan = true;
      }
      else
      {
         doorgaan = false;
         //String info = "Call sign, date/time or position not inserted";
         String info = "station ID, date/time or position not inserted";
         JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      } // else

      // check if logs dir was entered
      if (doorgaan == true)
      {
         if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
         {
            doorgaan = false;
            String info = "logs folder unknown, select: Maintenance -> Log files settings and retry";
            JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         }
      } // if (doorgaan == true)

      // check if upload URL was entered (only for format 101)
      if (doorgaan == true)
      {
         if ( (obs_format.equals(FORMAT_101)) && (upload_URL.equals("") || upload_URL == null) )
         {
            doorgaan = false;
            String info = "upload URL unknown, select: Maintenance -> Server settings and retry";
            JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         }
      } // if (doorgaan == true)
      
      // position + time sequence checks
      if (doorgaan == true)
      {
         bepaal_last_record_uit_immt();
         doorgaan = support_class.position_sequence_check();

         if (doorgaan)
         {
            //doorgaan = support_class.Check_Land_Sea_Mask();
            main_support.check_land_sea_mask_ok = true;
            support_class.Check_Land_Sea_Mask();
            if (main_support.check_land_sea_mask_ok == true)
            {
               doorgaan = true;
            }   
            else
            {
               doorgaan = false;
            }
            
            System.out.println("--- Output_Obs_to_server_menu_actionPerformed(); main_support.check_land_sea_mask_ok= " + main_support.check_land_sea_mask_ok);
            
         }
      } // if (doorgaan == true)      

      
      if (doorgaan == true)
      {
         new SwingWorker<Void, Void>()
         {
            @Override
            protected Void doInBackground() throws Exception
            {
               if (obs_format.equals(FORMAT_101))
               {
                  // NB although "obs_write = compose_coded_obs(SPATIE);" (makes FM13 record)  see above is not necessary for the compressed obs 
                  //    it is useful because parts of it will be used for checking position etc.
               
                  format_101_class = new FORMAT_101();
                  format_101_class.compress_and_decompress_101_control_center();
               } // if (obs_format.equals(FORMAT_101)) 
               
               return null;
            } // protected Void doInBackground() throws Exception
            @Override
            protected void done()
            {
               if (obs_format.equals(FORMAT_FM13)) 
               {
                  if (offline_mode == false)                // TurboWeb
                  {
                     //Output_obs_to_server_FM13_TurboWeb();
                     String info = "TurboWeb is disabled";
                     JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
                  }
                  else
                  {
                     Output_obs_to_server_FM13_TurboWin_stand_alone();
                  }
               } 
               else if (obs_format.equals(FORMAT_101))
               {
                  Output_obs_to_server_format_101();
               }
               else
               {
                  String info = "obs format unknown (select: Maintenance -> Obs format setting)";
                  JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
                  System.out.println("+++ Not supported obs format in Function: Output_Obs_to_server_menu_actionPerformed()");
               }  // else    
               
            } // protected void done()
         }.execute(); // new SwingWorker<Void, Void>()  
      } // if (doorgaan == true)       
             
   }                                                          
*/

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
    private void Output_Obs_to_server_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Output_Obs_to_server_menu_actionPerformed

       new SwingWorker<Boolean, Void>()
       {
          @Override
          protected Boolean doInBackground() throws Exception
          {
             boolean doorgaan = false;

             // compose coded obs
             String SPATIE = SPATIE_OBS_SERVER;                                     // use "_" as marker between obs groups
             obs_write = compose_coded_obs(SPATIE);

             if (obs_write.compareTo(UNDEFINED) != 0)
             {
                doorgaan = true;
             }
             else
             {
                doorgaan = false;
                //String info = "Call sign, date/time or position not inserted";
                String info = "station ID, date/time or position not inserted";
                JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
             } // else


             // check if logs dir was entered
             if (doorgaan == true)
             {
                if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
                {
                   doorgaan = false;
                   String info = "logs folder unknown, select: Maintenance -> Log files settings and retry";
                   JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                }
             } // if (doorgaan == true)

             
             // check if upload URL was entered (only for format 101)
             if (doorgaan == true)
             {
                if ( (obs_format.equals(FORMAT_101)) && (upload_URL.equals("") || upload_URL == null) )
                {
                   doorgaan = false;
                   String info = "upload URL unknown, select: Maintenance -> Server settings and retry";
                   JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                }
             } // if (doorgaan == true)
             

             // position + time sequence checks
             if (doorgaan == true)
             {
                bepaal_last_record_uit_immt();
                doorgaan = support_class.position_sequence_check();

                if (doorgaan)
                {
                   doorgaan = support_class.Check_Land_Sea_Mask();
                }
             } // if (doorgaan == true)
             
             
             // if appropriate make a format 101 obs
             if (doorgaan == true)
             {
                if (obs_format.equals(FORMAT_101))
                {
                   // NB although "obs_write = compose_coded_obs(SPATIE);" (makes FM13 record)  see above is not necessary for the compressed obs 
                   //    it is useful because parts of it will be used for checking position etc.
               
                   format_101_class = new FORMAT_101();
                   format_101_class.compress_and_decompress_101_control_center();
                } // if (obs_format.equals(FORMAT_101))
             } // if (doorgaan == true)


             return doorgaan;
               
          } // protected Void doInBackground() throws Exception

          @Override
          protected void done()
          {
             try
             {
                boolean doorgaan = get();
                //if (doorgaan == true)
                //{
                //   Output_Obs_to_server();                    // in this fuction also IMMT_log() (immt log storage)
                //}
                if (doorgaan == true)
                {
                   if (obs_format.equals(FORMAT_FM13))
                   {
                      if (offline_mode == false)                // TurboWeb
                      {
                         //Output_obs_to_server_FM13_TurboWeb();
                         String info = "TurboWeb is disabled";
                         JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE); 
                      }
                      else
                      {
                         Output_obs_to_server_FM13_TurboWin_stand_alone();
                      }
                   }
                   else if (obs_format.equals(FORMAT_101))
                   {
                      Output_obs_to_server_format_101_V2();
                   }
                   else
                   {
                      String info = "obs format unknown (select: Maintenance -> Obs format setting)";
                      JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);                      
                      System.out.println("+++ Not supported obs format in Function: Output_Obs_to_server_menu_actionPerformed()");
                   }  // else    
                } // if (doorgaan == true)  
             } // try
             catch (InterruptedException | ExecutionException ex) 
             {   
                System.out.println("+++ Error in Function: Output_Obs_to_server_menu_actionPerformed(). " + ex); 
             }
             
          } // protected void done()
       }.execute(); // new SwingWorker<Void, Void>()
               
   }//GEN-LAST:event_Output_Obs_to_server_menu_actionPerformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
    private void Input_Wind_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Wind_menu_actionPerformed
      // TODO add your handling code here:
      //if (wind_form == null)
      //{   
      //   wind_form = new mywind();               
      //   wind_form.setSize(800, 600);
      //}
      //wind_form.setVisible(true); 
      
      mywind form = new mywind();               
      form.setSize(800, 600);
      form.setVisible(true);  
    }//GEN-LAST:event_Input_Wind_menu_actionPerformed



   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Cloudcover_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Cloudcover_menu_actionPerformed
// TODO add your handling code here:
      //if (cloudcover_form == null)
      //{   
      //   cloudcover_form = new mycloudcover();               
      //   cloudcover_form.setSize(800, 600);
      //}
      //cloudcover_form.setVisible(true); 
      
      mycloudcover form = new mycloudcover();               
      form.setSize(800, 600);
      form.setVisible(true); 

   }//GEN-LAST:event_Input_Cloudcover_menu_actionPerformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Presentweather_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Presentweather_menu_actionPerformed
// TODO add your handling code here:
      //if (presentweather_form == null)
      //{   
      //   presentweather_form = new mypresentweather();               
      //   presentweather_form.setSize(800, 600);
      //}
      //presentweather_form.setVisible(true); 
      
      mypresentweather form = new mypresentweather();               
      form.setSize(800, 600);
      form.setVisible(true); 

   }//GEN-LAST:event_Input_Presentweather_menu_actionPerformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_waves_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_waves_menu_actionPerformed
      // TODO add your handling code here:
      
      
      mywaves form = new mywaves();               
      form.setSize(800, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Input_waves_menu_actionPerformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void observer_field_update()
   {
      //JOptionPane.showMessageDialog(null, myobserver.selected_observer, main.APPLICATION_NAME + " test", JOptionPane.WARNING_MESSAGE);
      if (myobserver.selected_observer.compareTo("") != 0)
      {
         jTextField20.setText(myobserver.selected_observer);
      }
      else
      {
         jTextField20.setText("");
      }
   }

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void temperatures_fields_update()
   {
      ////// air temp
      // 
      //
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)  // AWS connected mode
      {
         if (main.air_temp_from_AWS_present == true)              // NB was set in function: RS422_Read_AWS_Sensor_Data_For_Display() [file: main_RS232_RS424.java]
         {
            if (displayed_aws_data_obsolate)
            {
               jTextField36.setForeground(obsolate_color_data_from_aws);   // gray
            }
            else
            {   
               jTextField36.setForeground(main.input_color_from_aws);
            }
         }
         else
         {
            //System.out.println("+++ jTextField36.getForeground() = " + jTextField36.getForeground());
            //System.out.println("+++ main.input_color_from_aws = " + main.input_color_from_aws);
            if ((jTextField36.getForeground().getRGB() == main.input_color_from_aws.getRGB()) || (jTextField36.getForeground().getRGB() == main.obsolate_color_data_from_aws.getRGB()))
            {
               // last value was measured by AWS (color was red/gray) but now no new temp in last received aws string -> clear text field
               jTextField36.setText("");
               //System.out.println("+++ kleuren zijn het zelde");
               mytemp.air_temp = "";
            }
            
            jTextField36.setForeground(main.input_color_from_observer);
         } // else
      } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10  || RS232_connection_mode == 11)
      else // not AWS connected
      {
         if (APR == true)
         {
            if ( (main.obsolate_data_flag_II == true) || ((main.obsolate_data_flag == true) && (RS232_connection_mode == 7 || RS232_connection_mode == 8)) ) // NB 7/8 : Mintaka StarX (temp) linked
            {
               jTextField36.setForeground(main.obsolete_input_color_from_apr);   
            }
            else
            {
               jTextField36.setForeground(main.input_color_from_apr);
            }
         } // if (APR == true)  
         else // no APR
         {
            jTextField36.setForeground(main.input_color_from_observer);
         } // else (no APR)         
      }
      
      
      if ((mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null))    
      {
         // het kan zijn dat er bv alleen staat 25 dit moet dan worden 25.0 C 
         int pos = mytemp.air_temp.indexOf(".");
         if (pos == -1)                                                        // dus geen "." in de air temp string
         {
            jTextField36.setText(mytemp.air_temp + ".0" + " \u00B0" + "C");
         }    
         else
         { 
            jTextField36.setText(mytemp.air_temp + " \u00B0" + "C");
         }   
      }  
      else
      {
         jTextField36.setText(""); 
      }

      
      /////// wet bulb temp
      // 
      //
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)    // AWS connected mode
      {
         if (rh_from_AWS_present == true)
         {
            if (displayed_aws_data_obsolate)
            {
               // NB to show "NA" (see below) in color gray
               jTextField37.setForeground(obsolate_color_data_from_aws);   // gray
            }
            else
            {
              // NB to show "NA" (see below) in color red
              jTextField37.setForeground(main.input_color_from_aws);
            }
         }
         else
         {
            jTextField37.setForeground(main.input_color_from_observer);
         }
      }
      else // not AWS connected
      {
         if (APR == true)
         {
            //if (main.obsolate_data_flag_II == true)
            if ( (main.obsolate_data_flag_II == true) || ((main.obsolate_data_flag == true) && (RS232_connection_mode == 7 || RS232_connection_mode == 8)) ) // NB 7/8 : Mintaka StarX (temp) linked
            {
               jTextField37.setForeground(main.obsolete_input_color_from_apr);   
            }
            else
            {
               jTextField37.setForeground(main.input_color_from_apr);
            }
         } // if (APR == true)  
         else // no APR
         {
            jTextField37.setForeground(main.input_color_from_observer);
         } // else (no APR)         
      }
      
      if (rh_from_AWS_present == true)     
      {
         jTextField37.setText("NA");        // Not Applicable
         mytemp.wet_bulb_temp = "";
      }
      else 
      {   
         if ((mytemp.wet_bulb_temp.compareTo("") != 0) && (mytemp.wet_bulb_temp != null))    
         {
            // het kan zijn dat er bv alleen staat 25 dit moet dan worden 25.0 C 
            int pos = mytemp.wet_bulb_temp.indexOf(".");
            if (pos == -1)                                                        // dus geen "." in de air temp string
            {
               jTextField37.setText(mytemp.wet_bulb_temp + ".0" + " \u00B0" + "C");
            }    
            else
            { 
               jTextField37.setText(mytemp.wet_bulb_temp + " \u00B0" + "C");
            }   
         }  
         else
         {   
            jTextField37.setText(""); 
         }   
      }
      
      
      /////// dew point or relative humidity
      //
      // (NB in AWS mode the AWS measured relative humidity (no dew point available) is displayed and must also be send to the AWS if measured manually) 
      //
      //
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)       // AWS connected mode
      {
         // relative humidity (only if in AWS connected mode!)
         //
         if (rh_from_AWS_present == true)
         {
            if (displayed_aws_data_obsolate)
            {
               jTextField38.setForeground(obsolate_color_data_from_aws);   // gray
            }
            else
            {   
               jTextField38.setForeground(main.input_color_from_aws);
            }
         }
         else
         {
            if ((jTextField38.getForeground().getRGB() == main.input_color_from_aws.getRGB()) || (jTextField38.getForeground().getRGB() == main.obsolate_color_data_from_aws.getRGB()))
            {
               // last value was measured by AWS (color was red/gray ) but now no rh in last received aws string -> clear text field
               jTextField38.setText("");
               mytemp.double_rv = INVALID;
            }
            jTextField38.setForeground(main.input_color_from_observer);
         }
         
         // (first a trick for relative humidity rounding to 1 figure behind the decimal)
         double hulp_double_rv = Math.round(mytemp.double_rv * 10 * 100) / 10.0; // nb Math.round(xxx) - > geeft long terug  // NB * 100 for getting %
         if ((mytemp.double_rv != INVALID))    
            jTextField38.setText(Double.toString(hulp_double_rv) + " %");
         else
            jTextField38.setText("");
         
      } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)
      else // not AWS connected
      {   
         // dewpoint (only if in not AWS connected mode!)
         //  
         if (APR == true)
         {
            //if (main.obsolate_data_flag_II == true)
            if ( (main.obsolate_data_flag_II == true) || ((main.obsolate_data_flag == true) && (RS232_connection_mode == 7 || RS232_connection_mode == 8)) ) // NB 7/8 : Mintaka StarX (temp) linked
            {
               jTextField38.setForeground(main.obsolete_input_color_from_apr);   
            }
            else
            {
               jTextField38.setForeground(main.input_color_from_apr);
            }
         } // if (APR == true)  
         else // no APR
         {
            jTextField38.setForeground(main.input_color_from_observer);
         } // else (no APR)         

         
         if ((mytemp.double_dew_point != INVALID)) 
         {
            // (first a trick for dew point rounding to 1 figure behind the decimal)
            double hulp_double_dew_point = Math.round(mytemp.double_dew_point * 10) / 10.0; // nb Math.round(xxx) - > geeft long terug
            jTextField38.setText(Double.toString(hulp_double_dew_point) + " \u00B0" + "C");
         }
         else
         {   
            jTextField38.setText("");
         }
      } // else (not AWS connected)
      
      
      //////// sst temp
      // 
      //
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11) // AWS connected mode
      {
         if (main.SST_from_AWS_present == true)
         {
            if (displayed_aws_data_obsolate)
            {
               jTextField40.setForeground(obsolate_color_data_from_aws);   // gray
            }
            else
            {
               jTextField40.setForeground(main.input_color_from_aws);
            }
         }
         else
         {
            if ((jTextField40.getForeground().getRGB() == main.input_color_from_aws.getRGB()) || (jTextField40.getForeground().getRGB() == main.obsolate_color_data_from_aws.getRGB()))
            {
               // last value was measured by AWS (color was red/gray) but now no sst in last received aws string -> clear text field
               jTextField40.setText("");
               mytemp.sea_water_temp = "";
            }
            jTextField40.setForeground(main.input_color_from_observer);
         }
      } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10  || RS232_connection_mode == 11)
      else // not aws connected
      {
         jTextField40.setForeground(main.input_color_from_observer);
      }
      
      if ((mytemp.sea_water_temp.compareTo("") != 0) && (mytemp.sea_water_temp != null))    
      {
         // het kan zijn dat er bv alleen staat 25 dit moet dan worden 25.0 C 
         int pos = mytemp.sea_water_temp.indexOf(".");
         if (pos == -1)                                                        // dus geen "." in de air temp string
         {
            jTextField40.setText(mytemp.sea_water_temp + ".0" + " \u00B0" + "C"); 
         }    
         else
         { 
            jTextField40.setText(mytemp.sea_water_temp + " \u00B0" + "C");
         }   
      }  
      else
      {
         jTextField40.setText(""); 
      }   
      
      
      // update of the coded obs representation (bottom line main screen) 
      //
      if (APR == false)  // otherwise, in APR mode, the "next automated meteo report upload..." on botton status line will be overwritten everytime
      {
         coded_obs_update();
      }
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void wind_fields_update()
   {
      String field17_part1 = "";       // true wind dir
      String field17_part2 = "";       // true wind speed
      String field16_part1 = "";       // apparent wind dir
      String field16_part2 = "";       // apparent wind speed
     
      
      //////// true wind direction + true wind speed (main screen)
      // 
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)     // AWS connected mode
      {
         if (main.true_wind_dir_from_AWS_present == true || main.true_wind_speed_from_AWS_present == true)
         {
            if (displayed_aws_data_obsolate)
            {
               jTextField17.setForeground(obsolate_color_data_from_aws);   // gray
            }
            else
            {
               jTextField17.setForeground(main.input_color_from_aws);
            }
         }
         else // so: true_wind_dir_from_AWS_present = false AND true_wind_speed_from_AWS_present = false
         {
            if ((jTextField17.getForeground().getRGB() == main.input_color_from_aws.getRGB()) || (jTextField17.getForeground().getRGB() == main.obsolate_color_data_from_aws.getRGB()))
            {
               // last value was measured by AWS (color was red/gray) but now no new true wind dir and no new true wind speed in last received aws string -> clear text field
               jTextField17.setText("");
               field17_part1 = "";
               field17_part2 = "";
               mywind.int_true_wind_dir = INVALID;
               mywind.int_true_wind_speed = INVALID;
            }
            jTextField17.setForeground(main.input_color_from_observer);
         }
      } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11) 
      else
      {
         jTextField17.setForeground(main.input_color_from_observer);
      }
      
/*      
      if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE)
      {
         jTextField17.setText("variable");  
      }   
      else if ((mywind.int_true_wind_dir != INVALID) && (mywind.int_true_wind_dir != mywind.WIND_DIR_VARIABLE))
      {
         jTextField17.setText(Integer.toString(mywind.int_true_wind_dir) + " degr");
      }
      else
      {
         jTextField17.setText(""); 
      }
*/
      // true wind dir
      
      if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE)
      {
         field17_part1 = "var";  
      }   
      else if ((mywind.int_true_wind_dir != INVALID) && (mywind.int_true_wind_dir != mywind.WIND_DIR_VARIABLE) /*&& (main.true_wind_dir_from_AWS_present == true)*/)
      {
         field17_part1 = Integer.toString(mywind.int_true_wind_dir);
      }
      else
      {
         field17_part1 = "-"; 
      }
      
      // true wind speed
      if (mywind.int_true_wind_speed != INVALID /*&& (main.true_wind_speed_from_AWS_present == true)*/)
      {
         field17_part2 = Integer.toString(mywind.int_true_wind_speed);
      }
      else
      {
         field17_part2 = "-";
      }
      
      // true wind dir + true wind speeed 
      if (mywind.int_true_wind_dir != INVALID || mywind.int_true_wind_speed != INVALID)
      {
         if (main.wind_units.trim().indexOf(main.M_S) != -1)  // source wind units (as originally measured), e.g. always m/s in case of AWS
         {
            jTextField17.setText(field17_part1 + " \u00B0" + " / " + field17_part2 + " m/s");
         }
         else // thus if wind speed units knots or wind speed units unknown
         {
            jTextField17.setText(field17_part1 + " \u00B0" + " / " + field17_part2 + " kts");
         }
      }
      else
      {
         jTextField17.setText("");
      }
      

      //////// relative wind direction + relative wind speed (main screen)
      // 
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)     // AWS connected mode
      {
         if (main.relative_wind_dir_from_AWS_present == true || main.relative_wind_speed_from_AWS_present == true)
         {
            if (displayed_aws_data_obsolate)
            {
               jTextField16.setForeground(obsolate_color_data_from_aws);   // gray
            }
            else
            {
               jTextField16.setForeground(main.input_color_from_aws);
            }
         }
         else // so: relative_wind_dir_from_AWS_present = false AND relative_wind_speed_from_AWS_present = false
         {
            if ((jTextField16.getForeground().getRGB() == main.input_color_from_aws.getRGB()) || (jTextField16.getForeground().getRGB() == main.obsolate_color_data_from_aws.getRGB()))
            {
               // last value was measured by AWS (color was red/gray) but now no new relative wind dir and no new relative wind speed in last received aws string -> clear text field
               jTextField16.setText("");
               field16_part1 = "";
               field16_part2 = "";
               mywind.int_relative_wind_dir = INVALID;
               mywind.int_relative_wind_speed = INVALID;
            }
            jTextField16.setForeground(main.input_color_from_observer);
         }
      } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11) 
      else
      {
         jTextField16.setForeground(main.input_color_from_observer);
      }

      // relative wind dir
      if (mywind.int_relative_wind_dir == mywind.WIND_DIR_VARIABLE)
      {
         field16_part1 = "var";  
      }   
      else if ((mywind.int_relative_wind_dir != INVALID) && (mywind.int_relative_wind_dir != mywind.WIND_DIR_VARIABLE) /*&& (main.relative_wind_dir_from_AWS_present == true)*/)
      {
         field16_part1 = Integer.toString(mywind.int_relative_wind_dir);
      }
      else
      {
         field16_part1 = "-"; 
      }
      
      // relative wind speed
      if ((mywind.int_relative_wind_speed != INVALID) /*&& (main.relative_wind_speed_from_AWS_present == true)*/)
      {
         field16_part2 = Integer.toString(mywind.int_relative_wind_speed);
      }
      else
      {
         field16_part2 = "-";
      }
      
      // relative wind dir + relative wind speeed 
      if (mywind.int_relative_wind_dir != INVALID || mywind.int_relative_wind_speed != INVALID)
      {
         if (main.wind_units.trim().indexOf(main.M_S) != -1)       // source wind units (as originally measured), e.g. always m/s in case of AWS with wind sensor
         {
            jTextField16.setText(field16_part1 + " \u00B0" + " / " + field16_part2 + " m/s");
         }
         else // thus if wind speed units knots or wind speed units unknown
         {
            jTextField16.setText(field16_part1 + " \u00B0" + " / " + field16_part2 + " kts"); 
         }
      }
      else
      {
         jTextField16.setText("");
      }
     
      
      // ship ground cource  (not on main screen, only on wind input form)
      //
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)        // AWS connected mode
      {
         if (main.COG_from_AWS_present == false)
         {
            mywind.ship_ground_course = "";
         }
      }
      
      
      // ship ground speed  (not on main screen, only on wind input form)
      //
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)        // AWS connected mode
      {
         if (main.SOG_from_AWS_present == false)
         {
            mywind.ship_ground_speed = "";
         }
      }      
      
      
      // true heading  (not on main screen, only on wind input form)
      //
      if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)        // AWS connected mode
      {
         if (main.true_heading_from_AWS_present == false)
         {
            mywind.ship_heading = "";
         }
      }
      
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }

   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void clouds_low_fields_update()
   {
      if ((mycl.cl_code.compareTo("") != 0) && (mycl.cl_code != null))
         jTextField33.setText(mycl.cl_code + " (code)");
      else
         jTextField33.setText("");
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void clouds_middle_fields_update()
   {
      if ((mycm.cm_code.compareTo("") != 0) && (mycm.cm_code != null))
      {
         /* take only first char (i.c.w. special cases Cm 7a, 7b, 7c -> 7) */
         jTextField34.setText(mycm.cm_code.substring(0, 1) + " (code)");
      }
      else
         jTextField34.setText("");
  
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }
  

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void clouds_high_fields_update()
   {
      if ((mych.ch_code.compareTo("") != 0) && (mych.ch_code != null))
         jTextField35.setText(mych.ch_code + " (code)");
      else
         jTextField35.setText("");
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }
  

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void cloud_cover_fields_update()
   {
      if ((mycloudcover.N.compareTo("") != 0) && (mycloudcover.N != null))
         jTextField30.setText(mycloudcover.N);  
      else
         jTextField30.setText(""); 
      
      if ((mycloudcover.Nh.compareTo("") != 0) && (mycloudcover.Nh != null))
         jTextField31.setText(mycloudcover.Nh);  
      else
         jTextField31.setText(""); 
       
      if ((mycloudcover.h.compareTo("") != 0) && (mycloudcover.h != null))
         jTextField32.setText(mycloudcover.h);  
      else
         jTextField32.setText(""); 
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }       


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void visibility_fields_update()
   {
      // visibility
      //
      if ((myvisibility.VV.compareTo("") != 0) && (myvisibility.VV != null))
         jTextField18.setText(myvisibility.VV);  
      else
         jTextField18.setText("");   
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void waves_fields_update()
   {
      if ((mywaves.wind_waves_period.compareTo("") != 0) && (mywaves.wind_waves_period != null)) // NB null waarde heeft het als waves input pagina nooit geopend is
         jTextField23.setText(mywaves.wind_waves_period + " sec");  
      else
         jTextField23.setText(""); 
          
      if ((mywaves.wind_waves_height.compareTo("") != 0) && (mywaves.wind_waves_height != null))
         jTextField22.setText(mywaves.wind_waves_height + " metres"); 
      else
         jTextField22.setText(""); 

      if (mywaves.swell_1_period.equals("confused"))
         jTextField26.setText(mywaves.swell_1_period);
      else if (mywaves.swell_1_period.equals("no swell"))
         jTextField26.setText(mywaves.swell_1_period);       
      else if ((mywaves.swell_1_period.compareTo("") != 0) && (mywaves.swell_1_period != null))
         jTextField26.setText(mywaves.swell_1_period + " sec");  
      else
         jTextField26.setText("");

      if (mywaves.swell_1_height.equals("confused"))
         jTextField25.setText(mywaves.swell_1_height);
      else if (mywaves.swell_1_height.equals("no swell"))
         jTextField25.setText(mywaves.swell_1_height);
      else if ((mywaves.swell_1_height.compareTo("") != 0) && (mywaves.swell_1_height != null))
         jTextField25.setText(mywaves.swell_1_height + " metres");  
      else
         jTextField25.setText("");
      
      if (mywaves.swell_1_dir.equals("confused"))
         jTextField24.setText(mywaves.swell_1_dir); 
      else if (mywaves.swell_1_dir.equals("no swell"))
         jTextField24.setText(mywaves.swell_1_dir); 
      else if ((mywaves.swell_1_dir.compareTo("") != 0) && (mywaves.swell_1_dir != null))
         jTextField24.setText(mywaves.swell_1_dir + " degr");  
      else
         jTextField24.setText("");

      if ((mywaves.swell_2_period.compareTo("") != 0) && (mywaves.swell_2_period != null))
         jTextField29.setText(mywaves.swell_2_period + " sec"); 
      else
         jTextField29.setText(""); 
      
      if ((mywaves.swell_2_height.compareTo("") != 0) && (mywaves.swell_2_height != null))
         jTextField28.setText(mywaves.swell_2_height + " metres");  
      else
         jTextField28.setText("");
      
      if ((mywaves.swell_2_dir.compareTo("") != 0) && (mywaves.swell_2_dir != null))
         jTextField27.setText(mywaves.swell_2_dir + " degr");  
      else
         jTextField27.setText(""); 
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void ID_fields_update()
   {
      // call sign
      //
      //jTextField1.setText("");
      //if ((call_sign.compareTo("") != 0) && (call_sign != null))
      //{
      //   jTextField1.setText(call_sign);
      //}
      
      // ship name
      //
      jTextField1.setText("");
      if ((ship_name.compareTo("") != 0) && (ship_name != null))
      {
         jTextField1.setText(ship_name);
      }
      
      
      // station ID 
      //
      jTextField2.setText("");
      //if ((masked_call_sign.compareTo("") != 0) && (masked_call_sign != null))    // masked station ID
      //{
      //   jTextField2.setText(masked_call_sign);
      //}
      
      if ((station_ID.compareTo("") != 0) && (station_ID != null))
      {
         jTextField2.setText(station_ID);
      }
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }
   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void date_time_fields_update()
   {
      
      if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)       // AWS connected mode
      {
         if (displayed_aws_data_obsolate)
         {
            jTextField3.setForeground(obsolate_color_data_from_aws);   // gray
         }
         else
         {   
            jTextField3.setForeground(input_color_from_aws);
         }
      }
      else
      {
         jTextField3.setForeground(input_color_from_observer);
      }
      
      
      if ((mydatetime.day.compareTo("") != 0 && mydatetime.month.compareTo("") != 0 && mydatetime.year.compareTo("") != 0 && mydatetime.hour.compareTo("") != 0 && mydatetime.minute.compareTo("") != 0) &&
          (mydatetime.day != null && mydatetime.month != null && mydatetime.year != null && mydatetime.hour != null && mydatetime.minute != null))    
      {   
         jTextField3.setText(mydatetime.day + " " + mydatetime.month + " " + mydatetime.year + "  " + mydatetime.hour + "." + mydatetime.minute + " UTC");
      }   
      else
      {
         jTextField3.setText("");
      } 

      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();

   }  
   
   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void position_fields_update()
   {
      // position
      //
      if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)    // AWS 
      {
         if (displayed_aws_data_obsolate)
         {
            jTextField5.setForeground(obsolate_color_data_from_aws);   // gray
         }
         else
         {
            jTextField5.setForeground(main.input_color_from_aws);
         }
      }
      else // no AWS
      {
         if (APR == true)
         {
            if (main.obsolate_GPS_data_flag == true)
            {
               jTextField5.setForeground(main.obsolete_input_color_from_apr);     
            }
            else
            {
               jTextField5.setForeground(main.input_color_from_apr);
            }
         } // if (APR == true)  
         else // no APR
         {
            jTextField5.setForeground(main.input_color_from_observer);
         } // else (no APR)
      } // else (no AWS)
      
      if ((myposition.latitude_degrees.compareTo("") != 0 && myposition.latitude_minutes.compareTo("") != 0 && myposition.latitude_hemisphere.compareTo("") != 0 &&
           myposition.longitude_degrees.compareTo("") != 0 && myposition.longitude_minutes.compareTo("") != 0 && myposition.longitude_hemisphere.compareTo("") != 0) &&
          (myposition.latitude_degrees != null && myposition.latitude_minutes != null && myposition.latitude_hemisphere != null &&
           myposition.longitude_degrees != null && myposition.longitude_minutes != null && myposition.longitude_hemisphere != null))
      {
         jTextField5.setText(myposition.latitude_degrees + "\u00B0" + " - " + myposition.latitude_minutes + "' " + myposition.latitude_hemisphere.substring(0, 0 + 1) + "  " +
                             myposition.longitude_degrees + "\u00B0" + " - " + myposition.longitude_minutes + "' " + myposition.longitude_hemisphere.substring(0, 0 + 1));
      }
      else
      {
         jTextField5.setText(""); 
      }
      
      
      // course and speed
      //
      if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)        // AWS connected
      {
         if (displayed_aws_data_obsolate)
         {
            jTextField7.setForeground(obsolate_color_data_from_aws);       // gray
         }
         else
         {   
            jTextField7.setForeground(main.input_color_from_aws);
         }
      }
      else // no AWS
      {
         //jTextField7.setForeground(main.input_color_from_observer);
         if (APR == true)
         {
            if (main.obsolate_GPS_data_flag == true)
            {
               jTextField7.setForeground(main.obsolete_input_color_from_apr);     
            }
            else
            {
               jTextField7.setForeground(main.input_color_from_apr);
            }
         } // if (APR == true)  
         else // no APR
         {
            jTextField7.setForeground(main.input_color_from_observer);
         } // else (no APR)         
      }
      
      if ((myposition.course.compareTo("") != 0 && myposition.speed.compareTo("") != 0) && (myposition.course != null && myposition.speed != null))
      {
         jTextField7.setText(myposition.course + "\u00B0" + "  " + myposition.speed + " kts");
      }
      else
      {
         jTextField7.setText("");  
      }
      
      
      /* update of the coded obs representation (bottom line main screen) */
      if (APR == false)  // otherwise, in APR mode, the "next automated meteo report upload..." on botton status line will be overwritten everytime
      {
         coded_obs_update();
      }
   }
   
   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void present_weather_fields_update()
   {
      if ((mypresentweather.present_weather.compareTo("") != 0) && (mypresentweather.present_weather != null))
         jTextField13.setText(mypresentweather.present_weather);
      else
         jTextField13.setText(""); 
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }
   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void past_weather_fields_update()
   {
      //past weather (prim. phenomena) 
      //
      if ((mypastweather.past_weather_1.compareTo("") != 0) && (mypastweather.past_weather_1 != null))
         jTextField14.setText(mypastweather.past_weather_1);
      else
         jTextField14.setText(""); 
      
      // past weather (sec. phenomena)
      //
      if ((mypastweather.past_weather_2.compareTo("") != 0) && (mypastweather.past_weather_2 != null))
         jTextField15.setText(mypastweather.past_weather_2);
      else
         jTextField15.setText(""); 
      
      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }
                    

   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void barometer_fields_update()
   {
      // input text color setting 'air pressure reading' and 'air pressure MSL'
      // 
      if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)     // AWS connected  
      {
         if (displayed_aws_data_obsolate)                               // set in Function: RS422_init_new_aws_data_received_check_timer()[main_RS232_RS422.java]
         {
            jTextField9.setForeground(obsolate_color_data_from_aws);    // gray
            jTextField10.setForeground(obsolate_color_data_from_aws);   // gray
         }
         else
         {
            jTextField9.setForeground(main.input_color_from_aws);
            jTextField10.setForeground(main.input_color_from_aws);
         }
      }
      else // no AWS
      {
         if (APR == true)
         {
            if (main.obsolate_data_flag == true)
            {
               jTextField9.setForeground(main.obsolete_input_color_from_apr);   
               jTextField10.setForeground(main.obsolete_input_color_from_apr); 
            }
            else
            {
               jTextField9.setForeground(main.input_color_from_apr);
               jTextField10.setForeground(main.input_color_from_apr);     
            }
         } // if (APR == true)  
         else // no APR
         {
            jTextField9.setForeground(main.input_color_from_observer);
            jTextField10.setForeground(main.input_color_from_observer);
         } // else (no APR)
      } // else (no AWS)
      
      
      // air pressure reading
      //
      if ( (mybarometer.pressure_reading_corrected.compareTo("") != 0) && (mybarometer.pressure_reading_corrected != null) )
      {
         jTextField9.setText(mybarometer.pressure_reading_corrected + " hPa"); 
      }
      else
      {
         jTextField9.setText("");   
      }
       
      
      // air pressure MSL
      //
      if ( (mybarometer.pressure_msl_corrected.compareTo("") != 0) && (mybarometer.pressure_msl_corrected != null) )
      {
         jTextField10.setText(mybarometer.pressure_msl_corrected + " hPa");
      }
      else
      {
         jTextField10.setText("");   
      }
      
      
      // update of the coded obs representation (bottom line main screen) 
      //
      if (APR == false)  // otherwise, in APR mode, the "next automated meteo report upload..." on botton status line will be overwritten everytime
      {
         coded_obs_update();
      }
   }
        
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void barograph_fields_update()
   {
      // input text color setting 'amount of pressure tendency' and 'characteristic pressure tendency'
      // 
      if (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 10 || RS232_connection_mode == 11)      // AWS connected
      {
         if (displayed_aws_data_obsolate)
         {
            jTextField11.setForeground(obsolate_color_data_from_aws);    // gray
            jTextField12.setForeground(obsolate_color_data_from_aws);    // gray
         }
         else
         {
            jTextField11.setForeground(main.input_color_from_aws);
            jTextField12.setForeground(main.input_color_from_aws);
         }
      }
      else // no AWS
      {
         //jTextField11.setForeground(main.input_color_from_observer);
         //jTextField12.setForeground(main.input_color_from_observer);
         
         if (APR == true)
         {
            if (main.obsolate_data_flag == true)
            {
               jTextField11.setForeground(main.obsolete_input_color_from_apr);   
               jTextField12.setForeground(main.obsolete_input_color_from_apr); 
            }
            else
            {
               jTextField11.setForeground(main.input_color_from_apr);
               jTextField12.setForeground(main.input_color_from_apr);     
            }
         } // if (APR == true)    
         else // NO APR
         {
            jTextField11.setForeground(main.input_color_from_observer);
            jTextField12.setForeground(main.input_color_from_observer);
         } // else (no APR)
      }  // else (no AWS) 
      
      
      // amount of pressure tendency
      //
      if ((mybarograph.pressure_amount_tendency.compareTo("") != 0) && (mybarograph.pressure_amount_tendency != null))
      {
         jTextField11.setText(mybarograph.pressure_amount_tendency + " hPa");  
      }   
      else
      {
         jTextField11.setText("");   
      }
      
       
      // characteristic pressure tendency (a code)
      //
      if ((mybarograph.a_code.compareTo("") != 0) && (mybarograph.a_code != null))
      {
         jTextField12.setText(mybarograph.a_code + " (code)");
      }   
      else
      {
         jTextField12.setText("");   
      }
      
      
      // update of the coded obs representation (bottom line main screen) 
      //
      if (APR == false)  // otherwise, in APR mode, the "next automated meteo report upload..." on botton status line will be overwritten everytime
      {
         coded_obs_update();
      }
   }



   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void icing_fields_update()
   {
      if ( ((myicing.Is_code.compareTo("") != 0)   && (myicing.Is_code != null)) ||
           ((myicing.EsEs_code.compareTo("") != 0) && (myicing.EsEs_code != null)) ||
           ((myicing.Rs_code.compareTo("") != 0)   && (myicing.Rs_code != null)) )
      {
         jTextField21.setText("present");
      }
      else
      {
         jTextField21.setText("");
      }

      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }




   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void ice_fields_update()
   {
      if ( ((myice1.ci_code.compareTo("") != 0) && (myice1.ci_code != null)) ||
           ((myice1.Si_code.compareTo("") != 0) && (myice1.Si_code != null)) ||
           ((myice1.bi_code.compareTo("") != 0) && (myice1.bi_code != null)) ||
           ((myice1.Di_code.compareTo("") != 0) && (myice1.Di_code != null)) ||
           ((myice1.zi_code.compareTo("") != 0) && (myice1.zi_code != null))
         )
      {
         // if all ice parameters "u" (unable to report) -> do not set present
         if ( (myice1.ci_code.trim().compareTo("u") != 0) ||
              (myice1.Si_code.trim().compareTo("u") != 0) ||
              (myice1.bi_code.trim().compareTo("u") != 0) ||
              (myice1.Di_code.trim().compareTo("u") != 0) ||
              (myice1.zi_code.trim().compareTo("u") != 0)
            )
         {
            jTextField19.setText("present");
         }
         else
         {
            jTextField19.setText("");
         }
      }
      else
      {
         jTextField19.setText("");
      }

      /* update of the coded obs representation (bottom line main screen) */
      coded_obs_update();
   }



   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Cloudshigh_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Cloudshigh_menu_actionPerformed
// TODO add your handling code here:
      //if (ch_form == null)
      //{   
      //   ch_form = new mych();               
      //   ch_form.setSize(800, 600);
      //}
      //ch_form.setVisible(true); 
      
      mych form = new mych();               
      form.setSize(800, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Input_Cloudshigh_menu_actionPerformed


   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Position_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Position_menu_actionPerformed
// TODO add your handling code here:
      
      // date time for leaflet Map plot
      leaflet_maps_obs_day   = mydatetime.day;         // for date-time on leaflet map
      leaflet_maps_obs_month = mydatetime.month;       // for date-time on leaflet map
      leaflet_maps_obs_year  = mydatetime.year;        // for date-time on leaflet map
      leaflet_maps_obs_hour  = mydatetime.hour;        // for date-time on leaflet map
      
      // wind dir for leaflet Map plot
      if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE)
      {
         leaflet_maps_obs_wind_dir = "variable";  
      }
      else if ((mywind.int_true_wind_dir != INVALID) && (mywind.int_true_wind_dir != mywind.WIND_DIR_VARIABLE))
      {
         leaflet_maps_obs_wind_dir = Integer.toString(mywind.int_true_wind_dir) + " degr";
      }
      else
      {
         leaflet_maps_obs_wind_dir = "";
      } 
      
      // wind speed for leaflet Maps plot
      if (mywind.int_true_wind_speed != INVALID)
      {
         if (main.wind_units.trim().indexOf(main.M_S) != -1)
         {
            leaflet_maps_obs_wind_speed = Integer.toString(mywind.int_true_wind_speed) + " m/s";
         }
         else // thus if wind speed units knots or wind speed units unknown
         {
            leaflet_maps_obs_wind_speed = Integer.toString(mywind.int_true_wind_speed) + " knots";
         }
      }
      else
      {
         leaflet_maps_obs_wind_speed = "";
      }
      
      // air temp for leaflet Map plot
      if ((mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null))    
      {
         // it is possible that there is only the figures eg 25 -> change to 25.0 C 
         int pos = mytemp.air_temp.indexOf(".");
         if (pos == -1)                                                        // dus geen "." in de air temp string
         {
            leaflet_maps_obs_air_temp = mytemp.air_temp + ".0" + " &#176" + "C"; 
         }    
         else
         { 
            leaflet_maps_obs_air_temp = mytemp.air_temp + " &#176" + "C";
         }   
      }  
      else
      {
         leaflet_maps_obs_air_temp = ""; 
      }
      
      // SST for leaflet Map plot
      if ((mytemp.sea_water_temp.compareTo("") != 0) && (mytemp.sea_water_temp != null))    
      {
         // it is possible that there is only the figures eg 25 -> change to 25.0 C  
         int pos = mytemp.sea_water_temp.indexOf(".");
         if (pos == -1)                                                        // dus geen "." in de air temp string
         {
            leaflet_maps_obs_sst = mytemp.sea_water_temp + ".0" + " &#176" + "C";
         }    
         else
         { 
            leaflet_maps_obs_sst =  mytemp.sea_water_temp + " &#176" + "C";
         }   
      }  
      else
      {
         leaflet_maps_obs_sst = ""; 
      }
      
      // MSl pressure for leaflet Map plot
      if ( (mybarometer.pressure_msl_corrected.compareTo("") != 0) && (mybarometer.pressure_msl_corrected != null) )
      {
         leaflet_maps_obs_msl_pressure = mybarometer.pressure_msl_corrected + " hPa";
      }
      else
      {
         leaflet_maps_obs_msl_pressure = "";   
      }
      

      //if (position_form == null)
      //{   
      //   position_form = new myposition();
      //   position_form.setSize(800, 600);
      //}
      //position_form.setVisible(true); 
      
      myposition form = new myposition();               
      form.setSize(800, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Input_Position_menu_actionPerformed


   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_DateTime_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_DateTime_menu_actionPerformed
// TODO add your handling code here:
      
      // NB in serial connection mose (AWS or barometer connected) after an obs was send all parameters will be set to blank, 
      //    the "date & time obs" will be automatically updated/shown again (in case AWS every minute and in case barometer every 5 minutes) 
      //    but in "no serial connection mode" we have to ask the observer again
      
      if (main.RS232_connection_mode == 0)             // no serial connection (no AWS or barometer connected)
      {
         // ask the observer if this is the correct UTC date and time of the observation (and if yes: set accordingly)
         main.check_and_set_datetime_v2();    // use_system_date_time_for_updating will be set 
      
         if (use_system_date_time_for_updating == false)
         {   
            mydatetime form = new mydatetime();               
            form.setSize(800, 600);
            form.setVisible(true); 
         }
      } // if (main.RS232_connection_mode == 0)
      else
      {
         mydatetime form = new mydatetime();               
         form.setSize(800, 600);
         form.setVisible(true); 
      } // else
      
   }//GEN-LAST:event_Input_DateTime_menu_actionPerformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Visibility_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Visibility_menu_actionPerformed
      // TODO add your handling code here:
      //if (visibility_form == null)
      //{   
      //   visibility_form = new myvisibility();               
      //   visibility_form.setSize(800, 600);
      //}
      //visibility_form.setVisible(true); 
      myvisibility form = new myvisibility();               
      form.setSize(800, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Input_Visibility_menu_actionPerformed

   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void File_Exit_menu_actionPerformd(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_File_Exit_menu_actionPerformd
      // TODO add your handling code here:
      
      main_windowClosing(null);
      
   }//GEN-LAST:event_File_Exit_menu_actionPerformd

   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Pastweather_menu_actionperformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Pastweather_menu_actionperformed
      // TODO add your handling code here:
      //if (pastweather_form == null)
      //{   
      //   pastweather_form = new mypastweather();               
      //   pastweather_form.setSize(800, 600);
      //}
      //pastweather_form.setVisible(true); 
      
      mypastweather form = new mypastweather();               
      form.setSize(800, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Input_Pastweather_menu_actionperformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Cloudslow_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Cloudslow_menu_actionPerformed
       // TODO add your handling code here:
      //if (cl_form == null)
      //{   
      //   cl_form = new mycl();               
      //   cl_form.setSize(800, 600);
      //}
      //cl_form.setVisible(true); 
      
      mycl form = new mycl();               
      form.setSize(800, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Input_Cloudslow_menu_actionPerformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Cloudsmiddle_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Cloudsmiddle_menu_actionPerformed
       // TODO add your handling code here:
      //if (cm_form == null)
      //{   
      //   cm_form = new mycm();               
      //   cm_form.setSize(800, 600);
      //}
      //cm_form.setVisible(true); 
      
      mycm form = new mycm();               
      form.setSize(800, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Input_Cloudsmiddle_menu_actionPerformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Temperatures_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Temperatures_menu_actionPerformed
       // TODO add your handling code here:
      //if (temp_form == null)
      //{   
      //   temp_form = new mytemp();               
      //   temp_form.setSize(800, 600);
      //}
      //temp_form.setVisible(true); 
       
      
      mytemp form = new mytemp();               
      form.setSize(800, 600);
      form.setVisible(true); 
        
   }//GEN-LAST:event_Input_Temperatures_menu_actionPerformed

      
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Maintenance_Stationdata_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_Stationdata_actionPerformed
       // TODO add your handling code here:

      mode = STATION_DATA;

      if (main_support.password_ok) // no password needed, direct to the station data form
      {
         // open station data input page 
         mystationdata form = new mystationdata();
         form.setSize(800, 600);
         form.setVisible(true);   
      }   
      else // via the password input screen to the station data input form
      {
         mypassword form = new mypassword();
         form.setSize(400, 300);
         form.setVisible(true);
      }
   }//GEN-LAST:event_Maintenance_Stationdata_actionPerformed

   

   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Barometer_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Barometer_menu_actionPerformed
       // TODO add your handling code here:
      //if (barometer_form == null)
      //{   
      //   barometer_form = new mybarometer();
      //   barometer_form.setSize(800, 600);
      //}
      //barometer_form.setVisible(true);
      
      mybarometer form = new mybarometer();               
      form.setSize(800, 600);
      form.setVisible(true); 

   }//GEN-LAST:event_Input_Barometer_menu_actionPerformed


   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Input_Barograph_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Barograph_menu_actionPerformed
      // TODO add your handling code here:
      //if (barograph_form == null)
      //{   
      //   barograph_form = new mybarograph();
      //   barograph_form.setSize(800, 600);
      //}
      //barograph_form.setVisible(true); 
      
      mybarograph form = new mybarograph();               
      form.setSize(800, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Input_Barograph_menu_actionPerformed


   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Info_About_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Info_About_menu_actionPerformed
// TODO add your handling code here:
   about form = new about();               
   form.setSize(600, 700);
   form.setVisible(true); 
   
}//GEN-LAST:event_Info_About_menu_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Output_obs_by_email_all_manual()
{          
  
   // called from:
   //    - Output_obs_by_email_default_actionPerformed()
   //    - Output_obs_by_email_local_host_actionPerformed()  <-- deprecated
   //    - Output_obs_by_email_Gmail_actionPerformed()       <-- deprecated
   //    - Output_obs_by_email_Yahoo_actionPerformed()       <-- deprecated
   //    - Output_obs_by_email_Custom_actionPerformed()
   //
   

   new SwingWorker<Boolean, Void>()
   {
      @Override
      protected Boolean doInBackground() throws Exception
      {
         boolean doorgaan = false;
        
         // compose coded obs
         String SPATIE = SPATIE_OBS_VIEW;                                     // use " " as marker between obs groeps
         obs_write = compose_coded_obs(SPATIE);                        // returns UNDEFINED if call sign, position or date/tome not inserted
         
         // check Obs E-mail recipient address was added
         if (obs_email_recipient.compareTo("") != 0)
         {
            doorgaan = true;
         }
         else
         {
            doorgaan = false;
      
            String info = "Email address recipient not inserted (Select: Maintenance -> Email settings)";
            JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         } // else


         // check obs must contain at least station ID, date/time and position
         if (doorgaan == true)
         {
            if (obs_write.compareTo(UNDEFINED) != 0)
            {
               doorgaan = true;
            }
            else
            {
               doorgaan = false;
               //String info = "Call sign, date/time or position not inserted";
               String info = "station ID, date/time or position not inserted";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            } // else
         } // if (doorgaan == true)


         // check log dir known
         if (doorgaan == true)
         {
            if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
            {
               doorgaan = false;
               String info = "logs folder unknown, select: Maintenance -> Log files settings and retry";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         } // if (doorgaan == true)


         // position + time sequence checks
         if (doorgaan == true)
         {
            bepaal_last_record_uit_immt();
            doorgaan = support_class.position_sequence_check();

            if (doorgaan)
            {
               doorgaan = support_class.Check_Land_Sea_Mask();
            }
         } // if (doorgaan == true)

         
         // if appropriate make a format 101 obs
         if (doorgaan == true)
         {
            if (obs_format.equals(FORMAT_101))
            {
               // NB although "obs_write = compose_coded_obs(SPATIE);" (makes FM13 record)  see above is not necessary for the compressed obs 
               //    it is useful because parts of it will be used for checking position etc.
               
               format_101_class = new FORMAT_101();
               format_101_class.compress_and_decompress_101_control_center();
            } // if (obs_format.equals(FORMAT_101))
         } // if (doorgaan == true)
         

         return doorgaan;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         try
         {
            boolean doorgaan = get();
            if (doorgaan == true)
            {
               if (obs_format.equals(FORMAT_FM13))
               {
                  if (email_send_mode.equals(EMAIL_SEND_DEFAULT))
                  {
                     Output_obs_by_email_FM13();                                                         // default email app via desktop
                  }
                  else
                  {
                     boolean manual_send = true;
                     
                     if (custom_email_module.equals(PYTHON_EMAIL))
                     {   
                        Output_obs_by_email_Localhost_Gmail_Yahoo_FM13_format_101(manual_send);           // via python email app
                        
                     }
                     else // primary email module
                     {
                        Output_obs_by_email_jakarta_FM13_format_101(manual_send);                         // via jakarta email module 
                     }
                  }
               }
               else if (obs_format.equals(FORMAT_101))
               {
                  if (email_send_mode.equals(EMAIL_SEND_DEFAULT))
                  {
                     Output_obs_by_email_format_101();                                                    // default email app via desktop
                  }
                  else
                  {
                     boolean manual_send = true;
                     
                     if (custom_email_module.equals(PYTHON_EMAIL))
                     {   
                        Output_obs_by_email_Localhost_Gmail_Yahoo_FM13_format_101(manual_send);           // via python email app
                        
                     }
                     else
                     {
                        Output_obs_by_email_jakarta_FM13_format_101(manual_send);                         // via jakarta email module 
                     }
                  }
               }
               else
               {
                  String info = "obs format unknown (select: Maintenance -> Obs format setting)";
                  JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);                  
                  System.out.println("+++ Not supported obs format in Function: Output_obs_by_email_all_manual()");
               }  // else 
            } // if (doorgaan == true)
         } // try
         catch (InterruptedException | ExecutionException ex) 
         { 
            System.out.println("+++ Error in Function: Output_obs_by_email_all_manual() " + ex);
         }

      } // protected void done()
   }.execute(); // new SwingWorker<Void, Void>()
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Output_obs_to_file_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Output_obs_to_file_actionPerformed
// TODO add your handling code here:

   new SwingWorker<Boolean, Void>()
   {
      @Override
      protected Boolean doInBackground() throws Exception
      {
         boolean doorgaan = false;

         // compose coded obs
         String SPATIE = SPATIE_OBS_VIEW;                                     // marker between obs groups
         obs_write = compose_coded_obs(SPATIE);


         // check call sign and date time entered
         if (obs_write.compareTo(UNDEFINED) != 0)
         {
            doorgaan = true;
         }
         else
         {
            doorgaan = false;
            //String info = "Call sign, date/time or position not inserted";
            String info = "station ID, date/time or position not inserted";
            JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         } // else


         // check log dir known
         if (doorgaan == true)
         {
            if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
            {
               doorgaan = false;
               String info = "logs folder unknown, select: Maintenance -> Log files settings and retry";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         } // if (doorgaan == true)

         // position + time sequence checks
         if (doorgaan == true)
         {
            bepaal_last_record_uit_immt();
            
            doorgaan = support_class.position_sequence_check();

            if (doorgaan)
            {
               doorgaan = support_class.Check_Land_Sea_Mask();
            }
         } // if (doorgaan == true)
         
         
         // if appropriate make a format 101 obs
         if (doorgaan == true)
         {
            if (obs_format.equals(FORMAT_101))
            {
               // NB although "obs_write = compose_coded_obs(SPATIE);" (makes FM13 record)  see above is not necessary for the compressed obs 
               //    it is useful because parts of it will be used for checking position etc.
               
               format_101_class = new FORMAT_101();
               format_101_class.compress_and_decompress_101_control_center();
            } // if (obs_format.equals(FORMAT_101))
         } // if (doorgaan == true)


         return doorgaan;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         try
         {
            boolean doorgaan = get();
            //if (doorgaan == true)
            //{
            //   Output_obs_to_file();
            //}
            if (doorgaan == true)
            {
               if (obs_format.equals(FORMAT_FM13))
               {
                  Output_obs_to_file_FM13();
               }
               else if (obs_format.equals(FORMAT_101))
               {
                  Output_obs_to_file_format_101();
               }
               else
               {
                  String info = "obs format unknown (select: Maintenance -> Obs format setting)";
                  JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);                  
                  System.out.println("+++ Not supported obs format in Function: Output_obs_to_file_actionPerformed()");
               }  // else 
            } // if (doorgaan == true)
         } // try
         catch (InterruptedException | ExecutionException ex) 
         { 
            System.out.println("+++ Error in Function: Output_obs_to_file_actionPerformed(). " + ex);
         }

      } // protected void done()
   }.execute(); // new SwingWorker<Void, Void>()
  
}//GEN-LAST:event_Output_obs_to_file_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Maintenance_Email_settings_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_Email_settings_actionPerformed
// TODO add your handling code here:

   mode = EMAIL_SETTINGS;

   if (main_support.password_ok) // no password needed, direct to the email settings form
   {   
      myemailsettings form = new myemailsettings();
      form.setSize(1000, 700);
      form.setVisible(true);
   }
   else   
   {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
   }
}//GEN-LAST:event_Maintenance_Email_settings_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Maintenance_Log_files_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_Log_files_actionPerformed
// TODO add your handling code here:

   mode = LOG_FILES;

   if (main_support.password_ok) // no password needed, direct to the log files settings form
   {
      mylogfiles form = new mylogfiles();
      form.setSize(800, 600);
      form.setVisible(true);   
   }
   else
   {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
   }
}//GEN-LAST:event_Maintenance_Log_files_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Input_Observer_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Observer_menu_actionPerformed
    // TODO add your handling code here:
   //if (observer_form == null)
   //{   
   //   observer_form = new myobserver();
   //   observer_form.setSize(800, 600);
   //} 
   //observer_form.setVisible(true);
   
   myobserver form = new myobserver();               
   form.setSize(800, 600);
   form.setVisible(true); 
}//GEN-LAST:event_Input_Observer_menu_actionPerformed











/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Maintenance_Move_log_files_to_disk_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_Move_log_files_to_disk_actionPerformed
    // TODO add your handling code here:

   String move_mode_logs = MOVE_TO_DISK;
   String info           = "";
   boolean doorgaan        = true;


   /* are you sure? */
   info = "Uploading log files should be undertaken when it is intended to return the stored log files" +
          " to the National Meteorological Service.\nDo you wish to proceed";

   if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
   {
      doorgaan = true;
   }
   else
   {
      JOptionPane.showMessageDialog(null, "moving log files process cancelled", APPLICATION_NAME + " message", JOptionPane.INFORMATION_MESSAGE);
      doorgaan = false;
   }
   
   
   if (doorgaan == true)
   {   
      support_class.Move_log_files(move_mode_logs);   // return value of Move_log_files not from interest in this function
   }
}//GEN-LAST:event_Maintenance_Move_log_files_to_disk_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Maintenance_Observer_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_Observer_menu_actionPerformed
   // TODO add your handling code here:
   Input_Observer_menu_actionPerformed(evt);
}//GEN-LAST:event_Maintenance_Observer_menu_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Maintenance_Captains_Menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_Captains_Menu_actionPerformed
   // TODO add your handling code here:
   //if (captain_form == null)
   //{   
   //   captain_form = new mycaptain();
   //   captain_form.setSize(800, 600);
   //}
   //captain_form.setVisible(true);
   
   mycaptain form = new mycaptain();               
   form.setSize(800, 600);
   form.setVisible(true); 
}//GEN-LAST:event_Maintenance_Captains_Menu_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Maintenance_Move_log_files_by_email_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_Move_log_files_by_email_actionPerformed
   // TODO add your handling code here:
   boolean doorgaan        = true;
   String info             = "";


   /* are you sure? */
   info = "Uploading log files should be undertaken when it is intended to return the stored log files" +
          " to the National Meteorological Service.\nDo you wish to proceed";

   if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
   {
      doorgaan = true;
   }
   else
   {
      JOptionPane.showMessageDialog(null, "moving log files process cancelled", APPLICATION_NAME + " message", JOptionPane.INFORMATION_MESSAGE);
      doorgaan = false;
   }


   /* logs dir must be set before (e.g. C:/Users/Martin/Downloads/logs) */
   if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
   {
      doorgaan = false;
      info = "Logs folder unknown, select: Maintenance -> Log files settings and retry";
      JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
   }
   
   
   /* check logs email recipient is set */
   if (logs_email_recipient.equals("")) 
   {
      info = "logs email recipient not set\n" +
             "select: Maintenance -> Email settings" ;
      JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE); 
      doorgaan = false;
   }   
   
   
   /* check if there is an immt log source file present (and not empty) */
   if (doorgaan)
   {
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
   }
   
   
   // summary pop-up message in the case of CUSTOM email logs send method */
   if (doorgaan && log_files_email_send_method.equals(LOGS_CUSTOM_EMAIL))
   {
      if (logs_email_recipient.equals("") || your_custom_address.equals("") || custom_security.equals("") || custom_email_server.equals("") || custom_port.equals("")) 
      {
         info = "not all required email parameters set (logs email recipient, your email address, server, port, security[TLS,SSL,STARTTLS])\n" +
                "select: Maintenance -> Email settings" ;
         JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE); 
         doorgaan = false;
      }
      else
      {
         info = "sending meteo logs\n" +
                "\n" +
                "to: " + logs_email_recipient + "\n" +                    // eg "martin.stam@home.nl,martin.stam@knmi.nl";
                "from: " + your_custom_address + "\n" +                    // eg nedlloyd_ebro@nedlloyd.nl
                "subject: " + METEO_LOGS + ship_name + "\n" +   
                "attachement: " + main.ship_name + " " + main.LOGS_ZIP + "\n" +
                "\n" +         
                "Do you wish to proceed";

         if (JOptionPane.showConfirmDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
         {
            doorgaan = true;
         }
         else
         {
            JOptionPane.showMessageDialog(null, "moving log files process cancelled", APPLICATION_NAME + " message", JOptionPane.INFORMATION_MESSAGE);
            doorgaan = false;
         }
      } // else
   } // if (doorgaan && log_files_email_send_method.equals(LOGS_CUSTOM_EMAIL))
   

   if (doorgaan)
   {
      /* check sub dir temp already present, if not -> create */
      temp_logs_dir = logs_dir + java.io.File.separator + "temp";
      final File dirs = new File(temp_logs_dir);

      if (dirs.exists() == false)
      {
         ////* create subdir 'temp' (e.g. C:\Users\Martin\Downloads\logs/temp) */
         ///temp_logs_dir = logs_dir + java.io.File.separator + "temp";

         final boolean success = dirs.mkdirs();
         if (success == false)
         {
            doorgaan = false;
            JOptionPane.showMessageDialog(null, "Could not create " + temp_logs_dir + ", operation cancelled", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         }
         else
         {
            doorgaan = true;
         }
      } // if (temp_logs_dir.trim().equals("") == true || temp_logs_dir.trim().length() < 2)
      else // temp sub dir already present
      {
         // delete all the files in the temp dir (remains of a previous zip action)
         final File file_logs_dir = new File(temp_logs_dir + java.io.File.separator);
         String[] filenames = file_logs_dir.list(); // Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.

         for (int i = 0; i < filenames.length; i++)
         {
            File file_to_be_deleted = new File(temp_logs_dir + java.io.File.separator + filenames[i]);
            if (file_to_be_deleted.delete() == false)
            {
               JOptionPane.showMessageDialog(null, "delete error: " + filenames[i]  + " (Maintenance_Move_log_files_by_email_actionPerformed)"  , APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
            }
         } // for (int i = 0; i < filenames.length; i++)

         doorgaan = true;
      } // else

   } // if (doorgaan)


   /* move log files from logs_dir (e.g. C:\Users\Martin\Downloads\logs) to temp_logs_dir (e.g. C:\Users\Martin\Downloads\logs/temp) */
   if (doorgaan)
   {
      String move_mode_logs = MOVE_TO_EMAIL;
      doorgaan = support_class.Move_log_files(move_mode_logs);  // and zip the moved log files
   } // if (doorgaan)


   // obs and E-mail address OK -> proceed
   if (doorgaan == true)
   {
      new SwingWorker<Integer, Void>()
      {
         @Override
         protected Integer doInBackground() throws Exception
         {
            Integer responseCode = 0;    // 0 = ok
            
            if (log_files_email_send_method.equals(LOGS_CUSTOM_EMAIL))
            {
               // NB use the CUSTOM email method BUT this is only possible for the CUSTOM jakarta module not for the CUSTOM python module
               //    so even if python email module is selected in CUSTOM (Maintenace -> Email settings) jakarta email module will be used!!
               
               if (jakarta_email_class == null)
               {                                
                  jakarta_email_class = new Jakarta_Email();
               }
               
               String smtp_mode             = custom_security;                        // CUSTOM_TLS_STARTTLS / CUSTOM_TLS / CUSTOM_SSL_STARTTLS / CUSTOM_SSL
               String smtp_host_local       = custom_email_server;                    // eg "smtp.mail.special.com";                       
               String smtp_password_local   = custom_password;
               String send_to               = logs_email_recipient;                   // eg "martin.stam@home.nl,martin.stam@knmi.nl";
               String send_from             = your_custom_address;                    // eg nedlloyd_ebro@nedlloyd.nl
               String email_subject         = METEO_LOGS + " " + ship_name;           // fixed                                      
               String email_body            = "see attachment";                       // fixed (logs always as attachment)
               String smtp_port_local       = custom_port;                            // eg 587
               String attachment            = "yes";                                  // fixed (logs always as attachment)

               String smtp_password_local_plain = "";
               if (smtp_password_local.equals("null") == false)
               {
                  // NB smtp_password_local = encrypted -> decrypt it before passing it to the jakarta email module
                  smtp_password_local_plain = myemailsettings.decrypt(smtp_password_local);   
               }
               else
               {
                  // NB so a null value was not encrypted so do also not decrypt (?? mode: EMAIL_SEND_LOCAL_HOST)
                  smtp_password_local_plain = "null";
               }
               
               
               responseCode = jakarta_email_class.send_jakarta_email_logs(smtp_mode, smtp_host_local, smtp_password_local_plain, send_to, send_from, email_subject, email_body, smtp_port_local, attachment);
            }  
            else  // default (on this pc) email logs method
            {
               /*
               // Version 6 of the Java Platform, Standard Edition (Java SE), continues to narrow the gap with
               // new system tray functionality, better  print support for JTable, and now the Desktop API
               //(java.awt.Desktop API).
               //
               // Use the Desktop.isDesktopSupported() method to determine whether the Desktop API is available.
               // On the Solaris Operating System and the Linux platform, this API is dependent on Gnome libraries.
               // If those libraries are unavailable, this method will return false. After determining that the API is
               // supported, that is, the isDesktopSupported() returns true, the application can retrieve a Desktop
               // instance using the static method getDesktop().
               //
               */
               Desktop desktop = null;

               // Before more Desktop API is used, first check
               // whether the API is supported by this particular
               // virtual machine (VM) on this particular host.
               if (Desktop.isDesktopSupported())
               {
                  desktop = Desktop.getDesktop();
                  try
                  {
                     String body_txt = "please attach manually the file: " + temp_logs_dir + java.io.File.separator + ship_name + " " + LOGS_ZIP;
                     String mail_txt = logs_email_recipient + "?subject=" + METEO_LOGS + " " + ship_name + "&body=" + body_txt;

                     URI uriMailTo = null;
                     try
                     {
                        uriMailTo = new URI("mailto", mail_txt, null);
                     }
                     catch (URISyntaxException ex)
                     {
                        //JOptionPane.showMessageDialog(null, "Error invoking default Email program (URISyntaxException)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                        responseCode = 1;
                     }

                     desktop.mail(uriMailTo);

                  } // try
                  catch (IOException ex)
                  {
                     //JOptionPane.showMessageDialog(null, "Error invoking default Email program", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                     responseCode = 2;
                  }
               } // if (Desktop.isDesktopSupported())
               else
               {
                  //JOptionPane.showMessageDialog(null, "Error invoking default Email program (method not supported on this computer system)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  responseCode = 3;
               } // else
            } // else // default (on this pc) email logs method
            
            
            return responseCode;

         } // protected Void doInBackground() throws Exception

         @Override
         protected void done()
         {
            try
            {
               Integer response_code = get();
                
               switch (response_code)
               {
                  // messages if default (on this pc) email logs (e.g. IMMT) send method was used
                  case 1: JOptionPane.showMessageDialog(null, "Error invoking pc-default Email program (URISyntaxException)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                          break;
                  case 2: JOptionPane.showMessageDialog(null, "Error invoking pc-default Email program (IOException)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                          break;
                  case 3: JOptionPane.showMessageDialog(null, "Error invoking pc-default Email program (method not supported on this computer system)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                          break;
                    
                  // messages if CUSTOM email logs (e.g. IMMT) send method was used
                  case 101: JOptionPane.showMessageDialog(null, "logs send failed; no attachment found (for details see: Info -> System logs)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                            break;
                  case 102: JOptionPane.showMessageDialog(null, "logs send failed (for details see: Info -> System logs)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                            break; 
                  case 200: JOptionPane.showMessageDialog(null, "meteo logs successfully sent to " + logs_email_recipient, main.APPLICATION_NAME + " error", JOptionPane.INFORMATION_MESSAGE);
                            break;          
                          
               } // switch   
            } // try
            catch (InterruptedException | ExecutionException ex) 
            {
               main.log_turbowin_system_message("[EMAIL] logs send failed: " + ex);
               JOptionPane.showMessageDialog(null, "Error sending logs via email: " + ex, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         } // protected void done()

      }.execute(); // new SwingWorker<Void, Void>()
   } // if (doorgaan == true)

}//GEN-LAST:event_Maintenance_Move_log_files_by_email_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Next_form_automation_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Next_form_automation_menu_actionPerformed
   // TODO add your handling code here:

   /* initialisation */
   in_next_sequence = true;

   /* starting with the position data input screen */
   Input_Position_menu_actionPerformed(evt);

   //int seq_no_input_screen =5;

   /*
   // sequence_no_input_screen: 1  = CmShipDatetime()
   //                           2  = CmShipPosition()
   //                           3  = CmShipWind()
   //                           4  = CmShipWaves()
   //                           5  = CmShipBarometer()
   //                           6  = CmShipBarograph()
   //                           7  = CmShipTemperatures()
   //                           8  = CmShipPresentWeather()
   //                           9  = CmShipPastWeather()
   //                           10 = CmShipVisibility()
   //                           11 = CmShipCloudslow()
   //                           12 = CmShipCloudsmedium()
   //                           13 = CmShipCloudshigh()
   //                           14 = CmShipCloudsheight()
   //                           15 = CmShipObserver()
   */

   //while (/*stop_in_next_sequence == false &&*/ (seq_no_input_screen <= 15) && (seq_no_input_screen >= 1))
   //{
      //if (seq_no_input_screen == 5)
      //{
       //  Input_Barometer_menu_actionPerformed(evt);
       //  seq_no_input_screen++;
      //}

      //if ((seq_no_input_screen == 6) /*&& (barometer_form_active == false)*/)
      //{
      //   Input_Barograph_menu_actionPerformed(evt);

      //}

      //seq_no_input_screen++;
   //}


}//GEN-LAST:event_Next_form_automation_menu_actionPerformed


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void date_time_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_date_time_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)                          // NOT EUCOS AWS 
   //{
      Input_DateTime_menu_actionPerformed(null);
   //}   
}//GEN-LAST:event_date_time_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void position_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_position_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Position_menu_actionPerformed(null);
   //}
   // NB zoals onderstaande kan het ook
   //   myposition form = new myposition();
   //   form.setSize(800, 600);
   //   form.setVisible(true);
}//GEN-LAST:event_position_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void course_speed_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_course_speed_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Position_menu_actionPerformed(null);
   //}
}//GEN-LAST:event_course_speed_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void pressure_read_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressure_read_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Barometer_menu_actionPerformed(null);
   //}
}//GEN-LAST:event_pressure_read_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void pressure_msl_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressure_msl_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Barometer_menu_actionPerformed(null);
   //}
}//GEN-LAST:event_pressure_msl_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void amount_pressure_tendency_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_amount_pressure_tendency_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Barograph_menu_actionPerformed(null);
   //}
}//GEN-LAST:event_amount_pressure_tendency_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void char_pressure_tendency_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_char_pressure_tendency_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Barograph_menu_actionPerformed(null);
   //}
}//GEN-LAST:event_char_pressure_tendency_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void present_weather_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_present_weather_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   // NB in light mode Label13 (present weather) will be reused for the logo (eumetnet, noaa, sot)
   //    so no action if Label13 was become a label
   //if (!GUI_mode.equals(GUI_LIGHT))
   //{
   //   Input_Presentweather_menu_actionPerformed(null);
   //}
   
   Input_Presentweather_menu_actionPerformed(null);
}//GEN-LAST:event_present_weather_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void past_weather_1_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_past_weather_1_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Pastweather_menu_actionperformed(null);
}//GEN-LAST:event_past_weather_1_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void past_weather_2_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_past_weather_2_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Pastweather_menu_actionperformed(null);
}//GEN-LAST:event_past_weather_2_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void true_wind_speed_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_true_wind_speed_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Wind_menu_actionPerformed(null);
}//GEN-LAST:event_true_wind_speed_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void true_wind_dir_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_true_wind_dir_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Wind_menu_actionPerformed(null);
}//GEN-LAST:event_true_wind_dir_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void visibility_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_visibility_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Visibility_menu_actionPerformed(null);
}//GEN-LAST:event_visibility_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void observer_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_observer_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Observer_menu_actionPerformed(null);
}//GEN-LAST:event_observer_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void wind_wave_height_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wind_wave_height_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_wind_wave_height_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void wind_wave_period_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wind_wave_period_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_wind_wave_period_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void swell_1_dir_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_swell_1_dir_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_swell_1_dir_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void swell_1_height_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_swell_1_height_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_swell_1_height_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void swell_1_period_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_swell_1_period_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_swell_1_period_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void swell_2_dir_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_swell_2_dir_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_swell_2_dir_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void swell_2_height_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_swell_2_height_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_swell_2_height_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void swell_2_period_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_swell_2_period_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_swell_2_period_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void total_cloud_cover_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_total_cloud_cover_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Cloudcover_menu_actionPerformed(null);
}//GEN-LAST:event_total_cloud_cover_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void amount_cl_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_amount_cl_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Cloudcover_menu_actionPerformed(null);
}//GEN-LAST:event_amount_cl_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void height_lowest_cloud_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_height_lowest_cloud_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Cloudcover_menu_actionPerformed(null);
}//GEN-LAST:event_height_lowest_cloud_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void cl_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cl_mainscreen_mouseClicked
   // TODO add your handling code here:
  Input_Cloudslow_menu_actionPerformed(null);
}//GEN-LAST:event_cl_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void cm_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cm_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Cloudsmiddle_menu_actionPerformed(null);
}//GEN-LAST:event_cm_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void ch_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ch_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Cloudshigh_menu_actionPerformed(null);
}//GEN-LAST:event_ch_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void air_temp_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_air_temp_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Temperatures_menu_actionPerformed(null);
}//GEN-LAST:event_air_temp_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void wet_bulb_temp_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wet_bulb_temp_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Temperatures_menu_actionPerformed(null);
}//GEN-LAST:event_wet_bulb_temp_mainscreen_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void dew_point_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dew_point_mainscreen_mouseClicked
   // TODO add your handling code here:
   Input_Temperatures_menu_actionPerformed(null);
}//GEN-LAST:event_dew_point_mainscreen_mouseClicked



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void ship_name_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ship_name_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   Maintenance_Stationdata_actionPerformed(null);
}//GEN-LAST:event_ship_name_mainscreen_mouseClicked



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void station_ID_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_station_ID_mainscreen_mouseClicked
   // TODO add your handling code here:
   
   Maintenance_Stationdata_actionPerformed(null);
}//GEN-LAST:event_station_ID_mainscreen_mouseClicked



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void date_time_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_date_time_toolbar_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)                          // NOT EUCOS AWS 
   //{
      Input_DateTime_menu_actionPerformed(null);
   //}   
}//GEN-LAST:event_date_time_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void position_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_position_toolbar_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Position_menu_actionPerformed(null);
   //}
}//GEN-LAST:event_position_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void wind_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wind_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Wind_menu_actionPerformed(null);
}//GEN-LAST:event_wind_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void waves_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_waves_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_waves_menu_actionPerformed(null);
}//GEN-LAST:event_waves_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void barometer_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barometer_toolbar_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Barometer_menu_actionPerformed(null);
   //}    
}//GEN-LAST:event_barometer_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void barograph_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barograph_toolbar_mouseClicked
   // TODO add your handling code here:
   
   //if (RS232_connection_mode != 3)
   //{
      Input_Barograph_menu_actionPerformed(null);
   //}
}//GEN-LAST:event_barograph_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void temperatures_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_temperatures_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Temperatures_menu_actionPerformed(null);
}//GEN-LAST:event_temperatures_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void present_weather_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_present_weather_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Presentweather_menu_actionPerformed(null);
}//GEN-LAST:event_present_weather_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void past_weather_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_past_weather_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Pastweather_menu_actionperformed(null);
}//GEN-LAST:event_past_weather_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void visibility_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_visibility_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Visibility_menu_actionPerformed(null);
}//GEN-LAST:event_visibility_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void cl_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cl_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Cloudslow_menu_actionPerformed(null);

}//GEN-LAST:event_cl_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void cm_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cm_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Cloudsmiddle_menu_actionPerformed(null);
}//GEN-LAST:event_cm_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void ch_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ch_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Cloudshigh_menu_actionPerformed(null);
}//GEN-LAST:event_ch_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void height_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_height_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Cloudcover_menu_actionPerformed(null);

}//GEN-LAST:event_height_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void icing_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icing_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Icing_menu_actionPerformed(null);
}//GEN-LAST:event_icing_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void ice_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ice_toolbar_mouseClicked
   // TODO add your handling code here:
   Input_Ice_menu_actionPerformed(null);
}//GEN-LAST:event_ice_toolbar_mouseClicked


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void observer_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_observer_toolbar_mouseClicked
   // TODO add your handling code here:

  Input_Observer_menu_actionPerformed(null);
}//GEN-LAST:event_observer_toolbar_mouseClicked





/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Themes_1_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Themes_1_actionPerformed
   // TODO add your handling code here:
   boolean reset_main_class = false;
   
   
   //
   //////// Day colors (Nimbus (vanaf Java 1.6.10) based)
   //
   
      
   if (theme_mode.equals(THEME_TRANSPARENT))
   {
      reset_main_class = true;
      theme_changed = true;                          // for checking more than one instance running
   }
   
   if (reset_main_class)
   {
      mainClass.dispose();
   }
   
   try
   {
      //mainClass.dispose();
      //JFrame.setDefaultLookAndFeelDecorated(true);
      
      //mainClass.setVisible(true);
      //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      //setOpacity(0.7f);
     
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
      {
         if ("Nimbus".equals(info.getName()))
         {
            UIManager.setLookAndFeel(info.getClassName());
            
            // Nimbus default color values: https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html
            
            UIManager.put("control", new Color(214,217,223));                          // Nimbus default
            UIManager.put("nimbusBase", new Color(51,98,140));                         // Nimbus default
            UIManager.put("nimbusFocus", new Color(115,164,209));                      // Nimbus default
            UIManager.put("nimbusLightBackground", new Color(255,255,255));            // Nimbus default
            //UIManager.put("nimbusSelectionBackground", new Color(57,105,138));       // Nimbus default, selected/highlighted text eg to copy, NOT IMPORTANT
            UIManager.put("text", new Color(0,0,0));                                   // Nimbus default
            UIManager.put("nimbusBlueGrey", new Color(169,176,190));                   // Nimbus default

            SwingUtilities.updateComponentTreeUI(main.this);                           // moet komen na setLookAndFeel !!!
            
            jTextField4.setBackground(new java.awt.Color(204, 255, 255));              // status bar (for system messages)
            
            break;
         }
      }

      theme_mode = THEME_NIMBUS_DAY;
   }
   catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
   {
      String info = "Nimbus related Themes not supported on this computer";
      JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
      main.log_turbowin_system_message("[GENERAL] " + info);
   }   
   
   if (reset_main_class)
   {
      //JFrame.setDefaultLookAndFeelDecorated(true);
      mainClass = new main();
      mainClass.setVisible(true);
   }
   

}//GEN-LAST:event_Themes_1_actionPerformed


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Themes_2_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Themes_2_actionPerformed
   // TODO add your handling code here:
   boolean reset_main_class = false;

   // Night colors (Nimbus based)
   //
   
   // JFrame.setDefaultLookAndFeelDecorated(false);
  
  
   if (theme_mode.equals(THEME_TRANSPARENT))
   {
      reset_main_class = true;
      theme_changed = true;                          // for checking more than one instance running
   }
   
   if (reset_main_class)
   {
      mainClass.dispose();
   }
  
   try
   {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
      {
         if ("Nimbus".equals(info.getName()))
         {
            UIManager.setLookAndFeel(info.getClassName());
            
            UIManager.put("control", new Color(114,114,114)); 
            UIManager.put("nimbusBase", new Color(64,64,64)); 
            UIManager.put("nimbusFocus", new Color(191,191,191)); 
            UIManager.put("nimbusLightBackground", new Color(176,176,176)); 
            //UIManager.put("nimbusSelectionBackground", new Color(90,130,195)); 
            UIManager.put("text", new Color(0,0,0));   
            UIManager.put("nimbusBlueGrey", new Color(169,176,190));

            SwingUtilities.updateComponentTreeUI(main.this);                                   // moet komen na setLookAndFeel !!!
            
            jTextField4.setBackground(new java.awt.Color(192, 192, 192));                      // status bar (for system messages)
            
            break;
         }
      }
      theme_mode = THEME_NIMBUS_NIGHT;
   }
   catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
   {
      String info = "Nimbus related Themes not supported on this computer";
      JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
      main.log_turbowin_system_message("[GENERAL] " + info);
   }
  
   if (reset_main_class)
   {
      //JFrame.setDefaultLookAndFeelDecorated(true);
      mainClass = new main();
      mainClass.setVisible(true);
   }
  

}//GEN-LAST:event_Themes_2_actionPerformed




/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Themes_3_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Themes_3_actionPerformed
   // TODO add your handling code here:
   boolean reset_main_class = false;

   //
   //////// Sunrise, Nimbus (vanaf Java 1.6.10) based
   //
   // NB dit is eigenlijk geen Theme maar compleet ander color scheme
   
   if (theme_mode.equals(THEME_TRANSPARENT))
   {
      reset_main_class = true;
      theme_changed = true;                          // for checking more than one instance running
   }
   
   if (reset_main_class)
   {
      mainClass.dispose();
   }
   
   try
   {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
      {
         if ("Nimbus".equals(info.getName()))
         {
            UIManager.setLookAndFeel(info.getClassName());
            
            // eg for the color values see: http://www.rapidtables.com/web/color/RGB_Color.htm
             
            //UIManager.put("control", new Color(214,217,223));                          // panels, frame
            UIManager.put("control", new Color(255,178,102)); 
            
            UIManager.put("nimbusBase", new Color(51,98,140));                           // menu background unfolded items, radio buttons, yes/no buttons
            UIManager.put("nimbusFocus", new Color(115,164,209));                        // border color selected control (text field, button, radio button etc.)
            
            //UIManager.put("nimbusLightBackground", new Color(255,255,255));            // Nimbus default    // controls wit
            UIManager.put("nimbusLightBackground", new Color(255,204,153));              //  controls 
            
            //UIManager.put("nimbusSelectionBackground", new Color(57,105,138));         // Nimbus default    // selected text  ONBELANGRIJK
            UIManager.put("text", new Color(0,0,0));                                     // black texten
            
            UIManager.put("nimbusBlueGrey", new Color(169,176,190));                     // menu bar, text field, button, radio button etc.

            SwingUtilities.updateComponentTreeUI(main.this);                             // moet komen na setLookAndFeel !!!
            
            jTextField4.setBackground(new java.awt.Color(255,255,132));                  // status bar (for system messages)
            //jTextField4.setBackground(new java.awt.Color(255,153,153));
            
            break;
         }
      }
      theme_mode = THEME_NIMBUS_SUNRISE;
   }
   catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
   {
      String info = "Nimbus related Themes not supported on this computer";
      JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
      main.log_turbowin_system_message("[GENERAL] " + info);
   }

   if (reset_main_class)
   {
      //JFrame.setDefaultLookAndFeelDecorated(true);
      mainClass = new main();
      mainClass.setVisible(true);
   }
   
    
   /*
   // http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html
   //
   // Version Note: Do not set the Nimbus look and feel explicitly by invoking the UIManager.setLookAndFeel
   // method because not all versions or implementations of Java SE 6 support Nimbus. Additionally,
   // the location of the Nimbus package changed between the 6u10 and JDK7 releases. Iterating through
   // all installed look and feel implementations is a more robust approach because if Nimbus is not available,
   // the default look and feel is used. For the Java SE 6 Update 10 release, the Nimbus package is
   // located at com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel.
   */

}//GEN-LAST:event_Themes_3_actionPerformed




/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Input_Icing_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Icing_menu_actionPerformed
   // TODO add your handling code here:
   
   myicing form = new myicing();               
   form.setSize(800, 600);
   form.setVisible(true); 
}//GEN-LAST:event_Input_Icing_menu_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void icing_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icing_mainscreen_mouseClicked
   // TODO add your handling code here:

   
   Input_Icing_menu_actionPerformed(null);
}//GEN-LAST:event_icing_mainscreen_mouseClicked



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Input_Ice_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Input_Ice_menu_actionPerformed
   // TODO add your handling code here:
      
   myice1 form = new myice1();               
   form.setSize(800, 600);
   form.setVisible(true); 
}//GEN-LAST:event_Input_Ice_menu_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void ice_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ice_mainscreen_mouseClicked
   // TODO add your handling code here:

   
   Input_Ice_menu_actionPerformed(null);
}//GEN-LAST:event_ice_mainscreen_mouseClicked




/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void captain_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_captain_toolbar_mouseClicked
   // TODO add your handling code here:
   
   Maintenance_Captains_Menu_actionPerformed(null);
}//GEN-LAST:event_captain_toolbar_mouseClicked



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void next_screen_toolbar_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_next_screen_toolbar_mouseClicked
   // TODO add your handling code here:

   /* initialisation */
   in_next_sequence = true;

   /* starting with the position data input screen */
   Input_Position_menu_actionPerformed(null);
 
}//GEN-LAST:event_next_screen_toolbar_mouseClicked





/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Info_Statistics_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Info_Statistics_menu_actionPerformed
   // TODO add your handling code here:
   new SwingWorker<Integer, Void>()
   {
      @Override
      protected Integer doInBackground() throws Exception
      {
         String os = OSDetector.getOSString();

         
         // NB deprecated from October 2024: String link_url = "http://esurfmar.meteo.fr/cgi-bin/meteo/display_vos_ext.cgi?callchx=";
         String link_url = "https://esurfmar.meteo.fr/cgi-bin/display_vos_ext.cgi?callchx=";
         link_url += station_ID;
         
         Integer code = 0;
         Desktop desktop = null;

         if (os.equals("LINUX"))
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
            try 
            {
               // create cmd array
               String[] cmdArray = {"kde-open", link_url};

               // create a process and execute cmdArray
               Process process = Runtime.getRuntime().exec(cmdArray);
            } 
            catch (IOException e) 
            {
               try 
               {
                  // create cmd array
                  String[] cmdArray = {"xdg-open", link_url};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               } 
               catch (IOException e2) 
               {
                  try 
                  {
                     // create cmd array
                     String[] cmdArray = {"open", link_url};

                     // create a process and execute cmdArray
                     Process process = Runtime.getRuntime().exec(cmdArray);
                  } 
                  catch (IOException e3) 
                  {
                     code = -1;
                  } // catch (IOException e3)                 
               } // catch (IOException e2)                 
            } // catch  (IOException e) 
            if (code == -1)
            {
               if (Desktop.isDesktopSupported())
               {
                  desktop = Desktop.getDesktop();
                  URI uri = null;
                  try
                  {
                     String http_adres = link_url;  
                     uri = new URI(http_adres);
                     desktop.browse(uri);
                  }
                  catch (IOException | URISyntaxException ioe) 
                  { 
                     code = -2;
                  }
               } // if (Desktop.isDesktopSupported())
               else
               {
                  code = -1;
               } // else
            } // if (code == -1)  
         } // if (os.equals("LINUX"))
         else // Windows etc.
         {
            // Before more Desktop API is used, first check
            // whether the API is supported by this particular
            // virtual machine (VM) on this particular host.
            if (Desktop.isDesktopSupported())
            {
               desktop = Desktop.getDesktop();
               URI uri = null;
               try
               {
                  String http_adres = link_url;
                  uri = new URI(http_adres);
                  desktop.browse(uri);
               }
               catch(IOException | URISyntaxException ioe) 
               { 
                  code = -2;
               }
            } // if (Desktop.isDesktopSupported())
            else
            {
               code = -1;
            } // else
         } // else (Windows etc.)
         
         
         return code;
      } // protected Void doInBackground() throws Exception
      @Override
      protected void done()
      {
         try
         {
            Integer response_code = get();

            if (response_code == -1)
            {
               String message = "[GENERAL] Error invoking default web browser";
               JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               main.log_turbowin_system_message(message);
            }   
            else if (response_code == -2)
            {
               String message = "[GENERAL] Error invoking URL";
               JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               main.log_turbowin_system_message(message);
            }   
         } // try
         catch (InterruptedException | ExecutionException ex) 
         {   
            String message = "[GENERAL] Error invoking default web browser; " + ex.toString(); 
            main.log_turbowin_system_message(message);
            //main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
         } // catch
      } // protected void done()     
   }.execute(); // new SwingWorker<Void, Void>()


}//GEN-LAST:event_Info_Statistics_menu_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Amver_SailingPlan_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Amver_SailingPlan_actionPerformed
   // TODO add your handling code here:

   if (amver_report.compareTo("") != 0)
   {
      JOptionPane.showMessageDialog(null, "Please close first a previously opened AMVER form", main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
   }
   else
   {
      amver_report = AMVER_SP;              // AMVER sailing plan

      myamversailingplan form = new myamversailingplan();
      form.setSize(1000, 750);
      form.setVisible(true);
   }
}//GEN-LAST:event_Amver_SailingPlan_actionPerformed


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Amver_DeviationReport_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Amver_DeviationReport_actionPerformed
   // TODO add your handling code here:

   if (amver_report.compareTo("") != 0)
   {
      JOptionPane.showMessageDialog(null, "Please close first a previously opened AMVER form", main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
   }
   else
   {
      amver_report = AMVER_DR;             // AMVER deviation report

      myamversailingplan form = new myamversailingplan();
      form.setSize(1000, 700);
      form.setVisible(true);
   }
}//GEN-LAST:event_Amver_DeviationReport_actionPerformed


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Amver_ArrivalReport_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Amver_ArrivalReport_actionPerformed
   // TODO add your handling code here:
   if (amver_report.compareTo("") != 0)
   {
      JOptionPane.showMessageDialog(null, "Please close first a previously opened AMVER form", main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
   }
   else
   {
      amver_report = AMVER_FR;             // AMVER arrival(final) report

      myamversailingplan form = new myamversailingplan();
      form.setSize(1000, 750);
      form.setVisible(true);
   }
}//GEN-LAST:event_Amver_ArrivalReport_actionPerformed


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Amver_PositionReport_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Amver_PositionReport_actionPerformed
   // TODO add your handling code here:
   if (amver_report.compareTo("") != 0)
   {
      JOptionPane.showMessageDialog(null, "Please close first a previously opened AMVER form", main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
   }
   else
   {
      amver_report = AMVER_PR;             // AMVER position report

      myamversailingplan form = new myamversailingplan();
      form.setSize(1000, 700);
      form.setVisible(true);
   }
}//GEN-LAST:event_Amver_PositionReport_actionPerformed



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
    private void Graphs_Pressure_Sensor_Data_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Graphs_Pressure_Sensor_Data_actionPerformed
        // TODO add your handling code here:
       
       if (graph_form != null)
       {
          if (sensor_data_file_ophalen_timer_is_gecreeerd == true)  // 15-05-2013
          {
             if (RS232_view.sensor_data_file_ophalen_timer.isRunning())
             {
                RS232_view.sensor_data_file_ophalen_timer.stop();
             }
          }
          RS232_view.sensor_data_file_ophalen_timer = null;
          sensor_data_file_ophalen_timer_is_gecreeerd = false;
          
          
          if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true)  
          {
             if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning())
             {
                RS232_view.sensor_data_file_ophalen_timer_II.stop();
             }
          }
          RS232_view.sensor_data_file_ophalen_timer_II = null;
          sensor_data_file_ophalen_timer_is_gecreeerd_II = false;
          
          
          //graph_form.dispose();
          graph_form.setVisible(false);
       }
       
       mode_grafiek = MODE_PRESSURE;
       
       graph_form = new RS232_view();
       //graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full screen
       graph_form.setExtendedState(MAXIMIZED_BOTH); 
       graph_form.setVisible(true);  
             
    }//GEN-LAST:event_Graphs_Pressure_Sensor_Data_actionPerformed

    
    
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   private void Maintenance_Serial_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_Serial_actionPerformed
      // TODO add your handling code here:
      
      mode = SERIAL_CONNECTION;
      
      if (main_support.password_ok) // no password needed, direct to the connections settings form
      {
         RS232_settings form = new RS232_settings();
         form.setSize(800, 600);
         form.setVisible(true);   
      }
      else
      {
         mypassword form = new mypassword();
         form.setSize(400, 300);
         form.setVisible(true);
      }   
   }//GEN-LAST:event_Maintenance_Serial_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   private void main_windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_main_windowClosing
      // TODO add your handling code here:
     
      // log memory statistics 
      support_class.log_memory_statistics();
      
      
      String info = "Are you sure you want to exit this application?";
      //if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) || (RS232_connection_mode == 3) || (RS232_connection_mode == 4))  && (defaultPort != null) )
      if ( ((RS232_connection_mode != 0) && (defaultPort != null)) || (RS232_connection_mode == 6) || ((RS232_connection_mode_II != 0) && (defaultPort_II != null)) )   
      {
         // PTB220, PTB330, EUCAWS, OMC-140, MintakaDuo, Mintaka Star USB, HMP155 (all via serial comm) or Mintaka Star WiFi connected
         info += "\n\n (" + APPLICATION_NAME + " will stop with monitoring and collecting of the sensor data)";
         if (main.APR == true || main.WOW == true)
         {
            info += "\n (" + APPLICATION_NAME + " will stop with automated reports upload)";
            info += "\n (minimise " + APPLICATION_NAME + " instead of closing)";
         }
      }   
      int result = JOptionPane.showConfirmDialog(main.this, info, "Exit " + APPLICATION_NAME, JOptionPane.YES_NO_OPTION);

      if (result == JOptionPane.YES_OPTION)
      {
         // Remember to remove the listener (for checking only once instance running) before your application exits (see Function initComponents2()) [nb only in jnlp mode]
         //if (sisL != null)
         //{
         //   sis.removeSingleInstanceListener(sisL);
         //}
         
         // serial communication barometer (not neccessary for WiFi barometer)
         //if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) || (RS232_connection_mode == 3) || (RS232_connection_mode == 4)) && (defaultPort != null) )
         if ((RS232_connection_mode != 0) && (defaultPort != null))  
         {
            //
            // NB RxTx Serial ?
            // close() for Linux for a proper clean up of a port stale lock file (e.g. /var/lock/LCK...ttyUSB0), not appropiate to Windows
            // unfortunately it didn't help (see also: http://www.raspberrypi.org/forums/viewtopic.php?f=81&t=32186)
            //
            // The problem with file lock is likely due to the lock-file being created with root permissions (sudo), 
            // however the IDE is being ran under user mode. Either you will have to make the lock file create in user-mode,
            // change the ownership of the lock, or run the IDE in root. Running it in root shouldn't be a big issue. 
            //
            //
            // NB in case of WiFi: defaultport == null
            //
            //
            if (main.serialPort != null)
            {
               //try 
               //{
                  main.serialPort.removeDataListener();
                  main.serialPort.closePort();
                  main.serialPort = null;
               //} 
               //catch (SerialPortException ex) 
               //{
               //   System.out.println(ex);
               //}
            } // if (main.serialPort != null)
         } // if ((RS232_connection_mode != 0) && (defaultPort != null))
         
          // serial communication thermometer
         if ((RS232_connection_mode_II != 0) && (defaultPort_II != null))  
         {
            //
            // NB in case of WiFi: defaultport_II == null
            //
            if (main.serialPort_II != null)
            {
               main.serialPort_II.removeDataListener();
               main.serialPort_II.closePort();
               main.serialPort_II = null;
            } // if (main.serialPort_II != null)
         } // if ((RS232_connection_mode_II != 0) && (defaultPort_II != null))
         
         // serial communication GPS
         if ((RS232_GPS_connection_mode != 0) && (main_RS232_RS422.GPS_defaultPort != null))
         {
            // only necessary in case of RxTx serial?
            //if (main_RS232_RS422.GPS_serialPort != null)
            if (main.GPS_serialPort != null)   
            {
               //try 
               //{
                  main.GPS_serialPort.removeDataListener();
                  main.GPS_serialPort.closePort();
                  main.GPS_serialPort = null;
               //} 
               //catch (SerialPortException ex) 
               //{
               //   System.out.println(ex);
               //}
            } // if (main_RS232_RS422.GPS_serialPort != null)
         } // if ((RS232_GPS_connection_mode != 0) && (main_RS232_RS422.GPS_defaultPort != null))
         
         // if the program was minimized remove the trayIcon
         if ((main.ICONIFIED & this.getExtendedState()) == main.ICONIFIED)
         {
            tray.remove(trayIcon);
         }
         
         // TurboWin+ stopped message to log
         log_turbowin_system_message("[GENERAL] stopped " + APPLICATION_NAME + " " + application_mode + " " + APPLICATION_VERSION);
         
         // exit
         System.exit(0);     
      }      
   }//GEN-LAST:event_main_windowClosing

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   private void Graphs_Airtemp_Sensor_Data_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Graphs_Airtemp_Sensor_Data_actionPerformed
      // TODO add your handling code here:
      if (graph_form != null)
      {
         if (sensor_data_file_ophalen_timer_is_gecreeerd == true)  // 15-05-2013
         {
            if (RS232_view.sensor_data_file_ophalen_timer.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer = null;
         sensor_data_file_ophalen_timer_is_gecreeerd = false;
         
         
         if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true)  
         {
            if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer_II.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer_II = null;
         sensor_data_file_ophalen_timer_is_gecreeerd_II = false;
          
         //graph_form.dispose();
         graph_form.setVisible(false);
      }  
      
      if (RS232_connection_mode_II == 1)
      {
         mode_grafiek = MODE_AIRTEMP_II;
      }
      else
      {
         mode_grafiek = MODE_AIRTEMP;
      }
       
      
      graph_form = new RS232_view();
      //graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full screen
      graph_form.setExtendedState(MAXIMIZED_BOTH); 
      graph_form.setVisible(true);     
      
   }//GEN-LAST:event_Graphs_Airtemp_Sensor_Data_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   private void Graphs_SST_Sensor_data_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Graphs_SST_Sensor_data_actionPerformed
      // TODO add your handling code here:
      if (graph_form != null)
      {
         if (sensor_data_file_ophalen_timer_is_gecreeerd == true)  // 15-05-2013
         {
            if (RS232_view.sensor_data_file_ophalen_timer.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer = null;
         sensor_data_file_ophalen_timer_is_gecreeerd = false;
         
         
         if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true)  
         {
            if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer_II.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer_II = null;
         sensor_data_file_ophalen_timer_is_gecreeerd_II = false;
         
         
         //graph_form.dispose();
         graph_form.setVisible(false);
      }        
      
      mode_grafiek = MODE_SST;
       
      graph_form = new RS232_view();
      //graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full screen
      graph_form.setExtendedState(MAXIMIZED_BOTH); 
      graph_form.setVisible(true);     
      
   }//GEN-LAST:event_Graphs_SST_Sensor_data_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   private void Graphs_Wind_Speed_Sensor_Data_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Graphs_Wind_Speed_Sensor_Data_actionPerformed
      // TODO add your handling code here:
      if (graph_form != null)
      {
         if (sensor_data_file_ophalen_timer_is_gecreeerd == true)  // 15-05-2013
         {
            if (RS232_view.sensor_data_file_ophalen_timer.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer = null;
         sensor_data_file_ophalen_timer_is_gecreeerd = false;
         
         
         if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true)  
         {
            if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer_II.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer_II = null;
         sensor_data_file_ophalen_timer_is_gecreeerd_II = false;        
          
         //graph_form.dispose();
         graph_form.setVisible(false);
      }        
      mode_grafiek = MODE_WIND_SPEED;
       
      graph_form = new RS232_view();
      //graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full screen
      graph_form.setExtendedState(MAXIMIZED_BOTH); 
      graph_form.setVisible(true);     

   }//GEN-LAST:event_Graphs_Wind_Speed_Sensor_Data_actionPerformed


   

/*
   private void Output_obs_to_AWS_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Output_obs_to_AWS_actionPerformed
      // TODO add your handling code here:
      
   }//GEN-LAST:event_Output_obs_to_AWS_actionPerformed
*/
   

   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   private void Output_obs_to_AWS_actionPerformed(java.awt.event.ActionEvent evt) {      
      // TODO add your handling code here:
      String message_info = "";
      String log_info     = "";
      boolean send_ok     = true;
      
      
      if (main.defaultPort != null)
      {
         String obs_for_AWS_string = compile_obs_for_AWS();
         String log_obs_for_AWS_string = obs_for_AWS_string;  // for logging but still without the newline ("\r\n")
         System.out.println("Writing " + obs_for_AWS_string + " to " + main.defaultPort);
         obs_for_AWS_string += "\r\n";                  // <CR><LF>  required for EUCAWS          

         if (main.serialPort.isOpen())
         {
            //serialPort.writeBytes(obs_for_AWS_string.getBytes());              // Write data to port
            byte[] bytes_message_obs = obs_for_AWS_string.getBytes(StandardCharsets.UTF_8); // Java 7+ only
            if (main.serialPort.writeBytes(bytes_message_obs, bytes_message_obs.length) != -1)      // Write data to port 
            {
               message_info = "success obs sent to AWS";
               log_info = "[AWS] obs sent ok (" + log_obs_for_AWS_string + ") to " + main.defaultPort_descriptive; 
               send_ok = true;
            }
            else
            {
               message_info = "error obs to AWS";
               log_info = "[AWS] Unable to write " + log_obs_for_AWS_string + " to " + main.defaultPort_descriptive;  
               send_ok = false;
            }
         } // if (main.serialPort.isOpen())
         else
         {
            //System.out.println("+++ " + "[AWS] Couldn't open " +  main.serialPort.getDescriptivePortName());
            log_info = "[AWS] Couldn't open " +  main.serialPort.getDescriptivePortName() + " (" + main.defaultPort_descriptive + ")";
            message_info = "error obs to AWS";
            send_ok = false;
         } // else
      } // if (main.defaultPort != null)
      else
      {
         //JOptionPane.showMessageDialog(null, "Failed to send obs to AWS because no serial connection available (defaultPort = null)"  , APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
         log_info = "[AWS] Failed to send obs to AWS because no serial connection available (defaultPort = null)";
         message_info = "error obs to AWS";
         send_ok = false;
      }
      
      
      // show message box "success obs sent to AWS" or "error obs to AWS"
      if (send_ok)
      {
         JOptionPane.showMessageDialog(null, message_info , APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
      }
      else
      {
         JOptionPane.showMessageDialog(null, message_info , APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
      }
      //final JOptionPane pane_end = new JOptionPane(message_info, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
      //final JDialog end_dialog = pane_end.createDialog(APPLICATION_NAME);
      //
      //Timer timer_end = new Timer(1500, new ActionListener()
      //{
      //   @Override
      //   public void actionPerformed(ActionEvent e)
      //   {
      //      end_dialog.dispose();
      //   }
      //});
      //timer_end.setRepeats(false);
      //timer_end.start();
      //end_dialog.setVisible(true);
      
      // logging
      main.log_turbowin_system_message(log_info);  // NB writing also to screen console [System.out.println(log_info)] is part of this function
      
      // on request of Meteo France write the extra MANUAL measured and observed data to IMMT log 
      IMMT_AWS_manual_input_preperations();
      IMMT_log();
      
      // reset alll meteo parameters 
      main_RS232_RS422.RS422_initialise_AWS_Sensor_Data_For_Display(); // must be called before: Reset_all_meteo_parameters();
      Reset_all_meteo_parameters();
      
   }                                                  

   

/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void IMMT_AWS_manual_input_preperations() 
{
   // On request of Meteo France also IMMT storage of extra MANUAL entered parameters in AWS mode (observers are paid for these extra inserted parameters)
   //
   // Convert date/time and position in IMMT code
   // NB manual parameters (eg waves) can be added by observer
   // NB but also parameters by default measured by AWS if not present at that moment then they can be added by observer (pressure excepted)
   // NB     eg if temperature is not part of the incoming data (eg sensor failure) then observer can manually insert the temperature
   // NB Pressure can never be inserted by the observer in AWS EUCAWS mode (even if not measured by the AWS) 
   // 
   
   boolean position_ok = true;
   int len;
   int hulp_month;
  
   
   // month (mydatetime.month is retrieved every minute) (Function: RS422_Read_AWS_Sensor_Data_For_Display() [main_RS232_RS422.java])
   switch (mydatetime.month) 
   {
      case "January":
         hulp_month = 1;
         break;
      case "February":
         hulp_month = 2;
         break;
      case "March":
         hulp_month = 3;
         break;
      case "April":
         hulp_month = 4;
         break;
      case "May":
         hulp_month = 5;
         break;
      case "June":
         hulp_month = 6;
         break;
      case "July":
         hulp_month = 7;
         break;
      case "August":
         hulp_month = 8;
         break;
      case "September":
         hulp_month = 9;
         break;
      case "October":
         hulp_month = 10;
         break;
      case "November":
         hulp_month = 11;
         break;
      case "December":
         hulp_month = 12;
         break;
      default:
         hulp_month = 0;
         break;
   }  // switch
   
   // NB obs date time = AWS +1 hour compared to the AWS date time of incoming data !!
   
   // NB GregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute)
   // NB Constructs a GregorianCalendar with the given date and time set for the default time zone with the default locale.
   // NB month 0..11            // The first month of the year in the Gregorian and Julian calendars is JANUARY which is 0
   // NB dayOfMonth 1..31       // The first day of the month has value 1
   // NB hourOfDay: 0.. 23      // Field number for get and set indicating the hour of the day. HOUR_OF_DAY is used for the 24-hour clock. E.g., at 10:04:15.250 PM the HOUR_OF_DAY is 22.
   
   if (mydatetime.year.equals("") == false && mydatetime.year != null && mydatetime.day.equals("") == false && mydatetime.day != null && mydatetime.hour.equals("") == false && mydatetime.hour != null && hulp_month != 0)
   {
      GregorianCalendar cal_obs_datum_tijd = new GregorianCalendar(Integer.parseInt(mydatetime.year), 
                                                                   hulp_month -1, 
                                                                   Integer.parseInt(mydatetime.day),
                                                                   Integer.parseInt(mydatetime.hour),
                                                                   0
                                                                  );
      cal_obs_datum_tijd.add(Calendar.HOUR_OF_DAY, 1);                  // add 1 hour
      
      SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMddHH");       // NB MM-> start = 1!! (eg January = 01 !!)
      //sdf8.setTimeZone(TimeZone.getTimeZone("UTC"));                  // NOT IN THIS CASE [PROGRAM DO A 'DOUBLE' CONVERSION]
      String obs_date_time = sdf8.format(cal_obs_datum_tijd.getTime()); // eg 2017000308
      
      //System.out.println("+++ " + "mydatetime.hour = " + mydatetime.hour); 
      //System.out.println("+++ " + "obs_date_time = " + obs_date_time); 
      
      mydatetime.year    = obs_date_time.substring(0, 4);               // eg 2017          
      
      //hulp_int_month = Integer.parseInt(obs_date_time.substring(4, 6)) + 1;         // NB if January : hulp_int_month = 0 -> convert to -> mydatetime.MM_code = "01"
      //mydatetime.MM_code = String.valueOf(hulp_int_month);              // eg "1"
      //len = 2;                                                                          // MM_code always 2 characters width e.g. 02
      //if (mydatetime.MM_code.length() < len)                                            // pad on left with zeros
      //{   
      //  mydatetime.MM_code = "0000000000".substring(0, len - mydatetime.MM_code.length()) + mydatetime.MM_code;
      //}
      mydatetime.MM_code = obs_date_time.substring(4, 6);
      

      mydatetime.YY_code = obs_date_time.substring(6, 8);               // eg 03       // day of obs
      mydatetime.GG_code = obs_date_time.substring(8, 10);              // eg 08       // hour of obs
   }
   else
   {
      mydatetime.year    = "    ";               // 4x space           
      mydatetime.MM_code = "  ";                 // 2x space 
      mydatetime.YY_code = "  ";                 // 2x space // day of obs
      mydatetime.GG_code = "  ";                 // 2x space // hour of obs
   } // else

   //System.out.println("+++ " + "mydatetime.hour = " + mydatetime.hour); 
   //System.out.println("+++ " + "mydatetime.GG_code = " + mydatetime.GG_code); 
   // 
   //System.out.println("+++ " + "hulp_int_month = " + hulp_int_month); 
   //System.out.println("+++ " + "mydatetime.MM_code = " + mydatetime.MM_code); 
    
   
   
   //
   ////////////// position //////////////
   //
   // NB retrieved every minute. Function: RS422_Read_AWS_Sensor_Data_For_Display() [main_RS232_RS422.java]:
   //    - myposition.latitude_degrees
   //    - myposition.latitude_minutes
   //    - myposition.longitude_degrees
   //    - myposition.longitude_minutes
   
   // String latitude convert to integer
   try 
   {
      myposition.int_latitude_degrees = Integer.parseInt(myposition.latitude_degrees.trim());
   }
   catch (NumberFormatException e)
   {
      position_ok = false;
   }
      
   try 
   {
      myposition.int_latitude_minutes = Integer.parseInt(myposition.latitude_minutes.trim());
   }
   catch (NumberFormatException e)
   {
      position_ok = false;
   }
      
   // String longitude omzetten naar integer
   try 
   {
      myposition.int_longitude_degrees = Integer.parseInt(myposition.longitude_degrees.trim());
   }
   catch (NumberFormatException e)
   {
      position_ok = false;
   }
      
   try 
   {
      myposition.int_longitude_minutes = Integer.parseInt(myposition.longitude_minutes.trim());
   }
   catch (NumberFormatException e)
   {
      position_ok = false;
   }   
   
   
   if (position_ok)
   {
      // determine code figure quadrant of the globe
      //
      if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true) && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true))
      {
         myposition.Qc_code = "1";
      }
      else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true) && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true))
      {
         myposition.Qc_code = "3";
      }
      else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true) && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true))
      {
         myposition.Qc_code = "5";
      }
      else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true) && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true))
      {
         myposition.Qc_code = "7";
      }
      else
      {
         //JOptionPane.showMessageDialog(null, "internal error: Qc_code", main.APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE); // bug check message
         myposition.Qc_code = " ";
      }

      // determine code figures (latitude)
      //
      int int_latitude_minutes_6 = myposition.int_latitude_minutes / 6;               // devide the minutes by six and disregard the remainder
      String latitude_minutes_6 = Integer.toString(int_latitude_minutes_6);           // convert to String 
         
      if (latitude_minutes_6.length() != 1)                                           // debug check
      {
         //JOptionPane.showMessageDialog(null, "internal error: latitude_minutes_6",  main.APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
         position_ok = false;
      }

      myposition.lalala_code = myposition.latitude_degrees.trim().replaceFirst("^0+(?!$)", "") + latitude_minutes_6;

      len = 3;                                                                    // always 3 chars
      if (myposition.lalala_code.length() < len) 
      {
         // pad on left with zeros
         myposition.lalala_code = "0000000000".substring(0, len - myposition.lalala_code.length()) + myposition.lalala_code;
      }
         
      // determine code figures (longitude)
      //
      int int_longitude_minutes_6 = myposition.int_longitude_minutes / 6;               // devide the minutes by six and disregard the remainder
      String longitude_minutes_6 = Integer.toString(int_longitude_minutes_6);           // convert to String 
         
      if (longitude_minutes_6.length() != 1)                                            // debug check
      {
         //JOptionPane.showMessageDialog(null, "internal error: longitude_minutes_6",  main.APPLICATION_NAME + " error", JOptionPane.ERROR_MESSAGE);
         position_ok = false;
      }

      myposition.lolololo_code = myposition.longitude_degrees.trim().replaceFirst("^0+(?!$)", "") + longitude_minutes_6;
         
      len = 4;                                                                          // always 4 chars
      if (myposition.lolololo_code.length() < len)                                      // pad on left with zeros
      {
         myposition.lolololo_code = "0000000000".substring(0, len - myposition.lolololo_code.length()) + myposition.lolololo_code;
      }
   } // if (position_ok)

   
   if (position_ok == false)
   {
      myposition.Qc_code       = " ";       // 1x space
      myposition.lalala_code   = "   ";     // 3x space 
      myposition.lolololo_code = "    ";    // 4x space 
   }
}   
   

   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   private void Graph_Wind_Dir_Sensor_Data_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Graph_Wind_Dir_Sensor_Data_actionPerformed
      // TODO add your handling code here:
      if (graph_form != null)
      {
         if (sensor_data_file_ophalen_timer_is_gecreeerd == true)  
         {
            if (RS232_view.sensor_data_file_ophalen_timer.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer = null;
         sensor_data_file_ophalen_timer_is_gecreeerd = false;
         
         
         if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true)  
         {
            if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer_II.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer_II = null;
         sensor_data_file_ophalen_timer_is_gecreeerd_II = false;
          
         //graph_form.dispose();
         graph_form.setVisible(false);
      }        
      mode_grafiek = MODE_WIND_DIR;
       
      graph_form = new RS232_view();
      //graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full screen
      graph_form.setExtendedState(MAXIMIZED_BOTH); 
      graph_form.setVisible(true);           
   }//GEN-LAST:event_Graph_Wind_Dir_Sensor_Data_actionPerformed


   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   private void Output_obs_to_clipboard_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Output_obs_to_clipboard_actionPerformed
      // TODO add your handling code here:
      
      new SwingWorker<Boolean, Void>() 
      {
         @Override
         protected Boolean doInBackground() throws Exception
         {
            boolean doorgaan = false;

            // compose coded obs
            String SPATIE = SPATIE_OBS_VIEW;                                     // marker between obs groups
            obs_write = compose_coded_obs(SPATIE);


            // check call sign and date time entered (level 1b checks)
            if (obs_write.compareTo(UNDEFINED) != 0)
            {
               doorgaan = true;
            }
            else
            {
               doorgaan = false;
               //String info = "Call sign, date/time or position not inserted";
               String info = "station ID, date/time or position not inserted";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            } // else
            
            
            // level-2 checks
            //
            //
            if (doorgaan == true)
            {
               doorgaan = support_class.checking_level_2() == true;
            }
            
            
            // level-3 checks
            //
            //
            if (doorgaan == true)
            {
               doorgaan = support_class.checking_level_3() == true;
            }
            
            
            // check log dir known
            if (doorgaan == true)
            {
               if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
               {
                  doorgaan = false;
                  String info = "logs folder unknown, select: Maintenance -> Log files settings and retry";
                  JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               }
            } // if (doorgaan == true)


            // position + time sequence checks
            if (doorgaan == true)
            {
               bepaal_last_record_uit_immt();
               doorgaan = support_class.position_sequence_check();

               if (doorgaan)
               {
                  doorgaan = support_class.Check_Land_Sea_Mask();
               }
            } // if (doorgaan == true)
         
         
            // if appropriate make a format 101 obs
            if (doorgaan == true)
            {
               if (obs_format.equals(FORMAT_101))
               {
                  // NB although "obs_write = compose_coded_obs(SPATIE);" (makes FM13 record)  see above is not necessary for the compressed obs 
                  //    it is useful because parts of it will be used for checking position etc.
               
                  format_101_class = new FORMAT_101();
                  format_101_class.compress_and_decompress_101_control_center();
               } // if (obs_format.equals(FORMAT_101))
            } // if (doorgaan == true)


            return doorgaan;

         } // protected Void doInBackground() throws Exception

         @Override
         protected void done()
         {
            try
            {
               boolean doorgaan = get();
               //if (doorgaan == true)
               //{
               //   Output_obs_to_clipboard();
               //}
               if (doorgaan == true)
               {
                  if (obs_format.equals(FORMAT_FM13))
                  {
                     Output_obs_to_clipboard_FM13();
                  }
                  else if (obs_format.equals(FORMAT_101))
                  {
                     Output_obs_to_clipboard_format_101();
                  }
                  else
                  {
                     String info = "obs format unknown (select: Maintenance -> Obs format setting)";
                     JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
                     System.out.println("+++ Not supported obs format in Function: Output_obs_to_clipboard_actionPerformed()");
                  }  // else 
               } // if (doorgaan == true)
            } // try
            catch (InterruptedException | ExecutionException ex) 
            { 
               System.out.println("+++ Error in Function: Output_obs_to_clipboard_actionPerformed(). " + ex);
            }

         } // protected void done()
      }.execute(); // new SwingWorker<Void, Void>()
  

   }//GEN-LAST:event_Output_obs_to_clipboard_actionPerformed

   

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
   private void main_windowIconfied(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_main_windowIconfied
      // TODO add your handling code here:
      
      // NB on the latest Linux desktops (GNOME, Cinnamon, Xubuntu) issues with iconifying
      //    looks like the evt event WINDOW_ICONIFIED is send several times
      //
      boolean use_system_tray = false;

      OSDetector.OSType ostype = OSDetector.detect_OS();
      switch (ostype)
      {
         case WINDOWS: use_system_tray = true;
                       break;
         default:      use_system_tray = false;   
                       break;
      }
      
      
      if (use_system_tray)
      {
         //Check the SystemTray is supported
         if (!SystemTray.isSupported()) 
         {
            System.out.println("+++ SystemTray is not supported");
         }
         else
         {
            setVisible(false);

            final PopupMenu popup = new PopupMenu();
            trayIcon = new TrayIcon(createImage(main.ICONS_DIRECTORY + "tray.png"));
            //final SystemTray tray = SystemTray.getSystemTray();
            tray = SystemTray.getSystemTray();
            

            // set tool tip text
            trayIcon.setToolTip("TurboWin+ weather observations");


            // message pop-up (left mouse click on TurboWin+ system tray icon)
            //     NB The only way to display a "dynamic" tooltip that pops up when the mouse cursor hovers over the tray icon
            // 
            MouseListener ml = new MouseListener() 
            {
               @Override
               public void mouseClicked(MouseEvent e) 
               {
                  //// test begin ////
                  //trayIcon.displayMessage("TurboWin+", "test", MessageType.INFO);
                  /////// test end ////

                  //if (e.getButton() == MouseEvent.BUTTON1)           // left mouse button
                  if (SwingUtilities.isLeftMouseButton(e))             // to be sure this is better than testing on BUTTON1
                  {
                     /// test begin
                     //JOptionPane.showMessageDialog(null, "test", main.APPLICATION_NAME + " test left mouse button clicked", JOptionPane.INFORMATION_MESSAGE);
                     //trayIcon.displayMessage("TurboWin+", "test left mouse button clicked", MessageType.INFO);
                     /// test end
                     
                     
                     String info = "no sensor data available";        // eg just after start-up (takes 1 minute to start collecting) 

                     if (RS232_connection_mode == 4)                  // Mintaka Duo
                     {
                        // check if we receive valid data (parameter day is arbitrary, could eg also be month etc.)
                        try
                        {
                           // NB tray icon message will be set in: main_RS232_RS422.RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs()
                           boolean local_tray_icon_clicked = true;
                           RS232_mintaka.RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs(local_tray_icon_clicked); // retrieve mybarometer.pressure_reading
                        } // try
                        catch (NumberFormatException en)
                        {
                           info = "no sensor data available";   // eg just after start-up (takes 1 minute to start collecting) 
                        } // catch       
                     } // if (RS232_connection_mode == 4)

                     else if (RS232_connection_mode == 5 || RS232_connection_mode == 6)       // Mintaka Star USB or Mintaka Star WiFi
                     {
                        // check if we receive valid data (parameter day is arbitrary, could eg also be month etc.)
                        try
                        {
                           // NB tray icon message will be set in: RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs()
                           boolean local_tray_icon_clicked = true;
                           boolean StarX = false;
                           RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(local_tray_icon_clicked, StarX); // retrieve mybarometer.pressure_reading
                        } // try
                        catch (NumberFormatException en)
                        {
                           info = "no sensor data available";   // eg just after start-up (takes 1 minute to start collecting) 
                        } // catch       
                     } // else if (RS232_connection_mode == 5 || RS232_connection_mode == 6)  
                     else if (RS232_connection_mode == 7 || RS232_connection_mode == 8)       // Mintaka StarX USB or Mintaka StarX WiFi
                     {
                        // check if we receive valid data (parameter day is arbitrary, could eg also be month etc.)
                        try
                        {
                           // NB tray icon message will be set in: main_RS232_RS422.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs()
                           boolean local_tray_icon_clicked = true;
                           boolean StarX = true;
                           RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(local_tray_icon_clicked, StarX); // retrieve mybarometer.pressure_reading
                        } // try
                        catch (NumberFormatException en)
                        {
                           info = "no sensor data available";   // eg just after start-up (takes 1 minute to start collecting) 
                        } // catch       
                     } // else if (RS232_connection_mode == 7 || RS232_connection_mode == 8)      

                     else if ( (RS232_connection_mode == 1) || (RS232_connection_mode == 2) ) // PTB220 or PTB330
                     {
                        // check if we receive valid data (parameter day is arbitrary, could eg also be month etc.)
                        try
                        {
                           // NB tray icon message will be set in: RS232_Read_Sensor_Data_PPPP_For_Obs()
                           boolean local_tray_icon_clicked = true;
                           RS232_vaisala.RS232_Read_Sensor_Data_PPPP_For_Obs(local_tray_icon_clicked); // retrieve mybarometer.pressure_reading
                        } // try
                        catch (NumberFormatException en)
                        {
                           info = "no sensor data available";   // eg just after start-up (takes 1 minute to start collecting) 
                        } // catch       
                     } // if ( (RS232_connection_mode == 1) || (RS232_connection_mode == 2) )

                     else if ( ((RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 11) && (defaultPort != null)) || (RS232_connection_mode == 10) )    // AWS (serial or LAB)
                     {
                        // check if we receive valid data (parameter day is arbitrary, could eg also be month etc.)
                        try
                        {
                           int int_day = Integer.parseInt(mydatetime.day);

                           if ( (int_day >= 1) && (int_day <= 31) )
                           {   
                              //String wind_speed_units = "";
                              //
                              //if (main.wind_units.indexOf(main.M_S) == -1)  // so knots
                              //{
                              //   wind_speed_units = "kts";
                              //}   
                              //else
                              //{
                              //   wind_speed_units = "m/s";
                              //}   

                              info = "";
                              info = mydatetime.day + " " + mydatetime.month + " " + mydatetime.year + "  " + mydatetime.hour + "." + mydatetime.minute + " UTC" + "\n" +
                                     "\n" +
                                     "pressure at sensor height: " + mybarometer.pressure_reading + " hPa" + "\n" +
                                     "pressure at MSL: " + mybarometer.pressure_msl_corrected + " hPa"; // + "\n"; +
                                    // commented out because will not be visible due to the available pop-up space
                                    // "\n" +
                                    // "air temp at sensor height: " + mytemp.air_temp + " C" + "\n" +
                                    // "SST at sensor depth: " + mytemp.sea_water_temp + " C" + "\n" +
                                    // "\n" +
                                    // "true wind speed at sensor height: " + mywind.int_true_wind_speed + " " + wind_speed_units + "\n" +
                                    // "true wind dir at sensor height: " + mywind.int_true_wind_dir + " degr";
                           } // if ( (int_day >= 1) && (int_day <= 31) )

                           //trayIcon.displayMessage("TurboWin+", info, MessageType.INFO);
                           JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);

                        } // try
                        catch (NumberFormatException en)
                        {
                           info = "no sensor data available";   // eg just after start-up (takes 1 minute to start collecting) 
                           //trayIcon.displayMessage("TurboWin+", info, MessageType.INFO);
                           JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
                        } // catch       

                     } // else if ( (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 11) && (defaultPort != null) ) 
                     else // TurboWin+ stand-alone mode (no serial connection to barometer or AWS and no WiFi)
                     {
                        info = "right click icon to maximize or exit TurboWin+";
                        //trayIcon.displayMessage("TurboWin+", info, MessageType.INFO);
                        JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
                     } // else 

                  } // if (e.getButton() == MouseEvent.BUTTON1) 
               } // public void mouseClicked(MouseEvent e) 

               @Override
               public void mousePressed(MouseEvent e) {
                  //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
               }

               @Override
               public void mouseReleased(MouseEvent e) {
                  //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
               }

               @Override
               public void mouseEntered(MouseEvent e) {
                  //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
               }

               @Override
               public void mouseExited(MouseEvent e) {
                  //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
               }
            }; // MouseListener ml = new MouseListener()
            trayIcon.addMouseListener(ml); 


            // Create a pop-up menu components
            //

            if ( ((RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 11) && (defaultPort != null)) || (RS232_connection_mode == 10) )    // AWS connected
            {
               MenuItem dashboard_analog_Item = new MenuItem("dashboard analog");
               popup.add(dashboard_analog_Item);

               dashboard_analog_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Dashboard_AWS_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });        

               MenuItem dashboard_digital_Item = new MenuItem("dashboard digital");
               popup.add(dashboard_digital_Item);

               dashboard_digital_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Dashboard_AWS_digital_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });        

               MenuItem dashboard_hybrid_Item = new MenuItem("dashboard hybrid");
               popup.add(dashboard_hybrid_Item);

               dashboard_hybrid_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Dashboard_AWS_hybrid_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });     

               MenuItem dashboard_radar_Item = new MenuItem("dashboard wind radar");
               popup.add(dashboard_radar_Item);

               dashboard_radar_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Dashboard_AWS_radar_actionPerformed(null);  
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
               pressure_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_Pressure_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });            

               // wind dir graph
               //
               wind_dir_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graph_Wind_Dir_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });

               // wind speed graph
               //
               wind_speed_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_Wind_Speed_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });

               // SST graph
               //
               sst_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_SST_Sensor_data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });

               // air temp graph
               //
               air_temp_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_Airtemp_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });

               // total graph (pressure, air temp, wind dir and wind speed in one total graph)
               //
               total_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graph_All_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });

            } // if ( (RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode == 11) && (defaultPort != null) )  


            if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) || (RS232_connection_mode == 4) || (RS232_connection_mode == 5)) && (defaultPort != null) ) // PTB220 or PTB330 or Mintaka Duo or Mintaka Star USB
            {
               MenuItem dashboard_Item = new MenuItem("dashboard barometer");
               popup.add(dashboard_Item);

               popup.addSeparator();

               dashboard_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Dashboard_Barometer_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });            

               MenuItem pressure_graph_Item = new MenuItem("pressure graph");
               popup.add(pressure_graph_Item);

               //popup.addSeparator();

               pressure_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_Pressure_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });

               if ((RS232_connection_mode_II == 1) && (defaultPort_II != null))                     // also a HMP155 connected
               {
                  MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
                  popup.add(air_temp_graph_Item);

                  air_temp_graph_Item.addActionListener(new ActionListener() 
                  {
                     @Override
                     public void actionPerformed(ActionEvent e) 
                     {
                        Graphs_Airtemp_Sensor_Data_actionPerformed(null);  
                     } // public void actionPerformed(ActionEvent e)
                  }); 
               }

               popup.addSeparator();

            } // if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) || (RS232_connection_mode == 4) || (RS232_connection_mode == 5)) && (defaultPort != null) ) // PTB220 or PTB330 or Mintaka Duo


            if (RS232_connection_mode == 6) // Mintaka Star LAN
            {
               MenuItem dashboard_Item = new MenuItem("dashboard barometer");
               popup.add(dashboard_Item);

               popup.addSeparator();

               dashboard_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Dashboard_Barometer_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });                    

               MenuItem pressure_graph_Item = new MenuItem("pressure graph");
               popup.add(pressure_graph_Item);

               //popup.addSeparator();

               pressure_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_Pressure_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });

               if ((RS232_connection_mode_II == 1) && (defaultPort_II != null))                     // also a HMP155 connected
               {
                  MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
                  popup.add(air_temp_graph_Item);

                  air_temp_graph_Item.addActionListener(new ActionListener() 
                  {
                     @Override
                     public void actionPerformed(ActionEvent e) 
                     {
                        Graphs_Airtemp_Sensor_Data_actionPerformed(null);  
                     } // public void actionPerformed(ActionEvent e)
                  }); 
               }

               popup.addSeparator();

            } // if (RS232_connection_mode == 6)


            if ( ((RS232_connection_mode == 7) && (defaultPort != null)) || (RS232_connection_mode == 8) )  // Mintaka StarX
            {
               MenuItem dashboard_Item = new MenuItem("dashboard barometer");
               popup.add(dashboard_Item);

               popup.addSeparator();

               dashboard_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Dashboard_Barometer_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });                    

               MenuItem pressure_graph_Item = new MenuItem("pressure graph");
               popup.add(pressure_graph_Item);

               MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
               popup.add(air_temp_graph_Item);

               popup.addSeparator();

               pressure_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_Pressure_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });

               air_temp_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_Airtemp_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });
            } // if ( ((RS232_connection_mode == 7) && (defaultPort != null)) || (RS232_connection_mode == 8) )


            if ((RS232_connection_mode == 0) && (RS232_connection_mode_II == 1) && (defaultPort_II != null))    // no 1st instrument (barometer) but one 2nd instrument (temperature device)
            {
               MenuItem air_temp_graph_Item = new MenuItem("air temp graph");
               popup.add(air_temp_graph_Item);

               air_temp_graph_Item.addActionListener(new ActionListener() 
               {
                  @Override
                  public void actionPerformed(ActionEvent e) 
                  {
                     Graphs_Airtemp_Sensor_Data_actionPerformed(null);  
                  } // public void actionPerformed(ActionEvent e)
               });             

               popup.addSeparator();

            } // if ((RS232_connection_mode == 0) && (RS232_connection_mode_II == 1) && (defaultPort_II != null)) 


            // menu items 'exit' and 'restore (maximize)' always present!
            //
            MenuItem restoreItem = new MenuItem("Maximize TurboWin+");
            popup.add(restoreItem);
            MenuItem exitItem = new MenuItem("Exit TurboWin+");
            popup.add(exitItem);


            // exit program
            //
            exitItem.addActionListener(new ActionListener() 
            {
               @Override
               public void actionPerformed(ActionEvent e) 
               {
   /*               
                  String info = "Are you sure you want to exit this application?";
                  if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) || (RS232_connection_mode == 3)) && (defaultPort != null) )
                  {
                     info += "\n (TurboWin+ will stop with monitoring and collecting of the sensor data)";
                  }   

                  int result = JOptionPane.showConfirmDialog(main.this, info, "Exit " + APPLICATION_NAME, JOptionPane.YES_NO_OPTION);

                  if (result == JOptionPane.YES_OPTION)
                  {
                     tray.remove(trayIcon);
                     System.exit(0);     
                  }     
   */
                  main_windowClosing(null);
               } // public void actionPerformed(ActionEvent e)
            });


            // restore (maximize main screen)
            //
            restoreItem.addActionListener(new ActionListener() 
            {
               @Override
               public void actionPerformed(ActionEvent e) 
               {
                  setExtendedState(NORMAL); 
                  // restoring old windows state
                  //int state = getExtendedState();
                  //state = state & ~turbowin.main.ICONIFIED;
                  //setExtendedState(state);

                  setVisible(true); 
                  tray.remove(trayIcon);

                  // NB there will be no deiconified windows system message! (in case of the system tray)
                  main_window_updating_date_time();

               } // public void actionPerformed(ActionEvent e)
            });


            trayIcon.setPopupMenu(popup);

            try 
            {
               //if (tray != null && trayIcon != null)
               //{
               tray.add(trayIcon);
               //}
               //trayIcon.displayMessage("TurboWin+", "test", MessageType.INFO);
            } 
            catch (AWTException e) 
            {
               System.out.println("+++ TrayIcon could not be added.");
            }      

         } // else (so system tray available)
      } // if (use_system_tray)
   }//GEN-LAST:event_main_windowIconfied

   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/     
   private void Info_Calculator_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Info_Calculator_menu_actionPerformed
      // TODO add your handling code here:
      
      calculator form = new calculator();               
      form.setSize(350, 600);
      form.setVisible(true); 
   }//GEN-LAST:event_Info_Calculator_menu_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/     
   private void Maintenance_obs_format_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_obs_format_actionPerformed
      // TODO add your handling code here:
      mode = SET_OBS_FORMAT;

      if (main_support.password_ok) // no password needed, direct to the obs format settings form
      {
         myobsformat form = new myobsformat();
         form.setSize(800, 600);
         form.setVisible(true);
      }
      else
      {
         mypassword form = new mypassword();
         form.setSize(400, 300);
         form.setVisible(true);
      }
   }//GEN-LAST:event_Maintenance_obs_format_actionPerformed

	
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/     
   private void seawater_temp_mainscreen_mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seawater_temp_mainscreen_mouseClicked
      // TODO add your handling code here:
      Input_Temperatures_menu_actionPerformed(null);
   }//GEN-LAST:event_seawater_temp_mainscreen_mouseClicked

	
	
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/     
   private void Graph_All_Sensor_Data_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Graph_All_Sensor_Data_actionPerformed
      // TODO add your handling code here:
       
      if (graph_form != null)
      {
         if (sensor_data_file_ophalen_timer_is_gecreeerd == true)  // 15-05-2013
         {
            if (RS232_view.sensor_data_file_ophalen_timer.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer = null;
      
         sensor_data_file_ophalen_timer_is_gecreeerd = false;
         
       
         if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true)  // 15-05-2013
         {
            if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning())
            {
               RS232_view.sensor_data_file_ophalen_timer_II.stop();
            }
         }
         RS232_view.sensor_data_file_ophalen_timer_II = null;
      
         sensor_data_file_ophalen_timer_is_gecreeerd_II = false;
          
         //graph_form.dispose();
         graph_form.setVisible(false);
      }
       
      mode_grafiek = MODE_ALL_PARAMETERS;
      
      graph_form = new RS232_view();
      //graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full screen
      graph_form.setExtendedState(MAXIMIZED_BOTH); 
      graph_form.setVisible(true);  		
		
   }//GEN-LAST:event_Graph_All_Sensor_Data_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
   private void Maintenance_WOW_settings_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_WOW_settings_actionPerformed
      // TODO add your handling code here:
      
      mode = SET_WOW_APR_SETTINGS;

      if (main_support.password_ok) // no password needed, direct to the APR settings form
      {
         WOW_APR_settings form = new WOW_APR_settings();
         form.setSize(1000, 700);
         form.setVisible(true);   
      }
      else
      {
         mypassword form = new mypassword();
         form.setSize(400, 300);
         form.setVisible(true);      
      }
   }//GEN-LAST:event_Maintenance_WOW_settings_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
   private void Info_System_Log_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Info_System_Log_menu_actionPerformed
      // TODO add your handling code here:
      
      mysystem_log form = new mysystem_log();
      form.setExtendedState(MAXIMIZED_BOTH);                      // full screen
      form.setVisible(true);           
   }//GEN-LAST:event_Info_System_Log_menu_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
   private void Maintenance_server_settings_actionperformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maintenance_server_settings_actionperformed
      // TODO add your handling code here:
      
      mode = SET_SERVER_SETTINGS;

      if (main_support.password_ok) // no password needed, direct to the server settings form
      {
         myserversettings form = new myserversettings();
         form.setSize(1000, 700);
         form.setVisible(true);   
      }
      else
      {   
         mypassword form = new mypassword();
         form.setSize(400, 300);
         form.setVisible(true);      
      }
   }//GEN-LAST:event_Maintenance_server_settings_actionperformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
   private void Themes_4_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Themes_4_actionPerformed
      
      // TODO add your handling code here:
      boolean reset_main_class = false;
      
      //
      //////// Sunset, Nimbus (vanaf Java 1.6.10) based
      //
      // NB dit is eigenlijk geen Theme maar compleet ander color scheme
      
         
      if (theme_mode.equals(THEME_TRANSPARENT))
      {
         reset_main_class = true;
         theme_changed = true;                          // for checking more than one instance running
      }
   
      if (reset_main_class)
      {
         mainClass.dispose();
      }
      
      try
      {
         for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
         {
            if ("Nimbus".equals(info.getName()))
            {
               UIManager.setLookAndFeel(info.getClassName());
            
               // eg for the color values see: http://www.rapidtables.com/web/color/RGB_Color.htm
             
               //UIManager.put("control", new Color(214,217,223));                         // Nimbus default
               UIManager.put("control", new Color(255,110,110));                           // panels, frame
            
               UIManager.put("nimbusBase", new Color(51,98,140));                          // menu background unfolded items, radio buttons, yes/no buttons
               UIManager.put("nimbusFocus", new Color(115,164,209));                       // border color selected control (text field, button, radio button etc.)
            
               //UIManager.put("nimbusLightBackground", new Color(255,255,255));           // Nimbus default    // controls wit
               UIManager.put("nimbusLightBackground", new Color(255,204,204));             // controls 
            
               //UIManager.put("nimbusSelectionBackground", new Color(57,105,138));        // Nimbus default    // selected text  ONBELANGRIJK
               UIManager.put("text", new Color(0,0,0));                                    // Nimbus default, black texten
            
               UIManager.put("nimbusBlueGrey", new Color(169,176,190));                    // Nimbus default, menu bar, text field, button, radio button etc.

               SwingUtilities.updateComponentTreeUI(main.this);                            // moet komen na setLookAndFeel !!!
            
               jTextField4.setBackground(new java.awt.Color(255,51,51));                   // status bar (for system messages)
               //jTextField4.setBackground(new java.awt.Color(255,153,153));
            
               break;
            }
         }
         theme_mode = THEME_NIMBUS_SUNSET;
      }
      catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
      {
         String info = "Nimbus related Themes not supported on this computer";
         JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
         main.log_turbowin_system_message("[GENERAL] " + info);
      }
      
     if (reset_main_class)
     {
         //JFrame.setDefaultLookAndFeelDecorated(true);
         mainClass = new main();
         mainClass.setVisible(true);
      }
      
      
   }//GEN-LAST:event_Themes_4_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
   
   private void Info_send_System_log_menu_actionperformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Info_send_System_log_menu_actionperformed
      // TODO add your handling code here:
      

   new SwingWorker<Void, Void>()
   {
      @Override
      protected Void doInBackground() throws Exception
      {
         /*
         // Version 6 of the Java Platform, Standard Edition (Java SE), continues to narrow the gap with
         // new system tray functionality, better  print support for JTable, and now the Desktop API
         // (java.awt.Desktop API).
         //
         // Use the Desktop.isDesktopSupported() method to determine whether the Desktop API is available.
         // On the Solaris Operating System and the Linux platform, this API is dependent on Gnome libraries.
         // If those libraries are unavailable, this method will return false. After determining that the API is
         // supported, that is, the isDesktopSupported() returns true, the application can retrieve a Desktop
         // instance using the static method getDesktop().
         //
         */
         Desktop desktop          = null;
         String email_body_line   = "";
         String email_body_line_1 = "";
         String email_body_line_2 = "";

         // Before more Desktop API is used, first check
         // whether the API is supported by this particular
         // virtual machine (VM) on this particular host.
         if (Desktop.isDesktopSupported())
         {
            desktop = Desktop.getDesktop();
            try
            {
               TimeZone timeZone = TimeZone.getTimeZone("UTC");
               Calendar cal = Calendar.getInstance(timeZone);
               
               String file_naam_1 = "turbowin_system_" + sdf_tsl_1.format(cal.getTime()) + ".txt";
               
               
               cal.add(Calendar.MONTH, -1);
               String file_naam_2 = "turbowin_system_" + sdf_tsl_1.format(cal.getTime()) + ".txt";
               
               // NB OK but not possible to go to previuos month [deprecated] String file_naam = "turbowin_system_" + sdf_tsl_1.format(new Date()) + ".txt";     
               
               String volledig_path_turbowin_system_logs_1 = main.logs_dir + java.io.File.separator + main.TURBOWIN_SYSTEM_LOGS_DIR + java.io.File.separator + file_naam_1;
               String volledig_path_turbowin_system_logs_2 = main.logs_dir + java.io.File.separator + main.TURBOWIN_SYSTEM_LOGS_DIR + java.io.File.separator + file_naam_2;
               
               //
               // NB write every system log line in the email body fails !!
               //
               
               
               // check if System log 1 file exists (current month)
               final File system_file_1 = new File(volledig_path_turbowin_system_logs_1);
               if (system_file_1.exists() == true)
               {
                  email_body_line_1 = "Please attach manually the file: " + volledig_path_turbowin_system_logs_1;
                  email_body_line = email_body_line_1;
               } // if (system_file_1.exists() == true)
               
               // check if System log 2 file exists (previous month)
               final File system_file_2 = new File(volledig_path_turbowin_system_logs_2);
               if (system_file_2.exists() == true)
               {
                  email_body_line_2 = "Please attach manually the file: " + volledig_path_turbowin_system_logs_2;
                  
                  if (system_file_1.exists() == true)
                  {
                     email_body_line += "\nand\n\n";
                     email_body_line +=  email_body_line_2;
                  }
                  else
                  {
                     email_body_line = email_body_line_2;
                  }
               } // if (system_file_2.exists() == true)
               
               
               if (system_file_1.exists() == false && system_file_2.exists() == false)
               {
                  JOptionPane.showMessageDialog(null, "No " + APPLICATION_NAME + " system log files found", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);        
               }
               else
               {
                  /////// invoke email program //////
                  //
                  String email_recipient = "";
                  String email_subject = APPLICATION_NAME + " System logs " + main.ship_name;
                  String mail_txt = email_recipient + "?subject=" + email_subject  + "&body=" + email_body_line;
                  //String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=test";
                  URI uriMailTo = null;
                  try
                  {
                     uriMailTo = new URI("mailto", mail_txt, null);
                  }
                  catch (URISyntaxException ex)
                  {
                     JOptionPane.showMessageDialog(null, "Error invoking default Email program" + " (" + ex + ")", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  }

                  desktop.mail(uriMailTo);
               } // else
            } // try
            catch (IOException ex)
            {
               JOptionPane.showMessageDialog(null, "Error invoking default Email program" + " (" + ex + ")", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         } // if (Desktop.isDesktopSupported())
         else
         {
            JOptionPane.showMessageDialog(null, "Error invoking default Email program (-Desktop- method not supported on this computer system)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         } // else

         return null;
      } // protected Void doInBackground() throws Exception


   }.execute(); // new SwingWorker<Void, Void>()
      
   }//GEN-LAST:event_Info_send_System_log_menu_actionperformed

   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/      
   private void Dashboard_Barometer_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_Barometer_actionPerformed
      // TODO add your handling code here:
      
      if (dashboard_form != null)
      {
         if (DASHBOARD_view.dashboard_update_timer_is_gecreeerd == true)  
         {
            if (DASHBOARD_view.dashboard_update_timer.isRunning())
            {
               DASHBOARD_view.dashboard_update_timer.stop();
            }
         }
         DASHBOARD_view.dashboard_update_timer = null;
      
         DASHBOARD_view.dashboard_update_timer_is_gecreeerd = false;
          
         //graph_form.dispose();
         dashboard_form.setVisible(false);
      }      
      
      
      dashboard_form = new DASHBOARD_view();
      dashboard_form.setExtendedState(MAXIMIZED_BOTH); 
      dashboard_form.setVisible(true);       
      
   }//GEN-LAST:event_Dashboard_Barometer_actionPerformed

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/    
   private void Dashboard_AWS_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_AWS_actionPerformed
      // TODO add your handling code here:
      
      
      if (dashboard_form_AWS != null)
      {
         if (DASHBOARD_view_AWS.dashboard_update_AWS_timer_is_gecreeerd == true)  
         {
            if (DASHBOARD_view_AWS.dashboard_update_AWS_timer.isRunning())
            {
               DASHBOARD_view_AWS.dashboard_update_AWS_timer.stop();
            }
         }
         DASHBOARD_view_AWS.dashboard_update_AWS_timer = null;
      
         DASHBOARD_view_AWS.dashboard_update_AWS_timer_is_gecreeerd = false;
          
         //graph_form.dispose();
         dashboard_form_AWS.setVisible(false);
      }      
      
      
      dashboard_form_AWS = new DASHBOARD_view_AWS();
      dashboard_form_AWS.setExtendedState(MAXIMIZED_BOTH); 
      dashboard_form_AWS.setVisible(true);       
      
   }//GEN-LAST:event_Dashboard_AWS_actionPerformed

   
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
   private void Dashboard_AWS_digital_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_AWS_digital_actionPerformed
      // TODO add your handling code here:
      
      if (dashboard_form_AWS_digital != null)
      {
         if (DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer_is_gecreeerd == true)  
         {
            if (DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer.isRunning())
            {
               DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer.stop();
            }
         }
         DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer = null;
      
         DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer_is_gecreeerd = false;
          
         //graph_form.dispose();
         dashboard_form_AWS_digital.setVisible(false);
      }      
      
      
      dashboard_form_AWS_digital = new DASHBOARD_view_AWS_digital();
      dashboard_form_AWS_digital.setExtendedState(MAXIMIZED_BOTH); 
      dashboard_form_AWS_digital.setVisible(true);       
      
      
   }//GEN-LAST:event_Dashboard_AWS_digital_actionPerformed

   
   
  /***********************************************************************************************/
  /*                                                                                             */
  /*                                                                                             */
  /*                                                                                             */
  /***********************************************************************************************/   
   private void Dashboard_Latest_Obs_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_Latest_Obs_actionPerformed
      // TODO add your handling code here:
      
      DASHBOARD_latest_obs form = new DASHBOARD_latest_obs();               
      form.setSize(400, 300);
      form.setVisible(true);       
   }//GEN-LAST:event_Dashboard_Latest_Obs_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Maintenance_Show_maintenance_data_actionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_Maintenance_Show_maintenance_data_actionPerformed
   {//GEN-HEADEREND:event_Maintenance_Show_maintenance_data_actionPerformed
      // TODO add your handling code here:
      
      mode = MAINTENANCE_SHOW_DATA;

      if (main_support.password_ok) // no password needed, direct to the show-all-maintenance-data display form
      {
         mymaintenancedata form = new mymaintenancedata();
         form.setExtendedState(MAXIMIZED_BOTH);                      // full screen
         form.setVisible(true);         
      }
      else
      {
         mypassword form = new mypassword();
         form.setSize(400, 300);
         form.setVisible(true);
      }
   }//GEN-LAST:event_Maintenance_Show_maintenance_data_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Maintenance_Import_maintenance_data_actionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_Maintenance_Import_maintenance_data_actionPerformed
   {//GEN-HEADEREND:event_Maintenance_Import_maintenance_data_actionPerformed
      // TODO add your handling code here:
      
      mode = MAINTENANCE_IMPORT_DATA;

      if (main_support.password_ok) // no password needed, direct to the show-all-maintenance-data display form
      {
         mymaintenancedata form = new mymaintenancedata();
         form.setExtendedState(MAXIMIZED_BOTH);                      // full screen
         form.setVisible(true);         
      }
      else
      {
         mypassword form = new mypassword();
         form.setSize(400, 300);
         form.setVisible(true);
      }
   }//GEN-LAST:event_Maintenance_Import_maintenance_data_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/ 
   private void Maintenance_Export_maintenance_data_actionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_Maintenance_Export_maintenance_data_actionPerformed
   {//GEN-HEADEREND:event_Maintenance_Export_maintenance_data_actionPerformed
      // TODO add your handling code here:
      
      mode = MAINTENANCE_EXPORT_DATA;

      if (main_support.password_ok) // no password needed, direct to the show-all-maintenance-data display form
      {
         mymaintenancedata form = new mymaintenancedata();
         form.setExtendedState(MAXIMIZED_BOTH);                      // full screen
         form.setVisible(true);         
      }
      else
      {
         mypassword form = new mypassword();
         form.setSize(400, 300);
         form.setVisible(true);
      }   
   }//GEN-LAST:event_Maintenance_Export_maintenance_data_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Dashboard_AWS_hybrid_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_AWS_hybrid_actionPerformed
      // TODO add your handling code here:
      
      if (dashboard_form_AWS_hybrid != null)
      {
         if (DASHBOARD_view_AWS_hybrid.dashboard_update_timer_AWS_hybrid_is_gecreeerd == true)  
         {
            if (DASHBOARD_view_AWS_hybrid.dashboard_update_timer_AWS_hybrid.isRunning())
            {
               DASHBOARD_view_AWS_hybrid.dashboard_update_timer_AWS_hybrid.stop();
            }
         }
         DASHBOARD_view_AWS_hybrid.dashboard_update_timer_AWS_hybrid = null;
      
         DASHBOARD_view_AWS_hybrid.dashboard_update_timer_AWS_hybrid_is_gecreeerd = false;
          
         dashboard_form_AWS_hybrid.setVisible(false);
      }      
      
      dashboard_form_AWS_hybrid = new DASHBOARD_view_AWS_hybrid();
      dashboard_form_AWS_hybrid.setExtendedState(MAXIMIZED_BOTH); 
      dashboard_form_AWS_hybrid.setVisible(true);       
      
   }//GEN-LAST:event_Dashboard_AWS_hybrid_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Dashboard_AWS_radar_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_AWS_radar_actionPerformed
      // TODO add your handling code here:
      if (dashboard_form_AWS_radar != null)
      {
         if (DASHBOARD_view_AWS_radar.dashboard_update_timer_AWS_radar_is_gecreeerd == true)  
         {
            if (DASHBOARD_view_AWS_radar.dashboard_update_timer_AWS_radar.isRunning())
            {
               DASHBOARD_view_AWS_radar.dashboard_update_timer_AWS_radar.stop();
            }
         }
         DASHBOARD_view_AWS_radar.dashboard_update_timer_AWS_radar = null;
      
         DASHBOARD_view_AWS_radar.dashboard_update_timer_AWS_radar_is_gecreeerd = false;
          
         dashboard_form_AWS_radar.setVisible(false);
      }      
      
      dashboard_form_AWS_radar = new DASHBOARD_view_AWS_radar();
      dashboard_form_AWS_radar.setExtendedState(MAXIMIZED_BOTH); 
      dashboard_form_AWS_radar.setVisible(true);       
   }//GEN-LAST:event_Dashboard_AWS_radar_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void main_windowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_main_windowDeiconified
      // TODO add your handling code here:
      
      
      // NB looks like in Windows this will never be reached (for Windows see windowIconfied())
      //    note there is a difference betweenWindows and Linux system tray versus Dash
      //   
      
      //System.out.println("+++ Function main_windowDeiconified():" + evt);
   /*   
      OSDetector.OSType ostype = OSDetector.detect_OS();
      switch (ostype)
      {
         case WINDOWS: break;         // for Windows: this will be done in Function: main_windowIconfied() [main.java]
         default:      main_window_updating_message();
                       break;
      }
   */   
      main_window_updating_date_time();
      
   }//GEN-LAST:event_main_windowDeiconified

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Dashboard_latest_AWS_measurements_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_latest_AWS_measurements_actionPerformed
      // TODO add your handling code here:
      
      //if (mylatestmeasurements.compareTo("") != 0)
      //{
      //   JOptionPane.showMessageDialog(null, "Please close first a previously opened Latest AWS measuremnets form", main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
      // }
      //else
      //{
      //   latestmeasurements_report = MESUREMENTS_SP;              // AMVER sailing plan
      //
      //   mylatestmeasurements form = new mylatestmeasurements();
      //   form.setSize(1000, 700);
      //   form.setVisible(true);
      //}     
      
      mylatestmeasurements form = new mylatestmeasurements();
      form.setSize(1000, 700);
      form.setVisible(true);
      
   }//GEN-LAST:event_Dashboard_latest_AWS_measurements_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/  
   private void Maps_Obs_Manual_Map_Offline_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_Obs_Manual_Map_Offline_actionPerformed
      // TODO add your handling code here:
      
      OSM_mode = main.OSM_OFFLINE_MANUAL;
      Maps_OSM();
   }//GEN-LAST:event_Maps_Obs_Manual_Map_Offline_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void Maps_Obs_Manual_Map_Online_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_Obs_Manual_Map_Online_actionPerformed
      // TODO add your handling code here:
      
      OSM_mode = main.OSM_ONLINE_MANUAL;
      Maps_OSM();
   }//GEN-LAST:event_Maps_Obs_Manual_Map_Online_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/     
   private void Maps_AWS_Sensor_Map_Offline_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_AWS_Sensor_Map_Offline_actionPerformed
      // TODO add your handling code here:
      
      OSM_mode = main.OSM_OFFLINE_AWS_SENSOR;
      Maps_OSM();
   }//GEN-LAST:event_Maps_AWS_Sensor_Map_Offline_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/     
   private void Maps_AWS_Visual_Map_Offline_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_AWS_Visual_Map_Offline_actionPerformed
      // TODO add your handling code here:
      
      OSM_mode = main.OSM_OFFLINE_AWS_VISUAL;
      Maps_OSM();
   }//GEN-LAST:event_Maps_AWS_Visual_Map_Offline_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Maps_AWS_Sensor_Map_Online_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_AWS_Sensor_Map_Online_actionPerformed
      // TODO add your handling code here:
      
      OSM_mode = main.OSM_ONLINE_AWS_SENSOR;
      Maps_OSM();
   }//GEN-LAST:event_Maps_AWS_Sensor_Map_Online_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private void Maps_AWS_Visual_Map_Online_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_AWS_Visual_Map_Online_actionPerformed
      // TODO add your handling code here:
      
      OSM_mode = main.OSM_ONLINE_AWS_VISUAL;
      Maps_OSM();
   }//GEN-LAST:event_Maps_AWS_Visual_Map_Online_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/         
   private void Output_obs_by_email_default_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Output_obs_by_email_default_actionPerformed
      // TODO add your handling code here:
      
      email_send_mode = EMAIL_SEND_DEFAULT;
      Output_obs_by_email_all_manual();
   }//GEN-LAST:event_Output_obs_by_email_default_actionPerformed

   
   
   
   
   
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/     
   private void Info_barometer_comparison_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Info_barometer_comparison_menu_actionPerformed
      // TODO add your handling code here:
      
      barometer_comparison form = new barometer_comparison();               
      form.setSize(1000, 700);
      form.setVisible(true);       
   }//GEN-LAST:event_Info_barometer_comparison_menu_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/     
   private void Themes_5_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Themes_5_actionPerformed
      // TODO add your handling code here:
      
      if (!theme_mode.equals(THEME_TRANSPARENT))
      {
         mainClass.dispose();
         theme_changed = true;                       // for checking more than one instance running
      
         try 
         {
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            theme_mode = THEME_TRANSPARENT;
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
         } 
         catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
         {
            String info = "Error invoking Transparent Theme";
            JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
         }      
      
         JFrame.setDefaultLookAndFeelDecorated(true);     // !!! This is essential set it to the defult metal java mode (= the only Java Look and Feel suitable for tranaparency)
         mainClass = new main();
         mainClass.setVisible(true);
      }
      
   }//GEN-LAST:event_Themes_5_actionPerformed
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/    
   private void APR_toolbar_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_APR_toolbar_itemStateChanged
      // TODO add your handling code here:
      boolean checks_ok = true;
      boolean additional_checks_ok = true;
      
      
      APR = jCheckBox1.isSelected() == true;               // now APR = true or false
      
      if (!APR)
      {
         // NB reset JLabel39 (e.g. in APR mode: "--- more than 30 minutes to go for next automated upload, please do not insert observation data --- "
         //    must be reseted to original string
         main.jLabel39.setForeground(Color.BLACK);
         main.jLabel39.setText("--- adding data: input menu, popup menu, toolbar icons or click on the text labels or fields ---");
         
         String info = "automated reporting (AP[&T]R) is turned off";
         JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);            
      } // if (!APR)
      
      
      if (APR)
      {
         // AP[&T]R reporting interval
         if (checks_ok && main.APR_reporting_interval.equals(""))
         {
            JOptionPane.showMessageDialog(null, "AP[&T]R reporting interval not selected (Maintenance -> APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            checks_ok = false;
            APR = false;
            jCheckBox1.setSelected(false);
         }
         
         // AP[&T]R send method
         if ( checks_ok && (main.APTR_AWSR_send_method.equals("")))
         {
            JOptionPane.showMessageDialog(null, "AP[&T]R / AWSR send method unknown (Maintenance -> APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            checks_ok = false;
            APR = false;
            jCheckBox1.setSelected(false);
         }
         
         // AP[&T]R draught
         if (checks_ok)
         {
            try
            {
               double double_WOW_APR_average_draught = Double.parseDouble(main.WOW_APR_average_draught);
               
               if (main.WOW_APR_average_draught.equals("") || !(double_WOW_APR_average_draught >= 0 && double_WOW_APR_average_draught <= 50))
               {
                  JOptionPane.showMessageDialog(null, "normal steaming draft not in range 0.0 - 50.0 (Maintenance -> APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  checks_ok = false;
                  APR = false;
                  jCheckBox1.setSelected(false);
               }
            }
            catch (NumberFormatException e) 
            {
               JOptionPane.showMessageDialog(null, "normal steaming draft not in range 0.0 - 50.0 (Maintenance -> APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               checks_ok = false;
               APR = false;
               jCheckBox1.setSelected(false);
            }    
         } // if (checks_ok)
         
         // AP[&T]R barometer ic 
         if (checks_ok)
         {
            try
            {
               double double_barometer_instrument_correction = Double.parseDouble(main.barometer_instrument_correction.trim());
             
               if (main.barometer_instrument_correction.equals("") || !(double_barometer_instrument_correction >= -4.0 || double_barometer_instrument_correction <= 4.0))
               {
                  JOptionPane.showMessageDialog(null, "barometer instrument correction not in range -4.0 - 4.0 (Maintenance -> APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  checks_ok = false;
                  APR = false;
                  jCheckBox1.setSelected(false);
               }             
            }
            catch (NumberFormatException e) 
            { 
               JOptionPane.showMessageDialog(null, "barometer_instrument_correction not in range -4.0 - 4.0 (Maintenance -> APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               checks_ok = false;
               APR = false;
               jCheckBox1.setSelected(false);
            }  
         } // if (checks_ok) 
         
         // warning checks
         if (checks_ok)
         {
            additional_checks_ok = WOW_APR_settings.APR_additional_requirements_checks();
         }
         
         // pop-up message APR was turned on
         if (checks_ok && additional_checks_ok)
         {
            String info = "automated reporting (AP[&T]R) is turned on";
            JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);  
         }
         else
         {
            APR = false;
            jCheckBox1.setSelected(false);
         }
      } // if (APR)
      
      // NB below for APR turned on AND APR turned off !!
      // clear the text fields on the main screen (because maybe there are still values in the text fields from a previous setting eg APR = true) and enable the output menu items again
      main.Reset_all_meteo_parameters(); 
      main.disable_and_enable_output_menu_items();     // in fact also for ENABLING the output menu options if now set APR = false and before APR = true
      main.disable_dashboard_and_maps_menu_items();    // in fact also for ENABLING the dasboard menu options if now set APR = false and before APR = true
      
      // save the change
      if (main.offline_mode_via_cmd == true)     // after installation as standalone program this will always be the case
      {
         main.schrijf_configuratie_regels();          
      }
      else // so offline_via_jnlp mode or online (webstart) mode
      {
         //main.set_muffin();
         main.schrijf_configuratie_regels();
      }         
      
      // set start-up sequence finished flag
      //turbowin_start_up_sequence_finished = true;
   }//GEN-LAST:event_APR_toolbar_itemStateChanged

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void AWSR_toolbar_itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_AWSR_toolbar_itemStateChanged
      // TODO add your handling code here:
      boolean checks_ok = true;
      boolean additional_checks_ok = true;
      
      
      AWSR = jCheckBox2.isSelected() == true;               // now AWSR = true or false
      
      if (!AWSR)
      {
         // NB reset JLabel39 (e.g. in APR mode: "--- more than 30 minutes to go for next automated upload, please do not insert observation data --- "
         //    must be reseted to original string
         main.jLabel39.setForeground(Color.BLACK);
         main.jLabel39.setText("--- adding data: input menu, popup menu, toolbar icons or click on the text labels or fields ---");
         
         String info = "automated reporting (AWSR) is turned off";
         JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);            
      } // if (!AWSR)
      
      
      if (AWSR)
      {
         // AWSR reporting interval
         if (checks_ok && main.AWSR_reporting_interval.equals(""))
         {
            JOptionPane.showMessageDialog(null, "ASWR reporting interval not selected (Maintenance -> WOW/APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            checks_ok = false;
            AWSR = false;
            jCheckBox2.setSelected(false);
         }
         
         // AWSR send method
         if ( checks_ok && (main.APTR_AWSR_send_method.equals("")))
         {
            JOptionPane.showMessageDialog(null, "AWSR send method unknown (Maintenance -> WOW/APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            checks_ok = false;
            AWSR = false;
            jCheckBox2.setSelected(false);
         }
/*        
         // AWSR draught
         if (checks_ok)
         {
            try
            {
               double double_WOW_APR_average_draught = Double.parseDouble(main.WOW_APR_average_draught);
               
               if (main.WOW_APR_average_draught.equals("") || !(double_WOW_APR_average_draught >= 0 && double_WOW_APR_average_draught <= 50))
               {
                  JOptionPane.showMessageDialog(null, "normal steaming draft not in range 0.0 - 50.0 (Maintenance -> WOW/APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  checks_ok = false;
                  AWSR = false;
                  jCheckBox2.setSelected(false);
               }
            }
            catch (NumberFormatException e) 
            {
               JOptionPane.showMessageDialog(null, "normal steaming draft not in range 0.0 - 50.0 (Maintenance -> WOW/APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               checks_ok = false;
               AWSR = false;
               jCheckBox2.setSelected(false);
            }    
         } // if (checks_ok)
         
         // AWSR barometer ic 
         if (checks_ok)
         {
            try
            {
               double double_barometer_instrument_correction = Double.parseDouble(main.barometer_instrument_correction.trim());
             
               if (main.barometer_instrument_correction.equals("") || !(double_barometer_instrument_correction >= -4.0 || double_barometer_instrument_correction <= 4.0))
               {
                  JOptionPane.showMessageDialog(null, "barometer instrument correction not in range -4.0 - 4.0 (Maintenance -> WOW/APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  checks_ok = false;
                  AWSR = false;
                  jCheckBox2.setSelected(false);
               }             
            }
            catch (NumberFormatException e) 
            { 
               JOptionPane.showMessageDialog(null, "barometer_instrument_correction not in range -4.0 - 4.0 (Maintenance -> WOW/APR/APTR/AWSR settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               checks_ok = false;
               AWSR = false;
               jCheckBox2.setSelected(false);
            }  
         } // if (checks_ok) 
*/         
         // warning checks
         if (checks_ok)
         {
            additional_checks_ok = WOW_APR_settings.AWSR_additional_requirements_checks();
         }
         
         // pop-up message AWSR was turned on
         if (checks_ok && additional_checks_ok)
         {
            String info = "automated reporting (AWSR) is turned on";
            JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);  
         }
         else
         {
            AWSR = false;
            jCheckBox2.setSelected(false);
         }
      } // if (AWSR)
      
      // NB below for AWSR turned on AND AWSR turned off !!
      // clear the text fields on the main screen (because maybe there are still values in the text fields from a previous setting eg AWSR = true) and enable the output menu items again
      main.Reset_all_meteo_parameters(); 
      main.disable_and_enable_output_menu_items();     // in fact also for ENABLING the output menu options if now set AWSR = false and before AWSR = true
      
      // save the change
      if (main.offline_mode_via_cmd == true)     // after installation as standalone program this will always be the case
      {
         main.schrijf_configuratie_regels();          
      }
      else // so offline_via_jnlp mode or online (webstart) mode
      {
         main.schrijf_configuratie_regels();
      }         
      
      // set start-up sequence finished flag
      //turbowin_start_up_sequence_finished = true;
   }//GEN-LAST:event_AWSR_toolbar_itemStateChanged

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Dashboard_APR_radar_actionperformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_APR_radar_actionperformed
      // TODO add your handling code here:
      if (dashboard_form_APR_radar != null)
      {
         if (DASHBOARD_view_APR_radar.dashboard_update_APR_timer_is_gecreeerd == true)  
         {
            if (DASHBOARD_view_APR_radar.dashboard_update_APR_timer.isRunning())
            {
               DASHBOARD_view_APR_radar.dashboard_update_APR_timer.stop();
            }
         }
         DASHBOARD_view_APR_radar.dashboard_update_APR_timer = null;
      
         DASHBOARD_view_APR_radar.dashboard_update_APR_timer_is_gecreeerd = false;
          
         dashboard_form_APR_radar.setVisible(false);
      }      
      
      dashboard_form_APR_radar = new DASHBOARD_view_APR_radar();
      dashboard_form_APR_radar.setExtendedState(MAXIMIZED_BOTH); 
      dashboard_form_APR_radar.setVisible(true);       
      
   }//GEN-LAST:event_Dashboard_APR_radar_actionperformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void Maps_satellite_image_IR_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_satellite_image_IR_actionPerformed
      // TODO add your handling code here:
      
      String satellite_image_mode = SATELLITE_IR_IMAGE;
      support_class.determine_satellite_image_url(satellite_image_mode);
   }//GEN-LAST:event_Maps_satellite_image_IR_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Maps_satellite_image_SST_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_satellite_image_SST_actionPerformed
      // TODO add your handling code here:
      
      String satellite_image_mode = SATELLITE_SST_IMAGE;
      support_class.determine_satellite_image_url(satellite_image_mode);
   }//GEN-LAST:event_Maps_satellite_image_SST_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Output_obs_by_email_Custom_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Output_obs_by_email_Custom_actionPerformed
      // TODO add your handling code here:
      
      email_send_mode = EMAIL_SEND_CUSTOM;
      Output_obs_by_email_all_manual();
   }//GEN-LAST:event_Output_obs_by_email_Custom_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Maps_pilot_charts(final String chart)
   {
      new SwingWorker<Integer, Void>() 
      {
         @Override
         protected Integer doInBackground() throws Exception 
         {
            String os = OSDetector.getOSString();
            
            
            String sub_url_chart_number = "";
            String sub_url_chart_month = "";
            
            if (chart.substring(12, 14).equals("SA"))
            {
               sub_url_chart_number = "105";
            }
            else if (chart.substring(12, 14).equals("NA"))
            {
               sub_url_chart_number = "106";
            }
            else if (chart.substring(12, 14).equals("SP"))
            {
               sub_url_chart_number = "107";
            }
            else if (chart.substring(12, 14).equals("NP"))
            {
               sub_url_chart_number = "108";
            }
            else if (chart.substring(12, 14).equals("IN"))
            {
               sub_url_chart_number = "109";
            }
            
            sub_url_chart_month = chart.substring(15, 18).toLowerCase();
            
            // e.g., https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/raw/master/pilot_charts/105jan.pdf?inline=true 
            String link_url = "";
            link_url = "https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/raw/master/pilot_charts/" + sub_url_chart_number + sub_url_chart_month + ".pdf?inline=true";
            
            Integer code = 0;                              // ok
            Desktop desktop = null;
            
            if (os.equals("LINUX"))
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
               try 
               {
                  // create cmd array
                  String[] cmdArray = {"kde-open", link_url};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               } 
               catch (IOException e) 
               {
                  try 
                  {
                     // create cmd array
                     String[] cmdArray = {"xdg-open", link_url};

                     // create a process and execute cmdArray
                     Process process = Runtime.getRuntime().exec(cmdArray);
                  } 
                  catch (IOException e2) 
                  {
                     try 
                     {
                        // create cmd array
                        String[] cmdArray = {"open", link_url};

                        // create a process and execute cmdArray
                        Process process = Runtime.getRuntime().exec(cmdArray);
                     } 
                     catch (IOException e3) 
                     {
                        code = -1;
                     } // catch (IOException e3)                 
                  } // catch (IOException e2)                 
               } // catch  (IOException e) 
               if (code == -1)
               {
                  if (Desktop.isDesktopSupported())
                  {
                     desktop = Desktop.getDesktop();
                     URI uri = null;
                     try
                     {
                        String http_adres = link_url;  
                        uri = new URI(http_adres);
                        desktop.browse(uri);
                     }
                     catch (IOException | URISyntaxException ioe) 
                     { 
                        code = -2;
                     }
                  } // if (Desktop.isDesktopSupported())
                  else
                  {
                     code = -1;
                  } // else
               } // if (code == -1)  
            } // if (os.equals("LINUX"))
            else // Windows etc.
            {   
               // Before more Desktop API is used, first check
               // whether the API is supported by this particular
               // virtual machine (VM) on this particular host.
               if (Desktop.isDesktopSupported()) 
               {
                  desktop = Desktop.getDesktop();
                  URI uri = null;
                  try {
                     String http_adres = link_url;
                     uri = new URI(http_adres);
                     desktop.browse(uri);
                  } 
                  catch (IOException | URISyntaxException ioe) 
                  {
                     code = -2;
                  }
               } // if (Desktop.isDesktopSupported())
               else 
               {
                  code = -1;
               } // else
            } // else (Windows etc.)
            
            
            return code;

         } // protected Integer doInBackground() throws Exception

         @Override
         protected void done() 
         {
            try 
            {
               Integer response_code = get();

               if (response_code == -1) 
               {
                  String os = OSDetector.getOSString();
                  
                  String message = "";
                  if (os.equals("LINUX"))
                  {
                     message = "[GENERAL] Error invoking default web browser (OS = Linux)";
                  }
                  else
                  {   
                     message = "[GENERAL] Error invoking default web browser (-Desktop-method not supported on this computer system)";
                  }   
                  JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  main.log_turbowin_system_message(message);
               } 
               else if (response_code == -2) 
               {
                  String message = "[GENERAL] Error invoking URL";
                  JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  main.log_turbowin_system_message(message);
               }
            } // try
            catch (InterruptedException | ExecutionException ex) 
            {
               String message = "[GENERAL] Error invoking default web browser; " + ex.toString();
               main.log_turbowin_system_message(message);
            } // catch
         } // protected void done()      
      }.execute(); // new SwingWorker<Void, Void>()      
   }
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Maps_pilot_charts_SA_january_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_january_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_JANUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_january_actionPerformed

   private void Maps_pilot_charts_SA_february_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_february_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_FEBRUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_february_actionPerformed

   private void Maps_pilot_charts_SA_march_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_march_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_MARCH;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_march_actionPerformed

   private void Maps_pilot_charts_SA_april_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_april_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_APRIL;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_april_actionPerformed

   private void Maps_pilot_charts_SA_may_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_may_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_MAY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_may_actionPerformed

   private void Maps_pilot_charts_SA_june_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_june_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_JUNE;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_june_actionPerformed

   private void Maps_pilot_charts_SA_july_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_july_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_JULY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_july_actionPerformed

   private void Maps_pilot_charts_SA_august_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_august_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_AUGUST;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_august_actionPerformed

   private void Maps_pilot_charts_SA_september_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_september_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_SEPTEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_september_actionPerformed

   private void Maps_pilot_charts_SA_october_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_october_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_OCTOBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_october_actionPerformed

   private void Maps_pilot_charts_SA_november_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_november_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_NOVEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_november_actionPerformed

   private void Maps_pilot_charts_SA_december_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SA_december_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SA_DECEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SA_december_actionPerformed

   private void Maps_pilot_charts_NA_january_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_january_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_JANUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_january_actionPerformed

   private void Maps_pilot_charts_NA_february_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_february_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_FEBRUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_february_actionPerformed

   private void Maps_pilot_charts_NA_march_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_march_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_MARCH;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_march_actionPerformed

   private void Maps_pilot_charts_NA_april_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_april_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_APRIL;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_april_actionPerformed

   private void Maps_pilot_charts_NA_may_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_may_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_MAY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_may_actionPerformed

   private void Maps_pilot_charts_NA_june_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_june_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_JUNE;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_june_actionPerformed

   private void Maps_pilot_charts_NA_july_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_july_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_JULY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_july_actionPerformed

   private void Maps_pilot_charts_NA_august_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_august_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_AUGUST;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_august_actionPerformed

   private void Maps_pilot_charts_NA_september_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_september_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_SEPTEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_september_actionPerformed

   private void Maps_pilot_charts_NA_october_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_october_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_OCTOBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_october_actionPerformed

   private void Maps_pilot_charts_NA_november_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_november_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_NOVEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_november_actionPerformed

   private void Maps_pilot_charts_NA_december_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NA_december_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NA_DECEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NA_december_actionPerformed

   private void Maps_pilot_charts_SP_january_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_january_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_JANUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_january_actionPerformed

   private void Maps_pilot_charts_SP_february_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_february_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_FEBRUARY; 
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_february_actionPerformed

   private void Maps_pilot_charts_SP_march_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_march_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_MARCH;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_march_actionPerformed

   private void Maps_pilot_charts_SP_april_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_april_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_APRIL;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_april_actionPerformed

   private void Maps_pilot_charts_SP_may_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_may_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_MAY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_may_actionPerformed

   private void Maps_pilot_charts_SP_june_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_june_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_JUNE;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_june_actionPerformed

   private void Maps_pilot_charts_SP_july_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_july_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_JULY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_july_actionPerformed

   private void Maps_pilot_charts_SP_august_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_august_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_AUGUST;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_august_actionPerformed

   private void Maps_pilot_charts_SP_september_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_september_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_SEPTEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_september_actionPerformed

   private void Maps_pilot_charts_SP_october_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_october_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_OCTOBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_october_actionPerformed

   private void Maps_pilot_charts_SP_november_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_november_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_NOVEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_november_actionPerformed

   private void Maps_pilot_charts_SP_december_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_SP_december_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_SP_DECEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_SP_december_actionPerformed

   private void Maps_pilot_charts_NP_january_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_january_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_JANUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_january_actionPerformed

   private void Maps_pilots_charts_NP_february_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilots_charts_NP_february_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_FEBRUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilots_charts_NP_february_actionPerformed

   private void Maps_pilot_charts_NP_march_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_march_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_MARCH;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_march_actionPerformed

   private void Maps_pilot_charts_NP_april_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_april_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_APRIL;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_april_actionPerformed

   private void Maps_pilot_charts_NP_may_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_may_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_MAY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_may_actionPerformed

   private void Maps_pilot_charts_NP_june_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_june_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_JUNE;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_june_actionPerformed

   private void Maps_pilot_charts_NP_july_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_july_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_JULY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_july_actionPerformed

   private void Maps_pilot_charts_NP_august_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_august_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_AUGUST;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_august_actionPerformed

   private void Maps_pilot_charts_NP_september_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_september_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_SEPTEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_september_actionPerformed

   private void Maps_pilot_charts_NP_october_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_october_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_OCTOBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_october_actionPerformed

   private void Maps_pilot_charts_NP_november_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_november_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_NOVEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_november_actionPerformed

   private void Maps_pilot_charts_NP_december_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_NP_december_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_NP_DECEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_NP_december_actionPerformed

   private void Maps_pilot_charts_IN_january_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_january_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_JANUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_january_actionPerformed

   private void Maps_pilot_charts_IN_february_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_february_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_FEBRUARY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_february_actionPerformed

   private void Maps_pilot_charts_IN_march_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_march_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_MARCH;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_march_actionPerformed

   private void Maps_pilot_charts_IN_april_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_april_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_APRIL;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_april_actionPerformed

   private void Maps_pilot_charts_IN_may_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_may_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_MAY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_may_actionPerformed

   private void Maps_pilot_charts_IN_june_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_june_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_JUNE;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_june_actionPerformed

   private void Maps_pilot_charts_IN_july_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_july_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_JULY;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_july_actionPerformed

   private void Maps_pilot_charts_IN_august_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_august_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_AUGUST;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_august_actionPerformed

   private void Maps_pilot_charts_IN_september_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_september_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_SEPTEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_september_actionPerformed

   private void Maps_pilot_charts_IN_october_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_october_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_OCTOBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_october_actionPerformed

   private void Maps_pilot_charts_IN_november_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_november_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_NOVEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_november_actionPerformed

   private void Maps_pilot_charts_IN_december_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_pilot_charts_IN_december_actionPerformed
      // TODO add your handling code here:
      String pilot_charts_mode = PILOT_CHART_IN_DECEMBER;
      Maps_pilot_charts(pilot_charts_mode);
   }//GEN-LAST:event_Maps_pilot_charts_IN_december_actionPerformed

   private void Maps_satellite_image_vis_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Maps_satellite_image_vis_actionPerformed
      // TODO add your handling code here:
      
      String satellite_image_mode = SATELLITE_VIS_IMAGE;
      support_class.determine_satellite_image_url(satellite_image_mode);
   }//GEN-LAST:event_Maps_satellite_image_vis_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void Dashboard_Obs_Stats_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_Obs_Stats_actionPerformed
      // TODO add your handling code here:
      
      obs_stats_mode = OBSERVERS_STATS;
      
      myimmtlogperiod form = new myimmtlogperiod();
      form.setSize(500, 300);
      form.setVisible(true);
   }//GEN-LAST:event_Dashboard_Obs_Stats_actionPerformed

   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Dashboard_Observations_Stats_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dashboard_Observations_Stats_actionPerformed
      // TODO add your handling code here:
      
      obs_stats_mode = OBSERVATIONS_STATS;
      
      myimmtlogperiod form = new myimmtlogperiod();
      form.setSize(500, 300);
      form.setVisible(true); 
   }//GEN-LAST:event_Dashboard_Observations_Stats_actionPerformed

   
   
   private void Info_device_log_menu_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Info_device_log_menu_actionPerformed
      // TODO add your handling code here:
      
      mydevice_log form = new mydevice_log();
      form.setExtendedState(MAXIMIZED_BOTH);                      // full screen
      form.setVisible(true);
   }//GEN-LAST:event_Info_device_log_menu_actionPerformed
  
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/   
   private void Maps_OSM()
   {
      // called from: - Maps_Obs_Manual_Map_Online_actionPerformed() [main.java]
      //              - Maps_Obs_Manual_Map_Offline_actionPerformed() [main.java]
      //              - Maps_AWS_Sensor_Map_Offline_actionPerformed() [main.java]
      //              - Maps_AWS_sensor_Map_Online_actionPerformed() [main.java]
      //              - Maps_AWS_Visual_Map_Offline_actionPerformed() [main.java]
      //              - Maps_AWS_Visual_Map_Online_actionPerformed() [main.java]
      
      
      // for online and offline MAp
      //OSM osm = OSM.getInstance();  // NB https://stackoverflow.com/questions/1441984/how-can-i-know-whether-an-instance-of-a-class-already-exists-in-memory
     
      if (osm_class == null)
      {                                
         osm_class = new OSM();
      }

      // OSM OFFLINE
      //
      if (OSM_mode.equals(OSM_OFFLINE_MANUAL) || OSM_mode.equals(OSM_OFFLINE_AWS_SENSOR) || OSM_mode.equals(OSM_OFFLINE_AWS_VISUAL))
      {
         new SwingWorker<Boolean, Void>()
         {
            @Override
            protected Boolean doInBackground() throws Exception
            {
               boolean doorgaan = false;
               doorgaan = osm_class.OSM_control_center();          // here will be checked if all the OSM files are already present, if not they will be copied from jar to destination

               return doorgaan;
               
            } // protected Void doInBackground() throws Exception

            @Override
            protected void done()
            {
               try
               {
                  boolean doorgaan = get();
               
                  if (doorgaan)
                  {
                     if ((OSM_mode.equals(OSM_OFFLINE_MANUAL) || OSM_mode.equals(OSM_OFFLINE_AWS_VISUAL)))
                     {
                        // IMMT based
                        osm_class.OSM_IMMT_on_leaflet_map();   
                     }
                     else if (OSM_mode.equals(OSM_OFFLINE_AWS_SENSOR))
                     {
                        // sensor data files based
                        osm_class.OSM_AWS_Sensor_data_on_leaflet_map();
                     }
                  }
                  else
                  {
                     String info = "Error when displaying Obs's offline map";
                     main.log_turbowin_system_message("[OSM] " + info);
                     JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
                  } // else
               
               } // try
               catch (InterruptedException | ExecutionException ex) 
               {   
                  System.out.println("+++ Error in Function: Maps_Obs_Map_Offline_actionPerformed. " + ex); 
               }
            } // protected void done()
         }.execute(); // new SwingWorker<Boolean, Void>()
      } // if (OSM_mode.equals(OSM_OFFLINE)) etc.
      
      // OSM ONLINE
      //
      else if (OSM_mode.equals(OSM_ONLINE_MANUAL) || OSM_mode.equals(OSM_ONLINE_AWS_SENSOR) || OSM_mode.equals(OSM_ONLINE_AWS_VISUAL))
      {
         if ((OSM_mode.equals(OSM_ONLINE_MANUAL) || OSM_mode.equals(OSM_ONLINE_AWS_VISUAL)))
         {
            // IMMT based
            osm_class.OSM_IMMT_on_leaflet_map();
         }
         else if (OSM_mode.equals(OSM_ONLINE_AWS_SENSOR))
         {
            // sensor data files based
            osm_class.OSM_AWS_Sensor_data_on_leaflet_map();
         }
      }       
      
      // OSM_mode = unknown
      //
      else
      {
         String info = "Error when displaying Obs's map, unknown OSM display mode";
         main.log_turbowin_system_message("[OSM] " + info);
         JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);      
      } // else
   }

   
      
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/      
   private void main_window_updating_date_time()
   {                                         
      // TODO add your handling code here: 
       
      
      // called from: - main_windowDeiconified() [main.java]   // in case os = NOT WINDOWS
      //              - main_windowIconfied() [main.java]      // in case os = WINDOWS
      
      // NB in case of a connected AWS or barometer a timer will already update the date time field on the main screen
      
      
      if ((RS232_connection_mode == 3) || (RS232_connection_mode == 9) || (RS232_connection_mode == 10) || (RS232_connection_mode == 11) || (APR == true)) // AWS connected or APR
      {
         //System.out.println("+++ " + evt);
      
         String info = "Screen will be updated within max 1 minute";
         
         final JOptionPane pane_begin = new JOptionPane(info, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
         final JDialog updating_dialog = pane_begin.createDialog(main.APPLICATION_NAME);

         Timer timer_begin = new Timer(2000, new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               updating_dialog.dispose();
            }
         });
         timer_begin.setRepeats(false);
         timer_begin.start();
         updating_dialog.setVisible(true); 
      
      }


      // NB in case of a connected AWS or barometer a timer will already update the date time field on the main screen
      
   }       
   
   
   
   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
/*
   private void Output_obs_by_Gmail_FM13()
   {
      // This function called by: Output_obs_by_Gmail_actionPerformed()
      
      // https://dzone.com/articles/sending-mail-using-javamail-api-for-gmail-server
      // https://myaccount.google.com/lesssecureapps
      //
      //
      //
      //
      //

      new SwingWorker<Void, Void>()
      {
         @Override
         protected Void doInBackground() throws Exception
         {
         
            String obs_email_subject_new = obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");
            String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=" + obs_write;
                     
            // NB Use %0A for carriage returns, %20 for spaces [see "urlEncode(obs_write)"]
            // if ddmmyyyy in subject field -> replace by actual utc date of observation 
            //String obs_email_subject_new = urlEncode(obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00"));
            //String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=" + urlEncode(obs_write);
            
            
            // Recipient's email ID needs to be mentioned.
            //String to = "xyz@gmail.com";//change accordingly
            //String to = obs_email_recipient;
            String to = "martin.stam@home.nl";

            // Sender's email ID needs to be mentioned
            //String from = "abc@gmail.com";//change accordingly
            String gmail_from = "turbowin.observations@gmail.com";//change accordingly
            
            
            //final String username = "abc";//change accordingly
            final String gmail_username = "turbowin observations";//change accordingly
            
            //final String password = "*****";//change accordingly
            final String gmail_password = "****!!";//change accordingly

            // Assuming you are sending email through relay.jangosmtp.net
            String host = "smtp.gmail.com";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");   
            
            
            
            //Establishing a session with required user details
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gmail_from, gmail_password);
            }
            });
            
            try 
            {
               //Creating a Message object to set the email content
               MimeMessage msg = new MimeMessage(session);
            
               
               //Storing the comma seperated values to email addresses
               //String to = "recepient1@email.com,recepient2@gmail.com";
               //Parsing the String with defualt delimiter as a comma by marking the boolean as true and storing the email
               
               //addresses in an array of InternetAddress objects
               InternetAddress[] address = InternetAddress.parse(to, true);
               
               //Setting the recepients from the address variable
               msg.setRecipients(Message.RecipientType.TO, address);
               String timeStamp = new SimpleDateFormat("yyyymmdd_hh-mm-ss").format(new Date());
               msg.setSubject("Sample Mail : " + timeStamp);
               msg.setSentDate(new Date());
               msg.setText("Sampel System Generated mail");
               msg.setHeader("XPriority", "1");
               Transport.send(msg);
               System.out.println("Mail has been sent successfully");
            } 
            catch (MessagingException mex) 
            {
               System.out.println("Unable to send an email" + mex);
            }
            
            
            return null;

         } // protected Void doInBackground() throws Exception

         @Override
         protected void done()
         {
            IMMT_log();
         
            Reset_all_meteo_parameters();
         } // protected void done()

      }.execute(); // new SwingWorker<Void, Void>()
   }
*/  
 
  
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
protected static Image createImage(String path) 
   {
      //URL imageURL = TrayIconDemo.class.getResource(path);
      URL imageURL = main.class.getResource(path);
         
      if (imageURL == null) 
      {
         System.out.println("+++ tray icon image resource not found: " + path);
         return null;
      } 
      else 
      {
         return (new ImageIcon(imageURL)).getImage();
      }
   }
      
   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
private void Output_obs_to_clipboard_FM13()
{
   Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
   StringSelection selection = new StringSelection(obs_write);
   clipboard.setContents(selection, null);
   
   IMMT_log();
         
   Reset_all_meteo_parameters();
}
   


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/   
private void Output_obs_to_clipboard_format_101()
{
   boolean doorgaan                 = true;
   String clipboard_format_101_line = "";
   
   
   // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
   clipboard_format_101_line = get_format_101_obs_from_file();
   if (clipboard_format_101_line.equals("") == true)
   {
      doorgaan = false;
   }
   
   if (doorgaan == true)
   {   
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection selection = new StringSelection(clipboard_format_101_line);
      clipboard.setContents(selection, null);
   }
   
   IMMT_log();
         
   Reset_all_meteo_parameters();
}

   
   
/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private String compile_obs_for_AWS()  
{
   String AWS_obs                       = "";
   String AWS_id                        = "";
   String AWS_diff_SLL_WL               = "";
   String AWS_dd                        = "";
   String AWS_ff                        = "";
   String AWS_TTT                       = "";
   String AWS_rh                        = "";
   String AWS_sst                       = "";
   String AWS_VV                        = "";
   String AWS_ww                        = "";
   String AWS_W1                        = "";
   String AWS_W2                        = "";
   String AWS_N                         = "";
   String AWS_Nh                        = "";
   String AWS_Cl                        = "";
   String AWS_Cm                        = "";
   String AWS_Ch                        = "";    
   String AWS_h                         = "";
   String AWS_Pw                        = "";
   String AWS_Hw                        = "";
   String AWS_Dw1                       = "";
   String AWS_Pw1                       = "";
   String AWS_Hw1                       = "";
   String AWS_Dw2                       = "";
   String AWS_Pw2                       = "";
   String AWS_Hw2                       = "";
   String AWS_EsEs                      = "";
   String AWS_Rs                        = "";
   String AWS_Is                        = "";
   String AWS_ci                        = "";
   String AWS_bi                        = "";
   String AWS_zi                        = "";
   String AWS_Si                        = "";
   String AWS_Di                        = "";
  
   
   double double_wind_speed;
   double double_sst;
   double double_air_temp;
   
   
   // See docs: - "EUCAWS inputs/outputs Complementary information about codes by Pierre Blouch"
   //           - "SMD & PSO formats by jean-Baptiste Cohuet" 
   
   
   // AWS NMEA identifier
   //
   AWS_id                      = "$PTBWP";
   
   
   // departure of SLL from the actual sea level (diff SLL - WL) [format sHH; range -10..20; resolution 1; units m]
   //
   AWS_diff_SLL_WL             = diff_sll_wl;                  // global var (set in "Maintenance -> Station data" and "Input -> Wind")
   
   
   // wind direction [format WST; range 10..360; resolution 10; units deg]
   //
   if (true_wind_dir_from_AWS_present == false)               // if parameter is measured by AWS than must be not a part of the string send to the AWS
   {
      if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE)
      {
         AWS_dd = "0";                                       // NB wind dir = variable -> 0 : special for EUCAWS!! 
      }  
      else if (mywind.int_true_wind_dir != INVALID)
      {
         // NB So wind_dir = 0 (calm) also included here
         AWS_dd = Integer.toString(mywind.int_true_wind_dir);
      }
      else
      {
         AWS_dd = "";
      } 
   } // if (true_wind_dir_from_AWS_present == false)
   
   
   // wind speed [format WS.s; range 0..75; resolution 0.1; units m/s]
   //
   if (true_wind_speed_from_AWS_present == false)
   {
      if (mywind.int_true_wind_speed != INVALID) 
      {
         if (main.wind_units.trim().indexOf(main.M_S) != -1) // so wind speed in m/s
         {
            double_wind_speed = mywind.int_true_wind_speed * 1.0;                     // double_wind_speed: units m/s
         } 
         else // so wind speed units knots or wind speed units unknown
         {
            double_wind_speed = mywind.int_true_wind_speed * KNOT_M_S_CONVERSION;     // double_wind_speed: units m/s
         }

         // rounded one digit
         BigDecimal bd = new BigDecimal(double_wind_speed).setScale(1, RoundingMode.HALF_UP);  // one decimal, rounded e.g. 2.12939 -> 2.1
         double_wind_speed = bd.doubleValue();

         AWS_ff = Double.toString(double_wind_speed);
      } 
      else 
      {
         AWS_ff = "";
      }
   } // if (true_wind_speed_from_AWS_present == false)

   
   // air  temperature [format sTA.w; range -60..+60; resolution 0.1; unit C] 
   //
   if (air_temp_from_AWS_present == false)
   {
      if ((mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null))    
      {
        double_air_temp = Double.parseDouble(mytemp.air_temp);   
      
         BigDecimal bd = new BigDecimal(double_air_temp).setScale(1, RoundingMode.HALF_UP);  // one decimal, rounded e.g. 2.12939 -> 2.1
         double_air_temp = bd.doubleValue();
         
         AWS_TTT = Double.toString(double_air_temp);   
      }  
      else
      {
         AWS_TTT = "";
      }
   } // if (air_temp_from_AWS_present == false)
   
   
   // relative humidity [format UUU; range 0..100; resolution 1; unit %]
   //
   if (rh_from_AWS_present == false)
   {
      if ((mytemp.double_rv != main.INVALID))  
      {
         int int_rh = (int)Math.round(mytemp.double_rv * 100);    // rounding and in % (and eg not 100.0 but 100)
      
         if ((int_rh >= 0) && (int_rh <= 100))   
         { 
            AWS_rh = Integer.toString(int_rh);
         }
         else
         {
            AWS_rh = "";
         }
      } // if ((mytemp.double_rv != main.INVALID)) 
      else
      {
         AWS_rh = "";
      }
   } // if (rh_from_AWS_present == false)
   
   
   // sea water temperature [format sTW.w; range -5..45; resolution 0.1; unit C] 
   //
   if (SST_from_AWS_present == false)
   {
      if ((mytemp.sea_water_temp.compareTo("") != 0) && (mytemp.sea_water_temp != null))    
      {
         double_sst = Double.parseDouble(mytemp.sea_water_temp);   
      
         BigDecimal bd = new BigDecimal(double_sst).setScale(1, RoundingMode.HALF_UP);  // one decimal, rounded e.g. 2.12939 -> 2.1
         double_sst = bd.doubleValue();
         
         AWS_sst = Double.toString(double_sst);   
      }  
      else
      {
         AWS_sst = "";
      }
   } // if (SST_from_AWS_present == false)
   
   
   // visibility [format VV; range 0..99; resolution -; units: code]
   //
   if (myvisibility.VV_code.equals("//"))
   {
      AWS_VV = "";
   }   
   else if ((myvisibility.VV_code != null) && (myvisibility.VV_code.compareTo("") != 0))
   {
      AWS_VV = myvisibility.VV_code;
   }
   else
   {
      AWS_VV = "";  
   }
   
   
   // Present Weather [format WW; range 0..99; resolution -; units: bufr code table 020003]
   //
   if (mypresentweather.ww_code.equals("//"))
   {
     AWS_ww = ""; 
   }
   else if ((mypresentweather.ww_code != null) && (mypresentweather.ww_code.compareTo("") != 0))
   {
      AWS_ww = mypresentweather.ww_code;
   }
   else
   {
      AWS_ww = "";
   }
      
   
   // Past weather 1 (W1; bufr table 020004)
   //
   if (mypastweather.W1_code.equals("/"))
   {
      AWS_W1 = "";
   }
   else if ((mypastweather.W1_code != null) && (mypastweather.W1_code.compareTo("") != 0))
   {
      AWS_W1 = mypastweather.W1_code;
   }
   else
   {
      AWS_W1 = "";
   } 
     
      
   // past weather 2 (W2; bufr table 020004)
   //
   if (mypastweather.W2_code.equals("/"))
   {
      AWS_W2 = "";
   }
   else if ((mypastweather.W2_code != null) && (mypastweather.W2_code.compareTo("") != 0))
   {
      AWS_W2 = mypastweather.W2_code;
   }
   else
   {
      AWS_W2 = "";
   } 
    
   
   // total cloud cover (N)
   //
   if (mycloudcover.N_code.equals("/"))
   {
      AWS_N = "";
   }
   else if ((mycloudcover.N_code != null) && (mycloudcover.N_code.compareTo("") != 0))
   {
      AWS_N = mycloudcover.N_code;
   }
   else
   {
      AWS_N = ""; 
   }
   
   
   // Cloud amount Cl/Cm (Nh) [bufr table 020011]
   //
   if (mycloudcover.Nh_code.equals("/"))
   {
      AWS_Nh = "";
   }
   else if ((mycloudcover.Nh_code != null) && (mycloudcover.Nh_code.compareTo("") != 0))
   {
      AWS_Nh = mycloudcover.Nh_code;
   }
   else
   {
      AWS_Nh = ""; 
   }
         
   
   // clouds low (Cl) [bufr table 020012]
   //
   if (mycl.cl_code.equals("/"))
   {
      AWS_Cl = "";
   }
   else if ((mycl.cl_code != null) && (mycl.cl_code.compareTo("") != 0))
   {
      //AWS_Cl = mycl.cl_code;
      try
      {
         int hulp_cl = Integer.parseInt(mycl.cl_code) + 30;    // add 30, see bufr table 020012 
         AWS_Cl = Integer.toString(hulp_cl);
      }
      catch (NumberFormatException ex)
      {
         AWS_Cl = "";
         System.out.println("+++ Error compile obs for AWS; cloud type low (Cl) " + ex);
      } // catch      
   }   
   else
   {
      AWS_Cl = "";
   } 
      
      
   // clouds middle (Cm) [bufr table 020012]
   //
   if (mycm.cm_code.equals("/"))
   {
      AWS_Cm = "";
   }
   else if ((mycm.cm_code != null) && (mycm.cm_code.compareTo("") != 0))
   {
      //AWS_Cm = mycm.cm_code.substring(0, 1);// omdat bij cm_code in geval Cm7 een a, b, c er achter staat (dus 7a, 7b, 7c)
      try
      {
         int hulp_cm = Integer.parseInt(mycm.cm_code.substring(0, 1)) + 20;    // add 20, see bufr table 020012 
         AWS_Cm = Integer.toString(hulp_cm);
         // NB because if Cm code = Cm7 there is an addition a, b, c (so 7a, 7b, 7c)
      }
      catch (NumberFormatException ex)
      {
         AWS_Cm = "";
         System.out.println("+++ Error compile obs for AWS; cloud type middle (Cm) " + ex);
      } // catch    
   }    
   else
   {
      AWS_Cm = "";
   } 
      
      
   // clouds high (Ch) [bufr table 020012]
   //
   if (mych.ch_code.equals("/"))
   {
      AWS_Ch = "";
   }
   else if ((mych.ch_code != null) && (mych.ch_code.compareTo("") != 0))
   {
      //AWS_Ch = mych.ch_code;
      try
      {
         int hulp_ch = Integer.parseInt(mych.ch_code) + 10;    // add 10, see bufr table 020012 
         AWS_Ch = Integer.toString(hulp_ch);
      }
      catch (NumberFormatException ex)
      {
         AWS_Ch = "";
         System.out.println("+++ Error compile obs for AWS; cloud type high (Ch) " + ex);
      } // catch     
   }
   else
   {
      AWS_Ch = "";
   }   

   
   // height of base of lowest clouds (h)
   //
   if (mycloudcover.h_code.equals("/"))
   {
      AWS_h = "";
   }
   else if ((mycloudcover.h_code != null) && (mycloudcover.h_code.compareTo("") != 0))
   {
      AWS_h = mycloudcover.h_code;
   }
   else
   {
      AWS_h = "";
   } 
   
   
   // Pw (period wind waves)
   //
   if (mywaves.Pw_code.equals("//"))
   {
      AWS_Pw = "";
   }
   else if (mywaves.Pw_code.equals("99"))
   {
      AWS_Pw = "";                                 // EUCAWS (Bufr) cannot handle 99 so agreed this will become ""
   }
   else if ( (mywaves.Pw_code != null) && (mywaves.Pw_code.compareTo("") != 0) )
   {   
      // period < 10 sec than skip leading 0
      //if (Integer.parseInt(mywaves.Pw_code) >= 10)
      //{
      //   AWS_Pw = mywaves.Pw_code.substring(1, 1);
      //}
      //else
      //{
      //   AWS_Pw = mywaves.Pw_code;
      //}
      // skip if present the leading zero
      int int_Pw = Integer.parseInt(mywaves.Pw_code);
      AWS_Pw = Integer.toString(int_Pw);
   }
   else
   {
      AWS_Pw = "";
   } 
   
   
   // Hw (height of wind waves)
   //
   if (mywaves.Hw_code.equals("//"))
   {
      AWS_Hw = "";
   }
   else if (mywaves.Hw_code.equals("99"))
   {
      AWS_Hw = "";
   }
   else if ( (mywaves.Hw_code != null) && (mywaves.Hw_code.compareTo("") != 0) )
   {   
      //AWS_Hw = mywaves.Hw_code;
      
      double double_Hw = Double.parseDouble(mywaves.Hw_code) / 2;   // eg 03 in FM13 code -> 1.5 m
      
      BigDecimal bd = new BigDecimal(double_Hw).setScale(1, RoundingMode.HALF_UP);  // one decimals, rounded e.g. 0.50000 -> 0.5
      double_Hw = bd.doubleValue();
         
      AWS_Hw = Double.toString(double_Hw);   
   }
   else
   {
      AWS_Hw = "";
   } 
  
   
   // dw1 (direction of first swell)
   // 
   if (mywaves.Dw1_code.equals("//"))
   {
      AWS_Dw1 = "";
   }
   else if (mywaves.Dw1_code.equals("99"))
   {
      AWS_Dw1 = "";
   }
   else if ( (mywaves.Dw1_code != null) && (mywaves.Dw1_code.compareTo("") != 0) )
   {   
      AWS_Dw1 = mywaves.Dw1_code + "0";
   }
   else
   {
      AWS_Dw1 = "";
   } 
   
   
   // Pw1 (period of first swell)
   //
   if (mywaves.Pw1_code.equals("//"))
   {
      AWS_Pw1 = "";
   }
   else if (mywaves.Pw1_code.equals("99"))
   {
      AWS_Pw1 = "";
   }
   else if ( (mywaves.Pw1_code != null) && (mywaves.Pw1_code.compareTo("") != 0) )
   {   
      // period < 10 sec? than skip leading 0
      //if (Integer.parseInt(mywaves.Pw1_code) >= 10)
      //{
       //  AWS_Pw1 = mywaves.Pw1_code.substring(1, 1); 
      //}
      //else
      //{
      //   AWS_Pw1 = mywaves.Pw1_code;
      //}
      int int_Pw1 = Integer.parseInt(mywaves.Pw1_code);
      AWS_Pw1 = Integer.toString(int_Pw1);
   }
   else
   {
      AWS_Pw1 = "";
   } 
   
   
   // Hw1 (height of first swell)
   //
   if (mywaves.Hw1_code.equals("//"))
   {
      AWS_Hw1 = "";
   }
   else if (mywaves.Hw1_code.equals("99"))
   {
      AWS_Hw1 = "";
   }
   else if ( (mywaves.Hw1_code != null) && (mywaves.Hw1_code.compareTo("") != 0) )
   {   
      //AWS_Hw1 = mywaves.Hw1_code;
      
      double double_Hw1 = Double.parseDouble(mywaves.Hw1_code) / 2;   // eg 03 in FM13 code -> 1.5 m
      
      BigDecimal bd = new BigDecimal(double_Hw1).setScale(1, RoundingMode.HALF_UP);  // one decimals, rounded e.g. 0.50000 -> 0.5
      double_Hw1 = bd.doubleValue();
         
      AWS_Hw1 = Double.toString(double_Hw1);   
   }
   else
   {
      AWS_Hw1 = "";
   } 
   
   
   // Dw2 (direction of second swell)
   //
   if (mywaves.Dw2_code.equals("//"))
   {
      AWS_Dw2 = "";
   }
   else if (mywaves.Dw2_code.equals("99"))
   {
      AWS_Dw2 = "";
   }
   else if ( (mywaves.Dw2_code != null) && (mywaves.Dw2_code.compareTo("") != 0) )
   {   
      AWS_Dw2 = mywaves.Dw2_code + "0";
   }
   else
   {
      AWS_Dw2 = "";
   }    
   
   
   // Pw2 (period of second swell)
   // 
   if (mywaves.Pw2_code.equals("//"))
   {
      AWS_Pw2 = "";
   }
   else if (mywaves.Pw2_code.equals("99"))
   {
      AWS_Pw2 = "";
   }
   else if ( (mywaves.Pw2_code != null) && (mywaves.Pw2_code.compareTo("") != 0) )
   {   
      // period < 10 sec than skip leading 0
      //if (Integer.parseInt(mywaves.Pw2_code) >= 10)
      //{
      //  AWS_Pw2 = mywaves.Pw2_code.substring(1, 1); 
      //}
      //else
      //{
      //   AWS_Pw2 = mywaves.Pw2_code;
      //}
      int int_Pw2 = Integer.parseInt(mywaves.Pw2_code);
      AWS_Pw2 = Integer.toString(int_Pw2);
   }
   else
   {
      AWS_Pw2 = "";
   } 
   
   
   // Hw2 (height of second swell)
   //
   if (mywaves.Hw2_code.equals("//"))
   {
      AWS_Hw2 = "";
   }
   else if (mywaves.Hw2_code.equals("99"))
   {
      AWS_Hw2 = "";
   }
   else if ( (mywaves.Hw2_code != null) && (mywaves.Hw2_code.compareTo("") != 0) )
   {   
      double double_Hw2 = Double.parseDouble(mywaves.Hw2_code) / 2;   // eg 03 in FM13 code -> 1.5 m
      
      BigDecimal bd = new BigDecimal(double_Hw2).setScale(1, RoundingMode.HALF_UP);  // one decimals, rounded e.g. 0.50000 -> 0.5
      double_Hw2 = bd.doubleValue();
         
      AWS_Hw2 = Double.toString(double_Hw2);   
   }
   else
   {
      AWS_Hw2 = "";
   } 

   
   // ice deposit (thickness)
   //
   if (myicing.EsEs_code.equals("//"))
   {
      AWS_EsEs = "";
   }
   else if ( (myicing.EsEs_code != null) && (myicing.EsEs_code.compareTo("") != 0) )
   {
      double double_EsEs = Double.parseDouble(myicing.EsEs_code) / 100;   // eg 04 in FM13 code (4 cm) -> 0.04 m
      
      BigDecimal bd = new BigDecimal(double_EsEs).setScale(2, RoundingMode.HALF_UP);  // two decimals, rounded e.g. 0.12939 -> 0.13
      double_EsEs = bd.doubleValue();
         
      AWS_EsEs = Double.toString(double_EsEs);   
   }
   else
   {
      AWS_EsEs = "";
   }
   
   
   // rate of ice accretion (Rs) [bufr table 020032]
   //
   if (myicing.Rs_code.equals("/"))
   {
      AWS_Rs = "";
   }
   else if ( (myicing.Rs_code != null) && (myicing.Rs_code.compareTo("") != 0) )
   {
      AWS_Rs = myicing.Rs_code;
   }  
   else
   {
      AWS_Rs = "";
   }   
   
   
   // cause of ice accretion (Is) [bufr table 020033]
   //
   if (myicing.Is_code.equals("/"))
   {
      AWS_Is = "";
   }
   else if ( (myicing.Is_code != null) && (myicing.Is_code.compareTo("") != 0) )
   {
      if (myicing.Is_code.equals("1"))               // icing from spray (FM13 code)
      {
         AWS_Is = "8";                               // BUFR table 020033-equivalent (see "EUCAWS inputs/outputs complementary information about codes", Pierre Blouch)
      }
      else if (myicing.Is_code.equals("2"))          // icing from fog (FM13 code)
      {
         AWS_Is = "4";                               // BUFR table 020033 equivalent (see "EUCAWS inputs/outputs complementary information about codes", Pierre Blouch)
      }
      else if (myicing.Is_code.equals("3"))          // icing from spray and fog (FM13 code)
      {
         AWS_Is = "12";                              // BUFR table 020033-equivalent (see "EUCAWS inputs/outputs complementary information about codes", Pierre Blouch)
      }
       else if (myicing.Is_code.equals("4"))         // icing from rain (FM13 code)
      {
         AWS_Is = "2";                               // BUFR table 020033-equivalent (see "EUCAWS inputs/outputs complementary information about codes", Pierre Blouch)
      }
        else if (myicing.Is_code.equals("5"))        // icing from spray and rain (FM13 code)
      {
         AWS_Is = "10";                              // BUFR table 020033-equivalent (see "EUCAWS inputs/outputs complementary information about codes", Pierre Blouch)
      }
      else if (myicing.Is_code.equals("6"))          // icing from fog and rain (not present in FM13 code)
      {
         AWS_Is = "6";                               // BUFR table 020033-equivalent (see "EUCAWS inputs/outputs complementary information about codes", Pierre Blouch)
      }
      else if (myicing.Is_code.equals("14"))         // icing from spray and fog and rain (not present in FM13 code)
      {
         AWS_Is = "14";                              // BUFR table 020033-equivalent (see "EUCAWS inputs/outputs complementary information about codes", Pierre Blouch)
      }
      else
      {
         AWS_Is = "";
      }
   }  
   else
   {
      AWS_Is = "";
   }      
   
   
   // sea ice concentration (ci) [bufr table 020034]
   //
   if (myice1.ci_code.equals("/"))
   {
      AWS_ci = "";
   }
   else if (myice1.ci_code.equals("u"))                       // internal code used by TurboWin+ ("unable to report because of ......")
   {
      AWS_ci = "14";
   }
   else if ( (myice1.ci_code != null) && (myice1.ci_code.compareTo("") != 0) )
   {
      AWS_ci = myice1.ci_code;
   }
   else
   {
      AWS_ci = "";
   }
   
   
   // amount and type of ice (bi) [bufr table 020035]
   //
   if (myice1.bi_code.equals("/"))
   {
      AWS_bi = "";
   }
   else if (myice1.bi_code.equals("u"))                       // internal code used by TurboWin+ ("unable to report because of ......")
   {
      AWS_bi = "14";
   }
   else if ( (myice1.bi_code != null) && (myice1.bi_code.compareTo("") != 0) )
   {
      AWS_bi = myice1.bi_code;
   }
   else
   {
      AWS_bi = "";
   }   
   
   
   // ice situation (zi) [bufr table 020036]
   //
   if (myice1.zi_code.equals("/"))
   {
      AWS_zi = "";
   }
   else if (myice1.zi_code.equals("u"))                       // internal code used by TurboWin+ ("unable to report because of ......")
   {
      AWS_zi = "30";
   }
   else if ( (myice1.zi_code != null) && (myice1.zi_code.compareTo("") != 0) )
   {
      AWS_zi = myice1.zi_code;
   }
   else
   {
      AWS_zi = "";
   }   
   
   
   // ice development (Si) [bufr table 020037]
   //
   if (myice1.Si_code.equals("/"))
   {
      AWS_Si = "";
   }
   else if (myice1.Si_code.equals("u"))                       // internal code used by TurboWin+ ("unable to report because of ......")
   {
      AWS_Si = "30";
   }
   else if ( (myice1.Si_code != null) && (myice1.Si_code.compareTo("") != 0) )
   {
      AWS_Si = myice1.Si_code;
   }
   else
   {
      AWS_Si = "";
   }   
   
   
   // bearing of ice edge (Di) [bufr id 020038 NO TABLE]
   //
   if (myice1.Di_code.equals("/"))
   {
      AWS_Di = "";
   }
   else if (myice1.Di_code.equals("u"))                       // internal code used by TurboWin+ ("unable to report because of ......")
   {
      AWS_Di = "";                                            // there is no code table, only direction, no support for "unable to report...etc"
   }
   else if ( (myice1.Di_code != null) && (myice1.Di_code.compareTo("") != 0) )
   {
      if (myice1.Di_code.equals("0"))               // ship in shore or flaw lead (FM13 code)
      {
         AWS_Di = "";                                // see EUCAWS inputs/outputs Complementary information about codes", Pierre Blouch)
      }
      else if (myice1.Di_code.equals("1")) 
      {
         AWS_Di = "45";
      }
      else if (myice1.Di_code.equals("2")) 
      {
         AWS_Di = "90";
      }
      else if (myice1.Di_code.equals("3")) 
      {
         AWS_Di = "135";
      }
      else if (myice1.Di_code.equals("4")) 
      {
         AWS_Di = "180";
      }
      else if (myice1.Di_code.equals("5")) 
      {
         AWS_Di = "225";
      }
      else if (myice1.Di_code.equals("6")) 
      {
         AWS_Di = "270";
      }
      else if (myice1.Di_code.equals("7")) 
      {
         AWS_Di = "315";
      }
      else if (myice1.Di_code.equals("8")) 
      {
         AWS_Di = "360";
      }
      else
      {
         AWS_Di = "";
      }
   }
   else
   {
      AWS_Di = "";
   }   
   
   
   // compose AWS string
   //
   AWS_obs = AWS_id + "," +                   
             AWS_diff_SLL_WL + "," +                
             AWS_dd + "," +                  
             AWS_ff + "," +    
             AWS_TTT + "," + 
             AWS_rh + "," + 
             AWS_sst + "," +                       
             AWS_VV + "," +                
             AWS_ww + "," +                        
             AWS_W1 + "," +                       
             AWS_W2 + "," +                        
             AWS_N + "," +                         
             AWS_Nh + "," +                        
             AWS_Cl + "," +                        
             AWS_Cm + "," +                        
             AWS_Ch + "," +                            
             AWS_h + "," +                         
             AWS_Pw + "," +                        
             AWS_Hw + "," +                        
             AWS_Dw1 + "," +                       
             AWS_Pw1 + "," +                       
             AWS_Hw1 + "," +                       
             AWS_Dw2 + "," +                       
             AWS_Pw2 + "," +                       
             AWS_Hw2 + "," +                       
             AWS_EsEs + "," +                      
             AWS_Rs + "," +                        
             AWS_Is + "," +                        
             AWS_ci + "," +                        
             AWS_bi + "," +                        
             AWS_zi + "," +                        
             AWS_Si + "," +                        
             AWS_Di;                       
   
   
        
  // voor testen
  //JOptionPane.showMessageDialog(null, AWS_obs  , APPLICATION_NAME + " AWS_obs", JOptionPane.INFORMATION_MESSAGE);

   
   
   return AWS_obs;
}   
   




/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void set_APR_toolbar()
{
   // check APR
   if (APR)
   {
      jCheckBox1.setSelected(true);
   }
   else 
   {
      jCheckBox1.setSelected(false);
   }
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void set_AWSR_toolbar()
{
   // check AWSR
   if (AWSR)
   {
      jCheckBox2.setSelected(true);
   }
   else 
   {
      jCheckBox2.setSelected(false);
   }
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void initComponents2()
{
   // functions additional/supporting to this main class
   support_class = new main_support();
   
   /* title of main screen */
   setTitle(APPLICATION_NAME);                   // fixed
   
   /* fixed text bottom screen (e.g. Turboin+ stand-alone mode...) */
   jLabel4.setText(APPLICATION_NAME);
   
   /* set main application icon (top-left in title bar) */
   setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(main.ICONS_DIRECTORY + "tray.png")));

   /* status field (NB can be over written with different Themes) */
   jTextField4.setBackground(new java.awt.Color(204, 255, 255));   // Cyan 
   
   jTextField4.setName("fm13_field");

   /* create pop-up menu (right mouse button) */
   //create_popup_menu();
   // NB moved to lees_configuratie_regels() [main.java] and read_muffin() [main.java]
   
   /* determine the OS this program is running on */
   String os = OSDetector.getOSString();
   /* data directory */
   if (os.equals("WINDOWS")) {
      data_dir = "C:" + java.io.File.separator + "ProgramData" + java.io.File.separator + "TurboWinPlus";
   } else {
      data_dir = java.io.File.separator + "opt" + java.io.File.separator + "turbowinplus" + java.io.File.separator + "data";
   }
   //log_turbowin_system_message("[GENERAL] data dir:" + data_dir);
   //JOptionPane.showMessageDialog(null, data_dir, "data_dir", JOptionPane.INFORMATION_MESSAGE);
   System.out.println("data dir = " + data_dir);

   // for turbowin system logs
   sdf_tsl_1 = new SimpleDateFormat("MMM_yyyy");                                // e.g. JAN_2016 (part of the file name)
   sdf_tsl_1.setTimeZone(TimeZone.getTimeZone("UTC"));
   
   sdf_tsl_2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");                    // e.g. 09-Jan-2016 12:23:33 (time stamp of the recoreded messages)
   sdf_tsl_2.setTimeZone(TimeZone.getTimeZone("UTC"));
   
   
   // initialisation
   Reset_all_meteo_parameters();
   
   // NB in via Reset_all_meteo_parameters() the status line (JTextField4) was set literally to "undefined") (because the selection criterium -AWS connected'is there still not determined0
   jTextField4.setText(""); 
   
   /* check if the application is online or offline (different help file handling)  */
   //
   // NB werken via bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
   //               en hierna if (bs.isOffline() == true) gaat niet goed
   // deze methode kan niet goed online of offline bepalen (staat ook in API documentatie)
   // API: "The return value is does not have to be guaranteed to be reliable, as it is sometimes difficult to ascertain the true online / offline state of a client system."
   //

   // initialisation
   offline_mode = true;
   offline_mode_via_jnlp = false;
   offline_mode_via_cmd = true;
   
   // turbowin jnlp offline file present? (turbowin_jws_offline.jnlp)
   String volledig_path_jnlp_offline_file = data_dir + java.io.File.separator + JNLP_OFFLINE_FILE;
   File jnlp_offline_file = new File(volledig_path_jnlp_offline_file);
   if (jnlp_offline_file.exists())
   {
      // So file turbowin_jws_offline.jnlp exists (TurboWeb online version: than this file wil not be present) 
      offline_mode = true;
      offline_mode_via_jnlp = true;
   }
   
   // turbowin cmd or launcher file present? ("turbowin_plus_offline.cmd" or "turbowin_launcher.bat" or "turbowin_launcher")
   String volledig_path_cmd_offline_file = data_dir + java.io.File.separator + CMD_OFFLINE_FILE;
   File cmd_offline_file = new File(volledig_path_cmd_offline_file);
   
   String volledig_path_turbowin_launcher_file = data_dir + java.io.File.separator + TURBOWIN_LAUNCHER_FILE;
   File turbowin_launcher_file = new File(volledig_path_turbowin_launcher_file);
   
   String volledig_path_turbowin_launcher_file_linux = data_dir + java.io.File.separator + TURBOWIN_LAUNCHER_FILE_LINUX;
   File turbowin_launcher_file_linux = new File(volledig_path_turbowin_launcher_file_linux);
 
   //System.out.println("calculated turbowin_launcher_file path = " + turbowin_launcher_file);
   
   if (cmd_offline_file.exists() || turbowin_launcher_file.exists() || turbowin_launcher_file_linux.exists())   
   {
      //System.out.println(cmd_offline_file + " or " + turbowin_launcher_file + "found"); 
      
      // So file "turbowin_plus_offline.cmd" or "turbowin_launcher.bat" or "turbowin_launcher" exists (TurboWeb online version: than this file wil not be present) 
      offline_mode = true;
      offline_mode_via_cmd = true;
   }
   
   
   /* always for offline mode !!! fixed sub dir logs and sub dir amver(not user configurable) */
   if (offline_mode == true)
   {
      // logs sub dir
      //
      // NB logs dir fixed for offline mode (sub dir of main dir -main dir is the dir where jar file is located-)
      logs_dir = data_dir + java.io.File.separator + OFFLINE_LOGS_DIR;
      //JOptionPane.showMessageDialog(null, logs_dir, main.APPLICATION_NAME + " logs_dir test", JOptionPane.WARNING_MESSAGE);

      /* check sub dir logs already present, if not -> create */
      final File dirs = new File(logs_dir);
      if (dirs.exists() == false)
      {
         final boolean success = dirs.mkdirs();
         if (success == false)
         {
            JOptionPane.showMessageDialog(null, "Could not create " + logs_dir + ", disk write protected or no permission to write", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         }
      } // if (dirs.exists() == false)
      
      // if not done before, create sub-sub dir "turbowin_system" (logs\turbowin_system) (NB in case of online(web) mode this will be done in OK_button_actionPerformed() [mylogfiles.java])
      //
      if (dirs.isDirectory()) 
      {
         // create sub dir TURBOWIN_SYSTEM_LOGS (turbowin_system)
         String turbowin_system_logs_dir = main.logs_dir + java.io.File.separator + main.TURBOWIN_SYSTEM_LOGS_DIR;
         final File dir_turbowin_system_logs = new File(turbowin_system_logs_dir);   
         if (dir_turbowin_system_logs.exists() == false)
         {
            dir_turbowin_system_logs.mkdir();   
            log_turbowin_system_message("[GENERAL] created dir " + turbowin_system_logs_dir);
         }
      } //  if (dirs.isDirectory()) 
      
      
      // amver sub dir
      //
      // NB amver dir fixed for offline mode (sub dir of main dir -main dir is the dir where jar file is located-)
      String amver_dir = data_dir + java.io.File.separator + OFFLINE_AMVER_DIR;

      /* check sub dir amver already present, if not -> create */
      final File dirs_amver = new File(amver_dir);
      if (dirs_amver.exists() == false)
      {
         final boolean success = dirs_amver.mkdirs();
         if (success == false)
         {
            JOptionPane.showMessageDialog(null, "Could not create " + amver_dir + ", disk write protected or no permission to write", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         }
      } // if (dirs.exists() == false)

   } // if (offline_mode == true)
   
   
   if (offline_mode == true)
   {
      /* So file turbowin_jws_offline.jnlp and/or turbowin_plus_offline.cmd exists (TurboWeb online version: than these files wil not be present) */
      //offline_mode = true;

      /* gray (disable) the "output -> obs to server (internet)" menu selection option */   // see below now it is also an option for offline mode
      //jMenuItem20.setEnabled(false);

      /* gray (disable) the "Info -> Statistics(internet)" menu selection option */  
      //jMenuItem36.setEnabled(false);
      
      /* set label on bottom main screen */
      //application_mode =  "stand-alone mode";
      application_mode =  "";
      jLabel4.setText(APPLICATION_NAME + " " + application_mode); // NB can later in this start up process be overwritten if a barometer or AWS is coupled

      /* NB logs dir fixed for offline mode (sub dir of main dir -main dir is the dir where the jar file is located-) */
      /* see function: meta_data_from_configuration_regels_into_global_vars() */
   } 
   else
   {
      /* set label on bottom main screen */
      application_mode =  "web mode";
      jLabel4.setText(APPLICATION_NAME + " " + application_mode);
   }
   
   // Obs to server
   //if ((obs_format.equals(FORMAT_FM13)) && (offline_mode == true))
   //{
   //   // NB FM13 only "obs to server" in online mode, in case of format 101 "obs to server" is an output option in both modes (online/web and offline)
   //   // gray (disable) the "output -> obs to server (internet)" menu selection option
   //   jMenuItem20.setEnabled(false);
   //}
   
   // initialisation
   mode_grafiek = MODE_PRESSURE;
   
   // initialisation graph form (for barometer or AWS connected)
   graph_form = null;
   
   // all specific RS232 and RS422 functions
   RS232_RS422 = new main_RS232_RS422();
   RS232_mintaka_class = new RS232_mintaka();
   RS232_vaisala_class = new RS232_vaisala();
   
   // for hybrid and radar dashboard
   myship = null;
   
   /* read stored meta (station) data from muffins or from configuration files */
   if (offline_mode_via_cmd == true) // offline mode
   {
	   // check only one instance running (but not if this main class was created again due to a Theme change)
      if (!theme_changed)
      {
         int port_for_checking_instances = PORT;  // pORT is the default (cconstant)
                 
	   	try 
         {
            // NB PORT = 12345 at start up (= randomly chosen big number)
            //    can be over ruled by the first argument at command line at start up (see main)
            if (PORT_command_line.equals("") == false)
            {
               try 
               {
                  port_for_checking_instances = Integer.parseInt(PORT_command_line);
               } 
               catch (NumberFormatException e) 
               {
                  JOptionPane.showMessageDialog(null, "command line argument PORT number not OK", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  port_for_checking_instances = PORT;
               }              
            } // if (PORT_command_line.equals(""))  
               
            System.out.println("--- server port for checking multiple instances running = " + port_for_checking_instances);
            
			   //s = new ServerSocket(PORT, 10, InetAddress.getLocalHost());
            s = new ServerSocket(port_for_checking_instances, 10, InetAddress.getLocalHost());
		   } 
         catch (UnknownHostException e) 
         {
			  // shouldn't happen for localhost
		   } 
         catch (IOException e)
         {
			   // port taken, so app is already running
            JOptionPane.showMessageDialog(null, "TurboWin+ is already running", main.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE); 
			   System.exit(0);
		   }
      } // if (!theme_changed)
      
      // read stored meta data
      lees_configuratie_regels();          
   }
   else // so offline_via_jnlp mode or online (webstart) mode
   {
      // only jnlp mode can use the single instance running check 
      //
      // in offline mode: So by removing the file turbowin_plus_offline.cmd and invoking turbowin+ via turbowin_jws_offline.jnlp there will be only a single instance running check
      // online mode: is always started via the jnlp file -> always single instance running check
      //
      //
      
      // check only one instance running  (but not if this main class was created again due to a Theme change)
      if (!theme_changed)
      {
         //try 
         //{ 
         //   sis = (SingleInstanceService)ServiceManager.lookup("javax.jnlp.SingleInstanceService");
         //} 
         //catch (UnavailableServiceException e) { sis = null; } 
      
         // Register the single instance listener at the start of the application
         //if (sis != null)
         //{
         //   sisL = new SISListener();
         //   sis.addSingleInstanceListener(sisL);       
         //}
      } // if (!theme_changed)
      
      // read stored station data
      //
      //read_muffin();
   } // else  
   
   
   // get the systemTrays instance
   if (!SystemTray.isSupported()) 
   {
      log_turbowin_system_message("[GENERAL] SystemTray is not supported");
   }
   else
   {   
      tray = SystemTray.getSystemTray();
   }   
   
    
   // check wind speed units source in AWS mode
   // NB not available in this stage so see: check_meta_data() [main.java]
   
   // determine the screen resolution is greather than HD (1920x1080), useful for drawing ships on dashboards
   //display_resolution_greather_than_HD = main_support.determine_screen_size();
   
}  



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void create_popup_menu()
{
   /* create pop-up menu (right mouse button) */
   popup_input = new JPopupMenu();
      
   JMenuItem menuItem301 = new JMenuItem("Date & Time...");
   menuItem301.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_DateTime_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem301);    
      
   JMenuItem menuItem302 = new JMenuItem("Position, Course & Speed...");
   menuItem302.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_Position_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem302);    
   
   JMenuItem menuItem303 = new JMenuItem("Barometer reading...");
   menuItem303.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_Barometer_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem303);    
   
   JMenuItem menuItem304 = new JMenuItem("Barograph reading...");
   menuItem304.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_Barograph_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem304);    
   
   JMenuItem menuItem305 = new JMenuItem("Temperatures...");
   menuItem305.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_Temperatures_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem305);    
   
   JMenuItem menuItem306 = new JMenuItem("Wind...");
   menuItem306.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_Wind_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem306);    
   
   //if (!main.GUI_mode.equals(main.GUI_LIGHT))
   //{
      JMenuItem menuItem307 = new JMenuItem("Waves...");
      menuItem307.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            Input_waves_menu_actionPerformed(null);
         }
      });
      popup_input.add(menuItem307);    
   
      JMenuItem menuItem308 = new JMenuItem("Visibility...");
      menuItem308.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            Input_Visibility_menu_actionPerformed(null);
         }
      });
      popup_input.add(menuItem308); 

      JMenuItem menuItem309 = new JMenuItem("Present weather...");
      menuItem309.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            Input_Presentweather_menu_actionPerformed(null);
         }
      });
      popup_input.add(menuItem309);   
   
      JMenuItem menuItem310 = new JMenuItem("Past weather...");
      menuItem310.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            Input_Pastweather_menu_actionperformed(null);
         }
      });
      popup_input.add(menuItem310);    
   
      JMenuItem menuItem311 = new JMenuItem("Clouds low...");
      menuItem311.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            Input_Cloudslow_menu_actionPerformed(null);
         }
      });
      popup_input.add(menuItem311);    
   
      JMenuItem menuItem312 = new JMenuItem("Clouds middle...");
      menuItem312.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            Input_Cloudsmiddle_menu_actionPerformed(null);
         }
      });
      popup_input.add(menuItem312);    
   
      JMenuItem menuItem313 = new JMenuItem("Clouds high...");
      menuItem313.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            Input_Cloudshigh_menu_actionPerformed(null);
         }
      });
      popup_input.add(menuItem313);    
   
      JMenuItem menuItem314 = new JMenuItem("Cloud cover & height...");
      menuItem314.addActionListener(new java.awt.event.ActionListener() 
      {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            Input_Cloudcover_menu_actionPerformed(null);
         }
      });
      popup_input.add(menuItem314);    
   //} // if (!main.GUI_mode.equals(main.GUI_LIGHT))
   
   
   JMenuItem menuItem315 = new JMenuItem("Icing...");
   menuItem315.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_Icing_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem315);    
   
   JMenuItem menuItem316 = new JMenuItem("Ice...");
   menuItem316.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_Ice_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem316);    
   
   JMenuItem menuItem317 = new JMenuItem("Observer...");
   menuItem317.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Input_Observer_menu_actionPerformed(null);
      }
   });
   popup_input.add(menuItem317);  
   
   popup_input.addSeparator();
   
   JMenuItem menuItem318 = new JMenuItem("Day colours");
   menuItem318.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Themes_1_actionPerformed(null);
      }
   });
   popup_input.add(menuItem318);  
   
   JMenuItem menuItem319 = new JMenuItem("Night colours");
   menuItem319.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Themes_2_actionPerformed(null);
      }
   });
   popup_input.add(menuItem319);  
   
   JMenuItem menuItem320 = new JMenuItem("Sunrise colours");
   menuItem320.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Themes_3_actionPerformed(null);
      }
   });
   popup_input.add(menuItem320);  
   
   JMenuItem menuItem321 = new JMenuItem("Sunset colours");
   menuItem321.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Themes_4_actionPerformed(null);
      }
   });
   popup_input.add(menuItem321);  
   
   JMenuItem menuItem322 = new JMenuItem("Transparent");
   menuItem322.addActionListener(new java.awt.event.ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         Themes_5_actionPerformed(null);
      }
   });
   popup_input.add(menuItem322); 
   
   
   MouseListener popupListener_input = new PopupListener_input();
   addMouseListener(popupListener_input);                              // connect to jFrame otherwise eg: jTextField1.addMouseListener(popupListener);
   jToolBar1.addMouseListener(popupListener_input);                    // also connected to Toolbar now
   
   // in 'gui light' mode, by default, the logo (Label13 reused) do not respond to right mouse click
   //if (GUI_mode.equals(GUI_LIGHT))
   //{
   //   // in GUI LIGHT mode label13 (present weather in FULL mode) was altered to the chosen logo (eumetnet, noaa, sot)
   //   jLabel13.addMouseListener(popupListener_input);
   //}
}






/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
//private void IMMT_log()
public static void IMMT_log()        
{
   final String SPATIE_1 = " ";
   final String SPATIE_2 = "  ";
   final String SPATIE_3 = "   ";
   final String SPATIE_4 = "    ";
   final String SPATIE_5 = "     ";
   final String SPATIE_6 = "      ";
   final String SPATIE_7 = "       ";


	String immt_rec = "";                                   
	String Qc_1  = "9";                                      // 9 = the value of the element is missing
	String Qc_2  = "9";
	String Qc_3  = "9";
	String Qc_4  = "9";
	String Qc_5  = "9";
	String Qc_6  = "9";
	String Qc_7  = "9";
	String Qc_8  = "9";
	String Qc_9  = "9";
	String Qc_10 = "9";
	String Qc_11 = "9";
	String Qc_12 = "9";
	String Qc_13 = "9";
	String Qc_14 = "9";
	String Qc_15 = "9";
	String Qc_16 = "9";
	String Qc_17 = "9";
	String Qc_18 = "9";
	String Qc_19 = "9";
	String Qc_20 = "9";
	String Qc_21 = SPATIE_1;                                 // this one always blank (1 space) (MQCS version) MQCS version numbers not suitable for checking done by this program
	String Qc_22 = "9";                                      // only for vosclim -> this program always vosclim
	String Qc_23 = "9";                                      // only for vosclim -> this program always vosclim
	String Qc_24 = "9";                                      // only for vosclim -> this program always vosclim
	String Qc_25 = "9";                                      // only for vosclim -> this program always vosclim
	//String Qc_26 = "9";                                      // only for vosclim -> this program always vosclim
	String Qc_27 = "9";                                      // only for vosclim -> this program always vosclim
	String Qc_28 = "9";                                      // only for vosclim -> this program always vosclim
	String Qc_29 = "9";                                      // only for vosclim -> this program always vosclim

   boolean HDG_ok     = false;
   boolean sl_code_ok = false;


	//
	// immt record velden vullen (volgens WMO version IMMT-5)
	//
	immt_rec = "3";                                      // char number 1 (3=temp. in tenths of degrees C)

	immt_rec += mydatetime.year;                         // char number 2-5
	immt_rec += mydatetime.MM_code;                      // char number 6-7
	immt_rec += mydatetime.YY_code;                      // char number 8-9
	immt_rec += mydatetime.GG_code;                      // char number 10-11

   immt_rec += myposition.Qc_code;                      // char number 12

	immt_rec += myposition.lalala_code;                  // char number 13-15
	immt_rec += myposition.lolololo_code;                // char number 16-19
	Qc_20 = "1";

   immt_rec += "0";                                     // char number 20


   try
   {
      int num_h = Integer.valueOf(mycloudcover.h_code);
      if (num_h >= 0 && num_h <= 9)
      {
		   immt_rec += mycloudcover.h_code;               // char number 21
		   Qc_1 = "1";
      }
	   else
	   {
         immt_rec += SPATIE_1;                          // char number 21
	   }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 21 
   } // catch        
   
   
   try
   {
      int num_VV = Integer.valueOf(myvisibility.VV_code);
      if (num_VV >= 90 && num_VV <= 99)
      {
		   immt_rec += myvisibility.VV_code;              // char number 22-23
		   Qc_2 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 22-23
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 22-23
   } // catch


   try
   {
      int num_N = Integer.valueOf(mycloudcover.N_code);
      if (num_N >= 0 && num_N <= 9)
      {
         immt_rec += mycloudcover.N_code;               // char number 24
         Qc_3 = "1";
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 24
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 24
   } // catch


   try
   {
      int num_dd = Integer.valueOf(mywind.dd_code);
      if ((num_dd >= 0 && num_dd <= 36) || (num_dd == 99))
      {
		   immt_rec += mywind.dd_code;                    // char number 25-26
		   Qc_4 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 25-26
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 25-26
   } // catch


   try
   {
      int num_iw = Integer.valueOf(mywind.iw_code);
      if (num_iw == 0 || num_iw == 1 || num_iw == 3 || num_iw == 4)
      {
         immt_rec += mywind.iw_code;                    // char number 27
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 27
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 27
   } // catch


   try
   {
      // max ff 99 units for IMMT 
      int num_ff = Integer.valueOf(mywind.ff_code);
      if (num_ff >= 0 && num_ff <= 99)
      {
		   immt_rec += mywind.ff_code;                    // char number 28-29
		   Qc_5 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 28-29
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 28-29
   } // catch


   try
   {
      int num_sn_TTT = Integer.valueOf(mytemp.sn_TTT_code);
      if (num_sn_TTT >= 0 && num_sn_TTT <= 1)
      {
         immt_rec += mytemp.sn_TTT_code;                // char number 30
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 30
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 30
   } // catch


   try
   {
      int num_TTT = Integer.valueOf(mytemp.TTT_code);
      if (num_TTT >= 0 && num_TTT <= 999)
      {
		   immt_rec += mytemp.TTT_code;                    // char number 31-33
		   Qc_6 = "1";
      }
      else
      {
         immt_rec += SPATIE_3;                           // char number 31-33
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_3;                              // char number 31-33
   } // catch



   try
   {
   	// sign of dewpoint must be converted to 'computed immt code'
   	// also Sn_TdTdTd = 7(ice) is possible contrary to the operational branch
      
      //System.out.println("num_sn_TdTdTd = " + num_sn_TdTdTd);
      //System.out.println("mytemp.sn_TdTdTd_code = " + mytemp.sn_TdTdTd_code);   
      
      if ((mytemp.sn_TbTbTb_code.equals("")) && (mytemp.sn_TdTdTd_code.equals("") == false))
      {
         if (mytemp.sn_TdTdTd_code.equals("0"))   
         {
            immt_rec += "5";                               // char number 34
         }
         else if (mytemp.sn_TdTdTd_code.equals("1")) 
         {
            immt_rec += "6";                               // char number 34
         }
         else
         {
            immt_rec += SPATIE_1;                          // char number 34
         }
      } // if ((mytemp.sn_TbTbTb_code.equals("")) && (mytemp.sn_TdTdTd_code.equals("") == false))
      else if ((mytemp.sn_TbTbTb_code.equals("") == false) && (mytemp.sn_TdTdTd_code.equals("") == false))
      {
         int num_sn_TdTdTd = Integer.valueOf(mytemp.sn_TdTdTd_code);
         int num_sn_TbTbTb = Integer.valueOf(mytemp.sn_TbTbTb_code);

         if (num_sn_TdTdTd == 0)
         {
            immt_rec += "5";                             // char number 34
         }
         else if (num_sn_TdTdTd == 1 && num_sn_TbTbTb != 2 && num_sn_TbTbTb != 7)
         {
            immt_rec += "6";                             // char number 34
         }
         else if (num_sn_TdTdTd == 1 && (num_sn_TbTbTb == 2 || num_sn_TbTbTb == 7))
         {
            immt_rec += "7";                             // char number 34
         }
         else
         {
            immt_rec += SPATIE_1;                        // char number 34
         }
      } // else if ((mytemp.sn_TbTbTb_code.equals("") == false) && (mytemp.sn_TdTdTd_code.equals("") == false))
      else
      {
         immt_rec += SPATIE_1;                          // char number 34
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 34
   } // catch



   try
   {
      int num_TdTdTd = Integer.valueOf(mytemp.TdTdTd_code);
      if (num_TdTdTd >= 0 && num_TdTdTd <= 999)
      {
		   immt_rec += mytemp.TdTdTd_code;                // char number 35-37
		   Qc_7 = "1";
      }
      else
      {
         immt_rec += SPATIE_3;                          // char number 35-37
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_3;                             // char number 35-37
   } // catch


   try
   {
      int num_PPPP = Integer.valueOf(mybarometer.PPPP_code);
      if (num_PPPP >= 0 && num_PPPP <= 9999)
      {
		   immt_rec += mybarometer.PPPP_code;             // char number 38-41
		   Qc_8 = "1";
      }
      else
      {
         immt_rec += SPATIE_4;                          // char number 38-41
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_4;                             // char number 38-41
   } // catch


   try
   {
      int num_ww = Integer.valueOf(mypresentweather.ww_code);
      if (num_ww >= 0 && num_ww <= 99)
      {
		   immt_rec += mypresentweather.ww_code;          // char number 42-43
		   Qc_9 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 42-43
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 42-43
   } // catch

        

   try
   {
      int num_W1 = Integer.valueOf(mypastweather.W1_code);
      if (num_W1 >= 0 && num_W1 <= 9)
      {
         immt_rec += mypastweather.W1_code;             // char number 44
         Qc_9 = "1";
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 44
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 44
   } // catch



   try
   {
      int num_W2 = Integer.valueOf(mypastweather.W2_code);
      if (num_W2 >= 0 && num_W2 <= 9)
      {
         immt_rec += mypastweather.W2_code;             // char number 45
         Qc_9 = "1";
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 45
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 45
   } // catch


   try
   {
      int num_Nh = Integer.valueOf(mycloudcover.Nh_code);
      if (num_Nh >= 0 && num_Nh <= 9)
      {
         immt_rec += mycloudcover.Nh_code;             // char number 46
         Qc_3 = "1";
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 46
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 46
   } // catch


   try
   {
      int num_cl = Integer.valueOf(mycl.cl_code);
      if (num_cl >= 0 && num_cl <= 9)
      {
         immt_rec += mycl.cl_code;                     // char number 47
         Qc_3 = "1";
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 47
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 47
   } // catch


   try
   {
      String hulp_cm_code = "";

      if (mycm.cm_code.length() > 1)
      {
         hulp_cm_code = mycm.cm_code.substring(0, 1);    // NB .substring(0, 1) --> because Cm_code in case of Cm7 an a, b, c could be sticked to the 7 (7a, 7b, 7c)
      }
      else
      {
         hulp_cm_code = mycm.cm_code;
      }
      
      int num_cm = Integer.valueOf(hulp_cm_code);           
      if (num_cm >= 0 && num_cm <= 9)
      {
         immt_rec += hulp_cm_code;                      // char number 48
         Qc_3 = "1";
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 48
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 48
   } // catch



   try
   {
      int num_ch = Integer.valueOf(mych.ch_code);
      if (num_ch >= 0 && num_ch <= 9)
      {
         immt_rec += mych.ch_code;                     // char number 49
         Qc_3 = "1";
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 49
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 49
   } // catch



   try
   {
      int num_ss_TsTsTs = Integer.valueOf(mytemp.ss_TsTsTs_code);
      if (num_ss_TsTsTs >= 0 && num_ss_TsTsTs <= 7)
      {
         if (num_ss_TsTsTs == 0 || num_ss_TsTsTs == 2 || num_ss_TsTsTs == 4 || num_ss_TsTsTs == 6)
         {
            immt_rec += "0";                            // char number 50
         }
         else if (num_ss_TsTsTs == 1 || num_ss_TsTsTs == 3 || num_ss_TsTsTs == 5  || num_ss_TsTsTs == 7)
         {
            immt_rec += "1";                            // char number 50
         }
         else
         {
            immt_rec += SPATIE_1;                       // char number 50
         }
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 50
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 50
   } // catch



   try
   {
      int num_TsTsTs = Integer.valueOf(mytemp.TsTsTs_code);
      if (num_TsTsTs >= 0 && num_TsTsTs <= 999)
      {
		   immt_rec += mytemp.TsTsTs_code;                // char number 51-53
		   Qc_10 = "1";
      }
      else
      {
         immt_rec += SPATIE_3;                          // char number 51-53
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_3;                             // char number 51-53
   } // catch


   try
   {
      int num_immt_sst_indicator = Integer.valueOf(mytemp.immt_sst_indicator);
      if (num_immt_sst_indicator >= 0 && num_immt_sst_indicator <= 7)
      {
         immt_rec += mytemp.immt_sst_indicator;         // char number 54
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 54
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 54
   } // catch


   if (method_waves.equals(SEA_AND_SWELL_ESTIMATED) == true)
   {
      immt_rec += "0";                                  // char number 55
   }
   else if (method_waves.equals(WAVES_MEASURED_SHIPBORNE) == true)
   {
      immt_rec += "1";                                  // char number 55
   }
   else if (method_waves.equals(WAVES_MEASURED_BUOY) == true)
   {
      immt_rec += "4";                                  // char number 55
   }
   else if (method_waves.equals(WAVES_MEASURED_OTHER) == true)
   {
      immt_rec += "7";                                   // char number 55
   }
   else
   {
      // arbitrary, it could be " " also, but I choose "0" (estimated)
      immt_rec += "0";                                    // char number 55
   }



   try
   {
      int num_Pw = Integer.valueOf(mywaves.Pw_code);
      if (num_Pw >= 0 && num_Pw <= 99)
      {
		   immt_rec += mywaves.Pw_code;                  // char number 56-57
		   Qc_11 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 56-57
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 56-57
   } // catch


   try
   {
      int num_Hw = Integer.valueOf(mywaves.Hw_code);
      if (num_Hw >= 0 && num_Hw <= 99)
      {
		   immt_rec += mywaves.Hw_code;                  // char number 58-59
		   Qc_12 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 58-59
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 58-59
   } // catch



   try
   {
      int num_Dw1 = Integer.valueOf(mywaves.Dw1_code);
      if (num_Dw1 >= 0 && num_Dw1 <= 99)
      {
		   immt_rec += mywaves.Dw1_code;                  // char number 60-61
		   Qc_13 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 60-61
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 60-61
   } // catch


   try
   {
      int num_Pw1 = Integer.valueOf(mywaves.Pw1_code);
      if (num_Pw1 >= 0 && num_Pw1 <= 99)
      {
		   immt_rec += mywaves.Pw1_code;                  // char number 62-63
		   Qc_13 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 62-63
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 62-63
   } // catch


   try
   {
      int num_Hw1 = Integer.valueOf(mywaves.Hw1_code);
      if (num_Hw1 >= 0 && num_Hw1 <= 99)
      {
		   immt_rec += mywaves.Hw1_code;                  // char number 64-65
		   Qc_13 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 64-65
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 64-65
   } // catch


   if (main.obs_format.equals(main.FORMAT_FM13))
   {
      try
      {
         int num_Is = Integer.valueOf(myicing.Is_code);
         if (num_Is >= 1 && num_Is <= 5)
         {
            immt_rec += myicing.Is_code;                // char number 66
         }
         else
         {
            immt_rec += SPATIE_1;                       // char number 66
         }
      }
      catch (NumberFormatException e)
      {
         immt_rec += SPATIE_1;                          // char number 66
      }
   } // if (main.obs_format.equals(main.FORMAT_FM13))
   else // (eg format 101)
   {
      // NB format 101 is not according WMO code table 1751 as required for Is
      immt_rec += SPATIE_1;                             // char number 66
   } // else

   
   try
   {
      int num_EsEs = Integer.valueOf(myicing.EsEs_code);
      if (num_EsEs >= 0 && num_EsEs <= 99)
      {
         immt_rec += myicing.EsEs_code;                 // char number 67-68
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 67-68
      }
   }
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 67-68
   }

   
   try
   {
      int num_Rs = Integer.valueOf(myicing.Rs_code);
      if (num_Rs >= 0 && num_Rs <= 4)
      {
         immt_rec += myicing.Rs_code;                   // char number 69
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 69
      }
   }
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 69
   }

   immt_rec += "4";                                     // char number 70      // source of obs [4=electronic logbook]

   immt_rec += "4";                                     // char number 71      // obs platform [4=VOSClim]

   if (station_ID.length() == 7)   
   {
      immt_rec += station_ID;                           // char number 72-78
   }
   else // station ID less than 7 chars
   {
   	//int lengte = call_sign.trim().toUpperCase().length();
      int lengte = station_ID.length();
      
      if (lengte == 7)
      {
         immt_rec += station_ID;                         // char number 72-78
      }
      else if (lengte == 6)
      {
         immt_rec += station_ID;                         // char number 72-77
         immt_rec += SPATIE_1;                           // char number 78
      }
      else if (lengte == 5)
      {
         immt_rec += station_ID;                         // char number 72-76
         immt_rec += SPATIE_2;                           // char number 77-78
      }
      else if (lengte == 4)
      {
         immt_rec += station_ID;                         // char number 72-75
         immt_rec += SPATIE_3;                           // char number 76-78
      }
      else if (lengte == 3)
      {
         immt_rec += station_ID;                         // char number 72-74
         immt_rec += SPATIE_4;                           // char number 75-78
      }
      else // NB only call sign (station_ID) length of 3,4,5,6,7 char allowed (see IMMT description)
      {
         // note: in TurboWin "unknown"
         immt_rec += SPATIE_7;                             // char number 72-78
      }
   } // else (station ID less than 7 chars)

   if (recruiting_country.length() > 2)
   {
      immt_rec += recruiting_country.substring(recruiting_country.length() - 2);  // char number 79 - 80 (e.g. Netherlands NL)
   }
   else
   {
      immt_rec += SPATIE_2;                              // char number 79 - 80
   }

   immt_rec += SPATIE_1;                                 // char number 81

   immt_rec += "3";                                      // char number 82     NB 3 = automated QC only(inc time-seq checks)

   immt_rec += "1";                                      // char number 83    // 1 = weather indicator: manual

   immt_rec += "4";                                      // char number 84    //ir

   immt_rec += SPATIE_3;                                 // char number 85-87 // RRR

   immt_rec += SPATIE_1;                                 // char number 88    // tr

   try
   {
      int num_sn_TbTbTb_code = Integer.valueOf(mytemp.sn_TbTbTb_code);
      if (num_sn_TbTbTb_code >= 0 && num_sn_TbTbTb_code <= 2)
      {
		   immt_rec += mytemp.sn_TbTbTb_code;              // char number 89
      }
      else
      {
         immt_rec += SPATIE_1;                           // char number 89
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                              // char number 89
   } // catch


   try
   {
      int num_TbTbTb_code = Integer.valueOf(mytemp.TbTbTb_code);
      if (num_TbTbTb_code >= 0 && num_TbTbTb_code <= 999)
      {
		   immt_rec += mytemp.TbTbTb_code;                 // char number 90-92
         Qc_19 = "1";
      }
      else
      {
         immt_rec += SPATIE_3;                           // char number 90-92
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_3;                              // char number 90-92
   } // catch


   try
   {
      int num_a_code = Integer.valueOf(mybarograph.a_code);
      if (num_a_code >= 0 && num_a_code <= 8)
      {
		   immt_rec += mybarograph.a_code;                 // char number 93
         Qc_15 = "1";
      }
      else
      {
         immt_rec += SPATIE_1;                           // char number 93
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                              // char number 93
   } // catch


   try
   {
      int num_ppp_code = Integer.valueOf(mybarograph.ppp_code);
      if (num_ppp_code >= 0 && num_ppp_code <= 999)
      {
		   immt_rec += mybarograph.ppp_code;              // char number 94-96
         Qc_16 = "1";
      }
      else
      {
         immt_rec += SPATIE_3;                          // char number 94-96
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_3;                             // char number 94-96
   } // catch

   
   if (main.obs_format.equals(main.FORMAT_101))
   {
      // Ds in IMMT defined as 3 hours period; in format101 it is defined as 10 minutes period so Ds cannot be put in IMMT
      immt_rec += SPATIE_1;                             // char number 97
   }
   else
   {
      try
      {
         int num_Ds_code = Integer.valueOf(myposition.Ds_code);
         if (num_Ds_code >= 0 && num_Ds_code <= 9)
         {
		      immt_rec += myposition.Ds_code;             // char number 97
            Qc_17 = "1";
         }
         else
         {
            immt_rec += SPATIE_1;                       // char number 97
         }
      } // try
      catch (NumberFormatException e)
      {
         immt_rec += SPATIE_1;                          // char number 97
      } // catch
   } // else

   
   if (main.obs_format.equals(main.FORMAT_101))
   {
      // vs in IMMT defined as 3 hours period; in format101 it is defined as 10 minutes period so vs cannot be put in IMMT
      immt_rec += SPATIE_1;                             // char number 97
   }
   else
   {
      try
      {
         int num_vs_code = Integer.valueOf(myposition.vs_code);
         if (num_vs_code >= 0 && num_vs_code <= 9)
         {
	   	   immt_rec += myposition.vs_code;             // char number 98
            Qc_18 = "1";
         }
         else
         {
            immt_rec += SPATIE_1;                       // char number 98
         }
      } // try
      catch (NumberFormatException e)
      {
         immt_rec += SPATIE_1;                          // char number 98
      } // catch
   } // else

   
   try
   {
      int num_Dw2 = Integer.valueOf(mywaves.Dw2_code);
      if (num_Dw2 >= 0 && num_Dw2 <= 99)
      {
		   immt_rec += mywaves.Dw2_code;                  // char number 99-100
		   Qc_13 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 99-100
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 99-100
   } // catch


   try
   {
      int num_Pw2 = Integer.valueOf(mywaves.Pw2_code);
      if (num_Pw2 >= 0 && num_Pw2 <= 99)
      {
		   immt_rec += mywaves.Pw2_code;                  // char number 101-102
		   Qc_13 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 101-102
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 101-102
   } // catch

   
   try
   {
      int num_Hw2 = Integer.valueOf(mywaves.Hw2_code);
      if (num_Hw2 >= 0 && num_Hw2 <= 99)
      {
		   immt_rec += mywaves.Hw2_code;                  // char number 103-104
		   Qc_13 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 103-104
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 103-104
   } // catch

   
   try
   {
      int num_ci = Integer.valueOf(myice1.ci_code);
      if (num_ci >= 0 && num_ci <= 9)
      {
         immt_rec += myice1.ci_code;                    // char number 105
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 105
      }
   }
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 105
   }

   
   try
   {
      int num_Si = Integer.valueOf(myice1.Si_code);
      if (num_Si >= 0 && num_Si <= 9)
      {
         immt_rec += myice1.Si_code;                    // char number 106
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 106
      }
   }
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 106
   }

   
   try
   {
      int num_bi = Integer.valueOf(myice1.bi_code);
      if (num_bi >= 0 && num_bi <= 9)
      {
         immt_rec += myice1.bi_code;                    // char number 107
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 107
      }
   }
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 107
   }

   
   try
   {
      int num_Di = Integer.valueOf(myice1.Di_code);
      if (num_Di >= 0 && num_Di <= 9)
      {
         immt_rec += myice1.Di_code;                    // char number 108
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 108
      }
   }
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 108
   }

   
   try
   {
      int num_zi = Integer.valueOf(myice1.zi_code);
      if (num_zi >= 0 && num_zi <= 9)
      {
         immt_rec += myice1.zi_code;                    // char number 109
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 109
      }
   }
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 109
   }

   immt_rec += "A";                                     // char number 110 (FM-13 code version)

   immt_rec += "5";                                     // char number 111 (IMMT version)

	immt_rec += Qc_1;                                    // char number 112
	immt_rec += Qc_2;                                    // char number 113
	immt_rec += Qc_3;                                    // char number 114
	immt_rec += Qc_4;                                    // char number 115
	immt_rec += Qc_5;                                    // char number 116
	immt_rec += Qc_6;                                    // char number 117
	immt_rec += Qc_7;                                    // char number 118
	immt_rec += Qc_8;                                    // char number 119
	immt_rec += Qc_9;                                    // char number 120
	immt_rec += Qc_10;                                   // char number 121
	immt_rec += Qc_11;                                   // char number 122
	immt_rec += Qc_12;                                   // char number 123
	immt_rec += Qc_13;                                   // char number 124
	immt_rec += Qc_14;                                   // char number 125
	immt_rec += Qc_15;                                   // char number 126
	immt_rec += Qc_16;                                   // char number 127
	immt_rec += Qc_17;                                   // char number 128
	immt_rec += Qc_18;                                   // char number 129
	immt_rec += Qc_19;                                   // char number 130
	immt_rec += Qc_20;                                   // char number 131
   immt_rec += Qc_21;                                   // char number 132  (MQCS)


   try
   {
      int num_HDG = Integer.valueOf(mywind.HDG_code);
      if (num_HDG >= 1 && num_HDG <= 360)
      {
		   immt_rec += mywind.HDG_code;                   // char number 133-135
		   Qc_22 = "1";
         HDG_ok = true;
      }
      else
      {
         // heading (HDG) is blank if heading was the same as COG (see wind input screen)
         HDG_ok = false;
      }
   } // try
   catch (NumberFormatException e)
   {
      HDG_ok = false;
   } // catch



   // So HDG (heading) blank -> use COG
   if (HDG_ok == false)
   {
      try
      {
         int num_COG = Integer.valueOf(mywind.COG_code);   // use COG for HDG (see wind input screen) will be inserted into IMMT on the position of HDG
         if (num_COG >= 1 && num_COG <= 360)
         {
		      immt_rec += mywind.COG_code;                   // char number 133-135
		      Qc_22 = "1";
         }
         else
         {
            immt_rec += SPATIE_3;                          // char number 133-135
         }
      } // try
      catch (NumberFormatException e)
      {
         immt_rec += SPATIE_3;                             // char number 133-135
      } // catch
   } // if (HDG_ok == false)



   try
   {
      int num_COG = Integer.valueOf(mywind.COG_code);
      if (num_COG >= 0 && num_COG <= 360)
      {
		   immt_rec += mywind.COG_code;                   // char number 136-138
		   Qc_23 = "1";
      }
      else
      {
         immt_rec += SPATIE_3;                          // char number 136-138
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_3;                             // char number 136-138
   } // catch


   try
   {
      int num_SOG = Integer.valueOf(mywind.SOG_code);
      if (num_SOG >= 0 && num_SOG <= 99)
      {
		   immt_rec += mywind.SOG_code;                   // char number 139-140
		   Qc_24 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 139-140
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 139-140
   } // catch


   try
   {
      int num_SLL = Integer.valueOf(mywind.SLL_code);
      if (num_SLL >= 0 && num_SLL <= 99)
      {
		   immt_rec += mywind.SLL_code;                   // char number 141-142
		   Qc_25 = "1";
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 141-142
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 141-142
   } // catch


   try
   {
      int num_sl = Integer.valueOf(mywind.sl_code);
      if (num_sl >= 0 && num_sl <= 1)
      {
		   immt_rec += mywind.sl_code;                   // char number 143
         sl_code_ok = true;
      }
      else
      {
         immt_rec += SPATIE_1;                          // char number 143
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_1;                             // char number 143
   } // catch


   try
   {
      int num_hh = Integer.valueOf(mywind.hh_code);
      if (num_hh >= 0 && num_hh <= 99)
      {
		   immt_rec += mywind.hh_code;                   // char number 144-145

         // Qc27 serves as the indicator for both sl and hh (from IMMT-4)
         if (sl_code_ok == true)
         {
            // So both, sl and hh, are now ok
            Qc_27 = "1";
         }
      }
      else
      {
         immt_rec += SPATIE_2;                          // char number 144-145
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_2;                             // char number 144-145
   } // catch


   try
   {
      int num_RWD = Integer.valueOf(mywind.RWD_code);
      if (num_RWD >= 0 && num_RWD <= 360)
      {
		   immt_rec += mywind.RWD_code;                   // char number 146-148
		   Qc_28 = "1";
      }
      else
      {
         immt_rec += SPATIE_3;                          // char number 146-148
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_3;                             // char number 146-148
   } // catch


   try
   {
      int num_RWS = Integer.valueOf(mywind.RWS_code);
      if (num_RWS >= 0 && num_RWS <= 999)
      {
		   immt_rec += mywind.RWS_code;                   // char number 149-151
		   Qc_29 = "1";
      }
      else
      {
         immt_rec += SPATIE_3;                          // char number 149-151
      }
   } // try
   catch (NumberFormatException e)
   {
      immt_rec += SPATIE_3;                             // char number 149-151
   } // catch

   immt_rec += Qc_22;                                   // char number 152
   immt_rec += Qc_23;                                   // char number 153
   immt_rec += Qc_24;                                   // char number 154
   immt_rec += Qc_25;                                   // char number 155
   immt_rec += SPATIE_1;                                // char number 156      // NB former Qc_26;
   immt_rec += Qc_27;                                   // char number 157
   immt_rec += Qc_28;                                   // char number 158
   immt_rec += Qc_29;                                   // char number 159

   if (mytemp.double_rv >= 0.0 && mytemp.double_rv <= 1.0 && (mytemp.RH.trim().length() > 0)) // NB double_rv range 0.0 - 1.0
   {
      // RH
      int num_RH_code = (int)Math.floor(Math.abs(mytemp.double_rv * 100) * 10 + 0.5);         // e.g 0.5 -> 50 % -> 500 tenths of percentage
      String RH_code = Integer.toString(num_RH_code);     // convert to string      
              
      // NB RH_code always 4 characters width e.g. 0885 (accomplish via construction below)
      int len = 4;
      if (RH_code.length() < len)                         // pad on left with zeros
      {
         RH_code = "0000000000".substring(0, len - RH_code.length()) + RH_code;
      }
      
      if (RH_code.length() == 4)
      {
         immt_rec += RH_code;                             // char number 160-163  // relative humidity
         immt_rec += "0";                                 // char number 164      // relative humidity indicator
      }
      else
      {
         immt_rec += SPATIE_4;                            // char number 160-163  // relative humidity
         immt_rec += SPATIE_1;                            // char number 164      // relative humidity indicator
      }
   }
   else
   {   
      immt_rec += SPATIE_4;                              // char number 160-163  // relative humidity
      immt_rec += SPATIE_1;                              // char number 164      // relative humidity indicator
   }
   
   //immt_rec += SPATIE_1;                                // char number 165
   if (RS232_connection_mode == 3 || RS232_connection_mode == 11)                //  EUCAWS or AMOS2X
   {
      immt_rec += "2";                                   // char number 165      // 2 = AWS + manual observation
   }
   else if  (RS232_connection_mode == 9 || RS232_connection_mode == 10)          // OMC-140 AWS connected
   {
      immt_rec += "1";                                   // char number 165      // 1 = AWS
   }
   else
   {   
      immt_rec += "0";                                   // char number 165      // 0 = no AWS
   }
   
   int lengte_imo = imo_number.trim().length();

   if (lengte_imo == 7)
   {
      immt_rec += imo_number;                           // char number 166-172
   }
   else if (lengte_imo == 6)
   {
      immt_rec += imo_number;                           // char number 166-171
      immt_rec += SPATIE_1;                             // char number 172
   }
   else if (lengte_imo == 5)
   {
      immt_rec += imo_number;                           // char number 166-170
      immt_rec += SPATIE_2;                             // char number 171-172
   }
   else if (lengte_imo == 4)
   {
      immt_rec += imo_number;                           // char number 166-169
      immt_rec += SPATIE_3;                             // char number 170-172
   }
   else if (lengte_imo == 3)
   {
      immt_rec += imo_number;                           // char number 166-168
      immt_rec += SPATIE_4;                             // char number 169-172
   }
   else if (lengte_imo == 2)
   {
      immt_rec += imo_number;                           // char number 166-167
      immt_rec += SPATIE_5;                             // char number 168-172
   }
   else if (lengte_imo == 1)
   {
      immt_rec += imo_number;                           // char number 166
      immt_rec += SPATIE_6;                             // char number 167-172
   }
   else // 
   {
     immt_rec += SPATIE_7;                              // char number 166-172
   }


   /* BEGIN: THE FOLLOWING SECTION IS NOT PART OF THE OFFICIAL IMMT FORMAT */
   immt_rec += SPATIE_1;                                // char number 173


   if (myobserver.selected_observer != null && myobserver.selected_observer.compareTo("") != 0)
   {
/*      
      if (recruiting_country.indexOf("GERMANY") != -1)
      {
         //System.out.println("+++ GERMANY ok");
         
         // NB ONLY for Germany:
         //    convert TurboWin+ format storage of observer to TurboWin storage (replace ; by space and only use sure name + (first) initial
         //    Stam;M;1st off;-;       ->      Stam M
         String selected_observer = "";
         
         if (myobserver.selected_observer.length() > 2)
         {
            int pos = -1;
            int number_read_commas = 0;
            
            do
            {
               pos = myobserver.selected_observer.indexOf(";", pos + 1);
               if (pos != -1)
               {
                  number_read_commas++;
                  if (number_read_commas == 2)
                  {
                     selected_observer = myobserver.selected_observer.substring(0, pos);
                     selected_observer = selected_observer.replace(';', ' ');      //replace occurrence of ';' to ' '     (eg Stam;M  -> Stam M)
                     selected_observer = selected_observer.replaceAll("\\.", "");    //replaces all occurrences of '.' to '' (eg Brouwer M.F  -> Brouwer MF)
                     break;
                  }
               } // if (pos != -1)
            } while (pos != -1); 
         } // if (myobserver.selected_observer.length() > 2)
         
         immt_rec += selected_observer;                    // char number 174 - ?
         
      } // if (recruiting_country.indexOf("GERMANY") != -1)
      else
      {
         immt_rec += myobserver.selected_observer;         // char number 174 - ?
      }
*/     
      immt_rec += myobserver.selected_observer;         // char number 174 - ?
   }

   /* END: THE FOLLOWING SECTION IS NOT PART OF THE OFFICIAL IMMT FORMAT */


   schrijven_IMMT_log(immt_rec);

}



   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   private static void schrijven_IMMT_log(final String immt_rec)        
   {
      // called from: IMMT_log() [main.java]
      
      
      /* NB input/output in a GUI always via a SwingWorker (Core Java Volume 1 bld 795 e.v.; Volume 2 bld 37, 215) */

      boolean doorgaan = true;


      // first test logs dir was defined
      if (main.logs_dir.equals("") == true)
      {
         JOptionPane.showMessageDialog(null, "logs folder unknown, please select: Maintenance -> Log files settings", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         doorgaan = false;
      }


      if (doorgaan == true)
      {
         new SwingWorker<Integer, Void>()        
         {
            @Override
            protected Integer doInBackground() throws Exception        
            {
               Integer return_code          = -1;   // initialisation
               
               boolean huidige_rec_ok = true;
               String datum_tijd_positie_last_record = "";
               String datum_tijd_positie_huidige_record = "";

               // preventing observation will be stored twice or more times and present record is ok
               //
               if (last_record.length() >= 19)
               {
                  datum_tijd_positie_last_record = last_record.substring(1, 19);
               }

               if (immt_rec.length() >= 19)
               {
                  datum_tijd_positie_huidige_record = immt_rec.substring(1, 19);
                  
                  // basic test on year in present IMMT record
                  String str_year = datum_tijd_positie_huidige_record.substring(1,5);   // eg 32019101002745900210    351040200    0000        009310  etc. 
                  try
                  {   
                     int int_year = Integer.parseInt(str_year);
                     if (int_year >= 2000 && int_year <= 2099)
                     {
                        huidige_rec_ok = true;
                     }
                  }
                  catch (NumberFormatException e)
                  {
                     huidige_rec_ok = false;
                  }
               }
               else
               {
                  // To prevent records without date time and position will be stored
                  huidige_rec_ok = false;
               }

               if ((datum_tijd_positie_last_record.compareTo(datum_tijd_positie_huidige_record) != 0) && (huidige_rec_ok == true))
               {
                  String volledig_path = main.logs_dir + java.io.File.separator + IMMT_LOG;

                  try (BufferedWriter out = new BufferedWriter(new FileWriter(volledig_path, true))) // true means append the specified data to the file i.e. the pre-exist data in a file is not overwritten and the new data is appended after the pre-exist data.
                  {
                     out.write(immt_rec);
                     out.newLine();   // newLine(): write a line separator. The line separator string is defined by the system property line.separator, and is not necessarily a single newline ('\n') character.
           
                     return_code = 0;               // OK
                     
                  } // try
                  catch (IOException e)
                  {
                     //JOptionPane.showMessageDialog(null, "unable to write to: " + volledig_path, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                     return_code = 1;          // writing error
                  } // catch
               }
               else
               {
                  return_code = 2;              // double record
               }

               return return_code;

            } // protected Void doInBackground() throws Exception
            
            @Override
            protected void done()
            {
               String message = "";
               try 
               {
                  Integer return_code = get();
                  
                  // NB so the message of return_code -1 (nothing was done) is not logged / popped-up !!
                  
                  if (return_code == 0)
                  {
                     message = "appended immt.log successfully";
                  }
                  else if (return_code == 1)
                  {
                     String volledig_path_immt = main.logs_dir + java.io.File.separator + IMMT_LOG;
                     message = "unable to write to: " + volledig_path_immt;
                  } // if (return_code == 1)
                  
                  // NB so the message of return_code 2 (double immt record) is not logged / popped-up !!
               } 
               catch (InterruptedException | ExecutionException ex) 
               {
                  message = "exception when writing immt.log (" + ex + ")";
               }
               
               // log or pop-up the message
               if (message.equals("") == false)
               {
                  if (APR == true)
                  {   
                     main.log_turbowin_system_message("[IMMT] " + message);
                  }
                  else if (AWSR == true)
                  {   
                     main.log_turbowin_system_message("[IMMT] " + message);
                  }
                  else // manual (no APR and no AWSR)
                  {
                     if (message.contains ("successfully"))
                     {
                        // NB successfully written immt records only to log and not in pop-up message even in manual mode
                        main.log_turbowin_system_message("[IMMT] " + message);
                     }
                     else
                     {
                        JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                     }
                  }                  
                  
               } // if (message.equals("") == false)
            } // protected void done()    
            
         }.execute(); // new SwingWorker<Void, Void>()
      } // if (doorgaan == true)
   }



   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
/*   
   public static void internet_mouseClicked(final String help_dir) {
      // TODO add your handling code here:

      new SwingWorker<Void, Void>()
      {
         @Override
         protected Void doInBackground() throws Exception
         {
            Desktop desktop = null;
            boolean local_help_file_exists = false;
             
            // Are the help files stored locally? (installed as part of the complete TurboWin+ installation)
            //
            String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_dir; // nb help_dir is pecifiek per parameter bv wind, waves etc.
            File f = new File(help_file_path);
            if (f.isFile())
            {
               local_help_file_exists = true;
            }
            

            // Before more Desktop API is used, first check
            // whether the API is supported by this particular
            // virtual machine (VM) on this particular host.
            if (Desktop.isDesktopSupported())
            {
               desktop = Desktop.getDesktop();
               URI uri = null;
               try
               {
                  //if (offline_mode == false)
                  //{
                  //   String http_adres = main.URL_INTERNET_HELP + help_dir; // help_dir set in e.g. ch1_image_mouseClicked()
                  //   uri = new URI(http_adres);
                  //   desktop.browse(uri);
                  //}
                  //else if (offline_mode == true)
                  //{
                  //   String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_dir; // nb help_dir is pecifiek per parameter bv wind, waves etc.
                  //   desktop.open(new File(help_file_path));
                  //}
                  
                  // First always try the local help dir
                  // if no help file was found, secondly always try the internet
                  //
                  //
                  //String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_dir; // nb help_dir is pecifiek per parameter bv wind, waves etc.
                  //File f = new File(help_file_path);
                  //if (f.isFile())
                  if (local_help_file_exists)
                  {
                     desktop.open(new File(help_file_path));
                  }
                  else
                  {
                     String http_adres = main.URL_INTERNET_HELP + help_dir; // help_dir set in e.g. ch1_image_mouseClicked()
                     uri = new URI(http_adres);
                     desktop.browse(uri);
                  }
                  
               } // try
               catch (IOException | URISyntaxException ioe) { }
            } // if (Desktop.isDesktopSupported())
            else // Destop method not supported trying alternatives
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
               
               URI uri = null;
               String te_open_help_file = null;
               Runtime runtime;
                  
               //if (offline_mode == false)
               if (!local_help_file_exists)  
               {
                  String http_adres = main.URL_INTERNET_HELP + help_dir; 
                  uri = new URI(http_adres);
                  te_open_help_file = uri.toString();
                  // NB Maybe uri.toString() is maybe(?)not necassary in case "xdg-open" this should be tested (see also manual on "xdg-open")
               }
               else if (offline_mode == true)
               {
                  //String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_dir; // nb help_dir is pecifiek per parameter bv wind, waves etc.
                  te_open_help_file = help_file_path;
               }
                  
               runtime = Runtime.getRuntime();
                  
               // Microsoft Windows
               // --- NB Desktop method will always succeed
               
               // Linux (Gnome)
               // --- NB Desktop method will succeed (Desktop is based on Gnome) so not necessary to try a customised open command ---
                  
               // Linux (KDE)
               try 
               {
                  runtime.exec("kde-open " + te_open_help_file);
               } 
               catch (IOException e) 
               {
                  // Linux (RaspBerry) [14-11-2014: tested on stand-alone RaspBerry succesfully]
                  try 
                  {
                     runtime.exec("xdg-open " + te_open_help_file);
                  } 
                  catch (IOException e2) 
                  {
                     // Mac
                     try 
                     {
                        runtime.exec("open " + te_open_help_file);
                     } 
                     catch (IOException e3) 
                     {
                        JOptionPane.showMessageDialog(null, "Error invoking default web browser (-Desktop-method not supported on this computer system and Runtime alternatives failed)" , main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                     } // catch                 
                  } // catch                 
               } // catch                
            } // else (// Destop method not supported)

            return null;

         } // protected Void doInBackground() throws Exception
      }.execute(); // new SwingWorker<Void, Void>()
   }
*/   
   
   
   /***********************************************************************************************/
   /*                                                                                             */
   /*                                                                                             */
   /*                                                                                             */
   /***********************************************************************************************/
   public static void help_mouseClicked(final String help_page) {
      // TODO add your handling code here:

      String os = OSDetector.getOSString();
      
      boolean local_help_file_exists = false;
             
      // Are the help files stored locally? (installed as part of the complete TurboWin+ installation)
      //
      String help_file_path = System.getProperty("app.dir") + java.io.File.separator + ".." + java.io.File.separator + "runtime" + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_page; // nb help_page is parameter specific e.g. wind.pdf, waves.pdf etc.
      File f = new File(help_file_path);
      if (f.isFile())
      {
         local_help_file_exists = true;
      }
               
      URI uri = null;
      String te_openen_help_file = null;
                  
      if (!local_help_file_exists)  
      {
         // e.g. https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/help_files/barometer.pdf?inline=true 
         String http_adres = main.URL_INTERNET_HELP + help_page + "?inline=true"; 
         try 
         {
            uri = new URI(http_adres);
         } 
         catch (URISyntaxException ex) 
         {
            uri = null;
         }
        
         if (uri != null)
         {
            te_openen_help_file = uri.toString();
         }
      }
      else    
      {
         te_openen_help_file = help_file_path;
      }
      
      
      if (os.equals("LINUX"))
      {
         support_class.open_browser_on_linux(te_openen_help_file);
      } // if (os.equals("LINUX"))   
      else
      {
         support_class.open_browser_on_not_linux(te_openen_help_file);
      }   

/*      
      new SwingWorker<Void, Void>()
      {
         @Override
         protected Void doInBackground() throws Exception
         {
            Desktop desktop = null;
            boolean local_help_file_exists = false;
             
            // Are the help files stored locally? (installed as part of the complete TurboWin+ installation)
            //
            String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_page; // nb help_page is parameter specific e.g. wind.pdf, waves.pdf etc.
            File f = new File(help_file_path);
            if (f.isFile())
            {
               local_help_file_exists = true;
            }
            

            // Before more Desktop API is used, first check
            // whether the API is supported by this particular
            // virtual machine (VM) on this particular host.
            if (Desktop.isDesktopSupported())
            {
               desktop = Desktop.getDesktop();
               URI uri = null;
               try
               {
                  if (local_help_file_exists)
                  {
                     desktop.open(new File(help_file_path));
                  }
                  else
                  {
                     // e.g. https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/help_files/barometer.pdf?inline=true 
                     String http_adres = main.URL_INTERNET_HELP + help_page + "?inline=true"; // help_dir was set in java input page file e.g. mycm.java; ch1_image_mouseClicked()
                     uri = new URI(http_adres);
                     desktop.browse(uri);
                  }
                  
               } // try
               catch (IOException | URISyntaxException ioe) { }
            } // if (Desktop.isDesktopSupported())
            else // Destop method not supported trying alternatives
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
               
               URI uri = null;
               String te_open_help_file = null;
               //Runtime runtime;
                  
               if (!local_help_file_exists)  
               {
                  // e.g. https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/help_files/barometer.pdf?inline=true 
                  String http_adres = main.URL_INTERNET_HELP + help_page + "?inline=true"; 
                  uri = new URI(http_adres);
                  te_open_help_file = uri.toString();
                  // NB Maybe uri.toString() is maybe(?)not necassary in case "xdg-open" this should be tested (see also manual on "xdg-open")
               }
               else if (offline_mode == true)
               {
                  //String help_file_path = data_dir + java.io.File.separator + OFFLINE_HELP_DIR + java.io.File.separator + help_dir; // nb help_dir is specific per parameter eg wind, waves etc.
                  te_open_help_file = help_file_path;
               }
                  
               //runtime = Runtime.getRuntime();
                  
               // Microsoft Windows
               // --- NB Desktop method will always succeed
               
               // Linux (Gnome)
               // --- NB Desktop method will succeed (Desktop is based on Gnome) so not necessary to try a customised open command ---
                  
               // Linux (KDE)
               try 
               {
                  // deprecated from JDK19
                  //runtime.exec("kde-open " + te_open_help_file);
                  
                  // create cmd array
                  String[] cmdArray = {"kde-open", te_open_help_file};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               } 
               catch (IOException e) 
               {
                  // Linux (RaspBerry) [14-11-2014: tested on stand-alone RaspBerry succesfully]
                  try 
                  {
                     // deprecated from JDK19
                     //runtime.exec("xdg-open " + te_open_help_file);
                     
                     // create cmd array
                     String[] cmdArray = {"xdg-open", te_open_help_file};

                     // create a process and execute cmdArray
                     Process process = Runtime.getRuntime().exec(cmdArray);
                  } 
                  catch (IOException e2) 
                  {
                     // Mac
                     try 
                     {
                        // deprecated from JDK19
                        //runtime.exec("open " + te_open_help_file);
                        
                        // create cmd array
                        String[] cmdArray = {"open", te_open_help_file};

                        // create a process and execute cmdArray
                        Process process = Runtime.getRuntime().exec(cmdArray);
                     } 
                     catch (IOException e3) 
                     {
                        JOptionPane.showMessageDialog(null, "Error invoking default web browser (-Desktop-method not supported on this computer system and Runtime alternatives failed)" , main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                     } // catch                 
                  } // catch                 
               } // catch                
            } // else (// Destop method not supported)

            return null;

         } // protected Void doInBackground() throws Exception
      }.execute(); // new SwingWorker<Void, Void>()
*/      
   }
   


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void check_immt_size()
{
   // TODO add your handling code here:

   new SwingWorker<Boolean, Void>()
   {
      @Override
      protected Boolean doInBackground() throws Exception
      {
         boolean immt_size_limit_exceeded = false;

         /* first check if there is an immt log source file present (and not empty) */
         String volledig_path_immt = logs_dir + java.io.File.separator + IMMT_LOG;
         File immt_file = new File(volledig_path_immt);
         if (immt_file.exists() && immt_file.length() > IMMT_LIMIT)     // length() in bytes
         {
            immt_size_limit_exceeded = true;
         }

         return immt_size_limit_exceeded;
      }
      @Override
      protected void done()
      {
         try
         {
            boolean immt_size_limit_exceeded = get();   // retrieve return value from doInBackground()
            if (immt_size_limit_exceeded)
            {
               String info = "immt log (file with all stored observations for research and climatological use) exceeds ";
               info += IMMT_LIMIT / 1024;
               info += " Kb.\nPlease select one of the coming days: Maintenance -> Move log files ";
               JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);

            } // if (immt_size_limit_exceeded)

         } // try
         catch (InterruptedException | ExecutionException ex) { }

      } // protected void done()

   }.execute();
}










/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Output_obs_to_file_FM13()
{
   //JOptionPane.showMessageDialog(null, "test output obs to file", main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
   
   
   // pop-up the file chooser dialog box
   JFileChooser chooser = new JFileChooser();
   int result = chooser.showSaveDialog(main.this);
   if (result == JFileChooser.APPROVE_OPTION)
   {
      output_file = chooser.getSelectedFile().getPath();

      new SwingWorker<Boolean, Void>()
      {
         @Override
         protected Boolean doInBackground() throws Exception
         {
            boolean obs_written_ok = true;
            
            // write to selected file
            try (BufferedWriter out = new BufferedWriter(new FileWriter(output_file)))
            {
               out.write(obs_write);
               //out.newLine();   // newLine(): write a line separator. The line separator string is defined by the system property line.separator, and is not necessarily a single newline ('\n') character.

               // user feedback
               obs_written_ok = true;

            } // try
            catch (IOException | HeadlessException e)
            {
               obs_written_ok = false;
            } // catch

            return obs_written_ok;

         } // protected Void doInBackground() throws Exception

         @Override
         protected void done()
         {
            try 
            {
               boolean result_obs_written_ok = get();
            
               if (result_obs_written_ok == true)
               {
                  String info = "obs written to: " + output_file;
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);   
               }
               else
               {
                  JOptionPane.showMessageDialog(null, "unable to write to: " + output_file, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               }
               
               IMMT_log();
               
               Reset_all_meteo_parameters();
            } // protected void done()
            catch (InterruptedException | ExecutionException ex)
            {
               //Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
               System.out.println("--- Function Output_obs_to_file_FM13(): " + ex);
            }
         }

      }.execute(); // new SwingWorker<Void, Void>()
   } // if (result == JFileChooser.APPROVE_OPTION

}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Output_obs_to_file_format_101()
{
   // pop-up the file chooser dialog box
   JFileChooser chooser = new JFileChooser();
   int result = chooser.showSaveDialog(main.this);
   if (result == JFileChooser.APPROVE_OPTION)
   {
      output_file = chooser.getSelectedFile().getPath();

      new SwingWorker<Boolean, Void>()
      {
         @Override
         protected Boolean doInBackground() throws Exception
         {
            boolean obs_written_ok = true;
            boolean doorgaan            = true;
            String file_format_101_line = "";     
           
            
            file_format_101_line = get_format_101_obs_from_file();
            if (file_format_101_line.equals("") == true)
            {
               doorgaan = false;
            }
            
            if (doorgaan == true)
            {
               // write to selected file
               try (BufferedWriter out = new BufferedWriter(new FileWriter(output_file)))
               {
                  out.write(file_format_101_line);
                  //out.newLine();   // newLine(): write a line separator. The line separator string is defined by the system property line.separator, and is not necessarily a single newline ('\n') character.

                  // user feedback
                  obs_written_ok = true;

               } // try
               catch (IOException | HeadlessException e)
               {
                  obs_written_ok = false;
               } // catch
            } // if (doorgaan == true)
            
            return obs_written_ok;

         } // protected Void doInBackground() throws Exception

         @Override
         protected void done()
         {
            try
            {
               boolean result_obs_written_ok = get();
               if (result_obs_written_ok == true)
               {
                  String info = "obs written to: " + output_file;
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);    
               }
               else
               {
                  JOptionPane.showMessageDialog(null, "unable to write to: " + output_file, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);    
               }
            
               IMMT_log();
            
               Reset_all_meteo_parameters();
            }
            catch (InterruptedException | ExecutionException ex)
            {
               System.out.println("--- Function Output_obs_to_file_format_101(): " + ex);
            }
         } // protected void done()

      }.execute(); // new SwingWorker<Void, Void>()
   } // if (result == JFileChooser.APPROVE_OPTION

}


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void Output_obs_by_email_jakarta_FM13_format_101(boolean manual_send) // NB also for Custom email !
{
   // NB manual_send = true/false   (set in Output_obs_by_email_all_manual() [main.java] or RS232_Output_obs_by_email_all_APR()[main_RS232_RS422.java])
   //
   // NB This function called by: - Output_obs_by_email_all_manual()    --- done --- [main.java]             // note: in this function format101 obs and FM13 obs were already made!
   //                             - RS232_Output_obs_by_email_all_APR() --- done --- [main_RS232_RS422.java] // note: in this function format101 obs and FM13 obs were already made!
    
   
   new SwingWorker<Integer, Void>()
   {
      
      // /***************** TEST BEGIN *********/
            
      //:: GMAIL_TLS (with app password)
      //::.\dist\email_tbw.exe "GMAIL_TLS" "smtp.gmail.com" "xxxineinjletryoohdq" "martin.stam@home.nl" "turbowin.observations@gmail.com" "dit is een test via GMAIL_TLS with app password" "This is an obs test body line"  
      //      String smtp_mode = "GMAIL_TLS";
      //      String smtp_host_local = "smtp.gmail.com";
      //      String smtp_password_local = "xxxineinjletryooxxx";
      //      String send_to = obs_email_recipient;                    //"martin.stam@home.nl,martin.stam@knmi.nl";
      //      String send_from = "turbowin.observations@gmail.com";
      //      String email_subject = obs_email_subject;                //"dit is een test2 via GMAIL_TLS with app password";
      //      String email_body = "This is an obs test body line";
      
      // /***************** TEST END **********/
      
      
      @Override
      protected Integer doInBackground() throws Exception        
      {
         //
         ///////////////////////// PREPARE EMAIL PARAMETERS //////////////////////
         //
         
         // initialisation
         String smtp_mode           = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String smtp_host_local     = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String smtp_password_local = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String send_to             = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String send_from           = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script                                 
         String email_subject       = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String email_body          = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script   
         String send_cc             = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script 
         String smtp_port_local     = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script 
         String attachment          = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script 
         
         
         ///////////////////////// EMAIL VIA SMTP HOST (disabled from version 4.2) //////////////////////
         if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_LOCAL_HOST)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_SMTP_HOST)) )
         {
            JOptionPane.showMessageDialog(null, "invalid email send method (Maintenance -> Email settings, insert the CUSTOM settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         } // else if (email_send_mode.equals(EMAIL_SEND_LOCAL_HOST)) etc.
         
         ///////////////////////// EMAIL VIA GMAIL (disabled from version 4.2) //////////////////////
         else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_GMAIL)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_GMAIL)) )
         {
            JOptionPane.showMessageDialog(null, "invalid email send method (Maintenance -> Email settings, insert the CUSTOM settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         } // else if (email_send_mode.equals(EMAIL_SEND_GMAIL)) etc.
         
         ///////////////////////// EMAIL VIA YAHOO (disabled from version 4.2) //////////////////////
         else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_YAHOO)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_YAHOO_MAIL)) )
         {
            JOptionPane.showMessageDialog(null, "invalid email send method (Maintenance -> Email settings, insert the CUSTOM settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         } // else if (email_send_mode.equals(EMAIL_SEND_YAHOO)) etc.
         
         
         ///////////////////////// EMAIL VIA CUSTOM //////////////////////
         else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_CUSTOM)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL)) )
         {
            smtp_mode           = custom_security;                        // CUSTOM_TLS_STARTTLS / CUSTOM_TLS / CUSTOM_SSL_STARTTLS / CUSTOM_SSL
            smtp_host_local     = custom_email_server;                    // eg "smtp.mail.special.com";                       
            smtp_password_local = custom_password;
            send_to             = obs_email_recipient;                    // eg "martin.stam@home.nl,martin.stam@knmi.nl";
            send_from           = your_custom_address;                    // eg nedlloyd_ebro@nedlloyd.nl
            email_subject       = obs_email_subject;                      // nb can be overwritten in case of FM13 "ddhhmm"                     
            email_body          = "null";                                 // nb can be overwritten, see below
            smtp_port_local     = custom_port;                            // eg 587
            attachment          = "null";                                 // nb can be overwritten, FM13 never in attachment; format101 only if indicated by the user
             
            if (obs_email_cc.length() > 3)
            {
               send_cc = obs_email_cc;
            }
            else
            {
               send_cc = "null";
            }
            
            if (obs_format.equals(FORMAT_101) || (main.obs_format.equals(main.FORMAT_AWS) && main.eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN)))
            {
               if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT))
               {
                  attachment = "yes";
               }
            }                 
         } // else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_CUSTOM)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL)) )     
         
         
         
         //
         ///////////////////////// PREPARE OBS (format101 or FM13) //////////////////////
         //
         int jakarta_email_status = 0;
         boolean doorgaan = true;
       
         if ( (manual_send == true && (email_send_mode.equals(EMAIL_SEND_CUSTOM))) ||
              (manual_send == false && (APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL))) )  
         {
            if (obs_format.equals(FORMAT_101) || (main.obs_format.equals(main.FORMAT_AWS) && main.eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN)))
            {
               // NB format 101 message was already prepared in function: Output_obs_by_email_all() [main.java]
               //
               if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT))
               {
                  email_body = "see attachment";
               }
               else
               {   
                  // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
                  email_body = get_format_101_obs_from_file();
                  if (email_body.equals("") == true)                // no format101 message found
                  {
                     doorgaan = false;
                  }
               } // else
            } // if (obs_format.equals(FORMAT_101)) etc.
      
            if (obs_format.equals(FORMAT_FM13))
            {
               // if ddmmyyyy in subject field -> replace by actual utc date of observation 
               //String obs_email_subject_new = obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");
               //obs_email_subject = obs_email_subject_new;
               
               if (obs_email_subject.contains("ddhhmm"))
               {
                  email_subject = obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");
               }
               
               
               email_body = obs_write;
               if (email_body.equals("") == true)
               {
                  doorgaan = false;
               }
            } // if (obs_format.equals(FORMAT_FM13))
      
            if (!doorgaan)
            {
               jakarta_email_status = 1001;        // empty obs
            }
         } // if (email_send_mode.equals(EMAIL_SEND_LOCAL_HOST) || email_send_mode.equals(EMAIL_SEND_GMAIL) || email_send_mode.equals(EMAIL_SEND_YAHOO) etc.
         else
         {
            jakarta_email_status = 1002;          // invalid email_send_mode
            doorgaan = false;
         }
          
         
         //
         ///////////////////////// INVOKE JAKARTA EMAIL MODULE //////////////////////
         //
         if (doorgaan) // so no empty obs and send mode = ok
         {
            String info_cc         = "";                  // only for logging       
            String info_port       = "";                  // only for logging      
            String info_attachment = "";                  // only for logging    
            
            if (send_cc.equals("null"))    // no cc
            {
               //main.log_turbowin_system_message("[EMAIL] trying to send obs (" + email_body + ") to " + send_to + " from " + send_from + " via " + smtp_mode);
               info_cc = "none";
            }
            else // send also to the cc
            {
               info_cc = send_cc;
            }
            
            if (smtp_port_local.equals("null"))
            {
               info_port = "system defined";
            }
            else
            {
               info_port = smtp_port_local;
            }
            
            if (attachment.equals("null"))
            {
               info_attachment = "none";
            }
            else
            {
               info_attachment = "yes";
            }
            
            main.log_turbowin_system_message("[EMAIL] trying to send obs (body= " + "\"" + email_body + "\"" + ") to " + send_to + " cc " + info_cc + " from " + send_from + " via " + smtp_mode + " port " + info_port + " attachment " + info_attachment + " [primary email module]");
            
            
            if (jakarta_email_class == null)
            {                                
               jakarta_email_class = new Jakarta_Email();
            }

         
            String smtp_password_local_plain = "";
            if (smtp_password_local.equals("null") == false)
            {
               // NB smtp_password_local = encrypted -> decrypt it before passing it to the jakarta email module
               smtp_password_local_plain = myemailsettings.decrypt(smtp_password_local);   
            }
            else
            {
               // NB so a null value was not encrypted so do also not decrypt (?? mode: EMAIL_SEND_LOCAL_HOST)
               smtp_password_local_plain = "null";
            }
               
            // invoke the (jakarta)email send function
            
            //// TEST ///
            //System.out.println("--- smtp_host_local = " + smtp_host_local);
            //System.out.println("--- send_to = " + send_to);
            
            int exit_status = jakarta_email_class.send_jakarta_email_obs(smtp_mode, smtp_host_local, smtp_password_local_plain, send_to, send_from, email_subject, email_body, send_cc, smtp_port_local, attachment);
               
            
            jakarta_email_status = exit_status;
            
         } //  if (doorgaan)
         
         
         return jakarta_email_status;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         try 
         {
            int jakarta_email_status = get();
            
            if (jakarta_email_status == 0)            // OK
            {  
               IMMT_log();
               Reset_all_meteo_parameters();
               
               // NB also already written 'success' to system log
               
               if (manual_send == true)
               {
                  String info = "sent obs successfully";
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
               }
            } 
            else if (jakarta_email_status == 1001)        // empty obs
            {
               Reset_all_meteo_parameters();
               // NB because empty obs, do not write to IMMT log
               
               String info = "send obs failed (observation contains no data due to an internal error)";
               main.log_turbowin_system_message("[EMAIL] " + info);
               
               if (manual_send == true)
               {   
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE); 
               }
            }
            else if (jakarta_email_status == 1002)        // invalid send mode
            {
               IMMT_log();
               Reset_all_meteo_parameters();
               
               String info = "send obs failed (invalid manual or AP[T]R/AWSR email send mode)";
               main.log_turbowin_system_message("[EMAIL] " + info);
               
               if (manual_send == true)
               {   
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE); 
               }
            }
            else // failed to send
            {
               IMMT_log();
               Reset_all_meteo_parameters();
               
               // NB already written the cause of the failure to the system log (see doInBackground())
               
               if (manual_send == true)
               {   
                  String info = "send obs failed (see Info -> System log)";
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               }
            }
         } // try
         catch (InterruptedException | ExecutionException ex) 
         {
            main.log_turbowin_system_message("[EMAIL] error invoking email module (" + ex + ")");
            
            if (manual_send == true)
            {
               String info = "send obs failed (check Info -> System log)";
               System.out.println(info);
               JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         } // catch
         
      } // protected void done()

   }.execute(); // new SwingWorker<Void, Void>()
      
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void Output_obs_by_email_Localhost_Gmail_Yahoo_FM13_format_101(boolean manual_send) // NB also for Custom email !
{
   // NB manual_send = true/false   (set in Output_obs_by_email_all_manual() [main.java] or part of AP[&T]R / AWSR)
   //
   // NB This function called by: - Output_obs_by_email_all_manual() [main.java]                // note: in this function format101 obs and FM13 obs were already made!
   //                             - RS232_Output_obs_by_email_all_APR() [main_RS232_RS422.java] // note: in this function format101 obs and FM13 obs were already made!
   
   
   new SwingWorker<Integer, Void>()
   {
      
      // /***************** TEST BEGIN *********/
            
      //:: GMAIL_TLS (with app password)
      //::.\dist\email_tbw.exe "GMAIL_TLS" "smtp.gmail.com" "xxxineinjletryoohdq" "martin.stam@home.nl" "turbowin.observations@gmail.com" "dit is een test via GMAIL_TLS with app password" "This is an obs test body line"  
      //      String smtp_mode = "GMAIL_TLS";
      //      String smtp_host_local = "smtp.gmail.com";
      //      String smtp_password_local = "xxxineinjletryooxxx";
      //      String send_to = obs_email_recipient;                    //"martin.stam@home.nl,martin.stam@knmi.nl";
      //      String send_from = "turbowin.observations@gmail.com";
      //      String email_subject = obs_email_subject;                //"dit is een test2 via GMAIL_TLS with app password";
      //      String email_body = "This is an obs test body line";
      
      // /***************** TEST END **********/
      
      
      @Override
      protected Integer doInBackground() throws Exception        
      {
         //
         ///////////////////////// PREPARE EMAIL PARAMETERS //////////////////////
         //
         
         // initialisation
         String smtp_mode           = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String smtp_host_local     = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String smtp_password_local = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String send_to             = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String send_from           = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script                                 
         String email_subject       = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script
         String email_body          = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script   
         String send_cc             = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script 
         String smtp_port_local     = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script 
         String attachment          = "null";   // NB do not insert "" here because this will be considerd as a 'none' argument for the python script 
         
         
         ///////////////////////// EMAIL VIA SMTP HOST (disabled from version 4.2) //////////////////////
         if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_LOCAL_HOST)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_SMTP_HOST)) )
         {
            JOptionPane.showMessageDialog(null, "invalid email send method (Maintenance -> Email settings, insert the CUSTOM settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            
         /*   
            smtp_mode           = SMTP_HOST_SHIP;                         // 
            smtp_host_local     = local_email_server;                     // eg "smtp.ziggo.nl";
            //smtp_password_local = "null";
            send_to             = obs_email_recipient;                    // eg "martin.stam@home.nl,martin.stam@knmi.nl";
            send_from           = your_ship_address;
            email_subject       = obs_email_subject;                      // nb can be overwritten in case of FM13 "ddhhmm"                       
            email_body          = "null";                                 // nb can be overwritten, see below
            attachment          = "null";                                 // nb can be overwritten, FM13 never in body; format101 only if indicated by the user
            
            if (obs_email_cc.length() > 3)                                // now we are sure it is a valid email address
            {
               send_cc = obs_email_cc;
            }
            else
            {
               send_cc = "null";
            }
            
            if (smtp_host_password.length() > 3)
            {
               smtp_password_local = smtp_host_password;
               smtp_port_local     = smtp_host_port;                      // port only used if password was also available/inserted
            }
            else
            {
               smtp_password_local = "null";
               smtp_port_local     = "null";
            }  
            
            if (obs_format.equals(FORMAT_101))
            {
               if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT))
               {
                  attachment = "yes";
               }
            }
         */
         } // else if (email_send_mode.equals(EMAIL_SEND_LOCAL_HOST)) etc.
         
         
         ///////////////////////// EMAIL VIA GMAIL (disabled from version 4.2) //////////////////////
         else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_GMAIL)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_GMAIL)) )
         {
            JOptionPane.showMessageDialog(null, "invalid email send method (Maintenance -> Email settings, insert the CUSTOM settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         /*
            smtp_mode           = gmail_security;                         // eg GMAIL_TLS";
            smtp_host_local     = "smtp.gmail.com";                       
            smtp_password_local = gmail_app_password;
            send_to             = obs_email_recipient;                    // eg "martin.stam@home.nl,martin.stam@knmi.nl";
            send_from           = your_gmail_address;
            email_subject       = obs_email_subject;                      // nb can be overwritten in case of FM13 "ddhhmm"                     
            email_body          = "null";                                 // nb can be overwritten, see below
            smtp_port_local     = "null";                                 // port not used
            attachment          = "null";                                 // nb can be overwritten, FM13 never in attachment; format101 only if indicated by the user
            
            if (obs_email_cc.length() > 3)
            {
               send_cc = obs_email_cc;
            }
            else
            {
               send_cc = "null";
            }
            
            if (obs_format.equals(FORMAT_101))
            {
               if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT))
               {
                  attachment = "yes";
               }
            }
         */
         } // else if (email_send_mode.equals(EMAIL_SEND_GMAIL)) etc.
         
         
         ///////////////////////// EMAIL VIA YAHOO (disabled from version 4.2) //////////////////////
         else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_YAHOO)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_YAHOO_MAIL)) )
         {
            JOptionPane.showMessageDialog(null, "invalid email send method (Maintenance -> Email settings, insert the CUSTOM settings)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         /*
            smtp_mode           = yahoo_security;                         // eg YAHOO_TLS";
            smtp_host_local     = "smtp.mail.yahoo.com";                       
            smtp_password_local = yahoo_app_password;
            send_to             = obs_email_recipient;                    // eg "martin.stam@home.nl,martin.stam@knmi.nl";
            send_from           = your_yahoo_address;
            email_subject       = obs_email_subject;                      // nb can be overwritten in case of FM13 "ddhhmm"                     
            email_body          = "null";                                 // nb can be overwritten, see below
            smtp_port_local     = "null";                                 // port not used
            attachment          = "null";                                 // nb can be overwritten, FM13 never in attachment; format101 only if indicated by the user
            
            if (obs_email_cc.length() > 3)
            {
               send_cc = obs_email_cc;
            }
            else
            {
               send_cc = "null";
            }
            
            if (obs_format.equals(FORMAT_101))
            {
               if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT))
               {
                  attachment = "yes";
               }
            }
         */
         } // else if (email_send_mode.equals(EMAIL_SEND_YAHOO)) etc.
         
         
         ///////////////////////// EMAIL VIA CUSTOM //////////////////////
         else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_CUSTOM)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL)) )
         {
            smtp_mode           = custom_security;                        // CUSTOM_TLS_STARTTLS / CUSTOM_TLS / CUSTOM_SSL_STARTTLS / CUSTOM_SSL
            smtp_host_local     = custom_email_server;                    // eg "smtp.mail.special.com";                       
            smtp_password_local = custom_password;
            send_to             = obs_email_recipient;                    // eg "martin.stam@home.nl,martin.stam@knmi.nl";
            send_from           = your_custom_address;                    // eg nedlloyd_ebro@nedlloyd.nl
            email_subject       = obs_email_subject;                      // nb can be overwritten in case of FM13 "ddhhmm"                     
            email_body          = "null";                                 // nb can be overwritten, see below
            smtp_port_local     = custom_port;                            // eg 587
            attachment          = "null";                                 // nb can be overwritten, FM13 never in attachment; format101 only if indicated by the user
             
            if (obs_email_cc.length() > 3)
            {
               send_cc = obs_email_cc;
            }
            else
            {
               send_cc = "null";
            }
            
            //if (obs_format.equals(FORMAT_101))
            //{
            //   if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT))
            //   {
            //      attachment = "yes";
            //   }
            //}    
            if (obs_format.equals(FORMAT_101) || (main.obs_format.equals(main.FORMAT_AWS) && main.eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN)))
            {
               if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT))
               {
                  attachment = "yes";
               }
            } 
            
         } // else if ( (manual_send == true && email_send_mode.equals(EMAIL_SEND_CUSTOM)) || (manual_send == false && APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL)) )     
         
         
         
         //
         ///////////////////////// PREPARE OBS (format101 or FM13) //////////////////////
         //
         int python_email_status = 0;
         boolean doorgaan = true;
      /*  
         // shortcuts to SMTP host, Gmail and Yhoo disabled from version 4.2
         if ( (manual_send == true && (email_send_mode.equals(EMAIL_SEND_LOCAL_HOST) || 
                                       email_send_mode.equals(EMAIL_SEND_GMAIL) || 
                                       email_send_mode.equals(EMAIL_SEND_YAHOO) ||
                                       email_send_mode.equals(EMAIL_SEND_CUSTOM))) ||
              (manual_send == false && (APTR_AWSR_send_method.equals(APTR_AWSR_SMTP_HOST) || 
                                        APTR_AWSR_send_method.equals(APTR_AWSR_GMAIL) || 
                                        APTR_AWSR_send_method.equals(APTR_AWSR_YAHOO_MAIL) ||
                                        APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL))) )  
      */   
         if ( (manual_send == true && (email_send_mode.equals(EMAIL_SEND_CUSTOM))) ||
              (manual_send == false && (APTR_AWSR_send_method.equals(APTR_AWSR_CUSTOM_MAIL))) )  
         {
            //if (obs_format.equals(FORMAT_101))
            if (obs_format.equals(FORMAT_101) || (main.obs_format.equals(main.FORMAT_AWS) && main.eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN)))
            {
               // NB format 101 message was already prepared in function: Output_obs_by_email_all() [main.java]
               //
               if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT))
               {
                  email_body = "see attachment";
               }
               else
               {   
                  // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
                  email_body = get_format_101_obs_from_file();
                  if (email_body.equals("") == true)                // no format101 message found
                  {
                     doorgaan = false;
                  }
               } // else
            } // if (obs_format.equals(FORMAT_101))
      
            if (obs_format.equals(FORMAT_FM13))
            {
               // if ddmmyyyy in subject field -> replace by actual utc date of observation 
               //String obs_email_subject_new = obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");
               //obs_email_subject = obs_email_subject_new;
               
               if (obs_email_subject.contains("ddhhmm"))
               {
                  email_subject = obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");
               }
               
               
               email_body = obs_write;
               if (email_body.equals("") == true)
               {
                  doorgaan = false;
               }
            } // if (obs_format.equals(FORMAT_FM13))
      
            if (!doorgaan)
            {
               python_email_status = 1001;        // empty obs
            }
         } // if (email_send_mode.equals(EMAIL_SEND_LOCAL_HOST) || email_send_mode.equals(EMAIL_SEND_GMAIL) || email_send_mode.equals(EMAIL_SEND_YAHOO) etc.
         else
         {
            python_email_status = 1002;          // invalid email_send_mode
            doorgaan = false;
         }
          
         
         //
         ///////////////////////// INVOKE PYTHON EMAIL MODULE //////////////////////
         //
         if (doorgaan) // so no empty obs and send mode = ok
         {
            String info_cc         = "";                  // only for logging       
            String info_port       = "";                  // only for logging      
            String info_attachment = "";                  // only for logging    
            
            if (send_cc.equals("null"))    // no cc
            {
               //main.log_turbowin_system_message("[EMAIL] trying to send obs (" + email_body + ") to " + send_to + " from " + send_from + " via " + smtp_mode);
               info_cc = "none";
            }
            else // send also to the cc
            {
               //main.log_turbowin_system_message("[EMAIL] trying to send obs (" + email_body + ") to " + send_to + " cc " + send_cc + " from " + send_from + " via " + smtp_mode);
               info_cc = send_cc;
            }
            
            if (smtp_port_local.equals("null"))
            {
               info_port = "system defined";
            }
            else
            {
               info_port = smtp_port_local;
            }
            
            if (attachment.equals("null"))
            {
               info_attachment = "none";
            }
            else
            {
               info_attachment = "yes";
            }
            
            //main.log_turbowin_system_message(info);
            main.log_turbowin_system_message("[EMAIL] trying to send obs (body= " + "\"" + email_body + "\"" + ") to " + send_to + " cc " + info_cc + " from " + send_from + " via " + smtp_mode + " port " + info_port + " attachment " + info_attachment + " [secondary email module]");
            
            
            if (python_email_class == null)
            {                                
               python_email_class = new Python_Email();
            }

            boolean python_email_found_ok = false;
            python_email_found_ok = python_email_class.python_email_control_center();         
         
            if (python_email_found_ok) // 'python email exe' copied sucessfully (this time or already in the past) from jar to destination or was already present
            {  
               String smtp_password_local_plain = "";
               if (smtp_password_local.equals("null") == false)
               {
                  // NB smtp_password_local = encrypted -> decrypt it before passing it to the python email module
                  smtp_password_local_plain = myemailsettings.decrypt(smtp_password_local);   
               }
               else
               {
                  // NB so a null value was not encrypted so do also not decrypt (mode: EMAIL_SEND_LOCAL_HOST)
                  smtp_password_local_plain = "null";
               }
               
               // invoke the (python)email exe
               int exit_status = python_email_class.send_python_email(smtp_mode, smtp_host_local, smtp_password_local_plain, send_to, send_from, email_subject, email_body, send_cc, smtp_port_local, attachment);
               
               // convert the numerical return status to text line return status + write to system log
               String exit_status_text = python_email_class.python_email_exe_return_status_to_text(exit_status);
               main.log_turbowin_system_message("[EMAIL] " + exit_status_text);
            
               python_email_status = exit_status;
            }
            else // python module not found / copy-error
            {
               python_email_status = 1000;  // python module not found / copy-error
            }
         } //  if (doorgaan)
         
         
         return python_email_status;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         try 
         {
            int python_email_status = get();
            
            if (python_email_status == 0)            // OK
            {  
               IMMT_log();
               Reset_all_meteo_parameters();
               
               // NB also already written 'success' to system log
               
               if (manual_send == true)
               {
                  String info = "sent obs successfully";
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
               }
            } 
            else if (python_email_status == 1000) // copy failure or python email exe not find
            {
               IMMT_log();
               Reset_all_meteo_parameters();
               
               // NB already written the cause of the failure to the system log (and ../logs/python/log_python_email.txt) (see copy_python_email_module() [Python_Email.java])
               
               if (manual_send == true)
               {
                  String info = "error invoking email module (copy-error from jar to destination) (check Info -> System log)";
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               }
            }
            else if (python_email_status == 1001)        // empty obs
            {
               Reset_all_meteo_parameters();
               // NB because empty obs, do not write to IMMT log
               
               String info = "send obs failed (observation contains no data due to an internal error)";
               main.log_turbowin_system_message("[EMAIL] " + info);
               
               if (manual_send == true)
               {   
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE); 
               }
            }
            else if (python_email_status == 1002)        // invalid send mode
            {
               IMMT_log();
               Reset_all_meteo_parameters();
               
               String info = "send obs failed (invalid manual or AP[T]R/AWSR email send mode)";
               main.log_turbowin_system_message("[EMAIL] " + info);
               
               if (manual_send == true)
               {   
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE); 
               }
            }
            else // failed to send
            {
               IMMT_log();
               Reset_all_meteo_parameters();
               
               // NB already written the cause of the failure to the system log (see doInBackground())
               
               if (manual_send == true)
               {   
                  String info = "send obs failed (see Info -> System log)";
                  JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               }
            }
         } // try
         catch (InterruptedException | ExecutionException ex) 
         {
            main.log_turbowin_system_message("[EMAIL] error invoking email module (" + ex + ")");
            
            if (manual_send == true)
            {
               String info = "send obs failed (check Info -> System log)";
               System.out.println(info);
               JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         } // catch
         
      } // protected void done()

   }.execute(); // new SwingWorker<Void, Void>()
   
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Output_obs_by_email_FM13()
{
   // This function called by: Output_obs_by_email_all()

   new SwingWorker<Void, Void>()
   {
      @Override
      protected Void doInBackground() throws Exception
      {
         /*
         // Version 6 of the Java Platform, Standard Edition (Java SE), continues to narrow the gap with
         // new system tray functionality, better  print support for JTable, and now the Desktop API
         // (java.awt.Desktop API).
         //
         // Use the Desktop.isDesktopSupported() method to determine whether the Desktop API is available.
         // On the Solaris Operating System and the Linux platform, this API is dependent on Gnome libraries.
         // If those libraries are unavailable, this method will return false. After determining that the API is
         // supported, that is, the isDesktopSupported() returns true, the application can retrieve a Desktop
         // instance using the static method getDesktop().
         //
         */
         Desktop desktop = null;

         // Before more Desktop API is used, first check
         // whether the API is supported by this particular
         // virtual machine (VM) on this particular host.
         if (Desktop.isDesktopSupported())
         {
            desktop = Desktop.getDesktop();
            try
            {
               // if ddmmyyyy in subject field -> replace by actual utc date of observation 
         
               String obs_email_subject_new = obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");

               //String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=" + obs_write;
               String mail_txt = "";
               if (obs_email_cc.length() > 3)
               {
                  // NB after the email address you'll use a question mark to prefix the first variable, and ampersands ( & ) for each consecutive variable. (https://developer.yoast.com/guide-mailto-links/)
                  mail_txt = obs_email_recipient + "?cc=" + obs_email_cc + "&subject=" + obs_email_subject_new  + "&body=" + obs_write;
               }   
               else
               {
                  mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=" + obs_write;
               }
               
               
               URI uriMailTo = null;
               try
               {
                  uriMailTo = new URI("mailto", mail_txt, null);
               }
               catch (URISyntaxException ex)
               {
                  JOptionPane.showMessageDialog(null, "Error invoking default Email program (URISyntaxException)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               }

               desktop.mail(uriMailTo);

            } // try
            catch (IOException ex)
            {
               JOptionPane.showMessageDialog(null, "Error invoking default Email program", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         } // if (Desktop.isDesktopSupported())
         else // Desktop method not supported
         {
            // Now try to open with mailto
            //
            // Now (mailto)workarounds follow with 'best shots'
            //
            //
            // NB The main problem with using mailto is with breaking lines. Use %0A for carriage returns, %20 for spaces.
            // NB http://stackoverflow.com/questions/17373/how-do-i-open-the-default-mail-program-with-a-subject-and-body-in-a-cross-platfo
            // NB http://www.wsoftware.de/practices/proc-execs.html
            
            /* determine the OS this program is running on */
            String os = OSDetector.getOSString();
            
            
            //JOptionPane.showMessageDialog(null, "Error invoking default Email program (method not supported on this computer system)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            //Runtime runtime = Runtime.getRuntime();
            
            // NB Use %0A for carriage returns, %20 for spaces [see "urlEncode(obs_write)"]
            // if ddmmyyyy in subject field -> replace by actual utc date of observation 
            String obs_email_subject_new = urlEncode(obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00"));
        
            //String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=" + urlEncode(obs_write);
            String mail_txt = "";
            if (obs_email_cc.length() > 3)
            {
               // NB after the email address you'll use a question mark to prefix the first variable, and ampersands ( & ) for each consecutive variable. (https://developer.yoast.com/guide-mailto-links/)
               mail_txt = obs_email_recipient + "?cc" + obs_email_cc + "&subject=" + obs_email_subject_new  + "&body=" + urlEncode(obs_write);
            }   
            else
            {
               mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=" + urlEncode(obs_write);
            }
               
            //if (amos_mail) // DO NOT USE %0A for carriage returns and %20 for spaces [so NOT "urlEncode(obs_write)"] NB DIDN'T WORK EITHER SO FROM THIS VERSION NO PARTICULAR CODE FOR AMOS MAIL
            if (os.equals("WINDOWS"))   
            {
               try
               {
                  // deprecated from JDK19
                  //runtime.exec("cmd /c " + "start " + "mailto:" + mail_txt);
                  
                  // NB during a test on 25-05-2016 on Windows 10 Runtime.getRuntime(); worked only partially (the body contents wasn't copied to the amial client)
               
                  // create cmd array
                  String[] cmdArray = {"cmd", "/c", "start", "mailto:", mail_txt};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               }
               catch (IOException e)
               {
                  JOptionPane.showMessageDialog(null, "Error invoking default email client (-Desktop-method not supported on this computer system and Runtime alternatives failed)" , main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               }
            } // if (os.equals("WINDOWS")) 
            else if (os.equals("MACOS")) 
            {
               try
               {
                  // deprecated from JDK19
                  //runtime.exec("open " + "mailto:" + mail_txt);
                  
                  // create cmd array
                  String[] cmdArray = {"open", "mailto:", mail_txt};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               }
               catch (IOException e)
               {
                  JOptionPane.showMessageDialog(null, "Error invoking default email client (-Desktop-method not supported on this computer system and Runtime alternatives failed)" , main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               }
            } // else if (os.equals("MACOS")) 
            else // most probably a Linux;
            {
               try 
               {
                  // deprecated from JDK19
                  //runtime.exec("xdg-open " + "mailto:" + mail_txt);
                  
                  // create cmd array
                  String[] cmdArray = {"xdg-open", "mailto:", mail_txt};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               } 
               catch (IOException e2) 
               {
                  try
                  {
                     // deprecated from JDK19
                     //runtime.exec("kde-open " + "mailto:" + mail_txt);
                     
                     // create cmd array
                     String[] cmdArray = {"kde-open", "mailto:", mail_txt};

                     // create a process and execute cmdArray
                     Process process = Runtime.getRuntime().exec(cmdArray);
                  }
                  catch (IOException e3)
                  {
                     JOptionPane.showMessageDialog(null, "Error invoking default email client (-Desktop-method not supported on this computer system and Runtime alternatives failed)" , main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  }
               }
            } // else
         } // else (Desktop method not supported)
         
         
         return null;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         IMMT_log();
         
         Reset_all_meteo_parameters();
      } // protected void done()

   }.execute(); // new SwingWorker<Void, Void>()
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private static String urlEncode(String s) 
{
   // NB http://stackoverflow.com/questions/17373/how-do-i-open-the-default-mail-program-with-a-subject-and-body-in-a-cross-platfo
   
   StringBuilder sb = new StringBuilder();
   for (int i = 0; i < s.length(); i++) 
   {
       char ch = s.charAt(i);
       if (Character.isLetterOrDigit(ch)) 
       {
          sb.append(ch);
       }
       else 
       {
          sb.append(String.format("%%%02X", (int)ch));
       }
   }
   
   return sb.toString();
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Output_obs_by_email_format_101()
{
   // This function called by: Output_obs_by_email_all()
   

   new SwingWorker<Void, Void>()
   {
      @Override
      protected Void doInBackground() throws Exception
      {
         /*
         // Version 6 of the Java Platform, Standard Edition (Java SE), continues to narrow the gap with
         // new system tray functionality, better  print support for JTable, and now the Desktop API
         // (java.awt.Desktop API).
         //
         // Use the Desktop.isDesktopSupported() method to determine whether the Desktop API is available.
         // On the Solaris Operating System and the Linux platform, this API is dependent on Gnome libraries.
         // If those libraries are unavailable, this method will return false. After determining that the API is
         // supported, that is, the isDesktopSupported() returns true, the application can retrieve a Desktop
         // instance using the static method getDesktop().
         //
         */
         Desktop desktop        = null;
         String email_body_line = "";
         boolean doorgaan       = true;

         // Before more Desktop API is used, first check
         // whether the API is supported by this particular
         // virtual machine (VM) on this particular host.
         if (Desktop.isDesktopSupported())
         {
            desktop = Desktop.getDesktop();
            try
            {
               final String volledig_path_format_101_compressed_file = main.logs_dir + java.io.File.separator + FORMAT_101_ROOT_DIR + java.io.File.separator + FORMAT_101_TEMP_DIR + java.io.File.separator + "HPK_" + FORMAT_101_INPUT_FILE;// NB adding "HPK_" to the input file name is automatically done by the C-code compression functions
               
               //////// format 101 message in the e-mail body //////
               //
               if (main.obs_101_email.equals(FORMAT_101_BODY) == true)
               {
                  // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
                  email_body_line = get_format_101_obs_from_file();
                  if (email_body_line.equals("") == true)
                  {
                     doorgaan = false;
                  }
               } // if (main.obs_101_email.equals(FORMAT_101_BODY) == true);
               
               /////// format 101 message as attachment //////
               //
               else if (main.obs_101_email.equals(FORMAT_101_ATTACHEMENT) == true)
               {
                  // first check if compressed file exists
                  final File compressed_file = new File(volledig_path_format_101_compressed_file);

                  if (compressed_file.exists() == true)
                  {
                     email_body_line = "Please attach manually the file: " + volledig_path_format_101_compressed_file;
                     doorgaan = true;
                  }
                  else
                  {
                     JOptionPane.showMessageDialog(null, "No format 101 obs available. The following file does not exist: " + volledig_path_format_101_compressed_file, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                     doorgaan = false;
                  } // else
               } // else if (main.obs_101_email.equals(FORMAT_101_ATTACHEMENT) == true)
               
               
               /////// invoke email program //////
               //
               if (doorgaan == true)
               {
                  /* if ddmmyyyy in subject field -> replace by actual utc date of observation */
                  String obs_email_subject_new = obs_email_subject.replaceAll("ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");
                  
                  //String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=" + email_body_line;
                  String mail_txt = "";
                  if (obs_email_cc.length() > 3)
                  {
                     // NB after the email address you'll use a question mark to prefix the first variable, and ampersands ( & ) for each consecutive variable. (https://developer.yoast.com/guide-mailto-links/)
                     mail_txt = obs_email_recipient + "?cc=" + obs_email_cc + "&subject=" + obs_email_subject_new  + "&body=" + email_body_line;        
                  }
                  else
                  {
                     mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body=" + email_body_line;
                  }
                  
                  
                  URI uriMailTo = null;
                  try
                  {
                     uriMailTo = new URI("mailto", mail_txt, null);
                  }
                  catch (URISyntaxException ex)
                  {
                     JOptionPane.showMessageDialog(null, "Error invoking default Email program" + " (" + ex + ")", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
                  }

                  desktop.mail(uriMailTo);
               } // if (doorgaan == true)
            } // try
            catch (IOException ex)
            {
               JOptionPane.showMessageDialog(null, "Error invoking default Email program" + " (" + ex + ")", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            }
         } // if (Desktop.isDesktopSupported())
         else
         {
            JOptionPane.showMessageDialog(null, "Error invoking default Email program (-Desktop- method not supported on this computer system)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         } // else

         return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         IMMT_log();
         
         Reset_all_meteo_parameters();
      } // protected void done()

   }.execute(); // new SwingWorker<Void, Void>()
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private static String get_format_101_obs_from_file()
{
   String format_101_obs = "";
   
         
   final String volledig_path_format_101_compressed_file = main.logs_dir + java.io.File.separator + FORMAT_101_ROOT_DIR + java.io.File.separator + FORMAT_101_TEMP_DIR + java.io.File.separator + "HPK_" + FORMAT_101_INPUT_FILE;// NB adding "HPK_" to the input file name is automatically done by the C-code compression functions

   try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_format_101_compressed_file)))
   {
      if ((format_101_obs = in.readLine()) == null)
      {
         //JOptionPane.showMessageDialog(null, "When retrieveing format 101 data empty file: " + volledig_path_format_101_compressed_file, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
         System.out.println("--- error when retrieveing format 101 data empty file: " + volledig_path_format_101_compressed_file);
         format_101_obs = "";                // the function which invoke get_format_101_obs_from_file() will check this value
      } // else
                     
   } // try
   catch (IOException | HeadlessException e)
   {
      //JOptionPane.showMessageDialog(null, "When retrieving format 101 data error opening file: " + volledig_path_format_101_compressed_file + " (" + e + ")", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      System.out.println("--- error when retrieving format 101 data error opening file: " + volledig_path_format_101_compressed_file);
      format_101_obs = "";                   // the function which invoke get_format_101_obs_from_file() will check this value
   } // catch         
      
   //  JOptionPane.showMessageDialog(null, format_101_obs, "test inhoud format_101_obs" + " error", JOptionPane.WARNING_MESSAGE);

   return format_101_obs;
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
/*
private void Output_obs_to_server_FM13_TurboWeb()
{
   // NB only for TurboWeb because then the upload URL (url_basis) is known by default
   
   new SwingWorker<Void, Void>()
   {
      @Override
      protected Void doInBackground() throws Exception
      {
         BasicService bs = null;
         try
         {
            bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
         }
         catch (UnavailableServiceException ex) { }

         if (bs != null)
         {   
            URL url_basis = bs.getCodeBase();
            try
            {
               //String SPATIE = SPATIE_OBS_SERVER;                                     // dan "%20" als spatie grbruiken
               //url_php = new URL(url_basis + (String)compose_coded_obs(SPATIE));
               url_php = new URL(url_basis + "index_webstart.php?obs=" + obs_write);    // obs_write is a global var (set via: obs_write = compose_coded_obs(SPATIE);)

               // LET OP
               // NB met IE7 gaat als je voor spatie een " " neemt het wel goed
               //    met FireFox gaat als je voor spatie " " neemt het NIET goed

            } // try
            catch (MalformedURLException ex) {  }

            //this.getAppletContext().showDocument(url);
            bs.showDocument(url_php);
         }
         
         return null;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         IMMT_log();
         
         Reset_all_meteo_parameters();
      } // protected void done()
   }.execute(); // new SwingWorker<Void, Void>()

}
*/


/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Output_obs_to_server_FM13_TurboWin_stand_alone()
{
   // NB there is also a TurboWeb version [Output_obs_to_server_FM13_TurboWeb()]
   
   // http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
   //
   // NB see also: RS232_Send_Sensor_Data_to_APR_format101_Server() [main_RS232_RS422.java]
   //
   // called from: Output_Obs_to_server_menu_actionPerformed() [main.java]
   
   // Temporary message box, (pop-up for only a short time) automatically disappears
   //
   support_class.response_warning_pop_up();
   
   new SwingWorker<Integer, Void>()
   {
      @Override
      protected Integer doInBackground() throws Exception
      {
         Integer responseCode = main.OK_RESPONSE_FORMAT_FM13;            // OK 
         
         // compose coded obs [obs_write = FM13 obs !!!]
         // NB already done in Output_Obs_to_server_menu_actionPerformed()
         // String SPATIE = main.SPATIE_OBS_SERVER;                                   // use "_" as marker between obs groeps
         // main.obs_write = main.compose_coded_obs(SPATIE);                          // returns UNDEFINED if call sign, position or date/time not inserted

         if ( (main.obs_write.equals("") == true) || (main.obs_write.equals(main.UNDEFINED) == true) )
         {
            responseCode = main.INVALID_RESPONSE_FORMAT_FM13;        
         }
         
         
         if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_FM13))   
         {   
            // NB encoding not necessary for FM13, but if necessary in the future see the comments below
            
            // NB encoding:
            // Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme. This method uses the supplied encoding scheme to obtain 
            // the bytes for unsafe characters.
            // Note: The World Wide Web Consortium Recommendation states that UTF-8 should be used. Not doing so may introduce incompatibilites.
            // 
            // http://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters:
            // You only need to keep in mind to encode only the individual query string parameter name and/or value, not the entire URL, 
            // for sure not the query string parameter separator character & nor the parameter name-value separator character =.
            // String q = "random word 500 bank $";
            // String url = "http://example.com/query?q=" + URLEncoder.encode(q, "UTF-8");
            //
            // Encode all 'not alloud' ASCII chars if not java.net.URISyntaxException (with index number in the URL string)
            //String encoded_server_format_101_obs = URLEncoder.encode(server_format_101_line, "UTF-8");               
               
            ////String url = "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?obs=" + encoded_server_format_101_obs; 
            //String url = upload_URL + "obs=" + encoded_server_format_101_obs;       // eg upload U?rL = http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?
            String url = upload_URL + "obs=" + main.obs_write;   
            
            URL obj = null;
            try 
            {
               //obj = new URL(url);        // deprecated in Java 20
               obj = new URI(url).toURL();
               HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            
               // optional (default is GET)
	            con.setRequestMethod("GET");  
               //con.setDoOutput(true);        //  To be clear: setting URLConnection#setDoOutput(true) to true implicitly sets the request method to POST
                  
               String message = "[MANUAL] sending 'GET' request to URL: " + url;
               main.log_turbowin_system_message(message);
     
               responseCode = con.getResponseCode();
      
               // NB besides the response code there is also a corresponding response text, but unfortunately with html tags, 
               //    and only with the standard response codes, self defined response codes are not returned?. Not suitable for direct using it into a popup message box
               //    so only using the reponse code and locally (in this program) determined the corresponding return http message text
               //
                     
                     
            } // try
            catch (MalformedURLException ex)
            {
               //String message = "[MANUAL] send obs failed; MalformedURLException (function: Output_obs_to_server_format_101())"; 
               //main.log_turbowin_system_message(message);
               //main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
               
               responseCode = RESPONSE_MALFORMED_URL;
            }
            catch (URISyntaxException ex)
            {
               responseCode = main.RESPONSE_MALFORMED_URL;
            }
            catch (IOException ex) 
            {
               //String message = "[MANUAL] send obs failed; IOException; most probably no internet connection available; (function: Output_obs_to_server_format_101())"; 
               //main.log_turbowin_system_message(message);
               //main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
               
               responseCode = RESPONSE_NO_INTERNET;
            } // catch            
            
         } // if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_101))
         
         
         return responseCode;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         try 
         {
            Integer response_code = get();
            
            if (response_code == 200)          // OK
            {
               String message_a = "[MANUAL] send obs success"; 
                  
               // file logging
               main.log_turbowin_system_message(message_a);
                  
               // bottom line main screen
               main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message_a); // update status field (bottom line main -progress- window) 
               
               // pop-up message in manual mode
               String info = "<html>" + 
                             main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs success" + "<br>" +
                             "Many thanks for your cooperation" + 
                             "</html>";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
               
               IMMT_log();
               Reset_all_meteo_parameters();
            }
            else // send obs NOT ok
            {
               // NB besides the response code there is also a corresponding response text (from the apache server, but unfortunately with html tags, 
               //    and only with the standard response codes, self defined response codes are not returned (from the apache server)?. 
               //    Not suitable for direct using it into a popup message box
               //    so only using the reponse code and locally (in this program) determined the corresponding return http message text
               //
               String message_b = "[MANUAL] send obs failed; " + http_respons_code_to_text(response_code).replace("<br>", " ");
                  
               // file logging
               main.log_turbowin_system_message(message_b);
                  
               // bottom main screen
               main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message_b); 
               
               // pop-up message in manual mode
               String info = "<html>" + 
                             main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs failed; " + "<br>" +
                             http_respons_code_to_text(response_code) + 
                             "</html>";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            
               // "try again?" pop-up message
               if (JOptionPane.showConfirmDialog(null, "try again (Obs to server)", APPLICATION_NAME + " ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
               {
                  // YES button pressed (= try again)
                  Output_obs_to_server_FM13_TurboWin_stand_alone();
               }
               else // NO or CANCEL clicked by the user
               {
                  IMMT_log();
                  Reset_all_meteo_parameters();
               }   
            } // else (send obs NOT ok)
         } // protected void done()
         catch (InterruptedException | ExecutionException ex) 
         {
            String message = "[MANUAL] error in Function: Output_obs_to_server_FM13_TurboWin_stand_alone();" + ex.toString(); 
            
            // fille logging 
            main.log_turbowin_system_message(message);
            
            // bottom main screen
            main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
            
            // pop-up message in manual mode
            String info = "<html>" + 
                          main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs failed; " + "<br>" +
                          http_respons_code_to_text(RESPONSE_INTERRUPTION) + 
                          "</html>";
            JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            
            // "try again?" pop-up message
            if (JOptionPane.showConfirmDialog(null, "try again (Obs to server)", APPLICATION_NAME + " ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
            {
               // YES button pressed (= try again)
               Output_obs_to_server_FM13_TurboWin_stand_alone();
            }
            else // NO or CANCEL clicked by the user
            {
               IMMT_log();
               Reset_all_meteo_parameters();
            }   
         } // catch (InterruptedException | ExecutionException ex) 
      } // protected void done()
   }.execute(); // new SwingWorker<Void, Void>()
   
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
/*
private void Output_obs_to_server_format_101()
{
   // http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
   //
   // NB see also: RS232_Send_Sensor_Data_to_APR_format101_Server() [main_RS232_RS422.java]
   //
   // called from: Output_Obs_to_server_menu_actionPerformed() [main.java]
   
   
   // Temporary message box, (pop-up for only a short time) automatically disappears
   //
   support_class.response_warning_pop_up();
   
   new SwingWorker<Integer, Void>()
   {
      @Override
      protected Integer doInBackground() throws Exception
      {
         String server_format_101_line = "";
         Integer responseCode          = OK_RESPONSE_FORMAT_101;            // OK
         
         
         // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
         server_format_101_line = get_format_101_obs_from_file();
         if (server_format_101_line.equals("") == true)
         {
            responseCode = INVALID_RESPONSE_FORMAT_101;                        // self defined
         }
      
   
         
         
         if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_101))   
         {   
            // NB encoding:
            // Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme. This method uses the supplied encoding scheme to obtain 
            // the bytes for unsafe characters.
            // Note: The World Wide Web Consortium Recommendation states that UTF-8 should be used. Not doing so may introduce incompatibilites.
            // 
            // http://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters:
            // You only need to keep in mind to encode only the individual query string parameter name and/or value, not the entire URL, 
            // for sure not the query string parameter separator character & nor the parameter name-value separator character =.
            // String q = "random word 500 bank $";
            // String url = "http://example.com/query?q=" + URLEncoder.encode(q, "UTF-8");
            //
            // Encode all 'not alloud' ASCII chars if not java.net.URISyntaxException (with index number in the URL string)
            String encoded_server_format_101_obs = URLEncoder.encode(server_format_101_line, "UTF-8");               
               
            //String url = "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?obs=" + encoded_server_format_101_obs; 
            String url = upload_URL + "obs=" + encoded_server_format_101_obs;       // eg upload U?rL = http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?
               
            URL obj = null;
            try 
            {
               //obj = new URL(url);   // deprecated in Java 20
               obj = new URI(url).toURL();
               HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            
               // optional (default is GET)
	            con.setRequestMethod("GET");  
               //con.setDoOutput(true);        //  To be clear: setting URLConnection#setDoOutput(true) to true implicitly sets the request method to POST
                  
               String message = "[MANUAL] sending 'GET' request to URL: " + url;
               main.log_turbowin_system_message(message);
     
               responseCode = con.getResponseCode();
      
               // NB besides the response code there is also a corresponding response text, but unfortunately with html tags, 
               //    and only with the standard response codes, self defined response codes are not returned?. Not suitable for direct using it into a popup message box
               //    so only using the reponse code and locally (in this program) determined the corresponding return http message text
               //
             
                     
            } // try
            catch (MalformedURLException ex)
            {
               //String message = "[MANUAL] send obs failed; MalformedURLException (function: Output_obs_to_server_format_101())"; 
               //main.log_turbowin_system_message(message);
               //main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
               
               responseCode = RESPONSE_MALFORMED_URL;
            }
            catch (URISyntaxException ex)
            {
               responseCode = main.RESPONSE_MALFORMED_URL;
            }
            catch (IOException ex) 
            {
               //String message = "[MANUAL] send obs failed; IOException; most probably no internet connection available; (function: Output_obs_to_server_format_101())"; 
               //main.log_turbowin_system_message(message);
               //main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
               
               responseCode = RESPONSE_NO_INTERNET;
            } // catch            
            
         } // if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_101))
         
         
         return responseCode;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         try 
         {
            Integer response_code = get();
            
            if (response_code == 200)          // OK
            {
               String message_a = "[MANUAL] send obs success"; 
                  
               // file logging
               main.log_turbowin_system_message(message_a);
                  
               // bottom line main screen
               main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message_a); // update status field (bottom line main -progress- window) 
               
               // pop-up message in manual mode
               String info = "<html>" + 
                             main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs success" + "<br>" +
                             "Many thanks for your cooperation" + 
                             "</html>";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
               
               IMMT_log();
               Reset_all_meteo_parameters();
            }
            else // send obs NOT ok
            {
               // NB besides the response code there is also a corresponding response text (from the apache server, but unfortunately with html tags, 
               //    and only with the standard response codes, self defined response codes are not returned (from the apache server)?. 
               //    Not suitable for direct using it into a popup message box
               //    so only using the reponse code and locally (in this program) determined the corresponding return http message text
               //
               String message_b = "[MANUAL] send obs failed; " + http_respons_code_to_text(response_code).replace("<br>", " ");
                  
               // file logging
               main.log_turbowin_system_message(message_b);
                  
               // bottom main screen
               main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message_b); 
               
               // pop-up message in manual mode
               String info = "<html>" + 
                             main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs failed; " + "<br>" +
                             http_respons_code_to_text(response_code) + 
                             "</html>";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            
               // "try again?" pop-up message
               if (JOptionPane.showConfirmDialog(null, "try again (Obs to server)", APPLICATION_NAME + " ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
               {
                  // YES button pressed (= try again)
                  Output_obs_to_server_format_101();
               }
               else // NO or CANCEL clicked by the user
               {
                  IMMT_log();
                  Reset_all_meteo_parameters();
               }   
            } // else (send obs NOT ok)
         } // protected void done()
         catch (InterruptedException | ExecutionException ex) 
         {
            String message = "[MANUAL] error in Function: Output_obs_to_server_format_101;" + ex.toString(); 
            
            // fille logging 
            main.log_turbowin_system_message(message);
            
            // bottom main screen
            main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
            
            // pop-up message in manual mode
            String info = "<html>" + 
                          main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs failed; " + "<br>" +
                          http_respons_code_to_text(RESPONSE_INTERRUPTION) + 
                          "</html>";
            JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            
            // "try again?" pop-up message
            if (JOptionPane.showConfirmDialog(null, "try again (Obs to server)", APPLICATION_NAME + " ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
            {
               // YES button pressed (= try again)
               Output_obs_to_server_format_101();
            }
            else // NO or CANCEL clicked by the user
            {
               IMMT_log();
               Reset_all_meteo_parameters();
            }   
         } // catch (InterruptedException | ExecutionException ex) 
      } // protected void done()
   }.execute(); // new SwingWorker<Void, Void>()

}
*/



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void Output_obs_to_server_format_101_V2()
{
   // http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
   //
   // NB see also: RS232_Send_Sensor_Data_to_APR_format101_Server() [main_RS232_RS422.java]
   //
   // called from: Output_Obs_to_server_menu_actionPerformed() [main.java]
   
   
   // Temporary message box, (pop-up for only a short time) automatically disappears
   //
   support_class.response_warning_pop_up();
   
   new SwingWorker<String, Void>()
   {
      @Override
      protected String doInBackground() throws Exception
      {
         String server_format_101_line = "";
         int responseCode                = OK_RESPONSE_FORMAT_101;                  // OK
         String responseString           = "";
         HttpURLConnection con_http      = null;
         HttpsURLConnection con_https    = null;
         
         boolean isHttps = true; 
         if (server_com_protocol.equals(HTTP_PROTOCOL))
         {   
            isHttps = false;  
         }  
         
         // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
         server_format_101_line = get_format_101_obs_from_file();
         if (server_format_101_line.equals("") == true)
         {
            responseCode = INVALID_RESPONSE_FORMAT_101;                        // self defined
         }
         
         
         if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_101))   
         {   
            // NB encoding:
            // Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme. This method uses the supplied encoding scheme to obtain 
            // the bytes for unsafe characters.
            // Note: The World Wide Web Consortium Recommendation states that UTF-8 should be used. Not doing so may introduce incompatibilites.
            // 
            // http://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters:
            // You only need to keep in mind to encode only the individual query string parameter name and/or value, not the entire URL, 
            // for sure not the query string parameter separator character & nor the parameter name-value separator character =.
            // String q = "random word 500 bank $";
            // String url = "http://example.com/query?q=" + URLEncoder.encode(q, "UTF-8");
            //
            // Encode all 'not allowed' ASCII chars if not java.net.URISyntaxException (with index number in the URL string)
            String encoded_server_format_101_obs = URLEncoder.encode(server_format_101_line, "UTF-8");               
               
            //String url = "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?obs=" + encoded_server_format_101_obs; 
            String url = upload_URL + "obs=" + encoded_server_format_101_obs;       // eg upload U?rL = http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?
 
            int maxRetries = 3;  // Maximum retries per IP
            boolean success = false;
            URL obj = null;
            try 
            {
               // Resolve all IP addresses for the given domain
               InetAddress[] addresses = InetAddress.getAllByName(new URI(url).toURL().getHost());
           
               // Loop through all resolved IP addresses
               for (InetAddress address : addresses)                          // Loop through all resolved IP addresses
               {
                  // Multiple Connection Attempts (Failover Mechanism).
                  // If one IP is encountering issues, another IP may work fine. This is particularly useful in cases where 
                  // certain routing or networking issues might cause a TCP reset.
                  
                  System.out.println("--- Trying IP: " + address.getHostAddress());
                  int attempt = 0;
                  int backoff = 1000; // Start with 1 second
                  while (attempt < maxRetries && !success)                     // Automatic Retries within same IP
                  {
                     // Automatic Retries with Exponential Backoff (Delaying Retries).
                     // TCP resets can be caused by transient network issues or server-side conditions that may be temporary. 
                     //  A retry helps in such cases by allowing the server to recover, or by avoiding network glitches that       
                     // could have caused the reset. Exponential backoff prevents overwhelming the server with rapid retry attempts 
                     // and gives time for transient errors to resolve. It also helps in reducing congestion on the network
                     
                     attempt++;
                     try
                     {   
                        //obj = new URL(url);   // deprecated in Java 20
                        obj = new URI(url).toURL();

                        if (isHttps) 
                        {
                           String message = "[MANUAL] sending 'GET' request (https) to URL: " + url;
                           main.log_turbowin_system_message(message);

                           con_https = (HttpsURLConnection)obj.openConnection();    // For HTTPS
                           con_https.setRequestMethod("GET");
                           responseCode = con_https.getResponseCode(); 
                        }
                        else
                        { 
                           String message = "[MANUAL] sending 'GET' request (http) to URL: " + url;
                           main.log_turbowin_system_message(message);

                           con_http = (HttpURLConnection)obj.openConnection();    // FOR HTTP
                           con_http.setRequestMethod("GET");
                           responseCode = con_http.getResponseCode(); 
                        } // else               

                     } // try
                     catch (MalformedURLException | URISyntaxException ex)
                     {
                        responseCode = RESPONSE_MALFORMED_URL;
                        responseString = ex.getMessage();
                     }
                     catch (SocketException se) 
                     {
                        // (hopefully...) also catching TCP errors ('TCP reset from server' / 'TCP RST')
                        responseCode = RESPONSE_NO_INTERNET;
                        responseString = se.getMessage();                                 // e.g. ........
                        
                        Thread.sleep(backoff);                                            // Wait before retrying
                        backoff *= 2;                                                     // Exponential backoff
                     }   
                     catch (IOException ix) 
                     {
                        responseCode = RESPONSE_NO_INTERNET;
                        responseString = ix.getMessage();                                 // e.g. "Permission denied: connect" (when firewall blocking)
                        
                        Thread.sleep(backoff);                                            // Wait before retrying
                        backoff *= 2;                                                     // Exponential backoff
                     } // catch (IOException ex)   
                     finally 
                     {
                        // Always disconnect to free up resources
                        if (isHttps && (con_https != null))
                        {
                           con_https.disconnect();
                        }
                        if ((!isHttps) && (con_http != null)) 
                        {
                           con_http.disconnect();
                        }
                     }  // finally

                     if (responseCode == 200) 
                     {
                        // Handle response
                        success = true;
                     }
                  } // while (attempt < maxRetries && !success)

                  if (success) 
                  {
                      // Exit the IP loop if one IP worked successfully
                     break;
                  }
               } //  for (InetAddress address : addresses) 

               if (!success) 
               {
                  System.out.println("--- All IP addresses failed after retries.");
               }

            } // try
            catch (UnknownHostException | MalformedURLException e)
            {
               responseCode = RESPONSE_NO_INTERNET;
               responseString = e.getMessage();
            } 

         } // if (Objects.equals(responseCode, OK_RESPONSE_FORMAT_101))
         
         String response = Integer.toString(responseCode);
         
         if (!responseString.equals(""))
         {
            response += " (" + responseString + ")";
         }   
         
         return response;

      } // protected Void doInBackground() throws Exception

      @Override
      protected void done()
      {
         try 
         {
            String response = get();
           
            int int_response_code = INTERNAL_ERROR_RESPONSE_CODE;                                          // default, initial value
            String response_error = "";
      
            try
            {
               String string_response_code = response.substring(0,3);             // e.g. "200"
               try
               {   
                  int_response_code = Integer.parseInt(string_response_code); 
                  if (response.length() > 3)
                  {   
                     response_error = response.substring(3);
                  }   
               }
               catch (NumberFormatException e2)
               {
                  int_response_code = INTERNAL_ERROR_RESPONSE_CODE;   
               }
            }
            catch (IndexOutOfBoundsException e)
            {
               int_response_code = INTERNAL_ERROR_RESPONSE_CODE;   
            }
            
            if (int_response_code == 200)          // OK
            {
               String message_a = "[MANUAL] send obs success"; 
                  
               // file logging
               main.log_turbowin_system_message(message_a);
                  
               // bottom line main screen
               main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message_a); // update status field (bottom line main -progress- window) 
               
               // pop-up message in manual mode
               String info = "<html>" + 
                             main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs success" + "<br>" +
                             "Many thanks for your cooperation" + 
                             "</html>";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
               
               IMMT_log();
               Reset_all_meteo_parameters();
            }
            else // send obs NOT ok
            {
               // NB besides the response code there is also a corresponding response text (from the apache server, but unfortunately with html tags, 
               //    and only with the standard response codes, self defined response codes are not returned (from the apache server)?. 
               //    Not suitable for direct using it into a popup message box
               //    so only using the reponse code and locally (in this program) determined the corresponding return http message text
               //
               String message_b = "[MANUAL] send obs failed; " + http_respons_code_to_text(int_response_code).replace("<br>", " ") + response_error;
                  
               // file logging
               main.log_turbowin_system_message(message_b);
                  
               // bottom main screen
               main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message_b); 
               
               // pop-up message in manual mode
               String info = "<html>" + 
                             main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs failed; " + "<br>" +
                             http_respons_code_to_text(int_response_code) + response_error +  
                             "</html>";
               JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            
               // "try again?" pop-up message
               if (JOptionPane.showConfirmDialog(null, "try again (Obs to server)", APPLICATION_NAME + " ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
               {
                  // YES button pressed (= try again)
                  Output_obs_to_server_format_101_V2();
               }
               else // NO or CANCEL clicked by the user
               {
                  IMMT_log();
                  Reset_all_meteo_parameters();
               }   
            } // else (send obs NOT ok)
         } // protected void done()
         catch (InterruptedException | ExecutionException ex) 
         {
            String message = "[MANUAL] error in Function: Output_obs_to_server_format_101_V2;" + ex.toString(); 
            
            // fille logging 
            main.log_turbowin_system_message(message);
            
            // bottom main screen
            main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
            
            // pop-up message in manual mode
            String info = "<html>" + 
                          main.sdf_tsl_2.format(new Date()) + " UTC " + "send obs failed; " + "<br>" +
                          http_respons_code_to_text(RESPONSE_INTERRUPTION) + 
                          "</html>";
            JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            
            // "try again?" pop-up message
            if (JOptionPane.showConfirmDialog(null, "try again (Obs to server)", APPLICATION_NAME + " ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
            {
               // YES button pressed (= try again)
               Output_obs_to_server_format_101_V2();
            }
            else // NO or CANCEL clicked by the user
            {
               IMMT_log();
               Reset_all_meteo_parameters();
            }   
         } // catch (InterruptedException | ExecutionException ex) 
      } // protected void done()
   }.execute(); // new SwingWorker<Void, Void>()

}


///////

/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static String http_respons_code_to_text(int responseCode)
{
   // called from:
   //     - RS232_Send_Sensor_Data_to_APR_format101_Server() [main_RS232_RS422.java]
   //     - RS232_Send_Sensor_Data_to_APR_FM13_Server [main_RS232_RS422.java]
   
   
   String text = "Unknown error";          
   switch (responseCode) 
   {
      // 1xx: Information
      case 100: text = "Continue (HTTP code 100)"; break;
      case 101: text = "Switching Protocols (HTTP code 101)"; break;
      case 103: text = "Checkpoint (HTTP code 103)"; break;
      
      // 2xx: Successful
      case 200: text = "OK"; break;
      case 201: text = "Created"; break;
      case 202: text = "Accepted"; break;
      case 203: text = "Non-Authoritative Information"; break;
      case 204: text = "No Content"; break;
      case 205: text = "Reset Content"; break;
      case 206: text = "Partial Content"; break;
      
      // 3xx: Redirection
      case 300: text = "Multiple Choices (HTTP code 300)"; break;
      case 301: text = "Moved Permanently (HTTP code 301)"; break;
      case 302: text = "Moved Temporarily (HTTP code 302)"; break;
      case 303: text = "See Other (HTTP code 303)"; break;
      case 304: text = "Not Modified (HTTP code 304)"; break;
      case 305: text = "Use Proxy (HTTP code 305)"; break;
      case 306: text = "Switch Proxy (HTTP code 306)"; break;
      case 307: text = "Temporary Redirect (HTTP code 307)"; break;
      case 308: text = "Resume Incomplete (HTTP code 308)"; break;   
      
      // 4xx: Client Error
      case 400: text = "Bad Request (HTTP code 400)"; break;
      case 401: text = "Unauthorized (HTTP code 401)"; break;
      case 402: text = "Payment Required (HTTP code 402)"; break;
      case 403: text = "Forbidden (HTTP code 403)"; break;
      case 404: text = "Not Found (upload URL unknown) (HTTP code 404)"; break;
      case 405: text = "Method Not Allowed (HTTP code 405)"; break;
      case 406: text = "Not Acceptable (HTTP code 406)"; break;
      case 407: text = "Proxy Authentication Required (HTTP code 407)"; break;
      case 408: text = "Request Time-out (HTTP code 408)"; break;
      case 409: text = "Conflict (HTTP code 409)"; break;
      case 410: text = "Gone (HTTP code 410)"; break;
      case 411: text = "Length Required (HTTP code 411)"; break;
      case 412: text = "Precondition Failed (HTTP code 412)"; break;
      case 413: text = "Request Entity Too Large (HTTP code 413)"; break;
      case 414: text = "Request-URI Too Large (HTTP code 414)"; break;
      case 415: text = "Unsupported Media Type (HTTP code 415)"; break;
      case 416: text = "Requested Range Not Satisfiable (HTTP code 416)"; break;
      case 417: text = "Expectation Failed (HTTP code 417)"; break;        
            
      // 5xx: Server Error   
      case 500: text = "Internal Server Error (HTTP code 500)"; break;                
      case 501: text = "Not Implemented (HTTP code 501)"; break;
      case 502: text = "Bad Gateway (HTTP code 502)"; break;
      case 503: text = "Service Unavailable (HTTP code 503)"; break;
      case 504: text = "Gateway Time-out (HTTP code 504)"; break;
      case 505: text = "HTTP Version not supported (HTTP code 505)"; break;
      case 511: text = "Authentication Required (HTTP code 511)"; break;
         
      // 7xx: TurboWin+/server Error (custom/sef defined errors) [2 sections] 
      //// start first self defined section (must be coordinated with server [index_webstart_101.php]) //////   
      case 700: text = "obs invalid format"; break;                                                                       // self defined
      case 701: text = "station ID or call sign in the obs not on the email whitelist of this server." + "<br>" +         // self defined
                       "Please send an email with your station ID and call sign to the addressee National Meteorological Service"; break;
      case 702: text = "obs routing from server to Meteorological Centre failed"; break;                                  // self defined
       //// end first self defined section ////// 
         
      //// start second self defined section (no coordination with server [index_webstart_101.php or index_webstart_fm13.php]) //////      
      case 710: text = "internal error when generating format 101 obs"; break;                                            // self defined 
      //case 711: text = "most probably no internet connection available or firewall/scanner is blocking"; break;           // self defined (actually IOexception)
      case 711: text = "communication error"; break;                                                                      // self defined (actually IOexception)
      case 712: text = "internal error, malformed URL"; break;                                                            // self defined
      case 713: text = "most probably no internet connection available (format 101 obs ok)"; break;                       // self defined
      case 714: text = "InterruptedException or ExecutionException"; break;                                               // self defined
      case 715: text = "Unsupported UTF-8 encoding"; break;                                                               // self defined 
      case 716: text = "most probably no internet connection available (FM13 obs ok)"; break;                             // self defined
      case 717: text = "internal error when generating FM13 obs"; break;                                                  // self defined   
      case 718: text = "internal error when determining response code"; break;                                                 // self defined 
      //// end second self defined section //////         
         
      // default   
      default:  text = "Unknown error"; break;                  
   } // switch   
       
   
   return text;     
}                  






/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
private void bepaal_last_record_uit_immt()
{
   // NB This function will be called from within a swingworker e.g. see Output_obs_by_email_actionPerformed()
   //    so not necessary to use a swingworker here (it is adviced to use a swingworker when file reading/writing)

   String record                   = "";

   /* initialisatie */
   last_record                     = "";


   /* first check if there is an immt log source file present (and not empty) */
   String volledig_path_immt = logs_dir + java.io.File.separator + IMMT_LOG;

   File immt_file = new File(volledig_path_immt);
   if (immt_file.exists() && immt_file.length() > 0)     // length() in bytes
   {
      try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt)))
      {
         while ((record = in.readLine()) != null)
         {
            last_record = record; 
         }
      }
      catch (IOException ex) 
      { 
         System.out.println("--- Function bepaal_last_record_uit_immt(): " + ex);
      }

   } // if (immt_file.exists() && immt_file.length() > 0)
}






/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void Reset_all_meteo_parameters()        
{
   System.out.println("--- " + "Resetting all meteo parameters and clearing all fields main screen");
   
   
   // global var's
   //
   
   // past weather
   mypastweather.W1_code                          = "";
   mypastweather. W2_code                         = "";
   mypastweather.past_weather_1                   = "";
   mypastweather.past_weather_2                   = "";
   
   // present weather
   mypresentweather.ww_code                       = "";
   mypresentweather.present_weather               = "";
   
   // visibility
   myvisibility.VV                               = "";
   myvisibility.VV_code                          = "";
   
   // barograph
   mybarograph.a_code                            = "";      
   mybarograph.ppp_code                          = "";
   mybarograph.pressure_amount_tendency          = "";
   
   // barometer
   mybarometer.pressure_reading                  = "";
   mybarometer.pressure_msl                      = "";
   mybarometer.PPPP_code                         = "";
   mybarometer.deepest_draft                     = "";
   //mybarometer.barometer_instrument_correction_new = "";
   mybarometer.pressure_reading_corrected        = "";
   mybarometer.pressure_msl_corrected            = "";
   
   // Cl
   mycl.cl_code                                  = "";
   
   // Cm
   mycm.cm_code                                  = "";
   
   // Ch
   mych.ch_code                                  = ""; 
   
   // cloud cover and height
   mycloudcover.N                                = "";
   mycloudcover.Nh                               = "";
   mycloudcover.h                                = "";
   mycloudcover.N_code                           = "";                             
   mycloudcover.Nh_code                          = "";                             
   mycloudcover.h_code                           = "";                             
   
   // Temperatures
   mytemp.air_temp                              = "";           
   mytemp.wet_bulb_temp                         = "";
   mytemp.RH                                    = "";
   mytemp.sea_water_temp                        = "";
   mytemp.sn_TTT_code                           = "";
   mytemp.TTT_code                              = "";     
   mytemp.sn_TbTbTb_code                        = "";           // Tb = Twet
   mytemp.TbTbTb_code                           = "";     
   mytemp.ss_TsTsTs_code                        = "";           // Ts = Tsea
   mytemp.TsTsTs_code                           = "";     
   mytemp.sn_TdTdTd_code                        = "";
   mytemp.TdTdTd_code                           = "";
   mytemp.immt_sst_indicator                    = "";
   mytemp.double_dew_point                      = INVALID;
   mytemp.wet_bulb_frozen                       = false;
   mytemp.double_rv                             = INVALID;
   
   // Wind
   mywind.wind_dir                              = "";
   mywind.wind_speed                            = "";
   mywind.ship_ground_course                    = "";    
   mywind.ship_ground_speed                     = "";
   mywind.ship_heading                          = "";
   mywind.dd_code                               = "";
   mywind.ff_code                               = "";
   mywind.fff00_code                            = "";
   mywind.iw_code                               = "";
   mywind.HDG_code                              = "";
   mywind.COG_code                              = "";
   mywind.SOG_code                              = "";
   mywind.SLL_code                              = "";
   mywind.sl_code                               = "";
   mywind.hh_code                               = "";
   mywind.RWD_code                              = "";
   mywind.RWS_code                              = "";
   mywind.int_true_wind_dir                     = INVALID;
   mywind.int_true_wind_speed                   = INVALID;
   mywind.int_relative_wind_speed               = INVALID;
   mywind.int_relative_wind_dir                 = INVALID;
   
   // Waves
   mywaves.wind_waves_period                    = "";   
   mywaves.wind_waves_height                    = "";
   mywaves.swell_1_dir                          = "";
   mywaves.swell_1_period                       = "";
   mywaves.swell_1_height                       = "";
   mywaves.swell_2_dir                          = "";
   mywaves.swell_2_period                       = "";
   mywaves.swell_2_height                       = "";
   mywaves.Hw_code                              = "";
   mywaves.Pw_code                              = "";
   mywaves.Dw1_code                             = "";
   mywaves.Pw1_code                             = "";
   mywaves.Hw1_code                             = "";
   mywaves.Dw2_code                             = "";
   mywaves.Pw2_code                             = "";
   mywaves.Hw2_code                             = "";
  
   // Captain
   for (int r = 0; r < mycaptain.CAPTAIN_ROWS; r++)
   {
     for (int c = 0; c < mycaptain.CAPTAIN_COLUMNS; c++)
     {
        mycaptain.captain_data[r][c]             = "";
     }
   }
   
   // Observer
   for (int r = 0; r < myobserver.OBSERVER_ROWS; r++)
   {
      for (int c = 0; c < myobserver.OBSERVER_COLUMNS; c++)
      {
         myobserver.observer_data[r][c]          = "";
      }
   }
   myobserver.selected_observer                  = "";
   
   // two swell systems
   //---
   
   // one swell system
   //---
   
   // confused swell
   //---
   
   // Icing
   myicing.Is_code                               = "";      
   myicing.EsEs_code                             = "";      
   myicing.Rs_code                               = "";       
   
   // Ice
   myice1.ci_code                                = "";       
   myice1.Si_code                                = "";            
   myice1.bi_code                                = "";            
   myice1.Di_code                                = "";            
   myice1.zi_code                                = "";            
   
   // position
   myposition.latitude_degrees                   = "";
   myposition.latitude_minutes                   = "";
   myposition.latitude_hemisphere                = "";
   myposition.longitude_degrees                  = "";
   myposition.longitude_minutes                  = "";
   myposition.longitude_hemisphere               = "";
   myposition.course                             = "";
   myposition.speed                              = "";
   myposition.lalala_code                        = "";         
   myposition.lolololo_code                      = "";         
   myposition.Qc_code                            = "";         
   myposition.Ds_code                            = "";
   myposition.vs_code                            = "";
   myposition.int_latitude_degrees               = INVALID;          // also used for position sequence check and cloud height advice computation
   myposition.int_latitude_minutes               = INVALID;          // also used for position sequence check
   myposition.int_longitude_degrees              = INVALID;          // also used for position sequence check and cloud height advice computation
   myposition.int_longitude_minutes              = INVALID;          // also used for position sequence check
   myposition.SOG_APR                            = Double.MAX_VALUE; // exclusively used for APR
   myposition.COG_APR                            = Double.MAX_VALUE; // exclusively used for APR
   myposition.SOG_APR_wind                       = Double.MAX_VALUE; // exclusively used for APR
   myposition.COG_APR_wind                       = Double.MAX_VALUE; // exclusively used for APR
   
   // date time
   mydatetime.year                               = "";                           
   mydatetime.month                              = "";
   mydatetime.day                                = "";
   mydatetime.hour                               = "";
   mydatetime.MM_code                            = "";                           // month of year 
   mydatetime.YY_code                            = "";                           // day of the month 
   mydatetime.GG_code                            = "";                           // hour of obs       
   
   
   // call sign
   //--
   
   
   // update of the fields on the main screen (and obs line on bottom main screen)
   //
   date_time_fields_update();
   visibility_fields_update();
   barometer_fields_update();
   barograph_fields_update();
   cloud_cover_fields_update();
   clouds_high_fields_update();
   clouds_low_fields_update();
   clouds_middle_fields_update();
   ice_fields_update();
   icing_fields_update();
   observer_field_update();
   past_weather_fields_update();
   position_fields_update();
   present_weather_fields_update();
   temperatures_fields_update();
   waves_fields_update();
   wind_fields_update();
   
   
   // APR Dashboard reset
   //
   if (main.dashboard_form_APR_radar != null)
   {
      DASHBOARD_view_APR_radar.reset_APR_wind_variables();
   }
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void delete_logs_turbowin_system()
{
   // NB    sdf_tsl_1 = new SimpleDateFormat("MMM_yyyy");                                // e.g. JAN_2016 (part of the file name)
   //       sdf_tsl_1.setTimeZone(TimeZone.getTimeZone("UTC"));
   //
   
   // delete the log files of 3 months and older 
   new SwingWorker<Void, Void>()
   {
      GregorianCalendar cal_delete_datum;
      
      @Override
      protected Void doInBackground() throws Exception
      {
         for (int i = 3; i <= 12; i++)   
         {
            cal_delete_datum = new GregorianCalendar();
            cal_delete_datum.add(Calendar.MONTH, -i);
            
            String file_naam = "turbowin_system_" + main.sdf_tsl_1.format(cal_delete_datum.getTime()) + ".txt";
            String volledig_path_turbowin_system_logs = main.logs_dir + java.io.File.separator + main.TURBOWIN_SYSTEM_LOGS_DIR + java.io.File.separator + file_naam;
            
            File file_log_data = new File(volledig_path_turbowin_system_logs);
            if (file_log_data.exists())
            {
               file_log_data.delete();
            }            
         } // for (int i = 3; i < 12; i++) 

         return null;
      } // protected Void doInBackground() throws Exception
   }.execute(); // new SwingWorker<Void, Void>()
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
public static void log_turbowin_system_message(final String message)
{
   // NB for logging path e.g. .../logs/turbowin_system_Jan_2016.txt
   //    message e.g. : [WOW] barometer height above MSL not available (Maintenance -> Station data)
   //
   //
   //    line on screen e.g. [WOW] barometer height above MSL not available (Maintenance -> Station data)
   //    line in file log e.g. 12-Jan-2016 13:17:33 [WOW] barometer height above MSL not available (Maintenance -> Station data)
   
   
   // to screen
   //
   System.out.println(message);
   
   // to file
   //
   new SwingWorker<Void, Void>()
   {
      @Override
      protected Void doInBackground() throws Exception
      {
         String file_naam = "turbowin_system_" + main.sdf_tsl_1.format(new Date()) + ".txt";     
         String volledig_path_turbowin_system_logs = main.logs_dir + java.io.File.separator + main.TURBOWIN_SYSTEM_LOGS_DIR + java.io.File.separator + file_naam;
  
         try (BufferedWriter out = new BufferedWriter(new FileWriter(volledig_path_turbowin_system_logs, true)))  // true means append the specified data to the file i.e. the pre-exist data in a file is not overwritten and the new data is appended after the pre-exist data. 
         {
            // NB try-with-resource; resources (is and os) will be closed automatically when execution leaves the try block.
      
            out.write(main.sdf_tsl_2.format(new Date()) + " UTC ");                       // new Date() -> always in UTC (because of sdf set in UTC, sdf_tsl_1.setTimeZone(TimeZone.getTimeZone("UTC"));)
            out.write(message);
            out.newLine();      
         }
         catch (IOException ex) 
         { 
            System.out.println("+++ " + ex + "; trying to create the logs folder"); 
      
            // if not done before/deleted, create sub dir TURBOWIN_SYSTEM_LOGS (turbowin_system)
            if ((logs_dir != null) && (logs_dir.compareTo("") != 0))   
            {
               File f = new File(logs_dir);
               if (f.exists() && f.isDirectory()) 
               {         
                  String turbowin_system_logs_dir = main.logs_dir + java.io.File.separator + main.TURBOWIN_SYSTEM_LOGS_DIR;
                  final File dir_turbowin_system_logs = new File(turbowin_system_logs_dir);  
      
                  if (dir_turbowin_system_logs.exists() == false)
                  {
                     dir_turbowin_system_logs.mkdir();  
                     //main.log_turbowin_system_message("[GENERAL] created dir " + turbowin_system_logs_dir);
                  }
               } // if (f.exists() && f.isDirectory())
            } //  if ((logs_dir != null) && (logs_dir.compareTo("") != 0))   
         } // catch
   
         return null;
      } // protected Void doInBackground() throws Exception
   }.execute(); // new SwingWorker<Void, Void>()

}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/ 
public static void satellite_link_mouse_clicked(String url_satellite_image)
{
   new SwingWorker<Integer, Void>()
   {
      @Override
      protected Integer doInBackground() throws Exception
      {
         String os = OSDetector.getOSString();
         
         String link_url = "";
         link_url = "https://realearth.ssec.wisc.edu/?products=globalir&time=latest&center=52,4&zoom=4";
         link_url = url_satellite_image;
            
         Integer code = 0;                              // ok
         Desktop desktop = null;

         if (os.equals("LINUX"))
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
            try 
            {
               // create cmd array
               String[] cmdArray = {"kde-open", link_url};

               // create a process and execute cmdArray
               Process process = Runtime.getRuntime().exec(cmdArray);
            } 
            catch (IOException e) 
            {
               try 
               {
                  // create cmd array
                  String[] cmdArray = {"xdg-open", link_url};

                  // create a process and execute cmdArray
                  Process process = Runtime.getRuntime().exec(cmdArray);
               } 
               catch (IOException e2) 
               {
                  try 
                  {
                     // create cmd array
                     String[] cmdArray = {"open", link_url};

                     // create a process and execute cmdArray
                     Process process = Runtime.getRuntime().exec(cmdArray);
                  } 
                  catch (IOException e3) 
                  {
                     code = -1;
                  } // catch (IOException e3)                 
               } // catch (IOException e2)                 
            } // catch  (IOException e) 
            if (code == -1)
            {
               if (Desktop.isDesktopSupported())
               {
                  desktop = Desktop.getDesktop();
                  URI uri = null;
                  try
                  {
                     String http_adres = link_url;  
                     uri = new URI(http_adres);
                     desktop.browse(uri);
                  }
                  catch (IOException | URISyntaxException ioe) 
                  { 
                     code = -2;
                  }
               } // if (Desktop.isDesktopSupported())
               else
               {
                  code = -1;
               } // else
            } // if (code == -1)  
         } // if (os.equals("LINUX"))
         else // Windows etc.
         {
            // Before more Desktop API is used, first check
            // whether the API is supported by this particular
            // virtual machine (VM) on this particular host.
            if (Desktop.isDesktopSupported())
            {
               desktop = Desktop.getDesktop();
               URI uri = null;
               try
               {
                  String http_adres = link_url;  
                  uri = new URI(http_adres);
                  desktop.browse(uri);
               }
               catch (IOException | URISyntaxException ioe) 
               { 
                  code = -2;
               }
            } // if (Desktop.isDesktopSupported())
            else
            {
               code = -1;
            } // else
         } // else (Windows etc.)
         
         
         return code;

      } // protected Integer doInBackground() throws Exception
      
      @Override
      protected void done()
      {
         try
         {
            Integer response_code = get();

            if (response_code == -1)
            {
               String message = "[GENERAL] Error invoking default web browser";
               JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               main.log_turbowin_system_message(message);
            }   
            else if (response_code == -2)
            {
               String message = "[GENERAL] Error invoking URL";
               JOptionPane.showMessageDialog(null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
               main.log_turbowin_system_message(message);
            }   
         } // try
         catch (InterruptedException | ExecutionException ex) 
         {   
            String message = "[GENERAL] Error invoking default web browser; " + ex.toString(); 
            main.log_turbowin_system_message(message);
         } // catch
      } // protected void done()      
   }.execute(); // new SwingWorker<Void, Void>()
}



/***********************************************************************************************/
/*                                                                                             */
/*                                                                                             */
/*                                                                                             */
/***********************************************************************************************/
   /**
   * @param args the command line arguments
   */
   public static void main(String args[])        
   {
      // displays each of its command-line arguments on a line by itself:
      for (int i = 0; i < args.length; i++) 
      {
        System.out.println("--- Argument " + i + ": " + args[i]);
        if (i == 0)
        {
           PORT_command_line = args[0];
        }
      } // for (int i = 0; i < args.length; i++) 
      
      
      java.awt.EventQueue.invokeLater(new Runnable() 
      {
         @Override
         public void run() 
         {
            // Set opacity of a decorated JFrame in Java >= 8
            // see: https://stackoverflow.com/questions/39538731/set-opacity-of-a-decorated-jframe-in-java-8/45740640
            
            //new main().setVisible(true);
            // nb below insteadof new main().setVisible(true); because now there is a reference to the main class (which is used in hybrid and wind radar dashboards)
            mainClass = new main(); 
            mainClass.setVisible(true);
         }
      });
    }
    
 
 
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton jButton10;
   private javax.swing.JButton jButton11;
   private javax.swing.JButton jButton12;
   private javax.swing.JButton jButton13;
   private javax.swing.JButton jButton14;
   private javax.swing.JButton jButton15;
   private javax.swing.JButton jButton16;
   private javax.swing.JButton jButton17;
   private javax.swing.JButton jButton18;
   private javax.swing.JButton jButton19;
   private javax.swing.JButton jButton2;
   private javax.swing.JButton jButton20;
   private javax.swing.JButton jButton3;
   private javax.swing.JButton jButton4;
   private javax.swing.JButton jButton5;
   private javax.swing.JButton jButton6;
   private javax.swing.JButton jButton7;
   private javax.swing.JButton jButton8;
   private javax.swing.JButton jButton9;
   public static javax.swing.JCheckBox jCheckBox1;
   public static javax.swing.JCheckBox jCheckBox2;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel10;
   private javax.swing.JLabel jLabel11;
   private javax.swing.JLabel jLabel12;
   private javax.swing.JLabel jLabel13;
   private javax.swing.JLabel jLabel14;
   private javax.swing.JLabel jLabel15;
   private javax.swing.JLabel jLabel16;
   private javax.swing.JLabel jLabel17;
   private javax.swing.JLabel jLabel18;
   private javax.swing.JLabel jLabel19;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel20;
   private javax.swing.JLabel jLabel21;
   private javax.swing.JLabel jLabel22;
   private javax.swing.JLabel jLabel23;
   private javax.swing.JLabel jLabel24;
   private javax.swing.JLabel jLabel25;
   private javax.swing.JLabel jLabel26;
   private javax.swing.JLabel jLabel27;
   private javax.swing.JLabel jLabel28;
   private javax.swing.JLabel jLabel29;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel30;
   private javax.swing.JLabel jLabel31;
   private javax.swing.JLabel jLabel32;
   private javax.swing.JLabel jLabel33;
   private javax.swing.JLabel jLabel34;
   private javax.swing.JLabel jLabel35;
   private javax.swing.JLabel jLabel36;
   private javax.swing.JLabel jLabel37;
   private javax.swing.JLabel jLabel38;
   public static javax.swing.JLabel jLabel39;
   public static javax.swing.JLabel jLabel4;
   private javax.swing.JLabel jLabel40;
   public static javax.swing.JLabel jLabel41;
   private javax.swing.JLabel jLabel5;
   public static javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JLabel jLabel9;
   private javax.swing.JMenu jMenu1;
   private javax.swing.JMenu jMenu10;
   private javax.swing.JMenu jMenu11;
   private javax.swing.JMenu jMenu12;
   private javax.swing.JMenu jMenu13;
   private javax.swing.JMenu jMenu14;
   private javax.swing.JMenu jMenu15;
   private javax.swing.JMenu jMenu2;
   private javax.swing.JMenu jMenu3;
   private javax.swing.JMenu jMenu4;
   private javax.swing.JMenu jMenu5;
   private javax.swing.JMenu jMenu6;
   private javax.swing.JMenu jMenu7;
   private javax.swing.JMenu jMenu8;
   private javax.swing.JMenu jMenu9;
   private javax.swing.JMenuBar jMenuBar1;
   private javax.swing.JMenuItem jMenuItem1;
   private javax.swing.JMenuItem jMenuItem10;
   private javax.swing.JMenuItem jMenuItem100;
   private javax.swing.JMenuItem jMenuItem101;
   private javax.swing.JMenuItem jMenuItem102;
   private javax.swing.JMenuItem jMenuItem103;
   private javax.swing.JMenuItem jMenuItem104;
   private javax.swing.JMenuItem jMenuItem105;
   private javax.swing.JMenuItem jMenuItem106;
   private javax.swing.JMenuItem jMenuItem107;
   private javax.swing.JMenuItem jMenuItem108;
   private javax.swing.JMenuItem jMenuItem109;
   private javax.swing.JMenuItem jMenuItem11;
   private javax.swing.JMenuItem jMenuItem110;
   private javax.swing.JMenuItem jMenuItem111;
   private javax.swing.JMenuItem jMenuItem112;
   private javax.swing.JMenuItem jMenuItem113;
   private javax.swing.JMenuItem jMenuItem114;
   private javax.swing.JMenuItem jMenuItem115;
   private javax.swing.JMenuItem jMenuItem116;
   private javax.swing.JMenuItem jMenuItem117;
   private javax.swing.JMenuItem jMenuItem118;
   private javax.swing.JMenuItem jMenuItem119;
   private javax.swing.JMenuItem jMenuItem12;
   private javax.swing.JMenuItem jMenuItem120;
   private javax.swing.JMenuItem jMenuItem121;
   private javax.swing.JMenuItem jMenuItem122;
   private javax.swing.JMenuItem jMenuItem123;
   private javax.swing.JMenuItem jMenuItem124;
   private javax.swing.JMenuItem jMenuItem125;
   private javax.swing.JMenuItem jMenuItem126;
   private javax.swing.JMenuItem jMenuItem127;
   private javax.swing.JMenuItem jMenuItem128;
   private javax.swing.JMenuItem jMenuItem129;
   private javax.swing.JMenuItem jMenuItem13;
   private javax.swing.JMenuItem jMenuItem130;
   private javax.swing.JMenuItem jMenuItem131;
   private javax.swing.JMenuItem jMenuItem132;
   private javax.swing.JMenuItem jMenuItem133;
   private javax.swing.JMenuItem jMenuItem134;
   private javax.swing.JMenuItem jMenuItem135;
   private javax.swing.JMenuItem jMenuItem136;
   private javax.swing.JMenuItem jMenuItem137;
   private javax.swing.JMenuItem jMenuItem138;
   private javax.swing.JMenuItem jMenuItem139;
   private javax.swing.JMenuItem jMenuItem14;
   private javax.swing.JMenuItem jMenuItem140;
   private javax.swing.JMenuItem jMenuItem15;
   private javax.swing.JMenuItem jMenuItem16;
   private javax.swing.JMenuItem jMenuItem17;
   private javax.swing.JMenuItem jMenuItem18;
   private javax.swing.JMenuItem jMenuItem19;
   private javax.swing.JMenuItem jMenuItem2;
   public static javax.swing.JMenuItem jMenuItem20;
   private javax.swing.JMenuItem jMenuItem21;
   private javax.swing.JMenuItem jMenuItem22;
   public static javax.swing.JMenuItem jMenuItem23;
   public static javax.swing.JMenuItem jMenuItem24;
   private javax.swing.JMenuItem jMenuItem25;
   private javax.swing.JMenuItem jMenuItem26;
   private javax.swing.JMenuItem jMenuItem27;
   private javax.swing.JMenuItem jMenuItem28;
   private javax.swing.JMenuItem jMenuItem29;
   private javax.swing.JMenuItem jMenuItem3;
   private javax.swing.JMenuItem jMenuItem30;
   private javax.swing.JMenuItem jMenuItem31;
   private javax.swing.JMenuItem jMenuItem32;
   private javax.swing.JMenuItem jMenuItem33;
   private javax.swing.JMenuItem jMenuItem34;
   private javax.swing.JMenuItem jMenuItem35;
   private javax.swing.JMenuItem jMenuItem36;
   private javax.swing.JMenuItem jMenuItem37;
   private javax.swing.JMenuItem jMenuItem38;
   private javax.swing.JMenuItem jMenuItem39;
   private javax.swing.JMenuItem jMenuItem4;
   private javax.swing.JMenuItem jMenuItem40;
   private javax.swing.JMenuItem jMenuItem41;
   private javax.swing.JMenuItem jMenuItem42;
   private javax.swing.JMenuItem jMenuItem43;
   private javax.swing.JMenuItem jMenuItem44;
   private javax.swing.JMenuItem jMenuItem45;
   public static javax.swing.JMenuItem jMenuItem46;
   private javax.swing.JMenuItem jMenuItem47;
   public static javax.swing.JMenuItem jMenuItem48;
   private javax.swing.JMenuItem jMenuItem49;
   private javax.swing.JMenuItem jMenuItem5;
   private javax.swing.JMenuItem jMenuItem50;
   private javax.swing.JMenuItem jMenuItem51;
   private javax.swing.JMenuItem jMenuItem52;
   private javax.swing.JMenuItem jMenuItem53;
   private javax.swing.JMenuItem jMenuItem54;
   private static javax.swing.JMenuItem jMenuItem55;
   private static javax.swing.JMenuItem jMenuItem56;
   private static javax.swing.JMenuItem jMenuItem57;
   private static javax.swing.JMenuItem jMenuItem58;
   private javax.swing.JMenuItem jMenuItem59;
   private javax.swing.JMenuItem jMenuItem6;
   private javax.swing.JMenuItem jMenuItem60;
   private javax.swing.JMenuItem jMenuItem61;
   private static javax.swing.JMenuItem jMenuItem62;
   private static javax.swing.JMenuItem jMenuItem63;
   private static javax.swing.JMenuItem jMenuItem64;
   private static javax.swing.JMenuItem jMenuItem65;
   private static javax.swing.JMenuItem jMenuItem66;
   private static javax.swing.JMenuItem jMenuItem67;
   private static javax.swing.JMenuItem jMenuItem68;
   private static javax.swing.JMenuItem jMenuItem69;
   private javax.swing.JMenuItem jMenuItem7;
   private static javax.swing.JMenuItem jMenuItem70;
   private javax.swing.JMenuItem jMenuItem71;
   private javax.swing.JMenuItem jMenuItem72;
   private javax.swing.JMenuItem jMenuItem73;
   private javax.swing.JMenuItem jMenuItem74;
   private javax.swing.JMenuItem jMenuItem75;
   private javax.swing.JMenuItem jMenuItem76;
   private static javax.swing.JMenuItem jMenuItem77;
   private javax.swing.JMenuItem jMenuItem78;
   private javax.swing.JMenuItem jMenuItem79;
   private javax.swing.JMenuItem jMenuItem8;
   private static javax.swing.JMenuItem jMenuItem80;
   private javax.swing.JMenuItem jMenuItem81;
   private javax.swing.JMenuItem jMenuItem82;
   private javax.swing.JMenuItem jMenuItem83;
   private javax.swing.JMenuItem jMenuItem84;
   private javax.swing.JMenuItem jMenuItem85;
   private javax.swing.JMenuItem jMenuItem86;
   private javax.swing.JMenuItem jMenuItem87;
   private javax.swing.JMenuItem jMenuItem88;
   private javax.swing.JMenuItem jMenuItem89;
   private javax.swing.JMenuItem jMenuItem9;
   private javax.swing.JMenuItem jMenuItem90;
   private javax.swing.JMenuItem jMenuItem91;
   private javax.swing.JMenuItem jMenuItem92;
   private javax.swing.JMenuItem jMenuItem93;
   private javax.swing.JMenuItem jMenuItem94;
   private javax.swing.JMenuItem jMenuItem95;
   private javax.swing.JMenuItem jMenuItem96;
   private javax.swing.JMenuItem jMenuItem97;
   private javax.swing.JMenuItem jMenuItem98;
   private javax.swing.JMenuItem jMenuItem99;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JPanel jPanel5;
   private javax.swing.JSeparator jSeparator1;
   private javax.swing.JSeparator jSeparator10;
   private javax.swing.JToolBar.Separator jSeparator11;
   private javax.swing.JSeparator jSeparator12;
   private javax.swing.JToolBar.Separator jSeparator13;
   private javax.swing.JPopupMenu.Separator jSeparator14;
   private javax.swing.JPopupMenu.Separator jSeparator15;
   private javax.swing.JPopupMenu.Separator jSeparator16;
   private javax.swing.JPopupMenu.Separator jSeparator17;
   private javax.swing.JSeparator jSeparator2;
   private javax.swing.JSeparator jSeparator3;
   private javax.swing.JSeparator jSeparator4;
   private javax.swing.JPopupMenu.Separator jSeparator5;
   private javax.swing.JPopupMenu.Separator jSeparator6;
   private javax.swing.JPopupMenu.Separator jSeparator7;
   private javax.swing.JPopupMenu.Separator jSeparator8;
   private javax.swing.JPopupMenu.Separator jSeparator9;
   private static javax.swing.JTextField jTextField1;
   private static javax.swing.JTextField jTextField10;
   private static javax.swing.JTextField jTextField11;
   private static javax.swing.JTextField jTextField12;
   private static javax.swing.JTextField jTextField13;
   private static javax.swing.JTextField jTextField14;
   private static javax.swing.JTextField jTextField15;
   public static javax.swing.JTextField jTextField16;
   public static javax.swing.JTextField jTextField17;
   private static javax.swing.JTextField jTextField18;
   private static javax.swing.JTextField jTextField19;
   private static javax.swing.JTextField jTextField2;
   private static javax.swing.JTextField jTextField20;
   private static javax.swing.JTextField jTextField21;
   private static javax.swing.JTextField jTextField22;
   private static javax.swing.JTextField jTextField23;
   private static javax.swing.JTextField jTextField24;
   private static javax.swing.JTextField jTextField25;
   private static javax.swing.JTextField jTextField26;
   private static javax.swing.JTextField jTextField27;
   private static javax.swing.JTextField jTextField28;
   private static javax.swing.JTextField jTextField29;
   private static javax.swing.JTextField jTextField3;
   private static javax.swing.JTextField jTextField30;
   private static javax.swing.JTextField jTextField31;
   private static javax.swing.JTextField jTextField32;
   private static javax.swing.JTextField jTextField33;
   private static javax.swing.JTextField jTextField34;
   private static javax.swing.JTextField jTextField35;
   public static javax.swing.JTextField jTextField36;
   public static javax.swing.JTextField jTextField37;
   public static javax.swing.JTextField jTextField38;
   public static javax.swing.JTextField jTextField4;
   public static javax.swing.JTextField jTextField40;
   public static javax.swing.JTextField jTextField5;
   private static javax.swing.JTextField jTextField7;
   private static javax.swing.JTextField jTextField9;
   private javax.swing.JToolBar jToolBar1;
   // End of variables declaration//GEN-END:variables

   
  
   // private constants
   public static final String OBSERVERS_STATS               = "observers stats";
   public static final String OBSERVATIONS_STATS            = "observations stats";
   public static final String SATELLITE_IR_IMAGE            = "satellite_ir_image";
   public static final String SATELLITE_VIS_IMAGE           = "satellite_vis_image";
   public static final String SATELLITE_SST_IMAGE           = "satellite_sst_image";
   public static final String SPATIE_OBS_SERVER             = "_";//"%20";// must be replaced by receiving php program with " "this is to avoid problems by browsers handling spaces
   public static final int MAX_AANTAL_JAREN_IN_IMMT         = 5;
   public static final int MAX_AANTAL_WAARNEMERS            = 20;          // zie ook OBSERVER_ROWS in myobserver.java
   public static final int IMMT_3_POSITION_OBSERVER         = 160;         // in IMMT-3 records begin position observer name
   public static final int IMMT_4_POSITION_OBSERVER         = 173;         // in IMMT-4 records begin position observer name
   public static final int IMMT_5_POSITION_OBSERVER         = 173;         // in IMMT-5 records begin position observer name
   public static final int IMMT_5_LENGTH                    = 172;         // minimum number char in IMMT 5 record (without the  possible addition of the observer name)
   public static final int IMMT_POSITION_IMMT_VERSION       = 110;         // immt version voor zowel IMMT-3/IMMT-4/IMMT-5 staat deze op dezelfde pos, zal in de toekomst ook wel zo blijven, maar wel controleren voor volgende IMMT versies
   private static final int IMMT_LIMIT                      = 1024000;     //512000;      // 512000 bytes / 1024 = 500 kB
   public static final String MOVE_TO_EMAIL                 = "move_to_email";
   public static final String MOVE_TO_DISK                  = "move_to_disk";
   public static final String LOGS_ZIP                      = "logs.zip";
   private final String JNLP_OFFLINE_FILE                   = "turbowin_jws_offline.jnlp"; // deze file zal aanwezig zijn als alleen in offline mode gewerkt wordt (wordt dan door KNMI er bij geleverd)
   private final String CMD_OFFLINE_FILE                    = "turbowin_plus_offline.cmd"; // deze file zal aanwezig zijn als alleen in offline mode gewerkt wordt (wordt dan door KNMI er bij geleverd)
   private final String TURBOWIN_LAUNCHER_FILE              = "turbowin_launcher.bat";
   private final String TURBOWIN_LAUNCHER_FILE_LINUX        = "turbowin_launcher";
   private final String EMAIL_SEND_DEFAULT                  = "email_send_default";
   private static final String EMAIL_SEND_LOCAL_HOST        = "email_send_local_host";
   private static final String EMAIL_SEND_GMAIL             = "email_send_gmail";
   private static final String EMAIL_SEND_YAHOO             = "email_send_yahoo";
   private static final String EMAIL_SEND_CUSTOM            = "email_send_custom";
   private final String PILOT_CHART_SA_JANUARY              = "pilot_chart_SA_January";
   private final String PILOT_CHART_SA_FEBRUARY             = "pilot_chart_SA_February";
   private final String PILOT_CHART_SA_MARCH                = "pilot_chart_SA_March";
   private final String PILOT_CHART_SA_APRIL                = "pilot_chart_SA_April";
   private final String PILOT_CHART_SA_MAY                  = "pilot_chart_SA_May"; 
   private final String PILOT_CHART_SA_JUNE                 = "pilot_chart_SA_June";
   private final String PILOT_CHART_SA_JULY                 = "pilot_chart_SA_July";
   private final String PILOT_CHART_SA_AUGUST               = "pilot_chart_SA_August";
   private final String PILOT_CHART_SA_SEPTEMBER            = "pilot_chart_SA_September";
   private final String PILOT_CHART_SA_OCTOBER              = "pilot_chart_SA_October";
   private final String PILOT_CHART_SA_NOVEMBER             = "pilot_chart_SA_November";
   private final String PILOT_CHART_SA_DECEMBER             = "pilot_chart_SA_December";
   private final String PILOT_CHART_NA_JANUARY              = "pilot_chart_NA_January";
   private final String PILOT_CHART_NA_FEBRUARY             = "pilot_chart_NA_February";
   private final String PILOT_CHART_NA_MARCH                = "pilot_chart_NA_March";
   private final String PILOT_CHART_NA_APRIL                = "pilot_chart_NA_April";
   private final String PILOT_CHART_NA_MAY                  = "pilot_chart_NA_May"; 
   private final String PILOT_CHART_NA_JUNE                 = "pilot_chart_NA_June";
   private final String PILOT_CHART_NA_JULY                 = "pilot_chart_NA_July";
   private final String PILOT_CHART_NA_AUGUST               = "pilot_chart_NA_August";
   private final String PILOT_CHART_NA_SEPTEMBER            = "pilot_chart_NA_September";
   private final String PILOT_CHART_NA_OCTOBER              = "pilot_chart_NA_October";
   private final String PILOT_CHART_NA_NOVEMBER             = "pilot_chart_NA_November";
   private final String PILOT_CHART_NA_DECEMBER             = "pilot_chart_NA_December";
   private final String PILOT_CHART_SP_JANUARY              = "pilot_chart_SP_January";
   private final String PILOT_CHART_SP_FEBRUARY             = "pilot_chart_SP_February";
   private final String PILOT_CHART_SP_MARCH                = "pilot_chart_SP_March";
   private final String PILOT_CHART_SP_APRIL                = "pilot_chart_SP_April";
   private final String PILOT_CHART_SP_MAY                  = "pilot_chart_SP_May"; 
   private final String PILOT_CHART_SP_JUNE                 = "pilot_chart_SP_June";
   private final String PILOT_CHART_SP_JULY                 = "pilot_chart_SP_July";
   private final String PILOT_CHART_SP_AUGUST               = "pilot_chart_SP_August";
   private final String PILOT_CHART_SP_SEPTEMBER            = "pilot_chart_SP_September";
   private final String PILOT_CHART_SP_OCTOBER              = "pilot_chart_SP_October";
   private final String PILOT_CHART_SP_NOVEMBER             = "pilot_chart_SP_November";
   private final String PILOT_CHART_SP_DECEMBER             = "pilot_chart_SP_December";
   private final String PILOT_CHART_NP_JANUARY              = "pilot_chart_NP_January";
   private final String PILOT_CHART_NP_FEBRUARY             = "pilot_chart_NP_February";
   private final String PILOT_CHART_NP_MARCH                = "pilot_chart_NP_March";
   private final String PILOT_CHART_NP_APRIL                = "pilot_chart_NP_April";
   private final String PILOT_CHART_NP_MAY                  = "pilot_chart_NP_May"; 
   private final String PILOT_CHART_NP_JUNE                 = "pilot_chart_NP_June";
   private final String PILOT_CHART_NP_JULY                 = "pilot_chart_NP_July";
   private final String PILOT_CHART_NP_AUGUST               = "pilot_chart_NP_August";
   private final String PILOT_CHART_NP_SEPTEMBER            = "pilot_chart_NP_September";
   private final String PILOT_CHART_NP_OCTOBER              = "pilot_chart_NP_October";
   private final String PILOT_CHART_NP_NOVEMBER             = "pilot_chart_NP_November";
   private final String PILOT_CHART_NP_DECEMBER             = "pilot_chart_NP_December";
   private final String PILOT_CHART_IN_JANUARY              = "pilot_chart_IN_January";
   private final String PILOT_CHART_IN_FEBRUARY             = "pilot_chart_IN_February";
   private final String PILOT_CHART_IN_MARCH                = "pilot_chart_IN_March";
   private final String PILOT_CHART_IN_APRIL                = "pilot_chart_IN_April";
   private final String PILOT_CHART_IN_MAY                  = "pilot_chart_IN_May"; 
   private final String PILOT_CHART_IN_JUNE                 = "pilot_chart_IN_June";
   private final String PILOT_CHART_IN_JULY                 = "pilot_chart_IN_July";
   private final String PILOT_CHART_IN_AUGUST               = "pilot_chart_IN_August";
   private final String PILOT_CHART_IN_SEPTEMBER            = "pilot_chart_IN_September";
   private final String PILOT_CHART_IN_OCTOBER              = "pilot_chart_IN_October";
   private final String PILOT_CHART_IN_NOVEMBER             = "pilot_chart_IN_November";
   private final String PILOT_CHART_IN_DECEMBER             = "pilot_chart_IN_December";
   
   
   // public constants
   public static final String SPATIE_OBS_VIEW               = " ";
   public static final String UNDEFINED                     = "undefined";
   public static final String APTR_AWSR_SERVER              = "APTR_AWSR_server";          // WOW_APR_settings.java
   public static final String APTR_AWSR_SMTP_HOST           = "APTR_AWSR_SMTP_host";       // WOW_APR_settings.java
   public static final String APTR_AWSR_GMAIL               = "APTR_AWSR_Gmail";           // WOW_APR_settings.java
   public static final String APTR_AWSR_YAHOO_MAIL          = "APTR_AWSR_Yahoo_Mail";      // WOW_APR_settings.java
   public static final String APTR_AWSR_CUSTOM_MAIL         = "APTR_AWSR_Custom_Mail";     // WOW_APR_settings.java 
   public static final String SMTP_HOST_SHIP                = "SMTP_HOST_SHIP";
   public static final String GMAIL_TLS                     = "GMAIL_TLS";
   public static final String GMAIL_SSL                     = "GMAIL_SSL";
   public static final String YAHOO_TLS                     = "YAHOO_TLS";
   public static final String YAHOO_SSL                     = "YAHOO_SSL";
   public static final String CUSTOM_TLS                    = "CUSTOM_TLS";
   public static final String CUSTOM_TLS_STARTTLS           = "CUSTOM_TLS_STARTTLS";
   public static final String CUSTOM_SSL                    = "CUSTOM_SSL";
   public static final String CUSTOM_SSL_STARTTLS           = "CUSTOM_SSL_STARTTLS";
   public static final String LEAFLET_CSS_URL               = "  <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.3.1/dist/leaflet.css\"";
   public static final String LEAFLET_JS_URL                = "  <script src=\"https://unpkg.com/leaflet@1.3.1/dist/leaflet.js\"";
   public static final String LEAFLET_ESRI_URL              = "  <script src=\"https://unpkg.com/esri-leaflet@2.1.4/dist/esri-leaflet.js\"";
   public static final String LEAFLET_CSS_INTEGRITY         = "  integrity=\"sha512-Rksm5RenBEKSKFjgI3a41vrjkw4EVPlJ3+OiI65vTjIdo9brlAacEuKOiQ5OFh7cOI1bkDwLqdLw3Zg0cRJAAQ==\"";
   public static final String LEAFLET_JS_INTEGRITY          = "  integrity=\"sha512-/Nsx9X4HebavoBvEBuyp3I7od5tA0UzAxs+j83KgC8PU0kgB4XiK4Lfe4y4cgBtaRJQEIFCW+oC506aPT2L1zw==\"";
   public static final String LEAFLET_ESRI_INTEGRITY        = "  integrity=\"sha512-m+BZ3OSlzGdYLqUBZt3u6eA0sH+Txdmq7cqA1u8/B2aTXviGMMLOfrKyiIW7181jbzZAY0u+3jWoiL61iLcTKQ==\"";
   public static final String LEAFLET_MAPS_HTML_FILE        = "position_leaflet_maps.html";     // leaflet maps file for displaying just entered position 
   public static final double KNOT_M_S_CONVERSION           = 0.51444444444;
   public static final double M_S_KNOT_CONVERSION           = 1.94384449;
   public static final String OFFLINE_LOGS_DIR              = "logs";           // only used inoffline_mode
   public static final String OFFLINE_AMVER_DIR             = "amver";          // only used in offline_mode
   public static final String TURBOWIN_SYSTEM_LOGS_DIR      = "turbowin_system";// online(web) and offline mode
   public static final int INVALID                          = 9999999;
   public static final int CONFIGURATION_FILE_POS_INHOUD    = 21;               // eg "van wind source        : estimated; true speed and true direction"de pos waar estimated begint
   public static final int MAX_AANTAL_CONFIGURATIEREGELS    = 100;               // in configuratie file (for wind source e.d.)
   public static final String ICONS_DIRECTORY               = "icons/";
   //public static final String ICONS_DIRECTORY_R             = "icons";        // _R van revised "icons" i.p.v. "icons/" WERKT HELAAS NIET BIJ TOOLBAR ICONS, REDEN ONBEKEND
   public static final String CONFIGURATION_FILE            = "configuration.txt";
   public static final String OBSERVER_LOG                  = "observer.log";
   public static final String CAPTAIN_LOG                   = "captain.log";
   public static final String IMMT_LOG                      = "immt.log";
   
   public static final String SHIP_NAME_TXT                 = "ship name          : ";   // t/m : is 20 characters
   public static final String IMO_NUMBER_TXT                = "imo number         : ";   // t/m : is 20 characters
   public static final String CALL_SIGN_TXT                 = "call sign          : ";   // t/m : is 20 characters
   public static final String MASKED_CALL_SIGN_TXT          = "masked call sign   : ";   // t/m : is 20 characters
   public static final String TIME_ZONE_COMPUTER_TXT        = "time zone computer : ";   // t/m : is 20 characters
   public static final String RECRUITING_COUNTRY_TXT        = "recruiting country : ";   // t/m : is 20 characters
   public static final String METHOD_WAVES_TXT              = "method waves       : ";   // t/m : is 20 characters
   public static final String WIND_SOURCE_TXT               = "wind source        : ";   // t/m : is 20 characters
   public static final String BAROMETER_ABOVE_SLL_TXT       = "barometer above sll: ";   // t/m : is 20 characters
   public static final String BAROMETER_KEEL_TO_SLL_TXT     = "barometer keel-sll : ";   // t/m : is 20 characters
   public static final String PRESSURE_READING_MSL_TXT      = "pressure read msl  : ";   // t/m : is 20 characters
   public static final String AIR_TEMP_EXPOSURE_TXT         = "air temp exposure  : ";   // t/m : is 20 characters
   public static final String SST_EXPOSURE_TXT              = "sst exposure       : ";   // t/m : is 20 characters
   public static final String MAX_HEIGHT_DECK_CARGO_TXT     = "max. height cargo  : ";   // t/m : is 20 characters
   public static final String DIFF_SLL_WL_TXT               = "diff. sll-wl       : ";   // t/m : is 20 characters
   public static final String OBS_EMAIL_RECIPIENT_TXT       = "obs email recipient: ";   // t/m : is 20 characters
   public static final String OBS_EMAIL_SUBJECT_TXT         = "obs email subject  : ";   // t/m : is 20 characters
   public static final String LOGS_DIR_TXT                  = "logs folder        : ";   // t/m : is 20 characters
   public static final String LOGS_EMAIL_RECIPIENT_TXT      = "log email recipient: ";   // t/m : is 20 characters
   public static final String WIND_UNITS_TXT                = "wind units         : ";   // t/m : is 20 characters
   public static final String RS232_INSTRUMENT_TYPE_TXT     = "RS232 instrument   : ";   // t/m : is 20 characters
   public static final String RS232_BITS_PER_SEC_TXT        = "RS232 bps          : ";   // t/m : is 20 characters
   public static final String RS232_DATA_BITS_TXT           = "RS232 data bits    : ";   // t/m : is 20 characters
   public static final String RS232_PARITY_TXT              = "RS232 parity       : ";   // t/m : is 20 characters
   public static final String RS232_STOP_BITS_TXT           = "RS232 stop bits    : ";   // t/m : is 20 characters
   public static final String RS232_PREFERED_COM_PORT_TXT   = "RS232 prefered COM : ";   // t/m : is 20 characters (Windows and Linux)
   public static final String RS232_INSTRUMENT_TYPE_TXT_II  = "RS232 instrument_II: ";   // t/m : is 20 characters
   public static final String RS232_BITS_PER_SEC_TXT_II     = "RS232 bps_II       : ";   // t/m : is 20 characters
   public static final String RS232_DATA_BITS_TXT_II        = "RS232 data bits_II : ";   // t/m : is 20 characters
   public static final String RS232_PARITY_TXT_II           = "RS232 parity_II    : ";   // t/m : is 20 characters
   public static final String RS232_STOP_BITS_TXT_II        = "RS232 stop bits_II : ";   // t/m : is 20 characters
   public static final String RS232_PREFERED_COM_PORT_TXT_II= "RS232 pref COM_II  : ";   // t/m : is 20 characters (Windows and Linux)
   public static final String IC_BAROMETER_TXT              = "ic barometer       : ";   // t/m : is 20 characters
   public static final String OBS_FORMAT_TXT                = "obs format         : ";   // t/m : is 20 characters
   public static final String FORMAT_101_ENCRYPTION_TXT     = "format 101 encrypt : ";   // t/m : is 20 characters
   public static final String FORMAT_101_EMAIL_TXT          = "format 101 email   : ";   // t/m : is 20 characters
   public static final String RS232_PREF_COM_PORT_NAME_TXT  = "RS232 pref COM name: ";   // t/m : is 20 characters (OS X)
   public static final String WOW_PUBLISH_TXT               = "WOW publish        : ";   // t/m : is 20 characters 
   public static final String WOW_SITE_ID_TXT               = "WOW site ID        : ";   // t/m : is 20 characters 
   public static final String WOW_PIN_TXT                   = "WOW pin            : ";   // t/m : is 20 characters 
   public static final String WOW_REPORTING_INTERVAL_TXT    = "WOW rep. interval  : ";   // t/m : is 20 characters 
   //public static final String WOW_AVERAGE_BARO_HEIGHT_TXT   = "WOW barom. height  : ";   // t/m : is 20 characters 
   public static final String WOW_APR_AVERAGE_DRAUGHT_TXT   = "WOW/APR draught    : ";   // t/m : is 20 characters (average draught last years)
   public static final String AMOS_MAIL_TXT                 = "AMOS Mail          : ";   // t/m : is 20 characters
   public static final String RS232_GPS_TYPE_TXT            = "RS232 GPS type     : ";   // t/m : is 20 characters
   public static final String RS232_GPS_BITS_PER_SEC_TXT    = "RS232 GPS bps      : ";   // t/m : is 20 characters
   public static final String RS232_GPS_COM_PORT_TXT        = "RS232 GPS COM      : ";   // t/m : is 20 characters (Windows and Linux)
   public static final String RS232_GPS_COM_PORT_NAME_TXT   = "RS232 GPS COM name : ";   // t/m : is 20 characters (OS X)
   public static final String RS232_GPS_SENTENCE_TXT        = "RS232 GPS sentence : ";   // t/m : is 20 characters (RMC or GGA)
   public static final String APR_TXT                       = "APR                : ";   // t/m : is 20 characters
   public static final String APR_REPORTING_INTERVAL_TXT    = "APR rep. interval  : ";   // t/m : is 20 characters
   public static final String UPLOAD_URL_TXT                = "upload URL         : ";   // t/m : is 20 characters
   public static final String AWSR_TXT                      = "AWSR               : ";   // t/m : is 20 characters
   public static final String AWSR_REPORTING_INTERVAL_TXT   = "AWSR rep. int.     : ";   // t/m : is 20 characters
   public static final String WIND_UNITS_DASHBOARD_TXT      = "wind units dashbrd : ";   // t/m : is 20 characters
   public static final String SHIP_TYPE_DASHBOARD_TXT       = "ship type dashbrd  : ";   // t/m : is 20 characters
   public static final String HEIGHT_ANEMOMETER_TXT         = "anemometer-WL      : ";   // t/m : is 20 characters
   public static final String GUI_MODE_TXT                  = "GUI mode           : ";   // t/m : is 20 characters
   public static final String GUI_LOGO_TXT                  = "GUI logo           : ";   // t/m : is 20 characters
   public static final String OBS_EMAIL_CC_TXT              = "obs email cc       : ";   // t/m : is 20 characters
   public static final String LOCAL_EMAIL_SERVER_TXT        = "email local host   : ";   // t/m : is 20 characters
   public static final String YOUR_GMAIL_ADDRESS_TXT        = "your Gmail address : ";   // t/m : is 20 characters
   public static final String GMAIL_APP_PASSWORD_TXT        = "Gmail app password : ";   // t/m : is 20 characters
   public static final String GMAIL_SECURITY_TXT            = "Gmail security     : ";   // t/m : is 20 characters
   public static final String YOUR_YAHOO_ADDRESS_TXT        = "your Yahoo address : ";   // t/m : is 20 characters
   public static final String YAHOO_APP_PASSWORD_TXT        = "Yahoo app password : ";   // t/m : is 20 characters
   public static final String YAHOO_SECURITY_TXT            = "Yahoo security     : ";   // t/m : is 20 characters
   public static final String YOUR_SHIP_ADDRESS_TXT         = "your ship address  : ";   // t/m : is 20 characters  
   public static final String SMTP_HOST_PASSWORD_TXT        = "smtp host password : ";   // t/m : is 20 characters
   public static final String SMTP_HOST_PORT_TXT            = "smtp host port     : ";   // t/m : is 20 characters 
   public static final String APTR_AWSR_SEND_METHOD_TXT     = "APTR send method   : ";   // t/m : is 20 characters    // also for AWSR
   public static final String STATION_ID_TXT                = "station ID         : ";   // t/m : is 20 characters
   public static final String DASHBOARD_BACKGROUND_IMAGE_TXT= "dashboard image    : ";   // t/m : is 20 characters
   public static final String YOUR_CUSTOM_ADDRESS_TXT       = "your custom address: ";   // t/m : is 20 characters
   public static final String CUSTOM_EMAIL_SERVER_TXT       = "custom email server: ";   // t/m : is 20 characters
   public static final String CUSTOM_PASSWORD_TXT           = "custom email passw.: ";   // t/m : is 20 characters
   public static final String CUSTOM_SECURITY_TXT           = "custom security    : ";   // t/m : is 20 characters 
   public static final String CUSTOM_PORT_TXT               = "custom port        : ";   // t/m : is 20 characters
   public static final String POP_UP_DASHBOARD_TXT          = "pop-up dashboard   : ";   // t/m : is 20 characters
   public static final String POP_UP_DASHBOARD_INTERVAL_TXT = "pop-up dashb int.  : ";   // t/m : is 20 characters
   public static final String DASHBOARD_SHIP_DECK_COLOR_TXT = "dashbrd deck color : ";   // t/m : is 20 characters
   public static final String CUSTOM_EMAIL_MODULE_TXT       = "custom email module: ";   // t/m : is 20 characters
   public static final String LOGS_EMAIL_TXT                = "logs email         : ";   // t/m : is 20 characters
   public static final String EUCAWS_UPLOADS_METHOD_TXT     = "eucaws uploads     : ";   // t/m : is 20 characters
   public static final String PORT_MODE_OPTION_TXT          = "port mode option   : ";   // t/m : is 20 characters
   public static final String LAN_IP_ADDRESS_TXT            = "LAN IP address     : ";   // t/m : is 20 characters
   public static final String DASHBOARD_SHIP_TANK_COLOR_TXT = "dashbrd tank color : ";   // t/m : is 20 characters
   public static final String DASHBOARD_FONT_TXT            = "dashbrd font       : ";   // t/m : is 20 characters
   public static final String COM_PROTOCOL_TXT              = "com protocol       : ";   // t/m : is 20 characters
   
   public static final String YACHT                         = "yacht";
   public static final String FULL_RIGGED_3                 = "full_rigged_3";
   public static final String FULL_RIGGED_4                 = "full_rigged_4";
   public static final String FULL_RIGGED_5                 = "full_rigged_5";
   public static final String BARQUE_3                      = "barque_3";
   public static final String BARQUE_4                      = "barque_4";
   public static final String BARQUE_5                      = "barque_5";
   public static final String FRUIT_JUICE_TANKER            = "fruit juice tanker";
   public static final String LNG_TANKER_II                 = "LNG_tanker_II";
   public static final String REEFER_SHIP                   = "reefer_ship";
   public static final String CONTAINER_SHIP                = "container_ship";
   public static final String CONTAINER_SHIP_2              = "container_ship_II";
   public static final String BULK_CARRIER                  = "bulk_carrier";
   public static final String OIL_TANKER                    = "oil_tanker";
   public static final String LNG_TANKER                    = "LNG_tanker";
   public static final String PASSENGER_SHIP                = "passenger_ship";
   public static final String NEUTRAL_SHIP                  = "neutral_ship";
   public static final String GENERAL_CARGO_SHIP            = "general_cargo_ship";
   public static final String GENERAL_CARGO_SHIP_2          = "general_cargo_ship_II";
   public static final String GENERAL_CARGO_CLASSIC         = "general_cargo_classic";
   public static final String RESEARCH_VESSEL               = "research_vessel";
   public static final String RO_RO_SHIP                    = "Ro-Ro_ship";
   public static final String FERRY                         = "ferry";
   public static final String ESTIMATED_TRUE                = "estimated; true speed and true direction";
   public static final String MEASURED_OFF_BOW              = "measured; apparent speed and apparent direction (OFF THE BOW, clockwise)";
   public static final String MEASURED_TRUE                 = "measured; true speed and true direction";
   public static final String SLING_PSYCHROMETER            = "sling psychrometer";
   public static final String MARINE_SCREEN                 = "marine screen";
   public static final String INTAKE                        = "intake";
   public static final String BUCKET                        = "bucket" ;
   public static final String HULL_CONTACT_SENSOR           = "hull contact sensor";
   public static final String TRAILING_THERMISTOR           = "trailing thermistor";
   public static final String THROUGH_HULL_SENSOR           = "through hull sensor";
   public static final String RADIATION_THERMOMETER         = "radiation thermometer";
   public static final String BAIT_TANKS_THERMOMETER        = "bait tanks thermometer";
   public static final String OTHER                         = "other";
   public static final String TIME_ZONE_COMPUTER_UTC        = "UTC/GMT";
   public static final String TIME_ZONE_COMPUTER_OTHER      = "other";
   public static final String PRESSURE_READING_MSL_YES      = "yes";
   public static final String PRESSURE_READING_MSL_NO       = "no";
   public static final String STATION_DATA                  = "station data";            // see 'mode' in password form
   public static final String EMAIL_SETTINGS                = "email settings";          // see 'mode' in password form
   public static final String LOG_FILES                     = "log files";               // see 'mode' in password form
   public static final String SET_OBS_FORMAT                = "set obs format";          // see 'mode' in password form
   public static final String SET_WOW_APR_SETTINGS          = "set WOW APR settings";    // see 'mode' in password form
   public static final String SET_SERVER_SETTINGS           = "set server settings";     // see 'mode' in password form
   public static final String MAINTENANCE_SHOW_DATA         = "maintenance show  data";  // see 'mode' in password form
   public static final String MAINTENANCE_IMPORT_DATA       = "maintenance import data"; // see 'mode' in password form
   public static final String MAINTENANCE_EXPORT_DATA       = "maintenance export data"; // see 'mode' in password form
   //public static final String GUI_SETTINGS                  = "GUI settings";            // see 'mode' in password form
   public static final String SEA_AND_SWELL_ESTIMATED       = "wind sea and swell estimated";
   public static final String WAVES_MEASURED_SHIPBORNE      = "waves measured (shipborne wave recorder)";
   public static final String WAVES_MEASURED_BUOY           = "waves measured (buoy)";
   public static final String WAVES_MEASURED_OTHER          = "waves measured (other measurement system)";
   public static final String MUFFIN_LINE_SEPARATOR         = "%";                    // i.p.v. eol deze geeft problemen bij bytes -> String
   public static final String UK_OBS_EMAIL_SUBJECT          = "SXVX88 EGRR ddhhmm";
   //public static final String GENERAL_OBS_EMAIL_SUBJECT     = "weather observation";
   //public static final String URL_TURBOWIN                  = "https://projects.knmi.nl/turbowin/";
   public static final String URL_INTERNET_HELP             = "https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/raw/master/help_files/";   //"https://projects.knmi.nl/turbowin/webstart101/help/"; 
   public static final String OFFLINE_HELP_DIR              = "help";                 // wordt alleeen gebruikt indien in offline_mode
   public static final String KNOTS                         = "knots";
   public static final String M_S                           = "m/s";
   public static final String AMVER_SP                      = "amver_sp";             // AMVER sailing plan
   public static final String AMVER_DR                      = "amver_dr";             // AMVER deviation report
   public static final String AMVER_FR                      = "amver_fr";             // AMVER arrival report
   public static final String AMVER_PR                      = "amver_pr";             // AMVER position report
   public static final String FORMAT_FM13                   = "format_fm13";          // obs format
   public static final String FORMAT_AWS                    = "format_aws";           // obs format (if AWS connected)
   public static final String FORMAT_101                    = "format_101";           // obs format
   public static final String FORMAT_101_ENCRYPTION_YES     = "101_encrypt_yes";      // related to obs format
   public static final String FORMAT_101_ENCRYPTION_NO      = "101_encrypt_no";       // related to obs format
   public static final String FORMAT_101_BODY               = "101_email_body";       // related to obs format
   public static final String FORMAT_101_ATTACHEMENT        = "101_email_attachement";// related to obs format
   public static final String FORMAT_101_ROOT_DIR           = "format_101";           // directory for format 101
   public static final String FORMAT_101_TEMP_DIR           = "temp";                 // directory for format 101
   public static final String FORMAT_101_INPUT_FILE         = "format_101.txt";       // file for format 101 (NB outputfile is automatically "HPK_" + input file)
   public static final String THEME_NIMBUS_DAY              = "theme_nimbus_day";
   public static final String THEME_NIMBUS_NIGHT            = "theme_nimbus_night";
   public static final String THEME_NIMBUS_SUNRISE          = "theme_nimbus_sunrise";
   public static final String THEME_NIMBUS_SUNSET           = "theme_nimbus_sunset";
   public static final String THEME_TRANSPARENT             = "theme_transparent";
   public static final String OSM_OFFLINE_MANUAL            = "OSM_offline_manual";   // conventional VOS (APR included)
   public static final String OSM_ONLINE_MANUAL             = "OSM_online_manual";    // conventional VOS (APR included)
   public static final String OSM_ONLINE_AWS_SENSOR         = "OSM_online_AWS_sensor";
   public static final String OSM_OFFLINE_AWS_SENSOR        = "OSM_offline_AWS_sensor";
   public static final String OSM_ONLINE_AWS_VISUAL         = "OSM_online_AWS_visual";
   public static final String OSM_OFFLINE_AWS_VISUAL        = "OSM_offline_AWS_visual";
   //public static final String GUI_LIGHT                     = "GUI light";
   //public static final String GUI_FULL                      = "GUI full";
   //public static final String LOGO_EUMETNET                 = "logo EUMETNET"; 
   //public static final String LOGO_NOAA                     = "logo NOAA"; 
   //public static final String LOGO_SOT                      = "logo SOT"; 
   //public static final Integer APR_SCREEN_POP_UP_MINUTES_TO_HOUR = 10;
   public static final Integer APR_AWS_SCREEN_POP_UP_MINUTES_TO_HOUR = 10;
   public static final String PYTHON_EMAIL                  = "python_email";
   public static final String JAKARTA_EMAIL                 = "jakarta_email";
   public static final String LOGS_DEFAULT_EMAIL            = "logs_default_email";
   public static final String LOGS_CUSTOM_EMAIL             = "logs_custom_email";
   public static final String UPLOADS_VIA_EUCAWS            = "uploads_via_eucaws";         // obs format setting (if format is EUCAWS-AWS)
   public static final String UPLOADS_VIA_TURBOWIN          = "uploads_via_turbowin";       // obs format setting  (if format is EUCAWS-AWS) 
   public static final String HTTPS_PROTOCOL                = "HTTPS_protocol";
   public static final String HTTP_PROTOCOL                 = "HTTP_protocol";
   
   //public static final String KNMI_UPLOAD_URL               = "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?"; // "www.turbowin.knmi.nl/webstart101/index_webstart_101.php?";
   public static final Integer INVALID_RESPONSE_FORMAT_101         = 710;                    // self defined http response code
   public static final Integer RESPONSE_NO_INTERNET                = 711;                    // self defined http response code (IOException)
   public static final Integer RESPONSE_MALFORMED_URL              = 712;                    // self defined http response code
   public static final Integer OK_RESPONSE_FORMAT_101              = 713;                    // self defined http response code 
   public static final Integer RESPONSE_INTERRUPTION               = 714;                    // self defined http response code (InterruptedException | ExecutionException)
   public static final Integer RESPONSE_UNSUPPORTED_ENCODING       = 715;                    // self defined http response code 
   public static final Integer OK_RESPONSE_FORMAT_FM13             = 716;                    // self defined http response code
   public static final Integer INVALID_RESPONSE_FORMAT_FM13        = 717;                    // self defined http response code
   public static final Integer INTERNAL_ERROR_RESPONSE_CODE        = 718;                    // self defined http response code
   
   // public var's
   public static final String APPLICATION_NAME                     = "TurboWin+";            // NB DO NOT FORGET TO BUILD ALL AFTER A CHANGE OF THIS STRING
   public static final String APPLICATION_VERSION                  = "4.7.0 [64-bit] (build 3-April-2025)"; // NB DO NOT FORGET TO COMPILE MAIN.JAVA AND ABOUT.JAVA AFTER A CHANGE OF THIS STRING // NB wordt getest op substring "[64-bit]"
   public static final String APPLICATION_MET_MODULES              = "MAWSbin_TW; teste_hc_TW; email_tbw_43;";
   public static final String DASHBOARD_LOGO                       = "logo-sot.png";         // i.a. single dashboard barometer
   public static String application_mode                           = "";                     // e.g. web mode (set in initComponents2 [main.java] and [main_RS232_RS422.java]
   public static String amver_report                               = "";                     // AMVER
   public static String data_dir;
   public static String[] configuratie_regels                      = new String[MAX_AANTAL_CONFIGURATIEREGELS];// default values: null
   public static String ship_name                                  = "";                     // meta data (mystationdata.java)
   public static String imo_number                                 = "";                     // meta data
   //public static String call_sign                           = "";                     // meta data
   //public static String masked_call_sign                    = "";                     // meta data
   public static String station_ID                                 = "";                     // meta data
   public static String time_zone_computer                         = "";                     // meta data
   public static String recruiting_country                         = "";                     // meta data
   public static String method_waves                               = "";                     // meta data
   public static String wind_source                                = "";                     // meta data
   public static String barometer_above_sll                        = "";                     // meta data
   public static String keel_sll                                   = "";                     // meta data
   public static String air_temp_exposure                          = "";                     // meta data
   public static String sst_exposure                               = "";                     // meta data
   public static String max_height_deck_cargo                      = "";                     // meta data
   public static String diff_sll_wl                                = "";                     // meta data
   public static String pressure_reading_msl_yes_no                = "";                     // meta data
   public static String height_anemometer                          = "";                     // meta data
   public static String logs_dir                                   = "";                     // meta data (in this folder e.g. immt.log)
   public static String coded_obs_total                            = "";
   public static String obs_email_recipient                        = "";                     // meta data (myemailsettings.java)
   public static String obs_email_subject                          = "";                     // meta data (myemailsettings.java)
   public static String logs_email_recipient                       = "";                     // meta data (myemailsettings.java)
   public static String obs_email_cc                               = "";                     // meta data (myemailsettings.java)
   public static String local_email_server                         = "";                     // meta data (myemailsettings.java)
   public static String your_gmail_address                         = "";                     // meta data (myemailsettings.java)
   public static String gmail_app_password                         = "";                     // meta data (myemailsettings.java) // NB = encrypted!!
   public static String gmail_security                             = "";                     // meta data (myemailsettings.java)
   public static String your_yahoo_address                         = "";                     // meta data (myemailsettings.java) 
   public static String yahoo_app_password                         = "";                     // meta data (myemailsettings.java) // NB = encrypted!!
   public static String yahoo_security                             = "";                     // meta data (myemailsettings.java)
   public static String your_ship_address                          = "";                     // meta data (myemailsettings.java)
   public static String smtp_host_password                         = "";                     // meta data (myemailsettings.java) // NB = encrypted!!
   public static String smtp_host_port                             = "";                     // meta data (myemailsettings.java)
   public static String your_custom_address                        = "";                     // meta data (myemailsettings.java)
   public static String custom_email_server                        = "";                     // meta data (myemailsettings.java)     
   public static String custom_port                                = "";                     // meta data (myemailsettings.java)                  
   public static String custom_password                            = "";                     // meta data (myemailsettings.java) // NB = encrypted!!
   public static String custom_security                            = "";                     // meta data (myemailsettings.java)
   public static String custom_email_module                        = "";                     // meta data (myemailsettings.java) [PYTHON_EMAIL or JAKARTA_EMAIL]
   public static String log_files_email_send_method                = "";                     // meta data (myemailsettings.java) [LOGS_DEFAULT_EMAIL or LOGS_CUSTOM_EMAIL]
   public static String server_com_protocol                        = "";                     // meta data (myserversettings.java) [HTTPS_PROTOCOL, HTTP_PROTOCOL]
   
   public static String barometer_instrument_correction            = "";                     // meta data (mybarometer.java)
   public static String mode                                       = "";
   public static String wind_units                                 = "";                     // meta data (wind units observed/measured)
   public static String wind_units_dashboard                       = "";                     // meta data (wind units graphs/dashboard)
   public static String ship_type_dashboard                        = "";                     // meta data (for dashboard)
   public static String dashboard_background_image                 = "";                     // meta data (for dashboard)
   public static String ship_deck_color_String                     = "";                     // meta data (for dashboard)
   public static String ship_tank_color_String                     = "";                     // meta data (for dashboard)
   public static String dashboard_font                             = "";                     // meta data (for dashboard)
   
   public static String leaflet_maps_obs_year                      = "";                     // for date time in infowindow on World map
   public static String leaflet_maps_obs_month                     = "";                     // for date time in infowindow on World map
   public static String leaflet_maps_obs_day                       = "";                     // for date time in infowindow on World map
   public static String leaflet_maps_obs_hour                      = "";                     // for date time in infowindow on World map
   public static String leaflet_maps_obs_wind_dir                  = "";                     // for infowindow on World map
   public static String leaflet_maps_obs_wind_speed                = "";                     // for infowindow on World map
   public static String leaflet_maps_obs_air_temp                  = "";                     // for infowindow on World map
   public static String leaflet_maps_obs_sst                       = "";                     // for infowindow on World map
   public static String leaflet_maps_obs_msl_pressure              = "";                     // for infowindow on World map
   public static String OSM_mode                                   = "";
   public static String GUI_mode                                   = "";                     // light/full               // always empty from version 4.2
   public static String GUI_logo                                   = "";                     // EUMETNET/NOAA/SOT logo   // always empty from version 4.2
   
   public static String eucaws_uploads_method                      = "";                     // meta data for obs format settings
   public static String obs_format                                 = "";                     // meta data for obs format settings (e.g. FORMAT_101 or FORMAT_FM13)
   public static String obs_101_encryption                         = FORMAT_101_ENCRYPTION_NO;// meta data for obs format settings (from version 4.4. no call sign encryption by default)
   public static String obs_101_email                              = "";                     // meta data for obs format settings 
   public static String upload_URL                                 = "";                     // meta data for server settings (used by Output -> Obs to server)
   public static boolean amos_mail                                 = false;                  // meta data (myemailsettings.java)
   public static String newline                                    = System.getProperty("line.separator");
   public static String theme_mode                                 = "";
   public static int x_pos_frame;
   public static int y_pos_frame;
   public static int x_pos_small_frame;
   public static int y_pos_small_frame;
   public static int x_pos_about_frame;
   public static int y_pos_about_frame;
   public static int x_pos_main_frame;
   public static int y_pos_main_frame;
   public static int x_pos_start_frame;
   public static int y_pos_start_frame;
   public static int x_pos_amver_frame;
   public static int y_pos_amver_frame;
   public static int x_pos_calculator_frame;
   public static int y_pos_calculator_frame;
   public static int x_pos_pop_up_frame;
   public static int y_pos_pop_up_frame;
   public static int x_pos_immtlogperiod_frame;
   public static int y_pos_immtlogperiod_frame;
   public static int screenWidth;
   public static int screenHeight;
   public static boolean in_next_sequence                          = false;
   public static boolean offline_mode;
   public static boolean offline_mode_via_jnlp;
   public static boolean offline_mode_via_cmd;
   public static boolean tray_icon_clicked;
   public static boolean use_system_date_time_for_updating         = false;         // NB if you start with true then if the data/comfirmation box pop-ups and the user disagree the time is still inserted by the timer loop
   public static boolean theme_changed                             = false;         // used by checking more than one instance running
   public static TrayIcon trayIcon;
   public static String obs_write                                  = "";
   public static String PORT_command_line                          = "";            // alternative for PORT (via command line) for checking running second instance
   
   // private var's
   private static final int PORT                                   = 12345;		   // for checking only one instance is running  // random large port number
	private static ServerSocket s;                                                   // do not delete!
   //private SingleInstanceService sis                               = null;          // for checking only one instance is running
   //private SISListener sisL                                        = null;          // for checking only one instance is running
   public static String output_dir                                 = null;          // for function Kopieeren_Waarnemers_En_Aantallen()
   private static String hulp_dir                                  = "";            // for writing configuration.txt file in data_dir (system defined) AND logs_dir (user defined) (backup for muffin)
   //private URL url_php;
   
   private String output_file                                      = "";
   public static String volledig_path_dstFilename_immt             = "";
   public static String volledig_path_srcFilename_immt             = "";
   public static String volledig_path_backup_srcFilename_immt      = "";
   public static String volledig_path_dstFilename_captain          = "";
   public static String volledig_path_srcFilename_captain          = "";
   public static String volledig_path_backup_srcFilename_captain   = "";
   public static String temp_logs_dir                              = "";
   private static String email_send_mode                           = "";             // SMTP_LOCAL_HOST / GMAIL_TLS / GMAIL_SSL / YAHOO_TLS / YAHOO_SSL / EMAIL_SEND_CUSTOM
   public static String last_record                                = "";
   public static String[] jaar_substring_array                     = new String [MAX_AANTAL_JAREN_IN_IMMT];
   public static String[] observername_array                       = new String [MAX_AANTAL_WAARNEMERS];
   public static GregorianCalendar cal_systeem_datum_tijd;             
   public static GregorianCalendar cal_systeem_datum_tijd_UTC;
   public static GregorianCalendar cal_systeem_datum_tijd_LT;
   public static Font current_font;
   
   // RS232-RS422
   //
   private main_RS232_RS422 RS232_RS422                            = null;
   private RS232_mintaka RS232_mintaka_class                       = null;                 // do not delete!
   private RS232_vaisala RS232_vaisala_class                       = null;                 // do not delete!
   
   public static final String SERIAL_CONNECTION                    = "serial connection";  // see mode in password form
   //public static final String WIFI_CONNECTION                      = "WiFi connection";  // see mode in password form
   public static final String MODE_PRESSURE                        = "mode_pressure";
   public static final String MODE_AIRTEMP                         = "mode_airtemp";
   public static final String MODE_AIRTEMP_II                      = "mde_airtemp II";
   public static final String MODE_SST                             = "mode_sst";
   public static final String MODE_WIND_SPEED                      = "mode_windspeed";
   public static final String MODE_WIND_DIR                        = "mode_winddir";
   public static final String MODE_ALL_PARAMETERS                  = "mode_all_parameters";
   public static final String METEO_LOGS                           = "meteo logs";         // used in subject field email when sending meteo logs via email (default and custom)    
   
   public static final int RECORD_LENGTE_PTB330                    = 46;
   public static final int RECORD_DATUM_TIJD_BEGIN_POS_PTB330      = 34; 
   public static final int RECORD_MINUTEN_BEGIN_POS_PTB330         = RECORD_DATUM_TIJD_BEGIN_POS_PTB330 + 10;
   public static final int RECORD_P_BEGIN_POS_PTB330               = 0;
   public static final int RECORD_a_BEGIN_POS_PTB330               = 30; 
   public static final int RECORD_ppp_BEGIN_POS_PTB330             = 24; 
   
   public static final int RECORD_LENGTE_PTB220                    = 36;
   public static final int RECORD_DATUM_TIJD_BEGIN_POS_PTB220      = 24;
   public static final int RECORD_MINUTEN_BEGIN_POS_PTB220         = RECORD_DATUM_TIJD_BEGIN_POS_PTB220 + 10;
   public static final int RECORD_P_BEGIN_POS_PTB220               = 0;
   public static final int RECORD_a_BEGIN_POS_PTB220               = 22;
   public static final int RECORD_ppp_BEGIN_POS_PTB220             = 16;
   
   public static final int RECORD_LENGTE_HMP155                    = 48;
   
   public static int RS232_GPS_sentence                            = 0;   //  0 = no existing value; 1 = RMC ; 2 = GGA
   public static int RS232_GPS_connection_mode                     = 0;   //  0 = default = no GPS connected via RS232
   public static int RS232_connection_mode                         = 0;   //  0 = default = no meteorological instrument connected via RS232
   public static int RS232_connection_mode_II                      = 0;   //  0 = default = no 2nd meteorological instrument connected via RS232
   public static int bits_per_second                               = 0;   //  0 = meteorological instrument no existing value
   public static int bits_per_second_II                            = 0;   //  0 = meteorological instrument no existing value; 2nd meteo instrument
   public static int GPS_bits_per_second                           = 0;   //  0 = GPS no existing value
   public static int data_bits                                     = 0;   //  0 = meteorological instrument no existing value
   public static int data_bits_II                                  = 0;   //  0 = meteorological instrument no existing value; 2nd meteo instrument
   public static int parity                                        = 99;  // 99 = meteorological instrument no existing value
   public static int parity_II                                     = 99;  // 99 = meteorological instrument no existing value; 2nd meteo instrument
   public static int stop_bits                                     = 0;   //  0 = meteorological instrument no existing value 
   public static int stop_bits_II                                  = 0;   //  0 = meteorological instrument no existing value; 2nd meteo instrument 
   //public static int flow_control                                  = SerialPort.FLOWCONTROL_NONE;
   public static String prefered_COM_port_number                   = "";  // meteorological instrument Windows and Linux
   public static String prefered_COM_port_number_II                = "";  // meteorological instrument Windows and Linux
   public static String prefered_GPS_COM_port_number               = "";  // GPS Windows and Linux
   public static String prefered_COM_port_name                     = "";  // meteorological instrument OS X
   public static String prefered_GPS_COM_port_name                 = "";  // GPS OS X
   public static String prefered_COM_port                          = "";  // meteorological instrument generic (Windows/Linux)
   public static String prefered_COM_port_II                       = "";  // 2nd meteorological instrument generic (Windows/Linux)
   public static String prefered_GPS_COM_port                      = "";  // GPS generic (Windows/Linux/OS X)
   public static String defaultPort                                = null;// system port name for opening/closing etc
   public static String defaultPort_II                             = null;// system port name for opening/closing etc (2nd meteo instrument)
   public static String defaultPort_descriptive                    = null;// descripte port name for info messages and logging
   public static String defaultPort_descriptive_II                 = null;// descripte port name for info messages and logging
   public static String sensor_data_record_obs_pressure            = "";
   public static String sensor_data_record_obs_ppp                 = "";
   public static String sensor_data_record_obs_a                   = "";
   public static String mode_grafiek;                              // first initialisation in initComponents2() later in Functions: Graphs_Airtemp_Sensor_Data_actionPerformed() etc.
   public static String lan_ip_address                             = "";  // IP address TurboWin+ is listening to if Mintaka ENet box is connected
   public static SimpleDateFormat sdf3;
   public static SimpleDateFormat sdf4;
   public static SimpleDateFormat sdf_tsl_1;                              // TurboWin system logs
   public static SimpleDateFormat sdf_tsl_2;                              // TurboWin system logs
   public static boolean sensor_data_file_ophalen_timer_is_gecreeerd    = false;    // static!
   public static boolean sensor_data_file_ophalen_timer_is_gecreeerd_II = false;    // static!
   
   
   public static int VOT                                           = Integer.MAX_VALUE; // for dashboard Meteo France
   
   // NB parameters like position, date time and air pressure etc. are always not editable by the observer in AWS mode (so not necessary to keep a boolean record of these parameters)
   public static boolean date_from_AWS_present                     = false;
   public static boolean time_from_AWS_present                     = false;
   public static boolean latitude_from_AWS_present                 = false;
   public static boolean longitude_from_AWS_present                = false;
   public static boolean COG_from_AWS_present                      = false;
   public static boolean SOG_from_AWS_present                      = false;
   public static boolean true_heading_from_AWS_present             = false;
   public static boolean pressure_sensor_level_from_AWS_present    = false;
   public static boolean pressure_MSL_from_AWS_present             = false;
   public static boolean pressure_tendency_from_AWS_present        = false;
   public static boolean pressure_characteristic_from_AWS_present  = false;
   public static boolean air_temp_from_AWS_present                 = false;
   public static boolean rh_from_AWS_present                       = false;
   public static boolean SST_from_AWS_present                      = false;
   public static boolean relative_wind_speed_from_AWS_present      = false;
   public static boolean relative_wind_dir_from_AWS_present        = false;
   public static boolean true_wind_speed_from_AWS_present          = false;
   public static boolean true_wind_dir_from_AWS_present            = false;
   public static boolean true_wind_gust_from_AWS_present           = false;
   public static boolean true_wind_gust_dir_from_AWS_present       = false;
   public static boolean displayed_aws_data_obsolate               = false;   // for DASHBOARD; set in Function: RS422_init_new_aws_data_received_check_timer()[main_RS232_RS422.java]
   public static boolean displayed_barometer_data_obsolate         = false;   // for DASHBOARD; set in Function: RS232_WiFi_init_new_aws_data_received_check_timer()[main_RS232_RS422.java]
   public static boolean displayed_thermometer_data_obsolete       = false;   // for DASHBOARD; set in Function: RS232_WiFi_init_new_aws_data_received_check_timer_II()[main_RS232_RS422.java]
   public static boolean VOT_from_AWS_present                      = false;   // for DASHBOARD
   
   public static final int NUMBER_COM_PORTS                        = 20;     // used by checking COM ports meteorological instrument (barometer, EUCAWS) and also for GPS
   public static final int LENGTE_SMD_STRING                       = 14;//14;//1024;//20;  // 20 is willekeurig, moet nog precies bepaald worden
   public static final int TOTAL_NUMBER_RECORD_COMMAS              = 27;
   public static final int TOTAL_NUMBER_RECORD_COMMAS_MINTAKA      = 3;
   public static final int TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR = 8;
   public static final int TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX= 13;
   public static final int DATE_COMMA_NUMBER                       = 1;   // reading from EUCAWS sensor data file
   public static final int TIME_COMMA_NUMBER                       = 2;   //             --"--
   public static final int LATITUDE_COMMA_NUMBER                   = 3;   //             --"--
   public static final int LONGITUDE_COMMA_NUMBER                  = 4;   //             --"-- 
   public static final int COG_COMMA_NUMBER                        = 5;   //             --"--
   public static final int SOG_COMMA_NUMBER                        = 6;   //             --"--
   public static final int HEADING_COMMA_NUMBER                    = 7;   //             --"--
   public static final int PRESSURE_SENSOR_HEIGHT_COMMA_NUMBER     = 8;   //             --"--
   public static final int PRESSURE_MSL_COMMA_NUMBER               = 9;   //             --"--
   public static final int PRESSURE_TENDENCY_COMMA_NUMBER          = 10;  //             --"--
   public static final int PRESSURE_CHARACTERISTIC_COMMA_NUMBER    = 11;  //             --"--
   public static final int AIR_TEMP_COMMA_NUMBER                   = 12;  //             --"--
   public static final int HUMIDITY_COMMA_NUMBER                   = 13;  //             --"--
   public static final int SST_COMMA_NUMBER                        = 14;  //             --"--
   public static final int RELATIVE_WIND_SPEED_COMMA_NUMBER        = 15;  //             --"--
   public static final int RELATIVE_WIND_DIR_COMMA_NUMBER          = 16;  //             --"--
   public static final int TRUE_WIND_SPEED_COMMA_NUMBER            = 17;  //             --"--
   public static final int TRUE_WIND_DIR_COMMA_NUMBER              = 18;  //             --"--
   public static final int TRUE_WIND_GUST_COMMA_NUMBER             = 19;  //             --"--
   public static final int TRUE_WIND_GUST_DIR_COMMA_NUMBER         = 20;  //             --"--
   // NB --- supply voltage                                          21
   // NB --- internal temperature                                    22
   public static final int VOT_COMMA_NUMBER                        = 23; //              --"--
   // NB --- spare                                                   24
   // NB --- spare                                                   25
   // NB --- spare                                                   26
   
   
   public static int type_record_lengte                             = 0;
   public static int type_record_datum_tijd_begin_pos               = 0;
   public static int type_record_pressure_begin_pos                 = 0;
   public static int type_record_a_begin_pos                        = 0;
   public static int type_record_ppp_begin_pos                      = 0;
  
   //public static SerialPort[] portList;//public static String[] portList;
   public static GregorianCalendar obs_file_datum_tijd;
   public static SerialPort serialPort                              = null;
   public static SerialPort serialPort_II                           = null;
   public static SerialPort GPS_serialPort                          = null;
   public static String total_string;     
   public static String total_string_II;  
   public static boolean obsolate_data_flag                         = false;   // obsolete meteo data (barometer)
   public static boolean obsolate_data_flag_II                      = false;   // obsolete meteo data 2nd instrument (thermometer)
   public static boolean obsolate_GPS_data_flag                     = false;   // obsolete GPS data     
   
   private RS232_view graph_form;
   private DASHBOARD_view dashboard_form;
   private DASHBOARD_view_AWS dashboard_form_AWS;                   // analogue AWS
   private DASHBOARD_view_AWS_digital dashboard_form_AWS_digital;   // digital AWS
   private DASHBOARD_view_AWS_hybrid dashboard_form_AWS_hybrid;     // hybrid AWS
   public static Obs_Stats_view graph_obs_stats;
   public static DASHBOARD_view_AWS_radar dashboard_form_AWS_radar; // wind radar AWS
   public static DASHBOARD_view_APR_radar dashboard_form_APR_radar; // meteo radar APR // also used in mywind.java
   public static boolean dashboard_was_manually_closed_in_dashboard_pop_up_period  = false;  // in connection with pop-up dashboard (AWS and APR)
   public static boolean dashboard_was_automatically_opened         = false;                 // in connection with pop-up dashboard (AWS and APR)
   public static boolean turbowin_start_up_sequence_finished        = false;                 // in connection with pop-up dashboard (AWS and APR) 
   public static boolean in_dashboard_pop_up_period                 = false;                 // in connection with pop-up dashboard (AWS and APR) 
   public static boolean port_mode_option                           = false;                 // deactivate APR/AWSR is ship speed is minimal (e.g. < 1 knot) 
   
   public static final Color input_color_from_aws                   = Color.RED;  // color for text fields if input was measured by AWS (manually input of that text field disabled)
   public static final Color input_color_from_observer              = Color.BLACK;
   public static final Color obsolate_color_data_from_aws           = Color.GRAY;
   public static final Color input_color_from_apr                   = Color.RED;  
   public static final Color obsolete_input_color_from_apr          = Color.GRAY;
   
   // WOW
   public static boolean WOW                                        = false;       // meta data  yes or no publish on WOW (WeatherObservationsWebsite)
   public static String WOW_site_id                                 = "";          // meta data
   public static String WOW_site_pin                                = "";          // meya data
   public static String WOW_reporting_interval                      = "";          // meta data
   //public static String WOW_average_height_barometer                = "";        // meta data
   //public final static String WOW_REPORTING_INTERVAL_MANUAL         = "44444";     // meta data (44444 is just a number)
   
   // AP[&T]R (Automated Pressure [&Temperature] Reports)
   public static boolean APR                                        = false;       // meta data   (WOW_APR_settings.java)
   public static String APR_reporting_interval                      = "";          // meta data   (WOW_APR_settings.java)
   
   // AWSR (Automatic Weather Station Reports)
   public static boolean AWSR                                        = false;      // meta data   (WOW_APR_settings.java)
   public static String AWSR_reporting_interval                      = "";         // meta data   (WOW_APR_settings.java)
   
   // AP[&T]R and AWSR
   public static String APTR_AWSR_send_method                        = "";         // meta data   (WOW_APR_settings.java)
   
   // AP[&T]R /AWS pop-up visual obs reminder dashboard
   //public static boolean pop_up_dashboard                            = false;      // meta data   (WOW_APR_settings.java)
   //public static String pop_up_dashboard_interval                    = "";         // meta data   (WOW_APR_settings.java)   
   
   // AP[&T]R /AWS pop-up visual obs reminder screen
   public static boolean pop_up_screen                               = false;      // meta data   (WOW_APR_settings.java)
   public static String pop_up_screen_interval                       = "";         // meta data   (WOW_APR_settings.java)   
   public static pop_up_screen pop_up_form                           = null;
   public static boolean screen_was_manually_closed_in_pop_up_period = false;
   //public static boolean in_screen_pop_up_period                     = false;
   
   // WOW and AP[&T]R and AWSR
   public static String WOW_APR_average_draught                      = "";         // meta data   (WOW_APR_ettings.java)
 
   
   public static SystemTray tray;
   private JPopupMenu popup_input;
   
   public static ship myship;                                        // class
   public static main mainClass;                                     // class
   public static FORMAT_101 format_101_class;                        // class
   public static OSM osm_class;                                      // class
   public static Python_Email python_email_class;                    // class
   public static Jakarta_Email jakarta_email_class;                  // class
   public static main_support support_class;                         // class
   
   public static String obs_stats_mode;                              // to distinguish to display the observers stas or the observations stats
   //public static boolean display_resolution_greather_than_HD;        // for drawing ships on dashboards
}




