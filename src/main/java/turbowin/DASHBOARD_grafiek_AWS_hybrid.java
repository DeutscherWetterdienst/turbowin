package turbowin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class DASHBOARD_grafiek_AWS_hybrid extends JPanel {

  public DASHBOARD_grafiek_AWS_hybrid() {
    color_black = Color.BLACK;
    color_gray = Color.GRAY;
    color_red = Color.RED; // for main digits
    color_inserted_data = Color.RED; // Color.BLUE;
    block_contour_thickness = 1.0f;
    color_block_face = new Color(179, 242, 255); // Celeste
    color_wind_rose_ring = Color.DARK_GRAY;
    color_wind_arrow_actual = Color.YELLOW;
    color_wind_arrow_max = new Color(224, 224, 224, 120); // Color.LIGHT_GRAY;
    color_wind_rose_additional =
        Color.BLACK; // Color.YELLOW;               // BF circles + labels; N,W,E,S chars
    color_wind_rose_background = new Color(0, 191, 255, 255); // light blue

    // get the screen size
    //
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width_screen = screenSize.getWidth();
    double height_screen = screenSize.getHeight();
    System.out.println(
        "--- Screen resolution AWS Dashboard hybrid: " + width_screen + " x " + height_screen);
  }

  @Override
  public void paintComponent(Graphics g) {
    // eg:
    // https://www.google.nl/search?q=barometer&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjJ78PZg8_UAhWEJMAKHZA3AsAQ_AUICigB&biw=1920&bih=950#imgrc=hE2asDauQrhr6M:

    // NB calculating instrument_width can't be done in DASHBOARD_grafiek_AWS_hybrid() because from
    // here (paintComponents) it is only known
    //    see https://stackoverflow.com/questions/12010587/how-to-get-real-jpanel-size

    int worldLightDir = 315;
    int relativeLightDir = 0;
    boolean display_icon = false;
    double wind_rose_block_margin =
        DASHBOARD_view_AWS_hybrid.width_AWS_hybrid_dashboard / 30.0; // 20// left central block
    double temp_block_margin = wind_rose_block_margin; // right central block

    // always call the super's method to clean "dirty" pixels
    super.paintComponent(g);

    // general
    final Graphics2D g2d = (Graphics2D) g;
    setAllRenderingHints(g2d);

    // font size depending the dimensions of the top block (last updated block/label/text)
    //

    int fontSize_a = 8; // units (eg 1100) wind rose
    int fontSize_c = 10; // Bf labels
    int fontSize_d = 18;

    int fontSize_e = 0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      // fontSize_e = DASHBOARD_view_AWS_hybrid.width_AWS_hybrid_dashboard / 60;
      fontSize_e = DASHBOARD_view_AWS_hybrid.width_AWS_hybrid_dashboard / 70 + 10;
    } else // default font
    {
      fontSize_e = DASHBOARD_view_AWS_hybrid.width_AWS_hybrid_dashboard / 70;
    }

    font_a = new Font("SansSerif", Font.PLAIN, fontSize_a); // units (eg 1100)
    font_b = new Font("Monospaced", Font.BOLD, fontSize_d); // N, E, S, W
    font_c = new Font("SansSerif", Font.PLAIN, fontSize_c); // Bf labels
    font_e =
        new Font(
            "Dialog",
            Font.PLAIN,
            fontSize_e); // test top block ("Measured at: ") and bottom block ("visual observation
    // possible in 32 minutes")
    font_f =
        new Font(
            "Dialog",
            Font.PLAIN,
            (int) (fontSize_e / 1.2)); // labels and inserted data of the measured parameters
    font_c = new Font("SansSerif", Font.PLAIN, fontSize_c); // units (eg 1100)

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // background image(not in night vision mode)
    //

    // colors depending night or day vision
    if (DASHBOARD_view_AWS_hybrid.night_vision == true) {
      color_block_face = color_red;
      color_contour_block = color_black;
      color_digits = color_black;
      color_last_update =
          color_black; // color_dark_red;   // " measured_at" in case hybrid / Meteo France
      // dashboard
      color_update_message_south_panel = color_gray;
    } else // day vision
    {
      color_block_face = new Color(179, 242, 255);
      color_contour_block = color_inserted_data;
      color_digits = color_inserted_data;
      color_last_update = color_black; // color_red;
      color_update_message_south_panel = color_black;
    }

    // obsolete data (no cummunication for roughly > 2 minutes with the AWS)
    if (main.displayed_aws_data_obsolate == true) // valid for analog and digital dashboard
    {
      color_digits = main.obsolate_color_data_from_aws; // inserted data e.g. 12.3 C
      color_last_update = color_black;

      main_RS232_RS422.RS422_initialise_AWS_Sensor_Data_For_Display(); // NB see also:
      // main_RS232_RS422.MAX_AGE_AWS_DATA
    }

    //////////////////////////////////////////////// TOP BLOCK (LAST UPDATED)
    // ///////////////////////////////////////////////////////////////

    // set new origin
    g2d.translate(getWidth() / 2, getHeight() / 12); // getHeight()/12 -> top screen

    //
    ///////////////////// "measured at" label in top screen
    //
    String updated_text = "Measured at: ";
    String update_message = "";
    final double height_measured_at;
    final double width_measured_at;

    Image icon_date_time =
        new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "date_time.png"))
            .getImage();
    int icon_width = icon_date_time.getWidth(this);
    int icon_height = icon_date_time.getHeight(this);
    int total_horiz_space_arround_icon = icon_width;

    g2d.setFont(font_e);

    if ((main.date_from_AWS_present == false) || (main.time_from_AWS_present == false)) {
      update_message = updated_text + " -";
      FontRenderContext context_datum_tijd_parameter = g2d.getFontRenderContext();
      Rectangle2D bounds_datum_tijd_updated =
          font_e.getStringBounds(update_message, context_datum_tijd_parameter);
      height_measured_at =
          -bounds_datum_tijd_updated.getY(); // height char's, this is now a positve value e.g. 33.4
      width_measured_at =
          bounds_datum_tijd_updated.getWidth()
              + icon_width
              + total_horiz_space_arround_icon; // length string
    } else if (main_RS232_RS422.dashboard_string_last_update_record_date.equals("")
        || main_RS232_RS422.dashboard_string_last_update_record_time.equals("")) {
      update_message = updated_text + " unknown";
      FontRenderContext context_datum_tijd_parameter = g2d.getFontRenderContext();
      Rectangle2D bounds_datum_tijd_updated =
          font_e.getStringBounds(update_message, context_datum_tijd_parameter);
      height_measured_at =
          -bounds_datum_tijd_updated.getY(); // height char's, this is now a positve value e.g. 33.4
      width_measured_at =
          bounds_datum_tijd_updated.getWidth()
              + icon_width
              + total_horiz_space_arround_icon; // length string
    } else {
      // update_message = updated_text + " " +
      // main_RS232_RS422.dashboard_string_last_update_record_date_time.replace("-", " ");
      update_message =
          updated_text
              + " "
              + main_RS232_RS422.dashboard_string_last_update_record_date
              + "  "
              + main_RS232_RS422.dashboard_string_last_update_record_time
              + " UTC";
      FontRenderContext context_datum_tijd_parameter = g2d.getFontRenderContext();
      Rectangle2D bounds_datum_tijd_updated =
          font_e.getStringBounds(update_message, context_datum_tijd_parameter);
      height_measured_at = -bounds_datum_tijd_updated.getY(); // height char's
      width_measured_at =
          bounds_datum_tijd_updated.getWidth()
              + icon_width
              + total_horiz_space_arround_icon; // length string
    }

    if (height_measured_at > icon_height / 2) {
      display_icon = true;
    }

    double block_height_measured_at = 0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      block_height_measured_at = 2 * height_measured_at; // also be used by other blocks!!
    } else // use default font
    {
      block_height_measured_at = 4 * height_measured_at; // also be used by other blocks!!
    }

    // System.out.println("+++ y_top_label = " + y_top_label);        // eg -66.4
    // System.out.println("+++ y_bottom_label = " + y_bottom_label);  // eg 66.4
    // System.out.println("+++ height_measured_at = " + height_measured_at);
    // System.out.println("+++ icon-height = " + icon_height);        // eg 24

    // NB drawing of the TOP block after drawing of the left and right central blocks (because in
    // that case after smaller scaling the top block will be still visible over the central blocks)

    /////////////////////////////////////////////// LEFT BLOCK (WIND ROSE)
    // ///////////////////////////////////////////////////////////////
    //
    g2d.setFont(font_f);

    // set new origin
    g2d.translate(-getWidth() / 2, -getHeight() / 12); // return to original origin
    g2d.translate(getWidth() / 4, getHeight() / 2); // origin is now center left block
    // System.out.println("+++ left block: screen getWidth = " + getWidth());
    // System.out.println("+++ left block: screen getHeight = " + getHeight());

    // System.out.println("+++ left block: DASHBOARD_view_AWS_hybrid.width_AWS_hybrid_dashboard = "
    // + DASHBOARD_view_AWS_hybrid.width_AWS_hybrid_dashboard);
    // System.out.println("+++ left block: DASHBOARD_view_AWS_hybrid.height_AWS_hybrid_dashboard = "
    // + DASHBOARD_view_AWS_hybrid.height_AWS_hybrid_dashboard);
    // System.out.println("+++ left block: block_height_last_updated = " +
    // block_height_last_updated);

    // paint left block (relative to new origin !!)
    double block_wind_rose_width =
        (DASHBOARD_view_AWS_hybrid.width_AWS_hybrid_dashboard / 2 - (2 * wind_rose_block_margin));
    double block_wind_rose_height =
        (DASHBOARD_view_AWS_hybrid.height_AWS_hybrid_dashboard - block_height_measured_at * 2);
    double x_left_block_wind_rose = -block_wind_rose_width / 2;
    double y_top_block_wind_rose = -block_wind_rose_height / 2;

    // System.out.println("+++ left block: x_left_block_wind_rose = " + x_left_block_wind_rose);
    // System.out.println("+++ left block: block_wind_rose_width = " + block_wind_rose_width);
    // System.out.println("+++ left block: y_top_block_wind_rose = " + y_top_block_wind_rose);
    // System.out.println("+++ left block: block_wind_rose_height = " + block_wind_rose_height);

    // fill block
    g2d.setPaint(color_block_face);
    g2d.fill(
        new RoundRectangle2D.Double(
            x_left_block_wind_rose,
            y_top_block_wind_rose,
            block_wind_rose_width,
            block_wind_rose_height,
            20,
            20));

    // contour block
    g2d.setPaint(color_contour_block);
    g2d.setStroke(new BasicStroke(block_contour_thickness));
    g2d.draw(
        new RoundRectangle2D.Double(
            x_left_block_wind_rose,
            y_top_block_wind_rose,
            block_wind_rose_width,
            block_wind_rose_height,
            20,
            20));

    // Latitude, logitude speed, Course, Heading text
    double sub_block_wind_rose_width = block_wind_rose_width / 8.0;

    double x_latitude_text = 0.0;
    double x_longitude_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      x_latitude_text = -(3 * sub_block_wind_rose_width);
      x_longitude_text = -(1.5 * sub_block_wind_rose_width);
    } else {
      x_latitude_text = -(2 * sub_block_wind_rose_width);
      x_longitude_text = -(1 * sub_block_wind_rose_width);
    }

    double x_speed_text = 0;
    double x_course_text = +(1 * sub_block_wind_rose_width);
    double x_heading_text = +(2 * sub_block_wind_rose_width);

    double y_position_text =
        -(block_wind_rose_height / 2.0) + (height_measured_at); // bottom of the text string itself

    g2d.setColor(color_black);
    g2d.drawString("Lat", (int) x_latitude_text, (int) y_position_text);
    g2d.drawString("Long", (int) x_longitude_text, (int) y_position_text);
    // g2d.drawString("Speed", (int)x_speed_text, (int)y_position_text);
    if (main.dashboard_font.equals("1")) // bigger font
    {
      g2d.drawString("SOG", (int) x_speed_text, (int) y_position_text);
      g2d.drawString("COG", (int) x_course_text, (int) y_position_text);
    } else {
      g2d.drawString("Speed", (int) x_speed_text, (int) y_position_text);
      g2d.drawString("Course", (int) x_course_text, (int) y_position_text);
    }
    g2d.drawString("Heading", (int) x_heading_text, (int) y_position_text);

    // measured data (updated every minute)
    g2d.setColor(color_digits);
    double y_position_data =
        -(block_wind_rose_height / 2.0)
            + (2 * height_measured_at); // bottom of the text string itself

    // position icon
    if (display_icon) {
      Image icon_position =
          new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "position.png"))
              .getImage();

      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      double y_icon_pos =
          y_position_data
              - icon_height; // eg -22.1 - 24 // NB icon height was already defined in the begin of
      // this function
      double x_icon_pos = 0.0;
      if (main.dashboard_font.equals("1")) // bigger font
      {
        x_icon_pos = -(4 * sub_block_wind_rose_width);
      } else {
        x_icon_pos = -(3 * sub_block_wind_rose_width);
      }

      g2d.drawImage(
          icon_position,
          (int) x_icon_pos + total_horiz_space_arround_icon / 2,
          (int) y_icon_pos,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    // insert latitude
    //
    g2d.drawString("", (int) x_latitude_text, (int) y_position_data);
    if (main.latitude_from_AWS_present) {
      String reading_latitude = main_RS232_RS422.dashboard_string_last_update_record_latitude;

      int pos = reading_latitude.indexOf("\u00B0", 0);

      int reading_int_latitude_degrees = Integer.MAX_VALUE;
      try {
        reading_int_latitude_degrees =
            Integer.parseInt(
                reading_latitude.substring(0, pos)); // eg 42 12'N ->  reading_latitude_degrees = 42
      } catch (NumberFormatException ex) {
        reading_int_latitude_degrees = Integer.MAX_VALUE;
      }

      if (reading_int_latitude_degrees >= 0 && reading_int_latitude_degrees <= 90) {
        reading_latitude =
            reading_latitude.replaceAll("\\s", ""); // skip all the spaces in the latitude string
        g2d.drawString(reading_latitude, (int) x_latitude_text, (int) y_position_data);
      }
    } else {
      g2d.drawString("-", (int) x_latitude_text, (int) y_position_data);
    }

    // insert longitude
    //
    g2d.drawString("", (int) x_longitude_text, (int) y_position_data);
    if (main.longitude_from_AWS_present) {
      String reading_longitude = main_RS232_RS422.dashboard_string_last_update_record_longitude;

      int pos = reading_longitude.indexOf("\u00B0", 0);

      int reading_int_longitude_degrees = Integer.MAX_VALUE;
      try {
        reading_int_longitude_degrees =
            Integer.parseInt(
                reading_longitude.substring(
                    0, pos)); // eg 142 12'N ->  reading_longitude_degrees = 142
      } catch (NumberFormatException ex) {
        reading_int_longitude_degrees = Integer.MAX_VALUE;
      }

      if (reading_int_longitude_degrees >= 0 && reading_int_longitude_degrees <= 180) {
        reading_longitude =
            reading_longitude.replaceAll("\\s", ""); // skip all the spaces in the latitude string
        g2d.drawString(reading_longitude, (int) x_longitude_text, (int) y_position_data);
      }
    } else {
      g2d.drawString("-", (int) x_longitude_text, (int) y_position_data);
    }

    // insert speed (SOG)
    //
    g2d.drawString("", (int) x_speed_text, (int) y_position_data); // clear SOG insert field
    if (main.SOG_from_AWS_present) {
      String reading_SOG = main_RS232_RS422.dashboard_string_last_update_record_SOG;

      double reading_double_speed = Double.MAX_VALUE;
      try {
        reading_double_speed = Double.parseDouble(reading_SOG);
      } catch (NumberFormatException ex) {
        reading_double_speed = Double.MAX_VALUE;
      }

      if (reading_double_speed >= 0.0 && reading_double_speed <= 50.0) {

        if (main.dashboard_font.equals("1")) // bigger font
        {
          // no 'kts' addition in case of the bigger font
          String digits = reading_SOG;
          g2d.drawString(digits, (int) x_speed_text, (int) y_position_data);
        } else {
          // 'kts' addition if default font
          String digits = reading_SOG + " kts";
          g2d.drawString(digits, (int) x_speed_text, (int) y_position_data);
        }
      }
    } // if (main.SOG_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_speed_text, (int) y_position_data);
    }

    // NB main_RS232_RS422.dashboard_string_last_update_record_COG;    // [already roundend  - 360]
    // NB main_RS232_RS422.dashboard_string_last_update_record_heading // [already roundend  - 360]

    // insert course (COG)
    //
    int reading_int_COG = Integer.MAX_VALUE;
    boolean reading_COG_ok = false; // for alligning of the ship in the wind rose
    g2d.drawString("", (int) x_course_text, (int) y_position_data); // clear SOG insert field
    if (main.COG_from_AWS_present) {
      String reading_COG = main_RS232_RS422.dashboard_string_last_update_record_COG;

      try {
        reading_int_COG = Integer.parseInt(reading_COG);
      } catch (NumberFormatException ex) {
        reading_int_COG = Integer.MAX_VALUE;
      }

      if (reading_int_COG >= 1 && reading_int_COG <= 360) {
        String digits = reading_COG + "\u00B0";
        g2d.drawString(digits, (int) x_course_text, (int) y_position_data);
        reading_COG_ok = true;
      }
    } // if (main.COG_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_course_text, (int) y_position_data);
    }

    // insert Heading
    //
    int reading_int_heading = Integer.MAX_VALUE;
    boolean reading_heading_ok = false; // for alligning of the ship in the wind rose
    g2d.drawString("", (int) x_heading_text, (int) y_position_data); // clear SOG insert field
    if (main.true_heading_from_AWS_present) {
      String reading_heading = main_RS232_RS422.dashboard_string_last_update_record_heading;

      // int reading_int_heading = Integer.MAX_VALUE;
      try {
        reading_int_heading = Integer.parseInt(reading_heading);
      } catch (NumberFormatException ex) {
        reading_int_heading = Integer.MAX_VALUE;
      }

      if (reading_int_heading >= 1 && reading_int_heading <= 360) {
        String digits = reading_heading + "\u00B0";
        g2d.drawString(digits, (int) x_heading_text, (int) y_position_data);
        reading_heading_ok = true;
      }
    } // if (main.true_heading_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_heading_text, (int) y_position_data);
    }

    // wind rose
    //
    double wind_rose_diameter = 0.0;
    if (block_wind_rose_width < block_wind_rose_height) {
      wind_rose_diameter = block_wind_rose_width / 1.5;
    } else {
      wind_rose_diameter = block_wind_rose_height / 1.5;
    }

    // for wind rose and wind arrow
    double marker_circle_diameter_1 =
        wind_rose_diameter
            - 26; // eg outside marker circle: 220 - 26 = 194; [most 'outside' outside marker
    // circle]
    double marker_circle_diameter_2 =
        marker_circle_diameter_1
            - 10; // eg inside marker circle: 194 - 10 = 184; [most 'inside' outside marker circle]
    double bf_per_class_radius = (marker_circle_diameter_2 / 2) / 12;

    // wind rose itself
    //
    draw_wind_rose(
        g2d,
        wind_rose_diameter,
        marker_circle_diameter_1,
        marker_circle_diameter_2,
        bf_per_class_radius);

    // ship in wind rose
    //
    int reading_int_course = Integer.MAX_VALUE;
    boolean course_ok = false;

    if (reading_heading_ok) {
      reading_int_course = reading_int_heading;
      course_ok = true;
    } else // heading not ok/present so now try COG
    {
      if (reading_COG_ok) {
        reading_int_course = reading_int_COG;
        course_ok = true;
      }
    } // else

    ////////////////////////////////////////////////// BEGIN TESTING //////////////////////////////
    // reading_int_course = 360;
    // course_ok = true;
    // main.ship_type_dashboard = main.NEUTRAL_SHIP;
    // main.ship_type_dashboard = main.PASSENGER_SHIP;
    // main.ship_type_dashboard = main.OIL_TANKER;
    // main.ship_type_dashboard = main.CONTAINER_SHIP;
    // main.ship_type_dashboard = main.LNG_TANKER;
    ////////////////////////////////////////////////// END TESTING
    // ////////////////////////////////////////

    // if not yet done create object ship
    if (main.myship == null) {
      main.myship = new ship();
    }

    // if no (or not yet) ship type defined start with a container ship
    if (main.ship_type_dashboard.equals("")) {
      main.ship_type_dashboard = main.CONTAINER_SHIP;
    }

    // ship deck color if defined and stored before
    if (main.ship_deck_color_String.equals("") == false) {
      try {
        DASHBOARD_view_AWS_hybrid.deck_color =
            new Color(Integer.parseInt(main.ship_deck_color_String), true); // String to Color
      } catch (NumberFormatException e) {
        DASHBOARD_view_AWS_hybrid.deck_color = null;
      }
    }

    // ship (LNG)tank color if defined and stored before
    if (main.ship_tank_color_String.equals("") == false) {
      try {
        DASHBOARD_view_AWS_hybrid.tank_color =
            new Color(Integer.parseInt(main.ship_tank_color_String), true); // String to Color
      } catch (NumberFormatException e) {
        DASHBOARD_view_AWS_hybrid.tank_color = null;
      }
    }

    if (course_ok) {
      // set new drawing orientation (rotated to heading or COG)
      if (reading_int_course >= 1 && reading_int_course <= 360) {
        g2d.rotate(
            Math.toRadians(
                reading_int_course)); // what follows will be rotated xxx(reading_int_course)
        // degrees

        // improve the raised hatches on some ships
        relativeLightDir = worldLightDir - reading_int_course;
        relativeLightDir = (relativeLightDir % 360 + 360) % 360; //  normalize to [1, 360)
        if (relativeLightDir == 0) //  normalize to [1, 360)
        {
          relativeLightDir = 360;
        }
      }

      switch (main.ship_type_dashboard) {
        // NB "DASHBOARD_view_AWS_hybrid.deck_color = null" by default en will always stay at null;
        // in AWS hybrid mode no menu option to select different deck colors (contray to AWS-radar
        // and APR-radar dashboards)
        // NB TRICK: contrary to the line up here, via the AWS wind radar dashboard sub menu the
        // colors can be changed, these changed colors will then also be visible in the bybrid
        // dashboard!
        //
        case main.YACHT:
          main.myship.draw_yacht(g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.FULL_RIGGED_3:
          main.myship.draw_tall_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.FULL_RIGGED_4:
          main.myship.draw_tall_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.FULL_RIGGED_5:
          main.myship.draw_tall_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.BARQUE_3:
          main.myship.draw_tall_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.BARQUE_4:
          main.myship.draw_tall_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.BARQUE_5:
          main.myship.draw_tall_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.GENERAL_CARGO_SHIP:
          main.myship.draw_general_cargo_ship(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.CONTAINER_SHIP:
          main.myship.draw_container_ship(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.BULK_CARRIER:
          main.myship.draw_bulk_carrier(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color,
              relativeLightDir);
          break;
        case main.OIL_TANKER:
          main.myship.draw_oil_tanker(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.LNG_TANKER:
          main.myship.draw_lng_tanker(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color,
              DASHBOARD_view_AWS_hybrid.tank_color);
          break;
        case main.PASSENGER_SHIP:
          main.myship.draw_passenger_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.RESEARCH_VESSEL:
          main.myship.draw_research_vessel(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.NEUTRAL_SHIP:
          main.myship.draw_neutral_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.RO_RO_SHIP_1:
          main.myship.draw_ro_ro_ship_I(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.RO_RO_SHIP_2:
          main.myship.draw_ro_ro_ship_II(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision, relativeLightDir);
          break;
        case main.FERRY:
          main.myship.draw_ferry(g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.CONTAINER_SHIP_2:
          main.myship.draw_container_ship_II(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.GENERAL_CARGO_CLASSIC:
          main.myship.draw_general_cargo_classic(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.REEFER_SHIP:
          main.myship.draw_reefer_ship(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.GENERAL_CARGO_SHIP_2:
          main.myship.draw_general_cargo_ship_II(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.LNG_TANKER_II:
          main.myship.draw_lng_tanker_II(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.FRUIT_JUICE_TANKER:
          main.myship.draw_fruit_juice_tanker(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.CHEMICAL_TANKER:
          main.myship.draw_chemical_tanker(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.GENERAL_CARGO_SHIP_3:
          main.myship.draw_general_cargo_ship_III(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.HEAVY_LIFT_1:
          main.myship.draw_heavy_lift_I(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_radar.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.HEAVY_LIFT_2:
          main.myship.draw_heavy_lift_II(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_radar.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
        case main.BULK_CARRIER_2:
          main.myship.draw_bulk_carrier_II(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color,
              relativeLightDir);
          break;
        case main.SAILING_YACHT:
          main.myship.draw_sailing_yacht(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        case main.CATAMARAN:
          main.myship.draw_catamaran(
              g2d, wind_rose_diameter, DASHBOARD_view_AWS_hybrid.night_vision);
          break;
        default:
          main.myship.draw_container_ship(
              g2d,
              wind_rose_diameter,
              DASHBOARD_view_AWS_hybrid.night_vision,
              DASHBOARD_view_AWS_hybrid.deck_color);
          break;
      } // switch (main.ship_type_dashboard)

      // reset drawing orientation
      if (reading_int_course >= 1 && reading_int_course <= 360) {
        g2d.rotate(
            Math.toRadians(-reading_int_course)); // what follows will be rotated -xxx degrees
      }
    } // if (course_ok)

    // True wind 'arrow'
    //
    draw_wind_arrow(g2d, marker_circle_diameter_2, bf_per_class_radius);

    /////////////////////////////////////////////// RIGHT BLOCK (PRESSURE, AIR TEMP ETC.)
    // ///////////////////////////////////////////////////////////////
    g2d.setFont(font_f);

    g2d.translate(-getWidth() / 4, -getHeight() / 2); // return to original origin
    g2d.translate(3 * (getWidth() / 4), getHeight() / 2); // origin is now center right block

    // paint right block (relative to new origin !!)
    //
    double block_temp_width =
        (int) (DASHBOARD_view_AWS_hybrid.width_AWS_hybrid_dashboard / 2 - (2 * temp_block_margin));
    double block_temp_height =
        (int)
            (DASHBOARD_view_AWS_hybrid.height_AWS_hybrid_dashboard - block_height_measured_at * 2);
    double x_left_block_temp = -block_temp_width / 2;
    double y_top_block_temp = -block_temp_height / 2;

    // fill block
    g2d.setPaint(color_block_face);
    g2d.fill(
        new RoundRectangle2D.Double(
            x_left_block_temp, y_top_block_temp, block_temp_width, block_temp_height, 20, 20));

    // contour block
    g2d.setPaint(color_contour_block);
    g2d.setStroke(new BasicStroke(block_contour_thickness));
    g2d.draw(
        new RoundRectangle2D.Double(
            x_left_block_temp, y_top_block_temp, block_temp_width, block_temp_height, 20, 20));

    //
    ////////////// Sea level pressure, Barometric tendency ////////////////////
    //

    // Sea level pressure, Barometric tendency text label
    double sub_block_temp_width = block_temp_width / 4.0;
    double x_mslp_text = -(1 * sub_block_temp_width);
    // double x_tendency_text = - (0 * sub_block_temp_width);
    double x_tendency_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      x_tendency_text = 0.5 * sub_block_temp_width;
    } else {
      x_tendency_text = -(0 * sub_block_temp_width);
    }

    double y_pressure_text =
        -(block_temp_height / 2.0) + (1 * height_measured_at); // bottom of the text string itself

    g2d.setColor(color_black);

    if (main.dashboard_font.equals("1")) // bigger font
    {
      g2d.drawString("Sea level pres", (int) x_mslp_text, (int) y_pressure_text);
    } else {
      g2d.drawString("Sea level pressure", (int) x_mslp_text, (int) y_pressure_text);
    }

    if (main.dashboard_font.equals("1")) {
      g2d.drawString("Barometric tend", (int) x_tendency_text, (int) y_pressure_text);
    } else {
      g2d.drawString("Barometric tendency", (int) x_tendency_text, (int) y_pressure_text);
    }

    // measured pressure data updated every minute
    g2d.setColor(color_digits);
    double y_pressure_data =
        -(block_temp_height / 2.0) + (2 * height_measured_at); // bottom of the text string itself

    // icon
    //
    if (display_icon) {
      Image icon_pressure =
          new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "barometer.png"))
              .getImage();

      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      double y_icon_pressure =
          y_pressure_data
              - icon_height; // eg -22.1 - 24 // NB icon height was already defined in the begin of
      // this function
      double x_icon_pressure = -(2 * sub_block_temp_width);

      g2d.drawImage(
          icon_pressure,
          (int) x_icon_pressure
              + total_horiz_space_arround_icon / 2
              + (int) (sub_block_temp_width / 2),
          (int) y_icon_pressure,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    // insert air pressure reading mslp
    //
    g2d.drawString("", (int) x_mslp_text, (int) y_pressure_data);
    if (main.pressure_MSL_from_AWS_present) {
      double reading_mslp =
          main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic; // see:
      // RS422_update_AWS_dasboard_values()
      if (reading_mslp > 900.0 && reading_mslp < 1100.0) {
        String digits = Double.toString(reading_mslp) + " hPa";
        g2d.drawString(digits, (int) x_mslp_text, (int) y_pressure_data);
      }
    } else {
      g2d.drawString("-", (int) x_mslp_text, (int) y_pressure_data);
    }

    // insert air pressure tendency (and charactristic)
    //
    g2d.drawString("", (int) x_tendency_text, (int) y_pressure_data);
    if (main.pressure_tendency_from_AWS_present) {

      double tendency_reading =
          main_RS232_RS422.dashboard_double_last_update_record_pressure_tendency; // see:
      // RS422_update_AWS_dasboard_values()
      if (tendency_reading >= -99.9 && tendency_reading <= 99.9) {
        String digits = Double.toString(tendency_reading) + " hPa / 3 hrs";
        g2d.drawString(digits, (int) x_tendency_text, (int) y_pressure_data);
      }
    } else {
      g2d.drawString("-", (int) x_tendency_text, (int) y_pressure_data);
    }

    //
    ////////// Temperature and SST /////////////
    //

    // Temperature text label
    double x_air_temp_text = -(1 * sub_block_temp_width);
    double x_sst_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      x_sst_text = 0.5 * sub_block_temp_width;
    } else {
      x_sst_text = -(0 * sub_block_temp_width);
    }

    double y_temp_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_temp_text =
          -(block_temp_height / 2.0) + (4 * height_measured_at); // bottom of the text string itself
    } else {
      y_temp_text =
          -(block_temp_height / 2.0) + (5 * height_measured_at); // bottom of the text string itself
    }

    g2d.setColor(color_black);
    g2d.drawString("Air temperature", (int) x_air_temp_text, (int) y_temp_text);

    // g2d.drawString("Sea surface temperature", (int)x_sst_text, (int)y_temp_text);
    if (main.dashboard_font.equals("1")) {
      g2d.drawString("Sea surface temp", (int) x_sst_text, (int) y_temp_text);
    } else {
      g2d.drawString("Sea surface temperature", (int) x_sst_text, (int) y_temp_text);
    }

    // measured temp data updated every minute
    g2d.setColor(color_digits);

    double y_temp_data = 0.0; // bottom of the text string itself
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_temp_data =
          -(block_temp_height / 2.0) + (5 * height_measured_at); // bottom of the text string itself
    } else {
      y_temp_data =
          -(block_temp_height / 2.0) + (6 * height_measured_at); // bottom of the text string itself
    }

    // icon
    //
    if (display_icon) {
      Image icon_temp =
          new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "temperatures.png"))
              .getImage();

      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      double y_icon_temp =
          y_temp_data
              - icon_height; // eg -22.1 - 24 // NB icon height was already defined in the begin of
      // this function
      double x_icon_temp = -(2 * sub_block_temp_width);

      g2d.drawImage(
          icon_temp,
          (int) x_icon_temp + total_horiz_space_arround_icon / 2 + (int) (sub_block_temp_width / 2),
          (int) y_icon_temp,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    // insert sst
    //
    g2d.drawString("", (int) x_sst_text, (int) y_temp_data);
    if (main.SST_from_AWS_present) {
      double sst_reading = main_RS232_RS422.dashboard_double_last_update_record_sst;
      if (sst_reading > -60.0 && sst_reading < 60.0) {
        String digits = Double.toString(sst_reading) + " \u00B0C";
        g2d.drawString(digits, (int) x_sst_text, (int) y_temp_data);
      }
    } // if (main.SST_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_sst_text, (int) y_temp_data);
    }

    // insert air temp
    //
    g2d.drawString("", (int) x_air_temp_text, (int) y_temp_data);
    if (main.air_temp_from_AWS_present) {
      double air_temp_reading =
          main_RS232_RS422.dashboard_double_last_update_record_air_temp; // see:
      // RS422_update_AWS_dasboard_values()
      if (air_temp_reading > -60.0 && air_temp_reading < 60.0) {
        String digits = Double.toString(air_temp_reading) + " \u00B0C";
        g2d.drawString(digits, (int) x_air_temp_text, (int) y_temp_data);
      }
    } // if (main.air_temp_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_air_temp_text, (int) y_temp_data);
    }

    //
    ////////////////// Humidity /////////////////
    //

    // Humidity text label
    double x_humidity_text = -(1 * sub_block_temp_width);

    double y_humidity_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_humidity_text =
          -(block_temp_height / 2.0) + (7 * height_measured_at); // bottom of the text string itself
    } else {
      y_humidity_text =
          -(block_temp_height / 2.0) + (9 * height_measured_at); // bottom of the text string itself
    }

    g2d.setColor(color_black);
    g2d.drawString("Humidity", (int) x_humidity_text, (int) y_humidity_text);

    // measured temp data updated every minute
    g2d.setColor(color_digits);
    // double y_humidity_data = -(block_temp_height / 2.0) + (10 * height_measured_at);
    double y_humidity_data = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_humidity_data = -(block_temp_height / 2.0) + (8 * height_measured_at);
    } else {
      y_humidity_data = -(block_temp_height / 2.0) + (10 * height_measured_at);
    }

    // icon
    //
    if (display_icon) {
      Image icon_humidity =
          new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "humidity.png"))
              .getImage();

      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      double y_icon_humidity =
          y_humidity_data
              - icon_height; // eg -22.1 - 24 // NB icon height was already defined in the begin of
      // this function
      double x_icon_humidity = -(2 * sub_block_temp_width);

      g2d.drawImage(
          icon_humidity,
          (int) x_icon_humidity
              + total_horiz_space_arround_icon / 2
              + (int) (sub_block_temp_width / 2),
          (int) y_icon_humidity,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    // insert relative humidity
    //
    g2d.drawString("", (int) x_humidity_text, (int) y_humidity_data);
    if (main.rh_from_AWS_present) {

      double rh_reading = main_RS232_RS422.dashboard_double_last_update_record_humidity;
      if (rh_reading >= 0.0 && rh_reading <= 105.0) {
        String digits = Double.toString(rh_reading) + " %";
        g2d.drawString(digits, (int) x_humidity_text, (int) y_humidity_data);
      }
    } else {
      g2d.drawString("-", (int) x_humidity_text, (int) y_humidity_data);
    }

    //
    ////////////////// Relative wind ////////////////
    //

    // relative wind text label
    double x_relative_wind_speed_text = -(1 * sub_block_temp_width);
    double x_relative_wind_dir_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      x_relative_wind_dir_text = 0.5 * sub_block_temp_width;
    } else {
      x_relative_wind_dir_text = -(0 * sub_block_temp_width);
    }

    double y_relative_wind_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_relative_wind_text =
          -(block_temp_height / 2.0)
              + (10 * height_measured_at); // bottom of the text string itself
    } else {
      y_relative_wind_text =
          -(block_temp_height / 2.0)
              + (13 * height_measured_at); // bottom of the text string itself
    }

    g2d.setColor(color_black);
    g2d.drawString(
        "Relative wind spd", (int) x_relative_wind_speed_text, (int) y_relative_wind_text);
    if (main.dashboard_font.equals("1")) {
      g2d.drawString(
          "Relative wind dir", (int) x_relative_wind_dir_text, (int) y_relative_wind_text);
    } else {
      g2d.drawString(
          "Relative wind direction", (int) x_relative_wind_dir_text, (int) y_relative_wind_text);
    }

    // measured relative wind data updated every minute
    g2d.setColor(color_digits);
    double y_relative_wind_data = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_relative_wind_data =
          -(block_temp_height / 2.0)
              + (11 * height_measured_at); // bottom of the text string itself
    } else {
      y_relative_wind_data =
          -(block_temp_height / 2.0)
              + (14 * height_measured_at); // bottom of the text string itself
    }

    // icon
    //
    if (display_icon) {
      Image icon_relative_wind =
          new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "wind.png")).getImage();

      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      double y_icon_relative_wind =
          y_relative_wind_data
              - icon_height; // eg -22.1 - 24 // NB icon height was already defined in the begin of
      // this function
      double x_icon_relative_wind = -(2 * sub_block_temp_width);

      g2d.drawImage(
          icon_relative_wind,
          (int) x_icon_relative_wind
              + total_horiz_space_arround_icon / 2
              + (int) (sub_block_temp_width / 2),
          (int) y_icon_relative_wind,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    // insert relative wind speed
    //
    g2d.drawString("", (int) x_relative_wind_speed_text, (int) y_relative_wind_data);
    if (main.relative_wind_speed_from_AWS_present) {
      int relative_wind_speed_reading =
          main_RS232_RS422.dashboard_int_last_update_record_relative_wind_speed;
      if (relative_wind_speed_reading >= 0.0 && relative_wind_speed_reading <= 400.0) {
        // so the digit indication is up to 400 kts/m/s and the analogue up to 110 kts/m/s
        String digits = "";
        if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // units is knots
        {
          digits = Integer.toString(relative_wind_speed_reading) + " kts";
        } else // units is m/s
        {
          digits = Integer.toString(relative_wind_speed_reading) + " m/s";
        }

        g2d.drawString(digits, (int) x_relative_wind_speed_text, (int) y_relative_wind_data);
      }
    } // if (main.relative_wind_speed_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_relative_wind_speed_text, (int) y_relative_wind_data);
    }

    // insert relative wind direction
    //
    g2d.drawString("", (int) x_relative_wind_dir_text, (int) y_relative_wind_data);
    if (main.relative_wind_dir_from_AWS_present) {
      int relative_wind_dir_reading =
          main_RS232_RS422.dashboard_int_last_update_record_relative_wind_dir;
      if (relative_wind_dir_reading >= 1
          && relative_wind_dir_reading <= 360) // dir = 0 should be impossible
      {
        String digits = Integer.toString(relative_wind_dir_reading) + "\u00B0";
        g2d.drawString(digits, (int) x_relative_wind_dir_text, (int) y_relative_wind_data);
      }
    } // if (main.relative_wind_dir_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_relative_wind_dir_text, (int) y_relative_wind_data);
    }

    //
    ///////////////////// True wind ///////////////
    //
    double x_true_wind_speed_text = -(1 * sub_block_temp_width);
    double x_true_wind_dir_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      x_true_wind_dir_text = 0.5 * sub_block_temp_width;
    } else {
      x_true_wind_dir_text = -(0 * sub_block_temp_width);
    }

    // double y_true_wind_text = -(block_temp_height / 2.0) + (17 * height_measured_at);      //
    // bottom of the text string itself
    double y_true_wind_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_true_wind_text =
          -(block_temp_height / 2.0)
              + (13 * height_measured_at); // bottom of the text string itself
    } else {
      y_true_wind_text =
          -(block_temp_height / 2.0)
              + (17 * height_measured_at); // bottom of the text string itself
    }

    g2d.setColor(color_black);
    g2d.drawString("True wind speed", (int) x_true_wind_speed_text, (int) y_true_wind_text);
    if (main.dashboard_font.equals("1")) {
      g2d.drawString("True wind dir", (int) x_true_wind_dir_text, (int) y_true_wind_text);
    } else {
      g2d.drawString("True wind direction", (int) x_true_wind_dir_text, (int) y_true_wind_text);
    }

    // measured relative wind data updated every minute
    g2d.setColor(color_digits);
    double y_true_wind_data = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_true_wind_data =
          -(block_temp_height / 2.0)
              + (14 * height_measured_at); // bottom of the text string itself
    } else {
      y_true_wind_data =
          -(block_temp_height / 2.0)
              + (18 * height_measured_at); // bottom of the text string itself
    }

    // icon
    //
    if (display_icon) {
      Image icon_true_wind =
          new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "wind.png")).getImage();

      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      double y_icon_true_wind =
          y_true_wind_data
              - icon_height; // eg -22.1 - 24 // NB icon height was already defined in the begin of
      // this function
      double x_icon_true_wind = -(2 * sub_block_temp_width);

      g2d.drawImage(
          icon_true_wind,
          (int) x_icon_true_wind
              + total_horiz_space_arround_icon / 2
              + (int) (sub_block_temp_width / 2),
          (int) y_icon_true_wind,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    // insert truee wind speed
    //
    g2d.drawString("", (int) x_true_wind_speed_text, (int) y_true_wind_data);
    if (main.true_wind_speed_from_AWS_present) {
      int true_wind_speed_reading =
          main_RS232_RS422
              .dashboard_int_last_update_record_true_wind_speed; // NB could by kts or m/s
      if (true_wind_speed_reading >= 0.0 && true_wind_speed_reading <= 400.0) {
        // so the digit indication is up to 400 kts/m/s and the analogue up to 110 kts/m/s
        String digits = "";
        if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // units is knots
        {
          digits = Integer.toString(true_wind_speed_reading) + " kts";
        } else // units is m/s
        {
          digits = Integer.toString(true_wind_speed_reading) + " m/s";
        }

        g2d.drawString(digits, (int) x_true_wind_speed_text, (int) y_true_wind_data);
      }
    } // if (main.true_wind_speed_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_true_wind_speed_text, (int) y_true_wind_data);
    }

    // insert true wind direction
    //
    g2d.drawString("", (int) x_true_wind_dir_text, (int) y_true_wind_data);
    if (main.true_wind_dir_from_AWS_present) {
      int true_wind_dir_reading = main_RS232_RS422.dashboard_int_last_update_record_true_wind_dir;
      if (true_wind_dir_reading >= 1
          && true_wind_dir_reading <= 360) // dir = 0 should be impossible
      {
        String digits = Integer.toString(true_wind_dir_reading) + "\u00B0";
        g2d.drawString(digits, (int) x_true_wind_dir_text, (int) y_true_wind_data);
      }
    } // if (main.true_wind_dir_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_true_wind_dir_text, (int) y_true_wind_data);
    }

    //
    //////////////// True wind gust (max true wind) //////////////////
    //
    double x_true_gust_speed_text = -(1 * sub_block_temp_width);
    double x_true_gust_dir_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      x_true_gust_dir_text = 0.5 * sub_block_temp_width;
    } else {
      x_true_gust_dir_text = -(0 * sub_block_temp_width);
    }

    double y_true_gust_text = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_true_gust_text =
          -(block_temp_height / 2.0)
              + (16 * height_measured_at); // bottom of the text string itself
    } else {
      y_true_gust_text =
          -(block_temp_height / 2.0)
              + (21 * height_measured_at); // bottom of the text string itself
    }

    g2d.setColor(color_black);
    g2d.drawString("Max true wind spd", (int) x_true_gust_speed_text, (int) y_true_gust_text);
    if (main.dashboard_font.equals("1")) {
      g2d.drawString("Max true wind dir", (int) x_true_gust_dir_text, (int) y_true_gust_text);
    } else {
      g2d.drawString("Max true wind direction", (int) x_true_gust_dir_text, (int) y_true_gust_text);
    }

    // measured relative wind data updated every minute
    g2d.setColor(color_digits);
    double y_true_gust_data = 0.0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_true_gust_data =
          -(block_temp_height / 2.0)
              + (17 * height_measured_at); // bottom of the text string itself
    } else {
      y_true_gust_data =
          -(block_temp_height / 2.0)
              + (22 * height_measured_at); // bottom of the text string itself
    }

    // icon
    //
    if (display_icon) {
      Image icon_true_gust =
          new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "wind.png")).getImage();

      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      double y_icon_true_gust =
          y_true_gust_data
              - icon_height; // eg -22.1 - 24 // NB icon height was already defined in the begin of
      // this function
      double x_icon_true_gust = -(2 * sub_block_temp_width);

      g2d.drawImage(
          icon_true_gust,
          (int) x_icon_true_gust
              + total_horiz_space_arround_icon / 2
              + (int) (sub_block_temp_width / 2),
          (int) y_icon_true_gust,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    // insert true wind gust speed
    //
    g2d.drawString("", (int) x_true_gust_speed_text, (int) y_true_gust_data); // clear field
    if (main.true_wind_gust_from_AWS_present) {
      int true_wind_gust_speed_reading =
          main_RS232_RS422
              .dashboard_int_last_update_record_true_wind_gust; // NB could by kts or m/s
      if (true_wind_gust_speed_reading >= 0.0 && true_wind_gust_speed_reading <= 400.0) {
        // so the digit indication is up to 400 kts/m/s and the analogue up to 110 kts/m/s
        String digits = "";
        if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // units is knots
        {
          digits = Integer.toString(true_wind_gust_speed_reading) + " kts";
        } else // units is m/s
        {
          digits = Integer.toString(true_wind_gust_speed_reading) + " m/s";
        }

        g2d.drawString(digits, (int) x_true_gust_speed_text, (int) y_true_gust_data);
      }
    } // if (main.true_wind_gust_speed_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_true_gust_speed_text, (int) y_true_gust_data);
    }

    // insert true wind gust dir
    //
    g2d.drawString("", (int) x_true_gust_dir_text, (int) y_true_gust_data); // clear field
    if (main.true_wind_gust_dir_from_AWS_present) {
      int true_wind_gust_dir_reading =
          main_RS232_RS422
              .dashboard_int_last_update_record_true_wind_gust_dir; // NB could by kts or m/s
      if (true_wind_gust_dir_reading >= 1
          && true_wind_gust_dir_reading <= 360) // dir = 0 should be impossible
      {
        String digits = Integer.toString(true_wind_gust_dir_reading) + "\u00B0";
        g2d.drawString(digits, (int) x_true_gust_dir_text, (int) y_true_gust_data);
      }
    } // if (main.true_wind_gust_speed_from_AWS_present)
    else {
      g2d.drawString("-", (int) x_true_gust_dir_text, (int) y_true_gust_data);
    }

    /////////////////////////////////////////////// BOTTOM BLOCK (VISUAL OBS POSSIBLE IN ?? MINUTES)
    // ///////////////////////////////////////////////////////////////
    g2d.setFont(font_e);

    g2d.translate(-3 * (getWidth() / 4), -getHeight() / 2); // return to original origin
    g2d.translate(getWidth() / 2, getHeight() - (getHeight() / 12) + block_height_measured_at / 2);

    // visual obs message based on VOT
    String visual_obs_message = "-";
    if (main.VOT_from_AWS_present) {
      if (main_RS232_RS422.dashboard_int_last_update_record_VOT >= 0
          && main_RS232_RS422.dashboard_int_last_update_record_VOT < 100) {
        visual_obs_message = "Visual observation now possible";
        DASHBOARD_view_AWS_hybrid.jButton1.setEnabled(true); // button: "make an visual observation"
      } else if (main_RS232_RS422.dashboard_int_last_update_record_VOT < 0) {
        visual_obs_message =
            "Visual observation possible in "
                + Integer.toString(Math.abs(main_RS232_RS422.dashboard_int_last_update_record_VOT))
                + " minutes";
        DASHBOARD_view_AWS_hybrid.jButton1.setEnabled(
            false); // button: "make an visual observation"
      } else // VOT >= 100 (eg harbour mode)
      {
        DASHBOARD_view_AWS_hybrid.jButton1.setEnabled(false);
      }
    } // if (main.VOT_from_AWS_present)
    else // no VOT_from_aws_present
    {
      if (main.AWSR) // e.g. OMC-140 and 'EUCAWS + uploads via TurboWin+' (but NOT 'EUCAWS + uploads
      // via own iridium modem' or APR)
      {
        cal_AWSR_system_d_time =
            new GregorianCalendar(
                new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
        cal_AWSR_system_d_time.getTime(); // now effective
        int AWSR_system_hour =
            cal_AWSR_system_d_time.get(
                Calendar.HOUR_OF_DAY); // HOUR_OF_DAY is used for the 24-hour clock. E.g., at
        // 10:04:15.250 PM the HOUR_OF_DAY is 22.
        int AWSR_system_minutes = cal_AWSR_system_d_time.get(Calendar.MINUTE);

        if (((AWSR_system_hour + 1) % Integer.valueOf(main.AWSR_reporting_interval)) == 0) {
          int AWSR_minutes_for_upload = 60 - AWSR_system_minutes;
          if (AWSR_minutes_for_upload <= 30) {
            DASHBOARD_view_AWS_hybrid.jButton1.setEnabled(true);
            visual_obs_message = "Visual observation now possible";
          } else {
            DASHBOARD_view_AWS_hybrid.jButton1.setEnabled(false);
            visual_obs_message =
                "Visual observation possible in " + (AWSR_minutes_for_upload - 30) + " minutes";
          }
        } else {
          DASHBOARD_view_AWS_hybrid.jButton1.setEnabled(false);
        }
      } else {
        DASHBOARD_view_AWS_hybrid.jButton1.setEnabled(false);
      }
    } // else (no VOT_from_aws_present )

    // String visual_obs_message = "Visual observation possible in ?? minutes";
    FontRenderContext context_visual_obs_parameter = g2d.getFontRenderContext();
    Rectangle2D bounds_visual_obs =
        font_e.getStringBounds(visual_obs_message, context_visual_obs_parameter);
    final double height_visual_obs = -bounds_visual_obs.getY(); // height char's
    final double width_visual_obs =
        bounds_visual_obs.getWidth() + icon_width + total_horiz_space_arround_icon; // length string

    // "visual observation possible"- label NB EVERYTHING RELATIVE TO NEW ORIGIN !!!
    double x_left_label2 = -width_visual_obs / 2 - 10;
    double width_label2 = width_visual_obs + 20;

    double y_top_label2 = 0;
    double y_text_label2 = 0;
    if (main.dashboard_font.equals("1")) // bigger font
    {
      y_top_label2 = -(1 * height_visual_obs);
      y_text_label2 = (height_visual_obs / 2); // bottom of the text string itself
    } else // default font
    {
      y_top_label2 = -(2 * height_visual_obs);
      y_text_label2 = -(height_visual_obs / 1.5); // bottom of the text string itself
    }

    double height_label2 = 2 * height_visual_obs;

    double x_text_label2 = -(width_visual_obs / 2.0);

    // paint face "visual observation possible"-label
    g2d.setPaint(color_block_face);
    g2d.fill(
        new RoundRectangle2D.Double(
            x_left_label2, y_top_label2, width_label2, height_label2, 20, 20));

    // "visual observation possible"-label contour
    g2d.setPaint(color_contour_block);
    g2d.setStroke(new BasicStroke(block_contour_thickness));
    g2d.draw(
        new RoundRectangle2D.Double(
            x_left_label2, y_top_label2, width_label2, height_label2, 20, 20));

    // text label (visual observation possible)
    g2d.setColor(color_last_update);
    g2d.drawString(
        visual_obs_message,
        (int) x_text_label2 + icon_width + total_horiz_space_arround_icon / 2,
        (int) y_text_label2);

    // visibilty icon
    if (display_icon) {
      Image icon_visibilty =
          new ImageIcon(this.getClass().getResource(main.ICONS_DIRECTORY + "visibility.png"))
              .getImage();

      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      // double y_icon_vis = y_text_label2 - icon_height;      // eg -22.1 - 24 // NB icon height
      // was already defined in the begin of this function
      double y_icon_vis = y_text_label2 - icon_height;

      g2d.drawImage(
          icon_visibilty,
          (int) x_left_label2 + total_horiz_space_arround_icon / 2,
          (int) y_icon_vis,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    /////////////////////////////////////////////// TOP BLOCK //////////////////////////////////////
    //
    // "update date-time"- label NB EVERYTHING RELATIVE TO NEW ORIGIN !!!

    // System.out.println("+++ y_text_label = " + y_text_label);        // eg -22.1

    g2d.translate(
        -(getWidth() / 2),
        -(getHeight()
            - (getHeight() / 12)
            + block_height_measured_at / 2)); // return to original origin

    // set new origin
    g2d.translate(getWidth() / 2, getHeight() / 12);

    // "update date-time"- label NB EVERYTHING RELATIVE TO NEW ORIGIN !!!
    double x_left_label = -width_measured_at / 2 - 10;
    double width_label = width_measured_at + 20;

    double y_top_label = -(2 * height_measured_at);
    double height_label = 2 * height_measured_at;

    double x_text_label = -(width_measured_at / 2.0);
    double y_text_label = -(height_measured_at / 1.5); // bottom of the text string itself

    // paint face "date-time/update"-label
    g2d.setPaint(color_block_face);
    g2d.fill(
        new RoundRectangle2D.Double(x_left_label, y_top_label, width_label, height_label, 20, 20));

    // "update date-time"-label contour
    g2d.setPaint(color_contour_block);
    g2d.setStroke(new BasicStroke(block_contour_thickness));
    g2d.draw(
        new RoundRectangle2D.Double(x_left_label, y_top_label, width_label, height_label, 20, 20));

    // text (last updated) label
    g2d.setColor(color_last_update);
    g2d.drawString(
        update_message,
        (int) x_text_label + icon_width + total_horiz_space_arround_icon / 2,
        (int) y_text_label);

    // last updated (clock)icon
    if (display_icon) {
      // NB double y_icon = 0;               -> top icon is bottom label !!!!
      // NB double y_icon = y_top_label;     -> top icon is top label !!!!
      // NB double y_icon = y_text_label;    -> top icon is bottom text string
      // double y_icon_date = y_text_label - icon_height;               // eg -22.1 - (24 )
      double y_icon_date = y_text_label - icon_height;

      g2d.drawImage(
          icon_date_time,
          (int) x_left_label + total_horiz_space_arround_icon / 2,
          (int) y_icon_date,
          this); // NB The x,y location specifies the position for the top-left of the image.
    } // if (display_icon)

    ///////////////////////////////////////////////////// ADDITIONAL (NOT MAIN PANEL)
    // ///////////////////////////////////////////////////////////
    //
    ///////////////////// "last updated date and time" bottom left screen [South panel]
    //
    String updated_text_bottom_screen = "last updated: ";
    String update_message_bottom_screen = "";

    if (main_RS232_RS422.dashboard_string_last_update_record_date_time.equals("")) {
      update_message_bottom_screen = updated_text_bottom_screen + " unknown";
    } else {
      // update_message_bottom_screen = updated_text_bottom_screen + " " +
      // main_RS232_RS422.dashboard_string_last_update_record_date_time.replace("-", " ");
      update_message_bottom_screen =
          updated_text_bottom_screen
              + " "
              + main_RS232_RS422.dashboard_string_last_update_record_date_time;
    }

    DASHBOARD_view_AWS_hybrid.jLabel3.setForeground(color_update_message_south_panel);
    DASHBOARD_view_AWS_hybrid.jLabel3.setText(update_message_bottom_screen);
  }

  private void draw_wind_rose(
      Graphics2D g2d,
      double wind_rose_diameter,
      double marker_circle_diameter_1,
      double marker_circle_diameter_2,
      double bf_per_class_radius) {
    // eg:
    // https://www.google.nl/search?q=barometer&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjJ78PZg8_UAhWEJMAKHZA3AsAQ_AUICigB&biw=1920&bih=950#imgrc=hE2asDauQrhr6M:

    String letter = null;
    String text;
    int angle;
    int start;
    int x;
    int y;
    int width_letter;
    float thickness_wind_rose_ring = 1.0f;

    int horz_offset = (int) (wind_rose_diameter - 0); // -20 orig
    int units_start =
        (int)
            (wind_rose_diameter / 2
                - 18); // eg 220 / 2 - 18 = 92   // not the 5 and 10 units markers
    int units_end =
        (int)
            (wind_rose_diameter / 2
                - 14); // eg 220 / 2 - 14 = 96   // all markers end (5, 10 and intermediate)
    int units_5_start =
        (int) (wind_rose_diameter / 2 - 21); // eg 220 / 2 - 21 = 89   //the 5 units markers
    int units_10_start =
        (int) (wind_rose_diameter / 2 - 22); // eg 220 / 2 - 22 = 88   //the 10 units markers
    int text_base_units_values = (int) (wind_rose_diameter / 2 - 10); // eg 220 / 2 - 10 = 100
    // int text_base_comments         = text_base_units_values / 2 + 5;           // eg 100 / 2 + 5
    // = 55
    int text_base_comments = (int) (text_base_units_values - (22 * 1.5));
    int width_a1 = 0;
    int width_a2 = 0;

    if (DASHBOARD_view_AWS_hybrid.night_vision == false) {
      // main wind/compass rose area
      // g2d.setPaint(Color.LIGHT_GRAY);
      // g2d.setPaint(new Color(0,191,255, 255));
      g2d.setPaint(color_wind_rose_background);
      g2d.fill(
          new Arc2D.Double(
              -wind_rose_diameter / 2,
              -wind_rose_diameter / 2,
              wind_rose_diameter,
              wind_rose_diameter,
              0,
              360,
              Arc2D.CHORD));

    } else {
      Shape circle =
          new Ellipse2D.Float(
              (float) -marker_circle_diameter_2 / 2,
              (float) -marker_circle_diameter_2 / 2,
              (float) marker_circle_diameter_2,
              (float) marker_circle_diameter_2);
      g2d.setPaint(color_black);
      g2d.fill(circle);
    } // else

    // outside ring
    g2d.setColor(color_wind_rose_ring);
    g2d.setStroke(new BasicStroke(thickness_wind_rose_ring));
    g2d.draw(
        new Arc2D.Double(
            -wind_rose_diameter / 2,
            -wind_rose_diameter / 2,
            wind_rose_diameter,
            wind_rose_diameter,
            0,
            360,
            Arc2D.CHORD));

    for (int i = 0; i < 360; i++) {
      // NB first i = <never mind> -> paint starting point = always East -> clockwise
      g2d.setColor(color_black);

      g2d.setFont(font_a);
      // g2d.setFont(font_b);

      if ((i % 5 == 0) && (i % 10 != 0)) {
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawLine(units_5_start, 0, units_end, 0);
      } else if (i % 10 == 0) // tens marks                            // 10 units marks
      {
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(units_10_start, 0, units_end, 0);

        // eg text = "90"
        if (i >= 0 && i <= 270) {
          text = String.valueOf(i + 90); // i = 90 = start of the scale (East dir)
        } else {
          text = String.valueOf(i - 270); // i = 270 = North dir
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
        } else // 100 - 360 deg
        {
          y = 0 - width_a2 / 2;
        }

        // unit numbers e.g. 90
        angle = 90;
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text, 0, 0);

        g2d.rotate(-Math.toRadians(angle)); // rotate back
        g2d.translate(-(float) x, -(float) y); // translate back
      } // else (tens)

      g2d.setColor(color_wind_rose_additional);

      // text decoration 'EAST'
      start = 0;
      // if (i == 360 - 15 || i == 360 - 5 || i == start + 5 || i == start + 15)
      if (i == start) {
        letter = "E";

        g2d.setFont(font_b);
        width_letter = g2d.getFontMetrics().stringWidth("E");
        y = 0 - width_letter / 2;

        x = text_base_comments;
        angle = 90;
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(letter, 0, 0);

        g2d.rotate(-Math.toRadians(angle)); // rotate back
        g2d.translate(-(float) x, -(float) y); // translate back
      }

      // text decoration 'SOUTH'
      start = 90;
      // if (i == start - 20 || i == start - 10 || i == start || i == start + 10 || i == start + 20)
      if (i == start) {
        letter = "S";

        g2d.setFont(font_b);
        width_letter = g2d.getFontMetrics().stringWidth("S");
        y = 0 - width_letter / 2;

        x = text_base_comments;
        angle = 90;
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(letter, 0, 0);

        g2d.rotate(-Math.toRadians(angle)); // rotate back
        g2d.translate(-(float) x, -(float) y); // translate back
      }

      // text decoration 'WEST'
      start = 180;
      // if (i == start - 15 || i == start - 5 || i == start + 5 || i == start + 15)
      if (i == start) {
        letter = "W";

        g2d.setFont(font_b);
        width_letter = g2d.getFontMetrics().stringWidth("W");
        y = 0 - width_letter / 2;

        x = text_base_comments;
        angle = 90;
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(letter, 0, 0);

        g2d.rotate(-Math.toRadians(angle)); // rotate back
        g2d.translate(-(float) x, -(float) y); // translate back
      }

      // text decoration 'NORTH'
      start = 270;
      // if (i == start - 20 || i == start - 10 || i == start || i == start + 10 || i == start + 20)
      if (i == start) {
        letter = "N";

        g2d.setFont(font_b);
        width_letter = g2d.getFontMetrics().stringWidth("N");
        y = 0 - width_letter / 2;

        x = text_base_comments;
        angle = 90;
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(letter, 0, 0);

        g2d.rotate(-Math.toRadians(angle)); // rotate back
        g2d.translate(-(float) x, -(float) y); // translate back
      }

      // Because the rotate function of Java Graphics takes a radian value as a parameter, we
      // convert 3 degree to radians by the formula (?/180 x 3.0).
      // g2d.rotate((Math.PI / 180.0) * 3.0);  // 360 / 120 = 3.0
      g2d.rotate((Math.PI / 180.0) * 1.0); // 360 / 360 = 1.0
    } // for (int i = 0; i < 360; i++)

    // NB Arc2D.Double(double x, double y, double w, double h, double start, double extent, int
    // type)
    //    Constructs a new arc, initialized to the specified location, size, angular extents, and
    // closure type.
    //
    // marker circles
    //
    g2d.setColor(color_black);
    g2d.setStroke(new BasicStroke(1.0f));
    g2d.draw(
        new Arc2D.Double(
            -marker_circle_diameter_1 / 2,
            -marker_circle_diameter_1 / 2,
            marker_circle_diameter_1,
            marker_circle_diameter_1,
            360,
            360,
            Arc2D.OPEN)); // point East = 0 degrees (normally you would call East 90 degrees...) ->
    // to the left
    g2d.draw(
        new Arc2D.Double(
            -marker_circle_diameter_2 / 2,
            -marker_circle_diameter_2 / 2,
            marker_circle_diameter_2,
            marker_circle_diameter_2,
            360,
            360,
            Arc2D.OPEN)); // point East = 0 degrees -> to the left

    // Bf circles
    //
    g2d.setColor(color_wind_rose_additional);
    // float[] dash = {2f, 2f, 2f};   // length, space
    float[] dash = {2f, 4f}; // length, space
    g2d.setStroke(
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 1.0f));

    // every 3Bf a circle + label
    for (int i = 3; i <= 9; i += 3) {
      // NB i = Bf class
      g2d.draw(
          new Arc2D.Double(
              -bf_per_class_radius * i,
              -bf_per_class_radius * i,
              2 * bf_per_class_radius * i,
              2 * bf_per_class_radius * i,
              360,
              360,
              Arc2D
                  .OPEN)); // point East = 0 degrees (normally you would call East 90 degrees...) ->
      // to the left

      // Bf circles labels
      // g2d.setColor(color_black);
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

    // True wind label (left below wind rose)
    //
    g2d.setColor(color_black);
    g2d.setFont(font_f);
    // g2d.drawString("True wind", (int)(-wind_rose_diameter / 2), (int)(wind_rose_diameter / 2));

    if (main.dashboard_font.equals("1")) // bigger font
    {
      FontMetrics metrics = g2d.getFontMetrics(font_f);
      int height_font = metrics.getHeight(); // height char's
      g2d.drawString(
          "True wind",
          (int) (-wind_rose_diameter / 2),
          (int) (wind_rose_diameter / 2) + height_font);
    } else {
      g2d.drawString("True wind", (int) (-wind_rose_diameter / 2), (int) (wind_rose_diameter / 2));
    }

    // Course (COG) label (right below wind rose)
    // int width_COG_string = g2d.getFontMetrics().stringWidth("Course");
    // g2d.drawString("Course", (int)(wind_rose_diameter / 2 - width_COG_string),
    // (int)(wind_rose_diameter / 2));

  }

  private void draw_wind_arrow(
      Graphics2D g2d, double marker_circle_diameter_2, double bf_per_class_radius) {
    boolean true_wind_dir_ok = false;
    boolean true_wind_speed_ok = false;
    int Bf_class = Integer.MAX_VALUE;
    int true_wind_dir_reading = Integer.MAX_VALUE;

    // true wind dir
    if (main.true_wind_dir_from_AWS_present) {
      true_wind_dir_reading = main_RS232_RS422.dashboard_int_last_update_record_true_wind_dir;
      if (true_wind_dir_reading >= 1
          && true_wind_dir_reading <= 360) // dir = 0 should be impossible
      {
        true_wind_dir_ok = true;
      }
    } // if (main.true_wind_dir_from_AWS_present)

    // insert true wind speed as Bf
    if (main.true_wind_speed_from_AWS_present) {
      int true_wind_speed_reading =
          main_RS232_RS422
              .dashboard_int_last_update_record_true_wind_speed; // NB could by kts or m/s
      if (true_wind_speed_reading >= 0.0 && true_wind_speed_reading <= 400.0) {
        // so the digit indication is up to 400 kts/m/s and the analogue up to 110 kts/m/s
        // String digits = "";

        if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // units is knots
        {
          if (true_wind_speed_reading >= 64 && true_wind_speed_reading < 400) {
            Bf_class = 12;
            true_wind_speed_ok = true;
          } else {
            switch (true_wind_speed_reading) {
              case 0:
                Bf_class = 0;
                true_wind_speed_ok = true;
                break;
              case 1:
              case 2:
              case 3:
                Bf_class = 1;
                true_wind_speed_ok = true;
                break;
              case 4:
              case 5:
              case 6:
                Bf_class = 2;
                true_wind_speed_ok = true;
                break;
              case 7:
              case 8:
              case 9:
              case 10:
                Bf_class = 3;
                true_wind_speed_ok = true;
                break;
              case 11:
              case 12:
              case 13:
              case 14:
              case 15:
              case 16:
                Bf_class = 4;
                true_wind_speed_ok = true;
                break;
              case 17:
              case 18:
              case 19:
              case 20:
              case 21:
                Bf_class = 5;
                true_wind_speed_ok = true;
                break;
              case 22:
              case 23:
              case 24:
              case 25:
              case 26:
              case 27:
                Bf_class = 6;
                true_wind_speed_ok = true;
                break;
              case 28:
              case 29:
              case 30:
              case 31:
              case 32:
              case 33:
                Bf_class = 7;
                true_wind_speed_ok = true;
                break;
              case 34:
              case 35:
              case 36:
              case 37:
              case 38:
              case 39:
              case 40:
                Bf_class = 8;
                true_wind_speed_ok = true;
                break;
              case 41:
              case 42:
              case 43:
              case 44:
              case 45:
              case 46:
              case 47:
                Bf_class = 9;
                true_wind_speed_ok = true;
                break;
              case 48:
              case 49:
              case 50:
              case 51:
              case 52:
              case 53:
              case 54:
              case 55:
                Bf_class = 10;
                true_wind_speed_ok = true;
                break;
              case 56:
              case 57:
              case 58:
              case 59:
              case 60:
              case 61:
              case 62:
              case 63:
                Bf_class = 11;
                true_wind_speed_ok = true;
                break;
              default:
                Bf_class = Integer.MAX_VALUE;
                break;
            } // switch
          } // else
        } // if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1)
        else if (main.wind_units_dashboard.indexOf(main.M_S) != -1) // units is m/s
        {
          if (true_wind_speed_reading >= 33 && true_wind_speed_reading < 400) {
            Bf_class = 12;
            true_wind_speed_ok = true;
          } else {
            switch (true_wind_speed_reading) {
              case 0:
                Bf_class = 0;
                true_wind_speed_ok = true;
                break;
              case 1:
                Bf_class = 1;
                true_wind_speed_ok = true;
                break;
              case 2:
              case 3:
                Bf_class = 2;
                true_wind_speed_ok = true;
                break;
              case 4:
              case 5:
                Bf_class = 3;
                true_wind_speed_ok = true;
                break;
              case 6:
              case 7:
                Bf_class = 4;
                true_wind_speed_ok = true;
                break;
              case 8:
              case 9:
              case 10:
                Bf_class = 5;
                true_wind_speed_ok = true;
                break;
              case 11:
              case 12:
              case 13:
                Bf_class = 6;
                true_wind_speed_ok = true;
                break;
              case 14:
              case 15:
              case 16:
              case 17:
                Bf_class = 7;
                true_wind_speed_ok = true;
                break;
              case 18:
              case 19:
              case 20:
                Bf_class = 8;
                true_wind_speed_ok = true;
                break;
              case 21:
              case 22:
              case 23:
              case 24:
                Bf_class = 9;
                true_wind_speed_ok = true;
                break;
              case 25:
              case 26:
              case 27:
              case 28:
                Bf_class = 10;
                true_wind_speed_ok = true;
                break;
              case 29:
              case 30:
              case 31:
              case 32:
                Bf_class = 11;
                true_wind_speed_ok = true;
                break;
              default:
                Bf_class = Integer.MAX_VALUE;
                break;
            } // switch
          } // else
        } // else if (main.wind_units_dashboard.indexOf(main.M_S) != -1)
      }
    } // if (main.true_wind_speed_from_AWS_present)

    if (true_wind_speed_ok && true_wind_dir_ok) {
      double radius_wind_arrow_max = marker_circle_diameter_2 / 2;

      // NB "wind_rose_diameter = block_wind_rose_width / 1.5"  (if block_wind_rose_width <
      // block_wind_rose_heightz) else "wind_rose_diameter = block_wind_rose_height / 1.5" ;
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

      g2d.setStroke(new BasicStroke(1.0f));

      double max_arrow_width = 10;

      // polygon of the max theoretical wind arrow (12 Bf)
      //
      double x1_arrow =
          Math.cos(Math.toRadians(true_wind_dir_reading - (max_arrow_width / 2) - 90))
              * (radius_wind_arrow_max); // -90 because the drawing starts at EAST (90 degr)
      double x2_arrow =
          Math.cos(Math.toRadians(true_wind_dir_reading + (max_arrow_width / 2) - 90))
              * (radius_wind_arrow_max);
      double x3_arrow =
          Math.cos(Math.toRadians(true_wind_dir_reading + 0 - 90)) * (radius_wind_arrow_max);

      double y1_arrow =
          Math.sin(Math.toRadians(true_wind_dir_reading - (max_arrow_width / 2) - 90))
              * (radius_wind_arrow_max);
      double y2_arrow =
          Math.sin(Math.toRadians(true_wind_dir_reading + (max_arrow_width / 2) - 90))
              * (radius_wind_arrow_max);
      double y3_arrow =
          Math.sin(Math.toRadians(true_wind_dir_reading + 0 - 90)) * (radius_wind_arrow_max);

      double xPoints[] = {0, x1_arrow, x3_arrow, x2_arrow};
      double yPoints[] = {0, y1_arrow, y3_arrow, y2_arrow};

      GeneralPath polygon_max = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon_max.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints.length; index++) {
        polygon_max.lineTo(xPoints[index], yPoints[index]);
      }
      polygon_max.closePath();

      // polygon of the actual wind arrow (e.g 4 Bf)
      //
      double x1_arrow_actual =
          Math.cos(Math.toRadians(true_wind_dir_reading - (max_arrow_width / 2) - 90))
              * (radius_wind_arrow_actual); // -90 because the drawing starts at EAST (90 degr)
      double x2_arrow_actual =
          Math.cos(Math.toRadians(true_wind_dir_reading + (max_arrow_width / 2) - 90))
              * (radius_wind_arrow_actual);
      double x3_arrow_actual =
          Math.cos(Math.toRadians(true_wind_dir_reading + 0 - 90)) * (radius_wind_arrow_actual);

      double y1_arrow_actual =
          Math.sin(Math.toRadians(true_wind_dir_reading - (max_arrow_width / 2) - 90))
              * (radius_wind_arrow_actual);
      double y2_arrow_actual =
          Math.sin(Math.toRadians(true_wind_dir_reading + (max_arrow_width / 2) - 90))
              * (radius_wind_arrow_actual);
      double y3_arrow_actual =
          Math.sin(Math.toRadians(true_wind_dir_reading + 0 - 90)) * (radius_wind_arrow_actual);

      double xPoints_actual[] = {0, x1_arrow_actual, x3_arrow_actual, x2_arrow_actual};
      double yPoints_actual[] = {0, y1_arrow_actual, y3_arrow_actual, y2_arrow_actual};

      GeneralPath polygon_actual = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
      polygon_actual.moveTo(xPoints[0], yPoints[0]);
      for (int index = 1; index < xPoints_actual.length; index++) {
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
      // g2d.setColor(color_black);
      g2d.setColor(color_wind_arrow_actual);
      g2d.draw(polygon_max);
    } // if (true_wind_speed_ok && true_wind_dir_ok)
  }

  private void setAllRenderingHints(Graphics2D g2d) {
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 100);
    g2d.setRenderingHint(
        RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g2d.setRenderingHint(
        RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
  }

  private GregorianCalendar cal_AWSR_system_d_time;

  private final Color color_black;
  private final Color color_red;
  private final Color color_gray;
  private final Color color_wind_arrow_actual;
  private final Color color_wind_arrow_max;
  private Color color_last_update; // not final
  private Color color_update_message_south_panel; // not final
  private Color color_digits; // not final
  private final Color color_inserted_data;
  private final Color color_wind_rose_additional;

  private Font font_a;
  private Font font_b;
  private Font font_c;
  private Font font_e;
  private Font font_f;

  private final float block_contour_thickness;

  private Color color_contour_block; // not final (night/day colors)
  private Color color_block_face; // not final (night/day colors)
  private final Color color_wind_rose_ring;
  private final Color color_wind_rose_background;
}
