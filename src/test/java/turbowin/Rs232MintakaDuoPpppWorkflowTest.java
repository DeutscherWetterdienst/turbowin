package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232MintakaDuoPpppWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private SimpleDateFormat originalSdf8;
  private GregorianCalendar originalObservationDate;
  private GregorianCalendar originalSystemDate;
  private boolean originalTrayIconClicked;
  private String originalPressureRecord;
  private String originalPressureReading;
  private String originalCorrection;
  private JTextField originalPressureField;
  private JTextField originalDraftField;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalSdf8 = main_RS232_RS422.sdf8;
    originalObservationDate = main.obs_file_datum_tijd;
    originalSystemDate = main_RS232_RS422.cal_system_date_time;
    originalTrayIconClicked = main.tray_icon_clicked;
    originalPressureRecord = main.sensor_data_record_obs_pressure;
    originalPressureReading = mybarometer.pressure_reading;
    originalCorrection = main.barometer_instrument_correction;
    originalPressureField = mybarometer.jTextField1;
    originalDraftField = mybarometer.jTextField2;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main_RS232_RS422.sdf8 = utcFormat("dd MMM yyyy HH.mm");
    main.tray_icon_clicked = false;
    main.sensor_data_record_obs_pressure = "";
    main.barometer_instrument_correction = "";
    mybarometer.pressure_reading = "";
    mybarometer.jTextField1 = new JTextField();
    mybarometer.jTextField2 = new JTextField();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main_RS232_RS422.sdf8 = originalSdf8;
    main.obs_file_datum_tijd = originalObservationDate;
    main_RS232_RS422.cal_system_date_time = originalSystemDate;
    main.tray_icon_clicked = originalTrayIconClicked;
    main.sensor_data_record_obs_pressure = originalPressureRecord;
    mybarometer.pressure_reading = originalPressureReading;
    main.barometer_instrument_correction = originalCorrection;
    mybarometer.jTextField1 = originalPressureField;
    mybarometer.jTextField2 = originalDraftField;
  }

  @Test
  public void freshDuoRecordExtractsPressureForTheBarometerForm() throws Exception {
    runFormRecord("1000.04", new Date());

    assertEquals("1000.04", main.sensor_data_record_obs_pressure);
    assertEquals("1000.0", mybarometer.pressure_reading);
    assertEquals("1000.0", mybarometer.jTextField1.getText());
  }

  @Test
  public void staleAndMalformedRecordsRemainUnavailable() throws Exception {
    String staleMessage =
        runTrayRecord("1000.0", new Date(System.currentTimeMillis() - 10 * 60 * 1000L));
    assertEquals("", main.sensor_data_record_obs_pressure);
    assertNoDataMessage(staleMessage);

    String wrongCommaRecord =
        "1000.0,1000.0,1.0,1,extra*" + main.sdf4.format(new Date());
    String malformedMessage = runTrayRawRecord(wrongCommaRecord);
    assertEquals("", main.sensor_data_record_obs_pressure);
    assertNoDataMessage(malformedMessage);

    String shortMessage = runTrayRawRecord("too-short");
    assertEquals("", main.sensor_data_record_obs_pressure);
    assertNoDataMessage(shortMessage);
  }

  @Test
  public void pressureLimitsAreStrictAndTrayDoesNotPopulateTheForm() throws Exception {
    runFormRecord("900.0", new Date());
    assertEquals("", mybarometer.pressure_reading);

    runFormRecord("900.1", new Date());
    assertEquals("900.1", mybarometer.pressure_reading);

    runFormRecord("1099.9", new Date());
    assertEquals("1099.9", mybarometer.pressure_reading);

    runFormRecord("1100.0", new Date());
    assertEquals("", mybarometer.pressure_reading);

    String trayMessage = runTrayRecord("1000.0", new Date());
    assertEquals("", mybarometer.pressure_reading);
    assertEquals("", mybarometer.jTextField1.getText());
    if (!GraphicsEnvironment.isHeadless()) {
      assertTrue(trayMessage.contains("pressure at sensor height: 1000.0 hPa"));
    }
  }

  @Test
  public void trayCorrectionUsesOnlyValuesStrictlyInsideFourHpaLimits() throws Exception {
    assertTrayPressure("1.5", "1001.5");
    assertTrayPressure("-1.5", "998.5");
    assertTrayPressure("4.0", "1000.0");
    assertTrayPressure("-4.0", "1000.0");
    assertTrayPressure("4.1", "1000.0");
    assertTrayPressure("-4.1", "1000.0");
  }

  @Test
  public void missingRecordUsesTheTrayNoDataError() throws Exception {
    String dialogMessage = runTrayWithoutRecord();

    assertEquals("", main.sensor_data_record_obs_pressure);
    assertNoDataMessage(dialogMessage);
  }

  private void assertTrayPressure(String correction, String expectedPressure) throws Exception {
    main.barometer_instrument_correction = correction;
    String dialogMessage = runTrayRecord("1000.0", new Date());
    if (!GraphicsEnvironment.isHeadless()) {
      assertTrue(
          dialogMessage.contains("pressure at sensor height: " + expectedPressure + " hPa"));
    }
  }

  private void runFormRecord(String pressure, Date recordDate) throws Exception {
    resetModel();
    writeRecord(duoRecord(pressure, recordDate));
    runWorkflow(false);
  }

  private String runTrayRecord(String pressure, Date recordDate) throws Exception {
    resetModel();
    writeRecord(duoRecord(pressure, recordDate));
    return runTrayWorkflow();
  }

  private String runTrayRawRecord(String record) throws Exception {
    resetModel();
    writeRawRecord(record);
    return runTrayWorkflow();
  }

  private String runTrayWithoutRecord() throws Exception {
    resetModel();
    return runTrayWorkflow();
  }

  private String runTrayWorkflow() throws Exception {
    DialogCloser dialogCloser = new DialogCloser();
    dialogCloser.start();
    Thread.UncaughtExceptionHandler originalHandler =
        Thread.getDefaultUncaughtExceptionHandler();
    if (GraphicsEnvironment.isHeadless()) {
      Thread.setDefaultUncaughtExceptionHandler(
          (thread, error) -> {
            if (!(error instanceof HeadlessException) && originalHandler != null) {
              originalHandler.uncaughtException(thread, error);
            }
          });
    }
    try {
      main.obs_file_datum_tijd = new GregorianCalendar();
      Rs232MintakaDuoPpppWorkflow.start(true);
      waitFor(() -> main.obs_file_datum_tijd == null);
      dialogCloser.await();
      flushEdt();
      return dialogCloser.message();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void runWorkflow(boolean tray) throws Exception {
    main.obs_file_datum_tijd = new GregorianCalendar();
    Rs232MintakaDuoPpppWorkflow.start(tray);
    waitFor(() -> main.obs_file_datum_tijd == null);
    flushEdt();
  }

  private void resetModel() {
    main.sensor_data_record_obs_pressure = "";
    mybarometer.pressure_reading = "";
    mybarometer.jTextField1.setText("");
    mybarometer.jTextField2.setText("");
  }

  private void assertNoDataMessage(String dialogMessage) {
    if (!GraphicsEnvironment.isHeadless()) {
      assertEquals("no sensor data available", dialogMessage);
    }
  }

  private String duoRecord(String pressure, Date recordDate) {
    return pressure + ",1000.0,1.0,1*" + main.sdf4.format(recordDate);
  }

  private void writeRecord(String record) throws Exception {
    writeRawRecord(record);
  }

  private void writeRawRecord(String record) throws Exception {
    Date fileDate = new Date(System.currentTimeMillis() - 2 * 60 * 1000L);
    File file =
        new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate) + ".txt");
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
    assertTrue("Timed out waiting for Mintaka Duo PPPP workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      SwingUtilities.invokeAndWait(() -> {});
    }
  }

  private static final class DialogCloser {
    private Thread thread;
    private volatile String message = "";

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
                      if (((JDialog) window).getContentPane().getComponentCount() > 0) {
                        String dialogMessage = dialogMessage(window);
                        message = dialogMessage == null ? "dialog" : dialogMessage;
                      }
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

    String message() {
      return message;
    }

    private static String dialogMessage(Component component) {
      if (component instanceof JOptionPane) {
        Object message = ((JOptionPane) component).getMessage();
        return message == null ? "" : message.toString();
      }
      if (component instanceof Container) {
        for (Component child : ((Container) component).getComponents()) {
          String message = dialogMessage(child);
          if (message != null) {
            return message;
          }
        }
      }
      return null;
    }
  }
}
