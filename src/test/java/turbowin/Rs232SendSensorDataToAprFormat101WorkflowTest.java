package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import javax.swing.JTextField;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232SendSensorDataToAprFormat101WorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private final Map<Field, JTextField> originalTextFields = new HashMap<>();
  private HttpServer server;
  private String originalLogsDirectory;
  private SimpleDateFormat originalSystemLogMonthFormat;
  private SimpleDateFormat originalSystemLogMessageFormat;
  private String originalUploadUrl;
  private String originalStationId;
  private String originalServerProtocol;
  private String originalYear;
  private String originalDay;
  private String originalHour;
  private String originalMonthCode;
  private String originalDayCode;
  private String originalHourCode;
  private String originalQcCode;
  private String originalLatitudeCode;
  private String originalLongitudeCode;
  private String originalLastRecord;
  private FORMAT_101 originalFormat101;
  private boolean originalApr;
  private boolean originalAwsr;
  private int originalConnectionMode;
  private int originalResponseCode;
  private String originalFormat101Observation;

  @Before
  public void setUp() throws Exception {
    originalLogsDirectory = main.logs_dir;
    originalSystemLogMonthFormat = main.sdf_tsl_1;
    originalSystemLogMessageFormat = main.sdf_tsl_2;
    originalUploadUrl = main.upload_URL;
    originalStationId = main.station_ID;
    originalServerProtocol = main.server_com_protocol;
    originalYear = mydatetime.year;
    originalDay = mydatetime.day;
    originalHour = mydatetime.hour;
    originalMonthCode = mydatetime.MM_code;
    originalDayCode = mydatetime.YY_code;
    originalHourCode = mydatetime.GG_code;
    originalQcCode = myposition.Qc_code;
    originalLatitudeCode = myposition.lalala_code;
    originalLongitudeCode = myposition.lolololo_code;
    originalLastRecord = main.last_record;
    originalFormat101 = main.format_101_class;
    originalApr = main.APR;
    originalAwsr = main.AWSR;
    originalConnectionMode = main.RS232_connection_mode;
    originalResponseCode = main_RS232_RS422.APR_server_response_code;
    originalFormat101Observation = main_RS232_RS422.format_101_obs;

    replaceStaticTextFields(main.class, myposition.class, mybarometer.class, mybarograph.class,
        mytemp.class);

    File logsDirectory = temporaryFolder.newFolder("logs");
    new File(logsDirectory, main.TURBOWIN_SYSTEM_LOGS_DIR).mkdirs();
    main.logs_dir = logsDirectory.getPath();
    main.sdf_tsl_1 = utcFormat("MMM_yyyy");
    main.sdf_tsl_2 = utcFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
    main.APR = true;
    main.AWSR = false;
    main.RS232_connection_mode = 0;
    main.server_com_protocol = main.HTTP_PROTOCOL;
    main.last_record = "";
    main_RS232_RS422.APR_server_response_code = -1;
    setRequiredObservationData();
    installFormat101Module("");
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
    main.station_ID = originalStationId;
    main.server_com_protocol = originalServerProtocol;
    mydatetime.year = originalYear;
    mydatetime.day = originalDay;
    mydatetime.hour = originalHour;
    mydatetime.MM_code = originalMonthCode;
    mydatetime.YY_code = originalDayCode;
    mydatetime.GG_code = originalHourCode;
    myposition.Qc_code = originalQcCode;
    myposition.lalala_code = originalLatitudeCode;
    myposition.lolololo_code = originalLongitudeCode;
    main.last_record = originalLastRecord;
    main.format_101_class = originalFormat101;
    main.APR = originalApr;
    main.AWSR = originalAwsr;
    main.RS232_connection_mode = originalConnectionMode;
    main_RS232_RS422.APR_server_response_code = originalResponseCode;
    main_RS232_RS422.format_101_obs = originalFormat101Observation;
    restoreStaticTextFields();
  }

  @Test
  public void javaEncoderProducesCompressedObservation() throws Exception {
    String[] rawQuery = new String[1];
    startServer(
        exchange -> {
          rawQuery[0] = exchange.getRequestURI().getRawQuery();
          exchange.sendResponseHeaders(200, -1);
          exchange.close();
        });

    Rs232SendSensorDataToAprFormat101Workflow.start(false);
    awaitResponse(200);

    assertTrue(!main_RS232_RS422.format_101_obs.isEmpty());
    Path compressedFile = compressedFile();
    assertTrue(Files.exists(compressedFile));
    assertEquals(
        main_RS232_RS422.format_101_obs + System.lineSeparator(),
        Files.readString(compressedFile, StandardCharsets.ISO_8859_1));
    assertEquals(
        ServerObservationRequestBuilder.format101Url(main.upload_URL, main_RS232_RS422.format_101_obs)
            .substring(main.upload_URL.length()),
        rawQuery[0]);
    assertEquals(
        main_RS232_RS422.format_101_obs,
        Format101FileReader.readFirstLine(compressedFile.toFile()));
    assertEquals(
        main_RS232_RS422.format_101_obs,
        Format101EmailContentResolver.resolve(main.FORMAT_101_BODY, compressedFile.toFile()));

    Clipboard clipboard = new Clipboard("format 101 integration test");
    ObservationClipboardWriter.write(clipboard, main_RS232_RS422.format_101_obs);
    assertEquals(
        main_RS232_RS422.format_101_obs,
        clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor));
  }

  @Test
  public void failedJavaEncodingCannotReuseEarlierCompressedObservation() throws Exception {
    AtomicInteger requests = new AtomicInteger();
    startServer(
        exchange -> {
          requests.incrementAndGet();
          exchange.sendResponseHeaders(200, -1);
          exchange.close();
        });
    Path compressedFile = compressedFile();
    Files.writeString(compressedFile, "STALE_OBSERVATION\n", StandardCharsets.ISO_8859_1);
    Files.delete(
        Path.of(
            main.logs_dir,
            main.FORMAT_101_ROOT_DIR,
            "config",
            "S-AWS-101_modl_pilote.csv"));

    Rs232SendSensorDataToAprFormat101Workflow.start(false);

    awaitResponse(main.INVALID_RESPONSE_FORMAT_101);
    assertTrue(!Files.exists(compressedFile));
    assertEquals(0, requests.get());
    assertEquals("", main_RS232_RS422.format_101_obs);
  }

  @Test
  public void urlEncodesPayloadAndUsesConfiguredHttpProtocol() throws Exception {
    AtomicInteger requests = new AtomicInteger();
    String[] rawQuery = new String[1];
    startServer(
        exchange -> {
          requests.incrementAndGet();
          rawQuery[0] = exchange.getRequestURI().getRawQuery();
          exchange.sendResponseHeaders(200, -1);
          exchange.close();
        });
    installFormat101Module("BBXX TEST+é");

    main.server_com_protocol = main.HTTP_PROTOCOL;
    Rs232SendSensorDataToAprFormat101Workflow.start(false);

    awaitResponse(200);
    assertTrue(rawQuery[0].startsWith("obs="));
    assertTrue(rawQuery[0].contains("%"));
    assertEquals(1, requests.get());
  }

  @Test
  public void configuredHttpsProtocolUsesHttpsConnectionOnNetworkFailure() throws Exception {
    installFormat101Module("BBXX TEST");
    main.server_com_protocol = main.HTTPS_PROTOCOL;
    main.upload_URL = "https://127.0.0.1:1/?";

    Rs232SendSensorDataToAprFormat101Workflow.start(false);

    awaitResponse(main.RESPONSE_NO_INTERNET);
  }

  @Test
  public void returnsDeterministicHttpErrorAndNoInternetCodes() throws Exception {
    startServer(
        exchange -> {
          exchange.sendResponseHeaders(503, -1);
          exchange.close();
        });
    installFormat101Module("BBXX TEST");

    Rs232SendSensorDataToAprFormat101Workflow.start(false);
    awaitResponse(503);

    main_RS232_RS422.APR_server_response_code = -1;
    main.upload_URL = "http://127.0.0.1:1/?";
    Rs232SendSensorDataToAprFormat101Workflow.start(false);
    awaitResponse(main.RESPONSE_NO_INTERNET);
  }

  @Test
  public void retriesTransientTransportFailureBeforeReportingSuccess() throws Exception {
    AtomicInteger requests = new AtomicInteger();
    startServer(
        exchange -> {
          if (requests.incrementAndGet() == 1) {
            exchange.close();
          } else {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
          }
        });
    installFormat101Module("BBXX TEST");

    Rs232SendSensorDataToAprFormat101Workflow.start(false);

    awaitResponse(200);
    assertEquals(2, requests.get());
  }

  @Test
  public void retryControlsImmtSaveAndObservationResetAfterFailure() throws Exception {
    startServer(
        exchange -> {
          exchange.sendResponseHeaders(500, -1);
          exchange.close();
        });
    installFormat101Module("BBXX TEST");

    Rs232SendSensorDataToAprFormat101Workflow.start(false);
    awaitResponse(500);
    assertEquals("2024", mydatetime.year);
    assertTrue(!Files.exists(immtFile()));

    main_RS232_RS422.APR_server_response_code = -1;
    Rs232SendSensorDataToAprFormat101Workflow.start(true);

    awaitResponse(500);
    awaitImmtFile();
    waitFor(() -> "".equals(mydatetime.year));
    assertEquals("", myposition.Qc_code);
  }

  private void setRequiredObservationData() {
    main.station_ID = "TEST";
    mydatetime.year = "2024";
    mydatetime.day = "01";
    mydatetime.hour = "12";
    mydatetime.MM_code = "01";
    mydatetime.YY_code = "01";
    mydatetime.GG_code = "12";
    myposition.Qc_code = "1";
    myposition.lalala_code = "234";
    myposition.lolololo_code = "5678";
  }

  private void installFormat101Module(String output) throws Exception {
    Path format101Directory = Path.of(main.logs_dir, main.FORMAT_101_ROOT_DIR);
    Path configDirectory = format101Directory.resolve("config");
    Path tempDirectory = format101Directory.resolve(main.FORMAT_101_TEMP_DIR);
    Path logDirectory = format101Directory.resolve("log");
    Files.createDirectories(configDirectory);
    Files.createDirectories(tempDirectory);
    Files.createDirectories(logDirectory);

    try (InputStream pilot =
        getClass().getResourceAsStream("/format101/config/S-AWS-101_modl_pilote.csv")) {
      assertTrue(pilot != null);
      Files.copy(
          pilot,
          configDirectory.resolve("S-AWS-101_modl_pilote.csv"),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    Path compressor = format101Directory.resolve(FORMAT_101.COMPRESSION_EXE_LIN);
    String compressorScript;
    if ("empty".equals(output)) {
      compressorScript = "#!/bin/sh\n: > temp/HPK_format_101.txt\n";
    } else if (output.isEmpty()) {
      compressorScript = "#!/bin/sh\nexit 0\n";
    } else {
      compressorScript =
          "#!/bin/sh\nprintf '%s\\n' '" + output + "' > temp/HPK_format_101.txt\n";
    }
    writeExecutable(compressor, compressorScript);
  }

  private void startServer(com.sun.net.httpserver.HttpHandler handler) throws Exception {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/", handler);
    server.start();
    main.upload_URL = "http://127.0.0.1:" + server.getAddress().getPort() + "/?";
  }

  private void writeExecutable(Path path, String contents) throws Exception {
    Files.writeString(path, contents, StandardCharsets.UTF_8);
    assertTrue(path.toFile().setExecutable(true));
  }

  private void awaitResponse(int expected) throws Exception {
    waitFor(() -> main_RS232_RS422.APR_server_response_code == expected);
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

  private Path immtFile() {
    return Path.of(main.logs_dir, main.IMMT_LOG);
  }

  private Path compressedFile() {
    return Path.of(
        main.logs_dir,
        main.FORMAT_101_ROOT_DIR,
        main.FORMAT_101_TEMP_DIR,
        "HPK_" + main.FORMAT_101_INPUT_FILE);
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
    long deadline = System.nanoTime() + 10_000_000_000L;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
    assertTrue("Timed out waiting for APR Format 101 server workflow", condition.getAsBoolean());
  }
}
