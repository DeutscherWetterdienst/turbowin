package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JMenuItem;
import org.junit.Test;

public class GraphMenuStateUpdaterTest {

  @Test
  public void disablesAllGraphsWithoutAnInstrument() {
    JMenuItem[] items = menuItems();

    GraphMenuStateUpdater.update(0, 0, items[0], items[1], items[2], items[3], items[4], items[5]);

    for (JMenuItem item : items) {
      assertFalse(item.isEnabled());
    }
  }

  @Test
  public void enablesPressureOnlyForBarometerMode() {
    JMenuItem[] items = menuItems();

    GraphMenuStateUpdater.update(1, 0, items[0], items[1], items[2], items[3], items[4], items[5]);

    assertTrue(items[0].isEnabled());
    assertFalse(items[1].isEnabled());
    assertFalse(items[2].isEnabled());
    assertFalse(items[3].isEnabled());
    assertFalse(items[4].isEnabled());
    assertFalse(items[5].isEnabled());
  }

  @Test
  public void enablesPressureAndAirTemperatureForMintakaStarXMode() {
    JMenuItem[] items = menuItems();

    GraphMenuStateUpdater.update(7, 0, items[0], items[1], items[2], items[3], items[4], items[5]);

    assertTrue(items[0].isEnabled());
    assertTrue(items[1].isEnabled());
    assertFalse(items[2].isEnabled());
    assertFalse(items[3].isEnabled());
    assertFalse(items[4].isEnabled());
    assertFalse(items[5].isEnabled());
  }

  @Test
  public void enablesAllGraphsForAwsModeAndSecondaryTemperature() {
    JMenuItem[] items = menuItems();

    GraphMenuStateUpdater.update(3, 0, items[0], items[1], items[2], items[3], items[4], items[5]);

    for (JMenuItem item : items) {
      assertTrue(item.isEnabled());
    }

    items = menuItems();
    GraphMenuStateUpdater.update(0, 1, items[0], items[1], items[2], items[3], items[4], items[5]);

    assertFalse(items[0].isEnabled());
    assertTrue(items[1].isEnabled());
    assertFalse(items[2].isEnabled());
  }

  private static JMenuItem[] menuItems() {
    return new JMenuItem[] {
      new JMenuItem(),
      new JMenuItem(),
      new JMenuItem(),
      new JMenuItem(),
      new JMenuItem(),
      new JMenuItem()
    };
  }
}
