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

public class Rs232MintakaDuoWowAprWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private GregorianCalendar originalObservationDate;
  private int originalConnectionMode;
  private int originalSecondConnectionMode;
  private String originalPressureRecord;
  private String originalPressureCorrection;
  private String originalBarometerHeight;
  private String originalKeelHeight;
  private String originalDraught;
  private boolean originalApr;
  private boolean originalObsoleteData;
  private boolean originalObsoleteDataII;
  private String originalSendMethod;
  private String originalPressureCorrected;
  private String originalPressureMslCorrected;
  private String originalPpppCode;
  private String originalAirTemp;
  private String originalWetBulb;
  private String originalRh;
  private double originalDewPoint;
  private double originalRelativeHumidity;
  private final JTextField[] originalMainFields = new JTextField[7];

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObservationDate = main.obs_file_datum_tijd;
    originalConnectionMode = main.RS232_connection_mode;
    originalSecondConnectionMode = main.RS232_connection_mode_II;
    originalPressureRecord = main.sensor_data_record_obs_pressure;
    originalPressureCorrection = main.barometer_instrument_correction;
    originalBarometerHeight = main.barometer_above_sll;
    originalKeelHeight = main.keel_sll;
    originalDraught = main.WOW_APR_average_draught;
    originalApr = main.APR;
    originalObsoleteData = main.obsolate_data_flag;
    originalObsoleteDataII = main.obsolate_data_flag_II;
    originalSendMethod = main.APTR_AWSR_send_method;
    originalPressureCorrected = mybarometer.pressure_reading_corrected;
    originalPressureMslCorrected = mybarometer.pressure_msl_corrected;
    originalPpppCode = mybarometer.PPPP_code;
    originalAirTemp = mytemp.air_temp;
    originalWetBulb = mytemp.wet_bulb_temp;
    originalRh = mytemp.RH;
    originalDewPoint = mytemp.double_dew_point;
    originalRelativeHumidity = mytemp.double_rv;
    originalMainFields[0] = main.jTextField9;
    originalMainFields[1] = main.jTextField10;
    originalMainFields[2] = main.jTextField11;
    originalMainFields[3] = main.jTextField12;
    originalMainFields[4] = main.jTextField36;
    originalMainFields[5] = main.jTextField37;
    originalMainFields[6] = main.jTextField38;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main.RS232_connection_mode = 0;
    main.RS232_connection_mode_II = 0;
    main.barometer_instrument_correction = "1.5";
    main.barometer_above_sll = "0.0";
    main.keel_sll = "0.0";
    main.WOW_APR_average_draught = "0.0";
    main.APR = true;
    main.obsolate_data_flag = false;
    main.obsolate_data_flag_II = false;
    main.APTR_AWSR_send_method = "unsupported-test-method";
    setMainFieldReferences();
    resetModel();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.obs_file_datum_tijd = originalObservationDate;
    main.RS232_connection_mode = originalConnectionMode;
    main.RS232_connection_mode_II = originalSecondConnectionMode;
    main.sensor_data_record_obs_pressure = originalPressureRecord;
    main.barometer_instrument_correction = originalPressureCorrection;
    main.barometer_above_sll = originalBarometerHeight;
    main.keel_sll = originalKeelHeight;
    main.WOW_APR_average_draught = originalDraught;
    main.APR = originalApr;
    main.obsolate_data_flag = originalObsoleteData;
    main.obsolate_data_flag_II = originalObsoleteDataII;
    main.APTR_AWSR_send_method = originalSendMethod;
    mybarometer.pressure_reading_corrected = originalPressureCorrected;
    mybarometer.pressure_msl_corrected = originalPressureMslCorrected;
    mybarometer.PPPP_code = originalPpppCode;
    mytemp.air_temp = originalAirTemp;
    mytemp.wet_bulb_temp = originalWetBulb;
    mytemp.RH = originalRh;
    mytemp.double_dew_point = originalDewPoint;
    mytemp.double_rv = originalRelativeHumidity;
    main.jTextField9 = originalMainFields[0];
    main.jTextField10 = originalMainFields[1];
    main.jTextField11 = originalMainFields[2];
    main.jTextField12 = originalMainFields[3];
    main.jTextField36 = originalMainFields[4];
    main.jTextField37 = originalMainFields[5];
    main.jTextField38 = originalMainFields[6];
  }

  @Test
  public void validRecordAppliesPressureCorrectionAndPreparesAprData() throws Exception {
    runPressureRecord("1000.0", "APR", new Date());

    assertEquals("1001.5", mybarometer.pressure_reading_corrected);
    assertEquals("1001.5", mybarometer.pressure_msl_corrected);
    assertEquals("0015", mybarometer.PPPP_code);
  }

  @Test
  public void malformedStaleAndMissingRecordsProduceNoAprPressure() throws Exception {
    runRawRecord("too-short", "APR");
    assertCleared();

    resetModel();
    runPressureRecord(
        "1000.0", "APR", new Date(System.currentTimeMillis() - 20 * 60 * 1000L));
    assertCleared();

    resetModel();
    runWorkflow("APR");
    assertCleared();
  }

  @Test
  public void pressureLimitsAndInstrumentCorrectionBoundariesAreCharacterized() throws Exception {
    main.barometer_instrument_correction = "0.0";
    runPressureRecord("900.0", "APR", new Date());
    assertEquals("", mybarometer.pressure_reading_corrected);

    runPressureRecord("900.1", "APR", new Date());
    assertEquals("900.1", mybarometer.pressure_reading_corrected);

    runPressureRecord("1099.9", "APR", new Date());
    assertEquals("1099.9", mybarometer.pressure_reading_corrected);

    runPressureRecord("1100.0", "APR", new Date());
    assertEquals("", mybarometer.pressure_reading_corrected);

    main.barometer_instrument_correction = "4.0";
    runPressureRecord("1000.0", "APR", new Date());
    assertEquals("1004.0", mybarometer.pressure_reading_corrected);

    main.barometer_instrument_correction = "-4.0";
    runPressureRecord("1000.0", "APR", new Date());
    assertEquals("996.0", mybarometer.pressure_reading_corrected);

    main.barometer_instrument_correction = "4.1";
    runPressureRecord("1000.0", "APR", new Date());
    assertEquals("1000.0", mybarometer.pressure_reading_corrected);
  }

  @Test
  public void mainScreenUpdatesFieldsWithoutAprPreparationOrSend() throws Exception {
    runPressureRecord("1000.0", "MAIN_SCREEN", new Date());

    assertEquals("1001.5 hPa", main.jTextField9.getText());
    assertEquals("1001.5 hPa", main.jTextField10.getText());
    assertEquals("", mybarometer.PPPP_code);
  }

  @Test
  public void wowDestinationUsesTheDisabledWowBranch() throws Exception {
    String dialogMessage = runPressureRecord("1000.0", "WOW", new Date());

    if (!GraphicsEnvironment.isHeadless()) {
      assertEquals("WOW in TurboWin+ is disabled", dialogMessage);
    }
    assertEquals("", mybarometer.pressure_reading_corrected);
  }

  @Test
  public void secondInstrumentIsReadWhenConfigured() throws Exception {
    main.RS232_connection_mode_II = 1;
    writeHmpRecord("20.0", "-0.1", "10.0", "50.0", new Date());

    runPressureRecord("1000.0", "APR", new Date());

    assertEquals("20.0", mytemp.air_temp);
    assertEquals("-0.1", mytemp.wet_bulb_temp);
    assertEquals(10.0, mytemp.double_dew_point, 0.001);
    assertEquals(0.5, mytemp.double_rv, 0.001);
  }

  private String runPressureRecord(String pressure, String destination, Date recordDate)
      throws Exception {
    resetModel();
    writePressureRecord(pressure, recordDate);
    return runWorkflow(destination);
  }

  private String runRawRecord(String record, String destination) throws Exception {
    writeRawRecord(record);
    return runWorkflow(destination);
  }

  private String runWorkflow(String destination) throws Exception {
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
      Rs232MintakaDuoWowAprWorkflow.start(destination, false);
      waitFor(
          () ->
              main.obs_file_datum_tijd == null
                  && (main.RS232_connection_mode_II == 0 || mytemp.air_temp.length() > 0));
      dialogCloser.await();
      flushEdt();
      return dialogCloser.message();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void writePressureRecord(String pressure, Date recordDate) throws Exception {
    writeRawRecord(duoRecord(pressure, recordDate));
  }

  private void writeRawRecord(String record) throws Exception {
    Date fileDate = new Date(System.currentTimeMillis() - 2 * 60 * 1000L);
    File file =
        new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
  }

  private void writeHmpRecord(
      String airTemp, String wetBulb, String dewPoint, String rh, Date recordDate)
      throws Exception {
    String record =
        padded(airTemp)
            + " "
            + padded(wetBulb)
            + " "
            + padded(dewPoint)
            + " "
            + padded(rh)
            + " "
            + main.sdf4.format(recordDate);
    assertEquals(main.RECORD_LENGTE_HMP155, record.length());

    Date fileDate = new Date(System.currentTimeMillis() - 2 * 60 * 1000L);
    File file =
        new File(main.logs_dir, "sensor_data_II_" + main.sdf3.format(fileDate) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
  }

  private void resetModel() {
    main.sensor_data_record_obs_pressure = "";
    mybarometer.pressure_reading_corrected = "";
    mybarometer.pressure_msl_corrected = "";
    mybarometer.PPPP_code = "";
    mytemp.air_temp = "";
    mytemp.wet_bulb_temp = "";
    mytemp.RH = "";
    mytemp.double_dew_point = main.INVALID;
    mytemp.double_rv = main.INVALID;
    main.jTextField9.setText("");
    main.jTextField10.setText("");
    main.jTextField11.setText("");
    main.jTextField12.setText("");
    main.jTextField36.setText("");
    main.jTextField37.setText("");
    main.jTextField38.setText("");
  }

  private void assertCleared() {
    assertEquals("", mybarometer.pressure_reading_corrected);
    assertEquals("", mybarometer.pressure_msl_corrected);
    assertEquals("", mybarometer.PPPP_code);
  }

  private void setMainFieldReferences() {
    main.jTextField9 = new JTextField();
    main.jTextField10 = new JTextField();
    main.jTextField11 = new JTextField();
    main.jTextField12 = new JTextField();
    main.jTextField36 = new JTextField();
    main.jTextField37 = new JTextField();
    main.jTextField38 = new JTextField();
  }

  private String duoRecord(String pressure, Date recordDate) {
    return pressure + ",1000.0,1.0,1*" + main.sdf4.format(recordDate);
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
    assertTrue("Timed out waiting for Mintaka Duo WOW/APR workflow", condition.getAsBoolean());
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
                      String dialogMessage = dialogMessage(window);
                      message = dialogMessage == null ? "dialog" : dialogMessage;
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
