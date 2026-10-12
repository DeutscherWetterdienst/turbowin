package turbowin;

import java.awt.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.RepaintManager;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
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
import org.junit.rules.TemporaryFolder;
import turbowin.matchers.DialogTitleMatcher;

/** Base class for all my UI tests taking care of the basic setup. */
public class AbstractUiTest extends AssertJSwingTestCaseTemplate {

  @Rule public TemporaryFolder startupDirectory = new TemporaryFolder();
  private String originalDataDirectoryProperty;
  private static RepaintManager originalRepaintManager;
  private final Map<Field, Object> originalStartupSettings = new LinkedHashMap<>();

  /** The main entry point for any tests: the wrapped MainWindow. */
  protected FrameFixture frame;

  /**
   * Installs a {@link FailOnThreadViolationRepaintManager} to catch violations of Swing threading
   * rules.
   */
  @BeforeClass
  public static final void setUpOnce() {
    // avoid UI test execution in a headless environment (e.g. when building in CI environment like
    // Jenkins or TravisCI)
    Assume.assumeFalse(
        "Automated UI Test cannot be executed in headless environment",
        GraphicsEnvironment.isHeadless());
    originalRepaintManager = RepaintManager.currentManager(null);
    FailOnThreadViolationRepaintManager.install();
  }

  @AfterClass
  public static void restoreRepaintManager() {
    if (originalRepaintManager != null) {
      RepaintManager.setCurrentManager(originalRepaintManager);
    }
  }

  /**
   * Sets up this test's fixture, starting from creation of a new <code>{@link Robot}</code>.
   *
   * @see #setUpRobot()
   * @see #onSetUp()
   */
  @Before
  public final void setUp() throws Exception {
    originalDataDirectoryProperty = System.getProperty("turbowin.data.dir");
    System.setProperty("turbowin.data.dir", startupDirectory.getRoot().getAbsolutePath());
    // A missing configuration file leaves existing globals unchanged. Start each UI test in
    // manual-observation mode, with no hardware/reporting workers inherited from another test.
    for (String name :
        new String[] {
          "ship_name",
          "obs_format",
          "RS232_connection_mode",
          "RS232_connection_mode_II",
          "RS232_GPS_connection_mode",
          "theme_changed",
          "APR",
          "AWSR",
          "WOW"
        }) {
      Field field = main.class.getField(name);
      originalStartupSettings.put(field, field.get(null));
    }
    main.ship_name = "";
    main.obs_format = main.FORMAT_FM13;
    main.RS232_connection_mode = 0;
    main.RS232_connection_mode_II = 0;
    main.RS232_GPS_connection_mode = 0;
    main.theme_changed = false;
    main.APR = false;
    main.AWSR = false;
    main.WOW = false;
    // call provided AssertJSwingTestCaseTemplate.setUpRobot()
    this.setUpRobot();
    // initialize the graphical user interface
    main main =
        GuiActionRunner.execute(
            new GuiQuery<main>() {
              @Override
              protected main executeInEDT() throws Exception {
                turbowin.main main1 = new main();
                onWindowCreated(main1);
                main1.setVisible(true);
                return main1;
              }
            });
    this.frame = new FrameFixture(this.robot(), main);
    // A modal startup prompt makes the main frame not ready to accept input.
    closeStartDialogs();
    this.frame.show();
    //        this.frame.resizeTo(new Dimension(600, 600));
    onSetUp();
  }

  /** Captures constructor state on the EDT, before asynchronous startup finalization. */
  protected void onWindowCreated(main window) {}

  private void closeStartDialogs() {
    DialogFixture warning =
        WindowFinder.findDialog(new DialogTitleMatcher("TurboWin+ warning")).using(robot());
    warning.button(JButtonMatcher.withText("OK")).click();
    DialogFixture dateTime =
        WindowFinder.findDialog(new DialogTitleMatcher("Date and Time")).using(robot());
    dateTime.button(JButtonMatcher.withText(Pattern.compile("No|Nein"))).click();
    robot().waitForIdle();
  }

  /**
   * Subclasses that need to set up their own test fixtures in this method. Called as <strong>last
   * action</strong> during {@link #setUp()}.
   */
  protected void onSetUp() {
    // default: everything is already set up
  }

  /*****************************************************************************************
   * Here you could insert further helper methods, e.g. frequently used component matchers *
   *****************************************************************************************/

  /**
   * Cleans up any resources used in this test. After calling <code>{@link #onTearDown()}</code>,
   * this method cleans up resources used by this test's <code>{@link Robot}</code>.
   *
   * @see #cleanUp()
   * @see #onTearDown()
   */
  @After
  public final void tearDown() throws Exception {
    try {
      onTearDown();
      // The end-to-end observation test fills global weather models. Do not let those
      // values trigger validation dialogs or change records in later non-UI tests.
      ObservationStateResetter.resetValues();
      this.frame = null;
    } finally {
      try {
        cleanUp();
      } finally {
        try {
          // Disposing Swing windows does not release the application's single-instance lock.
          Field socketField = main.class.getDeclaredField("s");
          socketField.setAccessible(true);
          ServerSocket socket = (ServerSocket) socketField.get(null);
          if (socket != null) {
            socket.close();
            socketField.set(null, null);
          }
        } finally {
          for (Map.Entry<Field, Object> entry : originalStartupSettings.entrySet()) {
            entry.getKey().set(null, entry.getValue());
          }
          if (originalDataDirectoryProperty == null) {
            System.clearProperty("turbowin.data.dir");
          } else {
            System.setProperty("turbowin.data.dir", originalDataDirectoryProperty);
          }
        }
      }
    }
  }

  /**
   * Subclasses that need to clean up resources can do so in this method. Called as <strong>first
   * action</strong> during {@link #tearDown()}.
   */
  protected void onTearDown() {
    // default: nothing more to tear down
  }
}
