package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;
import javax.swing.JTextField;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232SendSensorDataToAprFm13WorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private final Map<Field, JTextField> originalTextFields = new HashMap<>();
  private HttpServer server;
  private String originalLogsDirectory;
  private SimpleDateFormat originalSystemLogMonthFormat;
  private SimpleDateFormat originalSystemLogMessageFormat;
  private String originalUploadUrl;
  private String originalObservation;
  private String originalStationId;
  private String originalYear;
  private String originalMonthCode;
  private String originalDayCode;
  private String originalHourCode;
  private String originalQcCode;
  private String originalLatitudeCode;
  private String originalLongitudeCode;
  private String originalLastRecord;
  private boolean originalApr;
  private int originalConnectionMode;
  private int originalResponseCode;

  @Before
  public void setUp() throws Exception {
    originalLogsDirectory = main.logs_dir;
    originalSystemLogMonthFormat = main.sdf_tsl_1;
    originalSystemLogMessageFormat = main.sdf_tsl_2;
    originalUploadUrl = main.upload_URL;
    originalObservation = main.obs_write;
    originalStationId = main.station_ID;
    originalYear = mydatetime.year;
    originalMonthCode = mydatetime.MM_code;
    originalDayCode = mydatetime.YY_code;
    originalHourCode = mydatetime.GG_code;
    originalQcCode = myposition.Qc_code;
    originalLatitudeCode = myposition.lalala_code;
    originalLongitudeCode = myposition.lolololo_code;
    originalLastRecord = main.last_record;
    originalApr = main.APR;
    originalConnectionMode = main.RS232_connection_mode;
    originalResponseCode = main_RS232_RS422.APR_server_response_code;
    replaceStaticTextFields(main.class, myposition.class, mybarometer.class, mybarograph.class,
        mytemp.class);

    File logsDirectory = temporaryFolder.newFolder("logs");
    new File(logsDirectory, main.TURBOWIN_SYSTEM_LOGS_DIR).mkdirs();
    main.logs_dir = logsDirectory.getPath();
    main.sdf_tsl_1 = utcFormat("MMM_yyyy");
    main.sdf_tsl_2 = utcFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
    main.APR = true;
    main.RS232_connection_mode = 0;
    main.last_record = "";
    main_RS232_RS422.APR_server_response_code = -1;
    setRequiredObservationData();
  }

  @After
  public void tearDown() throws Exception {
    if (server != null) {
      server.stop(0);
    }
    main.logs_dir = originalLogsDirectory;
    main.sdf_tsl_1 = originalSystemLogMonthFormat;
    main.sdf_tsl_2 = originalSystemLogMessageFormat;
    main.upload_URL = originalUploadUrl;
    main.obs_write = originalObservation;
    main.station_ID = originalStationId;
    mydatetime.year = originalYear;
    mydatetime.MM_code = originalMonthCode;
    mydatetime.YY_code = originalDayCode;
    mydatetime.GG_code = originalHourCode;
    myposition.Qc_code = originalQcCode;
    myposition.lalala_code = originalLatitudeCode;
    myposition.lolololo_code = originalLongitudeCode;
    main.last_record = originalLastRecord;
    main.APR = originalApr;
    main.RS232_connection_mode = originalConnectionMode;
    main_RS232_RS422.APR_server_response_code = originalResponseCode;
    restoreStaticTextFields();
  }

  @Test
  public void invalidComposedObservationUpdatesSharedResponseCode() throws Exception {
    main.station_ID = "";

    Rs232SendSensorDataToAprFm13Workflow.start(false);

    awaitResponse(main.INVALID_RESPONSE_FORMAT_FM13);
    assertEquals(main.UNDEFINED, main.obs_write);
    awaitSystemLogFile();
  }

  @Test
  public void malformedUrlAndNoInternetUpdateDistinctResponseCodes() throws Exception {
    main.upload_URL = "http://[invalid";
    Rs232SendSensorDataToAprFm13Workflow.start(false);
    awaitResponse(main.RESPONSE_MALFORMED_URL);
    awaitSystemLogFile();

    main_RS232_RS422.APR_server_response_code = -1;
    main.upload_URL = "http://127.0.0.1:1/?";
    Rs232SendSensorDataToAprFm13Workflow.start(false);
    awaitResponse(main.RESPONSE_NO_INTERNET);
    awaitSystemLogFile();
  }

  @Test
  public void httpSuccessSavesImmtAndResetsObservationState() throws Exception {
    startServer(200);

    Rs232SendSensorDataToAprFm13Workflow.start(false);

    awaitResponse(200);
    awaitSystemLogFile();
    awaitImmtFile();
    waitFor(() -> "".equals(mydatetime.year));
    assertEquals("", mydatetime.MM_code);
    assertEquals("", mydatetime.YY_code);
    assertEquals("", mydatetime.GG_code);
  }

  @Test
  public void retryControlsFailureImmtSaveAndReset() throws Exception {
    startServer(500);

    Rs232SendSensorDataToAprFm13Workflow.start(false);

    awaitResponse(500);
    awaitSystemLogFile();
    assertEquals("2024", mydatetime.year);
    assertTrue(!Files.exists(immtFile()));

    main_RS232_RS422.APR_server_response_code = -1;
    Rs232SendSensorDataToAprFm13Workflow.start(true);

    awaitResponse(500);
    awaitSystemLogFile();
    awaitImmtFile();
    waitFor(() -> "".equals(mydatetime.year));
  }

  private void setRequiredObservationData() {
    main.station_ID = "TEST";
    mydatetime.year = "2024";
    mydatetime.MM_code = "01";
    mydatetime.YY_code = "01";
    mydatetime.GG_code = "12";
    myposition.Qc_code = "1";
    myposition.lalala_code = "234";
    myposition.lolololo_code = "5678";
  }

  private void startServer(int responseCode) throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/",
        exchange -> {
          exchange.sendResponseHeaders(responseCode, -1);
          exchange.close();
        });
    server.start();
    main.upload_URL = "http://127.0.0.1:" + server.getAddress().getPort() + "/?";
  }

  private void awaitResponse(int expected) throws Exception {
    waitFor(() -> main_RS232_RS422.APR_server_response_code == expected);
  }

  private void awaitSystemLogFile() throws Exception {
    waitFor(() -> Files.exists(systemLogFile()));
  }

  private void awaitImmtFile() throws Exception {
    waitFor(() -> Files.exists(immtFile()));
    waitFor(
        () -> {
          try {
            return Files.size(immtFile()) > 0;
          } catch (Exception ex) {
            return false;
          }
        });
    assertTrue(Files.readString(immtFile(), StandardCharsets.UTF_8).length() > 0);
  }

  private Path systemLogFile() {
    return new File(
            main.logs_dir,
            main.TURBOWIN_SYSTEM_LOGS_DIR
                + File.separator
                + SystemLogWriterWorkflow.logFileName(main.sdf_tsl_1.format(new Date())))
        .toPath();
  }

  private Path immtFile() {
    return new File(main.logs_dir, main.IMMT_LOG).toPath();
  }

  private void replaceStaticTextFields(Class<?>... classes) throws Exception {
    for (Class<?> type : classes) {
      for (Field field : type.getDeclaredFields()) {
        if (field.getType() == JTextField.class && Modifier.isStatic(field.getModifiers())) {
          field.setAccessible(true);
          originalTextFields.put(field, (JTextField) field.get(null));
          field.set(null, new JTextField());
        }
      }
    }
  }

  private void restoreStaticTextFields() throws Exception {
    for (Map.Entry<Field, JTextField> entry : originalTextFields.entrySet()) {
      entry.getKey().set(null, entry.getValue());
    }
  }

  private static SimpleDateFormat utcFormat(String pattern) {
    return utcFormat(pattern, Locale.ROOT);
  }

  private static SimpleDateFormat utcFormat(String pattern, Locale locale) {
    SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format;
  }

  private static void waitFor(BooleanSupplier condition) throws Exception {
    long deadline = System.nanoTime() + 5_000_000_000L;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
    assertTrue("Timed out waiting for APR FM13 server workflow", condition.getAsBoolean());
  }
}
