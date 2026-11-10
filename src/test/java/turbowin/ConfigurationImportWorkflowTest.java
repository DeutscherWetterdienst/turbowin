package turbowin;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ConfigurationImportWorkflowTest {
  @Parameterized.Parameters(name = "maintenance={0}")
  public static List<Object[]> workflows() {
    return List.of(new Object[] {true}, new Object[] {false});
  }

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private final boolean maintenance;
  private String originalDataDirectory;
  private String originalLogsDirectory;
  private String originalShip;
  private String originalWind;
  private String originalPort;
  private String originalSecondaryPort;
  private String originalGpsPort;
  private String originalEmailModule;
  private boolean originalOfflineMode;
  private String[] originalLines;
  private String[] beforeLines;
  private Path source;
  private Path dataFile;
  private Path logsFile;
  private String logsDirectory;
  private static final String EXISTING_FILE = "existing configuration must survive\n";

  public ConfigurationImportWorkflowTest(boolean maintenance) {
    this.maintenance = maintenance;
  }

  @Before
  public void setUp() throws Exception {
    originalDataDirectory = main.data_dir;
    originalLogsDirectory = main.logs_dir;
    originalShip = main.ship_name;
    originalWind = main.wind_source;
    originalPort = main.prefered_COM_port;
    originalSecondaryPort = main.prefered_COM_port_II;
    originalGpsPort = main.prefered_GPS_COM_port;
    originalEmailModule = main.custom_email_module;
    originalOfflineMode = main.offline_mode;
    originalLines = main.configuratie_regels.clone();
    main.data_dir = temporaryFolder.newFolder("data").getPath();
    logsDirectory = temporaryFolder.newFolder("logs").getPath();
    main.logs_dir = logsDirectory;
    main.ship_name = "Existing ship";
    main.wind_source = "existing wind";
    main.offline_mode = true;
    Arrays.fill(main.configuratie_regels, "");
    main.configuratie_regels[0] = main.SHIP_NAME_TXT + "Existing ship";
    beforeLines = main.configuratie_regels.clone();
    source = temporaryFolder.newFile("import.txt").toPath();
    dataFile = Path.of(main.data_dir, main.CONFIGURATION_FILE);
    logsFile = Path.of(main.logs_dir, main.CONFIGURATION_FILE);
    Files.writeString(dataFile, EXISTING_FILE);
    Files.writeString(logsFile, EXISTING_FILE);
  }

  @After
  public void tearDown() throws Exception {
    SwingUtilities.invokeAndWait(() -> {});
    main.data_dir = originalDataDirectory;
    main.logs_dir = originalLogsDirectory;
    main.ship_name = originalShip;
    main.wind_source = originalWind;
    main.prefered_COM_port = originalPort;
    main.prefered_COM_port_II = originalSecondaryPort;
    main.prefered_GPS_COM_port = originalGpsPort;
    main.custom_email_module = originalEmailModule;
    main.offline_mode = originalOfflineMode;
    System.arraycopy(originalLines, 0, main.configuratie_regels, 0, originalLines.length);
  }

  @Test
  public void interruptedReadAfterIdentityLabelsDoesNotApplyOrSavePartialData() throws Exception {
    Outcome result =
        importFile(
            (file, lines, maximum) -> {
              lines[0] = main.SHIP_NAME_TXT + "Rejected ship";
              lines[1] = main.CALL_SIGN_TXT + "IGNORED";
              throw new IOException("simulated interrupted read");
            });

    assertRejected(result);
    assertTrue(result.message.get().contains("simulated interrupted read"));
  }

  @Test
  public void malformedValueAfterValidSettingsDoesNotApplyOrSave() throws Exception {
    Files.writeString(
        source, identityLines() + main.RS232_INSTRUMENT_TYPE_TXT + "private-invalid-value\n");

    Outcome result = importFile(ConfigurationFileStore::read);

    assertRejected(result);
    assertTrue(result.message.get().contains("line 3"));
    assertFalse(result.message.get().contains("private-invalid-value"));
  }

  @Test
  public void missingSourceDoesNotSaveOrReportSuccess() throws Exception {
    Files.delete(source);

    assertRejected(importFile(ConfigurationFileStore::read));
  }

  @Test
  public void emptyAndUnknownOnlyFilesAreRejected() throws Exception {
    for (String contents : new String[] {"", "future setting : ignored\n"}) {
      Files.writeString(source, contents);
      assertRejected(importFile(ConfigurationFileStore::read));
    }
  }

  @Test
  public void olderPartialFileIsReadOnceAndAppliedBeforeSavingOnEdt() throws Exception {
    Files.writeString(source, identityLines() + "future setting : ignored\n");
    AtomicInteger reads = new AtomicInteger();

    Outcome result =
        importFile(
            (file, lines, maximum) -> {
              reads.incrementAndGet();
              ConfigurationFileStore.read(file, lines, maximum);
              assertEquals("Existing ship", main.ship_name);
              assertArrayEquals(beforeLines, main.configuratie_regels);
            });

    assertTrue(result.success.get());
    assertEquals(1, reads.get());
    assertEquals(1, result.saves.get());
    assertEquals("Imported ship", result.shipAtSave.get());
    assertTrue(result.saveOnEdt.get());
    assertEquals("Imported ship", main.ship_name);
    assertEquals("existing wind", main.wind_source);
    assertEquals(main.SHIP_NAME_TXT + "Imported ship", main.configuratie_regels[0]);
    assertEquals(1, result.refreshes.get());
  }

  @Test
  public void acceptedImportPersistsAppliedSettingsToBothCopies() throws Exception {
    Files.writeString(source, identityLines());

    Outcome result = importFile(ConfigurationFileStore::read, true);

    assertTrue(result.success.get());
    assertEquals(1, result.saves.get());
    String saved = Files.readString(dataFile);
    assertTrue(saved.contains(main.SHIP_NAME_TXT + "Imported ship"));
    assertTrue(saved.contains(main.WIND_SOURCE_TXT + "existing wind"));
    assertEquals(EXISTING_FILE, Files.readString(logsFile));
  }

  @Test
  public void maintenanceMayImportLogsDirectoryButOfflineStationImportPreservesIt()
      throws Exception {
    String importedLogs = temporaryFolder.newFolder("imported-logs").getPath();
    Files.writeString(source, identityLines() + main.LOGS_DIR_TXT + importedLogs + "\n");

    Outcome result = importFile(ConfigurationFileStore::read);

    assertTrue(result.success.get());
    assertEquals(maintenance ? importedLogs : logsDirectory, main.logs_dir);
  }

  @Test
  public void onlyMaintenanceRequiresBothLegacyIdentityLabels() throws Exception {
    Files.writeString(source, main.WIND_SOURCE_TXT + "imported wind\n");

    Outcome result = importFile(ConfigurationFileStore::read);

    if (maintenance) {
      assertRejected(result);
      assertTrue(result.message.get().contains("missing ship-name or call-sign label"));
    } else {
      assertTrue(result.success.get());
      assertEquals("Existing ship", main.ship_name);
      assertEquals("imported wind", main.wind_source);
      assertEquals(1, result.saves.get());
    }
  }

  private static String identityLines() {
    return main.SHIP_NAME_TXT + "Imported ship\n" + main.CALL_SIGN_TXT + "LEGACY\n";
  }

  private void assertRejected(Outcome result) throws Exception {
    assertFalse(result.success.get());
    assertEquals(0, result.saves.get());
    assertEquals(maintenance ? 1 : 0, result.refreshes.get());
    assertEquals("Existing ship", main.ship_name);
    assertEquals("existing wind", main.wind_source);
    assertEquals(logsDirectory, main.logs_dir);
    assertArrayEquals(beforeLines, main.configuratie_regels);
    assertEquals(EXISTING_FILE, Files.readString(dataFile));
    assertEquals(EXISTING_FILE, Files.readString(logsFile));
    assertTrue(result.message.get().contains("settings unchanged"));
  }

  private Outcome importFile(ConfigurationImportReader.Reader reader) throws Exception {
    return importFile(reader, false);
  }

  private Outcome importFile(ConfigurationImportReader.Reader reader, boolean persist)
      throws Exception {
    Outcome result = new Outcome();
    CountDownLatch finished = new CountDownLatch(1);
    AtomicBoolean readOnEdt = new AtomicBoolean();
    AtomicBoolean callbackOnEdt = new AtomicBoolean();
    ConfigurationImportReader.Reader checkedReader =
        (file, lines, maximum) -> {
          readOnEdt.set(SwingUtilities.isEventDispatchThread());
          reader.read(file, lines, maximum);
        };
    Runnable save =
        () -> {
          result.shipAtSave.set(main.ship_name);
          result.saveOnEdt.set(SwingUtilities.isEventDispatchThread());
          result.saves.incrementAndGet();
          if (persist) {
            main.schrijf_configuratie_regels();
          }
        };
    Runnable refresh =
        () -> {
          callbackOnEdt.set(SwingUtilities.isEventDispatchThread());
          result.refreshes.incrementAndGet();
          if (!maintenance) {
            result.success.set(true);
          }
          finished.countDown();
        };
    SwingUtilities.invokeAndWait(
        () -> {
          if (maintenance) {
            MaintenanceDataImportWorkflow.start(
                source.toString(),
                refresh,
                (success, message) -> {
                  result.success.set(success);
                  result.message.set(message);
                },
                save,
                checkedReader);
          } else {
            StationDataImportWorkflow.start(
                source.toString(),
                refresh,
                save,
                message -> {
                  callbackOnEdt.set(SwingUtilities.isEventDispatchThread());
                  result.message.set(message);
                  finished.countDown();
                },
                checkedReader);
          }
        });
    assertTrue("Import completion timed out", finished.await(5, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {});
    assertFalse("Import must read off the EDT", readOnEdt.get());
    assertTrue("Import must complete on the EDT", callbackOnEdt.get());
    if (persist && result.success.get()) {
      long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
      String finalLine = main.EUCAWS_OBS_ID_TXT + main.eucaws_obs_id;
      boolean saved = false;
      do {
        List<String> dataLines = Files.readAllLines(dataFile);
        saved =
            dataLines.contains(main.SHIP_NAME_TXT + "Imported ship")
                && dataLines.contains(finalLine)
                && Files.readString(logsFile).equals(EXISTING_FILE);
        if (saved) {
          break;
        }
        Thread.sleep(10);
      } while (System.nanoTime() < deadline);
      assertTrue("Accepted import was not saved to the data-root configuration", saved);
    }
    return result;
  }

  private static final class Outcome {
    final AtomicBoolean success = new AtomicBoolean();
    final AtomicBoolean saveOnEdt = new AtomicBoolean();
    final AtomicInteger saves = new AtomicInteger();
    final AtomicInteger refreshes = new AtomicInteger();
    final AtomicReference<String> message = new AtomicReference<>("");
    final AtomicReference<String> shipAtSave = new AtomicReference<>();
  }
}
