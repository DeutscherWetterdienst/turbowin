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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232VaisalaHmp155ObservationWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private String originalAirTemp;
  private String originalWetBulbTemp;
  private String originalRh;
  private double originalDewPoint;
  private double originalRelativeHumidity;
  private boolean originalWetBulbFrozen;
  private GregorianCalendar originalObservationDate;
  private JTextField originalTextField1;
  private JTextField originalTextField2;
  private JTextField originalTextField4;
  private JRadioButton originalFrozenButton;
  private JRadioButton originalNotFrozenButton;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalAirTemp = TemperatureModel.air_temp;
    originalWetBulbTemp = TemperatureModel.wet_bulb_temp;
    originalRh = TemperatureModel.RH;
    originalDewPoint = TemperatureModel.double_dew_point;
    originalRelativeHumidity = TemperatureModel.double_rv;
    originalWetBulbFrozen = TemperatureModel.wet_bulb_frozen;
    originalObservationDate = main.obs_file_datum_tijd;
    originalTextField1 = TemperatureModel.jTextField1;
    originalTextField2 = TemperatureModel.jTextField2;
    originalTextField4 = TemperatureModel.jTextField4;
    originalFrozenButton = TemperatureModel.jRadioButton4;
    originalNotFrozenButton = TemperatureModel.jRadioButton3;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    resetModel();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    TemperatureModel.air_temp = originalAirTemp;
    TemperatureModel.wet_bulb_temp = originalWetBulbTemp;
    TemperatureModel.RH = originalRh;
    TemperatureModel.double_dew_point = originalDewPoint;
    TemperatureModel.double_rv = originalRelativeHumidity;
    TemperatureModel.wet_bulb_frozen = originalWetBulbFrozen;
    main.obs_file_datum_tijd = originalObservationDate;
    TemperatureModel.jTextField1 = originalTextField1;
    TemperatureModel.jTextField2 = originalTextField2;
    TemperatureModel.jTextField4 = originalTextField4;
    TemperatureModel.jRadioButton4 = originalFrozenButton;
    TemperatureModel.jRadioButton3 = originalNotFrozenButton;
  }

  @Test
  public void readsTheFixedWidthFieldsAndUsesTheirOffsets() throws Exception {
    runRecord("20.0", "-0.1", "10.0", "50.0", new Date());

    assertEquals(padded("20.0"), TemperatureModel.jTextField1.getText());
    assertEquals(padded("-0.1"), TemperatureModel.jTextField2.getText());
    assertEquals(padded("50.0"), TemperatureModel.jTextField4.getText());
    assertEquals(10.0, TemperatureModel.double_dew_point, 0.0);
    assertEquals(0.5, TemperatureModel.double_rv, 0.0);
  }

  @Test
  public void malformedRecordIsRejectedAndClearsTheSensorValues() throws Exception {
    TemperatureModel.air_temp = "old";
    TemperatureModel.wet_bulb_temp = "old";
    TemperatureModel.RH = "old";
    TemperatureModel.double_dew_point = 10.0;
    TemperatureModel.double_rv = 0.5;

    runErrorRecord("malformed");

    assertCleared();
  }

  @Test
  public void staleRecordIsRejectedAndClearsTheSensorValues() throws Exception {
    writeRecord(
        "20.0", "0.0", "10.0", "50.0", new Date(System.currentTimeMillis() - 20 * 60 * 1000L));
    TemperatureModel.air_temp = "old";
    TemperatureModel.RH = "old";

    runErrorWorkflow();

    assertCleared();
  }

  @Test
  public void temperatureAndDewPointLowerBoundariesArePreserved() throws Exception {
    runRecord("99.9", "0.0", "69.9", "50.0", new Date());
    assertEquals(padded("99.9"), TemperatureModel.air_temp);
    assertEquals(69.9, TemperatureModel.double_dew_point, 0.0);

    resetModel();
    runRecord("-99.9", "0.0", "-70.0", "50.0", new Date());
    assertEquals("", TemperatureModel.air_temp);
    assertEquals(main.INVALID, TemperatureModel.double_dew_point, 0.0);

    resetModel();
    runRecord("100.0", "0.0", "70.0", "100.0", new Date());
    assertEquals(padded("100.0"), TemperatureModel.air_temp);
    assertEquals(70.0, TemperatureModel.double_dew_point, 0.0);
    assertEquals(1.0, TemperatureModel.double_rv, 0.0);
  }

  @Test
  public void relativeHumidityZeroIsRejectedWhilePositiveBoundaryIsAccepted() throws Exception {
    runRecord("20.0", "0.0", "10.0", "0.1", new Date());
    assertEquals(0.001, TemperatureModel.double_rv, 0.0);

    resetModel();
    writeRecord("20.0", "0.0", "10.0", "0.0", new Date());
    runErrorWorkflow();
    assertEquals(padded("20.0"), TemperatureModel.air_temp);
    assertEquals(padded("0.0"), TemperatureModel.wet_bulb_temp);
    assertEquals("", TemperatureModel.RH);
    assertEquals(10.0, TemperatureModel.double_dew_point, 0.0);
    assertEquals(main.INVALID, TemperatureModel.double_rv, 0.0);
  }

  @Test
  public void sensorMarkersAreNotParsedAsNumericValues() throws Exception {
    runRecord("*", "*", "*", "*", new Date());

    assertEquals(padded("*"), TemperatureModel.jTextField1.getText());
    assertEquals(padded("*"), TemperatureModel.jTextField2.getText());
    assertEquals(padded("*"), TemperatureModel.jTextField4.getText());
    assertEquals(main.INVALID, TemperatureModel.double_dew_point, 0.0);
    assertEquals(main.INVALID, TemperatureModel.double_rv, 0.0);
  }

  @Test
  public void wetBulbTemperatureSelectsFrozenOrNotFrozenState() throws Exception {
    runRecord("20.0", "-0.1", "10.0", "50.0", new Date());
    assertTrue(TemperatureModel.jRadioButton4.isSelected());
    assertFalse(TemperatureModel.jRadioButton3.isSelected());

    resetModel();
    runRecord("20.0", "0.0", "10.0", "50.0", new Date());
    assertTrue(TemperatureModel.jRadioButton3.isSelected());
    assertFalse(TemperatureModel.jRadioButton4.isSelected());
  }

  @Test
  public void missingRecordUsesTheErrorPath() throws Exception {
    runErrorWorkflow();

    assertCleared();
  }

  private void runRecord(
      String airTemp, String wetBulbTemp, String dewPoint, String rh, Date recordDate)
      throws Exception {
    writeRecord(airTemp, wetBulbTemp, dewPoint, rh, recordDate);
    main.obs_file_datum_tijd = new GregorianCalendar();
    Rs232VaisalaHmp155ObservationWorkflow.start();
    // Wait for the final UI update. The raw air-temperature value can be cleared by done()
    // validation, so observing that temporary value races with the EDT at lower boundaries.
    AtomicReference<String> displayedHumidity = new AtomicReference<>();
    long deadline = System.nanoTime() + 5_000_000_000L;
    do {
      SwingUtilities.invokeAndWait(
          () -> displayedHumidity.set(TemperatureModel.jTextField4.getText()));
      if (padded(rh).equals(displayedHumidity.get())) {
        return;
      }
      Thread.sleep(10);
    } while (System.nanoTime() < deadline);
    assertEquals("Timed out waiting for HMP155 UI completion", padded(rh), displayedHumidity.get());
  }

  private void runErrorRecord(String record) throws Exception {
    writeRawRecord(record);
    runErrorWorkflow();
  }

  private void runErrorWorkflow() throws Exception {
    DialogCloser dialogCloser = new DialogCloser();
    dialogCloser.start();
    main.obs_file_datum_tijd = new GregorianCalendar();
    Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
    if (GraphicsEnvironment.isHeadless()) {
      Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
        if (!(error instanceof HeadlessException) && originalHandler != null) {
          originalHandler.uncaughtException(thread, error);
        }
      });
    }
    try {
      Rs232VaisalaHmp155ObservationWorkflow.start();
      waitFor(() -> main.obs_file_datum_tijd == null);
      dialogCloser.await();
      flushEdt();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void writeRecord(
      String airTemp, String wetBulbTemp, String dewPoint, String rh, Date recordDate)
      throws Exception {
    String record =
        field(airTemp)
            + " "
            + field(wetBulbTemp)
            + " "
            + field(dewPoint)
            + " "
            + field(rh)
            + " "
            + main.sdf4.format(recordDate);
    writeRawRecord(record);
  }

  private void writeRawRecord(String record) throws Exception {
    Calendar fileDate = new GregorianCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    File file =
        new File(main.logs_dir, "sensor_data_II_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
  }

  private void resetModel() {
    TemperatureModel.air_temp = "";
    TemperatureModel.wet_bulb_temp = "";
    TemperatureModel.RH = "";
    TemperatureModel.double_dew_point = main.INVALID;
    TemperatureModel.double_rv = main.INVALID;
    TemperatureModel.jTextField1 = new JTextField();
    TemperatureModel.jTextField2 = new JTextField();
    TemperatureModel.jTextField4 = new JTextField();
    TemperatureModel.jRadioButton3 = new JRadioButton();
    TemperatureModel.jRadioButton4 = new JRadioButton();
    TemperatureModel.jRadioButton3.setSelected(false);
    TemperatureModel.jRadioButton4.setSelected(false);
  }

  private void assertCleared() {
    assertEquals("", TemperatureModel.air_temp);
    assertEquals("", TemperatureModel.wet_bulb_temp);
    assertEquals("", TemperatureModel.RH);
    assertEquals(main.INVALID, TemperatureModel.double_dew_point, 0.0);
    assertEquals(main.INVALID, TemperatureModel.double_rv, 0.0);
  }

  private static String field(String value) {
    return padded(value);
  }

  private static String padded(String value) {
    return String.format("%-8s", value);
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
    assertTrue("Timed out waiting for HMP155 observation workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    Thread.sleep(50);
    SwingUtilities.invokeAndWait(() -> {});
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
