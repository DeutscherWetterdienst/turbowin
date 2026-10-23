package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs422ReadAndSendSensorDataForAwsrWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
    replaceStaticTextFields(main.class);

    File logsDirectory = temporaryFolder.newFolder("logs");
    new File(logsDirectory, main.TURBOWIN_SYSTEM_LOGS_DIR).mkdirs();
    main.logs_dir = logsDirectory.getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf_tsl_1 = utcFormat("MMM_yyyy");
    main.sdf_tsl_2 = utcFormat("dd-MMM-yyyy HH:mm:ss");
    main.AWSR = true;
    main.APR = false;
    main.RS232_connection_mode = 3;
    main.port_mode_option = false;
    main.wind_units = main.M_S;
    main.sst_exposure = main.BUCKET;
    main.max_height_deck_cargo = "0";
    main.APTR_AWSR_send_method = "unknown";

    owner = new main_RS232_RS422();
  }

  @After
  public void tearDown() throws Exception {
    restoreStaticState();
  }

  @Test
  public void selectsLastPeumbRecordAndPreservesFixedFieldConversions() throws Exception {
    RecordTime recordTime = recordTime();
    String first = awsRecord(recordTime, "12.345", "-123.456", "90.0", "2.83");
    String last = awsRecord(recordTime, "-12.345", "123.456", "0.0", "2.83");
    writeSensorFile(
        "not-a-peumb-record\n"
            + awsRecord(recordTime, "9.000", "9.000", "45.0", "1.00")
            + "\n"
            + first
            + "\n"
            + last);

    startWorkflow();
    waitFor(() -> !mydatetime.year.isEmpty());
    flushEdt();

    assertEquals("12", myposition.latitude_degrees);
    assertEquals(myposition.HEMISPHERE_SOUTH, myposition.latitude_hemisphere);
    assertEquals(39, myposition.int_latitude_minutes);
    assertEquals("123", myposition.longitude_degrees);
    assertEquals(myposition.HEMISPHERE_EAST, myposition.longitude_hemisphere);
    assertEquals(27, myposition.int_longitude_minutes);
    assertEquals("8", myposition.Ds_code);
    assertEquals("360", mywind.COG_code);
    assertEquals("2", myposition.vs_code);
    assertEquals(2.83 * owner.M_S_TO_KNOTS, myposition.SOG_APR_wind, 0.0001);
    assertEquals("1000.5", mybarometer.pressure_msl_corrected);
    assertEquals("0005", mybarometer.PPPP_code);
    assertEquals("20.5", mytemp.air_temp);
    assertEquals(0.53, mytemp.double_rv, 0.0001);
    assertEquals(10.6578, mytemp.double_dew_point, 0.0001);
    assertEquals("0", mytemp.sn_TdTdTd_code);
    assertEquals("107", mytemp.TdTdTd_code);
    assertEquals("005", mywind.RWS_code);
    assertEquals("350", mywind.RWD_code);
    assertTrue(Files.readString(systemLogFile()).contains("send method unknown"));
  }

  @Test
  public void missingEmptyMalformedAndUnreadableFilesClearAwsrState() throws Exception {
    myposition.latitude_degrees = "stale";
    startWorkflow();
    settleWorkflow();
    assertEquals("", myposition.latitude_degrees);
    assertEquals("", mydatetime.year);

    writeSensorFile("");
    myposition.latitude_degrees = "stale";
    startWorkflow();
    settleWorkflow();
    assertEquals("", myposition.latitude_degrees);

    writeSensorFile(
        awsRecord(recordTime(), "12.345", "123.456", "90.0", "2.83")
            .replaceFirst(",", ""));
    myposition.latitude_degrees = "stale";
    startWorkflow();
    settleWorkflow();
    assertEquals("", myposition.latitude_degrees);
    assertEquals("", mydatetime.year);

    File sensorFile = sensorDataFile();
    assertTrue(sensorFile.delete());
    assertTrue(sensorFile.mkdir());
    myposition.latitude_degrees = "stale";
    startWorkflow();
    settleWorkflow();
    assertEquals("", myposition.latitude_degrees);
    assertEquals("", mydatetime.year);
  }

  @Test
  public void validatesDateTimeRoundsObservationHourAndRejectsStaleRecords() throws Exception {
    RecordTime recordTime = recordTime();
    assertTrue(owner.RS422_Set_Date_Time_Obs_And_IMMT_For_AWSR(recordTime.date, recordTime.time));
    assertEquals(recordTime.expectedHour, mydatetime.GG_code);
    assertFalse(owner.RS422_Set_Date_Time_Obs_And_IMMT_For_AWSR("2026", "000000"));
    assertFalse(owner.RS422_Check_Date_Time_Last_Saved_Record_For_AWSR("2026", "000000"));
    assertTrue(owner.RS422_Check_Date_Time_Last_Saved_Record_For_AWSR(recordTime.date, recordTime.time));

    RecordTime stale = recordTime(20);
    assertFalse(owner.RS422_Check_Date_Time_Last_Saved_Record_For_AWSR(stale.date, stale.time));
  }

  @Test
  public void staleRecordIsNotSentAndIsReset() throws Exception {
    RecordTime stale = recordTime(20);
    writeSensorFile(awsRecord(stale, "12.345", "123.456", "90.0", "2.83"));
    startWorkflow();
    settleWorkflow();

    assertEquals("", mydatetime.year);
    assertEquals("", myposition.latitude_degrees);
    assertFalse(Files.exists(systemLogFile()) && Files.readString(systemLogFile()).contains("send method unknown"));
  }

  private void startWorkflow() {
    new Rs422ReadAndSendSensorDataForAwsrWorkflow(owner).start(false);
  }

  private void settleWorkflow() throws Exception {
    Thread.sleep(250);
    flushEdt();
  }

  private String awsRecord(
      RecordTime recordTime, String latitude, String longitude, String cog, String sog) {
    return String.join(
        ",",
        "$PEUMB",
        recordTime.date,
        recordTime.time,
        latitude,
        longitude,
        cog,
        sog,
        "180.4",
        "1006.6",
        "1000.5",
        "2.3",
        "2",
        "20.5",
        "53.0",
        "9.3",
        "4.6",
        "350.3",
        "4.2",
        "350.3",
        "7.8",
        "358.3",
        "23.1",
        "0",
        "10",
        "",
        "",
        "",
        recordTime.fileKey + "15");
  }

  private void writeSensorFile(String contents) throws Exception {
    Files.writeString(sensorDataFile().toPath(), contents);
  }

  private File sensorDataFile() {
    return new File(main.logs_dir, "sensor_data_" + recordTime().fileKey + ".txt");
  }

  private Path systemLogFile() {
    return new File(
            main.logs_dir,
            main.TURBOWIN_SYSTEM_LOGS_DIR
                + File.separator
                + SystemLogWriterWorkflow.logFileName(main.sdf_tsl_1.format(new java.util.Date())))
        .toPath();
  }

  private RecordTime recordTime() {
    return recordTime(2);
  }

  private RecordTime recordTime(int minutesAgo) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.add(Calendar.MINUTE, -minutesAgo);
    String date = utcFormat("yyyyMMdd").format(calendar.getTime());
    String time = utcFormat("HHmmss").format(calendar.getTime());
    String fileKey = main.sdf3.format(calendar.getTime());
    GregorianCalendar rounded = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    rounded.setTime(calendar.getTime());
    if (rounded.get(Calendar.MINUTE) > 30) {
      rounded.add(Calendar.HOUR_OF_DAY, 1);
    }
    return new RecordTime(date, time, fileKey, String.format("%02d", rounded.get(Calendar.HOUR_OF_DAY)));
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

  private void replaceStaticTextFields(Class<?> type) throws Exception {
    for (Field field : type.getDeclaredFields()) {
      if (field.getType() == JTextField.class && Modifier.isStatic(field.getModifiers())) {
        field.setAccessible(true);
        field.set(null, new JTextField());
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
    assertTrue("Timed out waiting for AWSR workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    SwingUtilities.invokeAndWait(() -> {});
  }

  private static final class RecordTime {
    private final String date;
    private final String time;
    private final String fileKey;
    private final String expectedHour;

    private RecordTime(String date, String time, String fileKey, String expectedHour) {
      this.date = date;
      this.time = time;
      this.fileKey = fileKey;
      this.expectedHour = expectedHour;
    }
  }
}
