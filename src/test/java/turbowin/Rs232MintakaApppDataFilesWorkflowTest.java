package turbowin;

import static org.junit.Assert.assertEquals;
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

public class Rs232MintakaApppDataFilesWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private String originalPpp;
  private String originalA;
  private GregorianCalendar originalObservationDate;
  private String originalAmount;
  private ObservableText originalTextField;
  private ObservableSelection<BarographModel.Tendency> originalTendency;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalPpp = main.sensor_data_record_obs_ppp;
    originalA = main.sensor_data_record_obs_a;
    originalObservationDate = main.obs_file_datum_tijd;
    originalAmount = BarographModel.pressure_amount_tendency;
    originalTextField = BarographModel.amountInput;
    originalTendency = BarographModel.tendency;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main.sensor_data_record_obs_ppp = "";
    main.sensor_data_record_obs_a = "";
    BarographModel.pressure_amount_tendency = "";
    BarographModel.amountInput = new ObservableText();
    BarographModel.tendency = new ObservableSelection<>(null);
    setButtons(false);
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.sensor_data_record_obs_ppp = originalPpp;
    main.sensor_data_record_obs_a = originalA;
    main.obs_file_datum_tijd = originalObservationDate;
    BarographModel.pressure_amount_tendency = originalAmount;
    BarographModel.amountInput = originalTextField;
    BarographModel.tendency = originalTendency;
  }

  @Test
  public void starAndStarXRecordsExtractTendencyAndCharacteristic() throws Exception {
    runRecord("1.24", "2", false);
    assertEquals("1.2", BarographModel.pressure_amount_tendency);
    assertEquals("1.2", BarographModel.amountInput.get());
    assertSelectedButton(3);

    runRecord("-1.26", "7", true);
    assertEquals("1.3", BarographModel.pressure_amount_tendency);
    assertSelectedButton(7);
  }

  @Test
  public void missingAndInvalidChecksumsResetTendencyAndPreserveOtherFormValues() throws Exception {
    seedExistingFormValues();
    Date recordDate = new Date();
    String payload = starPayload("1.0", "1.0", "2");
    runRejectedRecord(payload + main.sdf4.format(recordDate), false);
    assertRejectedFormValues();

    seedExistingFormValues();
    runRejectedRecord(payload + "*00" + main.sdf4.format(recordDate), true);
    assertRejectedFormValues();
  }

  @Test
  public void staleAndInvalidAgeRecordsResetTendencyAndPreserveOtherFormValues() throws Exception {
    seedExistingFormValues();
    Date staleDate = new Date(System.currentTimeMillis() - 20 * 60 * 1000L);
    runRejectedRecord(starRecord(starPayload("1.0", "1.0", "2"), staleDate), false);
    assertRejectedFormValues();

    seedExistingFormValues();
    runRejectedRecord(starRecord(starXPayload("1.0", "1.0", "2", "bad"), new Date()), true);
    assertRejectedFormValues();

    seedExistingFormValues();
    runRejectedRecord(starRecord(starXPayload("1.0", "1.0", "2", "-1"), new Date()), true);
    assertRejectedFormValues();
  }

  @Test
  public void tendencyBoundariesPreserveDirectionRules() throws Exception {
    runRecord("99.8", "2", false);
    assertEquals("99.8", BarographModel.pressure_amount_tendency);
    assertSelectedButton(3);

    runRecord("99.9", "2", false);
    assertEquals("99.9", BarographModel.pressure_amount_tendency);
    assertSelectedButton(12);

    runRecord("-99.8", "7", false);
    assertEquals("99.8", BarographModel.pressure_amount_tendency);
    assertSelectedButton(7);

    runRecord("-99.9", "7", false);
    assertEquals("99.9", BarographModel.pressure_amount_tendency);
    assertSelectedButton(12);
  }

  @Test
  public void characteristicCodesResetAndSelectTheExpectedPressureDirection() throws Exception {
    for (int code = 0; code <= 3; code++) {
      runRecord("1.0", Integer.toString(code), false);
      assertSelectedButton(code + 1);
    }
    for (int code = 5; code <= 8; code++) {
      runRecord("-1.0", Integer.toString(code), true);
      assertSelectedButton(code);
    }

    runRecord("0.0", "0", false);
    assertSelectedButton(9);
    runRecord("0.0", "4", false);
    assertSelectedButton(10);
    runRecord("0.0", "5", false);
    assertSelectedButton(11);
  }

  private void runRecord(String ppp, String a, boolean starX) throws Exception {
    main.obs_file_datum_tijd = new GregorianCalendar();
    writeCurrentRecord(
        starRecord(
            starX ? starXPayload("1000.0", ppp, a, "0") : starPayload("1000.0", ppp, a),
            new Date()));
    Rs232MintakaApppDataFilesWorkflow.start(starX);
    waitFor(() ->
        main.obs_file_datum_tijd == null
            && main.sensor_data_record_obs_ppp.equals(ppp)
            && main.sensor_data_record_obs_a.equals(a));
    flushEdt();
  }

  private void runRejectedRecord(String record, boolean starX) throws Exception {
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
      writeCurrentRecord(record);
      Rs232MintakaApppDataFilesWorkflow.start(starX);
      waitFor(() -> main.obs_file_datum_tijd == null);
      dialogCloser.await();
      flushEdt();
    } finally {
      Thread.setDefaultUncaughtExceptionHandler(originalHandler);
    }
  }

  private void seedExistingFormValues() {
    BarographModel.pressure_amount_tendency = "old";
    BarographModel.amountInput.set("old");
    setButtons(true);
  }

  private void assertRejectedFormValues() {
    assertEquals("", BarographModel.pressure_amount_tendency);
    assertEquals("old", BarographModel.amountInput.get());
    assertEquals(BarographModel.Tendency.UNDETERMINED, BarographModel.tendency.get());
  }

  private void assertSelectedButton(int expectedButton) {
    assertEquals(
        BarographModel.Tendency.values()[expectedButton - 1], BarographModel.tendency.get());
  }

  private void setButtons(boolean selected) {
    BarographModel.tendency.set(selected ? BarographModel.Tendency.UNDETERMINED : null);
  }

  private String starPayload(String pressure, String ppp, String a) {
    return pressure + ",1000.0," + ppp + "," + a + ", 52 00.0000N, 004 00.0000E,0,0,0";
  }

  private String starXPayload(String pressure, String ppp, String a, String age) {
    return starPayload(pressure, ppp, a) + ",20.0,50,19.0,10.0," + age;
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
    assertTrue("Timed out waiting for Mintaka a-ppp workflow", condition.getAsBoolean());
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
