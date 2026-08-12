package turbowin;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/** Applies a loaded toolbar icon to its corresponding toolbar button. */
final class ToolbarIconUpdater {

  private ToolbarIconUpdater() {}

  static void update(
      String imagePath,
      ImageIcon icon,
      JButton dateTime,
      JButton position,
      JButton wind,
      JButton waves,
      JButton barometer,
      JButton barograph,
      JButton temperatures,
      JButton presentWeather,
      JButton pastWeather,
      JButton visibility,
      JButton cloudsLow,
      JButton cloudsMiddle,
      JButton cloudsHigh,
      JButton cloudHeight,
      JButton icing,
      JButton ice,
      JButton observers,
      JButton captains,
      JButton nextScreen) {
    if (imagePath.equals(main.ICONS_DIRECTORY + "date_time.png")) {
      dateTime.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "position.png")) {
      position.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "wind.png")) {
      wind.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "waves.png")) {
      waves.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "barometer.png")) {
      barometer.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "barograph.png")) {
      barograph.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "temperatures.png")) {
      temperatures.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "present_weather.png")) {
      presentWeather.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "past_weather.png")) {
      pastWeather.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "visibility.png")) {
      visibility.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "cl.png")) {
      cloudsLow.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "cm.png")) {
      cloudsMiddle.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "ch.png")) {
      cloudsHigh.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "height.png")) {
      cloudHeight.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "icing.png")) {
      icing.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "ice.png")) {
      ice.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "observers.png")) {
      observers.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "captains.png")) {
      captains.setIcon(icon);
    } else if (imagePath.equals(main.ICONS_DIRECTORY + "next_screen.png")) {
      nextScreen.setIcon(icon);
    }
  }
}
