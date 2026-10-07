package turbowin;

import static turbowin.main.log_turbowin_system_message;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.JDialog;
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
        SatelliteImageUrlBuilder.buildSsec(satellite_image_mode, (int) centerLat, (int) centerLon);

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

    String url =
        SatelliteImageUrlBuilder.buildNoaa(satellite_image_mode, int_lat_degrees, int_lon_degrees);

    // invoke, in the default web browser, the url to the satellite image
    main.satellite_link_mouse_clicked(url);
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
    return PositionSequenceCheckWorkflow.run(this);
  }

  int bepaal_afstand_huidige_obs_pos_tot_vorige_obs_pos(
      double num_vorige_obs_breedte, double num_vorige_obs_lengte) {
    return PositionDistanceCalculator.calculate(
        num_vorige_obs_breedte,
        num_vorige_obs_lengte,
        myposition.int_latitude_degrees,
        myposition.int_latitude_minutes,
        myposition.latitude_hemisphere,
        myposition.int_longitude_degrees,
        myposition.int_longitude_minutes,
        myposition.longitude_hemisphere);
  }

  public boolean Move_log_files(final String move_mode_logs) {
    return LogFilesMoveWorkflow.move(move_mode_logs);
  }

  public void Kopieeren_Waarnemers_En_Aantallen() {
    ObserverStatisticsWorkflow.run();
  }

  Float parseValidationFloat(String value, String errorMessage) {
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

  Integer parseValidationInt(String value, String errorMessage) {
    try {
      if (value.equals("") == false && value != null) {
        return Integer.parseInt(value);
      }
    } catch (NumberFormatException ex) {
      main.log_turbowin_system_message(errorMessage);
    }
    return null;
  }

  Integer parseValidationFirstDigitInt(String value, String errorMessage) {
    try {
      if (value.equals("") == false && value != null) {
        return Integer.parseInt(value.substring(0, 1));
      }
    } catch (NumberFormatException ex) {
      main.log_turbowin_system_message(errorMessage);
    }
    return null;
  }

  record WindWaveValidation(float period, float height, boolean periodValid, boolean heightValid) {}

  record FloatValidation(float value, boolean valid) {}

  record IntegerValidation(int value, boolean valid) {}

  FloatValidation validateFloat(String value, String description, String validationFunction) {
    Float parsed =
        parseValidationFloat(
            value, "[GENERAL] " + description + "; Function: " + validationFunction + "()");
    return new FloatValidation(parsed == null ? main.INVALID : parsed, parsed != null);
  }

  IntegerValidation validateInteger(String value, String description, String validationFunction) {
    Integer parsed =
        parseValidationInt(
            value, "[GENERAL] " + description + "; Function: " + validationFunction + "()");
    return new IntegerValidation(parsed == null ? main.INVALID : parsed, parsed != null);
  }

  WindWaveValidation validateWindWaves(String validationFunction, boolean preserveEmptyHeightFlag) {
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
    return LevelTwoValidationWorkflow.run(this);
  }

  public boolean checking_level_3() {
    return LevelThreeValidationWorkflow.run(this);
  }

  public boolean Check_Land_Sea_Mask() {
    return LandSeaMaskConfirmationWorkflow.run();
  }

  private static final int DELAY_UPDATE_PASSWORD_LOOP =
      3600000; // 60 min                          // time in millisec to wait after timer is started
  // to fire first event (10 min = 10 * 1000 * 60 * 10 = 600000)
  public static Timer password_timer;
  public static boolean password_ok = false;
}
