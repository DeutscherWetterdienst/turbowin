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

public class Rs232VaisalaPressureTrendWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private int originalRecordLength;
  private int originalDatePosition;
  private int originalAPosition;
  private int originalPppPosition;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private String originalPpp;
  private String originalA;
  private GregorianCalendar originalObservationDate;
  private String originalAmount;
  private JTextField originalTextField;
  private final JRadioButton[] originalButtons = new JRadioButton[12];

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalRecordLength = main.type_record_lengte;
    originalDatePosition = main.type_record_datum_tijd_begin_pos;
    originalAPosition = main.type_record_a_begin_pos;
    originalPppPosition = main.type_record_ppp_begin_pos;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalPpp = main.sensor_data_record_obs_ppp;
    originalA = main.sensor_data_record_obs_a;
    originalObservationDate = main.obs_file_datum_tijd;
    originalAmount = mybarograph.pressure_amount_tendency;
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
    main.type_record_lengte = main.RECORD_LENGTE_PTB220;
    main.type_record_datum_tijd_begin_pos = main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220;
    main.type_record_a_begin_pos = main.RECORD_a_BEGIN_POS_PTB220;
    main.type_record_ppp_begin_pos = main.RECORD_ppp_BEGIN_POS_PTB220;
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main.sensor_data_record_obs_ppp = "";
    main.sensor_data_record_obs_a = "";
    mybarograph.pressure_amount_tendency = "";
    mybarograph.jTextField1 = new JTextField();
    setButtons(false);
  }

  @After
  public void tearDown() {
    main.logs_dir = originalLogsDir;
    main.type_record_lengte = originalRecordLength;
    main.type_record_datum_tijd_begin_pos = originalDatePosition;
    main.type_record_a_begin_pos = originalAPosition;
    main.type_record_ppp_begin_pos = originalPppPosition;
    main.sdf3 = originalSdf3;
    main.sdf4 = originalSdf4;
    main.sensor_data_record_obs_ppp = originalPpp;
    main.sensor_data_record_obs_a = originalA;
    main.obs_file_datum_tijd = originalObservationDate;
    mybarograph.pressure_amount_tendency = originalAmount;
    mybarograph.jTextField1 = originalTextField;
    setButtonReferences(originalButtons);
  }

  @Test
  public void freshRecordRoundsTendencyAndMapsPositiveNegativeAndZeroCodes() throws Exception {
    runRecord("13.19", "2", new Date());
    assertEquals("13.2", mybarograph.pressure_amount_tendency);
    assertEquals("13.2", mybarograph.jTextField1.getText());
    assertSelectedButton(3);

    runRecord("-1.0", "7", new Date());
    assertEquals("1.0", mybarograph.pressure_amount_tendency);
    assertSelectedButton(7);

    runRecord("0.0", "4", new Date());
    assertEquals("0.0", mybarograph.pressure_amount_tendency);
    assertSelectedButton(10);
  }

  @Test
  public void representativeCharacteristicCodesSelectTheirPressureDirectionStates()
      throws Exception {
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

  @Test
  public void malformedValuesDoNotReplaceExistingTendencyOrSelection() throws Exception {
    mybarograph.pressure_amount_tendency = "old";
    mybarograph.jTextField1.setText("old");
    mybarograph.jRadioButton1.setSelected(true);

    runRecord("abcde", "x", new Date());

    assertEquals("old", mybarograph.pressure_amount_tendency);
    assertEquals("old", mybarograph.jTextField1.getText());
    assertTrue(mybarograph.jRadioButton1.isSelected());
  }

  @Test
  public void staleRecordClearsTheValuesReadForTheObservation() throws Exception {
    main.sensor_data_record_obs_ppp = "pending";
    main.sensor_data_record_obs_a = "pending";

    writeRecord("1.0", "2", new Date(System.currentTimeMillis() - 10 * 60 * 1000L));
    main.obs_file_datum_tijd = new GregorianCalendar();
    Rs232VaisalaPressureTrendWorkflow.start();
    waitFor(
        () ->
            main.sensor_data_record_obs_ppp.isEmpty()
                && main.sensor_data_record_obs_a.isEmpty()
                && main.obs_file_datum_tijd == null);
    flushEdt();

    assertEquals("", main.sensor_data_record_obs_ppp);
    assertEquals("", main.sensor_data_record_obs_a);
  }

  private void runRecord(String ppp, String a, Date recordDate) throws Exception {
    main.sensor_data_record_obs_ppp = "pending";
    main.sensor_data_record_obs_a = "pending";
    writeRecord(ppp, a, recordDate);
    main.obs_file_datum_tijd = new GregorianCalendar();
    Rs232VaisalaPressureTrendWorkflow.start();
    waitFor(
        () ->
            main.sensor_data_record_obs_ppp.equals(padded(ppp))
                && main.sensor_data_record_obs_a.equals(a)
                && main.obs_file_datum_tijd == null);
    flushEdt();
  }

  private void writeRecord(String ppp, String a, Date recordDate) throws Exception {
    String record = fixedWidthRecord(ppp, a, main.sdf4.format(recordDate));
    Calendar fileDate = new GregorianCalendar();
    fileDate.add(Calendar.MINUTE, -2);
    File file =
        new File(
            main.logs_dir, "sensor_data_" + main.sdf3.format(fileDate.getTime()) + ".txt");
    Files.writeString(file.toPath(), record + System.lineSeparator());
  }

  private String fixedWidthRecord(String ppp, String a, String timestamp) {
    char[] record = "                                    ".toCharArray();
    String pppValue = padded(ppp);
    record[main.RECORD_ppp_BEGIN_POS_PTB220] = pppValue.charAt(0);
    record[main.RECORD_ppp_BEGIN_POS_PTB220 + 1] = pppValue.charAt(1);
    record[main.RECORD_ppp_BEGIN_POS_PTB220 + 2] = pppValue.charAt(2);
    record[main.RECORD_ppp_BEGIN_POS_PTB220 + 3] = pppValue.charAt(3);
    record[main.RECORD_ppp_BEGIN_POS_PTB220 + 4] = pppValue.charAt(4);
    record[main.RECORD_a_BEGIN_POS_PTB220] = a.charAt(0);
    for (int index = 0; index < timestamp.length(); index++) {
      record[main.RECORD_DATUM_TIJD_BEGIN_POS_PTB220 + index] = timestamp.charAt(index);
    }
    return new String(record);
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

  private void setButtons(boolean selected) {
    setButtonReferences(new JRadioButton[] {
      new JRadioButton(), new JRadioButton(), new JRadioButton(), new JRadioButton(),
      new JRadioButton(), new JRadioButton(), new JRadioButton(), new JRadioButton(),
      new JRadioButton(), new JRadioButton(), new JRadioButton(), new JRadioButton()
    });
    for (JRadioButton button : currentButtons()) {
      button.setSelected(selected);
    }
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

  private static String padded(String value) {
    return String.format("%-5s", value);
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
    assertTrue("Timed out waiting for pressure trend workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    Thread.sleep(50);
    SwingUtilities.invokeAndWait(() -> {});
  }
}
