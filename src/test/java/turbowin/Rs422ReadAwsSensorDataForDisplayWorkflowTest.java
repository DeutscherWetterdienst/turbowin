package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs422ReadAwsSensorDataForDisplayWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private final Map<Field, Object> originalStaticState = new HashMap<>();
  private main_RS232_RS422 owner;

  @Before
  public void setUp() throws Exception {
    captureStaticState(
        main.class,
        main_RS232_RS422.class,
        DateTimeModel.class,
        PositionModel.class,
        WindModel.class,
        BarometerModel.class,
        BarographModel.class,
        TemperatureModel.class);
    replaceStaticTextFields(main.class);
    main.jMenuItem46 = new JMenuItem();

    main.logs_dir = temporaryFolder.newFolder("logs").getPath();
    new File(main.logs_dir, main.TURBOWIN_SYSTEM_LOGS_DIR).mkdirs();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf_tsl_2 = utcFormat("dd-MMM-yyyy HH:mm:ss");
    main.RS232_connection_mode = 3;
    main.APR = false;
    main.AWSR = false;
    main.wind_units = main.M_S;
    main.eucaws_uploads_method = "";
    main.VOT = Integer.MAX_VALUE;

    owner = new main_RS232_RS422();
    owner.color_dark_green = new Color(76, 153, 0);
  }

  @After
  public void tearDown() throws Exception {
    restoreStaticState();
  }

  @Test
  public void missingEmptyAndMalformedFilesDoNotUpdateAwsDisplayState() throws Exception {
    runAndSettle();
    assertFalse(main.date_from_AWS_present);
    assertEquals("", DateTimeModel.year);

    writeSensorFile("");
    runAndSettle();
    assertFalse(main.date_from_AWS_present);
    assertEquals("", PositionModel.latitude_degrees);

    writeSensorFile("$PEUMB," + sensorFileKey() + ",malformed-record");
    runAndSettle();
    assertFalse(main.date_from_AWS_present);
    assertEquals("", WindModel.ship_ground_speed);
  }

  @Test
  public void selectsLastMatchingRecordAndConvertsDateCoordinatesAndDisplayValues()
      throws Exception {
    String key = sensorFileKey();
    String first = awsRecord(key + "15", "20260408", "061530", "11.100", "22.200", "90.0");
    String last = awsRecord(key + "45", "20260409", "071530", "-12.345", "123.456", "22.6");
    writeSensorFile("not-a-peumb-record\n" + first + System.lineSeparator() + last);

    runUntilParsed();

    assertEquals("2026", DateTimeModel.year);
    assertEquals("April", DateTimeModel.month);
    assertEquals("9", DateTimeModel.day);
    assertEquals("7", DateTimeModel.hour);
    assertEquals("15", DateTimeModel.minute);
    assertEquals("12", PositionModel.latitude_degrees);
    assertEquals("21", PositionModel.latitude_minutes);
    assertEquals("South", PositionModel.latitude_hemisphere);
    assertEquals("123", PositionModel.longitude_degrees);
    assertEquals("27", PositionModel.longitude_minutes);
    assertEquals("East", PositionModel.longitude_hemisphere);
    assertEquals(PositionModel.COURSE_023_067, PositionModel.course);
    assertEquals("23", WindModel.ship_ground_course);
    assertEquals(PositionModel.SPEED_6_10, PositionModel.speed);
    assertEquals("5.5", WindModel.ship_ground_speed);
    assertEquals(10.6578, TemperatureModel.double_dew_point, 0.0001);
    assertEquals("0", TemperatureModel.sn_TdTdTd_code);
    assertEquals("107", TemperatureModel.TdTdTd_code);

    assertEquals("9 April 2026", main_RS232_RS422.dashboard_string_last_update_record_date);
    assertEquals("7:15", main_RS232_RS422.dashboard_string_last_update_record_time);
    assertEquals("12\u00B021' S", main_RS232_RS422.dashboard_string_last_update_record_latitude);
    assertEquals("123\u00B027' E", main_RS232_RS422.dashboard_string_last_update_record_longitude);
    assertEquals("5.5", main_RS232_RS422.dashboard_string_last_update_record_SOG);
    assertEquals("23", main_RS232_RS422.dashboard_string_last_update_record_COG);
    assertEquals("180", main_RS232_RS422.dashboard_string_last_update_record_heading);
    assertEquals("9 April 2026  7.15 UTC", main.jTextField3.getText());
    assertEquals("12\u00B0 - 21' S  123\u00B0 - 27' E", main.jTextField5.getText());
  }

  @Test
  public void convertsRelativeWindUsingConfiguredUnitsAndRoundingBoundaries() throws Exception {
    writeSensorFile(
        awsRecord(sensorFileKey() + "15", "20260409", "071530", "12.345", "123.456", "90.0"));

    main.wind_units = main.M_S;
    runUntilParsed();
    assertEquals("5", WindModel.wind_speed);
    assertEquals(5, WindModel.int_relative_wind_speed);
    assertEquals(4.6, WindModel.double_relative_wind_speed, 0.0001);

    main.wind_units = main.KNOTS;
    runUntilParsed();
    assertEquals("9", WindModel.wind_speed);
    assertEquals(9, WindModel.int_relative_wind_speed);
    assertEquals(8.941684654, WindModel.double_relative_wind_speed, 0.0001);
  }

  @Test
  public void handlesAllSpecialVotValuesAndUpdatesAwsMenuState() throws Exception {
    int[] votValues = {-5, 100, 101, 102, 103, 104, 10};
    String[] expectedMessages = {
      "25 minutes to go before the obs will be sent by the AWS, please do not insert observation parameters",
      "ship is in port (no obs will be sent)",
      "observations are not required in this area (no obs will be sent)",
      "the AWS transmitter is out of order (no obs will be sent)",
      "the AWS GPS is out of order (no obs will be sent)",
      "AWS transmission is deactivated (no obs will be sent)",
      "10 minutes to go before the obs will be sent by the AWS, you can now insert observation parameters and select \"Output -> Obs to AWS\" "
    };

    for (int i = 0; i < votValues.length; i++) {
      writeSensorFile(
          awsRecord(
              sensorFileKey() + "15",
              "20260409",
              "071530",
              "12.345",
              "123.456",
              "90.0",
              votValues[i]));
      main.jMenuItem46.setEnabled(true);

      runUntilVotParsed();

      assertEquals(votValues[i], main.VOT);
      assertEquals(expectedMessages[i], main.jTextField4.getText());
      assertEquals(votValues[i] == 10, main.jMenuItem46.isEnabled());
    }
  }

  private String awsRecord(
      String outputTimestamp,
      String date,
      String time,
      String latitude,
      String longitude,
      String cog) {
    return awsRecord(outputTimestamp, date, time, latitude, longitude, cog, 10);
  }

  private String awsRecord(
      String outputTimestamp,
      String date,
      String time,
      String latitude,
      String longitude,
      String cog,
      int vot) {
    return String.join(
        ",",
        "$PEUMB",
        date,
        time,
        latitude,
        longitude,
        cog,
        "2.83",
        "180.4",
        "1006.6",
        "1000.5",
        "2.3",
        "2",
        "20.5",
        "53.0",
        "9.3",
        "4.6",
        "128.3",
        "4.2",
        "350.3",
        "7.8",
        "358.3",
        "23.1",
        "0",
        Integer.toString(vot),
        "",
        "",
        "",
        outputTimestamp);
  }

  private void writeSensorFile(String contents) throws Exception {
    Files.writeString(sensorDataFile().toPath(), contents);
  }

  private File sensorDataFile() {
    return new File(main.logs_dir, "sensor_data_" + sensorFileKey() + ".txt");
  }

  private String sensorFileKey() {
    GregorianCalendar calendar = new GregorianCalendar();
    if (calendar.get(Calendar.MINUTE) != 0) {
      calendar.add(Calendar.MINUTE, -1);
    }
    return main.sdf3.format(calendar.getTime());
  }

  private void runAndSettle() throws Exception {
    new Rs422ReadAwsSensorDataForDisplayWorkflow(owner).start();
    Thread.sleep(150);
    flushEdt();
  }

  private void runUntilParsed() throws Exception {
    new Rs422ReadAwsSensorDataForDisplayWorkflow(owner).start();
    waitFor(() -> main.date_from_AWS_present && main.time_from_AWS_present);
    waitFor(() -> main_RS232_RS422.dashboard_string_last_update_record_date.contains("April"));
    flushEdt();
  }

  private void runUntilVotParsed() throws Exception {
    new Rs422ReadAwsSensorDataForDisplayWorkflow(owner).start();
    waitFor(() -> main.VOT_from_AWS_present);
    flushEdt();
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
    assertTrue("Timed out waiting for AWS display workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    SwingUtilities.invokeAndWait(() -> {});
  }
}
