package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232MintakaStarXObservationWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private GregorianCalendar originalObservationDate;
  private String originalAirTemp;
  private String originalWetBulbTemp;
  private String originalRh;
  private double originalDewPoint;
  private ObservableText originalAirField;
  private ObservableText originalWetBulbField;
  private ObservableText originalRhField;
  private boolean originalFrozenSelection;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObservationDate = main.obs_file_datum_tijd;
    originalAirTemp = TemperatureModel.air_temp;
    originalWetBulbTemp = TemperatureModel.wet_bulb_temp;
    originalRh = TemperatureModel.RH;
    originalDewPoint = TemperatureModel.double_dew_point;
    originalAirField = TemperatureModel.airInput;
    originalWetBulbField = TemperatureModel.wetBulbInput;
    originalRhField = TemperatureModel.humidityInput;
    originalFrozenSelection = TemperatureModel.selectedWetBulbFrozen;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    TemperatureModel.airInput = new ObservableText();
    TemperatureModel.wetBulbInput = new ObservableText();
    TemperatureModel.humidityInput = new ObservableText();
    resetModel();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.obs_file_datum_tijd = originalObservationDate;
    TemperatureModel.air_temp = originalAirTemp;
    TemperatureModel.wet_bulb_temp = originalWetBulbTemp;
    TemperatureModel.RH = originalRh;
    TemperatureModel.double_dew_point = originalDewPoint;
    TemperatureModel.airInput = originalAirField;
    TemperatureModel.wetBulbInput = originalWetBulbField;
    TemperatureModel.humidityInput = originalRhField;
    TemperatureModel.selectedWetBulbFrozen = originalFrozenSelection;
  }

  @Test
  public void validRecordParsesDewPointAndUpdatesWetBulbUiState() throws Exception {
    runRecord("20.0", "75.0", "-0.4", "12.3", "0");

    assertEquals("20.0", TemperatureModel.air_temp);
    assertEquals("-0.4", TemperatureModel.wet_bulb_temp);
    assertEquals("75.0", TemperatureModel.RH);
    assertEquals(12.3, TemperatureModel.double_dew_point, 0.0);
    assertEquals("20.0", TemperatureModel.airInput.get());
    assertEquals("-0.4", TemperatureModel.wetBulbInput.get());
    assertEquals("75.0", TemperatureModel.humidityInput.get());
    assertTrue(TemperatureModel.selectedWetBulbFrozen);

    runRecord("20.0", "75.0", "0.4", "12.3", "0");
    assertFalse(TemperatureModel.selectedWetBulbFrozen);
  }

  @Test
  public void missingMalformedAndBadChecksumRecordsClearObservationValues() throws Exception {
    seedExistingValues();
    runWorkflowWithoutRecord();
    assertCleared();

    resetModel();
    seedExistingValues();
    writeRawRecord(
        starRecord(starXPayload("20.0", "75.0", "0.4", "12.3", "0") + ",extra", recentDate()));
    runWorkflow();
    assertCleared();

    resetModel();
    seedExistingValues();
    String payload = starXPayload("20.0", "75.0", "0.4", "12.3", "0");
    writeRawRecord(payload + "*00" + main.sdf4.format(recentDate()));
    runWorkflow();
    assertCleared();
  }

  @Test
  public void staleAndInvalidObservationAgesClearAllStarXValues() throws Exception {
    seedExistingValues();
    writeRawRecord(
        starRecord(
            starXPayload("20.0", "75.0", "0.4", "12.3", "0"),
            new Date(System.currentTimeMillis() - 20 * 60 * 1000L)));
    runWorkflow();
    assertCleared();

    resetModel();
    seedExistingValues();
    runRecord(
        "20.0",
        "75.0",
        "0.4",
        "12.3",
        Integer.toString(main_RS232_RS422.MAX_AGE_STARX_OBS_DATA + 1));
    assertCleared();

    resetModel();
    seedExistingValues();
    runRecord("20.0", "75.0", "0.4", "12.3", "bad");
    assertCleared();
  }

  @Test
  public void legacyTemperatureHumidityBoundariesAndMissingDewPointArePreserved() throws Exception {
    runRecord("99.9", "100.0", "99.9", "-70.0", "0");
    assertEquals("99.9", TemperatureModel.air_temp);
    assertEquals("99.9", TemperatureModel.wet_bulb_temp);
    assertEquals("100.0", TemperatureModel.RH);
    assertEquals(-70.0, TemperatureModel.double_dew_point, 0.0);

    resetModel();
    runRecord("-99.9", "0.0", "-99.9", "bad", "0");
    assertEquals("", TemperatureModel.air_temp);
    assertEquals("", TemperatureModel.wet_bulb_temp);
    assertEquals("", TemperatureModel.RH);
    assertEquals(main.INVALID, TemperatureModel.double_dew_point, 0.0);

    resetModel();
    runRecord("", "", "", "", "0");
    assertCleared();
  }

  private void runRecord(String airTemp, String rh, String wetBulbTemp, String dewPoint, String age)
      throws Exception {
    writeRawRecord(starRecord(starXPayload(airTemp, rh, wetBulbTemp, dewPoint, age), recentDate()));
    runWorkflow();
  }

  private void runWorkflowWithoutRecord() throws Exception {
    runWorkflow();
  }

  private void runWorkflow() throws Exception {
    DialogCloser dialogCloser = new DialogCloser();
    dialogCloser.start();
    Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
    if (GraphicsEnvironment.isHeadless()) {
      Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
        if (!(error instanceof HeadlessException) && originalHandler != null) {
          originalHandler.uncaughtException(thread, error);
        }
      });
    }
    try {
      main.obs_file_datum_tijd = new GregorianCalendar();
      Rs232MintakaStarXObservationWorkflow.start();
      waitFor(() -> main.obs_file_datum_tijd == null);
      dialogCloser.await();
      flushEdt();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void seedExistingValues() {
    TemperatureModel.air_temp = "old";
    TemperatureModel.wet_bulb_temp = "old";
    TemperatureModel.RH = "old";
    TemperatureModel.double_dew_point = 10.0;
  }

  private void resetModel() {
    TemperatureModel.air_temp = "";
    TemperatureModel.wet_bulb_temp = "";
    TemperatureModel.RH = "";
    TemperatureModel.double_dew_point = main.INVALID;
    TemperatureModel.airInput.set("");
    TemperatureModel.wetBulbInput.set("");
    TemperatureModel.humidityInput.set("");
    TemperatureModel.selectedWetBulbFrozen = false;
  }

  private void assertCleared() {
    assertEquals("", TemperatureModel.air_temp);
    assertEquals("", TemperatureModel.wet_bulb_temp);
    assertEquals("", TemperatureModel.RH);
    assertEquals(main.INVALID, TemperatureModel.double_dew_point, 0.0);
  }

  private String starXPayload(
      String airTemp, String rh, String wetBulbTemp, String dewPoint, String age) {
    return "1000.0,1000.0,1.0,2,52 00.0000N,004 00.0000E,0,0,0,"
        + airTemp
        + ","
        + rh
        + ","
        + wetBulbTemp
        + ","
        + dewPoint
        + ","
        + age;
  }

  private String starRecord(String payload, Date recordDate) {
    String checksum = main_RS232_RS422.Mintaka_Star_Checksum(payload + "*");
    return payload + "*" + checksum + main.sdf4.format(recordDate);
  }

  private Date recentDate() {
    return new Date(System.currentTimeMillis() - 2 * 60 * 1000L);
  }

  private void writeRawRecord(String record) throws Exception {
    Calendar fileDate = new GregorianCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    File file =
        new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
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
    assertTrue(
        "Timed out waiting for Mintaka StarX observation workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      SwingUtilities.invokeAndWait(() -> {});
    }
  }

  private static final class DialogCloser {
    private Thread thread;

    void start() {
      if (GraphicsEnvironment.isHeadless()) {
        return;
      }
      thread =
          new Thread(() -> {
            long deadline = System.nanoTime() + 5_000_000_000L;
            while (System.nanoTime() < deadline) {
              for (Window window : Window.getWindows()) {
                if (window instanceof JDialog && window.isVisible()) {
                  SwingUtilities.invokeLater(window::dispose);
                  return;
                }
              }
              try {
                Thread.sleep(10);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
              }
            }
          });
      thread.setDaemon(true);
      thread.start();
    }

    void await() throws InterruptedException {
      if (thread != null) {
        thread.join(5_000);
      }
    }
  }
}
