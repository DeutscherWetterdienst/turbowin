package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.junit.After;
import org.junit.Test;

public class AmverSailingPlanExportFormatterTest {

  @After
  public void resetExportState() {
    myamversailingplan.amver_sp_vessel = "";
    myamversailingplan.amver_sp_call_sign = "";
    myamversailingplan.amver_sp_imo_number = "";
    myamversailingplan.amver_sp_time_of_dep_day = "";
    myamversailingplan.amver_sp_time_of_dep_hour = "";
    myamversailingplan.amver_sp_time_of_dep_minute = "";
    myamversailingplan.amver_sp_time_of_dep_month = "";
    myamversailingplan.amver_sp_current_course = "";
    myamversailingplan.amver_sp_remainder_speed = "";
    myamversailingplan.amver_sp_port_dep_name = "";
    myamversailingplan.amver_sp_port_dep_lat = "";
    myamversailingplan.amver_sp_port_dep_lon = "";
    myamversailingplan.amver_sp_port_des_name = "";
    myamversailingplan.amver_sp_port_des_lat = "";
    myamversailingplan.amver_sp_port_des_lon = "";
    myamversailingplan.amver_sp_time_of_des_day = "";
    myamversailingplan.amver_sp_time_of_des_hour = "";
    myamversailingplan.amver_sp_time_of_des_minute = "";
    myamversailingplan.amver_sp_time_of_des_month = "";
    myamversailingplan.amver_sp_radio_guard = "";
    myamversailingplan.amver_track_data = new String[100][9];
    myamversailingplan.amver_sp_medical = new Boolean[4];
    myamversailingplan.amver_sp_relay = new Boolean[4];
  }

  @Test
  public void writesFieldsRoutesAndSelectedCapabilitiesInLegacyOrder() throws IOException {
    myamversailingplan.amver_sp_vessel = "VESSEL";
    myamversailingplan.amver_sp_call_sign = "CALL";
    myamversailingplan.amver_sp_imo_number = "IMO";
    myamversailingplan.amver_sp_time_of_dep_day = "01";
    myamversailingplan.amver_sp_time_of_dep_hour = "02";
    myamversailingplan.amver_sp_time_of_dep_minute = "03";
    myamversailingplan.amver_sp_time_of_dep_month = "JAN";
    myamversailingplan.amver_sp_current_course = "123";
    myamversailingplan.amver_sp_remainder_speed = "12.3";
    myamversailingplan.amver_sp_port_dep_name = "DEP";
    myamversailingplan.amver_sp_port_dep_lat = "LAT1";
    myamversailingplan.amver_sp_port_dep_lon = "LON1";
    myamversailingplan.amver_sp_port_des_name = "DES";
    myamversailingplan.amver_sp_port_des_lat = "LAT2";
    myamversailingplan.amver_sp_port_des_lon = "LON2";
    myamversailingplan.amver_sp_time_of_des_day = "04";
    myamversailingplan.amver_sp_time_of_des_hour = "05";
    myamversailingplan.amver_sp_time_of_des_minute = "06";
    myamversailingplan.amver_sp_time_of_des_month = "FEB";
    myamversailingplan.amver_sp_radio_guard = "YES";
    myamversailingplan.amver_track_data[0][0] = "ignored route number";
    myamversailingplan.amver_track_data[0][1] = "LEG-A";
    myamversailingplan.amver_track_data[1][2] = "LEG-B";
    myamversailingplan.amver_sp_medical = new Boolean[] {true, false, true, false};
    myamversailingplan.amver_sp_relay = new Boolean[] {false, true, false, true};

    StringWriter output = new StringWriter();
    AmverSailingPlanExportFormatter.write(output);

    String nl = System.lineSeparator();
    String expected =
        String.join(
                nl,
                Arrays.asList(
                    "amver_sp_vessel    : VESSEL",
                    "amver_sp_call_sign : CALL",
                    "amver_sp_imo_number: IMO",
                    "amver_sp_dep_day   : 01",
                    "amver_sp_dep_hour  : 02",
                    "amver_sp_dep_minute: 03",
                    "amver_sp_dep_month : JAN",
                    "amver_sp_course    : 123",
                    "amver_sp_speed     : 12.3",
                    "amver_sp_dep_port  : DEP",
                    "amver_sp_dep_lat   : LAT1",
                    "amver_sp_dep_lon   : LON1",
                    "amver_sp_des_port  : DES",
                    "amver_sp_des_lat   : LAT2",
                    "amver_sp_des_lon   : LON2",
                    "amver_sp_des_day   : 04",
                    "amver_sp_des_hour  : 05",
                    "amver_sp_des_minute: 06",
                    "amver_sp_des_month : FEB",
                    "amver_sp_track     : [0][1]LEG-A",
                    "amver_sp_track     : [1][2]LEG-B",
                    "amver_sp_radio     : YES",
                    "amver_sp_medical   : NONE",
                    "amver_sp_medical   : PA",
                    "amver_sp_relay     : AUSREP",
                    "amver_sp_relay     : MAREP"))
            + nl;

    assertEquals(expected, output.toString());
  }
}
