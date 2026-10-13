package turbowin;

import static turbowin.main_RS232_RS422.*;

import java.util.Date;
import javax.swing.SwingWorker;

final class Rs422UpdateAwsDashboardValuesWorkflow {

  private final main_RS232_RS422 owner;

  Rs422UpdateAwsDashboardValuesWorkflow(main_RS232_RS422 owner) {
    this.owner = owner;
  }

  void start() {
    // called from: RS422_Read_AWS_Sensor_Data_For_Display() [main_RS232_RS422.java]

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        // 'last update' date time [PC based]
        //
        //
        dashboard_string_last_update_record_date_time =
            main.sdf_tsl_2.format(new Date()) + " UTC"; // new Date() -> always in UTC

        // measured at (date, time) [part of the outputs AWS string]
        //
        //
        if (main.date_from_AWS_present) {
          dashboard_string_last_update_record_date =
              mydatetime.day + " " + mydatetime.month + " " + mydatetime.year;
        } else {
          dashboard_string_last_update_record_date = "";
        }

        if (main.time_from_AWS_present) {
          dashboard_string_last_update_record_time = mydatetime.hour + ":" + mydatetime.minute;
        } else {
          dashboard_string_last_update_record_time = "";
        }

        // latitude
        //
        //
        if (main.latitude_from_AWS_present) {
          dashboard_string_last_update_record_latitude =
              myposition.latitude_degrees
                  + "\u00B0"
                  + myposition.latitude_minutes
                  + "' "
                  + myposition.latitude_hemisphere.substring(0, 1);
        } else {
          dashboard_string_last_update_record_latitude = "";
        }

        // longitude
        //
        //
        if (main.longitude_from_AWS_present) {
          dashboard_string_last_update_record_longitude =
              myposition.longitude_degrees
                  + "\u00B0"
                  + myposition.longitude_minutes
                  + "' "
                  + myposition.longitude_hemisphere.substring(0, 1);
        } else {
          dashboard_string_last_update_record_longitude = "";
        }

        // SOG
        //
        //
        if (main.SOG_from_AWS_present) {
          dashboard_string_last_update_record_SOG = mywind.ship_ground_speed;
        } else {
          dashboard_string_last_update_record_SOG = "";
        }

        // COG
        //
        //
        if (main.COG_from_AWS_present) {
          dashboard_string_last_update_record_COG = mywind.ship_ground_course;
        } else {
          dashboard_string_last_update_record_COG = "";
        }

        // Heading
        //
        //
        if (main.true_heading_from_AWS_present) {
          dashboard_string_last_update_record_heading = mywind.ship_heading;
        } else {
          dashboard_string_last_update_record_heading = "";
        }

        // air pressure sensor_height
        //
        //
        // NB if AWS connected always pressure with ic intergrated (contrary to only a barometer
        // connected)
        //
        if (main.pressure_sensor_level_from_AWS_present) {
          // nb in case EUCAWS always ic integrated in barometer reading!
          dashboard_double_last_update_record_sensor_level_pressure_ic =
              Double.parseDouble(mybarometer.pressure_reading);
        } else {
          dashboard_double_last_update_record_sensor_level_pressure_ic = Double.MAX_VALUE;
        }

        // air pressure MSL
        //
        //
        // NB if AWS connected always pressure with ic intergrated (contrary to only a barometer
        // connected)
        //
        if (main.pressure_MSL_from_AWS_present) {
          dashboard_double_last_update_record_MSL_pressure_ic =
              Double.parseDouble(mybarometer.pressure_msl_corrected);
        } else {
          dashboard_double_last_update_record_MSL_pressure_ic = Double.MAX_VALUE;
        }

        // air pressure tendency
        //
        //
        //
        if (main.pressure_tendency_from_AWS_present) // set in Function:
        // RS422_Read_AWS_Sensor_Data_For_Display()
        {
          dashboard_double_last_update_record_pressure_tendency =
              Double.parseDouble(mybarograph.pressure_amount_tendency);
        } else {
          dashboard_double_last_update_record_pressure_tendency = Double.MAX_VALUE;
        }

        // air temperature
        //
        //
        if (main.air_temp_from_AWS_present) {
          dashboard_double_last_update_record_air_temp = Double.parseDouble(mytemp.air_temp);
        } else {
          dashboard_double_last_update_record_air_temp = Double.MAX_VALUE;
        }

        // humidity
        //
        //
        if (main.rh_from_AWS_present) {
          double hulp_double_rv =
              Math.round(mytemp.double_rv * 10 * 100)
                  / 10.0; // nb Math.round(xxx) - > geeft long terug  // NB * 100 for getting %
          dashboard_double_last_update_record_humidity = hulp_double_rv;
        } else {
          dashboard_double_last_update_record_humidity = Double.MAX_VALUE;
        }

        // true wind dir
        //
        //
        if (main.true_wind_dir_from_AWS_present) {
          dashboard_int_last_update_record_true_wind_dir = mywind.int_true_wind_dir;
        } else {
          dashboard_int_last_update_record_true_wind_dir = Integer.MAX_VALUE;
        }

        // relative wind dir
        //
        //
        if (main.relative_wind_dir_from_AWS_present) {
          // dashboard_int_last_update_record_relative_wind_dir = Integer.parseInt(mywind.wind_dir);
          dashboard_int_last_update_record_relative_wind_dir = mywind.int_relative_wind_dir;
        } else {
          dashboard_int_last_update_record_relative_wind_dir = Integer.MAX_VALUE;
        }

        // true wind gust dir
        //
        //
        if (main.true_wind_gust_dir_from_AWS_present) {
          dashboard_int_last_update_record_true_wind_gust_dir = mywind.int_true_wind_gust_dir;
        } else {
          dashboard_int_last_update_record_true_wind_gust_dir = Integer.MAX_VALUE;
        }

        // true wind speed
        //
        //
        if (main.true_wind_speed_from_AWS_present) {
          // NB 4 options:
          // 1) measured/observed in m/s   -> dashboard and graph in knots
          // 2) measured/observed in m/s   -> dashboard and graph in m/s
          // 3) measured/observed in knots -> dashboard and graph in knots
          // 4) measured/observed in knots -> dashboard and graph in m/s

          if (main.wind_units.indexOf(main.M_S)
              != -1) // wind_units 'as measured' set to m/s by observer in Maintenance -> Station
          // data
          {
            if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // dashboard in knots
            {
              // rounding up! (11 m/s -> 21.4 knots -> roundend 21 knots = Bf 5 but 11 m/s = Bf 6
              // !!!)
              dashboard_int_last_update_record_true_wind_speed =
                  (int) Math.round(mywind.double_true_wind_speed * main.M_S_KNOT_CONVERSION);

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_true_wind_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_true_wind_speed);
              dashboard_int_last_update_record_true_wind_speed_10m =
                  (int) Math.round(hulp_double_true_wind_speed_10m * main.M_S_KNOT_CONVERSION);
            } else if (main.wind_units_dashboard.indexOf(main.M_S) != -1) // dashboard in m/s
            {
              dashboard_int_last_update_record_true_wind_speed = mywind.int_true_wind_speed;

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_true_wind_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_true_wind_speed);
              dashboard_int_last_update_record_true_wind_speed_10m =
                  (int) Math.round(hulp_double_true_wind_speed_10m);
            }
          } else if (main.wind_units.indexOf(main.KNOTS)
              != -1) // wind_units 'as measured' set to knots by observer in Maintenance -> Station
          // data
          {
            if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // dashboard in knots
            {
              dashboard_int_last_update_record_true_wind_speed = mywind.int_true_wind_speed;

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_true_wind_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_true_wind_speed);
              dashboard_int_last_update_record_true_wind_speed_10m =
                  (int) Math.round(hulp_double_true_wind_speed_10m);
            } else if (main.wind_units_dashboard.indexOf(main.M_S) != -1) // dashboard in m/s
            {
              dashboard_int_last_update_record_true_wind_speed =
                  (int) Math.round(mywind.double_true_wind_speed * main.KNOT_M_S_CONVERSION);

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_true_wind_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_true_wind_speed);
              dashboard_int_last_update_record_true_wind_speed_10m =
                  (int) Math.round(hulp_double_true_wind_speed_10m * main.M_S_KNOT_CONVERSION);
            }
          }
        } else {
          dashboard_int_last_update_record_true_wind_speed = Integer.MAX_VALUE;
        }

        // relative wind speed
        //
        //
        if (main.relative_wind_speed_from_AWS_present) {
          // NB 4 options:
          // 1) measured/observed in m/s   -> dashboard and graph in knots
          // 2) measured/observed in m/s   -> dashboard and graph in m/s
          // 3) measured/observed in knots -> dashboard and graph in knots
          // 4) measured/observed in knots -> dashboard and graph in m/s

          if (main.wind_units.indexOf(main.M_S)
              != -1) // wind_units 'as measured' set to m/s by observer in Maintenance -> Station
          // data
          {
            if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // dashboard in knots
            {
              dashboard_int_last_update_record_relative_wind_speed =
                  (int) Math.round(mywind.double_relative_wind_speed * main.M_S_KNOT_CONVERSION);

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_relative_wind_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_relative_wind_speed);
              dashboard_int_last_update_record_relative_wind_speed_10m =
                  (int) Math.round(hulp_double_relative_wind_speed_10m * main.M_S_KNOT_CONVERSION);
            } else if (main.wind_units_dashboard.indexOf(main.M_S) != -1) // dashboard in m/s
            {
              dashboard_int_last_update_record_relative_wind_speed = mywind.int_relative_wind_speed;

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_relative_wind_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_relative_wind_speed);
              dashboard_int_last_update_record_relative_wind_speed_10m =
                  (int) Math.round(hulp_double_relative_wind_speed_10m);
            }
          } else if (main.wind_units.indexOf(main.KNOTS)
              != -1) // wind_units 'as measured' set to knots by observer in Maintenance -> Station
          // data
          {
            if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // dashboard in knots
            {
              dashboard_int_last_update_record_relative_wind_speed = mywind.int_relative_wind_speed;

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_relative_wind_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_relative_wind_speed);
              dashboard_int_last_update_record_relative_wind_speed_10m =
                  (int) Math.round(hulp_double_relative_wind_speed_10m);
            } else if (main.wind_units_dashboard.indexOf(main.M_S) != -1) // dashboard in m/s
            {
              dashboard_int_last_update_record_relative_wind_speed =
                  (int) Math.round(mywind.double_relative_wind_speed * main.KNOT_M_S_CONVERSION);

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_relative_wind_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_relative_wind_speed);
              dashboard_int_last_update_record_relative_wind_speed_10m =
                  (int) Math.round(hulp_double_relative_wind_speed_10m * main.M_S_KNOT_CONVERSION);
            }
          }
        } else {
          dashboard_int_last_update_record_relative_wind_speed = Integer.MAX_VALUE;
        }

        // true wind gust speed
        //
        //
        if (main.true_wind_gust_from_AWS_present) {
          // NB 4 options:
          // 1) measured/observed in m/s   -> dashboard and graph in knots
          // 2) measured/observed in m/s   -> dashboard and graph in m/s
          // 3) measured/observed in knots -> dashboard and graph in knots
          // 4) measured/observed in knots -> dashboard and graph in m/s

          if (main.wind_units.indexOf(main.M_S)
              != -1) // wind_units 'as measured' set to m/s by observer in Maintenance -> Station
          // data
          {
            if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // dashboard in knots
            {
              dashboard_int_last_update_record_true_wind_gust =
                  (int) Math.round(mywind.double_true_wind_gust_speed * main.M_S_KNOT_CONVERSION);

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_true_wind_gust_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_true_wind_gust_speed);
              dashboard_int_last_update_record_true_wind_gust_10m =
                  (int) Math.round(hulp_double_true_wind_gust_speed_10m * main.M_S_KNOT_CONVERSION);
            } else if (main.wind_units_dashboard.indexOf(main.M_S) != -1) // dashboard in m/s
            {
              dashboard_int_last_update_record_true_wind_gust = mywind.int_true_wind_gust_speed;

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_true_wind_gust_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_true_wind_gust_speed);
              dashboard_int_last_update_record_true_wind_gust_10m =
                  (int) Math.round(hulp_double_true_wind_gust_speed_10m);
            }
          } else if (main.wind_units.indexOf(main.KNOTS)
              != -1) // wind_units 'as measured' set to knots by observer in Maintenance -> Station
          // data
          {
            if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // dashboard in knots
            {
              dashboard_int_last_update_record_true_wind_gust = mywind.int_true_wind_gust_speed;

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_true_wind_gust_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_true_wind_gust_speed);
              dashboard_int_last_update_record_true_wind_gust_10m =
                  (int) Math.round(hulp_double_true_wind_gust_speed_10m);
            } else if (main.wind_units_dashboard.indexOf(main.M_S) != -1) // dashboard in m/s
            {
              dashboard_int_last_update_record_true_wind_gust =
                  (int) Math.round(mywind.double_true_wind_gust_speed * main.KNOT_M_S_CONVERSION);

              // 10 m wind speed (only for wind radar dashboard)
              double hulp_double_true_wind_gust_speed_10m =
                  owner.RS422_convert_wind_speed_to_10m(mywind.double_true_wind_gust_speed);
              dashboard_int_last_update_record_true_wind_gust_10m =
                  (int) Math.round(hulp_double_true_wind_gust_speed_10m * main.M_S_KNOT_CONVERSION);
            }
          }
        } else {
          dashboard_int_last_update_record_true_wind_gust = Integer.MAX_VALUE;
        }

        // SST
        //
        //
        if (main.SST_from_AWS_present) {
          dashboard_double_last_update_record_sst = Double.parseDouble((mytemp.sea_water_temp));
        } else {
          dashboard_double_last_update_record_sst = Double.MAX_VALUE;
        }

        // VOT
        //
        //
        if (main.VOT_from_AWS_present) {
          dashboard_int_last_update_record_VOT = main.VOT;
        } else {
          dashboard_int_last_update_record_VOT = Integer.MAX_VALUE;
        }

        return null;
      } // protected Void doInBackground() throws Exception
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
