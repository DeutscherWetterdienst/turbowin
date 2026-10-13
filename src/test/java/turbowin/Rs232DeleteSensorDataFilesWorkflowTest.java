package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232DeleteSensorDataFilesWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private int originalConnectionMode;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalConnectionMode = main.RS232_connection_mode;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.RS232_connection_mode = originalConnectionMode;
  }

  @Test
  public void ordinaryModeDeletesThe200HourToOneYearWindowOnly() throws Exception {
    main.RS232_connection_mode = 0;

    File newerFile = createSensorFile(199);
    File firstDeletedFile = createSensorFile(200);
    File lastDeletedFile = createSensorFile(8959);
    File olderFile = createSensorFile(8960);
    File secondSensorFile = createSecondSensorFile(200);

    new Rs232DeleteSensorDataFilesWorkflow().start();

    waitFor(
        () ->
            !firstDeletedFile.exists()
                && !lastDeletedFile.exists()
                && newerFile.exists()
                && olderFile.exists()
                && secondSensorFile.exists());

    assertTrue(newerFile.exists());
    assertFalse(firstDeletedFile.exists());
    assertFalse(lastDeletedFile.exists());
    assertTrue(olderFile.exists());
    assertTrue(secondSensorFile.exists());
  }

  @Test
  public void awsModeDeletesTheOneYearToTwoYearWindowOnly() throws Exception {
    main.RS232_connection_mode = 3;

    File newerFile = createSensorFile(8759);
    File firstDeletedFile = createSensorFile(8760);
    File lastDeletedFile = createSensorFile(26279);
    File olderFile = createSensorFile(26280);
    File secondSensorFile = createSecondSensorFile(8760);

    new Rs232DeleteSensorDataFilesWorkflow().start();

    waitFor(
        () ->
            !firstDeletedFile.exists()
                && !lastDeletedFile.exists()
                && newerFile.exists()
                && olderFile.exists()
                && secondSensorFile.exists());

    assertTrue(newerFile.exists());
    assertFalse(firstDeletedFile.exists());
    assertFalse(lastDeletedFile.exists());
    assertTrue(olderFile.exists());
    assertTrue(secondSensorFile.exists());
  }

  private File createSensorFile(int hoursAgo) throws Exception {
    File file = sensorFile(hoursAgo);
    assertTrue(file.createNewFile());
    return file;
  }

  private File createSecondSensorFile(int hoursAgo) throws Exception {
    File file = secondSensorFile(hoursAgo);
    assertTrue(file.createNewFile());
    return file;
  }

  private File sensorFile(int hoursAgo) {
    Calendar date = hoursAgoFromNow(hoursAgo);
    return new File(main.logs_dir, "sensor_data_" + main.sdf3.format(date.getTime()) + ".txt");
  }

  private File secondSensorFile(int hoursAgo) {
    Calendar date = hoursAgoFromNow(hoursAgo);
    return new File(
        main.logs_dir, "sensor_data_II_" + main.sdf3.format(date.getTime()) + ".txt");
  }

  private Calendar hoursAgoFromNow(int hoursAgo) {
    Calendar date = new GregorianCalendar();
    date.add(Calendar.HOUR_OF_DAY, -hoursAgo);
    return date;
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
    assertTrue("Timed out waiting for sensor file cleanup", condition.getAsBoolean());
  }
}
