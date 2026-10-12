package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.SystemTray;
import java.lang.reflect.Field;
import org.junit.Test;

/** Characterizes the observable state established by main startup. */
public class MainStartupCharacterizationTest extends AbstractUiTest {

  private String initialStatusLine;

  @Override
  protected void onWindowCreated(main window) {
    initialStatusLine = main.jTextField4.getText();
  }

  @Test
  public void startupInitializesCoreApplicationState() {
    assertEquals(main.APPLICATION_NAME, frame.target().getTitle());
    assertEquals(main.APPLICATION_NAME + " " + main.application_mode, main.jLabel4.getText());
    assertEquals("", initialStatusLine);
    assertEquals(main.MODE_PRESSURE, main.mode_grafiek);
    assertNull(main.myship);
  }

  @Test
  public void startupInitializesCoordinatorOwnedState() throws Exception {
    assertEquals(startupDirectory.getRoot().getAbsolutePath(), main.data_dir);
    assertNotNull(main.sdf_tsl_1);
    assertEquals("MMM_yyyy", main.sdf_tsl_1.toPattern());
    assertNotNull(main.sdf_tsl_2);
    assertEquals("dd-MMM-yyyy HH:mm:ss", main.sdf_tsl_2.toPattern());
    assertEquals("", initialStatusLine);
    assertTrue(main.offline_mode);
    assertTrue(main.offline_mode_via_cmd);

    assertNotNull(instanceField("RS232_RS422"));
    assertNotNull(instanceField("RS232_mintaka_class"));
    assertNotNull(instanceField("RS232_vaisala_class"));

    assertNotNull(staticField("s"));
    if (SystemTray.isSupported()) {
      assertNotNull(main.tray);
    }
  }

  private Object instanceField(String name) throws ReflectiveOperationException {
    Field field = main.class.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(frame.target());
  }

  private static Object staticField(String name) throws ReflectiveOperationException {
    Field field = main.class.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(null);
  }
}
