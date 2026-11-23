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
  private BarometerModel barometer;

  @Before
  public void setUp() throws Exception {
    save(main.class, "pressure_reading_msl_yes_no", "barometer_above_sll", "keel_sll");
    save(
        BarometerModel.class,
        "PPPP_code",
        "pressure_reading_corrected",
        "pressure_msl_corrected",
        "manualReduction");
    save(TemperatureModel.class, "air_temp");
    save(
        PositionModel.class,
        "int_latitude_degrees",
        "int_latitude_minutes",
        "int_longitude_degrees",
        "int_longitude_minutes",
        "latitude_hemisphere",
        "longitude_hemisphere");
    main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_NO;
    main.barometer_above_sll = "25";
    main.keel_sll = "0";
    PositionModel.int_latitude_degrees = 52;
    PositionModel.int_latitude_minutes = 0;
    PositionModel.int_longitude_degrees = 4;
    PositionModel.int_longitude_minutes = 0;
    PositionModel.latitude_hemisphere = PositionModel.HEMISPHERE_NORTH;
    PositionModel.longitude_hemisphere = PositionModel.HEMISPHERE_EAST;
    BarometerModel.pressure_reading_corrected = "1000.0";
    BarometerModel.PPPP_code = "0024";
    TemperatureModel.air_temp = "";

    // Exercise the calculation without opening a form or starting instrument readers.
    Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
    unsafeField.setAccessible(true);
    barometer =
        (BarometerModel) ((Unsafe) unsafeField.get(null)).allocateInstance(BarometerModel.class);
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
    TemperatureModel.air_temp = "30.0";
    reducePressure();
    assertEquals("1002.3", BarometerModel.pressure_msl_corrected);
  }

  @Test
  public void updatesPressureAndCodeWhenTemperatureIsEnteredChangedOrRemoved() throws Exception {
    reducePressure();
    assertEquals("1002.4", BarometerModel.pressure_msl_corrected);
    TemperatureModel.air_temp = "30.0";
    BarometerModel.refreshManualPressureReduction();
    assertEquals("1002.3", BarometerModel.pressure_msl_corrected);
    assertEquals("0023", BarometerModel.PPPP_code);
    TemperatureModel.air_temp = "0.0";
    BarometerModel.refreshManualPressureReduction();
    assertEquals("1002.5", BarometerModel.pressure_msl_corrected);
    assertEquals("0025", BarometerModel.PPPP_code);
    TemperatureModel.air_temp = "";
    BarometerModel.refreshManualPressureReduction();
    assertEquals("1002.4", BarometerModel.pressure_msl_corrected);
    assertEquals("0024", BarometerModel.PPPP_code);
  }

  @Test
  public void leavesAlreadySeaLevelPressureUnchanged() throws Exception {
    main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_YES;
    reducePressure();
    TemperatureModel.air_temp = "30.0";
    BarometerModel.refreshManualPressureReduction();
    assertEquals("1000.0", BarometerModel.pressure_msl_corrected);
  }

  @Test
  public void doesNotReuseManualReductionAfterResetOrReplacement() throws Exception {
    reducePressure();
    BarometerModel.pressure_msl_corrected = "1010.0";
    TemperatureModel.air_temp = "30.0";
    BarometerModel.refreshManualPressureReduction();
    assertEquals("1010.0", BarometerModel.pressure_msl_corrected);
    reducePressure();
    BarometerModel.clearManualReduction();
    TemperatureModel.air_temp = "0.0";
    BarometerModel.refreshManualPressureReduction();
    assertEquals("1002.3", BarometerModel.pressure_msl_corrected);
  }

  private void reducePressure() throws Exception {
    Method method = BarometerModel.class.getDeclaredMethod("MSL_Herleiding_Luchtdruk");
    method.setAccessible(true);
    method.invoke(barometer);
  }

  private void setInstance(String name, Object value) throws Exception {
    Field field = BarometerModel.class.getDeclaredField(name);
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
