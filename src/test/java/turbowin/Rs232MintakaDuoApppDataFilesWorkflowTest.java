package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JRadioButton;
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
  private final JRadioButton[] originalButtons = new JRadioButton[12];

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObservationDate = main.obs_file_datum_tijd;
    originalPpp = main.sensor_data_record_obs_ppp;
    originalA = main.sensor_data_record_obs_a;
    originalAmount = mybarograph.pressure_amount_tendency;
    originalACode = mybarograph.a_code;
    originalTextField = mybarograph.jTextField1;
    originalButtons[0] = mybarograph.jRadioButton1;
    originalButtons[1] = mybarograph.jRadioButton2;
    originalButtons[2] = mybarograph.jRadioButton3;
    originalButtons[3] = mybarograph.jRadioButton4;
    originalButtons[4] = mybarograph.jRadioButton5;
    originalButtons[5] = mybarograph.jRadioButton6;
    originalButtons[6] = mybarograph.jRadioButton7;
    originalButtons[7] = mybarograph.jRadioButton8;
    originalButtons[8] = mybarograph.jRadioButton9;
    originalButtons[9] = mybarograph.jRadioButton10;
    originalButtons[10] = mybarograph.jRadioButton11;
    originalButtons[11] = mybarograph.jRadioButton12;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    mybarograph.jTextField1 = new JTextField();
    setButtonReferences(newButtons());
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
    mybarograph.pressure_amount_tendency = originalAmount;
    mybarograph.a_code = originalACode;
    mybarograph.jTextField1 = originalTextField;
    setButtonReferences(originalButtons);
  }

  @Test
  public void exactDuoRecordParsesTendencyAndCharacteristic() throws Exception {
    runRecord("1.24", "2", new Date());

    assertEquals("1.24", main.sensor_data_record_obs_ppp);
    assertEquals("2", main.sensor_data_record_obs_a);
    assertEquals("1.2", mybarograph.pressure_amount_tendency);
    assertEquals("1.2", mybarograph.jTextField1.getText());
    assertSelectedButton(3);
  }

  @Test
  public void markerAndMalformedRecordsDoNotProduceAReading() throws Exception {
    writeRawRecord(duoRecord(" *", "2", recentDate()));
    runWorkflow();
    assertEquals(" *", main.sensor_data_record_obs_ppp);
    assertEquals("", main.sensor_data_record_obs_a);
    assertEquals("", mybarograph.pressure_amount_tendency);
    assertNoSelectedButtons();

    resetModel();
    writeRawRecord("1000.0,1000.0,1.0*" + main.sdf4.format(recentDate()));
    runWorkflow();
    assertEquals("", main.sensor_data_record_obs_ppp);
    assertEquals("", main.sensor_data_record_obs_a);
    assertEquals("", mybarograph.pressure_amount_tendency);
  }

  @Test
  public void threeMinuteFreshnessBoundaryClearsStaleData() throws Exception {
    runRecord("1.0", "2", new Date());
    assertEquals("1.0", mybarograph.pressure_amount_tendency);

    resetModel();
    writeRawRecord(duoRecord("1.0", "2", new Date(System.currentTimeMillis() - 10 * 60 * 1000L)));
    runWorkflow();
    assertEquals("", main.sensor_data_record_obs_ppp);
    assertEquals("", main.sensor_data_record_obs_a);
    assertEquals("", mybarograph.pressure_amount_tendency);
    assertNoSelectedButtons();
  }

  @Test
  public void tendencyBoundariesAndAllCharacteristicMappingsResetButtons() throws Exception {
    runRecord("13.19", "2", new Date());
    assertEquals("13.2", mybarograph.pressure_amount_tendency);
    assertSelectedButton(3);

    runRecord("-1.26", "7", new Date());
    assertEquals("1.3", mybarograph.pressure_amount_tendency);
    assertSelectedButton(7);

    runRecord("0.04", "4", new Date());
    assertEquals("0.0", mybarograph.pressure_amount_tendency);
    assertSelectedButton(10);

    resetModel();
    runRecord("99.89", "2", new Date());
    assertEquals("", mybarograph.pressure_amount_tendency);
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
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";
    mybarograph.jTextField1.setText("");
    setButtonReferences(currentButtons());
    for (JRadioButton button : currentButtons()) {
      button.setSelected(false);
    }
  }

  private void assertSelectedButton(int expectedButton) {
    JRadioButton[] buttons = currentButtons();
    for (int index = 0; index < buttons.length; index++) {
      if (index + 1 == expectedButton) {
        assertTrue(buttons[index].isSelected());
      } else {
        assertFalse(buttons[index].isSelected());
      }
    }
  }

  private void assertNoSelectedButtons() {
    for (JRadioButton button : currentButtons()) {
      assertFalse(button.isSelected());
    }
  }

  private JRadioButton[] currentButtons() {
    return new JRadioButton[] {
      mybarograph.jRadioButton1,
      mybarograph.jRadioButton2,
      mybarograph.jRadioButton3,
      mybarograph.jRadioButton4,
      mybarograph.jRadioButton5,
      mybarograph.jRadioButton6,
      mybarograph.jRadioButton7,
      mybarograph.jRadioButton8,
      mybarograph.jRadioButton9,
      mybarograph.jRadioButton10,
      mybarograph.jRadioButton11,
      mybarograph.jRadioButton12
    };
  }

  private void setButtonReferences(JRadioButton[] buttons) {
    mybarograph.jRadioButton1 = buttons[0];
    mybarograph.jRadioButton2 = buttons[1];
    mybarograph.jRadioButton3 = buttons[2];
    mybarograph.jRadioButton4 = buttons[3];
    mybarograph.jRadioButton5 = buttons[4];
    mybarograph.jRadioButton6 = buttons[5];
    mybarograph.jRadioButton7 = buttons[6];
    mybarograph.jRadioButton8 = buttons[7];
    mybarograph.jRadioButton9 = buttons[8];
    mybarograph.jRadioButton10 = buttons[9];
    mybarograph.jRadioButton11 = buttons[10];
    mybarograph.jRadioButton12 = buttons[11];
  }

  private JRadioButton[] newButtons() {
    JRadioButton[] buttons = new JRadioButton[12];
    for (int index = 0; index < buttons.length; index++) {
      buttons[index] = new JRadioButton();
    }
    return buttons;
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
