package turbowin;

import static org.junit.Assert.assertSame;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.junit.Test;

public class ToolbarIconUpdaterTest {

  @Test
  public void appliesIconsToTheirMatchingButtons() {
    JButton[] buttons = buttons();
    ImageIcon icon = new ImageIcon();

    update("date_time.png", icon, buttons);
    assertSame(icon, buttons[0].getIcon());

    update("next_screen.png", icon, buttons);
    assertSame(icon, buttons[18].getIcon());
  }

  @Test
  public void ignoresUnknownIconPaths() {
    JButton[] buttons = buttons();
    ImageIcon original = new ImageIcon();
    buttons[0].setIcon(original);

    update("unknown.png", new ImageIcon(), buttons);

    assertSame(original, buttons[0].getIcon());
  }

  private static JButton[] buttons() {
    JButton[] buttons = new JButton[19];
    for (int index = 0; index < buttons.length; index++) {
      buttons[index] = new JButton();
    }
    return buttons;
  }

  private static void update(String fileName, ImageIcon icon, JButton[] buttons) {
    ToolbarIconUpdater.update(
        main.ICONS_DIRECTORY + fileName,
        icon,
        buttons[0],
        buttons[1],
        buttons[2],
        buttons[3],
        buttons[4],
        buttons[5],
        buttons[6],
        buttons[7],
        buttons[8],
        buttons[9],
        buttons[10],
        buttons[11],
        buttons[12],
        buttons[13],
        buttons[14],
        buttons[15],
        buttons[16],
        buttons[17],
        buttons[18]);
  }
}
