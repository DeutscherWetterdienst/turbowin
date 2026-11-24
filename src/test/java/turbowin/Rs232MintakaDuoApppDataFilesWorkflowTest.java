package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232MintakaDuoApppDataFilesWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private GregorianCalendar originalObservationDate;
  private String originalPpp;
  private String originalA;
  private String originalAmount;
  private String originalACode;
  private JTextField originalTextField;
  private ObservableSelection<BarographModel.Tendency> originalTendency;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObservationDate = main.obs_file_datum_tijd;
    originalPpp = main.sensor_data_record_obs_ppp;
    originalA = main.sensor_data_record_obs_a;
    originalAmount = BarographModel.pressure_amount_tendency;
    originalACode = BarographModel.a_code;
    originalTextField = BarographModel.jTextField1;
    originalTendency = BarographModel.tendency;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    BarographModel.jTextField1 = new JTextField();
    BarographModel.tendency = new ObservableSelection<>(null);
    resetModel();
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.obs_file_datum_tijd = originalObservationDate;
    main.sensor_data_record_obs_ppp = originalPpp;
    main.sensor_data_record_obs_a = originalA;
    BarographModel.pressure_amount_tendency = originalAmount;
    BarographModel.a_code = originalACode;
    BarographModel.jTextField1 = originalTextField;
    BarographModel.tendency = originalTendency;
  }

  @Test
  public void exactDuoRecordParsesTendencyAndCharacteristic() throws Exception {
    runRecord("1.24", "2", new Date());

    assertEquals("1.24", main.sensor_data_record_obs_ppp);
    assertEquals("2", main.sensor_data_record_obs_a);
    assertEquals("1.2", BarographModel.pressure_amount_tendency);
    assertEquals("1.2", BarographModel.jTextField1.getText());
    assertSelectedButton(3);
  }

  @Test
  public void markerAndMalformedRecordsDoNotProduceAReading() throws Exception {
    writeRawRecord(duoRecord(" *", "2", recentDate()));
    runWorkflow();
    assertEquals(" *", main.sensor_data_record_obs_ppp);
    assertEquals("", main.sensor_data_record_obs_a);
    assertEquals("", BarographModel.pressure_amount_tendency);
    assertNoSelectedButtons();

    resetModel();
    writeRawRecord("1000.0,1000.0,1.0*" + main.sdf4.format(recentDate()));
    runWorkflow();
    assertEquals("", main.sensor_data_record_obs_ppp);
    assertEquals("", main.sensor_data_record_obs_a);
    assertEquals("", BarographModel.pressure_amount_tendency);
  }

  @Test
  public void threeMinuteFreshnessBoundaryClearsStaleData() throws Exception {
    runRecord("1.0", "2", new Date());
    assertEquals("1.0", BarographModel.pressure_amount_tendency);

    resetModel();
    writeRawRecord(duoRecord("1.0", "2", new Date(System.currentTimeMillis() - 10 * 60 * 1000L)));
    runWorkflow();
    assertEquals("", main.sensor_data_record_obs_ppp);
    assertEquals("", main.sensor_data_record_obs_a);
    assertEquals("", BarographModel.pressure_amount_tendency);
    assertNoSelectedButtons();
  }

  @Test
  public void tendencyBoundariesAndAllCharacteristicMappingsResetButtons() throws Exception {
    runRecord("13.19", "2", new Date());
    assertEquals("13.2", BarographModel.pressure_amount_tendency);
    assertSelectedButton(3);

    runRecord("-1.26", "7", new Date());
    assertEquals("1.3", BarographModel.pressure_amount_tendency);
    assertSelectedButton(7);

    runRecord("0.04", "4", new Date());
    assertEquals("0.0", BarographModel.pressure_amount_tendency);
    assertSelectedButton(10);

    resetModel();
    runRecord("99.89", "2", new Date());
    assertEquals("", BarographModel.pressure_amount_tendency);
    assertSelectedButton(12);

    for (int code = 0; code <= 3; code++) {
      runRecord("1.0", Integer.toString(code), new Date());
      assertSelectedButton(code + 1);
    }
    for (int code = 5; code <= 8; code++) {
      runRecord("-1.0", Integer.toString(code), new Date());
      assertSelectedButton(code);
    }
    runRecord("0.0", "0", new Date());
    assertSelectedButton(9);
    runRecord("0.0", "4", new Date());
    assertSelectedButton(10);
    runRecord("0.0", "5", new Date());
    assertSelectedButton(11);
  }

  private void runRecord(String ppp, String a, Date recordDate) throws Exception {
    resetModel();
    writeRawRecord(duoRecord(ppp, a, recordDate));
    runWorkflow();
  }

  private void runWorkflow() throws Exception {
    main.obs_file_datum_tijd = new GregorianCalendar();
    Rs232MintakaDuoApppDataFilesWorkflow.start();
    waitFor(() -> main.obs_file_datum_tijd == null);
    flushEdt();
  }

  private void resetModel() {
    main.sensor_data_record_obs_ppp = "";
    main.sensor_data_record_obs_a = "";
    BarographModel.pressure_amount_tendency = "";
    BarographModel.a_code = "";
    BarographModel.jTextField1.setText("");
    BarographModel.tendency.set(null);
  }

  private void assertSelectedButton(int expectedButton) {
    assertEquals(
        BarographModel.Tendency.values()[expectedButton - 1], BarographModel.tendency.get());
  }

  private void assertNoSelectedButtons() {
    assertNull(BarographModel.tendency.get());
  }

  private String duoRecord(String ppp, String a, Date recordDate) {
    return "1000.0,1000.0," + ppp + "," + a + "*" + main.sdf4.format(recordDate);
  }

  private void writeRawRecord(String record) throws Exception {
    Calendar fileDate = new GregorianCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    File file =
        new File(main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
  }

  private Date recentDate() {
    return new Date(System.currentTimeMillis() - 2 * 60 * 1000L);
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
    assertTrue("Timed out waiting for Mintaka Duo a-ppp workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      SwingUtilities.invokeAndWait(() -> {});
    }
  }
}
