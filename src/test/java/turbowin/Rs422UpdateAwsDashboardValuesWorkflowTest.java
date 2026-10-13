package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Rs422UpdateAwsDashboardValuesWorkflowTest {

  private final Map<Field, Object> originalStaticState = new HashMap<>();
  private main_RS232_RS422 owner;

  @Before
  public void setUp() throws Exception {
    captureStaticState(
        main.class,
        main_RS232_RS422.class,
        mydatetime.class,
        myposition.class,
        mywind.class,
        mybarometer.class,
        mybarograph.class,
        mytemp.class);

    main.sdf_tsl_2 = utcFormat("yyyy-MM-dd HH:mm:ss");
    main.wind_units = main.M_S;
    main.wind_units_dashboard = main.KNOTS;
    main.height_anemometer = "20";
    owner = new main_RS232_RS422();
    setAllPresenceFlags(false);
    resetDashboardValues();
  }

  @After
  public void tearDown() throws Exception {
    restoreStaticState();
  }

  @Test
  public void presentValuesAreDisplayedAndConvertedForAllAwsFields() throws Exception {
    setAllPresenceFlags(true);
    mydatetime.year = "2026";
    mydatetime.month = "April";
    mydatetime.day = "9";
    mydatetime.hour = "7";
    mydatetime.minute = "15";
    myposition.latitude_degrees = "12";
    myposition.latitude_minutes = "21";
    myposition.latitude_hemisphere = myposition.HEMISPHERE_SOUTH;
    myposition.longitude_degrees = "123";
    myposition.longitude_minutes = "27";
    myposition.longitude_hemisphere = myposition.HEMISPHERE_EAST;
    mywind.ship_ground_speed = "5.5";
    mywind.ship_ground_course = "23";
    mywind.ship_heading = "180";
    mybarometer.pressure_reading = "1006.6";
    mybarometer.pressure_msl_corrected = "1000.5";
    mybarograph.pressure_amount_tendency = "2.3";
    mytemp.air_temp = "20.5";
    mytemp.double_rv = 0.53;
    mywind.int_true_wind_dir = 350;
    mywind.int_relative_wind_dir = 128;
    mywind.int_true_wind_gust_dir = 358;
    mywind.int_true_wind_speed = 4;
    mywind.double_true_wind_speed = 4.2;
    mywind.int_relative_wind_speed = 5;
    mywind.double_relative_wind_speed = 4.6;
    mywind.int_true_wind_gust_speed = 8;
    mywind.double_true_wind_gust_speed = 7.8;
    mytemp.sea_water_temp = "9.3";
    main.VOT = 10;

    startUpdate();
    waitFor(() -> "5.5".equals(main_RS232_RS422.dashboard_string_last_update_record_SOG));

    assertEquals("9 April 2026", main_RS232_RS422.dashboard_string_last_update_record_date);
    assertEquals("7:15", main_RS232_RS422.dashboard_string_last_update_record_time);
    assertEquals(
        "12\u00B021' S", main_RS232_RS422.dashboard_string_last_update_record_latitude);
    assertEquals(
        "123\u00B027' E", main_RS232_RS422.dashboard_string_last_update_record_longitude);
    assertEquals("23", main_RS232_RS422.dashboard_string_last_update_record_COG);
    assertEquals("180", main_RS232_RS422.dashboard_string_last_update_record_heading);
    assertEquals(
        1006.6, main_RS232_RS422.dashboard_double_last_update_record_sensor_level_pressure_ic, 0.001);
    assertEquals(
        1000.5, main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic, 0.001);
    assertEquals(2.3, main_RS232_RS422.dashboard_double_last_update_record_pressure_tendency, 0.001);
    assertEquals(20.5, main_RS232_RS422.dashboard_double_last_update_record_air_temp, 0.001);
    assertEquals(53.0, main_RS232_RS422.dashboard_double_last_update_record_humidity, 0.001);
    assertEquals(350, main_RS232_RS422.dashboard_int_last_update_record_true_wind_dir);
    assertEquals(128, main_RS232_RS422.dashboard_int_last_update_record_relative_wind_dir);
    assertEquals(358, main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust_dir);
    assertEquals(8, main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed);
    assertEquals(9, main_RS232_RS422.dashboard_int_last_update_record_relative_wind_speed);
    assertEquals(15, main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust);
    assertEquals(9.3, main_RS232_RS422.dashboard_double_last_update_record_sst, 0.001);
    assertEquals(10, main_RS232_RS422.dashboard_int_last_update_record_VOT);
    assertTrue(main_RS232_RS422.dashboard_string_last_update_record_date_time.endsWith(" UTC"));

    double trueWindAt10m = owner.RS422_convert_wind_speed_to_10m(4.2);
    double relativeWindAt10m = owner.RS422_convert_wind_speed_to_10m(4.6);
    double gustAt10m = owner.RS422_convert_wind_speed_to_10m(7.8);
    assertEquals((int) Math.round(trueWindAt10m * main.M_S_KNOT_CONVERSION),
        main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed_10m);
    assertEquals((int) Math.round(relativeWindAt10m * main.M_S_KNOT_CONVERSION),
        main_RS232_RS422.dashboard_int_last_update_record_relative_wind_speed_10m);
    assertEquals((int) Math.round(gustAt10m * main.M_S_KNOT_CONVERSION),
        main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust_10m);
  }

  @Test
  public void absentValuesUseDashboardSentinelsAndClearPreviousValues() throws Exception {
    seedDashboardValues();
    startUpdate();
    waitFor(() -> main_RS232_RS422.dashboard_int_last_update_record_VOT == Integer.MAX_VALUE);

    assertEquals("", main_RS232_RS422.dashboard_string_last_update_record_date);
    assertEquals("", main_RS232_RS422.dashboard_string_last_update_record_time);
    assertEquals("", main_RS232_RS422.dashboard_string_last_update_record_latitude);
    assertEquals("", main_RS232_RS422.dashboard_string_last_update_record_longitude);
    assertEquals("", main_RS232_RS422.dashboard_string_last_update_record_SOG);
    assertEquals("", main_RS232_RS422.dashboard_string_last_update_record_COG);
    assertEquals("", main_RS232_RS422.dashboard_string_last_update_record_heading);
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_sensor_level_pressure_ic, 0.0);
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic, 0.0);
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_pressure_tendency, 0.0);
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_air_temp, 0.0);
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_humidity, 0.0);
    assertEquals(Integer.MAX_VALUE, main_RS232_RS422.dashboard_int_last_update_record_true_wind_dir);
    assertEquals(Integer.MAX_VALUE, main_RS232_RS422.dashboard_int_last_update_record_relative_wind_dir);
    assertEquals(Integer.MAX_VALUE, main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust_dir);
    assertEquals(Integer.MAX_VALUE, main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed);
    assertEquals(Integer.MAX_VALUE, main_RS232_RS422.dashboard_int_last_update_record_relative_wind_speed);
    assertEquals(Integer.MAX_VALUE, main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust);
    assertEquals(Double.MAX_VALUE, main_RS232_RS422.dashboard_double_last_update_record_sst, 0.0);
    assertEquals(Integer.MAX_VALUE, main_RS232_RS422.dashboard_int_last_update_record_VOT);
  }

  @Test
  public void malformedPressureAndTendencyPreserveValuesReachedBeforeFailure() throws Exception {
    main.pressure_MSL_from_AWS_present = true;
    mybarometer.pressure_msl_corrected = "not-a-pressure";
    main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic = 1012.3;
    startUpdate();
    flushEdt();
    assertEquals(1012.3, main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic, 0.001);

    resetDashboardValues();
    main.pressure_MSL_from_AWS_present = true;
    main.pressure_tendency_from_AWS_present = true;
    mybarometer.pressure_msl_corrected = "1000.5";
    mybarograph.pressure_amount_tendency = "not-a-tendency";
    main_RS232_RS422.dashboard_double_last_update_record_pressure_tendency = 2.4;
    startUpdate();
    waitFor(() -> main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic == 1000.5);
    assertEquals(2.4, main_RS232_RS422.dashboard_double_last_update_record_pressure_tendency, 0.001);
  }

  @Test
  public void knotInputAndInvalidAnemometerHeightUseLegacyConversionBoundaries() throws Exception {
    setAllPresenceFlags(false);
    main.true_wind_speed_from_AWS_present = true;
    main.relative_wind_speed_from_AWS_present = true;
    main.true_wind_gust_from_AWS_present = true;
    main.wind_units = main.KNOTS;
    main.wind_units_dashboard = main.M_S;
    main.height_anemometer = "20";
    mywind.int_true_wind_speed = 10;
    mywind.double_true_wind_speed = 10.0;
    mywind.int_relative_wind_speed = 10;
    mywind.double_relative_wind_speed = 10.0;
    mywind.int_true_wind_gust_speed = 10;
    mywind.double_true_wind_gust_speed = 10.0;

    startUpdate();
    waitFor(() -> main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed == 5);
    assertEquals(5, main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed);
    assertEquals(5, main_RS232_RS422.dashboard_int_last_update_record_relative_wind_speed);
    assertEquals(5, main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust);
    assertEquals(
        (int) Math.round(owner.RS422_convert_wind_speed_to_10m(10.0) * main.M_S_KNOT_CONVERSION),
        main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed_10m);

    main.height_anemometer = "10";
    resetDashboardValues();
    startUpdate();
    waitFor(() -> main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed == 5);
    int invalidConvertedWindSpeed = (int) Math.round(main.INVALID * main.M_S_KNOT_CONVERSION);
    assertEquals(
        invalidConvertedWindSpeed,
        main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed_10m);
    assertEquals(
        invalidConvertedWindSpeed,
        main_RS232_RS422.dashboard_int_last_update_record_relative_wind_speed_10m);
    assertEquals(
        invalidConvertedWindSpeed,
        main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust_10m);
  }

  private void startUpdate() {
    new Rs422UpdateAwsDashboardValuesWorkflow(owner).start();
  }

  private void setAllPresenceFlags(boolean value) {
    main.date_from_AWS_present = value;
    main.time_from_AWS_present = value;
    main.latitude_from_AWS_present = value;
    main.longitude_from_AWS_present = value;
    main.COG_from_AWS_present = value;
    main.SOG_from_AWS_present = value;
    main.true_heading_from_AWS_present = value;
    main.pressure_sensor_level_from_AWS_present = value;
    main.pressure_MSL_from_AWS_present = value;
    main.pressure_tendency_from_AWS_present = value;
    main.pressure_characteristic_from_AWS_present = value;
    main.air_temp_from_AWS_present = value;
    main.rh_from_AWS_present = value;
    main.SST_from_AWS_present = value;
    main.relative_wind_speed_from_AWS_present = value;
    main.relative_wind_dir_from_AWS_present = value;
    main.true_wind_speed_from_AWS_present = value;
    main.true_wind_dir_from_AWS_present = value;
    main.true_wind_gust_from_AWS_present = value;
    main.true_wind_gust_dir_from_AWS_present = value;
    main.VOT_from_AWS_present = value;
  }

  private void seedDashboardValues() {
    main_RS232_RS422.dashboard_string_last_update_record_date = "old date";
    main_RS232_RS422.dashboard_string_last_update_record_time = "old time";
    main_RS232_RS422.dashboard_string_last_update_record_latitude = "old latitude";
    main_RS232_RS422.dashboard_string_last_update_record_longitude = "old longitude";
    main_RS232_RS422.dashboard_string_last_update_record_SOG = "old SOG";
    main_RS232_RS422.dashboard_string_last_update_record_COG = "old COG";
    main_RS232_RS422.dashboard_string_last_update_record_heading = "old heading";
    main_RS232_RS422.dashboard_double_last_update_record_sensor_level_pressure_ic = 1.0;
    main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic = 2.0;
    main_RS232_RS422.dashboard_double_last_update_record_pressure_tendency = 3.0;
    main_RS232_RS422.dashboard_double_last_update_record_air_temp = 4.0;
    main_RS232_RS422.dashboard_double_last_update_record_humidity = 5.0;
    main_RS232_RS422.dashboard_int_last_update_record_true_wind_dir = 6;
    main_RS232_RS422.dashboard_int_last_update_record_relative_wind_dir = 7;
    main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust_dir = 8;
    main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed = 9;
    main_RS232_RS422.dashboard_int_last_update_record_relative_wind_speed = 10;
    main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust = 11;
    main_RS232_RS422.dashboard_double_last_update_record_sst = 12.0;
    main_RS232_RS422.dashboard_int_last_update_record_VOT = 13;
  }

  private void resetDashboardValues() {
    main_RS232_RS422.dashboard_string_last_update_record_date = "";
    main_RS232_RS422.dashboard_string_last_update_record_time = "";
    main_RS232_RS422.dashboard_string_last_update_record_latitude = "";
    main_RS232_RS422.dashboard_string_last_update_record_longitude = "";
    main_RS232_RS422.dashboard_string_last_update_record_SOG = "";
    main_RS232_RS422.dashboard_string_last_update_record_COG = "";
    main_RS232_RS422.dashboard_string_last_update_record_heading = "";
    main_RS232_RS422.dashboard_double_last_update_record_sensor_level_pressure_ic = Double.MAX_VALUE;
    main_RS232_RS422.dashboard_double_last_update_record_MSL_pressure_ic = Double.MAX_VALUE;
    main_RS232_RS422.dashboard_double_last_update_record_pressure_tendency = Double.MAX_VALUE;
    main_RS232_RS422.dashboard_double_last_update_record_air_temp = Double.MAX_VALUE;
    main_RS232_RS422.dashboard_double_last_update_record_humidity = Double.MAX_VALUE;
    main_RS232_RS422.dashboard_int_last_update_record_true_wind_dir = Integer.MAX_VALUE;
    main_RS232_RS422.dashboard_int_last_update_record_relative_wind_dir = Integer.MAX_VALUE;
    main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust_dir = Integer.MAX_VALUE;
    main_RS232_RS422.dashboard_int_last_update_record_true_wind_speed = Integer.MAX_VALUE;
    main_RS232_RS422.dashboard_int_last_update_record_relative_wind_speed = Integer.MAX_VALUE;
    main_RS232_RS422.dashboard_int_last_update_record_true_wind_gust = Integer.MAX_VALUE;
    main_RS232_RS422.dashboard_double_last_update_record_sst = Double.MAX_VALUE;
    main_RS232_RS422.dashboard_int_last_update_record_VOT = Integer.MAX_VALUE;
  }

  private void captureStaticState(Class<?>... types) throws Exception {
    for (Class<?> type : types) {
      for (Field field : type.getDeclaredFields()) {
        if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
          field.setAccessible(true);
          originalStaticState.put(field, field.get(null));
        }
      }
    }
  }

  private void restoreStaticState() throws Exception {
    for (Map.Entry<Field, Object> entry : originalStaticState.entrySet()) {
      entry.getKey().set(null, entry.getValue());
    }
  }

  private static SimpleDateFormat utcFormat(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format;
  }

  private static void waitFor(BooleanSupplier condition) throws Exception {
    long deadline = System.nanoTime() + 5_000_000_000L;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
    assertTrue("Timed out waiting for AWS dashboard workflow", condition.getAsBoolean());
    flushEdt();
  }

  private static void flushEdt() throws Exception {
    SwingUtilities.invokeAndWait(() -> {});
  }
}
