package turbowin;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.testing.AssertJSwingTestCaseTemplate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import turbowin.matchers.DialogTitleMatcher;

/** Exercises the production shutdown dialogs without terminating the test JVM. */
public class ConfigurationShutdownUiTest extends AssertJSwingTestCaseTemplate {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private static RepaintManager originalRepaintManager;
  private String originalDataDirectory;
  private String originalLogsDirectory;
  private String originalShipName;
  private String[] originalLines;
  private Path data;
  private Path logs;
  private FrameFixture frame;
  private ApplicationExitRequest request;
  private final CountDownLatch releaseWriter = new CountDownLatch(1);
  private final CountDownLatch completed = new CountDownLatch(1);
  private final AtomicInteger exits = new AtomicInteger();
  private final AtomicInteger cancellations = new AtomicInteger();
  private final AtomicInteger usableButtonClicks = new AtomicInteger();

  @BeforeClass
  public static void requireDisplay() {
    Assume.assumeFalse("Requires a graphical display", GraphicsEnvironment.isHeadless());
    originalRepaintManager = RepaintManager.currentManager(null);
    FailOnThreadViolationRepaintManager.install();
  }

  @AfterClass
  public static void restoreRepaintManager() {
    if (originalRepaintManager != null) {
      RepaintManager.setCurrentManager(originalRepaintManager);
    }
  }

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
    main.ship_name = "Graphical smoke test ship";
    setUpRobot();
    JFrame owner =
        GuiActionRunner.execute(
            () -> {
              JFrame window = new JFrame("TurboWin shutdown smoke test");
              JButton usable = new JButton("Still running");
              usable.addActionListener(event -> usableButtonClicks.incrementAndGet());
              window.add(usable);
              return window;
            });
    frame = new FrameFixture(robot(), owner);
    frame.show(new Dimension(500, 300));
  }

  @After
  public void tearDown() throws Exception {
    try {
      GuiActionRunner.execute(
          () -> {
            if (request != null) {
              request.cancelExit();
            }
          });
      releaseWriter.countDown();
      CountDownLatch drained = new CountDownLatch(1);
      ConfigurationWriteWorkflow.awaitPendingSaves(saved -> drained.countDown());
      assertTrue("Save queue did not drain", drained.await(10, TimeUnit.SECONDS));
    } finally {
      ConfigurationWriteWorkflow.cancelExit();
      try {
        cleanUp();
      } finally {
        main.data_dir = originalDataDirectory;
        main.logs_dir = originalLogsDirectory;
        main.ship_name = originalShipName;
        System.arraycopy(originalLines, 0, main.configuratie_regels, 0, originalLines.length);
      }
    }
  }

  @Test
  public void progressDialogCanCancelExitWhileAWriteIsBlocked() throws Exception {
    CountDownLatch started = new CountDownLatch(1);
    ConfigurationWriteWorkflow.write(
        (directory, lines, maximum) -> {
          started.countDown();
          try {
            if (!releaseWriter.await(20, TimeUnit.SECONDS)) {
              throw new IOException("Timed out waiting to release graphical test save");
            }
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException(ex);
          }
          ConfigurationWriteWorkflow.writeConfigurationFile(directory, lines, maximum);
        });
    assertTrue(started.await(5, TimeUnit.SECONDS));
    startExit();

    DialogFixture progress = dialog("Saving settings");
    progress.requireVisible();
    captureDialog("saving-settings");
    JProgressBar indicator = robot().finder().findByType(progress.target(), JProgressBar.class);
    assertTrue(GuiActionRunner.execute(() -> indicator.isIndeterminate()));
    progress.button(JButtonMatcher.withText("Cancel exit")).click();
    awaitCompletion();

    assertEquals(0, exits.get());
    assertEquals(1, cancellations.get());
    frame.button(JButtonMatcher.withText("Still running")).click();
    assertEquals(1, usableButtonClicks.get());
    releaseWriter.countDown();
    CountDownLatch drained = new CountDownLatch(1);
    ConfigurationWriteWorkflow.awaitPendingSaves(saved -> drained.countDown());
    assertTrue(drained.await(10, TimeUnit.SECONDS));
    assertEquals("Late completion revived a cancelled exit", 0, exits.get());
  }

  @Test
  public void failedSaveDialogCanCancelAndRestoreOwnerInteraction() throws Exception {
    blockDataDirectory();
    startExit();

    DialogFixture failure = dialog("Unable to save settings");
    captureDialog("save-failure");
    failure.button(JButtonMatcher.withText("Retry saving")).requireEnabled();
    failure.button(JButtonMatcher.withText("Exit without saving")).requireEnabled();
    failure.button(JButtonMatcher.withText("Cancel exit")).click();
    awaitCompletion();

    assertEquals(0, exits.get());
    assertEquals(1, cancellations.get());
    frame.button(JButtonMatcher.withText("Still running")).click();
    assertEquals(1, usableButtonClicks.get());
  }

  @Test
  public void retryButtonSavesCurrentSettingsAfterStorageIsRepaired() throws Exception {
    Path blockedData = blockDataDirectory();
    startExit();
    DialogFixture failure = dialog("Unable to save settings");
    Files.delete(blockedData);
    Files.createDirectory(blockedData);
    GuiActionRunner.execute(() -> main.ship_name = "Ship changed before retry");

    failure.button(JButtonMatcher.withText("Retry saving")).click();
    awaitCompletion();

    assertEquals(1, exits.get());
    assertEquals(0, cancellations.get());
    String saved = Files.readString(blockedData.resolve(main.CONFIGURATION_FILE));
    assertTrue(saved.contains(main.SHIP_NAME_TXT + "Ship changed before retry"));
    assertFalse(Files.exists(logs.resolve(main.CONFIGURATION_FILE)));
  }

  @Test
  public void exitWithoutSavingButtonRequiresExplicitClick() throws Exception {
    blockDataDirectory();
    startExit();

    DialogFixture failure = dialog("Unable to save settings");
    assertEquals(0, exits.get());
    failure.button(JButtonMatcher.withText("Exit without saving")).click();
    awaitCompletion();

    assertEquals(1, exits.get());
    assertEquals(0, cancellations.get());
  }

  @Test
  public void closingFailureDialogCancelsExit() throws Exception {
    blockDataDirectory();
    startExit();

    dialog("Unable to save settings").close();
    awaitCompletion();

    assertEquals(0, exits.get());
    assertEquals(1, cancellations.get());
  }

  private Path blockDataDirectory() throws Exception {
    Path blocked = temporaryFolder.newFile("blocked-data").toPath();
    GuiActionRunner.execute(() -> main.data_dir = blocked.toString());
    return blocked;
  }

  private void startExit() {
    request =
        new ApplicationExitRequest(
            (saved, cancel) ->
                ApplicationShutdownWorkflow.saveBeforeExit(frame.target(), saved, cancel),
            saved -> ApplicationShutdownWorkflow.decideAfterSaveFailure(frame.target(), saved),
            () -> {
              exits.incrementAndGet();
              completed.countDown();
            },
            () -> {
              ConfigurationWriteWorkflow.cancelExit();
              cancellations.incrementAndGet();
              completed.countDown();
            });
    SwingUtilities.invokeLater(request::start);
  }

  private DialogFixture dialog(String title) {
    return WindowFinder.findDialog(new DialogTitleMatcher(title)).using(robot());
  }

  private void awaitCompletion() throws Exception {
    assertTrue("Exit dialog did not complete", completed.await(10, TimeUnit.SECONDS));
    robot().waitForIdle();
  }

  private void captureDialog(String name) throws Exception {
    robot().waitForIdle();
    Path directory = Path.of("build", "reports", "gui-smoke");
    Files.createDirectories(directory);
    ImageIO.write(
        new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
        "png",
        directory.resolve(name + ".png").toFile());
  }
}
