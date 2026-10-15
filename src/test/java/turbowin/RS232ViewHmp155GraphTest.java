package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import sun.misc.Unsafe;

public class RS232ViewHmp155GraphTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private static final Unsafe UNSAFE = unsafe();
  private static final Method READ_GRAPH = readGraphMethod();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private SimpleDateFormat originalSdf2;
  private String originalMode;
  private String[] originalValues;
  private String[] originalTimes;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalSdf2 = RS232_view.sdf2;
    originalMode = RS232_view.mode_tijd_periode;
    originalValues = RS232_view.sensor_waarde_array_II.clone();
    originalTimes = RS232_view.datum_tijd_array_II.clone();

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    RS232_view.sdf2 = utcFormat("yyyyMMddHHmmss");
    RS232_view.mode_tijd_periode = RS232_view.MODE_DAY;
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    RS232_view.sdf2 = originalSdf2;
    RS232_view.mode_tijd_periode = originalMode;
    System.arraycopy(
        originalValues, 0, RS232_view.sensor_waarde_array_II, 0, originalValues.length);
    System.arraycopy(originalTimes, 0, RS232_view.datum_tijd_array_II, 0, originalTimes.length);
  }

  @Test
  public void fixedLengthRecordsUseMatchingHourAndFiveMinuteSlots() throws Exception {
    Date currentHour = currentHour();
    Date fiveMinutes = withMinute(currentHour, 5);
    Date sixMinutes = withMinute(currentHour, 6);
    Date wrongHour = new Date(fiveMinutes.getTime() - 60 * 60 * 1000L);

    writeRecord(currentHour, hmpRecord("12.3", main.sdf3.format(fiveMinutes) + "05"));
    writeRecord(currentHour, hmpRecord("99.9", main.sdf3.format(wrongHour) + "05"));
    writeRecord(currentHour, hmpRecord("22.2", main.sdf3.format(sixMinutes) + "06"));
    writeRecord(currentHour, hmpRecord("33.3", main.sdf3.format(currentHour) + "xx"));
    writeRecord(currentHour, "short");

    invokeReadGraph();

    int fiveMinuteSlot = (24 * 12) + 1;
    assertEquals("12.3", RS232_view.sensor_waarde_array_II[fiveMinuteSlot]);
    assertEquals(
        main.sdf3.format(fiveMinutes) + "05", RS232_view.datum_tijd_array_II[fiveMinuteSlot]);
    assertEquals("", RS232_view.sensor_waarde_array_II[fiveMinuteSlot + 1]);
  }

  @Test
  public void invalidTemperaturesCountOnlyInCurrentHour() throws Exception {
    Date currentHour = currentHour();
    Date previousHour = new Date(currentHour.getTime() - 60 * 60 * 1000L);
    for (int minute = 0; minute <= 20; minute += 5) {
      writeRecord(
          previousHour,
          hmpRecord(
              "bad",
              main.sdf3.format(withMinute(previousHour, minute)) + String.format("%02d", minute)));
    }
    for (int minute = 0; minute <= 15; minute += 5) {
      writeRecord(
          currentHour,
          hmpRecord(
              "bad",
              main.sdf3.format(withMinute(currentHour, minute)) + String.format("%02d", minute)));
    }

    String output = invokeReadGraphWithOutput();
    assertFalse(output.contains("more than 4"));

    writeRecord(
        currentHour,
        hmpRecord(
            "bad", main.sdf3.format(withMinute(currentHour, 20)) + String.format("%02d", 20)));
    output = invokeReadGraphWithOutput();
    assertTrue(output.contains("more than 4"));
  }

  @Test
  public void longMalformedCurrentRecordUsesTheRecordLengthMultiplier() throws Exception {
    Date currentHour = currentHour();
    writeRecord(currentHour, "x".repeat(20 * main.RECORD_LENGTE_HMP155 + 1));

    String output = invokeReadGraphWithOutput();

    assertTrue(output.contains("more than 20 records with wrong length"));
  }

  private String invokeReadGraphWithOutput() throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    try {
      System.setOut(new PrintStream(output));
      invokeReadGraph();
    } finally {
      System.setOut(originalOut);
    }
    return output.toString();
  }

  private void invokeReadGraph() throws Exception {
    RS232_view view = (RS232_view) UNSAFE.allocateInstance(RS232_view.class);
    Field dialogField = RS232_view.class.getDeclaredField("dialog_sensor_data_error_present");
    dialogField.setAccessible(true);
    dialogField.setBoolean(view, true);
    try {
      READ_GRAPH.invoke(view);
    } catch (InvocationTargetException exception) {
      Throwable cause = exception.getCause();
      if (cause instanceof Exception) {
        throw (Exception) cause;
      }
      throw exception;
    }
  }

  private void writeRecord(Date fileDate, String record) throws Exception {
    File file = new File(main.logs_dir, "sensor_data_II_" + main.sdf3.format(fileDate) + ".txt");
    Files.writeString(
        file.toPath(),
        record + System.lineSeparator(),
        java.nio.file.StandardOpenOption.CREATE,
        java.nio.file.StandardOpenOption.APPEND);
  }

  private String hmpRecord(String airTemperature, String timestamp) {
    return String.format("%-8s %-8s %-8s %-8s %s", airTemperature, "0.0", "0.0", "50.0", timestamp);
  }

  private static Date currentHour() {
    Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  private static Date withMinute(Date date, int minute) {
    Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    calendar.setTime(date);
    calendar.set(Calendar.MINUTE, minute);
    return calendar.getTime();
  }

  private static SimpleDateFormat utcFormat(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format;
  }

  private static Unsafe unsafe() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (Unsafe) field.get(null);
    } catch (ReflectiveOperationException exception) {
      throw new AssertionError(exception);
    }
  }

  private static Method readGraphMethod() {
    try {
      Method method =
          RS232_view.class.getDeclaredMethod("Read_Sensor_Data_Files_For_Air_Temp_Graph_HMP155");
      method.setAccessible(true);
      return method;
    } catch (ReflectiveOperationException exception) {
      throw new AssertionError(exception);
    }
  }
}
