package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Container;
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
import javax.swing.JOptionPane;
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
    originalAirTemp = mytemp.air_temp;
    originalWetBulbTemp = mytemp.wet_bulb_temp;
    originalRh = mytemp.RH;
    originalDewPoint = mytemp.double_dew_point;
    originalRelativeHumidity = mytemp.double_rv;
    originalWetBulbFrozen = mytemp.wet_bulb_frozen;
    originalObservationDate = main.obs_file_datum_tijd;
    originalTextField1 = mytemp.jTextField1;
    originalTextField2 = mytemp.jTextField2;
    originalTextField4 = mytemp.jTextField4;
    originalFrozenButton = mytemp.jRadioButton4;
    originalNotFrozenButton = mytemp.jRadioButton3;

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
    mytemp.air_temp = originalAirTemp;
    mytemp.wet_bulb_temp = originalWetBulbTemp;
    mytemp.RH = originalRh;
    mytemp.double_dew_point = originalDewPoint;
    mytemp.double_rv = originalRelativeHumidity;
    mytemp.wet_bulb_frozen = originalWetBulbFrozen;
    main.obs_file_datum_tijd = originalObservationDate;
    mytemp.jTextField1 = originalTextField1;
    mytemp.jTextField2 = originalTextField2;
    mytemp.jTextField4 = originalTextField4;
    mytemp.jRadioButton4 = originalFrozenButton;
    mytemp.jRadioButton3 = originalNotFrozenButton;
  }

  @Test
  public void readsTheFixedWidthFieldsAndUsesTheirOffsets() throws Exception {
    runRecord("20.0", "-0.1", "10.0", "50.0", new Date());

    assertEquals(padded("20.0"), mytemp.jTextField1.getText());
    assertEquals(padded("-0.1"), mytemp.jTextField2.getText());
    assertEquals(padded("50.0"), mytemp.jTextField4.getText());
    assertEquals(10.0, mytemp.double_dew_point, 0.0);
    assertEquals(0.5, mytemp.double_rv, 0.0);
  }

  @Test
  public void malformedRecordIsRejectedAndClearsTheSensorValues() throws Exception {
    mytemp.air_temp = "old";
    mytemp.wet_bulb_temp = "old";
    mytemp.RH = "old";
    mytemp.double_dew_point = 10.0;
    mytemp.double_rv = 0.5;

    runErrorRecord("malformed");

    assertCleared();
  }

  @Test
  public void staleRecordIsRejectedAndClearsTheSensorValues() throws Exception {
    writeRecord(
        "20.0", "0.0", "10.0", "50.0", new Date(System.currentTimeMillis() - 20 * 60 * 1000L));
    mytemp.air_temp = "old";
    mytemp.RH = "old";

    runErrorWorkflow();

    assertCleared();
  }

  @Test
  public void temperatureAndDewPointLowerBoundariesArePreserved() throws Exception {
    runRecord("99.9", "0.0", "69.9", "50.0", new Date());
    assertEquals(padded("99.9"), mytemp.air_temp);
    assertEquals(69.9, mytemp.double_dew_point, 0.0);

    resetModel();
    runRecord("-99.9", "0.0", "-70.0", "50.0", new Date());
    assertEquals("", mytemp.air_temp);
    assertEquals(main.INVALID, mytemp.double_dew_point, 0.0);

    resetModel();
    runRecord("100.0", "0.0", "70.0", "100.0", new Date());
    assertEquals(padded("100.0"), mytemp.air_temp);
    assertEquals(70.0, mytemp.double_dew_point, 0.0);
    assertEquals(1.0, mytemp.double_rv, 0.0);
  }

  @Test
  public void relativeHumidityZeroIsRejectedWhilePositiveBoundaryIsAccepted() throws Exception {
    runRecord("20.0", "0.0", "10.0", "0.1", new Date());
    assertEquals(0.001, mytemp.double_rv, 0.0);

    resetModel();
    writeRecord("20.0", "0.0", "10.0", "0.0", new Date());
    runErrorWorkflow();
    assertEquals(padded("20.0"), mytemp.air_temp);
    assertEquals(padded("0.0"), mytemp.wet_bulb_temp);
    assertEquals("", mytemp.RH);
    assertEquals(10.0, mytemp.double_dew_point, 0.0);
    assertEquals(main.INVALID, mytemp.double_rv, 0.0);
  }

  @Test
  public void sensorMarkersAreNotParsedAsNumericValues() throws Exception {
    runRecord("*", "*", "*", "*", new Date());

    assertEquals(padded("*"), mytemp.jTextField1.getText());
    assertEquals(padded("*"), mytemp.jTextField2.getText());
    assertEquals(padded("*"), mytemp.jTextField4.getText());
    assertEquals(main.INVALID, mytemp.double_dew_point, 0.0);
    assertEquals(main.INVALID, mytemp.double_rv, 0.0);
  }

  @Test
  public void wetBulbTemperatureSelectsFrozenOrNotFrozenState() throws Exception {
    runRecord("20.0", "-0.1", "10.0", "50.0", new Date());
    assertTrue(mytemp.jRadioButton4.isSelected());
    assertFalse(mytemp.jRadioButton3.isSelected());

    resetModel();
    runRecord("20.0", "0.0", "10.0", "50.0", new Date());
    assertTrue(mytemp.jRadioButton3.isSelected());
    assertFalse(mytemp.jRadioButton4.isSelected());
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
    waitFor(
        () ->
            mytemp.air_temp.equals(padded(airTemp)) && main.obs_file_datum_tijd == null);
    flushEdt();
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
      Thread.setDefaultUncaughtExceptionHandler(
          (thread, error) -> {
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
        field(airTemp) + " " + field(wetBulbTemp) + " " + field(dewPoint) + " " + field(rh)
            + " "
            + main.sdf4.format(recordDate);
    writeRawRecord(record);
  }

  private void writeRawRecord(String record) throws Exception {
    Calendar fileDate = new GregorianCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    File file =
        new File(
            main.logs_dir,
            "sensor_data_II_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
  }

  private void resetModel() {
    mytemp.air_temp = "";
    mytemp.wet_bulb_temp = "";
    mytemp.RH = "";
    mytemp.double_dew_point = main.INVALID;
    mytemp.double_rv = main.INVALID;
    mytemp.jTextField1 = new JTextField();
    mytemp.jTextField2 = new JTextField();
    mytemp.jTextField4 = new JTextField();
    mytemp.jRadioButton3 = new JRadioButton();
    mytemp.jRadioButton4 = new JRadioButton();
    mytemp.jRadioButton3.setSelected(false);
    mytemp.jRadioButton4.setSelected(false);
  }

  private void assertCleared() {
    assertEquals("", mytemp.air_temp);
    assertEquals("", mytemp.wet_bulb_temp);
    assertEquals("", mytemp.RH);
    assertEquals(main.INVALID, mytemp.double_dew_point, 0.0);
    assertEquals(main.INVALID, mytemp.double_rv, 0.0);
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
          new Thread(
              () -> {
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
