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
import java.util.Calendar;
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

public class Rs232MintakaPpppWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private SimpleDateFormat originalSdf8;
  private GregorianCalendar originalObservationDate;
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
    main.tray_icon_clicked = originalTrayIconClicked;
    main.sensor_data_record_obs_pressure = originalPressureRecord;
    mybarometer.pressure_reading = originalPressureReading;
    main.barometer_instrument_correction = originalCorrection;
    mybarometer.jTextField1 = originalPressureField;
    mybarometer.jTextField2 = originalDraftField;
  }

  @Test
  public void starAndStarXRecordsExtractPressureForTheBarometerForm() throws Exception {
    runFormRecord("1000.04", false, "1000.0");
    assertEquals("1000.0", mybarometer.jTextField1.getText());

    runFormRecord("1001.26", true, "1001.3");
    assertEquals("1001.3", mybarometer.jTextField1.getText());
  }

  @Test
  public void checksumAndCommaCountFailuresUseTheErrorDestination() throws Exception {
    String payload = starPayload("1000.0");
    Date recordDate = new Date();
    writeCurrentRecord(payload + "*00" + main.sdf4.format(recordDate));
    assertErrorDialogAndClearedPressure(false);

    resetModel();
    writeCurrentRecord(starRecord(starXPayload("1000.0", 0), recordDate));
    assertErrorDialogAndClearedPressure(true);
  }

  @Test
  public void starXObservationAgeLimitsAreApplied() throws Exception {
    runStarXAge(main_RS232_RS422.MAX_AGE_STARX_OBS_DATA, "1000.0");

    resetModel();
    runStarXAge(main_RS232_RS422.MAX_AGE_STARX_OBS_DATA + 1, "");
  }

  @Test
  public void pressureBoundariesAndTrayCorrectionAreCharacterized() throws Exception {
    runFormRecord("900.0", false, "");
    runFormRecord("900.1", false, "900.1");
    runFormRecord("1099.9", false, "1099.9");
    runFormRecord("1100.0", false, "1100.0");

    main.barometer_instrument_correction = "1.5";
    String dialogMessage = runTrayRecord("1000.0", false);
    if (!GraphicsEnvironment.isHeadless()) {
      assertTrue(dialogMessage.contains("pressure at sensor height: 1001.5 hPa"));
    }

    main.barometer_instrument_correction = "4.0";
    dialogMessage = runTrayRecord("1000.0", false);
    if (!GraphicsEnvironment.isHeadless()) {
      assertTrue(dialogMessage.contains("pressure at sensor height: 1000.0 hPa"));
    }
  }

  private void runFormRecord(String pressure, boolean starX, String expected) throws Exception {
    writeCurrentRecord(starRecord(starX ? starXPayload(pressure, 0) : starPayload(pressure), new Date()));
    if (expected.isEmpty()) {
      runWorkflowWithDialog(false, starX);
    } else {
      mybarometer.pressure_reading = "pending";
      Rs232MintakaPpppWorkflow.start(false, starX);
      waitFor(() -> main.obs_file_datum_tijd == null);
      flushEdt();
    }
    assertEquals(expected, mybarometer.pressure_reading);
  }

  private void runStarXAge(int age, String expected) throws Exception {
    writeCurrentRecord(starRecord(starXPayload("1000.0", age), new Date()));
    if (expected.isEmpty()) {
      String dialogMessage = runWorkflowWithDialog(true, true);
      if (!GraphicsEnvironment.isHeadless()) {
        assertTrue(dialogMessage.contains("obsolete"));
      }
    } else {
      mybarometer.pressure_reading = "pending";
      Rs232MintakaPpppWorkflow.start(false, true);
      waitFor(() -> main.obs_file_datum_tijd == null);
      flushEdt();
    }
    assertEquals(expected, mybarometer.pressure_reading);
  }

  private String runTrayRecord(String pressure, boolean starX) throws Exception {
    writeCurrentRecord(
        starRecord(starX ? starXPayload(pressure, 0) : starPayload(pressure), new Date()));
    mybarometer.pressure_reading = "pending";
    return runWorkflowWithDialog(true, starX);
  }

  private String runWorkflowWithDialog(boolean tray, boolean starX) throws Exception {
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
      Rs232MintakaPpppWorkflow.start(tray, starX);
      waitFor(() -> main.obs_file_datum_tijd == null);
      dialogCloser.await();
      flushEdt();
      return dialogCloser.message();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void assertErrorDialogAndClearedPressure(boolean starX) throws Exception {
    mybarometer.pressure_reading = "old";
    String dialogMessage = runWorkflowWithDialog(true, starX);
    assertEquals("", mybarometer.pressure_reading);
    if (!GraphicsEnvironment.isHeadless()) {
      assertTrue(dialogMessage.length() > 0);
    }
  }

  private void resetModel() {
    main.sensor_data_record_obs_pressure = "";
    mybarometer.pressure_reading = "";
    mybarometer.jTextField1.setText("");
    mybarometer.jTextField2.setText("");
  }

  private String starPayload(String pressure) {
    return pressure + ",1000.0,1.0,2, 52 00.0000N, 004 00.0000E,0,0,0";
  }

  private String starXPayload(String pressure, int age) {
    return pressure
        + ",1000.0,1.0,2, 52 00.0000N, 004 00.0000E,0,0,0,20.0,50,19.0,10.0,"
        + age;
  }

  private String starRecord(String payload, Date recordDate) {
    String checksum = main_RS232_RS422.Mintaka_Star_Checksum(payload + "*");
    return payload + "*" + checksum + main.sdf4.format(recordDate);
  }

  private void writeCurrentRecord(String record) throws Exception {
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
    assertTrue("Timed out waiting for Mintaka PPPP workflow", condition.getAsBoolean());
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
