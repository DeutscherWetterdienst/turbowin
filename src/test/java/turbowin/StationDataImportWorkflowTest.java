package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class StationDataImportWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String[] originalLines;
  private String originalDataDirectory;
  private String originalLogsDirectory;
  private boolean originalOfflineMode;
  private String originalShipName;
  private String originalWindSource;
  private String originalStationId;
  private String originalPort;
  private String originalSecondaryPort;
  private String originalGpsPort;
  private String originalEmailModule;
  private Path dataConfiguration;
  private Path logsConfiguration;

  @Before
  public void setUp() throws Exception {
    originalLines = main.configuratie_regels.clone();
    originalDataDirectory = main.data_dir;
    originalLogsDirectory = main.logs_dir;
    originalOfflineMode = main.offline_mode;
    originalShipName = main.ship_name;
    originalWindSource = main.wind_source;
    originalStationId = main.station_ID;
    originalPort = main.prefered_COM_port;
    originalSecondaryPort = main.prefered_COM_port_II;
    originalGpsPort = main.prefered_GPS_COM_port;
    originalEmailModule = main.custom_email_module;

    main.data_dir = temporaryFolder.newFolder("data").getPath();
    main.logs_dir = temporaryFolder.newFolder("logs").getPath();
    main.offline_mode = true;
    main.ship_name = "Previous ship";
    main.wind_source = "previous wind source";
    main.station_ID = "PREVIOUS";
    Arrays.fill(main.configuratie_regels, "");
    dataConfiguration = Path.of(main.data_dir, main.CONFIGURATION_FILE);
    logsConfiguration = Path.of(main.logs_dir, main.CONFIGURATION_FILE);
    Files.writeString(dataConfiguration, main.SHIP_NAME_TXT + main.ship_name + "\n");
    Files.writeString(logsConfiguration, main.SHIP_NAME_TXT + main.ship_name + "\n");
  }

  @After
  public void tearDown() throws Exception {
    // Completion assertions wait for the import callback and both configuration writes.
    SwingUtilities.invokeAndWait(() -> {});
    main.data_dir = originalDataDirectory;
    main.logs_dir = originalLogsDirectory;
    main.offline_mode = originalOfflineMode;
    main.ship_name = originalShipName;
    main.wind_source = originalWindSource;
    main.station_ID = originalStationId;
    main.prefered_COM_port = originalPort;
    main.prefered_COM_port_II = originalSecondaryPort;
    main.prefered_GPS_COM_port = originalGpsPort;
    main.custom_email_module = originalEmailModule;
    System.arraycopy(originalLines, 0, main.configuratie_regels, 0, originalLines.length);
  }

  @Test
  public void persistsImportedSettingsToBothConfigurationFiles() throws Exception {
    Path source = temporaryFolder.newFile("import.txt").toPath();
    writeImport(source);

    importAndAwaitSavedSettings(source);
  }

  @Test
  public void canImportFromAConfigurationFileThatWillBeOverwrittenBySaving() throws Exception {
    writeImport(dataConfiguration);

    importAndAwaitSavedSettings(dataConfiguration);
  }

  private void writeImport(Path file) throws IOException {
    Files.writeString(
        file,
        main.SHIP_NAME_TXT
            + "Imported ship\n"
            + main.WIND_SOURCE_TXT
            + "estimated; true speed and true direction\n"
            + main.STATION_ID_TXT
            + "IMPORTED\n"
            + main.LOGS_DIR_TXT
            + temporaryFolder.newFolder("old-installation-logs").getPath()
            + "\n");
  }

  private void importAndAwaitSavedSettings(Path source) throws Exception {
    String expectedLogsDirectory = main.logs_dir;
    String expectedLastLine = main.EUCAWS_OBS_ID_TXT + main.eucaws_obs_id;
    CountDownLatch refreshed = new CountDownLatch(1);
    AtomicBoolean refreshedOnEdt = new AtomicBoolean();
    AtomicReference<String> displayedShipName = new AtomicReference<>();

    SwingUtilities.invokeAndWait(
        () ->
            StationDataImportWorkflow.start(
                source.toString(),
                () -> {
                  refreshedOnEdt.set(SwingUtilities.isEventDispatchThread());
                  displayedShipName.set(main.ship_name);
                  refreshed.countDown();
                }));

    assertTrue("Import did not refresh the station form", refreshed.await(5, TimeUnit.SECONDS));
    awaitSavedSettings(dataConfiguration, expectedLastLine);
    awaitSavedSettings(logsConfiguration, expectedLastLine);

    assertTrue("Station form must be refreshed on the EDT", refreshedOnEdt.get());
    assertEquals("Imported ship", displayedShipName.get());
    assertEquals("Imported ship", main.ship_name);
    assertEquals("estimated; true speed and true direction", main.wind_source);
    assertEquals("IMPORTED", main.station_ID);
    assertEquals(
        "Offline import must preserve the current logs directory",
        expectedLogsDirectory,
        main.logs_dir);
    assertEquals(Files.readString(dataConfiguration), Files.readString(logsConfiguration));
  }

  private static void awaitSavedSettings(Path file, String expectedLastLine) throws Exception {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    List<String> lines;
    do {
      lines = Files.readAllLines(file);
      // The final configuration line ensures we do not mistake a partial write for completion.
      if (lines.contains(main.SHIP_NAME_TXT + "Imported ship")
          && lines.contains(main.WIND_SOURCE_TXT + "estimated; true speed and true direction")
          && lines.contains(main.STATION_ID_TXT + "IMPORTED")
          && !lines.isEmpty()
          && expectedLastLine.equals(lines.get(lines.size() - 1))) {
        return;
      }
      Thread.sleep(10);
    } while (System.nanoTime() < deadline);
    throw new AssertionError("Imported settings were not saved to " + file + ": " + lines);
  }
}
