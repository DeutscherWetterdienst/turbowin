package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JMenuItem;
import org.junit.Test;

public class DashboardAndMapsMenuStateUpdaterTest {

  @Test
  public void disablesAwsAndBarometerMenusWithoutRelevantConnections() {
    JMenuItem[] items = menuItems();

    update(0, false, false, items);

    assertFalse(items[0].isEnabled());
    assertFalse(items[1].isEnabled());
    assertFalse(items[3].isEnabled());
    assertFalse(items[6].isEnabled());
    assertFalse(items[9].isEnabled());
    assertTrue(items[8].isEnabled());
    assertFalse(items[12].isEnabled());
  }

  @Test
  public void appliesAwsConnectionRules() {
    JMenuItem[] items = menuItems();

    update(3, true, true, items);

    assertTrue(items[1].isEnabled());
    assertFalse(items[2].isEnabled());
    assertTrue(items[4].isEnabled());
    assertTrue(items[5].isEnabled());
    assertTrue(items[6].isEnabled());
    assertFalse(items[7].isEnabled());
    assertFalse(items[8].isEnabled());
    assertTrue(items[12].isEnabled());

    items = menuItems();
    update(3, false, true, items);
    assertTrue(items[1].isEnabled());
    assertFalse(items[12].isEnabled());
  }

  @Test
  public void enablesBarometerDashboardForBarometerConnection() {
    JMenuItem[] items = menuItems();

    update(1, false, false, items);

    assertTrue(items[0].isEnabled());
  }

  private static JMenuItem[] menuItems() {
    JMenuItem[] items = new JMenuItem[13];
    for (int index = 0; index < items.length; index++) {
      items[index] = new JMenuItem();
    }
    return items;
  }

  private static void update(int connectionMode, boolean apr, boolean offline, JMenuItem[] items) {
    DashboardAndMapsMenuStateUpdater.update(
        connectionMode,
        apr,
        offline,
        items[0],
        items[1],
        items[2],
        items[3],
        items[4],
        items[5],
        items[6],
        items[7],
        items[8],
        items[9],
        items[10],
        items[11],
        items[12]);
  }
}
