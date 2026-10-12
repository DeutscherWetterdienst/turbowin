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
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
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
  private JTextField originalAirField;
  private JTextField originalWetBulbField;
  private JTextField originalRhField;
  private JRadioButton originalFrozenButton;
  private JRadioButton originalNotFrozenButton;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObservationDate = main.obs_file_datum_tijd;
    originalAirTemp = mytemp.air_temp;
    originalWetBulbTemp = mytemp.wet_bulb_temp;
    originalRh = mytemp.RH;
    originalDewPoint = mytemp.double_dew_point;
    originalAirField = mytemp.jTextField1;
    originalWetBulbField = mytemp.jTextField2;
    originalRhField = mytemp.jTextField4;
    originalFrozenButton = mytemp.jRadioButton4;
    originalNotFrozenButton = mytemp.jRadioButton3;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    mytemp.jTextField1 = new JTextField();
    mytemp.jTextField2 = new JTextField();
    mytemp.jTextField4 = new JTextField();
    mytemp.jRadioButton3 = new JRadioButton();
    mytemp.jRadioButton4 = new JRadioButton();
    ButtonGroup wetBulbGroup = new ButtonGroup();
    wetBulbGroup.add(mytemp.jRadioButton3);
    wetBulbGroup.add(mytemp.jRadioButton4);
    resetModel();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.obs_file_datum_tijd = originalObservationDate;
    mytemp.air_temp = originalAirTemp;
    mytemp.wet_bulb_temp = originalWetBulbTemp;
    mytemp.RH = originalRh;
    mytemp.double_dew_point = originalDewPoint;
    mytemp.jTextField1 = originalAirField;
    mytemp.jTextField2 = originalWetBulbField;
    mytemp.jTextField4 = originalRhField;
    mytemp.jRadioButton4 = originalFrozenButton;
    mytemp.jRadioButton3 = originalNotFrozenButton;
  }

  @Test
  public void validRecordParsesDewPointAndUpdatesWetBulbUiState() throws Exception {
    runRecord("20.0", "75.0", "-0.4", "12.3", "0");

    assertEquals("20.0", mytemp.air_temp);
    assertEquals("-0.4", mytemp.wet_bulb_temp);
    assertEquals("75.0", mytemp.RH);
    assertEquals(12.3, mytemp.double_dew_point, 0.0);
    assertEquals("20.0", mytemp.jTextField1.getText());
    assertEquals("-0.4", mytemp.jTextField2.getText());
    assertEquals("75.0", mytemp.jTextField4.getText());
    assertTrue(mytemp.jRadioButton4.isSelected());
    assertFalse(mytemp.jRadioButton3.isSelected());

    runRecord("20.0", "75.0", "0.4", "12.3", "0");
    assertTrue(mytemp.jRadioButton3.isSelected());
    assertFalse(mytemp.jRadioButton4.isSelected());
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
    runRecord("20.0", "75.0", "0.4", "12.3", Integer.toString(main_RS232_RS422.MAX_AGE_STARX_OBS_DATA + 1));
    assertCleared();

    resetModel();
    seedExistingValues();
    runRecord("20.0", "75.0", "0.4", "12.3", "bad");
    assertCleared();
  }

  @Test
  public void legacyTemperatureHumidityBoundariesAndMissingDewPointArePreserved() throws Exception {
    runRecord("99.9", "100.0", "99.9", "-70.0", "0");
    assertEquals("99.9", mytemp.air_temp);
    assertEquals("99.9", mytemp.wet_bulb_temp);
    assertEquals("100.0", mytemp.RH);
    assertEquals(-70.0, mytemp.double_dew_point, 0.0);

    resetModel();
    runRecord("-99.9", "0.0", "-99.9", "bad", "0");
    assertEquals("", mytemp.air_temp);
    assertEquals("", mytemp.wet_bulb_temp);
    assertEquals("", mytemp.RH);
    assertEquals(main.INVALID, mytemp.double_dew_point, 0.0);

    resetModel();
    runRecord("", "", "", "", "0");
    assertCleared();
  }

  private void runRecord(
      String airTemp, String rh, String wetBulbTemp, String dewPoint, String age) throws Exception {
    writeRawRecord(starRecord(starXPayload(airTemp, rh, wetBulbTemp, dewPoint, age), recentDate()));
    runWorkflow();
  }

  private void runWorkflowWithoutRecord() throws Exception {
    runWorkflow();
  }

  private void runWorkflow() throws Exception {
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
      Rs232MintakaStarXObservationWorkflow.start();
      waitFor(() -> main.obs_file_datum_tijd == null);
      dialogCloser.await();
      flushEdt();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void seedExistingValues() {
    mytemp.air_temp = "old";
    mytemp.wet_bulb_temp = "old";
    mytemp.RH = "old";
    mytemp.double_dew_point = 10.0;
  }

  private void resetModel() {
    mytemp.air_temp = "";
    mytemp.wet_bulb_temp = "";
    mytemp.RH = "";
    mytemp.double_dew_point = main.INVALID;
    mytemp.jTextField1.setText("");
    mytemp.jTextField2.setText("");
    mytemp.jTextField4.setText("");
    mytemp.jRadioButton3.setSelected(false);
    mytemp.jRadioButton4.setSelected(false);
  }

  private void assertCleared() {
    assertEquals("", mytemp.air_temp);
    assertEquals("", mytemp.wet_bulb_temp);
    assertEquals("", mytemp.RH);
    assertEquals(main.INVALID, mytemp.double_dew_point, 0.0);
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
    assertTrue("Timed out waiting for Mintaka StarX observation workflow", condition.getAsBoolean());
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
