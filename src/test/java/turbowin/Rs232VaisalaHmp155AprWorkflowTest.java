package turbowin;

import static org.junit.Assert.assertEquals;
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

public class Rs232VaisalaHmp155AprWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDir;
  private SimpleDateFormat originalSdf3;
  private SimpleDateFormat originalSdf4;
  private GregorianCalendar originalObservationDate;
  private String originalAirTemp;
  private String originalWetBulbTemp;
  private String originalRh;
  private String originalSnTtt;
  private String originalTtt;
  private String originalSnTwet;
  private String originalTwet;
  private String originalSnTdew;
  private String originalTdew;
  private double originalDewPoint;
  private double originalRelativeHumidity;
  private boolean originalWetBulbFrozen;
  private String originalSendMethod;
  private boolean originalApr;
  private int originalConnectionMode;
  private boolean originalAirTempFromAws;
  private boolean originalRhFromAws;
  private boolean originalSstFromAws;
  private boolean originalDisplayedAwsDataObsolete;
  private boolean originalObsoleteData;
  private boolean originalObsoleteDataIi;
  private JTextField originalMainAirField;
  private JTextField originalMainWetBulbField;
  private JTextField originalMainDewPointField;
  private JTextField originalMainSeaWaterField;

  @Before
  public void setUp() {
    originalLogsDir = main.logs_dir;
    originalSdf3 = main.sdf3;
    originalSdf4 = main.sdf4;
    originalObservationDate = main.obs_file_datum_tijd;
    originalAirTemp = mytemp.air_temp;
    originalWetBulbTemp = mytemp.wet_bulb_temp;
    originalRh = mytemp.RH;
    originalSnTtt = mytemp.sn_TTT_code;
    originalTtt = mytemp.TTT_code;
    originalSnTwet = mytemp.sn_TbTbTb_code;
    originalTwet = mytemp.TbTbTb_code;
    originalSnTdew = mytemp.sn_TdTdTd_code;
    originalTdew = mytemp.TdTdTd_code;
    originalDewPoint = mytemp.double_dew_point;
    originalRelativeHumidity = mytemp.double_rv;
    originalWetBulbFrozen = mytemp.wet_bulb_frozen;
    originalSendMethod = main.APTR_AWSR_send_method;
    originalApr = main.APR;
    originalConnectionMode = main.RS232_connection_mode;
    originalAirTempFromAws = main.air_temp_from_AWS_present;
    originalRhFromAws = main.rh_from_AWS_present;
    originalSstFromAws = main.SST_from_AWS_present;
    originalDisplayedAwsDataObsolete = main.displayed_aws_data_obsolate;
    originalObsoleteData = main.obsolate_data_flag;
    originalObsoleteDataIi = main.obsolate_data_flag_II;
    originalMainAirField = main.jTextField36;
    originalMainWetBulbField = main.jTextField37;
    originalMainDewPointField = main.jTextField38;
    originalMainSeaWaterField = main.jTextField40;

    main.logs_dir = temporaryFolder.getRoot().getPath();
    main.sdf3 = utcFormat("yyyyMMddHH");
    main.sdf4 = utcFormat("yyyyMMddHHmm");
    main.APTR_AWSR_send_method = "";
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
    mytemp.sn_TTT_code = originalSnTtt;
    mytemp.TTT_code = originalTtt;
    mytemp.sn_TbTbTb_code = originalSnTwet;
    mytemp.TbTbTb_code = originalTwet;
    mytemp.sn_TdTdTd_code = originalSnTdew;
    mytemp.TdTdTd_code = originalTdew;
    mytemp.double_dew_point = originalDewPoint;
    mytemp.double_rv = originalRelativeHumidity;
    mytemp.wet_bulb_frozen = originalWetBulbFrozen;
    main.APTR_AWSR_send_method = originalSendMethod;
    main.APR = originalApr;
    main.RS232_connection_mode = originalConnectionMode;
    main.air_temp_from_AWS_present = originalAirTempFromAws;
    main.rh_from_AWS_present = originalRhFromAws;
    main.SST_from_AWS_present = originalSstFromAws;
    main.displayed_aws_data_obsolate = originalDisplayedAwsDataObsolete;
    main.obsolate_data_flag = originalObsoleteData;
    main.obsolate_data_flag_II = originalObsoleteDataIi;
    main.jTextField36 = originalMainAirField;
    main.jTextField37 = originalMainWetBulbField;
    main.jTextField38 = originalMainDewPointField;
    main.jTextField40 = originalMainSeaWaterField;
  }

  @Test
  public void readsFixedWidthFieldsAndPreparesImmtAndFm13Values() throws Exception {
    runRecord("-12.3", "-0.4", "-5.6", "75.0", "APR", new Date());

    assertEquals("-12.3", mytemp.air_temp);
    assertEquals("-0.4", mytemp.wet_bulb_temp);
    assertEquals("75.0", mytemp.RH);
    assertEquals(-5.6, mytemp.double_dew_point, 0.0);
    assertEquals(0.75, mytemp.double_rv, 0.0);
    assertEquals("1", mytemp.sn_TTT_code);
    assertEquals("123", mytemp.TTT_code);
    assertEquals("1", mytemp.sn_TbTbTb_code);
    assertEquals("004", mytemp.TbTbTb_code);
    assertEquals("1", mytemp.sn_TdTdTd_code);
    assertEquals("056", mytemp.TdTdTd_code);
  }

  @Test
  public void missingMalformedAndStaleRecordsClearAprValues() throws Exception {
    mytemp.air_temp = "old";
    mytemp.wet_bulb_temp = "old";
    mytemp.RH = "old";
    mytemp.double_dew_point = 10.0;
    mytemp.double_rv = 0.5;
    runWithoutRecord();
    assertCleared();

    resetModel();
    mytemp.air_temp = "old";
    writeRawRecord("malformed");
    runWorkflow("APR");
    assertCleared();

    resetModel();
    mytemp.air_temp = "old";
    writeRecord(
        "20.0",
        "0.0",
        "10.0",
        "50.0",
        new Date(System.currentTimeMillis() - 20 * 60 * 1000L));
    runWorkflow("APR");
    assertCleared();
  }

  @Test
  public void temperatureHumidityAndDewPointBoundariesAndSentinelsArePreserved() throws Exception {
    runRecord("99.9", "-99.9", "-70.0", "0.0", "APR", new Date());

    assertEquals("99.9", mytemp.air_temp);
    assertEquals("", mytemp.wet_bulb_temp);
    assertEquals("", mytemp.RH);
    assertEquals(main.INVALID, mytemp.double_dew_point, 0.0);
    assertEquals(main.INVALID, mytemp.double_rv, 0.0);

    resetModel();
    runRecord("*", "*", "*", "*", "APR", new Date());

    assertEquals("*", mytemp.air_temp);
    assertEquals("*", mytemp.wet_bulb_temp);
    assertEquals("*", mytemp.RH);
    assertEquals(main.INVALID, mytemp.double_dew_point, 0.0);
    assertEquals(main.INVALID, mytemp.double_rv, 0.0);
  }

  @Test
  public void aprSendsOnlyWhenAirTemperatureIsAvailable() throws Exception {
    runRecord("20.0", "0.0", "10.0", "50.0", "APR", new Date());
    assertEquals("20.0", mytemp.air_temp);

    resetModel();
    runRecord("bad", "0.0", "10.0", "50.0", "APR", new Date());
    assertEquals("", mytemp.air_temp);
  }

  @Test
  public void mainScreenDestinationUpdatesTemperatureFields() throws Exception {
    main.APR = true;
    main.RS232_connection_mode = 0;
    main.air_temp_from_AWS_present = false;
    main.rh_from_AWS_present = false;
    main.SST_from_AWS_present = false;
    main.displayed_aws_data_obsolate = false;
    main.obsolate_data_flag = false;
    main.obsolate_data_flag_II = false;
    main.jTextField36 = new JTextField();
    main.jTextField37 = new JTextField();
    main.jTextField38 = new JTextField();
    main.jTextField40 = new JTextField();

    runRecord("20.0", "0.0", "10.0", "50.0", "MAIN_SCREEN", new Date());

    assertEquals("20.0 \u00b0C", main.jTextField36.getText());
    assertEquals("0.0 \u00b0C", main.jTextField37.getText());
    assertEquals("10.0 \u00b0C", main.jTextField38.getText());
  }

  private void runRecord(
      String airTemp,
      String wetBulbTemp,
      String dewPoint,
      String rh,
      String destination,
      Date recordDate)
      throws Exception {
    writeRecord(airTemp, wetBulbTemp, dewPoint, rh, recordDate);
    runWorkflow(destination);
  }

  private void runWithoutRecord() throws Exception {
    runWorkflow("APR");
  }

  private void runWorkflow(String destination) throws Exception {
    Rs232VaisalaHmp155AprWorkflow.start(destination, false);
    waitFor(() -> main.obs_file_datum_tijd == null);
    flushEdt();
  }

  private void writeRecord(
      String airTemp, String wetBulbTemp, String dewPoint, String rh, Date recordDate)
      throws Exception {
    String record =
        field(airTemp)
            + " "
            + field(wetBulbTemp)
            + " "
            + field(dewPoint)
            + " "
            + field(rh)
            + " "
            + main.sdf4.format(recordDate);
    assertEquals(main.RECORD_LENGTE_HMP155, record.length());
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
    mytemp.sn_TTT_code = "";
    mytemp.TTT_code = "";
    mytemp.sn_TbTbTb_code = "";
    mytemp.TbTbTb_code = "";
    mytemp.sn_TdTdTd_code = "";
    mytemp.TdTdTd_code = "";
    mytemp.double_dew_point = main.INVALID;
    mytemp.double_rv = main.INVALID;
    mytemp.wet_bulb_frozen = false;
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
    assertTrue("Timed out waiting for HMP155 APR workflow", condition.getAsBoolean());
  }

  private static void flushEdt() throws Exception {
    for (int attempt = 0; attempt < 10; attempt++) {
      Thread.sleep(50);
      SwingUtilities.invokeAndWait(() -> {});
    }
  }
}
