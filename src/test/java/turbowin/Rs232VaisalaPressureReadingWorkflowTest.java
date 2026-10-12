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
import java.util.concurrent.atomic.AtomicReference;
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

public class Rs232VaisalaPressureReadingWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private int originalRecordLength;
  private int originalDatePosition;
  private int originalPressurePosition;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private SimpleDateFormat originalSdf8;
  private String originalPressureRecord;
  private String originalPressureReading;
  private String originalCorrection;
  private boolean originalTrayClick;
  private int originalConnectionMode;
  private GregorianCalendar originalObservationDate;
  private GregorianCalendar originalSystemDate;
  private JTextField originalTextField1;
  private JTextField originalTextField2;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalRecordLength = main.type_record_lengte;
    originalDatePosition = main.type_record_datum_tijd_begin_pos;
    originalPressurePosition = main.type_record_pressure_begin_pos;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalSdf8 = main_RS232_RS422.sdf8;
    originalPressureRecord = main.sensor_data_record_obs_pressure;
    originalPressureReading = mybarometer.pressure_reading;
    originalCorrection = main.barometer_instrument_correction;
    originalTrayClick = main.tray_icon_clicked;
    originalConnectionMode = main.RS232_connection_mode;
    originalObservationDate = main.obs_file_datum_tijd;
    originalSystemDate = main_RS232_RS422.cal_system_date_time;
    originalTextField1 = mybarometer.jTextField1;
    originalTextField2 = mybarometer.jTextField2;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.type_record_lengte = main.RECORD_LENGTE_PTB220;
    main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
    main.type_record_pressure_begin_pos = main.RECORD_P_BEGIN_POS_PTB220;
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main_RS232_RS422.sdf8 = utcFormat("dd MMM yyyy HH.mm");
    main.RS232_connection_mode = 1;
    main.barometer_instrument_correction = "";
    mybarometer.pressure_reading = "";
    mybarometer.jTextField1 = new JTextField();
    mybarometer.jTextField2 = new JTextField();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.type_record_lengte = originalRecordLength;
    main.type_record_datum_tijd_begin_pos = originalDatePosition;
    main.type_record_pressure_begin_pos = originalPressurePosition;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main_RS232_RS422.sdf8 = originalSdf8;
    main.sensor_data_record_obs_pressure = originalPressureRecord;
    mybarometer.pressure_reading = originalPressureReading;
    main.barometer_instrument_correction = originalCorrection;
    main.tray_icon_clicked = originalTrayClick;
    main.RS232_connection_mode = originalConnectionMode;
    main.obs_file_datum_tijd = originalObservationDate;
    main_RS232_RS422.cal_system_date_time = originalSystemDate;
    mybarometer.jTextField1 = originalTextField1;
    mybarometer.jTextField2 = originalTextField2;
  }

  @Test
  public void freshReadingIsRoundedAndWrittenToTheBarometerForm() throws Exception {
    runFormRecord("1012.34", new Date());

    assertEquals("1012.3", mybarometer.pressure_reading);
    assertEquals("1012.3", mybarometer.jTextField1.getText());
  }

  @Test
  public void pressureAcceptanceRangeIsStrictlyBetween900And1100() throws Exception {
    runFormRecord("900.0", new Date());
    assertEquals("", mybarometer.pressure_reading);

    runFormRecord("900.1", new Date());
    assertEquals("900.1", mybarometer.pressure_reading);

    runFormRecord("1099.9", new Date());
    assertEquals("1099.9", mybarometer.pressure_reading);

    mybarometer.pressure_reading = "";
    mybarometer.jTextField1.setText("");
    runFormRecord("1100.0", new Date());
    assertEquals("", mybarometer.pressure_reading);
  }

  @Test
  public void malformedReadingDoesNotReplaceTheExistingFormValue() throws Exception {
    mybarometer.pressure_reading = "old";
    mybarometer.jTextField1.setText("old");

    runFormRecord("bad", new Date());

    assertEquals("old", mybarometer.pressure_reading);
    assertEquals("old", mybarometer.jTextField1.getText());
  }

  @Test
  public void staleReadingIsClearedBeforeTheNoDataPath() throws Exception {
    main.sensor_data_record_obs_pressure = "pending";
    writeRecord("1000.0", new Date(System.currentTimeMillis() - 10 * 60 * 1000L));
    runTrayWorkflow("");

    assertEquals("", main.sensor_data_record_obs_pressure);
  }

  @Test
  public void missingRecordUsesTheNoDataPath() throws Exception {
    main.sensor_data_record_obs_pressure = "pending";
    runTrayWithoutRecord();

    assertEquals("", main.sensor_data_record_obs_pressure);
  }

  @Test
  public void trayReadingAppliesOnlyCorrectionsStrictlyInsideTheFourHpaLimits() throws Exception {
    assertTrayPressure("1.5", "1001.5");
    assertTrayPressure("-1.5", "998.5");
    assertTrayPressure("4.0", "1000.0");
    assertTrayPressure("-4.0", "1000.0");
  }

  private void assertTrayPressure(String correction, String expectedPressure) throws Exception {
    main.barometer_instrument_correction = correction;
    String dialogMessage = runTrayRecord("1000.0", new Date());
    if (!GraphicsEnvironment.isHeadless()) {
      assertTrue(dialogMessage.contains("pressure at sensor height: " + expectedPressure + " hPa"));
    }
  }

  private void runFormRecord(String pressure, Date recordDate) throws Exception {
    main.sensor_data_record_obs_pressure = "pending";
    writeRecord(pressure, recordDate);
    main.obs_file_datum_tijd = new GregorianCalendar();
    main.tray_icon_clicked = false;
    Rs232VaisalaPressureReadingWorkflow.start(false);
    waitFor(
        () ->
            main.sensor_data_record_obs_pressure.equals(padded(pressure, 7))
                && main.obs_file_datum_tijd == null);
    flushEdt();
  }

  private String runTrayRecord(String pressure, Date recordDate) throws Exception {
    main.sensor_data_record_obs_pressure = "pending";
    writeRecord(pressure, recordDate);
    return runTrayWorkflow(padded(pressure, 7));
  }

  private void runTrayWithoutRecord() throws Exception {
    runTrayWorkflow("");
  }

  private String runTrayWorkflow(String expectedRecord) throws Exception {
    DialogCloser dialogCloser = new DialogCloser();
    dialogCloser.start();
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
      main.obs_file_datum_tijd = new GregorianCalendar();
      main.tray_icon_clicked = true;
      Rs232VaisalaPressureReadingWorkflow.start(true);
      waitFor(
          () ->
              main.sensor_data_record_obs_pressure.equals(expectedRecord)
                  && main.obs_file_datum_tijd == null);
      dialogCloser.await();
      flushEdt();
      return dialogCloser.message();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void writeRecord(String pressure, Date recordDate) throws Exception {
    char[] record = "                                    ".toCharArray();
    String pressureValue = padded(pressure, 7);
    for (int index = 0; index < pressureValue.length(); index++) {
      record[main.RECORD_P_BEGIN_POS_PTB220 + index] = pressureValue.charAt(index);
    }
    String timestamp = main.sdf4.format(recordDate);
    for (int index = 0; index < timestamp.length(); index++) {
      record[main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220 + index] = timestamp.charAt(index);
    }

    Calendar fileDate = new GregorianCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    File file =
        new File(
            main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    Files.writeString(file.toPath(), new String(record) + System.lineSeparator());
  }

  private static String padded(String value, int width) {
    return String.format("%-" + width + "s", value);
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
    assertTrue("Timed out waiting for pressure reading workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    Thread.sleep(50);
    SwingUtilities.invokeAndWait(() -> {});
  }

  private static final class DialogCloser {
    private final AtomicReference<String> message = new AtomicReference<>();
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
                      message.set(findMessage(window));
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
      return message.get();
    }

    private static String findMessage(Window window) {
      JOptionPane pane = findPane(window);
      Object value = pane == null ? null : pane.getMessage();
      return value == null ? "" : value.toString();
    }

    private static JOptionPane findPane(Component component) {
      if (component instanceof JOptionPane) {
        return (JOptionPane) component;
      }
      if (component instanceof Container) {
        for (Component child : ((Container) component).getComponents()) {
          JOptionPane pane = findPane(child);
          if (pane != null) {
            return pane;
          }
        }
      }
      return null;
    }
  }
}
