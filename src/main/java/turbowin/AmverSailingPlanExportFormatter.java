package turbowin;

import java.io.IOException;
import java.io.Writer;

/** Writes the internal AMVER sailing-plan representation. */
final class AmverSailingPlanExportFormatter {

  private AmverSailingPlanExportFormatter() {}

  static void write(Writer out) throws IOException {
    line(out, "amver_sp_vessel    : ", myamversailingplan.amver_sp_vessel);
    line(out, "amver_sp_call_sign : ", myamversailingplan.amver_sp_call_sign);
    line(out, "amver_sp_imo_number: ", myamversailingplan.amver_sp_imo_number);
    line(out, "amver_sp_dep_day   : ", myamversailingplan.amver_sp_time_of_dep_day);
    line(out, "amver_sp_dep_hour  : ", myamversailingplan.amver_sp_time_of_dep_hour);
    line(out, "amver_sp_dep_minute: ", myamversailingplan.amver_sp_time_of_dep_minute);
    line(out, "amver_sp_dep_month : ", myamversailingplan.amver_sp_time_of_dep_month);
    line(out, "amver_sp_course    : ", myamversailingplan.amver_sp_current_course);
    line(out, "amver_sp_speed     : ", myamversailingplan.amver_sp_remainder_speed);
    line(out, "amver_sp_dep_port  : ", myamversailingplan.amver_sp_port_dep_name);
    line(out, "amver_sp_dep_lat   : ", myamversailingplan.amver_sp_port_dep_lat);
    line(out, "amver_sp_dep_lon   : ", myamversailingplan.amver_sp_port_dep_lon);
    line(out, "amver_sp_des_port  : ", myamversailingplan.amver_sp_port_des_name);
    line(out, "amver_sp_des_lat   : ", myamversailingplan.amver_sp_port_des_lat);
    line(out, "amver_sp_des_lon   : ", myamversailingplan.amver_sp_port_des_lon);
    line(out, "amver_sp_des_day   : ", myamversailingplan.amver_sp_time_of_des_day);
    line(out, "amver_sp_des_hour  : ", myamversailingplan.amver_sp_time_of_des_hour);
    line(out, "amver_sp_des_minute: ", myamversailingplan.amver_sp_time_of_des_minute);
    line(out, "amver_sp_des_month : ", myamversailingplan.amver_sp_time_of_des_month);

    for (int r = 0; r < myamversailingplan.AMVER_TRACK_ROWS; r++) {
      for (int c = 0; c < myamversailingplan.AMVER_TRACK_COLUMNS; c++) {
        // Route column 0 contains only the route number.
        if (myamversailingplan.amver_track_data[r][c] != null
            && myamversailingplan.amver_track_data[r][c].compareTo("") != 0
            && c != 0) {
          out.write("amver_sp_track     : ");
          out.write("[" + Integer.toString(r) + "]");
          out.write("[" + Integer.toString(c) + "]");
          out.write(myamversailingplan.amver_track_data[r][c]);
          out.write(System.lineSeparator());
        }
      }
    }

    line(out, "amver_sp_radio     : ", myamversailingplan.amver_sp_radio_guard);
    String[] medical = {"NONE", "NURSE", "PA", "MD"};
    for (int i = 0; i < medical.length; i++) {
      if (myamversailingplan.amver_sp_medical[i] == true) {
        line(out, "amver_sp_medical   : ", medical[i]);
      }
    }

    String[] relay = {"JASREP", "AUSREP", "CHILREP", "MAREP"};
    for (int i = 0; i < relay.length; i++) {
      if (myamversailingplan.amver_sp_relay[i] == true) {
        line(out, "amver_sp_relay     : ", relay[i]);
      }
    }
  }

  private static void line(Writer out, String label, String value) throws IOException {
    out.write(label + value);
    out.write(System.lineSeparator());
  }
}
