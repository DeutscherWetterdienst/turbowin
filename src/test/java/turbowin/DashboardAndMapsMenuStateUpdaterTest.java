package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JMenuItem;
import org.junit.Test;

public class DashboardAndMapsMenuStateUpdaterTest {

  @Test
  public void disablesAwsAndBarometerMenusWithoutRelevantConnections() {
    JMenuItem[] items = menuItems();

    update(0, false, false, 1920, 1080, items);

    assertFalse(items[0].isEnabled());
    assertFalse(items[1].isEnabled());
    assertFalse(items[2].isEnabled());
    assertFalse(items[4].isEnabled());
    assertFalse(items[7].isEnabled());
    assertFalse(items[10].isEnabled());
    assertTrue(items[9].isEnabled());
    assertFalse(items[13].isEnabled());
  }

  @Test
  public void appliesAwsAndScreenResolutionRules() {
    JMenuItem[] items = menuItems();

    update(3, true, true, 1920, 1080, items);

    assertTrue(items[1].isEnabled());
    assertTrue(items[2].isEnabled());
    assertFalse(items[3].isEnabled());
    assertTrue(items[5].isEnabled());
    assertTrue(items[6].isEnabled());
    assertTrue(items[7].isEnabled());
    assertFalse(items[8].isEnabled());
    assertFalse(items[9].isEnabled());
    assertTrue(items[13].isEnabled());

    items = menuItems();
    update(3, false, true, 1024, 768, items);
    assertFalse(items[1].isEnabled());
    assertFalse(items[13].isEnabled());
  }

  @Test
  public void enablesBarometerDashboardForBarometerConnection() {
    JMenuItem[] items = menuItems();

    update(1, false, false, 1920, 1080, items);

    assertTrue(items[0].isEnabled());
  }

  private static JMenuItem[] menuItems() {
    JMenuItem[] items = new JMenuItem[14];
    for (int index = 0; index < items.length; index++) {
      items[index] = new JMenuItem();
    }
    return items;
  }

  private static void update(
      int connectionMode,
      boolean apr,
      boolean offline,
      double width,
      double height,
      JMenuItem[] items) {
    DashboardAndMapsMenuStateUpdater.update(
        connectionMode,
        apr,
        offline,
        width,
        height,
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
        items[12],
        items[13]);
  }
}
