package turbowin;

import static turbowin.main.INVALID;
import static turbowin.main.log_turbowin_system_message;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
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
    PreviousObservationRecordParser.PreviousObservationRecord previousObservation =
        PreviousObservationRecordParser.parse(main.last_record);

    //
    //////////////// conversion from substring to ints or floats successful
    //
    if (previousObservation.valid()) {
      float num_vorige_obs_breedte = previousObservation.latitude();
      float num_vorige_obs_lengte = previousObservation.longitude();

      /*
      // compare date/time with the date/time of the previous obs
      */

      System.out.println("--- comparing date/time of entered obs with date/time of last saved obs");

      /* date-time previous saved obs */
      Calendar calendar_vorige_obs =
          new GregorianCalendar(
              previousObservation.year(),
              previousObservation.month() - 1,
              previousObservation.day(),
              previousObservation.hour(),
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

      if (ObservationTimeSequenceValidation.isNotLater(calendar_huidige_obs, calendar_vorige_obs)) {
        if (!PositionSequenceWorkflow.confirmDateTime(calendar_huidige_obs)) {
          doorgaan = false;
          time_sequence_checks_ok = false;
        }
      } // if (calendar_huidige_obs.compareTo(calendar_vorige_obs) < 0)

      /* present obs position compared to the position of the previous obs */
      if (doorgaan) {
        System.out.println("--- comparing position of entered obs with position of last saved obs");

        /* first determine the time diff between present and previous obs */
        long obs_verschil_uur =
            ObservationTimeSequenceValidation.elapsedWholeHours(
                calendar_huidige_obs, calendar_vorige_obs);

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

          if (PositionSequenceValidation.exceedsAllowedDistance(
              obs_verschil_uur, afstand_vorige_huidige_obs)) {
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

            if (!PositionSequenceWorkflow.confirmPosition(info)) {
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
    boolean zee_vak_ok = true;

    System.out.println("--- Checking entered position against a land-sea mask");

    LandSeaMaskPositionCalculator.Position maskPosition =
        LandSeaMaskPositionCalculator.calculate(
            myposition.latitude_hemisphere,
            myposition.longitude_hemisphere,
            myposition.int_latitude_degrees,
            myposition.int_longitude_degrees);
    Octant = maskPosition.octant();
    zee_vak_ok = LandSeaMaskWorkflow.check(maskPosition);

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
