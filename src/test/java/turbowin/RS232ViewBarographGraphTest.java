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
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import sun.misc.Unsafe;

public class RS232ViewBarographGraphTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private static final Unsafe UNSAFE = unsafe();
  private static final Method READ_GRAPH = readGraphMethod();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private SimpleDateFormat originalSdf2;
  private String originalMode;
  private int originalConnectionMode;
  private String[] originalValues;
  private String[] originalTimes;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalSdf2 = RS232_view.sdf2;
    originalMode = RS232_view.mode_tijd_periode;
    originalConnectionMode = main.RS232_connection_mode;
    originalValues = RS232_view.sensor_waarde_array.clone();
    originalTimes = RS232_view.datum_tijd_array.clone();

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
    main.RS232_connection_mode = originalConnectionMode;
    System.arraycopy(originalValues, 0, RS232_view.sensor_waarde_array, 0, originalValues.length);
    System.arraycopy(originalTimes, 0, RS232_view.datum_tijd_array, 0, originalTimes.length);
  }

  @Test
  public void ptb220AndPtb330UseMatchingHoursAndFiveMinuteSlots() throws Exception {
    for (int connectionMode : new int[] {1, 2}) {
      main.RS232_connection_mode = connectionMode;
      Date currentHour = currentHour();
      Date fiveMinutes = withMinute(currentHour, 5);
      Date sixMinutes = withMinute(currentHour, 6);
      Date wrongHour = new Date(fiveMinutes.getTime() - 60 * 60 * 1000L);

      writeRecords(
          currentHour,
          List.of(
              barographRecord(connectionMode, "1000.0", timestamp(fiveMinutes)),
              barographRecord(connectionMode, "850.0", timestamp(wrongHour)),
              barographRecord(connectionMode, "950.0", timestamp(sixMinutes)),
              barographRecord(connectionMode, "1050.0", hourTimestamp(currentHour) + "xx"),
              "short"));

      invokeReadGraph();

      int fiveMinuteSlot = (24 * 12) + 1;
      assertEquals("1000.0", RS232_view.sensor_waarde_array[fiveMinuteSlot]);
      assertEquals(timestamp(fiveMinutes), RS232_view.datum_tijd_array[fiveMinuteSlot]);
      assertEquals("", RS232_view.sensor_waarde_array[fiveMinuteSlot + 1]);
      assertFalse(containsValue("1050.0"));
    }
  }

  @Test
  public void pressureBoundariesAndCurrentHourThresholdArePreserved() throws Exception {
    main.RS232_connection_mode = 1;
    Date currentHour = currentHour();
    Date previousHour = new Date(currentHour.getTime() - 60 * 60 * 1000L);
    List<String> previousRecords = new ArrayList<>();
    for (int minute = 0; minute <= 20; minute += 5) {
      previousRecords.add(barographRecord(1, "bad", timestamp(withMinute(previousHour, minute))));
    }
    writeRecords(previousHour, previousRecords);
    writeRecords(
        currentHour,
        List.of(
            barographRecord(1, "900.0", timestamp(withMinute(currentHour, 0))),
            barographRecord(1, "1100.0", timestamp(withMinute(currentHour, 5))),
            barographRecord(1, "899.9", timestamp(withMinute(currentHour, 10))),
            barographRecord(1, "1100.1", timestamp(withMinute(currentHour, 15))),
            barographRecord(1, "bad", timestamp(withMinute(currentHour, 20)))));

    String output = invokeReadGraphWithOutput();
    assertEquals("900.0", RS232_view.sensor_waarde_array[24 * 12]);
    assertEquals("1100.0", RS232_view.sensor_waarde_array[(24 * 12) + 1]);
    assertFalse(output.contains("more than 4"));

    appendRecord(currentHour, barographRecord(1, "bad", timestamp(withMinute(currentHour, 25))));
    output = invokeReadGraphWithOutput();
    assertFalse(output.contains("more than 4"));

    appendRecord(currentHour, barographRecord(1, "bad", timestamp(withMinute(currentHour, 30))));
    output = invokeReadGraphWithOutput();
    assertTrue(output.contains("more than 4"));
  }

  @Test
  public void longMalformedCurrentRecordUsesTheRecordLengthMultiplier() throws Exception {
    main.RS232_connection_mode = 1;
    Date currentHour = currentHour();
    Date previousHour = new Date(currentHour.getTime() - 60 * 60 * 1000L);
    writeRecords(previousHour, List.of("bad", "also bad", "still bad"));
    writeRecords(
        currentHour, List.of(barographRecord(1, "1000.0", timestamp(withMinute(currentHour, 5)))));

    String output = invokeReadGraphWithOutput();
    assertFalse(output.contains("more than 20 records with wrong length"));

    appendRecord(currentHour, "x".repeat(20 * main.RECORD_LENGTE_PTB220 + 1));
    output = invokeReadGraphWithOutput();
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

  private void writeRecords(Date fileDate, List<String> records) throws Exception {
    File file = dataFile(fileDate);
    Files.writeString(
        file.toPath(),
        String.join(System.lineSeparator(), records) + System.lineSeparator(),
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  private void appendRecord(Date fileDate, String record) throws Exception {
    Files.writeString(
        dataFile(fileDate).toPath(),
        record + System.lineSeparator(),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
  }

  private File dataFile(Date fileDate) {
    return new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate) + ".txt");
  }

  private String barographRecord(int connectionMode, String pressure, String timestamp) {
    int recordLength = connectionMode == 1 ? main.RECORD_LENGTE_PTB220 : main.RECORD_LENGTE_PTB330;
    int datePosition =
        connectionMode == 1
            ? main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220
            : main.RECORD_DATUM_TIJD_BEGIN_POS_PTB330;
    char[] record = " ".repeat(recordLength).toCharArray();
    copy(record, 0, padded(pressure, 7));
    copy(record, datePosition, timestamp);
    return new String(record);
  }

  private boolean containsValue(String expected) {
    for (String value : RS232_view.sensor_waarde_array) {
      if (expected.equals(value)) {
        return true;
      }
    }
    return false;
  }

  private String timestamp(Date date) {
    return main.sdf4.format(date);
  }

  private String hourTimestamp(Date date) {
    return main.sdf3.format(date);
  }

  private static void copy(char[] target, int start, String value) {
    for (int index = 0; index < value.length(); index++) {
      target[start + index] = value.charAt(index);
    }
  }

  private static String padded(String value, int width) {
    return String.format("%-" + width + "s", value);
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
      Method method = RS232_view.class.getDeclaredMethod("Read_Sensor_Data_Files_For_Barograph");
      method.setAccessible(true);
      return method;
    } catch (ReflectiveOperationException exception) {
      throw new AssertionError(exception);
    }
  }
}
