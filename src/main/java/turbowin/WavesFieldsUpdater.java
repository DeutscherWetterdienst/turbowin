package turbowin;

import static turbowin.main.*;

/** Updates wave and swell fields on the main observation screen. */
final class WavesFieldsUpdater {

  private WavesFieldsUpdater() {}

  static void update() {
    // null waarde heeft het als waves input pagina nooit geopend is
    if ((mywaves.wind_waves_period.compareTo("") != 0) && (mywaves.wind_waves_period != null))
      jTextField23.setText(mywaves.wind_waves_period + " sec");
    else jTextField23.setText("");

    if ((mywaves.wind_waves_height.compareTo("") != 0) && (mywaves.wind_waves_height != null))
      jTextField22.setText(mywaves.wind_waves_height + " metres");
    else jTextField22.setText("");

    if (mywaves.swell_1_period.equals("confused")) jTextField26.setText(mywaves.swell_1_period);
    else if (mywaves.swell_1_period.equals("no swell"))
      jTextField26.setText(mywaves.swell_1_period);
    else if ((mywaves.swell_1_period.compareTo("") != 0) && (mywaves.swell_1_period != null))
      jTextField26.setText(mywaves.swell_1_period + " sec");
    else jTextField26.setText("");

    if (mywaves.swell_1_height.equals("confused")) jTextField25.setText(mywaves.swell_1_height);
    else if (mywaves.swell_1_height.equals("no swell"))
      jTextField25.setText(mywaves.swell_1_height);
    else if ((mywaves.swell_1_height.compareTo("") != 0) && (mywaves.swell_1_height != null))
      jTextField25.setText(mywaves.swell_1_height + " metres");
    else jTextField25.setText("");

    if (mywaves.swell_1_dir.equals("confused")) jTextField24.setText(mywaves.swell_1_dir);
    else if (mywaves.swell_1_dir.equals("no swell")) jTextField24.setText(mywaves.swell_1_dir);
    else if ((mywaves.swell_1_dir.compareTo("") != 0) && (mywaves.swell_1_dir != null))
      jTextField24.setText(mywaves.swell_1_dir + " degr");
    else jTextField24.setText("");

    if ((mywaves.swell_2_period.compareTo("") != 0) && (mywaves.swell_2_period != null))
      jTextField29.setText(mywaves.swell_2_period + " sec");
    else jTextField29.setText("");

    if ((mywaves.swell_2_height.compareTo("") != 0) && (mywaves.swell_2_height != null))
      jTextField28.setText(mywaves.swell_2_height + " metres");
    else jTextField28.setText("");

    if ((mywaves.swell_2_dir.compareTo("") != 0) && (mywaves.swell_2_dir != null))
      jTextField27.setText(mywaves.swell_2_dir + " degr");
    else jTextField27.setText("");

    coded_obs_update();
  }
}
