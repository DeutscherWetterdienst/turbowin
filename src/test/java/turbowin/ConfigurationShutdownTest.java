package turbowin;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigurationShutdownTest {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private String originalDataDirectory;
  private String originalLogsDirectory;
  private String originalShipName;
  private String[] originalLines;
  private Path data;
  private Path logs;
  private final CountDownLatch releaseFirst = new CountDownLatch(1);
  private final CountDownLatch releaseLater = new CountDownLatch(1);
  private final CountDownLatch finished = new CountDownLatch(1);
  private final AtomicInteger exits = new AtomicInteger();
  private final AtomicInteger cancellations = new AtomicInteger();
  private final AtomicBoolean completedOnEdt = new AtomicBoolean();
  private final AtomicReference<ConfigurationWriteWorkflow.SaveResult> result =
      new AtomicReference<>();

  @Before
  public void setUp() throws Exception {
    originalDataDirectory = main.data_dir;
    originalLogsDirectory = main.logs_dir;
    originalShipName = main.ship_name;
    originalLines = main.configuratie_regels.clone();
    data = temporaryFolder.newFolder("data").toPath();
    logs = temporaryFolder.newFolder("logs").toPath();
    main.data_dir = data.toString();
    main.logs_dir = logs.toString();
    main.ship_name = "Current ship";
    Files.writeString(data.resolve(main.CONFIGURATION_FILE), "previous data\n");
    Files.writeString(logs.resolve(main.CONFIGURATION_FILE), "previous logs\n");
  }

  @After
  public void tearDown() throws Exception {
    releaseFirst.countDown();
    releaseLater.countDown();
    CountDownLatch drained = new CountDownLatch(1);
    ConfigurationWriteWorkflow.awaitPendingSaves(saved -> drained.countDown());
    assertTrue("Save queue did not drain", drained.await(5, TimeUnit.SECONDS));
    ConfigurationWriteWorkflow.cancelExit();
    main.data_dir = originalDataDirectory;
    main.logs_dir = originalLogsDirectory;
    main.ship_name = originalShipName;
    System.arraycopy(originalLines, 0, main.configuratie_regels, 0, originalLines.length);
  }

  @Test
  public void exitWaitsForDelayedSaveAndBothFinalCopiesWithoutBlockingEdt() throws Exception {
    verifyDelayedExit(false, false);
  }

  @Test
  public void newerSaveSubmittedWhileWaitingIsAlsoDrained() throws Exception {
    verifyDelayedExit(true, false);
  }

  @Test
  public void successfulFinalSaveRecoversFromAnOlderQueuedFailure() throws Exception {
    verifyDelayedExit(false, true);
  }

  private void verifyDelayedExit(boolean saveDuringWait, boolean failOlderWrite) throws Exception {
    CountDownLatch firstStarted = new CountDownLatch(1);
    CountDownLatch laterStarted = new CountDownLatch(1);
    AtomicInteger attempts = new AtomicInteger();
    ConfigurationWriteWorkflow.ConfigurationWriter writer =
        (directory, lines, maximum) -> {
          attempts.incrementAndGet();
          if (directory.equals(data.toString()) && lines[0].contains("Older ship")) {
            firstStarted.countDown();
            try {
              if (!releaseFirst.await(5, TimeUnit.SECONDS)) {
                throw new IOException("Timed out waiting to release delayed save");
              }
            } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
              throw new IOException(ex);
            }
            if (failOlderWrite) {
              throw new IOException("Simulated older save failure");
            }
          }
          if (directory.equals(logs.toString()) && lines[0].contains("Later imported ship")) {
            laterStarted.countDown();
            try {
              if (!releaseLater.await(5, TimeUnit.SECONDS)) {
                throw new IOException("Timed out waiting to release later save");
              }
            } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
              throw new IOException(ex);
            }
          }
          ConfigurationWriteWorkflow.writeConfigurationFile(directory, lines, maximum);
        };
    main.ship_name = "Older ship";
    ConfigurationWriteWorkflow.write(writer);
    assertTrue("Delayed save did not start", firstStarted.await(5, TimeUnit.SECONDS));
    main.ship_name = "Current ship";
    ApplicationExitRequest request =
        startRequest(
            writer,
            saved -> {
              throw new AssertionError("Unexpected save failure: " + saved.failures());
            });
    AtomicBoolean uiResponsive = new AtomicBoolean();
    SwingUtilities.invokeAndWait(
        () -> {
          uiResponsive.set(true);
          request.start(); // A repeated close request must not queue another final save.
          if (saveDuringWait) {
            main.ship_name = "Later imported ship";
            ConfigurationWriteWorkflow.write(writer);
          }
        });
    assertTrue(uiResponsive.get());
    assertEquals("Exit ran while a write was blocked", 0, exits.get());
    assertEquals(1, attempts.get());
    releaseFirst.countDown();
    if (saveDuringWait) {
      assertTrue("Later save did not start", laterStarted.await(5, TimeUnit.SECONDS));
      SwingUtilities.invokeAndWait(() -> {});
      assertEquals("Exit overtook a save submitted during the wait", 0, exits.get());
      releaseLater.countDown();
    }
    awaitCompletion();

    assertEquals(1, exits.get());
    assertEquals(0, cancellations.get());
    assertTrue(result.get().successful());
    assertEquals(saveDuringWait ? 6 : 4, attempts.get());
    assertCopiesContain(saveDuringWait ? "Later imported ship" : "Current ship");
  }

  @Test
  public void cancellingWhileAWriteIsBlockedDoesNotExitWhenItEventuallyFinishes() throws Exception {
    CountDownLatch started = new CountDownLatch(1);
    ApplicationExitRequest request =
        startRequest(
            (directory, lines, maximum) -> {
              started.countDown();
              try {
                if (!releaseFirst.await(5, TimeUnit.SECONDS)) {
                  throw new IOException("Timed out waiting for cancellation");
                }
              } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IOException(ex);
              }
              ConfigurationWriteWorkflow.writeConfigurationFile(directory, lines, maximum);
            },
            saved -> {
              throw new AssertionError("Cancelled request must not ask for a decision");
            });
    assertTrue(started.await(5, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(request::cancelExit);
    awaitCompletion();
    assertEquals(1, cancellations.get());
    assertEquals(0, exits.get());

    releaseFirst.countDown();
    CountDownLatch drained = new CountDownLatch(1);
    ConfigurationWriteWorkflow.awaitPendingSaves(saved -> drained.countDown());
    assertTrue(drained.await(5, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {});
    assertEquals("Late save completion must not revive a cancelled exit", 0, exits.get());
    assertCopiesContain("Current ship");
  }

  @Test
  public void saveFailureCanCancelExitAndKeepTheApplicationUsable() throws Exception {
    AtomicInteger attempts = new AtomicInteger();
    startRequest(
        (directory, lines, maximum) -> {
          attempts.incrementAndGet();
          if (directory.equals(logs.toString())) {
            throw new IOException("Simulated logs-copy failure");
          }
          ConfigurationWriteWorkflow.writeConfigurationFile(directory, lines, maximum);
        },
        saved -> ApplicationExitRequest.Decision.CANCEL);
    awaitCompletion();

    assertEquals(2, attempts.get());
    assertEquals(0, exits.get());
    assertEquals(1, cancellations.get());
    assertEquals(1, result.get().failures().size());
    assertTrue(
        result.get().failures().get(0).contains(logs.resolve(main.CONFIGURATION_FILE).toString()));
    assertEquals("previous logs\n", Files.readString(logs.resolve(main.CONFIGURATION_FILE)));

    main.ship_name = "Saved after cancellation";
    ConfigurationWriteWorkflow.write();
    CountDownLatch savedAgain = new CountDownLatch(1);
    ConfigurationWriteWorkflow.awaitPendingSaves(saved -> savedAgain.countDown());
    assertTrue(savedAgain.await(5, TimeUnit.SECONDS));
    assertCopiesContain("Saved after cancellation");
  }

  @Test
  public void retrySavesBothCopiesAgainBeforeExiting() throws Exception {
    AtomicInteger attempts = new AtomicInteger();
    AtomicInteger decisions = new AtomicInteger();
    AtomicBoolean failOnce = new AtomicBoolean(true);
    startRequest(
        (directory, lines, maximum) -> {
          attempts.incrementAndGet();
          if (directory.equals(logs.toString()) && failOnce.getAndSet(false)) {
            throw new IOException("Simulated first-attempt failure");
          }
          ConfigurationWriteWorkflow.writeConfigurationFile(directory, lines, maximum);
        },
        saved -> {
          decisions.incrementAndGet();
          return ApplicationExitRequest.Decision.RETRY;
        });
    awaitCompletion();

    assertEquals(4, attempts.get());
    assertEquals(1, decisions.get());
    assertEquals(1, exits.get());
    assertEquals(0, cancellations.get());
    assertCopiesContain("Current ship");
  }

  @Test
  public void exitDespiteFailureRequiresExplicitDecision() throws Exception {
    AtomicInteger decisions = new AtomicInteger();
    startRequest(
        (directory, lines, maximum) -> {
          throw new IOException("Simulated unavailable storage");
        },
        saved -> {
          decisions.incrementAndGet();
          assertEquals(0, exits.get());
          assertEquals(2, saved.failures().size());
          return ApplicationExitRequest.Decision.EXIT_WITHOUT_SAVING;
        });
    awaitCompletion();

    assertEquals(1, decisions.get());
    assertEquals(1, exits.get());
    assertFalse(result.get().successful());
  }

  @Test
  public void snapshotFailureCanCancelExitWithoutLosingExistingFiles() throws Exception {
    main.ship_name = null;
    startRequest(
        ConfigurationWriteWorkflow::writeConfigurationFile,
        saved -> ApplicationExitRequest.Decision.CANCEL);
    awaitCompletion();

    assertEquals(0, exits.get());
    assertEquals(1, cancellations.get());
    assertTrue(result.get().failures().get(0).contains("Unable to prepare settings"));
    assertEquals("previous data\n", Files.readString(data.resolve(main.CONFIGURATION_FILE)));
    assertEquals("previous logs\n", Files.readString(logs.resolve(main.CONFIGURATION_FILE)));
  }

  @Test
  public void missingDestinationsAreReportedRatherThanClaimingSettingsWereSaved() throws Exception {
    main.data_dir = "";
    main.logs_dir = "";
    startRequest(
        ConfigurationWriteWorkflow::writeConfigurationFile,
        saved -> ApplicationExitRequest.Decision.CANCEL);
    awaitCompletion();

    assertEquals(0, exits.get());
    assertEquals(1, cancellations.get());
    assertTrue(result.get().failures().get(0).contains("No configuration directory"));
  }

  private ApplicationExitRequest startRequest(
      ConfigurationWriteWorkflow.ConfigurationWriter writer,
      Function<ConfigurationWriteWorkflow.SaveResult, ApplicationExitRequest.Decision> decide)
      throws Exception {
    ApplicationExitRequest request =
        new ApplicationExitRequest(
            (completed, cancel) ->
                ConfigurationWriteWorkflow.saveBeforeExit(
                    saved -> {
                      result.set(saved);
                      completed.accept(saved);
                    },
                    writer),
            decide,
            () -> {
              completedOnEdt.set(SwingUtilities.isEventDispatchThread());
              exits.incrementAndGet();
              finished.countDown();
            },
            () -> {
              ConfigurationWriteWorkflow.cancelExit();
              completedOnEdt.set(SwingUtilities.isEventDispatchThread());
              cancellations.incrementAndGet();
              finished.countDown();
            });
    SwingUtilities.invokeAndWait(request::start);
    return request;
  }

  private void awaitCompletion() throws Exception {
    assertTrue("Exit request did not finish", finished.await(5, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {});
    assertTrue("Exit decisions and cleanup must run on the EDT", completedOnEdt.get());
  }

  private void assertCopiesContain(String shipName) throws Exception {
    String saved = Files.readString(data.resolve(main.CONFIGURATION_FILE));
    assertTrue(saved.contains(main.SHIP_NAME_TXT + shipName));
    assertEquals(saved, Files.readString(logs.resolve(main.CONFIGURATION_FILE)));
  }
}
