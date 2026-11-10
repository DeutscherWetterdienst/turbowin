package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigurationReadWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalDataDirectory;
  private String originalLogsDirectory;
  private String originalSelectedDirectory;
  private String originalShipName;
  private String originalWindSource;
  private int originalConnectionMode;
  private String originalPort;
  private String originalSecondaryPort;
  private String originalGpsPort;
  private String originalEmailModule;
  private String[] originalLines;
  private Path preferred;
  private Path backup;

  @Before
  public void setUp() throws Exception {
    originalDataDirectory = main.data_dir;
    originalLogsDirectory = main.logs_dir;
    originalSelectedDirectory = main.hulp_dir;
    originalShipName = main.ship_name;
    originalWindSource = main.wind_source;
    originalConnectionMode = main.RS232_connection_mode;
    originalPort = main.prefered_COM_port;
    originalSecondaryPort = main.prefered_COM_port_II;
    originalGpsPort = main.prefered_GPS_COM_port;
    originalEmailModule = main.custom_email_module;
    originalLines = main.configuratie_regels.clone();
    main.data_dir = temporaryFolder.newFolder("data").getPath();
    main.logs_dir = temporaryFolder.newFolder("logs").getPath();
    main.ship_name = "Existing ship";
    main.wind_source = "existing wind source";
    main.RS232_connection_mode = 0;
    Arrays.fill(main.configuratie_regels, "");
    main.configuratie_regels[0] = main.SHIP_NAME_TXT + main.ship_name;
    preferred = Path.of(main.logs_dir, main.CONFIGURATION_FILE);
    backup = Path.of(main.data_dir, main.CONFIGURATION_FILE);
  }

  @After
  public void tearDown() throws Exception {
    SwingUtilities.invokeAndWait(() -> {});
    main.data_dir = originalDataDirectory;
    main.logs_dir = originalLogsDirectory;
    main.hulp_dir = originalSelectedDirectory;
    main.ship_name = originalShipName;
    main.wind_source = originalWindSource;
    main.RS232_connection_mode = originalConnectionMode;
    main.prefered_COM_port = originalPort;
    main.prefered_COM_port_II = originalSecondaryPort;
    main.prefered_GPS_COM_port = originalGpsPort;
    main.custom_email_module = originalEmailModule;
    System.arraycopy(originalLines, 0, main.configuratie_regels, 0, originalLines.length);
  }

  @Test
  public void malformedPreferredCopyFallsBackWithoutLeakingItsEarlierSettings() throws Exception {
    Files.writeString(
        preferred,
        main.SHIP_NAME_TXT + "Rejected ship\n" + main.RS232_INSTRUMENT_TYPE_TXT + "invalid\n");
    Files.writeString(backup, main.WIND_SOURCE_TXT + "backup wind source\n");

    List<String> messages = loadAndFinishStartup();

    assertEquals("Existing ship", main.ship_name);
    assertEquals("backup wind source", main.wind_source);
    assertEquals(main.data_dir, main.hulp_dir);
    assertEquals(main.WIND_SOURCE_TXT + "backup wind source", main.configuratie_regels[0]);
    assertTrue(
        messages.toString(),
        messages.stream().anyMatch(m -> m.contains(preferred.toString()) && m.contains("line 2")));
    assertTrue(
        messages.toString(),
        messages.stream().anyMatch(m -> m.contains("loaded") && m.contains(backup.toString())));
  }

  @Test
  public void olderPartialPreferredCopyPreservesSettingsItDoesNotContain() throws Exception {
    Files.writeString(
        preferred, main.SHIP_NAME_TXT + "Older ship\nunknown future option : ignored\n");
    Files.writeString(backup, main.WIND_SOURCE_TXT + "must not load backup\n");

    loadAndFinishStartup();

    assertEquals("Older ship", main.ship_name);
    assertEquals("existing wind source", main.wind_source);
    assertEquals(main.logs_dir, main.hulp_dir);
  }

  @Test
  public void unusableCopiesRetainExistingSettingsAndStillFinishStartup() throws Exception {
    String[] before = main.configuratie_regels.clone();
    Files.writeString(
        preferred, main.SHIP_NAME_TXT + "Rejected ship\n" + main.RS232_GPS_TYPE_TXT + "invalid\n");
    Files.writeString(
        backup,
        main.SHIP_NAME_TXT + "Also rejected\n" + main.RS232_INSTRUMENT_TYPE_TXT + "invalid\n");

    List<String> messages = loadAndFinishStartup();

    assertEquals("Existing ship", main.ship_name);
    assertEquals("existing wind source", main.wind_source);
    assertTrue(Arrays.equals(before, main.configuratie_regels));
    assertTrue(
        messages.toString(),
        messages.stream().anyMatch(m -> m.contains("retaining existing settings")));
  }

  @Test
  public void firstUseWithoutEitherCopyFinishesWithExistingDefaults() throws Exception {
    loadAndFinishStartup();

    assertEquals("Existing ship", main.ship_name);
    assertEquals("existing wind source", main.wind_source);
  }

  private static List<String> loadAndFinishStartup() throws Exception {
    List<String> messages = new ArrayList<>();
    CountDownLatch completed = new CountDownLatch(1);
    AtomicBoolean completedOnEdt = new AtomicBoolean();
    SwingUtilities.invokeAndWait(
        () ->
            ConfigurationReadWorkflow.read(
                () -> {
                  completedOnEdt.set(SwingUtilities.isEventDispatchThread());
                  completed.countDown();
                },
                messages::add));
    assertTrue("Startup completion timed out", completed.await(5, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {});
    assertTrue("Startup must complete on the EDT", completedOnEdt.get());
    return messages;
  }
}
