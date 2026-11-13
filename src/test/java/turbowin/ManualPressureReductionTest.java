package turbowin;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.misc.Unsafe;

public class ManualPressureReductionTest {
  private final Map<Field, Object> originalValues = new LinkedHashMap<>();
  private mybarometer barometer;

  @Before
  public void setUp() throws Exception {
    save(main.class, "pressure_reading_msl_yes_no", "barometer_above_sll", "keel_sll");
    save(
        mybarometer.class,
        "PPPP_code",
        "pressure_reading_corrected",
        "pressure_msl_corrected",
        "manualReduction");
    save(mytemp.class, "air_temp");
    save(
        myposition.class,
        "int_latitude_degrees",
        "int_latitude_minutes",
        "int_longitude_degrees",
        "int_longitude_minutes",
        "latitude_hemisphere",
        "longitude_hemisphere");
    main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_NO;
    main.barometer_above_sll = "25";
    main.keel_sll = "0";
    myposition.int_latitude_degrees = 52;
    myposition.int_latitude_minutes = 0;
    myposition.int_longitude_degrees = 4;
    myposition.int_longitude_minutes = 0;
    myposition.latitude_hemisphere = myposition.HEMISPHERE_NORTH;
    myposition.longitude_hemisphere = myposition.HEMISPHERE_EAST;
    mybarometer.pressure_reading_corrected = "1000.0";
    mybarometer.PPPP_code = "0024";
    mytemp.air_temp = "";

    // Exercise the calculation without opening a form or starting instrument readers.
    Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
    unsafeField.setAccessible(true);
    barometer = (mybarometer) ((Unsafe) unsafeField.get(null)).allocateInstance(mybarometer.class);
    setInstance("checks_ok", true);
    setInstance("double_pressure_reading_corrected", 1000.0);
    setInstance("double_deepest_draft", 5.0);
  }

  @After
  public void tearDown() throws Exception {
    for (Map.Entry<Field, Object> entry : originalValues.entrySet()) {
      entry.getKey().set(null, entry.getValue());
    }
  }

  @Test
  public void usesTemperatureAlreadyAvailableWhenPressureIsConfirmed() throws Exception {
    mytemp.air_temp = "30.0";
    reducePressure();
    assertEquals("1002.3", mybarometer.pressure_msl_corrected);
  }

  @Test
  public void updatesPressureAndCodeWhenTemperatureIsEnteredChangedOrRemoved() throws Exception {
    reducePressure();
    assertEquals("1002.4", mybarometer.pressure_msl_corrected);
    mytemp.air_temp = "30.0";
    mybarometer.refreshManualPressureReduction();
    assertEquals("1002.3", mybarometer.pressure_msl_corrected);
    assertEquals("0023", mybarometer.PPPP_code);
    mytemp.air_temp = "0.0";
    mybarometer.refreshManualPressureReduction();
    assertEquals("1002.5", mybarometer.pressure_msl_corrected);
    assertEquals("0025", mybarometer.PPPP_code);
    mytemp.air_temp = "";
    mybarometer.refreshManualPressureReduction();
    assertEquals("1002.4", mybarometer.pressure_msl_corrected);
    assertEquals("0024", mybarometer.PPPP_code);
  }

  @Test
  public void leavesAlreadySeaLevelPressureUnchanged() throws Exception {
    main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_YES;
    reducePressure();
    mytemp.air_temp = "30.0";
    mybarometer.refreshManualPressureReduction();
    assertEquals("1000.0", mybarometer.pressure_msl_corrected);
  }

  @Test
  public void doesNotReuseManualReductionAfterResetOrReplacement() throws Exception {
    reducePressure();
    mybarometer.pressure_msl_corrected = "1010.0";
    mytemp.air_temp = "30.0";
    mybarometer.refreshManualPressureReduction();
    assertEquals("1010.0", mybarometer.pressure_msl_corrected);
    reducePressure();
    mybarometer.clearManualReduction();
    mytemp.air_temp = "0.0";
    mybarometer.refreshManualPressureReduction();
    assertEquals("1002.3", mybarometer.pressure_msl_corrected);
  }

  private void reducePressure() throws Exception {
    Method method = mybarometer.class.getDeclaredMethod("MSL_Herleiding_Luchtdruk");
    method.setAccessible(true);
    method.invoke(barometer);
  }

  private void setInstance(String name, Object value) throws Exception {
    Field field = mybarometer.class.getDeclaredField(name);
    field.setAccessible(true);
    field.set(barometer, value);
  }

  private void save(Class<?> type, String... names) throws Exception {
    for (String name : names) {
      Field field = type.getDeclaredField(name);
      field.setAccessible(true);
      originalValues.put(field, field.get(null));
    }
  }
}
