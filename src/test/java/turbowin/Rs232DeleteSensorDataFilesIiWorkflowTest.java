package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
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

public class Rs232DeleteSensorDataFilesIiWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
  }

  @Test
  public void deletesOnlySecondSensorFilesInsideTheRetentionWindow() throws Exception {
    File justInsideRetention = createSensorFile(199);
    File firstDeletedFile = createSensorFile(200);
    File lastDeletedFile = createSensorFile(8959);
    File justOutsideRetention = createSensorFile(8960);
    File primarySensorFile = createPrimarySensorFile(200);

    new Rs232DeleteSensorDataFilesIiWorkflow().start();

    waitFor(
        () ->
            !firstDeletedFile.exists()
                && !lastDeletedFile.exists()
                && justInsideRetention.exists()
                && justOutsideRetention.exists()
                && primarySensorFile.exists());

    assertTrue(justInsideRetention.exists());
    assertFalse(firstDeletedFile.exists());
    assertFalse(lastDeletedFile.exists());
    assertTrue(justOutsideRetention.exists());
    assertTrue(primarySensorFile.exists());
  }

  private File createSensorFile(int hoursAgo) throws Exception {
    File file = sensorFile(hoursAgo);
    assertTrue(file.createNewFile());
    return file;
  }

  private File createPrimarySensorFile(int hoursAgo) throws Exception {
    Calendar date = hoursAgoFromNow(hoursAgo);
    File file = new File(main.logs_dir, "sensor_data_" + main.sdf3.format(date.getTime()) + ".txt");
    Files.writeString(file.toPath(), "must remain");
    return file;
  }

  private File sensorFile(int hoursAgo) {
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
    assertTrue("Timed out waiting for second sensor file cleanup", condition.getAsBoolean());
  }
}
