package turbowin;

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
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class main_support {

  public static String getLinuxFlavor() {
    String flavor = readOsRelease();
    if (flavor == null) {
      flavor = getLegacyLinuxFlavor();
    }

    return flavor != null ? flavor : "Unknown Linux Flavor";
  }

  private static String readOsRelease() {
    String osReleaseFile = "/etc/os-release";
    try (BufferedReader reader = new BufferedReader(new FileReader(osReleaseFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("PRETTY_NAME")) {
          // Common PRETTY_NAME Values by Distribution e.g.:
          //
          // Ubuntu 22.04 LTS
          // Debian GNU/Linux 12 (bookworm)
          // Fedora Linux 39
          return line.split("=")[1].replaceAll("\"", "");
        }
      }
    } catch (IOException ignored) {
    }

    return null;
  }

  private static String getLegacyLinuxFlavor() {
    String[] files = {"/etc/debian_version", "/etc/redhat-release", "/etc/SuSE-release"};
    for (String filePath : files) {
      try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        return reader.readLine();
      } catch (IOException ignored) {
      }
    }

    return null;
  }

  // public static void main(String[] args) {
  //     System.out.println("Linux Flavor: " + getLinuxFlavor());
  // }

  public static String print_libraries_name_and_version() {
    String library_log_string = "";

    try {
      Enumeration resEnum;
      resEnum =
          Thread.currentThread()
              .getContextClassLoader()
              .getResources(JarFile.MANIFEST_NAME); // Silently ignore wrong manifests on classpath?
      while (resEnum.hasMoreElements()) {
        try {
          URL url = (URL) resEnum.nextElement();
          InputStream is = url.openStream();
          if (is != null) {
            Manifest manifest = new Manifest(is);
            Attributes mainAttribs = manifest.getMainAttributes();
            String library_name = mainAttribs.getValue("Bundle-Name");
            String library_version = mainAttribs.getValue("Bundle-Version");

            if (library_name != null) {
              library_log_string += library_name + " " + library_version + "; ";
            }

            // System.out.println("--- " + library_log_string);
          }
        } catch (Exception e) {
          // NB Silently ignore wrong manifests on classpath
        }
      } // while (resEnum.hasMoreElements())
    } // try
    catch (IOException ex) {
      // NB Silently ignore wrong manifests on classpath
    }

    return library_log_string;
  }

  public void log_integrated_libraries() {
    log_turbowin_system_message(
        "[GENERAL] libraries: "
            + main.APPLICATION_MET_MODULES
            + " "
            + print_libraries_name_and_version());
  }

  public static void password_timer_task() {

    ActionListener update_password_action =
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            password_ok = false;
          }
        };

    // main loop for updating password_ok
    password_timer = new Timer(DELAY_UPDATE_PASSWORD_LOOP, update_password_action);
    password_timer.setRepeats(false); // false = only one action
    // password_timer.setInitialDelay(0);                                 // time in millisec to
    // wait after timer is started to fire first event
    password_timer.setCoalesce(true); // by default true, but to be certain
    password_timer.restart();
    // password_timer_is_gecreeerd = true;

  }

  public void open_browser_on_not_linux(final String subject_address) {
    BrowserOpenWorkflow.startNonLinux(subject_address);
  }

  public void open_browser_on_linux(final String subject_address) {
    BrowserOpenWorkflow.startLinux(subject_address);
  }

  public void response_warning_pop_up() {
    // Temporary message box, (pop-up for only a short time) automatically disappears
    //

    // called from: Output_obs_to_server_format_101()
    //              Output_obs_to_server_FM13_TurboWin_stand_alone()

    final JOptionPane pane =
        new JOptionPane(
            "server response may take a number of seconds",
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[] {},
            null);
    final JDialog response_warning_dialog = pane.createDialog(main.APPLICATION_NAME);

    Timer timer_begin =
        new Timer(
            3000,
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                response_warning_dialog.dispose();
              }
            });
    timer_begin.setRepeats(false);
    timer_begin.start();
    response_warning_dialog.setVisible(true);
  }

  public void determine_satellite_image_url_SSEC(String satellite_image_mode) {
    // defaults
    String products = "";
    String url_satellite_image = "";

    // determine type of satellite image
    if (satellite_image_mode.equals(main.SATELLITE_IR_IMAGE)) {
      products = "globalir";
    } else if (satellite_image_mode.equals(main.SATELLITE_VIS_IMAGE)) {
      products = "global1kmvis";
    } else if (satellite_image_mode.equals(main.SATELLITE_SST_IMAGE)) {
      products = "NESDIS-SST";
    }

    // Latitude
    double centerLat =
        Math.max(
            -90,
            Math.min(
                90,
                parseSatelliteCoordinate(
                    myposition.latitude_degrees,
                    myposition.latitude_hemisphere,
                    myposition.HEMISPHERE_SOUTH)));

    // Longitude
    double centerLon =
        Math.max(
            -180,
            Math.min(
                180,
                parseSatelliteCoordinate(
                    myposition.longitude_degrees,
                    myposition.longitude_hemisphere,
                    myposition.HEMISPHERE_WEST)));

    // Force zoom <= 4 to reduce chance of watermark
    int zoomLevel = 2;

    // construct URL
    url_satellite_image =
        String.format(
            "https://realearth.ssec.wisc.edu/?products=%s&time=latest&center=%.6f,%.6f&zoom=%d",
            products, centerLat, centerLon, zoomLevel);

    // open in browser
    main.satellite_link_mouse_clicked(url_satellite_image);
  }

  public void determine_satellite_image_url_NOAA(String satellite_image_mode) {
    //
    // Build a Worldview URL.
    //
    // @param centerLat Center latitude in degrees
    // @param centerLon Center longitude in degrees
    // @param halfWidthDeg Half-width of bounding box in degrees (controls zoom)
    // @param halfHeightDeg Half-height of bounding box in degrees (controls zoom)
    // @param layer Layer string, e.g., "GOES16:ABI-L2-CMIPF"
    // @param time Optional UTC time in format "YYYY-MM-DDTHH:MM:SSZ", or null for latest
    // @return Constructed Worldview URL
    //
    //
    //
    // NB For real-time frames, black areas happen a lot
    //
    //   - Cropping or viewport changes never fix it
    //   - Layer switching rarely fixes it
    //   - This problem is specific to Worldview tile service
    //

    int int_lat_degrees = 0;
    int int_lon_degrees = 0;
    double halfWidthDeg = 90.0; // ~zoom level
    double halfHeightDeg = 90.0; // ~zoom level
    String layer = "GOES16:ABI-L2-CMIPF"; // default the standard IR
    double centerLat = 0.0;
    double centerLon = 0.0;

    if (satellite_image_mode.equals(main.SATELLITE_IR_IMAGE)) {
      layer = "GOES16:ABI-L2-CMIPF";
      // layer = "GOES16:ABI-L2-MCMIPF";                  // The MCMIPF layers are multi-channel
      // composite products and are usually delivered with fewer or no tile gaps NO effect
    } else if (satellite_image_mode.equals(main.SATELLITE_SST_IMAGE)) {
      // layer = "GHRSST_L4_MUR_SST"; //"GOES16:ABI-L2-RadC02";
      layer = ""; // "GOES16:ABI-L2-RadC02";
    }

    // Latitude
    //
    if (myposition.latitude_degrees == null) {
      throw new NullPointerException("latitude_degrees");
    }
    if (!myposition.latitude_degrees.isEmpty() && myposition.latitude_hemisphere == null) {
      throw new NullPointerException("latitude_hemisphere");
    }
    int_lat_degrees =
        parseSatelliteCoordinate(
            myposition.latitude_degrees,
            myposition.latitude_hemisphere,
            myposition.HEMISPHERE_SOUTH);

    // Longitude
    //
    if (myposition.longitude_degrees == null) {
      throw new NullPointerException("longitude_degrees");
    }
    if (!myposition.longitude_degrees.isEmpty() && myposition.longitude_hemisphere == null) {
      throw new NullPointerException("longitude_hemisphere");
    }
    int_lon_degrees =
        parseSatelliteCoordinate(
            myposition.longitude_degrees,
            myposition.longitude_hemisphere,
            myposition.HEMISPHERE_WEST);

    //
    // bounding box
    //
    // - Even if the user is near the poles or the dateline, the bounding box will never produce
    // values outside the allowed world limits.
    // - This handles cases where your half-width or half-height zoom factor makes the bounding box
    // exceed the map envelope.
    //
    centerLon = int_lon_degrees;
    centerLat = int_lat_degrees;

    // ensure center coordinates remain valid
    centerLat = Math.max(-90.0, Math.min(90.0, centerLat));
    centerLon = Math.max(-180.0, Math.min(180.0, centerLon));

    double minLon = centerLon - halfWidthDeg;
    double maxLon = centerLon + halfWidthDeg;
    double minLat = centerLat - halfHeightDeg;
    double maxLat = centerLat + halfHeightDeg;

    // clamp bounding box to valid geographic limits
    minLat = Math.max(-90.0, minLat);
    maxLat = Math.min(90.0, maxLat);
    minLon = Math.max(-180.0, minLon);
    maxLon = Math.min(180.0, maxLon);

    String vParam = String.format("%.6f,%.6f,%.6f,%.6f", minLon, minLat, maxLon, maxLat);

    // compile complete URL string
    //    NB if a colour layer the visible layer 'menuitem' will not be visible in the left
    // worldview panel
    //    NB but a colour layer is need otherwise no image visible...
    //    Layer stacking: TrueColor first, then IR on top, then reference layers?
    //
    StringBuilder url = new StringBuilder("https://worldview.earthdata.nasa.gov/?");
    url.append("v=").append(vParam);
    url.append("&l=").append(layer);
    if (satellite_image_mode.equals(main.SATELLITE_IR_IMAGE)) {
      url.append(
          ",MODIS_Terra_CorrectedReflectance_TrueColor"); // trueColor gives land/sea reference even
      // if the main layer is semi-transparent
    } else if (satellite_image_mode.equals(main.SATELLITE_SST_IMAGE)) {
      url.append(",MODIS_Aqua_L2_Sea_Surface_Temp_Night,MODIS_Aqua_L2_Sea_Surface_Temp_Day");
      // url.append(",MODIS_Aqua_L3_SST_Thermal_4km_Night_Daily,MODIS_Aqua_L3_SST_Thermal_4km_Day_Daily);      // not working nov 2025
    }
    url.append(
        ",Reference_Labels,Reference_Features"); // with no reference layers, the map may appear
    // empty, dark, or “not loaded yet”
    // url.append("&t=now");                                    // force timestamp-independent
    // latest image, not necessary
    // url.append("&pt=").append("52.0,6.0,10,FF0000,ship");    // altough mentioned a few times in
    // docs it is not working nov 2025
    url.append("&al=false"); // clear all previous stored layers

    // invoke, in the default web browser, the url to the satellite image
    //
    main.satellite_link_mouse_clicked(url.toString());
  }

  public void log_memory_statistics() {
    // called from: - read_muffin() [main.java]
    //              - lees_configuratie_regels()[main.java]
    //              - main_windowClosing()[main.java]

    // print memory statistics
    // (https://stackoverflow.com/questions/3571203/what-are-runtime-getruntime-totalmemory-and-freememory)
    //
    // https://stackoverflow.com/questions/4667483/how-is-the-default-java-heap-size-determined
    //
    // NB max memory   = used memory + free memory + unlocated memory
    //    total memory = used memory + free memory

    // Total designated memory, this will equal the configured -Xmx value:
    long total_designated_memory = Runtime.getRuntime().maxMemory() / 1024 / 1024; // MB

    // Current allocated free memory, is the current allocated space ready for new objects. Caution
    // this is not the total free available memory:
    long current_allocated_free_memory = Runtime.getRuntime().freeMemory() / 1024 / 1024; // MB

    // Total allocated memory, is the total allocated space reserved for the java process:
    long total_allocated_memory = Runtime.getRuntime().totalMemory() / 1024 / 1024;

    // Used memory, has to be calculated:
    long used_memory = total_allocated_memory - current_allocated_free_memory;

    // Total free designated memory, has to be calculated:
    long total_free_designated_memory = total_designated_memory - used_memory;

    String memory_summary =
        "JVM: total designated memory = "
            + total_designated_memory
            + " MB; "
            + "current allocated free memory = "
            + current_allocated_free_memory
            + " MB; "
            + "total allocated memory = "
            + total_allocated_memory
            + " MB; "
            + "used memory = "
            + used_memory
            + " MB; "
            + "total free designated memory = "
            + total_free_designated_memory
            + " MB";
    log_turbowin_system_message("[GENERAL] " + memory_summary);
    // log_turbowin_system_message("[GENERAL] JVM total designated memory = " +
    // total_designated_memory + " MB");
    // log_turbowin_system_message("[GENERAL] JVM current allocated free memory = " +
    // current_allocated_free_memory + " MB");
    // log_turbowin_system_message("[GENERAL] JVM total allocated memory = " +
    // total_allocated_memory + " MB");
    // log_turbowin_system_message("[GENERAL] JVM used memory = " + used_memory + " MB");
    // log_turbowin_system_message("[GENERAL] JVM total free memory = " + total_free_memory + "
    // MB");
  }

  public void log_java_version() {
    // called from: - read_muffin()
    //              - lees_configuratie_regels()

    String java_version = System.getProperty("java.runtime.version");
    String java_name = System.getProperty("java.vm.name");

    log_turbowin_system_message("[GENERAL] Java: " + java_version + "; " + java_name);
  }

  public boolean position_sequence_check() {
    // initialisation
    boolean time_sequence_checks_ok = true;
    boolean doorgaan = true;
    boolean string_num_converions_ok = true;
    int num_vorige_obs_jaar = 0;
    int num_vorige_obs_maand = 0;
    int num_vorige_obs_dag = 0;
    int num_vorige_obs_uur = 0;
    int num_quadrant = 0;
    float num_latitude = 0;
    float num_longitude = 0;

    //
    ////////////////// first determine the substrings and convert them to numerical values
    //
    if (main.last_record.length() >= 19) {
      /* string date time from previous obs (last stored record) */
      String jaar = main.last_record.substring(1, 5); // cpp: jaar  = last_record.substr(1, 4);
      String maand = main.last_record.substring(5, 7); // cpp: maand = last_record.substr(5, 2);
      String dag = main.last_record.substring(7, 9); // cpp: dag   = last_record.substr(7, 2);
      String uur = main.last_record.substring(9, 11); // cpp: uur   = last_record.substr(9, 2);

      /* obs code position from previous obs (last stored record) */
      String quadrant =
          main.last_record.substring(
              11,
              12); // cpp: quadrant  = last_record.substr(11, 1);                       // WMO code
      // table 3333
      String latitude =
          main.last_record.substring(
              12,
              15); // cpp: latitude  = last_record.substr(12, 3);                       // tenths of
      // degrees
      String longitude =
          main.last_record.substring(
              15,
              19); // cpp: longitude = last_record.substr(15, 4);                       // tenths of
      // degrees

      /* date/time of last stored record convertion to numerical values */
      try {
        num_vorige_obs_jaar =
            Integer.valueOf(jaar.trim()); // cpp: num_vorige_obs_jaar = atoi(jaar.c_str());
      } catch (NumberFormatException ex) {
        System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
        string_num_converions_ok = false;
      }
      try {
        num_vorige_obs_maand =
            Integer.valueOf(maand.trim()); // cpp: num_vorige_obs_maand = atoi(maand.c_str());
      } catch (NumberFormatException ex) {
        System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
        string_num_converions_ok = false;
      }
      try {
        num_vorige_obs_dag =
            Integer.valueOf(dag.trim()); // cpp: num_vorige_obs_dag   = atoi(dag.c_str());
      } catch (NumberFormatException ex) {
        System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
        string_num_converions_ok = false;
      }
      try {
        num_vorige_obs_uur =
            Integer.valueOf(uur.trim()); // cpp: num_vorige_obs_uur   = atoi(uur.c_str());
      } catch (NumberFormatException ex) {
        System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
        string_num_converions_ok = false;
      }

      /* positie uit laatste record omzetten naar num_waarden + afronden + "+/-" (afh. quadrant) maken */
      try {
        num_quadrant =
            Integer.valueOf(quadrant.trim()); // cpp: num_quadrant  = atoi(quadrant.c_str());
      } catch (NumberFormatException ex) {
        System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
        string_num_converions_ok = false;
      }
      try {
        num_latitude =
            Float.valueOf(latitude.trim()); // cpp: num_latitude  = atoi(latitude.c_str());
      } catch (NumberFormatException ex) {
        System.out.println("+++ Error function: position_sequence_check(). " + ex.toString());
        string_num_converions_ok = false;
      }
      try {
        num_longitude =
            Float.valueOf(longitude.trim()); // cpp: num_longitude = atoi(longitude.c_str());
      } catch (NumberFormatException ex) {
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
    if (string_num_converions_ok == true) {
      float num_vorige_obs_breedte = num_latitude / 10; // nu in graden en tienden
      float num_vorige_obs_lengte = num_longitude / 10; // nu in graden en tienden

      if (num_quadrant == 3 || num_quadrant == 5) // Zuiderbreedte
      {
        num_vorige_obs_breedte *= -1;
      }
      if (num_quadrant == 5 || num_quadrant == 7) // Westerlengte
      {
        num_vorige_obs_lengte *= -1;
      }

      /*
      // compare date/time with the date/time of the previous obs
      */

      System.out.println("--- comparing date/time of entered obs with date/time of last saved obs");

      /* date-time previous saved obs */
      Calendar calendar_vorige_obs =
          new GregorianCalendar(
              num_vorige_obs_jaar,
              num_vorige_obs_maand - 1,
              num_vorige_obs_dag,
              num_vorige_obs_uur,
              0); // Month value is 0-based. e.g., 0 for January.

      /* date-time present obs */
      int num_huidige_obs_jaar = Integer.valueOf(mydatetime.year.trim());
      int num_huidige_obs_maand =
          Integer.valueOf(
              mydatetime.MM_code.trim()); // NB do not use mydatetime.month because = February etc.
      int num_huidige_obs_dag = Integer.valueOf(mydatetime.day.trim());
      int num_huidige_obs_uur = Integer.valueOf(mydatetime.hour.trim());

      Calendar calendar_huidige_obs =
          new GregorianCalendar(
              num_huidige_obs_jaar,
              num_huidige_obs_maand - 1,
              num_huidige_obs_dag,
              num_huidige_obs_uur,
              0); // Month value is 0-based. e.g., 0 for January.

      if (calendar_huidige_obs.compareTo(calendar_vorige_obs) <= 0) {
        SimpleDateFormat sdf2;
        sdf2 = new SimpleDateFormat("MMMM dd, yyyy HH"); // e.g "MMMM dd, yyyy" -> februari 27, 2010

        String string_calendar_huidige_obs = sdf2.format(calendar_huidige_obs.getTime());

        String info = "";
        info = "-time sequence check-\n";
        info += "obs date/time";
        info += " (";
        info += string_calendar_huidige_obs;
        info += ".00 UTC";
        info += ")";

        if (JOptionPane.showConfirmDialog(
                null, info, main.APPLICATION_NAME + " please confirm", JOptionPane.YES_NO_OPTION)
            == JOptionPane.NO_OPTION) {
          // MessageBox("Please correct the error (no final obs was coded)", "TurboWin message",
          // MB_OK);
          JOptionPane.showMessageDialog(
              null,
              "Please correct the error (no final obs was coded)",
              main.APPLICATION_NAME + " warning",
              JOptionPane.WARNING_MESSAGE);
          doorgaan = false;
          time_sequence_checks_ok = false;
        }
      } // if (calendar_huidige_obs.compareTo(calendar_vorige_obs) < 0)

      /* present obs position compared to the position of the previous obs */
      if (doorgaan) {
        System.out.println("--- comparing position of entered obs with position of last saved obs");

        /* first determine the time diff between present and previous obs */
        long obs_verschil_uur =
            (calendar_huidige_obs.getTimeInMillis() - calendar_vorige_obs.getTimeInMillis())
                / 3600000; // 3600000 = 1000 * 60 * 60 = 1 uur

        if (obs_verschil_uur
            >= 0) // alleen verdere checks als huidige obs datum/tijd is later dan vorige obs
        // datum/tijd
        {
          /* NB er wordt als grens genomen dat er max 30 mijl per uur afgelegd kan zijn */
          /* NB for testing see e.g.: http://williams.best.vwh.net/gccalc.htm */
          int afstand_vorige_huidige_obs =
              bepaal_afstand_huidige_obs_pos_tot_vorige_obs_pos(
                  num_vorige_obs_breedte, num_vorige_obs_lengte);

          // JOptionPane.showMessageDialog(null, afstand_vorige_huidige_obs,  main.APPLICATION_NAME
          // + " afstand tot vorige obs", JOptionPane.WARNING_MESSAGE);

          if (((obs_verschil_uur >= 0 && obs_verschil_uur <= 6)
                  && (afstand_vorige_huidige_obs > 180))
              || ((obs_verschil_uur > 6 && obs_verschil_uur <= 12)
                  && (afstand_vorige_huidige_obs > 360))
              || ((obs_verschil_uur > 12 && obs_verschil_uur <= 18)
                  && (afstand_vorige_huidige_obs > 540))
              || ((obs_verschil_uur > 18 && obs_verschil_uur <= 24)
                  && (afstand_vorige_huidige_obs > 720))) {
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

            if (JOptionPane.showConfirmDialog(
                    null,
                    info,
                    main.APPLICATION_NAME + " please confirm",
                    JOptionPane.YES_NO_OPTION)
                == JOptionPane.NO_OPTION) {
              JOptionPane.showMessageDialog(
                  null,
                  "Please correct the obs position (no final obs was coded)",
                  main.APPLICATION_NAME + " message",
                  JOptionPane.WARNING_MESSAGE);
              doorgaan = false;
              time_sequence_checks_ok = false;
            }
          } // if ( ((obs_verschil_uur > 0 && obs_verschil <= 6) etc.
        } // if (obs_verschil_uur > 0)
      } // if (doorgaan)
    } // if (string_num_converions_ok == true)

    return time_sequence_checks_ok;
  }

  private int bepaal_afstand_huidige_obs_pos_tot_vorige_obs_pos(
      double num_vorige_obs_breedte, double num_vorige_obs_lengte) {
    /* used formula :                                                  */
    /* cos_AB = sin_bA * sin_bB + cos_bA * cos_bB * cos_delta_l_AB     */
    /*                                                                 */
    /* distance = 60 arccos(cos_AB)                                    */
    /*                                                                 */
    /* from degrees angle to radians angle : graden * 60 * boogminuut  */

    // constants
    final int westgrens_POR = 90;
    final int oostgrens_POR = -90;
    final double boogminuut = 0.0002908882;
    final double h180pi = 3437.746771;

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

    num_huidige_obs_breedte =
        (double) myposition.int_latitude_degrees + ((double) myposition.int_latitude_minutes / 60);
    num_huidige_obs_lengte =
        (double) myposition.int_longitude_degrees
            + ((double) myposition.int_longitude_minutes / 60);

    if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true) {
      num_huidige_obs_breedte *= -1;
    }

    if (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true) {
      num_huidige_obs_lengte *= -1;
    }

    /* due to 180 degrees passage in the POR */
    // if ((num_huidige_obs_lengte >= westgrens_POR) && (num_huidige_obs_lengte <= oostgrens_POR))
    // // komt hier nooit in ????

    // if ( (num_huidige_obs_lengte >= westgrens_POR && num_huidige_obs_lengte <= 180) ||      //
    // westgrens_por +90
    //     (num_huidige_obs_lengte >= -180 && num_huidige_obs_lengte <= oostgrens_POR) )      //
    // oostgrens_por -90
    huidige_obs_in_por =
        (num_huidige_obs_lengte >= -180 && num_huidige_obs_lengte <= oostgrens_POR)
            || (num_huidige_obs_lengte >= westgrens_POR
                && num_huidige_obs_lengte <= 180); // westgrens_por +90 // oostgrens_por -90

    if (huidige_obs_in_por == true) {
      if (num_huidige_obs_lengte < 0) num_huidige_obs_lengte += 360;
      if (num_vorige_obs_lengte < 0) num_vorige_obs_lengte += 360;
    } // if (huidige_obs_in_por == true)

    /* determine longitude difference */
    delta_l_ab = num_huidige_obs_lengte - num_vorige_obs_lengte;

    /* longitude difference > 180: do not compute but give MAX_VALUE (> 180 gives issues in formula) */
    if (Math.abs(delta_l_ab) > 180) {
      afstand = Integer.MAX_VALUE;
    } else // longitude difference < 180
    {
      /* the (greatcircle) computation */
      sin_breedte_a = Math.sin(num_vorige_obs_breedte * 60 * boogminuut);
      sin_breedte_b = Math.sin(num_huidige_obs_breedte * 60 * boogminuut);
      cos_breedte_a = Math.cos(num_vorige_obs_breedte * 60 * boogminuut);
      cos_breedte_b = Math.cos(num_huidige_obs_breedte * 60 * boogminuut);
      cos_delta_l_ab = Math.cos(delta_l_ab * 60 * boogminuut);

      /* first test acos argument to prevent ACOS domain error (if 2x exact the same position + obs to screen)*/
      acos_argument =
          sin_breedte_a * sin_breedte_b + cos_breedte_a * cos_breedte_b * cos_delta_l_ab;
      if (acos_argument <= -1 || acos_argument >= 1) {
        afstand = 0;
      } else {
        afstand =
            (int)
                Math.round(
                    h180pi
                        * Math.acos(
                            acos_argument)); // Returns the closest int to the argument (zelde als
        // (int)Math.floor(a + 0.5f)
      }
    } // else (longitude difference < 180)

    return afstand;
  }

  public boolean Move_log_files(final String move_mode_logs) {
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

    boolean doorgaan = false;
    boolean doorgaan_captain = true;

    /* first check if there is an immt log source file present (and not empty) */
    main.volledig_path_srcFilename_immt = logs_dir + java.io.File.separator + IMMT_LOG;
    File immt_source_file = new File(main.volledig_path_srcFilename_immt);
    if (immt_source_file.exists() && immt_source_file.length() > 10) {
      doorgaan = true;
    } else {
      JOptionPane.showMessageDialog(
          null,
          "Move log files cancelled, reason: nothing to move; IMMT log (file with all stored observations for climatological use) empty ",
          main.APPLICATION_NAME + " error",
          JOptionPane.WARNING_MESSAGE);
      doorgaan = false;
    }

    /* OK immt log present, so continue */

    if (doorgaan == true) {
      /* in MOVE_TO_DISK mode filechooser dialog popup */
      if (move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
        // pop-up the file/directory chooser dialog box
        // JFileChooser chooser = new JFileChooser(".");
        //             // present dir
        // JFileChooser chooser = new
        // JFileChooser(javax.swing.filechooser.FileSystemView.getFileSystemView() ); // Constructs
        // a JFileChooser using the given FileSystemView
        JFileChooser chooser =
            new JFileChooser(); // Constructs a JFileChooser pointing to the user's default
        // directory. This default depends on the operating system. It is
        // typically the "My Documents" folder on Windows, and the user's
        // home directory on Unix.
        chooser.setFileSelectionMode(
            JFileChooser.DIRECTORIES_ONLY); // now the user can only select directories
        // int result = chooser.showSaveDialog(main.this);
        int result = chooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
          output_dir =
              chooser
                  .getSelectedFile()
                  .getPath(); // getSelectedFile() -> in this case returns not a file but a
          // directory !

          final File dirs = new File(output_dir);

          if (dirs.exists() == false) // output_dir not exists
          {
            final boolean success = dirs.mkdirs();
            if (success == false) {
              JOptionPane.showMessageDialog(
                  null,
                  "Could not create " + output_dir,
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
              doorgaan = false;
            }
          } // else destination path do not exist

          // output_dir and log_dir must be different !
          if (doorgaan == true) {
            if (output_dir.equals(logs_dir) == true) {
              JOptionPane.showMessageDialog(
                  null,
                  "Download folder the same as log files folder, LOG FILES NOT MOVED ",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);

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
      else if (move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
        output_dir = main.temp_logs_dir;
      } // else if (move_mode_logs.equals(MOVE_TO_EMAIL) == true)
    } // if doorgaan == true

    /*
    // move captain source file to captain destination file (if captain source file is present)
    */
    if (doorgaan == true) {
      /* captain distination file (eg PGDE_captain.log) */
      // volledig_path_dstFilename_captain = output_dir + java.io.File.separator + CAPTAIN_LOG;
      // volledig_path_dstFilename_captain = output_dir + java.io.File.separator + call_sign + "_" +
      // CAPTAIN_LOG;
      main.volledig_path_dstFilename_captain =
          output_dir + java.io.File.separator + station_ID + "_" + CAPTAIN_LOG;

      /* captain source file (captain.log) */
      main.volledig_path_srcFilename_captain = logs_dir + java.io.File.separator + CAPTAIN_LOG;

      /* captain backup file (eg PGDE_CAPTAIN_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd =
          new GregorianCalendar(
              new SimpleTimeZone(0, "UTC")); // gives system date and time (UTC) of this moment
      SimpleDateFormat sdf2;
      sdf2 = new SimpleDateFormat("MMMM dd, yyyy"); // e.g "MMMM dd, yyyy" -> februari 27, 2010
      String systeem_date_time = sdf2.format(cal_systeem_datum_tijd.getTime());
      // volledig_path_backup_srcFilename_captain = logs_dir + java.io.File.separator +
      // "CAPTAIN_BACKUP " + systeem_date_time + ".TXT";
      // volledig_path_backup_srcFilename_captain = logs_dir + java.io.File.separator + call_sign +
      // "_" + "CAPTAIN_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_captain =
          logs_dir
              + java.io.File.separator
              + station_ID
              + "_"
              + "CAPTAIN_BACKUP "
              + systeem_date_time
              + ".TXT";

      File captain_source_file = new File(main.volledig_path_srcFilename_captain);
      if (captain_source_file.exists()) {
        doorgaan_captain = captain_source_file.length() > 5;
      } else // no captain source file present
      {
        // NB no message necessary, because captain data (file) not mandatory (contrary to immt log)
        doorgaan_captain = false;
      }
    } // if (doorgaan == true)

    if (doorgaan == true
        && doorgaan_captain == true
        && move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
      CaptainLogMoveWorkflow.start(
          main.volledig_path_srcFilename_captain,
          main.volledig_path_dstFilename_captain,
          () -> handleCaptainMoveSuccess(move_mode_logs));
    } // if (doorgaan == true && doorgaan_captain == true &&
    // move_mode_logs.equals(main.MOVE_TO_DISK) == true)

    if (doorgaan == true
        && doorgaan_captain == true
        && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
      // NB do not use swingworker in case of email send, due to possible synchronisation issue (zip
      // attachement not created on time)

      /* copy captain source file to destination captain file */
      String result =
          LogFileCopyWorkflow.copy(
              main.volledig_path_srcFilename_captain, main.volledig_path_dstFilename_captain);

      if (result.equals("NOT_OK") == true) {
        JOptionPane.showMessageDialog(
            null,
            "Unable to move "
                + main.volledig_path_srcFilename_captain
                + " to "
                + main.volledig_path_dstFilename_captain,
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
      if (result.equals("OK") == true) {
        handleCaptainMoveSuccess(move_mode_logs);
      } // if (result_opgehaald.equals("OK") == true)
    } // if (doorgaan == true && doorgaan_captain == true &&
    // move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)

    /*
    // copy immt source file to destination and make a backup of the immt file
    //
    // + copy observer data plus number of observations (via function Kopieeren_Waarnemers_En_Aantallen())
    */
    if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
      /* immt distination file (eg PGDE_immt.log) */
      // volledig_path_dstFilename_immt = output_dir + java.io.File.separator + IMMT_LOG;
      // volledig_path_dstFilename_immt = output_dir + java.io.File.separator + call_sign + "_" +
      // IMMT_LOG;
      main.volledig_path_dstFilename_immt =
          output_dir + java.io.File.separator + station_ID + "_" + IMMT_LOG;

      /* immt source file (immt.log) */
      main.volledig_path_srcFilename_immt = logs_dir + java.io.File.separator + IMMT_LOG;

      /* immt backup file (eg PGDE_IMMT_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd =
          new GregorianCalendar(
              new SimpleTimeZone(0, "UTC")); // gives system date and time (UTC) of this moment
      SimpleDateFormat sdf2;
      sdf2 = new SimpleDateFormat("MMMM dd, yyyy"); // e.g "MMMM dd, yyyy" -> februari 27, 2010
      String systeem_date_time = sdf2.format(cal_systeem_datum_tijd.getTime());
      // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP "
      // + systeem_date_time + ".TXT";
      // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + call_sign + "_"
      // + "IMMT_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_immt =
          logs_dir
              + java.io.File.separator
              + station_ID
              + "_"
              + "IMMT_BACKUP "
              + systeem_date_time
              + ".TXT";

      ImmtLogMoveWorkflow.start(
          main.volledig_path_srcFilename_immt,
          main.volledig_path_dstFilename_immt,
          () -> handleImmtMoveSuccess(move_mode_logs));
    } // if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_DISK) == true)

    if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
      // NB do not use swingworker in case of email send, due to possible synchronisation issue (zip
      // attachement not created on time)

      /* immt distination file (eg PGDE_immt.log) */
      // volledig_path_dstFilename_immt = output_dir + java.io.File.separator + IMMT_LOG;
      // volledig_path_dstFilename_immt = output_dir + java.io.File.separator + call_sign + "_" +
      // IMMT_LOG;
      main.volledig_path_dstFilename_immt =
          output_dir + java.io.File.separator + station_ID + "_" + IMMT_LOG;

      /* immt source file (immt.log) */
      main.volledig_path_srcFilename_immt = logs_dir + java.io.File.separator + IMMT_LOG;

      /* immt backup file (eg PGDE_IMMT_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd =
          new GregorianCalendar(
              new SimpleTimeZone(0, "UTC")); // gives system date and time (UTC) of this moment
      SimpleDateFormat sdf2;
      sdf2 = new SimpleDateFormat("MMMM dd, yyyy"); // e.g "MMMM dd, yyyy" -> februari 27, 2010
      String systeem_date_time = sdf2.format(cal_systeem_datum_tijd.getTime());
      // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP "
      // + systeem_date_time + ".TXT";
      // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + call_sign + "_"
      // + "IMMT_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_immt =
          logs_dir
              + java.io.File.separator
              + station_ID
              + "_"
              + "IMMT_BACKUP "
              + systeem_date_time
              + ".TXT";

      /*
      // count number of obs per observer per year
      */
      support_class.Kopieeren_Waarnemers_En_Aantallen();

      /*
      // copy immt
      */
      String result =
          LogFileCopyWorkflow.copy(
              main.volledig_path_srcFilename_immt, main.volledig_path_dstFilename_immt);

      if ((result.equals("NOT_OK") == true)) {
        JOptionPane.showMessageDialog(
            null,
            "Unable to move "
                + main.volledig_path_srcFilename_immt
                + " to "
                + main.volledig_path_dstFilename_immt,
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
      if ((result.equals("OK") == true)) {
        handleImmtMoveSuccess(move_mode_logs);
      } // if (result_opgehaald.equals("OK") == true)
    } // if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)

    return doorgaan;
  }

  private void handleCaptainMoveSuccess(final String move_mode_logs) {
    File source_file = new File(main.volledig_path_srcFilename_captain);
    File renamed_file = new File(main.volledig_path_backup_srcFilename_captain);

    // Rename the source only after the copy has completed successfully.
    if (source_file.renameTo(renamed_file) == false) {
      // A same-day backup may already exist, causing the rename to fail.
      if (move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
        JOptionPane.showMessageDialog(
            null,
            "Backing up log file "
                + main.volledig_path_srcFilename_captain
                + " failed (2nd move/backup same day?)",
            main.APPLICATION_NAME + " info",
            JOptionPane.INFORMATION_MESSAGE);
      }
      // Delete the source so it cannot be mistaken for the active captain log after backup fails.
      source_file.delete();
    }
  }

  private void handleImmtMoveSuccess(final String move_mode_logs) {
    if (move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
      // Show this message only for disk moves. Do not show it when the files are zipped for email:
      // displaying it can delay zip creation even though the email program has already been opened
      // with a reference to that zip file.
      String info = "meteo log files moved to folder: " + output_dir;
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
      main.log_turbowin_system_message("[GENERAL] " + info);
    }

    // rename sourcefile to backup file (after it was copied)
    File source_file = new File(main.volledig_path_srcFilename_immt);
    File renamed_file = new File(main.volledig_path_backup_srcFilename_immt);

    if (source_file.renameTo(renamed_file) == false) {
      // Backup failures are user-visible in both disk and email modes, preserving legacy behavior.
      if (move_mode_logs.equals(main.MOVE_TO_DISK) == true
          || move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
        JOptionPane.showMessageDialog(
            null,
            "Backing up log file "
                + main.volledig_path_srcFilename_immt
                + " failed (2nd move/backup same day?)",
            main.APPLICATION_NAME + " info",
            JOptionPane.INFORMATION_MESSAGE);
      }
      source_file.delete();
    }

    if (move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
      // Zip only after the asynchronous move completes to avoid an I/O race or incomplete archive.
      zip_log_files();
    }

    String info =
        "Clearing all the data of the observers (surname, full initials/full christian name, rank, discharge book number)";
    if (JOptionPane.showConfirmDialog(
            null,
            info,
            main.APPLICATION_NAME + " message",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.YES_OPTION) {
      String volledig_path_observer = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;
      try {
        FileChannel.open(Paths.get(volledig_path_observer), StandardOpenOption.WRITE)
            .truncate(0)
            .close();
        JOptionPane.showMessageDialog(
            null,
            "Successfully cleared all the data of the observers",
            APPLICATION_NAME + " message",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(
            null,
            "Clearing all the data of the observers failed",
            APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
    }
  }

  private void zip_log_files() {
    File file_logs_dir = new File(temp_logs_dir /*+ java.io.File.separator*/);
    String[] filenames =
        file_logs_dir.list(); // Returns an array of strings naming the files and directories in the
    // directory denoted by this abstract pathname.

    for (int i = 0; i < filenames.length; i++) {
      filenames[i] = temp_logs_dir + java.io.File.separator + filenames[i];
    }

    // Create a buffer for reading the files
    byte[] buf = new byte[1024];

    try {
      // Create the ZIP file
      // String outFilename = "C:/Users/User/Downloads/logs/temp/logs.zip";
      // String outFilename = "C:/Users/User/Documents/logs.zip";
      String outFilename =
          temp_logs_dir
              + java.io.File.separator
              + ship_name
              + " "
              + main.LOGS_ZIP; // e.g. "C:/Users/User/Downloads/logs/temp/happy sailor logs.zip";
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

      // Compress the files
      for (int i = 0; i < filenames.length; i++) {
        // JOptionPane.showMessageDialog(null, filenames[i] , APPLICATION_NAME + " test",
        // JOptionPane.WARNING_MESSAGE);
        File te_zippen_file = new File(filenames[i]);
        if (te_zippen_file.isDirectory()
            == false) // ABSOLUUT GEEN DIRECTORIES, WANT DAN INVALID ARCHIEF
        {
          FileInputStream in = new FileInputStream(filenames[i]);

          // Add ZIP entry to output stream.
          try {
            // out.putNextEntry(new ZipEntry(filenames[i]));
            // out.putNextEntry(new ZipEntry(Path.GetFileName(filenames[i]))); // Path.GetFileName
            // -> to prevent full paths are included
            out.putNextEntry(
                new ZipEntry(
                    te_zippen_file.getName())); // getName -> to prevent full paths are included

            //// out.setLevel(Deflater.DEFAULT_COMPRESSION);
          } catch (ZipException ex) {
            JOptionPane.showMessageDialog(
                null,
                "zip error (Maintenance_Move_log_files_by_email_actionPerformed)",
                APPLICATION_NAME + " error",
                JOptionPane.ERROR_MESSAGE);
          }

          // Transfer bytes from the input file to the ZIP file
          int len;
          while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
          }

          // Complete the entry
          out.closeEntry();
          in.close();
        } // if (test.isDirectory() == false)
      } // for (int i = 0; i < filenames.length; i++)

      // Complete the ZIP file
      out.close();

    } // try
    catch (IOException e) {
      JOptionPane.showMessageDialog(
          null,
          "i/o zip error (Maintenance_Move_log_files_by_email_actionPerformed)",
          APPLICATION_NAME + " error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  public void Kopieeren_Waarnemers_En_Aantallen() {
    // called from: Move_log_files()-- doInbackground---  [main_support.java]

    /*
    // checking which years are present in immt.txt staan bv 2003, 2004 en 2005
    */

    int posi;
    int w;
    int immt_position_observer;
    int[][] aantal_waarnemer = new int[main.MAX_AANTAL_JAREN_IN_IMMT][main.MAX_AANTAL_WAARNEMERS];
    boolean jaar_substring_al_aanwezig;
    String record;
    String volledig_path_immt = logs_dir + java.io.File.separator + IMMT_LOG;
    // String moved_observername_file                = output_dir + java.io.File.separator +
    // OBSERVER_LOG;
    // String moved_observername_file                = output_dir + java.io.File.separator +
    // call_sign + "_" + OBSERVER_LOG;
    String moved_observername_file =
        main.output_dir + java.io.File.separator + station_ID + "_" + OBSERVER_LOG;
    String[] backup_moved_observername_file_array = new String[main.MAX_AANTAL_JAREN_IN_IMMT];
    String[] moved_observername_file_array = new String[main.MAX_AANTAL_JAREN_IN_IMMT];
    String jaar_substring;
    String waarnemer_substring = "";
    String observername_office;
    String immt_version;
    BufferedWriter out_0 = null;
    BufferedWriter out_0_backup = null;
    BufferedWriter out_1 = null;
    BufferedWriter out_1_backup = null;
    BufferedWriter out_2 = null;
    BufferedWriter out_2_backup = null;
    BufferedWriter out_3 = null;
    BufferedWriter out_3_backup = null;
    BufferedWriter out_4 = null;
    BufferedWriter out_4_backup = null;

    /* initialisation */
    for (int p = 0; p < main.MAX_AANTAL_JAREN_IN_IMMT; p++) {
      main.jaar_substring_array[p] = "";
    } // lege array plaats maken

    /* read all lines/records from immt log */
    try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt))) {
      while ((record = in.readLine()) != null) {
        if (record.length()
            > main.IMMT_POSITION_IMMT_VERSION) // NB at least number greater than year in IMMT
        {
          jaar_substring = record.substring(1, 5); // eg 2006
          jaar_substring_al_aanwezig = false;

          // System.out.println("+++ jaar_substring = " + jaar_substring);

          for (int k = 0; k < main.MAX_AANTAL_JAREN_IN_IMMT; k++) {
            if (main.jaar_substring_array[k].equals(jaar_substring)) // this year was stored before
            {
              jaar_substring_al_aanwezig = true;
              break;
            }
          } // for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)

          if (jaar_substring_al_aanwezig == false) {
            for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++) {
              if (main.jaar_substring_array[m].equals("")) // empty array place
              {
                main.jaar_substring_array[m] = jaar_substring;
                break;
              }
            } // for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)
          } // if (jaar_substring_al_aanwezig == false)
        } // if (obs_immt.length() > IMMT_POSITION_IMMT_VERSION)
      } // while((record = in.readLine()) != null)
    } // try
    catch (IOException ex) {
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
    for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++) {
      moved_observername_file_array[j] = "";
      backup_moved_observername_file_array[j] = "";
    }

    /* positie bepalen waar jaartal tussengevoegd moet worden (bv A:\observer.log -> A:\observer_2006.log) */
    posi = moved_observername_file.indexOf(".log"); // nb variable pos wordt ook gebruikt in TPoint

    /* (absolute) filenamen van de download (en download backup) observer files bepalen */

    cal_systeem_datum_tijd =
        new GregorianCalendar(
            new SimpleTimeZone(0, "UTC")); // geeft systeem datum tijd in UTC van dit moment
    SimpleDateFormat sdf2;
    sdf2 = new SimpleDateFormat("MMMM dd, yyyy"); // e.g "MMMM dd, yyyy" -> februari 27, 2010
    String systeem_date_time = sdf2.format(cal_systeem_datum_tijd.getTime());

    // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP " +
    // systeem_date_time + ".TXT";

    for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++) {
      if (main.jaar_substring_array[j].compareTo("") != 0) {
        /* eg A:\PGDE_observer_2006.log + A:\PGDE_observer_2007.log) */
        moved_observername_file_array[j] =
            moved_observername_file.substring(0, posi)
                + "_"
                + main.jaar_substring_array[j]
                + ".log";
        /* bepalen naam van de observername backup file (altijd op een vaste plaats) */

        /* eg PGDE_OBSERVER_2006_BACKUP November 22, 2009.txt + PGDE_OBSERVER_2007_BACKUP November 22, 2009.txt */
        // backup_moved_observername_file_array[j] = logs_dir + java.io.File.separator + "OBSERVER_"
        // + jaar_substring_array[j] + "_BACKUP " + systeem_date_time + ".TXT";
        // backup_moved_observername_file_array[j] = logs_dir + java.io.File.separator + call_sign +
        // "_" + "OBSERVER_" + jaar_substring_array[j] + "_BACKUP " + systeem_date_time + ".TXT";
        backup_moved_observername_file_array[j] =
            logs_dir
                + java.io.File.separator
                + station_ID
                + "_"
                + "OBSERVER_"
                + main.jaar_substring_array[j]
                + "_BACKUP "
                + systeem_date_time
                + ".TXT";
      } // if (jaar_substring_array[k] != "")
    } // for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)

    /*
    // NB you know here that observer name file in this stage is present
    */

    /* initialisation */
    for (int b = 0; b < main.MAX_AANTAL_WAARNEMERS; b++) {
      main.observername_array[b] = "";
    }

    String volledig_path_observer = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;

    /* read all lines/records from observer log */
    try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_observer))) {
      w = 0;
      while ((record = in.readLine()) != null) {
        if (w < main.MAX_AANTAL_WAARNEMERS) // extra check
        {
          main.observername_array[w++] = record;
        }
      } // while((record = in.readLine()) != null)

    } // try
    catch (IOException ex) {
      // do nothing, possible file was never created
    } // catch

    /*
    // uitlezen van immt log file om het aantal waarnemingen per waarnemer te tellen
    // NB je weet zeker dat de immt.log in deze fase aanwezig is
    */

    for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++) {
      for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS; i++) {
        aantal_waarnemer[m][i] = 0;
      }
    }

    /* read all lines/records from immt log */
    try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt))) {
      while ((record = in.readLine()) != null) {
        /* eerst immt version bepalen want dan is pas bekend wat de positie van de waarnemer is */
        if (record.length() > main.IMMT_POSITION_IMMT_VERSION) {
          immt_version =
              record.substring(
                  main.IMMT_POSITION_IMMT_VERSION, main.IMMT_POSITION_IMMT_VERSION + 1);

          if (immt_version.equals("3") == true) {
            immt_position_observer = main.IMMT_3_POSITION_OBSERVER;
          } else if (immt_version.equals("4") == true) {
            immt_position_observer = main.IMMT_4_POSITION_OBSERVER;
          } else if (immt_version.equals("5") == true) {
            immt_position_observer = main.IMMT_5_POSITION_OBSERVER;
          } else {
            immt_position_observer =
                INVALID; //  dan zal verderop geen waarnemer uit de record worden gelezen
          }

          // if (record.length() > IMMT_POSITION_OBSERVER - 1)
          if (record.length() > immt_position_observer - 1) {
            waarnemer_substring =
                record.substring(
                    immt_position_observer); // record.substring(IMMT_POSITION_OBSERVER);
            jaar_substring = record.substring(1, 5); // eg 2010

            for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS; i++) {
              if (main.observername_array[i].compareTo("") != 0) {
                // NB waarnemer_substring  : surname - ; - initials (separated by .'s) - ; - rank -
                // ; - discharge book number
                // NB observername_array[i]: surname - ; - initials (separated by .'s) - ; - rank -
                // ; - discharge book number

                if (waarnemer_substring.equals(main.observername_array[i]) == true) {
                  for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++) {
                    if (jaar_substring.equals(main.jaar_substring_array[m])) {
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
    catch (IOException ex) {
      // do nothing, possible file was never created
    } // catch

    /*
    // observers and number of made observations to download and backup file(s)
    */

    /* open the moved(download) and backup observer files */
    if (moved_observername_file_array[0].compareTo("") != 0) {
      try {
        out_0 = new BufferedWriter(new FileWriter(moved_observername_file_array[0]));
        out_0_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[0]));
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[0].compareTo("") != 0)
    if (moved_observername_file_array[1].compareTo("") != 0) {
      try {
        out_1 = new BufferedWriter(new FileWriter(moved_observername_file_array[1]));
        out_1_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[1]));
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[2].compareTo("") != 0)
    if (moved_observername_file_array[2].compareTo("") != 0) {
      try {
        out_2 = new BufferedWriter(new FileWriter(moved_observername_file_array[2]));
        out_2_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[2]));
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[2].compareTo("") != 0)
    if (moved_observername_file_array[3].compareTo("") != 0) {
      try {
        out_3 = new BufferedWriter(new FileWriter(moved_observername_file_array[3]));
        out_3_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[3]));
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[3].compareTo("") != 0)
    if (moved_observername_file_array[4].compareTo("") != 0) {
      try {
        out_4 = new BufferedWriter(new FileWriter(moved_observername_file_array[4]));
        out_4_backup = new BufferedWriter(new FileWriter(backup_moved_observername_file_array[4]));
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[4].compareTo("") != 0)

    /* write to the moved and backup files */
    for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++) {
      for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS; i++) {
        observername_office = main.observername_array[i];

        if (observername_office.compareTo("") != 0) {
          // observername_office += "\t";                // tab
          observername_office +=
              Integer.toString(aantal_waarnemer[m][i]); // number of observations waarnemingen

          if (m == 0 && aantal_waarnemer[m][i] != 0) {
            try {
              if (out_0 != null) {
                out_0.write(observername_office);
                out_0.newLine();
              }
            } catch (IOException ex) {
            }
            try {
              if (out_0_backup != null) {
                out_0_backup.write(observername_office);
                out_0_backup.newLine();
              }
            } catch (IOException ex) {
            }
          } // if (m == 0 && aantal_waarnemer[m][i] != 0)
          else if (m == 1 && aantal_waarnemer[m][i] != 0) {
            try {
              if (out_1 != null) {
                out_1.write(observername_office);
                out_1.newLine();
              }
            } catch (IOException ex) {
            }
            try {
              if (out_1_backup != null) {
                out_1_backup.write(observername_office);
                out_1_backup.newLine();
              }
            } catch (IOException ex) {
            }
          } // else if (m == 2 && aantal_waarnemer[j][i] != 0)
          else if (m == 2 && aantal_waarnemer[m][i] != 0) {
            try {
              if (out_2 != null) {
                out_2.write(observername_office);
                out_2.newLine();
              }
            } catch (IOException ex) {
            }
            try {
              if (out_2_backup != null) {
                out_2_backup.write(observername_office);
                out_2_backup.newLine();
              }
            } catch (IOException ex) {
            }
          } // else if (m == 2 && aantal_waarnemer[m][i] != 0)
          else if (m == 3 && aantal_waarnemer[m][i] != 0) {
            try {
              if (out_3 != null) {
                out_3.write(observername_office);
                out_3.newLine();
              }
            } catch (IOException ex) {
            }
            try {
              if (out_3_backup != null) {
                out_3_backup.write(observername_office);
                out_3_backup.newLine();
              }
            } catch (IOException ex) {
            }
          } // else if (m == 3 && aantal_waarnemer[m][i] != 0)
          else if (m == 4 && aantal_waarnemer[m][i] != 0) {
            try {
              if (out_4 != null) {
                out_4.write(observername_office);
                out_4.newLine();
              }
            } catch (IOException ex) {
            }
            try {
              if (out_4_backup != null) {
                out_4_backup.write(observername_office);
                out_4_backup.newLine();
              }
            } catch (IOException ex) {
            }
          } // else if (m == 4 && aantal_waarnemer[m][i] != 0)
        } // if (observername_office != "")
      } // for (i = 0; i < MAX_AANTAL_WAARNEMERS; i++)
    } // for (int m = 0; m < MAX_AANTAL_JAREN_IN_IMMT; m++)

    /* close all the (moved and backup) observer files */
    if (moved_observername_file_array[0].compareTo("") != 0) {
      try {
        if (out_0 != null) {
          out_0.close();
        }
        if (out_0_backup != null) {
          out_0_backup.close();
        }
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[0].compareTo("") != 0)
    if (moved_observername_file_array[1].compareTo("") != 0) {
      try {
        if (out_1 != null) {
          out_1.close();
        }
        if (out_1_backup != null) {
          out_1_backup.close();
        }
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[2].compareTo("") != 0)
    if (moved_observername_file_array[2].compareTo("") != 0) {
      try {
        if (out_2 != null) {
          out_2.close();
        }
        if (out_2_backup != null) {
          out_2_backup.close();
        }
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[2].compareTo("") != 0)
    if (moved_observername_file_array[3].compareTo("") != 0) {
      try {
        if (out_3 != null) {
          out_3.close();
        }
        if (out_3_backup != null) {
          out_3_backup.close();
        }
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[3].compareTo("") != 0)
    if (moved_observername_file_array[4].compareTo("") != 0) {
      try {
        if (out_4 != null) {
          out_4.close();
        }
        if (out_4_backup != null) {
          out_4_backup.close();
        }
      } // try
      catch (Exception e) {
        /* ... */
      }
    } // if (moved_observername_file_array[4].compareTo("") != 0)
  }

  private Float parseValidationFloat(String value, String errorMessage) {
    try {
      if (value.equals("") == false && value != null) {
        return Float.parseFloat(value);
      }
    } catch (NumberFormatException ex) {
      main.log_turbowin_system_message(errorMessage);
    }
    return null;
  }

  static int parseSatelliteCoordinate(
      String degrees, String hemisphere, String negativeHemisphere) {
    if (degrees != null && !degrees.isEmpty() && hemisphere != null && !hemisphere.isEmpty()) {
      try {
        int coordinate = Integer.parseInt(degrees.trim());
        return hemisphere.equals(negativeHemisphere) ? coordinate * -1 : coordinate;
      } catch (NumberFormatException ex) {
        // Invalid coordinates use the existing default of zero.
      }
    }
    return 0;
  }

  private Integer parseValidationInt(String value, String errorMessage) {
    try {
      if (value.equals("") == false && value != null) {
        return Integer.parseInt(value);
      }
    } catch (NumberFormatException ex) {
      main.log_turbowin_system_message(errorMessage);
    }
    return null;
  }

  private Integer parseValidationFirstDigitInt(String value, String errorMessage) {
    try {
      if (value.equals("") == false && value != null) {
        return Integer.parseInt(value.substring(0, 1));
      }
    } catch (NumberFormatException ex) {
      main.log_turbowin_system_message(errorMessage);
    }
    return null;
  }

  private record WindWaveValidation(
      float period, float height, boolean periodValid, boolean heightValid) {}

  private record FloatValidation(float value, boolean valid) {}

  private record IntegerValidation(int value, boolean valid) {}

  private FloatValidation validateFloat(
      String value, String description, String validationFunction) {
    Float parsed =
        parseValidationFloat(
            value, "[GENERAL] " + description + "; Function: " + validationFunction + "()");
    return new FloatValidation(parsed == null ? main.INVALID : parsed, parsed != null);
  }

  private IntegerValidation validateInteger(
      String value, String description, String validationFunction) {
    Integer parsed =
        parseValidationInt(
            value, "[GENERAL] " + description + "; Function: " + validationFunction + "()");
    return new IntegerValidation(parsed == null ? main.INVALID : parsed, parsed != null);
  }

  private WindWaveValidation validateWindWaves(
      String validationFunction, boolean preserveEmptyHeightFlag) {
    Float parsedPeriod =
        parseValidationFloat(
            mywaves.wind_waves_period,
            "[GENERAL] wind waves period conversion error; Function: " + validationFunction + "()");
    Float parsedHeight =
        parseValidationFloat(
            mywaves.wind_waves_height,
            "[GENERAL] wind waves height conversion error; Function: " + validationFunction + "()");

    boolean periodValid = parsedPeriod != null;
    boolean heightValid = parsedHeight != null;
    if (preserveEmptyHeightFlag && mywaves.wind_waves_height.equals("")) {
      // Preserve the legacy level-3 flag assignment for an empty height.
      periodValid = false;
      heightValid = true;
    }

    return new WindWaveValidation(
        parsedPeriod == null ? main.INVALID : parsedPeriod,
        parsedHeight == null ? main.INVALID : parsedHeight,
        periodValid,
        heightValid);
  }

  public boolean checking_level_2() {
    boolean doorgaan = true;
    boolean level_2_ok = true;
    boolean wind_waves_period_conversion_ok = true;
    boolean wind_waves_height_conversion_ok = true;
    boolean pressure_amount_tendency_conversion_ok = true;
    boolean cl_code_conversion_ok = true;
    boolean cm_code_conversion_ok = true;
    boolean ch_code_conversion_ok = true;
    boolean ww_code_conversion_ok = true;
    boolean VV_code_conversion_ok = true;
    boolean air_temp_conversion_ok = true;
    float float_wind_waves_period = main.INVALID;
    float float_wind_waves_height = main.INVALID;
    float float_pressure_amount_tendency = main.INVALID;
    float float_air_temp = main.INVALID;
    int int_cl_code = main.INVALID;
    int int_cm_code = main.INVALID;
    int int_ch_code = main.INVALID;
    int int_ww_code = main.INVALID;
    int int_VV_code = main.INVALID;
    Integer sky_not_discernible_array[] = {43, 45, 47, 49};
    Integer drizzle_rain_array[] = {
      50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69
    };
    Integer present_weather_36_39_array[] = {36, 37, 38, 39};
    Integer present_weather_48_49_array[] = {48, 49};
    Integer present_weather_56_57_array[] = {56, 57};
    Integer present_weather_66_67_array[] = {66, 67};
    Integer present_weather_68_69_array[] = {68, 69};
    Integer present_weather_70_75_array[] = {70, 71, 72, 73, 74, 75};
    Integer present_weather_76_79_array[] = {76, 77, 78, 79};
    Integer present_weather_83_86_array[] = {83, 84, 85, 86};

    //
    ///////////////////////////// conversions //////////////////////////
    //

    WindWaveValidation windWaveValidation = validateWindWaves("checking_level_2", false);
    float_wind_waves_period = windWaveValidation.period();
    float_wind_waves_height = windWaveValidation.height();
    wind_waves_period_conversion_ok = windWaveValidation.periodValid();
    wind_waves_height_conversion_ok = windWaveValidation.heightValid();

    FloatValidation pressureTendencyValidation =
        validateFloat(
            mybarograph.pressure_amount_tendency,
            "pressure amount tendency conversion error",
            "checking_level_2");
    float_pressure_amount_tendency = pressureTendencyValidation.value();
    pressure_amount_tendency_conversion_ok = pressureTendencyValidation.valid();

    // string Cl code conversion to int
    Integer parsedLowCloudType =
        parseValidationInt(
            mycl.cl_code, "[GENERAL] Cl conversion error; Function: checking_level_2()");
    if (parsedLowCloudType != null) {
      int_cl_code = parsedLowCloudType;
      cl_code_conversion_ok = true;
    } else {
      cl_code_conversion_ok = false;
    }

    // string Cm code conversion to int
    Integer parsedMiddleCloudType =
        parseValidationFirstDigitInt(
            mycm.cm_code, "[GENERAL] Cm conversion error; Function: checking_level_2()");
    if (parsedMiddleCloudType != null) {
      int_cm_code = parsedMiddleCloudType;
      cm_code_conversion_ok = true;
    } else {
      cm_code_conversion_ok = false;
    }

    // string Ch code conversion to int
    Integer parsedHighCloudType =
        parseValidationInt(
            mych.ch_code, "[GENERAL] Ch conversion error; Function: checking_level_2()");
    if (parsedHighCloudType != null) {
      int_ch_code = parsedHighCloudType;
      ch_code_conversion_ok = true;
    } else {
      ch_code_conversion_ok = false;
    }

    IntegerValidation presentWeatherValidation =
        validateInteger(mypresentweather.ww_code, "ww conversion error", "checking_level_2");
    int_ww_code = presentWeatherValidation.value();
    ww_code_conversion_ok = presentWeatherValidation.valid();

    // string VV code conversion to int
    Integer parsedVisibility =
        parseValidationInt(
            myvisibility.VV_code, "[GENERAL] VV conversion error; Function: checking_level_2()");
    if (parsedVisibility != null) {
      int_VV_code = parsedVisibility;
      VV_code_conversion_ok = true;
    } else {
      VV_code_conversion_ok = false;
    }

    FloatValidation airTemperatureValidation =
        validateFloat(mytemp.air_temp, "air temp conversion error", "checking_level_2");
    float_air_temp = airTemperatureValidation.value();
    air_temp_conversion_ok = airTemperatureValidation.valid();

    //
    ///////////////////////////// checks //////////////////////////
    //

    //
    ////////// wind - waves checks /////
    //
    if (doorgaan
        && !LevelTwoWindWaveValidation.validate(
            main.wind_units,
            mywind.int_true_wind_speed,
            wind_waves_period_conversion_ok,
            float_wind_waves_period,
            wind_waves_height_conversion_ok,
            float_wind_waves_height)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// air pressure /////
    //
    if (doorgaan
        && !LevelTwoPressureValidation.validate(
            pressure_amount_tendency_conversion_ok,
            mybarograph.a_code,
            mybarograph.pressure_amount_tendency,
            float_pressure_amount_tendency)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// cloud cover <-> cloud types /////
    //
    if (doorgaan
        && !LevelTwoCloudValidation.validate(
            cl_code_conversion_ok,
            cm_code_conversion_ok,
            ch_code_conversion_ok,
            int_cl_code,
            int_cm_code,
            int_ch_code)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// cloud cover <-> present weather /////
    //
    if (doorgaan
        && !LevelTwoCloudValidation.validatePresentWeather(
            ww_code_conversion_ok, int_ww_code, sky_not_discernible_array, drizzle_rain_array)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// cloud type <-> cloud height /////
    //
    if (doorgaan
        && !LevelTwoCloudValidation.validateCloudHeights(
            cl_code_conversion_ok, ch_code_conversion_ok, int_cl_code, int_ch_code)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// visibilty <-> present weather /////
    //
    if (doorgaan
        && !VisibilityPresentWeatherValidation.validate(
            ww_code_conversion_ok,
            int_ww_code,
            VV_code_conversion_ok,
            int_VV_code,
            mypresentweather.ww_40)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// present weather <-> air temperature /////
    //
    if (doorgaan
        && !PresentWeatherTemperatureValidation.validate(
            air_temp_conversion_ok,
            float_air_temp,
            int_ww_code,
            present_weather_36_39_array,
            present_weather_48_49_array,
            present_weather_56_57_array,
            present_weather_66_67_array,
            present_weather_68_69_array,
            present_weather_70_75_array,
            present_weather_76_79_array,
            present_weather_83_86_array)) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// icing <-> air temperature /////
    //
    if (doorgaan
        && !IcingAirTemperatureValidation.validate(
            air_temp_conversion_ok,
            float_air_temp,
            !myicing.Is_code.equals("")
                || !myicing.EsEs_code.equals("")
                || !myicing.Rs_code.equals(""))) {
      level_2_ok = false;
      doorgaan = false;
    }

    //
    ////////// ice <-> air temperature /////
    //
    if (doorgaan
        && !IceAirTemperatureValidation.validate(
            air_temp_conversion_ok,
            float_air_temp,
            !myice1.ci_code.equals("")
                || !myice1.Si_code.equals("")
                || !myice1.bi_code.equals("")
                || !myice1.Di_code.equals("")
                || !myice1.zi_code.equals(""))) {
      level_2_ok = false;
      doorgaan = false;
    }

    return level_2_ok;
  }

  public boolean checking_level_3() {
    boolean doorgaan = true;
    boolean level_3_ok = true;
    boolean wind_waves_period_conversion_ok = true;
    boolean wind_waves_height_conversion_ok = true;
    boolean first_swell_period_conversion_ok = true;
    boolean first_swell_height_conversion_ok = true;
    boolean second_swell_period_conversion_ok = true;
    boolean second_swell_height_conversion_ok = true;
    boolean air_temp_conversion_ok = true;
    boolean air_pressure_conversion_ok = true;
    boolean amount_pressure_tendency_conversion_ok = true;
    boolean sea_water_temp_conversion_ok = true;
    boolean ice_thickness_conversion_ok = true;
    boolean ww_code_conversion_ok = true;
    float float_wind_waves_period = main.INVALID;
    float float_wind_waves_height = main.INVALID;
    float float_first_swell_period = main.INVALID;
    float float_first_swell_height = main.INVALID;
    float float_second_swell_period = main.INVALID;
    float float_second_swell_height = main.INVALID;
    float float_air_temp = main.INVALID;
    float float_air_pressure_msl_corrected = main.INVALID;
    float float_amount_pressure_tendency = main.INVALID;
    float float_sea_water_temp = main.INVALID;
    float float_ice_thickness = main.INVALID;
    int int_ww_code = main.INVALID;
    //
    ///////////////////////////// conversions //////////////////////////
    //

    WindWaveValidation windWaveValidation = validateWindWaves("checking_level_3", true);
    float_wind_waves_period = windWaveValidation.period();
    float_wind_waves_height = windWaveValidation.height();
    wind_waves_period_conversion_ok = windWaveValidation.periodValid();
    wind_waves_height_conversion_ok = windWaveValidation.heightValid();

    // first swell system period conversion
    Float parsedFirstSwellPeriod =
        parseValidationFloat(
            mywaves.swell_1_period,
            "[GENERAL] first swell period conversion error; Function: checking_level_3()");
    if (parsedFirstSwellPeriod != null) {
      float_first_swell_period = parsedFirstSwellPeriod;
      first_swell_period_conversion_ok = true;
    } else {
      first_swell_period_conversion_ok = false;
    }

    // first swell system height conversion
    Float parsedFirstSwellHeight =
        parseValidationFloat(
            mywaves.swell_1_height,
            "[GENERAL] first swell height conversion error; Function: checking_level_3()");
    if (parsedFirstSwellHeight != null) {
      float_first_swell_height = parsedFirstSwellHeight;
      first_swell_height_conversion_ok = true;
    } else {
      first_swell_height_conversion_ok = false;
    }

    // second swell system period conversion
    Float parsedSecondSwellPeriod =
        parseValidationFloat(
            mywaves.swell_2_period,
            "[GENERAL] second swell period conversion error; Function: checking_level_3()");
    if (parsedSecondSwellPeriod != null) {
      float_second_swell_period = parsedSecondSwellPeriod;
      second_swell_period_conversion_ok = true;
    } else {
      second_swell_period_conversion_ok = false;
    }

    // second swell system height conversion
    Float parsedSecondSwellHeight =
        parseValidationFloat(
            mywaves.swell_2_height,
            "[GENERAL] second swell height conversion error; Function: checking_level_3()");
    if (parsedSecondSwellHeight != null) {
      float_second_swell_height = parsedSecondSwellHeight;
      second_swell_height_conversion_ok = true;
    } else {
      second_swell_height_conversion_ok = false;
    }

    FloatValidation airTemperatureValidation =
        validateFloat(mytemp.air_temp, "air temp conversion error", "checking_level_3");
    float_air_temp = airTemperatureValidation.value();
    air_temp_conversion_ok = airTemperatureValidation.valid();

    // string pressure_msl_corrected to float
    Float parsedAirPressureMsl =
        parseValidationFloat(
            mybarometer.pressure_msl_corrected,
            "[GENERAL] air pressure conversion error; Function: checking_level_3()");
    if (parsedAirPressureMsl != null) {
      float_air_pressure_msl_corrected = parsedAirPressureMsl;
      air_pressure_conversion_ok = true;
    } else {
      air_pressure_conversion_ok = false;
    }

    FloatValidation pressureTendencyValidation =
        validateFloat(
            mybarograph.pressure_amount_tendency,
            "amount air pressure tendency conversion error",
            "checking_level_3");
    float_amount_pressure_tendency = pressureTendencyValidation.value();
    amount_pressure_tendency_conversion_ok = pressureTendencyValidation.valid();

    // string SST to float
    Float parsedSeaWaterTemperature =
        parseValidationFloat(
            mytemp.sea_water_temp, "[GENERAL] SST conversion error; Function: checking_level_3()");
    if (parsedSeaWaterTemperature != null) {
      float_sea_water_temp = parsedSeaWaterTemperature;
      sea_water_temp_conversion_ok = true;
    } else {
      sea_water_temp_conversion_ok = false;
    }

    // string thickness ice accretion (EsEs) to float
    Float parsedIceThickness =
        parseValidationFloat(
            myicing.EsEs_code,
            "[GENERAL] ice thickness (EsEs) conversion error; Function: checking_level_3()");
    if (parsedIceThickness != null) {
      float_ice_thickness = parsedIceThickness; // EsEs_code = ice thickness in centimetres
      ice_thickness_conversion_ok = true;
    } else {
      ice_thickness_conversion_ok = false;
    }

    IntegerValidation presentWeatherValidation =
        validateInteger(mypresentweather.ww_code, "ww conversion error", "checking_level_3");
    int_ww_code = presentWeatherValidation.value();
    ww_code_conversion_ok = presentWeatherValidation.valid();

    //
    ///////////////////////////// checks //////////////////////////
    //

    // speed ship
    //
    if (doorgaan && !ShipSpeedConfirmationValidation.validate(myposition.vs_code)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // wind speed
    //
    if (doorgaan
        && !WindSpeedConfirmationValidation.validate(main.wind_units, mywind.int_true_wind_speed)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // wind waves period
    //
    if (doorgaan
        && !WindWaveConfirmationValidation.validate(
            wind_waves_period_conversion_ok,
            float_wind_waves_period,
            wind_waves_height_conversion_ok,
            float_wind_waves_height)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // first swell period
    //
    if (doorgaan
        && !SwellConfirmationValidation.validate(
            first_swell_period_conversion_ok,
            float_first_swell_period,
            first_swell_height_conversion_ok,
            float_first_swell_height,
            second_swell_period_conversion_ok,
            float_second_swell_period,
            second_swell_height_conversion_ok,
            float_second_swell_height)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // wind speed <--> wand waves height
    //
    if (doorgaan
        && !WindWaveConsistencyValidation.validate(
            main.wind_units,
            mywind.int_true_wind_speed,
            wind_waves_height_conversion_ok,
            float_wind_waves_height)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // air temp
    //
    if (doorgaan
        && !AirTemperatureConfirmationValidation.validate(air_temp_conversion_ok, float_air_temp)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // icing <-> air temperature
    //
    if (doorgaan
        && !IcingAirTemperatureValidation.confirmLevelThree(
            air_temp_conversion_ok,
            float_air_temp,
            !myicing.Is_code.equals("")
                || !myicing.EsEs_code.equals("")
                || !myicing.Rs_code.equals(""))) {
      doorgaan = false;
      level_3_ok = false;
    }

    // air pressure (MSL)
    //
    if (doorgaan
        && !AirPressureConfirmationValidation.validate(
            air_pressure_conversion_ok, float_air_pressure_msl_corrected)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // amount pressure tendency
    //
    if (doorgaan
        && !PressureTendencyConfirmationValidation.validateAmount(
            amount_pressure_tendency_conversion_ok, float_amount_pressure_tendency)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // amount pressure tendency <-> characteristic pressure tendency (a)
    //
    if (doorgaan
        && !PressureTendencyConfirmationValidation.validateCharacteristic(
            amount_pressure_tendency_conversion_ok,
            float_amount_pressure_tendency,
            mybarograph.a_code.equals(""))) {
      doorgaan = false;
      level_3_ok = false;
    }

    // SST
    //
    if (doorgaan
        && !SeaWaterTemperatureConfirmationValidation.validate(
            sea_water_temp_conversion_ok, float_sea_water_temp)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // thickness ice accretion (EsEs)
    //
    if (doorgaan
        && !IceThicknessConfirmationValidation.validate(
            ice_thickness_conversion_ok, float_ice_thickness)) {
      doorgaan = false;
      level_3_ok = false;
    }

    // present weather <-> icing
    //
    if (doorgaan
        && !PresentWeatherIcingValidation.validate(
            ww_code_conversion_ok,
            int_ww_code,
            myicing.Is_code.equals("")
                && myicing.EsEs_code.equals("")
                && myicing.Rs_code.equals(""))) {
      level_3_ok = false;
      doorgaan = false;
    }

    return level_3_ok;
  }

  public boolean Check_Land_Sea_Mask() {
    // called from doInBackground() 4x
    //    - Output_Obs_to_server_menu_actionPerformed() [main.java]
    //    - Output_obs_by_email_all_manual() [main.java]
    //    - Output_obs_to_file_actionPerformed() [main.java]
    //    - Output_obs_to_clipboard_actionPerformed() [main.java]

    // NB om de grootte van de jar te beperken wordt er alleen gecheckt op 1 graads vak niveau
    // (zgf_sam)
    //    en (i.t.t. TurboWin) niet op 1/10 graads vak niveau (zgf_lkw)

    int Octant = INVALID;
    int la_vak_10;
    int la_vak_1;
    int lo_vak_10;
    int lo_vak_1;
    int vak_10;
    int sam_index;
    int land_zee_cijfer_sam; // gevonden cijfer in ZGF_SAM (10 graads vakken file)
    int int_record_sam_vak_10;
    boolean zee_vak_ok = true;
    String record_sam;

    System.out.println("--- Checking entered position against a land-sea mask");

    try (InputStream is = getClass().getResourceAsStream(main.ICONS_DIRECTORY + "zgf_sam");
        BufferedReader in_1 = new BufferedReader(new InputStreamReader(is))) {
      // reading file 1 degree squares
      // InputStream is = getClass().getResourceAsStream(main.ICONS_DIRECTORY + "zgf_sam");
      // BufferedReader in_1 = new BufferedReader(new InputStreamReader(is));

      // deze aanzetten om de 1/10 graads niveau file mee te nemen
      // InputStream is = getClass().getResourceAsStream(main.ICONS_DIRECTORY + "zgf_lkw");
      // BufferedReader in_2 = new BufferedReader(new InputStreamReader(is));

      //
      // Octant bepalen (WMO code table 0371)
      //
      // if ((obs_North_or_South == "N") && (obs_East_or_West == "W"))
      if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true)
          && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true)) {
        // if (num_LoLoLoLo >= 0 && num_LoLoLoLo < 900)                      // n.b. 900 = 90.0 gr
        if (myposition.int_longitude_degrees >= 0 && myposition.int_longitude_degrees < 90)
          Octant = 0;
        else if (myposition.int_longitude_degrees >= 90
            && myposition.int_longitude_degrees <= 180) // n.b. 1800 = 180.0 gr
        Octant = 1;
      }

      // else if ((obs_North_or_South == "N") && (obs_East_or_West == "E"))
      else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH) == true)
          && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true)) {
        if (myposition.int_longitude_degrees >= 90 && myposition.int_longitude_degrees <= 180)
          Octant = 2;
        else if (myposition.int_longitude_degrees >= 0 && myposition.int_longitude_degrees < 90)
          Octant = 3;
      }

      // else if ((obs_North_or_South == "S") && (obs_East_or_West == "W"))
      else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true)
          && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true)) {
        if (myposition.int_longitude_degrees >= 0
            && myposition.int_longitude_degrees < 90) // n.b. 900 = 90.0 gr
        Octant = 5;
        else if (myposition.int_longitude_degrees >= 90
            && myposition.int_longitude_degrees <= 180) // n.b. 1800 = 180.0 gr
        Octant = 6;
      }

      // else if ((obs_North_or_South == "S") && (obs_East_or_West == "E"))
      else if ((myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true)
          && (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST) == true)) {
        if (myposition.int_longitude_degrees >= 90 && myposition.int_longitude_degrees <= 180)
          Octant = 7;
        else if (myposition.int_longitude_degrees >= 0 && myposition.int_longitude_degrees < 90)
          Octant = 8;
      }

      //
      // La-Vak bepalen
      //
      // la_vak_10    = num_LaLaLa / 100;                        // 123 -> 1
      // la_vak_1     = (num_LaLaLa / 10) % 10;                  // 123 -> 2
      la_vak_10 = myposition.int_latitude_degrees / 10; // 12 -> 1
      la_vak_1 = myposition.int_latitude_degrees % 10; // 12 -> 2
      // la_vak_1_10  = num_LaLaLa % 10;                         // 123 -> 3
      // la_voor_lkw  = num_LaLaLa / 10;                         // 123 -> 12

      //
      // Lo-Vak bepalen
      //
      // lo_vak_10    = (num_LoLoLoLo / 100) % 10;               // 1234 -> 2
      // lo_vak_1     = (num_LoLoLoLo / 10) % 10;                // 1234 -> 3
      lo_vak_10 = (myposition.int_longitude_degrees / 10) % 10; // 123 -> 2
      lo_vak_1 = (myposition.int_longitude_degrees) % 10; // 123 -> 3
      // lo_vak_1_10  = num_LoLoLoLo % 10;                       // 1234 -> 4
      // lo_voor_lkw  = (num_LoLoLoLo / 10) % 100;               // 1234 -> 23

      //
      // 10 graads vak bepalen (WMO code tabel 0371)
      //
      vak_10 = (Octant * 100) + (la_vak_10 * 10) + lo_vak_10;

      while (((record_sam = in_1.readLine()) != null) && (Octant != INVALID)) {
        // record_sam.read_line(in_1);
        if (record_sam.length() == 127) // zijn allemaal 127 char. lang
        {
          // if (atoi(record_sam.substring(0, 3)) == vak_10)
          try {
            int_record_sam_vak_10 = Integer.valueOf(record_sam.substring(0, 3));

            if (int_record_sam_vak_10 == vak_10) {
              sam_index = 5 + (11 * la_vak_1) + lo_vak_1; // start op positie 0

              // land_zee_cijfer_sam = atoi(record_sam.substring(sam_index, sam_index + 1));
              land_zee_cijfer_sam = Integer.valueOf(record_sam.substring(sam_index, sam_index + 1));

              if (land_zee_cijfer_sam == 0) // sea position
              zee_vak_ok = true;
              else if (land_zee_cijfer_sam == 4) // not yet but in mask/file
              zee_vak_ok = true;
              else if (land_zee_cijfer_sam == 2) // wrong position
              zee_vak_ok = false;
              else if (land_zee_cijfer_sam == 3) // land position
              zee_vak_ok = false;
              else if (land_zee_cijfer_sam == 1) // coast position
              {
                // code if testing on 1/10 degree squares
                zee_vak_ok = true;
              } // else if (land_zee_cijfer == 1)

              break; // ok, gevonden verlaten do-while sam
            } // if (atoi(record.SubString(0, 3)) == vak_10)
          } // try
          catch (NumberFormatException e) {
          }

        } // if (record.length() == 127)
        else // invalid line length
        {
          break;
        }
      } // while((record_sam = in_1.readLine()) != null)

    } catch (Exception ex) {
      // JOptionPane.showMessageDialog(null, "Reading error 'sea-land mask' file", APPLICATION_NAME
      // + " error", JOptionPane.WARNING_MESSAGE);
      System.out.println("--- Function Check_Land_Sea_Mask(): " + ex);
    } // catch

    if (zee_vak_ok == false) {
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

      if (JOptionPane.showConfirmDialog(
              null, info, main.APPLICATION_NAME + " please confirm", JOptionPane.YES_NO_OPTION)
          == JOptionPane.NO_OPTION) {
        JOptionPane.showMessageDialog(
            null,
            "Please correct the error (no final obs was coded)",
            main.APPLICATION_NAME + " warning",
            JOptionPane.WARNING_MESSAGE);
        zee_vak_ok = false;
      } else {
        zee_vak_ok = true;
      }
    } // if (zee_vak_ok == false)

    return zee_vak_ok;
  }

  private static final int DELAY_UPDATE_PASSWORD_LOOP =
      3600000; // 60 min                          // time in millisec to wait after timer is started
  // to fire first event (10 min = 10 * 1000 * 60 * 10 = 600000)
  public static Timer password_timer;
  public static boolean password_ok = false;
}
