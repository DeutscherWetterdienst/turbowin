package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Rs232OutputObsByEmailAllAprWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private final Map<Field, JTextField> originalTextFields = new HashMap<>();
  private String originalLogsDirectory;
  private SimpleDateFormat originalSystemLogMonthFormat;
  private SimpleDateFormat originalSystemLogMessageFormat;
  private String originalRecipient;
  private String originalObservationFormat;
  private String originalAwsUploadMethod;
  private String originalFormat101Mode;
  private String originalEmailModule;
  private String originalEmailSendMode;
  private String originalAutomaticSendMethod;
  private String originalCustomSecurity;
  private String originalCustomServer;
  private String originalCustomPassword;
  private String originalCustomPort;
  private String originalCustomAddress;
  private String originalEmailSubject;
  private String originalEmailCc;
  private String originalStationId;
  private String originalYear;
  private String originalMonth;
  private String originalDay;
  private String originalHour;
  private String originalMonthCode;
  private String originalDayCode;
  private String originalHourCode;
  private String originalQcCode;
  private String originalLatitudeCode;
  private String originalLongitudeCode;
  private String originalObservation;
  private boolean originalApr;
  private boolean originalAwsr;
  private FORMAT_101 originalFormat101Class;
  private Python_Email originalPythonEmailClass;
  private Jakarta_Email originalJakartaEmailClass;

  @Before
  public void setUp() throws Exception {
    originalLogsDirectory = main.logs_dir;
    originalSystemLogMonthFormat = main.sdf_tsl_1;
    originalSystemLogMessageFormat = main.sdf_tsl_2;
    originalRecipient = main.obs_email_recipient;
    originalObservationFormat = main.obs_format;
    originalAwsUploadMethod = main.eucaws_uploads_method;
    originalFormat101Mode = main.obs_101_email;
    originalEmailModule = main.custom_email_module;
    originalEmailSendMode = main.email_send_mode;
    originalAutomaticSendMethod = main.APTR_AWSR_send_method;
    originalCustomSecurity = main.custom_security;
    originalCustomServer = main.custom_email_server;
    originalCustomPassword = main.custom_password;
    originalCustomPort = main.custom_port;
    originalCustomAddress = main.your_custom_address;
    originalEmailSubject = main.obs_email_subject;
    originalEmailCc = main.obs_email_cc;
    originalStationId = main.station_ID;
    originalYear = mydatetime.year;
    originalMonth = mydatetime.month;
    originalDay = mydatetime.day;
    originalHour = mydatetime.hour;
    originalMonthCode = mydatetime.MM_code;
    originalDayCode = mydatetime.YY_code;
    originalHourCode = mydatetime.GG_code;
    originalQcCode = myposition.Qc_code;
    originalLatitudeCode = myposition.lalala_code;
    originalLongitudeCode = myposition.lolololo_code;
    originalObservation = main.obs_write;
    originalApr = main.APR;
    originalAwsr = main.AWSR;
    originalFormat101Class = main.format_101_class;
    originalPythonEmailClass = main.python_email_class;
    originalJakartaEmailClass = main.jakarta_email_class;
    replaceStaticTextFields(
        main.class, myposition.class, mybarometer.class, mybarograph.class, mytemp.class);

    File logsDirectory = temporaryFolder.newFolder("logs");
    new File(logsDirectory, main.TURBOWIN_SYSTEM_LOGS_DIR).mkdirs();
    main.logs_dir = logsDirectory.getPath();
    main.sdf_tsl_1 = utcFormat("MMM_yyyy");
    main.sdf_tsl_2 = utcFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
    main.obs_email_recipient = "recipient@example.org";
    main.obs_format = main.FORMAT_FM13;
    main.eucaws_uploads_method = "";
    main.obs_101_email = main.FORMAT_101_BODY;
    main.custom_email_module = main.JAKARTA_EMAIL;
    main.email_send_mode = main.EMAIL_SEND_CUSTOM;
    main.APTR_AWSR_send_method = main.APTR_AWSR_CUSTOM_MAIL;
    main.custom_security = main.CUSTOM_TLS;
    main.custom_email_server = "127.0.0.1";
    main.custom_password = "";
    main.custom_port = "1";
    main.your_custom_address = "sender@example.org";
    main.obs_email_subject = "Observation";
    main.obs_email_cc = "";
    main.APR = true;
    main.AWSR = false;
    main.format_101_class = null;
    main.python_email_class = null;
    main.jakarta_email_class = null;
    setRequiredObservationData();
  }

  @After
  public void tearDown() throws Exception {
    main.logs_dir = originalLogsDirectory;
    main.sdf_tsl_1 = originalSystemLogMonthFormat;
    main.sdf_tsl_2 = originalSystemLogMessageFormat;
    main.obs_email_recipient = originalRecipient;
    main.obs_format = originalObservationFormat;
    main.eucaws_uploads_method = originalAwsUploadMethod;
    main.obs_101_email = originalFormat101Mode;
    main.custom_email_module = originalEmailModule;
    main.email_send_mode = originalEmailSendMode;
    main.APTR_AWSR_send_method = originalAutomaticSendMethod;
    main.custom_security = originalCustomSecurity;
    main.custom_email_server = originalCustomServer;
    main.custom_password = originalCustomPassword;
    main.custom_port = originalCustomPort;
    main.your_custom_address = originalCustomAddress;
    main.obs_email_subject = originalEmailSubject;
    main.obs_email_cc = originalEmailCc;
    main.station_ID = originalStationId;
    mydatetime.year = originalYear;
    mydatetime.month = originalMonth;
    mydatetime.day = originalDay;
    mydatetime.hour = originalHour;
    mydatetime.MM_code = originalMonthCode;
    mydatetime.YY_code = originalDayCode;
    mydatetime.GG_code = originalHourCode;
    myposition.Qc_code = originalQcCode;
    myposition.lalala_code = originalLatitudeCode;
    myposition.lolololo_code = originalLongitudeCode;
    main.obs_write = originalObservation;
    main.APR = originalApr;
    main.AWSR = originalAwsr;
    main.format_101_class = originalFormat101Class;
    main.python_email_class = originalPythonEmailClass;
    main.jakarta_email_class = originalJakartaEmailClass;
    restoreStaticTextFields();
  }

  @Test
  public void missingRecipientStopsBeforeObservationRouting() throws Exception {
    main.obs_email_recipient = "";

    Rs232OutputObsByEmailAllAprWorkflow.start();

    awaitLogContains("[APR] Email address recipient not inserted");
    assertEquals(main.FORMAT_FM13, main.obs_format);
    assertNull(main.python_email_class);
    assertNull(main.jakarta_email_class);
  }

  @Test
  public void missingObservationAndLogDirectoryAreValidated() throws Exception {
    main.station_ID = "";
    Rs232OutputObsByEmailAllAprWorkflow.start();
    awaitLogContains("[APR] Call sign, date/time or position not present");

    setRequiredObservationData();
    main.logs_dir = "";
    Rs232OutputObsByEmailAllAprWorkflow.start();
    waitFor(() -> !main.UNDEFINED.equals(main.obs_write));
    assertNull(main.python_email_class);
    assertNull(main.jakarta_email_class);
  }

  @Test
  public void unsupportedFormatLogsWithoutSelectingAnEmailModule() throws Exception {
    main.obs_format = "unsupported";

    Rs232OutputObsByEmailAllAprWorkflow.start();

    awaitLogContains("[APR] obs format unknown");
    assertNull(main.python_email_class);
    assertNull(main.jakarta_email_class);
  }

  @Test
  public void fm13RoutesToPythonEmailModule() throws Exception {
    RecordingPythonEmail pythonEmail = new RecordingPythonEmail();
    main.custom_email_module = main.PYTHON_EMAIL;
    main.python_email_class = pythonEmail;

    Rs232OutputObsByEmailAllAprWorkflow.start();

    waitFor(() -> pythonEmail.controlCenterCalled);
    awaitAutomaticEmailCompletion();
    assertNull(main.jakarta_email_class);
  }

  @Test
  public void format101AndAwsTurbowinRoutesBuildFormat101DataAndUseJakartaModule()
      throws Exception {
    RecordingJakartaEmail jakartaEmail = new RecordingJakartaEmail();
    main.jakarta_email_class = jakartaEmail;
    main.obs_format = main.FORMAT_AWS;
    main.eucaws_uploads_method = main.UPLOADS_VIA_TURBOWIN;

    Rs232OutputObsByEmailAllAprWorkflow.start();

    waitFor(() -> main.format_101_class != null);
    waitFor(() -> jakartaEmail.sendCalled);
    awaitAutomaticEmailCompletion();
    assertNull(main.python_email_class);
  }

  private void setRequiredObservationData() {
    main.station_ID = "TEST";
    mydatetime.year = "2024";
    mydatetime.month = "01";
    mydatetime.day = "01";
    mydatetime.hour = "12";
    mydatetime.MM_code = "01";
    mydatetime.YY_code = "01";
    mydatetime.GG_code = "12";
    myposition.Qc_code = "1";
    myposition.lalala_code = "234";
    myposition.lolololo_code = "5678";
  }

  private static void awaitAutomaticEmailCompletion() throws Exception {
    // Calling the transport is not completion: done() still logs and resets the observation.
    // Let that callback finish before the next test replaces the shared observation state.
    waitFor(() -> mydatetime.year.isEmpty());
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void awaitLogContains(String expected) throws Exception {
    waitFor(
        () -> {
          try {
            return Files.exists(systemLogFile())
                && Files.readString(systemLogFile(), StandardCharsets.UTF_8).contains(expected);
          } catch (Exception ex) {
            return false;
          }
        });
  }

  private Path systemLogFile() {
    return new File(
            main.logs_dir,
            main.TURBOWIN_SYSTEM_LOGS_DIR
                + File.separator
                + SystemLogWriterWorkflow.logFileName(main.sdf_tsl_1.format(new Date())))
        .toPath();
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
    assertTrue("Timed out waiting for APR email workflow", condition.getAsBoolean());
  }

  private static final class RecordingPythonEmail extends Python_Email {
    private volatile boolean controlCenterCalled;

    @Override
    public boolean python_email_control_center() {
      controlCenterCalled = true;
      return false;
    }
  }

  private static final class RecordingJakartaEmail extends Jakarta_Email {
    private volatile boolean sendCalled;

    @Override
    public int send_jakarta_email_obs(
        String smtpMode,
        String smtpHost,
        String smtpPassword,
        String sendTo,
        String sendFrom,
        String subject,
        String body,
        String sendCc,
        String smtpPort,
        String attachment) {
      sendCalled = true;
      return 1;
    }
  }
}
