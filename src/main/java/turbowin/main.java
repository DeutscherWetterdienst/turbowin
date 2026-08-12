package turbowin;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

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
* main.configuratie_regels[86]                                  => dashboard_font;                          // for hybrid dashboard
* main.configuratie_regels[87]                                  => communication protocol client <-> server (HTTPS-HTTP]
* main.configuratie_regels[88]                                  => eucaws obs id (if added to SMD input)
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

public class main extends javax.swing.JFrame {

  /* inner class popupListener */
  class PopupListener_input extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      ShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      ShowPopup(e);
    }

    private void ShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup_input.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  } // class PopupListener_input extends MouseAdapter

  /* Creates new form main */
  public main() {

    if (!theme_mode.equals(THEME_TRANSPARENT)) {
      // always at first start-up of this application
      try {
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
          if ("Nimbus".equals(info.getName())) {
            UIManager.setLookAndFeel(info.getClassName());

            break;
          }
        }
      } catch (ClassNotFoundException
          | InstantiationException
          | IllegalAccessException
          | UnsupportedLookAndFeelException e) {
        // If Metal is not available, you can set the GUI to another look and feel.
        // JOptionPane.showMessageDialog(null, "Metal color scheme not supported on this computer",
        // main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
        try {
          UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } // java default look and feel (voor Java 1.6 het zelde als:
        // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");)
        catch (ClassNotFoundException
            | InstantiationException
            | IllegalAccessException
            | UnsupportedLookAndFeelException ex) {
        }
      }

      String os = OSDetector.getOSString();

      if (os.equals("LINUX")) {
        if (theme_changed == false) {
          current_font = super.getFont();
        } else // theme_changed
        {
          super.setFont(current_font);
        }
      }
    } //  if (!theme_mode.equals(THEME_TRANSPARENT))
    else // THEME_TRANSPARENT
    {
      // NB https://stackoverflow.com/questions/7434845/setting-the-default-font-of-swing-program

      String os = OSDetector.getOSString();

      if (os.equals("LINUX")) {
        // NB necesarry because otherwise under LINUX OS the labels/text etc. takes too much space
        // (but only in transparent mode!), not appropriate under Windos OS
        //    related to: getCrossPlatformLookAndFeelClassName() and
        // setDefaultLookAndFeelDecorated(true) (see: Themes_5_actionPerformed()[main.java])
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

    if (theme_mode.equals(THEME_TRANSPARENT)) {
      // NB before, by invoking initComponents2(), most of the main start-up settings were already
      // done
      //    but the specific main screen (menu) items settings must be done again

      setOpacity(0.75f);

      // font check
      String os = OSDetector.getOSString();

      if (os.equals("LINUX")) {
        Font f = jLabel1.getFont();
        String fontName = f.getFontName();
        if (fontName.equals("Ubuntu") == false) {
          JOptionPane.showMessageDialog(
              null,
              "Install Ubuntu fonts for a better GUI lay out in opacity Theme mode",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
        }
      }
    } // else if (theme_mode.equals(THEME_TRANSPARENT))

    // Font name to console (all operating systems)
    Font f = jLabel1.getFont();
    String fontName = f.getFontName();
    System.out.println("--- current font = " + fontName);
  }

  /**
   * This method is called from within the init() method to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
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
    jMenuItem141 = new javax.swing.JMenuItem();
    jMenuItem78 = new javax.swing.JMenuItem();
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
    addWindowListener(
        new java.awt.event.WindowAdapter() {
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
    jButton2.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton3.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton6.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton7.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton8.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton4.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton5.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton11.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton9.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton10.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton12.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton13.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton14.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton15.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton16.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton17.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton18.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton19.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jButton20.addMouseListener(
        new java.awt.event.MouseAdapter() {
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
    jCheckBox1.addItemListener(
        new java.awt.event.ItemListener() {
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
    jCheckBox2.addItemListener(
        new java.awt.event.ItemListener() {
          public void itemStateChanged(java.awt.event.ItemEvent evt) {
            AWSR_toolbar_itemStateChanged(evt);
          }
        });
    jToolBar1.add(jCheckBox2);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(
                jToolBar1,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(
                jToolBar1,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));

    jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel33.setForeground(new java.awt.Color(0, 0, 255));
    jLabel33.setText("Cl");
    jLabel33.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            cl_mainscreen_mouseClicked(evt);
          }
        });

    jTextField33.setEditable(false);
    jTextField33.setFocusable(false);
    jTextField33.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            cl_mainscreen_mouseClicked(evt);
          }
        });

    jLabel34.setForeground(new java.awt.Color(0, 0, 255));
    jLabel34.setText("Cm");
    jLabel34.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            cm_mainscreen_mouseClicked(evt);
          }
        });

    jTextField34.setEditable(false);
    jTextField34.setFocusable(false);
    jTextField34.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            cm_mainscreen_mouseClicked(evt);
          }
        });

    jLabel36.setForeground(new java.awt.Color(0, 0, 255));
    jLabel36.setText("Ch");
    jLabel36.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            ch_mainscreen_mouseClicked(evt);
          }
        });

    jTextField35.setEditable(false);
    jTextField35.setFocusable(false);
    jTextField35.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            ch_mainscreen_mouseClicked(evt);
          }
        });

    jLabel13.setForeground(new java.awt.Color(0, 0, 255));
    jLabel13.setText("Present weath.");
    jLabel13.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            present_weather_mainscreen_mouseClicked(evt);
          }
        });

    jTextField13.setEditable(false);
    jTextField13.setFocusable(false);
    jTextField13.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            present_weather_mainscreen_mouseClicked(evt);
          }
        });

    jLabel14.setForeground(new java.awt.Color(0, 0, 255));
    jLabel14.setText("Past weath. 1st");
    jLabel14.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_1_mainscreen_mouseClicked(evt);
          }
        });

    jTextField14.setEditable(false);
    jTextField14.setFocusable(false);
    jTextField14.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_1_mainscreen_mouseClicked(evt);
          }
        });

    jLabel15.setForeground(new java.awt.Color(0, 0, 255));
    jLabel15.setText("Past weath. 2nd");
    jLabel15.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_2_mainscreen_mouseClicked(evt);
          }
        });

    jTextField15.setEditable(false);
    jTextField15.setFocusable(false);
    jTextField15.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            past_weather_2_mainscreen_mouseClicked(evt);
          }
        });

    jLabel19.setForeground(new java.awt.Color(0, 0, 255));
    jLabel19.setText("Icing");
    jLabel19.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            icing_mainscreen_mouseClicked(evt);
          }
        });

    jTextField21.setEditable(false);
    jTextField21.setFocusable(false);
    jTextField21.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            icing_mainscreen_mouseClicked(evt);
          }
        });

    jLabel21.setText("Ice");
    jLabel21.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            ice_mainscreen_mouseClicked(evt);
          }
        });

    jTextField19.setEditable(false);
    jTextField19.setFocusable(false);
    jTextField19.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            ice_mainscreen_mouseClicked(evt);
          }
        });

    jLabel30.setForeground(new java.awt.Color(0, 0, 255));
    jLabel30.setText("Total cloud cov");
    jLabel30.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            total_cloud_cover_mainscreen_mouseClicked(evt);
          }
        });

    jTextField30.setEditable(false);
    jTextField30.setFocusable(false);
    jTextField30.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            total_cloud_cover_mainscreen_mouseClicked(evt);
          }
        });

    jLabel31.setForeground(java.awt.Color.blue);
    jLabel31.setText("Amount Cl (Cm)");
    jLabel31.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            amount_cl_mainscreen_mouseClicked(evt);
          }
        });

    jTextField31.setEditable(false);
    jTextField31.setFocusable(false);
    jTextField31.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            amount_cl_mainscreen_mouseClicked(evt);
          }
        });

    jLabel32.setForeground(new java.awt.Color(0, 0, 255));
    jLabel32.setText("ht lowest cloud");
    jLabel32.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            height_lowest_cloud_mainscreen_mouseClicked(evt);
          }
        });

    jTextField32.setEditable(false);
    jTextField32.setFocusable(false);
    jTextField32.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            height_lowest_cloud_mainscreen_mouseClicked(evt);
          }
        });

    jLabel20.setForeground(new java.awt.Color(0, 0, 255));
    jLabel20.setText("Observer");
    jLabel20.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            observer_mainscreen_mouseClicked(evt);
          }
        });

    jTextField20.setEditable(false);
    jTextField20.setFocusable(false);
    jTextField20.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            observer_mainscreen_mouseClicked(evt);
          }
        });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel2Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(
                                jPanel2Layout
                                    .createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel34)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel36)
                                    .addComponent(jLabel33)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel14)
                                    .addComponent(
                                        jLabel30,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addComponent(jLabel15)
                                    .addComponent(
                                        jLabel32,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addComponent(
                                        jLabel31,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addComponent(
                                        jLabel13,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        1,
                                        Short.MAX_VALUE))
                            .addComponent(jLabel20))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(
                                jTextField31,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField32,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField21,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField13,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField14,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField15,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField33,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField34,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField35,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField30,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField20,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField19,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                180,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap()));

    jPanel2Layout.linkSize(
        javax.swing.SwingConstants.HORIZONTAL,
        new java.awt.Component[] {
          jTextField13,
          jTextField14,
          jTextField15,
          jTextField19,
          jTextField20,
          jTextField21,
          jTextField30,
          jTextField31,
          jTextField32,
          jTextField33,
          jTextField34,
          jTextField35
        });

    jPanel2Layout.setVerticalGroup(
        jPanel2Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel2Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(
                                jTextField13,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(
                                jTextField14,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(
                                jTextField15,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField33,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(
                                jTextField34,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField35,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(
                                jTextField30,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31)
                            .addComponent(
                                jTextField31,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField32,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(
                                jTextField21,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField19,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField20,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                    .addContainerGap()));

    jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel38.setText("Seawater temp");
    jLabel38.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            seawater_temp_mainscreen_mouseClicked(evt);
          }
        });

    jTextField40.setEditable(false);
    jTextField40.setFocusable(false);
    jTextField40.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            seawater_temp_mainscreen_mouseClicked(evt);
          }
        });

    jLabel16.setText("Apparent wind");
    jLabel16.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            true_wind_speed_mainscreen_mouseClicked(evt);
          }
        });

    jTextField16.setEditable(false);
    jTextField16.setFocusable(false);
    jTextField16.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            true_wind_speed_mainscreen_mouseClicked(evt);
          }
        });

    jLabel22.setForeground(java.awt.Color.blue);
    jLabel22.setText("(Wind) wave ht");
    jLabel22.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_wave_height_mainscreen_mouseClicked(evt);
          }
        });

    jTextField22.setEditable(false);
    jTextField22.setFocusable(false);
    jTextField22.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_wave_height_mainscreen_mouseClicked(evt);
          }
        });

    jLabel24.setForeground(new java.awt.Color(0, 0, 255));
    jLabel24.setText("1st swell dir");
    jLabel24.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_dir_mainscreen_mouseClicked(evt);
          }
        });

    jTextField24.setEditable(false);
    jTextField24.setFocusable(false);
    jTextField24.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_dir_mainscreen_mouseClicked(evt);
          }
        });

    jLabel25.setForeground(new java.awt.Color(0, 0, 255));
    jLabel25.setText("1st swell height");
    jLabel25.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_height_mainscreen_mouseClicked(evt);
          }
        });

    jTextField25.setEditable(false);
    jTextField25.setFocusable(false);
    jTextField25.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_height_mainscreen_mouseClicked(evt);
          }
        });

    jLabel27.setForeground(new java.awt.Color(0, 0, 255));
    jLabel27.setText("2nd swell dir");
    jLabel27.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_dir_mainscreen_mouseClicked(evt);
          }
        });

    jTextField27.setEditable(false);
    jTextField27.setFocusable(false);
    jTextField27.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_dir_mainscreen_mouseClicked(evt);
          }
        });

    jLabel29.setForeground(new java.awt.Color(0, 0, 255));
    jLabel29.setText("2nd swell period");
    jLabel29.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_period_mainscreen_mouseClicked(evt);
          }
        });

    jTextField29.setEditable(false);
    jTextField29.setFocusable(false);
    jTextField29.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_period_mainscreen_mouseClicked(evt);
          }
        });

    jLabel17.setText("True wind");
    jLabel17.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            true_wind_dir_mainscreen_mouseClicked(evt);
          }
        });

    jTextField17.setEditable(false);
    jTextField17.setFocusable(false);
    jTextField17.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            true_wind_dir_mainscreen_mouseClicked(evt);
          }
        });

    jLabel23.setForeground(new java.awt.Color(0, 0, 255));
    jLabel23.setText("(Wind) wave per");
    jLabel23.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_wave_period_mainscreen_mouseClicked(evt);
          }
        });

    jTextField23.setEditable(false);
    jTextField23.setFocusable(false);
    jTextField23.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            wind_wave_period_mainscreen_mouseClicked(evt);
          }
        });

    jLabel26.setForeground(new java.awt.Color(0, 0, 255));
    jLabel26.setText("1st swell period");
    jLabel26.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_period_mainscreen_mouseClicked(evt);
          }
        });

    jTextField26.setEditable(false);
    jTextField26.setFocusable(false);
    jTextField26.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_1_period_mainscreen_mouseClicked(evt);
          }
        });

    jLabel28.setForeground(new java.awt.Color(0, 0, 255));
    jLabel28.setText("2nd swell height");
    jLabel28.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_height_mainscreen_mouseClicked(evt);
          }
        });

    jTextField28.setEditable(false);
    jTextField28.setFocusable(false);
    jTextField28.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            swell_2_height_mainscreen_mouseClicked(evt);
          }
        });

    jLabel18.setForeground(java.awt.Color.blue);
    jLabel18.setText("Visibility");
    jLabel18.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            visibility_mainscreen_mouseClicked(evt);
          }
        });

    jTextField18.setEditable(false);
    jTextField18.setFocusable(false);
    jTextField18.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            visibility_mainscreen_mouseClicked(evt);
          }
        });

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel3Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                            .addGroup(
                                jPanel3Layout
                                    .createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(
                                        jLabel17,
                                        javax.swing.GroupLayout.Alignment.LEADING,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addComponent(
                                        jLabel38,
                                        javax.swing.GroupLayout.Alignment.LEADING,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)))
                    .addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(
                                jTextField40,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField17,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField16,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField23,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField22,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField24,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField26,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField25,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField27,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField29,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField28,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                170,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField18,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    jPanel3Layout.linkSize(
        javax.swing.SwingConstants.HORIZONTAL,
        new java.awt.Component[] {
          jTextField16,
          jTextField17,
          jTextField18,
          jTextField22,
          jTextField23,
          jTextField24,
          jTextField25,
          jTextField26,
          jTextField27,
          jTextField28,
          jTextField29,
          jTextField40
        });

    jPanel3Layout.setVerticalGroup(
        jPanel3Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel3Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField40,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField17,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField16,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(
                                jTextField23,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField22,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(
                                jTextField24,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(
                                jTextField26,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(
                                jTextField25,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(
                                jTextField27,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(
                                jTextField29,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(
                                jTextField28,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(
                                jTextField18,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel1.setText("Ship name");
    jLabel1.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            ship_name_mainscreen_mouseClicked(evt);
          }
        });

    jTextField1.setEditable(false);
    jTextField1.setFocusable(false);
    jTextField1.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            ship_name_mainscreen_mouseClicked(evt);
          }
        });

    jLabel2.setText("Station ID");
    jLabel2.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            station_ID_mainscreen_mouseClicked(evt);
          }
        });

    jTextField2.setEditable(false);
    jTextField2.setFocusable(false);
    jTextField2.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            station_ID_mainscreen_mouseClicked(evt);
          }
        });

    jLabel3.setText("Date & Time obs");
    jLabel3.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            date_time_mainscreen_mouseClicked(evt);
          }
        });

    jTextField3.setEditable(false);
    jTextField3.setFocusable(false);
    jTextField3.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            date_time_mainscreen_mouseClicked(evt);
          }
        });

    jLabel5.setText("Position");
    jLabel5.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            position_mainscreen_mouseClicked(evt);
          }
        });

    jTextField5.setEditable(false);
    jTextField5.setFocusable(false);
    jTextField5.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            position_mainscreen_mouseClicked(evt);
          }
        });

    jLabel7.setText("Course & Speed");
    jLabel7.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            course_speed_mainscreen_mouseClicked(evt);
          }
        });

    jTextField7.setEditable(false);
    jTextField7.setFocusable(false);
    jTextField7.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            course_speed_mainscreen_mouseClicked(evt);
          }
        });

    jLabel9.setText("Pressure (read+ic)");
    jLabel9.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            pressure_read_mainscreen_mouseClicked(evt);
          }
        });

    jTextField9.setEditable(false);
    jTextField9.setFocusable(false);
    jTextField9.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            pressure_read_mainscreen_mouseClicked(evt);
          }
        });

    jLabel10.setText("Pressure (MSL)");
    jLabel10.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            pressure_msl_mainscreen_mouseClicked(evt);
          }
        });

    jTextField10.setEditable(false);
    jTextField10.setFocusable(false);
    jTextField10.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            pressure_msl_mainscreen_mouseClicked(evt);
          }
        });

    jLabel11.setText("Pressure tendency");
    jLabel11.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            amount_pressure_tendency_mainscreen_mouseClicked(evt);
          }
        });

    jTextField11.setEditable(false);
    jTextField11.setFocusable(false);
    jTextField11.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            amount_pressure_tendency_mainscreen_mouseClicked(evt);
          }
        });

    jLabel12.setText("Char. press. tend.");
    jLabel12.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            char_pressure_tendency_mainscreen_mouseClicked(evt);
          }
        });

    jTextField12.setEditable(false);
    jTextField12.setFocusable(false);
    jTextField12.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            char_pressure_tendency_mainscreen_mouseClicked(evt);
          }
        });

    jLabel35.setText("Air temp");
    jLabel35.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            air_temp_mainscreen_mouseClicked(evt);
          }
        });

    jTextField36.setEditable(false);
    jTextField36.setFocusable(false);
    jTextField36.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            air_temp_mainscreen_mouseClicked(evt);
          }
        });

    jLabel37.setText("Wet-bulb temp");
    jLabel37.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            wet_bulb_temp_mainscreen_mouseClicked(evt);
          }
        });

    jTextField37.setEditable(false);
    jTextField37.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    jTextField37.setFocusable(false);
    jTextField37.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            wet_bulb_temp_mainscreen_mouseClicked(evt);
          }
        });

    jLabel40.setText("Dew point");
    jLabel40.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            dew_point_mainscreen_mouseClicked(evt);
          }
        });

    jTextField38.setEditable(false);
    jTextField38.setFocusable(false);
    jTextField38.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(java.awt.event.MouseEvent evt) {
            dew_point_mainscreen_mouseClicked(evt);
          }
        });

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel4Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9)
                            .addComponent(jLabel12)
                            .addComponent(jLabel10)
                            .addComponent(jLabel3)
                            .addGroup(
                                jPanel4Layout
                                    .createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(
                                        jLabel2,
                                        javax.swing.GroupLayout.Alignment.LEADING,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addComponent(
                                        jLabel11,
                                        javax.swing.GroupLayout.Alignment.LEADING,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        98,
                                        Short.MAX_VALUE))
                            .addComponent(jLabel35)
                            .addComponent(jLabel37)
                            .addComponent(
                                jLabel40,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                105,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(
                                jTextField1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField2,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField3,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField5,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField7,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField9,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField10,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField11,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField12,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField36,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField37,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jTextField38,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                202,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap()));

    jPanel4Layout.linkSize(
        javax.swing.SwingConstants.HORIZONTAL,
        new java.awt.Component[] {
          jTextField1,
          jTextField10,
          jTextField11,
          jTextField12,
          jTextField2,
          jTextField3,
          jTextField36,
          jTextField37,
          jTextField38,
          jTextField5,
          jTextField7,
          jTextField9
        });

    jPanel4Layout.setVerticalGroup(
        jPanel4Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel4Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(
                                jTextField1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField2,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField3,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField5,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField7,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField9,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField10,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField11,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField12,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(
                                jTextField36,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37)
                            .addComponent(
                                jTextField37,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField38,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    jPanel5.setOpaque(false);

    jTextField4.setEditable(false);
    jTextField4.setFocusable(false);

    jLabel4.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel4.setText("Turbo+");

    jLabel39.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel39.setText(
        "--- adding data: input menu, popup menu, toolbar icons or click on the text labels or fields ---");

    jLabel6.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel6.setText("-");

    jLabel41.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
    jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel41.setText("-");

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel5Layout
                    .createSequentialGroup()
                    .addGroup(
                        jPanel5Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(
                                jLabel6,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                jTextField4,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                931,
                                Short.MAX_VALUE)
                            .addComponent(
                                jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE)
                            .addComponent(
                                jLabel39,
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                jSeparator1,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                931,
                                Short.MAX_VALUE)
                            .addComponent(
                                jLabel41,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    jPanel5Layout.linkSize(
        javax.swing.SwingConstants.HORIZONTAL,
        new java.awt.Component[] {jLabel4, jSeparator1, jTextField4});

    jPanel5Layout.setVerticalGroup(
        jPanel5Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel5Layout
                    .createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addComponent(jLabel39)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        jSeparator1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        2,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        jTextField4,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel41)));

    jMenuBar1.setMaximumSize(new java.awt.Dimension(200, 21));
    jMenuBar1.setPreferredSize(new java.awt.Dimension(200, 21));

    jMenu1.setText("File");

    jMenuItem1.setText("Exit");
    jMenuItem1.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            File_Exit_menu_actionPerformd(evt);
          }
        });
    jMenu1.add(jMenuItem1);

    jMenuBar1.add(jMenu1);

    jMenu2.setText("Input");

    jMenuItem30.setText("Next form automation");
    jMenuItem30.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Next_form_automation_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem30);
    jMenu2.add(jSeparator4);

    jMenuItem3.setText("Date & Time...");
    jMenuItem3.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_DateTime_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem3);

    jMenuItem4.setText("Position, Course & Speed...");
    jMenuItem4.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Position_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem4);

    jMenuItem7.setText("Barometer reading...");
    jMenuItem7.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Barometer_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem7);

    jMenuItem8.setText("Barograph reading...");
    jMenuItem8.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Barograph_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem8);

    jMenuItem9.setText("Temperatures...");
    jMenuItem9.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Temperatures_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem9);

    jMenuItem5.setText("Wind...");
    jMenuItem5.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Wind_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem5);

    jMenuItem6.setText("Waves...");
    jMenuItem6.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_waves_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem6);

    jMenuItem12.setText("Visibility...");
    jMenuItem12.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Visibility_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem12);

    jMenuItem10.setText("Present weather...");
    jMenuItem10.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Presentweather_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem10);

    jMenuItem11.setText("Past weather...");
    jMenuItem11.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Pastweather_menu_actionperformed(evt);
          }
        });
    jMenu2.add(jMenuItem11);

    jMenuItem13.setText("Clouds low...");
    jMenuItem13.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Cloudslow_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem13);

    jMenuItem14.setText("Clouds middle...");
    jMenuItem14.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Cloudsmiddle_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem14);

    jMenuItem15.setText("Clouds high...");
    jMenuItem15.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Cloudshigh_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem15);

    jMenuItem16.setText("Cloud cover & height...");
    jMenuItem16.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Cloudcover_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem16);

    jMenuItem17.setText("Icing...");
    jMenuItem17.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Icing_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem17);

    jMenuItem18.setText("Ice...");
    jMenuItem18.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Ice_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem18);

    jMenuItem19.setText("Observer...");
    jMenuItem19.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Input_Observer_menu_actionPerformed(evt);
          }
        });
    jMenu2.add(jMenuItem19);

    jMenuBar1.add(jMenu2);

    jMenu3.setText("Output");
    jMenu3.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_by_email_Custom_actionPerformed(evt);
          }
        });

    jMenuItem20.setText("Obs to server (internet)");
    jMenuItem20.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_Obs_to_server_menu_actionPerformed(evt);
          }
        });
    jMenu3.add(jMenuItem20);

    jMenuItem23.setText("Obs by Email (default)...");
    jMenuItem23.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_by_email_default_actionPerformed(evt);
          }
        });
    jMenu3.add(jMenuItem23);

    jMenuItem80.setText("Obs by Email (Custom)");
    jMenuItem80.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_by_email_Custom_actionPerformed(evt);
          }
        });
    jMenu3.add(jMenuItem80);

    jMenuItem24.setText("Obs to file...");
    jMenuItem24.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_to_file_actionPerformed(evt);
          }
        });
    jMenu3.add(jMenuItem24);

    jMenuItem46.setText("Obs to AWS");
    jMenuItem46.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_to_AWS_actionPerformed(evt);
          }
        });
    jMenu3.add(jMenuItem46);

    jMenuItem48.setText("Obs to clipboard");
    jMenuItem48.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Output_obs_to_clipboard_actionPerformed(evt);
          }
        });
    jMenu3.add(jMenuItem48);

    jMenuBar1.add(jMenu3);

    jMenu4.setText("Maintenance");
    jMenu4.setPreferredSize(new java.awt.Dimension(80, 19));

    jMenuItem21.setText("Station data...");
    jMenuItem21.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Stationdata_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem21);

    jMenuItem25.setText("Email settings...");
    jMenuItem25.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Email_settings_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem25);

    jMenuItem26.setText("Log files settings...");
    jMenuItem26.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Log_files_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem26);

    jMenuItem50.setText("Obs format setting...");
    jMenuItem50.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_obs_format_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem50);

    jMenuItem42.setText("Serial / USB / LAN device settings...");
    jMenuItem42.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Serial_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem42);

    jMenuItem52.setText("APR / APTR / AWSR settings...");
    jMenuItem52.setActionCommand("WOW/AP[&T]R/AWSR settings...");
    jMenuItem52.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_WOW_settings_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem52);

    jMenuItem54.setText("Server settings...");
    jMenuItem54.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_server_settings_actionperformed(evt);
          }
        });
    jMenu4.add(jMenuItem54);
    jMenu4.add(jSeparator2);

    jMenuItem27.setText("Observers...");
    jMenuItem27.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Observer_menu_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem27);

    jMenuItem28.setText("Captains...");
    jMenuItem28.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Captains_Menu_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem28);
    jMenu4.add(jSeparator3);

    jMenuItem2.setText("Move log files to (USB) disk...");
    jMenuItem2.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Move_log_files_to_disk_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem2);

    jMenuItem29.setText("Move log files by Email");
    jMenuItem29.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Move_log_files_by_email_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem29);
    jMenu4.add(jSeparator7);

    jMenuItem59.setText("Show all maintenance data");
    jMenuItem59.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Show_maintenance_data_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem59);

    jMenuItem60.setText("Export all maintenance data");
    jMenuItem60.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Export_maintenance_data_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem60);

    jMenuItem61.setText("Import all maintenance data");
    jMenuItem61.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maintenance_Import_maintenance_data_actionPerformed(evt);
          }
        });
    jMenu4.add(jMenuItem61);

    jMenuBar1.add(jMenu4);

    jMenu5.setText("Themes");
    jMenu5.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_5_actionPerformed(evt);
          }
        });

    jMenuItem31.setText("Day");
    jMenuItem31.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_1_actionPerformed(evt);
          }
        });
    jMenu5.add(jMenuItem31);

    jMenuItem32.setText("Night");
    jMenuItem32.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_2_actionPerformed(evt);
          }
        });
    jMenu5.add(jMenuItem32);

    jMenuItem34.setText("Sunrise");
    jMenuItem34.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_3_actionPerformed(evt);
          }
        });
    jMenu5.add(jMenuItem34);

    jMenuItem22.setText("Sunset");
    jMenuItem22.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_4_actionPerformed(evt);
          }
        });
    jMenu5.add(jMenuItem22);

    jMenuItem76.setText("Transparent");
    jMenuItem76.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Themes_5_actionPerformed(evt);
          }
        });
    jMenu5.add(jMenuItem76);

    jMenuBar1.add(jMenu5);

    jMenu6.setText("Amver");

    jMenuItem33.setText("Sailing Plan...");
    jMenuItem33.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Amver_SailingPlan_actionPerformed(evt);
          }
        });
    jMenu6.add(jMenuItem33);

    jMenuItem38.setText("Deviation Report...");
    jMenuItem38.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Amver_DeviationReport_actionPerformed(evt);
          }
        });
    jMenu6.add(jMenuItem38);

    jMenuItem39.setText("Arrival Report...");
    jMenuItem39.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Amver_ArrivalReport_actionPerformed(evt);
          }
        });
    jMenu6.add(jMenuItem39);

    jMenuItem40.setText("Position Report...");
    jMenuItem40.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Amver_PositionReport_actionPerformed(evt);
          }
        });
    jMenu6.add(jMenuItem40);

    jMenuBar1.add(jMenu6);

    jMenu8.setText("Graphs");

    jMenuItem41.setText("Sensor data pressure");
    jMenuItem41.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graphs_Pressure_Sensor_Data_actionPerformed(evt);
          }
        });
    jMenu8.add(jMenuItem41);

    jMenuItem43.setText("Sensor data air temp");
    jMenuItem43.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graphs_Airtemp_Sensor_Data_actionPerformed(evt);
          }
        });
    jMenu8.add(jMenuItem43);

    jMenuItem44.setText("Sensor data SST");
    jMenuItem44.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graphs_SST_Sensor_data_actionPerformed(evt);
          }
        });
    jMenu8.add(jMenuItem44);

    jMenuItem45.setText("Sensor data wind speed");
    jMenuItem45.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graphs_Wind_Speed_Sensor_Data_actionPerformed(evt);
          }
        });
    jMenu8.add(jMenuItem45);

    jMenuItem47.setText("Sensor data wind dir");
    jMenuItem47.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graph_Wind_Dir_Sensor_Data_actionPerformed(evt);
          }
        });
    jMenu8.add(jMenuItem47);

    jMenuItem51.setText("Sensor data total");
    jMenuItem51.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Graph_All_Sensor_Data_actionPerformed(evt);
          }
        });
    jMenu8.add(jMenuItem51);

    jMenuBar1.add(jMenu8);

    jMenu9.setText("Dashboard");

    jMenuItem72.setText("Observers stats");
    jMenuItem72.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_Obs_Stats_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem72);

    jMenuItem73.setText("Observations stats");
    jMenuItem73.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_Observations_Stats_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem73);

    jMenuItem58.setText("Latest Obs");
    jMenuItem58.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_Latest_Obs_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem58);

    jMenuItem65.setText("Latest AWS measurements");
    jMenuItem65.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_latest_AWS_measurements_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem65);
    jMenu9.add(jSeparator8);

    jMenuItem55.setText("Barometer");
    jMenuItem55.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_Barometer_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem55);

    jMenuItem77.setText("APR meteo radar");
    jMenuItem77.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_APR_radar_actionperformed(evt);
          }
        });
    jMenu9.add(jMenuItem77);
    jMenu9.add(jSeparator14);

    jMenuItem56.setText("AWS analog");
    jMenuItem56.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_AWS_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem56);

    jMenuItem57.setText("AWS digital");
    jMenuItem57.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_AWS_digital_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem57);

    jMenuItem63.setText("AWS hybrid");
    jMenuItem63.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_AWS_hybrid_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem63);

    jMenuItem64.setText("AWS wind radar");
    jMenuItem64.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Dashboard_AWS_radar_actionPerformed(evt);
          }
        });
    jMenu9.add(jMenuItem64);

    jMenuBar1.add(jMenu9);

    jMenu10.setText("Maps");

    jMenuItem66.setText("Obs's Map (offline)");
    jMenuItem66.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_Obs_Manual_Map_Offline_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem66);

    jMenuItem62.setText("AWS sensor Map (offline)");
    jMenuItem62.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_AWS_Sensor_Map_Offline_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem62);

    jMenuItem68.setText("AWS visual Map (offline)");
    jMenuItem68.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_AWS_Visual_Map_Offline_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem68);
    jMenu10.add(jSeparator9);

    jMenuItem67.setText("Obs's Map (internet)");
    jMenuItem67.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_Obs_Manual_Map_Online_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem67);

    jMenuItem69.setText("AWS sensor Map (internet)");
    jMenuItem69.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_AWS_Sensor_Map_Online_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem69);

    jMenuItem70.setText("AWS Obs's visual Map (internet)");
    jMenuItem70.setActionCommand("AWS visual Map (internet)");
    jMenuItem70.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_AWS_Visual_Map_Online_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem70);
    jMenu10.add(jSeparator15);

    jMenuItem141.setText("Satellite image IR [NOAA] (internet)");
    jMenuItem141.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_satellite_image_IR_NOAA_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem141);

    jMenuItem78.setText("Satellite image IR [SSEC] (internet)");
    jMenuItem78.setActionCommand("Satellite image IR (internet)");
    jMenuItem78.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_satellite_image_IR_SSEC_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem78);

    jMenuItem79.setText("Satellite image SST [NOAA] (internet)");
    jMenuItem79.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_satellite_image_SST_NOAA_actionPerformed(evt);
          }
        });
    jMenu10.add(jMenuItem79);
    jMenu10.add(jSeparator16);

    jMenu11.setText("Pilot charts SA (internet)");

    jMenuItem81.setText("January");
    jMenuItem81.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_january_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem81);

    jMenuItem71.setText("February");
    jMenuItem71.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_february_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem71);

    jMenuItem82.setText("March");
    jMenuItem82.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_march_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem82);

    jMenuItem83.setText("April");
    jMenuItem83.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_april_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem83);

    jMenuItem84.setText("May");
    jMenuItem84.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_may_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem84);

    jMenuItem85.setText("June");
    jMenuItem85.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_june_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem85);

    jMenuItem86.setText("July");
    jMenuItem86.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_july_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem86);

    jMenuItem87.setText("August");
    jMenuItem87.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_august_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem87);

    jMenuItem88.setText("September");
    jMenuItem88.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_september_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem88);

    jMenuItem89.setText("October");
    jMenuItem89.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_october_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem89);

    jMenuItem90.setText("November");
    jMenuItem90.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_november_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem90);

    jMenuItem91.setText("December");
    jMenuItem91.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SA_december_actionPerformed(evt);
          }
        });
    jMenu11.add(jMenuItem91);

    jMenu10.add(jMenu11);

    jMenu12.setText("Pilot charts NA (internet)");

    jMenuItem92.setText("January");
    jMenuItem92.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_january_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem92);

    jMenuItem93.setText("February");
    jMenuItem93.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_february_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem93);

    jMenuItem94.setText("March");
    jMenuItem94.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_march_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem94);

    jMenuItem95.setText("April");
    jMenuItem95.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_april_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem95);

    jMenuItem96.setText("May");
    jMenuItem96.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_may_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem96);

    jMenuItem97.setText("June");
    jMenuItem97.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_june_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem97);

    jMenuItem98.setText("July");
    jMenuItem98.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_july_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem98);

    jMenuItem99.setText("August");
    jMenuItem99.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_august_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem99);

    jMenuItem100.setText("September");
    jMenuItem100.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_september_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem100);

    jMenuItem101.setText("October");
    jMenuItem101.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_october_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem101);

    jMenuItem102.setText("November");
    jMenuItem102.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_november_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem102);

    jMenuItem103.setText("December");
    jMenuItem103.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NA_december_actionPerformed(evt);
          }
        });
    jMenu12.add(jMenuItem103);

    jMenu10.add(jMenu12);

    jMenu13.setText("Pilot charts SP (internet)");

    jMenuItem104.setText("January");
    jMenuItem104.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_january_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem104);

    jMenuItem105.setText("February");
    jMenuItem105.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_february_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem105);

    jMenuItem106.setText("March");
    jMenuItem106.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_march_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem106);

    jMenuItem107.setText("April");
    jMenuItem107.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_april_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem107);

    jMenuItem108.setText("May");
    jMenuItem108.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_may_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem108);

    jMenuItem109.setText("June");
    jMenuItem109.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_june_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem109);

    jMenuItem110.setText("July");
    jMenuItem110.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_july_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem110);

    jMenuItem111.setText("August");
    jMenuItem111.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_august_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem111);

    jMenuItem112.setText("September");
    jMenuItem112.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_september_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem112);

    jMenuItem113.setText("October");
    jMenuItem113.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_october_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem113);

    jMenuItem114.setText("November");
    jMenuItem114.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_november_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem114);

    jMenuItem115.setText("December");
    jMenuItem115.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_SP_december_actionPerformed(evt);
          }
        });
    jMenu13.add(jMenuItem115);

    jMenu10.add(jMenu13);

    jMenu14.setText("Pilot charts NP (internet)");

    jMenuItem116.setText("January");
    jMenuItem116.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_january_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem116);

    jMenuItem117.setText("February");
    jMenuItem117.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilots_charts_NP_february_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem117);

    jMenuItem118.setText("March");
    jMenuItem118.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_march_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem118);

    jMenuItem119.setText("April");
    jMenuItem119.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_april_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem119);

    jMenuItem120.setText("May");
    jMenuItem120.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_may_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem120);

    jMenuItem121.setText("June");
    jMenuItem121.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_june_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem121);

    jMenuItem122.setText("July");
    jMenuItem122.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_july_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem122);

    jMenuItem123.setText("August");
    jMenuItem123.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_august_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem123);

    jMenuItem124.setText("September");
    jMenuItem124.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_september_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem124);

    jMenuItem125.setText("October");
    jMenuItem125.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_october_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem125);

    jMenuItem126.setText("November");
    jMenuItem126.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_november_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem126);

    jMenuItem127.setText("December");
    jMenuItem127.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_NP_december_actionPerformed(evt);
          }
        });
    jMenu14.add(jMenuItem127);

    jMenu10.add(jMenu14);

    jMenu15.setText("Pilot charts Indian (internet)");

    jMenuItem128.setText("January");
    jMenuItem128.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_january_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem128);

    jMenuItem129.setText("February");
    jMenuItem129.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_february_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem129);

    jMenuItem130.setText("March");
    jMenuItem130.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_march_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem130);

    jMenuItem131.setText("April");
    jMenuItem131.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_april_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem131);

    jMenuItem132.setText("May");
    jMenuItem132.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_may_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem132);

    jMenuItem133.setText("June");
    jMenuItem133.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_june_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem133);

    jMenuItem134.setText("July");
    jMenuItem134.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_july_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem134);

    jMenuItem135.setText("August");
    jMenuItem135.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_august_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem135);

    jMenuItem136.setText("September");
    jMenuItem136.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_september_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem136);

    jMenuItem137.setText("October");
    jMenuItem137.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_october_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem137);

    jMenuItem138.setText("November");
    jMenuItem138.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_november_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem138);

    jMenuItem139.setText("December");
    jMenuItem139.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Maps_pilot_charts_IN_december_actionPerformed(evt);
          }
        });
    jMenu15.add(jMenuItem139);

    jMenu10.add(jMenu15);

    jMenuBar1.add(jMenu10);

    jMenu7.setText("Info");

    jMenuItem36.setText("Statistics (internet)");
    jMenuItem36.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_Statistics_menu_actionPerformed(evt);
          }
        });
    jMenu7.add(jMenuItem36);

    jMenuItem49.setText("Calculator...");
    jMenuItem49.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_Calculator_menu_actionPerformed(evt);
          }
        });
    jMenu7.add(jMenuItem49);

    jMenuItem75.setText("barometer comparison...");
    jMenuItem75.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_barometer_comparison_menu_actionPerformed(evt);
          }
        });
    jMenu7.add(jMenuItem75);
    jMenu7.add(jSeparator5);

    jMenuItem53.setText("System log");
    jMenuItem53.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_System_Log_menu_actionPerformed(evt);
          }
        });
    jMenu7.add(jMenuItem53);

    jMenuItem35.setText("send System logs");
    jMenuItem35.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_send_System_log_menu_actionperformed(evt);
          }
        });
    jMenu7.add(jMenuItem35);
    jMenu7.add(jSeparator17);

    jMenuItem74.setText("device log");
    jMenuItem74.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Info_device_log_menu_actionPerformed(evt);
          }
        });
    jMenu7.add(jMenuItem74);
    jMenu7.add(jSeparator6);

    jMenuItem37.setText("About...");
    jMenuItem37.addActionListener(
        new java.awt.event.ActionListener() {
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
        layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(
                jPanel1,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addGap(33, 33, 33)
                    .addGroup(
                        layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(
                                jPanel5,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addGroup(
                                layout
                                    .createSequentialGroup()
                                    .addComponent(
                                        jPanel4,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(
                                        jPanel3,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(
                                        jPanel2,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(72, 72, 72)));
    layout.setVerticalGroup(
        layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addComponent(
                        jPanel1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(
                        layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(
                                jPanel3,
                                javax.swing.GroupLayout.Alignment.LEADING,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                jPanel2,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                jPanel4,
                                javax.swing.GroupLayout.Alignment.LEADING,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        jPanel5,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                    .addContainerGap()));
  } // </editor-fold>//GEN-END:initComponents

  /**
   * @deprecated Use {@link DateTimeUtils#convert_month(int)} in new code.
   */
  @Deprecated
  public static String convert_month(int month_number) {
    return DateTimeUtils.convert_month(month_number);
  }

  public static void setUIFont(javax.swing.plaf.FontUIResource f) {
    SwingUiUtils.setUIFont(f);
  }

  public ImageIcon createImageIcon(String path_and_file) {
    return ImageUtils.createImageIcon(getClass(), path_and_file);
  }

  // The doInBackground method, which creates the image icon for the photograph, is invoked by the
  // background thread.
  // After the image icon is fully loaded, the done method is invoked on the event-dispatching
  // thread.
  // This updates the GUI to display the photograph

  // SwingWorker is only designed to be executed once. Executing a SwingWorker more than once will
  // not result in invoking the doInBackground method twice.
  // see: http://java.sun.com/javase/6/docs/api/javax/swing/SwingWorker.html
  private void loadImage(final String imagePath) {
    ImageLoadingWorkflow.start(this, imagePath);
  }

  void setToolbarIcon(String imagePath, ImageIcon icon) {
    ToolbarIconUpdater.update(
        imagePath, icon, jButton2, jButton3, jButton4, jButton5, jButton6, jButton7, jButton8,
        jButton9, jButton10, jButton11, jButton12, jButton13, jButton14, jButton15, jButton16,
        jButton17, jButton18, jButton19, jButton20);
  }

  private void loadImage_straight(final String imagePath) {
    ToolbarIconUpdater.update(
        imagePath,
        createImageIcon(imagePath),
        jButton2,
        jButton3,
        jButton4,
        jButton5,
        jButton6,
        jButton7,
        jButton8,
        jButton9,
        jButton10,
        jButton11,
        jButton12,
        jButton13,
        jButton14,
        jButton15,
        jButton16,
        jButton17,
        jButton18,
        jButton19,
        jButton20);
  }

  private void initImages() {
    ToolbarImageInitializationWorkflow.initialize(
        OSDetector.getOSString().equals("LINUX"), this::loadImage_straight, this::loadImage);
  }

  public static void check_and_set_datetime_v2() {
    DateTimeConfirmationWorkflow.checkAndSet();
  }

  void specific_connection_initComponents() {
    ConnectionInitializationWorkflow.initialize(
        theme_changed,
        RS232_connection_mode,
        RS232_connection_mode_II,
        RS232_GPS_connection_mode,
        RS232_RS422,
        jLabel39,
        jLabel40,
        jLabel41,
        jLabel6,
        jCheckBox1,
        jCheckBox2);
  }

  public static void disable_and_enable_output_menu_items() {
    OutputMenuStateUpdater.update();
  }

  static void disable_dashboard_and_maps_menu_items() {
    double width_screen = 0;
    double height_screen = 0;
    if (RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      width_screen = screenSize.getWidth();
      height_screen = screenSize.getHeight();
    }
    DashboardAndMapsMenuStateUpdater.update(
        RS232_connection_mode,
        APR,
        offline_mode,
        width_screen,
        height_screen,
        jMenuItem55,
        jMenuItem56,
        jMenuItem57,
        jMenuItem58,
        jMenuItem62,
        jMenuItem63,
        jMenuItem64,
        jMenuItem65,
        jMenuItem66,
        jMenuItem67,
        jMenuItem68,
        jMenuItem69,
        jMenuItem70,
        jMenuItem77);
  }

  public static void coded_obs_update() {
    CodedObservationFieldUpdater.update();
  }

  private void bepaal_frame_location() {
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    WindowLayoutCalculator.calculate(screenSize.width, screenSize.height);
    setLocation(x_pos_start_frame, y_pos_start_frame);
  }

  public static String compose_coded_obs(String SPATIE) {
    return ObservationComposer.compose(SPATIE);
  }

  public static void fill_configuratie_array() {
    ConfigurationManager.fillConfigurationLines();
  }

  public static void schrijf_configuratie_regels() {
    ConfigurationPersistenceWorkflow.write();
  }

  public void lees_configuratie_regels() {
    ConfigurationPersistenceWorkflow.read(this);
  }

  void disable_graph_menu_items() {
    GraphMenuStateUpdater.update(
        RS232_connection_mode,
        RS232_connection_mode_II,
        jMenuItem41,
        jMenuItem43,
        jMenuItem44,
        jMenuItem45,
        jMenuItem47,
        jMenuItem51);
  }

  public static void meta_data_from_configuration_regels_into_global_vars() {
    ConfigurationManager.applyConfigurationLines();
  }

  static void check_meta_data() {
    String info = ConfigurationValidator.findWarning();
    if (info.compareTo("") != 0) {
      JOptionPane.showMessageDialog(
          null, info, APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
    }
  }

  private void Output_Obs_to_server_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Output_Obs_to_server_menu_actionPerformed
    ServerObservationOutputWorkflow.start(this);
  } // GEN-LAST:event_Output_Obs_to_server_menu_actionPerformed

  private void Input_Wind_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Wind_menu_actionPerformed
    // TODO add your handling code here:
    // if (wind_form == null)
    // {
    //   wind_form = new mywind();
    //   wind_form.setSize(800, 600);
    // }
    // wind_form.setVisible(true);

    mywind form = new mywind();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Wind_menu_actionPerformed

  private void Input_Cloudcover_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Cloudcover_menu_actionPerformed
    // TODO add your handling code here:
    // if (cloudcover_form == null)
    // {
    //   cloudcover_form = new mycloudcover();
    //   cloudcover_form.setSize(800, 600);
    // }
    // cloudcover_form.setVisible(true);

    mycloudcover form = new mycloudcover();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Cloudcover_menu_actionPerformed

  private void Input_Presentweather_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Presentweather_menu_actionPerformed
    // TODO add your handling code here:
    // if (presentweather_form == null)
    // {
    //   presentweather_form = new mypresentweather();
    //   presentweather_form.setSize(800, 600);
    // }
    // presentweather_form.setVisible(true);

    mypresentweather form = new mypresentweather();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Presentweather_menu_actionPerformed

  private void Input_waves_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_waves_menu_actionPerformed
    // TODO add your handling code here:

    mywaves form = new mywaves();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_waves_menu_actionPerformed

  public static void observer_field_update() {
    ObserverFieldUpdater.update();
  }

  public static void temperatures_fields_update() {
    TemperatureFieldsUpdater.update();
  }

  public static void wind_fields_update() {
    WindFieldsUpdater.update();
  }

  public static void clouds_low_fields_update() {
    CloudFieldsUpdater.updateLow();
  }

  public static void clouds_middle_fields_update() {
    CloudFieldsUpdater.updateMiddle();
  }

  public static void clouds_high_fields_update() {
    CloudFieldsUpdater.updateHigh();
  }

  public static void cloud_cover_fields_update() {
    CloudFieldsUpdater.updateCover();
  }

  public static void visibility_fields_update() {
    VisibilityFieldsUpdater.update();
  }

  public static void waves_fields_update() {
    WavesFieldsUpdater.update();
  }

  public static void ID_fields_update() {
    IdentifierFieldsUpdater.update();
  }

  public static void date_time_fields_update() {
    DateTimeFieldsUpdater.update();
  }

  public static void position_fields_update() {
    PositionFieldsUpdater.update();
  }

  public static void present_weather_fields_update() {
    WeatherFieldsUpdater.updatePresent();
  }

  public static void past_weather_fields_update() {
    WeatherFieldsUpdater.updatePast();
  }

  public static void barometer_fields_update() {
    BarometerFieldsUpdater.update();
  }

  public static void barograph_fields_update() {
    BarographFieldsUpdater.update();
  }

  public static void icing_fields_update() {
    IcingFieldsUpdater.update();
  }

  public static void ice_fields_update() {
    IceFieldsUpdater.update();
  }

  private void Input_Cloudshigh_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Cloudshigh_menu_actionPerformed
    // TODO add your handling code here:
    // if (ch_form == null)
    // {
    //   ch_form = new mych();
    //   ch_form.setSize(800, 600);
    // }
    // ch_form.setVisible(true);

    mych form = new mych();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Cloudshigh_menu_actionPerformed

  private void Input_Position_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Position_menu_actionPerformed
    // TODO add your handling code here:

    // date time for leaflet Map plot
    leaflet_maps_obs_day = mydatetime.day; // for date-time on leaflet map
    leaflet_maps_obs_month = mydatetime.month; // for date-time on leaflet map
    leaflet_maps_obs_year = mydatetime.year; // for date-time on leaflet map
    leaflet_maps_obs_hour = mydatetime.hour; // for date-time on leaflet map

    // wind dir for leaflet Map plot
    if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE) {
      leaflet_maps_obs_wind_dir = "variable";
    } else if ((mywind.int_true_wind_dir != INVALID)
        && (mywind.int_true_wind_dir != mywind.WIND_DIR_VARIABLE)) {
      leaflet_maps_obs_wind_dir = Integer.toString(mywind.int_true_wind_dir) + " degr";
    } else {
      leaflet_maps_obs_wind_dir = "";
    }

    // wind speed for leaflet Maps plot
    if (mywind.int_true_wind_speed != INVALID) {
      if (main.wind_units.trim().indexOf(main.M_S) != -1) {
        leaflet_maps_obs_wind_speed = Integer.toString(mywind.int_true_wind_speed) + " m/s";
      } else // thus if wind speed units knots or wind speed units unknown
      {
        leaflet_maps_obs_wind_speed = Integer.toString(mywind.int_true_wind_speed) + " knots";
      }
    } else {
      leaflet_maps_obs_wind_speed = "";
    }

    // air temp for leaflet Map plot
    if ((mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null)) {
      // it is possible that there is only the figures eg 25 -> change to 25.0 C
      int pos = mytemp.air_temp.indexOf(".");
      if (pos == -1) // dus geen "." in de air temp string
      {
        leaflet_maps_obs_air_temp = mytemp.air_temp + ".0" + " &#176" + "C";
      } else {
        leaflet_maps_obs_air_temp = mytemp.air_temp + " &#176" + "C";
      }
    } else {
      leaflet_maps_obs_air_temp = "";
    }

    // SST for leaflet Map plot
    if ((mytemp.sea_water_temp.compareTo("") != 0) && (mytemp.sea_water_temp != null)) {
      // it is possible that there is only the figures eg 25 -> change to 25.0 C
      int pos = mytemp.sea_water_temp.indexOf(".");
      if (pos == -1) // dus geen "." in de air temp string
      {
        leaflet_maps_obs_sst = mytemp.sea_water_temp + ".0" + " &#176" + "C";
      } else {
        leaflet_maps_obs_sst = mytemp.sea_water_temp + " &#176" + "C";
      }
    } else {
      leaflet_maps_obs_sst = "";
    }

    // MSl pressure for leaflet Map plot
    if ((mybarometer.pressure_msl_corrected.compareTo("") != 0)
        && (mybarometer.pressure_msl_corrected != null)) {
      leaflet_maps_obs_msl_pressure = mybarometer.pressure_msl_corrected + " hPa";
    } else {
      leaflet_maps_obs_msl_pressure = "";
    }

    // if (position_form == null)
    // {
    //   position_form = new myposition();
    //   position_form.setSize(800, 600);
    // }
    // position_form.setVisible(true);

    myposition form = new myposition();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Position_menu_actionPerformed

  private void Input_DateTime_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_DateTime_menu_actionPerformed
    // TODO add your handling code here:

    // NB in serial connection mose (AWS or barometer connected) after an obs was send all
    // parameters will be set to blank,
    //    the "date & time obs" will be automatically updated/shown again (in case AWS every minute
    // and in case barometer every 5 minutes)
    //    but in "no serial connection mode" we have to ask the observer again

    if (main.RS232_connection_mode == 0) // no serial connection (no AWS or barometer connected)
    {
      // ask the observer if this is the correct UTC date and time of the observation (and if yes:
      // set accordingly)
      main.check_and_set_datetime_v2(); // use_system_date_time_for_updating will be set

      if (use_system_date_time_for_updating == false) {
        mydatetime form = new mydatetime();
        form.setSize(800, 600);
        form.setVisible(true);
      }
    } // if (main.RS232_connection_mode == 0)
    else {
      mydatetime form = new mydatetime();
      form.setSize(800, 600);
      form.setVisible(true);
    } // else
  } // GEN-LAST:event_Input_DateTime_menu_actionPerformed

  private void Input_Visibility_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Visibility_menu_actionPerformed
    // TODO add your handling code here:
    // if (visibility_form == null)
    // {
    //   visibility_form = new myvisibility();
    //   visibility_form.setSize(800, 600);
    // }
    // visibility_form.setVisible(true);
    myvisibility form = new myvisibility();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Visibility_menu_actionPerformed

  private void File_Exit_menu_actionPerformd(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_File_Exit_menu_actionPerformd
    // TODO add your handling code here:

    main_windowClosing(null);
  } // GEN-LAST:event_File_Exit_menu_actionPerformd

  private void Input_Pastweather_menu_actionperformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Pastweather_menu_actionperformed
    // TODO add your handling code here:
    // if (pastweather_form == null)
    // {
    //   pastweather_form = new mypastweather();
    //   pastweather_form.setSize(800, 600);
    // }
    // pastweather_form.setVisible(true);

    mypastweather form = new mypastweather();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Pastweather_menu_actionperformed

  private void Input_Cloudslow_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Cloudslow_menu_actionPerformed
    // TODO add your handling code here:
    // if (cl_form == null)
    // {
    //   cl_form = new mycl();
    //   cl_form.setSize(800, 600);
    // }
    // cl_form.setVisible(true);

    mycl form = new mycl();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Cloudslow_menu_actionPerformed

  private void Input_Cloudsmiddle_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Cloudsmiddle_menu_actionPerformed
    // TODO add your handling code here:
    // if (cm_form == null)
    // {
    //   cm_form = new mycm();
    //   cm_form.setSize(800, 600);
    // }
    // cm_form.setVisible(true);

    mycm form = new mycm();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Cloudsmiddle_menu_actionPerformed

  private void Input_Temperatures_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Temperatures_menu_actionPerformed
    // TODO add your handling code here:
    // if (temp_form == null)
    // {
    //   temp_form = new mytemp();
    //   temp_form.setSize(800, 600);
    // }
    // temp_form.setVisible(true);

    mytemp form = new mytemp();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Temperatures_menu_actionPerformed

  private void Maintenance_Stationdata_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maintenance_Stationdata_actionPerformed
    // TODO add your handling code here:

    mode = STATION_DATA;

    if (main_support.password_ok) // no password needed, direct to the station data form
    {
      // open station data input page
      mystationdata form = new mystationdata();
      form.setSize(800, 600);
      form.setVisible(true);
    } else // via the password input screen to the station data input form
    {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_Stationdata_actionPerformed

  private void Input_Barometer_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Barometer_menu_actionPerformed
    // TODO add your handling code here:
    // if (barometer_form == null)
    // {
    //   barometer_form = new mybarometer();
    //   barometer_form.setSize(800, 600);
    // }
    // barometer_form.setVisible(true);

    mybarometer form = new mybarometer();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Barometer_menu_actionPerformed

  private void Input_Barograph_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Barograph_menu_actionPerformed
    // TODO add your handling code here:
    // if (barograph_form == null)
    // {
    //   barograph_form = new mybarograph();
    //   barograph_form.setSize(800, 600);
    // }
    // barograph_form.setVisible(true);

    mybarograph form = new mybarograph();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Barograph_menu_actionPerformed

  private void Info_About_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Info_About_menu_actionPerformed
    // TODO add your handling code here:
    about form = new about();
    form.setSize(600, 700);
    form.setVisible(true);
  } // GEN-LAST:event_Info_About_menu_actionPerformed

  private void Output_obs_by_email_all_manual() {
    ObservationEmailManualWorkflow.start(this);
  }

  private void Output_obs_to_file_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Output_obs_to_file_actionPerformed
    ObservationFileOutputWorkflow.start(this);
  } // GEN-LAST:event_Output_obs_to_file_actionPerformed

  private void Maintenance_Email_settings_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maintenance_Email_settings_actionPerformed
    // TODO add your handling code here:

    mode = EMAIL_SETTINGS;

    if (main_support.password_ok) // no password needed, direct to the email settings form
    {
      myemailsettings form = new myemailsettings();
      form.setSize(1000, 700);
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_Email_settings_actionPerformed

  private void Maintenance_Log_files_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maintenance_Log_files_actionPerformed
    // TODO add your handling code here:

    mode = LOG_FILES;

    if (main_support.password_ok) // no password needed, direct to the log files settings form
    {
      mylogfiles form = new mylogfiles();
      form.setSize(800, 600);
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_Log_files_actionPerformed

  private void Input_Observer_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Observer_menu_actionPerformed
    // TODO add your handling code here:
    // if (observer_form == null)
    // {
    //   observer_form = new myobserver();
    //   observer_form.setSize(800, 600);
    // }
    // observer_form.setVisible(true);

    myobserver form = new myobserver();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Observer_menu_actionPerformed

  private void Maintenance_Move_log_files_to_disk_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maintenance_Move_log_files_to_disk_actionPerformed
    LogFilesDiskWorkflow.start();
  } // GEN-LAST:event_Maintenance_Move_log_files_to_disk_actionPerformed

  private void Maintenance_Observer_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maintenance_Observer_menu_actionPerformed
    // TODO add your handling code here:
    Input_Observer_menu_actionPerformed(evt);
  } // GEN-LAST:event_Maintenance_Observer_menu_actionPerformed

  private void Maintenance_Captains_Menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maintenance_Captains_Menu_actionPerformed
    // TODO add your handling code here:
    // if (captain_form == null)
    // {
    //   captain_form = new mycaptain();
    //   captain_form.setSize(800, 600);
    // }
    // captain_form.setVisible(true);

    mycaptain form = new mycaptain();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Maintenance_Captains_Menu_actionPerformed

  private void Maintenance_Move_log_files_by_email_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maintenance_Move_log_files_by_email_actionPerformed
    LogFilesEmailWorkflow.start();
  } // GEN-LAST:event_Maintenance_Move_log_files_by_email_actionPerformed

  private void Next_form_automation_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Next_form_automation_menu_actionPerformed
    // TODO add your handling code here:

    /* initialisation */
    in_next_sequence = true;

    /* starting with the position data input screen */
    Input_Position_menu_actionPerformed(evt);

    // int seq_no_input_screen =5;

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

    // while (/*stop_in_next_sequence == false &&*/ (seq_no_input_screen <= 15) &&
    // (seq_no_input_screen >= 1))
    // {
    // if (seq_no_input_screen == 5)
    // {
    //  Input_Barometer_menu_actionPerformed(evt);
    //  seq_no_input_screen++;
    // }

    // if ((seq_no_input_screen == 6) /*&& (barometer_form_active == false)*/)
    // {
    //   Input_Barograph_menu_actionPerformed(evt);

    // }

    // seq_no_input_screen++;
    // }

  } // GEN-LAST:event_Next_form_automation_menu_actionPerformed

  private void date_time_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_date_time_mainscreen_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)                          // NOT EUCOS AWS
    // {
    Input_DateTime_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_date_time_mainscreen_mouseClicked

  private void position_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_position_mainscreen_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Position_menu_actionPerformed(null);
    // }
    // NB zoals onderstaande kan het ook
    //   myposition form = new myposition();
    //   form.setSize(800, 600);
    //   form.setVisible(true);
  } // GEN-LAST:event_position_mainscreen_mouseClicked

  private void course_speed_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_course_speed_mainscreen_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Position_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_course_speed_mainscreen_mouseClicked

  private void pressure_read_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_pressure_read_mainscreen_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Barometer_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_pressure_read_mainscreen_mouseClicked

  private void pressure_msl_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_pressure_msl_mainscreen_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Barometer_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_pressure_msl_mainscreen_mouseClicked

  private void amount_pressure_tendency_mainscreen_mouseClicked(
      java.awt.event.MouseEvent
          evt) { // GEN-FIRST:event_amount_pressure_tendency_mainscreen_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Barograph_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_amount_pressure_tendency_mainscreen_mouseClicked

  private void char_pressure_tendency_mainscreen_mouseClicked(
      java.awt.event.MouseEvent
          evt) { // GEN-FIRST:event_char_pressure_tendency_mainscreen_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Barograph_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_char_pressure_tendency_mainscreen_mouseClicked

  private void present_weather_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_present_weather_mainscreen_mouseClicked
    // TODO add your handling code here:

    // NB in light mode Label13 (present weather) will be reused for the logo (eumetnet, noaa, sot)
    //    so no action if Label13 was become a label
    // if (!GUI_mode.equals(GUI_LIGHT))
    // {
    //   Input_Presentweather_menu_actionPerformed(null);
    // }

    Input_Presentweather_menu_actionPerformed(null);
  } // GEN-LAST:event_present_weather_mainscreen_mouseClicked

  private void past_weather_1_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_past_weather_1_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Pastweather_menu_actionperformed(null);
  } // GEN-LAST:event_past_weather_1_mainscreen_mouseClicked

  private void past_weather_2_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_past_weather_2_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Pastweather_menu_actionperformed(null);
  } // GEN-LAST:event_past_weather_2_mainscreen_mouseClicked

  private void true_wind_speed_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_true_wind_speed_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Wind_menu_actionPerformed(null);
  } // GEN-LAST:event_true_wind_speed_mainscreen_mouseClicked

  private void true_wind_dir_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_true_wind_dir_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Wind_menu_actionPerformed(null);
  } // GEN-LAST:event_true_wind_dir_mainscreen_mouseClicked

  private void visibility_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_visibility_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Visibility_menu_actionPerformed(null);
  } // GEN-LAST:event_visibility_mainscreen_mouseClicked

  private void observer_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_observer_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Observer_menu_actionPerformed(null);
  } // GEN-LAST:event_observer_mainscreen_mouseClicked

  private void wind_wave_height_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_wind_wave_height_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_wind_wave_height_mainscreen_mouseClicked

  private void wind_wave_period_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_wind_wave_period_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_wind_wave_period_mainscreen_mouseClicked

  private void swell_1_dir_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_swell_1_dir_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_swell_1_dir_mainscreen_mouseClicked

  private void swell_1_height_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_swell_1_height_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_swell_1_height_mainscreen_mouseClicked

  private void swell_1_period_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_swell_1_period_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_swell_1_period_mainscreen_mouseClicked

  private void swell_2_dir_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_swell_2_dir_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_swell_2_dir_mainscreen_mouseClicked

  private void swell_2_height_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_swell_2_height_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_swell_2_height_mainscreen_mouseClicked

  private void swell_2_period_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_swell_2_period_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_swell_2_period_mainscreen_mouseClicked

  private void total_cloud_cover_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_total_cloud_cover_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Cloudcover_menu_actionPerformed(null);
  } // GEN-LAST:event_total_cloud_cover_mainscreen_mouseClicked

  private void amount_cl_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_amount_cl_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Cloudcover_menu_actionPerformed(null);
  } // GEN-LAST:event_amount_cl_mainscreen_mouseClicked

  private void height_lowest_cloud_mainscreen_mouseClicked(
      java.awt.event.MouseEvent
          evt) { // GEN-FIRST:event_height_lowest_cloud_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Cloudcover_menu_actionPerformed(null);
  } // GEN-LAST:event_height_lowest_cloud_mainscreen_mouseClicked

  private void cl_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_cl_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Cloudslow_menu_actionPerformed(null);
  } // GEN-LAST:event_cl_mainscreen_mouseClicked

  private void cm_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_cm_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Cloudsmiddle_menu_actionPerformed(null);
  } // GEN-LAST:event_cm_mainscreen_mouseClicked

  private void ch_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_ch_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Cloudshigh_menu_actionPerformed(null);
  } // GEN-LAST:event_ch_mainscreen_mouseClicked

  private void air_temp_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_air_temp_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Temperatures_menu_actionPerformed(null);
  } // GEN-LAST:event_air_temp_mainscreen_mouseClicked

  private void wet_bulb_temp_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_wet_bulb_temp_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Temperatures_menu_actionPerformed(null);
  } // GEN-LAST:event_wet_bulb_temp_mainscreen_mouseClicked

  private void dew_point_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_dew_point_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Temperatures_menu_actionPerformed(null);
  } // GEN-LAST:event_dew_point_mainscreen_mouseClicked

  private void ship_name_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_ship_name_mainscreen_mouseClicked
    // TODO add your handling code here:

    Maintenance_Stationdata_actionPerformed(null);
  } // GEN-LAST:event_ship_name_mainscreen_mouseClicked

  private void station_ID_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_station_ID_mainscreen_mouseClicked
    // TODO add your handling code here:

    Maintenance_Stationdata_actionPerformed(null);
  } // GEN-LAST:event_station_ID_mainscreen_mouseClicked

  private void date_time_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_date_time_toolbar_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)                          // NOT EUCOS AWS
    // {
    Input_DateTime_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_date_time_toolbar_mouseClicked

  private void position_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_position_toolbar_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Position_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_position_toolbar_mouseClicked

  private void wind_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_wind_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Wind_menu_actionPerformed(null);
  } // GEN-LAST:event_wind_toolbar_mouseClicked

  private void waves_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_waves_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_waves_menu_actionPerformed(null);
  } // GEN-LAST:event_waves_toolbar_mouseClicked

  private void barometer_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_barometer_toolbar_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Barometer_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_barometer_toolbar_mouseClicked

  private void barograph_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_barograph_toolbar_mouseClicked
    // TODO add your handling code here:

    // if (RS232_connection_mode != 3)
    // {
    Input_Barograph_menu_actionPerformed(null);
    // }
  } // GEN-LAST:event_barograph_toolbar_mouseClicked

  private void temperatures_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_temperatures_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Temperatures_menu_actionPerformed(null);
  } // GEN-LAST:event_temperatures_toolbar_mouseClicked

  private void present_weather_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_present_weather_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Presentweather_menu_actionPerformed(null);
  } // GEN-LAST:event_present_weather_toolbar_mouseClicked

  private void past_weather_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_past_weather_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Pastweather_menu_actionperformed(null);
  } // GEN-LAST:event_past_weather_toolbar_mouseClicked

  private void visibility_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_visibility_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Visibility_menu_actionPerformed(null);
  } // GEN-LAST:event_visibility_toolbar_mouseClicked

  private void cl_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_cl_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Cloudslow_menu_actionPerformed(null);
  } // GEN-LAST:event_cl_toolbar_mouseClicked

  private void cm_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_cm_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Cloudsmiddle_menu_actionPerformed(null);
  } // GEN-LAST:event_cm_toolbar_mouseClicked

  private void ch_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_ch_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Cloudshigh_menu_actionPerformed(null);
  } // GEN-LAST:event_ch_toolbar_mouseClicked

  private void height_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_height_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Cloudcover_menu_actionPerformed(null);
  } // GEN-LAST:event_height_toolbar_mouseClicked

  private void icing_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_icing_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Icing_menu_actionPerformed(null);
  } // GEN-LAST:event_icing_toolbar_mouseClicked

  private void ice_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_ice_toolbar_mouseClicked
    // TODO add your handling code here:
    Input_Ice_menu_actionPerformed(null);
  } // GEN-LAST:event_ice_toolbar_mouseClicked

  private void observer_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_observer_toolbar_mouseClicked
    // TODO add your handling code here:

    Input_Observer_menu_actionPerformed(null);
  } // GEN-LAST:event_observer_toolbar_mouseClicked

  private void Themes_1_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Themes_1_actionPerformed
    // Nimbus default color values: https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html
    ThemeWorkflow.apply(
        this,
        THEME_NIMBUS_DAY,
        new Color(214, 217, 223),
        new Color(51, 98, 140),
        new Color(115, 164, 209),
        new Color(255, 255, 255),
        new Color(0, 0, 0),
        new Color(169, 176, 190),
        new Color(204, 255, 255));
  } // GEN-LAST:event_Themes_1_actionPerformed

  private void Themes_2_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Themes_2_actionPerformed
    // Night colors (Nimbus based).
    ThemeWorkflow.apply(
        this,
        THEME_NIMBUS_NIGHT,
        new Color(114, 114, 114),
        new Color(64, 64, 64),
        new Color(191, 191, 191),
        new Color(176, 176, 176),
        new Color(0, 0, 0),
        new Color(169, 176, 190),
        new Color(192, 192, 192));
  } // GEN-LAST:event_Themes_2_actionPerformed

  private void Themes_3_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Themes_3_actionPerformed
    // Sunrise is a separate color scheme rather than a normal Nimbus theme.
    ThemeWorkflow.apply(
        this,
        THEME_NIMBUS_SUNRISE,
        new Color(255, 178, 102),
        new Color(51, 98, 140),
        new Color(115, 164, 209),
        new Color(255, 204, 153),
        new Color(0, 0, 0),
        new Color(169, 176, 190),
        new Color(255, 255, 132));
  } // GEN-LAST:event_Themes_3_actionPerformed

  private void Input_Icing_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Icing_menu_actionPerformed
    // TODO add your handling code here:

    myicing form = new myicing();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Icing_menu_actionPerformed

  private void icing_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_icing_mainscreen_mouseClicked
    // TODO add your handling code here:

    Input_Icing_menu_actionPerformed(null);
  } // GEN-LAST:event_icing_mainscreen_mouseClicked

  private void Input_Ice_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Input_Ice_menu_actionPerformed
    // TODO add your handling code here:

    myice1 form = new myice1();
    form.setSize(800, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Input_Ice_menu_actionPerformed

  private void ice_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_ice_mainscreen_mouseClicked
    // TODO add your handling code here:

    Input_Ice_menu_actionPerformed(null);
  } // GEN-LAST:event_ice_mainscreen_mouseClicked

  private void captain_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_captain_toolbar_mouseClicked
    // TODO add your handling code here:

    Maintenance_Captains_Menu_actionPerformed(null);
  } // GEN-LAST:event_captain_toolbar_mouseClicked

  private void next_screen_toolbar_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_next_screen_toolbar_mouseClicked
    // TODO add your handling code here:

    /* initialisation */
    in_next_sequence = true;

    /* starting with the position data input screen */
    Input_Position_menu_actionPerformed(null);
  } // GEN-LAST:event_next_screen_toolbar_mouseClicked

  private void Info_Statistics_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Info_Statistics_menu_actionPerformed
    StatisticsLinkWorkflow.start();
  } // GEN-LAST:event_Info_Statistics_menu_actionPerformed

  private void Amver_SailingPlan_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Amver_SailingPlan_actionPerformed
    AmverReportWorkflow.open(AMVER_SP, 1000, 750);
  } // GEN-LAST:event_Amver_SailingPlan_actionPerformed

  private void Amver_DeviationReport_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Amver_DeviationReport_actionPerformed
    AmverReportWorkflow.open(AMVER_DR, 1000, 700);
  } // GEN-LAST:event_Amver_DeviationReport_actionPerformed

  private void Amver_ArrivalReport_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Amver_ArrivalReport_actionPerformed
    AmverReportWorkflow.open(AMVER_FR, 1000, 750);
  } // GEN-LAST:event_Amver_ArrivalReport_actionPerformed

  private void Amver_PositionReport_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Amver_PositionReport_actionPerformed
    AmverReportWorkflow.open(AMVER_PR, 1000, 700);
  } // GEN-LAST:event_Amver_PositionReport_actionPerformed

  void Graphs_Pressure_Sensor_Data_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Graphs_Pressure_Sensor_Data_actionPerformed
    // TODO add your handling code here:

    if (graph_form != null) {
      if (sensor_data_file_ophalen_timer_is_gecreeerd == true) // 15-05-2013
      {
        if (RS232_view.sensor_data_file_ophalen_timer.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer = null;
      sensor_data_file_ophalen_timer_is_gecreeerd = false;

      if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true) {
        if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer_II.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer_II = null;
      sensor_data_file_ophalen_timer_is_gecreeerd_II = false;

      // graph_form.dispose();
      graph_form.setVisible(false);
    }

    mode_grafiek = MODE_PRESSURE;

    graph_form = new RS232_view();
    // graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full
    // screen
    graph_form.setExtendedState(MAXIMIZED_BOTH);
    graph_form.setVisible(true);
  } // GEN-LAST:event_Graphs_Pressure_Sensor_Data_actionPerformed

  private void Maintenance_Serial_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maintenance_Serial_actionPerformed
    // TODO add your handling code here:

    mode = SERIAL_CONNECTION;

    if (main_support.password_ok) // no password needed, direct to the connections settings form
    {
      RS232_settings form = new RS232_settings();
      form.setSize(800, 600);
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_Serial_actionPerformed

  void main_windowClosing(java.awt.event.WindowEvent evt) { // GEN-FIRST:event_main_windowClosing
    // TODO add your handling code here:

    // log memory statistics
    support_class.log_memory_statistics();

    String info = "Are you sure you want to exit this application?";
    // if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) || (RS232_connection_mode
    // == 3) || (RS232_connection_mode == 4))  && (defaultPort != null) )
    if (((RS232_connection_mode != 0) && (defaultPort != null))
        || (RS232_connection_mode == 6)
        || ((RS232_connection_mode_II != 0) && (defaultPort_II != null))) {
      // PTB220, PTB330, EUCAWS, OMC-140, MintakaDuo, Mintaka Star USB, HMP155 (all via serial comm)
      // or Mintaka Star WiFi connected
      info +=
          "\n\n ("
              + APPLICATION_NAME
              + " will stop with monitoring and collecting of the sensor data)";
      if (main.APR == true || main.WOW == true) {
        info += "\n (" + APPLICATION_NAME + " will stop with automated reports upload)";
        info += "\n (minimise " + APPLICATION_NAME + " instead of closing)";
      }
    }
    int result =
        JOptionPane.showConfirmDialog(
            main.this, info, "Exit " + APPLICATION_NAME, JOptionPane.YES_NO_OPTION);

    if (result == JOptionPane.YES_OPTION) {
      // Remember to remove the listener (for checking only once instance running) before your
      // application exits (see Function initComponents2()) [nb only in jnlp mode]
      // if (sisL != null)
      // {
      //   sis.removeSingleInstanceListener(sisL);
      // }

      // serial communication barometer (not neccessary for WiFi barometer)
      // if ( ((RS232_connection_mode == 1) || (RS232_connection_mode == 2) ||
      // (RS232_connection_mode == 3) || (RS232_connection_mode == 4)) && (defaultPort != null) )
      if ((RS232_connection_mode != 0) && (defaultPort != null)) {
        //
        // NB RxTx Serial ?
        // close() for Linux for a proper clean up of a port stale lock file (e.g.
        // /var/lock/LCK...ttyUSB0), not appropiate to Windows
        // unfortunately it didn't help (see also:
        // http://www.raspberrypi.org/forums/viewtopic.php?f=81&t=32186)
        //
        // The problem with file lock is likely due to the lock-file being created with root
        // permissions (sudo),
        // however the IDE is being ran under user mode. Either you will have to make the lock file
        // create in user-mode,
        // change the ownership of the lock, or run the IDE in root. Running it in root shouldn't be
        // a big issue.
        //
        //
        // NB in case of WiFi: defaultport == null
        //
        //
        if (main.serialPort != null) {
          // try
          // {
          main.serialPort.removeDataListener();
          main.serialPort.closePort();
          main.serialPort = null;
          // }
          // catch (SerialPortException ex)
          // {
          //   System.out.println(ex);
          // }
        } // if (main.serialPort != null)
      } // if ((RS232_connection_mode != 0) && (defaultPort != null))

      // serial communication thermometer
      if ((RS232_connection_mode_II != 0) && (defaultPort_II != null)) {
        //
        // NB in case of WiFi: defaultport_II == null
        //
        if (main.serialPort_II != null) {
          main.serialPort_II.removeDataListener();
          main.serialPort_II.closePort();
          main.serialPort_II = null;
        } // if (main.serialPort_II != null)
      } // if ((RS232_connection_mode_II != 0) && (defaultPort_II != null))

      // serial communication GPS
      if ((RS232_GPS_connection_mode != 0) && (main_RS232_RS422.GPS_defaultPort != null)) {
        // only necessary in case of RxTx serial?
        // if (main_RS232_RS422.GPS_serialPort != null)
        if (main.GPS_serialPort != null) {
          // try
          // {
          main.GPS_serialPort.removeDataListener();
          main.GPS_serialPort.closePort();
          main.GPS_serialPort = null;
          // }
          // catch (SerialPortException ex)
          // {
          //   System.out.println(ex);
          // }
        } // if (main_RS232_RS422.GPS_serialPort != null)
      } // if ((RS232_GPS_connection_mode != 0) && (main_RS232_RS422.GPS_defaultPort != null))

      // if the program was minimized remove the trayIcon
      if ((main.ICONIFIED & this.getExtendedState()) == main.ICONIFIED) {
        tray.remove(trayIcon);
      }

      // TurboWin+ stopped message to log
      log_turbowin_system_message(
          "[GENERAL] stopped "
              + APPLICATION_NAME
              + " "
              + application_mode
              + " "
              + TurboWinAppInfo.APPLICATION_VERSION);

      // exit
      System.exit(0);
    }
  } // GEN-LAST:event_main_windowClosing

  void Graphs_Airtemp_Sensor_Data_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Graphs_Airtemp_Sensor_Data_actionPerformed
    // TODO add your handling code here:
    if (graph_form != null) {
      if (sensor_data_file_ophalen_timer_is_gecreeerd == true) // 15-05-2013
      {
        if (RS232_view.sensor_data_file_ophalen_timer.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer = null;
      sensor_data_file_ophalen_timer_is_gecreeerd = false;

      if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true) {
        if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer_II.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer_II = null;
      sensor_data_file_ophalen_timer_is_gecreeerd_II = false;

      // graph_form.dispose();
      graph_form.setVisible(false);
    }

    if (RS232_connection_mode_II == 1) {
      mode_grafiek = MODE_AIRTEMP_II;
    } else {
      mode_grafiek = MODE_AIRTEMP;
    }

    graph_form = new RS232_view();
    // graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full
    // screen
    graph_form.setExtendedState(MAXIMIZED_BOTH);
    graph_form.setVisible(true);
  } // GEN-LAST:event_Graphs_Airtemp_Sensor_Data_actionPerformed

  void Graphs_SST_Sensor_data_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Graphs_SST_Sensor_data_actionPerformed
    // TODO add your handling code here:
    if (graph_form != null) {
      if (sensor_data_file_ophalen_timer_is_gecreeerd == true) // 15-05-2013
      {
        if (RS232_view.sensor_data_file_ophalen_timer.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer = null;
      sensor_data_file_ophalen_timer_is_gecreeerd = false;

      if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true) {
        if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer_II.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer_II = null;
      sensor_data_file_ophalen_timer_is_gecreeerd_II = false;

      // graph_form.dispose();
      graph_form.setVisible(false);
    }

    mode_grafiek = MODE_SST;

    graph_form = new RS232_view();
    // graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full
    // screen
    graph_form.setExtendedState(MAXIMIZED_BOTH);
    graph_form.setVisible(true);
  } // GEN-LAST:event_Graphs_SST_Sensor_data_actionPerformed

  void Graphs_Wind_Speed_Sensor_Data_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Graphs_Wind_Speed_Sensor_Data_actionPerformed
    // TODO add your handling code here:
    if (graph_form != null) {
      if (sensor_data_file_ophalen_timer_is_gecreeerd == true) // 15-05-2013
      {
        if (RS232_view.sensor_data_file_ophalen_timer.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer = null;
      sensor_data_file_ophalen_timer_is_gecreeerd = false;

      if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true) {
        if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer_II.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer_II = null;
      sensor_data_file_ophalen_timer_is_gecreeerd_II = false;

      // graph_form.dispose();
      graph_form.setVisible(false);
    }
    mode_grafiek = MODE_WIND_SPEED;

    graph_form = new RS232_view();
    // graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full
    // screen
    graph_form.setExtendedState(MAXIMIZED_BOTH);
    graph_form.setVisible(true);
  } // GEN-LAST:event_Graphs_Wind_Speed_Sensor_Data_actionPerformed

  /*
     private void Output_obs_to_AWS_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Output_obs_to_AWS_actionPerformed
        // TODO add your handling code here:

     }//GEN-LAST:event_Output_obs_to_AWS_actionPerformed
  */

  private void Output_obs_to_AWS_actionPerformed(java.awt.event.ActionEvent evt) {
    AwsUploadWorkflow.start(this);
  }

  void IMMT_AWS_manual_input_preperations() {
    AwsManualInputPreparationWorkflow.prepare();
  }

  void Graph_Wind_Dir_Sensor_Data_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Graph_Wind_Dir_Sensor_Data_actionPerformed
    // TODO add your handling code here:
    if (graph_form != null) {
      if (sensor_data_file_ophalen_timer_is_gecreeerd == true) {
        if (RS232_view.sensor_data_file_ophalen_timer.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer = null;
      sensor_data_file_ophalen_timer_is_gecreeerd = false;

      if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true) {
        if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer_II.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer_II = null;
      sensor_data_file_ophalen_timer_is_gecreeerd_II = false;

      // graph_form.dispose();
      graph_form.setVisible(false);
    }
    mode_grafiek = MODE_WIND_DIR;

    graph_form = new RS232_view();
    // graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full
    // screen
    graph_form.setExtendedState(MAXIMIZED_BOTH);
    graph_form.setVisible(true);
  } // GEN-LAST:event_Graph_Wind_Dir_Sensor_Data_actionPerformed

  private void Output_obs_to_clipboard_actionPerformed(java.awt.event.ActionEvent evt) {
    ObservationClipboardWorkflow.start(this);
  }

  private void main_windowIconfied(
      java.awt.event.WindowEvent evt) { // GEN-FIRST:event_main_windowIconfied
    SystemTrayWorkflow.handle(this);
  } // GEN-LAST:event_main_windowIconfied

  private void Info_Calculator_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Info_Calculator_menu_actionPerformed
    // TODO add your handling code here:

    calculator form = new calculator();
    form.setSize(350, 600);
    form.setVisible(true);
  } // GEN-LAST:event_Info_Calculator_menu_actionPerformed

  private void Maintenance_obs_format_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maintenance_obs_format_actionPerformed
    // TODO add your handling code here:
    mode = SET_OBS_FORMAT;

    if (main_support.password_ok) // no password needed, direct to the obs format settings form
    {
      myobsformat form = new myobsformat();
      form.setSize(800, 600);
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_obs_format_actionPerformed

  private void seawater_temp_mainscreen_mouseClicked(
      java.awt.event.MouseEvent evt) { // GEN-FIRST:event_seawater_temp_mainscreen_mouseClicked
    // TODO add your handling code here:
    Input_Temperatures_menu_actionPerformed(null);
  } // GEN-LAST:event_seawater_temp_mainscreen_mouseClicked

  void Graph_All_Sensor_Data_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Graph_All_Sensor_Data_actionPerformed
    // TODO add your handling code here:

    if (graph_form != null) {
      if (sensor_data_file_ophalen_timer_is_gecreeerd == true) // 15-05-2013
      {
        if (RS232_view.sensor_data_file_ophalen_timer.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer = null;

      sensor_data_file_ophalen_timer_is_gecreeerd = false;

      if (sensor_data_file_ophalen_timer_is_gecreeerd_II == true) // 15-05-2013
      {
        if (RS232_view.sensor_data_file_ophalen_timer_II.isRunning()) {
          RS232_view.sensor_data_file_ophalen_timer_II.stop();
        }
      }
      RS232_view.sensor_data_file_ophalen_timer_II = null;

      sensor_data_file_ophalen_timer_is_gecreeerd_II = false;

      // graph_form.dispose();
      graph_form.setVisible(false);
    }

    mode_grafiek = MODE_ALL_PARAMETERS;

    graph_form = new RS232_view();
    // graph_form.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());       // full
    // screen
    graph_form.setExtendedState(MAXIMIZED_BOTH);
    graph_form.setVisible(true);
  } // GEN-LAST:event_Graph_All_Sensor_Data_actionPerformed

  private void Maintenance_WOW_settings_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maintenance_WOW_settings_actionPerformed
    // TODO add your handling code here:

    mode = SET_WOW_APR_SETTINGS;

    if (main_support.password_ok) // no password needed, direct to the APR settings form
    {
      WOW_APR_settings form = new WOW_APR_settings();
      form.setSize(1000, 700);
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_WOW_settings_actionPerformed

  private void Info_System_Log_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Info_System_Log_menu_actionPerformed
    // TODO add your handling code here:

    mysystem_log form = new mysystem_log();
    form.setExtendedState(MAXIMIZED_BOTH); // full screen
    form.setVisible(true);
  } // GEN-LAST:event_Info_System_Log_menu_actionPerformed

  private void Maintenance_server_settings_actionperformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maintenance_server_settings_actionperformed
    // TODO add your handling code here:

    mode = SET_SERVER_SETTINGS;

    if (main_support.password_ok) // no password needed, direct to the server settings form
    {
      myserversettings form = new myserversettings();
      form.setSize(1000, 700);
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_server_settings_actionperformed

  private void Themes_4_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Themes_4_actionPerformed
    // Sunset is a separate color scheme rather than a normal Nimbus theme.
    ThemeWorkflow.apply(
        this,
        THEME_NIMBUS_SUNSET,
        new Color(255, 110, 110),
        new Color(51, 98, 140),
        new Color(115, 164, 209),
        new Color(255, 204, 204),
        new Color(0, 0, 0),
        new Color(169, 176, 190),
        new Color(255, 51, 51));
  } // GEN-LAST:event_Themes_4_actionPerformed

  private void Info_send_System_log_menu_actionperformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Info_send_System_log_menu_actionperformed
    SystemLogEmailWorkflow.start();
  } // GEN-LAST:event_Info_send_System_log_menu_actionperformed

  void Dashboard_Barometer_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Dashboard_Barometer_actionPerformed
    // TODO add your handling code here:

    if (dashboard_form != null) {
      if (DASHBOARD_view.dashboard_update_timer_is_gecreeerd == true) {
        if (DASHBOARD_view.dashboard_update_timer.isRunning()) {
          DASHBOARD_view.dashboard_update_timer.stop();
        }
      }
      DASHBOARD_view.dashboard_update_timer = null;

      DASHBOARD_view.dashboard_update_timer_is_gecreeerd = false;

      // graph_form.dispose();
      dashboard_form.setVisible(false);
    }

    dashboard_form = new DASHBOARD_view();
    dashboard_form.setExtendedState(MAXIMIZED_BOTH);
    dashboard_form.setVisible(true);
  } // GEN-LAST:event_Dashboard_Barometer_actionPerformed

  void Dashboard_AWS_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Dashboard_AWS_actionPerformed
    // TODO add your handling code here:

    if (dashboard_form_AWS != null) {
      if (DASHBOARD_view_AWS.dashboard_update_AWS_timer_is_gecreeerd == true) {
        if (DASHBOARD_view_AWS.dashboard_update_AWS_timer.isRunning()) {
          DASHBOARD_view_AWS.dashboard_update_AWS_timer.stop();
        }
      }
      DASHBOARD_view_AWS.dashboard_update_AWS_timer = null;

      DASHBOARD_view_AWS.dashboard_update_AWS_timer_is_gecreeerd = false;

      // graph_form.dispose();
      dashboard_form_AWS.setVisible(false);
    }

    dashboard_form_AWS = new DASHBOARD_view_AWS();
    dashboard_form_AWS.setExtendedState(MAXIMIZED_BOTH);
    dashboard_form_AWS.setVisible(true);
  } // GEN-LAST:event_Dashboard_AWS_actionPerformed

  void Dashboard_AWS_digital_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Dashboard_AWS_digital_actionPerformed
    // TODO add your handling code here:

    if (dashboard_form_AWS_digital != null) {
      if (DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer_is_gecreeerd == true) {
        if (DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer.isRunning()) {
          DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer.stop();
        }
      }
      DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer = null;

      DASHBOARD_view_AWS_digital.dashboard_update_AWS_digital_timer_is_gecreeerd = false;

      // graph_form.dispose();
      dashboard_form_AWS_digital.setVisible(false);
    }

    dashboard_form_AWS_digital = new DASHBOARD_view_AWS_digital();
    dashboard_form_AWS_digital.setExtendedState(MAXIMIZED_BOTH);
    dashboard_form_AWS_digital.setVisible(true);
  } // GEN-LAST:event_Dashboard_AWS_digital_actionPerformed

  private void Dashboard_Latest_Obs_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Dashboard_Latest_Obs_actionPerformed
    // TODO add your handling code here:

    DASHBOARD_latest_obs form = new DASHBOARD_latest_obs();
    form.setSize(400, 300);
    form.setVisible(true);
  } // GEN-LAST:event_Dashboard_Latest_Obs_actionPerformed

  private void Maintenance_Show_maintenance_data_actionPerformed(
      java.awt.event.ActionEvent
          evt) // GEN-FIRST:event_Maintenance_Show_maintenance_data_actionPerformed
      { // GEN-HEADEREND:event_Maintenance_Show_maintenance_data_actionPerformed
    // TODO add your handling code here:

    mode = MAINTENANCE_SHOW_DATA;

    if (main_support
        .password_ok) // no password needed, direct to the show-all-maintenance-data display form
    {
      mymaintenancedata form = new mymaintenancedata();
      form.setExtendedState(MAXIMIZED_BOTH); // full screen
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_Show_maintenance_data_actionPerformed

  private void Maintenance_Import_maintenance_data_actionPerformed(
      java.awt.event.ActionEvent
          evt) // GEN-FIRST:event_Maintenance_Import_maintenance_data_actionPerformed
      { // GEN-HEADEREND:event_Maintenance_Import_maintenance_data_actionPerformed
    // TODO add your handling code here:

    mode = MAINTENANCE_IMPORT_DATA;

    if (main_support
        .password_ok) // no password needed, direct to the show-all-maintenance-data display form
    {
      mymaintenancedata form = new mymaintenancedata();
      form.setExtendedState(MAXIMIZED_BOTH); // full screen
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_Import_maintenance_data_actionPerformed

  private void Maintenance_Export_maintenance_data_actionPerformed(
      java.awt.event.ActionEvent
          evt) // GEN-FIRST:event_Maintenance_Export_maintenance_data_actionPerformed
      { // GEN-HEADEREND:event_Maintenance_Export_maintenance_data_actionPerformed
    // TODO add your handling code here:

    mode = MAINTENANCE_EXPORT_DATA;

    if (main_support
        .password_ok) // no password needed, direct to the show-all-maintenance-data display form
    {
      mymaintenancedata form = new mymaintenancedata();
      form.setExtendedState(MAXIMIZED_BOTH); // full screen
      form.setVisible(true);
    } else {
      mypassword form = new mypassword();
      form.setSize(400, 300);
      form.setVisible(true);
    }
  } // GEN-LAST:event_Maintenance_Export_maintenance_data_actionPerformed

  void Dashboard_AWS_hybrid_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Dashboard_AWS_hybrid_actionPerformed
    // TODO add your handling code here:

    if (dashboard_form_AWS_hybrid != null) {
      if (DASHBOARD_view_AWS_hybrid.dashboard_update_timer_AWS_hybrid_is_gecreeerd == true) {
        if (DASHBOARD_view_AWS_hybrid.dashboard_update_timer_AWS_hybrid.isRunning()) {
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
  } // GEN-LAST:event_Dashboard_AWS_hybrid_actionPerformed

  void Dashboard_AWS_radar_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Dashboard_AWS_radar_actionPerformed
    // TODO add your handling code here:
    if (dashboard_form_AWS_radar != null) {
      if (DASHBOARD_view_AWS_radar.dashboard_update_timer_AWS_radar_is_gecreeerd == true) {
        if (DASHBOARD_view_AWS_radar.dashboard_update_timer_AWS_radar.isRunning()) {
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
  } // GEN-LAST:event_Dashboard_AWS_radar_actionPerformed

  private void main_windowDeiconified(
      java.awt.event.WindowEvent evt) { // GEN-FIRST:event_main_windowDeiconified
    // TODO add your handling code here:

    // NB looks like in Windows this will never be reached (for Windows see windowIconfied())
    //    note there is a difference betweenWindows and Linux system tray versus Dash
    //

    // System.out.println("+++ Function main_windowDeiconified():" + evt);
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
  } // GEN-LAST:event_main_windowDeiconified

  private void Dashboard_latest_AWS_measurements_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Dashboard_latest_AWS_measurements_actionPerformed
    // TODO add your handling code here:

    // if (mylatestmeasurements.compareTo("") != 0)
    // {
    //   JOptionPane.showMessageDialog(null, "Please close first a previously opened Latest AWS
    // measuremnets form", main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
    // }
    // else
    // {
    //   latestmeasurements_report = MESUREMENTS_SP;              // AMVER sailing plan
    //
    //   mylatestmeasurements form = new mylatestmeasurements();
    //   form.setSize(1000, 700);
    //   form.setVisible(true);
    // }

    mylatestmeasurements form = new mylatestmeasurements();
    form.setSize(1000, 700);
    form.setVisible(true);
  } // GEN-LAST:event_Dashboard_latest_AWS_measurements_actionPerformed

  private void Maps_Obs_Manual_Map_Offline_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_Obs_Manual_Map_Offline_actionPerformed
    // TODO add your handling code here:

    OSM_mode = main.OSM_OFFLINE_MANUAL;
    Maps_OSM();
  } // GEN-LAST:event_Maps_Obs_Manual_Map_Offline_actionPerformed

  private void Maps_Obs_Manual_Map_Online_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_Obs_Manual_Map_Online_actionPerformed
    // TODO add your handling code here:

    OSM_mode = main.OSM_ONLINE_MANUAL;
    Maps_OSM();
  } // GEN-LAST:event_Maps_Obs_Manual_Map_Online_actionPerformed

  private void Maps_AWS_Sensor_Map_Offline_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_AWS_Sensor_Map_Offline_actionPerformed
    // TODO add your handling code here:

    OSM_mode = main.OSM_OFFLINE_AWS_SENSOR;
    Maps_OSM();
  } // GEN-LAST:event_Maps_AWS_Sensor_Map_Offline_actionPerformed

  private void Maps_AWS_Visual_Map_Offline_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_AWS_Visual_Map_Offline_actionPerformed
    // TODO add your handling code here:

    OSM_mode = main.OSM_OFFLINE_AWS_VISUAL;
    Maps_OSM();
  } // GEN-LAST:event_Maps_AWS_Visual_Map_Offline_actionPerformed

  private void Maps_AWS_Sensor_Map_Online_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_AWS_Sensor_Map_Online_actionPerformed
    // TODO add your handling code here:

    OSM_mode = main.OSM_ONLINE_AWS_SENSOR;
    Maps_OSM();
  } // GEN-LAST:event_Maps_AWS_Sensor_Map_Online_actionPerformed

  private void Maps_AWS_Visual_Map_Online_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_AWS_Visual_Map_Online_actionPerformed
    // TODO add your handling code here:

    OSM_mode = main.OSM_ONLINE_AWS_VISUAL;
    Maps_OSM();
  } // GEN-LAST:event_Maps_AWS_Visual_Map_Online_actionPerformed

  private void Output_obs_by_email_default_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Output_obs_by_email_default_actionPerformed
    // TODO add your handling code here:

    email_send_mode = EMAIL_SEND_DEFAULT;
    Output_obs_by_email_all_manual();
  } // GEN-LAST:event_Output_obs_by_email_default_actionPerformed

  private void Info_barometer_comparison_menu_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Info_barometer_comparison_menu_actionPerformed
    // TODO add your handling code here:

    barometer_comparison form = new barometer_comparison();
    form.setSize(1000, 700);
    form.setVisible(true);
  } // GEN-LAST:event_Info_barometer_comparison_menu_actionPerformed

  private void Themes_5_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Themes_5_actionPerformed
    // TODO add your handling code here:

    if (!theme_mode.equals(THEME_TRANSPARENT)) {
      mainClass.dispose();
      theme_changed = true; // for checking more than one instance running

      try {
        // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        theme_mode = THEME_TRANSPARENT;
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (ClassNotFoundException
          | InstantiationException
          | IllegalAccessException
          | UnsupportedLookAndFeelException ex) {
        String info = "Error invoking Transparent Theme";
        JOptionPane.showMessageDialog(
            null, info, main.APPLICATION_NAME + " message", JOptionPane.WARNING_MESSAGE);
      }

      JFrame.setDefaultLookAndFeelDecorated(
          true); // !!! This is essential set it to the defult metal java mode (= the only Java Look
      // and Feel suitable for tranaparency)
      mainClass = new main();
      mainClass.setVisible(true);
    }
  } // GEN-LAST:event_Themes_5_actionPerformed

  private void APR_toolbar_itemStateChanged(
      java.awt.event.ItemEvent evt) { // GEN-FIRST:event_APR_toolbar_itemStateChanged
    // TODO add your handling code here:
    boolean checks_ok = true;
    boolean additional_checks_ok = true;

    APR = jCheckBox1.isSelected() == true; // now APR = true or false

    if (!APR) {
      // NB reset JLabel39 (e.g. in APR mode: "--- more than 30 minutes to go for next automated
      // upload, please do not insert observation data --- "
      //    must be reseted to original string
      main.jLabel39.setForeground(Color.BLACK);
      main.jLabel39.setText(
          "--- adding data: input menu, popup menu, toolbar icons or click on the text labels or fields ---");

      String info = "automated reporting (AP[&T]R) is turned off";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
    } // if (!APR)

    if (APR) {
      // AP[&T]R reporting interval
      if (checks_ok && main.APR_reporting_interval.equals("")) {
        JOptionPane.showMessageDialog(
            null,
            "AP[&T]R reporting interval not selected (Maintenance -> APR/APTR/AWSR settings)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        checks_ok = false;
        APR = false;
        jCheckBox1.setSelected(false);
      }

      // AP[&T]R send method
      if (checks_ok && (main.APTR_AWSR_send_method.equals(""))) {
        JOptionPane.showMessageDialog(
            null,
            "AP[&T]R / AWSR send method unknown (Maintenance -> APR/APTR/AWSR settings)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        checks_ok = false;
        APR = false;
        jCheckBox1.setSelected(false);
      }

      // AP[&T]R draught
      if (checks_ok) {
        try {
          double double_WOW_APR_average_draught = Double.parseDouble(main.WOW_APR_average_draught);

          if (main.WOW_APR_average_draught.equals("")
              || !(double_WOW_APR_average_draught >= 0 && double_WOW_APR_average_draught <= 50)) {
            JOptionPane.showMessageDialog(
                null,
                "normal steaming draft not in range 0.0 - 50.0 (Maintenance -> APR/APTR/AWSR settings)",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
            checks_ok = false;
            APR = false;
            jCheckBox1.setSelected(false);
          }
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(
              null,
              "normal steaming draft not in range 0.0 - 50.0 (Maintenance -> APR/APTR/AWSR settings)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          checks_ok = false;
          APR = false;
          jCheckBox1.setSelected(false);
        }
      } // if (checks_ok)

      // AP[&T]R barometer ic
      if (checks_ok) {
        try {
          double double_barometer_instrument_correction =
              Double.parseDouble(main.barometer_instrument_correction.trim());

          if (main.barometer_instrument_correction.equals("")
              || !(double_barometer_instrument_correction >= -4.0
                  || double_barometer_instrument_correction <= 4.0)) {
            JOptionPane.showMessageDialog(
                null,
                "barometer instrument correction not in range -4.0 - 4.0 (Maintenance -> APR/APTR/AWSR settings)",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
            checks_ok = false;
            APR = false;
            jCheckBox1.setSelected(false);
          }
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(
              null,
              "barometer_instrument_correction not in range -4.0 - 4.0 (Maintenance -> APR/APTR/AWSR settings)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          checks_ok = false;
          APR = false;
          jCheckBox1.setSelected(false);
        }
      } // if (checks_ok)

      // warning checks
      if (checks_ok) {
        additional_checks_ok = WOW_APR_settings.APR_additional_requirements_checks();
      }

      // pop-up message APR was turned on
      if (checks_ok && additional_checks_ok) {
        String info = "automated reporting (AP[&T]R) is turned on";
        JOptionPane.showMessageDialog(
            null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
      } else {
        APR = false;
        jCheckBox1.setSelected(false);
      }
    } // if (APR)

    // NB below for APR turned on AND APR turned off !!
    // clear the text fields on the main screen (because maybe there are still values in the text
    // fields from a previous setting eg APR = true) and enable the output menu items again
    main.Reset_all_meteo_parameters();
    main.disable_and_enable_output_menu_items(); // in fact also for ENABLING the output menu
    // options if now set APR = false and before APR =
    // true
    main.disable_dashboard_and_maps_menu_items(); // in fact also for ENABLING the dasboard menu
    // options if now set APR = false and before APR =
    // true

    // save the change
    main.schrijf_configuratie_regels();

    // set start-up sequence finished flag
    // turbowin_start_up_sequence_finished = true;
  } // GEN-LAST:event_APR_toolbar_itemStateChanged

  private void AWSR_toolbar_itemStateChanged(
      java.awt.event.ItemEvent evt) { // GEN-FIRST:event_AWSR_toolbar_itemStateChanged
    // TODO add your handling code here:
    boolean checks_ok = true;
    boolean additional_checks_ok = true;

    AWSR = jCheckBox2.isSelected() == true; // now AWSR = true or false

    if (!AWSR) {
      // NB reset JLabel39 (e.g. in APR mode: "--- more than 30 minutes to go for next automated
      // upload, please do not insert observation data --- "
      //    must be reseted to original string
      main.jLabel39.setForeground(Color.BLACK);
      main.jLabel39.setText(
          "--- adding data: input menu, popup menu, toolbar icons or click on the text labels or fields ---");

      String info = "automated reporting (AWSR) is turned off";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
    } // if (!AWSR)

    if (AWSR) {
      // AWSR reporting interval
      if (checks_ok && main.AWSR_reporting_interval.equals("")) {
        JOptionPane.showMessageDialog(
            null,
            "ASWR reporting interval not selected (Maintenance -> WOW/APR/APTR/AWSR settings)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        checks_ok = false;
        AWSR = false;
        jCheckBox2.setSelected(false);
      }

      // AWSR send method
      if (checks_ok && (main.APTR_AWSR_send_method.equals(""))) {
        JOptionPane.showMessageDialog(
            null,
            "AWSR send method unknown (Maintenance -> WOW/APR/APTR/AWSR settings)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        checks_ok = false;
        AWSR = false;
        jCheckBox2.setSelected(false);
      }
      // warning checks
      if (checks_ok) {
        additional_checks_ok = WOW_APR_settings.AWSR_additional_requirements_checks();
      }

      // pop-up message AWSR was turned on
      if (checks_ok && additional_checks_ok) {
        String info = "automated reporting (AWSR) is turned on";
        JOptionPane.showMessageDialog(
            null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
      } else {
        AWSR = false;
        jCheckBox2.setSelected(false);
      }
    } // if (AWSR)

    // NB below for AWSR turned on AND AWSR turned off !!
    // clear the text fields on the main screen (because maybe there are still values in the text
    // fields from a previous setting eg AWSR = true) and enable the output menu items again
    main.Reset_all_meteo_parameters();
    main.disable_and_enable_output_menu_items(); // in fact also for ENABLING the output menu
    // options if now set AWSR = false and before AWSR
    // = true

    // save the change
    main.schrijf_configuratie_regels();

    // set start-up sequence finished flag
    // turbowin_start_up_sequence_finished = true;
  } // GEN-LAST:event_AWSR_toolbar_itemStateChanged

  private void Dashboard_APR_radar_actionperformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Dashboard_APR_radar_actionperformed
    // TODO add your handling code here:
    if (dashboard_form_APR_radar != null) {
      if (DASHBOARD_view_APR_radar.dashboard_update_APR_timer_is_gecreeerd == true) {
        if (DASHBOARD_view_APR_radar.dashboard_update_APR_timer.isRunning()) {
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
  } // GEN-LAST:event_Dashboard_APR_radar_actionperformed

  private void Maps_satellite_image_IR_SSEC_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_satellite_image_IR_SSEC_actionPerformed
    // TODO add your handling code here:

    String satellite_image_mode = SATELLITE_IR_IMAGE;
    support_class.determine_satellite_image_url_SSEC(satellite_image_mode);
  } // GEN-LAST:event_Maps_satellite_image_IR_SSEC_actionPerformed

  private void Output_obs_by_email_Custom_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Output_obs_by_email_Custom_actionPerformed
    // TODO add your handling code here:

    email_send_mode = EMAIL_SEND_CUSTOM;
    Output_obs_by_email_all_manual();
  } // GEN-LAST:event_Output_obs_by_email_Custom_actionPerformed

  private void Maps_pilot_charts(final String chart) {
    PilotChartsWorkflow.start(chart);
  }

  private void Maps_pilot_charts_SA_january_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_january_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_JANUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_january_actionPerformed

  private void Maps_pilot_charts_SA_february_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_february_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_FEBRUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_february_actionPerformed

  private void Maps_pilot_charts_SA_march_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_march_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_MARCH;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_march_actionPerformed

  private void Maps_pilot_charts_SA_april_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_april_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_APRIL;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_april_actionPerformed

  private void Maps_pilot_charts_SA_may_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_may_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_MAY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_may_actionPerformed

  private void Maps_pilot_charts_SA_june_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_june_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_JUNE;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_june_actionPerformed

  private void Maps_pilot_charts_SA_july_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_july_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_JULY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_july_actionPerformed

  private void Maps_pilot_charts_SA_august_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_august_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_AUGUST;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_august_actionPerformed

  private void Maps_pilot_charts_SA_september_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_september_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_SEPTEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_september_actionPerformed

  private void Maps_pilot_charts_SA_october_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_october_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_OCTOBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_october_actionPerformed

  private void Maps_pilot_charts_SA_november_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_november_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_NOVEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_november_actionPerformed

  private void Maps_pilot_charts_SA_december_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SA_december_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SA_DECEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SA_december_actionPerformed

  private void Maps_pilot_charts_NA_january_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_january_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_JANUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_january_actionPerformed

  private void Maps_pilot_charts_NA_february_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_february_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_FEBRUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_february_actionPerformed

  private void Maps_pilot_charts_NA_march_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_march_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_MARCH;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_march_actionPerformed

  private void Maps_pilot_charts_NA_april_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_april_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_APRIL;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_april_actionPerformed

  private void Maps_pilot_charts_NA_may_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_may_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_MAY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_may_actionPerformed

  private void Maps_pilot_charts_NA_june_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_june_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_JUNE;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_june_actionPerformed

  private void Maps_pilot_charts_NA_july_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_july_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_JULY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_july_actionPerformed

  private void Maps_pilot_charts_NA_august_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_august_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_AUGUST;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_august_actionPerformed

  private void Maps_pilot_charts_NA_september_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_september_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_SEPTEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_september_actionPerformed

  private void Maps_pilot_charts_NA_october_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_october_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_OCTOBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_october_actionPerformed

  private void Maps_pilot_charts_NA_november_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_november_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_NOVEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_november_actionPerformed

  private void Maps_pilot_charts_NA_december_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NA_december_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NA_DECEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NA_december_actionPerformed

  private void Maps_pilot_charts_SP_january_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_january_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_JANUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_january_actionPerformed

  private void Maps_pilot_charts_SP_february_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_february_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_FEBRUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_february_actionPerformed

  private void Maps_pilot_charts_SP_march_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_march_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_MARCH;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_march_actionPerformed

  private void Maps_pilot_charts_SP_april_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_april_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_APRIL;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_april_actionPerformed

  private void Maps_pilot_charts_SP_may_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_may_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_MAY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_may_actionPerformed

  private void Maps_pilot_charts_SP_june_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_june_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_JUNE;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_june_actionPerformed

  private void Maps_pilot_charts_SP_july_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_july_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_JULY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_july_actionPerformed

  private void Maps_pilot_charts_SP_august_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_august_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_AUGUST;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_august_actionPerformed

  private void Maps_pilot_charts_SP_september_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_september_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_SEPTEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_september_actionPerformed

  private void Maps_pilot_charts_SP_october_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_october_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_OCTOBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_october_actionPerformed

  private void Maps_pilot_charts_SP_november_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_november_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_NOVEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_november_actionPerformed

  private void Maps_pilot_charts_SP_december_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_SP_december_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_SP_DECEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_SP_december_actionPerformed

  private void Maps_pilot_charts_NP_january_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_january_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_JANUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_january_actionPerformed

  private void Maps_pilots_charts_NP_february_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilots_charts_NP_february_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_FEBRUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilots_charts_NP_february_actionPerformed

  private void Maps_pilot_charts_NP_march_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_march_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_MARCH;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_march_actionPerformed

  private void Maps_pilot_charts_NP_april_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_april_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_APRIL;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_april_actionPerformed

  private void Maps_pilot_charts_NP_may_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_may_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_MAY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_may_actionPerformed

  private void Maps_pilot_charts_NP_june_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_june_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_JUNE;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_june_actionPerformed

  private void Maps_pilot_charts_NP_july_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_july_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_JULY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_july_actionPerformed

  private void Maps_pilot_charts_NP_august_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_august_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_AUGUST;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_august_actionPerformed

  private void Maps_pilot_charts_NP_september_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_september_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_SEPTEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_september_actionPerformed

  private void Maps_pilot_charts_NP_october_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_october_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_OCTOBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_october_actionPerformed

  private void Maps_pilot_charts_NP_november_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_november_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_NOVEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_november_actionPerformed

  private void Maps_pilot_charts_NP_december_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_NP_december_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_NP_DECEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_NP_december_actionPerformed

  private void Maps_pilot_charts_IN_january_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_january_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_JANUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_january_actionPerformed

  private void Maps_pilot_charts_IN_february_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_february_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_FEBRUARY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_february_actionPerformed

  private void Maps_pilot_charts_IN_march_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_march_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_MARCH;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_march_actionPerformed

  private void Maps_pilot_charts_IN_april_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_april_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_APRIL;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_april_actionPerformed

  private void Maps_pilot_charts_IN_may_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_may_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_MAY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_may_actionPerformed

  private void Maps_pilot_charts_IN_june_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_june_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_JUNE;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_june_actionPerformed

  private void Maps_pilot_charts_IN_july_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_july_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_JULY;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_july_actionPerformed

  private void Maps_pilot_charts_IN_august_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_august_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_AUGUST;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_august_actionPerformed

  private void Maps_pilot_charts_IN_september_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_september_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_SEPTEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_september_actionPerformed

  private void Maps_pilot_charts_IN_october_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_october_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_OCTOBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_october_actionPerformed

  private void Maps_pilot_charts_IN_november_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_november_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_NOVEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_november_actionPerformed

  private void Maps_pilot_charts_IN_december_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_pilot_charts_IN_december_actionPerformed
    // TODO add your handling code here:
    String pilot_charts_mode = PILOT_CHART_IN_DECEMBER;
    Maps_pilot_charts(pilot_charts_mode);
  } // GEN-LAST:event_Maps_pilot_charts_IN_december_actionPerformed

  private void Dashboard_Obs_Stats_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Dashboard_Obs_Stats_actionPerformed
    // TODO add your handling code here:

    obs_stats_mode = OBSERVERS_STATS;

    myimmtlogperiod form = new myimmtlogperiod();
    form.setSize(500, 300);
    form.setVisible(true);
  } // GEN-LAST:event_Dashboard_Obs_Stats_actionPerformed

  private void Dashboard_Observations_Stats_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Dashboard_Observations_Stats_actionPerformed
    // TODO add your handling code here:

    obs_stats_mode = OBSERVATIONS_STATS;

    myimmtlogperiod form = new myimmtlogperiod();
    form.setSize(500, 300);
    form.setVisible(true);
  } // GEN-LAST:event_Dashboard_Observations_Stats_actionPerformed

  private void Info_device_log_menu_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Info_device_log_menu_actionPerformed
    // TODO add your handling code here:

    mydevice_log form = new mydevice_log();
    form.setExtendedState(MAXIMIZED_BOTH); // full screen
    form.setVisible(true);
  } // GEN-LAST:event_Info_device_log_menu_actionPerformed

  private void Maps_satellite_image_IR_NOAA_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_satellite_image_IR_NOAA_actionPerformed
    // TODO add your handling code here:

    String satellite_image_mode = SATELLITE_IR_IMAGE;
    support_class.determine_satellite_image_url_NOAA(satellite_image_mode);
  } // GEN-LAST:event_Maps_satellite_image_IR_NOAA_actionPerformed

  private void Maps_satellite_image_SST_NOAA_actionPerformed(
      java.awt.event.ActionEvent
          evt) { // GEN-FIRST:event_Maps_satellite_image_SST_NOAA_actionPerformed
    // TODO add your handling code here:

    String satellite_image_mode = SATELLITE_SST_IMAGE;
    support_class.determine_satellite_image_url_NOAA(satellite_image_mode);
  } // GEN-LAST:event_Maps_satellite_image_SST_NOAA_actionPerformed

  private void Maps_OSM() {
    OsmMapWorkflow.start();
  }

  void main_window_updating_date_time() {
    // TODO add your handling code here:

    // called from: - main_windowDeiconified() [main.java]   // in case os = NOT WINDOWS
    //              - main_windowIconfied() [main.java]      // in case os = WINDOWS

    // NB in case of a connected AWS or barometer a timer will already update the date time field on
    // the main screen

    if ((RS232_connection_mode == 3)
        || (RS232_connection_mode == 9)
        || (RS232_connection_mode == 10)
        || (RS232_connection_mode == 11)
        || (APR == true)) // AWS connected or APR
    {
      // System.out.println("+++ " + evt);

      String info = "Screen will be updated within max 1 minute";

      final JOptionPane pane_begin =
          new JOptionPane(
              info,
              JOptionPane.INFORMATION_MESSAGE,
              JOptionPane.DEFAULT_OPTION,
              null,
              new Object[] {},
              null);
      final JDialog updating_dialog = pane_begin.createDialog(main.APPLICATION_NAME);

      Timer timer_begin =
          new Timer(
              2000,
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  updating_dialog.dispose();
                }
              });
      timer_begin.setRepeats(false);
      timer_begin.start();
      updating_dialog.setVisible(true);
    }

    // NB in case of a connected AWS or barometer a timer will already update the date time field on
    // the main screen

  }

  protected static Image createImage(String path) {
    // URL imageURL = TrayIconDemo.class.getResource(path);
    URL imageURL = main.class.getResource(path);

    if (imageURL == null) {
      System.out.println("+++ tray icon image resource not found: " + path);
      return null;
    } else {
      return (new ImageIcon(imageURL)).getImage();
    }
  }

  void Output_obs_to_clipboard_FM13() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    ObservationClipboardWriter.write(clipboard, obs_write);

    IMMT_log();

    Reset_all_meteo_parameters();
  }

  void Output_obs_to_clipboard_format_101() {
    boolean doorgaan = true;
    String clipboard_format_101_line = "";

    // read the compressed obs (format 101) which is the only line in file HPK_format_101.txt
    clipboard_format_101_line = get_format_101_obs_from_file();
    if (clipboard_format_101_line.equals("") == true) {
      doorgaan = false;
    }

    if (doorgaan == true) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      ObservationClipboardWriter.write(clipboard, clipboard_format_101_line);
    }

    IMMT_log();

    Reset_all_meteo_parameters();
  }

  String compile_obs_for_AWS() {
    return AwsObservationComposer.compile();
  }

  public static void set_APR_toolbar() {
    // check APR
    if (APR) {
      jCheckBox1.setSelected(true);
    } else {
      jCheckBox1.setSelected(false);
    }
  }

  public static void set_AWSR_toolbar() {
    // check AWSR
    if (AWSR) {
      jCheckBox2.setSelected(true);
    } else {
      jCheckBox2.setSelected(false);
    }
  }

  private void initComponents2() {
    // functions additional/supporting to this main class
    support_class = new main_support();

    /* title of main screen */
    setTitle(APPLICATION_NAME); // fixed

    /* fixed text bottom screen (e.g. Turboin+ stand-alone mode...) */
    jLabel4.setText(APPLICATION_NAME);

    /* set main application icon (top-left in title bar) */
    setIconImage(
        Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource(main.ICONS_DIRECTORY + "tray.png")));

    /* status field (NB can be over written with different Themes) */
    jTextField4.setBackground(new java.awt.Color(204, 255, 255)); // Cyan

    jTextField4.setName("fm13_field");

    /* create pop-up menu (right mouse button) */
    // create_popup_menu();
    // NB moved to lees_configuratie_regels() [main.java] and read_muffin() [main.java]

    /* determine the OS this program is running on */
    String os = OSDetector.getOSString();
    /* data directory */
    if (os.equals("WINDOWS")) {
      data_dir =
          "C:" + java.io.File.separator + "ProgramData" + java.io.File.separator + "TurboWinPlus";
    } else {
      data_dir =
          java.io.File.separator
              + "opt"
              + java.io.File.separator
              + "turbowinplus"
              + java.io.File.separator
              + "data";
    }
    // log_turbowin_system_message("[GENERAL] data dir:" + data_dir);
    // JOptionPane.showMessageDialog(null, data_dir, "data_dir", JOptionPane.INFORMATION_MESSAGE);
    System.out.println("data dir = " + data_dir);

    // for turbowin system logs
    sdf_tsl_1 = new SimpleDateFormat("MMM_yyyy"); // e.g. JAN_2016 (part of the file name)
    sdf_tsl_1.setTimeZone(TimeZone.getTimeZone("UTC"));

    sdf_tsl_2 =
        new SimpleDateFormat(
            "dd-MMM-yyyy HH:mm:ss"); // e.g. 09-Jan-2016 12:23:33 (time stamp of the recoreded
    // messages)
    sdf_tsl_2.setTimeZone(TimeZone.getTimeZone("UTC"));

    // initialisation
    Reset_all_meteo_parameters();

    // NB in via Reset_all_meteo_parameters() the status line (JTextField4) was set literally to
    // "undefined") (because the selection criterium -AWS connected'is there still not determined0
    jTextField4.setText("");

    /* check if the application is online or offline (different help file handling)  */
    //
    // NB werken via bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
    //               en hierna if (bs.isOffline() == true) gaat niet goed
    // deze methode kan niet goed online of offline bepalen (staat ook in API documentatie)
    // API: "The return value is does not have to be guaranteed to be reliable, as it is sometimes
    // difficult to ascertain the true online / offline state of a client system."
    //

    // initialisation
    offline_mode = true;
    offline_mode_via_jnlp = false;
    offline_mode_via_cmd = true;

    // turbowin jnlp offline file present? (turbowin_jws_offline.jnlp)
    String volledig_path_jnlp_offline_file = data_dir + java.io.File.separator + JNLP_OFFLINE_FILE;
    File jnlp_offline_file = new File(volledig_path_jnlp_offline_file);
    if (jnlp_offline_file.exists()) {
      // So file turbowin_jws_offline.jnlp exists (TurboWeb online version: than this file wil not
      // be present)
      offline_mode = true;
      offline_mode_via_jnlp = true;
    }

    // turbowin cmd or launcher file present? ("turbowin_plus_offline.cmd" or
    // "turbowin_launcher.bat" or "turbowin_launcher")
    String volledig_path_cmd_offline_file = data_dir + java.io.File.separator + CMD_OFFLINE_FILE;
    File cmd_offline_file = new File(volledig_path_cmd_offline_file);

    String volledig_path_turbowin_launcher_file =
        data_dir + java.io.File.separator + TURBOWIN_LAUNCHER_FILE;
    File turbowin_launcher_file = new File(volledig_path_turbowin_launcher_file);

    String volledig_path_turbowin_launcher_file_linux =
        data_dir + java.io.File.separator + TURBOWIN_LAUNCHER_FILE_LINUX;
    File turbowin_launcher_file_linux = new File(volledig_path_turbowin_launcher_file_linux);

    // System.out.println("calculated turbowin_launcher_file path = " + turbowin_launcher_file);

    if (cmd_offline_file.exists()
        || turbowin_launcher_file.exists()
        || turbowin_launcher_file_linux.exists()) {
      // System.out.println(cmd_offline_file + " or " + turbowin_launcher_file + "found");

      // So file "turbowin_plus_offline.cmd" or "turbowin_launcher.bat" or "turbowin_launcher"
      // exists (TurboWeb online version: than this file wil not be present)
      offline_mode = true;
      offline_mode_via_cmd = true;
    }

    /* always for offline mode !!! fixed sub dir logs and sub dir amver(not user configurable) */
    if (offline_mode == true) {
      // logs sub dir
      //
      // NB logs dir fixed for offline mode (sub dir of main dir -main dir is the dir where jar file
      // is located-)
      logs_dir = data_dir + java.io.File.separator + OFFLINE_LOGS_DIR;
      // JOptionPane.showMessageDialog(null, logs_dir, main.APPLICATION_NAME + " logs_dir test",
      // JOptionPane.WARNING_MESSAGE);

      /* check sub dir logs already present, if not -> create */
      final File dirs = new File(logs_dir);
      if (dirs.exists() == false) {
        final boolean success = dirs.mkdirs();
        if (success == false) {
          JOptionPane.showMessageDialog(
              null,
              "Could not create " + logs_dir + ", disk write protected or no permission to write",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        }
      } // if (dirs.exists() == false)

      // if not done before, create sub-sub dir "turbowin_system" (logs\turbowin_system) (NB in case
      // of online(web) mode this will be done in OK_button_actionPerformed() [mylogfiles.java])
      //
      if (dirs.isDirectory()) {
        // create sub dir TURBOWIN_SYSTEM_LOGS (turbowin_system)
        String turbowin_system_logs_dir =
            main.logs_dir + java.io.File.separator + main.TURBOWIN_SYSTEM_LOGS_DIR;
        final File dir_turbowin_system_logs = new File(turbowin_system_logs_dir);
        if (dir_turbowin_system_logs.exists() == false) {
          dir_turbowin_system_logs.mkdir();
          log_turbowin_system_message("[GENERAL] created dir " + turbowin_system_logs_dir);
        }
      } //  if (dirs.isDirectory())

      // amver sub dir
      //
      // NB amver dir fixed for offline mode (sub dir of main dir -main dir is the dir where jar
      // file is located-)
      String amver_dir = data_dir + java.io.File.separator + OFFLINE_AMVER_DIR;

      /* check sub dir amver already present, if not -> create */
      final File dirs_amver = new File(amver_dir);
      if (dirs_amver.exists() == false) {
        final boolean success = dirs_amver.mkdirs();
        if (success == false) {
          JOptionPane.showMessageDialog(
              null,
              "Could not create " + amver_dir + ", disk write protected or no permission to write",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        }
      } // if (dirs.exists() == false)
    } // if (offline_mode == true)

    if (offline_mode == true) {
      /* So file turbowin_jws_offline.jnlp and/or turbowin_plus_offline.cmd exists (TurboWeb online version: than these files wil not be present) */
      // offline_mode = true;

      /* gray (disable) the "output -> obs to server (internet)" menu selection option */
      // see below now it is also an option for offline mode
      // jMenuItem20.setEnabled(false);

      /* gray (disable) the "Info -> Statistics(internet)" menu selection option */
      // jMenuItem36.setEnabled(false);

      /* set label on bottom main screen */
      // application_mode =  "stand-alone mode";
      application_mode = "";
      jLabel4.setText(
          APPLICATION_NAME
              + " "
              + application_mode); // NB can later in this start up process be overwritten if a
      // barometer or AWS is coupled

      /* NB logs dir fixed for offline mode (sub dir of main dir -main dir is the dir where the jar file is located-) */
      /* see function: meta_data_from_configuration_regels_into_global_vars() */
    } else {
      /* set label on bottom main screen */
      application_mode = "web mode";
      jLabel4.setText(APPLICATION_NAME + " " + application_mode);
    }

    // Obs to server
    // if ((obs_format.equals(FORMAT_FM13)) && (offline_mode == true))
    // {
    //   // NB FM13 only "obs to server" in online mode, in case of format 101 "obs to server" is an
    // output option in both modes (online/web and offline)
    //   // gray (disable) the "output -> obs to server (internet)" menu selection option
    //   jMenuItem20.setEnabled(false);
    // }

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
      // check only one instance running (but not if this main class was created again due to a
      // Theme change)
      if (!theme_changed) {
        int port_for_checking_instances = PORT; // pORT is the default (cconstant)

        try {
          // NB PORT = 12345 at start up (= randomly chosen big number)
          //    can be over ruled by the first argument at command line at start up (see main)
          if (PORT_command_line.equals("") == false) {
            try {
              port_for_checking_instances = Integer.parseInt(PORT_command_line);
            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(
                  null,
                  "command line argument PORT number not OK",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
              port_for_checking_instances = PORT;
            }
          } // if (PORT_command_line.equals(""))

          System.out.println(
              "--- server port for checking multiple instances running = "
                  + port_for_checking_instances);

          // s = new ServerSocket(PORT, 10, InetAddress.getLocalHost());
          s = new ServerSocket(port_for_checking_instances, 10, InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
          // shouldn't happen for localhost
        } catch (IOException e) {
          // port taken, so app is already running
          JOptionPane.showMessageDialog(
              null,
              "TurboWin+ is already running",
              main.APPLICATION_NAME,
              JOptionPane.ERROR_MESSAGE);
          System.exit(0);
        }
      } // if (!theme_changed)

      // read stored meta data
      lees_configuratie_regels();
    } else // so offline_via_jnlp mode or online (webstart) mode
    {
      // only jnlp mode can use the single instance running check
      //
      // in offline mode: So by removing the file turbowin_plus_offline.cmd and invoking turbowin+
      // via turbowin_jws_offline.jnlp there will be only a single instance running check
      // online mode: is always started via the jnlp file -> always single instance running check
      //
      //

      // check only one instance running  (but not if this main class was created again due to a
      // Theme change)
      if (!theme_changed) {
        // try
        // {
        //   sis = (SingleInstanceService)ServiceManager.lookup("javax.jnlp.SingleInstanceService");
        // }
        // catch (UnavailableServiceException e) { sis = null; }

        // Register the single instance listener at the start of the application
        // if (sis != null)
        // {
        //   sisL = new SISListener();
        //   sis.addSingleInstanceListener(sisL);
        // }
      } // if (!theme_changed)

      // read stored station data
      //
      // read_muffin();
    } // else

    // get the systemTrays instance
    if (!SystemTray.isSupported()) {
      log_turbowin_system_message("[GENERAL] SystemTray is not supported");
    } else {
      tray = SystemTray.getSystemTray();
    }

    // check wind speed units source in AWS mode
    // NB not available in this stage so see: check_meta_data() [main.java]

    // determine the screen resolution is greather than HD (1920x1080), useful for drawing ships on
    // dashboards
    // display_resolution_greather_than_HD = main_support.determine_screen_size();

  }

  void create_popup_menu() {
    /* create pop-up menu (right mouse button) */
    popup_input = new JPopupMenu();

    JMenuItem menuItem301 = new JMenuItem("Date & Time...");
    menuItem301.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_DateTime_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem301);

    JMenuItem menuItem302 = new JMenuItem("Position, Course & Speed...");
    menuItem302.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Position_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem302);

    JMenuItem menuItem303 = new JMenuItem("Barometer reading...");
    menuItem303.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Barometer_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem303);

    JMenuItem menuItem304 = new JMenuItem("Barograph reading...");
    menuItem304.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Barograph_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem304);

    JMenuItem menuItem305 = new JMenuItem("Temperatures...");
    menuItem305.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Temperatures_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem305);

    JMenuItem menuItem306 = new JMenuItem("Wind...");
    menuItem306.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Wind_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem306);

    // if (!main.GUI_mode.equals(main.GUI_LIGHT))
    // {
    JMenuItem menuItem307 = new JMenuItem("Waves...");
    menuItem307.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_waves_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem307);

    JMenuItem menuItem308 = new JMenuItem("Visibility...");
    menuItem308.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Visibility_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem308);

    JMenuItem menuItem309 = new JMenuItem("Present weather...");
    menuItem309.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Presentweather_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem309);

    JMenuItem menuItem310 = new JMenuItem("Past weather...");
    menuItem310.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Pastweather_menu_actionperformed(null);
          }
        });
    popup_input.add(menuItem310);

    JMenuItem menuItem311 = new JMenuItem("Clouds low...");
    menuItem311.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Cloudslow_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem311);

    JMenuItem menuItem312 = new JMenuItem("Clouds middle...");
    menuItem312.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Cloudsmiddle_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem312);

    JMenuItem menuItem313 = new JMenuItem("Clouds high...");
    menuItem313.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Cloudshigh_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem313);

    JMenuItem menuItem314 = new JMenuItem("Cloud cover & height...");
    menuItem314.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Cloudcover_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem314);
    // } // if (!main.GUI_mode.equals(main.GUI_LIGHT))

    JMenuItem menuItem315 = new JMenuItem("Icing...");
    menuItem315.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Icing_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem315);

    JMenuItem menuItem316 = new JMenuItem("Ice...");
    menuItem316.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Ice_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem316);

    JMenuItem menuItem317 = new JMenuItem("Observer...");
    menuItem317.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Input_Observer_menu_actionPerformed(null);
          }
        });
    popup_input.add(menuItem317);

    popup_input.addSeparator();

    JMenuItem menuItem318 = new JMenuItem("Day colours");
    menuItem318.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Themes_1_actionPerformed(null);
          }
        });
    popup_input.add(menuItem318);

    JMenuItem menuItem319 = new JMenuItem("Night colours");
    menuItem319.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Themes_2_actionPerformed(null);
          }
        });
    popup_input.add(menuItem319);

    JMenuItem menuItem320 = new JMenuItem("Sunrise colours");
    menuItem320.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Themes_3_actionPerformed(null);
          }
        });
    popup_input.add(menuItem320);

    JMenuItem menuItem321 = new JMenuItem("Sunset colours");
    menuItem321.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Themes_4_actionPerformed(null);
          }
        });
    popup_input.add(menuItem321);

    JMenuItem menuItem322 = new JMenuItem("Transparent");
    menuItem322.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Themes_5_actionPerformed(null);
          }
        });
    popup_input.add(menuItem322);

    MouseListener popupListener_input = new PopupListener_input();
    addMouseListener(popupListener_input); // connect to jFrame otherwise eg:
    // jTextField1.addMouseListener(popupListener);
    jToolBar1.addMouseListener(popupListener_input); // also connected to Toolbar now

    // in 'gui light' mode, by default, the logo (Label13 reused) do not respond to right mouse
    // click
    // if (GUI_mode.equals(GUI_LIGHT))
    // {
    //   // in GUI LIGHT mode label13 (present weather in FULL mode) was altered to the chosen logo
    // (eumetnet, noaa, sot)
    //   jLabel13.addMouseListener(popupListener_input);
    // }
  }

  // private void IMMT_log()
  public static void IMMT_log() {
    ImmtLogWorkflow.start();
  }

  private static void schrijven_IMMT_log(final String immt_rec) {
    ImmtLogWriterWorkflow.start(immt_rec);
  }

  public static void help_mouseClicked(final String help_page) {
    // TODO add your handling code here:

    String os = OSDetector.getOSString();

    boolean local_help_file_exists = false;

    // Are the help files stored locally? (installed as part of the complete TurboWin+ installation)
    //
    String help_file_path =
        System.getProperty("app.dir")
            + java.io.File.separator
            + ".."
            + java.io.File.separator
            + "runtime"
            + java.io.File.separator
            + OFFLINE_HELP_DIR
            + java.io.File.separator
            + help_page; // nb help_page is parameter specific e.g. wind.pdf, waves.pdf etc.
    File f = new File(help_file_path);
    if (f.isFile()) {
      local_help_file_exists = true;
    }

    URI uri = null;
    String te_openen_help_file = null;

    if (!local_help_file_exists) {
      // e.g.
      // https://download.dwd.de/pub/turbowin/archive/knmi/help_files/barometer.pdf
      String http_adres = main.URL_INTERNET_HELP + help_page + "";
      try {
        uri = new URI(http_adres);
      } catch (URISyntaxException ex) {
        uri = null;
      }

      if (uri != null) {
        te_openen_help_file = uri.toString();
      }
    } else {
      te_openen_help_file = help_file_path;
    }

    if (os.equals("LINUX")) {
      support_class.open_browser_on_linux(te_openen_help_file);
    } // if (os.equals("LINUX"))
    else {
      support_class.open_browser_on_not_linux(te_openen_help_file);
    }
  }

  void check_immt_size() {
    ImmtLogSizeCheckWorkflow.start();
  }

  void Output_obs_to_file_FM13() {
    Fm13FileOutputWorkflow.start(this);
  }

  void Output_obs_to_file_format_101() {
    Format101FileOutputWorkflow.start(this);
  }

  public static void Output_obs_by_email_jakarta_FM13_format_101(boolean manual_send) {
    JakartaObservationEmailWorkflow.start(manual_send);
  }

  public static void Output_obs_by_email_Localhost_Gmail_Yahoo_FM13_format_101(
      boolean manual_send) {
    PythonObservationEmailWorkflow.start(manual_send);
  }

  void Output_obs_by_email_FM13() {
    Fm13DesktopEmailWorkflow.start();
  }

  private static String urlEncode(String s) {
    return UrlUtils.urlEncode(s);
  }

  void Output_obs_by_email_format_101() {
    Format101DesktopEmailWorkflow.start();
  }

  static String get_format_101_obs_from_file() {
    return Format101DesktopEmailWorkflow.get_format_101_obs_from_file();
  }

  void Output_obs_to_server_FM13_TurboWin_stand_alone() {
    Fm13ServerWorkflow.start();
  }

  void Output_obs_to_server_format_101_V2() {
    Format101ServerWorkflow.start();
  }

  void bepaal_last_record_uit_immt() {
    // NB This function will be called from within a swingworker e.g. see
    // Output_obs_by_email_actionPerformed()
    //    so not necessary to use a swingworker here (it is adviced to use a swingworker when file
    // reading/writing)

    String record = "";

    /* initialisatie */
    last_record = "";

    /* first check if there is an immt log source file present (and not empty) */
    String volledig_path_immt = logs_dir + java.io.File.separator + IMMT_LOG;

    File immt_file = new File(volledig_path_immt);
    if (immt_file.exists() && immt_file.length() > 0) // length() in bytes
    {
      try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt))) {
        while ((record = in.readLine()) != null) {
          last_record = record;
        }
      } catch (IOException ex) {
        System.out.println("--- Function bepaal_last_record_uit_immt(): " + ex);
      }
    } // if (immt_file.exists() && immt_file.length() > 0)
  }

  public static void Reset_all_meteo_parameters() {
    ObservationStateResetter.resetValues();

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
    if (main.dashboard_form_APR_radar != null) {
      DASHBOARD_view_APR_radar.reset_APR_wind_variables();
    }
  }

  public static void delete_logs_turbowin_system() {
    SystemLogCleanupWorkflow.start();
  }

  public static void log_turbowin_system_message(final String message) {
    SystemLogWriterWorkflow.start(message);
  }

  public static void satellite_link_mouse_clicked(String url_satellite_image) {
    SatelliteLinkWorkflow.start(url_satellite_image);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    // displays each of its command-line arguments on a line by itself:
    for (int i = 0; i < args.length; i++) {
      System.out.println("--- Argument " + i + ": " + args[i]);
      if (i == 0) {
        PORT_command_line = args[0];
      }
    } // for (int i = 0; i < args.length; i++)

    java.awt.EventQueue.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            // Set opacity of a decorated JFrame in Java >= 8
            // see:
            // https://stackoverflow.com/questions/39538731/set-opacity-of-a-decorated-jframe-in-java-8/45740640

            // new main().setVisible(true);
            // nb below insteadof new main().setVisible(true); because now there is a reference to
            // the main class (which is used in hybrid and wind radar dashboards)
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
  private javax.swing.JMenuItem jMenuItem141;
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
  static javax.swing.JMenuItem jMenuItem55;
  static javax.swing.JMenuItem jMenuItem56;
  static javax.swing.JMenuItem jMenuItem57;
  static javax.swing.JMenuItem jMenuItem58;
  private javax.swing.JMenuItem jMenuItem59;
  private javax.swing.JMenuItem jMenuItem6;
  private javax.swing.JMenuItem jMenuItem60;
  private javax.swing.JMenuItem jMenuItem61;
  static javax.swing.JMenuItem jMenuItem62;
  static javax.swing.JMenuItem jMenuItem63;
  static javax.swing.JMenuItem jMenuItem64;
  static javax.swing.JMenuItem jMenuItem65;
  static javax.swing.JMenuItem jMenuItem66;
  static javax.swing.JMenuItem jMenuItem67;
  static javax.swing.JMenuItem jMenuItem68;
  static javax.swing.JMenuItem jMenuItem69;
  private javax.swing.JMenuItem jMenuItem7;
  static javax.swing.JMenuItem jMenuItem70;
  private javax.swing.JMenuItem jMenuItem71;
  private javax.swing.JMenuItem jMenuItem72;
  private javax.swing.JMenuItem jMenuItem73;
  private javax.swing.JMenuItem jMenuItem74;
  private javax.swing.JMenuItem jMenuItem75;
  private javax.swing.JMenuItem jMenuItem76;
  static javax.swing.JMenuItem jMenuItem77;
  private javax.swing.JMenuItem jMenuItem78;
  private javax.swing.JMenuItem jMenuItem79;
  private javax.swing.JMenuItem jMenuItem8;
  static javax.swing.JMenuItem jMenuItem80;
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
  static javax.swing.JTextField jTextField1;
  static javax.swing.JTextField jTextField10;
  static javax.swing.JTextField jTextField11;
  static javax.swing.JTextField jTextField12;
  static javax.swing.JTextField jTextField13;
  static javax.swing.JTextField jTextField14;
  static javax.swing.JTextField jTextField15;
  public static javax.swing.JTextField jTextField16;
  public static javax.swing.JTextField jTextField17;
  static javax.swing.JTextField jTextField18;
  static javax.swing.JTextField jTextField19;
  static javax.swing.JTextField jTextField2;
  static javax.swing.JTextField jTextField20;
  static javax.swing.JTextField jTextField21;
  static javax.swing.JTextField jTextField22;
  static javax.swing.JTextField jTextField23;
  static javax.swing.JTextField jTextField24;
  static javax.swing.JTextField jTextField25;
  static javax.swing.JTextField jTextField26;
  static javax.swing.JTextField jTextField27;
  static javax.swing.JTextField jTextField28;
  static javax.swing.JTextField jTextField29;
  static javax.swing.JTextField jTextField3;
  static javax.swing.JTextField jTextField30;
  static javax.swing.JTextField jTextField31;
  static javax.swing.JTextField jTextField32;
  static javax.swing.JTextField jTextField33;
  static javax.swing.JTextField jTextField34;
  static javax.swing.JTextField jTextField35;
  public static javax.swing.JTextField jTextField36;
  public static javax.swing.JTextField jTextField37;
  public static javax.swing.JTextField jTextField38;
  public static javax.swing.JTextField jTextField4;
  public static javax.swing.JTextField jTextField40;
  public static javax.swing.JTextField jTextField5;
  static javax.swing.JTextField jTextField7;
  static javax.swing.JTextField jTextField9;
  private javax.swing.JToolBar jToolBar1;
  // End of variables declaration//GEN-END:variables

  // private constants
  public static final String OBSERVERS_STATS = "observers stats";
  public static final String OBSERVATIONS_STATS = "observations stats";
  public static final String SATELLITE_IR_IMAGE = "satellite_ir_image";
  public static final String SATELLITE_VIS_IMAGE = "satellite_vis_image";
  public static final String SATELLITE_SST_IMAGE = "satellite_sst_image";
  public static final String SPATIE_OBS_SERVER =
      "_"; // "%20";// must be replaced by receiving php program with " "this is to avoid problems
  // by browsers handling spaces
  public static final int MAX_AANTAL_JAREN_IN_IMMT = 5;
  public static final int MAX_AANTAL_WAARNEMERS = 20; // zie ook OBSERVER_ROWS in myobserver.java
  public static final int IMMT_3_POSITION_OBSERVER =
      160; // in IMMT-3 records begin position observer name
  public static final int IMMT_4_POSITION_OBSERVER =
      173; // in IMMT-4 records begin position observer name
  public static final int IMMT_5_POSITION_OBSERVER =
      173; // in IMMT-5 records begin position observer name
  public static final int IMMT_5_LENGTH =
      172; // minimum number char in IMMT 5 record (without the  possible addition of the observer
  // name)
  public static final int IMMT_POSITION_IMMT_VERSION =
      110; // immt version voor zowel IMMT-3/IMMT-4/IMMT-5 staat deze op dezelfde pos, zal in de
  // toekomst ook wel zo blijven, maar wel controleren voor volgende IMMT versies
  static final int IMMT_LIMIT = 1024000; // 512000;      // 512000 bytes / 1024 = 500 kB
  public static final String MOVE_TO_EMAIL = "move_to_email";
  public static final String MOVE_TO_DISK = "move_to_disk";
  public static final String LOGS_ZIP = "logs.zip";
  private final String JNLP_OFFLINE_FILE =
      "turbowin_jws_offline.jnlp"; // deze file zal aanwezig zijn als alleen in offline mode gewerkt
  // wordt (wordt dan door KNMI er bij geleverd)
  private final String CMD_OFFLINE_FILE =
      "turbowin_plus_offline.cmd"; // deze file zal aanwezig zijn als alleen in offline mode gewerkt
  // wordt (wordt dan door KNMI er bij geleverd)
  private final String TURBOWIN_LAUNCHER_FILE = "turbowin_launcher.bat";
  private final String TURBOWIN_LAUNCHER_FILE_LINUX = "turbowin_launcher";
  static final String EMAIL_SEND_DEFAULT = "email_send_default";
  static final String EMAIL_SEND_LOCAL_HOST = "email_send_local_host";
  static final String EMAIL_SEND_GMAIL = "email_send_gmail";
  static final String EMAIL_SEND_YAHOO = "email_send_yahoo";
  static final String EMAIL_SEND_CUSTOM = "email_send_custom";
  private final String PILOT_CHART_SA_JANUARY = "pilot_chart_SA_January";
  private final String PILOT_CHART_SA_FEBRUARY = "pilot_chart_SA_February";
  private final String PILOT_CHART_SA_MARCH = "pilot_chart_SA_March";
  private final String PILOT_CHART_SA_APRIL = "pilot_chart_SA_April";
  private final String PILOT_CHART_SA_MAY = "pilot_chart_SA_May";
  private final String PILOT_CHART_SA_JUNE = "pilot_chart_SA_June";
  private final String PILOT_CHART_SA_JULY = "pilot_chart_SA_July";
  private final String PILOT_CHART_SA_AUGUST = "pilot_chart_SA_August";
  private final String PILOT_CHART_SA_SEPTEMBER = "pilot_chart_SA_September";
  private final String PILOT_CHART_SA_OCTOBER = "pilot_chart_SA_October";
  private final String PILOT_CHART_SA_NOVEMBER = "pilot_chart_SA_November";
  private final String PILOT_CHART_SA_DECEMBER = "pilot_chart_SA_December";
  private final String PILOT_CHART_NA_JANUARY = "pilot_chart_NA_January";
  private final String PILOT_CHART_NA_FEBRUARY = "pilot_chart_NA_February";
  private final String PILOT_CHART_NA_MARCH = "pilot_chart_NA_March";
  private final String PILOT_CHART_NA_APRIL = "pilot_chart_NA_April";
  private final String PILOT_CHART_NA_MAY = "pilot_chart_NA_May";
  private final String PILOT_CHART_NA_JUNE = "pilot_chart_NA_June";
  private final String PILOT_CHART_NA_JULY = "pilot_chart_NA_July";
  private final String PILOT_CHART_NA_AUGUST = "pilot_chart_NA_August";
  private final String PILOT_CHART_NA_SEPTEMBER = "pilot_chart_NA_September";
  private final String PILOT_CHART_NA_OCTOBER = "pilot_chart_NA_October";
  private final String PILOT_CHART_NA_NOVEMBER = "pilot_chart_NA_November";
  private final String PILOT_CHART_NA_DECEMBER = "pilot_chart_NA_December";
  private final String PILOT_CHART_SP_JANUARY = "pilot_chart_SP_January";
  private final String PILOT_CHART_SP_FEBRUARY = "pilot_chart_SP_February";
  private final String PILOT_CHART_SP_MARCH = "pilot_chart_SP_March";
  private final String PILOT_CHART_SP_APRIL = "pilot_chart_SP_April";
  private final String PILOT_CHART_SP_MAY = "pilot_chart_SP_May";
  private final String PILOT_CHART_SP_JUNE = "pilot_chart_SP_June";
  private final String PILOT_CHART_SP_JULY = "pilot_chart_SP_July";
  private final String PILOT_CHART_SP_AUGUST = "pilot_chart_SP_August";
  private final String PILOT_CHART_SP_SEPTEMBER = "pilot_chart_SP_September";
  private final String PILOT_CHART_SP_OCTOBER = "pilot_chart_SP_October";
  private final String PILOT_CHART_SP_NOVEMBER = "pilot_chart_SP_November";
  private final String PILOT_CHART_SP_DECEMBER = "pilot_chart_SP_December";
  private final String PILOT_CHART_NP_JANUARY = "pilot_chart_NP_January";
  private final String PILOT_CHART_NP_FEBRUARY = "pilot_chart_NP_February";
  private final String PILOT_CHART_NP_MARCH = "pilot_chart_NP_March";
  private final String PILOT_CHART_NP_APRIL = "pilot_chart_NP_April";
  private final String PILOT_CHART_NP_MAY = "pilot_chart_NP_May";
  private final String PILOT_CHART_NP_JUNE = "pilot_chart_NP_June";
  private final String PILOT_CHART_NP_JULY = "pilot_chart_NP_July";
  private final String PILOT_CHART_NP_AUGUST = "pilot_chart_NP_August";
  private final String PILOT_CHART_NP_SEPTEMBER = "pilot_chart_NP_September";
  private final String PILOT_CHART_NP_OCTOBER = "pilot_chart_NP_October";
  private final String PILOT_CHART_NP_NOVEMBER = "pilot_chart_NP_November";
  private final String PILOT_CHART_NP_DECEMBER = "pilot_chart_NP_December";
  private final String PILOT_CHART_IN_JANUARY = "pilot_chart_IN_January";
  private final String PILOT_CHART_IN_FEBRUARY = "pilot_chart_IN_February";
  private final String PILOT_CHART_IN_MARCH = "pilot_chart_IN_March";
  private final String PILOT_CHART_IN_APRIL = "pilot_chart_IN_April";
  private final String PILOT_CHART_IN_MAY = "pilot_chart_IN_May";
  private final String PILOT_CHART_IN_JUNE = "pilot_chart_IN_June";
  private final String PILOT_CHART_IN_JULY = "pilot_chart_IN_July";
  private final String PILOT_CHART_IN_AUGUST = "pilot_chart_IN_August";
  private final String PILOT_CHART_IN_SEPTEMBER = "pilot_chart_IN_September";
  private final String PILOT_CHART_IN_OCTOBER = "pilot_chart_IN_October";
  private final String PILOT_CHART_IN_NOVEMBER = "pilot_chart_IN_November";
  private final String PILOT_CHART_IN_DECEMBER = "pilot_chart_IN_December";

  // public constants
  public static final String SPATIE_OBS_VIEW = " ";
  public static final String UNDEFINED = "undefined";
  public static final String APTR_AWSR_SERVER = "APTR_AWSR_server"; // WOW_APR_settings.java
  public static final String APTR_AWSR_SMTP_HOST = "APTR_AWSR_SMTP_host"; // WOW_APR_settings.java
  public static final String APTR_AWSR_GMAIL = "APTR_AWSR_Gmail"; // WOW_APR_settings.java
  public static final String APTR_AWSR_YAHOO_MAIL = "APTR_AWSR_Yahoo_Mail"; // WOW_APR_settings.java
  public static final String APTR_AWSR_CUSTOM_MAIL =
      "APTR_AWSR_Custom_Mail"; // WOW_APR_settings.java
  public static final String SMTP_HOST_SHIP = "SMTP_HOST_SHIP";
  public static final String GMAIL_TLS = "GMAIL_TLS";
  public static final String GMAIL_SSL = "GMAIL_SSL";
  public static final String YAHOO_TLS = "YAHOO_TLS";
  public static final String YAHOO_SSL = "YAHOO_SSL";
  public static final String CUSTOM_TLS = "CUSTOM_TLS";
  public static final String CUSTOM_TLS_STARTTLS = "CUSTOM_TLS_STARTTLS";
  public static final String CUSTOM_SSL = "CUSTOM_SSL";
  public static final String CUSTOM_SSL_STARTTLS = "CUSTOM_SSL_STARTTLS";
  public static final String LEAFLET_CSS_URL =
      "  <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.3.1/dist/leaflet.css\"";
  public static final String LEAFLET_JS_URL =
      "  <script src=\"https://unpkg.com/leaflet@1.3.1/dist/leaflet.js\"";
  public static final String LEAFLET_ESRI_URL =
      "  <script src=\"https://unpkg.com/esri-leaflet@2.1.4/dist/esri-leaflet.js\"";
  public static final String LEAFLET_CSS_INTEGRITY =
      "  integrity=\"sha512-Rksm5RenBEKSKFjgI3a41vrjkw4EVPlJ3+OiI65vTjIdo9brlAacEuKOiQ5OFh7cOI1bkDwLqdLw3Zg0cRJAAQ==\"";
  public static final String LEAFLET_JS_INTEGRITY =
      "  integrity=\"sha512-/Nsx9X4HebavoBvEBuyp3I7od5tA0UzAxs+j83KgC8PU0kgB4XiK4Lfe4y4cgBtaRJQEIFCW+oC506aPT2L1zw==\"";
  public static final String LEAFLET_ESRI_INTEGRITY =
      "  integrity=\"sha512-m+BZ3OSlzGdYLqUBZt3u6eA0sH+Txdmq7cqA1u8/B2aTXviGMMLOfrKyiIW7181jbzZAY0u+3jWoiL61iLcTKQ==\"";
  public static final String LEAFLET_MAPS_HTML_FILE =
      "position_leaflet_maps.html"; // leaflet maps file for displaying just entered position
  public static final double KNOT_M_S_CONVERSION = 0.51444444444;
  public static final double M_S_KNOT_CONVERSION = 1.94384449;
  public static final String OFFLINE_LOGS_DIR = "logs"; // only used inoffline_mode
  public static final String OFFLINE_AMVER_DIR = "amver"; // only used in offline_mode
  public static final String TURBOWIN_SYSTEM_LOGS_DIR =
      "turbowin_system"; // online(web) and offline mode
  public static final int INVALID = 9999999;
  public static final int CONFIGURATION_FILE_POS_INHOUD =
      21; // eg "van wind source        : estimated; true speed and true direction"de pos waar
  // estimated begint
  public static final int MAX_AANTAL_CONFIGURATIEREGELS =
      100; // in configuratie file (for wind source e.d.)
  public static final String ICONS_DIRECTORY = "icons/";
  // public static final String ICONS_DIRECTORY_R             = "icons";        // _R van revised
  // "icons" i.p.v. "icons/" WERKT HELAAS NIET BIJ TOOLBAR ICONS, REDEN ONBEKEND
  public static final String CONFIGURATION_FILE = "configuration.txt";
  public static final String OBSERVER_LOG = "observer.log";
  public static final String CAPTAIN_LOG = "captain.log";
  public static final String IMMT_LOG = "immt.log";

  public static final String SHIP_NAME_TXT = "ship name          : "; // t/m : is 20 characters
  public static final String IMO_NUMBER_TXT = "imo number         : "; // t/m : is 20 characters
  public static final String CALL_SIGN_TXT = "call sign          : "; // t/m : is 20 characters
  public static final String MASKED_CALL_SIGN_TXT =
      "masked call sign   : "; // t/m : is 20 characters
  public static final String TIME_ZONE_COMPUTER_TXT =
      "time zone computer : "; // t/m : is 20 characters
  public static final String RECRUITING_COUNTRY_TXT =
      "recruiting country : "; // t/m : is 20 characters
  public static final String METHOD_WAVES_TXT = "method waves       : "; // t/m : is 20 characters
  public static final String WIND_SOURCE_TXT = "wind source        : "; // t/m : is 20 characters
  public static final String BAROMETER_ABOVE_SLL_TXT =
      "barometer above sll: "; // t/m : is 20 characters
  public static final String BAROMETER_KEEL_TO_SLL_TXT =
      "barometer keel-sll : "; // t/m : is 20 characters
  public static final String PRESSURE_READING_MSL_TXT =
      "pressure read msl  : "; // t/m : is 20 characters
  public static final String AIR_TEMP_EXPOSURE_TXT =
      "air temp exposure  : "; // t/m : is 20 characters
  public static final String SST_EXPOSURE_TXT = "sst exposure       : "; // t/m : is 20 characters
  public static final String MAX_HEIGHT_DECK_CARGO_TXT =
      "max. height cargo  : "; // t/m : is 20 characters
  public static final String DIFF_SLL_WL_TXT = "diff. sll-wl       : "; // t/m : is 20 characters
  public static final String OBS_EMAIL_RECIPIENT_TXT =
      "obs email recipient: "; // t/m : is 20 characters
  public static final String OBS_EMAIL_SUBJECT_TXT =
      "obs email subject  : "; // t/m : is 20 characters
  public static final String LOGS_DIR_TXT = "logs folder        : "; // t/m : is 20 characters
  public static final String LOGS_EMAIL_RECIPIENT_TXT =
      "log email recipient: "; // t/m : is 20 characters
  public static final String WIND_UNITS_TXT = "wind units         : "; // t/m : is 20 characters
  public static final String RS232_INSTRUMENT_TYPE_TXT =
      "RS232 instrument   : "; // t/m : is 20 characters
  public static final String RS232_BITS_PER_SEC_TXT =
      "RS232 bps          : "; // t/m : is 20 characters
  public static final String RS232_DATA_BITS_TXT =
      "RS232 data bits    : "; // t/m : is 20 characters
  public static final String RS232_PARITY_TXT = "RS232 parity       : "; // t/m : is 20 characters
  public static final String RS232_STOP_BITS_TXT =
      "RS232 stop bits    : "; // t/m : is 20 characters
  public static final String RS232_PREFERED_COM_PORT_TXT =
      "RS232 prefered COM : "; // t/m : is 20 characters (Windows and Linux)
  public static final String RS232_INSTRUMENT_TYPE_TXT_II =
      "RS232 instrument_II: "; // t/m : is 20 characters
  public static final String RS232_BITS_PER_SEC_TXT_II =
      "RS232 bps_II       : "; // t/m : is 20 characters
  public static final String RS232_DATA_BITS_TXT_II =
      "RS232 data bits_II : "; // t/m : is 20 characters
  public static final String RS232_PARITY_TXT_II =
      "RS232 parity_II    : "; // t/m : is 20 characters
  public static final String RS232_STOP_BITS_TXT_II =
      "RS232 stop bits_II : "; // t/m : is 20 characters
  public static final String RS232_PREFERED_COM_PORT_TXT_II =
      "RS232 pref COM_II  : "; // t/m : is 20 characters (Windows and Linux)
  public static final String IC_BAROMETER_TXT = "ic barometer       : "; // t/m : is 20 characters
  public static final String OBS_FORMAT_TXT = "obs format         : "; // t/m : is 20 characters
  public static final String FORMAT_101_ENCRYPTION_TXT =
      "format 101 encrypt : "; // t/m : is 20 characters
  public static final String FORMAT_101_EMAIL_TXT =
      "format 101 email   : "; // t/m : is 20 characters
  public static final String RS232_PREF_COM_PORT_NAME_TXT =
      "RS232 pref COM name: "; // t/m : is 20 characters (OS X)
  public static final String WOW_PUBLISH_TXT = "WOW publish        : "; // t/m : is 20 characters
  public static final String WOW_SITE_ID_TXT = "WOW site ID        : "; // t/m : is 20 characters
  public static final String WOW_PIN_TXT = "WOW pin            : "; // t/m : is 20 characters
  public static final String WOW_REPORTING_INTERVAL_TXT =
      "WOW rep. interval  : "; // t/m : is 20 characters
  // public static final String WOW_AVERAGE_BARO_HEIGHT_TXT   = "WOW barom. height  : ";   // t/m :
  // is 20 characters
  public static final String WOW_APR_AVERAGE_DRAUGHT_TXT =
      "WOW/APR draught    : "; // t/m : is 20 characters (average draught last years)
  public static final String AMOS_MAIL_TXT = "AMOS Mail          : "; // t/m : is 20 characters
  public static final String RS232_GPS_TYPE_TXT = "RS232 GPS type     : "; // t/m : is 20 characters
  public static final String RS232_GPS_BITS_PER_SEC_TXT =
      "RS232 GPS bps      : "; // t/m : is 20 characters
  public static final String RS232_GPS_COM_PORT_TXT =
      "RS232 GPS COM      : "; // t/m : is 20 characters (Windows and Linux)
  public static final String RS232_GPS_COM_PORT_NAME_TXT =
      "RS232 GPS COM name : "; // t/m : is 20 characters (OS X)
  public static final String RS232_GPS_SENTENCE_TXT =
      "RS232 GPS sentence : "; // t/m : is 20 characters (RMC or GGA)
  public static final String APR_TXT = "APR                : "; // t/m : is 20 characters
  public static final String APR_REPORTING_INTERVAL_TXT =
      "APR rep. interval  : "; // t/m : is 20 characters
  public static final String UPLOAD_URL_TXT = "upload URL         : "; // t/m : is 20 characters
  public static final String AWSR_TXT = "AWSR               : "; // t/m : is 20 characters
  public static final String AWSR_REPORTING_INTERVAL_TXT =
      "AWSR rep. int.     : "; // t/m : is 20 characters
  public static final String WIND_UNITS_DASHBOARD_TXT =
      "wind units dashbrd : "; // t/m : is 20 characters
  public static final String SHIP_TYPE_DASHBOARD_TXT =
      "ship type dashbrd  : "; // t/m : is 20 characters
  public static final String HEIGHT_ANEMOMETER_TXT =
      "anemometer-WL      : "; // t/m : is 20 characters
  public static final String GUI_MODE_TXT = "GUI mode           : "; // t/m : is 20 characters
  public static final String GUI_LOGO_TXT = "GUI logo           : "; // t/m : is 20 characters
  public static final String OBS_EMAIL_CC_TXT = "obs email cc       : "; // t/m : is 20 characters
  public static final String LOCAL_EMAIL_SERVER_TXT =
      "email local host   : "; // t/m : is 20 characters
  public static final String YOUR_GMAIL_ADDRESS_TXT =
      "your Gmail address : "; // t/m : is 20 characters
  public static final String GMAIL_APP_PASSWORD_TXT =
      "Gmail app password : "; // t/m : is 20 characters
  public static final String GMAIL_SECURITY_TXT = "Gmail security     : "; // t/m : is 20 characters
  public static final String YOUR_YAHOO_ADDRESS_TXT =
      "your Yahoo address : "; // t/m : is 20 characters
  public static final String YAHOO_APP_PASSWORD_TXT =
      "Yahoo app password : "; // t/m : is 20 characters
  public static final String YAHOO_SECURITY_TXT = "Yahoo security     : "; // t/m : is 20 characters
  public static final String YOUR_SHIP_ADDRESS_TXT =
      "your ship address  : "; // t/m : is 20 characters
  public static final String SMTP_HOST_PASSWORD_TXT =
      "smtp host password : "; // t/m : is 20 characters
  public static final String SMTP_HOST_PORT_TXT = "smtp host port     : "; // t/m : is 20 characters
  public static final String APTR_AWSR_SEND_METHOD_TXT =
      "APTR send method   : "; // t/m : is 20 characters    // also for AWSR
  public static final String STATION_ID_TXT = "station ID         : "; // t/m : is 20 characters
  public static final String DASHBOARD_BACKGROUND_IMAGE_TXT =
      "dashboard image    : "; // t/m : is 20 characters
  public static final String YOUR_CUSTOM_ADDRESS_TXT =
      "your custom address: "; // t/m : is 20 characters
  public static final String CUSTOM_EMAIL_SERVER_TXT =
      "custom email server: "; // t/m : is 20 characters
  public static final String CUSTOM_PASSWORD_TXT =
      "custom email passw.: "; // t/m : is 20 characters
  public static final String CUSTOM_SECURITY_TXT =
      "custom security    : "; // t/m : is 20 characters
  public static final String CUSTOM_PORT_TXT = "custom port        : "; // t/m : is 20 characters
  public static final String POP_UP_DASHBOARD_TXT =
      "pop-up dashboard   : "; // t/m : is 20 characters
  public static final String POP_UP_DASHBOARD_INTERVAL_TXT =
      "pop-up dashb int.  : "; // t/m : is 20 characters
  public static final String DASHBOARD_SHIP_DECK_COLOR_TXT =
      "dashbrd deck color : "; // t/m : is 20 characters
  public static final String CUSTOM_EMAIL_MODULE_TXT =
      "custom email module: "; // t/m : is 20 characters
  public static final String LOGS_EMAIL_TXT = "logs email         : "; // t/m : is 20 characters
  public static final String EUCAWS_UPLOADS_METHOD_TXT =
      "eucaws uploads     : "; // t/m : is 20 characters
  public static final String PORT_MODE_OPTION_TXT =
      "port mode option   : "; // t/m : is 20 characters
  public static final String LAN_IP_ADDRESS_TXT = "LAN IP address     : "; // t/m : is 20 characters
  public static final String DASHBOARD_SHIP_TANK_COLOR_TXT =
      "dashbrd tank color : "; // t/m : is 20 characters
  public static final String DASHBOARD_FONT_TXT = "dashbrd font       : "; // t/m : is 20 characters
  public static final String COM_PROTOCOL_TXT = "com protocol       : "; // t/m : is 20 characters
  public static final String EUCAWS_OBS_ID_TXT = "EUCAWS obs id      : "; // t/m : is 20 characters

  public static final String YACHT = "yacht";
  public static final String FULL_RIGGED_3 = "full_rigged_3";
  public static final String FULL_RIGGED_4 = "full_rigged_4";
  public static final String FULL_RIGGED_5 = "full_rigged_5";
  public static final String BARQUE_3 = "barque_3";
  public static final String BARQUE_4 = "barque_4";
  public static final String BARQUE_5 = "barque_5";
  public static final String FRUIT_JUICE_TANKER = "fruit juice tanker";
  public static final String LNG_TANKER_II = "LNG_tanker_II";
  public static final String REEFER_SHIP = "reefer_ship";
  public static final String CONTAINER_SHIP = "container_ship";
  public static final String CONTAINER_SHIP_2 = "container_ship_II";
  public static final String BULK_CARRIER = "bulk_carrier";
  public static final String BULK_CARRIER_2 = "bulk_carrier_II";
  public static final String OIL_TANKER = "oil_tanker";
  public static final String CHEMICAL_TANKER = "chemical_tanker";
  public static final String LNG_TANKER = "LNG_tanker";
  public static final String PASSENGER_SHIP = "passenger_ship";
  public static final String NEUTRAL_SHIP = "neutral_ship";
  public static final String GENERAL_CARGO_SHIP = "general_cargo_ship";
  public static final String GENERAL_CARGO_SHIP_2 = "general_cargo_ship_II";
  public static final String GENERAL_CARGO_SHIP_3 = "general_cargo_ship_III";
  public static final String HEAVY_LIFT_1 = "heavy-lift_I";
  public static final String HEAVY_LIFT_2 = "heavy-lift_II";
  public static final String GENERAL_CARGO_CLASSIC = "general_cargo_classic";
  public static final String RESEARCH_VESSEL = "research_vessel";
  public static final String RO_RO_SHIP_1 = "Ro-Ro_ship_I";
  public static final String RO_RO_SHIP_2 = "Ro-Ro_ship_II";
  public static final String SAILING_YACHT = "sailing_yacht";
  public static final String CATAMARAN = "catamaran";
  public static final String FERRY = "ferry";
  public static final String ESTIMATED_TRUE = "estimated; true speed and true direction";
  public static final String MEASURED_OFF_BOW =
      "measured; apparent speed and apparent direction (OFF THE BOW, clockwise)";
  public static final String MEASURED_TRUE = "measured; true speed and true direction";
  public static final String SLING_PSYCHROMETER = "sling psychrometer";
  public static final String MARINE_SCREEN = "marine screen";
  public static final String INTAKE = "intake";
  public static final String BUCKET = "bucket";
  public static final String HULL_CONTACT_SENSOR = "hull contact sensor";
  public static final String TRAILING_THERMISTOR = "trailing thermistor";
  public static final String THROUGH_HULL_SENSOR = "through hull sensor";
  public static final String RADIATION_THERMOMETER = "radiation thermometer";
  public static final String BAIT_TANKS_THERMOMETER = "bait tanks thermometer";
  public static final String OTHER = "other";
  public static final String TIME_ZONE_COMPUTER_UTC = "UTC/GMT";
  public static final String TIME_ZONE_COMPUTER_OTHER = "other";
  public static final String PRESSURE_READING_MSL_YES = "yes";
  public static final String PRESSURE_READING_MSL_NO = "no";
  public static final String STATION_DATA = "station data"; // see 'mode' in password form
  public static final String EMAIL_SETTINGS = "email settings"; // see 'mode' in password form
  public static final String LOG_FILES = "log files"; // see 'mode' in password form
  public static final String SET_OBS_FORMAT = "set obs format"; // see 'mode' in password form
  public static final String SET_WOW_APR_SETTINGS =
      "set WOW APR settings"; // see 'mode' in password form
  public static final String SET_SERVER_SETTINGS =
      "set server settings"; // see 'mode' in password form
  public static final String MAINTENANCE_SHOW_DATA =
      "maintenance show  data"; // see 'mode' in password form
  public static final String MAINTENANCE_IMPORT_DATA =
      "maintenance import data"; // see 'mode' in password form
  public static final String MAINTENANCE_EXPORT_DATA =
      "maintenance export data"; // see 'mode' in password form
  // public static final String GUI_SETTINGS                  = "GUI settings";            // see
  // 'mode' in password form
  public static final String SEA_AND_SWELL_ESTIMATED = "wind sea and swell estimated";
  public static final String WAVES_MEASURED_SHIPBORNE = "waves measured (shipborne wave recorder)";
  public static final String WAVES_MEASURED_BUOY = "waves measured (buoy)";
  public static final String WAVES_MEASURED_OTHER = "waves measured (other measurement system)";
  public static final String MUFFIN_LINE_SEPARATOR =
      "%"; // i.p.v. eol deze geeft problemen bij bytes -> String
  public static final String UK_OBS_EMAIL_SUBJECT = "SXVX88 EGRR ddhhmm";
  // public static final String GENERAL_OBS_EMAIL_SUBJECT     = "weather observation";
  // public static final String URL_TURBOWIN                  =
  // "https://projects.knmi.nl/turbowin/";
  public static final String URL_INTERNET_HELP =
      "https://download.dwd.de/pub/turbowin/archive/knmi/help_files/"; // "https://projects.knmi.nl/turbowin/webstart101/help/";
  public static final String OFFLINE_HELP_DIR =
      "help"; // wordt alleeen gebruikt indien in offline_mode
  public static final String KNOTS = "knots";
  public static final String M_S = "m/s";
  public static final String AMVER_SP = "amver_sp"; // AMVER sailing plan
  public static final String AMVER_DR = "amver_dr"; // AMVER deviation report
  public static final String AMVER_FR = "amver_fr"; // AMVER arrival report
  public static final String AMVER_PR = "amver_pr"; // AMVER position report
  public static final String FORMAT_FM13 = "format_fm13"; // obs format
  public static final String FORMAT_AWS = "format_aws"; // obs format (if AWS connected)
  public static final String FORMAT_101 = "format_101"; // obs format
  public static final String FORMAT_101_ENCRYPTION_YES = "101_encrypt_yes"; // related to obs format
  public static final String FORMAT_101_ENCRYPTION_NO = "101_encrypt_no"; // related to obs format
  public static final String FORMAT_101_BODY = "101_email_body"; // related to obs format
  public static final String FORMAT_101_ATTACHEMENT =
      "101_email_attachement"; // related to obs format
  public static final String FORMAT_101_ROOT_DIR = "format_101"; // directory for format 101
  public static final String FORMAT_101_TEMP_DIR = "temp"; // directory for format 101
  public static final String FORMAT_101_INPUT_FILE =
      "format_101.txt"; // file for format 101 (NB outputfile is automatically "HPK_" + input file)
  public static final String THEME_NIMBUS_DAY = "theme_nimbus_day";
  public static final String THEME_NIMBUS_NIGHT = "theme_nimbus_night";
  public static final String THEME_NIMBUS_SUNRISE = "theme_nimbus_sunrise";
  public static final String THEME_NIMBUS_SUNSET = "theme_nimbus_sunset";
  public static final String THEME_TRANSPARENT = "theme_transparent";
  public static final String OSM_OFFLINE_MANUAL =
      "OSM_offline_manual"; // conventional VOS (APR included)
  public static final String OSM_ONLINE_MANUAL =
      "OSM_online_manual"; // conventional VOS (APR included)
  public static final String OSM_ONLINE_AWS_SENSOR = "OSM_online_AWS_sensor";
  public static final String OSM_OFFLINE_AWS_SENSOR = "OSM_offline_AWS_sensor";
  public static final String OSM_ONLINE_AWS_VISUAL = "OSM_online_AWS_visual";
  public static final String OSM_OFFLINE_AWS_VISUAL = "OSM_offline_AWS_visual";
  // public static final String GUI_LIGHT                     = "GUI light";
  // public static final String GUI_FULL                      = "GUI full";
  // public static final String LOGO_EUMETNET                 = "logo EUMETNET";
  // public static final String LOGO_NOAA                     = "logo NOAA";
  // public static final String LOGO_SOT                      = "logo SOT";
  // public static final Integer APR_SCREEN_POP_UP_MINUTES_TO_HOUR = 10;
  public static final Integer APR_AWS_SCREEN_POP_UP_MINUTES_TO_HOUR = 10;
  public static final String PYTHON_EMAIL = "python_email";
  public static final String JAKARTA_EMAIL = "jakarta_email";
  public static final String LOGS_DEFAULT_EMAIL = "logs_default_email";
  public static final String LOGS_CUSTOM_EMAIL = "logs_custom_email";
  public static final String UPLOADS_VIA_EUCAWS =
      "uploads_via_eucaws"; // obs format setting (if format is EUCAWS-AWS)
  public static final String UPLOADS_VIA_TURBOWIN =
      "uploads_via_turbowin"; // obs format setting  (if format is EUCAWS-AWS)
  public static final String HTTPS_PROTOCOL = "HTTPS_protocol";
  public static final String HTTP_PROTOCOL = "HTTP_protocol";

  // public static final String KNMI_UPLOAD_URL               =
  // "http://www.knmi.nl/samenw/turbowin/webstart101/index_webstart_101.php?"; //
  // "www.turbowin.knmi.nl/webstart101/index_webstart_101.php?";
  public static final Integer INVALID_RESPONSE_FORMAT_101 = 710; // self defined http response code
  public static final Integer RESPONSE_NO_INTERNET =
      711; // self defined http response code (IOException)
  public static final Integer RESPONSE_MALFORMED_URL = 712; // self defined http response code
  public static final Integer OK_RESPONSE_FORMAT_101 = 713; // self defined http response code
  public static final Integer RESPONSE_INTERRUPTION =
      714; // self defined http response code (InterruptedException | ExecutionException)
  public static final Integer RESPONSE_UNSUPPORTED_ENCODING =
      715; // self defined http response code
  public static final Integer OK_RESPONSE_FORMAT_FM13 = 716; // self defined http response code
  public static final Integer INVALID_RESPONSE_FORMAT_FM13 = 717; // self defined http response code
  public static final Integer INTERNAL_ERROR_RESPONSE_CODE = 718; // self defined http response code

  // public var's
  public static final String APPLICATION_NAME =
      "TurboWin+"; // NB DO NOT FORGET TO BUILD ALL AFTER A CHANGE OF THIS STRING
  public static final String APPLICATION_MET_MODULES = "MAWSbin_TW; teste_hc_TW; email_tbw_43;";
  public static final String DASHBOARD_LOGO = "logo-sot.png"; // i.a. single dashboard barometer
  public static String application_mode =
      ""; // e.g. web mode (set in initComponents2 [main.java] and [main_RS232_RS422.java]
  public static String amver_report = ""; // AMVER
  public static String data_dir;
  public static String[] configuratie_regels =
      new String[MAX_AANTAL_CONFIGURATIEREGELS]; // default values: null
  public static String ship_name = ""; // meta data (mystationdata.java)
  public static String imo_number = ""; // meta data
  // public static String call_sign                           = "";                     // meta data
  // public static String masked_call_sign                    = "";                     // meta data
  public static String station_ID = ""; // meta data
  public static String time_zone_computer = ""; // meta data
  public static String recruiting_country = ""; // meta data
  public static String method_waves = ""; // meta data
  public static String wind_source = ""; // meta data
  public static String barometer_above_sll = ""; // meta data
  public static String keel_sll = ""; // meta data
  public static String air_temp_exposure = ""; // meta data
  public static String sst_exposure = ""; // meta data
  public static String max_height_deck_cargo = ""; // meta data
  public static String diff_sll_wl = ""; // meta data
  public static String pressure_reading_msl_yes_no = ""; // meta data
  public static String height_anemometer = ""; // meta data
  public static String logs_dir = ""; // meta data (in this folder e.g. immt.log)
  public static String coded_obs_total = "";
  public static String obs_email_recipient = ""; // meta data (myemailsettings.java)
  public static String obs_email_subject = ""; // meta data (myemailsettings.java)
  public static String logs_email_recipient = ""; // meta data (myemailsettings.java)
  public static String obs_email_cc = ""; // meta data (myemailsettings.java)
  public static String local_email_server = ""; // meta data (myemailsettings.java)
  public static String your_gmail_address = ""; // meta data (myemailsettings.java)
  public static String gmail_app_password =
      ""; // meta data (myemailsettings.java) // NB = encrypted!!
  public static String gmail_security = ""; // meta data (myemailsettings.java)
  public static String your_yahoo_address = ""; // meta data (myemailsettings.java)
  public static String yahoo_app_password =
      ""; // meta data (myemailsettings.java) // NB = encrypted!!
  public static String yahoo_security = ""; // meta data (myemailsettings.java)
  public static String your_ship_address = ""; // meta data (myemailsettings.java)
  public static String smtp_host_password =
      ""; // meta data (myemailsettings.java) // NB = encrypted!!
  public static String smtp_host_port = ""; // meta data (myemailsettings.java)
  public static String your_custom_address = ""; // meta data (myemailsettings.java)
  public static String custom_email_server = ""; // meta data (myemailsettings.java)
  public static String custom_port = ""; // meta data (myemailsettings.java)
  public static String custom_password = ""; // meta data (myemailsettings.java) // NB = encrypted!!
  public static String custom_security = ""; // meta data (myemailsettings.java)
  public static String custom_email_module =
      ""; // meta data (myemailsettings.java) [PYTHON_EMAIL or JAKARTA_EMAIL]
  public static String log_files_email_send_method =
      ""; // meta data (myemailsettings.java) [LOGS_DEFAULT_EMAIL or LOGS_CUSTOM_EMAIL]
  public static String server_com_protocol =
      ""; // meta data (myserversettings.java) [HTTPS_PROTOCOL, HTTP_PROTOCOL]

  public static String barometer_instrument_correction = ""; // meta data (mybarometer.java)
  public static String mode = "";
  public static String wind_units = ""; // meta data (wind units observed/measured)
  public static String wind_units_dashboard = ""; // meta data (wind units graphs/dashboard)
  public static String ship_type_dashboard = ""; // meta data (for dashboard)
  public static String dashboard_background_image = ""; // meta data (for dashboard)
  public static String ship_deck_color_String = ""; // meta data (for dashboard)
  public static String ship_tank_color_String = ""; // meta data (for dashboard)
  public static String dashboard_font = ""; // meta data (for dashboard)

  public static String leaflet_maps_obs_year = ""; // for date time in infowindow on World map
  public static String leaflet_maps_obs_month = ""; // for date time in infowindow on World map
  public static String leaflet_maps_obs_day = ""; // for date time in infowindow on World map
  public static String leaflet_maps_obs_hour = ""; // for date time in infowindow on World map
  public static String leaflet_maps_obs_wind_dir = ""; // for infowindow on World map
  public static String leaflet_maps_obs_wind_speed = ""; // for infowindow on World map
  public static String leaflet_maps_obs_air_temp = ""; // for infowindow on World map
  public static String leaflet_maps_obs_sst = ""; // for infowindow on World map
  public static String leaflet_maps_obs_msl_pressure = ""; // for infowindow on World map
  public static String OSM_mode = "";
  public static String GUI_mode = ""; // light/full               // always empty from version 4.2
  public static String GUI_logo = ""; // EUMETNET/NOAA/SOT logo   // always empty from version 4.2

  public static boolean eucaws_obs_id = false; // meta data (myobsformat.java)
  public static String eucaws_uploads_method = ""; // meta data for obs format settings
  public static String obs_format =
      ""; // meta data for obs format settings (e.g. FORMAT_101 or FORMAT_FM13)
  public static String obs_101_encryption =
      FORMAT_101_ENCRYPTION_NO; // meta data for obs format settings (from version 4.4. no call sign
  // encryption by default)
  public static String obs_101_email = ""; // meta data for obs format settings
  public static String upload_URL =
      ""; // meta data for server settings (used by Output -> Obs to server)
  public static boolean amos_mail = false; // meta data (myemailsettings.java)
  public static String newline = System.getProperty("line.separator");
  public static String theme_mode = "";
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
  public static boolean in_next_sequence = false;
  public static boolean offline_mode;
  public static boolean offline_mode_via_jnlp;
  public static boolean offline_mode_via_cmd;
  public static boolean tray_icon_clicked;
  public static boolean use_system_date_time_for_updating =
      false; // NB if you start with true then if the data/comfirmation box pop-ups and the user
  // disagree the time is still inserted by the timer loop
  public static boolean theme_changed = false; // used by checking more than one instance running
  public static TrayIcon trayIcon;
  public static String obs_write = "";
  public static String PORT_command_line =
      ""; // alternative for PORT (via command line) for checking running second instance

  // private var's
  private static final int PORT =
      12345; // for checking only one instance is running  // random large port number
  private static ServerSocket s; // do not delete!
  // private SingleInstanceService sis                               = null;          // for
  // checking only one instance is running
  // private SISListener sisL                                        = null;          // for
  // checking only one instance is running
  public static String output_dir = null; // for function Kopieeren_Waarnemers_En_Aantallen()
  static String hulp_dir =
      ""; // for writing configuration.txt file in data_dir (system defined) AND logs_dir (user
  // defined) (backup for muffin)
  // private URL url_php;

  String output_file = "";
  public static String volledig_path_dstFilename_immt = "";
  public static String volledig_path_srcFilename_immt = "";
  public static String volledig_path_backup_srcFilename_immt = "";
  public static String volledig_path_dstFilename_captain = "";
  public static String volledig_path_srcFilename_captain = "";
  public static String volledig_path_backup_srcFilename_captain = "";
  public static String temp_logs_dir = "";
  static String email_send_mode =
      ""; // SMTP_LOCAL_HOST / GMAIL_TLS / GMAIL_SSL / YAHOO_TLS / YAHOO_SSL / EMAIL_SEND_CUSTOM
  public static String last_record = "";
  public static String[] jaar_substring_array = new String[MAX_AANTAL_JAREN_IN_IMMT];
  public static String[] observername_array = new String[MAX_AANTAL_WAARNEMERS];
  public static GregorianCalendar cal_systeem_datum_tijd;
  public static GregorianCalendar cal_systeem_datum_tijd_UTC;
  public static GregorianCalendar cal_systeem_datum_tijd_LT;
  public static Font current_font;

  // RS232-RS422
  //
  private main_RS232_RS422 RS232_RS422 = null;
  private RS232_mintaka RS232_mintaka_class = null; // do not delete!
  private RS232_vaisala RS232_vaisala_class = null; // do not delete!

  public static final String SERIAL_CONNECTION = "serial connection"; // see mode in password form
  // public static final String WIFI_CONNECTION                      = "WiFi connection";  // see
  // mode in password form
  public static final String MODE_PRESSURE = "mode_pressure";
  public static final String MODE_AIRTEMP = "mode_airtemp";
  public static final String MODE_AIRTEMP_II = "mde_airtemp II";
  public static final String MODE_SST = "mode_sst";
  public static final String MODE_WIND_SPEED = "mode_windspeed";
  public static final String MODE_WIND_DIR = "mode_winddir";
  public static final String MODE_ALL_PARAMETERS = "mode_all_parameters";
  public static final String METEO_LOGS =
      "meteo logs"; // used in subject field email when sending meteo logs via email (default and
  // custom)

  public static final int RECORD_LENGTE_PTB330 = 46;
  public static final int RECORD_DATUM_TIJD_BEGIN_POS_PTB330 = 34;
  public static final int RECORD_MINUTEN_BEGIN_POS_PTB330 = RECORD_DATUM_TIJD_BEGIN_POS_PTB330 + 10;
  public static final int RECORD_P_BEGIN_POS_PTB330 = 0;
  public static final int RECORD_a_BEGIN_POS_PTB330 = 30;
  public static final int RECORD_ppp_BEGIN_POS_PTB330 = 24;

  public static final int RECORD_LENGTE_PTB220 = 36;
  public static final int RECORD_DATUM_TIJD_BEGIN_POS_PTB220 = 24;
  public static final int RECORD_MINUTEN_BEGIN_POS_PTB220 = RECORD_DATUM_TIJD_BEGIN_POS_PTB220 + 10;
  public static final int RECORD_P_BEGIN_POS_PTB220 = 0;
  public static final int RECORD_a_BEGIN_POS_PTB220 = 22;
  public static final int RECORD_ppp_BEGIN_POS_PTB220 = 16;

  public static final int RECORD_LENGTE_HMP155 = 48;

  public static int RS232_GPS_sentence = 0; //  0 = no existing value; 1 = RMC ; 2 = GGA
  public static int RS232_GPS_connection_mode = 0; //  0 = default = no GPS connected via RS232
  public static int RS232_connection_mode =
      0; //  0 = default = no meteorological instrument connected via RS232
  public static int RS232_connection_mode_II =
      0; //  0 = default = no 2nd meteorological instrument connected via RS232
  public static int bits_per_second = 0; //  0 = meteorological instrument no existing value
  public static int bits_per_second_II =
      0; //  0 = meteorological instrument no existing value; 2nd meteo instrument
  public static int GPS_bits_per_second = 0; //  0 = GPS no existing value
  public static int data_bits = 0; //  0 = meteorological instrument no existing value
  public static int data_bits_II =
      0; //  0 = meteorological instrument no existing value; 2nd meteo instrument
  public static int parity = 99; // 99 = meteorological instrument no existing value
  public static int parity_II =
      99; // 99 = meteorological instrument no existing value; 2nd meteo instrument
  public static int stop_bits = 0; //  0 = meteorological instrument no existing value
  public static int stop_bits_II =
      0; //  0 = meteorological instrument no existing value; 2nd meteo instrument
  // public static int flow_control                                  = SerialPort.FLOWCONTROL_NONE;
  public static String prefered_COM_port_number = ""; // meteorological instrument Windows and Linux
  public static String prefered_COM_port_number_II =
      ""; // meteorological instrument Windows and Linux
  public static String prefered_GPS_COM_port_number = ""; // GPS Windows and Linux
  public static String prefered_COM_port_name = ""; // meteorological instrument OS X
  public static String prefered_GPS_COM_port_name = ""; // GPS OS X
  public static String prefered_COM_port = ""; // meteorological instrument generic (Windows/Linux)
  public static String prefered_COM_port_II =
      ""; // 2nd meteorological instrument generic (Windows/Linux)
  public static String prefered_GPS_COM_port = ""; // GPS generic (Windows/Linux/OS X)
  public static String defaultPort = null; // system port name for opening/closing etc
  public static String defaultPort_II =
      null; // system port name for opening/closing etc (2nd meteo instrument)
  public static String defaultPort_descriptive =
      null; // descripte port name for info messages and logging
  public static String defaultPort_descriptive_II =
      null; // descripte port name for info messages and logging
  public static String sensor_data_record_obs_pressure = "";
  public static String sensor_data_record_obs_ppp = "";
  public static String sensor_data_record_obs_a = "";
  public static String
      mode_grafiek; // first initialisation in initComponents2() later in Functions:
  // Graphs_Airtemp_Sensor_Data_actionPerformed() etc.
  public static String lan_ip_address =
      ""; // IP address TurboWin+ is listening to if Mintaka ENet box is connected
  public static SimpleDateFormat sdf3;
  public static SimpleDateFormat sdf4;
  public static SimpleDateFormat sdf_tsl_1; // TurboWin system logs
  public static SimpleDateFormat sdf_tsl_2; // TurboWin system logs
  public static boolean sensor_data_file_ophalen_timer_is_gecreeerd = false; // static!
  public static boolean sensor_data_file_ophalen_timer_is_gecreeerd_II = false; // static!

  public static int VOT = Integer.MAX_VALUE; // for dashboard Meteo France

  // NB parameters like position, date time and air pressure etc. are always not editable by the
  // observer in AWS mode (so not necessary to keep a boolean record of these parameters)
  public static boolean date_from_AWS_present = false;
  public static boolean time_from_AWS_present = false;
  public static boolean latitude_from_AWS_present = false;
  public static boolean longitude_from_AWS_present = false;
  public static boolean COG_from_AWS_present = false;
  public static boolean SOG_from_AWS_present = false;
  public static boolean true_heading_from_AWS_present = false;
  public static boolean pressure_sensor_level_from_AWS_present = false;
  public static boolean pressure_MSL_from_AWS_present = false;
  public static boolean pressure_tendency_from_AWS_present = false;
  public static boolean pressure_characteristic_from_AWS_present = false;
  public static boolean air_temp_from_AWS_present = false;
  public static boolean rh_from_AWS_present = false;
  public static boolean SST_from_AWS_present = false;
  public static boolean relative_wind_speed_from_AWS_present = false;
  public static boolean relative_wind_dir_from_AWS_present = false;
  public static boolean true_wind_speed_from_AWS_present = false;
  public static boolean true_wind_dir_from_AWS_present = false;
  public static boolean true_wind_gust_from_AWS_present = false;
  public static boolean true_wind_gust_dir_from_AWS_present = false;
  public static boolean displayed_aws_data_obsolate = false; // for DASHBOARD; set in Function:
  // RS422_init_new_aws_data_received_check_timer()[main_RS232_RS422.java]
  public static boolean displayed_barometer_data_obsolate =
      false; // for DASHBOARD; set in Function:
  // RS232_WiFi_init_new_aws_data_received_check_timer()[main_RS232_RS422.java]
  public static boolean displayed_thermometer_data_obsolete =
      false; // for DASHBOARD; set in Function:
  // RS232_WiFi_init_new_aws_data_received_check_timer_II()[main_RS232_RS422.java]
  public static boolean VOT_from_AWS_present = false; // for DASHBOARD

  public static final int NUMBER_COM_PORTS =
      20; // used by checking COM ports meteorological instrument (barometer, EUCAWS) and also for
  // GPS
  public static final int LENGTE_SMD_STRING =
      14; // 14;//1024;//20;  // 20 is willekeurig, moet nog precies bepaald worden
  public static final int TOTAL_NUMBER_RECORD_COMMAS = 27;
  public static final int TOTAL_NUMBER_RECORD_COMMAS_MINTAKA = 3;
  public static final int TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STAR = 8;
  public static final int TOTAL_NUMBER_RECORD_COMMAS_MINTAKA_STARX = 13;
  public static final int DATE_COMMA_NUMBER = 1; // reading from EUCAWS sensor data file
  public static final int TIME_COMMA_NUMBER = 2; //             --"--
  public static final int LATITUDE_COMMA_NUMBER = 3; //             --"--
  public static final int LONGITUDE_COMMA_NUMBER = 4; //             --"--
  public static final int COG_COMMA_NUMBER = 5; //             --"--
  public static final int SOG_COMMA_NUMBER = 6; //             --"--
  public static final int HEADING_COMMA_NUMBER = 7; //             --"--
  public static final int PRESSURE_SENSOR_HEIGHT_COMMA_NUMBER = 8; //             --"--
  public static final int PRESSURE_MSL_COMMA_NUMBER = 9; //             --"--
  public static final int PRESSURE_TENDENCY_COMMA_NUMBER = 10; //             --"--
  public static final int PRESSURE_CHARACTERISTIC_COMMA_NUMBER = 11; //             --"--
  public static final int AIR_TEMP_COMMA_NUMBER = 12; //             --"--
  public static final int HUMIDITY_COMMA_NUMBER = 13; //             --"--
  public static final int SST_COMMA_NUMBER = 14; //             --"--
  public static final int RELATIVE_WIND_SPEED_COMMA_NUMBER = 15; //             --"--
  public static final int RELATIVE_WIND_DIR_COMMA_NUMBER = 16; //             --"--
  public static final int TRUE_WIND_SPEED_COMMA_NUMBER = 17; //             --"--
  public static final int TRUE_WIND_DIR_COMMA_NUMBER = 18; //             --"--
  public static final int TRUE_WIND_GUST_COMMA_NUMBER = 19; //             --"--
  public static final int TRUE_WIND_GUST_DIR_COMMA_NUMBER = 20; //             --"--
  // NB --- supply voltage                                          21
  // NB --- internal temperature                                    22
  public static final int VOT_COMMA_NUMBER = 23; //              --"--
  // NB --- spare                                                   24
  // NB --- spare                                                   25
  // NB --- spare                                                   26

  public static int type_record_lengte = 0;
  public static int type_record_datum_tijd_begin_pos = 0;
  public static int type_record_pressure_begin_pos = 0;
  public static int type_record_a_begin_pos = 0;
  public static int type_record_ppp_begin_pos = 0;

  // public static SerialPort[] portList;//public static String[] portList;
  public static GregorianCalendar obs_file_datum_tijd;
  public static SerialPort serialPort = null;
  public static SerialPort serialPort_II = null;
  public static SerialPort GPS_serialPort = null;
  public static String total_string;
  public static String total_string_II;
  public static boolean obsolate_data_flag = false; // obsolete meteo data (barometer)
  public static boolean obsolate_data_flag_II =
      false; // obsolete meteo data 2nd instrument (thermometer)
  public static boolean obsolate_GPS_data_flag = false; // obsolete GPS data

  private RS232_view graph_form;
  private DASHBOARD_view dashboard_form;
  private DASHBOARD_view_AWS dashboard_form_AWS; // analogue AWS
  private DASHBOARD_view_AWS_digital dashboard_form_AWS_digital; // digital AWS
  private DASHBOARD_view_AWS_hybrid dashboard_form_AWS_hybrid; // hybrid AWS
  public static Obs_Stats_view graph_obs_stats;
  public static DASHBOARD_view_AWS_radar dashboard_form_AWS_radar; // wind radar AWS
  public static DASHBOARD_view_APR_radar
      dashboard_form_APR_radar; // meteo radar APR // also used in mywind.java
  public static boolean dashboard_was_manually_closed_in_dashboard_pop_up_period =
      false; // in connection with pop-up dashboard (AWS and APR)
  public static boolean dashboard_was_automatically_opened =
      false; // in connection with pop-up dashboard (AWS and APR)
  public static boolean turbowin_start_up_sequence_finished =
      false; // in connection with pop-up dashboard (AWS and APR)
  public static boolean in_dashboard_pop_up_period =
      false; // in connection with pop-up dashboard (AWS and APR)
  public static boolean port_mode_option =
      false; // deactivate APR/AWSR is ship speed is minimal (e.g. < 1 knot)

  public static final Color input_color_from_aws =
      Color.RED; // color for text fields if input was measured by AWS (manually input of that text
  // field disabled)
  public static final Color input_color_from_observer = Color.BLACK;
  public static final Color obsolate_color_data_from_aws = Color.GRAY;
  public static final Color input_color_from_apr = Color.RED;
  public static final Color obsolete_input_color_from_apr = Color.GRAY;

  // WOW
  public static boolean WOW =
      false; // meta data  yes or no publish on WOW (WeatherObservationsWebsite)
  public static String WOW_site_id = ""; // meta data
  public static String WOW_site_pin = ""; // meya data
  public static String WOW_reporting_interval = ""; // meta data
  // public static String WOW_average_height_barometer                = "";        // meta data
  // public final static String WOW_REPORTING_INTERVAL_MANUAL         = "44444";     // meta data
  // (44444 is just a number)

  // AP[&T]R (Automated Pressure [&Temperature] Reports)
  public static boolean APR = false; // meta data   (WOW_APR_settings.java)
  public static String APR_reporting_interval = ""; // meta data   (WOW_APR_settings.java)

  // AWSR (Automatic Weather Station Reports)
  public static boolean AWSR = false; // meta data   (WOW_APR_settings.java)
  public static String AWSR_reporting_interval = ""; // meta data   (WOW_APR_settings.java)

  // AP[&T]R and AWSR
  public static String APTR_AWSR_send_method = ""; // meta data   (WOW_APR_settings.java)

  // AP[&T]R /AWS pop-up visual obs reminder dashboard
  // public static boolean pop_up_dashboard                            = false;      // meta data
  // (WOW_APR_settings.java)
  // public static String pop_up_dashboard_interval                    = "";         // meta data
  // (WOW_APR_settings.java)

  // AP[&T]R /AWS pop-up visual obs reminder screen
  public static boolean pop_up_screen = false; // meta data   (WOW_APR_settings.java)
  public static String pop_up_screen_interval = ""; // meta data   (WOW_APR_settings.java)
  public static pop_up_screen pop_up_form = null;
  public static boolean screen_was_manually_closed_in_pop_up_period = false;
  // public static boolean in_screen_pop_up_period                     = false;

  // WOW and AP[&T]R and AWSR
  public static String WOW_APR_average_draught = ""; // meta data   (WOW_APR_ettings.java)

  public static SystemTray tray;
  private JPopupMenu popup_input;

  public static ship myship; // class
  public static main mainClass; // class
  public static FORMAT_101 format_101_class; // class
  public static OSM osm_class; // class
  public static Python_Email python_email_class; // class
  public static Jakarta_Email jakarta_email_class; // class
  public static main_support support_class; // class

  public static String
      obs_stats_mode; // to distinguish to display the observers stas or the observations stats
  // public static boolean display_resolution_greather_than_HD;        // for drawing ships on
  // dashboards
}
