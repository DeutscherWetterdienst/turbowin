package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CaptainObserverLogWorkflowCharacterizationTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalLogsDirectory;

  @Before
  public void saveGlobalState() {
    originalLogsDirectory = main.logs_dir;
    clearCaptainData();
    clearObserverData();
  }

  @After
  public void restoreGlobalState() {
    main.logs_dir = originalLogsDirectory;
    clearCaptainData();
    clearObserverData();
  }

  @Test
  public void captainWriterPreservesPlaceholdersAndTrailingSeparators() throws Exception {
    Path logsDirectory = temporaryFolder.newFolder("captain-logs").toPath();
    main.logs_dir = logsDirectory.toString();
    CaptainModel.captain_data[0][0] = "Ada";
    CaptainModel.captain_data[0][2] = "12";
    CaptainModel.captain_data[0][4] = " ";

    CaptainLogWriteWorkflow.start();

    Path logFile = logsDirectory.resolve(main.CAPTAIN_LOG);
    assertEquals(
        "Ada;-;12;-; ;" + System.lineSeparator(),
        awaitContents(logFile, "Ada;-;12;-; ;" + System.lineSeparator()));
  }

  @Test
  public void observerWriterPreservesPlaceholdersAndSkipsRowsWithoutSurname() throws Exception {
    Path logsDirectory = temporaryFolder.newFolder("observer-logs").toPath();
    main.logs_dir = logsDirectory.toString();
    ObserverModel.observer_data[0][0] = "Bob";
    ObserverModel.observer_data[0][2] = "7";
    ObserverModel.observer_data[1][1] = "Initials without surname";

    ObserverLogWriteWorkflow.start();

    Path logFile = logsDirectory.resolve(main.OBSERVER_LOG);
    assertEquals(
        "Bob;-;7;-;" + System.lineSeparator(),
        awaitContents(logFile, "Bob;-;7;-;" + System.lineSeparator()));
  }

  @Test
  public void captainReaderParsesFieldsIncludingEmptyFieldsAndMissingSeparators() throws Exception {
    Path logsDirectory = temporaryFolder.newFolder("captain-reader-logs").toPath();
    main.logs_dir = logsDirectory.toString();
    Path logFile = logsDirectory.resolve(main.CAPTAIN_LOG);
    Files.writeString(
        logFile,
        "Ada;;12;-; ;" + System.lineSeparator() + "Incomplete;Only two" + System.lineSeparator(),
        StandardCharsets.UTF_8);

    CaptainModel owner = new CaptainModel();
    CaptainLogReadWorkflow.start(owner);

    awaitCondition(() ->
        "Ada".equals(CaptainModel.captain_data[0][0])
            && "".equals(CaptainModel.captain_data[0][1])
            && "12".equals(CaptainModel.captain_data[0][2])
            && "-".equals(CaptainModel.captain_data[0][3])
            && " ".equals(CaptainModel.captain_data[0][4])
            && "Incomplete".equals(CaptainModel.captain_data[1][0])
            && "".equals(CaptainModel.captain_data[1][1]));
  }

  @Test
  public void observerReaderParsesFieldsIncludingEmptyFieldsAndMissingSeparators()
      throws Exception {
    Path logsDirectory = temporaryFolder.newFolder("observer-reader-logs").toPath();
    main.logs_dir = logsDirectory.toString();
    Path logFile = logsDirectory.resolve(main.OBSERVER_LOG);
    Files.writeString(
        logFile,
        "Bob;;7;book;" + System.lineSeparator() + "Incomplete;Only two" + System.lineSeparator(),
        StandardCharsets.UTF_8);

    ObserverModel owner = new ObserverModel();
    ObserverLogReadWorkflow.start(owner);

    awaitCondition(() ->
        "Bob".equals(ObserverModel.observer_data[0][0])
            && "".equals(ObserverModel.observer_data[0][1])
            && "7".equals(ObserverModel.observer_data[0][2])
            && "book".equals(ObserverModel.observer_data[0][3])
            && "Incomplete".equals(ObserverModel.observer_data[1][0])
            && "".equals(ObserverModel.observer_data[1][1]));
  }

  @Test
  public void captainReaderStopsAtConfiguredRowLimit() throws Exception {
    Path logsDirectory = temporaryFolder.newFolder("captain-row-limit-logs").toPath();
    main.logs_dir = logsDirectory.toString();
    StringBuilder contents = new StringBuilder();
    for (int row = 0; row <= CaptainModel.CAPTAIN_ROWS; row++) {
      contents.append("Captain").append(row).append(";-;12;-; ;").append(System.lineSeparator());
    }
    Files.writeString(
        logsDirectory.resolve(main.CAPTAIN_LOG), contents.toString(), StandardCharsets.UTF_8);

    CaptainModel owner = new CaptainModel();
    CaptainLogReadWorkflow.start(owner);

    awaitCondition(() ->
        "Captain0".equals(CaptainModel.captain_data[0][0])
            && ("Captain" + (CaptainModel.CAPTAIN_ROWS - 1))
                .equals(CaptainModel.captain_data[CaptainModel.CAPTAIN_ROWS - 1][0]));
  }

  @Test
  public void observerReaderStopsAtConfiguredRowLimit() throws Exception {
    Path logsDirectory = temporaryFolder.newFolder("observer-row-limit-logs").toPath();
    main.logs_dir = logsDirectory.toString();
    StringBuilder contents = new StringBuilder();
    for (int row = 0; row <= ObserverModel.OBSERVER_ROWS; row++) {
      contents.append("Observer").append(row).append(";-;7;-;").append(System.lineSeparator());
    }
    Files.writeString(
        logsDirectory.resolve(main.OBSERVER_LOG), contents.toString(), StandardCharsets.UTF_8);

    ObserverModel owner = new ObserverModel();
    ObserverLogReadWorkflow.start(owner);

    awaitCondition(() ->
        "Observer0".equals(ObserverModel.observer_data[0][0])
            && ("Observer" + (ObserverModel.OBSERVER_ROWS - 1))
                .equals(ObserverModel.observer_data[ObserverModel.OBSERVER_ROWS - 1][0]));
  }

  @Test
  public void captainReaderLeavesClearedDataWhenFileIsMissing() throws Exception {
    Path logsDirectory = temporaryFolder.newFolder("missing-captain-log").toPath();
    main.logs_dir = logsDirectory.toString();
    CaptainModel.captain_data[0][0] = "stale";

    CaptainModel owner = new CaptainModel();
    CaptainModel.captain_data[0][0] = "stale";
    CaptainLogReadWorkflow.start(owner);

    awaitCondition(() -> captainDataIsEmpty());
  }

  @Test
  public void observerReaderLeavesClearedDataWhenFileIsMissing() throws Exception {
    Path logsDirectory = temporaryFolder.newFolder("missing-observer-log").toPath();
    main.logs_dir = logsDirectory.toString();
    ObserverModel.observer_data[0][0] = "stale";

    ObserverModel owner = new ObserverModel();
    ObserverModel.observer_data[0][0] = "stale";
    ObserverLogReadWorkflow.start(owner);

    awaitCondition(() -> observerDataIsEmpty());
  }

  private void clearCaptainData() {
    for (int row = 0; row < CaptainModel.CAPTAIN_ROWS; row++) {
      for (int column = 0; column < CaptainModel.CAPTAIN_COLUMNS; column++) {
        CaptainModel.captain_data[row][column] = "";
      }
    }
  }

  private void clearObserverData() {
    for (int row = 0; row < ObserverModel.OBSERVER_ROWS; row++) {
      for (int column = 0; column < ObserverModel.OBSERVER_COLUMNS; column++) {
        ObserverModel.observer_data[row][column] = "";
      }
    }
  }

  private String awaitContents(Path file, String expected) throws Exception {
    String lastContents = "<file not created>";
    for (int attempt = 0; attempt < 200; attempt++) {
      if (Files.exists(file)) {
        try {
          lastContents = Files.readString(file, StandardCharsets.UTF_8);
          if (lastContents.equals(expected)) {
            return lastContents;
          }
        } catch (IOException ignored) {
          // The worker may still be writing the file.
        }
      }
      Thread.sleep(10);
    }
    fail("Timed out waiting for " + file + "; contents were: " + lastContents);
    return null;
  }

  private void awaitCondition(java.util.function.BooleanSupplier condition) throws Exception {
    for (int attempt = 0; attempt < 200; attempt++) {
      if (condition.getAsBoolean()) {
        return;
      }
      Thread.sleep(10);
    }
    fail("Timed out waiting for reader data");
  }

  private boolean captainDataIsEmpty() {
    for (int row = 0; row < CaptainModel.CAPTAIN_ROWS; row++) {
      for (int column = 0; column < CaptainModel.CAPTAIN_COLUMNS; column++) {
        if (!"".equals(CaptainModel.captain_data[row][column])) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean observerDataIsEmpty() {
    for (int row = 0; row < ObserverModel.OBSERVER_ROWS; row++) {
      for (int column = 0; column < ObserverModel.OBSERVER_COLUMNS; column++) {
        if (!"".equals(ObserverModel.observer_data[row][column])) {
          return false;
        }
      }
    }
    return true;
  }
}
